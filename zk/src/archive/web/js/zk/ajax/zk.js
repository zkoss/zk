/* zk.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 29 17:17:26     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
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

//zk//
zk = { //static methods
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
	 */
	$import: function (name) {
		for (var j = 0, ref = window;;) {
			var k = name.indexOf('.', j),
				nm = k >= 0 ? name.substring(j, k): name.substring(j);
			var nxt = ref[nm];
			if (!nxt) throw "Unknown package/class: "+name;
			if (k < 0) return nxt;
			ref = nxt;
			j = k + 1;
		}
	},

	/** Declares a class that extendss the specified base class.
	 * @param baseClass the base class.
	 */
	$extends: function (baseClass, methods) {
	//Note: we cannot use extends due to IE and Safari
		var jclass = function() {
			this.$init.apply(this, arguments);
		};

		for (var p in baseClass.prototype) //inherit non-static
			if (p.charAt(0) != '$' || p == '$init') {
				jclass.prototype[p] = baseClass.prototype[p];
				jclass.prototype['$' + p ] = baseClass.prototype[p];
			}

		for (var p in methods)
			jclass.prototype[p] = methods[p];
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

	//DEBUG//
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
		zk._msg +=  '\n';
		setTimeout(zk._debug0, 600);
	},
	_debug0: function () {
		if (zk._msg) {
			var console = zkDOM.$("zk_dbg");
			if (!console) {
				console = document.createElement("DIV");
				document.body.appendChild(console);
				var html =
'<div id="zk_dbgbox" style="text-align:right;width:50%;right:0;bottom:0;position:absolute">'
+'<button onclick="zkDOM.detach(\'zk_dbgbox\')">close</button><br/>'
+'<textarea id="zk_dbg" style="width:100%" rows="10"></textarea></div>';
				zkDOM.setOuterHTML(console, html);
				console = zkDOM.$("zk_dbg");
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
	$init: zk.$void
};
