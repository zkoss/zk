/* Spinner.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

zul.inp.Spinner = zk.$extends(zul.inp.FormatWidget, {
	_value: 0,
	_step: 1,
	_buttonVisible: true,
	$define: {
		step: _zkf = function(){},
		buttonVisible: function(v){
			this.btn.style.display = v == 'true'? '': 'none';
			this.onSize();
			return;
		},
		min: _zkf = function(v){this._min = parseInt(v);},
		max: _zkf
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-spinner";
	},
	isButtonVisible: function(){
		return _buttonVisible;
	},
	intValue: function (){
		return this.$supers('getValue', arguments);
	},
	setConstraint: function (constr){
		if(constr != null){
			var constraint = new zul.inp.SimpleSpinnerConstraint(constr);
			this._min = constraint._min;
			this._max = constraint._max;
			this.$supers('setConstraint', [constraint]);
		}else
			this.$supers('setConstraint', arguments);
	},
	getInputNode_: function(){
		return this.getSubnode("real");
	},
	coerceFromString_: function (value) {//copy from intbox
		if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = parseInt(info.raw);
		if (info.raw != ''+val)
			return {error: zMsgFormat.format(msgzul.INTEGER_REQUIRED, value)};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	},
	coerceToString_: function (value) {//copy from intbox
		var fmt = this._format;
		return fmt ? zNumFormat.format(fmt, value): value ? ''+value: '';
	},
	onSize: _zkf = function () { //from zul.fixDropBtn2
		var btn = this.btn;
		//note: isRealVisible handles null argument
		if (zDom.isRealVisible(btn) && btn.style.position != "relative") {
			var inp = this.inp, img = btn.firstChild;
			if (!inp.offsetHeight || !img.offsetHeight) {
				setTimeout("_zkf()", 66);
				return;
			}
			$int = function(v,b){
				v = v ? parseInt(v, b || 10): 0;
				return isNaN(v) ? 0: v;
			} 
			//Bug 1738241: don't use align="xxx"
			var v = inp.offsetHeight - img.offsetHeight;
			if (v !== 0) {
				var imghgh = $int(zDom.getStyle(img, "height")) + v;
				img.style.height = (imghgh < 0 ? 0 : imghgh) + "px";
			}

			v = inp.offsetTop - img.offsetTop;
			btn.style.position = "relative";
			btn.style.top = v + "px";
			if (zk.safari) btn.style.left = "-2px";
		}
	},
	onShow: _zkf,
	onHide: zul.inp.Textbox.onHide,
	validate: zul.inp.Intbox.validate,
	doKeyPress_: function(evt){
		this.ignoreKeys(evt.domEvent, "0123456789" + zk.MINUS);
		this.$supers('doKeyPress_', arguments);
	},
	doKeyDown_: function(evt){
		var inp = this.inp;
		if (inp.disabled || inp.readOnly)
			return;
	
		var code =zEvt.keyCode(evt);
		switch (code) {
			case 38://up
				this.checkValue();
				this._increase(true);
				zEvt.stop(evt.domEvent);
				break;
			case 40://down
				this.checkValue();
				this._increase(false);
				zEvt.stop(evt.domEvent);
				break;
		}
		this.$supers('doKeyDown_', arguments);
	},
	ondropbtnup: function (evt) {
		zDom.rmClass(this._currentbtn, this.getZclass() + "-btn-clk");
		this.domUnlisten_(document.body, "mouseup", "ondropbtnup");
		this._currentbtn = null;
	},
	_btnDown: function(evt){
		
		if (this.inp && !this.inp.disabled && !zk.dragging) {
			if (this._currentbtn)
				this.ondropbtnup(evt);
			zDom.addClass(btn, this.getZclass() + "-btn-clk");
			this.domListen_(document.body, "mouseup", "ondropbtnup");
			this._currentbtn = btn;
		}
		var inp = this.inp,
			btn = this.btn;
			
		if(inp.disabled) return;

		this.checkValue();

		btn = zk.opera || zk.safari ? btn : btn.firstChild,
		ofs = zDom.revisedOffset(btn);
		
		if ((zEvt.y(evt) - ofs[1]) < btn.offsetHeight / 2) { //up
			this._increase(true);
			this._startAutoIncProc(true);
		} else {	// down
			this._increase(false);
			this._startAutoIncProc(false);
		}
	},
	checkValue: function(){
		var inp = this.inp,
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
	_btnUp: function(evt){
		var inp = this.inp;
		if(inp.disabled) return;

		this._onChanging();
		
		this._stopAutoIncProc();
		inp.focus();
	},
	_btnOut: function(evt){
		if (this.inp && !this.inp.disabled && !zk.dragging)
			zDom.rmClass(this.btn, this.getZclass()+"-btn-over");
			
		var inp = this.inp;
		if(inp.disabled) return;

		this._stopAutoIncProc();
	},
	_btnOver: function(evt){
		if (this.inp && !this.inp.disabled && !zk.dragging)
			zDom.addClass(this.btn, this.getZclass()+"-btn-over");
	},
	_increase: function (is_add){
		var inp = this.inp,
			value = parseInt(inp.value);
		if (is_add)
			result = value + this._step;
		else
			result = value - this._step;

		// control overflow
		if ( result > Math.pow(2,31)-1 )	result = Math.pow(2,31)-1;
		else if ( result < -Math.pow(2,31) ) result = -Math.pow(2,31);

		if (this._max!=null && result > this._max) result = this._max;
		else if (this._min!=null && result < this._min) result = this._min;

		inp.value = result;
		
		this._onChanging();
		
	},
	_clearValue: function(){
		var real = this.inp;
		real.value = real.defaultValue = "";
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
	bind_: function () {//after compose
		this.$supers('bind_', arguments); 
		this.timeId = null;
		var inp = this.inp = this.getSubnode("real");
		var btn = this.btn = this.getSubnode("btn");
		zWatch.listen('onShow', this);
		zWatch.listen('onSize', this);
		if(btn){
			this.domListen_(btn, "onmousedown", "_btnDown");
			this.domListen_(btn, "onmouseup", "_btnUp");
			this.domListen_(btn, "onmouseout", "_btnOut");
			this.domListen_(btn, "mouseover", "_btnOver");
		}
		this.onSize();	
	},
	unbind_: function () {
		if(this.timerId){
			clearTimeout(this.timerId);
			this.timerId = null;
		}
		zWatch.unlisten('onShow', this);
		zWatch.unlisten('onSize', this);
		if(btn){
			this.domUnlisten_(btn, "onmousedown", "_btnDown");
			this.domUnlisten_(btn, "onmouseup", "_btnUp");
			this.domUnlisten_(btn, "onmouseout", "_btnOut");
			this.domUnlisten_(btn, "mouseover", "_btnOver");
		}
		this.$supers('unbind_', arguments);
	}
	
});