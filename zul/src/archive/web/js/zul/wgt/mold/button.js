/* button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:43     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var html = '<span' + this.getOuterAttrs_() + '><table id="' +
		+ this.uuid +'$box"' + zUtl.cellps0,
		v = this.tabindex;
	if (v >= 0 && !zk.gecko && !zk.safari)
		html += ' tabindex="' + v + '"';
	html += '><tr><td>';
	return html + this.getLabel() + '</td></tr></table></span>';
}