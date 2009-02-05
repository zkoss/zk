/* tabbox-accd.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:27:49 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	out.push('<div ', this.domAttrs_(), '>');
	var tps = this.getTabpanels();
	if (tps) {
		tps.redraw(out);
	}
	out.push("</div>");
}