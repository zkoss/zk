/* au.js

{{IS_NOTE
	Purpose:
		JavaScript for asynchronous updates
	Description:
		
	History:
		Fri Jun 10 15:04:31     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
function zkau() {}

if (!zkau._reqs) {
	zkau._reqs = new Array(); //Ajax requests
	zkau._respXmls = new Array(); //responses in XML
	zkau._evts = new Array();
	zkau._js4resps = new Array(); //JS to eval upon response
	zkau._metas = {}; //(id, meta)
	zkau._movs = {}; //(id, Draggable): moveables
	zkau._drags = {}; //(id, Draggable): draggables
	zkau._drops = new Array(); //dropables
	zkau._idsp = {}; //ID spaces: {owner's uuid, {id, uuid}}
	zkau._stamp = 0; //used to make a time stamp
	zkau.topZIndex = 0; //topmost z-index for overlap/popup/modal
	zkau.floats = new Array(); //popup of combobox, bandbox, datebox...
	zkau._onsends = new Array(); //JS called before zkau._sendNow

	zk.addInit(function () {
		Event.observe(document, "keydown", zkau._onDocKeydown);
		Event.observe(document, "mousedown", zkau._onDocMousedown);
		Event.observe(document, "mouseover", zkau._onDocMouseover);
		Event.observe(document, "mouseout", zkau._onDocMouseout);

		Event.observe(document, "contextmenu", zkau._onDocCtxMnu);
		Event.observe(document, "click", zkau._onDocLClick);
		Event.observe(document, "dblclick", zkau._onDocDClick);

		zkau._oldDocUnload = window.onunload;
		window.onunload = zkau._onDocUnload; //unable to use Event.observe
	});
}

/** Handles onclick for button-type.
 */
zkau.onclick = function (evt) {
	if (typeof evt == 'string') {
		zkau.send({uuid: zkau.uuidOf(evt), cmd: "onClick", data: null});
		return;
	}

	if (!evt) evt = window.event;
	target = Event.element(evt);

	//it might be clicked on the inside element
	for (;; target = target.parentNode)
		if (!target) return;
		else if (target.id) break;

	var href = target.getAttribute("zk_href");
	if (href) {
		zk.go(href, false, target.getAttribute("zk_target"));
		return; //done
	}

	zkau.send({uuid: zkau.uuidOf(target.id),
		cmd: "onClick", data: zkau._getMouseData(evt, target)});
};
/** Returns the data for onClick. */
zkau._getMouseData = function (evt, target) {
	var extra = "";
	if (evt.altKey) extra += "a";
	if (evt.ctrlKey) extra += "c";
	if (evt.shiftKey) extra += "s";

	var ofs = Position.cumulativeOffset(target);
	var x = Event.pointerX(evt) - ofs[0];
	var y = Event.pointerY(evt) - ofs[1];
	return [x, y, extra];
};

/** Asks the server to update a component. */
zkau.doUpdatable = function (uuid, updatableId) {
	zkau.send({uuid: uuid, cmd: "doUpdatable", data: [updatableId]}, -1);
	zkau.remove(uuid);
}
/** Asks the server to remove a component. */
zkau.remove = function (uuid) {
	if (!uuid) {
		zk.error(mesg.UUID_REQUIRED);
		return;
	}
	zkau.send({uuid: uuid, cmd: "remove", data: null}, 5);
};

/** Called when the response is received from zkau._reqs.
 */
zkau._onRespReady = function () {
	while (zkau._reqs.length > 0) {
		var req = zkau._reqs.shift();
		try {
			if (req.readyState != 4) {
				zkau._reqs.unshift(req);
				break; //we handle response sequentially
			}

			if (zkau._revertpending) zkau._revertpending();
				//revert any pending when the first response is received

			if (req.status == 200) {
				var xmls = req.responseXML.getElementsByTagName("r");
				if (xmls)
					for (var j = 0; j < xmls.length; ++j)
						zkau._respXmls.push(xmls[j]);
			} else {
				zk.error(mesg.FAILED_TO_RESPONSE+req.statusText);
				zkau._cleanupOnFatal();
			}
		} catch (e) {
			//NOTE: if connection is off and req.status is accessed,
			//Mozilla throws exception while IE returns a value
			zk.error(mesg.FAILED_TO_RESPONSE+e.message);
			zkau._cleanupOnFatal();
		}
	}

	zkau._doQueResps();
	zkau._checkProgress();
};
zkau._checkProgress = function () {
	if (zkau._respXmls.length == 0 && zkau._reqs.length == 0)
		zk.progressDone();
};

/** Returns the timeout of the specified event.
 * It is mainly used to generate the timeout argument of zkau.send.
 */
zkau.asapTimeout = function (cmp, evtnm) {
	cmp = $(cmp);
	return cmp && cmp.getAttribute("zk_" + evtnm) == "true" ? 25: -1;
};

/** Adds a callback to be called before sending ZK request.
 * @param func the function call
 */
zkau.addOnSend = function (func) {
	zkau._onsends.push(func);
};
zkau.removeOnSend = function (func) {
	zkau._onsends.remove(func);
};
/** Sends a request to the client and queue it to zkau._reqs.
 * @param timout milliseconds.
 * If negative, it won't be sent until next non-negative event
 * If zero, it is sent immediately.
 */
zkau.send = function (evt, timeout) {
	if (timeout < 0) evt.implicit = true;
	zkau._evts.push(evt);
	if (!timeout) zkau._sendNow();
	else if (timeout > 0) setTimeout(zkau._sendNow, timeout);
};
/** Sends a request before any pending events.
 * Note: it doesn't cause any pending events (including evt) to be sent.
 * It is designed to be called in zkau.onSend
 */
zkau.sendAhead = function (evt) {
	zkau._evts.unshift(evt);
};
zkau._sendNow = function () {
	if (!zk_action || !zk_desktopId) {
		zk.error(mesg.NOT_FOUND+"zk_action or zk_desktopId");
		return;
	}

	if (zkau._evts.length == 0)
		return; //nothing to do

	if (zk.loading) {
		if (!zkau._sendadded) {
			zkau._sendadded = true;
			zk.addInit(zkau._sendNow); //note: when callback, zk.loading is false
		}
		return; //wait
	}

	//callback
	for (var j = 0; j < zkau._onsends.length; ++j) {
		try {
			zkau._onsends[j]();
		} catch (e) {
			zk.error(e.message);
		}
	}

	zkau._sendadded = false;

	//FUTURE: Consider XML (Pros: ?, Cons: larger packet)
	var content = "", implicit = true;
	for (var j = 0;; ++j) {
		var evt = zkau._evts.shift();
		if (!evt) break; //done

		implicit = implicit && evt.implicit;
		content += "&cmd."+j+"="+evt.cmd+"&uuid."+j+"="+evt.uuid;
		if (evt.data)
			for (var k = 0; k < evt.data.length; ++k) {
				var data = evt.data[k];
				content += "&data."+j+"="
					+ (data != null ? encodeURIComponent(data): 'zk_null~q');
			}
	}

	if (!content) return; //nothing to do

	content = "dtid="+zk_desktopId + content;
	var req;
	if (window.ActiveXObject) { //IE
		req = new ActiveXObject("Microsoft.XMLHTTP");
	} else if (window.XMLHttpRequest) { //None-IE
		req = new XMLHttpRequest();
	}

	if (req) {
		try {
			if (!zkau.ignoreResponse) { //use with care
				zkau._reqs.push(req);
				req.onreadystatechange = zkau._onRespReady;
			}
			req.open("POST", zk_action, true);
			req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			req.send(content);
			if (!implicit) zk.progress(900); //wait a moment to avoid annoying
		} catch (e) {
			try {
				if(typeof req.abort == "function") req.abort();
			} catch (e2) {
			}
			zk.error(mesg.FAILED_TO_SEND+zk_action+"\n"+content+"\n"+e.message);
		}
	} else {
		zk.error(mesg.FAILED_TO_SEND+zk_action+"\n"+content);
	}
};

