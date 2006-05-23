/* sel.js

{{IS_NOTE
	$Id: sel.js,v 1.28 2006/05/23 02:35:15 tomyeh Exp $
	Purpose:
		zk.Selectable
	Description:
		
	History:
		Fri Aug 26 08:45:55     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.html.zul");

////
// Seletable //
zk.Selectable = Class.create();
zk.Selectable.prototype = {
	initialize: function (cmp) {
		this.id = cmp.id;
		zkau.setMeta(cmp, this);
		this.init();
	},
	init: function () {
		this.element = $(this.id);
		if (!this.element) return;

		//_headtbl might be null, while other must be NOT null
		this.head = $(this.id + "!head");
		if (this.head) this.headtbl = zk.firstChild(this.head, "TABLE", true);
		this.body = $(this.id + "!body");
		if (this.body) this.bodytbl = zk.firstChild(this.body, "TABLE", true);
		this.bodyrows = this.bodytbl.tBodies[0].rows;
		if (!this.bodyrows) {
			alert(mesg.INVALID_STRUCTURE + this.id);
			return;
		}
		this.foot = $(this.id + "!foot");
		if (this.foot) this.foottbl = zk.firstChild(this.foot, "TABLE", true);

		if (!zk.isRealVisible(this.element)) return;

		var meta = this; //the nested function only see local var
		if (!this._inited) {
			this._inited = true;

			//If Mozilla, we have to eat keystrokes, or the page
			//will scroll when UP/DOWN is pressed
			if (zk.agtNav) {
				this.element.onkeydown = this.element.onkeyup
				= this.element.onkeypress = function (evt) {
					var target = Event.element(evt);
					if (zkSel._shallIgnoreEvent(target))
						return true;

					if (evt) {
						switch (evt.keyCode) {
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

				//To turn-off select, use "-moz-user-select: none" in CSS
			} else { //non-FF
				this.element.onselectstart = function () {return false;}
				//Tom Yeh: 20060106: side effect: unable to select textbox
			}
			this.fnResize = function () {
				//Tom Yeh: 20051230:
				//In certain case, IE will keep sending resize (because
				//our listbox might adjust size and cause resize again)
				//To avoid this endless loop, we resize once in a few seconds
				var time = new Date().getTime();
				if (!meta.nextTime || time > meta.nextTime) {
					meta.nextTime = time + 3000;
					meta._recalcSize();
				}
			};
			Event.observe(window, "resize", this.fnResize);

			for (var n = this.element; (n = n.parentNode) != null;)
				if (zk.tagName(n) == "FORM") {
					this.form = n;
					break;
				}
			if (this.form) {
				this.fnSubmit = function () {
					meta.onsubmit();
				};
				Event.observe(this.form, "submit", this.fnSubmit);
			}
		}

		if (zk.agtNav && this.headtbl && this.headtbl.rows.length) {
			var headrow = this.headtbl.rows[0];
			var empty = true;
			for (var j = headrow.cells.length; --j>=0;)
				if (headrow.cells[j].firstChild) {
					empty = false;
					break;
				}
			this.head.style.display = empty ? "none": "";
				//we have to hide if empty (otherwise, a small block is shown)
		}

		this.body.onscroll = function () {
			if (meta.head) meta.head.scrollLeft = meta.body.scrollLeft;
			if (meta.foot) meta.foot.scrollLeft = meta.body.scrollLeft;
			meta._render(zk.agtNav ? 200: 0);
				//Moz has a bug to send the request out if we don't wait long enough
				//How long is enough is unknown, but 200 seems fine
		};
		this._render(20);

		setTimeout("zkSel._calcSize('"+this.id+"')", 5);
			//don't calc now because browser might size them later
			//after the whole HTML page is processed
	},
	cleanup: function ()  {
		if (this.fnResize)
			Event.stopObserving(window, "resize", this.fnResize);
		if (this.fnSubmit)
			Event.stopObserving(this.form, "submit", this.fnSubmit);
		this.element = this.body = this.head = this.bodytbl = this.headtbl
			this.foot = this.foottbl = null;
			//in case: GC not works properly
	},

	/** Handles keydown sent to the body. */
	dobodykeydown: function (evt, target) {
		if (zkSel._shallIgnoreEvent(target))
			return true;

	// Note: We don't intercept body's onfocus to gain focus back to anchor.
	// Otherwise, it cause scroll problem on IE:
	// When user clicks on the scrollbar, it scrolls first and call onfocus,
	// then it will scroll back to the focus because _focusToAnc is called
		switch (evt.keyCode) {
		case 33: //PgUp
		case 34: //PgDn
		case 38: //UP
		case 40: //DOWN
		case 37: //LEFT
		case 39: //RIGHT
		case 32: //SPACE
		case 36: //Home
		case 35: //End
			if (zk.tagName(target) != "A")
				this._refocus();
			Event.stop(evt);
			return false;
		}
		return true;
	},
	/** Handles the keydown sent to the row. */
	dokeydown: function (evt, target) {
		if (zkSel._shallIgnoreEvent(target))
			return true;

		var row = zk.tagName(target) == "TR" ? target: zk.parentNode(target, "TR");
		if (!row) return true;

		var shift = evt.shiftKey, ctrl = evt.ctrlKey;
		if (shift && !this._isMultiple())
			shift = false; //OK to 

		var endless = false, step, lastrow;
		switch (evt.keyCode) {
		case 33: //PgUp
		case 34: //PgDn
			step = this.realsize();
			if (step == 0) step = 20;
			if (evt.keyCode == 33)
				step = -step;
			break;
		case 38: //UP
		case 40: //DOWN
			step = evt.keyCode == 40 ? 1: -1;
			break;
		case 32: //SPACE
			if (this._isMultiple()) this.toggleSelect(row, !this._isSelected(row));
			else this.select(row);
			break;
		case 36: //Home
		case 35: //End
			step = evt.keyCode == 35 ? 1: -1;
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
			if (shift) this.toggleSelect(row, true);
			for (; (row = step > 0 ? row.nextSibling: row.previousSibling) != null;) {
				if (zk.tagName(row) == "TR"  && this._isValid(row)) {
					if (shift) this.toggleSelect(row, true);

					if (zk.isVisible(row)) {
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
			else this.select(lastrow);
		}

		switch (evt.keyCode) {
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
	doclick: function (evt, target) {
		if (zkSel._shallIgnoreEvent(target)
		|| (zk.tagName(target) != "TR" && target.onclick)
		|| (zk.tagName(target) == "A" && !target.id.endsWith("!sel")))
			return;

		var checkmark = target.id && target.id.endsWith("!cm");
		var row = zk.tagName(target) == "TR" ? target: zk.parentNode(target, "TR");
		if (!row) return; //incomplete structure

		if (checkmark) {
			if (this._isMultiple()) {
				this.toggleSelect(row, target.checked);
			} else {
				this.select(row);
			}
		} else {
			if (this._isMultiple()) {
				if (evt.shiftKey) {
					this.selectUpto(row);
				} else if (evt.ctrlKey) {
					this.toggleSelect(row, !this._isSelected(row));
				} else {
	//Note: onclick means toggle if checkmark is enabled
	//Otherwise, we mimic Windows if checkmark is disabled
					var el = $(row.id + "!cm");
					if (el) this.toggleSelect(row, !el.checked);
					else this.select(row);
				}
			} else {
				this.select(row);
			}

			//since row might was selected, we always enfoce focus here
			this._focusToAnc(row);
			Event.stop(evt);
		}
	},

	/** Returns # of rows allowed. */
	size: function () {
		var sz = this.element.getAttribute("zk_size");
		return sz ? parseInt(sz): 0;
	},
	/** Returns the real # of rows (aka., real size). */
	realsize: function (v) {
		if ("number" == typeof v) {
			this.element.setAttribute("zk_realsize", v);
		} else {
			var sz = this.size();
			if (sz) return sz;
			sz = this.element.getAttribute("zk_realsize");
			return sz ? parseInt(sz): 0;
		}
	},

	/** Re-setfocus to the anchor who shall be in focus. */
	_refocus: function () {
		for (var j = 0; j < this.bodyrows.length; ++j) {
			var r = this.bodyrows[j];
			if (this._isFocus(r)) this._focusToAnc(r);
		}
	},
	/** Process the setAttr command sent from the server. */
	setAttr: function (name, value) {
		switch (name) {
		case "selectedIndex":
			this._setSelectedIndex(value);
			return true; //no more processing
		case "select": //select by uuid
			var row = $(value);
			this._selectOne(row, false);
			return true;
		case "selectAll":
			this._selectAll();
			return true; //no more processing
		case "zk_multiple": //whether to support multiple
			this._setMultiple("true" == value);
			return true;
		case "addSel": //add a row into selection
			var row = $(value);
			if (row) this._changeSelect(row, true);
			//no need to maintain other attr because it's server's job
			return true;
		case "rmSel": //remove a row from selection
			var row = $(value);
			if (row) this._changeSelect(row, false);
			//no need to maintain other attr because it's server's job
			return true;
		}
		return false;
	},
	/** Returns the item's UUID containing the specified row. */
	getItemUuid: function (row) {
		return row.id;
	},
	/** Selects an item, notify server and change focus if necessary. */
	select: function (row) {
		if (this._selectOne(row, true)) {
			//notify server
			zkau.send({
				uuid: this.id, cmd: "onSelect", data: [this.getItemUuid(row)]},
				zkau.asapTimeout(this.element, "onSelect"));
		}
	},
	/** Toggle the selection and notifies server. */
	toggleSelect: function (row, toSel) {
		this._changeSelect(row, toSel);
		this.focus(row);

		//maintain zk_selId
		var selId = this._getSelectedId();
		if (this._isMultiple()) {
			if (row.id == selId)
				this._fixSelelectedId();
		} else if (selId) {
			var sel = $(selId);
			if (sel) this._changeSelect(sel, false);
		}

		//notify server
		this._sendSelect();
	},
	/** Selects a range from the last focus up to the specified one.
	 * Callable only if multiple
	 */
	selectUpto: function (row) {
		if (this._isSelected(row)) {
			this.focus(row);
			return; //nothing changed
		}

		var focusfound = false, rowfound = false;
		for (var j = 0; j < this.bodyrows.length; ++j) {
			var r = this.bodyrows[j];
			if (focusfound) {
				this._changeSelect(r, true);
				if (r == row)
					break;
			} else if (rowfound) {
				this._changeSelect(r, true);
				if (this._isFocus(r))
					break;
			} else {
				rowfound = r == row;
				focusfound = this._isFocus(r);
				if (rowfound || focusfound) {
					this._changeSelect(r, true);
					if (rowfound && focusfound)
						break;
				}
			}
		}

		this.focus(row);
		this._fixSelelectedId();
		this._sendSelect();
	},

	/** Changes the specified row as zk_focus. */
	focus: function (row) {
		this._unsetFocusExcept(row);
		this._setFocus(row, true);
	},
	/** Sets focus to the specified row if it has the anchor. */
	_focusToAnc: function (row) {
		if (!row) return;
		var uuid = typeof row == 'string' ? row: row.id;
		var el = $(uuid + "!sel");
		if (!el) el = $(uuid + "!cm");
		if (el) zk.focusById(el.id);
	},

	/** Selects one and deselect others, and return whehter any changes.
	 * It won't notify the server.
	 * @param row the row to select. Unselect all if null
	 * @param toFocus whether to change focus
	 */
	_selectOne: function (row, toFocus) {
		row = $(row);
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

				var sel = $(selId);
				if (sel) {
					this._changeSelect(sel, false);
					if (row && toFocus) this._setFocus(sel, false);
				}
			} else {
				if (row && toFocus) this._unsetFocusExcept(row);
			}
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
			var el = $(row.id + "!cm");
			if (toSel) {
				if (el) el.checked = true;
				row.className = row.className + "sel";
				zkSel.onout(row);
				row.setAttribute("zk_sel", "true");
			} else {
				if (el) el.checked = false;
				var len = row.className.length;
				if (len > 3)
					row.className = row.className.substring(0, len - 3);
				zkSel.onout(row);
				row.setAttribute("zk_sel", "false");
			}
		}
		return changed;
	},
	/** Changes the zk_focus status, and return whether zk_focus is changed. */
	_setFocus: function (row, toFocus) {
		if (!this._isValid(row)) return false;

		var changed = this._isFocus(row) != toFocus;
		if (changed) {
			if (toFocus) {
				var el = $(row.id + "!cm");
				if (!el) {
					el = $(row.id + "!sel"); //just in case
					if (!el && row.cells.length > 0) {
						el = document.createElement("A");
						el.href = "javascript:;";
						el.id = row.id + "!sel";
						el.innerHTML = " ";
						el.onfocus = new Function("zkSel.cmonfocus(this)");
						el.onblur = new Function("zkSel.cmonblur(this)");
						row.cells[0].appendChild(el);
					}
				}
				if (el) zk.focusById(el.id);
				row.setAttribute("zk_focus", "true");
				zkSel.cmonfocus(row);

				if (zk.agtNav) this._render(5);
					//Firefox doesn't call onscroll when we moving by cursor, so...
			} else {
				var el = $(row.id + "!sel");
				if (el) row.cells[0].removeChild(el);
				row.removeAttribute("zk_focus");
				zkSel.cmonblur(row);
			}
		}
		return changed;
	},
	/** Cleans selected except the specified one, and returns any selected status
	 * is changed.
	 */
	_unsetSelectAllExcept: function (row) {
		var changed = false;
		for (var j = 0; j < this.bodyrows.length; ++j) {
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
		var changed = false;
		for (var j = 0; j < this.bodyrows.length; ++j) {
			var r = this.bodyrows[j];
			if (r != row && this._setFocus(r, false))
				changed = true;
		}
		return changed;
	},

	/** Renders listitems that become visible by scrolling.
	 */
	_render: function (timeout) {
		setTimeout("zkSel._renderNow('"+this.id+"')", timeout);
	},
	_renderNow: function () {
		if (this.element.getAttribute("zk_model") != "true") return;

		var rows = this.bodyrows;
		if (!rows.length) return; //no row at all

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var data = "";
		var min = this.body.scrollTop, max = min + this.body.offsetHeight;
		for (var j = 0; j < rows.length; ++j) {
			var r = rows[j];
			if (zk.isVisible(r)) {
				if (r.offsetTop + r.offsetHeight < min) continue;
				if (r.offsetTop >= max) break;
				if (r.getAttribute("zk_loaded") != "true")
					data += "," + this.getItemUuid(r);
			}
		}
		if (data) {
			data = data.substring(1);
			zkau.send({uuid: this.id, cmd: "onRender", data: [data]}, 0);
		}
	},

	/** Calculates the size. */
	_calcSize: function () {
		var headrow = this.headtbl && this.headtbl.rows.length ? this.headtbl.rows[0]: null;
		var bodyrow = this.bodyrows.length ? this.bodyrows[0]: null;

		var headhgh = this.head ? this.head.offsetHeight: 0;
		var rowhgh = bodyrow && bodyrow.offsetHeight ? bodyrow.offsetHeight:
			headrow && headrow.offsetHeight ? headrow.offsetHeight: 20;

		var diff = 2/*experiment*/, size = this.size(), height;
		if (!size) {
			var gap = this._getFitGap();
			if (gap > 0) { //not enough space
				height = this.body.offsetHeight - gap;
				if (height < 25) height = 25;
				size = Math.round((height - diff)/ rowhgh);
				if (size < 3) { //minimal 3 rows if auto-size
					size = 3;
					height = rowhgh * 3 + diff;
				}
				this.realsize(size);
			} else {
				this.realsize(0); //no limit at all
			}
		}

		if (size) {
			if (height) {
				this.body.style.height = height + "px";
			} else {
				this.body.style.height = (rowhgh * size + diff) + "px";
			}
		}

		var wd = this.element.style.width;
		if (wd && wd != "auto") {
			//IE: otherwise, element's width will be extended to fit body
			this.body.style.width = wd;
			if (this.head) this.head.style.width = wd;
			if (this.foot) this.foot.style.width = wd;
		}

		setTimeout("zkSel._calcSize2('"+this.id+"')", 0);
			//IE cannot calculate the size immediately after setting overflow to auto
	},
	/** Cacluates the gap to make overflow to fit-in.
	 * @return nonpositive means it already fit
	 */
	_getFitGap: function () {
		var gap = document.body.offsetHeight - zk.innerHeight()
				+ parseInt(zk.getCurrentStyle(document.body, "margin-top"))
				+ parseInt(zk.getCurrentStyle(document.body, "margin-bottom"))
				+ parseInt(zk.getCurrentStyle(document.body, "padding-top"))
				+ parseInt(zk.getCurrentStyle(document.body, "padding-bottom"));
		if (zk.agtIe) ++gap; //strange, but...
		if (gap <= 0) return 0; //already fit

		//this overflow might be caused by another element
		for (var el = this.element;;) {
			var p = el.offsetParent;
			if (!p) return gap; //yes
			if (zk.tagName(p) == "TD" && p.clientHeight > el.offsetHeight)
				return 0; //not caused by this element
			el = p;
		}
	},

	/** Calculates the size, part 2. */
	_calcSize2: function () {
		var tblwd = this.body.clientWidth;
		if (tblwd) { //IE: if no rows
			if (zk.agtIe && --tblwd < 0) tblwd = 0; //By experimental
			this.bodytbl.style.width = tblwd + "px";
		}

		if (this.headtbl) {
			if (tblwd) this.head.style.width = tblwd + 'px';
			var headrow = this.headtbl.rows.length ? this.headtbl.rows[0]: null;
			zk.cpCellWidth(headrow, this.bodyrows); //assign head's col width
		}
		if (this.foottbl) {
			if (tblwd) this.foot.style.width = tblwd + 'px';
			var footrow = this.foottbl.rows.length ? this.foottbl.rows[0]: null;
			zk.cpCellWidth(footrow, this.bodyrows); //assign foot's col width
		}
	},
	/** Recalculate the size. */
	_recalcSize: function () {
		this.bodytbl.style.width = "";
		if (this.headtbl) {
			this.head.style.width = this.body.style.height = "";
			if (this.headtbl.rows.length) {
				var headrow = this.headtbl.rows[0];
				for (var j = headrow.cells.length; --j >=0;)
					headrow.cells[j].style.width = "";
			}
		}
		if (this.foottbl) {
			this.foottbl.style.width = this.body.style.height = "";
			if (this.foottbl.rows.length) {
				var footrow = this.foottbl.rows[0];
				for (var j = footrow.cells.length; --j >=0;)
					footrow.cells[j].style.width = "";
			}
		}

		setTimeout("zkSel._calcSize('"+this.id+"')", 20);
	},

	/** Sels the selected item by an index (don't notify server). */
	_setSelectedIndex: function (value) {
		value = parseInt(value);
		if (!this.bodyrows.length || value < 0) {
			value = null;
		} else if (value < this.bodyrows.length) {
			value = this.bodyrows[value];
		} else {
			value = this.bodyrows[this.bodyrows.length-1];
		}
		this._selectOne(value, false); 
	},
	/** Sels all items (don't notify server and change focus, because it is from server). */
	_selectAll: function () {
		var rows = this.bodyrows;
		for (var j = 0; j < rows.length; ++j)
			this._changeSelect(rows[j], true);

		this._setSelectedId(rows.length ? rows[0].id: null);
	},

	/** Notifies the server the selection is changed (callable only if multiple). */
	_sendSelect: function () {
		//To reduce # of bytes to send, we use a string instead of array.
		var data = "";
		for (var j = 0; j < this.bodyrows.length; ++j) {
			var r = this.bodyrows[j];
			if (this._isSelected(r))
				data += "," + this.getItemUuid(r);
		}
		if (data) data = data.substring(1);
		zkau.send({uuid: this.id, cmd: "onSelect", data: [data]},
				zkau.asapTimeout(this.element, "onSelect"));
	},

	/** Returns zk_selId (aka., the id of the selected item), or null if
	 * no one is ever selected.
	 */
	_getSelectedId: function () {
		var selId = this.element.getAttribute("zk_selId");
		if (!selId) {
			alert(mesg.INVALID_STRUCTURE + "zk_selId not found");
			return null;
		}
		return selId == "zk_n_a" ? null: selId;
	},
	/** Sets zk_selId (aka., the id of the selected item). */
	_setSelectedId: function (selId) {
		this.element.setAttribute("zk_selId", selId ? selId: "zk_n_a");
	},
	/** Fixes zk_selId to the first selected item. */
	_fixSelelectedId: function () {
		var selId = null;
		for (var j = 0; j < this.bodyrows.length; ++j) {
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
		return row && row.getAttribute("zk_sel") == "true";
	},
	/** Whether an item has focus. */
	_isFocus: function (row) {
		return row && row.getAttribute("zk_focus") == "true";
	},
	/** Whether the component is multiple.
	 */
	_isMultiple: function () {
		return this.element.getAttribute("zk_multiple") == "true";
	},
	/** Changes the multiple status. Note: it won't notify the server any change
	 */
	_setMultiple: function (multiple) {
		this.element.setAttribute("zk_multiple", multiple ? "true": "false");
		if (!multiple) {
			var row = $(this._getSelectedId());
			this._unsetSelectAllExcept(row);
				//no need to unfocus because we don't want to change focus
		}
	},
	/** Returns whether the row is valid. */
	_isValid: function (row) {
		return row && !row.id.endsWith("!child");
	},

	/** Called when the form enclosing it is submitting. */
	onsubmit: function () {
		var nm = this.element.getAttribute("zk_name");
		if (!nm || !this.form) return;

		for (var j = 0; j < this.form.elements.length; ++j){
			var el = this.form.elements[j];
			if (el.name == nm && el.type == "hidden") {
				el.parentNode.removeChild(el);
				--j;
			}
		}

		for (var j = 0; j < this.bodyrows.length; ++j) {
			var r = this.bodyrows[j];
			if (this._isSelected(r)) {
				var inp = document.createElement("INPUT");
				inp.type = "hidden";
				inp.name = nm;
				inp.value = r.getAttribute("zk_value");
				this.form.appendChild(inp);
			}
		}
	}
};

////
// Utilities to help implement zk.Selectable //
function zkSel() {}

zkSel._init = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._init();
};
zkSel._calcSize = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._calcSize();
};
zkSel._calcSize2 = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._calcSize2();
};
zkSel._renderNow = function (uuid) {
	var meta = zkau.getMeta(uuid);
	if (meta) meta._renderNow();
};
zkSel._shallIgnoreEvent = function (el) {
	var tn = zk.tagName(el);
	return !el || ((tn == "INPUT" && !el.id.endsWith("!cm"))
	|| tn == "TEXTAREA" || tn == "BUTTON" || tn == "SELECT");
};
/** row's onmouseover. */
zkSel.onover = function (el) {
	if (!zk.dragging) {
		zk.backupStyle(el, "backgroundColor");
		el.style.backgroundColor =
			el.className.endsWith("sel") ? "#115588": "#DAE8FF";
	}
};
/** row's onmouseout. */
zkSel.onout = function (el) {
	if (!zk.dragging) zk.restoreStyle(el, "backgroundColor");
};
/** (!cm or !sel)'s onfocus. */
zkSel.cmonfocus = function (el) {
	var row = $(zkau.uuidOf(el.id));
	if (row) 
		if (!zk.agtNav) row.style.textDecoration = "underline";
		else if (row.cells.length) row.cells[0].style.textDecoration = "underline";
	
};
/** (!cm or !sel)'s onblur. */
zkSel.cmonblur = function (el) {
	var row = $(zkau.uuidOf(el.id));
	if (row) 
		if (!zk.agtNav) row.style.textDecoration = "";
		else if (row.cells.length) row.cells[0].style.textDecoration = "";
};

////
// listbox //
function zkLibox() {}

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
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Libox");
	return !meta || meta.dokeydown(evt, target);
};
/** Called when mouse click. */
zkLibox.onclick = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var meta = zkau.getMetaByType(target, "Libox");
	if (meta) meta.doclick(evt, target);
};

