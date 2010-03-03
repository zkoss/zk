/* vd.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Thu Oct 20 11:30:21	 2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul"); //zul

////
//Customization
/** Creates the error box to display the specified error message.
 * Developer can override this method to provide a customize errorbox.
 * If null is returned, alert() is used.
 *
 * @param id the component's ID
 * @param boxid the error box's ID
 * @param msg the message
 */
if (!window.Validate_errorbox) { //not customized
	window.Validate_errorbox = function (id, boxid, msg) {
		var URI = '/web/zul/img/vd/arrowU.' + (zk.ie6Only ? 'gif':'png'),
			html =
	'<div onmousedown="zkVld._ebmdown()" onmouseup="zkVld._ebmup()" id="'
	+boxid+'" style="display:none;position:absolute" class="z-errbox"><div>'
	+'<table width="250" border="0" cellpadding="0" cellspacing="0"><tr valign="top">'
	+'<td width="17"><img src="'
	+zk.getUpdateURI(URI)+'" id="'+id
	+'!img" onclick="zkVld._eblocate(this)" title="'+mesg.GOTO_ERROR_FIELD
	+'"/></td><td>'+zk.encodeXML(msg, true) //Bug 1463668: security
	+'</td><td width="16"><img src="'+zk.getUpdateURI('/web/zul/img/common/close-off.gif')
	+'" onclick="zkVld._ebclose(this)" onmouseover="zkau.onimgover(this)" onmouseout="zkau.onimgout(this)"/>'
	+'</td></tr></table></div></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		return $e(boxid);
	};
}

////
zkVld = {};
zkVld._ebs = []; //a list of id of errbox to show
zkau.valid = zkVld; //zkau depends on it

/**
 * To notify the error box that the parent is scrolled.
 * @since 3.0.6
 */
zkVld.onScrollAt = function (n) {
	for (var elms = zkVld._ebs, j = elms.length; --j >= 0;) { //parent first
		var elm = $e(elms[j]), outer = $outer(elm);
		for (var e = outer; e; e = $parent(e)) {
			if (!$visible(e))
				break;
			if (!n || e == n) { //elm is a child of n
				zkVld.syncErrBox(elm, true);
				break;
			}
		}
	}
};
/**
 * To sync the offset of the error box and its real component.
 * @param {Object} box
 * @param {Object} unfocused if true, the box only moves to the real component not zkau.currentFocus one.
 * @since 3.0.6
 */
