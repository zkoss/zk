/* boot.js

{{IS_NOTE
	$Id: boot.js,v 1.69 2006/05/26 06:41:13 tomyeh Exp $
	Purpose:
		Bootstrap JavaScript
	Description:
		
	History:
		Sun Jan 29 11:43:45     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
//
//
//zk//
function zk() {}
/** Default version used for all modules that don't define their individual
 * version.
 */
zk.build = "15"; //increase this if we want the browser to reload JavaScript
zk.mods = {};

/** Browser info. */
zk.agent = navigator.userAgent.toLowerCase();
zk.agtIe = (zk.agent.indexOf("msie") != -1) && (zk.agent.indexOf("opera") == -1);
zk.agtNav = (zk.agent.indexOf("mozilla") != -1) && (zk.agent.indexOf("spoofer") == -1)
	&& (zk.agent.indexOf("compatible") == -1) && (zk.agent.indexOf("opera") == -1)
	&& (zk.agent.indexOf("webtv") == -1) && (zk.agent.indexOf("hotjava") == -1);

/** Returns the version of the specified module name.
 */
zk.getBuild = function (nm) {
	return zk.mods[nm] || zk.build;
};

/** Adds a function that will be invoked after the document is loaded.
 */
zk.addInit = function (fn) {
	zk._initfns.push(fn);
};
/** Adds an array of components. */
zk.addInitCmps = function (cmps) {
	zk._initcmps.push(cmps);
};
/** Loads the specified module (JS). If a feature is called "a.b.c", then
 * zk_action + "/web/js" + "a/b/c.js" is loaded.
 * <p>To load a JS file directly, use zk.loadJS
 * @param nm the module name if no / is specified, or filename if / is
 * specified.
 */
zk.load = function (nm) {
	if (!nm) {
		zk.error("Module name must be specified");
		return;
	}

	if (!zk._modules[nm]) {
		zk._modules[nm] = true;
		zk._load(nm);
	}
};
/** Loads the required module for the specified component.
 * Note: it DOES NOT check any of its children.
 * @return true if zk_type is defined.
 */
zk.loadByType = function (n) {
	if (n.getAttribute) {
		var type = n.getAttribute("zk_type");
		if (type) {
			var j = type.lastIndexOf('.');
			if (j > 0) zk.load(type.substring(0, j));
			return true;
		}
	}
	return false;
}

/** Loads the javascript. It invokes _beforeLoad before loading,
 * and _afterLoad after loaded.
 */
zk._load = function (nm) {
	zk._beforeLoad();

	var e = document.createElement("script");
	e.type = "text/javascript" ;
	e.charset = "UTF-8";
	document.getElementsByTagName("HEAD")[0].appendChild(e);

	//IE: we have to append child first before setting onreadystatechange
	if (zk.agtIe) e.onreadystatechange = zk_onState;
	else e.onload = zk._afterLoad;

	var uri = nm;
	if (uri.indexOf('/') >= 0) {
		if (uri.charAt(0) != '/') uri = '/' + uri;
		e.src = zk.getUpdateURI("/web/_zver" + zk.build + uri);
	} else { //module name
		uri = uri.replace(/\./g, '/') + ".js";
		if (uri.charAt(0) != '/') uri = '/' + uri;
		e.src = zk.getUpdateURI("/web/_zver" + zk.getBuild(nm) + "/js" + uri);
	}

	if (zk.agtIe && e.readyState == "loaded") e.setAttribute("zk_loaded", "true");
};
zk._beforeLoad = function () {
	if (zk.loading ++) {
		zk._updCnt();
	} else {
		setTimeout(function () {
			if (zk.loading) {
				var n = document.getElementById("zk_loadprog");
				if (!n) zk._newProgDlg("zk_loadprog",
					'Loading (<span id="zk_loadcnt">'+zk.loading+'</span>)', 20, 20);
			}
		}, 1500);
	}
};
function zk_onState () { //onreadystatechange (IE only)
	var state = this.getAttribute("zk_loaded");
	if (this.readyState == "loaded") {
		if (state == "done") return;
	} else if (this.readyState == "complete") {
		if (state != "true") return;
	} else {
		return;
	}

	this.setAttribute("zk_loaded", "done");
	zk._afterLoad();
};
zk._afterLoad = function () {
	if (--zk.loading) {
		zk._updCnt();
	} else {
		var n = document.getElementById("zk_loadprog");
		if (n) n.parentNode.removeChild(n);
		if (zk._ready) zk._evalInit(); //zk._loadAndInit mihgt not finish
	}
};
zk._updCnt = function () {
	var n = document.getElementById("zk_loadcnt");
	if (n) {
		n.removeChild(n.firstChild);
		n.appendChild(document.createTextNode(zk.loading));
	}
};

