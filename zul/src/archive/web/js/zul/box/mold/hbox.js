/* hbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 14:10:32     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<table', this.domAttrs_(), zUtl.cellps0, '><tr');
	
	var	v = this.getAlign();
	if (v) out.push(' valign="', zul.box.Box._toValign(v), '"');
	out.push('>');

	for (var w = this.firstChild; w; w = w.nextSibling)
		this.encloseChildHTML_(w, false, out);

	out.push('</tr></table>');
}