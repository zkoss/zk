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
 *<p>Default {@link #getZclass}: z-menuseparator.
 */
export class Menuseparator extends zul.Widget {
	/** Returns whether parent is a {@link Menupopup}
	 * @return boolean
	 */
	public isPopup(): boolean | null {
		return this.parent && this.parent instanceof zul.menu.Menupopup;
	}

	/** Returns the {@link Menubar} that contains this menuseparator, or null if not available.
	 * @return zul.menu.Menubar
	 */
	public getMenubar(): zul.menu.Menubar | null {
		for (var p = this.parent; p; p = p.parent)
			if (p instanceof zul.menu.Menubar)
				return p;
		return null;
	}

	protected override doMouseOver_(evt: zk.Event): void {
		if (zul.menu._nOpen)
			zWatch.fire('onFloatUp', this); //notify all
		super.doMouseOver_(evt);
	}
}
zul.menu.Menuseparator = zk.regClass(Menuseparator);