/** Initializes the dom tree.
 */
zk.initAt = function (node) {
	if (!node) return;

	var cmps = new Array(), stk = new Array();
	stk.push(node);
	zk._loadAndInit({cmps: cmps, stk: stk, nosibling: true});
};

/** Loads all required module and initializes components. */
zk._loadAndInit = function (inf) {
	zk._ready = false;

	//The algorithm here is to mimic deep-first tree traversal
	//We cannot use recursive algorithm because it might take too much time
	//to execute and browser will alert users for aborting!
	for (var j = 0; inf.stk.length;) {
		if (++j > 2000) {
			setTimeout(function() {zk._loadAndInit(inf);}, 0);
			return; //let browser breath
		}

		var n = inf.stk.pop();

	//FF remembers the previous value that user entered when reload
	//We have to reset them because the server doesn't know any of them
		if (zk.agtNav) {
			switch (zk.tagName(n)) {
			case "INPUT":
				if (n.type == "checkbox" || n.type == "radio") {
					if (n.checked != n.defaultChecked)
						n.checked = n.defaultChecked;
					break;
				}
				if (n.type != "text" && n.type != "password") break;
				//fall thru
			case "TEXTAREA":
				if (n.value != n.defaultValue
				&& n.defaultValue != "zk_wrong!~-.zk_pha!6")
					n.value = n.defaultValue;
				break;
			case "OPTION":
				if (n.selected != n.defaultSelected)
					n.selected = n.defaultSelected;
				break;
			}
		}

		if (n.getAttribute
		&& (zk.loadByType(n)
		 || n.getAttribute("zk_drag") || n.getAttribute("zk_drop")))
			inf.cmps.push(n);

		//if nosibling, don't process its sibling (only process children)
		if (inf.nosibling) inf.nosibling = false;
		else if (n.nextSibling) inf.stk.push(n.nextSibling);
		if (n.firstChild) inf.stk.push(n.firstChild);
	}

	zk.addInitCmps(inf.cmps);
	zk._evalInit();
	zk._ready = true;
};

/** Initial components and init functions. */
zk._evalInit = function () {
	//Note: if loading, zk._doLoad will execute zk._evalInit after finish
	for (var j = 0; zk._initcmps.length && !zk.loading;) {
		var cmps = zk._initcmps.pop(); //reverse-order
		for (;;) {
			var n = cmps.pop(); //reverse-order: child first
			if (!n) break;

			var type = zk.getCompType(n);
			if (type) {
				var fn = "zk" + type + ".init";
				try {
					eval(fn+"&&"+fn+"(n)"); //to avoid being optimized, use short name
				} catch (ex) {
					zk.error("Failed to invoke "+fn+"\n"+ex.message);
				}
			}

			if (n.getAttribute("zk_drag")) zkau.initdrag(n);
			if (n.getAttribute("zk_drop")) zkau.initdrop(n);

			if (++j > 2000 || zk.loading) {
				if (cmps.length) zk.addInitCmps(cmps);
				if (!zk.loading)
					setTimeout(zk._evalInit, 0); //let browser breath
				return;
			}
		}
	}

	while (zk._initfns.length && !zk.loading)
		(zk._initfns.shift()).apply();
};
/** Check zk_type and invoke zkxxx.cleanup if declared.
 * @param cufn an optional function. If specified,
 * cufn.apply(n, new Array(n)) is called
 * for each node that has the zk_type attribute, after zkxxx.cleanup
 * is called.
 */
