/* common.js

{{IS_NOTE
	Purpose:
		Common utiltiies.
	Description:

	History:
		Fri Jun 10 18:16:11     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This file is distributed under LGPL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
if (!window.anima) { //avoid eval twice

// Standard //
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
String.prototype.skipWhitespaces = function (j) {
	for (var tl = this.length;j < tl; ++j) {
		var cc = this.charAt(j);
		if (cc != ' ' && cc != '\t' && cc != '\n' && cc != '\r')
			break;
	}
	return j;
};
String.prototype.nextWhitespace = function (j) {
	for (var tl = this.length;j < tl; ++j) {
		var cc = this.charAt(j);
		if (cc == ' ' || cc == '\t' || cc == '\n' || cc == '\r')
			break;
	}
	return j;
};

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

//This prototype is provided by the Mozilla foundation and is distributed under
/// the MIT license.
//http://www.ibiblio.org/pub/Linux/LICENSES/mit.license
if (!Array.prototype.forEach) {
	/**
	 * forEach is a JavaScript extension to the ECMA-262 standard; as such it may not
	 * be present in other implementations of the standard. (like IE)
	 * @param {Funcation} fun Function to execute for each element.
	 * @param {Object} thisp Object to use as this when executing callback, if any.
	 * @since 3.5.0
	 */
	Array.prototype.forEach = function(fun /*, thisp*/){
		if (typeof fun != "function")
			throw new TypeError();

		var thisp = arguments[1];
		for (var i = 0, len = this.length; i < len; i++) {
			if (i in this)
				fun.call(thisp, this[i], i, this);
		}
	};
}

//This prototype is provided by the Mozilla foundation and is distributed under
/// the MIT license.
//http://www.ibiblio.org/pub/Linux/LICENSES/mit.license
if (!Array.prototype.indexOf) {
	/**
	 * indexOf is a JavaScript extension to the ECMA-262 standard; as such it may
	 * not be present in other implementations of the standard. (like IE)
	 * @param {Element} Element to locate in the array.
	 * @param {Number} formIndex The index at which to begin the search.
	 * Defaults to 0, i.e. the whole array will be searched.
	 * If the index is greater than or equal to the length of the array, -1 is returned,
	 * i.e. the array will not be searched. If negative, it is taken as the offset
	 * from the end of the array.
	 * Note that even when the index is negative, the array is still searched from
	 * front to back. If the calculated index is less than 0, the whole array will be searched.
	 * @since 3.5.0
	 */
	Array.prototype.indexOf = function(elt /*, fromIndex*/) {
		var from = Number(arguments[1]) || 0,
			len = this.length;
		from = (from < 0) ? Math.ceil(from) : Math.floor(from);
		if (from < 0)
			from += len;

		for (; from < len; from++) {
			if (from in this &&	this[from] === elt)
				return from;
		}
		return -1;
	};
}
////
//Form//
function z_fmsubm(a, b, c) {
	var fns = this._submfns;
	for (var j = 0, fl = (fns ? fns.length: 0); j < fl; ++j)
		fns[j].apply(this, arguments);
	return this._ogsubm(a, b, c);
		//If IE, we cannot use _ogsubm.apply. Reason unknown.
};
if (zk.ie) {
	zk.fixSubmit = function (n) {
		n._ogsubm = n.submit;
		n.submit = z_fmsubm;
	}

	//Step 1. Override document.createElement
	zk._newElem = document.createElement;
	document.createElement = function (tag) {
		var n = zk._newElem(tag);
			//we cannot use zk._newElem.apply. Reason unknown.
		if (tag.toUpperCase() == "FORM")
			zk.fixSubmit(n);
		return n;
	};

	//Step 2: HTC (http://delete.me.uk/2004/09/addbehaviour.html)
	//Due to performance issue and unable to really make it work
	//we change submit in zk.init

} else {
	HTMLFormElement.prototype._ogsubm = HTMLFormElement.prototype.submit;
	HTMLFormElement.prototype.submit = z_fmsubm;
}

//////
// More zk utilities (defined also in boot.js) //
/** Returns an array to indicate the size of the text if it is placed
 * inside the element.
 * @since 3.6.0
 */
zk.getTextSize = function (el, txt) {
	var tsd = zk._txtSizDiv;
	if (!tsd) {
		tsd = zk._txtSizDiv = document.createElement("DIV");
		tsd.style.cssText = "top:-1000px;left:-1000px;position:absolute;visibility:hidden;border:none";
		document.body.appendChild(tsd);
	}

	for (var ss = zk.TEXT_STYLES, j = ss.length; --j >= 0;)
		tsd.style[ss[j]] = Element.getStyle(el, ss[j]);

	tsd.innerHTML = txt;
	return [tsd.offsetWidth, tsd.offsetHeight];
};
//refer to http://www.w3schools.com/css/css_text.asp
zk.TEXT_STYLES = [
	'fontFamily', 'fontSize', 'fontWeight', 'fontStyle',
	'letterSpacing', 'lineHeight', 'textAlign', 'textDecoration',
	'textIndent', 'textShadow', 'textTransform', 'textOverflow',
	'direction', 'wordSpacing', 'whiteSpace'];

zk.Shadow = zClass.create();
zk.Shadow.prototype = {
	_HTML: zk.ie6Only ? '" class="z-shadow"></div>':
		'" class="z-shadow"><div class="z-shadow-tl"><div class="z-shadow-tr"></div></div>'
		+'<div class="z-shadow-cl"><div class="z-shadow-cr"><div class="z-shadow-cm">&#160;</div></div></div>'
		+'<div class="z-shadow-bl"><div class="z-shadow-br"></div></div></div>',

	/**
	 * Constructor of the Shadow object.
	 *
	 * @param node the element to associate the shadow.
	 * @param opts The options
	 * <p>Alowed options:
	 * <ul>
	 * <li>left: The margin at left. Default: 4.</li>
	 * <li>right: The margin at right. Default: 4.</li>
	 * <li>top: the margin at top. Default: 3.</li>
	 * <li>bottom: the margin at bottom. Default: 3.</li>
	 * <li>autoShow: true to show the shadow in the initial phase. Default: false</li>
	 * <li>stackup: whether to create a stackup</li>
	 * </ul>
	 */
	initialize: function (node, opts) {
		opts = this.opts = zk.$default(opts, {
			left: 4, right: 4, top: 3, bottom: 3
		});
		if (zk.ie6Only) {
			opts.left -= 1;
			opts.right -= 8;
			opts.top -= 2;
			opts.bottom -= 6;
		}

		var sdwid = node.id + "!shadow";
		node.parentNode.insertAdjacentHTML("afterBegin", '<div id="'+sdwid+this._HTML);
		this.node = node;
		this.shadow = $e(sdwid);

		if (opts.autoShow) this.sync();
	},
	hide: function(){
		this.shadow.style.display = "none";
		if (this.stackup) this.stackup.style.display = "none";
	},
	sync: function () {
		var node = this.node, shadow = this.shadow;
		if (!node || !zk.isVisible(node)) {
			this.hide();
			return;
		}

		for (var c = shadow;;) {
			if (!(c = c.nextSibling) || c.tagName) {
				if (c != node)
					node.parentNode.insertBefore(shadow, node);
				break;
			}
		}
		shadow.style.zIndex = $int(Element.getStyle(node, "zIndex"));

		var opts = this.opts,
			l = node.offsetLeft, t = node.offsetTop,
			w = node.offsetWidth, h = node.offsetHeight,
			wd = w - opts.left + opts.right,
			hgh = h - opts.top + opts.bottom,
			st = shadow.style;
		st.left = (l + opts.left) + "px";
		st.top = (t + opts.top) + "px";
		st.width = Math.max(wd, 0) + "px";
		st.display = "block";
		if (zk.ie6Only) st.height = Math.max(hgh, 0) + "px";
		else {
			var cns = shadow.childNodes;
			cns[1].style.height = Math.max(hgh - cns[0].offsetHeight - cns[2].offsetHeight, 0) + "px";
		}

		var stackup = this.stackup;
		if(opts.stackup && node) {
			if(!stackup)
				stackup = this.stackup =
					zk.makeStackup(node, node.id + '!sdwstk', shadow);

			st = stackup.style;
			st.left = l +"px";
			st.top = t +"px";
			st.width = w +"px";
			st.height = h +"px";
			st.zIndex = $int(Element.getStyle(node, "zIndex"));
			st.display = "block";
		}
	},
	cleanup: function () {
		zk.remove(this.shadow);
		zk.remove(this.stackup);
		this.cmp = this.shadow = this.stackup = null;
	}
};


/**
 * Creates a 'stackup' (actually, an iframe) that makes
 * an element (with position:absolute) shown above others.
 * @since 3.5.0
 */
zk.makeStackup = function (cmp, id, anchor) {
	var ifr = document.createElement('iframe');
	ifr.id = id || (cmp ? cmp.id + "!ifrstk": 'z_ifrstk');
	ifr.style.cssText = "position:absolute;overflow:hidden;filter:alpha(opacity=0)";
	ifr.frameBorder = "no";
	ifr.tabIndex = -1;
	ifr.src = "";
	if (cmp) {
		ifr.style.left = cmp.style.left;
		ifr.style.top = cmp.style.top;
		ifr.style.width = cmp.offsetWidth + "px";
		ifr.style.height = cmp.offsetHeight + "px";
		cmp.parentNode.insertBefore(ifr, anchor || cmp);
	}
	return ifr;
};
/**
 * Applies the indicator mask over the specified element.
 * @param {Object/String} rel a related object
 * @param {String} message a message for the loading indicator. Default: mesg.LOADING
 * @since 3.5.0
 */
zk.applyMask = function (rel, message) {
	if (typeof rel == "string") rel = $e(rel);
	if (!rel || !zk.isRealVisible(rel, true)) return; //nothing do to.
	var progbox = $e(rel.id + "!progbox");
	if (progbox) return progbox;
	if (!message) message = window.mesg ? mesg.LOADING: 'Loading...';
	var n = document.createElement("DIV");
	document.body.appendChild(n);
	var xy = zk.revisedOffset(rel),
		w = zk.offsetWidth(rel),
		h = zk.offsetHeight(rel),
		html = '<div id="'+rel.id+'!progbox" style="visibility:hidden">'
			+ '<div class="z-apply-mask" style="display:block;top:' + xy[1]
			+ 'px;left:' + xy[0] + 'px;width:' + w + 'px;height:' + h + 'px;"></div>'
			+ '<div id="'+rel.id+'!z-loading" class="z-apply-loading"><div class="z-apply-loading-indicator">'
			+ '<span class="z-apply-loading-icon z-inline-block" alt="..."></span> '
			+ message+ '</div></div></div>';
	zk.setOuterHTML(n, html);
	var loading = $e(rel.id+"!z-loading"), progbox = $e(rel.id + "!progbox");
	if (loading) {
		if (loading.offsetHeight > rel.offsetHeight)
			loading.style.height = zk.revisedSize(loading, rel.offsetHeight, true) + "px";
		if (loading.offsetWidth > rel.offsetWidth)
			loading.style.width = zk.revisedSize(loading, rel.offsetWidth) + "px";
		loading.style.top = (xy[1] + ((h - loading.offsetHeight) /2)) + "px";
		loading.style.left = (xy[0] + ((w - loading.offsetWidth) /2)) + "px";
	}
	progbox.style.visibility = "";
	return progbox;
};
/** Override a method of the specified object.
 *
 * Example:
zk.override(zkau.cmd1, "outer", mine, function (uuid, cmp, html) {
	mine.outer(uuid, cmp, html);
});
 *
 * @param obj the object containing the method
 * @param fn the method name
 * @param supobj the object to have the super method (i.e., previous method)
 * @since 3.0.5
 */
