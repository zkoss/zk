/* sel.js

{{IS_NOTE
	Purpose:
		zk.Selectable
	Description:

	History:
		Fri Aug 26 08:45:55     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul");

////
//Customization
/** Returns the background color for a list item or tree item.
 * Developer can override this method by providing a different background.
 */
if (!window.Selectable_effect) { //define it only if not customized
	window.Selectable_effect = function (row, undo) {
		if (undo) {
			var zcls = getZKAttr(row, "zcls");
			zk.rmClass(row, zcls + "-over-seld");
			zk.rmClass(row, zcls + "-over");
		} else {
			var zcls = getZKAttr(row, "zcls");
			zk.addClass(row, zk.hasClass(row, zcls + "-seld") ? zcls + "-over-seld" : zcls + "-over");
		}
	};
}
var _zkselx = {};
zk.override(zkau.cmd1, "addAft",  _zkselx, function (uuid, cmp, html, pgid) {
	if (!cmp || !_zkselx._addChd(uuid, cmp, html))
		_zkselx.addAft(uuid, cmp, html, pgid);
});
zk.override(zkau.cmd1, "addBfr",  _zkselx, function (uuid, cmp, html) {
	if (!cmp || !_zkselx._addChd(uuid, cmp, html))
		_zkselx.addBfr(uuid, cmp, html);
});
_zkselx._addChd = function (uuid, cmp, html) {
	var h = html.trim(), from = h.indexOf("Lit"),
		isLit = h.indexOf("<tr") == 0 && from > -1 && from < h.indexOf(">"),
		type = $type(cmp);
	if (isLit && (!type || type.indexOf("Lit") < 0)) { // only first listitem.
		var head = $parentByTag(cmp.parentNode, "DIV"), cave = $e($uuid(head) + "!cave");
		if (!cave) return false; // no listbody
		var tBody = cave.tBodies[cave.tBodies.length-1];
		if (tBody.rows.length) {
			var n = tBody.rows[0], to = n.previousSibling;
			zk.insertHTMLBefore(n, html);
			zkau._initSibs(n, to, false);
		} else {
			zk.insertHTMLBeforeEnd(tBody, html);
			zkau._initChildren(tBody);
		}
		return true;
	}
	return false;
};

