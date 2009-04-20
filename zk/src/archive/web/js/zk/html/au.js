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
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
if (!window.zkau) { //avoid eval twice
////
//Customization
/** Returns the background color for a list item or tree item.
 * Developer can override this method by providing a different background.
 * @param e the element
 * @param undo whether to undo the effect (false to set effect)
 */
if (!window.Droppable_effect) { //define it only if not customized
	Droppable_effect = function (e, undo) {
		zk[undo ? "rmClass" : "addClass"] (e, "z-drag-over");
	};
}
/** Handles the error caused by processing the response.
 * @param msgCode the error message code.
 * It is either an index of mesg (e.g., "FAILED_TO_PROCESS"),
 * or an error message
 * @param msg2 the additional message (optional)
 * @param cmd the command (optional)
 * @param ex the exception (optional)
 * @since 3.0.6
 */
if (!window.onProcessError) {
	onProcessError = function (msgCode, msg2, cmd, ex) {
		var msg = mesg[msgCode];
		zk.error((msg?msg:msgCode)+'\n'+(msg2?msg2:"")+(cmd?cmd:"")+(ex?"\n"+ex.message:""));
	};
}
/** Confirms the user how to handle an error.
 * Default: it shows up a message asking the user whether to retry.
 * @since 3.0.6
 */
if (!window.confirmRetry) {
	confirmRetry = function (msgCode, msg2) {
		var msg = mesg[msgCode];
		return zk.confirm((msg?msg:msgCode)+'\n'+mesg.TRY_AGAIN+(msg2?"\n\n("+msg2+")":""));
	};
}

//au//
zkau = {};

zkau._cmdsQue = []; //response commands in XML
zkau._evts = {}; //(dtid, Array()): events that are not sent yet
zkau._js4resps = []; //JS to eval upon response
zkau._metas = {}; //(id, meta)
zkau._drags = {}; //(id, zDraggable): draggables
zkau._drops = []; //dropables
zkau._zidsp = {}; //ID spaces: {owner's uuid, {id, uuid}}
zkau._stamp = 0; //used to make a time stamp
zkau.initZIndex = 30; // less than 30 is used for Component developer.
zkau.topZIndex = zkau.initZIndex; //topmost z-index for overlap/popup/modal
zkau.topZIndexStep = 3; // the step of topmost z-index for overlap/popup/modal
zkau.floats = []; //popup of combobox, bandbox, datebox...
zkau._onsends = []; //JS called before zkau.sendNow
zkau._seqId = ($now() % 9999) + 1; //1-9999 (random init: bug 2691017)
zkau._dtids = []; //an array of desktop IDs
zkau._uris = {}; //a map of update engine's URIs ({dtid, uri})
zkau._spushInfo = {} //the server-push info: Map(dtid, {min, max, factor})
var undef; //an undefined variable

/** Adds a desktop.
 * @return true if it is a new desktop (3.0.8)
 */
zkau.addDesktop = function (dtid) {
	var ds = zkau._dtids;
	for (var j = ds.length; --j >= 0;)
		if (ds[j] == dtid)
			return false; //nothing to do
	ds.push(dtid);
	return true;
};
/** Returns the desktop's ID.
 * @param {String or Element} n the component to look for its desktop ID.
 * @since 3.0.0
 */
zkau.dtid = function (n) {
	if (zkau._dtids.length == 1) return zkau._dtids[0];

	for (n = $e(n); n; n = $parent(n)) {
		var id = getZKAttr(n, "dtid");
		if (id) return id;
	}
	return null;
};
/** Returns the URI of the update engine.
 * @since 3.0.6
 */
zkau.uri = function (dtid) {
	return zkau._dtids.length <= 1 || !dtid ? zkau._uri: zkau._uris[dtid];
};
/** Adds the update engine's UIR for the specified destkop.
 * @since 3.0.6
 */
zkau.addURI = function (dtid, uri) {
	zkau._uris[dtid] = uri;
	if (!zkau._uri) zkau._uri = uri; //performance fine tune
};

zk.addInit(function () {
	zk.listen(document, "keydown", zkau._onDocKeydown);
	zk.listen(document, "mousedown", zkau._onDocMousedown);
	zk.listen(document, "mouseover", zkau._onDocMouseover);
	zk.listen(document, "mouseout", zkau._onDocMouseout);

	zk.listen(document, "contextmenu", zkau._onDocCtxMnu);
	zk.listen(document, "click", zkau._onDocLClick);
	zk.listen(document, "dblclick", zkau._onDocDClick);
	zk.listen(window, "scroll", zkau._onDocScroll);
	zk.listen(window, "resize", zkau._onResize);

	zkau._oldUnload = window.onunload;
	window.onunload = zkau._onUnload; //unable to use zk.listen

	zkau._oldBfUnload = window.onbeforeunload;
	window.onbeforeunload = zkau._onBfUnload;
});
zkau._onDocScroll = function () {
	var ix = zk.innerX(), iy = zk.innerY();
	zkau._fixOffset($e("zk_mask"), ix, iy);
	zkau._fixOffset($e("zk_loading"), ix, iy);
	zkau._fixOffset($e("zk_loadprog"), ix, iy);
	zkau._fixOffset($e("zk_prog"), ix, iy);
	var d = $e("zk_debugbox");
	if (d) {
		d.style.top = iy + zk.innerHeight() - d.offsetHeight - 20 + "px";
		d.style.left = ix + zk.innerWidth() - d.offsetWidth - 20 + "px";
	}

	zk.onScrollAt();
};
zkau._fixOffset = function (el, x, y) {
	if (!el) return;
	var ix = $int(getZKAttr(el, "x")), iy = $int(getZKAttr(el, "y"));
	var top = $int(el.style.top) + (y - iy), left = $int(el.style.left) + (x - ix);
	el.style.top = top + "px";
	el.style.left = left + "px";
	setZKAttr(el, "x", x);
	setZKAttr(el, "y", y);
};
/** Handles onclick for button-type.
 */
zkau.onclick = function (evt) {
	if (typeof evt == 'string') {
		zkau.send({uuid: $uuid(evt), cmd: "onClick", ctl: true});
		return;
	}

	if (!evt) evt = window.event;
	var target = $outer(Event.element(evt));
	var href = getZKAttr(target, "href");
	if (href) {
		zk.go(href, false, getZKAttr(target, "target"));
		Event.stop(evt); //prevent _onDocLClick
		return; //done
	}

	zkau._lastClkUuid = target.id;
	zkau.send({uuid: target.id,
		cmd: "onClick", data: zkau._getMouseData(evt, target), ctl: true});
	//Don't stop event so popup will work (bug 1734801)
	zkau.addOnSend(zkau._resetLastClickId);
};
zkau._resetLastClickId = function () {zkau._lastClkUuid = null;};
/** Handles ondblclick for button (for non-FF).
 * Note: double clicks are handled by zkau._onDocDClick, but
 * some tags (button and checkbox) eat the event, so _onDocDClick won't
 * get the event.
 */
zkau.ondblclick = function (evt) {
	if (!evt) evt = window.event;
	var cmp = Event.element(evt);

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
	var ofs = zPos.cumulativeOffset(target);
	var x = Event.pointerX(evt) - ofs[0];
	var y = Event.pointerY(evt) - ofs[1];
	return [x, y, zkau.getKeys(evt)];
};
/** Returns the key info ("acs").
 * @since 3.6.0
 */
zkau.getKeys = function (evt) {
	if (!evt) return "";
	var extra = "";
	if (evt.altKey) extra += "a";
	if (evt.ctrlKey) extra += "c";
	if (evt.shiftKey) extra += "s";
	return extra;
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
	zkau.send({uuid: uuid, cmd: "remove"}, 5);
};

////ajax resend mechanism////
/** IE6 sometimes remains readyState==1 (reason unknown), so resend. */
zkau._areqTmout = function () {
	//Note: we don't resend if readyState >= 3, since the server is already
	//processing it
	var req = zkau._areq, reqInf = zkau._areqInf;
	if (req && req.readyState < 3) {
		zkau._areq = zkau._areqInf = null;
		try {
			if(typeof req.abort == "function") req.abort();
		} catch (e2) {
		}
		if (reqInf.tmout < 60000) reqInf.tmout += 3000;
			//sever might be busy, so prolong next timeout
		zkau._areqResend(reqInf);
	}
};

zkau._areqResend = function (reqInf, timeout) {
	if (zkau._seqId == reqInf.sid) {//skip if the response was recived
		zkau._preqInf = reqInf; //store as a pending request info
		setTimeout(zkau._areqResend2, timeout ? timeout: 0);
	}
};
zkau._areqResend2 = function () {
	var reqInf = zkau._preqInf;
	if (reqInf) {
		zkau._preqInf = null;
		if (zkau._seqId == reqInf.sid)
			zkau._sendNow2(reqInf);
	}
};
/** Called when the response is received from _areq.
 */
zkau._onRespReady = function () {
	try {
		var req = zkau._areq, reqInf = zkau._areqInf;
		if (req && req.readyState == 4) {
			zkau._areq = zkau._areqInf = null;
			if (reqInf.tfn) clearTimeout(reqInf.tfn); //stop timer

			if (zk.pfmeter) zkau.pfrecv(reqInf.dtid, zkau._pfGetIds(req));

			if (zkau._revertpending) zkau._revertpending();
				//revert any pending when the first response is received

			var sid = req.getResponseHeader("ZK-SID");
			if (req.status == 200) { //correct
				if (sid && sid != zkau._seqId) {
					zkau._errcode = "ZK-SID " + (sid ? "mismatch": "required");
					return;
				} //if sid null, always process (usually for error msg)

				if (zkau.pushXmlResp(reqInf.dtid, req)) { //valid response
					//advance SID to avoid receive the same response twice
					if (sid && ++zkau._seqId > 9999) zkau._seqId = 1;
					zkau._areqTry = 0;
					zkau._preqInf = null;
				}
			} else if (!sid || sid == zkau._seqId) { //ignore only if out-of-seq (note: 467 w/o sid)
				zkau._errcode = req.status;
				var eru = zk.eru['e' + req.status];
				if (typeof eru == "string") {
					zk.go(eru);
				} else {
				//handle MSIE's buggy HTTP status codes
				//http://msdn2.microsoft.com/en-us/library/aa385465(VS.85).aspx
					switch (req.status) { //auto-retry for certain case
					default:
						if (!zkau._areqTry) break;
						//fall thru
					case 12002: //server timeout
					case 12030: //http://danweber.blogspot.com/2007/04/ie6-and-error-code-12030.html
					case 12031:
					case 12152: // Connection closed by server.
					case 12159:
					case 13030:
					case 503: //service unavailable
						if (!zkau._areqTry) zkau._areqTry = 3; //two more try
						if (--zkau._areqTry) {
							zkau._areqResend(reqInf, 200);
							return;
						}
					}

					if (!zkau._ignorable && !zkau._unloading) {
						var msg = req.statusText;
						if (confirmRetry("FAILED_TO_RESPONSE", req.status+(msg?": "+msg:""))) {
							zkau._areqTry = 2; //one more try
							zkau._areqResend(reqInf);
							return;
						}
					}

					zkau._cleanupOnFatal(zkau._ignorable);
				}
			}
		}
	} catch (e) {
		if (!window.zkau)
			return; //the doc has been unloaded

		zkau._areq = zkau._areqInf = null;
		try {
			if(req && typeof req.abort == "function") req.abort();
		} catch (e2) {
		}

		//NOTE: if connection is off and req.status is accessed,
		//Mozilla throws exception while IE returns a value
		if (!zkau._ignorable && !zkau._unloading) {
			var msg = e.message;
			zkau._errcode = "[Receive] " + msg;
			//if (e.fileName) zkau._errcode += ", "+e.fileName;
			//if (e.lineNumber) zkau._errcode += ", "+e.lineNumber;
			if (confirmRetry("FAILED_TO_RESPONSE", (msg&&msg.indexOf("NOT_AVAILABLE")<0?msg:""))) {
				zkau._areqResend(reqInf);
				return;
			}
		}
		zkau._cleanupOnFatal(zkau._ignorable);
	}

	//handle pending ajax send
	if (zkau._sendPending && !zkau._areq && !zkau._preqInf) {
		zkau._sendPending = false;
		var ds = zkau._dtids;
		for (var j = ds.length; --j >= 0;)
			zkau._send2(ds[j], 0);
	}

	zkau.doCmds();
	zkau._checkProgress();
};
/** Parses a XML response and pushes the parsed commands to the queue.
 * @return false if no command found at all
 * @since 3.0.8
 */
zkau.pushXmlResp = function (dtid, req) {
	var xml = req.responseXML;
	if (!xml) {
		if (zk.pfmeter) zkau.pfdone(dtid, zkau._pfGetIds(req));
		return false; //invalid
	}

	var cmds = [],
		rs = xml.getElementsByTagName("r"),
		rid = xml.getElementsByTagName("rid");
	if (zk.pfmeter) {
		cmds.dtid = dtid;
		cmds.pfIds = zkau._pfGetIds(req);
	}

	if (rid && rid.length) {
		rid = $int(zk.getElementValue(rid[0])); //response ID
		if (!isNaN(rid)) cmds.rid = rid;
	}

	for (var j = 0, rl = rs ? rs.length: 0; j < rl; ++j) {
		var cmd = rs[j].getElementsByTagName("c")[0],
			data = rs[j].getElementsByTagName("d");

		if (!cmd) {
			zk.error(mesg.ILLEGAL_RESPONSE+"Command required");
			continue;
		}

		cmds.push(cmd = {cmd: zk.getElementValue(cmd)});
		cmd.data = [];
		for (var k = data ? data.length: 0; --k >= 0;)
			cmd.data[k] = zk.getElementValue(data[k]);
	}

	zkau._cmdsQue.push(cmds);
	return true;
};
/** Checks whether to turn off the progress prompt.
 * @return true if the processing is done
 */
zkau._checkProgress = function () {
	if (zkau.processing())
		return false;
	zk.progressDone();
	return true;
};
/** Returns whether any request is in processing.
 * @since 3.0.0
 */
zkau.processing = function () {
	return zkau._cmdsQue.length || zkau._areq || zkau._preqInf
		|| zkau._doingCmds;
};

/** Returns the timeout of the specified event.
 * It is mainly used to generate the timeout argument of zkau.send.
 *
 * @param timeout if non-negative, it is used when zkau.asap is true.
 */
zkau.asapTimeout = function (cmp, evtnm, timeout) {
	var asap = zkau.asap(cmp = $e(cmp), evtnm), fmt;
	if (!asap && evtnm == "onChange") {
		fmt = getZKAttr(cmp, "srvald");
		if (fmt) { //srvald specified
			fmt = fmt == "fmt";
			asap = !fmt; //if not fmt (and not null), it means server-side validation required
		}
	}
	return asap ? timeout >= 0 ? timeout: 38:
		fmt ? 350: -1; //if fmt we have to send it but not OK to a bit delay
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
/** Returns an array of queued events.
 * @since 3.0.0
 */
zkau.events = function (uuid) {
	return zkau._events(zkau.dtid(uuid));
};
/** Sends a request to the client
 * @param timout milliseconds.
 * If negative, it won't be sent until next non-negative event
 */
zkau.send = function (evt, timeout) {
	if (timeout < 0) evt.implicit = true;

	if (evt.uuid) {
		zkau._send(zkau.dtid(evt.uuid), evt, timeout);
	} else if (evt.dtid) {
		zkau._send(evt.dtid, evt, timeout);
	} else {
		var ds = zkau._dtids;
		for (var j = 0, dl = ds.length; j < dl; ++j)
			zkau._send(ds[j], evt, timeout);
	}
};
/** A shortcut of zkau.send(evt, zkau.asapTimeout(evt.uuid, evt.cmd, timeout)).
 * @since 3.0.5
 */
zkau.sendasap = function (evt, timeout) {
	zkau.send(evt, zkau.asapTimeout(evt.uuid, evt.cmd, timeout));
};
zkau._send = function (dtid, evt, timeout) {
	if (evt.ctl && zk_clkflto > 0) {
		//Don't send the same request if it is in processing
		if (zkau._areqInf && zkau._areqInf.ctli == evt.uuid
		&& zkau._areqInf.ctlc == evt.cmd)
			return;

		var t = $now();
		if (zkau._ctli == evt.uuid && zkau._ctlc == evt.cmd //Bug 1797140
		&& t - zkau._ctlt < zk_clkflto)
			return; //to prevent key stroke are pressed twice (quickly)

		//Note: it is still possible to queue two ctl with same uuid and cmd,
		//if the first one was not sent yet and the second one is generated
		//after 390ms.
		//However, it is rare so no handle it

		zkau._ctlt = t;
		zkau._ctli = evt.uuid;
		zkau._ctlc = evt.cmd;
	}

	zkau._events(dtid).push(evt);

	//Note: we don't send immediately (Bug 1593674)
	//Note: Unlike sendAhead and _send2, if timeout is undefined,
	//it is considered as 0.
	zkau._send2(dtid, timeout ? timeout: 0);
};
/** @param timeout if undefined or negative, it won't be sent. */
zkau._send2 = function (dtid, timeout) {
	if (dtid && timeout >= 0) setTimeout("zkau.sendNow('"+dtid+"')", timeout);
};
/** Sends a request before any pending events.
 * @param timout milliseconds.
 * If undefined or negative, it won't be sent until next non-negative event
 * Note: Unlike zkau.send, it considered undefined as not sending now
 * (reason: backward compatible)
 */
zkau.sendAhead = function (evt, timeout) {
	var dtid;
	if (evt.uuid) {
		zkau._events(dtid = zkau.dtid(evt.uuid)).unshift(evt);
	} else if (evt.dtid) {
		zkau._events(dtid = evt.dtid).unshift(evt);
	} else {
		var ds = zkau._dtids;
		for (var j = ds.length; --j >= 0; ++j) {
			zkau._events(ds[j]).unshift(evt);
			zkau._send2(ds[j], timeout); //Spec: don't convert unefined to 0 for timeout
		}
		return;
	}
	zkau._send2(dtid, timeout);
};
/** Sends any queued event of the specified desktop.
 * @return false if no queued event at all.
 */
zkau.sendNow = function (dtid) {
	var es = zkau._events(dtid);
	if (es.length == 0)
		return false; //nothing to do

	if (zk.loading) {
		zk.addInit(function () {zkau.sendNow(dtid);});
		return true; //wait
	}

	if (zkau._areq || zkau._preqInf) { //send ajax request one by one
		zkau._sendPending = true;
		return true; //wait
	}

	//callback (fckez uses it to ensure its value is sent back correctly
	for (var j = 0, ol = zkau._onsends.length; j < ol; ++j) {
		try {
			zkau._onsends[j](implicit); //it might add more events
		} catch (e) {
			zk.error(e.message);
		}
	}

	//bug 1721809: we cannot filter out ctl even if zkau.processing

	//decide implicit and ignorable
	var implicit = true, ignorable = true, ctli, ctlc;
	for (var j = es.length; --j >= 0;) {
		var evt = es[j];
		if (implicit && !evt.ignorable) { //ignorable implies implicit
			ignorable = false;
			if (!evt.implicit)
				implicit = false;
		}
		if (evt.ctl && !ctli) {
			ctli = evt.uuid;
			ctlc = evt.cmd;
		}
	}
	zkau._ignorable = ignorable;

	//Consider XML (Pros: ?, Cons: larger packet)
	var content = "";
	for (var j = 0, el = es.length; el; ++j, --el) {
		var evt = es.shift();
		content += "&cmd."+j+"="+evt.cmd+"&uuid."+j+"="+(evt.uuid?evt.uuid:'');
		if (evt.data)
			for (var k = 0, dl = evt.data.length; k < dl; ++k) {
				var data = evt.data[k];
				content += "&data."+j+"="
					+ (data != null ? encodeURIComponent(data): '_z~nil');
			}
	}

	if (content)
		zkau._sendNow2({
			sid: zkau._seqId, uri: zkau.uri(dtid),
			dtid: dtid, content: "dtid=" + dtid + content,
			ctli: ctli, ctlc: ctlc, implicit: implicit, ignorable: ignorable,
			tmout: 0
		});
	return true;
};
zkau._sendNow2 = function(reqInf) {
	var req = zkau.ajaxRequest(),
		uri = zkau._useQS(reqInf) ? reqInf.uri + '?' + reqInf.content: null;
	zkau.sentTime = $now(); //used by server-push (zkex)
	try {
		req.onreadystatechange = zkau._onRespReady;
		req.open("POST", uri ? uri: reqInf.uri, true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		req.setRequestHeader("ZK-SID", reqInf.sid);
		if (zkau._errcode) {
			req.setRequestHeader("ZK-Error-Report", zkau._errcode);
			delete zkau._errcode;
		}

		if (zk.pfmeter) zkau._pfsend(reqInf.dtid, req);

		zkau._areq = req;
		zkau._areqInf = reqInf;
		if (zk_resndto > 0)
			zkau._areqInf.tfn = setTimeout(zkau._areqTmout, zk_resndto + reqInf.tmout);

		if (uri) req.send(null);
		else req.send(reqInf.content);

		if (!reqInf.implicit) zk.progress(zk_procto); //wait a moment to avoid annoying
	} catch (e) {
		//handle error
		try {
			if(typeof req.abort == "function") req.abort();
		} catch (e2) {
		}

		if (!reqInf.ignorable && !zkau._unloading) {
			var msg = e.message;
			zkau._errcode = "[Send] " + msg;
			if (confirmRetry("FAILED_TO_SEND", msg)) {
				zkau._areqResend(reqInf);
				return;
			}
		}
		zkau._cleanupOnFatal(reqInf.ignorable);
	}
};
//IE: use query string if possible to avoid IE incomplete-request problem
zkau._useQS = zk.ie ? function (reqInf) {
	var s = reqInf.content, j = s.length, prev, cc;
	if (j + reqInf.uri.length < 2000) {
		while (--j >= 0) {
			cc = s.charAt(j);
			if (cc == '%' && prev >= '8') //%8x, %9x...
				return false;
			prev = cc;
		}
		return true;
	}
	return false;
}: zk.voidf;

/** Registers a script that will be evaluated when the next response is back.
 * Note: it executes only once, so you have to register again if necessary.
 * @param script a piece of JavaScript, or a function
 */
zkau.addOnResponse = function (script) {
	zkau._js4resps.push(script);
};
/** Evaluates scripts registered by addOnResponse. */
zkau._evalOnResponse = function () {
	while (zkau._js4resps.length)
		setTimeout(zkau._js4resps.shift(), 0);
};

/** Process the response response commands.
 * @since 3.0.7
 */
zkau.doCmds = function () {
	//avoid reentry since it calls loadAndInit, and loadAndInit call this
	if (zkau._doingCmds) {
		setTimeout(zkau.doCmds, 10);
	} else {
		zkau._doingCmds = true;
		try {
			zkau._doCmds0();
		} finally {
			zkau._doingCmds = false;

			if (zkau._checkProgress())
				zkau.doneTime = $now();
		}
	}
};
zkau._doCmds0 = function () {
	var ex, j = 0, que = zkau._cmdsQue, rid = zkau._resId;
	for (; j < que.length; ++j) {
		if (zk.loading) {
			zk.addInit(zkau.doCmds); //wait until the loading is done
			return;
		}

		var cmds = que[j];
		if (rid == cmds.rid || !rid || !cmds.rid //match
		|| zkau._dtids.length > 1) { //ignore multi-desktops (risky but...)
			que.splice(j, 1);

			var oldrid = rid;
			if (cmds.rid) {
				if ((rid = cmds.rid + 1) >= 1000)
					rid = 1; //1~999
				zkau._resId = rid;
			}

			try {
				if (zkau._doCmds1(cmds)) { //done
					j = -1; //start over
					if (zk.pfmeter) zkau.pfdone(cmds.dtid, cmds.pfIds);
				} else { //not done yet (=zk.loading)
					zkau._resId = oldrid; //restore
					que.splice(j, 0, cmds); //put it back
					zk.addInit(zkau.doCmds);
					return;
				}
			} catch (e) {
				if (!ex) ex = e;
				j = -1; //start over
			}
		}
	}

	if (que.length) { //sequence is wrong => enforce to run if timeout
		setTimeout(function () {
			if (que.length && rid == zkau._resId) {
				var r = que[0].rid;
				for (j = 1; j < que.length; ++j) { //find min
					var r2 = que[j].rid,
						v = r2 - r;
					if (v > 500 || (v < 0 && v > -500)) r = r2;
				}
				zkau._resId = r;
				zkau.doCmds();
			}
		}, 3600);
	}

	if (ex) throw ex;
};
zkau._doCmds1 = function (cmds) {
	var processed;
	try {
		while (cmds && cmds.length) {
			if (zk.loading)
				return false;

			processed = true;
			var cmd = cmds.shift();
			try {
				zkau.process(cmd.cmd, cmd.data);
			} catch (e) {
				onProcessError("FAILED_TO_PROCESS", null, cmd.cmd, e);
				throw e;
			}
		}
	} finally {
		if (processed && (!cmds || !cmds.length))
			zkau._evalOnResponse();
	}
	return true;
};
/** Process a command.
 */
zkau.process = function (cmd, data) {
	//I. process commands that data[0] is not UUID
	var fn = zkau.cmd0[cmd];
	if (fn) {
		fn.apply(zkau, data);
		return;
	}

	//I. process commands that require uuid
	if (!data || !data.length) {
		onProcessError("ILLEGAL_RESPONSE", "uuid is required for ", cmd);
		return;
	}

	fn = zkau.cmd1[cmd];
	if (fn) {
		data.splice(1, 0, $e(data[0])); //insert cmp
		fn.apply(zkau, data);
		return;
	}

	onProcessError("ILLEGAL_RESPONSE", "Unknown command: ", cmd);
};
/** Used by ZkFns. */
zk.process = function (cmd) {
	var args = [];
	for (var j = arguments.length; --j > 0;)
		args[j - 1] = arguments[j];
	zkau.process(cmd, args);
};

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
	cmp = zkau._attr(cmp, name);

	if ("visibility" == name) {
		zk.setVisible(cmp, value == "true");
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
		for (var j = 0, ol = cmp.options.length; j < ol; ++j)
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
			&& !getZKAttr(cmp, "float")) {
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
zkau._attr = function (cmp, name) {
	var real = $real(cmp);
	if (real != cmp && real) {
		if (name.startsWith("on")) return real;
			//Client-side-action must be done at the inner tag

		switch ($tag(real)) {
		case "INPUT":
		case "TEXTAREA":
			switch(name) {
			case "name": case "value": case "defaultValue":
			case "checked": case "defaultChecked":
			case "cols": case "size": case "maxlength":
			case "type": case "disabled": case "readOnly":
			case "rows":
				return real;
			}
			break;
		case "IMG":
			switch (name) {
			case "align": case "alt": case "border":
			case "hspace": case "vspace": case "src":
				return real;
			}
		}
	}
	return cmp;
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
	cmp = zkau._attr(cmp, name);

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
 * @param autoz whether to adjust zIndex only if necessary.
 * If false (used when creating a window), it always increases zIndex
 */
zkau.fixZIndex = function (cmp, silent, autoz) {
	if (!zkau._popups.length && !zkau._overlaps.length && !zkau._modals.length)
		zkau.topZIndex = zkau.initZIndex; //reset it!

	var zi = $int(cmp.style.zIndex);
	if (zi > zkau.topZIndex) {
		zkau.topZIndex = zi;
	} else if (!autoz || zi < zkau.topZIndex) {
		cmp.style.zIndex = zkau.topZIndex += zkau.topZIndexStep;
		if (!silent && cmp.id) {
			cmp = $outer(cmp);
			zkau.sendOnZIndex(cmp);
		}
	}
};
/** Automatically adjust z-index if node is part of popup/overalp/...
 */
zkau.autoZIndex = function (node) {
	for (; node; node = $parent(node)) {
		if (node.style && node.style.position == "absolute") {
			if (getZKAttr(node, "autoz"))
				zkau.fixZIndex(node, false, true); //don't inc if equals
		}
	}
};

//-- popup --//
if (!zkau._popups) {
	zkau._popups = []; //uuid
	zkau._overlaps = []; //uuid
	zkau._modals = []; //uuid (used zul.js or other modal)
}

/** Returns the current modal window's ID, or null.
 * @since 3.0.4
 */
zkau.currentModalId = function () {
	var modals = zkau._modals;
	return modals.length ? modals[modals.length - 1]: null;
};
/** Checks if we can move focus to the specified element.
 * If it is not in the current modal, false is returned and
 * focus is moved back to the current modal.
 *
 * @param checkOnly if true, the focus won't be changed.
 * @since 3.0.4
 */
zkau.canFocus = function (el, checkOnly) {
	var modalId = zkau.currentModalId();
	if (modalId && !zk.isAncestor(modalId, el)) {
		if (!checkOnly) {
			//Note: we cannot change focus to SPAN/DIV, but they might
			//gain focus (such as selecting a piece of text)
			var cf = zkau.currentFocus, cftn = $tag(cf);
			if (cf && cf.id && cftn != "SPAN" && cftn != "DIV"
			&& zk.isAncestor(modalId, cf.id))
				zk.asyncFocus(cf.id);
			else
				zk.asyncFocusDown(modalId);
		}
		return false;
	}
	return true;
};

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
	zkau.onfocus0(evtel);
};
/** When a component implements its own onfocus, it shall call back this
 * method and ignore the event if this method returns false.
 * Like zkau.onfocus except returns false if it shall be ignored.
 * @since 3.0.4
 */
zkau.onfocus0 = function (evtel, silent) { //accept both evt and cmp
	var el = zkau.evtel(evtel);
	if (!zkau.canFocus(el)) return false;

	zkau.currentFocus = el; //_onDocMousedown doesn't take care all cases
	zkau.closeFloatsOnFocus(el);
	if (zkau.valid) zkau.valid.uncover(el);

	zkau.autoZIndex(el);

	var cmp = $outer(el);
	if (!silent && zkau.asap(cmp, "onFocus"))
		zkau.send({uuid: cmp.id, cmd: "onFocus"}, 100);
	return true;
};
/**
 * @param noonblur not to send the onblur event (3.0.5)
 */
zkau.onblur = function (evtel, noonblur) {
	var el = zkau.evtel(evtel);
	if (el == zkau.currentFocus) zkau.currentFocus = null;
		//Note: _onDocMousedown is called before onblur, so we have to
		//prevent it from being cleared

	if (!noonblur && !zk.alerting) {
		var cmp = $outer(el);
		if (zkau.asap(cmp, "onBlur"))
			zkau.send({uuid: cmp.id, cmd: "onBlur"}, 100);
	}
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

/** Callback when iframe's URL/bookmark been changed.
 * Notice the containing page might not be ZK. It could be any technology
 * and it can got the notification by implementing this method.
 * @param uuid the component UUID
 * @param url the new URL
 * @since 3.5.0
 */
function onIframeURLChange(uuid, url) {
	if (zkau._unloading)
		return;

	zkau.sendasap({uuid: uuid, cmd: "onURIChange", data: [url]});
};
zkau.onURLChange = function () {
	try {
		var ifr = window.frameElement;
		if (!parent || parent == window || !ifr) //not iframe
			return;

		var l0 = parent.location, l1 = location,
			url = l0.protocol != l1.protocol || l0.host != l1.host
			|| l0.port != l1.port ? l1.href: l1.pathname,
			j = url.lastIndexOf(';'), k = url.lastIndexOf('?');
		if (j >= 0 && (k < 0 || j < k)) {
			var s = url.substring(0, j);
			url = k < 0 ? s: s + url.substring(k);
		}
		if (l1.hash && "#" != l1.hash) url += l1.hash;

		if (getZKAttr(ifr, "xsrc") != ifr.src) {//the first zul page being loaded
			var ifrsrc = ifr.src, loc = location.pathname;
			setZKAttr(ifr, "xsrc", ifrsrc);

		//The first zul page might or might not be ifr.src
		//We have to compare ifr.src with location
		//Gecko/Opera/Safari: ifr.src is a complete URL (including http://)
		//IE: ifr.src has no http://hostname/ (actually, same as server's value)
		//Opera: location.pathname has bookmark and jsessionid
		//Tomcat: /path;jsessionid=xxx#abc?xyz
			ifrsrc = zkau._simplifyURL(ifrsrc);
			loc = zkau._simplifyURL(loc);
			if (ifrsrc.endsWith(loc)
			|| loc.endsWith(ifrsrc)) { //the non-zul page is ifr.src
				setZKAttr(ifr, "xurl", url);
				return; //not notify if changed by server
			}
		}

		if (parent.onIframeURLChange && getZKAttr(ifr, "xurl") != url) {
			parent.onIframeURLChange(ifr.id, url);
			setZKAttr(ifr, "xurl", url);
		}
	} catch (e) { //due to JS sandbox, we cannot access if not from same host
		if (zk.debugJS) zk.debug("Unable to access parent frame");
	}
};
zkau._simplifyURL = function (url) {
	var j = url.lastIndexOf(';');
	if (j >= 0) url = url.substring(0, j);
	j = url.lastIndexOf('#');
	if (j >= 0) url = url.substring(0, j);
	j = url.lastIndexOf('?');
	if (j >= 0) url = url.substring(0, j);
	return url;
};

/** Handles window.onunload. */
zkau._onUnload = function () {
	zkau._unloading = true; //to disable error message

	if (zk.gecko) zk.restoreDisabled(); //Workaround Nav: Bug 1495382

	//20061109: Tom Yeh: Failed to disable Opera's cache, so it's better not
	//to remove the desktop.
	//Good news: Opera preserves the most udpated content, when BACK to
	//a cached page, its content. OTOH, IE/FF/Safari cannot.
	//Note: Safari won't send rmDesktop when onunload is called
	var bRmDesktop = !zk.opera && !zk.keepDesktop;
	if (bRmDesktop || zk.pfmeter) {
		try {
			var ds = zkau._dtids;
			for (var j = 0, dl = ds.length; j < dl; ++j) {
				var dtid = ds[j],
					req = zkau.ajaxRequest(),
					content = "dtid="+dtid+"&cmd.0="+
						(bRmDesktop?"rmDesktop":"dummy"),
					uri = zkau.uri(dtid);
				req.open("POST", zk.ie ? uri+"?"+content: uri, true);
				req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
				if (zk.pfmeter) zkau._pfsend(dtid, req, true);
				if (zk.ie) req.send(null);
				else req.send(content);
			}
		} catch (e) { //silent
		}
	}

	if (zkau._oldUnload) zkau._oldUnload.apply(window, arguments);
	zk.unlistenAll();
};
/** Handles window.onbeforeunload. */
zkau._onBfUnload = function () {
	if (!zk.skipBfUnload) {
		if (zkau.confirmClose)
			return zkau.confirmClose;

		var s = zk.beforeUnload();
		if (s) return s;
	}

	if (zkau._oldBfUnload) {
		var s = zkau._oldBfUnload.apply(window, arguments);
		if (s) return s;
	}

	zkau._unloading = true; //FF3 aborts ajax before calling window.onunload
	//Return nothing
};

/** Handle document.onmousedown. */
zkau._onDocMousedown = function (evt) {
	if (!evt) evt = window.event;

	var el = Event.element(evt);
	if (el == document.body || el == document.body.parentNode //scrollbar
	|| !zkau.canFocus(el)) return;

	zkau._savepos(evt);

	zkau.currentFocus = el;
	zkau.closeFloatsOnFocus(el);
	zkau.autoZIndex(el);
};
/** Handles the left click. */
zkau._onDocLClick = function (evt) {
	if (!evt) evt = window.event;

	if (evt.which == 1 || (evt.button == 0 || evt.button == 1)) {
		var target = $outer(Event.element(evt)), cmp = zkau._parentByZKAttr(target, "lfclk", "pop");
		if (cmp) {
			var ctx = getZKAttr(cmp, "pop");
			if (ctx) {
				ctx = zkau.getByZid(cmp, ctx);
				if (ctx && (!zkau._lastClkUuid || zkau._lastClkUuid == target.id)) {
					var type = $type(ctx);
					if (type) {
						zkau.closeFloats(ctx, cmp);

						ctx.style.position = "absolute";
						zk.setVParent(ctx); //FF: Bug 1486840, IE: Bug 1766244
						zkau._autopos(ctx, Event.pointerX(evt), Event.pointerY(evt));
						zk.eval(ctx, "context", type, cmp);
						if ($visible(ctx)) setZKAttr(ctx, "owner", cmp.id);
							//bookmark owner, so closeFloats know not to close
					}
				}
			}

			if (getZKAttr(cmp, "lfclk") && zkau.insamepos(evt) && !zkau._lastClkUuid)
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
			for (var n = target; n; n = $parent(n)) {
				var type = $type(n);
				if (type) {
					var o = window["zk" + type];
					if (o && o.onrtclk && o.onrtclk(n))
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

					ctx.style.position = "absolute";
					zk.setVParent(ctx); //FF: Bug 1486840, IE: Bug 1766244
					zkau._autopos(ctx, Event.pointerX(evt), Event.pointerY(evt));
					zk.eval(ctx, "context", type, cmp);
					if ($visible(ctx)) setZKAttr(ctx, "owner", cmp.id);
						//bookmark owner, so closeFloats know not to close
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
	if (zk.progressing) return; //skip tip if in processing

	if (!evt) evt = window.event;

	var cmp = Event.element(evt),
		cmps = zkau._parentsByZKAttrs(cmp, "hvig", "tip"),
		hvcmp = cmps[0];
	if (hvcmp) {
		var img = $tag(hvcmp) == "IMG" ? hvcmp: $e(hvcmp.id + "!hvig");
		if (img) {
			zkau._hviz = {id: img.id, src: img.src};
			img.src = getZKAttr(hvcmp, "hvig");
		}
	}

	cmp = cmps[1];
	if (cmp) {
		var tip = getZKAttr(cmp, "tip");
		tip = zkau.getByZid(cmp, tip);
		if (tip) {
			var open = zkau._tipz && zkau._tipz.open;
			if (!open || zkau._tipz.cmpId != cmp.id) {
				if (zkau._tipz) {
					zkau._tipz.shallClose = true;
					zkau._tryCloseTip();
				}
				zkau._tipz = {
					tipId: tip.id, cmpId: cmp.id,
					x: Event.pointerX(evt) + 1, y: Event.pointerY(evt) + 2
					 //Bug 1572286: position tooltip with some offset to allow
				};
				if (open) zkau._openTip(cmp.id, true);
				else setTimeout("zkau._openTip('"+cmp.id+"')", zk_tipto);
			} else {
				zkau._openTip(cmp.id, true);
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

	if (zkau._hviz) {
		var img = $e(zkau._hviz.id);
		if (img) img.src = zkau._hviz.src;
		zkau._hviz = null;
	}

	if (zkau._tipz)
		if (zkau._tipz.open) {
			zkau._tipz.shallClose = true;
			setTimeout(zkau._tryCloseTip, 300);
		} else
			zkau._tipz = null;
};
/** window.onresize */
zkau._onResize = function () {
	if (!zk.booted)
		return; //IE6: it sometimes fires an "extra" onResize in loading

	//Tom Yeh: 20051230:
	//1. In certain case, IE will keep sending onresize (because
	//grid/listbox may adjust size, which causes IE to send onresize again)
	//To avoid this endless loop, we ignore onresize a whilf if _reszfn
	//is called
	//
	//2. IE keeps sending onresize when dragging the browser's border,
	//so we have to filter (most of) them out

	var now = $now();
	if ((zkau._tmLastResz && now < zkau._tmLastResz) || zkau._inResize)
		return; //ignore resize for a while (since zk.onSizeAt might trigger onsize)

	var delay = zk.ie ? 250: 50;
	zkau._tmResz = now + delay - 1; //handle it later
	setTimeout(zkau._onDidResize, delay);
};
zkau._onDidResize = function () {
	if (!zkau._tmResz) return; //already handled

	var now = $now();
	if (zk.loading || anima.count || now < zkau._tmResz) {
		setTimeout(zkau._onDidResize, 10);
		return;
	}

	zkau._tmResz = null; //handled
	zkau._tmLastResz = now + 1000;
		//ignore following for a while if processing (in slow machine)

	if (zkau._cInfoReg)
		setTimeout(zkau._doClientInfo, 20);
	zkau._inResize = true;
	try {
			//we cannot pass zkau.cmd0.clientInfo directly
			//otherwise, FF will pass 1 as the firt argument,
			//i.e., it is equivalent to zkau.cmd0.clientInfo(1)
		zk.beforeSizeAt();
		zk.onSizeAt();
		//the onsize might be fire during delay 
		zkau._tmLastResz = $now() + (zk.ie ? 250: 50);
	} finally {
		zkau._inResize = false;
	}
};
zkau._doClientInfo = function () {
	zkau.cmd0.clientInfo();
};

zkau._openTip = function (cmpId, enforce) {
	if (!zkau._tipz || (zkau._tipz.open && !enforce))
		return;

 	if (!cmpId || cmpId == zkau._tipz.cmpId) {
	//We have to filter out non-matched cmpId because user might move
	//from one component to another
		var tip = $e(zkau._tipz.tipId);
		if (tip) {
			var cmp = $e(cmpId);
			zkau._tipz.open = true;

			tip.style.position = "absolute";
			zk.setVParent(tip); //FF: Bug 1486840, IE: Bug 1766244
			zkau._autopos(tip, zkau._tipz.x, zkau._tipz.y);
			zk.eval(tip, "context", null, cmp);
			//not setZKAttr(... "owner") since it is OK to close
		} else {
			zkau._tipz = null;
		}
	}
};
/** Closes tooltip if _tipz.shallClose is set. */
zkau._tryCloseTip = function () {
	if (zkau._tipz && zkau._tipz.shallClose) {
		if (zkau._tipz.open) {
			for (var close, n = $e(zkau._tipz.tipId), fts = zkau.floats, j = fts.length; --j >= 0;) {
				if (typeof fts[j].getFloatIds != "function") continue;
				if (!$visible(n) || getZKAttr(n, "animating") == "hide") break;
				for (var f = fts[j].getFloatIds(), len = f.length; --len >= 0;) {
					if (zk.isAncestor(n, f[len])) {
						fts[j]._close($e(f[len]));
						f.splice(len, 1);
						close = true;
					}
				}

				if (close) {
					zkau._tipz = null;
					break;
				}
			}
		}
	}
};

/** Returns the first parent who has one of the specified attributes,
 * or null if not found.
 * att1 and attr2 might be null.
 */
zkau._parentByZKAttr = function (n, attr1, attr2) {
	for (; n; n = $parent(n)) {
		if (attr1 && getZKAttr(n, attr1)) return n;
		if (attr2 && getZKAttr(n, attr2)) return n;
		if (getZKAttr(n, "float")) break;
			//tooltip/popup/context might be inside the component
	}
	return null;
};
/** Retuns two parents of the specified two attributes.
 * Both attr1 and attr2 cannot be null.
 */
zkau._parentsByZKAttrs = function (n, attr1, attr2) {
	var cmp1, cmp2, cnt = 2;
	for (; n && cnt; n = $parent(n)) {
		if (getZKAttr(n, attr1)) {
			--cnt;
			cmp1 = n;
		}
		if (getZKAttr(n, attr2)) {
			--cnt;
			cmp2 = n;
		}
		if (getZKAttr(n, "float")) break;
			//tooltip/popup/context might be inside the component
	}
	return [cmp1, cmp2];
};

/** Handles document.onkeydown. */
zkau._onDocKeydown = function (evt) {
	if (!evt) evt = window.event;
	var target = Event.element(evt),
		zkAttrSkip, evtnm, ctkeys, shkeys, alkeys, exkeys,
		keycode = Event.keyCode(evt), zkcode; //zkcode used to search z.ctkeys
	switch (keycode) {
	case 13: //ENTER
		var tn = $tag(target);
		if (tn == "TEXTAREA" || (tn == "BUTTON" && getZKAttr(target, "keyevt") != "true")
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
	case 45: //Ins
	case 46: //Del
		zkcode = keycode == 45 ? 'I': 'J';
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
	var meta = zkau.getMeta($uuid(target));
	if (meta && (typeof meta.getCurrentTarget == "function"))
		target = meta.getCurrentTarget();
	for (var n = target, ref; n; n = $parent(n)) {
		if (n.id && n.getAttribute) {
			if (!ref && n.id.indexOf("!") == -1)
				ref = n.id;
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

				var req = {uuid: n.id, cmd: evtnm, ctl: true,
					data: [keycode, evt.ctrlKey, evt.shiftKey, evt.altKey, ref]};
				if (zk.gecko && $tag(inp) == "SELECT" && $type(inp) && zkau.asap(inp, "onSelect"))
					zkau.lateReq = req; //Bug 1756559:let SELECT to send (see sel.js)
				else
					zkau.send(req, 38);
				Event.stop(evt);

				//Bug 2041347
				if (zk.ie && keycode == 112) {
					zk._oldOnHelp = window.onhelp;
					window.onhelp = function () {return false;}
					setTimeout(function () {window.onhelp = zk._oldOnHelp; zk._oldOnHelp = null;}, 200);
				}
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

	if (keycode == 27 && zkau.ignoreESC()) { //Bug 1927788: prevent FF from closing connection
		Event.stop(evt);
		return false; //eat
	}
	return true; //no special processing
};
/** Returns whether to ignore ESC.
 * @since 3.5.0
 */
zkau.ignoreESC = function () {
	return zkau._areq;
};
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
	var offset = getZKAttr(cmp, "offset");
	var left = cmp.style.left, top = cmp.style.top;
	if (offset && getZKAttr(cmp, "pos") == "parent") {
		var xy = offset.split(",");
		left = $int(left) - $int(xy[0]) + "px";
		top = $int(top) - $int(xy[1]) + "px";
	}
	zkau.sendasap({uuid: cmp.id, cmd: "onMove",
		data: [left, top, keys ? keys: ""], ignorable: true}); //yes, ignorable since it is implicit for modal window
};
zkau.sendOnZIndex = function (cmp) {
	zkau.sendasap({uuid: cmp.id, cmd: "onZIndex",
		data: [cmp.style.zIndex], ignorable: true}); //yes, ignorable since it is implicit for modal window
};
zkau.sendOnSize = function (cmp, keys) {
	zkau.sendasap({uuid: cmp.id, cmd: "onSize",
		data: [cmp.style.width, cmp.style.height, keys]});

	setTimeout(function () {zk.beforeSizeAt(cmp); zk.onSizeAt(cmp);},
		zk.ie6Only ? 800: 0);
	// If the vflex component in the window component, the offsetHeight of the specific component is wrong at the same time on IE6.
	// Thus, we have to invoke the zk.onSizeAt function again.
};
zkau.sendOnClose = function (uuid, closeFloats) {
	var el = $e(uuid);
	if (closeFloats) zkau.closeFloats(el);
	zkau.send({uuid: el.id, cmd: "onClose"}, 5);
};
/** Ask the server to redraw all desktops on the browser.
 * @since 3.0.6
 */
zkau.sendRedraw = function () {
	zk.errorDismiss();
	for (var ds = zkau._dtids, j = ds.length; --j >= 0;)
		zkau.send({dtid: ds[j], cmd: "redraw"});
};

/** Test if any float is opened.
 * @since 3.0.4
 */
zkau.anyFloat = function () {
	for (var fts = zkau.floats, j = fts.length; --j >= 0;)
		if (!fts[j].empty())
			return true;
	return false;
};

/** Closes popups and floats except any of the specified components
 * is an ancestor of popups and floats.
 *
 * Maybe it shall be named as closeFloatsBut, but we cannot due to backward
 * compatible issues.
 *
 * @param arguments a list of component (or its ID) to exclude if
 * a popup contains any of them
 * @return false if nothing changed.
 */
zkau.closeFloats = function () {
	return zkau._closeFloats("closeFloats", zkau._shallCloseBut, arguments);
};
/** Similar to zkau.closeFloats, except it is called when a component
 * is getting the focus.
 * More precisely, it treats all floats as popup windows, i.e.,
 * floats remains if it is an ancestor of aruments.
 */
zkau.closeFloatsOnFocus = function () {
	return zkau._closeFloats("closeFloatsOnFocus", zkau._shallCloseBut, arguments);
};
zkau._shallCloseBut = function (n, ancestors) {
	return !zk.isAncestorX(n, ancestors, true, true);
};
/** Closes popups and floats if they belongs to any of the specified component.
 * By belong we mean a component is a descendant of another.
 * @return false if nothing changed.
 * @since 3.0.2
 */
zkau.closeFloatsOf = function () {
	return zkau._closeFloats("closeFloatsOf", zkau._shallCloseOf, arguments);
};
zkau._shallCloseOf = function (n, ancestors) {
	return zk.isAncestorX1(ancestors, n, true, true);
}
zkau._closeFloats = function (method, shallClose, ancestors) {
	var closed;
	for (var j = zkau._popups.length; --j >=0;) {
	//reverse order is important if popup contains another
	//otherwise, IE seem have bug to handle them correctly
		var n = $e(zkau._popups[j]);
		if ($visible(n) && getZKAttr(n, "animating") != "hide"
		&& shallClose(n, ancestors)) {
		//we avoid hiding twice we have to check animating
			closed = true;
			zk.unsetVParent(n);
			zk.hide(n);
			zkau.sendasap({uuid: n.id, cmd: "onOpen", data: [false]});
				//We have to send onOpen since the server need to know
				//whether the popup becomes invsibile
		}
	}

	//floats: combobox, context menu...
	for (var fts = zkau.floats, j = fts.length; --j >= 0;) {
		var ft = fts[j];
		if (ft[method].apply(ft, ancestors))
			closed = true;
	}

	if (closed)
		zkau.hideCovered();
	return closed;
};

zkau.hideCovered = function() {
	var ary = [];
	for (var j = 0, pl = zkau._popups.length; j < pl; ++j) {
		var el = $e(zkau._popups[j]);
		if ($visible(el)) ary.push(el);
	}

	for (var j = 0, fl = zkau.floats.length; j < fl; ++j)
		zkau.floats[j].addHideCovered(ary);

	for (var j = 0, ol = zkau._overlaps.length; j < ol; ++j) {
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

//Server Push//
/** Sets the info of the server push.
 * @since 3.0.0
 */
zkau.setSPushInfo = function (dtid, info) {
	var i = zkau._spushInfo[dtid];
	if (!i) i = zkau._spushInfo[dtid] = {};

	if (info.min != null) i.min = info.min;
	if (info.max != null) i.max = info.max;
	if (info.factor != null) i.factor = info.factor;
};
/** Returns the info of the server push.
 * @since 3.0.0
 */
zkau.getSPushInfo = function (dtid) {
	return zkau._spushInfo[dtid];
};

////
//ID Space//
/** Returns element of the specified zid. */
zkau.getByZid = function (n, zid) {
	if (zid.startsWith("uuid(") && zid.endsWith(')'))
		return $e(zid.substring(5, zid.length - 1));

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
	for (var p = n; p; p = $parent(p)) {
		if (getZKAttr(p, "zidsp"))
			return p.id;
	}
	return "_zdt_" + zkau.dtid(n);
};

///////////////
//Drag & Drop//
zkau.initdrag = function (n) {
	zkau._drags[n.id] = new zDraggable(n, {
		starteffect: zk.voidf, // bug #1886342: we cannot use the zkau.closeFloats function in this situation.
		endeffect: zkau._enddrag, change: zkau._dragging,
		ghosting: zkau._ghostdrag, z_dragdrop: true,
		constraint: zkau._constraint,
		revert: zkau._revertdrag, ignoredrag: zkau._ignoredrag, zindex: 88800
	});
	zk.eval(n, "initdrag");
};
zkau.cleandrag = function (n) {
	if (zkau._drags[n.id]) {
		zkau._drags[n.id].destroy();
		delete zkau._drags[n.id];
	}
	zk.eval(n, "cleandrag");
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
zkau._dragging = function (dg, pointer, evt) {
	var target = Event.element(evt);
	if (target == dg.zk_lastTarget) return;

	var e = zkau._getDrop(dg.z_elorg || dg.element, pointer, evt);
	var flag = e && e == dg.zk_lastDrop;
	if (!e || e != dg.zk_lastDrop) {
		zkau._cleanLastDrop(dg);
		if (e) {
			dg.zk_lastDrop = e;
			Droppable_effect(e);
			flag = true;
		}
	}
	if (flag && dg.element._img) {
		if (dg.element._img.className != "z-drop-allow")
			dg.element._img.className = "z-drop-allow";
	} else if (dg.element._img) {
		if (dg.element._img.className != "z-drop-disallow")
		dg.element._img.className = "z-drop-disallow";
	}
	dg.zk_lastTarget = target;
};
zkau._revertdrag = function (cmp, pointer, evt) {
	if (zkau._getDrop(cmp, pointer, evt) == null)
		return true;

	//Note: we hve to revert when zkau._onRespReady called, since app might
	//change cmp's position
	var dg = zkau._drags[cmp.id];
	zkau._revertpending = function () {
		if (dg.z_x != null) {
			cmp.style.left = dg.z_x;
			cmp.style.top = dg.z_y;
			delete dg.z_x;
			delete dg.z_y;
		}
		delete zkau._revertpending; //exec once
	};
	return false;
};

zkau._enddrag = function (cmp, evt) {
	zkau._cleanLastDrop(zkau._drags[cmp.id]);
	var pointer = [Event.pointerX(evt), Event.pointerY(evt)];
	var e = zkau._getDrop(cmp, pointer, evt);
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
zkau._getDrop = function (cmp, pointer, evt) {
	var dragType = getZKAttr(cmp, "drag");
	var el = Event.element(evt);
	l_next:
	for (; el; el = $parent(el)) {
		if (el == cmp) return; //dropping to itself not allowed
		var dropTypes = getZKAttr(el, "drop");
		if (dropTypes) {
			if (dropTypes != "true") {
				if (dragType == "true") continue; //anonymous drag type
				for (var k = 0;;) {
					var l = dropTypes.indexOf(',', k);
					var s = l >= 0 ? dropTypes.substring(k, l): dropTypes.substring(k);
					if (s.trim() == dragType) break; //found
					if (l < 0) continue l_next;
					k = l + 1;
				}
			}
			return el; //found;
		}
	}
	return null;
};
zkau._cleanLastDrop = function (dg) {
	if (!dg) return;
	if (dg.zk_lastDrop) {
		Droppable_effect(dg.zk_lastDrop, true);
		dg.zk_lastDrop = null;
	}
	dg.zk_lastTarget = null;
};
zkau._proxyXY = function (evt) {
	return [Event.pointerX(evt) + 10, Event.pointerY(evt) + 10];
};
zkau._constraint = function (dg, p, evt) {
	return zkau._proxyXY(evt);
};
zkau._ghostdrag = function (dg, ghosting, evt) {
//Tom Yeh: 20060227: Use a 'fake' DIV if
//1) FF cannot handle z-index well if listitem is dragged across two listboxes
//2) Safari's ghosting position is wrong
//3) Opera's width is wrong if cloned

//Bug 1783363: Due to the use of "position:relative",
//a side effect of Bug 1766244, we no longer clone even if zk.ie
	var special;
	if (ghosting) {
		var tn = $tag(dg.element);
		zk.zk_special = special = "TR" == tn || "TD" == tn || "TH" == tn;
	} else {
		special = zk.zk_special;
	}
	if (ghosting) {
		zkau.beginGhostToDIV(dg);
		var ofs = zkau._proxyXY(evt);
		if (special) {
			var msg = "";
			var target = Event.element(evt);
			if (target.id.indexOf("!cave") > 0)
				msg = target.textContent || target.innerText;
			else if (target.id.indexOf("!cell") > 0) {
				var real = $real(target.id);
				msg = real.textContent || real.innerText;
			} else
				msg = target.textContent || target.innerText;
			if (!msg) msg = "";
			if (msg.length > 10) msg = msg.substring(0,10) + "...";
			var el = dg.element;
			document.body.insertAdjacentHTML("beforeEnd",
				'<div id="zk_ddghost" class="z-drop-ghost" style="position:absolute;top:'
				+ofs[1]+'px;left:'+ofs[0]+'px;"><div class="z-drop-cnt"><span id="zk_ddghost!img" class="z-drop-disallow"></span>&nbsp;'+msg+'</div></div>');
		}else {
			var el  = dg.element.cloneNode(true);
			el.id = "zk_ddghost";
			el.style.position = "absolute";
			var xy = zkau._proxyXY(evt);
			el.style.top = xy[1] + "px";
			el.style.left = xy[0] + "px";
			document.body.appendChild(el);
		}
		dg.element = $e("zk_ddghost");
		if (special) dg.element._img = $e(dg.element.id + "!img");
		document.body.style.cursor = "pointer";
	} else {
		dg.element._img = null;
		zkau.endGhostToDIV(dg);
		document.body.style.cursor = "";
	}
	return false;
};
/** Prepares to ghost dg.element to a DIV.
 * It is used when you want to ghost with a div.
 * @return the offset of dg.element.
 */
zkau.beginGhostToDIV = function (dg) {
	zk.dragging = true;
	dg.delta = dg.currentDelta();
	dg.z_elorg = dg.element;

	var ofs = zPos.cumulativeOffset(dg.element);
	dg.z_scrl = zPos.realOffset(dg.element);
	dg.z_scrl[0] -= zk.innerX(); dg.z_scrl[1] -= zk.innerY();
		//Store scrolling offset since zDraggable.draw not handle DIV well

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

//Perfomance Meter//
zkau._pfj = 0; //an index
zkau._pfRecvIds = {}; //a map of (dtid, ids) to denote what are received
zkau._pfDoneIds = {}; //a map of (dtid, ids) to denote what are processed
zkau._pfsend = function (dtid, req, completeOnly) {
	if (!completeOnly)
		req.setRequestHeader("ZK-Client-Start",
			dtid + "-" + zkau._pfj++ + "=" + Math.round($now()));

	if (zkau._pfRecvIds[dtid]) {
		req.setRequestHeader("ZK-Client-Receive", zkau._pfRecvIds[dtid]);
		zkau._pfRecvIds[dtid] = "";
	}
	if (zkau._pfDoneIds[dtid]) {
		req.setRequestHeader("ZK-Client-Complete", zkau._pfDoneIds[dtid]);
		zkau._pfDoneIds[dtid] = "";
	}
};
/** Returns a list of request IDs sent from the server separated by space.
 */
zkau._pfGetIds = function (req) {
	return req.getResponseHeader("ZK-Client-Complete");
};
/** Adds performance request IDs that have been processed completely.
 * @since 3.0.8
 */
zkau.pfrecv = function (dtid, pfIds) {
	zkau._pfAddIds(dtid, pfIds, zkau._pfRecvIds);
};
/** Adds performance request IDs that have been processed completely.
 * @since 3.0.7
 */
zkau.pfdone = function (dtid, pfIds) {
	zkau._pfAddIds(dtid, pfIds, zkau._pfDoneIds);
};
zkau._pfAddIds = function (dtid, pfIds, map) {
	if (pfIds && (pfIds = pfIds.trim())) {
		var s = pfIds + "=" + Math.round($now());
		if (map[dtid]) map[dtid] += ',' + s;
		else map[dtid] = s;
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
		zkau.send({dtid: zkau.dtid(wndid), cmd: "getUploadInfo", ignorable: true});
	}, 1000);
};
zkau.updateUploadInfo = function (p, cb) {
	if (cb <= 0) zkau.endUpload();
	else if (zkau._tmupload) {
		var img = $e("zk_upload!img");
		if (!img) {
			var html = '<div id="zk_upload" style="position:absolute;border:1px solid #77a;padding:9px;background-color:#fec;z-index:79000">'
				+'<div style="width:202px;border:1px inset"><div id="zk_upload!img" class="z-upload-icon"></div></div><br/>'+mesg.FILE_SIZE+Math.round(cb/1024)+mesg.KBYTES
				+'<br/><input type="button" value="'+mesg.CANCEL+'" onclick="zkau._cancelUpload()"</div>';
			document.body.insertAdjacentHTML("afterBegin", html);
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
		zk.alert(msg);
	},
	redirect: function (url, target) {
		try {
			zk.go(url, false, target);
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
	echo: function (dtid) {
		zkau.send({dtid: dtid, cmd: "dummy", ignorable: true});
	},
	clientInfo: function (dtid) {
		zkau._cInfoReg = true;
		zkau.send({dtid: dtid, cmd: "onClientInfo", data: [
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
				var html = '<iframe src="'+url+'" id="zk_download" name="zk_download" style="visibility:hidden;width:0;height:0;border:0"></iframe>';
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
	},
	closeErrbox: function () {
		if (zkau.valid)
			for (var i = arguments.length; --i >= 0;)
				zkau.valid.closeErrbox(arguments[i], false, true);
	},
	wrongValue: function () {
		for (var i = 0, len = arguments.length - 1; i < len; i += 2) {
			var uuid = arguments[i], msg = arguments[i + 1],
				cmp = $e(uuid);
			if (cmp) {
				cmp = $real(cmp); //refer to INPUT (e.g., datebox)
				//we have to update default value so validation will be done again
				var old = cmp.value;
				cmp.defaultValue = old + "_err"; //enforce to validate
				if (old != cmp.value) cmp.value = old; //Bug 1490079 (FF only)
				if (zkau.valid) zkau.valid.errbox(cmp.id, msg, true);
				else zk.alert(msg);
			} else if (!uuid) //keep silent if component (of uuid) not exist (being detaced)
				zk.alert(msg);
		}
	},
	showBusy: function (msg, open) {
		//close first (since users might want close and show diff message)
		var n = $e("zk_showBusy");
		if (n) {
			n.parentNode.removeChild(n);
			zk.restoreDisabled();
		}

		if (open == "true") {
			n = $e("zk_loadprog");
			if (n) n.parentNode.removeChild(n);
			n = $e("zk_prog");
			if (n) n.parentNode.removeChild(n);
			n = $e("zk_showBusy");
			if (!n) {
				msg = msg == "" ? mesg.PLEASE_WAIT : msg;
				Boot_progressbox("zk_showBusy", msg,
					0, 0, true, true);
				zk.disableAll();
			}
		}
	},
	scrollIntoView: function (id) {
		var n = $e(id);
		if (n && n.scrollIntoView) n.scrollIntoView();
	}
};
zkau.cmd1 = {
	setAttr: function (uuid, cmp, nm, val) {
		if (nm == "z.init" || nm == "z.chchg") { //initialize
			//Note: cmp might be null because it might be removed
			if (cmp) {
				var type = $type(cmp);
				if (type) {
					zk.loadByType(cmp);
					if (zk.loading) {
						zk.addInitCmp(cmp);
					} else {
						zk.eval(cmp, nm == "z.init" ? "init": "childchg", type);
					}
				}
			}
			return; //done
		}

		if (val == null && arguments.length <= 4) { //single null value
			zkau.cmd1.rmAttr(uuid, cmp, nm);
			return;
		}

		var done = false;
		if ("z.drag" == nm) {
			if (!getZKAttr(cmp, "drag")) zkau.initdrag(cmp);
			zkau.setAttr(cmp, nm, val);
			done = true;
		} else if ("z.drop" == nm) {
			if (!getZKAttr(cmp, "drop")) zkau.initdrop(cmp);
			zkau.setAttr(cmp, nm, val);
			done = true;
		} else if ("zid" == nm) {
			zkau.cleanzid(cmp);
			if (val) zkau.initzid(cmp, val);
		}

		var args = [cmp, "setAttr", null, nm, val];
		for (var j = arguments.length - 4; --j >= 0;)
			args[j + 5] = arguments[j + 4];
		if (zk.eval.apply(cmp, args))
			return; //done

		if (!done)
			zkau.setAttr(cmp, nm, val);
	},
	rmAttr: function (uuid, cmp, nm) {
		var done = false;
		if ("z.drag" == nm) {
			zkau.cleandrag(cmp);
			zkau.rmAttr(cmp, nm);
			done = true;
		} else if ("z.drop" == nm) {
			zkau.cleandrop(cmp);
			zkau.rmAttr(cmp, nm);
			done = true;
		}

		if (zk.eval(cmp, "rmAttr", null, nm)) //NOTE: cmp is NOT converted to real!
			return; //done

		if (!done)
			zkau.rmAttr(cmp, nm);
	},
	outer: function (uuid, cmp, html) {
		zk.unsetChildVParent(cmp, true); //OK to hide since it will be replaced
		var fns = zk.find(cmp, "onOuter"),
			cf = zkau.currentFocus, cfid;
		if (cf && zk.isAncestor(cmp, cf, true)) {
			cfid = cf.id
			zkau.currentFocus = null;
		} else
			cf = null;

		zk.cleanupAt(cmp);
		var from = cmp.previousSibling, from2 = cmp.parentNode,
			to = cmp.nextSibling;
		zk.setOuterHTML(cmp, html);
		if (from) zkau._initSibs(from, to, true);
		else zkau._initChildren(from2, to);

		if (zkau.valid) zkau.valid.fixerrboxes();

		if (cf && !zkau.currentFocus)
			if (cfid) zk.focus($e(cfid));
			// else zk.focusDown($e(uuid)); fails in grid with paging

		if (fns) {
			var ls = zk.find(cmp);
			if (zk.debugJS) {
				var fs = ls["onOuter"];
				if (fs && fs.length) //some register
					zk.error("Registering onOuter in init not allowed");
			}
			ls["onOuter"] = fns;
		}
		zk.fire(cmp, "onOuter");
	},
	addAft: function (uuid, cmp, html, pgid) {
		//Bug 1939059: This is a dirty fix. Refer to AuInsertBefore
		//Format: comp-uuid:pg-uuid (if native root)
		if (!cmp && pgid) {
			cmp = $e(pgid); //try page (though not possible)
			if (!cmp) cmp = document.body;
			zkau.cmd1.addChd(pgid, cmp, html);
			return;
		}

		var v = zk.isVParent(cmp);
		if (v) zk.unsetVParent(cmp);
		var n = $childExterior(cmp);
		var to = n.nextSibling;
		zk.insertHTMLAfter(n, html);
		zkau._initSibs(n, to, true);
		if (v) zk.setVParent(cmp);
	},
	addBfr: function (uuid, cmp, html) {
		var v = zk.isVParent(cmp);
		if (v) zk.unsetVParent(cmp);
		var n = $childExterior(cmp);
		var to = n.previousSibling;
		zk.insertHTMLBefore(n, html);
		zkau._initSibs(n, to, false);
		if (v) zk.setVParent(cmp);
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
			zk.unsetChildVParent(cmp, true); //OK to hide since it will be removed

			zk.cleanupAt(cmp);
			cmp = $childExterior(cmp);
			zk.remove(cmp);
			zkau.hideCovered(); // Bug #1858838
		}
		if (zkau.valid) zkau.valid.fixerrboxes();
	},
	focus: function (uuid, cmp) {
		if (!zk.eval(cmp, "focus")) {
			//Bug 1936366: endModal uses timer, so canFocus might be false
			//when this method is called
			setTimeout(function (){
				if (!zkau.canFocus(cmp, true)) return;

				zkau.autoZIndex(cmp); //some, say, window, not listen to onfocus
				cmp = $real(cmp); //focus goes to inner tag
				zk.asyncFocus(cmp.id, 35);
				}, 30); //wnd.js uses 20
		}
	},
	submit: function (uuid, cmp) {
		setTimeout(function (){if (cmp && cmp.submit) cmp.submit();}, 50);
	},
	invoke: function (uuid, cmp, func) {
		var args = arguments, len = args.length;
		if (len == 8)
			zk.eval(cmp, func, null, args[3], args[4], args[5], args[6], args[7]);
		else if (len == 7)
			zk.eval(cmp, func, null, args[3], args[4], args[5], args[6]);
		else if (len == 6)
			zk.eval(cmp, func, null, args[3], args[4], args[5]);
		else if (len == 5)
			zk.eval(cmp, func, null, args[3], args[4]);
		else if (len == 4)
			zk.eval(cmp, func, null, args[3]);
		else
			zk.eval(cmp, func, null);
	},
	popup: function (uuid, cmp, mode, x, y) {
		var type = $type(cmp);
		if (type) {
			if (mode == "0") { //close
				zkau.closeFloatsOf(cmp);
			} else {
				var ref;
				if (mode == "1") { //ref
					ref = $e(x);
					if (ref) {
						var ofs = zk.revisedOffset($e(x));
						x = ofs[0];
						y = ofs[1] + zk.offsetHeight(ref);
					}
				}
				cmp.style.position = "absolute";
				zk.setVParent(cmp); //FF: Bug 1486840, IE: Bug 1766244
				zkau._autopos(cmp, $int(x), $int(y));
				zk.eval(cmp, "context", type, ref);
			}
		}
	},
	echo2: function (uuid, cmp, evtnm, data) {
		zkau.send(
			{uuid: uuid, cmd: "echo",
				data: data != null ? [evtnm, data]: [evtnm], ignorable: true});
	}
};
zkau.cmd1.cmd = zkau.cmd1.invoke; //backward compatibility (2.4.1 or before)

} //if (!window.zkau)
