/* widget.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Jan 29 15:25:10     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.vd");

////
// textbox //
zkTxbox = {};
zkau.textbox = zkTxbox; //zkau depends on it
zkTxbox._intervals = {};
_zktbau = {
	setAttr: zkau.setAttr
};
zkau.setAttr = function (cmp, nm, val) {
	if ("disabled" == nm || "readOnly" == nm) {
		var inp = $real(cmp), type = inp.type ? inp.type.toUpperCase() : "";
		if (type == "TEXT" || type == "TEXTAREA") {
			var outer = $outer(cmp),
				mcls = getZKAttr(outer, "mcls");
			if ("disabled" == nm)
				zk[val == "true" ? "addClass" : "rmClass"](outer, mcls + "-disd");
			zk[val == "true" ? "addClass" : "rmClass"](inp, "disabled" == nm ? mcls + "-text-disd" : mcls + "-readonly");
		}
	}
	return _zktbau.setAttr(cmp, nm, val);
};
zkTxbox.init = function (cmp, onfocus, onblur) {
	zk.listen(cmp, "focus", onfocus ? onfocus: zkTxbox.onfocus);
	zk.listen(cmp, "blur", onblur ? onblur: zkTxbox.onblur);
	zk.listen(cmp, "select", zkTxbox.onselect);
	if ($tag(cmp) == "TEXTAREA")
		zk.listen(cmp, "keyup", zkTxbox.onkey);
		
	zk.listen(cmp, "keydown", zkTxbox.onkeydown);

	//Bug 1486556: we have to enforce zkTxbox to send value back for validating
	//at the server
	var sa = getZKAttr($outer(cmp), "srvald");
	if (sa && sa != "fmt") {
		var old = cmp.value;
		cmp.defaultValue = old + "-";
		if (old != cmp.value) cmp.value = old; //Bug 1490079
	}
	var outer = $outer(cmp),
		mcls = getZKAttr(outer, "mcls");
	if (cmp.readOnly) {
		zk.addClass(cmp, mcls + "-readonly");
	}
	if (cmp.disabled) {
		zk.addClass(cmp, mcls + "-text-disd");
		zk.addClass(outer, mcls + "-disd");
	}
};
zkTxbox.cleanup = zkTxbox.onHide = function (cmp) {
	var inp = $real(cmp);
	if (inp) zkVld.closeErrbox(inp.id, true);
};

zkTxbox.onselect = function (evt) {
	var inp = zkau.evtel(evt); //backward compatible (2.4 or before)
	var cmp = $outer(inp);
	if (zkau.asap(cmp, "onSelection")) {
		var sr = zk.getSelectionRange(inp);
		zkau.send({uuid: cmp.id, cmd: "onSelection",
				data: [sr[0], sr[1], inp.value.substring(sr[0], sr[1])]},
    	 	100);
    }
};
/** Handles onblur for text input.
 * Note: we don't use onChange because it won't work if user uses IE' auto-fill
 */
zkTxbox.onblur = function (evt) {
	var inp = zkau.evtel(evt), //backward compatible (2.4 or before)
		noonblur = zkTxbox._noonblur(inp);
	zkTxbox._scanStop(inp);
	zkTxbox.updateChange(inp, noonblur);
	zkau.onblur(evt, noonblur); //fire onBlur after onChange
	var cmp = $outer(inp),
		mcls = getZKAttr(cmp, "mcls");
	zk.rmClass(cmp, mcls + "-focus");
};
zkTxbox._scanStop = function (inp) {	
	//stop the scanning of onChaning first
	var interval = zkTxbox._intervals[inp.id];
	if (interval) {
		clearInterval(interval);
		delete zkTxbox._intervals[inp.id];
	}
	if (inp.removeAttribute) {
		inp.removeAttribute("zk_changing_last");
		inp.removeAttribute("zk_changing_selbk");
		inp.removeAttribute("zk_typeAhead");
	}
};
/** check any change.
 * @return false if failed (wrong data).
 */
