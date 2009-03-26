/* menu.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:05     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();

	if (this.isTopmost()) {
		out.push('<td align="left"', this.domAttrs_(), '><table id="', uuid,
				'$a"', zUtl.cellps0, ' class="', zcls, '-body');

		if (this.getImage()) {
			out.push(' ', zcls, '-body');
			if (this.getLabel())
				out.push('-text');

			out.push('-img');
		}

		out.push('" style="width: auto;"><tbody><tr><td class="', zcls,
				'-inner-l"><span class="', zcls, '-space"></span></td></td><td class="', zcls,
				'-inner-m"><div><button id="', uuid,
				'$b" type="button" class="', zcls, '-btn"');
		if (this.getImage())
			out.push(' style="background-image:url(', this.getImage(), ')"');

		out.push('>', zUtl.encodeXML(this.getLabel()), '&nbsp;</button>');

		if (this.menupopup) this.menupopup.redraw(out);

		out.push('</div></td><td class="', zcls, '-inner-r"><span class="', zcls, '-space"></span></td></tr></tbody></table></td>');

	} else {
		out.push('<li', this.domAttrs_(), '><a href="javascript:;" id="', uuid,
				'$a" class="', zcls, '-cnt ', zcls, '-cnt-img">', this.domContent_(), '</a>');
		if (this.menupopup) this.menupopup.redraw(out);

		out.push('</li>');
	}
}