/** Adds a script that will be evaluated when the next response is back. */
zkau.addOnResponse = function (script) {
	zkau._js4resps.push(script);
};
/** Evaluates scripts registered by addOnResponse. */
zkau._evalOnResponse = function () {
	for (;;) {
		var script = zkau._js4resps.shift();
		if (!script) break;
		setTimeout(script, 0);
	}
};

/** Process the responses queued in zkau._respXmls. */
zkau._doQueResps = function () {
	var ex;
	for (var j = 0; zkau._respXmls.length > 0;) {
		if (zk.loading) {
			if (!zkau._procadded) {
				zkau._procadded = true;
				zk.addInit(zkau._doQueResps); //Note: when callback, zk.loading is false
			}
			return; //wait until the loading is done
		}
		zkau._procadded = false;

		try {
			zkau._doResp(zkau._respXmls.shift());
		} catch (e) {
			if (!ex) ex = e;
		}

		if (!ex && ++j > 300) {
			setTimeout(zkau._doQueResps, 0); //let browser breath
			return;
		}
	}

	zkau._checkProgress();
	if (ex) throw ex;
};
/** Process the specified response in XML. */
zkau._doResp = function (respXml) {
	var cmd = respXml.getElementsByTagName("c")[0];
	var data = respXml.getElementsByTagName("d");
	if (!cmd) {
		zk.error(mesg.ILLEGAL_RESPONSE+"Command required");
		return;
	}

	cmd = zk.getElementValue(cmd);
	var dt0, dt1, dt2, dt3, dt4;
	var datanum = data ? data.length: 0;
	if (datanum >= 1) {
		dt0 = zk.getElementValue(data[0]);
		if (datanum >= 2) {
			dt1 = zk.getElementValue(data[1]);
			if (datanum >= 3) {
				dt2 = zk.getElementValue(data[2]);
				if (datanum >= 4) {
					dt3 = zk.getElementValue(data[3]);
					if (datanum >= 5) dt4 = zk.getElementValue(data[4]);
				}
			}
		}
	}

	try {
		zkau.process(cmd, datanum, dt0, dt1, dt2, dt3, dt4);
	} catch (e) {
		zk.error(mesg.FAILED_TO_PROCESS+cmd+"\n"+e.message+"\n"+dt0+"\n"+dt1);
		throw e;
	} finally {
		zkau._evalOnResponse();
	}
};
/** Process a command.
 */
zkau.process = function (cmd, datanum, dt0, dt1, dt2, dt3, dt4) {
	//I. process commands that dt0 is not UUID
	var fn = zkau.cmd0[cmd];
	if (fn) {
		fn.call(zkau, dt0, dt1, dt2, dt3, dt4);
		return;
	}

	//I. process commands that require uuid
	var uuid = dt0;
	if (!uuid) {
		zk.error(mesg.ILLEGAL_RESPONSE+"uuid is required for "+cmd);
		return;
	}
	var cmp = $(uuid);

	if (zkau.processExt
	&& zkau.processExt(cmd, uuid, cmp, datanum, dt1, dt2, dt3, dt4))
		return;

	fn = zkau.cmd1[cmd];
	if (fn) {
		fn.call(zkau, uuid, cmp, dt1, dt2, dt3, dt4);
		return;
	}

	zk.error(mesg.ILLEGAL_RESPONSE+"Unknown command: "+cmd);
};
zk.process = zkau.process; //ZK assumes zk.process, so change it

/** Cleans up if we detect obsolete or other severe errors. */
zkau._cleanupOnFatal = function () {
	for (uuid in zkau._metas) {
		var meta = zkau._metas[uuid];
		if (meta && meta.cleanupOnFatal)
			meta.cleanupOnFatal();
	}
};

/** Invoke zk.initAt for siblings. Note: from and to are excluded. */
zkau._initSibs = function (from, to, next) {
	for (;;) {
		from = next ? from.nextSibling: from.previousSibling;
		if (!from || from == to) break;
		zk.initAt(from);
	}
};
/** Invoke zk.initAt for all children. */
zkau._initChildren = function (n) {
	for (n = n.firstChild; n; n = n.nextSibling)
		zk.initAt(n);
};
/** Invoke inserHTMLBeforeEnd and then zk.initAt.
 */
zkau._insertAndInitBeforeEnd = function (n, html) {
	if (zk.tagName(n) == "TABLE" && zk.tagOfHtml(html) == "TR") {
		if (!n.tBodies || !n.tBodies.length) {
			var m = document.createElement("TBODY");
			n.appendChild(m);
			n = m;
		} else {
			n = n.tBodies[0];
		}
	}

	var lc = n.lastChild;
	zk.insertHTMLBeforeEnd(n, html);		
	if (lc) zkau._initSibs(lc, null, true);
	else zkau._initChildren(n);
};
/** Invoke zk.cleanupAt for all children. */
zkau._cleanupChildren = function (n) {
	for (n = n.firstChild; n; n = n.nextSibling)
		zk.cleanupAt(n, zkau.cleanupMeta);
};

/** Invoke zkau.onVisiAt for all descendant. */
zkau.onVisiChildren = function (n) {
	for (n = n.firstChild; n; n = n.nextSibling)
		zkau.onVisiAt(n);
};
/** Invoke zkau.onHideAt for all descendant. */
zkau.onHideChildren = function (n) {
	for (n = n.firstChild; n; n = n.nextSibling)
		zkau.onHideAt(n);
};

/** To notify a component that it becomes visible because one its ancestors
 * becomes visible. It recursively invokes its descendants.
 */
zkau.onVisiAt = function (n) {
	if (!n || (n.style && n.style.display == "none")) return; //done

	zk.eval(n, "onVisi");
	zkau.onVisiChildren(n);
};
/** To notify a component that it becomes invisible because one its ancestors
 * becomes invisible. It recursively invokes its descendants.
 */
zkau.onHideAt = function (n) {
	if (!n) return; //done

	//Bug 1526542: we have to blur if we want to hide a focused control in gecko
	if (zk.gecko) {
		var f = zkau.currentFocus;
		if (f && zk.isAncestor(n, f)) {
			zkau.currentFocus = null;
			try {f.blur();} catch (e) {}
		}
	}

	var type = zk.getCompType(n);
	if (type) {
		if (zkau.valid) {
			zkau.valid.closeErrbox(n.id);
			zkau.valid.closeErrbox(n.id + "!real");
		}

		zk.eval(n, "onHide", type);
	}
	zkau.onHideChildren(n);
};

