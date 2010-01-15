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
	var uuid = this.uuid,
		zcls = this.getZclass();
	out.push('<i', this.domAttrs_({text:true}), '><input id="',
			uuid, '-real" class="', zcls, '-inp-simple" autocomplete="off"',
			this.textAttrs_(), '/><i id="', uuid, '-btn" class="',
			zcls, '-btn-simple');

	if (!this._buttonVisible)
		out.push(' ', zcls, '-btn-right-edge');

	out.push('"></i>');

	this.redrawpp_(out);

	out.push('</i>');
}
