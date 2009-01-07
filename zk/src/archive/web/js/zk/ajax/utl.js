/* util.js

	Purpose:
		
	Description:
		
	History:
		Tue Sep 30 09:02:06     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zUtl = { //static methods
	//HTML/XML
	/** Encodes a message into a valid XML format. */
	encodeXML: function (txt, multiline) {
		var out = "";
		if (!txt) return out;

		var k = 0, tl = txt.length;
		for (var j = 0; j < tl; ++j) {
			var cc = txt.charAt(j);
			if (cc == '\n') {
				if (multiline) {
					out += txt.substring(k, j) + "<br/>\n";
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

	/** Returns the element's value (by catenate all CDATA and text).
	 */
	getElementValue: function (el) {
		var txt = ""
		for (el = el.firstChild; el; el = el.nextSibling)
			if (el.data) txt += el.data;
		return txt;
	},

	/** The same as ' cellpadding="0" cellspacing="0"'. */
 	cellps0: ' cellpadding="0" cellspacing="0" border="0"',
 	/** The same as '<img style="height:0;width:0"/>'. */
 	img0: '<img style="height:0;width:0"/>',
 
	/** Returns the current time (new Date().getTime()).
	 * It is a number starting from 01/01/1970.
	 */
	now: function () {
		return new Date().getTime();
	},
	/** Returns whether the first argument is the same, or an ancestor
	 * of the second argument.
	 * <p>It assumes the second argument has either a method called getParent
	 * or a property called parent, that refer to
	 * its parent (or null if it has no parent).
	 * <p>If p is null, it is always return true;
	 */
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
	/** Shows the progress box to notify user ZK Client is busy.
	 * @see zk.startProcessing
	 */
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

		if (mask) n.z_mask = new zk.eff.FullMask({mask: zDom.$(idmsk)});

		n = zDom.$(idtxt);
		if (mask && n) { //center
			n.style.left = (zDom.innerWidth() - n.offsetWidth) / 2 + x + "px";
			n.style.top = (zDom.innerHeight() - n.offsetHeight) / 2 + y + "px";
		}
		n.style.visibility = "visible";
	},
	/** Removes all progress boxed of the specified ID. */
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
	/** Go to the specified uri.
	 * @param overwrite whether to overwrite the history
	 * @param target the target frame (ignored if overwrite is true
	 */
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

	/** Instantiates an Ajax request. */
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
