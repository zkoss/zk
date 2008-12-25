/* button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:43     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass(),
		tabi = this._tabindex;
	tabi = tabi >= 0 ? ' tabindex="' + tabi + '"': '';

	out.push('<span', this.domAttrs_({style:1,domclass:1}), ' class="', zcls, '"');
	if (!this.isVisible()) out.push(' style="display:none"');
	out.push('><table id="', this.uuid, '$box"', zUtl.cellps0);
	if (tabi && !zk.gecko && !zk.safari) out.push(tabi);
	var s = this.domStyle_();
	if (s) out.push(' style="', s, '"');
	s = this.domClass_();
	if (s) out.push(' class="', s, '"');

	var btn = '<button id="' + this.uuid + '$btn" class="' + zcls + '"',
	s = this.isDisabled();
	if (s) btn += ' disabled="disabled"';
	if (tabi && (zk.gecko || zk.safari)) btn += tabi;
	btn += '></button>';

	out.push('><tr><td class="', zcls, '-tl">');
	if (!zk.ie) out.push(btn);
	out.push('</td><td class="', zcls, '-tm"></td>', '<td class="', zcls,
			'-tr"></td></tr>');

	out.push('<tr><td class="', zcls, '-cl">');
	if (zk.ie) out.push(btn);
	out.push('</td><td class="', zcls, '-cm">', this.domContent_(),
			'</td><td class="', zcls, '-cr"><i class="', zcls,
			'"></i></td></tr>',
			'<tr><td class="', zcls, '-bl"></td>',
			'<td class="', zcls, '-bm"></td>',
			'<td class="', zcls, '-br"></td></tr></table></span>');
}