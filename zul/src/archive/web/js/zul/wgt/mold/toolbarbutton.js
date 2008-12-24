/* toolbarbutton.js

	Purpose:

	Description:

	History:
		Sat Dec 22 12:58:43     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var html = '<a ' + this.domAttrs_();
	return html + '>' + this.domContent_() + '</a>';
}
