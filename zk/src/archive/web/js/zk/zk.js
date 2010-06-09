/* zk.js

	Purpose:

	Description:

	History:
		Mon Sep 29 17:17:26 2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
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

	function newClass() {
		return function () {
			this.$oid = ++_oid;
			this.$init.apply(this, arguments);

			var ais = this._$ais;
			if (ais) {
				delete this._$ais;
				for (var j = ais.length; j--;)
					ais[j].call(this);
			}
		};
	}
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
			if (ar && (jq.isArray(ar) || ar.zk)) //ar.zk: jq(xx)
				msg.push('[' + toLogMsg(ar, isDetailed) + ']');
			else if (zk.Widget.isInstance(ar))
				msg.push(wgt2s(ar));
			else if (ar && ar.nodeType) {
				var w = zk.Widget.$(ar);
				if (w) msg.push(jq.nodeName(ar), ':', wgt2s(w));
				else msg.push(jq.nodeName(ar), '#', ar.id);
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
	/** Whether it is Internet Explorer.
	 * @type boolean
	 */
	//ie: false,
	/** Whether it is Internet Exploer 6 (excluding 7 or others).
	 * @type boolean
	 */
	//ie6_: false,
	/** Whether it is Internet Exploer 7 or later.
	 * @type boolean
	 */
	//ie7: false,
	/** Whether it is Internet Exploer 7 (excluding 8 or others).
	 * @type boolean
	 */
	//ie7_: false,
	/** Whether it is Internet Exploer 8 or later.
	 * @type boolean
	 */
	//ie8: false,
	/** Whether it is Internet Exploer 8 or later, and running in
	 * Internet Explorer 7 compatible mode.
	 * @type boolean
	 */
	//ie8c: false,
	/** Whether it is Gecko-based browsers, such as Firefox.
	 * @type boolean
	 */
	//gecko: false,
	/** Whether it is Gecko-based browsers, such as Firefox, and it
	 * is version 2 (excluding 3 or others).
	 * @type boolean
	 */
	//gecko2_: false,
	/** Whether it is Gecko-based browsers, such as Firefox, and it
	 * is version 3 and later.
	 * @type boolean
	 */
	//gecko3: false,
	/** Whether it is Safari.
	 * @type boolean
	 */
	//safari: false,
	/** Whether it is Opera.
	 * @type boolean
	 */
	//opera: false,
	/** Whether it is Adobe AIR.
	 * @type boolean
	 */
	//air: false,
	/** Whether it supports CSS3.
	 * @type boolean
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
	 * By default, it is false. It is true if <a href="http://docs.zkoss.org/wiki/ZK_Light">ZK Light</a>
	 * is used
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

	/** Copies a map of properties (or options) from one map to another.
	 * Example: extending Array 
<pre><code>
zk.copy(Array.prototoype, {
 $add: function (o) {
  this.push(o);
 }
});
</code></pre>
	 * @param Object dst the destination object to copy properties to
	 * @param Object src the source object to copy properties from
	 * @return Object the destination object
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

	/** Defines a package. It creates the package if not defined yet.
	 * It is similar to Java's package statement except it returns the package
	 * object.
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

		var jclass = newClass();

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
	 * @see #override(Object, String, Object)
	 */
	/** Overrides a particular method.
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
	 * @see #override(Object, Map, Map)
	 */
	override: function (dst, backup, src) {
		if (typeof backup == "string") {
			dst['$' + backup] = dst[backup];
			dst[backup] = src;
		} else
			for (var nm in src)
				_overrideSub(dst, nm, backup[nm] = dst[nm], dst[nm] = src[nm]);
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
			pt['set' + nm2] = def(nm1, before, after);
			pt['get' + nm2] = pt['is' + nm2] =
				new Function('return this.' + nm1 + ';');
		}
		return klass;
	},

	/** A does-nothing function.
	 */ 
	$void: function () {},

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
	 */
	set: function (o, name, value, extra) {
		var m = o['set' + name.charAt(0).toUpperCase() + name.substring(1)];
		if (!m) o[name] = value;
		else if (arguments.length >= 4)
			m.call(o, value, extra);
		else
			m.call(o, value);
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
		var nm = name.charAt(0).toUpperCase() + name.substring(1);
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
		zUtl.destroyProgressbox("zk_proc");
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
		new _zErb(msg);
	},
	/** Closes all error messages shown by {@link #error}.
   	 * Example: 
<pre><code>zk.errorDismiss();</code></pre>
	 * @see #error
	 */
	errorDismiss: function () {
		_zErb.closeAll();
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
		setTimeout(function(){jq(doLog);}, 300);
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
		if (dt && !dt.stateless) throw "Desktop conflict";
		if (zk.updateURI == null)
			zk.updateURI = updateURI;
		if (zk.contextURI == null) //it might be ""
			zk.contextURI = contextURI;
		return dt || new Desktop(dtid, contextURI, updateURI, reqURI, true);
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

	zk.css3 = !(zk.ie || zk.gecko2_ || zk.opera);
	
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
	$class: zk.Object,
	/** The object ID. Each object has its own unique $oid.
	 * It is mainly used for debugging purpose.
	 * <p>Trick: you can test if a JavaScript object is a ZK object by examining this property, such as
	 * <code>if (o.$oid) alert('o is a ZK object');</code>
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
		for (var j = arguments.length, cls; j--;)
			if (cls = arguments[j]) {
				var c = this.$class;
				if (c == zk.Class)
					return this == zk.Object || this == zk.Class; //follow Java
				for (; c; c = c.superclass)
					if (c == cls)
						return true;
			}
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
		var args = [], bCls = typeof arg0 != "string";
		for (var j = arguments.length, end = bCls ? 1: 0; --j > end;)
			args.unshift(arguments[j]);
		return bCls ? this.$supers(arg0, arg1, args): this.$supers(arg0, args);
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

		if (typeof nm != "string") { //zk.Class assumed
			var old = supers[args], p; //args is method's name
			if (!(p = nm.prototype._$super) || !(nm = p[args])) //nm is zk.Class
				throw args + " not in superclass"; //args is the method name

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
			throw nm + " not in superclass";

		try {
			return m.apply(this, args);
		} finally {
			supers[nm] = old; //restore
		}
	},
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

zk.Class = function () {}
zk.Class.superclass = zk.Object;
zk.Class.prototype.$class = zk.Class;
/** @partial zk.Object
 */
_zkf = {
	//note we cannot generate javadoc for this because Java cannot have both static and non-static of the same name
	$class: zk.Class,
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
		for (; cls; cls = cls.superclass)
			if (this == cls)
				return true;
		return false;
	},
	$instanceof: zk.Object.prototype.$instanceof
};
zk.copy(zk.Class, _zkf);
zk.copy(zk.Object, _zkf);

