/* Columns.ts

	Purpose:

	Description:

	History:
		Wed Dec 24 15:25:32     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Defines the columns of a grid.
 * Each child of a columns element should be a {@link Column} element.
 * <p>Default {@link #getZclass}: z-columns.
 */
@zk.WrapClass('zul.grid.Columns')
export class Columns extends zul.mesh.ColumnMenuWidget {
	public override parent!: zul.grid.Grid | null;

	/** Returns the grid that contains this columns.
	 * @return zul.grid.Grid
	 */
	public getGrid(): zul.grid.Grid | null {
		return this.parent;
	}

	public override rerender(skipper?: zk.Skipper | number | null): this {
		if (this.desktop) {
			if (this.parent)
				this.parent.rerender();
			else
				super.rerender(skipper);
		}
		return this;
	}

	public override getGroupPackage_(): string {
		return 'zkex.grid';
	}

	//@Override
	protected override shallFireSizedLaterWhenAddChd_(): boolean {
		zWatch.listen({
			onCommandReady: this
		});
		return true;
	}

	// ZK-4008
	public onCommandReady(): void {
		zUtl.fireSized(this);
		zWatch.unlisten({
			onCommandReady: this
		});
	}
}