/* math.js

	Purpose:
		
	Description:
		
	History:
		Sun Dec 14 17:16:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.BigDecimal = zk.$extends(zk.Object, {
	_precision: 0,
	$define: {
		precision: null
	},
	$init: function (value) {
		value = value ? '' + value: '0';
		var j = value.lastIndexOf('.');
		if (j >= 0) {
			value = value.substring(0, j) + value.substring(j + 1);
			this._precision = value.length - j;
			this._dot = true;
		}
		this._value = value;
	},
	toString: function() {
		var j = this._value.length - this._precision;
		return this._value.substring(0, j) + (this._dot ? '.' + this._value.substring(j) : '');
	},
	toLocaleString: function() {
		var j = this._value.length - this._precision;
		return this._value.substring(0, j) + (this._precision ? zk.DECIMAL + this._value.substring(j) : '');
	}
});
