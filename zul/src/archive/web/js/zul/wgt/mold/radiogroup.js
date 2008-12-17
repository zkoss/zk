/* radiogroup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 09:32:45     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var html = '<span' + this.domAttrs_() + '>';
	for (var w = this.firstChild; w; w = w.nextSibling)
		html += w.redraw();
	return html + '</span>';
}