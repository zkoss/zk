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
zk.load("zul.zul"); //required by window's doModal

////
// textbox //
zkTxbox = {};
zkau.textbox = zkTxbox; //zkau depends on it

zkTxbox.init = function (cmp) {
	zk.listen(cmp, "focus", function () {zkTxbox.onfocus(cmp);});
	zk.listen(cmp, "blur", function() {zkTxbox.onblur(cmp);});

	//Bug 1486556: we have to enforce zkTxbox to send value back for validating
	//at the server
	if ($outer(cmp).getAttribute("zk_srvald") == "true") {
		var old = cmp.value;
		cmp.defaultValue = old + "-";
		if (old != cmp.value) cmp.value = old; //Bug 1490079
	}
};

/** Handles onblur for text input.
 * Note: we don't use onChange because it won't work if user uses IE' auto-fill
 */
zkTxbox.onblur = function (inp) {
	//stop the scanning of onChaning first
	var interval = zkau._intervals[inp.id];
	if (interval) {
		clearInterval(interval);
		delete zkau._intervals[inp.id];
	}
	if (inp.removeAttribute) inp.removeAttribute("zk_last_changing");

	zkTxbox.updateChange(inp, zkTxbox._noonblur(inp));
	zkau.onblur(inp); //fire onBlur after onChange
};
/** check any change.
 * @return false if failed (wrong data).
 */
zkTxbox.updateChange = function (inp, noonblur) {
	//Request 1565288: support maxlength for Textarea
	var maxlen = getZKAttr(inp, "maxlen");
	if (maxlen) {
		maxlen = parseInt(maxlen);
		if (maxlen > 0 && inp.value != inp.defaultValue && inp.value.length > maxlen)
			inp.value = inp.value.substring(0, maxlen);
	}

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

		while (cf) {
			if (cf == el) return true;

			//To resolve Bug 1486840 (see db.js and cb.js)
			if (zk.gecko) {
				var n = $e(getZKAttr(cf, "vparent"));
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
		var uuid = $uuid(inp);
		zkau.send({uuid: uuid, cmd: "onChange",
			data: [newval]}, zkau.asapTimeout(uuid, "onChange"));
	} else if (inp.getAttribute("zk_err")) {
		inp.removeAttribute("zk_err");
		zkau.send({uuid: $uuid(inp), cmd: "onError",
			data: [newval, null]}, -1); //clear error (even if not changed)
	}
};
zkTxbox.onfocus = function (inp) {
	zkau.onfocus(inp);

	//handling onChanging
	if (inp && inp.id && getZKAttr($outer(inp), "onChanging")) {
		inp.setAttribute("zk_last_changing", inp.value);
		if (!zkau._intervals[inp.id])
			zkau._intervals[inp.id] =
				setInterval("zkTxbox._scanChanging('"+inp.id+"')", 500);
	}
};
/** Scans whether any changes. */
zkTxbox._scanChanging = function (id) {
	var inp = $e(id);
	if (inp && getZKAttr($outer(inp), "onChanging")
	&& inp.getAttribute("zk_last_changing") != inp.value) {
		inp.setAttribute("zk_last_changing", inp.value);
		zkau.send({uuid: $uuid(id),
			cmd: "onChanging", data: [inp.value], implicit: true}, 1);
	}
}

////
//intbox//
zkInbox = {};
zkInbox.init = zkTxbox.init;
zkInbox.validate = function (cmp) {
	return zkVld.onlyInt(cmp.id);
};

////
//decimalbox//
zkDcbox = {};
zkDcbox.init = zkTxbox.init;
zkDcbox.validate = function (cmp) {
	return zkVld.onlyNum(cmp.id);
};

////
//doublebox//
zkDbbox = {};
zkDbbox.init = zkTxbox.init;
zkDbbox.validate = function (cmp) {
	return zkVld.onlyNum(cmp.id);
};

////
// button //
zkButton = {};
zkButton.init = function (cmp) {
	zk.listen(cmp, "click", zkau.onclick);
	zk.listen(cmp, "focus", function () {zkau.onfocus(cmp);});
	zk.listen(cmp, "blur", function() {zkau.onblur(cmp);});
};

zkTbtn = {}; //toolbarbutton
zkTbtn.init = function (cmp) {
	zk.listen(cmp, "click", function (evt) {
		if ("javascript:;" == cmp.href) zkau.onclick(evt);
		else {
			var t = cmp.getAttribute("target");
			if (cmp.href && !zk.isNewWindow(cmp.href, t))
				zk.progress();
		}
	});
	zk.listen(cmp, "focus", function () {zkau.onfocus(cmp);});
	zk.listen(cmp, "blur", function() {zkau.onblur(cmp);});
};

////
// checkbox and radio //
zkCkbox = {};
zkCkbox.init = function (cmp) {
	cmp = $real(cmp);
	zk.listen(cmp, "click", function () {zkCkbox.onclick(cmp);});
	zk.listen(cmp, "focus", function () {zkau.onfocus(cmp);});
	zk.listen(cmp, "blur", function() {zkau.onblur(cmp);});
};
zkCkbox.setAttr = function (cmp, nm, val) {
	if ("style" == nm) {
		var lbl = zk.firstChild(cmp, "LABEL", true);
		if (lbl) zkau.setAttr(lbl, nm, zk.getTextStyle(val));
	} else if (zkCkbox._inflds.contains(nm))
		cmp = $real(cmp);
	zkau.setAttr(cmp, nm, val);
	return true;
};
zkCkbox.rmAttr = function (cmp, nm) {
	if ("style" == nm) {
		var lbl = zk.firstChild(cmp, "LABEL", true);
		if (lbl) zkau.rmAttr(lbl, nm);
	} else if (zkCkbox._inflds.contains(nm))
		cmp = $real(cmp);
	zkau.rmAttr(cmp, nm);
	return true;
};
if (!zkCkbox._inflds)
	zkCkbox._inflds = ["checked", "disabled", "readonly", "name", "value"];

zkRadio = {};
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
		var uuid = $uuid(cmp);
		zkau.send({uuid: uuid, cmd: "onCheck", data: [newval]},
			zkau.asapTimeout(uuid, "onCheck"));
	}
};

