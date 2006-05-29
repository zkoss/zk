/* widget.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Jan 29 15:25:10     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.html.vd");
zk.load("zul.html.zul"); //required by window's doModal

////
// textbox //
function zkTxbox() {}
zkau.textbox = zkTxbox; //zkau depends on it

zkTxbox.init = function (cmp) {
	Event.observe(cmp, "focus", function () {zkTxbox.onfocus(cmp);});
	Event.observe(cmp, "blur", function() {zkTxbox.onblur(cmp);});
};

/** Handles onblur for text input.
 * Note: we don't use onChange because it won't work if user uses IE' auto-fill
 */
zkTxbox.onblur = function (inp) {
	zkTxbox.updateChange(inp, zkTxbox._noonblur(inp));
	zkau.onblur(inp); //fire onBlur after onChange
};
/** check any change.
 * @return false if failed (wrong data).
 */
zkTxbox.updateChange = function (inp, noonblur) {
	if (zkVld.validating) return true; //to avoid deadloop (when both fields are invalid)

	if (inp.id) {
		var msg = !noonblur ? zkVld.validate(inp.id): null;
			//It is too annoying (especial when checking non-empty)
			//if we alert user for something he doesn't input yet
		if (msg) {
			zkVld.errbox(inp.id, msg);
			inp.setAttribute("zk_err", "true");
			zkau.send({uuid: zkau.uuidOf(inp), cmd: "onError",
				data: [inp.value, msg]}, -1);
			return false; //failed
		}
		zkVld.closeErrbox(inp.id);

		if (inp.removeAttribute) inp.removeAttribute("zk_last_changing");
		var interval = zkau._intervals[inp.id];
		if (interval) clearInterval(interval);
	}

	if (!noonblur) zkTxbox.onupdate(inp);
	return true;
};
/** Tests whether NOT to do onblur (if inp currentFocus are in the same
 * component).
 */
zkTxbox._noonblur = function (inp) {
	var cf = zkau.currentFocus;
	if (inp && cf && inp != cf) {
		var el = inp;
		for (;; el = el.parentNode) {
			if (!el) return false;
			if (el.getAttribute && el.getAttribute("zk_combo") == "true")
				break;
		}

		while (cf) {
			if (cf == el) return true;

			//To resolve Bug 1486840 (see db.js and cb.js)
			if (zk.agtNav && cf.getAttribute) {
				var n = $(cf.getAttribute("zk_combo_parent"));
				if (n) {
					cf = n;
					continue;
				}
			}
			cf = cf.parentNode;
		}
	}
	return false;
};

/** Called if a component updates a text programmingly. Eg., datebox.
 * It checks whether the content is really changed and sends event if so.
 */
zkTxbox.onupdate = function (inp) {
	var newval = inp.value;
	if (newval != inp.defaultValue) { //changed
		inp.defaultValue = newval;
		var uuid = zkau.uuidOf(inp);
		zkau.send({uuid: uuid, cmd: "onChange",
			data: [newval]}, zkau.asapTimeout(uuid, "onChange"));
	} else if (inp.getAttribute("zk_err")) {
		inp.removeAttribute("zk_err");
		zkau.send({uuid: zkau.uuidOf(inp), cmd: "onError",
			data: [newval, null]}, -1); //clear error (even if not changed)
	}
};
zkTxbox.onfocus = function (inp) {
	zkau.onfocus(inp);

	//handling onChanging
	if (inp && inp.id && zkau.getOuter(inp).getAttribute("zk_onChanging")) {
		inp.setAttribute("zk_last_changing", inp.value);
		zkau._intervals[inp.id] =
			setInterval("zkTxbox._scanChanging('"+inp.id+"')", 500);
	}
};
/** Scans whether any changes. */
zkTxbox._scanChanging = function (id) {
	var inp = $(id);
	if (inp && zkau.getOuter(inp).getAttribute("zk_onChanging")
	&& inp.getAttribute("zk_last_changing") != inp.value) {
		inp.setAttribute("zk_last_changing", inp.value);
		zkau.send({uuid: zkau.uuidOf(id),
			cmd: "onChanging", data: [inp.value], implicit: true}, 1);
	}
}

