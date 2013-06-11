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
	
	out.push('<div name="', uuid, '"', this.domAttrs_(), '><ul>',
			'<li><a name="', uuid, '-first" class="', btn, ' ', this.$s('first'),
				'"><i class="z-icon-step-backward"></i></a></li>',
			'<li><a name="', uuid, '-prev" class="', btn, ' ', this.$s('previous'),
				'"><i class="z-icon-caret-left"></i></a></li>',
			'<li><input name="',
				uuid, '-real" class="', this.$s('input'), '" type="text" value="',
				this.getActivePage() + 1, '" size="3"></input><span class="',
				this.$s('text'), '"> / ', this.getPageCount(), '</span></li>',
			'<li><a name="', uuid, '-next" class="', btn, ' ', this.$s('next'),
				'"><i class="z-icon-caret-right"></i></a></li>',
			'<li><a name="', uuid, '-last" class="', btn, ' ', this.$s('last'),
				'"><i class="z-icon-step-forward"></i></a></li></ul>');
	
	if (this.isDetailed())
		this._infoTags(out);
	out.push('</div>');
}
