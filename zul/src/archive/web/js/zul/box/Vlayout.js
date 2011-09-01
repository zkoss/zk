/* Vlayout.js

	Purpose:
		
	Description:
		
	History:
		Fri Aug  6 12:37:19 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A vertical layout.
 * <p>Default {@link #getZclass}: z-vlayout.
 * @since 5.0.4
 */
zul.box.Vlayout = zk.$extends(zul.box.Layout, {
	getZclass: function () {
		return this._zclass == null ? "z-vlayout" : this._zclass;
	},
	isVertical_: function () {
		return true;
	}
});