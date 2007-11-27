/* au.js

{{IS_NOTE
	Purpose:
		JavaScript for asynchronous updates
	Description:
		
	History:
		Fri Jun 10 15:04:31     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
if (!window.zkau) { //avoid eval twice
zkau = {};

zkau._respQue = []; //responses in XML
zkau._areqTry = 0; //# of resend
zkau._evts = {}; //(dtid, Array())
zkau._js4resps = []; //JS to eval upon response
zkau._metas = {}; //(id, meta)
zkau._drags = {}; //(id, Draggable): draggables
zkau._drops = []; //dropables
zkau._zidsp = {}; //ID spaces: {owner's uuid, {id, uuid}}
zkau._stamp = 0; //used to make a time stamp
zkau.topZIndex = 0; //topmost z-index for overlap/popup/modal
zkau.floats = []; //popup of combobox, bandbox, datebox...
zkau._onsends = []; //JS called before zkau._sendNow
zkau._seqId = 0; //starting at 0 - the same as Desktop.getResponseSequence
zkau._dtids = []; //an array of desktop IDs

/** Adds a desktop. */
zkau.addDesktop = function (dtid) {
	var ds = zkau._dtids;
	for (var j = ds.length; --j >= 0;)
		if (ds[j] == dtid)
			return; //nothing to do
	ds.push(dtid);
};
/** Returns the desktop's ID. */
zkau._dtid = function (uuid) {
	if (zkau._dtids.length == 1) return zkau._dtids[0];

	for (var n = $e(uuid); n; n = n.parentNode) {
		var id = getZKAttr(n, "dtid");
		if (id) return id;
	}
	return null;
}

zk.addInit(function () {
	zk.listen(document, "keydown", zkau._onDocKeydown);
	zk.listen(document, "mousedown", zkau._onDocMousedown);
	zk.listen(document, "mouseover", zkau._onDocMouseover);
	zk.listen(document, "mouseout", zkau._onDocMouseout);

	zk.listen(document, "contextmenu", zkau._onDocCtxMnu);
	zk.listen(document, "click", zkau._onDocLClick);
	zk.listen(document, "dblclick", zkau._onDocDClick);

	zk.listen(window, "resize", zkau._onResize);

	zkau._oldUnload = window.onunload;
	window.onunload = zkau._onUnload; //unable to use zk.listen

	zkau._oldBfUnload = window.onbeforeunload;
	window.onbeforeunload = zkau._onBfUnload;
});

/** Handles onclick for button-type.
 */
zkau.onclick = function (evt) {
	if (typeof evt == 'string') {
		zkau.send({uuid: $uuid(evt), cmd: "onClick", data: null, ctl: true});
		return;
	}

	if (!evt) evt = window.event;
	var target = Event.element(evt);

	//it might be clicked on the inside element
	for (;; target = target.parentNode)
		if (!target) return;
		else if (target.id) break;

	var href = getZKAttr(target, "href");
	if (href) {
		zk.go(href, false, getZKAttr(target, "target"));
		Event.stop(evt); //prevent _onDocLClick
		return; //done
	}

	zkau.send({uuid: $uuid(target.id),
		cmd: "onClick", data: zkau._getMouseData(evt, target), ctl: true});
	//Don't stop event so popup will work (bug 1734801)
};
/** Handles ondblclick for button (for non-FF).
 * Note: double clicks are handled by zkau._onDocDClick, but
 * some tags (button and checkbox) eat the event, so _onDocDClick won't
 * get the event.
 */
