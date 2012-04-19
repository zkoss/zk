/* separator.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 16:59:26     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var tag = this.isVertical() ? 'span': 'div';
	out.push('<', tag, this.domAttrs_(), '><div class="', this.getZclass(), 
			'-inner">&nbsp;</div></', tag, '>');
}