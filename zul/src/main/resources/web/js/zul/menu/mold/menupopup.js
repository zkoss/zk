/* menupopup.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:06     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function menupopup$mold$(out) {
	var uuid = this.uuid;
	out.push('<div', this.domAttrs_(), ' role="menu"><div id="', uuid,
		'-a" tabindex="-1" class="z-focus-a" aria-hidden="true"></div><div class="',
		this.$s('separator'), '"></div><ul role="none" class="', this.$s('content'), '" id="', uuid, '-cave">');

	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</ul></div>');
}
