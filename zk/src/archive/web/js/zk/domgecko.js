/* domgecko.js

	Purpose:
		
	Description:
		
	History:
		Tue Sep 28 19:09:15 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
zk.copy(zjq.prototype, {
	beforeHideOnUnbind: function () { //Bug 3076384
		return this.jq.each(function () {
			for (var ns = this.getElementsByTagName("iframe"), j = ns.length; j--;)
				ns[j].src = zjq._src0;
		});
	}
});
