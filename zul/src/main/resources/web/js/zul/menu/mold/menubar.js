/* menubar.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:04     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function menubar$mold$(out) {
	let w;
	const uuid = this.uuid;
	if ('vertical' === this.getOrient()) {
		out.push('<div', this.domAttrs_(), ' role="menubar" aria-orientation="vertical"><ul id="', uuid, '-cave" role="none">');
		for (w = this.firstChild; w; w = w.nextSibling)
			this.encloseChildHTML_({out: out, child: w, vertical: true});
		out.push('</ul></div>');
	} else {
		var scrollable;
		out.push('<div', this.domAttrs_(), ' role="menubar">');
		if ((scrollable = this.checkScrollable())) {
			const scrollableClsHTML = this.$s('scrollable'),
				scrollIconHTML = this.$s('icon');

			out.push('<div id="', uuid, '-left" class="', this.$s('left'), ' ',
				scrollableClsHTML, '" aria-hidden="true"><i class="', scrollIconHTML,
				' z-icon-chevron-left"></i></div>',
				'<div id="', uuid, '-right" class="', this.$s('right'), ' ',
				scrollableClsHTML, '" aria-hidden="true"><i class="', scrollIconHTML,
				' z-icon-chevron-right"></i></div>',
				'<div id="', uuid, '-body" class="', this.$s('body'), '" role="none">',
				'<div id="', uuid, '-cnt" class="', this.$s('content'), '" role="none">');
		}
		out.push('<ul id="', uuid, '-cave" role="none">');
		for (w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</ul>');
		if (scrollable)
			out.push('</div></div>');
		out.push('</div>');
	}
}