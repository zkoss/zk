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
			var empty = wgt.$n('empty'),
				colspan = 0;
			if (wgt.rows && wgt.rows.nChildren) {
				empty.style.display = 'none';
			} else {
				if (wgt.columns) {
					for (var w = wgt.columns.firstChild; w; w = w.nextSibling)
							colspan++;
				}
				empty.colSpan = colspan || 1;
				// ZK-2365 table cell needs the "display:table-cell" when colspan is enable.
				empty.style.display = 'table-cell';
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
	$define: {
		/**
		 * Returns the message to display when there are no items
		 * @return String
		 * @since 5.0.7
		 */
		/**
		 * Sets the message to display when there are no items
		 * @param String msg
		 * @since 5.0.7
		 */
		emptyMessage: function (msg) {
			if (this.desktop) {
				var emptyContentDiv = jq(this.$n('empty-content')),
					emptyContentClz = this.$s('emptybody-content');
				if (msg && msg.trim().length != 0)
					emptyContentDiv.addClass(emptyContentClz);
				else
					emptyContentDiv.removeClass(emptyContentClz);
				emptyContentDiv.text(msg);
			}
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
		return row.nChildren <= col ? null : row.getChildAt(col);
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
		out.push('<tbody class="', this.$s('emptybody'), '"><tr><td id="',
				this.uuid, '-empty" style="display:none">',
				'<div id="', this.uuid, '-empty-content"');
		if (this._emptyMessage && this._emptyMessage.trim().length != 0)
			out.push('class="', this.$s('emptybody-content'), '"');
		out.push('>', zUtl.encodeXML(this._emptyMessage), '</div></td></tr></tbody>');
	},
	bind_: function (desktop, skipper, after) {
		this.$supers(Grid, 'bind_', arguments);
		var w = this;
		after.push(function () {
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
		// refix ZK-2840: only init scrollbar when height or vflex is set in mobile
		if (!this._scrollbar && canInitScrollbar) {
			if (!zk.mobile || (zk.mobile && (this.getHeight() || this.getVflex()))) {
				this._scrollbar = zul.mesh.Scrollbar.init(this); // 1823278: should show scroll bar here
			}
		}
		setTimeout(function () {
			if (canInitScrollbar) {
				self.refreshBar_();
			}
		}, 200);
	},
	destroyBar_: function () {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			bar = this._scrollbar = null;
		}
	},
	onResponse: function (ctl, opts) {
		if (this.desktop) {
			if (this._shallFixEmpty)
				_fixForEmpty(this);
		}
		this.$supers(Grid, 'onResponse', arguments);
	},
	// this function is used for Grid, Rows, and Columns
	_syncEmpty: function () {
		this._shallFixEmpty = true;
	},
	onChildAdded_: function (child) {
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
	},
	/**
	 * Scroll to the specified row by the given index.
	 * @param int index the index of row
	 * @param double scrollRatio the scroll ratio
	 * @since 8.5.2
	 */
	scrollToIndex: function (index, scrollRatio) {
		var self = this;
		this.waitForRendered_().then(function () {
			self._scrollToIndex(index, scrollRatio);
		});
	},
	_getFirstItemIndex: function () {
		return this.rows.firstChild._index;
	},
	_getLastItemIndex: function () {
		return this.rows.lastChild._index;
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
			var p = this.grid.rows ? this.grid.rows.firstChild : null;
			if (this.opts && this.opts.skipHidden)
				for (; p && !p.isVisible(); p = p.nextSibling) { /* empty */ }
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
			for (; q && !q.isVisible(); q = q.nextSibling) { /* empty */ }
		if (p)
			this.p = q;
		return p;
	}
});
