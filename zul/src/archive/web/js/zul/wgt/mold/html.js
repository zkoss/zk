/* html.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 23 20:41:06     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<span', this.domAttrs_(), '>',
		(jq.isArray(this._content) ? ""/*z$ea*/:this._content), '</span>');
}