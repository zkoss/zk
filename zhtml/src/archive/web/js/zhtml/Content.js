/* Content.js

	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 19:33:37 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
zhtml.Content = zk.$extends(zhtml.Widget, {
	$define: {
		content: function () {
			this.rerender();
		}
	},
	redraw: function (out) {
		var s = this.prolog;
		if (s) out.push(s);

		// B65-ZK-1836
		out.push((this._content||'').replace(/<\/(?=script>)/ig, '<\\/'));

		s = this.epilog;
		if (s) out.push(s);
	}
});
