/*--
xspfPlayer.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 19 12:01:12     2007, Created by jeffliu
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--*/
zkXspf = {};

zkXspf.init = function (cmp) {

};

zkXspf.cleanup = function (cmp) {

};

zkXspf.setAttr = function (cmp, name, value) {
	switch(name)
	{
		case "z:stop":
		{

			$e(cmp.id).innerHTML = "";
			return true;
		}
	}
    return false;

};