zk.override = function (obj, fn, supobj, func) {
	supobj[fn] = obj[fn];
	obj[fn] = func;
};

/**
 * Forces the browser to redo the CSS.
 * @since 3.5.0
 */
zk.redoCSS = zk.ie ? function (el, timeout) {
	el = $e(el);
	if (el) {
		try {
			var z = el.style.zoom;
			el.style.zoom = 1;
			el.className += ' ';
			if (el.offsetHeight);
			el.className.trim();
			setTimeout(function () {try {
				el.style.zoom = z;
			} catch (e){}}, timeout > 0 ? timeout: 1);
		} catch (e) {
		}
	}
} : function (el) {
	el = $e(el);
	if (el) {
		try {
			el.className += ' ';
			if (el.offsetHeight);
			el.className.trim();
		} catch (e) {
		}
	}
};
/**
 * Redraws the element by use of setOuterHTML.
 * @since 3.0.4
 */
zk.reOuter = function (cmp) {
	if (cmp) {
		cmp = $outer(cmp);
		zkau.cmd1.outer(cmp.id, cmp, zk.getOuterHTML(cmp));
	}
};
/**
 * Returns the outerHTML of the element.
 * @since 3.0.4
 */
zk.getOuterHTML = function (cmp) {
	if (cmp.outerHTML) return cmp.outerHTML;
	var div = document.createElement("DIV");
	var clone = cmp.cloneNode(true);
	div.appendChild(clone);
	return div.innerHTML;
}
/** To prevent onblur if alert is shown.
 * Note: browser will change the focus back, so it is safe to ingore.
 * @since 3.0.5
 */
zk.alert = function (msg) {
	zk.alerting = true;
	try {
		alert(msg);
	} finally {
		try {zk.alerting = false;} catch (e) {} //doc might be unloaded
	}
};
/** To confirm the user for an activity.
 * @since 3.0.6
 */
zk.confirm = function (msg) {
	zk.alerting = true;
	try {
		return confirm(msg);
	} finally {
		try {zk.alerting = false;} catch (e) {} //doc might be unloaded
	}
};
/** The css keywords of ZK framework, since 3.5.0 .*/
zk.cssKeywords = ["over", "click", "seld", "disd"];
/**
 * Returns the real class name that determines whether the end of the css name of
 * the specified element does not match the special string where is existed
 * in ZK framework. i.e zk.cssKeywords, including "over", "click", "seld", and "disd".
 * This match of the real class name is from the end to the beginning.
 * For example,
 * 	If el.className is a "ab ab-over abc disd abc-seld" string, the "abc" class name is returned.
 * <p>Note: if not one to be foundd, the "" string is assumed.</p>
 * @param {Object} el an element.
 * @param {Array} keywords an array that has some special keywords you want to ingore.
 * @since 3.5.0
 */
zk.realClass = function (el, keywords) {
	var cn = el.className.trim(), kds = keywords ? keywords.concat(zk.cssKeywords) : zk.cssKeywords;
 	if (cn) {
		for (var has, cs = cn.split(" "), i = cs.length; --i >= 0;) {
			if (!cs[i].trim()) continue;
			for (var j = kds.length; --j >= 0;) {
				if (cs[i] == kds[j] || cs[i].endsWith("-" + kds[j])) {
					has = true;
					break;
				}
			}
			if (has) {
				has = false;
				continue;
			}
			return cs[i];
		}
	}
	return "";
};

zk._clsregexs = {}; //Refer to http://developer.yahoo.net/yui
zk._clsRegEx = function(clsnm) {
	var re = zk._clsregexs[clsnm];
	if (!re) {
		re = new RegExp('(?:^|\\s+)' + clsnm + '(?:\\s+|$)', 'g');
		zk._clsregexs[clsnm] = re;
	}
	return re;
};
/** Returns whether it is part of the class name
 * of the specified element.
 */
zk.hasClass = function (el, clsnm) {
	return el.className.match(zk._clsRegEx(clsnm)) != null;
};

/** Adds the specified class name to the class name of the specified element.
 * @since 3.0.0
 */
zk.addClass = function (el, clsnm, bAdd) {
	if (bAdd == false) {
		zk.rmClass(el, clsnm);
		return;
	}

	if (!zk.hasClass(el, clsnm)) {
		var cn = el.className;
		if (cn.length) cn += ' ';
		el.className = cn + clsnm;
	}
};
/** Removes the specified class name from the the class name of the specified
 * element.
 * @since 3.0.0
 */
zk.rmClass = function (el, clsnm, bRemove) {
	if (bRemove == false) {
		zk.addClass(el, clsnm);
		return;
	}

	if (zk.hasClass(el, clsnm)) {
		var re = zk._clsRegEx(clsnm);
		el.className = el.className.replace(re, " ").trim();
	}
};

/** Sets the offset height. */
zk.setOffsetHeight = function (el, hgh) {
	hgh = hgh - zk.getPadBorderHeight(el)
		- $int(Element.getStyle(el, "margin-top"))
		- $int(Element.getStyle(el, "margin-bottom"));
	el.style.height = (hgh > 0 ? hgh: 0) + "px";
};

zk.borders = ["borderTopWidth", "borderRightWidth", "borderBottomWidth", "borderLeftWidth"];
zk.paddings = ["paddingTop", "paddingRight", "paddingBottom", "paddingLeft"];
zk.margins = ["marginTop", "marginRight", "marginBottom", "marginLeft"];
zk.boxIndices = {width: [1,3], height: [0,2], box: [0,1,2,3]};
/**
 * Returns the number of the margin width.
 * @since 3.5.0
 */
zk.getMarginWidth = function (el) {
	return zk.sumStyles(el, "width", zk.margins);
};
/**
 * Returns the number of the margin height.
 * @since 3.5.0
 */
zk.getMarginHeight = function (el) {
	return zk.sumStyles(el, "height", zk.margins);
};
/**
 * Returns the number of the border width.
 * @since 3.5.0
 */
zk.getBorderWidth = function (el) {
	return zk.sumStyles(el, "width", zk.borders);
};
/**
 * Returns the number of the border height.
 * @since 3.5.0
 */
zk.getBorderHeight = function (el) {
	return zk.sumStyles(el, "height", zk.borders);
};
/**
 * Returns the number of the padding height.
 * @since 3.5.0
 */
zk.getPaddingWidth = function (el) {
	return zk.sumStyles(el, "width", zk.paddings);
};
/**
 * Returns the number of the padding height.
 * @since 3.5.0
 */
zk.getPaddingHeight = function (el) {
	return zk.sumStyles(el, "height", zk.paddings);
};
/**
 * Returns the number of the padding width and the border width from the specified element.
 * @since 3.5.0
 */
zk.getPadBorderWidth = function (el) {
	return zk.getPaddingWidth(el) + zk.getBorderWidth(el);
};
/**
 * Returns the number of the padding height and the border height from the specified element.
 * @since 3.5.0
 */
zk.getPadBorderHeight = function (el) {
	return zk.getPaddingHeight(el) + zk.getBorderHeight(el);
};
/** Return el.offsetWidth, which solving Safari's bug. */
zk.offsetWidth = function (el) {
	if (!el) return 0;
	if (!zk.safari || $tag(el) != "TR") return el.offsetWidth;

	var wd = 0;
	for (var j = el.cells.length; --j >= 0;)
		wd += el.cells[j].offsetWidth;
	return wd;
};
/** Return el.offsetHeight, which solving Safari's bug. */
zk.offsetHeight = function (el) {
	if (!el) return 0;
	if (!zk.safari || $tag(el) != "TR") return el.offsetHeight;

	var hgh = 0;
	for (var j = el.cells.length; --j >= 0;) {
		var h = el.cells[j].offsetHeight;
		if (h > hgh) hgh = h;
	}
	return hgh;
};
/** Returns el.offsetTop, which solving Safari's bug. */
zk.offsetTop = function (el) {
	if (!el) return 0;
	if (zk.safari && $tag(el) === "TR" && el.cells.length)
		el = el.cells[0];
	return el.offsetTop;
};
/** Returns el.offsetLeft, which solving Safari's bug. */
zk.offsetLeft = function (el) {
	if (!el) return 0;
	if (zk.safari && $tag(el) === "TR" && el.cells.length)
		el = el.cells[0];
	return el.offsetLeft;
};
/** Returns the summation of the specified styles.
 *  For example,
 *  zk.sumStyles(el, "width", zk.paddings) sums the style values of
 * paddingLeft and paddingRight from the specified element.
 *
 * @param {String} type 'width', 'height', and 'box', which means the number of the width and the height.
 * @param styles {zk.paddings} or {zk.borders}.
 * @return {Number}
 * @since 3.0.0
 */
zk.sumStyles = function (el, type, styles) {
	var indices = zk.boxIndices[type],
		val = 0;
	for (var sz, i = 0, j = indices.length; i < j; i++) {
		sz = $int(Element.getStyle(el, styles[indices[i]]));
		if (!isNaN(sz)) val += sz;
	}
    return val;
};
/**
 * Returns the revised size, which subtracted the size of its CSS border or padding, for the specified element.
 * @param {Number} size original size of the specified element.
 * @param {Boolean} isHgh if true it will be "tb" top and bottom.
 * @return {Number}
 * @since 3.0.0
 */
zk.revisedSize = function (el, size, isHgh) {
    size -= isHgh ? zk.getPadBorderHeight(el) : zk.getPadBorderWidth(el);
	if (size < 0) size = 0;
	return size;
};
/**
 * Returns the revised position, which subtracted the offset of its scrollbar,
 * for the specified element.
 * @param {Object} el
 * @param {Array} ofs [left, top];
 * @return {Array} [left, top];
 * @since 3.0.0
 */
zk.revisedOffset = function (el, ofs) {
	if(!ofs) {
		if (el.getBoundingClientRect){ // IE and FF3
			var b = el.getBoundingClientRect();
			return [b.left + zk.innerX() - el.ownerDocument.documentElement.clientLeft,
				b.top + zk.innerY() - el.ownerDocument.documentElement.clientTop];
			// IE adds the HTML element's border, by default it is medium which is 2px
			// IE 6 and 7 quirks mode the border width is overwritable by the following css html { border: 0; }
			// IE 7 standards mode, the border is always 2px
			// This border/offset is typically represented by the clientLeft and clientTop properties
			// However, in IE6 and 7 quirks mode the clientLeft and clientTop properties are not updated when overwriting it via CSS
			// Therefore this method will be off by 2px in IE while in quirksmode
		}
		ofs = zPos.cumulativeOffset(el);
	}
	var scrolls = zPos.realOffset(el.parentNode);
	scrolls[0] -= zk.innerX(); scrolls[1] -= zk.innerY();
	return [ofs[0] - scrolls[0], ofs[1] - scrolls[1]];
};
if (zk.safari) {
	//fix safari's bug
	zk._oldposofs = zPos.positionedOffset;
	zPos.positionedOffset = function (el) {
		if ($tag(el) === "TR" && el.cells.length)
			el = el.cells[0];
		return zk._oldposofs(el);
	};
}
if (zk.gecko || zk.safari) {
	zk._oldcumofs = zPos.cumulativeOffset;
	zPos.cumulativeOffset = function (el) {
		//fix safari's bug: TR has no offsetXxx
		if (zk.safari && $tag(el) === "TR" && el.cells.length)
			el = el.cells[0];

		//fix gecko and safari's bug: if not visible before, offset is wrong
		var ofs;
		if (!$visible(el) && !zk.offsetWidth(el)) {
			el.style.display = "";
			ofs = zk._oldcumofs(el);
			el.style.display = "none";
		} else {
			ofs = zk._oldcumofs(el);
		}
		return ofs;
	};
}

/** Center the specified element.
 * @param flags a combination of center, left, right, top and bottom.
 * If omitted, center is assigned.
 */
