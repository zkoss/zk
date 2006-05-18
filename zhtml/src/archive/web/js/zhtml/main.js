/* main.js

{{IS_NOTE
	$Id: main.js,v 1.7 2006/05/09 07:47:24 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Feb  8 16:07:03     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
function zkRaw() {} //ZHTML components

zkRaw.init = function (cmp) {
	Event.observe(cmp, "click", function (evt) {zkRaw.onclick(evt, cmp);});
	Event.observe(cmp, "change", function () {zkRaw.onchange(cmp);});
	if (zk.agtNav && zk.tagName(cmp) == "FORM")
		Event.observe(cmp, "submit", function () {if (zkau.ignoreResponse) zk.pause(550);});
		//Bug 1480890: if submit in FF, we have to wait a while.
		//Otherwise, Ajax won't be sent
};

zkRaw.onclick = function (evt, cmp) {
	if (cmp.getAttribute("zk_onClick")) {
		//Bug 1480890: if submit in FF, we cannot access request's status
		//otherwise, it throws an exception
		if (zk.agtNav && cmp.type == "submit") {
			var tn = zk.tagName(cmp);
			if (tn == "INPUT" || tn == "BUTTON") {
				var fm = cmp.form;
				if (fm && !zk.isNewWindow(fm.action, fm.target)) {
					zkau.ignoreResponse = true;
					setTimeout("zkau.ignoreResponse = false", 680); //just in case
				}
			}
		}

		zkau.onclick(evt);
	}
};
zkRaw.onchange = function (cmp) {
	if (cmp.getAttribute("zk_onChange"))
		zkau.send({uuid: cmp.id, cmd: "onChange", data: [cmp.value]}, 25);
};
