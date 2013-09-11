/* combobutton.js

	Purpose:
		
	Description:
		
	History:
		Mon June 16 12:11:07     2013, Created by huangnoah

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var tabi = this._tabindex,
		uuid = this.uuid;
	
	out.push('<span ', this.domAttrs_());
	
	if (this._disabled)
		out.push(' disabled="disabled"');
	
	out.push(' ><span id="', uuid, '-real" class="', this.$s('content') ,'"');
	
	if (tabi)
		out.push(' tabindex="', tabi, '"');
	
	out.push('>', this.domContent_(), 
			 '<span id="', uuid, '-btn" class="', this.$s('button'), '">', 
				'<i id="', uuid, '-icon" class="', this.$s('icon'), ' z-icon-caret-down"></i>', 
			 '</span></span>');
	// pp
	if (this.firstChild)
		this.firstChild.redraw(out);
	
	out.push('</span>');
}