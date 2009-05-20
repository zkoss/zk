/* Label.js

	Purpose:
		
	Description:
		
	History:
		Sun Oct  5 00:22:03     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Label = zk.$extends(zul.Widget, {
	_value: '',

	$define: {
		value: _zkf = function () {
			var n = this.getNode();
			if (n) n.innerHTML = this.getEncodedText();
		},
		multiline: _zkf,
		pre: _zkf
	},

	getEncodedText: function () {
		return zUtl.encodeXML(this._value, {multiline:this._multiline,pre:this._pre});
	},

	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-label";
	}
});
