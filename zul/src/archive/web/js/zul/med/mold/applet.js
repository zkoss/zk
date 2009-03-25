/* applet.js

	Purpose:
		
	Description:
		
	History:
		Wed Mar 25 18:22:44     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<applet', this.domAttrs_(), '>');
	this._outParamHtml(out);
	out.push('</applet>');
}