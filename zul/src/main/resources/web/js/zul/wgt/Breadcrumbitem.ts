/* Breadcrumbitem.ts

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:03:53 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * An item inside a {@link zul.wgt.Breadcrumb}.
 * @defaultValue {@link getZclass}: "z-breadcrumbitem".
 */
@zk.WrapClass('zul.wgt.Breadcrumbitem')
export class Breadcrumbitem extends zul.LabelImageWidget {
	/** @internal */
	_href?: string;
	/** @internal */
	_target?: string;
	/** @internal */
	_disabled = false;

	/**
	 * Returns the navigation target URL. When undefined, the item renders as
	 * plain text (typically the last item).
	 * @defaultValue `undefined`.
	 */
	getHref(): string | undefined { return this._href; }
	/**
	 * Sets the navigation target URL. An unsafe scheme (javascript:, vbscript:,
	 * blob:, filesystem:, or a non-image data: URI) is rejected and leaves the
	 * value unchanged.
	 * @param href - the URL the item links to; an empty string renders the item
	 * as plain text.
	 */
	setHref(href: string, opts?: Record<string, boolean>): this {
		// Mirror Breadcrumbitem.java#setHref: reject schemes that turn an
		// <a href> into a script-execution or resource-exfiltration vector.
		// Strip ASCII control characters before the prefix test because
		// browsers strip 0x09 / 0x0A / 0x0D from URL schemes before parsing
		// — without this, "java\tscript:..." would slip past the literal
		// "javascript:" check but still execute in the resulting <a>.
		if (href != null && href !== '') {
			// Strip the same control-char set that Breadcrumbitem.java mirrors.
			var t = href.replace(/[\x00-\x1F\x7F]/g, '').trim().toLowerCase(); // eslint-disable-line no-control-regex
			if (t.startsWith('javascript:') || t.startsWith('vbscript:')
					|| t.startsWith('blob:') || t.startsWith('filesystem:')
					|| t.startsWith('data:image/svg+xml')
					|| (t.startsWith('data:') && !t.startsWith('data:image/'))) {
				zk.error('Breadcrumbitem: unsafe href scheme rejected — ' + href);
				return this;
			}
		}
		const o = this._href;
		this._href = href;
		if (o !== href || opts?.force) this.rerender();
		return this;
	}

	/**
	 * Returns the browsing context target (e.g. "_blank").
	 * @defaultValue `undefined`.
	 */
	getTarget(): string | undefined { return this._target; }
	/**
	 * Sets the browsing context target (e.g. "_blank").
	 * @param target - the target browsing context; an empty string clears it.
	 */
	setTarget(target: string, opts?: Record<string, boolean>): this {
		const o = this._target;
		this._target = target;
		if (o !== target || opts?.force) this.rerender();
		return this;
	}

	/**
	 * Returns whether this item is disabled (non-clickable).
	 * @defaultValue `false`.
	 */
	isDisabled(): boolean { return this._disabled; }
	/**
	 * Sets whether this item is disabled (non-clickable).
	 * @param disabled - whether the item is disabled.
	 */
	setDisabled(disabled: boolean, opts?: Record<string, boolean>): this {
		const o = this._disabled;
		this._disabled = disabled;
		// rerender — not just updateDomClass_ — because the mold picks
		// <a> vs <span> based on (_href && !_disabled). Class-only update
		// would leave the wrong element type in the DOM.
		if (o !== disabled || opts?.force) this.rerender();
		return this;
	}

	/**
	 * @returns the widget's CSS class.
	 * @defaultValue `z-breadcrumbitem`.
	 */
	override getZclass(): string {
		return this._zclass == null ? 'z-breadcrumbitem' : this._zclass;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		// A self-rerender (setLabel/setHref/setDisabled → rerender) rebuilds
		// this item's <li> WITHOUT the parent's collapse markers (display:none
		// + data-zk-bc-hidden), and the parent only applies collapse in its own
		// bind_, which does not re-run on a child rerender. Re-apply it here —
		// now that our new DOM is in place — so a previously-hidden item does
		// not reappear in the middle of the bar. _applyCollapse is idempotent
		// (it _undoCollapse's first) and rebuilds DOM only, so this can't loop.
		//
		// Guarded on _collapseReady so this fires only for a genuine post-mount
		// child rerender: during the parent's INITIAL bind_, children bind
		// before the parent runs (and flags) its own collapse pass, so they
		// skip here and the parent does the single mount-time pass — avoiding
		// O(N^2) redundant passes when N items mount together.
		var bc = this.parent as unknown as
				{ _applyCollapse?: () => void; _collapseReady?: boolean } | undefined;
		if (bc && bc._collapseReady && typeof bc._applyCollapse === 'function')
			bc._applyCollapse();
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ sc = super.domClass_(no);
		if (!no || !no.zclass) {
			if (this._disabled) sc += ' ' + this.$s('disabled');
		}
		return sc;
	}

	/** @internal */
	override doClick_(evt: zk.Event): void {
		if (this._disabled) {
			evt.stop();
			return;
		}
		super.doClick_(evt);
	}
}
