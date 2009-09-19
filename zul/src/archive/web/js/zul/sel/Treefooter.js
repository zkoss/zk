/* Treefooter.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:42     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Treefooter = zk.$extends(zul.LabelImageWidget, {
	_span: 1,

	$define: {
		span: function (v) {
			var n = this.$n();
			if (n) n.colSpan = v;
		}
	},
	
	getTree: function () {
		return this.parent ? this.parent.parent : null;
	},
	getTreecol: function () {
		var tree = this.getTree();
		if (tree) {
			var cs = tree.treecols;
			if (cs)
				return cs.getChildAt(this.getChildIndex());
		}
		return null;
	},
	getZclass: function () {
		return this._zclass == null ? "z-treefooter" : this._zclass;
	}
});
