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
			var n = this.$n();
			if (n && this.rows)
				this.rows.stripe();
		}
		return this;
	},
	rerender: function () {
		this.$supers('rerender', arguments);
		if (this.rows)
			this.rows._syncStripe();
		return this;
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
		else if (child.$instanceof(zul.mesh.Frozen))
			this.frozen = child;
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
		else if (child == this.frozen)
			this.frozen = null;
	},
	insertChildHTML_: function (child, before, desktop) {
		if (child.$instanceof(zul.grid.Rows)) {
			this.rows = child;
			if (this.ebodytbl) {
				jq(this.ebodytbl).append(child._redrawHTML());
				child.bind(desktop);
				return;
			}
		} 

		this.rerender();
	},

	getHeadWidgetClass: function () {
		return zul.grid.Columns;
	},
	getBodyWidgetIterator: function () {
		return new zul.grid.RowIter(this);
	}
});

zul.grid.RowIter = zk.$extends(zk.Object, {
	$init: function (grid) {
		this.grid = grid;
	},
	_init: function () {
		if (!this._isInit) {
			this._isInit = true;
			this.p = this.grid.rows ? this.grid.rows.firstChild: null;
		}
	},
	hasNext: function () {
		this._init();
		return this.p;
	},
	
	next: function () {
		this._init();
		var p = this.p;
		if (p) this.p = p.nextSibling;
		return p;
	}
});