////
// Seletable //
zk.Selectable = zClass.create();
zk.Selectable.prototype = {
	initialize: function (cmp) {
		this.id = cmp.id;
		zkau.setMeta(cmp, this);
		this.qcells = [];
		this.init();
	},
	init: function () {
		this.element = $e(this.id);
		if (!this.element) return;
		if (getZKAttr(this.element, "vflex") == "true") {
			if (zk.ie) this.element.style.overflow = "hidden";
			// added by Jumper for IE to get a correct offsetHeight so we need
			// to add this command faster than the this._calcSize() function.
			var hgh = this.element.style.height;
			if (!hgh || hgh == "auto") this.element.style.height = "99%"; // avoid border 1px;
		}
		//_headtbl might be null, while other must be NOT null
		this.body = $e(this.id + "!body");
		this.paging = getZKAttr(this.element, "pg") != null;
		if (this.body) {
			this.body.style.overflow = "";
			this.bodytbl = zk.firstChild(this.body, "TABLE", true);

			this.head = $e(this.id + "!head");
			this.foot = $e(this.id + "!foot");
			if (this.foot) this.foottbl = zk.firstChild(this.foot, "TABLE", true);
			if (this.head) {
				this.headtbl = zk.firstChild(this.head, "TABLE", true);
				this.headrows = this.headtbl.tBodies[this.headtbl.tBodies.length-1].rows;
				this.hdfaker = this.headtbl.tBodies[0].rows[0]; // head's faker
				this.bdfaker = this.bodytbl.tBodies[0].rows[0]; // body's faker
				if (this.foot) this.ftfaker = this.foottbl.tBodies[0].rows[0]; // foot's faker
			}

			if (this.bodytbl) {
				var bds = this.bodytbl.tBodies;
				if (!bds || !bds.length|| (this.head && bds.length < 2))
					this.bodytbl.appendChild(document.createElement("TBODY"));
				this.bodyrows = bds[this.head ? 1 : 0].rows;
			}
		}

		if (!this.bodyrows) {
			zk.error(mesg.INVALID_STRUCTURE + this.id);
			return;
		}

		var focusEl = $e(this.id, "a");
		zk.listen(focusEl, "keydown", this.onKeydown);
		zk.listen(focusEl, "keyup", zkLibox.onkeyup);
		zk.listen(focusEl, "blur", this.onBlur);
		var meta = this; //the nested function only see local var
		if (!this._inited) {
			this._inited = true;

			//If Mozilla, we have to eat keystrokes, or the page
			//will scroll when UP/DOWN is pressed
			if (zk.gecko) {
				this.element.onkeydown = this.element.onkeyup
				= this.element.onkeypress = function (evt) {
					var target = Event.element(evt);
					if (zkSel._shallIgnoreEvent(target))
						return true;

					if (evt) {
						switch (Event.keyCode(evt)) {
						case 33: //PgUp
						case 34: //PgDn
						case 38: //UP
						case 40: //DOWN
						case 37: //LEFT
						case 39: //RIGHT
						case 32: //SPACE
						case 36: //Home
						case 35: //End
							Event.stop(evt);
							return false;
						}
					}
					return true;
				};
			}

			this.form = zk.formOf(this.element);
			if (this.form) {
				this.fnSubmit = function () {
					meta.onsubmit();
				};
				zk.listen(this.form, "submit", this.fnSubmit);
			}
		}


		//FF: a small fragment is shown
		//IE: Bug 1775014
		if (this.headtbl && this.headrows.length) {
			var empty = true;
			l_out:
			for (var j = this.headrows.length; j;) {
				var headrow = this.headrows[--j];
				for (var k = headrow.cells.length; k;) {
					var n = headrow.cells[--k].firstChild; // Bug #1819037
					for (n = n ? n.firstChild: n; n; n = n.nextSibling)
						if (!n.id || !n.id.endsWith("!hint")) {
							empty = false;
							break l_out;
						}
				}
			}
			if (empty) this.head.style.display = "none"; // Bug #1819037, #1970048
				//we have to hide if empty (otherwise, a small block is shown)
			else this.head.style.display = "";// Bug #1832359
		}

		this.body.onscroll = function () {
			if(zk.opera) meta._scrollTop = meta.body.scrollTop;
			if (meta.head) meta.head.scrollLeft = meta.body.scrollLeft;
			if (meta.foot) meta.foot.scrollLeft = meta.body.scrollLeft;
			if (!meta.paging) meta._render(zk.gecko ? 200: 60);
				//Moz has a bug to send the request out if we don't wait long enough
				//How long is enough is unknown, but 200 seems fine
		};

	},
	/**
	 * Returns the current focused target. This function is used for zkau._onDocKeydown()
	 * @since 3.5.1
	 */
	getCurrentTarget: function () {
		return this._focusItem;
	},
	onBlur: function (evt) {
		var meta = zkau.getMeta($uuid(Event.element(evt)));
		if (meta) {
			zkSel.cmonblurTo(meta._focusItem);
			meta._lastSelected = meta._focusItem; // we need to store the last selected item for fixing Bug 2465826
			meta._focusItem = null; // reset Bug 2465826
		}
	},
	onKeydown: function (evt) {
		var target = Event.element(evt),
			tag = $tag(target),
			meta = zkau.getMeta($uuid(target));
		
		
		if (meta) {
			
			// Bug 2487562
			if (!zk.gecko3 || (tag != "INPUT" && tag != "TEXTAREA")) {
				zk.disableSelection(meta.element);
			}
			meta.dokeydown(evt, $e(meta._focusItem) || $e(getZKAttr(meta.element, "selId")));
			// sometimes the _focusItem is out of date;
		}
	},
	cleanup: function ()  {
		if (this.fnSubmit)
			zk.unlisten(this.form, "submit", this.fnSubmit);
		this.element = this.body = this.head = this.bodytbl = this.headtbl
			this.foot = this.foottbl = this.fnSubmit = this._focusItem = null;
			//in case: GC not works properly
	},
	/** Stripes the rows. */
	stripe: function () {
		var scOdd = getZKAttr(this.element, "scOddRow");
		if (!scOdd || !this.bodyrows) return;
		for (var j = 0, even = true, bl = this.bodyrows.length; j < bl; ++j) {
			var row = this.bodyrows[j];
			if ($visible(row) && getZKAttr(row, "nostripe") != "true") {
				zk.addClass(row, scOdd, !even);
				even = !even;
			}
		}
	},

	/** Handles keydown sent to the body. */
	dobodykeydown: function (evt, target) {
		if (zkSel._shallIgnoreEvent(target))
			return true;

	// Note: We don't intercept body's onfocus to gain focus back to anchor.
	// Otherwise, it cause scroll problem on IE:
	// When user clicks on the scrollbar, it scrolls first and call onfocus,
	// then it will scroll back to the focus because _focusToAnc is called
		switch (Event.keyCode(evt)) {
		case 33: //PgUp
		case 34: //PgDn
		case 38: //UP
		case 40: //DOWN
		case 37: //LEFT
		case 39: //RIGHT
		case 32: //SPACE
		case 36: //Home
		case 35: //End
			if ($tag(target) != "A")
				this._refocus();
			Event.stop(evt);
			return false;
		}
		return true;
	},
	/** Handles the keydown sent to the row. */
	dokeydown: function (evt, target) {
		if (zkau.processing() || zkSel._shallIgnoreEvent(target))
			return true;

		var row = $tag(target) == "TR" ? target: zk.parentNode(target, "TR");
		if (!row) return true;
		row = $e(row); // restore for Live Data
		var shift = evt.shiftKey, ctrl = evt.ctrlKey;
		if (shift && !this._isMultiple())
			shift = false; //OK to

		var endless = false, step, lastrow;
		switch (Event.keyCode(evt)) {
		case 33: //PgUp
		case 34: //PgDn
			step = this.realsize();
			if (step == 0) step = 20;
			if (Event.keyCode(evt) == 33)
				step = -step;
			break;
		case 38: //UP
		case 40: //DOWN
			step = Event.keyCode(evt) == 40 ? 1: -1;
			break;
		case 32: //SPACE
			if (this._isMultiple()) this.toggleSelect(row, !this._isSelected(row), evt);
			else this.select(row, zkau.getKeys(evt));
			break;
		case 36: //Home
		case 35: //End
			step = Event.keyCode(evt) == 35 ? 1: -1;
			endless = true;
			break;
		case 37: //LEFT
			this._doLeft(row);
			break;
		case 39: //RIGHT
			this._doRight(row);
			break;
		}

		if (step) {
			if (shift) this.toggleSelect(row, true, evt);
			for (; (row = step > 0 ? row.nextSibling: row.previousSibling) != null;) {
				if ($tag(row) == "TR"  && this._isValid(row) && !getZKAttr(row, "disd")) {
					if (shift) this.toggleSelect(row, true, evt);

					if ($visible(row)) {
						if (!shift) lastrow = row;
						if (!endless) {
							if (step > 0) --step;
							else ++step;
							if (step == 0) break;
						}
					}
				}
			}
		}
		if (lastrow) {
			if (ctrl) this.focus(lastrow);
			else this.select(lastrow, zkau.getKeys(evt));
			this._syncFocus(lastrow);
			//have to check each parent, may call zk.scrollIntoView several times sync with zk5
			zkau.cmd0.scrollIntoView(lastrow);
		}

		switch (Event.keyCode(evt)) {
		case 33: //PgUp
		case 34: //PgDn
		case 38: //UP
		case 40: //DOWN
		case 37: //LEFT
		case 39: //RIGHT
		case 32: //SPACE
		case 36: //Home
		case 35: //End
			Event.stop(evt);
			return false;
		}
		return true;
	},
	/** Do when the left key is pressed. */
	_doLeft: function (row) {
	},
	/** Do when the right key is pressed. */
	_doRight: function (row) {
	},
	/** Returns whether the type of the row is "Lit" or "Litgp" or "Litgpft". */
	_isRowType: function (row) {
		var type = $type(row);
		return type == "Lit" || type == "Litgp" || type == "Litgpft";
	},
	doclick: function (evt, target) {
		if (zkSel._shallIgnoreEvent(target))
			return;
		var tn = $tag(target);
		if((tn != "TR" && target.onclick)
		|| tn == "A" || getZKAttr(target, "lfclk") || getZKAttr(target, "dbclk"))
			return;

		var checkmark = target.id && target.id.endsWith("!cm");
		var row = zkSel.getNearestRow(target);
		if (!row || !this._isRowType(row) || getZKAttr(row, "rid") != this.id)
			return; //incomplete structure or grid in listbox...

		//It is better not to change selection only if dragging selected
		//(like Windows does)
		//However, FF won't fire onclick if dragging, so the spec is
		//not to change selection if dragging (selected or not)
		if (zk.dragging /*&& this._isSelected(row)*/)
			return;
		if (checkmark) {
			if (this._isMultiple()) {
				this.toggleSelect(row, target.checked, evt);
			} else {
				this.select(row, zkau.getKeys(evt));
			}
		} else {
		//Bug 1650540: double click as select again
		//Note: we don't handle if clicking on checkmark, since FF always
		//toggle and it causes incosistency
			if ((zk.gecko || zk.safari) && getZKAttr(row, "dbclk")) {
				var now = $now(), last = row._last;
				row._last = now;
				if (last && now - last < 900)
					return; //ignore double-click
			}
			this._syncFocus(row);
			if (this._isMultiple()) {
				if (evt && evt.shiftKey)
					this.selectUpto(row, evt);
				else if (evt && evt.ctrlKey)
					this.toggleSelect(row, !this._isSelected(row), evt);
				else // Bug: 1973470
					this.select(row, zkau.getKeys(evt));
			} else
				this.select(row, zkau.getKeys(evt));

			//since row might was selected, we always enfoce focus here
			this._focusToAnc(row);
			//if (evt) Event.stop(evt);
			//No much reason to eat the event.
			//Oppositely, it disabled popup (bug 1578659)
		}
	},
	/** maintain the offset of the focus proxy*/
	_syncFocus: function (row) {
		var focusEl = $e(getZKAttr(row, "rid"), "a"),
			offs = zk.revisedOffset(row);
		offs = this._toStyleOffset(focusEl, offs[0] + this.body.scrollLeft, offs[1]);
		focusEl.style.top = offs[1] + "px";
		focusEl.style.left = offs[0] + "px";
	},
	_toStyleOffset: function (el, x, y) {
		var ofs1 = zk.revisedOffset(el),
			ofs2 = zk.getStyleOffset(el);
		return [x - ofs1[0] + ofs2[0], y  - ofs1[1] + ofs2[1]];
	},
	/** Returns # of rows allowed. */
	size: function () {
		var sz = getZKAttr(this.element, "size");
		return sz ? $int(sz): 0;
	},
	/** Returns the real # of rows (aka., real size). */
	realsize: function (v) {
		if ("number" == typeof v) {
			this.element.setAttribute("zk_realsize", v);
		} else {
			var sz = this.size();
			if (sz) return sz;
			sz = this.element.getAttribute("zk_realsize");
			return sz ? $int(sz): 0;
		}
	},

	/** Re-setfocus to the anchor who shall be in focus. */
	_refocus: function () {
		for (var j = 0, bl = this.bodyrows.length; j < bl; ++j) {
			var r = this.bodyrows[j];
			if (this._isFocus(r)) {
				this._focusToAnc(r);
				break;
			}
		}
		var focusEl = $e(this.id, "a");
		if (focusEl && zkau.canFocus(focusEl))
			zk.asyncFocus(focusEl.id);

	},
	/** Process the setAttr command sent from the server. */
	setAttr: function (nm, val) {
		switch (nm) {
		case "z.innerWidth":
			if (this.headtbl) this.headtbl.style.width = val;
			if (this.bodytbl) this.bodytbl.style.width = val;
			if (this.foottbl) this.foottbl.style.width = val;
			return true;
		case "select": //select by uuid
			var row = $e(val);
			this._selectOne(row, false);
			zk.scrollIntoView(this.body, row);
			return true;
		case "selectAll":
			this._selectAll();
			return true; //no more processing
		case "z.multiple": //whether to support multiple
			this._setMultiple("true" == val);
			return true;
		case "chgSel": //val: a list of uuid to select
			var sels = {};
			for (var j = 0;;) {
				var k = val.indexOf(',', j);
				var s = (k >= 0 ? val.substring(j, k): val.substring(j)).trim();
				if (s) sels[s] = true;
				if (k < 0) break;
				j = k + 1;
			}

			var rows = this.bodyrows;
			for (var j = 0, rl = rows.length; j < rl; ++j)
				this._changeSelect(rows[j], sels[rows[j].id] == true);
			return true;
		case "z.vflex":
		if (val == "true") {
			if (zk.ie) this.element.style.overflow = "hidden";
			// added by Jumper for IE to get a correct offsetHeight so we need
			// to add this command faster than the this._calcSize() function.
			var hgh = this.element.style.height;
			if (!hgh || hgh == "auto") this.element.style.height = "99%"; // avoid border 1px;
		} else {
			if (zk.ie) this.element.style.overflow = ""; // cleanup style
		}
		case "z.size":
			zkau.setAttr(this.element, nm, val);
			this._recalcSize();
			return true;
		case "style.width":
			if (this.headtbl) this.headtbl.style.width = "";
			if (this.foottbl) this.foottbl.style.width = "";
		case "style":
		case "style.height":
			zkau.setAttr(this.element, nm, val);
			this._recalcSize();
			return true;
		case "scrollTop":
			if (this.body) {
				this.body.scrollTop = val;
				return true;
			}
			break;
		case "scrollLeft":
			if (this.body) {
				this.body.scrollLeft = val;
				return true;
			}
			break;
		case "z.scOddRow":
			zkau.setAttr(this.element, nm, val);
			this.stripe();
			return true;
		case "z.render":
			this._render(0);
			return true;
		}
		return false;
	},
	/** Returns the item's UUID containing the specified row. */
	getItemUuid: function (row) {
		return row.id;
	},
	/** Selects an item, notify server and change focus if necessary. */
	select: function (row, keys) {
		if (this._selectOne(row, true)) {
			//notify server
			zkau.sendasap({uuid: this.id, cmd: "onSelect",
				data: [this.getItemUuid(row), this.getItemUuid(row), keys||'']});
		}
	},
	/** Toggle the selection and notifies server. */
	toggleSelect: function (row, toSel, evt) {
		this._changeSelect(row, toSel);
		this.focus(row);

		//maintain z.selId
		var selId = this._getSelectedId();
		if (this._isMultiple()) {
			if (row.id == selId)
				this._fixSelelectedId();
		} else if (selId) {
			var sel = $e(selId);
			if (sel) this._changeSelect(sel, false);
		}

		//notify server
		this._sendSelect(row, evt);
	},
	/** Selects a range from the last focus up to the specified one.
	 * Callable only if multiple
	 */
	selectUpto: function (row, evt) {
		if (this._isSelected(row)) {
			this.focus(row);
			return; //nothing changed
		}

		var focusfound = false, rowfound = false;
		for (var j = 0, bl = this.bodyrows.length; j < bl; ++j) {
			var r = this.bodyrows[j];
			if (getZKAttr(r, "disd")) continue; // Bug: 2030986
			if (focusfound) {
				this._changeSelect(r, true);
				if (r == row)
					break;
			} else if (rowfound) {
				this._changeSelect(r, true);
				if (this._isFocus(r) || r == this._lastSelected)
					break;
			} else {
				rowfound = r == row;
				focusfound = this._isFocus(r) || r == this._lastSelected;
				if (rowfound || focusfound) {
					this._changeSelect(r, true);
					if (rowfound && focusfound)
						break;
				}
			}
		}

		this.focus(row);
		this._fixSelelectedId();
		this._sendSelect(row, evt);
	},

	/** Changes the specified row as focused. */
	focus: function (row) {
		if (zkau.canFocus(row, true)) {
			this._unsetFocusExcept(row);
			this._setFocus(row, true);
		}
	},
	/** Sets focus to the specified row if it has the anchor. */
	_focusToAnc: function (row) {
		if (row && zkau.canFocus(row, true)) {
			var uuid = typeof row == 'string' ? row: row.id,
				el = $e(uuid + "!cm");
			if ((el && el.tabIndex != -1) || (el = $e(getZKAttr(row, "rid"), "a"))) {//disabled due to modal, see zk.disableAll
				zk.asyncFocus(el.id);
				zkSel.cmonfocusTo(row);
			}
		}
	},

	/** Selects one and deselect others, and return whehter any changes.
	 * It won't notify the server.
	 * @param row the row to select. Unselect all if null
	 * @param toFocus whether to change focus
	 */
	_selectOne: function (row, toFocus) {
		row = $e(row);

		var selId = this._getSelectedId();

		if (this._isMultiple()) {
			if (row && toFocus) this._unsetFocusExcept(row);
			var changed = this._unsetSelectAllExcept(row);
			if (!changed && row && selId == row.id) {
				if (toFocus) this._setFocus(row, true);
				return false; //not changed
			}
		} else {
			if (selId) {
				if (row && selId == row.id) {
					if (toFocus) this._setFocus(row, true);
					return false; //not changed
				}

				var sel = $e(selId);
				if (sel) {
					this._changeSelect(sel, false);
					if (row)
						if(toFocus) this._setFocus(sel, false);
				}
			}
			if (row && toFocus) this._unsetFocusExcept(row);
		}
		//we always invoke _changeSelect to change focus
		if (row) {
			this._changeSelect(row, true);
			if (toFocus) this._setFocus(row, true);
			this._setSelectedId(row.id);
		} else {
			this._setSelectedId(null);
		}
		return true;
	},

	/** Changes the selected status of an item without affecting other items
	 * and return true if the status is really changed.
	 */
	_changeSelect: function (row, toSel) {
		if (!this._isValid(row)) return false;

		var changed = this._isSelected(row) != toSel;
		if (changed) {
			var el = $e(row.id + "!cm"),
				zcls = getZKAttr(row, "zcls");
			if (toSel) {
				if (el) el.checked = true;
				zk.addClass(row, zcls + "-seld");
				zkSel.onoutTo(row);
				setZKAttr(row, "sel", "true");
			} else {
				if (el) el.checked = false;
				zk.rmClass(row, zcls + "-seld");
				zkSel.onoutTo(row);
				setZKAttr(row, "sel", "false");
			}
			if (el) zkLcfc[this._isMultiple() ? "checkAll" : "onRadioClick"](this, el);
		}
		return changed;
	},
	/** Changes the focus status, and return whether it is changed. */
	_setFocus: function (row, toFocus) {
		if (!this._isValid(row)) return false;
		var changed = this._isFocus(row) != toFocus;
		if (changed) {
			if (toFocus) {
				var el = $e(row.id + "!cm");
				if ((el && el.tabIndex != -1) || (el = $e(this.id, "a"))) //disabled due to modal, see zk.disableAll
					zk.asyncFocus(el.id);
				zkSel.cmonfocusTo(row);

				if (!this.paging && zk.gecko) this._render(5);
					//Firefox doesn't call onscroll when we moving by cursor, so...

				this._focusItem = row;
			} else {
				zkSel.cmonblurTo(row);
			}
		}
		return changed;
	},
	/** Cleans selected except the specified one, and returns any selected status
	 * is changed.
	 */
	_unsetSelectAllExcept: function (row) {
		var changed = false;
		for (var j = 0, bl = this.bodyrows.length; j < bl; ++j) {
			var r = this.bodyrows[j];
			if (r != row && this._changeSelect(r, false))
				changed = true;
		}
		return changed;
	},
	/** Cleans selected except the specified one, and returns any selected status
	 * is changed.
	 */
	_unsetFocusExcept: function (row) {
		return this._focusItem && this._focusItem != row ? this._setFocus(this._focusItem, false): this._focusItem = null;
	},
	/** Renders listitems that become visible by scrolling.
	 */
	_render: function (timeout) {
		if(!this.paging || getZKAttr(this.element, "hasgroup"))
			setTimeout("zkSel._renderNow('"+this.id+"')", timeout);
	},
	_renderNow: function () {
		var rows = this.bodyrows;
		if (!rows || !rows.length || getZKAttr(this.element, "model") != "true") return;

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var data = "",
			min = this.body.scrollTop,
			max = min + this.body.offsetHeight;
		for (var j = 0, rl = rows.length; j < rl; ++j) {
			var r = rows[j];
			if ($visible(r)) {
				var top = zk.offsetTop(r);
				if (top + zk.offsetHeight(r) < min) continue;
				if (top > max) break; //Bug 1822517: max might be 0
				if (getZKAttr(r, "loaded") != "true")
					data += "," + this.getItemUuid(r);
				else if (getZKAttr(r, "inited") != "true") zk.initAt(r);
			}
		}
		if (data) {
			data = data.substring(1);
			zkau.send({uuid: this.id, cmd: "onRender", data: [data]}, 0);
		}
	},

	/** Calculates the size. */
	_calcSize: function () {
		this._calcHgh();
		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		//
		//Bug 1616056: we have to use style.width, if possible, since clientWidth
		//is sometime too big
		var wd = this.element.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			wd = zk.revisedSize(this.element, this.element.offsetWidth);
			if (wd < 0) wd = 0;
			if (wd) wd += "px";
		}
		if (wd) {
			this.body.style.width = wd;
			if (this.head) this.head.style.width = wd;
			if (this.foot) this.foot.style.width = wd;
		}

		var tblwd = this.body.clientWidth;
		if (zk.ie) {//By experimental: see zk-blog.txt
			if (this.headtbl && this.headtbl.offsetWidth != this.bodytbl.offsetWidth) 
				this.bodytbl.style.width = ""; //reset
			if (tblwd && this.body.offsetWidth == this.bodytbl.offsetWidth && this.body.offsetWidth - tblwd > 11) {
				if (--tblwd < 0) 
					tblwd = 0;
				this.bodytbl.style.width = tblwd + "px";
			}
			
			// bug #2799258
			var hgh = this.element.style.height;
			if (!hgh || hgh == "auto") {
				if (this._visihgh !== null && (this.body.offsetHeight - this.body.clientHeight)
						< zk.getScrollBarWidth()) 
						this.body.style.height = this._visihgh + "px"; 
					
				// resync
				tblwd = this.body.clientWidth;
			}
		}
		if (this.headtbl) {
			if (tblwd) this.head.style.width = tblwd + 'px';
			if (getZKAttr(this.element, "fixed") != "true")
				zul.adjustHeadWidth(this.hdfaker, this.bdfaker, this.ftfaker, this.bodyrows);
			else if (tblwd && this.foot) this.foot.style.width = tblwd + 'px';
		} else if (this.foottbl) {
			if (tblwd) this.foot.style.width = tblwd + 'px';
			if (this.foottbl.rows.length)
				zk.cpCellWidth(this.foottbl.rows[0], this.bodyrows, this); //assign foot's col width
		}
	},
	_beforeSize: function () {
		var wd = this.element.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			if (this.body) this.body.style.width = "";
			if (this.head) this.head.style.width = "";
			if (this.foot) this.foot.style.width = "";
		}
	},
	_recalcSize: function () {
		if (zk.isRealVisible(this.element)) {
			this._calcSize();// Bug #1813722
			this._render(155);
			// it seems no longer to be fixed with this, commented by jumperchen on 2009/05/26
			if (zk.ie7 && this.paging) zk.redoCSS(this.element); // Bug 2096807 && Test Case Z35-listbox-0002.zul
		}
	},
	/** Returns the visible row at the specified index. */
	_visiRowAt: function (index) {
		if (index >= 0) {
			var rows = this.bodyrows;
			for (var j = 0, rl = rows.length; j < rl; ++j) {
				var r = rows[j];
				if ($visible(r) && --index < 0)
					return r;
			}
		}
		return null;
	},
	_calcHgh: function () {
		var rows = this.bodyrows,
			hgh = this.element.style.height,
			isHgh = hgh && hgh != "auto" && hgh.indexOf('%') < 0;
		this._visihgh = null;
		if (isHgh) {
			hgh = $int(hgh);
			if (hgh) {
				hgh -= this._headHgh(0);
				if (hgh < 20) hgh = 20;
				var sz = 0;
				l_out:
				for (var h, j = 0, rl = rows.length; j < rl; ++sz, ++j) {
					//next visible row
					var r;
					for (;; ++j) {//no need to check length again
						if (j >= rl) break l_out;
						r = rows[j];
						if ($visible(r)) break;
					}

					h = zk.offsetTop(r) + zk.offsetHeight(r);
					if (h >= hgh) {
						if (h > hgh + 2) ++sz; //experimental
						break;
					}
				}
				sz = Math.ceil(sz && h ? (hgh * sz)/h: hgh/this._headHgh(20));

				this.realsize(sz);

                hgh -= (this.foot ? this.foot.offsetHeight : 0);
                this.body.style.height = (hgh < 0 ? 0 : hgh) + "px";

				//2007/12/20 We don't need to invoke the body.offsetHeight to avoid a performance issue for FF.
				if (zk.ie && this.body.offsetHeight) {} // bug #1812001.
				// note: we have to invoke the body.offestHeight to resolve the scrollbar disappearing in IE6
				// and IE7 at initializing phase.
				return; //done
			}
		}

		var nVisiRows = 0, nRows = this.size(), lastVisiRow, firstVisiRow, midVisiRow;
		for (var j = 0, rl = rows.length; j < rl; ++j) { //tree might collapse some items
			var r = rows[j];
			if ($visible(r)) {
				++nVisiRows;
				if (!firstVisiRow) firstVisiRow = r;

				if (nRows === nVisiRows) {
					midVisiRow = r;
					break;
					//nVisiRows and lastVisiRow useful only if nRows is larger,
					//so ok to break here
				}
				lastVisiRow = r;
			}
		}

		hgh = 0;
		var diff = 2/*experiment*/;
		if (!nRows) {
			if (getZKAttr(this.element, "vflex") == "true") {
				hgh = this._vflexSize();

				if (zk.ie && $int(getZKAttr(this.element, "hgh")) != hgh) {
					hgh -= 1; // need to display the bottom border.
					setZKAttr(this.element, "hgh", hgh);
				}
				if (hgh < 25) hgh = 25;

				var rowhgh = zk.offsetHeight(firstVisiRow);
				if (!rowhgh) rowhgh = this._headHgh(20);

				nRows = Math.round((hgh - diff)/ rowhgh);
			}
			this.realsize(nRows);
		}

		if (nRows) {
			if (!hgh) {
				if (!nVisiRows) hgh = this._headHgh(20) * nRows;
				else if (nRows <= nVisiRows) {
					hgh = zk.offsetTop(midVisiRow) + zk.offsetHeight(midVisiRow);
				} else {
					hgh = zk.offsetTop(lastVisiRow) + zk.offsetHeight(lastVisiRow);
					hgh = Math.ceil((nRows * hgh) / nVisiRows);
				}
				if (zk.ie) hgh += diff; //strange in IE (or scrollbar shown)
			}

			this.body.style.height = hgh + "px";

			//2007/12/20 We don't need to invoke the body.offsetHeight to avoid a performance issue for FF.
			if (zk.ie && this.body.offsetHeight) {} // bug #1812001.
			// note: we have to invoke the body.offestHeight to resolve the scrollbar disappearing in IE6
			// and IE7 at initializing phase.
			// 2008/03/28 The unnecessary scroll bar will appear when the vflex is true.
			
			// bug #2799258
			var h = this.element.style.height;
			if (!h || h == "auto") {
				h = this.body.offsetHeight - this.body.clientHeight;
				if (this.bodyrows.length && h > 11) {
					this.body.style.height = hgh + zk.getScrollBarWidth() + "px";
					this._visihgh = hgh;// cache
				}
			}
		} else {
			//if no hgh but with horz scrollbar, IE will show vertical scrollbar, too
			//To fix the bug, we extend the height
			hgh = this.element.style.height
			if (zk.ie && (!hgh || hgh == "auto")
			&& this.body.offsetWidth - this.body.clientWidth > 11) {
				if (!nVisiRows) this.body.style.height = ""; // bug #1806152 if start with 0px and no hgh, IE doesn't calculate the height of the element.
				else this.body.style.height =
						(this.body.offsetHeight) + "px";
			} else {
				this.body.style.height = "";
			}
			// bug #2799258
			if (!hgh || hgh == "auto") {
				hgh = this.body.offsetHeight - this.body.clientHeight;
				if (this.bodyrows.length && hgh > 11) {
					this._visihgh = this.body.offsetHeight; // cache
					this.body.style.height = this.body.offsetHeight + zk.getScrollBarWidth() + "px";
				}
			}
		}
	},
	/* Height of the head row. If now header, defval is returned. */
	_headHgh: function (defVal) {
		var hgh = this.head ? this.head.offsetHeight : 0;
		if (this.paging) {
			var pgit = $e(this.id + "!pgit"), pgib = $e(this.id + "!pgib");
			if (pgit) hgh += pgit.offsetHeight;
			if (pgib) hgh += pgib.offsetHeight;
		}
		return hgh ? hgh: defVal;
	},
	/** Returns the size for vflex
	 */
	_vflexSize: function () {
		if (zk.ie6Only) {
			// ie6 must reset the height of the element,
			// otherwise its offsetHeight might be wrong.
			var hgh = this.element.style.height;
			this.element.style.height = "";
			this.element.style.height = hgh;
		}
		var pgHgh = 0;
		if (this.paging) {
			var pgit = $e(this.id + "!pgit"), pgib = $e(this.id + "!pgib");
			if (pgit) pgHgh += pgit.offsetHeight;
			if (pgib) pgHgh += pgib.offsetHeight;
		}
		return this.element.offsetHeight - 2 - (this.head ? this.head.offsetHeight : 0)
			- (this.foot ? this.foot.offsetHeight : 0) - pgHgh; // Bug #1815882
	},
	/** Resize the specified column. */
	resizeCol: function (cmp, icol, col, wd, keys) {
		if (this.bodyrows)
			zulHdr.resizeAll(this, cmp, icol, col, wd, keys);
	},

	/** Sels all items (don't notify server and change focus, because it is from server). */
	_selectAll: function (notify, evt) {
		var rows = this.bodyrows;
		for (var j = 0, rl = rows.length; j < rl; ++j)
			if (!getZKAttr(rows[j], "disd"))
				this._changeSelect(rows[j], true);
		this._setSelectedId(rows.length ? rows[0].id: null);
		if (notify) this._sendSelect(rows[0], evt);
	},

	/** Notifies the server the selection is changed (callable only if multiple). */
	_sendSelect: function (row, evt) {
		//To reduce # of bytes to send, we use a string instead of array.
		var data = "";
		for (var j = 0, bl = this.bodyrows.length; j < bl; ++j) {
			var r = this.bodyrows[j];
			if (this._isSelected(r))
				data += "," + this.getItemUuid(r);
		}
		if (data) data = data.substring(1);
		zkau.sendasap({uuid: this.id, cmd: "onSelect",
			data: [data, row ? this.getItemUuid(row) : "", zkau.getKeys(evt), evt ? Event.element(evt).id.endsWith("!cm") : null]});
	},

	/** Returns z.selId (aka., the id of the selected item), or null if
	 * no one is ever selected.
	 */
	_getSelectedId: function () {
		var selId = getZKAttr(this.element, "selId");
		if (!selId) {
			zk.error(mesg.INVALID_STRUCTURE + "z.selId not found");
			return null;
		}
		return selId == "zk_n_a" ? null: selId;
	},
	/** Sets z.selId (aka., the id of the selected item). */
	_setSelectedId: function (selId) {
		setZKAttr(this.element, "selId", selId ? selId: "zk_n_a");
	},
	/** Fixes z.selId to the first selected item. */
	_fixSelelectedId: function () {
		var selId = null;
		for (var j = 0, bl = this.bodyrows.length; j < bl; ++j) {
			var r = this.bodyrows[j];
			if (this._isSelected(r)) {
				selId = r.id;
				break;
			}
		}
		this._setSelectedId(selId);
	},

	/** Whether an item is selected. */
	_isSelected: function (row) {
		return getZKAttr(row, "sel") == "true";
	},
	/** Whether an item has focus. */
	_isFocus: function (row) {
		return this._focusItem == row;
	},
	/** Whether the component is multiple.
	 */
	_isMultiple: function () {
		return getZKAttr(this.element, "multiple") == "true";
	},
	/** Changes the multiple status. Note: it won't notify the server any change
	 */
	_setMultiple: function (multiple) {
		setZKAttr(this.element, "multiple", multiple ? "true": "false");
		if (!multiple) {
			this._fixSelelectedId();
			var row = $e(this._getSelectedId());
			this._unsetSelectAllExcept(row);
				//no need to unfocus because we don't want to change focus
		}
	},
	/** Returns whether the row is valid. */
	_isValid: function (row) {
		return row && !row.id.endsWith("!child") && !row.id.endsWith("!ph") && !row.id.endsWith("!pt");
	},

	/** Called when the form enclosing it is submitting. */
	onsubmit: function () {
		var nm = getZKAttr(this.element, "name");
		if (!nm || !this.form) return;

		for (var j = 0, fl = this.form.elements.length; j < fl; ++j){
			var el = this.form.elements[j];
			if (getZKAttr(el, "hiddenBy") == this.id) {
				zk.remove(el);
				--j;
			}
		}

		for (var j = 0, bl = this.bodyrows.length; j < bl; ++j) {
			var r = this.bodyrows[j];
			if (this._isSelected(r))
				setZKAttr(
					zk.newHidden(nm, getZKAttr(r, "value"), this.form),
					"hiddenBy", this.id);
		}
	}
};

