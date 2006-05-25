/* au.js

{{IS_NOTE
	$Id: au.js,v 1.52 2006/05/25 10:26:27 tomyeh Exp $
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
	zkau._stamp = 0; //used to make a time stamp
	zkau.floats = new Array(); //popup of combobox, bandbox, datebox...

	zk.addInit(function () {
		Event.observe(document, "mousedown", zkau._onDocMousedown);
		Event.observe(document, "keydown", zkau._onDocKeydown);
		window.onunload = zkau._onDocUnload; //unable to use Event.observe
	});
}

/** Handles onclick. */
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

	var ofs = Position.cumulativeOffset(target);
	var x = Event.pointerX(evt) - ofs[0];
	var y = Event.pointerY(evt) - ofs[1];
	zkau.send({uuid: zkau.uuidOf(target.id), cmd: "onClick", data: [x, y]});
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

/** Processes the response.
 */
/** Sens a request to the client and queue it to zkau._reqs.
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
zkau._sendNow = function () {
	if (!zk_action) {
		alert(mesg.NOT_FOUND+"zk_action");
		return;
	}
	if (!zk_desktopId) {
		alert(mesg.NOT_FOUND+"zk_desktopId");
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

	content += "&dtid="+zk_desktopId;
	content = content.substring(1);
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
			//zkau._lastSend = new Date();
			if (!implicit) zk.progress(900); //wait a moment to avoid to annoying
		} catch (e) {
			alert(mesg.FAILED_TO_SEND+zk_action+"\n"+content+"\n"+e.message);
		}
	} else {
		alert(mesg.FAILED_TO_SEND+zk_action+"\n"+content);
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
	if ("obsolete" == cmd) { //desktop timeout
		if (dt0 == zk_desktopId) //just in case
			zkau._cleanupOnFatal();
		alert(dt1);
		return;
	} else if ("alert" == cmd) {
		if (zkau._checkResponse(cmd, null, null, 2, datanum)) {
			var cmp = dt0 ? $(dt0): null;
			if (cmp) {
				cmp = zkau.getReal(cmp); //refer to INPUT (e.g., datebox)
				if (zkau.valid) zkau.valid.errbox(cmp.id, dt1);
			} else {
				alert(dt1);
			}
		}
		return;
	} else if ("redirect" == cmd) {
		if (dt1) zk.go(dt0, false, dt1);
		else document.location.href = dt0;
		return;
	} else if ("title" == cmd) {
		document.title = dt0;
		return;
	} else if ("script" == cmd) {
		eval(dt0);
		return;
	} else if ("echo" == cmd) {
		zkau.send({uuid: "", cmd: "dummy", data: null});
		return;
	} else if ("print" == cmd) {
		window.print();
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

	if ("setAttr" == cmd) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 3, datanum))
			return;

		var done = false;
		if ("zk_drag" == dt1) {
			if (!cmp.getAttribute("zk_drag")) zkau.initdrag(cmp);
			zkau.setAttr(cmp, dt1, dt2);
			done = true;
		} else if ("zk_drop" == dt1) {
			if (!cmp.getAttribute("zk_drop")) zkau.initdrop(cmp);
			zkau.setAttr(cmp, dt1, dt2);
			done = true;
		}

		var type = zk.getCompType(cmp);
		if (type) { //NOTE: cmp is NOT converted to real!
			var fn = "zk"+type+".setAttr";
			if (eval(fn+"&&"+fn+"(cmp, dt1, dt2)"))
				return; //done
		}

		if (!done) {
			if (dt1.startsWith("on")) cmp = zkau.getReal(cmp);
				//Client-side-action must be done at the inner tag
			zkau.setAttr(cmp, dt1, dt2);
		}
	} else if ("rmAttr" == cmd) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 2, datanum))
			return;

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

		var type = zk.getCompType(cmp);
		if (type) { //NOTE: cmp is NOT converted to real!
			var fn = "zk"+type+".rmAttr";
			if (eval(fn+"&&"+fn+"(cmp, dt1)"))
				return; //done
		}

		if (!done) {
			if (dt1.startsWith("on")) cmp = zkau.getReal(cmp);
				//Client-side-action must be done at the inner tag
			zkau.rmAttr(cmp, dt1);
		}
	} else if ("init" == cmd) {
		//Note: cmp might be null because it might be removed
		if (!cmp || !zkau._checkResponse(cmd, uuid, cmp, 1, datanum))
			return;

		var type = zk.getCompType(cmp);
		if (type) {
			zk.loadByType(cmp);
			if (zk.loading) {
				var cmps = new Array();
				cmps.push(cmp);
				zk.addInitCmps(cmps);
			} else {
				eval("zk"+type+".init(cmp)");
			}
		}
	} else if ("cleanup" == cmd) {
		if (!zkau._checkResponse(cmd, null, null, 1, datanum))
			return;
		zkau.cleanupMeta(uuid);
	} else if ("outer" == cmd) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 2, datanum))
			return;

		zk.cleanupAt(cmp, zkau.cleanupMeta);
		zk.setOuterHTML(cmp, dt1);
		zk.initAt($(uuid));
		if (zkau.valid) zkau.valid.fixerrboxes();
	} else if ("inner" == cmd) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 2, datanum))
			return;

		zkau._cleanupChildren(cmp);
		zk.setInnerHTML(cmp, dt1);
		zkau._initInner(cmp);
		zkau._initChildren(cmp);
		if (zkau.valid) zkau.valid.fixerrboxes();
	} else if ("addAft" == cmd) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 2, datanum))
			return;

		var n = zkau._getChildExterior(cmp);
		var to = n.nextSibling;
		zk.insertHTMLAfter(n, dt1);
		zkau._initSibs(n, to, true);
	} else if ("addBfr" == cmd) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 2, datanum))
			return;

		var n = zkau._getChildExterior(cmp);
		var to = n.previousSibling;
		zk.insertHTMLBefore(n, dt1);
		zkau._initSibs(n, to, false);
	} else if ("addChd" == cmd){
		if (!zkau._checkResponse(cmd, uuid, cmp, 2, datanum))
			return;

		var n = $(uuid + "!child");
		if (n) {
			var to = n.previousSibling;
			zk.insertHTMLBefore(n, dt1);
			zkau._initSibs(n, to, false);
		} else {
			cmp = zkau.getReal(cmp); //go into the real tag (e.g., tabpanel)
			var n = cmp.lastChild;
			zk.insertHTMLBeforeEnd(cmp, dt1);
			if (n) zkau._initSibs(n, null, true);
			else zkau._initChildren(cmp);
		}
	} else if ("rm" == cmd) {
		if (!zkau._checkResponse(cmd, null, null, 1, datanum))
			return;

		//NOTE: it is possible the server asking removing a non-exist cmp
		//so keep silent if not found
		if (cmp) {
			zk.cleanupAt(cmp, zkau.cleanupMeta);
			cmp = zkau._getChildExterior(cmp);
			cmp.parentNode.removeChild(cmp);
		}
		if (zkau.valid) zkau.valid.fixerrboxes();
	} else if ("focus" == cmd) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 1, datanum))
			return;
		cmp = zkau.getReal(cmp); //focus goes to inner tag
		if (cmp.focus && !cmp.disabled)
			zk.focusById(cmp.id, 5);
				//delay it because focusDownById might be called implicitly
	} else if ("selAll" == cmd) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 1, datanum))
			return;
		cmp = zkau.getReal(cmp); //select goes to inner tag
		if (cmp.select)
			zk.selectById(cmp.id);
	} else if ("doPop" == cmd ) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 1, datanum))
			return;
		zkau.doPopup(cmp);
	} else if ("endPop" == cmd) {
		if (!zkau._checkResponse(cmd, null, null, 1, datanum))
			return;
		zkau.endPopup(uuid);
	} else if ("doOvl" == cmd ) {
		if (!zkau._checkResponse(cmd, uuid, cmp, 1, datanum))
			return;
		zkau.doOverlapped(cmp);
	} else if ("endOvl" == cmd) {
		if (!zkau._checkResponse(cmd, null, null, 1, datanum))
			return;
		zkau.endOverlapped(uuid);
	} else if ("meta" == cmd) {
		var meta = zkau.getMeta(uuid);
		if (meta) meta[dt1].call(meta, dt2, dt3, dt4);
	} else if ("closeErrbox" == cmd) {
		if (zkau.valid) {
			zkau.valid.closeErrbox(uuid);
			zkau.valid.closeErrbox(uuid + "!real");
		}
	} else {
		alert(mesg.ILLEGAL_RESPONSE+"Unknown command: "+cmd);
	}
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

/** To notify a component that its inner (not outer) is assigned.
 * If outer is assigned, init will be executed. In some case, you have
 * to monitor inner because no init is called in this case.
 */
