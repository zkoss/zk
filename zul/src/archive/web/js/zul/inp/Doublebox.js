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
	zk.load('zul.lang', function () {
		_allowKeys = zul.inp.NumberInputWidget._allowKeys+zk.DECIMAL+'e';
	});
		//supports 1e2
/**
 * An edit box for holding an float point value (double).
 * <p>Default {@link #getZclass}: z-doublebox.
 */
zul.inp.Doublebox = zk.$extends(zul.inp.NumberInputWidget, {
	$init: function () {
		this.$supers('$init', arguments);
		//bug B50-3325041
		this._allowKeys = _allowKeys;
	},
	setLocalizedSymbols: function (val) {
		var old = this._localizedSymbols;
		this.$supers('setLocalizedSymbols', arguments);
		if (this._localizedSymbols !== old)
			this._allowKeys += this._localizedSymbols.DECIMAL + 'e';
	},
	onSize: _zkf = function() {
		var width = this.getWidth();
		if (!width || width.indexOf('%') != -1)
			this.getInputNode().style.width = '';
		this.syncWidth();
	},
	onShow: _zkf,
	/** Synchronizes the input element's width of this component
	 */
	syncWidth: function () {
		zul.inp.RoundUtl.syncWidth(this, this.$n('right-edge'));
	},
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zk.fmt.Number.unformat(this._format, value, false, this._localizedSymbols),
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
		var fmt = this._format,
			DECIMAL = this._localizedSymbols ? this._localizedSymbols.DECIMAL : zk.DECIMAL;
		return value == null ? '' : fmt ? 
			zk.fmt.Number.format(fmt, value, this._rounding, this._localizedSymbols) : 
			DECIMAL == '.' ? (''+value) : (''+value).replace('.', DECIMAL);
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-doublebox" + (this.inRoundedMold() ? "-rounded": "");
	},
	bind_: function(){
		this.$supers(zul.inp.Doublebox, 'bind_', arguments);
		if (this.inRoundedMold())
			zWatch.listen({onSize: this, onShow: this});
	},	
	unbind_: function(){
		if (this.inRoundedMold())
			zWatch.unlisten({onSize: this, onShow: this});
		this.$supers(zul.inp.Doublebox, 'unbind_', arguments);
	}
});

})();
