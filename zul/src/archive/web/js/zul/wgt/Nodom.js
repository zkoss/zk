/* Nodom.js

	Purpose:

	Description:

	History:
		Tue Jul 19 15:21:48 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A no-dom widget with comment scope
 * @since 8.0.3
 */
zul.wgt.NoDOM = zk.$extends(zk.Widget, {
	getMold: function () {
		return 'nodom';
	}
});
zk.$intercepts(zul.wgt.NoDOM, zk.NoDOM);
