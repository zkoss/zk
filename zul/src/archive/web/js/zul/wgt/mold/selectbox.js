/* selectbox.js

	Purpose:
		
	Description:
		
	History:
		Fri Sep 30 10:51:52 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<select', this.domAttrs_(), '>');
	var s = $eval(this.items) || [] ;
	for (var i = 0, j = s.length; i < j; i++) {
		out.push('<option');
		if (this._selectedIndex > -1 && this._selectedIndex == i)
			out.push(' selected="selected"');
		out.push('>', s[i], '</option>');
	}
	out.push('</select>');
}