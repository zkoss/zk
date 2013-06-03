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
	var zcls = this.getZclass(),
		tbx = this.getTabbox(),
		uuid = this.uuid,
		self = this,
		iconImg = this.$s('icon-img'),
		getIcon = function(iconClass) {
			return '<i class="' + self.$s('icon') + ' z-' + iconClass + '"></i>';
		};
	out.push('<div ', this.domAttrs_(), '>');
	
	
	var hasToolbar = tbx.isTabscroll() && tbx.toolbar;
	if (!tbx.isVertical()) {
		if (hasToolbar) {
			out.push('<div class="', tbx.toolbar.getZclass(), '-outer">');
			tbx.toolbar.redraw(out);	
		}
		out.push('<div id="', uuid , '-right" class="', iconImg, ' ', this.$s('right'), '">' , getIcon('icon-caret-right'),  '</div>',
					'<div id="', uuid , '-left" class="', iconImg, ' ', this.$s('left'), '">' , getIcon('icon-caret-left'),  '</div>');
	}
	
	out.push('<div id="', uuid, '-header" class="', zcls, '-header">',
				'<ul id="', uuid, '-cave" class="', zcls, '-cnt">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push(		'<li id="', uuid, '-edge" class="', zcls, '-edge"></li>',
						!tbx.isVertical() ? '<div id="' + uuid + '-clear" class="z-clear"> </div>' : '',
				'</ul>',
			 '</div>');
	
	if (tbx.isVertical()) {
		out.push('<div id="', uuid , '-up" class="', iconImg, ' ', this.$s('up'), '">' , getIcon('icon-caret-up'),  '</div>',
					'<div id="', uuid , '-down" class="', iconImg, ' ', this.$s('down'), '">' , getIcon('icon-caret-down'),  '</div>');
	} 
	
	if (!tbx.isVertical()) {
		if(hasToolbar)
			out.push('</div>');
	}
	
	out.push(	'<div id="', uuid,	'-line" class="', zcls, '-space" ></div>',
			 '</div>');
	
}