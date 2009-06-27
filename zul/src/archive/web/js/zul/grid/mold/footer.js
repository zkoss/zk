/* footer.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 12:27:10     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<td', this.domAttrs_(), '><div id="', this.uuid,
		'-cave" class="', this.getZclass(), '">', this.domContent_());
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></td>');
}