////
//intbox, decimalbox //
zkInbox = zkDcbox = zkTxbox;

////
// button //
function zkButton() {}
zkButton.init = function (cmp) {
	Event.observe(cmp, "click", function (evt) {zkau.onclick(evt);});
	Event.observe(cmp, "focus", function () {zkau.onfocus(cmp);});
	Event.observe(cmp, "blur", function() {zkau.onblur(cmp);});
};
function zkTbtn() {} //toolbarbutton
zkTbtn.init = function (cmp) {
	Event.observe(cmp, "click", function (evt) {
		if ("javascript:;" == cmp.href) zkau.onclick(evt);
		else {
			var t = cmp.getAttribute("target");
			if (cmp.href && !zk.isNewWindow(cmp.href, t))
				zk.progress();
		}
	});
	Event.observe(cmp, "focus", function () {zkau.onfocus(cmp);});
	Event.observe(cmp, "blur", function() {zkau.onblur(cmp);});
};

////
// label, image, div//
function zkLabel() {}
zkLabel.init = function (cmp) {
	Event.observe(cmp, "click",
		function (evt) {
			if (!evt) evt = window.event;
			var target = Event.element(evt);
			if (target.getAttribute("zk_onClick") == "true")
				zkau.onclick(evt);
		});
};
zkDiv = zkImage = zkLabel;

////
// checkbox and radio //
function zkCkbox() {}
zkCkbox.init = function (cmp) {
	cmp = zkau.getReal(cmp);
	Event.observe(cmp, "click", function () {zkCkbox.onclick(cmp);});
	Event.observe(cmp, "focus", function () {zkau.onfocus(cmp);});
	Event.observe(cmp, "blur", function() {zkau.onblur(cmp);});
};
zkCkbox.setAttr = function (cmp, nm, val) {
	if ("style" == nm) {
		var lbl = zk.firstChild(cmp, "LABEL", true);
		if (lbl) zkau.setAttr(lbl, nm, zk.getTextStyle(val));
	} else if (zkCkbox._inflds.contains(nm))
		cmp = zkau.getReal(cmp);
	zkau.setAttr(cmp, nm, val);
	return true;
};
zkCkbox.rmAttr = function (cmp, nm) {
	if ("style" == nm) {
		var lbl = zk.firstChild(cmp, "LABEL", true);
		if (lbl) zkau.rmAttr(lbl, nm);
	} else if (zkCkbox._inflds.contains(nm))
		cmp = zkau.getReal(cmp);
	zkau.rmAttr(cmp, nm);
	return true;
};
if (!zkCkbox._inflds)
	zkCkbox._inflds = ["checked", "disabled", "readonly", "name", "value"];

function zkRadio() {}
zkRadio.init = zkCkbox.init;
zkRadio.setAttr = zkCkbox.setAttr;
zkRadio.rmAttr = zkCkbox.rmAttr;

/** Handles onclick for checkbox and radio. */
zkCkbox.onclick = function (cmp) {
	var newval = cmp.checked;
	//20060426: if radio, we cannot detect whether a radio is unchecked
	//by the browser -- so always zk.send
	if (cmp.type == "radio" || newval != cmp.defaultChecked) { //changed
		cmp.defaultChecked = newval;
		var uuid = zkau.uuidOf(cmp);
		zkau.send({uuid: uuid, cmd: "onCheck", data: [newval]},
			zkau.asapTimeout(uuid, "onCheck"));
	}
};

