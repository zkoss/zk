/* Hlayout.js

	Purpose:
		
	Description:
		
	History:
		Fri Aug  6 11:54:19 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A horizontal layout.
 * <p>Default {@link #getZclass}: z-hlayout.
 * @since 5.0.4
 */
zul.box.Hlayout = zk.$extends(zul.box.Layout, {
	_valign: "top",
	$define: { //zk.def
		/** Sets the vertical-align to top or bottom.
		 *
		 * @param String valign the value of vertical-align property
		 * "middle", "bottom".
		 * @since 6.0.0
		 */
		/** Returns the current valign.
		 * @return String
		 */
		valign: function () {
			 this.updateDomClass_();
		}
	},
	isVertical_: function () {
		return false;
	},
	getZclass: function () {
		return this._zclass == null ? "z-hlayout" : this._zclass;
	},
	// F60-ZK-537: Hlayout supports valign (top, middle and bottom),
	// set vertical-align to children cause wrong layout on IE6,
	// set it to parent directly
	domClass_: function () {
		var clsnm = this.$supers('domClass_', arguments),
			v;
		if (v = this._valign)
			if (v == "top") ;
			else if (v == "middle") clsnm += " z-valign-middle";
			else if (v == "bottom") clsnm += " z-valign-bottom";
		return clsnm;
	}
});