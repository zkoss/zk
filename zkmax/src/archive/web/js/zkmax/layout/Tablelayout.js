/* Tablelayout.js

	Purpose:
		
	Description:
		
	History:
		Thu May 19 14:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkmax.layout.Tablelayout = zk.$extends(zul.Widget, {
	_columns: 1,
	
	$define: {
		columns: function() {
			this.rerender();
		}
	},
	
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-table-layout";
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		this.rerender();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		this.rerender();
	}
});