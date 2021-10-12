/* a.js

	Purpose:
		
	Description:
		
	History:
		Thu Aug  6 15:21:48     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<a ', this.domAttrs_(), '>', this.domContent_());

	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</a>');
}
