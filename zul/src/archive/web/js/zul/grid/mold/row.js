/* row.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:26:53     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<tr', this.domAttrs_(), '>');
	var	zcls = this.getZclass();
	for (var j = 0, w = this.firstChild; w; w = w.nextSibling, j++)
		this.encloseChildHTML_({child:w, index: j, zclass: zcls, cls: 'z-overflow-hidden', out: out});
	out.push('</tr>');	
}
