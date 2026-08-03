/* Carousel.ts

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:04:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * A carousel (slideshow).
 * @defaultValue {@link getZclass}: "z-carousel".
 */
@zk.WrapClass('zul.wgt.Carousel')
export class Carousel extends zul.Widget {
	/** @internal */ _activeIndex = 0;
	/** @internal */ _autoplay = false;
	/** @internal */ _interval = 5000;
	/** @internal */ _showArrows = true;
	/** @internal */ _showIndicators = true;
	/** @internal */ _loop = true;
	/** @internal */ _pause = true;
	/** @internal */ _keyboard = true;
	/** @internal */ _orient = 'horizontal';
	/** @internal */ _effect = 'slide';
	/** @internal */ _timerId?: number;
	/** @internal */ _hovered = false;
	/** @internal */ _headClone?: HTMLElement;
	/** @internal */ _tailClone?: HTMLElement;
	/** @internal */ _wrapping?: 'forward' | 'backward';
	/** @internal */ _wrapTimerId?: number;
	/** @internal */ _onTransitionEndBound?: (evt: TransitionEvent) => void;
	/** @internal */ _dragging = false;
	/** @internal */ _dragStartX = 0;
	/** @internal */ _dragStartY = 0;
	/** @internal */ _dragStartTime = 0;
	/** @internal */ _dragBasePx = 0;
	/** @internal */ _dragPausedAutoplay = false;
	/** @internal */ _userPaused = false;
	/** @internal */ _onPointerDownBound?: (evt: PointerEvent) => void;
	/** @internal */ _onPointerMoveBound?: (evt: PointerEvent) => void;
	/** @internal */ _onPointerUpBound?: (evt: PointerEvent) => void;
	/** @internal */ _onPointerCancelBound?: (evt: PointerEvent) => void;
	/** @internal */ _onVisibilityChangeBound?: () => void;

	/**
	 * Returns the zero-based index of the currently active slide.
	 * @defaultValue `0`.
	 */
	getActiveIndex(): number { return this._activeIndex; }
	/**
	 * Sets the index of the currently active slide. An out-of-range value is clamped into `[0, slideCount-1]` (a warning is logged) rather than rejected.
	 * @param activeIndex - the zero-based index of the slide to activate.
	 */
	setActiveIndex(activeIndex: number, opts?: Record<string, boolean>): this {
		// Mirror Carousel.java#setActiveIndex's bounds check. The server setter
		// throws WrongValueException for an out-of-range index; clamp + warn on
		// the client instead so an out-of-range value pushed by a client MVVM
		// binding can't strand the track on a blank frame (a transform past the
		// last slide, with no slide marked active). The upper clamp only applies
		// once children exist — during initial mount the index may arrive before
		// the slides do, and the negative clamp still guards that window.
		var count = this.nChildren;
		if (activeIndex < 0) {
			zk.error('Carousel: activeIndex cannot be negative — '
					+ activeIndex + ' clamped to 0');
			activeIndex = 0;
		} else if (count > 0 && activeIndex >= count) {
			zk.error('Carousel: activeIndex ' + activeIndex
					+ ' out of range — clamped to ' + (count - 1));
			activeIndex = count - 1;
		}
		const o = this._activeIndex;
		this._activeIndex = activeIndex;
		if (o !== activeIndex || opts?.force) this._applyActive();
		return this;
	}

	/**
	 * Returns whether the carousel advances through its slides automatically.
	 * @defaultValue `false`.
	 */
	isAutoplay(): boolean { return this._autoplay; }
	/**
	 * Sets whether the carousel advances through its slides automatically, using the interval (see {@link setInterval}).
	 * @param autoplay - true to start advancing slides automatically, false to leave navigation manual.
	 */
	setAutoplay(autoplay: boolean, opts?: Record<string, boolean>): this {
		const o = this._autoplay;
		this._autoplay = autoplay;
		if (o !== autoplay || opts?.force) {
			// A server-driven autoplay change is authoritative and supersedes a
			// transient keyboard Space-pause: clear _userPaused before (re)starting,
			// otherwise _startTimer early-returns on the stale flag and an
			// autoplay false->true never resumes.
			this._userPaused = false;
			this._stopTimer();
			if (autoplay && this.desktop) this._startTimer();
		}
		return this;
	}

	/**
	 * Returns the autoplay interval in milliseconds.
	 * @defaultValue `5000`.
	 */
	getInterval(): number { return this._interval; }
	/**
	 * Sets the autoplay interval in milliseconds. Values below 500 are clamped to 500 (a warning is logged), since a tighter window cannot reliably finish the animation or announce the slide change.
	 * @param interval - the autoplay interval in milliseconds.
	 */
	setInterval(interval: number, opts?: Record<string, boolean>): this {
		// Mirror the server-side floor (Carousel.java#setInterval): values
		// below 500ms would starve the UI thread with a tight autoplay loop
		// AND outrun screen-reader announcements. Clamp + warn rather than
		// throw — MVVM bindings reject the whole load() pass on a thrown
		// Error, taking unrelated bindings down with them.
		if (interval < 500) {
			zk.error('Carousel: interval must be >= 500ms — '
					+ interval + ' clamped to 500');
			interval = 500;
		}
		const o = this._interval;
		this._interval = interval;
		if (o !== interval || opts?.force) {
			if (this._autoplay) {
				this._stopTimer();
				this._startTimer();
			}
		}
		return this;
	}

