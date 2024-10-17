/* Treerow.ts

	Purpose:

	Description:

	History:
		Wed Jun 10 15:32:43     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A treerow.
 * @defaultValue {@link getZclass}: z-treerow
 */
@zk.WrapClass('zul.sel.Treerow')
export class Treerow extends zul.Widget<HTMLTableRowElement> {
	override parent!: zul.sel.Treeitem | undefined;
	override firstChild!: zul.sel.Treecell | undefined;
	override lastChild!: zul.sel.Treecell | undefined;
	override nextSibling!: zul.sel.Treerow | zul.sel.Treechildren | undefined;
	override previousSibling!: zul.sel.Treerow | zul.sel.Treechildren | undefined;
	/** @internal */
	_shallCheckClearCache?: boolean;

	/**
	 * @returns the {@link Tree} instance containing this element.
	 */
	getTree(): zul.sel.Tree | undefined {
		return this.parent ? this.parent.getTree() : undefined;
	}

	/**
	 * @returns the level this cell is. The root is level 0.
	 */
	getLevel(): number {
		return this.parent ? this.parent.getLevel() : 0;
	}

	/**
	 * @returns the {@link Treechildren} associated with this {@link Treerow}.
	 */
	getLinkedTreechildren(): zul.sel.Treechildren | undefined {
		return this.parent ? this.parent.treechildren : undefined;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ scls = super.domClass_(no),
			p = this.parent;
		if (p && (!no || !no.zclass)) {
			if (p.isDisabled())
				scls += (scls ? ' ' : '') + this.$s('disabled');
			if (p.isSelected())
				scls += (scls ? ' ' : '') + this.$s('selected');
		}
		return scls;
	}

	/** @internal */
	override domTooltiptext_(): string | undefined {
		return this._tooltiptext || this.parent!._tooltiptext || this.parent!.parent!._tooltiptext;
	}

	/** @internal */
	override domStyle_(no?: zk.DomStyleOptions): string {
		// patch the case that treerow is hidden by treeitem visibility
		return ((this.parent && !this.parent._isRealVisible() && this.isVisible()) ?
				'display:none;' : '') + super.domStyle_(no);
	}

	override removeChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		for (var w = child.firstChild; w;) {
			var n = w.nextSibling; //remember, since remove will null the link
			child.removeChild(w); //deep first
			w = n;
		}
		super.removeChild(child, ignoreDom);
		// FIXME: Prior to TS migration, this function returns nothing ,which will
		// evaluate to undefined, which is falsey. But, doesn't it make more sense
		// to return `super.removeChild(child, ignoreDom)`?
		return false;
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var ti = this.parent!,
			tg = evt.domTarget;
		if (tg == this.$n('open') || tg == this.$n('icon')) {
			ti.setOpen(!ti._open);
			evt.stop();
			this.getTree()!.focus();
		} else if (!ti.isDisabled())
			super.doClick_(evt, popupOnly);
	}

	override scrollIntoView(): this {
		var bar = this.getTree()!._scrollbar;
		if (bar) {
			bar.syncSize();
			bar.scrollToElement(this.$n_());
		} else {
			super.scrollIntoView();
		}
		return this;
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tr>');
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({ onSize: this });
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({ onSize: this });
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		// ZK-5107
		this._shallCheckClearCache = true;
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		// ZK-5107
		this._shallCheckClearCache = true;
	}

	override onSize(): void {
		if (this._shallCheckClearCache) {
			this._shallCheckClearCache = false;
			if (this.getTree()?.isCheckmark()) {
				this.clearCache();
			}
		}
	}

	/** @internal */
	isLastVisibleChild_(): boolean {
		const treeitem = this.parent;
		return !!treeitem && treeitem == treeitem.parent?.getLastVisibleItem_();
	}
}