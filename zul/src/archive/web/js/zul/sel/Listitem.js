/* Listitem.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:17:40     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listitem = zk.$extends(zul.Widget, {
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

	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listitem" : this._zclass;
	},
	doClick_: function(evt) {
		this.parent.fire('onSelect',
			zk.copy({items: [this.uuid], reference: this.uuid}, evt.data));
		this.$supers('doClick_', arguments);
	}
});