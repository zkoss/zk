/* eslint-disable @typescript-eslint/no-dynamic-delete */
/* eslint-disable @typescript-eslint/dot-notation */
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

function _zk(sel?: string | Node | JQuery | JQuery.Event | zk.Event | zk.Widget | null): zjq { // eslint-disable-line zk/noNull
	return jq(sel, _zk).zk;
}

export interface AjaxURIOptions {
	desktop: zk.Desktop;
	au: boolean;
	resource: boolean;
	ignoreSession: boolean;
}

/** Copies a map of properties (or options) from one object to another
 * and optionally copies the original value to another map.
 * Example: extending Array
 * ```ts
 * zk.copy(Array.prototype, {
 *   $addAll: function (o) {
 *     return this.push(...o);
 *   }
 * });
 * ```
 * Example: copy style and restore back
 * ```ts
 * var backup = {};
 * zk.copy(n.style, {
 * 	 visibility: 'hidden',
 * 	 position: 'absolute',
 * 	 display: 'block'
 * }, backup);
 * try {
 * 	 //do whatever
 * } finally {
 * 	 zk.copy(n.style, backup);
 * }
 * ```
 * <p>Notice that {@link zk.copy} copies the properties directly regardless
 * if the target object has a setter or not. It is fast but if you want
 * to go thru the setter, if any, use {@link set}
 * instead.
 * @param dst - the destination object to copy properties to
 * @param src - the source object to copy properties from
 * @param backup - the map to stor the original value
 * @returns the destination object
 * @since 5.0.3
 */
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
	return function _createSuperInternal(this: object, ...args: unknown[]): NewableFunction {
		var Super = Object.getPrototypeOf(Derived) as typeof _createSuperInternal,
			result: NewableFunction;
		if (hasNativeReflectConstruct) {
			var NewTarget = (Object.getPrototypeOf(this) as object).constructor;
			result = Reflect.construct(Super, args, NewTarget) as NewableFunction;
		} else {
			result = Super.apply(this, args);
		}
		return result;
	};
}
const HasDescendant = Symbol('HasDescendant'), DescendantIndex = 12;
function newClass<T>(superclass: { $oid?: number }): T {
	var init = function (this: ZKObject): object {
		// For B95-ZK-4320.zul,
		// "->" means extension, "=>" means creation
		// case 1: A3 -> A2 -> A1 -> A
		//       : only A3 can invoke $init()
		// case 2: B1's $init() => (C2 and D3)
		//       : B1, C2, and D3 can invoke $init() if any.
		// call super constructor refer to babel
		const hasDescendant = arguments.length <= DescendantIndex + 1 ? arguments[DescendantIndex] === HasDescendant : arguments[DescendantIndex * 2] === HasDescendant,
			args: unknown[] = [...(arguments as unknown as [])];
		if (!hasDescendant) {
			if (arguments.length > DescendantIndex + 1) {
				args[DescendantIndex * 2] = HasDescendant;
			} else {
				args[DescendantIndex] = HasDescendant;
			}
		}
		const _this = _super.bind(this)(...args) as ZKObject;

		// Note: we cannot use Object.assign() here, because some prototype property may not be copied.
		_zk.copy(_this, Object.getPrototypeOf(this));

		// for example in B50-ZK-441.zul
		if (!hasDescendant) {

			// If $init() has invoked, don't need to call it again.
			if (!_this['__$inited']) {
				this.$init.call(_this, ...(args as []));
				// call afterCreated_() for ES6 class here
				this.afterCreated_.call(_this, ...(args as []));
			}

			var ais = _this._$ais;
			if (ais) {
				this._$ais = undefined;
				for (var j = ais.length; j--;)
					ais[j].bind(_this)();
			}
		}

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
	return function (this: zk.Widget): unknown {
		return this[nm];
	};
}
function defSet00(nm: string): GeneratedSetter {
	return function (v) {
		this[nm] = v;
		return this;
	};
}
function defSet01(nm: string, after: Setter): GeneratedSetter {
	return function (v, opts?: Record<string, boolean>) {
		var o = this[nm] as never;
		this[nm] = v;
		if (o !== v || opts?.force) {
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
	return function (v, opts?: Record<string, boolean>) {
		var o = this[nm] as never;
		this['__fname__'] = nm.substring(1);
		this[nm] = v = before.call(this, ...(arguments as never as [])) as never;
		if (o !== v || opts?.force)
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
function toLogMsg(ars: (Element | zk.Widget)[] | JQuery | IArguments, detailed): string {
	var msg: string[] = [];
	for (var j = 0, len = ars.length; j < len; j++) {
		if (msg.length) msg.push(', ');
		var ar = ars[j] as undefined | (Element | zk.Widget)[] | JQuery | Node;
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
			const logBox = document.createElement('div'),
				closeButton = document.createElement('button'),
				lineBreak = document.createElement('br'),
				textArea = document.createElement('textarea');

			logBox.id = 'zk_logbox';
			logBox.className = 'z-log';

			closeButton.className = 'z-button';
			closeButton.textContent = 'X';
			closeButton.addEventListener('click', () => {
				logBox.remove();
			});

			textArea.id = 'zk_log';
			textArea.rows = 10;

			logBox.appendChild(closeButton);
			logBox.appendChild(lineBreak);
			logBox.appendChild(textArea);

			document.body.appendChild(logBox);

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
// eslint-disable-next-line @typescript-eslint/consistent-indexed-object-style
interface R<T> {
	[keys: string]: T;
}
type UnknownProps = R<UnknownValues>;
type UnknownValues = unknown | R<UnknownProps>;
const _caches: UnknownProps = {};

/**
 * @class zk
 * @import zk.Package
 * @import zk.Class
 * @import zk.Desktop
 * @import zk.Widget
 * A collection of ZK core utilities.
 * The utilities are mostly related to the language enhancement we added to JavaScript,
 * such as {@link zk.$extends} and {@link zk.$package}.
 * <p>Refer to {@link jq} for DOM related utilities.
 */

/**
 * A map of all classes, `Map<int oid, zk.Class cls>`.
 * @since 5.0.8
 */
_zk.classes = {} as Record<number, unknown>;
/**
 * @returns if the given JS object is a class ({@link zk.Class}).
 * @param cls - the object to test whether it is a class (aka., a  ZK class)
 * @since 5.0.9
 */
_zk.isClass = function (cls: unknown): boolean {
	return cls != null && ((cls as NewableFunction).prototype instanceof ZKObject);
};
/**
 * @returns whether the given JS object is a ZK object ({@link zk.Object}).
 * @param o - the object to test whether it is a ZK object
 * @since 5.0.9
 */
_zk.isObject = function (o: unknown): boolean {
	return o != null && (o as object)['$supers'] != null;
};
/**
 * The delay before showing the processing prompt (unit: milliseconds).
 * @defaultValue `900` (depending on the server's configuration)
 */
_zk.procDelay = 900;
/**
 * The delay before showing a tooltip (unit: milliseconds).
 * @defaultValue `800` (depending on the server's configuration)
 */
_zk.tipDelay = 800;
/**
 * The timeout for re-sending AU request (unit: milliseconds).
 * @defaultValue `200` (depending on the server's configuration)
 * @since 6.5.2
 */
_zk.resendTimeout = 200;
/** The last position that the mouse was clicked (including left and right clicks). */
_zk.clickPointer = [0, 0] as zk.Offset;
/** The position of the mouse (including mouse move and click). */
_zk.currentPointer = [0, 0] as zk.Offset;
/** The widget that gains the focus now, or null if no one gains focus now. */
_zk.currentFocus = undefined as zk.Widget | undefined;
/** The topmost modal window, or null if no modal window at all. */
_zk.currentModal = undefined as zul.wnd.Window | undefined;
/**
 * The number of widget packages (i.e., JavaScript files) being loaded
 * (and not yet complete).
 * <p>When the JavaScript files of widgets are loading, you shall not create
 * any widget. Rather, you can use {@link afterLoad} to execute the creation
 * and other tasks after all required JavaScript files are loaded.
 * @see {@link load}
 * @see {@link afterLoad}
 * @see {@link processing}
 * @see {@link mounting}
 * @type int
 */
_zk.loading = 0;
/**
 * Whether ZK Client Engine has been mounting the peer widgets.
 * By mounting we mean the creation of the peer widgets under the
 * control of the server. To run after the mounting of the peer widgets,
 * use {@link afterMount}
 * @see {@link afterMount}
 * @see {@link processing}
 * @see {@link loading}
 * @see {@link booted}
 * @type boolean
 */
_zk.mounting = false;
/**
 * Whether Client Engine is processing something, such as processing
 * an AU response. This flag is set when {@link startProcessing}
 * is called, and cleaned when {@link endProcessing} is called.
 * @see {@link startProcessing}
 * @see {@link loading}
 * @see {@link mounting}
 * @type boolean
 */
_zk.processing = false;
/**
 * Indicates whether ZK Client Engine has been booted and created the initial widgets.
 * It is useful to know if it is caused by an asynchronous update (i.e., zk.booted is true).
 * @see {@link mounting}
 * @see {@link unloading}
 * @type boolean
 */
_zk.booted = false;
/**
 * Indicates whether the browser is unloading this document.
 * Note: when the function registered with {@link beforeUnload} is called, this flag is not set yet.
 * @see {@link loading}
 * @see {@link booted}
 * @type boolean
 */
_zk.unloading = false;
/**
 * Indicates if the application is busy.
 * It is actually a number that is increased when {@link zk.AuCmd0.showBusy} is called,
 * and decreased when {@link zk.AuCmd0.clearBusy} is called.
 * In other words, it is set by the application, and used to
 * indicate the application (rather than ZK) is busy.
 * @type int
 * @since 5.0.1
 */
_zk.busy = 0;
/**
 * The application's name, which will be initialized as server-side's
 * `WebApp.getAppName()`.
 * It will be used as title of {@link jq.alert}.
 * @type String
 * @since 5.0.6
 */
_zk.appName = 'ZK';

declare namespace _zk {
	/** The version of ZK, such as '5.0.0' */
	export let version: string;
	/** The build of ZK, such as '08113021' */
	export let build: string;

	/** The user agent of the browser. */
	export let agent: string;
	/**
	 * The version as double (only the first two part of the version, such as 3.5)
	 * if it is Gecko-based browsers,
	 * such as Firefox.
	 */
	export let gecko: string | number | false | undefined;
	/**
	 * The version as double (only the first two part of the version, such as 3.6) if it is Firefox,
	 * such as Firefox. Notice that it is Firefox's version, such as
	 * 3.5, 3.6 and 4.0.
	 * If not a firefox, it is null.
	 * @type Double
	 * @since 5.0.6
	 */
	export let ff: string | number | false | undefined;
	/**
	 * The version as double (only the first two part of the version, such as 533.1) if it is Safari-based, or null if not.
	 */
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let safari: boolean | undefined;
	/**
	 * The version as double (only the first two part of the version, such as 10.1) if it is Opera, or null if not.
	 */
	export let opera: string | number | false | undefined;
	/** Whether it supports CSS3. */
	export let css3: boolean;

	/** The character used for decimal sign. */
	export let DECIMAL: string;
	/** The character used for thousands separator. */
	export let GROUPING: string;
	/** The character used to represent minus sign. */
	export let MINUS: string;
	/** The character used for mille percent sign. */
	export let PER_MILL: string;
	/** The character used for percent sign. */
	export let PERCENT: string;
	/**
	 * Indicates whether an OS-level modal dialog is opened.
	 * In this case, onblur will be called so a widget can use it to
	 * decide whether to validate.
	 */
	export let alerting: boolean;
	/**
	 * Indicates whether {@link Widget#id} is always the same
	 * as {@link Widget#uuid}.
	 * <p>By default, it is false.
	 * <p>You could enable it if the pure-client approach is taken,
	 * since it is more convenient. However, it also means the concept of
	 * ID space is no longer valid.
	 */
	export let spaceless: boolean;
	/**
	 * Indicates whether to enable the tablet UI.
	 * @since 9.5.1
	 */
	export let tabletUIEnabled: boolean;
	/**
	 * Indicates whether to apply touch mode.
	 * @since 9.5.1
	 */
	export let touchEnabled: boolean;
	/** The widget that captures the keystrokes.
	 * Used to specify a widget that shall receive the following the onKeyPress and onKeyUp events, no matter what widget the event occurs on.
	 * ```ts
	 * doKeyDown_: function () {
	 *   zk.keyCapture = this;
	 *   this.$supers('doKeyDown_', arguments);
	 * }
	 * ```
	 * <p>Notice that the key capture is reset automatically after processing onKeyUp_.
	 * @see {@link mouseCapture}
	 */
	export let keyCapture: zk.Widget | undefined;
	/** The widget that captures the mouse events.
	 * Used to specify a widget that shall receive the following the onMouseMove and onMouseUp events, no matter what widget the event occurs on.
	 * ```ts
	 * doMouseDown_: function () {
	 *   zk.mouseCapture = this;
	 *   this.$supers('doMouseDown_', arguments);
	 * }
	 * ```
	 * <p>Notice that the mouse capture is reset automatically after processing onMouseUp_.
	 * @see {@link keyCapture}
	 */
	export let mouseCapture: zk.Widget | undefined;
}

/**
 * Retrieves and removes the value of the specified name of the given map.
 * @param props - a map of properties
 * @param nm - the name to retrieve the value
 * @returns the value.
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

/**
 * Defines a package. It creates and returns the package if not defined yet.
 * If the package is already defined, it does nothing but returns the package.
 * It is similar to Java's package statement except it returns the package
 * object.
 * <p>Notice the package is usually defined automatically by use of
 * <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Widget_Package_Descriptor">WPD</a>,
 * so you're rarely need to use this method.
 *
 * <p>Example:
 * ```ts
 * var foo = zk.$package('com.foo');
 * foo.Cool = zk.$extends(zk.Object);
 * ```
 *
 * @param name - the name of the package.
 * @returns Package
 * @see {@link zk.$import}
 * @see {@link zk.load}
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
/**
 * Imports a class or a package. It returns null if the package or class is not defined yet.
 * It returns null if the package or class is not defined yet and
 * `fn` is null.
 * <p>If an additional function, `fn` is specified,
 * this method assumes `name`
 * is a class and it will load the package of the class first.
 * If not found. Then, invoke the function after the class is loaded.
 * For example, the following creates a Listbox widget after loading
 * the package.
 * <p>Example:
 * ```ts
 * var foo = zk.$import('com.foo');
 * var cool = new foo.Cool();
 * var Cool = zk.$import('com.foo.Cooler');
 * var cooler = new Cooler();
 *
 * zk.$import('zul.sel.Listbox', function (cls) {new cls();});
 * ```
 * @param name - The name of the package or the class.
 * @param fn - The function to call after the class is loaded.
 * @returns a package ({@link Package}) or a class ({@link Class})
 * @see {@link zk.$package}
 * @see {@link zk.load}
 * @see {@link zk.$import}
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
 * ```ts
 * zul.Label = zk.$extends(zk.Widget, {
 *   _value: '',
 *   getValue() {
 *     return this._value;
 *   },
 *   setValue(value) {
 *     this._value = value;
 *   }
 * });
 * ```
 * <h3>$define</h3>
 * To simplify the declaration of the getters and setters, `$define`
 * is introduced. It is shortcut to invoke define. For example,
 * ```ts
 * foo.Widget = zk.$extends(zk.Widget, {
 *    _value: '',
 *    $define: {
 *      name: null,
 *      value: null
 *    },
 *    bind_: function () {
 *      //
 *    }
 * });
 * ```
 * <p>is equivalent to
 * ```ts
 * foo.Widget = zk.$extends(zk.Widget, {
 *   _value: '',
 *   bind_: function () {
 *     //
 *   }
 * });
 * zk.define(foo.Widget, {
 *   name: null,
 *   value: null
 * });
 * ```
 * <p>is equivalent to
 * ```ts
 * foo.Widget = zk.$extends(zk.Widget, {
 *   _value: '',
 *   getName: function () {
 *     return this._name;
 *   },
 *   setName: function (v) {
 *     this._name = v;
 *     return this;
 *   },
 *   getValue: function () {
 *     return this._value;
 *   },
 *   setValue: function (v) {
 *     this._value = v;
 *     return this;
 *   },
 *   bind_: function () {
 *     //
 *   }
 * });
 * ```
 * @param superclass - the super class to extend from
 * @param members - a map of non-static members
 * @param staticMembers - a map of static members. Ignored if omitted.
 * @returns the class being defined
 * @see {@link define}
 * @see {@link override}
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
 * ```ts
 * process: function (opts, defaultOptions) {
 *   opts = zk.$default(opts, defaultOptions);
 * }
 * ```
 * ```ts
 * opts = zk.$default(opts, {timeout: 100, max: true});
 *   //timeout and max are assigned to opts only if no such property found in opts
 * ```
 * @param opts - a map of options to which default options will be added
 * If null, an empty map is created automatically.
 * @param defaults - a map of default options
 * @returns the merged options
 * @see {@link zk.copy}
 */
_zk.$default = function<T, U> (opts: T, defaults: U): T & U {
	opts = opts || ({} as T);
	for (var p in defaults)
		if (opts[p as string] === undefined)
			opts[p as string] = defaults[p];
	return opts as T & U;
};

/**
 * Overrides the properties of a map.
 * It is similar to {@link zk.copy}, except
 * <ol>
 * <li>It preserves the original member (method or data) in the backup
 * argument.</li>
 * <li>It handles the class extensions ({@link zk.$extends}) well.
 * For example, the members of all deriving classes will be overriden too,
 * if necessary.</li>
 * </ol>
 * <p>Example,
 * ```ts
 * var _xCombobox = {}
 * zk.override(zul.inp.Combobox.prototype, _xCombobox, {
 *   redrawpp_: function (out) {
 *   	 if (!_redrawpp(this, out))
 *       _xCombobox.redrawpp_.apply(this, arguments); //call the original method
 *   },
 *   open: function () {
 *     _renderpp(this);
 *     _xCombobox.open.apply(this, arguments); //call the original method
 *   }
 * });
 * ```
 * @param dst - the destination object to override
 * @param backup - the map used to store the original members (or properties)
 * @param src - the source map providing the new members
 * @returns the destination object
 */
function override<T, U>(dst: T, backup: Record<string, unknown>, src: U & Partial<T> & ThisType<T & U>): T & U;
/**
 * Overrides a particular method.
 * The old method will be returned, and the caller could store it for
 * calling back. For example,
 *
 * ```ts
 * var superopen = zk.override(zul.inp.Combobox.prototype.open,
 *  function () {
 *    superopen.apply(this, arguments);
 *    //do whatever you want
 *  });
 * ```
 *
 * @param oldfunc - the old function that will be replaced
 * @param newfunc - the new function that will replace the old function
 * @returns the old function (i.e., oldfunc)
 * @since 5.0.6
 */
function override<T extends () => void>(oldfunc: T, newfunc: () => void): T;
/**
 * Overrides a particular method or data member.
 * The old method will be stored in method called $nm (assuming nm is
 * the method name to override. For example,
 *
 * ```ts
 * zk.override(zul.inp.Combobox.prototype, {},
 *  open() {
 *    this.$open.apply(this, arguments);
 *    //do whatever you want
 *  }
 * );
 * ```
 *
 * @param dst - the destination object to override
 * @param nm - the method name to override
 * @param val - the value of the method. To override a method, it has
 * to be an instance of {@link Function}.
 * @returns the destination object
 * @since 5.0.2
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
	if (typeof dst['$init'] == 'function') {
		const props: string[] = [];
		for (const nm in src) {
			backup[nm] = dst[nm as string];
			dst[nm as string] = src[nm];
			if (backup[nm] == undefined && typeof dst[nm as string] != 'function') {
				props.push(nm);
			}
		}
		if (props.length) {
			const oldInit = dst['$init'] as unknown as CallableFunction;
			dst['$init'] = function (...args: unknown[]) {
				oldInit.bind(this)(...args);
				// try to apply the properties again
				for (const nm of props) {
					this[nm] = src![nm] as never;
				}
			};
		}
	} else {
		for (const nm in src) {
			backup[nm] = dst[nm as string];
			dst[nm as string] = src[nm];
		}
	}
	return dst;
}
_zk.override = override;

/** Replaces existing properties and add new properties.
 * It is similar to {@link zk.override}, except
 * <ol>
 * <li>It preserves the overwritten members (methods or data) in the return
 * value.</li>
 * <li>It handles the class extensions ({@link zk.$extends}) well.
 * For example, the members of all deriving classes will be overriden too,
 * if necessary.</li>
 * <li>Overlapping properties must have identical types.</li>
 * </ol>
 * <p>Example,
 * ```ts
 * var _xCombobox = zk.augment(zul.inp.Combobox.prototype, {
 * redrawpp_: function (out) {
 * 	if (!_redrawpp(this, out))
 * 		_xCombobox.redrawpp_.apply(this, arguments); //call the original method
 * },
 * open: function () {
 * 	_renderpp(this);
 * 	_xCombobox.open.apply(this, arguments); //call the original method
 * }
 * });
 * ```
 * @param dst - the destination object to override
 * @param src - the source map providing the new members
 * @returns the backup object
 *
 * @since 10.0.0
 */
_zk.augment = function<D extends Pick<S, keyof D & keyof S>, S> (dst: D, src: S & Pick<D, keyof D & keyof S> & ThisType<D & S>) {
	const backup = {} as Pick<D, keyof D & keyof S>;
	if (typeof dst['$init'] == 'function') {
		const props: string[] = [];
		for (const nm in src) {
			backup[nm] = dst[nm as keyof D];
			dst[nm] = src[nm as keyof S];
			if (backup[nm] == undefined && typeof dst[nm as keyof D] != 'function') {
				props.push(nm);
			}
		}
		if (props.length) {
			const oldInit = dst['$init'] as unknown as CallableFunction;
			dst['$init'] = function (...args: unknown[]) {
				oldInit.bind(this)(...args);
				// try to apply the properties again
				for (const nm of props) {
					this[nm] = src[nm] as never;
				}
			};
		}
	} else {
		for (const nm in src) {
			backup[nm] = dst[nm as keyof D];
			dst[nm] = src[nm as keyof S];
		}
	}
	return backup;
};

/** Defines the setter and getter methods.
 * <p>Notice that you rarely need to invoke this method directly. Rather, you can specify them in a property named $define, when calling #$extends.
 * <p>For example, the following code snippet
 * ```ts
 * zk.define(zul.wgt.Button, {
 *   disabled: null
 * });
 * ```
 *
 * is equivalent to define three methods, isDisabled, getDisabled and setDisabled, as follows:
 *
 * ```ts
 * zul.wgt.Button = zk.$extends(zk.Widget, {
 *   isDisabled: _zkf = function () {
 *     return this._disabled;
 *   },
 *   isDisabled: _zkf,
 *   setDisabled: function (v) {
 *     this._disabled = v;
 *     return this;
 *   }
 * });
 * ```
 *
 * If you want to do something when the value is changed in the setter, you can specify a function as the value in the props argument. For example,
 *
 * ```ts
 * zk.define(zul.wgt.Button, {
 *   disabled: function () {
 *     if (this.desktop) this.rerender();
 *   }
 * });
 * ```
 *
 * will cause the setter to be equivalent to
 *
 * ```ts
 * setDisabled: function (v, opts) {
 *   this._disabled = v;
 *   if (this._disabled !== v || opts?.force)
 *     if (this.desktop) this.rerender();
 *   return this;
 * }
 * ```
 *
 * If you want to pre-process the value, you can specify a two-element array. The first element is the function to pre-process the value, while the second is to post-process if the value is changed, as described above. For example,
 *
 * ```ts
 * zk.define(zul.wgt.Button, {
 *   disabled: [
 *   	function (v) {
 *   		return v != null && v != false;
 *   	},
 *   	function () {
 * 	    if (this.desktop) this.rerender();
 *   	}
 *   ]
 * });
 * ```
 *
 * will cause the setter to equivalent to
 *
 * ```ts
 * setDisabled: function (v) {
 *   v = v != null && v != false;
 *   this._disabled = v;
 *   if (this._disabled !== v || opts?.force)
 *     if (this.desktop) this.rerender();
 *   return this;
 * }
 * ```
 *
 * Notice that when the function (specified in props) is called, the arguments are the same as the arguments passed to the setter (including additional arguments. For example, if button.setDisabled(true, false) is called, then the specified function is called with two arguments, true and false. In additions, as shown above, you can enforce the invocation of the function by passing a map with force as true.
 *
 * ```ts
 * wgt.setSomething(somevalue, {force:true});
 * ```
 * @param klass - the class to define the members
 * @param props - the map of members (aka., properties)
 * @see {@link zk.$extends}
 * @returns the class being defined
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

/**
 * A does-nothing-but-returns-false function.
 */
_zk.$void = function (): false {return false;};

/**
 * Parses a string to an integer.
 * <p>It is the same as the built-in parseInt method except it never return
 * NaN (rather, it returns 0).
 * @param v - the text to parse
 * @param b - represent the base of the number in the string. 10 is assumed if omitted.
 * @returns the integer
 */
_zk.parseInt = function (v: string | number | boolean | undefined, b?: number): number {
	return v && !isNaN(v = parseInt(v as string, b || 10)) ? v : 0;
};
/** Parses a string to a floating number.
 * <p>It is the same as the built-in parseFloat method except it never return
 * NaN (rather, it returns 0).
 * @param v - the text to parse
 * @returns the floating number
 * @since 5.0.2
 */
_zk.parseFloat = function (v: string | number | boolean | undefined): number {
	return v && !isNaN(v = parseFloat(v as string)) ? v : 0;
};

/**
 * Assigns a value to the specified property.
 * <p>For example, `zk.set(obj, "x", 123)`:<br/>
 * If setX is defined in obj, obj.setX(123) is called.
 * If not defined, obj.x = 123 is called.
 * <p>Anotehr example:
 * ```ts
 * zk.set(o, 'value', true); //set a single property
 * ```
 * @param o - the object to assign values to
 * @param name - the property's name
 * @param value - the property's value
 * @param extra - an extra argument to pass to the setX method
 * as the extra argument (the second argument).
 * For example, `zk.set(obj, 'x', 123, true)` invokes `obj.setX(123, true)`.
 * @see {@link get}
 * @returns the destination object
 */
function set<T>(o: T, name: string, value: unknown, extra: Record<string, unknown>): T;
/**
 * Sets the given properties from one object to another.
 * Example:
 * ```ts
 * zk.set(dst, src, ["foo", "mike"]);
 * ```
 * If dst has a method called setFoo and src has method called getMike,
 * then it is equivalent to
 * ```ts
 * dst.setFoo("foo", src["foo"]);
 * dst["mike"] = src.getMike();
 * ```
 *
 * @param dst - the destination object to copy properties to
 * @param src - the source object to copy properties from
 * @param props - an array of property names (String)
 * @param ignoreUndefined - whether to ignore undefined.
 * Optional (if not specified, false is assumed).
 * If true and src[name] is undefined, then dst[name] won't be assigned.
 * @returns the destination object
 * @since 5.0.3
 * @see {@link zk.copy}
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
/** @internal */
_zk._set = function (o: zk.Object, name: string, value, extra?): void { //called by widget.js (better performance)
	_zk._set2(o,
		o['set' + name.charAt(0).toUpperCase() + name.substring(1)] as never,
		name, value, extra);
};
/** @internal */
_zk._set2 = function (o: zk.Object, mtd: CallableFunction | undefined, name: string | undefined, value, extra?): void { //called by widget.js (better performance)
	if (mtd) {
		if (extra !== undefined)
			mtd.bind(o)(value, extra);
		else
			mtd.bind(o)(value);
	} else
		o[name!] = value as never;
};
/** Retrieves a value from the specified property.
 * <p>For example, `zk.get(obj, "x")`:<br/>
 * If getX or isX is defined in obj, obj.isX() or obj.getX() is returned.
 * If not defined, obj.x is returned.
 * <p>Another example:
 * ```ts
 * zk.get(o, 'value');
 * ```
 * @param o - the object to retrieve value from
 * @param name - the name
 * @returns the value of the property
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
/** Set a flag, {@link processing}, to indicate that it starts a processing. It also shows a message to indicate "processing" after the specified timeout.
 * <p>Example:
 * ```ts
 * zk.startProcessing(1000);
 * //do the lengthy operation
 * zk.endProcessing();
 * ```
 * @param timeout - the delay before showing a message to indicate "processing".
 * @see {@link zk.processing}
 * @see {@link zk.endProcessing}
 */
_zk.startProcessing = function (timeout: number, pid?: number /* internal use only */): void {
	_zk.processing = true;
	var t = setTimeout(jq.isReady ? showprgb : showprgbInit, timeout > 0 ? timeout : 0);
	if (pid) {
		_procq[pid] = t;
	}
};
/** Clears a flag, {@link processing}, to indicate that it the processing has done. It also removes a message, if any, that indicates "processing".
 * <p>Example:
 * ```ts
 * zk.startProcessing(1000);
 * //do the lengthy operation
 * zk.endProcessing();
 * ```
 * @see {@link zk.startProcessing}
 */
_zk.endProcessing = function (pid?: number | string /* internal use only */): void {
	//F70-ZK-2495: delete init crash timer once endProcessing is called
	const crashTimer = window.zkInitCrashTimer;
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

/**
 * Disable the default behavior of ESC. In other words, after called, the user cannot abort the loading from the server.
 * <p>To enable ESC, you have to invoke {@link enableESC} and the number
 * of invocations shall be the same.
 * @see {@link zk.enableESC}
 */
_zk.disableESC = function (): void {
	++_zk._noESC;
};
/**
 * Enables the default behavior of ESC (i.e., stop loading from the server).
 * @see {@link zk.disableESC}
 */
_zk.enableESC = function (): void {
	--_zk._noESC;
};
_zk._noESC = 0; //# of disableESC being called (also used by mount.js)

//DEBUG//
/**
 * Display an error message to indicate an error.
 * Example:
 * ```ts
 * zk.error('Oops! Something wrong:(');
 * ```
 * @param err - the error or error message
 * @param silent - only show error box
 * @see {@link errorPush}
 * @see {@link errorDismiss}
 * @see {@link log}
 * @see {@link stamp}
 */
_zk.error = function (err: Error | string, silent?: boolean): void {
	const msg = err instanceof Error ? err.message : err,
		stack = err instanceof Error ? err.stack : undefined;
	if (!silent) {
		if (stack)
			_zk.debugLog(stack);
		if (_zk.sendClientErrors)
			zAu.send(new zk.Event(zk.Desktop._dt, 'error', {href: document.location.href, message: stack ?? msg}, {ignorable: true}), 800);
	}
	_zk.errorPush(msg);
};
//DEBUG//
/**
 * If in debug-js, use console log to display an log message to indicate an debug log.
 * Example:
 * ```ts
 * zk.debugLog('Oops! Something wrong:(');
 * ```
 * @param msg - the warning message
 * @since 8.5.0
 */
_zk.debugLog = function (msg: string): void {
	if (_zk.debugJS) console.log(msg); // eslint-disable-line no-console
};
/**
 * Push an error message to the error box.
 * Example:
 * ```ts
 * zk.errorPush('Oops! Something wrong:(');
 * ```
 * @param msg - the error message
 * @see {@link zk.error}
 * @since 10.0.0
 */
_zk.errorPush = function (msg: string): void {
	_zk._Erbx.push(msg);
};
/** Closes all error messages shown by {@link error}.
 * Example:
 * ```ts
 * zk.errorDismiss();
 * ```
 * @see {@link zk.error}
 */
_zk.errorDismiss = function (): void {
	_zk._Erbx.remove();
};
/** Logs an message for debugging purpose.
 * Example:
 * ```ts
 * zk.log('reach here');
 * zk.log('value is", value);
 * ```
 * @param detailed - varient number of arguments to log
 * @see {@link zk.stamp}
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
 * (with {@link zk.log}).
 * @param name - the unique name to represent a time stamp
 * @param noAutoLog - whehter not to auto log the result.
 * If omitted or false, {@link zk.stamp} will be invoked automatically
 * to log the info and clean up all stamps after mounting is completed.
 * Turn it off, if you prefer to invoke it manually.
 *
 * Logs the information of all stamps made by {@link zk.stamp}.
 * After the invocation, all stamps are reset.
 */
_zk.stamp = function (name?: string, noAutoLog?: boolean): void {
	if (name) {
		if (!noAutoLog && !_stamps.length)
			setTimeout(_stampout, 0);
		_stamps.push({n: name, t: Date.now()});
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
 * ```ts
 * document.createElement("script").src = zk.ajaxURI('/web/js/com/foo/mine.js',{au:true});
 * ```
 * @param uri - the URI related to the AU engine. If null, the base URI is returned.
 * @param opts - the options. Allowed values:<br/>
 * <ul>
 * <li>au - whether to generate an URI for accessing the ZK update engine. If not specified, it is used to generate an URL to access any servlet</li>
 * <li>resource - whether to generate an URI for accessing the ZK resource engine. If not specified, it is used to generate an URL to access any servlet</li>
 * <li>desktop - the desktop or its ID. If null, the first desktop is used.</li>
 * <li>ignoreSession - whether to handle the session ID in the base URI.</li>
 * </ul>
 * @returns the encoded URI
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

	var abs = uri.startsWith('/');
	if (au && !abs) {
		abs = true;
		if (uri && !uri.startsWith('?')) // ignore url starts with '?....'
			uri = '/' + uri; //non-au supports relative path
	}

	uriPrefix = uriPrefix!;

	var j = uriPrefix.indexOf(';'), //ZK-1668: may have multiple semicolon in the URL
		k = uriPrefix.lastIndexOf('?');
	if (j < 0 && k < 0) return abs ? uriPrefix + uri : uri;

	if (k >= 0 && (j < 0 || k < j)) j = k;
	var prefix = abs ? uriPrefix.substring(0, j) : '';

	if (opts?.ignoreSession)
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
 * Example:
 * ```ts
 * var res = zk.ajaxResourceURI('/js/my/path/res.css');
 * ```
 *
 * @param uri - the resource URI.
 * @param version - the version string to build the cache-friendly URI. If none is set, use zk.build by default.
 * @param opts - the options. Allowed values:<br/>
 * <ul>
 * <li>au - whether to generate an URI for accessing the ZK update engine. If not specified, it is used to generate an URL to access any servlet</li>
 * <li>desktop - the desktop or its ID. If null, the first desktop is used.</li>
 * <li>ignoreSession - whether to handle the session ID in the base URI.</li>
 * </ul>
 * @returns the encoded resource URI
 * @since 9.0.0
 */
_zk.ajaxResourceURI = function (uri: string, version?: string, opts?: Partial<AjaxURIOptions>): string {
	if (!uri.startsWith('/'))
		uri = '/' + uri;
	opts = opts ?? {} as AjaxURIOptions;
	opts.resource = true;
	version = version || _zk.build;
	if (version) uri = '/web/_zv' + version + uri;
	else uri = '/web' + uri;
	return _zk.ajaxURI(uri, opts);
};
/**
 * Declares the desktop is used for the stateless context.
 * By stateless we mean the server doesn't maintain any widget at all.
 * @param dtid - the ID of the desktop to create
 * @param contextURI - the context URI, such as /zkdemo
 * @param updateURI - the update URI, such as /zkdemo/zkau
 * @param reqURI - the URI of the request path.
 * @returns the stateless desktop being created
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
 * @param name - the attribute name
 * @param script - the JS content
 * @since 8.0.0
 */
_zk.addDataHandler = function (name: string, script: string): void {
	if (!_zk.dataHandlers)
		_zk.dataHandlers = {};
	_zk.dataHandlers[name] = script;
};
/**
 * Test whether the name of the data attribute exists.
 * @returns true if existing.
 * @since 8.0.0
 */
_zk.hasDataHandler = function (name: string): boolean {
	return !!(_zk.dataHandlers && _zk.dataHandlers[name]);
};
/**
 * @returns the dataHandler (with a `run` method) from the given name
 * @since 8.0.0
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
				if (w.$ZKBINDER$) {
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
					subName = name.startsWith('data-') ? name.substring(5) : name;
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
/**
 * Intercepts a widget, when specific method has been called, the interceptor would be called first.
 * <p>Example,
 * ```ts
 * zk.$intercepts(zul.inp.Combobox, {
 *   open: function () {
 *     var context = this.$getInterceptorContext$();
 *     console.log('open');
 *     context.stop = false;
 *   }
 * });
 * ```
 * <p>In the interceptor function, you could call this.$getInterceptorContext$() to get the context object.
 * The context object has several properties, it would help you the deal with the interceptor:
 * <ul>
 * <li>context.stop - whether to call the original method in the widget.</li>
 * <li>context.result - the return value of the widget function.</li>
 * <li>context.args - the original arguments in the function of widget, you could update it for calling the original method.</li>
 * </ul>
 * @param targetClass - the destination object to override
 * @param interceptor - the interceptor map corresponds to the widget methods
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
		|| !ua.includes('compatible') && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua)
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
	&& (!agent.includes('trident')) && _ver(browser.version);
_zk.linux = agent.includes('linux');
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
_zk.mac = !_zk.ios && agent.includes('mac');

_zk.ipad = (_zk.webkit && agent.includes('ipad') && (
	//ZK-2245: add version info to zk.ios
	(iosver = agent.match(/version\/\d/)) && iosver[0].replace('version/', '')
	|| // ZK-2888, in iphone with chrome, it may not have version attribute.
	(iosver = agent.match(/ os \d/)) && iosver[0].replace(' os ', '')))
	|| // ZK-4451: iOS 13 safari ipad pretend itself as MacOS
	(_zk.safari && isTouchableMacIntel);

_zk.android = agent.includes('android');
_zk.mobile = _zk.ios || _zk.android;
_zk.css3 = true;
_zk.vendor = _zk.webkit ? 'webkit' : '';

let bodycls = '';
if (_zk.ff) {
	bodycls = 'gecko gecko' + Math.floor(Number(_zk.ff));
	_zk.vendor = 'Moz';
} else if (_zk.opera) { //no longer to worry 10.5 or earlier
	bodycls = 'opera';
	_zk.vendor = 'O';
} else {
	if (_zk.edge || _zk.edge_legacy) {
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

if (bodycls)
	jq(function () {
		jq(document.body).addClass(bodycls);
	});

_zk.vendor_ = _zk.vendor.toLowerCase();


/** @class zk.Buffer
 * A string concatenation implementation to speed up the rendering performance
 * in the modern browsers, except IE or MS's Edge. The implementation is to
 * cheat the mold js of the ZK widgets' implementation that it assumed the
 * argument is an array type in the early ZK version.
 * <p>Note: if the default implementation breaks the backward compatibility,
 * please use the following script to overwrite the implementation as the same
 * as the early ZK version. For example,
 * ```ts
 * zk.Buffer = Array;
 * ```
 * @since 8.0.0
 */
class Buffer extends Array<string> {
	out: string;
	constructor() {
		super();
		this.out = '';
	}
	override push(...items: (string | null | undefined)[]): number {
		for (const item of items) {
			if (item != null) {
				this.out += item;
			}
		}
		return this.length;
	}
	override join(separator?: string): string {
		if (separator)
			throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';
		return this.out;
	}
	override shift(): string | undefined {
		throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';
	}
	override unshift(...items: string[]): number {
		throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';
	}
	override pop(): string | undefined {
		throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';
	}
	override slice(start?: number | undefined, end?: number | undefined): string[] {
		throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';
	}
	override sort(compareFn?: ((a: string, b: string) => number) | undefined): this {
		throw 'Wrong usage here! Please run the script `zk.Buffer = Array;` instead.';
	}
}
// NOTE: the shape of the class `Buffer` shouldn't be leaked.
// Externally, `zk.Buffer` should always look like `ArrayConstructor`.
_zk.Buffer = Buffer as never as typeof Array<string>;


// eslint-disable-next-line @typescript-eslint/no-explicit-any
type PickFunction<T, K extends keyof T> = T[K] extends ((...args: any) => any) ? T[K] : never;

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
	/** @internal */
	declare _$ais?: CallableFunction[];
	/** @internal */
	declare _$supers: Record<string, unknown>;
	/** @internal */
	declare _$proxies: WeakMap<object, unknown>;
	/** @internal */
	declare _$super;
	/** @internal */
	declare _importantEvts: Record<string, unknown>;

	declare static $oid;

	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	constructor(..._rest: any[]/* for override compatibility */) {
		this.$oid = ++_oid;
	}

	/**
	 * The constructor.
	 * @defaultValue it does nothing so the subclass needs not to copy back
	 * (also harmless to call back).
	 * @see {@link afterInit}
	 * @deprecated as of 10.0 released. Using ES6 `constructor` instead.
	 */
	$init(props?: Record<string, unknown> | typeof zkac): void {
		// empty for subclass to override
		this['__$inited'] = true;
	}

	/**
	 * This function is called after the object is initialized and all its member
	 * fields are ready to use.
	 * @param props - the properties passed by constructor.
	 * @since 10.0.0
	 */
	afterCreated_(props?: Record<string, unknown> | typeof zkac): void {
		// empty for subclass to override
	}

	/**
	 * Specifies a function that shall be called after the object is initialized,
	 * i.e., after {@link zk.Object.$init} is called. This method can be called only during the execution of {@link zk.Object.$init}.
	 * <p>It is an advance feature that is used to allow a base class to do something that needs to wait for all deriving classes have been initialized.
	 *
	 * <p>Invocation Sequence:
	<ul>
	<li>The most derived class's $init (subclass)</li>
	<li>The based class's $init (if the derived class's $init invokes this.$supers('$init', arguments))</li>
	<li>The first function, if any, be added with afterInit, then the second (in the same order that afterInit was called)... </li>
	</ul>
	 * @param func - the function to register for execution later
	 * @see {@link zk.Object.$init}
	 * @deprecated as of 10.0 released. Using {@link afterCreated_} instead.
	 */
	afterInit(func: CallableFunction): void {
		(this._$ais = this._$ais || []).unshift(func); //reverse
	}
	/**
	 * The class that this object belongs to.
	 */
	get $class(): typeof ZKObject {
		return this.constructor as never;
	}

	/**
	 * @returns the class of the subsclass which extends from zk.Class.
	 * @since 10.0.0
	 */
	get$Class<T extends typeof ZKObject>(): T {
		return this.$class as T;
	}

	/**
	 * The object ID. Each object has its own unique $oid.
	 * It is mainly used for debugging purpose.
	 * <p>Trick: you can test if a JavaScript object is a ZK object by examining this property, such as
	 * `if (o.$oid) alert('o is a ZK object');`
	 * <p>Notice: zk.Class extends from zk.Object (so a class also has $oid)
	 */
	$oid = 0;
	/**
	 * Determines if this object is an instance of the class represented by the specified Class parameter.
	 * Example:
	 * ```ts
	 * if (obj.$instanceof(zul.wgt.Label, zul.wgt.Image)) {
	 * }
	 * ```
	 * @param klass - the Class object to be checked.
	 * Any number of arguments can be specified.
	 * @returns true if this object is an instance of the class
	 */
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	$instanceof(...klass: any[]): boolean {
		for (const k of klass) {
			if (this instanceof k) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Invokes a method defined in the superclass with any number of arguments. It is like Function's call() that takes any number of arguments.
	 * <p>Example:
	 * ```ts
	 * multiply: function (n) {
	 *   return this.$super('multiply', n + 2);
	 * }
	 * ```
	 * @param mtd - the method name to invoke
	 * @param args - any number of arguments
	 * @returns the object being returned by the method of the superclass.
	 * @see {@link zk.Object.$supers}
	 */
	$super<M extends keyof this & string, F extends PickFunction<this, M>>(mtd: M, ...args: Parameters<F>): ReturnType<F>
	/**
	 * Invokes a method defined in the superclass with any number of arguments.
	 * It is like Function's call() that takes any number of arguments.
	 * <p>It is similar to {@link ZKObject.$super}, but
	 * this method works even if the superclass calls back the same member method.
	 * In short, it is tedious but safer.
	 * <p>Example:
	 * ```ts
	 * foo.MyClass = zk.$extends(foo.MySuper, {
	 *   multiply: function (n) {
	 *     return this.$super(foo.MyClass, 'multiply', n + 2);
	 *   }
	 * ```
	 * <p>Notice that the class specified in the first argument is <i>not</i>
	 * the super class having the method. Rather,
	 * it is the class that invokes this method.
	 * @param klass - the class that invokes this method.
	 * @param mtd - the method name to invoke
	 * @param args - any number of arguments
	 * @returns the object being returned by the method of the superclass.
	 * @see {@link zk.Object.$supers}
	 * @since 5.0.2
	 */
	$super<M extends keyof this & string, F extends PickFunction<this, M>>(klass: typeof ZKObject, mtd: M, ...args: Parameters<F>): ReturnType<F>
	$super<M extends keyof this & string>(a: typeof ZKObject | M, b?: unknown, ...c: unknown[]): unknown {
		type Params = Parameters<PickFunction<this, M>>;
		if (typeof a == 'string') {
			return this.$supers(a, [b, ...c] as Params) as unknown;
		}
		return this.$supers(a, b as M, c as Params) as unknown;
	}
	/**
	 * Invokes a method defined in the superclass with an array of arguments. It is like Function's apply() that takes an array of arguments.
	 * <p>Example:
	 * ```ts
	 * multiply: function () {
	 *   return this.$supers('multiply', arguments);
	 * }
	 * ```
	 * @param name - the method name to invoke
	 * @param args - an array of arguments. In most case, you just pass
	 * `arguments` (the built-in variable).
	 * @returns the object being returned by the method of the superclass.
	 * @see {@link zk.Object.$super}
	 */
	$supers<M extends keyof this & string, F extends PickFunction<this, M>>(name: M, args: Parameters<F>): ReturnType<F>
	/**
	 * Invokes a method defined in the superclass with an array of arguments. It is like Function's apply() that takes an array of arguments.
	 * <p>It is similar to {@link zk.Object.$supers}, but
	 * this method works even if the superclass calls back the same member method.
	 * In short, it is tedious but safer.
	 * <p>Example:
	 * ```ts
	 * foo.MyClass = zk.$extends(foo.MySuper, {
	 *   multiply: function () {
	 *     return this.$supers(foo.MyClass, 'multiply', arguments);
	 *   }
	 * ```
	 * <p>Notice that the class specified in the first argument is <i>not</i>
	 * the super class having the method. Rather,
	 * it is the class that invokes this method.
	 * @param klass - the class that invokes this method.
	 * @param name - the method name to invoke
	 * @param args - an array of arguments. In most case, you just pass
	 * `arguments` (the built-in variable).
	 * @returns the object being returned by the method of the superclass.
	 * @see {@link zk.Object.$super}
	 * @since 5.0.2
	 */
	$supers<M extends keyof this & string, F extends PickFunction<this, M>>(klass: typeof ZKObject, name: M, args: Parameters<F>): ReturnType<F>
	$supers(nm: typeof ZKObject | string, args: string | unknown[], argx?: unknown[]): unknown {
		var supers = this._$supers;
		if (!supers) supers = this._$supers = {};

		if (typeof nm != 'string') { //zk.Class assumed
			let method: Function, // eslint-disable-line @typescript-eslint/ban-types
				old = supers[args as string], //args is method's name
				p: object; //args is method's name
			if (!(p = nm.prototype._$super as never || Object.getPrototypeOf(nm.prototype)) || !(method = p[args as string] as never)) //nm is zk.Class
				throw args + ' not in superclass'; //args is the method name

			supers[args as string] = p;
			try {
				return method.apply(this, argx) as unknown;
			} finally {
				supers[args as string] = old; //restore
			}
		}

		//locate method
		var old = supers[nm],
			m: undefined | Function, // eslint-disable-line @typescript-eslint/ban-types
			p: object & { _$super?: object },
			oldmtd: CallableFunction;
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
			return m.apply(this, args) as unknown;
		} finally {
			supers[nm] = old; //restore
		}
	}

	/**
	 * Proxies a member function such that it can be called with this object in a context that this object is not available.
	 * It sounds a bit strange at beginning but useful when passing a member
	 * of an object that will be executed as a global function.
	 *
	 * <p>Example: Let us say if you want a member function to be called periodically, you can do as follows.
	 * ```ts
	 * setInterval(wgt.proxy(wgt.doIt), 1000); //assume doIt is a member function of wgt
	 * ```
	 * <p>With proxy, when doIt is called, this references to wgt. On the other hand, the following won't work since this doesn't reference to wgt, when doIt is called.
	 * ```ts
	 * setInterval(wgt.doIt, 1000); //WRONG! doIt will not be called with wgt
	 * ```
	 * <p>Notice that this method caches the result so that it will return the same
	 * proxied function, if you pass the same function again.
	 * @param func - a method member of this object
	 * @returns a function that can be called as a global function
	 * (that actually have `this` referencing to this object).
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
	/**
	 * Determines if the specified Object is assignment-compatible with this Class. This method is equivalent to [[zk.Object#$instanceof].
	 * Example:
	 * ```ts
	 * if (klass.isInstance(obj)) {
	 * }
	 * ```
	 * @param o - the object to check
	 * @returns true if the object is an instance
	 */
	static isInstance<T extends typeof ZKObject>(this: T, o: unknown): o is InstanceType<T> {
		return o instanceof ZKObject && o instanceof this;
	}
	/**
	 * Determines if the class by this Class object is either the same as, or is a superclass of, the class represented by the specified Class parameter.
	 * Example:
	 * ```ts
	 * if (klass1.isAssignableFrom(klass2)) {
	 * }
	 * ```
	 * @param cls - the Class object to be checked, such as zk.Widget.
	 * @returns true if assignable
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

/** @internal */
_zk._Erbx = class _Erbx extends ZKObject { //used in HTML tags
	id: string;
	dg?: zk.Draggable;
	constructor(msg: string) {
		super();
		var id = 'zk_err',
			$id = '#' + id,
			click = _zk.mobile ? 'touchstart' : 'click',
			// Use zUtl.encodeXML -- Bug 1463668: security
			html = '<div class="z-error" id="' + id + '">'
			+ '<div id="' + id + '-p">'
			+ '<div class="errornumbers">' + (++_errcnt) + ' Errors</div>'
			+ '<div id="' + id + '-remove-btn" class="button">'
			+ '<i class="z-icon-times"></i></div>'
			+ '<div id="' + id + '-refresh-btn" class="button">'
			+ '<i class="z-icon-refresh"></i></div></div>'
			+ '<div class="messagecontent"><div class="messages">'
			+ zUtl.encodeXML(msg, {multiline: true}) + '</div></div></div>';

		jq(document.body).append(/*safe*/ html);
		document.getElementById(id + '-remove-btn')?.addEventListener(click, () => {
			_Erbx.remove();
		});
		document.getElementById(id + '-refresh-btn')?.addEventListener(click, () => {
			_Erbx.redraw();
		});

		_erbx = this;
		this.id = id;
		try {
			var n: HTMLElement;
			this.dg = new zk.Draggable(undefined, n = jq($id)[0], {
				handle: jq($id + '-p')[0], zIndex: n.style.zIndex,
				starteffect: _zk.$void,
				endeffect: _zk.$void});
		} catch (e) {
			_zk.debugLog((e as Error).message || e as string);
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
		zAu.send(new zk.Event(zk.Desktop._dt, 'redraw'));
	}
	static push(msg: string): _Erbx | undefined {
		if (!_erbx)
			return new _zk._Erbx(msg);

		var id = (_erbx as _Erbx).id;
		jq('#' + id + ' .errornumbers')
			.text(++_errcnt + ' Errors');
		jq('#' + id + ' .messages')
			.append(/*safe*/ '<div class="newmessage">' + zUtl.encodeXML(msg) + '</hr></div>');
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
	/** @internal */
	export let _isReloadingInObsolete: boolean;
	export let timerAlive: boolean;
	export let portlet2Data: Record<string, { namespace: string; resourceURL: string }> | undefined;
	export let ausending: boolean;
	export let xhrWithCredentials: boolean;
	export let isTimeout: boolean;
	export let visibilitychange: boolean;
	/** @internal */
	export let _focusByClearBusy: boolean;

	// ./mount

	/**
	 * Adds a function that will be executed when the browser is about to unload the document. In other words, it is called when window.onbeforeunload is called.
	 *
	 * <p>To remove the function, invoke this method by specifying remove to the opts argument.
	 * ```ts
	 * zk.beforeUnload(fn, {remove: true});
	 * ```
	 *
	 * @param fn - the function to execute.
	 * The function shall return null if it is OK to close, or a message (String) if it wants to show it to the end user for confirmation.
	 * @param opts - a map of options. Allowed vlaues:<br/>
	 * <ul>
	 * <li>remove: whether to remove instead of add.</li>
	 * </ul>
	 */
	export function beforeUnload(fn: () => string | undefined, opts?: {remove: boolean}): void;
	export let feature: {
		standard?: boolean;
		pe?: boolean;
		ee?: boolean;
	};
	export let clientinfo: Record<string, unknown> | undefined;
	export let pfmeter: boolean;
	/** @internal */
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
	/** @internal */
	export let _cfByMD: boolean;
	export let cfrg: [number, number] | undefined;
	/** @internal */
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let _avoidRod: boolean | undefined;

	/** @internal */
	// ./dom
	export let _prevFocus: zk.Widget | undefined;

	// ./effect
	export let useStackup: string | boolean | undefined;

	/** @internal */
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
	// eslint-disable-next-line zk/preferStrictBooleanType
	export let sendClientErrors: boolean | undefined;
	export let updateURI: string | undefined;
	export let resourceURI: string | undefined;
	export let contextURI: string | undefined;
	export let dataHandlers: Record<string, string | DataHandler> | undefined;

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