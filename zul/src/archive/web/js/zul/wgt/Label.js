/* Label.js

	Purpose:
		
	Description:
		
	History:
		Sun Oct  5 00:22:03     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Label = zk.$extends(zul.Widget, {
	value: '',

	//super//
	setAttr: function (nm, val) {
		if ("value" == nm) {
			this.value = val;
			if (this.node)
				this.node.innerHTML = val;
		} else
			$setAttr(nm, val);
	}
}, {
	embedAs: 'value' //retrieve zDom.$() as value
});
