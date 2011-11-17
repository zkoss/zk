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
	var uuid = this.uuid, zcls = this.getZclass(), content = this.domContent_();
	out.push('<span', this.domAttrs_(), '>', '<input type="checkbox" id="', uuid,
			'-real"', this.contentAttrs_(), '/><label ');
		
	//Fix bug 3290873 ,for ie6/7 if label is empty , ignore the "for" attribute 
	//on label to prevent the bug . We left the label here ,so it won't break 
	//user's style customization if exist.
	if(!(zk.ie < 8)/*not the same as zk.ie >= 8*/ || jq.trim(content))
		out.push('for="', uuid, '-real"');
	
	out.push(this.domTextStyleAttr_(), 
			' class="', zcls, '-cnt">', this.domContent_(),	'</label></span>');
			
}