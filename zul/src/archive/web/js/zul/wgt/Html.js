/* Html.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 23 20:35:12     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Html = zk.$extends(zul.Widget, {
	_content: '',
	getContent: function () {
		return this._content;
	},
	setContent: function (content) {
		if (!content) content = '';
		if (this._content != content) {
			this._content = content;
			var n = this.node;
			if (n) n.innerHTML = content;
		}
	}
});
