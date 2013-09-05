/* Longbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:43:22     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An edit box for holding an integer.
 * <p>Default {@link #getZclass}: z-longbox.
 */
zul.inp.Longbox = zk.$extends(zul.inp.NumberInputWidget, {
	//bug #2997037, cannot enter large long integer into longbox
	coerceFromString_: function (value) {
		if (!value) return null;
	
		var info = zk.fmt.Number.unformat(this._format, value, false, this._localizedSymbols),
			val = new zk.Long(info.raw),
			sval = val.$toString();
		if (info.raw != sval && info.raw != '-'+sval) //1e2 not supported (unlike Doublebox)
			return {error: zk.fmt.Text.format(msgzul.INTEGER_REQUIRED, value)};
		if (info.divscale)
			val.scale(-info.divscale);
		if (this._isOutRange(val.$toString()))
			return {error: zk.fmt.Text.format(msgzul.OUT_OF_RANGE+'(−9223372036854775808 - 9223372036854775807)')};
		return val;
	},
	coerceToString_: function(value) {
		var fmt = this._format;
		return value != null ? typeof value == 'string' ? value : 
			fmt ? zk.fmt.Number.format(fmt, value.$toString(), this._rounding, this._localizedSymbols)
				 : value.$toLocaleString() : '';
	},
	_isOutRange: function(val) {
		var negative = val.charAt(0) == '-';
		if (negative)
			val = val.substring(1);
		if (val.length > 19)
			return true;
		if (val.length < 19)
			return false;
		var maxval = negative ? '9223372036854775808' : '9223372036854775807';
		for(var j=0; j < 19; ++j) {
			if (val.charAt(j) > maxval.charAt(j))
				return true;
			if (val.charAt(j) < maxval.charAt(j))
				return false;
		}
		return false;
	},
	marshall_: function(val) {
		return val ? val.$toString() : val;
	},
	unmarshall_: function(val) {
		return val ? new zk.Long(val) : val;
	}
});