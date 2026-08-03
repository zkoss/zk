/* avatargroup.js

        Purpose:

        Description:

        History:
                Wed May 13 13:11:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function avatargroup$mold$(out) {
	// role="group" (so AT reads the stacked avatars as one composite set) is
	// layered on by the za11y add-on (EE). A group name, when wanted, is
	// supplied by the application via the client/attribute namespace
	// (ca:aria-label).
	out.push('<span', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</span>');
}
