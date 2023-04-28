/* Option.js

	Purpose:

	Description:

	History:
		Mon Jun  1 16:43:59     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A HTML option tag.
 */
zul.sel.Option = zk.$extends(zul.Widget, {
	_selected: false,
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
		/** Returns the value.
		 * <p>Default: null.
		 * <p>Note: the value is application dependent, you can place
		 * whatever value you want.
		 * <p>If you are using listitem with HTML Form (and with
		 * the name attribute), it is better to specify a String-typed
		 * value.
		 * @return String
		 */
		/** Sets the value.
		 * @param String value the value.
		 * <p>Note: the value is application dependent, you can place
		 * whatever value you want.
		 * <p>If you are using listitem with HTML Form (and with
		 * the name attribute), it is better to specify a String-typed
		 * value.
		 */
		value: null
	},
	//@Override
	focus: function (timeout) {
		var p = this.parent;
		if (p) p.focus(timeout);
	},

	//@Override
	setVisible: function (visible, fromServer) {
		if (this._visible != visible) {
			this._visible = visible;
			if (this.desktop)
				this.parent.requestRerender_(fromServer);
		}
	},
	/** Sets whether it is selected.
	 * @param boolean selected
	 */
	setSelected: function (selected) {
		if (this.__updating__) { // for B50-3012466.zul
			delete this.__updating__;
			return; //nothing to do for second loop triggered by this.parent.toggleItemSelection
		}
		try {
			selected = selected || false;
			this.__updating__ = true;
			if (this._selected != selected) {
				if (this.parent)
					this.parent.toggleItemSelection(this);
				this._setSelectedDirectly(selected); // always setting for B50-3012466.zul
			}
		} finally {
			delete this.__updating__;
		}
	},
	_setSelectedDirectly: function (selected) {
		var n = this.$n();
		// Bug ZK-2285, ignore if the status is the same for IE's issue
		if (n && n.selected != selected) {
			n.selected = selected ? 'selected' : '';
		}
		this._selected = selected;
	},
	/** Returns whether it is selected.
	 * <p>Default: false.
	 * @return boolean
	 */
	isSelected: function () {
		return this._selected;
	},
	/** Returns the label of the {@link Listcell} it contains, or null
	 * if no such cell.
	 * @return String
	 */
	getLabel: function () {
		return this.firstChild ? this.firstChild.getLabel() : null;
	},
	updateLabel_: function () {
		var n = this.$n();
		if (n) jq(n).html(this.domLabel_());
	},
	/** Returns the maximal length of each item's label.
	 * It is a shortcut of {@link Select#getMaxlength}.
	 * @return int
	 */
	getMaxlength: function () {
		return this.parent ? this.parent.getMaxlength() : 0;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		//B60-ZK-1303: force update parent's selected index.
		if (this.isSelected()) {
			this.parent.updateSelectionDirectly(this);
		}
	},
	doClick_: function (evt) {
		evt.stop(); // Eats the non-standard onclick event
	},
	/**
	 * The index for option widget only , not including the listhead.etc
	 * @since 6.0.1
	 */
	getOptionIndex_: function () {
		var parent = this.parent, ret = -1;
		if (parent) {
			for (var w = parent.firstChild; w; w = w.nextSibling) {
				if (w.$instanceof(zul.sel.Option)) {
					ret++;
					if (w == this) break;
				}
			}
		}
		return ret;
	},
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this.getMaxlength()});
	},
	domAttrs_: function () {
		var value = this.getValue();
		return this.$supers('domAttrs_', arguments) + (this.isDisabled() ? ' disabled="disabled"' : '')
			+ (this.isSelected() ? ' selected="selected"' : '') + (value ? ' value="' + value + '"' : '');
	},
	replaceWidget: function (newwgt) {
		this._syncItems(newwgt);
		this.$supers('replaceWidget', arguments);
	},
	_syncItems: function (newwgt) {
		if (this.parent && this.isSelected()) {
			var items = this.parent._selItems;
			if (items && items.$remove(this))
				items.push(newwgt);
		}
	}
});
