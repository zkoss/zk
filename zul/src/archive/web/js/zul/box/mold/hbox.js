/* hbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 14:10:32     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	delete this._splitterKid; 
	for (var w = this.firstChild; w; w = w.nextSibling)
		if (w.$instanceof(zul.box.Splitter)) {
			this._splitterKid = true;
			break;
		}
	this._configPack();
	
	out.push('<table', this.domAttrs_(), zUtl.cellps0, '><tr');
	
	var	v = this.getAlign();
	if (v && v != 'stretch') out.push(' valign="', zul.box.Box._toValign(v), '"');
	//IE && FF2 need to set height, or the table height will shrink to as high as inner table.
	//FF3 is OK to set or not set
	out.push('><td id="', this.uuid, '-frame" style="width:100%;height:100%"');
	
	if (!this._isStretchPack() && this._pack2) out.push(' align="', zul.box.Box._toHalign(this._pack2), '"');
	out.push('><table id="', this.uuid, '-real"', zUtl.cellps0, 'style="text-align:left');
	if (v == 'stretch') out.push(';height:100%');
	if (this._isStretchPack()) out.push(';width:100%');
	
	out.push('"><tr valign="', this._isStretchPack() && v && v != 'stretch'? zul.box.Box._toValign(v) : 'top', '">');
	
	for (var w = this.firstChild; w; w = w.nextSibling)
		this.encloseChildHTML_(w, false, out);

	out.push('</tr></table></td></tr></table>');
}