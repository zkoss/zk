/* domgecko.ts

	Purpose:

	Description:

	History:
		Tue Sep 28 19:09:15 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
import {type JQZK, zjq} from './dom';

Object.assign(zjq.prototype, {
	beforeHideOnUnbind: function (this: JQZK) { //Bug 3076384
		return this.jq.each(function () {
			// eslint-disable-next-line @typescript-eslint/ban-ts-comment
			// @ts-ignore
			for (var ns = this.getElementsByTagName('iframe'), j = ns.length; j--;)
				ns[j].src = zjq.src0;
		});
	}
});