////
// Utilities to help implement zk.Selectable //
zkSel = {
	/**
	 * Returns the nearest row element that is allowed by zk.Selectable._isRowType(),
	 * including row itself, recursively from row's parent node.
	 * @since 3.0.7
	 */
	getNearestRow: function (row) {
		var row = $parentByTag(row, "TR");
		while(row) {
			var rid = getZKAttr(row, "rid");
			if (rid) {
				var meta = zkau.getMeta(rid);
				if (meta && meta._isRowType)
					if (meta._isRowType(row))
						break;
			}
			row = $parentByTag(row.parentNode, "TR");
		}
		return row;
	}
};

zkSel._init = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._init();
};
zkSel._calcSize = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._calcSize();
};
zkSel._renderNow = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._renderNow();
};
zkSel._shallIgnoreEvent = function (el) {
	var tn = $tag(el);
	return !el || !zkau.canFocus(el)
	|| ((tn == "INPUT" && !el.id.endsWith("!cm"))
	|| tn == "TEXTAREA" || (tn == "BUTTON" && getZKAttr(el, "keyevt") != "true") || tn == "SELECT" || tn == "OPTION" || $type($outer(el)) == "Button");
};

/** row's onmouseover. */
zkSel.onover = function (evt) {
	if (!evt) evt = window.event;
	var row = zkSel.getNearestRow(Event.element(evt));
	if (row) Selectable_effect(row);
};
/** row's onmouseout. */
zkSel.onout = function (evt) {
	if (!evt) evt = window.event;
	zkSel.onoutTo(zkSel.getNearestRow(Event.element(evt)));
};
zkSel.onoutTo = function (row) {
	if (row) Selectable_effect(row, true);
};
zkSel.ondragover = function (evt) {
	var target = Event.element(evt);
	var tag = $tag(target);
	if (tag != "INPUT" && tag != "TEXTAREA") {
		var p = $parentByType(target, "Lic");
		if (p) p.firstChild.style.MozUserSelect = "none";
	}
};
zkSel.ondragout = function (evt) {
	var target = Event.element(evt);
	var p = $parentByType(target, "Lic");
	if (p) p.firstChild.style.MozUserSelect = "";
};
/** (!cm or !sel)'s onfocus. */
zkSel.cmonfocus = function (evt) {
	if (zkau.onfocus0(evt)) {
		if (!evt) evt = window.event;
		zkSel.cmonfocusTo($parentByTag(Event.element(evt), "TR"));
	}
};
/** (!cm or !sel)'s onblur. */
zkSel.cmonblur = function (evt) {
	if (!evt) evt = window.event;
	zkSel.cmonblurTo($parentByTag(Event.element(evt), "TR"));
};
zkSel.cmonfocusTo = function (row) {
	if (row) {
		zk.addClass(zkSel.getVisibleFirstChildIfAny(row), getZKAttr(row, "zcls") + "-focus");
	}
};
zkSel.cmonblurTo = function (row) {
	if (row) {
		var zcls = getZKAttr(row, "zcls");
		zk.rmClass(row, zcls + "-focus");
		for (var i = row.cells.length; --i >= 0;)
			zk.rmClass(row.cells[i], zcls + "-focus");
	}
};
zkSel.getVisibleFirstChildIfAny = function (row) {
	for (var i = 0, j = row.cells.length; i < j; i++)
		if (zk.isVisible(row.cells[i])) return row.cells[i];
	return row;
};
////
// listbox //
zkLibox = {}; //listbox
/** Called when the body got a key stroke. */
zkLibox.bodyonkeydown = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Libox");
	return !meta || meta.dobodykeydown(evt, target);
};
/** Called when a listitem got a key stroke. */
zkLibox.onkeydown = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt),
		tag = $tag(target);

	// Bug 2487562
	if (!zk.gecko3 || (tag != "INPUT" && tag != "TEXTAREA"))
		zk.disableSelection($parentByType(target, "Libox"));
	var meta = zkau.getMetaByType(target, "Libox");
	return !meta || meta.dokeydown(evt, target);
};
zkLibox.onkeyup = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	zk.enableSelection($parentByType(target, "Libox"));
};
/** Called when mouse click. */
zkLibox.onclick = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Libox");
	if (meta) meta.doclick(evt, target);
};

