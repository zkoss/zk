/* Select.js

	Purpose:

	Description:

	History:
		Mon Jun  1 16:43:51     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A HTML select tag.
 */
zul.sel.Select = zk.$extends(zul.Widget, {
	_selectedIndex: -1,
	_rows: 0,
	$init: function () {
		this.$supers('$init', arguments);
		this._selItems = [];
		this._groupsInfo = [];
	},
	$define: {
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
			var n = this.$n();
			if (n) n.multiple = multiple ? 'multiple' : '';
		},
		/**
		 * Returns whether it is disabled.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		/**
		 * Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: function (disabled) {
			var n = this.$n();
			if (n) n.disabled = disabled ? 'disabled' : '';
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
		selectedIndex: function (selectedIndex) {
			var i = 0, j = 0, w, n = this.$n();
			this.clearSelection();
			// B50-ZK-989: original skipFixIndex way gives wrong value for this._selectedIndex
			// select from server API call, fix the index
			for (w = this.firstChild; w && i < selectedIndex; w = w.nextSibling, i++) {
				if (w.$instanceof(zul.sel.Option)) {
					if (!w.isVisible())
						j++;
				} else if (w.$instanceof(zul.sel.Optgroup))
					j++;
				else i--;
			}

			selectedIndex -= j;
			if (n)
				n.selectedIndex = selectedIndex;

			if (selectedIndex > -1 && w && w.$instanceof(zul.sel.Option)) {
				w.setSelected(true);
				this._selItems.push(w);
			}
		},
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
		name: function (name) {
			var n = this.$n();
			if (n) n.name = name;
		},
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
			if (n) n.size = rows;
		},
		/**
		 * Returns the maximal length of each item's label.
		 * @return int
		 */
		/**
		 * Sets the maximal length of each item's label.
		 * @param int maxlength
		 */
		maxlength: function (maxlength, fromServer) {
			if (this.desktop)
				this.requestRerender_(fromServer);
		}
	},

	// ZK-2133: should sync all items
	setChgSel: function (val) { //called from the server
		var sels = {};
		for (var j = 0; ;) {
			var k = val.indexOf(',', j),
			s = (k >= 0 ? val.substring(j, k) : val.substring(j)).trim();
			if (s) sels[s] = true;
			if (k < 0) break;
			j = k + 1;
		}
		for (var w = this.firstChild; w; w = w.nextSibling)
			this._changeSelect(w, sels[w.uuid] == true);
	},

	/* Changes the selected status of an item without affecting other items
	 * and return true if the status is really changed.
	 */
	_changeSelect: function (option, toSel) {
		var changed = !!option.isSelected() != toSel;
		if (changed) {
			option.setSelected(toSel);
		}
		return changed;
	},
	/**
	 * If the specified item is selected, it is deselected. If it is not
	 * selected, it is selected. Other items in the list box that are selected
	 * are not affected, and retain their selected state.
	 * @param Option item
	 */
	toggleItemSelection: function (item) {
		if (item.isSelected()) this._removeItemFromSelection(item);
		else this._addItemToSelection(item);
	},
	/**
	 * Deselects all of the currently selected items and selects the given item.
	 *
	 * @param Option item
	 *            the item to select. If null, all items are deselected.
	 */
	selectItem: function (item) {
		if (!item)
			this.setSelectedIndex(-1);
		else if (this._multiple || !item.isSelected()) {
			if (item.getOptionIndex_)
				this.setSelectedIndex(item.getOptionIndex_());
			else
				this.setSelectedIndex(item.getChildIndex());
		}
	},
	_addItemToSelection: function (item) {
		if (!item.isSelected()) {
			var multiple = this._multiple;
			if (!multiple)
				this.clearSelection();
			var index = item.getOptionIndex_ ? item.getOptionIndex_() : item.getChildIndex();
			if (!multiple || (index < this._selectedIndex || this._selectedIndex < 0))
				this._selectedIndex = index;
			item._setSelectedDirectly(true);
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
			var item;
			for (;(item = this._selItems.pop());)
				item._setSelectedDirectly(false);
			this._selectedIndex = -1;
		}
	},
	domAttrs_: function () {
		var v;
		return this.$supers('domAttrs_', arguments)
			+ (this.isDisabled() ? ' disabled="disabled"' : '')
			+ (this.isMultiple() ? ' multiple="multiple"' : '')
			+ ((v = this.getSelectedIndex()) > -1 ? ' selectedIndex="' + v + '"' : '')
			+ ((v = this.getRows()) > 0 ? ' size="' + v + '"' : '')
			+ ((v = this.getName()) ? ' name="' + v + '"' : '');
	},
	bind_: function () {
		this.$supers(zul.sel.Select, 'bind_', arguments);

		var n = this.$n();
		this.domListen_(n, 'onChange')
			.domListen_(n, 'onFocus', 'doFocus_')
			.domListen_(n, 'onBlur', 'doBlur_');

		if (!zk.gecko) {
			var fn = [this, this._fixSelIndex];
			zWatch.listen({onRestore: fn, onVParent: fn});
		}
		zWatch.listen({onCommandReady: this});

		this._fixSelIndex();
	},
	unbind_: function () {
		zWatch.unlisten({onCommandReady: this});
		var n = this.$n();
		this.domUnlisten_(n, 'onChange')
			.domUnlisten_(n, 'onFocus', 'doFocus_')
			.domUnlisten_(n, 'onBlur', 'doBlur_')
			.$supers(zul.sel.Select, 'unbind_', arguments);

		var fn = [this, this._fixSelIndex];
		zWatch.unlisten({onRestore: fn, onVParent: fn});
	},
	_fixSelIndex: function () {
		if (this._selectedIndex < 0)
			this.$n().selectedIndex = -1;
	},
	_doChange: function (evt) {
		var n = this.$n(),
			opts = n.options,
			multiple = this._multiple,
			data = [], changed = false, reference;
		for (var j = 0, ol = opts.length; j < ol; ++j) {
			var opt = opts[j],
				o = zk.Widget.$(opt.id),
				v = opt.selected;
			if (multiple) {
				if (o && o._selected != v) {
					this.toggleItemSelection(o);
					changed = true;
				}
				if (v) {
					data.push(o);
					if (!reference) reference = o;
				}
			} else {
				if (o && o._selected != v && v) { // found the newly selected one
					this._addItemToSelection(o); //will clear other selection first
					changed = true;
					data.push(o);
					reference = o;
					break;
				}
			}
		}
		if (!changed)
			return;

		this.fire('onSelect', {items: data, reference: reference});
	},
	doBlur_: zk.$void,
	//Bug 1756559: ctrl key shall fore it to be sent first
	beforeCtrlKeys_: function (evt) {
		this._doChange(evt);
	},
	onChildAdded_: function (child) {
		if (child.$instanceof(zul.sel.Optgroup))
			this._groupsInfo.push(child);
		if (this.desktop)
			this.requestRerender_(true);
	},
	onChildRemoved_: function (child) {
		if (child.$instanceof(zul.sel.Optgroup))
			this._groupsInfo.$remove(child);
		if (this.desktop && !this.childReplacing_)
			this.requestRerender_(true);
	},
	requestRerender_: function (fromServer) {
		if (fromServer)
			this._shouldRerenderFlag = true;
		else
			this.rerender();
	},
	onCommandReady: function () {
		if (this._shouldRerenderFlag) {
			this._shouldRerenderFlag = false;
			this.rerender();
		}
	},
	/** Returns whether any {@link Optgroup} exists.
	 * @return boolean
	 * @since 8.6.0
	 */
	hasGroup: function () {
		return this._groupsInfo.length;
	},
	/** Returns the number of {@link Optgroup}.
	 * @return int
	 * @since 8.6.0
	 */
	getGroupCount: function () {
		return this._groupsInfo.length;
	},
	/** Returns a list of all {@link Optgroup}. The order is unmaintained.
	 * @return Array
	 * @since 8.6.0
	 */
	getGroups: function () {
		return this._groupsInfo.$clone();
	},
	setItemsInvalid_: function (wgts) {
		var wgt = this;
		zAu.createWidgets(wgts, function (ws) {
			wgt.replaceCavedChildren_('', ws);
		}, function (wx) {
			return wx;
		});
	}
});
