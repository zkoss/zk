/* zk.js

	Purpose:

	Description:

	History:
		Mon Sep 29 17:17:26 2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
jq = jQuery;
(zk = function (sel) {
	return jq(sel, zk).zk;
}).copy = function (dst, src) {
	dst = dst || {};
	for (var p in src)
		dst[p] = src[p];
	return dst;
};

(function () {
	var _oid = 0,
		_statelesscnt = 0,
		_logmsg,
		_stamps = [];

	function def(nm, before, after) {
		return function (v, opts) {
			if (before) v = before.apply(this, arguments);
			var o = this[nm];
			this[nm] = v;
			if (after && (o !== v || (opts && opts.force)))
				after.apply(this, arguments);
			return this;
		};
	}
	function showprgbInit() {
		//1. don't use jq() since it will be queued after others
		//2. zk.mnt.pgbg: pgbg() is called with a non-contained page; see mount.js
		if (jq.isReady||zk.mnt.pgbg||zk.Page.contained.length)
			_showprgb(true, zk.pi ? 'z-initing': null);
		else
			setTimeout(showprgbInit, 10);
	}
	function showprgb() { //When passed to FF's setTimeout, 1st argument is not null
		_showprgb();
	}
	function _showprgb(mask, icon) {
		if (zk.processing
		&& !jq("#zk_proc").length && !jq("#zk_showBusy").length)
			zUtl.progressbox("zk_proc", window.msgzk?msgzk.PLEASE_WAIT:'Processing...', mask, icon);
	}
	function wgt2s(w) {
		var s = w.className.substring(w.className.lastIndexOf('.') + 1);
		return w.id ? s + '@' + w.id: s + '#' + w.uuid;
	}
	function toLogMsg(ars, isDetailed) {
		var msg = [];
		for (var j = 0, len = ars.length; j < len; j++) {
			if (msg.length) msg.push(", ");
			var ar = ars[j];
			if (ar && (ar.$array || ar.zk)) //ar.zk: jq(xx)
				msg.push('[' + toLogMsg(ar, isDetailed) + ']');
			else if (zk.Widget.isInstance(ar))
				msg.push(wgt2s(ar));
			else if (ar && ar.nodeType) {
				var w = zk.Widget.$(ar);
				if (w) msg.push(ar.tagName, ':', wgt2s(w));
				else msg.push(ar.tagName, '#', ar.id);
			} else if (isDetailed && ar && (typeof ar == 'object') && !ar.nodeType) {
				var s = ['{\n'];
				for (var v in ar) 
					s.push(v, ':', ar[v], ',\n');
				if (s[s.length - 1] == ',\n') 
					s.pop();
				s.push('\n}');
				msg.push(s.join(''));
			} else if (typeof ar == 'function') {
				var s = '' + ar,
					m = s.indexOf('{'),
					k = m < 0 ? s.indexOf('\n'): -1;
				msg.push(s.substring(0, m > 0 ? m: k > 0 ? k: s.length));
			} else
				msg.push('' + ar);
		}
		return msg.join('');
	}
	function doLog() {
		if (_logmsg) {
			var console = jq("#zk_log");
			if (!console.length) {
				jq(document.body).append(
	'<div id="zk_logbox" class="z-log">'
	+'<button onclick="jq(\'#zk_logbox\').remove()">X</button><br/>'
	+'<textarea id="zk_log" rows="10"></textarea></div>');
				console = jq("#zk_log");
			}
			console = console[0];
			console.value += _logmsg;
			console.scrollTop = console.scrollHeight;
			_logmsg = null;
		}
	}

	function _stampout() {
		if (zk.mounting)
			return zk.afterMount(_stampout);
		zk.stamp('ending');
		zk.stamp();
	}

	/* Overrides all subclasses. */
	function _overrideSub(dstpt, nm, oldfn, newfn) {
		for (var sub = dstpt._$subs, j = sub ? sub.length: 0; --j >= 0;) {
			var subpt = sub[j];
			if (subpt[nm] === oldfn) {
				subpt[nm] = newfn;
				_overrideSub(subpt, nm, oldfn, newfn); //recursive
			}
		}
	}

/** @class zk
 * A collection of ZK core utilities.
 * The utilities are mostly related to the language enhancement we added to JavaScript,
 * such as {@link #$extends} and {@link #@package}.
 * <p>Refer to {@link jq} for DOM related utilities.
 */
