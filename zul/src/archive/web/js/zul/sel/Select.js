/* Select.js

	Purpose:
		
	Description:
		
	History:
		Mon Jun  1 16:43:51     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Select = zk.$extends(zul.Widget, {
	_selectedIndex: -1,
	_tabindex: -1,
	_rows: 0,
	$init: function () {
		this.$supers('$init', arguments);
		this._selItems = [];
	},
	$define: {
		multiple: function (multiple) {
			var n = this.getNode();
			if (n) n.multiple = multiple ? 'multiple' : '';
		},
		disabled: function (disabled) {
			var n = this.getNode();
			if (n) n.disabled = disabled ? 'disabled' : '';
		},
		selectedIndex: function (selectedIndex) {
			var n = this.getNode();
			if (n) n.selectedIndex = selectedIndex;
		},
		tabindex: function (tabindex) {
			var n = this.getNode();
			if (n) n.tabindex = tabindex >= 0 ? tabindex: '';
		},
		name: function (name) {
			var n = this.getNode();
			if (n) n.name = name;
		},
		rows: function (rows) {
			var n = this.getNode();
			if (n) n.size = rows;
		},
		maxlength: function (maxlength) {
			if (this.desktop)
				this.rerender();
		}
	},
	toggleItemSelection: function (item) {
		if (item.isSelected()) this._removeItemFromSelection(item);
		else this._addItemToSelection(item);
	},
	selectItem: function (item) {
		if (!item)
			this.setSelectedIndex(-1);
		else if (this._multiple || !item.isSelected())
			this.setSelectedIndex(item.getChildIndex());
	},
	_addItemToSelection: function (item) {
		if (!item.isSelected()) {
			if (!this._multiple) {
				this.selectItem(item);
			} else {
				var index = item.getChildIndex();
				if (index < this._selectedIndex || this._selectedIndex < 0) {
					this._selectedIndex = index;
				}
				item._selected = true;
				this._selItems.push(item);
			}
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
			var item;
			for(;(item = this._selItems.pop());)
				item._selected = false;
			this._selectedIndex = -1;
		}
	},
	domAttrs_: function () {
		return this.$supers('domAttrs_', arguments)
			+ (this.isDisabled() ? ' disabled="disabled"' :'')
			+ (this.isMultiple() ? ' multiple="multiple"' : '')
			+ (this.getSelectedIndex() > -1 ? ' selectedIndex=' + this.getSelectedIndex() : '')
			+ (this.getTabindex() > -1 ? ' tabindex=' + this.getTabindex(): '')
			+ (this.getRows() > 0 ? ' size=' + this.getRows(): '')
			+ (this.getName() ? ' name="' + this.getName() + '"': '');
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var n = this.getNode();
		this.domListen_(n, 'onChange');
		this.domListen_(n, 'onFocus', 'doFocus_');
		this.domListen_(n, 'onBlur', 'doBlur_');
	},
	unbind_: function () {
		var n = this.getNode();
		this.domUnlisten_(n, 'onChange');
		this.domUnlisten_(n, 'onFocus', 'doFocus_');
		this.domUnlisten_(n, 'onBlur', 'doBlur_');
		this.$supers('unbind_', arguments);
	},
	_doChange: function (evt) {		
		var data = [], reference, n = this.getNode();
		if (this.isMultiple()) {
			var opts = n.options;
			for (var j = 0, ol = opts.length; j < ol; ++j) {
				var opt = opts[j],
					o = zk.Widget.$(opt.id);
				if (o) o.setSelected(opt.selected);
				if (opt.selected) {
					data.push(opt.id);
					if (!reference) reference = opt.id;
				}
			}
		} else {
			var opt = n.options[n.selectedIndex];
			this.setSelectedIndex(n.selectedIndex);
			data.push(opt.id);
			reference = opt.id;
		}
		
		this.fire('onSelect', {items: data, reference: reference});
	
		// To Be Fixed: Bug 1756559: see au.js
		/**if (zkau.lateReq) {
			zkau.send(zkau.lateReq, 25);
			delete zkau.lateReq;
		}*/
	},
	doKeyUp_: function (evt) {
		if (zk.gecko || zk.safari) {
			if (this.isMultiple() || this.getSelectedIndex() === evt.domTarget.selectedIndex) 
				return; //not change or unnecessary.
			this._doChange(evt);
		} else this.$supers('doKeyUp_', arguments);
	}
});
