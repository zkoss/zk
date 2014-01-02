/* Dooublespinner.js

	Purpose:
		
	Description:
		
	History:
		Mon Dec 20 10:17:24     2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _digitsAfterDecimal(v, DECIMAL) {
		var vs = '' + v,
			i = vs.indexOf(DECIMAL);
		return i < 0 ? 0 : vs.length - i - 1;
	}
	function _shiftedSum(v1, v2, exp, asc) {
		var mul;
		if (exp) {
			mul = Math.pow(10, exp);
			v1 *= mul;
			v2 *= mul;
			v1 = Math.round(v1);
			v2 = Math.round(v2);
		}
		var res = asc ? v1 + v2 : v1 - v2;
		if (exp)
			res /= mul;
		return res;
	}
	function _updateFixedDigits(wgt, val) {
		var decimal = wgt._localizedSymbols ? wgt._localizedSymbols.DECIMAL : zk.DECIMAL,
				stepd = _digitsAfterDecimal(wgt._step, decimal),
				vald = _digitsAfterDecimal(val || wgt._value, decimal);
		wgt._fixedDigits = Math.max(stepd, vald);
	}
	
/**
 * An edit box for holding a constrained double.
 *
 * <p>Default {@link #getZclass}: z-doublespinner.
 * @since 5.0.6
 */
