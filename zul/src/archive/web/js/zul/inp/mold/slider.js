/* slider.js

	Purpose:
		
	Description:
		
	History:
		Thu May 22 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function (out) {
	var isVer = this.isVertical(),
		uuid = this.uuid;
	out.push('<div', this.domAttrs_(isVer ? {width:true} : {height:true}), '><div id="',
				uuid, '-inner" class="', this.$s('center'), '"><div id="',
			    uuid, '-btn" class="', this.$s('button'), '"></div></div></div>');
}