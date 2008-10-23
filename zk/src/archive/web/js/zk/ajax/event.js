/* event.js

{{IS_NOTE
	Purpose:
		ZK Event Handling
	Description:
		
	History:
		Thu Oct 23 10:53:17     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zkEvt = {
	/** Returns the mouse status.
	 */
	mouseData: function (evt, target) {
		var extra = "";
		if (evt.altKey) extra += "a";
		if (evt.ctrlKey) extra += "c";
		if (evt.shiftKey) extra += "s";

		var ofs = zkDom.cmOffset(target);
		var x = zkEvt.x(evt) - ofs[0];
		var y = zkEvt.y(evt) - ofs[1];
		return [x, y, extra];
	}
};
