/* drawer.js

	Purpose:

	Description:

	History:
		Fri Aug 30 11:19:05 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function redraw(out) {
	var uuid = this.uuid,
		title = this._title;
	out.push('<div', this.domAttrs_({width: true, height: true}), '>');
	out.push('<div id="', uuid, '-mask" class="', this.$s('mask'),' ', this._mask ? this.$s('mask-enabled') : '','"></div>');
	out.push('<div id="', uuid, '-real" class="', this.$s('real'), '"', this._dimension(), '>');
	out.push('<div id="', uuid, '-close" class="', this.$s('close'), '" style="', !this._closable ? 'display:none;' : '', '"><i class="z-icon-times"></i></div>');
	out.push('<div id="', uuid, '-header" class="', this.$s('header'), '" style="', title ? '' : 'display:none;', '">');
	out.push('<div id="', uuid, '-title" class="', this.$s('title'), '">', zUtl.encodeXML(title) ,'</div>');
	out.push('</div>');
	out.push('<div id="', uuid, '-container" class="', this.$s('container'), '">');
	out.push('<div id="', uuid ,'-cave" class="', this.$s('cave'), '">');
	for (var child = this.firstChild; child; child = child.nextSibling)
		child.redraw(out);
	out.push('</div></div></div></div></div>');
}