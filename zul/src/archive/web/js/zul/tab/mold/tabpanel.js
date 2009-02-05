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
		tabzcs = tab.getZclass(),
		tabbox = this.getTabbox();
	if (tabbox.inAccordionMold()) {//Accordion
		out.push('<div id="',uuid ,'"' ,' class="',zcls ,'-outer">' );
		if (tabbox.getMold() == "accordion" && tabbox.getPanelSpacing() != null && this.getIndex() != 0) {
			out.push('<div style="margin:0;display:list-item;width:100%;height:',tabbox.getpanelSpacing(),';"></div>');
		}
		out.push('<div id="',tab.uuid,'"',tab.domAttrs_(),'>',
		'<div align="left" class="',tabzcs,'-header" >');
		if (tab.isClosable()) {
			out.push('<a id="',tab.uuid,'$close"  class="',tabzcs,'-close"></a>');
		}
		out.push('<a href="javascript:;" id="',tab.uuid,'$a" class="',tabzcs,'-tl">',
		'<em class="',tabzcs,'-tr">','<span class="',tabzcs,'-tm">','<span class="',tabzcs,'-text">',
		tab.domContent_(),'</span></span></em></a></div></div>'
		);
		out.push('<div id="',uuid,'$real"', this.domAttrs_(),'>',
			'<div id="',uuid,'$cave" >');
		for (var w = this.firstChild; w; w = w.nextSibling)
				w.redraw(out);
		out.push('</div></div></div>');
	} else {//Default Mold
		out.push('<div id="', uuid,'"' ,this.domAttrs_(), '>', '<div id="', uuid, '$real">')
			for (var w = this.firstChild; w; w = w.nextSibling)
				w.redraw(out);
		out.push('</div></div>');
	}
}