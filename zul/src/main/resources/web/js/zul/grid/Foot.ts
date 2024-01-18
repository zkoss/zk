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
 * @defaultValue {@link getZclass}: z-foot.
 */
@zk.WrapClass('zul.grid.Foot')
export class Foot extends zul.Widget {
	override parent!: zul.grid.Grid | undefined;
	/**
	 * @returns the grid that contains this column.
	 */
	getGrid(): zul.grid.Grid | undefined {
		return this.parent;
	}

	//bug #3014664
	override setVflex(vflex: boolean | string | undefined): this { //vflex ignored for grid Foot
		const vflex0 = false;
		return super.setVflex(vflex0);
	}

	//bug #3014664
	override setHflex(hflex: boolean | string | undefined): this { //hflex ignored for grid Foot
		const hflex0 = false;
		return super.setHflex(hflex0);
	}

	/** @internal */
	override deferRedrawHTML_(out: string[]): void {
		out.push('<tr', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></tr>');
	}
}