/* East.ts

	Purpose:

	Description:

	History:
		Wed Jan  7 12:14:59     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An east region of a border layout.
 * <p>Default {@link #getZclass}: z-east.
 *
 * <p>Default: {@link #getCmargins()} is "0,0,0,0"</p>
 */
@zk.WrapClass('zul.layout.East')
export class East extends zul.layout.LayoutRegion {
	public _sumFlexWidth = true; //indicate shall add this flex width for borderlayout. @See _fixMinFlex in widget.js
	public _maxFlexHeight = true; //indicate shall check if the maximum flex height for borderlayout. @See _fixMinFlex in widget.js
	protected override _cmargins = [0, 0, 0, 0];
	public override sanchor = 'r';

	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link North} or {@link South}).
	 * @param String height
	 */
	public override setHeight(height: string): this { // readonly
		return this;
	}

	/**
	 * Returns {@link Borderlayout#EAST}.
	 * @return String
	 */
	public override getPosition(): string {
		return zul.layout.Borderlayout.EAST;
	}

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getWidth()}.
	 * @return String
	 */
	public getSize(): string | null | undefined {
		// Bug ZK-1490: Cannot find 'getWidth' method in widget.js
		return this.getWidth();
	}

	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link #setWidth(String)}.
	 * @param String size
	 */
	public setSize(size: string): this {
		return this.setWidth(size);
	}

	protected override _ambit2(ambit: zul.layout.LayoutRegionAmbit, mars: zk.Dimension, split: { offsetWidth; offsetHeight }): void {
		ambit.w += split.offsetWidth;
		ambit.h = mars.height;
		ambit.ts = ambit.x + ambit.w + (mars.width - mars.left); // total size;
		ambit.x = ambit.w + (mars.width - mars.left);
	}

	protected override _reszSp2(ambit: zul.layout.LayoutRegionAmbit, split: { w; h }): Partial<{ left; top; width; height }> {
		ambit.w -= split.w;
		ambit.x += split.w;
		return {
			left: jq.px0(ambit.x - split.w),
			top: jq.px0(ambit.y),
			height: jq.px0(ambit.h)
		};
	}
}