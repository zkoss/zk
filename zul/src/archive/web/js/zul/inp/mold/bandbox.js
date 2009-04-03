/* bandbox.js

	Purpose:
		
	Description:
		
	History:
		Fri Apr  3 15:25:16     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
	out.push('<span', this.domAttrs_({text:true}), '><input id="',
		uuid, '$real" class="', zcls, '-inp" autocomplete="off"',
		this.textAttrs_(), '/><span id="', this.uuid, '$btn" class="',
		zcls, '-btn"');

	if (!this._btnVisible)
		out.push(' style="display:none"');

	out.push('><span class="', zcls, '-img"></span></span><div id="',
		uuid, '$pp" class="', zcls,
		'-pp" style="display:none" tabindex="-1">');

	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</div></span>');
}