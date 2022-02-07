/* global Reflect: readonly */
/* zk.ts

	Purpose:

	Description:

	History:
		Mon Sep 29 17:17:26 2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
import {type Draggable} from '@zk/drag';
import {type Callable} from '@zk/types';
import {type Widget} from '@zk/widget';

export type DataHandler = (wgt: Widget, val: unknown) => void;

// eslint-disable-next-line @typescript-eslint/no-explicit-any
let _zk: any & {mm?: any}, _zkf;
(_zk = function (sel: string | HTMLElement | Widget) {
	return jq(sel, _zk).zk;
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
});

// Note: we cannot use Object.assign() here, because some prototype property may not be copied.
// eslint-disable-next-line space-before-function-paren
_zk.copy = function <T>(dst: T, src: ThisType<T>, backup?: object): T {
	// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
	dst = dst || ({} as T);
	for (var p in src) {
		if (backup) backup[p] = dst[p];
		dst[p] = src[p];
	}
	return dst;
};

	type Getter = Function; // eslint-disable-line @typescript-eslint/ban-types
	type Setter = Function; // eslint-disable-line @typescript-eslint/ban-types
	type GeneratedSetter = (this: Widget, v: unknown, opts: Partial<{ force: boolean }>) => Widget;

	var _oid = 0,
		_statelesscnt = 0,
		_logmsg,
		_stamps: {n: string; t: number}[] = [],
		_t0 = jq.now(),
		_procq = {}; // Bug ZK-3053

	function _isNativeReflectConstruct(): boolean {
		if (typeof Reflect === 'undefined' || !Reflect.construct)
			return false;
		if (Reflect.construct['sham'])
			return false;
		if (typeof Proxy === 'function')
			return true;
		try {
			Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function (): void {}));
			return true;
		} catch (e) {
			return false;
		}
	}

	function _inherits(subClass, superClass): void {
		if (typeof superClass !== 'function' && superClass !== null) {
			throw new TypeError('Super expression must either be null or a function');
		}
		subClass.prototype = Object.create(superClass && superClass.prototype, {
			constructor: {
				value: subClass,
				writable: true,
				configurable: true
			}
		});
		Object.defineProperty(subClass, 'prototype', {
			writable: false
		});
		if (superClass) Object.setPrototypeOf(subClass, superClass);
	}

	function _createSuper(Derived): Callable {
		var hasNativeReflectConstruct = _isNativeReflectConstruct();
		return function _createSuperInternal() {
			var Super = Object.getPrototypeOf(Derived),
				result;
			if (hasNativeReflectConstruct) {

				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore
				var NewTarget = Object.getPrototypeOf(this).constructor;
				result = Reflect.construct(Super, arguments, NewTarget);
			} else {
				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore
				result = Super.apply(this, arguments);
			}
			return result;
		};
	}

	function newClass<T>(superclass): T {
		var init = function (this: ZKObject & {____?}): object {

			let ____ = this.____;

			this._$super.____ = true;
			// call super constructor refer to babel
			let _this = _super.call(this) as ZKObject;

			// Note: we cannot use Object.assign() here, because some prototype property may not be copied.
			_zk.copy(_this, Object.getPrototypeOf(this));


			if (____ === undefined) {
				// eslint-disable-next-line no-console
			// 	this.$oid = ++_oid;
				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore
				this.$init.apply(_this, arguments);
			//
				var ais = _this._$ais;
				if (ais) {
					this._$ais = null;
					for (var j = ais.length; j--;)
						ais[j].call(_this);
				}
			}
			//
			delete this._$super.____;
			return _this as object;
		};

		_inherits(init, superclass);
		var _super = _createSuper(init);
		_super['class'] = superclass;
		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-ignore
		return init;
	}
	function regClass<S extends typeof ZKObject>(jclass: S): S {
		var oid = jclass.$oid = ++_oid;
		_zk.classes[oid] = jclass;
		return jclass;
	}

	function defGet(nm: string): Getter {
		return new Function('return this.' + nm + ';');
	}
	function defSet00(nm: string): GeneratedSetter {
		return function (v) {
			this[nm] = v;
			return this;
		};
	}
	function defSet01(nm: string, after: Setter): GeneratedSetter {
		return function (v, opts) {
			var o = this[nm];
			this[nm] = v;
			if (o !== v || (opts && opts.force)) {
				this['__fname__'] = nm.substring(1);
				after.apply(this, arguments);
				delete this['__fname__'];
			}
			return this;
		};
	}
	function defSet10(nm: string, before: Setter): GeneratedSetter {
		return function (/*v, opts*/) {
			this['__fname__'] = nm.substring(1);
			this[nm] = before.apply(this, arguments);
			delete this['__fname__'];
			return this;
		};
	}
	function defSet11(nm: string, before: Setter, after: Setter): GeneratedSetter {
		return function (v, opts) {
			var o = this[nm];
			this['__fname__'] = nm.substring(1);
			this[nm] = v = before.apply(this, arguments);
			if (o !== v || (opts && opts.force))
				after.apply(this, arguments);
			delete this['__fname__'];
			return this;
		};
	}

	function showprgbInit(): void {
		//don't use jq() since it will be queued after others
		if (jq.isReady || _zk.Page.contained.length)
			_showprgb(true, _zk.pi ? 'z-initing' : null);
		else
			setTimeout(showprgbInit, 10);
	}
	function showprgb(): void { //When passed to FF's setTimeout, 1st argument is not null
		_showprgb(_zk.processMask);
	}
	function _showprgb(mask: boolean | undefined, icon?: string | null): void {
		let $jq;
		if (_zk.processing
		&& !($jq = jq('#zk_proc')).length && !jq('#zk_showBusy').length) {
			zUtl.progressbox('zk_proc', window.msgzk ? msgzk.PLEASE_WAIT : 'Processing...', mask, icon);
		} else if (icon == 'z-initing') {
			$jq = $jq || jq('#zk_proc');
			if ($jq.length && $jq.hasClass('z-loading') && ($jq = $jq.parent()).hasClass('z-temp')) {
				$jq.append('<div class="z-initing"></div>');
			}
		}
	}
	function wgt2s(w: Widget): string {
		var s = w.widgetName;
		return s + (w.id ? '$' + w.id : '') + '#' + w.uuid + '$' + w.$oid;
	}
	async function toLogMsg(ars: Array<Element | Widget> | IArguments, detailed): Promise<string> {
		let {Widget} = await import('@zk/widget'); // avoid circular dependence
		var msg: string[] = [];
		for (var j = 0, len = ars.length; j < len; j++) {
			if (msg.length) msg.push(', ');
			var ar = ars[j];
			if (ar && (jq.isArray(ar) || ar.zk)) //ar.zk: jq(xx)
				msg.push('[' + toLogMsg(ar, detailed) + ']');
			else if (Widget && Widget.isInstance(ar))
				msg.push(wgt2s(ar));
			else if (ar && ar.nodeType) {
				var w = Widget && Widget.$(ar);
				if (w) msg.push(jq.nodeName(ar), (ar != w.$n() ? '#' + ar.id + '.' + ar.className : ''), ':', wgt2s(w));
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
				var str = '' + ar,
					m = str.indexOf('{'),
					k = m < 0 ? str.indexOf('\n') : -1;
				msg.push(str.substring(0, m > 0 ? m : k > 0 ? k : str.length));
			} else
				msg.push('' + ar);
		}
		return msg.join('');
	}
	function doLog(): void {
		if (_logmsg) {
			var console = document.getElementById('zk_log') as HTMLTextAreaElement;
			if (!console) {
				jq(document.body).append(
	'<div id="zk_logbox" class="z-log">'
	+ '<button class="z-button" onclick="jq(\'#zk_logbox\').remove()">X</button><br/>'
	+ '<textarea id="zk_log" rows="10"></textarea></div>');
				console = document.getElementById('zk_log') as HTMLTextAreaElement;
			}
			console.value += _logmsg;
			console.scrollTop = console.scrollHeight;
			_logmsg = null;
		}
	}

	function _stampout(): void {
		if (_zk.mounting) {
			_zk.afterMount(_stampout);
			return;
		}
		_zk.stamp('ending');
		_zk.stamp();
	}

