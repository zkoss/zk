/* Intbox.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 12:33:22     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Intbox = zk.$extends(zul.inp.FormatWidget, {
	intValue: function (){
		return this.$supers('getValue', arguments);
	},
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = parseInt(info.raw);
		
		if (info.raw != ''+val)
			return {error: zMsgFormat.format(msgzul.INTEGER_REQUIRED, value)};
		if (val > 2147483647 || val < -2147483648)
			return {error: zMsgFormat.format(msgzul.OUT_OF_RANGE+'(−2147483648 - 2147483647)')};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	},
	coerceToString_: function (value) {
		var fmt = this._format;
		return fmt ? zNumFormat.format(fmt, value): value ? ''+value: '';
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-intbox";
	},
	doKeyPress_: function(evt){
		this.ignoreKeys(evt.domEvent, "0123456789"+zk.MINUS);
	}
});