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
	
	/** Returns {@link Radiogroup} that this radio button belongs to.
	 * It is the nearest ancestor {@link Radiogroup}.
	 * In other words, it searches up the parent, parent's parent
	 * and so on for any {@link Radiogroup} instance.
	 * If found this radio belongs the found radiogroup.
	 * If not, this radio itself is a group.
	 */
	getRadiogroup: function (parent) {
		var wgt = parent || this.parent;
		for (; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.wgt.Radiogroup)) return wgt;
		return null;
	},

	/** Returns whether it is selected.
	 * <p>Default: false.
	 * <p>Don't override this. Override {@link #isChecked} instead.
	 */
	isSelected: function () {
		return this.isChecked();
	},
	/** Sets whether it is selected.
	 * <p>Don't override this. Override {@link #setChecked} instead.
	 * <p>The same as {@link #setChecked}.
	 */
	setSelected: function (selected, fromServer) {
		this.setChecked(selected, fromServer);
	},
	/** Sets the radio is checked and unchecked the others in the same radio
	 * group ({@link Radiogroup}.
	 */
	setChecked: function (checked, fromServer) {
		if (checked != this.isChecked()) {
			this.$super('setChecked', checked, fromServer);
			if (this.ereal) {
				var group = this.getRadiogroup();
				
				// bug #1893575 : we have to clean all of the radio at the same group.
				// in addition we can filter unnecessary onCheck with defaultChecked
				if (checked) {
					for (var items = group.getItems(), i = items.length; --i >= 0;) {
						if (items[i] != this) {
							items[i].ereal.defaultChecked = false;
							items[i]._checked = false;
						}
					}
				}
				if (group) 
					group.fixSelectedIndex_();
			}
		}
	},

	/** Returns the value.
	 * <p>Default: "".
	 */
	getValue: function () {
		return this._value;
	},
	/** Sets the value.
	 * @param value the value; If null, it is considered as empty.
	 */
	setValue: function (value) {
		if (value == null)
			value = "";
		if (this._value != value)
			this._value = value;
	},

	/** Returns the name of this radio button.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>It is readonly, and it is generated automatically
	 * to be the same as its parent's name ({@link Radiogroup#getName}).
	 */
	getName: function () {
		var group = this.getRadiogroup();
		return group != null ? group.getName(): this.uuid;
	},

	/** Returns the inner attributes for generating the HTML radio tag
	 * (the name and value attribute).
	 * <p>Used only by component developers.
	 */
	contentAttrs_: function () {
		var html = this.$super('contentAttrs_');
		html += ' value="' + this.getValue() + '"';
		return html;
	},
	/** Returns the Style of radio label
	 *
	 * <p>Default: "z-radio"
	 * <p>Since 3.5.1
	 * 
	 */
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-radio";
	},
	beforeParentChange_: function (newParent) {
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