	/**
	 * Returns whether the previous/next navigation arrows are shown.
	 * @defaultValue `true`.
	 */
	isShowArrows(): boolean { return this._showArrows; }
	/**
	 * Sets whether the previous/next navigation arrows are shown.
	 * @param showArrows - true to display the navigation arrows, false to hide them.
	 */
	setShowArrows(showArrows: boolean, opts?: Record<string, boolean>): this {
		const o = this._showArrows;
		this._showArrows = showArrows;
		if (o !== showArrows || opts?.force) this.rerender();
		return this;
	}

	/**
	 * Returns whether the slide indicators (the position dots) are shown.
	 * @defaultValue `true`.
	 */
	isShowIndicators(): boolean { return this._showIndicators; }
	/**
	 * Sets whether the slide indicators (the position dots) are shown.
	 * @param showIndicators - true to display the indicators, false to hide them.
	 */
	setShowIndicators(showIndicators: boolean, opts?: Record<string, boolean>): this {
		const o = this._showIndicators;
		this._showIndicators = showIndicators;
		if (o !== showIndicators || opts?.force) this.rerender();
		return this;
	}

	/**
	 * Returns whether the carousel wraps around from the last slide back to the first (and vice versa).
	 * @defaultValue `true`.
	 */
	isLoop(): boolean { return this._loop; }
	/**
	 * Sets whether the carousel wraps around from the last slide back to the first (and vice versa).
	 * @param loop - true to wrap around continuously, false to stop at the ends.
	 */
	setLoop(loop: boolean, opts?: Record<string, boolean>): this {
		const o = this._loop;
		this._loop = loop;
		if ((o !== loop || opts?.force) && this.desktop) {
			// A loop wrap animates via an armed transitionend + safety timeout.
			// Toggling loop while a wrap is in flight would leave _wrapping set,
			// so _applyActive's _applyTrackPosition is skipped and the track is
			// stranded on a now-removed clone slot until the stale timer fires.
			// Cancel it first — mirrors setEffect/setOrient's _cancelWrap().
			this._cancelWrap();
			if (loop) this._installClones();
			else this._removeClones();
			this._applyActive();
		}
		return this;
	}

	/**
	 * Returns whether autoplay pauses while the cursor hovers over the carousel.
	 * @defaultValue `true`.
	 */
	isPause(): boolean { return this._pause; }
	/**
	 * Sets whether autoplay pauses while the cursor hovers over the carousel.
	 * @param pause - true to pause autoplay on hover, false to keep advancing.
	 */
	setPause(pause: boolean, opts?: Record<string, boolean>): this {
		const o = this._pause;
		if (o !== pause || opts?.force) {
			this._pause = pause;
			// _pause is normally read lazily by the hover handlers, but a live
			// change while the pointer already rests on the carousel must re-sync
			// the timer now: _onHoverLeave's resume guard requires _pause, so a
			// hover-pause (taken when _pause was true) would never lift after a
			// server setPause(false) until the next mouseleave/enter cycle.
			// _startTimer centralizes the pause/hover/userPaused/drag guards, so
			// a stop+restart settles to the correct state in both directions —
			// resume when disabling pause-on-hover, stay stopped when enabling it
			// (mirrors setAutoplay/setInterval). Scoped to the hovered case
			// because _pause cannot affect the timer when the pointer is away.
			if (this._autoplay && this.desktop && this._hovered) {
				this._stopTimer();
				this._startTimer();
			}
		}
		return this;
	}

	/**
	 * Returns whether the carousel reacts to arrow keys when focused.
	 * @defaultValue `true`.
	 */
	isKeyboard(): boolean { return this._keyboard; }
	/**
	 * Sets whether the carousel reacts to arrow keys when focused.
	 * @param keyboard - true to enable arrow-key navigation, false to disable it.
	 */
	setKeyboard(keyboard: boolean, opts?: Record<string, boolean>): this {
		const o = this._keyboard;
		if (o !== keyboard || opts?.force) this._keyboard = keyboard;
		// No DOM rerender — _keyboard is read at runtime by keydown handler.
		return this;
	}

	/**
	 * Returns the orientation along which the slides advance.
	 * @defaultValue `"horizontal"`.
	 */
	getOrient(): string { return this._orient; }
	/**
	 * Sets the orientation along which the slides advance.
	 * @param orient - either "horizontal" or "vertical".
	 */
	setOrient(orient: string, opts?: Record<string, boolean>): this {
		// Mirror Java's null→default coercion (Carousel.setOrient): a null push
		// via client-MVVM would otherwise drop the z-carousel-horizontal/-vertical
		// axis class instead of restoring the default.
		if (orient == null) orient = 'horizontal';
		const o = this._orient;
		this._orient = orient;
		if (o !== orient || opts?.force) {
			// Cancel any in-flight drag before re-applying track position.
			// _onPointerMove reads _orient to decide axis and the new axis
			// doesn't share _dragBasePx with the old one — re-anchoring the
			// drag mid-flight would still snap back on pointerup. Drop the
			// drag instead: pointerup early-returns on _dragging===false, the
			// browser releases the pointer capture on the next event, and the
			// track's CSS transition is restored here.
			if (this._dragging) {
				this._dragging = false;
				var track = this.$n('track');
				if (track) track.style.transition = '';
				if (this._dragPausedAutoplay) {
					this._dragPausedAutoplay = false;
					if (this._autoplay && !(this._pause && this._hovered))
						this._startTimer();
				}
			}
			// A loop wrap animates on the OLD axis via an armed transitionend +
			// safety timeout; flipping the axis mid-wrap would let _finishWrap
			// snap on stale coordinates. Cancel it (the advanced _activeIndex is
			// kept) so the _applyTrackPosition below re-anchors cleanly on the
			// new axis — mirrors setEffect's _cancelWrap().
			this._cancelWrap();
			this.updateDomClass_();
			// _setTrackTransform writes translateX or translateY based on the
			// CURRENT _orient — without re-applying here, a horizontal carousel
			// switched to vertical at runtime keeps its stale `translateX(-N%)`
			// inline style and is mispositioned on the wrong axis until the
			// next slide change.
			if (this.desktop) this._applyTrackPosition();
		}
		return this;
	}