zk.copy(zk, {
	/** The delay before showing the processing prompt (unit: milliseconds). 
	 * <p>Default: 900 (depending on the server's configuration)
	 * @type int
	 */
	procDelay: 900,
	/** The delay before showing a tooltip (unit: milliseconds).
	 * Default: 800 (depending on the server's configuration) 
	 * @type int
	 */
	tipDelay: 800,
	/** The delay before resending the AU request, i.e., assuming the last AU request fails (unit: milliseconds). A negative value means not to resend at all.
	 * Default: -1 (depending on the server's configuration). 	
	 * @type int
	 */
	resendDelay: -1,
	/** The last position that the mouse was clicked (including left and right clicks).
	 * @type Offset
	 */
	clickPointer: [0, 0],
	/** The position of the mouse (including mouse move and click).
	 * @type Offset
	 */
	currentPointer: [0, 0],
	/** The number of widget packages (i.e., JavaScript files) being loaded
	 * (and not yet complete).
	 * <p>When the JavaScript files of widgets are loading, you shall not create
	 * any widget. Rather, you can use {@link #afterLoad} to execute the creation
	 * and other tasks after all required JavaScript files are loaded. 
	 * @see #load
	 * @see #afterLoad
	 * @see #processing
	 * @see #mounting
	 * @type int
	 */
	loading: 0,
	/** Whether ZK Client Engine has been mounting the peer widgets.
	 * By mounting we mean the creation of the peer widgets under the
	 * control of the server. To run after the mounting of the peer widgets,
	 * use {@link #afterMount}
	 * @see #afterMount
	 * @see #processing
	 * @see #loading
	 * @type boolean
	 */
	//mounting: false,
	/** Whether Client Engine is processing something, such as processing
	 * an AU response. This flag is set when {@link #startProcessing}
	 * is called, and cleaned when {@link #endProcessing} is called. 
	 * @see #startProcessing
	 * @see #loading
	 * @see #mounting
	 * @type boolean
	 */
	//processing: false,

	/** The user agent of the browser.
	 * @type String
	 */
	//agent: 'defined later',
	/** Whether it is Internet Explorer.
	 * @type boolean
	 */
	//ie: false,

	/** Copies a map of properties (or options) from one map to another.
     * <p>Example: We can extend Array.prototype as follows.
	 * <pre><code>zk.copy(Array.prototoype, {
	 * $add: function (o) {
 	 *  this.push(o);
	 * }
	 *});</code></pre>
	 *
	 * @param Map dest the destination object to copy properties to.
	 * @param Map src the properties to copy from 
	 * @return Map the destination object
	 */
	//copy: zk.$void, //define above

	/** Defines a package. It creates the package if not defined yet.
	 * It is similar to Java's package statement except it returns the package
	 * object.
	 *
	 * <p>Example:
	 * <pre><code>var foo = zk.$package('com.foo');
	 *foo.Cool = zk.#$extends(zk.Object);</code></pre> 
	 *
	 * @param String name the name of the package.
	 * @return zk.Package
	 */
	$package: function (name, end, wv) { //end used only by WpdExtendlet
		for (var j = 0, ref = window;;) {
			var k = name.indexOf('.', j),
				nm = k >= 0 ? name.substring(j, k): name.substring(j);
			var nxt = ref[nm], newpkg;
			if (newpkg = !nxt) nxt = ref[nm] = {};
			if (k < 0) {
				if (newpkg && end !== false) zk.setLoaded(name);
					//if $package(x, false) was called, zk.setLoaded won't be called
					//i.e., zk.setLoaded has to be called explicitly
				if (wv) nxt.$wv = true; //the wv (weeve) package is available
				return nxt;
			}
			ref = nxt;
			j = k + 1;
		}
	},
	$import: function (name, fn) {
		for (var j = 0, ref = window;;) {
			var k = name.indexOf('.', j),
				nm = k >= 0 ? name.substring(j, k): name.substring(j);
			var nxt = ref[nm];
			if (k < 0 || !nxt) {
				if (fn)
					if (nxt) fn(nxt);
					else
						zk.load(name.substring(0, name.lastIndexOf('.')),
							function () {fn(zk.$import(name));});
				return nxt;
			}
			ref = nxt;
			j = k + 1;
		}
	},

	$extends: function (superclass, members, staticMembers) {
		if (!superclass)
			throw 'unknown superclass';

		var jclass = function() {
			this.$oid = ++_oid;
			this.$init.apply(this, arguments);

			var ais = this._$ais;
			if (ais) {
				delete this._$ais;
				for (var j = ais.length; j--;)
					ais[j].call(this);
			}
		};

		var thispt = jclass.prototype,
			superpt = superclass.prototype,
			define = members['$define'];
		delete members['$define'];
		zk.copy(thispt, superpt); //inherit non-static
		zk.copy(thispt, members);

		for (var p in superclass) //inherit static
			if (p != 'prototype')
				jclass[p] = superclass[p];

		zk.copy(jclass, staticMembers);

		thispt.$class = jclass;
		thispt._$super = superpt;
		thispt._$subs = [];
		superpt._$subs.push(thispt);
			//maintain a list of subclasses (used zk.override)
		jclass.$class = zk.Class;
		jclass.superclass = superclass;

		zk.define(jclass, define);

		return jclass;
	},
	_$oid: 0,
	$default: function (opts, defaults) {
		opts = opts || {};
		for (var p in defaults)
			if (opts[p] === undefined)
				opts[p] = defaults[p];
		return opts;
	},

	override: function (dst, backup, src) {
		for (var nm in src)
			_overrideSub(dst, nm, backup[nm] = dst[nm], dst[nm] = src[nm]);
	},

	define: function (klass, props) {
		for (var nm in props) {
			var nm1 = '_' + nm,
				nm2 = nm.charAt(0).toUpperCase() + nm.substring(1),
				pt = klass.prototype,
				after = props[nm], before = null;
			if (after && after.$array) {
				before = after.length ? after[0]: null;
				after = after.length > 1 ? after[1]: null;
			}
			pt['set' + nm2] = def(nm1, before, after);
			pt['get' + nm2] = pt['is' + nm2] =
				new Function('return this.' + nm1 + ';');
		}
	},

	$void: function () {},

	parseInt: function (v, b) {
		v = v ? parseInt(v, b || 10): 0;
		return isNaN(v) ? 0: v;
	},

	set: function (o, name, value, extra) {
		var m = o['set' + name.charAt(0).toUpperCase() + name.substring(1)];
		if (!m) o[name] = value;
		else if (arguments.length >= 4)
			m.call(o, value, extra);
		else
			m.call(o, value);
	},
	get: function (o, name) {
		var nm = name.charAt(0).toUpperCase() + name.substring(1);
			m = o['get' + nm];
		if (m) return m.call(o);
		m = o['is' + nm];
		if (m) return m.call(o);
		return o[name];
	},

	//Processing//
	startProcessing: function (timeout) {
		zk.processing = true;
		setTimeout(jq.isReady ? showprgb: showprgbInit, timeout > 0 ? timeout: 0);
	},
	endProcessing: function() {
		zk.processing = false;
		zUtl.destroyProgressbox("zk_proc");
	},

	disableESC: function () {
		++zk._noESC;
	},
	enableESC: function () {
		--zk._noESC;
	},
	_noESC: 0, //# of disableESC being called (also used by mount.js)

	//DEBUG//
	error: function (msg) {
		new zk.eff.Error(msg);
	},
	errorDismiss: function () {
		zk.eff.Error.closeAll();
	},
	log: function (detailed) {		
		var msg = toLogMsg(
			(detailed !== zk) ? arguments :
				(function (args) {
					var a = [];
					for (var j = args.length; --j > 0;)
						a.unshift(args[j]);
					return a;
				})(arguments)
			, (detailed === zk)
		);
		_logmsg = (_logmsg ? _logmsg + msg: msg) + '\n';
		setTimeout(function(){jq(doLog);}, 300);
	},
	stamp: function (nm) {
		if (arguments.length) {
			if (!_stamps.length)
				setTimeout(_stampout, 0);
			_stamps.push({n: nm, t: zUtl.now()});
		} else if (_stamps.length) {
			var t0 = zk._t0;
			for (var inf; (inf = _stamps.shift());) {
				zk.log(inf.n + ': ' + (inf.t - zk._t0));
				zk._t0 = inf.t;
			}
			zk.log("total: " + (zk._t0 - t0));
		}
	},

	ajaxURI: function (uri, opts) {
		var ctx = zk.Desktop.$(opts?opts.desktop:null),
			au = opts && opts.au;
		ctx = (ctx ? ctx: zk)[au ? 'updateURI': 'contextURI'];
		if (!uri) return ctx;

		var abs = uri.charAt(0) == '/';
		if (au && !abs) {
			abs = true;
			uri = '/' + uri; //non-au supports relative path
		}

		var j = ctx.lastIndexOf(';'), k = ctx.lastIndexOf('?');
		if (j < 0 && k < 0) return abs ? ctx + uri: uri;

		if (k >= 0 && (j < 0 || k < j)) j = k;
		var prefix = abs ? ctx.substring(0, j): '';

		if (opts && opts.ignoreSession)
			return prefix + uri;

		var suffix = ctx.substring(j),
			l = uri.indexOf('?');
		return l >= 0 ?
			k >= 0 ?
			  prefix + uri.substring(0, l) + suffix + '&' + uri.substring(l+1):
			  prefix + uri.substring(0, l) + suffix + uri.substring(l):
			prefix + uri + suffix;
	},
	stateless: function (dtid, contextURI, updateURI) {
		var Desktop = zk.Desktop, dt;
		dtid = dtid || ('z_auto' + _statelesscnt++);
		dt = Desktop.all[dtid];
		if (dt && !dt.stateless) throw "Desktop conflict";
		zk.updateURI = zk.updateURI || updateURI;
		zk.contextURI = zk.contextURI || contextURI;
		return dt || new Desktop(dtid, contextURI, updateURI, true);
	}
});

