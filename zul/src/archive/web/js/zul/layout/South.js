/* South.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:14:58     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.South = zk.$extends(_zkbc = zul.layout.LayoutRegion, {
	setWidth: zk.$void, // readonly
	sanchor: 'b',

	getPosition: function () {
		return zul.layout.Borderlayout.SOUTH;
	},
	getSize: _zkbc.prototype.getHeight,
	setSize: _zkbc.prototype.setHeight
});