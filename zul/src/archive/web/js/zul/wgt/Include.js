/* Include.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 14 15:23:17     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zul.wgt.Include = zk.$extends(zk.Widget, {
	redraw: function () {
		var html = '<div id="' + this.uuid + '"';
		if (this.style) html += ' style="' + this.style + '"';
		html += '>';
		for (var w = this.firstChild; w; w = w.nextSibling)
			html += w.redraw();
		if (this.content) html += this.content;
		return html + '</div>';
	}
}, {
	embedAs: 'content' //retrieve zkDom.$() as value
});
