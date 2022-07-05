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
export class Treefoot extends zul.Widget {
	public override parent!: zul.sel.Tree | null;
	/** Returns the tree that it belongs to.
	 * @return Tree
	 */
	public getTree(): zul.sel.Tree | null {
		return this.parent;
	}

	//bug #3014664
	public override setVflex(v: boolean | string | null | undefined): void { //vflex ignored for Treefoot
		v = false;
		super.setVflex(v);
	}

	//bug #3014664
	public override setHflex(v: boolean | string | null | undefined): void { //hflex ignored for Treefoot
		v = false;
		super.setHflex(v);
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tr>');
	}
}
zul.sel.Treefoot = zk.regClass(Treefoot);
