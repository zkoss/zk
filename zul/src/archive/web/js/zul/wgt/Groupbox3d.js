/* Groupbox3d.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:43:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Groupbox3d = zk.$extends(zul.wgt.Groupbox, {
	legend: false,

	//super//
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls: "z-groupbox";
	}
});