zkau._initInner = function (n) {
	if (!n || (n.style && n.style.display == "none")) return; //done

	var type = zk.getCompType(n);
	if (type) {
		var fn = "zk"+type+".initInner";
		eval(fn+"&&"+fn+"(n)");
	}
}

/** To notify a component that it becomes visible because one its ancestors
 * becomes visible. It recursively invokes its descendants.
 */
zkau.onVisiAt = function (n) {
	if (!n || (n.style && n.style.display == "none")) return; //done

	var type = zk.getCompType(n);
	if (type) {
		var fn = "zk"+type+".onVisi";
		eval(fn+"&&"+fn+"(n)");
	}
	zkau.onVisiChildren(n);
};
/** To notify a component that it becomes invisible because one its ancestors
 * becomes invisible. It recursively invokes its descendants.
 */
zkau.onHideAt = function (n) {
	if (!n) return; //done

	var type = zk.getCompType(n);
	if (type) {
		if (zkau.valid) {
			zkau.valid.closeErrbox(n.id);
			zkau.valid.closeErrbox(n.id + "!real");
		}

		var fn = "zk"+type+".onHide";
		eval(fn+"&&"+fn+"(n)");
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
		if (name == "disabled" || name == "href")
			zkau.setStamp(cmp, name);
			//mark when this attribute is set (change or not), so
			//modal dialog and other know how to process it
			//--
			//Better to call setStamp always but, to save memory,...

		//Firefox return null for cmp.getAttribute("selectedIndex"), so...
		var j = name.indexOf('.'); 
		if (j >= 0) {
			if ("style" != name.substring(0, j)) {
				alert(mesg.UNSUPPORTED+name);
				return;
			}
			name = name.substring(j + 1);
			if (typeof(cmp.style[name]) == "boolean") //just in case
				value = "true" == value || name == value;
			cmp.style[name] = value;
			return;
		} else if ("style" == name) {
			zk.setStyle(cmp, value);
			return;
		}

		var old = "class" == name ? cmp.className:
			"selectedIndex" == name ? cmp.selectedIndex:
			"defaultValue" == name ? cmp.defaultValue: //Moz has problem to use getAttribute with this
			"disabled" == name ? cmp.disabled:
			"readonly" == name ? cmp.readonly:
				cmp.getAttribute(name);
		//Note: "true" != true (but "123" = 123)
		//so we have take care of boolean
		if (typeof(old) == "boolean")
			value = "true" == value || name == value; //e.g, reaonly="readonly"

		if (old != value) {
			if ("selectedIndex" == name) cmp.selectedIndex = value;
			else if ("class" == name) cmp.className = value;
			else if ("defaultValue" == name) {
				var old = cmp.value;
				cmp.defaultValue = value;
				if (old != cmp.value) cmp.value = old; //Bug 1490079 (happen in FF only)
			} else if ("disabled" == name) cmp.disabled = value;
			else if ("readonly" == name) cmp.readonly = value;
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
				alert(mesg.UNSUPPORTED+name);
				return;
			}
			cmp.style[name.substring(j + 1)] = "";
		} else if (cmp.hasAttribute(name)) {
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
/** Checks whether a response is correct. */
zkau._checkResponse = function (cmd, uuid, cmp, expectednum, datanum) {
	if (uuid && !cmp) {
		//alert(mesg.ILLEGAL_RESPONSE+"Component "+uuid+" not found");
		//If timer is used, a response might be processed after the timer
		//is removed. Thus, don't warn but silently ignore
		return false;
	}
	if (datanum != expectednum) {
		alert(mesg.ILLEGAL_RESPONSE+"Wrong number of arguments for "+cmd);
		return false;
	}
	return true;
};

//-- popup --//
if (!zkau._popups) {
	zkau._popups = new Array(); //uuid
	zkau._overlaps = new Array(); //uuid
	zkau._intervals = {};
}
/** Makes the component as popup. */
zkau.doPopup = function (cmp) {
	zkau.closeFloats();

	cmp.setAttribute("mode", "popup");

	var caption = $(cmp.id + "!caption");
	if (caption && caption.style.cursor == "") caption.style.cursor = "pointer";

	zkau.enableMoveable(cmp, zkau.autoZIndex, zkau.onWndMove);
	zkau._popups.push(cmp.id); //store ID because it might cease before endPopup
	zkau.hideCovered();
	zk.focusDownById(cmp.id, 0);
};
/** Makes the popup component as normal. */
zkau.endPopup = function (uuid) {
	var caption = $(uuid + "!caption");
	if (caption && caption.style.cursor == "pointer") caption.style.cursor = "";

	zkau._popups.remove(uuid);
	zkau.hideCovered();
	var cmp = $(uuid);
	if (cmp) {
		zkau.disableMoveable(cmp);
		cmp.removeAttribute("mode");
	}
};
/** Makes the component as overlapped. */
zkau.doOverlapped = function (cmp) {
	zkau.closeFloats();

	cmp.setAttribute("mode", "overlapped");

	var caption = $(cmp.id + "!caption");
	if (caption && caption.style.cursor == "") caption.style.cursor = "pointer";

	zkau.enableMoveable(cmp, zkau.autoZIndex, zkau.onWndMove);
	zkau.enableAutoZIndex(cmp);
	zkau._overlaps.push(cmp.id); //store ID because it might cease before endPopup
	zkau.hideCovered();
	zk.focusDownById(cmp.id, 0);
};
/** Makes the popup component as normal. */
zkau.endOverlapped = function (uuid) {
	var caption = $(uuid + "!caption");
	if (caption && caption.style.cursor == "pointer") caption.style.cursor = "";

	zkau._overlaps.remove(uuid);
	zkau.hideCovered();

	var cmp = $(uuid);
	if (cmp) {
		cmp.removeAttribute("mode");
		zkau.disableMoveable(cmp);
		zkau.disableAutoZIndex(cmp);
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
				handle: handle, zindex: cmp.style.zIndex,
				starteffect: Prototype.emptyFunction,
				starteffect: starteffect || Prototype.emptyFunction,
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

/** Enable a window to adjust z-index automatically. */
zkau.enableAutoZIndex = function (cmp) {
	if (cmp) Event.observe(cmp, "mousedown", zkau._onAutoZIndex);
};
/** Disable a window to adjust z-index automatically. */
zkau.disableAutoZIndex = function (cmp) {
	if (cmp) Event.stopObserving(cmp, "mousedown", zkau._onAutoZIndex);
};
/** The event handler for adjusting z-index. */
zkau._onAutoZIndex = function (evt) {
	if (!evt) evt = window.event;
	var node = Event.element(evt);
	for (;; node = node.parentNode) {
		if (!node) return;
		if (node.style && node.style.position == "absolute")
			break;
	}
	zkau.autoZIndex(node);
};
/** Adjust z-index automatically. */
zkau.autoZIndex = function (cmp) {
	var zi = parseInt(cmp.style.zIndex || "0");
	for (var j = 0; j < zkau._popups.length; ++j) {
		var el = $(zkau._popups[j]);
		if (el && cmp != el) {
			elzi = parseInt(el.style.zIndex || "0");
			if (elzi >= zi) zi = elzi + 1;
		}
	}
	for (var j = 0; j < zkau._overlaps.length; ++j) {
		var el = $(zkau._overlaps[j]);
		if (el && cmp != el) {
			elzi = parseInt(el.style.zIndex || "0");
			if (elzi >= zi) zi = elzi + 1;
		}
	}
	if (parseInt(cmp.style.zIndex || "0") != zi) {
		cmp.style.zIndex = zi;
		zkau.send({uuid: cmp.id, cmd: "onZIndex",
			data: [zi]}, zkau.asapTimeout(cmp, "onZIndex"));
	}
};

/** Called back when overlapped and popup is moved. */
zkau.onWndMove = function (cmp) {
	zkau.send({uuid: cmp.id, cmd: "onMove",
		data: [cmp.style.left, cmp.style.top]},
		zkau.asapTimeout(cmp, "onMove"));
};

zkau.onfocus = function (el) {
	zkau.currentFocus = el; //_onDocMousedown doesn't take care all cases
	if (!zkau.focusInFloats(el))
		setTimeout(zkau.closeFloats, 0);
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
		el.src = zk.rename(el.src, "on");
};
zkau.onimgout = function (el) {
	if (el && el.src.indexOf("-on") >= 0)
		el.src = zk.rename(el.src, "off");
};

/** Handles document.unload. */
zkau._onDocUnload = function () {
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
};
/** Handle document.onmousedown. */
zkau._onDocMousedown = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	zkau.currentFocus = target;
	if (!zkau.focusInFloats(target))
		setTimeout(zkau.closeFloats, 0);
};
/** Handles document.onkeydown. */
zkau._onDocKeydown = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var attrSkip, evtnm;
	switch (evt.keyCode) {
	case 13: //ENTER
	case 27: //ESC
		if (zkau.closeFloats()) {
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
			|| zkau._isInCtrlKeys(evt.keyCode, n.getAttribute("zk_ctrlKeys")))) {
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
	zkau.send({uuid: el.id, cmd: "onClose", data: null});
};
zkau.hide = function (uuid) {
	var el = $(uuid);
	if (el) el.style.display = "none";
	zkau.send({uuid: el.id, cmd: "onShow", data: ["false"]},
		zkau.asapTimeout(el, "onShow"));
};

/** Closes popups and floats. Return false if nothing changed. */
zkau.closeFloats = function() {
	var closed;
	for (;;) {
		//reverse order is important if popup contains another
		//otherwise, IE might have bug to handle them correctly
		var uuid = zkau._popups.pop();
		if (!uuid) break;

		closed = true;
		zkau.hide(uuid);
	}

	for (var j = 0; j < zkau.floats.length; ++j)
		if (zkau.floats[j].closeFloats()) //combobox popup
			closed = true;

	if (zkau.calclose && zkau.calclose()) //calendar's popup
		closed = true;
	if (closed)
		zkau.hideCovered();

	return closed;
};
zkau.hideCovered = function() {
	if (!zk.agtIe) return; //nothing to do

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
		alert(mesg.COMP_OR_UUID_REQUIRED);
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

///////////////
//Drag & Drop//
zkau.initdrag = function (n) {
	zkau._drags[n.id] = new Draggable(n, {
		revert: zkau._revertdrag, starteffect: Prototype.emptyFunction,
		endeffect: zkau._enddrag, change: zkau._dragging,
		ghosting: zkau._ghostdrag
	});
};
zkau.cleandrag = function (n) {
	if (zkau._drags[n.id]) {
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
	if (zk.agtNav || zkau._getDrop(n, pointer) == null)
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
		if (Position.within(e, pointer[0], pointer[1]))
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
//Tom Yeh: 20060227: FF cannot handle z-index well if listitem is dragged
//across two listboxes, so we use a fake DIV instead.
//On the other hand, IE handles selection improperly if transparent DIV is used
//so we use the standard ghosting
if (zk.agtNav) {
	zkau._ghostdrag = function (dg, ghosting) {
		if (ghosting) {
			zk.dragging = true;
			Position.prepare();
			var ofs = Position.positionedOffset(dg.element);
			document.body.insertAdjacentHTML("afterbegin",
				'<div id="zk_ddghost" style="position:absolute;top:'
				+ofs[1]+'px;left:'+ofs[0]+'px;width:'
				+dg.element.offsetWidth+'px;height:'+dg.element.offsetHeight
				+'px;border:1px dotted black">&nbsp;</div>');
	
			dg.zk_old = dg.element;
			dg.element = $("zk_ddghost");
		} else if (dg.zk_old) {
			zk.dragging = false;
			dg.element.parentNode.removeChild(dg.element);
			dg.element = dg.zk_old;
			dg.zk_old = null;
		}
		return false
	};
} else {
	zkau._ghostdrag = function (dg, ghosting) {
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
}

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
		if (n) n.style.display = "";
		if (n && n.id) id = n.id;
		zkau.onVisiAt(n);
	}
};

/** Makes a component invisible.
 * @param bHide false means show; otherwise (including undefined) is hide
 */
action.hide = function (id, bHide) {
	if (bHide == false) action.show(id);
	else {
		var n = $(id);
		if (n) n.style.display = "none";
		if (n && n.id) id = n.id;
		zkau.onHideAt(n);
	}
};

/** Slides down a component.
 * @param down false means slide-up; otherwise (including undefined) is slide-down
 */
action.slideDown = function (id, down) {
	if (down == false) action.slideUp(id);
	else {
		var n = $(id);
		if (!n.getAttribute || !n.getAttribute("zk_visible")) {
			if (n.setAttribute) n.setAttribute("zk_visible", "showing");
			Effect.SlideDown(n, {duration:0.4, afterFinish: action._afterDown});
		}
	}
};
action._afterDown = function (ef) {
	var n = ef.element;
	if (n.setAttribute) n.removeAttribute("zk_visible");
	zkau.onVisiAt(n);
};

/** Slides down a component.
 * @param up false means slide-down; otherwise (including undefined) is slide-up
 */
action.slideUp = function (id, up) {
	if (up == false) action.slideDown(id);
	else {
		var n = $(id);
		if (!n.getAttribute || !n.getAttribute("zk_visible")) {
			if (n.setAttribute) n.setAttribute("zk_visible", "hiding");
			Effect.SlideUp(n, {duration:0.4, afterFinish: action._afterUp});
		}
	}
};
action._afterUp = function (ef) {
	var n = ef.element;
	if (n.setAttribute) n.removeAttribute("zk_visible");
	zkau.onHideAt(n);
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
