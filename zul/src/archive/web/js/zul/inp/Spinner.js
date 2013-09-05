/* Spinner.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An edit box for holding a constrained integer.
 *
 * <p>Default {@link #getZclass}: z-spinner.
 */
zul.inp.Spinner = zk.$extends(zul.inp.NumberInputWidget, {
	_step: 1,
	_buttonVisible: true,
	$define: {
		/** Return the step of spinner
		 * @return int
		 */
		/** Set the step of spinner
		 * @param int step
		 */
		step: _zkf = function(){},
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
	/** Returns the value in int. If null, zero is returned.
	 * @return int
	 */
	intValue: function (){
		return this.$supers('getValue', arguments);
	},
	setConstraint: function (constr){
		if (typeof constr == 'string' && constr.charAt(0) != '['/*by server*/) {
			var constraint = new zul.inp.SimpleSpinnerConstraint(constr);
			this._min = constraint._min;
			this._max = constraint._max;
			this.$supers('setConstraint', [constraint]);
		} else
			this.$supers('setConstraint', arguments);
	},
	coerceFromString_: function (value) {//copy from intbox
		if (!value) return null;

		var info = zk.fmt.Number.unformat(this._format, value, false, this._localizedSymbols),
			val = parseInt(info.raw, 10);
		if (isNaN(val) || (info.raw != ''+val && info.raw != '-'+val))
			return {error: zk.fmt.Text.format(msgzul.INTEGER_REQUIRED, value)};
		if (val > 2147483647 || val < -2147483648)
			return {error: zk.fmt.Text.format(msgzul.OUT_OF_RANGE+'(−2147483648 - 2147483647)')};
				
		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	},
	coerceToString_: function (value) {//copy from intbox
		var fmt = this._format;
		return fmt ? zk.fmt.Number.format(fmt, value, this._rounding, this._localizedSymbols)
				: value != null ? ''+value: '';
	},
	onSize: function () {
		zul.inp.RoundUtl.onSize(this);
	},
	onHide: zul.inp.Textbox.onHide,
	validate: zul.inp.Intbox.validate,
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
		if (zk.ie) {
			var len = inp.value.length;
			zk(inp).setSelectionRange(len, len);
		}
		inp.focus();
	},
	_increase: function (is_add){
		var inp = this.getInputNode(),
			value = this.coerceFromString_(inp.value), //ZK-1851 convert input value using pattern
			result = is_add ? (value + this._step) : (value - this._step);
		
		// control overflow
		if (result > Math.pow(2,31)-1)
			result = Math.pow(2,31)-1;
		else if (result < -Math.pow(2,31))
			result = -Math.pow(2,31);
		
		//over bound shall restore value
		if (this._max!=null && result > this._max) result = value;
		else if (this._min!=null && result < this._min) result = value;

		inp.value = this.coerceToString_(result); //ZK-1851 convert result using pattern
		
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
	doFocus_: function (evt) {
		this.$supers('doFocus_', arguments);

		zul.inp.RoundUtl.doFocus_(this);
	},
	doBlur_: function (evt) {
		this.$supers('doBlur_', arguments);
		zul.inp.RoundUtl.doBlur_(this);
	},
	afterKeyDown_: function (evt,simulated) {
		if (!simulated && this._inplace)
			jq(this.$n()).toggleClass(this.getInplaceCSS(),  evt.keyCode == 13 ? null : false);
			
		return this.$supers('afterKeyDown_', arguments);
	},
	bind_: function () {//after compose
		this.$supers(zul.inp.Spinner, 'bind_', arguments); 
		
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

		this.$supers(zul.inp.Spinner, 'unbind_', arguments);
	},
	getBtnUpIconClass_: function () {
		return 'z-icon-angle-up';
	},
	getBtnDownIconClass_: function () {
		return 'z-icon-angle-down';
	}
});