/* domgecko.ts

	Purpose:

	Description:

	History:
		Tue Sep 28 19:09:15 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
Object.assign<zk.JQZK, Partial<zk.JQZK>>(zjq.prototype, {
	beforeHideOnUnbind(): unknown { //Bug 3076384
		return this.jq!.each(function () {
			for (var ns = this.getElementsByTagName('iframe'), j = ns.length; j--;)
				ns[j].src = zjq.src0;
		});
	}
});