/* tabpanel.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:30:32 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		tab = this.getLinkedTab(),
		tabbox = this.getTabbox();
	if (tabbox.inAccordionMold()) {//Accordion
		out.push('<div class="', zcls, '-outer" id="', uuid, '">');
		if (tab)
			tab.redraw(out);
		out.push('<div id="', uuid, '-real"', this.domAttrs_(), '>',
				'<div id="', uuid, '-cave">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div></div></div>');

	} else {//Default Mold
		out.push('<div id="', uuid, '"' , this.domAttrs_(), '>');
		if (tabbox.isHorizontal())
			out.push('<div id="', uuid, '-real">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		if (tabbox.isHorizontal())
			out.push('</div>');
		out.push('</div>');
	}
}