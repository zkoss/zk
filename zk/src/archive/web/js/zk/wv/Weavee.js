/* Weavee.js

	Purpose:
		
	Description:
		
	History:
		Thu Aug 27 18:49:02     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.wv.Weavee = zk.$extends(zk.Object, {
	$init: function (wgt) {
		wgt.weavee = this;
		this.widget = wgt;
	}
},{
	icon: '/web/js/zk/wv/ico/widget.gif'
	//belongs: [foo.Foo] //allowed parents
	//contains: [foo.Foo] //allowed children
});
