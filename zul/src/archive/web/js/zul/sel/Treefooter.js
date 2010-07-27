/* Treefooter.js

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
 * <p>Default {@link #getZclass}: z-treefooter
 */
zul.sel.Treefooter = zk.$extends(zul.mesh.FooterWidget, {
	/** Returns the tree that this belongs to.
	 * @return Tree
	 */
	getTree: function () {
		return this.getMeshWidget();
	},
	/** Returns the tree header that is in the same column as
	 * this footer, or null if not available.
	 * @return Treecol
	 */
	getTreecol: function () {
		return this.getHeaderWidget();
	},
	getZclass: function () {
		return this._zclass == null ? "z-treefooter" : this._zclass;
	}
});
