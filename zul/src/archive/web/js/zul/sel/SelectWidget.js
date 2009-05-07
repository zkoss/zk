/* SelectWidget.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.sel.SelectWidget = zk.$extends(zul.mesh.MeshWidget, {
}), { //zk.def
	checkmark: function (checkmark) {
		this.rerender();
	},
	multiple: function () {
		//TODO: handle selection
		if (this._checkmark) this.rerender();
	}
});