/* groupbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:47:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {	
	var	zcls = this.getZclass(),
	uuid = this.uuid,
	cap = this.caption,
	title = this.getTitle();
	title = title && !cap ? zUtl.encodeXML(title) :  null;
	
	out.push('<div', this.domAttrs_(), '>');
	
	if (title || cap) {
		out.push('<div class="', zcls, '-header',(this._closable? '': ' ' + zcls + '-readonly'),'">');
		if (cap)
			cap.redraw(out);
		else
			out.push('<div id="', uuid,'-title" class="', zcls, 
					'-title"><span class="', zcls, '-title-cnt">', title, '</span></div>');
		out.push('</div>');
	}
	
	this._redrawCave(out, skipper);
	
	// classicblue is deprecated and 
	// shadow not used in breeze, sapphire and silvertail,
	out.push('</div>');
}