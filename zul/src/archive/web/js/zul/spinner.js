/* spinner.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Wed Mar  5 10:06:07 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zk.load("zul.vd");
zkSpinner = {};

zkSpinner.init = function (cmp) {
	zkSpinner.onVisi = zkSpinner.onSize = zul.fixDropBtn2;
	zkSpinner.onHide = zkTxbox.onHide;
	zkSpinner.validate = zkInbox.validate;

	zkTxbox.init($real(cmp));

	cmp.runCount = 0;
	cmp.timerId = null;

	cmp.step = parseInt(getZKAttr(cmp, "step"));
	cmp.min = parseInt(getZKAttr(cmp, "min"));
	cmp.max = parseInt(getZKAttr(cmp, "max"));

	var inp = $real(cmp),
		btn = $e(cmp.id+"!btn");

	//event for inpu
	zk.listen(inp, "keypress", zkSpinner.onkeypress);
	zk.listen(inp, "keydown",zkSpinner._inpkeydown);

	//event for btn
	if(btn){
		zk.listen(btn, "mousedown", zkSpinner._btnDown);
		zk.listen(btn, "mouseup", zkSpinner._btnUp);
		zk.listen(btn, "mouseout", zkSpinner._btnOut);
		zk.listen(btn, "mouseover", zul.ondropbtnover);
	}
};

zkSpinner.cleanup = function (cmp) {
	if(cmp.timerId){
		clearTimeout(cmp.timerId);
		cmp.timerId = null;
	}
	zkTxbox.cleanup(cmp);
};
zkSpinner.setAttr = function (cmp, nm, val) {
	if ("value" == nm) {
		if(val){
			var inp = $real(cmp);
			inp.value = val;
		}else{
			zkSpinner._clearValue(cmp);
		}
		return true;
	}else if ("z.step" == nm) {
		cmp.step = parseInt(val);
		return true;
	}else if ("z.min" == nm) {
		cmp.min = parseInt(val);
		return true;
	}else if ("z.max" == nm) {
		cmp.max = parseInt(val);
		return true;
	}else if ("z.btnVisi" == nm) {
		var btn = $e(cmp.id + "!btn");
		if (btn) btn.style.display = val == "true" ? "": "none";
		zul.fixDropBtn2(cmp);
		return true;
	} else if ("style" == nm) {
		var inp = $real(cmp);
		if (inp) zkau.setAttr(inp, nm, zk.getTextStyle(val, true, true));
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
	return zkTxbox.setAttr(cmp, nm, val);
};

zkSpinner.rmAttr = function (cmp, nm) {
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
zkSpinner.onkeypress = function (evt) {
	zkInpEl.ignoreKeys(evt, "0123456789" + zk.MINUS);
};

zkSpinner._inpkeydown= function(evt){
	if (!evt) evt = window.event;
	var inp = Event.element(evt),
		cmp = $outer(inp);
	if (inp.disabled || inp.readOnly)
		return;

	var code =Event.keyCode(evt);
	switch (code) {
		case 38://up
			zkSpinner.checkValue(cmp);
			zkSpinner._increase(cmp, true);
			Event.stop(evt);
			break;
		case 40://down
			zkSpinner.checkValue(cmp);
			zkSpinner._increase(cmp, false);
			Event.stop(evt);
			break;
	}
};

zkSpinner.checkValue = function(cmp){
	inp = $real(cmp);

	if(!inp.value) {
		if(cmp.min && cmp.max)
			inp.value = (cmp.min<=0 && 0<=cmp.max) ? 0: cmp.min;
		else if (cmp.min)
			inp.value = cmp.min<=0 ? 0: cmp.min;
		else if (cmp.max)
			inp.value = 0<=cmp.max ? 0: cmp.max;
		else
			inp.value = 0;
	}
};

zkSpinner._btnDown= function(evt){
	if (!evt) evt = window.event;
	zul.ondropbtndown(evt);
	var cmp = $outer(Event.element(evt)),
		inp = $real(cmp);
	if(inp.disabled) return;

	zkSpinner.checkValue(cmp);


	var btn = zk.opera || zk.safari ? $e(cmp.id + "!btn") : zk.firstChild($e(cmp.id + "!btn"), "IMG"),
		ofs = zk.revisedOffset(btn);
	if ((Event.pointerY(evt) - ofs[1]) < btn.offsetHeight / 2) { //up
		zkSpinner._increase(cmp,true);
		zkSpinner._startAutoIncProc(cmp, true);
	} else {	// down
		zkSpinner._increase(cmp,false);
		zkSpinner._startAutoIncProc(cmp,false);
	}

};
zkSpinner._btnUp= function(evt){
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt)),
		inp = $real(cmp);
	if(inp.disabled) return;

	zkTxbox.sendOnChanging(inp);

	zkSpinner._stopAutoIncProc(cmp);
	inp.focus();
};
zkSpinner._btnOut= function(evt){
	if (!evt) evt = window.event;
	zul.ondropbtnout(evt);
	var cmp = $outer(Event.element(evt)),
		inp = $real(cmp);
	if(inp.disabled) return;

	zkSpinner._stopAutoIncProc(cmp);
};

//inner method
zkSpinner._increase=function (cmp,is_add){
	var inp = $real(cmp),
		value = parseInt(inp.value);
	if (is_add)
		result = value + cmp.step;
	else
		result = value - cmp.step;

	// control overflow
	if ( result > Math.pow(2,31)-1 )	result = Math.pow(2,31)-1;
	else if ( result < -Math.pow(2,31) ) result = -Math.pow(2,31);

	if (cmp.max!=null && result > cmp.max) result = cmp.max;
	else if (cmp.min!=null && result < cmp.min) result = cmp.min;

	inp.value = result;

	zkTxbox.sendOnChanging(inp);
};

zkSpinner._clearValue = function(cmp){
	var real = $real(cmp);
	real.value = real.defaultValue = "";
	return true;
};

zkSpinner._startAutoIncProc=function (cmp,isup){
	if(cmp.timerId)
		clearInterval(cmp.timerId);

	cmp.timerId = setInterval(function(){zkSpinner._increase(cmp,isup)}, 500);
};

zkSpinner._stopAutoIncProc=function (cmp){
	if(cmp.timerId)
		clearTimeout(cmp.timerId);

	cmp.timerId = null;
};