zkVld.syncErrBox = function (box, unfocused) {
	var cmp = $outer(box), ofs = zk.revisedOffset(cmp), wd = cmp.offsetWidth,
		hgh = cmp.offsetHeight, atTop;
	if (!unfocused && zkau.currentFocus && zkau.currentFocus.parentElement && zkau.currentFocus != cmp) {
		var o2 = zk.revisedOffset(zkau.currentFocus);
		if (o2[0] < ofs[0] + wd
		&& ofs[0] + wd + 220 < zk.innerX() + zk.innerWidth()) //Bug 1731646 (box's width unknown, so use 220)
			ofs[0] += wd + 2;
		else if (o2[1] < ofs[1]
		&& ofs[1] + hgh + 50 < zk.innerY() + zk.innerHeight())
			ofs[1] += hgh + 2;
		else atTop = true;
	} else {
		ofs[0] += wd + 2;
	}
	box.style.display = "block"; //we need to calculate the size
	if (atTop) ofs[1] -= box.offsetHeight + 1;
	ofs = zk.toStyleOffset(box, ofs[0], ofs[1]);
	box.style.left = ofs[0] + "px"; box.style.top = ofs[1] + "px";
	zkVld._fiximg(box);
};
/** Validates the specified component and returns the error msg. */
zkVld.validate = function (id) {
	//There are two ways to validate a component.
	//1. specify the function in z.valid or z.valid2
	id = $uuid(id);
	var cm = $e(id);
	if (getZKAttr(cm, "srvald") == "custom")
		return; //no client validation at all

	zkVld.validating = true; //to avoid deadloop (when both fields are invalid)
	try {
		if (cm) {
			var ermg = getZKAttr(cm, "ermg"); //custom message
			var fn = getZKAttr(cm, "valid");
			if (fn) {
				var msg =
					fn.indexOf('(') < 0 ?
						zk.resolve(fn).call(cm, id): eval(fn);
				if (msg) return ermg ? ermg: msg;
			}
			fn = getZKAttr(cm, "valid2");
			if (fn) {
				var msg =
					fn.indexOf('(') < 0 ?
						zk.resolve(fn).call(cm, id): eval(fn);
				if (msg) return ermg ? ermg: msg;
			}

			var msg = zk.eval(cm, "validate");
			if (msg) return ermg ? ermg: msg;
		}

		//2. define a method called validate in the metainfo
		var meta = zkau.getMeta(id);
		if (meta && meta.validate) {
			var msg = meta.validate();
			if (msg) return ermg ? ermg: msg;
		}

		zkVld.validating = false; //OK to check another field
		return null;
	} catch (ex) {
		zkVld.validating = false;
		throw ex;
	}
};
zkVld.onlyInt = zkVld.onlyLong = function (id) {
	return zkVld.onlyNum(id, true);
};
zkVld.onlyNum = function (id, noDot) {
	var inp = $e(id);
	if (!inp) return null;

	var fmt = $outer(inp);
	if (fmt) fmt = getZKAttr(fmt, "fmt");
	inp = $real(inp);
	var doted, numed, dashed, perted, grouped, val = inp.value.trim();
	for (var j = 0, vl = val.length; j < vl; ++j) {
		var cc = val.charAt(j);
		if (cc >= '0' && cc <= '9') {
			numed = true;
			continue
		}
		switch (cc) {
		case '+': case zk.MINUS:
			if (doted || numed || dashed || perted || grouped) break; //err
			dashed = true;
			continue; //ok
		case zk.DECIMAL:
			if (doted || perted) break; //err
			doted = cc == zk.DECIMAL;
			if (doted && noDot) return mesg.INTEGER_REQUIRED+val;
			continue;
		case zk.PERCENT:
			perted = true;
			//fall thru
		case zk.GROUPING:
			grouped = true;
		case ' ':
		case '\t':
			continue;
		default:
			if (fmt && fmt.indexOf(cc) >= 0) //recognize only in z.fmt
				continue;
			//error
		}
		return mesg.NUMBER_REQUIRED+val;
	}
	if (!numed && (doted || dashed || perted || grouped))
		return mesg.NUMBER_REQUIRED+val;

	return null;
};
zkVld.noEmpty = function (id) {
	var inp = $real($e(id));
	return inp && !inp.value.trim() ? mesg.EMPTY_NOT_ALLOWED: null;
};
/**
 * Validates the function of noEmpty and strict.
 * @param {Object} id
 * @since 3.0.2
 */
zkVld.noEmptyAndStrict = function (id) {
	var msg = zkVld.noEmpty(id);
	if (msg) return msg;
	return zkVld.strict(id);
};
/**
 * Validates the strict value for combobox.
 * @param {Object} id
 * @since 3.0.2
 */
