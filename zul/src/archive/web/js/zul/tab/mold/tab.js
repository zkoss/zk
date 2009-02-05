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
		uuid = this.uuid;;
	out.push('<li ', this.domAttrs_(), '>');
	if (this.isClosable()) {
		out.push('<a id="', uuid, '$close" class="', zcls, '-close"', 'onClick="return false;" ></a>');
	}
	out.push('<a id="', uuid, '$a" class="', zcls, '-body"', 'onClick="return false;" href="#">','<em id="', uuid, '$em">');
	if (this.isClosable())
		out.push('<span id="',uuid, '$inner" class="',zcls, '-inner ', zcls, '-close-inner">');
	else
		out.push('<span id="',uuid, '$inner" class="',zcls, '-inner ">');
	out.push('<span class="', zcls, '-text">',this.domContent_(),'</span></span></em></a></li>');
}