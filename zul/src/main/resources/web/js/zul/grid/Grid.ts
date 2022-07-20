/* Grid.ts

	Purpose:

	Description:

	History:
		Tue Dec 23 15:23:39     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
// fix for the empty message shows up or not.
function _fixForEmpty(wgt: zul.grid.Grid): void {
	if (wgt.desktop) {
		var empty = wgt.$n_<HTMLTableCellElement>('empty'),
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

@zk.WrapClass('zul.grid.Grid')
export class Grid extends zul.mesh.MeshWidget {
	override _scrollbar: zul.Scrollbar | null = null;
	_grid$rod?: boolean; // zkex.grid.Group
	_fixhdwcnt?: number; // zkex.grid.Detail
	_fixhdoldwd?: number; // zkex.grid.Detail
	_emptyMessage?: string;
	rows?: zul.grid.Rows | null;
	columns?: zul.grid.Columns | null;
	_shallFixEmpty?: boolean;
	_scOddRow?: string | null;
	// Prevent name clash with inherited method `_visibleRows`. Fortunately, Java
	// calls its setter, so this renaming is safe. See `renderProperties`
	// in `Grid.java`.
	_visibleRows_?: number;

	/**
	 * Returns the message to display when there are no items
	 * @return String
	 * @since 5.0.7
	 */
	getEmptyMessage(): string | undefined {
		return this._emptyMessage;
	}

	/**
	 * Sets the message to display when there are no items
	 * @param String msg
	 * @since 5.0.7
	 */
	setEmptyMessage(msg: string, opts?: Record<string, boolean>): this {
		const o = this._emptyMessage;
		this._emptyMessage = msg;

		if (o !== msg || (opts && opts.force)) {
			if (this.desktop) {
				var emptyContentDiv = jq(this.$n_('empty-content')),
					emptyContentClz = this.$s('emptybody-content');
				if (msg && msg.trim().length != 0)
					emptyContentDiv.addClass(emptyContentClz);
				else
					emptyContentDiv.removeClass(emptyContentClz);
				emptyContentDiv.html(msg);
			}
		}

		return this;
	}

	getVisibleRows(): number | undefined {
		return this._visibleRows_;
	}

	/** Sets the visible rows.
	 * Not allowed to set visibleRows and height/vflex at the same time.
	 * @param int rows
	 * @since 10.0.0
	 */
	setVisibleRows(rows: number, opts?: Record<string, boolean>): this {
		const o = this._visibleRows_;
		this._visibleRows_ = rows;

		if (o !== rows || (opts && opts.force)) {
			this.setRows(rows);
		}

		return this;
	}

	/** Returns the specified cell, or null if not available.
	 * @param int row which row to fetch (starting at 0).
	 * @param int col which column to fetch (starting at 0).
	 * @return zk.Widget
	 */
	getCell(row: number, col: number): zk.Widget | null | undefined {
		const rows = this.rows;
		if (!rows)
			return null;

		if (rows.nChildren <= row)
			return null;

		const gridRow = rows.getChildAt<zul.grid.Row>(row)!;
		return gridRow.nChildren <= col ? null : gridRow.getChildAt(col);
	}

	/** Returns the style class for the odd rows.
	 * <p>Default: {@link #getZclass()}-odd.
	 * @return String
	 */
	getOddRowSclass(): string {
		return this._scOddRow == null ? this.$s('odd') : this._scOddRow;
	}

	/** Sets the style class for the odd rows.
	 * If the style class doesn't exist, the striping effect disappears.
	 * You can provide different effects by providing the proper style
	 * classes.
	 * @param String scls
	 */
	setOddRowSclass(sclass: string): this {
		const scls = sclass || null;
		if (this._scOddRow != scls) {
			this._scOddRow = scls;
			var n = this.$n();
			if (n && this.rows)
				this.rows.stripe();
		}
		return this;
	}

	override rerender(skipper?: zk.Skipper | number | null): this {
		super.rerender(skipper);
		if (this.rows)
			this.rows._syncStripe();
		return this;
	}

	override insertBefore(child: zk.Widget, sibling: zk.Widget | null | undefined, ignoreDom?: boolean): boolean {
		if (super.insertBefore(child, sibling, !this.z_rod)) {
			this._fixOnAdd(child, ignoreDom, ignoreDom);
			return true;
		}
		return false;
	}

	override appendChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		if (super.appendChild(child, !this.z_rod)) {
			if (!this.insertingBefore_)
				this._fixOnAdd(child, ignoreDom, ignoreDom);
			return true;
		}
		return false;
	}

	_fixOnAdd(child: zk.Widget, ignoreDom?: boolean, _noSync?: boolean): void {
		if (child instanceof zul.grid.Rows) {
			this.rows = child;
			this._syncEmpty();
		} else if (child instanceof zul.grid.Columns) {
			this.columns = child;
			this._syncEmpty();
		} else if (child instanceof zul.grid.Foot)
			this.foot = child;
		else if (child instanceof zul.mesh.Paging) {
			this.paging = child;
			this.paging.setMeshWidget(this);
		} else if (child instanceof zul.mesh.Frozen)
			this.frozen = child;

		if (!ignoreDom)
			this.rerender();
		if (!_noSync) //bug#3301498: we have to sync even if child is rows
			this._syncSize();  //sync-size required
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);

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
		else if (child == this.paging) {
			this.paging.setMeshWidget(null);
			this.paging = null;
		} else if (child == this.frozen) {
			this.frozen = null;
			this.destroyBar_();
		}
		if (!isRows && !this.childReplacing_) //not called by onChildReplaced_
			this._syncSize();
	}

	/**
	 * a redraw method for the empty message , if you want to customize the message ,
	 * you could overwrite this.
	 * @param Array out A array that contains html structure ,
	 * 			it usually come from mold(redraw_).
	 */
	redrawEmpty_(out: string[]): void {
		out.push('<tbody class="', this.$s('emptybody'), '"><tr><td id="',
			this.uuid, '-empty" style="display:none">',
			'<div id="', this.uuid, '-empty-content"');
		if (this._emptyMessage && this._emptyMessage.trim().length != 0)
			out.push('class="', this.$s('emptybody-content'), '"');
		out.push('>', this._emptyMessage!, '</div></td></tr></tbody>');
	}

	override bind_(desktop: zk.Desktop | null | undefined, skipper: zk.Skipper | null | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var w = this;
		after.push(function () {
			_fixForEmpty(w);
		});
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		this.destroyBar_();
		super.unbind_(skipper, after, keepRod);
	}

	override onSize(): void {
		super.onSize();
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
	}

	destroyBar_(): void {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			bar = this._scrollbar = null;
		}
	}

	override onResponse(ctl?: zk.ZWatchController, opts?: Record<string, unknown>): void {
		if (this.desktop) {
			if (this._shallFixEmpty)
				_fixForEmpty(this);
		}
		super.onResponse(ctl, opts);
	}

	override _syncEmpty(): void {
		this._shallFixEmpty = true;
	}

	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (this.childReplacing_) //called by onChildReplaced_
			this._fixOnAdd(child, true); //_syncSize required
		//else handled by insertBefore/appendChild
	}

	override insertChildHTML_(child: zk.Widget, before?: zk.Widget | null, desktop?: zk.Desktop | null): void {
		if (child instanceof zul.grid.Rows) {
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
	}

	/**
	 * Returns the head widget class.
	 * @return zul.grid.Columns
	 */
	getHeadWidgetClass(): typeof zul.grid.Columns {
		return zul.grid.Columns;
	}

	/**
	 * Returns the tree item iterator.
	 * @return zul.grid.RowIter
	 */
	getBodyWidgetIterator(opts?: Record<string, unknown>): zul.grid.RowIter {
		return new zul.grid.RowIter(this, opts);
	}
	itemIterator = Grid.prototype.getBodyWidgetIterator;

	/**
	 * Returns whether the grid has group.
	 * @since 6.5.0
	 * @return boolean
	 */
	hasGroup(): boolean | undefined {
		return this.rows?.hasGroup();
	}

	/**
	 * Scroll to the specified row by the given index.
	 * @param int index the index of row
	 * @param double scrollRatio the scroll ratio
	 * @since 8.5.2
	 */
	scrollToIndex(index: number, scrollRatio: number): void {
		var self = this;
		void this.waitForRendered_().then(function () {
			self._scrollToIndex(index, scrollRatio);
		});
	}

	_getFirstItemIndex(): number | undefined {
		return this.rows!.firstChild!._index;
	}

	_getLastItemIndex(): number | undefined {
		return this.rows!.lastChild!._index;
	}
}

