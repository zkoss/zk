/* cb.js

{{IS_NOTE
	Purpose:
		combobox, bandbox
	Description:

	History:
		Fri Dec 16 18:28:03     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.vd");

////
//Customization
/** Returns the background color for a combo item.
 * Developer can override this method by providing a different background.
 */
if (!window.Comboitem_effect) { //define it only if not customized
	window.Comboitem_effect = function (item, undo) {
		var zcls = getZKAttr(item, "zcls");
		if (undo) {
			zk.rmClass(item, zcls + "-over-seld");
			zk.rmClass(item, zcls + "-over");
		} else
			zk.addClass(item, zk.hasClass(item, zcls + "-seld") ? zcls + "-over-seld": zcls + "-over");
	};
}

////
zkCmbox = {};
zkCmbox.onblur = function (evt) {
	var inp = zkau.evtel(evt),
		uuid = $uuid(inp),
		cmp = $e(uuid);
	if (!zkTxbox._noonblur(inp) && $type(cmp) == "Cmbox") {
		var inpval = inp.value.toLowerCase();
		if (inpval.length > 0) {
			var pp2 = $e(uuid + "!cave"),
				rows = pp2.rows, jfnd = -1,
				strict = (getZKAttr(cmp, "valid") || "")
					.toLowerCase().indexOf("strict") >= 0;
			for (var j = 0, rl = rows.length; j < rl; ++j) {
			var item = rows[j];
				var txt = zkCmbox.getLabel(item).toLowerCase();
				if (zk.isVisible(item) && (!strict || !getZKAttr(item, "disd")) && txt == inpval) {
					jfnd = j;
					break;
				}
			}
			if (jfnd >= 0) item = rows[jfnd];
			else item = {id: ""};

			if ((jfnd >= 0 || !strict) && getZKAttr(cmp, "selid") != item.id) {
				setZKAttr(cmp, "selid", item.id);
				zkau.sendasap({uuid: uuid, cmd: "onSelect", data: [item.id, item.id, zkau.getKeys(evt)]});
			}
		}
	}
	zkTxbox.onblur(evt);
};

zkCmbox.init = function (cmp) {
	zkCmbox.onVisi = zkCmbox.onSize = zul.onFixDropBtn;
	zkCmbox.onHide = zkTxbox.onHide; //widget.js is ready now

	var inp = $real(cmp);
	zkTxbox.init(inp, null, zkCmbox.onblur);
	zk.listen(inp, "keydown", zkCmbox.onkey);
	zk.listen(inp, "click", function (evt) {if (inp.readOnly && !zk.dragging) zkCmbox.onbutton(cmp, evt);});
		//To mimic SELECT, it drops down if readOnly

	var pp = $e(cmp.id + "!pp");
	if (pp) // Bug #1879511
		zk.listen(pp, "click", zkCmbox.closepp);

	var btn = $e(cmp.id + "!btn");
	if (btn) {
		zk.listen(btn, "click", function (evt) {
			if (!inp.disabled && !zk.dragging)
				zkCmbox.onbutton(cmp, evt);
		});
		zk.listen(btn, "mouseover", zul.ondropbtnover);
		zk.listen(btn, "mousedown", zul.ondropbtndown);
		zk.listen(btn, "mouseout", zul.ondropbtnout);
	}
};
zkCmbox.cleanup = function (cmp) {
	zkTxbox.cleanup(cmp);
	var pp = $e(cmp.id + "!pp");
	if (pp && pp._shadow) {
		pp._shadow.cleanup();
		pp._shadow = null;
	}
};
zkCmbox.strict = function (id) {
	var inp = $real($e(id));
	if (inp && inp.value) {
		var pp2 = $e(id + "!cave"), rows = pp2.rows;
		if (pp2 && pp2.rows) {
			for (var j = 0, rl = rows.length; j < rl; ++j) {
				var item = rows[j];
				if (!zk.isVisible(item) || getZKAttr(item, "disd")) continue;
				var txt = zkCmbox.getLabel(item);
				if (txt.toLowerCase() == inp.value.toLowerCase()) {
					inp.value = txt;
					return null; // found;
				}
			}
		}
		return mesg.VALUE_NOT_MATCHED; // not found;
	}
	return null; // unchecked
};
zkCmit = {};
zkCmit.init = function (cmp) {
	if (!getZKAttr(cmp, "disd")) {
		zk.listen(cmp, "click", zkCmit.onclick);
		zk.listen(cmp, "mouseover", zkCmit.onover);
		zk.listen(cmp, "mouseout", zkCmit.onout);
	}
};
/** When an item is clicked. */
zkCmit.onclick = function (evt) {
	if (!evt) evt = window.event;
	var item = $parentByTag(Event.element(evt), "TR");
	if (item) {
		zkCmbox._selback(item);
		zkau.closeFloats($outer($parent(item)));
			//including combobox, but excluding dropdown
		zkCmit.onoutTo(item); //onmouseout might be sent (especiall we change parent)

		//Request 1537962: better responsive
		var inp = zkCmbox.getInputByItem(item);
		var uuid = $uuid(inp);
		var cmp = $e(uuid);
		var selId = getZKAttr(cmp, "selid");
		if (selId != item.id) {
			setZKAttr(cmp, "selid", item.id);
			zkau.sendasap({uuid: uuid, cmd: "onSelect", data: [item.id, item.id, zkau.getKeys(evt)]});
		}
		if (inp) zkTxbox.updateChange(inp, false); //fire onChange
		Event.stop(evt); //Bug 1597852 (cb might be a child of listitem)
	}
};

