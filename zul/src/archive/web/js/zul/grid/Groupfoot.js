/* Groupfoot.js

	Purpose:
		
	Description:
		
	History:
		Fri May 15 15:25:15     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * GroupFoot serves as a summary row of group.
 * 
 * <p>Default {@link #getZclass}: z-groupfoot 
 * 
 * <p>Note: All the child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the {@link zul.wgt.Label#setSclass(String)}
 * after the child added.
 * 
 */
zul.grid.Groupfoot = zk.$extends(zul.grid.Row, {
	getZclass: function () {
		return this._zclass != null ? this._zclass : "z-groupfoot";
	},
	isStripeable_: function () {
		return false;
	}
});
