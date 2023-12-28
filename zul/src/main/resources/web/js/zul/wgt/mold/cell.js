/* cell.js

	Purpose:
		
	Description:
		
	History:
		Mon Aug 31 16:50:29     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function cell$mold$(out) {
	out.push('<td', this.domAttrs_(), '>', /*safe*/ this._colHtmlPre());
	for (var j = 0, w = this.firstChild; w; w = w.nextSibling, j++)
		w.redraw(out);
	out.push('</td>');
}
