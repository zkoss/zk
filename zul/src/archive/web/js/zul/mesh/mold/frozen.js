/* frozen.js

	Purpose:
		
	Description:
		
	History:
		Wed Sep  2 10:07:10     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
		
	out.push('<div', this.domAttrs_(), '><div id="', uuid, '-cave" class="', zcls,
			'-body">');
	
	for (var j = 0, w = this.firstChild; w; w = w.nextSibling, j++)
		w.redraw(out);
		
	out.push('</div><div id="', uuid, '-scrollX" class="', zcls, '-inner"><div></div></div>',
			'<div class="z-clear"></div></div>');
}
