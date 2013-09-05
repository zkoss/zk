/* treefoot.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:31:56     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<tr', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	
	var tree = this.getTree();
	if (tree._nativebar)
		out.push('<td class="', this.$s('bar'), '" />');
	
	out.push('</tr>');
}
