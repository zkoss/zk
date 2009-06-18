/* window.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 17 17:51:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {
	var zcls = this.getZclass(),
		uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption,
		contentStyle = this.getContentStyle(),
		contentSclass = this.getContentSclass(),
		mode = this.getMode(),
		withFrame = 'embedded' != mode && 'popup' != mode,
		noborder = 'normal' != this.getBorder() ? '-noborder' : '';
		
	out.push('<div', this.domAttrs_(), '>');

	if (caption || title) {
		out.push('<div class="', zcls, '-tl"><div class="',
			zcls, '-tr"></div></div><div class="',
			zcls, '-hl"><div class="', zcls,
			'-hr"><div class="', zcls, '-hm"><div id="',
			uuid, '-cap" class="', zcls, '-header">');

		if (caption) caption.redraw(out);
		else {
			if (this.isClosable())
				out.push('<div id="', uuid, '-close" class="', zcls, '-icon ', zcls, '-close"></div>');
			if (this.isMaximizable()) {
				out.push('<div id="', uuid, '-max" class="', zcls, '-icon ', zcls, '-max');
				if (this.isMaximized())
					out.push(' ', zcls, '-maxd');
				out.push('"></div>');
			}
			if (this.isMinimizable())
				out.push('<div id="' + uuid, '-min" class="', zcls, '-icon ', zcls, '-min"></div>');
			out.push(zUtl.encodeXML(title));
		}
		out.push('</div></div></div></div>');
	} else if (withFrame)
		out.push('<div class="', zcls, '-tl', noborder,
				'"><div class="', zcls, '-tr', noborder, '"></div></div>');

	if (withFrame)
		out.push('<div class="', zcls, '-cl', noborder,
			'"><div class="', zcls, '-cr', noborder,
			'"><div class="', zcls, '-cm', noborder, '">');

	out.push('<div id="', uuid, '-cave" class="');
	if (contentSclass) out.push(contentSclass, ' ');
	out.push(zcls, '-cnt', noborder, '"');
	if (contentStyle) out.push(' style="', contentStyle, '"');
	out.push('>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != caption)
				w.redraw(out);

	out.push('</div>');

	if (withFrame)
		out.push('</div></div></div><div class="', zcls, '-bl', noborder,
			'"><div class="', zcls, '-br', noborder, '"></div></div>');

	out.push('</div>');
}