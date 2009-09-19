/* Listfoot.js

	Purpose:
		
	Description:
		
	History:
		Tue Jun  9 18:03:06     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listfoot = zk.$extends(zul.Widget, {
	getListbox: function () {
		return this.parent;
	},
	getZclass: function () {
		return this._zclass == null ? "z-listfoot" : this._zclass;
	}
});