/** Sets an attribute (the default one). */
zkau.setAttr = function (cmp, name, value) {
	if ("visibility" == name) {
		action.show(cmp, "true" == value);
	} else if ("value" == name) {
		if (value != cmp.value) {
			cmp.value = value;
			if (cmp == zkau.currentFocus && cmp.select) cmp.select();
				//fix a IE bug that cursor disappear if input being
				//changed is onfocus
		}
		if (cmp.defaultValue != cmp.value)
			cmp.defaultValue = cmp.value;
	} else if ("checked" == name) {
		value = "true" == value || "checked" == value;
		if (value != cmp.checked)
			cmp.checked = value;
		if (cmp.defaultChecked != cmp.checked)
			cmp.defaultChecked = cmp.checked;
		//we have to update defaultChecked because click a radio
		//might cause another to unchecked, but browser doesn't
		//maintain defaultChecked
	} else if ("selectAll" == name && zk.tagName(cmp) == "SELECT") {
		value = "true" == value;
		for (var j = 0; j < cmp.options.length; ++j)
			cmp.options[j].selected = value;
	} else {
		var j = name.indexOf('.'); 
		if (j >= 0) {
			if ("style" != name.substring(0, j)) {
				zk.error(mesg.UNSUPPORTED+name);
				return;
			}
			name = zk.toJSStyleName(name.substring(j + 1));
			if (typeof(cmp.style[name]) == "boolean") //just in case
				value = "true" == value || name == value;
			cmp.style[name] = value;
			return;
		} else if ("style" == name) {
			zk.setStyle(cmp, value);
			return;
		}

		if (name == "disabled" || name == "href")
			zkau.setStamp(cmp, name);
			//mark when this attribute is set (change or not), so
			//modal dialog and other know how to process it
			//--
			//Better to call setStamp always but, to save memory,...

		//Firefox return null for cmp.getAttribute("selectedIndex"...), so...
		var old = "class" == name ? cmp.className:
			"selectedIndex" == name ? cmp.selectedIndex:
			"defaultValue" == name ? cmp.defaultValue: //Moz has problem to use getAttribute with this
			"disabled" == name ? cmp.disabled:
			"readOnly" == name ? cmp.readOnly:
				cmp.getAttribute(name);
		//Note: "true" != true (but "123" = 123)
		//so we have take care of boolean
		if (typeof(old) == "boolean")
			value = "true" == value || name == value; //e.g, reaonly="readOnly"

		if (old != value) {
			if ("selectedIndex" == name) cmp.selectedIndex = value;
			else if ("class" == name) cmp.className = value;
			else if ("defaultValue" == name) {
				var old = cmp.value;
				cmp.defaultValue = value;
				if (old != cmp.value) cmp.value = old; //Bug 1490079 (happen in FF only)
			} else if ("disabled" == name) cmp.disabled = value;
			else if ("readOnly" == name) cmp.readOnly = value;
			else cmp.setAttribute(name, value);
		}
	}
};
/** Returns the time stamp. */
zkau.getStamp = function (cmp, name) {
	var stamp = cmp.getAttribute("zk_st" + name);
	return stamp ? stamp: "";
};
/** Sets the time stamp. */
zkau.setStamp = function (cmp, name) {
	cmp.setAttribute("zk_st" + name, "" + ++zkau._stamp);
};
zkau.rmAttr = function (cmp, name) {
	if ("class" == name) {
		if (cmp.className) cmp.className = "";
	} else {
		var j = name.indexOf('.'); 
		if (j >= 0) {
			if ("style" != name.substring(0, j)) {
				zk.error(mesg.UNSUPPORTED+name);
				return;
			}
			cmp.style[name.substring(j + 1)] = "";
		} else if (!cmp.hasAttriute || cmp.hasAttribute(name)) {
			cmp.setAttribute(name, "");
		}
	}
};

/** A control might be enclosed by other tag while event is sent from
 * the control directly, so... */
zkau.uuidOf = function (n) {
	if (typeof n != 'string') {
		for (; n; n = n.parentNode)
			if (n.id) {
				n = n.id;
				break;
			}
	}
	if (!n) return "";
	var j = n.lastIndexOf('!');
	return j > 0 ? n.substring(0, j): n;
};

/** Returns the real element (ends with !real).
 * If a component's attributes are located in the inner tag, i.e.,
 * you have to surround it with span or other tag, you have to place
 * uuid!real on the inner tag
 *
 * Note: !chdextr is put by the parent as the exterior of its children,
 * while !real is by the component itself
 */
zkau.getReal = function (cmp) {
	if (!cmp) return null;
	var real = $(cmp.id + "!real");
	return real ? real: cmp;
};
/** Returns the enclosing element (not ends with !real).
 * If not found, cmp is returned.
 */
zkau.getOuter = function (cmp) {
	var id = zkau.uuidOf(cmp);
	if (id) {
		var n = $(id);
		if (n) return n;
	}
	return cmp;
};

/** Returns the peer (xxx!real => xxx, xxx => xxx!real), or null if n/a.
 */
/*zkau.getPeer = function (id) {
	return id ? $(
		id.endsWith("!real") ? id.substring(0, id.length-5): id+"!real"): null;
};*/

/** Returns the exterior of the specified component (ends with !chdextr).
 * Some components, hbox nad vbox, need to add exterior to child compoents,
 * and the exterior is named with "uuid!chdextr".
 */
zkau._getChildExterior = function (cmp) {
	var n = $(cmp.id + "!chdextr");
	return n ? n: cmp;
};

/** Corrects zIndex of the specified component, which must be absolute.
 * @param autoz whether it is called by
 * @param silent whether to send onZIndex
 */
zkau.fixZIndex = function (cmp, silent, autoz) {
	if (!zkau._popups.length && !zkau._overlaps.length && !zkau._modals.length)
		zkau.topZIndex = 0; //reset it!

	var zi = parseInt(cmp.style.zIndex || "0");
	if (zi > zkau.topZIndex) {
		zkau.topZIndex = zi;
	} else if (!autoz || zi < zkau.topZIndex) {
		cmp.style.zIndex = ++zkau.topZIndex;
		if (!silent && cmp.id) {
			cmp = zkau.getOuter(cmp);
			zkau.send({uuid: cmp.id, cmd: "onZIndex",
				data: [zi]}, zkau.asapTimeout(cmp, "onZIndex"));
		}
	}
};
/** Automatically adjust z-index if node is part of popup/overalp/...
 */
zkau.autoZIndex = function (node) {
	for (; node; node = node.parentNode) {
		if (node.style && node.style.position == "absolute") {
			if (node.getAttribute("zk_autoz"))
				zkau.fixZIndex(node, false, true); //don't inc if equals
			break;
		}
	}
};

//-- popup --//
if (!zkau._popups) {
	zkau._popups = new Array(); //uuid
	zkau._overlaps = new Array(); //uuid
	zkau._modals = new Array(); //uuid (used zul.js or other modal)
	zkau.wndmode = {}; //uuid of wnd that is draggable
	zkau._intervals = {};
}
/** Makes the component as popup. */
zkau.doPopup = function (cmp) {
	zkau.closeFloats(cmp);

	zkau.wndmode[cmp.id] = "popup";

	var caption = $(cmp.id + "!caption");
	if (caption && caption.style.cursor == "") caption.style.cursor = "move";

	zkau.fixZIndex(cmp);
	zkau.enableMoveable(cmp, null, zkau.onWndMove);
	zkau._popups.push(cmp.id); //store ID because it might cease before endPopup
	zkau.hideCovered();
	zk.focusDownById(cmp.id, 0);
};
/** Makes the popup component as normal. */
zkau.endPopup = function (uuid) {
	var caption = $(uuid + "!caption");
	if (caption && caption.style.cursor == "move") caption.style.cursor = "";

	zkau._popups.remove(uuid);
	zkau.hideCovered();
	var cmp = $(uuid);
	if (cmp) {
		zkau.wndmode[cmp.id] = null;
		zkau.disableMoveable(cmp);
	}
};
/** Makes the component as overlapped. */
zkau.doOverlapped = function (cmp) {
	zkau.closeFloats(cmp);

	zkau.wndmode[cmp.id] = "overlapped";

	var caption = $(cmp.id + "!caption");
	if (caption && caption.style.cursor == "") caption.style.cursor = "move";

	zkau.fixZIndex(cmp);
	zkau.enableMoveable(cmp, null, zkau.onWndMove);
	zkau._overlaps.push(cmp.id); //store ID because it might cease before endPopup
	zkau.hideCovered();
	zk.focusDownById(cmp.id, 0);
};
/** Makes the popup component as normal. */
zkau.endOverlapped = function (uuid) {
	var caption = $(uuid + "!caption");
	if (caption && caption.style.cursor == "move") caption.style.cursor = "";

	zkau._overlaps.remove(uuid);
	zkau.hideCovered();

	var cmp = $(uuid);
	if (cmp) {
		zkau.wndmode[cmp.id] = null;
		zkau.disableMoveable(cmp);
	}
}
/** Makes a window moveable. */
zkau.enableMoveable = function (cmp, starteffect, endeffect) {
	if (cmp) {
		zkau.disableMoveable(cmp);

		var handle = $(cmp.id + "!caption");
		if (handle) {
			cmp.style.position = "absolute"; //just in case
			zkau.initMoveable(cmp, {
				handle: handle,
				starteffect: starteffect || Prototype.emptyFunction,
				change: zkau.hideCovered,
				endeffect: endeffect || Prototype.emptyFunction});
			//we don't use change because it is called too frequently
		}
	}
};

