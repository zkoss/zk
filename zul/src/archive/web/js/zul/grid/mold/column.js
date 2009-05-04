/* column.js

	Purpose:
		
	Description:
		
	History:
		Wed Dec 24 15:26:18     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
	out.push('<th', this.domAttrs_(), '><div id="', uuid, '$cave" class="',
			zcls, '-cnt">', this.domContent_());
	if (this.parent.menupopup && this.parent.menupopup != 'none')
		out.push('<a id="', uuid, '$btn"  href="javascript:;" class="', zcls, '-btn"></a>');
	
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></th>');	
}
