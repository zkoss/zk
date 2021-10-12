/* menu.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:05     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		contentHandler = this._contentHandler;
	
	out.push('<li', this.domAttrs_(), ' role="none"><a role="menuitem" aria-haspopup="true" href="javascript:;" id="', uuid,
			'-a" class="', this.$s('content'), '"', this._disabled ? ' disabled="disabled" tabindex="-1"' : '', '>', this.domContent_(), '</a>');
	if (this.menupopup)
		this.menupopup.redraw(out);
	else if (contentHandler)
		contentHandler.redraw(out);
	
	out.push('</li>');
}