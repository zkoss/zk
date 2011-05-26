/* Treechildren.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _prevsib(child) {
		var p;
		if ((p=child.parent) && p.lastChild == child)
			return child.previousSibling;
	}
	function _fixOnAdd(oldsib, child, ignoreDom) {
		if (!ignoreDom) {
			if (oldsib) oldsib._syncIcon();
			var p;
			if ((p=child.parent) && p.lastChild == child
			&& (p=child.previousSibling))
				p._syncIcon();
		}
	}

/**
 * A treechildren.
 */
zul.sel.Treechildren = zk.$extends(zul.Widget, {
	/** Returns the {@link Tree} instance containing this element.
	 * @return Tree
	 */
	getTree: function () {
		return this.isTopmost() ? this.parent : this.parent ? this.parent.getTree() : null;
	},
	/** Returns the {@link Treerow} that is associated with
	 * this treechildren, or null if no such treerow.
	 * @return Treerow
	 */
	getLinkedTreerow: function () {
		// optimised to assume the tree doesn't have treerow property
		return this.parent ? this.parent.treerow : null;
	},
	/** Returns whether this treechildren is topmost.
	 * @return boolean
	 * @since 5.0.6
	 */
	isTopmost: function () {
		return this.parent && this.parent.$instanceof(zul.sel.Tree);
	},
	//@Override
	insertBefore: function (child, sibling, ignoreDom) {
		var oldsib = _prevsib(child);
		if (this.$supers('insertBefore', arguments)) {
			_fixOnAdd(oldsib, child, ignoreDom);
			return true;
		}
	},
	//@Override
	appendChild: function (child, ignoreDom) {
		var oldsib = _prevsib(child);
		if (this.$supers('appendChild', arguments)) {
			if (!this.insertingBefore_)
				_fixOnAdd(oldsib, child, ignoreDom);
			return true;
		}
	},
	insertChildHTML_: function (child, before, desktop) {
		var ben, isTopmost = this.isTopmost();
		if (before)
			before = before.getFirstNode_();
		if (!before && !isTopmost)
			ben = this.getCaveNode() || this.parent.getCaveNode();

		if (before)
			jq(before).before(child.redrawHTML_());
		else if (ben)
			jq(ben).after(child.redrawHTML_());
		else {
			if (isTopmost)
				jq(this.parent.$n('rows')).append(child.redrawHTML_());
			else
				jq(this).append(child.redrawHTML_());
		}
		child.bind(desktop);
	},
	getCaveNode: function () {
		for (var cn, w = this.lastChild; w; w = w.previousSibling)
			if ((cn = w.getCaveNode())) {
				
				// Bug 2909820
				if (w.treechildren) {
					var _cn  = w.treechildren.getCaveNode();
					if (_cn)
						cn = _cn;
				}
				return cn;	
			}
	},
	/*
	isVisible: function () {
		if (!this.$supers('isVisible', arguments))
			return false;
		var p;
		return this.isTopmost()
			|| ((p = this.parent) && p.isOpen() && p.isVisible()); //recursive
	},
	*/
	//@Override
	isRealVisible: function () {
		this._isRealVisible() && this.$supers('isRealVisible', arguments);
	},
	_isRealVisible: function () {
		var p;
		return this.isVisible() && (this.isTopmost() || 
				((p = this.parent) && p.isOpen() && p._isRealVisible()));
	},
	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 * @param Array items
	 * @return Array
	 */
	getItems: function (items, opts) {
		items = items || [];
		var skiphd = opts && opts.skipHidden;
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (!skiphd || w.isVisible()) {
				items.push(w);
				if (w.treechildren && (!skiphd || w.isOpen())) 
					w.treechildren.getItems(items, opts);
			}
		return items;
	},
	/** Returns the number of child {@link Treeitem}
	 * including all descendants. The same as {@link #getItems}.size().
	 * <p>Note: the performance is no good.
	 * @return int
	 */
	getItemCount: function () {
		var sz = 0;
		for (var w = this.firstChild; w; w = w.nextSibling, ++sz)
			if (w.treechildren)
				sz += w.treechildren.getItemCount();
		return sz;
	},
	getZclass: function () {
		return this._zclass == null ? "z-treechildren" : this._zclass;
	},
	beforeParentChanged_: function (newParent) {
		var oldtree = this.getTree();
		if (oldtree)
			oldtree._onTreechildrenRemoved(this);
			
		if (newParent) {
			var tree = newParent.$instanceof(zul.sel.Tree) ? newParent : newParent.getTree();
			if (tree) tree._onTreechildrenAdded(this);
		}
		this.$supers("beforeParentChanged_", arguments);
	},
	removeHTML_: function (n) {
		for (var cn, w = this.firstChild; w; w = w.nextSibling) {
			cn = w.$n();
			if (cn)
				w.removeHTML_(cn);
		}
		this.$supers('removeHTML_', arguments);
	},
	getOldWidget_: function (n) {
		var old = this.$supers('getOldWidget_', arguments);
		if (old && old.$instanceof(zul.sel.Treerow)) {
			var ti = old.parent;
			if (ti)
				return ti.treechildren;
			return null;
		}
		return old;
	},
	$n: function (nm) {
		if (this.firstChild)
			return nm ? this.firstChild.$n(nm) : this.firstChild.$n();
		return null;
	},
	replaceWidget: function (newwgt) {
		while (this.firstChild != this.lastChild)
			this.lastChild.detach();
		
		if (this.firstChild && this.firstChild.treechildren)
			this.firstChild.treechildren.detach();

		zul.sel.Treeitem._syncSelItems(this, newwgt);

		this.$supers('replaceWidget', arguments);
	}
});

})();