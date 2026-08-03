/* Confirmpopup.ts

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:04:18 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * An inline confirmation popup.
 * @defaultValue {@link getZclass}: "z-confirmpopup".
 */
@zk.WrapClass('zul.wgt.Confirmpopup')
export class Confirmpopup extends zul.wgt.Popup {
	/** @internal */ _header?: string;
	/** @internal */ _message?: string;
	/** @internal */ _iconSclass = 'z-icon-exclamation-triangle';
	/** @internal */ _severity = 'warning';
	/** @internal */ _placement = 'top';
	/** @internal */ _defaultFocus = 'ok';
	/** @internal */ _focusTimerId?: number;
	/** @internal */ _restoreFocusTimerId?: number;
	/** @internal */ _arrowTimerId?: number;
	/** @internal */ _prevFocus?: HTMLElement;
	/** @internal */ _okFired = false;
	/** @internal */ _cancelFired = false;
	/** @internal */ _refNode?: HTMLElement;

	/**
	 * Returns the optional header (title row) shown above the message body.
	 * @defaultValue `null`.
	 */
	getHeader(): string | undefined { return this._header; }
	/**
	 * Sets the optional header (title row) shown above the message body.
	 * @param header - the header text; null or an empty string clears it (the
	 * popup renders with no title row).
	 */
	setHeader(header: string, opts?: Record<string, boolean>): this {
		const o = this._header;
		this._header = header;
		if (o !== header || opts?.force) this._rerenderOrDeferUntilClose();
		return this;
	}

	/**
	 * Returns the placement of this popup relative to the trigger element. One
	 * of "top", "bottom", "left" or "right". The arrow points from this popup
	 * toward the trigger.
	 * @defaultValue `top`.
	 */
	getPlacement(): string { return this._placement; }
	/**
	 * Sets the placement of this popup relative to the trigger element.
	 * @param placement - one of "top", "bottom", "left" or "right".
	 */
	setPlacement(placement: string, opts?: Record<string, boolean>): this {
		const o = this._placement;
		this._placement = placement;
		if (o !== placement || opts?.force) {
			this.updateDomClass_();
			// Swapping the arrow-side class is not enough while the popup is
			// open: the box must be re-anchored to the new side and the arrow
			// re-pointed at the trigger, otherwise the arrow points away from
			// the trigger until the next close/reopen.
			if (this.isOpen() && this.desktop)
				this._reanchor();
		}
		return this;
	}

	/**
	 * Returns the confirmation message shown in the popup body.
	 * @defaultValue `null`.
	 */
	getMessage(): string | undefined { return this._message; }
	/**
	 * Sets the confirmation message shown in the popup body.
	 * @param message - the message text; null or an empty string clears it.
	 */
	setMessage(message: string, opts?: Record<string, boolean>): this {
		const o = this._message;
		this._message = message;
		if (o !== message || opts?.force) this._rerenderOrDeferUntilClose();
		return this;
	}

	/**
	 * Returns the icon CSS class shown beside the message. An empty string means
	 * the icon is explicitly suppressed (no icon node is rendered).
	 * @defaultValue `z-icon-exclamation-triangle`.
	 */
	getIconSclass(): string { return this._iconSclass; }
	/**
	 * Sets the icon CSS class. null restores the default
	 * (`z-icon-exclamation-triangle`); an empty string explicitly suppresses the
	 * icon (the popup renders with no icon node); any other value is used as the
	 * icon's CSS class.
	 * @param iconSclass - the icon CSS class; null restores the default, an empty
	 * string suppresses the icon.
	 */
	setIconSclass(iconSclass: string, opts?: Record<string, boolean>): this {
		// Mirror Confirmpopup.java#setIconSclass: null restores the default
		// icon, while the empty string is the explicit "no icon" signal. A
		// client-bind/MVVM load() pushing null would otherwise fall through the
		// mold's `if (this._iconSclass)` as falsy and hide the icon — the
		// opposite of the server's null=default semantics.
		if (iconSclass == null) iconSclass = 'z-icon-exclamation-triangle';
		const o = this._iconSclass;
		this._iconSclass = iconSclass;
		if (o !== iconSclass || opts?.force) this._rerenderOrDeferUntilClose();
		return this;
	}

