/* Menuseparator.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:35     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Used to create a separator between menu items.
 *
 *<p>Default {@link #getZclass}: z-menu-separator.
 */
zul.menu.Menuseparator = zk.$extends(zul.Widget, {
	/** Returns whether parent is a {@link Menupopup}
	 * @return boolean
	 */
	isPopup: function () {
		return this.parent && this.parent.$instanceof(zul.menu.Menupopup);
	},
	/** Returns the {@link Menubar} that contains this menuseparator, or null if not available.
	 * @return zul.menu.Menubar
	 */
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-separator" : this._zclass;
	},
	doMouseOver_: function () {
		if (zul.menu._nOpen)
			zWatch.fire('onFloatUp', this); //notify all
		this.$supers('doMouseOver_', arguments);
	}
});
