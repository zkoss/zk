/* radio.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 16 11:17:47     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function radio$mold$(out) {
	var uuid = this.uuid,
		rg = this.getRadiogroup();
	out.push('<span', this.domAttrs_({tabindex: 1}), '><input type="radio" id="', uuid,
		'-real"', /*safe*/ this.contentAttrs_(), '/><label for="', uuid, '-real"',
		' id="', uuid, '-cnt"', this.domTextStyleAttr_(),
		' class="', this.$s('content'), '">', this.domContent_(), '</label>',
		(rg && rg._orient == 'vertical' ? '<br/>' : ''), '</span>');
}