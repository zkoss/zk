/* Doublebox.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 13:35:32     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Doublebox = zk.$extends(zul.inp.FormatWidget, {
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-doublebox";
	}
});