/* South.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:58     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A south region of a border layout.
 * <p>Default {@link #getZclass}: z-south.
 */
zul.layout.South = zk.$extends(_zkf = zul.layout.LayoutRegion, {
	_sumFlexHeight: true, //indicate shall add this flex height for borderlayout. @See _setMinFlexSize in widget.js
	/**
	 * The width can't be specified in this component because its width is
	 * determined by other region components ({@link West} or {@link East}).
	 * @param String width
	 */
	setWidth: zk.$void, // readonly
	sanchor: 'b',
	/**
	 * Returns {@link Borderlayout#SOUTH}.
	 * @return String
	 */
	getPosition: function () {
		return zul.layout.Borderlayout.SOUTH;
	},
	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getHeight()}.
	 * @return String
	 */
	getSize: _zkf.prototype.getHeight,
	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link #setHeight(String)}.
	 * @param String size
	 */
	setSize: _zkf.prototype.setHeight,

	_ambit2: function (ambit, mars, split) {
		ambit.w = mars.left + mars.right;
		ambit.h += split.offsetHeight;
		ambit.ts = ambit.y + ambit.h + mars.bottom; // total size;
		ambit.y = ambit.h + mars.bottom;
	},
	_reszSp2: function (ambit, split) {
		ambit.h -= split.h;
		ambit.y += split.h;
		return {
			left: jq.px0(ambit.x),
			top: jq.px0(ambit.y - split.h),
			width: jq.px0(ambit.w)
		};
	}
});