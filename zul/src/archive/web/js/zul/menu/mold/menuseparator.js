/* menuseparator.js

	Purpose:
		
	Description:
		
	History:
		Thu Jan 15 09:03:06     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<li', this.domAttrs_(), '><span class="', this.getZclass(),
			'-inner">&nbsp;</span></li>');
}