zk.center = function (el, flags) {
	var wdgap = zk.offsetWidth(el),
		hghgap = zk.offsetHeight(el);

	if ((!wdgap || !hghgap) && !$visible(el)) {
		el.style.left = el.style.top = "-10000px"; //avoid annoying effect
		el.style.display = "block"; //we need to calculate the size
		wdgap = zk.offsetWidth(el);
		hghgap = zk.offsetHeight(el),
		el.style.display = "none"; //avoid Firefox to display it too early
	}

	var left = zk.innerX(), top = zk.innerY();
	var x, y, skipx, skipy;

	wdgap = zk.innerWidth() - wdgap;
	if (!flags) x = left + wdgap / 2;
	else if (flags.indexOf("left") >= 0) x = left;
	else if (flags.indexOf("right") >= 0) x = left + wdgap - 1; //just in case
	else if (flags.indexOf("center") >= 0) x = left + wdgap / 2;
	else {
		x = 0; skipx = true;
	}

	hghgap = zk.innerHeight() - hghgap;
	if (!flags) y = top + hghgap / 2;
	else if (flags.indexOf("top") >= 0) y = top;
	else if (flags.indexOf("bottom") >= 0) y = top + hghgap - 1; //just in case
	else if (flags.indexOf("center") >= 0) y = top + hghgap / 2;
	else {
		y = 0; skipy = true;
	}

	if (x < left) x = left;
	if (y < top) y = top;

	var ofs = zk.toStyleOffset(el, x, y);

	if (!skipx) el.style.left = ofs[0] + "px";
	if (!skipy) el.style.top =  ofs[1] + "px";
};
/** Returns the width and height.
 * In additions, it fixes brwoser's bugs, so call it as soon as possible.
 */
zk.getDimension = function (el) {
	var wd = zk.offsetWidth(el), hgh;
	if (!$visible(el) && !wd) {
		var fixleft = el.style.left == "" || el.style.left == "auto";
		if (fixleft) el.style.left = "0";
		var fixtop = el.style.top == "" || el.style.top == "auto";
		if (fixtop) el.style.top = "0";
			//IE6/gecko: otherwise, cumulativeOffset is wrong

		el.style.display = "";
		wd = zk.offsetWidth(el);
		hgh = zk.offsetHeight(el);
		el.style.display = "none";

		if (fixleft) el.style.left = "";
		if (fixtop) el.style.top = "";
	} else {
		hgh = zk.offsetHeight(el);
	}
	return [wd, hgh];
};
/** zPos a component being releted to another. */
zk.position = function (el, ref, type) {
	var refofs = zk.getDimension(el);
	var wd = refofs[0], hgh = refofs[1];
	refofs = zk.revisedOffset(ref);

	var x, y;
	var scx = zk.innerX(), scy = zk.innerY(),
		scmaxx = scx + zk.innerWidth(), scmaxy = scy + zk.innerHeight();

	if (type == "end_before") { //el's upper-left = ref's upper-right
		x = refofs[0] + zk.offsetWidth(ref);
		y = refofs[1];

		if (zk.ie) {
			var diff = $int(Element.getStyle(ref, "margin-top"));
			if (!isNaN(diff)) y += diff;
			diff = $int(Element.getStyle(ref, "margin-right"));
			if (!isNaN(diff)) x += diff;
		}

		if (x + wd > scmaxx)
			x = refofs[0] - wd;
		if (y + hgh > scmaxy)
			y = scmaxy - hgh;
	} else { //after-start: el's upper-left = ref's lower-left
		x = refofs[0];
		y = refofs[1] + zk.offsetHeight(ref);

		if (zk.ie) {
			var diff = $int(Element.getStyle(ref, "margin-bottom"));
			if (!isNaN(diff)) y += diff;
			diff = $int(Element.getStyle(ref, "margin-left"));
			if (!isNaN(diff)) x += diff;
		}

		if (y + hgh > scmaxy)
			y = refofs[1] - hgh;
		if (x + wd > scmaxx)
			x = scmaxx - wd;
	}

	if (x < scx) x = scx;
	if (y < scy) y = scy;
	refofs = zk.toStyleOffset(el, x, y);
	el.style.left = refofs[0] + "px"; el.style.top = refofs[1] + "px";
};

/** Returns the maximal allowed height of the specified element.
 * In other words, it is the client height of the parent minus all sibling's.
 * @since 3.0.3
 */
zk.getVflexHeight = function (el) {
	var hgh = el.parentNode.clientHeight;
	if (zk.ie6Only) { //IE6's clientHeight is wrong
		var ref = el.parentNode;
		var h = ref.style.height;
		if (h && h.endsWith("px")) {
			h = zk.revisedSize(ref, $int(h), true);
			if (h && h < hgh) hgh = h;
		}
	}

	for (var p = el, q; q = p.previousSibling;) {
		if (q.offsetHeight && $visible(q)) hgh -= q.offsetHeight; //may undefined
		p = q;
	}
	for (var p = el, q; q = p.nextSibling;) {
		if (q.offsetHeight && $visible(q)) hgh -= q.offsetHeight; //may undefined
		p = q;
	}
	return hgh;
};

/** Returns the style's coordination in [integer, integer].
 * Note: it ignores the unit and assumes px (so pt or others will be wrong)
 */
zk.getStyleOffset = function (el) {
	return [$int(el.style.left), $int(el.style.top)];
};
/** Converts from absolute coordination to style's coordination.
 * It is only useful for table's cell.
 * We cannot use zk.toParentCoord, because
 * after calling zDraggable, offsetParent becomes BODY but
 * style.left/top is still relevant to original offsetParent
 */
zk.toStyleOffset = function (el, x, y) {
	var oldx = el.style.left, oldy = el.style.top;
	if (zk.opera || zk.air || zk.ie8) {
		//Opera:
		//1)we have to reset left/top. Or, the second call position wrong
		//test case: Tooltips and Popups
		//2)we cannot assing "", either
		//test case: menu
		el.style.left = el.style.top = "0";
	} else {
		//IE/gecko fix: otherwise, zk.toStyleOffset won't correct
		if (el.style.left == "" || el.style.left == "auto") el.style.left = "0";
		if (el.style.top == "" || el.style.top == "auto") el.style.top = "0";
	}

	var ofs1 = zPos.cumulativeOffset(el);
	var ofs2 = zk.getStyleOffset(el);
	ofs1 = [x - ofs1[0] + ofs2[0], y  - ofs1[1] + ofs2[1]];
	el.style.left = oldx;
	el.style.top = oldy;
	return ofs1;
};

/** Whether el1 and el2 are overlapped. */
zk.isOverlapped = function (el1, el2) {
	return zk.isOffsetOverlapped(
		zPos.cumulativeOffset(el1), [el1.offsetWidth, el1.offsetHeight],
		zPos.cumulativeOffset(el2), [el2.offsetWidth, el2.offsetHeight]);
};
/** Whether ofs1/dim1 is overlapped with ofs2/dim2. */
zk.isOffsetOverlapped = function (ofs1, dim1, ofs2, dim2) {
	var o1x1 = ofs1[0], o1x2 = dim1[0] + o1x1,
		o1y1 = ofs1[1], o1y2 = dim1[1] + o1y1;
	var o2x1 = ofs2[0], o2x2 = dim2[0] + o2x1,
		o2y1 = ofs2[1], o2y2 = dim2[1] + o2y1;
	return o2x1 <= o1x2 && o2x2 >= o1x1 && o2y1 <= o1y2 && o2y2 >= o1y1;
};

/** Whether an element is really visible.
 * @param {Boolean} strict if true, it also checks the visibility property.(since 3.0.4)
 */
zk.isRealVisible = function (e, strict) {
	if (!e) return false;
	do {
		if (!$visible(e, strict)) return false;
		//note: document is the top parent and has NO style
	} while (e = $parent(e)); //yes, assign
	return true;
};
zk.isVisible = $visible; //backward compatible

/** Focus the specified element and any of its child. */
zk.focusDown = function (el) {
	return zk._focusDown(el, ["INPUT", "SELECT", "BUTTON"], true)
		|| zk._focusDown(el, ["A"], false);
};
/** checkA whether to check the A tag specially (i.e., focus if one ancestor
 * has z.type).
 */
zk._focusDown = function (el, match, checkA) {
	if (!el) return false;
	if (el.focus) {
		var tn = $tag(el);
		if (match.contains(tn)) {
			zk.focus(el);
			return true;
		}
		if (checkA && tn == "A") {
			for (var n = el; (n = $parent(n))/*yes, assign*/;) {
				if (getZKAttr(n, "type")) {
					zk.focus(el);
					return true;
				}
			}
		}
	}
	for (el = el.firstChild; el; el = el.nextSibling) {
		if (zk._focusDown(el, match))
			return true;
	}
	return false;
};
/** Focus the element with the specified ID and do it timeout later. */
zk.asyncFocusDown = function (id, timeout) {
	++zk.inAsyncFocus;
	setTimeout("--zk.inAsyncFocus; if (!zk.focusDown($e('"+id+"'))) window.focus();",
		timeout > 0? timeout: 0);
};
/** Focus the element without looking down, and do it timeout later. */
zk.asyncFocus = function (id, timeout) {
	++zk.inAsyncFocus;
	setTimeout("--zk.inAsyncFocus; zk.focus($e('"+id+"'));",
		timeout > 0? timeout: 0);
		//Workaround for an IE bug: we have to set focus twice since
		//the first one might fail (even we prolong the timeout to 1 sec)
};
zk.inAsyncFocus = 0;

/** Focus to the specified component w/o throwing exception. */
zk.focus = function (cmp) {
	if (cmp && cmp.focus)
		try {
			cmp.focus();
		} catch (e) {
			setTimeout(function() {
				try {
					cmp.focus();
				} catch (e) {
					setTimeout(function() {try {cmp.focus();} catch (e) {}}, 100);
				}
			}, 0);
		}
		//IE throws exception if failed to focus in some cases
};

/** Select the text of the element, and do it timeout later. */
zk.asyncSelect = function (id, timeout) {
	++zk.inAsyncSelect;
	setTimeout("--zk.inAsyncSelect; zk.select($e('"+id+"'));",
		timeout > 0? timeout: 0);
};
zk.inAsyncSelect = 0;

/** Select to the specified component w/o throwing exception. */
zk.select = function (cmp) {
	if (cmp && cmp.select)
		try {
			cmp.select();
		} catch (e) {
			setTimeout(function() {
				try {cmp.select();} catch (e) {}
			}, 0);
		}
		//IE throws exception when focus() in some cases
};

/** Returns the selection range of the specified control.
 * Note: if the function occurs some error, it always return [0, 0];
 */
zk.getSelectionRange = function(inp) {
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
};
zk.setSelectionRange = function (inp, start, end) {
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
};

/** Inserts a node after another.
 */
zk.insertAfter = function (el, ref) {
	var sib = ref.nextSibling;
	if (sib) ref.parentNode.insertBefore(el, sib);
	else ref.parentNode.appendChild(el);
};
/** Inserts a node before another.
 */
zk.insertBefore = function (el, ref) {
	ref.parentNode.insertBefore(el, ref);
};
/** Inserts an unparsed HTML immediately before the specified element.
 * @param el the sibling before which to insert
 */
zk.insertHTMLBefore = function (el, html) {
	if (zk.ie || zk.opera) {
		switch ($tag(el)) { //exclude TABLE
		case "TD": case "TH": case "TR": case "CAPTION": case "COLGROUP":
		case "TBODY": case "THEAD": case "TFOOT":
			var ns = zk._tblCreateElements(html);
			var p = el.parentNode;
			for (var j = 0, nl = ns.length; j < nl; ++j)
				p.insertBefore(ns[j], el);
			return;
		}
	}
	el.insertAdjacentHTML('beforeBegin', html);
};
/** Inserts an unparsed HTML immediately before the ending element.
 */
