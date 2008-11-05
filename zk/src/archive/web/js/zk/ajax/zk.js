/* zk.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 29 17:17:26 2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
//String//
String.prototype.startsWith = function (prefix) {
	return this.substring(0,prefix.length) == prefix;
};
String.prototype.endsWith = function (suffix) {
	return this.substring(this.length-suffix.length) == suffix;
};
String.prototype.trim = function () {
	var j = 0, tl = this.length, k = tl - 1;
	while (j < tl && this.charAt(j) <= ' ')
		++j;
	while (k >= j && this.charAt(k) <= ' ')
		--k;
	return j > k ? "": this.substring(j, k + 1);
};
String.prototype.camelize = function() {
	var parts = this.split('-'), len = parts.length;
	if (len == 1) return parts[0];

	var camelized = this.charAt(0) == '-' ?
		parts[0].charAt(0).toUpperCase() + parts[0].substring(1): parts[0];

	for (var i = 1; i < len; i++)
		camelized += parts[i].charAt(0).toUpperCase() + parts[i].substring(1);
	return camelized;
};

//Array//
/** Removes the specified object from the array if any.
 * Returns false if not found.
 */
Array.prototype.remove = function (o) {
	for (var j = 0, tl = this.length; j < tl; ++j) {
		if (o == this[j]) {
			this.splice(j, 1);
			return true;
		}
	}
	return false;
};
/** Returns whether the array contains the specified object.
 */
Array.prototype.contains = function (o) {
	for (var j = 0, tl = this.length; j < tl; ++j) {
		if (o == this[j])
			return true;
	}
	return false;
};
/** Adds the specified object to the end of the array.
 * @param overwrite whether to overwrite if the object is already in the array.
 * @return true if added successfully.
 */
Array.prototype.add = function (o, overwrite) {
	if (overwrite)
		for (var j = 0, tl = this.length; j < tl; ++j) {
			if (o == this[j]) {
				this[j] = o;
				return false;
			}
		}

	this.push(o);
	return true;
};
/** Clones this array. */
Array.prototype.clone = function() {
	return [].concat(this);
};

