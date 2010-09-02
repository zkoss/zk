/* button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:43     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass(),
		tabi = this._tabindex,
		uuid = this.uuid;
	tabi = tabi ? ' tabindex="' + tabi + '"': '';

	var btn = '<button type="' + this._type + '" id="' + uuid + '-btn" class="' + zcls + '"',
		s = this.isDisabled();
	if (s) btn += ' disabled="disabled"';
	if (tabi && (zk.gecko || zk.safari)) btn += tabi;
	btn += '></button>';

	var wd = "100%", hgh = "100%";
	if (zk.ie && !zk.ie8) {
		//Not to generate 100% in IE6/7 (or the width will be 100%)
		if (!this._width) wd = "";
		if (!this._height) hgh = "";
	}
	out.push('<span', this.domAttrs_(),
			'><table id="', uuid, '-box" style="width:', wd, ';height:', hgh, '"', zUtl.cellps0,
			(tabi && !zk.gecko && !zk.safari ? tabi : ''),
			'><tr><td class="', zcls, '-tl">', (!zk.ie ? btn : ''),
			'</td><td class="', zcls, '-tm"></td>', '<td class="', zcls,
			'-tr"></td></tr>', '<tr><td class="', zcls, '-cl">',
			(zk.ie ? btn : ''),
			'</td><td class="', zcls, '-cm">', this.domContent_(),
			'</td><td class="', zcls, '-cr"><div></div></td></tr>',
			'<tr><td class="', zcls, '-bl"></td>',
			'<td class="', zcls, '-bm"></td>',
			'<td class="', zcls, '-br"></td></tr></table></span>');
}