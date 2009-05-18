/* columnlayout.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass();
	out.push('<div', this.domAttrs_(), '><div id="',
	this.uuid,'$cave"', 'class="',zcls,'-inner">'); 
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div><div class="z-clear"></div></div>');
}
