/* Intbox.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 12:33:22     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Intbox = zk.$extends(zul.inp.FormatWidget, {
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-intbox";
	}
});