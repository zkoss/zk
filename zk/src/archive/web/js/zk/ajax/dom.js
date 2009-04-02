/* dom.js

	Purpose:

	Description:

	History:
		Mon Sep 29 17:17:32	 2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

Some of the codes are adopted from http://prototype.conio.net and http://script.aculo.us

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
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
	pageWidth: function () {
		var a = document.body.scrollWidth, b = document.body.offsetWidth;
		return a > b ? a: b;
	},
	pageHeight: function () {
		var a = document.body.scrollHeight, b = document.body.offsetHeight;
		return a > b ? a: b;
	},

	scrollTo: function (n) {
		n = zDom.$(n);
		var pos = zDom.cmOffset(n);
		scrollTo(pos[0], pos[1]);
	},
	scrollIntoView: function (el, ancestor) {
		if (arguments.length == 1) {
			el = zDom.$(el);
			if (el)
				for (var p = el; p = p.parentNode;)
					zDom._scrvw(p, el);
		} else
			zDom._scrvw(zDom.$(ancestor), zDom.$(el));
	},
	_scrvw: function (outer, inner) {
		if (outer && inner) {
			var padding = zk.parseInt(zDom.getStyle(inner, "padding-top"));
			var limit = inner.offsetTop - padding;
			if (limit < outer.scrollTop) {
				outer.scrollTop = limit;
			} else {
				limit = 3 + inner.offsetTop + inner.offsetHeight
					- outer.scrollTop - outer.clientHeight;
				if (limit > 0) outer.scrollTop += limit;
			}
		}
	},

	margins: {l: "margin-left", r: "margin-right", t: "margin-top", b: "margin-bottom"},
	borders: {l: "border-left-width", r: "border-right-width", t: "border-top-width", b: "border-bottom-width"},
	paddings: {l: "padding-left", r: "padding-right", t: "padding-top", b: "padding-bottom"},
	sumStyles: function (el, areas, styles) {
		var val = 0;
		for (var i = 0, len = areas.length; i < len; i++){
			 var w = zk.parseInt(zDom.getStyle(el, styles[areas.charAt(i)]));
			 if (!isNaN(w)) val += w;
		}
		return val;
	},

	setOffsetHeight: function (el, hgh) {
		hgh -= zDom.padBorderHeight(el)
			+ zk.parseInt(zDom.getStyle(el, "margin-top"))
			+ zk.parseInt(zDom.getStyle(el, "margin-bottom"));
		el.style.height = Math.max(0, hgh) + "px";
	},

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
	revisedWidth: function (el, size, excludeMargin) {
		size -= zDom.padBorderWidth(el);
		if (size > 0 && excludeMargin)
			size -= zDom.sumStyles(el, "lr", zDom.margins);
		return size < 0 ? 0: size;
	},
	revisedHeight: function (el, size, excludeMargin) {
		size -= zDom.padBorderHeight(el);
		if (size > 0 && excludeMargin)
			size -= zDom.sumStyles(el, "tb", zDom.margins);
		return size < 0 ? 0: size;
	},
	padBorderWidth: function (el) {
		return zDom.sumStyles(el, "lr", zDom.borders) + zDom.sumStyles(el, "lr", zDom.paddings);
	},
	padBorderHeight: function (el) {
		return zDom.sumStyles(el, "tb", zDom.borders) + zDom.sumStyles(el, "tb", zDom.paddings);
	},
	scrollbarWidth: function (el) {
		return (el.offsetWidth - el.clientWidth) + zDom.sumStyles(el, "lr", zDom.borders);
	},
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
	ncols: function (row, visibleOnly) {
		var cnt = 0, cells;
		if (row && (cells = rows.cells)) {
			for (var j = 0, cl = cells.length; j < cl; ++j)
				if (!visibleOnly || zDom.isVisible(cl)) {
					var span = cells[j].colSpan;
					if (span >= 1) cnt += span;
					else ++cnt;
				}
		}
		return cnt;
	},
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
		if (dim.nodeType) //DOM element
			dim = zDom.dimension(dim, true);
		var x = dim.left, y = dim.top,
			wd = zDom.dimension(el), hgh = wd.height; //only width and height
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
			var scX = zDom.innerX(),
				scY = zDom.innerY(),
				scMaxX = scX + zDom.innerWidth(),
				scMaxY = scY + zDom.innerHeight();

			if (x + wd > scMaxX) x = scMaxX - wd;
			if (x < scX) x = scX;
			if (y + hgh > scMaxY) y = scMaxY - hgh;
			if (y < scY) y = scY;
		}

		var ofs = zDom.toStyleOffset(el, x, y);
		el.style.left = ofs[0] + "px";
		el.style.top = ofs[1] + "px";
	},

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

	offsetParent: function (el) {
		if (el.offsetParent) return el.offsetParent;
		if (el == document.body) return el;

		while ((el = el.parentNode) && el != document.body)
			if (el.style && zDom.getStyle(el, 'position') != 'static') //in IE, style might not be available
				return el;

		return document.body;
	},
	offsetWidth: function (el) {
		if (!el) return 0;
		if (!zk.safari || zDom.tag(el) != "TR") return el.offsetWidth;

		var wd = 0;
		for (var cells = el.cells, j = cells.length; --j >= 0;)
			wd += cells[j].offsetWidth;
		return wd;
	},
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
	offsetTop: function (el) {
		if (!el) return 0;
		if (zk.safari && zDom.tag(el) === "TR" && el.cells.length)
			el = el.cells[0];
		return el.offsetTop;
	},
	offsetLeft: function (el) {
		if (!el) return 0;
		if (zk.safari && zDom.tag(el) === "TR" && el.cells.length)
			el = el.cells[0];
		return el.offsetLeft;
	},

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
	textSize: function (el, txt) {
		var tsd = zk._txtSizDiv;
		if (!tsd) {
			tsd = zk._txtSizDiv = document.createElement("DIV");
			tsd.style.cssText = "left:-1000px;position:absolute;visibility:hidden;border:none";
			document.body.appendChild(tsd);
		}

		for (var ss = zDom._TEXT_STYLES, j = ss.length; --j >= 0;)
			tsd.style[ss[j]] = zDom.getStyle(el, ss[j]);

		tsd.innerHTML = txt;
		return [tsd.offsetWidth, tsd.offsetHeight];
	},
	//refer to http://www.w3schools.com/css/css_text.asp
	_TEXT_STYLES: [
		'fontFamily', 'fontSize', 'fontWeight', 'fontStyle',
		'letterSpacing', 'lineHeight', 'textAlign', 'textDecoration',
		'textIndent', 'textShadow', 'textTransform', 'textOverflow',
		'direction', 'wordSpacing', 'whiteSpace'],

	dimension: function (el, revised) {
		var display = zDom.getStyle(el,  'display');
		if (display != 'none' && display != null) // Safari bug
			return zDom._addOfsToDim(el,
				{width: zDom.offsetWidth(el), height: zDom.offsetHeight(el)}, revised);

	// All *Width and *Height properties give 0 on elements with display none,
	// so enable the el temporarily
		var st = el.style,
			originalVisibility = st.visibility,
			originalPosition = st.position,
			originalDisplay = st.display;
		st.visibility = 'hidden';
		st.position = 'absolute';
		st.display = 'block';
		try {
			return zDom._addOfsToDim(el,
				{width: zDom.offsetWidth(el), height: zDom.offsetHeight(el)}, revised);
		} finally {
			st.display = originalDisplay;
			st.position = originalPosition;
			st.visibility = originalVisibility;
		}
	},
	_addOfsToDim: function (el, dim, revised) {
		if (revised) {
			var ofs = zDom.revisedOffset(el);
			dim.left = ofs[0];
			dim.top = ofs[1];
		} else {
			dim.left = zDom.offsetLeft(el);
			dim.top = zDom.offsetTop(el);
		}
		return dim;
	},

	_clsregexs: {}, //Refer to http://developer.yahoo.net/yui
	_clsRegEx: function (clsnm) {
		var re = zDom._clsregexs[clsnm];
		if (!re) {
			re = new RegExp('(?:^|\\s+)' + clsnm + '(?:\\s+|$)', 'g');
			zDom._clsregexs[clsnm] = re;
		}
		return re;
	},
	//class and style//
	hasClass: function (el, clsnm) {
		return el && el.className.match(zDom._clsRegEx(clsnm)) != null;
	},
	addClass: function (el, clsnm) {
		if (el && !zDom.hasClass(el, clsnm)) {
			var cn = el.className;
			if (cn.length) cn += ' ';
			el.className = cn + clsnm;
		}
	},
	rmClass: function (el, clsnm) {
		if (el && zDom.hasClass(el, clsnm)) {
			var re = zDom._clsRegEx(clsnm);
			el.className = el.className.replace(re, " ").trim();
		}
	},

	_txtstyles: ["color", "background-color", "background",	"white-space"],

	filterTextStyle: function (style, plus) {
		var ts = "";
		for (var j = 0, k = 0; k >= 0; j = k + 1) {
			k = style.indexOf(';', j);
			var s = k >= 0 ? style.substring(j, k): style.substring(j);
			var l = s.indexOf(':');
			var nm = l < 0 ? s.trim(): s.substring(0, l).trim();

			if (nm.startsWith("font")  || nm.startsWith("text")
			|| zDom._txtstyles.$contains(nm) || (plus && plus.$contains(nm)))
				ts += s + ';';
		}
		return ts;
	},

	getStyle: function(el, style) {
		var st = el.style;
		style = style.$camel();
		if (['float','cssFloat'].$contains(style))
			style = (typeof st.styleFloat != 'undefined' ? 'styleFloat' : 'cssFloat');
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
	setStyles: function(el, styles) {
		var st = el.style;
		for (var name in styles) {
			var value = styles[name];
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

	redoCSS: function (el) {
		el = zDom.$(el);
		if (el) {
			try {
				el.className += ' ';
				el.className.trim();
			} catch (e) {
			}
		}
	},
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
			n.innerHTML = zDom._rmOuter(html);
		} else { //non-IE
			var range = n.ownerDocument.createRange();
			range.setStartBefore(n);
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
	_rmOuter: function (html) {
		var j = html.indexOf('>') + 1, k = html.lastIndexOf('<');
		return k >= j ? html.substring(j, k): html;
	},
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

	insertAfter: function (el, ref) {
		var sib = ref.nextSibling;
		if (sib) ref.parentNode.insertBefore(el, sib);
		else ref.parentNode.appendChild(el);
	},
	insertBefore: function (el, ref) {
		ref.parentNode.insertBefore(el, ref);
	},
	remove: function (n) {
		n = zDom.$(n);
		if (n && n.parentNode) n.parentNode.removeChild(n);
	},

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
	makeVParent: function (el) {
		var p = el.parentNode;
		if (el.z_vp || p == document.body) return; //called twice or not necessary

		var sib = el.nextSibling,
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

	newStackup: function (el, id, anchor) {
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
	},

	getAttr: function (el, nm) {
		try {
			return el && el.getAttribute ? el.getAttribute(nm): null;
		} catch (e) {
			return null; //IE6: failed if el is TABLE and attribute not there
		}
	},
	setAttr: function (el, nm, val) {
		if (el && el.setAttribute) el.setAttribute(nm, val);
	},
	rmAttr: function (el, nm) {
		if (el && el.removeAttribute) el.removeAttribute(nm);
		else zDom.setAttr(el, nm, "");
	},

	/** Creates one if not found. */
	newFrame: function (id, src, style) {
		if (!src) src = zAu.comURI('/web/img/spacer.gif');
			//IE with HTTPS: we must specify the src

		var html = '<iframe id="'+id+'" name="'+id+'" src="'+src+'"';
		if (style) html += ' style="'+style+'"';
		html += '></iframe>';
		zDom.insertHTMLBeforeEnd(document.body, html);
		return zDom.$(id);
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
