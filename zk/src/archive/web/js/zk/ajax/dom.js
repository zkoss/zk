/* dom.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 29 17:17:32	 2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

Some of the codes are adopted from http://prototype.conio.net and http://script.aculo.us

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zDom = { //static methods
	/** Returns the DOM element with the specified ID, or null if not found.
	 * A shortcut of document.getElementById.
	 */
	$: function(id) {
		if (id && id.id) id = id.id;
		return typeof id == 'string' ?
			id ? document.getElementById(id): null: id;
			//strange but getElementById("") fails in IE7
	},
	/** Returns the tag name of the specified node. */
	tag: function (n) {
		return n && n.tagName ? n.tagName.toUpperCase(): "";
	},

	/** Returns the x coordination of the visible part. */
	innerX: function () {
		return pageXOffset
			|| document.documentElement.scrollLeft
			|| document.body.scrollLeft || 0;
	},
	/** Returns the y coordination of the visible part. */
	innerY: function () {
		return pageYOffset
			|| document.documentElement.scrollTop
			|| document.body.scrollTop || 0;
	},
	/** Returns the width of the visible part. */
	innerWidth: function () {
		return typeof innerWidth == "number" ? innerWidth:
			document.compatMode == "CSS1Compat" ?
				document.documentElement.clientWidth: document.body.clientWidth;
	},
	/** Returns the height of the visible part. */
	innerHeight: function () {
		return typeof innerHeight == "number" ? innerHeight:
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
	scrollTo: function (element) {
		element = zDom.$(element);
		var pos = zDom.cmOffset(element);
		scrollTo(pos[0], pos[1]);
		return element;
	},

	/** Returns the cumulative offset of the specified element.
	 */
	cmOffset: function (element) {
		element = zDom.$(element);
		var valueT = 0, valueL = 0, operaBug, el = element.parentNode;
		//Fix gecko difference, the offset of gecko excludes its border-width when its CSS position is relative or absolute
		if (zk.gecko) {
			while (el && el != document.body) {
				var style = zDom.getStyle(el, "position");
				if (style == "relative" || style == "absolute") {
					valueT += zk.parseInt(zDom.getStyle(el, "border-top-width"));
					valueL += zk.parseInt(zDom.getStyle(el, "border-left-width"));
				}
				el = el.offsetParent;
			}
		}

		do {
			//Bug 1577880: fix originated from http://dev.rubyonrails.org/ticket/4843
			if (zDom.getStyle(element, "position") == 'fixed') {
				valueT += zk.innerY() + element.offsetTop;
				valueL += zk.innerX() + element.offsetLeft;
				break;
			} else {
				//Fix opera bug. If the parent of "INPUT" or "SPAN" element is "DIV" 
				// and the scrollTop of "DIV" element is more than 0, the offsetTop of "INPUT" or "SPAN" element always is wrong.
				if (zk.opera) { 
					if (operaBug && element.nodeName == "DIV" && element.scrollTop != 0)
						valueT += element.scrollTop || 0;
					operaBug = element.nodeName == "SPAN" || element.nodeName == "INPUT";
				}
				valueT += element.offsetTop || 0;
				valueL += element.offsetLeft || 0;
				//Bug 1721158: In FF, element.offsetParent is null in this case
				element = zk.gecko && element != document.body ? zDom.offsetParent(element): element.offsetParent;
			}
		} while (element);
		return [valueL, valueT];
	},
	/** Returns the offset parent.
	 */
	offsetParent: function (element) {
		element = zDom.$(element);
		if (element.offsetParent) return element.offsetParent;
		if (element == document.body) return element;

		while ((element = element.parentNode) && element != document.body)
			if (element.style && zDom.getStyle(element, 'position') != 'static') //in IE, style might not be available
				return element;

		return document.body;
	},
	/** Returns the style. In addition to element.style, it also
	 * checked CSS styles that are applicated to the specified element.
	 */
	getStyle: function(element, style) {
		element = zDom.$(element);
		if (['float','cssFloat'].contains(style))
			style = (typeof element.style.styleFloat != 'undefined' ? 'styleFloat' : 'cssFloat');
		style = style.camelize();
		var value = element.style[style];
		if (!value) {
			if (document.defaultView && document.defaultView.getComputedStyle) {
				var css = document.defaultView.getComputedStyle(element, null);
				value = css ? css[style] : null;
			} else if (element.currentStyle) {
				value = element.currentStyle[style];
			}
		}
	
		if (value == 'auto' && ['width','height'].contains(style) && element.getStyle('display') != 'none')
			value = element['offset'+style.capitalize()] + 'px';
	
		if (zk.opera && ['left', 'top', 'right', 'bottom'].contains(style)
		&& zDom.getStyle(element, 'position') == 'static') value = 'auto';

		if(style == 'opacity') {
			if(value) return parseFloat(value);
			if(value = (element.getStyle('filter') || '').match(/alpha\(opacity=(.*)\)/)
			&& value[1]) return parseFloat(value[1]) / 100;
			return 1.0;
		}
		return value == 'auto' ? null : value;
	},

	/** Sets the style.
	*/
	setStyle: function(element, style) {
		element = zDom.$(element);
		for (var name in style) {
			var value = style[name];
			if(name == 'opacity') {
				if (value == 1) {
					value = (/gecko/.test(zk.userAgent) &&
						!/konqueror|safari|khtml/.test(zk.userAgent)) ? 0.999999 : 1.0;
					if(zk.ie)
						element.style.filter = element.getStyle('filter').replace(/alpha\([^\)]*\)/gi,'');
				} else if(value === '') {
					if(zk.ie)
						element.style.filter = element.getStyle('filter').replace(/alpha\([^\)]*\)/gi,'');
				} else {
					if(value < 0.00001) value = 0;
					if(zk.ie)
						element.style.filter = element.getStyle('filter').replace(/alpha\([^\)]*\)/gi,'') +
							'alpha(opacity='+value*100+')';
				}
			} else if(['float','cssFloat'].contains(name))
				name = (typeof element.style.styleFloat != 'undefined') ? 'styleFloat' : 'cssFloat';

			element.style[name.camelize()] = value;
		}
		return element;
	},

	/** Replaces the outer of the specified element with the HTML content.
	 * @return the new node (actually the first new node, if multiple)
	 */
	outerHTML: function(n, html) {
		n = zDom.$(n);
		var parent = n.parentNode, sib = n.previousSibling;

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
		else { //non-IE
			var range = document.createRange();
			range.setStartBefore(n);
			var df = range.createContextualFragment(html);
			parent.replaceChild(df, n);
		}

		if (!html) n = null;
		else if (sib) n = sib.nextSibling;
		else n = parent.firstChild;

		/* Turn it on if need to fix this limitation (about script)
		if (n && !zk.gecko && n.getElementsByTagName) {
			//ie/safari/opera doesn't run script in it, so eval manually
			var ns = n.getElementsByTagName("script");
			for (var j = 0, len = ns.length; j < len; ++j)
				eval(ns[j].text);
		}*/
		return n;
	},
	/** Detaches an element.
	 * @param n the element, or the element's ID.
	 */
	detach: function (n) {
		n = zDom.$(n);
		if (n && n.parentNode) n.parentNode.removeChild(n);
	},

	/** Listens a browser event.
	 */
	listen: function (el, evtnm, fn) {
		if (el.addEventListener)
			el.addEventListener(evtnm, fn, false);
		else /*if (el.attachEvent)*/
			el.attachEvent('on' + evtnm, fn);

		//Bug 1811352
		if ("submit" == evtnm && zDom.tag(el) == "FORM") {
			if (!el._submfns) el._submfns = [];
			el._submfns.push(fn);
		}
	},
	/** Un-listens a browser event.
	 */
	unlisten: function (el, evtnm, fn) {
		if (el.removeEventListener)
			el.removeEventListener(evtnm, fn, false);
		else if (el.detachEvent) {
			try {
				el.detachEvent('on' + evtnm, fn);
			} catch (e) {
			}
		}

		//Bug 1811352
		if ("submit" == evtnm && zDom.tag(el) == "FORM" && el._submfns)
			el._submfns.remove(fn);
	},

	/** Enables ESC (default behavior). */
	enableESC: function () {
		if (zDom._noESC) {
			zDom.unlisten(document, "keydown", zDom._noESC);
			delete zDom._noESC;
		}
		if (zDom._onErrChange) {
			window.onerror = zDom._oldOnErr;
			if (zDom._oldOnErr) delete zDom._oldOnErr;
			delete zDom._onErrChange;
		}
	},
	/** Disables ESC (so loading won't be aborted). */
	disableESC: function () {
		if (!zDom._noESC) {
			zDom._noESC = function (evt) {
				if (!evt) evt = window.event;
				if (evt.keyCode == 27) {
					if (evt.preventDefault) {
						evt.preventDefault();
						evt.stopPropagation();
					} else {
						evt.returnValue = false;
						evt.cancelBubble = true;
					}
					return false;//eat
				}
				return true;
			};
			zDom.listen(document, "keydown", zDom._noESC);

			//FUTURE: onerror not working in Safari and Opera
			//if error occurs, loading will be never ended, so try to ignore
			//we cannot use zDom.listen. reason: no way to get back msg...(FF)
			zDom._oldOnErr = window.onerror;
			zDom._onErrChange = true;
			window.onerror =
		function (msg, url, lineno) {
			//We display errors only for local class web resource
			//It is annoying to show error if google analytics's js not found
			var au = zkau.uri();
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
	},

	/** Shows the progress box to notify user ZK Client is busy.
	 * @see zk.showProcess
	 */
	progressbox: function (id, msg, mask) {
		if (mask && zk.Page.contained.length) {
			//TODO: apply a mask for each contained page
			//return;
		}

		var x = zDom.innerX(), y = zDom.innerY(),
			style = ' style="left:'+x+'px;top:'+y+'px"',
			idtxt = id + 't';
			html = '<div id="'+id+'"';
		if (mask) html += '><div id="zk_mask" class="z-modal-mask"'+style+'></div';
		html += '><div id="'+idtxt+'" class="z-loading"'+style
			+'><div class="z-loading-indicator"><img class="z-loading-icon" alt="..." src="'
			+zkau.comURI('/web/img/spacer.gif')+'"/> '
			+msg+'</div></div></div>'
		var n = document.createElement("DIV");
		document.body.appendChild(n);
		zDom.outerHTML(n, html);

		if (mask) { //center it
			n = zDom.$(idtxt);
			if (n) {
				n.style.left = (zDom.innerWidth() - n.offsetWidth) / 2 + x + "px";
				n.style.top = (zDom.innerHeight() - n.offsetHeight) / 2 + y + "px";
			}
		}
	},
	cleanAllProgress: function (id) {
		zDom.detach(id);

		//TODO: remove the mask for each contained page
	}
};

if (zk.ie) {
	zDom._tagOfHtml = function (html) {
		if (!html) return "";
	
		var j = html.indexOf('<') + 1, k = j, len = j ? html.length: 0;
		for (; k < len; ++k) {
			var cc = html.charAt(k);
			if (cc == '>' || zk.isWhitespace(cc))
				return html.substring(j, k).toUpperCase();
		}
		throw "Unknown tag in "+html;
	};
	zDom._tblNewElems = function (html) {
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
	};
}

/** Returns the ZK attribute of the specified name.
 */
function getZKAttr(el, nm) {
	//20061120:
	//1) getAttributeNS doesn't work properly to retrieve attribute back
	//2) setAttribute("z:nm", val) doesn't work in Safari
	try {
		return el && el.getAttribute ? el.getAttribute("z." + nm): null;
	} catch (e) {
		return null; //IE6: failed if el is TABLE and attribute not there
	}
}
/** Sets the ZK attribute of the specified name with the specified value.
 */
function setZKAttr(el, nm, val) {
	if (el && el.setAttribute) el.setAttribute("z." + nm, val);
}
function rmZKAttr(el, nm) {
	if (el && el.removeAttribute) el.removeAttribute("z." + nm);
	else setZKAttr(el, nm, "");
}
