/* Listfooter.js

	Purpose:
		
	Description:
		
	History:
		Tue Jun  9 18:03:07     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listfooter = zk.$extends(zul.LabelImageWidget, {
	_span: 1,

	$define: {
		span: function (v) {
			var n = this.$n();
			if (n) n.colSpan = v;
		}
	},
	
	getListbox: function () {
		return this.parent ? this.parent.parent : null;
	},
	getListheader: function () {
		var listbox = this.getListbox();
		if (listbox) {
			var cs = listbox.listhead;
			if (cs)
				return cs.getChildAt(this.getChildIndex());
		}
		return null;
	},
	getZclass: function () {
		return this._zclass == null ? "z-listfooter" : this._zclass;
	}
});