/** Called when focus command is received. */
zkLibox.focus = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta._refocus();
	return true;
};
/** Process the setAttr cmd sent from the server, and returns whether to
 * continue the processing of this cmd
 */
zkLibox.setAttr = function (cmp, nm, val) {
	var meta = zkau.getMeta(cmp);
	return meta && meta.setAttr(nm, val);
};

/** Init (and re-init) a listbox. */
zkLibox.init = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
	else {
		meta = new zk.Selectable(cmp);
		if (meta.body)
			zk.listen(meta.body, "keydown", zkLibox.bodyonkeydown);
	}
};
/** Called when a listbox becomes visible because of its parent. */
zkLibox.childchg = zkLibox.onVisi = zkLibox.onSize = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta._recalcSize();
	if (zk.opera && meta.body && meta._scrollTop)
		meta.body.scrollTop = meta._scrollTop;
};
zkLibox.beforeSize = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta._beforeSize();
};
zkLit = {}; //listitem
zkLit.init = function (cmp) {
	setZKAttr(cmp, "inited", "true");
	//zk.disableSelection(cmp);
	//Tom Yeh: 20060106: side effect: unable to select textbox if turned on
	if (!getZKAttr(cmp, "disd")) {
		zk.listen(cmp, "click", zkLibox.onclick);
		zk.listen(cmp, "mouseover", zkSel.onover);
		zk.listen(cmp, "mouseout", zkSel.onout);
	}
	zk.listen(cmp, "keydown", zkLibox.onkeydown);
	zk.listen(cmp, "keyup", zkLibox.onkeyup);
	zkLit.stripe(cmp);
};
/**
 * @since 3.5.2
 */
