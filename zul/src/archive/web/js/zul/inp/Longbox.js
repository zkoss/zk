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
zul.inp.Longbox = zk.$extends(zul.inp.FormatWidget, {
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
	//bug #2997037, cannot enter large long integer into longbox
	coerceFromString_: function (value) {
		if (!value) return null;
	
		var info = zk.fmt.Number.unformat(this._format, value),
			val = new zk.Long(info.raw),
			sval = val.$toString();
		if (info.raw != sval && info.raw != '-'+sval) //1e2 not supported (unlike Doublebox)
			return {error: zk.fmt.Text.format(msgzul.INTEGER_REQUIRED, value)};
		if (info.divscale) val.setPrecision(val.getPrecision() + info.divscale);
		if (this._isOutRange(val.$toString()))
			return {error: zk.fmt.Text.format(msgzul.OUT_OF_RANGE+'(−9223372036854775808 - 9223372036854775807)')};
		return val;
	},
	coerceToString_: function(value) {
		var fmt = this._format;
		return value != null ? typeof value == 'string' ? value : 
			fmt ? zk.fmt.Number.format(fmt, value.$toString(), this._rounding) : value.$toLocaleString() : '';
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-longbox" + (this.inRoundedMold() ? "-rounded": "");
	},
	doKeyPress_: function(evt){
		if (!this._shallIgnore(evt, zul.inp.InputWidget._allowKeys))
			this.$supers('doKeyPress_', arguments);
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
	},	
	bind_: function(){
		this.$supers(zul.inp.Longbox, 'bind_', arguments);
		if (this.inRoundedMold())
			zWatch.listen({onSize: this, onShow: this});
	},	
	unbind_: function(){
		if (this.inRoundedMold())
			zWatch.unlisten({onSize: this, onShow: this});
		this.$supers(zul.inp.Longbox, 'unbind_', arguments);
	}
});