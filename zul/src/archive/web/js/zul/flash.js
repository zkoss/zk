/* flash.js

{{IS_NOTE
	Purpose:
		flash
	Description:

	History:
		Tue Jul 24 12:01:12     2007, Created by jeffliu
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zkFlash = {};

zkFlash.setAttr = function (cmp, name, value) {

	switch(name) {
	case "style.height":
		$e(cmp.id).height = value;
		$e(cmp.id+"!emb").height = value;
		return true;
	case "style.width":
		$e(cmp.id).width = value;
		$e(cmp.id+"!emb").width = value;
		return true;
	case "play":
		$e(cmp.id+"!emb").play = value;
		return true;
	case "loop":
		$e(cmp.id+"!emb").loop = value;
		return true;
	case "wmode":
		$e(cmp.id+"!emb").wmode = value;
		return true;
	case "bgcolor":
		$e(cmp.id+"!emb").bgcolor = value;
		return true;
	}
    return false;

};

