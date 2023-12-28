/* spinner.js

	Purpose:
		
	Description:
		
	History:
		Thu May 27 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function spinner$mold$(out) {
	const uuid = this.uuid,
		isButtonVisible = this._buttonVisible;

	out.push('<span', this.domAttrs_({
			text: true,
			tabindex: true
		}), ' role="none">',
		'<input id="', uuid, '-real"', 'class="', this.$s('input'));

	if (!isButtonVisible)
		out.push(' ', this.$s('input-full'));

	out.push('" autocomplete="off"',
		/*safe*/ this.textAttrs_(), ' role="spinbutton"/>', '<span id="', uuid, '-btn"',
		'class="', this.$s('button'));

	if (!isButtonVisible)
		out.push(' ', this.$s('disabled'));

	var iconClassHTML = this.$s('icon') + ' ';
	out.push('" aria-hidden="true"><a id="', uuid, '-btn-up" class="', iconClassHTML, this.$s('up'),
		'"><i class="', /*safe*/ this.getBtnUpIconClass_(), '"></i></a><i class="', this.$s('separator'),
		'"></i><a id="', uuid, '-btn-down" class="', iconClassHTML, this.$s('down'),
		'"><i class="', /*safe*/ this.getBtnDownIconClass_(), '"></i></a>');

	out.push('</span></span>');
}