zkTxbox.updateChange = function (inp, noonblur) {
	if (zkVld.validating) return true; //to avoid deadloop (when both fields are invalid)

	if (inp && inp.id) {
		var msg = !noonblur ? zkVld.validate(inp.id): null;
			//It is too annoying (especial when checking non-empty)
			//if we alert user for something he doesn't input yet
		if (msg) {
			zkVld.errbox(inp.id, msg);
			inp.setAttribute("zk_err", "true");
			zkau.send({uuid: $uuid(inp), cmd: "onError",
				data: [inp.value, msg]}, -1);
			return false; //failed
		}
		zkVld.closeErrbox(inp.id);
	}

	if (!noonblur) zkTxbox.onupdate(inp);
	return true;
};
/** Tests whether NOT to do onblur (if inp currentFocus are in the same
 * component).
 */
zkTxbox._noonblur = function (inp) {
	if (zk.alerting) return true;

	var cf = zkau.currentFocus;
	if (inp && cf && inp != cf) {
		var el = inp;
		for (;; el = el.parentNode) {
			if (!el) return false;
			if (getZKAttr(el, "combo") == "true")
				break;
			if (getZKAttr(el, "type"))
				return false;
		}

		for (; cf; cf = $parent(cf))
			if (cf == el)
				return true;
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
		var uuid = $uuid(inp);			
		var sr = zk.getSelectionRange(inp);	
		zkau.sendasap({uuid: uuid, cmd: "onChange", data: [newval, false, sr[0]]},
			zk.delayTime_onChange ? zk.delayTime_onChange : 150);
	} else if (inp.getAttribute("zk_err")) {
		inp.removeAttribute("zk_err");
		zkau.send({uuid: $uuid(inp), cmd: "onError",
			data: [newval, null]}, -1); //clear error (even if not changed)
	}
};
zkTxbox.onkey = function (evt) {
	//Request 1565288 and 1738246: support maxlength for Textarea
	var inp = Event.element(evt);
	var maxlen = getZKAttr(inp, "maxlen");
	if (maxlen) {
		maxlen = $int(maxlen);
		if (maxlen > 0 && inp.value != inp.defaultValue
		&& inp.value.length > maxlen)
			inp.value = inp.value.substring(0, maxlen);
	}
};
zkTxbox.onkeydown = function (evt) {
	var inp = Event.element(evt),
		uuid = $uuid(inp),
		cmp = $e(uuid),
		keyCode = Event.keyCode(evt);
	if ((keyCode == 13 && zkau.asap(cmp, "onOK"))
	|| (keyCode == 27 && zkau.asap(cmp, "onCancel"))) {
		zkTxbox._scanStop(inp);
		zkTxbox.updateChange(inp, false);
		//Bug 1858869: no need to send onOK here since zkau._onDocKeydown will do
	}
};
zkTxbox.onfocus = function (evt) {
	var inp = zkau.evtel(evt), //backward compatible (2.4 or before)
		cmp = $outer(inp);
	if (zkau.onfocus0(evt)
	&& inp && inp.id && zkau.asap(cmp, "onChanging")) {
		//handling onChanging
		inp.setAttribute("zk_changing_last", inp.value);
		if (!zkTxbox._intervals[inp.id])
			zkTxbox._intervals[inp.id] =
				setInterval("zkTxbox._scanChanging('"+inp.id+"')", 500);
	}
	zk.addClass(cmp, getZKAttr(cmp, "mcls") + "-focus");
};
/** Scans whether any changes. */
zkTxbox._scanChanging = function (id) {
	var inp = $e(id);
	var value = inp.getAttribute("zk_typeAhead") || inp.value;
	if (inp && zkau.asap($outer(inp), "onChanging")
	&& inp.getAttribute("zk_changing_last") != value) {
		zkTxbox.sendOnChanging(inp, value);
	}
};
/**
 * Send the onChanging event to server.
 * @param {Object} inp
 * @param {Object} value the correct value of input element, if any.
 * @since 3.0.5
 */
zkTxbox.sendOnChanging = function (inp, value) {
	value = value || inp.value;
	inp.setAttribute("zk_changing_last", value);
	var selbk = inp.getAttribute("zk_changing_selbk");
	inp.removeAttribute("zk_changing_selbk");		
	var sr = zk.getSelectionRange(inp);
	zkau.send({uuid: $uuid(inp),
		cmd: "onChanging", data: [value, selbk == value, sr[0]],
		ignorable: true}, 100);
};
zkTxbox.setAttr = function (cmp, nm, val) {
	if("z.sel" == nm){
		var inp = $real(cmp);
		if ("all" == val) {
			zk.asyncSelect(inp.id, zk.ie ? 150 : 0);
			return true; //done
		}

		var ary = val.split(",");
		var start = $int(ary[0]), end = $int(ary[1]),
			len = inp.value.length;
		if (start < 0) start = 0;
		if (start > len) start = len;
		if (end < 0) end = 0;
		if (end > len) end = len;
		
		if (inp.setSelectionRange) {
			inp.setSelectionRange(start, end);
			inp.focus();
		} else if (inp.createTextRange) {
			var range = inp.createTextRange();
			if(start != end){
				range.moveEnd('character', end - range.text.length);
				range.moveStart('character', start);
			}else{
				range.move('character', start);
			}
			range.select();
		}
		return true;
	}
	return false;
};

