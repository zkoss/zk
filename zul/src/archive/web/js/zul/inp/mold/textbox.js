/* textbox.js

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 23:32:20     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid;
	// ZK-679: Textbox multi-line start with new-line failed in onCreate event
	// browser will ignore first newline
	if(this.isMultiline()) 
		out.push('<textarea', this.domAttrs_(), '>\n', this._areaText(), '</textarea>');
	else 
		out.push('<input', this.domAttrs_(), '/>');
}