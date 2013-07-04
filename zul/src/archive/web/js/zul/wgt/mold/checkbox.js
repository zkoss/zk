/* checkbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 10 16:51:36     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid, content = this.domContent_();
	out.push('<span', this.domAttrs_(), '>', '<input type="checkbox" id="', uuid,
			'-real"', this.contentAttrs_(), '/>', 
			'<label for="', uuid, '-real" id="', uuid, '-cnt"');
	
	out.push(this.domTextStyleAttr_(), 
			' class="', this.$s('content') ,'">', this.domContent_(), '</label></span>');
			
}