////
//intbox/longbox/decimalbox/doublebox//
zkInbox = {};
zkLnbox = {};
zkDcbox = {};
zkDbbox = {};
zkInpEl = {};
zkInpEl.baseChars = "+0123456789" + zk.MINUS + zk.PERCENT + zk.GROUPING;
zkInpEl.ignoreKeys = function (evt, keys) {
	var k = Event.keyCode(evt);
    if(!zk.ie && (Event.isSpecialKey(evt) || k == 8 || k == 46)) return;
    var c = Event.charCode(evt);
    if(keys.indexOf(String.fromCharCode(c)) === -1){
        Event.stop(evt);
    }
};
zkInbox.init = zkLnbox.init = function (cmp) {	
	zk.listen(cmp, "keypress", zkInbox.onkeypress);
	zkTxbox.init(cmp);
};
zkInbox.onkeypress = function (evt) {
	zkInpEl.ignoreKeys(evt, zkInpEl.baseChars);
};
zkDcbox.init = zkDbbox.init = function (cmp) {	
	zk.listen(cmp, "keypress", zkDcbox.onkeypress);
	zkTxbox.init(cmp);
};
zkDcbox.onkeypress = function (evt) {
	zkInpEl.ignoreKeys(evt, zkInpEl.baseChars + zk.DECIMAL);
};
zkInbox.setAttr = zkLnbox.setAttr = zkDcbox.setAttr = zkDbbox.setAttr = zkTxbox.setAttr ;
zkInbox.onHide = zkLnbox.onHide = zkDcbox.onHide = zkDbbox.onHide = zkTxbox.onHide;
zkInbox.cleanup = zkLnbox.cleanup = zkDcbox.cleanup = zkDbbox.cleanup = zkTxbox.cleanup;
zkInbox.validate = function (cmp) {
	return zkVld.onlyInt(cmp.id);
};
zkLnbox.validate = function (cmp) {
	return zkVld.onlyLong(cmp.id);
};
zkDcbox.validate = function (cmp) {
	return zkVld.onlyNum(cmp.id);
};
zkDbbox.validate = function (cmp) {
	return zkVld.onlyNum(cmp.id);
};