	/** @internal */ _pendingRerender = false;

	/**
	 * Re-render the popup, deferring while it's visible. A live rerender
	 * destroys the DOM mid-display: focus is lost (breaking the Tab trap),
	 * `_prevFocus` / `_arrowTimerId` / `_focusTimerId` get untracked because
	 * unbind_ runs between, and the popup visually flickers. When open, we
	 * stash the request and let close() drain it on the next reopen path.
	 * @internal
	 */
	_rerenderOrDeferUntilClose(): void {
		if (this.isOpen()) {
			this._pendingRerender = true;
			return;
		}
		this.rerender();
	}

	/**
	 * Returns the severity, which drives the icon/color styling of the popup. One
	 * of "info", "success", "warning", "danger" or "secondary".
	 * @defaultValue `warning`.
	 */
	getSeverity(): string { return this._severity; }
	/**
	 * Sets the severity, which drives the icon/color styling of the popup. null
	 * restores the default (`warning`).
	 * @param severity - one of "info", "success", "warning", "danger" or
	 * "secondary".
	 */
	setSeverity(severity: string, opts?: Record<string, boolean>): this {
		// null/undefined → default so a stateless binding can't emit a dead
		// `z-confirmpopup-null` class via $s() in updateDomClass_.
		if (severity == null) severity = 'warning';
		const o = this._severity;
		this._severity = severity;
		if (o !== severity || opts?.force) this.updateDomClass_();
		return this;
	}

	/**
	 * Returns which button gets keyboard focus when the popup opens — either "ok"
	 * or "cancel". For destructive operations (severity="danger"), prefer
	 * "cancel" so an accidental Enter keypress does not commit the action.
	 * @defaultValue `ok`.
	 */
	getDefaultFocus(): string { return this._defaultFocus; }
	/**
	 * Sets which button gets keyboard focus when the popup opens. Accepts "ok" or
	 * "cancel"; any other value is coerced to "ok". For destructive operations
	 * (severity="danger"), prefer "cancel" so an accidental Enter keypress does
	 * not commit the action.
	 * @param defaultFocus - either "ok" or "cancel".
	 */
	setDefaultFocus(defaultFocus: string, opts?: Record<string, boolean>): this {
		// Server-side Confirmpopup#setDefaultFocus throws WrongValueException
		// for anything other than "ok" / "cancel". Mirror that contract on
		// the client by surfacing the violation; we still coerce to "ok" so
		// the Tab trap has a valid $n() sub-node target — but the developer
		// sees in the console why their value did not take effect, rather
		// than silently disagreeing with the server state.
		if (defaultFocus !== 'ok' && defaultFocus !== 'cancel') {
			zk.error('Confirmpopup: defaultFocus must be "ok" or "cancel" — '
					+ defaultFocus + ' coerced to "ok"');
			defaultFocus = 'ok';
		}
		const o = this._defaultFocus;
		this._defaultFocus = defaultFocus;
		// Honor a change made while the popup is already open: move keyboard
		// focus to the new default button. Otherwise a server-driven switch to
		// 'cancel' on a danger popup (the documented safety knob) would leave
		// focus on OK, and Enter would still commit the destructive action
		// until the popup was closed and reopened.
		//
		// Only move focus when it currently rests INSIDE the popup (e.g. on the
		// other button). This honors the modal Tab-trap's invariant while not
		// yanking focus — or scroll-jumping — when an unrelated MVVM load()
		// batch re-pushes defaultFocus while the user is interacting elsewhere.
		if ((o !== defaultFocus || opts?.force) && this.isOpen() && this.desktop) {
			var root = this.$n(), btn = this.$n(defaultFocus);
			if (btn && root && root.contains(document.activeElement)
					&& document.activeElement !== btn)
				btn.focus();
		}
		return this;
	}

