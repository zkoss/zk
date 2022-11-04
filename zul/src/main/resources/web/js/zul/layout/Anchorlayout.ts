/* Anchorlayout.ts

	Purpose:

	Description:

	History:
		Mon Oct  3 11:14:17 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An anchorlayout lays out a container which can resize
 * it's children base on its width and height<br>
 *
 * @defaultValue {@link getZclass}: z-anchorlayout.
 *
 * @author peterkuo
 * @since 6.0.0
 */
@zk.WrapClass('zul.layout.Anchorlayout')
export class Anchorlayout extends zul.Widget {
	/** @internal */
	override beforeChildAdded_(child: zk.Widget, insertBefore?: zk.Widget): boolean {
		if (!(child instanceof zul.layout.Anchorchildren)) {
			zk.error('Unsupported child for Anchorlayout: ' + child.className);
			return false;
		}
		return true;
	}
}