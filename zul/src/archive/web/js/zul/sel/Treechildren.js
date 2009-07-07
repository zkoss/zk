/* Treechildren.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Treechildren = zk.$extends(zul.Widget, {
	getTree: function () {
		for (var wgt = this.parent; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.sel.Tree)) return wgt;
		return null;
	},
	getLinkedTreerow: function () {
		return this.parent && this.parent.$instanceof(zul.sel.Treeitem) ?
			this.parent.treerow: null;
	},
	rerender: function () {
		if (this.desktop) {
			for (var w = this.firstChild; w; w = w.nextSibling)
				w.rerender();
		}
		return this;
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
	getItems: function (items) {
		items = items || [];
		for (var w = this.firstChild; w; w = w.nextSibling) {
			items.push(w);
			if (w.treechildren) 
				w.treechildren.getItems(items);
		}
		return items;
	},
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
	}
});
