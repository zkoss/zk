/* Combobox.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:52:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Combobox = zk.$extends(zul.inp.InputWidget, {
	_btnVisible: true,
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-combobox";
	}
});