//zk.agent//
(function () {
	zk.agent = navigator.userAgent.toLowerCase();
	zk.safari = zk.agent.indexOf("safari") >= 0;
	zk.opera = zk.agent.indexOf("opera") >= 0;
	zk.gecko = zk.agent.indexOf("gecko/") >= 0 && !zk.safari && !zk.opera;
	var bodycls;
	if (zk.gecko) {
		var j = zk.agent.indexOf("firefox/");
		j = zk.parseInt(zk.agent.substring(j + 8));
		zk.gecko3 = j >= 3;
		zk.gecko2_ = !zk.gecko3;

		bodycls = 'gecko gecko' + j;
	} else if (zk.opera) {
		bodycls = 'opera';
	} else {
		var j = zk.agent.indexOf("msie ");
		zk.ie = j >= 0;
		if (zk.ie) {
			j = zk.parseInt(zk.agent.substring(j + 5));
			zk.ie7 = j >= 7; //ie7 or later
			zk.ie8c = j >= 8; //ie8 or later (including compatible)
			zk.ie8 = j >= 8 && document.documentMode >= 8; //ie8 or later
			zk.ie6_ = !zk.ie7;
			zk.ie7_ = zk.ie7 && !zk.ie8;
			bodycls = 'ie ie' + j;
		} else if (zk.safari)
			bodycls = 'safari';
	}
	if (zk.air = zk.agent.indexOf("adobeair") >= 0)
		bodycls = 'air';

	if (bodycls)
		jq(function () {
			var n = document.body,
				cn = n.className;
			if (cn) cn += ' ';
			n.className = cn + bodycls;
		});
})();

