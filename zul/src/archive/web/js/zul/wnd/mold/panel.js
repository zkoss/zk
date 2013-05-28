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
		caption = this.caption,
		isFrameRequired = zul.wnd.PanelRenderer.isFrameRequired(this),
		rounded = this._rounded(),
		noborder = !this._bordered(), //Unlike window, panel does not support other kind of borders
		noheader = !caption && !title;
		
	out.push('<div', this.domAttrs_(), '><div id="',
			uuid, '-header-outer" class="', zcls, '-header-outer">');
	if (caption || title) {
		out.push('<div id="',
				uuid, '-caption" class="', zcls, '-header">');
		if (caption) caption.redraw(out);
		else {
			var getIcon = function(iconClass) {
				return '<i class="' + zcls + '-icon z-' + iconClass + '"></i>';
			}
			
			if (this._closable)
				out.push('<div id="', uuid , '-close" class="', zcls, '-icon-img ', zcls, '-close">' , getIcon('icon-remove') ,  '</div>');
			if (this._maximizable) {
				out.push('<div id="', uuid , '-maximize" class="', zcls, '-icon-img ', zcls, '-maximize');
				if (this._maximized)
					out.push(' ', zcls, '-maximized');
				out.push('">', this._maximized ? getIcon('icon-resize-small') : getIcon('icon-fullscreen') , '</div>');
			}
			if (this._minimizable)
				out.push('<div id="', uuid , '-minimize" class="', zcls, '-icon-img ', zcls, '-minimize" >', getIcon('icon-minus'), '</div>');
			if (this._collapsible)
				out.push('<div id="', uuid , '-expand" class="', zcls, '-icon-img ', zcls, '-expand" >', this._collapsible ? getIcon('icon-caret-up') : getIcon('icon-caret-down'), '</div>');
			out.push(zUtl.encodeXML(title));
		} 
		
		out.push('</div>');
		
//		if (isFrameRequired) out.push('</div></div></div>');
	} //else if (rounded)
		//out.push('<div class="', zcls,'-tl ', zcls,'-tl-gray"><div class="' ,zcls ,'-tr ', zcls,'-tr-gray"></div></div>');
	
	out.push('</div><div id="', uuid, '-body" class="', zcls, '-body"');
	if (!this._open) 
		out.push(' style="display:none;"');
	out.push('>');
	
	if (!skipper) {
		if (rounded) {
			out.push('<div class="', zcls, '-cl"><div class="', zcls,
					'-cr"><div class="', zcls, '-cm');
			if (noheader) {
				out.push(' ', zcls, '-noheader');
			}
			out.push('">');		
		}
		if (this.tbar) {
			out.push('<div id="', uuid, '-tb" class="', zcls, '-top');
			
			if (noborder)
				out.push(' ', zcls, '-top-noborder');
			
			if (noheader)
				out.push(' ', zcls, '-noheader');
			
			out.push('">');
			this.tbar.redraw(out);
			out.push('</div>');
		}
		if (this.panelchildren)
			this.panelchildren.redraw(out);
			
		if (this.bbar) {
			out.push('<div id="', uuid, '-bb" class="', zcls, '-btm');
			
			if (noborder)
				out.push(' ', zcls, '-btm-noborder');
				
			if (noheader)
				out.push(' ', zcls, '-noheader');
			
			out.push('">');
			this.bbar.redraw(out);
			out.push('</div>');
		}
		
		if (rounded) {
			out.push('</div></div></div><div class="', zcls, '-fl');
			
			if (!this.fbar) out.push(' ', zcls, '-nobtm2');
			
			out.push('"><div class="', zcls, '-fr"><div class="', zcls, '-fm">');
		}
		if (this.fbar) {
			out.push('<div id="', uuid, '-fb" class="', zcls, '-btm2">');
			this.fbar.redraw(out);
			out.push('</div>');
		}
		if (rounded) 
			out.push('</div></div></div><div class="', zcls ,'-bl"><div class="', zcls ,'-br"></div></div>');
	} // end of uuid-body content
	out.push('</div></div>');
}