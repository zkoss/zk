/* vbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 14:10:39     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var html = '<table' + this.getOuterAttrs_() + zUtl.cellps0 + '>',
		spacing = this.spacing,
		spacing0 = spacing && spacing.startsWith('0')
			&& (spacing.length == 1 || zk.isDigit(spacing.charAt(1))),
		spstyle = spacing ? 'height:' + spacing: '';

	for (var w = this.firstChild; w; w = w.nextSibling) {
		html += '<tr id="' + this.uuid + '!chdextr"><td>'
			+ w.redraw() + '</td></tr>';
		if (w.nextSibling) {
			html += '<tr id="' + this.uuid + '!chdextr2"';
			var s = spstyle;
			if (spacing0 || !w.isVisible()) s = 'display:none' + s;
			if (s) html += ' style="' + s + '"';
			html += '><td>' + zUtl.img0 + '</td></tr>';
		}
	}
	return html + '</table>';
}