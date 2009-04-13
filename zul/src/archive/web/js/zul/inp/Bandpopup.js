/* Bandpopup.js

	Purpose:
		
	Description:
		
	History:
		Fri Apr  3 15:24:37     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Bandpopup = zk.$extends(zul.Widget, {
	//super
	preserveBlank: true, //iZuml
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-bandpopup";
	}
});
