/* spinner.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid;
	
	out.push('<i', this.domAttrs_({text:true}), '>',
			'<input id="', uuid,'-real"', 'class="', zcls,'-inp"',
			this.textAttrs_(),'/>', '<i id="', uuid,'-btn"',
			'class="', zcls,'-btn');
	
	if (!this._buttonVisible)
		out.push(' ', zcls, '-btn-right-edge');
	if (this._readonly)
		out.push(' ', zcls, '-btn-readonly');	
	if (zk.ie6_ && !this._buttonVisible && this._readonly)
		out.push(' ', zcls, '-btn-right-edge-readonly');
	
	out.push('"></i></i>');
	
}
