/* combobox.js

	Purpose:
		
	Description:
		
	History:
		Tus Dec 15 11:24:02     2009, Created by jimmy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
	out.push('<i', this.domAttrs_({text:true}), '><input id="',
			uuid, '-real" class="', zcls, '-inp" autocomplete="off"',
			this.textAttrs_(), '/><i id="', uuid, '-btn" class="',
			zcls, '-btn');

	if (!this._buttonVisible)
		out.push(' ', zcls, '-btn-right-edge');
	if (this._readonly)
		out.push(' ', zcls, '-btn-readonly');	
	if (zk.ie6_ && !this._buttonVisible && this._readonly)
		out.push(' ', zcls, '-btn-right-edge-readonly');		
	
	out.push('"></i>');

	this.redrawpp_(out);

	out.push('</i>');
}