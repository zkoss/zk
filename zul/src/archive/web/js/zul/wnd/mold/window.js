/* window.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 17 17:51:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {
	var zcls = this.getZclass(),
		uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption,
		contentStyle = this.getContentStyle(), wcExtStyle = '',
		contentSclass = this.getContentSclass(),
		mode = this.getMode(),
		withFrame = 'embedded' != mode && 'popup' != mode,
		noborder = 'normal' != this.getBorder() ? '-noborder' : '';
		
	out.push('<div', this.domAttrs_(), '>');

	if (caption || title) {
		out.push('<div class="', zcls, '-tl', noborder,
				'"><div class="', zcls, '-tr', noborder,
				'"><div class="', zcls, '-tm', noborder,
				'"><div id="', uuid, '$cap" class="', zcls,
				'-header">');
		if (caption) caption.redraw(out);
		else {
			if (this.isClosable())
				out.push('<div id="', uuid, '$close" class="',
						zcls, '-tool ', zcls, '-close"></div>');
			if (this.isMaximizable()) {
				out.push('<div id="', uuid, '$max" class="',
						zcls, '-tool ', zcls, '-maximize');
				if (this.isMaximized())
					out.push(' ', zcls, '-maximized');
				out.push('"></div>');
			}
			if (this.isMinimizable())
				out.push('<div id="' + uuid, '$min" class="',
						zcls, '-tool ', zcls, '-minimize"></div>');
			out.push(zUtl.encodeXML(title));
		}
		out.push('</div></div></div></div>');
		wcExtStyle = 'border-top:0;';
	} else if (withFrame)
		out.push('<div class="', zcls, '-tl', noborder,
				'"><div class="', zcls, '-tr', noborder,
				'"><div class="', zcls, '-tm', noborder,
				'-noheader"></div></div></div>');

	out.push('<div id="', uuid, '$body" class="', zcls, '-body">');

	if (withFrame)
		out.push('<div class="', zcls, '-cl', noborder,
				'"><div class="', zcls, '-cr', noborder,
				'"><div class="', zcls, '-cm', noborder, '">');

	if (contentStyle) wcExtStyle += contentStyle;

	out.push('<div id="', uuid, '$cave" class="');
	if (contentSclass) out.push(contentSclass, ' ');
	out.push(zcls, '-cnt', noborder, '"');
	if (wcExtStyle) out.push(' style="', wcExtStyle,'"');
	out.push('>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != caption)
				w.redraw(out);

	out.push('</div>');

	if (withFrame)
		out.push('</div></div></div><div class="', zcls, '-bl',
				noborder, '"><div class="', zcls, '-br', noborder,
				'"><div class="', zcls, '-bm', noborder,
				'"></div></div></div>');

	out.push('</div></div>');
}