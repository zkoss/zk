/* Center.ts

	Purpose:

	Description:

	History:
		Wed Jan  7 12:15:02     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A center region of a borderlayout.
 * <strong>Note:</strong> This component doesn't support the following method,
 * including {@link #setSplittable(boolean)}, {@link #setOpen(boolean)},
 * {@link #setCollapsible(boolean)}, {@link #setMaxsize(int)},
 * {@link #setMinsize(int)}, {@link #setHeight(String)},
 * {@link #setWidth(String)}, {@link #getSize()}, {@link #setSize(String)},
 * {@link #setSlide(boolean)}, {@link #setSlidable(boolean)}, {@link #setClosable(boolean)}
 * and {@link #setVisible(boolean)}.
 *
 * <p>Default {@link #getZclass}: z-center.
 */
@zk.WrapClass('zul.layout.Center')
export class Center extends zul.layout.LayoutRegion {
	public _sumFlexWidth = true; //indicate shall add this flex width for borderlayout. @See _fixMinFlex in widget.js
	public _maxFlexHeight = true;  //indicate shall check if the maximum flex height for borderlayout. @See _fixMinFlex in widget.js
	protected override _slidable = false; //Center region can't be slided
	protected override _closable = false; //Center region can't be closed

	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link North} or {@link South}).
	 * @param String height
	 */
	public override setHeight(height: string): this { // readonly
		return this;
	}

	/**
	 * The width can't be specified in this component because its width is
	 * determined by other region components ({@link West} or {@link East}).
	 * @param String width
	 */
	public override setWidth(width: string): this { // readonly
		return this;
	}

	/**
	 * This component can't be hidden.
	 * @param boolean visible
	 */
	public override setVisible(visible: boolean): this { // readonly
		return this;
	}

	/**
	 * The size can't be returned in this component.
	 * @return String
	 */
	public getSize = zk.$void;     // readonly

	/**
	 * The size can't be specified in this component.
	 * @param String size
	 */
	public setSize = zk.$void;        // readonly

	/**
	 * Center region can't be enabled the collapsed margin functionality.
	 * @param String cmargins
	 */
	public override setCmargins = zk.$void;        // readonly

	/**
	 * Center region can't be enabled the split functionality.
	 * @param boolean splittable
	 */
	public override setSplittable(splittable: boolean, opts?: Record<string, boolean>): this {// readonly
		return this;
	}

	/**
	 * Center region can't be closed.
	 * @param boolean open
	 */
	public override setOpen(open: boolean, opts?: Record<string, boolean>): this {// readonly
		return this;
	}

	/**
	 * Center region can't be enabled the collapse functionality.
	 * @param boolean collapsible
	 */
	public override setCollapsible(collapsible: boolean, opts?: Record<string, boolean>): this { // readonly
		return this;
	}

	/**
	 * Center region can't be enabled the maxsize.
	 * @param int maxsize
	 */
	public override setMaxsize(maxsize: number): this {// readonly
		return this;
	}

	/**
	 * Center region can't be enabled the minsize.
	 * @param int minsize
	 */
	public override setMinsize(minsize: number): this { // readonly
		return this;
	}

	/**
	 * Center region can't be slided.
	 * @param boolean slide
	 */
	public override setSlide(slide: boolean, opts?: Record<string, boolean>): this { // readonly
		return this;
	}

	/**
	 * Center region can't be slided.
	 * @param boolean slidable
	 */
	public override setSlidable(slidable: boolean): this { // readonly
		return this;
	}

	/**
	 * Center region can't be closed.
	 * @param boolean closable
	 */
	public override setClosable(v: boolean, opts?: Record<string, boolean>): this { // readonly
		return this;
	}

	protected override doMouseOver_ = zk.$void;    // do nothing.
	protected override doMouseOut_ = zk.$void;     // do nothing.
	public override doClick_ = zk.$void;           // do nothing.

	/**
	 * Returns {@link Borderlayout#CENTER}.
	 * @return String
	 */
	public override getPosition(): string {
		return zul.layout.Borderlayout.CENTER;
	}
}