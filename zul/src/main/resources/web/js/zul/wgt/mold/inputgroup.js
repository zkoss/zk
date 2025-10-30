/* inputgroup.js

		Purpose:
                
		Description:
                
		History:
				Thu Mar 07 16:51:03 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

*/
function redraw(out) {
	out.push('<div ', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling) {
		this.encloseChildHTML_({out: out, child: w});
	}
	out.push('</div>');
}