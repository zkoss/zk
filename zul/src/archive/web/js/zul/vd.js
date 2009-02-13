/* vd.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 20 11:30:21     2005, Created by tomyeh
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
		var html =
	'<div onmousedown="zkVld._ebmdown()" onmouseup="zkVld._ebmup()" id="'
	+boxid+'" style="display:none;position:absolute" class="errbox"><div>'
	+'<table width="250" border="0" cellpadding="0" cellspacing="0"><tr valign="top">'
	+'<td width="17"><img src="'
	+zk.getUpdateURI('/web/zul/img/vd/arrowU.gif')+'" id="'+id
	+'!img" onclick="zkVld._eblocate(this)" title="'+mesg.GOTO_ERROR_FIELD
	+'"/></td><td>'+zk.encodeXML(msg, true) //Bug 1463668: security
	+'</td><td width="16"><img src="'+zk.getUpdateURI('/web/zul/img/close-off.gif')
	+'" onclick="zkVld._ebclose(this)" onmouseover="zkau.onimgover(this)" onmouseout="zkau.onimgout(this)"/>'
	+'</td></tr></table></div></div>';
		document.body.insertAdjacentHTML("afterbegin", html);
		return $e(boxid);
	};
}

////
zkVld = {};
if (!zkVld._ebs) zkVld._ebs = []; //a list of id of errbox to show
if (!zkVld._cbs) zkVld._cbs = []; //a list of id of errbox that shall not shown
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
	if (!unfocused && zkau.currentFocus && zkau.currentFocus != cmp) {
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
zkVld.errbox = function (id, html) {
	id = $uuid(id);
	var cmp = $e(id);
	if (cmp && zk.isRealVisible(cmp, true)) {
		zkVld._errInfo = {id: id, html: html};
		setTimeout(zkVld._errbox, 5);
	}
	zkVld.validating = false;
};
zkVld._errbox = function () {
	if (!zkVld._errInfo) return; //nothing to do

	var id = zkVld._errInfo.id, html = zkVld._errInfo.html;
	zkVld._errInfo = null;

	var boxid = id + "!errb";
	zkVld.closeErrbox(boxid);

	if (zkVld._cbs.contains(boxid)) return; //don't show the error box if it will close.
	
	cmp = $e(id);
	if (cmp) {
		zk.addClass($real(cmp), "text-invalid");
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
		zkVld.syncErrBox(box);
	} else {
		box.style.display = "block"; //we need to calculate the size
		zk.center(box);
	}
	zkVld._fiximg(box);
	zkVld.uncover();

	if (!zk.opera && !zk.ie) Effect.SlideDown(box, {duration:0.5});
		//if we slide, opera will slide it at the top of screen and position it
		//later. No sure it is a bug of script.aculo.us or Opera
	if (zk.ie6Only) setTimeout(function () {
		var ifr = document.createElement('iframe');
		ifr.id = id + "!ifr";
		ifr.frameBorder = "no";
		ifr.src="javascript:false";
		ifr.style.cssText = "position:absolute;visibility:visible;overflow:hidden;filter:alpha(opacity=0);display:block";
		box.parentNode.appendChild(ifr);
		ifr.style.width = box.offsetWidth + "px";
		ifr.style.height = box.offsetHeight + "px";
		ifr.style.top = box.style.top;
		ifr.style.left = box.style.left;
		box._ifr = ifr;
	}, 0);
	zul.initMovable(box, {
		zindex: box.style.zIndex, starteffect: zk.voidf,
		endeffect: zkVld._fiximg, change: zkVld._change});
};
zkVld._change = function (dg, pointer, evt) {
	var el = dg.element;
	if (el._ifr) {
		el._ifr.style.top = el.style.top;
		el._ifr.style.left = el.style.left;
	}
	zkVld._fiximg(el);
};
/** box is the box element or the component's ID. 
 * 
 * @param {Object} coerce it is used to close the error box coercively. (@since 3.0.3)
 */
zkVld.closeErrbox = function (box, remaingError, coerce) {
	var boxid, id;
	if (typeof box == "string") {
		id = $uuid(box);
		boxid = id + "!errb";
		box = $e(boxid);
	} else if (box) {
		boxid = box.id;
		id = $uuid(boxid);
	}

	if (!remaingError) {
		var cmp = $e(id);
		if (cmp) {
			zk.rmClass($real(cmp), "text-invalid");
		}
	}

	if (box) {
		zul.cleanMovable(box.id);
		if (zk.ie6Only) {
			if (box._ifr) box._ifr.parentNode.removeChild(box._ifr);
			box._ifr = null;
		}
		box.parentNode.removeChild(box);
		zkVld._ebs.remove(box.id);
	} else if (boxid)
		if (coerce) {
			zkVld._cbs.push(boxid);
			setTimeout(function () {zkVld._cbs.remove(boxid);zkVld._ebs.remove(boxid);}, 5);
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
	var id = $uuid(box.id);
	var cmp = $e(id);
	var img = $e(id + "!img");
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
		img.src = zk.getUpdateURI('/web/zul/img/vd/arrow'+dir+'.gif');
	}
};
/** Makes el visible by moving away any error box covering el.
 */
zkVld.uncover = function (el) {
	var ctags = zk.coveredTagnames;
	for (var i = zkVld._ebs.length; --i >= 0;) {
		var boxid = zkVld._ebs[i];
		var box = $e(boxid);
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
	var elofs = Position.cumulativeOffset(el);
	var boxofs = Position.cumulativeOffset(box);

	if (zk.isOffsetOverlapped(
	elofs, [el.offsetWidth, el.offsetHeight],
	boxofs, [box.offsetWidth, box.offsetHeight])) {
		var cmp = $e(box.id.substring(0, box.id.length - 5));
		var y;
		if (cmp) {
			var cmpofs = Position.cumulativeOffset(cmp), cmphgh = cmp.offsetHeight;
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
