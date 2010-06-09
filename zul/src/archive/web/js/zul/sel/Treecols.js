/* Treecols.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:41     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A treecols.
 * <p>Default {@link #getZclass}: z-treecols
 */
zul.sel.Treecols = zk.$extends(zul.mesh.HeadWidget, {
	/** Returns the tree that it belongs to.
	 * @return Tree
	 */
	getTree: function () {
		return this.parent;
	},
	setVisible: function (visible) {
		if (this._visible != visible) {
			this.$supers('setVisible', arguments);
			this.getTree().rerender();
		}
	},
	getZclass: function () {
		return this._zclass == null ? "z-treecols" : this._zclass;
	}
});
