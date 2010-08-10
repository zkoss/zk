/* Hlayout.js

	Purpose:
		
	Description:
		
	History:
		Fri Aug  6 11:54:19 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A horizontal layout.
 * <p>Default {@link #getZclass}: z-hlayout.
 * @since 5.0.4
 */
zul.layout.Hlayout = zk.$extends(zul.layout.BaseLayout, {
	isVertical_: function () {
		return false;
	},
	getZclass: function () {
		return this._zclass == null ? "z-hlayout" : this._zclass;
	}
});