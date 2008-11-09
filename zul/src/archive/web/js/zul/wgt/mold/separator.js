/* separator.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 16:59:26     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var tag = this.vertical ? 'span': 'div';
	return '<' + tag + this.getDomAttrs_() + '>' + zUtl.img0 + '</' + tag + '>';
}