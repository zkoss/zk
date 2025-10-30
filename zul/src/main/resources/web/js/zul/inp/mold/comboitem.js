/* comboitem.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:59:06     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function comboitem$mold$(out) {
	out.push('<li', this.domAttrs_({text: true}), '>');

	const iconHTML = this.domIcon_(), imgHTML = this.domImage_();
	if (imgHTML) {
		if (iconHTML) {
			out.push('<span class="', this.$s('image'), '" aria-hidden="true">', imgHTML, '</span><span class="',
				this.$s('icon'), '" aria-hidden="true">', iconHTML, '</span><span class="',
				this.$s('text'), '">', this.domLabel_());
		} else {
			out.push('<span class="',
				this.$s('image'), '" aria-hidden="true">', imgHTML, '</span><span class="',
				this.$s('text'), '">', this.domLabel_());
		}
	} else {
		if (iconHTML) {
			out.push('<span class="',
				this.$s('icon'), '" aria-hidden="true">', iconHTML, '</span><span class="',
				this.$s('text'), '">', this.domLabel_());
		} else {
			// if no image specified, we still output the image for backward compatibility.
			out.push('<span class="',
				this.$s('image'), '" aria-hidden="true">', imgHTML, '</span><span class="',
				this.$s('text'), '">', this.domLabel_());
		}
	}


	var v;
	if ((v = this._description))
		out.push('<br/><span class="', this.$s('inner'), '">',
			zUtl.encodeXML(v), '</span>');
	if ((v = this._content))
		out.push('<span class="', this.$s('content'), '">', DOMPurify.sanitize(v), '</span>');

	out.push('</span></li>');
}