////
// window //
zkWnd = {};
zkWnd._szs = {}
zkWnd.init = function (cmp) {
	var btn = $e(cmp.id + "!close");
	if (btn) {
		zk.listen(btn, "click", function (evt) {zkau.sendOnClose(cmp, true); Event.stop(evt);});
		zk.listen(btn, "mouseover", function () {zkau.onimgover(btn);});
		zk.listen(btn, "mouseout", function () {zkau.onimgout(btn);});
		if (!btn.style.cursor) btn.style.cursor = "default";
	}

	zk.listen(cmp, "mousemove", function (evt) {zkWnd.onmove(evt, cmp);});
	zkWnd.setSizable(cmp, zkWnd.sizable(cmp));

	//bug 1469887: re-init since it might be caused by invalidate
	if (zkau.wndmode[cmp.id]) {
		var caption = $e(cmp.id + "!caption");
		if (caption && caption.style.cursor == "") caption.style.cursor = "move";
		zkau.fixWnd(cmp);
		zkau.floatWnd(cmp, null, zkau.onWndMove);
	}
};
zkWnd.cleanup = function (cmp) {
	zkWnd.setSizable(cmp, false);
	if (zkau.wndmode[cmp.id] == "modal")
		zul.endModal(cmp.id); //it also clear wndmode[cmp.id]
};
zkWnd.setAttr = function (cmp, nm, val) {
	zkau.setAttr(cmp, nm, val);
	if (nm == "z.sizable") zkWnd.setSizable(cmp, val == "true");
	return true;
};
zkWnd.beforeOuter = function (cmp) {
	if (zkau.wndmode[cmp.id] == "modal")
		zkau.wndmode[cmp.id + "!modal"] = true; //so afterOuter know to doModal
};
zkWnd.afterOuter = function (cmp) {
	var nm = cmp.id + "!modal";
	if (zkau.wndmode[nm]) {
		delete zkau.wndmode[nm];
		zul.doModal(cmp);
	}
};
zkWnd.sizable = function (cmp) {
	return getZKAttr(cmp, "sizable") == "true";
};
zkWnd.setSizable = function (cmp, sizable) {
	var id = cmp.id;
	if (sizable) {
		if (!zkWnd._szs[id]) {
			zkWnd._szs[id] = new Draggable(cmp, {
				starteffect: zk.voidf,
				endeffect: zkWnd._endsizing, ghosting: zkWnd._ghostsizing,
				revert: true, ignoredrag: zkWnd._ignoresizing
			});
		}
	} else {
		if (zkWnd._szs[id]) {
			zkWnd._szs[id].destroy();
			delete zkWnd._szs[id];
		}
	}
};
/** 0: none, 1: top, 2: right-top, 3: right, 4: right-bottom, 5: bottom,
 * 6: left-bottom, 7: left, 8: left-top
 */
