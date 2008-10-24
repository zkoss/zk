/* au.js

{{IS_NOTE
	Purpose:
		ZK Client Engine
	Description:
		
	History:
		Mon Sep 29 17:17:37     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zAu = { //static methods
	//General//
	/** Returns the URI to communicate with the server.
	 * @param uri the URI. If null, the base URI is returned.
	 * @param dt the desktop or its ID. If null, the first desktop is used.
	 * @param ignoreSessId whether to handle the session ID in the base URI
	 */
	comURI: function (uri, dt, ignoreSessId) {
		var au = zk.Desktop.$(dt).updateURI;
		if (!uri) return au;

		if (uri.charAt(0) != '/') uri = '/' + uri;

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
	},

	//Send Utilities//
	/** Adds a callback to be called before sending ZK request.
	 * @param func the function call
	 */
	addOnSend: function (func) {
		zAu._onsends.push(func);
	},
	/** Removes a onSend callback. */
	removeOnSend: function (func) {
		zAu._onsends.remove(func);
	},

	/** Returns whether any AU request is in processing.
	 */
	processing: function () {
		return zAu._cmdsQue.length || zAu._areq || zAu._preqInf
			|| zAu._doingCmds;
	},
	/** Returns the timeout of the specified AU request.
	 * It is mainly used to generate the timeout argument of zAu.send.
	 *
	 * @param timeout if non-negative, it is used when zAu.asap is true.
	 */
	asapTimeout: function (wgt, evtnm, timeout) {
		var asap = zAu.asap(wgt, evtnm), fmt;
		if (!asap && evtnm == "onChange") {
			fmt = getZKAttr(wgt, "srvald");
			if (fmt) { //srvald specified
				fmt = fmt == "fmt";
				asap = !fmt; //if not fmt (and not null), it means server-side validation required
			}
		}
		return asap ? timeout >= 0 ? timeout: 38:
			fmt ? 350: -1; //if fmt we have to send it but not OK to a bit delay
	},
	/** Returns whether any non-deferrable listener is registered for
	 * the specified event.
	 */
	asap: function (wgt, evtnm) {
		return zk.Widget.$(wgt).evtnm == "true" ;
	},

	/** Asks the server to update the result (for file uploading).
	 * @param wgt the widget or the widget ID.
	 */
	sendUpdateResult: function (wgt, updatableId) {
		zAu.send({wgt: wgt, cmd: "updateResult", data: [updatableId]}, -1);
	},
	/** Asks the server to remove a component.
	 */
	sendRemove: function (wgt) {
		zAu.send({wgt: wgt, cmd: "remove"}, 5);
	},

	//Error Handling//
	/** Confirms the user how to handle an error.
	 * Default: it shows up a message asking the user whether to retry.
	 */
	confirmRetry: function (msgCode, msg2) {
		var msg = mesg[msgCode];
		return zk.confirm((msg?msg:msgCode)+'\n'+mesg.TRY_AGAIN+(msg2?"\n\n("+msg2+")":""));
	},
	/** Handles the error caused by processing the response.
	 * @param msgCode the error message code.
	 * It is either an index of mesg (e.g., "FAILED_TO_PROCESS"),
	 * or an error message
	 * @param msg2 the additional message (optional)
	 * @param cmd the command (optional)
	 * @param ex the exception (optional)
	 */
	onError: function (msgCode, msg2, cmd, ex) {
		var msg = mesg[msgCode];
		zk.error((msg?msg:msgCode)+'\n'+(msg2?msg2:"")+(cmd?cmd:"")+(ex?"\n"+ex.message:""));
	},
	/** Sets the URI for an error code.
	 * If the length of  arguments is 1, then the argument must be
	 * the error code, and this method returns its URI (or null
	 * if not available).
	 * <p>If the length is larger than 1, they must be paired and
	 * the first element of the pair must be the package name,
	 * and the second is the version.
	 */
	errorURI: function (code, uri) {
		var args = arguments, len = args.length;
		if (len == 1)
			return zAu._eru['e' + code];

		if (len > 2) {
			for (var j = 0; j < len; j += 2)
				zAu.errorURI(args[j], args[j + 1]);
			return;
		}

		zAu._eru['e' + code] = uri;
	},
	_eru: {},

	////ajax resend mechanism////
	_cmdsQue: [], //response commands in XML
	_onsends: [], //JS called before 	_sendNow
	_seqId: 1, //1-999

	/** IE6 sometimes remains readyState==1 (reason unknown), so resend. */
	_areqTmout: function () {
		//Note: we don't resend if readyState >= 3, since the server is already
		//processing it
		var req = zAu._areq, reqInf = zAu._areqInf;
		if (req && req.readyState < 3) {
			zAu._areq = zAu._areqInf = null;
			try {
				if(typeof req.abort == "function") req.abort();
			} catch (e2) {
			}
			if (reqInf.tmout < 60000) reqInf.tmout += 3000;
				//sever might be busy, so prolong next timeout
			zAu._areqResend(reqInf);
		}
	},
	_areqResend: function (reqInf, timeout) {
		if (zAu._seqId == reqInf.sid) {//skip if the response was recived
			zAu._preqInf = reqInf; //store as a pending request info
			setTimeout(zAu._areqResend2, timeout ? timeout: 0);
		}
	},
	_areqResend2: function () {
		var reqInf = zAu._preqInf;
		if (reqInf) {
			zAu._preqInf = null;
			if (zAu._seqId == reqInf.sid)
				zAu._sendNow2(reqInf);
		}
	},
	/** Called when the response is received from _areq.
	 */
	_onRespReady: function () {
		try {
			var req = zAu._areq, reqInf = zAu._areqInf;
			if (req && req.readyState == 4) {
				zAu._areq = zAu._areqInf = null;
				if (reqInf.tfn) clearTimeout(reqInf.tfn); //stop timer
	
				if (zk.pfmeter) zAu.pfrecv(reqInf.dt, zAu._pfGetIds(req));
	
				if (zAu._revertpending) zAu._revertpending();
					//revert any pending when the first response is received
	
				var sid = req.getResponseHeader("ZK-SID");
				if (req.status == 200) { //correct
					if (sid && sid != zAu._seqId) {
						zAu._errcode = "ZK-SID " + (sid ? "mismatch": "required");
						return;
					} //if sid null, always process (usually for error msg)
	
					if (zAu.pushXmlResp(reqInf.dt, req)) { //valid response
						//advance SID to avoid receive the same response twice
						if (sid && ++zAu._seqId > 999) zAu._seqId = 1;
						zAu._areqTry = 0;
						zAu._preqInf = null;
					}
				} else if (!sid || sid == zAu._seqId) { //ignore only if out-of-seq (note: 467 w/o sid)
					zAu._errcode = req.status;
					var eru = zk.eru['e' + req.status];
					if (typeof eru == "string") {
						zk.go(eru);
					} else {
					//handle MSIE's buggy HTTP status codes
					//http://msdn2.microsoft.com/en-us/library/aa385465(VS.85).aspx
						switch (req.status) { //auto-retry for certain case
						default:
							if (!zAu._areqTry) break;
							//fall thru
						case 12002: //server timeout
						case 12030: //http://danweber.blogspot.com/2007/04/ie6-and-error-code-12030.html
						case 12031:
						case 12152: // Connection closed by server.
						case 12159:
						case 13030:
						case 503: //service unavailable
							if (!zAu._areqTry) zAu._areqTry = 3; //two more try
							if (--zAu._areqTry) {
								zAu._areqResend(reqInf, 200);
								return;
							}
						}
	
						if (!zAu._ignorable && !zAu._unloading) {
							var msg = req.statusText;
							if (confirmRetry("FAILED_TO_RESPONSE", req.status+(msg?": "+msg:""))) {
								zAu._areqTry = 2; //one more try
								zAu._areqResend(reqInf);
								return;
							}
						}
	
						zAu._cleanupOnFatal(zAu._ignorable);
					}
				}
			}
		} catch (e) {
			if (!window.zAu)
				return; //the doc has been unloaded
	
			zAu._areq = zAu._areqInf = null;
			try {
				if(req && typeof req.abort == "function") req.abort();
			} catch (e2) {
			}
	
			//NOTE: if connection is off and req.status is accessed,
			//Mozilla throws exception while IE returns a value
			if (!zAu._ignorable && !zAu._unloading) {
				var msg = e.message;
				zAu._errcode = "[Receive] " + msg;
				//if (e.fileName) zAu._errcode += ", "+e.fileName;
				//if (e.lineNumber) zAu._errcode += ", "+e.lineNumber;
				if (confirmRetry("FAILED_TO_RESPONSE", (msg&&msg.indexOf("NOT_AVAILABLE")<0?msg:""))) {
					zAu._areqResend(reqInf);
					return;
				}
			}
			zAu._cleanupOnFatal(zAu._ignorable);
		}
	
		//handle pending ajax send
		if (zAu._sendPending && !zAu._areq && !zAu._preqInf) {
			zAu._sendPending = false;
			var dts = zk.Desktop.all
			for (var dtid in dts)
				zAu._send2(dts[dtid], 0);
		}
	
		zAu.doCmds();
		zAu._checkProgress();
	},
	/** Parses a XML response and pushes the parsed commands to the queue.
	 * @return false if no command found at all
	 */
	pushXmlResp: function (dt, req) {
		var xml = req.responseXML;
		if (!xml) {
			if (zk.pfmeter) zAu.pfdone(dt, zAu._pfGetIds(req));
			return false; //invalid
		}
	
		var cmds = [],
			rs = xml.getElementsByTagName("r"),
			rid = xml.getElementsByTagName("rid");
		if (zk.pfmeter) {
			cmds.dt = dt;
			cmds.pfIds = zAu._pfGetIds(req);
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
	
		zAu._cmdsQue.push(cmds);
		return true;
	},

	/** Sends a request to the client
	 * @param timout milliseconds.
	 * If negative, it won't be sent until next non-negative event
	 */
	send: function (aureq, timeout) {
		if (timeout < 0) aureq.implicit = true;
	
		if (aureq.uuid) {
			zAu._send(zAu.dtid(aureq.uuid), aureq, timeout);
		} else if (aureq.dtid) {
			zAu._send(aureq.dtid, aureq, timeout);
		} else {
			var ds = zAu._dtids;
			for (var j = 0, dl = ds.length; j < dl; ++j)
				zAu._send(ds[j], aureq, timeout);
		}
	},
	/** A shortcut of zAu.send(aureq, zAu.asapTimeout(aureq.uuid, aureq.cmd, timeout)).
	 */
	sendasap: function (aureq, timeout) {
		zAu.send(aureq, zAu.asapTimeout(aureq.uuid, aureq.cmd, timeout));
	},
	_send: function (dtid, aureq, timeout) {
		if (aureq.ctl) {
			//Don't send the same request if it is in processing
			if (zAu._areqInf && zAu._areqInf.ctli == aureq.uuid
			&& zAu._areqInf.ctlc == aureq.cmd)
				return;
	
			var t = $now();
			if (zAu._ctli == aureq.uuid && zAu._ctlc == aureq.cmd //Bug 1797140
			&& t - zAu._ctlt < 390)
				return; //to prevent key stroke are pressed twice (quickly)
	
			//Note: it is still possible to queue two ctl with same uuid and cmd,
			//if the first one was not sent yet and the second one is generated
			//after 390ms.
			//However, it is rare so no handle it
	
			zAu._ctlt = t;
			zAu._ctli = aureq.uuid;
			zAu._ctlc = aureq.cmd;
		}
	
		zAu._events(dtid).push(aureq);
	
		//Note: we don't send immediately (Bug 1593674)
		//Note: Unlike sendAhead and _send2, if timeout is undefined,
		//it is considered as 0.
		zAu._send2(dtid, timeout ? timeout: 0);
	},
	/** @param timeout if undefined or negative, it won't be sent. */
	_send2: function (dtid, timeout) {
		if (dtid && timeout >= 0) setTimeout("zAu._sendNow('"+dtid+"')", timeout);
	},
	/** Sends a request before any pending events.
	 * @param timout milliseconds.
	 * If undefined or negative, it won't be sent until next non-negative event
	 * Note: Unlike zAu.send, it considered undefined as not sending now
	 * (reason: backward compatible)
	 */
	sendAhead: function (aureq, timeout) {
		var dtid;
		if (aureq.uuid) {
			zAu._events(dtid = zAu.dtid(aureq.uuid)).unshift(aureq);
		} else if (aureq.dtid) {
			zAu._events(dtid = aureq.dtid).unshift(aureq);
		} else {
			var ds = zAu._dtids;
			for (var j = ds.length; --j >= 0; ++j) {
				zAu._events(ds[j]).unshift(aureq);
				zAu._send2(ds[j], timeout); //Spec: don't convert unefined to 0 for timeout
			}
			return;
		}
		zAu._send2(dtid, timeout);
	},
	_sendNow: function (dtid) {
		var es = zAu._events(dtid);
		if (es.length == 0)
			return; //nothing to do
	
		if (zk.loading) {
			zk.addInit(function () {zAu._sendNow(dtid);});
			return; //wait
		}
	
		if (zAu._areq || zAu._preqInf) { //send ajax request one by one
			zAu._sendPending = true;
			return;
		}
	
		//callback (fckez uses it to ensure its value is sent back correctly
		for (var j = 0, ol = zAu._onsends.length; j < ol; ++j) {
			try {
				zAu._onsends[j](implicit); //it might add more events
			} catch (e) {
				zk.error(e.message);
			}
		}
	
		//bug 1721809: we cannot filter out ctl even if zAu.processing
	
		//decide implicit and ignorable
		var implicit = true, ignorable = true, ctli, ctlc;
		for (var j = es.length; --j >= 0;) {
			var aureq = es[j];
			if (implicit && !aureq.ignorable) { //ignorable implies implicit
				ignorable = false;
				if (!aureq.implicit)
					implicit = false;
			}
			if (aureq.ctl && !ctli) {
				ctli = aureq.uuid;
				ctlc = aureq.cmd;
			}
		}
		zAu._ignorable = ignorable;
	
		//Consider XML (Pros: ?, Cons: larger packet)
		var content = "";
		for (var j = 0, el = es.length; el; ++j, --el) {
			var aureq = es.shift();
			content += "&cmd."+j+"="+aureq.cmd+"&uuid."+j+"="+(aureq.uuid?aureq.uuid:'');
			if (aureq.data)
				for (var k = 0, dl = aureq.data.length; k < dl; ++k) {
					var data = aureq.data[k];
					content += "&data."+j+"="
						+ (data != null ? encodeURIComponent(data): '_z~nil');
				}
		}
	
		if (content)
			zAu._sendNow2({
				sid: zAu._seqId, uri: zAu.comURI(null, dtid),
				dtid: dtid, content: "dtid=" + dtid + content,
				ctli: ctli, ctlc: ctlc, implicit: implicit, ignorable: ignorable,
				tmout: 0
			});
	},
	_sendNow2: function(reqInf) {
		var req = zAu.ajaxRequest(),
			uri = zAu._useQS(reqInf) ? reqInf.uri + '?' + reqInf.content: null;
		zAu.sentTime = $now(); //used by server-push (zkex)
		try {
			req.onreadystatechange = zAu._onRespReady;
			req.open("POST", uri ? uri: reqInf.uri, true);
			req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			req.setRequestHeader("ZK-SID", reqInf.sid);
			if (zAu._errcode) {
				req.setRequestHeader("ZK-Error-Report", zAu._errcode);
				delete zAu._errcode;
			}
	
			if (zk.pfmeter) zAu._pfsend(reqInf.dtid, req);
	
			zAu._areq = req;
			zAu._areqInf = reqInf;
			if (zk_resndto > 0)
				zAu._areqInf.tfn = setTimeout(zAu._areqTmout, zk_resndto + reqInf.tmout);
	
			if (uri) req.send(null);
			else req.send(reqInf.content);
	
			if (!reqInf.implicit) zk.progress(zk_procto); //wait a moment to avoid annoying
		} catch (e) {
			//handle error
			try {
				if(typeof req.abort == "function") req.abort();
			} catch (e2) {
			}
	
			if (!reqInf.ignorable && !zAu._unloading) {
				var msg = e.message;
				zAu._errcode = "[Send] " + msg;
				if (confirmRetry("FAILED_TO_SEND", msg)) {
					zAu._areqResend(reqInf);
					return;
				}
			}
			zAu._cleanupOnFatal(reqInf.ignorable);
		}
	},
	//IE: use query string if possible to avoid IE incomplete-request problem
	_useQS: zk.ie ? function (reqInf) {
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
	}: zk.$void,

	/** Registers a script that will be evaluated when the next response is back.
	 * Note: it executes only once, so you have to register again if necessary.
	 * @param script a piece of JavaScript, or a function
	 */
	addOnResponse: function (script) {
		zAu._js4resps.push(script);
	},
	/** Evaluates scripts registered by addOnResponse. */
	_evalOnResponse: function () {
		while (zAu._js4resps.length)
			setTimeout(zAu._js4resps.shift(), 0);
	},
	
	/** Process the response response commands.
	 */
	doCmds: function () {
		//avoid reentry since it calls loadAndInit, and loadAndInit call this
		if (zAu._doingCmds) {
			setTimeout(zAu.doCmds, 10);
		} else {
			zAu._doingCmds = true;
			try {
				zAu._doCmds0();
			} finally {
				zAu._doingCmds = false;
	
				if (zAu._checkProgress())
					zAu.doneTime = $now();
			}
		}
	},
	_doCmds0: function () {
		var ex, j = 0, que = zAu._cmdsQue, rid = zAu._resId;
		for (; j < que.length; ++j) {
			if (zk.loading) {
				zk.addInit(zAu.doCmds); //wait until the loading is done
				return;
			}
	
			var cmds = que[j];
			if (rid == cmds.rid || !rid || !cmds.rid //match
			|| zAu._dtids.length > 1) { //ignore multi-desktops (risky but...)
				que.splice(j, 1);
	
				var oldrid = rid;
				if (cmds.rid) {
					if ((rid = cmds.rid + 1) >= 1000)
						rid = 1; //1~999
					zAu._resId = rid;
				}
	
				try {
					if (zAu._doCmds1(cmds)) { //done
						j = -1; //start over
						if (zk.pfmeter) zAu.pfdone(cmds.dtid, cmds.pfIds);
					} else { //not done yet (=zk.loading)
						zAu._resId = oldrid; //restore
						que.splice(j, 0, cmds); //put it back
						zk.addInit(zAu.doCmds);
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
				if (que.length && rid == zAu._resId) {
					var r = que[0].rid;
					for (j = 1; j < que.length; ++j) { //find min
						var r2 = que[j].rid,
							v = r2 - r;
						if (v > 500 || (v < 0 && v > -500)) r = r2;
					}
					zAu._resId = r;
					zAu.doCmds();
				}
			}, 3600);
		}
	
		if (ex) throw ex;
	},
	_doCmds1: function (cmds) {
		var processed;
		try {
			while (cmds && cmds.length) {
				if (zk.loading)
					return false;
	
				processed = true;
				var cmd = cmds.shift();
				try {
					zAu.process(cmd.cmd, cmd.data);
				} catch (e) {
					onProcessError("FAILED_TO_PROCESS", null, cmd.cmd, e);
					throw e;
				}
			}
		} finally {
			if (processed && (!cmds || !cmds.length))
				zAu._evalOnResponse();
		}
		return true;
	},
	/** Process a command.
	 */
	process: function (cmd, data) {
		//I. process commands that data[0] is not UUID
		var fn = zAu.cmd0[cmd];
		if (fn) {
			fn.apply(zAu, data);
			return;
		}
	
		//I. process commands that require uuid
		if (!data || !data.length) {
			onProcessError("ILLEGAL_RESPONSE", "uuid is required for ", cmd);
			return;
		}
	
		fn = zAu.cmd1[cmd];
		if (fn) {
			data.splice(1, 0, $e(data[0])); //insert wgt
			fn.apply(zAu, data);
			return;
		}
	
		onProcessError("ILLEGAL_RESPONSE", "Unknown command: ", cmd);
	},
	
	/** Cleans up if we detect obsolete or other severe errors. */
	_cleanupOnFatal: function (ignorable) {
		for (var uuid in zAu._metas) {
			var meta = zAu._metas[uuid];
			if (meta && meta.cleanupOnFatal)
				meta.cleanupOnFatal(ignorable);
		}
	},
	
	/** Invoke zk.initAt for siblings. Note: from and to are excluded. */
	_initSibs: function (from, to, next) {
		for (;;) {
			from = next ? from.nextSibling: from.previousSibling;
			if (!from || from == to) break;
			zk.initAt(from);
		}
	},
	/** Invoke zk.initAt for all children. Note: to is excluded. */
	_initChildren: function (n, to) {
		for (n = n.firstChild; n && n != to; n = n.nextSibling)
			zk.initAt(n);
	},
	/** Invoke inserHTMLBeforeEnd and then zk.initAt.
	 */
	_insertAndInitBeforeEnd: function (n, html) {
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
		if (lc) zAu._initSibs(lc, null, true);
		else zAu._initChildren(n);
	},
	
	/** Sets an attribute (the default one). */
	setAttr: function (wgt, name, value) {
		wgt = zAu._attr(wgt, name);
	
		if ("visibility" == name) {
			zk.setVisible(wgt, value == "true");
		} else if ("value" == name) {
			if (value != wgt.value) {
				wgt.value = value;
				if (wgt == zAu.currentFocus && wgt.select) wgt.select();
					//fix a IE bug that cursor disappear if input being
					//changed is onfocus
			}
			if (wgt.defaultValue != wgt.value)
				wgt.defaultValue = wgt.value;
		} else if ("checked" == name) {
			value = "true" == value || "checked" == value;
			if (value != wgt.checked)
				wgt.checked = value;
			if (wgt.defaultChecked != wgt.checked)
				wgt.defaultChecked = wgt.checked;
			//we have to update defaultChecked because click a radio
			//might cause another to unchecked, but browser doesn't
			//maintain defaultChecked
		} else if ("selectAll" == name && $tag(wgt) == "SELECT") {
			value = "true" == value;
			for (var j = 0, ol = wgt.options.length; j < ol; ++j)
				wgt.options[j].selected = value;
		} else if ("style" == name) {
			zk.setStyle(wgt, value);
		} else if (name.startsWith("z.")) { //ZK attributes
			setZKAttr(wgt, name.substring(2), value);
		} else {
			var j = name.indexOf('.'); 
			if (j >= 0) {
				if ("style" != name.substring(0, j)) {
					zk.error(mesg.UNSUPPORTED+name);
					return;
				}
				name = name.substring(j + 1).camelize();
				if (typeof(wgt.style[name]) == "boolean") //just in case
					value = "true" == value || name == value;
				wgt.style[name] = value;
	
				if ("width" == name && (!value || value.indexOf('%') < 0) //don't handle width with %
				&& !getZKAttr(wgt, "float")) {
					var ext = $e(wgt.id + "!chdextr");
					if (ext && $tag(ext) == "TD" && ext.colSpan == 1)
						ext.style.width = value;
				}
				return;
			}
	
			if (name == "disabled" || name == "href")
				zAu.setStamp(wgt, name);
				//mark when this attribute is set (change or not), so
				//modal dialog and other know how to process it
				//--
				//Better to call setStamp always but, to save memory,...
	
			//Firefox cannot handle many properties well with getAttribute/setAttribute,
			//such as selectedIndex, scrollTop...
			var old = "class" == name ? wgt.className:
				"selectedIndex" == name ? wgt.selectedIndex:
				"disabled" == name ? wgt.disabled:
				"readOnly" == name ? wgt.readOnly:
				"scrollTop" == name ? wgt.scrollTop:
				"scrollLeft" == name ? wgt.scrollLeft:
					wgt.getAttribute(name);
	
			//Note: "true" != true (but "123" = 123)
			//so we have take care of boolean
			if (typeof(old) == "boolean")
				value = "true" == value || name == value; //e.g, reaonly="readOnly"
	
			if (old != value) {
				if ("selectedIndex" == name) wgt.selectedIndex = value;
				else if ("class" == name) wgt.className = value;
				else if ("disabled" == name) wgt.disabled = value;
				else if ("readOnly" == name) wgt.readOnly = value;
				else if ("scrollTop" == name) wgt.scrollTop = value;
				else if ("scrollLeft" == name) wgt.scrollLeft = value;
				else wgt.setAttribute(name, value);
			}
		}
	},
	_attr: function (wgt, name) {
		var real = $real(wgt);
		if (real != wgt && real) {
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
		return wgt;
	}
};
