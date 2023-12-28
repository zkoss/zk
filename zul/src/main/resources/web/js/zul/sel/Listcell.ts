/* Listcell.ts

	Purpose:

	Description:

	History:
		Thu Apr 30 22:17:54     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _isListgroup(wgt: zk.Widget): wgt is zkex.sel.Listgroup {
	return zk.isLoaded('zkex.sel') && wgt instanceof zkex.sel.Listgroup;
}
function _isListgroupfoot(wgt: zk.Widget): boolean {
	return zk.isLoaded('zkex.sel') && wgt instanceof zkex.sel.Listgroupfoot;
}
/**
 * A list cell.
 *
 * @defaultValue {@link getZclass}: z-listcell
 */
@zk.WrapClass('zul.sel.Listcell')
export class Listcell extends zul.LabelImageWidget<HTMLTableCellElement> {
	// Parent could be null as asserted by `bindChildren_`.
	override parent?: zul.sel.Listitem;
	override previousSibling?: zul.sel.Listcell;
	override nextSibling?: zul.sel.Listcell;
	/** @internal */
	_span = 1;

	/**
	 * @returns number of columns to span this cell.
	 * @defaultValue `1`.
	 */
	getColspan(): number {
		return this.getSpan();
	}

	/**
	 * Sets the number of columns to span this cell.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	setColspan(colspan: number, opts?: Record<string, boolean>): this {
		return this.setSpan(colspan, opts);
	}

	// change colspan to span since ZK 10.0.0
	getSpan(): number {
		return this._span;
	}

	// change colspan to span since ZK 10.0.0
	setSpan(span: number, opts?: Record<string, boolean>): this {
		const o = this._span;
		this._span = span = Math.max(span, 1);

		if (o !== span || opts?.force) {
			var n = this.$n();
			if (n) n.colSpan = this._span;
		}

		return this;
	}

	override setLabel(label: string, opts?: Record<string, boolean>): this {
		super.setLabel(label, opts);
		if (this.desktop) {
			var p: zk.Widget = this.parent!;
			if (_isListgroup(p))
				p.rerender();
			else if (p instanceof zul.sel.Option || p instanceof zul.sel.Optgroup)
				p.updateLabel_();
		}
		return this;
	}

	/**
	 * @returns the list box that it belongs to.
	 */
	getListbox(): zul.sel.Listbox | undefined {
		var p = this.parent;
		return p ? p.parent : undefined;
	}

	override getTextNode(): HTMLElement | undefined {
		return jq(this.$n_()).find('>div:first')[0];
	}

	/**
	 * @returns the maximal length for this cell.
	 * If listbox's mold is "select", it is the same as
	 * {@link Select#getMaxlength}
	 * If not, it is the same as the correponding {@link getListheader}'s
	 * {@link Listheader#getMaxlength}.
	 *
	 * <p>Note: {@link Option#getMaxlength} is the same as {@link Select#getMaxlength}.
	 */
	getMaxlength(): number | undefined {
		var box = this.getListbox();
		if (!box) return 0;
		if (box.getMold() == 'select') {
			// @ts-expect-error: This is likely dead code and Listbox doesn't have getMaxlength.
			// eslint-disable-next-line @typescript-eslint/no-unsafe-return, @typescript-eslint/no-unsafe-call
			return box.getMaxlength();
		}
		var lc = this.getListheader();
		return lc ? lc.getMaxlength() : 0;
	}

	/**
	 * @returns the list header that is in the same column as
	 * this cell, or null if not available.
	 */
	getListheader(): zul.sel.Listheader | undefined {
		var box = this.getListbox();
		if (box?.listhead) {
			var j = this.getChildIndex();
			if (j < box.listhead.nChildren)
				return box.listhead.getChildAt<zul.sel.Listheader>(j);
		}
		return undefined;
	}

	/** @internal */
	override domLabel_(): string {
		return zUtl.encodeXML(this.getLabel(), { maxlength: this.getMaxlength() });
	}

	/** @internal */
	override domContent_(): string {
		var /*safe*/ s1 = super.domContent_(),
			s2 = this._colHtmlPre();
		return s1 ? s2 ? s2 + '&nbsp;' + s1 : s1 : s2;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var /*safe*/ scls = super.domClass_(no),
			p = this.parent!,
			head = this.getListheader();

		if ((!no || !no.zclass) && (_isListgroup(p) || _isListgroupfoot(p)))
			scls += ' ' + p.$s('inner');
		if (head && !head.isVisible())
			scls += ' ' + this.$s('hidden-header');

		return scls;
	}

	override clearCache(): void {
		if (this.parent) {
			// Listitem.$n('cm') may be wrong if only the listcell is rerendered.
			this.parent.clearCache();
		}
		super.clearCache();
	}

