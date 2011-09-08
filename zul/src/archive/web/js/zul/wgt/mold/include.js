/* include.js

	Purpose:
		
	Description:
		
	History:
		Mon Apr 19 12:14:44 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
function (out) {
	out.push('<div', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	if (this._comment)
		out.push('<!--\n');
	if ((w=this._xcnt) && !jq.isArray(w)) //array -> z$ea
		out.push(w); //not z$ea
	if (this._comment)
		out.push('\n-->');
	out.push('</div>');
}