zk.insertHTMLBeforeEnd = function (el, html) {
	if (zk.ie || zk.opera) {
		var tn = $tag(el);
		switch (tn) {
		case "TABLE": case "TR":
		case "TBODY": case "THEAD": case "TFOOT": case "COLGROUP":
		/*case "TH": case "TD": case "CAPTION":*/ //no need to handle them
			var ns = zk._tblCreateElements(html);
			if (tn == "TABLE" && ns.length && $tag(ns[0]) == "TR") {
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
};
/** Inserts an unparsed HTML immediately after the specified element.
 * @param el the sibling after which to insert
 */
zk.insertHTMLAfter = function (el, html) {
	if (zk.ie || zk.opera) {
		switch ($tag(el)) { //exclude TABLE
		case "TD": case "TH": case "TR": case "CAPTION":
		case "TBODY": case "THEAD": case "TFOOT":
		case "COLGROUP": case "COL":
			var ns = zk._tblCreateElements(html);
			var sib = el.nextSibling;
			var p = el.parentNode;
			for (var j = 0, nl = ns.length; j < nl; ++j)
				if (sib != null) p.insertBefore(ns[j], sib);
				else p.appendChild(ns[j]);
			return;
		}
	}
	el.insertAdjacentHTML('afterEnd', html);
};

/** Sets the inner HTML.
 */
zk.setInnerHTML = function (el, html) {
	if (zk.ie || zk.opera) {
		var tn = $tag(el);
		if (tn == "TR" || tn == "TABLE" || tn == "TBODY" || tn == "THEAD"
		|| tn == "TFOOT" || tn == "COLGROUP" || tn == "COL") { //ignore TD/TH/CAPTION
			var ns = zk._tblCreateElements(html);
			if (tn == "TABLE" && ns.length && $tag(ns[0]) == "TR") {
				var bd = el.tBodies;
				if (!bd || !bd.length) {
					bd = document.createElement("TBODY");
					el.appendChild(bd);
					el = bd;
				} else {
					el = bd[0];
					while (el.nextSibling)
						el.parentNode.removeChild(el.nextSibling);
				}
			}
			while (el.firstChild)
				el.removeChild(el.firstChild);
			for (var j = 0, nl = ns.length; j < nl; ++j)
				el.appendChild(ns[j]);
			return;
		}
	}
	el.innerHTML = html;
};
/** Sets the outer HTML.
 */
zk.setOuterHTML = function (el, html) {
	//NOTE: Safari doesn't support __defineSetter__
	var p = el.parentNode;
	if (zk.ie || zk.opera) {
		var tn = $tag(el);
		if (tn == "TD" || tn == "TH" || tn == "TABLE" || tn == "TR"
		|| tn == "CAPTION" || tn == "TBODY" || tn == "THEAD"
		|| tn == "TFOOT" || tn == "COLGROUP" || tn == "COL") {
			var ns = zk._tblCreateElements(html);
			var sib = el.nextSibling;
			p.removeChild(el);
			for (var j = 0, nl = ns.length; j < nl; ++j)
				if (sib) p.insertBefore(ns[j], sib);
				else p.appendChild(ns[j]);
		} else {
			el.outerHTML = html;
		}
	} else {
		var r = el.ownerDocument.createRange();
		r.setStartBefore(el);
		var df = r.createContextualFragment(html);
		p.replaceChild(df, el);
	}

	for (p = p.firstChild; p; p = p.nextSibling) {
		if ($tag(p)) { //skip Text
			if (!$visible(p)) zk._hideExtr(p);
			else zk._showExtr(p);
			break;
		}
	}
};

/** Returns the next sibling with the specified tag name, or null if not found.
 */
zk.nextSibling = function (el, tagName) {
	while (el && (el = el.nextSibling) != null && $tag(el) != tagName)
		;
	return el;
};
/** Returns the next sibling with the specified tag name, or null if not found.
 */
zk.previousSibling = function (el, tagName) {
	while (el && (el = el.previousSibling) != null && $tag(el) != tagName)
		;
	return el;
};
/** Returns the parent with the specified tag name, or null if not found.
 * <p>Unlike parentByTag, the search excludes el
 */
zk.parentNode = function (el, tagName) {
	while (el && (el = $parent(el))/*yes,assign*/ && $tag(el) != tagName)
		;
	return el;
};
/**
 * Returns a collection of child nodes of the given element. (Only nodeType = 1)
 * @param el an element.
 * @param fn a filter function to test for each element, if any. Returns true, the child node
 * is legal.
 * @since 3.5.0
 */
zk.childNodes = function (el, fn) {
	var nodes = [];
	for (var n = el.firstChild; n; n = n.nextSibling)
		if (n.nodeType == 1 && (!(typeof fn == "function") || fn(n))) nodes.push(n);
	return nodes;
};
/** Returns the first child of the specified node. */
zk.firstChild = function (el, tagName, descendant) {
	for (var n = el.firstChild; n; n = n.nextSibling)
		if ($tag(n) == tagName)
			return n;

	if (descendant) {
		for (var n = el.firstChild; n; n = n.nextSibling) {
			var chd = zk.firstChild(n, tagName, descendant);
			if (chd)
				return chd;
		}
	}
	return null;
};
/** Returns the last child of the specified node.
 * @since 3.5.0
 */
zk.lastChild = function (el, tagName, descendant) {
	for (var n = el.lastChild; n; n = n.previousSibling)
		if ($tag(n) == tagName)
			return n;

	if (descendant) {
		for (var n = el.lastChild; n; n = n.previousSibling) {
			var chd = zk.lastChild(n, tagName, descendant);
			if (chd)
				return chd;
		}
	}
	return null;
};
/** Returns whether a node is an ancestor of another (including itself).
 *
 * @param ckuuid whether to check UUID is the same (i.e., whether
 * they are different part of the same component).
 */
zk.isAncestor = function (p, c, ckuuid) {
	if (ckuuid && $uuid(p) == $uuid(c))
		return true;

	p = $e(p);
	c = $e(c);
	for (; c; c = $parent(c))
		if (p == c)
			return true;
	return false;
};
/** Returns whether a node is an ancestor of one of an array of elements.
 *
 * @param ckuuid whether to check UUID is the same (i.e., whether
 * they are different part of the same component).
 * @param ckowner whether to check p's owner (z.owner) (3.5.0)
 */
zk.isAncestorX = function (p, ary, ckuuid, ckowner) {
	for (var j = 0, al = ary.length; j < al; ++j)
		if (zk.isAncestor(p, ary[j], ckuuid))
			return true;

	if (ckowner) {
		var owner = $e(getZKAttr(p, "owner"));
		return owner && zk.isAncestorX(owner, ary, ckuuid, ckowner);
	}
	return false;
};
/** Returns whether any of an array of elments is an ancestor of another.
 * @since 3.0.2
 * @param ckowner whether to check c's owner (z.owner) (3.5.0)
 */
zk.isAncestorX1 = function (ary, c, ckuuid, ckowner) {
	for (var j = 0, al = ary.length; j < al; ++j)
		if (zk.isAncestor(ary[j], c, ckuuid))
			return true;

	if (ckowner) {
		var owner = $e(getZKAttr(c, "owner"));
		return owner && zk.isAncestorX1(ary, owner, ckuuid, ckowner);
	}
	return false;
};

/** Returns the enclosing tag for the specified HTML codes.
 */
zk.tagOfHtml = function (html) {
	if (!html) return "";

	var j = html.indexOf('>'), k = html.lastIndexOf('<');
	if (j < 0 || k < 0) {
		zk.error("Unknown tag: "+html);
		return "";
	}
	var head = html.substring(0, j);
	j = head.indexOf('<') + 1;
	j = head.skipWhitespaces(j);
	k = head.nextWhitespace(j);
	return head.substring(j, k).toUpperCase();
};

/** Appends an unparsed HTML immediately after the last child.
 * @param el the parent
 */
//zk.appendHTMLChild = function (el, html) {
//	el.insertAdjacentHTML('beforeEnd', html);
//};

///// fix inserting/updating TABLE/TR/TD ////
if (zk.ie || zk.opera) {
//IE don't support TABLE/TR/TD well.

//20061109: Tom M. Yeh:
//Browser: Opera:
//Issue: When append TD with insertAdjacentHTML, it creates extra sibling,
//TextNode.
//Test: Live data of listbox in ZkDEMO
	/** Creates TABLE/TD/TH/TR... with the specified HTML text.
	 *
	 * Thanks the contribution of Ilian Ianev, Barclays Global Investors,
	 * for the following fix.
	 */
	zk._tblCreateElements = function (html) {
		var level;
		html = html.trim(); //If not trim, Opera will create TextNode!
		var tag = zk.tagOfHtml(html)
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
			var nt = $tag(n);
			if (nt == tag || nt != "TBODY")
				ns.push(n);
			el.removeChild(n);
		}
		return ns;
	};
}

/** Returns the element's value (by catenate all CDATA and text).
 */
zk.getElementValue = function (el) {
	var txt = ""
	for (el = el.firstChild; el; el = el.nextSibling)
		if (el.data) txt += el.data;
	return txt;
};

/** Extends javascript for Non-IE
 */
if (!zk.ie && !HTMLElement.prototype.insertAdjacentHTML) {
	//insertAdjacentHTML
	HTMLElement.prototype.insertAdjacentHTML = function (sWhere, sHTML) {
		var df;   // : DocumentFragment
		var r = this.ownerDocument.createRange();

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
			zk.insertAfter(df, this);
			break;
		}
	};
};

//-- Image utilities --//
/** Rename by changing the type (after -).
 * It works with url(/x/y.gif), too
 *url: the original URL
 *type: the type to rename to: open or closed
 *todo: support _zh_TW
*/
zk.renType = function (url, type) {
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
};
/** Rename between / and .
 */
zk.rename = function (url, name) {
	var j = url.lastIndexOf(';');
	var suffix;
	if (j >= 0) {
		suffix = url.substring(j);
		url = url.substring(0, j);
	} else
		suffix = "";

	j = url.lastIndexOf('.');
	var k = url.lastIndexOf('/'),
		ext = j <= k ? "": url.substring(j);
	return url.substring(0, k + 1) + name + ext + suffix;
};

//-- special routines --//
if (!zk._actg1) {
	zk._actg1 = ["IFRAME","EMBED","APPLET"];
		// Due to using zk.makeStackup(), we don't handle the "SELECT" tag for IE6
		// in zk.hideCovered() function.
	zk._actg2 = ["A","BUTTON","TEXTAREA","INPUT"];
	zk._actg3 = ["IFRAME","EMBED"]; // zk.disableAll use only.
	if (zk.ie6Only) { //ie7 solves the z-order issue of SELECT
		zk._actg3.unshift("SELECT"); //change visibility is required
	} else
		zk._actg2.unshift("SELECT");
	zk.coveredTagnames = zk._actg1; //backward compatible 2.4 or before

	zk._disTags = []; //A list of {element: xx, what: xx}
	zk._hidCvred = []; //A list of {element: xx, visibility: xx}
}

/** Disables all active tags. */
zk.disableAll = function (parent) {
	for (var j = 0, al1 = zk._actg3.length; j < al1; j++)
		zk._dsball(parent, document.getElementsByTagName(zk._actg3[j]), true);

	if (zk.dbModal) //not disable-behind-modal
		for (var j = 0, al2 = zk._actg2.length; j < al2; j++)
			zk._dsball(parent, document.getElementsByTagName(zk._actg2[j]));
};
zk._dsball = function (parent, els, visibility) {
	l_els:
	for (var k = 0, elen = els.length; k < elen; k++) {
		var el = els[k];
		if (zk.isAncestor(parent, el))
			continue;
		for(var m = 0, dl = zk._disTags.length; m < dl; ++m) {
			var info = zk._disTags[m];
			if (info.element == el)
				continue l_els;
		}

		var tn = $tag(el), what;
		if (visibility) { //_actg1
			if (!zk.shallHideDisabled(el)) continue;

			what = el.style.visibility;
			el.style.visibility = "hidden";
		} else if (zk.gecko && tn == "A") {
//Firefox doesn't support the disable of A
			what = "h:" + zkau.getStamp(el, "tabIndex") + ":" +
				(el.tabIndex ? el.tabIndex: 0); //just in case (if null)
			el.tabIndex = -1;
		} else {
			what = "d:" + zkau.getStamp(el, "disabled") + ":" + el.disabled;
			el.disabled = true;
		}
		zk._disTags.push({element: el, what: what});
	}
};
/** Whether an element belonging to zk._disTags shall be handled.
 * @since 3.0.4
 */
zk.shallHideDisabled = function (el) {
	var tn = $tag(el);
	return (tn != "IFRAME" && tn != "EMBED" && tn != "APPLET")
		|| (getZKAttr(el, "autohide") == "true" && $visible(el, true));
};
/** Restores tags being disabled by previous disableAll. If el is not null,
 * only el's children are enabled
 */
zk.restoreDisabled = function (n) {
	var skipped = [];
	for (var bug1498895 = zk.ie, dlen = zk._disTags.length; dlen; --dlen) {
		var info = zk._disTags.shift();
		var el = info.element;
		if (el && el.tagName) { //still exists
			if (n && !zk.isAncestor(n, el)) { //not processed yet
				skipped.push(info);
				continue;
			}
			var what = info.what;
			if (what.startsWith("d:")) {
				var j = what.indexOf(':', 2);
				if (what.substring(2, j) == zkau.getStamp(el, "disabled"))
					el.disabled = what.substring(j + 1) == "true";
			} else if (what.startsWith("h:")) {
				var j = what.indexOf(':', 2);
				if (what.substring(2, j) == zkau.getStamp(el, "href"))
					el.tabIndex = what.substring(j + 1);
			} else
				el.style.visibility = what;

			//Workaround IE: Bug 1498895
			/**if (bug1498895) { disable by the bug #1884111,
			 // because we had the new concept that restores the previous focus,
			 // we don't need to find which input element needs to be focused.
				var tn = $tag(el);
				if ((tn == "INPUT" && (el.type == "text" || el.type == "password"))
				||  tn == "TEXTAREA"){
				//focus only visible (to prevent scroll)
					try {
						var ofs = zPos.cumulativeOffset(el);
						if (ofs[0] >= zk.innerX() && ofs[1] >= zk.innerY()
						&& (ofs[0]+20) <= (zk.innerX()+zk.innerWidth())
						&& (ofs[1]+20) <= (zk.innerY()+zk.innerHeight())) {
							el.focus();
							bug1498895 = false;
						}
					} catch (e) {
					}
				}
			}*/
		}
	}
	zk._disTags = skipped;
};
/** Hide select, iframe and applet if it is covered by any of ary
 * and not belonging to any of ary.
 * If ary is empty, it restores what have been hidden by last invocation
 * to this method.
 */
zk.hideCovered = function (ary) {
	if (!ary || ary.length == 0) {
		var hl = zk._hidCvred.length;
		while (hl) {
			var info = zk._hidCvred.shift();
			if (info.element.style)
				info.element.style.visibility = info.visibility;
			--hl;
		}
		return;
	}

	var cts = zk._actg1;
	for (var j = 0, clen = cts.length; j < clen; ++j) {
		var els = document.getElementsByTagName(cts[j]);
		loop_els:
		for (var k = 0, elen = els.length; k < elen; k++) {
			var el = els[k];
			if (!zk.isRealVisible(el)) continue;

			for (var m = 0, al = ary.length; m < al; ++m) {
				if (zk.isAncestor(ary[m], el))
					continue loop_els;
			}

			var overlapped = false;
			if (zk.shallHideDisabled(el))
				for (var m = 0, al = ary.length; m < al; ++m) {
					if (zk.isOverlapped(ary[m], el)) {
						overlapped = true;
						break;
					}
				}

			if (overlapped) {
				for (var m = 0, hl = zk._hidCvred.length; m < hl; ++m) {
					if (el == zk._hidCvred[m].element)
						continue loop_els;
				}
				zk._hidCvred
					.push({element: el, visibility: el.style.visibility});
				el.style.visibility = "hidden";
			} else {
				for (var m = 0, hl = zk._hidCvred.length; m < hl; ++m) {
					if (el == zk._hidCvred[m].element) {
						el.style.visibility = zk._hidCvred[m].visibility;
						zk._hidCvred.splice(m, 1);
						break;
					}
				}
			}
		}
	}
};

/** Retrieve a member by use of a.b.c */
zk.resolve = function (fullnm) {
	for (var j = 0, v = window;;) {
		var k = fullnm.indexOf('.', j);
		var nm = k >= 0 ? fullnm.substring(j, k): fullnm.substring(j);
		v = v[nm];
		if (k < 0 || !v) return v;
		j = k + 1;
	}
};

/** Sets the style. */
zk.setStyle = function (el, style) {
	for (var j = 0, k = 0; k >= 0; j = k + 1) {
		k = style.indexOf(';', j);
		var s = k >= 0 ? style.substring(j, k): style.substring(j);
		var l = s.indexOf(':');
		var nm, val;
		if (l < 0) {
			nm = s.trim(); val = "";
		} else {
			nm = s.substring(0, l).trim();
			val = s.substring(l + 1).trim();
		}

		if (nm) el.style[nm.camelize()] = val;
	}
};

/** Returns the text-relevant style (same as HTMLs.getTextRelevantStyle).
 * @param incwd whether to include width
 * @param inchgh whether to include height
 */
zk.getTextStyle = function (style, incwd, inchgh) {
	var ts = "";
	for (var j = 0, k = 0; k >= 0; j = k + 1) {
		k = style.indexOf(';', j);
		var s = k >= 0 ? style.substring(j, k): style.substring(j);
		var l = s.indexOf(':');
		var nm = l < 0 ? s.trim(): s.substring(0, l).trim();

		if (nm.startsWith("font")  || nm.startsWith("text")
		|| zk._txtstyles.contains(nm)
		|| (incwd && nm == "width") || (inchgh && nm == "height"))
			ts += s + ';';
	}
	return ts;
};
if (!zk._txtstyles)
	zk._txtstyles = ["color", "background-color", "background",
		"white-space"];

/** Backup a style of the specified name.
 * The second call to backupStyle is ignored until zk.restoreStyle is called.
 * Usually used with onmouseover.
 */
zk.backupStyle = function (el, nm) {
	var bknm = "zk_bk" + nm;
	if (!el.getAttribute(bknm))
		el.setAttribute(bknm, el.style[nm] || "_zk_none_");
};
/** Restore the style of the specified name.
 * Usually used with onover.
 */
zk.restoreStyle = function (el, nm) {
	if (el && el.getAttribute && el.style) { //el might be removed!
		var bknm = "zk_bk" + nm;
		var val = el.getAttribute(bknm);
		if (val) {
			el.removeAttribute(bknm);
			el.style[nm] = val == "_zk_none_" ? "": val;
		}
	}
};

zk.scrollIntoView = function (outer, inner) {
	if (inner && inner.scrollIntoView) inner.scrollIntoView();
};

/** Go to the specified uri.
 * @param overwrite whether to overwrite the history
 * @param target the target frame (ignored if overwrite is true
 */
zk.go = function (url, overwrite, target) {
	if (!url) {
		location.reload();
	} else if (overwrite) {
		location.replace(url);
	} else if (target) {
		//we have to process query string because browser won't do it
		//even if we use insertHTMLBeforeEnd("<form...")
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
		if (url && !zk.isNewWindow(url, target))
			zk.progress();
		frm.submit();
	} else {
		location.href = url;
	}
};
/** Tests whether a new window will be opened.
 * More precisely, whether the current document won't be changed.
 */
zk.isNewWindow = function (url, target) {
	return url.startsWith("mailto:") || url.startsWith("javascript:")
		|| (target && target != "_self");
};

/* Convert query string (a=b&c=d) to hiddens of the specified form.
 */
zk.queryToHiddens = function (frm, qs) {
	for(var j = 0;;) {
		var k = qs.indexOf('=', j);
		var l = qs.indexOf('&', j);

		var nm, val;
		if (k < 0 || (k > l && l >= 0)) { //no value part
			nm = l >= 0 ? qs.substring(j, l): qs.substring(j);
			val = "";
		} else {
			nm = qs.substring(j, k);
			val = l >= 0 ? qs.substring(k + 1, l): qs.substring(k + 1);
		}
		zk.newHidden(nm, val, frm);

		if (l < 0) return; //done
		j = l + 1;
	}
};

/** Creates a frame if it is not created yet. */
zk.newFrame = function (name, src, style) {
	var frm = $e(name);
	if (frm) return frm;

	if (!src) src = zk.getUpdateURI('/web/img/spacer.gif');
		//IE with HTTPS: we must specify the src

	var html = '<iframe id="'+name+'" name="'+name+'" src="'+src+'"';
	if (style) html += ' style="'+style+'"';
	html += '></iframe>';
	zk.insertHTMLBeforeEnd(document.body, html);
	return $e(name);
};

/** Returns the nearest form, or null if not available.
 * @since 3.0.0
 */
zk.formOf = function (n) {
	for (; n; n = n.parentNode)
		if ($tag(n) == "FORM")
			return n;
};

/** Creates a hidden field.
 * @param parent to assign the hidden to. Ignored if null.
 * @since 3.0.0
 */
zk.newHidden = function (nm, val, parent) {
	var inp = document.createElement("INPUT");
	inp.type = "hidden";
	inp.name = nm;
	inp.value = val;
	if (parent) parent.appendChild(inp);
	return inp;
};

/** Returns the number of columns (considering colSpan)
 */
zk.ncols = function (cells) {
	var cnt = 0;
	if (cells) {
		for (var j = 0, cl = cells.length; j < cl; ++j) {
			var span = cells[j].colSpan;
			if (span >= 1) cnt += span;
			else ++cnt;
		}
	}
	return cnt;
};
/**
 * Retrieves the index of the object in the cells collection of a row.
 * Note: The function fixed the problem of IE that the cell.cellIndex returns a wrong index
 * if there is a hidden cell in the table. So, the behavior is difference among others.
 * @param {Object} cell
 * @since 3.0.1
 */
zk.cellIndex = function (cell) {
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
};


/** Returns the number of columns (considering colSpan)
 */
zk.ncols = function (cells) {
	var cnt = 0;
	if (cells) {
		for (var j = 0; j < cells.length; ++j) {
			var span = cells[j].colSpan;
			if (span >= 1) cnt += span;
			else ++cnt;
		}
	}
	return cnt;
};

/** Copies the width of each cell from one row to another.
 * It handles colspan of srcrows, but not dst's colspan, nor rowspan
 *
 * @param srcrows all rows from the source table. Don't pass just one row
 * because a row might not have all cells.
 */
zk.cpCellWidth = function (dst, srcrows, mate) {
	if (dst == null || srcrows == null || !srcrows.length
	|| !dst.cells || !dst.cells.length)
		return;

	var ncols = dst.cells.length; //TODO: handle colspan for dst: ncols = zk.ncols(dst.cells);
	var src, maxnc = 0, loadIdx = getZKAttr(mate.element, "lastLoadIdx");
	for (var j = 0, len = $int(loadIdx) || srcrows.length; j < len; ++j) {
		var row = srcrows[j];
		if (!zk.isVisible(row) || getZKAttr(row, "loaded") == "false") continue;
		var cells = row.cells;
		var nc = zk.ncols(cells);
		var valid = cells.length == nc && $visible(row);
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
			d.style.width = zk.revisedSize(s, s.offsetWidth);
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

	if (zk.opera && getZKAttr(mate.element, "fixed") != "true")
		dst.parentNode.parentNode.style.width = sum + "px";

	if (fakeRow)
		src.parentNode.removeChild(src);
};
//Number//
/** digits specifies at least the number of digits must be ouput. */
zk.formatFixed = function (val, digits) {
	var s = "" + val;
	for (var j = digits - s.length; --j >= 0;)
		s = "0" + s;
	return s;
};

//Date//
/** Parses a string into a Date object.
 * @param strict whether not to lenient
 */
zk.parseDate = function (txt, fmt, strict) {
	if (!fmt) fmt = "yyyy/MM/dd";
	var val = new Date();
	var y = val.getFullYear(),
		m = val.getMonth(),
		d = val.getDate(),
		hr = val.getHours(),
		min = val.getMinutes();

	var	ts = [], mindex = fmt.indexOf("MMM"), ary = [];
	for (var i = 0, j = txt.length; i < j; i++) {
		var c = txt.charAt(i);
		if (c.match(/\d/)) {
			ary.push(c);
		} else if (mindex > -1 && mindex <= i) {
			if (c.match(/\w/)) {
				ary.push(c);
			} else {
				if (c.charCodeAt() < 128) {
					if (ary.length) {
						ts.push(ary.join(""));
						ary = [];
					}
				} else
					ary.push(c);
			}
		} else if (ary.length) {
			ts.push(ary.join(""));
			ary = [];
		}
	}
	if (ary.length) ts.push(ary.join(""));
	for (var i = 0, j = 0, fl = fmt.length; j < fl; ++j) {
		var cc = fmt.charAt(j);
		if ((cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')) {
			var len = 1;
			for (var k = j; ++k < fl; ++len)
				if (fmt.charAt(k) != cc)
					break;

			var nosep; //no separator
			if (k < fl) {
				var c2 = fmt.charAt(k);
				nosep = c2 == 'y' || c2 == 'M' || c2 == 'd' || c2 == 'E';
			}

			var token = ts[i++] != null ? ts[i++] : "";//Bug 2560497 : if no seperator, token must be assigned.
			switch (cc) {
			case 'y':
				if (nosep) {
					if (len <= 3) len = 2;
					if (token && token.length > len) {
						ts[--i] = token.substring(len);
						token = token.substring(0, len);
					}
				}
				y = $int(token);
				if (isNaN(y)) return null; //failed
				if (y < 100) y += y > 29 ? 1900 : 2000;
				break;
			case 'M':
				var mon = txt.substring(j);
				for (var index = zk.SMON.length; --index >= 0;) {
					if (mon.toLowerCase().startsWith(zk.SMON[index].toLowerCase())) {
						token = zk.SMON[index];
						m = index;
						break;
					}
				}
				if (len == 3 && token) {
					break; // nothing to do.
				}else if (len <= 2) {
					if (nosep && token.length > 2) {
						ts[--i] = token.substring(2);
						token = token.substring(0, 2);
					}
					m = $int(token) - 1;
					if (isNaN(m)) return null; //failed
				} else {
					for (var l = 0;; ++l) {
						if (l == 12) return null; //failed
						if (len == 3) {
							if (zk.SMON[l] == token) {
								m = l;
								break;
							}
						} else {
							if (token && zk.FMON[l].startsWith(token)) {
								m = l;
								break;
							}
						}
					}
				}
				break;
			case 'd':
				if (nosep) {
					if (len < 2) len = 2;
					if (token && token.length > len) {
						ts[--i] = token.substring(len);
						token = token.substring(0, len);
					}
				}
				d = $int(token);
				if (isNaN(d)) return null; //failed
				break;
			case 'H':
			case 'h':
			case 'K':
			case 'k':
				if (nosep) {
					if (len < 2) len = 2;
					if (token.length > len) {
						ts[--i] = token.substring(len);
						token = token.substring(0, len);
					}
				}
				hr = $int(token);
				if (isNaN(hr)) return null; //failed
				break;
			case 'm':
				if (nosep) {
					if (len < 2) len = 2;
					if (token.length > len) {
						ts[--i] = token.substring(len);
						token = token.substring(0, len);
					}
				}
				min = $int(token);
				if (isNaN(min)) return null; //failed
				break;
			//default: ignored
			}
			j = k - 1;
		}
	}

	var dt = new Date(y, m, d, hr, min);
	if (strict) {
		if (dt.getFullYear() != y || dt.getMonth() != m || dt.getDate() != d ||
			dt.getHours() != hr || dt.getMinutes() != min)
			return null; //failed

		txt = txt.trim();
		txt = zk._ckDate(zk.SDOW, txt);
		txt = zk._ckDate(zk.S2DOW, txt);
		txt = zk._ckDate(zk.FDOW, txt);
		txt = zk._ckDate(zk.SMON, txt);
		txt = zk._ckDate(zk.S2MON, txt);
		txt = zk._ckDate(zk.FMON, txt);
		txt = zk._ckDate(zk.APM, txt);
		for (var j = txt.length; --j >= 0;) {
			var cc = txt.charAt(j);
			if ((cc > '9' || cc < '0') && fmt.indexOf(cc) < 0)
				return null; //failed
		}
	}
	return dt;
};
zk._ckDate = function (ary, txt) {
	if (txt.length)
		for (var j = ary.length; --j >= 0;) {
			var k = txt.indexOf(ary[j]);
			if (k >= 0)
				txt = txt.substring(0, k) + txt.substring(k + ary[j].length);
		}
	return txt;
};

/** Generates a formated string for the specified Date object. */
zk.formatDate = function (val, fmt) {
	if (!fmt) fmt = "yyyy/MM/dd";

	var txt = "";
	for (var j = 0, fl = fmt.length; j < fl; ++j) {
		var cc = fmt.charAt(j);
		if ((cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')) {
			var len = 1;
			for (var k = j; ++k < fl; ++len)
				if (fmt.charAt(k) != cc)
					break;

			switch (cc) {
			case 'y':
				if (len <= 3) txt += zk.formatFixed(val.getFullYear() % 100, 2);
				else txt += zk.formatFixed(val.getFullYear(), len);
				break;
			case 'M':
				if (len <= 2) txt += zk.formatFixed(val.getMonth()+1, len);
				else if (len == 3) txt += zk.SMON[val.getMonth()];
				else txt += zk.FMON[val.getMonth()];
				break;
			case 'd':
				txt += zk.formatFixed(zk.dayInMonth(val), len);
				break;
			case 'E':
				if (len <= 3) txt += zk.SDOW[val.getDay()];
				else txt += zk.FDOW[val.getDay()];
				break;
			case 'D':
				txt += zk.dayInYear(val);
				break;
			case 'w':
				txt += zk.weekInYear(val);
				break;
			case 'W':
				txt += zk.weekInMonth(val);
				break;
			case 'G':
				txt += "AD";
				break;
			case 'F':
				txt += zk.dayOfWeekInMonth(val);
				break;
			case 'H':
				if (len <= 2) txt += zk.formatFixed(val.getHours(), len);
				break;
			case 'k':
				if (len <= 2) txt += zk.formatFixed(val.getHours() == 0 ? "24" : val.getHours(), len);
				break;
			case 'K':
				if (len <= 2) txt += zk.formatFixed(val.getHours() > 11 ? val.getHours() - 12 : val.getHours(), len);
				break;
			case 'h':
				if (len <= 2) txt += zk.formatFixed(val.getHours() > 11 ? val.getHours() - 12 : val.getHours() == 0 ? "12" : val.getHours(), len);
				break;
			case 'm':
				if (len <= 2) txt += zk.formatFixed(val.getMinutes(), len);
				break;
			case 'Z':
				txt += -(val.getTimezoneOffset()/60);
				break;
			case 'a':
				txt += val.getHours() > 11 ? "PM" : "AM";
				break;
			default:
				txt += '1';
				//fake; SimpleDateFormat.parse might ignore it
				//However, it must be an error if we don't generate a digit
			}
			j = k - 1;
		} else {
			txt += cc;
		}
	}
	return txt;
};
/** Converts milli-second to day. */
zk.ms2day = function (t) {
	return Math.round(t / 86400000);
};
/** Day in year (starting at 1). */
zk.dayInYear = function (d, ref) {
	if (!ref) ref = new Date(d.getFullYear(), 0, 1);
	return 1 + zk.ms2day(d - ref);
};
/** Day in month (starting at 1). */
zk.dayInMonth = function (d) {
	return d.getDate();
};
/** Week in year (starting at 1). */
zk.weekInYear = function (d, ref) {
	if (!ref) ref = new Date(d.getFullYear(), 0, 1);
	var wday = ref.getDay();
	if (wday == 7) wday = 0;
	return 1 + Math.floor((zk.ms2day(d - ref) + wday) / 7);
};
/** Week in month (starting at 1). */
zk.weekInMonth = function (d) {
	return zk.weekInYear(d, new Date(d.getFullYear(), d.getMonth(), 1));
};
/** Day of week in month. */
zk.dayOfWeekInMonth = function (d) {
	return 1 + Math.floor(zk.ms2day(d - new Date(d.getFullYear(), d.getMonth(), 1)) / 7);
};

/** Returns an integer of the attribute of the specified element. */
zk.getIntAttr = function (el, nm) {
	return $int(el.getAttribute(nm));
};

//selection//
zk.clearSelection = function () {
	try{
		if (window["getSelection"]) {
			if (zk.safari) window.getSelection().collapse();
			else window.getSelection().removeAllRanges();
		} else if (document.selection) {
			if (document.selection.empty) document.selection.empty();
			else if(document.selection.clear) document.selection.clear();
		}
		return true;
	} catch (e){
		return false;
	}
};
/** Disable whether the specified element is selectable. */
zk.disableSelection = function (el) {
	el = $e(el);
	if (el)
		if (zk.gecko)
			el.style.MozUserSelect = "none";
		else if (zk.safari)
			el.style.KhtmlUserSelect = "none";
		else if (zk.ie)
			el.onselectstart = function (evt) {
				if (!evt) evt = window.event;
				var n = Event.element(evt), tag = $tag(n);
				return tag == "TEXTAREA" || tag == "INPUT" && (n.type == "text" || n.type == "password");
			};
};
/** Enables whether the specified element is selectable. */
zk.enableSelection = function (el) {
	el = $e(el);
	if (el)
		if (zk.gecko)
			el.style.MozUserSelect = "";
		else if (zk.safari)
			el.style.KhtmlUserSelect = "";
		else if (zk.ie)
			el.onselectstart = null;
};

/*Float: used to be added to zkau.floats
 * Derives must provide an implementation of _close(el).
 */
zk.Float = zClass.create();
zk.Float.prototype = {
	initialize: function () {
	},
	/** returns true if no float at all. */
	empty: function () {
		return !this._ftid;
	},
	/** Closes this float if the specified ID is matched.
	 * @return whether it is closed
	 */
	close: function (id) {
		if (this._ftid == id)
			this.closeFloats();
	},
	/** Closes (hides) all floats unless it is an ancestor of any of arguments.
	 * @param arguments a list of components that shall be closed
	 */
	closeFloats: function () {
		return this._closeFloats(false, zkau._shallCloseBut, arguments);
	},
	/** Closes all floats when a component is getting the focus.
	 * @param arguments a list of components that shall be closed
	 */
	closeFloatsOnFocus: function () {
		return this._closeFloats(true, zkau._shallCloseBut, arguments);
	},
	/** Closes all floats if it belongs to any of arguments.
	 * @since 3.0.2
	 */
	closeFloatsOf: function () {
		return this._closeFloats(false, zkau._shallCloseOf, arguments);
	},
	_closeFloats: function (onfocus, shallClose, ancestors) {
		if (this._ftid) {
			var n = $e(this._ftid);
			if ($visible(n) && getZKAttr(n, "animating") != "hide"
			&& (!onfocus || shallClose(n, ancestors))) {
				this._close(n);
				this._ftid = null;
				return true;
			}
		}
		return false;
	},
	/** Adds elements that we have to hide what they covers.
	 */
	addHideCovered: function (ary) {
		if (this._ftid) {
			var el = $e(this._ftid);
			if (el) ary.push(el);
		}
	},
//zk.Float doesn't support asPopup. Reason: if asPopup is true, there must
//be multiple floats, i.e., zk.Floats shall be used instead
	/** Sets the popup ID.
	 */
	setFloatId: function (id) {
		this._ftid = id;
	}
};

/*Floats: used to be added to zkau.floats
 * Derives must provide an implementation of _close(el).
 */
zk.Floats = zClass.create();
zk.Floats.prototype = {
	initialize: function () {
		this._ftids = [];
		this._aspps = {}; //(id, whether a float behaves like a popup)
	},
	/** returns true if no float at all. */
	empty: function () {
		return !this._ftids.length;
	},
	/** Closes this float if the specified ID is matched.
	 * @return whether it is closed
	 */
	close: function (id) {
		for (var j = this._ftids.length; j;)
			if (this._ftids[--j] == id) {
				this.closeFloats();
				return true;
			}
		return false;
	},
	/** Closes (hides) all floats unless it is an ancestor of any of arguments.
	 * @param arguments a list of components that shall be closed
	 */
	closeFloats: function () {
		return this._closeFloats(false, zkau._shallCloseBut, arguments);
	},
	/** Closes all floats when a component is getting the focus.
	 * @param arguments a list of components that shall be closed
	 */
	closeFloatsOnFocus: function () {
		return this._closeFloats(true, zkau._shallCloseBut, arguments);
	},
	/** Closes all floats if it belongs to any of arguments.
	 * @since 3.0.2
	 */
	closeFloatsOf: function () {
		return this._closeFloats(false, zkau._shallCloseOf, arguments);
	},
	_closeFloats: function (onfocus, shallClose, ancestors) {
		var closed;
		for (var j = this._ftids.length; --j >= 0;) {
			var id = this._ftids[j];
			var n = $e(id);
			if ($visible(n) && getZKAttr(n, "animating") != "hide"
			&& ((!onfocus && !this._aspps[id]) || shallClose(n, ancestors))) {
				this._ftids.splice(j, 1);
				this._close(n);
				closed = true;
			}
		}
		return closed;
	},
	/** Adds elements that we have to hide what they covers.
	 */
	addHideCovered: function (ary) {
		for (var j = 0, fl = this._ftids.length; j < fl; ++j) {
			var el = $e(this._ftids[j]);
			if (el) ary.push(el);
		}
	},

	getFloatIds: function () {
		return this._ftids;
	},
	/** Adds the float ID.
	 *
	 * @param asPopup whether the float behaves like a popup window
	 * (i.e., it remains open if closeFloats is called due to
	 * the activities of its child),
	 * Otherwise, the float is always closed when closeFloats is called.
	 */
	addFloatId: function (id, asPopup) {
		this._ftids.push(id);
		if (asPopup) this._aspps[id] = true;
	},
	removeFloatId: function (id) {
		this._ftids.remove(id);
		delete this._aspps[id];
	}
};

//Histroy//
zk.History = zClass.create();
zk.History.prototype = {
	initialize: function () {
		this.curbk = "";
		zk.addBeforeInit(function () { // Bug #1847708
			zkau.history.checkBookmark(); // We don't need to wait for the first time.
			setInterval("zkau.history.checkBookmark()", 250);
		});
			//Though IE use history.html, timer is still required
			//because user might specify URL directly
	},
	/** Sets a bookmark that user can use forward and back buttons */
	bookmark: function (nm) {
		if (this.curbk != nm) {
			this.curbk = nm; //to avoid loop back the server
			var encnm = encodeURIComponent(nm);
			location.hash = zk.safari || !encnm ? encnm: '#' + encnm;
			this._bkIframe(nm);
			zkau.onURLChange();
		}
	},
	/** Checks whether the bookmark is changed. */
	checkBookmark: function() {
		var nm = this.getBookmark();
		if (nm != this.curbk) {
			this.curbk = nm;
			zkau.send({uuid: '', cmd: "onBookmarkChange", data: [nm]}, 50);
			zkau.onURLChange();
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
		var url = zk.getUpdateURI("/web/js/zk/html/history.html", true),
			ifr = $e('zk_histy');
		if (!ifr) ifr = zk.newFrame('zk_histy', url, "display:none");

		if (nm) url += '?' +encodeURIComponent(nm);
		ifr.src = url;
	}: zk.voidf,
	/** called when history.html is loaded*/
	onHistoryLoaded: zk.ie ? function (src) {
		var j = src.indexOf('?');
		var nm = j >= 0 ? src.substring(j + 1): '';
		location.hash = nm ? /*zk.safari ? nm:*/ '#' + nm: '';
		this.checkBookmark();
	}: zk.voidf
};

/** Removes a node. */
zk.remove = function (n) {
	if (n) Element.remove(n);
};

/** An event listener that does nothing but stop event propogation.
 * @since 3.0.4
 */
zk.doEventStop = function (evt) {
	if (!evt) evt = window.event;
	Event.stop(evt);
};
/**
 * Sets the visibility property of the specified cmp.
 * @param {Object} options.anima if true, always invoke zk.show() which
 * handles anima (animation).
 * @since 3.0.5
 */
zk.setVisible = function (cmp, visible, options) {
	//Bug 1896588: if cmp invisible, just don't do animation (performance in IE)
	if ((options && (options.anima || options==true/*backward compatible*/))
	|| zk.isRealVisible(cmp.parentNode, true))
		zk.show(cmp, visible);
	else if (visible) action.show(cmp);
	else action.hide(cmp);
};
if (zk.opera)
	/**
	 * Cleans up the visibility of the specified component.
	 * Note: In Opera, "inherit" will cause a big bug of text input element, which is inside
	 * the specified component.
	 * @since 3.0.8
	 */
	zk.cleanVisibility = function (cmp) {
		cmp.style.visibility = "visible"; // visible will cause an other bug, but we need do it for Input element.
	};
else
	/**
	 * Cleans up the visibility of the specified component.
	 * @since 3.0.8
	 */
	zk.cleanVisibility = function (cmp) {
		cmp.style.visibility = "inherit";
	};
////
//show & hide
/** Shows the specified element with the effect specified in conshow,
 * if any. It will call action.show if no effect is specified.
 * CSA shall not call this method. Rather, call action.show instead.
 * @param {Object} options.noCallback if true, onVisitAt/onHideAt will not be called.
 */
zk.show = function (id, bShow, options) {
	if (bShow == false) {
		zk.hide(id, true, options);
		return;
	}

	var n = $e(id);
	if (n) {
		var js = getZKAttr(n, "conshow");
		if (js) {
			rmZKAttr(n, "conshow"); //avoid dead loop
			try {
				eval(js);
			} finally {
				setZKAttr(n, "conshow", js);
			}
		} else {
			action.show(n, options);
		}
	}
};
/** Hides the specified element with the effect specified in conhide,
 * if any. It will call action.hide if no effect is specified.
 * CSA shall not call this method. Rather, call action.hide instead.
 * @param {Object} options.noCallback if true, onVisitAt/onHideAt will not be called.
 */
zk.hide = function (id, bHide, options) {
	if (bHide == false) {
		zk.show(id, true, options);
		return;
	}

	var n = $e(id);
	if (n) {
		var js = getZKAttr(n, "conhide");
		if (js) {
			rmZKAttr(n, "conhide"); //avoid dead loop
			try {
				eval(js);
			} finally {
				setZKAttr(n, "conhide", js);
			}
		} else {
			action.hide(n, options);
		}
	}
};
/** Shows the exterior. */
zk._showExtr = function (n) {
	if (!getZKAttr(n, "float")) {
		var ext = $e(n.id + "!chdextr");
		if (ext && "true" == getZKAttr(ext, "coexist")) {
			ext.style.display = "";
			ext = $e(n.id + "!chdextr2"); //hbox/vbox's space
			if (ext && ext.style.width != "0"
			&& ext.style.height != "0") //box use hidden to solve some browser bugs
				ext.style.display = "";
		}
	}
};
/** Hides the exterior. */
zk._hideExtr = function (n) {
	if (!getZKAttr(n, "float")) {
		var ext = $e(n.id + "!chdextr");
		if (ext && "true" == getZKAttr(ext, "coexist")) {
			ext.style.display = "none";
			ext = $e(n.id + "!chdextr2"); //hbox/vbox's space
			if (ext) ext.style.display = "none";
		}
	}
};

///////////////////
// Communication //
comm = {}
/** Sends the onClick event to the server.
 * @param cmp the component's UUID, a child element's ID (xxx!yyy),
 * or a child element
 * @param area a string to identify this click
 * @since 3.0.5
 */
comm.sendClick = function (cmp, area) {
	cmp = $outer(cmp);
	if (cmp)
		zkau.send({uuid: cmp.id, cmd: "onClick", data: [area], ctl: true});
};
/** Sends the onUser event to the server.
 * @param cmp the component's UUID, a child element's ID (xxx!yyy),
 * or a child element
 * @param arguments[1-n] data (Event.getData)
 * @since 3.0.5
 */
comm.sendUser = function (cmp) {
	cmp = $outer(cmp);
	if (cmp) {
		var len = arguments.length, data;
		if (len > 1) {
			data = [];
			for (var j = 1; j < len; ++j)
				data[j - 1] = arguments[j];
		}
		zkau.send({uuid: cmp.id, cmd: "onUser", data: data, ctl: true});
	}
};
/** Sends the specified event to the server.
 * @param cmp the component's UUID, a child element's ID (xxx!yyy),
 * or a child element
 * @param evt the event name, e.g., onMyEvent
 * @param arguments[1-n] data (Event.getData)
 * @since 3.0.5
 */
comm.sendEvent = function (cmp, evt) {
	cmp = $outer(cmp);
	if (cmp) {
		var len = arguments.length, data;
		if (len > 2) {
			data = [];
			for (var j = 2; j < len; ++j)
				data[j - 2] = arguments[j];
		}
		zkau.send({uuid: cmp.id, cmd: evt, data: data, ctl: true});
	}
};

//////////////
/// Action ///
/** Basic utilities for Client Side Action.
 */
action = {};
/** Makes a component visible.
 * @param {Object} options.noCallback if true, onVisitAt/onHideAt will not be called.
 */
action.show = function (id, options) {
	var n = $e(id);
	if (n)
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "zk.show");
		} else {
			zk._showExtr(n);  //parent visible first
			n.style.display = "";
			if ((!options || (options!=true/*backward*/ && !options.noCallback))
			&& zk.isRealVisible(n))
				zk.onVisiAt(n); //callback later
				//Bug 1896588: don't do onVisiAt if not visible
		}
};

/** Makes a component invisible.
 * @param {Object} options.noCallback if true, onVisitAt/onHideAt will not be called.
 */
action.hide = function (id, options) {
	var n = $e(id);
	if (n)
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "zk.hide");
		} else {
			if ((!options || (options!=true/*backward*/ && !options.noCallback))
			&& zk.isRealVisible(n))
				zk.onHideAt(n); //callback first
				//Bug 1896588: don't do onHideAt if not visible
			n.style.display = "none";
			zk._hideExtr(n); //hide parent later
		}
};

///////////
// anima //
/* Animation effects. It requires the component to have the <div><div>
 * structure.
 */
anima = {
	/** @since 3.0.1 */
	count: 0,
	/** Make a component visible by increasing the opacity.
	 *
	 * <p>Event: <code>beforeAppear</code> and <code>afterAppear</code>.
	 * The <code>beforeAppear</code> callback function is called before the
	 * effect begins. The <code>afterAppear</code> callback function
	 * is called after the effect is finished. (since 3.5.0)
	 * </p>
	 * @param id component or its ID
	 */
	appear: function (id, dur) {
		var n = $e(id);
		if (n) {
			if (getZKAttr(n, "animating")) {
				zk._addAnique(n.id, "anima.appear");
			} else {
				++anima.count;
				setZKAttr(n, "animating", "show");
				zk._showExtr(n);  //parent visible first
				zEffect.Appear(n, {
					duration:dur ? dur/1000: 0.6, name: "Appear",
					beforeStart: anima._beforeAnimaVisi,
					afterFinish: anima._afterAnimaVisi});
			}
		}
	},
	/**
	 * Make a component visible by moving down.
	 * @param {Object} id
	 * @since 3.0.2
	 * @see #moveBy
	 */
	moveDown: function (id) {
		anima.moveBy(id, 'top');
	},
	/**
	 * Make a component visible by moving right.
	 * @param {Object} id
	 * @since 3.0.2
	 * @see #moveBy
	 */
	moveRight: function (id) {
		anima.moveBy(id, 'left');
	},
	/**
	 * Make a component visible by moving diagonal.
	 * @param {Object} id
	 * @since 3.0.2
	 * @see #moveBy
	 */
	moveDiagonal: function (id) {
		anima.moveBy(id);
	},
	/** Make a component visible by moving.
	 *
	 * <p>Event: <code>beforeMoveBy</code> and <code>afterMoveBy</code>.
	 * The <code>beforeMoveBy</code> callback function is called before the
	 * effect begins. The <code>afterMoveBy</code> callback function
	 * is called after the effect is finished. (since 3.5.0)
	 * </p>
	 *
	 * @param id component or its ID
	 * @param pos the move position. "top" means from 0 to the original top,
	 *  "left" means from 0 to the original left.
	 * @since 3.0.2
	 */
	moveBy: function (id, pos, dur) {
		var n = $e(id);
		if (n) {
			if (getZKAttr(n, "animating")) {
				zk._addAnique(n.id, "anima." + (pos == "top" ? "moveDown" : pos == "left" ?
					"moveRight" : "moveBy"));
			} else {
				++anima.count;
				setZKAttr(n, "animating", "show");
				zk._showExtr(n);  //parent visible first
				if (!pos) pos = "topleft"
				zEffect.MoveBy(n, 0, 0, {
					duration:dur ? dur/1000: 0.6, name: "MoveBy",
					beforeStart: anima._beforeAnimaVisi,
					afterFinish: anima._afterAnimaVisi,
					afterSetup: function(effect) {
						if (pos.indexOf("left") > -1) {
							effect.options.x = effect.originalLeft;
							effect.originalLeft = 0;
						}
						if (pos.indexOf("top") > -1) {
							effect.options.y = effect.originalTop;
							effect.originalTop = 0;
						}
						effect.element.show();
				}});
			}
		}
	},
	/** Make a component invisible by sliding in.
	 * <p>Event: <code>beforeSlideIn</code> and <code>afterSlideIn</code>.
	 * The <code>beforeSlideIn</code> callback function is called before the
	 * effect begins. The <code>afterSlideIn</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param id component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 * @since 3.5.0
	 */
	slideIn: function (id, anchor, dur) {
		var n = $e(id);
		if (n) {
			if (getZKAttr(n, "animating")) {
				zk._addAnique(n.id, "anima.slideIn");
			} else {
				++anima.count;
				setZKAttr(n, "animating", "show");
				zk._showExtr(n); //callback first
				zEffect.SlideIn(n, anchor, {
					duration:dur ? dur/1000: 0.4, name: "SlideIn",
					beforeStart: anima._beforeAnimaVisi,
					afterFinish: anima._afterAnimaVisi});
			}
		}
	},
	/** Make a component invisible by sliding out.
	 * <p>Event: <code>beforeSlideOut</code> and <code>afterSlideIn</code>.
	 * The <code>beforeSlideIn</code> callback function is called before the
	 * effect begins. The <code>afterSlideOut</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param id component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 * @since 3.5.0
	 */
	slideOut: function (id, anchor, dur) {
		var n = $e(id);
		if (n) {
			if (getZKAttr(n, "animating")) {
				zk._addAnique(n.id, "anima.slideOut");
			} else {
				++anima.count;
				setZKAttr(n, "animating", "hide");
				zk.onHideAt(n); //callback first
				zEffect.SlideOut(n, anchor, {
					duration:dur ? dur/1000: 0.4, name: "SlideOut",
					beforeStart: anima._beforeAnimaHide,
					afterFinish: anima._afterAnimaHide});
			}
		}
	},
	/** Make a component visible by sliding down.
	 *
	 * <p>Event: <code>beforeSlideDown</code> and <code>afterSlideDown</code>.
	 * The <code>beforeSlideDown</code> callback function is called before the
	 * effect begins. The <code>afterSlideDown</code> callback function
	 * is called after the effect is finished. (since 3.5.0)
	 * </p>
	 * @param id component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 */
	slideDown: function (id, anchor, dur) {
		var n = $e(id);
		if (n) {
			if (getZKAttr(n, "animating")) {
				zk._addAnique(n.id, "anima.slideDown");
			} else {
				if (anchor && typeof anchor != "string") {
					dur = anchor.duration; // backward compatible
					anchor = 't';
				}
				++anima.count;
				setZKAttr(n, "animating", "show");
				zk._showExtr(n);  //parent visible first
				zEffect.SlideDown(n, anchor, {
					duration:dur ? dur/1000: 0.4, name: "SlideDown",
					beforeStart: anima._beforeAnimaVisi,
					afterFinish: anima._afterAnimaVisi, y :0});
					//duration must be less than 0.5 since other part assumes it
			}
		}
	},
	/** Make a component invisible by sliding up.
	 *
	 * <p>Event: <code>beforeSlideUp</code> and <code>afterSlideUp</code>.
	 * The <code>beforeSlideUp</code> callback function is called before the
	 * effect begins. The <code>afterSlideUp</code> callback function
	 * is called after the effect is finished. (since 3.5.0)
	 * </p>
	 * @param id component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 */
	slideUp: function (id, anchor, dur) {
		var n = $e(id);
		if (n) {
			if (getZKAttr(n, "animating")) {
				zk._addAnique(n.id, "anima.slideUp");
			} else {
				if (anchor && typeof anchor != "string") {
					dur = anchor.duration; // backward compatible
					anchor = 't';
				}
				++anima.count;
				setZKAttr(n, "animating", "hide");
				zk.onHideAt(n); //callback first
				zEffect.SlideUp(n, anchor, {
					duration:dur ? dur/1000: 0.4, name: "SlideUp",
					beforeStart: anima._beforeAnimaHide,
					afterFinish: anima._afterAnimaHide});
					//duration must be less than 0.5 since other part assumes it
			}
		}
	},
	/** Make a component invisible by fading it out.
	 *
	 * <p>Event: <code>beforeFade</code> and <code>afterFade</code>.
	 * The <code>beforeFade</code> callback function is called before the
	 * effect begins. The <code>afterFade</code> callback function
	 * is called after the effect is finished. (since 3.5.0)
	 * </p>
	 * @param id component or its ID
	 */
	fade: function (id, dur) {
		var n = $e(id);
		if (n) {
			if (getZKAttr(n, "animating")) {
				zk._addAnique(n.id, "anima.fade");
			} else {
				++anima.count;
				setZKAttr(n, "animating", "hide");
				zk.onHideAt(n); //callback first
				zEffect.Fade(n, {
					duration:dur ? dur/1000: 0.55, name: "Fade",
					beforeStart: anima._beforeAnimaHide,
					afterFinish: anima._afterAnimaHide});
			}
		}
	},
	/** Make a component invisible by puffing away.
	 *
	 * <p>Event: <code>beforePuff</code> and <code>afterPuff</code>.
	 * The <code>beforePuff</code> callback function is called before the
	 * effect begins. The <code>afterPuff</code> callback function
	 * is called after the effect is finished. (since 3.5.0)
	 * </p>
	 * @param id component or its ID
	 */
	puff: function (id, dur) {
		var n = $e(id);
		if (n) {
			if (getZKAttr(n, "animating")) {
				zk._addAnique(n.id, "anima.puff");
			} else {
				++anima.count;
				setZKAttr(n, "animating", "hide");
				zk.onHideAt(n); //callback first
				zEffect.Puff(n, {
					duration:dur ? dur/1000: 0.6, name: "Puff",
					beforeStart: anima._beforeAnimaHide,
					afterFinish: anima._afterAnimaHide});
			}
		}
	},
	/** Make a component invisible by fading and dropping out.
	 *
	 * <p>Event: <code>beforeDropOut</code> and <code>afterDropOut</code>.
	 * The <code>beforeDropOut</code> callback function is called before the
	 * effect begins. The <code>afterDropOut</code> callback function
	 * is called after the effect is finished. (since 3.5.0)
	 * </p>
	 * @param id component or its ID
	 */
	dropOut: function (id, dur) {
		var n = $e(id);
		if (n) {
			if (getZKAttr(n, "animating")) {
				zk._addAnique(n.id, "anima.dropOut");
			} else {
				++anima.count;
				setZKAttr(n, "animating", "hide");
				zk.onHideAt(n); //callback first
				zEffect.DropOut(n, {
					duration:dur ? dur/1000: 0.6, name: "DropOut",
					beforeStart: anima._beforeAnimaHide,
					afterFinish: anima._afterAnimaHide});
			}
		}
	},
	// private
	// The function called before the hidden effect begins.(since 3.5.0)
	_beforeAnimaHide: function (ef) {
		var n = ef.element || ef.effects[0].element;
		if (n) zk.fire(n, "before" + ef.name, [n, ef]);
	},
	// private
	// The function called before the visible effect begins.(since 3.5.0)
	_beforeAnimaVisi: function (ef) {
		var n = ef.element || ef.effects[0].element;
		if (n) zk.fire(n, "before" + ef.name, [n, ef]);
	},
	// private
	// The function called after the hidden effect is finished.(since 3.5.0)
	_afterAnimaHide: function (ef) {
		var n = ef.element || ef.effects[0].element;
		if (n) {
			zk._hideExtr(n); //hide parent later
			--anima.count;
			rmZKAttr(n, "animating");
			zk._doAnique(n.id);
			zk.fire(n, "after" + ef.name, [n, ef]);
		}
	},
	// private
	// The function called after the visible effect is finished.(since 3.5.0)
	_afterAnimaVisi: function (ef) {
		var n = ef.element || ef.effects[0].element;
		if (n) {
			--anima.count;
			rmZKAttr(n, "animating");
			if (zk.ie) zk.redoCSS(n); // fixed a bug of the finished animation for IE
			zk.onVisiAt(n);
			zk._doAnique(n.id);
			zk.fire(n, "after" + ef.name, [n, ef]);
		}
	}
};

//animation queue
zk._anique = {};
	//queue for waiting animating to clear: map(id, array(js_func_name))
zk._addAnique = function(id, funcnm) {
	var ary = zk._anique[id];
	if (!ary)
		ary = zk._anique[id] = [];
	ary.push(funcnm);
};
zk._doAnique = function (id) {
	var ary = zk._anique[id];
	if (ary) {
		var n = $e(id), al = ary.length;
		while (al) {
			if (getZKAttr(n, "animating"))
				break;
			var js = ary.shift();
			eval(js+"('"+id+"')");
			al--;
		}

		if (!al)
			delete zk._anique[id];
	}
};

} //if (!window.anima)
