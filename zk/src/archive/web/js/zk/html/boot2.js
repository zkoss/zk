/* boot.js

{{IS_NOTE
	Purpose:
		Bootstrap JavaScript
	Description:
		
	History:
		Sun Jan 29 11:43:45     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
/** Returns the proper URI.
 * @param ignoreSessId whether not to append session ID.
 * @param modver the module version to insert into uri, or null to use zk.build.
 * Note: modver is used if uri starts with /uri
 */
zk.getUpdateURI = function (uri, ignoreSessId, modver, dtid) {
	var au = zkau.uri(dtid);
	if (!uri) return au;

	if (uri.charAt(0) != '/') uri = '/' + uri;
	if (modver && uri.length >= 5 && uri.substring(0, 5) == "/web/")
		uri = "/web/_zv" + modver + uri.substring(4);

	var j = au.lastIndexOf(';'), k = au.lastIndexOf('?');
	if (j < 0 && k < 0) return au + uri;

	if (k >= 0 && (j < 0 || k < j)) j = k;
	var prefix = au.substring(0, j);

	if (ignoreSessId)
		return prefix + uri;

	var suffix = au.substring(j);
	var l = uri.indexOf('?');
	return l >= 0 ?
		k >= 0 ?
		  prefix + uri.substring(0, l) + suffix + '&' + uri.substring(l+1):
		  prefix + uri.substring(0, l) + suffix + uri.substring(l):
		prefix + uri + suffix;
};

//-- progress --//
/** Turn on the progressing dialog after the specified timeout. */
zk.progress = function (timeout) {
	zk.progressing = true;
	if (timeout > 0) setTimeout(zk._progress, timeout);
	else zk._progress();
};
zk.progressDone = function() {
	zk.progressing = zk.progressPrompted = false;
	zk.cleanAllMask("zk_prog");
	if (zk.dbModal) zk.restoreDisabled();
};
/** Generates the progressing dialog. */
zk._progress = function () {
	if (zk.progressing && !zk.loading) {
		var n = $e("zk_showBusy");
		if (n) return;
		n = $e("zk_prog");
		if (!n) {
			var msg;
			try {msg = mesg.PLEASE_WAIT;} catch (e) {msg = "Processing...";}
				//when the first boot, mesg might not be ready
			if (zk.dbModal && !zk.booted) {zk.disableAll();}
			AU_progressbar("zk_prog", msg, !zk.booted);
			zk.progressPrompted = true;
		}
	}
};

//-- utilities --//
zk.https = function () {
	var p = location.protocol;
	return p && "https:" == p.toLowerCase();
};
/** Returns the x coordination of the visible part. */
zk.innerX = function () {
	return window.pageXOffset
		|| document.documentElement.scrollLeft
		|| document.body.scrollLeft || 0;
};
/** Returns the y coordination of the visible part. */
zk.innerY = function () {
	return window.pageYOffset
		|| document.documentElement.scrollTop
		|| document.body.scrollTop || 0;
};

zk.innerWidth = function () {
	return typeof window.innerWidth == "number" ? window.innerWidth:
		document.compatMode == "CSS1Compat" ?
			document.documentElement.clientWidth: document.body.clientWidth;
};
zk.innerHeight = function () {
	return typeof window.innerHeight == "number" ? window.innerHeight:
		document.compatMode == "CSS1Compat" ?
			document.documentElement.clientHeight: document.body.clientHeight;
};
zk.pageWidth = function () {
	var a = document.body.scrollWidth, b = document.body.offsetWidth;
	return a > b ? a: b;
};
zk.pageHeight = function () {
	var a = document.body.scrollHeight, b = document.body.offsetHeight;
	return a > b ? a: b;
};

zk._setOuterHTML = function (n, html) {
	if (n.outerHTML) n.outerHTML = html;
	else { //non-IE
		var range = document.createRange();
		range.setStartBefore(n);
		var df = range.createContextualFragment(html);
		n.parentNode.replaceChild(df, n);
	}
};

