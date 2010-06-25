/* Grid.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:23:39     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The grid related widgets, such as grid and row.
 */
//zk.$package('zul.grid');

/**
 * A grid is an element that contains both rows and columns elements.
 * It is used to create a grid of elements.
 * Both the rows and columns are displayed at once although only one will
 * typically contain content, while the other may provide size information.
 *
 * <p>Default {@link #getZclass}: z-grid.
 *
 * <p>To have a grid without stripping, you can specify a non-existent
 * style class to {@link #setOddRowSclass}.
 *
 * 
 */
zul.grid.Grid = zk.$extends(zul.mesh.MeshWidget, {
	/** Returns the specified cell, or null if not available.
	 * @param int row which row to fetch (starting at 0).
	 * @param int col which column to fetch (starting at 0).
	 * @return zk.Widget
	 */	
	getCell: function (row, col) {
		if (!this.rows) return null;
		if (rows.nChildren <= row) return null;

		var row = rows.getChildAt(row);
		return row.nChildren <= col ? null: row.getChildAt(col);
	},
	/** Returns the style class for the odd rows.
	 * <p>Default: {@link #getZclass()}-odd.
	 * @return String
	 */
	getOddRowSclass: function () {
		return this._scOddRow == null ? this.getZclass() + "-odd" : this._scOddRow;
	},
	/** Sets the style class for the odd rows.
	 * If the style class doesn't exist, the striping effect disappears.
	 * You can provide different effects by providing the proper style
	 * classes.
	 * @param String scls
	 */
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
	insertBefore: function (child, sibling, ignoreDom) {
		if (this.$super('insertBefore', child, sibling, !this.z_rod)) {
			this._fixOnAdd(child, ignoreDom, ignoreDom);
			return true;
		}
	},
	appendChild: function (child, ignoreDom) {
		if (this.$super('appendChild', child, !this.z_rod)) {
			if (!this.insertingBefore_)
				this._fixOnAdd(child, ignoreDom, ignoreDom);
			return true;
		}
	},
	_fixOnAdd: function (child, ignoreDom, _noSync) {
		var isRows;
		if (child.$instanceof(zul.grid.Rows)) {
			this.rows = child;
			isRows = true;
		} else if (child.$instanceof(zul.grid.Columns)) 
			this.columns = child;
		else if (child.$instanceof(zul.grid.Foot)) 
			this.foot = child;
		else if (child.$instanceof(zul.mesh.Paging)) 
			this.paging = child;
		else if (child.$instanceof(zul.mesh.Frozen)) 
			this.frozen = child;

		if (!ignoreDom)
			this.rerender();
		if (!isRows && !_noSync)
			this._syncSize();  //sync-size required
	},
	onChildReplaced_: function (oldc, newc) {
		this.onChildRemoved_(oldc, true);
		this._fixOnAdd(newc, true); //_syncSize required
	},
	onChildRemoved_: function (child, _noSync) {
		this.$supers('onChildRemoved_', arguments);
		var isRows;
		if (child == this.rows) {
			this.rows = null;
			isRows = true;
		} else if (child == this.columns) 
			this.columns = null;
		else if (child == this.foot) 
			this.foot = null;
		else if (child == this.paging) 
			this.paging = null;
		else if (child == this.frozen) 
			this.frozen = null;

		if (!isRows && !_noSync)
			this._syncSize();
	},
	insertChildHTML_: function (child, before, desktop) {
		if (child.$instanceof(zul.grid.Rows)) {
			this.rows = child;
			var fakerows = this.$n('rows');
			if (fakerows) {
				jq(fakerows).replaceWith(child.redrawHTML_());
				child.bind(desktop);
				this.ebodyrows = child.$n().rows;
				return;
			} else {
				var tpad = this.$n('tpad');
				if (tpad) {
					jq(tpad).after(child.redrawHTML_());
					child.bind(desktop);
					this.ebodyrows = child.$n().rows;
					return;
				} else if (this.ebodytbl) {
					jq(this.ebodytbl).append(child.redrawHTML_());
					child.bind(desktop);
					this.ebodyrows = child.$n().rows;
					return;
				}
			}
		}

		this.rerender();
	},
	/**
	 * Returns the head widget class.
	 * @return zul.grid.Columns
	 */
	getHeadWidgetClass: function () {
		return zul.grid.Columns;
	},
	/**
	 * Returns the tree item iterator.
	 * @return zul.grid.RowIter
	 */
	getBodyWidgetIterator: function () {
		return new zul.grid.RowIter(this);
	}
});
/**
 * The row iterator.
 * @disable(zkgwt)
 */
zul.grid.RowIter = zk.$extends(zk.Object, {
	/** Constructor
	 * @param Grid grid the widget that the iterator belongs to
	 */
	$init: function (grid) {
		this.grid = grid;
	},
	_init: function () {
		if (!this._isInit) {
			this._isInit = true;
			this.p = this.grid.rows ? this.grid.rows.firstChild: null;
		}
	},
	 /**
     * Returns <tt>true</tt> if the iteration has more elements
     * @return boolean
     */
	hasNext: function () {
		this._init();
		return this.p;
	},
	/**
     * Returns the next element in the iteration.
     *
     * @return Row the next element in the iteration.
     */
	next: function () {
		this._init();
		var p = this.p;
		if (p) this.p = p.nextSibling;
		return p;
	}
});