	/**
	 * Returns the transition effect used when moving between slides.
	 * @defaultValue `"slide"`.
	 */
	getEffect(): string { return this._effect; }
	/**
	 * Sets the transition effect used when moving between slides.
	 * @param effect - one of "slide", "fade" or "none".
	 */
	setEffect(effect: string, opts?: Record<string, boolean>): this {
		// Mirror Java's null→default coercion (Carousel.setEffect): a null push
		// would otherwise emit the dead class z-carousel-effect-null (no LESS rule).
		if (effect == null) effect = 'slide';
		const o = this._effect;
		this._effect = effect;
		if (o !== effect || opts?.force) {
			this.updateDomClass_();
			// Clone install/remove is gated on `_effect === 'slide'`. When the
			// effect toggles in or out of 'slide' at runtime we must re-sync
			// the clone DOM nodes — otherwise switching to 'fade'/'none'
			// leaves zombie clones inflating the visible total, and switching
			// to 'slide' from another effect leaves wrap-mode without the
			// clones it needs to animate to.
			if (this.desktop && this._loop) {
				this._cancelWrap();
				this._removeClones();
				if (effect === 'slide') this._installClones();
				this._applyActive();
			}
			// Pointer-drag listeners are installed in bind_ only when
			// _effect==='slide' at bind time. Mirror that gate at runtime so
			// fade→slide enables swipe and slide→fade tears the listeners
			// down (they're meaningless without a spatial axis).
			if (this.desktop) this._syncDragListeners();
		}
		return this;
	}

	/** @internal */
	_syncDragListeners(): void {
		var track = this.$n('track');
		if (!track) return;
		var want = this._effect === 'slide',
			have = !!this._onPointerDownBound;
		if (want && !have) {
			var self = this;
			this._onPointerDownBound = function (e): void { self._onPointerDown(e); };
			this._onPointerMoveBound = function (e): void { self._onPointerMove(e); };
			this._onPointerUpBound = function (e): void { self._onPointerUp(e); };
			this._onPointerCancelBound = function (e): void { self._onPointerCancel(e); };
			track.addEventListener('pointerdown', this._onPointerDownBound);
			track.addEventListener('pointermove', this._onPointerMoveBound);
			track.addEventListener('pointerup', this._onPointerUpBound);
			track.addEventListener('pointercancel', this._onPointerCancelBound);
		} else if (!want && have) {
			if (this._onPointerDownBound) track.removeEventListener('pointerdown', this._onPointerDownBound);
			if (this._onPointerMoveBound) track.removeEventListener('pointermove', this._onPointerMoveBound);
			if (this._onPointerUpBound) track.removeEventListener('pointerup', this._onPointerUpBound);
			if (this._onPointerCancelBound) track.removeEventListener('pointercancel', this._onPointerCancelBound);
			this._onPointerDownBound = this._onPointerMoveBound = undefined;
			this._onPointerUpBound = this._onPointerCancelBound = undefined;
			this._dragging = false;
		}
	}

