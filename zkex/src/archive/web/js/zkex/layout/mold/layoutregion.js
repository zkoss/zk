/* layoutregion.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:44:56     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		noCenter = this.getPosition() != zkex.layout.Borderlayout.CENTER,
		pzcls = this.parent.getZclass();
	out.push('<div id="', uuid,  '">', '<div id="', uuid, '$real"',
			this.domAttrs_({id: 1}), '>');
			
	if (this.getTitle()) {
		out.push('<div id="', uuid, '$cap" class="', zcls, '-header">');
		if (noCenter) {
			out.push('<div id="', uuid, '$btn" class="', pzcls,
					'-tool ', zcls, '-collapse"');
			if (!this.isCollapsible())
				out.push(' style="display:none;"');
			out.push('></div>');
		}
		out.push(zUtl.encodeXML(this.getTitle()), '</div>');
	}
	out.push('<div id="', uuid, '$cave" class="', zcls, '-body">');
	
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	
	out.push('</div></div>');
	
	if (noCenter) {
		out.push('<div id="', uuid, '$split" class="', zcls, '-split"></div>');
		if (this.getTitle()) {
			out.push('<div id="', uuid, '$colled" class="', zcls,
					'-collapsed" style="display:none"><div id="',
					uuid, '$btned" class="', pzcls, '-tool ', zcls, '-expand"');
			if (!this.isCollapsible())
				out.push(' style="display:none;"');
				
			out.push('></div></div>');
		}
	}
	out.push('</div>');
}