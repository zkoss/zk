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
	var zcls = this.getZclass();
	var isScaleMold = this.inScaleMold();
	
	if(isScaleMold){
		var tmp=this.uuid;
		out.push('<div id="'+this.uuid+'" class="'+zcls+'-tick">');
		this.uuid = this.uuid+"-real"; //this is for calling domAttrs
	}
	
	out.push('<div', this.domAttrs_(), '>');
	
	if(isScaleMold)
		this.uuid = tmp;
	
		out.push('<div id="',this.uuid,'-inner" class="',zcls,'-center">');
			out.push('<div id="',this.uuid,'-btn" class="',zcls,'-btn">');
				out.push('<input id="',this.uuid,'-inp" class="',zcls,'-inp" style="display:none;position:relative"/>');
	out.push('</div></div></div>');
	
	if(isScaleMold)
		out.push('</div>');
}