/* Tree.js

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
 * <p>Default {@link #getZclass}: z-tree, and an other option is z-dottree.
 */
zul.sel.Tree = zk.$extends(zul.sel.SelectWidget, {
	/**
	 * clears the tree children.
	 */
	clear: function () {
		if (!this._treechildren || !this._treechildren.nChildren)
			return;
		for (var w = this._treechildren.firstChild; w; w = w.nextSibling)
			w.detach();
	},
	getZclass: function () {
		return this._zclass == null ? "z-tree" : this._zclass;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments)
		if (child.$instanceof(zul.sel.Treecols))
			this.treecols = child;
		else if (child.$instanceof(zul.sel.Treefoot))
			this.treefoot = child;
		else if (child.$instanceof(zul.sel.Treechildren)) {
			this.treechildren = child;
			this._fixSelectedSet();
		} else if (child.$instanceof(zul.mesh.Paging))
			this.paging = child;
			
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
			_selItems.clear();
			_sel = null;
		} else if (child == this.paging)
			this.paging = null;
			
		this._syncSize();
	},
	_onTreeitemAdded: function (item) {
		this._fixNewChild(item);
		this._onTreechildrenAdded(item.treechildren);
	},
	_onTreeitemRemoved: function (item) {
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
	},
	_onTreechildrenAdded: function (tchs) {
		if (!tchs || tchs.parent == this)
			return; //already being processed by insertBefore

		//main the selected status
		for (var j = 0, items = tchs.getItems(), k = items.length; j < k; ++j)
			if (items[j]) this._fixNewChild(items[j]);
	},
	_onTreechildrenRemoved: function (tchs) {
		if (tchs == null || tchs.parent == this)
			return; //already being processed by onChildRemoved

		//main the selected status
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
				if (_sel == null) {
					_sel = items[j];
				} else if (!_multiple) {
					items[j]._selected = false;
					continue;
				}
				this._selItems.push(item);
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
	/**
	 * Returns the head widget class. i.e. {@link Treecols}
	 * @return Object
	 */
	getHeadWidgetClass: function () {
		return zul.sel.Treecols;
	},
	/**
	 * Returns the tree item iterator.
	 * @return Object
	 */
	itemIterator: _zkf = function () {
		return new zul.sel.TreeItemIter(this);
	},
	/**
	 * Returns the tree item iterator.
	 * @return Object
	 * @see #itemIterator
	 */
	getBodyWidgetIterator: _zkf,

	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 * @return Array
	 */
	getItems: function () {
		return this.treechildren ? this.treechildren.getItems(): [];
	},
	/** Returns the number of child {@link Treeitem}.
	 * The same as {@link #getItems}.size().
	 * <p>Note: the performance of this method is no good.
	 * @return int
	 */
	getItemCount: function () {
		return this.treechildren != null ? this.treechildren.getItemCount(): 0;
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
	}
});
/**
 * Tree item iterator.
 */
zul.sel.TreeItemIter = zk.$extends(zk.Object, {
	/** Constructor
	 * @param Tree tree the widget that the iterator belongs to
	 */
	$init: function (tree) {
		this.tree = tree;
	},
	_init: function () {
		if (!this._isInit) {
			this._isInit = true;
			this.items = this.tree.getItems();
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