/** Process the setAttr cmd sent from the server, and returns whether to
 * continue the processing of this cmd
 */
zkLibox.setAttr = function (cmp, name, value) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.setAttr(name, value);
};

/** Init (and re-init) a listbox. */
zkLibox.init = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
	else {
		var bdy = $(cmp.id + "!body");
		if (bdy)
			Event.observe(bdy, "keydown",
				function (evt) {return zkLibox.bodyonkeydown(evt);});

		new zk.Selectable(cmp);
	}
};

/** Called when a listbox becomes visible because of its parent. */
zkLibox.onVisi = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
};

function zkLit() {}
zkLit.init = function (cmp) { //listitem
	Event.observe(cmp, "click", function (evt) {zkLibox.onclick(evt);});
	Event.observe(cmp, "keydown", function (evt) {return zkLibox.onkeydown(evt);});
	Event.observe(cmp, "mouseover", function () {return zkSel.onover(cmp);});
	Event.observe(cmp, "mouseout", function () {return zkSel.onout(cmp);});
};

function zkLcfc() {}
zkLcfc.init = function (cmp) { //checkmark or the first hyperlink of listcell
	Event.observe(cmp, "focus", function () {return zkSel.cmonfocus(cmp);});
	Event.observe(cmp, "blur", function () {return zkSel.cmonblur(cmp);});
};

////
// listbox mold=select //
function zkLisel() {}
zkLisel.init = function (cmp) {
	Event.observe(cmp, "change", function (evt) {zkLisel.onchange(cmp);});
	Event.observe(cmp, "focus", function () {zkau.onfocus(cmp);});
	Event.observe(cmp, "blur", function() {zkau.onblur(cmp);});
};
/** Handles onchange from select/list. */
zkLisel.onchange = function (target) {
	var data;
	if (target.multiple) {
		//To reduce # of bytes to send, we use a string instead of array.
		data = "";
		var opts = target.options;
		for (var j = 0; j < opts.length; ++j) {
			var opt = opts[j];
			if (opt.selected)
				data += ","+opt.id;
		}
		if (data) data = data.substring(1);
		//Design consideration: we could use defaultSelected to minimize
		//# of options to transmit. However, we often to set selectedIndex
		//which won't check defaultSelected
		//Besides, most of items are not selected
	} else {
		var opt = target.options[target.selectedIndex];
		data = opt.id;
	}
	var uuid = zkau.uuidOf(target);
	zkau.send({uuid: uuid, cmd: "onSelect", data: [data]},
			zkau.asapTimeout(uuid, "onSelect"));
};
