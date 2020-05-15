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
	var uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption,
		contentStyle = this.getContentStyle(),
		contentSclass = this.getContentSclass(),
		tabi = this._tabindex,
		btnRenderer = zul.wgt.ButtonRenderer;

	out.push('<div', this.domAttrs_({tabindex: 1}), '>');//tabindex attribute will be set in the child elements
	
	if (caption || title) {
		out.push('<div id="',
			uuid, '-cap" class="', this.$s('header'), '">');

		
		if (caption) caption.redraw(out);
		else {
			out.push('<div id="', uuid, '-icons" class="', this.$s('icons'), '">');
			if (this._minimizable)
				btnRenderer.redrawMinimizeButton(this, out, tabi);
			if (this._maximizable)
				btnRenderer.redrawMaximizeButton(this, out, tabi);
			if (this._closable)
				btnRenderer.redrawCloseButton(this, out, tabi);
			out.push('</div>');
			out.push(zUtl.encodeXML(title));
		}
		out.push('</div>');
	} 
	out.push('<div id="', uuid, '-cave" class="');
	
	if (contentSclass)
		out.push(contentSclass, ' ');
	out.push(this.$s('content'), '" ');
	
	if (contentStyle)
		out.push(' style="', contentStyle, '"');
	out.push('>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != caption)
				w.redraw(out);
	out.push('</div></div>');
}