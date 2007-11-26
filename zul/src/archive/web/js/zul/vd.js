/* vd.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 20 11:30:21     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
if (!zkVld._ebs) zkVld._ebs = [];
zkau.valid = zkVld; //zkau depends on it

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
	val = inp.value.trim();
	for (var j=0,doted,numed,dashed,perted; j < val.length; ++j) {
		var cc = val.charAt(j);
		if (cc >= '0' && cc <= '9') {
			numed = true;
			continue
		}
		switch (cc) {
		case '+': case zk.MINUS:
			if (doted || numed || dashed || perted) break; //err
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
	return null;
};
zkVld.noEmpty = function (id) {
	var inp = $real($e(id));
	return inp && !inp.value.trim() ? mesg.EMPTY_NOT_ALLOWED: null;
};

/** creates an error message box. */
zkVld.errbox = function (id, html) {
	id = $uuid(id);
	var cmp = $e(id);
	if (!cmp || !zk.isRealVisible(cmp)) return; //don't do it

	zkVld._errInfo = {id: id, html: html};
	setTimeout(zkVld._errbox, 5);
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
		var inp = $real(cmp);
		cmp._vdOldStyle = {bgc:inp.style.backgroundColor};
		inp.style.backgroundColor = "#FEF1E9";
	}

	if (!zk.isRealVisible(cmp)) return; //don't show the erro box

	if (getZKAttr(cmp, "srvald") == "custom")
		return; //don't show the default error box if custom

	var box = Validate_errorbox(id, boxid, html);
	if (!box) {
		alert(html);
		return;
	}

	zkVld._ebs.push(boxid);

	if (!zkVld._cnt) zkVld._cnt = 0;
	box.style.zIndex = 70000 + zkVld._cnt++;
	if (cmp) {
		var ofs = zk.revisedOffset(cmp), wd = cmp.offsetWidth,
			hgh = cmp.offsetHeight, atTop;
		if (zkau.currentFocus && zkau.currentFocus != cmp) {
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
	} else {
		box.style.display = "block"; //we need to calculate the size
		zk.center(box);
	}
	zkVld._fiximg(box);
	zkVld.uncover();

	if (!zk.opera) Effect.SlideDown(box, {duration:0.5});
		//if we slide, opera will slide it at the top of screen and position it
		//later. No sure it is a bug of script.aculo.us or Opera
	zul.initMovable(box, {
		zindex: box.style.zIndex, effecting: zkVld._fiximg,
		starteffect: zk.voidf, endeffect: zkVld._fiximg});
};
/** box is the box element or the component's ID. */
zkVld.closeErrbox = function (box, remaingError) {
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
		if (cmp && cmp._vdOldStyle) {
			$real(cmp).style.backgroundColor = cmp._vdOldStyle.bgc;
			cmp._vdOldStyle = null;
		}
	}

	if (box) {
		zul.cleanMovable(box.id);
		box.parentNode.removeChild(box);
		zkVld._ebs.remove(box.id);
	} else if (boxid) {
		zkVld._ebs.remove(boxid);
	}
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

		for (var j = 0; j < ctags.length; ++j) {
			var els = document.getElementsByTagName(ctags[j]);
			for (var k = 0 ; k < els.length; k++)
				if (zk.isRealVisible(els[k]))
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
}
