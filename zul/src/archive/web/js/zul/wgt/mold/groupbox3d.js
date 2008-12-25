/* groupbox3d.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:47:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {	
	var	zcls = this.getZclass(),
		uuid = this.uuid,
		cap = this.caption;

	out.push('<div', this.domAttrs_(), '>');
	
	if (cap) {
		out.push('<div class="', zcls, '-tl"><div class="', zcls,
				'-tr"><div class="', zcls, '-tm"><div class="', zcls, '-header">');
		cap.redraw(out);
		out.push('</div></div></div></div>');
	}
	
	out.push('<div id="', uuid, '$panel" class="', zcls, '-body"');
	if (!this.isOpen()) out.push(' style="display:none"');
	out.push('><div id="', uuid, '$cave"', this._contentAttrs(), '>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != cap)
				w.redraw(out);
	out.push('</div></div>',
		//shadow
		'<div id="', uuid, '$sdw" class="', zcls, '-bl"><div class="', zcls,
			'-br"><div class="', zcls, '-bm"></div></div></div></div>');
}