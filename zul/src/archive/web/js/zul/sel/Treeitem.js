/* Treeitem.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:32:43     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	//test if a treexxx is closed or any parent treeitem is closed
	function _closed(ti) {
		for (; ti && !ti.$instanceof(zul.sel.Tree); ti = ti.parent)
			if (ti.isOpen && !ti.isOpen())
				return true;
	}

	function _rmSelItemsDown(items, wgt) {
		if (wgt.isSelected())
			items.$remove(wgt);

		var w;
		if (w = wgt.treechildren)
			for (w = w.firstChild; w && items.length; w = w.nextSibling)
				_rmSelItemsDown(items, w);
	}
	function _addSelItemsDown(items, wgt) {
		if (wgt.isSelected())
			items.push(wgt);

		var w;
		if (w = wgt.treechildren)
			for (w = w.firstChild; w; w = w.nextSibling)
				_addSelItemsDown(items, w);
	}

	function _showDOM(wgt, visible) {
		var n = wgt.$n();
		if (n)
			n.style.display = visible ? "" : "none";
		var chld;
		if (chld = wgt.treechildren)
			for (var w = chld.firstChild; w; w = w.nextSibling)
				if (w._visible && w._open) // optimized, need to recurse only if open and visible
					_showDOM(w, visible);
	}
	
	function _syncTreeBodyHeight(wgt) {
		if (zk.ie < 8) {
			var tree = wgt.getTree();
			if (tree) {
				tree.ebody.style.height = '';
				tree._syncBodyHeight();
			}
		}
	}
	//Bug ZK-1766
	function _searchPrevRenderedItem(wgt) {
		var target;
		if (wgt) {
			if (wgt.treerow) {
				return wgt;
			}
			if (wgt.isContainer()) {
				for (var c = wgt.treechildren.lastChild; c; c = c.previousSibling) {
					target = _searchPrevRenderedItem(c);
					if (target)
						return target;
				}
			}
			target = _searchPrevRenderedItem(wgt.previousSibling);
		}
		return target;
	}
	//Bug ZK-1766
	function _searchNextRenderedItem(wgt) {
		var target;
		if (wgt) {
			if (wgt.treerow) {
				return wgt;
			}
			if (wgt.isContainer()) {
				for (var c = wgt.treechildren.firstChild; c; c = c.nextSibling) {
					target = _searchNextRenderedItem(c);
					if (target)
						return target;
				}
			}
			target = _searchNextRenderedItem(wgt.nextSibling);
		}
		return target;
	}
	
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
			if (!img || _closed(this.parent)) {
                if (img) {// B65-ZK-1609: Tree close/open icon is not correct after calling clearOpen and reopen a node
                    var cn = img.className;
                    img.className = open ? cn.replace('-close', '-open') : cn.replace('-open', '-close');
                }
				return;
			}
            
            var cn = img.className,
				tree = this.getTree(),
				ebodytbl = tree ? tree.ebodytbl: null,
				oldwd = ebodytbl ? ebodytbl.clientWidth: 0; //ebodytbl shall not be null (just in case)
			img.className = open ? cn.replace('-close', '-open') : cn.replace('-open', '-close');
			if (!open) zWatch.fireDown('onHide', this);
			this._showKids(open);
			if (open)
				zUtl.fireShown(this);
			if (tree) {
				tree._sizeOnOpen();

				if (!fromServer)
					this.fire('onOpen', {open: open},
						{toServer: tree.inPagingMold() || tree.isModel()});

				tree._syncFocus(this);
				tree.focus();

				if (ebodytbl) {
					tree._fixhdwcnt = tree._fixhdwcnt || 0;
					if (!tree._fixhdwcnt++)
						tree._fixhdoldwd = oldwd;
					setTimeout(function () {
						if (!--tree._fixhdwcnt && tree.$n() && 
								(tree._fixhdoldwd != ebodytbl.clientWidth || (zk.ie < 8))) // B50-3343001: IE 6/7
							tree._calcSize();
					}, 250);
				}
			}
		}
	},
	_showKids: function (open) {
		var tc = this.treechildren;
		if (tc)
			for (var w = tc.firstChild, vi = tc._isRealVisible(); w; w = w.nextSibling) {
				w.$n().style.display = vi && w.isVisible() && open ? '' : 'none';
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
		return this.parent ? this.parent.getTree() : null;
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

		var cell = this.treerow.firstChild;
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
	_isRealVisible: function () {
		var p;
		return this.isVisible() && (p = this.parent) && p._isRealVisible();
	},
	_isVisibleInTree: function () {
		// used by Treecell#_isLastVisibleChild
		if (!this.isVisible())
			return;
		var c = this.parent, p;
		if (!c || !c.isVisible() || !(p = c.parent))
			return false;
		if (p.$instanceof(zul.sel.Tree))
			return true;
		// Treeitem
		return p._isVisibleInTree(); // timing issue, does not concern open state
	},
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			if (this.treerow) this.treerow.setVisible(visible);
			// Bug: B50-3293724
			_showDOM(this, this._isRealVisible());
		}
		return this;
	},
	doMouseOver_: function (evt) {
		var ico = this.$n('open');
		if (evt.domTarget == ico)
			jq(this.$n()).addClass(this.getZclass() + '-ico-over');
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		var ico = this.$n('open');	
		if (evt.domTarget == ico)
			jq(this.$n()).removeClass(this.getZclass() + '-ico-over');
		this.$supers('doMouseOut_', arguments);
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
		this.$supers("beforeParentChanged_", arguments);
	},
	//@Override
	insertBefore: function (child, sibling, ignoreDom) {
		if (this.$super('insertBefore', child, sibling,
		ignoreDom || (!this.z_rod && child.$instanceof(zul.sel.Treechildren)))) {
			this._fixOnAdd(child, ignoreDom);
			return true;
		}
	},
	//@Override
	appendChild: function (child, ignoreDom) {
		if (this.$super('appendChild', child,
		ignoreDom || (!this.z_rod && child.$instanceof(zul.sel.Treechildren)))) {
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
	onChildRemoved_: function(child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.treerow) {
			this.treerow = null;
		//	_syncTreeBodyHeight(this); ZK-360: fixed; ZK-1735: dont need to sync
		} else if (child == this.treechildren) {
			this.treechildren = null;
			if (!this.childReplacing_) //NOT called by onChildReplaced_
				this._syncIcon(); // remove the icon
		}
	},
	onChildAdded_: function(child) {
		this.$supers('onChildAdded_', arguments);
		if (this.childReplacing_) //called by onChildReplaced_
			this._fixOnAdd(child, true);
		else if (this.desktop)
            this._fixOnAdd(child, true); // fixed dynamically change treerow. B65-ZK-1608
		if (this.desktop && child.$instanceof(zul.sel.Treerow)) {
		//	_syncTreeBodyHeight(this); ZK-360: fixed; ZK-1735: dont need to sync
		}
		//else was handled by insertBefore/appendChild
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
		zul.sel.Treeitem._syncSelItems(this, newwgt);
		if (this.treechildren)
			this.treechildren.detach();
		this.$supers('replaceWidget', arguments);
	},
	_removeChildHTML: function (n) {
		for(var cn, w = this.firstChild; w; w = w.nextSibling) {
			if (w != this.treerow && (cn = w.$n()))
				w.removeHTML_(cn);
		}
	},
	_renderChildHTML: (function () {
		function _getTreePath(tree, node) {
			var p = node,
				paths = [p.getChildIndex()];
			while (p) {
				p = p.parent.parent;
				if (p.$instanceof(zul.sel.Treeitem)) {
					paths.unshift(p.getChildIndex());
				} else {
					break;
				}
			}
			return paths;
		}
		
		// return -1 if thisPath is before itemPath,
		// return 1 if thisPath is after itemPath,
		function _compareTreePath(thisPath, itemPath) {
			var depth = 0;
			while (true) {
				if (thisPath[depth] < itemPath[depth]) {
					return -1;
				} else if (thisPath[depth] > itemPath[depth]) {
					return 1;
				} else if (thisPath[depth] == itemPath[depth]) {
					if (thisPath[depth] == undefined) //just in case, it should never be run into this line.
						break;
					depth++;
					continue;
				} else {
					if (thisPath[depth] == undefined) { // shorter is at before
						return -1;
					} else {
						return 1;
					}
				}
			}
			return 1;
		}
		return function (childHTML) {
			var tree = this.getTree(),
				erows = tree.$n('rows');
			
			// has children
			if (erows && erows.childNodes.length) {
				// do binary search for the insertion point
				var low = 0,
					children = erows.childNodes,
					high = children.length - 1,
					mid = 0,
					thisPath = _getTreePath(tree, this),
					treePaths = {}; // store the calculated path for performance.
				
				treePaths[this.uuid] = thisPath;
				while (low <= high) {
					mid = (low + high) >>> 1;
					
					var item = zk.Widget.$(children[mid].id).parent,
						itemPath = treePaths[item.uuid];
					
					if (!itemPath) {
						itemPath = _getTreePath(tree, item);
						treePaths[item.uuid] = itemPath;
					}
					if (_compareTreePath(thisPath, itemPath) == 1) {
						low = mid + 1;
					} else {
						high = mid - 1;
						if (low >= high)
							mid -= 1;
						
					}
				}
				
				if (mid >= 0)
					jq(children[mid]).after(childHTML);
				else if (erows.firstChild) // the first one
					jq(erows.firstChild).before(childHTML);
				else
					jq(erows).append(childHTML);
			} else {
				jq(erows).append(childHTML);
			}
			
		};
	})(),
	insertChildHTML_: function (child, before, desktop) {
		if (before = before ? before.getFirstNode_(): null)
			jq(before).before(child.redrawHTML_());
		else
			this._renderChildHTML(child.redrawHTML_());
		
		child.bind(desktop);
	},
	getOldWidget_: function (n) {
		var old = this.$supers('getOldWidget_', arguments);
		if (old && old.$instanceof(zul.sel.Treerow))
			return old.parent;
		return old;
	},
	replaceHTML: function (n, desktop, skipper) {
		this._removeChildHTML(n);
		this.$supers('replaceHTML', arguments);
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
},{
	//package utiltiy: sync selected items for replaceWidget
	_syncSelItems: function (oldwgt, newwgt) {
		var items;
		if ((items = oldwgt.getTree()) && (items = items._selItems))
			if (oldwgt.$instanceof(zul.sel.Treechildren)) {
				for (var item = oldwgt.firstChild; item; item = item.nextSibling)
					_rmSelItemsDown(items, item)
				for (var item = newwgt.firstChild; item; item = item.nextSibling)
					_addSelItemsDown(items, item);
			} else { //Treeitem
				_rmSelItemsDown(items, oldwgt)
				_addSelItemsDown(items, newwgt);
			}
	}
});
})();