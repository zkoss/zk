/* pkg.ts

Purpose:
	The package utilities (part of zk)
Description:

History:
	Tue Oct  7 16:32:04     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var _loaded = {'zk': true}, //loaded
	_xloadings: string[] = [], //loading (exclude loaded)
	_loadedsemis: string[] = [], //loaded but not inited
	_afterLoadFronts: CallableFunction[] = [],
	_afterLoads: CallableFunction[] = [],
	_afterPkgLoad: Record<string, CallableFunction[]> = {}, //after pkg loaded
	_pkgdepend: Record<string, string[]> = {},
	_pkgver: Record<string, string> = {},
	_pkghosts: Record<string, string[]> = {}/*package host*/,
	_defhost: string[] = []/*default host*/,
	_loading = Object.assign({'zul.lang': true}, _loaded); //loading (include loaded)

//We don't use e.onload since Safari doesn't support t
//See also Bug 1815074
function markLoading(nm: string): void {
	//loading
	_loading[nm] = true;

	_xloadings.push(nm);
	if (updCnt() == 1) {
		zk.disableESC();
	}
}
function doEnd(afs: CallableFunction[], wait?): void {
	for (var fn: undefined | CallableFunction; fn = afs.shift();) {
		if (updCnt() || (wait && _loadedsemis.length)) {
			afs.unshift(fn);
			return;
		}
		fn();
	}
}
function updCnt(): number {
	return (zk.loading = _xloadings.length);
}

export function setLoaded(pkg: string, wait?: boolean): void { //internal
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
			_afterLoadFronts.$addAll(afpk);
		}

		var deps = _pkgdepend[pkg];
		if (deps) {
			delete _pkgdepend[pkg];
			for (var pn: string | undefined; pn = deps.shift();)
				zk.load(pn);
		}
	}

	if (!updCnt()) {
		try {
			zk.enableESC();
		} catch (ex) {
			zk.debugLog((ex as Error).message ?? ex);
		}
		doEnd(_afterLoadFronts);
		doEnd(_afterLoads, 1);
	}
}
/**
 * Notify ZK that the name of the JavaScript file is loaded.
 * This method is designed to be used with {@link loadScript}, such
 * that ZK Client knows if a JavaScript file is loaded.
 * @param name - the name of the JavaScript file.
 * It must be the same as the one passed to {@link loadScript}.
 */
export function setScriptLoaded(name: string): void {//_zkf,
	zk.setLoaded(name);
}

/**
 * Tests if a package is loaded (or being loaded).
 * @param pkg - the package name
 * @param loading - [Optional; default: false]
 * If true is specified, this method returns true if the package is loaded or being loaded. If false or omitted, it returns true only if the package is loaded.
 * @returns true if loaded
 * @see {@link load}
 */
export function isLoaded(pkg: string, loading?: boolean): boolean {
	return !!((loading && _loading[pkg]) || _loaded[pkg]);
}
/**
 * Loads the specified package(s). This method is called automatically when mounting the peer widgets. However, if an application developer wants to access JavaScript packages that are not loaded, he has to invoke this method.
 * <p>The loading of a package is asynchronous, so you cannot create the widget immediately. Rather, use the `func` argument, func, or use #afterLoad to execute.
 * ```ts
 * zk.load('zul.utl', function () {
 *   new zul.utl.Timer();
 * });
 * ```
 * @param pkg - the package name
 * @param func - the function to execute after all packages are loaded. Ignored if omitted. Notice that func won't be executed until all requested packages are loaded; not just what are specified here.
 * @returns true if all required packages are loaded
 */
export function load(pkg: string, func?: CallableFunction): boolean
/**
 * Loads the specified package(s). This method is called automatically when mounting the peer widgets. However, if an application developer wants to access JavaScript packages that are not loaded, he has to invoke this method.
 * <p>The loading of a package is asynchronous, so you cannot create the widget immediately. Rather, use the `func` argument, func, or use #afterLoad to execute.
 * @param pkg - the package name
 * @param dt - the desktop used to get URI of the JavaScript file ({@link ajaxURI}). If null, the first desktop is assumed.
 * @param func - the function to execute after all packages are loaded. Ignored if omitted. Notice that func won't be executed until all requested packages are loaded; not just what are specified here.
 * @returns true if all required packages are loaded
 * @see {@link load}
 */
