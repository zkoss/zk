/* Treeitem.ts

	Purpose:

	Description:

	History:
		Wed Jun 10 15:32:43     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
//test if a treexxx is closed or any parent treeitem is closed
function _closed(treechildren: zul.sel.Treechildren | null): boolean {
	interface WidgetWithOpen extends zk.Widget {
		isOpen?(): boolean;
	}
	for (let ti: WidgetWithOpen | null = treechildren; ti && !(ti instanceof zul.sel.Tree); ti = ti.parent)
		if (ti.isOpen && !ti.isOpen())
			return true;
	return false;
}

function _rmSelItemsDown(items: zul.sel.Treeitem[], wgt: zul.sel.Treeitem): void {
	if (wgt.isSelected())
		items.$remove(wgt);
	for (let w = wgt.treechildren?.firstChild; w && items.length; w = w.nextSibling)
		_rmSelItemsDown(items, w);
}
function _addSelItemsDown(items: zul.sel.Treeitem[], wgt: zul.sel.Treeitem): void {
	if (wgt.isSelected())
		items.push(wgt);
	for (let w = wgt.treechildren?.firstChild; w; w = w.nextSibling)
		_addSelItemsDown(items, w);
}

function _showDOM(wgt: zul.sel.Treeitem, visible: boolean | null | undefined): void {
	const n = wgt.$n();
	if (n)
		n.style.display = visible ? '' : 'none';
	for (let w = wgt.treechildren?.firstChild; w; w = w.nextSibling)
		if (w._visible && w._open) // optimized, need to recurse only if open and visible
			_showDOM(w, visible);
}

function _getTreePath(tree: zul.sel.Tree | null, node: zul.sel.Treeitem | null): number[] {
	const paths: number[] = [];
	for (let p: zk.Widget | null = node; p && p instanceof zul.sel.Treeitem; p = p.parent!.parent)
		paths.unshift(p.getChildIndex());
	return paths;
}

// return -1 if thisPath is before itemPath,
// return 1 if thisPath is after itemPath,
function _compareTreePath(thisPath: number[], itemPath: number[]): 1 | -1 {
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
@zk.WrapClass('zul.sel.Treeitem')
export class Treeitem extends zul.sel.ItemWidget {
	override parent!: zul.sel.Treechildren | null;
	override firstChild!: zul.sel.Treerow | zul.sel.Treechildren | null;
	override lastChild!: zul.sel.Treerow | zul.sel.Treechildren | null;
	override nextSibling!: zul.sel.Treeitem | null;
	override previousSibling!: zul.sel.Treeitem | null;

	_open = true;
	treerow?: zul.sel.Treerow | null;
	treechildren?: zul.sel.Treechildren | null;

	/** Returns whether this container is open.
	 * <p>Default: true.
	 * @return boolean
	 */
	isOpen(): boolean {
		return this._open;
	}

	/** Sets whether this container is open.
	 * @param boolean open
	 */
	// FIXME: can a defSet generated setter accept more than one arguments before `opts`?
	setOpen(open: boolean, fromServer?: boolean, opts?: Record<string, boolean>): this {
		const o = this._open;
		this._open = open;

		if (o !== open || (opts && opts.force)) {
			var img = this.$n('open'),
				icon = this.$n('icon');
			if (!img || _closed(this.parent)) {
				if (icon) {
					// B65-ZK-1609: Tree close/open icon is not correct after calling clearOpen and reopen a node
					var cn = icon.className;
					icon.className = open ?
						cn.replace('-right', '-down').replace('-close', '-open') :
						cn.replace('-down', '-right').replace('-open', '-close');
				}
				return this;
			}

			// (just in case)
			if (icon) {
				var cn = icon.className;
				icon.className = open ?
					cn.replace('-right', '-down').replace('-close', '-open') :
					cn.replace('-down', '-right').replace('-open', '-close');
			}

			var tree = this.getTree(),
				ebodytbl = tree ? tree.ebodytbl : null,
				oldwd = ebodytbl ? ebodytbl.clientWidth : 0; // ebodytbl shall not be null

			if (!open)
				zWatch.fireDown('onHide', this);
			this._showKids(open);
			if (open) {
				zUtl.fireShown(this);
				tree!._updHeaderCM();
			}
			if (tree) {
				tree._sizeOnOpen();

				if (!fromServer)
					this.fire('onOpen', {open: open},
							{toServer: tree.inPagingMold() || tree.isModel()});

				tree._syncFocus(this);

				if (ebodytbl) {
					tree._fixhdwcnt = tree._fixhdwcnt || 0;
					if (!tree._fixhdwcnt++)
						tree._fixhdoldwd = oldwd;
					setTimeout(function () {
						if (!--tree!._fixhdwcnt!
								&& tree!.$n()
								&& (tree!._fixhdoldwd != ebodytbl!.clientWidth))
							tree!._calcSize();
					}, 250);
				}
			}
		}

		return this;
	}

	_showKids(open: boolean): void {
		var tc = this.treechildren;
		if (tc)
			for (var w = tc.firstChild, vi = tc._isRealVisible(); w; w = w.nextSibling) {
				var n = w.$n();
				if (n)
					n.style.display = vi && w.isVisible() && open ? '' : 'none';
				if (w.isOpen())
					w._showKids(open);
			}
	}

	override isStripeable_(): boolean {
		return false;
	}

	/**
	 * Returns the mesh widget. i.e. {@link Tree}
	 * @return Tree
	 */
	override getMeshWidget(): zul.sel.Tree | null {
		return this.parent ? this.parent.getTree() : null;
	}

	/**
	 * Returns the {@link Tree}.
	 * @return Tree
	 * @see #getMeshWidget
	 */
	getTree = Treeitem.prototype.getMeshWidget;

	override getZclass(): string {
		// NOTE: Dead code. Treeitem is not rendered. A treerow is rendered instead,
		// so this function will not be called.
		if (this.treerow) return this.treerow.getZclass();
		return '';
	}

	override $n(): HTMLTableRowElement | null | undefined
	override $n(nm?: string): HTMLElement | null | undefined
	override $n(nm?: string): HTMLElement | null | undefined {
		if (this.treerow)
			return nm ? this.treerow.$n(nm) : this.treerow.$n() || jq(this.treerow.uuid, zk)[0];
		return null;
	}

	/** Returns whether the element is to act as a container
	 * which can have child elements.
	 * @return boolean
	 */
	isContainer(): boolean {
		return this.treechildren != null;
	}

	/** Returns whether this element contains no child elements.
	 * @return boolean
	 */
	isEmpty(): boolean {
		return !this.treechildren || !this.treechildren.nChildren;
	}

	/** Returns the level this cell is. The root is level 0.
	 * @return int
	 */
	getLevel(): number {
		var level = 0;
		for (var item: zul.sel.Tree | zul.sel.Treeitem | null = this; ; ++level) {
			if (!item.parent)
				break;

			item = item.parent.parent;
			if (!item || item instanceof zul.sel.Tree)
				break;
		}
		return level;
	}

	/** Returns the label of the {@link Treecell} it contains, or null
	 * if no such cell.
	 * @return String
	 */
	override getLabel(): string | null {
		var cell = this.getFirstCell();
		return cell ? cell.getLabel() : null;
	}

	/** Sets the label of the {@link Treecell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 * @param String label
	 */
	setLabel(label: string): void {
		this._autoFirstCell().setLabel(label);
	}

	/**
	 * Returns the first treecell.
	 * @return Treecell
	 */
	getFirstCell(): zul.sel.Treecell | null {
		return this.treerow ? this.treerow.firstChild : null;
	}

	_autoFirstCell(): zul.sel.Treecell {
		if (!this.treerow)
			this.appendChild(new zul.sel.Treerow());

		var cell = this.treerow!.firstChild;
		if (!cell) {
			cell = new zul.sel.Treecell();
			this.treerow!.appendChild(cell);
		}
		return cell;
	}

	/** Returns the image of the {@link Treecell} it contains.
	 * @return String
	 */
	getImage(): string | null | undefined {
		var cell = this.getFirstCell();
		return cell ? cell.getImage() : null;
	}

	/** Sets the image of the {@link Treecell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 * @param String image
	 * @return Treeitem
	 */
	setImage(image: string): this {
		this._autoFirstCell().setImage(image);
		return this;
	}

	/** Returns the parent tree item,
	 * or null if this item is already the top level of the tree.
	 * The parent tree item is actually the grandparent if any.
	 * @return Treeitem
	 */
	getParentItem(): zul.sel.Treeitem | null {
		const p = this.parent?.parent; // null/undefined are not instances of any Object
		return p instanceof zul.sel.Treeitem ? p : null;
	}

	_isRealVisible(): boolean | null | undefined {
		const p = this.parent;
		return this.isVisible() && p && p._isRealVisible();
	}

	_isVisibleInTree(): boolean | undefined {
		// used by Treecell#_isLastVisibleChild
		if (!this.isVisible())
			return;
		var c = this.parent,
			p: zul.sel.Tree | zul.sel.Treeitem | null;
		if (!c || !c.isVisible() || !(p = c.parent))
			return false;
		if (p instanceof zul.sel.Tree)
			return true;
		// Treeitem
		return p._isVisibleInTree(); // timing issue, does not concern open state
	}

	override setVisible(visible: boolean | undefined): void {
		if (this.isVisible() != visible) {
			super.setVisible(visible);
			if (this.treerow) this.treerow.setVisible(visible);
			// Bug: B50-3293724
			_showDOM(this, this._isRealVisible());
		}
	}

	override beforeParentChanged_(newParent: zul.sel.Treechildren | null): void {
		var oldtree = this.getTree();
		if (oldtree)
			oldtree._onTreeitemRemoved(this);

		if (newParent) {
			var tree = newParent.getTree();
			if (tree)
				tree._onTreeitemAdded(this);
		}
		super.beforeParentChanged_(newParent);
	}

	//@Override
	override isRealElement(): boolean {
		return false; // fixed for ZK Client selector issue
	}

	//@Override
	override insertBefore(child: zk.Widget, sibling: zk.Widget | null | undefined, ignoreDom?: boolean): boolean {
		if (super.insertBefore(child, sibling,
		ignoreDom || (!this.z_rod && child instanceof zul.sel.Treechildren))) {
			this._fixOnAdd(child, ignoreDom);
			return true;
		}
		return false;
	}

	//@Override
	override appendChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		if (super.appendChild(child,
		ignoreDom || (!this.z_rod && child instanceof zul.sel.Treechildren))) {
			if (!this.insertingBefore_)
				this._fixOnAdd(child, ignoreDom);
			return true;
		}
		return false;
	}

	_fixOnAdd(child: zk.Widget, ignoreDom?: boolean): void {
		if (child instanceof zul.sel.Treerow)
			this.treerow = child;
		else if (child instanceof zul.sel.Treechildren) {
			this.treechildren = child;
			if (!ignoreDom && this.treerow)
				this.rerender();
		}
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (child == this.treerow) {
			this.treerow = null;
		} else if (child == this.treechildren) {
			this.treechildren = null;
			if (!this.childReplacing_) //NOT called by onChildReplaced_
				this._syncIcon(true); // remove the icon
		}
	}

	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (this.childReplacing_) //called by onChildReplaced_
			this._fixOnAdd(child, true);
		else if (this.desktop)
			this._fixOnAdd(child, true); // fixed dynamically change treerow. B65-ZK-1608
	}

	override removeHTML_(n: HTMLElement | HTMLElement[]): void {
		for (var w: zk.Widget | null = this.firstChild; w; w = w.nextSibling) {
			const cn = w.$n();
			if (cn)
				w.removeHTML_(cn);
		}
		super.removeHTML_(n);
	}

	override replaceWidget(newwgt: zul.sel.Treeitem, skipper?: zk.Skipper): void {
		zul.sel.Treeitem._syncSelItems(this, newwgt);
		if (this.treechildren)
			this.treechildren.detach();
		super.replaceWidget(newwgt, skipper);
	}

	_removeChildHTML(n: HTMLElement | string): void {
		for (var cn: HTMLElement | null | undefined, w = this.firstChild; w; w = w.nextSibling) {
			if (w != this.treerow && (cn = w.$n()))
				w.removeHTML_(cn);
		}
	}

	_renderChildHTML(childHTML: string): void {
		var tree = this.getTree()!,
			erows = tree.ebodyrows;

		// has children
		if (erows && erows.childNodes.length) {
			// do binary search for the insertion point
			var low = 0,
				children = erows.childNodes as NodeListOf<HTMLElement>,
				high = children.length - 1,
				mid = 0,
				thisPath = _getTreePath(tree, this);

			while (low <= high) {
				mid = (low + high) >>> 1;

				var item = zk.Widget.$<zul.sel.Treerow>(children[mid].id)!.parent,
					itemPath = _getTreePath(tree, item);

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
			jq(erows!).append(childHTML);
		}
	}

	override insertChildHTML_(child: zk.Widget, before?: zk.Widget | null, desktop?: zk.Desktop | null): void {
		const nodeOfBefore = before ? before.getFirstNode_() : null;
		if (nodeOfBefore)
			jq(nodeOfBefore).before(child.redrawHTML_());
		else
			this._renderChildHTML(child.redrawHTML_());

		child.bind(desktop);
	}

	override getOldWidget_(n: HTMLElement | string): zk.Widget | null | undefined {
		var old = super.getOldWidget_(n);
		if (old && old instanceof zul.sel.Treerow)
			return old.parent;
		return old;
	}

	override replaceHTML(n: HTMLElement | string, desktop: zk.Desktop | null, skipper?: zk.Skipper | null, _trim_?: boolean, _callback_?: CallableFunction[]): void {
		this._removeChildHTML(n);
		super.replaceHTML(n, desktop, skipper, _trim_, _callback_);
	}

	_syncIcon(isRemoved?: boolean): void {
		if (this.desktop && this.treerow) {
			const treecell = this.treerow.firstChild;
			if (treecell)
				treecell._syncIcon(isRemoved);
			for (let i = this.treechildren?.firstChild; i; i = i.nextSibling)
				i._syncIcon(isRemoved);
		}
	}

	//@Override
	override compareItemPos_(item: zul.sel.Treeitem): number {
		if (this == item)
			return 0;
		var tree = this.getTree();
		return _compareTreePath(_getTreePath(tree, item), _getTreePath(tree, this));
	}

	//package utiltiy: sync selected items for replaceWidget
	static _syncSelItems<T extends (zul.sel.Treeitem | zul.sel.Treechildren)>(oldwgt: T, newwgt: T): void {
		const items = oldwgt.getTree()?._selItems;
		if (items)
			if (oldwgt instanceof zul.sel.Treechildren) { // If true, newwgt would also be Treechildren.
				for (var item = oldwgt.firstChild; item; item = item.nextSibling)
					_rmSelItemsDown(items, item);
				for (var item = (newwgt as zul.sel.Treechildren).firstChild; item; item = item.nextSibling)
					_addSelItemsDown(items, item);
			} else { // Both oldwgt and newwgt would be Treeitem.
				_rmSelItemsDown(items, oldwgt);
				_addSelItemsDown(items, newwgt as zul.sel.Treeitem);
			}
	}
}