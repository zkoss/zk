/* cb.js

{{IS_NOTE
	Purpose:
		combobox, bandbox
	Description:
		
	History:
		Fri Dec 16 18:28:03     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.widget");

function zkCmbox() {}

zkCmbox.init = function (cmp) {
	var inp = $real(cmp);
	zkTxbox.init(inp);
	zk.listen(inp, zk.ie ? "keydown": "keypress", zkCmbox.onkey);
		//IE: use keydown. otherwise, it causes the window to scroll
	zk.listen(inp, "click", function () {if (inp.readOnly) zkCmbox.onbutton(cmp);});
		//To mimic SELECT, it drops down if readOnly

	var btn = $e(cmp.id + "!btn");
	if (btn) zk.listen(btn, "click", function () {zkCmbox.onbutton(cmp);});
	btn.align = "absmiddle";
};

function zkCmit() {}

zkCmit.init = function (cmp) {
	zk.listen(cmp, "click", function () {zkCmbox.onclickitem(cmp);});
	zk.listen(cmp, "mouseover", function () {zkCmbox.onover(cmp);});
	zk.listen(cmp, "mouseout", function () {zkCmbox.onout(cmp);});
};

/** Handles setAttr. */
zkCmbox.setAttr = function (cmp, nm, val) {
	if (nm == "repos") { //hilite the most matched item
		var pp = $e(cmp.id + "!pp");
		if (pp) {
			pp.removeAttribute("zk_ckval"); //re-check is required
			if (pp.style.display != "none")
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
	} else if (zkCmbox._inflds.contains(nm)) {
		cmp = $real(cmp);
	}
	zkau.setAttr(cmp, nm, val);
	return true;
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
	} else if (zkCmbox._inflds.contains(nm))
		cmp = $real(cmp);
	zkau.rmAttr(cmp, nm);
	return true;
};
if (!zkCmbox._inflds)
	zkCmbox._inflds = ["name", "value", "cols", "size", "maxlength",
		"type", "disabled", "readonly", "rows"];

