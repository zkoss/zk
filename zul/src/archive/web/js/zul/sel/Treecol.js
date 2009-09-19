/* Treecol.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Treecol = zk.$extends(zul.mesh.HeaderWidget, {
	getTree: zul.mesh.HeaderWidget.prototype.getMeshWidget,
	$define: {
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
			zul.sel.Treecol.updateCell(tree.treechildren, jcol);			
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
