/* Listfoot.js

	Purpose:
		
	Description:
		
	History:
		Tue Jun  9 18:03:06     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A row of {@link Listfooter}.
 *
 * <p>Like {@link Listhead}, each listbox has at most one {@link Listfoot}.
 * <p>Default {@link #getZclass}: z-listfoot
 */
zul.sel.Listfoot = zk.$extends(zul.Widget, {
	/** Returns the list box that it belongs to.
	 * @return Listbox
	 */
	getListbox: function () {
		return this.parent;
	},
	getZclass: function () {
		return this._zclass == null ? "z-listfoot" : this._zclass;
	},
	//bug #3014664
	setVflex: function (v) { //vflex ignored for Listfoot
		v = false;
		this.$super(zul.sel.Listfoot, 'setVflex', v);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for Listfoot
		v = false;
		this.$super(zul.sel.Listfoot, 'setHflex', v);
	}
});
