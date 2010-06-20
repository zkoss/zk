/* Foot.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 12:26:51     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Defines a set of footers ({@link Footer}) for a grid ({@link Grid}).
 * <p>Default {@link #getZclass}: z-foot.
 */
zul.grid.Foot = zk.$extends(zul.Widget, {
	/** Returns the grid that contains this column. 
	 * @return zul.grid.Grid
	 */
	getGrid: function () {
		return this.parent;
	},
	getZclass: function () {
		return this._zclass == null ? "z-foot" : _zclass;
	},
	//bug #3014664
	setVflex: function (v) { //vflex ignored for grid Foot
		v = false;
		this.$super(zul.grid.Foot, 'setVflex', v);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for grid Foot
		v = false;
		this.$super(zul.grid.Foot, 'setHflex', v);
	}
});