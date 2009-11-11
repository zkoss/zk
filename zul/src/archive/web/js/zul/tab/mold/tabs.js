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
		tbx = this.getTabbox(),
		uuid = this.uuid;
	out.push('<div ', this.domAttrs_(), '>');
	if (tbx.isVertical()) {
		out.push('<div id="', uuid, '-header" class="', zcls, '-header">',
				'<ul id="', uuid, '-cave" class="', zcls, '-cnt">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('<li id="', uuid, '-edge" class="', zcls, '-edge" ></li></ul></div>',
				'<div id="', uuid, '-up"> </div>',
				'<div id="', uuid, '-down"></div></div><div id="', uuid,
				'-line" class="', zcls, '-space" ></div>');
	} else {
		var hasToolbar = tbx.isTabscroll() && tbx.toolbar;
		if (hasToolbar) {
			out.push('<div class="', tbx.toolbar.getZclass(), '-outer">');
				tbx.toolbar.redraw(out);	
		}
		out.push('<div id="', uuid, '-right">', '</div>',
			'<div id="', uuid, '-left">', '</div>', '<div id="', uuid, '-header"',
			' class="', zcls, '-header" >', '<ul id="', uuid, '-cave"', 'class="', zcls, '-cnt">');
			for (var w = this.firstChild; w; w = w.nextSibling)
				w.redraw(out);
		out.push('<li id="', uuid, '-edge"', ' class="', zcls, '-edge" ></li>',
			'<div id="',uuid,'-clear" class="z-clear"> </div>',
			'</ul></div>');
		if (hasToolbar)	out.push('</div>');	
		out.push('<div id="', uuid, '-line"',
			' class="', zcls, '-space" ></div></div>');
	}
}