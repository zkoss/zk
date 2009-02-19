/* pkg.js

	Purpose:
		zPkg: the package utilities
	Description:
		
	History:
		Tue Oct  7 16:32:04     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zPkg = {
	loading: 0,

	/** Called after the whole package is declared. */
	end: function (pkg) {
		if (!zPkg._lding.$remove(pkg))
			zPkg._lded[pkg] = true;
			//specified in lang.xml (or in HTML directly)

		var al2 = zPkg._afld2[pkg];
		if (al2) {
			delete zPkg._afld2[pkg];
			for (var fn, aflds = zPkg._aflds; fn = al2.pop();)
				aflds.unshift(fn);
		}

		if (!zPkg._updCnt()) {
			try {
				zEvt.enableESC();
				zUtl.destroyProgressbox("zk_loadprog");
			} catch (ex) {
			}

			for (var fn, aflds = zPkg._aflds; fn = aflds.shift();)
				fn();
		}
	},
	isLoaded: function (pkg) {
		return zPkg._lded[pkg];
	},
	load: function (pkg, dt) {
		var pkglds = zPkg._lded;
		if (!pkg || pkglds[pkg]) return !zPkg.loading;
			//since pkg might be loading (-> return false)

		pkglds[pkg] = true;

		var deps = zPkg._deps[pkg];
		if (deps) {
			delete zPkg._deps[pkg];
			for (var pn; pn = deps.unshift();)
				zPkg.load(pn);
		}

		//We don't use e.onload since Safari doesn't support t
		//See also Bug 1815074

		zPkg._lding.unshift(pkg);
		if (zPkg._updCnt() == 1) {
			zEvt.disableESC();
			setTimeout(zPkg._pgbox, 380);
		}

		var modver = pkg.indexOf('.');
		if (modver) modver = zPkg.getVersion(pkg.substring(0, modver));
		if (!modver) modver = zk.build;

		var e = document.createElement("script"),
			uri = pkg.replace(/\./g, '/') + "/zk.wpd";
		e.type = "text/javascript";
		e.charset = "UTF-8";

		if (uri.charAt(0) != '/') uri = '/' + uri;
		if (modver) uri = "/web/_zv" + modver + "/js" + uri;
		else uri = "/web/js" + uri;

		e.src = zAu.comURI(uri, dt);
		document.getElementsByTagName("HEAD")[0].appendChild(e);
	},
	_lded: {'zk': true}, //loaded
	_lding: [], //loading
	_aflds: [],
	_afld2: {},
	_deps: {},

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

	getVersion: function (pkg) {
		return zPkg._pkgVers[pkg];
	},
	setVersion: function (pkg, ver) {
		zPkg._pkgVers[pkg] = ver;
	},
	depends: function (a, b) {
		if (a && b) //a depends on b
			if (zPkg._lded[a]) zPkg.load(b);
			else {
				var deps = zPkg._deps;
				if (deps[a]) deps[a].push(b);
				else deps[a] = [b];
			}
	},
	_pkgVers: {}
};
zk.afterLoad = function (a, b) { //part of zk
	if (typeof a == 'string') {
		if (!b) return;
		if (zPkg._lded[a]) a = b;
		else {
			var al2 = zPkg._afld2;
			if (al2[a]) al2[a].push(b);
			else al2[a] = [b];
			return;
		}
	}

	if (zPkg.loading) {
		zPkg._aflds.push(a);
		return true;
	}
	a();
};
