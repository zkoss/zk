/* Footer.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 12:26:51     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Footer = zk.$extends(zul.LabelImageWidget, {
	_span: 1,

	$define: {
		span: function (v) {
			var n = this.$n();
			if (n) n.colSpan = v;
		}
	},
	
	getGrid: function () {
		return this.parent ? this.parent.parent : null;
	},
	getColumn: function () {
		var grid = this.getGrid();
		if (grid) {
			var cs = grid.columns;
			if (cs)
				return cs.getChildAt(this.getChildIndex());
		}
		return null;
	},
	getZclass: function () {
		return this._zclass == null ? "z-footer" : this._zclass;
	}
});