/* WidgetInfo.js

	Purpose:
		
	Description:
		
	History:
		Thu Sep  3 14:30:38     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _wgtInfs = {page: 'zk.Page'};

	function _load(pkgs, f, weave) {
		zk.load(pkgs.join(','), weave ? function () {
			for (var j = pkgs.length, nm; --j >= 0;)
				if (zk.$package(nm = pkgs[j]).$wv)
					zk.load(nm + '.wv');
			zk.afterLoad(f);
		}: f);
	}

zk.wgt.WidgetInfo = {
	all: _wgtInfs,
	getClassName: function (wgtnm) {
		return _wgtInfs[wgtnm];
	},
	register: function (infs) {
		for (var i = 0, len = infs.length; i < len; ++i) {
			var clsnm = infs[i],
				j = clsnm.lastIndexOf('.'),
				wgtnm = j >= 0 ? clsnm.substring(j + 1): clsnm;
			_wgtInfs[wgtnm.substring(0,1).toLowerCase()+wgtnm.substring(1)] = clsnm;
		}
	},
	loadAll: function (f, weave) {
		var pkgmap = {}, pkgs = [];
		for (var w in _wgtInfs) {
			var clsnm = _wgtInfs[w];
			pkgmap[clsnm.substring(0, clsnm.lastIndexOf('.'))] = true;
		}
		for (var w in pkgmap)
			pkgs.push(w);
		_load(pkgs, f, weave);
	}
};

})();