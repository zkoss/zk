/* util.js

	Purpose:
		
	Description:
		
	History:
		Tue Sep 30 09:02:06     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _decs = {lt: '<', gt: '>', amp: '&', quot: '"'},
		_encs = {};
	for (var v in _decs)
		_encs[_decs[v]] = v;

	function _pathname(url) {
		var j = url.indexOf("//");
		if (j > 0) {
			j = url.indexOf("/", j + 2);
			if (j > 0) return url.substring(j);
		}
	}

	/* Creates HIDDEN elements based on the specified query string
	 * @param DOMElement parent the parent node (required).
	 * @param String qs the query string
	 */
	function _queryToHiddens(parent, qs) {
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
			jq.newHidden(nm, val, parent);
	
			if (l < 0) return; //done
			j = l + 1;
		}
	}

/** @class zUtl
 * @import zk.Widget
 * @import zk.xml.Utl
 * The basic utilties.
 * <p>For more utilities, refer to {@link Utl}.
 */
zUtl = { //static methods
	//Character
    /**
     * Returns whether the character is according to its opts.
     * @param char cc the character
     * @param Map opts the options.
<table border="1" cellspacing="0" width="100%">
<caption> Allowed Options
</caption>
<tr>
<th> Name
</th><th> Allowed Values
</th><th> Description
</th></tr>
<tr>
<td> digit
</td><td> true, false
</td><td> Specifies the character is digit only.
</td></tr>
<tr>
<td> upper
</td><td> true, false
</td><td> Specifies the character is upper case only.
</td></tr>
<tr>
<td> lower
</td><td> true, false
</td><td> Specifies the character is lower case only.
</td></tr>
<tr>
<td> whitespace
</td><td> true, false
</td><td> Specifies the character is whitespace only.
</td></tr>
<tr>
<td> opts[cc]
</td><td> true, false
</td><td> Specifies the character is allowed only.
</td></tr>
</table>
     * @return boolean
     */
	isChar: function (cc, opts) {
		return (opts.digit && cc >= '0' && cc <= '9')
			|| (opts.upper && cc >= 'A' && cc <= 'Z')
			|| (opts.lower && cc >= 'a' && cc <= 'z')
			|| (opts.whitespace && (cc == ' ' || cc == '\t' || cc == '\n' || cc == '\r'))
			|| opts[cc];
	},

	//HTML/XML
	/** Parses the specifie text into a map.
	 * For example
	 *<pre><code>
zUtl.parseMap("a=b,c=d");
zUtl.parseMap("a='b c',c=de", ',', "'\"");
</code></pre>
	 * @param String text the text to parse
	 * @param String separator the separator. If omitted, <code>','</code>
	 * is assumed
	 * @param String quote the quote to handle. Ignored if omitted.
	 * @return Map the map
	 */
	parseMap: function (text, separator, quote) {
		var map = {};
		if (text) {
			var ps = text.split(separator || ',');
			if (quote) {
				var tmp = [],
					re = new RegExp(quote, 'g'),
					key = '', t, pair;
				while((t = ps.shift()) !== undefined) {
					if ((pair = (key += t).match(re)) && pair.length != 1) {
						if (key)
							tmp.push(key);
						key = '';
					} else
						key += separator;
				}
				ps = tmp;
			}
			for (var len = ps.length; len--;) {
				var key = ps[len].trim(),
					index = key.indexOf('=');
				if (index != -1)
					map[key.substring(0, index)] = key.substring(index + 1, key.length).trim();
			}
		}
		return map;
	},

	/** Encodes the string to a valid XML string.
	 * Refer to {@link Utl} for more XML utilities.
	 * @param String txt the text to encode
	 * @param Map opts [optional] the options. Allowd value:
	 * <ul>
	 * <li>pre - whether to replace whitespace with &amp;nbsp;</li>
	 * <li>multiline - whether to replace linefeed with &lt;br/&gt;</li>
	 * <li>maxlength - the maximal allowed length of the text</li>
	 * </ul>
	 * @return String the encoded text.
	 */
	encodeXML: function (txt, opts) {
		var out = "";
		txt = txt != null ? String(txt):'';
		var k = 0, tl = txt.length,
			pre = opts && opts.pre,
			multiline = pre || (opts && opts.multiline),
			maxlength = opts ? opts.maxlength : 0;

		if (!multiline && maxlength && tl > maxlength) {
			var j = maxlength;
			while (j > 0 && txt.charAt(j - 1) == ' ')
				--j;
			return txt.substring(0, j) + '...';
		}

		for (var j = 0; j < tl; ++j) {
			var cc = txt.charAt(j);
			if (cc == '\n') {
				if (multiline) {
					out += txt.substring(k, j) + "<br/>\n";
					k = j + 1;
				}
			} else if (cc == ' ' || cc == '\t') {
				if (pre) {
					out += txt.substring(k, j) + '&nbsp;';
					if (cc == '\t') out += '&nbsp;&nbsp;&nbsp;';
					k = j + 1;
				}
			} else {
				var enc = _encs[cc];
				if (enc) {
					out += txt.substring(k, j) + '&' + enc + ';';
					k = j + 1;
				}
			}
		}
		return !k ? txt:
			k < tl ? out + txt.substring(k): out;
	},
	/** Decodes the XML string into a normal string.
	 * For example, &amp;lt; is convert to &lt;
	 * @param String txt the text to decode
	 * @return String the decoded string
	 */
	decodeXML: function (txt) {
		var out = "";
		if (!txt) return out;

		var k = 0, tl = txt.length;
		for (var j = 0; j < tl; ++j) {
			var cc = txt.charAt(j);
			if (cc == '&') {
				var l = txt.indexOf(';', j + 1);
				if (l >= 0) {
					var dec = txt.charAt(j + 1) == '#' ?
						String.fromCharCode(
							parseInt(txt.substring(
								txt.charAt(j + 2) == '0' ? j + 3: j + 2, l))):
						_decs[txt.substring(j + 1, l)];
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

	/** A shortcut of <code>' cellpadding="0" cellspacing="0" border="0"'</code>.
	 * @type String
	 */
 	cellps0: ' cellpadding="0" cellspacing="0" border="0"',
 	/** A shortcut of <code>'&lt;img style="height:0;width:0"/&gt;'</code>.
 	 * @type String
 	 */
 	img0: '<img style="height:0;width:0"/>',
 	/** A shortcut of <code>'&lt;i style="height:0;width:0"/&gt;'</code>.
 	 * @type String
 	 */
 	i0: '<i style="height:0;width:0"/>',
 
 	/** Returns a long value representing the current time (unit: miliseconds).
 	 * @return long
 	 */
	now: function () {
		return new Date().getTime();
	},
	/** Returns today (at 0:0AM).
	 * @param boolean full if true, returns the full time, else only returns year, month
	 * 		, and date
	 * @return Date
	 */
	today: function (full) {
		var d = new Date();
		return full ? new Date(d.getFullYear() + zk.YDELTA, d.getMonth(), d.getDate(),
				d.getHours(), d.getMinutes(), d.getMilliseconds())
			: new Date(d.getFullYear() + zk.YDELTA, d.getMonth(), d.getDate());
	},

	/** Returns if one is ancestor of the other.
	 * It assumes the object has either a method called <code>getParent</code>
	 * or a field called <code>parent</code>.
	 * A typical example is used to test the widgets ({@link Widget}).
	 * @param Object p the parent. This method return true if p is null
	 or p is the same as c
	 * @param Object c the child
	 * @return boolean
	 */
	isAncestor: function (p, c) {
		if (!p) return true;
		for (; c; c = c.getParent ? c.getParent(): c.parent)
			if (p == c)
				return true;
		return false;
	},

	//progress//
	/** Creates a message box to indicate something is being processed
	 * @param String id the ID of the DOM element being created
	 * @param String msg the message to shown
	 * @param boolean mask whether to show sem-transparent mask to prevent
	 * the user from accessing it.
	 * @param String icon the CSS class used to shown an icon in the box.
	 * Ignored if not specified.
	 * @see #destroyProgressbox
	 */
	progressbox: function (id, msg, mask, icon) {
		if (mask && zk.Page.contained.length) {
			for (var c = zk.Page.contained.length, e = zk.Page.contained[--c]; e; e = zk.Page.contained[--c]) {
				if (!e._applyMask)
					e._applyMask = new zk.eff.Mask({
						id: e.uuid + "-mask",
						message: msg,
						anchor: e.$n()
					});
			}
			return;
		}

		if (mask)
			zk.isBusy++;

		var x = jq.innerX(), y = jq.innerY(),
			style = ' style="left:'+x+'px;top:'+y+'px"',
			idtxt = id + '-t',
			idmsk = id + '-m',
			html = '<div id="'+id+'"';
		if (mask)
			html += '><div id="' + idmsk + '" class="z-modal-mask"'+style+'></div';
		html += '><div id="'+idtxt+'" class="z-loading"'+style
			+'><div class="z-loading-indicator"><span class="z-loading-icon"></span> '
			+msg+'</div></div>';
		if (icon)
			html += '<div class="' + icon + '"></div>';
		jq(document.body).append(html + '</div>');
		var $n = jq(id, zk),
			n = $n[0],
			$txt = jq(idtxt, zk);
		if (mask)
			n.z_mask = new zk.eff.FullMask({
				mask: jq(idmsk, zk)[0],
				zIndex: $txt.css('z-index') - 1
			});

		if (mask && $txt.length) { //center
			var txt = $txt[0],
				st = txt.style;
			st.left = jq.px((jq.innerWidth() - txt.offsetWidth) / 2 + x);
			st.top = jq.px((jq.innerHeight() - txt.offsetHeight) / 2 + y);
		}

		$n.zk.cleanVisibility();
	},
	/** Removes the message box created by {@link #progressbox}.
	 * @param String id the ID of the DOM element of the message box
	 */
	destroyProgressbox: function (id) {
		var $n = jq(id, zk), n;
		if ($n.length) {
			if (n = $n[0].z_mask) n.destroy();
			$n.remove();
			if (--zk.isBusy < 0)
				zk.isBusy = 0;
		}

		for (var c = zk.Page.contained.length, e = zk.Page.contained[--c]; e; e = zk.Page.contained[--c])
			if (e._applyMask) {
				e._applyMask.destroy();
				e._applyMask = null;
			}
	},

	//HTTP//
	/** Navigates to the specified URL.
	 * @param String url the URL to go to
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>target - the name of the target browser window. The same browswer
	 * window is assumed if omitted. You can use any value allowed in
	 * the target attribute of the HTML FORM tag, such as _self, _blank,
	 * _parent and _top.</li>
	 * <li>overwrite - whether load a new page in the current browser window.
	 * If true, the new page replaces the previous page's position in the history list.</li>
	 * <li>reload - whether to enforce the reload.
	 * It is used if the new URL is the same as the current one except
	 * the bookmark (<code>#xxx</code>).</li>
	 * </ul>
	 */
	go: function (url, opts) {
		opts = opts || {};
		if (opts.target) {
			//we have to process query string because browser won't do it
			//even if we use jq().append("<form...")
			try {
				var frm = document.createElement("FORM");
				document.body.appendChild(frm);
				var j = url.indexOf('?');
				if (j > 0) {
					var qs = url.substring(j + 1);
					url = url.substring(0, j);
					_queryToHiddens(frm, qs);
				}
				frm.name = "go";
				frm.action = url;
				frm.method = "GET";
				frm.target = opts.target;
				frm.submit();
			} catch (e) { //happens if popup block
			}
		} else if (opts.overwrite) {
			location.replace(url ? url: location.href);
		} else {
			if (url) {
				location.href = url;

				if (!opts.reload)
					return;

				var j = url.indexOf('#'),
					un = j >= 0 ? url.substring(0, j): url,
					pn = _pathname(location.href);
				j = pn.indexOf('#');
				if (j >= 0) pn = pn.substring(0, j);
				if (pn != un)
					return;
				//fall thru (bug 2882149)
			}
			location.reload();
		}
	},

	/** Converts an integer array to a string (separated by comma).
	 * @param int[] ary the integer array to convert.
	 * If null, an empty string is returned.
	 * @return String
	 * @see #stringToInts
	 */
	intsToString: function (ary) {
		if (!ary) return "";

		var sb = [];
		for (var j = 0, k = ary.length; j < k; ++j)
			sb.push(ary[j]);
		return sb.join();
	},
	/** Converts a string separated by comma to an array of integers.
	 * @see #intsToString
	 * @param String text the string to convert.
	 * If null, null is returned.
	 * @param int defaultValue the default value used if the value
	 * is not specified. For example, zUtl.stringToInts("1,,3", 2) returns [1, 2, 3].
	 * @return int[]
	 */
	stringToInts: function (text, defaultValue) {
		if (text == null)
			return null;

		var list = [];
		for (var j = 0;;) {
			var k = text.indexOf(',', j),
				s = (k >= 0 ? text.substring(j, k): text.substring(j)).trim();
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
	/** Converts a map to a string
	 * @see #intsToString
	 * @param Map map the map to convert
	 * @param String assign the symbol for assignment. If omitted, '=' is assumed.
	 * @param String separator the symbol for separator. If omitted, ',' is assumed.
	 * @return String
	 */
	mapToString: function (map, assign, separator) {
		assign = assign || '=';
		separator = separator || ' ';
		var out = [];
		for (var v in map)
			out.push(separator, v, assign, map[v]);
		out[0] = '';
		return out.join('');
	}
};

zk._t0 = zk._t1 = zUtl.now(); //_t0 used by zk.js and _t1 by mount.js
})();
