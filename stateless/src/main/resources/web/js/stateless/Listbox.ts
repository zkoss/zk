/* Listbox.ts

	Purpose:

	Description:

	History:
		Wed Jan 19 14:39:15 CST 2021, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
export default {};
declare module '@zul/sel/Listbox' {
	interface Listbox {
		isStateless(): boolean;
	}
}
zk.afterLoad('zul.sel', () => {
	const _xItemIter = zk.augment(zul.sel.ItemIter.prototype, {
		cur: undefined as number | undefined,
		length: undefined as number | undefined,
		_init(): void {
			if (!this._isInit) {
				_xItemIter._init.call(this);
				let listbox = this.box,
					pagingChild = listbox.getPagingChild();
				if (this.cur == undefined) this.cur = 0;
				if (this.length == undefined) this.length = listbox._nrows;

				// if paginal exists and no model case, we do the paging in client
				if (pagingChild && listbox.isStateless() && !listbox.getModel() && (!this.opts || !this.opts.skipPaging)) {
					let activePage = pagingChild.getActivePage(),
						pageSize = pagingChild.getPageSize();
					this.cur = activePage * pageSize;
					this.length = Math.min(this.length, (activePage + 1) * pageSize);
					this.p = listbox.getChildAt(this.cur + listbox.firstItem!.getChildIndex());

					// fix #105: mark all widgets are z_rod = true by default, except the current active page.
					if (this.opts && this.opts.z_rod) {
						let w = listbox.firstItem,
							start = 0,
							end = this.length,
							lastitem = listbox.lastItem!.nextSibling;
						while (w && w != lastitem) {
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
	});
});