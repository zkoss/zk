/* image.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 15:07:28     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<img', this.domAttrs_(), '/>');
}