	/**
	 * @returns the widget's CSS class.
	 * @defaultValue `z-carousel`.
	 */
	override getZclass(): string {
		return this._zclass == null ? 'z-carousel' : this._zclass;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ sc = super.domClass_(no);
		if (!no || !no.zclass) {
			sc += ' ' + this.$s(this._orient);
			sc += ' ' + this.$s('effect-' + this._effect);
		}
		return sc;
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		// Carousel root needs to be keyboard-focusable for arrow-key /
		// Space-key nav. Emitting tabindex inline (instead of via
		// setAttribute in bind_) means the root is focusable from the
		// first paint frame — no flash window where focusing the root
		// would silently fall through to the body.
		return super.domAttrs_(no) + ' tabindex="0"';
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._installClones();
		this._applyActive();
		if (this._autoplay) this._startTimer();
		var prev = this.$n('prev'), next = this.$n('next');
		if (prev) this.domListen_(prev, 'onClick', '_onPrev');
		if (next) this.domListen_(next, 'onClick', '_onNext');
		var ind = this.$n('indicators');
		if (ind) this.domListen_(ind, 'onClick', '_onIndicator');
		var root = this.$n();
		if (root) {
			this.domListen_(root, 'onMouseEnter', '_onHoverEnter');
			this.domListen_(root, 'onMouseLeave', '_onHoverLeave');
			this.domListen_(root, 'onKeyDown', '_onKeyDown');
			// tabindex='0' is emitted via domAttrs_() so the root is
			// focusable from the first paint frame; no setAttribute here.
		}
		// Pointer-based touch / mouse-drag swipe. Only meaningful for
		// `slide` — fade has no spatial axis and `none` skips transitions.
		// Delegate to the same effect-gated installer setEffect() uses so the
		// install/teardown rules live in one place and can't drift.
		this._syncDragListeners();
		// Background-tab autoplay pause: browsers throttle hidden-tab timers to
		// ≥1 Hz but still advance them, so the user returns to a carousel
		// parked at a random slide. Stop the timer outright when the tab is
		// hidden and resume on return (respecting hover-pause).
		var selfVis = this;
		this._onVisibilityChangeBound = function (): void {
			if (document.hidden) {
				selfVis._stopTimer();
			} else if (selfVis._autoplay && !(selfVis._pause && selfVis._hovered)) {
				selfVis._startTimer();
			}
		};
		document.addEventListener('visibilitychange', this._onVisibilityChangeBound);
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this._stopTimer();
		this._cancelWrap();
		this._removeClones();
		var prev = this.$n('prev'), next = this.$n('next');
		if (prev) this.domUnlisten_(prev, 'onClick', '_onPrev');
		if (next) this.domUnlisten_(next, 'onClick', '_onNext');
		var ind = this.$n('indicators');
		if (ind) this.domUnlisten_(ind, 'onClick', '_onIndicator');
		var root = this.$n();
		if (root) {
			this.domUnlisten_(root, 'onMouseEnter', '_onHoverEnter');
			this.domUnlisten_(root, 'onMouseLeave', '_onHoverLeave');
			this.domUnlisten_(root, 'onKeyDown', '_onKeyDown');
		}
		var track = this.$n('track');
		if (track) {
			if (this._onPointerDownBound) track.removeEventListener('pointerdown', this._onPointerDownBound);
			if (this._onPointerMoveBound) track.removeEventListener('pointermove', this._onPointerMoveBound);
			if (this._onPointerUpBound) track.removeEventListener('pointerup', this._onPointerUpBound);
			if (this._onPointerCancelBound) track.removeEventListener('pointercancel', this._onPointerCancelBound);
		}
		this._onPointerDownBound = this._onPointerMoveBound = undefined;
		this._onPointerUpBound = this._onPointerCancelBound = undefined;
		this._dragging = false;
		// Reset hover state: after a rerender the old DOM (and its mouseleave)
		// is gone, so a stale _hovered=true would suppress autoplay forever on
		// the rebound widget.
		this._hovered = false;
		if (this._onVisibilityChangeBound) {
			document.removeEventListener('visibilitychange', this._onVisibilityChangeBound);
			this._onVisibilityChangeBound = undefined;
		}
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		this._refreshClones();
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		// Removing the active (or any later) child can leave _activeIndex
		// pointing past the new end. Clamp client-side so _applyActive sees
		// a valid index — without this, the active-class fall-off leaves
		// zero slides marked active until the next server round-trip.
		var total = this._count();
		if (total === 0) this._activeIndex = 0;
		else if (this._activeIndex >= total) this._activeIndex = total - 1;
		this._refreshClones();
		if (this.desktop) this._applyActive();
	}

	/**
	 * Re-installs head/tail clones after a runtime child-set change.
	 * Without this, a carousel that starts with one item then grows to two
	 * via `appendChild` after bind never gets clones (`_installClones`
	 * early-exits on `count < 2` during bind), so wrap-mode silently falls
	 * back to the non-clone snap.
	 *
	 * Skipped while unbound — `bind_` will install clones from scratch.
	 *
	 * @internal
	 */
	_refreshClones(): void {
		if (!this.desktop) return;
		this._removeClones();
		this._installClones();
		this._applyTrackPosition();
	}

	/** @internal */
	_onHoverEnter(): void {
		this._hovered = true;
		if (this._pause && this._autoplay) this._stopTimer();
	}
	/** @internal */
	_onHoverLeave(): void {
		this._hovered = false;
		if (this._pause && this._autoplay && !this._timerId) this._startTimer();
	}
	/** @internal */
	_onKeyDown(evt: zk.Event): void {
		if (!this._keyboard) return;
		// Don't hijack arrow/Space keys when focus is inside an interactive
		// control within a slide (input/textarea/select/button/link/editable):
		// the user is operating that control, not navigating the carousel.
		// Mirrors the same guard in _onPointerDown.
		var domEvt = evt.domEvent as KeyboardEvent | undefined,
			t = domEvt?.target as HTMLElement | undefined;
		if (t && t.closest('input, textarea, select, button, a, [contenteditable]'))
			return;
		var key = domEvt?.key;
		if (key === 'ArrowRight' || (this._orient === 'vertical' && key === 'ArrowDown')) {
			this._gotoNext(true);
			evt.stop();
		} else if (key === 'ArrowLeft' || (this._orient === 'vertical' && key === 'ArrowUp')) {
			this._gotoPrev(true);
			evt.stop();
		} else if (key === ' ' && this._autoplay) {
			// Space toggles autoplay while focused on the carousel — gives
			// keyboard users a way to pause an animated/auto-advancing
			// region (WCAG 2.2.2 Pause, Stop, Hide). Track the pause intent
			// as a separate flag (_userPaused) so hover-leave / visibility-
			// change restart paths can honour it without forcing us to mutate
			// _autoplay (which is the server-supplied configuration).
			if (this._userPaused) {
				this._userPaused = false;
				if (!(this._pause && this._hovered)) this._startTimer();
			} else {
				this._userPaused = true;
				this._stopTimer();
			}
			evt.stop();
		}
	}

