/* SelectWidget.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.SelectWidget = zk.$extends(zul.mesh.MeshWidget, {
	_rows: 0,
	$init: function () {
		this.$supers('$init', arguments);
		this._selItems = [];
	},
	$define: {
		rows: function (rows) {
			if (this.desktop)
				this.onSize();
		},
		checkmark: function (checkmark) {
			if (this.desktop)
				this.rerender();
		},
		multiple: function (multiple) {
			if (!this._multiple && this._selItems.length) {
				var item = this.getSelectedItem();
				for (var it; (it = this._selItems.pop());) 
					if (it != item) {
						if (!this._checkmark)
							it._setSelectedDirectly(false);
						else it._selected = false;
					}
					
				this._selItems.push(item);
				
				// reset the selected id
				this._chgSel = item.uuid;
			}
			if (this._checkmark && this.desktop)
				this.rerender();
		},
		chgSel: function (val) {
			var sels = {};
			for (var j = 0;;) {
				var k = val.indexOf(',', j),
					s = (k >= 0 ? val.substring(j, k): val.substring(j)).trim();
				if (s) sels[s] = true;
				if (k < 0) break;
				j = k + 1;
			}
			for (var it = this.getBodyWidgetIterator(), w; (w = it.next());)
				this._changeSelect(w, sels[w.uuid] == true);
		},
		selectedIndex: null
	},
	setSelectedItem: function (item) {
		item = zk.Widget.$(item);
		if (this._selectedItem != item) {
			this._selectedItem = item;
			if (item) {
				this._selectOne(item, false);
				zk(item).scrollIntoView(this.ebody);
			}
		}
	},
	getSelectedItem: function () {
		return this._selItems[0];
	},
	getSelectedItems: function () {
		// returns a readonly array
		return this._selItems.$clone();
	},
	setHeight: function (height) {
		if (this._height != height) {
			this._height = height;
			var n = this.getNode();
			if (n) {
				 n.style.height = v || '';
				this.onSize();
			}
		}
	},
	onSize: function () {
		if (this.isRealVisible()) {
			var n = this.getNode();
			if (n._lastsz && n._lastsz.height == n.offsetHeight && n._lastsz.width == n.offsetWidth)
				return; // unchanged
				
			this._calcSize();// Bug #1813722
			this.fireOnRender(155);
			
			
			// ToBeFixed
			//if (zk.opera && meta.body && meta._scrollTop)
			//	meta.body.scrollTop = meta._scrollTop;
			 
		}
	},
	/** Calculates the size. */
	_calcSize: function () {
		this._calcHgh();
		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		//note: we don't solve this bug for paging yet
		var n = this.getNode(),
			wd = n.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			wd = zk(n).revisedWidth(n.offsetWidth);
			if (wd < 0) wd = 0;
			if (wd) wd += "px";
		}
		if (wd) {
			this.ebody.style.width = wd;
			if (this.ehead) this.ehead.style.width = wd;
			if (this.efoot) this.efoot.style.width = wd;
		}
		
		//Bug 1659601: we cannot do it in init(); or, IE failed!
		var tblwd = this.ebody.clientWidth;
		if (zk.ie) {//By experimental: see zk-blog.txt
			if (this.eheadtbl &&
			this.eheadtbl.offsetWidth !=
			this.ebodytbl.offsetWidth) 
				this.ebodytbl.style.width = ""; //reset 
			if (tblwd && this.ebody.offsetWidth == this.ebodytbl.offsetWidth &&
			this.ebody.offsetWidth - tblwd > 11) { //scrollbar
				if (--tblwd < 0) 
					tblwd = 0;
				this.ebodytbl.style.width = tblwd + "px";
			}
		}
		if (this.ehead) {
			if (tblwd) this.ehead.style.width = tblwd + 'px';
			if (!this.isFixedLayout() && this.ebodyrows && this.ebodyrows.length)
				this.$class._adjHeadWd(this);
		} else if (this.efoot) {
			if (tblwd) this.efoot.style.width = tblwd + 'px';
			if (this.efoottbl.rows.length && this.ebodyrows && this.ebodyrows.length)
				this.$class.cpCellWidth(this);
		}
		n._lastsz = {height: n.offsetHeight, width: n.offsetWidth}; // cache for the dirty resizing.
	},
	_calcHgh: function () {
		var rows = this.ebodyrows,
			n = this.getNode(),
			hgh = n.style.height,
			isHgh = hgh && hgh != "auto" && hgh.indexOf('%') < 0;
		if (isHgh) {
			hgh = zk.parseInt(hgh);
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
						if (zk(r).isVisible()) break;
					}

					var $r = zk(r);
					h = $r.offsetTop() + $r.offsetHeight();
					if (h >= hgh) {
						if (h > hgh + 2) ++sz; //experimental
						break;
					}
				}
				sz = Math.ceil(sz && h ? (hgh * sz)/h: hgh/this._headHgh(20));

				this._visibleRows(sz);

                hgh -= (this.efoot ? this.efoot.offsetHeight : 0);
                this.ebody.style.height = (hgh < 0 ? 0 : hgh) + "px";

				//2007/12/20 We don't need to invoke the body.offsetHeight to avoid a performance issue for FF.
				if (zk.ie && this.ebody.offsetHeight) {} // bug #1812001.
				// note: we have to invoke the body.offestHeight to resolve the scrollbar disappearing in IE6
				// and IE7 at initializing phase.
				return; //done
			}
		}

		var nVisiRows = 0, nRows = this.getRows(), lastVisiRow, firstVisiRow, midVisiRow;
		for (var j = 0, rl = rows.length; j < rl; ++j) { //tree might collapse some items
			var r = rows[j];
			if (zk(r).isVisible()) {
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
			if (this.isVflex()) {
				hgh = this._vflexSize(n.style.height);

				if (zk.ie && this._cachehgh != hgh) {
					hgh -= 1; // need to display the bottom border.
					this._cachehgh = hgh;
				}
				if (hgh < 25) hgh = 25;

				var rowhgh = zk(firstVisiRow).offsetHeight();
				if (!rowhgh) rowhgh = this._headHgh(20);

				nRows = Math.round((hgh - diff)/ rowhgh);
				if (nRows < 3) { //minimal 3 rows if auto-size
					nRows = 3;
					hgh = rowhgh * 3 + diff;
				}
			}
			this._visibleRows(nRows);
		}

		if (nRows) {
			if (!hgh) {
				if (!nVisiRows) hgh = this._headHgh(20) * nRows;
				else if (nRows <= nVisiRows) {
					var $midVisiRow = zk(midVisiRow);
					hgh = $midVisiRow.offsetTop() + $midVisiRow.offsetHeight();
				} else {
					var $lastVisiRow = zk(lastVisiRow);
					hgh = $lastVisiRow.offsetTop() + $lastVisiRow.offsetHeight();
					hgh = Math.ceil((nRows * hgh) / nVisiRows);
				}
				if (zk.ie) hgh += diff; //strange in IE (or scrollbar shown)
			}

			this.ebody.style.height = hgh + "px";

			//2007/12/20 We don't need to invoke the body.offsetHeight to avoid a performance issue for FF.
			if (zk.ie && this.ebody.offsetHeight) {} // bug #1812001.
			// note: we have to invoke the body.offestHeight to resolve the scrollbar disappearing in IE6
			// and IE7 at initializing phase.
			// 2008/03/28 The unnecessary scroll bar will appear when the vflex is true.
			
			// bug #2799258
			var h = n.style.height;
			if (!h || h == "auto") {
				h = zk.gecko ? this.ebody.offsetHeight - this.ebody.clientHeight : this.ebody.offsetWidth - this.ebody.clientWidth;
				if (h > 11)
					this.ebody.style.height = hgh + jq.scrollbarWidth() + "px";
			}
		} else {
			//if no hgh but with horz scrollbar, IE will show vertical scrollbar, too
			//To fix the bug, we extend the height
			hgh = n.style.height;
			if (zk.ie && (!hgh || hgh == "auto")
			&& this.ebody.offsetWidth - this.ebody.clientWidth > 11) {
				if (!nVisiRows) this.ebody.style.height = ""; // bug #1806152 if start with 0px and no hgh, IE doesn't calculate the height of the element.
				else this.ebody.style.height =
						(this.ebody.offsetHeight * 2 - this.ebody.clientHeight) + "px";
			} else {
				this.ebody.style.height = "";
			}
			
			// bug #2799258
			if (!hgh || hgh == "auto") {
				hgh = this.ebody.offsetWidth - this.ebody.clientWidth;
				if (hgh > 11)
					this.ebody.style.height = this.ebody.offsetHeight + jq.scrollbarWidth() + "px";
			}
		}
	},
	/** Returns the real # of rows (aka., real size). */
	_visibleRows: function (v) {
		if ("number" == typeof v) {
			this._visiRows = v;
		} else
			return this.getRows() || this._visiRows || 0;
	},
	/* Height of the head row. If now header, defval is returned. */
	_headHgh: function (defVal) {
		var hgh = this.ehead ? this.ehead.offsetHeight : 0;
		if (this.paging) {
			var pgit = this.getSubnode('pgit'),
				pgib = this.getSubnode('pgib');
			if (pgit) h += pgit.offsetHeight;
			if (pgib) h += pgib.offsetHeight;
		}
		return hgh ? hgh: defVal;
	},
	indexOfItem: function (item) {
		if (item.getMeshWidget() == this) {
			for (var i = 0, it = this.getBodyWidgetIterator(), w; (w = it.next()); i++)
				if (w == item) return i;
		}
		return -1;
	},
	toggleItemSelection: function (item) {
		if (item.isSelected()) this._removeItemFromSelection(item);
		else this._addItemToSelection(item);
	},
	selectItem: function (item) {
		if (!item)
			this.setSelectedIndex(-1);
		else if (this._multiple || !item.isSelected())
			this.setSelectedIndex(this.indexOfItem(item));
	},
	_addItemToSelection: function (item) {
		if (!item.isSelected()) {
			if (!this._multiple) {
				this.selectItem(item);
			} else {
				var index = this.indexOfItem(item);
				if (index < this._selectedIndex || this._selectedIndex < 0) {
					this._selectedIndex = index;
				}
				item._selected = true;
			}
			this._selItems.push(item);
		}
	},
	_removeItemFromSelection: function (item) {
		if (item.isSelected()) {
			if (!this._multiple) {
				this.clearSelection();
			} else {
				item._selected = false;
				this._selItems.$remove(item);				
			}
		}
	},
	clearSelection: function () {
		if (this._selItems.length) {
			for(var item;(item = this._selItems.pop());)
				item._selected = false;
			this._selectedIndex = -1;
		}
	},
	//super
	focus: function (timeout) {
		var btn;
		if (this.isVisible() && this.canActivate({checkOnly:true})
		&& (btn = this.getSubnode('a'))) {
			if (this._focusItem) {
				for (var it = this.getBodyWidgetIterator(), w; (w = it.next());)
					if (this._isFocus(w)) {
						w.focus();
						break;
					}
			}
			zk(btn).focus(timeout);
			return true;
		}
		return false;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var btn = this.getSubnode('a');
		if (btn) {
			this.domListen_(btn, 'onFocus', 'doFocus_');
			this.domListen_(btn, 'onKeyDown');
			this.domListen_(btn, 'onBlur', 'doBlur_');
		}
	},
	unbind_: function () {
		var btn = this.getSubnode('a');
		if (btn) {
			this.domUnlisten_(btn, 'onFocus', 'doFocus_');
			this.domUnlisten_(btn, 'onKeyDown');
			this.domUnlisten_(btn, 'onBlur', 'doBlur_');
		}
		this.$supers('unbind_', arguments);
	},
	doBlur_: function (evt) {
		if (this._focusItem)
			this._lastSelectedItem = this._focusItem;
		this._focusItem = null;
		this.$supers('doBlur_', arguments);
	},
	_doClick: function (evt) {

		//It is better not to change selection only if dragging selected
		//(like Windows does)
		//However, FF won't fire onclick if dragging, so the spec is
		//not to change selection if dragging (selected or not)
		if (zk.dragging || this._shallIgnoreEvent(evt))
			return;
			
		var target = evt.domTarget,
			tn = target.tagName,
			tw = zk.Widget.$(target);
		if ((tn != "TR" && target.onclick)
				|| tn == "A" ||(tw != row &&
					(tw.isListen('onDoubleClick') || tw.isListen('onClick'))))
			return;

		var	row = evt.target,
			checkmark = target == row.getSubnode('cm');
			
		if (checkmark) {
			if (this.isMultiple()) {
				this._toggleSelect(row, target.checked, evt);
			} else {
				this._select(row, evt);
			}
		} else {
		//Bug 1650540: double click as select again
		//Note: we don't handle if clicking on checkmark, since FF always
		//toggle and it causes incosistency
			if ((zk.gecko || zk.safari) && row.isListen('onDoubleClick')) {
				var now = $now(), last = row._last;
				row._last = now;
				if (last && now - last < 900)
					return; //ignore double-click
			}
			this._syncFocus(row);
			if (this.isMultiple()) {
				if (evt.data.shiftKey)
					this._selectUpto(row, evt);
				else if (evt.data.ctrlKey)
					this._toggleSelect(row, !row.isSelected(), evt);
				else // Bug: 1973470
					this._select(row, evt);
			} else
				this._select(row, evt);

			//since row might was selected, we always enfoce focus here
			row.focus();
			//if (evt) Event.stop(evt);
			//No much reason to eat the event.
			//Oppositely, it disabled popup (bug 1578659)
		}
	},
	/** Handles keydown sent to the body. */
	doKeyDown_: function (evt) {
		if (!this._shallIgnoreEvent(evt)) {

		// Note: We don't intercept body's onfocus to gain focus back to anchor.
		// Otherwise, it cause scroll problem on IE:
		// When user clicks on the scrollbar, it scrolls first and call onfocus,
		// then it will scroll back to the focus because _focusToAnc is called
			switch (evt.data.keyCode) {
			case 33: //PgUp
			case 34: //PgDn
			case 38: //UP
			case 40: //DOWN
			case 37: //LEFT
			case 39: //RIGHT
			case 32: //SPACE
			case 36: //Home
			case 35: //End
				if (evt.domTarget.tagName != "A")
					this.focus();
				evt.stop();
				return false;
			}
		}
		this.$supers('doKeyDown_', arguments);
	},
	_doKeyDown: function (evt) {
		if (zAu.processing() || this._shallIgnoreEvent(evt))
			return true;

		var row = this._focusItem || this.getSelectedItem();
		if (!row) return true;
		var data = evt.data,
			shift = data.shiftKey, ctrl = data.ctrlKey;
		if (shift && !this.isMultiple())
			shift = false; //OK to

		var endless = false, step, lastrow;
		switch (data.keyCode) {
		case 33: //PgUp
		case 34: //PgDn
			step = this._visibleRows();
			if (step == 0) step = 20;
			if (data.keyCode == 33)
				step = -step;
			break;
		case 38: //UP
		case 40: //DOWN
			step = data.keyCode == 40 ? 1: -1;
			break;
		case 32: //SPACE
			if (this.isMultiple()) this._toggleSelect(row, !row.isSelected(), evt);
			else this._select(row, evt);
			break;
		case 36: //Home
		case 35: //End
			step = data.keyCode == 35 ? 1: -1;
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
			if (shift) this._toggleSelect(row, true, evt);
			var nrow = row.getNode();
			for (; (nrow = step > 0 ? nrow.nextSibling: nrow.previousSibling);) {
				var r = zk.Widget.$(nrow);
				if (r.$instanceof(zul.sel.Treerow))
					r = r.parent;
				if (!r.isDisabled()) {
					if (shift) this._toggleSelect(r, true, evt);

					if (zk(nrow).isVisible()) {
						if (!shift) lastrow = r;
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
			if (ctrl) this._focus(lastrow);
			else this._select(lastrow, evt);
			this._syncFocus(lastrow);
			zk(lastrow).scrollIntoView(this.ebody); // Bug #1823947 and #1823278
		}

		switch (data.keyCode) {
		case 33: //PgUp
		case 34: //PgDn
		case 38: //UP
		case 40: //DOWN
		case 37: //LEFT
		case 39: //RIGHT
		case 32: //SPACE
		case 36: //Home
		case 35: //End
			evt.stop();
			return false;
		}
		return true;
	},
	_doLeft: zk.$void,
	_doRight: zk.$void,
	_shallIgnoreEvent: function(evt) {
		var tn = evt.domTarget.tagName;
		return !evt.domTarget || !evt.target.canActivate() ||
		((tn == "INPUT" && !evt.domTarget.id.endsWith('-cm')) ||
		tn == "TEXTAREA" ||
		(tn == "BUTTON" && !evt.domTarget.id.endsWith('-a')) ||
		tn == "SELECT" ||
		tn == "OPTION" ||
		(zPkg.isLoaded('zul.wgt') && evt.target.$instanceof(zul.wgt.Button)))
	},
	/** maintain the offset of the focus proxy*/
	_syncFocus: function (row) {
		var focusEl = this.getSubnode('a'),
			n = row.getNode(),
			offs = zk(n).revisedOffset();
		offs = this._toStyleOffset(focusEl, offs[0] + this.ebody.scrollLeft, offs[1]);
		focusEl.style.top = offs[1] + "px";
		focusEl.style.left = offs[0] + "px";
	},
	_toStyleOffset: function (el, x, y) {
		var ofs1 = zk(el).revisedOffset(),
			x2 = zk.parseInt(el.style.left), y2 = zk.parseInt(el.style.top);;
		return [x - ofs1[0] + x2, y  - ofs1[1] + y2];
	},
	/** Selects an item, notify server and change focus if necessary. */
	_select: function (row, evt) {
		if (this._selectOne(row, true)) {
			//notify server
			this.fireOnSelect(row, evt);
		}
	},
	/** Selects a range from the last focus up to the specified one.
	 * Callable only if multiple
	 */
	_selectUpto: function (row, evt) {
		if (row.isSelected()) {
			this._focus(row);
			return; //nothing changed
		}

		var focusfound = false, rowfound = false;
		for (var it = this.getBodyWidgetIterator(), w; (w = it.next());) {
			if (w.isDisabled()) continue; // Bug: 2030986
			if (focusfound) {
				this._changeSelect(w, true);
				if (w == row)
					break;
			} else if (rowfound) {
				this._changeSelect(w, true);
				if (this._isFocus(w) || w == this._lastSelectedItem)
					break;
			} else {
				rowfound = w == row;
				focusfound = this._isFocus(w) || w == this._lastSelectedItem;
				if (rowfound || focusfound) {
					this._changeSelect(w, true);
					if (rowfound && focusfound)
						break;
				}
			}
		}

		this._focus(row);
		this.fireOnSelect(row, evt);
	},
	setSelectAll: _zkf = function (notify, evt) {
		for (var it = this.getBodyWidgetIterator(), w; (w = it.next());)
			if (!w.isDisabled())
				this._changeSelect(w, true);
		if (notify) this.fireOnSelect(this.getSelectedItem(), evt);
	},
	selectAll: _zkf,
	/** Selects one and deselect others, and return whehter any changes.
	 * It won't notify the server.
	 * @param row the row to select. Unselect all if null
	 * @param toFocus whether to change focus
	 */
	_selectOne: function (row, toFocus) {
		var selItem = this.getSelectedItem();
		if (this.isMultiple()) {
			if (row && toFocus) this._unsetFocusExcept(row);
			var changed = this._unsetSelectAllExcept(row);
			if (!changed && row && selItem == row) {
				if (toFocus) this._setFocus(row, true);
				return false; //not changed
			}
		} else {
			if (selItem) {
				if (selItem == row) {
					if (toFocus) this._setFocus(row, true);
					return false; //not changed
				}
				this._changeSelect(selItem, false);
				if (row)
					if(toFocus) this._setFocus(selItem, false);
			}
			if (row && toFocus) this._unsetFocusExcept(row);
		}
		//we always invoke _changeSelect to change focus
		if (row) {
			this._changeSelect(row, true);
			if (toFocus) this._setFocus(row, true);
		}
		return true;
	},
	/** Toggle the selection and notifies server. */
	_toggleSelect: function (row, toSel, evt) {
		if (!this.isMultiple()) {
			var old = this.getSelectedItem();
			if (row != old && toSel)
				this._changeSelect(row, false);
		}
		
		this._changeSelect(row, toSel);
		this._focus(row);

		//notify server
		this.fireOnSelect(row, evt);
	},
	fireOnSelect: function (reference, evt) {
		var data = [];
		
		for (var it = this.getSelectedItems(), len = it.length; --len >=0;)
			if (it[len].isSelected())
				data.push(it[len].uuid);
		this.fire('onSelect', zk.copy({items: data, reference: reference}, evt.data));
	},
	/** Changes the specified row as focused. */
	_focus: function (row) {
		if (this.canActivate({checkOnly:true})) {
			this._unsetFocusExcept(row);
			this._setFocus(row, true);
		}
	},
	/** Changes the selected status of an item without affecting other items
	 * and return true if the status is really changed.
	 */
	_changeSelect: function (row, toSel) {
		var changed = row.isSelected() != toSel;
		if (changed) {
			row.setSelected(toSel);
			row._toggleEffect(true);
		}
		return changed;
	},
	_isFocus: function (row) {
		return this._focusItem == row;
	},
	/** Changes the focus status, and return whether it is changed. */
	_setFocus: function (row, toFocus) {
		var changed = this._isFocus(row) != toFocus;
		if (changed) {
			if (toFocus) {
				if (!row.focus()) {
					this.focus();
				}
				if (!this.paging && zk.gecko) 
					this.fireOnRender(5);
					//Firefox doesn't call onscroll when we moving by cursor, so...
			}
		}
		if (!toFocus)
			row._doFocusOut();
		return changed;
	},
	/** Cleans selected except the specified one, and returns any selected status
	 * is changed.
	 */
	_unsetSelectAllExcept: function (row) {
		var changed = false;
		for (var it = this.getSelectedItems(), len = it.length; --len >= 0;) {
			if (it[len] != row && this._changeSelect(it[len], false))
				changed = true;
		}
		return changed;
	},
	/** Cleans selected except the specified one, and returns any selected status
	 * is changed.
	 */
	_unsetFocusExcept: function (row) {
		if (this._focusItem && this._focusItem != row) 
			this._setFocus(this._focusItem, false)
		else
			this._focusItem = null;
	}
});
