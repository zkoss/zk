/* Fileupload.js

	Purpose:
		
	Description:
		
	History:
		Thu Jul 16 17:13:30     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A fileupload widget is the same as {@link zul.wgt.Button}
 */
zul.wgt.Fileupload = zk.$extends(zul.wgt.Button, {
	getZclass: function () { // keep the button's zclass
		return this._zclass == null ? 'z-button' : this._zclass;
	}
});
