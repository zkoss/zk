/* South.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:58     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.South = zk.$extends(_zkf = zul.layout.LayoutRegion, {
	setWidth: zk.$void, // readonly
	sanchor: 'b',

	getPosition: function () {
		return zul.layout.Borderlayout.SOUTH;
	},
	getSize: _zkf.prototype.getHeight,
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