/* tabbox.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:27:40 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	out.push('<div ', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	if (this.isVertical())
		out.push('<div class="z-clear" ></div>');
	out.push("</div>");
}