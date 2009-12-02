/* Treeitem.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:43     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.sel.Treeitem = zk.$extends(zul.sel.ItemWidget, {
	_open: true,
	_checkable: true,
	$define: {
		open: function (open, fromServer) {
			var img = this.$n('open');
			if (!img) return;
			var cn = img.className;
			img.className = open ? cn.replace('-close', '-open') : cn.replace('-open', '-close');
			this._showKids(open);
			this.getMeshWidget().onSize();
			if (!fromServer) {
				var tree = this.getTree(),
					indemand = tree.inPagingMold() || tree.isModel();
				this.fire('onOpen', {open: open}, {toServer: indemand});
			}
		}
	},
	rerender: function () {
		if (this.desktop) {
			if (this.treerow)
				this.treerow.rerender();
			if (this.treechildren)
				this.treechildren.rerender();
		}
		return this;
	},
	_showKids: function (open) {
		if (this.treechildren)
			for (var w = this.treechildren.firstChild; w; w = w.nextSibling) {
				w.$n().style.display = open ? '' : 'none';
				if (w.isOpen())
					w._showKids(open);
			}
	},
	isStripeable_: function () {
		return false;
	},
	getMeshWidget: _zkf = function () {
		for (var wgt = this.parent; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.sel.Tree)) return wgt;
		return null;		
	},
	getNode: function () {
		if (this.treerow) return this.treerow.$n();
		return null;
	},
	getZclass: function () {
		if (this.treerow) return this.treerow.getZclass();
		return null;
	},
	$n: function (nm) {
		if (this.treerow) return this.treerow.$n(nm);
		return null;
	},
	getTree: _zkf,
	isContainer: function () {
		return this.treechildren != null;
	},
	isEmpty: function () {
		return !this.treechildren || !this.treechildren.nChildren;
	},
	getLevel: function () {
		var level = 0;
		for (var  item = this;; ++level) {
			if (!item.parent)
				break;

			item = item.parent.parent;
			if (!item || item.$instanceof(zul.sel.Tree))
				break;
		}
		return level;
	},
	getLabel: function () {
		var cell = this.getFirstCell();
		return cell ? cell.getLabel(): null;
	},
	setLabel: function (label) {
		this._autoFirstCell().setLabel(label);
	},
	getFirstCell: function () {
		return this.treerow ? this.treerow.firstChild : null;
	},
	_autoFirstCell: function () {
		if (!this.treerow)
			this.appendChild(new zul.sel.Treerow());

		var cell = this.treerow.getFirstChild();
		if (!cell) {
			cell = new zul.sel.Treecell();
			this.treerow.appendChild(cell);
		}
		return cell;
	},
	getImage: function () {
		var cell = this.getFirstCell();
		return cell ? cell.getImage(): null;
	},
	setImage: function (image) {
		this._autoFirstCell().setImage(image);
		return this;
	},
	getParentItem: function () {
		var p = this.parent && this.parent.parent ? this.parent.parent : null;
		return p && p.$instanceof(zul.sel.Treeitem) ? p : null;
	},
	setVisible: function (visible) {
		if (this._visible != visible) {
			this.$supers('setVisible', arguments);
			if (this.treerow) this.treerow.setVisible(visible);
		}
		return this;
	},
	
	beforeParentChanged_: function(newParent) {
		var oldtree = this.getTree();
		if (oldtree) 
			oldtree._onTreeitemRemoved(this);
		
		if (newParent) {
			var tree = newParent.getTree();
			if (tree) 
				tree._onTreeitemAdded(this);
		}
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments)
		if (child.$instanceof(zul.sel.Treerow)) 
			this.treerow = child;
		else if (child.$instanceof(zul.sel.Treechildren)) {
			this.treechildren = child;
			if (this.treerow) 
				this.rerender();
		}
	},
	onChildRemoved_: function(child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.treerow) 
			this.treerow = null;
		else if (child == this.treechildren) {
			this.treechildren = null;
			this.rerender();
		}
	},
	doClick_: function(evt) {
		if (this.isDisabled()) return;
		if (evt.domTarget == this.$n('open')) {
			this.setOpen(!this._open);
			evt.stop();
		} else this.$supers('doClick_', arguments);
	},
	removeChild: function (child) {
		for (var w = child.firstChild; w;) {
			var n = w.nextSibling; //remember, since remove will null the link
			child.removeChild(w); //deep first
			w = n;
		}
		this.$supers('removeChild', arguments);
	},
	insertChildHTML_: function (child, before, desktop) {
		if (before = before ? before.getFirstNode_(): null)
			jq(before).before(child._redrawHTML());
		else
			jq(this.getCaveNode()).after(child._redrawHTML());
				//treechild is a DOM sibling (so use after)
		child.bind(desktop);
	}
});