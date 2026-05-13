/* breadcrumb.js

        Purpose:
                
        Description:
                
        History:
                Mon May 11 15:15:17 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

function breadcrumb$mold$(out) {
	out.push('<nav', this.domAttrs_(), '><ol class="', this.$s('list'), '">');
	var sep = this._separator,
		zclsHtml = this.getZclass(),
		iconSclassHtml;
	if (sep && sep.indexOf('icon:') === 0) {
		iconSclassHtml = zUtl.encodeXMLAttribute(sep.substring(5));
	}
	var first = true;
	for (var w = this.firstChild; w; w = w.nextSibling) {
		if (!first) {
			out.push('<li class="', zclsHtml, '-separator">');
			if (iconSclassHtml) {
				out.push('<i class="', iconSclassHtml, '"></i>');
			} else {
				out.push(zUtl.encodeXML(sep));
			}
			out.push('</li>');
		}
		first = false;
		w.redraw(out);
	}
	out.push('</ol></nav>');
}
