/* fileupload.js

	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 10:17:14     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass();
	out.push('<form ', this.domAttrs_(), ' method="post" enctype="multipart/form-data" >');
			out.push('<div id="'+this.uuid+'-fsuploadprogress"></div>');
			out.push('<div class="'+zcls+'-fileWrapper">');
				out.push('<input type="file" name="file" id="'+this.uuid+'-file" size="1"/>');
				//size=1 for firefox 2 bug & should plus left:-33px
			out.push('</div>');
	out.push('</form>');
}