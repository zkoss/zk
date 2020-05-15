/* popup.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 17 19:16:12     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid;
		
	out.push('<div', this.domAttrs_(), ' role="tooltip">');
	
	if(this._fixarrow)	// Merge breeze: a div for pointer in Errorbox
		out.push('<div id=', uuid, '-p class="z-pointer"></div>');
			
	out.push('<div id="', uuid, '-cave" class="', this.$s('content'), '">');
	this.prologHTML_(out);
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	this.epilogHTML_(out);
	out.push('</div></div>');
}