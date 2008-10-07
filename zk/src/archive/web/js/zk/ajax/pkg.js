/* pkg.js

{{IS_NOTE
	Purpose:
		zkPkg: the package utilities
	Description:
		
	History:
		Tue Oct  7 16:32:04     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zkPkg = {
	/** Called after the whole package is declared.
	 * It must be the last statement in a JavaScript file.
	 */
	end: function (lang, pkg, wgts) {
		pkg = zk.$import(pkg);
		for (var j = wgts.length; --j >= 0;)
			pkg[wgts[j]].prototype.language = lang;

		//TODO: tell zkau the JS file is loaded
	},
	/** Loads the specified package.
	 */
	load: function (pkg) {
		var zw = zk.Widget;
		if (!zw._pkgLds[pkg]) {
		}
	},
	_pkgLds: {},

	/** Sets the package's version.
	 */
	setVersion: function (pkg, ver) {
		var args = arguments, len = args.length;
		if (len > 2) {
			for (var j = 0; j < len; j += 2)
				zkPkg.setVersion(args[j], args[j + 1]);
		} else if (pkg && ver)
			zkPkg._pkgVers[pkg] = ver;
	},
	/** Returns the version of the specified package, or null if not available.
	 */
	getVersion: function (pkg) {
		return zkPkg._pkgVers[pkg];
	},
	_pkgVers: {}
};
