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
// Standard //
String.prototype.startsWith = function (prefix) {
	return this.substring(0,prefix.length) == prefix;
};
String.prototype.endsWith = function (suffix) {
	return this.substring(this.length-suffix.length) == suffix;
};
String.prototype.trim = function () {
	var j = 0, k = this.length - 1;
	while (j < this.length && this.charAt(j) <= ' ')
		++j;
	while (k >= j && this.charAt(k) <= ' ')
		--k;
	return j > k ? "": this.substring(j, k + 1);
};
String.prototype.skipWhitespaces = function (j) {
	for (;j < this.length; ++j) {
		var cc = this.charAt(j);
		if (cc != ' ' && cc != '\t' && cc != '\n' && cc != '\r')
			break;
	}
	return j;
};
String.prototype.nextWhitespace = function (j) {
	for (;j < this.length; ++j) {
		var cc = this.charAt(j);
		if (cc == ' ' || cc == '\t' || cc == '\n' || cc == '\r')
			break;
	}
	return j;
};
String.prototype.skipWhitespacesBackward = function (j) {
	for (;j >= 0; --j) {
		var cc = this.charAt(j);
		if (cc != ' ' && cc != '\t' && cc != '\n' && cc != '\r')
			break;
	}
	return j;
};

/** Removes the specified object from the array if any.
 * Returns false if not found.
 */
Array.prototype.remove = function (o) {
	for (var j = 0; j < this.length; ++j) {
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
	for (var j = 0; j < this.length; ++j) {
		if (o == this[j])
			return true;
	}
	return false;
};

//
// More zk utilities (defined also in boot.js) //

/** Listen an event.
 * Why not to use prototype's Event.observe? Performance.
 */
zk.listen = function (el, evtnm, fn) {
	if (el.addEventListener)
		el.addEventListener(evtnm, fn, false);
	else /*if (el.attachEvent)*/
		el.attachEvent('on' + evtnm, fn);
};
/** Un-listen an event.
 */
zk.unlisten = function (el, evtnm, fn) {
	if (el.removeEventListener)
		el.removeEventListener(evtnm, fn, false);
	else if (el.detachEvent) {
		try {
			el.detachEvent('on' + evtnm, fn);
		} catch (e) {
		}
	}
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
		if (el.style.display == "none") {
			el.style.display = "";
			ofs = zk._oldcumofs(el);
			el.style.display = "none";
		} else {
			ofs = zk._oldcumofs(el);
		}
		return ofs;
	};
}

/** Center the specified element. */
zk.center = function (el) {
	var elwd = zk.offsetWidth(el);
	var elhgh = zk.offsetHeight(el);

	var height = zk.innerHeight();
	var width = zk.innerWidth();
	var top = zk.innerY();
	var left = zk.innerX();

	var ofs = zk.toParentOffset(el,
		left + (width - elwd) / 2, top + (height - elhgh) / 2);
	if (ofs[0] < 0) ofs[0] = 0;
	if (ofs[1] < 0) ofs[1] = 0;
	el.style.left = ofs[0] + "px"; el.style.top =  ofs[1] + "px";
};
/** Returns the width and height.
 * In additions, it fixes brwoser's bugs, so call it as soon as possible.
 */
