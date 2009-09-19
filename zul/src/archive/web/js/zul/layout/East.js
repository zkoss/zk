/* East.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:59     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.East = zk.$extends(_zkf = zul.layout.LayoutRegion, zul.layout._East = {
	setHeight: zk.$void, // readonly
	sanchor: 'r',

	getPosition: function () {
		return zul.layout.Borderlayout.EAST;
	},
	getSize: _zkf.prototype.getWidth,
	setSize: _zkf.prototype.setWidth,

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
			left: jq.px(ambit.x - split.w),
			top: jq.px(ambit.y),
			height: jq.px(ambit.h)
		};
	}
});