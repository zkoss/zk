/* tabs.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:29:05 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid;
	out.push('<div ', this.domAttrs_(), '>' ,'<div id="', uuid, '$right">','</div>',
		'<div id="', uuid, '$left">','</div>','<div id="', uuid, '$header"',
		' class="', zcls, '-header" >','<ul id="', uuid, '$cave"','class="', zcls, '-cnt">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
	out.push('<li id="', uuid,'$edge"',
		' class="', zcls, '-edge" ></li><div class="z-clear"></div></ul>',
		'</div><div id="', uuid, '$line"',
		' class="', zcls, '-space" ></div></div>');
}