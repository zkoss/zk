/* zk.js

	Purpose:

	Description:

	History:
		Mon Sep 29 17:17:26 2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(zk = function (sel) {
	return jq(sel, zk).zk;
}).copy = function (dst, src, bu) {
	dst = dst || {};
	for (var p in src) {
		if (bu) bu[p] = dst[p];
		dst[p] = src[p];
	}
	return dst;
};

(function () {
	var _oid = 0,
		_statelesscnt = 0,
		_logmsg,
		_stamps = [],
		_t0 = jq.now();

	function newClass(copy) {
		var init = function () {
			if (!init.$copied) {
				init.$copied = true;
				var cf = init.$copyf;
				delete init.$copyf;
				cf();
			}
			this.$oid = ++_oid;
			this.$init.apply(this, arguments);

			var ais = this._$ais;
			if (ais) {
				delete this._$ais;
				for (var j = ais.length; j--;)
					ais[j].call(this);
			}
		};
		init.$copyf = copy;
		init.$copied = !init.$copyf;
		return init;
	}
	function regClass(jclass, superclass) {
		var oid = jclass.$oid = ++_oid;
		zk.classes[oid] = jclass;
		jclass.prototype.$class = jclass;
		jclass.$class = zk.Class;
		(jclass._$extds = (jclass.superclass = superclass) ?
			zk.copy({}, superclass._$extds): {})[oid] = jclass;
			//_$extds is a map of all super classes and jclass
		return jclass;
	}

	function defGet(nm) {
		return new Function('return this.' + nm + ';');
	}
	function defSet00(nm) {
		return function (v) {
			this[nm] = v;
			return this;
		};
	}
	function defSet01(nm, after) {
		return function (v, opts) {
			var o = this[nm];
			this[nm] = v;
			if (o !== v || (opts && opts.force)) {
				this.__fname__ = nm.substring(1);
				after.apply(this, arguments);
				delete this.__fname__;
			}
			return this;
		};
	}
	function defSet10(nm, before) {
		return function (/*v, opts*/) {
			this.__fname__ = nm.substring(1);
			this[nm] = before.apply(this, arguments);
			delete this.__fname__;
			return this;
		};
	}
	function defSet11(nm, before, after) {
		return function (v, opts) {
			var o = this[nm];
			this.__fname__ = nm.substring(1);
			this[nm] = v = before.apply(this, arguments);
			if (o !== v || (opts && opts.force))
				after.apply(this, arguments);
			delete this.__fname__;
			return this;
		};
	}

	function showprgbInit() {
		//don't use jq() since it will be queued after others
		if (jq.isReady||zk.Page.contained.length)
			_showprgb(true, zk.pi ? 'z-initing': null);
		else
			setTimeout(showprgbInit, 10);
	}
	function showprgb() { //When passed to FF's setTimeout, 1st argument is not null
		_showprgb();
	}
	function _showprgb(mask, icon) {
		var $jq;
		if (zk.processing
		&& !($jq = jq('#zk_proc')).length && !jq('#zk_showBusy').length) {
			zUtl.progressbox('zk_proc', window.msgzk?msgzk.PLEASE_WAIT:'Processing...', mask, icon);
		} else if (icon == 'z-initing') {
			var $jq = $jq || jq('#zk_proc');
			if ($jq.length && $jq.hasClass('z-loading') && ($jq = $jq.parent()).hasClass('z-temp')) {
				$jq.append('<div class="z-initing"></div>');
			}
		}
	}
	function wgt2s(w) {
		var s = w.widgetName;
		return s + (w.id ? '$' + w.id: '') + '#' + w.uuid + '$' + w.$oid;
	}
	function toLogMsg(ars, detailed) {
		var msg = [], Widget = zk.Widget;
		for (var j = 0, len = ars.length; j < len; j++) {
			if (msg.length) msg.push(", ");
			var ar = ars[j];
			if (ar && (jq.isArray(ar) || ar.zk)) //ar.zk: jq(xx)
				msg.push('[' + toLogMsg(ar, detailed) + ']');
			else if (Widget && Widget.isInstance(ar))
				msg.push(wgt2s(ar));
			else if (ar && ar.nodeType) {
				var w = Widget && Widget.$(ar);
				if (w) msg.push(jq.nodeName(ar), (ar != w.$n() ? '#'+ar.id+'.'+ar.className:''), ':', wgt2s(w));
				else msg.push(jq.nodeName(ar), '#', ar.id);
			} else if (detailed && ar && (typeof ar == 'object') && !ar.nodeType) {
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
			var console = jq('#zk_log');
			if (!console.length) {
				jq(document.body).append(
	'<div id="zk_logbox" class="z-log">'
	+'<button class="z-button" onclick="jq(\'#zk_logbox\').remove()">X</button><br/>'
	+'<textarea id="zk_log" rows="10"></textarea></div>');
				console = jq('#zk_log');
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
	function _overrideSub(dstpt, nm, oldfn, newfn, tobak) {
		for (var sub = dstpt._$subs, j = sub ? sub.length: 0; --j >= 0;) {
			var subpt = sub[j];
			if (subpt[nm] === oldfn) {
				if (tobak)
					subpt['$'+nm] = oldfn; // B50-ZK-493
				subpt[nm] = newfn;
				_overrideSub(subpt, nm, oldfn, newfn, tobak); //recursive
			}
		}
	}
	
/** @class zk
 * @import zk.Package
 * @import zk.Class
 * @import zk.Desktop
 * @import zk.Widget
 * A collection of ZK core utilities.
 * The utilities are mostly related to the language enhancement we added to JavaScript,
 * such as {@link #$extends} and {@link #$package}.
 * <p>Refer to {@link jq} for DOM related utilities.
 */
zk.copy(zk, {
	/** A map of all classes, Map<int oid, zk.Class cls>.
	 * @since 5.0.8
	 */
	classes: {},
	/** Returns if the given JS object is a class ({@link zk.Class}).
	 * @param Object cls the object to test whether it is a class (aka., a  ZK class)
	 * @since 5.0.9
	 */
	isClass: function (cls) {
		return cls && cls.$class == zk.Class;
	},
	/** Returns whether the given JS object is a ZK object ({@link zk.Object}).
	 * @param Object o the object to test whether it is a ZK object
	 * @since 5.0.9
	 */
	isObject: function (o) {
		return o && o.$supers != null;
	},
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
    /** The timeout for re-sending AU request (unit: milliseconds).
     * Default: 200 (depending on the server's configuration)
	 * @since 6.5.2
     * @type int
     */
    resendTimeout: 200,
	/** The last position that the mouse was clicked (including left and right clicks).
	 * @type Offset
	 */
	clickPointer: [0, 0],
	/** The position of the mouse (including mouse move and click).
	 * @type Offset
	 */
	currentPointer: [0, 0],
	/** The widget that gains the focus now, or null if no one gains focus now.
	 * @type Widget
	 */
	//currentFocus: null,
	/** The topmost modal window, or null if no modal window at all.
	 * @type zul.wnd.Window
	 */
	//currentModal: null,

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
	 * @see #booted
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
	/** Indicates whether ZK Client Engine has been booted and created the initial widgets.
	 * It is useful to know if it is caused by an asynchronous update (i.e., zk.booted is true).
	 * @see #mounting
	 * @see #unloading
	 * @type boolean
	 */
	//booted: false,
	/** Indicates whether the browser is unloading this document.
	 * Note: when the function registered with {@link #beforeUnload} is called, this flag is not set yet.
	 * @see #loading
	 * @see #booted
	 * @type boolean
	 */
	//unloading: false,
	/** Indicates if the application is busy.
	 * It is actually a number that is increased when {@link zk.AuCmd0#showBusy(String)} is called,
	 * and decreased when {@link zk.AuCmd0#clearBusy()} is called.
	 * In other words, it is set by the application, and used to
	 * indicate the application (rather than ZK) is busy.
	 * @type int
	 * @since 5.0.1
	 */
	busy: 0,
	/** The application's name, which will be initialized as server-side's
	 * <code>WebApp.getAppName()</code>.
	 * It will be used as title of {@link jq#alert}.
	 * @type String
	 * @since 5.0.6
	 */
	appName: 'ZK',

	/** The version of ZK, such as '5.0.0'
	 * @type String
	 */
	//version: '',
	/** The build of ZK, such as '08113021'
	 * @type String
	 */
	//build: '',

	/** The user agent of the browser.
	 * @type String
	 */
	//agent: '',
	/** Returns the DOM API's version
	 * if the browser is Internet Explorer, or null if not.
	 * <p>Note: it is DOM API's version, not the browser version.
	 * In other words, it depends on if the browser is running in a compatible mode.
	 * FOr the browser's version, please use {@link #iex} instead.
	 * @type Double
	 */
	//ie: null,
	/** Returns the browser's version as double (only the first two part of the version, such as 8)
	 * if the browser is Internet Explorer, or null if not.
	 * <p>Notice that this is the browser's version, which might not be the
	 * version of DOM API. For DOM API's version, please use {@link #ie} instead.
	 * @type Double
	 * @since 5.0.7
	 */
	//iex: null,
	/** Whether it is Internet Exploer 6 (excluding 7 or others).
	 * @type Boolean
	 */
	//ie6_: false,
	/** Whether it is Internet Exploer 7 or later.
	 * @type Boolean
	 */
	//ie7: false,
	/** Whether it is Internet Exploer 7 (excluding 8 or others).
	 * @type Boolean
	 */
	//ie7_: false,
	/** Whether it is Internet Exploer 8 or later.
	 * @type Boolean
	 */
	//ie8: false,
	/** Whether it is Internet Exploer 8 or later, and running in
	 * Internet Explorer 7 compatible mode.
	 * @type Boolean
	 */
	//ie8c: false,
	/** Whether it is Internet Exploer 9 or later.
	 * @type Boolean
	 * @since 5.0.5
	 */
	//ie9: false,
	/** Returns the version as double (only the first two part of the version, such as 3.5)
	 * if it is Gecko-based browsers,
	 * such as Firefox.
	 * @type Double
	 */
	//gecko: null,
	/** Returns the version as double (only the first two part of the version, such as 3.6) if it is Firefox,
	 * such as Firefox. Notice that it is Firefox's version, such as
	 * 3.5, 3.6 and 4.0.
	 * If not a firefox, it is null.
	 * @type Double
	 * @since 5.0.6
	 */
	//ff: null,
	/** Returns the version as double (only the first two part of the version, such as 533.1) if it is Safari-based, or null if not.
	 * @type Double
	 */
	//safari: null,
	/** Returns the version as double (only the first two part of the version, such as 10.1) if it is Opera, or null if not.
	 * @type Double
	 */
	//opera: null,
	/** Whether it is Adobe AIR.
	 * @type Boolean
	 */
	//air: false,
	/** Whether it supports CSS3.
	 * @type Boolean
	 */
	//css3: false,

	/** The character used for decimal sign.
	 * @type char
	 */
	//DECIMAL: '',
	/** The character used for thousands separator.
	 * @type char
	 */
	//GROUPING: '',
	/** The character used to represent minus sign.
	 * @type char
	 */
	//MINUS: '',
	/** The character used for mille percent sign.
	 * @type char
	 */
	//PER_MILL: '',
	/** The character used for percent sign.
	 * @type char
	 */
	//PERCENT: '',

	/** Indicates whether an OS-level modal dialog is opened.
	 * In this case, onblur will be called so a widget can use it to
	 * decide whether to validate.
	 * @type boolean
	 */
	//alerting: false,
	/** Indicates whether {@link Widget#id} is always the same
	 * as {@link Widget#uuid}.
	 * <p>By default, it is false.
	 * <p>You could enable it if the pure-client approach is taken,
	 * since it is more convenient. However, it also means the concept of
	 * ID space is no longer valid.
	 * @type boolean
	 */
	//spaceless: null,

	/** The widget that captures the keystrokes.
	 * Used to specify a widget that shall receive the following the onKeyPress and onKeyUp events, no matter what widget the event occurs on.
<pre><code>
doKeyDown_: function () {
 zk.keyCapture = this;
 this.$supers('doKeyDown_', arguments);
}
</code></pre>
	 * <p>Notice that the key capture is reset automatically after processing onKeyUp_.
	 * @see #mouseCapture
	 * @type Widget
	 */
	//keyCapture: null,
	/** The widget that captures the mouse events.
	 * Used to specify a widget that shall receive the following the onMouseMove and onMouseUp events, no matter what widget the event occurs on.
<pre><code>
doMouseDown_: function () {
 zk.mouseCapture = this;
 this.$supers('doMouseDown_', arguments);
}
</code></pre>
	 * <p>Notice that the mouse capture is reset automatically after processing onMouseUp_.
	 * @see #keyCapture
	 * @type Widget
	 */
	//mouseCapture: null,

	/** Copies a map of properties (or options) from one object to another.
	 * Example: extending Array
<pre><code>
zk.copy(Array.prototoype, {
 $addAll: function (o) {
  return this.push.apply(this, o);
 }
});
</code></pre>
	 * <p>Notice that {@link #copy} copies the properties directly regardless
	 * if the target object has a setter or not. It is fast but if you want
	 * to go thru the setter, if any, use {@link #set(Object, Object, Array, boolean)}
	 * instead.
	 * @param Object dst the destination object to copy properties to
	 * @param Object src the source object to copy properties from
	 * @return Object the destination object
	 */
	/** Copies a map of properties (or options) from one object to another
	 * and copies the original value to another map.
	 * Example: copy style and restore back
<pre><code>
var backup = {};
zk.copy(n.style, {
	visibility: 'hidden',
	position: 'absolute',
	display: 'block'
	}, backup);
try {
	//do whatever
} finally {
	zk.copy(n.style, backup);
}
</code></pre>
	 * <p>Notice that {@link #copy} copies the properties directly regardless
	 * if the target object has a setter or not. It is fast but if you want
	 * to go thru the setter, if any, use {@link #set(Object, Object, Array, boolean)}
	 * instead.
	 * @param Object dst the destination object to copy properties to
	 * @param Object src the source object to copy properties from
	 * @param Map backup the map to stor the original value
	 * @return Object the destination object
	 * @since 5.0.3
	 */
	//copy: function () {},

	/** Retrieves and removes the value of the specified name of the given map.
	 * @param Map props a map of properties
	 * @param String nm the name to retrieve the value
	 * @return Object the value.
	 * @since 5.0.2
	 */
	cut: function (props, nm) {
		var v;
		if (props) {
			v = props[nm];
			delete props[nm];
		}
		return v;
	},

	/** Defines a package. It creates and returns the package if not defined yet.
	 * If the package is already defined, it does nothing but returns the package.
	 * It is similar to Java's package statement except it returns the package
	 * object.
	 * <p>Notice the package is usually defined automatically by use of
	 * <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Widget_Package_Descriptor>WPD</a>,
	 * so you're rarely need to use this method.
	 *
	 * <p>Example:
	 * <pre><code>var foo = zk.$package('com.foo');
	 *foo.Cool = zk.$extends(zk.Object);</code></pre>
	 *
	 * @param String name the name of the package.
	 * @return Package
	 * @see #$import(String)
	 * @see #load
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
	/** Imports a class or a package. It returns null if the package or class is not defined yet.
	 * <p>Example:
<pre><code>
var foo = zk.$import('com.foo');
var cool = new foo.Cool();
var Cool = zk.$import('com.foo.Cooler');
var cooler = new Cooler();
</code></pre>
	 * @param String name The name of the package or the class.
	 * @return Object a package ({@link Package}) or a class ({@link Class})
	 * @see #$package
	 * @see #load
	 * @see #$import(String, Function)
	 */
	/** Imports a package or class, and load it if <code>fn</code> is specified.
	 * It returns null if the package or class is not defined yet and
	 * <code>fn</code> is null.
	 * <p>If an additional function, <code>fn</code> is specified,
	 * this method assumes <code>name</code>
	 * is a class and it will load the package of the class first.
	 * If not found. Then, invoke the function after the class is loaded.
	 * For example, the following creates a Listbox widget after loading
	 * the package.
<pre><code>
zk.$import('zul.sel.Listbox', function (cls) {new cls();});
</code></pre>
	 * @param String name The name of the package or the class.
	 * @param Function fn The function to call after the class is loaded.
	 * If specified, it assumes <code>name</code> is a class, and it will
	 * load the package of the class automatically.
	 * In additions, the function is called with the class as the argument.
	 * @return Object a package ({@link Package}) or a class ({@link Class})
	 * @see #$package
	 * @see #$import(String)
	 * @see #load
	 */
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

	/** Defines a class. It returns the class being defined.
	 * <p>Example:
<pre><code>
zul.Label = zk.$extends(zk.Widget, {
 _value: '',
 getValue() {
  return this._value;
 },
 setValue(value) {
  this._value = value;
 }
});
</code></pre>
	 * <h3>$define</h3>
	 * To simplify the declaration of the getters and setters, <code>$define</code>
	 * is introduced. It is shortcut to invoke define. For example,
<pre><code>
foo.Widget = zk.$extends(zk.Widget, {
  _value: '',
  $define: {
    name: null,
    value: null
  },
  bind_: function () {
    //
  }
});
</code></pre>
	 * <p>is equivalent to
<pre><code>
foo.Widget = zk.$extends(zk.Widget, {
  _value: '',
  bind_: function () {
    //
  }
});
zk.define(foo.Widget, {
    name: null,
    value: null
});
</code></pre>

<p>is equivalent to
<pre><code>
foo.Widget = zk.$extends(zk.Widget, {
  _value: '',
  getName: function () {
    return this._name;
  },
  setName: function (v) {
    this._name = v;
    return this;
  },
  getValue: function () {
    return this._value;
  },
  setValue: function (v) {
    this._value = v;
    return this;
  },
  bind_: function () {
    //
  }
});
</code></pre>
	 * @param Class sueprclass the super class to extend from
	 * @param Map members a map of non-static members
	 * @param Map staticMembers a map of static members. Ignored if omitted.
	 * @return Class the class being defined
	 * @see #define
	 * @see #override
	 */
	$extends: function (superclass, members, staticMembers) {
		if (!superclass)
			throw 'unknown superclass';

		var fe = !(zk.feature && zk.feature.ee),
			superpt = superclass.prototype,
			jclass = newClass(function () {
				if (superclass.$copyf && !superclass.$copied) {
					superclass.$copyf();
					superclass.$copied = true;
				}
				var define = members['$define'],
					superpt = superclass.prototype,
					thispt = jclass.prototype;
				
				if (define)	delete members['$define'];
				
				var zf = zk.feature;
				if (!(zf && zf.ee)) {
					for (var p in superpt) {//inherit non-static
						var $p = '|'+p+'|';
						if ('|_$super|_$subs|$class|_$extds|superclass|className|widgetName|blankPreserved|'.indexOf($p) < 0) {
							thispt[p] = superpt[p];	
						} else if (thispt[p] == undefined && '|className|widgetName|blankPreserved|'.indexOf($p) >= 0) {
							thispt[p] = superpt[p]; // have to inherit from its parent.
						}
					}
				}
				
				zk.define(jclass, define);
				zk.copy(thispt, members);
			}),
			thispt = jclass.prototype;
		
		if (fe) {
			jclass.$copyf();
			jclass.$copied = true;
		} else {
			function _init() { this.constructor = jclass; };
		    _init.prototype = superclass.prototype;
		    jclass.prototype = new _init();
			thispt = jclass.prototype;
		}
		
		for (var p in superclass) //inherit static
			if ('|prototype|$copyf|$copied|'.indexOf('|'+p+'|') < 0)
				jclass[p] = superclass[p];

		zk.copy(jclass, staticMembers);

		thispt._$super = superpt;
		thispt._$subs = [];
		superpt._$subs.push(thispt);
			//maintain a list of subclasses (used zk.override)
		return regClass(jclass, superclass);
	},

	/** Provides the default values for the specified options.
	 * <p>Example:
<pre><code>
process: function (opts, defaultOptions) {
 opts = zk.$default(opts, defaultOptions);
}
</code></pre>
<pre><code>
opts = zk.$default(opts, {timeout: 100, max: true});
 //timeout and max are assigned to opts only if no such property found in opts
</code></pre>
	 * @param Map opts a map of options to which default options will be added
	 * If null, an empty map is created automatically.
	 * @param Map defaults a map of default options
	 * @return Map the merged options
	 * @see #copy
	 */
	$default: function (opts, defaults) {
		opts = opts || {};
		for (var p in defaults)
			if (opts[p] === undefined)
				opts[p] = defaults[p];
		return opts;
	},

	/** Overrides the properties of a map.
	 * It is similar to {@link #copy}, except
	 * <ol>
	 * <li>It preserves the original member (method or data) in the backup
	 * argument.</li>
	 * <li>It handles the class extensions ({@link #$extends}) well.
	 * For example, the members of all deriving classes will be overriden too,
	 * if necessary.</li>
	 * </ol>
	 * <p>Example,
<pre><code>
var _xCombobox = {}
zk.override(zul.inp.Combobox.prototype, _xCombobox, {
	redrawpp_: function (out) {
		if (!_redrawpp(this, out))
			_xCombobox.redrawpp_.apply(this, arguments); //call the original method
	},
	open: function () {
		_renderpp(this);
		_xCombobox.open.apply(this, arguments); //call the original method
	}
});
</code></pre>
	 * @param Object dst the destination object to override
	 * @param Map backup the map used to store the original members (or properties)
	 * @param Map src the source map providing the new members
	 * @return Object the destination object
	 * @see #override(Function, Function)
	 * @see #override(Object, String, Object)
	 */
	/** Overrides a particular method.
	 * The old method will be returned, and the caller could store it for
	 * calling back. For example,
	 *
	 * <pre><code>var superopen = zk.override(zul.inp.Combobox.prototype.open,
	 *  function () {
	 *    superopen.apply(this, arguments);
	 *    //do whatever you want
	 *  });</code></pre>
	 *
	 * @param Function oldfunc the old function that will be replaced
	 * @param Function newfunc the new function that will replace the old function
	 * @return Function the old function (i.e., oldfunc)
	 * @since 5.0.6
	 * @see #override(Object, Map, Map)
	 */
	/** Overrides a particular method or data member.
	 * The old method will be stored in method called $nm (assuming nm is
	 * the method name to override. For example,
	 *
	 * <pre><code>zk.override(zul.inp.Combobox.prototype, "open",
	 *  function () {
	 *    this.$open.apply(this, arguments);
	 *    //do whatever you want
	 *  });</code></pre>
	 *
	 * @param Object dst the destination object to override
	 * @param String nm the method name to override
	 * @param Object val the value of the method. To override a method, it has
	 * to be an instance of {@link Function}.
	 * @return Object the destination object
	 * @since 5.0.2
	 * @see #override(Function, Function)
	 * @see #override(Object, Map, Map)
	 */
	override: function (dst, backup, src) {
		var $class = dst.$class;
		if ($class && $class.$copied === false) {
			var f = $class.$copyf;
			$class.$copyf = function () {
				f();
				$class.$copied = true;
				zk.override(dst, backup, src);
			};
			return dst;
		}
		var fe = !(zk.feature && zk.feature.ee);
		switch (typeof backup) {
		case 'function':
			var old = dst;
			dst = backup;
			return old;
		case 'string':
			// B50-ZK-493: shall update subclasses
			if (fe)
				_overrideSub(dst, backup, dst['$'+backup] = dst[backup], dst[backup] = src, true);
			else {
				dst['$'+backup] = dst[backup];
				dst[backup] = src;
			}
			return dst;
		}
		if (fe) {
			for (var nm in src)
				_overrideSub(dst, nm, backup[nm] = dst[nm], dst[nm] = src[nm]);
		} else {
			for (var nm in src) {
				backup[nm] = dst[nm];
				dst[nm] = src[nm];
			}
		}
		return dst;
	},

	/** Defines the setter and getter methods.
	 * <p>Notice that you rarely need to invoke this method directly. Rather, you can specify them in a property named $define, when calling #$extends.
	 * <p>For example, the following code snippet
<pre><code>
zk.define(zul.wgt.Button, {
  disabled: null
});
</code></pre>

is equivalent to define three methods, isDisabled, getDisabled and setDisabled, as follows:

<pre><code>
zul.wgt.Button = zk.$extends(zk.Widget, {
  isDisabled: _zkf = function () {
    return this._disabled;
  },
  isDisabled: _zkf,
  setDisabled: function (v) {
    this._disabled = v;
    return this;
  }
});
</code></pre>

If you want to do something when the value is changed in the setter, you can specify a function as the value in the props argument. For example,

<pre><code>
zk.define(zul.wgt.Button, {
  disabled: function () {
    if (this.desktop) this.rerender();
  }
});
</code></pre>

will cause the setter to be equivalent to

<pre><code>
setDisabled: function (v, opts) {
  this._disabled = v;
  if (this._disabled !== v || (opts && opts.force))
    if (this.desktop) this.rerender();
  return this;
}
</code></pre>

If you want to pre-process the value, you can specify a two-element array. The first element is the function to pre-process the value, while the second is to post-process if the value is changed, as described above. For example,

<pre><code>
zk.define(zul.wgt.Button, {
  disabled: [
  	function (v) {
  		return v != null && v != false;
  	},
  	function () {
	    if (this.desktop) this.rerender();
  	}
  ]
});
</code></pre>

will cause the setter to equivalent to

<pre><code>
setDisabled: function (v) {
  v = v != null && v != false;
  this._disabled = v;
  if (this._disabled !== v || (opts && opts.force))
    if (this.desktop) this.rerender();
  return this;
}
</code></pre>

Notice that when the function (specified in props) is called, the arguments are the same as the arguments passed to the setter (including additional arguments. For example, if button.setDisabled(true, false) is called, then the specified function is called with two arguments, true and false. In additions, as shown above, you can enforce the invocation of the function by passing a map with force as true.

<pre><code>
wgt.setSomething(somevalue, {force:true});
</code></pre>
	 * @param Class klass the class to define the members
	 * @param Map props the map of members (aka., properties)
	 * @see #$extends
	 * @return Class the class being defined
	 */
	define: function (klass, props) {
		for (var nm in props) {
			var nm1 = '_' + nm,
				nm2 = nm.charAt(0).toUpperCase() + nm.substring(1),
				pt = klass.prototype,
				after = props[nm], before = null;
			if (jq.isArray(after)) {
				before = after.length ? after[0]: null;
				after = after.length > 1 ? after[1]: null;
			}
			pt['set' + nm2] = before ?
				after ? defSet11(nm1, before, after): defSet10(nm1, before):
				after ? defSet01(nm1, after): defSet00(nm1);
			pt['get' + nm2] = pt['is' + nm2] = defGet(nm1);
		}
		return klass;
	},

	/** A does-nothing-but-returns-false function.
	 */
	$void: function () {return false;},

	/** Parses a string to an integer.
	 * <p>It is the same as the built-in parseInt method except it never return
	 * NaN (rather, it returns 0).
	 * @param String v the text to parse
	 * @param int b represent the base of the number in the string. 10 is assumed if omitted.
	 * @return int the integer
	 */
	parseInt: function (v, b) {
		return v && !isNaN(v = parseInt(v, b || 10)) ? v: 0;
	},
	/** Parses a string to a floating number.
	 * <p>It is the same as the built-in parseFloat method except it never return
	 * NaN (rather, it returns 0).
	 * @param String v the text to parse
	 * @return double the floating number
	 * @since 5.0.2
	 */
	parseFloat: function (v) {
		return v && !isNaN(v = parseFloat(v)) ? v: 0;
	},

	/** Assigns a value to the specified property.
	 * <p>For example, <code>zk.set(obj, "x", 123)</code>:<br/>
	 * If setX is defined in obj, obj.setX(123) is called.
	 * If not defined, obj.x = 123 is called.
	 * <p>Anotehr example:
<pre><code>
zk.set(o, 'value', true); //set a single property
</code></pre>
	 * @param Object o the object to assign values to
	 * @param String name the property's name
	 * @param Object value the property's value
	 * @param Object extra an extra argument to pass to the setX method
	 * as the extra argument (the second argument).
	 * For example, <code>zk.set(obj, 'x', 123, true)</code> invokes <code>obj.setX(123, true)</code>.
	 * @see #get
	 * @return Object the destination object
	 */
	/** Sets the given properties from one object to another.
	 * Example:
<pre><code>
zk.set(dst, src, ["foo", "mike"]);
</code></pre>
	 * If dst has a method called setFoo and src has method called getMike,
	 * then it is equivalent to
<pre><code>
	dst.setFoo("foo", src["foo"]);
	dst["mike"] = src.getMike();
</code></pre>
	 *
	 * @param Object dst the destination object to copy properties to
	 * @param Object src the source object to copy properties from
	 * @param Array props an array of property names (String)
	 * @param boolean ignoreUndefined whether to ignore undefined.
	 * Optional (if not specified, false is assumed).
	 * If true and src[name] is undefined, then dst[name] won't be assigned.
	 * @return Object the destination object
	 * @since 5.0.3
	 * @see #copy
	 */
	set: function (o, name, value, extra) {
		if (typeof name == 'string') {
			zk._set(o, name, value, extra);
		} else //o: dst, name: src, value: props
			for (var j = 0, len = value.length, m, n, v; j < len;) {
				n = value[j++];
				m = name['get' + n.charAt(0).toUpperCase() + n.substring(1)];
				if (!extra || m || name[n] !== undefined) //extra: ignoreUndefined in this case
					zk._set(o, n, m ? m.call(name): name[n]);
			}
		return o;
	},
	_set: function (o, name, value, extra) { //called by widget.js (better performance)
		zk._set2(o,
			o['set' + name.charAt(0).toUpperCase() + name.substring(1)],
			name, value, extra);
	},
	_set2: function (o, mtd, name, value, extra) { //called by widget.js (better performance)
		if (mtd) {
			if (extra !== undefined)
				mtd.call(o, value, extra);
			else
				mtd.call(o, value);
		} else
			o[name] = value;
	},
	/** Retrieves a value from the specified property.
	 * <p>For example, <code>zk.get(obj, "x")</code>:<br/>
	 * If getX or isX is defined in obj, obj.isX() or obj.getX() is returned.
	 * If not defined, obj.x is returned.
	 * <p>Another example:
<pre><code>
zk.get(o, 'value');
</code></pre>
	 * @param Object o the object to retreive value from
	 * @param String name the name
	 * @return Object the value of the property
	 */
	get: function (o, name) {
		var nm = name.charAt(0).toUpperCase() + name.substring(1),
			m = o['get' + nm];
		if (m) return m.call(o);
		m = o['is' + nm];
		if (m) return m.call(o);
		return o[name];
	},

	//Processing//
	/** Set a flag, {@link #processing}, to indicate that it starts a processing. It also shows a message to indicate "processing" after the specified timeout.
	 * <p>Example:
<pre></code>
zk.startProcessing(1000);
//do the lengthy operation
zk.endProcessing();
</code></pre>
	 * @param int timeout the delay before showing a message to indicate "processing".
	 * @see #processing
	 * @see #endProcessing
	 */
	startProcessing: function (timeout) {
		zk.processing = true;
		setTimeout(jq.isReady ? showprgb: showprgbInit, timeout > 0 ? timeout: 0);
	},
	/** Clears a flag, {@link #processing}, to indicate that it the processing has done. It also removes a message, if any, that indicates "processing".
	 * <p>Example:
<pre><code>
zk.startProcessing(1000);
//do the lengthy operation
zk.endProcessing();
</code></pre>
	 * @see #startProcessing
	 */
	endProcessing: function () {
		zk.processing = false;
		zUtl.destroyProgressbox('zk_proc');
	},

	/** Disable the default behavior of ESC. In other words, after called, the user cannot abort the loading from the server.
	 * <p>To enable ESC, you have to invoke {@link #enableESC} and the number
	 * of invocations shall be the same.
	 * @see #enableESC
	 */
	disableESC: function () {
		++zk._noESC;
	},
	/** Enables the default behavior of ESC (i.e., stop loading from the server).
	 * @see #disableESC
	 */
	enableESC: function () {
		--zk._noESC;
	},
	_noESC: 0, //# of disableESC being called (also used by mount.js)

	//DEBUG//
	/** Display an error message to indicate an error.
	 *  Example:
<pre><code>zk.error('Oops! Something wrong:(');</code></pre>
	 * @param String msg the error message
	 * @see #errorDismiss
	 * @see #log
	 * @see #stamp(String, boolean)
	 */
	error: function (msg) {
		zAu.send(new zk.Event(null, 'error', {message: msg}, {ignorable: true}), 800);
		zk._Erbx.push(msg);
	},
	/** Closes all error messages shown by {@link #error}.
   	 * Example:
<pre><code>zk.errorDismiss();</code></pre>
	 * @see #error
	 */
	errorDismiss: function () {
		zk._Erbx.remove();
	},
	/** Logs an message for debugging purpose.
	 * Example:
<pre><code>
zk.log('reach here');
zk.log('value is", value);
</code></pre>
	 * @param Object... detailed varient number of arguments to log
	 * @see #stamp(String, boolean)
	 */
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
		if (zk.mobile) {
			console.log(_logmsg);
			_logmsg = null;
		} else setTimeout(function(){jq(doLog);}, 300);
	},
	/** Make a time stamp for this momemt; used for performance tuning.
	 * A time stamp is represented by a name. It is an easy way to measure
	 * the performance. At the end of executions, the time spent between
	 * any two stamps (including beginning and ending) will be logged
	 * (with {@link #log}).
	 * @param String name the unique name to represent a time stamp
	 * @param boolean noAutoLog whehter not to auto log the result.
	 * If omitted or false, {@link #stamp()} will be invoked automatically
	 * to log the info and clean up all stamps after mounting is completed.
	 * Turn it off, if you prefer to invoke it manually.
	 */
	/** Logs the information of all stamps made by {@link #stamp(String, boolean)}.
	 * After the invocation, all stamps are reset.
	 */
	stamp: function (nm, noAutoLog) {
		if (nm) {
			if (!noAutoLog && !_stamps.length)
				setTimeout(_stampout, 0);
			_stamps.push({n: nm, t: jq.now()});
		} else if (_stamps.length) {
			var t0 = _t0;
			for (var inf; (inf = _stamps.shift());) {
				zk.log(inf.n + ': ' + (inf.t - _t0));
				_t0 = inf.t;
			}
			zk.log('total: ' + (_t0 - t0));
		}
	},

	/** Encodes and returns the URI to communicate with the server.
	 * Example:
<pre><code>document.createElement("script").src = zk.ajaxURI('/web/js/com/foo/mine.js',{au:true});</code></pre>
	 * @param String uri - the URI related to the AU engine. If null, the base URI is returned.
	 * @param Map opts [optinal] the options. Allowed values:<br/>
	 * <ul>
	 * <li>au - whether to generate an URI for accessing the ZK update engine. If not specified, it is used to generate an URL to access any servlet</li>
	 * <li>desktop - the desktop or its ID. If null, the first desktop is used.</li>
	 * <li>ignoreSession - whether to handle the session ID in the base URI.</li>
	 * </ul>
	 * @return String the encoded URI
	 */
	ajaxURI: function (uri, opts) {
		var ctx = zk.Desktop.$(opts?opts.desktop:null),
			au = opts && opts.au;
		ctx = (ctx ? ctx: zk)[au ? 'updateURI': 'contextURI'];
		uri = uri || '';

		var abs = uri.charAt(0) == '/';
		if (au && !abs) {
			abs = true;
			if (uri)
				uri = '/' + uri; //non-au supports relative path
		}

		var j = ctx.indexOf(';'), //ZK-1668: may have multiple semicolon in the URL
			k = ctx.lastIndexOf('?');
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
	/** Declares the desktop is used for the stateless context.
	 * By stateless we mean the server doesn't maintain any widget at all.
	 * @param String dtid the ID of the desktop to create
	 * @param String contextURI the context URI, such as /zkdemo
	 * @param String updateURI the update URI, such as /zkdemo/zkau
	 * @param String reqURI the URI of the request path.
	 * @return Desktop the stateless desktop being created
	 */
	stateless: function (dtid, contextURI, updateURI, reqURI) {
		var Desktop = zk.Desktop, dt;
		dtid = dtid || ('z_auto' + _statelesscnt++);
		dt = Desktop.all[dtid];
		if (dt && !dt.stateless) throw 'Desktop conflict';
		if (zk.updateURI == null)
			zk.updateURI = updateURI;
		if (zk.contextURI == null) //it might be ""
			zk.contextURI = contextURI;
		return dt || new Desktop(dtid, contextURI, updateURI, reqURI, true);
	}
});

//zk.agent//
(function () {
	function _ver(ver) {
		return parseFloat(ver) || ver;
	}
	function _setCookie(name, value, exdays) {
		var exdate = new Date();
		exdate.setDate(exdate.getDate() + exdays);
		var value = escape(value) + ((exdays == null) ? '' : '; expires=' + exdate.toUTCString());
		document.cookie = name + '=' + value + ';path=/';
	}
	function _getCookie(name) {
		var cookies = document.cookie.split(';'),
			len = cookies.length,
			value = 0;
		for (var i = 0, c, j; i < len; i++) {
			c = cookies[i];
			j = c.indexOf('=');
			if (name == jq.trim(c.substr(0, j))) {
				value = zk.parseInt(jq.trim(c.substr(j+1)));
				break;
			}
		}
		return value;
	}

	// jQuery 1.9 remove the jQuery.browser
	jq.uaMatch = function( ua ) {
		ua = ua.toLowerCase();

		var match = /(chrome)[ \/]([\w.]+)/.exec( ua ) ||
			/(webkit)[ \/]([\w.]+)/.exec( ua ) ||
			/(opera)(?:.*version|)[ \/]([\w.]+)/.exec( ua ) ||
			/(msie) ([\w.]+)/.exec( ua ) ||
			ua.indexOf('compatible') < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec( ua ) ||
			[];

		return {
			browser: match[ 1 ] || '',
			version: match[ 2 ] || '0'
		};
	};

	// Don't clobber any existing jq.browser in case it's different
	if ( !jq.browser ) {
		matched = jq.uaMatch( navigator.userAgent );
		browser = {};

		if ( matched.browser ) {
			browser[ matched.browser ] = true;
			browser.version = matched.version;
		}

		// Chrome is Webkit, but Webkit is also Safari.
		if ( browser.chrome ) {
			browser.webkit = true;
		} else if ( browser.webkit ) {
			browser.safari = true;
		}

		jq.browser = browser;
	}
	var browser = jq.browser,
		agent = zk.agent = navigator.userAgent.toLowerCase();
	zk.opera = browser.opera && _ver(browser.version);
	zk.ff = zk.gecko = browser.mozilla && _ver(browser.version);
	zk.linux = agent.indexOf('linux') >= 0;
	zk.mac = !zk.ios && agent.indexOf('mac') >= 0;
	zk.webkit = browser.webkit;
	zk.chrome = browser.chrome;
	zk.safari = browser.webkit && !zk.chrome; // safari only
	zk.ios = zk.webkit && /iphone|ipad|ipod/.test(agent);
	zk.android = zk.webkit && (agent.indexOf('android') >= 0);
	zk.mobile = zk.ios || zk.android;
	zk.css3 = true;
	
	zk.vendor = zk.webkit ? 'webkit' : '';

	var bodycls;
	if (zk.ff) {
		if (zk.ff < 5 //http://www.useragentstring.com/_uas_Firefox_version_5.0.php
		&& (bodycls = agent.indexOf('firefox/')) > 0)
			zk.ff = zk.gecko = _ver(agent.substring(bodycls + 8));
		bodycls = 'gecko gecko' + Math.floor(zk.ff);
		zk.vendor = 'Moz';
	} else if (zk.opera) { //no longer to worry 10.5 or earlier
		bodycls = 'opera';
		zk.vendor = 'O';
	} else {
		zk.iex = browser.msie && _ver(browser.version); //browser version
			//zk.iex is the Browser Mode (aka., Compatibility View)
			//while zk.ie is the Document Mode
		if (zk.iex) {
			if ((zk.ie = document.documentMode||zk.iex) < 6) //IE7 has no documentMode
				zk.ie = 6; //assume quirk mode
			zk.ie7 = zk.ie >= 7; //ie7 or later
			zk.ie8 = zk.ie >= 8; //ie8 or later
			zk.css3 = zk.ie9 = zk.ie >= 9; //ie9 or later
			zk.ie10 = zk.ie >= 10; //ie10 or later
			zk.ie6_ = zk.ie < 7;
			zk.ie7_ = zk.ie == 7;
			zk.ie8_ = zk.ie == 8;
			zk.ie9_ = zk.ie == 9;
			zk.ie10_ = zk.ie == 10;
			bodycls = 'ie ie' + Math.floor(zk.ie);
			zk.vendor = 'ms';
			
			// ZK-1878: IE Compatibility View issue when using Meta tag with IE=edge
			var v = _getCookie('zkie-compatibility');
			if (zk.iex != zk.ie || (v && v != zk.ie)) {
				if (v != zk.ie) {
					_setCookie('zkie-compatibility', zk.ie, 365*10);
					window.location.reload();
				}
			}
		} else {
			if (zk.webkit)
				bodycls = 'webkit webkit' + Math.floor(zk.webkit);
			if (zk.mobile) {
				bodycls = (bodycls || '') + ' mobile';
				if (zk.ios)
					bodycls = (bodycls || '') + ' ios';
				else
					bodycls = (bodycls || '') + ' android';
			}
		}
	}
	if ((zk.air = agent.indexOf('adobeair') >= 0) && zk.webkit)
		bodycls = (bodycls || '') + ' air';

	if (bodycls)
		jq(function () {
			jq(document.body).addClass(bodycls);
		});
	
	zk.vendor_ = zk.vendor.toLowerCase();
})();

//zk.Object//
	function getProxy(o, f) { //used by zk.Object
		return function () {
				return f.apply(o, arguments);
			};
	}
zk.Class = function () {}; //regClass() requires zk.Class
regClass(zk.Object = newClass());
/** @class zk.Object
 * The root of the class hierarchy.
 * @see zk.Class
 */
zk.Object.prototype = {
	/** The constructor.
	 * <p>Default: it does nothing so the subclass needs not to copy back
	 * (also harmless to call back).
	 * @see #afterInit
	 */
	$init: zk.$void,
	/** Specifies a function that shall be called after the object is initialized,
	 * i.e., after {@link #$init} is called. This method can be called only during the execution of {@link #$init}.
	 * <p>It is an advance feature that is used to allow a base class to do something that needs to wait for all deriving classes have been initialized.
	 *
	 * <p>Invocation Sequence:
	<ul>
	<li>The most derived class's $init (subclass)</li>
	<li>The based class's $init (if the derived class's $init invokes this.$supers('$init', arguments))</li>
	<li>The first function, if any, be added with afterInit, then the second (in the same order that afterInit was called)... </li>
	</ul>
	 * @param Function func the function to register for execution later
	 * @see #$init
	 */
	afterInit: function (f) {
		(this._$ais = this._$ais || []).unshift(f); //reverse
	},
	/** The class that this object belongs to.
	 * @type zk.Class
	 */
	//$class: zk.Object, //assigned in regClass()
	/** The object ID. Each object has its own unique $oid.
	 * It is mainly used for debugging purpose.
	 * <p>Trick: you can test if a JavaScript object is a ZK object by examining this property, such as
	 * <code>if (o.$oid) alert('o is a ZK object');</code>
	 * <p>Notice: zk.Class extends from zk.Object (so a class also has $oid)
	 * @type int
	 */
	//$oid: 0,
	/** Determines if this object is an instance of the class represented by the specified Class parameter.
	 * Example:
<pre><code>
if (obj.$instanceof(zul.wgt.Label, zul.wgt.Image)) {
}
</code></pre>
	 * @param Class klass the Class object to be checked.
	 * Any number of arguments can be specified.
	 * @return boolean true if this object is an instance of the class
	 */
	$instanceof: function () {
		if (this.$class)
			for (var extds = this.$class._$extds, args = arguments,
					j = args.length, cls; j--;)
				if ((cls = args[j]) && extds[cls.$oid])
					return true; //found
		return false;
	},
	/** Invokes a method defined in the superclass with any number of arguments. It is like Function's call() that takes any number of arguments.
	 * <p>Example:
<pre><code>
multiply: function (n) {
 return this.$super('multiply', n + 2);
}
</code></pre>
	 * @param String mtd the method name to invoke
	 * @param Object... vararg any number of arguments
	 * @return Object the object being returned by the method of the superclass.
	 * @see #$supers
	 */
	/** Invokes a method defined in the superclass with any number of arguments.
	 * It is like Function's call() that takes any number of arguments.
	 * <p>It is similar to {@link #$super(String, Object...)}, but
	 * this method works even if the superclass calls back the same member method.
	 * In short, it is tedious but safer.
	 * <p>Example:
<pre><code>
foo.MyClass = zk.$extends(foo.MySuper, {
  multiply: function (n) {
   return this.$super(foo.MyClass, 'multiply', n + 2);
  }
</code></pre>
	 * <p>Notice that the class specified in the first argument is <i>not</i>
	 * the super class having the method. Rather,
	 * it is the class that invokes this method.
	 * @param Class klass the class that invokes this method.
	 * @param String mtd the method name to invoke
	 * @param Object... vararg any number of arguments
	 * @return Object the object being returned by the method of the superclass.
	 * @see #$supers
	 * @since 5.0.2
	 */
	$super: function (arg0, arg1) {
		if (typeof arg0 != 'string') {
			return this.$supers(arg0, arg1, [].slice.call(arguments, 2));
		}
		return this.$supers(arg0, [].slice.call(arguments, 1));
	},
	/** Invokes a method defined in the superclass with an array of arguments. It is like Function's apply() that takes an array of arguments.
	 * <p>Example:
<pre><code>
multiply: function () {
 return this.$supers('multiply', arguments);
}
</code></pre>
	 * @param String mtd the method name to invoke
	 * @param Array args an array of arguments. In most case, you just pass
	 * <code>arguments</code> (the built-in variable).
	 * @return Object the object being returned by the method of the superclass.
	 * @see #$super
	 */
	/** Invokes a method defined in the superclass with an array of arguments. It is like Function's apply() that takes an array of arguments.
	 * <p>It is similar to {@link #$supers(String, Array)}, but
	 * this method works even if the superclass calls back the same member method.
	 * In short, it is tedious but safer.
	 * <p>Example:
<pre><code>
foo.MyClass = zk.$extends(foo.MySuper, {
  multiply: function () {
   return this.$supers(foo.MyClass, 'multiply', arguments);
  }
</code></pre>
	 * <p>Notice that the class specified in the first argument is <i>not</i>
	 * the super class having the method. Rather,
	 * it is the class that invokes this method.
	 * @param Class klass the class that invokes this method.
	 * @param String mtd the method name to invoke
	 * @param Array args an array of arguments. In most case, you just pass
	 * <code>arguments</code> (the built-in variable).
	 * @return Object the object being returned by the method of the superclass.
	 * @see #$super
	 * @since 5.0.2
	 */
	$supers: function (nm, args, argx) {
		var supers = this._$supers;
		if (!supers) supers = this._$supers = {};

		if (typeof nm != 'string') { //zk.Class assumed
			var old = supers[args], p; //args is method's name
			if (!(p = nm.prototype._$super) || !(nm = p[args])) //nm is zk.Class
				throw args + ' not in superclass'; //args is the method name

			supers[args] = p;
			try {
				return nm.apply(this, argx);
			} finally {
				supers[args] = old; //restore
			}
		}

		//locate method
		var old = supers[nm], m, p, oldmtd;
		if (old) {
			oldmtd = old[nm];
			p = old;
		} else {
			oldmtd = this[nm];
			p = this;
		}
		while (p = p._$super)
			if (oldmtd != p[nm]) {
				m = p[nm];
				if (m) supers[nm] = p;
				break;
			}

		if (!m)
			throw nm + ' not in superclass';

		try {
			return m.apply(this, args);
		} finally {
			supers[nm] = old; //restore
		}
	},
	//a list of subclass's prototypes
	_$subs: [],

	/** Proxies a member function such that it can be called with this object in a context that this object is not available.
	 * It sounds a bit strange at beginning but useful when passing a member
	 * of an object that will be executed as a global function.
	 *
	 * <p>Example: Let us say if you want a member function to be called periodically, you can do as follows.
<pre><code>
setInterval(wgt.proxy(wgt.doIt), 1000); //assume doIt is a member function of wgt
</code></pre>
	* <p>With proxy, when doIt is called, this references to wgt. On the other hand, the following won't work since this doesn't reference to wgt, when doIt is called.
<pre><code>
setInterval(wgt.doIt, 1000); //WRONG! doIt will not be called with wgt
</code></pre>
	* <p>Notice that this method caches the result so that it will return the same
	* proxied function, if you pass the same function again.
	* @param Function func a method member of this object
	* @return Function a function that can be called as a global function
	* (that actually have <code>this</code> referencing to this object).
	*/
	proxy: function (f) {
		var fps = this._$proxies, fp;
		if (!fps) this._$proxies = fps = {};
		else if (fp = fps[f]) return fp;
		return fps[f] = getProxy(this, f);
	}
};

/** @partial zk.Object
 */
_zkf = {
	/** Determines if the specified Object is assignment-compatible with this Class. This method is equivalent to [[zk.Object#$instanceof].
	 * Example:
<pre><code>
if (klass.isInstance(obj)) {
}
</code></pre>
	 * @param Object o the object to check
	 * @return boolean true if the object is an instance
	 */
	isInstance: function (o) {
		return o && o.$instanceof && o.$instanceof(this);
	},
	/** Determines if the class by this Class object is either the same as, or is a superclass of, the class represented by the specified Class parameter.
	 * Example:
<pre><code>
if (klass1.isAssignableFrom(klass2)) {
}
</code></pre>
	 * @param zk.Class cls the Class object to be checked, such as zk.Widget.
	 * @return boolean true if assignable
	 */
	isAssignableFrom: function (cls) {
		return cls && (cls = cls._$extds) && cls[this.$oid] != null;
	}
};
zk.copy(zk.Object, _zkf);
zk.copy(regClass(zk.Class, zk.Object), _zkf);

//error box//
var _erbx, _errcnt = 0;

zk._Erbx = zk.$extends(zk.Object, { //used in HTML tags
	$init: function (msg) {
		var id = 'zk_err',
			$id = '#' + id,
			click = zk.mobild ? ' ontouchstart' : ' onclick',
			// Use zUtl.encodeXML -- Bug 1463668: security
 			html = ['<div class="z-error" id="', id, '">',
 			        '<div class="messagecontent"><div class="messages">',
 			        zUtl.encodeXML(msg, {multiline : true}), '</div></div>',
 			        '<div id="', id, '-p"><div class="errornumbers">',
 					'<i class="z-icon-warning-sign"/>&nbsp;', ++_errcnt, ' Errors</div>',
 					'<div class="button"', click, '="zk._Erbx.remove()">',
 					'<i class="z-icon-remove"/></div>',
 					'<div class="button"', click, '="zk._Erbx.redraw()">',
 					'<i class="z-icon-refresh"/></div></div></div>'];

		jq(document.body).append(html.join(''));
		_erbx = this;
		this.id = id;
		try {
			var n;
			this.dg = new zk.Draggable(null, n = jq($id)[0], {
				handle: jq($id + '-p')[0], zIndex: n.style.zIndex,
				starteffect: zk.$void, starteffect: zk.$void,
				endeffect: zk.$void});
		} catch (e) {
		}
		jq($id).slideDown(1000);
	},
	destroy: function () {
		_erbx = null;
		_errcnt = 0;
		if (this.dg) this.dg.destroy();
		jq('#' + this.id).remove();
	}
},{
	redraw: function () {
		zk.errorDismiss();
		zAu.send(new zk.Event(null, 'redraw'));
	},
	push: function (msg) {
		if (!_erbx)
			return new zk._Erbx(msg);

		var id = _erbx.id;
		jq('#' + id + ' .errornumbers')
			.html('<i class="z-icon-warning-sign"/>&nbsp;'+ ++_errcnt + ' Errors');
		jq('#' + id + ' .messages')
			.prepend('<div class="newmessage">' + msg + '</hr></div>');
		jq('#' + id + ' .newmessage')
			.removeClass('newmessage').addClass('message').slideDown(600)
	},
	remove: function () {
		if (_erbx) _erbx.destroy();
	}
});
})();