zkWnd._insizer = function (cmp, x, y) {
	var ofs = Position.cumulativeOffset(cmp);
	var r = ofs[0] + cmp.offsetWidth, b = ofs[1] + cmp.offsetHeight;
	if (x - ofs[0] <= 5) {
		if (y - ofs[1] <= 5) return 8;
		else if (b - y <= 5) return 6;
		else return 7;
	} else if (r - x <= 5) {
		if (y - ofs[1] <= 5) return 2;
		else if (b - y <= 5) return 4;
		else return 3;
	} else {
		if (y - ofs[1] <= 5) return 1;
		else if (b - y <= 5) return 5;
	}
};
zkWnd.onmove = function (evt, cmp) {
	var target = Event.element(evt);
	if (zkWnd.sizable(cmp)) {
		var c = zkWnd._insizer(cmp, Event.pointerX(evt), Event.pointerY(evt));
		if (c) {
			zk.backupStyle(cmp, "cursor");
			cmp.style.cursor = c == 1 ? 'n-resize': c == 2 ? 'ne-resize':
				c == 3 ? 'e-resize': c == 4 ? 'se-resize':
				c == 5 ? 's-resize': c == 6 ? 'sw-resize':
				c == 7 ? 'w-resize': 'nw-resize';
		} else {
			zk.restoreStyle(cmp, "cursor");
		}
	}
};
/** Called by zkWnd._szs[]'s ignoredrag for resizing window. */
zkWnd._ignoresizing = function (cmp, pointer) {
	var dg = zkWnd._szs[cmp.id];
	if (dg) {
		var v = zkWnd._insizer(cmp, pointer[0], pointer[1]);
		if (v) {
			switch (dg.z_dir = v) {
			case 1: case 5: dg.options.constraint = 'vertical'; break;
			case 3: case 7: dg.options.constraint = 'horizontal'; break;
			default: dg.options.constraint = null;
			}
			zk.disableSelection(cmp);
			return false;
		}
	}
	return true;
};
zkWnd._endsizing = function (cmp, evt) {
	zk.enableSelection(cmp);
	var dg = zkWnd._szs[cmp.id];
	if (dg && dg.z_szofs && (dg.z_szofs[0] || dg.z_szofs[1])) {
		var keys = "";
		if (evt) {
			if (evt.altKey) keys += 'a';
			if (evt.ctrlKey) keys += 'c';
			if (evt.shiftKey) keys += 's';
		}

		//adjust size
		setTimeout("zkWnd._resize($e('"+cmp.id+"'),"+dg.z_dir+","
			+dg.z_szofs[0]+","+dg.z_szofs[1]+",'"+keys+"')", 50);
		dg.z_dir = dg.z_szofs = null;
	}
};
zkWnd._resize = function (cmp, dir, ofsx, ofsy, keys) {
	var l, t, w = cmp.offsetWidth, h = cmp.offsetHeight;
	if (ofsy) {
		if (dir == 8 || dir <= 2) {
			h -= ofsy;
			if (h < 0) {
				ofsy = cmp.offsetHeight;
				h = 0;
			}
			t = parseInt(cmp.style.top || "0") + ofsy;
		}
		if (dir >= 4 && dir <= 6) {
			h += ofsy;
			if (h < 0) h = 0;
		}
	}
	if (ofsx) {
		if (dir >= 6 && dir <= 8) {
			w -= ofsx;
			if (w < 0) {
				ofsx = cmp.offsetWidth;
				w = 0;
			}
			l = parseInt(cmp.style.left || "0") + ofsx;
		}
		if (dir >= 2 && dir <= 4) {
			w += ofsx;
			if (w < 0) w = 0;
		}
	}
	if (w != cmp.offsetWidth || h != cmp.offsetHeight) {
		if (w != cmp.offsetWidth) cmp.style.width = w + "px";
		if (h != cmp.offsetHeight) cmp.style.height = h + "px";
		zkau.sendOnSize(cmp, keys);
	}
	if (l != null || t != null) {
		if (l != null) cmp.style.left = l + "px";
		if (t != null) cmp.style.top = t + "px";
		zkau.sendOnMove(cmp, keys);
	}
};

