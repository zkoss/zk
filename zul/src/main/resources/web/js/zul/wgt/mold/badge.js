/* badge.js

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:15:12 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function badge$mold$(out) {
	out.push('<span', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling) {
		w.redraw(out);
	}
	if (this._shouldRenderIndicator()) {
		// role=status + contextual aria-label are layered on by the za11y
		// add-on (EE); the CE mold emits the bare visual indicator only.
		out.push('<span class="', this.$s('indicator'), '">',
			zUtl.encodeXML(this._indicatorText()), '</span>');
	}
	out.push('</span>');
}
