/* tab.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:29:16 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	var zcls = this.getZclass(),
		tbx = this.getTabbox(),
		uuid = this.uuid;
	out.push('<li ', this.domAttrs_(), '>');
	if (this.isClosable()) {
		out.push('<a id="', uuid, '$close" class="', zcls, '-close"', 'onClick="return false;" ></a>');
	} else if (tbx.isVertical()) {
		out.push('<a class="', zcls, '-noclose" ></a>');
	}
	out.push('<div id="', uuid, '$hl" class="', zcls, '-hl"><div id="', uuid, '$hr" class="', zcls, '-hr">');
	if (this.isClosable())
		out.push('<div id="',uuid, '$hm" class="',zcls, '-hm ', zcls, '-hm-close">');
	else
		out.push('<div id="',uuid, '$hm" class="',zcls, '-hm ">');
	out.push('<span class="', zcls, '-text">',this.domContent_(),'</span></div></div></div></li>');
}