/* Grid.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:23:39     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Grid = zk.$extends(zul.mesh.MeshWidget, {
	getCell: function (row, col) {
		if (!this.rows) return null;
		if (rows.nChildren <= row) return null;

		var row = rows.getChildAt(row);
		return row.nChildren <= col ? null: row.getChildAt(col);
	},
	getOddRowSclass: function () {
		return this._scOddRow == null ? this.getZclass() + "-odd" : this._scOddRow;
	},
	setOddRowSclass: function (scls) {
		if (!scls) scls = null;
		if (this._scOddRow != scls) {
			this._scOddRow = scls;
			var n = this.getNode();
			if (n && this.rows)
				this.rows.stripe();
		}
	},

	//-- super --//
	getZclass: function () {
		return this._zclass == null ? "z-grid" : this._zclass;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.grid.Rows))
			this.rows = child;
		else if (child.$instanceof(zul.grid.Columns))
			this.columns = child;
		else if (child.$instanceof(zul.grid.Foot))
			this.foot = child;
		else if (child.$instanceof(zul.mesh.Paging))
			this.paging = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.rows)
			this.rows = null;
		else if (child == this.columns)
			this.columns = null;
		else if (child == this.foot)
			this.foot = null;
		else if (child == this.paging)
			this.paging = null;
	},
	insertChildHTML_: function (child, before, desktop) {
		if (child.$instanceof(zul.grid.Rows)) {
			this.rows = child;
			if (this.ebodytbl) {
				zDom.insertHTMLBeforeEnd(this.ebodytbl, child._redrawHTML());
				child.bind_(desktop);
				return;
			}
		} 

		this.rerender();
	},

	getHeadWidgetClass: function () {
		return zul.grid.Columns;
	},
	getBodyWidgetIterator: function () {
		return new zul.grid.BodyWidgetIterator(this);
	}
});

zul.grid.BodyWidgetIterator = zk.$extends(zk.Object, {
	$init: function (grid) {
		var rows = grid.rows;
		this.p = rows ? rows.firstChild: null;
	},
	hasNext: function () {
		return this.p;
	},
	next: function () {
		var p = this.p;
		if (p) this.p = p.nextSibling;
		return p;
	}
});
