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
 * <p>Default {@link #getZclass}: z-treerow
 */
@zk.WrapClass('zul.sel.Treerow')
export class Treerow extends zul.Widget<HTMLTableRowElement> {
	public override parent!: zul.sel.Treeitem | null;
	public override firstChild!: zul.sel.Treecell | null;
	public override lastChild!: zul.sel.Treecell | null;
	public override nextSibling!: zul.sel.Treerow | zul.sel.Treechildren | null;
	public override previousSibling!: zul.sel.Treerow | zul.sel.Treechildren | null;
	private _shallCheckClearCache?: boolean;

	/** Returns the {@link Tree} instance containing this element.
	 * @return Tree
	 */
	public getTree(): zul.sel.Tree | null {
		return this.parent ? this.parent.getTree() : null;
	}

	/** Returns the level this cell is. The root is level 0.
	 * @return int
	 */
	public getLevel(): number {
		return this.parent ? this.parent.getLevel() : 0;
	}

	/** Returns the {@link Treechildren} associated with this
	 * {@link Treerow}.
	 * @return Treechildren
	 */
	public getLinkedTreechildren(): zul.sel.Treechildren | null | undefined {
		return this.parent ? this.parent.treechildren : null;
	}

	protected override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no),
			p = this.parent;
		if (p && (!no || !no.zclass)) {
			if (p.isDisabled())
				scls += (scls ? ' ' : '') + this.$s('disabled');
			if (p.isSelected())
				scls += (scls ? ' ' : '') + this.$s('selected');
		}
		return scls;
	}

	protected override domTooltiptext_(): string | null | undefined {
		return this._tooltiptext || this.parent!._tooltiptext || this.parent!.parent!._tooltiptext;
	}

	//@Override
	protected override domStyle_(no?: zk.DomStyleOptions): string {
		// patch the case that treerow is hidden by treeitem visibility
		return ((this.parent && !this.parent._isRealVisible() && this.isVisible()) ?
				'display:none;' : '') + super.domStyle_(no);
	}

	//@Override
	public override removeChild(child: zk.Widget, ignoreDom?: boolean): boolean {
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

	//@Override
	public override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var ti = this.parent!,
			tg = evt.domTarget;
		if (tg == this.$n('open') || tg == this.$n('icon')) {
			ti.setOpen(!ti._open);
			evt.stop();
			this.getTree()!.focus();
		} else if (!ti.isDisabled())
			super.doClick_(evt, popupOnly);
	}

	//@Override
	public override scrollIntoView(): this {
		var bar = this.getTree()!._scrollbar;
		if (bar) {
			bar.syncSize();
			bar.scrollToElement(this.$n_());
		} else {
			super.scrollIntoView();
		}
		return this;
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tr>');
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onResponse: this});
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onResponse: this});
		super.unbind_(skipper, after, keepRod);
	}

	protected override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		// ZK-5107
		this._shallCheckClearCache = true;
	}

	protected override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		// ZK-5107
		this._shallCheckClearCache = true;
	}

	public onResponse(): void {
		if (this._shallCheckClearCache) {
			this._shallCheckClearCache = false;
			let p = this.getTree();
			if (p && p.isCheckmark()) {
				this.clearCache();
			}
		}
	}
}