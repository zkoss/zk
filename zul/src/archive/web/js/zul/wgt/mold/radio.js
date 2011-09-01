/* radio.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 16 11:17:47     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		rg = this.getRadiogroup();
	out.push('<span', this.domAttrs_(), '>', '<input type="radio" id="', uuid,
		'-real"', this.contentAttrs_(), '/><label for="', uuid, '-real"',
		this.domTextStyleAttr_(), ' class="', zcls, '-cnt">', this.domContent_(),
		'</label>', (rg && rg._orient == 'vertical' ? '<br/>' :''), '</span>');
}