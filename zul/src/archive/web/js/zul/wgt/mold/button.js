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
	var zcls = this.getZclass(),
		tabi = this._tabindex;
	tabi = tabi >= 0 ? ' tabindex="' + tabi + '"': '';

	var html = '<span' + this.domAttrs_({style:1,domclass:1})
		+ ' class="' + zcls + '"';
	if (!this.isVisible()) html += ' style="display:none"';
	html += '><table id="' + this.uuid +'$box"' + zUtl.cellps0;
	if (tabi && !zk.gecko && !zk.safari) html += tabi;
	var s = this.domStyle_();
	if (s) html += ' style="' + s + '"';
	s = this.domClass_();
	if (s) html += ' class="' + s + '"';

	var btn = '<button id="' + this.uuid + '$btn" class="' + zcls + '"',
	s = this.isDisabled();
	if (s) btn += ' disabled="disabled"';
	if (tabi && (zk.gecko || zk.safari)) btn += tabi;
	btn += '></button>';

	html += '><tr><td class="' + zcls + '-tl">';
	if (!zk.ie) html += btn;
	html += '</td><td class="' + zcls + '-tm"></td>'
		+ '<td class="' + zcls + '-tr"></td></tr>';

	html += '<tr><td class="' + zcls + '-cl">';
	if (zk.ie) html += btn;
	html += '</td><td class="' + zcls + '-cm">'
		+ this.domContent_()
		+ '</td><td class="' + zcls + '-cr"><i class="' + zcls
		+ '"></i></td></tr>';

	return html + '<tr><td class="' + zcls + '-bl"></td>'
		+ '<td class="' + zcls + '-bm"></td>'
		+ '<td class="' + zcls + '-br"></td></tr></table></span>';
}