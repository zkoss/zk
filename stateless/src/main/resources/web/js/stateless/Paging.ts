/* Paging.ts

	Purpose:

	Description:

	History:
		12:19 PM 2021/12/22, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
export default {};
declare module '@zul/mesh/MeshWidget' {
	interface MeshWidget {
		isStateless(): boolean;
	}
}
zk.afterLoad('zul.mesh', () => {
	const xPagingWidget: Partial<zul.mesh.Paging> = {};
	zk.override(zul.mesh.Paging.prototype, xPagingWidget, {
		isClientPaging(meshWidget?: zul.mesh.MeshWidget): boolean {
			meshWidget = meshWidget ?? this.getMeshWidget();
			return meshWidget != null && meshWidget.isStateless() && !meshWidget.isModel();
		},
		rerender(skipper?: zk.Skipper | number): typeof this {
			let meshWidget = this.getMeshWidget();
			if (this.isClientPaging(meshWidget)) {
				if (meshWidget != this.parent) {
					// external paginal
					xPagingWidget.rerender!.call(this, skipper);
				}
				meshWidget!.rerender();
			} else {
				xPagingWidget.rerender!.call(this, skipper);
			}
			return this;
		},
		getPageCount(): number {
			// if in client paging, we recalculate it always.
			if (this.isClientPaging()) {
				var pageCount = Math.floor((this.getTotalSize() - 1) / this._pageSize + 1);
				if (pageCount == 0) pageCount = 1;
				this._pageCount = pageCount;
			}
			return this._pageCount;
		},
		getTotalSize(): number {
			let totalSize = this._totalSize;
			if (totalSize > 0) {
				return totalSize;
			} else {
				let meshWidget = this.getMeshWidget();
				if (meshWidget && this.isClientPaging(meshWidget)) {
					let totalSize = 0,
						iter = meshWidget.getBodyWidgetIterator({skipHidden: true, skipPaging: true});
					if (iter.length) {
						totalSize = iter.length;
					} else {
						while (iter.hasNext()) {
							totalSize++;
							iter.next();
						}
					}
					// catch the result to avoid recalculate each time.
					this._totalSize = totalSize;
					return totalSize;
				} else {
					return totalSize;
				}
			}
		}
	});
});