
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
		el.style.height = (hgh > 0 ? hgh: 0) + "px";
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
			wd = w - opts.left + opts.right,
			hgh = h - opts.top + opts.bottom,
			st = shadow.style;
		st.left = (l + opts.left) + "px";
		st.top = (t + opts.top) + "px";
		st.width = wd + "px";
		st.display = "block";
		if (zk.ie6Only) st.height = hgh + "px";
		else {
			var cns = shadow.childNodes;
			cns[1].style.height = (hgh - cns[0].offsetHeight - cns[2].offsetHeight) + "px";
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
}finally{zPkg.end(_z);}}