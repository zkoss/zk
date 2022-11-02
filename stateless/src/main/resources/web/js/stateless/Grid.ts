/* Grid.ts

	Purpose:

	Description:

	History:
		Wed Jan 19 14:39:15 CST 2021, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
export default {};
declare module '@zul/grid/Grid' {
	interface Grid {
		isStateless(): boolean;
	}
}
zk.afterLoad('zul.grid', () => {
	const _xRowIter = zk.augment(zul.grid.RowIter.prototype, {
		cur: undefined as number | undefined,
		/** @internal */
		_init(): void {
			if (!this._isInit) {
				_xRowIter._init.call(this);
				const grid = this.grid,
					pagingChild = grid.getPagingChild();
				if (!this.cur) this.cur = 0;
				if (!this.length) this.length = grid.rows!.nChildren;

				// if paginal exists and no model case, we do the paging in client
				if (pagingChild && grid.isStateless() && !grid.getModel() && (!this.opts || !this.opts.skipPaging)) {
					const activePage = pagingChild.getActivePage(),
						pageSize = pagingChild.getPageSize();
					this.cur = activePage * pageSize;
					this.length = Math.min(this.length, (activePage + 1) * pageSize);
					this.p = grid.rows!.getChildAt(this.cur);

					// fix #105: mark all widgets are z_rod = true by default, except the current active page.
					if (this.opts && this.opts.z_rod) {
						let w = grid.rows!.firstChild,
							start = 0,
							end = this.length;
						while (w) {
							if (start < this.cur || start >= end) {
								w.z_rod = true;
							}
							w = w.nextSibling;
							start++;
						}
					}
				}
			}
		},
		hasNext(): boolean {
			this._init();
			this.cur!++;
			if (this.cur! > this.length!) return false;
			return !!this.p;
		}
	}),
	_xRows = zk.augment(zul.grid.Rows.prototype, {
		redraw(out: string[], skipper?: zk.Skipper): void {
			if (this.parent instanceof zul.grid.Grid) {
				if (this.parent.isModel() || !this.parent.isStateless()) {
					_xRows.redraw.call(this, out, skipper);
				} else {
					// stateless widget and client paging only
					out.push('<tbody', this.domAttrs_(), ' role="rowgroup">');
					const iter = this.parent.getBodyWidgetIterator({
						skipHidden: true,
						z_rod: true
					});

					while (iter.hasNext()) {
						iter.next()!.redraw(out);
					}
					out.push('</tbody>');
				}
			}
		}
	});
});