/** Makes a window un-moveable. */
zkau.disableMoveable = function (cmp) {
	if (cmp) zkau.cleanMoveable(cmp.id);
};

/** Make a component moveable (by moving). */
zkau.initMoveable = function (cmp, options) {
	zkau._movs[cmp.id] = new Draggable(cmp, options);
};
/** Undo moveable for a component. */
zkau.cleanMoveable = function (id) {
	if (zkau._movs[id]) {
		zkau._movs[id].destroy();
		zkau._movs[id] = null;
	}
}

/** Called back when overlapped and popup is moved. */
zkau.onWndMove = function (cmp) {
	zkau.send({uuid: cmp.id, cmd: "onMove",
		data: [cmp.style.left, cmp.style.top]},
		zkau.asapTimeout(cmp, "onMove"));
};

zkau.onfocus = function (el) {
	zkau.currentFocus = el; //_onDocMousedown doesn't take care all cases
	if (!zkau.focusInFloats(el)) zkau.closeFloats(el);
	if (zkau.valid) zkau.valid.uncover(el);

	var cmp = zkau.getOuter(el);
	if (cmp.getAttribute("zk_onFocus") == "true")
		zkau.send({uuid: cmp.id, cmd: "onFocus", data: null}, 25);
};
zkau.onblur = function (el) {
	if (el == zkau.currentFocus) zkau.currentFocus = null;
		//Note: _onDocMousedown is called before onblur, so we have to
		//prevent it from being cleared

	var cmp = zkau.getOuter(el);
	if (cmp.getAttribute("zk_onBlur") == "true")
		zkau.send({uuid: cmp.id, cmd: "onBlur", data: null}, 25);
};

zkau.onimgover = function (el) {
	if (el && el.src.indexOf("-off") >= 0)
		el.src = zk.renType(el.src, "on");
};
zkau.onimgout = function (el) {
	if (el && el.src.indexOf("-on") >= 0)
		el.src = zk.renType(el.src, "off");
};

/** Handles document.unload. */
zkau._onDocUnload = function () {
	if (zk.gecko) zk.restoreDisabled(); //Workaround Nav: Bug 1495382

	var content = "dtid="+zk_desktopId+"&cmd.0=rmDesktop";
	var req;
	if (window.ActiveXObject) { //IE
		req = new ActiveXObject("Microsoft.XMLHTTP");
	} else if (window.XMLHttpRequest) { //None-IE
		req = new XMLHttpRequest();
	}

	if (req) {
		try {
			req.open("POST", zk_action, true);
			req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			req.send(content);
		} catch (e) { //silent
		}
	}

	if (zkau._oldDocUnload) zkau._oldDocUnload.apply(document);
};
/** Handle document.onmousedown. */
zkau._onDocMousedown = function (evt) {
	if (!evt) evt = window.event;

	var node = Event.element(evt);
	zkau.currentFocus = node;

	if (!zkau.focusInFloats(node)) zkau.closeFloats(node);

	zkau.autoZIndex(node);
};
/** Handles the left click. */
zkau._onDocLClick = function (evt) {
	if (!evt) evt = window.event;

	if (evt.which == 1 || (evt.button == 0 || evt.button == 1)) {
		var cmp = Event.element(evt);
		cmp = zkau._getParentByAttr(cmp, "zk_lfclk", "zk_pop");
		if (cmp) {
			var ctx = cmp.getAttribute("zk_pop");
			if (ctx) {
				ctx = zkau.getByZid(cmp, ctx);
				if (ctx) {
					var type = zk.getCompType(ctx);
					if (type) {
						zkau.closeFloats(ctx);

						zkau._autopos(ctx, Event.pointerX(evt), Event.pointerY(evt));
						zk.eval(ctx, "context", type, cmp);
					}
				}
			}

			if (cmp.getAttribute("zk_lfclk"))
				zkau.send({uuid: zkau.uuidOf(cmp),
					cmd: "onClick", data: zkau._getMouseData(evt, cmp)});

			//no need to Event.stop
		}
	}
	//don't return anything. Otherwise, it replaces event.returnValue in IE (Bug 1541132)
};
/** Autoposition by the specified (x, y). */
zkau._autopos = function (el, x, y) {
	var ofs = zk.getDimension(el);
	var wd = ofs[0], hgh = ofs[1];

	var scx = zk.innerX(), scy = zk.innerY(),
		scwd = zk.innerWidth(), schgh = zk.innerHeight();
	if (x + wd > scwd) {
		x = scwd - wd;
		if (x < scx) x = scx;
	}
	if (y + hgh > schgh) {
		y = schgh - hgh;
		if (y < scy) y = scy;
	}

	ofs = zk.toStylePos(el, x, y);
	el.style.left = ofs[0] + "px";
	el.style.top = ofs[1] + "px";
};

