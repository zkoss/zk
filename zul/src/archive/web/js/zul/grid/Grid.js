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

/** @class zul.grid.Renderer
 * The renderer used to render a grid.
 * It is designed to be overriden
 */
zul.grid.Renderer = {
	/** Update the size of the column menu button when mouse over
	 * 
	 * @param zul.grid.Column col the column
	 */
	updateColumnMenuButton: function (col) {
		var btn;
		if (btn = col.$n('btn')) btn.style.height = col.$n().offsetHeight - 1 + "px";
	}
};

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
	$define:{
		emptyMessage:function(msg){
			if(this.desktop) jq("td",this.$n("empty")).html(msg);
		}
	},
	/** Returns the specified cell, or null if not available.
	 * @param int row which row to fetch (starting at 0).
	 * @param int col which column to fetch (starting at 0).
	 * @return zk.Widget
	 */	
	getCell: function (row, col) {
		var rows;
		if (!(rows = this.rows)) return null;
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
	getFocusCell: function (el) {
		var tbody = this.rows.$n();
		if (jq.isAncestor(tbody, el)) {
			var tds = jq(el).parents('td'), td;
			for (var i = 0, j = tds.length; i < j; i++) {
				td = tds[i];
				if (td.parentNode.parentNode == tbody) {
					return td;
				}
			}
		}
	},
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
		if (child.$instanceof(zul.grid.Rows)) {
			this.rows = child;
			this._syncEmpty();
		} else if (child.$instanceof(zul.grid.Columns)) {
			this.columns = child;
			this._syncEmpty();
		} else if (child.$instanceof(zul.grid.Foot)) 
			this.foot = child;
		else if (child.$instanceof(zul.mesh.Paging)) 
			this.paging = child;
		else if (child.$instanceof(zul.mesh.Frozen)) 
			this.frozen = child;

		if (!ignoreDom)
			this.rerender();
		if (!_noSync)//bug#3301498: we have to sync even if child is rows
			this._syncSize();  //sync-size required
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);

		var isRows;
		if (child == this.rows) {
			this.rows = null;
			isRows = true;
			this._syncEmpty();
		} else if (child == this.columns) {
			this.columns = null;
			this._syncEmpty();
		} else if (child == this.foot) 
			this.foot = null;
		else if (child == this.paging) 
			this.paging = null;
		else if (child == this.frozen) 
			this.frozen = null;

		if (!isRows && !this.childReplacing_) //not called by onChildReplaced_
			this._syncSize();
	},
	/**
	 * a redraw method for the empty message , if you want to customize the message ,
	 * you could overwrite this.
	 * @param Array out A array that contains html structure ,
	 * 			it usually come from mold(redraw_). 
	 */
	redrawEmpty_: function (out) {
		var uuid = this.uuid, zcls = this.getZclass();
		out.push('<tbody id="', uuid, '-empty" class="', zcls,
				'-empty-body" style="display:none"><tr><td>',
				this._emptyMessage ,'</td></tr></tbody>');
	},
	bind_: function (desktop, skipper, after) {
		this.$supers(zul.grid.Grid, 'bind_', arguments);
		var w = this;
		after.push(function() {
			w._fixForEmpty();
		});
	},
	onResponse: function () {
		if (this._shallFixEmpty) 
			this._fixForEmpty();
		this.$supers('onResponse', arguments);
	},
	// this function is used for Grid, Rows, and Columns
	_syncEmpty: function () {
		this._shallFixEmpty = true;
	},
	// fix for the empty message shows up or now.
	_fixForEmpty: function () {
		if (this.desktop) {
			if (this.rows && this.rows.nChildren) {
				jq(this.$n("empty")).hide().find("td").attr("colspan", 1); // colspan 1 fixed IE7 issue ZK-528
			} else {
				var $jq = jq(this.$n("empty")),
					colspan = 0;
				if (this.columns) {
					for (var w = this.columns.firstChild; w; w = w.nextSibling)
						if (w.isVisible())
							colspan++;
				}
				
				$jq.find("td").attr("colspan", colspan || 1);
				$jq.show();
			}
		}
		this._shallFixEmpty = false;
	},	
	onChildAdded_: function(child) {
		this.$supers('onChildAdded_', arguments);
		if (this.childReplacing_) //called by onChildReplaced_
			this._fixOnAdd(child, true); //_syncSize required
		//else handled by insertBefore/appendChild
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
	getBodyWidgetIterator: function (opts) {
		return new zul.grid.RowIter(this, opts);
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
	$init: function (grid, opts) {
		this.grid = grid;
		this.opts = opts;
	},
	_init: function () {
		if (!this._isInit) {
			this._isInit = true;
			var p = this.grid.rows ? this.grid.rows.firstChild: null;
			if (this.opts && this.opts.skipHidden)
				for (; p && !p.isVisible(); p = p.nextSibling) {}
			this.p = p;
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
		var p = this.p,
			q = p ? p.nextSibling : null;
		if (this.opts && this.opts.skipHidden)
			for (; q && !q.isVisible(); q = q.nextSibling) {}
		if (p) 
			this.p = q;
		return p;
	}
});