/* Filename.js

	Purpose:
		
	Description:
		
	History:
		Thu May 15 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

zul.wgt.Filename = zk.$extends(zul.Widget, {
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-progressmeter";
	},

	bind_: function () {//after compose
		this.$supers('bind_', arguments); 
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
	}

})

//add getter and setter part
zk.def(zul.wgt.Filename, { 
	xxx: function () {
		
	}
});