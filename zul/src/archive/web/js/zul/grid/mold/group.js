/* group.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 10:15:18     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<tr', this.domAttrs_(), '>');
	var	zcls = this.getZclass(),
		overflow = !this.getGrid().isSizedByContent() ? 'z-overflow-hidden' : '' ;
	for (var j = 0, w = this.firstChild; w; w = w.nextSibling, j++)
		this.encloseChildHTML_({child:w, index: j, zclass: zcls, cls: overflow, out: out});
	out.push('</tr>');	
}