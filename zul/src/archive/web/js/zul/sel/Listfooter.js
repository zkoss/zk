/* Listfooter.js

	Purpose:
		
	Description:
		
	History:
		Tue Jun  9 18:03:07     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A column of the footer of a list box ({@link Listbox}).
 * Its parent must be {@link Listfoot}.
 *
 * <p>Unlike {@link Listheader}, you could place any child in a list footer.
 * <p>Note: {@link Listcell} also accepts children.
 * <p>Default {@link #getZclass}: z-listfooter.
 */
zul.sel.Listfooter = zk.$extends(zul.LabelImageWidget, {
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

	/** Returns the listbox that this belongs to.
	 * @return Listbox
	 */
	getListbox: function () {
		return this.parent ? this.parent.parent : null;
	},
	/** Returns the list header that is in the same column as
	 * this footer, or null if not available.
	 * @return Listheader
	 */
	getListheader: function () {
		var listbox = this.getListbox();
		if (listbox) {
			var cs = listbox.listhead;
			if (cs)
				return cs.getChildAt(this.getChildIndex());
		}
		return null;
	},
	getZclass: function () {
		return this._zclass == null ? "z-listfooter" : this._zclass;
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
		var head = this.getListheader(),
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