zkLit.focus = function (cmp) {
	var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
	if (meta) {
		meta._focusItem = cmp;
		meta._refocus();
		return true;
	}
};

zkLit.setAttr = function (cmp, nm, val) {
	if (nm == "visibility") {// Bug #1836257
		var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
		if (meta) {
			if (!meta.fixedStripe) meta.fixedStripe = function () {meta.stripe();};
			setTimeout(meta.fixedStripe, 0);
		}
	}
	return false;
};
zkLit.initdrag = function (cmp) {
	if (zk.gecko) {
		zk.listen(cmp, "mouseover", zkSel.ondragover);
		zk.listen(cmp, "mouseout",  zkSel.ondragout);
	}
};
zkLit.cleandrag = function (cmp) {
	if (zk.gecko) {
		zk.unlisten(cmp, "mouseover", zkSel.ondragover);
		zk.unlisten(cmp, "mouseout",  zkSel.ondragout);
	}
};
zkLit.cleanup = function (cmp) {
	zkLit.stripe(cmp, true);
};
zkLit.stripe = function (cmp, isClean) {
	var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
	if (meta) {
		if (!meta.fixedStripe) meta.fixedStripe = function () {meta.stripe();};
		if (isClean) zk.addCleanupLater(meta.fixedStripe, false, meta.id + "Lit");
		else zk.addInitLater(meta.fixedStripe, false, meta.id + "Lit");
	}
};
zkLic = {}; //listcell or Treecell
zkLic.initdrag = zkLit.initdrag;
zkLic.cleandrag = zkLit.cleandrag;
zkLic.setAttr = function (cmp, nm, val) {
	if ("style" == nm) {
		var cell = cmp.firstChild;
		var v = zk.getTextStyle(val);
		if (v) zkau.setAttr(cell, nm, v);
		zkau.setAttr(cmp, nm, val);
		return true;
	}
	return false;
};
/** Called when _onDocCtxMnu is called. */
zkLit.onrtclk = function (cmp) {
	var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
	if (meta && !meta._isSelected(cmp)) meta.doclick(null, cmp);
};