/**
 * The row iterator.
 * @disable(zkgwt)
 */
@zk.WrapClass('zul.grid.RowIter')
export class RowIter extends zk.Object implements zul.mesh.ItemIterator {
	grid: zul.grid.Grid;
	opts?: Record<string, unknown>;
	_isInit?: boolean;
	p?: zul.grid.Row | null;

	/** Constructor
	 * @param Grid grid the widget that the iterator belongs to
	 */
	constructor(grid: zul.grid.Grid, opts?: Record<string, unknown>) {
		super();
		this.grid = grid;
		this.opts = opts;
	}

	_init(): void {
		if (!this._isInit) {
			this._isInit = true;
			var p = this.grid.rows ? this.grid.rows.firstChild : null;
			if (this.opts && this.opts.skipHidden)
				for (; p && !p.isVisible(); p = p.nextSibling) { /* empty */ }
			this.p = p;
		}
	}

	/**
	* Returns <tt>true</tt> if the iteration has more elements
	* @return boolean
	*/
	hasNext(): boolean {
		this._init();
		return !!this.p;
	}

	/**
	 * Returns the next element in the iteration.
	 *
	 * @return Row the next element in the iteration.
	 */
	next(): zul.grid.Row | null | undefined {
		this._init();
		var p = this.p,
			q = p ? p.nextSibling : null;
		if (this.opts && this.opts.skipHidden)
			for (; q && !q.isVisible(); q = q.nextSibling) { /* empty */ }
		if (p)
			this.p = q;
		return p;
	}
}