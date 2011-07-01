/* style.js

	Purpose:
		
	Description:
		
	History:
		Thu Jun 30 14:33:41 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
function (out) {
	var src, v;
	if (src = this._src)
		out.push('<link rel="stylesheet" type="text/css" href="', src, '"');
	else
		out.push('<style');

	if (v = this._media)
		out.push(' media="', v, '"');
	out.push(this.domAttrs_());

	if (src)
		out.push('/>');
	else {
		out.push('>');
		if (v = this._content)
			out.push(v);
		out.push('</style>');
	}
}
