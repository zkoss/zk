/* vbox.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 14:10:39     2008, Created by tomyeh

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
	
	if (!this._isStretchPack() && this._pack2) out.push(' valign="', zul.box.Box._toValign(this._pack2), '"');
	out.push('><td id="', this.uuid, '-frame" style="width:100%');
	//IE and Safari need to set height, or the table height will shrink to as high as inner table
	//FF2 should not set this, or the td will stretch the parent table height.
	//FF3 is OK to set or not set
	if (zk.ie || zk.safari) out.push(';height:100%');
	out.push('"');
	
	var v = this.getAlign();
	if (v && v != 'stretch') out.push(' align="', zul.box.Box._toHalign(v), '"');
	out.push('><table id="', this.uuid, '-real"', zUtl.cellps0, 'style="text-align:left');
	if (v == 'stretch') out.push(';width:100%');
	if (this._isStretchPack()) out.push(';height:100%');
	out.push('">');

	for (var w = this.firstChild; w; w = w.nextSibling)
		this.encloseChildHTML_(w, false, out);

	out.push('</table></td></tr></table>');
}