/** Pause milliseconds. */
zk.pause = function (millis) {
	if (millis) {
		var d = $now(), n;
		do {
			n = $now();
		} while (n - d < millis);
	}
};

//-- HTML/XML --//
zk.encodeXML = function (txt, multiline) {
	var out = "";
	if (txt)
		for (var j = 0, tl = txt.length; j < tl; ++j) {
			var cc = txt.charAt(j);
			switch (cc) {
			case '<': out += "&lt;"; break;
			case '>': out += "&gt;"; break;
			case '&': out += "&amp;"; break;
			case '"': out += "&quot;"; break;
			case '\n':
				if (multiline) {
					out += "<br/>";
					break;
				}
			default:
				out += cc;
			}
		}
	return out
};

//-- debug --//
/** Generates a message for debugging. */
zk.message = function () {
	var msg = "", a = arguments;
	if (a.length > 1) {
		for (var i = 0, len = a.length; i < len; i++)
			msg += "[" + a[i] + "] ";
	} else msg = arguments[0];
	zk._msg = zk._msg ? zk._msg + msg: msg;
	zk._msg +=  '\n';
	setTimeout(zk._domsg, 600);
		//for better performance and less side effect, execute later
};
zk._domsg = function () {
	if (zk._msg) {
		var console = $e("zk_msg");
		if (!console) {
			console = document.createElement("DIV");
			document.body.appendChild(console);
			var html = '<div id="zk_debugbox" class="z-debugbox" style="visibility:hidden">'
+'<table cellpadding="0" cellspacing="0" width="100%"><tr>'
+'<td width="20pt"><button onclick="zk._msgclose(this)">close</button><br/>'
+'<button onclick="$e(\'zk_msg\').value = \'\'">clear</button></td>'
+'<td><textarea id="zk_msg" style="width:99%" rows="10"></textarea></td></tr></table></div>';
			zk._setOuterHTML(console, html);
			console = $e("zk_msg");
			var d = $e("zk_debugbox");
			d.style.top = zk.innerY() + zk.innerHeight() - d.offsetHeight - 20 + "px";
			d.style.left = zk.innerX() + zk.innerWidth() - d.offsetWidth - 20 + "px";
			zk.cleanVisibility(d);
		}
		console.value = console.value + zk._msg + '\n';
		console.scrollTop = console.scrollHeight;
		zk._msg = null;
	}
};
zk._msgclose = function (n) {
	while ((n = n.parentNode) != null)
		if ($tag(n) == "DIV") {
			n.parentNode.removeChild(n);
			return;
		}
};
//FUTURE: developer could control whether to turn on/off
zk.log = zk.debug = zk.message;

/** Error message must be a popup. */
zk.error = function (msg) {
	if (!zk.booted) {
		setTimeout(function () {zk.error(msg)}, 100);
		return;
	}

	if (!zk._errcnt) zk._errcnt = 1;
	var id = "zk_err_" + zk._errcnt++;
	var box = document.createElement("DIV");
	document.body.appendChild(box);
	var html =
 '<div style="position:absolute;z-index:99000;padding:3px;left:'
+(zk.innerX()+50)+'px;top:'+(zk.innerY()+20)
+'px;width:550px;border:1px solid #963;background-color:#fc9" id="'
+id+'"><table cellpadding="2" cellspacing="2" width="100%"><tr valign="top">'
+'<td width="20pt"><button onclick="zkau.sendRedraw()">redraw</button>'
+'<button onclick="zk._msgclose(this)">close</button></td>'
+'<td style="border:1px inset">'+zk.encodeXML(msg, true) //Bug 1463668: security
+'</td></tr></table></div>';
	zk._setOuterHTML(box, html);
	box = $e(id); //we have to retrieve back

	try {
		new zDraggable(box, {
			handle: box, zindex: box.style.zIndex,
			starteffect: zk.voidf, starteffect: zk.voidf, endeffect: zk.voidf});
	} catch (e) {
	}
};
/** Closes all error box (zk.error).
 * @since 3.0.6
 */
zk.errorDismiss = function () {
	for (var j = zk._errcnt; j; --j)
		zk.remove($e("zk_err_" + j));
};
