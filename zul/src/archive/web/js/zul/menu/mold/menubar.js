/* menubar.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:04     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid;
	if ('vertical' == this.getOrient()) {
		out.push('<div', this.domAttrs_(), '><ul id="', uuid, '-cave">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			this.encloseChildHTML_({out: out, child: w, vertical: true});
		out.push('</ul></div>');
	} else {
		var scrollable;
		out.push('<div', this.domAttrs_(), '>')
		if (scrollable = this.checkScrollable()) {
			var scrollableCls = this.$s('scrollable'),
				scrollIcon = this.$s('icon');
				
			out.push('<div id="', uuid, '-left" class="', this.$s('left'), ' ',
						scrollableCls, '"><i class="', scrollIcon,
						' z-icon-caret-left"></i></div>',
					'<div id="', uuid, '-right" class="', this.$s('right'), ' ',
						scrollableCls, '"><i class="', scrollIcon,
						' z-icon-caret-right"></i></div>',
					'<div id="', uuid, '-body" class="', this.$s('body'), '">',
					'<div id="', uuid, '-cnt" class="', this.$s('content'), '">');
		}
		out.push('<ul id="', uuid, '-cave">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</ul>');
		if (scrollable)
			out.push('</div></div>');
		out.push('</div>');
	}
}