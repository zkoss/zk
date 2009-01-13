/* math.js

	Purpose:
		
	Description:
		
	History:
		Sun Dec 14 17:16:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/*zk.BigInteger = zk.$extends(zk.Object, {
	$init: function (value) {
		this._value = value ? '' + value: '0';
	}
});*/
zk.BigDecimal = zk.$extends(zk.Object, {
	_prec: 0,
	$init: function (value) {
		value = value ? '' + value: '0';
		var j = value.lastIndexOf('.');
		if (j >= 0) {
			value = value.substring(0, j) + value.substring(j + 1);
			this._prec = value.length - j;
		}
		this._value = value;
	}
});
