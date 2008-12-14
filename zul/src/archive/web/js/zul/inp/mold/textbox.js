/* textbox.js

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 23:32:20     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	return this.isMultiline() ?
		'<textarea' + this.domAttrs_() + this.innerAttrs_() + '>' + this._areaText() + '</textarea>':
		'<input' + this.domAttrs_() + this.innerAttrs_() + '/>';
}