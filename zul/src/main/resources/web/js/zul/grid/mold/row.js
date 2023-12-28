/* row.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:26:53     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function row$mold$(out) {
	out.push('<tr', this.domAttrs_(), '>');
	var /*safe*/ zcls = this.getZclass(),
		grid = this.getGrid(),
		head = grid.getHeadWidget();
	let h;
	if (head)
		h = head.firstChild;
	for (var j = 0, w = this.firstChild; w; w = w.nextSibling, j++) {
		var opts = {child: w, index: j, zclass: zcls, out: out};
		if (h)
			opts.visible = h.isVisible();
		this.encloseChildHTML_(opts);
		if (h)
			h = h.nextSibling;
	}
	out.push('</tr>');
}
