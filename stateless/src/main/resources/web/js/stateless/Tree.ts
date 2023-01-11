/* Tree.ts

	Purpose:

	Description:

	History:
		12:49 PM 2021/12/22, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
export default {};
zk.afterLoad('zul.sel', () => {
	const xTreeItemIter = zk.augment(zul.sel.TreeItemIter.prototype, {
		/** @internal */
		_init(): void {
			if (!this._isInit) {
				xTreeItemIter._init.call(this);
				const tree = this.tree,
					pagingChild = tree.getPagingChild();

				// if paginal exists and no model case, we do the paging in client
				if (pagingChild && tree.isStateless() && !tree.getModel() && (!this.opts || !this.opts.skipPaging)) {
					const activePage = pagingChild.getActivePage(),
						pageSize = pagingChild.getPageSize();
					this.cur = activePage * pageSize;
					this.length = Math.min(this.length as number, (activePage + 1) * pageSize);

					// fix #105: mark all widgets are z_rod = true by default, except the current active page.
					if (this.opts && this.opts.z_rod) {
						const items = tree.getItems(),
							end = this.length;
						let start = 0;
						for (let i = 0, j = items.length; i < j; i++) {
							if (start < this.cur || start >= end) {
								// use treerow instead.
								items[i].treerow!.z_rod = true;
							}
							if (items[i]._isRealVisible()) {
								start++;
							}
						}
					}
				}
			}
		}
	}),
	xTreechildren = zk.augment(zul.sel.Treechildren.prototype, {
		redraw(out: string[], iter?: zk.Skipper | zul.sel.TreeItemIter): void {
			if (this.parent instanceof zul.sel.Tree) {
				if (this.parent.isModel() || !this.parent.inPagingMold() || !this.parent.isStateless()) {
					xTreechildren.redraw.call(this, out, iter as zk.Skipper | undefined);
				} else {
					// stateless widget and client paging only
					out.push('<tbody id="', this.parent.uuid, '-rows" ', this.domAttrs_({id: true}), '>');
					let iter = this.parent.getBodyWidgetIterator({skipHidden: true, z_rod: true});
					while (iter.hasNext()) {
						iter.next()!.redraw(out, iter as never);
					}
					out.push('</tbody>');
				}
			} else {
				if (iter instanceof zul.sel.TreeItemIter) {
					while (iter.hasNext())
						iter.next()!.redraw(out, iter as never);
				} else {
					xTreechildren.redraw.call(this, out, iter);
				}
			}
		}
	});
	let xTreeitem: Partial<zul.sel.Treeitem> = {};
	zk.override(zul.sel.Treeitem.prototype, xTreeitem, {
		isClientPaging(tree?: zul.sel.Tree): boolean {
			tree = tree ?? this.getTree();
			return !!(tree && tree.isStateless() && !tree.isModel() && tree.getPagingChild() != null);
		},
		redraw(out: string[], iter?: zk.Skipper | zul.sel.TreeItemIter): void {
			if (this.treerow) this.treerow.redraw(out);
			if (this.treechildren) this.treechildren.redraw(out, iter as never);
		},
		/** @internal */
		_showKids(open: boolean): void {
			let tree = this.getTree();
			if (this.isClientPaging(tree)) {
				tree!.paging!._totalSize = 0;// reset cache to recalculate it
				// client Paging case
				if (!tree!.inRerendering_()) {
					tree!.rerender(10);
					return;
				}
			}
			xTreeitem._showKids!.call(this, open);
		},
		fire(evtnm: string, data?: unknown, opts?: zk.EventOptions, timeout?: number): zk.Event {
			if ('onOpen' == evtnm && this.isClientPaging()) {
				// ignore toServer
				if (opts && opts.toServer) {
					opts.toServer = false;
				}
			}
			return xTreeitem.fire!.call(this, evtnm, data, opts, timeout);
		},
		/** @internal */
		getPath_(): number[] {
			let path: number[] = [], parent = this as zul.sel.Treeitem | zul.sel.Treechildren | zul.sel.Tree | undefined;
			while (!(parent instanceof zul.sel.Tree)) {
				if (parent instanceof zul.sel.Treeitem && parent._index != null) {
					path.unshift(parent._index);
				}
				parent = parent!.parent;
			}
			return path;
		}
	});
});