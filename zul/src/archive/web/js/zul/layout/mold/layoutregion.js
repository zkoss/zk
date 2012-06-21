/* layoutregion.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:44:56     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		noCenter = this.getPosition() != zul.layout.Borderlayout.CENTER,
		pzcls = this.parent.getZclass();
	out.push('<div id="', uuid,  '">', '<div id="', uuid, '-real"',
			this.domAttrs_({id: 1}), '>');
	
	this.titleRenderer_(out);
	out.push('<div id="', uuid, '-cave" class="', zcls, '-body">');
	
	var firstChild = this.getFirstChild();
	if (firstChild)
		firstChild.redraw(out);
	
	out.push('</div></div>');
	
	if (noCenter) {
		out.push('<div id="', uuid, '-split" class="', zcls, '-splt"><span id="'
			, uuid, '-splitbtn" class="', zcls, '-splt-btn"');
		if (!this._collapsible)
			out.push(' style="display:none;"');
		out.push('></span></div>', '<div id="', uuid, '-colled" class="', zcls,
				'-colpsd" style="display:none"><div id="',
				uuid, '-btned" class="', pzcls, '-icon ', zcls, '-exp"');
		if (!this._collapsible)
			out.push(' style="display:none;"');
				
		out.push('><div class="', pzcls, '-icon-img"></div></div></div>');
	}
	out.push('</div>');
}