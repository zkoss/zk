/* window.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 17 17:51:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {
	var zcls = this.getZclass(),
		uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption,
		contentStyle = this.getContentStyle(),
		contentSclass = this.getContentSclass(),
		withFrame = zul.wnd.WindowRenderer.shallCheckBorder(this),
		bordercls = this._border;

	bordercls = "normal" == bordercls ? "":
		"none" == bordercls ? "-noborder" : '-' + bordercls;

	out.push('<div', this.domAttrs_(), '>');

	if (caption || title) {
		out.push('<div class="', zcls, '-tl"><div class="',
			zcls, '-tr"></div></div><div class="',
			zcls, '-hl"><div class="', zcls,
			'-hr"><div class="', zcls, '-hm"><div id="',
			uuid, '-cap" class="', zcls, '-header">');

		if (caption) caption.redraw(out);
		else {
			if (this._closable)
				out.push('<div id="', uuid, '-close" class="', zcls, '-icon ', zcls, '-close"></div>');
			if (this._maximizable) {
				out.push('<div id="', uuid, '-max" class="', zcls, '-icon ', zcls, '-max');
				if (this._maximized)
					out.push(' ', zcls, '-maxd');
				out.push('"></div>');
			}
			if (this._minimizable)
				out.push('<div id="' + uuid, '-min" class="', zcls, '-icon ', zcls, '-min"></div>');
			out.push(zUtl.encodeXML(title));
		}
		out.push('</div></div></div></div>');
	} else if (withFrame)
		out.push('<div class="', zcls, '-tl', bordercls,
				'"><div class="', zcls, '-tr', bordercls, '"></div></div>');

	if (withFrame)
		out.push('<div class="', zcls, '-cl', bordercls,
			'"><div class="', zcls, '-cr', bordercls,
			'"><div class="', zcls, '-cm', bordercls, '">');

	out.push('<div id="', uuid, '-cave" class="');
	if (contentSclass) out.push(contentSclass, ' ');
	out.push(zcls, '-cnt', bordercls, '"');
	if (contentStyle) out.push(' style="', contentStyle, '"');
	out.push('>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != caption)
				w.redraw(out);

	out.push('</div>');

	if (withFrame)
		out.push('</div></div></div><div class="', zcls, '-bl', bordercls,
			'"><div class="', zcls, '-br', bordercls, '"></div></div>');

	out.push('</div>');
}