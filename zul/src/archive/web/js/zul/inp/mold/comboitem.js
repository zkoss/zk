/* comboitem.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:59:06     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass(),
		v = this._description,
		m = v? '<br/><div class="' + zcls + '-img-bottom">&nbsp;</div>' : '';
	// ZK-945
	// merge image and label into one td
	// add spacer so can customize it easier
	out.push('<tr', this.domAttrs_({text:true}), '><td><div class="',
		zcls, '-img">', this.domImage_(), m, '</div><span class="',
		zcls, '-spacer">&nbsp;</span><div class="',
		zcls, '-text">', this.domLabel_());

	
	if (v)
		out.push('<br/><span class="', zcls, '-inner">',
			zUtl.encodeXML(v), '</span>');
	if (v = this._content)
		out.push('<span class="', zcls, '-cnt">', v, '</span>');

	out.push('</div></td></tr>');
}
