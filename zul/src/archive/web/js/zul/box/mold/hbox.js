/* hbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 14:10:32     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var html = '<table' + this.domAttrs_() + zUtl.cellps0 + '><tr',
		v = this.getAlign();
	if (v) html += ' valign="' + zul.box.Box._toValign(v) +'"';
	html += '>';

	for (var w = this.firstChild; w; w = w.nextSibling)
		html += this.encloseChildHTML_(w);
	return html + '</tr></table>';
}