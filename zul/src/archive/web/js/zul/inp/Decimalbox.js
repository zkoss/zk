/* Decimalbox.js

	Purpose:
		
	Description:
		
	History:
		Fri June 11 13:35:32     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Decimalbox = zk.$extends(zul.inp.FormatWidget, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = new zk.BigDecimal(info.raw);
		if (info.raw != val.toString() && info.raw.indexOf('e') < 0) //unable to handle 1e2
			return {error: zMsgFormat.format(msgzul.NUMBER_REQUIRED, value)};
		if (info.divscale) val.setPrecision(val.getPrecision() + info.divscale);
		return val;
	},
	coerceToString_: function(value) {
		var fmt = this._format;
		return value != null ? fmt ? zNumFormat.format(fmt, value.toString()) : value.toLocaleString() : null;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-decimalbox";
	},
	doKeyPress_: function(evt){
		if (!this._shallIgnore(evt, zul.inp.Doublebox._allowKeys))
			this.$supers('doKeyPress_', arguments);
	}
});