	/** @internal */
	_onPrev(evt: zk.Event): void {
		evt.stop();
		this._gotoPrev(true);
	}
	/** @internal */
	_onNext(evt: zk.Event): void {
		evt.stop();
		this._gotoNext(true);
	}
	/** @internal */
	_onIndicator(evt: zk.Event): void {
		var t = evt.domTarget as HTMLElement | undefined;
		if (!t) return;
		var idxAttr = t.getAttribute('data-index');
		if (idxAttr != null) {
			var idx = parseInt(idxAttr, 10);
			// A corrupt/missing data-index would parse to NaN; writing NaN
			// into _activeIndex would cascade into `translateX(NaN%)` and
			// strand the carousel on a blank frame.
			if (Number.isNaN(idx)) return;
			evt.stop();
			this._gotoIndex(idx, true);
		}
	}

	/** @internal */
	_gotoPrev(byUser?: boolean): void {
		if (this._wrapping) return;
		var total = this._count();
		if (total === 0) return;
		var i = this._activeIndex - 1;
		if (i < 0) {
			if (!this._loop) return;
			if (this._tailClone) {
				this._wrapBackward(byUser);
				return;
			}
			i = total - 1;
		}
		this._gotoIndex(i, byUser);
	}
	/** @internal */
	_gotoNext(byUser?: boolean): void {
		if (this._wrapping) return;
		var total = this._count();
		if (total === 0) return;
		var i = this._activeIndex + 1;
		if (i >= total) {
			if (!this._loop) return;
			if (this._headClone) {
				this._wrapForward(byUser);
				return;
			}
			i = 0;
		}
		this._gotoIndex(i, byUser);
	}

	/**
	 * @internal
	 * @param byUser - true when the navigation was user-initiated (arrow key,
	 * indicator click, swipe). Threaded to {@link _updateStatus} so a manual
	 * move is announced to screen readers even while autoplay is running,
	 * whereas autoplay's own ticks stay silent to avoid live-region spam.
	 */
	_gotoIndex(i: number, byUser?: boolean): void {
		// Ignore navigation while a loop wrap is animating — mirrors the
		// _gotoNext/_gotoPrev guard. The indicator click is the one nav path
		// that reaches _gotoIndex directly; without this it would retarget the
		// track mid-wrap, so the user sees the track lurch toward the clone
		// and then jump when _finishWrap snaps.
		if (this._wrapping) return;
		// Reject an out-of-range target before it corrupts _activeIndex or
		// fires a wasted onChanging/onSelect to the server. _gotoNext/_gotoPrev
		// always pass an in-range i; this guards the indicator-click path
		// (_onIndicator), where a tampered data-index can parse to a number yet
		// land out of bounds (e.g. data-index="999") — the NaN guard there does
		// not catch that. Carousel.java#service re-validates server-side too.
		var total = this._count();
		if (i < 0 || i >= total) return;
		if (i === this._activeIndex) return;
		var from = this._activeIndex;
		// toServer is gated on byUser: a user-initiated move (arrow/indicator/
		// swipe) is sent so the server tracks the active slide — same as a
		// Tabbox/Listbox selection. Autoplay ticks pass byUser falsy, so
		// fireX falls back to listener-gating (sent only when a listener / MVVM
		// binding exists), avoiding a per-tick AU flood on every autoplaying
		// carousel while still syncing when something is actually bound.
		this.fire('onChanging', {fromIndex: from, toIndex: i}, {toServer: byUser});
		this._activeIndex = i;
		this._applyActive(byUser);
		this.fire('onSelect', {index: i}, {toServer: byUser});
	}

	/** @internal */
	_count(): number {
		// zk.Widget maintains nChildren on every child add/remove; the head/tail
		// wrap clones are raw DOM nodes (not child widgets), so this counts only
		// real slides — same as the old firstChild→nextSibling walk, but O(1).
		return this.nChildren;
	}

	/** @internal */
	_applyActive(byUser?: boolean): void {
		if (!this.desktop) return;
		var n = this.$n();
		if (!n) return;
		// Lazy clamp mirroring Carousel.java#getActiveIndex: setActiveIndex can
		// arrive before the slides do (a binding evaluated before the children
		// load), in which case its own upper-bound check was skipped (nChildren
		// was 0). Correct it here at render time so the track never translates
		// past the last slide / leaves no slide marked active.
		var total = this._count();
		if (total > 0 && this._activeIndex >= total) this._activeIndex = total - 1;
		else if (this._activeIndex < 0) this._activeIndex = 0;
		this._applyActiveClass();
		// WCAG 2.1 SC 4.1.3 Status Messages: announce the new slide via the
		// polite live region so screen readers learn the activeIndex change
		// without a focus move. Autoplay's own ticks stay silent (byUser is
		// falsy) to avoid live-region spam, but a user-initiated move (arrow
		// key / indicator / swipe) is announced even while autoplay runs.
		this._updateStatus(byUser);
		// while a wrap animation is in flight, _wrapForward / _wrapBackward
		// own the track transform — don't fight them by snapping to the real
		// position, or the user sees the ugly backward swipe we're trying to
		// avoid.
		if (!this._wrapping) this._applyTrackPosition();
	}

	/**
	 * @internal
	 * Hook for the live-region "Slide N of M" announcement. CE keeps it as a
	 * no-op; the za11y add-on (EE) augments it to turn the -status node into a
	 * live region and write the localized status text on activeIndex change.
	 */
	_updateStatus(byUser?: boolean): void {
		void byUser;
	}

