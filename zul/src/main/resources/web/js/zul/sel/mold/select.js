/* select.js

	Purpose:
		
	Description:
		
	History:
		Mon Jun  1 16:44:09     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function select$mold$(out) {
	out.push('<select', this.domAttrs_(), '>');

	let w;
	if (this.hasGroup())
		for (w = this.firstChild; w; w = w.nextSibling) {
			if ((w instanceof zul.sel.Optgroup)) w.redraw(out);
		}
	else
		for (w = this.firstChild; w; w = w.nextSibling) {
			if ((w instanceof zul.sel.Option) && w.isVisible()) w.redraw(out);
		}

	out.push('</select>');
}
