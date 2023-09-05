/* Auxhead.ts

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
 * @defaultValue {@link getZclass}: z-auxhead.
 */
@zk.WrapClass('zul.mesh.Auxhead')
export class Auxhead extends zul.mesh.HeadWidget {
	/** @internal */
	override bind_(desktop: zk.Desktop | undefined, skipper: zk.Skipper | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		// B50-3306729: the first header should have border-left when the first column is covered with other header
		this.fixBorder_();
	}

	// B50-3306729: the first header should have border-left when the first column is covered with other header
	/** @internal */
	fixBorder_(): void {
		var fc = jq(this).children(':first-child'),
			rspan = fc.attr('rowspan')!,
			times = parseInt(rspan) - 1;
		if (rspan && times > 0) {
			for (var head = this.nextSibling; head && times != 0; head = head.nextSibling, times--)
				jq(head.firstChild!).addClass(this.$s('border'));
		}
	}
}