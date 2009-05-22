/* ItemWidget.js

	Purpose:
		
	Description:
		
	History:
		Fri May 22 21:50:50     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.ItemWidget = zk.$extends(zul.Widget, {
	$define: {
		checkable: function () {
			this.rerender();
		},
		selected: function () {
	//		if (this.parent)
	//			this.parent.toggleItemSelection(this);
		},
		disabled: function () {
		}
	},

	isStripeable_: function () {
		return true;
	},	

	//super//
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			if (this.isStripeable_() && this.parent)
				this.parent.stripe();
		}
	},
	doClick_: function(evt) {
		this.parent.fire('onSelect',
			zk.copy({items: [this.uuid], reference: this.uuid}, evt.data));
		this.$supers('doClick_', arguments);
	}
});