////
// button //
zkButton = {
	down_btn: null,
	init: function (cmp) {
		if (getZKAttr(cmp, "disd")) return;
		
		var cmp = $real(cmp);
		zk.listen(cmp, "click", zkau.onclick);
		zk.listen(cmp, "dblclick", zkau.ondblclick);
			//we have to handle here since _onDocDClick won't receive it
		zk.disableSelection(cmp);
		var btn = $e($uuid(cmp), "btn");
		zk.listen(btn, "focus", zkButton.onfocus);
		zk.listen(btn, "blur", zkButton.onblur);
		zk.listen(cmp, "mousedown", zkButton.ondown);
		zk.listen(cmp, "mouseup", zkButton.onup);
		zk.listen(cmp, "mouseover", zkButton.onover);
		zk.listen(cmp, "mouseout", zkButton.onout);
	},
	onover: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $real(Event.element(evt));
		zk.addClass(cmp, getZKAttr(cmp, "mcls") + "-hover");
	},
	onout: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $real(Event.element(evt));
		if (cmp != zkButton.down_btn) {		
			zk.rmClass(cmp, getZKAttr(cmp, "mcls") + "-hover");
		}
	},
	onfocus: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $real(Event.element(evt));
		zk.addClass(cmp, getZKAttr(cmp, "mcls") + "-focus");
		zkau.onfocus(evt);
	},
	onblur: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $real(Event.element(evt));
		zk.rmClass(cmp, getZKAttr(cmp, "mcls") + "-focus");
		zkau.onblur(evt);
	},
	ondown: function (evt) {
		if (!evt) evt = window.event;
		var cmp = $real(Event.element(evt)),
			mcls = getZKAttr(cmp, "mcls");
		zk.addClass(cmp, mcls + "-click");
		zk.addClass(cmp, mcls + "-hover");
		zkButton.down_btn = cmp;
		zk.listen(document.body, "mouseup", zkButton.onup);
	},
	onup: function (evt) {
		if (!evt) evt = window.event;
		if (zkButton.down_btn) {
			var mcls = getZKAttr(zkButton.down_btn, "mcls");
			zk.rmClass(zkButton.down_btn, mcls + "-click");
			zk.rmClass(zkButton.down_btn, mcls + "-hover");
		}
		zkButton.down_btn = null;
		zk.unlisten(document.body, "mouseup", zkButton.onup);
	},
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "style.height":
			case "style.width":
			case "style":
			case "class":
				zkau.setAttr($real(cmp), nm, val);
				return true;
		}
		zkau.setAttr(cmp, nm, val);
		zkau.setAttr($real(cmp), nm, val);
		return true;
	}
};
if (zk.ie) {
	zkButton.onVisi = zkButton.onSize = function(cmp){
		cmp = $real(cmp);
		if (cmp.offsetHeight) {
			var cellHgh = $int(Element.getStyle(cmp.rows[0].cells[0], "height"));
			if (cellHgh != cmp.rows[0].cells[0].offsetHeight) {
				cmp.rows[1].style.height = cmp.offsetHeight -
				cellHgh - $int(Element.getStyle(cmp.rows[2].cells[0], "height")) + "px";
			}
		}
	};
}
//toolbarbutton
zkTbtn = {
	init: function (cmp) {
		zk.listen(cmp, "click", zkTbtn.onclick);
		
		if (!getZKAttr(cmp, "disd")) {
			zk.listen(cmp, "focus", zkau.onfocus);
			zk.listen(cmp, "blur", zkau.onblur);
		}
	},
	onclick: function (evt) {
		if (!evt) evt = window.event;
		var cmp = Event.element(evt);
		if (getZKAttr(cmp, "disd")) {
			Event.stop(evt);
			return;
		}
		zkau.onclick(evt, true); //Bug 1878839: we shall always fire onClick
		//No need to call zk.proress() since zkau.onclick sends onClick
	}
};

////
// checkbox and radio //
zkCkbox = {};
zkCkbox.init = function (cmp) {
	cmp = $real(cmp);
	zk.listen(cmp, "click", function () {zkCkbox.onclick(cmp);});
	zk.listen(cmp, "focus", zkau.onfocus);
	zk.listen(cmp, "blur", zkau.onblur);
};
zkCkbox.setAttr = function (cmp, nm, val) {
	if ("style" == nm) {
		var lbl = zk.firstChild(cmp, "LABEL", true);
		if (lbl) zkau.setAttr(lbl, nm, zk.getTextStyle(val));
	}
	zkau.setAttr(cmp, nm, val);
	return true;
};
zkCkbox.rmAttr = function (cmp, nm) {
	if ("style" == nm) {
		var lbl = zk.firstChild(cmp, "LABEL", true);
		if (lbl) zkau.rmAttr(lbl, nm);
	}
	zkau.rmAttr(cmp, nm);
	return true;
};

zkRadio = {};
zkRadio.init = zkCkbox.init;
zkRadio.setAttr = zkCkbox.setAttr;
zkRadio.rmAttr = zkCkbox.rmAttr;

/** Handles onclick for checkbox and radio. */
zkCkbox.onclick = function (cmp) {
	var newval = cmp.checked;
	if (newval != cmp.defaultChecked) { //changed
		// bug #1893575 : we have to clean all of the radio at the same group.
		// in addition we can filter unnecessary onCheck with defaultChecked
		if (cmp.type == "radio") {
			var nms = document.getElementsByName(cmp.name);
			for (var i = nms.length; --i >= 0;)
				nms[i].defaultChecked = false;
		}
		cmp.defaultChecked = newval;
		var uuid = $uuid(cmp);
		zkau.sendasap({uuid: uuid, cmd: "onCheck", data: [newval]});
	}
};

