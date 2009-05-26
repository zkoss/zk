/* tablechildren.js

	Purpose:
		
	Description:
		
	History:
		Thu May 19 14:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<td', this.domAttrs_()+' ');
	var v;
	if((v = this.getRowspan())!=1)
		out.push(' rowspan="',v,'"');
	if((v = this.getColspan())!=1)
		out.push(' colspan="',v,'"');
	out.push('>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</td>');
}