	/** @internal */
	_isFirstVisibleChild(): boolean {
		let firstVisibleChild = this.parent!.firstChild;
		while (firstVisibleChild && !firstVisibleChild.isVisible())
			firstVisibleChild = firstVisibleChild.nextSibling;
		return this == firstVisibleChild;
	}

	/** @internal */
	_colHtmlPre(): string {
		var s = '',
			box = this.getListbox(),
			p = this.parent!;
		// ZK-5037: invisible first column hides checkmarks in a listbox
		if (box != null && (this.getListheader() ?? this)._isFirstVisibleChild()) {
			var isGrp = _isListgroup(p);
			// insert checkmark
			//B70-ZK-2053:make sure checkmark won't display on multiple listgroup
			if (box.isCheckmark() && !_isListgroupfoot(p)
				&& (!isGrp || (box.groupSelect && box.isMultiple()))) {
				// ZK-5035: Remove the old checkmark to prevent duplicate checkmarks
				if (this.parent?.$n('cm'))
					this.nextSibling?.rerender();

				var chkable = p.isSelectable(),
					multi = box.isMultiple();
				/*safe*/ s += '<span id="' + /*safe*/ p.uuid + '-cm" class="' + p.$s('checkable')
					+ ' ' + (multi ? p.$s('checkbox') : p.$s('radio'));

				if (!chkable || p.isDisabled())
					s += ' ' + p.$s('disabled');

				s += '"';
				if (!chkable)
					s += ' style="visibility:hidden"';

				s += '><i class="' + p.$s('icon') + ' '
					+ (multi ? 'z-icon-check' : 'z-icon-radio') + '"></i></span>';
			}
			// insert toggle icon
			if (_isListgroup(p)) { // For "type predicates" to work, isGrp cannot be used.
				var cls = p._open ?
					p.getIconOpenClass_() + ' ' + p.$s('icon-open') :
					p.getIconCloseClass_() + ' ' + p.$s('icon-close');
				/*safe*/ s += '<span id="' + /*safe*/ p.uuid + '-img" class="' + p.$s('icon') + '"><i class="' + /*safe*/ cls + '"></i></span>';
			}
			if (s) return s;
		}
		return (!this.getImage() && !this.getLabel() && !this.firstChild) ? '&nbsp;' : '';
	}

	/** @internal */
	override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);
		//sync frozen
		var box = this.getListbox(),
			frozen = box ? box.frozen : undefined,
			node = this.$n();
		if (frozen && node) // NOTE: non-null frozen implies non-null box
			box!._moveToHidingFocusCell(node.cellIndex);
	}

	/** @internal */
	override doMouseOver_(evt: zk.Event): void {
		var n = this.$n();

		// ZK-2136: all children should apply -moz-user-select: none
		if (n && zk.gecko && (this._draggable || this.parent!._draggable)
			&& !jq.nodeName(evt.domTarget, 'input', 'textarea')) {
			jq(n).addClass('z-draggable-over');
		}
		super.doMouseOver_(evt);
	}

	/** @internal */
	override doMouseOut_(evt: zk.Event): void {
		var n = this.$n();

		// ZK-2136: all children should apply -moz-user-select: none
		if (n && zk.gecko && (this._draggable || this.parent!._draggable)
			&& !jq.nodeName(evt.domTarget, 'input', 'textarea')) {
			jq(n).removeClass('z-draggable-over'); // Bug ZK-580
		}
		super.doMouseOut_(evt);
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		return super.domAttrs_(no)
			+ (this._span > 1 ? ` colspan="${this._span}"` : '');
	}

	//-- super --//
	/** @internal */
	override domStyle_(no?: zk.DomStyleOptions): string {
		var style = '',
			head = this.getListheader();
		if (head) {
			if (head._align)
				style += 'text-align:' + head._align + ';';
			if (head._valign)
				style += 'vertical-align:' + head._valign + ';';
			if (!head.isVisible())
				no = zk.copy(no, { visible: true });
		}
		return super.domStyle_(no) + style;
	}

	/** @internal */
	override bindChildren_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		var p = this.parent;
		if (!p || !(p instanceof zul.sel.Option || p instanceof zul.sel.Optgroup))
			super.bindChildren_(desktop, skipper, after);
	}

	/** @internal */
	override unbindChildren_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var p = this.parent;
		if (!p || !(p instanceof zul.sel.Option || p instanceof zul.sel.Optgroup))
			super.unbindChildren_(skipper, after, keepRod);
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push(`<td ${this.domAttrs_({ domClass: true })} class="z-renderdefer"></td>`);
	}
}