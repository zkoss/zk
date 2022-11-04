/* Cell.ts

	Purpose:

	Description:

	History:
		Mon Aug 31 16:50:22     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The generic cell component to be embedded into {@link Row} or
 * {@link zul.box.Box} for fully control style and layout.
 *
 * @defaultValue {@link getZclass}: z-cell.
 * @import zul.grid.*
 * @import zul.box.*
 */
@zk.WrapClass('zul.wgt.Cell')
export class Cell extends zul.Widget<HTMLTableCellElement> {
	/** @internal */
	_colspan = 1;
	/** @internal */
	_rowspan = 1;
	/** @internal */
	_rowType = 0;
	/** @internal */
	_boxType = 1;
	/** @internal */
	_align?: string;
	/** @internal */
	_valign?: string;
	/** @internal */
	_headerVisible?: boolean;

	/**
	 * @returns number of columns to span.
	 * @defaultValue `1`.
	 */
	getColspan(): number {
		return this._colspan;
	}

	/**
	 * Sets the number of columns to span.
	 * It is the same as the colspan attribute of HTML TD tag.
	 */
	setColspan(colspan: number, opts?: Record<string, boolean>): this {
		const o = this._colspan;
		this._colspan = colspan;

		if (o !== colspan || opts?.force) {
			var n = this.$n();
			if (n)
				n.colSpan = colspan;
		}

		return this;
	}

	/**
	 * @returns number of rows to span.
	 * @defaultValue `1`.
	 */
	getRowspan(): number {
		return this._rowspan;
	}

	/**
	 * Sets the number of rows to span.
	 * <p>It is the same as the rowspan attribute of HTML TD tag.
	 */
	setRowspan(rowspan: number, opts?: Record<string, boolean>): this {
		const o = this._rowspan;
		this._rowspan = rowspan;

		if (o !== rowspan || opts?.force) {
			var n = this.$n();
			if (n)
				n.rowSpan = rowspan;
		}

		return this;
	}

	/**
	 * @returns the horizontal alignment.
	 * @defaultValue `null` (system default: left unless CSS specified).
	 */
	getAlign(): string | undefined {
		return this._align;
	}

	/**
	 * Sets the horizontal alignment.
	 */
	setAlign(align: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = align;

		if (o !== align || opts?.force) {
			var n = this.$n();
			if (n)
				n.align = align;
		}

		return this;
	}

	/**
	 * @returns the vertical alignment.
	 * @defaultValue `null` (system default: top).
	 */
	getValign(): string | undefined {
		return this._valign;
	}

	/**
	 * Sets the vertical alignment of this grid.
	 */
	setValign(valign: string, opts?: Record<string, boolean>): this {
		const o = this._valign;
		this._valign = valign;

		if (o !== valign || opts?.force) {
			var n = this.$n();
			if (n)
				n.vAlign = valign;
		}

		return this;
	}

	/** @internal */
	_getParentType(): number | undefined {
		var isRow = zk.isLoaded('zul.grid') && this.parent instanceof zul.grid.Row;
		if (!isRow) {
			return zk.isLoaded('zul.box') && this.parent instanceof zul.box.Box ?
					this._boxType : undefined;
		}
		return this._rowType;
	}

	/** @internal */
	_getRowAttrs(): string {
		return (this.parent as zul.grid.Row)._childAttrs(this, this.getChildIndex());
	}

	/** @internal */
	_getBoxAttrs(): string {
		return (this.parent as zul.box.Box)._childInnerAttrs(this);
	}

	/** @internal */
	_colHtmlPre(): string {
		var s = '',
			p = this.parent;
		if (zk.isLoaded('zkex.grid') && p instanceof zkex.grid.Group && this == p.firstChild)
			s += p.domContent_();
		return s;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (this._getParentType() == this._rowType) {
			if (this._headerVisible === false) scls += ' ' + this.$s('hidden-column');
		}
		return scls;
	}

	/** @internal */
	override domStyle_(no?: zk.DomStyleOptions): string {
		var style = '';
		if (this._align)
			style += ' text-align:' + this._align + ';';
		if (this._valign)
			style += ' vertical-align:' + this._valign + ';';
		if (this._getParentType() == this._rowType && this._headerVisible === false)
			no = zk.copy(no, {visible: true});
		return super.domStyle_(no) + style;
	}

	/** @internal */
	override domAttrs_(no?: zk.DomAttrsOptions): string {
		var s = super.domAttrs_(no), v;
		if ((v = this._colspan) != 1)
			s += ' colspan="' + v + '"';
		if ((v = this._rowspan) != 1)
			s += ' rowspan="' + v + '"';

		var m1: Record<string, string> | undefined,
			m2 = zUtl.parseMap(s, ' ', '"');
		switch (this._getParentType()) {
		case this._rowType:
			m1 = zUtl.parseMap(this._getRowAttrs(), ' ', '"');
			break;
		case this._boxType:
			m1 = zUtl.parseMap(this._getBoxAttrs(), ' ', '"');
			break;
		}
		if (m1) {
			//Bug ZK-1349: merge user style and row style instead of override
			var s1: string | Record<string, string> = m1.style,
				s2: string | Record<string, string> = m2.style,
				style;
			if (s1 && s2) {
				s1 = zUtl.parseMap(s1.replace(/"/g, '').replace(/:/g, '='), ';');
				s2 = zUtl.parseMap(s2.replace(/"/g, '').replace(/:/g, '='), ';');
				zk.copy(s1, s2);
				style = zUtl.mapToString(s1, ':', ';');
			}
			zk.copy(m1, m2);
			if (style)
				m1.style = '"' + style + '"';
		}
		return ' ' + zUtl.mapToString(m1!); // FIXME: m1 could be undefined
	}

	override setVisible(visible: boolean): this {
		super.setVisible(visible);
		// B65-ZK-2015: redoCSS in IE10 to make sure component will show when visible is true
		if (zk.ie10_ && visible)
			zk(this.$n()).redoCSS();
		return this;
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push('<td', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></td>');
	}

	/** @internal */
	override getFlexContainer_(): HTMLElement | undefined {
		return undefined;
	}
}