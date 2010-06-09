/* Treecol.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A treecol.
 * <p>Default {@link #getZclass}: z-treecol
 */
zul.sel.Treecol = zk.$extends(zul.mesh.HeaderWidget, {
	/** Returns the tree that it belongs to.
	 * @return Tree
	 */
	getTree: zul.mesh.HeaderWidget.prototype.getMeshWidget,
	$define: {
    	/** Returns the maximal length of each item's label.
    	 * @return int
    	 */
    	/** Sets the maximal length of each item's label.
    	 * @param int maxlength
    	 */
		maxlength: [function (v) {
			return !v || v < 0 ? 0 : v; 
		}, function () {
			if (this.desktop)
				this.updateCells_();
		}]
	},
	updateCells_: function () {
		var tree = this.getTree();
		if (tree)
			zul.sel.Treecol.updateCell(tree.treechildren, this.getChildIndex());			
	},
	getZclass: function () {
		return this._zclass == null ? "z-treecol" : this._zclass;
	}
}, {
	updateCell: function(tch, jcol) {
		if (!tch) 
			return;
		
		for (var w = tch.firstChild; w; w = w.nextSibling) {
			if (w.treerow) {
				if (jcol < w.treerow.nChildren) 
					w.treerow.getChildAt(jcol).rerender();
			}
			
			zul.sel.Treecol.updateCell(w.treechildren, jcol); //recursive
		}
	}
});