/** onmouoseover(evt). */
zkCmit.onover = function (evt) {
	if (!evt) evt = window.event;
	var item = $parentByTag(Event.element(evt), "TR");
	if (item) Comboitem_effect(item);
};
/** onmouseout(evt). */
zkCmit.onout = function (evt) {
	if (!evt) evt = window.event;
	zkCmit.onoutTo($parentByTag(Event.element(evt), "TR"));
};
zkCmit.onoutTo = function (item) {
	if (item) Comboitem_effect(item, true);
};

/** Handles setAttr. */
zkCmbox.setAttr = function (cmp, nm, val) {
	if (nm == "repos") { //hilite the most matched item
		var pp = $e(cmp.id + "!pp");
		if (pp) {
			pp.removeAttribute("zk_ckval"); //re-check is required
			if ($visible(pp))
				zkCmbox._open(cmp, cmp.id, pp, true);
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
	} else if ("z.btnVisi" == nm) {
		var btn = $e(cmp.id + "!btn");
		if (btn) btn.style.display = val == "true" ? "": "none";
		zul.onFixDropBtn(cmp);
		return true;
	} else if ("z.sel" == nm ) {
		return zkTxbox.setAttr(cmp, nm, val);
	} else if ("value" == nm) {
		var selid = getZKAttr(cmp, "selid");
		if (selid) {
			var txt = zkCmbox.getLabel($e(selid)) || "";
			if (txt.toLowerCase() != val.toLowerCase())
				rmZKAttr(cmp, "selid");
		}
	}
	return false;
};
zkCmbox.rmAttr = function (cmp, nm) {
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

zkCmbox.childchg = function (cb) {
	//we have to re-adjust the width since children are added/removed
	var pp = $e(cb.id + "!pp");
	if (!$visible(pp))
		return;

	var ppofs = zkCmbox._ppofs(pp);
	if (ppofs[0] == "auto") {
		var pp2 = $e(cb.id + "!cave");
		if (zk.ie) {
			if (pp2) pp2.style.width = "";
			pp.style.width = "";
			setTimeout("zkCmbox._cc2('"+cb.id+"')", 0);
				//we cannot handle it immediately in IE
		} else {
			zkCmbox._fixsz(cb, pp, pp2, ppofs);
		}
	}
};
zkCmbox._cc2 = function (uuid) {
	var cb = $e(uuid);
	var pp = $e(uuid + "!pp");
	if ($visible(pp))
		zkCmbox._fixsz(cb, pp, $e(cb.id + "!cave"), zkCmbox._ppofs(pp));
};

zkCmbox.onkey = function (evt) {
	var inp = Event.element(evt);
	if (!inp) return true;

	inp.removeAttribute("zk_typeAhead"); // reset
	var uuid = $uuid(inp.id), cb = $e(uuid), pp = $e(uuid + "!pp");
	if (!pp) return true;

	var keycode = Event.keyCode(evt);
	var opened = $visible(pp);
	if (keycode == 9 || (zk.safari && keycode == 0)) { //TAB or SHIFT-TAB (safari)
		if (opened) zkCmbox.close(pp);
		return true;
	}

	if (evt.altKey && (keycode == 38 || keycode == 40)) {//UP/DN
		if (keycode == 38) { //UP
			if (opened) zkCmbox.close(pp);
		} else {
			if (!opened) zkCmbox.open(pp, true);
		}
		//FF: if we eat UP/DN, Alt+UP degenerate to Alt (select menubar)
		if (zk.ie) {
			Event.stop(evt);
			return false;
		}
		return true;
	}

	//Request 1537962: better responsive
	if (opened && keycode == 13) { //ENTER
		var item = zkCmbox._autoselback(uuid); //Better usability(Bug 1633335): auto selback
		var cmp = $e(uuid), selId = getZKAttr(cmp, "selid");
		if (item && selId != item.id) {
			setZKAttr(cmp, "selid", item.id);
			zkau.sendasap({uuid: uuid, cmd: "onSelect", data: [item.id, item.id, zkau.getKeys(evt)]});
		}
		zkTxbox.updateChange(inp, false); //fire onChange
		return true;
	}

	if (keycode == 18 || keycode == 27 || keycode == 13 || keycode == 36
	|| keycode == 37 || (evt.shiftKey && keycode == 39)|| (keycode >= 112 && keycode <= 123)) //ALT, ESC, Enter, Fn
		return true; //ignore it (doc will handle it)

	var bCombobox = $type(cb) == "Cmbox";
	var selback = keycode == 38 || keycode == 40; //UP and DN
	if (getZKAttr(cb, "adr") == "true" && !opened){
		zkCmbox.open(pp, bCombobox && !selback);
		zkCmbox._hiliteLater(uuid, evt);
	}
	else if (!bCombobox)
		return true; //ignore
	else if (!selback /*&& opened disabled by JumperChen 2008/01/09*/)
		zkCmbox._hiliteLater(uuid, evt);

	if (selback/* || getZKAttr(cb, "aco") == "true"*/) {
		//Note: zkCmbox.open won't repos immediately, so we have to delay it
		setTimeout("zkCmbox._hilite('"+uuid+"',true,"+(keycode == 38)+ ", " +
			((!evt.shiftKey || keycode != 16)  && !evt.ctrlKey && keycode != 8 && keycode != 46) + ", " + keycode + ")", 3);
		Event.stop(evt);
		return false;
	}
	return true;
};
zkCmbox._hiliteLater = function (uuid, evt) {
	var keycode = Event.keyCode(evt);
	setTimeout("zkCmbox._hilite('"+uuid+"', false, false," +
		((!evt.shiftKey || keycode != 16) && !evt.ctrlKey && keycode != 8 && keycode != 46) + ", " + keycode +  ")", 1); //IE: keydown
};

/* Whn the button is clicked on button. */
zkCmbox.onbutton = function (cmp, evt) {
	var pp = $e(cmp.id + "!pp");
	if (pp) {
		if (!$visible(pp)) zkCmbox.open(pp, true);
		else zkCmbox.close(pp, true);

		if (!evt) evt = window.event; //Bug 1911864
		Event.stop(evt);
	}
};
zkCmbox.dropdn = function (cmp, dropdown) {
	var pp = $e(cmp.id + "!pp");
	if (pp) {
		if ("true" == dropdown) zkCmbox.open(pp, true);
		else zkCmbox.close(pp, true);
	}
};

/** Marks an item as selected or un-selected. */
zkCmbox._setsel = function (item, sel) {
	zk.addClass(item, getZKAttr(item, "zcls") + "-seld", sel);
};

/** Returns the text contained in the specified item. */
zkCmbox.getLabel = function (item) {
	return item ? zkCmbox.decodeXML(getZKAttr(item, 'label')) : '';
};
zkCmbox._decs = {lt: '<', gt: '>', amp: '&', "#034": '"', "#039": '\''};
zkCmbox.decodeXML = function (txt) {
	var out = "";
	if (!txt) return out;

	var k = 0, tl = txt.length;
	for (var j = 0; j < tl; ++j) {
		var cc = txt.charAt(j);
		if (cc == '&') {
			var l = txt.indexOf(';', j + 1);
			if (l >= 0) {
				var dec = zkCmbox._decs[txt.substring(j + 1, l)];
				if (dec) {
					out += txt.substring(k, j) + dec;
					k = (j = l) + 1;
				}
			}
		}
	}
	return !k ? txt:
		k < tl ? out + txt.substring(k): out;
};
zkCmbox.open = function (pp, hilite) {
	pp = $e(pp);
	var uuid = $uuid(pp.id);
	var cb = $e(uuid);
	if (!cb) return;

	zkau.closeFloats(pp);
	zkCmbox._pop.addFloatId(pp.id, $type(cb) != "Cmbox");

	zkCmbox._open(cb, uuid, pp, hilite);
	if (zk.ie6Only) zk.redoCSS(pp);
	if (zkau.asap(cb, "onOpen"))
		zkau.send({uuid: uuid, cmd: "onOpen",
			data: [true, null, $e(uuid + "!real").value]});
};
zkCmbox._open = function (cb, uuid, pp, hilite) {
	var ppofs = zkCmbox._ppofs(pp);
	pp.style.width = ppofs[0];
	pp.style.height = "auto";

	var pp2 = $e(uuid + "!cave");
	if (pp2) pp2.style.width = pp2.style.height = "auto";
	pp.style.position = "absolute"; //just in case
	pp.style.display = "block";
	pp.style.zIndex = "88000";

	// throw out
	pp.style.visibility = "hidden";
	pp.style.left = "-10000px";

	zk.onVisiAt(pp);

	//FF: Bug 1486840
	//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
	//NOTE: since the parent/child relation is changed, new listitem
	//must be inserted into the popup (by use of uuid!child) rather
	//than invalidate!!
	zk.setVParent(pp);

	// throw in
	pp.style.visibility = "";
	pp.style.left = "";

	zkCmbox._fixsz(cb, pp, pp2, ppofs);//fix size

	zk.position(pp, $real(cb), "after-start");

	setTimeout("zkCmbox._repos('"+uuid+"',"+hilite+")", 3);
		//IE issue: we have to re-position again because some dimensions
		//might not be correct here
};
/** Returns [width, height] for the popup if specified by user. */
zkCmbox._ppofs = function (pp) {
	for (var n = pp.firstChild; n; n = n.nextSibling)
		if (n.id) {
			if (!n.id.endsWith("!cave")) { //bandbox's popup
				var w = n.style.width, h = n.style.height;
				return [w ? w: "auto", h ? h: "auto"];
			}
			break;
		}
	return ["auto", "auto"];
};

/** Fixes the dimension of popup. */
zkCmbox._fixsz = function (cb, pp, pp2, ppofs) {
	if (ppofs[1] == "auto" && pp.offsetHeight > 250) {
		pp.style.height = "250px";
	} else if (pp.offsetHeight < 10) {
		pp.style.height = "10px"; //minimal
	}

	if (ppofs[0] == "auto") {
		if (pp.offsetWidth < cb.offsetWidth) {
			pp.style.width = cb.offsetWidth + "px";
			if (pp2) pp2.style.width = "100%";
				//Note: we have to set width to auto and then 100%
				//Otherwise, the width is too wide in IE
		} else {
			var wd = zk.innerWidth() - 20;
			if (wd < cb.offsetWidth) wd = cb.offsetWidth;
			if (pp.offsetWidth > wd) pp.style.width = wd;
		}
	}
};
/** Re-position the popup. */
zkCmbox._repos = function (uuid, hilite) {
	var cb = $e(uuid);
	if (!cb) return;

	var pp = $e(uuid + "!pp");
	var pp2 = $e(uuid + "!cave");
	var inpId = cb.id + "!real";
	var inp = $e(inpId);

	//FF issue:
	//If both horz and vert scrollbar are visible:
	//a row might be hidden by the horz bar.
	var rows = pp2 ? pp2.rows: null;
	if (rows) {
		var gap = pp.offsetHeight - pp.clientHeight;
		if (gap > 10 && pp.offsetHeight < 150) { //scrollbar
			var hgh = 0;
			for (var j = rows.length; --j >= 0;)
				hgh += rows[j].offsetHeight;
			pp.style.height = (hgh + 20) + "px";
				//add the height of scrollbar (18 is an experimental number)
		}
	}

	zk.position(pp, inp, "after-start");
	zkau.hideCovered();
	zk.asyncFocus(inpId);
	if (!pp._shadow)
		pp._shadow = new zk.Shadow(pp, {left: -4, right: 4, top: 2, bottom: 3, autoShow: true, stackup: (zk.useStackup === undefined ? zk.ie6Only: zk.useStackup)});
	else pp._shadow.sync();

	if (hilite) zkCmbox._hilite(uuid);
};

/** Selects back the specified item. */
zkCmbox._selback = function (item) {
	var txt = zkCmbox.getLabel(item);

	var inp = zkCmbox.getInputByItem(item);
	if (inp) {
		inp.value = txt;
		inp.setAttribute("zk_changing_selbk", txt); //used with onChanging (widget.js)
		zk.asyncFocus(inp.id);
		zk.asyncSelect(inp.id);
	}
};
/** Selects back the current hilited item, if any. */
zkCmbox._autoselback = function (uuid) {
	var pp2 = $e(uuid + "!cave");
	if (!pp2) return;

	var rows = pp2.rows;
	if (!rows) return;

	for (var j = rows.length; --j >= 0;) {
		var item = rows[j];
		if (item.getAttribute("zk_hilite") == "true") {
			zkCmbox._selback(item);
			return item;
		}
	}
};

/** Returns the input by specifying an item. */
zkCmbox.getInputByItem = function (item) {
	//we cannot use $parentByType because parentNode is changed in gecko
	var uuid = $uuid(item.parentNode);
	if (!uuid) return null;

	var inpId = uuid + "!real";
	return $e(inpId);
}

/** Auto-hilite the most matched item.
 * @param selback whether to select back (either UP or DN is pressed)
 * @param bUp whether UP is pressed
 */
zkCmbox._hilite = function (uuid, selback, bUp, reminder, keycode) {
	var inp = $e(uuid + "!real");
	if (!inp) return;

//	var aco = getZKAttr($e(uuid), "aco") == "true";
	var pp = $e(uuid + "!pp");
	if (!pp /*|| (!selback && *//*!aco &&*//* !$visible(pp))*/) return;
	var pp2 = $e(uuid + "!cave");
	if (!pp2) return;
	var rows = pp2.rows;
	if (!rows) return;

	//The comparison is case-insensitive
	var inpval = inp.value.toLowerCase(), ckval = pp.getAttribute("zk_ckval");
	if (!selback && ckval == inpval && ckval == inp.getAttribute("zk_changing_last"))
		return; //not changed

	//Identify the best matched item
	var jfnd = -1, exact = !inpval, old;
	for (var j = 0, rl = rows.length; j < rl; ++j) {
		var item = rows[j];
		if (!zk.isVisible(item)) continue;
		if (!exact) {
			var txt = zkCmbox.getLabel(item).toLowerCase();
			if (txt == inpval) {
				exact = true;
				jfnd = j;
			} else if (jfnd < 0 && txt.startsWith(inpval)) {
				jfnd = j;
			}
		}
		if (item.getAttribute("zk_hilite") == "true") {
			if (old) { //impossible but recovering from error
				zkCmbox._setsel(item, false);
				item.removeAttribute("zk_hilite");
			} else {
				old = item;
			}
		}
	}

	var found;
	if (selback) {
		if (jfnd < 0) {
			if (rows.length) found = rows[zkCmbox._next(rows, 0)];
		} else {
			if (exact) {
				var b = document.selection;
				if ((b && "Text" == b.type && document.selection.createRange().text.toLowerCase() == inpval)
				|| (!b && inpval.length && inp.selectionStart == 0
					&& inp.selectionEnd == inpval.length))
						jfnd = zkCmbox[bUp ? "_prev" : "_next"](rows, jfnd, true);
			} else {
				if (bUp) {
					jfnd = zkCmbox._prev(rows, jfnd);
					if (zkCmbox.disabled(rows[jfnd]))
						jfnd = zkCmbox._next(rows, jfnd);
				} else {
					jfnd = zkCmbox._next(rows, jfnd);
					if (zkCmbox.disabled(rows[jfnd]))
						jfnd = zkCmbox._prev(rows, jfnd);
				}
			}
			if (jfnd >= 0) found = rows[jfnd];
		}
		if (found) {
			zkCmbox._selback(found);
			var cmp = $e(uuid);
			var selId = getZKAttr(cmp, "selid");
			if (selId != found.id) {
				setZKAttr(cmp, "selid", found.id);
				zkau.sendasap({uuid: uuid, cmd: "onSelect", data: [found.id, found.id]});
			}
		}
	} else if (jfnd >= 0) {
		found = getZKAttr(rows[jfnd], "disd") ? null : rows[jfnd];
	}

	if (old != found) {
		if (old) {
			zkCmbox._setsel(old, false);
			old.removeAttribute("zk_hilite");
		}
		if (found) {
			zkCmbox._setsel(found, true);
			found.setAttribute("zk_hilite", "true");
			inpval = zkCmbox.getLabel(found);
		}
	}
	if (reminder) {
		inp.setAttribute("zk_typeAhead", inp.value);
		if (found) {
			var c = zkCmbox.getLabel(found);
			var start = inp.value.length, end = c.length, strict = getZKAttr($e(uuid), "valid") || "",
				ahead = strict.toLowerCase().indexOf("strict") > -1 ? c :
						inp.value + (start < end ? c.substring(start) : "") ;
			if (inp.value != ahead) {
				inp.value = ahead;
				if (inp.setSelectionRange) {
					inp.setSelectionRange(start, end);
					inp.focus();
				} else if (inp.createTextRange) {
					var range = inp.createTextRange();
					if (start != end) {
						range.moveEnd('character', end - range.text.length);
						range.moveStart('character', start);
					} else {
						range.move('character', start);
					}
					range.select();
				}
			}
		}
	} else if (keycode && (keycode == 8 || keycode == 46))
		inp.setAttribute("zk_typeAhead", inp.value);

	zk.scrollIntoView(pp, found); //make sure found is visible
	pp.setAttribute("zk_ckval", inpval);
};
zkCmbox._prev = function (rows, index, including) {
	for (var i = index - (including ? 1 : 0); i >= 0 ; i--)
		if (!zkCmbox.disabled(rows[i])) return i;
	return index;
};
zkCmbox._next = function (rows, index, including) {
	for (var i = index + (including ? 1 : 0), rl = rows.length; i < rl; i++)
		if (!zkCmbox.disabled(rows[i])) return i;
	return index;
};
/**
 * Returns whether the item is disabled.
 * @since 3.0.4
 */
zkCmbox.disabled = function (n) {
	return !n || !zk.isVisible(n) || getZKAttr(n, "disd");
};
/** Called from the server to close the popup based on combobox, not popup.
 */
zkCmbox.cbclose = function (cb) {
	zkCmbox.close(cb.id + "!pp", true);
};
zkCmbox.close = function (pp, focus) {
	pp = $e(pp);
	var uuid = $uuid(pp.id);
	pp.style.display = "none";
	zk.unsetVParent(pp);

	zkCmbox._pop.removeFloatId(pp.id);
	zk.onHideAt(pp);
	zkau.hideCovered();

	if (focus)
		zk.asyncFocus(uuid + "!real");
	if (pp._shadow) pp._shadow.hide();
	var cb = $e(uuid),
		btn = $e(uuid, "btn"),
		zcls = getZKAttr(cb, "zcls");
	if (btn) zk.rmClass(btn, zcls + "-btn-over");
	if (cb && zkau.asap(cb, "onOpen"))
		zkau.send({uuid: uuid, cmd: "onOpen",
			data: [false, null, $e(uuid + "!real").value]});
};
zkCmbox.closepp = function (evt) {
	if (!evt) evt = window.event;
	var pp = Event.element(evt);
	for (; pp; pp = pp.parentNode) {
		if (pp.id) {
			if (pp.id.endsWith("!pp"))
				zkCmbox.close(pp, true);
			return; //done
		}
		if (pp.onclick) return;
	}
};

zk.FloatCombo = zClass.create();
Object.extend(Object.extend(zk.FloatCombo.prototype, zk.Floats.prototype), {
	_close: function (el) {
		zkCmbox.close(el);
	}
});
if (!zkCmbox._pop)
	zkau.floats.push(zkCmbox._pop = new zk.FloatCombo()); //hook to zkau.js

//-- bandbox --//
zkBdbox = zkCmbox;