	/**
	 * @returns the widget's CSS class.
	 * @defaultValue `z-confirmpopup`.
	 */
	override getZclass(): string {
		return this._zclass == null ? 'z-confirmpopup' : this._zclass;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ sc = super.domClass_(no);
		if (!no || !no.zclass) {
			sc += ' ' + this.$s(this._severity);
			sc += ' ' + this.$s('placement-' + this._placement);
			// Popup.open() applies the open-state class imperatively (addClass),
			// not through domClass_. setSeverity()/setPlacement() call
			// updateDomClass_() while the popup is open, which rewrites the whole
			// className from domClass_() — re-emit the open class here so a live
			// severity/placement change doesn't strip the open-state styling out
			// from under a visible popup.
			if (this.isOpen())
				sc += ' ' + this.$s('open');
		}
		return sc;
	}

	/**
	 * Anchors the popup at the trigger element with an arrow on the
	 * `placement` side. Always overrides the incoming `position` (the
	 * server-side `Popup.open(Component)` defaults to `at_pointer`, which
	 * would detach the popup from the trigger and stop the arrow from
	 * pointing at it).
	 *
	 * Also captures the currently-focused element so close() can restore
	 * focus there — required by WCAG 2.4.3 (focus order) for any
	 * dialog/popup that steals focus.
	 */
	override open(ref?: zul.wgt.Ref, offset?: zk.Offset, position?: string,
			opts?: zul.wgt.PopupOptions): void {
		// A scroll-back reopen from Popup._onSyncScroll re-invokes open() while
		// _keepVisible is still set from the transient keepVisible close (a real
		// dismissal clears it). That is not a fresh user open: the focus-restore
		// target (_prevFocus) and the ok/cancel one-shots were preserved across
		// the scroll-hide and must not be clobbered, and focus must not be yanked
		// back onto the default button. On such a reentry we reposition only.
		var reentry = this._keepVisible === true;
		// Cancel any pending timers from a prior open() that wasn't followed
		// by close() (rapid reopen, programmatic open while still showing).
		// MUST happen before we schedule the new arrow/focus timers below —
		// _clearFocusTimer() also clears _arrowTimerId, so calling it after
		// scheduling would kill the just-scheduled callback.
		this._clearFocusTimer();
		if (!reentry) {
			this._prevFocus = document.activeElement as HTMLElement | undefined;
			this._okFired = false;
			this._cancelFired = false;
		}
		this._refNode = this._resolveRefNode(ref);
		position = this._placementPosition();
		super.open(ref, offset, position, opts);
		// Reposition the arrow so it points at the trigger's center even
		// when the popup itself has been shifted off-center to stay inside
		// the viewport (e.g. trigger near a screen edge). Without this,
		// CSS's static `left: 50%` keeps the arrow at the popup's center.
		// Deferred to next tick: Popup.open's positioning is partly async
		// (zWatch.fire('onVParent'), CSS transitions) — reading
		// getBoundingClientRect inside the synchronous call returns stale
		// coords. A microtask gives the layout one frame to settle.
		var arrowSelf = this;
		this._arrowTimerId = window.setTimeout(function (): void {
			arrowSelf._arrowTimerId = undefined;
			if (arrowSelf.desktop) arrowSelf._repositionArrow();
		}, 0);
		// Defer focus to next tick so Popup's positioning + show animation
		// settles first; focusing inside a still-hidden element silently
		// no-ops in some browsers. Track the timer id so close()/unbind_()
		// can cancel it — otherwise a rapid open→close races against this
		// callback and focuses a stale (detached) node. Skipped on a scroll-
		// back reentry so the popup doesn't steal focus from wherever the user
		// is during a plain scroll.
		if (!reentry) {
			var self = this;
			this._focusTimerId = window.setTimeout(function (): void {
				self._focusTimerId = undefined;
				if (!self.desktop) return;
				var btn = self.$n(self._defaultFocus);
				if (btn) btn.focus();
			}, 0);
		}
	}

