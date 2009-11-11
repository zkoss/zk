/* spinner.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid;
	
	out.push('<span', this.domAttrs_({text:true}), '>',
			'<input id="', uuid,'-real"', 'class="', zcls,'-inp"',
			this.textAttrs_(),'/>', '<span id="', uuid,'-btn"',
			'class="', zcls,'-btn" ');
	
	if(!this.isButtonVisible())
		out.push('" style="display:none"');
	
	out.push('><span class="', zcls, '-img"></span></span></span>');
	
}
