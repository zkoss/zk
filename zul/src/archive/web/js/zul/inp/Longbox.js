/* Longbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:43:22     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Longbox = zk.$extends(zul.inp.Intbox, {
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-longbox";
	}
});