/** Eats UP/DN keys. */
zkCmbox.ondown = function (evt) {
	//IE: if NOT eat UP/DN here, it de-select the text
	//IE: if we eat UP/DN here, onpress won't be sent
	//IE: we have to handle UP/DN in onup (so the repeat feature is lost)
	if (evt.keyCode == 38 || evt.keyCode == 40) { //UP and DN
		Event.stop(evt);
		return false;
	}
	if (evt.keyCode == 9) { //TAB; IE: close now so to show covered SELECT
		var inp = Event.element(evt);
		if (inp) {
			var uuid = $uuid(inp.id);
			var pp = $e(uuid + "!pp");
			if (pp && pp.style.display != "none") zkCmbox.close(pp);
		}
	}
	return true;
};
zkCmbox.onkey = function (evt) {
	var inp = Event.element(evt);
	if (!inp) return true;

	var uuid = $uuid(inp.id);
	var cb = $e(uuid);
	var pp = $e(uuid + "!pp");
	if (!pp) return true;

	var opened = pp.style.display != "none";
	if (evt.keyCode == 9) { //TAB
		if (opened) zkCmbox.close(pp);
		return true;
	}

	if (evt.altKey && (evt.keyCode == 38 || evt.keyCode == 40)) {//UP/DN
		if (evt.keyCode == 38) { //UP
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
	if (opened && evt.keyCode == 13) { //ENTER
		zkTxbox.updateChange(inp, false); //fire onChange
		return true;
	}

	if (evt.keyCode == 18 || evt.keyCode == 27
	|| (evt.keyCode >= 112 && evt.keyCode <= 123)) //ALT and ESC, Fn
		return true; //ignore it (doc will handle it)

	var bCombobox = $type(cb) == "Cmbox";
	var selback = evt.keyCode == 38 || evt.keyCode == 40; //UP and DN
	if (getZKAttr(cb, "adr") == "true" && !opened)
		zkCmbox.open(pp, bCombobox && !selback);
	else if (!bCombobox)
		return true; //ignore
	else if (!selback && opened)
		setTimeout("zkCmbox._hilite('"+uuid+"')", 1); //IE: keydown

	if (selback/* || getZKAttr(cb, "aco") == "true"*/) {
		//Note: zkCmbox.open won't repos immediately, so we have to delay it
		setTimeout("zkCmbox._hilite('"+uuid+"',true,"+(evt.keyCode == 38)+")", 3);
		Event.stop(evt);
		return false;
	}
	return true;
};

/* Whn the button is clicked on button. */
zkCmbox.onbutton = function (cmp) {
	var pp = $e(cmp.id + "!pp");
	if (pp) {
		if (pp.style.display == "none") zkCmbox.open(pp, true);
		else zkCmbox.close(pp, true);
	}
};

/** When an item is clicked. */
zkCmbox.onclickitem = function (item) {
	zkCmbox._selback(item);
	zkau.closeFloats(item);
	zkCmbox.onout(item); //onmouseout might be sent (especiall we change parent)

	//Request 1537962: better responsive
	var inp = zkCmbox.getInputByItem(item);
	if (inp) zkTxbox.updateChange(inp, false); //fire onChange
};
/** onmouoseover(el). */
zkCmbox.onover = function (el) {
	if (!zk.dragging) {
		zk.backupStyle(el, "backgroundColor");
		el.style.backgroundColor =
			el.className.endsWith("sel") ? "#115588": "#DAE8FF";
	}
};
/** onmouseout(el). */
zkCmbox.onout = function (el) {
	if (!zk.dragging) zk.restoreStyle(el, "backgroundColor");
};
/** Marks an item as selected or un-selected. */
zkCmbox._setsel = function (item, sel) {
	var clsnm = item.className;
	if (sel) {
		if (!clsnm.endsWith("sel"))
			item.className = clsnm + "sel";
	} else {
		if (clsnm.endsWith("sel"))
			item.className = clsnm.substring(0, clsnm.length - 3);
	}
};

/** Returns the text contained in the specified item. */
zkCmbox.getLabel = function (item) {
	return item && item.cells && item.cells.length > 1 ?
		zk.getElementValue(item.cells[1]): ""; 
};

zkCmbox.open = function (pp, hilite) {
	pp = $e(pp);
	zkau.closeFloats(pp); //including popups
	zkCmbox._pop._popupId = pp.id;

	var uuid = $uuid(pp.id);
	var cb = $e(uuid);
	if (!cb) return;

	zkCmbox._open(cb, uuid, pp, hilite);

	zkau.send({uuid: uuid, cmd: "onOpen", data: [true]},
		zkau.asapTimeout(cb, "onOpen"));
};
zkCmbox._open = function (cb, uuid, pp, hilite) {
	var pp2 = $e(uuid + "!cave");
	pp.style.width = pp.style.height = "auto";
	if (pp2) pp2.style.width = pp2.style.height = "auto";
	pp.style.position = "absolute"; //just in case
	pp.style.overflow = "auto"; //just in case
	pp.style.display = "block";
	pp.style.zIndex = "80000";
	zkau.onVisiChildren(pp);

	if (zk.gecko) {
		setZKAttr(pp, "vparent", uuid); //used by zkTxbox._noonblur
		document.body.appendChild(pp); //Bug 1486840
		//However, since the parent/child relation is changed, new listitem
		//must be inserted into the popup (by use of uuid!child) rather
		//than invalidate!!
	}

	//fix size
	if (pp.offsetHeight > 200) {
		pp.style.height = "200px";
		pp.style.width = "auto"; //recalc
	} else if (pp.offsetHeight < 10) {
		pp.style.height = "10px"; //minimal
	}
	if (pp.offsetWidth < cb.offsetWidth) {
		pp.style.width = cb.offsetWidth + "px";
		if (pp2) pp2.style.width = "100%";
			//Note: we have to set width to auto and then 100%
			//Otherwise, IE won't behave correctly.
	} else {
		var wd = zk.innerWidth() - 20;
		if (wd < cb.offsetWidth) wd = cb.offsetWidth;
		if (pp.offsetWidth > wd) pp.style.width = wd;
	}

	zk.position(pp, cb, "after-start");

	setTimeout("zkCmbox._repos('"+uuid+"',"+hilite+")", 3);
		//IE issue: we have to re-position again because some dimensions
		//might not be correct here
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

	zk.position(pp, cb, "after-start");
	zkau.hideCovered();
	zk.focusById(inpId);

	if (hilite) zkCmbox._hilite(uuid);
};

/** Selects back the specified item. */
zkCmbox._selback = function (item) {
	var txt = zkCmbox.getLabel(item); 

	var inp = zkCmbox.getInputByItem(item);
	if (inp) {
		inp.value = txt;
		zk.focusById(inp.id);
		zk.selectById(inp.id);
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
zkCmbox._hilite = function (uuid, selback, bUp) {
	var inp = $e(uuid + "!real");
	if (!inp) return;

//	var aco = getZKAttr($e(uuid), "aco") == "true";
	var pp = $e(uuid + "!pp");
	if (!pp || (!selback && /*!aco &&*/ !zk.isVisible(pp))) return;
	var pp2 = $e(uuid + "!cave");
	if (!pp2) return;
	var rows = pp2.rows;
	if (!rows) return;

	//The comparison is case-insensitive
	var inpval = inp.value.toLowerCase();
	if (!selback && pp.getAttribute("zk_ckval") == inpval)
		return; //not changed

	//Identify the best matched item
	var jfnd = -1, exact = !inpval, old;
	for (var j = 0; j < rows.length; ++j) {
		var item = rows[j];
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
			if (rows.length) found = rows[0];
		} else {
			if (exact) {
				var b = document.selection;
				if ((b && "Text" == b.type && document.selection.createRange().text.toLowerCase() == inpval)
				|| (!b && inpval.length && inp.selectionStart == 0
					&& inp.selectionEnd == inpval.length)) {
					if (bUp) {
						if (jfnd > 0) --jfnd;
					} else {
						if (jfnd + 1 < rows.length) ++jfnd;
					}
				}
			}
			if (jfnd >= 0) found = rows[jfnd];
		}
		if (found) zkCmbox._selback(found);
	} else if (jfnd >= 0) {
		found = rows[jfnd];
	}

	if (old != found) {
		if (old) {
			zkCmbox._setsel(old, false);
			old.removeAttribute("zk_hilite");
		}
		if (found) {
			zkCmbox._setsel(found, true);
			found.setAttribute("zk_hilite", "true");
		}
	}

	zk.scrollIntoView(pp, found); //make sure found is visible

	pp.setAttribute("zk_ckval", inpval);
};

zkCmbox.close = function (pp, focus) {
	pp = $e(pp);
	var uuid = $uuid(pp.id);
	if (zk.gecko) {
		$e(uuid).appendChild(pp); //Bug 1486840
		rmZKAttr(pp, "vparent");
	}

	zkCmbox._pop._popupId = null;
	pp.style.display = "none";
	zkau.hideCovered();

	if (focus)
		zk.focusById(uuid + "!real");
};

zk.FloatCombo = Class.create();
Object.extend(Object.extend(zk.FloatCombo.prototype, zk.Float.prototype), {
	_close: function (el) {
		zkCmbox.close(el);
	}
});
if (!zkCmbox._pop)
	zkau.floats.push(zkCmbox._pop = new zk.FloatCombo()); //hook to zkau.js

//-- bandbox --//
zkBdbox = zkCmbox;
