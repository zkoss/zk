/* Menuseparator.js

	Purpose:
		
	Description:
		
	History:
		Thu Jan 15 09:02:35     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.menu.Menuseparator = zk.$extends(zul.Widget, {
	isPopup: function () {
		return this.parent && this.parent.$instanceof(zul.menu.Menupopup);
	},
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-separator" : this._zclass;
	},
	doMouseOver_: function (evt, devt) {
		zWatch.fire('onFloatUp', null, this); //notify all
		this.$supers('doMouseOver_', arguments);
	}
});
