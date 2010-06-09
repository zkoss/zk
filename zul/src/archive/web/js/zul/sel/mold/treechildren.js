/* treechildren.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:31:55     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	if (this.parent.$instanceof(zul.sel.Tree)) {
		out.push('<tbody id="',this.parent.uuid,'-rows" ', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</tbody>');
	} else
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
}
