/* North.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:57     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.North = zk.$extends(_zkbc = zul.layout.LayoutRegion, zul.layout._North = {
	setWidth: zk.$void, // readonly
	sanchor: 't',

	getPosition: function () {
		return zul.layout.Borderlayout.NORTH;
	},
	getSize: _zkbc.prototype.getHeight,
	setSize: _zkbc.prototype.setHeight
});