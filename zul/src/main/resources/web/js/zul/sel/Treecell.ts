/* Treecell.ts

	Purpose:

	Description:

	History:
		Wed Jun 10 15:32:39     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A treecell.
 *
 * <p>In XUL, treecell cannot have any child, but ZUL allows it.
 * Thus, you could place any kind of children in it. They will be placed
 * right after the image and label.
 *
 * <p>Default {@link #getZclass}: z-treecell
 */
@zk.WrapClass('zul.sel.Treecell')
export class Treecell extends zul.LabelImageWidget<HTMLTableCellElement> {
	public override parent!: zul.sel.Treerow | null;
	public override nextSibling!: zul.sel.Treecell | null;
	public override previousSibling!: zul.sel.Treecell | null;

	private _colspan = 1;
	private _clearCache?: boolean;

	/**
	 * The width can't be specified in this component.
	 */
	public override setWidth(width: string | null): void {
		// This function simply does nothing.
	}

	/** Returns number of columns to span this cell.
	 * Default: 1.
	 * @return int
	 */
	public getColspan(): number {
		return this._colspan;
	}

	/** Sets the number of columns to span this cell.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 * @param int colspan
	 */
	public setColspan(colspan: number, opts?: Record<string, boolean>): this {
		const o = this._colspan;
		this._colspan = colspan = Math.max(colspan, 1);

		if (o !== colspan || (opts && opts.force)) {
			var n = this.$n();
			if (n) n.colSpan = this._colspan;
		}

		return this;
	}

	/** Return the tree that owns this cell.
	 * @return Tree
	 */
	public getTree(): zul.sel.Tree | null {
		return this.parent ? this.parent.getTree() : null;
	}

	protected override domStyle_(no?: zk.DomStyleOptions): string {
		no = zk.copy(no, {width: true}); //bug#3185657: not span content if given width
		var style = '',
			tc = this.getTreecol();
			// B70-ZK-2946: adds the text-align from treecol, same as Listcell
		if (tc) {
			if (tc._align) style += 'text-align: ' + tc._align + ';';
			if (!tc.isVisible()) no = zk.copy(no, {visible: true});
		}
		return super.domStyle_(no) + style;
	}

	/** Returns the tree col associated with this cell, or null if not available.
	 * @return Treecol
	 */
	public getTreecol(): zul.sel.Treecol | null | undefined {
		var tree = this.getTree();
		if (tree && tree.treecols) {
			var j = this.getChildIndex();
			if (j < tree.treecols.nChildren)
				return tree.treecols.getChildAt<zul.sel.Treecol>(j);
		}
		return null;
	}

	/** Returns the level this cell is. The root is level 0.
	 * @return int
	 */
	public getLevel(): number {
		return this.parent ? this.parent.getLevel() : 0;
	}

	/** Returns the maximal length of each item's label.
	 * @return int
	 */
	public getMaxlength(): number {
		var tc = this.getTreecol();
		return tc ? tc.getMaxlength()! : 0;
	}

