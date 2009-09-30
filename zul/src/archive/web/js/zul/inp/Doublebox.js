/* Doublebox.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 13:35:32     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Doublebox = zk.$extends(zul.inp.FormatWidget, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = parseFloat(info.raw),
			len = info.raw.length;
			rawcc = info.raw.substring(len-1, len);
		if (info.raw != ''+val+(rawcc == '.' ? rawcc : '') && info.raw != '+'+val && info.raw.indexOf('e') < 0) //unable to handle 1e2
			return {error: zMsgFormat.format(msgzul.NUMBER_REQUIRED, value)};

		if (info.divscale) val = val / Math.pow(10, info.divscale);
		return val;
	},
	coerceToString_: function(value) {
		var fmt = this._format;
		return value != null ? fmt ? zNumFormat.format(fmt, value) : '' + value : '';
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-doublebox";
	},
	doKeyPress_: function(evt){
		if (!this._shallIgnore(evt, zul.inp.Doublebox._allowKeys))
			this.$supers('doKeyPress_', arguments);
	}
},{
	_allowKeys: zul.inp.InputWidget._allowKeys+zk.DECIMAL+zk.PERCENT+zk.GROUPING
});