let _caches = {};

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
Object.assign(_zk, {
	/** A map of all classes, Map<int oid, zk.Class cls>.
	 * @since 5.0.8
	 */
	classes: {},
	/** Returns if the given JS object is a class ({@link zk.Class}).
	 * @param Object cls the object to test whether it is a class (aka., a  ZK class)
	 * @since 5.0.9
	 */
	isClass(cls: unknown): boolean {
		return cls != null && ((cls as object & {prototype}).prototype instanceof ZKObject);
	},
	/** Returns whether the given JS object is a ZK object ({@link zk.Object}).
	 * @param Object o the object to test whether it is a ZK object
	 * @since 5.0.9
	 */
	isObject: function (o: unknown): boolean {
		return o != null && (o as object)['$supers'] != null;
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
	currentFocus: null,
	/** The topmost modal window, or null if no modal window at all.
	 * @type zul.wnd.Window
	 */
	currentModal: null,

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
	mounting: false,
	/** Whether Client Engine is processing something, such as processing
	 * an AU response. This flag is set when {@link #startProcessing}
	 * is called, and cleaned when {@link #endProcessing} is called.
	 * @see #startProcessing
	 * @see #loading
	 * @see #mounting
	 * @type boolean
	 */
	processing: false,
	/** Indicates whether ZK Client Engine has been booted and created the initial widgets.
	 * It is useful to know if it is caused by an asynchronous update (i.e., zk.booted is true).
	 * @see #mounting
	 * @see #unloading
	 * @type boolean
	 */
	booted: false,
	/** Indicates whether the browser is unloading this document.
	 * Note: when the function registered with {@link #beforeUnload} is called, this flag is not set yet.
	 * @see #loading
	 * @see #booted
	 * @type boolean
	 */
	unloading: false,
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

	/** Indicates whether to enable the tablet UI.
	 * @type boolean
	 * @since 9.5.1
	 */
	//tabletUIEnabled: false,
	/** Indicates whether to apply touch mode.
	 * @type boolean
	 * @since 9.5.1
	 */
	//touchEnabled: false,

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
	copy: _zk.copy,

	/** Retrieves and removes the value of the specified name of the given map.
	 * @param Map props a map of properties
	 * @param String nm the name to retrieve the value
	 * @return Object the value.
	 * @since 5.0.2
	 */
	cut(props: Record<string, unknown>, nm: string): object | undefined {
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
	$package(name: string, end?: boolean, wv?: boolean): unknown { //end used only by WpdExtendlet
		for (var j = 0, ref: unknown = window; ;) {
			var k = name.indexOf('.', j),
				nm = k >= 0 ? name.substring(j, k) : name.substring(j),
				nxt = (ref as Record<string, unknown>)[nm], newpkg;
			if (newpkg = !nxt) nxt = (ref as Record<string, unknown>)[nm] = {};
			if (k < 0) {
				if (newpkg && end !== false) _zk.setLoaded(name);
					//if $package(x, false) was called, zk.setLoaded won't be called
					//i.e., zk.setLoaded has to be called explicitly
				if (wv) (nxt as Record<string, unknown>).$wv = true; //the wv (weeve) package is available
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
	$import(name: string, fn?: Callable): unknown {
		var last;
		if (last = _caches[name]) {
			if (fn) fn(last);
			return last;
		}
		for (var j = 0, ref: unknown = window; ;) {
			var k = name.indexOf('.', j),
				nm = k >= 0 ? name.substring(j, k) : name.substring(j),
				nxt = (ref as Record<string, unknown>)[nm];
			if (k < 0 || !nxt) {
				if (fn) {
					if (nxt) fn(nxt);
					else
						_zk.load(name.substring(0, name.lastIndexOf('.')),
							function () {fn(_zk.$import(name));});
				}
				_caches[name] = nxt;
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
	$extends<S extends typeof ZKObject, D, D2>(superclass: S,
		 members: D & ThisType<D>, staticMembers?: D2): S {
		if (!superclass)
			throw 'unknown superclass';

		var superpt = superclass.prototype,
			jclass = newClass<typeof ZKObject>(superclass),
			thispt = jclass.prototype,
			define = members['$define'];

		if (define)	{
			delete members['$define'];
			_zk.define(jclass, define);
		}

		// copy static members
		// Object.setPrototypeOf(jclass, superclass);

		// simulate Class extends.
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		// var _init = function (this: any): void { this.constructor = jclass; };
		// 	_init.prototype = superclass.prototype;
		// 	jclass.prototype = new _init();
		// 	var thispt = jclass.prototype;


		Object.assign(jclass, staticMembers);
		Object.assign(thispt, members);

		thispt._$super = superpt;
		return regClass<S>(jclass as S);
	},
	/** Provides the default values for the specified options.


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
	$default<T>(opts: T, defaults: T): T {
		// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
		opts = opts || ({} as T);
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
	override<T extends Partial<ZKObject>>(dst: T, backup: Record<string, unknown>, src: ThisType<T>): T {
		switch (typeof backup) {
		case 'function':
			var old = dst;
			dst = backup;
			return old;
		case 'string':
			dst['$' + backup] = dst[backup];
			dst[backup as string] = src;
			return dst;
		}
		for (var nm in src) {
			backup[nm] = dst[nm];
			dst[nm] = src[nm];
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
				before = after.length ? after[0] : null;
				after = after.length > 1 ? after[1] : null;
			}
			pt['set' + nm2] = before ?
				after ? defSet11(nm1, before, after) : defSet10(nm1, before) :
				after ? defSet01(nm1, after) : defSet00(nm1);
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
		return v && !isNaN(v = parseInt(v, b || 10)) ? v : 0;
	},
	/** Parses a string to a floating number.
	 * <p>It is the same as the built-in parseFloat method except it never return
	 * NaN (rather, it returns 0).
	 * @param String v the text to parse
	 * @return double the floating number
	 * @since 5.0.2
	 */
	parseFloat: function (v) {
		return v && !isNaN(v = parseFloat(v)) ? v : 0;
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
			_zk._set(o, name, value, extra);
		} else //o: dst, name: src, value: props
			for (var j = 0, len = value.length, m, n; j < len;) {
				n = value[j++];
				m = name['get' + n.charAt(0).toUpperCase() + n.substring(1)];
				if (!extra || m || name[n] !== undefined) //extra: ignoreUndefined in this case
					_zk._set(o, n, m ? m.call(name) : name[n]);
			}
		return o;
	},
	_set: function (o, name, value, extra) { //called by widget.js (better performance)
		_zk._set2(o,
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
	startProcessing: function (timeout, pid /* internal use only */) {
		_zk.processing = true;
		var t = setTimeout(jq.isReady ? showprgb : showprgbInit, timeout > 0 ? timeout : 0);
		if (pid) {
			_procq[pid] = t;
		}
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
	endProcessing: function (pid /* internal use only */) {
		//F70-ZK-2495: delete init crash timer once endProcessing is called
		let crashTimer = window['zkInitCrashTimer'];
		if (crashTimer) {
			clearTimeout(crashTimer);
			window['zkInitCrashTimer'] = false;
		}
		_zk.processing = false;
		if (pid) {
			var t = _procq[pid];
			if (t) {
				clearTimeout(t);
			}
			delete _procq[pid];
		}
		zUtl.destroyProgressbox('zk_proc');
	},

	/** Disable the default behavior of ESC. In other words, after called, the user cannot abort the loading from the server.
	 * <p>To enable ESC, you have to invoke {@link #enableESC} and the number
	 * of invocations shall be the same.
	 * @see #enableESC
	 */
	disableESC: function () {
		++_zk._noESC;
	},
	/** Enables the default behavior of ESC (i.e., stop loading from the server).
	 * @see #disableESC
	 */
	enableESC: function () {
		--_zk._noESC;
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
		zAu.send(new _zk.Event(null, 'error', {message: msg}, {ignorable: true}), 800);
		_zk._Erbx.push(msg);
	},
	//DEBUG//
	/** If in debug-js, use console log to display an log message to indicate an debug log.
	 *  Example:
<pre><code>zk.debugLog('Oops! Something wrong:(');</code></pre>
	 * @param String msg the warning message
	 * @since 8.5.0
	 */
	debugLog: function (msg) {
		if (_zk.debugJS) console.log(msg); // eslint-disable-line no-console
	},
	/** Closes all error messages shown by {@link #error}.
   	 * Example:
<pre><code>zk.errorDismiss();</code></pre>
	 * @see #error
	 */
	errorDismiss: function () {
		_zk._Erbx.remove();
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
		toLogMsg(
			(detailed !== _zk) ? arguments :
				[].slice.call(arguments, 1)
			, (detailed === _zk)
		).then(msg => (_logmsg ? _logmsg + msg : msg) + '\n');
		setTimeout(function () {jq(doLog);}, 300);
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
				_zk.log(inf.n + ': ' + (inf.t - _t0));
				_t0 = inf.t;
			}
			_zk.log('total: ' + (_t0 - t0));
		}
	},

	/** Encodes and returns the URI to communicate with the server.
	 * Example:
<pre><code>document.createElement("script").src = zk.ajaxURI('/web/js/com/foo/mine.js',{au:true});</code></pre>
	 * @param String uri - the URI related to the AU engine. If null, the base URI is returned.
	 * @param Map opts [optional] the options. Allowed values:<br/>
	 * <ul>
	 * <li>au - whether to generate an URI for accessing the ZK update engine. If not specified, it is used to generate an URL to access any servlet</li>
	 * <li>resource - whether to generate an URI for accessing the ZK resource engine. If not specified, it is used to generate an URL to access any servlet</li>
	 * <li>desktop - the desktop or its ID. If null, the first desktop is used.</li>
	 * <li>ignoreSession - whether to handle the session ID in the base URI.</li>
	 * </ul>
	 * @return String the encoded URI
	 */
	ajaxURI: function (uri, opts) {
		var ctx = _zk.Desktop.$(opts ? opts.desktop : null),
			au = opts && opts.au,
			res = opts && opts.resource,
			uriPrefix,
			base = ctx || _zk;

		if (au) {
			uriPrefix = base.updateURI;
		} else if (res) {
			uriPrefix = base.resourceURI;
		} else
			uriPrefix = base.contextURI;

		uri = uri || '';

		var abs = uri.charAt(0) == '/';
		if (au && !abs) {
			abs = true;
			if (uri)
				uri = '/' + uri; //non-au supports relative path
		}

		var j = uriPrefix.indexOf(';'), //ZK-1668: may have multiple semicolon in the URL
			k = uriPrefix.lastIndexOf('?');
		if (j < 0 && k < 0) return abs ? uriPrefix + uri : uri;

		if (k >= 0 && (j < 0 || k < j)) j = k;
		var prefix = abs ? uriPrefix.substring(0, j) : '';

		if (opts && opts.ignoreSession)
			return prefix + uri;

		var suffix = uriPrefix.substring(j),
			l = uri.indexOf('?');
		return l >= 0 ?
			k >= 0 ?
			prefix + uri.substring(0, l) + suffix + '&' + uri.substring(l + 1) :
			prefix + uri.substring(0, l) + suffix + uri.substring(l) :
			prefix + uri + suffix;
	},
	/**
	 * Encodes and returns the resource URI from Class-Web Resources (CWR).
	 * Example: <pre><code>var res = zk.resourceURI('/js/my/path/res.css');</code></pre>
	 *
	 * @param String uri the resource URI.
	 * @param String version [optional] the version string to build the cache-friendly URI. If none is set, use zk.build by default.
	 * @param Map opts [optional] the options. Allowed values:<br/>
	 * <ul>
	 * <li>au - whether to generate an URI for accessing the ZK update engine. If not specified, it is used to generate an URL to access any servlet</li>
	 * <li>desktop - the desktop or its ID. If null, the first desktop is used.</li>
	 * <li>ignoreSession - whether to handle the session ID in the base URI.</li>
	 * </ul>
	 * @return String the encoded resource URI
	 * @since 9.0.0
	 */
	ajaxResourceURI: function (uri, version, opts) {
		if (uri.charAt(0) != '/')
			uri = '/' + uri;
		opts = opts || {};
		opts['resource'] = true;
		version = version || _zk.build;
		if (version) uri = '/web/_zv' + version + uri;
		else uri = '/web' + uri;
		return _zk.ajaxURI(uri, opts);
	},
	/** Declares the desktop is used for the stateless context.
	 * By stateless we mean the server doesn't maintain any widget at all.
	 * @param String dtid the ID of the desktop to create
	 * @param String contextURI the context URI, such as /zkdemo
	 * @param String updateURI the update URI, such as /zkdemo/zkau
	 * @param String reqURI the URI of the request path.
	 * @return Desktop the stateless desktop being created
	 */
	stateless: function (dtid, contextURI, updateURI, resourceURI, reqURI) {
		var Desktop = _zk.Desktop, dt;
		dtid = dtid || ('z_auto' + _statelesscnt++);
		dt = Desktop.all[dtid];
		if (dt && !dt.stateless) throw 'Desktop conflict';
		if (_zk.updateURI == null)
			_zk.updateURI = updateURI;
		if (_zk.resourceURI == null)
			_zk.resourceURI = resourceURI;
		if (_zk.contextURI == null) //it might be ""
			_zk.contextURI = contextURI;
		return dt || new Desktop(dtid, contextURI, updateURI, reqURI, true);
	},
	/**
	 * Adds data attribute handler
	 * @param String name the attribute name
	 * @param String script the JS content
	 * @since 8.0
	 */
	addDataHandler: function (name, script) {
		if (!_zk.dataHandlers)
			_zk.dataHandlers = {};
		_zk.dataHandlers[name] = script;
	},
	/**
	 * Test whether the name of the data attribute exists.
	 * @return boolean ture if existing.
	 * @since 8.0
	 */
	hasDataHandler: function (name) {
		return _zk.dataHandlers && !!_zk.dataHandlers[name];
	},
	/**
	 * Returns the dataHandler from the given name
	 * @return Object with a run method to run
	 * @since 8.0
	 */
	getDataHandler: function (name) {
		if (_zk.hasDataHandler(name)) {
			return {run: function (wgt, dataValue) {
				var fun = _zk.dataHandlers![name];
				if (typeof fun !== 'function')
					fun = jq.evalJSON(fun) as DataHandler;
				try {
					dataValue = jq.parseJSON(dataValue);
				} catch (e) {
					_zk.debugLog(e.message || e);
				}
				var dataHandlerService,
					w = wgt;
				for (; w; w = w.parent) {
					if (w['$ZKBINDER$']) {
						if (!w._$binder) w._$binder = new zkbind.Binder(w, this);
						dataHandlerService = w._$binder;
						break;
					} else if (w['$ZKAUS$']) {
						if (!w._$service) w._$service = new _zk.Service(w, this);
						dataHandlerService = w._$service;
						break;
					}
				}
				if (w && dataHandlerService) {
					jq.extend(this, dataHandlerService);
					var oldCommand = this['command'],
						subName = name.indexOf('data-') == 0 ? name.substring(5) : name;
					this['command'] = function () {
						oldCommand.call(this, subName + arguments[0], arguments[1]);
					};
					var oldAfter = this['after'];
					this['after'] = function () {
						oldAfter.call(this, subName + arguments[0], arguments[1]);
					};
				}
				fun.call(this, wgt, dataValue);
			}};
		}
		_zk.error('not found: ' + name);
	},
	/** Intercepts a widget, when specific method has been called, the interceptor would be called first.
	 * <p>Example,
<pre><code>
zk.$intercepts(zul.inp.Combobox, {
	open: function () {
		var context = this.$getInterceptorContext$();
		console.log('open');
		context.stop = false;
	}
});
</code></pre>
	 * <p>In the interceptor function, you could call this.$getInterceptorContext$() to get the context object.
	 * The context object has several properties, it would help you the deal with the interceptor:
	 * <ul>
	 * <li>context.stop - whether to call the original method in the widget.</li>
	 * <li>context.result - the return value of the widget function.</li>
	 * <li>context.args - the original arguments in the function of widget, you could update it for calling the original method.</li>
	 * </ul>
	 * @param Class targetClass the destination object to override
	 * @param Object interceptor the interceptor map corresponds to the widget methods
	 * @since 8.0.3
	 */
	$intercepts: function (targetClass, interceptor) {
		if (!targetClass)
			throw 'unknown targetClass';
		if (!interceptor)
			throw 'unknown interceptor';

		if (targetClass && targetClass.$copied === false) {
			var f = targetClass.$copyf;
			targetClass.$copyf = function () {
				f();
				targetClass.$copied = true;
				_zk.$intercepts(targetClass, interceptor);
			};
			return;
		}

		var targetpt = targetClass.prototype;
		for (var nm in interceptor) {
			if (targetpt[nm]) {
				//init interceptor function
				if (!targetpt['$getInterceptorContext$']) {
					targetpt['_$$interceptorContext'] = [];
					targetpt['$getInterceptorContext$'] = function () {
						return this._$$interceptorContext[this._$$interceptorContext.length - 1];
					};
				}
				(function (nm, oldFunc) {
					targetpt[nm] = function () {
						var context = {stop: false, result: null, args: arguments},
							arr = this._$$interceptorContext;
						arr.push(context);
						interceptor[nm].apply(this, arguments);
						var result = context.stop ? context.result : oldFunc.apply(this, context.args);
						arr.splice(arr.indexOf(context), 1);
						return result;
					};
				})(nm, targetpt[nm]);
			}
		}
	}
});

//zk.agent//
(function () {
	function _ver(ver: string): number | string {
		return parseFloat(ver) || ver;
	}

	// jQuery 1.9 remove the jQuery.browser
	jq.uaMatch = function (ua) {
		ua = ua.toLowerCase();

		var match = /(chrome)[ /]([\w.]+)/.exec(ua)
			|| /(webkit)[ /]([\w.]+)/.exec(ua)
			|| /(opera)(?:.*version|)[ /]([\w.]+)/.exec(ua)
			|| /(msie) ([\w.]+)/.exec(ua)
			|| ua.indexOf('compatible') < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua)
			|| [];

		return {
			browser: match[ 1 ] || '',
			version: match[ 2 ] || '0'
		};
	};

	// Don't clobber any existing jq.browser in case it's different
	if (!jq.browser) {
		var matched = jq.uaMatch(navigator.userAgent);
		browser = {version: ''};

		if (matched.browser) {
			browser[ matched.browser ] = true;
			browser.version = matched.version;
		}

		// Chrome is Webkit, but Webkit is also Safari.
		if (browser.chrome) {
			browser.webkit = true;
		} else if (browser.webkit) {
			browser.safari = true;
		}

		jq.browser = browser;
	}
	var browser = jq.browser,
		agent = _zk.agent = navigator.userAgent.toLowerCase(),
		// ZK-4451: iOS 13 safari ipad pretend itself as MacOS
		isTouchableMacIntel = navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 0,
		iosver;
	_zk.opera = browser.opera && _ver(browser.version);
	_zk.ff = _zk.gecko = browser.mozilla
		&& (agent.indexOf('trident') < 0) && _ver(browser.version);
	_zk.linux = agent.indexOf('linux') >= 0;
	_zk.mac = !_zk.ios && agent.indexOf('mac') >= 0;
	_zk.webkit = browser.webkit;
	_zk.chrome = browser.chrome;
	_zk.safari = browser.webkit && !_zk.chrome; // safari only

	// support W$'s Edge
	_zk.edge_legacy = _zk.webkit && _zk.chrome && ((iosver = agent.indexOf('edge')) >= 0 && _ver(agent.substring(iosver + 5)));
	_zk.edge = _zk.webkit && _zk.chrome && ((iosver = agent.indexOf('edg/')) >= 0 && agent.substring(iosver + 4));

	_zk.ios = (_zk.webkit && /iphone|ipad|ipod/.test(agent) && (
		//ZK-2245: add version info to zk.ios
		(iosver = agent.match(/version\/\d/)) && iosver[0].replace('version/', '')
		|| // ZK-2888, in iphone with chrome, it may not have version attribute.
		(iosver = agent.match(/ os \d/)) && iosver[0].replace(' os ', '')))
		|| // ZK-4451: iOS 13 safari ipad pretend itself as MacOS
		(_zk.safari && isTouchableMacIntel);

	_zk.ipad = (_zk.webkit && /ipad/.test(agent) && (
		//ZK-2245: add version info to zk.ios
		(iosver = agent.match(/version\/\d/)) && iosver[0].replace('version/', '')
		|| // ZK-2888, in iphone with chrome, it may not have version attribute.
		(iosver = agent.match(/ os \d/)) && iosver[0].replace(' os ', '')))
		|| // ZK-4451: iOS 13 safari ipad pretend itself as MacOS
		(_zk.safari && isTouchableMacIntel);

	_zk.android = agent.indexOf('android') >= 0;
	_zk.mobile = _zk.ios || _zk.android;
	_zk.css3 = true;
	var ie11 = browser.mozilla && (agent.indexOf('trident') >= 0) && _ver(browser.version);

	_zk.vendor = _zk.webkit ? 'webkit' : '';

	var bodycls = '';
	if (_zk.ff) {
		bodycls = 'gecko gecko' + Math.floor(Number(_zk.ff));
		_zk.vendor = 'Moz';
	} else if (_zk.opera) { //no longer to worry 10.5 or earlier
		bodycls = 'opera';
		_zk.vendor = 'O';
	} else {
		_zk.iex = browser.msie && _ver(browser.version); //browser version
			//zk.iex is the Browser Mode (aka., Compatibility View)
			//while zk.ie is the Document Mode
		if (!_zk.iex && ie11)
			_zk.iex = ie11;

		if (_zk.iex) {
			_zk.ie = document['documentMode'] || _zk.iex;
			if (_zk.ie) {
				// zk.ien: the version n or later but less than 11
				if (_zk.ie < 11 && _zk.ie >= 9) {
					_zk.ie9 = _zk.ie >= 9;
					_zk.ie10 = _zk.ie >= 10;
				}
				_zk['ie' + _zk.ie + '_'] = true;
				_zk.css3 = _zk.ie >= 9;
				bodycls = 'ie ie' + Math.floor(_zk.ie);
			}
			_zk.vendor = 'ms';
		} else if (_zk.edge || _zk.edge_legacy) {
			bodycls = 'edge';
		} else {
			if (_zk.chrome) {
				bodycls = 'webkit chrome';
			} else if (_zk.safari) {
				bodycls = 'webkit safari';
			} else if (_zk.webkit) {
				bodycls = 'webkit';
			}
		}
	}
	if (_zk.mobile) {
		bodycls += ' mobile';
		if (_zk.ios)
			bodycls += ' ios';
		else
			bodycls += ' android';
	}
	if ((_zk.air = agent.indexOf('adobeair') >= 0) && _zk.webkit)
		bodycls += ' air';

	if (bodycls)
		jq(function () {
			jq(document.body).addClass(bodycls);
		});

	_zk.vendor_ = _zk.vendor.toLowerCase();
})();

if (!_zk.ie && !_zk.edge_legacy) {
	/** @class zk.Buffer
	 * A string concatenation implementation to speed up the rendering performance
	 * in the modern browsers, except IE or MS's Edge. The implementation is to
	 * cheat the mold js of the ZK widgets' implementation that it assumed the
	 * argument is an array type in the early ZK version.
	 * <p>Note: if the default implementation breaks the backward compatibility,
	 * please use the following script to overwrite the implementation as the same
	 * as the early ZK version. For example,
<pre><code>
zk.Buffer = Array;
</code></pre>
	 * </p>
	 * @since 8.0.0
	 */
	// eslint-disable-next-line @typescript-eslint/ban-ts-comment
	// @ts-ignore
	_zk.Buffer = function () {
		this['out'] = '';
	};

	_zk.Buffer.prototype = new Array;
	Object.assign(_zk.Buffer.prototype, {
		push: function () {
			for (var i = 0, j = arguments.length; i < j; i++)
				if (arguments[i] != null || arguments[i] != undefined)
					this['out'] += arguments[i];
		},
		join: function (str) {
			if (str)
				throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';
			return this['out'];
		},
		shift: _zkf = function () {throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';},
		unshift: _zkf,
		pop: _zkf,
		slice: _zkf,
		sort: _zkf
	});
} else {
	// eslint-disable-next-line @typescript-eslint/ban-ts-comment
	// @ts-ignore
	_zk.Buffer = Array;
}
//zk.Object//
function getProxy(o, f) { //used by zk.Object
	return function () {
			return f.apply(o, arguments);
		};
}

/** @class zk.Object
 * The root of the class hierarchy.
 * @see zk.Class
 */
export class ZKObject {
	declare public _$ais: Callable[] | null;
	declare public _$supers: Record<string, unknown>;
	declare public _$proxies: WeakMap<object, unknown>;
	declare public _$super;
	declare public prototype;
	declare public _importantEvts: {[key: string]: unknown};

	declare public static $oid;

	public constructor() {
		this.$oid = ++_oid;
	}

	/** The constructor.
	 * <p>Default: it does nothing so the subclass needs not to copy back
	 * (also harmless to call back).
	 * @see #afterInit
	 */
	protected $init(value?: number | string): void {
	}
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
	protected afterInit(func: Callable): void {
		(this._$ais = this._$ais || []).unshift(func); //reverse
	}
	/** The class that this object belongs to.
	 * @type zk.Class
	 */
	public get $class(): typeof ZKObject {
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		return this.constructor as any;
	}

	/**
	 * Returns the class of the subsclass which extends from zk.Class.
	 * @type zk.Class
	 * @since 10.0.0
	 */
	public get$Class<T extends typeof ZKObject>(): T {
		return this.$class as T;
	}

	/** The object ID. Each object has its own unique $oid.
	 * It is mainly used for debugging purpose.
	 * <p>Trick: you can test if a JavaScript object is a ZK object by examining this property, such as
	 * <code>if (o.$oid) alert('o is a ZK object');</code>
	 * <p>Notice: zk.Class extends from zk.Object (so a class also has $oid)
	 * @type int
	 */
	public $oid = 0;
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
	public $instanceof<T extends typeof ZKObject>(...klass: T[]): boolean {
		for (let k of klass) {
			if (this instanceof k) {
				return true;
			}
		}
		return false;
	}
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
	protected $super(mtd: string, ...args: unknown[]): unknown;
	protected $super(klass: ZKObject | string, mtd: string, ...args: unknown[]): unknown {
		if (typeof klass != 'string') {
			return this.$supers(klass, mtd, args);
		}
		return this.$supers(klass, [mtd, ...args]);
	}
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
	public $supers(nm: ZKObject | string, args: string | unknown[], argx?: unknown[]): unknown {
		var supers = this._$supers;
		if (!supers) supers = this._$supers = {};

		if (typeof nm != 'string') { //zk.Class assumed
			let method;
			var old = supers[args as string], p; //args is method's name
			if (!(p = nm.prototype._$super) || !(method = p[args as string])) //nm is zk.Class
				throw args + ' not in superclass'; //args is the method name

			supers[args as string] = p;
			try {
				return method.apply(this, argx);
			} finally {
				supers[args as string] = old; //restore
			}
		}

		//locate method
		var old = supers[nm], m, p, oldmtd;
		if (old) {
			oldmtd = (old as object)[nm];
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
	}

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
	// ref: https://github.com/Microsoft/TypeScript/pull/27028
	public proxy<A extends unknown[], R>(func: (...args: A) => R): (...args: A) => R;
	public proxy<A0, A extends unknown[], R>(func: (arg0: A0, ...args: A) => R): (arg0: A0, ...args: A) => R;
	public proxy<A0, A1, A extends unknown[], R>(func: (arg0: A0, arg1: A1, ...args: A) => R): (arg0: A0, arg1: A1, ...args: A) => R;
	public proxy<A0, A1, A2, A extends unknown[], R>(func: (arg0: A0, arg1: A1, arg2: A2, ...args: A) => R): (arg0: A0, arg1: A1, arg2: A2, ...args: A) => R;
	public proxy<A0, A1, A2, A3, A extends unknown[], R>(func: (arg0: A0, arg1: A1, arg2: A2, arg3: A3, ...args: A) => R): (arg0: A0, arg1: A1, arg2: A2, arg3: A3, ...args: A) => R;
	public proxy<AX, R>(func: (...args: AX[]) => R): (...args: AX[]) => R {
		var fps = this._$proxies, fp;
		if (!fps) this._$proxies = fps = new WeakMap();
		else if (fp = fps.get(func)) return fp;
		var fn = getProxy(this, func);
		fps.set(func, fn);
		return fn;
	}
	/** Determines if the specified Object is assignment-compatible with this Class. This method is equivalent to [[zk.Object#$instanceof].
	 * Example:
<pre><code>
if (klass.isInstance(obj)) {
}
</code></pre>
	 * @param Object o the object to check
	 * @return boolean true if the object is an instance
	 */
	public static isInstance(o: unknown): boolean {
		return o instanceof ZKObject && o.$instanceof(this);
	}
	/** Determines if the class by this Class object is either the same as, or is a superclass of, the class represented by the specified Class parameter.
	 * Example:
<pre><code>
if (klass1.isAssignableFrom(klass2)) {
}
</code></pre>
	 * @param zk.Class cls the Class object to be checked, such as zk.Widget.
	 * @return boolean true if assignable
	 */
	public static isAssignableFrom(cls: typeof ZKObject): boolean {
		return cls && (cls.prototype instanceof this || cls === this);
	}
}
_zk.Object = ZKObject;
export class ZKClass extends ZKObject {
	declare public superclass;
} //regClass() requires zk.Class
_zk.Class = ZKClass;

//error box//
var _erbx, _errcnt = 0;

_zk._Erbx = class _Erbx extends ZKObject { //used in HTML tags
	public id: string;
	public dg: Draggable | undefined;
	public constructor(msg: string) {
		super();
		var id = 'zk_err',
			$id = '#' + id,
			click = _zk.mobile ? ' ontouchstart' : ' onclick',
			// Use zUtl.encodeXML -- Bug 1463668: security
			html = '<div class="z-error" id="' + id + '">'
			+ '<div id="' + id + '-p">'
			+ '<div class="errornumbers">' + (++_errcnt) + ' Errors</div>'
			+ '<div class="button"' + click + '="zk._Erbx.remove()">'
			+ '<i class="z-icon-times"></i></div>'
			+ '<div class="button"' + click + '="zk._Erbx.redraw()">'
			+ '<i class="z-icon-refresh"></i></div></div>'
			+ '<div class="messagecontent"><div class="messages">'
			+ zUtl.encodeXML(msg, {multiline: true}) + '</div></div></div>';

		jq(document.body).append(html);
		_erbx = this;
		this.id = id;
		try {
			var n;
			this.dg = new _zk.Draggable(null, n = jq($id)[0], {
				handle: jq($id + '-p')[0], zIndex: n.style.zIndex,
				starteffect: _zk.$void,
				endeffect: _zk.$void});
		} catch (e) {
			_zk.debugLog(e.message || e);
		}
		jq($id).slideDown(1000);
	}
	public destroy(): void {
		_erbx = null;
		_errcnt = 0;
		if (this.dg) this.dg.destroy();
		jq('#' + this.id).remove();
	}
	public static redraw(): void {
		_zk.errorDismiss();
		zAu.send(new _zk.Event(null, 'redraw'));
	}
	public static push(msg: string): void {
		if (!_erbx)
			return new _zk._Erbx(msg);

		var id = _erbx.id;
		jq('#' + id + ' .errornumbers')
			.html(++_errcnt + ' Errors');
		jq('#' + id + ' .messages')
			.append('<div class="newmessage">' + zUtl.encodeXML(msg) + '</hr></div>');
		jq('#' + id + ' .newmessage')
			.removeClass('newmessage').addClass('message').slideDown(600);
	}
	public static remove(): void {
		if (_erbx) _erbx.destroy();
	}
};

export default _zk;

export type $void = typeof _zk.$void;