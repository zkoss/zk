/* Radio.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 16 11:17:26     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A radio button.
 *
 * <p>Radio buttons without a ancestor {@link Radiogroup} is considered
 * as the same group.
 * The nearest ancestor {@link Radiogroup} is the group that the radio
 * belongs to. See also {@link #getRadiogroup}.
 * 
 * <p>Event:
 * <ol>
 * <li>onCheck event is sent when a checkbox
 * is checked or unchecked by user.</li>
 * </ol>
 *
 */
zul.wgt.Radio = zk.$extends(zul.wgt.Checkbox, {
	$define: {
		/** Returns the value.
		 * <p>Default: "".
		 * @return String
		 */
		/** Sets the value.
		 * @param String value the value; If null, it is considered as empty.
		 */
		value: function (v) {
			var n = this.$n('real');
			if (n) n.value = v || '';
		}
	},
	/** Returns {@link Radiogroup} that this radio button belongs to.
	 * It is the nearest ancestor {@link Radiogroup}.
	 * In other words, it searches up the parent, parent's parent
	 * and so on for any {@link Radiogroup} instance.
	 * If found this radio belongs the found radiogroup.
	 * If not, this radio itself is a group.
	 * @return Radiogroup
	 */
	getRadiogroup: function (parent) {
		var wgt = parent || this.parent;
		for (; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.wgt.Radiogroup)) return wgt;
		return null;
	},
	/** Sets the radio is checked and unchecked the others in the same radio
	 * group ({@link Radiogroup}
	 * @param boolean checked
	 * @return Radio
	 * @disable(zkgwt)
	 */
	setChecked: _zkf = function (checked) {
		if (checked != this._checked) {
			this._checked = checked;
			var n = this.$n('real');
			if (n) {
				n.checked = checked || false;

				var group = this.getRadiogroup();
				if (group) {
					// bug #1893575 : we have to clean all of the radio at the same group.
					if (checked) {
						for (var items = group.getItems(), i = items.length; i--;) {
							if (items[i] != this) {
								items[i].$n('real').checked = false;
								items[i]._checked = false;
							}
						}
					}
					group._fixSelectedIndex();
				}
			}
		}
		return this;
	},
	/** Sets whether it is selected.
	 * <p>Don't override this. Override {@link #setChecked} instead.
	 * <p>The same as {@link #setChecked}.
	 * @param boolean selected
	 * @return Radio
	 */
	setSelected: _zkf,
	/** Returns whether it is selected.
	 * <p>Default: false.
	 * <p>Don't override this. Override {@link #isChecked} instead.
	 * @return boolean
	 */
	isSelected: zul.wgt.Checkbox.prototype.isChecked,
	/** Returns the name of this radio button.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>It is readonly, and it is generated automatically
	 * to be the same as its parent's name ({@link Radiogroup#getName}).
	 * @return String
	 */
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
		this.$supers("beforeParentChanged_", arguments);
	}
});