zkLcfc = {}; //checkmark or the first hyperlink of listcell
zkLcfc.init = function (cmp) {
	zk.listen(cmp, "focus", zkSel.cmonfocus);
	zk.listen(cmp, "blur", zkSel.cmonblur);
	zk.listen(cmp, "click", cmp.type == "checkbox" ? zkLcfc.onclick : zkLcfc.onRadioClick);
};
zkLcfc.onRadioClick = function (evt, radio) {
	var r = radio || zkau.evtel(evt);
	for (var nms = document.getElementsByName(r.name), i = nms.length; --i >= 0;)
		nms[i].defaultChecked = false;
	r.defaultChecked = r.checked;
};
zkLcfc.onclick = function (evt) {
	var cmp = zkau.evtel(evt);
	zkLcfc.checkAll(zkau.getMeta(getZKAttr($parentByTag(cmp, "TR"), "rid")), cmp);
};
/**
 * Checks whether the all-checked item should be checked.
 * @param {Object} meta the Selectable object of the specified cm.
 * @param {Object} cm an input check, if any, otherwise, checks all the items.
 * @since 3.0.6
 */
zkLcfc.checkAll = function (meta, cm) {
	if (!meta || !meta._headerCkitem) return;
	if (cm && !cm.checked) {
		meta._headerCkitem.checked = false;
		return;
	}
	var checked;
	for (var rows = meta.bodyrows, j = rows.length; --j >= 0;)
		if (!getZKAttr(rows[j], "disd"))
			if (!(checked = ($e(rows[j].id + "!cm") || {}).checked)) break;
	if (checked) meta._headerCkitem.checked = true;
};
zkLhfc = {}; //checkmark for listheader
zkLhfc.init = function (cmp) {
	zk.listen(cmp, "click", zkLhfc.onclick);
	zk.addInit(function () {
		var meta = zkau.getMeta(getZKAttr($parentByTag(cmp.parentNode, "TR"), "rid"));
		if (meta) {
			meta._headerCkitem = cmp;
			zkLcfc.checkAll(meta);
		}
	});
};
zkLhfc.cleanup = function (cmp) {
	var meta = zkau.getMeta(getZKAttr($parentByTag(cmp.parentNode, "TR"), "rid"));
	if (meta) meta._headerCkitem = null;
};
zkLhfc.onclick = function (evt) {
	var cmp = zkau.evtel(evt);
	var meta = zkau.getMeta(getZKAttr($parentByTag(cmp.parentNode, "TR"), "rid"));
	if (meta)
		cmp.checked ? meta._selectAll(true, evt) : meta.select("", zkau.getKeys(evt));
};
zk.addBeforeInit(function () {
	//Listheader
	//init it later because zul.js might not be loaded yet
	zkLhr = {}
	Object.extend(zkLhr, zulHdr);

	/** Resize the column. */
	zkLhr.resize = function (col1, icol, wd1, keys) {
		var box = getZKAttr(col1.parentNode, "rid");
		if (box) {
			var meta = zkau.getMeta(box);
			if (meta)
				meta.resizeCol(
					$parentByType(col1, "Lhrs"), icol, col1, wd1, keys);
		}
	};

	//Listhead
	zkLhrs = zulHdrs;
});

