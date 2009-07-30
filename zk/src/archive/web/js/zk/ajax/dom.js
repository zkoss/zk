/* dom.js

	Purpose:
		Enhance jQuery
	Description:
		
	History:
		Fri Jun 12 10:44:53     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zjq = function (jq) { //ZK extension
	this.jq = jq;
};
zk.override(jq.fn, zjq._fn = {}, {
	init: function (sel, ctx) {
		if (ctx === zk) {
			if (sel && typeof sel == 'string'
			&& zUtl.isChar(sel.charAt(0), {digit:1,upper:1,lower:1,'_':1})) {
				var el = document.getElementById(sel);
				if (!el || el.id == sel) {
					var ret = jq(el || []);
					ret.context = document;
					ret.selector = '#' + sel;
					ret.zk = new zjq(ret);
					return ret;
				}
				sel = '#' + sel;
			}
			ctx = null;
		}
		if (zk.Widget && zk.Widget.isInstance(sel))
			sel = sel.$n() || '#' + sel.uuid;
		var ret = zjq._fn.init.call(this, sel, ctx);
		ret.zk = new zjq(ret);
		return ret;
	},
	replaceWith: function (w, desktop, skipper) {
		if (!zk.Widget.isInstance(w))
			return zjq._fn.replaceWith.apply(this, arguments);

		var n = this[0];
		if (n) w.replaceHTML(n, desktop, skipper);
		return this;
	}
});
jq.fn.init.prototype = jq.fn;

jq.each(['before','after','append','prepend'], function (i, nm) {
	zjq._fn[nm] = jq.fn[nm];
	jq.fn[nm] = function (w, desktop) {
		if (!zk.Widget.isInstance(w))
			return zjq._fn[nm].apply(this, arguments);

		if (!this.length) return this;
		if (!zk.Desktop._ndt) zk.stateless();

		var ret = zjq._fn[nm].call(this, w._redrawHTML());
		w.bind(desktop);
		zWatch.fireDown('beforeSize', null, w);
		zWatch.fireDown('onSize', null, w);
		return ret;
	};
});

zjq.prototype = { //ZK extension
	widget: function () {
		var ws = [];
		for (var j = this.jq.length; j--;) {
			var w = zk.Widget.$(this.jq[j]);
			if (w) ws.unshift(w);
		}
		return ws;
	},
	cleanVisibility: function () {
		return this.jq.each(function () {
			zjq._cleanVisi(this);
		});
	},
	isVisible: function (strict) {
		var n = this.jq[0];
		return n && (!n.style || (n.style.display != "none" && (!strict || n.style.visibility != "hidden")));
	},
	isRealVisible: function (strict) {
		var n = this.jq[0];
		return n && this.isVisible(strict) && (n.offsetWidth > 0 || n.offsetHeight > 0);
	},

	scrollTo: function () {
		if (this.jq.length) {
			var pos = this.cmOffset();
			scrollTo(pos[0], pos[1]);
		}
		return this;
	},
	scrollIntoView: function (parent) {
		var n = this.jq[0];
		if (n) {
			parent = parent || document.body;
			for (var p = n, c; (p = p.parentNode) && n != parent; n = p)
				c = zjq._scrollIntoView(p, n, c);
		}
		return this;
	},

	sumStyles: function (areas, styles) {
		var val = 0;
		for (var i = 0, len = areas.length, $jq = this.jq; i < len; i++){
			 var w = zk.parseInt($jq.css(styles[areas.charAt(i)]));
			 if (!isNaN(w)) val += w;
		}
		return val;
	},

	setOffsetHeight: function (hgh) {
		var $jq = this.jq;
		hgh -= this.padBorderHeight()
			+ zk.parseInt($jq.css("margin-top"))
			+ zk.parseInt($jq.css("margin-bottom"));
		$jq[0].style.height = jq.px(hgh);
		return this;
	},

	revisedOffset: function (ofs) {
		var el = this.jq[0];
		if(!ofs) {
			if (el.getBoundingClientRect){ // IE and FF3
				var b = el.getBoundingClientRect();
				return [b.left + jq.innerX() - el.ownerDocument.documentElement.clientLeft,
					b.top + jq.innerY() - el.ownerDocument.documentElement.clientTop];
				// IE adds the HTML element's border, by default it is medium which is 2px
				// IE 6 and 7 quirks mode the border width is overwritable by the following css html { border: 0; }
				// IE 7 standards mode, the border is always 2px
				// This border/offset is typically represented by the clientLeft and clientTop properties
				// However, in IE6 and 7 quirks mode the clientLeft and clientTop properties are not updated when overwriting it via CSS
				// Therefore this method will be off by 2px in IE while in quirksmode
			}
			ofs = this.cmOffset();
		}
		var scrolls = zk(el.parentNode).scrollOffset();
		scrolls[0] -= jq.innerX(); scrolls[1] -= jq.innerY();
		return [ofs[0] - scrolls[0], ofs[1] - scrolls[1]];
	},
	revisedWidth: function (size, excludeMargin) {
		size -= this.padBorderWidth();
		if (size > 0 && excludeMargin)
			size -= this.sumStyles("lr", jq.margins);
		return size < 0 ? 0: size;
	},
	revisedHeight: function (size, excludeMargin) {
		size -= this.padBorderHeight();
		if (size > 0 && excludeMargin)
			size -= this.sumStyles("tb", jq.margins);
		return size < 0 ? 0: size;
	},
	padBorderWidth: function () {
		return this.sumStyles("lr", jq.borders) + this.sumStyles("lr", jq.paddings);
	},
	padBorderHeight: function () {
		return this.sumStyles("tb", jq.borders) + this.sumStyles("tb", jq.paddings);
	},
	vflexHeight: function () {
		var el = this.jq[0],
			hgh = el.parentNode.clientHeight;
		if (zk.ie6_) { //IE6's clientHeight is wrong
			var ref = el.parentNode,
				h = ref.style.height;
			if (h && h.endsWith("px")) {
				h = zk(ref).revisedHeight(zk.parseInt(h));
				if (h && h < hgh) hgh = h;
			}
		}

		for (var p = el; p = p.previousSibling;)
			if (p.offsetHeight && zk(p).isVisible())
				hgh -= p.offsetHeight; //may undefined
		for (var p = el; p = p.nextSibling;)
			if (p.offsetHeight && zk(p).isVisible())
				hgh -= p.offsetHeight; //may undefined
		return hgh;
	},
	cellIndex: function () {
		var cell = this.jq[0],
			i = 0;
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
	ncols: function (visibleOnly) {
		var row = this.jq[0],
			cnt = 0, cells;
		if (row && (cells = row.cells))
			for (var j = 0, cl = cells.length; j < cl; ++j) {
				var cell = cells[j];
				if (!visibleOnly || zk(cell).isVisible()) {
					var span = cell.colSpan;
					if (span >= 1) cnt += span;
					else ++cnt;
				}
			}
		return cnt;
	},
	toStyleOffset: function (x, y) {
		var el = this.jq[0],
			oldx = el.style.left, oldy = el.style.top,
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

		var ofs1 = this.cmOffset(),
			x2 = zk.parseInt(el.style.left),
			y2 = zk.parseInt(el.style.top);
		ofs1 = [x - ofs1[0] + x2, y  - ofs1[1] + y2];

		el.style.left = oldx; el.style.top = oldy; //restore
		return ofs1;
	},
	center: function (flags) {
		var el = this.jq[0],
			wdgap = this.offsetWidth(),
			hghgap = this.offsetHeight();

		if ((!wdgap || !hghgap) && !this.isVisible()) {
			el.style.left = el.style.top = "-10000px"; //avoid annoying effect
			el.style.display = "block"; //we need to calculate the size
			wdgap = this.offsetWidth();
			hghgap = this.offsetHeight(),
			el.style.display = "none"; //avoid Firefox to display it too early
		}

		var left = jq.innerX(), top = jq.innerY();
		var x, y, skipx, skipy;

		wdgap = jq.innerWidth() - wdgap;
		if (!flags) x = left + wdgap / 2;
		else if (flags.indexOf("left") >= 0) x = left;
		else if (flags.indexOf("right") >= 0) x = left + wdgap - 1; //just in case
		else if (flags.indexOf("center") >= 0) x = left + wdgap / 2;
		else {
			x = 0; skipx = true;
		}

		hghgap = jq.innerHeight() - hghgap;
		if (!flags) y = top + hghgap / 2;
		else if (flags.indexOf("top") >= 0) y = top;
		else if (flags.indexOf("bottom") >= 0) y = top + hghgap - 1; //just in case
		else if (flags.indexOf("center") >= 0) y = top + hghgap / 2;
		else {
			y = 0; skipy = true;
		}

		if (x < left) x = left;
		if (y < top) y = top;

		var ofs = this.toStyleOffset(x, y);

		if (!skipx) el.style.left = jq.px(ofs[0]);
		if (!skipy) el.style.top =  jq.px(ofs[1]);
		return this;
	},
	position: function (dim, where, opts) {
		where = where || "overlap";
		if (dim.nodeType) //DOM element
			dim = zk(dim).dimension(true);
		var x = dim.left, y = dim.top,
			wd = this.dimension(), hgh = wd.height; //only width and height
		wd = wd.width;

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
			var scX = jq.innerX(),
				scY = jq.innerY(),
				scMaxX = scX + jq.innerWidth(),
				scMaxY = scY + jq.innerHeight();

			if (x + wd > scMaxX) x = scMaxX - wd;
			if (x < scX) x = scX;
			if (y + hgh > scMaxY) y = scMaxY - hgh;
			if (y < scY) y = scY;
		}

		var el = this.jq[0],
			ofs = this.toStyleOffset(x, y);
		el.style.left = jq.px(ofs[0]);
		el.style.top = jq.px(ofs[1]);
		return this;
	},

	scrollOffset: function() {
		var el = this.jq[0],
			t = 0, l = 0;
		do {
			t += el.scrollTop  || 0;
			l += el.scrollLeft || 0;
			el = el.parentNode;
		} while (el);
		return [l, t];
	},
	cmOffset: function () {
		//fix safari's bug: TR has no offsetXxx
		var el = this.jq[0];
		if (zk.safari && el.tagName === "TR" && el.cells.length)
			el = el.cells[0];

		//fix gecko and safari's bug: if not visible before, offset is wrong
		if (!(zk.gecko || zk.safari)
		|| this.isVisible() || this.offsetWidth())
			return zjq._cmOffset(el);

		el.style.display = "";
		var ofs = zjq._cmOffset(el);
		el.style.display = "none";
		return ofs;
	},

	absolutize: function() {
		var el = this.jq[0];
		if (el.style.position == 'absolute') return this;

		var offsets = zjq._posOffset(),
			left = offsets[0], top = offsets[1],
			st = el.style;
		el._$orgLeft = left - parseFloat(st.left  || 0);
		el._$orgTop = top  - parseFloat(st.top || 0);
		st.position = 'absolute';
		st.top = jq.px(top);
		st.left = jq.px(left);
		return this;
	},
	relativize: function() {
		var el = this.jq[0];
		if (el.style.position == 'relative') return this;

		var st = el.style;
		st.position = 'relative';
		var top  = parseFloat(st.top  || 0) - (el._$orgTop || 0),
			left = parseFloat(st.left || 0) - (el._$orgLeft || 0);

		st.top = jq.px(top);
		st.left = jq.px(left);
		return this;
	},

	offsetWidth: function () {
		var el = this.jq[0];
		if (!zk.safari || el.tagName != "TR") return el.offsetWidth;

		var wd = 0;
		for (var cells = el.cells, j = cells.length; j--;)
			wd += cells[j].offsetWidth;
		return wd;
	},
	offsetHeight: function () {
		var el = this.jq[0];
		if (!zk.safari || el.tagName != "TR") return el.offsetHeight;

		var hgh = 0;
		for (var cells = el.cells, j = cells.length; j--;) {
			var h = cells[j].offsetHeight;
			if (h > hgh) hgh = h;
		}
		return hgh;
	},
	offsetTop: function () {
		var el = this.jq[0];
		if (zk.safari && el.tagName === "TR" && el.cells.length)
			el = el.cells[0];
		return el.offsetTop;
	},
	offsetLeft: function () {
		var el = this.jq[0];
		if (zk.safari && el.tagName === "TR" && el.cells.length)
			el = el.cells[0];
		return el.offsetLeft;
	},

	viewportOffset: function() {
		var t = 0, l = 0, el = this.jq[0], p = el;
		do {
			t += p.offsetTop  || 0;
			l += p.offsetLeft || 0;

			// Safari fix
			if (p.offsetParent==document.body)
			if (jq(p).css('position')=='absolute') break;

		} while (p = p.offsetParent);

		do {
			if (!zk.opera || el.tagName=='BODY') {
				t -= el.scrollTop  || 0;
				l -= el.scrollLeft || 0;
			}
		} while (el = el.parentNode);
		return [l, t];
	},
	textSize: function (txt) {
		var el = this.jq[0],
			tsd = zk._txtSizDiv;
		if (!tsd) {
			tsd = zk._txtSizDiv = document.createElement("DIV");
			tsd.style.cssText = "left:-1000px;top:-1000px;position:absolute;visibility:hidden;border:none";
			document.body.appendChild(tsd);
		}

		for (var ss = zjq._TEXT_STYLES, j = ss.length; j--;) {
			var nm = ss[j].$camel();
			tsd.style[nm] = jq(el).css(nm);
		}

		tsd.innerHTML = txt;
		return [tsd.offsetWidth, tsd.offsetHeight];
	},

	dimension: function (revised) {
		var display = this.jq.css('display');
		if (display != 'none' && display != null) // Safari bug
			return this._addOfsToDim(
				{width: this.offsetWidth(), height: this.offsetHeight()}, revised);

	// All *Width and *Height properties give 0 on elements with display none,
	// so enable the element temporarily
		var st = this.jq[0].style,
			originalVisibility = st.visibility,
			originalPosition = st.position,
			originalDisplay = st.display;
		st.visibility = 'hidden';
		st.position = 'absolute';
		st.display = 'block';
		try {
			return this._addOfsToDim(
				{width: this.offsetWidth(), height: this.offsetHeight()}, revised);
		} finally {
			st.display = originalDisplay;
			st.position = originalPosition;
			st.visibility = originalVisibility;
		}
	},
	_addOfsToDim: function (dim, revised) {
		if (revised) {
			var ofs = this.revisedOffset();
			dim.left = ofs[0];
			dim.top = ofs[1];
		} else {
			dim.left = this.offsetLeft();
			dim.top = this.offsetTop();
		}
		return dim;
	},

	redoCSS: function (timeout) {
		zjq._rdcss.push(this.jq[0]);
		setTimeout(zjq._redoCSS, timeout >= 0 ? timeout : 100);
		return this;
	},

	makeVParent: function () {
		var el = this.jq[0],
			p = el.parentNode;
		if (el.vparent || p == document.body) return this; //called twice or not necessary

		var sib = el.nextSibling,
			agtx = el.z_vpagtx = document.createElement("SPAN");
		agtx.style.display = "none";
		if (sib) p.insertBefore(agtx, sib);
		else p.appendChild(agtx);

		el.vparent = p;
		document.body.appendChild(el);
		return this;
	},
	undoVParent: function () {
		var el = this.jq[0],
			p = el.vparent;
		if (p) {
			var agtx = el.z_vpagtx;
			el.vparent = el.z_vpagtx = null;
			if (agtx) {
				p.insertBefore(el, agtx);
				jq(agtx).remove();
			} else
				p.appendChild(el);
		}
		return this;
	},

	//focus/select//
	focus: function (timeout) {
		var n = this.jq[0];
		if (!n || !n.focus) return false;
			//ie: INPUT's focus not function

		var tag = n.tagName;
		if (tag != 'BUTTON' && tag != 'INPUT' && tag != 'A'
		&& tag != 'SELECT' && tag != 'IFRAME')
			return false;

		if (timeout >= 0) setTimeout(function() {zjq._focus(n);}, timeout);
		else zjq._focus(n);
		return true;
	},
	select: function (n, timeout) {
		var n = this.jq[0];
		if (!n || typeof n.select != 'function') return false;

		if (timeout >= 0) setTimeout(function() {zjq._select(n);}, timeout);
		else zjq._select(n);
		return true;
	},

	getSelectionRange: function() {
		var inp = this.jq[0];
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
	setSelectionRange: function (start, end) {
		var inp = this.jq[0],
			len = inp.value.length;
		if (start == null || start < 0) start = 0;
		if (start > len) start = len;
		if (end == null || end > len) end = len;
		if (end < 0) end = 0;

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
		return this;
	},

	//selection//
	disableSelection: function () {
		return this.jq.each(function () {
			zjq._disbSel(this);
		});
	},
	enableSelection: function () {
		return this.jq.each(function () {
			zjq._enbSel(this);
		});
	},

	setStyles: function (styles) {
		var $ = this.jq;
		for (var nm in styles)
			$.css(nm, styles[nm]);
	}
};

zk.copy(jq, { //ZK extension to jq
	px: function (v) {
		return Math.max(v, 0) + "px";
	},

	$$: function (id, subId) {
		return typeof id == 'string' ?
			id ? document.getElementsByName(id + (subId ? '-' + subId : '')): null: id;
	},

	isAncestor: function (p, c) {
		if (!p) return true;
		for (; c; c = c.vparent||c.parentNode)
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
	pageWidth: function () {
		var a = document.body.scrollWidth, b = document.body.offsetWidth;
		return a > b ? a: b;
	},
	pageHeight: function () {
		var a = document.body.scrollHeight, b = document.body.offsetHeight;
		return a > b ? a: b;
	},

	margins: {l: "margin-left", r: "margin-right", t: "margin-top", b: "margin-bottom"},
	borders: {l: "border-left-width", r: "border-right-width", t: "border-top-width", b: "border-bottom-width"},
	paddings: {l: "padding-left", r: "padding-right", t: "padding-top", b: "padding-bottom"},

	scrollbarWidth: function () {
		var tsd = zjq._sbwDiv;
		if (!tsd) {
			tsd = zjq._sbwDiv = document.createElement("DIV");
			tsd.style.cssText = "top:-1000px;left:-1000px;position:absolute;visibility:hidden;border:none;width:50px;height:50px;overflow:scroll;";
			document.body.appendChild(tsd);
		}
		return tsd.offsetWidth - tsd.clientWidth;
	},
	isOverlapped: function (ofs1, dim1, ofs2, dim2) {
		var o1x1 = ofs1[0], o1x2 = dim1[0] + o1x1,
			o1y1 = ofs1[1], o1y2 = dim1[1] + o1y1;
		var o2x1 = ofs2[0], o2x2 = dim2[0] + o2x1,
			o2y1 = ofs2[1], o2y2 = dim2[1] + o2y1;
		return o2x1 <= o1x2 && o2x2 >= o1x1 && o2y1 <= o1y2 && o2y2 >= o1y1;
	},

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

	filterTextStyle: function (style, plus) {
		if (typeof style == 'string') {
			var ts = "";
			if (style)
				for (var j = 0, k = 0; k >= 0; j = k + 1) {
					k = style.indexOf(';', j);
					var s = k >= 0 ? style.substring(j, k): style.substring(j),
						l = s.indexOf(':'),
						nm = l < 0 ? s.trim(): s.substring(0, l).trim();
					if (nm && (zjq._TEXT_STYLES.$contains(nm)
					|| zjq._TEXT_STYLES2.$contains(nm)
					|| (plus && plus.$contains(nm))))
						ts += s + ';';
				}
			return ts;
		}

		var ts = {};
		for (var nm in style)
			if (zjq._TEXT_STYLES.$contains(nm)
			|| zjq._TEXT_STYLES2.$contains(nm)
			|| (plus && plus.$contains(nm)))
				ts[nm] = style[nm];
		return ts;
	},

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

	appendScript: function (src, charset) {
		var e = document.createElement("SCRIPT");
		e.type = "text/javascript";
		e.charset = charset || "UTF-8";
		e.src = src;
		document.getElementsByTagName("HEAD")[0].appendChild(e);
		return this;
	},
	newFrame: function (id, src, style) {
		if (!src) src = zk.ie ? "javascript:false;": "";
			//IE: prevent secure/nonsecure warning with HTTPS

		var html = '<iframe id="'+id+'" name="'+id+'" src="'+src+'"';
		if (style == null) style = 'display:none';
		html += ' style="'+style+'"></iframe>';
		jq(document.body).append(html);
		return zk(id).jq[0];
	},
	newStackup: function (el, id, anchor) {
		el = jq(el||[], zk)[0];
		var ifr = document.createElement('iframe');
		ifr.id = id || (el ? el.id + "-ifrstk": 'z_ifrstk');
		ifr.style.cssText = "position:absolute;overflow:hidden;filter:alpha(opacity=0)";
		ifr.frameBorder = "no";
		ifr.tabIndex = -1;
		ifr.src = zk.ie ? "javascript:false;": "";
			//IE: prevent secure/nonsecure warning with HTTPS
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
	confirm: function (msg) {
		zk.alerting = true;
		try {
			return confirm(msg);
		} finally {
			try {zk.alerting = false;} catch (e) {} //doc might be unloaded
		}
	},
	alert: function (msg) {
		zk.alerting = true;
		try {
			alert(msg);
		} finally {
			try {zk.alerting = false;} catch (e) {} //doc might be unloaded
		}
		return this;
	}
});

zk.copy(zjq, { //private
	_cleanVisi: function (n) {
		n.style.visibility = "inherit";
	},
	_scrollIntoView: function (outer, inner, info) {
		if (outer && inner) {
			var ooft = zk(outer).revisedOffset(),
				ioft = info ? info.oft : zk(inner).revisedOffset(),		 
				top = ioft[1] - ooft[1] + outer.scrollTop,
				ih = info ? info.h : inner.offsetHeight,
				bottom = top + ih,
				updated;
			//for fix the listbox(livedate) keydown select always at top
			if (/*outer.clientHeight < inner.offsetHeight || */ outer.scrollTop > top) {
				outer.scrollTop = top;
				updated = true;
			} else if (bottom > outer.clientHeight + outer.scrollTop) {
				outer.scrollTop = !info ? bottom : bottom - (outer.clientHeight + (inner.parentNode == outer ? 0 : outer.scrollTop));
				updated = true;
			}
			if (updated || !info) {
				if (!info)
					info = {
						oft: ioft,
						h: inner.offsetHeight,
						el: inner
					};
				else info.oft = zk(info.el).revisedOffset();
			}
	        outer.scrollTop = outer.scrollTop;
			return info; 
		}
	},
	_cmOffset: function (el) {
		var t = 0, l = 0, operaBug;
		//Fix gecko difference, the offset of gecko excludes its border-width when its CSS position is relative or absolute
		if (zk.gecko) {
			var p = el.parentNode;
			while (p && p != document.body) {
				var $p = jq(p),
					style = $p.css("position");
				if (style == "relative" || style == "absolute") {
					t += zk.parseInt($p.css("border-top-width"));
					l += zk.parseInt($p.css("border-left-width"));
				}
				p = p.offsetParent;
			}
		}

		do {
			//Bug 1577880: fix originated from http://dev.rubyonrails.org/ticket/4843
			var $el = jq(el);
			if ($el.css("position") == 'fixed') {
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
					zjq._ofsParent(el): el.offsetParent;
			}
		} while (el);
		return [l, t];
	},
	_ofsParent: function (el) {
		if (el.offsetParent) return el.offsetParent;
		if (el == document.body) return el;

		while ((el = el.parentNode) && el != document.body)
			if (el.style && jq(el).css('position') != 'static') //in IE, style might not be available
				return el;

		return document.body;
	},
	_posOffset: function(el) {
		if (zk.safari && el.tagName === "TR" && el.cells.length)
			el = el.cells[0];

		var t = 0, l = 0;
		do {
			t += el.offsetTop  || 0;
			l += el.offsetLeft || 0;
			//Bug 1721158: In FF, el.offsetParent is null in this case
			el = zk.gecko && el != document.body ?
				zjq._ofsParent(el): el.offsetParent;
			if (el) {
				if(el.tagName=='BODY') break;
				var p = jq(el).css('position');
				if (p == 'relative' || p == 'absolute') break;
			}
		} while (el);
		return [l, t];
	},

	_rdcss: [],
	_redoCSS: function (el) {
		if (zjq._rdcss.length) {
			try {
				var el;
				while ((el = zjq._rdcss.pop())) {
					if (el) {
						el.className += ' ';
						if (el.offsetHeight) 
							;
						el.className.trim();
					}
				}
			} catch (e) {
			}
			
			// just in case
			setTimeout(zjq._redoCSS);
		}
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
	_select: function (n) {
		try {
			n.select();
		} catch (e) {
			setTimeout(function() {
				try {n.select();} catch (e) {}
			}, 0);
		} //IE throws exception when select() in some cases
	},

	_disbSel: zk.gecko ? function (el) {
		el.style.MozUserSelect = "none";
	}: zk.safari ? function (el) {
		el.style.KhtmlUserSelect = "none";
	}: zk.ie ? function (el) {
		el.onselectstart = function (evt) {
			var n = evt.target, tag = n.tagName;
			return tag == "TEXTAREA" || tag == "INPUT" && (n.type == "text" || n.type == "password");
		};
	}: zk.$void,
	_enbSel:  zk.gecko ? function (el) {
		el.style.MozUserSelect = "";
	}: zk.safari ? function (el) {
		el.style.KhtmlUserSelect = "";
	}: zk.ie ? function (el) {
		el.onselectstart = null;
	}: zk.$void,

	//refer to http://www.w3schools.com/css/css_text.asp
	_TEXT_STYLES: [
		'font-family', 'font-size', 'font-weight', 'font-style',
		'letter-spacing', 'line-height', 'text-align', 'text-decoration',
		'text-indent', 'text-shadow', 'text-transform', 'text-overflow',
		'direction', 'word-spacing', 'white-space'],
	_TEXT_STYLES2: ["color", "background-color", "background"]
});