/** Handles the double click. */
zkau._onDocDClick = function (evt) {
	if (!evt) evt = window.event;

	var cmp = Event.element(evt);
	cmp = zkau._getParentByAttr(cmp, "zk_dbclk");
	if (cmp) {
		var uuid = cmp.getAttribute("zk_item"); //treerow (and other transparent)
		if (!uuid) uuid = zkau.uuidOf(cmp);
		zkau.send({uuid: uuid,
			cmd: "onDoubleClick", data: zkau._getMouseData(evt, cmp)});
		//no need to Event.stop
	}
};
/** Handles the right click (context menu). */
zkau._onDocCtxMnu = function (evt) {
	if (!evt) evt = window.event;

	var cmp = Event.element(evt);
	cmp = zkau._getParentByAttr(cmp, "zk_ctx", "zk_rtclk");

	if (cmp) {
		var ctx = cmp.getAttribute("zk_ctx");
		if (ctx) {
			ctx = zkau.getByZid(cmp, ctx);
			if (ctx) {
				var type = zk.getCompType(ctx);
				if (type) {
					zkau.closeFloats(ctx);

					zkau._autopos(ctx, Event.pointerX(evt), Event.pointerY(evt));
					zk.eval(ctx, "context", type, cmp);
				}
			}
		}

		if (cmp.getAttribute("zk_rtclk")) {
			var uuid = cmp.getAttribute("zk_item"); //treerow (and other transparent)
			if (!uuid) uuid = zkau.uuidOf(cmp);
			zkau.send({uuid: uuid,
				cmd: "onRightClick", data: zkau._getMouseData(evt, cmp)});
		}

		Event.stop(evt);
		return false;
	}
	return !zk.ie || evt.returnValue;
};
zkau._onDocMouseover = function (evt) {
	if (!evt) evt = window.event;

	var cmp = Event.element(evt);
	cmp = zkau._getParentByAttr(cmp, "zk_tip");
	if (cmp) {
		var tip = cmp.getAttribute("zk_tip");
		tip = zkau.getByZid(cmp, tip);
		if (tip) {
			if (!zkau._tipz || zkau._tipz.cmdId != cmp.id) {
				var open = zkau._tipz && zkau._tipz.open;
				zkau._tipz = {
					tipId: tip.id, cmpId: cmp.id,
					x: Event.pointerX(evt),
					y: Event.pointerY(evt)
				};
				if (open) zkau._openTip(cmp.id);
				else setTimeout("zkau._openTip('"+cmp.id+"')", 800);
			}
			return; //done
		}
	}
	if (zkau._tipz) {
		if (zkau._tipz.open) {
			var tip = $(zkau._tipz.tipId);
			if (tip && zk.isAncestor(tip, Event.element(evt))) {
				zkau._tipz.shallClose = false; //don't close it
			} else {
				zkau._tipz.shallClose = true;
				setTimeout(zkau._tryCloseTip, 300);
			}
		} else
			zkau._tipz = null;
	}
};
zkau._onDocMouseout = function (evt) {
	if (!evt) evt = window.event;

	if (zkau._tipz)
		if (zkau._tipz.open) {
			zkau._tipz.shallClose = true;
			setTimeout(zkau._tryCloseTip, 300);
		} else
			zkau._tipz = null;
};
zkau._openTip = function (cmpId) {
	//We have to filter out non-matched cmpId because user might move
	//from one component to another
	if (zkau._tipz && !zkau._tipz.open
	 && (!cmpId || cmpId == zkau._tipz.cmpId)) {
		var tip = $(zkau._tipz.tipId);
		zkau.closeFloats(tip);
		if (tip) {
			var cmp = $(cmpId);
			zkau._tipz.open = true;
			zkau._autopos(tip, zkau._tipz.x, zkau._tipz.y);
			zk.eval(tip, "context", null, cmp);
		} else {
			zkau._tipz = null;
		}
	}
};
/** Closes tooltip if _tipz.shallClose is set. */
zkau._tryCloseTip = function () {
	if (zkau._tipz && zkau._tipz.shallClose) {
		if (zkau._tipz.open) zkau.closeFloats();
		zkau._tipz = null;
	}
};

/** Returns the target of right-click, or null if not found. */
zkau._getParentByAttr = function (n, attr1, attr2) {
	for (; n; n = n.parentNode) {
		if (n.getAttribute) {
			if (attr1 && n.getAttribute(attr1)) return n;
			if (attr2 && n.getAttribute(attr2)) return n;
		}
	}
	return null;
};

/** Handles document.onkeydown. */
zkau._onDocKeydown = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var attrSkip, evtnm;
	switch (evt.keyCode) {
	case 13: //ENTER
		var tn = zk.tagName(target);
		if (tn == "TEXTAREA" || tn == "BUTTON"
		|| (tn == "INPUT" && target.type.toLowerCase() == "button"))
			return true; //don't change button's behavior (Bug 1556836)
	case 27: //ESC
		if (zkau.closeFloats(target)) {
			Event.stop(evt);
			return false; //eat
		}
		if (evt.keyCode == 13) {
			attrSkip = "zk_skipOK"; evtnm = "onOK";
		} else {
			attrSkip = "zk_skipCancel"; evtnm = "onCancel";
		}
		break;
	case 16: //Shift
	case 17: //Ctrl
	case 18: //Alt
		return true;
	default:
		//F1: 112, F12: 123
		if (evt.ctrlKey || (evt.keyCode >= 112 && evt.keyCode <= 123)) {
			evtnm = "onCtrlKey";
			break;
		}
		return true;
	}

	for (var n = target; n; n = n.parentNode) {
		if (n.id && n.getAttribute) {
			if (n.getAttribute("zk_" + evtnm) == "true"
			&& (evtnm != "onCtrlKey"
			|| zkau._isInCtrlKeys(evt.keyCode, n.getAttribute("zk_ctkeys")))) {
				var bSend = true;
				if (zkau.currentFocus) {
					var inp = zkau.currentFocus;
					switch (zk.tagName(inp)) {
					case "INPUT":
						var type = inp.type.toLowerCase();
						if (type != "text" && type != "password")
							break; //ignore it
						//fall thru
					case "TEXTAREA":
						bSend = zkau.textbox && zkau.textbox.updateChange(inp, false);
					}
				}

				zkau.send({uuid: n.id, cmd: evtnm,
					data: [evt.keyCode, evt.ctrlKey, evt.shiftKey, evt.altKey]},
					25);
				Event.stop(evt);
				return false;
			}
			if ("onCancel" == evtnm && zk.getCompType(n) == "Wnd") {
				if (n.getAttribute("zk_closable") == "true") {
					zkau.close(n);
					Event.stop(evt);
					return false;
					//20060504: Bug 1481676: Tom Yeh
					//We have to stop ESC event. Otherwise, it terminates
					//Ajax connection (issue by close)
				}
				break;
			}
			if (attrSkip && n.getAttribute(attrSkip) == "true")
				break; //nothing to do
		}
	}
	return true; //no special processing
}
/** returns whether a key code is specified in keys. */
zkau._isInCtrlKeys = function (code, keys) {
	if (keys) {
		for (var j = 0; j < keys.length; ++j) {
			var cc = keys.charAt(j);
			var v = keys.charCodeAt(j);
			if (cc == '0') v = 121; //F10
			else if (cc >= '1' && cc <= '9') v += 112 - "1".charCodeAt(0);

			if (code == v) return true;
		}
	}
	return false;
};

/** Whether the focus is in the same component. */
zkau.focusInFloats = function (target) {
	for (var j = 0; j < zkau.floats.length; ++j)
		if (zkau.floats[j].focusInFloats(target))
			return true;

	for (var j = 0; j < zkau._popups.length; ++j) {
		var el = $(zkau._popups[j]);
		if (el != null && target != null && zk.isAncestor(el, target))
			return true;
	}
	return false;
};

zkau.close = function (uuid) {
	el = $(uuid);
	zkau.send({uuid: el.id, cmd: "onClose", data: null}, 5);
};
zkau.hide = function (uuid) {
	var el = $(uuid);
	if (el) el.style.display = "none";
	zkau.send({uuid: el.id, cmd: "onShow", data: ["false"]},
		zkau.asapTimeout(el, "onShow"));
};

/** Closes popups and floats. Return false if nothing changed. */
zkau.closeFloats = function (owner) {
	owner = $(owner);
	var closed, popups = new Array();
	for (;;) {
		//reverse order is important if popup contains another
		//otherwise, IE might have bug to handle them correctly
		var uuid = zkau._popups.pop();
		if (!uuid) break;

		if (zk.isAncestor($(uuid), owner)) {
			popups.push(uuid);
		} else {
			closed = true;
			zkau.hide(uuid);
		}
	}
	zkau._popups = popups;

	for (var j = 0; j < zkau.floats.length; ++j)
		if (zkau.floats[j].closeFloats()) //combobox popup
			closed = true;

	if (closed)
		zkau.hideCovered();
	return closed;
};
zkau.hideCovered = function() {
	if (!zk.ie) return; //nothing to do

	var ary = new Array();
	for (var j = 0; j < zkau._popups.length; ++j) {
		var el = $(zkau._popups[j]);
		if (el) ary.push(el);
	}

	for (var j = 0; j < zkau.floats.length; ++j)
		zkau.floats[j].addHideCovered(ary);

	for (var j = 0; j < zkau._overlaps.length; ++j) {
		var el = $(zkau._overlaps[j]);
		if (el) ary.push(el);
	}
	zk.hideCovered(ary);

	if (zkau.valid) zkau.valid.uncover();
};

