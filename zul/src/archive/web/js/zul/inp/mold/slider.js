/* slider.js

	Purpose:
		
	Description:
		
	History:
		Thu May 22 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function (out) {
	var zcls = this.getZclass(),
		isScaleMold = this.inScaleMold(),
		uuid = this.uuid;
		
	if(isScaleMold){
		out.push('<div id="', uuid, '" class="', zcls, '-tick">');
		this.uuid += '-real'; //this is for calling domAttrs
	}
	
	out.push('<div', this.domAttrs_(), '>');
	
	if(isScaleMold)
		this.uuid = uuid;
	
		out.push('<div id="', uuid, '-inner" class="', zcls, '-center">',
				'<div id="', uuid, '-btn" class="', zcls, '-btn">',
				'</div></div></div>');
	
	if(isScaleMold)
		out.push('</div>');
}