zk.getDimension = function (el) {
	var wd = zk.offsetWidth(el), hgh;
	if (el.style.display == "none" && !wd) {
		if (el.style.left == "" || el.style.left == "auto") el.style.left = "0";
		if (el.style.top == "" || el.style.top == "auto") el.style.top = "0";
			//IE6/gecko: otherwise, cumulativeOffset is wrong
		el.style.display = "";
		wd = zk.offsetWidth(el);
		hgh = zk.offsetHeight(el);
		el.style.display = "none";
	} else {
		hgh = zk.offsetHeight(el);
	}
	return [wd, hgh];
};
/** Position a component being releted to another. */
zk.position = function (el, ref, type) {
	var refofs = zk.getDimension(el);
	var wd = refofs[0], hgh = refofs[1];

	refofs = Position.cumulativeOffset(ref);
	var x, y;
	var scx = zk.innerX(), scy = zk.innerY(),
		scmaxx = scx + zk.innerWidth(), scmaxy = scy + zk.innerHeight();

	if (type == "end_before") { //el's upper-left = ref's upper-right
		x = refofs[0] + zk.offsetWidth(ref);
		y = refofs[1];

		if (zk.ie) {
			var diff = parseInt(Element.getStyle(ref, "margin-top")||"0", 10);
			if (!isNaN(diff)) y += diff;
			diff = parseInt(Element.getStyle(ref, "margin-right")||"0", 10);
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
			var diff = parseInt(Element.getStyle(ref, "margin-bottom")||"0", 10);
			if (!isNaN(diff)) y += diff;
			diff = parseInt(Element.getStyle(ref, "margin-left")||"0", 10);
			if (!isNaN(diff)) x += diff;
		}

		if (y + hgh > scmaxy)
			y = refofs[1] - hgh;
		if (x + wd > scmaxx)
			x = scmaxx - wd;
	}

	if (x < scx) x = scx;
	if (y < scy) y = scy;
	refofs = zk.toStylePos(el, x, y);
	el.style.left = refofs[0] + "px"; el.style.top = refofs[1] + "px";
};

/** Converts to coordination related to the containing element.
 * This is useful if you need to specify el.style.left or top.
 */
zk.toParentOffset = function (el, x, y) {
	var p = Position.offsetParent(el);
	if (p) {
		var refofs = Position.positionedOffset(p);
		x -= refofs[0]; y -= refofs[1];
	}
	return [x, y];
};
/** Returns the style's coordination in [integer, integer].
 * Note: it ignores the unit and assumes px (so pt or others will be wrong)
 */
zk.getStyleOffset = function (el) {
	return [parseInt(el.style.left || '0'), parseInt(el.style.top || '0')];
};
/** Converts from absolute coordination to style's coordination.
 * It is only useful for table's cell.
 * We cannot use zk.toParentCoord, because
 * after calling Draggable, offsetParent becomes BODY but
 * style.left/top is still relevant to original offsetParent
 */
