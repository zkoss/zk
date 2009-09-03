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
	var _loaded = {'zk': true}, //loaded
		_loading = {'zk': true}, //loading (include loaded)
		_xloadings = [], //loading (exclude loaded)
		_loadedsemis = [], //loaded but not inited
		_afterLoadFronts = [],
		_afterLoads = [],
		_afterPkgLoad = {}, //after pkg loaded
		_pkgdepend = {},
		_pkgver = {},
		_pkghosts = {}/*package host*/, _defhost = []/*default host*/;

	function doLoad(pkg, dt) {
		if (!pkg || _loading[pkg])
			return !zk.loading && !_loadedsemis.length;
			//since pkg might be loading (-> return false)

		_loading[pkg] = true;

		//We don't use e.onload since Safari doesn't support t
		//See also Bug 1815074

		_xloadings.push(pkg);
		if (updCnt() == 1) {
			zk.disableESC();
			setTimeout(prgbox, 380);
		}

		var modver = pkg.indexOf('.');
		if (modver) modver = zk.getVersion(pkg.substring(0, modver));
		if (!modver) modver = zk.build;

		var e = document.createElement("script"),
			uri = pkg + ".wpd",
			host = zk.getHost(pkg, true);
		e.type = "text/javascript";
		e.charset = "UTF-8";

		if (uri.charAt(0) != '/') uri = '/' + uri;

		if (host) uri = host + "/web/js" + uri;
		else {
			if (modver) uri = "/web/_zv" + modver + "/js" + uri;
			else uri = "/web/js" + uri;
			uri = zk.ajaxURI(uri, {desktop:dt,au:true});
		}

		e.src = uri;
		document.getElementsByTagName("HEAD")[0].appendChild(e);
		return false;
	}
	function doEnd(afs, wait) {
		for (var fn; fn = afs.shift();) {
			if (updCnt() || (wait && _loadedsemis.length)) {
				afs.unshift(fn);
				return;
			}
			fn();
		}
	}
	function loadmsg() {
		var msg = '';
		for (var j = _xloadings.length; --j >=0;) {
			if (msg) msg += ', ';
			msg += _xloadings[j];
		}
		return msg;
	}
	function updCnt() {
		zk.loading = _xloadings.length;
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
		_xloadings.$remove(pkg);
		_loading[pkg] = true;

		if (wait) _loadedsemis.push(pkg);
		else {
			_loadedsemis.$remove(pkg);
			_loaded[pkg] = true;

			var afpk = _afterPkgLoad[pkg];
			if (afpk) {
				delete _afterPkgLoad[pkg];
				_afterLoadFronts.push.apply(_afterLoadFronts, afpk); //add all
			}

			var deps = _pkgdepend[pkg];
			if (deps) {
				delete _pkgdepend[pkg];
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
			doEnd(_afterLoadFronts);
			doEnd(_afterLoads, 1);
		}
	},
	isLoaded: function (pkg) {
		return _loaded[pkg];
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
		return _pkgver[pkg];
	},
	setVersion: function (pkg, ver) {
		_pkgver[pkg] = ver;
	},
	depends: function (a, b) {
		if (a && b) //a depends on b
			if (_loaded[a]) zk.load(b);
			else {
				if (_pkgdepend[a]) _pkgdepend[a].push(b);
				else _pkgdepend[a] = [b];
			}
	},

	afterLoad: function (a, b, front) {
		if (typeof a == 'string') {
			if (!b) return true;

			for (var pkgs = a.split(','), j = pkgs.length; j--;) {
				var p = pkgs[j].trim();
				if (p && !_loaded[p]) {
					while (j--) {
						var p2 = pkgs[j].trim();
						if (p2 && !_loaded[p2]) { //yes, more
							var a1 = a, b1 = b;
							b = function () {
								zk.afterLoad(a1, b1, front); //check again
							};
							break;
						}
					}

					if (_afterPkgLoad[p]) _afterPkgLoad[p].push(b);
					else _afterPkgLoad[p] = [b];
					return false;
				}
			}

			//all loaded
			a = b;
		}

		if (a) {
			if (zk.loading || _loadedsemis.length) {
				(front ? _afterLoadFronts: _afterLoads).push(a);
				return false;
			}
			if (!jq.isReady) {
				jq(a);
				return false;
			}
			a();
			return true;
		}
	},
	getHost: function (pkg, js) {
		for (var p in _pkghosts)
			if (pkg.startsWith(p))
				return _pkghosts[p][js ? 1: 0];
		return _defhost[js ? 1: 0];
	},
	setHost: function (host, updURI, pkgs) {
		var hostUpd = host + updURI;
		if (!_defhost.length)
			for (var scs = document.getElementsByTagName("SCRIPT"), j = 0, len = scs.length;
			j < len; ++j) {
				var src = scs[j].src;
				if (src)
					if (src.startsWith(host)) {
						_defhost = [host, hostUpd];
						break;
					} else if (src.indexOf("/zk.wpd") >= 0)
						break;
			}
		for (var j = 0; j < pkgs.length; ++j)
			_pkghosts[pkgs[j]] = [host, hostUpd];
	}
  }
})());
