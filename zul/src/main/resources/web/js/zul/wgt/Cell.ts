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
 * <p>Default {@link #getZclass}: z-cell.
 * @import zul.grid.*
 * @import zul.box.*
 */
@zk.WrapClass('zul.wgt.Cell')
export class Cell extends zul.Widget<HTMLTableCellElement> {
	private _colspan = 1;
	private _rowspan = 1;
	private _rowType = 0;
	private _boxType = 1;
	private _align?: string;
	private _valign?: string;
	public _headerVisible?: boolean;

	/** Returns number of columns to span.
	 * Default: 1.
	 * @return int
	 */
	public getColspan(): number {
		return this._colspan;
	}

	/** Sets the number of columns to span.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 * @param int colspan
	 */
	public setColspan(v: number, opts?: Record<string, boolean>): this {
		const o = this._colspan;
		this._colspan = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n)
				n.colSpan = v;
		}

		return this;
	}

	/** Returns number of rows to span.
	 * Default: 1.
	 * @return int
	 */
	public getRowspan(): number {
		return this._rowspan;
	}

	/** Sets the number of rows to span.
	 * <p>It is the same as the rowspan attribute of HTML TD tag.
	 * @param int rowspan
	 */
	public setRowspan(v: number, opts?: Record<string, boolean>): this {
		const o = this._rowspan;
		this._rowspan = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n)
				n.rowSpan = v;
		}

		return this;
	}

	/** Returns the horizontal alignment.
	 * <p>Default: null (system default: left unless CSS specified).
	 * @return String
	 */
	public getAlign(): string | undefined {
		return this._align;
	}

	/** Sets the horizontal alignment.
	 * @param String align
	 */
	public setAlign(v: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n)
				n.align = v;
		}

		return this;
	}

	/** Returns the vertical alignment.
	 * <p>Default: null (system default: top).
	 * @return String
	 */
	public getValign(): string | undefined {
		return this._valign;
	}

	/** Sets the vertical alignment of this grid.
	 * @param String valign
	 */
	public setValign(v: string, opts?: Record<string, boolean>): this {
		const o = this._valign;
		this._valign = v;

		if (o !== v || (opts && opts.force)) {
			var n = this.$n();
			if (n)
				n.vAlign = v;
		}

		return this;
	}

	private _getParentType(): number | null {
		var isRow = zk.isLoaded('zul.grid') && this.parent instanceof zul.grid.Row;
		if (!isRow) {
			return zk.isLoaded('zul.box') && this.parent instanceof zul.box.Box ?
					this._boxType : null;
		}
		return this._rowType;
	}

	private _getRowAttrs(): string {
		return (this.parent as zul.grid.Row)._childAttrs(this, this.getChildIndex());
	}

	private _getBoxAttrs(): string {
		return (this.parent as zul.box.Box)._childInnerAttrs(this);
	}

	public _colHtmlPre(): string {
		var s = '',
			p = this.parent;
		if (zk.isLoaded('zkex.grid') && p instanceof zkex.grid.Group && this == p.firstChild)
			s += p.domContent_();
		return s;
	}

	protected override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (this._getParentType() == this._rowType) {
			if (this._headerVisible === false) scls += ' ' + this.$s('hidden-column');
		}
		return scls;
	}

	protected override domStyle_(no?: zk.DomStyleOptions): string {
		var style = '';
		if (this._align)
			style += ' text-align:' + this._align + ';';
		if (this._valign)
			style += ' vertical-align:' + this._valign + ';';
		if (this._getParentType() == this._rowType && this._headerVisible === false)
			no = zk.copy(no, {visible: true});
		return super.domStyle_(no) + style;
	}

	//super//
	public override domAttrs_(no?: zk.DomAttrsOptions): string {
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

	public override setVisible(visible: boolean | undefined): void {
		super.setVisible(visible);
		// B65-ZK-2015: redoCSS in IE10 to make sure component will show when visible is true
		if (zk.ie10_ && visible)
			zk(this.$n()).redoCSS();

	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<td', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></td>');
	}

	public override getFlexContainer_(): HTMLElement | null | undefined {
		return null;
	}
}