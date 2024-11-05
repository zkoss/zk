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
		if (wgt.rows?.nChildren) {
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
	/** @internal */
	_grid$rod?: boolean; // zkex.grid.Group
	/** @internal */
	_fixhdwcnt?: number; // zkex.grid.Detail
	/** @internal */
	_fixhdoldwd?: number; // zkex.grid.Detail
	/** @internal */
	_emptyMessage?: string;
	rows?: zul.grid.Rows;
	columns?: zul.grid.Columns;
	/** @internal */
	_shallFixEmpty?: boolean;
	/** @internal */
	_scOddRow?: string;
	// Prevent name clash with inherited method `_visibleRows`. Fortunately, Java
	// calls its setter, so this renaming is safe. See `renderProperties`
	// in `Grid.java`.
	/** @internal */
	_visibleRows_?: number;

	/**
	 * @returns the message to display when there are no items
	 * @since 5.0.7
	 */
	getEmptyMessage(): string | undefined {
		return this._emptyMessage;
	}

	/**
	 * Sets the message to display when there are no items
	 * @since 5.0.7
	 */
	setEmptyMessage(emptyMessage: string, opts?: Record<string, boolean>): this {
		const o = this._emptyMessage;
		this._emptyMessage = emptyMessage;

		if (o !== emptyMessage || opts?.force) {
			if (this.desktop) {
				var emptyContentDiv = jq(this.$n_('empty-content')),
					emptyContentClz = this.$s('emptybody-content');
				if (emptyMessage && emptyMessage.trim().length != 0)
					emptyContentDiv.addClass(emptyContentClz);
				else
					emptyContentDiv.removeClass(emptyContentClz);
				emptyContentDiv.text(emptyMessage);
			}
		}

		return this;
	}

	getVisibleRows(): number | undefined {
		return this._visibleRows_;
	}

	/**
	 * Sets the visible rows.
	 * Not allowed to set visibleRows and height/vflex at the same time.
	 * @since 10.0.0
	 */
	setVisibleRows(visibleRows: number, opts?: Record<string, boolean>): this {
		const o = this._visibleRows_;
		this._visibleRows_ = visibleRows;

		if (o !== visibleRows || opts?.force) {
			this.setRows(visibleRows);
		}

		return this;
	}

	/**
	 * @returns the specified cell, or null if not available.
	 * @param row - which row to fetch (starting at 0).
	 * @param col - which column to fetch (starting at 0).
	 */
	getCell(row: number, col: number): zk.Widget | undefined {
		const rows = this.rows;
		if (!rows)
			return undefined;

		if (rows.nChildren <= row)
			return undefined;

		const gridRow = rows.getChildAt<zul.grid.Row>(row)!;
		return gridRow.nChildren <= col ? undefined : gridRow.getChildAt(col);
	}

	/**
	 * @returns the style class for the odd rows.
	 * @defaultValue {@link getZclass}-odd.
	 */
	getOddRowSclass(): string {
		return this._scOddRow == null ? this.$s('odd') : this._scOddRow;
	}

	/**
	 * Sets the style class for the odd rows.
	 * If the style class doesn't exist, the striping effect disappears.
	 * You can provide different effects by providing the proper style
	 * classes.
	 */
	setOddRowSclass(oddRowSclass: string): this {
		const scls = oddRowSclass;
		if (this._scOddRow != scls) {
			this._scOddRow = scls;
			var n = this.$n();
			if (n && this.rows)
				this.rows.stripe();
		}
		return this;
	}

	override rerender(skipper?: zk.Skipper | number): this {
		super.rerender(skipper);
		if (this.rows)
			this.rows._syncStripe();
		return this;
	}

	override insertBefore(child: zk.Widget, sibling: zk.Widget | undefined, ignoreDom?: boolean): boolean {
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

	/** @internal */
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

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);

		var isRows;
		if (child == this.rows) {
			this.rows = undefined;
			isRows = true;
			this._syncEmpty();
		} else if (child == this.columns) {
			this.columns = undefined;
			this._syncEmpty();
		} else if (child == this.foot)
			this.foot = undefined;
		else if (child == this.paging) {
			this.paging.setMeshWidget(undefined);
			this.paging = undefined;
		} else if (child == this.frozen) {
			this.frozen = undefined;
			this.destroyBar_();
		}
		if (!isRows && !this.childReplacing_) //not called by onChildReplaced_
			this._syncSize();
	}

	/**
	 * A redraw method for the empty message, if you want to customize the message,
	 * you could overwrite this.
	 * @param out - An array that contains html structure, it usually come from `mold(redraw_)`.
	 * @internal
	 */
	redrawEmpty_(out: string[]): void {
		out.push('<tbody class="', this.$s('emptybody'), '"><tr><td id="',
			this.uuid, '-empty" style="display:none">',
			'<div id="', this.uuid, '-empty-content"');
		if (this._emptyMessage && this._emptyMessage.trim().length != 0)
			out.push('class="', this.$s('emptybody-content'), '"');
		out.push('>', zUtl.encodeXML(this._emptyMessage!), '</div></td></tr></tbody>');
	}

	/** @internal */
	override bind_(desktop: zk.Desktop | undefined, skipper: zk.Skipper | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		after.push(() => {
			_fixForEmpty(this);
		});
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this.destroyBar_();
		super.unbind_(skipper, after, keepRod);
	}

	override onSize(): void {
		super.onSize();
		var canInitScrollbar = this.desktop && !this._nativebar;
		if (!this._scrollbar && canInitScrollbar) {
			this._scrollbar = zul.mesh.Scrollbar.init(this); // 1823278: should show scroll bar here
		}
		setTimeout(() => {
			if (canInitScrollbar) {
				this.refreshBar_();
			}
		}, 200);
	}

	/** @internal */
	destroyBar_(): void {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			bar = this._scrollbar = undefined;
		}
	}

	override onResponse(ctl?: zk.ZWatchController, opts?: Record<string, unknown>): void {
		if (this.desktop) {
			if (this._shallFixEmpty)
				_fixForEmpty(this);
		}
		super.onResponse(ctl, opts);
	}

	/** @internal */
	override _syncEmpty(): void {
		this._shallFixEmpty = true;
	}
	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (this.childReplacing_) //called by onChildReplaced_
			this._fixOnAdd(child, true); //_syncSize required
		//else handled by insertBefore/appendChild
	}

	/** @internal */
	override insertChildHTML_(child: zk.Widget, before?: zk.Widget, desktop?: zk.Desktop): void {
		if (child instanceof zul.grid.Rows) {
			this.rows = child;
			var fakerows = this.$n('rows');
			if (fakerows) {
				jq(fakerows).replaceWith(/*safe*/ child.redrawHTML_());
				child.bind(desktop);
				this.ebodyrows = child.$n();
				return;
			} else {
				var tpad = this.$n('tpad');
				if (tpad) {
					jq(tpad).after(/*safe*/ child.redrawHTML_());
					child.bind(desktop);
					this.ebodyrows = child.$n();
					return;
				} else if (this.ebodytbl) {
					jq(this.ebodytbl).append(/*safe*/ child.redrawHTML_());
					child.bind(desktop);
					this.ebodyrows = child.$n();
					return;
				}
			}
		}

		this.rerender();
	}

	/**
	 * @returns the head widget class.
	 */
	getHeadWidgetClass(): typeof zul.grid.Columns {
		return zul.grid.Columns;
	}

	/**
	 * @returns the tree item iterator.
	 */
	getBodyWidgetIterator(opts?: Record<string, unknown>): zul.grid.RowIter {
		return new zul.grid.RowIter(this, opts);
	}
	itemIterator = Grid.prototype.getBodyWidgetIterator;

	/**
	 * @returns whether the grid has group.
	 * @since 6.5.0
	 */
	hasGroup(): boolean {
		return !!this.rows?.hasGroup();
	}

	/**
	 * Scroll to the specified row by the given index.
	 * @param index - the index of row
	 * @param scrollRatio - the scroll ratio
	 * @since 8.5.2
	 */
	scrollToIndex(index: number, scrollRatio: number): void {
		void this.waitForRendered_().then(() => {
			this._scrollToIndex(index, scrollRatio);
		});
	}

	/** @internal */
	_getFirstItemIndex(): number | undefined {
		return this.rows!.firstChild!._index;
	}

	/** @internal */
	_getLastItemIndex(): number | undefined {
		return this.rows!.lastChild!._index;
	}
}

