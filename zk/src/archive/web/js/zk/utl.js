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
 * The basic utilties.
 * <p>For more utilities, refer to {@link zk.xml.Utl}.
 */
zUtl = { //static methods
	//Character
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
	 * Refer to {@link zk.xml.Utl} for more XML utilities.
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

 	cellps0: ' cellpadding="0" cellspacing="0" border="0"',
 	img0: '<img style="height:0;width:0"/>',
 	i0: '<i style="height:0;width:0"/>',
 
	now: function () {
		return new Date().getTime();
	},
	today: function () {
		var d = new Date();
		return new Date(d.getFullYear(), d.getMonth(), d.getDate());
	},
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
	destroyProgressbox: function (id) {
		var $n = jq(id, zk), n;
		if ($n.length) {
			if (n = $n[0].z_mask) n.destroy();
			$n.remove();
		}

		for (var c = zk.Page.contained.length, e = zk.Page.contained[--c]; e; e = zk.Page.contained[--c])
			if (e._applyMask) {
				e._applyMask.destroy();
				e._applyMask = null;
			}
	},

	//HTTP//
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
	mapToString: function (map, assigner, separator) {
		assigner = assigner || '=';
		separator = separator || ' ';
		var out = [];
		for (var v in map)
			out.push(separator, v, assigner, map[v]);
		out[0] = '';
		return out.join('');
	}
};

zk._t0 = zk._t1 = zUtl.now(); //_t0 used by zk.js and _t1 by mount.js
})();
