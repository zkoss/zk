/* div.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct 19 15:11:36     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function () {
	var html = '<div id="' + this.uuid + '"';
	if (this.style) html += ' style="' + this.style + '"';
	html += '>';

	for (var w = this.firstChild; w; w = w.nextSibling)
		html += w.redraw();
	return html + '</div>';
}