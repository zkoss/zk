/* Option.js

	Purpose:
		
	Description:
		
	History:
		Mon Jun  1 16:43:59     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Option = zk.$extends(zul.Widget, {
	_label: '',

	$define: {
		label: function () {
			this.rerender();
		},
		disabled: function (disabled) {
			var n = this.getNode();
			if (n) n.disabled = disabled ? 'disabled' : '';
		}
	},
	setSelected: function (selected) {
		if (this._selected != selected) {
			if (this.parent)
				this.parent.toggleItemSelection(this);
			
			var n = this.getNode();
			if (n) n.selected = selected ? 'selected' : '';
			this._selected = selected;
		}
	},
	isSelected: function () {
		return this._selected;
	},
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel());
	},
	domAttrs_: function () {
		return this.$supers('domAttrs_', arguments) + (this.isDisabled() ? ' disabled="disabled"' :'') +
		(this.isSelected() ? ' selected="selected"' : '');
	}
});