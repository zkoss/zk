/* SelectWidget.js

	Purpose:

	Description:

	History:
		Thu Apr 30 22:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The selectable widgets, such as listbox and tree.
 */
//zk.$package('zul.sel');

(function() {
	function _beforeChildKey(wgt, evt) {
		return zAu.processing() || wgt._shallIgnore(evt);
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
	
	function listenOnFitSize(wgt) {
		if (wgt._rows && !wgt._rowsOnFitSize) {
			zWatch.listen({onFitSize: wgt});
			wgt._rowsOnFitSize = true;
		}
	}
	function unlistenOnFitSize(wgt) {
		if (wgt._rowsOnFitSize) {
			zWatch.unlisten({onFitSize: wgt});
			delete wgt._rowsOnFitSize;
		}
	}
	function _updHeaderCM(box) { //update header's checkmark
		if (--box._nUpdHeaderCM <= 0 && box.desktop && box._headercm && box._multiple) {
			var zcls = zk.Widget.$(box._headercm).getZclass() + '-checked',
				$headercm = jq(box._headercm);
			$headercm[box._isAllSelected() ? 'addClass': 'removeClass'](zcls);
			zk($headercm).redoCSS(-1, {'fixFontIcon': true});
		}
	}
	function _isButton(evt) {
		return evt.target.$button //for extension, it makes a widget as a button
			|| (zk.isLoaded('zul.wgt')
			&& evt.target.$instanceof(zul.wgt.Button, zul.wgt.Toolbarbutton));
	}
	function _isInputWidget(evt) { // B50-ZK-460
		return evt.target.$inputWidget //for extension, it makes a widget as a input widget
			|| (zk.isLoaded('zul.inp') && evt.target.$instanceof(zul.inp.InputWidget));
	}
	function _focusable(evt) {
		return (jq.nodeName(evt.domTarget, 'input', 'textarea', 'button', 'select', 'option', 'a')
				&& !evt.target.$instanceof(zul.sel.SelectWidget))
			|| _isButton(evt) || _isInputWidget(evt);
	}
	function _fixReplace(w) {
		return w && (w = w.uuid) ? zk.Widget.$(w): null;
	}

var SelectWidget =
/**
 * A skeletal implementation for a select widget.
 */
zul.sel.SelectWidget = zk.$extends(zul.mesh.MeshWidget, {
	_rows: 0,
	/** Whether to change a list item selection on right click
	 * <p>Default: true (unless the server changes the setting)
	 * @since 5.0.5
	 * @type boolean
	 */
	rightSelect: true,
	_anchorTop:0,
	_anchorLeft:0,	
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
			listenOnFitSize(this);
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
			}
			if (this._checkmark && this.desktop)
				this.rerender();
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
						this._selectOne(w, true);
						if (!this._listbox$rod) {
							var bar = this._scrollbar;
							if (bar)
								bar.scrollToElement(item.$n());
						}
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
	setChgSel: function (val) { //called from the server
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
	setFocusIndex: function (index) { // called from server
		// F60-ZK-715
		if (index < 0)
			return;
		var self = this;
		setTimeout(function () { // items not ready yet
			var w;
			for (var it = self.getBodyWidgetIterator(); (w = it.next()) && index--;)
				if (!it.hasNext())
					break;
			self._focusItem = w;
		});
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
		if (!item)
			this.clearSelection();
		else {
			this._selectOne(item, true);
			// Bug ZK-1483: Jumpy scrollbar for listbox with rod when items are selected
			if (!this._listbox$rod) {
				var bar = this._scrollbar;
				if (bar)
					bar.scrollToElement(item.$n());
				if (this._nativebar)
					zk(item).scrollIntoView(this.ebody);
			}
			if (zk.ff >= 4 && this.ebody && this._nativebar) { // B50-ZK-293: FF5 misses to fire onScroll
				// B50-ZK-440: ebody can be null when ROD
				this._currentTop = this.ebody.scrollTop; 
				this._currentLeft = this.ebody.scrollLeft;
			}
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
		if (!this._nvflex && this._height != height) {
			this._height = height;
			var n = this.$n();
			if (n) {
				n.style.height = height || '';
				this.onSize();
			}
		}
	},
	setVflex: function(v) {
		this.$supers('setVflex', arguments);
		if (this.desktop) this.onSize();
	},
	setHflex: function(v) {
		this.$supers('setHflex', arguments);
		if (this.desktop) this.onSize();
	},
	_getEbodyWd: function () {
		var anchor = this.$n('a');
		// Bug in B30-1823236.zul, the anchor needs to be hidden before invoking this.ebody.clientWidth
		if (zk.webkit)
			anchor.style.display = 'none';

		//Bug 1659601: we cannot do it in init(); or, IE failed!
		var tblwd = zk.opera && this.ebody.offsetHeight == 0 ? // B50-ZK-269
				this.ebody.offsetWidth : this.ebody.clientWidth;

		if (zk.webkit)
			anchor.style.display = '';
		return tblwd;
	},
	_beforeCalcSize: function () {
		// Bug 279925
		if (zk.ie8) {
			var anchor = this.$n('a');
			this._oldCSS = anchor.style.display;
			anchor.style.display = 'none';
		}

		//Bug in B30-1926094-1.zul
		if (zk.ie)
			this._syncFocus(this._focusItem);

		this._calcHgh();
	},
	_afterCalcSize: function () {
		// Bug 279925
		if (zk.ie8) {
			this.$n('a').style.display = this._oldCSS;
			delete this._oldCSS;
		}
		this.$supers('_afterCalcSize', arguments);
	},
	onFitSize: function () {
		// B50-ZK-598: when having rows, height needs to be determined when onFitSize
		if (this._rows)
			this._calcHgh();
	},
	_calcHgh: function () {
		var rows = this.ebodyrows.rows,
			n = this.$n(),
			hgh = n.style.height,
			isHgh = hgh && hgh != 'auto' && hgh.indexOf('%') < 0;
		if (isHgh) {
			hgh = zk.parseInt(hgh) - zk(n).padBorderHeight();
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
				//bug# 3036398: frozen scrollbar disappear when listbox with vflex="1"
                hgh -= (this.efrozen && this._nativebar ? this.efrozen.offsetHeight : 0);
                this.ebody.style.height = (hgh < 0 ? 0 : hgh) + 'px';
				return; //done
			}
		}

		var nVisiRows = 0, nRows = this.getRows(), lastVisiRow, firstVisiRow, midVisiRow;
		for (var j = 0, rl = rows.length; j < rl; ++j) { //tree might collapse some items
			var r = rows[j];
			if (zk(r).isVisible()) {
				++nVisiRows;
				if (!firstVisiRow)
					firstVisiRow = r;

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

				var rowhgh = firstVisiRow ? zk(firstVisiRow).offsetHeight(): null;
				if (!rowhgh)
					rowhgh = this._headHgh(20);

				nRows = Math.round((hgh - diff)/ rowhgh);
			}
			this._visibleRows(nRows);
		}

		if (nRows) {
			if (!hgh) {
				if (!nVisiRows) {
					hgh = this._headHgh(20, true) * nRows;
				} else if (nRows <= nVisiRows) {
					var $midVisiRow = zk(midVisiRow);
					hgh = $midVisiRow.offsetTop() + $midVisiRow.offsetHeight();
				} else {
					var $lastVisiRow = zk(lastVisiRow);
					hgh = $lastVisiRow.offsetTop() + $lastVisiRow.offsetHeight();
					hgh = Math.ceil((nRows * hgh) / nVisiRows);
				}
			}
			this.ebody.style.height = hgh + 'px';
		} else {
			this.ebody.style.height = '';
			var focusEL = this.$n('a');
			if ((this.paging || this._paginal) && focusEL)
				focusEL.style.top = '0px'; // Bug ZK-1715: focus has no chance to sync if don't select item after changing page.
		}
	},
	/* Returns the real # of rows (aka., real size). */
	_visibleRows: function (v) {
		if ('number' == typeof v) {
			this._visiRows = v;
		} else
			return this.getRows() || this._visiRows || 0;
	},
	/* Height of the head row. If no header, defval is returned. */
	_headHgh: function (defVal, isExcludeAuxhead) {
		var headWidget = this.getHeadWidget(), //Bug ZK-1297: get head height exclude auxhead
			head = this.ehead,
			hgh = isExcludeAuxhead ? (headWidget ? headWidget.$n().offsetHeight : 0) : (head ? head.offsetHeight : 0);
		if (this.paging) {
			var pgit = this.$n('pgit'),
				pgib = this.$n('pgib');
			if (pgit) hgh += pgit.offsetHeight;
			if (pgib) hgh += pgib.offsetHeight;
		}
		return hgh ? hgh: defVal;
	},
	/**
	 * Returns the index of the ItemWidget
	 * @param ItemWidget item
	 * @return int
	 */
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
				this._selectedIndex = this.indexOfItem(item);
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
			this._updHeaderCM();
		} else {
			//Bug ZK-1834: should reset Focus Element after clearing selected item
			this._anchorTop = this._anchorLeft = 0;
			this._syncFocus();
		}
	},
	//super
	focus_: function (timeout) {
		var btn;
		if (btn = this.$n('a')) {
			if (this._focusItem) {
				for (var it = this.getBodyWidgetIterator(), w; (w = it.next());) 
					if (this._isFocus(w)) {
						w.focus_(timeout);
						break;
					}
			} else {
				// Bug ZK-414
				if (this._currentTop)
					btn.style.top = this._currentTop + 'px';
				if (this._currentLeft)
					btn.style.left = this._currentLeft + 'px'; 	
			}
			this.focusA_(btn, timeout);
			return true;
		}
		return false;
	},
	focusA_: function(btn, timeout) { //called by derived class
		zk(btn).focus(timeout);
	},
	bind_: function () {
		this.$supers(SelectWidget, 'bind_', arguments);
		var btn = this.$n('a');
		if (btn)
			this.domListen_(btn, 'onFocus', 'doFocus_')
				.domListen_(btn, 'onKeyDown')
				.domListen_(btn, 'onBlur', 'doBlur_');
		this.updateFormData();
		this._updHeaderCM();
	},
	unbind_: function () {
		unlistenOnFitSize(this);
		var btn = this.$n('a');
		if (btn)
			this.domUnlisten_(btn, 'onFocus', 'doFocus_')
				.domUnlisten_(btn, 'onKeyDown')
				.domUnlisten_(btn, 'onBlur', 'doBlur_');
		this.$supers(SelectWidget, 'unbind_', arguments);
	},
	clearCache: function () {
		this.$supers('clearCache', arguments);
		this.efield = null;
	},
	doFocus_: function (evt) {
		var row	= this._focusItem || this._lastSelectedItem;
		if (row) row._doFocusIn();
		this.$supers('doFocus_', arguments);
	},
	doBlur_: function (evt) {
		if (this._focusItem) {
			this._lastSelectedItem = this._focusItem;
			this._focusItem._doFocusOut();
		}
		this._focusItem = null;
		this.$supers('doBlur_', arguments);
	},
	/** Returns whether to ignore the selection.
	 * It is called when selecting an item ({@link ItemWidget#doSelect_}).
	 * <p>Default: always false (don't ignore) unless {@link #rightSelect} is true and event is onRightClick.
	 * Notice that clicking on button/textbox are already ignored, i.e.,
	 * this method won't be called if the user clicks on, say, a button.
	 * @param zk.Event evt the event
	 * @param ItemWidget row the row about to be selected
	 * @return int 1 (true): ignore,<br/>
	 * 0 (false): select if single select, and toggle selection if multiple,<br/>
	 * and -1: always select (even if multiple)
	 */
	shallIgnoreSelect_: function (evt/*, row*/) { //row has to be the second argument for backward compatible
		//see also _shallIgnore
		return evt.name == 'onRightClick' ? this.rightSelect ? -1: true: false;
	},
	//@param bSel whether it is called by _doItemSelect
	_shallIgnore: function(evt, bSel) { // move this function in the widget for override
		if (!evt.domTarget || !evt.target.canActivate())
			return true;

		if (bSel) {
			try {
				var el = evt.domTarget;
				if (el) //Not use jq.isAncestor since it calls vparentNode
					for (;;) {
						if (el.id == this.uuid) //listbox
							break;
						if (!(el = el.parentNode))
							return true; //vparent
					}
			} catch (e) {
			}

			if (typeof (bSel = this.nonselectableTags) == 'string') {
				if (!bSel)
					return; //not ignore
				if (bSel == '*')
					return true;

				var tn = jq.nodeName(evt.domTarget),
					bInpBtn = tn == 'input' && evt.domTarget.type.toLowerCase() == 'button';
				if (bSel.indexOf(tn) < 0) {
					return bSel.indexOf('button') >= 0
						&& (_isButton(evt) || bInpBtn);
				}
				return !bInpBtn || bSel.indexOf('button') >= 0;
			}
		}

		return _focusable(evt);
	},
	_doItemSelect: function (row, evt) { //called by ItemWidget
		//It is better not to change selection only if dragging selected
		//(like Windows does)
		//However, FF won't fire onclick if dragging, so the spec is
		//not to change selection if dragging (selected or not)
		var alwaysSelect,
			cmClicked = this._checkmark && evt.domTarget == row.$n('cm');
		if (zk.dragging || (!cmClicked && (this._shallIgnore(evt, true)
		|| ((alwaysSelect = this.shallIgnoreSelect_(evt, row))
			&& !(alwaysSelect = alwaysSelect < 0)))))
			return;

		var skipFocus = _focusable(evt); //skip focus if evt is on a focusable element
		if (this._checkmark
		&& !evt.data.shiftKey && !(evt.data.ctrlKey || evt.data.metaKey) 
		&& (!this._cdo || cmClicked)) {
			// Bug 2997034
			this._syncFocus(row);

			if (this._multiple) {
				var seled = row.isSelected();
				if (!seled || !alwaysSelect)
					this._toggleSelect(row, !seled, evt, skipFocus);
			} else
				this._select(row, evt, skipFocus);
		} else {
		//Bug 1650540: double click as select again
		//Note: we don't handle if clicking on checkmark, since FF always
		//toggle and it causes incosistency
			if ((zk.gecko || zk.webkit) && row.isListen('onDoubleClick')) {
				var now = jq.now(), last = row._last;
				row._last = now;
				if (last && now - last < 900)
					return; //ignore double-click
			}
			this._syncFocus(row);
			if (this._multiple) {
				if (evt.data.shiftKey)
					this._selectUpto(row, evt, skipFocus);
				else if (evt.data.ctrlKey || evt.data.metaKey || zk.mobile) //let multiple selection without checkmark work on tablet
					this._toggleSelect(row, !row.isSelected(), evt, skipFocus);
				else if (!alwaysSelect || !row.isSelected())// Bug: 1973470
					this._select(row, evt, skipFocus);
			} else
				this._select(row, evt, skipFocus);

			//since row might was selected, we always enforce focus here
			if (!skipFocus)
				row.focus();
			//if (evt) evt.stop();
			//No much reason to eat the event.
			//In opposite, it disabled popup (bug 1578659)
		}
	},
	/* Handles keydown sent to the body. */
	doKeyDown_: function (evt) {
		if (!this._shallIgnore(evt)) {

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
				if (!jq.nodeName(evt.domTarget, 'a'))
					this.focus();
				if (evt.domTarget == this.$n('a')) {// for test tool.
					if (evt.target == this) //try to avoid the condition inside the _doKeyDown()
						evt.target = this._focusItem || this.getSelectedItem() || this;
					this._doKeyDown(evt);
				}
				evt.stop();
				return false;
			}
		}

		if (!zk.gecko || !jq.nodeName(evt.domTarget, 'input', 'textarea'))
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

		var row = this._focusItem || this.getSelectedItem(),
			data = evt.data,
			shift = data.shiftKey, ctrl = (data.ctrlKey || data.metaKey);
		if (shift && !this._multiple)
			shift = false; //OK to

		var endless = false, step, lastrow;

		// for test tool when browser is webkit
		if (zk.webkit && typeof data.keyCode == 'string')
			data.keyCode = zk.parseInt(data.keyCode);
		switch (data.keyCode) {
		case 33: //PgUp
		case 34: //PgDn
			var pgnl = this.paging || this._paginal;
			if (row && pgnl) { // F60-ZK-715
				var npg = this.getActivePage() + (data.keyCode == 33 ? -1 : 1);
				// TODO: concern ctrl
				if (npg > -1 && npg < this.getPageCount())
					this.fire('onAcrossPage', {
						page: npg, 
						offset: this.indexOfItem(row),
						shift: !shift || !this._multiple ? 0 : 
							data.keyCode == 33 ? this.getPageSize() : -this.getPageSize()
					});
				return;
			}
			step = this._visibleRows();
			if (step == 0)
				step = this.getPageSize() || 20;
			if (data.keyCode == 33)
				step = -step;
			break;
		case 38: //UP
		case 40: //DOWN
			step = data.keyCode == 40 ? 1: -1;
			break;
		case 32: //SPACE
			if (row) {
				if (this._multiple)
					this._toggleSelect(row, !row.isSelected(), evt);
				else
					this._select(row, evt);
			}
			break;
		case 36: //Home
		case 35: //End
			step = data.keyCode == 35 ? 1: -1;
			endless = true;
			break;
		case 37: //LEFT
			if (row)
				this._doLeft(row);
			break;
		case 39: //RIGHT
			if (row)
				this._doRight(row);
			break;
		}
		
		if (step > 0 || (step < 0 && row)) {
			if (row && shift && !row.isDisabled()) // Bug ZK-1715: not select item if disabled.
				this._toggleSelect(row, true, evt);
			var nrow = row ? row.$n() : null;
			for (;;) {
				if (!nrow) { // no focused/selected item yet
					var w = this.getBodyWidgetIterator().next();
					if (w)
						nrow = w.$n(); // F60-ZK-423: first row
					else
						return; // empty
				} else
					nrow = step > 0 ? nrow.nextSibling : nrow.previousSibling;
				
				if (!nrow) { // F60-ZK-715: across to next/previous page if any
					if (endless)
						break; // ignore Home/End key
					var pg = this.paging || this._paginal, pnum;
					if (pg) {
						pnum = pg.getActivePage();
						// TODO: concern ctrl
						if (step > 0 ? (pnum + 1 < pg.getPageCount()) : pnum > 0)
							this.fire('onAcrossPage', {
								page: pnum + (step > 0 ? 1 : 0), 
								offset: step > 0 ? 0 : -1,
								shift: !this._multiple || !shift ? 0 : step > 0 ? -1 : 1
							});
					}
					break;
				}
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
			if (ctrl)
				this._focus(lastrow);
			else
				this._select(lastrow, evt);
			this._syncFocus(lastrow);
			var bar = this._scrollbar;
			if (bar)
				bar.scrollToElement(lastrow.$n());
		}

		return _afterChildKey(evt);
	},
	_doKeyUp: function (evt) { //called by ItemWidget only
		return _beforeChildKey(this, evt) || _afterChildKey(evt);
	},
	_doLeft: zk.$void,
	_doRight: zk.$void,
	/* maintain the offset of the focus proxy*/
	_syncFocus: function (row) {
		var focusEl = this.$n('a');
		if (!focusEl) //Bug ZK-1480: widget may not rendered when ROD enabled
			return;
		
		var focusElStyle = focusEl.style,
			oldTop = this._anchorTop,
			oldLeft = this._anchorLeft,
			offs, n;
		if (row && (n = row.$n())) {
			offs = zk(n).revisedOffset();
			offs = this._toStyleOffset(focusEl, offs[0] + this.ebody.scrollLeft, offs[1]);
		} else	// ZK-798, use old value if exists
			offs = [oldLeft? oldLeft : 0, oldTop? oldTop : 0];

		this.fixAnchor_(offs, focusEl);
		if( this._anchorTop != offs[1] || this._anchorLeft != offs[0]){
			//ZK-798, to prevent firing onAnchorPos too many times when moust over a rod listbox,
			//if _anchorTop/_anchorLeft is the same , just ignore the event.
			this._anchorTop = offs[1];
			this._anchorLeft = offs[0];
			this.fire('onAnchorPos',{top:this._anchorTop,left:this._anchorLeft});			
		}
		
		focusElStyle.top = this._anchorTop + 'px';
		focusElStyle.left = this._anchorLeft + 'px';
	},
	_toStyleOffset: function (el, x, y) {
		var ofs1 = zk(el).revisedOffset(),
			x2 = zk.parseInt(el.style.left), y2 = zk.parseInt(el.style.top);;
		return [x - ofs1[0] + x2, y  - ofs1[1] + y2];
	},
	/**
	 * May need fix anchor.
	 * @param int[] offs The anchor offset [left, top]
	 * @since 6.0.0
	 */
	fixAnchor_: function (offs, focusEl) {
		var body = this.ebody,
			sw = body.scrollWidth,
			sh = body.scrollHeight;
		if (offs[0] >= sw) offs[0] = sw - jq(focusEl).width();
		if (offs[1] >= sh) offs[1] = sh - jq(focusEl).height();
	},
	/* Selects an item, notify server and change focus if necessary. */
	_select: function (row, evt, skipFocus) {
		if (this._selectOne(row, skipFocus)) {
			//notify server
			this.fireOnSelect(row, evt);
		}
	},
	/* Selects a range from the last focus up to the specified one.
	 * Callable only if multiple
	 */
	_selectUpto: function (row, evt, skipFocus) {
		if (row.isSelected()) {
			if (!skipFocus)
				this._focus(row);
			return; //nothing changed
		}

		var focusfound = false, rowfound = false,
			// ZK-1096: this._lastSelectedItem is only updated when doBlur
			lastSelected = this._focusItem || this._lastSelectedItem; 
		for (var it = this.getBodyWidgetIterator(), si = this.getSelectedItem(), w; (w = it.next());) {
			if (w.isDisabled()) continue; // Bug: 2030986
			if (focusfound) {
				this._changeSelect(w, true);
				if (w == row)
					break;
			} else if (rowfound) {
				this._changeSelect(w, true);
				if (this._isFocus(w) || w == lastSelected)
					break;
			} else if (!si) { // Bug: 3337441
				if (w != row)
					continue;
				this._changeSelect(w, true);
				break;
			} else {
				rowfound = w == row;
				focusfound = this._isFocus(w) || w == lastSelected;
				if (rowfound || focusfound) {
					this._changeSelect(w, true);
					if (rowfound && focusfound)
						break;
				}
			}
		}

		if (!skipFocus)
			this._focus(row);
		this.fireOnSelect(row, evt);
	},
	/**
	 * Selects all items.
	 * @param boolean notify if true, fire onSelect event to server
	 * @param jq.Event evt
	 * @disable(zkgwt)
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
	 * @disable(zkgwt)
	 */
	selectAll: _zkf,
	/* Selects one and deselect others, and return whehter any changes.
	 * It won't notify the server.
	 */
	_selectOne: function (row, skipFocus) {
		var selItem = this.getSelectedItem();
		if (this._multiple) {
			if (row && !skipFocus) this._unsetFocusExcept(row);
			var changed = this._unsetSelectAllExcept(row);
			if (!changed && row && selItem == row) {
				if (!skipFocus) this._setFocus(row, true);
				return false; //not changed
			}
		} else {
			if (selItem) {
				if (selItem == row) {
					if (!skipFocus) this._setFocus(row, true);
					return false; //not changed
				}
				this._changeSelect(selItem, false);
				if (row)
					if(!skipFocus) this._setFocus(selItem, false);
			}
			if (row && !skipFocus) this._unsetFocusExcept(row);
		}
		//we always invoke _changeSelect to change focus
		if (row) {
			this._changeSelect(row, true);
			if (!skipFocus) this._setFocus(row, true);
		}
		return true;
	},
	/* Toggle the selection and notifies server. */
	_toggleSelect: function (row, toSel, evt, skipFocus) {
		if (!this._multiple) {
			var old = this.getSelectedItem();
			if (row != old && toSel)
				this._changeSelect(row, false);
		}

		this._changeSelect(row, toSel);
		if (!skipFocus)
			this._focus(row);

		//notify server
		this.fireOnSelect(row, evt);
	},
	/** Fires the onSelect event.
	 * If the widget is created at the server, the event will be sent
	 * to the server too.
	 * @param zk.Widget ref the reference which causes this onSelect event.
	 * Ignored if null.
	 * @since 5.0.5
	 */
	fireOnSelect: function (ref, evt) {
		var data = [];

		for (var it = this.getSelectedItems(), j = it.length; j--;)
			if (it[j].isSelected())
				data.push(it[j]);

		var edata, keep = true;
		if (evt) {
			edata = evt.data;
			if (this._multiple) // B50-ZK-421
				keep = (edata.ctrlKey || edata.metaKey) || edata.shiftKey || 
					(this._checkmark && (!this._cdo || (evt.domTarget.id && evt.domTarget.id.endsWith('-cm'))));
		}

		this.fire('onSelect', zk.copy({items: data, reference: ref, clearFirst: !keep}, edata));
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
		var changed = !!row.isSelected() != toSel;
		if (changed) {
			row.setSelected(toSel);
			//row._toggleEffect(true);
		}
		return changed;
	},
	_isFocus: function (row) {
		return this._focusItem == row;
	},
	/* Changes the focus status, and return whether it is changed. */
	_setFocus: function (row, bFocus) {
		var changed = this._isFocus(row) != bFocus;
		if (changed) {
			if (bFocus) {
				if (!row.focus())
					this.focus();

				if (!this.paging && zk.gecko)
					this.fireOnRender(5);
					//Firefox doesn't call onscroll when we moving by cursor, so...
			}
		}
		if (!bFocus)
			row._doFocusOut();
		return changed;
	},
	/* Cleans selected except the specified one, and returns any selected status
	 * is changed.
	 */
	_unsetSelectAllExcept: function (row) {
		var changed = false;
		for (var it = this.getSelectedItems(), j = it.length; j--;) {
			if (it[j] != row && this._changeSelect(it[j], false))
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
	},
	_updHeaderCM: function () { //update header's checkmark
		if (this._headercm && this._multiple) {
			var box = this, v;
			this._nUpdHeaderCM = (v = this._nUpdHeaderCM) > 0 ? v + 1: 1;
			setTimeout(function () {_updHeaderCM(box);}, 100); //do it in batch
		}
	},
	_isAllSelected: function () {
		for (var it = this.getBodyWidgetIterator({skipHidden:true}), w; (w = it.next());)
			if (!w.isDisabled() && !w.isSelected())
				return false;
		return true;
	},
	_ignoreHghExt: function () {
		return this._rows > 0;
	},
	//@Override
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (this.desktop) {
			if (child.$instanceof(zul.sel.ItemWidget) && child.isSelected())
				this._syncFocus(child);
			//Bug ZK-1473: when using template to render listbox,
			//   this._focusItem still remain the removed one, 
			//   set it with the newly rendered one to prevent keyboard navigation jump back to top
			var n, offs;
			if (this._focusItem != child && (n = child.$n())) {
				offs = zk(n).revisedOffset();
				offs = this._toStyleOffset(this.$n('a'), offs[0] + this.ebody.scrollLeft, offs[1]);
				if (offs[0] == this._anchorLeft && offs[1] == this._anchorTop)
					this._focusItem = child;
			}
		}
	},
	//@Override
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		var selItems = this._selItems, len;
		if (this.desktop && child.$instanceof(zul.sel.ItemWidget) && (len = selItems.length))
			this._syncFocus(selItems[len - 1]);
	},
	//@Override
	replaceWidget: function (newwgt) {
		this.$supers('replaceWidget', arguments);

		newwgt._lastSelectedItem = _fixReplace(this._lastSelectedItem);
		newwgt._focusItem = _fixReplace(this._focusItem);
	}
});

})();