/* @param ghosting whether to create or remove the ghosting
 */
zkWnd._ghostsizing = function (dg, ghosting, pointer) {
	if (ghosting) {
		var ofs = zkau.beginGhostToDIV(dg);
		var html =
			'<div id="zk_ddghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
			+'px;';
		if (dg.z_dir == 8 || dg.z_dir <= 2)
			html += 'border-top:3px solid darkgray;';
		if (dg.z_dir >= 2 && dg.z_dir <= 4)
			html += 'border-right:3px solid darkgray;';
		if (dg.z_dir >= 4 && dg.z_dir <= 6)
			html += 'border-bottom:3px solid darkgray;';
		if (dg.z_dir >= 6 && dg.z_dir <= 8)
			html += 'border-left:3px solid darkgray;';
		document.body.insertAdjacentHTML("afterbegin", html + '"></div>');
		dg.element = $e("zk_ddghost");
	} else {
		var org = zkau.getGhostOrgin(dg);
		if (org) {
			//calc how much window is resized
			var ofs1 = Position.cumulativeOffset(dg.element);
			var ofs2 = Position.cumulativeOffset(org);
			dg.z_szofs = [ofs1[0] - ofs2[0], ofs1[1] - ofs2[1]];
		} else {
			dg.z_szofs = null;
		}
		zkau.endGhostToDIV(dg);
	}
};

////
// groupbox, caption //
zkGrbox = {};
zkCapt = {};

zkGrbox.setAttr = function (cmp, nm, val) {
	if ("z.open" == nm) {
		zkGrbox.open(cmp, val == "true", true);
		return true; //no need to store the z.open attribute
	}
	return false;
};
zkGrbox.onclick = function (evt, uuid) {
	if (!evt) evt = window.event;

	var target = Event.element(evt);
	var tn = $tag(target);
	if ("BUTTON" == tn || "INPUT" == tn || "TEXTAREA" == tn || "SELECT" == tn
	|| "A" == tn || ("TD" != tn && "TR" != tn && target.onclick))
		return;

	if (uuid) {
		var cmp = $e(uuid);
		if (getZKAttr(cmp, "closable") == "false")
			return;

		cmp = $e(uuid + "!slide");
		if (cmp)
			zkGrbox.open(uuid, cmp.style.display == "none");
	}
};
zkGrbox.open = function (gb, open, silent) {
	var gb = $e(gb);
	if (gb) {
		var panel = $e(gb.id + "!slide");
		if (panel && open != (panel.style.display != "none")
		&& !panel.getAttribute("zk_visible")) {
			action.slideDown(panel, open);
			if (!silent)
				zkau.send({uuid: gb.id, cmd: "onOpen", data: [open]},
					zkau.asapTimeout(gb, "onOpen"));
		}
	}
};

zkCapt.init = function (cmp) {
	var gb = zkCapt._parentGrbox(cmp);
	cmp = cmp.rows[0]; //first row
	if (gb && cmp) {
		zk.listen(cmp, "click",
			function (evt) {zkGrbox.onclick(evt, gb.id);});
	}
};
zkCapt._parentGrbox = function (p) {
	while (p = p.parentNode) {
		var type = $type(p);
		if (type == "Grbox") return p;
		if (type) break;
	}
	return null;
};

////
// Image//
zkImg = {};

