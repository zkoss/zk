/* auxhead.js

	Purpose:
		
	Description:
		
	History:
		Mon May  4 17:15:44     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<tr', this.domAttrs_(), ' align="left">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</tr>');
}
