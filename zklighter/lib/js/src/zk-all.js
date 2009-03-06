
zk = {
	procDelay: 900,
	tipDelay: 800,
	resendDelay: -1,
	lastPointer: [0, 0],
	currentPointer: [0, 0],

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

		for (var p in superclass) //inherit static
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
	forEach: function (objs, fn) {
		var args = [];
		for (var j = arguments.length; --j >= 2;)
			args.unshift(arguments[j]);
		for (var j = 0, len = objs.length; j < len;)
			fn.apply(objs[j], args);
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
	+'<tr valign="top"><td class="z-error-msg">'+zUtl.encodeXML(msg, {multiline:true}) //Bug 1463668: security
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

	zk.xbodyClass = 'gecko gecko' + j;
} else if (zk.opera) {
	zk.xbodyClass = 'opera';
} else {
	var j = zk.agent.indexOf("msie ");
	zk.ie = j >= 0;
	if (zk.ie) {
		j = zk.parseInt(zk.agent.substring(j + 5));
		zk.ie7 = j >= 7; //ie7 or later
		zk.ie8 = j >= 8; //ie8 or later
		zk.ie6Only = !zk.ie7;

		zk.xbodyClass = 'ie ie' + j;
	} else if (zk.safari)
		zk.xbodyClass = 'safari';
}
if (zk.air = zk.agent.indexOf("adobeair") >= 0)
	zk.xbodyClass = 'air';

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


/*zk.BigInteger = zk.$extends(zk.Object, {
	$init: function (value) {
		this._value = value ? '' + value: '0';
	}
});*/
zk.BigDecimal = zk.$extends(zk.Object, {
	_prec: 0,
	$init: function (value) {
		value = value ? '' + value: '0';
		var j = value.lastIndexOf('.');
		if (j >= 0) {
			value = value.substring(0, j) + value.substring(j + 1);
			this._prec = value.length - j;
		}
		this._value = value;
	}
});


zUtl = { //static methods
	//HTML/XML
	encodeXML: function (txt, opts) {
		var out = "";
		if (!txt) return out;

		var k = 0, tl = txt.length,
			pre = opts && opts.pre,
			multiline = pre || (opts && opts.multiline);
		for (var j = 0; j < tl; ++j) {
			var cc = txt.charAt(j);
			if (cc == '\n') {
				if (multiline) {
					out += txt.substring(k, j) + "<br/>\n";
					k = j + 1;
				}
			} else if (cc == ' ' || cc == '\t') {
				if (pre) {
					out += txt.substring(k, j) + '&nbsp;';
					if (cc == '\t') out += '&nbsp;&nbsp;&nbsp;';
					k = j + 1;
				}
			} else {
				var enc = zUtl._encs[cc];
				if (enc) {
					out += txt.substring(k, j) + '&' + enc + ';';
					k = j + 1;
				}
			}
		}
		return !k ? txt:
			k < tl ? out + txt.substring(k): out;
	},
	decodeXML: function (txt) {
		var out = "";
		if (!txt) return out;

		var k = 0, tl = txt.length;
		for (var j = 0; j < tl; ++j) {
			var cc = txt.charAt(j);
			if (cc == '&') {
				var l = txt.indexOf(';', j + 1);
				if (l >= 0) {
					var dec = zUtl._decs[txt.substring(j + 1, l)];
					if (dec) {
						out += txt.substring(k, j) + dec;
						k = (j = l) + 1;
					}
				}
			}
		}
		return !k ? txt:
			k < tl ? out + txt.substring(k): out;
	},
	_decs: {lt: '<', gt: '>', amp: '&', quot: '"'},
	_encs: {},

	renType: function (url, type) {
		var j = url.lastIndexOf(';');
		var suffix;
		if (j >= 0) {
			suffix = url.substring(j);
			url = url.substring(0, j);
		} else
			suffix = "";

		j = url.lastIndexOf('.');
		if (j < 0) j = url.length; //no extension at all
		var	k = url.lastIndexOf('-'),
			m = url.lastIndexOf('/'),
			ext = j <= m ? "": url.substring(j),
			pref = k <= m ? j <= m ? url: url.substring(0, j): url.substring(0, k);
		if (type) type = "-" + type;
		else type = "";
		return pref + type + ext + suffix;
	},

	getElementValue: function (el) {
		var txt = ""
		for (el = el.firstChild; el; el = el.nextSibling)
			if (el.data) txt += el.data;
		return txt;
	},

 	cellps0: ' cellpadding="0" cellspacing="0" border="0"',
 	img0: '<img style="height:0;width:0"/>',
 
	now: function () {
		return new Date().getTime();
	},
	today: function () {
		var d = new Date();
		return new Date(d.getFullYear(), d.getMonth(), d.getDate());
	},
	isAncestor: function (p, c) {
		if (!p) return true;
		for (; c; c = c.getParent ? c.getParent(): c.parent)
			if (p == c)
				return true;
		return false;
	},
	isDescendant: function (c, p) {
		return zUtl.isAncestor(p, c);
	},

	//progress//
	progressbox: function (id, msg, mask) {
		if (mask && zk.Page.contained.length) {
			for (var c = zk.Page.contained.length, e = zk.Page.contained[--c]; e; e = zk.Page.contained[--c]) {
				if (!e._applyMask)
					e._applyMask = new zk.eff.Mask({
						id: e.uuid + "$mask",
						anchor: e.getNode()
					});
			}
			return;
		}

		var x = zDom.innerX(), y = zDom.innerY(),
			style = ' style="left:'+x+'px;top:'+y+'px"',
			idtxt = id + '$t',
			idmsk = id + '$m',
			html = '<div id="'+id+'"';
		if (mask)
			html += '><div id="' + idmsk + '" class="z-modal-mask"'+style+'></div';
		html += '><div id="'+idtxt+'" class="z-loading"'+style
			+'><div class="z-loading-indicator"><span class="z-loading-icon"></span> '
			+msg+'</div></div></div>'
		var n = document.createElement("DIV");
		document.body.appendChild(n);
		n = zDom.setOuterHTML(n, html);

		var txt = zDom.$(idtxt);
		if (mask)
			n.z_mask = new zk.eff.FullMask({
				mask: zDom.$(idmsk),
				zIndex: zDom.getStyle(txt, 'z-index') - 1
			});

		if (mask && txt) { //center
			txt.style.left = (zDom.innerWidth() - txt.offsetWidth) / 2 + x + "px";
			txt.style.top = (zDom.innerHeight() - txt.offsetHeight) / 2 + y + "px";
		}
		zDom.cleanVisibility(n);
	},
	destroyProgressbox: function (id) {
		var n = zDom.$(id);
		if (n) {
			if (n.z_mask) n.z_mask.destroy();
			zDom.remove(n);
		}

		for (var c = zk.Page.contained.length, e = zk.Page.contained[--c]; e; e = zk.Page.contained[--c])
			if (e._applyMask) {
				e._applyMask.destroy();
				e._applyMask = null;
			}
	},

	//HTTP//
	go: function (url, overwrite, target) {
		if (!url) {
			location.reload();
		} else if (overwrite) {
			location.replace(url);
		} else if (target) {
			//we have to process query string because browser won't do it
			//even if we use zDom.insertHTMLBeforeEnd("<form...")
			try {
				var frm = document.createElement("FORM");
				document.body.appendChild(frm);
				var j = url.indexOf('?');
				if (j > 0) {
					var qs = url.substring(j + 1);
					url = url.substring(0, j);
					zk.queryToHiddens(frm, qs);
				}
				frm.name = "go";
				frm.action = url;
				frm.method = "GET";
				frm.target = target;
				frm.submit();
			} catch (e) { //happens if popup block
			}
		} else {
			location.href = url;
		}
	},

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
	},
	intsToString: function (ary) {
		if (!ary) return "";

		var sb = [];
		for (var j = 0, k = ary.length; j < k; ++j)
			sb.push(ary[j]);
		return sb.join();
	},
	stringToInts: function (numbers, defaultValue) {
		if (numbers == null)
			return null;

		var list = [];
		for (var j = 0;;) {
			var k = numbers.indexOf(',', j),
				s = (k >= 0 ? numbers.substring(j, k): numbers.substring(j)).trim();
			if (s.length == 0) {
				if (k < 0) break;
				list.push(defaultValue);
			} else
				list.push(zk.parseInt(s));

			if (k < 0) break;
			j = k + 1;
		}
		return list;
	},
	_init: function () {
		delete zUtl._init;

		var encs = zUtl._encs, decs = zUtl._decs;
		for (var v in decs)
			encs[decs[v]] = v;
	}
};
zUtl._init();


zDom = { //static methods
	$: function(id, alias) {
		return typeof id == 'string' ?
			id ? document.getElementById(id + (alias ? '$' + alias : '')): null: id;
			//strange but getElementById("") fails in IE7
	},
	$$: function (id, subId) {
		return typeof id == 'string' ?
			id ? document.getElementsByName(id + (subId ? '$' + subId : '')): null: id;
	},
	tag: function (n) {
		return n && n.tagName ? n.tagName.toUpperCase(): "";
	},

	hide: function (n) {
		n.style.display = 'none';
	},
	show: function (n) {
		n.style.display = '';
	},
	cleanVisibility: zk.opera ? function (n) {
		n.style.visibility = "visible";
			// visible will cause an other bug, but we need do it for Input element.
	} : function (n) {
		n.style.visibility = "inherit";
	},
	isVisible: function (n, strict) {
		return n && (!n.style || (n.style.display != "none" && (!strict || n.style.visibility != "hidden")));
	},
	isRealVisible: function (n, strict) {
		for (; n; n = zDom.parentNode(n))
			if (!zDom.isVisible(n, strict))
				return false;
		return true;
	},
	isAncestor: function (p, c) {
		if (!p) return true;
		for (; c; c = zDom.parentNode(c))
			if (p == c)
				return true;
		return false;
	},

	innerX: function () {
		return window.pageXOffset
			|| document.documentElement.scrollLeft
			|| document.body.scrollLeft || 0;
	},
	innerY: function () {
		return window.pageYOffset
			|| document.documentElement.scrollTop
			|| document.body.scrollTop || 0;
	},
	innerWidth: function () {
		return typeof window.innerWidth == "number" ? window.innerWidth:
			document.compatMode == "CSS1Compat" ?
				document.documentElement.clientWidth: document.body.clientWidth;
	},
	innerHeight: function () {
		return typeof window.innerHeight == "number" ? window.innerHeight:
			document.compatMode == "CSS1Compat" ?
				document.documentElement.clientHeight: document.body.clientHeight;
	},
	/** Returns the page total width. */
	pageWidth: function () {
		var a = document.body.scrollWidth, b = document.body.offsetWidth;
		return a > b ? a: b;
	},
	/** Returns the page total height. */
	pageHeight: function () {
		var a = document.body.scrollHeight, b = document.body.offsetHeight;
		return a > b ? a: b;
	},

	/** Scrolls the browser window to the specified element. */
	scrollTo: function (n) {
		n = zDom.$(n);
		var pos = zDom.cmOffset(n);
		scrollTo(pos[0], pos[1]);
		return n;
	},

	/** A map of the margin styles. */
	margins: {l: "margin-left", r: "margin-right", t: "margin-top", b: "margin-bottom"},
	/** A map of the border styles. */
	borders: {l: "border-left-width", r: "border-right-width", t: "border-top-width", b: "border-bottom-width"},
	/** A map of the padding styles. */
	paddings: {l: "padding-left", r: "padding-right", t: "padding-top", b: "padding-bottom"},
	/** Returns the summation of the specified styles.
	 *  For example,
	 * zDom.sumStyles(el, "lr", zDom.paddings) sums the style values of
	 * zDom.paddings['l'] and zDom.paddings['r'].
	 *
	 * @param areas the areas is abbreviation for left "l", right "r", top "t", and bottom "b".
	 * So you can specify to be "lr" or "tb" or more.
	 * @param styles {@link #paddings} or {@link #borders}. 
	 */
	sumStyles: function (el, areas, styles) {
		var val = 0;
		for (var i = 0, len = areas.length; i < len; i++){
			 var w = zk.parseInt(zDom.getStyle(el, styles[areas.charAt(i)]));
			 if (!isNaN(w)) val += w;
		}
		return val;
	},

	/** Sets the offset height by specifying the inner height.
	 * @param hgh the height without margin and border
	 */
	setOffsetHeight: function (el, hgh) {
		hgh -= zDom.padBorderHeight(el)
			+ zk.parseInt(zDom.getStyle(el, "margin-top"))
			+ zk.parseInt(zDom.getStyle(el, "margin-bottom"));
		el.style.height = Math.max(0, hgh) + "px";
	},

	/**
	 * Returns the revised position, which subtracted the offset of its scrollbar,
	 * for the specified element.
	 * @param {Object} el
	 * @param {Array} ofs [left, top];
	 * @return {Array} [left, top];
	 */
	revisedOffset: function (el, ofs) {
		if(!ofs) {
			if (el.getBoundingClientRect){ // IE and FF3
				var b = el.getBoundingClientRect();
				return [b.left + zDom.innerX() - el.ownerDocument.documentElement.clientLeft,
					b.top + zDom.innerY() - el.ownerDocument.documentElement.clientTop];
				// IE adds the HTML element's border, by default it is medium which is 2px
				// IE 6 and 7 quirks mode the border width is overwritable by the following css html { border: 0; }
				// IE 7 standards mode, the border is always 2px
				// This border/offset is typically represented by the clientLeft and clientTop properties
				// However, in IE6 and 7 quirks mode the clientLeft and clientTop properties are not updated when overwriting it via CSS
				// Therefore this method will be off by 2px in IE while in quirksmode
			}
			ofs = zDom.cmOffset(el);
		}
		var scrolls = zDom.scrollOffset(el);
		scrolls[0] -= zDom.innerX(); scrolls[1] -= zDom.innerY(); 
		return [ofs[0] - scrolls[0], ofs[1] - scrolls[1]];
	},
	/**
	 * Returns the revised width, which subtracted the size of its CSS border or padding, for the specified element.
	 * @param size original size of the specified element. 
	 * @param excludeMargin excludes the margins. You rarely need this unless
	 * size is in term of the parent
	 */
	revisedWidth: function (el, size, excludeMargin) {
		size -= zDom.padBorderWidth(el);
		if (size > 0 && excludeMargin)
			size -= zDom.sumStyles(el, "lr", zDom.margins);
		return size < 0 ? 0: size;
	},
	/**
	 * Returns the revised width, which subtracted the size of its CSS border or padding, for the specified element.
	 * @param size original size of the specified element. 
	 * @param excludeMargin excludes the margins. You rarely need this unless
	 * size is in term of the parent
	 */
	revisedHeight: function (el, size, excludeMargin) {
		size -= zDom.padBorderHeight(el);
		if (size > 0 && excludeMargin)
			size -= zDom.sumStyles(el, "tb", zDom.margins);
		return size < 0 ? 0: size;
	},
	/**
	 * Returns the number of the padding width and the border width from the specified element.
	 */
	padBorderWidth: function (el) {
		return zDom.sumStyles(el, "lr", zDom.borders) + zDom.sumStyles(el, "lr", zDom.paddings);
	},
	/**
	 * Returns the number of the padding height and the border height from the specified element.  
	 */
	padBorderHeight: function (el) {
		return zDom.sumStyles(el, "tb", zDom.borders) + zDom.sumStyles(el, "tb", zDom.paddings);
	},
	/**
	 * Returns the number of the scrollbar.
	 */
	scrollbarWidth: function (el) {
		return (el.offsetWidth - el.clientWidth) + zDom.sumStyles(el, "lr", zDom.borders);
	},
	/** Returns the maximal allowed height of the specified element.
	 * In other words, it is the client height of the parent minus all sibling's.
	 */
	vflexHeight: function (el) {
		var hgh = el.parentNode.clientHeight;
		if (zk.ie6Only) { //IE6's clientHeight is wrong
			var ref = el.parentNode;
			var h = ref.style.height;
			if (h && h.endsWith("px")) {
				h = zDom.revisedHeight(ref, zk.parseInt(h));
				if (h && h < hgh) hgh = h;
			}
		}

		for (var p = el; p = p.previousSibling;)
			if (p.offsetHeight && zDom.isVisible(p)) hgh -= p.offsetHeight; //may undefined
		for (var p = el; p = p.nextSibling;)
			if (p.offsetHeight && zDom.isVisible(p)) hgh -= p.offsetHeight; //may undefined
		return hgh;
	},
	/**
	 * Retrieves the index of the object in the cells collection of a row.
	 * Note: The function fixed the problem of IE that the cell.cellIndex returns a wrong index 
	 * if there is a hidden cell in the table. So, the behavior is difference among others.
	 * @param {Element} cell
	 */
	cellIndex: function (cell) {
		var i = 0; 
		if (zk.ie) {
			var cells = cell.parentNode.cells;
			for(var j = 0, cl = cells.length; j < cl; j++) {
				if (cells[j] == cell) {
					i = j;
					break;
				}
			}
		} else i = cell.cellIndex;
		return i; 
	},
	/** Returns the number of columns (considering colSpan)
	 */
	ncols: function (cells) {
		var cnt = 0;
		if (cells) {
			for (var j = 0, cl = cells.length; j < cl; ++j) {
				var span = cells[j].colSpan;
				if (span >= 1) cnt += span;
				else ++cnt;
			}
		}
		return cnt;
	},
	/** Converts from absolute coordination to style's coordination.
	 */
	toStyleOffset: function (el, x, y) {
		var oldx = el.style.left, oldy = el.style.top,
			resetFirst = zk.opera || zk.air;
		//Opera:
		//1)we have to reset left/top. Or, the second call position wrong
		//test case: Tooltips and Popups
		//2)we cannot assing "", either
		//test case: menu
		//IE/gecko fix: auto causes toStyleOffset incorrect
		if (resetFirst || el.style.left == "" || el.style.left == "auto")
			el.style.left = "0";
		if (resetFirst || el.style.top == "" || el.style.top == "auto")
			el.style.top = "0";

		var ofs1 = zDom.cmOffset(el);
		var x2 = zk.parseInt(el.style.left), y2 = zk.parseInt(el.style.top);
		ofs1 = [x - ofs1[0] + x2, y  - ofs1[1] + y2];

		el.style.left = oldx; el.style.top = oldy; //restore
		return ofs1;
	},
	/** Center the specified element.
	 * @param flags a combination of center, left, right, top and bottom.
	 * If omitted, center is assigned.
	 */
	center: function (el, flags) {
		var wdgap = zDom.offsetWidth(el),
			hghgap = zDom.offsetHeight(el);

		if ((!wdgap || !hghgap) && !zDom.isVisible(el)) {
			el.style.top = "-10000px"; //avoid annoying effect
			el.style.display = "block"; //we need to calculate the size
			wdgap = zDom.offsetWidth(el);
			hghgap = zDom.offsetHeight(el),
			el.style.display = "none"; //avoid Firefox to display it too early
		}

		var left = zDom.innerX(), top = zDom.innerY();
		var x, y, skipx, skipy;

		wdgap = zDom.innerWidth() - wdgap;
		if (!flags) x = left + wdgap / 2;
		else if (flags.indexOf("left") >= 0) x = left;
		else if (flags.indexOf("right") >= 0) x = left + wdgap - 1; //just in case
		else if (flags.indexOf("center") >= 0) x = left + wdgap / 2;
		else {
			x = 0; skipx = true;
		}

		hghgap = zDom.innerHeight() - hghgap;
		if (!flags) y = top + hghgap / 2;
		else if (flags.indexOf("top") >= 0) y = top;
		else if (flags.indexOf("bottom") >= 0) y = top + hghgap - 1; //just in case
		else if (flags.indexOf("center") >= 0) y = top + hghgap / 2;
		else {
			y = 0; skipy = true;
		}

		if (x < left) x = left;
		if (y < top) y = top;

		var ofs = zDom.toStyleOffset(el, x, y);	

		if (!skipx) el.style.left = ofs[0] + "px";
		if (!skipy) el.style.top =  ofs[1] + "px";
	},
	position: function (el, dim, where, opts) {
		where = where || "overlap";
		var box = zDom.getDimension(el),
			wd = box.width,
			hgh = box.height,
			x = dim.top,
			y = dim.left;
			
		switch(where) {
		case "before_start":
			y -= hgh;
			break;
		case "before_end":
			y -= hgh;
			x += dim.width - wd;
			break;
		case "after_start":
			y += dim.height;
			break;
		case "after_end":
			y += dim.height;
			x += dim.width - wd;
			break;
		case "start_before":
			x -= wd;
			break;
		case "start_after":
			x -= wd;
			y += dim.height - hgh;
			break;
		case "end_before":
			x += dim.width;
			break; 
		case "end_after":
			x += dim.width;
			y += dim.height - hgh;
			break;
		case "at_pointer":
			var offset = zk.lastPointer;
			x = offset[0];
			y = offset[1];
			break;
		case "after_pointer":
			var offset = zk.lastPointer;
			x = offset[0];
			y = offset[1] + 20;
			break;
		default: // overlap is assumed
			// nothing to do.
		}

		if (!opts || !opts.overflow) {
			var scX = zDom.innerX(),
				scY = zDom.innerY(),
				scMaxX = scX + zDom.innerWidth(),
				scMaxY = scY + zDom.innerHeight();

			if (x + wd > scMaxX) x = scMaxX - wd;
			if (x < scX) x = scX;
			if (y + hgh > scMaxY) y = scMaxY - hgh;
			if (y < scY) y = scY;
		}
	
		box = zDom.toStyleOffset(el, x, y);
		el.style.left = box[0] + "px";
		el.style.top = box[1] + "px";
	},

	/** Calculates the cumulative scroll offset of an element in nested scrolling containers.
	 * Adds the cumulative scrollLeft and scrollTop of an element and all its parents.
	 * Used for calculating the scroll offset of an element that is in more than one scroll container (e.g., a draggable in a scrolling container which is itself part of a scrolling document).
	 * Note that all values are returned as numbers only although they are expressed in pixels.
	 */
	scrollOffset: function(el) {
		var t = 0, l = 0, tag = zDom.tag(el);
		do {
			//Fix opera bug (see the page function)
			// If tag is "IMG" or "TR", the "DIV" element's scrollTop should be ignored.
			// Because the offsetTop of element "IMG" or "TR" is excluded its scrollTop.  
			var t2 = zDom.tag(el);
			if (!zk.opera || t2 == 'BODY'
			|| (tag != "TR" && tag != "IMG"  && t2 == 'DIV')) { 
				t += el.scrollTop  || 0;
				l += el.scrollLeft || 0;
			}
			el = el.parentNode;
		} while (el);
		return [l, t];
	},
	cmOffset: function (el) {
		//fix safari's bug: TR has no offsetXxx
		if (zk.safari && zDom.tag(el) === "TR" && el.cells.length)
			el = el.cells[0];

		//fix gecko and safari's bug: if not visible before, offset is wrong
		if (!(zk.gecko || zk.safari)
		|| zDom.isVisible(el) || zDom.offsetWidth(el))
			return zDom._cmOffset(el);

		el.style.display = "";
		var ofs = zDom._cmOffset(el);
		el.style.display = "none";
		return ofs;
	},
	_cmOffset: function (el) {
		var t = 0, l = 0, operaBug;
		//Fix gecko difference, the offset of gecko excludes its border-width when its CSS position is relative or absolute
		if (zk.gecko) {
			var p = el.parentNode;
			while (p && p != document.body) {
				var style = zDom.getStyle(p, "position");
				if (style == "relative" || style == "absolute") {
					t += zk.parseInt(zDom.getStyle(p, "border-top-width"));
					l += zk.parseInt(zDom.getStyle(p, "border-left-width"));
				}
				p = p.offsetParent;
			}
		}

		do {
			//Bug 1577880: fix originated from http://dev.rubyonrails.org/ticket/4843
			if (zDom.getStyle(el, "position") == 'fixed') {
				t += zk.innerY() + el.offsetTop;
				l += zk.innerX() + el.offsetLeft;
				break;
			} else {
				//Fix opera bug. If the parent of "INPUT" or "SPAN" is "DIV" 
				// and the scrollTop of "DIV" is more than 0, the offsetTop of "INPUT" or "SPAN" always is wrong.
				if (zk.opera) { 
					if (operaBug && el.nodeName == "DIV" && el.scrollTop != 0)
						t += el.scrollTop || 0;
					operaBug = el.nodeName == "SPAN" || el.nodeName == "INPUT";
				}
				t += el.offsetTop || 0;
				l += el.offsetLeft || 0;
				//Bug 1721158: In FF, el.offsetParent is null in this case
				el = zk.gecko && el != document.body ?
					zDom.offsetParent(el): el.offsetParent;
			}
		} while (el);
		return [l, t];
	},

	isOverlapped: function (ofs1, dim1, ofs2, dim2) {
		var o1x1 = ofs1[0], o1x2 = dim1[0] + o1x1,
			o1y1 = ofs1[1], o1y2 = dim1[1] + o1y1;
		var o2x1 = ofs2[0], o2x2 = dim2[0] + o2x1,
			o2y1 = ofs2[1], o2y2 = dim2[1] + o2y1;
		return o2x1 <= o1x2 && o2x2 >= o1x1 && o2y1 <= o1y2 && o2y2 >= o1y1;
	},

	/** Make the position of the element as absolute. */
	absolutize: function(el) {
		if (el.style.position == 'absolute') return;

		var offsets = zDom._posOffset(el),
			left = offsets[0], top = offsets[1],
			st = el.style;
		/* Bug 1591389
		var width   = el.clientWidth;
		var height  = el.clientHeight;
		*/

		el._$orgLeft = left - parseFloat(st.left  || 0);
		el._$orgTop = top  - parseFloat(st.top || 0);
		/* Bug 1591389
		el._$orgWd = st.width;
		el._$orgHgh = st.height;
		*/
		st.position = 'absolute';
		st.top = top + 'px';
		st.left = left + 'px';
		/* Bug 1591389
		st.width = width + 'px';
		st.height = height + 'px';
		*/
	},
	_posOffset: function(el) {
		if (zk.safari && zDom.tag(el) === "TR" && el.cells.length)
			el = el.cells[0];

		var t = 0, l = 0;
		do {
			t += el.offsetTop  || 0;
			l += el.offsetLeft || 0;
			//Bug 1721158: In FF, el.offsetParent is null in this case
			el = zk.gecko && el != document.body ?
				zDom.offsetParent(el): el.offsetParent;
			if (el) {
				if(el.tagName=='BODY') break;
				var p = zDom.getStyle(el, 'position');
				if (p == 'relative' || p == 'absolute') break;
			}
		} while (el);
		return [l, t];
	},
	/** Make the position of the element as relative. */
	relativize: function(el) {
		if (el.style.position == 'relative') return;

		var st = el.style;
		st.position = 'relative';
		var top  = parseFloat(st.top  || 0) - (el._$orgTop || 0),
			left = parseFloat(st.left || 0) - (el._$orgLeft || 0);

		st.top = top + 'px';
		st.left = left + 'px';
		/* Bug 1591389
		st.height = el._$orgHgh;
		st.width = el._$orgWd;
		*/
	},

	/** Returns the offset parent. */
	offsetParent: function (el) {
		if (el.offsetParent) return el.offsetParent;
		if (el == document.body) return el;

		while ((el = el.parentNode) && el != document.body)
			if (el.style && zDom.getStyle(el, 'position') != 'static') //in IE, style might not be available
				return el;

		return document.body;
	},
	/** Return element's offsetWidth, which solving Safari's bug.
	 * Meaningful only if element is TR).
	 */
	offsetWidth: function (el) {
		if (!el) return 0;
		if (!zk.safari || zDom.tag(el) != "TR") return el.offsetWidth;

		var wd = 0;
		for (var cells = el.cells, j = cells.length; --j >= 0;)
			wd += cells[j].offsetWidth;
		return wd;
	},
	/** Return element.offsetHeight, which solving Safari's bug. */
	offsetHeight: function (el) {
		if (!el) return 0;
		if (!zk.safari || zDom.tag(el) != "TR") return el.offsetHeight;

		var hgh = 0;
		for (var cells = el.cells, j = cells.length; --j >= 0;) {
			var h = cells[j].offsetHeight;
			if (h > hgh) hgh = h;
		}
		return hgh;
	},
	/** Returns el.offsetTop, which solving Safari's bug. */
	offsetTop: function (el) {
		if (!el) return 0;
		if (zk.safari && zDom.tag(el) === "TR" && el.cells.length)
			el = el.cells[0];
		return el.offsetTop;
	},
	/** Returns el.offsetLeft, which solving Safari's bug. */
	offsetLeft: function (el) {
		if (!el) return 0;
		if (zk.safari && zDom.tag(el) === "TR" && el.cells.length)
			el = el.cells[0];
		return el.offsetLeft;
	},

	/* Returns the X/Y coordinates of element relative to the viewport. */
	viewportOffset: function(el) {
		var t = 0, l = 0, p = el;
		do {
			t += p.offsetTop  || 0;
			l += p.offsetLeft || 0;

			// Safari fix
			if (p.offsetParent==document.body)
			if (zDom.getStyle(p, 'position')=='absolute') break;
	
		} while (p = p.offsetParent);

		do {
			if (!zk.opera || el.tagName=='BODY') {
				t -= el.scrollTop  || 0;
				l -= el.scrollLeft || 0;
			}
		} while (el = el.parentNode);
		return [l, t];
	},
	/** Returns an array to indicate the size of the text if it is placed
	 * inside the element.
	 */
	getTextSize: function (el, txt) {
		var tsd = zk._txtSizDiv;
		if (!tsd) {
			tsd = zk._txtSizDiv = document.createElement("DIV");
			tsd.style.cssText = "left:-1000px;position:absolute;visibility:hidden;border:none";
			document.body.appendChild(tsd);
		}

		for (var ss = zk.TEXT_STYLES, j = ss.length; --j >= 0;)
			tsd.style[ss[j]] = Element.getStyle(el, ss[j]);

		tsd.innerHTML = txt;
		return [tsd.offsetWidth, tsd.offsetHeight];
	},
	//refer to http://www.w3schools.com/css/css_text.asp
	TEXT_STYLES: [
		'fontFamily', 'fontSize', 'fontWeight', 'fontStyle',
		'letterSpacing', 'lineHeight', 'textAlign', 'textDecoration',
		'textIndent', 'textShadow', 'textTransform', 'textOverflow',
		'direction', 'wordSpacing', 'whiteSpace'],

	getDimension: function (el) {
		var display = zDom.getStyle(el,  'display');
		if (display != 'none' && display != null) // Safari bug
			return {width: zDom.offsetWidth(el), height: zDom.offsetHeight(el),
				top: zDom.offsetTop(el), left: zDom.offsetLeft(el)};

	// All *Width and *Height properties give 0 on elements with display none,
	// so enable the el temporarily
		var st = el.style,
			originalVisibility = st.visibility,
			originalPosition = st.position,
			originalDisplay = st.display;
		st.visibility = 'hidden';
		st.position = 'absolute';
		st.display = 'block';
		var originalWidth = el.clientWidth,
			originalHeight = el.clientHeight,
			originalTop = el.offsetTop,
			originalLeft = el.offsetLeft;
		st.display = originalDisplay;
		st.position = originalPosition;
		st.visibility = originalVisibility;
		return {width: originalWidth, height: originalHeight,
			top: originalTop, left: originalLeft};
	},

	//class and style//
	/** Returns whether it is part of the class name
	 * of the specified element.
	 */
	hasClass: function (el, clsnm) {
		var cn = el ? el.className: '';
		return cn && (' '+cn+' ').indexOf(' '+clsnm+' ') != -1;
	},
	/** Adds the specified class name to the class name of the specified element.
	 */
	addClass: function (el, clsnm) {
		if (el && !zDom.hasClass(el, clsnm)) {
			var cn = el.className;
			if (cn.length) cn += ' ';
			el.className = cn + clsnm;
		}
	},
	/** Removes the specified class name from the the class name of the specified
	 * element.
	 */
	rmClass: function (el, clsnm) {
		if (el && zDom.hasClass(el, clsnm)) {
			var re = new RegExp('(?:^|\\s+)' + clsnm + '(?:\\s+|$)', "g");
			el.className = el.className.replace(re, " ").trim();
		}
	},
	
	_txtstyles: ["color", "background-color", "background",	"white-space"],
	
	/** Returns the text-relevant style (same as HTMLs.getTextRelevantStyle).
	 * @param incwd whether to include width
	 * @param inchgh whether to include height
	 */
	getTextStyle: function (style, incwd, inchgh) {
		var ts = "";
		for (var j = 0, k = 0; k >= 0; j = k + 1) {
			k = style.indexOf(';', j);
			var s = k >= 0 ? style.substring(j, k): style.substring(j);
			var l = s.indexOf(':');
			var nm = l < 0 ? s.trim(): s.substring(0, l).trim();
	
			if (nm.startsWith("font")  || nm.startsWith("text")
			|| zDom._txtstyles.$contains(nm)
			|| (incwd && nm == "width") || (inchgh && nm == "height"))
				ts += s + ';';
		}
		return ts;
	},

	/** Returns the style. In addition to n.style, it also
	 * checked CSS styles that are applicated to the specified element.
	 */
	getStyle: function(el, style) {
		var st = el.style;
		if (['float','cssFloat'].$contains(style))
			style = (typeof st.styleFloat != 'undefined' ? 'styleFloat' : 'cssFloat');
		style = style.$camel();
		var value = st[style];
		if (!value) {
			if (document.defaultView && document.defaultView.getComputedStyle) {
				var css = document.defaultView.getComputedStyle(el, null);
				value = css ? css[style] : null;
			} else if (el.currentStyle) {
				value = el.currentStyle[style];
			}
		}

		if (value == 'auto' && ['width','height'].$contains(style)
		&& zDom.getStyle(el, 'display') != 'none')
			value = el['offset'+style.capitalize()] + 'px';

		if (zk.opera && ['left', 'top', 'right', 'bottom'].$contains(style)
		&& zDom.getStyle(el, 'position') == 'static') value = 'auto';

		if(style == 'opacity') {
			if(value) return parseFloat(value);
			if(value = (zDom.getStyle(el, 'filter') || '').match(/alpha\(opacity=(.*)\)/)
			&& value[1]) return parseFloat(value[1]) / 100;
			return 1.0;
		}
		return value == 'auto' ? null : value;
	},
	/** Sets the style.
	 * @param style a map of styles to update (String name, String value).
	 */
	setStyle: function(el, style) {
		var st = el.style;
		for (var name in style) {
			var value = style[name];
			if(name == 'opacity') {
				if (value == 1) {
					value = (/gecko/.test(zk.userAgent) &&
						!/konqueror|safari|khtml/.test(zk.userAgent)) ? 0.999999 : 1.0;
					if(zk.ie)
						st.filter = zDom.getStyle(el, 'filter').replace(/alpha\([^\)]*\)/gi,'');
				} else if(value === '') {
					if(zk.ie)
						st.filter = zDom.getStyle(el, 'filter').replace(/alpha\([^\)]*\)/gi,'');
				} else {
					if(value < 0.00001) value = 0;
					if(zk.ie)
						st.filter = zDom.getStyle(el, 'filter').replace(/alpha\([^\)]*\)/gi,'') +
							'alpha(opacity='+value*100+')';
				}
			} else if(['float','cssFloat'].$contains(name))
				name = (typeof st.styleFloat != 'undefined') ? 'styleFloat' : 'cssFloat';

			st[name.$camel()] = value;
		}
	},
	/** Parses a string-type style into a map of styles
	 * that can be used with {@link #setStyle}.
	 */
	parseStyle: function (style) {
		var map = {};
		if (style) {
			var pairs = style.split(';');
			for (var j = 0, len = pairs.length; j < len;) {
				var v = pairs[j++].split(':'),
					nm = v.length > 0 ? v[0].trim(): '';
				if (nm)
					map[nm] = v.length > 1 ? v[1].trim(): '';
			}
		}
		return map;
	},
	/** Returns the opacity style of the specified element, including CSS class. */
	getOpacity: function (el) {
		return zDom.getStyle(el, 'opacity');
	},
	/** Sets the opacity style of the specified element. */
	setOpacity: function(el, value){
		zDom.setStyle(el, {opacity:value});
	},

	/** Forces the browser to redo the CSS by adding and removing a CSS class. */
	redoCSS: function (el, timeout) {
		el = zDom.$(el);
		if (el) {
			try {
				el.className += ' ';
				el.className.trim();
			} catch (e) {
			}
		}
	},
	/** Redraws the element by use of setOuterHTML. */
	reOuter: function (el) {
		el = zDom.$(el);
		if (el)
			zDom.setOuterHTML(el, zDom.getOuterHTML(el));
	},

	cleanWhitespace: function (el) {
		for (var node = el.firstChild; node;) {
			var nextNode = node.nextSibling;
			if (node.nodeType == 3 && !/\S/.test(node.nodeValue))
				el.removeChild(node);
			node = nextNode;
		}
	},

	makePositioned: function (el) {
		var pos = zDom.getStyle(el,  'position');
		if (pos == 'static' || !pos) {
			el._$positioned = true;
			el.style.position = 'relative';
			// Opera returns the offset relative to the positioning context, when an
			// element is position relative but top and left have not been defined
			if (zk.opera)
				el.style.top = el.style.left = 0;
		}
	},
	undoPositioned: function (el) {
		if (el._$positioned) {
			el._$positioned = undefined;
			var st = el.style;
			st.position = st.top = st.left = st.bottom = st.right = '';
		}
	},
	makeClipping: function (el) {
		if (!el._$clipping) {
			var st = el.style;
			el._$clipping = true;
			el._$overflow = st.overflow;
			el._$overflowX = st.overflowX;
			el._$overflowY = st.overflowY;
			if (zDom.getStyle(el, 'overflow') != 'hidden')
				st.overflow = 'hidden';
		}
	},
	undoClipping: function (el) {
		if (el._$clipping) {
		//Bug 1822717 and 1882277
			var st = el.style;
			st.overflow = el._$overflow;
			st.overflowX = el._$overflowX;
			st.overflowY = el._$overflowY;
			el._$clipping = el._$overflow = el._$overflowX = el._$overflowY = undefined;
		}
	},

	getOuterHTML: function (el) {
		if (el.outerHTML) return el.outerHTML;
		var div = document.createElement("DIV");
		var clone = el.cloneNode(true);
		div.appendChild(clone);
		return div.innerHTML;
	},
	/** Replaces the outer of the specified element with the HTML content.
	 * @return the new node (actually the first new node, if multiple)
	 */
	setOuterHTML: function(n, html) {
		n = zDom.$(n);
		var parent = n.parentNode, sib = n.previousSibling;

		zDom.unfixDom(n); //undo fix of browser issues

		if (zk.ie) {
			var tn = zDom.tag(n);
			if (tn == "TD" || tn == "TH" || tn == "TABLE" || tn == "TR"
			|| tn == "CAPTION" || tn == "TBODY" || tn == "THEAD"
			|| tn == "TFOOT" || tn == "COLGROUP" || tn == "COL") {
				var ns = zDom._tblNewElems(html);
				var nsib = n.nextSibling;
				parent.removeChild(n);

				for (var j = 0, len = ns.length; j < len; ++j)
					if (nsib) parent.insertBefore(ns[j], nsib);
					else parent.appendChild(ns[j]);
			} else
				n.outerHTML = html;
		} if (n.outerHTML)
			n.outerHTML = html;
		else if (zk.gecko2Only && zDom.tag(n) == 'LEGEND') {
			//A dirty fix (not work if html is not LEGEND
			n.innerHTML = zDom._removeOuter(html);
		} else { //non-IE
			var range = n.ownerDocument.createRange();
			range.selectNodeContents(n);
			parent.replaceChild(range.createContextualFragment(html), n);
		}

		if (!html) n = null;
		else if (sib) n = sib.nextSibling;
		else n = parent.firstChild;

		zDom.fixDom(n);  //fix browser issues

		/* Turn it on if need to fix this limitation (about script)
		if (n && !zk.gecko && n.getElementsByTagName) {
			//ie/safari/opera doesn't run script in it, so eval manually
			var ns = n.getElementsByTagName("SCRIPT");
			for (var j = 0, len = ns.length; j < len; ++j)
				eval(ns[j].text);
		}*/
		return n;
	},
	_removeOuter: function (html) {
		var j = html.indexOf('>') + 1, k = html.lastIndexOf('<');
		return k >= j ? html.substring(j, k): html;
	},
	/** Inserts an unparsed HTML immediately before the specified element.
	 * @param el the sibling before which to insert
	 */
	insertHTMLBefore: function (el, html) {
		if (zk.ie) {
			switch (zDom.tag(el)) { //exclude TABLE
			case "TD": case "TH": case "TR": case "CAPTION": case "COLGROUP":
			case "TBODY": case "THEAD": case "TFOOT":
				var ns = zDom._tblNewElems(html);
				var p = el.parentNode;
				for (var j = 0, nl = ns.length; j < nl; ++j)
					p.insertBefore(ns[j], el);
				return;
			}
		}
		el.insertAdjacentHTML('beforeBegin', html);
	},
	/** Inserts an unparsed HTML immediately before the ending element.
	 */
	insertHTMLBeforeEnd: function (el, html) {
		if (zk.ie) {
			var tn = zDom.tag(el);
			switch (tn) {
			case "TABLE": case "TR":
			case "TBODY": case "THEAD": case "TFOOT": case "COLGROUP":
			/*case "TH": case "TD": case "CAPTION":*/ //no need to handle them
				var ns = zDom._tblNewElems(html);
				if (tn == "TABLE" && ns.length && zDom.tag(ns[0]) == "TR") {
					var bd = el.tBodies;
					if (!bd || !bd.length) {
						bd = document.createElement("TBODY");
						el.appendChild(bd);
						el = bd;
					} else {
						el = bd[bd.length - 1];
					}
				}
				for (var j = 0, nl = ns.length; j < nl; ++j)
					el.appendChild(ns[j]);
				return;
			}
		}
		el.insertAdjacentHTML("beforeEnd", html);
	},
	/** Inserts an unparsed HTML immediately after the specified element.
	 * @param el the sibling after which to insert
	 */
	insertHTMLAfter: function (el, html) {
		if (zk.ie) {
			switch (zDom.tag(el)) { //exclude TABLE
			case "TD": case "TH": case "TR": case "CAPTION":
			case "TBODY": case "THEAD": case "TFOOT":
			case "COLGROUP": case "COL":
				var ns = zDom._tblNewElems(html);
				var sib = el.nextSibling;
				var p = el.parentNode;
				for (var j = 0, nl = ns.length; j < nl; ++j)
					if (sib != null) p.insertBefore(ns[j], sib);
					else p.appendChild(ns[j]);
				return;
			}
		}
		el.insertAdjacentHTML('afterEnd', html);
	},

	/** Inserts a node after another.
	 */
	insertAfter: function (el, ref) {
		var sib = ref.nextSibling;
		if (sib) ref.parentNode.insertBefore(el, sib);
		else ref.parentNode.appendChild(el);
	},
	/** Inserts a node before another.
	 */
	insertBefore: function (el, ref) {
		ref.parentNode.insertBefore(el, ref);
	},
	/** Detaches an element.
	 * @param n the element, or the element's ID.
	 */
	remove: function (n) {
		n = zDom.$(n);
		if (n && n.parentNode) n.parentNode.removeChild(n);
	},

	/** Appends a JavaScript node.
	 * @param charset the charset. UTF-8 is assumed if omitted.
	 */
	appendScript: function (src, charset) {
		var e = document.createElement("SCRIPT");
		e.type = "text/javascript";
		e.charset = charset ? charset: "UTF-8";
		e.src = src;
		document.getElementsByTagName("HEAD")[0].appendChild(e);
	},

	/** Returns the next sibling with the specified tag name, or null if not found.
	 */
	nextSibling: function (el, tagName) {
		while (el && (el = el.nextSibling) != null && zDom.tag(el) != tagName)
			;
		return el;
	},
	/** Returns the next sibling with the specified tag name, or null if not found.
	 */
	previousSibling: function (el, tagName) {
		while (el && (el = el.previousSibling) != null && zDom.tag(el) != tagName)
			;
		return el;
	},
	firstChild: function (el, tagName, descendant) {
		for (var n = el.firstChild; n; n = n.nextSibling)
			if (zDom.tag(n) == tagName)
				return n;

		if (descendant)
			for (var n = el.firstChild; n; n = n.nextSibling) {
				var chd = zDom.firstChild(n, tagName, descendant);
				if (chd) return chd;
			}
		return null;
	},
	lastChild: function (el, tagName, descendant) {
		for (var n = el.lastChild; n; n = n.previousSibling)
			if (zDom.tag(n) == tagName)
				return n;
	
		if (descendant) {
			for (var n = el.lastChild; n; n = n.previousSibling) {
				var chd = zDom.lastChild(n, tagName, descendant);
				if (chd)
					return chd;
			}
		}
		return null;
	},

	/** Returns the parent node including the virtual parent. */
	parentNode: function (el) {
		return el.z_vp || el.parentNode;
	},
	parentByTag: function (el, tagName) {
		for (; el; el = zDom.parentNode(el))
			if (zDom.tag(el) == tagName)
				return el;
		return null;
	},
	vparent: function (el) {
		return el.z_vp;
	},
	/**Position an element able to apear above others.
	 * It doesn't change style.position (which is caller's job).
	 * Rather, it changes its parent to document.body.
	 * Remember to call {@link #undoVParent} (at least, in {@link #unbind_})
	 * if you called this method.
	 */
	makeVParent: function (el) {
		if (el.z_vp) return; //called twice

		var sib = el.nextSibling,
			p = el.parentNode,
			agtx = el.z_vpagtx = document.createElement("SPAN");
		agtx.style.display = "none";
		if (sib) p.insertBefore(agtx, sib);
		else p.appendChild(agtx);

		el.z_vp = p;
		document.body.appendChild(el);
	},
	undoVParent: function (el) {
		var p = el.z_vp;
		if (p) {
			var agtx = el.z_vpagtx;
			el.z_vp = el.z_vpagtx = null;
			if (agtx) {
				p.insertBefore(el, agtx);
				zDom.remove(agtx);
			} else
				p.appendChild(el);
		}
	},

	/**
	 * Creates a 'stackup' (actually, an iframe) that makes
	 * an element (with position:absolute) shown above others.
	 * The stackup is used to resolve the layer issues:
	 * <ul>
	 * <li>IE6: SELECT's dropdown above any other DOM element</li>
	 * <li>All browser: PDF iframe above any other DOM element.
	 * However, this approach works only in FF and IE.</li<
	 * </ul>
	 * @param el the element to retrieve the dimensions.
	 * If omitted, the stackup is not appended to the DOM tree.
	 * @param id ID of the iframe. If omitted and el is specified,
	 * it is el.id + '$ifrstk'. If both el and id are omitted, 'z_ifrstk'
	 * is assumed.
	 * @param anchor whether to insert the DOM element before.
	 * If omitted, el is assumed.
	 */
	makeStackup: function (el, id, anchor) {
		var ifr = document.createElement('iframe');
		ifr.id = id || (el ? el.id + "$ifrstk": 'z_ifrstk');
		ifr.style.cssText = "position:absolute;overflow:hidden;filter:alpha(opacity=0)";
		ifr.frameBorder = "no";
		ifr.tabIndex = -1;
		ifr.src = "";
		if (el) {
			ifr.style.width = el.offsetWidth + "px";
			ifr.style.height = el.offsetHeight + "px";
			ifr.style.top = el.style.top;
			ifr.style.left = el.style.left;
			el.parentNode.insertBefore(ifr, anchor || el);
		}
		return ifr;
	},

	//dialog//
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

	//focus/select//
	/** Focus to the specified component w/o throwing exception.
	 * @return whether focus is allowed. Currently, it accepts only
	 * BUTTON, INPUT, SELECT and IFRAME.
	 */
	focus: function (n, timeout) {
		n = zDom.$(n);
		if (!n || !n.focus) return false;
			//ie: INPUT's focus not function

		var tag = zDom.tag(n);
		if (tag != 'BUTTON' && tag != 'INPUT' && tag != 'SELECT'
		&& tag != 'IFRAME') return false;

		if (timeout >= 0) setTimeout(function() {zDom._focus(n);}, timeout);
		else zDom._focus(n);
		return true;
	},
	_focus: function (n) {
		try {
			n.focus();
		} catch (e) {
			setTimeout(function() {
				try {
					n.focus();
				} catch (e) {
					setTimeout(function() {try {n.focus();} catch (e) {}}, 100);
				}
			}, 0);
		} //IE throws exception if failed to focus in some cases
	},

	/** Select to the specified component w/o throwing exception. */
	select: function (n, timeout) {
		n = zDom.$(n);
		if (!n || typeof n.select != 'function') return false;

		if (timeout >= 0) setTimeout(function() {zDom._select(n);}, timeout);
		else zDom._select(n);
		return true;
	},
	_select: function (n) {
		try {
			n.select();
		} catch (e) {
			setTimeout(function() {
				try {n.select();} catch (e) {}
			}, 0);
		} //IE throws exception when select() in some cases
	},
	/** Returns the selection range of the specified input control.
	 * Note: if the function occurs some error, it always return [0, 0];
	 */
	getSelectionRange: function(inp) {
		try {
			if (document.selection != null && inp.selectionStart == null) { //IE
				var range = document.selection.createRange(); 
				var rangetwo = inp.createTextRange(); 
				var stored_range = ""; 
				if(inp.type.toLowerCase() == "text"){
					stored_range = rangetwo.duplicate();
				}else{
					 stored_range = range.duplicate(); 
					 stored_range.moveToElementText(inp); 
				}
				stored_range.setEndPoint('EndToEnd', range); 
				var start = stored_range.text.length - range.text.length;
				return [start, start + range.text.length];
			} else { //Gecko
				return [inp.selectionStart, inp.selectionEnd];
			}
		} catch (e) {
			return [0, 0];
		}
	},
	setSelectionRange: function (inp, start, end) {
		var len = inp.value.length;
		if (start < 0) start = 0;
		if (start > len) start = len;
		if (end < 0) end = 0;
		if (end > len) end = len;
	
		if (inp.setSelectionRange) {
			inp.setSelectionRange(start, end);
			inp.focus();
		} else if (inp.createTextRange) {
			var range = inp.createTextRange();
			if(start != end){
				range.moveEnd('character', end - range.text.length);
				range.moveStart('character', start);
			}else{
				range.move('character', start);
			}
			range.select();
		}
	},

	//selection//
	clearSelection: function () {
		try{
			if (window["getSelection"]) { 
				if (zk.safari) window.getSelection().collapse();
				else window.getSelection().removeAllRanges();
			} else if (document.selection) {
				if (document.selection.empty) document.selection.empty();
				else if (document.selection.clear) document.selection.clear();
			}
			return true;
		} catch (e){
			return false;
		}
	},
	/** Disable whether the specified element is selectable. */
	disableSelection: function (el) {
		el = zDom.$(el);
		if (el)
			if (zk.gecko)
				el.style.MozUserSelect = "none";
			else if (zk.safari)
				el.style.KhtmlUserSelect = "none"; 
			else if (zk.ie)
				el.onselectstart = function (evt) {
					var n = zEvt.target(evt), tag = zDom.tag(n);
					return tag == "TEXTAREA" || tag == "INPUT" && (n.type == "text" || n.type == "password");
				};
	},
	/** Enables whether the specified element is selectable. */
	enableSelection: function (el) {
		el = zDom.$(el);
		if (el)
			if (zk.gecko)
				el.style.MozUserSelect = ""; 
			else if (zk.safari)
				el.style.KhtmlUserSelect = "";
			else if (zk.ie)
				el.onselectstart = null;
	}
};

if (zk.ie) {
  zk.copy(zDom, {
	//fix TABLE issue
	_tagOfHtml: function (html) {
		if (!html) return "";

		var j = html.indexOf('<') + 1, k = j, len = j ? html.length: 0;
		for (; k < len; ++k) {
			var cc = html.charAt(k);
			if (cc == '>' || zk.isWhitespace(cc))
				return html.substring(j, k).toUpperCase();
		}
		throw "Unknown tag in "+html;
	},
	_tblNewElems: function (html) {
		var level, tag = zDom._tagOfHtml(html);
		switch (tag) {
		case "TABLE":
			level = 0;
			break;
		case "TR":
			level = 2;
			html = '<table>' + html + '</table>';
			break;
		case "TH": case "TD":
			level = 3;
			html = '<table><tr>' + html + '</tr></table>';
			break;
		case "COL":
			level = 2;
			html = '<table><colgroup>'+html+'</colgroup></table>';
			break;
		default://case "THEAD": case "TBODY": case "TFOOT": case "CAPTION": case "COLGROUP":
			level = 1;
			html = '<table>' + html + '</table>';
			break;
		}

		//get the correct node
		var el = document.createElement('DIV');
		el.innerHTML = html;
		while (--level >= 0)
			el = el.firstChild;

		//detach from parent and return
		var ns = [];
		for (var n; n = el.firstChild;) {
			//IE creates extra tbody if add COLGROUP
			//However, the following skip is dirty-fix, assuming html doesn't
			//contain TBODY (unless it is the first tag)
			var nt = zDom.tag(n);
			if (nt == tag || nt != "TBODY")
				ns.push(n);
			el.removeChild(n);
		}
		return ns;
	}
  });
} else if (!HTMLElement.prototype.insertAdjacentHTML) { //none-IE
	//insertAdjacentHTML
	HTMLElement.prototype.insertAdjacentHTML = function (sWhere, sHTML) {
		var r = this.ownerDocument.createRange(), df;

		switch (String(sWhere).toLowerCase()) {  // convert to string and unify case
		case "beforebegin":
			r.setStartBefore(this);
			df = r.createContextualFragment(sHTML);
			this.parentNode.insertBefore(df, this);
			break;

		case "afterbegin":
			r.selectNodeContents(this);
			r.collapse(true);
			df = r.createContextualFragment(sHTML);
			this.insertBefore(df, this.firstChild);
			break;

		case "beforeend":
			r.selectNodeContents(this);
			r.collapse(false);
			df = r.createContextualFragment(sHTML);
			this.appendChild(df);
			break;

		case "afterend":
			r.setStartAfter(this);
			df = r.createContextualFragment(sHTML);
			zDom.insertAfter(df, this);
			break;
		}
	};
}

//fix DOM
zk.copy(zDom,
  zk.ie ? {
	fixDom: function (n) {
		if (n) {
			zDom._fxns.push(n);
			setTimeout(zDom._fixDom, 100);
		}
	},
	unfixDom: function (n) {
		if (n && !zDom._fxns.$remove(n))
			setTimeout(function() {zDom._unfixDom(n);}, 1000);
	},
	_fxns: [], //what to fix
	_fixDom: function () {
		var n = zDom._fxns.shift();
		if (n) {
			zDom._fixBU(n.getElementsByTagName("A")); //Bug 1635685, 1612312
			zDom._fixBU(n.getElementsByTagName("AREA")); //Bug 1896749

			if (zDom._fxns.length) setTimeout(zDom._fixDom, 300);
		}
	},
	_unfixDom: function (n) {
		if (n) {
			zDom._unfixBU(n.getElementsByTagName("A"));
			zDom._unfixBU(n.getElementsByTagName("AREA"));
		}
	},
	_fixBU: function (ns) {
		for (var j = ns.length; --j >= 0;) {
			var n = ns[j];
			if (!n.z_fixed && n.href.indexOf("javascript:") >= 0) {
				n.z_fixed = true;
				zEvt.listen(n, "click", zDom._doSkipBfUnload);
			}
		}
	},
	_unfixBU: function (ns) {
		for (var j = ns.length; --j >= 0;) {
			var n = ns[j];
			if (n.z_fixed) {
				n.z_fixed = false;
				zEvt.unlisten(n, "click", zDom._doSkipBfUnload);
			}
		}
	},
	_doSkipBfUnload: function () {
		zk.skipBfUnload = true;
		setTimeout(zDom._unSkipBfUnload, 0); //restore
	},
	_unSkipBfUnload: function () {
		zk.skipBfUnload = false;
	}
  }: {
	fixDom: zk.$void,
	unfixDom: zk.$void
  });

zk.Color = zk.$extends(zk.Object, {
	$init: function (color) {
		var rgb = this.rgb = [0, 0, 0];
		if(color.slice(0,4) == 'rgb(') {  
			var cols = color.slice(4,color.length-1).split(',');  
			for (var j = 0, len = cols.length; j < len; j++)
				rgb[j] = parseInt(cols[j]); //dec
		} else if(color.slice(0,1) == '#') {  
			if (color.length == 4) {
				for(var j = 0; j < 3; j++) {
					var cc = color.charAt(j + 1);
					rgb[j] = parseInt(cc + cc, 16); //hex
				}
			} else if(color.length == 7) {
				for(var j = 0, i = 1; j < 3; j++, i += 2)
					rgb[j] = parseInt(color.substring(i, i+2), 16); //hex
			}  
		}
	},
	toString: function () {
		var s = '#';
		for (var j = 0; j < 3;) {
			var v = this.rgb[j++];;
			if (v < 16) s += '0';
			s += new Number(v).toString(16);
		}
		return s;
	}
});


zEvt = {
	target: function(evt) {
		evt = evt || window.event;
		return evt.target || evt.srcElement;
	},
	stop: function(evt) {
		evt = evt || window.event;
		if (evt.preventDefault) {
			evt.preventDefault();
			evt.stopPropagation();
		} else {
			evt.returnValue = false;
			evt.cancelBubble = true;
			if (!evt.shiftKey && !evt.ctrlKey)
				evt.keyCode = 0; //Bug 1834891
		}
	},

	leftClick: function(evt) {
		evt = evt || window.event;
		return evt.which == 1 || evt.button == 0 || evt.button == 1;
	},
	rightClick: function (evt) {
		evt = evt || window.event;
		return evt.which == 3 || evt.button == 2;
	},
	mouseData: function (evt, target) {
		evt = evt || window.event;
		var ofs = zDom.cmOffset(target ? target: zEvt.target(evt)),
			px = zEvt.x(evt), py = zEvt.y(evt);
		return {
			x: px - ofs[0], y: py - ofs[1],
			pageX: px, pageY: py,
			keys: zEvt.keyMetaData(evt),
			marshal: zEvt._mouseDataMarshal
		};
	},
	keyData: function (evt) {
		evt = evt || window.event;
		return {
			keyCode: zEvt.keyCode(evt),
			charCode: zEvt.charCode(evt),
			keys: zEvt.keyMetaData(evt),
			marshal: zEvt._keyDataMarshal
		};
	},
	keyMetaData: function (evt) {
		evt = evt || window.event;
		return {
			altKey: evt.altKey,
			ctrlKey: evt.ctrlKey,
			shiftKey: evt.shiftKey,
			leftClick: zEvt.leftClick(evt),
			rightClick: zEvt.rightClick(evt),
			marshal: zEvt._keyMetaDataMarshal
		};
	},
	_mouseDataMarshal: function () {
		return [this.x, this.y, this.pageX, this.pageY, this.keys.marshal()];
	},
	_keyDataMarshal: function () {
		return [this.keyCode, this.charCode, this.keys.marshal()];
	},
	_keyMetaDataMarshal: function () {
		var s = "";
		if (this.altKey) s += 'a';
		if (this.ctrlKey) s += 'c';
		if (this.shiftKey) s += 's';
		if (this.leftClick) s += 'l';
		if (this.rightClick) s += 'r';
		return s;
	},

	x: function (evt) {
		evt = evt || window.event;
		return evt.pageX || (evt.clientX +
			(document.documentElement.scrollLeft || document.body.scrollLeft));
  	},
	y: function(evt) {
		evt = evt || window.event;
		return evt.pageY || (evt.clientY +
			(document.documentElement.scrollTop || document.body.scrollTop));
	},
	pointer: function (evt) {
		return [zEvt.x(evt), zEvt.y(evt)];
	},

	charCode: function(evt) {
		evt = evt || window.event;
		return evt.charCode || evt.keyCode;
	},
	keyCode: function(evt) {
		evt = evt || window.event;
		var k = evt.keyCode || evt.charCode;
		return zk.safari ? (zEvt.safariKeys[k] || k) : k;
	},

	listen: function (el, evtnm, fn) {
		if (el.addEventListener)
			el.addEventListener(evtnm, fn, false);
		else /*if (el.attachEvent)*/
			el.attachEvent('on' + evtnm, fn);
	},
	unlisten: function (el, evtnm, fn) {
		if (el.removeEventListener)
			el.removeEventListener(evtnm, fn, false);
		else if (el.detachEvent) {
			try {
				el.detachEvent('on' + evtnm, fn);
			} catch (e) {
			}
		}
	},

	enableESC: function () {
		if (zDom._noESC) {
			zEvt.unlisten(document, "keydown", zDom._noESC);
			delete zDom._noESC;
		}
		if (zDom._onErrChange) {
			window.onerror = zDom._oldOnErr;
			if (zDom._oldOnErr) delete zDom._oldOnErr;
			delete zDom._onErrChange;
		}
	},
	disableESC: function () {
		if (!zDom._noESC) {
			zDom._noESC = function (evt) {
				evt = evt || window.event;
				if (evt.keyCode == 27) {
					zEvt.stop(evt);
					return false;//eat
				}
				return true;
			};
			zEvt.listen(document, "keydown", zDom._noESC);

			//FUTURE: onerror not working in Safari and Opera
			//if error occurs, loading will be never ended, so try to ignore
			//we cannot use zEvt.listen. reason: no way to get back msg...(FF)
			zDom._oldOnErr = window.onerror;
			zDom._onErrChange = true;
			window.onerror =
	function (msg, url, lineno) {
		//We display errors only for local class web resource
		//It is annoying to show error if google analytics's js not found
		var au = zAu.comURI();
		if (au && url.indexOf(location.host) >= 0) {
			var v = au.lastIndexOf(';');
			v = v >= 0 ? au.substring(0, v): au;
			if (url.indexOf(v + "/web/") >= 0) {
				msg = mesg.FAILED_TO_LOAD + url + "\n" + mesg.FAILED_TO_LOAD_DETAIL
					+ "\n" + mesg.CAUSE + msg+" (line "+lineno + ")";
				if (zk.error) zk.error(msg);
				else alert(msg);
				return true;
			}
		}
	};
		}
	}
};

if (zk.safari)
	zEvt.safariKeys = {
		25: 9, 	   // SHIFT-TAB
		63232: 38, // up
		63233: 40, // down
		63234: 37, // left
		63235: 39, // right
		63272: 46, // delete
		63273: 36, // home
		63275: 35, // end
		63276: 33, // pgup
		63277: 34  // pgdn
	};

zk.Event = zk.$extends(zk.Object, {
	$init: function (target, name, data, opts, nativeEvent) {
		this.currentTarget = this.target = target;
		this.name = name;
		this.data = data;
		this.opts = opts;
		var devt = this.nativeEvent = nativeEvent || window.event;
		if (devt) this.nativeTarget = zEvt.target(devt);
	},
	stop: function (b) {
		this.stopped = !b;
	}
},{
	BS:		8,
	TAB:	9,
	ENTER:	13,
	SHIFT:	16,
	CTRL:	17,
	ALT:	18,
	ESC:	27,
	PGUP:	33,
	PGDN:	34,
	END:	35,
	HOME:	36,
	LFT:	37,
	UP:		38,
	RGH:	39,
	DN:		40,
	INS:	45,
	DEL:	46,
	F1:		112
});

zWatch = {
	listen: function (name, o) {
		var wts = zWatch._wts[name];
		if (wts) {
			var bindLevel = o.bindLevel;
			if (bindLevel != null) {
				for (var j = wts.length;;) {
					if (--j < 0) {
						wts.unshift(o);
						break;
					}
					if (bindLevel >= wts[j].bindLevel) { //parent first
						wts.$addAt(j + 1, o);
						break;
					}
				}
			} else
				wts.push(o);
		} else
			wts = zWatch._wts[name] = [o];
	},
	unlisten: function (name, o) {
		var wts = zWatch._wts[name];
		return wts && wts.$remove(o);
	},
	unlistenAll: function (name) {
		delete zWatch._wts[name];
	},
	fire: function (name, opts, vararg) {
		var wts = zWatch._wts[name];
		if (wts && wts.length) {
			var args = [];
			for (var j = 2, l = arguments.length; j < l;)
				args.push(arguments[j++]);

			wts = wts.$clone(); //make a copy since unlisten might happen
			if (opts) {
				if (opts.visible)
					for (var j = wts.length; --j >= 0;)
						if (!zWatch._visible(wts[j]))
							wts.splice(j, 1);

				if (opts.timeout >= 0) {
					setTimeout(
					function () {
						var o;
						while (o = wts.shift())
							o[name].apply(o, args);
					}, opts.timeout);
					return;
				}
			}

			var o;
			while (o = wts.shift())
				o[name].apply(o, args);
		}
	},
	fireDown: function (name, opts, origin, vararg) {
		var wts = zWatch._wts[name];
		if (wts && wts.length) {
			zWatch._sync();

			var args = [origin]; //origin as 1st
			for (var j = 3, l = arguments.length; j < l;)
				args.push(arguments[j++]);

			var found, bindLevel = origin.bindLevel,
				visibleOnly = opts && opts.visible;
			if (bindLevel != null) {
				found = [];
				for (var j = wts.length, o; --j >= 0;) { //child first
					o = wts[j];
					var diff = bindLevel > o.bindLevel;
					if (diff > 0) break;//nor ancestor, nor this (&sibling)
					if (!diff && origin == o) {
						found.unshift(o);
						break; //found this (and no descendant ahead)
					}

					var fn = visibleOnly ? zWatch._visibleChild: zUtl.isAncestor;
					if (fn(origin, o)) found.unshift(o); //parent first
				}
			} else
				found = wts.$clone(); //make a copy since unlisten might happen

			if (opts && opts.timeout >= 0) {
				setTimeout(
				function () {
					var o;
					while (o = found.shift())
						o[name].apply(o, args);
				}, opts.timeout);
				return;
			}

			var o;
			while (o = found.shift())
				o[name].apply(o, args);
		}
	},
	_visibleChild: function (p, c) {
		for (; c; c = c.parent) {
			if (p == c) return true; //check parent before visible
			if (c.isVisible && !c.isVisible(true)) break;
		}
		return false;
	},
	_visible: function (w) {
		for (; w; w = w.parent)
			if (w.isVisible && !w.isVisible(true)) return false;
		return true;
	},
	onBindLevelMove: function () {
		zWatch._dirty = true;
	},
	_sync: function () {
		if (!zWatch._dirty) return;

		zWatch._dirty = false;
		for (var nm in zWatch._wts) {
			var wts = zWatch._wts[nm];
			if (wts.length && wts[0].bindLevel != null)
				wts.sort(zWatch._cmp);
		}
	},
	_cmp: function (a, b) {
		return a.bindLevel - b.bindLevel;
	},
	_wts: {}
};
zWatch.listen('onBindLevelMove', zWatch);


zk.Draggable = zk.$extends(zk.Object, {
	$init: function(control, node, opts) {
		var zdg = zk.Draggable;
		if (!zdg._stackup) {
		//IE: if we don't insert stackup at beginning, dragging is slow
			var n = zdg._stackup = zDom.makeStackup(null, 'z_ddstkup');
			zDom.hide(n);
			document.body.appendChild(n);
		}

		this.control = control;
		this.node = node = node ? zDom.$(node): control.node || control.getNode();

		opts = zk.$default(opts, {
			zIndex: 1000,
			scrollSensitivity: 20,
			scrollSpeed: 15,
			delay: 0
		});

		if (opts.reverteffect == null)
			opts.reverteffect = zdg._defRevertEffect;
		if (opts.endeffect == null) {
			opts.endeffect = zdg._defEndEffect;
			if (opts.starteffect == null)
				opts.starteffect = zdg._defStartEffect;
		}


		if(opts.handle) this.handle = zDom.$(opts.handle);
		if(!this.handle) this.handle = node;

		if(opts.scroll && !opts.scroll.scrollTo && !opts.scroll.outerHTML) {
			opts.scroll = zDom.$(opts.scroll);
			this._isScrollChild = zUtl.isAncestor(opts.scroll, node);
		}

		this.delta = this._currentDelta();
		this.opts = opts;
		this.dragging = false;   

		zEvt.listen(this.handle, "mousedown",
			this.proxy(this._mouseDown, '_pxMouseDown'));

		zdg._register(this);
	},
	destroy: function() {
		zEvt.unlisten(this.handle, "mousedown", this._pxMouseDown);
		zk.Draggable._unregister(this);
		this.node = this.control = this.handle = null;
	},

	/** [left, right] of this node. */
	_currentDelta: function() {
		return [zk.parseInt(zDom.getStyle(this.node, 'left')),
			zk.parseInt(zDom.getStyle(this.node, 'top'))];
	},

	_startDrag: function(evt) {
		//disable selection
		zDom.disableSelection(document.body); // Bug #1820433
		if (this.opts.overlay) { // Bug #1911280
			this._overlay = document.createElement("DIV");
			document.body.appendChild(this._overlay);
			this._overlay = zDom.setOuterHTML(this._overlay,
				'<div class="z-dd-overlay" id="zk_dd_overlay"></div>');
			zDom.disableSelection(this._overlay);
			var st = this._overlay.style;
			st.width = zDom.pageWidth() + "px";
			st.height = zDom.pageHeight() + "px";
		}
		zk.dragging = this.dragging = true;

		var node = this.node,
			zdg = zk.Draggable;
		if(this.opts.ghosting)
			if (typeof this.opts.ghosting == 'function') {
				this.delta = this._currentDelta();
				this.z_elorg = this.node;

				var ofs = zDom.cmOffset(this.node);
				this.z_scrl = zDom.scrollOffset(this.node);
				this.z_scrl[0] -= zDom.innerX(); this.z_scrl[1] -= zDom.innerY();
					//Store scrolling offset since _draw not handle DIV well
				ofs[0] -= this.z_scrl[0]; ofs[1] -= this.z_scrl[1];

				node = this.node = this.opts.ghosting(this, ofs, evt);
			} else {
				this._clone = node.cloneNode(true);
				this.z_orgpos = node.style.position; //Bug 1514789
				if (this.z_orgpos != 'absolute')
					zDom.absolutize(node);
				node.parentNode.insertBefore(this._clone, node);
			}

		if (this.opts.stackup) {
			var defStackup = zdg._stackup;
			if (zDom.isVisible(defStackup)) //in use
				this._stackup = zDom.makeStackup(node, node.id + '$ddstk');
			else {
				this._stackup = defStackup;
				this._syncStackup();
				node.parentNode.insertBefore(this._stackup, node);
			}
		}

		if(this.opts.zIndex) { //after ghosting
			this.originalZ = zk.parseInt(zDom.getStyle(node, 'z-index'));
			node.style.zIndex = this.opts.zIndex;
		}

		if(this.opts.scroll) {
			if (this.opts.scroll == window) {
				var where = this._getWindowScroll(this.opts.scroll);
				this.originalScrollLeft = where.left;
				this.originalScrollTop = where.top;
			} else {
				this.originalScrollLeft = this.opts.scroll.scrollLeft;
				this.originalScrollTop = this.opts.scroll.scrollTop;
			}
		}

		if(this.opts.starteffect)
			this.opts.starteffect(this);
	},
	_syncStackup: function () {
		if (this._stackup) {
			var node = this.node,
				st = this._stackup.style;
			st.display = 'block';
			st.left = node.offsetLeft + "px";
			st.top = node.offsetTop + "px";
			st.width = node.offsetWidth + "px";
			st.height = node.offsetHeight + "px";
		}
	},

	_updateDrag: function(pointer, evt) {
		if(!this.dragging) this._startDrag(evt);
		this._updateInnerOfs();

		this._draw(pointer, evt);
		if (this.opts.change) this.opts.change(this, pointer);
		this._syncStackup();

		if(this.opts.scroll) {
			this._stopScrolling();

			var p;
			if (this.opts.scroll == window) {
				with(this._getWindowScroll(this.opts.scroll)) { p = [ left, top, left+width, top+height ];}
			} else {
				p = zDom.viewportOffset(this.opts.scroll);
				p[0] += this.opts.scroll.scrollLeft + this._innerOfs[0];
				p[1] += this.opts.scroll.scrollTop + this._innerOfs[1];
				p.push(p[0]+this.opts.scroll.offsetWidth);
				p.push(p[1]+this.opts.scroll.offsetHeight);
			}

			var speed = [0,0];
			if(pointer[0] < (p[0]+this.opts.scrollSensitivity)) speed[0] = pointer[0]-(p[0]+this.opts.scrollSensitivity);
			if(pointer[1] < (p[1]+this.opts.scrollSensitivity)) speed[1] = pointer[1]-(p[1]+this.opts.scrollSensitivity);
			if(pointer[0] > (p[2]-this.opts.scrollSensitivity)) speed[0] = pointer[0]-(p[2]-this.opts.scrollSensitivity);
			if(pointer[1] > (p[3]-this.opts.scrollSensitivity)) speed[1] = pointer[1]-(p[3]-this.opts.scrollSensitivity);
			this._startScrolling(speed);
		}

		// fix AppleWebKit rendering
		if(navigator.appVersion.indexOf('AppleWebKit')>0) window.scrollBy(0,0);

		zEvt.stop(evt);
	},

	_finishDrag: function(evt, success) {
		this.dragging = false;
		if (this._overlay) {
			zDom.remove(this._overlay);
			delete this._overlay;
		}

		//enable selection back and clear selection if any
		zDom.enableSelection(document.body);
		setTimeout(zDom.clearSelection, 0);

		var stackup = this._stackup;
		if (stackup) {
			if (stackup == zk.Draggable._stackup) zDom.hide(stackup);
			else zDom.remove(stackup);
			delete this._stackup;
		}

		var node = this.node;
		if(this.opts.ghosting)
			if (typeof this.opts.ghosting == 'function') {
				if (this.opts.endghosting)
					this.opts.endghosting(this, this.z_elorg);
				if (this.node != this.z_elorg) {
					zDom.remove(this.node);
					this.node = this.z_elorg;
				}
				delete this.z_elorg;
			} else {
				if (this.z_orgpos != "absolute") { //Bug 1514789
					zDom.relativize(node);
					node.style.position = this.z_orgpos;
				}
				zDom.remove(this._clone);
				this._clone = null;
			}

		var pointer = zEvt.pointer(evt);
		var revert = this.opts.revert;
		if(revert && typeof revert == 'function')
			revert = revert(this, pointer, evt);

		var d = this._currentDelta();
		if(revert && this.opts.reverteffect) {
			this.opts.reverteffect(this,
				d[1]-this.delta[1], d[0]-this.delta[0]);
		} else {
			this.delta = d;
		}

		if(this.opts.zIndex)
			node.style.zIndex = this.originalZ;

		if(this.opts.endeffect) 
			this.opts.endeffect(this, evt);

		zk.Draggable._deactivate(this);

		setTimeout("zk.dragging=false", 0);
				//we have to reset it later since event is fired later (after onmouseup)
	},

	_mouseDown: function (evt) {
		var node = this.node,
			zdg = zk.Draggable;
		if(zdg._dragging[node] || !zEvt.leftClick(evt))
			return;

		// abort on form elements, fixes a Firefox issue
		var target = zEvt.target(evt)
			tag = zDom.tag(target);
		if(tag=='INPUT' || tag=='SELECT' || tag=='OPTION' || tag=='BUTTON' || tag=='TEXTAREA')
			return;

		//Skip popup/dropdown (of combobox and others)
		for (var n = target; n && n != node; n = n.parentNode)
			if (zDom.getStyle(n, 'position') == 'absolute')
				return;

		var pointer = zEvt.pointer(evt);
		if (this.opts.ignoredrag && this.opts.ignoredrag(this, pointer, evt))
			return;

		var pos = zDom.cmOffset(node);
		this.offset = [pointer[0] - pos[0], pointer[1] - pos[1]];

		zdg._activate(this);

		//Bug 1845026
		//We need to ensure that the onBlur evt is fired before the onSelect evt for consistent among four browsers. 
		if (zk.currentFocus) {
			var f = zk.currentFocus.getNode();
			if (f && target != f && typeof f.blur == "function")
				f.blur();
		}
		zEvt.stop(evt);

		var c = this.control;
		if (c && !c.$instanceof(zk.Widget)) c = null;
		zk.Widget.domMouseDown(c); //since event is stopped
	},
	_keyPress: function(evt) {
		if(zEvt.keyCode(evt) == zEvt.ESC) {
			this._finishDrag(evt, false);
			zEvt.stop(evt);
		}
	},

	_endDrag: function(evt) {
		if(this.dragging) {
			this._stopScrolling();
			this._finishDrag(evt, true);
			zEvt.stop(evt);
		}
	},

	_draw: function(point, evt) {
		var node = this.node,
			pos = zDom.cmOffset(node);
		if(this.opts.ghosting) {
			var r = zDom.scrollOffset(node);
			pos[0] += r[0] - this._innerOfs[0]; pos[1] += r[1] - this._innerOfs[1];
		}

		var d = this._currentDelta();
		pos[0] -= d[0]; pos[1] -= d[1];

		if(this.opts.scroll && (this.opts.scroll != window && this._isScrollChild)) {
			pos[0] -= this.opts.scroll.scrollLeft-this.originalScrollLeft;
			pos[1] -= this.opts.scroll.scrollTop-this.originalScrollTop;
		}

		var p = [point[0]-pos[0]-this.offset[0],
			point[1]-pos[1]-this.offset[1]];

		if(this.opts.snap)
			if(typeof this.opts.snap == 'function') {
				p = this.opts.snap(this, p);
			} else {
				if(this.opts.snap instanceof Array) {
					p = [Math.round(p[0]/this.opts.snap[0])*this.opts.snap[0],
						Math.round(p[1]/this.opts.snap[1])*this.opts.snap[1]];
				} else {
					p = [Math.round(p[0]/this.opts.snap)*this.opts.snap,
						Math.round(p[1]/this.opts.snap)*this.opts.snap];
				}
			}

		//Resolve scrolling offset when DIV is used
		if (this.z_scrl) {
			p[0] -= this.z_scrl[0]; p[1] -= this.z_scrl[1];
		}

		var style = node.style;
		if (typeof this.opts.draw == 'function') {
			this.opts.draw(this, p, evt);
		} else if (typeof this.opts.constraint == 'function') {
			var np = this.opts.constraint(this, p, evt); //return null or [newx, newy]
			if (np) p = np;
			style.left = p[0] + "px";
			style.top  = p[1] + "px";
		} else {
			if((!this.opts.constraint) || (this.opts.constraint=='horizontal'))
				style.left = p[0] + "px";
			if((!this.opts.constraint) || (this.opts.constraint=='vertical'))
				style.top  = p[1] + "px";
		}

		if(style.visibility=="hidden") style.visibility = ""; // fix gecko rendering
	},

	_stopScrolling: function() {
		if(this.scrollInterval) {
			clearInterval(this.scrollInterval);
			this.scrollInterval = null;
			zk.Draggable._lastScrollPointer = null;
		}
	},
	_startScrolling: function(speed) {
		if(speed[0] || speed[1]) {
			this.scrollSpeed = [speed[0]*this.opts.scrollSpeed,speed[1]*this.opts.scrollSpeed];
			this.lastScrolled = new Date();
			this.scrollInterval = setInterval(this.proxy(this._scroll), 10);
		}
	},

	_scroll: function() {
		var zdg = zk.Draggable,
			current = new Date(),
			delta = current - this.lastScrolled;
		this.lastScrolled = current;
		if(this.opts.scroll == window) {
			with (this._getWindowScroll(this.opts.scroll)) {
				if (this.scrollSpeed[0] || this.scrollSpeed[1]) {
				  var d = delta / 1000;
				  this.opts.scroll.scrollTo( left + d*this.scrollSpeed[0], top + d*this.scrollSpeed[1] );
				}
			}
		} else {
			this.opts.scroll.scrollLeft += this.scrollSpeed[0] * delta / 1000;
			this.opts.scroll.scrollTop  += this.scrollSpeed[1] * delta / 1000;
		}

		this._updateInnerOfs();
		if (this._isScrollChild) {
			zdg._lastScrollPointer = zdg._lastScrollPointer || zdg._lastPointer;
			zdg._lastScrollPointer[0] += this.scrollSpeed[0] * delta / 1000;
			zdg._lastScrollPointer[1] += this.scrollSpeed[1] * delta / 1000;
			if (zdg._lastScrollPointer[0] < 0)
				zdg._lastScrollPointer[0] = 0;
			if (zdg._lastScrollPointer[1] < 0)
				zdg._lastScrollPointer[1] = 0;
			this._draw(zdg._lastScrollPointer);
		}

		if(this.opts.change) {
			var evt = window.event;
			this.opts.change(this, evt ? zEvt.pointer(evt): zdg._lastPointer);
		}
	},

	_updateInnerOfs: function () {
		this._innerOfs = [zDom.innerX(), zDom.innerY()];
	},
	_getWindowScroll: function(w) {
		var T, L, W, H;
		with (w.document) {
			if (w.document.documentElement && documentElement.scrollTop) {
				T = documentElement.scrollTop;
				L = documentElement.scrollLeft;
			} else if (w.document.body) {
				T = body.scrollTop;
				L = body.scrollLeft;
			}
			if (w.innerWidth) {
				W = w.innerWidth;
				H = w.innerHeight;
			} else if (w.document.documentElement && documentElement.clientWidth) {
				W = documentElement.clientWidth;
				H = documentElement.clientHeight;
			} else {
				W = body.offsetWidth;
				H = body.offsetHeight
			}
		}
		return {top: T, left: L, width: W, height: H};
	}

},{ //static
	_drags: [],
	_dragging: [],

	_register: function(draggable) {
		var zdg = zk.Draggable;
		if(zdg._drags.length == 0) {
			zEvt.listen(document, "mouseup", zdg._docmouseup);
			zEvt.listen(document, "mousemove", zdg._docmousemove);
			zEvt.listen(document, "keypress", zdg._dockeypress);
		}
		zdg._drags.push(draggable);
	},
	_unregister: function(draggable) {
		var zdg = zk.Draggable;
		zdg._drags.$remove(draggable);
		if(zdg._drags.length == 0) {
			zEvt.unlisten(document, "mouseup", zdg._docmouseup);
			zEvt.unlisten(document, "mousemove", zdg._docmousemove);
			zEvt.unlisten(document, "keypress", zdg._dockeypress);
		}
	},

	_activate: function(draggable) {
		var zdg = zk.Draggable;
		if(zk.opera || draggable.opts.delay) { 
			zdg._timeout = setTimeout(function() { 
				zk.Draggable._timeout = null; 
				window.focus(); 
				zk.Draggable.activeDraggable = draggable; 
			}, draggable.opts.delay); 
		} else {
			window.focus(); // allows keypress events if window isn't currently focused, fails for Safari
			zdg.activeDraggable = draggable;
		}
	},
	_deactivate: function() {
		zk.Draggable.activeDraggable = null;
	},

	_docmousemove: function(evt) {
		var zdg = zk.Draggable;
		if(!zdg.activeDraggable) return;

		if (!evt) evt = window.event;
		var pointer = zEvt.pointer(evt);
		// Mozilla-based browsers fire successive mousemove events with
		// the same coordinates, prevent needless redrawing (moz bug?)
		if(zdg._lastPointer && zdg._lastPointer[0] == pointer [0]
		&& zdg._lastPointer[1] == pointer [1])
			return;

		zdg._lastPointer = pointer;
		zdg.activeDraggable._updateDrag(pointer, evt);
	},
	_docmouseup: function(evt) {
		if (!evt) evt = window.event;
		var zdg = zk.Draggable;
		if(zdg._timeout) { 
			clearTimeout(zdg._timeout); 
			zdg._timeout = null; 
		}
		if(!zdg.activeDraggable) return;

		zdg._lastPointer = null;
		zdg.activeDraggable._endDrag(evt);
		zdg.activeDraggable = null;
	},
	_dockeypress: function(evt) {
		if (!evt) evt = window.event;
		var zdg = zk.Draggable;
		if(zdg.activeDraggable)
			zdg.activeDraggable._keyPress(evt);
	},
	_restorePos: zk.ie ? function (el, pos) {
		//In IE, we have to detach and attach. We cannot simply restore position!!
		//Otherwise, a strange bar appear
		if (pos != 'absolute' && pos != 'relative') {
			var p = el.parentNode;
			var n = el.nextSibling;
			zDom.remove(el);
			el.style.position = pos;
			if (n) p.insertBefore(el, n);
			else p.appendChild(el);
		} else
			el.style.position = pos;
	}: function (el, pos) {
		el.style.position = pos;
	},

	//default effect//
	_defStartEffect: function (draggable) {
		var node = draggable.node;
		node._$opacity = zDom.getOpacity(node);
		zk.Draggable._dragging[node] = true;
		new zk.eff.Opacity(node, {duration:0.2, from:node._$opacity, to:0.7}); 
	},
	_defEndEffect: function(draggable) {
		var node = draggable.node,
			toOpacity = typeof node._$opacity == 'number' ? node._$opacity : 1.0;
		new zk.eff.Opacity(node, {duration:0.2, from:0.7,
			to:toOpacity, queue: {scope:'_draggable', position:'end'},
			afterFinish: function () { 
				zk.Draggable._dragging[node] = false;
			}
		});
	},
	_defRevertEffect: function(draggable, top_offset, left_offset) {
		var node = draggable.node,
			orgpos = node.style.position, //Bug 1538506
			dur = Math.sqrt(Math.abs(top_offset^2)+Math.abs(left_offset^2))*0.02;
		new zk.eff.Move(node, { x: -left_offset, y: -top_offset,
			duration: dur, queue: {scope:'_draggable', position:'end'}});

		//Bug 1538506: a strange bar appear in IE
		setTimeout(function () {
			zk.Draggable._restorePos(node, orgpos);
		}, dur * 1000 + 10);
	}
});


zEffect = {
	fade: function(element, opts) {
		element = zDom.$(element);
		var oldOpacity = element.style.opacity || '';
		opts = zk.$default(opts, {to: 0.0});
		if (!opts.from) opts.from = zDom.getOpacity(element) || 1.0;
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) { 
				if(effect.opts.to==0) {
					var e = effect.node;
					zDom.hide(e);
					zDom.setStyle(e, {opacity: oldOpacity}); 
				}
			};
		return new zk.eff.Opacity(element,opts);
	},
	appear: function(element, opts) {
		element = zDom.$(element);
		opts = zk.$default(opts, {to: 1.0});
		if (!opts.from)
			opts.from = zDom.getStyle(element, 'display') == 'none' ? 0.0 : zDom.getOpacity(element) || 0.0;
		
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) { 
				zDom.rerender(effect.node);
				// force Safari to render floated elements properly
			};
		if (!opts.beforeSetup)
			opts.beforeSetup = function(effect) {
				var e = effect.node;
				zDom.setOpacity(e, effect.opts.from);
				zDom.show(e);
			};
		return new zk.eff.Opacity(element,opts);
	},

	puff: function(element, opts) {
		element = zDom.$(element);
		var oldStyle = { 
			opacity: element.style.opacity || '', 
			position: zDom.getStyle(element, 'position'),
			top:  element.style.top,
			left: element.style.left,
			width: element.style.width,
			height: element.style.height
		};
		opts = zk.$default(opts, {duration: 1.0});
		if (!opts.beforeSetupInternal)
			opts.beforeSetupInternal = function(effect) {
				zDom.absolutize(effect._effects[0].node)
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect._effects[0].node;
				zDom.hide(e);
				zDom.setStyle(e, oldStyle);
			};
		return new zk.eff.Parallel(
			[ new zk.eff.Scale(element, 200, 
				{sync: true, scaleFromCenter: true, scaleContent: true, restoreAfterFinish: true}),
				new zk.eff.Opacity(element, {sync: true, to: 0.0}) ],
			opts);
	},

	blindUp: function(element, opts) {
		element = zDom.$(element);
		zDom.makeClipping(element);
		opts = zk.$default(opts,
			{scaleContent: false, scaleX: false, restoreAfterFinish: true});
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect.node;
				zDom.hide(e);
				zDom.undoClipping(e);
			};
		return new zk.eff.Scale(element, 0, opts);
	},
	blindDown: function(element, opts) {
		element = zDom.$(element);
		var elementDimensions = zDom.getDimension(element);
		opts = zk.$default(opts,{
			scaleContent: false,
			scaleX: false,
			scaleFrom: 0,
			scaleMode: {originalHeight: elementDimensions.height, originalWidth: elementDimensions.width},
			restoreAfterFinish: true});
		if (!opts.afterSetup)
			opts.afterSetup = function(effect) {
				var e = effect.node;
				zDom.makeClipping(e);
				zDom.setStyle(e, {height: '0px'});
				zDom.show(e); 
			};
		if (!opts.afterFinishInternal)
			afterFinishInternal = function(effect) {
				zDom.undoClipping(effect.node);
			};
		return new zk.eff.Scale(element, 100, opts);
	},

	switchOff: function(element, opts) {
		element = zDom.$(element);
		var oldOpacity = element.style.opacity || '';
		opts = zk.$default(opts, {
			duration: 0.4, from: 0, transition: zEffect._Tranx.flicker});
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				new zk.eff.Scale(effect.node, 1, { 
					duration: 0.3, scaleFromCenter: true,
					scaleX: false, scaleContent: false,
					restoreAfterFinish: true,
					beforeSetup: function(effect) {
						var e = effect.node;
						zDom.makePositioned(e);
						zDom.makeClipping(e);
					},
					afterFinishInternal: function(effect) {
						var e = effect.node;
						zDom.hide(e);
						zDom.undoClipping(e);
						zDom.undoPositioned(e);
						zDom.setStyle(e, {opacity: oldOpacity});
					}
				});
			}
		return appear(element, opts);
	},

	dropOut: function(element, opts) {
		element = zDom.$(element);
		var oldStyle = {
			top: zDom.getStyle(element, 'top'),
			left: zDom.getStyle(element, 'left'),
			opacity: element.style.opacity || ''};
		opts = zk.$default(opts, {duration: 0.5});
		if (!opts.beforeSetup)
			opts.beforeSetup = function(effect) {
				zDom.makePositioned(effect._effects[0].node);
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect._effects[0].node;
				zDom.hide(e);
				zDom.undoPositioned(e);
				zDom.setStyle(e, oldStyle);
			};
		return new zk.eff.Parallel(
			[new zk.eff.Move(element, {x: 0, y: 100, sync: true}), 
			new zk.eff.Opacity(element, { sync: true, to: 0.0})], opts);
	},

	slideOut: function(element, opts) {
		var anchor = opts ? opts.anchor || 't': 't';
		element = zDom.$(element);

		var movement, st = element.style;
		switch (anchor) {
		case 't':
			movement = {x: 0, y: -zk.parseInt(st.height), sync: true};
			break;
		case 'b':
			movement = {x: 0, y: zk.parseInt(st.height), sync: true};
			break;
		case 'l':
			movement = {x: -zk.parseInt(st.width), y: 0, sync: true};
			break;
		case 'r':
			movement = {x: zk.parseInt(st.width), y: 0, sync: true};
			break;
		}

		var oldStyle = {
			top: zDom.getStyle(element, 'top'),
			left: zDom.getStyle(element, 'left'),
			opacity: element.style.opacity || ''};
		opts = zk.$default(opts, {duration: 0.5});
		if (!opts.beforeSetup)
			opts.beforeSetup = function(effect) {
				zDom.makePositioned(effect._effects[0].node); 
			};
		if (!opts.beforeFinishInternal)
			opts.beforeFinishInternal = function (effect) {
				zDom.hide(effect._effects[0].node);
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect._effects[0].node;
				zDom.undoPositioned(e)
				zDom.setStyle(e, oldStyle);
			}; 
		return new zk.eff.Parallel(
			[new zk.eff.Move(element, movement)], opts);
	},
	slideIn: function(element, opts) {
		var anchor = opts ? opts.anchor || 't': 't';
		element = zDom.$(element);
		var oldStyle = {
			top: zDom.getStyle(element, 'top'),
			left: zDom.getStyle(element, 'left'),
			opacity: element.style.opacity || ''};

		var movement, st = element.style;
		switch (anchor) {
		case 't':
			var t = zk.parseInt(st.top), h = zk.parseInt(st.height);
			st.top = t - h + "px";
			movement = {x: 0, y: h, sync: true};
			break;
		case 'b':
			var t = zk.parseInt(st.top), h = zk.parseInt(st.height);
			st.top = t + h + "px";
			movement = {x: 0, y: -h, sync: true};
			break;
		case 'l':
			var l = zk.parseInt(st.left), w = zk.parseInt(st.width);
			st.left = l - w + "px";
			movement = {x: w, y: 0, sync: true};
			break;
		case 'r':
			var l = zk.parseInt(st.left), w = zk.parseInt(st.width);
			st.left = l + w + "px";
			movement = {x: -w, y: 0, sync: true};
			break; 
		}

		opts = zk.$default(opts, {duration: 0.5});
		if (!opts.beforeSetup)
			opts.beforeSetup = function(effect) {
				var e = effect._effects[0].node;
				zDom.show(e);
				zDom.makePositioned(e);
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect._effects[0].node;
				zDom.undoPositioned(e);
				zDom.setStyle(e, oldStyle);
			};
		return new zk.eff.Parallel(
			[new zk.eff.Move(element, movement)], opts);
	},

	slideDown: function(element, opts) {
		var anchor = opts ? opts.anchor || 't': 't';
		element = zDom.$(element);
		zDom.cleanWhitespace(element);

		// SlideDown need to have the content of the element wrapped in a container element with fixed height!
		var orig = {t: zDom.getStyle(element, 'top'), l: zDom.getStyle(element, 'left')},
			isVert = anchor == 't' || anchor == 'b',
			dims = zDom.getDimension(element);

		opts = zk.$default(opts, {
			scaleContent: false,
			scaleX: !isVert, scaleY: isVert,
			scaleFrom: zk.opera ? 0 : 1,
			scaleMode: {originalHeight: dims.height, originalWidth: dims.width},
			restoreAfterFinish: true
		});
		if (!opts.afterSetup)
			opts.afterSetup = function(effect) {
				var e = effect.node;
				zDom.makePositioned(e);
				switch (anchor) {
				case 't':
					zDom.makeClipping(e);
					zDom.setStyle(e, {height: '0px'});
					zDom.show(e);
					break;
				case 'b':
					orig.ot = dims.top + dims.height;
					zDom.makeClipping(e);
					zDom.setStyle(e, {height: '0px', top: orig.ot + 'px'});
					zDom.show(e);
					break;
				case 'l':
					zDom.makeClipping(e);
					zDom.setStyle(e, {width: '0px'});
					zDom.show(e);
					break;
				case 'r':
					orig.ol = dims.left + dims.width;
					zDom.makeClipping(e);
					zDom.setStyle(e, {width: '0px', left: orig.ol + 'px'});
					zDom.show(e);
					break;
				}
			};
		if (!opts.afterUpdateInternal)
			opts.afterUpdateInternal = function(effect){
				var e = effect.node;
				if (anchor == 'b')
					zDom.setStyle(e, {top: (orig.ot - zk.parseInt(effect.node.style.height)) + 'px'});
				else if (anchor == 'r')
					zDom.setStyle(e, {left: (orig.ol - zk.parseInt(effect.node.style.width)) + 'px'});
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function(effect) {
				var e = effect.node;
				zDom.undoClipping(e);
				zDom.undoPositioned(e);
				zDom.setStyle(e, {top: orig.t, left: orig.l});
			};
		return new zk.eff.Scale(element, 100, opts);
	},
	slideUp: function(element, opts) {
		var anchor = opts ? opts.anchor || 't': 't';
		element = zDom.$(element);
		zDom.cleanWhitespace(element);

		var orig = {t: zDom.getStyle(element, 'top'), l: zDom.getStyle(element, 'left')},
			isVert = anchor == 't' || anchor == 'b';

		opts = zk.$default(opts, {
			scaleContent: false, 
			scaleX: !isVert, scaleY: isVert,
			scaleMode: 'box', scaleFrom: 100,
			restoreAfterFinish: true});
		if (!opts.beforeStartInternal)
			opts.beforeStartInternal = function(effect) {
				var e = effect.node;
				zDom.makePositioned(e);
				zDom.makeClipping(e);
				zDom.show(e);
				orig.ot = e.offsetTop;
				orig.oh = e.offsetHeight;
				orig.ol = e.offsetLeft;
				orig.ow = e.offsetWidth;
			};
		if (!opts.afterUpdateInternal)
			opts.afterUpdateInternal = function (effect) {
				var e = effect.node;
				if (anchor == 'b')
					zDom.setStyle(e, {top: (orig.ot + orig.oh - zk.parseInt(effect.node.style.height)) + 'px'});
				else if (anchor == 'r')
					zDom.setStyle(e, {left: (orig.ol + orig.ow - zk.parseInt(effect.node.style.width)) + 'px'});
			};
		if (!opts.beforeFinishInternal)
			opts.beforeFinishInternal = function (effect) {
				zDom.hide(effect.node);
			};
		if (!opts.afterFinishInternal)
			opts.afterFinishInternal = function (effect) {
				var e = effect.node;
				zDom.undoClipping(e);
				zDom.undoPositioned(e);
				zDom.setStyle(e, {top: orig.t, left: orig.l});
			};
		return new zk.eff.Scale(element, zk.opera ? 0 : 1, opts);
	}
};

zk.eff = {}
zk.eff.Base_ = zk.$extends(zk.Object, {
	position: null,
	start: function(opts) {
		this.opts = zk.$default(opts, zEffect._defOpts);
		this.name = this.opts.name || "Base";
		this.currentFrame = 0;
		this.state = 'idle';
		this.startOn = this.opts.delay*1000;
		this.finishOn = this.startOn + (this.opts.duration*1000);
		this.event('beforeStart');

		if(!this.opts.sync) zEffect._Queue.add(this);
	},
	loop: function(timePos) {
		if(timePos >= this.startOn) {
			if(timePos >= this.finishOn) {
				this.render(1.0);
				this.cancel();
				this.event('beforeFinish');
				if(this.finish) this.finish(); 
				this.event('afterFinish');
				return;  
			}
			var pos = (timePos - this.startOn) / (this.finishOn - this.startOn);
			var frame = Math.round(pos * this.opts.fps * this.opts.duration);
			if(frame > this.currentFrame) {
				this.render(pos);
				this.currentFrame = frame;
			}
		}
	},
	render: function(pos) {
		if(this.state == 'idle') {
			this.state = 'running';
			this.event('beforeSetup');
			if(this.setup) this.setup();
			this.event('afterSetup');
		}
		if(this.state == 'running') {
			if(this.opts.transition) pos = this.opts.transition(pos);
			pos *= (this.opts.to-this.opts.from);
			pos += this.opts.from;
			this.position = pos;
			this.event('beforeUpdate');
			if(this.update) this.update(pos);
			this.event('afterUpdate');
		}
	},
	cancel: function() {
		if(!this.opts.sync)
			zEffect._Queue.remove(this);
		this.state = 'finished';
	},
	event: function(eventName) {
		if(this.opts[eventName + 'Internal'])
			this.opts[eventName + 'Internal'](this);
		if(this.opts[eventName]) this.opts[eventName](this);
	}
});

zk.eff.Parallel = zk.$extends(zk.eff.Base_, {
	$init: function(effects, opts) {
		this._effects = effects || [];
		this.start(opts);
	},
	update: function(position) {
		for (var j = 0, effs = this._effects, len = effs.length; j < len;)
			effs[j++].render(position);
	},
	finish: function(position) {
		for (var j = 0, effs = this._effects, len = effs.length; j < len;) {
			var ef = effs[j++];
			ef.render(1.0);
			ef.cancel();
			ef.event('beforeFinish');
			if(ef.finish) ef.finish(position);
			ef.event('afterFinish');
		}
	}
});

zk.eff.Opacity = zk.$extends(zk.eff.Base_, {
	$init: function(element, opts) {
		var e = this.node = zDom.$(element);
		// make this work on IE on elements without 'layout'
		if(zk.ie && (!e.currentStyle.hasLayout))
			zDom.setStyle(e, {zoom: 1});
		opts = zk.$default(opts, {to: 1.0});
		if (!opts.from) opts.from = zDom.getOpacity(e) || 0.0,
		this.start(opts);
	},
	update: function(position) {
		zDom.setOpacity(this.node, position);
	}
});

zk.eff.Move = zk.$extends(zk.eff.Base_, {
	$init: function(element, opts) {
		this.node = zDom.$(element);
		opts = zk.$default(opts, {x: 0, y: 0, mode: 'relative'});
		this.start(opts);
	},
	setup: function() {
	// Bug in Opera: Opera returns the "real" position of a static element or
	// relative element that does not have top/left explicitly set.
	// ==> Always set top and left for position relative elements in your stylesheets 
	// (to 0 if you do not need them) 
		var e = this.node;
		zDom.makePositioned(e);
		this.originalLeft = parseFloat(zDom.getStyle(e, 'left') || '0');
		this.originalTop = parseFloat(zDom.getStyle(e, 'top')  || '0');
		if(this.opts.mode == 'absolute') {
			// absolute movement, so we need to calc deltaX and deltaY
			this.opts.x -= this.originalLeft;
			this.opts.y -= this.originalTop;
		}
	},
	update: function(position) {
		zDom.setStyle(this.node, {
			left: Math.round(this.opts.x  * position + this.originalLeft) + 'px',
			top:  Math.round(this.opts.y  * position + this.originalTop)  + 'px'
		});
	}
});

zk.eff.Scale = zk.$extends(zk.eff.Base_, {
	$init: function(element, percent, opts) {
		this.node = zDom.$(element);
		opts = zk.$default(opts, {
			scaleX: true,
			scaleY: true,
			scaleContent: true,
			scaleFromCenter: false,
			scaleMode: 'box', // 'box' or 'contents' or {} with provided values
			scaleFrom: 100.0,
			scaleTo: percent
			});
		this.start(opts);
	},
	setup: function() {
		var el = this.node;
		this.restoreAfterFinish = this.opts.restoreAfterFinish || false;
		this.nodePositioning = zDom.getStyle(el, 'position');
		
		this.originalStyle = {};
		for (var j = 0, styles=['top','left','width','height','fontSize'],
		len = styles.length; j < len;) {
			var s = styles[j++]
			this.originalStyle[s] = el.style[s];
		}

		this.originalTop = el.offsetTop;
		this.originalLeft = el.offsetLeft;

		var fontSize = zDom.getStyle(el, 'font-size') || '100%';
		for (var j = 0, types=['em','px','%','pt'],
		len = types.length; j < len;) {
			var t = types[j++];
			if(fontSize.indexOf(t) > 0) {
				this.fontSize = parseFloat(fontSize);
				this.fontSizeType = t;
			}
		}

		this.factor = (this.opts.scaleTo - this.opts.scaleFrom)/100;

		this.dims = null;
		if(this.opts.scaleMode=='box')
			this.dims = [el.offsetHeight, el.offsetWidth];
		if(/^content/.test(this.opts.scaleMode))
			this.dims = [el.scrollHeight, el.scrollWidth];
		if(!this.dims)
			this.dims = [this.opts.scaleMode.originalHeight,
				this.opts.scaleMode.originalWidth];
	},
	update: function(position) {
		var currentScale = (this.opts.scaleFrom/100.0) + (this.factor * position);
		if(this.opts.scaleContent && this.fontSize)
			zDom.setStyle(this.node, {fontSize: this.fontSize * currentScale + this.fontSizeType});
		this.setDimensions(this.dims[0] * currentScale, this.dims[1] * currentScale);
	},
	finish: function(position) {
		if(this.restoreAfterFinish)
			zDom.setStyle(this.node, this.originalStyle);
	},
	setDimensions: function(height, width) {
		var d = {};
		if(this.opts.scaleX) d.width = Math.round(width) + 'px';
		if(this.opts.scaleY) d.height = Math.round(height) + 'px';
		if(this.opts.scaleFromCenter) {
			var topd = (height - this.dims[0])/2;
			var leftd = (width  - this.dims[1])/2;
			if(this.nodePositioning == 'absolute') {
				if(this.opts.scaleY) d.top = this.originalTop-topd + 'px';
				if(this.opts.scaleX) d.left = this.originalLeft-leftd + 'px';
			} else {
				if(this.opts.scaleY) d.top = -topd + 'px';
				if(this.opts.scaleX) d.left = -leftd + 'px';
			}
		}
		zDom.setStyle(this.node, d);
	}
});

zk.eff.ScrollTo = zk.$extends(zk.eff.Base_, {
	$init: function(element, opts) {
		this.node = zDom.$(element);
		this.start(opts || {});
	},
	setup: function() {
		var innerY = zDom.innerY(),
			offsets = zDom.cmOffset(this.node);
		if(this.opts.offset) offsets[1] += this.opts.offset;
		var max = window.innerHeight ? 
			window.height - window.innerHeight :
			document.body.scrollHeight - 
			(document.documentElement.clientHeight ? 
				document.documentElement.clientHeight : document.body.clientHeight);
		this.scrollStart = innerY;
		this.delta = (offsets[1] > max ? max : offsets[1]) - this.scrollStart;
	},
	update: function(position) {
		window.scrollTo(zDom.innerX(), this.scrollStart + (position*this.delta));
	}
});

zEffect._Tranx = {
	sinoidal: function(pos) {
		return (-Math.cos(pos*Math.PI)/2) + 0.5;
	},
	flicker: function(pos) {
		return ((-Math.cos(pos*Math.PI)/4) + 0.75) + Math.random()/4;
	},
	pulse: function(pos, pulses) { 
		pulses = pulses || 5; 
		return Math.round((pos % (1/pulses)) * pulses) == 0 ? 
			((pos * pulses * 2) - Math.floor(pos * pulses * 2)) : 
			1 - ((pos * pulses * 2) - Math.floor(pos * pulses * 2));
	},
	none: function(pos) {
		return 0;
	},
	full: function(pos) {
		return 1;
	}
};
zEffect._Queue = {
	_effects: [],
	_interval: null,

	add: function(effect) {
		var timestamp = zUtl.now(),
			position = typeof effect.opts.queue == 'string' ? 
				effect.opts.queue : effect.opts.queue.position,
			effque = zEffect._Queue,
			effs = effque._effects;

		switch(position) {
		case 'front':
			// move unstarted effects after this effect  
			for (var j = 0, len = effs.length; j < len;) {
				var ef = effs[j++];
				if (ef.state == 'idle') {
					e.startOn  += effect.finishOn;
					e.finishOn += effect.finishOn;
				}
			}
			break;
		case 'with-last':
			for (var j = 0, len = effs.length; j < len;) {
				var v = effs[j++].startOn;
				if (v > timestamp) timestamp = v;
			}
			break;
		case 'end':
			// start effect after last queued effect has finished
			for (var j = 0, len = effs.length; j < len;) {
				var v = effs[j++].finishOn;
				if (v > timestamp) timestamp = v;
			}
			break;
		}

		effect.startOn  += timestamp;
		effect.finishOn += timestamp;

		if(!effect.opts.queue.limit || (effs.length < effect.opts.queue.limit))
			effs.push(effect);

		if(!effque._interval) 
			effque._interval = setInterval(effque.loop, 15);
	},
	remove: function(effect) {
		var effque = zEffect._Queue, effs = effque._effects;
		effs.$remove(effect);
		if(!effs.length) {
			clearInterval(effque._interval);
			effque._interval = null;
		}
	},
	loop: function() {
		var timePos = zUtl.now(), effs = zEffect._Queue._effects;
		for(var i=0, len=effs.length; i<len; i++) 
			if(effs[i])
				effs[i].loop(timePos);
	}
};

zEffect._defOpts = {
  transition: zEffect._Tranx.sinoidal,
  duration:   1.0,   // seconds
  fps:        60.0,  // max. 60fps due to zEffect.Queue implementation
  sync:       false, // true for combining
  from:       0.0,
  to:         1.0,
  delay:      0.0,
  queue:      'parallel'
};

zk.eff.Shadow = zk.$extends(zk.Object, {
	_HTML: zk.ie6Only ? '" class="z-shadow"></div>':
		'" class="z-shadow"><div class="z-shadow-tl"><div class="z-shadow-tr"></div></div>'
		+'<div class="z-shadow-cl"><div class="z-shadow-cr"><div class="z-shadow-cm">&#160;</div></div></div>'
		+'<div class="z-shadow-bl"><div class="z-shadow-br"></div></div></div>',

	$init: function (element, opts) {
		opts = this.opts = zk.$default(opts, {
			left: 4, right: 4, top: 3, bottom: 3
		});
		if (zk.ie6Only) {
			opts.left -= 1;
			opts.right -= 8;
			opts.top -= 2;
			opts.bottom -= 6;
		}

		this.node = element;
		var sdwid = element.id + "$sdw";
		zDom.insertHTMLBefore(element, '<div id="'+sdwid+this._HTML);
		this.shadow = zDom.$(sdwid);
	},
	destroy: function () {
		zDom.remove(this.shadow);
		zDom.remove(this.stackup);
		this.node = this.shadow = this.stackup = null;
	},
	hide: function(){
		this.shadow.style.display = 'none';
		if (this.stackup) this.stackup.style.display = 'none';
	},
	sync: function () {
		var node = this.node, shadow = this.shadow;
		if (!node || !zDom.isVisible(node, true)) {
			this.hide();
			return false;
		}

		for (var c = shadow;;) {
			if (!(c = c.nextSibling) || c.tagName) {
				if (c != node)
					node.parentNode.insertBefore(shadow, node);
				break;
			}
		}
		shadow.style.zIndex = zk.parseInt(zDom.getStyle(node, "zIndex"));

		var opts = this.opts,
			l = node.offsetLeft, t = node.offsetTop,
			w = node.offsetWidth, h = node.offsetHeight,
			wd = Math.max(0, w - opts.left + opts.right),
			hgh = Math.max(0, h - opts.top + opts.bottom),
			st = shadow.style;
		st.left = (l + opts.left) + "px";
		st.top = (t + opts.top) + "px";
		st.width = wd + "px";
		st.display = "block";
		if (zk.ie6Only) st.height = hgh + "px";
		else {
			var cns = shadow.childNodes;
			cns[1].style.height = Math.max(0, hgh - cns[0].offsetHeight - cns[2].offsetHeight) + "px";
		}

		var stackup = this.stackup;
		if(opts.stackup && node) {
			if(!stackup)
				stackup = this.stackup =
					zDom.makeStackup(node, node.id + '$sdwstk', shadow);

			st = stackup.style;
			st.left = l +"px";
			st.top = t +"px";
			st.width = w +"px";
			st.height = h +"px";
			st.zIndex = zk.parseInt(zDom.getStyle(node, "zIndex"));
			st.display = "block";
		}
		return true;
	},
	getBottomElement: function () {
		return this.stackup || this.shadow;
	}
});

zk.eff.FullMask = zk.$extends(zk.Object, {
	$init: function (opts) {
		opts = opts || {};
		var mask = this.mask = opts.mask;
		if (this.mask) {
			if (opts.anchor)
				opts.anchor.parentNode.insertBefore(mask, opts.anchor);
			if (opts.id) mask.id = opts.id;
			if (opts.zIndex != null) mask.style.zIndex = opts.zIndex;
			if (opts.visible == false) mask.style.display = 'none';
		} else {
			var maskId = opts.id || 'z_mask',
				html = '<div id="' + maskId + '" class="z-modal-mask"';//FF: don't add tabIndex
			if (opts.zIndex != null || opts.visible == false) {
				html += ' style="';
				if (opts.zIndex != null) html += 'z-index:' + opts.zIndex;
				if (opts.visible == false) html += ';display:none';
				html +='"';
			}

			html += '></div>'
			if (opts.anchor)
				opts.anchor.insertAdjacentHTML('beforeBegin', html);
			else
				document.body.insertAdjacentHTML('beforeEnd', html);
			mask = this.mask = zDom.$(maskId);
		}
		if (opts.stackup)
			this.stackup = zDom.makeStackup(mask, mask.id + '$mkstk');

		this._syncPos();

		zEvt.listen(mask, "mousemove", zEvt.stop);
		zEvt.listen(mask, "click", zEvt.stop);
		zEvt.listen(window, "resize", this.proxy(this._syncPos, '_pxSyncPos'));
		zEvt.listen(window, "scroll", this._pxSyncPos);
	},
	destroy: function () {
		var mask = this.mask;
		zEvt.unlisten(mask, "mousemove", zEvt.stop);
		zEvt.unlisten(mask, "click", zEvt.stop);
		zEvt.unlisten(window, "resize", this._pxSyncPos);
		zEvt.unlisten(window, "scroll", this._pxSyncPos);
		zDom.remove(mask);
		zDom.remove(this.stackup);
		this.mask = this.stackup = null;
	},
	hide: function () {
		this.mask.style.display = 'none';
		if (this.stackup) this.stackup.style.display = 'none';
	},
	sync: function (el) {
		if (!zDom.isVisible(el, true)) {
			this.hide();
			return;
		}

		if (this.mask.nextSibling != el) {
			var p = el.parentNode;
			p.insertBefore(this.mask, el);
			if (this.stackup)
				p.insertBefore(this.stackup, this.mask);
		}

		var st = this.mask.style;
		st.display = 'block';
		st.zIndex = el.style.zIndex;
		if (this.stackup) {
			st = this.stackup.style;
			st.display = 'block';
			st.zIndex = el.style.zIndex;
		}
	},
	/** Position a mask to cover the whole browser window. */
	_syncPos: function () {
		var n = this.mask;
		var ofs = zDom.toStyleOffset(n, zDom.innerX(), zDom.innerY()),
			st = n.style;
		st.left = ofs[0] + "px";
		st.top = ofs[1] + "px";
		st.width = zDom.innerWidth() + "px";
		st.height = zDom.innerHeight() + "px";
		st.display = "block";

		n = this.stackup;
		if (n) {
			n = n.style;
			n.left = st.left;
			n.top = st.top;
			n.width = st.width;
			n.height = st.height;
		}
	}
});

zk.eff.Mask = zk.$extends(zk.Object, {
	$init: function(opts) {
		opts = opts || {};
		var anchor = (typeof opts.anchor == "string") ? zDom.$(opts.anchor) : opts.anchor;
		
		if (!anchor || !zDom.isRealVisible(anchor, true)) return; //nothing do to.
		
		var maskId = opts.id || 'z_applymask',
			progbox = zDom.$(maskId);
		
		if (progbox) return this;
		
		var msg = opts.msg || (window.mesg?mesg.LOADING:"Loading..."),
			n = document.createElement("DIV");
		
		document.body.appendChild(n);
		var xy = opts.offset || zDom.revisedOffset(anchor), 
			w = opts.width || zDom.offsetWidth(anchor),
			h = opts.height || zDom.offsetHeight(anchor),
			html = '<div id="'+maskId+'" style="visibility:hidden">' 
				+ '<div class="z-apply-mask" style="display:block;top:' + xy[1]
				+ 'px;left:' + xy[0] + 'px;width:' + w + 'px;height:' + h + 'px;"></div>'
				+ '<div id="'+maskId+'$z_loading" class="z-apply-loading"><div class="z-apply-loading-indicator">'
				+ '<span class="z-apply-loading-icon"></span> '
				+ msg+ '</div></div></div>';
		zDom.setOuterHTML(n, html);
		var loading = zDom.$(maskId+"$z_loading"),
			mask = this.mask = zDom.$(maskId);
		
		if (loading) {
			if (loading.offsetHeight > h) 
				loading.style.height = zDom.revisedHeight(loading, h) + "px";
			if (loading.offsetWidth > w)
				loading.style.width = zDom.revisedWidth(loading, w) + "px";
			loading.style.top = (xy[1] + ((h - loading.offsetHeight) /2)) + "px";
			loading.style.left = (xy[0] + ((w - loading.offsetWidth) /2)) + "px";
		}
		
		mask.style.visibility = "";
	},
	destroy: function () {
		var mask = this.mask;
		zDom.remove(mask);
		this.mask = null;
	}
});

zk.eff.Tooltip = zk.$extends(zk.Object, {
	beforeBegin: function (ref) {
		var overTip = this._tip == ref;
		if (overTip) this._clearClosing(); //not close tip if over tip
		return !overTip;//disable tip in tip
	},
	begin: function (tip, ref) {
		if (this._tip != tip) {
			this.close_();

			this._inf = {
				tip: tip, ref: ref,
				timer: setTimeout(this.proxy(this.open_, '_pxopen'), zk.tipDelay)
			};
		} else
			this._clearClosing();
	},
	end: function (ref) {
		if (this._ref == ref || this._tip == ref)
			this._tmClosing =
				setTimeout(this.proxy(this.close_, '_pxclose'), 100);
			//don't cloes immediate since user might move from ref to toolip
		else
			this._clearOpening();
	},
	open_: function () {
		var inf = this._inf;
		if (inf) {
			var tip = this._tip = inf.tip,
				ref = this._ref = inf.ref;
			this._inf = null;
			tip.open(ref, zk.currentPointer, null, {sendOnOpen:true});
		}
	},
	close_: function () {
		this._clearOpening();
		this._clearClosing();

		var tip = this._tip;
		if (tip) {
			this._tip = this._ref = null;
			tip.close({sendOnOpen:true});
		}
	},
	_clearOpening: function () {
		var inf = this._inf;
		if (inf) {
			this._inf = null;
			clearTimeout(inf.timer);
		}
	},
	_clearClosing: function () {
		var tmClosing = this._tmClosing;
		if (tmClosing) {
			this._tmClosing = null;
			clearTimeout(tmClosing);
		}
	}
});
zTooltip = new zk.eff.Tooltip();


// anima //
/* Animation effects. It requires the component to have the <div><div>
 * structure.
 */
zAnima = {
	/** Number of pending animation */
	count: 0,

	isAnimating: function (n) {
		return n && n._$animating;
	},

	/** Make a component visible by increasing the opacity.
	 *
	 * @param opts a map of options.
	 * <dl>
	 * <dt>duration</dt><dd>milliseconds</dd>
	 * <dt>beforeAnima</dt><dd>A function to call before animation. 
	 * Not: <code>this</code> will be widget when it is called.</dd>
	 * <dt>afterAnima</dt><dd>A function to call after animation. 
	 * Not: <code>this</code> will be widget when it is called.</dd>
	 * </dl>
	 */
	appear: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.appear(widget, n, opts);});
			} else {
				n._$animating = "show";
				zEffect.appear(n,
					zAnima._mergeOpts(widget, opts,	{duration: 0.6}));
			}
		}
	},
	/**
	 * Make a component visible by moving down.
	 * @see #moveBy
	 */
	moveDown: function (widget, n, opts) {
		zAnima.moveBy(widget, n, zk.$defaut(opts, {dir: 't'}));
	},
	/**
	 * Make a component visible by moving right.
	 * @param {Object} n
	 * @see #moveBy
	 */
	moveRight: function (widget, n, opts) {
		zAnima.moveBy(widget, n, zk.$defaut(opts, {dir: 'l'}));
	},
	/**
	 * Make a component visible by moving diagonal.
	 * @param {Object} n
	 * @see #moveBy
	 */
	moveDiagonal: function (widget, n, opts) {
		zAnima.moveBy(widget, n, opts);
	},
	/** Make a component visible by moving.
	 * 
	 * @param opts a map of options. See {@link #appear}.
	 * In addition,
	 * <dl>
	 * <dt>dir</dt><dd>the direction: "t" means from 0 to the original top, 
	 *  "l" means from 0 to the original left, and 'lt' means from the left-top
	 * corner (default)</dd>
	 * </dl>
	 */
	moveBy: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.moveBy(widget, n, opts);});
			} else {
				n._$animating = "show";

				var dir = opts ? opts.dir: 0;
				if (!dir) dir = "lt"

  				new zk.eff.Move(n, 0, 0, zAnima._mergeOpts(widget, opts, {
					duration: 0.6,
					afterSetup: function(effect) {
						if (dir.indexOf('l') >= 0) {
							effect.opts.x = effect.originalLeft;
							effect.originalLeft = 0;
						}
						if (dir.indexOf('t') >= 0) {
							effect.opts.y = effect.originalTop;
							effect.originalTop = 0;
						}
						zDom.show(effect.node);
					}
				}));
			}
		}
	},
	/** Make a component invisible by sliding in.
	 * @param opts a map of options. See {@link #appear}.
	 * In addition,
	 * <dl>
	 * <dt>anchor</dt><dd>An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.</dd>
	 * </dl>
	 */
	slideIn: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideIn(widget, n, opts);});
			} else {
				n._$animating = "show";
				zEffect.slideIn(n, zAnima._mergeOpts(widget, opts, {duration: 0.4}));
			}
		}
	},
	/** Make a component invisible by sliding out.
	 * @param opts a map of options. See {@link #appear}.
	 * In addition,
	 * <dl>
	 * <dt>anchor</dt><dd>An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.</dd>
	 * </dl>
	 */
	slideOut: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideOut(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.slideOut(n, zAnima._mergeOpts(widget, opts, {duration: 0.4}));
			}
		}
	},
	/** Make a component visible by sliding down.
	 * 
	 * @param n component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 */
	slideDown: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideDown(widget, n, opts);});
			} else {
				n._$animating = "show";
				zEffect.slideDown(n, zAnima._mergeOpts(widget, opts, {duration: 0.4, y:0}));
			}
		}
	},
	/** Make a component invisible by sliding up.
	 * @param opts a map of options. See {@link #appear}.
	 * In addition,
	 * <dl>
	 * <dt>anchor</dt><dd>An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.</dd>
	 * </dl>
	 */
	slideUp: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideUp(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.slideUp(n, zAnima._mergeOpts(widget, opts, {duration:0.4}));
			}
		}
	},
	/** Make a component invisible by fading it out.
	 */
	fade: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.fade(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.fade(n,
					zAnima._mergeOpts(widget, opts, {duration: 0.55}));
			}
		}
	},
	/** Make a component invisible by puffing away.
	 */
	puff: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.puff(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.puff(n,
					zAnima._mergeOpts(widget, opts, {duration: 0.6}));
			}
		}
	},
	/** Make a component invisible by fading and dropping out.
	 * 
	 * @param n component or its ID
	 */
	dropOut: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.dropOut(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.dropOut(n,
					zAnima._mergeOpts(widget, opts, {duration: 0.6}));
			}
		}
	},

	// private //
	_mergeOpts: function (widget, opts, defOpts) {
		opts = zk.$default(opts, {animaWidget: widget});
		if (opts.duration) opts.duration /= 1000;
		return zk.$default(zk.$default(opts, defOpts), zAnima._defOpts);
	},
	_defOpts: {
		beforeStart: function (ef) {
			var n = ef.node || ef._effects[0].node;
			if (n) {
				++zAnima.count;
				var opts = ef.opts,
					widget = opts.animaWidget;
				if (opts.beforeAnima) opts.beforeAnima.call(widget, n);
			}
		},
		afterFinish: function (ef) {
			var n = ef.node || ef._effects[0].node;
			if (n) {
				--zAnima.count;
				n._$animating = null;
				if (zk.ie && zDom.isVisible(n)) zDom.redoDOM(n); //fix an IE bug

				zAnima._doAniQue(n);

				var opts = ef.opts,
					widget = opts.animaWidget;
				if (opts.afterAnima) opts.afterAnima.call(widget, n);
			}
		}
	},

	//animation queue
	_aniQue: {},
		//queue for waiting animating to clear: map(id, array(js_func_name))
	_addAniQue: function(n, fn) {
		var que = zAnima._aniQue,
			id = n.id ? n.id: n,
			ary = que[id];
		if (!ary) ary = que[id] = [];
		ary.push(fn);
	},
	_doAniQue: function (n) {
		var que = zAnima._aniQue,
			id = n.id ? n.id: n,
			ary = que[id], fn;
		if (ary) {
			while(!n._$animating && (fn = ary.shift()))
				fn();
			if (!ary.length) delete que[id];
		}
	}
};


zk.Widget = zk.$extends(zk.Object, {
	_visible: true,
	nChildren: 0,
	bindLevel: -1,
	_mold: 'default',
	className: 'zk.Widget',

	$init: function (props) {
		this._asaps = {}; //event listened at server
		this._lsns = {}; //listeners(evtnm,listener)
		this._$lsns = {}; //listners registered by server(evtnm, fn)
		this._subnodes = {}; //store sub nodes for widget(domId, domNode)
		
		if (props) {
			var mold = props.mold;
			if (mold != null) {
				if (mold) this._mold = mold;
				delete props.mold; //avoid setMold being called
			}

			this.set(props);
		}

		if (!this.uuid) this.uuid = zk.Widget.nextUuid();
	},

	getMold: function () {
		return this._mold;
	},
	setMold: function (mold) {
		if (mold != this._mold) {
			this._mold = mold;
			this.rerender();
		}
	},

	getSpaceOwner: function () {
		for (var w = this; w; w = w.parent)
			if (w._fellows) return w;
		return null;
	},
	getFellow: function (id, global) {
		var ow = this.getSpaceOwner();
		if (!ow) return null;

		var f = ow._fellows[id];
		return f || !global ? f: zk.Widget._global[id];
	},
	getId: function () {
		return this.id;
	},
	setId: function (id) {
		var $Widget = zk.Widget, old = this.id;
		if (old) {
			delete zk.Widget._global[id];
			$Widget._rmIdSpace(this);
		}

		this.id = id;

		if (id) {
			zk.Widget._global[id] = this;
			$Widget._addIdSpace(this);
		}
	},

	set: function (name, value, extra) {
		if (arguments.length == 1) {
			for (var p in name)
				this._set(p, name[p]);
			return;
		}
		this._set(name, value, extra);
	},
	_set: function (name, value, extra) {
		if (name.length > 4 && name.startsWith('$$on')) {
			var cls = this.$class,
				ime = cls._importantEvts;
			(ime || (cls._importantEvts = {}))[name.substring(2)] = true;
		} else if (name.length > 3 && name.startsWith('$on'))
			this._asaps[name.substring(1)] = value;
		else
			zk.set(this, name, value, extra);
	},
	getChildAt: function (j) {
		if (j >= 0)
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (--j < 0)
					return w;
		return null;
	},
	getChildIndex: function () {
		var w = this.parent, j = 0;
		if (w)
			for (w = w.firstChild; w; w = w.nextSibling, ++j)
				if (w == this)
					return j;
		return 0;
	},
	setChildren: function (children) {
		if (children)
			for (var j = 0, l = children.length; j < l;)
				this.appendChild(children[j++]);
	},
	appendChild: function (child) {
		if (child == this.lastChild)
			return false;

		var oldpt = child.parent;
		if (oldpt != this)
			child.beforeParentChanged_(this);

		if (oldpt) {
			if (this._moveChild(child)) return true; //done
			oldpt.removeChild(child);
		}

		child.parent = this;
		var p = this.lastChild;
		if (p) {
			p.nextSibling = child;
			child.previousSibling = p;
			this.lastChild = child;
		} else {
			this.firstChild = this.lastChild = child;
		}
		++this.nChildren;

		zk.Widget._addIdSpaceDown(child);

		var dt = this.desktop;
		if (dt) this.insertChildHTML_(child, null, dt);

		this.onChildAdded_(child);
		return true;
	},
	insertBefore: function (child, sibling) {
		if (!sibling || sibling.parent != this)
			return this.appendChild(child);

		if (child == sibling || child.nextSibling == sibling)
			return false;

		if (child.parent != this)
			child.beforeParentChanged_(this);

		if (child.parent) {
			if (this._moveChild(child, sibling)) return true;
			child.parent.removeChild(child);
		}

		child.parent = this;
		var p = sibling.previousSibling;
		if (p) {
			child.previousSibling = p;
			p.nextSibling = child;
		} else this.firstChild = child;

		sibling.previousSibling = child;
		child.nextSibling = sibling;

		++this.nChildren;

		zk.Widget._addIdSpaceDown(child);

		var dt = this.desktop;
		if (dt) this.insertChildHTML_(child, sibling, dt);

		this.onChildAdded_(child);
		return true;
	},
	removeChild: function (child) {
		if (!child.parent)
			return false;
		if (this != child.parent)
			return false;

		child.beforeParentChanged_(null);

		var p = child.previousSibling, n = child.nextSibling;
		if (p) p.nextSibling = n;
		else this.firstChild = n;
		if (n) n.previousSibling = p;
		else this.lastChild = p;
		child.nextSibling = child.previousSibling = child.parent = null;

		--this.nChildren;

		zk.Widget._rmIdSpaceDown(child);

		if (child.desktop)
			this.removeChildHTML_(child, p);
		this.onChildRemoved_(child);
		return true;
	},
	_replaceWgt: function (newwgt) { //called by au's outer
		var node = this.getNode(),
			p = newwgt.parent = this.parent,
			s = newwgt.previousSibling = this.previousSibling;
		if (s) s.nextSibling = newwgt;
		else if (p) p.firstChild = newwgt;

		s = newwgt.nextSibling = this.nextSibling;
		if (s) s.previousSibling = newwgt;
		else if (p) p.lastChild = newwgt;

		if (this.desktop) {
			if (!newwgt.desktop) newwgt.desktop = this.desktop;
			if (node) newwgt.replaceHTML(node, newwgt.desktop);

			zk.Widget._fixBindLevel(newwgt, p ? p.bindLevel + 1: 0);
			zWatch.fire('onBindLevelMove', null, newwgt);
		}

		if (p) {
			p.onChildRemoved_(this);
			p.onChildAdded_(newwgt);
		}
	},
	beforeParentChanged_: function () {
	},
	domMovable_: function () {
		return true;
	},
	_moveChild: function (child, moveBefore) {
		if (child._floating || !child.domMovable_() || !this.domMovable_()
		|| !this.desktop || !child.desktop)
			return false;

		var beforeNode = null;
		if (moveBefore && !(beforeNode = moveBefore.getNode()))
			return false;

		var node = this.getNode(), kidnode = child.getNode();
			dt = this.desktop, kiddt = child.desktop,
			oldpt = child.parent;
		child._node = this._node = child.desktop = this.desktop = null; //to avoid bind_ and unbind_
		try {
			oldpt.removeChild(child);
			this.insertBefore(child, moveBefore);

			zDom.remove(kidnode);
			node.parentNode.insertBefore(kidnode, beforeNode);

			//Not calling unbind and bind, so handle bindLevel here
			var v = this.bindLevel + 1;
			if (child.bindLevel != v) {
				zk.Widget._fixBindLevel(child, v);
				zWatch.fire('onBindLevelMove', null, child);
			}
		} finally {
			this.desktop = dt; child.desktop = kiddt;
			this._node = node; child._node = kidnode;
		}

		oldpt.onChildRemoved_(child);
		this.onChildAdded_(child);
			//they are called if parent is the same
		return true;
	},

	isRealVisible: function () {
		for (var wgt = this; wgt; wgt = wgt.parent) {
			if (!wgt.isVisible()) return false;
			var n = wgt.getNode();
			if (n && !zDom.isVisible(n)) return false; //possible (such as in a hbox)
		}
		return true;
	},
	isVisible: function (strict) {
		var visible = this._visible;
		if (!strict || !visible)
			return visible;
		var n = this.getNode();
		return !n || zDom.isVisible(n);
	},
	setVisible: function (visible, fromServer) {
		if (this._visible != visible) {
			this._visible = visible;

			var p = this.parent;
			if (p && visible) p.onChildVisible_(this, true); //becoming visible
			if (this.desktop) this._setVisible(visible);
			if (p && !visible) p.onChildVisible_(this, false); //become invisible
		}
	},
	_setVisible: function (visible) {
		var parent = this.parent,
			parentVisible = !parent || parent.isRealVisible(),
			node = this.getNode(),
			floating = this._floating;

		if (!parentVisible) {
			if (!floating) this.setDomVisible_(node, visible);
			return;
		}

		if (visible) {
			var zi;
			if (floating)
				this._setZIndex(zi = this._topZIndex(), true);

			this.setDomVisible_(node, true);

			//from parent to child
			for (var fs = zk.Widget._floating, j = 0, fl = fs.length; j < fl; ++j) {
				var w = fs[j].widget;
				if (this != w && this._floatVisibleDependent(w)) {
					zi = zi >= 0 ? ++zi: w._topZIndex();
					var n = fs[j].node;
					if (n != w.getNode()) w.setFloatZIndex_(n, zi); //only a portion
					else w._setZIndex(zi, true);

					w.setDomVisible_(n, true, {visibility:1});
				}
			}

			zWatch.fireDown('onVisible', {visible:true}, this);
		} else {
			zWatch.fireDown('onHide', {visible:true}, this);

			for (var fs = zk.Widget._floating, j = fs.length,
			bindLevel = this.bindLevel; --j >= 0;) {
				var w = fs[j].widget;
				if (bindLevel >= w.bindLevel)
					break; //skip non-descendant (and this)
				if (this._floatVisibleDependent(w))
					w.setDomVisible_(fs[j].node, false, {visibility:1});
			}

			this.setDomVisible_(node, false);
		}
	},
	/** Returns if the specified widget's visibility depends this widget. */
	_floatVisibleDependent: function (wgt) {
		for (; wgt; wgt = wgt.parent)
			if (wgt == this) return true;
			else if (!wgt.isVisible()) break;
		return false;
	},
	setDomVisible_: function (n, visible, opts) {
		if (!opts || opts.display)
			n.style.display = visible ? '': 'none';
		if (opts && opts.visibility)
			n.style.visibility = visible ? 'visible': 'hidden';
	},
	onChildAdded_: function (child) {
	},
	onChildRemoved_: function (child) {
	},
	onChildVisible_: function (child, visible) {
	},
	setTopmost: function () {
		var n = this.getNode();
		if (n && this._floating) {
			var zi = this._topZIndex();
			this._setZIndex(zi, true);

			for (var fs = zk.Widget._floating, j = 0, fl = fs.length;
			j < fl; ++j) { //parent first
				var w = fs[j].widget;
				if (this != w && zUtl.isAncestor(this, w) && w.isVisible()) {
					var n = fs[j].node
					if (n != w.getNode()) w.setFloatZIndex_(n, ++zi); //only a portion
					else w._setZIndex(++zi, true);
				}
			}
		}
	},
	/** Returns the topmost z-index for this widget.*/
	_topZIndex: function () {
		var zi = 0;
		for (var fs = zk.Widget._floating, j = fs.length; --j >= 0;) {
			var w = fs[j].widget;
			if (w._zIndex >= zi && !zUtl.isAncestor(this, w) && w.isVisible())
				zi = w._zIndex + 1;
		}
		return zi;
	},
	isFloating_: function () {
		return this._floating;
	},
	setFloating_: function (floating, opts) {
		if (this._floating != floating) {
			var fs = zk.Widget._floating;
			if (floating) {
				//parent first
				var inf = {widget: this, node: opts && opts.node? opts.node: this.getNode()},
					bindLevel = this.bindLevel;
				for (var j = fs.length;;) {
					if (--j < 0) {
						fs.unshift(inf);
						break;
					}
					if (bindLevel >= fs[j].widget.bindLevel) { //parent first
						fs.$addAt(j + 1, inf);
						break;
					}
				}
				this._floating = true;
			} else {
				for (var j = fs.length; --j >= 0;)
					if (fs[j].widget == this)
						fs.$removeAt(j);
				this._floating = false;
			}
		}
	},

	getWidth: function () {
		return this._width;
	},
	setWidth: function (width) {
		this._width = width;
		var n = this.getNode();
		if (n) n.style.width = width ? width: '';
	},
	getHeight: function () {
		return this._height;
	},
	setHeight: function (height) {
		this._height = height;
		var n = this.getNode();
		if (n) n.style.height = height ? height: '';
	},
	getZIndex: _zkf = function () {
		return this._zIndex;
	},
	getZindex: _zkf,
	setZIndex: _zkf = function (zIndex) { //2nd arg is fromServer
		this._setZIndex(zIndex);
	},
	setZindex: _zkf,
	_setZIndex: function (zIndex, fire) {
		if (this._zIndex != zIndex) {
			this._zIndex = zIndex;
			var n = this.getNode();
			if (n) {
				n.style.zIndex = zIndex >= 0 ? zIndex: '';
				if (fire) this.fire('onZIndex', zIndex, {ignorable: true});
			}
		}
	},
	getLeft: function () {
		return this._left;
	},
	setLeft: function (left) {
		this._left = left;
		var n = this.getNode();
		if (n) n.style.left = left ? left: '';
	},
	getTop: function () {
		return this._top;
	},
	setTop: function (top) {
		this._top = top;
		var n = this.getNode();
		if (n) n.style.top = top ? top: '';
	},
	getTooltiptext: function () {
		return this._tooltiptext;
	},
	setTooltiptext: function (tooltiptext) {
		this._tooltiptext = tooltiptext;
		var n = this.getNode();
		if (n) n.title = tooltiptext ? tooltiptext: '';
	},

	getStyle: function () {
		return this._style;
	},
	setStyle: function (style) {
		if (this._style != style) {
			this._style = style;
			this.updateDomStyle_();
		}
	},
	getSclass: function () {
		return this._sclass;
	},
	setSclass: function (sclass) {
		if (this._sclass != sclass) {
			this._sclass = sclass;
			this.updateDomClass_();
		}
	},
	getZclass: function () {
		return this._zclass;
	},
	setZclass: function (zclass) {
		if (this._zclass != zclass) {
			this._zclass = zclass;
			this.updateDomClass_();
		}
	},

	redraw: function (out) {
		var s = this.prolog;
		if (s) out.push(s);

		for (var p = this, mold = this._mold; p; p = p.superclass) {
			var f = p.$class.molds[mold];
			if (f) return f.apply(this, arguments);
		}
		throw "mold "+mold+" not found in "+this.className;
	},
	updateDomClass_: function () {
		if (this.desktop) {
			var n = this.getNode();
			if (n) n.className = this.domClass_();
		}
	},
	updateDomStyle_: function () {
		if (this.desktop)
			zDom.setStyle(this.getNode(), zDom.parseStyle(this.domStyle_()));
	},

	domStyle_: function (no) {
		var style = '';
		if (!this.isVisible() && (!no || !no.visible))
			style = 'display:none;';
		if (!no || !no.style) {
			var s = this.getStyle(); 
			if (s) {
				style += s;
				if (s.charAt(s.length - 1) != ';') style += ';';
			}
		}
		if (!no || !no.width) {
			var s = this.getWidth();
			if (s) style += 'width:' + s + ';';
		}
		if (!no || !no.height) {
			var s = this.getHeight();
			if (s) style += 'height:' + s + ';';
		}
		if (!no || !no.left) {
			var s = this.getLeft();
			if (s) style += 'left:' + s + ';';
		}
		if (!no || !no.top) {
			var s = this.getTop();
			if (s) style += 'top:' + s + ';';
		}
		if (!no || !no.zIndex) {
			var s = this.getZIndex();
			if (s >= 0) style += 'z-index:' + s + ';';
		}
		return style;
	},
	domClass_: function (no) {
		var scls = '';
		if (!no || !no.sclass) {
			var s = this.getSclass();
			if (s) scls = s;
		}
		if (!no || !no.zclass) {
			var s = this.getZclass();
			if (s) scls += (scls ? ' ': '') + s;
		}
		return scls;
	},
	domAttrs_: function (no) {
		var html = !no || !no.id ? ' id="' + this.uuid + '"': '';
		if (!no || !no.domStyle) {
			var s = this.domStyle_();
			if (s) html += ' style="' + s + '"';
		}
		if (!no || !no.domclass) {
			var s = this.domClass_();
			if (s) html += ' class="' + s + '"';
		}
		if (!no || !no.tooltiptext) {
			var s = this._tooltiptext;
			if (s) html += ' title="' + s + '"';
		}
		return html;
	},

	replaceHTML: function (n, desktop, skipper) {
		if (!desktop) {
			desktop = this.desktop;
			if (!zk.Desktop._ndt) zkboot('z_auto');
		}

		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(this, cf, true)) {
			zk.currentFocus = null;
		} else
			cf = null;

		var p = this.parent;
		if (p) p.replaceChildHTML_(this, n, desktop, skipper);
		else {
			if (n.z_wgt) n.z_wgt.unbind_(skipper); //unbind first (w/o removal)
			zDom.setOuterHTML(n, this._redrawHTML(skipper));
			this.bind_(desktop, skipper);
		}

		//TODO: if (zAu.valid) zAu.valid.fixerrboxes();
		if (cf && !zk.currentFocus) cf.focus();

		if (!skipper) {
			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
	insertHTML: function (n, where, desktop) {
		n.insertAdjacentHTML(where, this._redrawHTML());
		this.bind_(desktop);
	},
	_redrawHTML: function (skipper) {
		var out = [];
		this.redraw(out, skipper);
		return out.join('');
	},
	rerender: function (skipper) {
		if (this.desktop) {
			var n = this.getNode();
			if (n) {
				if (skipper) {
					var skipInfo = skipper.skip(this);
					if (skipInfo) {
						this.replaceHTML(n, null, skipper);

						skipper.restore(this, skipInfo);

						zWatch.fireDown('beforeSize', null, this);
						zWatch.fireDown('onSize', null, this);
						return; //done
					}
				}
				this.replaceHTML(n);
			}
		}
	},

	replaceChildHTML_: function (child, n, desktop, skipper) {
		if (n.z_wgt) n.z_wgt.unbind_(skipper); //unbind first (w/o removal)
		zDom.setOuterHTML(n, child._redrawHTML(skipper));
		child.bind_(desktop, skipper);
	},
	insertChildHTML_: function (child, before, desktop) {
		var bfn, ben;
		if (before) {
			bfn = before._getBeforeNode();
			if (!bfn) before = null;
		}
		if (!before)
			for (var w = this;;) {
				ben = w.getSubnode('cave') || w.getNode();
				if (ben) break;

				var w2 = w.nextSibling;
				if (w2) {
					bfn = w2._getBeforeNode();
					if (bfn) break;
				}

				if (!(w = w.parent)) {
					ben = document.body;
					break;
				}
			}

		if (bfn)
			zDom.insertHTMLBefore(bfn, child._redrawHTML());
		else
			zDom.insertHTMLBeforeEnd(ben, child._redrawHTML());
		child.bind_(desktop);
	},
	_getBeforeNode: function () {
		for (var w = this; w; w = w.nextSibling) {
			var n = w._getFirstNodeDown();
			if (n) return n;
		}
	},
	_getFirstNodeDown: function () {
		var n = this.getNode();
		if (n) return n;
		for (var w = this.firstChild; w; w = w.nextSibling) {
			n = w._getFirstNodeDown();
			if (n) return n;
		}
	},
	removeChildHTML_: function (child, prevsib) {
		var n = child.getNode();
		if (!n) child._prepareRemove(n = []);

		child.unbind_();

		if (n.$array)
			for (var j = n.length; --j >= 0;)
				zDom.remove(n[j]);
		else
			zDom.remove(n);
	},
	_prepareRemove: function (ary) {
		for (var w = this.firstChild; w; w = w.nextSibling) {
			var n = w.getNode();
			if (n) ary.push(n);
			else w._prepareRemove(ary);
		}
	},
	getSubnode: function (name) {
		var n = this._subnodes[name];
		if (!n && this.desktop)	n = this._subnodes[name] = zDom.$(this.uuid, name);
		return n;
	},
	getNode: function () {
		var n = this._node;
		if (!n && this.desktop && !this._nodeSolved) {
			n = zDom.$(this.uuid);
			if (n) {
				n.z_wgt = this;
				this._node = n;
			}
			this._nodeSolved = true;
		}
		return n;
	},

	bind_: function (desktop, skipper) {
		zk.Widget._binds[this.uuid] = this;

		if (!desktop) desktop = zk.Desktop.$(this.uuid);
		this.desktop = desktop;

		var p = this.parent;
		this.bindLevel = p ? p.bindLevel + 1: 0;

		for (var child = this.firstChild; child; child = child.nextSibling)
			if (!skipper || !skipper.skipped(this, child))
				child.bind_(desktop); //don't pass skipper
	},
	unbind_: function (skipper) {
		delete zk.Widget._binds[this.uuid];

		var n = this._node;
		if (n) {
			n.z_wgt = null;
			this._node = null;
		}
		for (var el in this._subnodes)
			this._subnodes[el] = null;
		
		this.desktop = null;
		this._nodeSolved = false;
		this.bindLevel = -1;

		for (var child = this.firstChild; child; child = child.nextSibling)
			if (!skipper || !skipper.skipped(this, child))
				child.unbind_(); //don't pass skipper
	},

	focus: function (timeout) {
		var node = this.getNode();
		if (node && this.isVisible() && this.canActivate({checkOnly:true})) {
			if (zDom.focus(node, timeout)) {
				zk.currentFocus = this;
				this.setTopmost();
				return true;
			}
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w.isVisible() && w.focus(timeout))
					return true;
		}
		return false;
	},
	canActivate: function (opts) {
		var modal = zk.currentModal;
		if (modal && !zUtl.isAncestor(modal, this)) {
			if (!opts || !opts.checkOnly) {
				var cf = zk.currentFocus;
				//Note: browser might change focus later, so delay a bit
				if (cf && zUtl.isAncestor(modal, cf)) cf.focus(0);
				else modal.focus(0);
			}
			return false;
		}
		return true;
	},

	//widget event//
	fireX: function (evt, timeout) {
		evt.currentTarget = this;
		var evtnm = evt.name,
			lsns = this._lsns[evtnm],
			len = lsns ? lsns.length: 0;
		if (len) {
			for (var j = 0; j < len;) {
				var inf = lsns[j++], o = inf[1];
				(inf[2] || o[evtnm]).call(o, evt);
				if (evt.stopped) return evt; //no more processing
			}
		}

		if (this.inServer && this.desktop) {
			var asap = this._asaps[evtnm];
			if (asap == null) {
				var ime = this.$class._importantEvts;
				if (ime && ime[evtnm])
					asap = false;
			}
			if (asap != null) //true or false
				zAu.send(evt, asap ? timeout >= 0 ? timeout: 38: -1);
		}
		return evt;
	},
	fire: function (evtnm, data, opts, timeout) {
		return this.fireX(new zk.Event(this, evtnm, data, opts), timeout);
	},
	listen: function (evtnm, listener, fn, priority) {
		if (!priority) priority = 0;
		var inf = [priority, listener, fn],
			lsns = this._lsns[evtnm];
		if (!lsns) lsns = this._lsns[evtnm] = [inf];
		else
			for (var j = lsns.length; --j >= 0;)
				if (lsns[j][0] >= priority) {
					lsns.$addAt(j + 1, inf);
					break;
				}
	},
	unlisten: function (evtnm, listener, fn) {
		var lsns = this._lsns[evtnm];
		for (var j = lsns ? lsns.length: 0; --j >= 0;)
			if (lsns[j][1] == listener && lsns[j][2] == fn) {
				lsns.$removeAt(j);
				return true;
			}
		return false;
	},
	isListen: function (evtnm) {
		if (this._asaps[evtnm]) return true;
		var lsns = this._lsns[evtnm];
		return lsns && lsns.length;
	},
	setListeners: function (infs) {
		for (var evtnm in infs)
			this._setListener(evtnm, infs[evtnm]);
	},
	setListener: function (inf) {
		this._setListener(inf[0], inf[1]);
	},
	_setListener: function (evtnm, fn) {
		var lsns = this._$lsns,
			oldfn = lsns[evtnm];
		if (oldfn) { //unlisten first
			delete lsns[evtnm];
			this.unlisten(evtnm, this, oldfn);
		}
		if (fn) {
			if (typeof fn != 'function') fn = new Function(fn);
			this.listen(evtnm, this, lsns[evtnm] = fn);
		}
	},
	setMethods: function (infs) {
		for (var mtdnm in infs)
			this._setMethod(mtdnm, infs[mtdnm]);
	},
	setMethod: function (inf) {
		this._setMethod(inf[0], inf[1]);
	},
	_setMethod: function (mtdnm, fn) {
		if (fn) {
			if (typeof fn != 'function') fn = eval(fn);
			var oldnm = '$' + mtdnm;
			if (!this[oldnm]) this[oldnm] = this[mtdnm]; //only once
			this[mtdnm] = fn;
				//use eval, since complete func decl
		} else {
			var oldnm = '$' + mtdnm;
			this[mtdnm] = this[oldnm]; //restore
			delete this[oldnm];
		}
	},

	//ZK event handling//
	doClick_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doClick_(evt);
		}	
	},
	doDoubleClick_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doDoubleClick_(evt);
		}	
	},
	doRightClick_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doRightClick_(evt);
		}	
	},
	doMouseOver_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOver_(evt);
		}	
	},
	doMouseOut_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOut_(evt);
		}	
	},
	doMouseDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseDown_(evt);
		}	
	},
	doMouseUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseUp_(evt);
		}	
	},
	doMouseMove_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseMove_(evt);
		}	
	},
	doKeyDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyDown_(evt);
		}	
	},
	doKeyUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyUp_(evt);
		}	
	},
	doKeyPress_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyPress_(evt);
		}	
	},

	//DOM event handling//
	domFocus_: function () {
		if (!this.canActivate()) return false;

		zk.currentFocus = this;
		zWatch.fire('onFloatUp', null, this); //notify all

		if (this.isListen('onFocus'))
			this.fire('onFocus');
		return true;
	},
	domBlur_: function () {
		//due to domMouseDown called, zk.currentFocus already point to the
		//widget gaining focus
		if (zk.currentFocus == this) zk.currentFocus = null;
		if (this.isListen('onBlur'))
			this.fire('onBlur');
	}

}, {
	_floating: [], //[{widget,node}]
	$: function (n, strict) {
		var binds = zk.Widget._binds;
		if (typeof n == 'string') {
			var j = n.indexOf('$');
			return binds[j >= 0 ? n.substring(0, j): n];
		}

		if (!n || zk.Widget.isInstance(n)) return n;
		else n = n.z_target || n.target || n.srcElement || n; //check DOM event first
				//z_target: used if we have to override the default

		for (; n; n = zDom.parentNode(n)) {
			var wgt = n.z_wgt;
			if (wgt) return wgt;

			var id = n.id;
			if (id) {
				var j = id.indexOf('$');
				if (j >= 0) {
					id = id.substring(0, j);
					if (strict) {
						wgt = binds[id];
						if (wgt) {
							var n2 = wgt.getNode();
							if (n2 && zDom.isAncestor(n2, n)) return wgt;
							continue;
						}
					}
				}
				wgt = binds[id];
				if (wgt) return wgt;
			}
		}
		return null;
	},
	_binds: {}, //Map(uuid, wgt): bind but no node

	//Event Handling//
	domMouseDown: function (wgt) {
		var modal = zk.currentModal;
		if (modal && !wgt) {
			var cf = zk.currentFocus;
			//Note: browser might change focus later, so delay a bit
			//(it doesn't work if we stop event instead of delay - IE)
			if (cf && zUtl.isAncestor(modal, cf)) cf.focus(0);
			else modal.focus(0);
		} else if (!wgt || wgt.canActivate()) {
			zk.currentFocus = wgt;
			if (wgt) zWatch.fire('onFloatUp', null, wgt); //notify all
		}
	},

	//uuid//
	uuid: function (id) {
		var uuid = typeof id == 'object' ? id.id || '' : id,
			j = uuid.indexOf('$');
		return j >= 0 ? uuid.substring(0, j): id;
	},
	nextUuid: function () {
		return '_z_' + zk.Widget._nextUuid++;
	},
	_nextUuid: 0,

	isAutoId: function (id) {
		return !id || id.startsWith('_z_') || id.startsWith('z_');
	},

	_fixBindLevel: function (wgt, v) {
		var $Widget = zk.Widget;
		wgt.bindLevel = v++;
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			$Widget._fixBindLevel(wgt, v);
	},

	_addIdSpace: function (wgt) {
		if (wgt._fellows) wgt._fellows[wgt.id] = wgt;
		var p = wgt.parent;
		if (p) {
			p = p.getSpaceOwner();
			if (p) p._fellows[wgt.id] = wgt;
		}
	},
	_rmIdSpace: function (wgt) {
		if (wgt._fellows) delete wgt._fellows[wgt.id];
		var p = wgt.parent;
		if (p) {
			p = p.getSpaceOwner();
			if (p) delete p._fellows[wgt.id];
		}
	},
	_addIdSpaceDown: function (wgt) {
		var ow = wgt.parent;
		ow = ow ? ow.getSpaceOwner(): null;
		if (ow) {
			var fn = zk.Widget._addIdSpaceDown0;
			fn(wgt, ow, fn);
		}
	},
	_addIdSpaceDown0: function (wgt, owner, fn) {
		if (wgt.id) owner._fellows[wgt.id] = wgt;
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			fn(wgt, owner, fn);
	},
	_rmIdSpaceDown: function (wgt) {
		var ow = wgt.parent;
		ow = ow ? ow.getSpaceOwner(): null;
		if (ow) {
			var fn = zk.Widget._rmIdSpaceDown0;
			fn(wgt, ow, fn);
		}
	},
	_rmIdSpaceDown0: function (wgt, owner, fn) {
		if (wgt.id) delete owner._fellows[wgt.id];
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			fn(wgt, owner, fn);
	},

	_global: {} //a global ID space
});

zk.Page = zk.$extends(zk.Widget, {//unlik server, we derive from Widget!
	_style: "width:100%;height:100%",
	className: 'zk.Page',

	$init: function (props, contained) {
		this._fellows = {};

		this.$super('$init', props);

		if (contained) zk.Page.contained.push(this);
	},
	redraw: function (out, skipper) {
		out.push('<div id="', this.uuid, '" style="', this.getStyle(), '">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out, skipper);
		out.push('</div>');
	}

},{
	contained: []
});

zk.Desktop = zk.$extends(zk.Widget, {
	bindLevel: 0,
	className: 'zk.Desktop',

	$init: function (dtid, updateURI) {
		this.$super('$init', {uuid: dtid}); //id also uuid

		this._aureqs = [];

		var zkdt = zk.Desktop, dts = zkdt.all, dt = dts[dtid];
		if (!dt) {
			this.id = dtid;
			this.updateURI = updateURI;
			dts[dtid] = this;
			++zkdt._ndt;
			if (!zkdt._dt) zkdt._dt = this; //default desktop
		} else if (updateURI)
			dt.updateURI = updateURI;

		zkdt.sync();
	},
	_exists: function () {
		var id = this._pguid; //_pguid not assigned at beginning
		return !id || zDom.$(id);
	},
	bind_: zk.$void,
	unbind_: zk.$void,
	setId: zk.$void
},{
	$: function (dtid) {
		var zkdt = zk.Desktop, dts = zkdt.all, w;
		if (zkdt._ndt > 1) {
			if (typeof dtid == 'string') {
				w = dts[dtid];
				if (w) return w;
			}
			w = zk.Widget.$(dtid);
			if (w)
				for (; w; w = w.parent) {
					if (w.desktop)
						return w.desktop;
					if (w.$instanceof(zkdt))
						return w;
				}
		}
		if (w = zkdt._dt) return w;
		for (dtid in dts)
			return dts[dtid];
	},
	all: {},
	_ndt: 0,
	sync: function () {
		var zkdt = zk.Desktop, dts = zkdt.all;
		if (zkdt._dt && !zkdt._dt._exists()) //removed
			zkdt._dt = null;
		for (var dtid in dts) {
			var dt = dts[dtid];
			if (!dt._exists()) { //removed
				delete dts[dtid];
				--zkdt._ndt;
			} else if (!zkdt._dt)
				zkdt._dt = dt;
		}
	}
});

zk.Skipper = zk.$extends(zk.Object, {
	skipped: function (wgt, child) {
		return wgt.caption != child;
	},
	skip: function (wgt, skipId) {
		var skip = zDom.$(skipId || (wgt.uuid + '$cave'));
		if (skip && skip.firstChild) {
			zDom.remove(skip);
			return skip;
		}
		return null;
	},
	restore: function (wgt, skip) {
		if (skip) {
			var loc = zDom.$(skip.id);
			for (var el; el = skip.firstChild;) {
				skip.removeChild(el);
				loc.appendChild(el);
			}
		}
	}
});
zk.Skipper.nonCaptionSkipper = new zk.Skipper();

zk.Native = zk.$extends(zk.Widget, {
	className: 'zk.Native',

	redraw: function (out) {
		var s = this.prolog;
		if (s) out.push(s);

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		s = this.epilog;
		if (s) out.push(s);
	}
});

zk.Macro = zk.$extends(zk.Widget, {
	className: 'zk.Macro',

	redraw: function (out) {
		out.push('<span', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</span>');
	}
});

zk.RefWidget = zk.$extends(zk.Widget, {
	bind_: function () {
		var w = zk.Widget.$(this.uuid);
		if (!w || !w.desktop) throw 'illegal: '+w;

		var p = w.parent, q;
		if (p) { //shall be a desktop
			var dt = w.desktop, n = w._node;
			w.desktop = w._node = null; //avoid unbind/bind
			p.removeChild(w);
			w.desktop = dt; w._node = n;
		}

		p = w.parent = this.parent,
		q = w.previousSibling = this.previousSibling;
		if (q) q.nextSibling = w;
		else if (p) p.firstChild = w;

		q = w.nextSibling = this.nextSibling;
		if (q) q.previousSibling = w;
		else if (p) p.lastChild = w;

		//no need to call super since it is bound
	}
});


zPkg = {
	loading: 0,

	/** Called after the whole package is declared. */
	end: function (pkg) {
		if (!zPkg._lding.$remove(pkg))
			zPkg._lded[pkg] = true;
			//specified in lang.xml (or in HTML directly)

		var al2 = zPkg._afld2[pkg];
		if (al2) {
			delete zPkg._afld2[pkg];
			for (var fn, aflds = zPkg._aflds; fn = al2.pop();)
				aflds.unshift(fn);
		}

		if (!zPkg._updCnt()) {
			try {
				zEvt.enableESC();
				zUtl.destroyProgressbox("zk_loadprog");
			} catch (ex) {
			}

			for (var fn, aflds = zPkg._aflds; fn = aflds.shift();)
				fn();
		}
	},
	isLoaded: function (pkg) {
		return zPkg._lded[pkg];
	},
	load: function (pkg, dt) {
		var pkglds = zPkg._lded;
		if (!pkg || pkglds[pkg]) return !zPkg.loading;
			//since pkg might be loading (-> return false)

		pkglds[pkg] = true;

		var deps = zPkg._deps[pkg];
		if (deps) {
			delete zPkg._deps[pkg];
			for (var pn; pn = deps.unshift();)
				zPkg.load(pn);
		}

		//We don't use e.onload since Safari doesn't support t
		//See also Bug 1815074

		zPkg._lding.unshift(pkg);
		if (zPkg._updCnt() == 1) {
			zEvt.disableESC();
			setTimeout(zPkg._pgbox, 380);
		}

		var modver = pkg.indexOf('.');
		if (modver) modver = zPkg.getVersion(pkg.substring(0, modver));
		if (!modver) modver = zk.build;

		var e = document.createElement("script"),
			uri = pkg.replace(/\./g, '/') + "/zk.wpd";
		e.type = "text/javascript";
		e.charset = "UTF-8";

		if (uri.charAt(0) != '/') uri = '/' + uri;
		if (modver) uri = "/web/_zv" + modver + "/js" + uri;
		else uri = "/web/js" + uri;

		e.src = zAu.comURI(uri, dt);
		document.getElementsByTagName("HEAD")[0].appendChild(e);
	},
	_lded: {'zk': true}, //loaded
	_lding: [], //loading
	_aflds: [],
	_afld2: {},
	_deps: {},

	_ldmsg: function () {
		var msg = '';
		for (var lding = zPkg._lding, j = lding.length; --j >=0;) {
			if (msg) msg += ', ';
			msg += lding[j];
		}
		return msg;
	},
	_updCnt: function () {
		zPkg.loading = zPkg._lding.length;
		try {
			var n = zDom.$("zk_loadcnt");
			if (n) n.innerHTML = zPkg._ldmsg();
		} catch (ex) {
		}
		return zPkg.loading;
	},
	_pgbox: function () {
		if (zPkg.loading || window.dbg_progressbox) { //dbg_progressbox: debug purpose
			var n = zDom.$("zk_loadprog");
			if (!n)
				zUtl.progressbox("zk_loadprog",
					'Loading <span id="zk_loadcnt">'+zPkg._ldmsg()+'</span>',
					true);
		}	
	},

	getVersion: function (pkg) {
		return zPkg._pkgVers[pkg];
	},
	setVersion: function (pkg, ver) {
		zPkg._pkgVers[pkg] = ver;
	},
	depends: function (a, b) {
		if (a && b) //a depends on b
			if (zPkg._lded[a]) zPkg.load(b);
			else {
				var deps = zPkg._deps;
				if (deps[a]) deps[a].push(b);
				else deps[a] = [b];
			}
	},
	_pkgVers: {}
};
zk.afterLoad = function (a, b) { //part of zk
	if (typeof a == 'string') {
		if (!b) return;
		if (zPkg._lded[a]) a = b;
		else {
			var al2 = zPkg._afld2;
			if (al2[a]) al2[a].push(b);
			else al2[a] = [b];
			return;
		}
	}

	if (zPkg.loading) {
		zPkg._aflds.push(a);
		return true;
	}
	a();
};


var _zkmt = zUtl.now(); //JS loaded

function zkboot(dtid, updateURI, force) {
	if (!zk.sysInited) zkm.sysInit();

	var zkdt = zk.Desktop, dt;
	if (!force) {
		if (dtid) {
			dt = zkdt.all[dtid];
			if (dt) {
				if (!dt.updateURI) dt.updateURI = updateURI;
				return dt;
			}
		} else
			dt = zkdt._dt;
	}
	return dt || new zkdt(dtid, updateURI);
}

function zkblbg(binding) {
	zk.mounting = true;
	zkm.binding = binding;
	var t = 390 - (zUtl.now() - _zkmt);
	zk.startProcessing(t > 0 ? t: 0);
}

function zkpgbg(pguid, style, dtid, contained, updateURI) {
	var props = {};
	if (style) props.style = style;
	if (dtid) zkdtbg(dtid, updateURI)._pguid = pguid;
	zkm.push({type: "#p", uuid: pguid, contained: contained, props: props});
}
function zkbg(type, uuid, mold, props) {
	zkm.push({type: type, uuid: uuid, mold: mold, props: props});
}
function zkb2(uuid, type, props) { //zhtml
	zkm.push({type: type||'zhtml.Widget', uuid: uuid, props: props});
}
function zkdtbg(dtid, updateURI) {
	var dt = zk.Desktop.$(dtid);
	if (dt == null) dt = new zk.Desktop(dtid, updateURI);
	else if (updateURI) dt.updateURI = updateURI;
	zkm.curdt = dt;
	return dt;
}

//Init Only//
function zkver() {
	var args = arguments, len = args.length;
	zk.version = args[0];
	zk.build = args[1];

	for (var j = 2; j < len; j += 2)
		zPkg.setVersion(args[j], args[j + 1]);
}

function zkopt(opts) {
	for (var nm in opts) {
		var val = opts[nm];
		switch (nm) {
		case "pd": zk.procDelay = val; break;
		case "td": zk.tipDelay =  val; break;
		case "rd": zk.resendDelay = val; break;
		case "dj": zk.debugJS = val; break;
		case "kd": zk.keepDesktop = val; break;
		case "pf": zk.pfmeter = val; break;
		case "cd": zk.clickFilterDelay = val; break;
		}
	}
}

function zkmld(wgtcls, molds) {
	if (!wgtcls.superclass) {
		zk.afterLoad(function () {zkmld(wgtcls, molds);});
		return;
	}

	var ms = wgtcls.molds = {};
	for (var nm in molds) {
		var fn = molds[nm];
		ms[nm] = typeof fn == 'function' ? fn: fn[0].molds[fn[1]];
	}		
}
function zkam(fn) {
	if (zk.mounting || !zk.sysInited) {
		zkm._afMts.push(fn);
		return true;
	}
	setTimeout(fn, 0);
};
zk.afterMount = zkam; //part of zk

/** Used internally. */
zkm = {
	push: function(w) {
		w.children = [];
		if (zkm._wgts.length > 0)
			zkm._wgts[0].children.push(w);
		zkm._wgts.unshift(w);
	},
	pop: function() {
		var w = zkm._wgts.shift();
		if (!zkm._wgts.length) {
			var cfi = zkm._crInf0;
			cfi.push([zkm.curdt, w, zkm.binding]);
			cfi.stub = zAu.stub;
			zAu.stub = null;
			zkm.exec(zkm.mount);
		}
	},
	top: function() {
		return zkm._wgts[0];
	},
	end: function() {
		zkm._wgts = [];
		zkm._curdt = null;
		zkm.binding = false;
	},

	sysInit: function() {
		zk.sysInited = true;
		zkm.sysInit = null; //free

		var ebc = zk.xbodyClass;
		if (ebc) {
			zk.xbodyClass = null;
			var n = document.body
				cn = n.className;
			if (cn.length) cn += ' ';
			n.className = cn + ebc;
		}

		zEvt.listen(document, "keydown", zkm.docKeyDown);
		zEvt.listen(document, "keyup", zkm.docKeyUp);
		zEvt.listen(document, "keypress", zkm.docKeyPress);

		zEvt.listen(document, "mousedown", zkm.docMouseDown);
		zEvt.listen(document, "mouseup", zkm.docMouseUp);
		zEvt.listen(document, "mousemove", zkm.docMouseMove);
		zEvt.listen(document, "mouseover", zkm.docMouseOver);
		zEvt.listen(document, "mouseout", zkm.docMouseOut);

		zEvt.listen(document, "click", zkm.docClick);
		zEvt.listen(document, "dblclick", zkm.docDblClick);
		zEvt.listen(document, "contextmenu", zkm.docCtxMenu);

		zEvt.listen(window, "resize", zkm.docResize);
		zEvt.listen(window, "scroll", zkm.docScroll);

		zkm._oldUnload = window.onunload;
		window.onunload = zkm.wndUnload; //unable to use zEvt.listen

		zkm._oldBfUnload = window.onbeforeunload;
		window.onbeforeunload = zkm.wndBfUnload;
	},
	mount: function() {
		//1. load JS
		var cfi = zkm._crInf0;
		for (var j = cfi.length; --j >= 0;) {
			var inf = cfi[j];
			if (!inf.jsLoad) {
				inf.jsLoad = true;
				zkm.pkgLoad(inf[1]);
				zkm.exec(zkm.mount);
				return;
			}
		}

		//2. create wgt
		var stub = cfi.stub;
		if (stub) { //AU
			cfi.stub = null;
			zkm.mtAU(stub);
		} else { //browser loading
			zk.bootstrapping = true;
			if (zk.sysInited)
				zkm.mtBL();
			else if (document.readyState) {
				var tid = setInterval(function(){
					if (/loaded|complete/.test(document.readyState)) {
						clearInterval(tid);
						zkm.mtBL();
					}
				}, 50);
			} else //gecko
				setTimeout(zkm.mtBL, 120);
				//don't count on DOMContentLoaded since the page might
				//be loaded by another ajax solution (i.e., portal)
				//Also, Bug 1619959: FF not fire it if in 2nd iframe
		}
	},
	/** mount for browser loading */
	mtBL: function() {
		if (!zk.sysInited) zkm.sysInit();

		if (zPkg.loading) {
			zk.afterLoad(zkm.mtBL);
			return;
		}

		var crInf0 = zkm._crInf0,
			inf = crInf0.shift();
		if (inf) {
			zkm._crInf1.push([inf[0], zkm.create(inf[0], inf[1]), inf[2]]);
				//desktop as parent for browser loading
	
			if (crInf0.length)
				return zkm.exec(zkm.mtBL);
		}

		zkm.mtBL0();
	},
	mtBL0: function() {
		if (zkm._crInf0.length)
			return; //another page started

		if (zPkg.loading) {
			zk.afterLoad(zkm.mtBL0);
			return;
		}

		var crInf1 = zkm._crInf1,
			inf = crInf1.shift();
		if (inf) {
			var wgt = inf[1];
			if (inf[2]) wgt.bind_(inf[0]); //binding
			else wgt.replaceHTML(wgt.uuid, inf[0]);
			return zkm.exec(zkm.mtBL0); //loop back to check if loading
		}

		setTimeout(zkm.mtBL1, 0);
			//use timeout since there might be multiple zkblbg
	},
	mtBL1: function () {
		if (zkm._crInf0.length || zkm._crInf1.length)
			return; //another page started

		for (var fn; fn = zkm._afMts.shift();) {
			fn();
			if (zPkg.loading) {
				zk.afterLoad(zkm.mtBL1); //fn might load packages
				return;
			}
		}

		zk.mounting = zk.bootstrapping = false;
		zk.endProcessing();
	},

	/** mount for AU */
	mtAU: function (stub) {
		if (zPkg.loading) {
			zk.afterLoad(function () {zkm.mtAU(stub);});
			return;
		}

		var crInf0 = zkm._crInf0,
			inf = crInf0.shift();
		if (inf) {
			stub(zkm.create(null, inf[1]));
			if (crInf0.length)
				return zkm.exec(function () {zkm.mtAU(stub);}); //loop back to check if loading
		}

		zkm.mtAU0();
	},
	mtAU0: function () {
		if (zAu._moreCmds()) {
			zk.mounting = false;
			zAu.doCmds();
			return; //wait zAu to call
		}

		for (var fn; fn = zkm._afMts.shift();) {
			fn();
			if (zPkg.loading) {
				zk.afterLoad(zkm.mtAU0); //fn might load packages
				return;
			}
		}

		zk.mounting = false;
		zAu.doCmds(); //server-push (w/ afterLoad) and pfdone
	},

	/** create the widget tree. */
	create: function (parent, wginf) {
		var wgt, props = wginf.props || {};
		if (wginf.type == "#p") {
			wgt = new zk.Page({uuid: wginf.uuid}, wginf.contained);
			wgt.inServer = true;
			if (parent) parent.appendChild(wgt);
		} else {
			var cls = zk.$import(wginf.type),
				uuid = wginf.uuid,
				initOpts = {uuid: uuid},
				v = wginf.mold;
			if (v) initOpts.mold = v;
			var wgt = new cls(initOpts);
			wgt.inServer = true;
			if (parent) parent.appendChild(wgt);

			//z_ea: value embedded as element's text
			v = props.z_ea;
			if (v) {
				delete props.z_ea;
				var embed = zDom.$(uuid);
				if (embed) {
					var val = embed.innerHTML;
					if (v.charAt(0) == '$') { //decode
						v = v.substring(1);
						val = zUtl.decodeXML(val);
					}
					props[v] = val;
				}
			}

			//z_al: evaluated after load
			v = props.z_al;
			if (v) {
				delete props.z_al;
				for (var p in v)
					props[p] = v[p](); //must be func
			}
		}

		wgt.set(props);

		for (var j = 0, childs = wginf.children, len = childs.length;
		j < len; ++j)
			zkm.create(wgt, childs[j]);
		return wgt;
	},

	/** Loads package of a widget tree. */
	pkgLoad: function (w) {
		var type = w.type; j = type.lastIndexOf('.');
		if (j >= 0)
			zPkg.load(type.substring(0, j), zkm.curdt);

		//z_pk: pkgs to load
		var pkgs = w.z_pk;
		if (pkgs) {
			delete w.z_pk;
			pkgs = pkgs.split(',');
			for (var j = 0, l = pkgs.length; j < l;)
				zPkg.load(pkgs[j++].trim());
		}

		for (var children = w.children, len = children.length, j = 0; j < len;++j)
			zkm.pkgLoad(children[j]);
	},

	/** exec and delay if too busy, so progressbox has a chance to show. */
	exec: function (fn) {
		var t = zUtl.now(), dt = t - _zkmt;
		if (dt > 300) { //huge page
			_zkmt = t;
			dt >>= 4;
			setTimeout(fn, dt < 35 ? dt: 35); //breathe
				//IE optimize the display if delay is too short
		} else
			fn();
	},

	_wgts: [],
	_crInf0: [], //create info
	_crInf1: [], //create info
	_afMts: [], //afterMount funcs

	//Event Handler//
	docMouseDown: function (evt) {
		evt = evt || window.event;
		var target = zEvt.target(evt),
			$Widget = zk.Widget,
			wgt = $Widget.$(evt, true);
		zk.lastPointer[0] = zEvt.x(evt);
		zk.lastPointer[1] = zEvt.y(evt);

		if (target != document.body && target != document.body.parentNode) //not click on scrollbar
			$Widget.domMouseDown(wgt); //null if mask

		if (wgt) {
			var wevt = new zk.Event(wgt, 'onMouseDown', zkm._mouseData(evt, wgt), null, evt);
			wgt.doMouseDown_(wevt);
			if (wevt.stopped) zEvt.stop(evt);
		}
	},
	_mouseData: function (evt, wgt) {
		var n = zEvt.target(evt);
		if (!n || !n.id || n.id.indexOf('$') >= 0)
			n = wgt.getNode(); //use n if possible for better performance
		return zEvt.mouseData(evt, n);
	},
	docMouseUp: function (evt) {
		evt = evt || window.event;
		var wgt = zk.mouseCapture;
		if (wgt) zk.mouseCapture = null;
		else wgt = zk.Widget.$(evt, true);
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onMouseUp', zkm._mouseData(evt, wgt), null, evt);
			wgt.doMouseUp_(wevt);
			if (wevt.stopped) zEvt.stop(evt);
		}
	},
	docMouseMove: function (evt) {
		evt = evt || window.event;
		zk.currentPointer[0] = zEvt.x(evt);
		zk.currentPointer[1] = zEvt.y(evt);

		var wgt = zk.mouseCapture;
		if (!wgt) wgt = zk.Widget.$(evt, true);
		if (wgt)
			wgt.doMouseMove_(new zk.Event(wgt, 'onMouseMove', zkm._mouseData(evt, wgt), null, evt));
	},
	docMouseOver: function (evt) {
		evt = evt || window.event;
		zk.currentPointer[0] = zEvt.x(evt);
		zk.currentPointer[1] = zEvt.y(evt);

		var wgt = zk.Widget.$(evt, true);
		if (wgt)
			wgt.doMouseOver_(new zk.Event(wgt, 'onMouseOver', zkm._mouseData(evt, wgt), null, evt));
	},
	docMouseOut: function (evt) {
		evt = evt || window.event;
		var wgt = zk.Widget.$(evt, true);
		if (wgt)
			wgt.doMouseOut_(new zk.Event(wgt, 'onMouseOut', zkm._mouseData(evt, wgt), null, evt));
	},
	docKeyDown: function (evt) {
		evt = evt || window.event;
		var wgt = zk.Widget.$(evt, true);
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onKeyDown', zEvt.keyData(evt), null, evt);
			wgt.doKeyDown_(wevt);
			if (wevt.stopped) zEvt.stop(evt);
		}
		if (zEvt.keyCode(evt) == 27 && zAu.shallIgnoreESC()) //Bug 1927788: prevent FF from closing connection
			zEvt.stop(evt); //eat
	},
	docKeyUp: function (evt) {
		evt = evt || window.event;
		var wgt = zk.keyCapture;
		if (wgt) zk.keyCapture = null;
		else wgt = zk.Widget.$(evt, true);
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onKeyUp', zEvt.keyData(evt), null, evt);
			wgt.doKeyUp_(wevt);
			if (wevt.stopped) zEvt.stop(evt);
		}
	},
	docKeyPress: function (evt) {
		evt = evt || window.event;
		var wgt = zk.keyCapture;
		if (!wgt) wgt = zk.Widget.$(evt, true);
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onKeyPress', zEvt.keyData(evt), null, evt);
			wgt.doKeyPress_(wevt);
			if (wevt.stopped) zEvt.stop(evt);
		}
	},
	docClick: function (evt) {
		if (zk.processing) return;

		evt = evt || window.event;
		if (evt.which == 1 || (evt.button == 0 || evt.button == 1)) {
			var wgt = zk.Widget.$(evt, true);
			if (wgt) {
				var wevt = new zk.Event(wgt, 'onClick', zkm._mouseData(evt, wgt), {ctl:true}, evt)
				wgt.doClick_(wevt);
				if (wevt.stopped) zEvt.stop(evt);
			}
			//don't return anything. Otherwise, it replaces event.returnValue in IE (Bug 1541132)
		}		
	},
	docDblClick: function (evt) {
		if (zk.processing) return;

		evt = evt || window.event;
		var wgt = zk.Widget.$(evt, true);
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onDoubleClick', zkm._mouseData(evt, wgt), {ctl:true}, evt);
			wgt.doDoubleClick_(wevt);

			if (wevt.stopped) {
				zEvt.stop(evt); //prevent browser default
				return false;
			}
		}
	},
	docCtxMenu: function (evt) {
		if (zk.processing) return;

		evt = evt || window.event;
		zk.lastPointer[0] = zEvt.x(evt);
		zk.lastPointer[1] = zEvt.y(evt);

		var wgt = zk.Widget.$(evt, true);
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onRightClick', zkm._mouseData(evt, wgt), {ctl:true}, evt);
			wgt.doRightClick_(wevt);

			if (wevt.stopped) {
				zEvt.stop(evt); //prevent browser default
				return false;
			}
		}
		return !zk.ie || evt.returnValue;
	},
	docScroll: function () {
		zWatch.fire('onScroll'); //notify all
	},
	docResize: function () {
		if (!zk.sysInited || zk.mounting)
			return; //IE6: it sometimes fires an "extra" onResize in loading

	//Tom Yeh: 20051230:
	//1. In certain case, IE will keep sending onresize (because
	//grid/listbox may adjust size, which causes IE to send onresize again)
	//To avoid this endless loop, we ignore onresize a while if docResize
	//was called
	//
	//2. IE keeps sending onresize when dragging the browser's border,
	//so we have to filter (most of) them out

		var now = zUtl.now(), resz = zkm._resz;
		if ((resz.lastTime && now < resz.lastTime) || resz._inResize)
			return; //ignore resize for a while (since onSize might trigger onsize)

		var delay = zk.ie ? 250: 50;
		resz.time = now + delay - 1; //handle it later
		setTimeout(zkm.docDidResize, delay);
	},
	docDidResize: function () {
		var resz = zkm._resz;
		if (!resz.time) return; //already handled

		var now = zUtl.now();
		if (zk.mounting || zPkg.loading || zAnima.count || now < resz.time) {
			setTimeout(zkm.docDidResize, 10);
			return;
		}

		resz.time = null; //handled
		resz.lastTime = now + 1000;
			//ignore following for a while if processing (in slow machine)

		zAu._onClientInfo();

		resz._inResize = true;
		try {
			zWatch.fire('beforeSize'); //notify all
			zWatch.fire('onSize'); //notify all
			resz.lastTime = zUtl.now() + 8;
		} finally {
			resz._inResize = false;
		}
	},
	_resz: {},

	wndUnload: function () {
		zk.unloading = true; //to disable error message

		//20061109: Tom Yeh: Failed to disable Opera's cache, so it's better not
		//to remove the desktop.
		//Good news: Opera preserves the most udpated content, when BACK to
		//a cached page, its content. OTOH, IE/FF/Safari cannot.
		//Note: Safari won't send rmDesktop when onunload is called
		var bRmDesktop = !zk.opera && !zk.keepDesktop;
		if (bRmDesktop || zk.pfmeter) {
			try {
				var dts = zk.Desktop.all;
				for (var dtid in dts) {
					var req = zUtl.newAjax(),
						content = "dtid="+dtid+"&cmd.0="+
							(bRmDesktop?"rmDesktop":"dummy"),
						dt = dts[dtid],
						uri = zAu.comURI(null, dt);
					req.open("POST", zk.ie ? uri+"?"+content: uri, true);
					req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
					if (zk.pfmeter) zAu._pfsend(dtid, req, true);
					if (zk.ie) req.send(null);
					else req.send(content);
				}
			} catch (e) { //silent
			}
		}

		if (zkm._oldUnload) zkm._oldUnload.apply(window, arguments);
	},
	wndBfUnload: function () {
		if (!zk.skipBfUnload) {
			if (zk.confirmClose)
				return zk.confirmClose;

			for (var bfs = zkm._bfs, j = 0; j < bfs.length; ++j) {
				var s = bfs[j]();
				if (s) return s;
			}
		}

		if (zkm._oldBfUnload) {
			var s = zkm._oldBfUnload.apply(window, arguments);
			if (s) return s;
		}

		zk.unloading = true; //FF3 aborts ajax before calling window.onunload
		//Return nothing
	},
	_bfs: []
};
zkble = zkm.end;
zke = zkpge = zkm.pop;

zk.beforeUnload = function (fn, opts) { //part of zk
	if (opts && opts.remove) zkm._bfs.$remove(fn);
	else zkm._bfs.push(fn);
};


zHistory = {
	/** Sets a bookmark that user can use forward and back buttons */
	bookmark: function (nm) {
		if (zHistory._curbk != nm) {
			zHistory._curbk = nm; //to avoid loop back the server
			var encnm = encodeURIComponent(nm);
			location.hash = zk.safari || !encnm ? encnm: '#' + encnm;
			zHistory._bkIframe(nm);
			zHistory.onURLChange();
		}
	},
	/** Checks whether the bookmark is changed. */
	checkBookmark: function() {
		var nm = zHistory.getBookmark();
		if (nm != zHistory._curbk) {
			zHistory._curbk = nm;
			zAu.send(new zk.Event(null, "onBookmarkChange", nm), 50);
			zHistory.onURLChange();
		}
	},
	getBookmark: function () {
		var nm = location.hash;
		var j = nm.indexOf('#');
		return j >= 0 ? decodeURIComponent(nm.substring(j + 1)): '';
	},
	/** bookmark iframe */
	_bkIframe: zk.ie ? function (nm) {
		//Bug 2019171: we have to create iframe frist
		var url = zAu.comURI("/web/js/zk/html/history.html", null, true),
			ifr = $e('zk_histy');
		if (!ifr) ifr = zk.newFrame('zk_histy', url, "display:none");

		if (nm) url += '?' +encodeURIComponent(nm);
		ifr.src = url;
	}: zk.$void,
	/** called when history.html is loaded*/
	onHistoryLoaded: zk.ie ? function (src) {
		var j = src.indexOf('?');
		var nm = j >= 0 ? src.substring(j + 1): '';
		location.hash = nm ? /*zk.safari ? nm:*/ '#' + nm: '';
		zHistory.checkBookmark();
	}: zk.$void,

	/** */
	onURLChange: function () {
		//TODO
	}
};

zHistory._curbk = "";
zk.afterMount(function () { // Bug 1847708
	zHistory.checkBookmark(); // We don't need to wait for the first time.
	setInterval("zHistory.checkBookmark()", 250);
	//Though IE use history.html, timer is still required 
	//because user might specify URL directly
});


zAu = {
	comURI: function (uri, dt, ignoreSessId) {
		var au = zk.Desktop.$(dt).updateURI;
		if (!uri) return au;

		if (uri.charAt(0) != '/') uri = '/' + uri;

		var j = au.lastIndexOf(';'), k = au.lastIndexOf('?');
		if (j < 0 && k < 0) return au + uri;

		if (k >= 0 && (j < 0 || k < j)) j = k;
		var prefix = au.substring(0, j);

		if (ignoreSessId)
			return prefix + uri;

		var suffix = au.substring(j);
		var l = uri.indexOf('?');
		return l >= 0 ?
			k >= 0 ?
			  prefix + uri.substring(0, l) + suffix + '&' + uri.substring(l+1):
			  prefix + uri.substring(0, l) + suffix + uri.substring(l):
			prefix + uri + suffix;
	},
	/** Called by mount.js when onReSize */
	_onClientInfo: function () {
		if (zAu._cInfoReg)
			setTimeout(zAu._fireClientInfo, 20);
				//we cannot pass zAu.cmd0.clientInfo directly
				//otherwise, FF will pass 1 as the firt argument,
				//i.e., it is equivalent to zAu.cmd0.clientInfo(1)
	},
	_fireClientInfo: function () {
		zAu.cmd0.clientInfo();
	},

	//Error Handling//
	confirmRetry: function (msgCode, msg2) {
		var msg = mesg[msgCode];
		return zDom.confirm((msg?msg:msgCode)+'\n'+mesg.TRY_AGAIN+(msg2?"\n\n("+msg2+")":""));
	},
	showError: function (msgCode, msg2, cmd, ex) {
		var msg = mesg[msgCode];
		zk.error((msg?msg:msgCode)+'\n'+(msg2?msg2:"")+(cmd?cmd:"")+(ex?"\n"+ex.message:""));
	},
	getErrorURI: function (code) {
		return zAu._eru['e' + code];
	},
	setErrorURI: function (code, uri) {
		if (len > 2) {
			for (var j = 0; j < len; j += 2)
				zAu.setErrorURI(args[j], args[j + 1]);
			return;
		}
		zAu._eru['e' + code] = uri;
	},
	_eru: {},

	////Ajax Send////
	processing: function () {
		return zk.mounting || zAu._cmdsQue.length || zAu._areq || zAu._preqInf;
	},
	/** Checks whether to turn off the progress prompt. */
	_ckProcessng: function () {
		if (!zAu.processing()) {
			zk.endProcessing();
			zAu.doneTime = zUtl.now();
		}
	},

	send: function (aureq, timeout) {
		if (timeout < 0) {
			var opts = aureq.opts;
			if (!opts) opts = aureq.opts = {};
			opts.implicit = true;
		}

		var t = aureq.target;
		if (t) {
			zAu._send(t.className == 'zk.Desktop' ? t: t.desktop, aureq, timeout);
		} else {
			var dts = zk.Desktop.all;
			for (var dtid in dts)
				zAu._send(dts[dtid], aureq, timeout);
		}
	},
	sendAhead: function (aureq, timeout) {
		var t = aureq.target;
		if (t) {
			var dt = t.className == 'zk.Desktop' ? t: t.desktop;
			dt._aureqs.unshift(aureq);
			zAu._send2(dt, timeout);
		} else {
			var dts = zk.Desktop.all;
			for (var dtid in dts) {
				dt._aureqs.unshift(aureq);
				zAu._send2(dts[dtid], timeout);
			}
			return;
		}
	},

	////Ajax receive////
	pushXmlResp: function (dt, req) {
		var xml = req.responseXML;
		if (!xml) {
			if (zk.pfmeter) zAu.pfdone(dt, zAu._pfGetIds(req));
			return false; //invalid
		}

		var cmds = [],
			rs = xml.getElementsByTagName("r"),
			rid = xml.getElementsByTagName("rid");
		if (zk.pfmeter) {
			cmds.dt = dt;
			cmds.pfIds = zAu._pfGetIds(req);
		}

		if (rid && rid.length) {
			rid = zk.parseInt(zUtl.getElementValue(rid[0])); //response ID
			if (!isNaN(rid)) cmds.rid = rid;
		}

		for (var j = 0, rl = rs ? rs.length: 0; j < rl; ++j) {
			var cmd = rs[j].getElementsByTagName("c")[0],
				data = rs[j].getElementsByTagName("d");

			if (!cmd) {
				zk.error(mesg.ILLEGAL_RESPONSE+"Command required");
				continue;
			}

			cmds.push(cmd = {cmd: zUtl.getElementValue(cmd)});
			cmd.data = [];
			for (var cd = cmd.data, k = data ? data.length: 0; --k >= 0;)
				cd.unshift(zAu._decodeData(zUtl.getElementValue(data[k])));
		}

		zAu._cmdsQue.push(cmds);
		return true;
	},
	_decodeData: function (d) {
		var v = d.substring(1);
		switch (d.charAt(0).toLowerCase()) {
		case 'c': case 's': return v;
		case 'n': return null;
		case '1': case '3': return true;
		case '0': case '2': return false;
		case 'i': case 'l': case 'b': case 'h':
			return parseInt(v);
		case 'd': case 'f':
			return parseFloat(v);
		case 't': return new Date(parseInt(v));
		case 'j': return new zk.BigInteger(v);
		case 'k': return new zk.BigDecimal(v);
		case '[':
			d = v;
			v = [];
			var esc;
			for (var len = d.length, j = 0, k = 0;; ++j) {
				if (j >= len) {
					if (len)
						v.push(zAu._decodeData(esc || d.substring(k, j)));
					return v;
				}
				var cc = d.charAt(j);
				if (cc == '\\') {
					if (!esc) esc = d.substring(k, j);
					esc += d.charAt(++j);
				} else if (cc == ',') {
					v.push(zAu._decodeData(esc || d.substring(k, j)));
					k = j + 1;
					esc = null;
				} else if (esc)
					esc += cc;
			}
		}
		return v;
	},
	process: function (cmd, varags) { //by server only (encoded)
		var data = [];
		for (var j = arguments.length; --j > 0;)
			data.unshift(zAu._decodeData(arguments[j]));
		zAu._process(cmd, data);
	},
	_process: function (cmd, data) { //decoded
		//I. process commands that data[0] is not UUID
		var fn = zAu.cmd0[cmd];
		if (fn) {
			fn.apply(zAu, data);
			return;
		}

		//I. process commands that require uuid
		if (!data || !data.length) {
			zAu.showError("ILLEGAL_RESPONSE", "uuid is required for ", cmd);
			return;
		}

		fn = zAu.cmd1[cmd];
		if (fn) {
			data.splice(1, 0, zk.Widget.$(data[0])); //insert wgt
			fn.apply(zAu, data);
			return;
		}

		zAu.showError("ILLEGAL_RESPONSE", "Unknown command: ", cmd);
	},
	shallIgnoreESC: function () {
		return zAu._areq;
	},

	//ajax internal//
	_cmdsQue: [], //response commands in XML
	_seqId: 1, //1-999

	/** IE6 sometimes remains readyState==1 (reason unknown), so resend. */
	_areqTmout: function () {
		//Note: we don't resend if readyState >= 3, since the server is already
		//processing it
		var req = zAu._areq, reqInf = zAu._areqInf;
		if (req && req.readyState < 3) {
			zAu._areq = zAu._areqInf = null;
			try {
				if(typeof req.abort == "function") req.abort();
			} catch (e2) {
			}
			if (reqInf.tmout < 60000) reqInf.tmout += 3000;
				//sever might be busy, so prolong next timeout
			zAu._areqResend(reqInf);
		}
	},
	_areqResend: function (reqInf, timeout) {
		if (zAu._seqId == reqInf.sid) {//skip if the response was recived
			zAu._preqInf = reqInf; //store as a pending request info
			setTimeout(zAu._areqResend2, timeout ? timeout: 0);
		}
	},
	_areqResend2: function () {
		var reqInf = zAu._preqInf;
		if (reqInf) {
			zAu._preqInf = null;
			if (zAu._seqId == reqInf.sid)
				zAu._sendNow2(reqInf);
		}
	},
	/** Called when the response is received from _areq. */
	_onRespReady: function () {
		try {
			var req = zAu._areq, reqInf = zAu._areqInf;
			if (req && req.readyState == 4) {
				zAu._areq = zAu._areqInf = null;
				if (reqInf.tfn) clearTimeout(reqInf.tfn); //stop timer

				if (zk.pfmeter) zAu.pfrecv(reqInf.dt, zAu._pfGetIds(req));

				if (zAu._revertpending) zAu._revertpending();
					//revert any pending when the first response is received

				var sid = req.getResponseHeader("ZK-SID");
				if (req.status == 200) { //correct
					if (sid && sid != zAu._seqId) {
						zAu._errcode = "ZK-SID " + (sid ? "mismatch": "required");
						return;
					} //if sid null, always process (usually for error msg)

					if (zAu.pushXmlResp(reqInf.dt, req)) { //valid response
						//advance SID to avoid receive the same response twice
						if (sid && ++zAu._seqId > 999) zAu._seqId = 1;
						zAu._areqTry = 0;
						zAu._preqInf = null;
					}
				} else if (!sid || sid == zAu._seqId) { //ignore only if out-of-seq (note: 467 w/o sid)
					zAu._errcode = req.status;
					var eru = zAu._eru['e' + req.status];
					if (typeof eru == "string") {
						zUtl.go(eru);
					} else {
					//handle MSIE's buggy HTTP status codes
					//http://msdn2.microsoft.com/en-us/library/aa385465(VS.85).aspx
						switch (req.status) { //auto-retry for certain case
						default:
							if (!zAu._areqTry) break;
							//fall thru
						case 12002: //server timeout
						case 12030: //http://danweber.blogspot.com/2007/04/ie6-and-error-code-12030.html
						case 12031:
						case 12152: // Connection closed by server.
						case 12159:
						case 13030:
						case 503: //service unavailable
							if (!zAu._areqTry) zAu._areqTry = 3; //two more try
							if (--zAu._areqTry) {
								zAu._areqResend(reqInf, 200);
								return;
							}
						}

						if (!zAu._ignorable && !zk.unloading) {
							var msg = req.statusText;
							if (zAu.confirmRetry("FAILED_TO_RESPONSE", req.status+(msg?": "+msg:""))) {
								zAu._areqTry = 2; //one more try
								zAu._areqResend(reqInf);
								return;
							}
						}

						zAu._cleanupOnFatal(zAu._ignorable);
					}
				}
			}
		} catch (e) {
			if (!window.zAu)
				return; //the doc has been unloaded

			zAu._areq = zAu._areqInf = null;
			try {
				if(req && typeof req.abort == "function") req.abort();
			} catch (e2) {
			}

			//NOTE: if connection is off and req.status is accessed,
			//Mozilla throws exception while IE returns a value
			if (!zAu._ignorable && !zk.unloading) {
				var msg = e.message;
				zAu._errcode = "[Receive] " + msg;
				//if (e.fileName) zAu._errcode += ", "+e.fileName;
				//if (e.lineNumber) zAu._errcode += ", "+e.lineNumber;
				if (zAu.confirmRetry("FAILED_TO_RESPONSE", (msg&&msg.indexOf("NOT_AVAILABLE")<0?msg:""))) {
					zAu._areqResend(reqInf);
					return;
				}
			}
			zAu._cleanupOnFatal(zAu._ignorable);
		}

		//handle pending ajax send
		if (zAu._sendPending && !zAu._areq && !zAu._preqInf) {
			zAu._sendPending = false;
			var dts = zk.Desktop.all
			for (var dtid in dts)
				zAu._send2(dts[dtid], 0);
		}

		zAu.doCmds();
	},

	_send: function (dt, aureq, timeout) {
		var opts = aureq.opts, clkfd = zk.clickFilterDelay;
		if (clkfd > 0 && opts && opts.ctl) {
			//Don't send the same request if it is in processing
			if (zAu._areqInf && zAu._areqInf.ctli == aureq.uuid
			&& zAu._areqInf.ctlc == aureq.cmd)
				return;

			var t = zUtl.now();
			if (zAu._ctli == aureq.uuid && zAu._ctlc == aureq.cmd //Bug 1797140
			&& t - zAu._ctlt < clkfd)
				return; //to prevent key stroke are pressed twice (quickly)

			//Note: it is still possible to queue two ctl with same uuid and cmd,
			//if the first one was not sent yet and the second one is generated
			//after 390ms. However, it is rare so no handle it

			zAu._ctlt = t;
			zAu._ctli = aureq.uuid;
			zAu._ctlc = aureq.cmd;
		}

		dt._aureqs.push(aureq);

		zAu._send2(dt, timeout);
			//Note: we don't send immediately (Bug 1593674)
	},
	_send2: function (dt, timeout) {
		if (!timeout) timeout = 0;
		if (dt && timeout >= 0)
			setTimeout(function(){zAu.sendNow(dt);}, timeout);
	},
	sendNow: function (dt) {
		var es = dt._aureqs;
		if (es.length == 0)
			return false;

		if (zk.mounting) {
			zk.afterMount(function(){zAu.sendNow(dt);});
			return true; //wait
		}

		if (zAu._areq || zAu._preqInf) { //send ajax request one by one
			zAu._sendPending = true;
			return true;
		}

		//notify watches (fckez uses it to ensure its value is sent back correctly
		try {
			zWatch.fire('onSend', null, implicit);
		} catch (e) {
			zk.error(e.message);
		}

		//bug 1721809: we cannot filter out ctl even if zAu.processing

		//decide implicit and ignorable
		var implicit = true, ignorable = true, ctli, ctlc;
		for (var j = es.length; --j >= 0;) {
			var aureq = es[j], opts = aureq.opts;
			if (implicit && (!opts || !opts.ignorable)) { //ignorable implies implicit
				ignorable = false;
				if (!opts || !opts.implicit)
					implicit = false;
			}
			if (opts && opts.ctl && !ctli) {
				ctli = aureq.target.uuid;
				ctlc = aureq.name;
			}
		}
		zAu._ignorable = ignorable;

		//Consider XML (Pros: ?, Cons: larger packet)
		var content = "", $Event = zk.Event;
		for (var j = 0, el = es.length; el; ++j, --el) {
			var aureq = es.shift(),
				evtnm = aureq.name,
				target = aureq.target;
			content += "&cmd."+j+"="+evtnm;
			if (target && target.className != 'zk.Desktop')
				content += "&uuid."+j+"="+target.uuid;

			var data = aureq.data;
			if (data && data.marshal) data = data.marshal();
			if (data != null) {
				if (!data.$array) data = [data];
				for (var k = 0, dl = data.length; k < dl; ++k) {
					var d = data[k];
					content += "&data."+j+"="
						+ (d != null ? encodeURIComponent(d): '_z~nil');
				}
			}
		}

		if (content)
			zAu._sendNow2({
				sid: zAu._seqId, uri: zAu.comURI(null, dt),
				dt: dt, content: "dtid=" + dt.id + content,
				ctli: ctli, ctlc: ctlc, implicit: implicit,
				ignorable: ignorable, tmout: 0
			});
		return true;
	},
	_sendNow2: function(reqInf) {
		var req = zUtl.newAjax(),
			uri = zAu._useQS(reqInf) ? reqInf.uri + '?' + reqInf.content: null;
		zAu.sentTime = zUtl.now(); //used by server-push (zkex)
		try {
			req.onreadystatechange = zAu._onRespReady;
			req.open("POST", uri ? uri: reqInf.uri, true);
			req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			req.setRequestHeader("ZK-SID", reqInf.sid);
			if (zAu._errcode) {
				req.setRequestHeader("ZK-Error-Report", zAu._errcode);
				delete zAu._errcode;
			}

			if (zk.pfmeter) zAu._pfsend(reqInf.dt, req);

			zAu._areq = req;
			zAu._areqInf = reqInf;
			if (zk.resendDelay > 0)
				zAu._areqInf.tfn = setTimeout(zAu._areqTmout, zk.resendDelay + reqInf.tmout);

			if (uri) req.send(null);
			else req.send(reqInf.content);

			if (!reqInf.implicit) zk.startProcessing(zk.procDelay); //wait a moment to avoid annoying
		} catch (e) {
			//handle error
			try {
				if(typeof req.abort == "function") req.abort();
			} catch (e2) {
			}

			if (!reqInf.ignorable && !zk.unloading) {
				var msg = e.message;
				zAu._errcode = "[Send] " + msg;
				if (zAu.confirmRetry("FAILED_TO_SEND", msg)) {
					zAu._areqResend(reqInf);
					return;
				}
			}
			zAu._cleanupOnFatal(reqInf.ignorable);
		}
	},
	//IE: use query string if possible to avoid IE incomplete-request problem
	_useQS: zk.ie ? function (reqInf) {
		var s = reqInf.content, j = s.length, prev, cc;
		if (j + reqInf.uri.length < 2000) {
			while (--j >= 0) {
				cc = s.charAt(j);
				if (cc == '%' && prev >= '8') //%8x, %9x...
					return false;
				prev = cc;
			}
			return true;
		}
		return false;
	}: zk.$void,

	doCmds: function () {
		_zkmt = zUtl.now(); //used by zkm.exec

		for (var fn; fn = zAu._dcfns.shift();)
			fn();

		var ex, j = 0, que = zAu._cmdsQue, rid = zAu._resId;
		for (; j < que.length; ++j) {
			if (zk.mounting) return; //wait zkm.mtAU to call

			var cmds = que[j];
			if (rid == cmds.rid || !rid || !cmds.rid //match
			|| zAu._dtids.length > 1) { //ignore multi-desktops (risky but...)
				que.splice(j, 1);

				var oldrid = rid;
				if (cmds.rid) {
					if ((rid = cmds.rid + 1) >= 1000)
						rid = 1; //1~999
					zAu._resId = rid;
				}

				try {
					if (zAu._doCmds1(cmds)) { //done
						j = -1; //start over
						if (zk.pfmeter) {
							var fn = function () {zAu.pfdone(cmds.dt, cmds.pfIds);};
							if (zk.mounting) zAu._dcfns.push(fn);
							else fn();
						}
					} else { //not done yet (=zk.mounting)
						zAu._resId = oldrid; //restore
						que.splice(j, 0, cmds); //put it back
						return; //wait zkm.mtAU to call
					}
				} catch (e) {
					if (!ex) ex = e;
					j = -1; //start over
				}
			}
		}

		if (que.length) { //sequence is wrong => enforce to run if timeout
			setTimeout(function () {
				if (que.length && rid == zAu._resId) {
					var r = que[0].rid;
					for (j = 1; j < que.length; ++j) { //find min
						var r2 = que[j].rid,
							v = r2 - r;
						if (v > 500 || (v < 0 && v > -500)) r = r2;
					}
					zAu._resId = r;
					zAu.doCmds();
				}
			}, 3600);
		} else
			zAu._ckProcessng();

		if (ex) throw ex;
	},
	_dcfns: [],
	_doCmds1: function (cmds) {
		var processed;
		try {
			while (cmds && cmds.length) {
				if (zk.mounting) return false;

				processed = true;
				var cmd = cmds.shift();
				try {
					zAu._process(cmd.cmd, cmd.data);
				} catch (e) {
					zAu.showError("FAILED_TO_PROCESS", null, cmd.cmd, e);
					throw e;
				}
			}
		} finally {
			if (processed && (!cmds || !cmds.length))
				zWatch.fire('onResponse', {timeout:0}); //use setTimeout
		}
		return true;
	},
	//Used by zkm.mtAU to know any pending
	_moreCmds: function () {
		return zAu._cmdsQue.length
	},

	/** Cleans up if we detect obsolete or other severe errors. */
	_cleanupOnFatal: function (ignorable) {
		for (var uuid in zAu._metas) {
			var meta = zAu._metas[uuid];
			if (meta && meta.cleanupOnFatal)
				meta.cleanupOnFatal(ignorable);
		}
	}
};

//Commands//
zAu.cmd0 = { //no uuid at all
	bookmark: function (bk) {
		zHistory.bookmark(bk);
	},
	obsolete: function (dt0, dt1) { //desktop timeout
		zAu._cleanupOnFatal();
		zk.error(dt1);
	},
	alert: function (msg) {
		zDom.alert(msg);
	},
	redirect: function (url, target) {
		try {
			zUtl.go(url, false, target);
		} catch (ex) {
			if (!zk.confirmClose) throw ex;
		}
	},
	title: function (dt0) {
		document.title = dt0;
	},
	script: function (dt0) {
		eval(dt0);
	},
	echo: function (dtid) {
		zAu.send(new zk.Event(zk.Desktop.$(dtid), "dummy", null, {ignorable: true}));
	},
	clientInfo: function (dtid) {
		zAu._cInfoReg = true;
		zAu.send(new zk.Event(zk.Desktop.$(dtid), "onClientInfo", 
			[new Date().getTimezoneOffset(),
			screen.width, screen.height, screen.colorDepth,
			zDom.innerWidth(), zDom.innerHeight(), zDom.innerX(), zDom.innerY()]));
	},
	download: function (url) {
		if (url) {
			var ifr = zDom.$('zk_download');
			if (ifr) {
				ifr.src = url; //It is OK to reuse the same iframe
			} else {
				var html = '<iframe src="'+url
				+'" id="zk_download" name="zk_download" style="visibility:hidden;width:0;height:0;border:0" frameborder="0"></iframe>';
				zk.insertHTMLBeforeEnd(document.body, html);
			}
		}
	},
	print: function () {
		window.print();
	},
	scrollBy: function (x, y) {
		window.scrollBy(x, y);
	},
	scrollTo: function (x, y) {
		window.scrollTo(x, y);
	},
	resizeBy: function (x, y) {
		window.resizeBy(x, y);
	},
	resizeTo: function (x, y) {
		window.resizeTo(x, y);
	},
	moveBy: function (x, y) {
		window.moveBy(x, y);
	},
	moveTo: function (x, y) {
		window.moveTo(x, y);
	},
	cfmClose: function (msg) {
		zk.confirmClose = msg;
	},
	showBusy: function (msg, open) {
		//close first (since users might want close and show diff message)
		var n = zDom.$("zk_showBusy");
		if (n) {
			n.parentNode.removeChild(n);
			zk.restoreDisabled();
		}

		if (open == "true") {
			n = zDom.$("zk_loadprog");
			if (n) n.parentNode.removeChild(n);
			n = zDom.$("zk_prog");
			if (n) n.parentNode.removeChild(n);
			n = zDom.$("zk_showBusy");
			if (!n) {
				msg = msg == "" ? mesg.PLEASE_WAIT : msg;
				zUtl.progressbox("zk_showBusy", msg, true);
				zk.disableAll();
			}
		}
	}
};
zAu.cmd1 = {
	wrongValue: function (uuid, cmp, dt1) {
		for (var uuids = uuid.split(","), i = 0, j = uuids.length; i < j; i++) {
			cmp = zDom.$(uuids[i]);
			if (cmp) {
				cmp = $real(cmp); //refer to INPUT (e.g., datebox)
				//we have to update default value so validation will be done again
				var old = cmp.value;
				cmp.defaultValue = old + "_err"; //enforce to validate
				if (old != cmp.value) cmp.value = old; //Bug 1490079 (FF only)
				if (zAu.valid) zAu.valid.errbox(cmp.id, arguments[i+2], true);
				else zDom.alert(arguments[i+2]);
			} else if (!uuids[i]) { //keep silent if component (of uuid) not exist (being detaced)
				zDom.alert(arguments[i+2]);
			}
		}
	},
	setAttr: function (uuid, wgt, nm, val) {
		wgt.set(nm, val, true); //3rd arg: fromServer
	},
	outer: function (uuid, wgt, code) {
		zAu.stub = function (newwgt) {
			wgt._replaceWgt(newwgt);
		};
		zk.mounting = true;
		eval(code);
	},
	addAft: function (uuid, wgt, code) {
		//Bug 1939059: This is a dirty fix. Refer to AuInsertBefore
		//Format: comp-uuid:pg-uuid (if native root)
		if (!wgt) {
			var j = uuid.indexOf(':');
			if (j >= 0) { //native root
				wgt = zk.Widget.$(uuid.substring(0, j)); //try comp (though not possible)
				if (!wgt) {
					uuid = uuid.substring(j + 1); //try page
					wgt = zk.Widget.$(uuid);
					if (wgt) zAu.cmd1.addChd(uuid, wgt, code);
					else {
						zAu.stub = zAu.cmd1._asBodyChild;
						zk.mounting = true;
						eval(code);
					}
					return;
				}
			}
		}

		zAu.stub = function (child) {
			var p = wgt.parent;
			p.insertBefore(child, wgt.nextSibling);
			if (p.$instanceof(zk.Desktop))
				zAu.cmd1._asBodyChild(child);
		};
		zk.mounting = true;
		eval(code);
	},
	addBfr: function (uuid, wgt, code) {
		zAu.stub = function (child) {
			wgt.parent.insertBefore(child, wgt);
		};
		zk.mounting = true;
		eval(code);
	},
	addChd: function (uuid, wgt, code) {
		zAu.stub = function (child) {
			wgt.appendChild(child);
		};
		zk.mounting = true;
		eval(code);
	},
	_asBodyChild: function (child) {
		child.insertHTML(document.body, "beforeEnd");
	},
	rm: function (uuid, wgt) {
		if (wgt) {
			var p = wgt.parent;
			if (p) p.removeChild(wgt);
			else {
				p = wgt.getNode();
				wgt.unbind_();
				zDom.remove(p);
			}
		}
		//TODO if (zAu.valid) zAu.valid.fixerrboxes();
	},
	focus: function (uuid, wgt) {
		wgt.focus();
	},
	closeErrbox: function (uuid, cmp) {
		if (zAu.valid) {
			var uuids = uuid.trim().split(',');
			for (var i = uuids.length; --i >= 0;)
				zAu.valid.closeErrbox(uuids[i], false, true);
		}
	},
	submit: function (uuid, cmp) {
		setTimeout(function (){if (cmp && cmp.submit) cmp.submit();}, 50);
	},
	invoke: function (uuid, wgt, func, vararg) {
		var args = [];
		for (var j = arguments.length; --j > 2;)
			args.unshift(arguments[j]);
		wgt[func].apply(wgt, args);
	},
	echo2: function (uuid, wgt, evtnm, data) {
		zAu.send(new zk.Event(wgt, "echo",
			data != null ? [evtnm, data]: [evtnm], {ignorable: true}));
	}
};


zNumFormat = {
	format: function (fmt, val) {
		if (!val) return '';
		return '' + val; //TODO
	},
	unformat: function (fmt, val) {
		if (!val) return {raw: val, divscale: 0};

		var divscale = 0, //the second element
			minus, sb, cc, ignore;
		for (var j = 0, len = val.length; j < len; ++j) {
			cc = val.charAt(j);
			ignore = true;

			//We handle percent and (nnn) specially
			if (cc == zk.PERCENT) divscale += 2;
			else if (cc == zk.PER_MILL) divscale += 3;
			else if (cc == '(') minus = true;
			else if (cc != '+') ignore = false;

			//We don't add if cc shall be ignored (not alphanum but in fmt)
			if (!ignore)
				ignore = (cc < '0' || cc > '9')
				&& cc != zk.DECIMAL && cc != zk.MINUS && cc != '+'
				&& (zk.isWhitespace(cc) || cc == zk.GROUPING || cc == ')'
					|| (fmt && fmt.indexOf(cc) >= 0));
			if (ignore) {
				if (!sb) sb = val.substring(0, j);
			} else {
				var c2 = cc == zk.MINUS ? '-':
					cc == zk.DECIMAL ? '.':  cc;
				if (cc != c2 && !sb)
					sb = val.substring(0, j);
				if (sb) sb += c2;
			}
		}
		if (minus) {
			if (!sb) sb = val;
			if (sb.length)
				if (sb.charAt(0) == '-') sb = sb.substring(1); //-- => +
				else sb = '-' + sb;
		}
		return {raw: sb || val, divscale: divscale};
	}
};

zMsgFormat = {
	format: function (msg) {
		var i = 0, sb = '';
		for (var j = 0, len = msg.length, cc, k; j < len; ++j) {
			cc = msg.charAt(j);
			if (cc == '\\') {
				if (++j >= len) break;
				sb += msg.substring(i, j);
				cc = msg.charAt(j);
				switch (cc) {
				case 'n': cc = '\n'; break;
				case 't': cc = '\t'; break;
				case 'r': cc = '\r'; break;
				}
				sb += cc;
				i = j + 1;
			} else if (cc == '{') {
				k = msg.indexOf('}', j + 1);
				if (k < 0) break;
				sb += msg.substring(i, j)
					+ arguments[zk.parseInt(msg.substring(j + 1, k)) + 1];
				i = j = k + 1;
			}
		}
		if (sb) sb += msg.substring(i);
		return sb || msg;
	}
};

_z='zul';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

/** The base class for XUL widget (org.zkoss.zul.impl.XulElement).
 */
zul.Widget = zk.$extends(zk.Widget, {
	getContext: function () {
		return this._context;
	},
	setContext: function (context) {
		if (zk.Widget.isInstance(context))
			context = 'uuid(' + context.uuid + ')';
		this._context = context;
	},
	getPopup: function () {
		return this._popup;
	},
	setPopup: function (popup) {
		if (zk.Widget.isInstance(popup))
			popup = 'uuid(' + popup.uuid + ')';
		this._popup = popup;
	},
	getTooltip: function () {
		return this._tooltip;
	},
	setTooltip: function (tooltip) {
		if (zk.Widget.isInstance(tooltip))
			tooltip = 'uuid(' + tooltip.uuid + ')';
		this._tooltip = tooltip;
	},
	getCtrlKeys: function () {
		return this._ctrlKeys;
	},
	setCtrlKeys: function (keys) {
		if (this._ctrlKeys != keys) {
			if (!keys) {
				this._ctrlKeys = this._parsedCtlKeys = null;
				return;
			}

		var parsed = ['', '', '', '', ''], which = 0; //ext(#), ctl, alt, shft
		for (var j = 0, len = keys.length; j < len; ++j) {
			var cc = keys.charAt(j); //ext
			switch (cc) {
			case '^':
			case '$':
			case '@':
				if (which)
					throw "Combination of Shift, Alt and Ctrl not supported: "+keys;
				which = cc == '^' ? 1: cc == '@' ? 2: 3;
				break;
			case '#':
				var k = j + 1;
				for (; k < len; ++k) {
					var c2 = keys.charAt(k);
					if ((c2 > 'Z' || c2 < 'A') 	&& (c2 > 'z' || c2 < 'a')
					&& (c2 > '9' || c2 < '0'))
						break;
				}
				if (k == j + 1)
					throw "Unexpected character "+cc+" in "+keys;

				var s = keys.substring(j+1, k).toLowerCase();
				if ("pgup" == s) cc = 'A';
				else if ("pgdn" == s) cc = 'B';
				else if ("end" == s) cc = 'C';
				else if ("home" == s) cc = 'D';
				else if ("left" == s) cc = 'E';
				else if ("up" == s) cc = 'F';
				else if ("right" == s) cc = 'G';
				else if ("down" == s) cc = 'H';
				else if ("ins" == s) cc = 'I';
				else if ("del" == s) cc = 'J';
				else if (s.length > 1 && s.charAt(0) == 'f') {
					var v = zk.parseInt(s.substring(1));
					if (v == 0 || v > 12)
						throw "Unsupported function key: #f" + v;
					cc = 'O'.$inc(v); //'P': F1, 'Q': F2... 'Z': F12
				} else
					throw "Unknown #"+s+" in "+keys;

				parsed[which] += cc;
				which = 0;
				j = k - 1;
				break;
			default:
				if (!which || ((cc > 'Z' || cc < 'A') 
				&& (cc > 'z' || cc < 'a') && (cc > '9' || cc < '0')))
					throw "Unexpected character "+cc+" in "+keys;
				if (which == 3)
					throw "$a - $z not supported (found in "+keys+"). Allowed: $#f1, $#home and so on.";

				if (cc <= 'Z' && cc >= 'A')
					cc = cc.$inc('a'.$sub('A')); //to lower case
				parsed[which] += cc;
				which = 0;
				break;
			}
		}

			this._parsedCtlKeys = parsed;
			this._ctrlKeys = keys;
		}
	},

	//super//
	doClick_: function (evt) {
		if (!evt._popuped) {
			var popup = this._smartFellow(this._popup);
			if (popup) {
				evt._popuped = true;
				popup.open(this, [evt.data.pageX, evt.data.pageY], null, {sendOnOpen:true});
				evt.stop();
			}
		}
		this.$supers('doClick_', arguments);
	},
	doRightClick_: function (evt) {
		if (!evt._ctxed) {
			var ctx = this._smartFellow(this._context);
			if (ctx) {
				evt._ctxed = true;
				ctx.open(this, [evt.data.pageX, evt.data.pageY], null, {sendOnOpen:true});
				evt.stop(); //prevent default context menu to appear
			}
		}
		this.$supers('doRightClick_', arguments);
	},
	doMouseOver_: function (evt) {
		if (!evt._tiped && zTooltip.beforeBegin(this)) {
			var tip = this._smartFellow(this._tooltip);
			if (tip) {
				evt._tiped = true;
				zTooltip.begin(tip, this);
			}
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		zTooltip.end(this);
		this.$supers('doMouseOut_', arguments);
	},
	_smartFellow: function (id) {
		return id ? id.startsWith('uuid(') && id.endsWith(')') ?
			zk.Widget.$(id.substring(5, id.length - 1)):
			this.getFellow(id, true): null;
	}
});

(_zkwg=_zkpk.Widget).prototype.className='zul.Widget';
zul.LabelImageWidget = zk.$extends(zul.Widget, {
	getLabel: function () {
		var v = this._label;
		return v ? v: '';
	},
	setLabel: function(label) {
		if (label == null) label = '';
		if (this._label != label) {
			this._label = label;
			this.updateDomContent_();
		}
	},
	getImage: function () {
		return this._image;
	},
	setImage: function(image) {
		if (this._image != image) {
			this._image = image;
			this.updateDomContent_();
		}
	},
	getHoverImage: function () {
		return this._himg;
	},
	setHoverImage: function (himg) {
		this._himg = himg;
	},
	updateDomContent_: function () {
		this.rerender();
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this._image;
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		return label ? img + ' ' + label: img;
	},
	doMouseOver_: function () {
		var himg = this._himg;
		if (himg) {
			var img = this.getImageNode_();
			if (img) img.src = himg;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this._himg) {
			var img = this.getImageNode_();
			if (img) img.src = this._image;
		}
		this.$supers('doMouseOut_', arguments);
	},
	getImageNode_: function () {
		if (!this._eimg && this._image)
			this._eimg = zDom.firstChild(this.getNode(), "IMG", true);
		return this._eimg;
	},
	unbind_: function () {
		this._eimg = null;
		this.$supers('unbind_', arguments);
	}
});

(_zkwg=_zkpk.LabelImageWidget).prototype.className='zul.LabelImageWidget';
}finally{zPkg.end(_z);}}_z='zul.wgt';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.wgt.Div = zk.$extends(zul.Widget, {
});

(_zkwg=_zkpk.Div).prototype.className='zul.wgt.Div';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<div', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Include = zk.$extends(zul.Widget, {
	/** Returns the content of this include.
	 */
	getContent: function () {
		var v = this._content;
		return v ? v: '';
	},
	/** Sets the content of this include.
	 */
	setContent: function(content) {
		if (content == null) content = '';
		if (this._content != content) {
			this._content = content;
			var n = this.getNode();
			if (n) n.innerHTML = content;
		}
	},

	//super//
	redraw: function (out) {
		out.push('<div id="', this.uuid, '"');
		if (this.style) out.push(' style="', this.style, '"');
		out.push('>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push(this._content, '</div>');
	}
});

(_zkwg=_zkpk.Include).prototype.className='zul.wgt.Include';
zul.wgt.Label = zk.$extends(zul.Widget, {
	_value: '',

	/** Returns the value of this label.
	 */
	getValue: function () {
		return this._value;
	},
	/** Sets the value of this label.
	 */
	setValue: function(value) {
		if (!value) value = '';
		if (this._value != value) {
			this._value = value;
			var n = this.getNode();
			if (n) n.innerHTML = this.getEncodedText();
		}
	},
	isMultiline: function () {
		return this._multiline;
	},
	setMultiline: function (multiline) {
		if (multiline != this._multiline) {
			this._multiline = multiline;
			var n = this.getNode();
			if (n) n.innerHTML = this.getEncodedText();
		}
	},
	isPre: function () {
		return this._pre;
	},
	setPre: function (pre) {
		if (pre != this._pre) {
			this._pre = pre;
			var n = this.getNode();
			if (n) n.innerHTML = this.getEncodedText();
		}
	},

	getEncodedText: function () {
		return zUtl.encodeXML(this._value, {multiline:this._multiline,pre:this._pre});
	},
	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-label";
	}
});

(_zkwg=_zkpk.Label).prototype.className='zul.wgt.Label';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<span', this.domAttrs_(), '>', this.getEncodedText(), '</span>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Button = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,

	/** Returns the orient of this button.
	 */
	getOrient: function () {
		return this._orient;
	},
	/** Sets the orient of this button.
	 */
	setOrient: function(orient) {
		if (this._orient != orient) {
			this._orient = orient;
			this.updateDomContent_();
		}
	},
	/** Returns the dir of this button.
	 */
	getDir: function () {
		return this._dir;
	},
	/** Sets the dir of this button.
	 */
	setDir: function(dir) {
		if (this._dir != dir) {
			this._dir = dir;
			this.updateDomContent_();
		}
	},
	/** Returns whether this button is disabled
	 */
	isDisabled: function () {
		return this._disabled;
	},
	/** Sets whether this button is disabled
	 */
	setDisabled: function(disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			if (this.desktop)
				if (this._mold == 'os') this.getNode().disabled = true;
				else this.rerender(); //bind and unbind required
		}
	},
	/** Returns the tab index
	 */
	getTabindex: function () {
		return this._tabindex;
	},
	/** Sets the tab index
	 */
	setTabindex: function(tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			var n = this.getNode();
			if (n) (this.getSubnode('btn') || n).tabIndex = tabindex;
		}
	},

	getHref: function () {
		return this._href;
	},
	setHref: function (href) {
		this._href = href;
	},
	getTarget: function () {
		return this._target;
	},
	setTarget: function (target) {
		this._target = target;
	},

	//super//
	focus: function (timeout) {
		if (this.isVisible() && this.canActivate({checkOnly:true})) {
			zDom.focus(this.getSubnode('btn') ? this.getSubnode('btn'): this.getNode(), timeout);
			return true;
		}
		return false;
	},

	/** Updates the label and image. */
	updateDomContent_: function () {
		if (this.desktop) {
			var n = this.getSubnode('box');
			if (n) n.tBodies[0].rows[1].cells[1].innerHTML = this.domContent_();
			else this.getNode().innertHTML = this.domContent_();
		}
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage();
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		var space = "vertical" == this.getOrient() ? '<br/>': ' ';
		return this.getDir() == 'reverse' ?
			label + space + img: img + space + label;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (this._disabled && (!no || !no.zclass)) {
			var s = this.getZclass();
			if (s) scls += (scls ? ' ': '') + s + '-disd';
		}
		return scls;
	},

	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: this._mold == 'os' ? "z-button-os": "z-button";
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		var $Button = zul.wgt.Button, n;
		if (this._mold == 'os') {
			n = this.getNode();
		} else {
			if (this._disabled) return;

			zDom.disableSelection(this.getSubnode('box'));

			n = this.getSubnode('btn');
		}

		zEvt.listen(n, "focus", $Button._doFocus);
		zEvt.listen(n, "blur", $Button._doBlur);
	},
	unbind_: function () {
		var $Button = zul.wgt.Button,
			n = this._mold == 'os' ? this.getNode(): this.getSubnode('btn');
		if (n) {
			zEvt.unlisten(n, "focus", $Button._doFocus);
			zEvt.unlisten(n, "blur", $Button._doBlur);
		}

		this.$supers('unbind_', arguments);
	},
	doClick_: function (wevt) {
		if (!this._disabled) {
			this.fireX(wevt);

			if (!wevt.stopped) {
				var href = this._href;
				if (href)
					zUtl.go(href, false, this.getTarget(), "target");
			}
		}
		//Unlike DOM, we don't proprogate to parent (so no calling $supers)
	},
	doMouseOver_: function () {
		zDom.addClass(this.getSubnode('box'), this.getZclass() + "-over");
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this != zul.wgt.Button._curdn)
			zDom.rmClass(this.getSubnode('box'), this.getZclass() + "-over");
		this.$supers('doMouseOut_', arguments);
	},
	doMouseDown_: function () {
		var box = this.getSubnode('box'),
			zcls = this.getZclass();
		zDom.addClass(box, zcls + "-clk");
		zDom.addClass(box, zcls + "-over");
		zDom.focus(this.getSubnode('btn'), 30);

		zk.mouseCapture = this; //capture mouse up
		this.$supers('doMouseDown_', arguments);
	},
	doMouseUp_: function () {
		var box = this.getSubnode('box'),
			zcls = this.getZclass();
		zDom.rmClass(box, zcls + "-clk");
		zDom.rmClass(box, zcls + "-over");
		this.$supers('doMouseUp_', arguments);
	}
},{
	_doFocus: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt && wgt.domFocus_() //FF2 will cause a focus error when resize browser.
		&& wgt._mold != 'os')
			zDom.addClass(wgt.getSubnode('box'), wgt.getZclass() + "-focus");
	},
	_doBlur: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt._mold != 'os')
			zDom.rmClass(wgt.getSubnode('box'), wgt.getZclass() + "-focus");
		wgt.domBlur_();
	}
});
(_zkwg=_zkpk.Button).prototype.className='zul.wgt.Button';_zkmd={};
_zkmd['os']=
function (out) {
	out.push('<button', this.domAttrs_());
	var tabi = this._tabindex;
	if (this._disabled) out.push(' disabled="disabled"');
	if (tabi >= 0) out.push(' tabindex="', tabi, '"');
	out.push('>', this.domContent_(), '</button>');
}
_zkmd['default']=
function (out) {
	var zcls = this.getZclass(),
		tabi = this._tabindex;
	tabi = tabi >= 0 ? ' tabindex="' + tabi + '"': '';

	out.push('<span', this.domAttrs_({style:1,domclass:1}), ' class="', zcls, '"');
	if (!this.isVisible()) out.push(' style="display:none"');
	out.push('><table id="', this.uuid, '$box"', zUtl.cellps0);
	if (tabi && !zk.gecko && !zk.safari) out.push(tabi);
	var s = this.domStyle_();
	if (s) out.push(' style="', s, '"');
	s = this.domClass_();
	if (s) out.push(' class="', s, '"');

	var btn = '<button id="' + this.uuid + '$btn" class="' + zcls + '"',
	s = this.isDisabled();
	if (s) btn += ' disabled="disabled"';
	if (tabi && (zk.gecko || zk.safari)) btn += tabi;
	btn += '></button>';

	out.push('><tr><td class="', zcls, '-tl">');
	if (!zk.ie) out.push(btn);
	out.push('</td><td class="', zcls, '-tm"></td>', '<td class="', zcls,
			'-tr"></td></tr>');

	out.push('<tr><td class="', zcls, '-cl">');
	if (zk.ie) out.push(btn);
	out.push('</td><td class="', zcls, '-cm">', this.domContent_(),
			'</td><td class="', zcls, '-cr"><div></div></td></tr>',
			'<tr><td class="', zcls, '-bl"></td>',
			'<td class="', zcls, '-bm"></td>',
			'<td class="', zcls, '-br"></td></tr></table></span>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Separator = zk.$extends(zul.Widget, {
	_orient: 'horizontal',

	isVertical: function () {
		return this._orient == 'vertical';
	},
	/** Returns the orient of this button.
	 */
	getOrient: function () {
		return this._orient;
	},
	/** Sets the orient of this button.
	 */
	setOrient: function(orient) {
		if (this._orient != orient) {
			this._orient = orient;
			this.updateDomClass_();
		}
	},

	/** Returns whether to display a visual bar as the separator.
	 * <p>Default: false
	 */
	isBar: function () {
		return this._bar;
	},
	/** Sets  whether to display a visual bar as the separator.
	 */
	setBar: function(bar) {
		if (this._bar != bar) {
			this._bar = bar;
			this.updateDomClass_();
		}
	},
	/** Returns the spacing.
	 * <p>Default: null (depending on CSS).
	 */
	getSpacing: function () {
		return this._spacing;
	},
	/** Sets the spacing.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em")
	 */
	setSpacing: function(spacing) {
		if (this._spacing != spacing) {
			this._spacing = spacing;
			this.updateDomStyle_();
		}
	},

	//super//
	getZclass: function () {
		var zcls = this.zclass,
			bar = this.isBar();
		return zcls ? zcls: "z-separator" +
			(this.isVertical() ? "-ver" + (bar ? "-bar" : "") :
				"-hor" + (bar ? "-bar" : ""))
	},
	domStyle_: function () {
		var s = this.$supers('domStyle_', arguments);
		if (!this._isPercentGecko()) return s;

		var v = zk.parseInt(_spacing.substring(0, _spacing.length() - 1).trim());
		if (v <= 0) return s;
		v = v >= 2 ? (v / 2) + "%": "1%";

		return 'margin:' + (this.isVertical() ? '0 ' + v: v + ' 0')
			+ ';' + s;
	},
	getWidth: function () {
		var wd = this.$supers('getWidth', arguments);
		return !this.isVertical() || (wd != null && wd.length() > 0)
			|| this._isPercentGecko() ? wd: this._spacing;
		
	},
	getHeight: function () {
		var hgh = this.$supers('getHeight', arguments);
		return this.isVertical() || (hgh != null && hgh.length() > 0)
			|| this._isPercentGecko() ? hgh: this._spacing;
	},
	_isPercentGecko: function () {
		return zk.gecko && this._spacing != null && this._spacing.endsWith("%");
	}
});

(_zkwg=_zkpk.Separator).prototype.className='zul.wgt.Separator';_zkmd={};
_zkmd['default']=
function (out) {
	var tag = this.isVertical() ? 'span': 'div';
	out.push('<', tag, this.domAttrs_(), '>&nbsp;</', tag, '>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Space = zk.$extends(zul.wgt.Separator, {
	_orient: 'vertical'
});
(_zkwg=_zkpk.Space).prototype.className='zul.wgt.Space';_zkmd={};
_zkmd['default']=
zul.wgt.Separator.molds['default']
zkmld(_zkwg,_zkmd);
zul.wgt.Caption = zk.$extends(zul.LabelImageWidget, {
	//super//
	domDependent_: true, //DOM content depends on parent

	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-caption";
	},
	domContent_: function () {
		var label = this.getLabel(),
			img = this.getImage(),
			title = this.parent ? this.parent.title: '';
		if (title) label = label ? title + ' - ' + label: title;
		label = zUtl.encodeXML(label);
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		return label ? img + ' ' + label: img;
	},
	unbind_: function () {
		var n = this.getNode(), parent = this.parent;
		if (n && parent.$instanceof(zul.wgt.Groupbox))
			zEvt.unlisten(n, "click", zul.wgt.Caption.ongbclk);

		this.$supers('unbind_', arguments);
	},
	doClick_: function () {
		if (this.parent.$instanceof(zul.wgt.Groupbox))
			this.parent.setOpen(!this.parent.isOpen());
		this.$supers('doClick_', arguments);
	},
	//private//
	/** Whether to generate a close button. */
	_isCloseVisible: function () {
		var parent = this.parent;
		return parent.isClosable && parent.isClosable();
	},
	/** Whether to generate a minimize button. */
	_isMinimizeVisible: function () {
		var parent = this.parent;
		return parent.isMinimizable && parent.isMinimizable();
	},
	/** Whether to generate a maximize button. */
	_isMaximizeVisible: function () {
		var parent = this.parent;
		return parent.isMaximizable && parent.isMaximizable();
	}
});
(_zkwg=_zkpk.Caption).prototype.className='zul.wgt.Caption';_zkmd={};
_zkmd['default']=
function (out) {
	var parent = this.parent;
	if (parent.isLegend && parent.isLegend()) {
		out.push('<legend', this.domAttrs_(), '>', this.domContent_());
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</legend>');
		return;
	}

	var zcls = this.getZclass(),
		cnt = this.domContent_(),
		puuid = parent.uuid,
		pzcls = parent.getZclass();
	out.push('<table', this.domAttrs_(), zUtl.cellps0,
			' width="100%"><tr valign="middle"><td align="left" class="',
			zcls, '-l">', (cnt?cnt:'&nbsp;'), //Bug 1688261: nbsp required
			'</td><td align="right" class="', zcls,
			'-r" id="', this.uuid, '$cave">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</td>');
	if (this._isMinimizeVisible())
		out.push('<td width="16"><div id="', puuid, '$min" class="',
				pzcls, '-tool ', pzcls, '-minimize"></div></td>');
	if (this._isMaximizeVisible()) {
		out.push('<td width="16"><div id="', puuid, '$max" class="',
				pzcls, '-tool ', pzcls, '-maximize');
		if (parent.isMaximized())
			out.push(' ', pzcls, '-maximized');
		out.push('"></div></td>');
	}
	if (this._isCloseVisible())
		out.push('<td width="16"><div id="', puuid, '$close" class="',
				pzcls, '-tool ', pzcls, '-close"></div></td>');

	out.push('</tr></table>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Checkbox = zk.$extends(zul.LabelImageWidget, {
	_tabindex: -1,
	
	isDisabled: function () {
		return this._disabled;
	},
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			if (this.getSubnode('real'))
				this.getSubnode('real').disabled = disabled;
		}
	},
	isChecked: function () {
		return this._checked;
	},
	setChecked: function (checked) {
		if (this._checked != checked) {
			this._checked = checked;
			if (this.getSubnode('real')) this.getSubnode('real').checked = checked;
		}
	},
	getName: function () {
		return this._name;
	},
	setName: function (name) {
		if (!name) name = null;
		if (this._name != name) {
			this._name = name;
			if (this.getSubnode('real'))
				this.getSubnode('real').name = name;
		}
	},
	getTabindex: function () {
		return this._tabindex;
	},
	setTabindex: function (tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			if (this.getSubnode('real'))
				this.getSubnode('real').tabIndex = tabindex;
		}
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-checkbox";
	},
	contentAttrs_: function () {
		var html = '', v = this.getName(); // cannot use this._name for radio
		if (v)
			html += ' name="' + v + '"';
		if (this._disabled)
			html += ' disabled="disabled"';
		if (this._checked)
			html += ' checked="checked"';
		v = this._tabindex;
		if (v >= 0)
			html += ' tabindex="' + v + '"';
		return html;
	},
	labelAttrs_: function () {
		var style = zDom.getTextStyle(this.domStyle_());
		return style ? ' style="' + style + '"' : "";
	},
	bind_: function (desktop) {
		this.$supers('bind_', arguments);

		var $Checkbox = zul.wgt.Checkbox,
			n = this.getSubnode('real');

		if (zk.gecko2Only)
			zEvt.listen(n, "click", zul.wgt.Checkbox._doClick);
			// bug #2233787 : this is a bug of firefox 2, it need get currentTarget
		zEvt.listen(n, "focus", this.proxy(this.domFocus_, '_fxFocus'));
		zEvt.listen(n, "blur", this.proxy(this.domBlur_, '_fxBlur'));
	},
	unbind_: function () {
		var $Checkbox = zul.wgt.Checkbox,
			n = this.getSubnode('real');
		
		if (zk.gecko2Only)
			zEvt.unlisten(n, "click", zul.wgt.Checkbox._doClick);
		zEvt.unlisten(n, "focus", this._fxFocus);
		zEvt.unlisten(n, "blur", this._fxBlur);

		this.$supers('unbind_', arguments);
	},
	doClick_: function () {
		var real = this.getSubnode('real'),
			val = real.checked;
		if (val != real.defaultChecked) { //changed
			this.setChecked(val);
			real.defaultChecked = val;
			this.fire('onCheck', val);
		}
		return this.$supers('doClick_', arguments);
	},
	updateDomStyle_: function () {
		var node = this.getNode()
		zDom.setStyle(node, zDom.parseStyle(this.domStyle_()));
		var label = zDom.firstChild(node, "LABEL", true);
		if (label) zDom.setStyle(label, zDom.parseStyle(zDom.getTextStyle(this.domStyle_())));
	}
});
if (zk.gecko2Only)
	zul.wgt.Checkbox._doClick = function (evt) {
		evt.z_target = evt.currentTarget;
			//bug #2233787 : this is a bug of firefox 2, it need get currentTarget
	};

(_zkwg=_zkpk.Checkbox).prototype.className='zul.wgt.Checkbox';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
	out.push('<span', this.domAttrs_(), '>', '<input type="checkbox" id="', uuid,
			'$real"', this.contentAttrs_(), '/><label for="', uuid, '$real"',
			this.labelAttrs_(), ' class="', zcls, '-cnt">', this.domContent_(),
			'</label></span>');	
}
zkmld(_zkwg,_zkmd);
zul.wgt.Groupbox = zk.$extends(zul.Widget, {
	_open: true,
	_closable: true,

	isLegend: function () {
		return this._mold == 'default';
	},
	isOpen: function () {
		return this._open;
	},
	setOpen: function (open, fromServer) {
		if (this._open != open) {
			this._open = open;

			var node = this.getNode();
			if (node) {
				var panel = this.getSubnode('panel');
				if (panel) { //!legend
					if (open) zAnima.slideDown(this, panel, {afterAnima: this._afterSlideDown});
					else zAnima.slideUp(this, panel, {beforeAnima: this._beforeSlideUp});
				} else {
					zDom[open ? 'rmClass': 'addClass'](node, this.getZclass() + "-colpsd");
					zWatch.fireDown(open ? 'onVisible': 'onHide', {visible:true}, this);
				}
				if (!fromServer) this.fire('onOpen', open);
			}
		}
	},
	isClosable: function () {
		return this._closable;
	},
	setClosable: function (closable) {
		if (this._closable != closable) {
			this._closable = closable;
			this._updateDomOuter();
		}
	},
	getContentStyle: function () {
		return this._cntStyle;
	},
	setContentStyle: function (style) {
		if (this._cntStyle != style) {
			this._cntStyle = style;
			this._updateDomOuter();
		}
	},
	getContentSclass: function () {
		return this._cntSclass;
	},
	setContentSclass: function (sclass) {
		if (this._cntSclass != sclass) {
			this._cntSclass = sclass;
			this._updateDomOuter();
		}
	},

	_updateDomOuter: function () {
		this.rerender(zk.Skipper.nonCaptionSkipper);
	},
	_contentAttrs: function () {
		var html = ' class="', s = this._cntSclass;
		if (s) html += s + ' ';
		html += this.getZclass() + '-cnt"';

		s = this._cntStyle;
		if (!this.isLegend() && this.caption) s = 'border-top:0;' + (s ? s: '');
		if (s) html += ' style="' + s + '"';
		return html;
	},

	//watch//
	onSize: _zkf = function () {
		var hgh = this.getNode().style.height;
		if (hgh && hgh != "auto") {
			var n = this.getSubnode('cave');
			if (n) {
				if (zk.ie6Only) n.style.height = "";
				n.style.height =
					zDom.revisedHeight(n, zDom.vflexHeight(n.parentNode), true)
					+ "px";
					//we use n.parentNode(=this.getSubnode('panel')) to calc vflex,
					//so we have to subtract margin, too
			}
		}
		setTimeout(this.proxy(this._fixShadow), 500);
			//shadow raraly needs to fix so OK to delay for better performance
			//(getSubnode('sdw') a bit slow due to zDom.$)
	},
	onVisible: _zkf,
	_afterSlideDown: function (n) {
		zWatch.fireDown("onVisible", {visible:true}, this);
	},
	_beforeSlideUp: function (n) {
		zWatch.fireDown("onHide", {visible:true}, this);
	},
	_fixShadow: function () {
		var sdw = this.getSubnode('sdw');
		if (sdw)
			sdw.style.display =
				zk.parseInt(zDom.getStyle(this.getSubnode('cave'), "border-bottom-width")) ? "": "none";
				//if no border-bottom, hide the shadow
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) this.onSize();
	},

	//super//
	focus: function (timeout) {
		if (this.desktop) {
			var cap = this.caption;
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w != cap && w.focus(timeout))
					return true;
			return cap && cap.focus(timeout);
		}
		return false;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls: this.isLegend() ? "z-fieldset": "z-groupbox";
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		if (!this.isLegend()) {
			zWatch.listen("onSize", this);
			zWatch.listen("onVisible", this);
		}
	},
	unbind_: function () {
		if (!this.isLegend()) {
			zWatch.unlisten("onSize", this);
			zWatch.unlisten("onVisible", this);
		}
		this.$supers('unbind_', arguments);
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Caption))
			this.caption = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.caption)
			this.caption = null;
	},

	domClass_: function () {
		var html = this.$supers('domClass_', arguments);
		if (!this._open) {
			if (html) html += ' ';
			html += this.getZclass() + '-colpsd';
		}
		return html;
	}
});
(_zkwg=_zkpk.Groupbox).prototype.className='zul.wgt.Groupbox';_zkmd={};
_zkmd['3d']=
function (out, skipper) {	
	var	zcls = this.getZclass(),
		uuid = this.uuid,
		cap = this.caption;

	out.push('<div', this.domAttrs_(), '>');
	
	if (cap) {
		out.push('<div class=\"', zcls, '-tl"><div class="', zcls,
			'-tr"></div></div><div class="', zcls, '-hl"><div class="',
			zcls, '-hr"><div class="', zcls, '-hm"><div class="',
			zcls, '-header">');
		cap.redraw(out);
		out.push('</div></div></div></div>');
	}
	
	out.push('<div id="', uuid, '$panel" class="', zcls, '-body"');
	if (!this.isOpen()) out.push(' style="display:none"');
	out.push('><div id="', uuid, '$cave"', this._contentAttrs(), '>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != cap)
				w.redraw(out);
	out.push('</div></div>',
		//shadow
		'<div id="', uuid, '$sdw" class="', zcls, '-bl"><div class="', zcls,
			'-br"><div class="', zcls, '-bm"></div></div></div></div>');
}
_zkmd['default']=
function (out, skipper) {
	out.push('<fieldset', this.domAttrs_(), '>');
	
	var cap = this.caption;
	if (cap) cap.redraw(out);

	out.push('<div id="', this.uuid, '$cave"', this._contentAttrs(), '>');
	
	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != cap)
				w.redraw(out);
	out.push('</div></fieldset>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Html = zk.$extends(zul.Widget, {
	_content: '',
	getContent: function () {
		return this._content;
	},
	setContent: function (content) {
		if (!content) content = '';
		if (this._content != content) {
			this._content = content;
			var n = this.getNode();
			if (n) n.innerHTML = content;
		}
	}
});

(_zkwg=_zkpk.Html).prototype.className='zul.wgt.Html';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<span', this.domAttrs_(), '>', this._content, '</span>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Popup = zk.$extends(zul.Widget, {
	_visible: false,
	isOpen: function () {
		return this.isVisible();
	},
	open: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position);

		var node = this.getNode();
		zDom.setStyle(node, {position: "absolute"});
		zDom.makeVParent(node);
		if (posInfo)
			zDom.position(node, posInfo.dim, posInfo.pos, opts);
		
		this.setVisible(true);
		this.setFloating_(true);
		this.setTopmost();
		
		if (this.isListen("onOpen")) {
			// use a progress bar to hide the popup
			this.mask = new zk.eff.Mask({
				id: this.uuid + "$mask",
				anchor: node
			});
			
			// register onResponse to remove the progress bar after receiving
			// the response from server.
			zWatch.listen('onResponse', this);		
		}
		if (zk.ie6Only) {
			if (!this._stackup)
				this._stackup = zDom.makeStackup(node);
			else {
				this._stackup.style.top = node.style.top;
				this._stackup.style.left = node.style.left;
				this._stackup.style.display = "block";
			}
		}
		ref = zk.Widget.$(ref); // just in case, if ref is not a kind of zul.Widget.
		if (opts && opts.sendOnOpen) this.fire('onOpen', ref ? [true, ref.uuid] : true);
		zDom.cleanVisibility(node);
	},
	position: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position);
		if (posInfo)
			zDom.position(this.getNode(), posInfo.dim, posInfo.pos, opts);
	},
	_posInfo: function (ref, offset, position, opts) {
		var pos, dim;
		
		if (ref && position) {
			if (typeof ref == 'string')
				ref = zk.Widget.$(ref);
				
			if (ref) {
				var refn = zul.Widget.isInstance(ref) ? ref.getNode() : ref,
					ofs = zDom.revisedOffset(refn);
				pos = position;
				dim = {
					top: ofs[0], left: ofs[1],
					width: zDom.offsetWidth(refn), height: zDom.offsetHeight(refn)  
				}
			}
		} else if (offset && offset.$array) {
			dim = {
				top: zk.parseInt(offset[0]),
				left:  zk.parseInt(offset[1])
			}
		}
		if (dim) return {pos: pos, dim: dim};
	},
	onResponse: function () {
		if (this.mask) this.mask.destroy();
		zWatch.unlisten('onResponse', this);
		this.mask = null;
	},
	close: function (opts) {
		if (this._stackup)
			this._stackup.style.display = "none";
		
		this.setVisible(false);
		zDom.undoVParent(this.getNode());
		this.setFloating_(false);
		if (opts && opts.sendOnOpen) this.fire('onOpen', false);
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-popup";
	},
	onFloatUp: function(wgt){
		if (!this.isVisible()) 
			return;
		for (var floatFound; wgt; wgt = wgt.parent) {
			if (wgt == this) {
				if (!floatFound) 
					this.setTopmost();
				return;
			}
			floatFound = floatFound || wgt.isFloating_();
		}
		this.close({sendOnOpen:true});
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen('onFloatUp', this);
		zWatch.listen('onVisible', this);
		this.setFloating_(true);
	},
	unbind_: function () {
		if (this._stackup) {
			zDom.remove(this._stackup);
			this._stackup = null;
		}
		
		zWatch.unlisten('onFloatUp', this);
		zWatch.unlisten('onVisible', this);
		this.setFloating_(false);
		this.$supers('unbind_', arguments);
	},
	onVisible: zk.ie7 ? function (wgt) {
		var node = wgt.getNode(),
			wdh = node.style.width,
			fir = zDom.firstChild(node, "DIV"),
			last = zDom.lastChild(zDom.lastChild(node, "DIV"), "DIV"),
			n = wgt.getSubnode('cave').parentNode;
		
		if (!wdh || wdh == "auto") {
			var diff = zDom.padBorderWidth(n.parentNode) + zDom.padBorderWidth(n.parentNode.parentNode);
			if (fir) {
				fir.firstChild.firstChild.style.width = Math.max(0, n.offsetWidth - (zDom.padBorderWidth(fir)
					+ zDom.padBorderWidth(fir.firstChild) - diff)) + "px";
			}
			if (last) {
				last.firstChild.firstChild.style.width = Math.max(0, n.offsetWidth - (zDom.padBorderWidth(last)
					+ zDom.padBorderWidth(last.firstChild) - diff)) + "px";
			}
		} else {
			if (fir) fir.firstChild.firstChild.style.width = "";
			if (last) last.firstChild.firstChild.style.width = "";
		}
	}: zk.$void,
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		zWatch.fireDown('onVisible', {visible:true}, this);
	},
	prologHTML_: function (out) {
	},
	epilogHTML_: function (out) {
	}
});

(_zkwg=_zkpk.Popup).prototype.className='zul.wgt.Popup';_zkmd={};
_zkmd['default']=
function (out) {
	var zcls = this.getZclass();
	out.push('<div', this.domAttrs_(), '><div class="', zcls, '-tl"><div class="',
			zcls, '-tr"><div class="', zcls, '-tm"></div></div></div>', '<div id="',
			this.uuid, '$bwrap" class="', zcls, '-body"><div class="', zcls,
			'-cl"><div class="', zcls, '-cr"><div class="', zcls, '-cm">',
			'<div id="', this.uuid, '$cave" class="', zcls, '-cnt">');
	this.prologHTML_(out);
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	this.epilogHTML_(out);
	out.push('</div></div></div></div><div class="', zcls, '-bl"><div class="',
			zcls, '-br"><div class="', zcls, '-bm"></div></div></div></div></div>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Radio = zk.$extends(zul.wgt.Checkbox, {
	_value: '',
	
	getRadiogroup: function (parent) {
		var wgt = parent || this.parent;
		for (; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.wgt.Radiogroup)) return wgt;
		return null;
	},
	isSelected: function () {
		return this.isChecked();
	},
	setSelected: function (selected, fromServer) {
		this.setChecked(selected, fromServer);
	},
	setChecked: function (checked, fromServer) {
		if (checked != this.isChecked()) {
			this.$supers('setChecked', arguments);
			if (this.getSubnode('real')) {
				var group = this.getRadiogroup();
				
				// bug #1893575 : we have to clean all of the radio at the same group.
				// in addition we can filter unnecessary onCheck with defaultChecked
				if (checked) {
					for (var items = group.getItems(), i = items.length; --i >= 0;) {
						if (items[i] != this) {
							items[i].getSubnode('real').defaultChecked = false;
							items[i]._checked = false;
						}
					}
				}
				if (group) 
					group._fixSelectedIndex();
			}
		}
	},
	getValue: function () {
		return this._value;
	},
	setValue: function (value) {
		if (value == null)
			value = "";
		if (this._value != value)
			this._value = value;
	},
	getName: function () {
		var group = this.getRadiogroup();
		return group != null ? group.getName(): this.uuid;
	},
	contentAttrs_: function () {
		var html = this.$supers('contentAttrs_', arguments);
		html += ' value="' + this.getValue() + '"';
		return html;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-radio";
	},
	beforeParentChanged_: function (newParent) {
		var oldParent = this.getRadiogroup(),
			newParent = newParent ? this.getRadiogroup(newParent) : null;
		if (oldParent != newParent) {
			if (oldParent && oldParent.$instanceof(zul.wgt.Radiogroup))
				oldParent._fixOnRemove(this); 
			if (newParent && newParent.$instanceof(zul.wgt.Radiogroup))
				newParent._fixOnAdd(this); 
		}
	}
});

(_zkwg=_zkpk.Radio).prototype.className='zul.wgt.Radio';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		rg = this.getRadiogroup();
	out.push('<span', this.domAttrs_(), '>', '<input type="radio" id="', uuid,
				'$real"', this.contentAttrs_(), '/><label for="', uuid, '$real"',
				this.labelAttrs_(), ' class="', zcls, '-cnt">', this.domContent_(),
				'</label>', (rg && rg.getOrient() == 'vertical' ? '<br/></span>' : '</span>'));
}
zkmld(_zkwg,_zkmd);
zul.wgt.Radiogroup = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_jsel: -1,
	
	getOrient: function () {
		return this._orient;
	},
	setOrient: function (orient) {
		if (this._orient != orient) {
			this._orient = orient;
			this.rerender();
		}
	},
	getItemAtIndex: function (index) {
		if (index < 0)
			return null;
		return this._getAt(this, {value: 0}, index);
	},
	getItemCount: function () {
		return this.getItems().length;
	},
	getItems: function () {
		return this._concatItem(this);
	},
	getSelectedIndex: function () {
		return this._jsel;
	},
	setSelectedIndex: function (jsel) {
		if (jsel < 0) jsel = -1;
		if (this._jsel != jsel) {
			if (jsel < 0) {
				getSelectedItem().setSelected(false);
			} else {
				getItemAtIndex(jsel).setSelected(true);
			}
		}
	},
	getSelectedItem: function () {
		return this._jsel >= 0 ? this.getItemAtIndex(this._jsel): null;
	},
	setSelectedItem: function (item) {
		if (item == null)
			this.setSelectedIndex(-1);
		else if (item.$instanceof(zul.wgt.Radio))
			item.setSelected(true);
	},
	appendItem: function (label, value) {
		var item = new zul.wgt.Radio();
		item.setLabel(label);
		item.setValue(value);
		this.appendChild(item);
		return item;
	},
	removeItemAt: function (index) {
		var item = this.getItemAtIndex(index);
		this.removeChild(item);
		return item;
	},
	getName: function () {
		return this._name;
	},
	setName: function (name) {
		if (!name) name = null;
		if (this._name != name) {
			this._name = name;
			for (var items = this.getItems(), i = items.length; --i >= 0;)
				items[i].setName(name);
		}
	},
	/** private method */
	_fixSelectedIndex: function () {
		this._jsel = this._fixSelIndex(this, {value: 0});
	},
	_concatItem: function (cmp) {
		var sum = [];
		for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {			
			if (wgt.$instanceof(zul.wgt.Radio)) 
				sum.push(wgt);
			else 
				if (!wgt.$instanceof(zul.wgt.Radiogroup)) { //skip nested radiogroup
					sum = sum.concat(this._concatItem(wgt));
				}
		}
		return sum;
	},
	_getAt: function (cmp, cur, index) {
		for (var cnt = 0, wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
			if (wgt.$instanceof(zul.wgt.Radio)) {
				if (cnt.value++ == index) return wgt;
			} else if (!wgt.$instanceof(zul.wgt.Radiogroup)) {
				var r = this._getAt(wgt, cur, index);
				if (r != null) return r;
			}				
		}
		return null;
	},
	_fixOnAdd: function (child) {
		if (this._jsel >= 0 && child.isSelected()) {
			child.setSelected(false); //it will call _fixSelectedIndex()
		} else {
			this._fixSelectedIndex();
		}
	},
	_fixOnRemove: function (child) {
		if (child.isSelected()) {
			this._jsel = -1;
		} else if (this._jsel > 0) { //excluding 0
			this._fixSelectedIndex();
		}
	},
	_fixSelIndex: function (cmp, cur) {
		for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
			if (wgt.$instanceof(zul.wgt.Radio)) {
				if (wgt.isSelected())
					return cur.value;
				++cur.value;
			} else if (!wgt.$instanceof(zul.wgt.Radiogroup)) {
				var jsel = this._fixSelIndex(wgt, cur);
				if (jsel >= 0) return jsel;
			}
		}
		return -1;
	}
});

(_zkwg=_zkpk.Radiogroup).prototype.className='zul.wgt.Radiogroup';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<span', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</span>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Toolbar = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	_align: "start",
	getAlign: function(){
		return this._align;
	},
	setAlign: function(align){
		if (!align) 
			align = "start";
		if (this._align != align) {
			this._align = align;
			this.rerender();
		}
	},
	getOrient: function(){
		return this._orient;
	},
	setOrient: function(orient){
		if (this._orient != orient) {
			this._orient = orient;
			this.rerender();
		}
	},
	// super
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar" +
		(this.inPanelMold() ? "-panel" : "");
	},
	// protected 
    inPanelMold: function(){
        return this._mold == "panel";
    },
	onChildAdded_: function(){
		this.$supers('onChildAdded_', arguments);
		if (this.inPanelMold()) 
			this.rerender();
	},
	onChildRemoved_: function(){
		this.$supers('onChildRemoved_', arguments);
		if (this.inPanelMold()) 
			this.rerender();
	}
	
});

(_zkwg=_zkpk.Toolbar).prototype.className='zul.wgt.Toolbar';_zkmd={};
_zkmd['panel']=
function (out) {
	var zcls = this.getZclass();
	out.push('<div ', this.domAttrs_(), '>', '<div class="', zcls, '-body ',
				zcls, '-', this.getAlign(), '" >', '<table id="', this.uuid,
				'$cnt" class="', zcls, '-cnt"', zUtl.cellps0, '><tbody>');
	if ('vertical' != this.getOrient()) {
		out.push("<tr>");
		for (var w = this.firstChild; w; w = w.nextSibling) {
			out.push('<td class="', zcls, '-hor">');
			w.redraw(out);
			out.push("</td>");
		}
		out.push("</tr>");
	} else {
		for (var w = this.firstChild; w; w = w.nextSibling) {
			out.push('<tr><td class="', zcls, '-ver">');
			w.redraw(out);
			out.push('</td></tr>');
		}
	}
	out.push('</tbody></table><div class="z-clear"></div></div></div>');
}

_zkmd['default']=
function (out) {
	var zcls = this.getZclass(),
		space = 'vertical' != this.getOrient() ? '' : '<br/>';
		
	out.push('<div ', this.domAttrs_(), '>', '<div id="', this.uuid, '$cave"',
				' class="', zcls, "-body ", zcls, '-', this.getAlign(), '" >');
	
	for (var w = this.firstChild; w; w = w.nextSibling) {
		out.push(space);
		w.redraw(out);
	}
	out.push('</div><div class="z-clear"></div></div>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Toolbarbutton = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,
	_disabled: false,

	isDisabled: function(){
		return this._disabled;
	},
	setDisabled: function(disabled){
		if (this._disabled != disabled) {
			this._disabled = disabled;
			this.updateDomClass_();//update class and attr
		}
	},
	
	getDir: function(){
		return this._dir;
	},
	setDir: function(dir){
		if (this._dir != dir) {
			this._dir = dir;
			var n = this.getNode();
			if (n) n.innerHTML = this.domContent_();
		}
	},
	
	getHref: function(){
		return this._href;
	},
	setHref: function(href){
		if (this._href != href) {
			this._href = href;
			var n = this.getNode();
			if (n) n.href = href;
		}
	},
	
	getOrient: function(){
		return this._orient;
	},
	setOrient: function(orient){
		if (this._orient != orient) {
			this._orient = orient;
			var n = this.getNode();
			if (n) n.innerHTML = this.domContent_();
		}
	},
	
	getTarget: function(){
		return this._target;
	},
	setTarget: function(target){
		if (this._target != target) {
			this._target = target;
			var n = this.getNode();
			if (n) n.target = target;
		}
	},
	
	getTabindex: function(){
		return this._tabindex == -1 ? "" : this._tabindex;
	},
	setTabindex: function(tabindex){
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			var n = this.getNode();
			if (n) n.tabIndex = tabindex < 0 ? null : tabindex;
		}
	},
	// super//
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar-button";
	},

	bind_: function(){
		this.$supers('bind_', arguments);
		var n = this.getNode();
		if (!this._disabled) {
			zEvt.listen(n, "focus", this.proxy(this.domFocus_, '_fxFocus'));
			zEvt.listen(n, "blur", this.proxy(this.domBlur_, '_fxBlur'));
		}
	},
	unbind_: function(){
		var n = this.getNode();
		zEvt.unlisten(n, "focus", this._fxFocus);
		zEvt.unlisten(n, "blur", this._fxBlur);
		this.$supers('unbind_', arguments);
	},
	domContent_: function(){
		var label = zUtl.encodeXML(this.getLabel()), img = this.getImage();
		if (!img) 
			return label;
		
		img = '<img src="' + img + '" align="absmiddle" />';
		var space = "vertical" == this.getOrient() ? '<br/>' : '';
		return this.getDir() == 'reverse' ? label + space + img : img + space + label;
	},
	domClass_: function(no){
		var scls = this.$supers('domClass_', arguments);
		if (this._disabled && (!no || !no.zclass)) {
			var s = this.getZclass();
			if (s) 
				scls += (scls ? ' ' : '') + s + '-disd';
		}
		return scls;
	},
	domAttrs_: function(no){
		var attr = this.$supers('domAttrs_', arguments);
		if (this.getTarget()) 
			attr += ' target="' + this.getTarget() + '"';
		if (this.getTabindex()) 
			attr += ' tabIndex="' + this.getTabindex() + '"';
		if (this.getHref()) 
			attr += ' href="' + this.getHref() + '"';
		else 
			attr += ' href="javascript:;"';
		return attr;
	},
	doClick_: function(wevt, evt){
		if (this._disabled)
			zEvt.stop(evt); //prevent default behavior
		else {
			this.fireX(wevt);
			if (wevt.stopped)
				zEvt.stop(evt); //prevent default behavior
		}
		//Unlike DOM, we don't proprogate to parent (so no calling $supers)
	}
});

(_zkwg=_zkpk.Toolbarbutton).prototype.className='zul.wgt.Toolbarbutton';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<a ', this.domAttrs_(), '>', this.domContent_(), '</a>');
}

zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}_z='zul.box';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.box.Box = zk.$extends(zul.Widget, {
	_mold: 'vertical',
	_align: 'start',
	_pack: 'start',

	/** Returns if it is a vertical box. */
	isVertical: function () {
		return 'vertical' == this._mold;
	},
	/** Returns the orient. */
	getOrient: function () {
		return this._mold;
	},

	/** Returns the align of this button.
	 */
	getAlign: function () {
		return this._align;
	},
	/** Sets the align of this button.
	 */
	setAlign: function(align) {
		if (this._align != align) {
			this._align = align;
			//TODO
		}
	},
	/** Returns the pack of this button.
	 */
	getPack: function () {
		return this._pack;
	},
	/** Sets the pack of this button.
	 */
	setPack: function(pack) {
		if (this._pack != pack) {
			this._pack = pack;
			//TODO
		}
	},

	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: this.isVertical() ? "z-vbox" : "z-hbox";
	},

	onChildVisible_: function (child, visible) {
		this.$supers('onChildVisible_', arguments);
		if (this.desktop) this._fixChildDomVisible(child, visible);
	},
	replaceChildHTML_: function (child) {
		this.$supers('replaceChildHTML_', arguments);
		this._fixChildDomVisible(child, child._visible);
	},
	_fixChildDomVisible: function (child, visible) {
		var n = child.getSubnode('chdex');
		if (n) n.style.display = visible ? '': 'none';
		n = child.getSubnode('chdex2');
		if (n) n.style.display = visible ? '': 'none';

		if (this.lastChild == child) {
			n = child.previousSibling;
			if (n) {
				n = n.getSubnode('chdex2');
				if (n) n.style.display = visible ? '': 'none';
			}
		}
	},

	insertChildHTML_: function (child, before, desktop) {
		if (before) {
			zDom.insertHTMLBefore(before.getSubnode('chdex'), this.encloseChildHTML_(child));
		} else {
			var n = this.getNode();
			if (this.isVertical())
				n = n.tBodies[0];
			else
				n = n.tBodies[0].rows[0];
			zDom.insertHTMLBeforeEnd(n, this.encloseChildHTML_(child, true));
		}
		child.bind_(desktop);
	},
	removeChildHTML_: function (child, prevsib) {
		this.$supers('removeChildHTML_', arguments);
		zDom.remove(child.uuid + '$chdex');
		zDom.remove(child.uuid + '$chdex2');
		if (prevsib && this.lastChild == prevsib) //child is last
			zDom.remove(prevsib.uuid + '$chdex2');
	},
	encloseChildHTML_: function (child, prefixSpace, out) {
		var oo = [];
		if (this.isVertical()) {
			oo.push('<tr id="', child.uuid, '$chdex"',
				this._childOuterAttrs(child),
				'><td', this._childInnerAttrs(child),
				'>');
			child.redraw(oo);
			oo.push('</td></tr>');

			if (child.nextSibling)
				oo.push(this._spacingHTML(child));
			else if (prefixSpace) {
				var pre = child.previousSibling;
				if (pre) oo.unshift(this._spacingHTML(pre));
			}
		} else {
			oo.push('<td id="', child.uuid, '$chdex"',
				this._childOuterAttrs(child),
				this._childInnerAttrs(child),
				'>');
			child.redraw(oo);
			oo.push('</td>');

			if (child.nextSibling)
				oo.push(this._spacingHTML(child));
			else if (prefixSpace) {
				var pre = child.previousSibling;
				if (pre) oo.unshift(this._spacingHTML(pre));
			}
		}
		if (!out) return oo.join('');

		for (var j = 0, len = oo.length; j < len; ++j)
			out.push(oo[j]);
	},
	_spacingHTML: function (child) {
		var oo = [],
			spacing = this.spacing,
			spacing0 = spacing && spacing.startsWith('0')
				&& (spacing.length == 1 || zk.isDigit(spacing.charAt(1))),
			vert = this.isVertical(),
			spstyle = spacing ? (vert?'height:':'width:') + spacing: '';

		oo.push('<t', vert?'r':'d', ' id="', child.uuid,
			'$chdex2" class="', this.getZclass(), '-sep"');

		var s = spstyle;
		if (spacing0 || !child.isVisible()) s = 'display:none' + s;
		if (s) oo.push(' style="', s, '"');

		oo.push('>', vert?'<td>':'', zUtl.img0, vert?'</td></tr>':'</td>');
		return oo.join('');
	},
	_childOuterAttrs: function (child) {
		var html = '';
		if (child.$instanceof(zul.box.Splitter))
			html = ' class="' + child.getZclass() + '-outer"';
		else if (this.isVertical()) {
			var v = this.getPack();
			if (v) html = ' valign="' + zul.box.Box._toValign(v) + '"';
		} else
			return ''; //if hoz and not splitter, display handled in _childInnerAttrs

		if (!child.isVisible()) html += ' style="display:none"';
		return html;
	},
	_childInnerAttrs: function (child) {
		var html = '',
			vert = this.isVertical(),
			$Splitter = zul.box.Splitter;
		if (child.$instanceof($Splitter))
			return vert ? ' class="' + child.getZclass() + '-outer-td"': '';
				//spliter's display handled in _childOuterAttrs

		var v = vert ? this.getAlign(): this.getPack();
		if (v) html += ' align="' + zul.box.Box._toHalign(v) + '"'

		var style = '', szes = this._sizes;
		if (szes) {
			for (var j = 0, len = szes.length, c = this.firstChild;
			c && j < len; c = c.nextSibling) {
				if (child == c) {
					style = (vert ? 'height:':'width:') + szes[j];
					break;
				}
				if (!c.$instanceof($Splitter))
					++j;
			}
		}

		if (!vert && !child.isVisible()) style += ';display:none';
		return style ? html + ' style="' + style + '"': html;
	},

	//called by Splitter
	_bindWatch: function () {
		if (!this._watchBound) {
			this._watchBound = true;
			zWatch.listen("onSize", this);
			zWatch.listen("onVisible", this);
			zWatch.listen("onHide", this);
		}
	},
	unbind_: function () {
		if (this._watchBound) {
			this._watchBound = false;
			zWatch.unlisten("onSize", this);
			zWatch.unlisten("onVisible", this);
			zWatch.unlisten("onHide", this);
		}

		this.$supers('unbind_', arguments);
	},
	onSize: _zkf = function () {
		if (!this.isRealVisible()) return;

		var $Splitter = zul.box.Splitter;
		for (var c = this.firstChild;; c = c.nextSibling) {
			if (!c) return; //no splitter
			if (c.$instanceof($Splitter)) //whether the splitter has been dragged
				break;
		}

		var vert = this.isVertical(), node = this.getNode();

		//Bug 1916473: with IE, we have make the whole table to fit the table
		//since IE won't fit it even if height 100% is specified
		if (zk.ie) {
			var p = node.parentNode;
			if (zDom.tag(p) == "TD") {
				var nm = vert ? "height": "width",
					sz = vert ? p.clientHeight: p.clientWidth;
				if ((node.style[nm] == "100%" || this._box100) && sz) {
					node.style[nm] = sz + "px";
					this._box100 = true;
				}
			}
		}

		//Note: we have to assign width/height fisrt
		//Otherwise, the first time dragging the splitter won't be moved
		//as expected (since style.width/height might be "")

		var nd = vert ? node.rows: node.rows[0].cells,
			total = vert ? zDom.revisedHeight(node, node.offsetHeight):
				zDom.revisedWidth(node, node.offsetWidth);

		for (var i = nd.length; --i >= 0;) {
			var d = nd[i];
			if (zDom.isVisible(d))
				if (vert) {
					var diff = d.offsetHeight;
					if(d.id && !d.id.endsWith("$chdex2")) { //TR
						//Bug 1917905: we have to manipulate height of TD in Safari
						if (d.cells.length) {
							var c = d.cells[0];
							c.style.height = zDom.revisedHeight(c, i ? diff: total) + "px";
						}
						d.style.height = ""; //just-in-case
					}
					total -= diff;
				} else {
					var diff = d.offsetWidth;
					if(d.id && !d.id.endsWith("$chdex2")) //TD
						d.style.width = zDom.revisedWidth(d, i ? diff: total) + "px";
					total -= diff;
				}
		}
	},
	onVisible: _zkf,
	onHide: _zkf
},{
	_toValign: function (v) {
		return v ? "start" == v ? "top": "center" == v ? "middle":
			"end" == v ? "bottom": v: null;
	},
	_toHalign: function (v) {
		return v ? "start" == v ? "left": "end" == v ? "right": v: null;
	}
});

(_zkwg=_zkpk.Box).prototype.className='zul.box.Box';_zkmd={};
_zkmd['vertical']=
function (out) {
	out.push('<table', this.domAttrs_(), zUtl.cellps0, '>');

	for (var w = this.firstChild; w; w = w.nextSibling)
		this.encloseChildHTML_(w, false, out);

	out.push('</table>');
}
_zkmd['horizontal']=
function (out) {
	out.push('<table', this.domAttrs_(), zUtl.cellps0, '><tr');
	
	var	v = this.getAlign();
	if (v) out.push(' valign="', zul.box.Box._toValign(v), '"');
	out.push('>');

	for (var w = this.firstChild; w; w = w.nextSibling)
		this.encloseChildHTML_(w, false, out);

	out.push('</tr></table>');
}
zkmld(_zkwg,_zkmd);
zul.box.Splitter = zk.$extends(zul.Widget, {
	_collapse: "none",
	_open: true,

	/** Returns if it is a vertical box. */
	isVertical: function () {
		var p = this.parent;
		return !p || p.isVertical();
	},
	/** Returns the orient. */
	getOrient: function () {
		var p = this.parent;
		return p ? p.getOrient(): "vertical";
	},

	/** Returns whether it is open.
	 */
	isOpen: function () {
		return this._open;
	},
	/** Sets whther it is open.
	 */
	setOpen: function(open, fromServer) {
		if (this._open != open) {
			this._open = open;

			var node = this.getNode();
			if (!node) return;
			var colps = this.getCollapse();
			if (!colps || "none" == colps) return; //nothing to do

			var nd = this.getSubnode('chdex'),
				tn = zDom.tag(nd),
				vert = this.isVertical(),
				$Splitter = this.$class,
				before = colps == "before",
				sib = before ? $Splitter._prev(nd, tn): $Splitter._next(nd, tn),
				sibwgt = zk.Widget.$(sib),
				fd = vert ? "height": "width", diff;
			if (sib) {
				sibwgt.setDomVisible_(sib, open); //fire onVisible/onHide
				sibwgt.parent._fixChildDomVisible(sibwgt, open);

				diff = zk.parseInt(sib.style[fd]);

				if (!before && sibwgt && !sibwgt.nextSibling) {
					var sp = this.getSubnode('chdex2');
					if (sp) {
						sp.style.display = open ? '': 'none'; //no onVisible/onHide
						diff += zk.parseInt(sp.style[fd]);
					}
				}
			}

			sib = before ? $Splitter._next(nd, tn): $Splitter._prev(nd, tn);
			if (sib) {
				diff = zk.parseInt(sib.style[fd]) + (open ? -diff: diff);
				if (diff < 0) diff = 0;
				sib.style[fd] = diff + "px";
				if (open) zWatch.fireDown('onSize', null, sibwgt);
			}

			node.style.cursor = !open ? "default" : vert ? "s-resize": "e-resize";
			this._fixNSDomClass();

			this._fixbtn();
			this._fixszAll();

			if (!fromServer) this.fire('onOpen', open);
		}
	},
	/** Returns the collapse of this button.
	 */
	getCollapse: function () {
		return this._collapse;
	},
	/** Sets the collapse of this button.
	 */
	setCollapse: function(collapse) {
		if (this._collapse != collapse) {
			this._collapse = collapse;
			if (this.desktop) {
				this._fixbtn();
				this._fixsz();
			}
		}
	},

	//super//
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls:
			"z-splitter" + (this.isVertical() ? "-ver" : "-hor");
	},
	setZclass: function () {
		this.$supers('setZclass', arguments);
		if (this.desktop)
			this._fixDomClass(true);
	},

	bind_: function () {
		this.$supers('bind_', arguments);

		var box = this.parent;
		if (box) box._bindWatch();

		zWatch.listen("onSize", this);
		zWatch.listen("beforeSize", this);
		zWatch.listen("onVisible", this);

		this._fixDomClass();
			//Bug 1921830: if spiltter is invalidated...

		var node = this.getNode(),
			$Splitter = this.$class;
			vert = this.isVertical();
			btn = this.button = this.getSubnode('btn');
		node.style.cursor = this.isOpen() ?
			vert ? "s-resize": "e-resize": "default";
		btn.style.cursor = "pointer";

		if (zk.ie) {
			zEvt.listen(btn, "mouseover", $Splitter.onover);
			zEvt.listen(btn, "mouseout", $Splitter.onout);
		}
		zEvt.listen(btn, "click", $Splitter.onclick);

		this._fixbtn();

		this._drag = new zk.Draggable(this, node, {
			constraint: this.getOrient(), ignoredrag: $Splitter._ignoresizing,
			ghosting: $Splitter._ghostsizing, overlay: true,
			snap: $Splitter._snap, endeffect: $Splitter._endDrag});

		if (!this.isOpen()) {
			var nd = this.getSubnode('chdex'), tn = zDom.tag(nd),
				colps = this.getCollapse();
			if (!colps || "none" == colps) return; //nothing to do

			var sib = colps == "before" ? $Splitter._prev(nd, tn): $Splitter._next(nd, tn);
			zDom.hide(sib); //no onHide at bind_
			var sibwgt = zk.Widget.$(sib);
			sibwgt.parent._fixChildDomVisible(sibwgt, false);

			this._fixNSDomClass();
		}
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("beforeSize", this);
		zWatch.unlisten("onVisible", this);

		var $Splitter = this.$class,
			btn = this.button;
		if (btn) {
			if (zk.ie) {
				zEvt.unlisten(btn, "mouseover", $Splitter.onover);
				zEvt.unlisten(btn, "mouseout", $Splitter.onout);
			}
			zEvt.unlisten(btn, "click", $Splitter.onclick);
		}

		this._drag.destroy();
		this._drag = null;
		this.$supers('unbind_', arguments);
	},

	/** Fixed DOM class for the enclosing TR/TD tag. */
	_fixDomClass: function (inner) {
		var node = this.getNode(),
			p = node.parentNode;
		if (p) {
			var vert = this.isVertical(),
				zcls = this.getZclass();;
			if (vert) p = p.parentNode; //TR
			if (p && p.id.endsWith("$chdex")) {
				p.className = zcls + "-outer";
				if (vert)
					node.parentNode.className = zcls + "-outer-td";
			}
		}
		if (inner) this._fixbtn();
	},
	_fixNSDomClass: function () {
		var node = this.getNode(),
			zcls = this.getZclass(),
			open = this.isOpen();
		if(open && zDom.hasClass(node, zcls+"-ns"))
			zDom.rmClass(node, zcls+"-ns");
		else if (!open && !zDom.hasClass(node, zcls+"-ns"))
			zDom.addClass(node, zcls+"-ns");
	},
	_fixbtn: function () {
		var btn = this.button,
			colps = this.getCollapse();
		if (!colps || "none" == colps) {
			btn.style.display = "none";
		} else {
			var zcls = this.getZclass(),
				before = colps == "before";
			if (!this.isOpen()) before = !before;

			if (this.isVertical()) {
				zDom.rmClass(btn, zcls + "-btn-" + (before ? "b" : "t"));
				zDom.addClass(btn, zcls + "-btn-" + (before ? "t" : "b"));
			} else {
				zDom.rmClass(btn, zcls + "-btn-" + (before ? "r" : "l"));
				zDom.addClass(btn, zcls + "-btn-" + (before ? "l" : "r"));
			}
			btn.style.display = "";
		}
	},
	_fixsz: _zkf = function () {
		if (!this.isRealVisible()) return;

		var node = this.getNode(), pn = node.parentNode;
		if (pn) {
			var btn = this.button,
				bfcolps = "before" == this.getCollapse();
			if (this.isVertical()) {
				//Note: when the browser resizes, it might adjust splitter's wd/hgh
				//Note: the real wd/hgh might be bigger than 8px (since the width
				//of total content is smaller than pn's width)
				//We 'cheat' by align to top or bottom depending on z.colps
				if (bfcolps) {
					pn.vAlign = "top";
					pn.style.backgroundPosition = "top left";
				} else {
					pn.vAlign = "bottom";
					pn.style.backgroundPosition = "bottom left";
				}

				node.style.width = ""; // clean width
				node.style.width = pn.clientWidth + "px"; //all wd the same
				btn.style.marginLeft = ((node.offsetWidth - btn.offsetWidth) / 2)+"px";
			} else {
				if (bfcolps) {
					pn.align = "left";
					pn.style.backgroundPosition = "top left";
				} else {
					pn.align = "right";
					pn.style.backgroundPosition = "top right";
				}

				node.style.height = ""; // clean height
				node.style.height =
					(zk.safari ? pn.parentNode.clientHeight: pn.clientHeight)+"px";
					//Bug 1916332: TR's clientHeight is correct (not TD's) in Safari
				btn.style.marginTop = ((node.offsetHeight - btn.offsetHeight) / 2)+"px";
			}
		}
	},
	onVisible: _zkf,
	onSize: _zkf,
	beforeSize: function () {
		this.getNode().style[this.isVertical() ? "width": "height"] = "";
	},

	_fixszAll: function () {
		//1. find the topmost box
		var box = this.parent;
		if (box) this.$class._fixKidSplts(box.getNode());
		else this._fixsz();
	}
},{
	onclick: function (evt) {
		var wgt = zk.Widget.$(evt);
		zDom.rmClass(wgt.button, wgt.getZclass() + "-btn-visi");
		wgt.setOpen(!wgt.isOpen());
	},

	//drag&drop
	_ignoresizing: function (draggable, pointer, evt) {
		var wgt = draggable.control;
		if (!wgt.isOpen()) return true;

		var run = draggable.run = {},
			node = wgt.getNode();
		run.org = zDom.cmOffset(node);
		var nd = wgt.getSubnode('chdex'),
			tn = zDom.tag(nd),
			$Splitter = zul.box.Splitter;
		run.prev = $Splitter._prev(nd, tn);
		run.next = $Splitter._next(nd, tn);
		run.prevwgt = wgt.previousSibling;
		run.nextwgt = wgt.nextSibling;
		run.z_offset = zDom.cmOffset(node);
		return false;
	},
	_ghostsizing: function (draggable, ofs, evt) {
		var node = draggable.node;
		var html = '<div id="zk_ddghost" style="font-size:0;line-height:0;background:#AAA;position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zDom.offsetWidth(node)+'px;height:'+zDom.offsetHeight(node)
			+'px;"></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		return zDom.$("zk_ddghost");
	},
	_endDrag: function (draggable) {
		var wgt = draggable.control,
			node = wgt.getNode(),
			$Splitter = zul.box.Splitter,
			flInfo = $Splitter._fixLayout(wgt),
			run = draggable.run, diff, fd;

		if (wgt.isVertical()) {
			diff = run.z_point[1];
			fd = "height";

			//We adjust height of TD if vert
			if (run.next && run.next.cells.length) run.next = run.next.cells[0];
			if (run.prev && run.prev.cells.length) run.prev = run.prev.cells[0];
		} else {
			diff = run.z_point[0];
			fd = "width";
		}
		if (!diff) return; //nothing to do

		if (run.nextwgt) zWatch.fireDown('beforeSize', null, run.nextwgt);
		if (run.prevwgt) zWatch.fireDown('beforeSize', null, run.prevwgt);
		
		if (run.next) {
			var s = zk.parseInt(run.next.style[fd]);
			s -= diff;
			if (s < 0) s = 0;
			run.next.style[fd] = s + "px";
		}
		if (run.prev) {
			var s = zk.parseInt(run.prev.style[fd]);
			s += diff;
			if (s < 0) s = 0;
			run.prev.style[fd] = s + "px";
		}

		if (run.nextwgt) zWatch.fireDown('onSize', null, run.nextwgt);
		if (run.prevwgt) zWatch.fireDown('onSize', null, run.prevwgt);

		$Splitter._unfixLayout(flInfo);
			//Stange (not know the cause yet): we have to put it
			//befor _fixszAll and after onSize

		wgt._fixszAll();
			//fix all splitter's size because table might be with %
		draggable.run = null;//free memory
	},
	_snap: function (draggable, pos) {
		var run = draggable.run,
			wgt = draggable.control,
			x = pos[0], y = pos[1];
		if (wgt.isVertical()) {
			if (y <= run.z_offset[1] - run.prev.offsetHeight) {
				y = run.z_offset[1] - run.prev.offsetHeight;
			} else {
				var max = run.z_offset[1] + run.next.offsetHeight - wgt.getNode().offsetHeight;
				if (y > max) y = max;
			}
		} else {
			if (x <= run.z_offset[0] - run.prev.offsetWidth) {
				x = run.z_offset[0] - run.prev.offsetWidth;
			} else {
				var max = run.z_offset[0] + run.next.offsetWidth - wgt.getNode().offsetWidth;
				if (x > max) x = max;
			}
		}
		run.z_point = [x - run.z_offset[0], y - run.z_offset[1]];

		return [x, y];
	},

	_next: function (n, tn) {
		return zDom.nextSibling(zDom.nextSibling(n, tn), tn);
	},
	_prev: function (n, tn) {
		return zDom.previousSibling(zDom.previousSibling(n, tn), tn);
	},

	_fixKidSplts: function (n) {
		if (zDom.isVisible(n)) { //n might not be an element
			var wgt = n.z_wgt, //don't use zk.Widget.$ since we check each node
				$Splitter = zul.box.Splitter;
			if (wgt && wgt.$instanceof($Splitter))
				wgt._fixsz();

			for (n = n.firstChild; n; n = n.nextSibling)
				$Splitter._fixKidSplts(n);
		}
	}
});

if (zk.ie) {
	zul.box.Splitter.onover = function (evt) {
		var wgt = zk.Widget.$(evt);
		zDom.addClass(wgt.button, wgt.getZclass() + '-btn-visi');
	};
	zul.box.Splitter.onout = function (evt) {
		var wgt = zk.Widget.$(evt);
		zDom.rmClass(wgt.button, wgt.getZclass() + '-btn-visi');
	};
}
/** Use fix table layout */
if (zk.opera) { //only opera needs it
	zul.box.Splitter._fixLayout = function (wgt) {
		var box = wgt.parent.getNode();
		if (box.style.tableLayout != "fixed") {
			var fl = [box, box.style.tableLayout];
			box.style.tableLayout = "fixed";
			return fl;
		}
	};
	zul.box.Splitter._unfixLayout = function (fl) {
		if (fl) fl[0].style.tableLayout = fl[1];
	};
} else
	zul.box.Splitter._fixLayout = zul.box.Splitter._unfixLayout = zk.$void;

(_zkwg=_zkpk.Splitter).prototype.className='zul.box.Splitter';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<div', this.domAttrs_(), '><span id="',
			this.uuid, '$btn" style="display:none"></span></div>');
}
zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}_z='zul.grid';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.grid.HeaderWidget = zk.$extends(zul.LabelImageWidget, {
	getAlign: function () {
		return this._align;
	},
	setAlign: function (align) {
		if (this._align != align) {
			this._align = align;
			this.invalidateWhole_();
		}
	},
	getValign: function () {
		return this._valign;
	},
	setValign: function (valign) {
		if (this._valign != valign) {
			this._valign = valign;
			this.invalidateWhole_();
		}
	},
	invalidateWhole_: function () {
		var wgt = this.getOwner();
		if (wqt) wgt.rerender();
	},
	getOwner: function () {
		return this.parent ? this.parent.parent : null;
	},
	isSortable_: function () {
		return false;
	},
	getColAttrs: function () {
		return (this._align ? ' align="' + this._align + '"' : '')
			+ (this._valign ? ' valign="' + this._valign + '"' : '') ;
	},
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			this.invalidateWhole();
		}
	},
	domAttrs_: function (no) {
		var attrs = this.$supers('domAttrs_', arguments);
		return attrs + this.getColAttrs();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.parent.isSizable()) this._initsz();
		this._fixedFaker();
	},
	unbind_: function () {
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		this.$supers('unbind_', arguments);
	},
	_initsz: function () {
		var n = this.getNode();
		if (n && !this._drag) {
			var $Header = this.$class;
			this._drag = new zk.Draggable(this, null, {
				revert: true, constraint: "horizontal",
				ghosting: $Header._ghostsizing,
				endghosting: $Header._endghostsizing,
				snap: $Header._snapsizing,
				ignoredrag: $Header._ignoresizing,
				endeffect: $Header._aftersizing
			});
		}
	},
	_fixedFaker: function () {
		var n = this.getNode(),
			index = zDom.cellIndex(n),
			owner = this.getOwner();
		for (var faker, fs = this.$class._faker, i = fs.length; --i >= 0;) {
			faker = owner['e' + fs[i]]; // internal element
			if (faker && !this.getSubnode(fs[i])) 
				faker[faker.cells.length > index ? "insertBefore" : "appendChild"]
					(this._createFaker(n, fs[i]), faker.cells[index]);
		}
	},
	_createFaker: function (n, postfix) {
		var t = document.createElement("TH"), 
			d = document.createElement("DIV");
		t.id = n.id + "$" + postfix;
		t.className = n.className;
		t.style.cssText = n.style.cssText;
		d.style.overflow = "hidden";
		t.appendChild(d);
		return t;
	},
	doClick_: function (evt) {
		if (!zk.dragging && zk.Widget.$(evt.nativeTarget) == this && this.isSortable_() 
			&& zDom.tag(evt.nativeTarget) != "INPUT") {
			this.fire('onSort');
			evt.stop();
		}
	},
	doMouseMove_: function (evt) {
		if (zk.dragging || !this.parent.isSizable()) return;
		var n = this.getNode(),
			ofs = zDom.revisedOffset(n); // Bug #1812154
		if (this._insizer(evt.data.pageX - ofs[0])) {
			zDom.addClass(n, this.getZclass() + "-sizing");
		} else {
			zDom.rmClass(n, this.getZclass() + "-sizing");
		}
	},
	doMouseOut_: function (evt) {
		if (this.parent.isSizable()) {
			var n = this.getNode()
			zDom.rmClass(n, this.getZclass() + "-sizing");
		}
	},
	_insizer: function (x) {
		return x >= this.getNode().offsetWidth - 10;
	}
}, {
	_faker: ["hdfaker", "bdfaker", "ftfaker"],
	
	_onSizingMarshal: function () {
		return [this.index, this.uuid, this.width, this.keys ? this.keys.marshal(): ''];
	},
	//dragdrop//
	_ghostsizing: function (dg, ofs, evt) {
		var wgt = dg.control,
			el = wgt.getOwner().eheadtbl;
			of = zDom.revisedOffset(el),
			n = wgt.getNode();
		
		ofs[1] = of[1];
		ofs[0] += zDom.offsetWidth(n);
		document.body.insertAdjacentHTML("afterBegin",
			'<div id="zk_hdghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:3px;height:'+zDom.offsetHeight(el.parentNode.parentNode)
			+'px;background:darkgray"></div>');
		return zDom.$("zk_hdghost");		
	},
	_endghostsizing: function (dg, origin) {
		dg._zszofs = zDom.revisedOffset(dg.node)[0] - zDom.revisedOffset(origin)[0];
	},
	_snapsizing: function (dg, pointer) {
		var n = dg.control.getNode(),
			ofs = zDom.revisedOffset(n);
		pointer[0] += zDom.offsetWidth(n); 
		if (ofs[0] + dg._zmin >= pointer[0])
			pointer[0] = ofs[0] + dg._zmin;
		return pointer;
	},
	_ignoresizing: function (dg, pointer, evt) {
		var wgt = dg.control,
			n = wgt.getNode(),
			ofs = zDom.revisedOffset(n); // Bug #1812154
			
		if (wgt._insizer(pointer[0] - ofs[0])) {
			dg._zmin = 10 + zDom.padBorderWidth(n);		
				return false;
		}
		return true;
	},
	_aftersizing: function (dg, evt) {
		var wgt = dg.control,
			n = wgt.getNode(),
			owner = wgt.getOwner(),
			wd = dg._zszofs,
			table = owner.eheadtbl,
			head = table.tBodies[0].rows[0], 
			rwd = zDom.revisedWidth(n, wd),
			cells = head.cells,
			cidx = zDom.cellIndex(n),
			total = 0;
			
		for (var k = cells.length; --k >= 0;)
			if (k !== cidx) total += cells[k].offsetWidth;

		// For Opera, the code of adjusting width must be in front of the adjusting table.
		// Otherwise, the whole layout in Opera always shows wrong.
		if (owner.efoottbl) {
			owner.eftfaker.cells[cidx].style.width = wd + "px";
		}
		var fixed;
		if (owner.ebodytbl) {
			if (zk.opera && !owner.ebodytbl.style.tableLayout) {
				fixed = "auto";
				owner.ebodytbl.style.tableLayout = "fixed";
			}
			owner.ebdfaker.cells[cidx].style.width = wd + "px";
		}
		
		head.cells[cidx].style.width = wd + "px";
		n.style.width = rwd + "px";
		var cell = n.firstChild;
		cell.style.width = zDom.revisedWidth(cell, rwd) + "px";
		
		table.style.width = total + wd + "px";		
		if (owner.efoottbl)
			owner.efoottbl.style.width = table.style.width;
		
		if (owner.ebodytbl)
			owner.ebodytbl.style.width = table.style.width;
			
		if (zk.opera && fixed) owner.ebodytbl.style.tableLayout = fixed;
		
		wgt.parent.fire('onColSize', {
			index: cidx,
			uuid: wgt.uuid,
			width: wd + "px",
			keys: zEvt.keyMetaData(evt),
			marshal: wgt.$class._onSizingMarshal
		}, null, 0);
	}
});

(_zkwg=_zkpk.HeaderWidget).prototype.className='zul.grid.HeaderWidget';
zul.grid.HeadersWidget = zk.$extends(zul.Widget, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen('onColSize', this, null, -1000);
	},
	onColSize: function (evt) {
		var owner = this.parent;
		if (!owner.isFixedLayout()) owner.$class.adjustHeadWidth(owner);
		owner.fire('onInnerWidth', owner.eheadtbl.style.width);
		owner.fireScrollRender(zk.gecko ? 200 : 60);
	},
	isSizable: function () {
		return this._sizable;
	},
	setSizable: function (sizable) {
		if (this._sizable != sizable) {
			this._sizable = sizable;
			this.rerender();
		}
	},
	unbind_: function () {
		if (this.hdfaker) zDom.remove(this.hdfaker);
		if (this.bdfaker) zDom.remove(this.bdfaker);
		if (this.ftfaker) zDom.remove(this.ftfaker);
		this.$supers('unbind_', arguments);
	}
});

(_zkwg=_zkpk.HeadersWidget).prototype.className='zul.grid.HeadersWidget';
zul.grid.Column = zk.$extends(zul.grid.HeaderWidget, {
	_sortDir: "natural",
	_sortAsc: "none",
	_sortDsc: "none",
	
	$init: function () {
		this.$supers('$init', arguments);
		this.listen('onSort', this, null, -1000);
	},
	getGrid: function () {
		return this.getOwner();
	},
	setSort: function (type) {
		if (type && type.startsWith('client')) {
			this.setSortAscending(type);
			this.setSortDescending(type);
		} else {
			this.setSortAscending('none');
			this.setSortDescending('none');
		}
	},
	getSortDirection: function () {
		return this._sortDir;
	},
	setSortDirection: function (sortDir) {
		if (this._sortDir != sortDir) {
			this._sortDir = sortDir;
			var n = this.getNode();
			
			if (n) {
				var zcls = this.getZclass();
				zDom.rmClass(n, zcls + "-sort-dsc");
				zDom.rmClass(n, zcls + "-sort-asc");
				switch (sortDir) {
				case "ascending":
					zDom.addClass(n, zcls + "-sort-asc");
					break;
				case "descending":
					zDom.addClass(n, zcls + "-sort-dsc");
					break;
				default: // "natural"
					zDom.addClass(n, zcls + "-sort");
					break;
				}
			}
		}
	},
	isSortable_: function () {
		return this._sortAsc != "none" || this._sortDsc != "none";
	},
	getSortAscending: function () {
		return this._sortAsc;
	},
	setSortAscending: function (sorter) {
		if (!sorter) sorter = "none";
		if (this._sortAsc != sorter) {
			this._sortAsc = sorter;
			var n = this.getNode(),
				zcls = this.getZclass();
			if (n) {
				if (sorter == "none") {
					zDom.rmClass(n, zcls + "-sort-asc");
					if (this._sortDsc == "none")
						zDom.rmClass(n, zcls + "-sort");					
				} else
					zDom.addClass(n, zcls + "-sort");
			}
		}
	},
	getSortDescending: function () {
		return this._sortDsc;
	},
	setSortDescending: function (sorter) {
		if (!sorter) sorter = "none";
		if (this._sortDsc != sorter) {
			this._sortDsc = sorter;
			var n = this.getNode(),
				zcls = this.getZclass();
			if (n) {
				if (sorter == "none") {
					zDom.rmClass(n, zcls + "-sort-dsc");
					if (this._sortAsc == "none")
						zDom.rmClass(n, zcls + "-sort");					
				} else
					zDom.addClass(n, zcls + "-sort");
			}
		}
	},
	sort: function (ascending, evt) {
		var dir = this.getSortDirection();
		if (ascending) {
			if ("ascending" == dir) return false;
		} else {
			if ("descending" == dir) return false;
		}

		var sorter = ascending ? this._sortAsc: this._sortDsc;
		if (sorter == "fromServer")
			return false;
		else if (sorter == "none") {
			evt.stop();
			return false;
		}
		
		var grid = this.getGrid();
		if (!grid || grid.isModel()) return false;
			// if in model, the sort should be done by server
			
		var	rows = grid.rows;		
		if (!rows) return false;
		rows.parent.removeChild(rows);
		evt.stop();
		var d = [], col = this.getChildIndex();
		for (var i = 0, z = 0, row = rows.firstChild; row; row = row.nextSibling, z++)
			for (var k = 0, cell = row.firstChild; cell; cell = cell.nextSibling, k++) 
				if (k == col) {
					d[i++] = {
						wgt: cell,
						index: z
					};
				}
		
		var dsc = dir == "ascending" ? 1 : -1,
			fn = this.sorting,
			isNumber = sorter == "client(number)";
		d.sort(function(a, b) {
			var v = fn(a.wgt, b.wgt, isNumber) * dsc;
			if (v == 0) {
				v = (a.index < b.index ? -1 : 1);
			}
			return v;
		});
		for (var i = 0, k = d.length;  i < k; i++) {
			rows.appendChild(d[i].wgt.parent);
		}
		this._fixDirection(ascending);
		grid.appendChild(rows);
		return true;
	},
	sorting: function(a, b, isNumber) {
		var v1 = a.getValue(), v2 = b.getValue();
			if (isNumber) return v1 - v2;
		return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
	},
	_fixDirection: function (ascending) {
		//maintain
		for (var w = this.parent.firstChild; w; w = w.nextSibling) {
			w.setSortDirection(
				w != this ? "natural": ascending ? "ascending": "descending");
		}
	},
	setLabel: function (label) {
		this.$supers('setLabel', arguments);
		// TODO menupopup
	},
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			this.$supers('setVisible', arguments);
			// TODO menupopup
		}
	},
	onSort: function (evt) {
		var dir = this.getSortDirection();
		if ("ascending" == dir) this.sort(false, evt);
		else if ("descending" == dir) this.sort(true, evt);
		else if (!this.sort(true, evt)) this.sort(false, evt);
	},
	getZclass: function () {
		return this._zclass == null ? "z-column" : this._zclass;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this._sortAsc != "none" || this._sortDsc != "none" ?  this.getZclass() + '-sort': '';
			return scls != null ? scls + ' ' + added : added;
		}
		return scls;
	}
});

(_zkwg=_zkpk.Column).prototype.className='zul.grid.Column';_zkmd={};
_zkmd['default']=
function (out) {
	var zcls = this.getZclass();
	out.push('<th', this.domAttrs_(), '><div id="', this.uuid, '$cave" class="',
			zcls, '-cnt">', this.domContent_());
	if (this.parent.menupopup && this.parent.menupopup != 'none')
		out.push('<a id="', this.uuid, '$btn"  href="javascript:;" class="', zcls, '-btn"></a>');
	
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></th>');	
}

zkmld(_zkwg,_zkmd);
zul.grid.Columns = zk.$extends(zul.grid.HeadersWidget, {
	_mpop: "none",
	_columnshide: true,
	_columnsgroup: true,
	
	getGrid: function () {
		return this.parent;
	},
	setColumnshide: function (columnshide) {
		if (this._columnshide != columnshide) {
			this._columnshide = columnshide;
			//postOnInitLater();
			//smartUpdate("z.columnshide", _columnshide);
		}
	},
	isColumnshide: function () {
		return this._columnshide;
	},
	setColumnsgroup: function (columnsgroup) {
		if (this._columnsgroup != columnsgroup) {
			this._columnsgroup = columnsgroup;
			//postOnInitLater();
			//smartUpdate("z.columnsgroup", _columnsgroup);
		}
	},
	isColumnsgroup: function () {
		return this._columnsgroup;
	},
	getMenupopup: function () {
		return this._mpop;
	},
	setMenupopup: function (mpop) {
		/**if (!Objects.equals(_mpop, mpop)) {
			_mpop = mpop;
			invalidate();
			postOnInitLater();
		}*/
	},
	rerender: function () {
		if (this.desktop) {
			if (this.parent)
				this.parent.rerender();
			else 
				this.$superts('rerender', arguments);
		}
	},
	setPopup: function (mpop) {
		if (zk.Widget.isInstance(mpop))
			this._mpop = mpop;
	},
	_getMpopId: function () {
		/**final String mpop = getMenupopup();
		if ("none".equals(mpop)) return "zk_n_a";
		if ("auto".equals(mpop)) return _menupopup.getId();
		return mpop;*/
	},
	getZclass: function () {
		return this._zclass == null ? "z-columns" : this._zclass;
	}
});

(_zkwg=_zkpk.Columns).prototype.className='zul.grid.Columns';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<tr', this.domAttrs_(), ' align="left">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</tr>');
}
zkmld(_zkwg,_zkmd);
zul.grid.Grid = zk.$extends(zul.Widget, {
	_pagingPosition: "bottom",
	_preloadsz: 7,
	/** ROD Mold */
	_innerWidth: "100%",
	_innerTop: "height:0px;display:none",
	_innerBottom: "height:0px;display:none",
	
	isVflex: function () {
		return this._vflex;
	},
	setVflex: function (vflex) {
		if (this._vflex != vflex) {
			this._vflex = vflex;
			var n = this.getNode();
			if (n) {
				if (vflex) {
					// added by Jumper for IE to get a correct offsetHeight so we need 
					// to add this command faster than the this._calcSize() function.
					var hgh = n.style.height;
					if (!hgh || hgh == "auto") n.style.height = "99%"; // avoid border 1px;
				}
				this.onSize();
			}
		}
	},
	setFixedLayout: function (fixedLayout) {
		if(this._fixedLayout != fixedLayout) {
			this._fixedLayout = fixedLayout;
			this.rerender();
		}
	},
	isFixedLayout: function () {
		return this._fixedLayout;
	},
	getHeads: function () {
		var heads = [];
		for (var w = this.firstChild; w; w = w.nextSibling) {
			if (w.$instanceof(zul.grid.Auxhead) || w.$instanceof(zul.grid.Columns))
				heads.push(w);
		}
		return heads;
	},
	getCell: function (row, col) {
		if (!this.rows) return null;
		if (rows.nChildren <= row) return null;

		var row = rows.getChildAt(row);
		return row.nChildren <= col ? null: row.getChildAt(col);
	},
	getAlign: function () {
		return this._align;
	},
	setAlign: function (align) {
		if (this._align != align) {
			this._align = align;
			var n = this.getNode();
			if (n) n.align = align;
		}
	},
	setPagingPosition: function (pagingPosition) {
		if(this._pagingPosition != pagingPosition) {
			this._pagingPosition = pagingPosition;
			this.rerender();
		}
	},
	getPagingPosition: function () {
		return this._pagingPosition;
	},
	getPageSize: function () {
		return this.paging.getPageSize();
	},
	setPageSize: function (pgsz) {
		this.paging.setPageSize(pgsz);
	},
	getPageCount: function () {
		return this.paging.getPageCount();
	},
	getActivePage: function () {
		return this.paging.getActivePage();
	},
	setActivePage: function (pg) {
		this.paging.setActivePage(pg);
	},
	_inPagingMold: function () {
		return "paging" == this.getMold();
	},
	setInnerHeight: function (innerHeight) {
		if (innerHeight == null) innerHeight = "100%";
		if (this._innerHeight != innerHeight) {
			this._innerHeight = innerHeight;
			// TODO for ROD Mold
		}
	},
	getInnerHeight: function () {
		return this._innerHeight;
	},
	setInnerTop: function (innerTop) {
		if (innerTop == null) innerTop = "height:0px;display:none";
		if (this._innerTop != innerTop) {
			this._innerTop = innerTop;
			// TODO for ROD Mold
		}
	},
	getInnerTop: function () {
		return this._innerTop;
	},
	setInnerBottom: function (innerBottom) {
		if (innerBottom == null) innerBottom = "height:0px;display:none";
		if (this._innerBottom != innerBottom) {
			this._innerBottom = innerBottom;
			// TODO for ROD Mold
		}
	},
	getInnerBottom: function () {
		return this._innerBottom;
	},
	isModel: function () {
		return this._model;
	},
	setModel: function (model) {
		if (this._model != model) {
			this._model = model;
		}
	},
	getPreloadSize: function () {
		return this._preloadsz;
	},
	setPreloadSize: function (sz) {
		if (sz >= 0)
			this._preloadsz = sz;
	},
	setInnerWidth: function (innerWidth) {
		if (innerWidth == null) innerWidth = "100%";
		if (this._innerWidth != innerWidth) {
			this._innerWidth = innerWidth;
			if (this.eheadtbl) this.eheadtbl.style.width = innerWidth;
			if (this.ebodytbl) this.ebodytbl.style.width = innerWidth;
			if (this.efoottbl) this.efoottbl.style.width = innerWidth;
		}
	},
	getInnerWidth: function () {
		return this._innerWidth;
	},
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		var n = this.getNode();
		if (n) {
			if (zk.ie6Only && this.ebody) 
				this.ebody.style.height = height;
			// IE6 cannot shrink its height, we have to specify this.body's height to equal the element's height. 
			this._setHgh(height);
			this.onSize();
		}
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.eheadtbl) this.eheadtbl.style.width = "";
		if (this.efoottbl) this.efoottbl.style.width = "";
	},
	setStyle: function (style) {
		if (this._style != style) {
			this.$supers('setStyle', arguments);
			this.onSize();
		}
	},
	getOddRowSclass: function () {
		return this._scOddRow == null ? this.getZclass() + "-odd" : this._scOddRow;
	},
	setOddRowSclass: function (scls) {
		if (!scls) scls = null;
		if (this._scOddRow != scls) {
			this._scOddRow = scls;
			var n = this.getNode();
			if (n && this.rows)
				this.rows.stripe();
		}
	},
	getZclass: function () {
		return this._zclass == null ? "z-grid" : this._zclass;
	},
	//-- super --//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.grid.Rows))
			this.rows = child;
		else if (child.$instanceof(zul.grid.Columns))
			this.columns = child;
		else if (child.$instanceof(zul.grid.Foot))
			this.foot = child;			
		else if (child.$instanceof(zul.grid.Paging))
			this.paging = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.rows)
			this.rows = null;
		else if (child == this.columns)
			this.columns = null;
		else if (child == this.foot)
			this.foot = null;			
		else if (child == this.paging)
			this.paging = null;
	},
	insertChildHTML_: function (child, before, desktop) {
		if (child.$instanceof(zul.grid.Rows)) {
			this.rows = child;
			if (this.ebodytbl) {
				zDom.insertHTMLBeforeEnd(this.ebodytbl, child._redrawHTML());
				child.bind_(desktop);
				return;
			}
		} 
		
		this.rerender();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.isVflex()) {
			// added by Jumper for IE to get a correct offsetHeight so we need 
			// to add this command faster than the this._calcSize() function.
			var hgh = this.getNode().style.height;
			if (!hgh || hgh == "auto") this.getNode().style.height = "99%"; // avoid border 1px;
		}
		this._bindDomNode();
		this._fixHeaders();
		if (this.ebody) {
			zEvt.listen(this.ebody, 'scroll', this.proxy(this.doScroll_, '_pxDoScroll'));
			this.ebody.style.overflow = ''; // clear
		}
		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
		zWatch.listen("beforeSize", this);
	},
	unbind_: function () {
		if (this.ebody)
			zEvt.unlisten(this.ebody, 'scroll', this._pxDoScroll);
			
		this.ebody = this.ehead = this.efoot = this.ebodytbl
			= this.eheadtbl = this.efoottbl = null;
		
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);
		zWatch.unlisten("beforeSize", this);
		
		this.$supers('unbind_', arguments);
	},
	_fixHeaders: function () {
		if (this.columns && this.ehead) {
			var empty = true;
			for (var w = this.columns.firstChild; w; w = w.nextSibling) 
				if (w.getLabel() || w.getImage()) {
					empty = false;
					break;
				}
			this.ehead.style.display = empty ? 'none' : '';
		}
	},
	_bindDomNode: function () {
		for (var n = this.getNode().firstChild; n; n = n.nextSibling)
			switch(n.id) {
			case this.uuid + '$head':
				this.ehead = n;
				this.eheadtbl = zDom.firstChild(n, 'TABLE');
				break;
			case this.uuid + '$body':
				this.ebody = n;
				this.ebodytbl = zDom.firstChild(n, 'TABLE');
				break;
			case this.uuid + '$foot':
				this.efoot = n;
				this.efoottbl = zDom.firstChild(n, 'TABLE');
				break;
			}
		if (this.ehead) {
			this.ehdfaker = this.eheadtbl.tBodies[0].rows[0];
			this.ebdfaker = this.ebodytbl.tBodies[0].rows[0];
			if (this.efoottbl)
				this.eftfaker = this.efoottbl.tBodies[0].rows[0];
		}
	},
	fireScrollRender: function (timeout) {
		setTimeout(this.proxy(this.onScrollRender), timeout ? timeout : 100);
	},
	doScroll_: function () {
		if (this.ehead)
			this.ehead.scrollLeft = this.ebody.scrollLeft;
		if (this.efoot)
			this.efoot.scrollLeft = this.ebody.scrollLeft;
		if (!this.paging) this.fireScrollRender(zk.gecko ? 200 : 60);
	},
	onScrollRender: function () {
		if (!this.isModel() || !this.rows || !this.rows.nChildren) return;

		//Note: we have to calculate from top to bottom because each row's
		//height might diff (due to different content)
		var data = "";
		var min = this.ebody.scrollTop, max = min + this.ebody.offsetHeight;
		for (var rows = this.rows.getNode().rows, j = 0, rl = rows.length; j < rl; ++j) {
			var r = rows[j];
			if (zDom.isVisible(r)) {
				var top = zDom.offsetTop(r);
				if (top + zDom.offsetHeight(r) < min) continue;
				if (top > max) break; //Bug 1822517
				if (!zk.Widget.$(r)._loaded)
					data += "," + r.id;
			}
		}
		if (data) {
			data = data.substring(1);
			this.fire('onRender', data);
		}
	},
	//watch//
	beforeSize: function () {
		// IE6 needs to reset the width of each sub node if the width is a percentage
		var wd = zk.ie6Only ? this.getWidth() : this.getNode().style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			if (this.ebody) this.ebody.style.width = "";
			if (this.ehead) this.ehead.style.width = "";
			if (this.efoot) this.efoot.style.width = "";
		}
	},
	onSize: _zkf = function () {
		if (this.isRealVisible()) {
			var n = this.getNode();
			if (n._lastsz && n._lastsz.height == n.offsetHeight && n._lastsz.width == n.offsetWidth)
				return; // unchanged
				
			this._calcSize();// Bug #1813722
			this.fireScrollRender(155);
			if (zk.ie7) zDom.redoCSS(this.getNode()); // Bug 2096807
		}
	},
	onVisible: _zkf,
	onRender: function (evt) {
		var d = evt.data.marshal();
		this._curpos = d[0];
		this._visicnt = d[1];
		if (this.columns)
			this.setInnerWidth(d[2]);
			
		this.setInnerHeight(d[3]);
		this._onRender();
		evt.stop();
	},
	_vflexSize: function (hgh) {
		var n = this.getNode();
		if (zk.ie6Only) { 
			// ie6 must reset the height of the element,
			// otherwise its offsetHeight might be wrong.
			n.style.height = "";
			n.style.height = hgh;
		}
		return n.offsetHeight - 2 - (this.ehead ? this.ehead.offsetHeight : 0)
			- (this.efoot ? this.efoot.offsetHeight : 0); // Bug #1815882 and Bug #1835369
	},
	/* set the height. */
	_setHgh: function (hgh) {
		if (this.isVflex() || (hgh && hgh != "auto" && hgh.indexOf('%') < 0)) {
			var h =  this._vflexSize(hgh); 
			if (this.paging) {
				/** TODO 
				 * var pgit = $e(this.id + "!pgit"), pgib = $e(this.id + "!pgib");
				if (pgit) h -= pgit.offsetHeight;
				if (pgib) h -= pgib.offsetHeight;*/
			}
			if (h < 0) h = 0;

			this.ebody.style.height = h + "px";
			
			//2007/12/20 We don't need to invoke the body.offsetHeight to avoid a performance issue for FF. 
			if (zk.ie && this.ebody.offsetHeight) {} // bug #1812001.
			// note: we have to invoke the body.offestHeight to resolve the scrollbar disappearing in IE6 
			// and IE7 at initializing phase.
		} else {
			//Bug 1556099: it is strange if we ever check the value of
			//body.offsetWidth. The grid's body's height is 0 if init called
			//after grid become visible (due to opening an accordion tab)
			this.ebody.style.height = "";
			this.getNode().style.height = hgh;
		}
	},
	/** Calculates the size. */
	_calcSize: function () {
		var n = this.getNode();
		this._setHgh(n.style.height);
		//Bug 1553937: wrong sibling location
		//Otherwise,
		//IE: element's width will be extended to fit body
		//FF and IE: sometime a horizontal scrollbar appear (though it shalln't)
		//note: we don't solve this bug for paging yet
		var wd = n.style.width;
		if (!wd || wd == "auto" || wd.indexOf('%') >= 0) {
			wd = zDom.revisedWidth(n, n.offsetWidth);
			if (wd < 0) wd = 0;
			if (wd) wd += "px";
		}
		if (wd) {
			this.ebody.style.width = wd;
			if (this.ehead) this.ehead.style.width = wd;
			if (this.efoot) this.efoot.style.width = wd;
		}
		//Bug 1659601: we cannot do it in init(); or, IE failed!
		var tblwd = this.ebody.clientWidth;
		if (zk.ie) //By experimental: see zk-blog.txt
			if (this.eheadtbl && this.eheadtbl.offsetWidth
					!= this.ebodytbl.offsetWidth)
				this.ebodytbl.style.width = ""; //reset 
			if (tblwd && this.ebody.offsetWidth == this.ebodytbl.offsetWidth
					&& this.ebody.offsetWidth - tblwd > 11) { //scrollbar
				if (--tblwd < 0) tblwd = 0;
				this.ebodytbl.style.width = tblwd + "px";
			}
				
		if (this.ehead) {
			if (tblwd) this.ehead.style.width = tblwd + 'px';
			if (!this.isFixedLayout() && this.rows)
				this.$class.adjustHeadWidth(this);
		} else if (this.efoot) {
			if (tblwd) this.efoot.style.width = tblwd + 'px';
			if (this.efoottbl.rows.length && this.rows)
				this.$class.cpCellWidth(this);
		}
		n._lastsz = {height: n.offsetHeight, width: n.offsetWidth}; // cache for the dirty resizing.
	},
	domFaker_: function (out, fakeId, zcls) {
		out.push('<tbody style="visibility:hidden;height:0px"><tr id="',
				this.columns.uuid, fakeId, '" class="', zcls, '-faker">');
		for (var w = this.columns.firstChild; w; w = w.nextSibling)
			out.push('<th id="', w.uuid, fakeId, '"', w.domAttrs_(),
				 	'><div style="overflow:hidden"></div></th>');
		out.push('</tr></tbody>');
	}
}, {
	adjustHeadWidth: function (wgt) {
		// function (hdfaker, bdfaker, ftfaker, rows) {
		var hdfaker = wgt.ehdfaker,
			bdfaker = wgt.ebdfaker,
			ftfaker = wgt.eftfaker,
			rows = wgt.rows.getNode().rows;
		if (!hdfaker || !bdfaker || !hdfaker.cells.length
		|| !bdfaker.cells.length || !zDom.isRealVisible(hdfaker) || !rows.length) return;
		
		var hdtable = wgt.ehead.firstChild, head = wgt.columns.getNode();
		if (!head) return; 
		if (zk.opera) {
			if (!hdtable.style.width) {
				var isFixed = true, tt = wgt.ehead.offsetWidth;
				for(var i = hdfaker.cells.length; --i >=0;) {
					if (!hdfaker.cells[i].style.width || hdfaker.cells[i].style.width.indexOf("%") >= 0) {
						isFixed = false; 
						break;
					}
					tt -= zk.parseInt(hdfaker.cells[i].style.width);
				}
				if (!isFixed || tt >= 0) hdtable.style.tableLayout = "auto";
			}
		}
		
		// Bug #1886788 the size of these table must be specified a fixed size.
		var bdtable = wgt.ebody.firstChild,
			total = Math.max(hdtable.offsetWidth, bdtable.offsetWidth), 
			tblwd = Math.min(bdtable.parentNode.clientWidth, bdtable.offsetWidth);
			
		if (total == wgt.ebody.offsetWidth && 
			wgt.ebody.offsetWidth > tblwd && wgt.ebody.offsetWidth - tblwd < 20)
			total = tblwd;
			
		var count = total;
		hdtable.style.width = total + "px";	
		
		if (bdtable) bdtable.style.width = hdtable.style.width;
		if (wgt.efoot) wgt.efoot.firstChild.style.width = hdtable.style.width;
		
		for (var i = bdfaker.cells.length; --i >= 0;) {
			if (!zDom.isVisible(hdfaker.cells[i])) continue;
			var wd = i != 0 ? bdfaker.cells[i].offsetWidth : count;
			bdfaker.cells[i].style.width = zDom.revisedWidth(bdfaker.cells[i], wd) + "px";
			hdfaker.cells[i].style.width = bdfaker.cells[i].style.width;
			if (ftfaker) ftfaker.cells[i].style.width = bdfaker.cells[i].style.width;
			var cpwd = zDom.revisedWidth(head.cells[i], zk.parseInt(hdfaker.cells[i].style.width));
			head.cells[i].style.width = cpwd + "px";
			var cell = head.cells[i].firstChild;
			cell.style.width = zDom.revisedWidth(cell, cpwd) + "px";
			count -= wd;
		}
		
		// in some case, the total width of this table may be changed.
		if (total != hdtable.offsetWidth) {
			total = hdtable.offsetWidth;
			tblwd = Math.min(wgt.ebody.clientWidth, bdtable.offsetWidth);
			if (total == wgt.ebody.offsetWidth && 
				wgt.ebody.offsetWidth > tblwd && wgt.ebody.offsetWidth - tblwd < 20)
				total = tblwd;
				
			hdtable.style.width = total + "px";	
			if (bdtable) bdtable.style.width = hdtable.style.width;
			if (wgt.efoot) wgt.efoot.firstChild.style.width = hdtable.style.width;
		}
	},
	cpCellWidth: function (wgt) {
		var dst = wgt.efoot.firstChild.rows[0],
			srcrows = wgt.rows.getNode().rows;
		if (!dst || !srcrows.length || !dst.cells.length)
			return;
		var ncols = dst.cells.length, //TODO: handle colspan for dst: ncols = zk.ncols(dst.cells);
			src, maxnc = 0, loadIdx = wgt._lastLoadIdx;
		for (var j = 0, len = loadIdx || srcrows.length; j < len; ++j) {
			var row = srcrows[j];
			if (!zDom.isVisible(row) || !zk.Widget.$(row)._loaded) continue;
			var cells = row.cells, nc = zDom.ncols(cells),
				valid = cells.length == nc && zDom.isVisible(row);
				//skip with colspan and invisible
			if (valid && nc >= ncols) {
				maxnc = ncols;
				src = row;
				break;
			}
			if (nc > maxnc) {
				src = valid ? row: null;
				maxnc = nc;
			} else if (nc == maxnc && !src && valid) {
				src = row;
			}
		}
		if (!maxnc) return;
	
		var fakeRow = !src;
		if (fakeRow) { //the longest row containing colspan
			src = document.createElement("TR");
			src.style.height = "0px";
				//Note: we cannot use display="none" (offsetWidth won't be right)
			for (var j = 0; j < maxnc; ++j)
				src.appendChild(document.createElement("TD"));
			srcrows[0].parentNode.appendChild(src);
		}
	
		//we have to clean up first, since, in FF, if dst contains %
		//the copy might not be correct
		for (var j = maxnc; --j >=0;)
			dst.cells[j].style.width = "";
	
		var sum = 0;
		for (var j = maxnc; --j >= 0;) {
			var d = dst.cells[j], s = src.cells[j];
			if (zk.opera) {
				sum += s.offsetWidth;
				d.style.width = zDom.revisedWidth(s, s.offsetWidth);
			} else {
				d.style.width = s.offsetWidth + "px";
				if (maxnc > 1) { //don't handle single cell case (bug 1729739)
					var v = s.offsetWidth - d.offsetWidth;
					if (v != 0) {
						v += s.offsetWidth;
						if (v < 0) v = 0;
						d.style.width = v + "px";
					}
				}
			}
		}
	
		if (zk.opera && !wgt.isFixedLayout())
			dst.parentNode.parentNode.style.width = sum + "px";
	
		if (fakeRow)
			src.parentNode.removeChild(src);
	}
});

(_zkwg=_zkpk.Grid).prototype.className='zul.grid.Grid';_zkmd={};
_zkmd['paging']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		innerWidth = this.getInnerWidth(),
		width = innerWidth == '100%' ? ' width="100%"' : '',
		width1 =  innerWidth != '100%' ? 'width:' + innerWidth : '',
		inPaging = this._inPagingMold();
	out.push('<div', this.domAttrs_(), (this.getAlign() ? ' align="' + this.getAlign() + '"' : ''), '>');
	
	if (inPaging && this.paging
			&& (this.getPagingPosition() == 'top' || this.getPagingPosition() == 'both')) {
		out.push('<div id="', uuid, '$pgit" class="', zcls, '-pgi-t">');
		this.paging.redraw(out);
		out.push('</div>');
	}
	
	if (this.columns) {
		out.push('<div id="', uuid, '$head" class="', zcls, '-header">',
				'<table', width, zUtl.cellps0,
				' style="table-layout:fixed;', width1,'">');
		this.domFaker_(out, '$hdfaker', zcls);
		
		for (var hds = this.getHeads(), w = hds.shift(); w; w = hds.shift())
			w.redraw(out);
	
		out.push('</table></div>');
	}
	out.push('<div id="', uuid, '$body" class="', zcls, '-body"');
	
	var hgh = this.getHeight();
	if (hgh) out.push(' style="height:', hgh, '"');
	
	out.push('><table', width, zUtl.cellps0);
	
	if (this.isFixedLayout())
		out.push(' style="table-layout:fixed;', width1,'"');
		
	out.push('>');
	
	if (this.columns)
		this.domFaker_(out, '$bdfaker', zcls);
	
	if (this.rows) this.rows.redraw(out);
	
	out.push('</table></div>');
	
	if (this.foot) {
		out.push('<div id="', uuid, '$foot" class="', zcls, '-footer">',
				'<table', width, zUtl.cellps0, ' style="table-layout:fixed;', width1,'">');
		if (this.columns) 
			this.domFaker_(out, '$ftfaker', zcls);
			
		this.foot.redraw(out);
		out.push('</table></div>');
	}
	if (inPaging && this.paging
			&& (this.getPagingPosition() == 'bottom' || this.getPagingPosition() == 'both')) {
		out.push('<div id="', uuid, '$pgib" class="', zcls, '-pgi-b">');
		this.paging.redraw(out);
		out.push('</div>');
	}
	out.push('</div>');
}

_zkmd['default']=[_zkpk.Grid,'paging'];zkmld(_zkwg,_zkmd);
zul.grid.Row = zk.$extends(zul.Widget, {
	getGrid: function () {
		return this.parent ? this.parent.parent : null;
	},
	getAlign: function () {
		return this._align;
	},
	setAlign: function (align) {
		if (this._align != align) {
			this._align = align;
			var n = this.getNode();
			if (n)
				n.align = align;
		}
	},
	isNowrap: function () {
		return this._nowrap;
	},
	setNowrap: function (nowrap) {
		if (this._nowrap != nowrap) {
			this._nowrap = nowrap;
			var n = this.getNode();
			if (n)
				n.noWrap = nowrap;
		}
	},
	getValign: function () {
		return this._valign;
	},
	setValign: function (valign) {
		if (this._valign != valign) {
			this._valign = valign;
			var n = this.getNode();
			if (n)
				n.vAlign = valign;
		}
	},
	setVisible: function (visible) {
		if (this.isVisible() != visible) {
			// TODO: for rows.getGroup
			/**final Rows rows = (Rows) getParent();
			if (rows != null) {
				final Group g = rows.getGroup(getIndex());
				if (g == null || g.isOpen())
					rows.addVisibleItemCount(visible ? 1 : -1);
			}*/
			
			this.$supers('setVisible', arguments);
			if (this.isStripeable_() && this.parent)
				this.parent.stripe();
		}
	},
	getSpans: function () {
		return zUtl.intsToString(this._spans);
	},
	setSpans: function (spans) {
		if (this.getSpans() != spans) {
			this._spans = zUtl.stringToInts(spans, 1);
			this.rerender();
		}
	},
	_getIndex: function () {
		return this.parent ? this.getChildIndex() : -1;
	},
	getZclass: function () {
		return this._zclass != null ? this._zclass : "z-row";
	},
	getGroup: function () {
		// TODO: for group
		/**
		if (this instanceof Group) return (Group)this;
		final Rows rows = (Rows) getParent();
		return (rows != null) ? rows.getGroup(getIndex()) : null;
		*/
	},
	setStyle: function (style) {
		if (this._style != style) {
			if (!zk._rowTime) zk._rowTime = zUtl.now();
			this._style = style;
			this.rerender();
		}
	},
	getSclass: function () {
		var sclass = this.$supers('getSclass', arguments);
		if (sclass != null) return sclass;

		var grid = this.getGrid();
		return grid ? grid.getSclass(): sclass;
	},
	insertChildHTML_: function (child, before, desktop) {
		var cls = this.getGrid().isFixedLayout() ? 'z-overflow-hidden' : '';
		if (before) {
			zDom.insertHTMLBefore(before.getSubnode('chdextr'),
				this.encloseChildHTML_({child: child, index: child.getChildIndex(),
						zclass: this.getZclass(), cls: cls}));
		} else
			zDom.insertHTMLBeforeEnd(this.getNode(),
				this.encloseChildHTML_({child: child, index: child.getChildIndex(),
						zclass: this.getZclass(), cls: cls}));
		
		child.bind_(desktop);
	},
	removeChildHTML_: function (child, prevsib) {
		this.$supers('removeChildHTML_', arguments);
		zDom.remove(child.uuid + '$chdextr');
	},
	encloseChildHTML_: function (opts) {
		var out = opts.out || [],
			child = opts.child;
		out.push('<td id="', child.uuid, '$chdextr"', this._childAttrs(child, opts.index),
				'>', '<div id="', child.uuid, '$cell" class="', opts.zclass, '-cnt ',
				opts.cls, '">');
		child.redraw(out);
		out.push('</div></td>');
		if (!opts.out) return out.join('');
	},
	_childAttrs: function (child, index) {
		var realIndex = index, span = 1;
		if (this._spans) {
			for (var j = 0, k = this._spans.length; j < k; ++j) {
				if (j == index) {
					span = this._spans[j];
					break;
				}
				realIndex += this._spans[j] - 1;
			}
		}

		var colattrs, visible, hgh,
			grid = this.getGrid();
		
		if (grid) {
			var cols = grid.columns;
			if (cols) {
				if (realIndex < cols.nChildren) {
					var col = cols.getChildAt(realIndex);
					colattrs = col.getColAttrs();
					visible = col.isVisible() ? '' : 'display:none';
					hgh = col.getHeight();
				}
			}
		}

		var style = this.domStyle_({visible:1, width:1, height:1}),
			isDetail = child.$instanceof(zul.grid.Detail);
		if (isDetail) {
			var wd = child.getWidth();
			if (wd) 
				style += "width:" + wd + ";";
		}

		if (visible || hgh) {
			style += visible;
			if (hgh)
				style += 'height:' + hgh + ';';
		}
		
		var clx = isDetail ? child.getZclass() + "-outer" : this.getZclass() + "-inner";
		
		if (!colattrs && !style && span === 1)
			return ' class="' + clx + '"';

		var attrs = colattrs ? colattrs : '';
		
		if (span !== 1)
			attrs += ' colspan="' + span + '"';
		return attrs + ' style="' + style + '"' + ' class="' + clx + '"';
	},
	isStripeable_: function () {
		return true;
	},
	//-- super --//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.grid.Detail))
			this.detail = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.detail)
			this.detail = null;
	}
});
/** // TODO for drag and drop
 * if (zk.gecko) {
	zul.grid.Row.prototype.doMouseOver_ = function (wevt, evt) {
		var target = this._getDirectChildByElement(zEvt.target(evt), this.getNode());
		if (target)
			target.firstChild.style.MozUserSelect = "none";
		this.$supers('doMouseOver_', arguments);
	};
	zul.grid.Row.prototype.doMouseOut_ = function (wevt, evt) {
		var target = this._getDirectChildByElement(zEvt.target(evt), this.getNode());
		if (target)
			target.firstChild.style.MozUserSelect = "";
		this.$supers('doMouseOut_', arguments);
	};
	zul.grid.Row.prototype._getDirectChildByElement = function (el, parent) {
		for (;el; el = el.parentNode)
			if (el.parentNode == parent) return el;
		return null;
	};
}*/
(_zkwg=_zkpk.Row).prototype.className='zul.grid.Row';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<tr', this.domAttrs_(), '>');
	var	zcls = this.getZclass(),
		overflow = this.getGrid().isFixedLayout() ? 'z-overflow-hidden' : '' ;
	for (var j = 0, w = this.firstChild; w; w = w.nextSibling, j++)
		this.encloseChildHTML_({child:w, index: j, zclass: zcls, cls: overflow, out: out});
	out.push('</tr>');	
}

zkmld(_zkwg,_zkmd);
zul.grid.Rows = zk.$extends(zul.Widget, {
	_visibleItemCount: 0,
	$init: function () {
		this.$supers('$init', arguments);
		this._groupsInfo = [];
		this._groups = [];
	},
	getGrid: function () {
		return this.parent;
	},
	getGroupCount: function () {
		return this._groupsInfo.length;
	},
	getGroups: function () {
		return this._groups;
	},
	hasGroup: function () {
		return this._groupsInfo.length != 0;
	},
	getZclass: function () {
		return this._zclass == null ? "z-rows" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen('onResponse', this);
		zk.afterMount(this.proxy(this.onResponse));
	},
	unbind_: function () {
		zWatch.unlisten('onResponse', this);
		this.$supers('unbind_', arguments);
	},
	onResponse: function () {
		if (this._shallStripe) {
			this.stripe();
			this.getGrid().onSize();
		}
	},
	_syncStripe: function () {
		this._shallStripe = true;
		if (!this.inServer && this.desktop)
			this.onResponse();
	},
	stripe: function () {
		var scOdd = this.getGrid().getOddRowSclass();
		if (!scOdd) return;
		var n = this.getNode();
		for (var j = 0, w = this.firstChild, even = true; w; w = w.nextSibling, ++j) {
			if (w.isVisible() && w.isStripeable_()) {
				zDom[even ? 'rmClass' : 'addClass'](n.rows[j], scOdd);
				w.fire("onStripe");
				even = !even;
			}
		}
		this._shallStripe = false;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		this._syncStripe();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		this._syncStripe();
	},
	//Paging//
	getVisibleItemCount: function () {
		return this._visibleItemCount;
	},
	_addVisibleItemCount: function (count) {
		if (count) {
			this._visibleItemCount += count;
			if (this.parent != null && this.parent.inPagingMold()) {
				var pgi = this.parent.getPaginal();
				pgi.setTotalSize(this._visibleItemCount);
				// TODO invalidate(); // the set of visible items might change
			}
		}
	},
	_fixGroupIndex: function (j, to, infront) {
		//TODO:
		/**for (Iterator it = getChildren().listIterator(j);
		it.hasNext() && (to < 0 || j <= to); ++j) {
			Object o = it.next();
			if (o instanceof Group) {
				int[] g = getLastGroupsInfoAt(j + (infront ? -1 : 1));
				if (g != null) {
					g[0] = j;
					if (g[2] != -1) g[2] += (infront ? 1 : -1);
				}
			}
		}*/
	},
	getGroup: function (index) {
		if (!this._groupsInfo.length) return null;
		var g = this._getGroupsInfoAt(index);
		if (g != null) return ; // TODO (Group)getChildren().get(g[0]);
		return null;
	},
	/**
	 * Returns the last groups info which matches with the same index.
	 * Because dynamically maintain the index of the groups will occur the same index
	 * at the same time in the loop. 
	 */
	_getLastGroupsInfoAt: function (index) {
		/**
		int [] rg = null;
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[])it.next();
			if (index == g[0]) rg = g;
			else if (index < g[0]) break;
		}
		return rg;*/
	},
	/**
	 * Returns an int array that it has two length, one is an index of Group,
	 * and the other is the number of items of Group(inclusive).
	 */
	_getGroupsInfoAt: function (index, isGroup) {
		// TODO:
		/**
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[])it.next();
			if (isGroup) {
				if (index == g[0]) return g;
			} else if ((index > g[0] && index <= g[0] + g[1]))
				return g;
		}
		return null;*/
	}
	//-- super --//
	/** TODO
	 * public boolean insertBefore(Component child, Component refChild) {
		if (!(child instanceof Row))
			throw new UiException("Unsupported child for rows: "+child);
		Row newItem = (Row) child;
		final int jfrom = hasGroup() && newItem.getParent() == this ? newItem.getIndex(): -1;	

		final boolean isReorder = child.getParent() == this;
		if (newItem instanceof Groupfoot){
			if (!hasGroup())
				throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");
			if (refChild == null) {
				if (getLastChild() instanceof Groupfoot)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (isReorder) {
					final int idx = newItem.getIndex();				
					final int[] ginfo = getGroupsInfoAt(idx);
					if (ginfo != null) {
						ginfo[1]--; 
						ginfo[2] = -1;
					}
				}
				final int[] g = (int[]) _groupsInfo.get(getGroupCount()-1);
				g[2] = getChildren().size() - (isReorder ? 2 : 1);
			} else {
				final int idx = ((Row)refChild).getIndex();				
				final int[] g = getGroupsInfoAt(idx);
				if (g == null)
					throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");				
				if (g[2] != -1)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (idx != (g[0] + g[1]))
					throw new UiException("Groupfoot must be placed after the last Row of the Group");
				g[2] = idx-1;
				if (isReorder) {
					final int nindex = newItem.getIndex();				
					final int[] ginfo = getGroupsInfoAt(nindex);
					if (ginfo != null) {
						ginfo[1]--; 
						ginfo[2] = -1;
					}
				}
			}							
		}
		if (super.insertBefore(child, refChild)) {
			if(hasGroup()) {
				final int
					jto = refChild instanceof Row ? ((Row)refChild).getIndex(): -1,
					fixFrom = jfrom < 0 || (jto >= 0 && jfrom > jto) ? jto: jfrom;
				if (fixFrom >= 0) fixGroupIndex(fixFrom,
					jfrom >=0 && jto >= 0 ? jfrom > jto ? jfrom: jto: -1, !isReorder);
			}
			if (newItem instanceof Group) {
				Group group = (Group) newItem;
				int index = group.getIndex();
				if (_groupsInfo.isEmpty())
					_groupsInfo.add(new int[]{group.getIndex(), getChildren().size() - index, -1});
				else {
					int idx = 0;
					int[] prev = null, next = null;
					for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
						int[] g = (int[])it.next();
						if(g[0] <= index) {
							prev = g;
							idx++;
						} else {
							next = g;
							break;
						}
					}
					if (prev != null) {
						int leng = index - prev[0], 
							size = prev[1] - leng + 1;
						prev[1] = leng;
						_groupsInfo.add(idx, new int[]{index, size, size > 1 ? prev[2] : -1});
						if (size > 1) prev[2] = -1; // reset groupfoot
					} else if (next != null) {
						_groupsInfo.add(idx, new int[]{index, next[0] - index, -1});
					}
				}
			} else if (hasGroup()) {
				int index = newItem.getIndex();
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]++;
					if (g[2] != -1) g[2]++;
				}
				
			}
			
			afterInsert(child);
			return true;
		}
		return false;
	}*/
	/**
	 * If the child is a group, its groupfoot will be removed at the same time.
	 */
	/** TODO
	 * public boolean removeChild(Component child) {
		if (child.getParent() == this)
			beforeRemove(child);
		int index = hasGroup() ? ((Row)child).getIndex() : -1;
		if(super.removeChild(child)) {
			if (child instanceof Group) {
				int[] prev = null, remove = null;
				for(Iterator it = _groupsInfo.iterator(); it.hasNext();) {
					int[] g = (int[])it.next();
					if (g[0] == index) {
						remove = g;
						break;
					}
					prev = g;
				}
				if (prev != null && remove !=null) {
					prev[1] += remove[1] - 1;
				}
				fixGroupIndex(index, -1, false);
				if (remove != null) {
					_groupsInfo.remove(remove);
					final int idx = remove[2];
					if (idx != -1) {
						removeChild((Component) getChildren().get(idx -1));
							// Because the fixGroupIndex will skip the first groupinfo,
							// we need to subtract 1 from the idx variable
					}
				}
			} else if (hasGroup()) {
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]--;
					if (g[2] != -1) g[2]--;
					fixGroupIndex(index, -1, false);
				}
				else fixGroupIndex(index, -1, false);
				if (child instanceof Groupfoot){
					final int[] g1 = getGroupsInfoAt(index);	
					if(g1 != null){ // group info maybe remove cause of grouphead removed in previous op
						g1[2] = -1;
					}
				}
			}
			return true;
		}
		return false;
	}*/
	/** Callback if a child has been inserted.
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	/**protected void afterInsert(Component comp) {
		updateVisibleCount((Row) comp, false);
		checkInvalidateForMoved(comp, false);
	}*/
	/** Callback if a child will be removed (not removed yet).
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	/**protected void beforeRemove(Component comp) {
		updateVisibleCount((Row) comp, true);
		checkInvalidateForMoved(comp, true);
	}*/
	/**
	 * Update the number of the visible item before it is removed or after it is added.
	 */
	/**private void updateVisibleCount(Row row, boolean isRemove) {
		if (row instanceof Group || row.isVisible()) {
			final Group g = getGroup(row.getIndex());
			
			// We shall update the number of the visible item in the following cases.
			// 1) If the row is a type of Groupfoot, it is always shown.
			// 2) If the row is a type of Group, it is always shown.
			// 3) If the row doesn't belong to any group.
			// 4) If the group of the row is open.
			if (row instanceof Groupfoot || row instanceof Group || g == null || g.isOpen())
				addVisibleItemCount(isRemove ? -1 : 1);
			
			if (row instanceof Group) {
				final Group group = (Group) row;
				
				// If the previous group exists, we shall update the number of
				// the visible item from the number of the visible item of the current group.
				final Row preRow = (Row) row.getPreviousSibling();
				if (preRow == null) {
					if (!group.isOpen()) {
						addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					}
				} else {
					final Group preGroup = preRow instanceof Group ? (Group) preRow : getGroup(preRow.getIndex());
					if (preGroup != null) {
						if (!preGroup.isOpen() && group.isOpen())
							addVisibleItemCount(isRemove ? -group.getVisibleItemCount() : group.getVisibleItemCount());
						else if (preGroup.isOpen() && !group.isOpen())
							addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					} else {
						if (!group.isOpen())
							addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					}
				}
			}
		}
		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold())
			grid.getPaginal().setTotalSize(getVisibleItemCount());
	}*/
	/** Checks whether to invalidate, when a child has been added or 
	 * or will be removed.
	 * @param bRemove if child will be removed
	 */
	/**private void checkInvalidateForMoved(Component child, boolean bRemove) {
		//No need to invalidate if
		//1) act == last and child in act
		//2) act != last and child after act
		//Except removing last elem which in act and act has only one elem
		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold() && !isInvalidated()) {
			final List children = getChildren();
			final int sz = children.size(),
				pgsz = grid.getPageSize();
			int n = sz - (grid.getActivePage() + 1) * pgsz;
			if (n <= 0) {//must be last page
				n += pgsz; //check in-act (otherwise, check after-act)
				if (bRemove && n <= 1) { //last elem, in act and remove
					invalidate();
					return;
				}
			} else if (n > 50)
				n = 50; //check at most 50 items (for better perf)

			for (ListIterator it = children.listIterator(sz);
			--n >= 0 && it.hasPrevious();)
				if (it.previous() == child)
					return; //no need to invalidate

			invalidate();
		}
	}*/

	/** Returns an iterator to iterate thru all visible children.
	 * Unlike {@link #getVisibleItemCount}, it handles only the direct children.
	 * Component developer only.
	 * @since 3.5.1
	 */
	/**public Iterator getVisibleChildrenIterator() {
		final Grid grid = getGrid();
		if (grid != null && grid.inSpecialMold())
			return grid.getDrawerEngine().getVisibleChildrenIterator();
		return new VisibleChildrenIterator();
	}*/
	/**
	 * An iterator used by visible children.
	 */
	/**private class VisibleChildrenIterator implements Iterator {
		private final ListIterator _it = getChildren().listIterator();
		private Grid _grid = getGrid();
		private int _count = 0;
		public boolean hasNext() {
			if (_grid == null || !_grid.inPagingMold()) return _it.hasNext();
			
			if (_count >= _grid.getPaginal().getPageSize()) {
				return false;
			}

			if (_count == 0) {
				final Paginal pgi = _grid.getPaginal();
				int begin = pgi.getActivePage() * pgi.getPageSize();
				for (int i = 0; i < begin && _it.hasNext();) {
					getVisibleRow((Row)_it.next());
					i++;
				}
			}
			return _it.hasNext();
		}
		private Row getVisibleRow(Row row) {
			if (row instanceof Group) {
				final Group g = (Group) row;
				if (!g.isOpen()) {
					for (int j = 0, len = g.getItemCount(); j < len
							&& _it.hasNext(); j++)
						_it.next();
				}
			}
			while (!row.isVisible())
				row = (Row)_it.next();
			return row;
		}
		public Object next() {
			if (_grid == null || !_grid.inPagingMold()) return _it.next();
			_count++;
			final Row row = (Row)_it.next();
			return _it.hasNext() ? getVisibleRow(row) : row;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}*/
});

(_zkwg=_zkpk.Rows).prototype.className='zul.grid.Rows';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<tbody', this.domAttrs_() , '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
		
	out.push('</tbody>');	
}

zkmld(_zkwg,_zkmd);
zul.grid.Foot = zk.$extends(zul.Widget, {
	getGrid: function () {
		return this.parent;
	},
	getZclass: function () {
		return this._zclass == null ? "z-foot" : _zclass;
	}
});
(_zkwg=_zkpk.Foot).prototype.className='zul.grid.Foot';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<tr', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</tr>');
}

zkmld(_zkwg,_zkmd);
zul.grid.Footer = zk.$extends(zul.LabelImageWidget, {
	_span: 1,
	
	getGrid: function () {
		return this.parent ? this.parent.parent : null;
	},
	getColumn: function () {
		var grid = this.getGrid();
		if (grid) {
			var cs = grid.columns;
			if (cs)
				return cs.getChildAt(this.getChildIndex());
		}
		return null;
	},
	getSpan: function () {
		return this._span;
	},
	setSpan: function (span) {
		if (this._span != span) {
			this._span = span;
			var n = this.getNode();
			if (n) n.colspan = span;
		}
	},
	getZclass: function () {
		return this._zclass == null ? "z-footer" : this._zclass;
	}
});
(_zkwg=_zkpk.Footer).prototype.className='zul.grid.Footer';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<td', this.domAttrs_(), '><div id="', this.uuid,
		'$cave" class="', this.getZclass(), '">', this.domContent_());
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div></td>');
}

zkmld(_zkwg,_zkmd);
zul.grid.Paging = zk.$extends(zul.Widget, {
	_pgsz: 20,
	_ttsz: 0,
	_npg: 1,
	_actpg: 0,
	_pginc: 10,
	_autohide: 10,
	replaceHTML: function () {
		if (this.isBothPaging())
			this.parent.rerender();
		else
			this.$supers('replaceHTML', arguments);
	},
	isBothPaging: function () {
		return this.parent && this.parent.getPagingPosition
					&& "both" == this.parent.getPagingPosition();
	},
	getPageSize: function () {
		return this._pgsz;
	},
	setPageSize: function (size) {
		if (this._pgsz != size) {
			this._pgsz = size;
			this._updatePageNum();
			// TODO this.fire('onPagingImpl', this._actpg);
		}
	},
	getTotalSize: function () {
		return this._ttsz;
	},
	setTotalSize: function (size) {
		if (this._ttsz != size) {
			this._ttsz = size;
			this._updatePageNum();
			if (this._detailed) rerender();
		}
	},
	_updatePageNum: function () {
		var v = Math.floor((this._ttsz - 1) / this._pgsz + 1);
		if (v == 0) v = 1;
		if (v != this._npg) {
			this._npg = v;
			if (this._actpg >= this._npg)
				this._actpg = this._npg - 1;
		}
	},
	getPageCount: function () {
		return this._npg;
	},
	setPageCount: function (npg) {
		this._npg = npg;
	},
	getActivePage: function () {
		return this._actpg;
	},
	setActivePage: function (pg) {
		if (this._actpg != pg) {
			this._actpg = pg;
			// TODO this.fire('onPagingImpl', this._actpg);
		}
	},
	getPageIncrement: function () {
		return this._pginc;
	},
	setPageIncrement: function (pginc) {
		if (_pginc != pginc) {
			_pginc = pginc;
			this.rerender();
		}
	},
	isDetailed: function () {
		return this._detailed;
	},
	setDetailed: function (detailed) {
		if (this._detailed != detailed) {
			this._detailed = detailed;
			this.rerender();
		}
	},
	isAutohide: function () {
		return this._autohide;
	},
	setAutohide: function (autohide) {
		if (this._autohide != autohide) {
			this._autohide = autohide;
			if (this._npg == 1) this.rerender();
		}
	},
	_infoTags: function () {
		if (this._ttsz == 0)
			return "";
		var lastItem = (this._actpg+1) * this._pgsz,
			out = [];
		out.push('<div class="', this.getZclass(), '-info">[ ', lastItem + 1,
				' - ', lastItem > this._ttsz ? this._ttsz : lastItem, ' / ',
				this._ttsz, ' ]</div>');
		return out.join('');
	},
	_innerTags: function () {
		var out = [];

		var half = this._pginc / 2,
			begin, end = this._actpg + half - 1;
		if (end >= this._npg) {
			end = this._npg - 1;
			begin = end - this._pginc + 1;
			if (begin < 0) begin = 0;
		} else {
			begin = this._actpg - half;
			if (begin < 0) begin = 0;
			end = begin + this._pginc - 1;
			if (end >= this._npg) end = this._npg - 1;
		}
		var zcs = this.getZclass();
		if (this._actpg > 0) {
			if (begin > 0) //show first
				this.appendAnchor(zcs, out, msgzul.FIRST, 0);
			this.appendAnchor(zcs, out, msgzul.PREV, this._actpg - 1);
		}

		var bNext = this._actpg < this._npg - 1;
		for (; begin <= end; ++begin) {
			if (begin == this._actpg) {
				this.appendAnchor(zcs, out, begin + 1, begin, true);
			} else {
				this.appendAnchor(zcs, out, begin + 1, begin);
			}
		}

		if (bNext) {
			this.appendAnchor(zcs, out, msgzul.NEXT, this._actpg + 1);
			if (end < this._npg - 1) //show last
				this.appendAnchor(zcs, out, msgzul.LAST, this._npg - 1);
		}
		if (this._detailed)
			out.push('<span>[', this._actpg * this._pgsz + 1, '/', this._ttsz, "]</span>");
		return out.join('');
	},
	appendAnchor: function (zclass, out, label, val, seld) {
		zclass += "-cnt" + (seld ? " " + zclass + "-seld" : "");
		out.push('<a class="', zclass, '" href="javascript:;" onclick="zul.grid.Paging.go(this,',
				val, ')">', label, '</a>&nbsp;');
	},
	getZclass: function () {
		var added = "os" == this.getMold() ? "-os" : "";
		return this._zclass == null ? "z-paging" + added : this._zclass;
	},
	isVisible: function () {
		var visible = this.$supers('isVisible', arguments);
		return visible && (this._npg > 1 || !this._autohide);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.getMold() == "os") return;
		var uuid = this.uuid,
			inputs = zDom.$$(uuid, 'real'),
			zcls = this.getZclass(),
			$Paging = this.$class;
			
		for (var i = inputs.length; --i>=0;) {
			zEvt.listen(inputs[i], "keydown", $Paging._doKeyDown);
			zEvt.listen(inputs[i], "blur", $Paging._doBlur);
		}
		
		for (var postfix = ['first', 'prev', 'last', 'next'], k = postfix.length; --k >=0; ) {
			var btn = zDom.$$(uuid, postfix[k]);
			for (var j = btn.length; --j>=0;) {
				zEvt.listen(btn[j], "mouseover", $Paging._doMouseOver);
				zEvt.listen(btn[j], "mouseout", $Paging._doMouseOut);
				zEvt.listen(btn[j], "mousedown", $Paging._doMouseDown);
				zEvt.listen(btn[j], "click", $Paging['_do' + postfix[k] + 'Click']);
				if (this._npg == 1)
					zDom.addClass(btn[j], zcls + "-btn-disd");
				else if (postfix[k] == 'first' || postfix[k] == 'prev') {
					if (this._actpg == 0) zDom.addClass(btn[j], zcls + "-btn-disd");
				} else if (this._actpg == this._npg - 1) {
					zDom.addClass(btn[j], zcls + "-btn-disd");
				}
			}
		}
	},
	unbind_: function () {
		if (this.getMold() != "os") {
			var uuid = this.uuid, inputs = zDom.$$(uuid, 'real'), $Paging = this.$class;
			
			for (var i = inputs.length; --i >= 0;) {
				zEvt.unlisten(inputs[i], "keydown", $Paging._doKeyDown);
				zEvt.unlisten(inputs[i], "blur", $Paging._doBlur);
			}
			
			for (var postfix = ['first', 'prev', 'last', 'next'], k = postfix.length; --k >= 0;) {
				var btn = zDom.$$(uuid, postfix[k]);
				for (var j = btn.length; --j >= 0;) {
					zEvt.unlisten(btn[j], "mouseover", $Paging._doMouseOver);
					zEvt.unlisten(btn[j], "mouseout", $Paging._doMouseOut);
					zEvt.unlisten(btn[j], "mousedown", $Paging._doMouseDown);
					zEvt.unlisten(btn[j], "click", $Paging['_do' + postfix[k] + 'Click']);
				}
			}
		}
		this.$supers('unbind_', arguments);
	}
}, {
	go: function (anc, pgno) {
		var wgt = zk.Widget.isInstance(anc) ? anc : zk.Widget.$(anc);
		if (wgt && wgt.getActivePage() != pgno)
			wgt.fire('onPaging', pgno);
	},
	_doKeyDown: function (evt) {
		if (!evt) evt = window.event;
		var inp = zEvt.target(evt),
			wgt = zk.Widget.$(inp);
		if (inp.disabled || inp.readOnly)
			return;
	
		var code =zEvt.keyCode(evt);
		switch(code){
		case 48:case 96://0
		case 49:case 97://1
		case 50:case 98://2
		case 51:case 99://3	
		case 52:case 100://4
		case 53:case 101://5
		case 54:case 102://6
		case 55:case 103://7
		case 56:case 104://8
		case 57:case 105://9
			break;		
		case 37://left
			break;		
		case 38: case 33: //up, PageUp
			wgt.$class._increase(inp, wgt, 1);
			zEvt.stop(evt);
			break;
		case 39://right
			break;		
		case 40: case 34: //down, PageDown
			wgt.$class._increase(inp, wgt, -1);
			zEvt.stop(evt);
			break;
		case 36://home
			wgt.$class.go(wgt,0);
			zEvt.stop(evt);
			break;
		case 35://end
			wgt.$class.go(wgt, wgt._npg - 1);
			zEvt.stop(evt);
			break;
		case 9: case 8: case 46: //tab, backspace, delete 
			break;
		case 13: //enter
			wgt.$class._increase(inp, wgt, 0);
			wgt.$class.go(wgt, inp.value-1);
			zEvt.stop(evt);
			break;
		default:
			if (!(code >= 112 && code <= 123) //F1-F12
			&& !evt.ctrlKey && !evt.altKey)
				zEvt.stop(evt);
		}
	},
	_doBlur: function (evt) {
		if (!evt) evt = window.event;
		var inp = zEvt.target(evt),
			wgt = zk.Widget.$(inp);
		if (inp.disabled || inp.readOnly)
			return;
		
		wgt.$class._increase(inp, wgt, 0);
		wgt.$class.go(wgt, inp.value-1);
		zEvt.stop(evt);
	},
	_increase: function (inp, wgt, add){
		var value = zk.parseInt(inp.value);
		value += add;
		if (value < 1) value = 1;
		else if (value > wgt._npg) value = wgt._npg;
		inp.value = value;
	},
	_dofirstClick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			zcls = wgt.getZclass();
		
		if (wgt.getActivePage() != 0) {
			wgt.$class.go(wgt, 0);
			var uuid = wgt.uuid;
			for (var postfix = ['first', 'prev'], k = postfix.length; --k >= 0;)
				for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
					zDom.addClass(btn[i], zcls + "-btn-disd");
		}
	},
	_doprevClick: function (evt) {		
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			ap = wgt.getActivePage(),
			zcls = wgt.getZclass();
		
		if (ap > 0) {
			wgt.$class.go(wgt, ap - 1);
			if (ap - 1 == 0) {
				var uuid = wgt.uuid;
				for (var postfix = ['first', 'prev'], k = postfix.length; --k >= 0;)
					for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
						zDom.addClass(btn[i], zcls + "-btn-disd");
			}
		}
	},
	_donextClick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			ap = wgt.getActivePage(),
			pc = wgt.getPageCount(),
			zcls = wgt.getZclass();
		
		if (ap < pc - 1) {
			wgt.$class.go(wgt, ap + 1);
			if (ap + 1 == pc - 1) {
				var uuid = wgt.uuid;
				for (var postfix = ['last', 'next'], k = postfix.length; --k >= 0;)
					for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
						zDom.addClass(btn[i], zcls + "-btn-disd");
			}
		}
	},
	_dolastClick: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zk.Widget.$(evt),
			pc = wgt.getPageCount(),
			zcls = wgt.getZclass();
		
		if (wgt.getActivePage() < pc - 1) {
			wgt.$class.go(wgt, pc - 1);
			var uuid = wgt.uuid;
			for (var postfix = ['last', 'next'], k = postfix.length; --k >= 0;)
				for (var btn = zDom.$$(uuid, postfix[k]), i = btn.length; --i >= 0;)
					zDom.addClass(btn[i], zcls + "-btn-disd");
		}
		
	},
	_doMouseOver: function (evt) {
		if (!evt) evt = window.event;
		var target = zEvt.target(evt),
			table = zDom.parentByTag(target, "TABLE"),
			zcls = zk.Widget.$(target).getZclass();
		if (!zDom.hasClass(table, zcls + "-btn-disd")) 
			zDom.addClass(table, zcls + "-btn-over");
	},
	_doMouseOut: function (evt) {
		if (!evt) evt = window.event;
		var target = zEvt.target(evt),
			table = zDom.parentByTag(target, "TABLE"),
			wgt = zk.Widget.$(target);
		zDom.rmClass(table, wgt.getZclass() + "-btn-over");
	},
	_doMouseDown: function (evt) {		
		if (!evt) evt = window.event;
		var target = zEvt.target(evt),
			table = zDom.parentByTag(target, "TABLE"),
			wgt = zk.Widget.$(target),
			zcls = wgt.getZclass();
		if (zDom.hasClass(table, zcls + "-btn-disd")) return;
		
		zDom.addClass(table, zcls + "-btn-clk");
		wgt.$class._downbtn = table;
		zEvt.listen(document.body, "mouseup", wgt.$class._doMouseUp);
	},
	_doMouseUp: function (evt) {
		if (!evt) evt = window.event;
		if (zul.grid.Paging._downbtn) {
			var zcls = zk.Widget.$(zul.grid.Paging._downbtn).getZclass();
			zDom.rmClass(zul.grid.Paging._downbtn, zcls + "-btn-clk");
		}
		zul.grid.Paging._downbtn = null;
		zEvt.unlisten(document.body, "mouseup", zul.grid.Paging._doMouseUp);
	}
});

(_zkwg=_zkpk.Paging).prototype.className='zul.grid.Paging';_zkmd={};
_zkmd['os']=
function (out) {
	if (this.getMold() == "os") {
		out.push('<div', this.domAttrs_(), '>', this._innerTags(), '</div>');
		return;
	}
	var uuid = this.uuid,
		zcls = this.getZclass();
	out.push('<div name="', uuid, '"', this.domAttrs_(), '>', '<table', zUtl.cellps0,
			'><tbody><tr><td><table id="', uuid, '$first" name="', uuid, '$first"',
			zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
			'<td class="', zcls, '-btn-l"><i>&#160;</i></td>',
			'<td class="', zcls, '-btn-m"><em unselectable="on">',
			'<button type="button" class="', zcls, '-first"> </button></em></td>',
			'<td class="', zcls, '-btn-r"><i>&#160;</i></td></tr></tbody></table></td>',
			'<td><table id="', uuid, '$prev" name="', uuid, '$prev"', zUtl.cellps0,
			' class="', zcls, '-btn"><tbody><tr><td class="', zcls, '-btn-l"><i>&#160;</i></td>',
			'<td class="', zcls, '-btn-m"><em unselectable="on"><button type="button" class="',
			zcls, '-prev"> </button></em></td><td class="', zcls, '-btn-r"><i>&#160;</i></td>',
			'</tr></tbody></table></td><td><span class="', zcls, '-sep"/></td>',
			'<td><span class="', zcls, '-text"></span></td><td><input id="',
			uuid, '$real" name="', uuid, '$real" type="text" class="', zcls,
			'-inp" value="', this.getActivePage() + 1, '" size="3"/></td>',
			'<td><span class="', zcls, '-text">/ ', this.getPageCount(), '</span></td>',
			'<td><span class="', zcls, '-sep"/></td><td><table id="', uuid,
			'$next" name="', uuid, '$next"', zUtl.cellps0, ' class="', zcls, '-btn">',
			'<tbody><tr><td class="', zcls, '-btn-l"><i>&#160;</i></td><td class="',
			zcls, '-btn-m"><em unselectable="on"><button type="button" class="',
			zcls, '-next"> </button></em></td><td class="', zcls, '-btn-r"><i>&#160;</i></td>',
			'</tr></tbody></table></td><td><table id="', uuid, '$last" name="',
			uuid, '$last"', zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
			'<td class="', zcls, '-btn-l"><i>&#160;</i></td><td class="', zcls,
			'-btn-m"><em unselectable="on"><button type="button" class="', zcls,
			'-last"> </button></em></td><td class="', zcls, '-btn-r"><i>&#160;</i></td>',
			'</tr></tbody></table></td></tr></tbody></table>');
			
	if (this.isDetailed()) out.push(this._infoTags());
	out.push('</div>');
}

_zkmd['default']=[_zkpk.Paging,'os'];zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}_z='zul.inp';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.inp.InputWidget = zk.$extends(zul.Widget, {
	_maxlength: 0,
	_cols: 0,
	_tabindex: -1,
	_type: 'text',

	$init: function () {
		this._pxLateBlur = this.proxy(this._lateBlur);
		this.$supers('$init', arguments);
	},
	getType: function () {
		return this._type;
	},
	isMultiline: function() {
		return false;
	},

	getValue: function () {
		return this._value;
	},
	setValue: function (value, fromServer) {
		if (fromServer) this.clearErrorMessage(true);
		else if (value == this._lastRawValVld) return; //not changed
 		else value = this._validate(value);

		if ((!value || !value.error) && (fromServer || this._value != value)) {
			this._value = value;
			if (this.einp) {
				this.einp.value = value = this.coerceToString_(value);
				if (fromServer) this.einp.defaultValue = value;
			}
		}
	},

	getName: function () {
		return this._name;
	},
	setName: function (name) {
		if (this._name != name) {
			this._name = name;
			if (this.einp)
				this.einp.name = name;
		}
	},
	isDisabled: function () {
		return this._disabled;
	},
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			if (this.einp) {
				this.einp.disabled = disabled;
				var zcls = this.getZclass(),
					fnm = disabled ? 'addClass': 'rmClass';
				zDom[fnm](this.getNode(), zcls + '-disd');
				zDom[fnm](this.einp, zcls + '-text-disd');
			}
		}
	},
	isReadonly: function () {
		return this._readonly;
	},
	setReadonly: function (readonly) {
		if (this._readonly != readonly) {
			this._readonly = readonly;
			if (this.einp) {
				this.einp.readOnly = readonly;
				zDom[readonly ? 'addClass': 'rmClass'](this.einp,
					this.getZclass() + '-readonly');
			}
		}
	},

	getCols: function () {
		return this._cols;
	},
	setCols: function (cols) {
		if (this._cols != cols) {
			this._cols = cols;
			if (this.einp)
				if (this.isMultiline()) this.einp.cols = cols;
				else this.einp.size = cols;
		}
	},
	getMaxlength: function () {
		return this._maxlength;
	},
	setMaxlengths: function (maxlength) {
		if (this._maxlength != maxlength) {
			this._maxlength = maxlength;
			if (this.einp && !this.isMultiline())
				this.einp.maxLength = maxlength;
		}
	},
	getTabindex: function () {
		return this._tabindex;
	},
	setTabindex: function (tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			if (this.einp)
				this.einp.tabIndex = tabindex;
		}
	},

	innerAttrs_: function () {
		var html = '', v;
		if (this.isMultiline()) {
			v = this._cols;
			if (v > 0) html += ' cols="' + v + '"';
		} else {
			html += ' value="' + this.coerceToString_(this.getValue()) + '"';
			html += ' type="' + this._type + '"';
			v = this._cols;
			if (v > 0) html += ' size="' + v + '"';
			v = this._maxlength;
			if (v > 0) html += ' maxlength="' + v + '"';
		}
		v = this._tabindex;
		if (v >= 0) html += ' tabindex="' + v +'"';
		v = this._name;
		if (v) html += ' name="' + v + '"';
		if (this._disabled) html += ' disabled="disabled"';
		if (this._readonly) html += ' readonly="readonly"';
		return html;
	},
	_areaText: function () {
		return zUtl.encodeXML(this.coerceToString_(this._value));
	},

	setConstraint: function (cst) {
		if (typeof cst == 'string')
			this._cst = new zul.inp.SimpleConstraint(cst);
		else if (cst == true) //by-server
			this._cst = true;
		else
			this._cst = cst;
		if (this._cst) delete this._lastRawValVld; //revalidate required
	},
	getConstraint: function () {
		return this._cst;
	},
	//dom event//
	doFocus_: function (evt) {
		if (!zDom.tag(zEvt.target(evt)) //Bug 2111900
		|| !this.domFocus_())
			return;

		if (this.isListen('onChanging')) {
			this._lastChg = this.einp.value;
			this._tidChg = setInterval(this.proxy(this._onChanging), 500);
		}
		zDom.addClass(this.einp, this.getZclass() + '-focus');
	},
	doBlur_: function (evt) {
		this._stopOnChanging();

		zDom.rmClass(this.einp, this.getZclass() + '-focus');
		this.domBlur_();

		setTimeout(this._pxLateBlur, 0);
			//curretFocus still unknow, so wait a while to execute
	},
	_doSelect: function (evt) {
		if (this.isListen('onSelection')) {
			var inp = this.einp,
				sr = zDom.getSelectionRange(inp),
				b = sr[0], e = sr[1];
			this.fire('onSelection', {start: b, end: e,
				selected: inp.value.substring(b, e),
				marshal: this._onSelMarshal});
		}
	},
	_onSelMarshal: function () {
		return [this.start, this.end, this.selected];
	},
	_lateBlur: function () {
		if (this.shallUpdate_(zk.currentFocus))
			this._updateChange();
	},
	shallUpdate_: function (focus) {
		return !focus || !zUtl.isAncestor(this, focus);
	},
	getErrorMesssage: function () {
		return this._errmsg;
	},
	clearErrorMessage: function (revalidate, remainError) {
		var w = this._errbox;
		if (w) {
			this._errbox = null;
			w.destroy();
		}
		if (!remainError) {
			this._errmsg = null;
			zDom.rmClass(this.einp, this.getZclass() + "-text-invalid");
		}
		if (revalidate)
			delete this._lastRawValVld; //cause re-valid
	},
	coerceFromString_: function (value) {
		return value;
	},
	coerceToString_: function (value) {
		return value || '';
	},
	_markError: function (val, msg) {
		this._errmsg = msg;

		if (this.desktop) { //err not visible if not attached
			zDom.addClass(this.einp, this.getZclass() + "-text-invalid");

			var cst = this._cst, errbox;
			if (cst) {
				errbox = cst.showCustomError;
				if (errbox) errbox = errbox.call(cst, this, msg);
			}

			if (!errbox) this._errbox = this.showError_(msg);

			this.fire('onError',
				{value: val, message: msg, marshal: this._onErrMarshal});
		}
	},
	validate_: function (val) {
		if (this._cst) {
			if (this._cst == true) { //by server
				return; //TODO
			}
			return this._cst.validate(this, val);
		}
	},
	_validate: function (value) {
		zul.inp.validating = true;
		try {
			var val = value;
			if (typeof val == 'string' || val == null) {
				val = this.coerceFromString_(val);
				if (val) {
					var errmsg = val.error;
					if (errmsg) {
						this.clearErrorMessage(true);
						this._markError(val, errmsg);
						return val;
					}
				}
			}

			//unlike server, validation occurs only if attached
			if (!this.desktop) this._errmsg = null;
			else {
				this.clearErrorMessage(true);
				var msg = this.validate_(val);
				if (msg) {
					this._markError(val, msg);
					return {error: msg};
				} else
					this._lastRawValVld = value; //raw
			}
			return val;
		} finally {
			zul.inp.validating = false;
		}
	},
	showError_: function (msg) {
		var eb = new zul.inp.Errorbox();
		eb.show(this, msg);
		return eb;
	},
	_updateChange: function () {
		if (zul.inp.validating) return; //avoid deadloop (when both focus and blur fields invalid)

		var inp = this.einp,
			value = inp.value;
		if (value == this._lastRawValVld)
			return; //not changed

		var wasErr = this._errmsg,
			val = this._validate(value);
		if (!val || !val.error) {
			inp.value = value = this.coerceToString_(val);
			if (wasErr || value != inp.defaultValue) {
				this._value = val;
				inp.defaultValue = value;
				this.fire('onChange', this._onChangeData(value), null, 150);
			}
		}
	},
	_onChanging: function () {
		var inp = this.einp,
			val = this.valueEnter__ || inp.value;
		if (this._lastChg != val) {
			this._lastChg = val;
			var valsel = this.valueSel_;
			this.valueSel_ = null;
			this.fire('onChanging', this._onChangeData(val, valsel == val),
				{ignorable:1}, 100);
		}
	},
	_onChangeData: function (val, selbak) {
		return {value: val,
			bySelectBack: selbak,
			start: zDom.getSelectionRange(this.einp)[0],
			marshal: this._onChangeMarshal}
	},
	_onChangeMarshal: function () {
		return [this.value, this.bySelectBack, this.start];
	},
	_onErrMarshal: function () {
		return [this.vale, this.message];
	},
	_stopOnChanging: function () {
		if (this._tidChg) {
			clearInterval(this._tidChg);
			this._tidChg = this._lastChg = this.valueEnter_ =
			this.valueSel_ = null;
		}
	},

	//super//
	focus: function (timeout) {
		if (this.isVisible() && this.canActivate({checkOnly:true})) {
			zDom.focus(this.einp, timeout);
			return true;
		}
		return false;
	},
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments),
			zcls = this.getZclass();
		if (!no || !no.zclass) {
			if (this._disabled)
				sc += ' ' + zcls + '-disd';
		}
		if (!no || !no.input) {
			if (this._disabled)
				sc += ' ' + zcls + '-text-disd';
			if (this._readonly)
				sc += ' ' + zcls + '-readonly';
		}
		return sc;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var inp = this.einp = this.getSubnode('inp') || this.getNode();
		zEvt.listen(inp, "focus", this.proxy(this.doFocus_, '_pxFocus'));
		zEvt.listen(inp, "blur", this.proxy(this.doBlur_, '_pxBlur'));
		zEvt.listen(inp, "select", this.proxy(this._doSelect, '_pxSelect'));
	},
	unbind_: function () {
		this.clearErrorMessage(true);

		var n = this.einp;
		zEvt.unlisten(n, "focus", this._pxFocus);
		zEvt.unlisten(n, "blur", this._pxBlur);
		zEvt.unlisten(n, "select", this._pxSelect);

		this.einp = null;
		this.$supers('unbind_', arguments);
	},
	doKeyDown_: function (evt) {
		var keyCode = evt.keyCode, keys = evt.keys;
		if (keyCode == 9 && !keys.altKey && !keys.ctrlKey && !keys.shiftKey
		&& this._tabbable) {
			var sr = zDom.getSelectionRange(inp),
				val = inp.value;
			val = val.substring(0, sr[0]) + '\t' + val.substring(sr[1]);
			inp.value = val;

			val = sr[0] + 1;
			zDom.setSelectionRange(inp, val, val);

			evt.stop();
			return;
		}

		if ((keyCode == 13 && this.isListen('onOK'))
		|| (keyCode == 27 && this.isListen('onCancel'))) {
			this._stopOnChanging();
			this._updateChange();
		}
	},
	doKeyUp_: function () {
		//Support maxlength for Textarea
		if (this.isMultiline()) {
			var maxlen = this._maxlength;
			if (maxlen > 0) {
				var inp = this.einp, val = inp.value;
				if (val != inp.defaultValue && val.length > maxlen)
					inp.value = val.substring(0, maxlen);
			}
		}
		this.$supers('doKeyUp_', arguments);
	}
});

(_zkwg=_zkpk.InputWidget).prototype.className='zul.inp.InputWidget';
zul.inp.Errorbox = zk.$extends('zul.wgt.Popup', {
	$init: function () {
		this.$supers('$init', arguments);
		this.setWidth("260px");
		this.setSclass('z-errbox');
	},
	show: function (owner, msg) {
		this.parent = owner; //fake
		this.msg = msg;
		this.insertHTML(document.body, "beforeEnd");
		this.open(owner, null, "end_before", {overflow:true});
	},
	destroy: function () {
		this.close();
		var n = this.getNode();
		this.unbind_();
		zDom.remove(n);
		this.parent = null;
	},
	//super//
	bind_: function () {
		this.$supers('bind_', arguments);

		var $Errorbox = zul.inp.Errorbox;
		this._drag = new zk.Draggable(this, null, {
			starteffect: zk.$void,
			endeffect: $Errorbox._enddrag,
			ignoredrag: $Errorbox._ignoredrag,
			change: $Errorbox._change
		});
		zWatch.listen('onScroll', this);
	},
	unbind_: function () {
		this._drag.destroy();
		zWatch.unlisten('onScroll', this);

		this.$supers('unbind_', arguments);
		this._drag = null;
	},
	onScroll: function (wgt) {
		if (wgt) { //scroll requires only if inside, say, borderlayout
			this.position(this.parent, null, "end_before", {overflow:true});
			this._fixarrow();
		}
	},
	setDomVisible_: function (node, visible) {
		this.$supers('setDomVisible_', arguments);
		var stackup = this._stackup;
		if (stackup) stackup.style.display = visible ? '': 'none';
	},
	doMouseMove_: function (evt) {
		var el = evt.nativeTarget;
		if (el == this.getSubnode('c')) {
			var y = evt.data.pageY,
				size = zk.parseInt(zDom.getStyle(el, 'padding-right'))
				offs = zDom.revisedOffset(el);
			if (y >= offs[1] && y < offs[1] + size)	zDom.addClass(el, 'z-errbox-close-over');
			else zDom.rmClass(el, 'z-errbox-close-over');
		} else this.$supers('doMouseMove_', arguments);
	},
	doMouseOut_: function (evt) {
		var el = evt.nativeTarget;
		if (el == this.getSubnode('c'))
			zDom.rmClass(el, 'z-errbox-close-over');
		else
			this.$supers('doMouseOut_', arguments);
	},
	doClick_: function (evt) {
		var el = evt.nativeTarget;
		if (el == this.getSubnode('c') && zDom.hasClass(el, 'z-errbox-close-over'))
			this.parent.clearErrorMessage(true, true);
		else {
			this.$supers('doClick_', arguments);
			this.parent.focus(0);
		}
	},
	open: function () {
		this.$supers('open', arguments);
		this.setTopmost();
		this._fixarrow();
	},
	prologHTML_: function (out) {
		var id = this.uuid;
		out.push('<div id="', id);
		out.push('$a" class="z-errbox-left z-arrow" title="')
		out.push(zUtl.encodeXML(mesg.GOTO_ERROR_FIELD));
		out.push('"><div id="', id, '$c" class="z-errbox-right z-errbox-close"><div class="z-errbox-center">');
		out.push(zUtl.encodeXML(this.msg, {multiline:true})); //Bug 1463668: security
		out.push('</div></div></div>');
	},
	onFloatUp: function (wgt) {
		if (wgt == this) {
			this.setTopmost();
			return;
		}
		if (!wgt || wgt == this.parent || !this.isVisible())
			return;

		var top1 = this, top2 = wgt;
		while ((top1 = top1.parent) && !top1.isFloating_())
			;
		for (; top2 && !top2.isFloating_(); top2 = top2.parent)
			;
		if (top1 == top2) { //uncover if sibling
			var n = wgt.getNode();
			if (n) this._uncover(n);
		}
	},
	_uncover: function (el) {
		var elofs = zDom.cmOffset(el),
			node = this.getNode(),
			nodeofs = zDom.cmOffset(node);

		if (zDom.isOverlapped(
		elofs, [el.offsetWidth, el.offsetHeight],
		nodeofs, [node.offsetWidth, node.offsetHeight])) {
			var parent = this.parent.getNode(), y;
			var ptofs = zDom.cmOffset(parent),
				pthgh = parent.offsetHeight,
				ptbtm = ptofs[1] + pthgh;
			y = elofs[1] + el.offsetHeight <=  ptbtm ? ptbtm: ptofs[1] - node.offsetHeight;
				//we compare bottom because default is located below

			var ofs = zDom.toStyleOffset(node, 0, y);
			node.style.top = ofs[1] + "px";
			this._fixarrow();
		}
	},
	_fixarrow: function () {
		var parent = this.parent.getNode(),
			node = this.getNode(),
			arrow = this.getSubnode('a'),
			ptofs = zDom.revisedOffset(parent),
			nodeofs = zDom.revisedOffset(node);
		var dx = nodeofs[0] - ptofs[0], dy = nodeofs[1] - ptofs[1], dir;
		if (dx >= parent.offsetWidth - 2) {
			dir = dy < -10 ? "ld": dy >= parent.offsetHeight - 2 ? "lu": "l";
		} else if (dx < 0) {
			dir = dy < -10 ? "rd": dy >= parent.offsetHeight - 2 ? "ru": "r";
		} else {
			dir = dy < 0 ? "d": "u";
		}
		arrow.className = 'z-errbox-left z-arrow-' + dir;
	}
},{
	_enddrag: function (dg) {
		var errbox = dg.control;
		errbox.setTopmost();
		errbox._fixarrow();
	},
	_ignoredrag: function (dg, pointer, evt) {
		return zEvt.target(evt) == dg.control.getSubnode('c') && zDom.hasClass(dg.control.getSubnode('c'), 'z-errbox-close-over');
	},
	_change: function (dg) {
		var errbox = dg.control,
			stackup = errbox._stackup;
		if (stackup) {
			var el = errbox.getNode();
			stackup.style.top = el.style.top;
			stackup.style.left = el.style.left;
		}
		errbox._fixarrow();
	}
});
(_zkwg=_zkpk.Errorbox).prototype.className='zul.inp.Errorbox';
zul.inp.SimpleConstraint = zk.$extends(zk.Object, {
	$init: function (a, b, c) {
		if (typeof a == 'string') {
			this._flags = {};
			this._init(a);
		} else {
			if (a) this._flags = typeof a == 'number' ? this._cvtNum(a): a;
			this._regex = typeof b == 'string' ? new RegExp(b): b;
			this._errmsg = c; 
		}
	},
	_init: function (cst) {
		l_out:
		for (var j = 0, k = 0, len = cst.length; k >= 0; j = k + 1) {
			for (;; ++j) {
				if (j >= len) return; //done

				var cc = cst.charAt(j);
				if (cc == '/') {
					for (k = ++j;; ++k) { //look for ending /
						if (k >= len) { //no ending /
							k = -1;
							break;
						}

						cc = cst.charAt(k);
						if (cc == '/') break; //ending / found
						if (cc == '\\') ++k; //skip one
					}
					this._regex = new RegExp(k >= 0 ? cst.substring(j, k): cst.substring(j));
					continue l_out;
				}
				if (cc == ':') {
					this._errmsg = cst.substring(j + 1).trim();
					return; //done
				}
				if (!zk.isWhitespace(cc))
					break;
			}

			var s;
			for (k = j;; ++k) {
				if (k >= len) {
					s = cst.substring(j);
					k = -1;
					break;
				}
				var cc = cst.charAt(k);
				if (cc == ',' || cc == ':' || cc == ';' || cc == '/') {
					s = cst.substring(j, k);
					if (cc == ':' || cc == '/') --k;
					break;
				}
			}

			this.parseConstraint_(s.trim().toLowerCase());
		}
	},
	parseConstraint_: function (cst) {
		var f = this._flags;
		if (cst == "no positive")
			f.NO_POSITIVE = true;
		else if (cst == "no negative")
			f.NO_NEGATIVE = true;
		else if (cst == "no zero")
			f.NO_ZERO = true;
		else if (cst == "no empty")
			f.NO_EMPTY = true;
		else if (cst == "no future")
			f.NO_FUTURE = true;
		else if (cst == "no past")
			f.NO_PAST = true;
		else if (cst == "no today")
			f.NO_TODAY = true;
		else if (cst == "strict")
			f.STRICT = true;
	},
	_cvtNum: function (v) { //compatible with server side
		var f = {};
		if (v & 1)
			f.NO_POSITIVE = f.NO_FUTURE = true;
		if (v & 2)
			f.NO_NEGATIVE = f.NO_PAST = true;
		if (v & 4)
			f.NO_ZERO = f.NO_TODAY = true;
		if (v & 0x100)
			f.NO_EMPTY = true;
		if (v & 0x200)
			f.STRICT = true;
		return f;
	},
	validate: function (wgt, val) {
		var f = this._flags,
			msg = this._errmsg;

		switch (typeof val) {
		case 'string':
			if (f.NO_EMPTY && (!val || !val.trim()))
				return msgzul.EMPTY_NOT_ALLOWED;
			var regex = this._regex;
			if (regex && !regex.test(val))
				return msg || msgzul.ILLEGAL_VALUE;
			if (f.STRICT && val) {
				//TODO VALUE_NOT_MATCHED;
			}
			return;
		case 'number':
			if (val > 0) {
				if (f.NO_POSITIVE) return msg || this._msgNumDenied();
			} else if (val == 0) {
				if (f.NO_ZERO) return msg || this._msgNumDenied();
			} else
				if (f.NO_NEGATIVE) return msg || this._msgNumDenied();
			return;
		}

		if (val && val.getFullYear) {
			var today = zUtl.today(),
				val = new Date(val.getFullYear(), val.getMonth(), val.getDate());
			if (val > today) {
				if (f.NO_FUTURE) return msg || this._msgDateDenied();
			} else if (val == today) {
				if (f.NO_TODAY) return msg || this._msgDateDenied();
			} else
				if (f.NO_PAST) return msg || this._msgDateDenied();
			return;
		}

		if (val && val.compareTo) {
			var b = val.compareTo(0);
			if (b > 0) {
				if (f.NO_POSITIVE) return msg || this._msgNumDenied();
			} else if (b == 0) {
				if (f.NO_ZERO) return msg || this._msgNumDenied();
			} else
				if (f.NO_NEGATIVE) return msg || this._msgNumDenied();
			return;
		}

		if (!val && f.NO_EMPTY) return msg || msgzul.EMPTY_NOT_ALLOWED;
	},
	_msgNumDenied: function () {
		var f = this._flags;
		if (f.NO_POSITIVE)
			return f.NO_ZERO ?
				f.NO_NEGATIVE ? NO_POSITIVE_NEGATIVE_ZERO: msgzul.NO_POSITIVE_ZERO:
				f.NO_NEGATIVE ? msgzul.NO_POSITIVE_NEGATIVE: msgzul.NO_POSITIVE;
		else if (f.NO_NEGATIVE)
			return f.NO_ZERO ? msgzul.NO_NEGATIVE_ZERO: msgzul.NO_NEGATIVE;
		else if (f.NO_ZERO)
			return msgzul.NO_ZERO;
		return msgzul.ILLEGAL_VALUE;
	},
	_msgDateDenied: function () {
		var f = this._flags;
		if (f.NO_FUTURE)
			return f.NO_TODAY ?
				f.NO_PAST ? NO_FUTURE_PAST_TODAY: msgzul.NO_FUTURE_TODAY:
				f.NO_PAST ? msgzul.NO_FUTURE_PAST: msgzul.NO_FUTURE;
		else if (f.NO_PAST)
			return f.NO_TODAY ? msgzul.NO_PAST_TODAY: msgzul.NO_PAST;
		else if (f.NO_TODAY)
			return msgzul.NO_TODAY;
		return msgzul.ILLEGAL_VALUE;
	}
});


zul.inp.Textbox = zk.$extends(zul.inp.InputWidget, {
	_value: '',
	_rows: 1,

	isMultiline: function () {
		return this._multiline;
	},
	setMultiline: function (multiline) {
		if (this._multiline != multiline) {
			this._multiline = multiline;
			this.rerender();
		}
	},
	isTabbable: function () {
		return this._tabbable;
	},
	setTabbable: function (tabbable) {
		this._tabbable = tabbable;
	},
	getRows: function () {
		return this._rows;
	},
	setRows: function (rows) {
		if (this._rows != rows) {
			this._rows = rows;
			if (this.einp && this.isMultiline())
				this.einp.rows = rows;
		}
	},
	setType: function (type) {
		if (this._type != type) {
			this._type = type;
			if (this.einp)
				this.einp.type = type;
		}
	},

	//super//
	innerAttrs_: function () {
		var html = this.$supers('innerAttrs_', arguments);
		if (this._multiline)
			html += ' rows="' + this._rows + '"';
		return html;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-textbox";
	}
});

(_zkwg=_zkpk.Textbox).prototype.className='zul.inp.Textbox';_zkmd={};
_zkmd['default']=
function (out) {
	if(this.isMultiline()) 
		out.push('<textarea', this.domAttrs_(), this.innerAttrs_(), '>',
				this._areaText(), '</textarea>');
	else
		out.push('<input', this.domAttrs_(), this.innerAttrs_(), '/>');
}
zkmld(_zkwg,_zkmd);
zul.inp.FormatWidget = zk.$extends(zul.inp.InputWidget, {
	getFormat: function () {
		return this._format;
	},
	setFormat: function (format) {
		if (this._format != format) {
			this._format = format;
			if (this.einp)
				this.einp.value = this.coerceToString_(this._value);
		}
	}
});

(_zkwg=_zkpk.FormatWidget).prototype.className='zul.inp.FormatWidget';
zul.inp.Intbox = zk.$extends(zul.inp.FormatWidget, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = parseInt(info.raw);
		if (info.raw != ''+val)
			return {error: zMsgFormat.format(msgzul.INTEGER_REQUIRED, value)};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	},
	coerceToString_: function (value) {
		var fmt = this._format;
		return fmt ? zNumFormat.format(fmt, value): value ? ''+value: '';
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-intbox";
	}
});
(_zkwg=_zkpk.Intbox).prototype.className='zul.inp.Intbox';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<input', this.domAttrs_(), this.innerAttrs_(), '/>');
}
zkmld(_zkwg,_zkmd);
zul.inp.Doublebox = zk.$extends(zul.inp.Intbox, {
	coerceFromString_: function (value) {
		if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = parseFloat(info.raw);
		if (info.raw != ''+val && info.raw.indexOf('e') < 0) //unable to handle 1e2
			return {error: zMsgFormat.format(msgzul.NUMBER_REQUIRED, value)};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-doublebox";
	}
});
(_zkwg=_zkpk.Doublebox).prototype.className='zul.inp.Doublebox';_zkmd={};
_zkmd['default']=[_zkpk.Intbox,'default'];zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}_z='zul.menu';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zPkg.load('zul.wgt');
zul.menu.Menubar = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	
	getOrient: function () {
		return this._orient;
	},
	setOrient: function (orient) {
		if (this._orient != orient) {
			this._orient = orient;
			this.rerender();
		}
	},
	isAutodrop: function () {
		return this._autodrop;
	},
	setAutodrop: function (autodrop) {
		if (this._autodrop != autodrop)
			this._autodrop = autodrop;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menubar" +
				("vertical" == this.getOrient() ? "-ver" : "-hor") : this._zclass;
	},
	unbind_: function () {
		this._lastTarget = null;
		this.$supers('unbind_', arguments);
	},
	insertChildHTML_: function (child, before, desktop) {
		if (before)
			zDom.insertHTMLBefore(before.getSubnode('chdextr'),
				this.encloseChildHTML_({child: child, vertical: 'vertical' == this.getOrient()}));
		else
			zDom.insertHTMLBeforeEnd(this.getNode(),
				this.encloseChildHTML_({child: child, vertical: 'vertical' == this.getOrient()}));
		
		child.bind_(desktop);
	},
	removeChildHTML_: function (child, prevsib) {
		this.$supers('removeChildHTML_', arguments);
		zDom.remove(child.uuid + '$chdextr');
	},
	encloseChildHTML_: function (opts) {
		var out = opts.out || [],
			child = opts.child,
			isVert = opts.vertical;
		if (isVert) {
			out.push('<td id="', child.uuid, '$chdextr"');
			if (child.getHeight())
				out.push(' height="', child.getHeight(), '"');
			out.push('>');
		}
		child.redraw(out);
		if (isVert)
			out.push('</tr>');
		if (!opts.out) return out.join('');
	}
});

(_zkwg=_zkpk.Menubar).prototype.className='zul.menu.Menubar';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid;
	if ('vertical' == this.getOrient()) {
		out.push('<div', this.domAttrs_(), '><table id="', uuid, '$cave"',
				zUtl.cellps0, '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			this.encloseChildHTML_({out: out, child: w, vertical: true});
		out.push('</table></div>');
	} else {
		out.push('<div', this.domAttrs_(), '><table', zUtl.cellps0, '>',
				'<tr valign="bottom" id="', uuid, '$cave">')
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</tr></table></div>');
	}
}

zkmld(_zkwg,_zkmd);
zul.menu.Menu = zk.$extends(zul.LabelImageWidget, {
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this._image;
		if (!img) img = zAu.comURI('web/img/spacer.gif');

		img = '<img src="' + img + '" align="absmiddle" class="' + this.getZclass() + '-img"/>';
		return label ? img + ' ' + label: img;
	},
	isTopmost: function () {
		return this._topmost;
	},
	beforeParentChanged_: function (newParent) {
		this._topmost = newParent && !(newParent.$instanceof(zul.menu.Menupopup));		
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu" : this._zclass;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		return this.isTopmost() ?
			style + 'padding-left:4px;padding-right:4px;': style;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.menu.Menupopup)) 
			this.menupopup = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.menupopup) 
			this.menupopup = null;
	},
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	/** Removes the extra space (IE only) */
	_fixBtn: function () {
		var btn = this.getSubnode('b');
		if (!btn || !btn.innerHTML.trim()) return;
		btn.style.width = zDom.getTextSize(btn, btn.innerHTML)[0] + zDom.frameWidth(btn) + "px";
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		
		if (!this.isTopmost()) {
			var anc = this.getSubnode('a'), n = this.getNode();
			zEvt.listen(anc, "focus", this.proxy(this.domFocus_, '_fxFocus'));
			zEvt.listen(anc, "blur", this.proxy(this.domBlur_, '_fxBlur'));
			zEvt.listen(n, "mouseover", this.proxy(this._doMouseOver, '_fxMouseOver'));
			zEvt.listen(n, "mouseout", this.proxy(this._doMouseOut, '_fxMouseOut'));
		} else {
			if (zk.ie) this._fixBtn();
			
			var anc = this.getSubnode('a');
			zEvt.listen(anc, "mouseover", this.proxy(this._doMouseOver, '_fxMouseOver'));
			zEvt.listen(anc, "mouseout", this.proxy(this._doMouseOut, '_fxMouseOut'));
		}
	},
	unbind_: function () {		
		if (!this.isTopmost()) {
			var anc = this.getSubnode('a'),
				n = this.getNode();
			zEvt.unlisten(anc, "focus", this._fxFocus);
			zEvt.unlisten(anc, "blur", this._fxBlur);
			zEvt.unlisten(n, "mouseover", this._fxMouseOver);
			zEvt.unlisten(n, "mouseout", this._fxMouseOut);
		} else {
			var n = this.getNode();
			zEvt.unlisten(n, "mouseover", this._fxMouseOver);
			zEvt.unlisten(n, "mouseout", this._fxMouseOut);
		}
		
		this.$supers('unbind_', arguments);		
	},
	doClick_: function (evt) {
		if (this.isTopmost() && !zDom.isAncestor(this.getSubnode('a'), evt.nativeTarget)) return;		
		zDom.addClass(this.getSubnode('a'), this.getZclass() + '-btn-seld');
		if (this.menupopup) {
			this.menupopup._shallClose = false;
			if (this.isTopmost())
				this.getMenubar()._lastTarget = this;
			if (!this.menupopup.isOpen()) this.menupopup.open();
		}
		this.fireX(evt);
	},
	_doMouseOver: function (evt) {
		if (this.$class._isActive(this)) return;
		
		var	topmost = this.isTopmost();
		if (topmost && zk.ie && !zDom.isAncestor(this.getSubnode('a'), zEvt.target(evt)))
				return; // don't activate 
		
		this.$class._addActive(this);
		if (!topmost) {
			if (this.menupopup) this.menupopup._shallClose = false;
			zWatch.fire('onFloatUp', null, this); //notify all
			if (this.menupopup && !this.menupopup.isOpen()) this.menupopup.open();
		} else {
			var menubar = this.getMenubar();
			if (this.menupopup && menubar.isAutodrop()) {
				menubar._lastTarget = this;
				this.menupopup._shallClose = false;
				zWatch.fire('onFloatUp', null, this); //notify all
				if (!this.menupopup.isOpen()) this.menupopup.open();
			} else {
				var target = menubar._lastTarget;
				if (target && target != this && menubar._lastTarget.menupopup
						&& menubar._lastTarget.menupopup.isVisible()) {
					menubar._lastTarget.menupopup.close({sendOnOpen:true});
					this.$class._rmActive(menubar._lastTarget);
					menubar._lastTarget = this;
					if (this.menupopup) this.menupopup.open();
				}
			}
		}
	},
	_doMouseOut: function (evt) {
		if (zk.ie) {
			var n = this.getSubnode('a'),
				xy = zDom.revisedOffset(n),
				p = zEvt.pointer(evt),
				x = p[0],
				y = p[1],
				diff = this.isTopmost() ? 1 : 0,
				vdiff = this.isTopmost() && 'vertical' == this.parent.getOrient() ? 1 : 0;
			if (x - diff > xy[0] && x <= xy[0] + n.offsetWidth && y - diff > xy[1] &&
					y - vdiff <= xy[1] + n.offsetHeight)
				return; // don't deactivate;
		}
		var	topmost = this.isTopmost();
		if (topmost) {
			if (this.menupopup && this.getMenubar().isAutodrop()) {
				this.$class._rmActive(this);
				if (this.menupopup.isOpen()) this.menupopup._shallClose = true;
				zWatch.fire('onFloatUp', {
					timeout: 10
				}, this); //notify all
			}
		} else if (this.menupopup && !this.menupopup.isOpen())
			this.$class._rmActive(this);
	}
}, {
	_isActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		return zDom.hasClass(n, cls);
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		zDom.addClass(n, cls);
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		zDom.rmClass(n, cls);
	}
});

(_zkwg=_zkpk.Menu).prototype.className='zul.menu.Menu';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
		
	if (this.isTopmost()) {
		out.push('<td align="left"', this.domAttrs_(), '><table id="', uuid,
				'$a"', zUtl.cellps0, ' class="', zcls, '-btn');
		
		if (this.getImage()) {
			out.push(' ', zcls, '-btn');
			if (this.getLabel())
				out.push('-text');
			
			out.push('-img');			
		}
		
		out.push('" style="width: auto;"><tbody><tr><td class="', zcls,
				'-btn-l"><i>&nbsp;</i></td><td class="', zcls,
				'-btn-m"><em unselectable="on"><button id="', uuid,
				'$b" type="button" class="', zcls, '-btn-text"');
		if (this.getImage())
			out.push(' style="background-image:url(', this.getImage(), ')"');
			
		out.push('>', zUtl.encodeXML(this.getLabel()), '</button>');
		
		if (this.menupopup) this.menupopup.redraw(out);
		
		out.push('</em></td><td class="', zcls, '-btn-r"><i>&nbsp;</i></td></tr></tbody></table></td>');
		
	} else {
		out.push('<li', this.domAttrs_(), '><a href="javascript:;" id="', uuid,
				'$a" class="', zcls, '-cnt ', zcls, '-cnt-img">', this.domContent_(), '</a>');
		if (this.menupopup) this.menupopup.redraw(out);
		
		out.push('</li>');
	}
}

zkmld(_zkwg,_zkmd);
zul.menu.Menuitem = zk.$extends(zul.LabelImageWidget, {
	_value: "",
	
	isCheckmark: function () {
		return this._checkmark;
	},
	setCheckmark: function (checkmark) {
		if (this._checkmark != checkmark) {
			this._checkmark = checkmark;
			this.rerender();
		}
	},
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			this.rerender();
		}
	},
	isDisabled: function () {
		return this._disabled;
	},
	getValue: function () {
		return this._value;
	},
	setValue: function (value) {
		if (!value)	value = "";
		this._value = value;
	},
	isChecked: function () {
		return this._checked;
	},
	setChecked: function (checked) {
		if (this._checked != checked) {
			this._checked = checked;
			if (this._checked)
				this._checkmark = this._checked;
			var n = this.getNode();
			if (n && !this.isTopmost() && !this.getImage()) {
				var zcls = this.getZclass();
				zDom.rmClass(n, zcls + '-cnt-ck');
				zDom.rmClass(n, zcls + '-cnt-unck');
				if (this._checkmark)
					zDom.addClass(n, zcls + (this._checked ? '-cnt-ck' : '-cnt-unck'));
			}
		}
	},
	isAutocheck: function () {
		return this._autocheck;
	},
	setAutocheck: function (autocheck) {
		if (this._autocheck != autocheck)
			this._autocheck = autocheck;
	},
	getHref: function () {
		return this._href;
	},
	setHref: function (href) {
		if (this._href != href) {
			this._href = href;
			this.rerender();
		}
	},
	getTarget: function () {
		return this._target;
	},
	setTarget: function (target) {
		if (this._target !=  target) {
			this._target = target;
			var anc = this.getSubnode('a');
			if (anc) {
				if (this.isTopmost())
					anc = anc.parentNode;
				anc.target = this._target;
			}
		}
	},
	isTopmost: function () {
		return this._topmost;
	},
	beforeParentChanged_: function (newParent) {
		this._topmost = newParent && !(newParent.$instanceof(zul.menu.Menupopup));		
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this.isDisabled() ? this.getZclass() + '-disd' : '';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-item" : this._zclass;
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this._image;
		if (!img) img = zAu.comURI('web/img/spacer.gif');

		img = '<img src="' + img + '" align="absmiddle" class="' + this.getZclass() + '-img"/>';
		return label ? img + ' ' + label: img;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		return this.isTopmost() ?
			style + 'padding-left:4px;padding-right:4px;': style;
	},
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	/** Removes the extra space (IE only) */
	_fixBtn: function () {
		var btn = this.getSubnode('b');
		if (!btn || !btn.innerHTML.trim()) return;
		btn.style.width = zDom.getTextSize(btn, btn.innerHTML)[0] + zDom.frameWidth(btn) + "px";
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		
		if (!this.isDisabled()) {			
			var anc = this.getSubnode('a'),
				n = this.getNode();
			
			zEvt.listen(n, "mouseover", this.proxy(this._doMouseOver, '_fxMouseOver'));
			zEvt.listen(n, "mouseout", this.proxy(this._doMouseOut, '_fxMouseOut'));
			
			if (this.isTopmost()) {
				zEvt.listen(anc, "focus", this.proxy(this.domFocus_, '_fxFocus'));
				zEvt.listen(anc, "blur", this.proxy(this.domBlur_, '_fxBlur'));
			}
		}
		if (zk.ie && this.isTopmost()) this._fixBtn();
	},
	unbind_: function () {
		if (!this.isDisabled()) {
			var anc = this.getSubnode('a'),
				n = this.getNode();				
			zEvt.unlisten(n, "mouseover", this._fxMouseOver);
			zEvt.unlisten(n, "mouseout", this._fxMouseOut);
			
			if (this.isTopmost()) {
				zEvt.unlisten(anc, "focus", this._fxFocus);
				zEvt.unlisten(anc, "blur", this._fxBlur);
			}
		}
			
		this.$supers('unbind_', arguments);		
	},
	doClick_: function (evt) {
		if (this._disabled)
			evt.stop();
		else {
			if (!this.$class._isActive(this)) return;
			
			var topmost = this.isTopmost(),
				anc = this.getSubnode('a');
			
			if (topmost) {
				zDom.rmClass(anc, this.getZclass() + '-btn-over');
				anc = anc.parentNode;
			}
			if ('javascript:;' == anc.href) {
				if (this.isAutocheck()) {
					this.setChecked(!this.isChecked());
					this.fire('onCheck', this.isChecked());
				}
				this.fireX(evt);
			} else {
				if (zk.ie && topmost && this.getNode().id != anc.id) 
					zUtl.go(anc.href, false, anc.target);
					// Bug #1886352 and #2154611 
					//Note: we cannot eat onclick. or, <a> won't work
			}
			zWatch.fire('onFloatUp', null, this); //notify all
		}
	},
	_doMouseOver: function (evt) {
		if (this.$class._isActive(this)) return;
		if (!this.isDisabled()) {
			if (this.isTopmost() && zk.ie && !zDom.isAncestor(this.getSubnode('a'), zEvt.target(evt)))
				return;
			
			this.$class._addActive(this);
			zWatch.fire('onFloatUp', null, this); //notify all
		}
	},
	_doMouseOut: function (evt) {
		if (!this.isDisabled()) {
			if (zk.ie) {
					var n = this.getSubnode('a'),
						xy = zDom.revisedOffset(n),
						p = zEvt.pointer(evt),
						x = p[0],
						y = p[1],
						diff = this.isTopmost() ? 1 : 0;
					if (x - diff > xy[0] && x <= xy[0] + n.offsetWidth && y - diff > xy[1] &&
						y <= xy[1] + n.offsetHeight)
						return; // don't deactivate;
				}
			this.$class._rmActive(this);
		}
	}
}, {
	_isActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		return zDom.hasClass(n, cls);
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		zDom.addClass(n, cls);
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		zDom.rmClass(n, cls);
	}
});

(_zkwg=_zkpk.Menuitem).prototype.className='zul.menu.Menuitem';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
	
	if (this.isTopmost()) {
		out.push('<td align="left"', this.domAttrs_(), '><a href="',
				this.getHref() ? this.getHref() : 'javascript:;', '"');
		if (this.getTarget())
			out.push(' target="', this.getTarget(), '"');
		out.push(' class="', zcls, '-cnt"><table id="', uuid, '$a"', zUtl.cellps0,
				' class="', zcls, '-btn');
		if (this.getImage()) {
			out.push(' ', zcls, '-btn');
			if (this.getLabel())
				out.push('-text');
			
			out.push('-img');			
		}
		out.push('" style="width: auto;"><tbody><tr><td class="', zcls,
				'-btn-l"><i>&nbsp;</i></td><td class="', zcls,
				'-btn-m"><em unselectable="on"><button id="', uuid,
				'$b" type="button" class="', zcls, '-btn-text"');
		if (this.getImage())
			out.push(' style="background-image:url(', this.getImage(), ')"');
			
		out.push('>', zUtl.encodeXML(this.getLabel()), '</button></em></td><td class="',
					zcls, '-btn-r"><i>&nbsp;</i></td></tr></tbody></table></a></td>');
	} else {
		out.push('<li', this.domAttrs_(), '>');
		var cls = zcls + '-cnt' +
				(!this.getImage() && this.isCheckmark() ? 
						' ' + zcls + (this.isChecked() ? '-cnt-ck' : '-cnt-unck') : '');
		out.push('<a href="', this.getHref() ? this.getHref() : 'javascript:;', '"');
		if (this.getTarget())
			out.push(' target="', this.getTarget(), '"');
		out.push(' id="', uuid, '$a" class="', cls, '">', this.domContent_(), '</a></li>');
	}
}

zkmld(_zkwg,_zkmd);
zul.menu.Menuseparator = zk.$extends(zul.Widget, {
	isPopup: function () {
		return this.parent && this.parent.$instanceof(zul.menu.Menupopup);
	},
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-separator" : this._zclass;
	},
	doMouseOver_: function () {
		zWatch.fire('onFloatUp', null, this); //notify all
		this.$supers('doMouseOver_', arguments);
	}
});

(_zkwg=_zkpk.Menuseparator).prototype.className='zul.menu.Menuseparator';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<li', this.domAttrs_(), '><span class="', this.getZclass(),
			'-inner">&nbsp;</span></li>');
}

zkmld(_zkwg,_zkmd);
zul.menu.Menupopup = zk.$extends('zul.wgt.Popup', {
	_curIndex: -1,
	
	_getCurrentIndex: function () {
		return this._curIndex;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-popup" : this._zclass;
	},
	getSelectedItem: function () {
		return this.getSelectedItemAt(this.getSelectedIndex());
	},
	_isActiveItem: function (wgt) { 
		return wgt.isVisible() && (wgt.$instanceof(zul.menu.Menu) || (wgt.$instanceof(zul.menu.Menuitem) && !wgt.isDisabled()));
	},
	_currentChild: function (index) {
		var index = index != null ? index : this._curIndex;
		for (var w = this.firstChild, k = -1; w; w = w.nextSibling) 
			if (this._isActiveItem(w) && ++k === index)
				return w;
		return null;
	},
	_previousChild: function (wgt) {
		wgt = wgt ? wgt.previousSibling : this.lastChild;
		var lastChild = this.lastChild == wgt;
		for (; wgt; wgt = wgt.previousSibling) 
			if (this._isActiveItem(wgt)) {
				this._curIndex--;
				return wgt;
			}
		if (lastChild) return null; // avoid deadloop;
		this.curIndex = 0;
		for (wgt = this.firstChild; wgt; wgt = wgt.nextSibling)
			if (this._isActiveItem(wgt)) this._curIndex++;
		return this._previousChild();
	},
	_nextChild: function (wgt) {
		wgt = wgt ? wgt.nextSibling : this.firstChild;
		var firstChild = this.firstChild == wgt;
		for (; wgt; wgt = wgt.nextSibling) 
			if (this._isActiveItem(wgt)) {
				this._curIndex++;
				return wgt;
			}
		if (firstChild) return null; // avoid deadloop;
		this._curIndex = -1;	
		return this._nextChild();		
	},
	_syncShadow: function () {
		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this.getNode(), {stackup:zk.ie6Only});
		this._shadow.sync();
	},
	_hideShadow: function () {
		if (this._shadow) this._shadow.hide();
	},
	close: function () {
		this.$supers('close', arguments);
		this._hideShadow();
		var menu = this.parent;
		if (menu.$instanceof(zul.menu.Menu) && menu.isTopmost())
			zDom.rmClass(menu.getSubnode('a'), menu.getZclass() + "-btn-seld");
		
		var item = this._currentChild();
		if (item) item.$class._rmActive(item);
		this._curIndex = -1;
		this.$class._rmActive(this);
		// TODO for columns' menu zk.fire(pp, "close");
	},
	open: function (ref, offset, position, opts) {
		if (this.parent.$instanceof(zul.menu.Menu)) {
			if (!offset) {
				ref = this.parent.getSubnode('a');
				if (!position)
					if (this.parent.isTopmost())
						position = this.parent.parent.getOrient() == 'vertical'
							? 'end_before' : 'after_start';
					else position = 'end_before';
			}
		}
		this.$super('open', ref, offset, position, opts);
	},
	onFloatUp: function(wgt) {
		if (!this.isVisible()) 
			return;
		var org = wgt;
		if (this.parent.menupopup == this && !this.parent.isTopmost() && !this.parent.$class._isActive(this.parent)) {
			this.close({sendOnOpen:true});
			return;
		}
		
		// check if the wgt belongs to the popup
		for (var floatFound; wgt; wgt = wgt.parent) {
			if (wgt == this || (wgt.menupopup == this && !this._shallClose)) {
				if (!floatFound)
					this.setTopmost();
				return;
			}
			floatFound = floatFound || wgt.isFloating_();
		}
		
		// check if the popup is one of the wgt's children
		for (var floatFound, wgt = this.parent; wgt; wgt = wgt.parent) {
			if (wgt == org) {
				if (this._shallClose) break;
				if (!floatFound) 
					this.setTopmost();
				return;
			}
			floatFound = floatFound || wgt.isFloating_();
		}
		
		// check if the popup is an active menu
		if (!this._shallClose && this.parent.$instanceof(zul.menu.Menu)) {
			var menubar = this.parent.getMenubar();
			if (menubar && menubar._lastTarget == this.parent)
				return;
		}
		this.close({sendOnOpen:true});
	},
	onVisible: function (wgt) {
		if (zk.ie7) {
			var pp = this.getNode();
			if (!pp.style.width) {// Bug 2105158 and Bug 1911129
				var ul = this.getSubnode('cave');
				pp.style.width = ul.offsetWidth + zDom.padBorderWidth(pp) + "px";
			}
		}
		this._syncShadow();
		var anc = this.getSubnode('a');
		if (anc) {
			
			// just in case
			if (zk.ie)
				zDom.cleanVisibility(this.getNode());
				
			anc.focus();
		}
	},
	onHide: function (wgt) {
		this._hideShadow();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen('onHide', this);
	},
	unbind_: function () {
		if (this._shadow)
			this._shadow.destroy();
		this._shadow = null;
		zWatch.unlisten('onHide', this);
		this.$supers('unbind_', arguments);
	},
	doKeyDown_: function (evt) {
		var w = this._currentChild(),
			keycode = evt.data.keyCode;
		switch (keycode) {
		case 38: //UP
		case 40: //DOWN
			if (w) w.$class._rmActive(w);
			w = keycode == 38 ? this._previousChild(w) : this._nextChild(w);
			if (w) w.$class._addActive(w);
			break;
		case 37: //LEFT
			this.close();
			
			if (this.parent.$instanceof(zul.menu.Menu) && !this.parent.isTopmost()) {
				var pp = this.parent.parent;
				if (pp) {
					var anc = pp.getSubnode('a');
					if (anc) anc.focus();
				}
			}
			break;
		case 39: //RIGHT
			if (w && w.$instanceof(zul.menu.Menu) && w.menupopup)
				w.menupopup.open();
			break;
		case 13: //ENTER
			if (w && w.$instanceof(zul.menu.Menuitem)) {
				w.doClick_(new zk.Event(w, 'onClick', null, {
					ctl: true
				}, evt.nativeEvent));
				zWatch.fire('onFloatUp', null, w); //notify all
				this.close({sendOnOpen:true});
			}
			break;
		}
		evt.stop();
		this.$supers('doKeyDown_', arguments);
	},
	doMouseOver_: function (evt) {
		this._shallClose = false;
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		this._shallClose = true;
		this.$supers('doMouseOut_', arguments);
	}	
}, {
	_rmActive: function (wgt) {
		if (wgt.parent.$instanceof(zul.menu.Menu)) {
			wgt.parent.$class._rmActive(wgt.parent);
			if (!wgt.parent.isTopmost())
				this._rmActive(wgt.parent.parent);
		}
	}
});

(_zkwg=_zkpk.Menupopup).prototype.className='zul.menu.Menupopup';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		tags = zk.ie || zk.gecko ? 'a' : 'button'; 
	out.push('<div', this.domAttrs_(), '><', tags, ' id="', uuid,
			'$a" tabindex="-1" onclick="return false;" href="javascript:;"',
			' style="padding:0 !important; margin:0 !important; border:0 !important;',
			' background: transparent !important; font-size: 1px !important;',
			' width: 1px !important; height: 1px !important;-moz-outline: 0 none;',
			' outline: 0 none;	-moz-user-select: text; -khtml-user-select: text;"></',
			tags, '><ul class="', zcls, '-cnt" id="', uuid, '$cave">');
			
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
		
	out.push('</ul></div>');
}

zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}_z='zul.panel';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zPkg.load('zul.wgt');
zul.panel.Panel = zk.$extends(zul.Widget, {
	_border: "none",
	_title: "",
	_open: true,
	
	$init: function () {
		this.$supers('$init', arguments);
		this.listen('onClose', this, null, -1000);
		this.listen('onMove', this, null, -1000);
	},
	setVisible: function (visible, fromServer) {
		if (this._visible != visible) {
			/** TODO
			 * if (this.isMaximized()) {
				zkPanel.maximize(cmp, false);
			} else if (this.isMinimized()) {
				zkPanel.minimize(cmp, false);
			}*/
			this.$supers('setVisible', arguments);
		}
	},
	setHeight: function () {
		this.$supers('setHeight', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
	setWidth: function () {
		this.$supers('setWidth', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
	setTop: function () {
		this._hideShadow();
		this.$supers('setTop', arguments);
		this._syncShadow();
		
	},
	setLeft: function () {
		this._hideShadow();
		this.$supers('setLeft', arguments);
		this._syncShadow();		
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
	isOpen: function () {
		return this._open;
	},
	setOpen: function (open, fromServer) {
		if (this._open != open) {
			this._open = open;
			var node = this.getNode();
			if (node) {
				var zcls = this.getZclass(),
					body = this.getSubnode('body');
				if (body) {
					if (open) {
						zDom.rmClass(node, zcls + '-collapsed');
						zAnima.slideDown(this, body, {
							afterAnima: this._afterSlideDown
						});
					} else {
						zDom.addClass(node, zcls + '-collapsed');
						this._hideShadow();
						
						// windows 2003 with IE6 will cause an error when user toggles the panel in portallayout.
						if (zk.ie6Only && !node.style.width)
							node.runtimeStyle.width = "100%";
							
						zAnima.slideUp(this, body, {
							beforeAnima: this._beforeSlideUp
						});
					}
				}
				if (fromServer) this.fire('onOpen', open);
			}
		}
	},
	isFramable: function () {
		return this._framable;
	},
	setFramable: function (framable) {
		if (this._framable != framable) {
			this._framable = framable;
			this.rerender();
		}
	},
	setMovable: function (movable) {
		if (this._movable != movable) {
			this._movable = movable;
			this.rerender();
		}
	},
	isMovable: function () {
		return this._movable;
	},
	isFloatable: function () {
		return this._floatable;
	},
	setFloatable: function (floatable) {
		if (this._floatable != floatable) {
			this._floatable = floatable;
			this.rerender();
		}
	},
	isMaximized: function () {
		return this._maximized;
	},
	setMaximized: function (maximized) {
		if (this._maximized != maximized) {
			this._maximized = maximized;
			// TODO
		}
	},
	isMaximizable: function () {
		return this._maximizable;
	},
	setMaximizable: function (maximizable) {
		if (this._maximizable != maximizable) {
			this._maximizable = maximizable;
			this.rerender();
		}
	},
	isMinimized: function () {
		return this._minimized;
	},
	setMinimized: function (minimized, fromServer) {
		if (this._minimized != minimized) {
			this._minimized = minimized;
			/** TODO 
			 * if (_minimized) {
				_maximized = false;
				setVisible0(false); //avoid dead loop
			} else setVisible0(true);
			*/
			var node = this.getNode();
			if (node) {
				var s = node.style, l = s.left, t = s.top, w = s.width, h = s.height;
				if (minimized) {
					zWatch.fireDown('onHide', {visible:true}, this);
					zDom.hide(node);
				} else {
					zDom.show(node);
					zWatch.fireDown('onVisible', {visible:true}, this);
				}
				if (!fromServer) {
					var wgt = this;
					this.fire('onMinimize', {
						left: s.left,
						top: s.top,
						width: s.width,
						height: s.height,
						minimized: minimized,
						marshal: wgt.$class._onMinimizeMarshal
					});
				}
			}
		}
	},
	isMinimizable: function () {
		return this._minimizable;
	},
	setMinimizable: function (minimizable) {
		if (this._minimizable != minimizable) {
			this._minimizable = minimizable;
			this.rerender();
		}
	},
	isCollapsible: function () {
		return this._collapsible;
	},
	setCollapsible: function (collapsible) {
		if (this._collapsible != collapsible) {
			this._collapsible = collapsible;
			this.rerender();
		}
	},
	isClosable: function () {
		return this._closable;
	},
	setClosable: function (closable) {
		if (this._closable != closable) {
			this._closable = closable;
			this.rerender();
		}
	},
	getBorder: function () {
		return this._border;
	},
	setBorder: function (border) {
		if (!border || '0' == border)
			border = "none";
		if (this._border != border) {
			this._border = border;
			this.rerender();
		}
	},
	getTitle: function () {
		return this._title;
	},
	setTitle: function (title) {
		if (!title) title = "";
		if (this._title != title) {
			this._title = title;
			this.rerender();
		}
	},
	addToolbar: function (name, toolbar) {
		switch (name) {
			case 'tbar':
				this.tbar = toolbar;
				break;
			case 'bbar':
				this.bbar = toolbar;
				break;
			case 'fbar':
				this.fbar = toolbar;
				break;
			default: return false; // not match
		}
		return this.appendChild(toolbar);
	},
	//event handler//
	onClose: function () {
		if (!this.inServer) //let server handle if in server
			this.parent.removeChild(this); //default: remove
	},
	onMove: function (evt) {
		this._left = evt.data[0];
		this._top = evt.data[1];
	},
	//watch//
	onSize: _zkf = function () {
		this._hideShadow();
		if (this.isMaximized()) {
			/** TODO 
			 * if (this.__maximized)
				this._syncMaximized();
			this.__maximized = false; // avoid deadloop
			*/
		}
		this._fixHgh();
		this._fixWdh();
		this._syncShadow();
	},
	onVisible: _zkf,
	onHide: function () {
		this._hideShadow();
	},
	_fixWdh: zk.ie7 ? function () {
		if (!this.isFramable() || !this.panelchildren) return;
		var n = this.getNode(),
			cm = this.panelchildren.getNode().parentNode
			wdh = n.style.width,
			tl = zDom.firstChild(n, "DIV"),
			bl = zDom.lastChild(zDom.lastChild(n, "DIV"), "DIV");
			
		if (!wdh || wdh == "auto") {
			var diff = zDom.padBorderWidth(cm.parentNode) + zDom.padBorderWidth(cm.parentNode.parentNode);
			if (tl) {
				tl.firstChild.firstChild.style.width = 
					Math.max(0, cm.offsetWidth - (zDom.padBorderWidth(tl) + zDom.padBorderWidth(tl.firstChild) - diff)) + "px";
			}
			if (bl) {
				bl.firstChild.firstChild.style.width =
					Math.max(0, cm.offsetWidth - (zDom.padBorderWidth(bl) + zDom.padBorderWidth(bl.firstChild) - diff)) + "px";
			}
		} else {
			if (tl) tl.firstChild.firstChild.style.width = "";
			if (bl) bl.firstChild.firstChild.style.width = "";
		}
	} : zk.$void,
	_fixHgh: function () {
		if (!this.panelchildren || !this.isRealVisible()) return;
		var n = this.getNode(),
			body = this.panelchildren.getNode(),
			hgh = n.style.height;
		if (zk.ie6Only && ((hgh && hgh != "auto" )|| body.style.height)) body.style.height = "0px";
		if (hgh && hgh != "auto")
			body.style.height = zDom.revisedHeight(body, n.offsetHeight - this._padBorderHeight(n) - 1, true) + 'px';
	},
	_padBorderHeight: function (n) {
		var h = zDom.padBorderHeight(n) + this._titleHeight(n),
			tbar = this.getSubnode('tbar'), bbar = this.getSubnode('bbar');
	    if (this.isFramable()) {
			var body = this.getSubnode('body'),
				ft = zDom.lastChild(body, "DIV"),
				title = this.getSubnode('cap');
	        h += ft.offsetHeight;
			if (this.panelchildren)
				h += zDom.padBorderHeight(this.panelchildren.getNode().parentNode);
			if (title)
		        h += zDom.padBorderHeight(title.parentNode);
	    } else {
			var fbar = this.getSubnode('fbar');
			if (fbar) h += fbar.offsetHeight;
		}
		if (tbar) h += tbar.offsetHeight;
		if (bbar) h += bbar.offsetHeight;
	    return h;
	},
	_titleHeight: function (n) {
		var cap = this.getSubnode('cap');
		return cap ? cap.offsetHeight : 
				this.isFramable() ?
					zDom.firstChild(n, "DIV").firstChild.firstChild.offsetHeight : 0;
	},
	_syncMaximized: function (cmp) {
		/** TODO 
		 * if (!this._lastSize) return;
		var floated = zkPanel.isFloatable(cmp), op = floated ? zPos.offsetParent(cmp) : cmp.parentNode,
			s = cmp.style;
			
		// Sometimes, the clientWidth/Height in IE6 is wrong. 
		var sw = zk.ie6Only && op.clientWidth == 0 ? (op.offsetWidth - zk.sumStyles(op, "rl", zk.borders)) : op.clientWidth;
		if (!floated) {
			sw -= zk.sumStyles(op, "rl", zk.paddings);
			sw = zk.revisedSize(cmp, sw);
		}
		if (sw < 0) sw = 0;
		s.width = sw + "px";
		if (getZKAttr(cmp, "open") == "true") {
			var sh = zk.ie6Only && op.clientHeight == 0 ? (op.offsetHeight - zk.sumStyles(op, "tb", zk.borders)) : op.clientHeight;
			if (!floated) {
				sh -= zk.sumStyles(op, "tb", zk.paddings);
				sh = zk.revisedSize(cmp, sh, true);
			}
			if (sh < 0) sh = 0;
			s.height = sh + "px";
		}*/
	},
	onFloatUp: function (wgt) {
		if (!this.isVisible() || !this.isFloatable())
			return; //just in case

		for (; wgt; wgt = wgt.parent) {
			if (wgt == this) {
				this.setTopmost();
				return;
			}
			if (wgt.isFloating_())
				return;
		}
	},
	getZclass: function () {
		return this._zclass == null ?  "z-panel" : this._zclass;
	},
	_afterSlideDown: function (n) {
		zWatch.fireDown("onVisible", {visible:true}, this);
	},
	_beforeSlideUp: function (n) {
		zWatch.fireDown("onHide", {visible:true}, this);
	},
	_initFloat: function () {
		var n = this.getNode();
		if (!n.style.top && !n.style.left) {		
			var xy = zDom.revisedOffset(n);
			n.style.left = xy[0] + "px";
			n.style.top = xy[1] + "px";
		}
		
		n.style.position = "absolute";
		if (this.isMovable())
			this._initMove();
			
		this._syncShadow();
		
		if (this.isRealVisible()) {
			zDom.cleanVisibility(n);
			this.setTopmost();
		}
	},
	_initMove: function (cmp) {
		var handle = this.getSubnode('cap');
		if (handle && !this._drag) {
			handle.style.cursor = "move";
			var $Panel = this.$class;
			this._drag = new zk.Draggable(this, null, {
				handle: handle, overlay: true, stackup: true,
				starteffect: $Panel._startmove,
				ignoredrag: $Panel._ignoremove,
				endeffect: $Panel._aftermove});
		}
	},
	_syncShadow: function () {
		if (!this.isFloatable()) {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = null;
			}
		} else {
			if (!this._shadow)
				this._shadow = new zk.eff.Shadow(this.getNode(),
					{left: -4, right: 4, top: -2, bottom: 3, stackup:true});
			this._shadow.sync();
		}
	},
	_hideShadow: function () {
		var shadow = this._shadow;
		if (shadow) shadow.hide();
	},
	//super//
	bind_: function () {
		this.$supers('bind_', arguments);
		
		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
		zWatch.listen("onHide", this);
		
		var uuid = this.uuid,
			$Panel = this.$class;		
		
		if (this.isFloatable()) {
			zWatch.listen('onFloatUp', this);
			this.setFloating_(true);
			this._initFloat();
		}
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);
		zWatch.unlisten('onFloatUp', this);
		zWatch.unlisten("onHide", this);
		this.setFloating_(false);
		
		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = null;
		}
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}	
		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			this.fire('onClose');
			evt.stop();
			break;
		case this.getSubnode('max'):
			// TODO
			break;
		case this.getSubnode('min'):
			if (this.isMinimizable())
				this.setMinimized(!this.isMinimized());
			break;
		case this.getSubnode('toggle'):
			if (this.isCollapsible())
				this.setOpen(!this.isOpen());
			break;
		}
		this.$supers('doClick_', arguments);
	},
	doMouseOver_: function (evt) {
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			zDom.addClass(this.getSubnode('close'), this.getZclass() + '-close-over');
			break;
		case this.getSubnode('max'):
			var zcls = this.getZclass(),
				added = this.isMaximized() ? ' ' + zcls + '-maximized-over' : '';
			zDom.addClass(this.getSubnode('max'), zcls + '-maximize-over' + added);
			break;
		case this.getSubnode('min'):
			zDom.addClass(this.getSubnode('min'), this.getZclass() + '-minimize-over');
			break;
		case this.getSubnode('toggle'):
			zDom.addClass(this.getSubnode('toggle'), this.getZclass() + '-toggle-over');
			break;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			zDom.rmClass(this.getSubnode('close'), this.getZclass() + '-close-over');
			break;
		case this.getSubnode('max'):
			var zcls = this.getZclass(),
				max = this.getSubnode('max');
			if (this.isMaximized())
				zDom.rmClass(max, zcls + '-maximized-over');
			zDom.rmClass(max, zcls + '-maximize-over');
			break;
		case this.getSubnode('min'):
			zDom.rmClass(this.getSubnode('min'), this.getZclass() + '-minimize-over');
			break;
		case this.getSubnode('toggle'):
			zDom.rmClass(this.getSubnode('toggle'), this.getZclass() + '-toggle-over');
			break;
		}
		this.$supers('doMouseOut_', arguments);
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var zcls = this.getZclass();
			var added = "normal" == this.getBorder() ? '' : zcls + '-noborder';
			if (added) scls += (scls ? ' ': '') + added;
			addded = this.isOpen() ? '' : zcls + '-collapsed';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Caption))
			this.caption = child;
		else if (child.$instanceof(zul.panel.Panelchildren))
			this.panelchildren = child;
		else if (child.$instanceof(zul.wgt.Toolbar)) {
			if (this.firstChild == child)
				this.tbar = child;
			else if (this.lastChild == child && child.previousSibling.$instanceof(zul.wgt.Toolbar))
				this.fbar = child;
			else if (child.previousSibling.$instanceof(zul.panel.Panelchildren))
				this.bbar = child;
		}
		this.rerender();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.caption)
			this.caption = null;
		else if (child == this.panelchildren)
			this.panelchildren = null;
		else if (child == this.tbar)
			this.tbar = null;
		else if (child == this.bbar)
			this.bbar = null;
		else if (child == this.fbar)
			this.fbar = null;
		this.rerender();
	}
}, {
	//drag
	_startmove: function (dg) {
		dg.control._hideShadow();
		//Bug #1568393: we have to change the percetage to the pixel.
		var el = dg.node;
		if(el.style.top && el.style.top.indexOf("%") >= 0)
			 el.style.top = el.offsetTop + "px";
		if(el.style.left && el.style.left.indexOf("%") >= 0)
			 el.style.left = el.offsetLeft + "px";
		//zkau.closeFloats(cmp, handle);
	},
	_ignoremove: function (dg, pointer, evt) {
		var wgt = dg.control;			
		switch (zEvt.target(evt)) {
			case wgt.getSubnode('close'):
			case wgt.getSubnode('max'):
			case wgt.getSubnode('min'):
			case wgt.getSubnode('toggle'):
				return true; //ignore special buttons
		}
		return false;
	},
	_aftermove: function (dg, evt) {
		var wgt = dg.control;
		wgt._syncShadow();
		var keys = zEvt.keyMetaData(evt),
			node = wgt.getNode(),
			x = zk.parseInt(node.style.left),
			y = zk.parseInt(node.style.top);
		wgt.fire('onMove', {
			x: x + 'px',
			y: y + 'px',
			keys: keys,
			marshal: wgt.$class._onMoveMarshal
		}, {ignorable: true});
	},
	_onMoveMarshal: function () {
		return [this.x, this.y, this.keys ? this.keys.marshal(): ''];
	},
	_onMinimizeMarshal: function(){
		return [this.left, this.top, this.width, this.height, this.minimized];
	}
});

(_zkwg=_zkpk.Panel).prototype.className='zul.panel.Panel';_zkmd={};
_zkmd['default']=
function (out, skipper) {
	var zcls = this.getZclass(),
		uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption,
		framable = this.isFramable(),
		noborder = this.getBorder() != 'normal';
		
	out.push('<div', this.domAttrs_(), '>');
	
	if (framable) {
		out.push('<div class="', zcls, '-tl');
		if (!caption && !title)
			out.push(' ', zcls, '-noheader');
		out.push('"><div class="', zcls, '-tr"><div class="', zcls, '-tm">');
	}
	if (caption || title) {
		out.push('<div id="', uuid, '$cap" class="', zcls, '-header');
		
		if (!framable && noborder)
			out.push(' ', zcls, '-header-noborder');
			
		out.push('">');
		if (!caption) {
			if (this.isClosable())
				out.push('<div id="', uuid, '$close" class="', zcls, '-tool ',
						zcls, '-close"></div>');
			if (this.isMaximizable()) {
				out.push('<div id="', uuid, '$max" class="', zcls, '-tool ',
						zcls, '-maximize');
				if (this.isMaximized())
					out.push(' ', zcls, '-maximized');
				out.push('"></div>');
			}
			if (this.isMinimizable())
				out.push('<div id="', uuid, '$min" class="', zcls, '-tool ',
						zcls, '-minimize"></div>');
			if (this.isCollapsible())
				out.push('<div id="', uuid, '$toggle" class="', zcls, '-tool ',
						zcls, '-toggle"></div>');
			out.push(zUtl.encodeXML(title));
		} else caption.redraw(out);
		
		out.push('</div>');
	}
	if (framable) out.push('</div></div></div>');
	
	out.push('<div id="', uuid, '$body" class="', zcls, '-body"');
	
	if (!this.isOpen()) out.push(' style="display:none;"');
	
	out.push('>');
	
	if (framable) 
		out.push('<div class="', zcls, '-cl"><div class="', zcls,
				'-cr"><div class="', zcls, '-cm">');
	if (this.tbar) {
		out.push('<div id="', uuid, '$tbar" class="', zcls, '-tbar');
		
		if (noborder)
			out.push(' ', zcls, '-tbar-noborder');
		
		if (framable && !caption && !title)
			out.push(' ', zcls, '-noheader');
		
		out.push('">');
		this.tbar.redraw(out);
		out.push('</div>');
	}
	if (this.panelchildren)
		this.panelchildren.redraw(out);
		
	if (this.bbar) {
		out.push('<div id="', uuid, '$bbar" class="', zcls, '-bbar');
		
		if (noborder)
			out.push(' ', zcls, '-bbar-noborder');
			
		if (framable && !caption && !title)
			out.push(' ', zcls, '-noheader');
		
		out.push('">');
		this.bbar.redraw(out);
		out.push('</div>');
	}
	
	if (framable) {
		out.push('</div></div></div><div class="', zcls, '-bl');
		
		if (!this.fbar) out.push(' ', zcls, '-nofbar');
		
		out.push('"><div class="', zcls, '-br"><div class="', zcls, '-bm">');
	}
	if (this.fbar) {
		out.push('<div id="', uuid, '$fbar" class="', zcls, '-fbar">');
		this.fbar.redraw(out);
		out.push('</div>');
	}
	if (framable) out.push('</div></div></div>');
	out.push('</div></div>');
}
zkmld(_zkwg,_zkmd);
zul.panel.Panelchildren = zk.$extends(zul.Widget, {
	setHeight: zk.$void,      // readonly
	setWidth: zk.$void,       // readonly

	// super
	getZclass: function () {
		return this._zclass == null ?  "z-panel-children" : this._zclass;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var zcls = this.getZclass();
			var added = !this.parent.getTitle() && !this.parent.caption ?
				zcls + '-noheader' : '';				
			if (added) scls += (scls ? ' ': '') + added;
			added = this.parent.getBorder() == 'normal' ? '' : zcls + '-noborder';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', null, this.parent);
			zWatch.fireDown('onSize', null, this.parent);
		}
	}
});
(_zkwg=_zkpk.Panelchildren).prototype.className='zul.panel.Panelchildren';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<div', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div>');
}

zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}_z='zul.utl';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.utl.Script = zk.$extends(zk.Widget, {
	/** Returns the content of JavaScript codes to execute.
	 */
	getContent: function () {
		return this._content;
	},
	/** Sets the content of JavaScript code snippet to execute.
	 * <p>Note: the codes won't be evaluated until it is attached the
	 * DOM tree. If it is attached, the JavaScript code sippet is executed
	 * immediately. In additions, it executes only once.
	 * <p>When the JavaScript code snipped is executed, you can access
	 * this widget by use of <code>this</code>. In other words,
	 * it was executed in the context of a member method of this widget.
	 * @param cnt the content. It could be a String instance, or
	 * a Function instance.
	 */
	setContent: function (cnt) {
		this._content = cnt;
		if (cnt) {
			this._fn = typeof cnt == 'function' ? cnt: new Function(cnt);
			if (this.desktop) //check parent since no this.getNode()
				this._exec();
		} else
			this._fn = null;
	},
	/** Returns the src of the JavaScript file.
	 */
	getSrc: function () {
		return this._src;
	},
	/** Sets the src (URI) of the JavaScript file.
	 */
	setSrc: function (src) {
		this._src = src;
		if (src) {
			this._srcrun = false;
			if (this.desktop)
				this._exec();
		}
	},

	/** Returns the charset. */
	getCharset: function () {
		return this._charset;
	},
	/** Sets the charset. */
	setCharset: function (charset) {
		this._charset = charset;
	},

	_exec: function () {
		var pkgs = this.packages; //not visible to client (since meaningless)
		if (!pkgs) return this._exec0();

		this.packages = null; //only once
		pkgs = pkgs.split(',');
		for (var j = 0, l = pkgs.length; j < l;)
			zPkg.load(pkgs[j++].trim());

		if (zPkg.loading)
			zk.afterLoad(this.proxy(this._exec0));
		else
			this._exec0();
	},
	_exec0: function () {
		var wgt = this, fn = this._fn;
		if (fn) {
			this._fn = null; //run only once
			zk.afterMount(function () {fn.apply(wgt);});
		}
		if (this._src && !this._srcrun) {
			this._srcrun = true; //run only once
			zDom.appendScript(this._src, this._charset);
		}
	},

	//super//
	redraw: function () {
		return '';
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		this._exec();
	}
});

(_zkwg=_zkpk.Script).prototype.className='zul.utl.Script';
zul.utl.Timer = zk.$extends(zk.Widget, {
	_running: true,
	_delay: 0,

	isRepeats: function () {
		return this._repeats;
	},
	setRepeats: function (repeats) {
		if (this._repeats != repeats) {
			this._repeats = repeats;
			if (this.desktop) this._sync();
		}
	},
	getDelay: function () {
		return this._delay;
	},
	setDelay: function (delay) {
		if (this._delay != delay) {
			this._delay = delay;
			if (this.desktop) this._sync();
		}
	},
	isRunning: function () {
		return this._running;
	},
	setRunning: function (running) {
		if (this._running != running) {
			this._running = running;
			if (this.desktop) this._sync();
		}
	},
	play: function () {
		this.setRunning(true);
	},
	stop: function () {
		this.setRunning(false);
	},

	_sync: function () {
		this._stop();
		this._play();
	},
	_play: function () {
		if (this._running) {
			var fn = this.proxy(this._tmfn);
			if (this._repeats)
				this._iid = setInterval(fn, this._delay);
			else
				this._tid = setTimeout(fn, this._delay);
		}
	},
	_stop: function () {
		var id = this._iid;
		if (id) {
			this._iid = null;
			clearInterval(id)
		}
		id = this._tid;
		if (id) {
			this._tid = null;
			clearTimeout(id);
		}
	},
	_tmfn: function () {
		this.fire('onTimer', null, {ignorable: true});
	},

	//super//
	redraw: function () {
		return '';
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this._running) this._play();
	},
	unbind_: function () {
		this._stop();
		this.$supers('unbind_', arguments);
	}
});

(_zkwg=_zkpk.Timer).prototype.className='zul.utl.Timer';
zul.utl.Style = zk.$extends(zk.Widget, {
	getSrc: function () {
		return this._src;
	},
	setSrc: function (src) {
		if (this._src != src) {
			this._src = src;
			this._content = null;
			if (this.desktop) this._updLink();
		}
	},
	getContent: function () {
		return this._content;
	},
	setContent: function (content) {
		if (!zk.bootstrapping)
			throw "Content cannot be changed after bootstrapping";
		this._content = content;
	},

	//super//
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this._gened) this._gened = false; //<style> gened
		else this._updLink();
	},
	unbind_: function () {
		zDom.remove(this._getLink());
		this.$supers('unbind_', arguments);
	},
	_updLink: function () {
		var head = this._getHead(),
			ln = this._getLink(head),
			n = this.getNode();
		if (n) n.innerHTML = '';
		if (ln) ln.href = this._src;
		else {
			ln = document.createElement("LINK");
			ln.id = this.uuid;
			ln.rel = "stylesheet";
			ln.type = "text/css";
			ln.href = this._src;
			head.appendChild(ln);
		}
	},
	_getHead: function () {
		return head = document.getElementsByTagName("HEAD")[0];
	},
	_getLink: function (head) {
		head = head || this._getHead();
		for (var lns = head.getElementsByTagName("LINK"), j = lns.length,
		uuid = this.uuid; --j >= 0;)
			if (lns[j].id == uuid)
				return lns[j];
	},
	redraw: function (out) {
		//IE: unable to look back LINK or STYLE with ID
		if (zk.bootstrapping && this._content) {
			out.push('<span style="display:none" id="',
				this.uuid, '"><style type="text/css">\n',
				this._content, '\n</style></span>\n');
			this._gened = true;
		}
	}
});
(_zkwg=_zkpk.Style).prototype.className='zul.utl.Style';
}finally{zPkg.end(_z);}}_z='zul.wnd';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zPkg.load('zul.wgt');
zul.wnd.Window = zk.$extends(zul.Widget, {
	_mode: 'embedded',
	_border: 'none',
	_minheight: 100,
	_minwidth: 200,
	_zIndex: 0,

	$init: function () {
		this._fellows = {};

		this.$supers('$init', arguments);

		this.listen('onClose', this, null, -1000);
		this.listen('onMove', this, null, -1000);
		this.listen('onZIndex', this, null, -1000);
		this._skipper = new zul.wnd.Skipper(this);
	},

	getMode: function () {
		return this._mode;
	},
	setMode: function (mode) {
		if (this._mode != mode) {
			this._mode = mode;
			this._updateDomOuter();
		}
	},
	doOverlapped: function () {
		this.setMode('overlapped');
	},
	doPopup: function () {
		this.setMode('popup');
	},
	doHighlighted: function () {
		this.setMode('highlighted');
	},
	doModal: function () {
		this.setMode('modal');
	},
	doEmbedded: function () {
		this.setMode('embedded');
	},

	_doOverlapped: function () {
		var pos = this.getPosition(),
			n = this.getNode();
		if (!pos && !n.style.top && !n.style.left) {
			var xy = zDom.revisedOffset(n);
			n.style.left = xy[0] + "px";
			n.style.top = xy[1] + "px";
		} else if (pos == "parent")
			this._posByParent();

		zDom.makeVParent(n);
		this._syncShadow();
		this._updateDomPos();

		if (this.isRealVisible()) {
			zDom.cleanVisibility(n);
			this.setTopmost();
		}

		this._makeFloat();
	},
	_doModal: function () {
		var pos = this.getPosition(),
			n = this.getNode();
		if (pos == "parent") this._posByParent();

		zDom.makeVParent(n);
		this._syncShadow();
		this._updateDomPos(true);

		if (!pos) { //adjust y (to upper location)
			var top = zk.parseInt(n.style.top), y = zDom.innerY();
			if (y) {
				var y1 = top - y;
				if (y1 > 100) n.style.top = top - (y1 - 100) + "px";
			} else if (top > 100)
				n.style.top = "100px";
		}

		//Note: modal must be visible
		var realVisible = this.isRealVisible();
		if (realVisible) {
			zDom.cleanVisibility(n);
			this.setTopmost();
		}
		this._syncMask();

		this._mask = new zk.eff.FullMask({
			id: this.uuid + "$mask",
			anchor: this._shadow.getBottomElement(),
				//bug 1510218: we have to make it as a sibling
			zIndex: this._zIndex,
			stackup: zk.ie6Only,
			visible: realVisible});

		if (realVisible) {
			this._prevmodal = zk.currentModal;
			var modal = zk.currentModal = this;
			this._prevfocus = zk.currentFocus; //store
			this.focus(0);
		}

		this._makeFloat();
	},
	/** Must be called before calling makeVParent. */
	_posByParent: function () {
		var n = this.getNode(),
			ofs = zDom.revisedOffset(n.parentNode),
			left = zk.parseInt(n.style.left), top = zk.parseInt(n.style.top);
		this._offset = ofs;
		n.style.left = ofs[0] + zk.parseInt(n.style.left) + "px";
		n.style.top = ofs[1] + zk.parseInt(n.style.top) + "px";
	},
	_syncShadow: function () {
		if (this._mode == 'embedded') {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = null;
			}
		} else {
			if (!this._shadow)
				this._shadow = new zk.eff.Shadow(this.getNode(),
					{left: -4, right: 4, top: -2, bottom: 3, stackup:true});
			this._shadow.sync();
		}
	},
	_syncMask: function () {
		if (this._mask) this._mask.sync(this._shadow.getBottomElement());
	},
	_hideShadow: function () {
		var shadow = this._shadow;
		if (shadow) shadow.hide();
	},
	_makeFloat: function () {
		var handle = this.getSubnode('cap');
		if (handle && !this._drag) {
			handle.style.cursor = "move";
			var $Window = this.$class;
			this._drag = new zk.Draggable(this, null, {
				handle: handle, overlay: true, stackup: true,
				starteffect: $Window._startmove,
				ghosting: $Window._ghostmove,
				endghosting: $Window._endghostmove,
				ignoredrag: $Window._ignoremove,
				endeffect: $Window._aftermove});
		}
	},
	_updateDomPos: function (force) {
		var n = this.getNode(), pos = this._pos;
		if (pos == "parent"/*handled by the caller*/ || (!pos && !force))
			return;

		var st = n.style;
		st.position = "absolute"; //just in case
		var ol = st.left, ot = st.top;
		zDom.center(n, pos);
		var sdw = this._shadow;
		if (pos && sdw) {
			var opts = sdw.opts, l = n.offsetLeft, t = n.offsetTop; 
			if (pos.indexOf("left") >= 0 && opts.left < 0)
				st.left = (l - opts.left) + "px";
			else if (pos.indexOf("right") >= 0 && opts.right > 0)
				st.left = (l - opts.right) + "px";
			if (pos.indexOf("top") >= 0 && opts.top < 0)
				st.top = (t - opts.top) + "px";
			else if (pos.indexOf("bottom") >= 0 && opts.bottom > 0)
				st.top = (t - opts.bottom) + "px";
		}
		this._syncShadow();
		if (ol != st.left || ot != st.top)
			this._fireOnMove();
	},

	getTitle: function () {
		return this._title;
	},
	setTitle: function (title) {
		if (this._title != title) {
			this._title = title;
			this._updateDomOuter();
		}
	},
	getBorder: function () {
		return this._border;
	},
	setBorder: function (border) {
		if (!border || '0' == border)
			border = "none";
		if (this._border != border) {
			this._border = border;
			this._updateDomOuter();
		}
	},
	getPosition: function () {
		return this._pos;
	},
	setPosition: function (pos) {
		if (this._pos != pos) {
			this._pos = pos;

			if (this.desktop && this._mode != 'embedded') {
				this._updateDomPos(); //TODO: handle pos = 'parent'
			}
		}
	},

	getMinheight: function () {
		return this._minheight;
	},
	setMinheight: function (minheight) {
		if (this._minheight != minheight) {
			this._minheight = minheight;

			//TODO
		}
	},
	getMinwidth: function () {
		return this._minwidth;
	},
	setMinwidth: function (minwidth) {
		if (this._minwidth != minwidth) {
			this._minwidth = minwidth;

			//TODO
		}
	},

	isClosable: function () {
		return this._closable;
	},
	setClosable: function (closable) {
		if (this._closable != closable) {
			this._closable = closable;
			this._updateDomOuter();
		}
	},
	isSizable: function () {
		return this._sizable;
	},
	setSizable: function (sizable) {
		if (this._sizable != sizable) {
			this._sizable = sizable;
			this._updateDomOuter();
		}
	},
	isMaximized: function () {
		return this._maximized;
	},
	setMaximized: function (maximized) {
		if (this._maximized != maximized) {
			this._maximized = maximized;
			this._updateDomOuter();
		}
	},
	isMaximizable: function () {
		return this._maximizable;
	},
	setMaximizable: function (maximizable) {
		if (this._maximizable != maximizable) {
			this._maximizable = maximizable;
			this._updateDomOuter();
		}
	},
	isMinimized: function () {
		return this._minimized;
	},
	setMinimized: function (minimized) {
		if (this._minimized != minimized) {
			this._minimized = minimized;
			this._updateDomOuter();
		}
	},
	isMinimizable: function () {
		return this._minimizable;
	},
	setMinimizable: function (minimizable) {
		if (this._minimizable != minimizable) {
			this._minimizable = minimizable;
			this._updateDomOuter();
		}
	},

	getContentStyle: function () {
		return this._cntStyle;
	},
	setContentStyle: function (style) {
		if (this._cntStyle != style) {
			this._cntStyle = style;
			this._updateDomOuter();
		}
	},
	getContentSclass: function () {
		return this._cntSclass;
	},
	setContentSclass: function (sclass) {
		if (this._cntSclass != sclass) {
			this._cntSclass = sclass;
			this._updateDomOuter();
		}
	},

	_updateDomOuter: function () {
		this.rerender(this._skipper);
	},

	//event handler//
	onClose: function () {
		if (!this.inServer) //let server handle if in server
			this.parent.removeChild(this); //default: remove
	},
	onMove: function (evt) {
		this._left = evt.data[0];
		this._top = evt.data[1];
	},
	onZIndex: function (evt) {
		this._syncShadow();
		this._syncMask();
	},
	//watch//
	onSize: _zkf = function () {
		this._hideShadow();
		if (this.isMaximized()) {
			/** TODO 
			 * if (this._maximized)
				this._syncMaximized();
			this._maximized = false; // avoid deadloop
			*/
		}
		this._fixHgh();
		this._fixWdh();
		this._syncShadow();
	},
	onVisible: _zkf,
	onFloatUp: function (wgt) {
		if (!this.isVisible() || this._mode == 'embedded')
			return; //just in case

		if (this._mode == 'popup') {
			for (var floatFound; wgt; wgt = wgt.parent) {
				if (wgt == this) {
					if (!floatFound) this.setTopmost();
					return;
				}
				floatFound = floatFound || wgt.isFloating_();
			}
			this.setVisible(false);
			this.fire('onOpen', false);
		} else
			for (; wgt; wgt = wgt.parent) {
				if (wgt == this) {
					this.setTopmost();
					return;
				}
				if (wgt.isFloating_())
					return;
			}
	},
	_fixWdh: zk.ie7 ? function () {
		if (this._mode == 'embedded' || this._mode == 'popup' || !this.isRealVisible()) return;
		var n = this.getNode(),
			cave = this.getSubnode('cave').parentNode,
			wdh = n.style.width,
			tl = zDom.firstChild(n, "DIV"),
			hl = tl && this.getSubnode("cap") ? zDom.nextSibling(tl, "DIV") : null,
			bl = zDom.lastChild(n, "DIV");
			
		if (!wdh || wdh == "auto") {
			var diff = zDom.padBorderWidth(cave.parentNode) + zDom.padBorderWidth(cave.parentNode.parentNode);
			if (tl) tl.firstChild.style.width = cave.offsetWidth + diff + "px";
			if (hl) hl.firstChild.firstChild.style.width = cave.offsetWidth
				- (zDom.padBorderWidth(hl) + zDom.padBorderWidth(hl.firstChild) - diff) + "px";
			if (bl) bl.firstChild.style.width = cave.offsetWidth + diff + "px";
		} else {
			if (tl) tl.firstChild.style.width = "";
			if (hl) hl.firstChild.style.width = "";
			if (bl) bl.firstChild.style.width = "";
		}
	} : zk.$void,
	_fixHgh: function () {
		if (!this.isRealVisible()) return;
		var n = this.getNode(),
			hgh = n.style.height
			cave = this.getSubnode('cave'),
			cvh = cave.style.height;
		if (hgh && hgh != "auto") {
			if (zk.ie6Only) cave.style.height = "0px";
			zDom.setOffsetHeight(cave, this._offsetHeight(n));
		} else if (cvh && cvh != "auto") {
			if (zk.ie6Only) cave.style.height = "0px";
			cave.style.height = "";
		}
	},
	_offsetHeight: function (n) {
		var h = n.offsetHeight - 1 - this._titleHeight(n);
		if(this._mode != 'embedded' && this._mode != 'popup') {
			var cave = this.getSubnode('cave'), bl = zDom.lastChild(n, "DIV"),
				cap = this.getSubnode("cap");
			h -= bl.offsetHeight;
			if (cave)
				h -= zDom.padBorderHeight(cave.parentNode);
			if (cap)
				h -= zDom.padBorderHeight(cap.parentNode);
		}
		return h - zDom.padBorderHeight(n);
	},
	_titleHeight: function (n) {
		var cap = this.getSubnode('cap'),
			tl = zDom.firstChild(n, "DIV");
		return cap ? cap.offsetHeight + tl.offsetHeight:
			this._mode != 'embedded' && this._mode != 'popup' ?
				zDom.nextSibling(tl, "DIV").offsetHeight: 0;
	},

	_fireOnMove: function (keys) {
		var pos = this._pos, node = this.getNode(),
			x = zk.parseInt(node.style.left),
			y = zk.parseInt(node.style.top);
		if (pos == 'parent') {
			var vparent = zDom.vparent(node);
			if (vparent) {
				var ofs = zDom.reviseOffset(vparent);
				x -= ofs[0];
				y -= ofs[1];
			}
		}
		this.fire('onMove', {
			x: x + 'px',
			y: y + 'px',
			keys: keys,
			marshal: zul.wnd.Window._onMoveMarshal
		}, {ignorable: true});
	},

	//super//
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop) {
			this._fixHgh();
			this._syncShadow();

			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this); // Note: IE6 is broken, because its offsetHeight doesn't update.
		}
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.desktop) {
			this._fixWdh();
			this._syncShadow();

			zWatch.fireDown('beforeSize', null, this);
			zWatch.fireDown('onSize', null, this);
		}
	},
	setDomVisible_: function () {
		this.$supers('setDomVisible_', arguments);
		this._syncShadow();
		this._syncMask();
	},
	setZIndex: function () {
		this.$supers('setZIndex', arguments);
		this._syncShadow();
		this._syncMask();
	},
	focus: function (timeout) {
		if (this.desktop) {
			var cap = this.caption;
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w != cap && w.focus(timeout))
					return true;
			return cap && cap.focus(timeout);
		}
		return false;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-window-" + this._mode;
	},

	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Caption))
			this.caption = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.caption)
			this.caption = null;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments),
			visible = this.isVisible();
		if ((!no || !no.visible) && visible && this.isMinimized())
			style = 'display:none;'+style;
		if (this._mode != 'embedded')
			style = (visible ? "position:absolute;visibility:hidden;" : "position:absolute;")
				+style;
		return style;
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		var $Window = this.$class;

		var mode = this._mode;
		zWatch.listen('onSize', this);
		zWatch.listen('onVisible', this);
		if (mode != 'embedded') {
			zWatch.listen('onFloatUp', this);
			this.setFloating_(true);

			if (mode == 'modal' || mode == 'highlighted') this._doModal();
			else this._doOverlapped();
		}
	},
	unbind_: function () {
		var node = this.getNode();
		node.style.visibility = 'hidden'; //avoid unpleasant effect

		//we don't check this._mode here since it might be already changed
		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = null;
		}
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		if (this._mask) {
			this._mask.destroy();
			this._mask = null;
		}
		
		zDom.undoVParent(node);
		zWatch.unlisten('onFloatUp', this);
		zWatch.unlisten('onSize', this);
		zWatch.unlisten('onVisible', this);
		this.setFloating_(false);

		if (zk.currentModal == this) {
			zk.currentModal = this._prevmodal;
			var prevfocus = this._prevfocus;
			if (prevfocus) prevfocus.focus(0);
			this._prevfocus = this._prevmodal = null;
		}

		var $Window = this.$class;
		for (var nms = ['close', 'max', 'min'], j = 3; --j >=0;) {
			var nm = nms[j],
				n = this['e' + nm ];
			if (n) {
				this['e' + nm ] = null;
				zEvt.unlisten(n, 'click', $Window[nm + 'click']);
				zEvt.unlisten(n, 'mouseover', $Window[nm + 'over']);
				zEvt.unlisten(n, 'mouseout', $Window[nm + 'out']);
			}
		}
		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			this.fire('onClose');
			break;
		case this.getSubnode('max'):
			// TODO
			break;
		case this.getSubnode('min'):
			// TODO 
			// if (this.isMinimizable())
			//	this.setMinimized(!this.isMinimized());
			break;
		}
		evt.stop();
		this.$supers('doClick_', arguments);
	},
	doMouseOver_: function (evt) {
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			zDom.addClass(this.getSubnode('close'), this.getZclass() + '-close-over');
			break;
		case this.getSubnode('max'):
			var zcls = this.getZclass(),
				added = this.isMaximized() ? ' ' + zcls + '-maxd-over' : '';
			zDom.addClass(this.getSubnode('max'), zcls + '-max-over' + added);
			break;
		case this.getSubnode('min'):
			zDom.addClass(this.getSubnode('min'), this.getZclass() + '-min-over');
			break;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		switch (evt.nativeTarget) {
		case this.getSubnode('close'):
			zDom.rmClass(this.getSubnode('close'), this.getZclass() + '-close-over');
			break;
		case this.getSubnode('max'):
			var zcls = this.getZclass(),
				max = this.getSubnode('max');
			if (this.isMaximized())
				zDom.rmClass(max, zcls + '-maxd-over');
			zDom.rmClass(max, zcls + '-max-over');
			break;
		case this.getSubnode('min'):
			zDom.rmClass(this.getSubnode('min'), this.getZclass() + '-min-over');
			break;
		}
		this.$supers('doMouseOut_', arguments);
	}
},{ //static
	_onMoveMarshal: function () {
		return [this.x, this.y, this.keys ? this.keys.marshal(): ''];
	},

	//drag
	_startmove: function (dg) {
		//Bug #1568393: we have to change the percetage to the pixel.
		var el = dg.node;
		if(el.style.top && el.style.top.indexOf("%") >= 0)
			 el.style.top = el.offsetTop + "px";
		if(el.style.left && el.style.left.indexOf("%") >= 0)
			 el.style.left = el.offsetLeft + "px";
		//TODO zkau.closeFloats();
	},
	_ghostmove: function (dg, ofs, evt) {
		var wnd = dg.control,
			el = dg.node;
		wnd._hideShadow();
		var top = zDom.firstChild(el, "DIV"),
			header = zDom.nextSibling(top, 'DIV'),
			fakeT = top.cloneNode(true),
			fakeH = header.cloneNode(true),
			html = '<div id="zk_wndghost" class="z-window-move-ghost" style="position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zDom.offsetWidth(el)+'px;height:'+zDom.offsetHeight(el)
			+'px;z-index:'+el.style.zIndex+'"><dl></dl></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		dg._wndoffs = ofs;
		el.style.visibility = "hidden";
		var h = el.offsetHeight - top.offsetHeight - header.offsetHeight;
		el = zDom.$("zk_wndghost");
		el.firstChild.style.height = zDom.revisedHeight(el.firstChild, h) + "px";
		el.insertBefore(fakeT, el.firstChild);
		el.insertBefore(fakeH, el.lastChild);
		return el;
	},
	_endghostmove: function (dg, origin) {
		var el = dg.node; //ghost
		origin.style.top = origin.offsetTop + el.offsetTop - dg._wndoffs[1] + "px";
		origin.style.left = origin.offsetLeft + el.offsetLeft - dg._wndoffs[0] + "px";

		document.body.style.cursor = "";
	},
	_ignoremove: function (dg, pointer, evt) {
		var el = dg.node,
			wgt = dg.control;
		switch (zEvt.target(evt)) {
			case wgt.getSubnode('close'):
			case wgt.getSubnode('max'):
			case wgt.getSubnode('min'):
				return true; //ignore special buttons
		}
		if (!wgt.isSizable()
		|| (el.offsetTop + 4 < pointer[1] && el.offsetLeft + 4 < pointer[0] 
		&& el.offsetLeft + el.offsetWidth - 4 > pointer[0]))
			return false; //accept if not sizable or not on border
		return true;
	},
	_aftermove: function (dg, evt) {
		dg.node.style.visibility = "";
		var wgt = dg.control;
		wgt._syncShadow();
		wgt._fireOnMove(zEvt.keyMetaData(evt));
	}
});

zul.wnd.Skipper = zk.$extends(zk.Skipper, {
	$init: function (wnd) {
		this._w = wnd;
	},
	restore: function () {
		this.$supers('restore', arguments);
		var w = this._w;
		if (w._mode != 'embedded') {
			w._updateDomPos(); //skipper's size is wrong in bind_
			w._syncShadow();
		}
	}
});

(_zkwg=_zkpk.Window).prototype.className='zul.wnd.Window';_zkmd={};
_zkmd['default']=
function (out, skipper) {
	var zcls = this.getZclass(),
		uuid = this.uuid,
		title = this.getTitle(),
		caption = this.caption,
		contentStyle = this.getContentStyle(),
		contentSclass = this.getContentSclass(),
		mode = this.getMode(),
		withFrame = 'embedded' != mode && 'popup' != mode,
		noborder = 'normal' != this.getBorder() ? '-noborder' : '';
		
	out.push('<div', this.domAttrs_(), '>');

	if (caption || title) {
		out.push('<div class="', zcls, '-tl"><div class="',
			zcls, '-tr"></div></div><div class="',
			zcls, '-hl"><div class="', zcls,
			'-hr"><div class="', zcls, '-hm"><div id="',
			uuid, '$cap" class="', zcls, '-header">');

		if (caption) caption.redraw(out);
		else {
			if (this.isClosable())
				out.push('<div id="', uuid, '$close" class="', zcls, '-icon ', zcls, '-close"></div>');
			if (this.isMaximizable()) {
				out.push('<div id="', uuid, '$max" class="', zcls, '-icon ', zcls, '-max');
				if (this.isMaximized())
					out.push(' ', zcls, '-maxd');
				out.push('"></div>');
			}
			if (this.isMinimizable())
				out.push('<div id="' + uuid, '$min" class="', zcls, '-icon ', zcls, '-min"></div>');
			out.push(zUtl.encodeXML(title));
		}
		out.push('</div></div></div></div>');
	} else if (withFrame)
		out.push('<div class="', zcls, '-tl', noborder,
				'"><div class="', zcls, '-tr', noborder, '"></div></div>');

	if (withFrame)
		out.push('<div class="', zcls, '-cl', noborder,
			'"><div class="', zcls, '-cr', noborder,
			'"><div class="', zcls, '-cm', noborder, '">');

	out.push('<div id="', uuid, '$cave" class="');
	if (contentSclass) out.push(contentSclass, ' ');
	out.push(zcls, '-cnt', noborder, '"');
	if (contentStyle) out.push(' style="', contentStyle, '"');
	out.push('>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != caption)
				w.redraw(out);

	out.push('</div>');

	if (withFrame)
		out.push('</div></div></div><div class="', zcls, '-bl', noborder,
			'"><div class="', zcls, '-br', noborder, '"></div></div>');

	out.push('</div>');
}
zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}_z='zul.tab';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.tab.Tabbox = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	_tabscroll: true,
	getTabs: function () {
		//The tabs must in index 0
		return this.getChildAt(0);
	},
	getTabpanels: function () {
		//The tabpanels must in index 1
		return this.getChildAt(1);
	},
	isTabscroll: function() {
		return this._tabscroll;
	},
	setTabscroll: function(tabscroll) {
		if (this._tabscroll != tabscroll) {
			this._tabscroll = tabscroll;
			this.rerender();
		}
	},
	getZclass: function () {
		return this._zclass == null ? "z-tabbox" +
			( this.inAccordionMold() ? "-" + this.getMold() : this.isVertical() ? "-ver" : "") : this._zclass;
	},
	setOrient: function(orient) {
		if ("horizontal" == orient || "vertical" == orient || !this.inAccordionMold()) {
			if (this._orient != orient) {
				this._orient = orient;
				this.rerender();
			}
		}
	},
	getOrient: function () {
		return this._orient;
	},
	isHorizontal: function() {
		return "horizontal" == this.getOrient();
	},
	isVertical: function() {
		return "vertical" == this.getOrient();
	},
	inAccordionMold: function () {
		return this.getMold().indexOf("accordion") < 0 ? false : true;
	},
	getSelectedIndex: function() {
		var tabnode = zDom.$(this._seltab),
		    tab = zk.Widget.$(tabnode);
		return tab != null ? tab.getIndex() : -1 ;
	},
	setSelectedIndex: function(index) {
		var tabs = this.getTabs();
		if (!tabs) return;
		this.setSelectedTab(tabs.getChildAt(index));
	},
	getSelectedPanel: function() {
		var tabnode = zDom.$(this._seltab),
		    tab = zk.Widget.$(tabnode);
		return tab != null ? tab.getLinkedPanel() : null;
	},
	setSelectedPanel: function(panel) {
		if (panel != null && panel.getTabbox() != this)
			return
		var tab = panel.getLinkedTab();
		if (!tab) return
		this.setSelectedTab(tab);
	},
	getSelectedTab: function() {
		var tabnode = zDom.$(this._seltab);
		return zk.Widget.$(tabnode);
	},
	setSelectedTab: function(tab) {
        if (zul.tab.Tab.isInstance(tab))
            tab = tab.uuid;
        if (this._selTab != tab) {
            this._selTab = tab;
            var wgt = zk.Widget.$(tab);
            if (wgt) {
                wgt.setSelected(true);
            }
        }
	},
	getPanelSpacing: function() {
		return this._panelSpacing;
	},
	setPanelSpacing: function(panelSpacing) {
		if (panelSpacing != null && panelSpacing.length == 0)
			panelSpacing = null;
		if (this._panelSpacing != panelSpacing) {
			this._panelSpacing = panelSpacing;
			this.rerender();
		}
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		this.tabs = this.getTabs();
		this.tabpanels = this.getTabpanels();
//		if (this.inAccordionMold()) {
//			zDom.cleanVisibility(this.getNode());
//		}
		zk.afterMount(
			this.proxy(function () {
				if (this.inAccordionMold()) {
					;
				} else {
					var x = this._selTab, wgt = zDom.$(x), tab = zk.Widget.$(wgt);
					tab.setSelected(true);
				}
			})
		);
	}
});
(_zkwg=_zkpk.Tabbox).prototype.className='zul.tab.Tabbox';_zkmd={};
_zkmd['accordion-lite']=
function (out) {
	out.push('<div ', this.domAttrs_(), '>');
	var tps = this.getTabpanels();
	if (tps) {
		tps.redraw(out);
	}
	out.push("</div>");
}
_zkmd['default']=
function (out) {
	out.push('<div ', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push("</div>");
}
_zkmd['accordion']=
function (out) {
	out.push('<div ', this.domAttrs_(), '>');
	var tps = this.getTabpanels();
	if (tps) {
		tps.redraw(out);
	}
	out.push("</div>");
}
zkmld(_zkwg,_zkmd);
zul.tab.Tabs = zk.$extends(zul.Widget, {
	getTabbox: function() {
		return this.parent ? this.parent : null;
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabs" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : ""):
		this._zclass;
	},
	onSize: _zkf = function () {
		var tabbox = this.getTabbox();
		if (tabbox.getNode())
			zDom.cleanVisibility(tabbox.getNode());
	},
	onVisible: _zkf, 
	insertChildHTML_: function (child, before, desktop) {
		var last = child.previousSibling;
		if (before || !last) {
			zDom.insertHTMLBefore(before.getNode(), child._redrawHTML());
		} else {
			zDom.insertHTMLAfter(last.getNode(), child._redrawHTML());
		}
		child.bind_(desktop);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);
		this.$supers('unbind_', arguments);
	}
});
(_zkwg=_zkpk.Tabs).prototype.className='zul.tab.Tabs';_zkmd={};
_zkmd['default']=
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid;
	out.push('<div ', this.domAttrs_(), '>' ,'<div id="', uuid, '$right">','</div>',
		'<div id="', uuid, '$left">','</div>','<div id="', uuid, '$header"',
		' class="', zcls, '-header" >','<ul id="', uuid, '$cave"','class="', zcls, '-cnt">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
	out.push('<li id="', uuid,'$edge"',
		' class="', zcls, '-edge" ></li><div class="z-clear"></div></ul>',
		'</div><div id="', uuid, '$line"',
		' class="', zcls, '-space" ></div></div>');
}
zkmld(_zkwg,_zkmd);
zul.tab.Tab = zk.$extends(zul.LabelImageWidget, {
	_selected : false,
	_closable : false,
	_disabled : false,
	
	isClosable: function() {
		return this._closable;
	},
	setClosable: function(closable) {
		if(this._closable != closable) {
			this._closable = closable;
			this.rerender();
		}
	},
	isSelected: function() {
		return this._selected;
	},
	setSelected: function(selected) {
		if (this._selected != selected) {
			if (this.getNode()) {
				this._selected = selected;
				this._selTab(this, false);
			}
		}
	},
	isDisabled: function() {
		return this._disabled;
	},
	setDisabled: function(disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			this.rerender();
		}
	},
	getTabbox: function() {
		return this.parent ? this.parent.parent : null;
	},
	getIndex: function() {
		return this.getChildIndex();
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tab" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : "-" + tabbox._mold):
		this._zclass;
	},
	getLinkedPanel: function() {
		var tabbox =  this.getTabbox(),
			tabpanels = tabbox.getTabpanels(),
			index = this.getIndex();
		return tabpanels.getChildAt(index);
	},
	_doClosebtnClick : function(evt) {
		if (!evt) evt = window.event;
		if (this._disabled)
			return;
		this.fire('onClose', true);
		zEvt.stop(evt);
	},
	_sliding: function(tab) {
		var tabbox = this.getTabbox(),
			panel = this.getLinkedPanel();
		if (!panel || !tabbox || !tabbox.inAccordionMold())
			return false;

		/*for (var node = panel; node = node.nextSibling;)
			if (getZKAttr($real(node), "animating"))
				return true;

		for (var node = panel; node = node.previousSibling;)
			if (getZKAttr($real(node), "animating"))
				return true;*/
		return false;
	},
	_selTab: function(tb, notify) {
		var tabbox = this.getTabbox(),
			tab =  tb.getNode();
			old = this._getSelTab(tab);
		/*
		 * if (zkTabbox2._isVert(tabbox))
			zkTabs2.scrollingchk($uuid($parent(tab)),"vsel",tab);
		else if (!zkTabbox2._isAccord(tabbox))
			zkTabs2.scrollingchk($uuid($parent(tab)),"sel",tab);
		 */
		if (!tab) return;
		if (old != tab) {
			if (old)
				this._setTabSel(old, false, false, notify);
			this._setTabSel(tab, true, notify, notify);
		}
	},
	_setTabSel: function(tab, toSel, notify, animation) {
		if (tab._selected == toSel)
			return;
		tab._selected = toSel;
		zDom[toSel ? "addClass" : "rmClass"](tab, this.getZclass() + "-seld" );
		var tabbox = this.getTabbox(),
			accd = tabbox.inAccordionMold(),
			panel = zk.Widget.$(tab).getLinkedPanel();
		if (panel)
			if (accd && animation) {
				var p = panel.getSubnode("real");
				zAnima[toSel ? "slideDown" : "slideUp"](p);
			} else {
				var pl = panel.getNode();
				zDom[toSel ? "show" : "hide"](pl);
			}
		/*
		if (!accd) {
			var tabs = $parentByType(tab, "Tabs2");
			   if (tabs) zkTabs2._fixWidth(tabs.id);
		}

		if (notify)
			zkau.sendasap({
				uuid: tab.id,
				cmd: "onSelect",
				data: [tab.id]
			});
		 */
	},
	/**
	 * Get selected tab
	 * @param {Object} tab
	 */
	_getSelTab: function(tab) {
		var tabbox = this.getTabbox();
		if (!tab) return null;
		if (tabbox.inAccordionMold()) {
			//@TODO Accordion
		} else {
			var node = tab;
			for (node = tab; node = node.nextSibling;)
				if (node._selected)
					return node;
			for (node = tab; node = node.previousSibling;)
				if (node._selected)
					return node;
			if (tab._selected) return tab;
		}
		return null;
	},
	//protected
	doClick_: function(evt) {
		if (!evt) evt = window.event;
		if (this._disabled)
			return;
		//@TODO
//		if (!zkTab2._sliding(tab)) //Bug 1571408
			this._selTab(this, true);
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this.isDisabled() ? this.getZclass() + '-disd' : '';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage();
		if (!img) return label;
		img = '<img src="' + img + '" align="absmiddle" class="' + this.getZclass() + '-img"/>';
		return label ? img + ' ' + label: img;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var uuid = this.uuid,
			closebtn = zDom.$(uuid, 'close');
        if (closebtn) {
			zEvt.listen(closebtn, "click", this.proxy(this._doClosebtnClick, '_tabClose'));
			if (!closebtn.style.cursor)
				closebtn.style.cursor = "default";
		//				if (zk.ie6Only) {
		//					zEvt.listen(closebtn, "mouseover", this.proxy(this._doMouseOver, '_tabMouseOver'));
		//					zEvt.listen(closebtn, "mouseout", this.proxy(this._doMouseOut, '_tabMouseOut'));
		//            	}
//
//	 var meta = $parent(cmp);
//	 if (!meta._toscroll)
//	 meta._toscroll = function () {
//	 zkTabs2.scrollingchk($uuid(meta));
//	 };
//	 zk.addInit(meta._toscroll, false, $uuid(meta) );

		}
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
		var closebtn = zDom.$(this.uuid, 'close');
		if (closebtn) {
			zWatch.unlisten(closebtn, "click", this.proxy(this._doClosebtnClick, '_tabClose'));		
		}
	}
});
(_zkwg=_zkpk.Tab).prototype.className='zul.tab.Tab';_zkmd={};
_zkmd['default']=
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid;;
	out.push('<li ', this.domAttrs_(), '>');
	if (this.isClosable()) {
		out.push('<a id="', uuid, '$close" class="', zcls, '-close"', 'onClick="return false;" ></a>');
	}
	out.push('<a id="', uuid, '$a" class="', zcls, '-body"', 'onClick="return false;" href="#">','<em id="', uuid, '$em">');
	if (this.isClosable())
		out.push('<span id="',uuid, '$inner" class="',zcls, '-inner ', zcls, '-close-inner">');
	else
		out.push('<span id="',uuid, '$inner" class="',zcls, '-inner ">');
	out.push('<span class="', zcls, '-text">',this.domContent_(),'</span></span></em></a></li>');
}
zkmld(_zkwg,_zkmd);
zul.tab.Tabpanels = zk.$extends(zul.Widget, {
	getTabbox: function() {
		return this.parent ? this.parent : null;
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabpanels" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : "-" + tabbox._mold):
		this._zclass;
	}
});
(_zkwg=_zkpk.Tabpanels).prototype.className='zul.tab.Tabpanels';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<div id="', this.uuid,'"' ,this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div>');
}
zkmld(_zkwg,_zkmd);
zul.tab.Tabpanel = zk.$extends(zul.Widget, {
	getTabbox: function() {
		return this.parent ? this.parent.parent : null;
	},
	isVisible: function() {
		return this.$supers('isVisible', arguments) && this.isSelected();
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabpanel" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : "-" + tabbox._mold):
		this._zclass;
	},
	bind_: function() {
		this.$supers('bind_', arguments);
		this._fixPanelHgh();
	},
	getLinkedTab: function() {
		var tabbox =  this.getTabbox(),
			tabs = tabbox.getTabs(),
			index = this.getIndex();
		return tabs.getChildAt(index);
	},
	getIndex:function() {
		return this.getChildIndex();
	},
	isSelected: function() {
		var tab = this.getLinkedTab();
		return tab != null && tab.isSelected();
	},
	_fixPanelHgh: function() {
		var tabbox = this.getTabbox();
		//@TODO Fix
		/*if (!tabbox.inAccordionMold()) {
			var hgh = zDom.getStyle(tabbox.getNode(),"height");
			var panels = this.parent;
			if (panels) {
                for (var pos, n = panels.firstChild; n; n = n.nextSibling) {
                    if (n.id) {
                        if (zk.ie) { // Bug: 1968434, this solution is very dirty but necessary.
                            pos = n.style.position;
                            n.style.position = "relative";
                        }
                        if (hgh && hgh != "auto") {//tabbox has height
                            hgh = zDom.vflexHeight(panels);
                            zDom.revisedHeight(n, hgh);
                        }
                        //let real div 100% height
                        zk.log(zDom.$(this.uuid, "$real"));
                        zDom.addClass(zDom.$(this.uuid, "$real"), this.getZclass() + "-cnt");
                        if (zk.ie)
                            n.style.position = pos;
                    }
                }
			}
		}*/
	}

});
(_zkwg=_zkpk.Tabpanel).prototype.className='zul.tab.Tabpanel';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		tab = this.getLinkedTab(),
		tabzcs = tab.getZclass(),
		tabbox = this.getTabbox();
	if (tabbox.inAccordionMold()) {//Accordion
		out.push('<div id="',uuid ,'"' ,' class="',zcls ,'-outer">' );
		if (tabbox.getMold() == "accordion" && tabbox.getPanelSpacing() != null && this.getIndex() != 0) {
			out.push('<div style="margin:0;display:list-item;width:100%;height:',tabbox.getpanelSpacing(),';"></div>');
		}
		out.push('<div id="',tab.uuid,'"',tab.domAttrs_(),'>',
		'<div align="left" class="',tabzcs,'-header" >');
		if (tab.isClosable()) {
			out.push('<a id="',tab.uuid,'$close"  class="',tabzcs,'-close"></a>');
		}
		out.push('<a href="javascript:;" id="',tab.uuid,'$a" class="',tabzcs,'-tl">',
		'<em class="',tabzcs,'-tr">','<span class="',tabzcs,'-tm">','<span class="',tabzcs,'-text">',
		tab.domContent_(),'</span></span></em></a></div></div>'
		);
		out.push('<div id="',uuid,'$real"', this.domAttrs_(),'>',
			'<div id="',uuid,'$cave" >');
		for (var w = this.firstChild; w; w = w.nextSibling)
				w.redraw(out);
		out.push('</div></div></div>');
	} else {//Default Mold
		out.push('<div id="', uuid,'"' ,this.domAttrs_(), '>', '<div id="', uuid, '$real">')
			for (var w = this.firstChild; w; w = w.nextSibling)
				w.redraw(out);
		out.push('</div></div>');
	}
}
zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}_z='zul.layout';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.layout.Borderlayout = zk.$extends(zul.Widget, {
	setResize: function () {
		this.resize();
	},
	//-- super --//
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.layout.North))
			this.north = child;
		else if (child.$instanceof(zul.layout.South))
			this.south = child;
		else if (child.$instanceof(zul.layout.Center))
			this.center = child;
		else if (child.$instanceof(zul.layout.West))
			this.west = child;
		else if (child.$instanceof(zul.layout.East))
			this.east = child;
		this.resize();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.north)
			this.north = null;
		else if (child == this.south)
			this.south = null;
		else if (child == this.center)
			this.center = null;
		else if (child == this.west)
			this.west = null;
		else if (child == this.east)
			this.east = null;
		this.resize();
	},
	getZclass: function () {
		return this._zclass == null ? "z-border-layout" : this._zclass;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);
		this.$supers('unbind_', arguments);
	},
	// private
	// returns the ambit of the specified cmp for region calculation. 
	_getAmbit: function (wgt, ignoreSplit) {
		var region = wgt.getPosition();
		if (region && !wgt.isOpen()) {
			var colled = wgt.getSubnode('colled');
			return {
				w: colled ? colled.offsetWidth : 0,
				h: colled ? colled.offsetHeight : 0
			};
		}
		var w = wgt.getWidth() || '',
			h = wgt.getHeight() || '',
			widx = w.indexOf('%'),
			hidx = h.indexOf('%');

		var ambit = {
			w: widx > 0 ?
				Math.max(
					Math.floor(this.getNode().offsetWidth * zk.parseInt(w.substring(0, widx)) / 100),
					0) : wgt.getSubnode('real').offsetWidth, 
			h: hidx > 0 ?
				Math.max(
					Math.floor(this.getNode().offsetHeight * zk.parseInt(h.substring(0, hidx)) / 100),
					0) : wgt.getSubnode('real').offsetHeight
		};
		if (region && !ignoreSplit) {
			var split = wgt.getSubnode('split') || {offsetHeight:0, offsetWidth:0};
			wgt._fixSplit();
			switch (region) {
				case this.$class.NORTH:
				case this.$class.SOUTH:
					ambit.h += split.offsetHeight;
					break;
				case this.$class.WEST:
				case this.$class.EAST:
					ambit.w += split.offsetWidth;
					break;
			}
		}
		return ambit;
	},
	_getMargins: function (wgt) {
		return this._arrayToObject(wgt.isOpen() ? wgt._margins : wgt._cmargins);
	},
	resize: function () {
		if (this.desktop)
			this._resize();
	},
	_resize: function (isOnSize) {
		if (!this.isRealVisible()) return;
		this._isOnSize = isOnSize;
		var el = this.getNode(),
			width = el.offsetWidth,
			height = el.offsetHeight,
			cW = width,
			cH = height,
			cY = 0,
			cX = 0;

		if (this.north && zDom.isVisible(this.north.getNode())) {
			var ambit = this._getAmbit(this.north),
				mars = this._getMargins(this.north);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = mars.top;
			cY = ambit.h + ambit.y + mars.bottom;
			cH -= cY;
			this._resizeWgt(this.north, ambit);
		}
		if (this.south && zDom.isVisible(this.south.getNode())) {
			var ambit = this._getAmbit(this.south),
				mars = this._getMargins(this.south),
				total = (ambit.h + mars.top + mars.bottom);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = height - total + mars.top;
			cH -= total;
			this._resizeWgt(this.south, ambit);
		}
		if (this.west && zDom.isVisible(this.west.getNode())) {
			var ambit = this._getAmbit(this.west),
				mars = this._getMargins(this.west),
				total = (ambit.w + mars.left + mars.right);
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = mars.left;
			ambit.y = cY + mars.top;
			cX += total;
			cW -= total;
			this._resizeWgt(this.west, ambit);
		}
		if (this.east && zDom.isVisible(this.east.getNode())) {
			var ambit = this._getAmbit(this.east),
				mars = this._getMargins(this.east),
				total = (ambit.w + mars.left + mars.right);
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = width - total + mars.left;
			ambit.y = cY + mars.top;
			cW -= total;
			this._resizeWgt(this.east, ambit);
		}
		if (this.center && zDom.isVisible(this.center.getNode())) {
			var mars = this._getMargins(this.center),
				ambit = {
					x: cX + mars.left,
					y: cY + mars.top,
					w: cW - (mars.left + mars.right),
					h: cH - (mars.top + mars.bottom)
				};
			this._resizeWgt(this.center, ambit);
		}
		zDom.cleanVisibility(el);
		this._isOnSize = false; // reset
	},
	_arrayToObject: function (array) {
		return {top: array[0], left: array[1], right: array[2], bottom: array[3]};
	},
	_resizeWgt: function (wgt, ambit, ignoreSplit) {
		if (wgt.isOpen()) {
			if (!ignoreSplit && wgt.getSubnode('split')) {
				wgt._fixSplit();
				 ambit = this._resizeSplit(wgt, ambit);
			}
			var s = wgt.getSubnode('real').style; 
			s.left = ambit.x + "px";
			s.top = ambit.y + "px";
			this._resizeBody(wgt, ambit);
		} else {
			wgt.getSubnode('split').style.display = "none";
			var colled = wgt.getSubnode('colled');
			if (colled) {
				colled.style.left = ambit.x + "px";
				colled.style.top = ambit.y + "px";
				colled.style.height = zDom.revisedHeight(colled, ambit.h) + "px";
				colled.style.width = zDom.revisedWidth(colled, ambit.w) + "px";
			}
		}
	},
	_resizeSplit: function (wgt, ambit) {
		var split = wgt.getSubnode('split');
		if (!zDom.isVisible(split)) return ambit;
		var sAmbit = {
				w: split.offsetWidth, 
				h: split.offsetHeight
			},
			s = split.style;
		switch (wgt.getPosition()) {
			case this.$class.NORTH:
				ambit.h -= sAmbit.h;
			  	s.left = ambit.x + "px";
				s.top = (ambit.y + ambit.h) + "px";
				s.width = (ambit.w < 0 ? 0 : ambit.w) + "px";
				break;
			case this.$class.SOUTH:
				ambit.h -= sAmbit.h;
				ambit.y += sAmbit.h;
				s.left = ambit.x + "px";
				s.top = (ambit.y - sAmbit.h) + "px";
				s.width = (ambit.w < 0 ? 0 : ambit.w) + "px";
				break;
			case this.$class.WEST:
				ambit.w -= sAmbit.w;
				s.left = (ambit.x + ambit.w) + "px";
				s.top = ambit.y + "px";
				s.height = (ambit.h < 0 ? 0 : ambit.h) + "px";
				break;
			case this.$class.EAST:
				ambit.w -= sAmbit.w;
				s.left = ambit.x + "px";
				s.top = ambit.y + "px";
				s.height = (ambit.h < 0 ? 0 : ambit.h) + "px";
				ambit.x += sAmbit.w;
				break;
		}
		return ambit;
	},
	_resizeBody: function (wgt, ambit) {
		ambit.w = Math.max(0, ambit.w);
		ambit.h = Math.max(0, ambit.h);
		var el = wgt.getSubnode('real'),
			bodyEl = wgt.isFlex() && wgt.firstChild ?
						wgt.firstChild.getNode() : wgt.getSubnode('cave');
		if (!this._ignoreResize(el, ambit.w, ambit.h)) {
			ambit.w = zDom.revisedWidth(el, ambit.w);
			el.style.width = ambit.w + "px";
			ambit.w = zDom.revisedWidth(bodyEl, ambit.w);
			bodyEl.style.width = ambit.w + "px";

			ambit.h = zDom.revisedHeight(el, ambit.h);
			el.style.height = ambit.h + "px";
			ambit.h = zDom.revisedHeight(bodyEl, ambit.h);
			if (wgt.getSubnode('cap')) ambit.h = Math.max(0, ambit.h - wgt.getSubnode('cap').offsetHeight);
			bodyEl.style.height = ambit.h + "px";
			if (wgt.isAutoscroll()) { 
				bodyEl.style.overflow = "auto";
				bodyEl.style.position = "relative";
			} else {
				bodyEl.style.overflow = "hidden";
				bodyEl.style.position = "";
			}
			if (!this._isOnSize) {
				zWatch.fireDown('beforeSize', null, wgt);
				zWatch.fireDown('onSize', null, wgt);
			}
		}
	},
	_ignoreResize : function(el, w, h) { 
		if (el._lastSize && el._lastSize.width == w && el._lastSize.height == h) {
			return true;
		} else {
			el._lastSize = {width: w, height: h};
			return false;
		}
	},
	//zWatch//
	onSize: _zkf = function () {
		this._resize(true);
	},
	onVisible: _zkf
}, {
	NORTH: "north",
	SOUTH: "south",
	EAST: "east",
	WEST: "west",
	CENTER: "center"
});

(_zkwg=_zkpk.Borderlayout).prototype.className='zul.layout.Borderlayout';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<div', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div>');
}

zkmld(_zkwg,_zkmd);
zul.layout.LayoutRegion = zk.$extends(zul.Widget, {
	_open: true,
	_border: "normal",
	_maxsize: 2000,
	_minsize: 0,

	$init: function () {
		this.$supers('$init', arguments);
		this._margins = [0, 0, 0, 0];
		this._cmargins = [5, 5, 5, 5];
	},
	getTitle: function () {
		return this._title;
	},
	setTitle: function (title) {
		if (this._title != title) {
			this._title = title;
			this.rerender();
		}
	},
	getBorder: function () {
		return this._border;
	},
	setBorder: function (border) {
		if (!border || '0' == border)
			border = "none";
		if (this._border != border) {
			this._border = border;
			this.updateDomClass_();
		}
	},
	isSplittable: function () {
		return this._splittable;
	},
	setSplittable: function (splittable) {
		if (this._splittable != splittable) {
			this._splittable = splittable;
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	},
	setMaxsize: function (maxsize) {
		if (this._maxsize != maxsize)
			this._maxsize = maxsize;
	},
	getMaxsize: function () {
		return this._maxsize;
	},
	setMinsize: function (minsize) {
		if (this._minsize != minsize)
			this._minsize = minsize;
	},
	getMinsize: function () {
		return this._minsize;
	},
	isFlex: function () {
		return this._flex;
	},
	setFlex: function (flex) {
		if (this._flex != flex) {
			this._flex = flex;
			this.rerender();
		}
	},
	getMargins: function () {
		return zUtl.intsToString(this._margins);
	},
	setMargins: function (margins) {
		if (this.getMargins() != margins) {
			this._margins = zUtl.stringToInts(margins, 0);
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	},
	getCmargins: function () {
		return zUtl.intsToString(this._cmargins);
	},
	setCmargins: function (cmargins) {
		if (this.getCmargins() != cmargins) {
			this._cmargins = zUtl.stringToInts(cmargins, 0);
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	},
	isCollapsible: function () {
		return this._collapsible;
	},
	setCollapsible: function (collapsible) {
		if (this._collapsible != collapsible) {
			this._collapsible = collapsible;
			var btn = this.getSubnode(this.isOpen() ? 'btn' : 'btned');
			if (btn)
				btn.style.display = collapsible ? '' : 'none';
		}
	},
	isAutoscroll: function () {
		return this._autoscroll;
	},
	setAutoscroll: function (autoscroll) {
		if (this._autoscroll != autoscroll) {
			this._autoscroll = autoscroll;
			var cave = this.getSubnode('cave');
			if (cave) {
				var bodyEl = this.isFlex() && this.firstChild ?
						this.firstChild.getNode() : cave;
				if (autoscroll) {
					bodyEl.style.overflow = "auto";
					bodyEl.style.position = "relative";
					zEvt.listen(bodyEl, "scroll", this.proxy(this.doScroll_, '_pxDoscroll'));
				} else {
					bodyEl.style.overflow = "hidden";
					bodyEl.style.position = "";
					zEvt.unlisten(bodyEl, "scroll", this._pxDoscroll);
				}
			}
		}
	},
	isOpen: function () {
		return this._open;
	},
	setOpen: function (open, fromServer, nonAnima) {
		if (this._open != open) {
			this._open = open;
			if (!this.getNode() || !this.isCollapsible())
				return; //nothing changed

			var colled = this.getSubnode('colled'),
				real = this.getSubnode('real');
			if (open) {
				if (colled) {
					if (!nonAnima) 
						zAnima.slideOut(this, colled, {
							anchor: this.sanchor,
							duration: 200,
							afterAnima: this.$class.afterSlideOut
						});
					else {
						zDom[open ? 'show' : 'hide'](real);
						zDom[!open ? 'show' : 'hide'](colled);
						zWatch.fireDown(open ? 'onVisible' : 'onHide', {visible:true}, this);
					}
				}
			} else {
				if (colled && !nonAnima) 
					zAnima.slideOut(this, real, {
							anchor: this.sanchor,
							beforeAnima: this.$class.beforeSlideOut,
							afterAnima: this.$class.afterSlideOut
						});
				else {
					if (colled)
						zDom[!open ? 'show' : 'hide'](colled);
					zDom[open ? 'show' : 'hide'](real);
				}
			}
			if (nonAnima) this.parent.resize();
			if (!fromServer) this.fire('onOpen', open);	
		}
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = "normal" == this.getBorder() ? '' : this.getZclass() + "-noborder";
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	getZclass: function () {
		return this._zclass == null ? "z-" + this.getPosition() : this._zclass;
	},
	//-- super --//
	setWidth: function (width) {
		this._width = width;
		var real = this.getSubnode('real');
		if (real) real.style.width = width ? width: '';
	},
	setHeight: function (height) {
		this._height = height;
		var real = this.getSubnode('real');
		if (real) real.style.height = height ? height: '';
	},
	setVisible: function (visible) {
		if (this._visible != visible) {
			this.$supers('setVisible', arguments);
			var real = this.getSubnode('real');
			if (real) {
				real.style.display = real.parentNode.style.display;
				this.parent.resize();
			}
		}
	},	
	updateDomClass_: function () {
		if (this.desktop) {
			var real = this.getSubnode('real');
			if (real) {
				real.className = this.domClass_();
				if (this.parent) 
					this.parent.resize();
			}
		}
	},
	updateDomStyle_: function () {
		if (this.desktop) {
			var real = this.getSubnode('real');
			if (real) {
				zDom.setStyle(real, zDom.parseStyle(this.domStyle_()));
				if (this.parent) 
					this.parent.resize();
			}
		}
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.layout.Borderlayout)) {
			this.setFlex(true);
			zDom.addClass(this.getNode(), this.getZclass() + "-nested");
		}
		if (this.parent && this.desktop)
			this.parent.resize();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child.$instanceof(zul.layout.Borderlayout)) {
			this.setFlex(false);
			zDom.rmClass(this.getNode(), this.getZclass() + "-nested");
		}
		if (this.parent && this.desktop)
			this.parent.resize();
	},
	rerender: function () {
		this.$supers('rerender', arguments);
		if (this.parent)
			this.parent.resize();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.getPosition() != zul.layout.Borderlayout.CENTER) {
			var split = this.getSubnode('split');			
			if (split) {
				this._fixSplit();
				var vert = this._isVertical(),
					$LayoutRegion = this.$class;
				
				this._drag = new zk.Draggable(this, split, {
					constraint: vert ? 'vertical': 'horizontal',
					ghosting: $LayoutRegion._ghosting,
					snap: $LayoutRegion._snap,
					zIndex: 12000,
					overlay: true,
					ignoredrag: $LayoutRegion._ignoredrag,
					endeffect: $LayoutRegion._endeffect					
				});	
				if (!this.isOpen()) {
					this._open = true;
					this.setOpen(false, true, true);
				}
			}
		}
		var n = this.getNode(),
			real = n.firstChild;
					
		if (this.isOpen() && !zDom.isVisible(real)) n.style.display = "none";
		
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.getNode() : this.getSubnode('cave');
			zEvt.listen(bodyEl, "scroll", this.proxy(this.doScroll_, '_pxDoscroll'));
		}
	},
	unbind_: function () {
		if (this.isAutoscroll()) {
			var bodyEl = this.isFlex() && this.firstChild ?
					this.firstChild.getNode() : this.getSubnode('cave');
			zEvt.unlisten(bodyEl, "scroll", this._pxDoscroll);
		}
		if (this.getSubnode('split')) {			
			if (this._drag) {
				this._drag.destroy();
				this._drag = null;
			}
		}
		this.$supers('unbind_', arguments);
	},
	doScroll_: function () {
		zWatch.fireDown('onScroll', null, this);
	},
	doMouseOver_: function (evt) {
		if (this.getSubnode('btn')) {
			switch (evt.nativeTarget) {
			case this.getSubnode('btn'):
				zDom.addClass(this.getSubnode('btn'), this.getZclass() + '-collapse-over');
				break;
			case this.getSubnode('btned'):
				zDom.addClass(this.getSubnode('btned'), this.getZclass() + '-expand-over');
				// don't break
			case this.getSubnode('colled'):
				zDom.addClass(this.getSubnode('colled'), this.getZclass() + '-collapsed-over');
				break;
			}
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		if (this.getSubnode('btn')) {
			switch (evt.nativeTarget) {
			case this.getSubnode('btn'):
				zDom.rmClass(this.getSubnode('btn'), this.getZclass() + '-collapse-over');
				break;
			case this.getSubnode('btned'):
				zDom.rmClass(this.getSubnode('btned'), this.getZclass() + '-expand-over');
				// don't break
			case this.getSubnode('colled'):
				zDom.rmClass(this.getSubnode('colled'), this.getZclass() + '-collapsed-over');
				break;
			}
		}
		this.$supers('doMouseOut_', arguments);		
	},
	doClick_: function (evt) {
		if (this.getSubnode('btn')) {
			var target = evt.nativeTarget;
			switch (target) {
			case this.getSubnode('btn'):
			case this.getSubnode('btned'):
				if (this._isSlide || zAnima.count) return;
				if (this.getSubnode('btned') == target) {
					var s = this.getSubnode('real').style;
					s.visibilty = "hidden";
					s.display = "";
					this._syncSize(true);
					s.visibilty = "";
					s.display = "none";
				}
				this.setOpen(!this.isOpen());
				break;
			case this.getSubnode('colled'):					
				if (this._isSlide) return;
				this._isSlide = true;
				var real = this.getSubnode('real'),
					s = real.style;
				s.visibilty = "hidden";
				s.display = "";
				this._syncSize();
				this._original = [s.left, s.top];
				this._alignTo();
				s.zIndex = 100;

				this.getSubnode('btn').style.display = "none"; 
				s.visibilty = "";
				s.display = "none";
				zAnima.slideDown(this, real, {
					anchor: this.sanchor,
					afterAnima: this.$class.afterSlideDown
				});
				break;
			}
		}
		this.$supers('doClick_', arguments);		
	},
	_doDocClick: function (evt) {
		var target = zEvt.target(evt);
		if (this._isSlide && !zDom.isAncestor(this.getSubnode('real'), target)) {
			if (this.getSubnode('btned') == target) {
				this.$class.afterSlideUp.apply(this, [target]);
				this.setOpen(true, false, true);
			} else 
				if ((!this._isSlideUp && this.$class.uuid(target) != this.uuid) || !zAnima.count) {
					this._isSlideUp = true;
					zAnima.slideUp(this, this.getSubnode('real'), {
						anchor: this.sanchor,
						afterAnima: this.$class.afterSlideUp
					});
				}
		}
	},
	_syncSize: function (inclusive) {
		var layout = this.parent,
			el = layout.getNode(),
			width = el.offsetWidth,
			height = el.offsetHeight,
			cH = height,
			cY = 0,
			cX = 0,
			n = layout.north,
			s = layout.south,
			e = layout.east,
			w = layout.west,
			c = layout.center;
		this._open = true;
		if (n && (zDom.isVisible(n.getNode()) || zDom.isVisible(n.getSubnode('colled')))) {
			var ignoreSplit = n == this,
				ambit = layout._getAmbit(n, ignoreSplit),
				mars = layout._getMargins(n);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = mars.top;
			cY = ambit.h + ambit.y + mars.bottom;
			cH -= cY;
			if (ignoreSplit) {
				ambit.w = this.getSubnode('colled').offsetWidth;
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.w += cmars.left + cmars.right;
				}
				layout._resizeWgt(n, ambit, true);
				this._open = false;
				return;
			}
		}
		if (s && (zDom.isVisible(s.getNode()) || zDom.isVisible(s.getSubnode('colled')))) {
			var ignoreSplit = s == this,
				ambit = layout._getAmbit(s, ignoreSplit),
				mars = layout._getMargins(s),
				total = (ambit.h + mars.top + mars.bottom);
			ambit.w = width - (mars.left + mars.right);
			ambit.x = mars.left;
			ambit.y = height - total + mars.top;
			cH -= total;
			if (ignoreSplit) {
				ambit.w = this.getSubnode('colled').offsetWidth;
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.w += cmars.left + cmars.right;
				}
				layout._resizeWgt(s, ambit, true);
				this._open = false;
				return;
			}
		}
		if (w && (zDom.isVisible(w.getNode()) || zDom.isVisible(w.getSubnode('colled')))) {
			var ignoreSplit = w == this,
				ambit = layout._getAmbit(w, ignoreSplit),
				mars = layout._getMargins(w);
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = mars.left;
			ambit.y = cY + mars.top;
			if (ignoreSplit) {
				ambit.h = this.getSubnode('colled').offsetHeight
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.h += cmars.top + cmars.bottom;
				}
				layout._resizeWgt(w, ambit, true);
				this._open = false;
				return;
			}
		}
		if (e && (zDom.isVisible(e.getNode()) || zDom.isVisible(e.getSubnode('colled')))) {
			var ignoreSplit = e == this,
				ambit = layout._getAmbit(e, ignoreSplit),
				mars = layout._getMargins(e),
				total = (ambit.w + mars.left + mars.right); 
			ambit.h = cH - (mars.top + mars.bottom);
			ambit.x = width - total + mars.left;
			ambit.y = cY + mars.top;
			if (ignoreSplit) {
				ambit.h = this.getSubnode('colled').offsetHeight
				if (inclusive) {
					var cmars = layout._arrayToObject(this._cmargins);
					ambit.h += cmars.top + cmars.bottom;
				}
				layout._resizeWgt(e, ambit, true);
				this._open = false;
				return;
			}
		}
	},
	_fixSplit: function () {
		zDom[this.isSplittable() ? 'show' : 'hide'](this.getSubnode('split'));	
	},
	_alignTo: function () {
		var from = this.getSubnode('colled'),
			to = this.getSubnode('real');
		switch (this.getPosition()) {
		case zul.layout.Borderlayout.NORTH:
			to.style.top = from.offsetTop + from.offsetHeight + "px";
			to.style.left = from.offsetLeft + "px";
			break;
		case zul.layout.Borderlayout.SOUTH:
			to.style.top = from.offsetTop - to.offsetHeight + "px";
			to.style.left = from.offsetLeft + "px";
			break;
		case zul.layout.Borderlayout.WEST:
			to.style.left = from.offsetLeft + from.offsetWidth + "px";
			to.style.top = from.offsetTop + "px";
			break;
		case zul.layout.Borderlayout.EAST:
			to.style.left = from.offsetLeft - to.offsetWidth + "px";
			to.style.top = from.offsetTop + "px";
			break;
		}
	},
	_isVertical : function () {
		return this.getPosition() != zul.layout.Borderlayout.WEST &&
				this.getPosition() != zul.layout.Borderlayout.EAST;
	}
}, {
	// invokes border layout's renderer before the component slides out
	beforeSlideOut: function (n) {
		var s = this.getSubnode('colled').style;
		s.display = "";
		s.visibility = "hidden";
		s.zIndex = 1;
		this.parent.resize();
	},
	// a callback function after the component slides out.
	afterSlideOut: function (n) {
		if (this.isOpen()) 
			zAnima.slideIn(this, this.getSubnode('real'), {
				anchor: this.sanchor,
				afterAnima: this.$class.afterSlideIn
			});
		else {
			var colled = this.getSubnode('colled'),
				s = colled.style;
			s.zIndex = ""; // reset z-index refered to the beforeSlideOut()
			s.visibility = "";
			zAnima.slideIn(this, colled, {
				anchor: this.sanchor,				
				duration: 200
			});
		}
	},
	// recalculates the size of the whole border layout after the component sildes in.
	afterSlideIn: function (n) {
		this.parent.resize();
	},
	// a callback function after the collapsed region slides down
	afterSlideDown: function (n) {
		zEvt.listen(document, "click", this.proxy(this._doDocClick, '_pxdoDocClick'));
	},
	// a callback function after the collapsed region slides up
	afterSlideUp: function (n) {
		var s = n.style;
		s.left = this._original[0];
		s.top = this._original[1];
		n._lastSize = null;// reset size for Borderlayout
		s.zIndex = "";
		this.getSubnode('btn').style.display = "";
		zEvt.unlisten(document, "click", this._pxdoDocClick);
		this._isSlideUp = this._isSlide = false;
	},
	// Drag and drop
	_ignoredrag: function (dg, pointer, evt) {
			var target = zEvt.target(evt),
				wgt = dg.control;
			if (!target || target != wgt.getSubnode('split')) return true;
			if (wgt.isSplittable() && wgt.isOpen()) {			
				var $Layout = zul.layout.Borderlayout,
					pos = wgt.getPosition(),
					maxs = wgt.getMaxsize(),
					mins = wgt.getMinsize(),
					ol = wgt.parent,
					real = wgt.getSubnode('real'),
					mars = ol._arrayToObject(wgt._margins),
					lr = zDom.padBorderWidth(real)
						+ (pos == $Layout.WEST ? mars.left : mars.right),
					tb = zDom.padBorderWidth(real)
						+ (pos == $Layout.NORTH ? mars.top : mars.bottom),
					min = 0,
					uuid = wgt.uuid;
				switch (pos) {
				case $Layout.NORTH:	
				case $Layout.SOUTH:
					var r = ol.center || (pos == $Layout.NORTH ? ol.south : ol.north);
					if (r) {
						if ($Layout.CENTER == r.getPosition()) {
							var east = ol.east,
								west = ol.west;
							maxs = Math.min(maxs, (real.offsetHeight + r.getSubnode('real').offsetHeight)- min);
						} else {
							maxs = Math.min(maxs, ol.getNode().offsetHeight
									- r.getSubnode('real').offsetHeight - r.getSubnode('split').offsetHeight
									- wgt.getSubnode('split').offsetHeight - min); 
						}
					} else {
						maxs = ol.getNode().offsetHeight - wgt.getSubnode('split').offsetHeight;
					}
					break;				
				case $Layout.WEST:
				case $Layout.EAST:
					var r = ol.center || (pos == $Layout.WEST ? ol.east : ol.west);
					if (r) {
						if ($Layout.CENTER == r.getPosition()) {
							maxs = Math.min(maxs, (real.offsetWidth
									+ zDom.revisedWidth(r.getSubnode('real'), r.getSubnode('real').offsetWidth))- min);
						} else {
							maxs = Math.min(maxs, ol.getNode().offsetWidth
									- r.getSubnode('real').offsetWidth - r.getSubnode('split').offsetWidth - wgt.getSubnode('split').offsetWidth - min); 
						}
					} else {
						maxs = ol.getNode().offsetWidth - wgt.getSubnode('split').offsetWidth;
					}
					break;						
				}
				var ofs = zDom.cmOffset(real);
				dg._rootoffs = {
					maxs: maxs,
					mins: mins,
					top: ofs[1],
					left : ofs[0],
					right : real.offsetWidth,
					bottom: real.offsetHeight
				};
				return false;
			}
		return true;
	},
	_endeffect: function (dg, evt) {
		var wgt = dg.control,
			keys = "";
		if (wgt._isVertical())
			wgt.setHeight(dg._point[1] + 'px');
		else
			wgt.setWidth(dg._point[0] + 'px');
			
		dg._rootoffs = dg._point = null;
		
		wgt.parent.resize();
		wgt.fire('onSize', {
			width: wgt.getSubnode('real').style.width,
			height: wgt.getSubnode('real').style.height,
			keys: zEvt.keyMetaData(evt),
			marshal: wgt.$class._onSizeMarshal
		});
	},
	_onSizeMarshal: function () {
		return [this.width, this.height, this.keys ? this.keys.marshal(): ''];
	},
	_snap: function (dg, pointer) {
		var wgt = dg.control,
			x = pointer[0],
			y = pointer[1],
			$Layout = zul.layout.Borderlayout,
			split = wgt.getSubnode('split'),
			b = dg._rootoffs, w, h;
		switch (wgt.getPosition()) {
		case $Layout.NORTH:
			if (y > b.maxs + b.top) y = b.maxs + b.top;
			if (y < b.mins + b.top) y = b.mins + b.top;
			w = x;
			h = y - b.top;
			break;				
		case $Layout.SOUTH:
			if (b.top + b.bottom - y - split.offsetHeight > b.maxs) {
				y = b.top + b.bottom - b.maxs - split.offsetHeight;
				h = b.maxs;
			} else if (b.top + b.bottom - b.mins - split.offsetHeight <= y) {
				y = b.top + b.bottom - b.mins - split.offsetHeight;
				h = b.mins;
			} else h = b.top - y + b.bottom - split.offsetHeight;
			w = x;
			break;
		case $Layout.WEST:
			if (x > b.maxs + b.left) x = b.maxs + b.left;
			if (x < b.mins + b.left) x = b.mins + b.left;
			w = x - b.left;
			h = y;
			break;		
		case $Layout.EAST:			
			if (b.left + b.right - x - split.offsetWidth > b.maxs) {
				x = b.left + b.right - b.maxs - split.offsetWidth;
				w = b.maxs;
			} else if (b.left + b.right - b.mins - split.offsetWidth <= x) {
				x = b.left + b.right - b.mins - split.offsetWidth;
				w = b.mins;
			} else w = b.left - x + b.right - split.offsetWidth;
			h = y;
			break;						
		}
		dg._point = [w, h];
		return [x, y];
	},
	_ghosting: function (dg, ofs, evt) {
		var el = dg.node,
			html = '<div id="zk_layoutghost" style="font-size:0;line-height:0;background:#AAA;position:absolute;top:'
			+ofs[1]+'px;left:'+ofs[0]+'px;width:'
			+zDom.offsetWidth(el)+'px;height:'+zDom.offsetHeight(el)
			+'px;cursor:'+el.style.cursor+';"></div>';
		document.body.insertAdjacentHTML("afterBegin", html);
		return zDom.$("zk_layoutghost");
	}
});

(_zkwg=_zkpk.LayoutRegion).prototype.className='zul.layout.LayoutRegion';
zul.layout.North = zk.$extends(zul.layout.LayoutRegion, {
	setWidth: zk.$void, // readonly
	sanchor: 't',
	
	getPosition: function () {
		return zul.layout.Borderlayout.NORTH;
	},
	getSize: function () {
		return this.getHeight();
	},
	setSize: function (size) {
		this.setHeight(size);
	}
});
(_zkwg=_zkpk.North).prototype.className='zul.layout.North';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		noCenter = this.getPosition() != zul.layout.Borderlayout.CENTER,
		pzcls = this.parent.getZclass();
	out.push('<div id="', uuid,  '">', '<div id="', uuid, '$real"',
			this.domAttrs_({id: 1}), '>');
			
	if (this.getTitle()) {
		out.push('<div id="', uuid, '$cap" class="', zcls, '-header">');
		if (noCenter) {
			out.push('<div id="', uuid, '$btn" class="', pzcls,
					'-tool ', zcls, '-collapse"');
			if (!this.isCollapsible())
				out.push(' style="display:none;"');
			out.push('></div>');
		}
		out.push(zUtl.encodeXML(this.getTitle()), '</div>');
	}
	out.push('<div id="', uuid, '$cave" class="', zcls, '-body">');
	
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	
	out.push('</div></div>');
	
	if (noCenter) {
		out.push('<div id="', uuid, '$split" class="', zcls, '-split"></div>');
		if (this.getTitle()) {
			out.push('<div id="', uuid, '$colled" class="', zcls,
					'-collapsed" style="display:none"><div id="',
					uuid, '$btned" class="', pzcls, '-tool ', zcls, '-expand"');
			if (!this.isCollapsible())
				out.push(' style="display:none;"');
				
			out.push('></div></div>');
		}
	}
	out.push('</div>');
}
zkmld(_zkwg,_zkmd);
zul.layout.South = zk.$extends(zul.layout.LayoutRegion, {
	setWidth: zk.$void, // readonly
	sanchor: 'b',
	getPosition: function () {
		return zul.layout.Borderlayout.SOUTH;
	},
	getSize: function () {
		return this.getHeight();
	},
	setSize: function (size) {
		this.setHeight(size);
	}
});
(_zkwg=_zkpk.South).prototype.className='zul.layout.South';_zkmd={};
_zkmd['default']=[_zkpk.North,'default'];zkmld(_zkwg,_zkmd);
zul.layout.Center = zk.$extends(zul.layout.LayoutRegion, {
	setCmargins: zk.$void,    // readonly
	setSplittable: zk.$void,  // readonly
	setOpen: zk.$void,        // readonly
	setCollapsible: zk.$void, // readonly
	setMaxsize: zk.$void,     // readonly
	setMinsize: zk.$void,     // readonly
	setHeight: zk.$void,      // readonly
	setWidth: zk.$void,       // readonly
	setVisible: zk.$void,     // readonly
	getSize: zk.$void,        // readonly
	setSize: zk.$void,        // readonly
	doMouseOver_: zk.$void,   // do nothing.
	doMouseOut_: zk.$void,    // do nothing.
	doClick_: zk.$void,       // do nothing.
	
	getPosition: function () {
		return zul.layout.Borderlayout.CENTER;
	}
});
(_zkwg=_zkpk.Center).prototype.className='zul.layout.Center';_zkmd={};
_zkmd['default']=[_zkpk.North,'default'];zkmld(_zkwg,_zkmd);
zul.layout.East = zk.$extends(zul.layout.LayoutRegion, {
	setHeight: zk.$void, // readonly
	sanchor: 'r',
	
	$init: function () {
		this.$supers('$init', arguments);
		this.setCmargins("0,5,5,0");
	},
	getPosition: function () {
		return zul.layout.Borderlayout.EAST;
	},
	getSize: function () {
		return this.getWidth();
	},
	setSize: function (size) {
		this.setWidth(size);
	}
});
(_zkwg=_zkpk.East).prototype.className='zul.layout.East';_zkmd={};
_zkmd['default']=[_zkpk.North,'default'];zkmld(_zkwg,_zkmd);
zul.layout.West = zk.$extends(zul.layout.LayoutRegion, {
	setHeight: zk.$void, // readonly
	sanchor: 'l',
	$init: function () {
		this.$supers('$init', arguments);
		this.setCmargins("0,5,5,0");
	},
	getPosition: function () {
		return zul.layout.Borderlayout.WEST;
	},
	getSize: function () {
		return this.getWidth();
	},
	setSize: function (size) {
		this.setWidth(size);
	}
});

(_zkwg=_zkpk.West).prototype.className='zul.layout.West';_zkmd={};
_zkmd['default']=[_zkpk.North,'default'];zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}