/* layout.js

	Purpose:
		
	Description:
		
	History:
		Fri Aug  6 11:54:41 TST 2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
    out.push('<div ', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		this.encloseChildHTML_(w, out);
    out.push('</div>');
}