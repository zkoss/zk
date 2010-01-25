/* Treechildren.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A treechildren.
 */
zul.sel.Treechildren = zk.$extends(zul.Widget, {
	/** Returns the {@link Tree} instance containing this element.
	 * @return Tree
	 */
	getTree: function () {
		for (var wgt = this.parent; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.sel.Tree)) return wgt;
		return null;
	},
	/** Returns the {@link Treerow} that is associated with
	 * this treechildren, or null if no such treerow.
	 * @return Treerow
	 */
	getLinkedTreerow: function () {
		return this.parent && this.parent.$instanceof(zul.sel.Treeitem) ?
			this.parent.treerow: null;
	},
	//@Override
	insertBefore: function (child, sibling, ignoreDom) {
		var oldsib = this._fixBeforeAdd(child);

		if (this.$supers('insertBefore', arguments)) {
			this._fixOnAdd(oldsib, child, ignoreDom);
			return true;
		}
	},
	//@Override
	appendChild: function (child, ignoreDom) {
		var oldsib = this._fixBeforeAdd(child);

		if (this.$supers('appendChild', arguments)) {
			this._fixOnAdd(oldsib, child, ignoreDom);
			return true;
		}
	},
	_fixBeforeAdd: function (child) {
		var p;
		if ((p=child.parent) && p.lastChild == child)
			return child.previousSibling;
	},
	_fixOnAdd: function (oldsib, child, ignoreDom) {
		if (!ignoreDom) {
			if (oldsib && (oldsib = oldsib.treerow)) oldsib.rerender();
			var p;
			if ((p=child.parent) && p.lastChild == child
			&& (p=child.previousSibling) && (p=p.treerow)) p.rerender();
		}
	},
	insertChildHTML_: function (child, before, desktop) {
		var ben;
		if (before)
			before = before.getFirstNode_();
		if (!before && !this.parent.$instanceof(zul.sel.Tree))
			ben = this.getCaveNode() || this.parent.getCaveNode();

		if (before)
			jq(before).before(child.redrawHTML_());
		else if (ben)
			jq(ben).after(child.redrawHTML_());
		else {
			if (this.parent.$instanceof(zul.sel.Tree))
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
	isVisible: function () {
		if (!this.$supers('isVisible', arguments))
			return false;

		if (!this.parent) return false;
		if (!(this.parent.$instanceof(zul.sel.Treeitem)))
			return true;
		if (!this.parent.isOpen())
			return false;
		return !(this.parent.parent.$instanceof(zul.sel.Treechildren))
			|| this.parent.parent.isVisible(); //recursive
	},
	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 * @return Array
	 */
	getItems: function (items) {
		items = items || [];
		for (var w = this.firstChild; w; w = w.nextSibling) {
			items.push(w);
			if (w.treechildren) 
				w.treechildren.getItems(items);
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
	},
	removeChild: function (child) {
		for (var w = child.firstChild; w;) {
			var n = w.nextSibling; //remember, since remove will null the link
			child.removeChild(w); //deep first
			w = n;
		}
		this.$supers('removeChild', arguments);
	}
});
