/* input.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 16 13:13:15     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid,
		isRounded = this.inRoundedMold();
	if(!isRounded)
		out.push('<input', this.domAttrs_(), '/>');
	else {
		out.push('<i', this.domAttrs_({text:true}), '>',
				'<input id="', uuid, '-real"', 'class="', zcls, '-inp"', 
				this.textAttrs_(), '/>', '<i id="', uuid, '-right-edge"',
				'class="', zcls, '-right-edge');
		if (zk.ie6_ && this._readonly)
			out.push(' ', zcls, '-right-edge-readonly');
		out.push('"></i></i>');
	}
}