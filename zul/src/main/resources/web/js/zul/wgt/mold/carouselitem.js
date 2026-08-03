/* carouselitem.js

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:15:34 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function carouselitem$mold$(out) {
	// Per slide role="group" + aria-roledescription="slide" + the positional
	// "Slide N of M" aria-label are layered on by the za11y add-on (EE). The
	// active highlight class is added client-side by Carousel._applyActiveClass()
	// in bind_, so domAttrs_ here emits the full class (zclass + user sclass).
	out.push('<div', this.domAttrs_(), '>');
	var label = this.getLabel(),
		imgHtml = this.domImage_();
	if (imgHtml) out.push(imgHtml);
	if (label) out.push('<div class="', this.getZclass(), '-label">',
		zUtl.encodeXML(label), '</div>');
	for (var c = this.firstChild; c; c = c.nextSibling) {
		c.redraw(out);
	}
	out.push('</div>');
}
