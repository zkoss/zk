/* SelectWidget.js

	Purpose:
		
	Description:
		
	History:
		Thu Apr 30 22:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.SelectWidget = zk.$extends(zul.mesh.MeshWidget, {
	isCheckmark: function () {
		return this._checkmark;
	},
	setCheckmark: function (checkmark) {
		if (this._checkmark != checkmark) {
			this._checkmark = checkmark;
			if (this.desktop) this.rerender();
		}
	},

	isMultiple: function () {
		return this._multiple;
	},
	setMultiple: function (multiple) {
		if (this._multiple != multiple) {
			this._multiple = multiple;
			//TODO: handle selection
			if (this.desktop && this._checkmark) this.rerender();
		}
	}

});