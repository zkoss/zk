/* Treefoot.js

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
zul.sel.Treefoot = zk.$extends(zul.Widget, {
	/** Returns the tree that it belongs to.
	 * @return Tree
	 */
	getTree: function () {
		return this.parent;
	},
	getZclass: function () {
		return this._zclass == null ? "z-treefoot" : this._zclass;
	}
});
