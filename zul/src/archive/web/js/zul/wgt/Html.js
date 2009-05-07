/* Html.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 23 20:35:12     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.wgt.Html = zk.$extends(zul.Widget, {
	_content: ''	
}), { //zk.def
	content: function (v) {
		var n = this.getNode();
		if (n) n.innerHTML = content || '';
	}
});