/* splitter.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov  9 17:43:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var icon = this.$s('icon'),
		zicon = ' z-icon-ellipsis-' + ('vertical' == this.getOrient() ? 'horizontal' : 'vertical');
	out.push('<div', this.domAttrs_(), '><span id="',
			this.uuid, '-btn" class="', this.$s('button'),'">',
			'<i class="', icon, zicon, '"></i>',
			'<i id="', this.uuid ,'-icon" class="', icon, '"></i>',
			'<i class="', icon, zicon, '"></i>',
			'</span></div>');
}