zul.inp.Doublespinner = zk.$extends(zul.inp.NumberInputWidget, {
	_step: 1,
	_buttonVisible: true,
	$define: {
		/** Return the step of double spinner
		 * @return double
		 */
		/** Set the step of dobule spinner
		 * @param double step
		 */
		step: function (v) {
			_updateFixedDigits(this);
		},
		/** Returns whether the button (on the right of the textbox) is visible.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether the button (on the right of the textbox) is visible.
		 * @param boolean visible
	 	 */
		buttonVisible: function (v) {
			zul.inp.RoundUtl.buttonVisible(this, v);
		}
	},
	inRoundedMold: function () {
		return true;
	},
	/** Returns the value in double. If null, zero is returned.
	 * @return double
	 */
	doubleValue: function (){
		return this.$supers('getValue', arguments);
	},
	setConstraint: function (constr){
		if (typeof constr == 'string' && constr.charAt(0) != '['/*by server*/) {
			var constraint = new zul.inp.SimpleDoubleSpinnerConstraint(constr);
			this._min = constraint._min;
			this._max = constraint._max;
			this.$supers('setConstraint', [constraint]);
		} else
			this.$supers('setConstraint', arguments);
	},
	coerceFromString_: function (value) {//copy from doublebox
		// B50-3322816
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
    
    		if (isNaN(val) || (raw != valstr && raw != '-'+valstr && raw.indexOf('e') < 0)) { //1e2: assumes OK
    			if (!isNaN(val) && raw != valstr) //Bug ZK-1218: show Illegal value instead if input is number but too long
					return {error: zk.fmt.Text.format(msgzul.ILLEGAL_VALUE)};
    			return {error: zk.fmt.Text.format(msgzul.NUMBER_REQUIRED, value)};
    		}
    	}
    
    	if (info.divscale) val = val / Math.pow(10, info.divscale);
		
		// B50-3322795
    	_updateFixedDigits(this, val);
    	return val;
	},
	coerceToString_: function (value) {//copy from intbox
		var fmt = this._format,
			DECIMAL = this._localizedSymbols ? this._localizedSymbols.DECIMAL : zk.DECIMAL;
		
		// ZK-2084: fix display for different step
		if (typeof value === 'number' && value % 1 == 0) { // is integer
			var precision = 1;
			if (this._step && (decimal = (this._step + '').split('.')[1])) {
				precision = decimal.length;
			}
			value = parseFloat(value).toFixed(precision);
		}
			
		return value == null ? '' : fmt ? 
			zk.fmt.Number.format(fmt, parseFloat(value), this._rounding, this._localizedSymbols) : 
				DECIMAL == '.' ? (''+value) : (''+value).replace('.', DECIMAL);
	},
	onSize: function () {
		zul.inp.RoundUtl.onSize(this);
	},

	onHide: zul.inp.Textbox.onHide,
	validate: zul.inp.Doublebox.validate,
	doKeyDown_: function(evt){
		var inp = this.getInputNode();
		if (inp.disabled || inp.readOnly)
			return;
	
		switch (evt.keyCode) {
		case 38://up
			this.checkValue();
			this._increase(true);
			evt.stop();
			return;
		case 40://down
			this.checkValue();
			this._increase(false);
			evt.stop();
			return;
		}
		this.$supers('doKeyDown_', arguments);
	},
	_ondropbtnup: function (evt) {
		this.domUnlisten_(document.body, 'onZMouseup', '_ondropbtnup');
		this._stopAutoIncProc();
		this._currentbtn = null;
	},
	_btnDown: function(evt){
		if (!this._buttonVisible || this._disabled) return;

		var btn = this.$n('btn');
		
		if (!zk.dragging) {
			if (this._currentbtn) // just in case
				this._ondropbtnup(evt);
			
			this.domListen_(document.body, 'onZMouseup', '_ondropbtnup');
			this._currentbtn = btn;
		}
		
		this.checkValue();
		
		var ofs = zk(btn).revisedOffset(),
			isOverUpBtn = (evt.pageY - ofs[1]) < btn.offsetHeight/2;
		
		if (isOverUpBtn) { //up
			this._increase(true);
			this._startAutoIncProc(true);
		} else {	// down
			this._increase(false);
			this._startAutoIncProc(false);
		}
		
		// disable browser's text selection
		evt.stop();
	},
	/**
	 * Sets bound value if the value out of range 
	 */
	checkValue: function(){
		var inp = this.getInputNode(),
			min = this._min,
			max = this._max;

		if(!inp.value) {
			if(min && max)
				inp.value = (min<=0 && 0<=max) ? 0: min;
			else if (min)
				inp.value = min<=0 ? 0: min;
			else if (max)
				inp.value = 0<=max ? 0: max;
			else
				inp.value = 0;
		}
	},
	_btnUp: function(evt) {
		if (!this._buttonVisible || this._disabled || zk.dragging) return;

		this._onChanging();
		this._stopAutoIncProc();

		var inp = this.getInputNode();
		if (zk.ie < 11) {
			var len = inp.value.length;
			zk(inp).setSelectionRange(len, len);
		}
		inp.focus();
	},
	_increase: function (asc){
		var inp = this.getInputNode(),
			value = this.coerceFromString_(inp.value);
		
		if (value && value.error)
			return; //nothing to do if error happens
		
		var	shiftLen = Math.max(_digitsAfterDecimal(value), this._fixedDigits),
			result = _shiftedSum(value, this._step, shiftLen, asc); // B50-3301517
		
		// control overflow
		if ( result > Math.pow(2,63)-1 )	result = Math.pow(2,63)-1;
		else if ( result < -Math.pow(2,63) ) result = -Math.pow(2,63);

		//over bound shall restore value
		if (this._max!=null && result > this._max) result = value;
		else if (this._min!=null && result < this._min) result = value;

		inp.value = this.coerceToString_(result);
		
		this._onChanging();
		
	},
	_clearValue: function(){
		this.getInputNode().value = this._defRawVal = '';
		return true;
	},
	_startAutoIncProc: function (isup){
		var widget = this;
		if(this.timerId)
			clearInterval(this.timerId);

		this.timerId = setInterval(function(){widget._increase(isup)}, 200);
	},
	_stopAutoIncProc: function (){
		if(this.timerId)
			clearTimeout(this.timerId);

		this.timerId = null;
	},
	/** Synchronizes the input element's width of this component
	 */
	syncWidth: function () {
		zul.inp.RoundUtl.syncWidth(this, this.$n('btn'));
	},
	doFocus_: function (evt) {
		this.$supers('doFocus_', arguments);

		zul.inp.RoundUtl.doFocus_(this);
	},
	doBlur_: function (evt) {
		this.$supers('doBlur_', arguments);
		zul.inp.RoundUtl.doBlur_(this);
	},
	afterKeyDown_: function (evt,simulated) {
		if(!simulated && this._inplace)
			jq(this.$n()).toggleClass(this.getInplaceCSS(),  evt.keyCode == 13 ? null : false);
			
		return this.$supers('afterKeyDown_', arguments);
	},
	getAllowedKeys_: function () {
		var symbols = this._localizedSymbols;
		return this.$supers('getAllowedKeys_', arguments)
			+ (symbols ? symbols: zk).DECIMAL + 'e';
		//supports scientific expression such as 1e2
	},
	bind_: function () {//after compose
		this.$supers(zul.inp.Doublespinner, 'bind_', arguments);

		var btn;
		if(btn = this.$n('btn'))
			this.domListen_(btn, 'onZMouseDown', '_btnDown')
				.domListen_(btn, 'onZMouseUp', '_btnUp');

		zWatch.listen({onSize: this});
	},
	unbind_: function () {
		if(this.timerId){
			clearTimeout(this.timerId);
			this.timerId = null;
		}
		zWatch.unlisten({onSize: this});
		var btn = this.$n('btn');
		if(btn)
			this.domUnlisten_(btn, 'onZMouseDown', '_btnDown')
				.domUnlisten_(btn, 'onZMouseUp', '_btnUp');

		this.$supers(zul.inp.Doublespinner, 'unbind_', arguments);
	},
	getBtnUpIconClass_: function () {
		return 'z-icon-angle-up';
	},
	getBtnDownIconClass_: function () {
		return 'z-icon-angle-down';
	}
});

})();