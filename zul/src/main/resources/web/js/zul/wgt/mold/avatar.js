/* avatar.js

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:15:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function avatar$mold$(out) {
	// After a runtime load failure (bind_'s onerror sets _imgError) fall
	// through to the initials/icon branch so domContent_ renders the
	// fallback — otherwise the rerender re-emits the same broken <img> and
	// the documented image→icon→label chain never advances past the glyph.
	// The image's native alt is kept; role="img"/aria-label for initials/icon
	// mode is layered on by the za11y add-on (EE).
	var img = this._image && !this._imgError ? this._image : undefined;
	if (img) {
		var altHtml = zUtl.encodeXMLAttribute(this.getLabel() || '');
		out.push('<span', this.domAttrs_(), '><img src="',
				zUtl.encodeXMLAttribute(img),
				'" alt="', altHtml, '"></span>');
	} else {
		out.push('<span', this.domAttrs_(), '>',
				this.domContent_(), '</span>');
	}
}
