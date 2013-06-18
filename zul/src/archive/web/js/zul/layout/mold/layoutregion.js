/* layoutregion.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:44:56     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		pos = this.getPosition(),
		BL = zul.layout.Borderlayout,
		noCenter = pos != BL.CENTER,
		parent = this.parent;
	out.push('<div id="', uuid,  '">', '<div id="', uuid, '-real"',
			this.domAttrs_({id: 1}), '>');
	
	this.titleRenderer_(out);
	out.push('<div id="', uuid, '-cave" class="', this.$s('body'), '">');
	
	var firstChild = this.getFirstChild();
	if (firstChild)
		firstChild.redraw(out);
	
	out.push('</div></div>');
	
	if (noCenter) {
		var icon = this.$s('icon'),
			doticon = ' z-icon-ellipsis-' +
				(BL.WEST == pos || BL.EAST == pos ? 'vertical' : 'horizontal'),
			splitIcon = '';

		switch (pos) {
			case BL.NORTH:
				splitIcon = 'z-icon-caret-up';
				break;
			case BL.SOUTH:
				splitIcon = 'z-icon-caret-down';
				break;
			case BL.WEST:
				splitIcon = 'z-icon-caret-left';
				break;
			case BL.EAST:
				splitIcon = 'z-icon-caret-right';
				break;
		}
		out.push('<div id="', uuid, '-split" class="', this.$s('splitter'),
			'"><span id="', uuid, '-splitbtn" class="', this.$s('splitter-button'));
		if (!this._collapsible)
			out.push(' ', this.$s('splitter-button-disabled'));
		out.push('">',
			'<i class="', icon, doticon, '"></i>',
			'<i class="', icon, ' ', splitIcon, '"></i>',
			'<i class="', icon, doticon, '"></i>',
			'</span></div>', '<div id="', uuid, '-colled" class="',
				this.$s('collapsed'), '" style="display:none"><i id="',
				uuid, '-btned" class="', parent.$s('icon'), ' ', this.getIconClass_(true),
				'"');
		if (!this._collapsible)
			out.push(' style="display:none;"');
				
		out.push('></i></div>');
	}
	out.push('</div>');
}