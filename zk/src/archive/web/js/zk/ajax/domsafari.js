/* domsafari.js

	Purpose:
		Enhance/fix jQuery for Safari
	Description:
		
	History:
		Fri Jun 12 12:03:53     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zjq._sfKeys = {
	25: 9, 	   // SHIFT-TAB
	63232: 38, // up
	63233: 40, // down
	63234: 37, // left
	63235: 39, // right
	63272: 46, // delete
	63273: 36, // home
	63275: 35, // end
	63276: 33, // pgup
	63277: 34  // pgdn
};
zk.override(jq.event, zjq._evt = {}, {
	fix: function (evt) {
		evt = zjq._evt.fix(evt);
		var v = zjq._sfKeys[evt.keyCode];
		if (v) evt.keyCode = v;
		return evt;
	}
});
