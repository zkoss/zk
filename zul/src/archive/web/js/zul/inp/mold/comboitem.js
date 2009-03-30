/* comboitem.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:59:06     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass();
	out.push('<tr', this.domAttrs_({text:true}), '><td class="',
		zcls, '-img">', this.domImage_(), '</td><td class="',
		zcls, '-text">', this.domLabel_());

	var v;
	if (v = this._description)
		out.push('<br/><span class="', zcls, '-inner">',
			zUtl.encodeXML(v), '</span>');
	if (v = this._content)
		out.push('<span class="', zcls, '-cnt">', v, '</span>');

	out.push('</td></tr>');
}
