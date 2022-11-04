/* Menuseparator.ts

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:35     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Used to create a separator between menu items.
 *
 * @defaultValue {@link getZclass}: z-menuseparator.
 */
@zk.WrapClass('zul.menu.Menuseparator')
export class Menuseparator extends zul.Widget {
	/**
	 * @returns whether parent is a {@link Menupopup}
	 */
	isPopup(): boolean {
		return !!this.parent && this.parent instanceof zul.menu.Menupopup;
	}

	/**
	 * @returns the {@link Menubar} that contains this menuseparator, or null if not available.
	 */
	getMenubar(): zul.menu.Menubar | undefined {
		for (var p = this.parent; p; p = p.parent)
			if (p instanceof zul.menu.Menubar)
				return p;
		return undefined;
	}

	/** @internal */
	override doMouseOver_(evt: zk.Event): void {
		if (zul.menu._nOpen)
			zWatch.fire('onFloatUp', this); //notify all
		super.doMouseOver_(evt);
	}
}
