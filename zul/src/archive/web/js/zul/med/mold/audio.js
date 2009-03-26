/* audio.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 11:59:58     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<embed', this.domAttrs_(), ' mastersound enablejavascript="true"/>');
}