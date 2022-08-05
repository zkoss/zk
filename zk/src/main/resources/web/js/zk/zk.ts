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
declare global {
	interface Window {
		zkInitCrashTimer: number | undefined;
	}
}
export type DataHandler = (wgt: zk.Widget, val: unknown) => void;

let _zkf;

function _zk(sel?: string | Node | JQuery | JQuery.Event | zk.Event | zk.Widget | null): zjq { // eslint-disable-line zk/noNull
	return jq(sel, _zk).zk;
}

export interface AjaxURIOptions {
	desktop: zk.Desktop;
	au: boolean;
	resource: boolean;
	ignoreSession: boolean;
}
// Note: we cannot use Object.assign() here, because some prototype property may not be copied.
_zk.copy = function<T, U> (dst: T, src: U, backup?: Record<string, unknown>): T & U {
	dst ||= {} as T;
	for (var p in src) {
		if (backup) backup[p as string] = dst[p as string];
		dst[p as string] = src[p];
	}
	return dst as T & U;
};

type Getter = Function; // eslint-disable-line @typescript-eslint/ban-types
type Setter = Function; // eslint-disable-line @typescript-eslint/ban-types
type GeneratedSetter = (this: zk.Widget, v: unknown, opts: Partial<{ force: boolean }>) => zk.Widget;

var _oid = 0,
	_statelesscnt = 0,
	_logmsg,
	_stamps: {n: string; t: number}[] = [],
	_t0 = jq.now(),
	_procq: Record<number, number | undefined> = {}; // Bug ZK-3053

function _isNativeReflectConstruct(): boolean {
	if (typeof Reflect === 'undefined' || !Reflect.construct)
		return false;
	if (Reflect.construct['sham'])
		return false;
	if (typeof Proxy === 'function')
		return true;
	try {
		Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function (): void { return; }));
		return true;
	} catch (e) {
		return false;
	}
}
// eslint-disable-next-line zk/noNull
function _inherits(subClass: NewableFunction, superClass: NewableFunction | null): void {
	// eslint-disable-next-line zk/noNull
	if (typeof superClass !== 'function' && superClass !== null) {
		throw new TypeError('Super expression must either be null or a function');
	}
	subClass.prototype = Object.create(superClass && superClass.prototype as never, {
		constructor: {
			value: subClass,
			writable: true,
			configurable: true
		}
	}) as never;
	Object.defineProperty(subClass, 'prototype', {
		writable: false
	});
	if (superClass) Object.setPrototypeOf(subClass, superClass);
}

function _createSuper(Derived): CallableFunction {
	const hasNativeReflectConstruct = _isNativeReflectConstruct();
	return function _createSuperInternal(): NewableFunction {
		var Super = Object.getPrototypeOf(Derived) as CallableFunction,
			result: NewableFunction;
		if (hasNativeReflectConstruct) {

			// eslint-disable-next-line @typescript-eslint/ban-ts-comment
			// @ts-ignore
			var NewTarget = (Object.getPrototypeOf(this as never) as object).constructor as never;
			result = Reflect.construct(Super, arguments, NewTarget) as never;
		} else {
			// eslint-disable-next-line @typescript-eslint/ban-ts-comment
			// @ts-ignore
			result = Super.bind(this as object)(...arguments) as NewableFunction;
		}
		return result;
	};
}
// the "___s" is the sequence number of the "this" instance to check its "_$super" order
var ___s = 1;
function newClass<T>(superclass: { $oid?: number }): T {
	var init = function (this: ZKObject & {___s?: number ; ____?: number}): object {
		// For B95-ZK-4320.zul, the ___s is always differed by 1, so we use
		// "superclass.$oid" to distinguish whether is the same inherited class or not.
		// For example,
		// "->" means extension, "=>" means creation
		// case 1: A3 -> A2 -> A1 -> A
		//       : only A3 can invoke $init()
		// case 2: B1's $init() => (C2 and D3)
		//       : B1, C2, and D3 can invoke $init() if any.
		this.___s = superclass.$oid ?? ___s;
		let ____ = this.____;

		// eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
		this._$super.____ = superclass.$oid ? (superclass.$oid + 1) : ___s++;
		// call super constructor refer to babel
		let _this = _super.bind(this)() as ZKObject;

		// Note: we cannot use Object.assign() here, because some prototype property may not be copied.
		_zk.copy(_this, Object.getPrototypeOf(this));

		// if not differed by 1, it could be another instance with the same zk.$extends() widget.
		// for example in B50-ZK-441.zul
		if (____ === undefined || ____ - 1 < this.___s) {

			// call afterCreated_() for ES6 class here
			this.afterCreated_.call(_this, ...(arguments as never as []));

			this.$init.call(_this, ...(arguments as never as []));

			var ais = _this._$ais;
			if (ais) {
				this._$ais = undefined;
				for (var j = ais.length; j--;)
					ais[j].bind(_this)();
			}
		}

		// eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
		delete this._$super.____;
		delete this.___s;

		// reset if it's greater than 100,000.
		___s %= 100000;

		return _this as object;
	};

	_inherits(init as never, superclass as never);
	var _super = _createSuper(init);
	_super['class'] = superclass;
	return init as unknown as T;
}
function regClass<S extends typeof ZKObject>(jclass: S): S {
	var oid = jclass.$oid = ++_oid;
	_zk.classes[oid] = jclass;
	return jclass;
}
_zk.regClass = regClass;

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
		var o = this[nm] as never;
		this[nm] = v;
		if (o !== v || (opts && opts.force)) {
			this['__fname__'] = nm.substring(1);
			after.call(this, ...(arguments as never as []));
			delete this['__fname__'];
		}
		return this;
	};
}
function defSet10(nm: string, before: Setter): GeneratedSetter {
	return function (/*v, opts*/) {
		this['__fname__'] = nm.substring(1);
		this[nm] = before.call(this, ...(arguments as never as [])) as never;
		delete this['__fname__'];
		return this;
	};
}
function defSet11(nm: string, before: Setter, after: Setter): GeneratedSetter {
	return function (v, opts) {
		var o = this[nm] as never;
		this['__fname__'] = nm.substring(1);
		this[nm] = v = before.call(this, ...(arguments as never as [])) as never;
		if (o !== v || (opts && opts.force))
			after.call(this, ...(arguments as never as []));
		delete this['__fname__'];
		return this;
	};
}

