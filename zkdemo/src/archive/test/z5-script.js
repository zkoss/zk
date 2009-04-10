/* z5-script.js

	Purpose:
		
	Description:
		
	History:
		Thu Dec 11 15:53:54     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var cnt = 0;
function appendTo(wgt) {
	var l = new zul.wgt.Label();
	l.setValue(' ' + ++cnt);
	wgt.appendChild(l);
}

function createWidgets(parent) {
	zk.zuml.Parser.createWidgets(parent,
		'<div forEach="${[1, 2]}">${each}: <textbox value="${each}"/></div>');
}
