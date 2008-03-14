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

zk.load("zul.widget");
zkSpinner = {
};

zkSpinner.init = function (cmp) {
	zkSpinner.onVisi = zkWgt.fixDropBtn; 
	zkSpinner.onHide = zkTxbox.onHide; 	
	zkSpinner.validate = zkInbox.validate;

	zkTxbox.init($real(cmp));
	
	cmp.runCount = 0;
	cmp.timerId = null;
			
	var inp = $real(cmp);
	cmp.currentStep = parseInt(getZKAttr(inp, "step"));
	cmp.min = parseInt(getZKAttr(inp, "min"));
	cmp.max = parseInt(getZKAttr(inp, "max"));

	var btn = $e(cmp.id+"!btn");
	
	//event for inpu
	zk.listen(cmp, "keypress", zkSpinner.onkeypress);
	
	//event for btn
	if(btn){
		zk.listen(btn, "mousedown", zkSpinner._btnDown);
		zk.listen(btn, "mouseup", zkSpinner._btnUp);
		zk.listen(btn, "mouseout", zkSpinner._btnOut);

		zkWgt.fixDropBtn(cmp);
	}
};

zkSpinner.cleanup = function (cmp) {
	if(cmp.timerId){
		clearTimeout(cmp.timerId);
		cmp.timerId = null;
	}
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
		cmp.currentStep = parseInt(val);
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
		if(val=="true"){
			zkWgt.fixDropBtn(cmp);
		}
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
	zkau.setAttr(cmp, nm, val);
	return true;
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

zkSpinner._btnDown= function(evt){
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt)),
		inp = $real(cmp);
	if(inp.disabled) return;

	var btn = $e(cmp.id + "!btn"),
		ofs = Position.cumulativeOffset(btn);
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
	var cmp = $outer(Event.element(evt));
	var inp = $real(cmp);
	if(inp.disabled) return;

	zkSpinner._stopAutoIncProc(cmp);
	inp.focus();
};
zkSpinner._btnOut= function(evt){
	if (!evt) evt = window.event;
	var cmp = $outer(Event.element(evt));
	var inp = $real(cmp);
	if(inp.disabled) return;

	zkSpinner._stopAutoIncProc(cmp);
};

//inner method
zkSpinner._increase=function (cmp,is_add){
	var inp = $real(cmp);
	var value = parseInt(inp.value);
	if(is_add){
		result = value + cmp.currentStep;
	}else{
		result = value - cmp.currentStep;
	}
	if (result > cmp.max) result = cmp.max;
	else if (result < cmp.min) result = cmp.min;
	
	inp.value = result;
};

zkSpinner._clearValue = function(cmp){
	$real(cmp).value="";
	return true;
};

zkSpinner._startAutoIncProc=function (cmp,isup){
	if(cmp.timerId){
		clearInterval(cmp.timerId);
	}
	cmp.timerId = setInterval(function(){zkSpinner._increase(cmp,isup)}, 500);
};

zkSpinner._stopAutoIncProc=function (cmp){
	if(cmp.timerId){
		clearTimeout(cmp.timerId);
	}
	cmp.timerId = null;
};
