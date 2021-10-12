/* listcell.js

	Purpose:
		
	Description:
		
	History:
		Mon May  4 19:30:59     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<td', this.domAttrs_(), '><div id="', this.uuid,
		'-cave" class="', this.$s('content'), '"', 
		this.domTextStyleAttr_(), '>', this.domContent_());
	
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	
	out.push('</div></td>');
}
