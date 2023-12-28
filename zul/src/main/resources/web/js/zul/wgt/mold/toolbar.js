/* toolbar.js

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function toolbar$mold$(out) {
	var uuid = this.uuid;

	out.push('<div ', this.domAttrs_(), '><div id="', uuid, '-cave"',
		' class="', this.$s('content'), ' ', this.$s(this.getAlign()), '" role="none" >');

	this._tabindexTargetChild = undefined;
	for (var w = this.firstChild; w; w = w.nextSibling) {
		w.redraw(out);
	}
	out.push('</div>');

	if (this.isOverflowPopup()) {
		out.push('<i id="', uuid, '-overflowpopup-button"',
			' class="', zUtl.encodeXML(this._getOverflowPopupBtnClass()), '" aria-hidden="true"></i>');
		out.push('<div id="', uuid, '-pp" class="', this.$s('popup'), ' ', this.$s('popup-close'), '"></div>');
	}
	out.push('<div class="z-clear" aria-hidden="true"></div></div>');
}
