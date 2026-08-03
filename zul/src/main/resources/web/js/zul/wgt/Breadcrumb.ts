/* Breadcrumb.ts

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:03:43 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * A breadcrumb navigation container.
 * @defaultValue {@link getZclass}: "z-breadcrumb".
 */
@zk.WrapClass('zul.wgt.Breadcrumb')
export class Breadcrumb extends zul.Widget {
	/** @internal */
	_separator = '/';
	/** @internal */
	_maxItems = 0;
	/** @internal */
	_ellipsisBtn?: HTMLButtonElement;
	/**
	 * True once the user has clicked the ellipsis to reveal the collapsed
	 * items. While set, {@link _applyCollapse} keeps everything visible so a
	 * child's later self-rerender doesn't silently snap the bar shut. Reset on
	 * a full rerender (bind_), i.e. when separator / maxItems change or the
	 * server re-renders the component.
	 * @internal
	 */
	_expanded = false;
	/**
	 * True after bind_ has run the initial collapse. The child re-collapse hook
	 * (Breadcrumbitem.bind_) checks this so it fires only for a genuine
	 * post-mount child rerender — not once per child during the parent's
	 * initial bind, which would be O(N^2) redundant collapse passes.
	 * @internal
	 */
	_collapseReady = false;

	/**
	 * Returns the separator between items.
	 * @defaultValue `/`.
	 */
	getSeparator(): string { return this._separator; }
	/**
	 * Sets the separator between items. Also accepts the icon form
	 * "icon:z-icon-chevron-right".
	 * @param separator - the separator text or icon spec.
	 */
	setSeparator(separator: string, opts?: Record<string, boolean>): this {
		// Mirror Breadcrumb.java#setSeparator: a null/empty separator falls back
		// to "/" so a client-bind null/'' push doesn't render a blank separator
		// the server contract forbids.
		if (separator == null || separator === '') separator = '/';
		const o = this._separator;
		this._separator = separator;
		if (o !== separator || opts?.force) this.rerender();
		return this;
	}

	/**
	 * Returns the maximum number of items to display before collapsing. 0 means
	 * no limit.
	 * @defaultValue `0`.
	 */
	getMaxItems(): number { return this._maxItems; }
	/**
	 * Sets the maximum number of items to display before collapsing. Use 0 to
	 * disable collapse; a non-zero value must be at least 2, since the collapse
	 * always keeps the first and last item.
	 * @param maxItems - the maximum number of items, or 0 to disable collapse.
	 */
	setMaxItems(maxItems: number, opts?: Record<string, boolean>): this {
		// Mirror Breadcrumb.java#setMaxItems (0 = unlimited, otherwise >= 2).
		// Clamp + warn rather than throw so a bad MVVM binding doesn't collapse
		// the whole load() pass — the same client-lenient pattern as Avatar#setGap.
		// A negative value disables collapse (0); maxItems = 1 cannot collapse
		// below the first+last pair, so it snaps up to 2.
		if (maxItems < 0 || maxItems === 1) {
			const clamped = maxItems < 0 ? 0 : 2;
			zk.error('Breadcrumb: maxItems must be 0 (unlimited) or >= 2 — '
					+ maxItems + ' coerced to ' + clamped);
			maxItems = clamped;
		}
		const o = this._maxItems;
		this._maxItems = maxItems;
		if (o !== maxItems || opts?.force) this.rerender();
		return this;
	}

	/**
	 * @returns the widget's CSS class.
	 * @defaultValue `z-breadcrumb`.
	 */
	override getZclass(): string {
		return this._zclass == null ? 'z-breadcrumb' : this._zclass;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		// A full (re)render starts collapsed regardless of any prior user
		// expand — separator/maxItems changes route through here.
		this._expanded = false;
		this._applyCollapse();
		this._collapseReady = true;
		var root = this.$n();
		if (root) this.domListen_(root, 'onKeyDown', '_onKeyDown');
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this._collapseReady = false;
		var root = this.$n();
		if (root) this.domUnlisten_(root, 'onKeyDown', '_onKeyDown');
		// Rerender tears down DOM entirely; explicit undo isn't strictly
		// necessary, but cheap insurance against partial-rerender edge cases.
		this._undoCollapse();
		super.unbind_(skipper, after, keepRod);
	}