zkau.ondblclick = function (evt) {
	if (!evt) evt = window.event;
	var cmp = Event.element(evt);

	//it might be clicked on the inside element
	for (;; cmp = cmp.parentNode)
		if (!cmp) return;
		else if (cmp.id) break;

	cmp = $outer(cmp);
	if (cmp && getZKAttr(cmp, "dbclk")) {
		zkau.send({uuid: cmp.id,
			cmd: "onDoubleClick", data: zkau._getMouseData(evt, cmp), ctl: true});
		Event.stop(evt); //just in case: prevent _onDocDClick to run again
		return false;
	}
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

/** Asks the server to update a component (for file uploading). */
zkau.sendUpdateResult = function (uuid, updatableId) {
	zkau.send({uuid: uuid, cmd: "updateResult", data: [updatableId]}, -1);
}
/** Asks the server to remove a component. */
zkau.sendRemove = function (uuid) {
	if (!uuid) {
		zk.error(mesg.UUID_REQUIRED);
		return;
	}
	zkau.send({uuid: uuid, cmd: "remove", data: null}, 5);
};

////ajax timeout////
/** IE6 sometimes remains readyState==1 (reason unknown), and we have
 * to resend
 */
zkau._areqTmout = function () {
	var req = zkau._areq, reqInf = zkau._areqInf;
	if (req && req.readyState != 4) {
		zkau._areq = zkau._areqInf = null;
		try {
			if(typeof req.abort == "function") req.abort();
		} catch (e2) {
		}

		zkau._areqResend(reqInf[1]);
	}
};
zkau._areqResend = function (es) {
	var timeout;
	for (var j = es.length; --j >= 0;)
		zkau.sendAhead(es[j], j ? timeout: 0);
};
/** Called when the response is received from zkau._areq.
 */
zkau._onRespReady = function () {
	try {
		var req = zkau._areq, reqInf = zkau._areqInf;
		if (req && req.readyState == 4) {
			zkau._areq = zkau._areqInf = null;
			clearTimeout(reqInf[0]); //stop timer

			if (zkau._revertpending) zkau._revertpending();
				//revert any pending when the first response is received

			if (req.status == 200) {
				zkau._areqTry = 0;
				var sid = req.responseXML.getElementsByTagName("sid");
				if (sid && sid.length) {
					sid = $int(zk.getElementValue(sid[0]));
					if (isNaN(sid) || sid < 0 || sid > 1024) sid = null; //ignore if error sid
				} else
					sid = null;

				//locate whether to insert the response by use of sid
				var que = zkau._respQue;
				var ofs = que.length;
				if (sid != null)
					while (ofs > 0 && que[ofs - 1].sid != null
					&& zkau.cmprsid(sid, que[ofs - 1].sid) < 0)
						--ofs;

				var resp = {sid: sid, cmds: zkau._parseCmds(req.responseXML)};
				if (ofs == que.length) que.push(resp);
				else que.splice(ofs, 0, resp); //insert
			} else {
				//handle MSIE's buggy HTTP status codes
				switch (req.status) {
				case 12029: // 12029 to 12031 correspond to dropped connections.
					if (++zkau._areqTry > 3) {
						zkau._areqTry = 0;
						break; //the server is dead
					}
				case 12002: // Server timeout
				case 12030:
				case 12031:
				case 12152: // Connection closed by server.
				case 12159:
				case 13030:
					zkau._areqResend(reqInf[1]);
					return;
				}

				if (!zkau._ignorable && !zkau._unloading)
					zk.error(mesg.FAILED_TO_RESPONSE+(req.statusText!="Unknown"?req.statusText:""));
				zkau._cleanupOnFatal(zkau._ignorable);
			}
		}
	} catch (e) {
		zkau._areq = zkau._areqInf = null;
		try {
			if(req && typeof req.abort == "function") req.abort();
		} catch (e2) {
		}

		//NOTE: if connection is off and req.status is accessed,
		//Mozilla throws exception while IE returns a value
		if (!zkau._ignorable && !zkau._unloading) {
			var msg = e.message;
			zk.error(mesg.FAILED_TO_RESPONSE+(msg.indexOf("NOT_AVAILABLE")<0?msg:""));
		}
		zkau._cleanupOnFatal(zkau._ignorable);
	}

	//handle pending ajax send
	if (zkau._sendPending && !zkau._areq) {
		zkau._sendPending = false;
		var ds = zkau._dtids;
		for (var j = ds.length; --j >= 0;)
			zkau._send2(ds[j], 0);
	}

	zkau._doQueResps();
	zkau._checkProgress();
};
zkau._parseCmds = function (xml) {
	var rs = xml.getElementsByTagName("r")
	if (!rs) return null;

	var cmds = [];
	for (var j = 0; j < rs.length; ++j) {
		var cmd = rs[j].getElementsByTagName("c")[0];
		var data = rs[j].getElementsByTagName("d");

		if (!cmd) {
			zk.error(mesg.ILLEGAL_RESPONSE+"Command required");
			continue;
		}

		cmds.push(cmd = {cmd: zk.getElementValue(cmd)});

		switch (cmd.datanum = data ? data.length: 0) {
		default: //5 or more
			cmd.dt4 = zk.getElementValue(data[4]);
		case 4:
			cmd.dt3 = zk.getElementValue(data[3]);
		case 3:
			cmd.dt2 = zk.getElementValue(data[2]);
		case 2:
			cmd.dt1 = zk.getElementValue(data[1]);
		case 1:
			cmd.dt0 = zk.getElementValue(data[0]);
		case 0:
		}
	}
	return cmds;
};
/** Returns 1 if a > b, -1 if a < b, or 0 if a == b.
 * Note: range of sid is 0 ~ 1023.
 */
zkau.cmprsid = function (a, b) {
	var dt = a - b;
	return dt == 0 ? 0: (dt > 0 && dt < 512) || dt < -512 ? 1: -1
};
zkau._checkProgress = function () {
	if (!zkau._inProcess())
		zk.progressDone();
};
zkau._inProcess = function () {
	return zkau._respQue.length || zkau._areq;
};

/** Returns the timeout of the specified event.
 * It is mainly used to generate the timeout argument of zkau.send.
 *
 * @param timeout if non-negative, it is used when zkau.asap is true.
 */
zkau.asapTimeout = function (cmp, evtnm, timeout) {
	return zkau.asap(cmp, evtnm) ? timeout >= 0 ? timeout: 38: -1;
};
/** Returns whether any non-deferrable listener is registered for
 * the specified event.
 */
zkau.asap = function (cmp, evtnm) {
	return getZKAttr($e(cmp), evtnm) == "true" ;
};

/** Returns the event list of the specified desktop ID.
 */
zkau._events = function (dtid) {
	var es = zkau._evts;
	if (!es[dtid])
		es[dtid] = [];
	return es[dtid];
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
/** Sends a request to the client
 * @param timout milliseconds.
 * If negative, it won't be sent until next non-negative event
 */
zkau.send = function (evt, timeout) {
	if (timeout < 0) evt.implicit = true;

	if (evt.uuid) {
		zkau._send(zkau._dtid(evt.uuid), evt, timeout);
	} else {
		var ds = zkau._dtids;
		for (var j = 0; j < ds.length; ++j)
			zkau._send(ds[j], evt, timeout);
	}
};
zkau._send = function (dtid, evt, timeout) {
	if (evt.ctl) {
		var t = new Date().getTime();
		if (zkau._ctl == evt.uuid && t - zkau._ctlt < 450)
			return; //to prevent key stroke are pressed twice (quickly)
		zkau._ctlt = t;
		zkau._ctl = evt.uuid;
	}

	zkau._events(dtid).push(evt);

	//Note: we don't send immediately (Bug 1593674)
	//Note: Unlike sendAhead and _send2, if timeout is undefined,
	//it is considered as 0.
	zkau._send2(dtid, timeout ? timeout: 0);
};
/** @param timeout if undefined or negative, it won't be sent. */
zkau._send2 = function (dtid, timeout) {
	if (dtid && timeout >= 0) setTimeout("zkau._sendNow('"+dtid+"')", timeout);
};
/** Sends a request before any pending events.
 * @param timout milliseconds.
 * If undefined or negative, it won't be sent until next non-negative event
 * Note: Unlike zkau.send, it considered undefined as not sending now
 * (reason: backward compatible)
 */
zkau.sendAhead = function (evt, timeout) {
	if (evt.uuid) {
		var dtid;
		zkau._events(dtid = zkau._dtid(evt.uuid)).unshift(evt);
		zkau._send2(dtid, timeout);
	} else {
		var ds = zkau._dtids;
		for (var j = ds.length; --j >= 0; ++j) {
			zkau._events(ds[j]).unshift(evt);
			zkau._send2(ds[j], timeout); //Spec: don't convert unefined to 0 for timeout
		}
	}
};
zkau._sendNow = function (dtid) {
	var es = zkau._events(dtid);
	if (es.length == 0)
		return; //nothing to do

	if (zk.loading) {
		zk.addInit(function () {zkau._sendNow(dtid);});
		return; //wait
	}

	if (!zk_action) {
		zk.error(mesg.NOT_FOUND+"zk_action");
		return;
	}

	if (zkau._areq) { //send ajax request one by one
		zkau._sendPending = true;
		return;
	}

	//bug 1721809: we cannot filter out ctl even if zkau.processing

	//decide implicit and ignorable
	var implicit = true, ignorable = true;
	for (var j = es.length; --j >= 0;) {
		if (!es[j].ignorable) { //ignorable implies implicit
			ignorable = false;
			if (!es[j].implicit) {
				implicit = false;
				break;
			}
		}
	}
	zkau._ignorable = ignorable;

	//callback (fckez uses it to ensure its value is sent back correctly
	for (var j = 0; j < zkau._onsends.length; ++j) {
		try {
			zkau._onsends[j](implicit); //it might add more events
		} catch (e) {
			zk.error(e.message);
		}
	}

	//FUTURE: Consider XML (Pros: ?, Cons: larger packet)
	var reqes = []; //backup events
	var content = "";
	for (var j = 0; es.length; ++j) {
		var evt = es.shift();
		reqes.push(evt);
		content += "&cmd."+j+"="+evt.cmd+"&uuid."+j+"="+evt.uuid;
		if (evt.data)
			for (var k = 0; k < evt.data.length; ++k) {
				var data = evt.data[k];
				content += "&data."+j+"="
					+ (data != null ? encodeURIComponent(data): 'zk_null~q');
			}
	}

	if (!content) return; //nothing to do

	content = "dtid=" + dtid + content;
	var req = zkau._areq = zkau.ajaxRequest();
	var msg;
	try {
		req.onreadystatechange = zkau._onRespReady;
		req.open("POST", zk_action, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

		zkau._areqInf = [setTimeout(zkau._areqTmout, 9100), reqes];
		req.send(content);

		if (!implicit) zk.progress(zk_procto); //wait a moment to avoid annoying
		return; //success
	} catch (e) {
		try {
			if(typeof req.abort == "function") req.abort();
		} catch (e2) {
		}
		msg = e.message;
	}

	//handle error
	if (!ignorable && !zkau._unloading)
		zk.error(mesg.FAILED_TO_SEND+zk_action+"\n"+content+(msg?"\n"+msg:""));
	zkau._cleanupOnFatal(ignorable);
};

/** Adds a script that will be evaluated when the next response is back. */
zkau.addOnResponse = function (script) {
	zkau._js4resps.push(script);
};
/** Evaluates scripts registered by addOnResponse. */
zkau._evalOnResponse = function () {
	while (zkau._js4resps.length)
		setTimeout(zkau._js4resps.shift(), 0);
};

/** Process the responses queued in zkau._respQue. */
zkau._doQueResps = function () {
	var ex;
	var que = zkau._respQue;
	for (var j = 0; que.length;) {
		if (zk.loading) {
			zk.addInit(zkau._doQueResps); //Note: when callback, zk.loading is false
			break; //wait until the loading is done
		}

		try {
			var oldSeqId = zkau._seqId;
			var resp = que.shift();
			if (resp.sid == zkau._seqId || resp.sid == null
			|| zkau._dtids.length > 1) { //unable to support seqId if multi-desktop
				//we have to inc seqId first since _doResps might throw exception
				if (resp.sid != null && ++zkau._seqId == 1024)
					zkau._seqId = 0;

				if (!zkau._doResps(resp.cmds)) {
					que.unshift(resp); //handle it later
					zkau._seqId = oldSeqId; //restore seqId
				}
			} else {
				que.unshift(resp); //undo

				setTimeout(function () {
					if (que.length && zkau._seqId == oldSeqId) { //no new processed
						zkau._seqId = que[0].sid;
							//skip to the first ID if timeout
						zkau._doQueResps();
					}
				}, 3600);
				break; //wait for timeout, or arrival of another response
			}
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
zkau._doResps = function (cmds) {
	while (cmds && cmds.length) {
		if (zk.loading)
			return false;

		var cmd = cmds.shift();
		try {
			zkau.process(cmd.cmd, cmd.datanum,
				cmd.dt0, cmd.dt1, cmd.dt2, cmd.dt3, cmd.dt4);
		} catch (e) {
			zk.error(mesg.FAILED_TO_PROCESS+cmd.cmd+"\n"+e.message+"\n"+cmd.dt0+"\n"+cmd.dt1);
			throw e;
		} finally {
			zkau._evalOnResponse();
		}
	}
	return true;
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
	var cmp = $e(uuid);

	fn = zkau.cmd1[cmd];
	if (fn) {
		fn.call(zkau, uuid, cmp, dt1, dt2, dt3, dt4);
		return;
	}

	zk.error(mesg.ILLEGAL_RESPONSE+"Unknown command: "+cmd);
};
zk.process = zkau.process; //ZK assumes zk.process, so change it

/** Cleans up if we detect obsolete or other severe errors. */
zkau._cleanupOnFatal = function (ignorable) {
	for (var uuid in zkau._metas) {
		var meta = zkau._metas[uuid];
		if (meta && meta.cleanupOnFatal)
			meta.cleanupOnFatal(ignorable);
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
/** Invoke zk.initAt for all children. Note: to is excluded. */
zkau._initChildren = function (n, to) {
	for (n = n.firstChild; n && n != to; n = n.nextSibling)
		zk.initAt(n);
};
/** Invoke inserHTMLBeforeEnd and then zk.initAt.
 */
zkau._insertAndInitBeforeEnd = function (n, html) {
	if ($tag(n) == "TABLE" && zk.tagOfHtml(html) == "TR") {
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

/** Sets an attribute (the default one). */
zkau.setAttr = function (cmp, name, value) {
	if ("visibility" == name) {
		zk.show(cmp, "true" == value);
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
	} else if ("selectAll" == name && $tag(cmp) == "SELECT") {
		value = "true" == value;
		for (var j = 0; j < cmp.options.length; ++j)
			cmp.options[j].selected = value;
	} else if ("style" == name) {
		zk.setStyle(cmp, value);
	} else if (name.startsWith("z.")) { //ZK attributes
		setZKAttr(cmp, name.substring(2), value);
	} else {
		var j = name.indexOf('.'); 
		if (j >= 0) {
			if ("style" != name.substring(0, j)) {
				zk.error(mesg.UNSUPPORTED+name);
				return;
			}
			name = name.substring(j + 1).camelize();
			if (typeof(cmp.style[name]) == "boolean") //just in case
				value = "true" == value || name == value;
			cmp.style[name] = value;

			if ("width" == name && (!value || value.indexOf('%') < 0) //don't handle width with %
			&& "true" != getZKAttr(cmp, "float")) {
				var ext = $e(cmp.id + "!chdextr");
				if (ext && $tag(ext) == "TD" && ext.colSpan == 1)
					ext.style.width = value;
			}
			return;
		}

		if (name == "disabled" || name == "href")
			zkau.setStamp(cmp, name);
			//mark when this attribute is set (change or not), so
			//modal dialog and other know how to process it
			//--
			//Better to call setStamp always but, to save memory,...

		//Firefox cannot handle many properties well with getAttribute/setAttribute,
		//such as selectedIndex, scrollTop...
		var old = "class" == name ? cmp.className:
			"selectedIndex" == name ? cmp.selectedIndex:
			"disabled" == name ? cmp.disabled:
			"readOnly" == name ? cmp.readOnly:
			"scrollTop" == name ? cmp.scrollTop:
			"scrollLeft" == name ? cmp.scrollLeft:
				cmp.getAttribute(name);

		//Note: "true" != true (but "123" = 123)
		//so we have take care of boolean
		if (typeof(old) == "boolean")
			value = "true" == value || name == value; //e.g, reaonly="readOnly"

		if (old != value) {
			if ("selectedIndex" == name) cmp.selectedIndex = value;
			else if ("class" == name) cmp.className = value;
			else if ("disabled" == name) cmp.disabled = value;
			else if ("readOnly" == name) cmp.readOnly = value;
			else if ("scrollTop" == name) cmp.scrollTop = value;
			else if ("scrollLeft" == name) cmp.scrollLeft = value;
			else cmp.setAttribute(name, value);
		}
	}
};
/** Returns the time stamp. */
zkau.getStamp = function (cmp, name) {
	var stamp = getZKAttr(cmp, "stm" + name);
	return stamp ? stamp: "";
};
/** Sets the time stamp. */
zkau.setStamp = function (cmp, name) {
	setZKAttr(cmp, "stm" + name, "" + ++zkau._stamp);
};
zkau.rmAttr = function (cmp, name) {
	if ("class" == name) {
		if (cmp.className) cmp.className = "";
	} else if (name.startsWith("z.")) { //ZK attributes
		rmZKAttr(cmp, name.substring(2));
		return;
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

/** Corrects zIndex of the specified component, which must be absolute.
 * @param silent whether to send onZIndex
 * @param autoz whether to adjust zIndex only necessary.
 * If false (used when creating a window), it always increases zIndex
 */
zkau.fixZIndex = function (cmp, silent, autoz) {
	if (!zkau._popups.length && !zkau._overlaps.length && !zkau._modals.length)
		zkau.topZIndex = 0; //reset it!

	var zi = $int(cmp.style.zIndex);
	if (zi > zkau.topZIndex) {
		zkau.topZIndex = zi;
	} else if (!autoz || zi < zkau.topZIndex) {
		cmp.style.zIndex = ++zkau.topZIndex;
		if (!silent && cmp.id) {
			cmp = $outer(cmp);
			zkau.sendOnZIndex(cmp);
		}
	}
};
/** Automatically adjust z-index if node is part of popup/overalp/...
 */
zkau.autoZIndex = function (node) {
	for (; node; node = node.parentNode) {
		if (node.style && node.style.position == "absolute") {
			if (getZKAttr(node, "autoz"))
				zkau.fixZIndex(node, false, true); //don't inc if equals
			break;
		}
	}
};

//-- popup --//
if (!zkau._popups) {
	zkau._popups = []; //uuid
	zkau._overlaps = []; //uuid
	zkau._modals = []; //uuid (used zul.js or other modal)
}

//-- utilities --//
/** Returns the element of the specified element.
 * It is the same as Event.elemet(evt), but
 * it is smart enough to know whether evt is an element.
 * It is used to make a method able to accept either event or element.
 */
zkau.evtel = function (evtel) {
	if (!evtel) evtel = window.event;
	else if (evtel.parentNode) return evtel;
	return Event.element(evtel);
};

zkau.onfocus = function (evtel) { //accept both evt and cmp
	var el = zkau.evtel(evtel);
	zkau.currentFocus = el; //_onDocMousedown doesn't take care all cases
	zkau.closeFloatsOnFocus(el);
	if (zkau.valid) zkau.valid.uncover(el);

	zkau.autoZIndex(el);

	var cmp = $outer(el);
	if (zkau.asap(cmp, "onFocus"))
		zkau.send({uuid: cmp.id, cmd: "onFocus", data: null}, 100);
};
zkau.onblur = function (evtel) {
	var el = zkau.evtel(evtel);
	if (el == zkau.currentFocus) zkau.currentFocus = null;
		//Note: _onDocMousedown is called before onblur, so we have to
		//prevent it from being cleared

	var cmp = $outer(el);
	if (zkau.asap(cmp, "onBlur"))
		zkau.send({uuid: cmp.id, cmd: "onBlur", data: null}, 100);
};

zkau.onimgover = function (evtel) {
	var el = zkau.evtel(evtel);
	if (el && el.src.indexOf("-off") >= 0)
		el.src = zk.renType(el.src, "on");
};
zkau.onimgout = function (evtel) {
	var el = zkau.evtel(evtel);
	if (el && el.src.indexOf("-on") >= 0)
		el.src = zk.renType(el.src, "off");
};

/** Returns the Ajax request. */
zkau.ajaxRequest = function () {
	if (window.XMLHttpRequest) {
		return new XMLHttpRequest();
	} else {
		try {
			return new ActiveXObject('Msxml2.XMLHTTP');
		} catch (e2) {
			return new ActiveXObject('Microsoft.XMLHTTP');
		}
	}
};

/** Handles window.unload. */
zkau._onUnload = function () {
	zkau._unloading = true; //to disable error message

	if (zk.gecko) zk.restoreDisabled(); //Workaround Nav: Bug 1495382

	//20061109: Tom Yeh: Failed to disable Opera's cache, so it's better not
	//to remove the desktop. Side effect: BACK to an page, its content might
	//not be consistent with server's (due to Opera incapable to restore
	//DHTML content 100% correctly)

	if (!zk.opera && !zk.keepDesktop) {
		try {
			var ds = zkau._dtids;
			for (var j = 0; j < ds.length; ++j) {
				var req = zkau.ajaxRequest();
				req.open("POST", zk_action, true);
				req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
				req.send("dtid="+ds[j]+"&cmd.0=rmDesktop");
			}
		} catch (e) { //silent
		}
	}

	if (zkau._oldUnload) zkau._oldUnload.apply(window, arguments);
	zk.unlistenAll();
};
/** Handles window.onbeforeunload. */
zkau._onBfUnload = function () {
	if (zkau.confirmClose)
		return zkau.confirmClose;
	if (zkau._oldBfUnload)
		return zkau._oldBfUnload.apply(window, arguments);
	//Return nothing
};

/** Handle document.onmousedown. */
zkau._onDocMousedown = function (evt) {
	if (!evt) evt = window.event;

	zkau._savepos(evt);

	var node = Event.element(evt);
	zkau.currentFocus = node;

	zkau.closeFloatsOnFocus(node);

	zkau.autoZIndex(node);
};
/** Handles the left click. */
zkau._onDocLClick = function (evt) {
	if (!evt) evt = window.event;

	if (evt.which == 1 || (evt.button == 0 || evt.button == 1)) {
		var cmp = Event.element(evt);
		cmp = zkau._parentByZKAttr(cmp, "lfclk", "pop");
		if (cmp) {
			var ctx = getZKAttr(cmp, "pop");
			if (ctx) {
				ctx = zkau.getByZid(cmp, ctx);
				if (ctx) {
					var type = $type(ctx);
					if (type) {
						zkau.closeFloats(ctx, cmp);

						zkau._autopos(ctx, Event.pointerX(evt), Event.pointerY(evt));
						zk.eval(ctx, "context", type, cmp);
					}
				}
			}

			if (getZKAttr(cmp, "lfclk") && zkau.insamepos(evt))
				zkau.send({uuid: $uuid(cmp),
					cmd: "onClick", data: zkau._getMouseData(evt, cmp), ctl: true});

			//no need to Event.stop
		}
	}
	//don't return anything. Otherwise, it replaces event.returnValue in IE (Bug 1541132)
};
/** Saves the mouse position to be used with insamepos. */
zkau._savepos = function (evt) {
	if (evt)
		zkau._mspos = [Event.pointerX(evt), Event.pointerY(evt), Event.element(evt)];
};
/** Checks whether the position of the specified event is the same the one
 * stored with zkau._savepos.
 * Note: if the target element is different, it is considered as IN THE SAME POSITION.
 */
zkau.insamepos = function (evt) {
	if (!evt || !zkau._mspos) return true; //yes, true

	if (Event.element(evt) != zkau._mspos[2]) return true; //yes, true

	var x = Event.pointerX(evt) - zkau._mspos[0];
	var y = Event.pointerY(evt) - zkau._mspos[1];
	return x > -3 && x < 3 && y > -3 && y < 3;
};
/** Autoposition by the specified (x, y). */
zkau._autopos = function (el, x, y) {
	var ofs = zk.getDimension(el);
	var wd = ofs[0], hgh = ofs[1];

	var scx = zk.innerX(), scy = zk.innerY(),
		scmaxx = scx + zk.innerWidth(), scmaxy = scy + zk.innerHeight();
	if (x + wd > scmaxx) {
		x = scmaxx - wd;
		if (x < scx) x = scx;
	}
	if (y + hgh > scmaxy) {
		y = scmaxy - hgh;
		if (y < scy) y = scy;
	}

	ofs = zk.toStyleOffset(el, x, y);
	el.style.left = ofs[0] + "px";
	el.style.top = ofs[1] + "px";
};

/** Handles the double click. */
zkau._onDocDClick = function (evt) {
	if (!evt) evt = window.event;

	var cmp = Event.element(evt);
	cmp = zkau._parentByZKAttr(cmp, "dbclk");
	if (cmp/* no need since browser handles it: && zkau.insamepos(evt)*/) {
		var uuid = getZKAttr(cmp, "item"); //treerow (and other transparent)
		if (!uuid) uuid = $uuid(cmp);
		zkau.send({uuid: uuid,
			cmd: "onDoubleClick", data: zkau._getMouseData(evt, cmp), ctl: true});
		//no need to Event.stop
	}
};
/** Handles the right click (context menu). */
zkau._onDocCtxMnu = function (evt) {
	if (!evt) evt = window.event;

	var target = Event.element(evt);
	var cmp = zkau._parentByZKAttr(target, "ctx", "rtclk");

	if (cmp) {
		var ctx = getZKAttr(cmp, "ctx");
		var rtclk = getZKAttr(cmp, "rtclk");
		if (ctx || rtclk) {
			//Give the component a chance to handle right-click
			for (var n = target; n; n = n.parentNode) {
				var type = $type(n);
				if (type) {
					var o = window["zk" + type];
					if (o && o.onrtclk)
						if (o.onrtclk(n))
							ctx = rtclk = null;
				}
				if (n == cmp) break; //only up to the one with right click
			}
		}

		if (ctx) {
			ctx = zkau.getByZid(cmp, ctx);
			if (ctx) {
				var type = $type(ctx);
				if (type) {
					zkau.closeFloats(ctx, cmp);

					zkau._autopos(ctx, Event.pointerX(evt), Event.pointerY(evt));
					zk.eval(ctx, "context", type, cmp);
				}
			}
		}

		if (rtclk/*no need since oncontextmenu: && zkau.insamepos(evt)*/) {
			var uuid = getZKAttr(cmp, "item"); //treerow (and other transparent)
			if (!uuid) uuid = $uuid(cmp);
			zkau.send({uuid: uuid,
				cmd: "onRightClick", data: zkau._getMouseData(evt, cmp), ctl: true});
		}

		Event.stop(evt);
		return false;
	}
	return !zk.ie || evt.returnValue;
};
zkau._onDocMouseover = function (evt) {
	if (!evt) evt = window.event;

	var cmp = Event.element(evt);
	/** Request 1628075: give up.
	    Why? the wait cursor won't disappear until users move the cursor
	if (cmp && cmp.style && cmp.getAttribute && $tag(cmp) != "HTML") {
		var bk = cmp.getAttribute("z_bk4wait");
		if (zk.progressPrompted) {
			if (!bk) {
				var cr = cmp.style.cursor;
				cmp.setAttribute("z_bk4wait", cr ? cr: "nil");
				cmp.style.cursor = "wait";
			}
		} else if (bk) {
			cmp.removeAttribute("z_bk4wait");
			cmp.style.cursor = bk == "nil" ? "": bk;
		}
	}*/

	cmp = zkau._parentByZKAttr(cmp, "tip");
	if (cmp && !zk.progressing) { //skip tip if in processing
		var tip = getZKAttr(cmp, "tip");
		tip = zkau.getByZid(cmp, tip);
		if (tip) {
			var open = zkau._tipz && zkau._tipz.open;
			if (!open || zkau._tipz.cmpId != cmp.id) {
				zkau._tipz = {
					tipId: tip.id, cmpId: cmp.id,
					x: Event.pointerX(evt) + 1, y: Event.pointerY(evt) + 2
					 //Bug 1572286: position tooltip with some offset to allow
				};
				if (open) zkau._openTip(cmp.id);
				else setTimeout("zkau._openTip('"+cmp.id+"')", zk_tipto);
			}
			return; //done
		}
	}
	if (zkau._tipz) {
		if (zkau._tipz.open) {
			var tip = $e(zkau._tipz.tipId);
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
/** document.onmouseout */
zkau._onDocMouseout = function (evt) {
	if (!evt) evt = window.event;

	if (zkau._tipz)
		if (zkau._tipz.open) {
			zkau._tipz.shallClose = true;
			setTimeout(zkau._tryCloseTip, 300);
		} else
			zkau._tipz = null;
};
/** window.onresize */
zkau._onResize = function () {
	if (zkau._cInfoReg) {
		//since IE keeps sending onresize when dragging the browser border,
		//we reduce # of packs sent to the server by use of timeout
		zkau._cInfoPend = true;
		setTimeout(zkau._doClientInfo, 100);
	}
};

/** send clientInfo to the server ontimeout. */
zkau._doClientInfo = function () {
	if (zkau._cInfoPend) {
		zkau._cInfoPend = false;
		zkau.cmd0.clientInfo();
	}
};

zkau._openTip = function (cmpId) {
	//We have to filter out non-matched cmpId because user might move
	//from one component to another
	if (zkau._tipz && !zkau._tipz.open
	 && (!cmpId || cmpId == zkau._tipz.cmpId)) {
		var tip = $e(zkau._tipz.tipId);
		zkau.closeFloats(tip, $e(cmpId));
		if (tip) {
			var cmp = $e(cmpId);
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
zkau._parentByZKAttr = function (n, attr1, attr2) {
	for (; n; n = n.parentNode) {
		if (attr1 && getZKAttr(n, attr1)) return n;
		if (attr2 && getZKAttr(n, attr2)) return n;
	}
	return null;
};

/** Handles document.onkeydown. */
zkau._onDocKeydown = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt);
	var zkAttrSkip, evtnm, ctkeys, shkeys, alkeys, exkeys;
	var keycode = evt.keyCode, zkcode; //zkcode used to search z.ctkeys
	switch (keycode) {
	case 13: //ENTER
		var tn = $tag(target);
		if (tn == "TEXTAREA" || tn == "BUTTON"
		|| (tn == "INPUT" && target.type.toLowerCase() == "button"))
			return true; //don't change button's behavior (Bug 1556836)
		//fall thru
	case 27: //ESC
		if (zkau.closeFloats(target)) {
			Event.stop(evt);
			return false; //eat
		}
		if (keycode == 13) {
			zkAttrSkip = "skipOK"; evtnm = "onOK";
		} else {
			zkAttrSkip = "skipCancel"; evtnm = "onCancel";
		}
		break;
	case 16: //Shift
	case 17: //Ctrl
	case 18: //Alt
		return true;
	case 44: //Ins
	case 45: //Del
		zkcode = keycode == 44 ? 'I': 'J';
		break;
	default:
		if (keycode >= 33 && keycode <= 40) { //PgUp, PgDn, End, Home, L, U, R, D
			zkcode = String.fromCharCode('A'.charCodeAt(0) + (keycode - 33));
				//A~H: PgUp, ...
			break;
		} else if (keycode >= 112 && keycode <= 123) { //F1: 112, F12: 123
			zkcode = String.fromCharCode('P'.charCodeAt(0) + (keycode - 112));
				//M~Z: F1~F12
			break;
		} else if (evt.ctrlKey || evt.altKey) {
			zkcode = String.fromCharCode(keycode).toLowerCase();
			break;
		}
		return true;
	}

	if (zkcode) evtnm = "onCtrlKey";

	for (var n = target; n; n = n.parentNode) {
		if (n.id && n.getAttribute) {
			if (getZKAttr(n, evtnm) == "true"
			&& (!zkcode || zkau._inCtkeys(evt, zkcode, getZKAttr(n, "ctkeys")))) {
				var bSend = true;
				if (zkau.currentFocus) {
					var inp = zkau.currentFocus;
					switch ($tag(inp)) {
					case "INPUT":
						var type = inp.type.toLowerCase();
						if (type != "text" && type != "password")
							break; //ignore it
						//fall thru
					case "TEXTAREA":
						bSend = zkau.textbox && zkau.textbox.updateChange(inp, false);
					}
				}

				zkau.send({uuid: n.id, cmd: evtnm, ctl: true,
					data: [keycode, evt.ctrlKey, evt.shiftKey, evt.altKey]},
					38);
				Event.stop(evt);
				return false;
			}
			if ("onCancel" == evtnm && $type(n) == "Wnd") {
				if (getZKAttr(n, "closable") == "true") {
					zkau.sendOnClose(n);
					Event.stop(evt);
					return false;
					//20060504: Bug 1481676: Tom Yeh
					//We have to stop ESC event. Otherwise, it terminates
					//Ajax connection (issue by close)
				}
				break;
			}
			if (zkAttrSkip && getZKAttr(n, zkAttrSkip) == "true")
				break; //nothing to do
		}
	}
	return true; //no special processing
}
/** returns whether a key code is specified in keys. */
zkau._inCtkeys = function (evt, zkcode, keys) {
	if (keys) {
		//format: ctl+k;alt+k;shft+k;k
		var cc = evt.ctrlKey ? '^': evt.altKey ? '@': evt.shiftKey ? '$': '#';
		var j = keys.indexOf(cc), k = keys.indexOf(';', j + 1);
		if (j >=0 && k >= 0) {
			keys = keys.substring(j + 1, k);
			return keys.indexOf(zkcode) >= 0;
		}
	}
	return false;
};

zkau.sendOnMove = function (cmp, keys) {
	zkau.send({uuid: cmp.id, cmd: "onMove",
		data: [cmp.style.left, cmp.style.top, keys ? keys: ""]},
		zkau.asapTimeout(cmp, "onMove"));
};
zkau.sendOnZIndex = function (cmp) {
	zkau.send({uuid: cmp.id, cmd: "onZIndex",
		data: [cmp.style.zIndex]}, zkau.asapTimeout(cmp, "onZIndex"));
};
zkau.sendOnSize = function (cmp, keys) {
	zkau.send({uuid: cmp.id, cmd: "onSize",
		data: [cmp.style.width, cmp.style.height, keys]},
		zkau.asapTimeout(cmp, "onSize"));
};
zkau.sendOnClose = function (uuid, closeFloats) {
	if (closeFloats) zkau.closeFloats();
	el = $e(uuid);
	zkau.send({uuid: el.id, cmd: "onClose", data: null}, 5);
};

/** Closes popups and floats.
 * Return false if nothing changed.
 * @param arguments a list of component (or its ID) to exclude if
 * a popup contains any of them
 */
zkau.closeFloats = function () {
	return zkau._closeFloats("closeFloats", arguments);
};
/** Similar to zkau.closeFloats, except it is called when a component
 * is getting the focus.
 * More precisely, it treats all floats as popup windows, i.e.,
 * floats remains if it is an ancestor of aruments.
 */
zkau.closeFloatsOnFocus = function () {
	return zkau._closeFloats("closeFloatsOnFocus", arguments);
};
zkau._closeFloats = function (method, ancestors) {
	var closed;
	for (var j = zkau._popups.length; --j >=0;) {
	//reverse order is important if popup contains another
	//otherwise, IE seem have bug to handle them correctly
		var n = $e(zkau._popups[j]);
		if ($visible(n)
		&& getZKAttr(n, "animating") != "hide"
		&& !zk.isAncestorX(n, ancestors, true)) {
		//we avoid hiding twice we have to check animating
			closed = true;
			zk.hide(n);
			zkau.send({uuid: n.id, cmd: "onOpen", data: [false]},
				zkau.asapTimeout(n, "onOpen"));
				//We have to send onOpen since the server need to know
				//whether the popup becomes invsibile
		}
	}

	//floats: combobox, context menu...
	for (var j = zkau.floats.length; --j >= 0;) {
		var n = zkau.floats[j];
		if (n[method].apply(n, ancestors))
			closed = true;
	}

	if (closed)
		zkau.hideCovered();
	return closed;
};

zkau.hideCovered = function() {
	var ary = [];
	for (var j = 0; j < zkau._popups.length; ++j) {
		var el = $e(zkau._popups[j]);
		if ($visible(el)) ary.push(el);
	}

	for (var j = 0; j < zkau.floats.length; ++j)
		zkau.floats[j].addHideCovered(ary);

	for (var j = 0; j < zkau._overlaps.length; ++j) {
		var el = $e(zkau._overlaps[j]);
		if ($visible(el)) ary.push(el);
	}
	zk.hideCovered(ary);

	if (zkau.valid) zkau.valid.uncover();
};

/** Returns the meta info associated with the specified cmp or its UUID.
 */
zkau.getMeta = function (cmp) {
	var id = typeof cmp == 'string' ? cmp: cmp ? cmp.id: null;
	if (!id) return null;
	return zkau._metas[$uuid(id)];
};
/** Returns the meta info associated with the specified cmp or its UUID.
 */
zkau.setMeta = function (cmp, info) {
	var id = typeof cmp == 'string' ? cmp: cmp ? cmp.id: null;
	if (!id) {
		zk.error(mesg.COMP_OR_UUID_REQUIRED);
		return;
	}
	if (info) zkau._metas[$uuid(id)] = info;
	else delete zkau._metas[$uuid(id)]; //save memory
};
/** Returns the info by specified any child component and the type.
 */
zkau.getMetaByType = function (el, type) {
	el = $parentByType(el, type);
	return el != null ? zkau.getMeta(el): null;
};

/** Cleans up meta of the specified component.
 * <p>Note: this method is called automatically if z.type is defined.
 * So, you need to call this method only if no z.type -- very rare.
 */
zkau.cleanupMeta = function (cmp) {
	var meta = zkau.getMeta(cmp);
	if (meta) {
		if (meta.cleanup) meta.cleanup();
		zkau.setMeta(cmp, null);
	}
};

////
//ID Space//
/** Returns element of the specified zid. */
zkau.getByZid = function (n, zid) {
	var oid = zkau._zidOwner(n);
	var v = zkau._zidsp[oid];
	if (v) {
		v = v[zid];
		if (v) return $e(v);
	}
};
zkau.initzid = function (n, zid) {
	var oid = zkau._zidOwner(n);
	var ary = zkau._zidsp[oid];
	if (!ary) ary = zkau._zidsp[oid] = {};
	if (!zid) zid = getZKAttr(n, "zid");
	ary[zid] = n.id;
};
zkau.cleanzid = function (n) {
	var oid = zkau._zidOwner(n);
	var ary = zkau._zidsp[oid];
	if (ary) delete ary[getZKAttr(n, "zid")];
};
/** Clean an ID space. */
zkau.cleanzidsp = function (n) {
	delete zkau._zidsp[n.id];
};
/** Returns the space owner that n belongs to, or null if not found. */
zkau._zidOwner = function (n) {
	//zidsp currently applied only to page, since we'd like
	//developers easy to reference other component in the same page
	//If you want to support a new space, just generae zidsp="true"
	for (var p = n; p; p = p.parentNode) {
		if (getZKAttr(p, "zidsp"))
			return p.id;
	}
	return "_zdt_" + zkau._dtid(n);
};

///////////////
//Drag & Drop//
zkau.initdrag = function (n) {
	zkau._drags[n.id] = new Draggable(n, {
		starteffect: zk.voidf,
		endeffect: zkau._enddrag, change: zkau._dragging,
		ghosting: zkau._ghostdrag, z_dragdrop: true,
		revert: zkau._revertdrag, ignoredrag: zkau._ignoredrag
	});
};
zkau.cleandrag = function (n) {
	if (zkau._drags[n.id]) {
		zkau._drags[n.id].destroy();
		delete zkau._drags[n.id];
	}
};
zkau.initdrop = function (n) {
	zkau._drops.unshift(n); //last created, first matched
};
zkau.cleandrop = function (n) {
	zkau._drops.remove(n);
};

zkau._ignoredrag = function (el, pointer) {
	return zk.eval(el, "ignoredrag", null, pointer);
};
zkau._dragging = function (dg, pointer) {
	var e = zkau._getDrop(dg.z_elorg || dg.element, pointer);
	if (!e || e != dg.zk_lastDrop) {
		zkau._cleanLastDrop(dg);
		if (e) {
			dg.zk_lastDrop = e;
			zk.backupStyle(e, "backgroundColor");
			e.style.backgroundColor = "#A8A858";
		}
	}
};
zkau._revertdrag = function (cmp, pointer) {
	if (zkau._getDrop(cmp, pointer) == null)
		return true;

	//Note: we hve to revert when zkau._onRespReady called, since app might
	//change cmp's position
	var dg = zkau._drags[cmp.id];
	var orgpos = cmp.style.position;
	zkau._revertpending = function () {
		//Bug 1599737: a strange bar appears
		if (zk.ie && orgpos != 'absolute' && orgpos != 'relative')
			zkau._fixie4drop(cmp, orgpos);
		cmp.style.left = dg.z_x;
		cmp.style.top = dg.z_y;
		zkau._revertpending = null; //exec once
	};
	return false;
};
if (zk.ie) {
//In IE, we have to detach and attach. We cannot simply restore position!!
//Otherwise, a strange bar appear
	zkau._fixie4drop = function (el, orgpos) {
		var p = el.parentNode;
		var n = el.nextSibling;
		zk.remove(el);
		el.style.position = orgpos;
		if (n) p.insertBefore(el, n);
		else p.appendChild(el);
	};
}

zkau._enddrag = function (cmp, evt) {
	zkau._cleanLastDrop(zkau._drags[cmp.id]);
	var pointer = [Event.pointerX(evt), Event.pointerY(evt)];
	var e = zkau._getDrop(cmp, pointer);
	if (e) {
		var keys = "";
		if (evt) {
			if (evt.altKey) keys += 'a';
			if (evt.ctrlKey) keys += 'c';
			if (evt.shiftKey) keys += 's';
		}
		setTimeout("zkau._sendDrop('"+cmp.id+"','"+e.id+"','"+pointer[0]+"','"+pointer[1]+"','"+keys+"')", 38);
			//In IE, listitem is selected after _enddrag, so we have to
			//delay the sending of onDrop
	}
};
zkau._sendDrop = function (dragged, dropped, x, y, keys) {
	zkau.send({uuid: dropped, cmd: "onDrop", data: [dragged, x, y, keys]});
};
zkau._getDrop = function (cmp, pointer) {
	var dragType = getZKAttr(cmp, "drag");
	var found = null;
	l_next:
	for (var j = 0; j < zkau._drops.length; ++j) {
		var e = zkau._drops[j];
		if (e == cmp) continue; //dropping to itself not allowed

		var dropTypes = getZKAttr(e, "drop");
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
		if (Position.withinIncludingScrolloffsets(e, pointer[0], pointer[1])
		&& (!found || found.style.zIndex < e.style.zIndex))
				found = e;
	}
	return found;
};
zkau._cleanLastDrop = function (dg) {
	if (dg && dg.zk_lastDrop) {
		zk.restoreStyle(dg.zk_lastDrop, "backgroundColor");
		dg.zk_lastDrop = null;
	}
};
zkau._ghostdrag = function (dg, ghosting) {
//Tom Yeh: 20060227: Use a 'fake' DIV if
//1) FF cannot handle z-index well if listitem is dragged across two listboxes
//2) Safari's ghosting position is wrong
//3) Opera's width is wrong if cloned
	var special;
	if (!zk.ie) {
		if (ghosting) {
			var tn = $tag(dg.element);
			zk.zk_special = special = "TR" == tn || "TD" == tn || "TH" == tn;
		} else {
			special = zk.zk_special;
		}
	}

	if (special) {
		if (ghosting) {
			var ofs = zkau.beginGhostToDIV(dg);
			var el = dg.element;
			document.body.insertAdjacentHTML("afterbegin",
				'<div id="zk_ddghost" style="position:absolute;top:'
				+ofs[1]+'px;left:'+ofs[0]+'px;width:'
				+zk.offsetWidth(el)+'px;height:'+zk.offsetHeight(el)
				+'px;border:1px dotted black"></div>');
			dg.element = $e("zk_ddghost");
		} else {
			zkau.endGhostToDIV(dg);
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
		setTimeout("zk.dragging=false", 0);
			//we have to reset it later since onclick is fired later (after onmouseup)
	}
	return true;
};
/** Prepares to ghost dg.element to a DIV.
 * It is used when you want to ghost with a div.
 * @return the offset of dg.element.
 */
zkau.beginGhostToDIV = function (dg) {
	zk.dragging = true;
	dg.delta = dg.currentDelta();
	dg.z_elorg = dg.element;

	var ofs = Position.cumulativeOffset(dg.element);
	dg.z_scrl = Position.realOffset(dg.element);
	dg.z_scrl[0] -= zk.innerX(); dg.z_scrl[1] -= zk.innerY();
		//Store scrolling offset since Draggable.draw not handle DIV well

	ofs[0] -= dg.z_scrl[0]; ofs[1] -= dg.z_scrl[1];
	return ofs;
};
/** Returns the origin element before ghosted.
 * <p>Note: the ghosted DIV is dg.element.
 * It is called between beginGhostToDIV and endGhostToDIV
 */
zkau.getGhostOrgin = function (dg) {
	return dg.z_elorg;
};
/** Clean and remove the ghosted DIV.
 */
zkau.endGhostToDIV = function (dg) {
	setTimeout("zk.dragging=false", 0);
		//we have to reset it later since onclick is fired later (after onmouseup)
	if (dg.z_elorg && dg.element != dg.z_elorg) {
		zk.remove(dg.element);
		dg.element = dg.z_elorg;
		delete dg.z_elorg;
	}
};

//Upload//
/** Begins upload (called when the submit button is pressed)
 * @param wndid id of window to close, if any
 */
zkau.beginUpload = function (wndid) {
	zkau.endUpload();
	zkau._upldWndId = wndid;
	zkau._tmupload = setInterval(function () {
		zkau.send({uuid: '', cmd: "getUploadInfo", data: null});
	}, 660);
};
zkau.updateUploadInfo = function (p, cb) {
	if (cb <= 0) zkau.endUpload();
	else if (zkau._tmupload) {
		var img = $e("zk_upload!img");
		if (!img) {
			var html = '<div id="zk_upload" style="position:absolute;border:1px solid #77a;padding:9px;background-color:#fec;z-index:79000">'
				+'<div style="width:202px;border:1px inset"><img id="zk_upload!img" src="'+zk.getUpdateURI('/web/zk/img/prgmeter.gif')
				+'"/></div><br/>'+mesg.FILE_SIZE+Math.round(cb/1024)+mesg.KBYTES
				+'<br/><input type="button" value="'+mesg.CANCEL+'" onclick="zkau._cancelUpload()"</div>';
			document.body.insertAdjacentHTML("afterbegin", html);
			zk.center($e("zk_upload"));
			img = $e("zk_upload!img");
		}
		if (p >= 0 && p <= 100) {
			img.style.height = "10px"; //avoid being scaled when setting width
			img.style.width = (p * 2) + "px";
		}
	}
};
zkau._cancelUpload = function () {
	zkau.endUpload();

	if (zkau._upldWndId) {
		zkau.sendOnClose(zkau._upldWndId);
		zkau._upldWndId = null;
	}
};
/** Called to end the upload. It must be called if beginUpload is called.
 */
zkau.endUpload = function () {
	zk.focus(window);
		//focus might be in iframe of fileupload dlg, so get it back
		//otherwise, IE might lose focus forever (see also Bug 1526542)

	zk.remove($e("zk_upload"));
	if (zkau._tmupload) {
		clearInterval(zkau._tmupload);
		zkau._tmupload = null;
	}
};

//Miscellanous//
zkau.history = new zk.History();

//Commands//
zkau.cmd0 = { //no uuid at all
	bookmark: function (dt0) {
		zkau.history.bookmark(dt0);
	},
	obsolete: function (dt0, dt1) { //desktop timeout
		zkau._cleanupOnFatal();
		zk.error(dt1);
	},
	alert: function (msg) {
		alert(msg);
	},
	redirect: function (url, target) {
		try {
			if (target) zk.go(url, false, target);
			else if (url) document.location.href = url;
			else document.location.reload();
		} catch (ex) {
			if (!zkau.confirmClose) throw ex;
		}
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
		zkau._cInfoReg = true;
		zkau.send({uuid: "", cmd: "onClientInfo", data: [
			new Date().getTimezoneOffset(),
			screen.width, screen.height, screen.colorDepth,
			zk.innerWidth(), zk.innerHeight(), zk.innerX(), zk.innerY()
		]});
	},
	download: function (url) {
		if (url) {
			var ifr = $e('zk_download');
			if (ifr) {
				ifr.src = url; //It is OK to reuse the same iframe
			} else {
				var html = '<iframe src="'+url+'" id="zk_download" name="zk_download" style="display:none;width:0;height:0;border:0"></iframe>';
				zk.insertHTMLBeforeEnd(document.body, html);
			}
		}
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
	},
	cfmClose: function (msg) {
		zkau.confirmClose = msg;
	}
};
zkau.cmd1 = {
	wrongValue: function (uuid, cmp, dt1) {
		if (cmp) {
			cmp = $real(cmp); //refer to INPUT (e.g., datebox)

			//we have to update default value so validation will be done again
			var old = cmp.value;
			cmp.defaultValue = old + "_err"; //enforce to validate
			if (old != cmp.value) cmp.value = old; //Bug 1490079 (FF only)

			if (zkau.valid) zkau.valid.errbox(cmp.id, dt1);
			else alert(dt1);
		} else {
			alert(dt1);
		}
	},
	setAttr: function (uuid, cmp, dt1, dt2) {
		if (dt1 == "z.init" || dt1 == "z.chchg") { //initialize
			//Note: cmp might be null because it might be removed
			if (cmp) {
				var type = $type(cmp);
				if (type) {
					zk.loadByType(cmp);
					if (zk.loading) {
						zk.addInitCmp(cmp);
					} else {
						zk.eval(cmp, dt1 == "z.init" ? "init": "childchg", type);
					}
				}
			}
			return; //done
		}

		var done = false;
		if ("z.drag" == dt1) {
			if (!getZKAttr(cmp, "drag")) zkau.initdrag(cmp);
			zkau.setAttr(cmp, dt1, dt2);
			done = true;
		} else if ("z.drop" == dt1) {
			if (!getZKAttr(cmp, "drop")) zkau.initdrop(cmp);
			zkau.setAttr(cmp, dt1, dt2);
			done = true;
		} else if ("zid" == dt1) {
			zkau.cleanzid(cmp);
			if (dt2) zkau.initzid(cmp, dt2);
		}

		if (zk.eval(cmp, "setAttr", null, dt1, dt2)) //NOTE: cmp is NOT converted to real!
			return; //done

		if (!done) {
			if (dt1.startsWith("on")) cmp = $real(cmp);
				//Client-side-action must be done at the inner tag

			zkau.setAttr(cmp, dt1, dt2);
		}
	},
	rmAttr: function (uuid, cmp, dt1) {
		var done = false;
		if ("z.drag" == dt1) {
			zkau.cleandrag(cmp);
			zkau.rmAttr(cmp, dt1);
			done = true;
		} else if ("z.drop" == dt1) {
			zkau.cleandrop(cmp);
			zkau.rmAttr(cmp, dt1);
			done = true;
		}

		if (zk.eval(cmp, "rmAttr", null, dt1)) //NOTE: cmp is NOT converted to real!
			return; //done

		if (!done) {
			if (dt1.startsWith("on")) cmp = $real(cmp);
				//Client-side-action must be done at the inner tag
			zkau.rmAttr(cmp, dt1);
		}
	},
	outer: function (uuid, cmp, html) {
		zk.cleanupAt(cmp);
		var from = cmp.previousSibling, from2 = cmp.parentNode,
			to = cmp.nextSibling;
		zk.setOuterHTML(cmp, html);
		if (from) zkau._initSibs(from, to, true);
		else zkau._initChildren(from2, to);
		if (zkau.valid) zkau.valid.fixerrboxes();
	},
	addAft: function (uuid, cmp, html) {
		var n = $childExterior(cmp);
		var to = n.nextSibling;
		zk.insertHTMLAfter(n, html);
		zkau._initSibs(n, to, true);
	},
	addBfr: function (uuid, cmp, html) {
		var n = $childExterior(cmp);
		var to = n.previousSibling;
		zk.insertHTMLBefore(n, html);
		zkau._initSibs(n, to, false);
	},
	addChd: function (uuid, cmp, html) {
		/* To add the first child properly, it checks as follows.
		//1) a function called addFirstChild
		2) uuid + "!cave" (as parent)
		3) an attribute called z.cave to hold id (as parent)
		4) uuid + "!child" (as next sibling)
		5) uuid + "!real" (as parent)
		 */
		//if (zk.eval(cmp, "addFirstChild", html))
		//	return;

		var n = $e(uuid + "!cave");
		if (!n) {
			n = getZKAttr(cmp, "cave");
			if (n) n = $e(n);
		}
		if (n) { //as last child of n
			zkau._insertAndInitBeforeEnd(n, html);
			return;
		}

		n = $e(uuid + "!child");
		if (n) { //as previous sibling of n
			var to = n.previousSibling;
			zk.insertHTMLBefore(n, html);
			zkau._initSibs(n, to, false);
			return;
		}

		cmp = $real(cmp); //go into the real tag (e.g., tabpanel)
		zkau._insertAndInitBeforeEnd(cmp, html);
	},
	rm: function (uuid, cmp) {
		//NOTE: it is possible the server asking removing a non-exist cmp
		//so keep silent if not found
		if (cmp) {
			zk.cleanupAt(cmp);
			cmp = $childExterior(cmp);
			zk.remove(cmp);
		}
		if (zkau.valid) zkau.valid.fixerrboxes();
	},
	focus: function (uuid, cmp) {
		if (!zk.eval(cmp, "focus")) {
			zkau.autoZIndex(cmp); //some, say, window, not listen to onfocus
			cmp = $real(cmp); //focus goes to inner tag
			zk.asyncFocus(cmp.id, 10);
				//delay it since some comp, e.g., endModal, uses timer
				//to do the multiple-step operation
		}
	},
	meta: function (uuid, cmp, dt1, dt2, dt3, dt4) {
		var meta = zkau.getMeta(uuid);
		if (meta) meta[dt1].call(meta, dt2, dt3, dt4);
	},
	closeErrbox: function (uuid, cmp) {
		if (zkau.valid)
			zkau.valid.closeErrbox(uuid);
	},
	submit: function (uuid, cmp) {
		setTimeout(function (){if (cmp && cmp.submit) cmp.submit();}, 50);
	},
	cmd: function (uuid, cmp, func, arg0, arg1, arg2) {
		zk.eval(cmp, func, null, arg0, arg1, arg2);
	}
};

} //if (!window.zkau)