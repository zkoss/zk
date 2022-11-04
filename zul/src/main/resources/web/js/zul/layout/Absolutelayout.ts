/* Absolutelayout.ts

	Purpose:

	Description:

	History:
		Mon Oct  3 11:14:17 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * <p>An Absolutelayout component can contain absolute positioned multiple
 * absolutechildren components.
 *
 * @defaultValue {@link getZclass}: z-absolutelayout.
 *
 * @author ashish
 * @since 6.0.0
 */
@zk.WrapClass('zul.layout.Absolutelayout')
export class Absolutelayout extends zul.Widget {
	/** @internal */
	override beforeChildAdded_(child: zk.Widget, insertBefore?: zk.Widget): boolean {
		if (!(child instanceof zul.layout.Absolutechildren)) {
			zk.error('Unsupported child for Absolutelayout: ' + child.className);
			return false;
		}
		return true;
	}
	static redraw(this: zk.Widget, out: string[]): void {
		out.push('<div ', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div>');
	}
}