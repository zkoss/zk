/* vbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 14:10:39     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<table', this.domAttrs_(), zUtl.cellps0, '>');

	for (var w = this.firstChild; w; w = w.nextSibling)
		out.push(this.encloseChildHTML_(w));
	out.push('</table>');
}