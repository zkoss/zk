/* label.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct 19 15:15:59     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function () {
	var html = '<span id="' + this.uuid + '"';
	if (this.style) html += ' style="' + this.style + '"';
	return html+'>'+this.value+'</span>';
}