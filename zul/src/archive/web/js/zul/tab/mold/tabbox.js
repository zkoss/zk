/* tabbox.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:27:40 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	var iconImg = this.tabs.$s('icon-img'),
		uuid = this.uuid,
		zicon = this.$s('icon'),
		getIcon = function(iconClass) {
			return '<i class="' + zicon + ' z-' + iconClass + '"></i>';
		};
	out.push('<div ', this.domAttrs_(), '>');
	if (this.isHorizontalBottom()){
		if (this.tabpanels) this.tabpanels.redraw(out);
		out.push('<div id="', uuid , '-right" class="', iconImg, ' ', this.$s('right'), '">' , getIcon('icon-caret-right'),  '</div>',
				'<div id="', uuid , '-left" class="', iconImg, ' ', this.$s('left'), '">' , getIcon('icon-caret-left'),  '</div>');
		if (this.tabs) this.tabs.redraw(out);
		if(this.isTabscroll() && this.toolbar) this.toolbar.redraw(out);
	} else {
		if (!this.isVertical()) {			
			out.push('<div id="', uuid , '-right" class="', iconImg, ' ', this.$s('right'), '">' , getIcon('icon-caret-right'),  '</div>',
						'<div id="', uuid , '-left" class="', iconImg, ' ', this.$s('left'), '">' , getIcon('icon-caret-left'),  '</div>');
			if(this.isTabscroll() && this.toolbar) {
				this.toolbar.redraw(out);
			}
		}
		if (this.tabs) this.tabs.redraw(out);
		if (this.isVertical()) {
			out.push('<div id="', uuid , '-up" class="', iconImg, ' ', this.$s('up'), '">' , getIcon('icon-caret-up'),  '</div>',
						'<div id="', uuid , '-down" class="', iconImg, ' ', this.$s('down'), '">' , getIcon('icon-caret-down'),  '</div>');
		}
		if (this.tabpanels) this.tabpanels.redraw(out);
		
	}	
	
	if (this.isVertical())
		out.push('<div class="z-clear"></div>');
	out.push("</div>");
}