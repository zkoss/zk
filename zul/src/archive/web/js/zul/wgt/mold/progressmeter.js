/* progressmeter.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 10:17:14     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<div', this.domAttrs_(), '><span id="',
			this.uuid,'-img"', 'class="', this.$s('image'),'"></span></div>');
}