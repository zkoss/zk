/* breadcrumbitem.js

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:15:22 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function breadcrumbitem$mold$(out) {
	// aria-current="page" on the last item is layered on by the za11y add-on (EE).
	out.push('<li', this.domAttrs_(), '>');
	if (this._href && !this._disabled) {
		out.push('<a href="', zUtl.encodeXMLAttribute(this._href), '"');
		if (this._target) {
			// rel=noopener noreferrer when navigating to a separate browsing
			// context closes the reverse-tabnabbing hole: without it, the new
			// window can read/manipulate window.opener and redirect the
			// originating page to a phishing clone.
			out.push(' target="', zUtl.encodeXMLAttribute(this._target),
				'" rel="noopener noreferrer"');
		}
		out.push('>', this.domContent_(), '</a>');
	} else {
		// tabindex="-1" keeps the current-page / disabled item OUT of the
		// natural Tab order but lets Breadcrumb's arrow-key handler land
		// focus here via .focus(). Without this, End would skip the
		// current page entirely.
		out.push('<span tabindex="-1">', this.domContent_(), '</span>');
	}
	out.push('</li>');
}
