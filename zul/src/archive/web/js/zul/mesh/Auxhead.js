/* Auxhead.js

	Purpose:
		
	Description:
		
	History:
		Mon May  4 15:57:46     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Used to define a collection of auxiliary headers ({@link Auxheader}).
 *
 * <p>Non XUL element.
 * <p>Default {@link #getZclass}: z-auxhead.
 */
zul.mesh.Auxhead = zk.$extends(zul.mesh.HeadWidget, {
	//super//
	getZclass: function () {
		return this._zclass == null ? "z-auxhead" : this._zclass;
	}
});
