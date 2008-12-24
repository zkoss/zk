/* toolbarpanel.js

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var zcls = this.getZclass();
		html = '<div ' + this.domAttrs_() + '>',
		clear = '<div class="z-clear"></div>';	
		html += '<div class="' + zcls + '-body ' + zcls + '-' + this.getAlign() + '" >';
		html += '<table id="' + this.uuid + '$cnt" class="' + zcls + '-cnt"' + zUtl.cellps0 +'><tbody>';
		if ('vertical' != this.getOrient()) {
			html += "<tr>"
			for (var w = this.firstChild; w; w = w.nextSibling) {
				html += '<td class="' + this.getZclass() + '-hor">'
				html += w.redraw() + "</td>";
			}
			html += "</tr>"
		} else {
			for (var w = this.firstChild; w; w = w.nextSibling) {
				html += '<tr><td class="' + this.getZclass() + '-ver">'
				html += w.redraw() + "</td></tr>";
			}
		}
	return html + "</tbody></table>" +clear + '</div>' + '</div>';
}
