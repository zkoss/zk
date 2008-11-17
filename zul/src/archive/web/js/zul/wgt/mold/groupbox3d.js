/* groupbox3d.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:47:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var html = '<div' + this.domAttrs_() + '>',
		zcls = this.getZclass(),
		uuid = this.uuid;
		cap = this.caption;

	if (cap)
		html += '<div class="' + zcls + '-tl"><div class="' + zcls
			+ '-tr"><div class="' + zcls + '-tm"><div class="zcls-header">'
			+ cap.redraw() + '</div></div></div></div>';

	html += '<div id="' + uuid + '$panel" class="' + zcls + '-body"';
	if (!this.isOpen()) html += ' style="display:none"';
	html += '><div id="' + uuid + '$cave"' + this._contentAttrs() + '>';

	for (var w = this.firstChild; w; w = w.nextSibling)
		if (w != cap)
			html += w.redraw();
	return html + '</div></div>'
		//shadow
		+ '<div id="' + uuid + '$sdw" class="' + zcls +'-bl"><div class="' + zcls
		+ '-br"><div class="' + zcls + '-bm"></div></div></div></div>';
}