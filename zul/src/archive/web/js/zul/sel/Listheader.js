/* Listheader.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:25:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listheader = zk.$extends(zul.mesh.SortWidget, {
	getListbox: zul.mesh.HeaderWidget.prototype.getMeshWidget,
	
	getMeshBody: zul.mesh.HeaderWidget.prototype.getMeshWidget,
	
	//super//
	getZclass: function () {
		return this._zclass == null ? "z-listheader" : this._zclass;
	},
	domContent_: function () {
		var s = this.$supers('domContent_', arguments),
			box = this.getListbox();
		if (box != null && this.parent.firstChild == this 
		&& box.isCheckmark() && box.isMultiple())
			s = '<input type="checkbox" id="' + this.uuid + '$cm"/>'
				+ (s ? '&nbsp;' + s:'');
		return s;
	}
});
