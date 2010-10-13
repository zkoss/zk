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
		uuid = this.uuid,
		isRounded = this.inRoundedMold(),
		isButtonVisible = this._buttonVisible;
	
	out.push('<i', this.domAttrs_({text:true}), '>',
			'<input id="', uuid,'-real"', 'class="', zcls,'-inp');
			
	if(!isButtonVisible)
		out.push(' ', zcls, '-right-edge');
		
	out.push('"', this.textAttrs_(),'/>', '<i id="', uuid,'-btn"',
			'class="', zcls,'-btn ');
	
	if (isRounded) {
		if (!isButtonVisible)
			out.push(' ', zcls, '-btn-right-edge');
		if (this._readonly)
			out.push(' ', zcls, '-btn-readonly');	
		if (zk.ie6_ && !isButtonVisible && this._readonly)
			out.push(' ', zcls, '-btn-right-edge-readonly');
	} else if (!isButtonVisible)
		out.push('" style="display:none"');	
	
	out.push('">');
	//Merge breeze: for splitting timebox/spinner button to two pieces
	zul.inp.Renderer.renderSpinnerButton(out, this);
	out.push('</i></i>');
	
}
