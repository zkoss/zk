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
	var zcls = this.getZclass(),
		uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption;
		
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
			
			if (this._closable)
				out.push('<div id="', uuid , '-close" class="', this.$s('icon-img'), ' ', this.$s('close'), '">' , getIcon('icon-remove') ,  '</div>');
			if (this._maximizable) {
				out.push('<div id="', uuid , '-maximize" class="', this.$s('icon-img'), ' ', this.$s('maximize'));
				if (this._maximized)
					out.push(' ', this.$s('maximized'));
				out.push('">', this._maximized ? getIcon('icon-resize-small') : getIcon('icon-fullscreen') , '</div>');
			}
			if (this._minimizable)
				out.push('<div id="', uuid , '-minimize" class="', this.$s('icon-img'), ' ',  this.$s('minimize'), '" >', getIcon('icon-minus'), '</div>');
			if (this._collapsible)
				out.push('<div id="', uuid , '-expand" class="', this.$s('icon-img'), ' ',  this.$s('expand'), '" >', this._collapsible ? getIcon('icon-caret-up z-icon-large') : getIcon('icon-caret-down'), '</div>');
			out.push(zUtl.encodeXML(title));
		} 
		
		out.push('</div>');
	} 
	
	out.push('</div><div id="', uuid, '-body" class="', this.$s('body'), '"');
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
			out.push('<div id="', uuid, '-bb" class="', this.$s('btm'), '">');
			this.bbar.redraw(out);
			out.push('</div>');
		}
		
		if (this.fbar) {
			out.push('<div id="', uuid, '-fb" class="', this.$s('btm2'), '">');
			this.fbar.redraw(out);
			out.push('</div>');
		}
	}
	
	out.push('</div></div>');
}