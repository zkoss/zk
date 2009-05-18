/* detail.js

	Purpose:
		
	Description:
		
	History:
		Mon May 18 11:49:27     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var	zcls = this.getZclass();
	
	out.push('<div', this.domAttrs_(), '><div id="', this.uuid, '$img" class="', 
	zcls, '-img"></div><div id="', this.uuid, '$cave" style="', this.getContentStyle() || '',
		';display:none;" class="', this.getContentSclass() || '', '">');
	
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></div>');	
}