/** Returns the meta info associated with the specified cmp or its UUID.
 */
zkau.getMeta = function (cmp) {
	var id = typeof cmp == 'string' ? cmp: cmp ? cmp.id: null;
	if (!id) return null;
	return zkau._metas[zkau.uuidOf(id)];
};
/** Returns the meta info associated with the specified cmp or its UUID.
 */
zkau.setMeta = function (cmp, info) {
	var id = typeof cmp == 'string' ? cmp: cmp ? cmp.id: null;
	if (!id) {
		zk.error(mesg.COMP_OR_UUID_REQUIRED);
		return;
	}
	zkau._metas[zkau.uuidOf(id)] = info;
};
/** Returns the info by specified any child component and the type.
 */
zkau.getMetaByType = function (el, type) {
	el = zkau.getParentByType(el, type);
	return el != null ? zkau.getMeta(el): null;
};

/** Cleans up meta of the specified component. */
zkau.cleanupMeta = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) {
		if (meta.cleanup) meta.cleanup();
		zkau.setMeta(cmp, null);
	}
};

/** Returns the parent node with the specified type.
 */
zkau.getParentByType = function (el, type) {
	for (; el; el = el.parentNode)
		if (zk.getCompType(el) == type)
			return el;
	return null;
};

////
//ID Space//
/** Returns element of the specified zid. */
zkau.getByZid = function (n, zid) {
	while (n) {
		n = zkau.getIdOwner(n);
		var v = zkau._idsp[n ? n.id: "zk_dksp"];
		if (v) {
			v = v[zid];
			if (v) return $(v);
		}
		if (n) n = n.parentNode;
	}
	return null;
};
/** Returns the space owner that n belongs to, or null if not found. */
zkau.getIdOwner = function (n) {
	for (; n; n = n.parentNode) {
		if (n.getAttribute && n.getAttribute("zk_idsp"))
			return n;
	}
	return null;
};
zkau.initzid = function (n, zid) {
	var o = zkau.getIdOwner(n);
	o = o ? o.id: "zk_dksp";
	var ary = zkau._idsp[o];
	if (!ary) ary = zkau._idsp[o] = {};
	if (!zid) zid = n.getAttribute("zid");
	ary[zid] = n.id;
};
zkau.cleanzid = function (n) {
	var o = zkau.getIdOwner(n);
	o = o ? o.id: "zk_dksp";
	var ary = zkau._idsp[o];
	if (ary) ary[n.getAttribute("zid")] = null;
};
/** Clean an ID space. */
zkau.cleanidsp = function (n) {
	zkau._idsp[n.id] = null;
};

///////////////
//Drag & Drop//
zkau.initdrag = function (n) {
	zkau._drags[n.id] = new Draggable(n, {
		revert: zkau._revertdrag,
		starteffect: Prototype.emptyFunction,
		endeffect: zkau._enddrag, change: zkau._dragging,
		ghosting: zkau._ghostdrag, z_dragdrop: true
	});
	if (zk.ie) {
		//disable onselect
		var tn = zk.tagName(n);
		var nosel;
		if (tn == "INPUT") {
			var t = n.type.toLowerCase();
			nosel = t != "text" && t != "password";
		} else {
			nosel = tn != "TEXTAREA";
		}
		if (nosel) n.onselectstart = function () {return false;}
	}
};
zkau.cleandrag = function (n) {
	if (zkau._drags[n.id]) {
		n.onselectstart = null;
		zkau._drags[n.id].destroy();
		zkau._drags[n.id] = null;
	}
};
zkau.initdrop = function (n) {
	zkau._drops.push(n);
};
zkau.cleandrop = function (n) {
	zkau._drops.remove(n);
};

zkau._dragging = function (dg, pointer) {
	var e = zkau._getDrop(dg.element, pointer);
	if (!e || e != dg.zk_lastDrop) {
		zkau._cleanLastDrop(dg);
		if (e) {
			dg.zk_lastDrop = e;
			dg.zk_lastDropBkc = e.style.backgroundColor;
			e.style.backgroundColor = "#A8A858";
		}
	}
};
zkau._revertdrag = function (n, pointer) {
	if (zk.gecko || zkau._getDrop(n, pointer) == null)
		return true;

	var dg = zkau._drags[n.id];
	zkau._revertpending = function() {
		n.style.left = dg.z_x;
		n.style.top = dg.z_y;
		zkau._revertpending = null; //exec once
	};
	return false;
};
zkau._enddrag = function (n, pointer) {
	zkau._cleanLastDrop(zkau._drags[n.id]);
	var e = zkau._getDrop(n, pointer);
	if (e) setTimeout("zkau._sendDrop('"+n.id+"','"+e.id+"')", 50);
		//In IE, listitem is selected after _enddrag, so we have to
		//delay the sending of onDrop
};
zkau._sendDrop = function (dragged, dropped) {
	zkau.send({uuid: dropped, cmd: "onDrop", data: [dragged]});
};
zkau._getDrop = function (n, pointer) {
	var dragType = n.getAttribute("zk_drag");
	l_next:
	for (var j = 0; j < zkau._drops.length; ++j) {
		var e = zkau._drops[j];
		if (e == n) continue; //dropping to itself not allowed

		var dropTypes = e.getAttribute("zk_drop");
		if (dropTypes != "true") { //accept all
			if (dragType == "true") continue; //anonymous drag type

			for (var k = 0;;) {
				var l = dropTypes.indexOf(',', k);
				var s = l >= 0 ? dropTypes.substring(k, l): dropTypes.substring(k);
				if (s.trim() == dragType) break; //found
				if (l < 0) continue l_next;
				k = l + 1;
			}
		}
		if (Position.withinIncludingScrolloffsets(e, pointer[0], pointer[1]))
			return e;
	}
	return null;
};
zkau._cleanLastDrop = function (dg) {
	if (dg.zk_lastDrop) {
		dg.zk_lastDrop.style.backgroundColor = dg.zk_lastDropBkc;
		dg.zk_lastDrop = null;
	}
};
zkau._ghostdrag = function (dg, ghosting) {
//Tom Yeh: 20060227: Use a 'fake' DIV if
//1) FF cannot handle z-index well if listitem is dragged across two listboxes
//2) Safari's ghosting position is wrong
	var special;
	if (zk.gecko || zk.safari) {
		if (ghosting) {
			var tn = zk.tagName(dg.element);
			zk.zk_special = special = "TR" == tn || "TD" == tn || "TH" == tn;
		} else {
			special = zk.zk_special;
		}
	}

	if (special) {
		if (ghosting) {
			zk.dragging = true;
			dg.delta = dg.currentDelta();

			//we have to store scrolling offset first since Draggable.draw cannot
			//calculate from the DIV
			dg.z_scrl = Position.realOffset(dg.element);
			var pos = Position.cumulativeOffset(dg.element);

			pos[0] -= dg.z_scrl[0]; pos[1] -= dg.z_scrl[1];
			document.body.insertAdjacentHTML("afterbegin",
				'<div id="zk_ddghost" style="position:absolute;top:'
				+pos[1]+'px;left:'+pos[0]+'px;width:'
				+zk.offsetWidth(dg.element)+'px;height:'+zk.offsetHeight(dg.element)
				+'px;border:1px dotted black">&nbsp;</div>');

			dg.zk_old = dg.element;
			dg.element = $("zk_ddghost");
		} else if (dg.zk_old) {
			zk.dragging = false;
			Element.remove(dg.element);
			dg.element = dg.zk_old;
			dg.zk_old = null;
		}
		return false
	}

	if (ghosting) {
		zk.dragging = true;
		dg.delta = dg.currentDelta();
			//dragdrop.js cache left/top, but we might change it in other way
		dg.z_x = dg.element.style.left; dg.z_y = dg.element.style.top;
		zkau._revertpending = null;
	} else {
		zk.dragging = false;
	}
	return true;
};

