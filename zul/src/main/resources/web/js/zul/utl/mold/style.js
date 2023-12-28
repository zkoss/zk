/* style.js

	Purpose:
		
	Description:
		
	History:
		Thu Jun 30 14:33:41 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
function style$mold$(out) {
	var src, v;

	out.push('<div style="display:none" id="', this.uuid, '">&#160;');
	if ((src = this._src))
		out.push('<link id="', this.uuid, '-real" rel="stylesheet" type="text/css" href="', DOMPurify.sanitize(src), '"');
	else
		out.push('<style id="', this.uuid, '-real"');

	if ((v = this._media))
		out.push(' media="', zUtl.encodeXML(v), '"');
	out.push(this.domAttrs_({id: true}));

	if (src)
		out.push('></style>');
	else {
		out.push('>');
		if ((v = this._content))
			out.push(DOMPurify.sanitize(v));
		out.push('</style>');
	}
	out.push('</div>');
}
