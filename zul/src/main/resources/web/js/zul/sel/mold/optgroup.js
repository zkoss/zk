/* optgroup.js

	Purpose:

	Description:

	History:
		Mon Sep 03 13:01:21 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var visible = this.isVisible();
	if (visible) out.push('<optgroup', this.domAttrs_(), '>');
	for (var w = this.nextSibling; w ; w = w.nextSibling) {
		if (w.$instanceof(zul.sel.Optgroup))
			break;
		if (w.$instanceof(zul.sel.Option) && this.isOpen() && w.isVisible())
			w.redraw(out);
	}
	if (visible) out.push('</optgroup>');
}
