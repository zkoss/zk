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
	end: function (pkg, wait) {
		zPkg._ldings.$remove(pkg);
		zPkg._lding[pkg] = true;

		if (wait) zPkg._wait.push(pkg);
		else {
			zPkg._wait.$remove(pkg);
			zPkg._lded[pkg] = true;

			var afpk = zPkg._afpklds[pkg];
			if (afpk) {
				delete zPkg._afpklds[pkg];
				var afs = zPkg._afld0s;
				afs.push.apply(afs, afpk); //add all
			}

			var deps = zPkg._deps[pkg];
			if (deps) {
				delete zPkg._deps[pkg];
				for (var pn; pn = deps.unshift();)
					zPkg.load(pn);
			}
		}

		if (!zPkg._updCnt()) {
			try {
				zk.enableESC();
				zUtl.destroyProgressbox("zk_loadprog");
			} catch (ex) {
			}
			zPkg._end(zPkg._afld0s);
			zPkg._end(zPkg._aflds, 1);
		}
	},
	_end: function (afs, wait) {
		for (var fn; fn = afs.shift();) {
			if (zPkg._updCnt() || (wait && zPkg._wait.length)) {
				afs.unshift(fn);
				return;
			}
			fn();
		}
	},
	isLoaded: function (pkg) {
		return zPkg._lded[pkg];
	},
	load: function (pkg, dt, func) {
		if (typeof dt == 'function')
			if (func) throw 'At most one function allowed';
			else {
				func = dt;
				dt = null;
			}

		if (func) zk.afterLoad(pkg, func, true);

		var loading;
		for (var pkgs = pkg.split(','), j = pkgs.length; j--;) {
			pkg = pkgs[j].trim();
			if (!zPkg._load(pkg, dt))
				loading = true;
		}
		return !loading;
	},
	_load: function (pkg, dt) {
		if (!pkg || zPkg._lding[pkg])
			return !zPkg.loading && !zPkg._wait.length;
			//since pkg might be loading (-> return false)

		zPkg._lding[pkg] = true;

		//We don't use e.onload since Safari doesn't support t
		//See also Bug 1815074

		zPkg._ldings.push(pkg);
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

		e.src = zk.ajaxURI(uri, dt, {au:true});
		document.getElementsByTagName("HEAD")[0].appendChild(e);
		return false;
	},
	_lded: {'zk': true}, //loaded
	_lding: {'zk': true}, //loading (include loaded)
	_wait: [], //loaded but not inited
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
			var n = jq("#zk_loadcnt")[0];
			if (n) n.innerHTML = zPkg._ldmsg();
		} catch (ex) {
		}
		return zPkg.loading;
	},
	_pgbox: function () {
		if (zPkg.loading || window.dbg_progressbox) { //dbg_progressbox: debug purpose
			var n = jq("#zk_loadprog")[0];
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
	_pkgVers: {},

	afterLoad: function (a, b, front) {
		if (typeof a == 'string') {
			if (!b) return true;

			for (var pkgs = a.split(','), j = pkgs.length; j--;) {
				var p = pkgs[j].trim();
				if (p && !zPkg._lded[p]) {
					while (j--) {
						var p2 = pkgs[j].trim();
						if (p2 && !zPkg._lded[p2]) { //yes, more
							var a1 = a, b1 = b;
							b = function () {
								zk.afterLoad(a1, b1, front); //check again
							};
							break;
						}
					}

					var afpk = zPkg._afpklds;
					if (afpk[p]) afpk[p].push(b);
					else afpk[p] = [b];
					return false;
				}
			}

			//all loaded
			a = b;
		}

		if (zPkg.loading || zPkg._wait.length) {
			(front ? zPkg._afld0s: zPkg._aflds).push(a);
			return false;
		}
		a();
		return true;
}
};
zk.afterLoad = zPkg.afterLoad; //part of zk