	/**
	 * Closes the popup. Treats any path other than the OK button as a
	 * passive dismissal and fires `onCancel` exactly once — covers
	 * Cancel-button click, Escape key, click-outside (Popup base class
	 * auto-closes on outside click), and programmatic `close()` calls.
	 *
	 * `_cancelFired` is a sticky one-shot: it's reset to `false` only by
	 * {@link open}, never inside close itself. That matters because
	 * `Popup` base internals can re-enter `close()` during outside-click
	 * detection + transitionend cleanup — a non-sticky flag would let the
	 * second invocation fire onCancel again, including over the OK path
	 * (since the first call's reset would clear `_okFired` too).
	 */
	override close(opts?: zul.wgt.PopupOptions): void {
		// A keepVisible close is the base Popup transiently hiding the popup
		// because it scrolled out of view (Popup._onSyncScroll) — it reopens on
		// scroll-back. That is NOT a user dismissal, so it must not fire
		// onCancel to the server or tear down the focus-restore state; just
		// delegate the hide. Real dismissals (Cancel/Escape/click-outside/
		// programmatic close) carry no keepVisible flag and fall through.
		if (opts && opts.keepVisible) {
			super.close(opts);
			return;
		}
		if (!this._okFired && !this._cancelFired) {
			this._cancelFired = true;
			this.fire('onCancel', {}, {toServer: true});
		}
		this._clearFocusTimer();
		// Drain a rerender deferred by _rerenderOrDeferUntilClose while
		// the popup was open. Doing it now (after super.close() detaches
		// the DOM) lets the next open() build off the new HTML.
		var hadPending = this._pendingRerender;
		this._pendingRerender = false;
		// Snapshot the focus target before super.close() runs — super
		// detaches the popup and clears _refNode-adjacent state.
		const focusTarget = this._pickFocusTargetForRestore();
		this._prevFocus = undefined;
		this._refNode = undefined;
		super.close(opts);
		if (hadPending) this.rerender();
		// Defer the .focus() to the next macrotask so super.close()'s
		// synchronous unwind (virtual-parent detach, onhide fire,
		// focusout events that bubble) finishes first — applying focus
		// inline can race those events and leave the focus on whatever
		// the unwind ended up activating. Track the timer id so a rapid
		// close → reopen cycle can cancel the stale restore — otherwise
		// the late .focus(trigger) call yanks focus out of the newly
		// opened modal.
		if (focusTarget) {
			var self = this;
			this._restoreFocusTimerId = window.setTimeout(function (): void {
				self._restoreFocusTimerId = undefined;
				if (document.body.contains(focusTarget)
						&& typeof focusTarget.focus === 'function') {
					try { focusTarget.focus(); } catch (e) { /* non-focusable */ }
				}
			}, 0);
		}
	}

	/** @internal */
	_pickFocusTargetForRestore(): HTMLElement | undefined {
		// Prefer the element that was focused right before open(), but fall
		// back to the trigger ref when activeElement was something the user
		// didn't actually interact with (browsers don't auto-focus buttons
		// on click in some platforms, so document.activeElement at open()
		// time can be the body or document.documentElement).
		const prev = this._prevFocus;
		if (prev && prev !== document.body && prev !== document.documentElement)
			return prev;
		return this._refNode;
	}

	/** @internal */
	_resolveRefNode(ref: unknown): HTMLElement | undefined {
		if (!ref) return undefined;
		if (ref instanceof HTMLElement) return ref;
		// zk.$(...) is the canonical "resolve any reference form" helper used
		// by Popup.open itself — accepts Widget instance, DOM element, uuid
		// string, jQuery wrapper. Going through it keeps our resolution in
		// sync with whatever Popup's own positioning code sees.
		const w = zk.$(ref as Parameters<typeof zk.$>[0]);
		if (w && typeof w.$n === 'function') {
			return w.$n() ?? undefined;
		}
		return undefined;
	}

