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
zkDom = { //static methods
	/** Returns the DOM element with the specified ID. */
	$: function(id) {
		if (id && id.id) id = id.id;
		return typeof id == 'string' ?
			id ? document.getElementById(id): null: id;
			//strange but getElementById("") fails in IE7
	},
	getParent: function (n) {
		return n.parentNode; //TODO: handle vparent
	},
	/** Returns the tag name of the specified node. */
	tag: function (n) {
		return n && n.tagName ? n.tagName.toUpperCase(): "";
	},
	/** Replaces the outer of the specified element with the HTML content.
	 * @return the new node (actually the first new node, if multiple)
	 */
	setOuterHTML: function(n, html) {
		n = zkDom.$(n);
		var parent = n.parentNode, sib = n.previousSibling;

		if (zk.ie) {
			var tn = zkDom.tag(n);
			if (tn == "TD" || tn == "TH" || tn == "TABLE" || tn == "TR"
			|| tn == "CAPTION" || tn == "TBODY" || tn == "THEAD"
			|| tn == "TFOOT" || tn == "COLGROUP" || tn == "COL") {
				var ns = zkDom._tblNewElems(html);
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
		n = zkDom.$(n);
		if (n && n.parentNode) n.parentNode.removeChild(n);
	},

	enableESC: function () {
		//TODO
	},
	disableESC: function () {
		//TODO
	},

	/** Shows the progress box to notify user ZK Client is busy.
	 */
	progressbox: function (id, msg, mask) {
		if (mask && zk.Page.contained.length) {
			//TODO: apply a mask for each contained page
			//return;
		}

		var html = '<div id="'+id+'"';
		if (mask) html += '><div id="zk_mask" class="z-modal-mask"></div';
		html += '><div class="z-loading"><div class="z-loading-indicator">'
			+'<img class="z-loading-icon" alt="..." src="'+zkCom.getUpdateURI('/web/img/spacer.gif')+'"/> '
			+msg+'</div></div></div>'
		var n = document.createElement("DIV");
		document.body.appendChild(n);
		zkDom.setOuterHTML(n, html);
	}
};

if (zk.ie) {
	zkDom._tagOfHtml = function (html) {
		if (!html) return "";
	
		var j = html.indexOf('<') + 1, k = j, len = j ? html.length: 0;
		for (; k < len; ++k) {
			var cc = html.charAt(k);
			if (cc == '>' || zk.isWhitespace(cc))
				return html.substring(j, k).toUpperCase();
		}
		throw "Unknown tag in "+html;
	};
	zkDom._tblNewElems = function (html) {
		var level, tag = zkDom._tagOfHtml(html);
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
			var nt = zkDom.tag(n);
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
