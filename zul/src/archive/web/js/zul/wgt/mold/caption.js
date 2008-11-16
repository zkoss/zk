/* caption.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 13:02:44     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	if (this.parent.legend) {
		var html = '<legend' + this.domAttrs_() + '>' + this.domContent_();
		for (var w = this.firstChild; w; w = w.nextSibling)
			html += w.redraw();
		return html + '</legend>';
	}
	var html = '<div' + this.domAttrs_() + '>';
	for (var w = this.firstChild; w; w = w.nextSibling)
		html += w.redraw();
	return html + '</div>';
}