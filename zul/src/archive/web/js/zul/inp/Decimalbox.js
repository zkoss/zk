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
	var _allowKeys;
    
	// Fixed merging JS issue
	zk.load('zul.lang', function () {
		_allowKeys = zul.inp.InputWidget._allowKeys+zk.DECIMAL;
	});
/**
 * An edit box for holding BigDecimal.
 * <p>Default {@link #getZclass}: z-decimalbox.
 */
zul.inp.Decimalbox = zk.$extends(zul.inp.FormatWidget, {
	$define: { //zk.def
		/** Returns the rounding mode.
		 * <ul>
		 * <li>0: ROUND_UP</li>
		 * <li>1: ROUND_DOWN</li>
		 * <li>2: ROUDN_CEILING</li>
		 * <li>3: ROUND_FLOOR</li>
		 * <li>4: ROUND_HALF_UP</li>
		 * <li>5: ROUND_HALF_DOWN</li>
		 * <li>6: ROUND_HALF_EVEN</li>
		 * </ul>
		 * @return int
		 */
		/** Sets the rounding mode.
		 * <ul>
		 * <li>0: ROUND_UP</li>
		 * <li>1: ROUND_DOWN</li>
		 * <li>2: ROUDN_CEILING</li>
		 * <li>3: ROUND_FLOOR</li>
		 * <li>4: ROUND_HALF_UP</li>
		 * <li>5: ROUND_HALF_DOWN</li>
		 * <li>6: ROUND_HALF_EVEN</li>
		 * </ul>
		 * @param int rounding mode
		 */
		rounding: null,
		/** Returns the precision scale.
		 * @return int
		 */
		/** Sets the precision scale.
		 * @param int scale
		 */
		scale: null
	},
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zk.fmt.Number.unformat(this._format, value),
			val = new zk.BigDecimal(info.raw),
			sval = val.$toString();
		if (info.raw != sval && info.raw != '-'+sval) //1e2 not supported (unlike Doublebox)
			return {error: zk.fmt.Text.format(msgzul.NUMBER_REQUIRED, value)};
		if (info.divscale) val.setPrecision(val.getPrecision() + info.divscale);
		if (this._scale > 0) //bug #3089502: setScale in decimalbox not working
			val = zk.fmt.Number.setScale(val, this._scale, this._rounding);
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
