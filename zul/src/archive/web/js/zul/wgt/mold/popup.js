/* popup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 19:16:12     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		isFrameRequired = zul.wgt.Popup.Renderer.isFrameRequired();
		
	out.push('<div', this.domAttrs_(), '>');
	
	if (isFrameRequired)
		out.push('<div class="', zcls, '-tl"><div class="', 
					zcls, '-tr"></div></div>');
	else if(this._fixarrow)	// Merge breeze: a div for pointer in Errorbox
		out.push('<div id=', uuid, '-p class="z-pointer"></div>');
		
	out.push('<div id="', uuid, '-body" class="', zcls, '-cl">');
	
	if (isFrameRequired)
		out.push('<div class="', zcls, '-cr"><div class="', zcls, '-cm">');
		
	out.push('<div id="', uuid, '-cave" class="', zcls, '-cnt">');
	this.prologHTML_(out);
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	this.epilogHTML_(out);
	out.push('</div></div></div>');
	
	if (isFrameRequired)
		out.push('</div><div class="', zcls, '-bl"><div class="',
					zcls, '-br"></div></div></div>');
}