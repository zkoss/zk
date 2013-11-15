/* toolbar.js

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	// ZK-1706: the width of empty space does not always equal 3px in ie9, ie10 
	var zcls = this.getZclass(),
		space = 'vertical' != this.getOrient() ? (zk.ie9 ? '<span></span>' : '') : '<br/>';
		
	out.push('<div ', this.domAttrs_(), '>', '<div id="', this.uuid, '-cave"',
				' class="', zcls, "-body ", zcls, '-', this.getAlign(), '" >');
	
	for (var w = this.firstChild; w; w = w.nextSibling) {
		out.push(space);
		w.redraw(out);
	}
	out.push('</div><div class="z-clear"></div></div>');
}