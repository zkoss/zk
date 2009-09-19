/* Foot.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 12:26:51     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Foot = zk.$extends(zul.Widget, {
	getGrid: function () {
		return this.parent;
	},
	getZclass: function () {
		return this._zclass == null ? "z-foot" : _zclass;
	}
});