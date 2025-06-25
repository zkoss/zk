/* Auxheader.js

	Purpose:

	Description:

	History:
		Mon May  4 17:00:30     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An auxiliary header.
 * <p>Default {@link #getZclass}: z-auxheader.
 */
zul.mesh.Auxheader = zk.$extends(zul.mesh.HeaderWidget, {
	_colspan: 1,
	_rowspan: 1,

	$define: {
		/** Returns number of columns to span this header.
		 * Default: 1.
		 * @return int
		 */
		/** Sets the number of columns to span this header.
		 * <p>It is the same as the colspan attribute of HTML TD tag.
		 * @param int colspan
		 */
		colspan: function (v) {
			var n = this.$n();
			if (n) {
				n.colSpan = v;
			}
		},
		/** Returns number of rows to span this header.
		 * Default: 1.
		 * @return int
		 */
		/** Sets the number of rows to span this header.
		 * <p>It is the same as the rowspan attribute of HTML TD tag.
		 * @param int rowspan
		 */
		rowspan: function (v) {
			var n = this.$n();
			if (n) {
				n.rowSpan = v;
			}
		}
	},
	//super//
	domAttrs_: function () {
		var s = this.$supers('domAttrs_', arguments), v;
		if ((v = this._colspan) != 1)
			s += ' colspan="' + v + '"';
		if ((v = this._rowspan) != 1)
			s += ' rowspan="' + v + '"';
		return s;
	}
});