zk.toStylePos = function (el, x, y) {
	if (zk.opera) {
		//Opera:
		//1)we have to reset left/top. Or, the second call position wrong
		//test case: Tooltips and Popups
		//2)we cannot assing "", either
		//test case: menu
		el.style.left = el.style.top = "0";
	} else {
		//IE/gecko fix: otherwise, toStylePos won't correct
		var fixleft = el.style.left == "" || el.style.left == "auto";
		if (fixleft) el.style.left = "0";
		var fixtop = el.style.top == "" || el.style.top == "auto";
		if (fixtop) el.style.top = "0";
	}

	var ofs1 = Position.cumulativeOffset(el);
	var ofs2 = zk.getStyleOffset(el);
	ofs1 = [x - ofs1[0] + ofs2[0], y  - ofs1[1] + ofs2[1]];

	if (fixleft) el.style.left = "";
	if (fixtop) el.style.top = "";
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

/** Whether an element is visible. */
zk.isVisible = function (el) {
	return el && el.style && el.style.display != "none";
};
/** Whether an element is really visible. */
zk.isRealVisible = function (e) {
	if (!e) return false;
	do {
		if (e.style && e.style.display == "none") return false;
		//note: document is the top parent and has NO style
	} while (e = e.parentNode);
	return true;
};

/** Focus the specified element and any of its child. */
zk.focusDown = function (el) {
	return zk._focusDown(el, new Array("INPUT", "SELECT", "BUTTON"), true)
		|| zk._focusDown(el, new Array("A"), false);
};
/** checkA whether to check the A tag specially (i.e., focus if one ancestor
 * has z:type).
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
			for (var n = el; (n = n.parentNode) != null;) {
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
zk.focusDownById = function (id, timeout) {
	var script = "if (!zk.focusDown($e('"+id+"'))) window.focus()";
	zk._doTwice(script, timeout);
};
/** Focus the element without looking down, and do it timeout later. */
zk.focusById = function (id, timeout) {
	var script = "zk._focus($e('"+id+"'))";
	zk._doTwice(script, timeout);
};
zk._focus = function (cmp) {
	if (cmp && cmp.focus) try {cmp.focus();} catch (e) {}
		//IE throws exception when focus in some cases
};
/** Select the text of the element, and do it timeout later. */
zk.selectById = function (id, timeout) {
	var script = "zk._select($e('"+id+"'))";
	zk._doTwice(script, timeout);
};
zk._select = function (cmp) {
	if (cmp && cmp.select) try {cmp.select();} catch (e) {}
		//IE throws exception when focus() in some cases
};
zk._doTwice = function (script, timeout) {
	if (!timeout) timeout = 0;
	setTimeout(script, timeout);
	setTimeout(script, timeout);
		//Workaround for an IE bug: we have to set focus twice since
		//the first one might fail (even we prolong the timeout to 1 sec)
};

/** Inserts an unparsed HTML immediately before the specified element.
 * @param el the sibling before which to insert
 */
zk.insertHTMLBefore = function (el, html) {
	if (zk.ie || zk.opera) {
		switch ($tag(el)) { //exclude TABLE
		case "TD": case "TH": case "TR": case "CAPTION":
		case "TBODY": case "THEAD": case "TFOOT":
			var n = document.createElement(zk.tagOfHtml(html));
			el.parentNode.insertBefore(n, el);
			zk._tdfixReplaceOuterHTML(n, html);
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
		case "TBODY": case "THEAD": case "TFOOT":
		/*case "TH": case "TD": case "CAPTION":*/ //no need to handle them
			var tn2 = zk.tagOfHtml(html);
			if (tn == "TABLE" && tn2 == "TR") {
				var bd = el.tBodies;
				if (!bd || !bd.length) {
					bd = document.createElement("TBODY");
					el.appendChild(bd);
					el = bd;
				} else {
					el = bd[0];
				}
			}
			var n = document.createElement(tn2);
			el.appendChild(n);
			zk._tdfixReplaceOuterHTML(n, html);
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
			var sib = el.nextSibling;
			if (sib != null) {
				zk.insertHTMLBefore(sib, html);
			} else {
				var n = document.createElement(zk.tagOfHtml(html));
				el.parentNode.appendChild(n);
				zk._tdfixReplaceOuterHTML(n, html);
			}
			return;
		}
	}
	el.insertAdjacentHTML('afterEnd', html);
};

/** Sets the inner HTML.
 */
zk.setInnerHTML = function (el, html) {
	if (zk.ie || zk.opera) {
		zk._tdfixReplaceInnerHTML(el, html);
	} else {
		el.innerHTML = html;
	}
};
/** Sets the outer HTML.
 */
zk.setOuterHTML = function (el, html) {
	//NOTE: Safari doesn't support __defineSetter__
	if (zk.ie || zk.opera) {
		var tn = $tag(el);
		if (tn == "TD" || tn == "TH" || tn == "TABLE" || tn == "TR"
		|| tn == "CAPTION" || tn == "TBODY" || tn == "THEAD"
		|| tn == "TFOOT") {
			zk._tdfixReplaceOuterHTML(el, html);
			return;
		}
		el.outerHTML = html;
	} else {
		var r = el.ownerDocument.createRange();
		r.setStartBefore(el);
		var df = r.createContextualFragment(html);
		el.parentNode.replaceChild(df, el);
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
 */
zk.parentNode = function (el, tagName) {
	while (el && (el = el.parentNode) != null && $tag(el) != tagName)
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

/** Returns whether a node is an ancestor of another (including itself). */
zk.isAncestor = function(p, c) {
	while (c) {
		if (p == c)
			return true;

		//To resolve Bug 1486840 (see db.js and cb.js)
		if (zk.gecko) { 
			var n = $e(getZKAttr(c, "vparent"));
			if (n) {
				c = n;
				continue;
			}
		}
		c = c.parentNode;
	}
	return false;
};

/** Returns the enclosing tag for the specified HTML codes.
 */
zk.tagOfHtml = function (html) {
	if (!html) return "";

	var j = html.indexOf('>'), k = html.lastIndexOf('<');
	if (j < 0 || k < 0) {
		alert("Unknown tag: "+html);
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
//IE don't support TABLE/TR/TD well.

//20061109: Tom M. Yeh:
//Browser: Opera:
//Issue: When append TD with insertAdjacentHTML, it creates extra sibling,
//TextNode.
//Test: Live data of listbox in ZkDEMO
if (zk.ie || zk.opera) {
	/** Replace HTML for TR, TD and others; the same as outerHTML
	 */
	zk._tdfixReplaceOuterHTML = function (el, html) { //patch for IE
		var j = html.indexOf('>');
		if (j < 0) {
			alert("Unsupported replace: "+html);
			return;
		}

		var head, inner, k;
		for (k = j; --k >= 0;) {
			var cc = html.charAt(k);
			if (cc == ' ' || cc == '\t' || cc == '\n') continue;
			if (cc == '/') {
				head = html.substring(0, k);
				inner = "";
			}
			break;
		}
		if (!head) {
			head = html.substring(0, j);
			k = html.lastIndexOf('<');
			inner = k > j ? html.substring(j + 1, k): "";
		}

		//replace attributes
		//Potential bug: we don't remove attributes not found in html
		//It is enough for now but we might need to do it in FUTURE
		j = head.indexOf('<') + 1;
		j = head.skipWhitespaces(j);
		k = head.nextWhitespace(j);
		var tag = head.substring(j, k).toUpperCase();
		if ($tag(el) != tag) {
			alert("Unsupported replace: different tags: old="+el.tagName+", new="+tag);
			return;
		}

		for (;;) {
			j = k;
			j = head.skipWhitespaces(j);
			if (j >= head.length) break; //done
			k = head.indexOf('=', j);
			if (k < 0) {
				alert("Unsupported: attribute must have a value:\n"+head)
				return;
			}
			var attr = head.substring(j, head.skipWhitespacesBackward(k))
				.toLowerCase();
			var val;
			j = head.skipWhitespaces(k + 1);
			if (head.charAt(j) == '"') {
				k = head.indexOf('"', ++j);
				if (k < 0) k = head.length;
				val = head.substring(j, k);
				++k;
			} else {
				k = head.nextWhitespace(j);
				val = head.substring(j, k);
			}

			switch (attr) {
			case "id": el.id = val; break;
			case "class": el.className = val; break;
			case "style": zk.setStyle(el, val); break;
			case "onclick":
			case "ondblclick":
			case "onkeydown":
			case "onkeypress":
			case "onkeyup":
			case "onmousedown":
			case "onmousemove":
			case "onmouseout":
			case "onmouseover":
			case "onmouseup":
				el[attr] = new Function(val); break;
			case "colspan": el.colSpan = val; break; //IE bug
			case "rowspan": el.rowSpan = val; break; //IE bug
			case "cellpadding": el.cellPadding = val; break; //IE bug
			case "cellspacing": el.cellSpacing = val; break; //IE bug
			case "valign": el.vAlign = val; break; //IE bug
			default: el.setAttribute(attr, val);
			}
		}

		if (inner)
			zk._tdfixReplaceInnerHTML(el, inner);
	};
	/** Replace HTML for TR, TD and others; the same as outerHTML, used
	 * since IE don't support non-SPAN/DIV well.
	 */
	zk._tdfixReplaceInnerHTML = function (el, html) { //patch for IE
		//replace inner
		var tn = $tag(el);
		if (tn == "TR" || tn == "TABLE" || tn == "TBODY" || tn == "THEAD"
		|| tn == "TFOOT") { //ignore TD/TH/CAPTION
			while (el.firstChild)
				el.removeChild(el.firstChild);

			if (tn == "TABLE") {
				var tagInfo = zk._tdfixNextTag(html, 0);
				if (tagInfo && tagInfo.tagName == "TR") {
					var n = document.createElement("TBODY");
					el.appendChild(n);
					el = n;
				}
			}

			for (var j = 0, depth = 0; j < html.length;) {
				var tagInfo = zk._tdfixNextTag(html, j);
				if (!tagInfo) return;

				var tagnm = tagInfo.tagName;
				var n = document.createElement(tagnm);
				el.appendChild(n);

				var k = html.indexOf('>', tagInfo.index);
				for (var depth = 0; k >= 0;) {
					tagInfo = zk._tdfixNextTag(html, k + 1);
					if (!tagInfo) break;
					k = html.indexOf('>', tagInfo.index);
					if (tagnm == tagInfo.tagName) {
						++depth;
					} else if ("/" + tagnm == tagInfo.tagName) {
						if (--depth < 0)
							break;
					}
				}

				if (k < 0) k = html.length;
				else ++k;
				zk._tdfixReplaceOuterHTML(n, html.substring(j, k));
				j = k;
			}
		} else {
			el.innerHTML = html;
				//10192006:No longer handles IE's bug (see Bug 1455584)
				//Reason: invalidate(Range) is removed, so no much chance to
				//get here
		}
	};
	/** Next tag info. */
	zk._tdfixNextTag = function (html, j) {
		var k = html.indexOf('<', j);
		if (k < 0) return null;

		var l = html.skipWhitespaces(k + 1);
		var tagnm = "";
		if (html.charAt(l) == '/') {
			tagnm = "/";
			l = html.skipWhitespaces(l + 1);
		}

		for (;; ++l) {
			if (l >= html.length) return null; //ignore it
			var cc = html.charAt(l);
			if ((cc < 'a' || cc > 'z') && (cc < 'A' || cc > 'Z'))
				break;
			tagnm += cc;
		}
		return {tagName: tagnm.toUpperCase(), index: l};
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
			if (this.nextSibling)
				this.parentNode.insertBefore(df, this.nextSibling);
			else
				this.parentNode.appendChild(df);
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
	var j = url.lastIndexOf('.'),
		k = url.lastIndexOf('-'),
		m = url.lastIndexOf('/');
	var ext = j <= m ? "": url.substring(j);
	var pref = k <= m ? j <= m ? url: url.substring(0, j): url.substring(0, k);
	if (type) type = "-" + type;
	else type = "";
	return pref + type + ext;
};
/** Rename between / and .
 */
zk.rename = function (url, name) {
	var j = url.lastIndexOf('.'),
		k = url.lastIndexOf('/');
	var ext = j <= k ? "": url.substring(j);
	return url.substring(0, k + 1) + name + ext;
};

//-- special routines --//
if (!zk.activeTagnames) {
	zk.activeTagnames =
		new Array("A","BUTTON","TEXTAREA","INPUT","SELECT","IFRAME","APPLET");
	zk._disTags = new Array(); //A list of {element: xx, what: xx}
	zk._hidCvred = new Array(); //A list of {element: xx, visibility: xx}

	zk.coveredTagnames = new Array("IFRAME","APPLET"); //Bug 1562239 
	if (zk.ie)
		zk.coveredTagnames.unshift("SELECT");
}

/** Disables all active tags. */
zk.disableAll = function (parent) {
	for (var j = 0; j < zk.activeTagnames.length; j++) {
		var els = document.getElementsByTagName(zk.activeTagnames[j]);
		l_els:
		for (var k = 0 ; k < els.length; k++) {
			var el = els[k];
			if (zk.isAncestor(parent, el))
				continue;
			for(var m = 0; m < zk._disTags.length; ++m) {
				var info = zk._disTags[m];
				if (info.element == el)
					continue l_els;
			}

			if (zk._disTags.contains(el))
				continue;

			var what;
			var tn = $tag(el);
			if (tn == "IFRAME" || tn == "APPLET" || (zk.ie && tn == "SELECT")) {
	//Note: we don't check isOverlapped because mask always covers it
				what = el.style.visibility;
				el.style.visibility = "hidden";
			} else if (!zk.ie && tn == "A") {
	//Firefox doesn't support the disable of A
				what = "h:" + zkau.getStamp(el, "href") + ":" + el.href;
				el.href = "";
			} else {
				what = "d:" + zkau.getStamp(el, "disabled") + ":" + el.disabled;
				el.disabled = true;
			}
			zk._disTags.push({element: el, what: what});
		}
	}
};
/** Restores tags being disabled by previous disableAll. If el is not null,
 * only el's children are enabled
 */
zk.restoreDisabled = function (n) {
	var skipped = new Array();
	for (var bug1498895 = zk.ie;;) {
		var info = zk._disTags.shift();
		if (!info) break;

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
					el.href = what.substring(j + 1);
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
		for (;;) {
			var info = zk._hidCvred.shift();
			if (!info) break;

			if (info.element.style)
				info.element.style.visibility = info.visibility;
		}
		return;
	}

	for (var j = 0; j < zk.coveredTagnames.length; ++j) {
		var els = document.getElementsByTagName(zk.coveredTagnames[j]);
		loop_els:
		for (var k = 0 ; k < els.length; k++) {
			var el = els[k];
			if (!zk.isRealVisible(el)) continue;

			for (var m = 0; m < ary.length; ++m) {
				if (zk.isAncestor(ary[m], el))
					continue loop_els;
			}

			var overlapped = false;
			for (var m = 0; m < ary.length; ++m) {
				if (zk.isOverlapped(ary[m], el)) {
					overlapped = true;
					break;
				}
			}

			if (overlapped) {
				for (var m = 0; m < zk._hidCvred.length; ++m) {
					if (el == zk._hidCvred[m].element)
						continue loop_els;
				}
				zk._hidCvred
					.push({element: el, visibility: el.style.visibility});
				el.style.visibility = "hidden";
			} else {
				for (var m = 0; m < zk._hidCvred.length; ++m) {
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
	var bknm = "zk_bk" + nm;
	var val = el.getAttribute(bknm);
	if (val) {
		el.removeAttribute(bknm);
		el.style[nm] = val == "_zk_none_" ? "": val;
	}
};

/** Scroll inner into visible, assuming outer has a scrollbar. */
zk.scrollIntoView = function (outer, inner) {
	if (outer && inner) {
		var padding = Element.getStyle(inner, "padding-top");
		padding = padding ? parseInt(padding, 10): 0;
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
	if (overwrite) {
		document.location.replace(url);
	} else {
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
		if (target) frm.target = target;
		if (url && !zk.isNewWindow(url, target)) zk.progress();
		frm.submit();
	}
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

		var inp = document.createElement("INPUT");
		inp.type = "hidden";
		frm.appendChild(inp);

		if (k < 0 || (k > l && l >= 0)) { //no value part
			inp.name = l >= 0 ? qs.substring(j, l): qs.substring(j);
			inp.value = "";
		} else {
			inp.name = qs.substring(j, k);
			inp.value = l >= 0 ? qs.substring(k + 1, l): qs.substring(k + 1);
		}

		if (l < 0) return; //done
		j = l + 1;
	}
}

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
zk.cpCellWidth = function (dst, srcrows) {
	if (dst == null || srcrows == null || !srcrows.length
	|| !dst.cells || !dst.cells.length)
		return;

	var ncols = dst.cells.length; //TODO: handle colspan for dst: ncols = zk.ncols(dst.cells);
	var src, maxnc = 0;
	for (var j = 0; j < srcrows.length; ++j) {
		var row = srcrows[j];
		var cells = row.cells;
		var nc = zk.ncols(cells);
		var valid = cells.length == nc && row.display != "none";
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

	for (var j = maxnc; --j >= 0;) {
		var d = dst.cells[j], s = src.cells[j];
		d.style.width = s.offsetWidth + "px";
		var v = s.offsetWidth - d.offsetWidth;
		if (v != 0) {
			v += s.offsetWidth;
			if (v < 0) v = 0;
			d.style.width = v + "px";
		}
	}

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
	var y = val.getFullYear(), m = val.getMonth(), d = val.getDate();

	var ts = txt.split(/\W+/);
	for (var i = 0, j = 0; j < fmt.length; ++j) {
		var cc = fmt.charAt(j);
		if (cc == 'y' || cc == 'M' || cc == 'd' || cc == 'E') {
			var len = 1;
			for (var k = j; ++k < fmt.length; ++len)
				if (fmt.charAt(k) != cc)
					break;

			var nosep; //no separator
			if (k < fmt.length) {
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
				y = parseInt(token, 10);
				if (isNaN(y)) return null; //failed
				if (y < 100) y += y > 29 ? 1900 : 2000;
				break;
			case 'M':
				if (len <= 2) {
					if (nosep && token.length > 2) {
						ts[--i] = token.substring(2);
						token = token.substring(0, 2);
					}
					m = parseInt(token, 10) - 1;
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
				d = parseInt(token, 10);
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
	for (var j = 0; j < fmt.length; ++j) {
		var cc = fmt.charAt(j);
		if (cc == 'y' || cc == 'M' || cc == 'd' || cc == 'E') {
			var len = 1;
			for (var k = j; ++k < fmt.length; ++len)
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
	return parseInt(el.getAttribute(nm) || "0", 10);
};
