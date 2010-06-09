/* Decimalbox.js

	Purpose:
		
	Description:
		
	History:
		Fri June 11 13:35:32     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _allowKeys = zul.inp.InputWidget._allowKeys+zk.DECIMAL+zk.PERCENT+zk.GROUPING;

/**
 * An edit box for holding BigDecimal.
 * <p>Default {@link #getZclass}: z-decimalbox.
 */
zul.inp.Decimalbox = zk.$extends(zul.inp.FormatWidget, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zk.fmt.Number.unformat(this._format, value),
			val = new zk.BigDecimal(info.raw);
		if (info.raw != val.$toString() && info.raw != '-'+val) //1e2 not supported (unlike Doublebox)
			return {error: zk.fmt.Text.format(msgzul.NUMBER_REQUIRED, value)};
		if (info.divscale) val.setPrecision(val.getPrecision() + info.divscale);
		return val;
	},
	coerceToString_: function(value) {
		var fmt = this._format;
		return value != null ? typeof value == 'string' ? value : 
			fmt ? zk.fmt.Number.format(fmt, value.$toString(), this._rounding) : value.$toLocaleString() : '';
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-decimalbox";
	},
	doKeyPress_: function(evt){
		if (!this._shallIgnore(evt, _allowKeys))
			this.$supers('doKeyPress_', arguments);
	}
});

})();
