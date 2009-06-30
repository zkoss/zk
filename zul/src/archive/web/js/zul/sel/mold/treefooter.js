/* treefooter.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:31:57     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<td', this.domAttrs_(), '><div id="', this.uuid,
		'-cave" class="', this.getZclass(), '">', this.domContent_());
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></td>');
}
