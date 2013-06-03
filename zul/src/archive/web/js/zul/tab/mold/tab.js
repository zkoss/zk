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
		self = this,
		iconImg = this.$s('icon-img'),
		getIcon = function(iconClass) {
			return '<i id="' + uuid + '-icon-close" class="' + self.$s('icon') + ' z-' + iconClass + '"></i>';
		};
	 
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

		if (tbx.getPanelSpacing() && this.getIndex())
			out.push('<div class="', zcls, '-spacing" style="margin:0;display:list-item;width:100%;height:', tbx.getPanelSpacing(), ';"></div>');

		out.push('<div id="', this.uuid, '"', this.domAttrs_(), '>',
					'<div align="left" class="', zcls, '-header" >');

		if (this.isClosable())
			out.push('<div id="', uuid , '-close" class="', iconImg, ' ', this.$s('close'), '">' , getIcon('icon-remove'), '</div>');

		this.contentRenderer_(out);
			
		out.push('</div></div>');

		if (n) // panel already rendered, do insert
			jq(n).prepend(out.join(''));
	} else {
		out.push('<li ', this.domAttrs_(), '>');
		if (this.isClosable())
			out.push('<div id="', uuid , '-close" class="', iconImg, ' ', this.$s('close'), '">' , getIcon('icon-remove'),  '</div>');
		else if (tbx.isVertical())
			out.push('<a class="', zcls, '-noclose" ></a>');
		this.contentRenderer_(out);
		
		out.push('</li>');
	}
}