export function load(pkg: string, dt?: zk.Desktop, func?: CallableFunction): boolean
export function load(pkg: string, dt?: zk.Desktop | CallableFunction, func?: CallableFunction): boolean {
	if (typeof dt == 'function') {
		if (func)
			throw 'At most one function allowed';
		else {
			func = dt as () => void;
			dt = undefined;
		}
	}

	if (func) zk.afterLoad(pkg, func, true);

	var loading;
	for (var pkgs = pkg.split(','), j = pkgs.length; j--;) {
		pkg = pkgs[j].trim();
		if (!zk._load(pkg, dt))
			loading = true;
	}
	return !loading;
}
/** @internal */
export function _load(pkg: string, dt?: zk.Desktop): boolean { //called by mount.js (better performance)
	if (!pkg || _loading[pkg])
		return !zk.loading && !_loadedsemis.length;
		//since pkg might be loading (-> return false)

	markLoading(pkg);

	var e = document.createElement('script'),
		uri = pkg + '.wpd',
		host = zk.getHost(pkg, true);
	e.type = 'text/javascript';
	e.charset = 'UTF-8';

	if (!uri.startsWith('/')) uri = '/' + uri;

	if (host) uri = host + '/web/js' + uri;
	else
		uri = zk.ajaxResourceURI('/js' + uri, zk.getVersion(pkg), {desktop: dt});
	e.src = uri;
	jq.head()?.appendChild(e);
	return false;
}

/**
 * Loads a JavaScript file.
 * @param src - the URL of the JavaScript file.
 * @param name - the name to shown up in the progressing dialog.
 * Specify a non-empty string if you want ZK not to create widgets until
 * this file is loaded. Ignored if not specified or null.
 * If you specify a name here, you have to call {@link setScriptLoaded}
 * when the script is loaded. Otherwise, {@link zk.loading} won't be zero
 * and ZK Client Engine is halted.
 * @param charset - the charset. UTF-8 is assumed if null.
 * @param force - the script to be loaded. (no matter it is loading or loaded)
 * @param callback - the function to execute after the JavaScript file loaded. (since 9.6.3)
 */
export function loadScript(src: string, name?: string, charset?: string, force?: boolean, callback?: CallableFunction): void { // FIXME: return ZKCoreUtilityStatic;
	if (name) {
		if (!force && zk.isLoaded(name, true))
			return;
		markLoading(name);
	}

	var e = document.createElement('script');
	e.type = 'text/javascript';
	e.charset = charset || 'UTF-8';
	e.src = src;
	if (callback) {
		e.onload = function () {
			callback();
		};
	}
	jq.head()?.appendChild(e);
}
/**
 * Loads a CSS file.
 * @param href - the URL of the CSS file.
 * @param id - the identifier. Ignored if not specified.
 * @param media - the media attribute. Ignored if not specified.
 * @param callback - function to execute after the CSS file loaded. (since 9.6.3)
 * @since 5.0.4
 */
export function loadCSS(href: string, id?: string, media?: string, callback?: CallableFunction): void {
	var ln = document.createElement('link');
	if (id) ln.id = id;
	ln.rel = 'stylesheet';
	ln.type = 'text/css';
	ln.href = href;

	if (callback) {
		ln.onload = function () {
			callback();
		};
	}
	if (media) ln.media = media;
	jq.head()?.appendChild(ln);
}

/**
 * @returns the version of the specified package, or null if not available.
 * @param pkg - the package name
 */
export function getVersion(pkg: string): string | undefined {
	for (var ver: string | undefined; pkg; pkg = pkg.substring(0, pkg.lastIndexOf('.')))
		if (ver = _pkgver[pkg])
			return ver;
	return undefined;
}
/**
 * Sets the version of the specified package.
 * @param pkg - the package name
 * @param ver - the version
 */
export function setVersion(pkg: string, ver: string): void {
	_pkgver[pkg] = ver;
}
/**
 * Declare a package that must be loaded when loading another package.
 * <p>Notice that it doesn't guarantee the loading order of the two packages. Thus, it is better to do in #afterLoad if a code snippet depends on both packages.
 * @param a - the name of the package that depends another package. In other words, calling #loading against this package will cause dependedPkgnn being loaded.
 * @param b - the name of the package that shall be loaded if another package is being loaded.
 * In other words, it reads "a depends on b".
 * @see {@link afterLoad}
 */
