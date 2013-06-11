/* paging.js

	Purpose:
		
	Description:	
		
	History:
		Fri Jan 23 15:00:45     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	if (this.getMold() == "os") {
		out.push('<div', this.domAttrs_(), '>', this._innerTags(), '</div>');
		return;
	}
	
	var uuid = this.uuid,
		btn = this.$s('button');
	
	out.push('<div', this.domAttrs_(), '><ul>',
			'<li><a id="', uuid, '-first" class="', btn,
				'"><i class="z-icon-step-backward"></i></a></li>',
			'<li><a id="', uuid, '-prev" class="', btn,
				'"><i class="z-icon-caret-left"></i></a></li>',
			'<li><input id="',
				uuid, '-real" class="', this.$s('input'), '" type="text" value="',
				this.getActivePage() + 1, '" size="1"></input><span class="',
				this.$s('text'), '"> / ', this.getPageCount(), '</span></li>',
			'<li><a id="', uuid, '-next" class="', btn,
				'"><i class="z-icon-caret-right"></i></a></li>',
			'<li><a id="', uuid, '-last" class="', btn,
				'"><i class="z-icon-step-forward"></i></a></li></ul>');
	
	if (this.isDetailed())
		this._infoTags(out);
	out.push('</div>');
}