zkVld.strict = function (id) {
	if(window.zkCmbox)
		return window.zkCmbox.strict(id);
	return null;
};
/** creates an error message box. */
zkVld.errbox = function (id, html, multiple) {
	id = $uuid(id);
	var cmp = $e(id);
	if (cmp && zk.isRealVisible(cmp, true)) {
		zkVld._errInfo = {id: id, html: html};
		zkVld._errbox();
	}
	zkVld.validating = false;
};
zkVld._errbox = function () {
	if (!zkVld._errInfo) return; //nothing to do
	var id = zkVld._errInfo.id, html = zkVld._errInfo.html;
	zkVld._errInfo = null;

	var boxid = id + "!errb";
	zkVld.closeErrbox(boxid);
	
	cmp = $e(id);
	if (cmp) {
		zk.addClass($real(cmp), getZKAttr(cmp, "zcls") + "-text-invalid");
	}

	if (!zk.isRealVisible(cmp, true)) return; //don't show the erro box

	if (getZKAttr(cmp, "srvald") == "custom")
		return; //don't show the default error box if custom

	var box = Validate_errorbox(id, boxid, html);
	if (!box) {
		zk.alert(html);
		return;
	}

	zkVld._ebs.push(boxid);

	if (!zkVld._cnt) zkVld._cnt = 0;
	box.style.zIndex = $int(Element.getStyle(box ,"z-index")) + (++zkVld._cnt);
	if (cmp) {
		if (!cmp._focusFn) { // Bug 2280308
			cmp._focusFn = function (e) {
				var box = $e(boxid);
				if (box) {
					setTimeout(function() {zkVld.syncErrBox(box)});
				}
			};
		}
		zk.listen(cmp, "focus", cmp._focusFn);
		zkVld.syncErrBox(box);
	} else {
		box.style.display = "block"; //we need to calculate the size
		zk.center(box);
	}
	zkVld._fiximg(box);
	zkVld.uncover();

	if (!zk.opera) anima.slideDown(box, {duration:0.5});
		//if we slide, opera will slide it at the top of screen and position it
		//later. No sure it is a bug of script.aculo.us or Opera
	if ((zk.useStackup === undefined ? zk.ie6Only: zk.useStackup)) setTimeout(function () {
		box._stackup = zk.makeStackup(box);
	}, 0);
	zul.initMovable(box, {
		zindex: box.style.zIndex, starteffect: zk.voidf,
		endeffect: zkVld._fiximg, change: zkVld._change});
	
	// bug 2851102
	if (box)
		zk._vpts[box.id] = cmp;
};
zkVld._change = function (dg, pointer, evt) {
	var el = dg.element;
	if (el._stackup) {
		el._stackup.style.top = el.style.top;
		el._stackup.style.left = el.style.left;
	}
	zkVld._fiximg(el);
};
/** box is the box element or the component's ID.
 *
 * @param {Object} coerce it is used to close the error box coercively. (@since 3.0.3)
 */
zkVld.closeErrbox = function (box, remainError, coerce) {
	var boxid, id;
	if (typeof box == "string") {
		id = $uuid(box);
		boxid = id + "!errb";
		box = $e(boxid);
	} else if (box) {
		boxid = box.id;
		id = $uuid(boxid);
	}

	var cmp = $e(id);
	if (!remainError) {
		if (cmp) {
			zk.rmClass($real(cmp), getZKAttr(cmp, "zcls") + "-text-invalid");
		}
	}
	if (cmp && cmp._focusFn) { // Bug 2280308
		zk.unlisten(cmp, "focus", cmp._focusFn);
		cmp._focusFn = null;
	}
	
	// Bug #2851102
	if (boxid)
		delete zk._vpts[boxid];
		
	if (box) {
		zul.cleanMovable(box.id);
		if (zk.ie6Only) {
			if (box._stackup) box._stackup.parentNode.removeChild(box._stackup);
			box._stackup = null;
		}
		box.parentNode.removeChild(box);
		zkVld._ebs.remove(box.id);
	} else if (boxid)
		if (coerce) {
			zkVld._ebs.remove(boxid);
		} else
			zkVld._ebs.remove(boxid);
};
/** Closes the errob only without clean up the error. */
zkVld._ebclose = function (el) {
	for (; el; el = el.parentNode)
		if (el.id && el.id.endsWith("!errb")) {
			var id = el.id.substring(0, el.id.length - 5);
			zkVld.closeErrbox(id, true);
			//zkVld.focus($e(id));
			//annoying (unable to leave) if user want to fix error later
			return;
		}
};
zkVld._eblocate = function (el) {
	for (; el; el = el.parentNode)
		if (el.id && el.id.endsWith("!errb")) {
			var id = el.id.substring(0, el.id.length - 5);
			zkVld.focus($e(id));
			return;
		}
};
zkVld.focus = function (el) {
	if (el) {
		try {
			if (el.select) el.select();
			if (el.focus) el.focus();
		} catch (e) {
		}
	}
};
zkVld._ebmdown = function () {zkVld.validating = true;};
zkVld._ebmup = function () {zkVld.validating = false;};

