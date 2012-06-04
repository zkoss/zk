/* tab.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:29:16 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	var zcls = this.getZclass(),
		tbx = this.getTabbox(),
		uuid = this.uuid;
	if (tbx.inAccordionMold()) {//Accordion
		var panel = this.getLinkedPanel(),
			n = panel? panel.$n() : null,
			c = n? n.firstChild : null;
		// Bug ZK-419
		// no linked panel
		// Bug ZK-674
		// Bug ZK-886
		// return if no LinkedPanel
		// or the LinkedPanel already Linked to another tab
		// this._oldId is from Tab.js#_logId
		if (!panel || (c && c != panel.$n('cave')
			&& (this._oldId? c.id != this._oldId : c != this.$n()))) 
			return;
		// push to new array to insert if panel already rendered
		out = n? [] : out;

		if (tbx.getMold() == 'accordion-lite') {
			out.push('<div id="', this.uuid, '"', this.domAttrs_(), '>',
				'<div align="left" class="', zcls, '-header">');
			if (this.isClosable())
				out.push('<a id="', this.uuid, '-close" class="', zcls, '-close"><div class="', zcls, '-close-icon"></div></a>');

			out.push('<div href="javascript:;" id="', this.uuid, '-tl" class="', zcls, '-tl">',
					'<div class="', zcls, '-tr">',
					'<span class="', zcls, '-tm">',
					'<span class="', zcls, '-text">', this.domContent_(),
					'</span></span></div></div></div></div>');
		} else {
			var isFrameRequired = zul.tab.TabRenderer.isFrameRequired();
			if (tbx.getPanelSpacing() && this.getIndex())
				out.push('<div class="', zcls, '-spacing" style="margin:0;display:list-item;width:100%;height:', tbx.getPanelSpacing(), ';"></div>');

			out.push('<div id="', this.uuid, '"', this.domAttrs_(), '>',
					'<div align="left" class="', zcls, '-header" >');
			if (isFrameRequired)
				out.push('<div class="', zcls, '-tl" ><div class="', zcls, '-tr" ></div></div>',
						'<div class="', zcls, '-hl" >',
						'<div class="', zcls, '-hr" >');
			out.push('<div class="' + zcls + '-hm" >');

			if (this.isClosable())
				out.push('<a id="', this.uuid, '-close"  class="', zcls, '-close"><div class="', zcls, '-close-icon"></div></a>');

			out.push('<span class="', zcls, '-text">', this.domContent_(), '</span></div></div></div>');
			
			if (isFrameRequired)
				out.push('</div></div>');
		}
		if (n) // panel already rendered, do insert
			jq(n).prepend(out.join(''));
	} else {
		out.push('<li ', this.domAttrs_(), '>');
		if (this.isClosable())
			out.push('<a id="', uuid, '-close" class="', zcls, '-close"', 'onClick="return false;" ><div class="', zcls, '-close-icon"></div></a>');
		else if (tbx.isVertical())
			out.push('<a class="', zcls, '-noclose" ></a>');

		out.push('<div id="', uuid, '-hl" class="', zcls, '-hl"><div id="', uuid, '-hr" class="', zcls, '-hr">');
		if (this.isClosable())
			out.push('<div id="', uuid, '-hm" class="', zcls, '-hm ', zcls, '-hm-close">');
		else
			out.push('<div id="', uuid, '-hm" class="', zcls, '-hm ">');
		out.push('<span class="', zcls, '-text">', this.domContent_(), '</span></div></div></div></li>');
	}
}