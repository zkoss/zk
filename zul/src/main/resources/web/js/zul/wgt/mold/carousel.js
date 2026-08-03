/* carousel.js

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:15:29 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function carousel$mold$(out) {
	var uuid = this.uuid,
		zclsHtml = this.getZclass(),
		count = 0;
	out.push('<div', this.domAttrs_(), '>');
	out.push('<div id="', uuid, '-track" class="', zclsHtml, '-track">');
	for (var w = this.firstChild; w; w = w.nextSibling) {
		w.redraw(out);
		count++;
	}
	out.push('</div>');
	if (this._showArrows && count > 1) {
		// The visible glyph (‹/›) is CSS-rendered via ::before. ARIA labelling
		// of these controls is layered on by the za11y add-on (EE).
		out.push('<button type="button" id="', uuid, '-prev" class="', zclsHtml, '-arrow ', zclsHtml,
			'-arrow-prev"><span>&#x2039;</span></button>');
		out.push('<button type="button" id="', uuid, '-next" class="', zclsHtml, '-arrow ', zclsHtml,
			'-arrow-next"><span>&#x203A;</span></button>');
	}
	if (this._showIndicators && count > 1) {
		// Slide-picker dots. The -indicator-active class tracks the displayed
		// slide (kept in lockstep by _applyActiveClass); aria roles/labels and
		// aria-current are layered on by the za11y add-on (EE).
		out.push('<div id="', uuid, '-indicators" class="', zclsHtml, '-indicators">');
		for (var i = 0; i < count; i++) {
			var isActive = i === this._activeIndex;
			out.push('<button type="button"',
				' class="', zclsHtml, '-indicator',
				(isActive ? ' ' + zclsHtml + '-indicator-active' : ''),
				'" data-index="', /*safe*/i, '"></button>');
		}
		out.push('</div>');
	}
	// Visually-hidden announcer node. The za11y add-on (EE) turns it into a
	// live region and writes the "Slide N of M" status when activeIndex changes.
	out.push('<span id="', uuid, '-status" class="', zclsHtml, '-status"></span>');
	out.push('</div>');
}
