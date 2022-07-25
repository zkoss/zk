/* Treechildren.ts

	Purpose:

	Description:

	History:
		Wed Jun 10 15:32:40     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _prevsib(child: zul.sel.Treeitem): zul.sel.Treeitem | undefined {
	const p = child.parent;
	if (p && p.lastChild == child)
		return child.previousSibling;
}
function _fixOnAdd(oldsib: zul.sel.Treeitem | undefined, child: zul.sel.Treeitem, ignoreDom?: boolean): void {
	if (!ignoreDom) {
		if (oldsib) oldsib._syncIcon();
		const p = child.parent, q = child.previousSibling;
		if (p && p.lastChild == child && q)
			q._syncIcon();
	}
}

function _syncFrozen(wgt: zul.sel.Treechildren): void {
	var tree = wgt.getTree(),
		frozen: zul.mesh.Frozen | undefined;
	if (tree && tree._nativebar && (frozen = tree.frozen))
		frozen._syncFrozen();
}

@zk.WrapClass('zul.sel.Treechildren')
export class Treechildren extends zul.Widget {
	override parent!: zul.sel.Tree | zul.sel.Treeitem | undefined;
	override firstChild!: zul.sel.Treeitem | undefined;
	override lastChild!: zul.sel.Treeitem | undefined;
	override nextSibling!: zul.sel.Treerow | zul.sel.Treechildren | undefined;
	override previousSibling!: zul.sel.Treerow | zul.sel.Treechildren | undefined;

	override bind_(desktop: zk.Desktop | undefined, skipper: zk.Skipper | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onResponse: this});
		var w = this;
		after.push(function () {
			_syncFrozen(w);
		});
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onResponse: this});
		super.unbind_(skipper, after, keepRod);
	}

	onResponse(): void {
		if (this.desktop) {
			var tree = this.getTree();
			if (tree && tree.frozen) {
				tree._shallSyncFrozen = true;
				tree.onSize();
			}
		}
	}

	/** Returns the {@link Tree} instance containing this element.
	 * @return Tree
	 */
	getTree(): zul.sel.Tree | undefined {
		return this.isTopmost() ? this.parent as zul.sel.Tree : this.parent ? (this.parent as zul.sel.Treeitem).getTree() : undefined;
	}

	/** Returns the {@link Treerow} that is associated with
	 * this treechildren, or null if no such treerow.
	 * @return Treerow
	 */
	getLinkedTreerow(): zul.sel.Treerow | undefined {
		// optimised to assume the tree doesn't have treerow property
		return this.parent ? (this.parent as zul.sel.Treeitem).treerow : undefined;
	}

	/** Returns whether this treechildren is topmost.
	 * @return boolean
	 * @since 5.0.6
	 */
	isTopmost(): boolean {
		// null/undefined can only be instanceof null/undefined, respectively
		return this.parent instanceof zul.sel.Tree;
	}

	//@Override
	override isRealElement(): boolean {
		return false; // fixed for ZK Client selector issue
	}

	//@Override
	override insertBefore(child: zul.sel.Treeitem, sibling?: zk.Widget, ignoreDom?: boolean): boolean {
		var oldsib = _prevsib(child);
		if (super.insertBefore(child, sibling, ignoreDom)) {
			_fixOnAdd(oldsib, child, ignoreDom);
			return true;
		}
		return false;
	}

	//@Override
	override appendChild(child: zul.sel.Treeitem, ignoreDom?: boolean): boolean {
		var oldsib = _prevsib(child);
		if (super.appendChild(child, ignoreDom)) {
			if (!this.insertingBefore_)
				_fixOnAdd(oldsib, child, ignoreDom);
			return true;
		}
		return false;
	}

	override insertChildHTML_(child: zk.Widget, before_?: zk.Widget, desktop?: zk.Desktop): void {
		var ben: HTMLElement | undefined,
			isTopmost = this.isTopmost(),
			before: zk.Widget | HTMLElement | undefined = before_;
		if (before)
			before = before.$n() ? before.getFirstNode_() : undefined; //Bug ZK-1424: fine tune performance when open with rod
		if (!before && !isTopmost)
			ben = this.getCaveNode() || this.parent!.getCaveNode();

		if (before)
			jq(before).before(child.redrawHTML_());
		else if (ben)
			jq(ben).after(child.redrawHTML_());
		else {
			if (isTopmost)
				jq(this.parent!.$n_('rows')).append(child.redrawHTML_());
			else
				jq(this).append(child.redrawHTML_());
		}
		child.bind(desktop);
	}

	override getCaveNode(): HTMLElement | undefined {
		for (var w = this.lastChild; w; w = w.previousSibling) {
			let cn = w.getCaveNode();
			if (cn) {
				// Bug 2909820
				if (w.treechildren) {
					const _cn = w.treechildren.getCaveNode();
					if (_cn)
						cn = _cn;
				}
				return cn;
			}
		}
	}

	//@Override
	override isRealVisible(opts?: zk.RealVisibleOptions): boolean {
		return !!this._isRealVisible() && super.isRealVisible(opts);
	}

	_isRealVisible(): boolean {
		const p = this.parent as zul.sel.Treeitem | undefined;
		return this.isVisible() && !!(this.isTopmost() || (p && p.isOpen() && p._isRealVisible()));
	}

	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 * @param Array items
	 * @return Array
	 */
	getItems(items?: zul.sel.Treeitem[], opts?: {skipHidden?: boolean}): zul.sel.Treeitem[] {
		items = items || [];
		var skiphd = opts && opts.skipHidden;
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (!skiphd || w.isVisible()) {
				items.push(w);
				if (w.treechildren && (!skiphd || w.isOpen()))
					w.treechildren.getItems(items, opts);
			}
		return items;
	}

	/** Returns the number of child {@link Treeitem}
	 * including all descendants. The same as {@link #getItems}.size().
	 * <p>Note: the performance is no good.
	 * @return int
	 */
	getItemCount(opts?: {skipHidden?: boolean}): number {
		var sz = 0,
			skiphd = opts && opts.skipHidden;
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (!skiphd || w.isVisible()) {
				sz++;
				if (w.treechildren && (!skiphd || w.isOpen()))
					sz += w.treechildren.getItemCount(opts);
			}
		return sz;
	}

	override beforeParentChanged_(newParent?: zul.sel.Tree | zul.sel.Treeitem): void {
		var oldtree = this.getTree();
		if (oldtree)
			oldtree._onTreechildrenRemoved(this);

		if (newParent) {
			var tree = newParent instanceof zul.sel.Tree ? newParent : newParent.getTree();
			if (tree) tree._onTreechildrenAdded(this);
		}
		super.beforeParentChanged_(newParent);
	}

	override removeHTML_(n: HTMLElement | HTMLElement[]): void {
		for (var w = this.firstChild; w; w = w.nextSibling) {
			const cn = w.$n();
			if (cn)
				w.removeHTML_(cn);
		}
		super.removeHTML_(n);
	}

	override getOldWidget_(n: HTMLElement | string): zk.Widget | undefined {
		var old = super.getOldWidget_(n);
		if (old && old instanceof zul.sel.Treerow) {
			var ti = old.parent;
			if (ti)
				return ti.treechildren;
			return undefined;
		}
		return old;
	}

	// FIXME: make this generic? Note that its super method is generic.
	override $n(nm?: string): HTMLElement | undefined {
		if (this.isTopmost())
			return this.getTree()!.$n('rows');

		// to support @treechildren selector on zk.Widget.$(),
		// the original implementation may not work properly, because
		// its firstChild.$n() may not exists (in ROD case) but itself does, so
		// we use parent.$n() instead here to prevent this issue, and from the
		// other part of the implementation in Treechildren.js seems not to
		// depend on this.$n() if isTopmost() is returned with false
		return this.parent!.$n();
	}

	override replaceWidget(newwgt: zul.sel.Treechildren, skipper?: zk.Skipper): void {
		while (this.firstChild != this.lastChild)
			this.lastChild!.detach();

		if (this.firstChild && this.firstChild.treechildren)
			this.firstChild.treechildren.detach();

		zul.sel.Treeitem._syncSelItems(this, newwgt);

		super.replaceWidget(newwgt, skipper);
	}

	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (this.desktop)
			this.getTree()!._syncSize();
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (this.desktop)
			this.getTree()!._syncSize();
	}

	override replaceChildHTML_(child: zk.Widget, n: HTMLElement | string, desktop?: zk.Desktop, skipper?: zk.Skipper, _trim_?: boolean): void {
		var oldwgt = child.getOldWidget_(n);
		if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
		else if (this.shallChildROD_(child))
			(this.$class as typeof Treechildren)._unbindrod(child); //possible (e.g., Errorbox: jq().replaceWith)
		var content = child.redrawHTML_(skipper, _trim_);
		if (zk.ie11_ || zk.edge_legacy) { // Zk-3371: IE/Edge performance workaround (domie not apply to ie 11)
			var jqelem = jq(n),
				len = content.length,
				elem = jqelem[0];
			if (len > 1048576) {
				var chunkSize = len / 2,
					splitPos = content.lastIndexOf('</tr>', chunkSize),
					chunk1 = content.substr(0, splitPos),
					chunk2 = content.substr(splitPos);
				elem.insertAdjacentHTML('afterend', chunk2);
				elem.insertAdjacentHTML('afterend', chunk1);
			} else {
				elem.insertAdjacentHTML('afterend', content);
			}
			jqelem.remove();
		} else {
			jq(n).replaceWith(content);
		}
		child.bind(desktop, skipper);
	}
}