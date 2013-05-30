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
		contentSclass = this.getContentSclass();

	out.push('<div', this.domAttrs_(), '><div id="',
			uuid, '-header-outer" class="', this.$s('header-outer'), '">');
	
	if (caption || title) {
		out.push('<div id="',
			uuid, '-caption" class="', this.$s('header'), '">');

		
		if (caption) caption.redraw(out);
		else {
			var self = this;
			var getIcon = function(iconClass) {
				return '<i class="' + self.$s('icon') + ' z-' + iconClass + '"></i>';
			}
			
			var iconImg = this.$s('icon-img');
			if (this._closable)
				out.push('<div id="', uuid , '-close" class="', iconImg, ' ', this.$s('close'), '">' , getIcon('icon-remove'),  '</div>');
			if (this._maximizable) {
				out.push('<div id="', uuid , '-maximize" class="', iconImg, ' ', this.$s('maximize'));
				if (this._maximized)
					out.push(' ', this.$s('maximized'));
				out.push('">', this._maximized ? getIcon('icon-resize-small') : getIcon('icon-fullscreen') , '</div>');
			}
			if (this._minimizable)
				out.push('<div id="', uuid , '-minimize" class="', iconImg, ' ', this.$s('minimize'), '" >', getIcon('icon-minus'), '</div>');
			out.push(zUtl.encodeXML(title));
		}
		
		out.push('</div>');
	} 
	
	out.push('</div><div id="',
				uuid, '-content-outer" class="', this.$s('content-outer'), '"><div id="', uuid, '-cave" class="');
	if (contentSclass) out.push(contentSclass, ' ');
	out.push(this.$s('content'), '" ');
	if (contentStyle) out.push(' style="', contentStyle, '"');
	out.push('>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != caption)
				w.redraw(out);
	out.push('</div></div></div>');
}