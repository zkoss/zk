/* textbox.js

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 23:32:20     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	if(this.isMultiline()) 
		out.push('<textarea', this.domAttrs_(), this.innerAttrs_(), '>',
				this._areaText(), '</textarea>');
	else
		out.push('<input', this.domAttrs_(), this.innerAttrs_(), '/>');
}