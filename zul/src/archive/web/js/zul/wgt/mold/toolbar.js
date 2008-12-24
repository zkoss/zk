/* toolbar.js

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function () {
	var zcls = this.getZclass();
		html = '<div ' + this.domAttrs_() + '>',
		clear = '<div class="z-clear"></div>';
		html += '<div id="' + this.uuid + '$cave"';
		html += ' class="' + zcls + "-body " + zcls + '-' + this.getAlign() + '" >';
		for (var w = this.firstChild; w; w = w.nextSibling)
			html += ('vertical' != this.getOrient() ? '' : '<br/>') + w.redraw();
	return html + '</div>' + clear + '</div>';
}