	/**
	 * Left/Right arrow keys move focus between visible (non-collapsed)
	 * breadcrumb items + the expand-ellipsis button when collapsed. Home /
	 * End jump to first / last. Matches WAI-ARIA Authoring Practices for
	 * breadcrumb-like horizontal navigation.
	 * @internal
	 */
	_onKeyDown(evt: zk.Event): void {
		var ke = evt.domEvent as KeyboardEvent | undefined;
		if (!ke) return;
		var key = ke.key;
		if (key !== 'ArrowLeft' && key !== 'ArrowRight'
				&& key !== 'Home' && key !== 'End') return;
		var root = this.$n();
		if (!root) return;
		// Focusable atoms: each non-hidden item's <a href> (navigable items)
		// or its <span tabindex="-1"> (current-page / disabled items), plus
		// the expand-ellipsis button if present. Order matches DOM. Without
		// the span branch, End would skip the current page entirely because
		// the current item is rendered as <span> (no href).
		var focusable = root.querySelectorAll(
				':scope > ol > li:not([data-zk-bc-hidden]) > a[href], '
				+ ':scope > ol > li:not([data-zk-bc-hidden]) > span[tabindex], '
				+ ':scope > ol > li.' + this.getZclass() + '-ellipsis > button');
		if (focusable.length === 0) return;
		var active = document.activeElement,
			currentIdx = -1;
		for (var i = 0; i < focusable.length; i++) {
			if (focusable[i] === active) { currentIdx = i; break; }
		}
		var nextIdx: number;
		if (key === 'Home') nextIdx = 0;
		else if (key === 'End') nextIdx = focusable.length - 1;
		else if (key === 'ArrowRight')
			nextIdx = currentIdx < 0 ? 0 : Math.min(focusable.length - 1, currentIdx + 1);
		else // ArrowLeft
			nextIdx = currentIdx < 0 ? focusable.length - 1 : Math.max(0, currentIdx - 1);
		(focusable[nextIdx] as HTMLElement).focus();
		evt.stop();
	}

	/**
	 * Click handler for the expand-ellipsis button — reveals all collapsed
	 * items so keyboard / pointer users can reach them, then moves focus
	 * onto the first-revealed item.
	 * @internal
	 */
	_onExpand(evt: zk.Event): void {
		evt.stop();
		var firstRevealed: HTMLElement | undefined,
			root = this.$n();
		if (root) {
			var hidden = root.querySelectorAll('[data-zk-bc-hidden="true"]');
			if (hidden.length > 0) {
				firstRevealed = hidden[0] as HTMLElement;
			}
		}
		this._undoCollapse();
		// Latch the expanded state so a subsequent child self-rerender (which
		// re-runs _applyCollapse via Breadcrumbitem.bind_) does not snap the
		// bar shut behind the user. Reset only by a full rerender (bind_).
		this._expanded = true;
		if (firstRevealed) {
			// The first revealed item may be a navigable <a href> OR a
			// current-page / disabled item the mold renders as <span tabindex>.
			// Match both so focus is never lost after expanding (the ellipsis
			// button that had focus was just removed by _undoCollapse).
			var link = firstRevealed.querySelector<HTMLElement>('a[href], span[tabindex]');
			if (link) link.focus();
		}
	}

	/**
	 * @internal
	 * Single source of the {@link _maxItems} collapse arithmetic, shared by
	 * {@link _hiddenCount} and {@link _applyCollapse} so the two can never
	 * drift (and so the EE `za11y` ellipsis "Show N more" label, which reads
	 * {@link _hiddenCount}, always matches the items actually hidden). Given
	 * the number of rendered items, returns how many to keep at the head and
	 * tail and how many middle items are hidden; `hidden === 0` means "do not
	 * collapse" (max disabled, or too few items to bother).
	 * @param count - the number of rendered (bound) breadcrumb items.
	 */
	_collapseGeometry(count: number): {keepStart: number; keepEnd: number; hidden: number} {
		var max = this._maxItems;
		if (max > 0 && count > max) {
			var keepStart = 1,
				keepEnd = Math.max(1, max - keepStart);
			if (keepStart + keepEnd < count)
				return {keepStart: keepStart, keepEnd: keepEnd, hidden: count - (keepStart + keepEnd)};
		}
		return {keepStart: 0, keepEnd: 0, hidden: 0};
	}

