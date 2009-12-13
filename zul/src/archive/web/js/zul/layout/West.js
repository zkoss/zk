/* West.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:01     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.West = zk.$extends(_zkf = zul.layout.LayoutRegion, {
	setHeight: zk.$void, // readonly
	sanchor: 'l',
	
	$init: function () {
		this.$supers('$init', arguments);
		this.setCmargins("0,3,3,0");
	},
	
	getPosition: function () {
		return zul.layout.Borderlayout.WEST;
	},
	getSize: _zkf.prototype.getWidth,
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
