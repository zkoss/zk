/* Auxhead.js

	Purpose:
		
	Description:
		
	History:
		Mon May  4 15:57:46     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.mesh.Auxhead = zk.$extends(zul.mesh.HeadWidget, {
	//super//
	getZclass: function () {
		return this._zclass == null ? "z-auxhead" : this._zclass;
	}
});
