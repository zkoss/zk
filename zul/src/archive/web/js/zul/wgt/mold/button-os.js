/* button-os.js

	Purpose:
		Button's os mold
	Description:
		
	History:
		Fri Nov 28 12:31:30     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<button', this.domAttrs_());
	var tabi = this._tabindex;
	if (this._disabled) out.push(' disabled="disabled"');
	if (tabi >= 0) out.push(' tabindex="', tabi, '"');
	out.push('>', this.domContent_(), '</button>');
}