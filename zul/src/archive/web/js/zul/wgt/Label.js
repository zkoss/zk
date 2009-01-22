/* Label.js

	Purpose:
		
	Description:
		
	History:
		Sun Oct  5 00:22:03     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Label = zk.$extends(zul.Widget, {
	_value: '',

	/** Returns the value of this label.
	 */
	getValue: function () {
		return this._value;
	},
	/** Sets the value of this label.
	 */
	setValue: function(value) {
		if (!value) value = '';
		if (this._value != value) {
			this._value = value;
			var n = this.getNode();
			if (n) n.innerHTML = this.getEncodedText();
		}
	},
	getEncodedText: function () {
		var value = zUtl.encodeXML(this._value);
		return value.replace(/ /g, '&nbsp;').replace(/\t/, '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
	},
	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-label";
	}
});
