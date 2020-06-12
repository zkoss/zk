/* panel.js

	Purpose:
		
	Description:
		
	History:
		Mon Jan 12 18:31:46     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {
	var uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption,
		btnRenderer = zul.wgt.ButtonRenderer,
		tabi = this._tabindex;

	out.push('<div', this.domAttrs_({tabindex: 1}), '>');//tabindex attribute will be set in the child elements
	if (caption || title) {
		out.push('<div id="', uuid, '-head" class="', this.$s('head'), '">', 
				'<div id="', uuid, '-cap" class="', this.$s('header'), '">');
		if (caption) caption.redraw(out);
		else {
			out.push('<div id="', uuid, '-icons" class="', this.$s('icons'), '">');
			if (this._collapsible)
				btnRenderer.redrawCollapseButton(this, out, tabi);
			if (this._minimizable)
				btnRenderer.redrawMinimizeButton(this, out, tabi);
			if (this._maximizable)
				btnRenderer.redrawMaximizeButton(this, out, tabi);
			if (this._closable)
				btnRenderer.redrawCloseButton(this, out, tabi);
			out.push('</div>');
			out.push(zUtl.encodeXML(title));
		}
		out.push('</div></div>');
	} else {
		out.push('<div id="', uuid, '-drag-button" class="', this.$s('drag-button'), " z-icon-minus", '"></div>');
	}
	
	out.push('<div id="', uuid, '-body" class="', this.$s('body'), '"');
	if (!this._open) 
		out.push(' style="display:none;"');
	out.push('>');
	
	if (!skipper) {
		if (this.tbar) {
			out.push('<div id="', uuid, '-tb" class="', this.$s('top'), '">');
			this.tbar.redraw(out);
			out.push('</div>');
		}
		
		if (this.panelchildren)
			this.panelchildren.redraw(out);
			
		if (this.bbar) {
			out.push('<div id="', uuid, '-bb" class="', this.$s('bottom'), '">');
			this.bbar.redraw(out);
			out.push('</div>');
		}
		
		if (this.fbar) {
			out.push('<div id="', uuid, '-fb" class="', this.$s('footer'), '">');
			this.fbar.redraw(out);
			out.push('</div>');
		}
	}
	
	out.push('</div></div>');
}