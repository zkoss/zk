/* rows.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:26:46     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<tbody', this.domAttrs_() , '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</tbody>');	
}
