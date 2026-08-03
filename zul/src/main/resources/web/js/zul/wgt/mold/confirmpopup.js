/* confirmpopup.js

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:15:45 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function confirmpopup$mold$(out) {
	// role="alertdialog"/aria-modal/aria-labelledby (pointing at the -header or
	// -message text node) are layered on by the za11y add-on (EE). The CE mold
	// keeps the stable -header/-message ids so that layer has targets.
	var uuid = this.uuid,
		zclsHtml = this.getZclass();
	out.push('<div', this.domAttrs_(), '>');
	out.push('<span class="', zclsHtml, '-arrow"></span>');
	if (this._header) {
		out.push('<div id="', uuid, '-header" class="', zclsHtml, '-header">',
			zUtl.encodeXML(this._header), '</div>');
	}
	out.push('<div class="', zclsHtml, '-body">');
	if (this._iconSclass) {
		out.push('<i class="', zclsHtml, '-icon ', zUtl.encodeXMLAttribute(this._iconSclass),
			'"></i>');
	}
	if (this._message) {
		out.push('<div id="', uuid, '-message" class="', zclsHtml, '-message">',
			zUtl.encodeXML(this._message), '</div>');
	}
	out.push('</div>');
	out.push('<div class="', zclsHtml, '-footer">');
	// Button text is the locale-resolved MZul.OK/CANCEL already shipped to the
	// client as msgzul.OK/msgzul.CANCEL (zul/impl/Utils#outLocaleJavaScript) —
	// read it directly here instead of carrying a per-popup label property.
	out.push('<button type="button" id="', uuid, '-cancel" class="', zclsHtml, '-cancel">',
		zUtl.encodeXML(msgzul.CANCEL), '</button>');
	out.push('<button type="button" id="', uuid, '-ok" class="', zclsHtml, '-ok">',
		zUtl.encodeXML(msgzul.OK), '</button>');
	out.push('</div>');
	out.push('</div>');
}