zk.cleanupAt = function (n, cufn) {
	if (n.getAttribute) {
		if (n.getAttribute("zk_drag")) zkau.cleandrag(n);
		if (n.getAttribute("zk_drop")) zkau.cleandrop(n);
	}

	var type = zk.getCompType(n);
	if (type) {
		var fn = "zk"+type+".cleanup";
		eval(fn+"&&"+fn+"(n)");
		if (cufn) cufn.apply(n, new Array(n));
	}

	for (n = n.firstChild; n; n = n.nextSibling)
		zk.cleanupAt(n, cufn); //recursive for child component
};

/** Loads the specified style sheet (CSS).
 * @param uri Example, "/a/b.css". It will be prefixed with zk_action + "/web",
 * unless http:// or https:// is specified
 */
zk.loadCSS = function (uri) {
	var e = document.createElement("LINK");
	e.rel = "stylesheet";
	e.type = "text/css";
	document.getElementsByTagName("HEAD")[0].appendChild(e);
	if (uri.indexOf("://") < 0) {
		if (uri.charAt(0) != '/') uri = '/' + uri;
		uri = zk.getUpdateURI("/web/_zver" + zk.build + uri);
	}
	e.href = uri;
};
/** Loads the specified JavaScript file directly.
 * @param uri Example, "/a/b.css". It will be prefixed with zk_action + "/web",
 * unless http:// or https:// is specified
 * @param fn the function to execute after loading. It is optional.
 */
zk.loadJS = function (uri, fn) {
	var e = document.createElement("script");
	e.type	= "text/javascript" ;
	e.charset = "UTF-8";
	document.getElementsByTagName("HEAD")[0].appendChild(e);
	if (fn)
		e.onload = e.onreadystatechange = function() {
			if (!e.readyState || e.readyState == 'loaded') fn.apply();
		};

	if (uri.indexOf("://") < 0) {
		if (uri.charAt(0) != '/') uri = '/' + uri;
		uri = zk.getUpdateURI("/web/_zver" + zk.build + uri);
	}
	e.src = uri;
};

/** Returns the proper URI.
 * @param ignoreSessId whether not to append session ID.
 */
zk.getUpdateURI = function (uri, ignoreSessId) {
	if (!uri) return zk_action;

	if (uri.charAt(0) != '/') uri = '/' + uri;
	var j = zk_action.lastIndexOf(';'), k = zk_action.lastIndexOf('?');
	if (j < 0 && k < 0) return zk_action + uri;

	if (k >= 0 && (j < 0 || k < j)) j = k;
	uri = zk_action.substring(0, j) + uri;
	return ignoreSessId ? uri: uri + zk_action.substring(j);
};

/** Returns the type of a node without module. */
zk.getCompType = function (n) {
	if (n.getAttribute) {
		var type = n.getAttribute("zk_type");
		if (type) {
			var j = type.lastIndexOf('.');
			return j >= 0 ? type.substring(j + 1): type;
		}
	}
	return null;
};

