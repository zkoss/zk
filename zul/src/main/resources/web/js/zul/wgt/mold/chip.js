/* chip.js

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:16:16 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function chip$mold$(out) {
	out.push('<span', this.domAttrs_(), '>', this.domContent_());
	if (this._closable) {
		// Real <button> instead of <i role=button>: gets native keyboard
		// activation (Enter/Space), correct disabled-propagation when the
		// chip is disabled, default focus ring, and high-contrast outline.
		// type="button" prevents form submission when a chip is rendered
		// inside a <form>. The icon-font glyph lives in the child <i>; CSS
		// strips the button chrome (border/background/padding) in
		// confirmpopup/chip.less so it still fits the chip's compact layout.
		// The close button's contextual aria-label ("Remove <label>") is
		// layered on by the za11y add-on (EE).
		var disabledHtml = this._disabled ? ' disabled' : '';
		out.push('<button type="button" id="', this.uuid, '-close" class="',
			this.$s('close'), '"', disabledHtml,
			'><i class="z-icon-times"></i></button>');
	}
	out.push('</span>');
}
