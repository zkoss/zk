/* pkg.js

	Purpose:
		zPkg: the package utilities
	Description:
		
	History:
		Tue Oct  7 16:32:04     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zPkg = {
	loading: 0,

	/** Called after the whole package is declared.
	 * It must be the last statement in a JavaScript file.
	 * @param pkg the package name
	 */
	end: function (pkg) {
		if (zPkg._lding.$remove(pkg)) {
			if (!zPkg._updCnt()) {
				try {
					zEvt.enableESC();
					zUtl.destroyProgressbox("zk_loadprog");
				} catch (ex) {
				}

				for (var fn, aflds = zPkg._aflds; fn = aflds.shift();)
					fn();
			}
		} else //specified in lang.xml (or in HTML directly)
			zPkg._lded[pkg] = true;
	},
	/** Loads the specified package.
	 * @param pkg the package name
	 * @param dtid the desktop ID. If null, the first desktop is used.
	 */
	load: function (pkg, dtid) {
		var pkglds = zPkg._lded;
		if (pkglds[pkg]) return;

		pkglds[pkg] = true;

		//We don't use e.onload since Safari doesn't support t
		//See also
		//Bug 1815074: IE bug: zk.ald might be called before appendChild returns

		zPkg._lding.unshift(pkg);
		if (zPkg._updCnt() == 1) {
			zEvt.disableESC();
			setTimeout(zPkg._pgbox, 350);
		}

		var modver = pkg.indexOf('.');
		if (modver) modver = zPkg.version(pkg.substring(0, modver));
		if (!modver) modver = zk.build;

		var e = document.createElement("script"),
			uri = pkg.replace(/\./g, '/') + "/zk.wpd";
		e.type = "text/javascript";
		e.charset = "UTF-8";

		if (uri.charAt(0) != '/') uri = '/' + uri;
		if (modver) uri = "/web/_zv" + modver + "/js" + uri;
		else uri = "/web/js" + uri;

		e.src = zAu.comURI(uri, dtid);
		document.getElementsByTagName("HEAD")[0].appendChild(e);
	},
	_lded: {}, //loaded
	_lding: [], //loading
	_aflds: [],

	_ldmsg: function () {
		var msg = '';
		for (var lding = zPkg._lding, j = lding.length; --j >=0;) {
			if (msg) msg += ', ';
			msg += lding[j];
		}
		return msg;
	},
	_updCnt: function () {
		zPkg.loading = zPkg._lding.length;
		try {
			var n = zDom.$("zk_loadcnt");
			if (n) n.innerHTML = zPkg._ldmsg();
		} catch (ex) {
		}
		return zPkg.loading;
	},
	_pgbox: function () {
		if (zPkg.loading || window.dbg_progressbox) { //dbg_progressbox: debug purpose
			var n = zDom.$("zk_loadprog");
			if (!n)
				zUtl.progressbox("zk_loadprog",
					'Loading <span id="zk_loadcnt">'+zPkg._ldmsg()+'</span>',
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
zk.afterLoad = function (fn) { //part of zk
	if (zPkg.loading)
		return zPkg._aflds.$add(fn);
	fn();
};
