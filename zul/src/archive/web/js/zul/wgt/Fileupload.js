/* Fileupload.js

	Purpose:
		
	Description:
		
	History:
		Thu Jul 16 17:13:30     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.utl.Fileupload = zk.$extends(zul.Widget, {
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-fileupload";
	}
});
