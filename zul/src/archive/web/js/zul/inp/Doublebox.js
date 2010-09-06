/* Doublebox.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 13:35:32     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _allowKeys;
	
	// Fixed merging JS issue
	zk.afterLoad('zul.lang', function () {
		_allowKeys = zul.inp.InputWidget._allowKeys+zk.DECIMAL+'e';
	});
		//supports 1e2
/**
 * An edit box for holding an float point value (double).
 * <p>Default {@link #getZclass}: z-doublebox.
 */
zul.inp.Doublebox = zk.$extends(zul.inp.FormatWidget, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zk.fmt.Number.unformat(this._format, value),
			raw = info.raw,
			val = parseFloat(raw),
			valstr = ''+val,
			valind = valstr.indexOf('.'),
			rawind = raw.indexOf('.');

		if (isNaN(val) || valstr.indexOf('e') < 0) {
			if (rawind == 0) {
				raw = '0' + raw;
				++rawind;
			}

			if (rawind >= 0 && raw.substring(raw.substring(rawind+1)) && valind < 0) { 
				valind = valstr.length;
				valstr += '.';
			}

			var len = raw.length,	
				vallen = valstr.length;
		
			//pre zeros
			if (valind >=0 && valind < rawind) {
				vallen -= valind;
				len -= rawind;
				for(var zerolen = rawind - valind; zerolen-- > 0;)
					valstr = '0' + valstr;
			}

			//post zeros
			if (vallen < len) {
				for(var zerolen = len - vallen; zerolen-- > 0;)
					valstr += '0';
			}

			if (isNaN(val) || (raw != valstr && raw != '-'+valstr && raw.indexOf('e') < 0)) //1e2: assumes OK
				return {error: zk.fmt.Text.format(msgzul.NUMBER_REQUIRED, value)};
		}

		if (info.divscale) val = val / Math.pow(10, info.divscale);
		return val;
	},
	_allzero: function(val) {
		for(var len= val.length; len-- > 0; )
			if (val.charAt(len) != '0') return false;
		return true;
	},
	coerceToString_: function(value) {
		var fmt = this._format;
		return value == null ? '' : fmt ? 
			zk.fmt.Number.format(fmt, value, this._rounding) : 
			zk.DECIMAL == '.' ? (''+value) : (''+value).replace('.', zk.DECIMAL);
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-doublebox";
	},
	doKeyPress_: function(evt){
		if (!this._shallIgnore(evt, _allowKeys))
			this.$supers('doKeyPress_', arguments);
	}
});

})();
