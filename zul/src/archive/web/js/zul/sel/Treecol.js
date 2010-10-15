/* Treecol.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _updCells(tch, jcol) {
		if (tch)
			for (var w = tch.firstChild, tr; w; w = w.nextSibling) {
				if ((tr = w.treerow) && jcol < tr.nChildren) 
					tr.getChildAt(jcol).rerender(0);

				_updCells(w.treechildren, jcol); //recursive
			}
	}

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
			if (this.desktop) {
				this.rerender(0);
				this.updateCells_();
			}
		}]
	},
	updateCells_: function () {
		var tree = this.getTree();
		if (tree) {
			var jcol = this.getChildIndex(),
				tf = tree.treefoot;

			_updCells(tree.treechildren, jcol);

			if (tf && jcol < tf.nChildren)
				tf.getChildAt(jcol).rerender(0);
		}
	},
	getZclass: function () {
		return this._zclass == null ? "z-treecol" : this._zclass;
	},
	//@Override
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel(), {maxlength: this._maxlength});
	}
});

})();