/**
 * The row iterator.
 */
@zk.WrapClass('zul.grid.RowIter')
export class RowIter extends zk.Object implements zul.mesh.ItemIterator {
	grid: zul.grid.Grid;
	opts?: Record<string, unknown>;
	/** @internal */
	_isInit?: boolean;
	p?: zul.grid.Row;
	length?: number;

	/**
	 * @param grid - the widget that the iterator belongs to
	 */
	constructor(grid: zul.grid.Grid, opts?: Record<string, unknown>) {
		super();
		this.grid = grid;
		this.opts = opts;
	}

	/** @internal */
	_init(): void {
		if (!this._isInit) {
			this._isInit = true;
			var p = this.grid.rows ? this.grid.rows.firstChild : undefined;
			if (this.opts?.skipHidden)
				for (; p && !p.isVisible(); p = p.nextSibling) { /* empty */ }
			this.p = p;
		}
	}

	/**
	* @returns `true` if the iteration has more elements
	*/
	hasNext(): boolean {
		this._init();
		return !!this.p;
	}

	/**
	 * @returns the next element in the iteration.
	 */
	next(): zul.grid.Row | undefined {
		this._init();
		var p = this.p,
			q = p ? p.nextSibling : undefined;
		if (this.opts?.skipHidden)
			for (; q && !q.isVisible(); q = q.nextSibling) { /* empty */ }
		if (p)
			this.p = q;
		return p;
	}
}