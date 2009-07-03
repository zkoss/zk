/* Datebox.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri June 9 10:29:16 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid;
	out.push('<span id="', uuid, '"',this.domAttrs_() ,
			'><input id="', uuid, '-real" class="', zcls,'-inp" autocomplete="off"', this.textAttrs_(), ' />',
			'<span id="', uuid, '-btn" class="', zcls, '-btn"');
	if (!this._buttonVisible)
		out.push(' style="display:none"');

	out.push('><span class="', zcls, '-img" ></span></span><div id="',
			uuid, '-pp" class="', zcls, '-pp" style="display:none" tabindex="-1">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></span>');

}