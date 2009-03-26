/* menuitem.js

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
		out.push('<td align="left"', this.domAttrs_(), '><a href="',
				this.getHref() ? this.getHref() : 'javascript:;', '"');
		if (this.getTarget())
			out.push(' target="', this.getTarget(), '"');
		out.push(' class="', zcls, '-cnt"><table id="', uuid, '$a"', zUtl.cellps0,
				' class="', zcls, '-body');
		if (this.getImage()) {
			out.push(' ', zcls, '-body');
			if (this.getLabel())
				out.push('-text');

			out.push('-img');
		}
		out.push('" style="width: auto;"><tbody><tr><td class="', zcls,
				'-inner-l"><span class="', zcls, '-space"></span></td><td class="', zcls,
				'-inner-m"><div><button id="', uuid,
				'$b" type="button" class="', zcls, '-btn"');
		if (this.getImage())
			out.push(' style="background-image:url(', this.getImage(), ')"');

		out.push('>', zUtl.encodeXML(this.getLabel()), '&nbsp;</button></div></td><td class="',
					zcls, '-inner-r"><span class="', zcls, '-space"></span></td></tr></tbody></table></a></td>');
	} else {
		out.push('<li', this.domAttrs_(), '>');
		var cls = zcls + '-cnt' +
				(!this.getImage() && this.isCheckmark() ?
						' ' + zcls + (this.isChecked() ? '-cnt-ck' : '-cnt-unck') : '');
		out.push('<a href="', this.getHref() ? this.getHref() : 'javascript:;', '"');
		if (this.getTarget())
			out.push(' target="', this.getTarget(), '"');
		out.push(' id="', uuid, '$a" class="', cls, '">', this.domContent_(), '</a></li>');
	}
}
