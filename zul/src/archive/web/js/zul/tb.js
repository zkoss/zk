/* timebox.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Jul 9, 2007 10:03:38 AM , Created by Dennis Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.vd");
zkTmbox = {
	POS_MIN:'1',
	POS_HOUR:'2'
};

zkTmbox.init = function (cmp) {
	zkTmbox.onVisi = zkTmbox.onSize = zul.onFixDropBtn;
	zkTmbox.onHide = zkTxbox.onHide;

	zkTxbox.init($real(cmp));

	cmp.lastTime = new Date();
	cmp.lastTime.setHours(0);
	cmp.lastTime.setMinutes(0);
	cmp.lastTimeStr = "";
	cmp.changed = false;

	cmp.currentStep = 1;
	cmp.defaultStep = 1;
	cmp.lastPos = 0;
	cmp.runCount = 0;
	cmp.timerId = null;

	var inp = $real(cmp),
		btn = $e(cmp.id+"!btn");

	//event for inpu
	zk.listen(inp, "focus",zkTmbox._inpfocus);
	zk.listen(inp, "blur",zkTmbox._inpblur);
	zk.listen(inp, "click",zkTmbox._inpclick);
	zk.listen(inp, "keydown",zkTmbox._inpkeydown);

	//event for btn
	if(btn){
		zk.listen(btn, "mousedown", zkTmbox._btnDown);
		zk.listen(btn, "mouseup", zkTmbox._btnUp);
		zk.listen(btn, "click", zk.doEventStop); //prevent listitem being selected
		zk.listen(btn, "mouseout", zkTmbox._btnOut);
		zk.listen(btn, "mouseover", zul.ondropbtnover);
	}

	if(inp.value){
		zkTmbox._setTime(cmp,inp.value);
	}else{
		zkTmbox._clearTime(cmp);
	}
};
zkTmbox.cleanup = function (cmp) {
	if(cmp.timerId){
		clearTimeout(cmp.timerId);
		cmp.timerId = null;
	}
	zkTxbox.cleanup(cmp);
};
zkTmbox.setAttr = function (cmp, nm, val) {
	if ("value" == nm) {
		if (val) {
			zkTmbox._setTime(cmp, val);
		} else {
			zkTmbox._clearTime(cmp);
		}
		var real = $real(cmp); // Bug 1948963 (related to 1998417).
		if (real.defaultValue != real.value)
			real.defaultValue = real.value;
		return true;
	} else if ("z.btnVisi" == nm) {
		var btn = $e(cmp.id + "!btn");
		if (btn)
			btn.style.display = val == "true" ? "" : "none";
		zul.onFixDropBtn(cmp);
		return true;
	} else if ("style" == nm) {
		var inp = $real(cmp);
		if (inp)
			zkau.setAttr(inp, nm, zk.getTextStyle(val, true, true));
	} else if ("style.width" == nm) {
		var inp = $real(cmp);
		if (inp) {
			inp.style.width = val;
			return true;
		}
	} else if ("style.height" == nm) {
		var inp = $real(cmp);
		if (inp) {
			inp.style.height = val;
			return true;
		}
	}
	zkau.setAttr(cmp, nm, val);
	return true;
};

zkTmbox.rmAttr = function (cmp, nm) {
	if ("style" == nm) {
		var inp = $real(cmp);
		if (inp) zkau.rmAttr(inp, nm);
	} else if ("style.width" == nm) {
		var inp = $real(cmp);
		if (inp) inp.style.width = "";
	} else if ("style.height" == nm) {
		var inp = $real(cmp);
		if (inp) inp.style.height = "";
	}
	zkau.rmAttr(cmp, nm);
	return true;
};

//event implements
zkTmbox._inpfocus= function(evt){
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt)),
		sels = zkTmbox._selrange(cmp);
	cmp.lastPos = sels[0];
};
zkTmbox._inpblur= function(evt){
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt)),
		inp = $real(cmp);
	if(!zkTmbox._check(inp.value)){
		zkTmbox.setTime(cmp,cmp.lastTimeStr);
	}
};
zkTmbox._inpclick= function(evt){
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt)),
		sels = zkTmbox._selrange(cmp);
	cmp.lastPos = sels[0];
};
zkTmbox._inpkeydown= function(evt){
	if (!evt) evt = window.event;
	var inp = Event.element(evt),
		cmp = $outer(inp),
		sels = zkTmbox._selrange(cmp);
	if (inp.disabled || inp.readOnly)
		return;

	cmp.lastPos = sels[0];
	var code =Event.keyCode(evt);
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
		zkTmbox._setTimeDigit(cmp,code);
		Event.stop(evt);
		break;
	case 37://left
		break;
	case 38://up
		zkTmbox.onUp(cmp);
		Event.stop(evt);
		break;
	case 39://right
		break;
	case 40://down
		zkTmbox.onDown(cmp);
		Event.stop(evt);
		break;
	case 46://del
		zkTmbox.clearTime(cmp);
		Event.stop(evt);
		break;
	case 13: case 27: case 9: case 35:case 36://enter,esc,tab,home,end
		break;
	default:
		if (!(code >= 112 && code <= 123) //F1-F12
		&& !evt.ctrlKey && !evt.altKey)
			Event.stop(evt);
	}
};

zkTmbox._btnDown= function(evt){
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt)),
		inp = $real(cmp);
	if(inp.disabled || zk.dragging) return;

	var btn = zk.opera || zk.safari ? $e(cmp.id + "!btn") : zk.firstChild($e(cmp.id + "!btn"), "IMG"),
		ofs = zk.revisedOffset(btn);
	if ((Event.pointerY(evt) - ofs[1]) < btn.offsetHeight / 2) { //up
		zkTmbox.onUp(cmp);
		zkTmbox._startAutoIncProc(cmp, true);
	} else {
		zkTmbox.onDown(cmp);
		zkTmbox._startAutoIncProc(cmp,false);
	}
	zk.listen(btn, "mousedown", zul.ondropbtndown);
};
zkTmbox._btnUp= function(evt){
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt)),
		inp = $real(cmp);
	if(inp.disabled || zk.dragging) return;
	zkTxbox.sendOnChanging(inp);
	zkTmbox._stopAutoIncProc(cmp);
	zkTmbox._markPositionSel(cmp);
	inp.focus();
};
zkTmbox._btnOut= function(evt){
	if (!evt) evt = window.event;
	zul.ondropbtnout(evt);
	var cmp = $outer(Event.element(evt)),
		inp = $real(cmp);
	if(inp.disabled || zk.dragging) return;

	zkTmbox._stopAutoIncProc(cmp);
};

//inner method
//get selection range by order.
zkTmbox._selrange = function (cmp){
	var sel = zk.getSelectionRange($real(cmp));
	if(sel[0]>sel[1]){
		var t = sel[1];
		sel[1] = sel[0];
		sel[0] = t;
	}
	return sel;
};
//calculate how many seconds will be increased in current pos when up or down
zkTmbox._calInc = function (cmp){
	var pos = zkTmbox._checkPosition(cmp);
	switch(pos){
	case zkTmbox.POS_MIN:
		return 60;
		break;
	case zkTmbox.POS_HOUR:
		return 3600;
		break;
	default:
		return 0;
	}
};

zkTmbox.onUp = function(cmp) {
	zkTmbox._increaseTime(cmp, zkTmbox._calInc(cmp))
	zkTmbox._markPositionSel(cmp);
};
zkTmbox.onDown = function(cmp) {
	zkTmbox._increaseTime(cmp, -zkTmbox._calInc(cmp))
	zkTmbox._markPositionSel(cmp);
};

//check selection position on minute or hour.
zkTmbox._checkPosition = function(cmp) {
	return cmp.lastPos <= 2 ? zkTmbox.POS_HOUR : zkTmbox.POS_MIN;
};

zkTmbox._markPositionSel = function(cmp) {
	var pos = zkTmbox._checkPosition(cmp);
	switch (pos) {
		case zkTmbox.POS_HOUR:
			zkTmbox._markselection(cmp, 0, 2);
			break;
		case zkTmbox.POS_MIN:
			zkTmbox._markselection(cmp, 3, 5);
			break;
	}
};

zkTmbox._check = function(timestr) {
	if (!timestr) {
		return false;
	}
	var ta = timestr.split(':');
	if (ta.length == 2) {
		var hour = $int(ta[0]),
			min = $int(ta[1]),
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
};

zkTmbox._increaseTime = function(cmp, inc_sec) {
	var t = cmp.lastTime.getTime();
	t = t + 1000 * inc_sec * cmp.currentStep;
	var date = new Date();
	date.setTime(t);
	var hour = date.getHours(),
		min = date.getMinutes(),
		newtimestr = zk.formatFixed(hour, 2) + ":" + zk.formatFixed(min, 2);

	zkTmbox.setTime(cmp, newtimestr);
};

zkTmbox._setTimeDigit = function(cmp, n) {
	var sel = zkTmbox._selrange(cmp),
		hour = cmp.lastTime.getHours(),
		min = cmp.lastTime.getMinutes(),
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
	var newtimestr = zk.formatFixed(hour, 2) + ":" + zk.formatFixed(min, 2);
	zkTmbox.setTime(cmp, newtimestr);
	zkTmbox._markselection(cmp, newpos, newpos);
};

zkTmbox._setTime = function(cmp, timestr) {
	if (cmp.lastTimeStr == timestr || !zkTmbox._check(timestr)) {
		return false;
	}
	var inp = $real(cmp),
		ta = timestr.split(':'),
		hour = $int(ta[0]),
		min = $int(ta[1]),
		newtimestr = zk.formatFixed(hour, 2) + ":" + zk.formatFixed(min, 2);

	cmp.lastTime.setHours(hour);
	cmp.lastTime.setMinutes(min)

	inp.value = newtimestr;
	cmp.lastTimeStr = newtimestr;

	return true;
};
zkTmbox.setTime = function(cmp, timestr) {
	if (zkTmbox._setTime(cmp, timestr)) {
		//mark changed, and fire changing event to server
		cmp.changed = true;
		//InputElement already handle onChange,onChanging if you inherit for InputElement and name your
		//input to uuid!real.
	}
};
zkTmbox._clearTime = function(cmp) {
	if (!cmp.lastTimeStr) {
		return false;
	}
	cmp.lastTimeStr = "";
	cmp.lastTime.setHours(0);
	cmp.lastTime.setMinutes(0);
	$real(cmp).value = "";
	return true;
};
zkTmbox.clearTime = function(cmp) {
	if (zkTmbox._clearTime(cmp)) {
		cmp.changed = true;
		//fire changing event to server
	}
};
zkTmbox._autoIncTimeout = function(cmp, inc_sec) {
	zkTmbox._increaseTime(cmp, inc_sec);
	if (cmp.timerId) {
		//increase Step value
		if (cmp.runCount != 0 && (cmp.runCount % 10) == 0) {
			cmp.currentStep = cmp.currentStep + 1;
		}
		cmp.runCount = cmp.runCount + 1;
		zkTxbox.sendOnChanging($real(cmp));
	}
};
zkTmbox._startAutoIncProc = function(cmp, isup) {
	if (cmp.timerId) {
		clearInterval(cmp.timerId);
	}

	var inc_sec = zkTmbox._calInc(cmp);
	if (!isup)
		inc_sec = -inc_sec
	cmp.timerId = setInterval(function() {
		zkTmbox._autoIncTimeout(cmp, inc_sec)
	}, 500);
};
zkTmbox._stopAutoIncProc = function(cmp) {
	if (cmp.timerId) {
		clearTimeout(cmp.timerId);
	}
	cmp.currentStep = cmp.defaultStep;
	cmp.runCount = 0;
	cmp.timerId = null;
};

//copy from zkTxbox.setAttr 'sel' attribute
zkTmbox._markselection = function(cmp, start, end) {
	var inp = $real(cmp);
	if (inp.setSelectionRange) {
		inp.setSelectionRange(start, end);
		inp.focus();
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
};