////
// listbox mold=select //
zkLisel = {};
zkLisel.init = function (cmp) {
	zk.listen(cmp, "change", zkLisel.onchange);
	if (zk.gecko || zk.safari) zk.listen(cmp, "keyup", zkLisel.onkeyup);
	zk.listen(cmp, "focus", zkau.onfocus);
	zk.listen(cmp, "blur", zkau.onblur);
	cmp._lastSelIndex = cmp.selectedIndex;
};
zkLisel.onkeyup = function (evt) {
	var cmp = zkau.evtel(evt);
	if (cmp.multiple || cmp._lastSelIndex === cmp.selectedIndex) return; //not change or unnecessary.
	zkLisel.onchange(evt);
};
zkLisel.setAttr = function (cmp, nm, val) {
	if (nm == "selectedIndex")
		cmp._lastSelIndex = $int(val);
	return false;
};
/** Handles onchange from select/list. */
zkLisel.onchange = function (evtel) {
	var cmp = zkau.evtel(evtel); //backward compatible with 2.4 or before
	var data, reference;
	if (cmp.multiple) {
		//To reduce # of bytes to send, we use a string instead of array.
		data = "";
		var opts = cmp.options;
		for (var j = 0, ol = opts.length; j < ol; ++j) {
			var opt = opts[j];
			if (opt.selected) {
				data += ","+opt.id;
				if (!reference) reference = opt.id;
			}
		}
		if (data) data = data.substring(1);
		//Design consideration: we could use defaultSelected to minimize
		//# of options to transmit. However, we often to set selectedIndex
		//which won't check defaultSelected
		//Besides, most of items are not selected
	} else {
		var opt = cmp.options[cmp.selectedIndex];
		data = opt.id;
		reference = opt.id;
		cmp._lastSelIndex = cmp.selectedIndex;
	}
	var uuid = $uuid(cmp);
	zkau.sendasap({uuid: uuid, cmd: "onSelect", data: [data, reference]});

	//Bug 1756559: see au.js
	if (zkau.lateReq) {
		zkau.send(zkau.lateReq, 25);
		delete zkau.lateReq;
	}
};
/** List Group*/
zkLitgp = {
	init: function (cmp) {
		setZKAttr(cmp, "inited", "true");
		if (!getZKAttr(cmp, "disd")) {
			zk.listen(cmp, "click", zkLibox.onclick);
			zk.listen(cmp, "mouseover", zkSel.onover);
			zk.listen(cmp, "mouseout", zkSel.onout);
		}
		zk.listen(cmp, "keydown", zkLibox.onkeydown);
		cmp._img = zk.firstChild(cmp, "IMG", true);
		if (cmp._img) zk.listen(cmp._img, "click", zkLitgp.ontoggle);
		var table = cmp.parentNode.parentNode;
		if (table.tBodies.length > 1) {
			var span = 0;
			for (var row = table.rows[0], i = row.cells.length; --i >=0;)
				if(zk.isVisible(row.cells[i])) span++;
			for (var cells = cmp.cells, i = cells.length; --i >= 0;)
				span -= cells[i].colSpan;
			if (span > 0 && cmp.cells.length) cmp.cells[cmp.cells.length - 1].colSpan += span;
		}
	},
	ontoggle: function (evt) {
		if (!evt) evt = window.event;
		var target = Event.element(evt),
			row = zk.parentNode(target, "TR");
		if (!row) return; //incomplete structure

		var meta = zkau.getMeta(getZKAttr(row, "rid"));
		if (meta) meta._syncFocus(row);
		var toOpen = !zkLitgp.isOpen(row); //toggle
		zkLitgp._openItem(row, toOpen);

		if (toOpen && meta || getZKAttr(meta.element, "model") == "true") {
			if (toOpen) meta.stripe();
			if (!meta.paging) meta._recalcSize(); // group in paging will invalidate the whole items.
		}
		Event.stop(evt);
	},
	isOpen: function (row) {
		return getZKAttr(row, "open") == "true";
	},/** Opens an item */
	_openItem: function (row, toOpen, silent) {
		setZKAttr(row, "open", toOpen ? "true": "false"); //change it value
		if (row._img) {
			if (toOpen) {
				zk.rmClass(row._img,getZKAttr(row, "zcls")+"-img-close");
				zk.addClass(row._img,getZKAttr(row, "zcls")+"-img-open");
			} else {
				zk.rmClass(row._img,getZKAttr(row, "zcls")+"-img-open");
				zk.addClass(row._img,getZKAttr(row, "zcls")+"-img-close");
			}
		}
		zkLitgp._openItemNow(row, toOpen);
		if (!silent)
			zkau.send({uuid: row.id,
				cmd: "onOpen", data: [toOpen]},
				toOpen && getZKAttr(row, "lod") ? 38: //load-on-demand
					zkau.asapTimeout(row, "onOpen"));
				//always send since the client has to update Openable
	},
	_openItemNow: function (row, toOpen) {
		for (var table = row.parentNode.parentNode, i = row.rowIndex + 1, j = table.rows.length; i < j; i++) {
			if ($type(table.rows[i]) == "Litgp" || $type(table.rows[i]) == "Litgpft") break;
			if (getZKAttr(table.rows[i], "visible") == "true")
				table.rows[i].style.display = toOpen ? "" : "none";
		}
	},
	cleanup: function (row) {
		row._img = null;
		var prev, table = row.parentNode.parentNode;
		for (var i = row.rowIndex - 1; --i >= 0;) {
			if ($type(table.rows[i]) == "Litgp") {
				prev = table.rows[i];
				break;
			}
		}
		if (prev)
			zk.addCleanupLater(function () {
				prev = $e(prev.id);
				if (prev)
					zkLitgp._openItem(prev, zkLitgp.isOpen(prev), true);
			}, false, row.id);
		else zkLitgp._openItemNow(row, true);
	},
	setAttr: function (cmp, nm, val) {
		if (nm == "z.open") {
			zkLitgp._openItem(cmp, "true" == val, true);
			if ("true" == val) {
				var meta = zkau.getMeta(getZKAttr(cmp, "rid"));
				if (meta) meta.stripe();
			}
			return true;
		}
		return false;
	},
	initdrag: zkLit.initdrag,
	cleandrag: zkLit.cleandrag,
	onrtclk: zkLit.onrtclk
};
zkLitgp.onVisi = zkLitgp.onSize = function (cmp) {
	zkLitgp._openItem(cmp, zkLitgp.isOpen(cmp), true);
	zkLit.stripe(cmp);
};
/** List Group Footer*/
zkLitgpft = {
	init: function (cmp) {
		setZKAttr(cmp, "inited", "true");
		if (!getZKAttr(cmp, "disd")) {
			zk.listen(cmp, "click", zkLibox.onclick);
			zk.listen(cmp, "mouseover", zkSel.onover);
			zk.listen(cmp, "mouseout", zkSel.onout);
		}
		zk.listen(cmp, "keydown", zkLibox.onkeydown);
		zk.listen(cmp, "keyup", zkLibox.onkeyup);
	},
	cleanup: zkLit.cleanup,
	initdrag: zkLit.initdrag,
	cleandrag: zkLit.cleandrag,
	onrtclk: zkLit.onrtclk
};