////
// window //
function zkWnd() {}
zkWnd.init = function (cmp) {
	var img = $(cmp.id + "!img");
	if (img) {
		Event.observe(img, "click", function () {zkau.close(cmp);});
		Event.observe(img, "mouseover", function () {zkau.onimgover(img);});
		Event.observe(img, "mouseout", function () {zkau.onimgout(img);});
	}
};
zkWnd.cleanup = function (cmp) {
	if (cmp.getAttribute("mode") == "modal")
		zul.endModal(cmp.id);
};
zkWnd.initInner = function (cmp) {
	zkWnd.init(cmp);
	if (cmp.getAttribute("mode")) {
		var caption = $(cmp.id + "!caption");
		if (caption && caption.style.cursor == "") caption.style.cursor = "pointer";
		zkau.disableMoveable(cmp);
		zkau.enableMoveable(cmp, zkau.autoZIndex, zkau.onWndMove);
	}
};

////
// groupbox, caption //
function zkGrbox() {}
function zkCapt() {}

zkGrbox.onclick = function (evt, uuid) {
	if (!evt) evt = window.event;

	var target = Event.element(evt);
	var tn = zk.tagName(target);
	if ("BUTTON" == tn || "INPUT" == tn || "TEXTAREA" == tn || "SELECT" == tn
	|| "A" == tn || ("TD" != tn && "TR" != tn && target.onclick))
		return;

	if (uuid) {
		var cmp = $(uuid);
		if (cmp && cmp.getAttribute("zk_closable") == "false")
			return;

		cmp = $(uuid + "!slide");
		if (cmp)
			zkGrbox.open(uuid, cmp.style.display == "none");
	}
};
zkGrbox.open = function (uuid, open) {
	var gb = $(uuid);
	var panel = $(uuid + "!slide");
	if (gb && panel && open != (panel.style.display != "none")
	&& !panel.getAttribute("zk_visible")) {
		action.slideDown(panel, open);
		zkau.send({uuid: uuid, cmd: "onOpen", data: [open]},
			zkau.asapTimeout(gb, "onOpen"));
	}
};

zkCapt.init = function (cmp) {
	var gb = zkCapt._parentGrbox(cmp);
	cmp = cmp.rows[0]; //first row
	if (gb && cmp) {
		Event.observe(cmp, "click",
			function (evt) {zkGrbox.onclick(evt, gb.id);});
	}
};
zkCapt._parentGrbox = function (p) {
	while (p = p.parentNode) {
		var type = zk.getCompType(p);
		if (type == "Grbox") return p;
		if (type) break;
	}
	return null;
};

////
// Map //
function zkMap() {}

zkMap.init = function (cmp) {
	zk.newFrame("zk_hfr_"); //creates a hidden frame
};
zkMap.setAttr = function (cmp, nm, val) {
	if (zkMap._inflds.contains(nm))
		cmp = zkau.getReal(cmp);
	zkau.setAttr(cmp, nm, val);
	return true;
};
zkMap.rmAttr = function (cmp, nm) {
	if (zkMap._inflds.contains(nm))
		cmp = zkau.getReal(cmp);
	zkau.rmAttr(cmp, nm);
	return true;
};
if (!zkMap._inflds)
	zkMap._inflds = ["align", "alt", "border", "hspace", "vspace", "src"];

/** Called when an area is clicked. */
zkMap.onarea = function (id) {
	var cmp = $(id);
	if (cmp) {
		var map = zkau.getParentByType(cmp, "Map");
		if (map)
			zkau.send({uuid: map.id, cmd: "onClick", data: [cmp.getAttribute("zk_id")]});
	}
};
/** Called by map-done.dsp */
zkMap.onclick = function (href) {
	var j = href.indexOf('?');
	if (j < 0) return;

	var k = href.indexOf('?', ++j);
	if (k < 0 ) return;

	var id = href.substring(j, k);
	if (!$(id)) return; //component might be removed

	j = href.indexOf(',', ++k);
	if (j < 0) return;

	var x = href.substring(k, j);
	var y = href.substring(j + 1);
	zkau.send({uuid: id, cmd: "onClick", data: [x, y]});
};
