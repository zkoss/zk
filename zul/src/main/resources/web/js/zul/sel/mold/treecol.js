/* treecol.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:31:55     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<th', this.domAttrs_(), '><div id="', this.uuid, '-cave" class="',
			this.$s('content'), '"', this.domTextStyleAttr_(),
			'><div class="', this.$s('sorticon'), '"><i id="', this.uuid, '-sort-icon"></i></div>', this.domContent_());
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></th>');	
}