//////////////
/// ACTION ///
function action() {}

/** Makes a component visible.
 * @param bShow false means hide; otherwise (including undefined) is show
 */
action.show = function (id, bShow) {
	if (bShow == false) action.hide(id);
	else {
		var n = $(id);
		if (n) {
			n.style.display = "";
			zkau.onVisiAt(n); //callback later
		}
	}
};

/** Makes a component invisible.
 * @param bHide false means show; otherwise (including undefined) is hide
 */
action.hide = function (id, bHide) {
	if (bHide == false) action.show(id);
	else {
		var n = $(id);
		if (n) {
			zkau.onHideAt(n); //callback first
			n.style.display = "none";
		}
	}
};

/** Slides down a component.
 * @param down false means slide-up; otherwise (including undefined) is slide-down
 */
action.slideDown = function (id, down) {
	if (down == false) action.slideUp(id);
	else {
		var n = $(id);
		if (n && (!n.getAttribute || !n.getAttribute("zk_visible"))) {
			if (n.setAttribute) n.setAttribute("zk_visible", "showing");
			Effect.SlideDown(n, {duration:0.4, afterFinish: action._afterDown});
		}
	}
};
action._afterDown = function (ef) {
	var n = ef.element;
	if (n) {
		if (n.setAttribute) n.removeAttribute("zk_visible");
		zkau.onVisiAt(n);
	}
};

/** Slides down a component.
 * @param up false means slide-down; otherwise (including undefined) is slide-up
 */
action.slideUp = function (id, up) {
	if (up == false) action.slideDown(id);
	else {
		var n = $(id);
		if (n && (!n.getAttribute || !n.getAttribute("zk_visible"))) {
			if (n.setAttribute) n.setAttribute("zk_visible", "hiding");
			zkau.onHideAt(n); //callback first
			Effect.SlideUp(n, {duration:0.4, afterFinish: action._afterUp});
		}
	}
};
action._afterUp = function (ef) {
	var n = ef.element;
	if (n && n.setAttribute) n.removeAttribute("zk_visible");
};

/*Float: used to be added to zkau.floats
	Derives must provide an implementation of _close(el).
*/
zk.Float = Class.create();
zk.Float.prototype = {
	initialize: function () {
	},
	/** Whether the mousedown event shall be ignored for the specified
	 * event.
	 */
	focusInFloats: function (el) {
		var uuid = zkau.uuidOf(this._popupId);
		if (el != null && this._popupId != null) {
			if (zkau.uuidOf(el) == uuid)
				return true;
			var popup = $(this._popupId);
			return popup && zk.isAncestor(popup, el);
		}
		return false;
	},
	/** Closes (hides) all menus. */
	closeFloats: function() {
		if (this._popupId) {
			var el = $(this._popupId);
			if (el) this._close(el);
			return true;
		}
		return false;
	},
	/** Adds elements that we have to hide what they covers.
	 */
	addHideCovered: function (ary) {
		if (this._popupId) {
			var el = $(this._popupId);
			if (el) ary.push(el);
		}
	}
};

//Histroy//
zk.History = Class.create();
zk.History.prototype = {
	initialize: function () {
		this.curbk = "";
		setInterval("zkau.history.checkBookmark()", 520);
			//Though IE use history.html, timer is still required 
			//because user might specify URL directly
	},
	/** Sets a bookmark that user can use forward and back buttons */
	bookmark: function (nm) {
		if (this.curbk != nm) {
			this.curbk = nm; //to avoid loop back the server
			var encnm = encodeURIComponent(nm);
			window.location.hash = zk.safari ? encnm: '#' + encnm;
			if (zk.ie /*|| zk.safari*/) this.bkIframe(nm);
		}
	},
	/** Checks whether the bookmark is changed. */
	checkBookmark: function() {
		var nm = this.getBookmark();
		if (nm != this.curbk) {
			this.curbk = nm;
			zkau.send({uuid: '', cmd: "onBookmarkChanged", data: [nm]}, 25);
		}
	},
	getBookmark: function () {
		var nm = window.location.hash;
		var j = nm.indexOf('#');
		return j >= 0 ? decodeURIComponent(nm.substring(j + 1)): '';
	}
};
if (zk.ie /*|| zk.safari*/) {
	/** bookmark iframe */
	zk.History.prototype.bkIframe = function (nm) {
		var url = zk.getUpdateURI("/web/js/zk/html/history.html", true);
		if (nm) url += '?' +encodeURIComponent(nm);

		var ifr = $('zk_histy');
		if (ifr) {
			ifr.src = url;
		} else {
			zk.newFrame('zk_histy', url,
				/*zk.safari ? "width:0;height:0;display:inline":*/ "display:none");
		}
	};
	/** called when history.html is loaded*/
	zk.History.prototype.onHistoryLoaded = function (src) {
		var j = src.indexOf('?');
		var nm = j >= 0 ? src.substring(j + 1): '';
		window.location.hash = nm ? /*zk.safari ? nm:*/ '#' + nm: '';
		this.checkBookmark();
	};
}

zkau.history = new zk.History();

//Upload//
zkau.beginUpload = function () {
	zkau.endUpload();
	zkau._tmupload = setInterval(function () {
		zkau.send({uuid: '', cmd: "getUploadInfo", data: null});
	}, 660);
};
zkau.updateUploadInfo = function (p, cb) {
	if (cb <= 0) zkau.endUpload();
	else if (zkau._tmupload) {
		var img = $("zk_upload!img");
		if (!img) {
			var html = '<div id="zk_upload" style="position:absolute;border:1px solid #77a;padding:9px;background-color:#fec;z-index:79000">'
				+'<div style="width:102px;border:1px inset"><img id="zk_upload!img" src="'+zk.getUpdateURI('/web/zk/img/prgmeter.gif')
				+'"/></div><br/>'+mesg.FILE_SIZE+Math.round(cb/1024)+mesg.KBYTES+'</div>';
			document.body.insertAdjacentHTML("afterbegin", html);
			zk.center($("zk_upload"));
			img = $("zk_upload!img");
		}
		if (p >= 0 && p <= 100) {
			img.style.height = "10px"; //avoid being scaled when setting width
			img.style.width = p + "px";
		}
	}
};
zkau.endUpload = function () {
	var div = $("zk_upload");
	if (div) Element.remove(div);
	if (zkau._tmupload) {
		clearInterval(zkau._tmupload);
		zkau._tmupload = null;
	}
};

