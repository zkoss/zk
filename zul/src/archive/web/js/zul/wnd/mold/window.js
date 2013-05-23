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

	bordercls = 'normal' == bordercls ? '':
		'none' == bordercls ? '-noborder' : '-' + bordercls;

	out.push('<div', this.domAttrs_(), '>');

	if(bordercls != '-noborder' && zcls != 'z-window-popup')
		out.push('<div class="', zcls,'-outer">');
	
	if (caption || title) {
		if(bordercls == '-noborder' || zcls == 'z-window-popup')
			out.push('<div id="',
					uuid, '-cap-outer" class="', zcls, '-header-outer">');
			
		out.push('<div id="',
			uuid, '-cap" class="', zcls, '-header">');

		if (caption) caption.redraw(out);
		else {
			var iconInner = '<div class="' + zcls + '-icon-img"></div>';
			if (this._closable)
				out.push('<div id="', uuid, '-close" class="', zcls, '-icon ', zcls, '-close">', iconInner, '</div>');
			if (this._maximizable) {
				out.push('<div id="', uuid, '-maximize" class="', zcls, '-icon ', zcls, '-maximize');
				if (this._maximized)
					out.push(' ', zcls, '-maximized');
				out.push('">', iconInner, '</div>');
			}
			if (this._minimizable)
				out.push('<div id="' + uuid, '-minimize" class="', zcls, '-icon ', zcls, '-minimize">', iconInner, '</div>');
			out.push(zUtl.encodeXML(title));
		}
		
		if(bordercls == '-noborder' || zcls == 'z-window-popup')
			out.push('</div>');
		
		out.push('</div>');
	} 
	
	out.push('<div id="', uuid, '-cave" class="');
	if (contentSclass) out.push(contentSclass, ' ');
	out.push(zcls, '-content', bordercls, '"');
	if (contentStyle) out.push(' style="', contentStyle, '"');
	out.push('>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != caption)
				w.redraw(out);

	out.push('</div>');

	if (bordercls != '-noborder' && zcls != 'z-window-popup')
		out.push('</div>');

	out.push('</div>');
}