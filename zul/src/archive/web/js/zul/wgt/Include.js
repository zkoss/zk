/* Include.js

	Purpose:
		
	Description:
		
	History:
		Tue Oct 14 15:23:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Include = zk.$extends(zul.Widget, {
	_content: '',

	$define: {
		content: function (v) {
			var n = this.$n();
			if (n) {
				if (v && this._comment)
					v = '<!--\n' + v + '\n-->';
				n.innerHTML = v  || '';
			}
		},
		comment: null
	},

	//super//
	redraw: function (out) {
		out.push('<div', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		if (this._comment)
			out.push('<!--\n');
		out.push(this._content);
		if (this._comment)
			out.push('\n-->');
		out.push('</div>');
	}
});
