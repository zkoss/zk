/* auxheader.js

	Purpose:
		
	Description:
		
	History:
		Mon May  4 17:15:50     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<th', this.domAttrs_(), '><div id="', this.uuid, '$cave" class="',
	this.getZclass(), '-cnt"', this.domTextStyleAttr_(), '>', this.domContent_());
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></td>');
}
