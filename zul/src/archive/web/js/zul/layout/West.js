/* West.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:01     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.West = zk.$extends(zul.layout.LayoutRegion, {
	setHeight: zk.$void, // readonly
	sanchor: 'l',

	$init: function () {
		this.$supers('$init', arguments);
		this.setCmargins("0,5,5,0");
	},
	getPosition: function () {
		return zul.layout.Borderlayout.WEST;
	},
	getSize: function () {
		return this.getWidth();
	},
	setSize: function (size) {
		this.setWidth(size);
	}
});