//error box//
var _errs = [], _errcnt = 0;

_zErb = zk.$extends(zk.Object, {
	$init: function (msg) {
		var id = "zk_err" + _errcnt++,
			$id = '#' + id;
			x = (_errcnt * 5) % 50, y = (_errcnt * 5) % 50,
			html =
	'<div class="z-error" style="left:'+(jq.innerX()+x)+'px;top:'+(jq.innerY()+y)
	+'px;" id="'+id+'"><table cellpadding="2" cellspacing="2" width="100%"><tr>'
	+'<td align="right"><div id="'+id+'-p">';
	if (!zk.light)
		html += '<span class="btn" onclick="_zErb._redraw()">redraw</span>&nbsp;';
	html += '<span class="btn" onclick="_zErb._close(\''+id+'\')">close</span></div></td></tr>'
	+'<tr valign="top"><td class="z-error-msg">'+zUtl.encodeXML(msg, {multiline:true}) //Bug 1463668: security
	+'</td></tr></table></div>';
		jq(document.body).append(html);

		this.id = id;
		_errs.push(this);

		try {
			var n;
			this.dg = new zk.Draggable(null, n = jq($id)[0], {
				handle: jq($id + '-p')[0], zIndex: n.style.zIndex,
				starteffect: zk.$void, starteffect: zk.$void,
				endeffect: zk.$void});
		} catch (e) {
		}
	},
	destroy: function () {
		_errs.$remove(this);
		if (this.dg) this.dg.destroy();
		jq('#' + this.id).remove();
	}
},{
	_redraw: function () {
		zk.errorDismiss();
		zAu.send(new zk.Event(null, 'redraw'));
	},
	_close: function (id) {
		for (var j = _errs.length; j--;) {
			var e = _errs[j];
			if (e.id == id) {
				_errs.splice(j, 1);
				e.destroy();
				return;
			}
		}
	},
	closeAll: function () {
		for (var j = _errs.length; j--;)
			_errs[j].destroy();
		_errs = [];
	}
});

})();
