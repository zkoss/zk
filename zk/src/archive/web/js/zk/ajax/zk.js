/* zk.js

	Purpose:
		
	Description:
		
	History:
		Mon Sep 29 17:17:26 2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
//zk//
zk = {
	procDelay: 900,
	tipDelay: 800,
	resendDelay: -1,
	lastPointer: [0, 0],
	currentPointer: [0, 0],

	/** Whether ZK is creating a new page. */
	//creating: 0,

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

	$extends: function (superclass, members, staticMembers) {
		if (!superclass)
			throw 'unknown superclass';

	//Note: we cannot use extends due to IE and Safari
		var jclass = function() {
			this.$init.apply(this, arguments);
		};

		if (typeof superclass == 'string') {
			var sc = zk.$import(superclass);
			if (!sc) {
				sc = superclass.lastIndexOf('.');
				if (sc > 0)  {
					sc = superclass.substring(0, sc);
					if (!zPkg.load(sc)) {
						zk.afterLoad(function () {
							var sc = zk.$import(superclass);
							if (!sc)
								throw "unknown superclass "+superclass;
							zk._$extends(jclass,
								sc, members, staticMembers);
						});
						return jclass;
					}
				}
				throw "superclass not found, "+superclass;
			}
			superclass = sc;
		}

		this._$extends(jclass, superclass, members, staticMembers);
		return jclass;
	},
	_$extends: function (jclass, superclass, members, staticMembers) {
		var thisprototype = jclass.prototype,
			superprototype = superclass.prototype;
		zk.copy(thisprototype, superprototype); //inherit non-static
		zk.copy(thisprototype, members);

		for (var p in superclass) //inherit static members
			if (p != 'prototype')
				jclass[p] = superclass[p];

		zk.copy(jclass, staticMembers);

		thisprototype.$class = jclass;
		thisprototype._$super = superprototype;
		jclass.$class = zk.Class;
		jclass.superclass = superclass;
	},
	$default: function (opts, defaults) {
		opts = opts || {};
		for (var p in defaults)
			if (opts[p] == null)
				opts[p] = defaults[p];
		return opts;
	},
	copy: function (dst, src) {
		if (!dst) dst = {};
		for (var p in src)
			dst[p] = src[p];
		return dst;
	},

	$void: function() {
		return '';
	},

	parseInt: function (v, b) {
		v = v ? parseInt(v, b || 10): 0;
		return isNaN(v) ? 0: v;
	},
	isDigit: function (c) {
		return c >= '0' && c <= '9';
	},
	isWhitespace: function (cc) {
		return cc == ' ' || cc == '\t' || cc == '\n' || cc == '\r';
	},

	set: function (o, name, value, extra) {
		if (arguments.length == 2) {
			for (var p in name)
				zk._set(o, p, name[p]);
			return;
		}
		zk._set(o, name, value, extra);
	},
	_set: function (o, name, value, extra) {
		var m = o['set' + name.charAt(0).toUpperCase() + name.substring(1)];
		if (m) m.call(o, value, extra);
		else o[name] = value;
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
		setTimeout(zk.sysInited ? zk._showproc: zk._showprocmk, timeout > 0 ? timeout: 0);
			//Note: zk.sysInited might be cleared before _showproc
	},
	endProcessing: function() {
		zk.processing = false;
		zUtl.destroyProgressbox("zk_proc");
	},
	_showprocmk: function () {
		//it might be called before doc ready
		if (document.readyState
		&& !/loaded|complete/.test(document.readyState)) {
			var tid = setInterval(function(){
				if (/loaded|complete/.test(document.readyState)) {
					clearInterval(tid);
					zk._showprocmk();
				}
			}, 0);
			return;
		}
		zk._showproc0(true);
	},
	_showproc: function () {
		zk._showproc0();
	},
	_showproc0: function (mask) {
		if (zk.processing) {
			if (zDom.$("zk_proc") || zDom.$("zk_showBusy"))
				return;

			var msg;
			try {msg = mesg.PLEASE_WAIT;} catch (e) {msg = "Processing...";}
				//when the first boot, mesg might not be ready
			zUtl.progressbox("zk_proc", msg, mask);
		}
	},

	//status

	//DEBUG//
	error: function (msg) {
		if (!zk.sysInited) {
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
	+'px;" id="'+id+'"><table cellpadding="2" cellspacing="2" width="100%"><tr>'
	+'<td align="right"><div id="'+id
	+'$p"><a href="javascript:zk._sendRedraw()">redraw</a>&nbsp;'
	+'<a href="javascript:zDom.remove(\''+id+'\')">close</a></div></td></tr>'
	+'<tr valign="top"><td class="z-error-msg">'+zUtl.encodeXML(msg, true) //Bug 1463668: security
	+'</td></tr></table></div>';
		box = zDom.setOuterHTML(box, html);

		try {
			new zk.Draggable(null, box, {
				handle: zDom.$(id+'$p'), zIndex: box.style.zIndex,
				starteffect: zk.$void, starteffect: zk.$void,
				endeffect: zk.$void});
		} catch (e) {
		}
	},
	errorDismiss: function () {
		for (var j = zk._errcnt; j; --j)
			zDom.remove("zk_err" + j);
	},
	_sendRedraw: function () {
		zk.errorDismiss();
		zAu.send(new zk.Event(null, 'redraw'));
	},
	log: function (vararg) {
		var ars = arguments, msg = '';
		for (var j = 0, len = ars.length; j < len; j++) {
			if (msg) msg += ", ";
			msg += ars[j];
		}

		zk._msg = (zk._msg ? zk._msg + msg: msg) + '\n';
		setTimeout(zk._log0, 600);
	},
	_log0: function () {
		if (zk._msg) {
			var console = zDom.$("zk_log");
			if (!console) {
				console = document.createElement("DIV");
				document.body.appendChild(console);
				var html =
	'<div id="zk_logbox" class="z-log">'
	+'<button onclick="zDom.remove(\'zk_logbox\');">X</button><br/>'
	+'<textarea id="zk_log" rows="10"></textarea></div>';
				zDom.setOuterHTML(console, html);
				console = zDom.$("zk_log");
			}
			console.value += zk._msg;
			console.scrollTop = console.scrollHeight;
			zk._msg = null;
		}
	}
};

