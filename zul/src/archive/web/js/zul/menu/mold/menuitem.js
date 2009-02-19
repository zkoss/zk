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
				' class="', zcls, '-btn');
		if (this.getImage()) {
			out.push(' ', zcls, '-btn');
			if (this.getLabel())
				out.push('-text');
			
			out.push('-img');			
		}
		out.push('" style="width: auto;"><tbody><tr><td class="', zcls,
				'-btn-l"><i>&nbsp;</i></td><td class="', zcls,
				'-btn-m"><em unselectable="on"><button id="', uuid,
				'$b" type="button" class="', zcls, '-btn-text"');
		if (this.getImage())
			out.push(' style="background-image:url(', this.getImage(), ')"');
			
		out.push('>', zUtl.encodeXML(this.getLabel()), '</button></em></td><td class="',
					zcls, '-btn-r"><i>&nbsp;</i></td></tr></tbody></table></a></td>');
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
