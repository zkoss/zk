/* Tree.ts

	Purpose:

	Description:

	History:
		Wed Jun 10 16:32:29     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 *  A container which can be used to hold a tabular
 * or hierarchical set of rows of elements.
 *
 * <p>Event:
 * <ol>
 * <li>onSelect event is sent when user changes the selection.</li>
 * </ol>
 *
 * <p>Default {@link #getZclass}: z-tree.
 */
@zk.WrapClass('zul.sel.Tree')
export class Tree extends zul.sel.SelectWidget {
	public override _selItems!: zul.sel.Treeitem[]; // initialized in super constructor
	public override _scrollbar: zul.Scrollbar | null = null;
	public _barPos = null;
	public treecols?: zul.sel.Treecols | null;
	public treefoot?: zul.sel.Treefoot | null;
	public treechildren?: zul.sel.Treechildren | null;
	public _shallSyncFrozen?: boolean;
	public _tree$noSelectAll?: boolean;
	private _sel?: zul.sel.Treeitem | null;
	public _fixhdwcnt?: number; // zul.sel.Treeitem.prototype.setOpen
	public _fixhdoldwd?: number; // zul.sel.Treeitem.prototype.setOpen

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		this.destroyBar_();
		super.unbind_(skipper, after, keepRod);
	}

	public override onSize(): void {
		super.onSize();
		var self = this,
			frozen = this.frozen;
		if (this._shallSyncFrozen && frozen && this._nativebar) {
			frozen.onSize();
			this._shallSyncFrozen = false;
		}
		setTimeout(function () {
			if (self.desktop && !self._nativebar) {
				if (!self._scrollbar)
					self._scrollbar = zul.mesh.Scrollbar.init(self);
				self.refreshBar_();
			}
		}, 200);
	}

	protected override refreshBar_(showBar?: boolean, scrollToTop?: boolean): void {
		var bar = this._scrollbar;
		if (bar) {
			var scrollPosition: {l: number; t: number} | null | undefined;
			if (this._currentLeft || this._currentTop) {
				scrollPosition = {l: this._currentLeft, t: this._currentTop};
			}
			//open/close tree node in paging mold will invalidate
			//  keep scroll position before sync scrollbar size
			if (this.inPagingMold() && scrollPosition) {
				showBar = true;
			}
			bar.syncSize(showBar || this._shallShowScrollbar);
			delete this._shallShowScrollbar; // use undefined rather false

			// ZK-355: Scroll to current position
			if (scrollPosition) {
				bar.scrollTo(scrollPosition.l, scrollPosition.t);
				scrollPosition = null;
			}

			//sync frozen
			var frozen = this.frozen,
				start: number;
			if (frozen && (start = frozen._start) != 0) {
				frozen._doScrollNow(start);
				bar.setBarPosition(start);
			}
		}
	}

	protected destroyBar_(): void {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			bar = this._scrollbar = null;
		}
	}

	/**
	 * clears the tree children.
	 */
	public override clear(): void {
		if (!this.treechildren || !this.treechildren.nChildren)
			return;
		for (var w = this.treechildren.firstChild; w; w = w.nextSibling)
			w.detach();
	}

	public override insertBefore(child: zk.Widget, sibling: zk.Widget | null | undefined, ignoreDom?: boolean): boolean {
		if (super.insertBefore(child, sibling, !this.z_rod)) {
			this._fixOnAdd(child, ignoreDom, ignoreDom);
			return true;
		}
		return false;
	}

	public override appendChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		if (super.appendChild(child, !this.z_rod)) {
			if (!this.insertingBefore_)
				this._fixOnAdd(child, ignoreDom, ignoreDom);
			return true;
		}
		return false;
	}

	private _fixOnAdd(child: zk.Widget, ignoreDom: boolean | undefined, _noSync?: boolean): void {
		if (child instanceof zul.sel.Treecols)
			this.treecols = child;
		else if (child instanceof zul.sel.Treechildren) {
			this.treechildren = child;
			this._fixSelectedSet();
		} else if (child instanceof zul.mesh.Paging) {
			this.paging = child;
			this.paging.setMeshWidget(this);
		} else if (child instanceof zul.sel.Treefoot)
			this.treefoot = child;
		else if (child instanceof zul.mesh.Frozen)
			this.frozen = child;
		if (!ignoreDom)
			this.rerender();
		if (!_noSync)
			this._syncSize();
	}

	// ZK-5050
	protected override beforeChildReplaced_(oldc: zk.Widget, newc: zk.Widget): void {
		this._fixOnAdd(newc, true, true);
	}

	protected override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);

		if (child == this.treecols)
			this.treecols = null;
		else if (child == this.treefoot)
			this.treefoot = null;
		else if (child == this.treechildren) {
			this.treechildren = null;
			this._selItems = [];
			this._sel = null;
		} else if (child == this.paging) {
			this.paging.setMeshWidget(null);
			this.paging = null;
		} else if (child == this.frozen) {
			this.frozen = null;
			this.destroyBar_();
		}

		if (!this.childReplacing_) //NOT called by onChildReplaced_
			this._syncSize();
	}

	protected override onChildAdded_(child: zul.mesh.HeadWidget): void {
		super.onChildAdded_(child);
		if (this.childReplacing_) //called by onChildReplaced_
			this._fixOnAdd(child, true);
		//else handled by insertBefore/appendChild
	}

	public _onTreeitemAdded(item: zul.sel.Treeitem): void {
		this._fixNewChild(item);
		this._onTreechildrenAdded(item.treechildren);
	}

	public _onTreeitemRemoved(item: zul.sel.Treeitem): void {
		var fixSel;
		if (item.isSelected()) {
			this._selItems.$remove(item);
			fixSel = this._sel == item;
			if (fixSel && !this._multiple) {
				this._sel = null;
			}
		}
		this._onTreechildrenRemoved(item.treechildren);
		if (fixSel) this._fixSelected();
		const upperItem = item.previousSibling || item.getParentItem();
		if (upperItem) {
			this._shallSyncFocus = upperItem;
		} else {
			this._shallSyncFocus = true; // reset the anchor to the top;
		}
	}

	public _onTreechildrenAdded(tchs: zul.sel.Treechildren | null | undefined): void {
		if (!tchs || tchs.parent == this)
			return; //the rest is already being processed by insertBefore

		//maintain the selected status
		for (var j = 0, items = tchs.getItems(), k = items.length; j < k; ++j)
			if (items[j]) this._fixNewChild(items[j]);
	}

	public _onTreechildrenRemoved(tchs: zul.sel.Treechildren | null | undefined): void {
		if (tchs == null || tchs.parent == this)
			return; //already being processed by onChildRemoved

		//maintain the selected status
		var fixSel;
		for (var j = 0, items = tchs.getItems(), k = items.length; j < k; ++j) {
			const item = items[j];
			if (item.isSelected()) {
				this._selItems.$remove(item);
				if (this._sel == item) {
					if (!this._multiple) {
						this._sel = null;
						return; //done
					}
					fixSel = true;
				}
			}
		}
		if (fixSel) this._fixSelected();
	}

	private _fixNewChild(item: zul.sel.Treeitem): void {
		if (item.isSelected()) {
			if (this._sel && !this._multiple) {
				item._selected = false;
				item.rerender();
			} else {
				if (!this._sel)
					this._sel = item;
				this._selItems.push(item);
			}
		}
	}

	private _fixSelectedSet(): void {
		this._sel = null;
		this._selItems = [];
		for (var j = 0, items = this.getItems(), k = items.length; j < k; ++j) {
			if (items[j].isSelected()) {
				if (this._sel == null) {
					this._sel = items[j];
				} else if (!this._multiple) {
					items[j]._selected = false;
					continue;
				}
				this._selItems.push(items[j]);
			}
		}
	}

	private _fixSelected(): boolean {
		var sel: zul.sel.Treeitem | undefined;
		switch (this._selItems.length) {
		case 1:
			sel = this._selItems[0];
			// fallthrough
		case 0:
			break;
		default:
			for (var j = 0, items = this.getItems(), k = items.length; j < k; ++j) {
				if (items[j].isSelected()) {
					sel = items[j];
					break;
				}
			}
		}

		if (sel != this._sel) {
			this._sel = sel;
			return true;
		}
		return false;
	}

	public _sizeOnOpen(): void {
		this._shallShowScrollbar = true;
		var cols = this.treecols, wd;
		if (!cols || this.isSizedByContent() || this._hflex == 'min')
			this.syncSize();
		else {
			for (let w = cols.firstChild; w; w = w.nextSibling)
				if (w._hflex || !(wd = w._width) || wd == 'auto') {
					this.syncSize();
					return;
				}
		}
		this.doResizeScroll_();
	}

	/**
	 * Returns the head widget class. i.e. {@link Treecols}
	 * @return zul.sel.Treecols
	 */
	public getHeadWidgetClass(): typeof zul.sel.Treecols {
		return zul.sel.Treecols;
	}

	/**
	 * Returns the tree item iterator.
	 * @return zul.sel.TreeItemIter
	 * @disable(zkgwt)
	 */
	public itemIterator(opts?: Record<string, unknown>): zul.mesh.ItemIterator {
		return new zul.sel.TreeItemIter(this, opts);
	}

	/**
	 * Returns the tree item iterator.
	 * @return zul.sel.TreeItemIter
	 * @see #itemIterator
	 * @disable(zkgwt)
	 */
	public getBodyWidgetIterator = Tree.prototype.itemIterator;

	public override _updHeaderCM(): void {
		const tc = this.treecols?.firstChild;
		if (this._headercm && this._multiple && tc)
			tc._checked = this._isAllSelected();
		super._updHeaderCM();
	}

	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 * @return Array
	 */
	public getItems(opts?: Record<string, unknown>): zul.sel.Treeitem[] {
		return this.treechildren ? this.treechildren.getItems(null, opts) : [];
	}

	/** Returns the number of child {@link Treeitem}.
	 * The same as {@link #getItems}.size().
	 * <p>Note: the performance of this method is no good.
	 * @return int
	 */
	public getItemCount(opts?: {skipHidden?: boolean}): number {
		return this.treechildren != null ? this.treechildren.getItemCount(opts) : 0;
	}

	protected override _doLeft(row: zul.sel.Treeitem): void {
		if (row.isOpen()) {
			row.setOpen(false);
		}
	}

	protected override _doRight(row: zul.sel.Treeitem): void {
		if (!row.isOpen()) {
			row.setOpen(true);
		}
	}

	/** Returns whether to ignore the selection.
	 * It is called when selecting an item ({@link ItemWidget#doSelect_}).
	 * <p>Default: ignore the selection if it is clicked on the open icon or {@link #rightSelect} is true and event is onRightClick.
	 * @param zk.Event evt the event
	 * @param ItemWidget row the row about to be selected
	 * @return boolean whether to ignore the selection
	 */
	protected override shallIgnoreSelect_(evt: zk.Event, row: zul.sel.ItemWidget): boolean {
		var n = evt.domTarget;
		if (n) {
			var id = n.id;
			return id.endsWith('open') || id.endsWith('icon')
				|| (evt.name == 'onRightClick' && !this.rightSelect);
		}
		return false;
	} // Bug ZK-2295

	public override clearSelection(): void {
		super.clearSelection();
		this._sel = null;
	} // Bug ZK-2295

	protected override _addItemToSelection(item: zul.sel.ItemWidget): void {
		super._addItemToSelection(item);
		this._sel = this._selItems[0]; // resync
	} // Bug ZK-2295

	protected override _removeItemFromSelection(item: zul.sel.ItemWidget): void {
		super._removeItemFromSelection(item);
		this._sel = this._selItems[0]; // resync
	} // @Override F70-ZK-2433

	protected override checkOnHighlightDisabled_(): boolean {
		if (this._selectOnHighlightDisabled) {
			var selection = window.getSelection || document['selection'];
			if (selection) {
				return selection()!.toString().length > 0;
			}
		}
		return false;
	}
}

/**
 * Tree item iterator.
 */
@zk.WrapClass('zul.sel.TreeItemIter')
export class TreeItemIter extends zk.Object implements zul.mesh.ItemIterator {
	public tree: Tree;
	public opts?: Record<string, unknown>;
	private _isInit?: boolean;
	public cur?: number;
	public items?: zul.sel.Treeitem[];
	public length?: number;

	/** Constructor
	 * @param Tree tree the widget that the iterator belongs to
	 */
	public constructor(tree: zul.sel.Tree, opts?: Record<string, unknown>) {
		super();
		this.tree = tree;
		this.opts = opts;
	}

	private _init(): void {
		if (!this._isInit) {
			this._isInit = true;
			this.items = this.tree.getItems(this.opts);
			this.length = this.items.length;
			this.cur = 0;
		}
	}

	/**
	* Returns <tt>true</tt> if the iteration has more elements
	* @return boolean
	*/
	public hasNext(): boolean {
		this._init();
		return this.cur! < this.length!;
	}

	/**
	 * Returns the next element in the iteration.
	 *
	 * @return Treeitem the next element in the iteration.
	 */
	public next(): zul.sel.Treeitem {
		this._init();
		return this.items![this.cur!++];
	}
}