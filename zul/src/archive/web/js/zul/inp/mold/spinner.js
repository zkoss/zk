/* spinner.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass();
	
	out.push('<span', this.domAttrs_({text:true}), '>');
	out.push('<input id="', this.uuid,'-real"', 'class="', this.getZclass(),'-inp"',this.textAttrs_(),'/>');
	out.push('<span id="', this.uuid,'-btn"', 'class="', this.getZclass(),'-btn" ');
	
	if(!this.isButtonVisible())
		out.push('" style="display:none"');
	
	out.push('><img class="', zcls, '-img" onmousedown="return false;"');
	out.push( 'src="',zAu.comURI('web/img/spacer.gif'),'"/></span></span>');
	
}
		