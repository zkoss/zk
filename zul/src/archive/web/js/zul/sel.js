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
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul");

////
//Customization
/** Returns the background color for a list item or tree item.
 * Developer can override this method by providing a different background.
 */
if (!window.Selectable_bgcolor) { //define it only if not customized
	window.Selectable_bgcolor = function (row) {
		var clr = Element.getStyle(row, "color");
		return clr == "#000" || clr == "rgb(0, 0, 0)" || clr == "white" ?
			row.className.endsWith("sel") ? "#778ABB": "#EAEFFF":
			row.className.endsWith("sel") ? "#115588": "#DAE8FF";
	};
}

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
		this.element = $e(this.id);
		if (!this.element) return;

		//_headtbl might be null, while other must be NOT null
		this.body = $e(this.id + "!body");
		if (this.body) {
			this.bodytbl = zk.firstChild(this.body, "TABLE", true);
			if (this.bodytbl) {
				var bds = this.bodytbl.tBodies;
				if (!bds || !bds.length)
					this.bodytbl.appendChild(document.createElement("TBODY"));
				this.bodyrows = bds[0].rows;
			}

			this.head = $e(this.id + "!head");
			if (this.head) this.headtbl = zk.firstChild(this.head, "TABLE", true);
			this.foot = $e(this.id + "!foot");
			if (this.foot) this.foottbl = zk.firstChild(this.foot, "TABLE", true);
		} else {
			this.paging = true;
			this.body = $e(this.id + "!paging");
			this.bodytbl = zk.firstChild(this.body, "TABLE", true);
			this.bodyrows = this.bodytbl.tBodies[1].rows;
		}
		if (!this.bodyrows) {
			alert(mesg.INVALID_STRUCTURE + this.id);
			return;
		}

		if (!zk.isRealVisible(this.element)) return;

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
			}

			if (!this.paging) {
				this.fnResize = function () {
					//Tom Yeh: 20051230:
					//In certain case, IE will keep sending resize (because
					//our listbox might adjust size and cause resize again)
					//To avoid this endless loop, we resize once in a few seconds
					var time = new Date().getTime();
					if (!meta.nextTime || time > meta.nextTime) {
						meta.nextTime = time + 3000;
						meta.recalcSize(true);
					}
				};
				zk.listen(window, "resize", this.fnResize);
			}

			for (var n = this.element; (n = n.parentNode) != null;)
				if ($tag(n) == "FORM") {
					this.form = n;
					break;
				}
			if (this.form) {
				this.fnSubmit = function () {
					meta.onsubmit();
				};
				zk.listen(this.form, "submit", this.fnSubmit);
			}
		}

		if (!this.paging) {
			if ((zk.gecko||zk.opera) && this.headtbl && this.headtbl.rows.length == 1) {
				var headrow = this.headtbl.rows[0];
				var empty = true;
				l_out:
				for (var j = headrow.cells.length; --j>=0;)
					for (var n = headrow.cells[j].firstChild; n; n = n.nextSibling)
						if (!n.id || !n.id.endsWith("!hint")) {
							empty = false;
							break l_out;
						}
				if (empty) this.head.style.display = "none";
					//we have to hide if empty (otherwise, a small block is shown)
			}

			this.body.onscroll = function () {
				if (meta.head) meta.head.scrollLeft = meta.body.scrollLeft;
				if (meta.foot) meta.foot.scrollLeft = meta.body.scrollLeft;
				meta._render(zk.gecko ? 200: 60);
					//Moz has a bug to send the request out if we don't wait long enough
					//How long is enough is unknown, but 200 seems fine
			};
		}

		setTimeout("zkSel._calcSize('"+this.id+"')", 5);
			//don't calc now because browser might size them later
			//after the whole HTML page is processed

		this._render(150); //prolong a bit since calSize might not be ready
	},
	cleanup: function ()  {
		if (this.fnResize)
			zk.unlisten(window, "resize", this.fnResize);
		if (this.fnSubmit)
			zk.unlisten(this.form, "submit", this.fnSubmit);
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
			if ($tag(target) != "A")
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

		var row = $tag(target) == "TR" ? target: zk.parentNode(target, "TR");
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
				if ($tag(row) == "TR"  && this._isValid(row)) {
					if (shift) this.toggleSelect(row, true);

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
	/** Returns the type of the row. */
	_rowType: function () {
		return "Lit";
	},
	doclick: function (evt, target) {
		if (zkSel._shallIgnoreEvent(target))
			return;
		var tn = $tag(target);
		if((tn != "TR" && target.onclick)
		|| (tn == "A" && !target.id.endsWith("!sel"))
		|| getZKAttr(target, "lfclk") || getZKAttr(target, "dbclk"))
			return;

		var checkmark = target.id && target.id.endsWith("!cm");
		var row = tn == "TR" ? target: zk.parentNode(target, "TR");
		if (!row || $type(row) != this._rowType())
			return; //incomplete structure or grid in listbox...

		//It is better not to change selection only if dragging selected
		//(like Windows does)
		//However, FF won't fire onclick if dragging, so the spec is
		//not to change selection if dragging (selected or not)
		if (zk.dragging /*&& this._isSelected(row)*/)
			return;

		if (checkmark) {
			if (this._isMultiple()) {
				this.toggleSelect(row, target.checked);
			} else {
				this.select(row);
			}
		} else {
		//Bug 1650540: double click as select again
		//Note: we don't handle if clicking on checkmark, since FF always
		//toggle and it causes incosistency
			if ((zk.gecko || zk.safari) && getZKAttr(row, "dbclk")) {
				var now = new Date(), last = row._last;
				row._last = now;
				if (last && now - last < 900)
					return; //ignore double-click
			}

			if (this._isMultiple()) {
				if (evt && evt.shiftKey) {
					this.selectUpto(row);
				} else if (evt && evt.ctrlKey) {
					this.toggleSelect(row, !this._isSelected(row));
				} else {
	//Note: onclick means toggle if checkmark is enabled
	//Otherwise, we mimic Windows if checkmark is disabled
					var el = $e(row.id + "!cm");
					if (el) this.toggleSelect(row, !el.checked);
					else this.select(row);
				}
			} else {
				this.select(row);
			}

			//since row might was selected, we always enfoce focus here
			this._focusToAnc(row);

			//if (evt) Event.stop(evt);
			//No much reason to eat the event.
			//Oppositely, it disabled popup (bug 1578659)
		}
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
		for (var j = 0; j < this.bodyrows.length; ++j) {
			var r = this.bodyrows[j];
			if (this._isFocus(r)) this._focusToAnc(r);
		}
	},
	/** Process the setAttr command sent from the server. */
	setAttr: function (name, value) {
		switch (name) {
		case "select": //select by uuid
			var row = $e(value);
			this._selectOne(row, false);
			return true;
		case "selectAll":
			this._selectAll();
			return true; //no more processing
		case "z.multiple": //whether to support multiple
			this._setMultiple("true" == value);
			return true;
		case "chgSel": //value: a list of uuid to select
			var sels = {};
			for (var j = 0;;) {
				var k = value.indexOf(',', j);
				var s = (k >= 0 ? value.substring(j, k): value.substring(j)).trim();
				if (s) sels[s] = true;
				if (k < 0) break;
				j = k + 1;
			}

			var rows = this.bodyrows;
			for (var j = 0; j < rows.length; ++j)
				this._changeSelect(rows[j], sels[rows[j].id] == true);
			return true;
		case "z.vflex":
		case "z.size":
			zkau.setAttr(this.element, name, value);
			this.recalcSize(true);
			return true;
		case "style":
		case "style.width":
		case "style.height":
			if (!this.paging) {
				zkau.setAttr(this.element, name, value);
				this.init();
				return true;
			}
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

	/** Changes the specified row as focused. */
	focus: function (row) {
		this._unsetFocusExcept(row);
		this._setFocus(row, true);
	},
	/** Sets focus to the specified row if it has the anchor. */
	_focusToAnc: function (row) {
		if (!row) return;

		var uuid = typeof row == 'string' ? row: row.id;
		var el = $e(uuid + "!cm");
		if (!el) el = $e(uuid + "!sel");
		if (el && el.tabIndex != -1) //disabled due to modal, see zk.disableAll
			zk.asyncFocus(el.id);
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
						else this._fixAnc(sel, false); //Bug 1505786 (called by setAttr with "selected")
				}
			} else {
				if (row && toFocus) this._unsetFocusExcept(row);
			}
		}

		//we always invoke _changeSelect to change focus
		if (row) {
			this._changeSelect(row, true);
			if (toFocus) this._setFocus(row, true);
			else this._fixAnc(row, true); //Bug 1505786
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
			var el = $e(row.id + "!cm");
			if (toSel) {
				if (el) el.checked = true;
				row.className = row.className + "sel";
				zkSel.onoutTo(row);
				setZKAttr(row, "sel", "true");
			} else {
				if (el) el.checked = false;
				var len = row.className.length;
				if (len > 3)
					row.className = row.className.substring(0, len - 3);
				zkSel.onoutTo(row);
				setZKAttr(row, "sel", "false");
			}
		}
		return changed;
	},
	/** Changes the focus status, and return whether it is changed. */
	_setFocus: function (row, toFocus) {
		if (!this._isValid(row)) return false;

		var changed = this._isFocus(row) != toFocus;
		if (changed) {
			this._fixAnc(row, toFocus);
			if (toFocus) {
				var el = $e(row.id + "!cm");
				if (!el) el = $e(row.id + "!sel");
				if (el && el.tabIndex != -1) //disabled due to modal, see zk.disableAll
					zk.asyncFocus(el.id);
				zkSel.cmonfocusTo(row);

				if (!this.paging && zk.gecko) this._render(5);
					//Firefox doesn't call onscroll when we moving by cursor, so...
			} else {
				zkSel.cmonblurTo(row);
			}
		}
		return changed;
	},
	_fixAnc: function (row, toAnc) {
		var el = $e(row.id + "!sel");
		if (toAnc) {
			if (!el && !$e(row.id + "!cm") && row.cells.length > 0) {
				el = document.createElement("A");
				el.href = "javascript:;";
				el.id = row.id + "!sel";
				el.innerHTML = " ";
				el.onfocus = zkSel.cmonfocus;
				el.onblur = zkSel.cmonblur;
				if (row.cells[0].firstChild) row.cells[0].insertBefore(el, row.cells[0].firstChild); 
				else row.cells[0].appendChild(el);
			}
		} else {
			zk.remove(el);
		}
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
		if (getZKAttr(this.element, "model") != "true") return;

		var rows = this.bodyrows;
		if (!rows.length) return; //no row at all

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var data = "";
		var min = this.body.scrollTop, max = min + this.body.offsetHeight;
		for (var j = 0; j < rows.length; ++j) {
			var r = rows[j];
			if ($visible(r)) {
				var top = zk.offsetTop(r);
				if (top + zk.offsetHeight(r) < min) continue;
				if (top >= max) break;
				if (getZKAttr(r, "loaded") != "true")
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
		this._calcHgh();

		if (this.paging) return; //nothing to adjust since single table

		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		//
		//Bug 1616056: we have to use style.width, if possible, since clientWidth
		//is sometime too big
		var wd = this.element.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			wd = this.element.clientWidth;
			if (wd) wd += "px";
		}
		if (wd) {
			this.body.style.width = wd;
			if (this.head) this.head.style.width = wd;
			if (this.foot) this.foot.style.width = wd;
		}

		var tblwd = this.body.clientWidth;
		if (zk.ie) //By experimental: see zk-blog.txt
			if (tblwd && this.body.offsetWidth - tblwd > 11) {
				if (--tblwd < 0) tblwd = 0;
				this.bodytbl.style.width = tblwd + "px";
			} else
				this.bodytbl.style.width = "";

		if (this.headtbl) {
			if (tblwd) this.head.style.width = tblwd + 'px';
			if (this.headtbl.rows.length)
				zk.cpCellWidth(this.headtbl.rows[0], this.bodyrows); //assign head's col width
		}
		if (this.foottbl) {
			if (tblwd) this.foot.style.width = tblwd + 'px';
			if (this.foottbl.rows.length)
				zk.cpCellWidth(this.foottbl.rows[0], this.bodyrows); //assign foot's col width
		}
	},
	/** Returns the visible row at the specified index. */
	_visiRowAt: function (index) {
		if (index >= 0) {
			var rows = this.bodyrows;
			for (var j = 0; j < rows.length; ++j) {
				var r = rows[j];
				if ($visible(r) && --index < 0)
					return r;
			}
		}
		return null;
	},
	_calcHgh: function () {
		var rows = this.bodyrows;
		var len = 0, lastVisiRow, firstVisiRow;
		for (var j = 0; j < rows.length; ++j) { //tree might collapse some items
			var r = rows[j];
			if ($visible(r)) {
				if (!firstVisiRow) firstVisiRow = r;
				lastVisiRow = r;
				++len;
			}
		}

		var hgh = this.element.style.height;
		if (hgh && hgh != "auto" && hgh.indexOf('%') < 0) {
			hgh = $int(hgh);
			if (hgh) {
				hgh -= this._headHgh(0);
				if (hgh < 20) hgh = 20;
				var sz = 0;
				for (var h, j = 0;; ++sz, ++j) {
					if (sz == len) {
						sz = Math.ceil(sz && h ? (hgh * sz)/h: hgh/this._headHgh(20));
						break;
					}

					//next visible row
					var r;
					for (;; ++j) {//no need to check length again
						r = rows[j];
						if ($visible(r)) break;
					}

					h = zk.offsetTop(r) + zk.offsetHeight(r);
					if (h >= hgh) {
						if (h > hgh + 2) ++sz; //experimental
						break;
					}
				}

				this.realsize(sz);
				this.body.style.height = hgh + "px";
				return; //done
			}
		}

		hgh = 0;
		var diff = 2/*experiment*/, sz = this.size();
		if (!sz) {
			if (getZKAttr(this.element, "vflex") == "true") {
				hgh = this._vflexSize();
				if (hgh < 25) hgh = 25;

				var rowhgh = zk.offsetHeight(firstVisiRow);
				if (!rowhgh) rowhgh = this._headHgh(20);

				sz = Math.round((hgh - diff)/ rowhgh);
				if (sz < 3) { //minimal 3 rows if auto-size
					sz = 3;
					hgh = rowhgh * 3 + diff;
				}
			}
			this.realsize(sz);
		}

		if (sz) {
			if (!hgh) {
				if (!len) hgh = this._headHgh(20) * sz;
				else if (sz <= len) {
					var r = this._visiRowAt(sz - 1);
					hgh = zk.offsetTop(r) + zk.offsetHeight(r);
				} else {
					hgh = zk.offsetTop(lastVisiRow) + zk.offsetHeight(lastVisiRow);
					hgh = Math.ceil((sz * hgh) / len);
				}
				if (zk.ie) hgh += diff; //strange in IE (or scrollbar shown)
			}

			this.body.style.height = hgh + "px";
		} else {
			//if no hgh but with horz scrollbar, IE will show vertical scrollbar, too
			//To fix the bug, we extend the height
			hgh = this.element.style.height;
			if (zk.ie && (!hgh || hgh == "auto")
			&& this.body.offsetWidth - this.body.clientWidth > 11) {
				this.body.style.height =
					(this.body.offsetHeight * 2 - this.body.clientHeight) + "px";
			} else {
				this.body.style.height = "";
			}
		}
	},
	/* Height of the head row. If now header, defval is returned. */
	_headHgh: function (defVal) {
		var n = this.headtbl;
		n = n && n.rows.length ? n.rows[0]: null;
		var hgh = n ? zk.offsetHeight(n): 0;
		return hgh ? hgh: defVal;
	},
	/** Returns the size for vflex
	 */
	_vflexSize: function () {
		var diff = document.body.offsetHeight - zk.innerHeight()
				+ $int(Element.getStyle(document.body, "margin-top"))
				+ $int(Element.getStyle(document.body, "margin-bottom"))
				+ $int(Element.getStyle(document.body, "padding-top"))
				+ $int(Element.getStyle(document.body, "padding-bottom"));
		if (zk.ie) ++diff; //strange, but...

		//check whether TD
		if (diff > 0)
			for (var n = this.element, p; p = n.parentNode; n = p) {
				if ($tag(p) == "TD") {
					//whether other cells heigher than this
					if (p.clientHeight - n.offsetHeight > 20) //to be precise we have to count margin instead of 20
						diff = 0; //no need to shrink
					break;
				}
			}
		return this.body.offsetHeight - diff;
	},

	/** Recalculate the size. */
	recalcSize: function (cleansz) {
		if (cleansz) this.cleanSize();
		setTimeout("zkSel._calcSize('"+this.id+"')", 50);
	},
	cleanSize: function () {
		if (this.paging) return; //nothing to adjust since single table

		this.body.style.width = this.bodytbl.style.width = "";
		if (this.headtbl) {
			this.head.style.width = this.body.style.height = "";
			if (this.headtbl.rows.length) {
				var headrow = this.headtbl.rows[0];
				for (var j = headrow.cells.length; --j >=0;)
					headrow.cells[j].style.width = "";
			}
		}
		if (this.foottbl) {
			this.foot.style.width = this.body.style.height = "";
			if (this.foottbl.rows.length) {
				var footrow = this.foottbl.rows[0];
				for (var j = footrow.cells.length; --j >=0;)
					footrow.cells[j].style.width = "";
			}
		}
	},
	/** Resize the specified column. */
	resizeCol: function (cmp, icol, col1, wd1, col2, wd2, keys) {
		var rows = this.bodyrows;
		if (rows) {
			zulHdr.resizeAll(rows, cmp, icol, col1, wd1, col2, wd2, keys);
			this.recalcSize();
		}
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

	/** Returns z.selId (aka., the id of the selected item), or null if
	 * no one is ever selected.
	 */
	_getSelectedId: function () {
		var selId = getZKAttr(this.element, "selId");
		if (!selId) {
			alert(mesg.INVALID_STRUCTURE + "z.selId not found");
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
		return getZKAttr(row, "sel") == "true";
	},
	/** Whether an item has focus. */
	_isFocus: function (row) {
		return $e(row.id + "!sel") || $e(row.id + "!cm");
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
			var row = $e(this._getSelectedId());
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
		var nm = getZKAttr(this.element, "name");
		if (!nm || !this.form) return;

		for (var j = 0; j < this.form.elements.length; ++j){
			var el = this.form.elements[j];
			if (getZKAttr(el, "hiddenBy") == this.id) {
				zk.remove(el);
				--j;
			}
		}

		for (var j = 0; j < this.bodyrows.length; ++j) {
			var r = this.bodyrows[j];
			if (this._isSelected(r)) {
				var inp = document.createElement("INPUT");
				inp.type = "hidden";
				inp.name = nm;
				inp.value = getZKAttr(r, "value");
				setZKAttr(inp, "hiddenBy", this.id);
				this.form.appendChild(inp);
			}
		}
	}
};

////
// Utilities to help implement zk.Selectable //
zkSel = {};

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
	return !el || ((tn == "INPUT" && !el.id.endsWith("!cm"))
	|| tn == "TEXTAREA" || tn == "BUTTON" || tn == "SELECT" || tn == "OPTION");
};

/** row's onmouseover. */
zkSel.onover = function (evt) {
	if (!zk.dragging) {
		if (!evt) evt = window.event;
		var row = $parentByTag(Event.element(evt), "TR");
		if (row) {
			zk.backupStyle(row, "backgroundColor");
			row.style.backgroundColor = Selectable_bgcolor(row);
		}
	}
};
/** row's onmouseout. */
zkSel.onout = function (evt) {
	if (!zk.dragging) {
		if (!evt) evt = window.event;
		zkSel.onoutTo($parentByTag(Event.element(evt), "TR"));
	}
};
zkSel.onoutTo = function (row) {
	if (row)
		zk.restoreStyle(row, "backgroundColor");
};
/** (!cm or !sel)'s onfocus. */
zkSel.cmonfocus = function (evt) {
	if (!evt) evt = window.event;
	zkSel.cmonfocusTo($parentByTag(Event.element(evt), "TR"));
};
/** (!cm or !sel)'s onblur. */
zkSel.cmonblur = function (evt) {
	if (!evt) evt = window.event;
	zkSel.cmonblurTo($parentByTag(Event.element(evt), "TR"));
};
zkSel.cmonfocusTo = function (row) {
	if (row)
		if (!zk.gecko) row.style.textDecoration = "underline";
		else if (row.cells.length) row.cells[0].style.textDecoration = "underline";	
};
zkSel.cmonblurTo = function (row) {
	if (row) 
		if (!zk.gecko) row.style.textDecoration = "";
		else if (row.cells.length) row.cells[0].style.textDecoration = "";
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

/** Called when focus command is received. */
zkLibox.focus = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta._refocus();
	return true;
};
/** Process the setAttr cmd sent from the server, and returns whether to
 * continue the processing of this cmd
 */
zkLibox.setAttr = function (cmp, name, value) {
	var meta = zkau.getMeta(cmp);
	return meta && meta.setAttr(name, value);
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
zkLibox.childchg = zkLibox.init;

/** Called when a listbox becomes visible because of its parent. */
zkLibox.onVisi = zkLibox.onSize = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) meta.init();
};

zkLit = {}; //listitem
zkLit.init = function (cmp) {
	//zk.disableSelection(cmp);
	//Tom Yeh: 20060106: side effect: unable to select textbox if turned on

	zk.listen(cmp, "click", zkLibox.onclick);
	zk.listen(cmp, "keydown", zkLibox.onkeydown);
	zk.listen(cmp, "mouseover", zkSel.onover);
	zk.listen(cmp, "mouseout", zkSel.onout);
};
/** Called when _onDocCtxMnu is called. */
zkLit.onrtclk = function (cmp) {
	var meta = zkau.getMetaByType(cmp, "Libox");
	if (meta && !meta._isSelected(cmp)) meta.doclick(null, cmp);
};

zkLcfc = {}; //checkmark or the first hyperlink of listcell
zkLcfc.init = function (cmp) {
	zk.listen(cmp, "focus", zkSel.cmonfocus);
	zk.listen(cmp, "blur", zkSel.cmonblur);
};

zk.addModuleInit(function () {
	//Listheader
	//init it later because zul.js might not be loaded yet
	zkLhr = {}
	Object.extend(zkLhr, zulHdr);

	/** Resize the column. */
	zkLhr.resize = function (col1, col2, icol, wd1, wd2, keys) {
		var box = $parentByType(col1, "Libox");
		if (box) {
			var meta = zkau.getMeta(box);
			if (meta)
				meta.resizeCol(
					$parentByType(col1, "Lhrs"), icol, col1, wd1, col2, wd2, keys);
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
	zk.listen(cmp, "focus", zkau.onfocus);
	zk.listen(cmp, "blur", zkau.onblur);
};
/** Handles onchange from select/list. */
zkLisel.onchange = function (evtel) {
	var cmp = zkau.evtel(evtel); //backward compatible with 2.4 or before
	var data;
	if (cmp.multiple) {
		//To reduce # of bytes to send, we use a string instead of array.
		data = "";
		var opts = cmp.options;
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
		var opt = cmp.options[cmp.selectedIndex];
		data = opt.id;
	}
	var uuid = $uuid(cmp);
	zkau.send({uuid: uuid, cmd: "onSelect", data: [data]},
			zkau.asapTimeout(uuid, "onSelect"));
};
