/* flashchart.js

    Purpose:

	Description:

	History:
		Nov 26, 2009 11:58:49 AM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.

*/
function (out) {
	out.push('<div', this.domAttrs_(), '><object id="', this.uuid, '-chart"></object></div>');
}