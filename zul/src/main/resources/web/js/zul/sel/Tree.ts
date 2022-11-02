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
 * @defaultValue {@link getZclass}: z-tree.
 */
@zk.WrapClass('zul.sel.Tree')
export class Tree extends zul.sel.SelectWidget {
	/** @internal */
	override _selItems!: zul.sel.Treeitem[]; // initialized in super constructor
	/** @internal */
	override _scrollbar?: zul.Scrollbar = undefined;
	/** @internal */
	_barPos = undefined;
	treecols?: zul.sel.Treecols;
	treefoot?: zul.sel.Treefoot;
	treechildren?: zul.sel.Treechildren;
	/** @internal */
	_shallSyncFrozen?: boolean;
	/** @internal */
	_tree$noSelectAll?: boolean;
	/** @internal */
	_sel?: zul.sel.Treeitem;
	/** @internal */
	_fixhdwcnt?: number; // zul.sel.Treeitem.prototype.setOpen
	/** @internal */
	_fixhdoldwd?: number; // zul.sel.Treeitem.prototype.setOpen

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this.destroyBar_();
		super.unbind_(skipper, after, keepRod);
	}

	override onSize(): void {
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

	/** @internal */
	override refreshBar_(showBar?: boolean, scrollToTop?: boolean): void {
		var bar = this._scrollbar;
		if (bar) {
			var scrollPosition: {l: number; t: number} | undefined;
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
				scrollPosition = undefined;
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

	/** @internal */
	destroyBar_(): void {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			bar = this._scrollbar = undefined;
		}
	}

	/**
	 * clears the tree children.
	 */
	override clear(): void {
		if (!this.treechildren || !this.treechildren.nChildren)
			return;
		for (var w = this.treechildren.firstChild; w; w = w.nextSibling)
			w.detach();
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
	/** @internal */
	override beforeChildReplaced_(oldc: zk.Widget, newc: zk.Widget): void {
		this._fixOnAdd(newc, true, true);
	}

	/** @internal */
	override beforeChildAdded_(child: zk.Widget, insertBefore?: zk.Widget): boolean {
		if (child instanceof zul.sel.Treecols) {
			if (this.treecols && this.treecols != child) {
				zk.error('Only one treecols is allowed: ' + this.className);
				return false;
			}
		} else if (child instanceof zul.sel.Treefoot) {
			if (this.treefoot && this.treefoot != child) {
				zk.error('Only one treefoot is allowed: ' + this.className);
				return false;
			}
		} else if (child instanceof zul.mesh.Frozen) {
			if (this.frozen && this.frozen != child) {
				zk.error('Only one frozen child is allowed: ' + this.className);
				return false;
			}
		} else if (child instanceof zul.sel.Treechildren) {
			if (this.treechildren && this.treechildren != child) {
				zk.error('Only one treechildren is allowed: ' + this.className);
				return false;
			}
		} else if (child instanceof zul.mesh.Paging) {
			if (this.getPaginal()) {
				zk.error('External paging cannot coexist with child paging, ' + this.className);
				return false;
			}
			if (this.paging && this.paging != child) {
				zk.error('Only one paging is allowed: ' + this.className);
				return false;
			}
			if (this.getMold() != 'paging') {
				zk.error('The child paging is allowed only in the paging mold, ' + this.className);
				return false;
			}
		} else if (zul.grid /* zul.grid might not exist. See zk.wpd */ && child instanceof zul.grid.Foot) {
			if (this.foot && this.foot != child) {
				zk.error('Only one foot child is allowed: ' + this.className);
				return false;
			}
		} else if (!(child instanceof zul.mesh.Auxhead)) {
			zk.error('Unsupported newChild: ' + child.className);
			return false;
		}
		return true;
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);

		if (child == this.treecols)
			this.treecols = undefined;
		else if (child == this.treefoot)
			this.treefoot = undefined;
		else if (child == this.treechildren) {
			this.treechildren = undefined;
			this._selItems = [];
			this._sel = undefined;
		} else if (child == this.paging) {
			this.paging.setMeshWidget(undefined);
			this.paging = undefined;
		} else if (child == this.frozen) {
			this.frozen = undefined;
			this.destroyBar_();
		}

		if (!this.childReplacing_) //NOT called by onChildReplaced_
			this._syncSize();
	}

	/** @internal */
	override onChildAdded_(child: zul.mesh.HeadWidget): void {
		super.onChildAdded_(child);
		if (this.childReplacing_) //called by onChildReplaced_
			this._fixOnAdd(child, true);
		//else handled by insertBefore/appendChild
	}

	/** @internal */
	_onTreeitemAdded(item: zul.sel.Treeitem): void {
		this._fixNewChild(item);
		this._onTreechildrenAdded(item.treechildren);
	}

	/** @internal */
	_onTreeitemRemoved(item: zul.sel.Treeitem): void {
		var fixSel;
		if (item.isSelected()) {
			this._selItems.$remove(item);
			fixSel = this._sel == item;
			if (fixSel && !this._multiple) {
				this._sel = undefined;
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

	/** @internal */
	_onTreechildrenAdded(tchs: zul.sel.Treechildren | undefined): void {
		if (!tchs || tchs.parent == this)
			return; //the rest is already being processed by insertBefore

		//maintain the selected status
		for (var j = 0, items = tchs.getItems(), k = items.length; j < k; ++j)
			if (items[j]) this._fixNewChild(items[j]);
	}

	/** @internal */
	_onTreechildrenRemoved(tchs: zul.sel.Treechildren | undefined): void {
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
						this._sel = undefined;
						return; //done
					}
					fixSel = true;
				}
			}
		}
		if (fixSel) this._fixSelected();
	}

	/** @internal */
	_fixNewChild(item: zul.sel.Treeitem): void {
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

	/** @internal */
	_fixSelectedSet(): void {
		this._sel = undefined;
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

	/** @internal */
	_fixSelected(): boolean {
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

	/** @internal */
	_sizeOnOpen(): void {
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
	 * @returns the head widget class. i.e. {@link Treecols}
	 */
	getHeadWidgetClass(): typeof zul.sel.Treecols {
		return zul.sel.Treecols;
	}

	/**
	 * @returns the tree item iterator.
	 */
	itemIterator(opts?: Record<string, unknown>): zul.sel.TreeItemIter {
		return new zul.sel.TreeItemIter(this, opts);
	}

	/**
	 * @returns the tree item iterator.
	 * @see {@link itemIterator}
	 */
	getBodyWidgetIterator = Tree.prototype.itemIterator;

	/** @internal */
	override _updHeaderCM(): void {
		const tc = this.treecols?.firstChild;
		if (this._headercm && this._multiple && tc)
			tc._checked = this._isAllSelected();
		super._updHeaderCM();
	}

	/**
	 * @returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 */
	getItems(opts?: Record<string, unknown>): zul.sel.Treeitem[] {
		return this.treechildren ? this.treechildren.getItems(undefined, opts) : [];
	}

	/**
	 * @returns the number of child {@link Treeitem}.
	 * The same as {@link getItems}.size().
	 * <p>Note: the performance of this method is no good.
	 */
	getItemCount(opts?: {skipHidden?: boolean}): number {
		return this.treechildren != null ? this.treechildren.getItemCount(opts) : 0;
	}

	/** @internal */
	override _doLeft(row: zul.sel.Treeitem): void {
		if (row.isOpen()) {
			row.setOpen(false);
		}
	}

	/** @internal */
	override _doRight(row: zul.sel.Treeitem): void {
		if (!row.isOpen()) {
			row.setOpen(true);
		}
	}

	/**
	 * @returns whether to ignore the selection.
	 * It is called when selecting an item ({@link ItemWidget#doSelect_}).
	 * @defaultValue ignore the selection if it is clicked on the open icon or {@link rightSelect} is true and event is onRightClick.
	 * @param evt - the event
	 * @param row - the row about to be selected
	 * @internal
	 */
	override shallIgnoreSelect_(evt: zk.Event, row: zul.sel.ItemWidget): boolean {
		var n = evt.domTarget;
		if (n) {
			var id = n.id;
			return id.endsWith('open') || id.endsWith('icon')
				|| (evt.name == 'onRightClick' && !this.rightSelect);
		}
		return false;
	} // Bug ZK-2295

	override clearSelection(): void {
		super.clearSelection();
		this._sel = undefined;
	} // Bug ZK-2295

	/** @internal */
	override _addItemToSelection(item: zul.sel.ItemWidget): void {
		super._addItemToSelection(item);
		this._sel = this._selItems[0]; // resync
	} // Bug ZK-2295

	/** @internal */
	override _removeItemFromSelection(item: zul.sel.ItemWidget): void {
		super._removeItemFromSelection(item);
		this._sel = this._selItems[0]; // resync
	} // @Override F70-ZK-2433

	/** @internal */
	override checkOnHighlightDisabled_(): boolean {
		if (this._selectOnHighlightDisabled) {
			// eslint-disable-next-line @typescript-eslint/dot-notation
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
	tree: Tree;
	opts?: Record<string, unknown>;
	/** @internal */
	_isInit?: boolean;
	cur?: number;
	items?: zul.sel.Treeitem[];
	length?: number;

	/**
	 * @param tree - the widget that the iterator belongs to
	 */
	constructor(tree: zul.sel.Tree, opts?: Record<string, unknown>) {
		super();
		this.tree = tree;
		this.opts = opts;
	}

	/** @internal */
	_init(): void {
		if (!this._isInit) {
			this._isInit = true;
			this.items = this.tree.getItems(this.opts);
			this.length = this.items.length;
			this.cur = 0;
		}
	}

	/**
	* @returns `true` if the iteration has more elements
	*/
	hasNext(): boolean {
		this._init();
		return this.cur! < this.length!;
	}

	/**
	 * @returns the next element in the iteration.
	 */
	next(): zul.sel.Treeitem | undefined {
		this._init();
		return this.items![this.cur!++];
	}
}