/* pkg.js

	Purpose:
		The package utilities (part of zk)
	Description:
		
	History:
		Tue Oct  7 16:32:04     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.copy(zk, (function() {
	var _loaded = {'zk': true}, //loaded
		_xloadings = [], //loading (exclude loaded)
		_loadedsemis = [], //loaded but not inited
		_afterLoadFronts = [],
		_afterLoads = [],
		_afterPkgLoad = {}, //after pkg loaded
		_pkgdepend = {},
		_pkgver = {},
		_pkghosts = {}/*package host*/, _defhost = []/*default host*/;

	if (!zk.ie) _loaded['zk.canvas'] = true;
	var _loading = zk.copy({}, _loaded); //loading (include loaded)

	//We don't use e.onload since Safari doesn't support t
	//See also Bug 1815074
	function markLoading(nm) {
		//loading
		_loading[nm] = true;

		_xloadings.push(nm);
		if (updCnt() == 1) {
			zk.disableESC();
			setTimeout(prgbox, 380);
		}
	}
	function doLoad(pkg, dt) {
		if (!pkg || _loading[pkg])
			return !zk.loading && !_loadedsemis.length;
			//since pkg might be loading (-> return false)

		markLoading(pkg);

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
		for (var j = _xloadings.length, k = 0; --j >=0;) {
			if (msg) msg += ', ';
			if (++k == 5) {
				k = 0;
				msg += '<br/>';
			}
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
					(window.msgzk?msgzk.LOADING:"Loading")
					+' <span id="zk_loadcnt">'+loadmsg()+'</span>',
					true);
			}
		}	
	}

