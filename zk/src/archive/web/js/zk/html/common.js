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
	This program is distributed under GPL Version 2.0 in the hope that
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
/** Tom Yeh 20070630: Remove unused codes
String.prototype.skipWhitespacesBackward = function (j) {
	for (;j >= 0; --j) {
		var cc = this.charAt(j);
		if (cc != ' ' && cc != '\t' && cc != '\n' && cc != '\r')
			break;
	}
	return j;
};
*/

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

/** Returns whether it is part of the class name
 * of the specified element.
 */
zk.hasClass = function (el, clsnm) {
	var cn = el.className;
	return cn && (' '+cn+' ').indexOf(' '+clsnm+' ') != -1;
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
    	var re = new RegExp('(?:^|\\s+)' + clsnm + '(?:\\s+|$)', "g");
        el.className = el.className.replace(re, " ");            
	}
};

/** Sets the offset height. */
zk.setOffsetHeight = function (el, hgh) {
	hgh = hgh
		- $int(Element.getStyle(el, "padding-top"))
		- $int(Element.getStyle(el, "padding-bottom"))
		- $int(Element.getStyle(el, "margin-top"))
		- $int(Element.getStyle(el, "margin-bottom"))
		- $int(Element.getStyle(el, "border-top-width"))
		- $int(Element.getStyle(el, "border-bottom-width"));
	el.style.height = (hgh > 0 ? hgh: 0) + "px";
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
zk.borders = {l: "border-left-width", r: "border-right-width", t: "border-top-width", b: "border-bottom-width"};
zk.paddings = {l: "padding-left", r: "padding-right", t: "padding-top", b: "padding-bottom"};
/** Returns the summation of the specified styles.
 *  For example,
 *  zk.sumStyles(el, "lr", zk.paddings) sums the style values of
 * zk.paddings['l'] and zk.paddings['r'].
 *
 * @param {String} areas the areas is abbreviation for left "l", right "r", top "t", and bottom "b".
 * So you can specify to be "lr" or "tb" or more.
 * @param styles {zk.paddings} or {zk.borders}. 
 * @return {Number}
 * @since 3.0.0
 */
zk.sumStyles = function (el, areas, styles) {
	var val = 0;
    for (var i = 0, l = areas.length; i < l; i++){
		 var w = $int(Element.getStyle(el, styles[areas.charAt(i)]));
         if (!isNaN(w)) val += w;
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
	var areas = "lr";
	if (isHgh) areas = "tb";
    size -= (zk.sumStyles(el, areas, zk.borders) + zk.sumStyles(el, areas, zk.paddings));
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
		if (el.getBoundingClientRect){ // IE
			var b = el.getBoundingClientRect();
			return [b.left + zk.innerX() - 3 , b.top + zk.innerY() - 3];
		}
		ofs = Position.cumulativeOffset(el);
	}
	var scrolls = Position.realOffset(el);
	scrolls[0] -= zk.innerX(); scrolls[1] -= zk.innerY(); 
	return [ofs[0] - scrolls[0], ofs[1] - scrolls[1]];
};
if (zk.safari) {
	//fix safari's bug
	zk._oldposofs = Position.positionedOffset;
	Position.positionedOffset = function (el) {
		if ($tag(el) === "TR" && el.cells.length)
			el = el.cells[0];
		return zk._oldposofs(el);
	};
}
if (zk.gecko || zk.safari) {
	zk._oldcumofs = Position.cumulativeOffset;
	Position.cumulativeOffset = function (el) {
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
		el.style.top = "-10000px"; //avoid annoying effect
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
/** Position a component being releted to another. */
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

/** Returns the style's coordination in [integer, integer].
 * Note: it ignores the unit and assumes px (so pt or others will be wrong)
 */
zk.getStyleOffset = function (el) {
	return [$int(el.style.left), $int(el.style.top)];
};
/** Converts from absolute coordination to style's coordination.
 * It is only useful for table's cell.
 * We cannot use zk.toParentCoord, because
 * after calling Draggable, offsetParent becomes BODY but
 * style.left/top is still relevant to original offsetParent
 */
zk.toStyleOffset = function (el, x, y) {
	var oldx = el.style.left, oldy = el.style.top;
	if (zk.opera) {
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

	var ofs1 = Position.cumulativeOffset(el);
	var ofs2 = zk.getStyleOffset(el);
	ofs1 = [x - ofs1[0] + ofs2[0], y  - ofs1[1] + ofs2[1]];

	el.style.left = oldx;
	el.style.top = oldy;
	return ofs1;
};

/** Whether el1 and el2 are overlapped. */
zk.isOverlapped = function (el1, el2) {
	return zk.isOffsetOverlapped(
		Position.cumulativeOffset(el1), [el1.offsetWidth, el1.offsetHeight],
		Position.cumulativeOffset(el2), [el2.offsetWidth, el2.offsetHeight]);
};
/** Whether ofs1/dim1 is overlapped with ofs2/dim2. */
zk.isOffsetOverlapped = function (ofs1, dim1, ofs2, dim2) {
	var o1x1 = ofs1[0], o1x2 = dim1[0] + o1x1,
		o1y1 = ofs1[1], o1y2 = dim1[1] + o1y1;
	var o2x1 = ofs2[0], o2x2 = dim2[0] + o2x1,
		o2y1 = ofs2[1], o2y2 = dim2[1] + o2y1;
	return o2x1 <= o1x2 && o2x2 >= o1x1 && o2y1 <= o1y2 && o2y2 >= o1y1;
};

/** Whether an element is really visible. */
zk.isRealVisible = function (e) {
	if (!e) return false;
	do {
		if (!$visible(e)) return false;
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
			try {el.focus();} catch (e) {}
			//IE throws exception when focus in some cases
			return true;
		}
		if (checkA && tn == "A") {
			for (var n = el; (n = $parent(n))/*yes, assign*/;) {
				if (getZKAttr(n, "type")) {
					try {el.focus();} catch (e) {}
					//IE throws exception when focus in some cases
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
				try {cmp.focus();} catch (e) {}
			}, 0);
		}
		//IE throws exception when focus in some cases
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
	if (document.selection != null && inp.selectionStart == null) { //IE
		try {
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
		} catch (e) {
			return [0, 0];
		}
	} else { //Gecko
		return [inp.selectionStart, inp.selectionEnd];
	}
}

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

/** Returns whether a node is an ancestor of another (including itself).
 *
 * @param checkuuid whether to check UUID is the same (i.e., whether
 * they are different part of the same component).
 */
zk.isAncestor = function (p, c, checkuuid) {
	if (checkuuid && $uuid(p) == $uuid(c))
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
 * @param checkuuid whether to check UUID is the same (i.e., whether
 * they are different part of the same component).
 */
zk.isAncestorX = function (p, ary, checkuuid) {
	for (var j = 0, al = ary.length; j < al; ++j)
		if (zk.isAncestor(p, ary[j], checkuuid))
			return true;
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
	zk._actg1 = ["IFRAME"/*,"APPLET"*/]; //comment out APPLET for better performance
	zk._actg2 = ["A","BUTTON","TEXTAREA","INPUT"];
	if (zk.ie && !zk.ie7) { //ie7 solves the z-order issue of SELECT
		zk._actg1.unshift("SELECT"); //change visibility is required
	} else
		zk._actg2.unshift("SELECT");

	zk.coveredTagnames = zk._actg1; //backward compatible 2.4 or before

	zk._disTags = []; //A list of {element: xx, what: xx}
	zk._hidCvred = []; //A list of {element: xx, visibility: xx}
}

/** Disables all active tags. */
zk.disableAll = function (parent) {
	for (var j = 0, al1 = zk._actg1.length; j < al1; j++)
		zk._dsball(parent, document.getElementsByTagName(zk._actg1[j]), true);

	if (!zk.ndbModal) //not disable-behind-modal
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

		var what;
		var tn = $tag(el);
		if (visibility) { //_actg1
			if (tn == "IFRAME" && getZKAttr(el, "autohide") != "true")
				continue; //handle only autohide iframe

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
			if (bug1498895) {
				var tn = $tag(el);
				if ((tn == "INPUT" && (el.type == "text" || el.type == "password"))
				||  tn == "TEXTAREA"){
				//focus only visible (to prevent scroll)
					try {
						var ofs = Position.cumulativeOffset(el);
						if (ofs[0] >= zk.innerX() && ofs[1] >= zk.innerY()
						&& (ofs[0]+20) <= (zk.innerX()+zk.innerWidth())
						&& (ofs[1]+20) <= (zk.innerY()+zk.innerHeight())) {
							el.focus();
							bug1498895 = false;
						}
					} catch (e) {
					}
				}
			}
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
		var ifr = "IFRAME" == cts[j];
		loop_els:
		for (var k = 0, elen = els.length; k < elen; k++) {
			var el = els[k];
			if (!zk.isRealVisible(el)) continue;

			for (var m = 0, al = ary.length; m < al; ++m) {
				if (zk.isAncestor(ary[m], el))
					continue loop_els;
			}

			var overlapped = false;
			if (!ifr || getZKAttr(el, "autohide") == "true") {
			//Note: z.autohide may be set dynamically,
			//so consider it as not overlapped
				for (var m = 0, al = ary.length; m < al; ++m) {
					if (zk.isOverlapped(ary[m], el)) {
						overlapped = true;
						break;
					}
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

/** Scroll inner into visible, assuming outer has a scrollbar. */
zk.scrollIntoView = function (outer, inner) {
	if (outer && inner) {
		var padding = $int(Element.getStyle(inner, "padding-top"));
		var limit = inner.offsetTop - padding;
		if (limit < outer.scrollTop) {
			outer.scrollTop = limit;
		} else {
			limit = 3 + inner.offsetTop + inner.offsetHeight
				- outer.scrollTop - outer.clientHeight;
			if (limit > 0) outer.scrollTop += limit;
		}
	}
};

/** Go to the specified uri.
 * @param overwrite whether to overwrite the history
 * @param target the target frame (ignored if overwrite is true
 */
zk.go = function (url, overwrite, target) {
	var bProgress = !zk.opera && !zk.keepDesktop
		&& window.location.href.indexOf('#') < 0; //whether to show progress
		//we don't show progress for opera, since user might press BACK to
		//return this page (and found the progress dlg remains on browse)
		//
		//Bug 1773575: with # and the same url, no redraw
	if (bProgress && url) {
		bProgress =	url.indexOf("://") < 0 && !url.startsWith("mailto:")
			&& !url.startsWith("javascript:") && !url.startsWith("about:");
	}
	if (!url) {
		if (bProgress) zk.progress(); //BACK button issue
		window.location.reload();
	} else if (overwrite) {
		if (bProgress) zk.progress();
		window.location.replace(url);
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
		if (url && !zk.isNewWindow(url, target) && bProgress)
			zk.progress();
		frm.submit();
	} else {
		if (bProgress) zk.progress();
		window.location.href = url;
	}
	if (bProgress) zk.progressDone(); // Bug #1843032
};
/** Tests whether a new window will be opened.
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
/** Copies the width of each cell from one row to another.
 * It handles colspan of srcrows, but not dst's colspan, nor rowspan
 *
 * @param {Object} srcrows all rows from the source table. Don't pass just one row
 * @param {Object} mate a component gird or tree or listbox.
 * @param {Boolean} stripe whether renders the stripe.
 * @param {Boolean} again whether re-calculates the width of each cell for opera or safari.
 * @param {Number} column index. It only copy the index column, if any.
 * @since 3.0.0
 */
zk.cpCellWidth = function (dst, srcrows, mate, stripe, again, index) {
	if (dst == null || srcrows == null || !srcrows.length
	|| !dst.cells.length || !zk.isRealVisible(dst))
		return;
	//Note: With Opera, we cannot use table-layout=fixed and we have to assign
	//the table width (test case: fixed-table-header.html)	
	var hdtable = dst.parentNode.parentNode;
	if (hdtable.style.width) {
		var bdtable = srcrows[0].parentNode.parentNode;
		bdtable.style.width = hdtable.style.width;		
	} else if (zk.opera && hdtable) {
		hdtable.style.tableLayout = "auto";
		hdtable.style.width = "";
	}
	var found, scOdd = stripe ? getZKAttr(mate.element, "scOddRow") : null, dstwds = [], cacheCss;
	for (var i = 0, even = true, l = srcrows.length, firstChild; i < l; ++i) {
		var row = srcrows[i], cells = row.cells;		
		if (!zk.isVisible(row)) continue;
		if (!firstChild) firstChild = row;
		if (getZKAttr(row, "loaded") == "false") continue;
		if (stripe && scOdd && zk.isVisible(row)) {
			zk.addClass(row, scOdd, !even);
			even = !even;
		}
		if(!found && getZKAttr(row, "sel") == "true") found = row;
		for (var j = 0, z = 0, le = cells.length ; j < le; ++j) {
			if (j < dst.cells.length) {
				var s = cells[j], d = dst.cells[z], cs = s.colSpan;
				if (!zk.isVisible(d)) { //Bug #1828044
					s.style.display = "none";
					z += cs; // header count
					continue;
				}
				if (cs > 1) {
					if (cs + z <= dst.cells.length) {
						var unwd = [], total = 0, ttlOffset = 0;
						for (var k = 0; k < cs; k++) {
							var d = dst.cells[z+k];
							if (!dstwds[z+k]) {
								var wd =  d.style.width;
								if (wd) {
									if (wd == "auto" || wd.indexOf('%') > -1) 
										d.style.width = zk.revisedSize(d, d.offsetWidth) + "px";
									dstwds[z+k] = d.offsetWidth;
									total += dstwds[z+k];
									ttlOffset += d.offsetWidth;		
								} else unwd.push([d, k]);
							} else {
								total += dstwds[z+k];
								ttlOffset += d.offsetWidth;
							}							
						}
						
						var cell = s.firstChild;
						if (unwd.length) {
							var amount = s.offsetWidth - total;
							if (amount < unwd.length * 20) {
								amount = unwd.length * 20;
								var rwd = zk.revisedSize(s, amount + total);
								s.style.width = rwd + "px";
								cell.style.width = s.style.width;
							}
							var each = Math.max(Math.floor((amount) / unwd.length), 0);
							while (unwd.length)	{								
								var data = unwd.shift();
								var d = data[0], k = data[1];
								if (unwd.length) amount -= each;
									else each = amount;
								var wd = zk.safari ? each  : zk.revisedSize(d, each);
								d.style.width = wd + "px";								
								var cave = d.firstChild;
								if (cave) cave.style.width = zk.revisedSize(cave, wd) + "px";
								dstwds[z+k] = d.offsetWidth;
								total += dstwds[z+k];
								ttlOffset += d.offsetWidth;
							} 
						}	
													
						var rwd = zk.revisedSize(s, total);
						s.style.width = rwd + "px";
						cell.style.width = s.style.width;	
								
						if (!again && i == 0 && zk.ie) setTimeout(function (){zk.cpCellWidth(dst, srcrows, mate, false, true)}, 500);															
					}				
				} else {			
					if (index == null || index == z) {
						if (!dstwds[z]) {
							var wd =  d.style.width, cell = d.firstChild, w;				
							if (wd == "auto" || wd.indexOf('%') > -1) 
								d.style.width = zk.revisedSize(d, d.offsetWidth)+ "px";
							wd = d.style.width;
							dstwds[z] = wd ? (zk.ie && z == dst.cells.length -1 ? d.offsetWidth - 2 : d.offsetWidth) :
								zk.ie && z == dst.cells.length-1 ? s.offsetWidth - 2 : s.offsetWidth;
							
							if (!wd) {
								w =  zk.revisedSize(d, dstwds[z]); 
								d.style.width = w + "px";
							} else w = $int(wd);
							if (cell) cell.style.width = zk.revisedSize(cell, w) + "px";	
						}
						
						var cell = s.firstChild;
						if (!cacheCss || s.className != cacheCss.el.className || s.style.cssText)
							cacheCss = {el: s , size : zk.sumStyles(s, "lr", zk.borders) + zk.sumStyles(s, "lr", zk.paddings)};
						var rwd = dstwds[z] - cacheCss.size;
						rwd = (rwd < 0 ? 0 : rwd ) +"px";// #Bugs 1817636
						if (firstChild == row)
							s.style.width = rwd;
						if (cell) cell.style.width = rwd;
					}
					if (index == z) break;
				}				
				z += cs; // header count
			}
		}		
	}
	if (found) zk.scrollIntoView(mate.body, found);
	if (!again && (zk.safari || zk.opera)) setTimeout(function (){zk.cpCellWidth(dst, srcrows, mate, false, true)}, 5);	
	// Note :we have to re-calculate the width of cell column for safari.
};
/**
Copies the width of each cell from the element's header.
 * It handles colspan of srcrows.
 *
 * @param {Array} dst the element's header. 
 * @param {Array} srcrows each cell.
 * @since 3.0
 */
zk.cpCellArrayWidth = function (dst, srcrows) {
	if (dst == null || srcrows == null || !srcrows.length
	|| !dst.cells.length)
		return;
	for (var j = srcrows.length, cacheCss; --j >= 0;) {
		var s = srcrows.shift();
		var z = zk.cellIndex(s);
		if (dst.cells.length <= z) continue; // Bug #1852313
		var d = dst.cells[z], wd = 0, cell = s.firstChild;
		if (s.colSpan > 1) {
			if (s.colSpan + z <= dst.cells.length) {				
				for (var k = 0; k < s.colSpan; k++) {
					var hd = dst.cells[z+k];
					wd += zk.ie && z+k == dst.cells.length -1 ? hd.offsetWidth - 2 : hd.offsetWidth; 												
				}
			}
		} else {
			if (zk.mozilla)	wd += $int(d.style.width); // Bug #1826938
			else wd += zk.ie && z == dst.cells.length -1 ? d.offsetWidth - 2 : d.offsetWidth;
		}		
		if (!cacheCss || s.className != cacheCss.el.className || s.style.cssText)
			cacheCss = {el: s , size : zk.sumStyles(s, "lr", zk.borders) + zk.sumStyles(s, "lr", zk.paddings)};
		var rwd = wd - cacheCss.size;
		rwd = (rwd < 0 ? 0 : rwd ) +"px";// #Bugs 1817636
		if (!s.parentNode.rowIndex)
			s.style.width = rwd;
		if (cell) cell.style.width = rwd;
	}
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
	var y = val.getFullYear(), m = val.getMonth(), d = val.getDate();

	var ts = txt.split(/\W+/);
	for (var i = 0, j = 0, fl = fmt.length; j < fl; ++j) {
		var cc = fmt.charAt(j);
		if (cc == 'y' || cc == 'M' || cc == 'd' || cc == 'E') {
			var len = 1;
			for (var k = j; ++k < fl; ++len)
				if (fmt.charAt(k) != cc)
					break;

			var nosep; //no separator
			if (k < fl) {
				var c2 = fmt.charAt(k);
				nosep = c2 == 'y' || c2 == 'M' || c2 == 'd' || c2 == 'E';
			}

			var token = ts[i++];
			switch (cc) {
			case 'y':
				if (nosep) {
					if (len <= 3) len = 2;
					if (token.length > len) {
						ts[--i] = token.substring(len);
						token = token.substring(0, len);
					}
				}
				y = $int(token);
				if (isNaN(y)) return null; //failed
				if (y < 100) y += y > 29 ? 1900 : 2000;
				break;
			case 'M':
				if (len <= 2) {
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
							if (zk.SMON[l].split(/\W+/)[0] == token) {
								m = l;
								break;
							}
						} else {
							if (zk.FMON[l].split(/\W+/)[0] == token) {
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
					if (token.length > len) {
						ts[--i] = token.substring(len);
						token = token.substring(0, len);
					}
				}
				d = $int(token);
				if (isNaN(d)) return null; //failed
				break;
			//case 'E': ignored
			}
			j = k - 1;
		}
	}

	var dt = new Date(y, m, d);
	if (strict && (dt.getFullYear() != y
	|| dt.getMonth() != m || dt.getDate() != d))
		return null; //failed
	return dt;
};

/** Generates a formated string for the specified Date object. */
zk.formatDate = function (val, fmt) {
	if (!fmt) fmt = "yyyy/MM/dd";

	var txt = "";
	for (var j = 0, fl = fmt.length; j < fl; ++j) {
		var cc = fmt.charAt(j);
		if (cc == 'y' || cc == 'M' || cc == 'd' || cc == 'E') {
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
				txt += zk.formatFixed(val.getDate(), len);
				break;
			default://case 'E':
				if (len <= 3) txt += zk.SDOW[val.getDay()];
				else txt += zk.FDOW[val.getDay()];
			}
			j = k - 1;
		} else {
			txt += cc;
		}
	}
	return txt;
};

/** Returns an integer of the attribute of the specified element. */
zk.getIntAttr = function (el, nm) {
	return $int(el.getAttribute(nm));
};

//selection//
zk.clearSelection = function(){
	try{
		if(window["getSelection"]){ 
			if(zk.safari){
				window.getSelection().collapse();
			}else{
				window.getSelection().removeAllRanges();
			}
		}else if(document.selection){
			if(document.selection.empty){
				document.selection.empty();
			}else if(document.selection.clear){
				document.selection.clear();
			}
		}
		return true;
	} catch (e){
		return false;
	}
}
/** Disable whether the specified element is selectable. */
zk.disableSelection = function (el) {
	el = $e(el);
	if (el)
		if (zk.gecko)
			el.style.MozUserSelect = "none";
		else if (zk.safari)
			el.style.KhtmlUserSelect = "none"; 
		else if (zk.ie)
			el.onselectstart = function () {return false;};
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
/** Clears selection, if any. */
zk.clearSelection = function (){
	try {
		if (window["getSelection"]){ 
			if (zk.safari) window.getSelection().collapse();
			else window.getSelection().removeAllRanges();
		} else if (document.selection){
			if (document.selection.empty) document.selection.empty();
			else if(document.selection.clear) document.selection.clear();
		}
	} catch (e){ //ignore
	}
}


/*Float: used to be added to zkau.floats
 * Derives must provide an implementation of _close(el).
 */
zk.Float = Class.create();
zk.Float.prototype = {
	initialize: function () {
	},
	/** Closes this float if the specified ID is matched.
	 * @return whether it is closed
	 */
	close: function (id) {
		if (this._ftid == id)
			this.closeFloats();
	},
	/** Closes (hides) all floats.
	 * @param arguments a list of components that shall be closed
	 */
	closeFloats: function () {
		return this._closeFloats(false, arguments);
	},
	/** Closes all floats when a component is getting the focus.
	 * @param arguments a list of components that shall be closed
	 */
	closeFloatsOnFocus: function () {
		return this._closeFloats(true, arguments);
	},
	_closeFloats: function (onfocus, ancestors) {
		if (this._ftid) {
			var n = $e(this._ftid);
			if ($visible(n)
			&& getZKAttr(n, "animating") != "hide"
			&& (!onfocus || !zk.isAncestorX(n, ancestors, true))) {
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
zk.Floats = Class.create();
zk.Floats.prototype = {
	initialize: function () {
		this._ftids = [];
		this._aspps = {}; //(id, whether a float behaves like a popup)
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
	/** Closes (hides) all floats.
	 * @param arguments a list of components that shall be closed
	 */
	closeFloats: function () {
		return this._closeFloats(false, arguments);
	},
	/** Closes all floats when a component is getting the focus.
	 * @param arguments a list of components that shall be closed
	 */
	closeFloatsOnFocus: function () {
		return this._closeFloats(true, arguments);
	},
	_closeFloats: function (onfocus, ancestors) {
		var closed;
		for (var j = this._ftids.length; --j >= 0;) {
			var id = this._ftids[j];
			var n = $e(id);
			if ($visible(n)
			&& getZKAttr(n, "animating") != "hide"
			&& ((!onfocus && !this._aspps[id]) || !zk.isAncestorX(n, ancestors, true))) {
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
zk.History = Class.create();
zk.History.prototype = {
	initialize: function () {
		this.curbk = "";
		zk.addModuleInit(function () { // Bug #1847708
			zkau.history.checkBookmark(); // We don't need to wait for the first time.
			setInterval("zkau.history.checkBookmark()", 520);
		});
			//Though IE use history.html, timer is still required 
			//because user might specify URL directly
	},
	/** Sets a bookmark that user can use forward and back buttons */
	bookmark: function (nm) {
		if (this.curbk != nm) {
			this.curbk = nm; //to avoid loop back the server
			var encnm = encodeURIComponent(nm);
			window.location.hash = zk.safari ? encnm: '#' + encnm;
			if (zk.ie /*|| zk.safari*/) this.bkIframe(nm);
		}
	},
	/** Checks whether the bookmark is changed. */
	checkBookmark: function() {
		var nm = this.getBookmark();
		if (nm != this.curbk) {
			this.curbk = nm;
			zkau.send({uuid: '', cmd: "onBookmarkChanged", data: [nm]}, 50);
		}
	},
	getBookmark: function () {
		var nm = window.location.hash;
		var j = nm.indexOf('#');
		return j >= 0 ? decodeURIComponent(nm.substring(j + 1)): '';
	}
};
if (zk.ie /*|| zk.safari*/) {
	/** bookmark iframe */
	zk.History.prototype.bkIframe = function (nm) {
		var url = zk.getUpdateURI("/web/js/zk/html/history.html", true);
		if (nm) url += '?' +encodeURIComponent(nm);

		var ifr = $e('zk_histy');
		if (ifr) {
			ifr.src = url;
		} else {
			zk.newFrame('zk_histy', url,
				/*zk.safari ? "width:0;height:0;display:inline":*/ "display:none");
		}
	};
	/** called when history.html is loaded*/
	zk.History.prototype.onHistoryLoaded = function (src) {
		var j = src.indexOf('?');
		var nm = j >= 0 ? src.substring(j + 1): '';
		window.location.hash = nm ? /*zk.safari ? nm:*/ '#' + nm: '';
		this.checkBookmark();
	};
}

/** Removes a node. */
zk.remove = function (n) {
	if (n) Element.remove(n);
};

////
//show & hide
/** The lowest level to make a component visible/invisible.
 * CSA shall not call this method.
 */
zk.show = function (id, bShow) {
	if (bShow == false) {
		zk.hide(id);
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
			action.show(n);
		}
	}
};
/** The lowest level to make a component invisible/visible.
 * CSA shall not call this method.
 */
zk.hide = function (id, bHide) {
	if (bHide == false) {
		zk.show(id);
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
			action.hide(n);
		}
	}
};
/** Shows the exterior. */
zk._showExtr = function (n) {
	if ("true" != getZKAttr(n, "float")) {
		var ext = $e(n.id + "!chdextr");
		if (ext && "true" == getZKAttr(ext, "coexist")) {
			ext.style.display = "";
			ext = $e(n.id + "!chdextr2"); //hbox/vbox
			if (ext) ext.style.display = "";
		}
	}
};
/** Hides the exterior. */
zk._hideExtr = function (n) {
	if ("true" != getZKAttr(n, "float")) {
		var ext = $e(n.id + "!chdextr");
		if (ext && "true" == getZKAttr(ext, "coexist")) {
			ext.style.display = "none";
			ext = $e(n.id + "!chdextr2"); //hbox/vbox
			if (ext) ext.style.display = "none";
		}
	}
};

//////////////
/// ACTION ///
/** Basic utilities for Client Side Action.
 */
action = {};

/** Makes a component visible.
 */
action.show = function (id) {
	var n = $e(id);
	if (n)
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "zk.show");
		} else {
			zk._showExtr(n);  //parent visible first
			n.style.display = "";
			zk.onVisiAt(n); //callback later
		}
};

/** Makes a component invisible.
 */
action.hide = function (id) {
	var n = $e(id);
	if (n)
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "zk.hide");
		} else {
			zk.onHideAt(n); //callback first
			n.style.display = "none";
			zk._hideExtr(n); //hide parent later
		}
};

///////////
// anima //
/* Animation effects. It requires the component to have the <div><div>
 * structure.
 */
anima = {}
/** @since 3.0.1 */
anima.count = 0;

/** Make a component visible by increasing the opacity.
 * @param id component or its ID
 */
anima.appear = function (id, dur) {
	var n = $e(id);
	if (n) {
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "anima.appear");
		} else {
			++anima.count;
			setZKAttr(n, "animating", "show");
			zk._showExtr(n);  //parent visible first
			Effect.Appear(n, {duration:dur ? dur/1000: 0.8, afterFinish: anima._afterVisi});
		}
	}
};
/** Make a component visible by sliding down.
 * @param id component or its ID
 */
anima.slideDown = function (id, dur) {
	var n = $e(id);
	if (n) {
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "anima.slideDown");
		} else {
			++anima.count;
			setZKAttr(n, "animating", "show");
			zk._showExtr(n);  //parent visible first
			Effect.SlideDown(n, {duration:dur ? dur/1000: 0.4, afterFinish: anima._afterVisi});
				//duration must be less than 0.5 since other part assumes it
		}
	}
};
/** Make a component invisible by sliding up.
 * @param id component or its ID
 */
anima.slideUp = function (id, dur) {
	var n = $e(id);
	if (n) {
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "anima.slideUp");
		} else {
			++anima.count;
			setZKAttr(n, "animating", "hide");
			zk.onHideAt(n); //callback first
			Effect.SlideUp(n, {duration:dur ? dur/1000: 0.4, afterFinish: anima._afterHide});
				//duration must be less than 0.5 since other part assumes it
		}
	}
};
/** Make a component invisible by fading it out.
 * @param id component or its ID
 */
anima.fade = function (id, dur) {
	var n = $e(id);
	if (n) {
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "anima.fade");
		} else {
			++anima.count;
			setZKAttr(n, "animating", "hide");
			zk.onHideAt(n); //callback first
			Effect.Fade(n, {duration:dur ? dur/1000: 0.55, afterFinish: anima._afterHide});
		}
	}
};
/** Make a component invisible by puffing away.
 * @param id component or its ID
 */
anima.puff = function (id, dur) {
	var n = $e(id);
	if (n) {
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "anima.puff");
		} else {
			++anima.count;
			setZKAttr(n, "animating", "hide");
			zk.onHideAt(n); //callback first
			Effect.Puff(n, {duration:dur ? dur/1000: 0.7, afterFinish: anima._afterHide0});
		}
	}
};
/** Make a component invisible by fading and dropping out.
 * @param id component or its ID
 */
anima.dropOut = function (id, dur) {
	var n = $e(id);
	if (n) {
		if (getZKAttr(n, "animating")) {
			zk._addAnique(n.id, "anima.dropOut");
		} else {
			++anima.count;
			setZKAttr(n, "animating", "hide");
			zk.onHideAt(n); //callback first
			Effect.DropOut(n, {duration:dur ? dur/1000: 0.7, afterFinish: anima._afterHide0});
		}
	}
};

anima._afterVisi = function (ef) {
	var n = ef.element;
	if (n) {
		--anima.count;
		rmZKAttr(n, "animating");
		zk.onVisiAt(n);
		zk._doAnique(n.id);
	}
};
anima._afterHide = function (ef) {
	var n = ef.element;
	if (n) {
		zk._hideExtr(n); //hide parent later
		--anima.count;
		rmZKAttr(n, "animating");
		zk._doAnique(n.id);
	}
};
anima._afterHide0 = function (ef) {
	var n = ef.effects[0].element;
	if (n) {
		zk._hideExtr(n); //hide parent later
		--anima.count;
		rmZKAttr(n, "animating");
		zk._doAnique(n.id);
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