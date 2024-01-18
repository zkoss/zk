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
 * @defaultValue {@link getZclass}: z-listfoot
 */
@zk.WrapClass('zul.sel.Listfoot')
export class Listfoot extends zul.Widget {
	override parent!: zul.sel.Listbox | undefined;
	/**
	 * @returns the list box that it belongs to.
	 */
	getListbox(): zul.sel.Listbox | undefined {
		return this.parent;
	}

	//bug #3014664
	override setVflex(vflex?: boolean | string): this { //vflex ignored for Listfoot
		const vflex0 = false;
		return super.setVflex(vflex0);
	}

	//bug #3014664
	override setHflex(hflex?: boolean | string): this { //hflex ignored for Listfoot
		const hflex0 = false;
		return super.setHflex(hflex0);
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tr>');
	}
}