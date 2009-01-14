/* panel.js

	Purpose:
		
	Description:
		
	History:
		Mon Jan 12 18:31:46     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {
	var zcls = this.getZclass(),
		uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption,
		framable = this.isFramable(),
		noborder = this.getBorder() != 'normal';
		
	out.push('<div', this.domAttrs_(), '>');
	
	if (framable) {
		out.push('<div class="', zcls, '-tl');
		if (!caption && !title)
			out.push(' ', zcls, '-noheader');
		out.push('"><div class="', zcls, '-tr"><div class="', zcls, '-tm">');
	}
	if (caption || title) {
		out.push('<div id="', uuid, '$cap" class="', zcls, '-header');
		
		if (!framable && noborder)
			out.push(' ', zcls, '-header-noborder');
			
		out.push('">');
		if (!caption) {
			if (this.isClosable())
				out.push('<div id="', uuid, '$close" class="', zcls, '-tool ',
						zcls, '-close"></div>');
			if (this.isMaximizable()) {
				out.push('<div id="', uuid, '$max" class="', zcls, '-tool ',
						zcls, '-maximize');
				if (this.isMaximized())
					out.push(' ', zcls, '-maximized');
				out.push('"></div>');
			}
			if (this.isMinimizable())
				out.push('<div id="', uuid, '$min" class="', zcls, '-tool ',
						zcls, '-minimize"></div>');
			if (this.isCollapsible())
				out.push('<div id="', uuid, '$toggle" class="', zcls, '-tool ',
						zcls, '-toggle"></div>');
			out.push(zUtl.encodeXML(title));
		} else caption.redraw(out);
		
		out.push('</div>');
	}
	if (framable) out.push('</div></div></div>');
	
	out.push('<div id="', uuid, '$body" class="', zcls, '-body"');
	
	if (!this.isOpen()) out.push(' style="display:none;"');
	
	out.push('>');
	
	if (framable) 
		out.push('<div class="', zcls, '-cl"><div class="', zcls,
				'-cr"><div class="', zcls, '-cm">');
	if (this.tbar) {
		out.push('<div id="', uuid, '$tbar" class="', zcls, '-tbar');
		
		if (noborder)
			out.push(' ', zcls, '-tbar-noborder');
		
		if (framable && !caption && !title)
			out.push(' ', zcls, '-noheader');
		
		out.push('">');
		this.tbar.redraw(out);
		out.push('</div>');
	}
	if (this.panelchildren)
		this.panelchildren.redraw(out);
		
	if (this.bbar) {
		out.push('<div id="', uuid, '$bbar" class="', zcls, '-bbar');
		
		if (noborder)
			out.push(' ', zcls, '-bbar-noborder');
			
		if (framable && !caption && !title)
			out.push(' ', zcls, '-noheader');
		
		out.push('">');
		this.bbar.redraw(out);
		out.push('</div>');
	}
	
	if (framable) {
		out.push('</div></div></div><div class="', zcls, '-bl');
		
		if (!this.fbar) out.push(' ', zcls, '-nofbar');
		
		out.push('"><div class="', zcls, '-br"><div class="', zcls, '-bm">');
	}
	if (this.fbar) {
		out.push('<div id="', uuid, '$fbar" class="', zcls, '-fbar">');
		this.fbar.redraw(out);
		out.push('</div>');
	}
	if (framable) out.push('</div></div></div>');
	out.push('</div></div>');
}