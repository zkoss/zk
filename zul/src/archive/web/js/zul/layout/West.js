/* West.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:01     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.West = zk.$extends(_zkbc = zul.layout.LayoutRegion, zul.layout._West = {
	setHeight: zk.$void, // readonly
	sanchor: 'l',

	getPosition: function () {
		return zul.layout.Borderlayout.WEST;
	},
	getSize: _zkbc.prototype.getWidth,
	setSize: _zkbc.prototype.setWidth,

	_ambit2: function (ambit, mars, split) {
		ambit.w += split.offsetWidth;
		ambit.h = mars.top + mars.bottom;
		ambit.ts = ambit.x + ambit.w + mars.right; // total size;
	},
	_reszSp2: function (ambit, split) {
		ambit.w -= split.w;
		return {
			left: jq.px(ambit.x + ambit.w),
			top: jq.px(ambit.y),
			height: jq.px(ambit.h)
		};
	}
});
