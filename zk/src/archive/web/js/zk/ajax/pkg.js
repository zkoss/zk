/* pkg.js

{{IS_NOTE
	Purpose:
		zPkg: the package utilities
	Description:
		
	History:
		Tue Oct  7 16:32:04     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zPkg = {
	/** Called after the whole package is declared.
	 * It must be the last statement in a JavaScript file.
	 */
	end: function (pkg) {
		if (--zk.loading) {
			zk._updCnt();
		} else {
			try {
				zDom.enableESC();
				zDom.cleanAllProgress("zk_loadprog");
			} catch (ex) {
			}

			for (var fn, aflds = zPkg._aflds; fn = aflds.shift();)
				fn();
		}
	},
	/** Loads the specified package.
	 * @param dtid the desktop ID. If null, the first desktop is used.
	 */
	load: function (pkg, dtid) {
		var pkglds = zPkg._pkgLds;
		if (pkglds[pkg]) return;

		pkglds[pkg] = true;

		//We don't use e.onload since Safari doesn't support t
		//See also
		//Bug 1815074: IE bug: zk.ald might be called before appendChild returns

		if (zk.loading++) {
			zPkg._updCnt();
		} else {
			zDom.disableESC();
			setTimeout(zPkg._pgbox, 350);
		}

		var modver = pkg.indexOf('.');
		if (modver) modver = zPkg.version(pkg.substring(0, modver));
		if (!modver) modver = zk.build;

		var e = document.createElement("script"),
			uri = pkg.replace(/\./g, '/') + "/zk.wpd";
		e.type = "text/javascript" ;
		e.charset = "UTF-8";

		if (uri.charAt(0) != '/') uri = '/' + uri;
		if (modver) uri = "/web/_zv" + modver + "/js" + uri;
		else uri = "/web/js" + uri;

		e.src = zAu.comURI(uri, dtid);
		document.getElementsByTagName("HEAD")[0].appendChild(e);
	},
	_pkgLds: {},

	/** Adds a function that shall be executed after loaded.
	 * If zk.loading is true, it is executed immediately.
	 * @return if it was added successfully.
	 * If fn was added, nothing is changed and false is returned.
	 */
	addAfterLoad: function (fn) {
		if (zk.loading)
			return zPkg._aflds.add(fn, true);
		fn();
		return false;
	},
	_aflds: [],

	_updCnt: function () {
		try {
			var n = zDom.$("zk_loadcnt");
			if (n) n.innerHTML = "" + zk.loading;
		} catch (ex) {
		}
	},
	_pgbox: function () {
		if (zk.loading || window.dbg_progressbox) { //dbg_progressbox: debug purpose
			var n = zDom.$("zk_loadprog");
			if (!n)
				zDom.progressbox("zk_loadprog",
					'Loading (<span id="zk_loadcnt">'+zk.loading+'</span>)',
					true);
		}	
	},

	/** Returns or sets the package's version.
	 * <p>If there is only one argument, it must be the package name
	 * and this method returns its version.
	 */
	version: function (pkg, ver) {
		if (arguments.length <= 1)
			return zPkg._pkgVers[pkg];

		zPkg._pkgVers[pkg] = ver;
	},
	_pkgVers: {}
};
