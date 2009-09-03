/* WidgetInfo.js

	Purpose:
		
	Description:
		
	History:
		Thu Sep  3 14:30:38     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.wgt.WidgetInfo = {
	getClass: function (wgtnm) {
		return zk.wgt._all[wgtnm];
	},
	register: function (infs) {
		zk.copy(zk.wgt._all, infs);
	},
	loadAll: function (f) {
		var wgtinfs = zk.wgt._all;
		for (var w in wgtinfs) {
			var clsnm = wgtinfs[w],
				j = clsnm.lastIndexOf('.');
			zk.load(clsnm.substring(0, j));
		}
		if (f) zk.afterLoad(f);
	}
};