//Commands//
zkau.cmd0 = { //no uuid at all
	bookmark: function (dt0) {
		zkau.history.bookmark(dt0);
	},
	obsolete: function (dt0, dt1) { //desktop timeout
		if (dt0 == zk_desktopId) //just in case
			zkau._cleanupOnFatal();
		zk.error(dt1);
	},
	alert: function (dt0, dt1) {
		var cmp = dt0 ? $(dt0): null;
		if (cmp) {
			cmp = zkau.getReal(cmp); //refer to INPUT (e.g., datebox)
			if (zkau.valid) zkau.valid.errbox(cmp.id, dt1);
		} else {
			alert(dt1);
		}
	},
	redirect: function (dt0, dt1) {
		if (dt1) zk.go(dt0, false, dt1);
		else document.location.href = dt0;
	},
	title: function (dt0) {
		document.title = dt0;
	},
	script: function (dt0) {
		eval(dt0);
	},
	echo: function () {
		zkau.send({uuid: "", cmd: "dummy", data: null});
	},
	clientInfo: function () {
		zkau.send({uuid: "", cmd: "onClientInfo", data: [
			new Date().getTimezoneOffset(),
			screen.width, screen.height, screen.colorDepth,
			zk.innerWidth(), zk.innerHeight(), zk.innerX(), zk.innerY()
		]});
	},
	print: function () {
		window.print();
	},
	scrollBy: function (x, y) {
		window.scrollBy(x, y);
	},
	scrollTo: function (x, y) {
		window.scrollTo(x, y);
	},
	resizeBy: function (x, y) {
		window.resizeBy(x, y);
	},
	resizeTo: function (x, y) {
		window.resizeTo(x, y);
	},
	moveBy: function (x, y) {
		window.moveBy(x, y);
	},
	moveTo: function (x, y) {
		window.moveTo(x, y);
	}
};
zkau.cmd1 = {
	setAttr: function (uuid, cmp, dt1, dt2) {
		if (dt1 == "zk_init" || dt1 == "zk_chchg") { //initialize
			//Note: cmp might be null because it might be removed
			if (cmp) {
				var type = zk.getCompType(cmp);
				if (type) {
					zk.loadByType(cmp);
					if (zk.loading) {
						var cmps = new Array();
						cmps.push(cmp);
						zk.addInitCmps(cmps);
					} else {
						zk.eval(cmp, dt1 == "zk_init" ? "init": "childchg", type);
					}
				}
			}
			return; //done
		}

		var done = false;
		if ("zk_drag" == dt1) {
			if (!cmp.getAttribute("zk_drag")) zkau.initdrag(cmp);
			zkau.setAttr(cmp, dt1, dt2);
			done = true;
		} else if ("zk_drop" == dt1) {
			if (!cmp.getAttribute("zk_drop")) zkau.initdrop(cmp);
			zkau.setAttr(cmp, dt1, dt2);
			done = true;
		} else if ("zid" == dt1) {
			zkau.cleanzid(cmp);
			if (dt2) zkau.initzid(cmp, dt2);
		}

		if (zk.eval(cmp, "setAttr", null, dt1, dt2)) //NOTE: cmp is NOT converted to real!
			return; //done

		if (!done) {
			if (dt1.startsWith("on")) cmp = zkau.getReal(cmp);
				//Client-side-action must be done at the inner tag

			zkau.setAttr(cmp, dt1, dt2);
		}
	},
	rmAttr: function (uuid, cmp, dt1) {
		var done = false;
		if ("zk_drag" == dt1) {
			zkau.cleandrag(cmp);
			zkau.rmAttr(cmp, dt1);
			done = true;
		} else if ("zk_drop" == dt1) {
			zkau.cleandrop(cmp);
			zkau.rmAttr(cmp, dt1);
			done = true;
		}

		if (zk.eval(cmp, "rmAttr", null, dt1)) //NOTE: cmp is NOT converted to real!
			return; //done

		if (!done) {
			if (dt1.startsWith("on")) cmp = zkau.getReal(cmp);
				//Client-side-action must be done at the inner tag
			zkau.rmAttr(cmp, dt1);
		}
	},
	outer: function (uuid, cmp, dt1) {
		zk.eval(cmp, "beforeOuter");
		zk.cleanupAt(cmp, zkau.cleanupMeta);
		zk.setOuterHTML(cmp, dt1);
		cmp = $(uuid);
		zk.initAt(cmp);
		zk.eval(cmp, "afterOuter");
		if (zkau.valid) zkau.valid.fixerrboxes();
	},
/* 20060907: Tom M. Yeh: abandon inner to reduce the complexity
	inner: function (uuid, cmp, dt1) {
		zkau._cleanupChildren(cmp);
		zk.setInnerHTML(cmp, dt1);
		zk.eval(cmp, "initInner");
		zkau._initChildren(cmp);
		if (zkau.valid) zkau.valid.fixerrboxes();
	},
*/
	addAft: function (uuid, cmp, dt1) {
		var n = zkau._getChildExterior(cmp);
		var to = n.nextSibling;
		zk.insertHTMLAfter(n, dt1);
		zkau._initSibs(n, to, true);
	},
	addBfr: function (uuid, cmp, dt1) {
		var n = zkau._getChildExterior(cmp);
		var to = n.previousSibling;
		zk.insertHTMLBefore(n, dt1);
		zkau._initSibs(n, to, false);
	},
	addChd: function (uuid, cmp, dt1) {
		/* To add the first child properly, it checks as follows.
		//1) a function called addFirstChild
		2) uuid + "!cave" (as parent)
		3) an attribute called zk_cave to hold id (as parent)
		4) uuid + "!child" (as next sibling)
		5) uuid + "!real" (as parent)
		 */
		//if (zk.eval(cmp, "addFirstChild", dt1))
		//	return;

		var n = $(uuid + "!cave");
		if (!n) {
			n = cmp.getAttribute("zk_cave");
			if (n) n = $(n);
		}
		if (n) { //as last child of n
			zkau._insertAndInitBeforeEnd(n, dt1);
			return;
		}

		n = $(uuid + "!child");
		if (n) { //as previous sibling of n
			var to = n.previousSibling;
			zk.insertHTMLBefore(n, dt1);
			zkau._initSibs(n, to, false);
			return;
		}

		cmp = zkau.getReal(cmp); //go into the real tag (e.g., tabpanel)
		zkau._insertAndInitBeforeEnd(cmp, dt1);
	},
	rm: function (uuid, cmp) {
		//NOTE: it is possible the server asking removing a non-exist cmp
		//so keep silent if not found
		if (cmp) {
			zk.cleanupAt(cmp, zkau.cleanupMeta);
			cmp = zkau._getChildExterior(cmp);
			Element.remove(cmp);
		}
		if (zkau.valid) zkau.valid.fixerrboxes();
	},
	focus: function (uuid, cmp) {
		if (!zk.eval(cmp, "focus")) {
			cmp = zkau.getReal(cmp); //focus goes to inner tag
			if (cmp.focus && !cmp.disabled)
				zk.focusById(cmp.id, 5);
					//delay it because focusDownById might be called implicitly
		}
	},
	selAll: function (uuid, cmp) {
		cmp = zkau.getReal(cmp); //select goes to inner tag
		if (cmp.select)
			zk.selectById(cmp.id);
	},
	doPop: function (uuid, cmp) {
		zkau.doPopup(cmp);
	},
	endPop: function (uuid, cmp) {
		zkau.endPopup(uuid);
	},
	doOvl: function (uuid, cmp) {
		zkau.doOverlapped(cmp);
	},
	endOvl: function (uuid, cmp) {
		zkau.endOverlapped(uuid);
	},
	meta: function (uuid, cmp, dt1, dt2, dt3, dt4) {
		var meta = zkau.getMeta(uuid);
		if (meta) meta[dt1].call(meta, dt2, dt3, dt4);
	},
	closeErrbox: function (uuid, cmp) {
		if (zkau.valid) {
			zkau.valid.closeErrbox(uuid);
			zkau.valid.closeErrbox(uuid + "!real");
		}
	},
	submit: function (uuid, cmp) {
		setTimeout(function (){if (cmp && cmp.submit) cmp.submit();}, 50);
	}
};
