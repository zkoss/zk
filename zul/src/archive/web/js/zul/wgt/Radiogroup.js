/* Radiogroup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 09:25:17     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A radio group.
 *
 * <p>Note: To support the versatile layout, a radio group accepts any kind of
 * children, including {@link Radio}. On the other hand, the parent of
 * a radio, if any, must be a radio group.
 *
 */
zul.wgt.Radiogroup = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_jsel: -1,

	$define: { //zk.def
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * @param String orient either "horizontal" or "vertical".
		 */
		orient: function () {
			this.rerender();
		},
		/** Returns the name of this group of radio buttons.
		 * All child radio buttons shared the same name ({@link Radio#getName}).
		 * <p>Default: automatically generated an unique name
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 * @return String
		 */
		/** Sets the name of this group of radio buttons.
		 * All child radio buttons shared the same name ({@link Radio#getName}).
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 * @param String name
		 */
		name: function (v) {
			for (var items = this.getItems(), i = items.length; i--;)
				items[i].setName(name);
		}
	},
	/** Returns the radio button at the specified index.
	 * @param int index
	 * @return Radio
	 */
	getItemAtIndex: function (index) {
		if (index < 0)
			return null;
		return this._getAt(this, {value: 0}, index);
	},
	/** Returns the number of radio buttons in this group.
	 * @return int
	 */
	getItemCount: function () {
		return this.getItems().length;
	},
	/** Returns the all of radio buttons in this group.
	 * @return Array
	 */
	getItems: function () {
		return this._concatItem(this);
	},
	/** Returns the index of the selected radio button (-1 if no one is selected).
	 * @return int
	 */
	getSelectedIndex: function () {
		return this._jsel;
	},
	/** Deselects all of the currently selected radio button and selects
	 * the radio button with the given index.
	 * @param int selectedIndex
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
	 * @return Radio
	 */
	getSelectedItem: function () {
		return this._jsel >= 0 ? this.getItemAtIndex(this._jsel): null;
	},
	/**  Deselects all of the currently selected radio buttons and selects
	 * the given radio button.
	 * @param Radio selectedItem
	 */
	setSelectedItem: function (item) {
		if (item == null)
			this.setSelectedIndex(-1);
		else if (item.$instanceof(zul.wgt.Radio))
			item.setSelected(true);
	},
	appendItem: function (label, value) {
		var item = new zul.wgt.Radio();
		item.setLabel(label);
		item.setValue(value);
		this.appendChild(item);
		return item;
	},
	/** 
	 * Removes the child radio button in the list box at the given index.
	 * @param int index
	 * @return Radio the removed radio button.
	 */
	removeItemAt: function (index) {
		var item = this.getItemAtIndex(index);
		this.removeChild(item);
		return item;
	},

	/** private method */
	_fixSelectedIndex: function () {
		this._jsel = this._fixSelIndex(this, {value: 0});
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
	_fixOnAdd: function (child) {
		if (this._jsel >= 0 && child.isSelected()) {
			child.setSelected(false); //it will call _fixSelectedIndex()
		} else {
			this._fixSelectedIndex();
		}
	},
	_fixOnRemove: function (child) {
		if (child.isSelected()) {
			this._jsel = -1;
		} else if (this._jsel > 0) { //excluding 0
			this._fixSelectedIndex();
		}
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
