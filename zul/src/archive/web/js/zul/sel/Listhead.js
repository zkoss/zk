/* Listhead.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:25:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A list headers used to define multi-columns and/or headers.
 *
 *  <p>Default {@link #getZclass}: z-listhead.
 */
zul.sel.Listhead = zk.$extends(zul.mesh.HeadWidget, {
	/** Returns the listbox that this belongs to.
	 * @return Listbox
	 */
	getListbox: zul.mesh.HeadWidget.prototype.getMeshWidget,
	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listhead" : this._zclass;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		var list = this.getListbox();
		if (list && list.$instanceof(zul.sel.Listbox)) list._syncEmpty();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		var list = this.getListbox();
		if (list && list.$instanceof(zul.sel.Listbox)) list._syncEmpty();
	}
});