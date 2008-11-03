/* util.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep 30 09:02:06     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zUtl = { //static methods
	//HTML/XML
	/** Encodes a message into a valid XML format. */
	encodeXML: function (txt, multiline) {
		var out = "";
		if (txt)
			for (var j = 0, tl = txt.length; j < tl; ++j) {
				var cc = txt.charAt(j);
				switch (cc) {
				case '<': out += "&lt;"; break;
				case '>': out += "&gt;"; break;
				case '&': out += "&amp;"; break;
				case '"': out += "&quot;"; break;
				case '\n':
					if (multiline) {
						out += "<br/>";
						break;
					}
				default:
					out += cc;
				}
			}
		return out;
	},
	/** Returns the element's value (by catenate all CDATA and text).
	 */
	getElementValue: function (el) {
		var txt = ""
		for (el = el.firstChild; el; el = el.nextSibling)
			if (el.data) txt += el.data;
		return txt;
	},

	/** Returns the current time (new Date().getTime()).
	 * It is a number starting from 01/01/1970.
	 */
	now: function () {
		return new Date().getTime();
	},
	/** Executes a method after the specified delay (milliseconds).
	 * It is the same as window.setTimeout except <code>this</code> refers
	 * to the specified object, <code>obj</code>.
	 */
	setTimeout: function (obj, method, timeout/*, ...*/) {
		var args = [];
		for (var j = arguments.length; --j >= 3;)
			args.unshift(arguments[j]);
		setTimeout(function () {method.apply(obj, args)}, timeout);
	},
	/** Schedules a method or repeated execution.
	 * It is the same as window.setInterval exception <code>this</code> refers
	 * to the specified object, <code>obj</code>.
	 */
	setInterval: function (obj, method, period/*, ...*/) {
		var args = [];
		for (var j = arguments.length; --j >= 3;)
			args.unshift(arguments[j]);
		setInterval(function () {method.apply(obj, args)}, period);
	},
	/** Returns whether the first argument is the same, or an ancestor
	 * of the second argument.
	 * <p>It assumes the second argument has either a method called getParent
	 * or a property called parent, that refer to
	 * its parent (or null if it has no parent).
	 * <p>If p is null, it is always return true;
	 */
	isAncestor: function (p, c) {
		if (!p) return true;
		for (; c; c = c.getParent ? c.getParent(): c.parent)
			if (p == c)
				return true;
		return false;
	},

	/** To confirm the user for an activity.
	 */
	confirm: function (msg) {
		zk.alerting = true;
		try {
			return confirm(msg);
		} finally {
			try {zk.alerting = false;} catch (e) {} //doc might be unloaded
		}
	},
	/** To prevent onblur if alert is shown.
	 * Note: browser will change the focus back, so it is safe to ingore.
	 */
	alert: function (msg) {
		zk.alerting = true;
		try {
			alert(msg);
		} finally {
			try {zk.alerting = false;} catch (e) {} //doc might be unloaded
		}
	},

	/** Instantiates an Ajax request. */
	newAjax: function () {
		if (window.XMLHttpRequest) {
			return new XMLHttpRequest();
		} else {
			try {
				return new ActiveXObject('Msxml2.XMLHTTP');
			} catch (e2) {
				return new ActiveXObject('Microsoft.XMLHTTP');
			}
		}
	}
};