export function depends(a: string, b: string): void {
	if (a && b) {//a depends on b
		if (_loaded[a])
			zk.load(b);
		else {
			if (_pkgdepend[a]) _pkgdepend[a].push(b);
			else _pkgdepend[a] = [b];
		}
	}
}

/**
 * Declares a function that shall be executed after all requested packages are loaded (i.e., {@link loading} is 0).
 * If all packages has been loaded, the function is executed immediately
 * and this method returns true.
 * @param func - the function to execute
 * @returns whether func has been executed
 * @see {@link afterLoad}
 * @see {@link depends}
 * @see {@link load}
 */
export function afterLoad(func: CallableFunction): boolean
/**
 * Declares a function that shall be executed only if the specified
 * package(s) are loaded (and {@link loading} is 0). Notice that it won't cause the package(s) to execute. Rather, it defers the execution of the specified function until someone else loads the package (by use of {@link load}).
 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/General_Control/JavaScript_Packaging">JavaScript Packaging</a>
 * <p>To know whether all requested packages are loaded (i.e., ZK is not loading any package), you can check {@link loading} if it is 0.
 * <p>Notice that functions specified in the second format execute before those specified in the first format.
 * <p>Example
 * ```ts
 * zk.afterLoad('foo', function() {new foo.Foo();});
 * zk.afterLoad('foo1,foo2', function() {new foo1.Foo(foo2.Foo);});
 * zk.afterLoad(function() {});
 * ```
 * @param pkgs - the package(s) that the specified function depends on. In other words, the function is evaluated only if the package(s) are loaded. If you want to specify multiple packages, separate them with comma.
 * @param func - the function to execute
 * @see {@link afterLoad}
 * @see {@link depends}
 * @see {@link load}
 */
export function afterLoad(pkgs: string, func: CallableFunction, front?: boolean): void
export function afterLoad(a: string | CallableFunction, b?: CallableFunction, front?: boolean): boolean | void {
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
			(front ? _afterLoadFronts : _afterLoads).push(a);
			return false;
		}
		a(); //note: we cannot use jq(a); otherwise, user cannot use it embedded script (test: zk light's helloword.html)
		return true;
	}
}
/**
 * @returns the URI of the server (so called host) for the specified package.
 * <p>ZK Client Engine loads the packages from the same server that returns the HTML page.
 * If a package might be loaded from a different server, you can invoke #setHost to specify it and then #getHost will return the correct URL for the specified package.
 * @param pkg - the package name
 * @param js - whether the returned URL is used to load the JavaScript file.
 * {@link load} will pass true to this argument to indicate the URI is used to load the JavaScript file. However, if you just want the URL to send the request back (such as json-p with jQuery's json), don't pass anything (or pass false) to this argument.
 * @see {@link setHost}
 */
export function getHost(pkg: string, js: boolean): string {
	for (var p in _pkghosts)
		if (pkg.startsWith(p))
			return _pkghosts[p][js ? 1 : 0];
	return _defhost[js ? 1 : 0];
}
/**
 * Defines the URL of the host for serving the specified packages.
 * @param host - the host, such as http://www.zkoss.org.
 * @param resURI - the resource URI, such as /zkdemo/zkau,
 * that is used to load the JavaScript files
 * @param pkgs - an array of pckage names (String)
 * @see {@link getHost}
 */
export function setHost(host: string, resURI: string, pkgs: string[]): void {
	var hostRes = host + resURI;
	if (!_defhost.length)
		for (var scs = document.getElementsByTagName('script'), j = 0, len = scs.length;
		j < len; ++j) {
			var src = scs[j].src;
			if (src)
				if (src.startsWith(host)) {
					_defhost = [host, hostRes];
					break;
				} else if (src.includes('/zk.wpd'))
					break;
		}
	for (var j = 0; j < pkgs.length; ++j)
		_pkghosts[pkgs[j]] = [host, hostRes];
}

zk.copy(zk, {
	setLoaded,
	setScriptLoaded,
	isLoaded,
	load,
	_load,
	loadScript,
	loadCSS,
	getVersion,
	setVersion,
	depends,
	afterLoad,
	getHost,
	setHost,
});