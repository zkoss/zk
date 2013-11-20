/* Grid.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:23:39     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	// fix for the empty message shows up or not.
	function _fixForEmpty(wgt) {
		if (wgt.desktop) {
			var $jq = jq(wgt.$n('empty')),
				colspan = 0;
			if (wgt.rows && wgt.rows.nChildren) {
				$jq.hide();
			} else {
				if (wgt.columns) {
					for (var w = wgt.columns.firstChild; w; w = w.nextSibling)
						if (w.isVisible())
							colspan++;
				}
				$jq.attr('colspan', colspan || 1);
				$jq.show();
			}
		}
		wgt._shallFixEmpty = false;
	}

var Grid =
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
 */
zul.grid.Grid = zk.$extends(zul.mesh.MeshWidget, {
	_scrollbar: null,
	_firstLoad: true,
	$define: {
		emptyMessage: function(msg) {
			if(this.desktop)
				jq(this.$n('empty')).html(msg);
		}
	},
	/** Returns the specified cell, or null if not available.
	 * @param int row which row to fetch (starting at 0).
	 * @param int col which column to fetch (starting at 0).
	 * @return zk.Widget
	 */	
	getCell: function (row, col) {
		var rows;
		if (!(rows = this.rows))
			return null;
		
		if (rows.nChildren <= row)
			return null;
		
		var row = rows.getChildAt(row);
		return row.nChildren <= col ? null: row.getChildAt(col);
	},
	/** Returns the style class for the odd rows.
	 * <p>Default: {@link #getZclass()}-odd.
	 * @return String
	 */
	getOddRowSclass: function () {
		return this._scOddRow == null ? this.$s('odd') : this._scOddRow;
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
		this.$supers(Grid, 'rerender', arguments);
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
		if (!_noSync) //bug#3301498: we have to sync even if child is rows
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
		else if (child == this.frozen) {
			this.frozen = null;
			this.destroyBar_();
		}
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
		out.push('<tbody class="', this.$s('emptybody'), '"><tr><td id="'
				, this.uuid, '-empty" style="display:none">',
				this._emptyMessage ,'</td></tr></tbody>');
	},
	bind_: function (desktop, skipper, after) {
		this.$supers(Grid, 'bind_', arguments);
		var w = this;
		after.push(function() {
			_fixForEmpty(w);
		});
	},
	unbind_: function () {
		this.destroyBar_();
		this.$supers(Grid, 'unbind_', arguments);
	},
	onSize: function () {
		this.$supers(Grid, 'onSize', arguments);
		var self = this,
			canInitScrollbar = this.desktop && !this._nativebar;
		if (!this._scrollbar && canInitScrollbar)
			this._scrollbar = zul.mesh.Scrollbar.init(this); // 1823278: should show scroll bar here
		setTimeout(function () {
			if (canInitScrollbar) {
				if (!self._grid$rod || self.inPagingMold())
					self.refreshBar_();
			}
		}, 200);
	},
	refreshBar_: function (showBar, scrollToTop) {
		var bar = this._scrollbar;
		if (bar) {
			bar.syncSize(showBar || this._shallShowScrollbar);
			this._shallShowScrollbar = false;
			if (scrollToTop)
				bar.scrollTo(0, 0);
			else
				bar.scrollTo(this._currentLeft, this._currentTop);
			
			//sync frozen
			var frozen = this.frozen,
				start;
			if (frozen && (start = frozen._start) != 0) {
				frozen._doScrollNow(start);
				bar.setBarPosition(start);
			}
		}
	},
	destroyBar_: function () {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			bar = this._scrollbar = null;
		}
	},
	_onRender: function () {
		this.$supers(Grid, '_onRender', arguments);
		if (!this._firstLoad)
			this._shallShowScrollbar = true;
		
		this._firstLoad = false;
	},
	onResponse: function (ctl, opts) {
		if (this.desktop) {
			if (this._shallFixEmpty) 
				_fixForEmpty(this);
			var rtags = opts ? opts.rtags : null;
			if (rtags && rtags.onDataLoading)
				this._shallShowScrollbar = true;
		}
		this.$supers(Grid, 'onResponse', arguments);
	},
	// this function is used for Grid, Rows, and Columns
	_syncEmpty: function () {
		this._shallFixEmpty = true;
	},
	onChildAdded_: function(child) {
		this.$supers(Grid, 'onChildAdded_', arguments);
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
				this.ebodyrows = child.$n();
				return;
			} else {
				var tpad = this.$n('tpad');
				if (tpad) {
					jq(tpad).after(child.redrawHTML_());
					child.bind(desktop);
					this.ebodyrows = child.$n();
					return;
				} else if (this.ebodytbl) {
					jq(this.ebodytbl).append(child.redrawHTML_());
					child.bind(desktop);
					this.ebodyrows = child.$n();
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
	},
	/**
	 * Returns whether the grid has group.
	 * @since 6.5.0
	 * @return boolean
	 */
	hasGroup: function () {
		return this.rows && this.rows.hasGroup();
	}
});
})();

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