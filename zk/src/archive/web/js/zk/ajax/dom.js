/* dom.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 29 17:17:32     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zkDOM = { //static methods
	/** Returns the DOM element with the specified ID. */
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
	/** Replaces the outer of the specified element with the HTML content.
	 * @return the new node (actually the first new node, if multiple)
	 */
	setOuterHTML: function(n, html) {
		n = zkDOM.$(n);
		var parent = n.parentNode, sib = n.previousSibling;

		if (zk.ie) {
			var tn = zkDOM.tag(n);
			if (tn == "TD" || tn == "TH" || tn == "TABLE" || tn == "TR"
			|| tn == "CAPTION" || tn == "TBODY" || tn == "THEAD"
			|| tn == "TFOOT" || tn == "COLGROUP" || tn == "COL") {
				var ns = zkDOM._tblNewElems(html);
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

		if (n && !zk.gecko && n.getElementsByTagName) {
			//ie/safari/opera doesn't run script in it, so eval manually
			var ns = n.getElementsByTagName("script");
			for (var j = 0, len = ns.length; j < len; ++j)
				eval(ns[j].text);
		}
		return n;
	},
	/** Detaches an element.
	 * @param n the element, or the element's ID.
	 */
	detach: function (n) {
		n = zkDOM.$(n);
		if (n && n.parentNode) n.parentNode.removeChild(n);
	}
};

if (zk.ie) {
	zkDOM._tagOfHtml = function (html) {
		if (!html) return "";
	
		var j = html.indexOf('<') + 1, k = j, len = j ? html.length: 0;
		for (; k < len; ++k) {
			var cc = html.charAt(k);
			if (cc == '>' || zk.isWhitespace(cc))
				return html.substring(j, k).toUpperCase();
		}
		throw "Unknown tag in "+html;
	};
	zkDOM._tblNewElems = function (html) {
		var level, tag = zkDOM._tagOfHtml(html);
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
			var nt = zkDOM.tag(n);
			if (nt == tag || nt != "TBODY")
				ns.push(n);
			el.removeChild(n);
		}
		return ns;
	};
}
