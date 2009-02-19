/* groupbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:46:42     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {
	out.push('<fieldset', this.domAttrs_(), '>');
	
	var cap = this.caption;
	if (cap) cap.redraw(out);

	out.push('<div id="', this.uuid, '$cave"', this._contentAttrs(), '>');
	
	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != cap)
				w.redraw(out);
	out.push('</div></fieldset>');
}