	/** @internal */
	_applyActiveClass(): void {
		var i = 0,
			idx = this._activeIndex,
			total = this._count();
		for (var w = this.firstChild; w; w = w.nextSibling) {
			var node = w.$n();
			if (node) {
				// Derive the active modifier from the item's OWN zclass, not the
				// carousel's zclass + 'item' — those coincide only at the default
				// 'z-carousel'/'z-carouselitem' naming, so a custom zclass on the
				// carousel or item would otherwise add a class no CSS rule matches.
				var activeClsHtml = w.getZclass() + '-active';
				if (i === idx)
					node.classList.add(/*safe*/activeClsHtml);
				else
					node.classList.remove(/*safe*/activeClsHtml);
			}
			i++;
		}
		var ind = this.$n('indicators');
		if (ind) {
			var dots = ind.children;
			for (var j = 0; j < dots.length; j++) {
				var isActive = j === idx;
				if (isActive) dots[j].classList.add(this.$s('indicator-active'));
				else dots[j].classList.remove(this.$s('indicator-active'));
			}
		}
		// At loop=false boundaries, disable the corresponding nav button so
		// keyboard / AT users get the standard "this button does nothing
		// right now" affordance instead of arrows that silently no-op. When
		// loop is true, both arrows are always usable (they wrap), so make
		// sure any disabled state from a prior loop=false config is cleared.
		var prev = this.$n('prev'),
			next = this.$n('next');
		if (!this._loop) {
			if (prev) {
				if (idx <= 0) prev.setAttribute('disabled', 'disabled');
				else prev.removeAttribute('disabled');
			}
			if (next) {
				if (idx >= total - 1) next.setAttribute('disabled', 'disabled');
				else next.removeAttribute('disabled');
			}
		} else {
			if (prev) prev.removeAttribute('disabled');
			if (next) next.removeAttribute('disabled');
		}
	}

	/** @internal */
	_applyTrackPosition(): void {
		// 'fade' positions slides via opacity (carousel.less forces
		// `transform: none` on the track), so the track transform is irrelevant
		// to it — skip. 'slide' AND 'none' both rely on the track transform to
		// bring the active slide into the overflow-clipped viewport ('none' is
		// the same jump with `transition: none`); excluding 'none' here froze it
		// permanently on slide 0.
		if (this._effect === 'fade') return;
		var track = this.$n('track');
		if (!track) return;
		// Clamp the index actually used for the transform. setActiveIndex can be
		// pushed by a client binding before the slides mount (count 0, so its own
		// upper clamp is skipped) and _refreshClones re-applies position without
		// routing through _applyActive's clamp — either path could otherwise drive
		// the track to e.g. translateX(-500%), past every slide (or past nothing),
		// stranding a blank frame. Nothing to position when the track is empty.
		var total = this._count();
		if (total < 1) return;
		var idx = this._activeIndex;
		if (idx >= total) idx = total - 1;
		else if (idx < 0) idx = 0;
		// With clones installed, the real items occupy DOM slots 1..total
		// inside the track (slot 0 is the tail clone), so we offset by 1.
		var offset = this._tailClone ? 1 : 0,
			pos = -(idx + offset) * 100;
		if (this._orient === 'vertical')
			track.style.transform = 'translateY(' + pos + '%)';
		else
			track.style.transform = 'translateX(' + pos + '%)';
	}

	/** @internal */
	_startTimer(): void {
		if (this._timerId) return;
		// Never start auto-advance mid-drag. A mouse-drag whose pointer leaves
		// the root fires onMouseLeave → _onHoverLeave, which would otherwise
		// resume autoplay and fight the in-progress drag (both write the track
		// transform every frame). Drag-end clears _dragging before its own
		// resume call, so this only blocks the racing hover / visibility paths.
		if (this._dragging) return;
		// User explicitly paused via Space — honour that across all resume
		// paths (hover-leave, visibility-return, drag-end). The flag is
		// cleared only by a second Space keypress.
		if (this._userPaused) return;
		// Hover-pause (pauseOnHover) must survive a setter-driven restart too:
		// setInterval()/setAutoplay() call _startTimer() unconditionally, so a
		// server smartUpdate while the pointer rests on the carousel would
		// otherwise resume auto-advance under the user's cursor. Centralizing
		// the guard here also backstops every other resume path.
		if (this._pause && this._hovered) return;
		// WCAG 2.1 SC 2.3.3 Animation from Interactions: if the OS-level
		// prefers-reduced-motion setting is on, autoplay-induced motion is
		// exactly the vestibular trigger the user opted out of. The LESS
		// reduce-motion block kills the transition; this completes the story
		// by stopping the automatic advance itself. The user can still
		// navigate via arrows / indicators / keyboard.
		if (typeof window.matchMedia === 'function'
				&& window.matchMedia('(prefers-reduced-motion: reduce)').matches)
			return;
		var self = this;
		this._timerId = window.setInterval(function () {
			self._gotoNext();
		}, this._interval);
	}
	/** @internal */
	_stopTimer(): void {
		if (this._timerId) {
			window.clearInterval(this._timerId);
			this._timerId = undefined;
		}
	}

	/** @internal */
	_installClones(): void {
		if (this._headClone || this._tailClone) return;
		if (this._effect !== 'slide') return;
		if (!this._loop) return;
		if (this._count() < 2) return;
		var track = this.$n('track');
		if (!track) return;
		var firstW = this.firstChild, lastW = this.lastChild;
		if (!firstW || !lastW) return;
		var first = firstW.$n(), last = lastW.$n();
		if (!first || !last) return;
		this._headClone = this._buildClone(first);
		this._tailClone = this._buildClone(last);
		// Strip each clone's item base/active class using the SOURCE item's own
		// zclass, so the clone stays invisible to `.z-carouselitem` selectors
		// even under a custom carouselitem zclass.
		this._headClone.classList.remove(firstW.getZclass(), firstW.getZclass() + '-active');
		this._tailClone.classList.remove(lastW.getZclass(), lastW.getZclass() + '-active');
		track.appendChild(this._headClone);
		track.insertBefore(this._tailClone, track.firstChild);
	}