////
// groupbox, caption //
zkGrbox = {
	setAttr: function (cmp, nm, val) {
		switch (nm) {
		case "z.open":
			zkGrbox.open(cmp, val == "true", true);
			return true; //no need to store z.open
	
		case "z.cntStyle":
			var n = $e(cmp.id + "!cave");
			if (n) {
				zk.setStyle(n, val != null ? val: "");
				zkGrbox._fixHgh(cmp);
			}
			return true; //no need to store z.cntType
		case "style":
		case "style.height":
			zkau.setAttr(cmp, nm, val);
			zkGrbox._fixHgh(cmp);
			return true;
		}
		return false;
	},
	onclick: function (evt, uuid) {
		if (!evt) evt = window.event;
	
		var target = Event.element(evt);
	
		// Bug: 1991550
		for (var type; (type = $type(target)) != "Grbox"; target = $parent(target)) {
			var tn = $tag(target);
			if (type == "Button" || "BUTTON" == tn || "INPUT" == tn || "TEXTAREA" == tn || "SELECT" == tn ||
				"A" == tn || ("TD" != tn && "TR" != tn && target.onclick)) 
				return;
		}
	
		if (uuid) {
			var cmp = $e(uuid);
			if (getZKAttr(cmp, "closable") == "false")
				return;
	
			cmp = $e(uuid + "!slide");
			if (cmp)
				zkGrbox.open(uuid, !$visible(cmp), false, true);
		}
	},
	open: function (gb, open, silent, ignorable) {
		var gb = $e(gb);
		if (gb) {
			var panel = $e(gb.id + "!slide");
			if (panel && open != $visible(panel)
			&& !panel.getAttribute("zk_visible")
			&& (!ignorable || !getZKAttr(panel, "animating"))) {
				if (open) anima.slideDown(panel);
				else anima.slideUp(panel);
	
				if (!silent)
					zkau.sendasap({uuid: gb.id, cmd: "onOpen", data: [open]});
	
				if (open) setTimeout(function() {zkGrbox._fixHgh(gb);}, 500); //after slide down
			}
		}
	}
};
zkGrbox.onSize = zkGrbox._fixHgh = function (cmp) {
	var n = $e(cmp.id + "!cave");
	if (n) {
		var hgh = cmp.style.height;
		if (hgh && hgh != "auto") {
			if (zk.ie6Only) n.style.height = "";
			zk.setOffsetHeight(n, zk.getVflexHeight(n.parentNode));
		}

		//if no border-bottom, hide the shadow
		var sdw = $e(cmp.id + "!sdw");
		if (sdw) {
			var w = $int(Element.getStyle(n, "border-bottom-width"));
			sdw.style.display = w ? "": "none";
		}
	}

};
// groupbox default mold;
zkGrfs = {
	init: function (cmp) {
		var head = zk.firstChild(cmp, "LEGEND");
		if (head) zk.listen(head, "click", zkGrfs.onclick);
	},
	onclick: function (evt) {
		if (!evt) evt = window.event;
		var target = Event.element(evt);
		var tn = $tag(target);
		if ("BUTTON" == tn || "INPUT" == tn || "TEXTAREA" == tn || "SELECT" == tn
		|| "A" == tn || ("TD" != tn && "TR" != tn && target.onclick))
			return;
			
		var cmp = $parentByTag(target, "FIELDSET");
		zkGrfs.open(cmp);
	},
	open: function (cmp, silent) {
		var mcls = getZKAttr(cmp, "mcls") + "-collapsed",
			open = zk.hasClass(cmp, mcls);
		zk[open ? "rmClass" : "addClass"](cmp, mcls);
		if (!silent)
			zkau.sendasap({uuid: cmp.id, cmd: "onOpen", data: [open]});
	},
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "z.open":
				zkGrfs.open(cmp, val == "true", true);
				return true; //no need to store z.open
		
			case "z.cntStyle":
				var n = $e(cmp.id + "!cave");
				if (n)
					zk.setStyle(n, val != null ? val: "");
					
				return true; //no need to store z.cntType
		}
	}
};
zkCapt = {};

zkCapt.init = function (cmp) {
	var gb = zkCapt._parentGrbox(cmp);
	cmp = cmp.rows[0]; //first row
	if (gb && cmp) {
		zk.listen(cmp, "click",
			function (evt) {zkGrbox.onclick(evt, gb.id);});
	}
};
zkCapt._parentGrbox = function (p) {
	while (p = p.parentNode) { //yes, assign
		var type = $type(p);
		if (type == "Grbox") return p;
		if (type) break;
	}
	return null;
};

