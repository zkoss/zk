/* spinner.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		isButtonVisible = this._buttonVisible;
	
	out.push('<span', this.domAttrs_({text:true}), '>',
			'<input id="', uuid,'-real"', 'class="', this.$s('input'));

	if (!isButtonVisible)
		out.push(' ', this.$s('rightedge'));
	
	out.push('" autocomplete="off"',
			this.textAttrs_(),'/>', '<span id="', uuid,'-btn"',
			'class="', this.$s('button'));
	
	if (!isButtonVisible)
		out.push(' ', this.$s('disabled'));
		
	var iconClass = this.$s('icon') + ' ';
	out.push('"><a id="', uuid, '-btn-up" class="', iconClass, this.$s('up'),
			'"><i class="', this.getBtnUpIconClass_(), '"></i></a><i class="', this.$s('separator'),
			'"></i><a id="', uuid, '-btn-down" class="', iconClass, this.$s('down'),
			'"><i class="', this.getBtnDownIconClass_(), '"></i></a>');

	out.push('</span></span>');
}