//zk.Object//
	function getProxy(o, f) { //used by zk.Object
		return function () {
				return f.apply(o, arguments);
			};
	}
zk.Object = function () {};
/** @class zk.Object
 * The root of the class hierarchy.
 */
zk.Object.prototype = {
	$init: zk.$void,
	$afterInit: function (f) {
		(this._$ais = this._$ais || []).unshift(f); //reverse
	},
	/** The class that this object belongs to.
	 * @type zk.Class
	 */
	$class: zk.Object,
	$instanceof: function (cls) {
		if (cls) {
			var c = this.$class;
			if (c == zk.Class)
				return this == zk.Object || this == zk.Class; //follow Java
			for (; c; c = c.superclass)
				if (c == cls)
					return true;
		}
		return false;
	},
	$super: function (mtdnm) {
		var args = [];
		for (var j = arguments.length; --j > 0;)
			args.unshift(arguments[j]);
		return this.$supers(mtdnm, args);
	},
	$supers: function (mtdnm, args) {
		var supers = this._$supers;
		if (!supers) supers = this._$supers = {};

		//locate method
		var old = supers[mtdnm], m, p, oldmtd;
		if (old) {
			oldmtd = old[mtdnm];
			p = old;
		} else {
			oldmtd = this[mtdnm];
			p = this;
		}
		for (;;) {
			if (!(p = p._$super))
				throw mtdnm + " not in superclass";
			if (oldmtd != p[mtdnm]) {
				m = p[mtdnm];
				if (m) supers[mtdnm] = p;
				break;
			}
		}

		try {
			return m.apply(this, args);
		} finally {
			supers[mtdnm] = old; //restore
		}
	},
	_$subs: [],

	proxy: function (f) {
		var fps = this._$proxies, fp;
		if (!fps) this._$proxies = fps = {};
		else if (fp = fps[f]) return fp;
		return fps[f] = getProxy(this, f);
	}
};

zk.Class = function () {}
zk.Class.superclass = zk.Object;
/** @partial zk.Class, zk.Object
 */
_zkf = {
	/** The class that this object belongs to.
	 * @type zk.Class
	 */
	$class: zk.Class,
	isInstance: function (o) {
		return o && o.$instanceof && o.$instanceof(this);
	},
	isAssignableFrom: function (cls) {
		for (; cls; cls = cls.superclass)
			if (this == cls)
				return true;
		return false;
	},
	$instanceof: zk.Object.prototype.$instanceof
};
zk.copy(zk.Class, _zkf);
zk.copy(zk.Object, _zkf);

})();
