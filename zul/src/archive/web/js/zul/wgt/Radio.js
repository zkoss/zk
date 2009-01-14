/* Radio.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 16 11:17:26     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Radio = zk.$extends(zul.wgt.Checkbox, {
	_value: '',
	
	getRadiogroup: function (parent) {
		var wgt = parent || this.parent;
		for (; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.wgt.Radiogroup)) return wgt;
		return null;
	},
	isSelected: function () {
		return this.isChecked();
	},
	setSelected: function (selected, fromServer) {
		this.setChecked(selected, fromServer);
	},
	setChecked: function (checked, fromServer) {
		if (checked != this.isChecked()) {
			this.$supers('setChecked', arguments);
			if (this.getSubnode('real')) {
				var group = this.getRadiogroup();
				
				// bug #1893575 : we have to clean all of the radio at the same group.
				// in addition we can filter unnecessary onCheck with defaultChecked
				if (checked) {
					for (var items = group.getItems(), i = items.length; --i >= 0;) {
						if (items[i] != this) {
							items[i].getSubnode('real').defaultChecked = false;
							items[i]._checked = false;
						}
					}
				}
				if (group) 
					group._fixSelectedIndex();
			}
		}
	},
	getValue: function () {
		return this._value;
	},
	setValue: function (value) {
		if (value == null)
			value = "";
		if (this._value != value)
			this._value = value;
	},
	getName: function () {
		var group = this.getRadiogroup();
		return group != null ? group.getName(): this.uuid;
	},
	contentAttrs_: function () {
		var html = this.$supers('contentAttrs_', arguments);
		html += ' value="' + this.getValue() + '"';
		return html;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-radio";
	},
	beforeParentChanged_: function (newParent) {
		var oldParent = this.getRadiogroup(),
			newParent = newParent ? this.getRadiogroup(newParent) : null;
		if (oldParent != newParent) {
			if (oldParent && oldParent.$instanceof(zul.wgt.Radiogroup))
				oldParent._fixOnRemove(this); 
			if (newParent && newParent.$instanceof(zul.wgt.Radiogroup))
				newParent._fixOnAdd(this); 
		}
	}
});
