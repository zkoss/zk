/* pg.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May 30 14:17:08 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/

zk.load("zul.widget");


//Paging//
zkPg = {
	down_btn: "",
	
	init: function (cmp) {
		zkTxbox.init($real(cmp));
		
		cmp.actpg = $int(getZKAttr(cmp, "actpg"));
		cmp.npg = $int(getZKAttr(cmp, "numpg"));
	
		//event for input
		var inp = $real(cmp);
		zk.listen(inp, "keypress", zkPg.onkeypress);
		zk.listen(inp, "keydown", zkPg.inpkeydown);
		zk.listen(inp, "blur", zkPg.inpblur);
		
		var tb_first = $e(cmp.id+"!tb_f");
		var tb_prev = $e(cmp.id+"!tb_p");
		var tb_next = $e(cmp.id+"!tb_n");
		var tb_last = $e(cmp.id+"!tb_l");
		
		zk.listen(tb_first, "click", zkPg.onclick_first);
		zk.listen(tb_prev, "click", zkPg.onclick_prev);
		zk.listen(tb_next, "click", zkPg.onclick_next);
		zk.listen(tb_last, "click", zkPg.onclick_last);
		
		zk.listen(tb_first, "mouseover", zkPg.onover);
		zk.listen(tb_prev, "mouseover", zkPg.onover);
		zk.listen(tb_next, "mouseover", zkPg.onover);
		zk.listen(tb_last, "mouseover", zkPg.onover);
		
		zk.listen(tb_first, "mouseout", zkPg.onout);
		zk.listen(tb_prev, "mouseout", zkPg.onout);
		zk.listen(tb_next, "mouseout", zkPg.onout);
		zk.listen(tb_last, "mouseout", zkPg.onout);
		
		zk.listen(tb_first, "mousedown", zkPg.ondown);
		zk.listen(tb_prev, "mousedown", zkPg.ondown);
		zk.listen(tb_next, "mousedown", zkPg.ondown);
		zk.listen(tb_last, "mousedown", zkPg.ondown);
		
		zk.listen(tb_first, "focus", zkPg.onfocus);
		zk.listen(tb_prev, "focus", zkPg.onfocus);
		zk.listen(tb_next, "focus", zkPg.onfocus);
		zk.listen(tb_last, "focus", zkPg.onfocus);
		
		zk.listen(tb_first, "blur", zkPg.onblur);
		zk.listen(tb_prev, "blur", zkPg.onblur);
		zk.listen(tb_next, "blur", zkPg.onblur);
		zk.listen(tb_last, "blur", zkPg.onblur);

		if (cmp.actpg == 0) {
			zk.addClass(tb_first, "z-item-disd");
			zk.addClass(tb_prev, "z-item-disd");
		} else if (cmp.actpg == cmp.npg - 1) {
			zk.addClass(tb_next, "z-item-disd");
			zk.addClass(tb_last, "z-item-disd");
		}
	},
	
	go: function (anc, pgno) {
		var cmp = $parentByType(anc, "Pg");
		if (cmp)
			zkau.send({uuid: cmp.id, cmd: "onPaging", data: [pgno]});
	},
	
	onkeypress: function (evt) {
		zkInpEl.ignoreKeys(evt, "0123456789");
	},
	
	inpblur: function (evt) {
		if (!evt) evt = window.event;
		var inp = Event.element(evt),
			cmp = $outer(inp);
		if (inp.disabled || inp.readOnly)
			return;
		
		zkPg.checkValue(cmp);
		zkPg.go(cmp, inp.value-1);
		Event.stop(evt);
	},
	
	inpkeydown: function(evt){
		if (!evt) evt = window.event;
		var inp = Event.element(evt),
			cmp = $outer(inp);
		if (inp.disabled || inp.readOnly)
			return;
	
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
			break;		
		case 37://left
			break;		
		case 38: case 33: //up, PageUp
			zkPg.increase(cmp,true);
			Event.stop(evt);
			break;
		case 39://right
			break;		
		case 40: case 34: //down, PageDown
			zkPg.increase(cmp,false);
			Event.stop(evt);
			break;
		case 36://home
			zkPg.go(cmp,0);
			Event.stop(evt);
			break;
		case 35://end
			zkPg.go(cmp, cmp.npg - 1);
			Event.stop(evt);
			break;
		case 9: case 8: case 46: //tab, backspace, delete 
			break;
		case 13: //enter
			zkPg.checkValue(cmp);
			zkPg.go(cmp, inp.value-1);
			Event.stop(evt);
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
			&& !evt.ctrlKey && !evt.altKey)
				Event.stop(evt);
		}
	},
	
	checkValue: function(cmp){
		inp = $real(cmp);
		var value = $int(inp.value);
		
		if (value < 1)
			value = 1;
		else if (value > cmp.npg)
			value = cmp.npg;
			
		inp.value = value;
	},
	
	increase: function (cmp,is_add){
		var inp = $real(cmp);
		var value = $int(inp.value);
		if(is_add){
			value = value + 1;
		}else{
			value = value - 1;
		}
		if (value < 1) value = 1;
		else if (value > cmp.npg) value = cmp.npg;
		
		inp.value = value;
	},
	
	onclick_first: function(evt){
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt));
		
		if (cmp.actpg != 0)
			zkPg.go(cmp,0);
	},
	
	onclick_last: function(evt){
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt));
		
		if (cmp.actpg != cmp.npg - 1)
			zkPg.go(cmp, cmp.npg-1);
	},
	
	onclick_prev: function(evt){
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt));
		
		if (cmp.actpg > 0)
			zkPg.go(cmp, cmp.actpg - 1);
	},
	
	onclick_next: function(evt){
		if (!evt) evt = window.event;
		var cmp = $outer(Event.element(evt));
		
		if (cmp.actpg < cmp.npg - 1)
			zkPg.go(cmp, cmp.actpg + 1);
	},
	
	onover: function (evt) {
		if (!evt) evt = window.event;
		var table = $parentByTag(Event.element(evt), "TABLE");
		var cmp = $outer(Event.element(evt));
		
		if (table.className.indexOf("z-item-disd") == -1) 
			zk.addClass(table, "z-btn-over");
	},
	
	onout: function (evt) {
		if (!evt) evt = window.event;
		var table = $parentByTag(Event.element(evt), "TABLE");
		zk.rmClass(table, "z-btn-over");
	},
	
	ondown: function (evt) {
		if (!evt) evt = window.event;
		var table = $parentByTag(Event.element(evt), "TABLE");
		var cmp = $outer(Event.element(evt));

		if (table.className.indexOf("z-item-disd") != -1)  return;
		
		zk.addClass(table, "z-btn-click");
		down_btn = table.id;
		zk.listen(document.body, "mouseup", zkPg.onup);
	},
	
	onup: function (evt) {
		if (!evt) evt = window.event;
		zk.rmClass($e(down_btn), "z-btn-click");
		zk.unlisten(document.body, "mouseup", zkPg.onup);
	},
	
	onfocus: function (evt) {
		if (!evt) evt = window.event;
		var table = $parentByTag(Event.element(evt), "TABLE");
		zk.addClass(table, "z-btn-focus");
	},
	
	onblur: function (evt) {
		if (!evt) evt = window.event;
		var table = $parentByTag(Event.element(evt), "TABLE");
		zk.rmClass(table, "z-btn-focus");
	}
};