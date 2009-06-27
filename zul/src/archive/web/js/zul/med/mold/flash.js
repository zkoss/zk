/* flash.js

	Purpose:
		
	Description:
		
	History:
		Sat Mar 28 22:03:14     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	out.push('<div', this.domAttrs_({width:true,height:true}),
		'><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=',
		this._version, '" width="', this._width||'', '" height="', this._height||'',
		'"><param name="movie" value="', this._src,
		'"></param><param name="wmode" value="', this._wmode,
		'"></param><param name="quality" value="', this._quality,
		'"></param><param name="autoplay" value="', this._autoplay,
		'"></param><param name="loop" value="', this._loop,
		'"></param>');

	var bgc;
	if (bgc = this._bgcolor)
		out.push('<param name="bgcolor" value="', bgc, '"');

	out.push('<embed id="', this.uuid, '-emb" src="', this._src,
		'" type="application/x-shockwave-flash" wmode="', this._wmode,
		'" quality="', this._quality,
		'" autoplay="', this._autoplay,
		'" loop="', this._loop,
		'" width="', this._width||'', '" height="', this._height||'',
		'"');

	if (bgc) out.push(' bgcolor="', bgc, '"');

	out.push('></embed></object></div>');
}