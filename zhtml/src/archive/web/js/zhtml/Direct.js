/* Direct.js

	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 19:33:37 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
zhtml.Direct = zk.$extends(zhtml.Widget, {
	redraw: function (out) {
		var s = this.prolog;
		if (s) out.push(s);

		out.push(this.content||'');

		s = this.epilog;
		if (s) out.push(s);
	}
});
