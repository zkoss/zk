/* Treefoot.ts

	Purpose:

	Description:

	History:
		Wed Jun 10 15:32:41     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A row of {@link Treefooter}.
 *
 * <p>Like {@link Treecols}, each tree has at most one {@link Treefoot}.
 * <p>Default {@link #getZclass}: z-treefoot
 */
@zk.WrapClass('zul.sel.Treefoot')
export class Treefoot extends zul.Widget {
	override parent!: zul.sel.Tree | undefined;
	/** Returns the tree that it belongs to.
	 * @return Tree
	 */
	getTree(): zul.sel.Tree | undefined {
		return this.parent;
	}

	//bug #3014664
	override setVflex(vflex?: boolean | string): this { //vflex ignored for Treefoot
		vflex = false;
		return super.setVflex(vflex);
	}

	//bug #3014664
	override setHflex(hflex: boolean | string): this { //hflex ignored for Treefoot
		hflex = false;
		return super.setHflex(hflex);
	}

	override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tr>');
	}
}