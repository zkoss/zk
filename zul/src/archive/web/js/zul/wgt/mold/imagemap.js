/* imagemap.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 26 15:54:09     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid, mapid = uuid + '$map';
	out.push('<span', this.domAttrs_({content:1}), '><a href="',
		this._doneURI(), '?', uuid, '" target="zk_hfr_"><img id="',
		uuid, '$real"', this.contentAttrs_(),
		'/></a><map name="', mapid, '" id="', mapid, '">');

	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</map></span>');
}
