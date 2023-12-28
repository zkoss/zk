/* combobutton.js

	Purpose:
		
	Description:
		
	History:
		Mon June 16 12:11:07     2013, Created by huangnoah

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function combobutton$mold$(out) {
	var tabi = this._tabindex | 0,
		uuid = this.uuid;

	out.push('<span ', this.domAttrs_({tabindex: 1}));//we have a default 0 for tabindex of this element

	if (this._disabled) {
		out.push(' disabled="disabled" aria-disabled="true"');
	} else {
		out.push(' tabindex="', /*safe*/ tabi, '"');
	}
	out.push(' ><span id="', uuid, '-real" class="', this.$s('content'), '" role="none"');

	out.push('>', this.domContent_(),
		'<span id="', uuid, '-btn" class="', this.$s('button'), '" role="none">',
		'<i id="', uuid, '-icon" class="', this.$s('icon'), ' z-icon-caret-down" aria-hidden="true"></i>',
		'</span></span>');
	// pp
	if (this.firstChild)
		this.firstChild.redraw(out);

	out.push('</span>');
}