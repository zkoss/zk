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
		caption = this.caption;

	out.push('<div', this.domAttrs_(), '>');
	if (caption || title) {
		out.push('<div id="', uuid, '-head" class="', this.$s('head'), '">', 
				'<div id="', uuid, '-cap" class="', this.$s('header'), '">');
		if (caption) caption.redraw(out);
		else {
			var	icon = this.$s('icon'),
				getIcon = function(iconClass) {
					return '<i class="z-icon-' + iconClass + '"></i>';
				};
			
			if (this._closable) {
				out.push('<div id="', uuid , '-close" class="', icon, ' ',
						this.$s('close'), '">' , getIcon('remove') ,  '</div>');
			}
			if (this._maximizable) {
				var maxd = this._maximized;
				out.push('<div id="', uuid , '-max" class="', icon, ' ',
						this.$s('maximize'));
				if (maxd)
					out.push(' ', this.$s('maximized'));
				out.push('">', maxd ? getIcon('resize-small') : getIcon('fullscreen'),
						'</div>');
			}
			if (this._minimizable) {
				out.push('<div id="', uuid , '-min" class="', icon, ' ',
						this.$s('minimize'), '" >', getIcon('minus'), '</div>');
			}
			if (this._collapsible) {
				out.push('<div id="', uuid , '-exp" class="', icon, ' ',
						this.$s('expand'), '" >',
						this._open ? getIcon('caret-up') : getIcon('caret-down'),
						'</div>');
			}
			out.push(zUtl.encodeXML(title));
		}
		out.push('</div></div>');
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