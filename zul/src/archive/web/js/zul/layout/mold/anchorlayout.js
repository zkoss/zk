/* anchorlayout.js

	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 13:14:17 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass();
	out.push('<div', this.domAttrs_(), '>',
				'<div id="',this.uuid, '-cave"', 'class="',zcls,'-body">'); 
				for (var w = this.firstChild; w; w = w.nextSibling)
					w.redraw(out);
		out.push('</div>',
			'</div>');
}