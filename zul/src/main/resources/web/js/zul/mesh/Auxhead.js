/* Auxhead.js

	Purpose:

	Description:

	History:
		Mon May  4 15:57:46     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Used to define a collection of auxiliary headers ({@link Auxheader}).
 *
 * <p>Non XUL element.
 * <p>Default {@link #getZclass}: z-auxhead.
 */
zul.mesh.Auxhead = zk.$extends(zul.mesh.HeadWidget, {
	bind_: function (desktop, skipper, after) {
		this.$supers(zul.mesh.Auxhead, 'bind_', arguments);
		// B50-3306729: the first header should have border-left when the first column is covered with other header
		this.fixBorder_();
	},
	// B50-3306729: the first header should have border-left when the first column is covered with other header
	fixBorder_: function () {
		var fc = jq(this).children(':first-child'),
			rspan = fc.attr('rowspan'),
			times = parseInt(rspan) - 1;
		if (rspan && times > 0) {
			for (var head = this.nextSibling; head && times != 0; head = head.nextSibling, times--)
				jq(head.firstChild).addClass(this.$s('border'));
		}
	}
});