//zk//
zk = { //static methods
	/** The ZK version. */
	//version: '5.0.0',
	/** The ZK build number. */
	//build: '0',
	/** The processing prompt delay. */
	//procDelay: 0,
	/** The tooltip delay. */
	//tipDelay: 0,
	/** The resend delay. */
	//resendDelay: 0,
	/** # of JS files being loaded. */
	loading: 0,

	/** Whether ZK is creating a new page. */
	//creating: 0,
	/** Whether ZK has created at least one page. */
	//booted: 0,
	/** Whether ZK is processing something (such as creating, doing AU). */
	//processing: 0,
	/** The DOM element that gains the focus. */
	//currentFocus: null,

	/** Declares a package.
	 * It is similar to Java's package statement.
	 * <p>Example:
	 * <pre><code>
	 * _ = zk.package('com.foo');
	 * _.Cool = zk.extends(zk.Object);
	 * </code></pre>
	 * @param name the package name (a String object).
	 */
	$package: function (name) {
		for (var j = 0, ref = window;;) {
			var k = name.indexOf('.', j),
				nm = k >= 0 ? name.substring(j, k): name.substring(j);
			var nxt = ref[nm];
			if (!nxt) nxt = ref[nm] = {};
			if (k < 0) return nxt;
			ref = nxt;
			j = k + 1;
		}
	},
	/** Imports a package or a class.
	 * <p>Example:
	 * <pre><code>
	 * var foo = zk.import('com.foo');
	 * var cool = new foo.Cool();
	 * var Cool = zk.import('com.foo.Cooler');
	 * var cooler = new Cooler();
	 * </code></pre>
	 * @param name the package name (a String object).
	 * @return the package or class being imported, or null if not found
	 */
	$import: function (name) {
		for (var j = 0, ref = window;;) {
			var k = name.indexOf('.', j),
				nm = k >= 0 ? name.substring(j, k): name.substring(j);
			var nxt = ref[nm];
			if (k < 0 || !nxt) return nxt;
			ref = nxt;
			j = k + 1;
		}
	},

	/** Declares a class that extendss the specified base class.
	 * @param baseClass the base class.
	 * @param methods the non-static methods
	 * @param staticMethods the static methods.
	 */
	$extends: function (baseClass, methods, staticMethods) {
	//Note: we cannot use extends due to IE and Safari
		var jclass = function() {
			this.construct.apply(this, arguments);
		};

		for (var p in baseClass.prototype) { //inherit non-static
			var cc = p.charAt(0);
			if (cc != '$' || p == '$instanceof') {
				var m = baseClass.prototype[p];
				jclass.prototype[p] = m;
				if (cc != '_' && cc != '$' && typeof m == 'function') //not private method
					jclass.prototype['$' + p ] = m;
			}
		}

		for (var p in methods)
			jclass.prototype[p] = methods[p];

		for (var p in staticMethods)
			jclass[p] = staticMethods[p];

		jclass.prototype.$class = jclass;
		jclass.superclass = baseClass;
		jclass.isInstance = baseClass.isInstance;
		jclass.isAssignableFrom = baseClass.isAssignableFrom;
		return jclass;
	},

	/** A does-nothing function. */
	$void: function() {
	},
	/** An abstract function, i.e., a function of an interface.
	 * It always throws an exception when called.
	 */
	$abstract: function () {
		throw "abstract method";
	},

	/** parse a string to an integer. */
	parseInt: function (v, b) {
		v = v ? parseInt(v, b || 10): 0;
		return isNaN(v) ? 0: v;
	},
	/** Returns whether a character is a white space. */
	isWhitespace: function (cc) {
		return cc == ' ' || cc == '\t' || cc == '\n' || cc == '\r';
	},

	//Processing//
	/** Set a flag, zk.processing, that it starts an processing.
	 * It also shows a message to indicate "processing" after the specified timeout.
	 */
	startProcessing: function (timeout) {
		zk.processing = true;
		if (timeout > 0) setTimeout(zk._showproc, timeout);
		else zk._showproc();
	},
	/** Clear a flag, zk.processing, to indicate the processing is done.
	 * It also removes the message indicating "processing".
	 */
	endProcessing: function() {
		zk.processing = false;
		zUtl.cleanAllProgress("zk_proc");
	},
	/** Shows the message of zk.startProcessing. */
	_showproc: function () {
		if (zk.processing && !zk.loading) {
			if (zDom.$("zk_proc") || zDom.$("zk_showBusy"))
				return;

			var msg;
			try {msg = mesg.PLEASE_WAIT;} catch (e) {msg = "Processing...";}
				//when the first boot, mesg might not be ready
			zUtl.progressbox("zk_proc", msg, !zk.booted);
		}
	},

	//DEBUG//
	/** Generates an error message. */
	error: function (msg) {
		if (!zk.booted) {
			setTimeout(function () {zk.error(msg)}, 100);
			return;
		}

		if (!zk._errcnt) zk._errcnt = 1;
		var id = "zk_err" + zk._errcnt++,
			x = (zk._errcnt * 5) % 50, y = (zk._errcnt * 5) % 50,
			box = document.createElement("DIV");
		document.body.appendChild(box);
		var html =
	 '<div class="z-error" style="left:'+(zDom.innerX()+x)+'px;top:'+(zDom.innerY()+y)
	+'px;" id="'+id+'"><table cellpadding="2" cellspacing="2" width="100%"><tr valign="top">'
	+'<td width="20pt"><button onclick="zAu.sendRedraw()">redraw</button>'
	+'<button onclick="zDom.remove(\''+id+'\')">close</button></td>'
	+'<td class="z-error-msg">'+zUtl.encodeXML(msg, true) //Bug 1463668: security
	+'</td></tr></table></div>';
		zDom.setOuterHTML(box, html);

		//TODO: draggable box
		//box = zDom.$e(id); //we have to retrieve back
	},
	/** Closes all error box (zk.error).
	 */
	errorDismiss: function () {
		for (var j = zk._errcnt; j; --j)
			zDom.remove("zk_err" + j);
	},
	/** Generates a message for debugging purpose. */
	debug: function (msg/*, ...*/) {
		var a = arguments;
		if (a.length > 1) {
			var m = "";
			for (var i = 0, len = a.length; i < len; i++) {
				if (m) m += ", ";
				m += a[i];
			}
			msg = m;
		}

		zk._msg = zk._msg ? zk._msg + msg: msg;
		zk._msg += '\n';
		setTimeout(zk._debug0, 600);
	},
	_debug0: function () {
		if (zk._msg) {
			var console = zDom.$("zk_dbg");
			if (!console) {
				console = document.createElement("DIV");
				document.body.appendChild(console);
				var html =
'<div id="zk_dbgbox" style="text-align:right;width:50%;right:0;bottom:0;position:absolute">'
+'<button onclick="zDom.remove(\'zk_dbgbox\')" style="font-size:9px">X</button><br/>'
+'<textarea id="zk_dbg" style="width:100%" rows="10"></textarea></div>';
				zDom.setOuterHTML(console, html);
				console = zDom.$("zk_dbg");
			}
			console.value = console.value + zk._msg + '\n';
			console.scrollTop = console.scrollHeight;
			zk._msg = null;
		}
	}
};

zk.agent = navigator.userAgent.toLowerCase();
zk.safari = zk.agent.indexOf("safari") >= 0;
zk.opera = zk.agent.indexOf("opera") >= 0;
zk.gecko = zk.agent.indexOf("gecko/") >= 0 && !zk.safari && !zk.opera;
if (zk.gecko) {
	var j = zk.agent.indexOf("firefox/");
	j = zk.parseInt(zk.agent.substring(j + 8));
	zk.gecko3 = j >= 3;
	zk.gecko2Only = !zk.gecko3;
} else if (!zk.opera) {
	var j = zk.agent.indexOf("msie ");
	zk.ie = j >= 0;
	if (zk.ie) {
		j = zk.parseInt(zk.agent.substring(j + 5));
		zk.ie7 = j >= 7; //ie7 or later
		zk.ie8 = j >= 8; //ie8 or later
		zk.ie6Only = !zk.ie7;
	}
}
zk.air = zk.agent.indexOf("adobeair") >= 0;

//Object//
/** The Object class that all other classes are extended from. */
zk.Object = function () {};
zk.Object.prototype = {
	construct: zk.$void,
	/** The class of this object belongs to. */
	$class: zk.Object,
	/** Determines if this object is an instance of the specified class. */
	$instanceof: function (cls) {
		if (cls)
			for (var c = this.$class; c; c = c.superclass)
				if (c == cls)
					return true;
		return false;
	}
};
/** Determines if the specified object is an instance of this class. */
zk.Object.isInstance = function (o) {
	return o && o.$instanceof && o.$instanceof(this);
};
/** Determines if this class is a super class of the specified class. */
zk.Object.isAssignableFrom = function (cls) {
	for (; cls; cls = cls.superclass)
		if (this == cls)
			return true;
	return false;
};
