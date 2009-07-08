/* Radiogroup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 09:25:17     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Radiogroup = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_jsel: -1,

	$define: { //zk.def
		orient: function () {
			this.rerender();
		},
		name: function (v) {
			for (var items = this.getItems(), i = items.length; i--;)
				items[i].setName(name);
		}
	},

	getItemAtIndex: function (index) {
		if (index < 0)
			return null;
		return this._getAt(this, {value: 0}, index);
	},
	getItemCount: function () {
		return this.getItems().length;
	},
	getItems: function () {
		return this._concatItem(this);
	},
	getSelectedIndex: function () {
		return this._jsel;
	},
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
	getSelectedItem: function () {
		return this._jsel >= 0 ? this.getItemAtIndex(this._jsel): null;
	},
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
