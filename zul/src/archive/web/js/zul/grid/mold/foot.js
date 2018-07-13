/* foot.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 12:27:11     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<tr', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	
	var grid = this.getGrid();
	if (grid._nativebar && !grid.frozen)
		out.push('<td class="', this.$s('bar'), '" />');
	
	out.push('</tr>');
}
