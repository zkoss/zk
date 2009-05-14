/* progressmeter.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 10:17:14     2009, Created by kindalu

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass();
	out.push('<div', this.domAttrs_(), '><span id="',
	this.uuid,'$img"', 'class="',zcls,'-img"></span></div>');
}