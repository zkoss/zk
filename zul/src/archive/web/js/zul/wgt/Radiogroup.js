/* Radiogroup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 09:25:17     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Radiogroup = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_jsel: -1,
	
	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	getOrient: function () {
		return this._orient;
	},
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	setOrient: function (orient) {
		if (this._orient != orient) {
			this._orient = orient;
			var n = this.node;
			if (n) this.rerender();
		}
	},

	/** Returns the radio button at the specified index.
	 */
	getItemAtIndex: function (index) {
		if (index < 0)
			return null;
		return this._getAt(this, {value: 0}, index);
	},
	_getAt: function (cmp, cur, index) {
		for (var cnt = 0, wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
			if (wgt.$instanceof(zul.wgt.Radio)) {
				if (cnt.value++ == index) return wgt;
			} else if (!wgt.$instanceof(zul.wgt.Radiogroup)) {
				var r = this._getAt(wgt, cur, index);
				if (r != null) return r;
			}				
		}
		return null;
	},

	/** Returns the number of radio buttons in this group.
	 */
	getItemCount: function () {
		return this.getItems().length;
	},
	/**
	 * Returns the radio items belonging to this group
	 */
	getItems: function () {
		return this._concatItem(this);
	},
	_concatItem: function (cmp) {
		var sum = [];
		for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {			
			if (wgt.$instanceof(zul.wgt.Radio)) 
				sum.push(wgt);
			else 
				if (!wgt.$instanceof(zul.wgt.Radiogroup)) { //skip nested radiogroup
					sum = sum.concat(this._concatItem(wgt));
				}
		}
		return sum;
	},
	/** Returns the index of the selected radio button (-1 if no one is selected).
	 */
	getSelectedIndex: function () {
		return this._jsel;
	},
	/** Deselects all of the currently selected radio button and selects
	 * the radio button with the given index.
	 */
	setSelectedIndex: function (jsel) {
		if (jsel < 0) jsel = -1;
		if (this._jsel != jsel) {
			if (jsel < 0) {
				getSelectedItem().setSelected(false);
			} else {
				getItemAtIndex(jsel).setSelected(true);
			}
		}
	},
	/** Returns the selected radio button.
	 */
	getSelectedItem: function () {
		return this._jsel >= 0 ? this.getItemAtIndex(this._jsel): null;
	},
	/**  Deselects all of the currently selected radio buttons and selects
	 * the given radio button.
	 */
	setSelectedItem: function (item) {
		if (item == null)
			this.setSelectedIndex(-1);
		else if (item.$instanceof(zul.wgt.Radio))
			item.setSelected(true);
	},
	/** Appends a radio button.
	 */
	appendItem: function (label, value) {
		var item = new zul.wgt.Radio();
		item.setLabel(label);
		item.setValue(value);
		this.appendChild(item);
		return item;
	},
	/**  Removes the child radio button in the list box at the given index.
	 * @return the removed radio button.
	 */
	removeItemAt: function (index) {
		var item = this.getItemAtIndex(index);
		this.removeChild(item);
		return item;
	},
	/** Returns the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * <p>Default: automatically generated an unique name
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	getName: function () {
		return this._name;
	},
	/** Sets the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	setName: function (name) {
		if (!name) name = null;
		if (this._name != name) {
			this._name = name;
			for (var items = this.getItems(), i = items.length; --i >= 0;)
				items[i].setName(name);
		}
	},

	//utilities for radio//
	/** Called when a radio is added to this group.
	 */
	fixOnAdd_: function (child) {
		if (this._jsel >= 0 && child.isSelected()) {
			child.setSelected(false); //it will call fixSelectedIndex_()
		} else {
			this.fixSelectedIndex_();
		}
	},
	/** Called when a radio is removed from this group.
	 */
	fixOnRemove_: function (child) {
		if (child.isSelected()) {
			this._jsel = -1;
		} else if (this._jsel > 0) { //excluding 0
			this.fixSelectedIndex_();
		}
	},
	/** Fix the selected index, _jsel, assuming there are no selected one
	 * before (and excludes) j-the radio button.
	 */
	fixSelectedIndex_: function () {
		this._jsel = this._fixSelIndex(this, {value: 0});
	},
	_fixSelIndex: function (cmp, cur) {
		for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
			if (wgt.$instanceof(zul.wgt.Radio)) {
				if (wgt.isSelected())
					return cur.value;
				++cur.value;
			} else if (!wgt.$instanceof(zul.wgt.Radiogroup)) {
				var jsel = this._fixSelIndex(wgt, cur);
				if (jsel >= 0) return jsel;
			}
		}
		return -1;
	}
});
