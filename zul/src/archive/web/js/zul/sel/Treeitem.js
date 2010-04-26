/* Treeitem.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:43     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A treeitem.
 *
 * <p>Event:
 * <ol>
 * <li>onOpen is sent when a tree item is opened or closed by user.</li>
 * <li>onDoubleClick is sent when user double-clicks the treeitem.</li>
 * <li>onRightClick is sent when user right-clicks the treeitem.</li>
 * </ol>
 *
 */
zul.sel.Treeitem = zk.$extends(zul.sel.ItemWidget, {
	_open: true,
	_checkable: true,
	$define: {
    	/** Returns whether this container is open.
    	 * <p>Default: true.
    	 * @return boolean
    	 */
    	/** Sets whether this container is open.
    	 * @param boolean open
    	 */
		open: function (open, fromServer) {
			var img = this.$n('open');
			if (!img) return;
			var cn = img.className;
			img.className = open ? cn.replace('-close', '-open') : cn.replace('-open', '-close');
			this._showKids(open);
			zWatch.fireDown(open ? 'onShow': 'onHide', this);
			this.getMeshWidget().onSize();
			if (!fromServer) {
				var tree = this.getTree(),
					indemand = tree.inPagingMold() || tree.isModel();
				this.fire('onOpen', {open: open}, {toServer: indemand});
			}
			var tree = this.getTree();
			if (tree)
				tree.focus();
		}
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
	/**
	 * Returns the mesh widget. i.e. {@link Tree}
	 * @return Tree
	 */
	getMeshWidget: _zkf = function () {
		for (var wgt = this.parent; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.sel.Tree)) return wgt;
		return null;		
	},
	/**
	 * Returns the {@link Tree}.
	 * @return Tree
	 * @see #getMeshWidget
	 */
	getTree: _zkf,
	getZclass: function () {
		if (this.treerow) return this.treerow.getZclass();
		return null;
	},
	$n: function (nm) {
		if (this.treerow)
			return nm ? this.treerow.$n(nm) : this.treerow.$n() || jq(this.treerow.uuid, zk)[0];
		return null;
	},
	/** Returns whether the element is to act as a container
	 * which can have child elements.
	 * @return boolean
	 */
	isContainer: function () {
		return this.treechildren != null;
	},
	/** Returns whether this element contains no child elements.
	 * @return boolean
	 */
	isEmpty: function () {
		return !this.treechildren || !this.treechildren.nChildren;
	},
	/** Returns the level this cell is. The root is level 0.
	 * @return int
	 */
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
	/** Returns the label of the {@link Treecell} it contains, or null
	 * if no such cell.
	 * @return String
	 */
	getLabel: function () {
		var cell = this.getFirstCell();
		return cell ? cell.getLabel(): null;
	},
	/** Sets the label of the {@link Treecell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 * @param String label
	 */
	setLabel: function (label) {
		this._autoFirstCell().setLabel(label);
	},
	/**
	 * Returns the first treecell.
	 * @return Treecell
	 */
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
	/** Returns the image of the {@link Treecell} it contains.
	 * @return String
	 */
	getImage: function () {
		var cell = this.getFirstCell();
		return cell ? cell.getImage(): null;
	},
	/** Sets the image of the {@link Treecell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 * @param String image
	 * @return Treeitem
	 */
	setImage: function (image) {
		this._autoFirstCell().setImage(image);
		return this;
	},
	/** Returns the parent tree item,
	 * or null if this item is already the top level of the tree.
	 * The parent tree item is actually the grandparent if any.
	 * @return Treeitem
	 */
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
	//@Override
	insertBefore: function (child, sibling, ignoreDom) {
		if (this.$super('insertBefore', child, sibling, ignoreDom || child.$instanceof(zul.sel.Treechildren))) {
			this._fixOnAdd(child, ignoreDom);
			return true;
		}
	},
	//@Override
	appendChild: function (child, ignoreDom) {
		if (this.$super('appendChild', child, ignoreDom || child.$instanceof(zul.sel.Treechildren))) {
			if (!this.insertingBefore_)
				this._fixOnAdd(child, ignoreDom);
			return true;
		}
	},
	_fixOnAdd: function (child, ignoreDom) {
		if (child.$instanceof(zul.sel.Treerow)) 
			this.treerow = child;
		else if (child.$instanceof(zul.sel.Treechildren)) {
			this.treechildren = child;
			if (!ignoreDom && this.treerow) 
				this.rerender();
		}
	},
	onChildReplaced_: function (oldc, newc) {
		this.onChildRemoved_(oldc, true);
		this._fixOnAdd(newc, true);
	},
	onChildRemoved_: function(child, _noSync) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.treerow) 
			this.treerow = null;
		else if (child == this.treechildren) {
			this.treechildren = null;
			if (!_noSync) this._syncIcon(); // remove the icon
		}
	},
	removeHTML_: function (n) {
		for (var cn, w = this.firstChild; w; w = w.nextSibling) {
			cn = w.$n();
			if (cn)
				w.removeHTML_(cn);
		}
		this.$supers('removeHTML_', arguments);
	},
	replaceWidget: function (newwgt) {
		this._syncSelectedItem(newwgt);
		if (this.treechildren)
			this.treechildren.detach();
		this.$supers('replaceWidget', arguments);
	},
	_syncSelectedItem: function (newwgt) {
		var tree = this.getTree();
		if (tree && this.isSelected()) {
			var items = tree._selItems;
			if (items && items.$remove(this))
				items.push(newwgt);
		}
	},
	insertChildHTML_: function (child, before, desktop) {
		if (before = before ? before.getFirstNode_(): null)
			jq(before).before(child.redrawHTML_());
		else
			jq(this.getCaveNode()).after(child.redrawHTML_());
				//treechild is a DOM sibling (so use after)
		child.bind(desktop);
	},
	_syncIcon: function () {
		if (this.desktop && this.treerow) {
			var i = this.treerow;
			if (i = i.firstChild)
				i._syncIcon();
			if (i = this.treechildren)
				for (i = i.firstChild; i; i = i.nextSibling)
					i._syncIcon();
		}
	}
});