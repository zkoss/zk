/* East.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:59     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var East =
/**
 * An east region of a border layout.
 * <p>Default {@link #getZclass}: z-east.
 * 
 * <p>Default: {@link #getCmargins()} is "0,3,3,0"</p>
 */
zul.layout.East = zk.$extends(_zkf = zul.layout.LayoutRegion, {
	_sumFlexWidth: true, //indicate shall add this flex width for borderlayout. @See _fixMinFlex in widget.js
	_maxFlexHeight: true, //indicate shall check if the maximum flex height for borderlayout. @See _fixMinFlex in widget.js
	
	/**
	 * The height can't be specified in this component because its height is
	 * determined by other region components ({@link North} or {@link South}).
	 * @param String height
	 */
	setHeight: zk.$void, // readonly
	sanchor: 'r',

	$init: function () {
		this.$supers('$init', arguments);
		this._cmargins = [0, 3, 3, 0];
	},
	/**
	 * Returns {@link Borderlayout#EAST}.
	 * @return String
	 */
	getPosition: function () {
		return zul.layout.Borderlayout.EAST;
	},
	/**
	 * Returns the size of this region. This method is shortcut for
	 * {@link #getWidth()}.
	 * @return String
	 */
	getSize: function () {
		// Bug ZK-1490: Cannot find 'getWidth' method in widget.js
		return this.$supers(East, 'getWidth', arguments);
	},
	/**
	 * Sets the size of this region. This method is shortcut for
	 * {@link #setWidth(String)}.
	 * @param String size
	 */
	setSize: function () {
		return this.$supers(East, 'setWidth', arguments);
	},

	_ambit2: function (ambit, mars, split) {
		ambit.w += split.offsetWidth;
		ambit.h = mars.top + mars.bottom;
		ambit.ts = ambit.x + ambit.w + mars.right; // total size;
		ambit.x = ambit.w + mars.right; 
	},
	_reszSp2: function (ambit, split) {
		ambit.w -= split.w;
		ambit.x += split.w;
		return {
			left: jq.px0(ambit.x - split.w),
			top: jq.px0(ambit.y),
			height: jq.px0(ambit.h)
		};
	}
});