////
// Image//
zkImg = {};

if (zk.ie6Only) {
	//Request 1522329: PNG with alpha color in IE
	//To simplify the implementation, Image.java invalidates instead of smartUpdate
	zkImg.init = function (cmp) {
		// this function should be invoked faster than zkau.initdrag(), otherwise its drag-drop will fail.
		return zkImg._fixpng(cmp);
	};
	zkImg._fixpng = function (img) {
		if (img.getAttribute("zk_alpha") && img.src
		&& img.src.toLowerCase().endsWith(".png")) {
			var id = img.id;
			var wd = img.width, hgh = img.height;
			if (!wd) wd = img.offsetWidth;
			if (!hgh) hgh = img.offsetHeight;

			var commonStyle = "width:"+wd+"px;height:"+hgh+"px;";
			if (img.hspace) commonStyle +="margin-left:"+img.hspace+"px;margin-right:"+img.hspace+"px;";
			if (img.vspace) commonStyle +="margin-top:"+img.vspace+"px;margin-bottom:"+img.vspace+"px;";
			commonStyle += img.style.cssText;

			var html = '<span id="'+id
				+'" style="filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\''
				+img.src+"', sizingMethod='scale');display:inline-block;";
			if (img.align == "left") html += "float:left;";
			else if (img.align == "right") html += "float:right;";
			if ($tag(img.parentNode) == "A") html += "cursor:hand;";
			html += commonStyle+'"';
			if (img.className) html += ' class="'+img.className+'"';
			if (img.title) html += ' title="'+img.title+'"';

			//process zk_xxx
			for (var attrs = img.attributes, j = 0, al = attrs.length; j < al; ++j) {
				var attr = attrs.item(j);
				if (attr.name.startsWith("z."))
					html += ' '+attr.name+'="'+attr.value+'"';
			}

			html += '></span>';

			if (img.isMap) {
				html += '<img style="position:relative;left:-'+wd+'px;'+commonStyle
					+'" src="'+zk.getUpdateURI('/web/img/spacer.gif')
					+'" ismap="ismap"';
				if (img.useMap) html += ' usemap="'+img.useMap+'"';
				html += '/>';
			}
			img.outerHTML = html;
			return $e(id); //transformed
		}
	}
}

////
// Imagemap //
zkMap = {};
zkArea = {};

zkMap.init = function (cmp) {
	zkMap._ckchd(cmp);
	zk.newFrame("zk_hfr_",
		null, zk.safari ? "width:0;height:0;display:inline": "display:none");
		//creates a hidden frame. However, in safari, we cannot use invisible frame
		//otherwise, safari will open a new window
};
/** Check if any child (area). */
zkMap._ckchd = function (cmp) {
	var mapid = cmp.id + "_map",
		map = $e(mapid),
		img = $real(cmp),
		bArea = map && map.areas.length;
	img.useMap = bArea ? "#" + mapid: "";
	img.isMap = !bArea;
};
zkMap.setAttr = function (cmp, nm, val) {
	if ("ckchd" == nm) {
		zkMap._ckchd(cmp);
		return true;
	}
	return false;
};
zkMap.onSize = function (cmp) {
	if (zk.ie6Only) {
		var img = $real(cmp);
		return zkImg._fixpng(img);
	}
};
zkArea.init = function (cmp) {
	zk.listen(cmp, "click", zkArea.onclick);
};

/** Called when an area is clicked. */
zkArea.onclick = function (evt) {
	if (zkMap._toofast()) return;

	var cmp = Event.element(evt);
	if (cmp) {
		var map = $parentByType(cmp, "Map");
		if (map)
			zkau.send({uuid: map.id,
				cmd: "onClick", data: [getZKAttr(cmp, "aid")], ctl: true});
	}
};
/** Called by map-done.dsp */
zkMap.onclick = function (href) {
	if (zkMap._toofast()) return;

	var j = href.indexOf('?');
	if (j < 0) return;

	var k = href.indexOf('?', ++j);
	if (k < 0 ) return;

	var id = href.substring(j, k);
	if (!$e(id)) return; //component might be removed

	j = href.indexOf(',', ++k);
	if (j < 0) return;

	var x = href.substring(k, j);
	var y = href.substring(j + 1);
	zkau.send({uuid: id, cmd: "onClick", data: [x, y], ctl: true});
};
zkMap._toofast = function () {
	if (zk.gecko) { //bug 1510374
		var now = $now();
		if (zkMap._stamp && now - zkMap._stamp < 800)
			return true;
		zkMap._stamp = now;
	}
	return false
}

