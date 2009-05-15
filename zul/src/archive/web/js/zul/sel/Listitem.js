/* Listitem.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:17:40     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.sel.Listitem = zk.$extends(zul.Widget, {
	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listitem" : this._zclass;
	},
	doClick_: function(evt) {
		this.parent.fire('onSelect', {
			items: [this.uuid],
			reference: this.uuid,
			keys: zEvt.metaData(evt)
		});
		this.$supers('doClick_', arguments);
	}
}), { // zk.def
	checkable: function () {
		this.rerender();
	},
	selected: function () {
//		if (this.parent)
//			this.parent.toggleItemSelection(this);
	},
	disabled: function () {
	}
});