//String//
zk.copy(String.prototype, {
	startsWith: function (prefix) {
		return this.substring(0,prefix.length) == prefix;
	},
	endsWith: function (suffix) {
		return this.substring(this.length-suffix.length) == suffix;
	},
	trim: function () {
		var j = 0, tl = this.length, k = tl - 1;
		while (j < tl && this.charAt(j) <= ' ')
			++j;
		while (k >= j && this.charAt(k) <= ' ')
			--k;
		return j > k ? "": this.substring(j, k + 1);
	},
	$camel: function() {
		var parts = this.split('-'), len = parts.length;
		if (len == 1) return parts[0];

		var camelized = this.charAt(0) == '-' ?
			parts[0].charAt(0).toUpperCase() + parts[0].substring(1): parts[0];

		for (var i = 1; i < len; i++)
			camelized += parts[i].charAt(0).toUpperCase() + parts[i].substring(1);
		return camelized;
	},
	$inc: function (diff) {
		return String.fromCharCode(this.charCodeAt(0) + diff)
	},
	$sub: function (cc) {
		return this.charCodeAt(0) - cc.charCodeAt(0);
	}
});

//Array//
zk.copy(Array.prototype, {
	$array: true, //indicate it is an array
	$contains: function (o) {
		for (var j = 0, tl = this.length; j < tl; ++j) {
			if (o == this[j])
				return true;
		}
		return false;
	},
	$add: function (o, overwrite) {
		if (overwrite)
			for (var tl = this.length, j = 0; j < tl; ++j)
				if (o == this[j]) {
					this[j] = o;
					return false;
				}
 		this.push(o);
 		return true;
	},
	$addAt: function (j, o) {
		var l = this.length;
		if (j >= l) this.push(o);
		else this.splice(j, 0, o);
	},
	$remove: function (o) {
		for (var j = 0, tl = this.length; j < tl; ++j) {
			if (o == this[j]) {
				this.splice(j, 1);
				return true;
			}
		}
		return false;
	},
	$removeAt: function (j) {
		if (j < this.length) this.splice(j, 1);
	},
	$clone: function() {
		return [].concat(this);
	}
});
if (!Array.prototype.indexOf)
	Array.prototype.indexOf = function (o) {
		for (var i = 0, len = this.length; i < len; i++)
			if (this[i] == o) return i;
		return -1;
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
zk.Object = function () {};
zk.Object.prototype = {
	$init: zk.$void,
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
	$super: function (mtdnm, vararg) {
		var args = [];
		for (var j = arguments.length; --j > 0;)
			args.unshift(arguments[j]);
		return this.$supers(mtdnm, args);
	},
	$supers: function (mtdnm, args) {
		var supers = this._$supers;
		if (!supers) supers = this._$supers = {};

		//locate the method
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

	proxy: function (f, nm) {
		if (nm) {
			var fpx = this[nm];
			if (fpx) return fpx;
		}

		var o = this,
			fpx = function () {
				return f.apply(o, arguments);
			};
		if (nm) this[nm] = fpx;
		return fpx;
	}
};

//Class//
zk.Class = function () {}
zk.Class.superclass = zk.Object;
_zkf = {
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
