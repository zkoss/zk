/* div.js

	Purpose:
		
	Description:
		
	History:
		Sun Oct 19 15:11:36     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var html = '<div' + this.domAttrs_() + '>';
	for (var w = this.firstChild; w; w = w.nextSibling)
		html += w.redraw();
	return html + '</div>';
}