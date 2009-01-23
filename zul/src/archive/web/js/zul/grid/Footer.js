/* Footer.js

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 12:26:51     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Footer = zk.$extends(zul.LabelImageWidget, {
	_span: 1,
	
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
	getSpan: function () {
		return this._span;
	},
	setSpan: function (span) {
		if (this._span != span) {
			this._span = span;
			var n = this.getNode();
			if (n) n.colspan = span;
		}
	},
	getZclass: function () {
		return this._zclass == null ? "z-footer" : this._zclass;
	}
});