/** @partial zk
 */
  return { //internal utility
	setLoaded: _zkf = function (pkg, wait) { //internal
		_xloadings.$remove(pkg);
		_loading[pkg] = true;

		if (wait) {
			if (!_loaded[pkg]) _loadedsemis.push(pkg);
		} else {
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
	/** Notify ZK that the name of the JavaScript file is loaded.
	 * This method is designed to be used with {@link #loadScript}, such
	 * that ZK Client knows if a JavaScript file is loaded.
	 * @param String name the name of the JavaScript file.
	 * It must be the same as the one passed to {@link #loadScript}.
	 */
	setScriptLoaded: _zkf,

	/** Tests if a package is loaded (or being loaded). 
	 * @param String pkg the package name
	 * @param boolean loading [Optional; default: false]
	 * If true is specified, this method returns true if the package is loaded or being loaded. If false or omitted, it returns true only if the package is loaded. 
	 * @return boolean true if loaded
	 * @see #load
	 */
	isLoaded: function (pkg, loading) {
		return (loading && _loading[pkg]) || _loaded[pkg];
	},
	/** Loads the specified package(s). This method is called automatically when mounting the peer widgets. However, if an application developer wants to access JavaScript packages that are not loaded, he has to invoke this method.
	 * <p>The loading of a package is asynchronous, so you cannot create the widget immediately. Rather, use the <code>func</code> argument, func, or use #afterLoad to execute. 
<pre><code>
zk.load('zul.utl', function () {
  new zul.utl.Timer();
});
</code></pre>
	 * @param String pkg the package name
	 * @param Function func [optional] the function to execute after all packages are loaded. Ignored if omitted. Notice that func won't be executed until all requested packages are loaded; not just what are specified here. 
	 * @return boolean true if all required packages are loaded
	 */
	/** Loads the specified package(s). This method is called automatically when mounting the peer widgets. However, if an application developer wants to access JavaScript packages that are not loaded, he has to invoke this method.
	 * <p>The loading of a package is asynchronous, so you cannot create the widget immediately. Rather, use the <code>func</code> argument, func, or use #afterLoad to execute. 
	 * @param String pkg the package name
	 * @param zk.Desktop dt [optional] the desktop used to get URI of the JavaScript file ({@link #ajaxURI}). If null, the first desktop is assumed. 
	 * @param Function func [optional] the function to execute after all packages are loaded. Ignored if omitted. Notice that func won't be executed until all requested packages are loaded; not just what are specified here. 
	 * @return boolean true if all required packages are loaded
	 * @see #load(String, Function)
	 */
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

	/** Loads a JavaScript file.
	 * @param String src the URL of the JavaScript file.
	 * @param String name the name to shown up in the progressing dialog.
	 * Specify a non-empty string if you want ZK not to create widgets until
	 * this file is loaded. Ignored if not specified or null.
	 * If you specify a name here, you have to call {@link #setScriptLoaded}
	 * when the script is loaded. Otherwise, @{link zk#loading} won't be zero
	 * and ZK Client Engine is halted.
	 * @param String charset the charset. UTF-8 is assumed if null.
	 */
	loadScript: function (src, name, charset) {
		if (name)
			markLoading(name);

		var e = document.createElement("SCRIPT");
		e.type = "text/javascript";
		e.charset = charset || "UTF-8";
		e.src = src;
		document.getElementsByTagName("HEAD")[0].appendChild(e);
		return this;
	},

	/** Returns the version of the specified package.
	 * @param String pkg the package name
	 * @return String the version
	 */
	getVersion: function (pkg) {
		return _pkgver[pkg];
	},
	/** Sets the version of the specified package.
	 * @param String pkg the package name
	 * @param String ver the version
	 */
	setVersion: function (pkg, ver) {
		_pkgver[pkg] = ver;
	},
	/** Declare a package that must be loaded when loading another package.
	 * <p>Notice that it doesn't guarantee the loading order of the two packages. Thus, it is better to do in #afterLoad if a code snippet depends on both packages. 
	 * @param String a the name of the package that depends another package. In other words, calling #loading against this package will cause dependedPkgnn being loaded. 
	 * @param String b the name of the package that shall be loaded if another package is being loaded.
	 * In other words, it reads "a depends on b".
	 * @see #afterLoad
	 */
	depends: function (a, b) {
		if (a && b) //a depends on b
			if (_loaded[a]) zk.load(b);
			else {
				if (_pkgdepend[a]) _pkgdepend[a].push(b);
				else _pkgdepend[a] = [b];
			}
	},

	/** Declares a function that shall be executed after all requested packages are loaded (i.e., {@link #loading} is 0).
	 * If all packages has been loaded, the function is executed immediately
	 * and this method returns true. 
	 * @param Function func the function to execute
	 * @return boolean whether func has been executed
	 * @see #afterLoad(String, Function)
	 * @see #depends
	 * @see #load
	 */
	/** Declares a function that shall be executed only if the specified
	 * package(s) are loaded (and {@link #loading} is 0). Notice that it won't cause the package(s) to execute. Rather, it defers the execution of the specified function until someone else loads the package (by use of {@link #load}).
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Customize_JavaScript_Codes">Customize JavaScript Codes</a>
	 * <p>To know whether all requested packages are loaded (i.e., ZK is not loading any package), you can check {@link #loading} if it is 0.
	 * <p>Notice that functions specified in the second format execute before those specified in the first format.
	 * <p>Example
<pre><code>
zk.afterLoad('foo', function() {new foo.Foo();}); 
zk.afterLoad('foo1,foo2', function() {new foo1.Foo(foo2.Foo);}); 
zk.afterLoad(function() {});
</code></pre>
	 * @param String pkgs the package(s) that the specified function depends on. In other words, the function is evaluated only if the package(s) are loaded. If you want to specify multiple packages, separate them with comma.
	 * @param Function func the function to execute
	 * @see #afterLoad(Function)
	 * @see #depends
	 * @see #load
	 */
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
			a(); //note: we cannot use jq(a); otherwise, user cannot use it embedded script (test: zk light's helloword.html)
			return true;
		}
	},
	/** Returns the URI of the server (so called host) for the specified package.
	 * <p>ZK Client Engine loads the packages from the same server that returns the HTML page.
	 * If a package might be loaded from a different server, you can invoke #setHost to specify it and then #getHost will return the correct URL for the specified package. 
	 * @param String pkg the package name
	 * @param boolean js whether the returned URL is used to load the JavaScript file.
	 * {@link #load} will pass true to this argument to indicate the URI is used to load the JavaScript file. However, if you just want the URL to send the request back (such as json-p with jQuery's json), don't pass anything (or pass false) to this argument. 
	 * @return String the URI
	 * @see #setHost
	 */
	getHost: function (pkg, js) {
		for (var p in _pkghosts)
			if (pkg.startsWith(p))
				return _pkghosts[p][js ? 1: 0];
		return _defhost[js ? 1: 0];
	},
	/** Defines the URL of the host for serving the specified packages. 
	 * @param String host the host, such as http://www.zkoss.org.
	 * @param String updURI the update URI, such as /zkdemo/zkau,
	 * that is used to load the JavaScript files
	 * @param Array pkgs an array of pckage names (String)
	 * @see #getHost
	 */
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
