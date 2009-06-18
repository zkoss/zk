/* popup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 19:16:12     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass();
    out.push('<div', this.domAttrs_(), '><div class="', zcls, '-tl"><div class="',
			 zcls, '-tr"></div></div>', '<div id="', this.uuid, '-body" class="',
			  zcls, '-cl"><div class="', zcls, '-cr"><div class="', zcls, '-cm">',
			   '<div id="', this.uuid, '-cave" class="', zcls, '-cnt">');
	this.prologHTML_(out);
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	this.epilogHTML_(out);
	out.push('</div></div></div></div><div class="', zcls, '-bl"><div class="',
			zcls, '-br"></div></div></div>');
}