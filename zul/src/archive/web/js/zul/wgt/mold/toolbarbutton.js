/* toolbarbutton.js

	Purpose:

	Description:

	History:
		Sat Dec 22 12:58:43     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass();
	out.push('<div', this.domAttrs_(), '><div class="',
		zcls, '-body"><div ', this.domTextStyleAttr_(), 
		'class="', zcls, '-cnt">', this.domContent_(),
		'</div></div></div>');
}
