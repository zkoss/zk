/* label.js

	Purpose:
		
	Description:
		
	History:
		Sun Oct 19 15:15:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<span', this.domAttrs_(), '>', zUtl.encodeXML(this._value), '</span>');
}