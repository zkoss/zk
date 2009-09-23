/* select.js

	Purpose:
		
	Description:
		
	History:
		Mon Jun  1 16:44:09     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<select', this.domAttrs_(), '>');
	
	for (var w = this.firstChild; w; w = w.nextSibling)
		if (w.isVisible()) w.redraw(out);
		
	out.push('</select>');
}