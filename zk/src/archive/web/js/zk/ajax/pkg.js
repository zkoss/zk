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
		zPkg._ldings.$remove(pkg);
		zPkg._lded[pkg] = zPkg._lding[pkg] = true;

		var deps = zPkg._deps[pkg];
		if (deps) {
			delete zPkg._deps[pkg];
			for (var pn; pn = deps.unshift();)
				zPkg.load(pn);
		}

		var afpk = zPkg._afpklds[pkg];
		if (afpk) {
			delete zPkg._afpklds[pkg];
			var afs = zPkg._afld0s;
			afs.push.apply(afs, afpk); //add all
		}

		if (!zPkg._updCnt()) {
			try {
				zk.enableESC();
				zUtl.destroyProgressbox("zk_loadprog");
			} catch (ex) {
			}
			zPkg._end(zPkg._afld0s) && zPkg._end(zPkg._aflds);
		}
	},
	_end: function (afs) {
		for (var fn; fn = afs.shift();) {
			fn();
			if (zPkg._updCnt()) return false; //some loading
		}
		return true;
	},
	isLoaded: function (pkg) {
		return zPkg._lded[pkg];
	},
	load: function (pkg, dt, func) {
		if (func) zk.afterLoad(pkg, func, true);

		var loading;
		for (var pkgs = pkg.split(','), j = pkgs.length; --j >= 0;) {
			pkg = pkgs[j].trim();
			if (!zPkg._load(pkg, dt))
				loading = true;
		}
		return !loading;
	},
	_load: function (pkg, dt) {
		if (!pkg || zPkg._lding[pkg])
			return !zPkg.loading;
			//since pkg might be loading (-> return false)

		zPkg._lding[pkg] = true;

		//We don't use e.onload since Safari doesn't support t
		//See also Bug 1815074

		zPkg._ldings.unshift(pkg);
		if (zPkg._updCnt() == 1) {
			zk.disableESC();
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
		return false;
	},
	_lded: {'zk': true}, //loaded
	_lding: {'zk': true}, //loading (include loaded)
	_ldings: [], //loading (exclude loaded)
	_afld0s: [],
	_aflds: [],
	_afpklds: {}, //after pkg loaded
	_deps: {},

	_ldmsg: function () {
		var msg = '';
		for (var lding = zPkg._ldings, j = lding.length; --j >=0;) {
			if (msg) msg += ', ';
			msg += lding[j];
		}
		return msg;
	},
	_updCnt: function () {
		zPkg.loading = zPkg._ldings.length;
		try {
			var n = jq("#zk_loadcnt").$();
			if (n) n.innerHTML = zPkg._ldmsg();
		} catch (ex) {
		}
		return zPkg.loading;
	},
	_pgbox: function () {
		if (zPkg.loading || window.dbg_progressbox) { //dbg_progressbox: debug purpose
			var n = jq("#zk_loadprog").$();
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
zk.afterLoad = function (a, b, front) { //part of zk
	if (typeof a == 'string') {
		if (!b) return true;

		for (var pkgs = a.split(','), j = pkgs.length; --j >= 0;) {
			a = pkgs[j].trim();
			if (a && !zPkg._lded[a]) {
				var afpk = zPkg._afpklds;
				if (afpk[a]) afpk[a].push(b);
				else afpk[a] = [b];
				return false;
			}
		}

		//all loaded
		a = b;
	}

	if (zPkg.loading) {
		(front ? zPkg._afld0s: zPkg._aflds).push(a);
		return false;
	}
	a();
	return true;
};