	/** @internal */
	_removeClones(): void {
		if (this._headClone && this._headClone.parentNode)
			this._headClone.parentNode.removeChild(this._headClone);
		if (this._tailClone && this._tailClone.parentNode)
			this._tailClone.parentNode.removeChild(this._tailClone);
		this._headClone = this._tailClone = undefined;
	}

	/** @internal */
	_buildClone(src: HTMLElement): HTMLElement {
		var c = src.cloneNode(true) as HTMLElement;
		// Mark the clone so it is invisible to selectors like `.z-carouselitem`
		// (tests count real items only). The item's own base/active classes are
		// stripped by the caller (_installClones), which has the source widget's
		// zclass — needed for correct stripping under a custom carouselitem zclass.
		c.classList.add(this.getZclass() + '-clone');
		// avoid duplicate ids in the document
		c.removeAttribute('id');
		var ided = c.querySelectorAll('[id]');
		for (var i = 0; i < ided.length; i++) ided[i].removeAttribute('id');
		// Make the clone non-interactive — without `inert`, a keyboard user
		// can Tab into invisible focusables inside the clone (button, input,
		// link), and screen readers expose duplicate buttons. `inert` removes
		// the entire subtree from sequential focus navigation AND from the
		// accessibility tree, in one attribute. Supported in all modern
		// browsers; older browsers fall back to "no harm" (the clone is
		// visually offscreen via the track translate anyway).
		c.setAttribute('inert', '');
		return c;
	}

	/** @internal */
	_wrapForward(byUser?: boolean): void {
		var track = this.$n('track');
		if (!track || !this._headClone) return;
		this._wrapping = 'forward';
		// advance semantic state immediately so onSelect / indicator / active
		// class reflect the destination — the visual catch-up happens after
		// transitionend.
		var from = this._activeIndex;
		// See _gotoIndex: toServer gated on byUser (user nav synced, autoplay
		// ticks listener-gated).
		this.fire('onChanging', {fromIndex: from, toIndex: 0}, {toServer: byUser});
		this._activeIndex = 0;
		this._applyActiveClass();
		// WCAG 2.1 SC 4.1.3: live-region announcement on every active-index
		// change, including loop wraps — the destination slot has changed
		// even though the visual animation is a wrap.
		this._updateStatus(byUser);
		this.fire('onSelect', {index: 0}, {toServer: byUser});
		// animate the track one extra slot forward, onto the head clone
		var total = this._count(),
			pos = -(total + 1) * 100;
		this._setTrackTransform(pos);
		this._armWrapEnd(track);
	}

	/** @internal */
	_wrapBackward(byUser?: boolean): void {
		var track = this.$n('track');
		if (!track || !this._tailClone) return;
		this._wrapping = 'backward';
		var total = this._count(),
			from = this._activeIndex;
		// See _gotoIndex: toServer gated on byUser.
		this.fire('onChanging', {fromIndex: from, toIndex: total - 1}, {toServer: byUser});
		this._activeIndex = total - 1;
		this._applyActiveClass();
		this._updateStatus(byUser);
		this.fire('onSelect', {index: total - 1}, {toServer: byUser});
		// animate to the tail clone, which sits at DOM slot 0
		this._setTrackTransform(0);
		this._armWrapEnd(track);
	}

	/** @internal */
	_armWrapEnd(track: HTMLElement): void {
		var self = this,
			// Filter on target+propertyName so transitionend events bubbling
			// up from child elements (e.g. a Carouselitem with a CSS hover
			// scale, an image opacity fade, an indicator dot transition)
			// don't prematurely terminate the wrap. The track's own
			// transform is the only signal we care about.
			handler = function (e?: TransitionEvent): void {
				if (e && (e.target !== track || e.propertyName !== 'transform'))
					return;
				self._finishWrap();
			};
		this._onTransitionEndBound = handler;
		track.addEventListener('transitionend', handler);
		// safety net: if transitionend never fires (tab hidden, transition
		// cancelled, transform identical to current), still finish the wrap.
		// The safety-net path passes no event, so handler runs unconditionally.
		this._wrapTimerId = window.setTimeout(handler, 600);
	}

	/** @internal */
	_finishWrap(): void {
		if (!this._wrapping) return;
		var track = this.$n('track');
		if (track && this._onTransitionEndBound)
			track.removeEventListener('transitionend', this._onTransitionEndBound);
		this._onTransitionEndBound = undefined;
		if (this._wrapTimerId) {
			window.clearTimeout(this._wrapTimerId);
			this._wrapTimerId = undefined;
		}
		this._wrapping = undefined;
		if (!track) return;
		// snap to the real position without animation
		var prev = track.style.transition;
		track.style.transition = 'none';
		this._applyTrackPosition();
		// force a reflow so the next transform change re-engages the transition
		void track.offsetHeight;
		track.style.transition = prev;
	}

	/** @internal */
	_cancelWrap(): void {
		if (this._wrapTimerId) {
			window.clearTimeout(this._wrapTimerId);
			this._wrapTimerId = undefined;
		}
		var track = this.$n('track');
		if (track && this._onTransitionEndBound)
			track.removeEventListener('transitionend', this._onTransitionEndBound);
		this._onTransitionEndBound = undefined;
		this._wrapping = undefined;
	}

