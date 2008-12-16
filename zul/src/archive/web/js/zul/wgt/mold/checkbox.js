/* checkbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 10 16:51:36     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var uuid = this.uuid,
		zcls = this.getZclass();
	return '<span' + this.domAttrs_({disabled: 1, checked: 1, tabindex: 1}) + '>'
		 + '<input type="checkbox" id="' + uuid + '$real"' + this._contentAttrs()
		 + '/><label for="' + uuid + '$real"' + this._labelAttrs()
		 + ' class="' + zcls + '-cnt">' + this.domContent_() + '</label></span>';	
}