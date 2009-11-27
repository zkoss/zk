/* menu.js

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
		contentType = this._contentType,
		picker = this.getPicker(),
		palette = this.getPalette();

	if (this.isTopmost()) {
		out.push('<td align="left"', this.domAttrs_(), '><table id="', uuid,
				'-a"', zUtl.cellps0, ' class="', zcls, '-body');

		if (this.getImage()) {
			out.push(' ', zcls, '-body');
			if (this.getLabel())
				out.push('-text');

			out.push('-img');
		}

		out.push('" style="width: auto;"><tbody><tr><td class="', zcls,
				'-inner-l"><span class="', zcls, '-space"></span></td><td class="', zcls,
				'-inner-m"><div><', btn, ' id="', uuid,
				'-b" type="button" class="', zcls, '-btn"');
		if (this.getImage())
			out.push(' style="background-image:url(', this.getImage(), ')"');

		out.push('>', zUtl.encodeXML(this.getLabel()), '&nbsp;</', btn, '>');

		if (this.menupopup)
			this.menupopup.redraw(out);
		else if (contentType) {
			switch (contentType) {
			case 'color':
				if (picker) {
					out.push('<div id="', uuid, '-picker-pp" class="', zcls, '-picker-pp" style="display:none">');
					picker.redraw(out);
					out.push('</div>');
				}
				if (palette) {
					out.push('<div id="', uuid, '-palette-pp" class="', zcls, '-palette-pp" style="display:none">');
					palette.redraw(out);
					out.push('</div>');
				}
				break;
			case 'content':
				out.push('<div id="', uuid, '-cnt-pp" class="', zcls, '-cnt-pp" style="display:none"><div class="', zcls,'-cnt-body">', this._content, '</div></div>');
			}
		}
		out.push('</div></td><td class="', zcls, '-inner-r"><span class="', zcls, '-space"></span></td></tr></tbody></table></td>');

	} else {
		out.push('<li', this.domAttrs_(), '><a href="javascript:;" id="', uuid,
				'-a" class="', zcls, '-cnt ', zcls, '-cnt-img">', this.domContent_(), '</a>');

		if (this.menupopup)
			this.menupopup.redraw(out);
		else if (contentType) {
			switch (contentType) {
			case 'color':
				if (picker) {
					out.push('<div id="', uuid, '-picker-pp" class="', zcls, '-picker-pp" style="display:none">');
					picker.redraw(out);
					out.push('</div>');
				}
				if (palette) {
					out.push('<div id="', uuid, '-palette-pp" class="', zcls, '-palette-pp" style="display:none">');
					palette.redraw(out);
					out.push('</div>');
				}
				break;
			case 'content':
				out.push('<div id="', uuid, '-cnt-pp" class="', zcls, '-cnt-pp" style="display:none"><div class="', zcls,'-cnt-body">', this._content, '</div></div>');
			}
		}
		out.push('</li>');
	}
}