	/** @internal */
	_setTrackTransform(pct: number): void {
		var track = this.$n('track');
		if (!track) return;
		if (this._orient === 'vertical')
			track.style.transform = 'translateY(' + pct + '%)';
		else
			track.style.transform = 'translateX(' + pct + '%)';
	}

	/** @internal */
	_onPointerDown(e: PointerEvent): void {
		// mouse: left button only; touch / pen: button is 0 by default
		if (e.pointerType === 'mouse' && e.button !== 0) return;
		if (this._dragging) return;
		// Don't claim the pointer when the press lands on a focusable form
		// control — a slide may contain a button, link or input, and the
		// setPointerCapture below would otherwise steal subsequent pointer
		// events away from the control (click never reaches the button,
		// typing doesn't focus the input). The closest()-walk also catches
		// the case where the control wraps something that received the
		// pointerdown (e.g. an icon span inside a button).
		var t = e.target as HTMLElement | undefined;
		if (t && t.closest('input, textarea, select, button, a, [contenteditable]'))
			return;
		// finish any in-flight wrap synchronously so the drag base is real
		if (this._wrapping) this._finishWrap();
		var track = this.$n('track'), root = this.$n();
		if (!track || !root) return;
		this._dragging = true;
		this._dragStartX = e.clientX;
		this._dragStartY = e.clientY;
		this._dragStartTime = Date.now();
		// Record the autoplay INTENT (server-supplied _autoplay), not the
		// currently-running timer. Otherwise a hover-paused carousel that
		// gets dragged would leave _dragPausedAutoplay=false and never
		// resume after drag-end + mouseleave: _onHoverLeave's restart
		// branch gates on `!this._timerId` which stays true forever.
		if (this._autoplay) {
			this._stopTimer();
			this._dragPausedAutoplay = true;
		}
		// freeze current visual position as a px offset so the move handler
		// can apply deltas in px without fighting the % math.
		var trackBox = track.getBoundingClientRect(),
			rootBox = root.getBoundingClientRect();
		this._dragBasePx = this._orient === 'vertical'
			? trackBox.top - rootBox.top
			: trackBox.left - rootBox.left;
		track.style.transition = 'none';
		try { track.setPointerCapture(e.pointerId); } catch (ex) { /* unsupported */ }
	}

	/** @internal */
	_onPointerMove(e: PointerEvent): void {
		if (!this._dragging) return;
		var dx = e.clientX - this._dragStartX,
			dy = e.clientY - this._dragStartY,
			delta = this._orient === 'vertical' ? dy : dx,
			newPx = this._dragBasePx + delta,
			track = this.$n('track');
		if (!track) return;
		if (this._orient === 'vertical')
			track.style.transform = 'translateY(' + newPx + 'px)';
		else
			track.style.transform = 'translateX(' + newPx + 'px)';
	}

	/** @internal */
	_onPointerUp(e: PointerEvent): void {
		if (!this._dragging) return;
		this._dragging = false;
		var dx = e.clientX - this._dragStartX,
			dy = e.clientY - this._dragStartY,
			delta = this._orient === 'vertical' ? dy : dx,
			elapsed = Math.max(1, Date.now() - this._dragStartTime),
			velocity = Math.abs(delta) / elapsed,
			track = this.$n('track'),
			root = this.$n();
		// re-enable the CSS transition so the snap / next animates
		if (track) track.style.transition = '';
		try { if (track) track.releasePointerCapture(e.pointerId); } catch (ex) { /* unsupported */ }
		if (this._dragPausedAutoplay) {
			this._dragPausedAutoplay = false;
			if (this._autoplay && !(this._pause && this._hovered))
				this._startTimer();
		}
		if (!root) {
			this._applyTrackPosition();
			return;
		}
		var viewport = this._orient === 'vertical' ? root.clientHeight : root.clientWidth,
			threshold = Math.max(40, viewport * 0.2),
			before = this._activeIndex;
		// negative delta → swipe forward (next); positive delta → swipe backward (prev)
		if (delta < -threshold || (delta < -10 && velocity > 0.5)) {
			this._gotoNext(true);
		} else if (delta > threshold || (delta > 10 && velocity > 0.5)) {
			this._gotoPrev(true);
		}
		// Snap the dragged track home unless something else is now moving it: a
		// below-threshold swipe (no branch taken), or a threshold-crossing swipe
		// whose navigation was a no-op (single slide; loop=false at a boundary;
		// loop=true with <2 slides so no wrap clone exists). When a nav changed
		// the index, _gotoIndex→_applyActive already snapped; when a loop wrap
		// started, _wrapForward/_wrapBackward own the transform (_wrapping set),
		// so skip both of those cases.
		if (this._activeIndex === before && !this._wrapping)
			this._applyTrackPosition();
	}

	/** @internal */
	_onPointerCancel(e: PointerEvent): void {
		if (!this._dragging) return;
		this._dragging = false;
		var track = this.$n('track');
		if (track) {
			track.style.transition = '';
			try { track.releasePointerCapture(e.pointerId); } catch (ex) { /* unsupported */ }
		}
		this._applyTrackPosition();
		if (this._dragPausedAutoplay) {
			this._dragPausedAutoplay = false;
			// Mirror _onPointerUp's hover-pause guard so a tap-then-leave
			// cancel doesn't restart autoplay over a still-hovered carousel.
			if (this._autoplay && !(this._pause && this._hovered))
				this._startTimer();
		}
	}
}
