/* Center.js

	Purpose:
		
	Description:
		
	History:
		Wed Jan  7 12:15:02     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.layout.Center = zk.$extends(zul.layout.LayoutRegion, {
	setCmargins: zk.$void,    // readonly
	setSplittable: zk.$void,  // readonly
	setOpen: zk.$void,        // readonly
	setCollapsible: zk.$void, // readonly
	setMaxsize: zk.$void,     // readonly
	setMinsize: zk.$void,     // readonly
	setHeight: zk.$void,      // readonly
	setWidth: zk.$void,       // readonly
	setVisible: zk.$void,     // readonly
	getSize: zk.$void,        // readonly
	setSize: zk.$void,        // readonly
	doMouseOver_: zk.$void,   // do nothing.
	doMouseOut_: zk.$void,    // do nothing.
	doClick_: zk.$void,       // do nothing.
	
	getPosition: function () {
		return zul.layout.Borderlayout.CENTER;
	}
});