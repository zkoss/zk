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
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
//zk//
if (!window.zk) { //avoid eval twice

////
//Customization
/** Creates the error box to display the specified error message.
 * Developer can override this method to provide a customize errorbox.
 * If null is returned, alert() is used.
 *
 * @param id the progress box's ID
 * @param msg the message
 * @param x the x-coordinate of the box
 * @param y the y-coordinate of the box
 * @param mk sets whether show the modal_mark (since 3.0.2)
 * @param center sets whether center the loading bar (since 3.0.2)
 */
if (!window.Boot_progressbox) { //not customized
	window.Boot_progressbox = function (id, msg, x, y, mk, center) {
		var n = document.createElement("DIV");
		document.body.appendChild(n);

		var html = '<div id="'+id+'"';

		var mask = mk || zk.loading && !zk._prgsOnce;
		if (mask) {
			zk._prgsOnce = true; //do it only once
			html += ' ><div class="modal_mask" style="display:block"></div><div'
		} else html += "><div";
		if (typeof x != 'string' || x.indexOf("%") == -1) x += "px";
		if (typeof y != 'string' || y.indexOf("%") == -1) y += "px";
		html += ' id="z-loading" class="z-loading" style="left:'+x+';top:'+y+';">'
		+'<div class="z-loading-indicator">'
		+'<img alt="..." style="width:18px;height:18px" src="'+zk.getUpdateURI('/web/zk/img/progress2.gif')+'"/> '
		+msg+'</div></div></div>';

		zk._setOuterHTML(n, html);
		if (center) {
			var el = $e("z-loading");
			if (el) {
				el.style.left = (zk.innerWidth() - el.offsetWidth) / 2 + "px";
				el.style.top = (zk.innerHeight() - el.offsetHeight) / 2 + "px";
			}
		}
		return $e(id);
	};
}

/////
// zk
zk = {};
zk.build = "8v"; //increase this if we want the browser to reload JavaScript
zk.voidf = Prototype.emptyFunction;
zk.booting = true; //denote ZK is booting

/** Browser info. */
zk.agent = navigator.userAgent.toLowerCase();
zk.safari = zk.agent.indexOf("safari") != -1;
zk.opera = zk.agent.indexOf("opera") != -1;
zk.ie = zk.agent.indexOf("msie") != -1 && !zk.opera;
zk.ie7 = zk.agent.indexOf("msie 7") != -1; //ie7 or later
zk.ie6Only =  zk.ie && !zk.ie7;
zk.gecko = zk.agent.indexOf("gecko/") != -1 && !zk.safari && !zk.opera;
zk.windows = zk.agent.indexOf("windows") != -1;
zk.mozilla = zk.gecko && zk.agent.indexOf("firefox/") == -1;
//zk.macintosh = zk.agent.indexOf("macintosh") != -1;

/** Listen an event.
 * Why not to use prototype's Event.observe? Performance.
 */
zk.listen = function (el, evtnm, fn) {
	if (el.addEventListener)
		el.addEventListener(evtnm, fn, false);
	else /*if (el.attachEvent)*/
		el.attachEvent('on' + evtnm, fn);

	//Bug 1811352
	if ("submit" == evtnm && $tag(el) == "FORM") {
		if (!el._submfns) el._submfns = [];
		el._submfns.push(fn);
	}
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

	//Bug 1811352
	if ("submit" == evtnm && $tag(el) == "FORM" && el._submfns)
		el._submfns.remove(fn);
};

if (zk.ie) { //Bug 1741959: avoid memory leaks
	zk._ltns = {} // map(el, [evtnm, fn])
	zk._unltns = []; //array of [el, [evtnm, fn]]

	zk._listen = zk.listen;
	zk.listen = function (el, evtnm, fn) {
		zk._listen(el, evtnm, fn);

		var ls = zk._ltns[el];
		if (!ls) zk._ltns[el] = ls = {};
		var fns = ls[evtnm];
		if (!fns) ls[evtnm] = fns = [];
		fns.push(fn);
	};

	zk._unlisten = zk.unlisten;
	zk.unlisten = function (el, evtnm, fn) {
		zk._unlisten(el, evtnm, fn);

		var ls = zk._ltns[el];
		var fns = ls ? ls[evtnm]: null;
		if (fns) {
			fns.remove(fn);
			if (!fns.length) delete ls[evtnm];
		}
	};

	/** Unlisten events associated with the specified ID.
	 * Bug 1741959: IE meory leaks
	 */
	zk.unlistenAll = function (el) {
		if (el) {
			var ls = zk._ltns[el];
			if (ls) {
				zk._unltns.push([el, ls]);
				delete zk._ltns[el];
				setTimeout(zk._unlistenOne, 10000 + 20000*Math.random());
					//Note: the performance is not good, so delay 10~30s
			}
		} else {
			while (zk._unltns.length)
				zk._unlistenOne();

			for (var el in zk._ltns) {
				var ls = zk._ltns[el];
				if (ls) {
					delete zk._ltns[el];
					zk._unlistenNode(el, ls);
				}
			}
		}
	};
	zk._unlistenOne = function () {
		if (zk._unltns.length) {
			var inf = zk._unltns.shift();
			zk._unlistenNode(inf[0], inf[1]);
		}
	};
	zk._unlistenNode = function (el, ls) {
		for (var evtnm in ls) {
			var fns = ls[evtnm];
			delete ls[evtnm];
			for (var j = fns.length; --j >= 0;) {
				try {
					zk._unlisten(el, evtnm, fns[j]);
					fns[j] = null; //just in case
				} catch (e) { //ignore
				}
			}
			fns.length = 0; //just in case
		}
	};
} else {
	/** No function if not IE. */
	zk.unlistenAll = zk.voidf;
}

/** disable ESC to prevent user from pressing ESC to stop loading */
zk.disableESC = function () {
	if (!zk._noESC) {
		zk._noESC = function (evt) {
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
		zk.listen(document, "keydown", zk._noESC);

		//FUTURE: onerror not working in Safari and Opera
		//if error occurs, loading will be never ended, so try to ignore
		//we cannot use zk.listen. reason: no way to get back msg...(FF)
		zk._oldOnErr = window.onerror;
		zk._onErrChanged = true;
		window.onerror =
	function (msg, url, lineno) {
		//We display errors only for local class web resource
		//It is annoying to show error if google analytics's js not found
		if (url.indexOf(location.host) >= 0) {
			var v = zk_action.lastIndexOf(';');
			var v = v >= 0 ? zk_action.substring(0, v): zk_action;
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
};
zk.disableESC(); //disable it as soon as possible
/** Enables ESC to back to the normal mode. */
zk.enableESC = function () {
	if (zk._noESC) {
		zk.unlisten(document, "keydown", zk._noESC);
		delete zk._noESC;
	}
	if (zk._onErrChanged) {
		window.onerror = zk._oldOnErr;
		if (zk._oldOnErr) delete zk._oldOnErr;
		delete zk._onErrChanged;
	}
};

//////////////////////////////////////
zk.mods = {}; //ZkFns depends on it

/** Returns the current time (new Date().getTime()) (since 01/01/1970).
 * @since 3.0.0
 */
function $now() {
	return new Date().getTime();
}
/** Note: it is easy to cause problem with EMBED, if we use prototype's $() since
 * it tried to extend the element.
 */
function $e(id) {
	return typeof id == 'string' ? id ? document.getElementById(id): null: id;
		//strange but getElementById("") fails in IE7
}
/** A control might be enclosed by other tag while event is sent from
 * the control directly, so... */
function $uuid(n) {
	if (typeof n != 'string') {
		for (; n; n = $parent(n))
			if (n.id) {
				n = n.id;
				break;
			}
	}
	if (!n) return "";
	var j = n.lastIndexOf('!');
	return j > 0 ? n.substring(0, j): n;
}
/** Returns the real element (ends with !real).
 * If a component's attributes are located in the inner tag, i.e.,
 * you have to surround it with span or other tag, you have to place
 * uuid!real on the inner tag
 *
 * Note: !chdextr is put by the parent as the exterior of its children,
 * while !real is by the component itself
 */
function $real(cmp) {
	var id = $uuid(cmp);
	if (id) {
		var n = $e(id + "!real");
		if (n) return n;
		n = $e(id);
		if (n) return n;
	}
	return cmp;
}
/** Returns the enclosing element (not ends with !real).
 * If not found, cmp is returned.
 */
function $outer(cmp) {
	var id = $uuid(cmp);
	if (id) {
		var n = $e(id);
		if (n) return n;
	}
	return cmp;
}
/** Returns the type of a node without module. */
function $type(n) {
	var type = getZKAttr(n, "type");
	if (type) {
		var j = type.lastIndexOf('.');
		return j >= 0 ? type.substring(j + 1): type;
	}
	return null;
};
/** Returns the peer (xxx!real => xxx, xxx => xxx!real), or null if n/a.
 */
/*function $peer(id) {
	return id ? $e(
		id.endsWith("!real") ? id.substring(0, id.length-5): id+"!real"): null;
}*/
/** Returns the exterior of the specified component (ends with !chdextr).
 * Some components, hbox nad vbox, need to add exterior to child compoents,
 * and the exterior is named with "uuid!chdextr".
 */
function $childExterior(cmp) {
	var n = $e(cmp.id + "!chdextr");
	return n ? n: cmp;
}

/** Returns the parent node of the specified element.
 * It handles virtual parent.
 */
function $parent(n) {
	var p = zk._vpts[n.id];
	return p ? p: n.parentNode;
}
/** Sets virtual parent. It is used if a popup is limited (cropped) by a parent div.
 * @since 3.0.0
 */
zk.setVParent = function (n) {
	var id = n.id, p = n.parentNode;
	if (!id) {
		zk.error("id required, "+n);
		return;
	}
	if (zk.isVParent(id))
		return; //called twice

	var sib = n.nextSibling;
	if (sib) {
		var agtx = document.createElement("SPAN");
		agtx.id = id + "!agtx";
		agtx.style.display = "none";
		p.insertBefore(agtx, sib);
	}

	zk._vpts[id] = p;

	if (!getZKAttr(n, "dtid")) setZKAttr(n, "dtid", zkau.dtid(n));
	document.body.appendChild(n);
};
/**
 * Returns whether the element has a virtual parent.
 * @since 3.0.0
 * @param {Object} or {String} n
 */
zk.isVParent = function (n) {
	return zk._vpts[n && n.id ? n.id: n];
};
/** Unsets virtual parent.
 * @since 3.0.0
 */
zk.unsetVParent = function (n) {
	var id = n.id, p = zk._vpts[id];
	delete zk._vpts[id];
	if (p) {
		var sib = $e(id + "!agtx");
		if (sib) {
			p.insertBefore(n, sib);
			zk.remove(sib);
		} else
			p.appendChild(n);
	}
};
/** unsetVParent if it is a child of the specified node, n.
 * 	Note: including itself.
 * @since 3.0.0
 */
zk.unsetChildVParent = function (n) {
	var bo = [];
	for (var id in zk._vpts)
		if (zk.isAncestor(n, id))
			bo.push(id);

	for (var j = bo.length; --j >= 0;) {
		n = $e(bo[j]);
		zk.unsetVParent(n);
	}
	return bo;
};
//Note: we have to use string to access {}. Otherwise, the behavior is strange
zk._vpts = {}; //a map of virtual parent (n.id, n's parent)

/** Returns the nearest parent element, including el itself, with the specified type.
 */
function $parentByType(el, type) {
	for (; el; el = $parent(el))
		if ($type(el) == type)
			return el;
	return null;
};
/** Returns the tag name in the upper case. */
function $tag(el) {
	return el && el.tagName ? el.tagName.toUpperCase(): "";
};
/** Returns the nearest parent element, including el itself, with the specified tag.
 */
function $parentByTag(el, tagName) {
	for (; el; el = $parent(el))
		if ($tag(el) == tagName)
			return el;
	return null;
};
/** Returns whether an element is visible.
 * Returns false if none-existence.
 * Returns true if no style.
 */
function $visible(el) {
	return el && (!el.style || el.style.display != "none");
}

/** Converts to an integer. It handles null and "07" */
function $int(v, b) {
	v = v ? parseInt(v, b || 10): 0;
	return isNaN(v) ? 0: v;
};

/** Returns the ZK attribute of the specified name.
 * Note: the name space of ZK attributes is "http://www.zkoss.org/2005/zk"
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
};
/** Sets the ZK attribute of the specified name with the specified value.
 */
function setZKAttr(el, nm, val) {
	if (el && el.setAttribute) el.setAttribute("z." + nm, val);
};
function rmZKAttr(el, nm) {
	if (el && el.removeAttribute) el.removeAttribute("z." + nm);
	else setZKAttr(el, nm, "");
};

/** Returns the version of the specified module name.
 */
zk.getBuild = function (nm) {
	return zk.mods[nm] || zk.build;
};

/** Adds a function that will be invoked after all components are
 * initialized.
 * <p>Note: it is called after all components are initialized.
 * <p>The function is removed from the list right before invoked,
 * so it won't be called twice (unless you call zk.addInit again).
 * @param front whether to add the function to the front of the list
 * @param {String} unique whether not to add if redundant. If any, fn is added
 * only if fn was not added before.
 */
zk.addInit = function (fn, front, unique) {
	if (typeof unique == "string") {
		if(zk._initids[unique]) return;	
		zk._initids[unique] = true;
	}
	zk._addfn(zk._initfns, fn, front);
};
/** Adds a function that will be invoked 25 milliseconds, after
 * all components are initialized.
 * Like zk.addInit, the function is called after all components are
 * initialized. However, the function added by addInitLater is invoked
 * with a timer that is called 25 milliseconds.
 * Thus, it is designed to add functions that cannot be called
 * immediately after initialization (zkType.init).
 *
 * <p>The function is removed from the list right before invoked,
 * so it won't be called twice (unless you call zk.addInitLater again).
 *
 * @param front whether to add the function to the front of the list
 * @param {String} unique whether not to add if redundant. If any, fn is added
 * only if fn was not added before.
 * @since 3.0.0
 */
zk.addInitLater = function (fn, front, unique) {
	if (typeof unique == "string") {
		if(zk._inLatids[unique]) return;
		zk._inLatids[unique] = true;
	}
	zk._addfn(zk._inLatfns, fn, front);
};
zk._addfn = function (fns, fn, front) {
	if (front) fns.unshift(fn);
	else fns.push(fn);
};
/** Adds a function for module initialization.
 * It is called after all javascript fies are loaded, and before
 * initializing the components.
 *
 * <p>In other words, ZK invokes functions added by zk.addModuleInit,
 * then initializes all components, and finally invokes functions added
 * by zk.addInit.
 *
 * <p>The function is removed from the list right before invoked,
 * so it won't be called twice (unless you call zk.addModuleInit again).
 */
zk.addModuleInit = function (fn) {
	zk._initmods.push(fn);
};
/** Adds a component that must be initialized after
 * all modules are loaded and initialized.
 */
zk.addInitCmp = function (cmp) {
	zk._initcmps.push(cmp);
};

/** Adds a function that will be invoked after all components are
 * cleaned up.
 * Unlike zk.addCleanupLater, the components being cleaned up are
 * still available when fn is called.
 * <p>Note: it is called after all components are initialized.
 * <p>The function is removed from the list right before invoked,
 * so it won't be called twice (unless you call zk.addInit again).
 * @param front whether to add the function to the front of the list
 * @param {String} unique whether not to add if redundant. If any, fn is added
 * only if fn was not added before.
 */
zk.addCleanup = function (fn, front, unique) {
	if (typeof unique == "string") {
		if(zk._cuids[unique]) return;	
		zk._cuids[unique] = true;
	}
	zk._addfn(zk._cufns, fn, front);
};
/** Adds a function that will be invoked 25 milliseconds, after
 * all components are cleaned up.
 * Note: when fn is called, the component being cleaned up may be removed.
 *
 * <p>The function is removed from the list right before invoked,
 * so it won't be called twice (unless you call zk.addInitLater again).
 *
 * @param front whether to add the function to the front of the list
 * @param {String} unique whether not to add if redundant. If any, fn is added
 * only if fn was not added before.
 * @since 3.0.0
 */
zk.addCleanupLater = function (fn, front, unique) {
	if (typeof unique == "string") {
		if(zk._cuLatids[unique]) return;	
		zk._cuLatids[unique] = true;
	}
	zk._addfn(zk._cuLatfns, fn, front);
};

/** Adds a function that will be invoked before the browser is unloading
 * the page.
 * If the function returns a string, then the whole execution will stop
 * and the string is returned to browser (to warning the user).
 * If nothing is returned, the following functions will be invoked.
 *
 * @param front whether to add the function to the front of the list
 * @since 3.0.0
 */
zk.addBeforeUnload = function (fn, front) {
	if (front) zk._bfunld.unshift(fn);
	else zk._bfunld.push(fn);
};
/** Removes the function added by zk.addBeforeUnload.
 * @since 3.0.0
 */
zk.rmBeforeUnload = function (fn) {
	zk._bfunld.remove(fn);
};
/** Called when window.onbeforeunload is called.
 * Note: you rarely need to invoke this directly (except au.js).
 * @since 3.0.0
 */
zk.beforeUnload = function () {
	for (var j = 0, bl = zk._bfunld.length; j < bl; ++j) {
		var s = zk._bfunld[j]();
		if (s) return s;
	}
};

/** Invokes the specified function that depends on the specified module.
 * If the module is not loaded yet, it will be loaded first.
 * If it is loaded, the function executes directly.
 * @since 3.0.0
 */
zk.invoke = function (nm, fn) {
	if (!zk._modules[nm]) zk.load(nm, fn);
	else if (zk.loading) zk.addModuleInit(fn);
	else fn();
};

/** Loads the specified module (JS). If a feature is called "a.b.c", then
 * zk_action + "/web/js" + "a/b/c.js" is loaded.
 *
 * <p>To load a JS file that other modules don't depend on, use zk.loadJS.
 *
 * @param nm the module name if no / is specified, or filename if / is
 * specified, or URL if :// is specified.
 * @param initfn the function that will be added to zk.addModuleInit
 * @param ckfn used ONLY if URL (i.e., xxx://) is used as nm,
 * and the file being loaded doesn't invoke zk.ald().
 * @param modver the version of the module, or null to use zk.getBuild(nm)
 */
zk.load = function (nm, initfn, ckfn, modver) {
	if (!nm) {
		zk.error("Module name must be specified");
		return;
	}

	if (!zk._modules[nm]) {
		zk._modules[nm] = true;
		if (initfn) zk.addModuleInit(initfn);
		zk._load(nm, modver);
		if (ckfn) zk._ckfns.push(ckfn);
	}
};
/** Loads the required module for the specified component.
 * Note: it DOES NOT check any of its children.
 * @return true if z.type is defined.
 */
zk.loadByType = function (n) {
	var type = getZKAttr(n, "type");
	if (type) {
		var j = type.lastIndexOf('.');
		if (j > 0) zk.load(type.substring(0, j));
		return true;
	}
	return false;
}

/** Loads the javascript. It invokes _bld before loading.
 *
 * <p>The JavaScript file being loaded must<br/>
 * 1) call zk.ald() after loaded<br/>
 * 2) pass ckfn to test whether it is loaded.
 */
zk._load = function (nm, modver) {
	zk._bld();

	var e = document.createElement("script");
	e.type = "text/javascript" ;

	var zcb;
	if (zk.gecko) {
		e.onload = zk.ald;
		zcb = "";
	} else {
		zcb = "/_zcbzk.ald";
			//Note: we use /_zcb to enforce callback of zk.ald
	}

	var uri = nm;
	if (uri.indexOf("://") > 0) {
		e.src = uri;
	} else if (uri.indexOf('/') >= 0) {
		if (uri.charAt(0) != '/') uri = '/' + uri;
		e.charset = "UTF-8";
		e.src = zk.getUpdateURI("/web" + zcb + uri, false, modver);
	} else { //module name
		uri = uri.replace(/\./g, '/');
		var j = uri.lastIndexOf('!');
		uri = j >= 0 ?
			uri.substring(0, j) + ".js." + uri.substring(j + 1):
			uri + ".js";
		if (uri.charAt(0) != '/') uri = '/' + uri;
		e.charset = "UTF-8";
		if (!modver) modver = zk.getBuild(nm);
		e.src = zk.getUpdateURI("/web" + zcb + "/js" + uri, false, modver);
	}
	document.getElementsByTagName("HEAD")[0].appendChild(e);
		//Bug 1815074: IE bug:
		//zk.ald might be called before returning from appendChild
};
/** before load. */
zk._bld = function () {
	if (zk.loading ++) {
		zk._updCnt();
	} else {
		zk.disableESC();

		zk._ckload = setInterval(function () {
			for (var j = 0, cl = zk._ckfns.length; j < cl; ++j)
				if (zk._ckfns[j]()) {
					zk._ckfns.splice(j--, 1);
					zk.ald();
				} else return; //wait a while
		}, 10);

		setTimeout(function () {
			if (zk.loading) {
				var n = $e("zk_loadprog");
				if (!n)
					Boot_progressbox("zk_loadprog",
						'Loading (<span id="zk_loadcnt">'+zk.loading+'</span>)',
						"45%", "40%");
			}
		}, 350);
	}
};
/** after load. */
zk.ald = function () {
	if (--zk.loading) {
		try {
			zk._updCnt();
		} catch (ex) {
			zk.error("Failed to count. "+ex.message);
		}
	} else {
		try {
			zk.enableESC();

			if (zk._ckload) {
				clearInterval(zk._ckload);
				delete zk._ckload;
			}

			var n = $e("zk_loadprog");
			if (n) n.parentNode.removeChild(n);
		} catch (ex) {
			zk.error("Failed to stop counting. "+ex.message);
		}
		
		if (zk._ready) zk._evalInit(); //zk._loadAndInit mihgt not finish
	}
};
zk._updCnt = function () {
	var n = $e("zk_loadcnt");
	if (n) n.innerHTML = "" + zk.loading;
};

/** Initializes the dom tree.
 */
zk.initAt = function (node) {
	if (!node) return;

	var stk = [];
	stk.push(node);
	zk._initszcmps.push(node);
	zk._loadAndInit({stk: stk, nosibling: true});
};

/** Loads all required module and initializes components. */
zk._loadAndInit = function (inf) {
	zk._ready = false;

	//The algorithm here is to mimic deep-first tree traversal
	//We cannot use recursive algorithm because it might take too much time
	//to execute and browser will alert users for aborting!
	for (var j = 0; inf.stk.length;) {
		if (++j > 1000) {
			setTimeout(function() {zk._loadAndInit(inf);}, 10);
			return; //let browser breath
		}

		var n = inf.stk.pop();

	//FF remembers the previous value that user entered when reload
	//We have to reset them because the server doesn't know any of them
		if (zk.gecko) {
			switch ($tag(n)) {
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
				//break;
			}
		} else if (zk.ie) {
			switch ($tag(n)) {
			case "A": //Bug 1635685 and 1612312
			case "AREA": //Bug 1896749
				if (n.href.indexOf("javascript:") >= 0)
					zk.listen(n, "click", zk._ieFixBfUnload);
				break;
			case "FORM":
				//Bug 1811352
				zk.fixSubmit(n);
				//break;
			}
		}

		var v = getZKAttr(n, "dtid");
		if (v) zkau.addDesktop(v); //desktop's ID found

		if (zk.loadByType(n) || getZKAttr(n, "drag")
		|| getZKAttr(n, "drop") || getZKAttr(n, "zid"))
			zk._initcmps.push(n);

		//if nosibling, don't process its sibling (only process children)
		if (inf.nosibling) inf.nosibling = false;
		else if (n.nextSibling && !getZKAttr(n, "skipsib"))
			inf.stk.push(n.nextSibling);

		//Since 3.0.2, we introduce z.skipdsc to stop parsing the descendants.
		//It improves the performance for the sophisticated component
		//that want to initialize children in a custom way.
		if (n.firstChild && !getZKAttr(n, "skipdsc"))
			inf.stk.push(n.firstChild);
	}

	zk._evalInit();
	zk._ready = true;
};
/** Fix bug 1635685 and 1612312 (IE/IE7 only):
 * IE invokes onbeforeunload if <a href="javascript;"> is clicked (sometimes)
 * It actually ignores window.beforeunload temporary by
 * set zk.skipBfUnload.
 */
if (zk.ie) {
	zk._ieFixBfUnload = function () {
		zk.skipBfUnload = true;
		setTimeout(zk._skipBackBF, 0); //restore
	};
	zk._skipBackBF = function () {
		zk.skipBfUnload = false;
	};
}

/** Initial components and init functions. */
zk._evalInit = function () {
	do {
		while (!zk.loading && zk._initmods.length)
			(zk._initmods.shift())();

		//Note: if loading, zk._doLoad will execute zk._evalInit after finish
		for (var j = 0; zk._initcmps.length && !zk.loading;) {
			var n = zk._initcmps.pop(); //reverse-order (child first)

			var m = zk.eval(n, "init");
			if (m) n = m; //it might be transformed

			if (getZKAttr(n, "zid")) zkau.initzid(n);
			if (getZKAttr(n, "drag")) zkau.initdrag(n);
			if (getZKAttr(n, "drop")) zkau.initdrop(n);

			var type = $type(n);
			if (type) {
				var o = window["zk" + type];
				if (o) {
					//We put child in front of parent (by use of push)
					//note: init is called child child-first, but
					//onVisi/onHide is called parent-first
					if (o["onVisi"]) zk._tvisicmps.push(n.id); //child-first
					if (o["onHide"]) zk._thidecmps.push(n.id); //child-first
					if (o["onSize"]) zk._tsizecmps.push(n.id); //child-first
				}
			}

			if (zk.loading || ++j > 1000) {
				if (!zk.loading)
					setTimeout(zk._evalInit, 10); //let browser breath
				return;
			}
		}

		while (!zk.loading && zk._initfns.length)
			(zk._initfns.shift())();

		if (!zk.loading && !zk._initfns.length) {
			zk._initids = {}; //cleanup

			//put _tsizecmps at the head of _sizecmps and keep child-first
			for (var es = zk._tvisicmps; es.length;)
				zk._visicmps.unshift(es.pop());
			for (var es = zk._thidecmps; es.length;)
				zk._hidecmps.unshift(es.pop());
			for (var es = zk._tsizecmps; es.length;)
				zk._sizecmps.unshift(es.pop());

			while (zk._initszcmps.length)
				zk.onSizeAt(zk._initszcmps.shift());

			setTimeout(zk._initLater, 25);
		}
	} while (!zk.loading && (zk._initmods.length || zk._initcmps.length
	|| zk._initfns.length));
	//Bug 1815074: _initfns might cause _initmods to be added
};
zk._initLater = function () {
	while (!zk.loading && zk._inLatfns.length)
		(zk._inLatfns.shift())();
	if (!zk.loading && !zk._inLatfns.length)
		zk._inLatids = {};
};

/** Evaluate a method of the specified component.
 *
 * It assumes fn is a method name of a object called "zk" + type
 * (in window).
 * Nothing happens if no such object or no such method.
 *
 * @param n the component
 * @param fn the method name, e.g., "init"
 * @param type the component type. If omitted, $type(n)
 * is assumed.
 * @param a0 the first of extra arguments; null to omitted
 * @return the result
 */
zk.eval = function (n, fn, type, a0, a1, a2, a3, a4, a5, a6, a7) {
	if (!type) type = $type(n);
	if (type) {
		var o = window["zk" + type];
		if (o) {
			var f = o[fn];
			if (f) {
				try {
					return f(n,a0,a1,a2,a3,a4,a5,a6,a7);
				} catch (ex) {
					zk.error("Failed to invoke zk"+type+"."+fn+"\n"+ex.message);
				}
			}
		}
	}
	return false;
};

/** Check z.type and invoke zkxxx.cleanup if declared.
 */
zk.cleanupAt = function (n) {
	zk._cleanupAt(n);

	while (zk._cufns.length)
		(zk._cufns.shift())();
		
	zk._cuids = {};
	setTimeout(zk._cleanLater, 25);
};
zk._cleanLater = function () {
	while (zk._cuLatfns.length)
		(zk._cuLatfns.shift())();
	zk._cuLatids = {};
};
zk._cleanupAt = function (n) {
	if (getZKAttr(n, "zid")) zkau.cleanzid(n);
	if (getZKAttr(n, "zidsp")) zkau.cleanzidsp(n);
	if (getZKAttr(n, "drag")) zkau.cleandrag(n);
	if (getZKAttr(n, "drop")) zkau.cleandrop(n);

	var type = $type(n);
	if (type) {
		zk.eval(n, "cleanup", type);
		zkau.cleanupMeta(n); //note: it is called only if type is defined
		zk.unlistenAll(n); //Bug 1741959: memory leaks
		zk._visicmps.remove(n.id);
		zk._hidecmps.remove(n.id);
		zk._sizecmps.remove(n.id);
	}

	for (n = n.firstChild; n; n = n.nextSibling)
		zk._cleanupAt(n); //recursive for child component
};

/** To notify a component that it becomes visible because one its ancestors
 * becomes visible. All descendants of n is invoked if onVisi is declared.
 * The invocation of onVisi is parent-first (and then child).
 *@param n the topmost element that has become visible.
 * Note: n has become visible when this method is called.
 * If null, all elements will be handled
 */
zk.onVisiAt = function (n) {
	for (var elms = zk._visicmps, j = elms.length; --j >= 0;) { //parent first
		var elm = $e(elms[j]);
		for (var e = elm; e; e = $parent(e)) {
			if (!$visible(e))
				break;
			if (e == n || !n) { //elm is a child of n
				zk.eval(elm, "onVisi");
				break;
			}
		}
	}
};
/** To notify a component that it becomes invisible because one its ancestors
 * becomes invisible. All descendants of n is invoked if onHide is declared.
 * The invocation of onHide is parent-first (and then child).
 *@param n the topmost element that will become invisible.
 * Note: n is still visible when this method is called.
 * If null, all elements will be handled.
 */
zk.onHideAt = function (n) {
	//Bug 1526542: we have to blur if we want to hide a focused control in gecko and IE
	var f = zkau.currentFocus;
	if (f && zk.isAncestor(n, f)) {
		zkau.currentFocus = null;
		try {f.blur();} catch (e) {}
	}

	for (var elms = zk._hidecmps, j = elms.length; --j >= 0;) { //parent first
		var elm = $e(elms[j]);
		for (var e = elm; e; e = $parent(e)) {
			if (!$visible(e)) //yes, ignore hidden ones
				break;
			if (e == n || !n) { //elm is a child of n
				zk.eval(elm, "onHide");
				break;
			}
		}
	}
};
/** To notify a component that its parent's size is changed.
 * All descendants of n is invoked if onSize is declared.
 * The invocation of onSize is parent-first (and then child).
 * @param n the topmost element whose size is changed.
 * If null, the browser's size is changed and all elements will be handled.
 * @since 3.0.4
 */
zk.onSizeAt = function (n) {
	for (var elms = zk._sizecmps, j = elms.length; --j >= 0;) { //parent first
		var elm = $e(elms[j]);
		for (var e = elm; e; e = $parent(e)) {
			if (!$visible(e))
				break;
			if (!n || e == n) { //elm is a child of n
				zk.eval(elm, "onSize");
				break;
			}
		}
	}
};

//extra//
/** Loads the specified style sheet (CSS).
 * @param uri Example, "/a/b.css". It will be prefixed with zk_action + "/web",
 * unless http:// or https:// is specified
 */
zk.loadCSS = function (uri) {
	if (uri.indexOf("://") < 0) {
		if (uri.charAt(0) != '/') uri = '/' + uri;
		uri = zk.getUpdateURI("/web" + uri);
	}
	zk.loadCSSDirect(uri);
};
/**Loads the specified style sheet (CSS) without prefixing with /web.
 *
 * @param uri the URI. Note: it is assigned to the href attribute directly
 * without prefix with /web
 * @param id the identifier (the id attribute). Optional.
 * @since 3.0.2
 */
zk.loadCSSDirect = function (uri, id) {
	var e = document.createElement("LINK");
	if (id) e.id = id;
	e.rel = "stylesheet";
	e.type = "text/css";
	e.href = uri;
	document.getElementsByTagName("HEAD")[0].appendChild(e);
};
/** Loads the specified JavaScript file directly.
 * @param uri Example, "/a/b.css". It will be prefixed with zk_action + "/web",
 * unless http:// or https:// is specified
 * @param fn the function to execute after loading. It is optional.
 * Not function under safari
 */
zk.loadJS = function (uri, fn) {
	var e = document.createElement("script");
	e.type	= "text/javascript" ;
	e.charset = "UTF-8";
	if (fn)
		e.onload = e.onreadystatechange = function() {
			if (!e.readyState || e.readyState == 'loaded') fn();
		};

	if (uri.indexOf("://") < 0) {
		if (uri.charAt(0) != '/') uri = '/' + uri;
		uri = zk.getUpdateURI("/web" + uri);
	}
	e.src = uri;
	document.getElementsByTagName("HEAD")[0].appendChild(e);
};

/** Returns the proper URI.
 * @param ignoreSessId whether not to append session ID.
 * @param modver the module version to insert into uri, or null to use zk.build.
 * Note: modver is used if uri starts with /uri
 */
zk.getUpdateURI = function (uri, ignoreSessId, modver) {
	if (!uri) return zk_action;

	if (uri.charAt(0) != '/') uri = '/' + uri;
	if (modver && uri.length >= 5 && uri.substring(0, 5) == "/web/")
		uri = "/web/_zver" + modver + uri.substring(4);

	var j = zk_action.lastIndexOf(';'), k = zk_action.lastIndexOf('?');
	if (j < 0 && k < 0) return zk_action + uri;

	if (k >= 0 && (j < 0 || k < j)) j = k;
	var prefix = zk_action.substring(0, j);

	if (ignoreSessId)
		return prefix + uri;

	var suffix = zk_action.substring(j);
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
	var n = $e("zk_prog");
	if (n) n.parentNode.removeChild(n);
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
				//when the first boot, mesg is not ready

			Boot_progressbox("zk_prog", msg, zk.innerX(), zk.innerY());
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
			var html =
 '<div class="debugbox">'
+'<table cellpadding="0" cellspacing="0" width="100%"><tr>'
+'<td width="20pt"><button onclick="zk._msgclose(this)">close</button><br/>'
+'<button onclick="$e(\'zk_msg\').value = \'\'">clear</button></td>'
+'<td><textarea id="zk_msg" style="width:99%" rows="10"></textarea></td></tr></table></div>';
			zk._setOuterHTML(console, html);
			console = $e("zk_msg");
		}
		console.value = console.value + zk._msg + '\n';
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
zk.debug = zk.message;

/** Error message must be a popup. */
zk.error = function (msg) {
	if (!zk._errcnt) zk._errcnt = 1;
	var id = "zk_err_" + zk._errcnt++;
	var box = document.createElement("DIV");
	document.body.appendChild(box);
	var html =
 '<div style="position:absolute;z-index:99000;padding:3px;left:'
+(zk.innerX()+50)+'px;top:'+(zk.innerY()+20)
+'px;width:550px;border:1px solid #963;background-color:#fc9" id="'
+id+'"><table cellpadding="2" cellspacing="2" width="100%"><tr valign="top">'
+'<td width="20pt"><button onclick="zk._msgclose(this)">close</button></td>'
+'<td style="border:1px inset">'+zk.encodeXML(msg, true) //Bug 1463668: security
+'</td></tr></table></div>';
	zk._setOuterHTML(box, html);
	box = $e(id); //we have to retrieve back

	try {
		new Draggable(box, {
			handle: box, zindex: box.style.zIndex,
			starteffect: zk.voidf, starteffect: zk.voidf, endeffect: zk.voidf});
	} catch (e) {
	}
};

//-- bootstrapping --//
zk.loading = 0;
zk._modules = {}; //Map(String nm, boolean loaded)
zk._initfns = []; //used by addInit
zk._initids = {};
zk._inLatfns = []; //used by addInitLater
zk._inLatids = {};
zk._initmods = []; //used by addModuleInit
zk._cufns = []; //used by addCleanup
zk._cuids = {};
zk._cuLatfns = []; //used by addCleanupLater
zk._cuLatids = {};
zk._bfunld = []; //used by addBeforeUnload
zk._initcmps = []; //comps to init
zk._initszcmps = []; //comps that requires onSizeAt to be called in _evalInit
zk._ckfns = []; //functions called to check whether a module is loaded (zk._load)
zk._visicmps = []; //an array of component's ID that requires zkType.onVisi; the child is in front of the parent
zk._hidecmps = []; //an array of component's ID that requires zkType.onHide; the child is in front of the parent
zk._sizecmps = []; //an array of component's ID that requires zkType.onSize; the child is in front of the parent
zk._tsizecmps = [], zk._tvisicmps = [], zk._thidecmps = []; //temporary array
function myload() {
	var f = zk._onload;
	if (f) {
		zk._onload = null; //called only once
		f();
	}
}
zk._onload = function () {
	//It is possible to move javascript defined in zul's language.xml
	//However, IE has bug to order JavaScript properly if zk._load is used
	zk.progress(600);
	zk.addInitLater(zk.progressDone);
	zk.addInitLater(function() {zk.booting = false;});
	zk.initAt(document.body);
};

//Source: http://dean.edwards.name/weblog/2006/06/again/
if (zk.ie && !zk.https()) {
	//IE consider the following <script> insecure, so skip is https
	document.write('<script id="_zie_load" defer src="javascript:void(0)"><\/script>');
	var e = $e("_zie_load");
	e.onreadystatechange = function() {
		if ("complete" == this.readyState) //don't check loaded!
	        myload(); // call the onload handler
	};
	e.onreadystatechange();
} else if (zk.safari) {
    var timer = setInterval(function() {
		if (/loaded|complete/.test(document.readyState)) {
			clearInterval(timer);
			delete timer;
			myload();
		}
	}, 10);
} else {
	//Bug 1619959: FF not always fire DOMContentLoaded (such as in 2nd iframe),
	//so we have to use onload in addition to register DOMContentLoaded
	if (zk.gecko)
		zk.listen(document, "DOMContentLoaded", myload)

	zk._oldOnload = window.onload;
	window.onload = function () {
		myload();
		if (zk._oldOnload)
			zk._oldOnload.apply(window, arguments);
	}
}
} //if (!window.zk)
