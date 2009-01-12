/* North.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:57     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkex.layout.North = zk.$extends(zkex.layout.LayoutRegion, {
	setWidth: zk.$void, // readonly
	sanchor: 't',
	
	getPosition: function () {
		return zkex.layout.Borderlayout.NORTH;
	},
	getSize: function () {
		return this.getHeight();
	},
	setSize: function (size) {
		this.setHeight(size);
	}
});