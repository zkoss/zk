/* Optgroup.js

	Purpose:

	Description:

	History:
		Mon Sep 03 13:01:21 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A HTML optgroup tag.
 * @since 8.6.0
 */
zul.sel.Optgroup = zk.$extends(zul.Widget, {
	_open: true,

	$define: {
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
		/** Returns whether this container is open.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether this container is open.
		 * @param boolean open
		 */
		open: function (open, fromServer) {
			if (this.desktop)
				this.parent.requestRerender_(fromServer);
		}
	},
	/** Returns the label of the {@link Listcell} it contains, or null
	 * if no such cell.
	 * @return String
	 */
	getLabel: function () {
		return this.firstChild ? this.firstChild.domLabel_() : null;
	},
	updateLabel_: function () {
		var n = this.$n();
		if (n) n.label = this.getLabel();
	},
	//@Override
	setVisible: function (visible, fromServer) {
		if (this._visible != visible) {
			this._visible = visible;
			if (this.desktop)
				this.parent.requestRerender_(fromServer);
		}
	},
	domAttrs_: function (no) {
		var attr = this.$supers('domAttrs_', arguments),
			label = this.getLabel(),
			disabled = this.isDisabled();
		if (label)
			attr += ' label="' + label + '"';
		if (disabled)
			attr += ' disabled="disabled"';
		return attr;
	}
});
