/* SelectWidget.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function() {
	function _beforeChildKey(wgt, evt) {
		if (zAu.processing() || wgt._shallIgnoreEvent(evt))
			return true;

		var row = wgt._focusItem || wgt.getSelectedItem();
		if (!row) return true;
	}
	function _afterChildKey(evt) {
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
			evt.stop();
			return false;
		}
		return true;
	}

/**
 * A skeletal implementation for a select widget.
 */
zul.sel.SelectWidget = zk.$extends(zul.mesh.MeshWidget, {
	_rows: 0,
	$init: function () {
		this.$supers('$init', arguments);
		this._selItems = [];
	},
	$define: {
		/**
		 * Returns the rows. Zero means no limitation.
		 * <p>
		 * Default: 0.
		 * @return int
		 */
		/**
		 * Sets the rows.
		 * <p>
		 * Note: if both {@link #setHeight} is specified with non-empty,
		 * {@link #setRows} is ignored
		 * @param int rows
		 */
		rows: function (rows) {
			var n = this.$n();
			if (n) {
				n._lastsz = null;
				this.onSize();
			}
		},
		/**
		 * Returns whether the check mark shall be displayed in front of each item.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		/**
		 * Sets whether the check mark shall be displayed in front of each item.
		 * <p>
		 * The check mark is a checkbox if {@link #isMultiple} returns true. It is a
		 * radio button if {@link #isMultiple} returns false.
		 * @param boolean checkmark
		 */
		checkmark: function (checkmark) {
			if (this.desktop)
				this.rerender();
		},
		/**
		 * Returns whether multiple selections are allowed.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		/**
		 * Sets whether multiple selections are allowed.
		 * @param boolean multiple
		 */
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
		/**
		 * Returns the index of the selected item (-1 if no one is selected).
		 * @return int
		 */
		/**
		 * Deselects all of the currently selected items and selects the item with
		 * the given index.
		 * @param int selectedIndex
		 */
		selectedIndex: [
			function (v) {
				return v < -1 || (!v && v !== 0) ? -1 : v;
			},
			function() {
				var selected = this._selectedIndex;
				this.clearSelection();
				this._selectedIndex = selected;
				if (selected > -1) {
					var w;
					for (var it = this.getBodyWidgetIterator(); selected-- >=0;)
						w = it.next();
					if (w) {
						this._selectOne(w, false);
						zk(w).scrollIntoView(this.ebody);
					}						
				}
			}
		],
		/**
		 * Returns the name of this component.
		 * <p>
		 * Default: null.
		 * <p>
		 * The name is used only to work with "legacy" Web application that handles
		 * user's request by servlets. It works only with HTTP/HTML-based browsers.
		 * It doesn't work with other kind of clients.
		 * <p>
		 * Don't use this method if your application is purely based on ZK's
		 * event-driven model.
		 * @return String
		 */
		/**
		 * Sets the name of this component.
		 * <p>
		 * The name is used only to work with "legacy" Web application that handles
		 * user's request by servlets. It works only with HTTP/HTML-based browsers.
		 * It doesn't work with other kind of clients.
		 * <p>
		 * Don't use this method if your application is purely based on ZK's
		 * event-driven model.
		 * 
		 * @param String name
		 *            the name of this component.
		 */
		name: function () {
			if (this.destkop) this.updateFormData();	
		}
	},
	updateFormData: function () {
		if (this._name) {
			if (!this.efield) 
				this.efield = jq(this.$n()).append('<div style="display:none;"></div>').find('> div:last-child')[0];
			
			jq(this.efield).children().remove();
			
			// don't use jq.newHidden() in this case, because the performance is not good.
			var data = [],
				tmp = '<input type="hidden" name="' + this._name + '" value="';
			for (var i = 0, j = this._selItems.length; i < j; i++)
				data.push(tmp, this._selItems[i].getValue(), '"/>');
				
			jq(this.efield).append(data.join(''));
		} else if (this.efield) {
			jq(this.efield).remove();
			this.efield = null;
		}
	},
	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #selectItem}.
	 * @param ItemWidget item
	 */
	setSelectedItem: function (item) {
		item = zk.Widget.$(item);
		if (item) {
			this._selectOne(item, false);
			zk(item).scrollIntoView(this.ebody);
		}
	},
	/**
	 * Returns the selected item.
	 * @return ItemWidget
	 */
	getSelectedItem: function () {
		return this._selItems[0];
	},
	/**
	 * Returns all selected items.
	 * @return Array
	 */
	getSelectedItems: function () {
		// returns a readonly array
		return this._selItems.$clone();
	},
	setHeight: function (height) {
		if (this._height != height) {
			this._height = height;
			var n = this.$n();
			if (n) {
				n.style.height = height || '';
				this.onSize();
			}
		}
	},
	/* Calculates the size. */
	_calcSize: function () {
		
		//Bug in B30-1926094-1.zul 
		if (zk.ie)
			this._syncFocus(this._focusItem);
			
		this._calcHgh();
		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		//note: we don't solve this bug for paging yet
		var n = this.$n(),
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
			if (this.isSizedByContent() && this.ebodyrows && this.ebodyrows.length)
				this.$class._adjHeadWd(this);
			else if (tblwd && this.efoot) this.efoot.style.width = tblwd + 'px';
		} else if (this.efoot) {
			if (tblwd) this.efoot.style.width = tblwd + 'px';
			if (this.efoottbl.rows.length && this.ebodyrows && this.ebodyrows.length)
				this.$class.cpCellWidth(this);
		}
		n._lastsz = {height: n.offsetHeight, width: n.offsetWidth}; // cache for the dirty resizing.
	},
	_calcHgh: function () {
		var rows = this.ebodyrows,
			n = this.$n(),
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
			}
			this._visibleRows(nRows);
		}

		if (nRows) {
			if (!hgh) {
				if (!nVisiRows) hgh = this._headHgh(20) * nRows;
				else {
					var tpad = this.$n('tpad'),
						tpadhgh = (tpad ? tpad.offsetHeight : 0);
					if (nRows <= nVisiRows) {
						var $midVisiRow = zk(midVisiRow);
						hgh = $midVisiRow.offsetTop() + $midVisiRow.offsetHeight() - tpadhgh;
					} else {
						var $lastVisiRow = zk(lastVisiRow);
						hgh = $lastVisiRow.offsetTop() + $lastVisiRow.offsetHeight() - tpadhgh;
						hgh = Math.ceil((nRows * hgh) / nVisiRows);
					}
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
				
				// Bug #2129992 somethings the size of the offsetHeight in IE6,
				// IE7, and IE7 in compatible mode is wrong.
				if (zk.ie && !zk.ie8 && this.ebodytbl) {
					var ow = this.ebody.offsetWidth,
						cw = this.ebody.clientWidth,
						w = ow - cw;
					if (cw && w > 11) {
						if (ow == this.ebodytbl.offsetWidth)
							this.ebodytbl.style.width = jq.px0(zk(this.ebodytbl).revisedWidth(this.ebodytbl.offsetWidth - w));
					}
				}
				
				h = this.ebody.offsetHeight - this.ebody.clientHeight;
				
				// Bug #2805177, we have to check the clientWidth first.
				if (this.ebody.clientHeight && h > 11)
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
				hgh = this.ebody.offsetHeight - this.ebody.clientHeight;
				
				// Bug #2805177, we have to check the clientWidth first.
				if (this.ebody.clientHeight && hgh > 11)
					this.ebody.style.height = this.ebody.offsetHeight + jq.scrollbarWidth() + "px";
			}
		}
	},
	/* Returns the real # of rows (aka., real size). */
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
			var pgit = this.$n('pgit'),
				pgib = this.$n('pgib');
			if (pgit) hgh += pgit.offsetHeight;
			if (pgib) hgh += pgib.offsetHeight;
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
		this.updateFormData();
	},
	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #setSelectedItem}.
	 * 
	 * @param ItemWidget item
	 *            the item to select. If null, all items are deselected.
	 */
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
				item._setSelectedDirectly(true);
			}
			this._selItems.push(item);
		}
	},
	_removeItemFromSelection: function (item) {
		if (item.isSelected()) {
			if (!this._multiple) {
				this.clearSelection();
			} else {
				item._setSelectedDirectly(false);
				this._selItems.$remove(item);				
			}
		}
	},
	/**
	 * Clears the selection.
	 */
	clearSelection: function () {
		if (this._selItems.length) {
			for(var item;(item = this._selItems.pop());)
				item._setSelectedDirectly(false);
			this._selectedIndex = -1;
		}
	},
	//super
	focus: function (timeout) {
		var btn;
		if (this.isVisible() && this.canActivate({checkOnly:true})
		&& (btn = this.$n('a'))) {
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
		var btn = this.$n('a');
		if (btn)
			this.domListen_(btn, 'onFocus', 'doFocus_')
				.domListen_(btn, 'onKeyDown')
				.domListen_(btn, 'onBlur', 'doBlur_');
		this.updateFormData();
	},
	unbind_: function () {
		var btn = this.$n('a');
		if (btn)
			this.domUnlisten_(btn, 'onFocus', 'doFocus_')
				.domUnlisten_(btn, 'onKeyDown')
				.domUnlisten_(btn, 'onBlur', 'doBlur_');
		this.efield = null;
		this.$supers('unbind_', arguments);
	},
	doBlur_: function (evt) {
		if (this._focusItem) {
			this._lastSelectedItem = this._focusItem;
			this._focusItem._doFocusOut();
		}
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
		if (tn == "A" || (!zul.sel.ItemWidget.isInstance(tw) && //Bug 2379135
		(tw.isListen('onDoubleClick') || tw.isListen('onClick')
		|| tw.getContext() || target.onclick)))
			return;

		var	row = evt.target,
			checkmark = target == row.$n('cm');
			
		if (checkmark) {
			if (this.isMultiple()) {
				this._toggleSelect(row, !row.isSelected(), evt);
			} else {
				this._select(row, evt);
			}
		} else {
		//Bug 1650540: double click as select again
		//Note: we don't handle if clicking on checkmark, since FF always
		//toggle and it causes incosistency
			if ((zk.gecko || zk.safari) && row.isListen('onDoubleClick')) {
				var now = zUtl.now(), last = row._last;
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
	/* Handles keydown sent to the body. */
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
		var tag = evt.domTarget.tagName;
		if (!zk.gecko3 || (tag != "INPUT" && tag != "TEXTAREA"))
			zk(this.$n()).disableSelection();
			
		// Feature #1978624
		if (evt.target == this) //try to give to the focus item
			evt.target = this._focusItem || this.getSelectedItem() || this;
		this.$supers('doKeyDown_', arguments);
	},
	doKeyUp_: function (evt) {
		zk(this.$n()).enableSelection();
		evt.stop({propagation: true});
		this.$supers('doKeyUp_', arguments);
	},
	_doKeyDown: function (evt) { //called by listener of this widget and ItemWidget
		if (_beforeChildKey(this, evt))
			return true;

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
			var nrow = row.$n();
			for (;nrow && (nrow = step > 0 ? nrow.nextSibling: nrow.previousSibling);) {
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

		return _afterChildKey(evt);
	},
	_doKeyUp: function (evt) { //called by ItemWidget only
		return _beforeChildKey(this, evt) || _afterChildKey(evt);
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
		(zk.isLoaded('zul.wgt') && evt.target.$instanceof(zul.wgt.Button)))
	},
	/* maintain the offset of the focus proxy*/
	_syncFocus: function (row) {
		var focusEl = this.$n('a'),
			offs;
		if (row) {
			var n = row.$n(), offs = zk(n).revisedOffset();
			offs = this._toStyleOffset(focusEl, offs[0] + this.ebody.scrollLeft, offs[1]);
		} else
			offs = [0, 0];
		focusEl.style.top = offs[1] + "px";
		focusEl.style.left = offs[0] + "px";
	},
	_toStyleOffset: function (el, x, y) {
		var ofs1 = zk(el).revisedOffset(),
			x2 = zk.parseInt(el.style.left), y2 = zk.parseInt(el.style.top);;
		return [x - ofs1[0] + x2, y  - ofs1[1] + y2];
	},
	/* Selects an item, notify server and change focus if necessary. */
	_select: function (row, evt) {
		if (this._selectOne(row, true)) {
			//notify server
			this.fireOnSelect(row, evt);
		}
	},
	/* Selects a range from the last focus up to the specified one.
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
	/**
	 * Selects all items.
	 * @param boolean isSelectAll
	 */
	setSelectAll: _zkf = function (notify, evt) {
		for (var it = this.getBodyWidgetIterator(), w; (w = it.next());)
			if (!w.isDisabled())
				this._changeSelect(w, true);
		if (notify && evt !== true)
			this.fireOnSelect(this.getSelectedItem(), evt);
	},
	/**
	 * Selects all items.
	 * @param boolean notify if true, fire onSelect event to server
	 * @param jq.Event evt
	 */
	selectAll: _zkf,
	/* Selects one and deselect others, and return whehter any changes.
	 * It won't notify the server.
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
	/* Toggle the selection and notifies server. */
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
		var edata = evt.data, keep;
		if (this._multiple)
			keep = edata.ctrlKey || edata.shiftKey || (evt.domTarget.id ? evt.domTarget.id.endsWith('-cm') : false);

		this.fire('onSelect', zk.copy({items: data, reference: reference, clearFirst: !keep}, edata));
	},
	/* Changes the specified row as focused. */
	_focus: function (row) {
		if (this.canActivate({checkOnly:true})) {
			this._unsetFocusExcept(row);
			this._setFocus(row, true);
		}
	},
	/* Changes the selected status of an item without affecting other items
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
	/* Changes the focus status, and return whether it is changed. */
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
	/* Cleans selected except the specified one, and returns any selected status
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
	/* Cleans selected except the specified one, and returns any selected status
	 * is changed.
	 */
	_unsetFocusExcept: function (row) {
		if (this._focusItem && this._focusItem != row) 
			this._setFocus(this._focusItem, false)
		else
			this._focusItem = null;
	}
});

})();
