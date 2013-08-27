/* toolbarbutton.js

	Purpose:

	Description:

	History:
		Sat Dec 22 12:58:43     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<a', this.domAttrs_(), '><span id="', this.uuid, '-cnt"',
			this.domTextStyleAttr_(), 'class="', this.$s('content'), '">',
			this.domContent_(), '</span></a>');
}