if (zk.ie && !zk.ie7) {
	//Request 1522329: PNG with alpha color in IE
	//To simplify the implementation, Image.java invalidates instead of smartUpdate
	zkImg.init = function (cmp) {
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
			for (var attrs = img.attributes, j = 0; j < attrs.length; ++j) {
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
	zk.newFrame("zk_hfr_",
		null, zk.safari ? "width:0;height:0;display:inline": "display:none");
		//creates a hidden frame. However, in safari, we cannot use invisible frame
		//otherwise, safari will open a new window
	if (zk.ie && !zk.ie7) {
		var img = $real(cmp);
		return zkImg._fixpng(img);
	}
};
zkArea.init = function (cmp) {
	var map = $parentByType(cmp, "Map");
	var img = $real(map);
	if (img && !img.useMap)
		img.useMap = "#" + map.id + "_map";
};
zkArea.cleanup = function (cmp) {
	if (cmp.parentNode.areas.length <= 1) { //removing the last area
		var img = $real($parentByType(cmp, "Map"));
		if (img) img.useMap = "";
			//Safari bug not solved yet: once useMap is cleaned up, ismap won't
			//fall back to no-useMap
	}
};

zkMap.setAttr = function (cmp, nm, val) {
	if (zkMap._inflds.contains(nm))
		cmp = $real(cmp);
	zkau.setAttr(cmp, nm, val);
	return true;
};
zkMap.rmAttr = function (cmp, nm) {
	if (zkMap._inflds.contains(nm))
		cmp = $real(cmp);
	zkau.rmAttr(cmp, nm);
	return true;
};
if (!zkMap._inflds)
	zkMap._inflds = ["align", "alt", "border", "hspace", "vspace", "src"];

/** Called when an area is clicked. */
zkArea.onclick = function (id) {
	if (zkMap._toofast()) return;

	var cmp = $e(id);
	if (cmp) {
		var map = $parentByType(cmp, "Map");
		if (map)
			zkau.send({uuid: map.id,
				cmd: "onClick", data: [getZKAttr(cmp, "aid")]});
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
	zkau.send({uuid: id, cmd: "onClick", data: [x, y]});
};
zkMap._toofast = function () {
	if (zk.gecko) { //bug 1510374
		var now = new Date().getTime();
		if (zkMap._stamp && now - zkMap._stamp < 800)
			return true;
		zkMap._stamp = now;
	}
	return false
}

//progressmeter//
zkPMeter = {};
zkPMeter.init = function (cmp) {
	var img = $e(cmp.id + "!img");
	if (img) {
		var val = parseInt(getZKAttr(cmp, "value") || "0");
		img.style.width = Math.round((cmp.clientWidth * val) / 100) + "px";
	}
};
zkPMeter.setAttr = function (cmp, nm, val) {
	zkau.setAttr(cmp, nm, val);
	if ("z.value" == nm)
		zkPMeter.init(cmp);
	return true;
}

//Paging//
zkPg = {};
zkPg.go = function (anc, pgno) {
	var cmp = $parentByType(anc, "Pg");
	if (cmp) {
		zkau.fixFalseConfirmClose(); //Fix bug 1612312
		zkau.send({uuid: cmp.id, cmd: "onPaging", data: [pgno]});
	}
};

//popup//
zkPop = {};

/** Called by au.js's context menu. */
zkPop.context = function (ctx, ref) {
	if (getZKAttr(ctx, "onOpen"))
		zkau.send({uuid: ctx.id, cmd: "onOpen", data: [true, ref.id]});

	action.show(ctx); //onVisiAt is called in action.show
	zkPop._pop._popupId = ctx.id;
	zkau.hideCovered();
};
zkPop.close = function (ctx) {
	zkPop._pop._popupId = null;
	action.hide(ctx);
	zkau.hideCovered();
};

zk.Popup = Class.create();
Object.extend(Object.extend(zk.Popup.prototype, zk.Float.prototype), {
	_close: function (el) {
		zkPop.close(el);
	}
});
if (!zkPop._pop)
	zkau.floats.push(zkPop._pop = new zk.Popup()); //hook to zkau.js
