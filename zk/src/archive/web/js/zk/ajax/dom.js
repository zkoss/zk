/* dom.js

	Purpose:
	
	Description:
	
	History:
		Mon Sep 29 17:17:32	 2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

Some of the codes are adopted from http://prototype.conio.net and http://script.aculo.us

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
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
	scrollTo: function (n) {
		n = zDom.$(n);
		var pos = zDom.cmOffset(n);
		scrollTo(pos[0], pos[1]);
		return n;
	},

	/** Returns the cumulative offset of the specified element.
	 */
	cmOffset: function (n) {
		n = zDom.$(n);
		var valueT = 0, valueL = 0, operaBug, el = n.parentNode;
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
			if (zDom.getStyle(n, "position") == 'fixed') {
				valueT += zk.innerY() + n.offsetTop;
				valueL += zk.innerX() + n.offsetLeft;
				break;
			} else {
				//Fix opera bug. If the parent of "INPUT" or "SPAN" n is "DIV" 
				// and the scrollTop of "DIV" n is more than 0, the offsetTop of "INPUT" or "SPAN" n always is wrong.
				if (zk.opera) { 
					if (operaBug && n.nodeName == "DIV" && n.scrollTop != 0)
						valueT += n.scrollTop || 0;
					operaBug = n.nodeName == "SPAN" || n.nodeName == "INPUT";
				}
				valueT += n.offsetTop || 0;
				valueL += n.offsetLeft || 0;
				//Bug 1721158: In FF, n.offsetParent is null in this case
				n = zk.gecko && n != document.body ? zDom.offsetParent(n): n.offsetParent;
			}
		} while (n);
		return [valueL, valueT];
	},
	/** Returns the offset parent.
	 */
	offsetParent: function (n) {
		n = zDom.$(n);
		if (n.offsetParent) return n.offsetParent;
		if (n == document.body) return n;

		while ((n = n.parentNode) && n != document.body)
			if (n.style && zDom.getStyle(n, 'position') != 'static') //in IE, style might not be available
				return n;

		return document.body;
	},
	/** Returns the style. In addition to n.style, it also
	 * checked CSS styles that are applicated to the specified element.
	 */
	getStyle: function(n, style) {
		n = zDom.$(n);
		if (['float','cssFloat'].contains(style))
			style = (typeof n.style.styleFloat != 'undefined' ? 'styleFloat' : 'cssFloat');
		style = style.camelize();
		var value = n.style[style];
		if (!value) {
			if (document.defaultView && document.defaultView.getComputedStyle) {
				var css = document.defaultView.getComputedStyle(n, null);
				value = css ? css[style] : null;
			} else if (n.currentStyle) {
				value = n.currentStyle[style];
			}
		}

		if (value == 'auto' && ['width','height'].contains(style) && n.getStyle('display') != 'none')
			value = n['offset'+style.capitalize()] + 'px';

		if (zk.opera && ['left', 'top', 'right', 'bottom'].contains(style)
		&& zDom.getStyle(n, 'position') == 'static') value = 'auto';

		if(style == 'opacity') {
			if(value) return parseFloat(value);
			if(value = (n.getStyle('filter') || '').match(/alpha\(opacity=(.*)\)/)
			&& value[1]) return parseFloat(value[1]) / 100;
			return 1.0;
		}
		return value == 'auto' ? null : value;
	},

	/** Sets the style.
	 * @param style a map of styles to update (String name, String value).
	*/
	setStyle: function(n, style) {
		n = zDom.$(n);
		for (var name in style) {
			var value = style[name];
			if(name == 'opacity') {
				if (value == 1) {
					value = (/gecko/.test(zk.userAgent) &&
						!/konqueror|safari|khtml/.test(zk.userAgent)) ? 0.999999 : 1.0;
					if(zk.ie)
						n.style.filter = n.getStyle('filter').replace(/alpha\([^\)]*\)/gi,'');
				} else if(value === '') {
					if(zk.ie)
						n.style.filter = n.getStyle('filter').replace(/alpha\([^\)]*\)/gi,'');
				} else {
					if(value < 0.00001) value = 0;
					if(zk.ie)
						n.style.filter = n.getStyle('filter').replace(/alpha\([^\)]*\)/gi,'') +
							'alpha(opacity='+value*100+')';
				}
			} else if(['float','cssFloat'].contains(name))
				name = (typeof n.style.styleFloat != 'undefined') ? 'styleFloat' : 'cssFloat';

			n.style[name.camelize()] = value;
		}
		return n;
	},

	/** Replaces the outer of the specified element with the HTML content.
	 * @return the new node (actually the first new node, if multiple)
	 */
	setOuterHTML: function(n, html) {
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
	/** Focus the element without looking down, and do it timeout later. */
	asyncFocus: function (id, timeout) {
		setTimeout("zDom.focus('"+id+"')", timeout > 0? timeout: 0);
			//Workaround for an IE bug: we have to set focus twice since
			//the first one might fail (even we prolong the timeout to 1 sec)
	},
	/** Focus to the specified component w/o throwing exception. */
	focus: function (n) {
		n = zDom.$(n);
		if (n && n.focus)
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

	/** Select the text of the element after the specified timeout.
	 */
	asyncSelect: function (id, timeout) {
		setTimeout("zDom.select('"+id+"')", timeout > 0? timeout: 0);
	},
	/** Select to the specified component w/o throwing exception. */
	select: function (n) {
		n = zDom.$(n);
		if (n && n.select)
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
			zk.insertAfter(df, this);
			break;
		}
	};
}
