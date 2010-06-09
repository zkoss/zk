/* Div.js

	Purpose:
		
	Description:
		
	History:
		Sun Oct  5 00:20:23     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The same as HTML DIV tag.
 * <p>Note: a {@link zul.wnd.Window} without title and caption has the same visual effect
 * as {@link Div}, but {@link Div} doesn't implement IdSpace.
 * In other words, {@link Div} won't affect the uniqueness of identifiers.
 */
zul.wgt.Div = zk.$extends(zul.Widget, {
	$define: {
		/** Returns the alignment.
		 * <p>Default: null (use browser default).
		 * @return String
		 */
		/** Sets the alignment: one of left, center, right, ustify,
		 * @param String align
		 */
		align: function (v) {
			var n = this.$n();
			if (n)
				n.align = v;
		}
	},
	//super//
	domAttrs_: function(no) {
		var align = this._align,
			attr = this.$supers('domAttrs_', arguments);
		return align != null ? attr + ' align="' + align + '"' : attr;
	}
});
