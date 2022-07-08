/* Foot.ts

	Purpose:

	Description:

	History:
		Fri Jan 23 12:26:51     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Defines a set of footers ({@link Footer}) for a grid ({@link Grid}).
 * <p>Default {@link #getZclass}: z-foot.
 */
@zk.WrapClass('zul.grid.Foot')
export class Foot extends zul.Widget {
	public override parent!: zul.grid.Grid | null;
	/** Returns the grid that contains this column.
	 * @return zul.grid.Grid
	 */
	public getGrid(): zul.grid.Grid | null {
		return this.parent;
	}

	//bug #3014664
	public override setVflex(v: boolean | string | null | undefined): void { //vflex ignored for grid Foot
		v = false;
		super.setVflex(v);
	}

	//bug #3014664
	public override setHflex(v: boolean | string | null | undefined): void { //hflex ignored for grid Foot
		v = false;
		super.setHflex(v);
	}

	protected override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tr>');
	}
}