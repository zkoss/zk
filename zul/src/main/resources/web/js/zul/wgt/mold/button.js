/* button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:43     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function button$mold$(out) {
	out.push('<button type="', zUtl.encodeXML(this._type), '"', this.domAttrs_());
	if (this._disabled) out.push(' disabled="disabled"');
	out.push('>', this.domContent_(), '</button>');
}