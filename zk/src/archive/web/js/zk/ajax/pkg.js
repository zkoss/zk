/* pkg.js

	Purpose:
		The package utilities (part of zk)
	Description:
		
	History:
		Tue Oct  7 16:32:04     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.copy(zk, (function() {
	var loaded = {'zk': true}, //loaded
		loading = {'zk': true}, //loading (include loaded)
		xloadings = [], //loading (exclude loaded)
		semiLoads = [], //loaded but not inited
		afterLoadFronts = [],
		afterLoads = [],
		afterPkgLoad = {}, //after pkg loaded
		pkgdepend = {},
		pkgver = {};

	function doLoad(pkg, dt) {
		if (!pkg || loading[pkg])
			return !zk.loading && !semiLoads.length;
			//since pkg might be loading (-> return false)

		loading[pkg] = true;

		//We don't use e.onload since Safari doesn't support t
		//See also Bug 1815074

		xloadings.push(pkg);
		if (updCnt() == 1) {
			zk.disableESC();
			setTimeout(prgbox, 380);
		}

		var modver = pkg.indexOf('.');
		if (modver) modver = zk.getVersion(pkg.substring(0, modver));
		if (!modver) modver = zk.build;

		var e = document.createElement("script"),
			uri = pkg.replace(/\./g, '/') + "/zk.wpd";
		e.type = "text/javascript";
		e.charset = "UTF-8";

		if (uri.charAt(0) != '/') uri = '/' + uri;
		if (modver) uri = "/web/_zv" + modver + "/js" + uri;
		else uri = "/web/js" + uri;

		e.src = zk.ajaxURI(uri, {desktop:dt,au:true});
		document.getElementsByTagName("HEAD")[0].appendChild(e);
		return false;
	}
	function doEnd(afs, wait) {
		for (var fn; fn = afs.shift();) {
			if (updCnt() || (wait && semiLoads.length)) {
				afs.unshift(fn);
				return;
			}
			fn();
		}
	}
	function loadmsg() {
		var msg = '';
		for (var lding = xloadings, j = lding.length; --j >=0;) {
			if (msg) msg += ', ';
			msg += lding[j];
		}
		return msg;
	}
	function updCnt() {
		zk.loading = xloadings.length;
		try {
			var n = jq("#zk_loadcnt")[0];
			if (n) n.innerHTML = loadmsg();
		} catch (ex) {
		}
		return zk.loading;
	}
	function prgbox() {
		if (zk.loading) {
			var n = jq("#zk_loadprog")[0];
			if (!n) {
				if (!jq.isReady)
					return setTimeout(prgbox, 10);
						//don't use jq() since it will be queued after others

				zUtl.progressbox("zk_loadprog",
					'Loading <span id="zk_loadcnt">'+loadmsg()+'</span>',
					true);
			}
		}	
	}

  return { //internal utility
	setLoaded: function (pkg, wait) {
		xloadings.$remove(pkg);
		loading[pkg] = true;

		if (wait) semiLoads.push(pkg);
		else {
			semiLoads.$remove(pkg);
			loaded[pkg] = true;

			var afpk = afterPkgLoad[pkg];
			if (afpk) {
				delete afterPkgLoad[pkg];
				var afs = afterLoadFronts;
				afs.push.apply(afs, afpk); //add all
			}

			var deps = pkgdepend[pkg];
			if (deps) {
				delete pkgdepend[pkg];
				for (var pn; pn = deps.unshift();)
					zk.load(pn);
			}
		}

		if (!updCnt()) {
			try {
				zk.enableESC();
				zUtl.destroyProgressbox("zk_loadprog");
			} catch (ex) {
			}
			doEnd(afterLoadFronts);
			doEnd(afterLoads, 1);
		}
	},
	isLoaded: function (pkg) {
		return loaded[pkg];
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
			if (!doLoad(pkg, dt))
				loading = true;
		}
		return !loading;
	},

	getVersion: function (pkg) {
		return pkgver[pkg];
	},
	setVersion: function (pkg, ver) {
		pkgver[pkg] = ver;
	},
	depends: function (a, b) {
		if (a && b) //a depends on b
			if (loaded[a]) zk.load(b);
			else {
				var deps = pkgdepend;
				if (deps[a]) deps[a].push(b);
				else deps[a] = [b];
			}
	},

	afterLoad: function (a, b, front) {
		if (typeof a == 'string') {
			if (!b) return true;

			for (var pkgs = a.split(','), j = pkgs.length; j--;) {
				var p = pkgs[j].trim();
				if (p && !loaded[p]) {
					while (j--) {
						var p2 = pkgs[j].trim();
						if (p2 && !loaded[p2]) { //yes, more
							var a1 = a, b1 = b;
							b = function () {
								zk.afterLoad(a1, b1, front); //check again
							};
							break;
						}
					}

					var afpk = afterPkgLoad;
					if (afpk[p]) afpk[p].push(b);
					else afpk[p] = [b];
					return false;
				}
			}

			//all loaded
			a = b;
		}

		if (zk.loading || semiLoads.length) {
			(front ? afterLoadFronts: afterLoads).push(a);
			return false;
		}
		a();
		return true;
	}
  }
})());
