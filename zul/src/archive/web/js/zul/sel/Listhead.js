/* Listhead.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:25:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listhead = zk.$extends(zul.mesh.HeadWidget, {
	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listhead" : this._zclass;
	}
});