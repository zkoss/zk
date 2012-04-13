/* spinner.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid,
		isRounded = this.inRoundedMold(),
		isButtonVisible = this._buttonVisible;
	
	out.push('<i', this.domAttrs_({text:true}), '>',
			'<input id="', uuid,'-real"', 'class="', zcls,'-inp');
			
	if(!isButtonVisible)
		out.push(' ', zcls, '-right-edge');
		
	out.push('"', this.textAttrs_(),'/>', '<i id="', uuid,'-btn"',
			'class="', zcls, '-btn');
	
	if (isRounded) {
		if (!isButtonVisible)
			out.push(' ', zcls, '-btn-right-edge');
		if (this._readonly)
			out.push(' ', zcls, '-btn-readonly');	
		if (zk.ie6_ && !isButtonVisible && this._readonly)
			out.push(' ', zcls, '-btn-right-edge-readonly');
	} else if (!isButtonVisible)
		out.push('" style="display:none');	
	
	out.push('">');
	if (!isRounded)
		out.push('<div id="', uuid, '-btn-up" class="', zcls, 
				'-btn-upper"><div class="', zcls, '-btn-up-icon"></div></div>',
				'<div id="', uuid, '-btn-down" class="', zcls, 
				'-btn-lower"><div class="', zcls, '-btn-down-icon"></div></div>');
	out.push('</i></i>');
	
}
