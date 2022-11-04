/* West.ts

	Purpose:

	Description:

	History:
		Wed Jan  7 12:15:01     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * A west region of a border layout.
 * @defaultValue {@link getZclass}: z-west.
 * @defaultValue {@link getCmargins} is "0,0,0,0"
 */
@zk.WrapClass('zul.layout.West')
export class West extends zul.layout.LayoutRegion {
	/** @internal */
	_sumFlexWidth = true; //indicate shall add this flex width for borderlayout. @See _fixMinFlex in widget.js
	/** @internal */
	_maxFlexHeight = true; //indicate shall check if the maximum flex height for borderlayout. @See _fixMinFlex in widget.js
	override sanchor = 'l';
	/** @internal */
	override _cmargins = [0, 0, 0, 0];

	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link North} or {@link South}).
	 */
	override setHeight(height: string): this { // readonly
		return this;
	}

	/**
	 * @returns The value {@link zul.layout.Borderlayout.WEST}.
	 */
	override getPosition(): string {
		return zul.layout.Borderlayout.WEST;
	}

	/**
	 * @returns the size of this region. This method is shortcut for {@link getWidth}.
	 */
	getSize(): string | undefined {
		return this.getWidth();
	}

	/**
	 * Sets the size of this region. This method is shortcut for {@link setWidth}.
	 */
	setSize(size: string): this {
		return this.setWidth(size);
	}

	/** @internal */
	override _ambit2(ambit: zul.layout.LayoutRegionAmbit, mars: zk.Dimension, split: { offsetWidth; offsetHeight }): void {
		ambit.w += split.offsetWidth;
		ambit.h = mars.height;
		ambit.ts = ambit.x + ambit.w + (mars.width - mars.left); // total size;
	}

	/** @internal */
	override _reszSp2(ambit: zul.layout.LayoutRegionAmbit, split: { w; h }): Partial<{ left; top; width; height }> {
		ambit.w -= split.w;
		return {
			left: jq.px0(ambit.x + ambit.w),
			top: jq.px0(ambit.y),
			height: jq.px0(ambit.h)
		};
	}
}