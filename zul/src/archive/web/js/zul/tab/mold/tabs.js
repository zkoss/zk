/* tabs.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:29:05 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	var tbx = this.getTabbox(),
		uuid = this.uuid,
		zicon = this.$s('icon'),
		iconImg = this.$s('icon-img'),
		getIcon = function(iconClass) {
			return '<i class="' + zicon + ' z-' + iconClass + '"></i>';
		};
	out.push('<div ', this.domAttrs_(), '>', 	
			   '<ul id="', uuid, '-cave" class="', this.$s('content'), '">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push(    '<li id="', uuid, '-edge" class="', this.$s('edge'), '"></li>', 
			!tbx.isVertical() ? '<div id="' + uuid + '-clear" class="z-clear"> </div>' : '',
			   '</ul>',
			   '<div id="', uuid,	'-line" class="',  this.$s('space'), '" ></div>',
			 '</div>');
	
}