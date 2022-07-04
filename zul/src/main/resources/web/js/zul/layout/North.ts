/* North.ts

	Purpose:

	Description:

	History:
		Wed Jan  7 12:14:57     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A north region of a border layout.
 * <p>Default {@link #getZclass}: z-north.
 */
@zk.WrapClass('zul.layout.North')
export class North extends zul.layout.LayoutRegion {
	public _sumFlexHeight = true; //indicate shall add this flex height for borderlayout. @See _fixMinFlex in widget.js
	public override sanchor = 't';
	protected override _cmargins = [0, 0, 0, 0];

	/**
	 * The width can't be specified in this component because its width is
	 * determined by other region components ({@link West} or {@link East}).
	 * @param String width
	 */
	public override setWidth(width: string): this { // readonly
		return this;
	}

	/**
	 * Returns {@link Borderlayout#NORTH}.
	 * @return String
	 */
	public override getPosition(): string {
		return zul.layout.Borderlayout.NORTH;
	}

	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getHeight()}.
	 * @return String
	 */
	public getSize(): string | null | undefined {
		return this.getHeight();
	}

	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link #setHeight(String)}.
	 * @param String size
	 */
	public setSize(size: string): this {
		return this.setHeight(size);
	}

	protected override _ambit2(ambit: zul.layout.LayoutRegionAmbit, mars: zk.Dimension, split: { offsetWidth; offsetHeight }): void {
		ambit.w = mars.width;
		ambit.h += split.offsetHeight;
		ambit.ts = ambit.y + ambit.h + (mars.height - mars.top); // total size;
	}

	protected override _reszSp2(ambit: zul.layout.LayoutRegionAmbit, split: { w; h }): Partial<{ left; top; width; height }> {
		ambit.h -= split.h;
		return {
			left: jq.px0(ambit.x),
			top: jq.px0(ambit.y + ambit.h),
			width: jq.px0(ambit.w)
		};
	}
}