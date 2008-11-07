/* Box.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 12:10:53     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.box.Box = zk.$extends(zul.Widget, {
	mold: 'vertical',

	//super//
	setAttr: function (nm, val) {
		//TODO
		this.$super('setAttr', nm, val);
	}
});
