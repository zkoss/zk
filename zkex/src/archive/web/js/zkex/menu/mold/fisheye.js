/* fisheye.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	
	out.push('<div', this.domAttrs_(), '><img id="',
	this.uuid,'$img"','src="',this.getImage() ,'" class="',this.getZclass(),'-img"/>',
	'<div id="',this.uuid,'$label" style="display:none;" class="',this.getZclass(),'-text">');
	
	out.push(this.getLabel());
	
	out.push('</div></div>');
	
}

