/* Footer.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 12:26:51     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A column of the footer of a grid ({@link Grid}).
 * Its parent must be {@link Foot}.
 *
 * <p>Unlike {@link Column}, you could place any child in a grid footer.
 * <p>Default {@link #getZclass}: z-footer.
 */
zul.grid.Footer = zk.$extends(zul.LabelImageWidget, {
	_span: 1,

	$define: {
		/** Returns number of columns to span this footer.
		 * Default: 1.
		 * @return int
		 */
		/** Sets the number of columns to span this footer.
		 * <p>It is the same as the colspan attribute of HTML TD tag.
		 * @param int span
		 */
		span: function (v) {
			var n = this.$n();
			if (n) n.colSpan = v;
		},
		/** Returns the horizontal alignment of this column.
    	 * <p>Default: null (system default: left unless CSS specified).
    	 * @return String
    	 */
    	/** Sets the horizontal alignment of this column.
    	 * @param String align
    	 */
		align: function (v) {
			var n = this.$n();
			if (n) n.align = v;
		},
		/** Returns the vertical alignment of this grid.
		 * <p>Default: null (system default: top).
		 * @return String
		 */
		/** Sets the vertical alignment of this grid.
		 * @param String valign
		 */
		valign: function (v) {
			var n = this.$n();
			if (n) n.vAlign = v;
		}
	},
	/** Returns the grid that this belongs to.
	 * @return zul.grid.Grid
	 */
	getGrid: function () {
		return this.parent ? this.parent.parent : null;
	},
	/** Returns the column that is in the same column as
	 * this footer, or null if not available.
	 * @return zul.grid.Column
	 */
	getColumn: function () {
		var grid = this.getGrid();
		if (grid) {
			var cs = grid.columns;
			if (cs)
				return cs.getChildAt(this.getChildIndex());
		}
		return null;
	},
	getZclass: function () {
		return this._zclass == null ? "z-footer" : this._zclass;
	},
	getAlignAttrs: function () {
		return (this._align ? ' align="' + this._align + '"' : '')
			+ (this._valign ? ' valign="' + this._valign + '"' : '') ;
	},
	//super
	domStyle_: function (no) {
		var style = '';
		if (zk.ie8 && this._align)
			style += 'text-align:' + this._align + ';';
		
		return style + this.$super('domStyle_', no);
	},
	domAttrs_: function () {
		var head = this.getColumn(),
			added;
		if (head)
			added = head.getColAttrs();
		if (this._align || this._valign)
			added = this.getAlignAttrs();
		return this.$supers('domAttrs_', arguments)
			+ (this._span > 1 ? ' colspan="' + this._span + '"' : '')
			+ (added ? ' ' + added : '');
	}
});