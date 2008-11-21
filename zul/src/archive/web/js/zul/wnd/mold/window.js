/* window.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 17 17:51:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (skipper) {
	var zcls = this.getZclass(),
		uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption,
		contentStyle = this.getContentStyle(), wcExtStyle = '',
		contentSclass = this.getContentSclass(),
		mode = this.getMode(),
		withFrame = 'embedded' != mode && 'popup' != mode,
		noborder = 'normal' != this.getBorder() ? '-noborder' : '',
		html = '<div' + this.domAttrs_() + '>';

	if (caption || title) {
		html += '<div class="' + zcls + '-tl' + noborder
			+ '"><div class="' + zcls + '-tr' + noborder
			+ '"><div class="' + zcls + '-tm' + noborder
			+ '"><div id="' + uuid + '$cap" class="' + zcls
			+ '-header">';
		if (caption) html += caption.redraw();
		else {
			if (this.isClosable())
				html += '<div id="' + uuid + '$close" class="'
					+ zcls + '-tool ' + zcls + '-close"></div>';
			if (this.isMaximizable()) {
				html += '<div id="' + uuid + '$max" class="'
					+ zcls + '-tool ' + zcls + '-maximize';
				if (this.isMaximized())
					html += ' ' + zcls + '-maximized';
				html += '"></div>';
			}
			if (this.isMinimizable())
				html += '<div id="' + uuid + '$min" class="'
					+ zcls + '-tool ' + zcls + '-minimize"></div>';
			html += zUtl.encodeXML(title);
		}
		html += '</div></div></div></div>';
		wcExtStyle = 'border-top:0;';
	} else if (withFrame)
		html += '<div class="' + zcls + '-tl' + noborder
			+ '"><div class="' + zcls + '-tr' + noborder
			+ '"><div class="' + zcls + '-tm' + noborder
			+ '-noheader"></div></div></div>';

	html += '<div id="' + uuid + '$bwrap" class="' + zcls + '-body">';

	if (withFrame)
		html += '<div class="' + zcls + '-cl' + noborder
			+ '"><div class="' + zcls + '-cr' + noborder
			+ '"><div class="' + zcls + '-cm' + noborder + '">';

	if (contentStyle) wcExtStyle += contentStyle;

	html += '<div id="' + uuid + '$cave" class="';
	if (contentSclass) html += contentSclass + ' ';
	html += zcls + '-cnt' + noborder + '"';
	if (wcExtStyle) html += ' style="' + wcExtStyle +'"';
	html += '>';

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != caption)
				html += w.redraw();

	html += '</div>';

	if (withFrame)
		html += '</div></div></div><div class="' + zcls + '-bl'
			+ noborder + '"><div class="' + zcls + '-br' + noborder
			+ '"><div class="' + zcls + '-bm' + noborder
			+ '"></div></div></div>';

	return html + '</div></div>';
}