function showprgbInit(): void {
	//don't use jq() since it will be queued after others
	if (jq.isReady || zk.Page.contained.length)
		_showprgb(true, _zk.pi ? 'z-initing' : undefined);
	else
		setTimeout(showprgbInit, 10);
}
function showprgb(): void { //When passed to FF's setTimeout, 1st argument is not null
	_showprgb(_zk.processMask);
}
function _showprgb(mask?: boolean, icon?: string): void {
	let $jq: undefined | JQuery;
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
function wgt2s(w: zk.Widget): string {
	var s = w.widgetName;
	return s + (w.id ? '$' + w.id : '') + '#' + w.uuid + '$' + w.$oid;
}
function toLogMsg(ars: Array<Element | zk.Widget> | JQuery | IArguments, detailed): string {
	var msg: string[] = [];
	for (var j = 0, len = ars.length; j < len; j++) {
		if (msg.length) msg.push(', ');
		var ar = ars[j] as undefined | Array<Element | zk.Widget> | JQuery | Node;
		if (ar && (Array.isArray(ar) || (ar as JQuery).zk)) //ar.zk: jq(xx)
			msg.push('[' + toLogMsg(ar as Exclude<typeof ar, Node | undefined>, detailed) + ']');
		else if (ar instanceof zk.Widget)
			msg.push(wgt2s(ar));
		else if (ar && ar instanceof Node) {
			var w = zk.Widget.$(ar);
			if (w) msg.push(jq.nodeName(ar), (ar != w.$n() ? '#' + (ar as HTMLElement).id + '.' + (ar as HTMLElement).className : ''), ':', wgt2s(w));
			else msg.push(jq.nodeName(ar), '#', (ar as HTMLElement).id);
		} else if (detailed && ar && (typeof ar == 'object') && !(ar instanceof Node)) {
			var s = ['{\n'];
			for (var v in ar)
				s.push(v, ':', ar[v] as never, ',\n');
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
		_logmsg = undefined;
	}
}

function _stampout(): void {
	if (_zk.mounting) {
		zk.afterMount(_stampout);
		return;
	}
	_zk.stamp('ending');
	_zk.stamp();
}
interface R<T> {
	[keys: string]: T;
}
type UnknownProps = R<UnknownValues>;
type UnknownValues = unknown | R<UnknownProps>;
let _caches: UnknownProps = {};

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

/** A map of all classes, {@code Map<int oid, zk.Class cls>}.
 * @since 5.0.8
 */
// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
_zk.classes = {} as Record<number, unknown>;
/** Returns if the given JS object is a class ({@link zk.Class}).
 * @param Object cls the object to test whether it is a class (aka., a  ZK class)
 * @since 5.0.9
 */
_zk.isClass = function (cls: unknown): boolean {
	return cls != null && ((cls as NewableFunction).prototype instanceof ZKObject);
};
/** Returns whether the given JS object is a ZK object ({@link zk.Object}).
 * @param Object o the object to test whether it is a ZK object
 * @since 5.0.9
 */
_zk.isObject = function (o: unknown): boolean {
	return o != null && (o as object)['$supers'] != null;
};
/** The delay before showing the processing prompt (unit: milliseconds).
 * <p>Default: 900 (depending on the server's configuration)
 * @type int
 */
_zk.procDelay = 900;
/** The delay before showing a tooltip (unit: milliseconds).
 * Default: 800 (depending on the server's configuration)
 * @type int
 */
_zk.tipDelay = 800;
/** The timeout for re-sending AU request (unit: milliseconds).
 * Default: 200 (depending on the server's configuration)
 * @since 6.5.2
 * @type int
 */
_zk.resendTimeout = 200;
/** The last position that the mouse was clicked (including left and right clicks).
 * @type zk.Offset
 */
_zk.clickPointer = [0, 0] as zk.Offset;
/** The position of the mouse (including mouse move and click).
 * @type zk.Offset
 */
_zk.currentPointer = [0, 0] as zk.Offset;
/** The widget that gains the focus now, or null if no one gains focus now.
 * @type Widget
 */
_zk.currentFocus = undefined as zk.Widget | undefined;
/** The topmost modal window, or null if no modal window at all.
 * @type zul.wnd.Window
 */
_zk.currentModal = undefined as zul.wnd.Window | undefined;
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
_zk.loading = 0;
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
_zk.mounting = false;
/** Whether Client Engine is processing something, such as processing
 * an AU response. This flag is set when {@link #startProcessing}
 * is called, and cleaned when {@link #endProcessing} is called.
 * @see #startProcessing
 * @see #loading
 * @see #mounting
 * @type boolean
 */
_zk.processing = false;
/** Indicates whether ZK Client Engine has been booted and created the initial widgets.
 * It is useful to know if it is caused by an asynchronous update (i.e., zk.booted is true).
 * @see #mounting
 * @see #unloading
 * @type boolean
 */
_zk.booted = false;
/** Indicates whether the browser is unloading this document.
 * Note: when the function registered with {@link #beforeUnload} is called, this flag is not set yet.
 * @see #loading
 * @see #booted
 * @type boolean
 */
_zk.unloading = false;
/** Indicates if the application is busy.
 * It is actually a number that is increased when {@link zk.AuCmd0#showBusy(String)} is called,
 * and decreased when {@link zk.AuCmd0#clearBusy()} is called.
 * In other words, it is set by the application, and used to
 * indicate the application (rather than ZK) is busy.
 * @type int
 * @since 5.0.1
 */
_zk.busy = 0;
/** The application's name, which will be initialized as server-side's
 * <code>WebApp.getAppName()</code>.
 * It will be used as title of {@link jq#alert}.
 * @type String
 * @since 5.0.6
 */
_zk.appName = 'ZK';

declare namespace _zk {
	/** The version of ZK, such as '5.0.0'
	 * @type String
	 */
	//version: '',
	export let version: string;
	/** The build of ZK, such as '08113021'
	 * @type String
	 */
	//build: '',
	export let build: string;

	/** The user agent of the browser.
	 * @type String
	 */
	//agent: '',
	export let agent: string;
	/** Returns the DOM API's version
	 * if the browser is Internet Explorer, or null if not.
	 * <p>Note: it is DOM API's version, not the browser version.
	 * In other words, it depends on if the browser is running in a compatible mode.
	 * FOr the browser's version, please use {@link #iex} instead.
	 * @type Double
	 */
	//ie: null,
	export let ie: number | undefined;
	/** Returns the browser's version as double (only the first two part of the version, such as 8)
	 * if the browser is Internet Explorer, or null if not.
	 * <p>Notice that this is the browser's version, which might not be the
	 * version of DOM API. For DOM API's version, please use {@link #ie} instead.
	 * @type Double
	 * @since 5.0.7
	 */
	//iex: null,
	export let iex: number | string | false | undefined;
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
	export let air: boolean;
	/** Whether it supports CSS3.
	 * @type Boolean
	 */
	//css3: false,

	/** The character used for decimal sign.
	 * @type char
	 */
	//DECIMAL: '',
	export let DECIMAL: string;
	/** The character used for thousands separator.
	 * @type char
	 */
	//GROUPING: '',
	export let GROUPING: string;
	/** The character used to represent minus sign.
	 * @type char
	 */
	//MINUS: '',
	export let MINUS: string;
	/** The character used for mille percent sign.
	 * @type char
	 */
	//PER_MILL: '',
	export let PER_MILL: string;
	/** The character used for percent sign.
	 * @type char
	 */
	//PERCENT: '',
	export let PERCENT: string;
	/** Indicates whether an OS-level modal dialog is opened.
	 * In this case, onblur will be called so a widget can use it to
	 * decide whether to validate.
	 * @type boolean
	 */
	//alerting: false,
	export let alerting: boolean;
	/** Indicates whether {@link Widget#id} is always the same
	 * as {@link Widget#uuid}.
	 * <p>By default, it is false.
	 * <p>You could enable it if the pure-client approach is taken,
	 * since it is more convenient. However, it also means the concept of
	 * ID space is no longer valid.
	 * @type boolean
	 */
	//spaceless: null,
	export let spaceless: boolean;
	/** Indicates whether to enable the tablet UI.
	 * @type boolean
	 * @since 9.5.1
	 */
	//tabletUIEnabled: false,
	export let tabletUIEnabled: boolean;
	/** Indicates whether to apply touch mode.
	 * @type boolean
	 * @since 9.5.1
	 */
	//touchEnabled: false,
	export let touchEnabled: boolean;
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
	export let keyCapture: zk.Widget | undefined;
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
	export let mouseCapture: zk.Widget | undefined;
}

/** Copies a map of properties (or options) from one object to another.
 * Example: extending Array
<pre><code>
zk.copy(Array.prototype, {
 $addAll: function (o) {
  return this.push(...o);
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
// _zk.copy = _zk.copy;

/** Retrieves and removes the value of the specified name of the given map.
 * @param Map props a map of properties
 * @param String nm the name to retrieve the value
 * @return Object the value.
 * @since 5.0.2
 */
_zk.cut = function <T> (props: Record<string, T>, nm: string): T | undefined {
	var v: T | undefined;
	if (props) {
		v = props[nm];
		delete props[nm];
	}
	return v;
};

/** Defines a package. It creates and returns the package if not defined yet.
 * If the package is already defined, it does nothing but returns the package.
 * It is similar to Java's package statement except it returns the package
 * object.
 * <p>Notice the package is usually defined automatically by use of
 * <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Widget_Package_Descriptor">WPD</a>,
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
_zk.$package = function (name: string, end?: boolean, wv?: boolean): unknown { //end used only by WpdExtendlet
	for (var j = 0, ref: Record<string, unknown> | Window = window; ;) {
		var k = name.indexOf('.', j),
			nm = k >= 0 ? name.substring(j, k) : name.substring(j),
			nxt = ref[nm] as undefined | Record<string, unknown>, newpkg;
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
};
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
_zk.$import = function (name: string, fn?: CallableFunction): unknown {
	var last;
	if (last = _caches[name]) {
		if (fn) fn(last);
		return last;
	}
	for (var j = 0, ref: Window | UnknownProps = window; ;) {
		var k = name.indexOf('.', j),
			nm = k >= 0 ? name.substring(j, k) : name.substring(j),
			nxt = ref[nm] as UnknownProps;
		if (k < 0 || !nxt) {
			if (fn) {
				if (nxt) fn(nxt);
				else
					zk.load(name.substring(0, name.lastIndexOf('.')),
						function () {fn(_zk.$import(name));});
			}
			_caches[name] = nxt;
			return nxt;
		}
		ref = nxt;
		j = k + 1;
	}
};

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
_zk.$extends = function<S extends typeof ZKObject, D, D2> (superclass: S,
		members: D & ThisType<D>, staticMembers?: D2): S {
	if (!superclass)
		throw 'unknown superclass';

	var superpt = superclass.prototype,
		jclass = newClass<typeof ZKObject>(superclass),
		thispt = jclass.prototype,
		define = members['$define'] as never;

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
};

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
_zk.$default = function<T, U> (opts: T, defaults: U): T & U {
	// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
	opts = opts || ({} as T);
	for (var p in defaults)
		if (opts[p as string] === undefined)
			opts[p as string] = defaults[p];
	return opts as T & U;
};

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
function override<T, U>(dst: T, backup: Record<string, unknown>, src: U & ThisType<T & U>): T & U;
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
function override<T extends () => void>(oldfunc: T, newfunc: () => void): T;
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
function override<T>(dst: T, nm: string, val: () => void): T;
function override<T extends () => void, U> (dst: T, backup: Record<string, unknown> | (() => void) | string, src?: U): T {
	switch (typeof backup) {
	case 'function':
		var old = dst;
		dst = backup as T;
		return old;
	case 'string':
		dst['$' + backup] = dst[backup] as never;
		dst[backup] = src;
		return dst;
	}
	for (var nm in src) {
		backup[nm] = dst[nm as string];
		dst[nm as string] = src[nm];
	}
	return dst;
}
_zk.override = override;

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
_zk.define = function (klass: NewableFunction, props: Record<string, CallableFunction[] | CallableFunction>) {
	for (var nm in props) {
		var nm1 = '_' + nm,
			nm2 = nm.charAt(0).toUpperCase() + nm.substring(1),
			pt = klass.prototype as object,
			after: CallableFunction[] | CallableFunction | undefined = props[nm], before: CallableFunction | undefined = undefined;
		if (Array.isArray(after)) {
			before = after.length ? after[0] : undefined;
			after = after.length > 1 ? after[1] : undefined;
		}
		pt['set' + nm2] = before ?
			after ? defSet11(nm1, before, after) : defSet10(nm1, before) :
			after ? defSet01(nm1, after) : defSet00(nm1);
		pt['get' + nm2] = pt['is' + nm2] = defGet(nm1);
	}
	return klass;
};

/** A does-nothing-but-returns-false function.
 */
_zk.$void = function (): false {return false;};

/** Parses a string to an integer.
 * <p>It is the same as the built-in parseInt method except it never return
 * NaN (rather, it returns 0).
 * @param String v the text to parse
 * @param int b represent the base of the number in the string. 10 is assumed if omitted.
 * @return int the integer
 */
_zk.parseInt = function (v: string | number | boolean | undefined, b?: number): number {
	return v && !isNaN(v = parseInt(v as string, b || 10)) ? v : 0;
};
/** Parses a string to a floating number.
 * <p>It is the same as the built-in parseFloat method except it never return
 * NaN (rather, it returns 0).
 * @param String v the text to parse
 * @return double the floating number
 * @since 5.0.2
 */
_zk.parseFloat = function (v: string | number): number {
	return v && !isNaN(v = parseFloat(v as string)) ? v : 0;
};

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
function set<T>(o: T, name: string, value: unknown, extra: Record<string, unknown>): T;
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
function set<T extends zk.Object, U>(dst: T, src: U, props: string[], ignoreUndefined: boolean): T;
function set<T extends zk.Object, U>(o: T, name: string | U, value: unknown[] | unknown, extra): T {
	if (typeof name == 'string') {
		_zk._set(o, name, value, extra);
	} else if (Array.isArray(value)) {//o: dst, name: src, value: props
		for (var j = 0, len = value.length, m: undefined | CallableFunction, n: string; j < len;) {
			n = value[j++] as string;
			m = name['get' + n.charAt(0).toUpperCase() + n.substring(1)] as undefined | CallableFunction;
			if (!extra || m || name[n] !== undefined) //extra: ignoreUndefined in this case
				_zk._set(o, n, m ? m.bind(name)() : name[n]);
		}
	}
	return o;
}
_zk.set = set;
_zk._set = function (o: zk.Object, name: string, value, extra?): void { //called by widget.js (better performance)
	_zk._set2(o,
		o['set' + name.charAt(0).toUpperCase() + name.substring(1)] as never,
		name, value, extra);
};
_zk._set2 = function (o: zk.Object, mtd: CallableFunction | undefined, name: string | undefined, value, extra?): void { //called by widget.js (better performance)
	if (mtd) {
		if (extra !== undefined)
			mtd.bind(o)(value, extra);
		else
			mtd.bind(o)(value);
	} else
		o[name as string] = value as never;
};
/** Retrieves a value from the specified property.
 * <p>For example, <code>zk.get(obj, "x")</code>:<br/>
 * If getX or isX is defined in obj, obj.isX() or obj.getX() is returned.
 * If not defined, obj.x is returned.
 * <p>Another example:
<pre><code>
zk.get(o, 'value');
</code></pre>
	* @param Object o the object to retrieve value from
	* @param String name the name
	* @return Object the value of the property
	*/
_zk.get = function (o: zk.Object, name: string): unknown {
	var nm = name.charAt(0).toUpperCase() + name.substring(1),
		m = o['get' + nm] as undefined | CallableFunction;
	if (m) return m.bind(o)();
	m = o['is' + nm] as undefined | CallableFunction;
	if (m) return m.bind(o)();
	return o[name];
};

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
_zk.startProcessing = function (timeout: number, pid?: number /* internal use only */): void {
	_zk.processing = true;
	var t = setTimeout(jq.isReady ? showprgb : showprgbInit, timeout > 0 ? timeout : 0);
	if (pid) {
		_procq[pid] = t;
	}
};
/** Clears a flag, {@link #processing}, to indicate that it the processing has done. It also removes a message, if any, that indicates "processing".
 * <p>Example:
<pre><code>
zk.startProcessing(1000);
//do the lengthy operation
zk.endProcessing();
</code></pre>
	* @see #startProcessing
	*/
_zk.endProcessing = function (pid?: number | string /* internal use only */): void {
	//F70-ZK-2495: delete init crash timer once endProcessing is called
	let crashTimer = window.zkInitCrashTimer;
	if (crashTimer) {
		clearTimeout(crashTimer);
		window.zkInitCrashTimer = undefined;
	}
	_zk.processing = false;
	if (pid) {
		var t = _procq[pid] as never;
		if (t) {
			clearTimeout(t);
		}
		delete _procq[pid];
	}
	zUtl.destroyProgressbox('zk_proc');
};

/** Disable the default behavior of ESC. In other words, after called, the user cannot abort the loading from the server.
 * <p>To enable ESC, you have to invoke {@link #enableESC} and the number
 * of invocations shall be the same.
 * @see #enableESC
 */
_zk.disableESC = function (): void {
	++_zk._noESC;
};
/** Enables the default behavior of ESC (i.e., stop loading from the server).
 * @see #disableESC
 */
_zk.enableESC = function (): void {
	--_zk._noESC;
};
_zk._noESC = 0; //# of disableESC being called (also used by mount.js)

//DEBUG//
/** Display an error message to indicate an error.
 *  Example:
<pre><code>zk.error('Oops! Something wrong:(');</code></pre>
	* @param String msg the error message
	 * @param boolean silent only show error box
	* @see #errorDismiss
	* @see #log
	* @see #stamp(String, boolean)
	*/
_zk.error = function (msg: string, silent?: boolean): void {
	if (!silent) {
		zAu.send(new zk.Event(zk.Desktop._dt!, 'error', {message: msg}, {ignorable: true}), 800);
	}
	_zk._Erbx.push(msg);
};
//DEBUG//
/** If in debug-js, use console log to display an log message to indicate an debug log.
 *  Example:
<pre><code>zk.debugLog('Oops! Something wrong:(');</code></pre>
	* @param String msg the warning message
	* @since 8.5.0
	*/
_zk.debugLog = function (msg: string): void {
	if (_zk.debugJS) console.log(msg); // eslint-disable-line no-console
};
/** Closes all error messages shown by {@link #error}.
 * Example:
<pre><code>zk.errorDismiss();</code></pre>
	* @see #error
	*/
_zk.errorDismiss = function (): void {
	_zk._Erbx.remove();
};
/** Logs an message for debugging purpose.
 * Example:
<pre><code>
zk.log('reach here');
zk.log('value is", value);
</code></pre>
	* @param Object... detailed varient number of arguments to log
	* @see #stamp(String, boolean)
	*/
_zk.log = function (detailed): void {
	const msg = toLogMsg(
		(detailed !== _zk) ? arguments :
			[].slice.call(arguments, 1)
		, (detailed === _zk)
	);
	_logmsg = (_logmsg ? _logmsg + msg : msg) + '\n';
	setTimeout(function () {jq(doLog);}, 300);
};
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
_zk.stamp = function (nm?: string, noAutoLog?: boolean): void {
	if (nm) {
		if (!noAutoLog && !_stamps.length)
			setTimeout(_stampout, 0);
		_stamps.push({n: nm, t: Date.now()});
	} else if (_stamps.length) {
		var t0 = _t0;
		for (var inf: undefined | {n: string ; t: number}; (inf = _stamps.shift());) {
			_zk.log(inf.n + ': ' + (inf.t - _t0));
			_t0 = inf.t;
		}
		_zk.log('total: ' + (_t0 - t0));
	}
};

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
_zk.ajaxURI = function (uri: string | undefined, opts?: Partial<AjaxURIOptions>): string {
	var ctx = zk.Desktop.$(opts ? opts.desktop : undefined),
		au = opts && opts.au,
		res = opts && opts.resource,
		uriPrefix: string | undefined,
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

	uriPrefix = uriPrefix!;

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
};
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
_zk.ajaxResourceURI = function (uri: string, version?: string, opts?: Partial<AjaxURIOptions>): string {
	if (uri.charAt(0) != '/')
		uri = '/' + uri;
	opts = opts ?? {} as AjaxURIOptions;
	opts['resource'] = true;
	version = version || _zk.build;
	if (version) uri = '/web/_zv' + version + uri;
	else uri = '/web' + uri;
	return _zk.ajaxURI(uri, opts);
};
/** Declares the desktop is used for the stateless context.
 * By stateless we mean the server doesn't maintain any widget at all.
 * @param String dtid the ID of the desktop to create
 * @param String contextURI the context URI, such as /zkdemo
 * @param String updateURI the update URI, such as /zkdemo/zkau
 * @param String reqURI the URI of the request path.
 * @return Desktop the stateless desktop being created
 */
_zk.stateless = function (dtid?: string, contextURI?: string, updateURI?: string, resourceURI?: string, reqURI?: string) {
	var Desktop = zk.Desktop, dt: undefined | zk.Desktop;
	dtid = dtid || ('z_auto' + _statelesscnt++);
	dt = Desktop.all[dtid];
	if (dt && !dt.stateless) throw 'Desktop conflict';
	if (_zk.updateURI == null)
		_zk.updateURI = updateURI;
	if (_zk.resourceURI == null)
		_zk.resourceURI = resourceURI;
	if (_zk.contextURI == null) //it might be ""
		_zk.contextURI = contextURI;
	return dt || new Desktop(dtid, contextURI, updateURI, reqURI, true as unknown as string);
};
/**
 * Adds data attribute handler
 * @param String name the attribute name
 * @param String script the JS content
 * @since 8.0
 */
_zk.addDataHandler = function (name: string, script: string): void {
	if (!_zk.dataHandlers)
		_zk.dataHandlers = {};
	_zk.dataHandlers[name] = script;
};
/**
 * Test whether the name of the data attribute exists.
 * @return boolean ture if existing.
 * @since 8.0
 */
_zk.hasDataHandler = function (name: string): boolean {
	return !!(_zk.dataHandlers && _zk.dataHandlers[name]);
};
/**
 * Returns the dataHandler from the given name
 * @return Object with a run method to run
 * @since 8.0
 */
_zk.getDataHandler = function (name: string) {
	if (_zk.hasDataHandler(name)) {
		return {run: function (wgt: zk.Widget, dataValue: string | unknown) {
			var fun = _zk.dataHandlers![name];
			if (typeof fun !== 'function')
				fun = jq.evalJSON(fun) as DataHandler;
			try {
				dataValue = JSON.parse(dataValue as never);
			} catch (e) {
				_zk.debugLog((e as Error).message ?? e);
			}
			var dataHandlerService: undefined | zk.Service | zkbind.Binder,
				w: zk.Widget | undefined = wgt;
			for (; w; w = w.parent) {
				if (w['$ZKBINDER$']) {
					if (!w._$binder) w._$binder = new zkbind.Binder(w, this);
					dataHandlerService = w._$binder;
					break;
				} else if (w['$ZKAUS$']) {
					if (!w._$service) w._$service = new zk.Service(w, this as unknown as zk.Widget);
					dataHandlerService = w._$service;
					break;
				}
			}
			if (w && dataHandlerService) {
				jq.extend(this, dataHandlerService);
				var oldCommand = this['command'] as CallableFunction,
					subName = name.indexOf('data-') == 0 ? name.substring(5) : name;
				this['command'] = function () {
					oldCommand.bind(this)(subName + arguments[0], arguments[1]);
				};
				var oldAfter = this['after'] as CallableFunction;
				this['after'] = function () {
					oldAfter.bind(this)(subName + arguments[0], arguments[1]);
				};
			}
			fun.call(this, wgt, dataValue);
		}};
	}
	_zk.error('not found: ' + name);
};
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
_zk.$intercepts = function (targetClass: typeof zk.Object, interceptor: Record<string, CallableFunction> | object): void {
	if (!targetClass)
		throw 'unknown targetClass';
	if (!interceptor)
		throw 'unknown interceptor';

	var targetpt = targetClass.prototype;
	for (var nm in interceptor) {
		if (targetpt[nm]) {
			//init interceptor function
			if (!targetpt['$getInterceptorContext$']) {
				targetpt['_$$interceptorContext'] = [];
				targetpt['$getInterceptorContext$'] = function (this: zk.Widget & {_$$interceptorContext: []}) {
					return this._$$interceptorContext[this._$$interceptorContext.length - 1];
				};
			}
			(function (nm: string, oldFunc: CallableFunction) {
				targetpt[nm] = function (this: zk.Widget & {_$$interceptorContext: unknown[]}): unknown {
					var context = {stop: false, result: undefined, args: arguments},
						arr = this._$$interceptorContext;
					arr.push(context);
					(interceptor[nm] as CallableFunction).bind(this)(...(arguments as unknown as []));
					var result = context.stop ? context.result : oldFunc.bind(this)(...(context.args as unknown as [])) as never;
					arr.splice(arr.indexOf(context), 1);
					return result;
				};
			})(nm, targetpt[nm] as CallableFunction);
		}
	}
};


//zk.agent//
function _ver(ver: string): number | string {
	return parseFloat(ver) || ver;
}

// jQuery 1.9 remove the jQuery.browser
jq.uaMatch = function (ua: string) {
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
	const matched = jq.uaMatch(navigator.userAgent),
		browser: zk.BrowserOptions = {version: ''};

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
let browser = jq.browser,
	agent = _zk.agent = navigator.userAgent.toLowerCase(),
	// ZK-4451: iOS 13 safari ipad pretend itself as MacOS
	isTouchableMacIntel = navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 0,
	iosver: undefined | number | RegExpMatchArray | null; // eslint-disable-line zk/noNull
_zk.opera = browser.opera && _ver(browser.version);
_zk.ff = _zk.gecko = browser.mozilla
	&& (agent.indexOf('trident') < 0) && _ver(browser.version);
_zk.linux = agent.indexOf('linux') >= 0;
_zk.webkit = browser.webkit;
_zk.chrome = browser.chrome;
_zk.safari = browser.webkit && !_zk.chrome; // safari only

// support W$'s Edge
_zk.edge_legacy = _zk.webkit && _zk.chrome && ((iosver = agent.indexOf('edge')) >= 0 && _ver(agent.substring(iosver + 5)));
_zk.edge = _zk.webkit && _zk.chrome && ((iosver = agent.indexOf('edg/')) >= 0 && agent.substring(iosver + 4));

_zk.ios = ((_zk.webkit && /iphone|ipad|ipod/.test(agent) && (
	//ZK-2245: add version info to zk.ios
	(iosver = agent.match(/version\/\d/)) && iosver[0].replace('version/', '')
	|| // ZK-2888, in iphone with chrome, it may not have version attribute.
	(iosver = agent.match(/ os \d/)) && iosver[0].replace(' os ', '')))
	|| // ZK-4451: iOS 13 safari ipad pretend itself as MacOS
	(_zk.safari && isTouchableMacIntel)) as string | boolean;
_zk.mac = !_zk.ios && agent.indexOf('mac') >= 0;

_zk.ipad = ((_zk.webkit && /ipad/.test(agent) && (
	//ZK-2245: add version info to zk.ios
	(iosver = agent.match(/version\/\d/)) && iosver[0].replace('version/', '')
	|| // ZK-2888, in iphone with chrome, it may not have version attribute.
	(iosver = agent.match(/ os \d/)) && iosver[0].replace(' os ', '')))
	|| // ZK-4451: iOS 13 safari ipad pretend itself as MacOS
	(_zk.safari && isTouchableMacIntel)) as string | boolean;

_zk.android = agent.indexOf('android') >= 0;
_zk.mobile = _zk.ios || _zk.android;
_zk.css3 = true;
const ie11 = browser.mozilla && (agent.indexOf('trident') >= 0) && _ver(browser.version);

_zk.vendor = _zk.webkit ? 'webkit' : '';

let bodycls = '';
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
		_zk.ie = (document['documentMode'] || _zk.iex) as undefined | number;
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
	class Buffer extends Array<string> {
		out: string;
		constructor() {
			super();
			this.out = '';
		}
		override push(...items: string[]): number {
			for (var i = 0, j = arguments.length; i < j; i++)
			if (arguments[i] != null || arguments[i] != undefined)
			this.out += arguments[i];
			return this.length;
		}
		override join(str): string {
			if (str)
				throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';
			return this.out;
		}
		override shift = _zkf = function () {throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';};
		override unshift = _zkf as never;
		override pop = _zkf as never;
		override slice = _zkf as never;
		override sort = _zkf as never;
	}
	_zk.Buffer = Buffer;
} else {
	_zk.Buffer = Array<string>;
}

/** @class zk.Object
 * The root of the class hierarchy.
 * @see zk.Class
 */
// Originally, making ZKObject abstract is to allow zk.regClass to accept abstract
// classes as parameter, yet it also makes sense that ZKObject is defined abstract
// independent of the use of zk.regClass. There are also "workarounds" for zk.regClass
// to accept abstract classes in the face of a concrete ZKObject, e.g., omit
// `new` property from `typeof ZKObject` then intersect with `{abstract new() => T}`.
export abstract class ZKObject {
	declare _$ais?: CallableFunction[];
	declare _$supers: Record<string, unknown>;
	declare _$proxies: WeakMap<object, unknown>;
	declare _$super;
	declare _importantEvts: {[key: string]: unknown};

	declare static $oid;

	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	constructor(..._rest: any[]/* for override compatibility */) {
		this.$oid = ++_oid;
	}

	/** The constructor.
	 * <p>Default: it does nothing so the subclass needs not to copy back
	 * (also harmless to call back).
	 * @see #afterInit
	 * @deprecated as of 10.0 released. Using ES6 {@code constructor} instead.
	 */
	$init(props?: Record<string, unknown> | typeof zkac): void {
		// empty for subclass to override
	}

	/**
	 * This function is called after the object is initialized and all its member
	 * fields are ready to use.
	 * @param props Map the properties passed by constructor.
	 * @since 10.0
	 */
	afterCreated_(props?: Record<string, unknown> | typeof zkac): void {
		// empty for subclass to override
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
	 * @deprecated as of 10.0 released. Using {@link #afterCreated_()} instead.
	 */
	afterInit(func: CallableFunction): void {
		(this._$ais = this._$ais || []).unshift(func); //reverse
	}
	/** The class that this object belongs to.
	 * @type zk.Class
	 */
	get $class(): typeof ZKObject {
		return this.constructor as never;
	}

	/**
	 * Returns the class of the subsclass which extends from zk.Class.
	 * @type zk.Class
	 * @since 10.0.0
	 */
	get$Class<T extends typeof ZKObject>(): T {
		return this.$class as T;
	}

	/** The object ID. Each object has its own unique $oid.
	 * It is mainly used for debugging purpose.
	 * <p>Trick: you can test if a JavaScript object is a ZK object by examining this property, such as
	 * <code>if (o.$oid) alert('o is a ZK object');</code>
	 * <p>Notice: zk.Class extends from zk.Object (so a class also has $oid)
	 * @type int
	 */
	$oid = 0;
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
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	$instanceof<Ts extends any[]>(...klass: Ts): boolean {
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
	$super(mtd: string, ...args: unknown[]): unknown;
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
	$super(klass: typeof ZKObject, mtd: string, ...args: unknown[]): unknown;
	$super(klass: typeof ZKObject | string, mtd: string, ...args: unknown[]): unknown {
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
	$supers(nm: typeof ZKObject | string, args: string | unknown[], argx?: unknown[]): unknown {
		var supers = this._$supers;
		if (!supers) supers = this._$supers = {};

		if (typeof nm != 'string') { //zk.Class assumed
			let method: CallableFunction, old = supers[args as string], p: object; //args is method's name
			if (!(p = nm.prototype._$super as never || Object.getPrototypeOf(nm.prototype)) || !(method = p[args as string] as never)) //nm is zk.Class
				throw args + ' not in superclass'; //args is the method name

			supers[args as string] = p;
			try {
				return method.bind(this)(...argx as unknown as []);
			} finally {
				supers[args as string] = old; //restore
			}
		}

		//locate method
		var old = supers[nm], m: undefined | CallableFunction, p: object & { _$super?: object }, oldmtd: CallableFunction;
		if (old) {
			oldmtd = (old as object)[nm] as never;
			p = old as never;
		} else {
			oldmtd = this[nm] as never;
			p = this;
		}
		// In the following two cases, we have to examine `Object.getPrototypeOf`.
		// 1. When something `zk.$extends` an ES6 class.
		// 2. When something `zk.override` an ES6 class with a method body containing
		//    `$supers`, as in `zkcml/zkmax/listbox-rod/fireOnRender`.
		// Furthermore, for ZK classes created with `zk.$extends` to work as usual,
		// we need to prioritize `_$supers`.
		while (p = (p._$super as never || Object.getPrototypeOf(p)))
			if (oldmtd != p[nm]) {
				m = p[nm] as never;
				if (m) supers[nm] = p;
				break;
			}

		if (!m)
			throw nm + ' not in superclass';

		try {
			return m.bind(this)(...args as unknown as []);
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
	proxy<A extends unknown[], R>(func: (...args: A) => R): (...args: A) => R;
	proxy<A0, A extends unknown[], R>(func: (arg0: A0, ...args: A) => R): (arg0: A0, ...args: A) => R;
	proxy<A0, A1, A extends unknown[], R>(func: (arg0: A0, arg1: A1, ...args: A) => R): (arg0: A0, arg1: A1, ...args: A) => R;
	proxy<A0, A1, A2, A extends unknown[], R>(func: (arg0: A0, arg1: A1, arg2: A2, ...args: A) => R): (arg0: A0, arg1: A1, arg2: A2, ...args: A) => R;
	proxy<A0, A1, A2, A3, A extends unknown[], R>(func: (arg0: A0, arg1: A1, arg2: A2, arg3: A3, ...args: A) => R): (arg0: A0, arg1: A1, arg2: A2, arg3: A3, ...args: A) => R;
	proxy<AX, R>(func: (...args: AX[]) => R): (...args: AX[]) => R {
		var fps = this._$proxies, fp: R | undefined;
		if (!fps) this._$proxies = fps = new WeakMap();
		else if (fp = fps.get(func) as never) return fp;
		var fn = func.bind(this);
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
	static isInstance<T extends typeof ZKObject>(this: T, o: unknown): o is InstanceType<T> {
		return o instanceof ZKObject && o instanceof this;
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
	static isAssignableFrom(cls: typeof ZKObject): boolean {
		return cls && (cls.prototype instanceof this || cls === this);
	}
}
_zk.Object = ZKObject;
namespace _zk {
	export type Object = ZKObject;
	// This namespace is not "declared". Thus, `Class` will be assigned to `_zk` automatically.
	export class Class extends ZKObject {
		declare superclass;
	} //regClass() requires zk.Class
}

//error box//
var _erbx, _errcnt = 0;

_zk._Erbx = class _Erbx extends ZKObject { //used in HTML tags
	id: string;
	dg?: zk.Draggable;
	constructor(msg: string) {
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
			var n: HTMLElement;
			this.dg = new zk.Draggable(undefined, n = jq($id)[0], {
				handle: jq($id + '-p')[0], zIndex: n.style.zIndex,
				starteffect: _zk.$void,
				endeffect: _zk.$void});
		} catch (e) {
			_zk.debugLog((e as Error).message ?? e);
		}
		jq($id).slideDown(1000);
	}
	destroy(): void {
		_erbx = undefined;
		_errcnt = 0;
		if (this.dg) this.dg.destroy();
		jq('#' + this.id).remove();
	}
	static redraw(): void {
		_zk.errorDismiss();
		zAu.send(new zk.Event(zk.Desktop._dt!, 'redraw'));
	}
	static push(msg: string): _Erbx | void {
		if (!_erbx)
			return new _zk._Erbx(msg);

		var id = (_erbx as _Erbx).id;
		jq('#' + id + ' .errornumbers')
			.html(++_errcnt + ' Errors');
		jq('#' + id + ' .messages')
			.append('<div class="newmessage">' + zUtl.encodeXML(msg) + '</hr></div>');
		jq('#' + id + ' .newmessage')
			.removeClass('newmessage').addClass('message').slideDown(600);
	}
	static remove(): void {
		if (_erbx) (_erbx as _Erbx).destroy();
	}
};

export default _zk;

export type $void = typeof _zk.$void;

declare namespace _zk {
	// ./au
	export let _isReloadingInObsolete: boolean;
	export let timerAlive: boolean;
	export let portlet2Data: Record<string, { namespace: string; resourceURL: string }> | undefined;
	export let ausending: boolean;
	export let xhrWithCredentials: boolean;
	export let isTimeout: boolean;
	export let visibilitychange: boolean;
	export let _focusByClearBusy: boolean;

	// ./mount
	export function beforeUnload(fn: () => string | undefined, opts?: {remove: boolean}): void;
	export let feature: {
		standard?: boolean;
		pe?: boolean;
		ee?: boolean;
	};
	export let clientinfo: Record<string, unknown> | undefined;
	export let pfmeter: boolean;
	export let _crWgtUuids: string[];
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let focusBackFix: boolean | undefined; // Must be optional (have undefined as type) do be deleted.
	export let scriptErrorHandlerEnabled: boolean;
	export let scriptErrorHandlerRegistered: boolean;
	export let scriptErrorHandler: ((evt) => void) | undefined;
	export let rmDesktoping: boolean;
	export let keepDesktop: boolean;
	export let skipBfUnload: boolean;
	export let confirmClose: string | undefined;

	// ./drag
	export let dragging: boolean;
	
	// ./widget
	export let timeout: number;
	export let groupingDenied: boolean;
	export let progPos: string | undefined;
	export let _cfByMD: boolean;
	export let cfrg: [number, number] | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let _avoidRod: boolean | undefined;

	// ./dom
	export let _prevFocus: zk.Widget | undefined;

	// ./effect
	export let useStackup: string | boolean | undefined;

	// zul.grid.Row.prototype.setStyle
	export let _rowTime: number | undefined;

	// zul.Imagemap
	export let IMAGEMAP_DONE_URI: string | undefined;

	// zul/dom
	export let themeName: string | undefined;
}

declare namespace _zk {
	// used in this file and accessed via _zk
	export let pi: number | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let processMask: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let debugJS: boolean | undefined;
	export let updateURI: string | undefined;
	export let resourceURI: string | undefined;
	export let contextURI: string | undefined;
	export let dataHandlers: Record<string, string | DataHandler> | undefined;

	// not used in ./zk
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie6: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie6_: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie7: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie7_: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie8: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie8_: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie8c: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie9: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie9_: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie10: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie10_: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie11: boolean | undefined;
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let ie11_: boolean | undefined;

	// zul/db/datefmt
	export const APM: string[];
	export const DOW_1ST: number;
	export const ERA: string;
	export const FDOW: string[];
	export const FMON: string[];
	export const LAN_TAG: string;
	export const MINDAYS: number;
	export const S2DOW: string[];
	export const S2MON: never;
	export const SDOW: string[];
	export const SMON: string[];
	export const TDYS: number;
	export const YDELTA: number;
}

window.zk = _zk.copy(_zk, window.zk); // setting from Java side