	protected override domLabel_(): string {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this.getMaxlength()});
	}

	public override getTextNode(): HTMLElement | null | undefined {
		return this.getCaveNode();
	}

	protected override domContent_(): string {
		var s1 = super.domContent_(),
			s2 = this._colHtmlPre();
		return s1 ? s2 ? s2 + '<span class="' + this.$s('text') + '">' + s1 + '</span>' : s1 : s2;
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (this._clearCache) { // B60-ZK-1348
			this._clearCache = false;
			const p = this.parent;
			if (p) {
				p.clearCache(); //$n('open')
			}
		}
	}

	protected override doMouseOver_(evt: zk.Event): void {
		var n = this.$n();

		// ZK-2136: all children should apply -moz-user-select: none
		if (n && zk.gecko && (this._draggable || this.parent!._draggable)
				&& !jq.nodeName(evt.domTarget!, 'input', 'textarea')) {
			jq(n).addClass('z-draggable-over');
		}
		super.doMouseOver_(evt);
	}

	protected override doMouseOut_(evt: zk.Event): void {
		var n = this.$n();

		// ZK-2136: all children should apply -moz-user-select: none
		if (n && zk.gecko && (this._draggable || this.parent!._draggable)
				&& !jq.nodeName(evt.domTarget!, 'input', 'textarea')) {
			jq(n).removeClass('z-draggable-over'); // Bug ZK-580
		}
		super.doMouseOut_(evt);
	}

	public override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);
		//sync frozen
		var tree = this.getTree(),
			frozen = tree ? tree.frozen : null,
			tbody = tree && tree.treechildren ? tree.treechildren.$n() : null;
		if (frozen && tbody) {
			const tds = jq(evt.domTarget!).parents('td');
			for (let i = 0, j = tds.length; i < j; i++) {
				const td = tds[i];
				if (td.parentNode!.parentNode == tbody) {
					tree!._moveToHidingFocusCell(td.cellIndex);
					break;
				}
			}
		}
	}

	public _syncIcon(isRemoved?: boolean): void {
		this.rerender(isRemoved ? -1 : null);
		if (this.parent) {
			this._clearCache = true;
		}
	}

	private _colHtmlPre(): string {
		if (this.parent!.firstChild == this) {
			var item = this.parent!.parent!,
				tree = item.getTree(),
				sb = new zk.Buffer<string>();
			if (tree) {
				if (tree.isCheckmark()) {
					var chkable = item.isSelectable(),
						multi = tree.isMultiple(),
						cmCls = multi ? item.$s('checkbox') : item.$s('radio'),
						ckCls = multi ? ' z-icon-check' : ' z-icon-radio';
					sb.push('<span id="', this.parent!.uuid, '-cm" class="',
							item.$s('checkable'), ' ', cmCls);

					if (!chkable || item.isDisabled())
						sb.push(' ', item.$s('disabled'));

					sb.push('"');
					if (!chkable)
						sb.push(' style="visibility:hidden"');

					sb.push('><i class="', item.$s('icon'), ckCls, '"></i></span>');
				}
			}
			var iconScls = tree ? tree.getZclass() : '',
				pitems = this._getTreeitems(item, tree);
			for (var j = 0, k = pitems.length; j < k; ++j)
				this._appendIcon(sb, iconScls, 'spacer', false);

			if (item.isContainer()) {
				var name = item.isOpen() ? 'open' : 'close';
				this._appendIcon(sb, iconScls, name, true);
			} else {
				this._appendIcon(sb, iconScls, 'spacer', false);
			}
			return sb.join('');
		} else {
			// ZK-4679: We don't have to generate &nbsp; for empty cell to correct the tree's height in IE anymore
			return '';
		}
	}

	private _getTreeitems(item: zul.sel.Treeitem, tree: zul.sel.Tree | null): zul.sel.Treeitem[] {
		var pitems: zul.sel.Treeitem[] = [],
			p: zul.sel.Treeitem | zul.sel.Tree | null = item;
		for (;;) {
			const tch: zul.sel.Treechildren | null = (p as zul.sel.Treeitem).parent;
			if (!tch)
				break;
			p = tch.parent;
			if (!p || p == tree)
				break;
			pitems.unshift(p as zul.sel.Treeitem);
		}
		return pitems;
	}

	private _appendIcon(sb: string[], iconScls: string, name: string, button: boolean): void {
		var openCloseIcon = '';
		sb.push('<span class="');
		if (name == 'spacer') {
			sb.push(iconScls, '-line ', iconScls, '-', name, '"');
		} else {
			var id = '';
			if (button) {
				var item = this.parent;
				if (item)
					id = item.uuid + '-icon';
			}
			sb.push(iconScls, '-icon"');
			var icon = this.getIconOpenClass_();
			if (name.indexOf('close') > -1)
				icon = this.getIconCloseClass_();

			openCloseIcon += '<i id="' + id + '" class="' + icon + ' ' + iconScls
					+ '-' + name + '"></i>';
		}
		if (button) {
			var item = this.parent; // B65-ZK-1608, appendChild() will invoke before treeitem._fixOnAdd()
			if (item)
				sb.push(' id="', item.uuid, '-open"');
		}
		sb.push('>', openCloseIcon, '</span>');
	}

	protected getIconOpenClass_(): string {
		return 'z-icon-caret-down';
	}

	protected getIconCloseClass_(): string {
		return 'z-icon-caret-right';
	}

	public override getWidth(): string | null | undefined {
		var col = this.getTreecol();
		return col ? col.getWidth() : null;
	}

	public override domAttrs_(no?: zk.DomAttrsOptions): string {
		return super.domAttrs_(no)
			+ (this._colspan > 1 ? ' colspan="' + this._colspan + '"' : '');
	}

	protected override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no),
			col = this.getTreecol();
		if (col) {
			if (!col.isVisible()) scls += ' ' + this.$s('hidden-col');
		}
		return scls;
	}

	protected override updateDomContent_(): void {
		super.updateDomContent_();
		if (this.parent)
			this.parent.clearCache();
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<td', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></td>');
	}
}