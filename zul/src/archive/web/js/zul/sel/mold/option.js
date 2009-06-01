/* option.js

	Purpose:
		
	Description:
		
	History:
		Mon Jun  1 16:44:16     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<option', this.domAttrs_(), '>', this.domLabel_(), '</option>');
}
