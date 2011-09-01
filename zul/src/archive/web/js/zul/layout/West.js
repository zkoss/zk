/* West.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:01     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A west region of a border layout.
 * <p>Default {@link #getZclass}: z-west.
 * 
 * <p>Default: {@link #getCmargins()} is "0,3,3,0"</p>
 */
zul.layout.West = zk.$extends(_zkf = zul.layout.LayoutRegion, {
	_sumFlexWidth: true, //indicate shall add this flex width for borderlayout. @See _fixMinFlex in widget.js
	_maxFlexHeight: true, //indicate shall check if the maximum flex height for borderlayout. @See _fixMinFlex in widget.js

	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link North} or {@link South}).
	 * @param String height
	 */
	setHeight: zk.$void, // readonly
	sanchor: 'l',
	
	$init: function () {
		this.$supers('$init', arguments);
		this._cmargins = [0, 3, 3, 0];
	},
	/**
	 * Returns {@link Borderlayout#WEST}.
	 * @return String
	 */
	getPosition: function () {
		return zul.layout.Borderlayout.WEST;
	},
	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getWidth()}.
	 * @return String
	 */
	getSize: _zkf.prototype.getWidth,
	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link #setWidth(String)}.
	 * @param String size
	 */
	setSize: _zkf.prototype.setWidth,

	_ambit2: function (ambit, mars, split) {
		ambit.w += split.offsetWidth;
		ambit.h = mars.top + mars.bottom;
		ambit.ts = ambit.x + ambit.w + mars.right; // total size;
	},
	_reszSp2: function (ambit, split) {
		ambit.w -= split.w;
		return {
			left: jq.px0(ambit.x + ambit.w),
			top: jq.px0(ambit.y),
			height: jq.px0(ambit.h)
		};
	}
});