zkVld._fiximg = function (box) {
	var id = $uuid(box.id),
		cmp = $e(id),
		img = $e(id + "!img");
	if (cmp && img) {
		var cmpofs = zk.revisedOffset(cmp);
		var boxofs = zk.revisedOffset(box);
		var dx = boxofs[0] - cmpofs[0], dy = boxofs[1] - cmpofs[1], dir;
		if (dx > cmp.offsetWidth) {
			dir = dy < -10 ? "LD": dy > cmp.offsetHeight + 10 ? "LU": "L";
		} else if (dx < 0) {
			dir = dy < -10 ? "RD": dy > cmp.offsetHeight + 10 ? "RU": "R";
		} else {
			dir = dy < 0 ? "D": "U";
		}
		img.src = zk.getUpdateURI('/web/zul/img/vd/arrow'+dir+(zk.ie6Only?'.gif':'.png'));
	}
};
/** Makes el visible by moving away any error box covering el.
 */
zkVld.uncover = function (el) {
	var ctags = zk.coveredTagnames;
	for (var i = zkVld._ebs.length; --i >= 0;) {
		var boxid = zkVld._ebs[i],
			box = $e(boxid);
		if (!box) {
			zkVld._ebs.splice(i, 1);
			continue;
		}

		if (el) zkVld._uncover(box, el);
		else if (!ctags.length) return;

		for (var j = 0, cl = ctags.length; j < cl; ++j) {
			var els = document.getElementsByTagName(ctags[j]);
			for (var k = 0, elen = els.length; k < elen; k++)
				if (zk.shallHideDisabled(els[k]) && zk.isRealVisible(els[k], true))
					zkVld._uncover(box, els[k], true);
		}
	}
};
zkVld._uncover = function (box, el, ctag) {
	var elofs = zPos.cumulativeOffset(el),
		boxofs = zPos.cumulativeOffset(box);

	if (zk.isOffsetOverlapped(
	elofs, [el.offsetWidth, el.offsetHeight],
	boxofs, [box.offsetWidth, box.offsetHeight])) {
		var cmp = $e(box.id.substring(0, box.id.length - 5));
		var y;
		if (cmp) {
			var cmpofs = zPos.cumulativeOffset(cmp), cmphgh = cmp.offsetHeight;
			if (ctag) {
				var y1 = elofs[1] + el.offsetHeight, boxhgh = box.offsetHeight;
				y = cmpofs[1];
				if (y1 > y + cmphgh || y1 + boxhgh < y) {
					var y2 = elofs[1] - boxhgh;
					if (y2 > y + cmphgh || y2 + boxhgh < y) {
						//both not intercepted, use the closed one
						var d1 = y1 > y ? y1 - y - cmphgh: y - y1 - boxhgh;
						var d2 = y2 > y ? y2 - y - cmphgh: y - y2 - boxhgh;
						y = d1 <= d2 ? y1: y2;
					} else { //intercept with y2
						y = y2;
					}
				} else { //intercept with y1
					y = y1;
				}
			} else {
				var cmpbtm = cmpofs[1] + cmphgh;
				y = elofs[1] + el.offsetHeight <=  cmpbtm ? cmpbtm: cmpofs[1] - box.offsetHeight;
				//we compare bottom because default is located below
			}
		} else {
			y = boxofs[1] > elofs[1] ?
				elofs[1] + el.offsetHeight: elofs[1] - box.offsetHeight;
		}

		var ofs = zk.toStyleOffset(box, 0, y);
		box.style.top = ofs[1] + "px";
		zkVld._fiximg(box);
	}
};

/** Makes sure useless error boxes are removed. Alernative way is to use
 * cleanup, but it is too costly to have all comps having cleanup.
 */
zkVld.fixerrboxes = function () {
	for (var j = zkVld._ebs.length; --j >= 0;) {
		var boxid = zkVld._ebs[j];
		var box = $e(boxid);
		if (box) {
			var id = boxid.substring(0, boxid.length - 5);
			var cmp = $e(id);
			if (!cmp) zkVld.closeErrbox(box); //dead
		} else {
			zkVld._ebs.splice(j, 1);
		}
	}
};

