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
	//		p = this.getMeshWidget();
	//		if (p)
	//			p.toggleItemSelection(this);
		},
		disabled: function () {
		}
	},

	isStripeable_: function () {
		return true;
	},
	getMeshWidget: function () {
		return this.parent;
	},

	//super//
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			if (this.isStripeable_()) {
				var p = this.getMeshWidget();
				if (p) p.stripe();
			}
		}
	},
	doClick_: function(evt) {
		var p = this.getMeshWidget();
		p.fire('onSelect',
			zk.copy({items: [this.uuid], reference: this.uuid}, evt.data));
		this.$supers('doClick_', arguments);
	}
});