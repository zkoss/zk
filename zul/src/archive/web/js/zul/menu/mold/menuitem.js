/* menuitem.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:03:05     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		target = this.getTarget(),
		chechmark = this._checkmark;
	
	out.push('<li', this.domAttrs_(), ' role="none">');
	
	out.push('<a role="' + (chechmark ? 'menuitemcheckbox' : 'menuitem') + '" href="', this.getHref() ? this.getHref() : 'javascript:;', '"');
	if (target)
		out.push(' target="', target, '"');
	out.push(' id="', uuid, '-a" class="', this.$s('content'), '"',
			this._disabled ? ' disabled="disabled"' : '',
			'>', this.domContent_(), '</a></li>'); //Merge breeze
}
