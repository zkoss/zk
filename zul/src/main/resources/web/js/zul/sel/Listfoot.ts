/* Listfoot.ts

	Purpose:

	Description:

	History:
		Tue Jun  9 18:03:06     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A row of {@link Listfooter}.
 *
 * <p>Like {@link Listhead}, each listbox has at most one {@link Listfoot}.
 * <p>Default {@link #getZclass}: z-listfoot
 */
export class Listfoot extends zul.Widget {
	public override parent!: zul.sel.Listbox | null;
	/** Returns the list box that it belongs to.
	 * @return Listbox
	 */
	public getListbox(): zul.sel.Listbox | null {
		return this.parent;
	}

	//bug #3014664
	public override setVflex(v: boolean | string | null | undefined): void { //vflex ignored for Listfoot
		v = false;
		super.setVflex(v);
	}

	//bug #3014664
	public override setHflex(v: boolean | string | null | undefined): void { //hflex ignored for Listfoot
		v = false;
		super.setHflex(v);
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tr>');
	}
}
zul.sel.Listfoot = zk.regClass(Listfoot);
