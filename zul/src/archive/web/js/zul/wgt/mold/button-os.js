/* button-os.js

	Purpose:
		Button's os mold
	Description:
		
	History:
		Fri Nov 28 12:31:30     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var html = this.domAttrs_(), tabi = this._tabindex;
	if (this._disabled) html += ' disabled="disabled"';
	if (tabi >= 0) html += ' tabindex="' + tabi +'"';
	return '<button' + html + '>' + this.domContent_() + '</button>';
}