//progressmeter//
zkPMeter = {};
zkPMeter.onSize = zkPMeter.onVisi = function (cmp) {
	var img = $e(cmp.id + "!img");
	if (img) {
		if (zk.ie6Only) img.style.width = ""; //Bug 1899749
		var val = $int(getZKAttr(cmp, "value"));
		img.style.width = Math.round((cmp.clientWidth * val) / 100) + "px";
	}
};
zkPMeter.setAttr = function (cmp, nm, val) {
	zkau.setAttr(cmp, nm, val);
	if ("z.value" == nm || "style.width" == nm)
		zkPMeter.onSize(cmp);
	return true;
}


//popup//
zkPop = {
	/** Called by au.js's context menu. */
	context: function (ctx, ref) {	
		zk.show(ctx); //onVisiAt is called in zk.show
		var asap= zkau.asap(ctx, "onOpen");	
		if (asap) {
			//use a progress bar to hide the popup
			var mask = zk.applyMask(ctx.id, "");
			//register addOnReponse to remove the progress bar after receiving the response from server
			if (mask) zkau.addOnResponse("zk.remove($e('"+mask.id+"'))");		
		}		
		zkPop._pop.addFloatId(ctx.id, true); //it behaves like Popup (rather than dropdown)
		zkau.hideCovered();
		if (zk.ie6Only) {
			if (!ctx._lining)
				ctx._lining = zk.createLining(ctx);
			else {
				ctx._lining.style.top = ctx.style.top;
				ctx._lining.style.left = ctx.style.left;
				ctx._lining.style.display = "block";
			}
		}
		
		ctx.style.visibility = "inherit";
		if (asap)
			zkau.send({uuid: ctx.id, cmd: "onOpen",
				data: ref ? [true, ref.id]: [true]});
	},
	close: function (ctx) {
		zkPop._pop.removeFloatId(ctx.id);
		zkPop._close(ctx);
			
		rmZKAttr(ctx, "owner"); //it is set by au.js after calling zkPop.context
	},
	_close: function (ctx) {
		ctx.style.display = "none";
		
		if (ctx._lining)
			ctx._lining.style.display = "none";
			
		zk.unsetVParent(ctx);
		zkau.hideCovered();
	
		if (zkau.asap(ctx, "onOpen"))
			zkau.send({uuid: ctx.id, cmd: "onOpen", data: [false]});
	},
	cleanup: function (ctx) {
		if (ctx._lining)
			zk.remove(ctx._lining);
		ctx._lining = null;
	},
	onVisi: zk.ie7 ? function (cmp) {
		var wdh = cmp.style.width;
		var fir = zk.firstChild(cmp, "DIV"), last = zk.lastChild(zk.lastChild(cmp, "DIV"), "DIV"),
			n = $e(cmp.id + "!cave").parentNode;
		if (!wdh || wdh == "auto") {
			var diff = zk.getFrameWidth(n.parentNode) + zk.getFrameWidth(n.parentNode.parentNode);
			if (fir) {
				fir.firstChild.firstChild.style.width = n.offsetWidth - (zk.getFrameWidth(fir)
					+ zk.getFrameWidth(fir.firstChild) - diff) + "px";
			}
			if (last) {
				last.firstChild.firstChild.style.width = n.offsetWidth - (zk.getFrameWidth(last)
					+ zk.getFrameWidth(last.firstChild) - diff) + "px";
			}
		} else {
			if (fir) fir.firstChild.firstChild.style.width = "";
			if (last) last.firstChild.firstChild.style.width = "";
		}
	}: zk.voidf,
	setAttr: function (cmp, nm, val) {
		if ("style.width" == nm) {
			zkau.setAttr(cmp, nm, val);
			zkPop.onVisi(cmp);
			return true;
		}
		return false;
	}
};
zk.Popup = zClass.create();
Object.extend(Object.extend(zk.Popup.prototype, zk.Floats.prototype), {
	_close: function (el) {
		zkPop._close(el);
	}
});
if (!zkPop._pop)
	zkau.floats.push(zkPop._pop = new zk.Popup()); //hook to zkau.js

//iframe//
zkIfr = {}

