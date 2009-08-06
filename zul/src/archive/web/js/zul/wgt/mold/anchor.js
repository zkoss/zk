/* anchor.js

	Purpose:
		
	Description:
		
	History:
		Thu Aug  6 15:21:48     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<a ', this.domAttrs_(), '>', this.domContent_(), '</a>');
}
