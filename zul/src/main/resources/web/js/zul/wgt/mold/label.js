/* label.js

	Purpose:
		
	Description:
		
	History:
		Sun Oct 19 15:15:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
function label$mold$(out) {
	out.push('<span', this.domAttrs_(), '>', /*safe*/ this.getEncodedText(), '</span>');
}