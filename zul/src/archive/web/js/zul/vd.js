/* vd.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 20 11:30:21     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
function zkVld() {};
if (!zkVld._ebs) zkVld._ebs = new Array();
zkau.valid = zkVld; //zkau depends on it

/** Validates the specified component and returns the error msg. */
zkVld.validate = function (id) {
	//There are two ways to validate a component.
	//1. specify the function in zk_valid or zk_valid2
	id = $uuid(id);
	var cm = $e(id);
	zkVld.validating = true; //to avoid deadloop (when both fields are invalid)
	try {
		if (cm) {
			var ermg = cm.getAttribute("zk_ermg"); //custom message
			var fn = cm.getAttribute("zk_valid");
			if (fn) {
				var msg = zk.resolve(fn).call(cm, id);
				if (msg) return ermg ? ermg: msg;
			}
			fn = cm.getAttribute("zk_valid2");
			if (fn) {
				var msg = zk.resolve(fn).call(cm, id);
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
zkVld.onlyInt = function (id) {
	return zkVld.onlyNum(id, true);
};
zkVld.onlyNum = function (id, noDot) {
	var inp = $e(id);
	if (!inp) return null;

	var fmt = $outer(inp);
	if (fmt) fmt = fmt.getAttribute("zk_fmt");
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
			if (fmt && fmt.indexOf(cc) >= 0) //recognize only in zk_fmt
				continue;
			//error
		}
		return mesg.NUMBER_REQUIRED+val;
	}
	return null;
};
zkVld.noEmpty = function (id) {
	var inp = $e(id);
	if (!inp) return true;
	inp = $real(inp);
	if (!inp.value.trim())
		return mesg.EMPTY_NOT_ALLOWED;
	return null;
};

/** creates an error message box. */
zkVld.errbox = function (id, html) {
	var inp = $e(id);
	if (!zk.isRealVisible(inp)) return; //don't do it

	if (zk.gecko && inp && inp.focus && zkVld.focusonerror) {
		zkVld._errbox(id, html);
		setTimeout(function() {
			try {
				if (inp.select) inp.select();
				inp.focus();
			} finally {
				zkVld.validating = false;
			}
		}, 0);
	} else {
		try {
			if (inp && zkVld.focusonerror) {
				if (inp.select) inp.select();
				if (inp.focus) inp.focus();
			}
			zkVld._errbox(id, html);
		} finally {
			zkVld.validating = false;
		}
	}
};
zkVld._errbox = function (id, html) {
	var boxid = id + "!errb";
	zkVld.closeErrbox($e(boxid));

	html = '<div onmousedown="zkVld._ebmdown()" onmouseup="zkVld._ebmup()" id="'
		+boxid+'" style="display:none" class="errbox"><div>'
		+'<table width="250" border="0" cellpadding="0" cellspacing="0"><tr valign="top">'
		+'<td width="17"><img src="'
		+zk.getUpdateURI('/web/zul/img/vd/arrowU.gif')+'" id="'+id
		+'!img" onclick="zkVld._eblocate(this)" title="'+mesg.GOTO_ERROR_FIELD
		+'"/></td><td>'+zk.encodeXML(html, true) //Bug 1463668: security
		+'</td><td width="16"><img src="'+zk.getUpdateURI('/web/zul/img/close-off.gif')
		+'" onclick="zkVld._ebclose(this)" onmouseover="zkau.onimgover(this)" onmouseout="zkau.onimgout(this)"/>'
		+'</td></tr></table></div></div>';
	document.body.insertAdjacentHTML("afterbegin", html);
	var box = $e(boxid);
	if (!box) {
		alert(html);
		return;
	}

	zkVld._ebs.push(box.id);

	if (!zkVld._cnt) zkVld._cnt = 0;
	box.style.zIndex = 70000 + zkVld._cnt++;
	box.style.position = "absolute";
	box.style.display = "block"; //we need to calculate the size
	var inp = $e(id);
	if (inp) {
		var ref = $e($uuid(id));
		if (!ref) ref = inp;
		var ofs = Position.cumulativeOffset(ref);
		ofs = zk.toParentOffset(box, ofs[0], ofs[1] + ref.offsetHeight);
		box.style.left = ofs[0] + "px"; box.style.top = ofs[1] + "px";
		//we don't consider zkau.currentFocus here because onblur
		//is called before onfocus
	} else {
		zk.center(box);
	}
	zkVld.uncover();

	Effect.SlideDown(box, {duration:0.5});
	zkau.initMoveable(box, {
		zindex: box.style.zIndex, effecting: zkVld._fiximg,
		starteffect: Prototype.emptyFunction, endeffect: zkVld._fiximg});
};
/** box is the box element or the input's ID. */
zkVld.closeErrbox = function (box) {
	var boxid;
	if (typeof box == "string") {
		boxid = box;
		box = $e(box + "!errb");
	}

	if (box) {
		zkau.cleanMoveable(box.id);
		box.parentNode.removeChild(box);
		zkVld._ebs.remove(box.id);
	} else if (boxid) {
		zkVld._ebs.remove(boxid);
	}
};
zkVld._ebclose = function (el) {
	for (; el; el = el.parentNode)
		if (el.id && el.id.endsWith("!errb")) {
			var id = el.id.substring(0, el.id.length - 5);
			zkVld.closeErrbox(id);
			//zkVld.focus($e(id));
			//It is a bit annoying if user want to fix error later
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
		if (el.select) el.select();
		if (el.focus) el.focus();
	}
};
zkVld._ebmdown = function () {zkVld.validating = true;};
zkVld._ebmup = function () {zkVld.validating = false;};

zkVld._fiximg = function (box) {
	var id = box.id.substring(0, box.id.length - 5);
	var inp = $e(id);
	var img = $e(id + "!img");
	if (inp && img) {
		var inpofs = Position.cumulativeOffset(inp);
		var imgofs = Position.cumulativeOffset(img);
		var dx = inpofs[0] - imgofs[0], dy = inpofs[1] - imgofs[1],
			hgh = inp.offsetHeight;
		var dir;
		if (dx > 20) {
			dir = dy > hgh-5 ? "RD": dy < -10 ? "RU": "R";
		} else if (dx < -inp.offsetWidth+10) {
			dir = dy > hgh-5 ? "LD": dy < -10 ? "LU": "L";
		} else {
			dir = dy >= 0 ? "D": "U";
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
		var inp = $e(box.id.substring(0, box.id.length - 5));
		var y;
		if (inp) {
			var inpofs = Position.cumulativeOffset(inp);
			if (ctag) {
				y = inpofs[1] < elofs[1] ? elofs[1] + el.offsetHeight:
					elofs[1] - box.offsetHeight;
			} else {
				var inpbtm = inpofs[1] + inp.offsetHeight;
				y = elofs[1] + el.offsetHeight <=  inpbtm ? inpbtm: inpofs[1] - box.offsetHeight;
				//we compare bottom because default is located below
			}
		} else {
			y = boxofs[1] > elofs[1] ?
				elofs[1] + el.offsetHeight: elofs[1] - box.offsetHeight;
		}
		var ofs = zk.toParentOffset(box, 0, y);
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
			var inp = $e(id);
			if (!inp) zkVld.closeErrbox(box); //dead
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
