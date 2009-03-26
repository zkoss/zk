/* menuseparator.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:06     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var tagnm = this.isPopup() ? "li" : "td";
	out.push('<',tagnm, this.domAttrs_(), '><span class="', this.getZclass(),
			'-inner">&nbsp;</span></',tagnm,'>');
}
