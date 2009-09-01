/* cell.js

	Purpose:
		
	Description:
		
	History:
		Mon Aug 31 16:50:29     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<td', this.domAttrs_(), '>');
	for (var j = 0, w = this.firstChild; w; w = w.nextSibling, j++)
		w.redraw(out);
	out.push('</td>');
}
