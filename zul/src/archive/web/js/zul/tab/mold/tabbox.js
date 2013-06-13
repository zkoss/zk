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
	var icon = this.$s('icon'),
		uuid = this.uuid,
		getIcon = function(fontIconCls) {
			return '<i class="z-' + fontIconCls + '"></i>';
		};
	out.push('<div ', this.domAttrs_(), '>');
	if (this.isHorizontalBottom()){
		if (this.tabpanels) this.tabpanels.redraw(out);
		out.push('<div id="', uuid , '-right" class="', icon, ' ', this.$s('right'), '">' , getIcon('icon-caret-right'),  '</div>',
				'<div id="', uuid , '-left" class="', icon, ' ', this.$s('left'), '">' , getIcon('icon-caret-left'),  '</div>');
		if (this.tabs) this.tabs.redraw(out);
		if(this.isTabscroll() && this.toolbar) this.toolbar.redraw(out);
	} else {
		if (!this.isVertical()) {			
			out.push('<div id="', uuid , '-right" class="', icon, ' ', this.$s('right'), '">' , getIcon('icon-caret-right'),  '</div>',
						'<div id="', uuid , '-left" class="', icon, ' ', this.$s('left'), '">' , getIcon('icon-caret-left'),  '</div>');
			if(this.isTabscroll() && this.toolbar) {
				this.toolbar.redraw(out);
			}
		}
		if (this.tabs) this.tabs.redraw(out);
		if (this.isVertical()) {
			out.push('<div id="', uuid , '-up" class="', icon, ' ', this.$s('up'), '">' , getIcon('icon-caret-up'),  '</div>',
						'<div id="', uuid , '-down" class="', icon, ' ', this.$s('down'), '">' , getIcon('icon-caret-down'),  '</div>');
		}
		if (this.tabpanels) this.tabpanels.redraw(out);
		
	}	
	
	if (this.isVertical())
		out.push('<div class="z-clear"></div>');
	out.push("</div>");
}