	/**
	 * Moves the arrow so its tip stays aligned with the trigger's center
	 * along the placement axis. The popup itself may have been shifted by
	 * the framework's viewport-clamp logic so it remains on-screen — the
	 * arrow follows the trigger, not the popup.
	 *
	 * Clamps within the popup's edges so the arrow can't slide off the
	 * box when the trigger is far enough away to push it past the corner.
	 *
	 * @internal
	 */
	_repositionArrow(): void {
		const root = this.$n();
		if (!root) return;
		const arrow = root.querySelector<HTMLElement>('.' + this.$s('arrow'));
		if (!arrow) return;
		const refNode = this._refNode;
		if (!refNode) {
			// Opened at a raw coordinate (no trigger element to anchor to) — a
			// statically-centered arrow would point at empty space, so hide it.
			arrow.style.display = 'none';
			return;
		}
		// Reset a prior coordinate-open hide so a later ref-based reopen of the
		// same widget shows the arrow again.
		arrow.style.display = '';
		// Clear both inline offsets first: a runtime placement flip across axes
		// (e.g. top -> left) sets the new axis below but would otherwise leave
		// the stale offset on the old axis, pushing the arrow off the trigger.
		arrow.style.left = '';
		arrow.style.top = '';
		const triggerRect = refNode.getBoundingClientRect(),
			popupRect = root.getBoundingClientRect();
		if (this._placement === 'top' || this._placement === 'bottom') {
			const triggerCenter = triggerRect.left + triggerRect.width / 2;
			let local = triggerCenter - popupRect.left;
			const arrowHalfWidth = arrow.offsetWidth / 2;
			local = Math.max(arrowHalfWidth, Math.min(popupRect.width - arrowHalfWidth, local));
			arrow.style.left = local + 'px';
		} else {
			const triggerCenter = triggerRect.top + triggerRect.height / 2;
			let local = triggerCenter - popupRect.top;
			const arrowHalfHeight = arrow.offsetHeight / 2;
			local = Math.max(arrowHalfHeight, Math.min(popupRect.height - arrowHalfHeight, local));
			arrow.style.top = local + 'px';
		}
	}

	/**
	 * The ZK popup position string for the current `placement`. The box is
	 * anchored on the side opposite the arrow so the arrow points back at the
	 * trigger (placement "top" → box above → arrow on the bottom edge).
	 * @internal
	 */
	_placementPosition(): string {
		switch (this._placement) {
		case 'bottom': return 'after_center';
		case 'left': return 'start_center';
		case 'right': return 'end_center';
		case 'top':
		default: return 'before_center';
		}
	}

	/**
	 * Re-anchors an already-open popup to the current `placement` and re-points
	 * its arrow. Reuses the open-time ref/offset/opts (only the position string
	 * changes) and goes through {@link Popup#position} — the same non-disruptive
	 * re-place path as `reposition()` / `_onSyncScroll`, so focus and the
	 * onOK/onCancel one-shots are left intact.
	 * @internal
	 */
	_reanchor(): void {
		const args = this.getPositionArgs_();
		if (!args) return;
		this.position(args[0], args[1], this._placementPosition(), args[3]);
		// Re-point the arrow once layout settles (same deferral as open()).
		if (this._arrowTimerId !== undefined)
			clearTimeout(this._arrowTimerId);
		const self = this;
		this._arrowTimerId = window.setTimeout(function (): void {
			self._arrowTimerId = undefined;
			if (self.desktop) self._repositionArrow();
		}, 0);
	}

