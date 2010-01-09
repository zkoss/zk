/* Longbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:43:22     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An edit box for holding an integer.
 * <p>Default {@link #getZclass}: z-longbox.
 */
zul.inp.Longbox = zk.$extends(zul.inp.Intbox, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zk.fmt.Number.unformat(this._format, value),
			val = parseInt(info.raw);
		
		if (info.raw != ''+val && info.raw != '-'+val)
			return {error: zk.fmt.Text.format(msgzul.INTEGER_REQUIRED, value)};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-longbox";
	}
});