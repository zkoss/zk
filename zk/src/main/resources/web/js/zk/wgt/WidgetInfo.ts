/* WidgetInfo.ts

	Purpose:
		
	Description:
		
	History:
		Thu Sep  3 14:30:38     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var _wgtInfs = {page: 'zk.Page'};

function _load(pkgs: string[], f: (() => void), weave: boolean): void {
	zk.load(pkgs.join(','), weave ? function () {
		for (var j = pkgs.length, nm: string; --j >= 0;) {
			if ((zk.$package(nm = pkgs[j]) as Record<string, unknown>).$wv)
				zk.load(nm + '.wv');
		}
		zk.afterLoad(f);
	} : f);
}

/** Utilities to handle widgets, such as the information about widgets.
 */
//zk.$package('zk.wgt');

/**
 * @class zk.wgt.WidgetInfo
 * Information of widgets.
 * <p>When this package (zk.wgt) is loaded, all widget informations defined
 * in the server are loaded automatically.
 */
export var WidgetInfo = {
	/**
	 * A map ({@link Map}) of widget informations (readonly).
	 * The key is the widget name, such as textbox, and the value is
	 * the class name. However the value might be changed in the future,
	 * so it is better to iterate only the keys.
	 * <p>To add a mapping, use {@link register}.
	 * @type Map
	 */
	all: _wgtInfs,
	/**
	 * @returns the class name of the widget, such as `"zul.inp.Textbox"`.
	 * @param wgtnm - the widget name, such as textbox
	 */
	getClassName(wgtnm: string): string {
		return _wgtInfs[wgtnm] as string;
	},
	/**
	 * Registers an arry of widget information.
	 * @param infs - an array of widget class names. For example,
	 * `['zul.wnd.Window', 'zul.inp.Textbox']`.
	 */
	register(infs: string[]): void {
		for (var i = 0, len = infs.length; i < len; ++i) {
			var clsnm = infs[i],
				j = clsnm.lastIndexOf('.'),
				wgtnm = j >= 0 ? clsnm.substring(j + 1) : clsnm;
			_wgtInfs[wgtnm.substring(0, 1).toLowerCase() + wgtnm.substring(1)] = clsnm;
		}
	},
	/**
	 * Loads all packages requires by all widgets.
	 * It is usually used in a visual designer, such as ZK Weaver, to
	 * make sure the classes of all widgets are available to use.
	 * @param f - the function to run after all packages are loaded
	 * @param weave - whether to load the package used for ZK weaver.
	 * For example, if a package is called zul.wnd, then we assume zul.wnd.wv is the package
	 * for widgets defined in zul.wnd to communicate with ZK Weaver.
	 */
	loadAll(f: () => void, weave: boolean): void {
		var pkgmap = {}, pkgs: string[] = [];
		for (var w in _wgtInfs) {
			var clsnm = _wgtInfs[w] as string;
			pkgmap[clsnm.substring(0, clsnm.lastIndexOf('.'))] = true;
		}
		for (var w in pkgmap)
			pkgs.push(w);
		_load(pkgs, f, weave);
	}
};
zk.wgt.WidgetInfo = WidgetInfo;
