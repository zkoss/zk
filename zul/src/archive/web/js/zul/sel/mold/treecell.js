/* treecell.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:31:54     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {
	out.push('<td', this.domAttrs_(), '><div id="', this.uuid,
		'-cave" class="', this.getZclass() + '-cnt');

	var tree = this.getTree();
	if (tree != null && !tree.isSizedByContent())
		out.push(' z-overflow-hidden');

	out.push('"', this.domTextStyleAttr_(), '>', this.domContent_());

	if (!skipper)
    	for (var w = this.firstChild; w; w = w.nextSibling)
    		w.redraw(out);

	out.push('</div></td>');
}