if (zk.ie) {
	zkIfr.init = function (cmp) {
	//Bug 1896797: setVParent (for overlapped) cause IE7 malfunction, so reload
	//1. it is OK if under AU (so only booting)
	//2. no 2nd load so the performance not hurt
		if (zk.booting)
			for (var n = cmp; n = n.parentNode;) {
				var m = getZKAttr(n, "mode");
				if (m && m != "embedded")
					cmp.src = cmp.src;
			}
	};
} else if (zk.gecko) { //Bug 1692495
	zkIfr.onVisi = function (cmp) {
		if (cmp.src.indexOf(".xml") >= 0)
			cmp.src = cmp.src; //strange workaround: reload xml
	};
}

//Style//
var zkStyle = {};
zkStyle.init = function (cmp) {
	var src = getZKAttr(cmp, "src");
	if (src) zk.loadCSSDirect(src, cmp.id + "-");
};
zkStyle.cleanup = function (cmp) {
	var css = $e(cmp.id + "-");
	if (css) zk.remove(css);
};

//utilities//
var zkWgt = {};
/** Fixes the button align with an input box, such as combobox, datebox. for the trendy mold
 * @since 3.5.0
 */
zkWgt.fixDropBtn2 = function (cmp) {
	//For new initial phase, we don't need to delay the function for IE. (Bug 1752477) 
	var cmp = $e(cmp);
	if (cmp) zkWgt._fixdbtn2(cmp);
};
zkWgt._fixdbtn2 = function (cmp) {
	cmp = $e(cmp);
	if (!cmp) return; //it might be gone if the user press too fast

	var btn = $e(cmp.id + "!btn");
	//note: isRealVisible handles null argument
	if (zk.isRealVisible(btn) && btn.style.position != "relative") {
		var inp = $real(cmp), img = zk.firstChild(btn, "IMG");
		if (!inp.offsetHeight || !img.offsetHeight) {
			setTimeout("zkWgt._fixdbtn2($e('" + cmp.id +"'))", 66);
			return;
		}

		//Bug 1738241: don't use align="xxx"
		var v = inp.offsetHeight - img.offsetHeight;
		if (v !== 0) {
			var imghgh = $int(Element.getStyle(img, "height")) + v;
			img.style.height = (imghgh < 0 ? 0 : imghgh) + "px"; 
		}

		v = inp.offsetTop - img.offsetTop;
		btn.style.position = "relative";
		btn.style.top = v + "px";
		if (zk.safari) btn.style.left = "-2px";
	}
};
zkWgt.onbtnover = function (evt) {
	if (!evt) evt = window.event;
	var btn = $parentByTag(Event.element(evt), "SPAN"),
		inp = $real(btn),
		mcls = getZKAttr($outer(btn), "mcls");
	if (inp && !inp.disabled && !zk.dragging)
		zk.addClass(btn, mcls + "-btn-over");
};
zkWgt.onbtnout = function (evt) {
	if (!evt) evt = window.event;
	var btn = $parentByTag(Event.element(evt), "SPAN"),
		inp = $real(btn),
		mcls = getZKAttr($outer(btn), "mcls");
	if (inp && !inp.disabled && !zk.dragging)
		zk.rmClass(btn, mcls + "-btn-over");
};
zkWgt.onbtndown = function (evt) {
	if (!evt) evt = window.event;
	var btn = $parentByTag(Event.element(evt), "SPAN"),
		inp = $real(btn),
		mcls = getZKAttr($outer(btn), "mcls");
	if (inp && !inp.disabled && !zk.dragging) {
		if (zkWgt._currentbtn) 
			zkWgt.onbtnup(evt);
		zk.addClass(btn, mcls + "-btn-click");
		zk.listen(document.body, "mouseup", zkWgt.onbtnup);
		zkWgt._currentbtn = btn;
	}
};
zkWgt.onbtnup = function (evt) {
	zkWgt._currentbtn = $e(zkWgt._currentbtn);
	var mcls = getZKAttr($outer(zkWgt._currentbtn), "mcls");
	zk.rmClass(zkWgt._currentbtn, mcls + "-btn-click");
	zk.unlisten(document.body, "mouseup", zkWgt.onbtnup);
	zkWgt._currentbtn = null;
};
zkWgt.onFixDropBtn = function (cmp) {
	zkWgt.fixDropBtn2(cmp);
};