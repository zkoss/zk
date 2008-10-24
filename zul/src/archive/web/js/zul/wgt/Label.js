/* Label.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  5 00:22:03     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zul.wgt.Label = zk.$extends(zk.Widget, {
	value: ''
}, {
	embedAs: 'value' //retrieve zDom.$() as value
});
