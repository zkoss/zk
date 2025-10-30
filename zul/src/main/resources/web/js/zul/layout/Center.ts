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
 * including {@link setSplittable}, {@link setOpen},
 * {@link setCollapsible}, {@link setMaxsize},
 * {@link setMinsize}, {@link setHeight},
 * {@link setWidth}, {@link getSize}, {@link setSize},
 * {@link setSlide}, {@link setSlidable}, {@link setClosable}
 * and {@link setVisible}.
 *
 * @defaultValue {@link getZclass}: z-center.
 */
@zk.WrapClass('zul.layout.Center')
export class Center extends zul.layout.LayoutRegion {
	/** @internal */
	_sumFlexWidth = true; //indicate shall add this flex width for borderlayout. @See _fixMinFlex in widget.js
	/** @internal */
	_maxFlexHeight = true;  //indicate shall check if the maximum flex height for borderlayout. @See _fixMinFlex in widget.js
	/** @internal */
	override _slidable = false; //Center region can't be slided
	/** @internal */
	override _closable = false; //Center region can't be closed

	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link North} or {@link South}).
	 */
	override setHeight(height: string): this { // readonly
		return this;
	}

	/**
	 * The width can't be specified in this component because its width is
	 * determined by other region components ({@link West} or {@link East}).
	 */
	override setWidth(width: string): this { // readonly
		return this;
	}

	/**
	 * This component can't be hidden.
	 */
	override setVisible(visible: boolean): this { // readonly
		return this;
	}

	/**
	 * The size can't be returned in this component.
	 */
	getSizegetSize(): string | undefined { return; } // readonly

	/**
	 * The size can't be specified in this component.
	 */
	setSize(size: string): this { return this;} // readonly

	/**
	 * Center region can't be enabled the collapsed margin functionality.
	 */
	override setCmargins(cmargins: string): this {        // readonly
		return this;
	}

	/**
	 * Center region can't be enabled the split functionality.
	 */
	override setSplittable(splittable: boolean, opts?: Record<string, boolean>): this {// readonly
		return this;
	}

	/**
	 * Center region can't be closed.
	 */
	override setOpen(open: boolean, opts?: Record<string, boolean>): this {// readonly
		return this;
	}

	/**
	 * Center region can't be enabled the collapse functionality.
	 */
	override setCollapsible(collapsible: boolean, opts?: Record<string, boolean>): this { // readonly
		return this;
	}

	/**
	 * Center region can't be enabled the maxsize.
	 */
	override setMaxsize(maxsize: number): this {// readonly
		return this;
	}

	/**
	 * Center region can't be enabled the minsize.
	 */
	override setMinsize(minsize: number): this { // readonly
		return this;
	}

	/**
	 * Center region can't be slided.
	 */
	override setSlide(slide: boolean, opts?: Record<string, boolean>): this { // readonly
		return this;
	}

	/**
	 * Center region can't be slided.
	 */
	override setSlidable(slidable: boolean): this { // readonly
		return this;
	}

	/**
	 * Center region can't be closed.
	 */
	override setClosable(closable: boolean, opts?: Record<string, boolean>): this { // readonly
		return this;
	}

	/** @internal */
	override doMouseOver_(evt: zk.Event): void { return; }    // do nothing.
	/** @internal */
	override doMouseOut_(evt: zk.Event): void { return; }     // do nothing.
	/** @internal */
	override doClick_(evt: zk.Event): void { return; }        // do nothing.

	/**
	 * @returns The value {@link zul.layout.Borderlayout.CENTER}.
	 */
	override getPosition(): string {
		return zul.layout.Borderlayout.CENTER;
	}
}