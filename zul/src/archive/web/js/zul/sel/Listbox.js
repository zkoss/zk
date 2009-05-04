/* Listbox.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:16:07     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listbox = zk.$extends(zul.sel.SelectWidget, {
	//-- super --//
	getZclass: function () {
		return this._zclass == null ? "z-grid" : this._zclass;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
	}
});