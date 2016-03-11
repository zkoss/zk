/* Tree.js

	Purpose:

	Description:

	History:
		Wed Jun 10 16:32:29     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var Tree =
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
zul.sel.Tree = zk.$extends(zul.sel.SelectWidget, {
	_scrollbar: null,
	_barPos: null,
	unbind_: function () {
		this.destroyBar_();
		this.$supers(Tree, 'unbind_', arguments);
	},
	onSize: function () {
		this.$supers(Tree, 'onSize', arguments);
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
	},
	refreshBar_: function (showBar) {
		var bar = this._scrollbar;
		if (bar) {
			var scrollPosition;
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
	/**
	 * clears the tree children.
	 */
	clear: function () {
		if (!this._treechildren || !this._treechildren.nChildren)
			return;
		for (var w = this._treechildren.firstChild; w; w = w.nextSibling)
			w.detach();
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
		if (child.$instanceof(zul.sel.Treecols))
			this.treecols = child;
		else if (child.$instanceof(zul.sel.Treechildren)) {
			this.treechildren = child;
			this._fixSelectedSet();
		} else if (child.$instanceof(zul.mesh.Paging))
			this.paging = child;
		else if (child.$instanceof(zul.sel.Treefoot))
			this.treefoot = child;
		else if (child.$instanceof(zul.mesh.Frozen)) 
			this.frozen = child;
		if (!ignoreDom)
			this.rerender();
		if (!_noSync)
			this._syncSize();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);

		if (child == this.treecols)
			this.treecols = null;
		else if (child == this.treefoot)
			this.treefoot = null;
		else if (child == this.treechildren) {
			this.treechildren = null;
			this._selItems = [];
			this._sel = null;
		} else if (child == this.paging)
			this.paging = null;
		else if (child == this.frozen) {
			this.frozen = null;
			this.destroyBar_();
		}

		if (!this.childReplacing_) //NOT called by onChildReplaced_
			this._syncSize();
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (this.childReplacing_) //called by onChildReplaced_
			this._fixOnAdd(child, true);
		//else handled by insertBefore/appendChild
	},
	_onTreeitemAdded: function (item) {
		this._fixNewChild(item);
		this._onTreechildrenAdded(item.treechildren);
	},
	_onTreeitemRemoved: function (item) {
		var fixSel, upperItem;
		if (item.isSelected()) {
			this._selItems.$remove(item);
			fixSel = this._sel == item;
			if (fixSel && !this._multiple) {
				this._sel = null;
			}
		}
		this._onTreechildrenRemoved(item.treechildren);
		if (fixSel) this._fixSelected();
		if (upperItem = item.previousSibling || item.getParentItem()) {
			this._shallSyncFocus = upperItem;
		} else {
			this._shallSyncFocus = true; // reset the anchor to the top;
		}
	},
	_onTreechildrenAdded: function (tchs) {
		if (!tchs || tchs.parent == this)
			return; //the rest is already being processed by insertBefore

		//maintain the selected status
		for (var j = 0, items = tchs.getItems(), k = items.length; j < k; ++j)
			if (items[j]) this._fixNewChild(items[j]);
	},
	_onTreechildrenRemoved: function (tchs) {
		if (tchs == null || tchs.parent == this)
			return; //already being processed by onChildRemoved

		//maintain the selected status
		var item, fixSel;
		for (var j = 0, items = tchs.getItems(), k = items.length; j < k; ++j) {
			item = items[j];
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
	},
	_fixNewChild: function (item) {
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
	},
	_fixSelectedSet: function () {
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
	},
	_fixSelected: function () {
		var sel;
		switch (this._selItems.length) {
		case 1:
			sel = this._selItems[0];
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
	},
	_sizeOnOpen: function () {
		this._shallShowScrollbar = true;
		var cols = this.treecols, w, wd;
		if (!cols || this.isSizedByContent() || this._hflex == 'min')
			this.syncSize();
		else {
			for (w = cols.firstChild; w; w = w.nextSibling)
				if (w._hflex || !(wd = w._width) || wd == 'auto') {
					this.syncSize();
					return;
				}
		}
		this.doResizeScroll_();
	},
	/**
	 * Returns the head widget class. i.e. {@link Treecols}
	 * @return zul.sel.Treecols
	 */
	getHeadWidgetClass: function () {
		return zul.sel.Treecols;
	},
	/**
	 * Returns the tree item iterator.
	 * @return zul.sel.TreeItemIter
	 * @disable(zkgwt)
	 */
	itemIterator: _zkf = function (opts) {
		return new zul.sel.TreeItemIter(this, opts);
	},
	/**
	 * Returns the tree item iterator.
	 * @return zul.sel.TreeItemIter
	 * @see #itemIterator
	 * @disable(zkgwt)
	 */
	getBodyWidgetIterator: _zkf,

	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 * @return Array
	 */
	getItems: function (opts) {
		return this.treechildren ? this.treechildren.getItems(null, opts) : [];
	},
	/** Returns the number of child {@link Treeitem}.
	 * The same as {@link #getItems}.size().
	 * <p>Note: the performance of this method is no good.
	 * @return int
	 */
	getItemCount: function () {
		return this.treechildren != null ? this.treechildren.getItemCount() : 0;
	},
	_doLeft: function (row) {
		if (row.isOpen()) {
			row.setOpen(false);
		}
	},
	_doRight: function (row) {
		if (!row.isOpen()) {
			row.setOpen(true);
		}
	},

	/** Returns whether to ignore the selection.
	 * It is called when selecting an item ({@link ItemWidget#doSelect_}).
	 * <p>Default: ignore the selection if it is clicked on the open icon or {@link #rightSelect} is true and event is onRightClick.
	 * @param zk.Event evt the event
	 * @param ItemWidget row the row about to be selected
	 * @return boolean whether to ignore the selection
	 */
	shallIgnoreSelect_: function (evt/*, row*/) {
		var n = evt.domTarget;
		if (n) {
			var id = n.id;
			return id.endsWith('open') || id.endsWith('icon')
				|| (evt.name == 'onRightClick' && !this.rightSelect);
		}
	},// Bug ZK-2295
	clearSelection: function () {
		this.$supers('clearSelection', arguments);
		this._sel = null;
	},// Bug ZK-2295
	_addItemToSelection: function () {
		this.$supers('_addItemToSelection', arguments);
		this._sel = this._selItems[0]; // resync
	},// Bug ZK-2295
	_removeItemFromSelection: function () {
		this.$supers('_removeItemFromSelection', arguments);
		this._sel = this._selItems[0]; // resync
	},// @Override F70-ZK-2433
	checkOnHighlightDisabled_: function () {
		if (this._selectOnHighlightDisabled) {
			var selection = window.getSelection || document.selection;
			if (selection) {
				if (zk.ie && zk.ie < 9) {
					return selection.type == 'Text' && selection.createRange().htmlText.length > 0;
				} else {
					return selection().toString().length > 0;
				}
			}
		}
	}
});
/**
 * Tree item iterator.
 */
zul.sel.TreeItemIter = zk.$extends(zk.Object, {
	/** Constructor
	 * @param Tree tree the widget that the iterator belongs to
	 */
	$init: function (tree, opts) {
		this.tree = tree;
		this.opts = opts;
	},
	_init: function () {
		if (!this._isInit) {
			this._isInit = true;
			this.items = this.tree.getItems(this.opts);
			this.length = this.items.length;
			this.cur = 0;
		}
	},
	 /**
     * Returns <tt>true</tt> if the iteration has more elements
     * @return boolean
     */
	hasNext: function () {
		this._init();
		return this.cur < this.length;
	},
	/**
     * Returns the next element in the iteration.
     *
     * @return Treeitem the next element in the iteration.
     */
	next: function () {
		this._init();
		return this.items[this.cur++];
	}
});

