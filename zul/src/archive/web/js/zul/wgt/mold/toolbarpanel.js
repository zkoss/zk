/* toolbarpanel.js

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<div ', this.domAttrs_(), '>', '<table id="', this.uuid,
				'-cave" class="', this.$s('content'), ' ', this.$s(this.getAlign()),
				'"', zUtl.cellps0, '><tbody>');
	if ('vertical' != this.getOrient()) {
		out.push("<tr>");
		for (var w = this.firstChild; w; w = w.nextSibling) {
			out.push('<td class="', this.$s('horizontal'), '">');
			w.redraw(out);
			out.push("</td>");
		}
		out.push("</tr>");
	} else {
		for (var w = this.firstChild; w; w = w.nextSibling) {
			out.push('<tr><td class="', this.$s('vertical'), '">');
			w.redraw(out);
			out.push('</td></tr>');
		}
	}
	out.push('</tbody></table><div class="z-clear"></div></div>');
}
