/* Radio.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 16 11:17:26     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
_zkc = zul.wgt.Radio = zk.$extends(zul.wgt.Checkbox, {
	getRadiogroup: function (parent) {
		var wgt = parent || this.parent;
		for (; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.wgt.Radiogroup)) return wgt;
		return null;
	},
	setChecked: _zkf = function (checked) {
		if (checked != this._checked) {
			this._checked = checked;
			var n = this.getSubnode('real');
			if (n) {
				n.checked = checked || false;

				var group = this.getRadiogroup();
				if (group) {
					// bug #1893575 : we have to clean all of the radio at the same group.
					if (checked) {
						for (var items = group.getItems(), i = items.length; --i >= 0;) {
							if (items[i] != this) {
								items[i].getSubnode('real').checked = false;
								items[i]._checked = false;
							}
						}
					}
					group._fixSelectedIndex();
				}
			}
		}
	},
	setSelected: _zkf,
	isSelected: zul.wgt.Checkbox.prototype.isChecked,
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

zk.def(_zkc, {
	value: function (v) {
		var n = this.getSubnode('real');
		if (n) n.value = v || '';
	}
});
