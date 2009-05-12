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
zk.load("zul.vd");


//Paging//
zkPg = {
	down_btn: null,

	init: function (cmp) {
		cmp.actpg = $int(getZKAttr(cmp, "actpg"));
		cmp.npg = $int(getZKAttr(cmp, "numpg"));

		var inputs = $es(cmp.id + "!real");
		for (var i = inputs.length; --i>=0;)
			zkTxbox.init(inputs[i]);

		//event for input
		for (var i = inputs.length; --i>=0;) {
			zk.listen(inputs[i], "keypress", zkPg.onkeypress);
			zk.listen(inputs[i], "keydown", zkPg.inpkeydown);
			zk.listen(inputs[i], "blur", zkPg.inpblur);
		}

		var tb_first = $es(cmp.id+"!tb_f"),
			tb_prev = $es(cmp.id+"!tb_p"),
			tb_next = $es(cmp.id+"!tb_n"),
			tb_last = $es(cmp.id+"!tb_l"),
			zcls = getZKAttr(cmp, "zcls");

		for (var i = tb_first.length; --i>=0;) {
			zk.listen(tb_first[i], "click", zkPg.onclick_first);
			zk.listen(tb_prev[i], "click", zkPg.onclick_prev);
			zk.listen(tb_next[i], "click", zkPg.onclick_next);
			zk.listen(tb_last[i], "click", zkPg.onclick_last);

			if (cmp.npg == 1) {
				zk.addClass(tb_first[i], zcls + "-btn-disd");
				zk.addClass(tb_prev[i], zcls + "-btn-disd");
				zk.addClass(tb_next[i], zcls + "-btn-disd");
				zk.addClass(tb_last[i], zcls + "-btn-disd");
			} else {
				if (cmp.actpg == 0) {
					zk.addClass(tb_first[i], zcls + "-btn-disd");
					zk.addClass(tb_prev[i], zcls + "-btn-disd");
				}
				else if (cmp.actpg == cmp.npg - 1) {
					zk.addClass(tb_next[i], zcls + "-btn-disd");
					zk.addClass(tb_last[i], zcls + "-btn-disd");
				}
			}
		}

		for (var btns = ["!tb_f", "!tb_p", "!tb_n", "!tb_l"], i = btns.length; --i >= 0;){
			var btn = $es(cmp.id + btns[i]);
			for (var j = btn.length; --j>=0;) {
				zk.listen(btn[j], "mouseover", zkPg.onover);
				zk.listen(btn[j], "mouseout", zkPg.onout);
				zk.listen(btn[j], "mousedown", zkPg.ondown);
			}
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

		zkPg.checkValue(inp);
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
			zkPg.increase(inp,true);
			Event.stop(evt);
			break;
		case 39://right
			break;
		case 40: case 34: //down, PageDown
			zkPg.increase(inp,false);
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
			zkPg.checkValue(inp);
			zkPg.go(cmp, inp.value-1);
			Event.stop(evt);
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
			&& !evt.ctrlKey && !evt.altKey)
				Event.stop(evt);
		}
	},

	checkValue: function(inp){
		var	cmp = $outer(inp),
			value = $int(inp.value);

		if (value < 1)
			value = 1;
		else if (value > cmp.npg)
			value = cmp.npg;

		inp.value = value;
	},

	increase: function (inp,is_add){
		var	cmp = $outer(inp),
			value = $int(inp.value) + (is_add ? 1 : -1);

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
		var table = $parentByTag(Event.element(evt), "TABLE"),
			zcls = getZKAttr($outer(table), "zcls");
		if (!zk.hasClass(table, zcls + "-btn-disd"))
			zk.addClass(table, zcls + "-btn-over");
	},

	onout: function (evt) {
		if (!evt) evt = window.event;
		var table = $parentByTag(Event.element(evt), "TABLE"),
			zcls = getZKAttr($outer(table), "zcls");
		zk.rmClass(table, zcls + "-btn-over");
	},

	ondown: function (evt) {
		if (!evt) evt = window.event;
		var table = $parentByTag(Event.element(evt), "TABLE"),
			zcls = getZKAttr($outer(table), "zcls");
		if (zk.hasClass(table, zcls + "-btn-disd")) return;

		zk.addClass(table, zcls + "-btn-clk");
		zkPg.down_btn = table;
		zk.listen(document.body, "mouseup", zkPg.onup);
	},

	onup: function (evt) {
		if (!evt) evt = window.event;
		if (zkPg.down_btn) {
			var zcls = getZKAttr($outer(zkPg.down_btn), "zcls");
			zk.rmClass(zkPg.down_btn, zcls + "-btn-clk");
		}
		zkPg.down_btn = null;
		zk.unlisten(document.body, "mouseup", zkPg.onup);
	},
	setAttr: function (cmp, nm, val) {
		if (nm == "z.info") {
			var info = $e(cmp, "info");
			if (info) info.innerHTML = val;
			return true;
		}
		return false;
	}
};
//Paging-os//
zkPgOS = {
	go: function (anc, pgno) {
		var cmp = $parentByType(anc, "PgOS");
		if (cmp)
			zkau.send({uuid: cmp.id, cmd: "onPaging", data: [pgno]});
	},
	setAttr: function (cmp, nm, val) {
		if (nm == "z.info") {
			var info = $e(cmp, "info");
			if (info) info.innerHTML = val;
			return true;
		}
		return false;
	}
};