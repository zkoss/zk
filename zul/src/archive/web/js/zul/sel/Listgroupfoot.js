/* Listgroupfoot.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 09:29:46     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * GroupFooter serves as a summary listitem of listgroup.
 * 
 * <p>Default {@link #getZclass}: z-listgroupfoot
 * 
 *<p>Note: All the {@link Label} child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the {@link Label#setSclass(String)}
 * after the child added.
 */
zul.sel.Listgroupfoot = zk.$extends(zul.sel.Listitem, {
	getZclass: function () {
		return this._zclass != null ? this._zclass : "z-listgroupfoot";
	},
	/**
	 * Returns whether is stripeable or not.
	 * <p> Default: false
	 * @return boolean
	 */
	isStripeable_: function () {
		return false;
	}
});