/* Html.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 23 20:35:12     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Html = zk.$extends(zul.Widget, {
	_content: '',
	$define: {
		content: function (v) {
			var n = this.$n();
			if (n) n.innerHTML = v|| '';
		}
	}
});