	/** @internal */
	_clearFocusTimer(): void {
		if (this._focusTimerId !== undefined) {
			clearTimeout(this._focusTimerId);
			this._focusTimerId = undefined;
		}
		if (this._restoreFocusTimerId !== undefined) {
			clearTimeout(this._restoreFocusTimerId);
			this._restoreFocusTimerId = undefined;
		}
		if (this._arrowTimerId !== undefined) {
			clearTimeout(this._arrowTimerId);
			this._arrowTimerId = undefined;
		}
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var ok = this.$n('ok'), cancel = this.$n('cancel'), root = this.$n();
		if (ok) this.domListen_(ok, 'onClick', '_onOk');
		if (cancel) this.domListen_(cancel, 'onClick', '_onCancel');
		if (root) this.domListen_(root, 'onKeyDown', '_onKeyDown');
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this._clearFocusTimer();
		var ok = this.$n('ok'), cancel = this.$n('cancel'), root = this.$n();
		if (ok) this.domUnlisten_(ok, 'onClick', '_onOk');
		if (cancel) this.domUnlisten_(cancel, 'onClick', '_onCancel');
		if (root) this.domUnlisten_(root, 'onKeyDown', '_onKeyDown');
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_onKeyDown(evt: zk.Event): void {
		// role="alertdialog" semantics: Escape closes (firing onCancel via
		// the close() override), Enter activates OK (same code path as
		// clicking OK), and Tab is constrained to the OK/Cancel pair so
		// keyboard users can't tab into the background while the popup is
		// "modal".
		var ke = evt.domEvent as KeyboardEvent | undefined;
		if (!ke) return;
		if (ke.key === 'Escape') {
			this.close();
			evt.stop();
			return;
		}
		if (ke.key === 'Enter') {
			// Honour native button semantics: Enter on a focused button
			// activates THAT button. When focus is on the message body or
			// header (e.g. user clicked to select text), Enter must not
			// commit OK unprompted — that would fire a destructive action
			// the user did not intend. Route through _defaultFocus instead
			// so a danger popup with defaultFocus='cancel' stays safe.
			var active = document.activeElement,
				cancelBtn = this.$n('cancel'),
				okBtn = this.$n('ok');
			if (active === cancelBtn && cancelBtn) {
				(cancelBtn as HTMLButtonElement).click();
			} else if (active === okBtn && okBtn) {
				(okBtn as HTMLButtonElement).click();
			} else if (this._defaultFocus === 'cancel' && cancelBtn) {
				(cancelBtn as HTMLButtonElement).click();
			} else if (okBtn) {
				(okBtn as HTMLButtonElement).click();
			}
			evt.stop();
			return;
		}
		if (ke.key === 'Tab') {
			var ok = this.$n('ok'), cancel = this.$n('cancel');
			if (!ok || !cancel) return;
			// DOM order is fixed by the mold: cancel comes first, ok second
			// (confirmpopup.js:37-40). The trap mirrors that order regardless
			// of _defaultFocus — _defaultFocus only controls which button
			// gets initial focus, not which one is last in tab order.
			var active = document.activeElement;
			if (!ke.shiftKey && active === ok) {
				cancel.focus();
				evt.stop();
			} else if (ke.shiftKey && active === cancel) {
				ok.focus();
				evt.stop();
			} else if (active !== ok && active !== cancel) {
				// Focus escaped into the popup's non-button regions
				// (header / message body / popup root after a click). Pull
				// it back to the default-focus button so Tab can never
				// traverse into the underlying page — keeps the modal
				// contract intact per WCAG 2.4.3 / WAI-ARIA dialog.
				var dest = this._defaultFocus === 'cancel' ? cancel : ok;
				dest.focus();
				evt.stop();
			}
		}
	}

	/** @internal */
	_onOk(evt: zk.Event): void {
		evt.stop();
		// Dedup: Enter-to-confirm synthesizes okBtn.click(), and some browsers
		// (Safari) ALSO dispatch a native default-button click, so _onOk can
		// re-enter and fire onOK twice. _okFired is reset only by open(), so a
		// second entry within the same open cycle is a duplicate — drop it.
		if (this._okFired) return;
		this._okFired = true;
		this.fire('onOK', {}, {toServer: true});
		this.close();
	}
	/** @internal */
	_onCancel(evt: zk.Event): void {
		// close() override fires onCancel for the non-OK path; don't
		// duplicate the fire here.
		evt.stop();
		this.close();
	}
}
