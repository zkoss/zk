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
function tabpanel$mold$(out) {
	let w;
	const uuid = this.uuid,
		tabbox = this.getTabbox();
	if (tabbox.inAccordionMold()) {//Accordion
		var tab = this.getLinkedTab();

		out.push('<div class="', this.getZclass(), '" id="', uuid, '" role="none">');
		// only draw tab if it is not rendered
		if (tab && !tab.$n())
			tab.redraw(out);
		out.push('<div id="', uuid, '-cave"', this.domAttrs_({
			id: 1,
			zclass: 1
		}), ' role="region">');
		for (w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div></div>');

	} else {//Default Mold
		out.push('<div ', this.domAttrs_(), ' role="tabpanel">');
		for (w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div>');
	}
}