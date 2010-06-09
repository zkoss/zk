/* WidgetInfo.js

	Purpose:
		
	Description:
		
	History:
		Thu Sep  3 14:30:38     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
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

/** Utilities to handle widgets, such as the information about widgets.
 */
//zk.$package('zk.wgt');

/** @class zk.wgt.WidgetInfo
 * Information of widgets.
 * <p>Whne this package (zk.wgt) is loaded, all widget informations defined
 * in the server are loaded automatically.
 */
zk.wgt.WidgetInfo = {
	/** A map ({@link Map}) of widget informations (readonly).
	 * The key is the widget name, such as textbox, and the value is
	 * the class name. However the value might be changed in the future,
	 * so it is better to iterate only the keys.
	 * <p>To add a mapping, use {@link #register}.
	 * @type Map
	 */
	all: _wgtInfs,
	/** Returns the class name of the widget.
	 * @param String wgtnm the widget name, such as textbox
	 * @return String the class name, such as zul.inp.Textbox
	 */
	getClassName: function (wgtnm) {
		return _wgtInfs[wgtnm];
	},
	/** Registers an arry of widget information.
	 * @param Array infs an array of widget class names. For example,
	 * ['zul.wnd.Window', 'zul.inp.Textbox'].
	 */
	register: function (infs) {
		for (var i = 0, len = infs.length; i < len; ++i) {
			var clsnm = infs[i],
				j = clsnm.lastIndexOf('.'),
				wgtnm = j >= 0 ? clsnm.substring(j + 1): clsnm;
			_wgtInfs[wgtnm.substring(0,1).toLowerCase()+wgtnm.substring(1)] = clsnm;
		}
	},
	/** Loads all packages requires by all widgets.
	 * It is usually used in a visual designer, such as ZK Weaver, to
	 * make sure the classes of all widgets are available to use.
	 * @param Function f the function to run after all packages are loaded
	 * @param boolean weave whether to load the package used for ZK weaver.
	 * For example, if a package is called zul.wnd, then we assume zul.wnd.wv is the package
	 * for widgets defined in zul.wnd to communicate with ZK Weaver.
	 */
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