/* West.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:01     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkex.layout.West = zk.$extends(zkex.layout.LayoutRegion, {
	setHeight: zk.$void, // readonly
	sanchor: 'l',
	$init: function () {
		this.$supers('$init', arguments);
		this.setCmargins("0,5,5,0");
	},
	getPosition: function () {
		return zkex.layout.Borderlayout.WEST;
	},
	getSize: function () {
		return this.getWidth();
	},
	setSize: function (size) {
		this.setWidth(size);
	}
});
