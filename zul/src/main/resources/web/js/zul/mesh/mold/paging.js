/* paging.js

	Purpose:
		
	Description:	
		
	History:
		Fri Jan 23 15:00:45     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function paging$mold$(out) {
	var uuid = this.uuid;

	out.push('<nav name="', uuid, '"', this.domAttrs_(), '>');
	if (this.getMold() == "os") {
		out.push(this._innerTags(), '</nav>');
		return;
	}
	
	var btn = this.$s('button'),
		showFirstLast = this._showFirstLast ? '' : 'display: none;';
	
	out.push('<ul role="none">',
			'<li style="' , showFirstLast, '" role="none"><button name="', uuid, '-first" class="', btn, ' ', this.$s('first'),
				'" aria-label="', msgzul.FIRST, '"><i class="z-paging-icon z-icon-angle-double-left" aria-hidden="true"></i></button></li>',
			'<li role="none"><button name="', uuid, '-prev" class="', btn, ' ', this.$s('previous'),
				'" aria-label="', msgzul.PREV, '"><i class="z-paging-icon z-icon-angle-left" aria-hidden="true"></i></button></li>',
			'<li role="none"><input name="',
				uuid, '-real" class="', this.$s('input'), '" type="text" value="',
				this.getActivePage() + 1, '" size="3" aria-label="', msgzul.CURRENT, '"></input><span class="',
				this.$s('text'), '" aria-hidden="true"> / ', this.getPageCount(), '</span></li>',
			'<li role="none"><button name="', uuid, '-next" class="', btn, ' ', this.$s('next'),
				'" aria-label="', msgzul.NEXT, '"><i class="z-paging-icon z-icon-angle-right" aria-hidden="true"></i></button></li>',
			'<li role="none" style="' , showFirstLast, '"><button name="', uuid, '-last" class="', btn, ' ', this.$s('last'),
				'" aria-label="', msgzul.LAST, '"><i class="z-paging-icon z-icon-angle-double-right" aria-hidden="true"></i></button></li></ul>');
	
	if (this.isDetailed())
		this._infoTags(out);
	out.push('</nav>');
}
