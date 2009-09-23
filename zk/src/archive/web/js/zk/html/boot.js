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
//zk//
if (!window.zk) { //avoid eval twice
zk = {};
zk.booting = true; //deprecated (use !zk.booted instead)

////
//Customization
/** Creates the message box to notify the user that ZK client engine
 * is booting.
 * Developer can override this method to provide a customized message box.
 *
 * @param id the progress box's ID
 * @param msg the message
 * @param x the x-coordinate of the box
 * @param y the y-coordinate of the box
 * @param mask sets whether show the modal_mark (since 3.0.2)
 * @param center sets whether center the loading bar (since 3.0.2)
 */
if (!window.Boot_progressbox) { //not customized
	Boot_progressbox = function (id, msg, x, y, mask, center) {
		var html = '<div id="'+id+'"';
		var ix = zk.innerX(), iy = zk.innerY();
		if (mask) {
			if (zk._ctpgs.length) {
				for (var c = zk._ctpgs.length, e = $e(zk._ctpgs[--c]); e; e = $e(zk._ctpgs[--c]))
					zk.applyMask(e);
				return;
			}
			html += '><div id="zk_mask" class="z-modal-mask" style="display:block;left:'+ ix + 'px;top:' + iy +	
				'px;" z.x="' + ix + '" z.y="' + iy + '"></div><div';
		} else html += "><div";

		if (typeof x != 'string' || x.indexOf("%") == -1) x += "px";
		if (typeof y != 'string' || y.indexOf("%") == -1) y += "px";

		html += ' id="zk_loading" class="z-loading" style="left:'+x+';top:'+y+';visibility: hidden;"'
		+' z.x="' + ix + '" z.y="' + iy + '"><div class="z-loading-indicator">'
		+'<span class="z-loading-icon z-inline-block"  alt="..."></span> '
		+msg+'</div></div></div>';
		
		var n = document.createElement("DIV");
		document.body.appendChild(n);
		zk._setOuterHTML(n, html);
		if (mask) {
			var mk = $e("zk_mask");
			zk.listen(mk, "mousemove", Event.stop);
			zk.listen(mk, "click", Event.stop);
		}
		var el = $e("zk_loading");
		if (center) {
			if (el) {
				el.style.left = zk.px((zk.innerWidth() - el.offsetWidth) / 2 + ix, true);
				el.style.top = zk.px((zk.innerHeight() - el.offsetHeight) / 2 + iy, true);
				
				// the use is for zkau._fixOffset
				setZKAttr(el, "x", ix);
				setZKAttr(el, "y", iy);
			}
		}
		zk.cleanVisibility(el);

		return $e(id);
	};
}
/** Creates the message box to notify the user that his request is in
 * processing.
 * Developer can override this method to provide a customized message box.
 * @param id the progress box's ID
 * @param msg the message
 * @param mask sets whether show the modal_mark
 * @since 3.0.6
 */
if (!window.AU_progressbox) {
	AU_progressbar = function (id, msg, mask) {
		Boot_progressbox(id, msg, zk.innerX(), zk.innerY(), mask);
	};
}

/////
// zk
/** Converts to an integer. It handles null and "07" */
function $int(v, b) {
	v = v ? parseInt(v, b || 10): 0;
	return isNaN(v) ? 0: v;
};

/** Browser info. */
zk.agent = navigator.userAgent.toLowerCase();
zk.safari = zk.agent.indexOf("safari") >= 0;
zk.opera = zk.agent.indexOf("opera") >= 0;
zk.gecko = zk.agent.indexOf("gecko/") >= 0 && !zk.safari && !zk.opera;
if (zk.gecko) {
	var j = zk.agent.indexOf("firefox/");
	j = $int(zk.agent.substring(j + 8));
	zk.gecko3 = j >= 3;
	zk.gecko2Only = !zk.gecko3;

	zk.xbodyClass = 'gecko gecko' + j;
} else if (zk.opera) {
	zk.xbodyClass = 'opera';
} else {
	var j = zk.agent.indexOf("msie ");
	zk.ie = j >= 0;
	if (zk.ie) {
		j = $int(zk.agent.substring(j + 5));
		zk.ie7 = j >= 7; //ie7 or later
		zk.ie8All = j >= 8; //ie8 or later (including compatible)
		zk.ie8 = j >= 8 && document.documentMode >= 8; //ie8 or later
		zk.ie6Only = !zk.ie7;

		zk.xbodyClass = 'ie ie' + j;
	} else if (zk.safari)
		zk.xbodyClass = 'safari';
}
if (zk.air = zk.agent.indexOf("adobeair") >= 0)
	zk.xbodyClass = 'air';
//zk.windows = zk.agent.indexOf("windows") >= 0;
//zk.firefox = zk.gecko && zk.agent.indexOf("firefox/") < 0;

//zk.mac = zk.agent.indexOf("macintosh") >= 0;
zk._js4ld = {}; //{name, [script]}
zk._ctpgs = []; //contained page IDs
zk._gevts = {}; // ZK Global Events. 
zk._jscnt = 0; // Global JS loading count.
zk._jsmap = {}; // Global JS loaded map.
zk.voidf = function () {return false;}; //always return false

/**
 * Appends an event listener from the specified component
 * 
 * <p>Note: If you want to stop the propagation of the event,
 * 	you can return false at that listener function</p>
 * 
 * For example, 
 * <pre><code>
zk.on(cmp, "close", function (cmp) {
	// do something...

	return false; // if you want to stop the propagation of the event. Otherwise, don't need to return.

});</code></pre>
 * @param {Object} cmp an ID or a zk component
 * @param {String} evtnm The name of event to listen for
 * @param {Function} fn handler The method the event invokes
 * @since 3.5.0
 */
zk.on = function (cmp, evtnm, fn) {
	var id = typeof cmp == "string" ? cmp: cmp ? cmp.id: null;
	if (!id) {
		zk.error(mesg.COMP_OR_UUID_REQUIRED);
		return;
	}
	var ls = zk._gevts[id];
	if (!ls) zk._gevts[id] = ls = {};
	var fns = ls[evtnm];
	if (!fns) ls[evtnm] = fns = [];
	fns.push(fn);
};
/**
 * Removes an event listener from the specified component
 * @param {Object} cmp an ID or a zk component
 * @param {String} evtnm The name of event to listen for
 * @param {Function} fn The handler to remove
 * @since 3.5.0
 */
zk.un = function (cmp, evtnm, fn) {
	var ls = zk.find(cmp),
		fns = ls[evtnm];
	if (fns) {
		fns.remove(fn);
		if (!fns.length) delete ls[evtnm];
	}
};
/**
 * Removes all listeners for the specified component
 * @param {Object} cmp an ID or a zk component
 * @since 3.5.0
 */
zk.unAll = function (cmp) {
	var ls = zk.find(cmp);
	for (var evtnm in ls) {
		var fns = ls[evtnm];
		delete ls[evtnm];
		fns = null;
	}
};
/**
 * Finds all listeners of the event name from the specified component
 * @param {Object} cmp an ID or a zk component
 * @param {String} evtnm The name of event to listen for, if any. Otherwise,
`* all the listeners of the specified component are returned.
 * @since 3.5.0
 */
zk.find = function (cmp, evtnm) {
	var id = typeof cmp == "string" ? cmp: cmp ? cmp.id: null;
	if (!id) return null;
	var ls = zk._gevts[id];
	return ls ? evtnm ? ls[evtnm] : ls : null;
};

/**
 * Fires the specified event with the passed parameters.
 * @param {Object} cmp an ID or a zk component
 * @param {String} evtnm The name of event to listen for
 * @param {Object...} args Variable number of parameters are passed to handlers
 * @param {Object} scope The scope in which to execute the listener function. 
 * 	The listener function's "this" context.
 * @since 3.5.0
 */
zk.fire = function (cmp, evtnm, args, scope) {
	var fns = zk.find(cmp, evtnm);
	if (fns) {
		cmp = $e(cmp);
		for (var i = 0, j = fns.length; i < j; i++) {
			var f = fns[i];
			if (!args) args = [cmp];
			if(f.apply(scope || cmp, args) === false)
				return; // stop propagation, if any.
		}
	}
};

/** Listen a browser event.
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
/** Un-listen a browser event.
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

	zk._listen = zk.listen;
	zk.listen = function (el, evtnm, fn) {
		zk._listen(el, evtnm, fn);

		var id = _zklnid(el),
			ls = zk._ltns[id];
		if (!ls) zk._ltns[id] = ls = [];
		ls.push([el, evtnm, fn]);
	};
	function _zklnid(el) {
		return el.id || _zkAnyPid(el)
		|| (el == document ? '_doc_': el == window ? '_win_': el);
	}
	function _zkAnyPid(el) {
		while (el = el.parentNode) {
			var id = el.id;
			if (id) return id;
		}
	}

	zk._unlisten = zk.unlisten;
	zk.unlisten = function (el, evtnm, fn) {
		zk._unlisten(el, evtnm, fn);

		var id = _zklnid(el);
		for (var ls = zk._ltns[id], j = ls ? ls.length: 0, inf; j--;) {
			inf = ls[j];
			if (el == inf[0] && evtnm == inf[1] && fn == inf[2]) {
				ls.splice(j, 1);
				if (!ls.length) delete zk._ltns[id];
				break;
			}
		}
	};

	/** Unlisten events associated with the specified ID.
	 * Bug 1741959: IE meory leaks
	 */
	zk.unlistenAll = function (el) {
		if (el) {
			var id = _zklnid(el),
				ls = zk._ltns[id];
			if (ls) {
				delete zk._ltns[id];
				zk._unlitenNow(ls);
			}
		} else {
			for (var id in zk._ltns) {
				var ls = zk._ltns[id];
				if (ls) {
					delete zk._ltns[id];
					zk._unlitenNow(ls);
				}
			}
		}
	};
	zk._unlitenNow = function (ls) {
		for (var inf; inf = ls.shift();) {
			try {
				zk._unlisten(inf[0], inf[1], inf[2]);
			} catch (e) { //ignore
				if (zk.debugJS)
					throw e;
			}
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
		zk._onErrChange = true;
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
};
zk.disableESC(); //disable it as soon as possible
/** Enables ESC to back to the normal mode. */
zk.enableESC = function () {
	if (zk._noESC) {
		zk.unlisten(document, "keydown", zk._noESC);
		delete zk._noESC;
	}
	if (zk._onErrChange) {
		window.onerror = zk._oldOnErr;
		if (zk._oldOnErr) delete zk._oldOnErr;
		delete zk._onErrChange;
	}
};

/** Assigns a map of default propeties (copied only if not defined)
 * @since 3.6.0
 */
zk.$default = function (opts, defaults) {
	opts = opts || {};
	for (var p in defaults)
		if (opts[p] == null)
			opts[p] = defaults[p];
	return opts;
};
/** Copies a map of propeties
 * @since 3.6.0
 */
zk.copy = function (dst, src) {
	if (!dst) dst = {};
	for (var p in src)
		dst[p] = src[p];
	return dst;
};
zk.apply = zk.copy; //backward compatible

//////////////////////////////////////
zk.mods = {}; //ZkFns depends on it

/** 
 * A shorthand of document.getElementsByName();
 * @since 3.5.0
 */
function $es(id) {
	return typeof id == 'string' ? id ? document.getElementsByName(id): null: id;
}
/** Returns the current time (new Date().getTime()) (since 01/01/1970).
 * @since 3.0.0
 */
function $now() {
	return new Date().getTime();
}
/** Note: it is easy to cause problem with EMBED, if we use prototype's z$() since
 * it tried to extend the element.
 * @param alias a name is postfixed with the id via the "!" word. (since 3.5.0)
 *  for example, if alias is "real", the "id!real" element is returned, if any.
 */
function $e(id, alias) {
	if (id && id.id) id = id.id;
	return typeof id == 'string' ? id ? document.getElementById(id + (alias ? "!" + alias : "")): null: id;
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
/**
 * Returns the id of the first element which can be found from its parent node, inclusive itself.
 * Otherwise, "" is assumed. 
 * @param {Object} n an element.
 * @since 3.0.6
 */
function $id(n) {
	for (; n; n = $parent(n))
		if (n.id) return n.id;
	return ""; 
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
 * @param hide to avoid some annoying effect you can specify whether
 * to hide before unsetting the parent. Note: since it is hidden,
 * x.style.display will be changed -- use this option with a lot of cares
 * @since 3.0.0
 */
zk.unsetChildVParent = function (n, hide) {
	var bo = [];
	for (var id in zk._vpts)
		if (zk.isAncestor(n, id))
			bo.push(id);

	for (var j = bo.length; --j >= 0;) {
		n = $e(bo[j]);
		if (hide) n.style.display = "none";
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
 * @param {Boolean} strict if true, it also checks the visibility property.(since 3.0.4)
 */
function $visible(el, strict) {
	return el && (!el.style || (el.style.display != "none" && (!strict || el.style.visibility != "hidden")));
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

/** Returns the version of the specified module name.
 */
zk.getBuild = function (nm) {
	return zk.mods[nm] || zk.build || "0";
};

/** Adds a function that will be invoked after all components are
 * initialized.
 * <p>Note: it is called after all components are initialized
 * (init, beforeSize and onSize),
 * so it might be better to be called as addAfterInit
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
 * <p>Like zk.addInit, the function is called after all components are
 * initialized, so it might be better to be called as addAfterInitLater.
 * However, the function added by addInitLater is invoked
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
 * <p>In other words, ZK invokes functions added by zk.addBeforeInit,
 * then initializes all components, and finally invokes functions added
 * by zk.addInit.
 *
 * <p>The function is removed from the list right before invoked,
 * so it won't be called twice (unless you call zk.addBeforeInit again).
 */
zk.addBeforeInit = zk.addModuleInit = function (fn) { //addModuleInit is for backward compatible to 3.0.3 and earlier
	zk._bfinits.push(fn);
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

/** Unwatches a list of ZK callbacks.
 * Example, zk.unwatch(n, "onVisi", "onSize"), and then zkType.onVisi
 * and zkType.onSize will be ignored for the specified component
 * Note: it cannot be called in zkType.init().
 * @since 3.0.5
 */
zk.unwatch = function (n) {
	if (typeof n != "string") n = n.id;
	for (var as = arguments, j = as.length; --j > 0;)
		switch (as[j]) {
		case "onVisi": zk._visicmps.remove(n); break;
		case "onHide": zk._hidecmps.remove(n); break;
		case "onSize": zk._szcmps.remove(n); break;
		case "beforeSize": zk._bfszcmps.remove(n); break;
		case "onScroll": zk._scrlcmps.remove(n);
		}
};
/** Watches a list of ZK callbacks.
 * Example, zk.watch(n, "onVisi", "onSize"), and then zkType.onVisi
 * and zkType.onSize will be called back for the specified component.
 * By default, they will be called back, so you don't need to invoke
 * this method unless you call zk.unwatch() to stop callback.
 * Note: it cannot be called in zkType.init().
 * @since 3.0.5
 */
zk.watch = function (n) {
	n = $e(n);
	for (var as = arguments, j = as.length; --j > 0;)
		switch (as[j]) {
		case "onVisi": zk._watch(n, zk._visicmps); break;
		case "onHide": zk._watch(n, zk._hidecmps); break;
		case "onSize": zk._watch(n, zk._szcmps); break;
		case "beforeSize": zk._watch(n, zk._bfszcmps); break;
		case "onScroll": zk._watch(n, zk._scrlcmps);
		}
};
zk._watch = function (n, cmps) {
	for (var j = 0; j < cmps.length; ++j) {
		var c = cmps[j];
		if (zk.isAncestor(c, n)) {
			cmps.splice(j, 0, n.id);
			return;
		}
	}
	cmps.unshift(n.id);
};

/** Invokes the specified function that depends on the specified module.
 * If the module is not loaded yet, it will be loaded first.
 * If it is loaded, the function executes directly.
 * @since 3.0.0
 */
zk.invoke = function (nm, fn, dtid) {
	if (!zk._modules[nm]) zk.load(nm, fn, null, null, dtid);
	else if (zk.loading) zk.addBeforeInit(fn);
	else fn();
};

/** Registers a script that will be evaluated when the specified module
 * is loaded.
 * @param script a piece of JavaScript, or a function
 * @since 3.0.6
 */
zk.addOnLoad = function (nm, script) {
	if (zk._modules[nm]) {
		setTimeout(script, 0);
	} else {
		var ary = zk._js4ld[nm] = [];
		ary.push(script);
	}
};

/** Loads the specified module (JS). If a feature is called "a.b.c", then
 * zkau.uri() + "/web/js" + "a/b/c.js" is loaded.
 *
 * <p>To load a JS file that other modules don't depend on, use zk.loadJS.
 *
 * @param nm the module name if no / is specified, or filename if / is
 * specified, or URL if :// is specified.
 * @param initfn the function that will be added to zk.addBeforeInit
 * @param ckfn used if URL (i.e., xxx://) is used as nm
 * and the file being loaded doesn't invoke zk.ald(), or
 * we have to check more condition than calling back zk.ald().
 * @param modver the version of the module, or null to use zk.getBuild(nm)
 */
zk.load = function (nm, initfn, ckfn, modver, dtid) {
	if (!nm) {
		zk.error("Module name must be specified");
		return;
	}

	if (!zk._modules[nm]) {
		zk._modules[nm] = true;
		if (initfn) zk.addBeforeInit(initfn);
		zk._load(nm, modver, dtid, ckfn);
	}
};
zk._loadByType = function (nm, n) {
	if (!zk._modules[nm]) {
		zk._modules[nm] = true;
		zk._load(nm, null, zkau.dtid(n));
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
		if (j > 0) zk._loadByType(type.substring(0, j), n);
		return true;
	}
	return false;
};

/** Loads the javascript. It invokes _bld before loading.
 */
zk._load = function (nm, modver, dtid, ckfn) {
	//We don't use e.onload since Safari doesn't support it
	//See also
	//Bug 1815074: IE bug: zk.ald might be called before appendChild returns

	zk._bld();
	var e = document.createElement("script"),
		prefix = "/web",
		uri = nm;
	e.type = "text/javascript" ;

	if (ckfn) zk._ckfns.push(ckfn);
	else prefix += "/_zcbzk.ald-" + zk._jscnt++;

	if (uri.indexOf("://") > 0) {
		if (!ckfn && zk.debugJS)
			zk.error("zk.load: ckfn required to load "+uri);
		e.src = uri;
	} else if (uri.indexOf('/') >= 0) {
		if (uri.charAt(0) != '/') uri = '/' + uri;
		e.charset = "UTF-8";
		e.src = zk.getUpdateURI(prefix + uri, false, modver, dtid);
	} else { //module name
		uri = uri.replace(/\./g, '/');
		var j = uri.lastIndexOf('!');
		uri = j >= 0 ?
			uri.substring(0, j) + ".js." + uri.substring(j + 1):
			uri + ".js";
		if (uri.charAt(0) != '/') uri = '/' + uri;
		e.charset = "UTF-8";
		if (!modver) modver = zk.getBuild(nm);
		e.src = zk.getUpdateURI(prefix + "/js" + uri, false, modver, dtid);
	}
	document.getElementsByTagName("HEAD")[0].appendChild(e);
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
					--cl;
					zk.ald();
				} else return; //wait a while
		}, 10);

		setTimeout(function () {
			if (zk.loading || window.dbg_progressbox) {
				var n = $e("zk_loadprog");
				if (!n)
					Boot_progressbox("zk_loadprog",
						'Loading (<span id="zk_loadcnt">'+zk.loading+'</span>)',
						0, 0, true, true);
			}
		}, 350);
	}
};
/** after load. */
zk.ald = function (jscnt) {
	if (zk._jsmap[jscnt])
		return; // invoked twice (possible since timeplot or other might use the same prefix _zcb... to load their modules)

	if (--zk.loading) {
		zk._jsmap[jscnt] = true;
		try {
			zk._updCnt();
		} catch (ex) {
			zk.error("Failed to count. "+ex.message);
		}
	} else {
		// reset the status of loading JS file first, otherwise, it will cause zk.load() out of sync.
		zk._jsmap = {};
		zk._jscnt = 0;
		
		try {
			zk.enableESC();

			if (zk._ckload) {
				clearInterval(zk._ckload);
				delete zk._ckload;
			}

			//dispatch zk._js4ld
			for (var nm in zk._js4ld)
				if (zk._modules[nm]) {
					var ary = zk._js4ld[nm];
					if (ary) {
						delete zk._js4ld[nm];
						while (ary.length)
							setTimeout(ary.shift(), 0);
					}
				}

			zk.cleanAllMask("zk_loadprog");
		} catch (ex) {
			zk.error("Failed to stop counting. "+ex.message);
		}

		if (zk._ready) zk._evalInit(); //zk._loadAndInit might not finish
	}
};
/**
 * Cleans up all the masks applied by the Boot_progressbox function.
 * @param {String} id an element Id, such as "zk_loadprog" or "zk_prog"
 * @since 3.5.0
 */
zk.cleanAllMask = function (id) {
	var n = $e(id);
	if (n) zk.remove(n);
	for (var c = zk._ctpgs.length, n = $e(zk._ctpgs[--c]+"!progbox"); n; n = $e(zk._ctpgs[--c]+"!progbox"))
		zk.remove(n);
};
zk._updCnt = function () {
	var n = $e("zk_loadcnt");
	if (n) n.innerHTML = "" + zk.loading;
};

/** Initializes the dom tree.
 */
zk.initAt = function (node) {
	if (!node || node.nodeType != 1) return;

	var stk = [];
	stk.push(node);

	zk._loadAndInit({stk: stk, nosibling: true});
};
/** Initializes all children of the specified node.
 * @since 3.5.0
 */
zk.initChildren = function (n) {
	for (n = n.firstChild; n; n = n.nextSibling)
		zk.initAt(n);
};
/** Loads all required module and initializes components. */
zk._loadAndInit = function (inf) {
	zk._ready = false;

	//The algorithm here is to mimic deep-first tree traversal
	//We cannot use recursive algorithm because it might take too much time
	//to execute and browser will alert users for aborting!
	while (inf.stk.length) {
		var n = inf.stk.pop();
		if (n.nodeType == 1) {
			try { 
				if (!zk.ie) {
	//FF remembers the previous value that user entered when reload
	//We have to reset them because the server doesn't know any of them
					switch ($tag(n)) {
					case "INPUT":
						if (n.type == "checkbox" || n.type == "radio") {
							if (n.checked != n.defaultChecked)
								n.checked = n.defaultChecked;
							if (zk.opera) {	// Bug 2383106						
								zk.setVParent(n);
								zk.unsetVParent(n);
							}
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
				} else {
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
			} catch (e) {} // IE7 failed if href contains incorrect encoding

			var v = getZKAttr(n, "dtid");
			if (v) { //desktop's ID found
				if (zkau.addDesktop(v) && zk.pfmeter)
					zkau.pfrecv(v, v);

				var uri = getZKAttr(n, "au");
				if (uri) zkau.addURI(v, uri);
			}

			if (zk.loadByType(n) || getZKAttr(n, "drag")
			|| getZKAttr(n, "drop") || getZKAttr(n, "zid"))
				zk._initcmps.push(n);

			//Test if a (non-owned) page included by other content (e.g. JSP)
			if (getZKAttr(n, "zidsp") == "ctpage")
				zk._ctpgs.push(n.id);
		}

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
		while (!zk.loading && zk._bfinits.length)
			(zk._bfinits.shift())();

		//Note: if loading, zk._doLoad will execute zk._evalInit after finish
		while (zk._initcmps.length && !zk.loading) {
			var n = zk._initcmps.pop(); //reverse-order (child first)

			var m = zk.eval(n, "init");
			if (m) n = m; //it might be transformed

			if (getZKAttr(n, "zid")) zkau.initzid(n);
			if (getZKAttr(n, "drag")) zkau.initdrag(n);
//			if (getZKAttr(n, "drop")) zkau.initdrop(n);

			var type = $type(n);
			if (type) {
				var o = window["zk" + type];
				if (o) {
					//We put child in front of parent (by use of push)
					//note: init is called child child-first, but
					//onVisi/onHide is called parent-first
					if (o["onVisi"]) zk._tvisicmps.push(n); //child-first
					if (o["onHide"]) zk._thidecmps.push(n); //child-first
					if (o["onSize"]) zk._tszcmps.push(n); //child-first
					if (o["beforeSize"]) zk._tbfszcmps.push(n); //child-first
					if (o["onScroll"]) zk._tscrlcmps.push(n); //child-first
				}
			}

			if (zk.loading)
				return;
		}

		if (!zk.loading) {
			//put _tvisicmps at the head of _visicmps and keep child-first
			for (var es = zk._tvisicmps; es.length;)
				zk._visicmps.unshift(es.pop());
			for (var es = zk._thidecmps; es.length;)
				zk._hidecmps.unshift(es.pop());
			for (var es = zk._tscrlcmps; es.length;)
				zk._scrlcmps.unshift(es.pop());

			//since beforeSize/onSize might zk.unwatch, add to _szcmps first
			for (var es = zk._tbfszcmps, j = es.length; --j >= 0;)
				zk._bfszcmps.unshift(es[j]);
			for (var es = zk._tszcmps, j = es.length; --j >= 0;)
				zk._szcmps.unshift(es[j]);

			for (var es = zk._tbfszcmps; es.length;) {
				var n = es.pop(); //parent at the end, so pop the end first
				if ($visible(n)) zk.eval(n, "beforeSize");
			}
			for (var es = zk._tszcmps; es.length;) {
				var n = es.pop();
				if ($visible(n)) zk.eval(n, "onSize");
			}
		}

		while (!zk.loading && zk._initfns.length)
			(zk._initfns.shift())();

		if (!zk.loading && !zk._initfns.length) {
			zk._initids = {}; //cleanup

			setTimeout(zk._initLater, 25);
		}
	} while (!zk.loading && (zk._bfinits.length || zk._initcmps.length
	|| zk._initfns.length));
	//Bug 1815074: _initfns might cause _bfinits to be added

	zkau.doCmds(); //since response commands might not be processed yet
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
 * @return the result
 */
zk.eval = function (n, fn, type) {
	if (!type) type = $type(n);
	if (type) {
		var o = window["zk" + type];
		if (o) {
			var f = o[fn];
			if (f) {
				try {
					var args = [n];
					for (var j = arguments.length - 2; --j > 0;) //3->1, 4->2...
						args[j] = arguments[j + 2];
					return f.apply(o, args);
				} catch (ex) {
					zk.error("Failed to invoke zk"+type+"."+fn+"\n"+ex.message);
					if (zk.debugJS) throw ex;
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
	zk._afterCleanup();
};
/** Cleanup all children of the specified node.
 * @since 3.5.0
 */
zk.cleanupChildren = function (n) {
	for (n = n.firstChild; n; n = n.nextSibling)
		zk._cleanupAt(n);
	zk._afterCleanup();
};
zk._afterCleanup = function () {
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
//	if (getZKAttr(n, "drop")) zkau.cleandrop(n);
	if (getZKAttr(n, "hvig")) zkau.cleanhvig(n);

	var type = $type(n);
	if (type) {
		zk.eval(n, "cleanup", type);
		zkau.cleanupMeta(n); //note: it is called only if type is defined
		zk._visicmps.remove(n);
		zk._hidecmps.remove(n);
		zk._szcmps.remove(n);
		zk._bfszcmps.remove(n);
		zk._scrlcmps.remove(n);
	}
	zk.unlistenAll(n); //Bug 1741959: memory leaks
	zk.unAll(n); //bug #2313106 whatever it is, we shall invoke zk.unAll()
	
	for (n = n.firstChild; n; n = n.nextSibling)
		if (n.nodeType == 1) zk._cleanupAt(n); //recursive for child component
};

/** To notify a component that it becomes visible because one its ancestors
 * becomes visible. All descendants of n is invoked if onVisi is declared.
 * The invocation of onVisi is parent-first (and then child).
 *@param n the topmost element that has become visible.
 * Note: n has become visible when this method is called.
 * If null, all elements will be handled
 */
zk.onVisiAt = function (n) {
	//Note: process from last since zk.unwatch assumes it
	for (var elms = zk._visicmps, j = elms.length; --j >= 0;) { //parent first
		for (var elm = elms[j], e = elm; e; e = $parent(e)) {
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

	//Note: process from last since zk.unwatch assumes it
	for (var elms = zk._hidecmps, j = elms.length; --j >= 0;) { //parent first
		for (var elm = elms[j], e = elm; e; e = $parent(e)) {
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
	//Note: process from last since zk.unwatch assumes it
	for (var elms = zk._szcmps, j = elms.length; --j >= 0;) { //parent first
		for (var elm = elms[j], e = elm; e; e = $parent(e)) {
			if (!$visible(e))
				break;
			if (!n || e == n) { //elm is a child of n
				zk.eval(elm, "onSize");
				break;
			}
		}
	}
};
/** To notify a component that its parent's size WILL be changed.
 * All descendants of n is invoked if beforeSize is declared.
 * The invocation of beforeSize is parent-first (and then child).
 *
 * <p>The typical use is: call beforeSizeAt first, adjust the width/height
 * and then call onSizeAt.
 *
 * <p>The component usually cleans up in zkXxx.beforeSize, if necessary, and
 * does the sizing in zkXxx.onSize, if necessary.
 *
 * @param n the topmost element whose size is changed.
 * If null, the browser's size is changed and all elements will be handled.
 * @since 3.0.4
 */
zk.beforeSizeAt = function (n) {
	//Note: process from last since zk.unwatch assumes it
	for (var elms = zk._bfszcmps, j = elms.length; --j >= 0;) { //parent first
		for (var elm = elms[j], e = elm; e; e = $parent(e)) {
			if (!$visible(e))
				break;
			if (!n || e == n) { //elm is a child of n
				zk.eval(elm, "beforeSize");
				break;
			}
		}
	}
};
/** To notify a component that the parent is scrolled.
 * All descendants of n is invoked if onScroll is declared.
 * The invocation of onScroll is parent-first (and then child).
 * @param n the topmost element which is scrolled.
 * If null, the browser's size is changed and all elements will be handled.
 * @since 3.0.5
 */
zk.onScrollAt = function (n) {
	if (zkau.valid) zkau.valid.onScrollAt(n); // Bug #1819264
	//Note: process from last since zk.unwatch assumes it
	for (var elms = zk._scrlcmps, j = elms.length; --j >= 0;) { //parent first
		for (var elm = elms[j], e = elm; e; e = $parent(e)) {
			if (!$visible(e))
				break;
			if (!n || e == n) { //elm is a child of n
				zk.eval(elm, "onScroll");
				break;
			}
		}
	}
};

//extra//
/** Loads the specified style sheet (CSS).
 * @param uri Example, "/a/b.css". It will be prefixed with zkau.uri() + "/web",
 * unless http:// or https:// is specified
 */
zk.loadCSS = function (uri, dtid) {
	if (uri.indexOf("://") < 0) {
		if (uri.charAt(0) != '/') uri = '/' + uri;
		uri = zk.getUpdateURI("/web" + uri, false, null, dtid);
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
 * @param uri Example, "/a/b.css". It will be prefixed with zkau.uri() + "/web",
 * unless http:// or https:// is specified
 * @param fn the function to execute after loading. It is optional.
 * Not function under safari
 */
zk.loadJS = function (uri, fn, dtid) {
	var e = document.createElement("script");
	e.type	= "text/javascript" ;
	e.charset = "UTF-8";
	if (fn)
		e.onload = e.onreadystatechange = function() {
			if (!e.readyState || e.readyState == 'loaded') fn();
		};

	if (uri.indexOf("://") < 0) {
		if (uri.charAt(0) != '/') uri = '/' + uri;
		uri = zk.getUpdateURI("/web" + uri, false, null, dtid);
	}
	e.src = uri;
	document.getElementsByTagName("HEAD")[0].appendChild(e);
};
//-- utilities --//
zk.https = function () {
	var p = location.protocol;
	return p && "https:" == p.toLowerCase();
};
//-- bootstrapping --//
zk.loading = 0;
zk._modules = {}; //Map(String nm, boolean loaded)
zk._initfns = []; //used by addInit
zk._initids = {};
zk._inLatfns = []; //used by addInitLater
zk._inLatids = {};
zk._bfinits = []; //used by addBeforeInit
zk._cufns = []; //used by addCleanup
zk._cuids = {};
zk._cuLatfns = []; //used by addCleanupLater
zk._cuLatids = {};
zk._bfunld = []; //used by addBeforeUnload
zk._initcmps = []; //comps to init
zk._ckfns = []; //functions called to check whether a module is loaded (zk._load)
zk._visicmps = []; //an array of component's ID that requires zkType.onVisi; the child is in front of the parent
zk._hidecmps = []; //an array of component's ID that requires zkType.onHide; the child is in front of the parent
zk._szcmps = []; //an array of component's ID that requires zkType.onSize; the child is in front of the parent
zk._bfszcmps = []; //an array of component's ID that requires zkType.beforeSize; the child is in front of the parent
zk._scrlcmps = []; //an array of component's ID that requires zkType.onScroll; the child is in front of the parent
zk._tszcmps = [], zk._tbfszcmps = [], zk._tscrlcmps = [],
zk._tvisicmps = [], zk._thidecmps = []; //temporary array
function myload() {
	var ebc = zk.xbodyClass;
	if (ebc) {
		zk.xbodyClass = null;
		var n = document.body
			cn = n.className;
		if (cn.length) cn += ' ';
		n.className = cn + ebc;
	}

	var f = zk._onload;
	if (f) {
		zk._onload = null; //called only once
		f();
	}
}
zk.bootDone = function () {
	if (zk.pfmeter)
		for (var dtids = zkau._dtids, j = dtids.length; --j >=0;)
			zkau.pfdone(dtids[j], dtids[j]);
	zk.progressDone();
	zk.booting = false;
	zk.booted = true;
	zkau.onURLChange();
};
zk._onload = function () {
	//It is possible to move javascript defined in zul's language.xml
	//However, IE has bug to order JavaScript properly if zk._load is used
	zk.progress(600);
	zk.addInitLater(zk.bootDone);
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
