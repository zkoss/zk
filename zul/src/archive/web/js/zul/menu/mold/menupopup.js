/* menupopup.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:06     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		tags = zk.ie || zk.gecko ? 'a' : 'button';
	out.push('<div', this.domAttrs_(), '><', tags, ' id="', uuid,
			'-a" tabindex="-1" onclick="return false;" href="javascript:;"',
			' class="z-focus-a"></',
			tags, '><div class="', this.$s('separator') ,'"></div><ul class="', this.$s('content'), '" id="', uuid, '-cave">');

	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</ul></div>');
}
