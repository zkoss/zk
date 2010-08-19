/* Radiogroup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 09:25:17     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {

	function _concatItem(group) {
		var sum = _concatItem0(group);
		sum.push.apply(sum, group._externs);
		return sum;
	}
	function _concatItem0(cmp) {
		var sum = [];
		for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {			
			if (wgt.$instanceof(zul.wgt.Radio)) 
				sum.push(wgt);
			else if (!wgt.$instanceof(zul.wgt.Radiogroup)) //skip nested radiogroup
				sum = sum.concat(_concatItem0(wgt));
		}
		return sum;
	}

	function _getAt(group, cur, index) {
		var r = _getAt0(group, cur, index);
		if (!r)
			for (var extns = group._externs, j = 0, l = extns.length; j < l; ++j)
				if (!_redudant(group, extns[j]) && cur.value++ == index)
					return extns[j];
		return r;
	}
	function _getAt0(cmp, cur, index) {
		for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
			if (wgt.$instanceof(zul.wgt.Radio)) {
				if (cur.value++ == index) return wgt;
			} else if (!wgt.$instanceof(zul.wgt.Radiogroup)) {
				var r = _getAt0(wgt, cur, index);
				if (r != null) return r;
			}				
		}
	}

	function _fixSelIndex(group, cur) {
		var jsel = _fixSelIndex0(group, cur);
		if (jsel < 0)
			for (var extns = group._externs, j = 0, l = extns.length, radio; j < l; ++j)
				if (!_redudant(group, radio = extns[j])) {
					if (radio.isSelected())
						return cur.value;
					++cur.value;
				}
		return jsel;
	}
	function _fixSelIndex0(cmp, cur) {
		for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
			if (wgt.$instanceof(zul.wgt.Radio)) {
				if (wgt.isSelected())
					return cur.value;
				++cur.value;
			} else if (!wgt.$instanceof(zul.wgt.Radiogroup)) {
				var jsel = _fixSelIndex0(wgt, cur);
				if (jsel >= 0) return jsel;
			}
		}
		return -1;
	}

	function _redudant(group, radio) {
		for (var p = radio; (p = p.parent) != null;)
			if (p.$instanceof(zul.wgt.Radiogroup))
				return p == group;
	}

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

	$init: function () {
		this.$supers("$init", arguments);
		this._externs = [];
	},
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
				items[i].setName(v);
		}
	},
	/** Returns the radio button at the specified index.
	 * @param int index
	 * @return Radio
	 */
	getItemAtIndex: function (index) {
		return index >= 0 ? _getAt(this, {value: 0}, index): null;
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
		return _concatItem(this);
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
		if (item && !this._rmExtern(item)) {
			var p = item.parent;
			if (p) p.removeChild(item);
		}
		return item;
	},

	/** private method */
	_fixSelectedIndex: function () {
		this._jsel = _fixSelIndex(this, {value: 0});
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

	_addExtern: function (radio) {
		this._externs.push(radio);
		if (!_redudant(this, radio))
			this._fixOnAdd(radio);
	},
	_rmExtern: function (radio) {
		if (this._externs.$remove(radio)) {
			if (!_redudant(this, radio))
				this._fixOnRemove(radio);
			return true;
		}
	}
});
})();