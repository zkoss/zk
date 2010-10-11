/* menuitem.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:05     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		btn = zk.ie && !zk.ie8 ? 'input' : 'button',
		target = this.getTarget(),
		img = this.getImage();

	if (this.isTopmost()) {
		out.push('<td align="left"', this.domAttrs_(), '><a href="',
				this.getHref() ? this.getHref() : 'javascript:;', '"');
		if (target)
			out.push(' target="', target, '"');
		out.push(' class="', zcls, '-cnt"><table id="', uuid, '-a"', zUtl.cellps0,
				' class="', zcls, '-body');
		if (img) {
			out.push(' ', zcls, '-body');
			if (this.getLabel())
				out.push('-text');

			out.push('-img');
		}
		out.push('" style="width: auto;"><tbody><tr><td class="', zcls,
				'-inner-l"><span class="', zcls, '-space"></span></td><td class="', zcls,
				'-inner-m"><div><', btn, ' id="', uuid,
				'-b" type="button" class="', zcls, '-btn"');
		if (img)
			out.push(' style="background-image:url(', img, ')"');

		out.push('>', zUtl.encodeXML(this.getLabel()), '&nbsp;</', btn, '></div></td><td class="',
					zcls, '-inner-r"><span class="', zcls, '-space"></span></td></tr></tbody></table></a></td>');
	} else {
		out.push('<li', this.domAttrs_(), '><div class="', zcls, '-cl"><div class="', zcls, '-cr"><div class="', zcls, '-cm">');
		var cls = zcls + '-cnt' +
				(!img && this.isCheckmark() ?
						' ' + zcls + (this.isChecked() ? '-cnt-ck' : '-cnt-unck') : ''),
			mold = this.getMold();
		
		out.push('<a href="', this.getHref() ? this.getHref() : 'javascript:;', '"');
		if (target)
			out.push(' target="', target, '"');
		out.push(' id="', uuid, '-a" class="', cls, '">', this.domContent_(), '</a></div></div></div></li>'); //ADDED
	}
}
