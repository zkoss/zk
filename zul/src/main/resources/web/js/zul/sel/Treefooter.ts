/* Treefooter.ts

	Purpose:

	Description:

	History:
		Wed Jun 10 15:32:42     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A column of the footer of a tree ({@link Tree}).
 * Its parent must be {@link Treefoot}.
 *
 * <p>Unlike {@link Treecol}, you could place any child in a tree footer.
 * <p>Note: {@link Treecell} also accepts children.
 * @defaultValue {@link getZclass}: z-treefooter
 */
@zk.WrapClass('zul.sel.Treefooter')
export class Treefooter extends zul.mesh.FooterWidget {
	// NOTE: The parent of Treefooter should be Treefoot, but Treefoot is not a HeadWidget.
	// override parent!: zul.sel.Treefoot | null;

	/**
	 * @returns the tree that this belongs to.
	 */
	getTree(): zul.sel.Tree | undefined {
		return this.getMeshWidget() as zul.sel.Tree | undefined;
	}

	/**
	 * @returns the tree header that is in the same column as
	 * this footer, or null if not available.
	 */
	getTreecol(): zul.sel.Treecol | undefined {
		return this.getHeaderWidget() as zul.sel.Treecol | undefined;
	}

	/**
	 * @returns the maximal length for this cell.
	 * It is the same as the correponding {@link getTreecol}'s
	 * {@link Treecol#getMaxlength}.
	 * @since 5.0.5
	 */
	getMaxlength(): number {
		var tc = this.getTreecol();
		return tc ? tc.getMaxlength()! : 0;
	}

	/** @internal */
	override domLabel_(): string {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this.getMaxlength()});
	}
}