//-- progress --//
/** Turn on the progressing dialog after the specified timeout. */
zk.progress = function (timeout) {
	zk._progressing = true;
	if (timeout > 0) setTimeout(zk._progress, timeout);
	else zk._progress();
};
zk.progressDone = function() {
	zk._progressing = false;
	var n = document.getElementById("zk_prog");
	if (n) n.parentNode.removeChild(n);
};
/** Generates the progressing dialog. */
zk._progress = function () {
	if (zk._progressing) {
		var n = document.getElementById("zk_prog");
		if (!n) {
			var msg;
			try {msg = mesg.PLEASE_WAIT;} catch (e) {msg = "Processing...";}
				//when the first boot, mesg is not ready

			n = zk._newProgDlg("zk_prog", msg, 0, zk.innerY());
			if (n) {
				var left = zk.innerWidth() - n.offsetWidth
					- (zk.agtNav ? 18: 2); //FF shall subtract scrollbar
				if (left < 0) left = 0;
				n.style.left = left + "px";
			}
		}
	}
};
zk._newProgDlg = function (id, msg, x, y) {
	var n = document.createElement("DIV");
	document.body.appendChild(n);

	var html = '<div id="'+id+'" style="left:'+x+'px;top:'+y+'px;'
	+'position:absolute;z-index:79000;background-color:#FFF0C8;'
	+'white-space:nowrap;border:1px solid #77a;padding:6px;">'
	+'<img alt="." src="'+zk.getUpdateURI('/web/zk/img/progress.gif')+'"/> '
	+msg+'</div>';

	zk._setOuterHTML(n, html);
	return document.getElementById(id);
};

//-- debug --//
/** Generates a message for debugging. */
zk.message = function (msg) {
	var console = document.getElementById("zk_console");
	if (!console) {
		console = document.createElement("DIV");
		document.body.appendChild(console);
		var html =
 '<div style="border:1px solid #77c">'
+'<table cellpadding="0" cellspacing="0" width="100%"><tr><td width="20pt">'
+'<button onclick="zk._dbgclose(this)">close</button><br/>'
+'<button onclick="document.getElementById(\'zk_console\').value = \'\'">clear</button></td>'
+'<td><textarea id="zk_console" style="width:100%" rows="3"></textarea></td></tr></table></div>';
		zk._setOuterHTML(console, html);
		console = document.getElementById("zk_console");
	}
	console.value = console.value + msg + '\n';
};
zk._dbgclose = function (n) {
	while ((n = n.parentNode) != null)
		if (zk.tagName(n) == "DIV") {
			n.parentNode.removeChild(n);
			return;
		}
};
//FUTURE: developer could control whether to turn on/off
zk.debug = zk.message;
zk.error = zk.message;

//-- utilities --//
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
	//1. If window.innerWidth is ever supported, its meaning is the same
	//2. IE use document.body.clientWidth since document.documentElement.clientWidth is 0
	//3. Moz uses document.documentElement.clientWidth which excludes the scrollbar
	//while Moz document.body.clientWidth includes the scrollbar
	return window.innerWidth ? window.innerWidth:
		document.documentElement && document.documentElement.clientWidth ?
		document.documentElement.clientWidth: document.body.clientWidth;
};
zk.innerHeight = function () {
	return window.innerHeight ? window.innerHeight:
		document.documentElement && document.documentElement.clientHeight ?
		document.documentElement.clientHeight: document.body.clientHeight;
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

/** Returns the tag name in the upper case. */
zk.tagName = function (el) {
	return el && el.tagName ? el.tagName.toUpperCase(): "";
};

/** Pause milliseconds. */
zk.pause = function (millis) {
	if (millis) {
		var d = new Date(), n;
		do {
			n = new Date();
		} while (n - d < millis);
	}
};

//-- bootstrapping --//
if (!zk._modules) {
	zk.loading = 0;
	zk._modules = {};
	zk._initfns = new Array(); //used by addInit
	zk._initcmps = new Array(); //an array of comp list to init

	var oldol = window.onload;
	window.onload = function () {
		//It is possible to move javascript defined in zul's language.xml
		//However, IE has bug to order JavaScript properly if zk._load is used

		zk.progress(600);
		if (oldol && oldol.apply) zk.addInit(oldol);
		zk.addInit(zk.progressDone);

		zk.initAt(document.body);
	};
}

