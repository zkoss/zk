/* auxhead.js

	Purpose:
		
	Description:
		
	History:
		Mon May  4 17:15:44     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<tr', this.domAttrs_(), ' style="text-align:left;" role="row">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	var mesh = this.getMeshWidget();
	if (mesh && mesh._nativebar)
		out.push('<th class="', this.$s('bar'), '" ></th>');
	out.push('</tr>');
}
