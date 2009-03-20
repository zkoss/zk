/* iframe.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 19 11:48:02     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<iframe', this.domAttrs_(), '>', '</iframe>');
}