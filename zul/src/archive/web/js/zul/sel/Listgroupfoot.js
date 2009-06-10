/* Listgroupfoot.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 09:29:46     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Listgroupfoot = zk.$extends(zul.sel.Listitem, {
	getZclass: function () {
		return this._zclass != null ? this._zclass : "z-listgroupfoot";
	},
	isStripeable_: function () {
		return false;
	}
});