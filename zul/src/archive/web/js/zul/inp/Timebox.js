/* inp.zul

	Purpose:
		testing textbox.intbox.spinner,timebox,doublebox,longbox and decimalbox on zk5
	Description:
		
	History:
		Thu June 11 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

zul.inp.Timebox = zk.$extends(zul.inp.FormatWidget, {
	POS_MIN:'1',
	POS_HOUR:'2',
	_value: "00:00",
	_step: 1,
	_buttonVisible: true,
	$define: {
		buttonVisible: function(v){
			this.btn.style.display = v == 'true'? '': 'none';
			this.onSize();
			return;
		},
		value: function(v){
			if(this.desktop){
				if(v)
					this._setTime(v.toString());
				else
					this._clearTime();
				var real = this.inp;
				if(real.defaultValue != real.value)
					real.defaultValue = real.value;
			}
		}
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-timebox";
	},
	isButtonVisible: function(){
		return _buttonVisible;
	},
	getInputNode_: function(){
		return this.getSubnode("real");
	},
	coerceFromString_: function (value) {
		if (!value) return null;

		if((typeof value) == 'string' && value.length == 5 && value.indexOf(':') == 2){
			var hrmin = value.split(":"),
				hr = parseInt(hrmin[0]),
				min = parseInt(hrmin[1]);
			if(!isNaN(hr) && !isNaN(min)){
				return value;
			}
		}
		return {error: zMsgFormat.format(msgzul.DATE_REQUIRED + "HH:mm ,rather than: " + value)};
	},
	onSize: _zkf = function () { //from zul.fixDropBtn2
		var btn = this.btn;
		//note: isRealVisible handles null argument
		if (zk(btn).isRealVisible() && btn.style.position != "relative") {
			var inp = this.inp, img = btn.firstChild;
			if (!inp.offsetHeight || !img.offsetHeight) {
				setTimeout(function () {this.onSize()}, 66);
				return;
			}
		
			//Bug 1738241: don't use align="xxx"
			var v = inp.offsetHeight - img.offsetHeight;
			if (v !== 0) {
				var imghgh = zk.parseInt(jq(img).css("height")) + v;
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
	doFocus_: _zkf = function(evt){
		this.$supers('doFocus_', arguments);
		sels = this._selrange();
		this.lastPos = sels[0];
	},
	doClick_: function(evt){
		this.$supers('doClick_', arguments);
		sels = this._selrange();
		this.lastPos = sels[0];
	},
	doBlur_: function(evt){
		this.$supers('doBlur_', arguments);
		if(!this._check(this.inp.value)){
			this.setTime();
		}
	},
	doKeyDown_: function(evt){
		var inp = this.inp,
			sels = this._selrange();
		if (inp.disabled || inp.readOnly)
			return;
	
		this.lastPos = sels[0];
		var code = evt.keyCode;
		switch(code){
		case 48:case 96://0
		case 49:case 97://1
		case 50:case 98://2
		case 51:case 99://3
		case 52:case 100://4
		case 53:case 101://5
		case 54:case 102://6
		case 55:case 103://7
		case 56:case 104://8
		case 57:case 105://9
			code = code - (code>=96?96:48);
			this._setTimeDigit(code);
			evt.stop();
			return;
		case 37://left
			break;
		case 38://up
			this.onUp();
			evt.stop();
			return;
		case 39://right
			break;
		case 40://down
			this.onDown();
			evt.stop();
			return;
		case 46://del
			this.clearTime();
			evt.stop();
			return;
		case 9:
			//zkTxbox.onupdate(inp);
			break
		case 13: case 27: case 35:case 36://enter,esc,tab,home,end
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
			&& !evt.ctrlKey && !evt.altKey)
				evt.stop();
		}
		this.$supers('doKeyDown_', arguments);
	},
	ondropbtnup: function (evt) {
		jq(this._currentbtn).removeClass(this.getZclass() + "-btn-clk");
		this.domUnlisten_(document.body, "mouseup", "ondropbtnup");
		this._currentbtn = null;
	},
	_btnDown: function(evt){	
		
		if (this.inp && !this.inp.disabled) {
			if (this._currentbtn)
				this.ondropbtnup(evt);
			jq(btn).addClass(this.getZclass() + "-btn-clk");
			this.domListen_(document.body, "mouseup", "ondropbtnup");
			this._currentbtn = btn;
		}
		
		var inp = this.inp,
			btn = this.btn;
		if(inp.disabled) return;
		
		var btn = zk.opera || zk.safari ? btn : btn.firstChild,
			ofs = zk(btn).revisedOffset();
		if ((evt.pageY - ofs[1]) < btn.offsetHeight / 2) { //up
			this.onUp();
			this._startAutoIncProc(true);
		} else {
			this.onDown();
			this._startAutoIncProc(false);
		}
		
	},
	_btnUp: function(evt){
		var inp = this.inp;
		if(inp.disabled || zk.dragging) return;

		this._onChanging();
		
		this._stopAutoIncProc();
		this._markPositionSel();
		inp.focus();
	},
	_btnOut: function(evt){
		if (this.inp && !this.inp.disabled && !zk.dragging)
			jq(this.btn).removeClass(this.getZclass()+"-btn-over");
			
		var inp = this.inp;
		if(inp.disabled || zk.dragging) return;

		this._stopAutoIncProc();
	},
	_btnOver: function(evt){
		if (this.inp && !this.inp.disabled && !zk.dragging)
			jq(this.btn).addClass(this.getZclass()+"-btn-over");
	},
	_selrange: function (){
			var sel = zk(this.inp).getSelectionRange();
			if(sel[0]>sel[1]){
				var t = sel[1];
				sel[1] = sel[0];
				sel[0] = t;
			}
			return sel;
	},
	_calInc: function (cmp){
		var pos = this._checkPosition();
		switch(pos){
		case this.POS_MIN:
			return 60;
		case this.POS_HOUR:
			return 3600;
		default:
			return 0;
		}
	},
	onUp: function(cmp) {
		this._increaseTime(this._calInc())
		this._onChanging();
		this._markPositionSel();
	},
	onDown: function(cmp) {
		this._increaseTime(-this._calInc())
		this._onChanging();
		this._markPositionSel();
	},
	_clearValue: function(){
		var real = this.inp;
		real.value = real.defaultValue = "";
		return true;
	},
	_checkPosition: function() {
		return this.lastPos <= 2 ? this.POS_HOUR : this.POS_MIN;
	},
	_markPositionSel: function() {
		var pos = this._checkPosition();
		switch (pos) {
		case this.POS_HOUR:
			this._markselection(0, 2);
			break;
		case this.POS_MIN:
			this._markselection(3, 5);
			break;
		}
	},
	_check: function(timestr) {
		if (!timestr) {
			return false;
		}
		var ta = timestr.split(':');
		if (ta.length == 2) {
			var hour = zk.parseInt(ta[0]),
				min = zk.parseInt(ta[1]),
				hiterr = false;
			if (isNaN(hour) || hour > 23 || hour < 0) {
				return false;
			}
			if (isNaN(min) || min > 59 || min < 0) {
				return false;
			}
			return true;
		}
		return false;
	},
	_formatFixed: function (val, digits) {
		var s = "" + val;
		for (var j = digits - s.length; j--;)
			s = "0" + s;
		return s;
	},
	_increaseTime: function(inc_sec) {
		var t = this.lastTime.getTime();
		t = t + 1000 * inc_sec * this.currentStep;
		var date = new Date();
		date.setTime(t);
		var hour = date.getHours(),
			min = date.getMinutes(),
			newtimestr = this._formatFixed(hour, 2) + ":" + this._formatFixed(min, 2);
	
		this.setTime(newtimestr);
	},
	_setTimeDigit: function(n) {
		var sel = this._selrange(),
			hour = this.lastTime.getHours(),
			min = this.lastTime.getMinutes(),
			newpos = 0;
		switch (sel[0]) {
		case 0:
			if (n > 2) {
				hour = n;
				newpos = 3;
			} else {
				hour = hour % 10 + n * 10;
				newpos = 1;
			}
			if (hour > 23)
				return;

			break;
		case 1:
			hour = hour - hour % 10 + n;
			if (hour > 23)
				return;
			newpos = 3;
			break;
		case 3:
			if (n > 5) {
				min = n;
			} else {
				min = min % 10 + n * 10;
			}
			if (min > 59)
				return;
			newpos = 4;
			break;
		case 4:
			min = min - min % 10 + n;
			if (min > 59)
				return;
			newpos = 4;
			break;
		default:
			return;
		}
		var newtimestr = this._formatFixed(hour, 2) + ":" + this._formatFixed(min, 2);
		this.setTime(newtimestr);
		this._markselection(newpos, newpos);
	},
	_setTime: function(timestr) {
		if (this.lastTimeStr == timestr || !this._check(timestr)) {
			return false;
		}
		var inp = this.inp,
			ta = timestr.split(':'),
			hour = zk.parseInt(ta[0]),
			min = zk.parseInt(ta[1]),
			newtimestr = this._formatFixed(hour, 2) + ":" + this._formatFixed(min, 2);
	
		this.lastTime.setHours(hour);
		this.lastTime.setMinutes(min)
	
		inp.value = newtimestr;
		this.lastTimeStr = newtimestr;
	
		return true;
	},
	setTime: function(timestr) {
		if (this._setTime(timestr)) {
			//mark changed, and fire changing event to server
			this.changed = true;
			//InputElement already handle onChange,onChanging if you inherit for InputElement and name your
			//input to uuid!real.
		}
	},
	_clearTime: function() {
		if (!this.lastTimeStr) {
			return false;
		}
		this.lastTimeStr = "";
		this.lastTime.setHours(0);
		this.lastTime.setMinutes(0);
		this.inp.value = "";
		return true;
	},
	clearTime: function() {
		if (this._clearTime()) {
			this.changed = true;
			//fire changing event to server
		}
	},
	_autoIncTimeout: function(inc_sec) {
		this._increaseTime(inc_sec);
		if (this.timerId) {
			//increase Step value
			if (this.runCount != 0 && (this.runCount % 10) == 0) {
				this.currentStep = this.currentStep + 1;
			}
			this.runCount = this.runCount + 1;
			this._onChanging();
		}
	},
	_startAutoIncProc: function(isup) {
		if (this.timerId) {
			clearInterval(this.timerId);
		}
	
		var inc_sec = this._calInc();
		var widget = this;
		if (!isup)
			inc_sec = -inc_sec
		this.timerId = setInterval(function() {
			widget._autoIncTimeout(inc_sec)
		}, 300);
	},
	_stopAutoIncProc: function() {
		if (this.timerId) {
			clearTimeout(this.timerId);
		}
		this.currentStep = this.defaultStep;
		this.runCount = 0;
		this.timerId = null;
	},
	_markselection: function(start, end) {
		var inp = this.inp;
		if (inp.setSelectionRange) {
			inp.setSelectionRange(start, end);
		} else if (inp.createTextRange) {
			var range = inp.createTextRange();
			if (start != end) {
				range.moveEnd('character', end - range.text.length);
				range.moveStart('character', start);
			} else {
				range.move('character', start);
			}
			range.select();
		}
	},
	bind_: function () {//after compose
		this.$supers('bind_', arguments);
		this.lastTime = new Date();
		this.lastTime.setHours(0);
		this.lastTime.setMinutes(0);
		this.lastTimeStr = "";
		this.changed = false;
	
		this.currentStep = 1;
		this.defaultStep = 1;
		this.lastPos = 0;
		this.runCount = 0;
		this.timerId = null;
		
		var inp = this.inp = this.getSubnode("real");
		var btn = this.btn = this.getSubnode("btn");
		
		zWatch.listen({onSize: this, onShow: this});			
		if(btn)
			this.domListen_(btn, "onmousedown", "_btnDown")
				.domListen_(btn, "onmouseup", "_btnUp")
				.domListen_(btn, "onmouseout", "_btnOut")
				.domListen_(btn, "mouseover", "_btnOver");
		if(inp.value)
			this._setTime(inp.value);
		else
			this._clearTime()
			
		this.onSize();	
	},
	unbind_: function () {
		if(this.timerId){
			clearTimeout(this.timerId);
			this.timerId = null;
		}
		zWatch.unlisten({onSize: this, onShow: this});
		var btn = this._btn;
		if(btn)
			this.domUnlisten_(btn, "onmousedown", "_btnDown")
				.domUnlisten_(btn, "onmouseup", "_btnUp")
				.domUnlisten_(btn, "onmouseout", "_btnOut")
				.domUnlisten_(btn, "mouseover", "_btnOver");
		//zkTxbox.cleanup(cmp);
		this.$supers('unbind_', arguments);
	}
	
});