	/**
	 * @internal
	 * Number of middle items the {@link _maxItems} collapse hides (0 when not
	 * collapsing), via the canonical {@link _collapseGeometry}. Exposed so the
	 * EE `za11y` ellipsis "Show N more" label reads one count from here instead
	 * of recomputing the formula cross-repo.
	 */
	_hiddenCount(): number {
		var count = 0;
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w.$n()) count++;
		return this._collapseGeometry(count).hidden;
	}

	/**
	 * @internal
	 * Hides the middle items when {@link _maxItems} is exceeded and inserts
	 * an ellipsis (`…`) plus a synthetic separator in their place. Visible
	 * shape: <code>[first] / [...] / [last (maxItems-1)]</code>. The hidden
	 * widgets remain bound — only `display:none` is toggled — so dynamic
	 * `setLabel`/`setHref` on a hidden item keeps working when it later
	 * becomes visible again.
	 */
	_applyCollapse(): void {
		// If the ellipsis button currently holds focus, a re-collapse rebuilds
		// it (a different node) — capture that so we can restore keyboard focus
		// to the fresh button instead of dropping it to <body>.
		var hadEllipsisFocus = !!this._ellipsisBtn
				&& document.activeElement === this._ellipsisBtn;
		this._undoCollapse();
		// The user expanded the bar — keep everything visible. (Reset on a full
		// rerender via bind_.)
		if (this._expanded) return;
		var root = this.$n();
		if (!root) return;
		var items: HTMLElement[] = [];
		for (var w = this.firstChild; w; w = w.nextSibling) {
			var node = w.$n();
			if (node) items.push(node);
		}
		// Canonical collapse arithmetic — see _collapseGeometry. hidden === 0
		// means nothing to collapse (max disabled / too few items).
		var count = items.length,
			geo = this._collapseGeometry(count);
		if (geo.hidden === 0) return;
		var keepStart = geo.keepStart,
			keepEnd = geo.keepEnd,
			firstHidden = items[keepStart],
			ol = firstHidden.parentElement;
		if (!ol) return;
		// Inject a separator + ellipsis at the start of the hidden range.
		// Clone the first natural separator so icon/text style matches.
		var /*safe*/ zclsHtml = this.getZclass(),
			injectedSep: HTMLElement | undefined,
			sampleSep = items[0].nextElementSibling,
			ellipsisLi = document.createElement('li');
		if (sampleSep && sampleSep.classList.contains(zclsHtml + '-separator')) {
			injectedSep = sampleSep.cloneNode(true) as HTMLElement;
			injectedSep.dataset.zkBcInjected = 'sep';
			ol.insertBefore(injectedSep, firstHidden);
		}
		ellipsisLi.classList.add(zclsHtml + '-ellipsis');
		ellipsisLi.dataset.zkBcInjected = 'ellipsis';
		// Real <button> instead of passive text so keyboard users can
		// Tab to it and Enter / Space to expand. CSS keeps the visual as
		// just "…" but native button gives focus ring + activation.
		var ellipsisBtn = document.createElement('button');
		ellipsisBtn.type = 'button';
		ellipsisBtn.textContent = '…';
		// The "Show N more" aria-label is layered on by the za11y add-on (EE).
		// Wire via the framework's domListen_ so the click is tracked in
		// ZK's listener registry — _undoCollapse calls domUnlisten_ before
		// removing the node, preventing the per-rebuild listener accretion
		// that addEventListener would cause.
		this.domListen_(ellipsisBtn, 'onClick', '_onExpand');
		this._ellipsisBtn = ellipsisBtn;
		ellipsisLi.appendChild(ellipsisBtn);
		ol.insertBefore(ellipsisLi, injectedSep ?? firstHidden);
		// Hide the items in the collapse range + the natural separator that
		// follows each (skip the injected separator above).
		for (var i = keepStart; i < count - keepEnd; i++) {
			var li = items[i];
			// Hide via the data attribute (CSS rule in breadcrumb.less) rather
			// than inline style, so an author's inline `display` survives a
			// collapse/expand cycle untouched.
			li.dataset.zkBcHidden = 'true';
			var nextSep = li.nextElementSibling as HTMLElement | undefined;
			if (nextSep && nextSep.classList.contains(zclsHtml + '-separator')
					&& !nextSep.dataset.zkBcInjected) {
				nextSep.dataset.zkBcHidden = 'true';
			}
		}
		// Restore keyboard focus to the rebuilt ellipsis button if the old one
		// (just removed by _undoCollapse) had it — keeps a keyboard/SR user in
		// place when a child rerender triggers a re-collapse.
		if (hadEllipsisFocus && this._ellipsisBtn) this._ellipsisBtn.focus();
	}

	/** @internal */
	_undoCollapse(): void {
		var root = this.$n();
		if (!root) return;
		// Unlisten the ellipsis button BEFORE removing it so ZK's listener
		// registry doesn't keep a dangling reference to the detached node.
		if (this._ellipsisBtn) {
			this.domUnlisten_(this._ellipsisBtn, 'onClick', '_onExpand');
			this._ellipsisBtn = undefined;
		}
		var injected = root.querySelectorAll('[data-zk-bc-injected]');
		for (var i = 0; i < injected.length; i++) injected[i].remove();
		var hidden = root.querySelectorAll('[data-zk-bc-hidden="true"]');
		for (var j = 0; j < hidden.length; j++) {
			var el = hidden[j] as HTMLElement;
			delete el.dataset.zkBcHidden;
		}
	}
}