/** Add what will cover dropdown list. */
zkVld.addHideCovered = function (ary) {
	for (var j = zkVld._ebs.length; --j >= 0;) {
		var el = $e(zkVld._ebs[j]);
		if (el) ary.push(el);
	}
};

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
				zcls = getZKAttr(outer, "zcls");
			if ("disabled" == nm)
				zk[val == "true" ? "addClass" : "rmClass"](outer, zcls + "-disd");
			zk[val == "true" ? "addClass" : "rmClass"](inp, "disabled" == nm ? zcls + "-text-disd" : zcls + "-readonly");
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
	/** commented by the bug #2787876
	 * var sa = getZKAttr($outer(cmp), "srvald");
	if (sa && sa != "fmt") {
		var old = cmp.value;
		cmp.defaultValue = old + "-";
		if (old != cmp.value) cmp.value = old; //Bug 1490079
	}*/
	var outer = $outer(cmp),
		zcls = getZKAttr(outer, "zcls");
	if (cmp.readOnly) {
		zk.addClass(cmp, zcls + "-readonly");
	}
	if (cmp.disabled) {
		zk.addClass(cmp, zcls + "-text-disd");
		zk.addClass(outer, zcls + "-disd");
	}
};
zkTxbox.cleanup = zkTxbox.onHide = function (cmp) {
	var inp = $real(cmp);
	if (inp) {	
		zkTxbox._scanStop(inp);
		zkVld.closeErrbox(inp.id, true);
	}
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
		zcls = getZKAttr(cmp, "zcls");
	zk.rmClass(cmp, zcls + "-focus");
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
	var newval = inp.value,
		sa = getZKAttr($outer(inp), "srvald");
	
	// bug #2787876 and  #1486556
	if ((sa && sa != "fmt") || newval != inp.defaultValue) { //changed
		inp.defaultValue = newval;
		var uuid = $uuid(inp),
			sr = zk.getSelectionRange(inp);
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
	var inp = Event.element(evt),
		maxlen = getZKAttr(inp, "maxlen");
	if (maxlen) {
		maxlen = $int(maxlen);
		if (maxlen > 0 && inp.value != inp.defaultValue
		&& inp.value.length > maxlen)
			inp.value = inp.value.substring(0, maxlen);
	}
};
zkTxbox.onkeydown = function (evt) {
	if (!evt) evt = window.event;
	var inp = Event.element(evt),
		uuid = $uuid(inp),
		cmp = $e(uuid),
		keyCode = Event.keyCode(evt);

	if (keyCode == 9 && !evt.altKey && !evt.ctrlKey && !evt.shiftKey
	&& getZKAttr(cmp, "tabbable")) {
		var sr = zk.getSelectionRange(inp),
			val = inp.value;
		val = val.substring(0, sr[0]) + '\t' + val.substring(sr[1]);
		inp.value = val;

		val = sr[0] + 1;
		zk.setSelectionRange(inp, val, val);

		Event.stop(evt);
		return;
	}

	if ((keyCode == 13 && zkau.asap(cmp, "onOK"))
	|| (keyCode == 27 && zkau.asap(cmp, "onCancel"))) {
		//Bug 2904793: zkTxbox._scanStop(inp);
		zkTxbox.updateChange(inp, false);
		//Bug 1858869: no need to send onOK here since zkau._onDocKeydown will do
	}
};
zkTxbox.onfocus = function (evt) {
	var inp = zkau.evtel(evt), //backward compatible (2.4 or before)
		cmp = $outer(inp);
	if (!$tag(inp)) return; //Bug 2111900
	if (zkau.onfocus0(evt)
	&& inp && inp.id && zkau.asap(cmp, "onChanging")) {
		//handling onChanging
		inp.setAttribute("zk_changing_last", inp.value);
		if (!zkTxbox._intervals[inp.id])
			zkTxbox._intervals[inp.id] =
				setInterval("zkTxbox._scanChanging('"+inp.id+"')", 500);
	}
	zk.addClass(cmp, getZKAttr(cmp, "zcls") + "-focus");
};
/** Scans whether any changes. */
zkTxbox._scanChanging = function (id) {
	var inp = $e(id),
		value = inp.getAttribute("zk_typeAhead") || inp.value;
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
		zk.setSelectionRange(inp, $int(ary[0]), end = $int(ary[1]));
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
	if(evt.altKey || evt.ctrlKey)
		return;
	var k = Event.keyCode(evt);
	if(!zk.ie && k < 47) return;
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
