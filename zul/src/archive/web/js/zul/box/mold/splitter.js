/* splitter.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov  9 17:43:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<div', this.domAttrs_(), '><span id="',
			this.uuid, '-btn"></span></div>');
}