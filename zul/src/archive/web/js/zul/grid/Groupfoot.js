/* Groupfoot.js

	Purpose:
		
	Description:
		
	History:
		Fri May 15 15:25:15     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Groupfoot = zk.$extends(zul.grid.Row, {
	getZclass: function () {
		return this._zclass != null ? this._zclass : "z-groupfoot";
	},
	isStripeable_: function () {
		return false;
	}
});
