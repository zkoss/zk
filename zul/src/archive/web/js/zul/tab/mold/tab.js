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
	var tbx = this.getTabbox(),
		uuid = this.uuid,
		icon = this.$s('icon'),
		removeIcon = '<i id="' + uuid + '-cls" class="z-icon-remove ' + icon + '"></i>',
		isAccordion = tbx.inAccordionMold(),
		tag = isAccordion ? 'div' : 'li';
	 
	if (isAccordion) {//Accordion
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
	}

	out.push('<', tag, ' ', this.domAttrs_(), '>');
	var c = this.firstChild,
		hasCaption = c ? c.$instanceof(zul.wgt.Caption) : false;
	if (!hasCaption) 
		out.push('<a id="', uuid, '-cave" class="', this.$s('content'), '" >');

	if (this.isClosable())
		out.push('<div id="', uuid , '-btn" class="', this.$s('button'), '">', removeIcon, '</div>');

	this.contentRenderer_(out);
		
	if (!hasCaption)
		out.push('</a>');
	out.push('</', tag, '>');

	if (isAccordion && n) // panel already rendered, do insert
		jq(n).prepend(out.join(''));
	
}