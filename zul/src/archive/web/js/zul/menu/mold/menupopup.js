/* menupopup.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:06     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		tags = zk.ie || zk.gecko ? 'a' : 'button';
	out.push('<div', this.domAttrs_(), '><', tags, ' id="', uuid,
			'-a" tabindex="-1" onclick="return false;" href="javascript:;"',
			' style="padding:0 !important; margin:0 !important; border:0 !important;',
			' background: transparent !important; font-size: 1px !important;',
			' width: 1px !important; height: 1px !important;-moz-outline: 0 none;',
			' outline: 0 none;	-moz-user-select: text; -khtml-user-select: text;"></',
			tags, '><ul class="', zcls, '-cnt" id="', uuid, '-cave">');

	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</ul></div>');
}