zk.copy(jq.prototype, { //Extension to jq() directly
	zsync: function (evtnm) {
		var wgt = this.zk.widget()[0];
		if (wgt) {
			var elem = this[0];
			jq.timers.push(
			function () {
				for (var i = jq.timers.length; i--;)
					if (jq.timers[i].elem === elem)
						return true;
				
				zWatch.fireDown(evtnm || 'onSize', null, wgt);
				return false;
			});
		}
	}
});

zk.copy(jq.Event.prototype, { //ZK extension to jQuery.Event
	stop: function () {
		this.preventDefault();
		this.stopPropagation();
	},
	mouseData: function () {
		return zk.copy({
			pageX: this.pageX, pageY: this.pageY
		}, this.metaData());
	},
	keyData: function () {
		return zk.copy({
			keyCode: this.keyCode,
			charCode: this.charCode
			}, this.metaData());
	},
	metaData: function () {
		var inf = {};
		if (this.altKey) inf.altKey = true;
		if (this.ctrlKey) inf.ctrlKey = true;
		if (this.shiftKey) inf.shiftKey = true;
		inf.which = this.which || 0;
		return inf;
	}
});

zk.copy(jq.event, {
	stop: function (evt) {
		evt.stop();
	},
	filterMetaData: function (data) {
		var inf = {}
		if (data.altKey) inf.altKey = true;
		if (data.ctrlKey) inf.ctrlKey = true;
		if (data.shiftKey) inf.shiftKey = true;
		inf.which = data.which || 0;
		return inf;
	},
	toEvent: function (evt, wgt) {
		var type = evt.type,
			target = zk.Widget.$(evt) || wgt,
			data, opts;

		if (type.startsWith('mouse')) {
			if (type.length > 5)
				type = 'Mouse' + type.charAt(5).toUpperCase() + type.substring(6);
			data = evt.mouseData();
		} else if (type.startsWith('key')) {
			if (type.length > 3)
				type = 'Key' + type.charAt(3).toUpperCase() + type.substring(4);
			data = evt.keyData();
		} else if (type == 'dblclick') {
			data = evt.mouseData();
			opts = {ctl:true};
			type = 'DoubleClick';
		} else {
			if (type == 'click') {
				data = evt.mouseData();
				opts = {ctl:true};
			}
			type = type.charAt(0).toUpperCase() + type.substring(1);
		}
		return new zk.Event(target, 'on' + type, data, opts, evt);
	}
});
