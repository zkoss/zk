/* au.js

	Purpose:
		ZK Client Engine
	Description:
	
	History:
		Mon Sep 29 17:17:37     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _errURIs = {}, errCode,
		_perrURIs = {}, //server-push error URI
		cmdsQue = [], //response commands in XML
		ajaxReq, ajaxReqInf, pendingReqInf, ajaxReqTries,
		sendPending, ctlUuid, ctlTime, ctlCmd, responseId,
		seqId = (zUtl.now() % 9999) + 1, //1-9999 (random init: bug 2691017)
		doCmdFns = [],
		idTimeout, //timer ID for automatica timeout
		pfIndex = 0; //performance meter index

	// Checks whether to turn off the progress prompt
	function checkProcessng() {
		if (!zAu.processing()) {
			zk.endProcessing();
			zAu.doneTime = zUtl.now();
		}
	}
	function pushCmds(dt, req) {
		var rt = req.responseText;
		if (!rt) {
			if (zk.pfmeter) zAu._pfdone(dt, pfGetIds(req));
			return false; //invalid
		}

		var cmds = [];
		if (zk.pfmeter) {
			cmds.dt = dt;
			cmds.pfIds = pfGetIds(req);
		}

		rt = jq.evalJSON(rt);
		var	rs = rt.rs,
			rid = rt.rid;
		if (rid) {
			rid = parseInt(rid); //response ID
			if (!isNaN(rid)) cmds.rid = rid;
		}

		for (var j = 0, rl = rs ? rs.length: 0; j < rl; ++j) {
			var r = rs[j],
				cmd = r[0],
				data = r[1];

			if (!cmd) {
				zk.error(msgzk.ILLEGAL_RESPONSE+"Command required");
				continue;
			}

			cmds.push({cmd: cmd, data: data || []});
		}

		cmdsQue.push(cmds);
		return true;
	}
	function doProcess(cmd, data) { //decoded
		//1. process commands that data[0] is not UUID
		var fn = zAu.cmd0[cmd];
		if (fn)
			return fn.apply(zAu, data);

		//2. process commands that require uuid
		if (!(fn = zAu.cmd1[cmd]) || !data.length)
			return zAu.showError("ILLEGAL_RESPONSE",
				fn ? (data.length ? data[0]+" not found":"uuid required")+" for ": "Unknown ", cmd);

		data[0] = zk.Widget.$(data[0]); //might be null (such as rm)
		fn.apply(zAu, data);
	}

	// IE6 sometimes remains readyState==1 (reason unknown), so resend.
	function ajaxReqTimeout() {
		//Note: we don't resend if readyState >= 3, since the server is already
		//processing it
		var req = ajaxReq, reqInf = ajaxReqInf;
		if (req && req.readyState < 3) {
			ajaxReq = ajaxReqInf = null;
			try {
				if(typeof req.abort == "function") req.abort();
			} catch (e2) {
			}
			if (reqInf.tmout < 60000) reqInf.tmout += 3000;
				//sever might be busy, so prolong next timeout
			ajaxReqResend(reqInf);
		}
	}
	function ajaxReqResend(reqInf, timeout) {
		if (seqId == reqInf.sid) {//skip if the response was recived
			pendingReqInf = reqInf; //store as a pending request info
			setTimeout(ajaxReqResend2, timeout ? timeout: 0);
		}
	}
	function ajaxReqResend2() {
		var reqInf = pendingReqInf;
		if (reqInf) {
			pendingReqInf = null;
			if (seqId == reqInf.sid)
				ajaxSendNow(reqInf);
		}
	}
	// Called when the response is received from ajaxReq.
	function onResponseReady() {
		var req = ajaxReq, reqInf = ajaxReqInf;
		try {
			if (req && req.readyState == 4) {
				ajaxReq = ajaxReqInf = null;
				if (reqInf.tfn) clearTimeout(reqInf.tfn); //stop timer

				if (zk.pfmeter) zAu._pfrecv(reqInf.dt, pfGetIds(req));

				var sid = req.getResponseHeader("ZK-SID");
				if (req.status == 200) { //correct
					if (sid && sid != seqId) {
						errCode = "ZK-SID " + (sid ? "mismatch": "required");
						return;
					} //if sid null, always process (usually for error msg)

					if (pushCmds(reqInf.dt, req)) { //valid response
						//advance SID to avoid receive the same response twice
						if (sid && ++seqId > 9999) seqId = 1;
						ajaxReqTries = 0;
						pendingReqInf = null;
					}
				} else if (!sid || sid == seqId) { //ignore only if out-of-seq (note: 467 w/o sid)
					errCode = req.status;
					var eru = _errURIs['' + req.status];
					if (typeof eru == "string") {
						zUtl.go(eru, {reload: true});
					} else {
					//handle MSIE's buggy HTTP status codes
					//http://msdn2.microsoft.com/en-us/library/aa385465(VS.85).aspx
						switch (req.status) { //auto-retry for certain case
						default:
							if (!ajaxReqTries) break;
							//fall thru
						case 12002: //server timeout
						case 12030: //http://danweber.blogspot.com/2007/04/ie6-and-error-code-12030.html
						case 12031:
						case 12152: // Connection closed by server.
						case 12159:
						case 13030:
						case 503: //service unavailable
							if (!ajaxReqTries) ajaxReqTries = 3; //two more try
							if (--ajaxReqTries) {
								ajaxReqResend(reqInf, 200);
								return;
							}
						}

						if (!reqInf.ignorable && !zk.unloading) {
							var msg = req.statusText;
							if (zAu.confirmRetry("FAILED_TO_RESPONSE", req.status+(msg?": "+msg:""))) {
								ajaxReqTries = 2; //one more try
								ajaxReqResend(reqInf);
								return;
							}
						}
					}
				}
			}
		} catch (e) {
			if (!window.zAu)
				return; //the doc has been unloaded

			ajaxReq = ajaxReqInf = null;
			try {
				if(req && typeof req.abort == "function") req.abort();
			} catch (e2) {
			}

			//NOTE: if connection is off and req.status is accessed,
			//Mozilla throws exception while IE returns a value
			if (reqInf && !reqInf.ignorable && !zk.unloading) {
				var msg = e.message;
				errCode = "[Receive] " + msg;
				//if (e.fileName) errCode += ", "+e.fileName;
				//if (e.lineNumber) errCode += ", "+e.lineNumber;
				if (zAu.confirmRetry("FAILED_TO_RESPONSE", (msg&&msg.indexOf("NOT_AVAILABLE")<0?msg:""))) {
					ajaxReqResend(reqInf);
					return;
				}
			}
		}

		//handle pending ajax send
		if (sendPending && !ajaxReq && !pendingReqInf) {
			sendPending = false;
			var dts = zk.Desktop.all;
			for (var dtid in dts)
				ajaxSend2(dts[dtid], 0);
		}

		zAu._doCmds();
	}

	function ajaxSend(dt, aureq, timeout) {
		var clkfd = zk.clickFilterDelay;
		if (clkfd > 0 && (aureq.opts||{}).ctl) {
			//Don't send the same request if it is in processing
			if (ajaxReqInf && ajaxReqInf.ctli == aureq.uuid
			&& ajaxReqInf.ctlc == aureq.cmd)
				return;

			var t = zUtl.now();
			if (ctlUuid == aureq.uuid && ctlCmd == aureq.cmd //Bug 1797140
			&& t - ctlTime < clkfd)
				return; //to prevent key stroke are pressed twice (quickly)

			//Note: it is still possible to queue two ctl with same uuid and cmd,
			//if the first one was not sent yet and the second one is generated
			//after 390ms. However, it is rare so no handle it

			ctlTime = t;
			ctlUuid = aureq.uuid;
			ctlCmd = aureq.cmd;
		}

		dt._aureqs.push(aureq);

		ajaxSend2(dt, timeout);
			//Note: we don't send immediately (Bug 1593674)
	}
	function ajaxSend2(dt, timeout) {
		if (!timeout) timeout = 0;
		if (dt && timeout >= 0)
			setTimeout(function(){zAu.sendNow(dt);}, timeout);
	}
	function ajaxSendNow(reqInf) {
		var setting = zAu.ajaxSettings,
			req = setting.xhr(),
			uri = shallUseQS(reqInf) ? reqInf.uri + '?' + reqInf.content: null;
		zAu.sentTime = zUtl.now(); //used by server-push (cpsp)
		try {
			req.onreadystatechange = onResponseReady;
			req.open("POST", uri ? uri: reqInf.uri, true);
			req.setRequestHeader("Content-Type", setting.contentType);
			req.setRequestHeader("ZK-SID", reqInf.sid);
			if (errCode) {
				req.setRequestHeader("ZK-Error-Report", errCode);
				errCode = null;
			}

			if (zk.pfmeter) zAu._pfsend(reqInf.dt, req);

			ajaxReq = req;
			ajaxReqInf = reqInf;
			if (zk.resendDelay > 0)
				ajaxReqInf.tfn = setTimeout(ajaxReqTimeout, zk.resendDelay + reqInf.tmout);

			if (uri) req.send(null);
			else req.send(reqInf.content);

			if (!reqInf.implicit) zk.startProcessing(zk.procDelay); //wait a moment to avoid annoying
		} catch (e) {
			//handle error
			try {
				if(typeof req.abort == "function") req.abort();
			} catch (e2) {
			}

			if (!reqInf.ignorable && !zk.unloading) {
				var msg = e.message;
				errCode = "[Send] " + msg;
				if (zAu.confirmRetry("FAILED_TO_SEND", msg)) {
					ajaxReqResend(reqInf);
					return;
				}
			}
		}
	}
	function toJSON(target, data) {
		if (data.pageX != null && data.x == null)  {
			var ofs = target ? zk(target).cmOffset(): [0,0];
			data.x = data.pageX - ofs[0];
			data.y = data.pageY - ofs[1];
		}
		return jq.toJSON(data);
	}

	//IE: use query string if possible to avoid incomplete-request problem
	var shallUseQS = zk.ie ? function (reqInf) {
		var s = reqInf.content, j = s.length, prev, cc;
		if (j + reqInf.uri.length < 2000) {
			while (j--) {
				cc = s.charAt(j);
				if (cc == '%' && prev >= '8') //%8x, %9x...
					return false;
				prev = cc;
			}
			return true;
		}
		return false;
	}: zk.$void;

	function doCmdsNow(cmds) {
		try {
			while (cmds && cmds.length) {
				if (zk.mounting) return false;

				var cmd = cmds.shift();
				try {
					doProcess(cmd.cmd, cmd.data);
				} catch (e) {
					zAu.showError("FAILED_TO_PROCESS", null, cmd.cmd, e);
					throw e;
				}
			}
		} finally {
		//Bug #2871135, always fire since the client might send back empty
			if (!cmds || !cmds.length)
				zWatch.fire('onResponse', null, {timeout:0}); //use setTimeout
		}
		return true;
	}
	function _asBodyChild(child) {
		jq(document.body).append(child);
	}

	//Perfomance Meter//
	// Returns request IDs sent from the server separated by space.
	function pfGetIds(req) {
		return req.getResponseHeader("ZK-Client-Complete");
	}
	function pfAddIds(dt, prop, pfIds) {
		if (pfIds && (pfIds = pfIds.trim())) {
			var s = pfIds + "=" + Math.round(zUtl.now());
			if (dt[prop]) dt[prop] += ',' + s;
			else dt[prop] = s;
		}
	}

	//misc//
	function fireClientInfo() {
		zAu.cmd0.clientInfo();
	}
	function sendTimeout() {
		zAu.send(new zk.Event(null, "dummy", {timeout: true}, {ignorable: true}));
	}

/** @class zAu
 * @import zk.Widget
 * @import zk.Event
 * @import zk.AuCmd0
 * @import zk.AuCmd1
 * The AU Engine used to send the AU requests to the server and to process
 * the AU responses.
 * <p>Notice {@link zAu}, {@link AuCmd0} and {@link AuCmd1}
 * are not available in <a href="http://docs.zkoss.org/wiki/ZK_Light">ZK Light</a>.
 */
zAu = {
	_resetTimeout: function () { //called by mount.js
		if (idTimeout) {
			clearTimeout(idTimeout);
			idTimeout = null;
		}
		if (zk.timeout > 0)
			idTimeout = setTimeout(sendTimeout, zk.timeout * 1000);
  	},
	_onClientInfo: function () { //Called by mount.js when onReSize
		if (zAu._cInfoReg) setTimeout(fireClientInfo, 20);
			//we cannot pass zAu.cmd0.clientInfo directly
			//otherwise, FF will pass 1 as the firt argument,
			//i.e., it is equivalent to zAu.cmd0.clientInfo(1)
	},

	//Error Handling//
	/** Called to confirm the user whether to retry, when an error occurs.
	 * @param String msgCode the message code
	 * @param String msg2 the additional message. Ignored if not specified or null.
	 * @return boolean whether to retry
	 */
	confirmRetry: function (msgCode, msg2) {
		var msg = msgzk[msgCode];
		return jq.confirm((msg?msg:msgCode)+'\n'+msgzk.TRY_AGAIN+(msg2?"\n\n("+msg2+")":""));
	},
	/** Called to shown an error if a severe error occurs.
	 * By default, it is an orange box.
	 * @param String msgCode the message code
	 * @param String msg2 the additional message. Ignored if not specified or null.
	 * @param String cmd the command causing the problem. Ignored if not specified or null.
	 * @param Throwable ex the exception
	 */
	showError: function (msgCode, msg2, cmd, ex) {
		var msg = msgzk[msgCode];
		zk.error((msg?msg:msgCode)+'\n'+(msg2?msg2:"")+(cmd?cmd:"")+(ex?"\n"+ex.message:""));
	},
	/** Returns the URI for the specified error.
	 * @param int code the error code
	 * @return String the URI.
	 */
	getErrorURI: function (code) {
		return _errURIs['' + code];
	},
	/** Sets the URI for the specified error.
	 * @param int code the error code
	 * @param String uri the URI
	 */
	/** Sets the URI for the errors specified in a map.
	 * @param Map errors A map of errors where the key is the error code (int),
	 * while the value is the URI (String).
	 */
	setErrorURI: function (code, uri) {
		if (arguments.length == 1) {
			for (var c in code)
				zAu.setErrorURI(c, code[c]);
		} else
			_errURIs['' + code] = uri;
	},
	/** Sets the URI for the server-push related error.
	 * @param int code the error code
	 * @return String the URI.
	 */
	getPushErrorURI: function (code) {
		return _perrURIs['' + code];
	},
	/** Sets the URI for the server-push related error.
	 * @param int code the error code
	 * @param String uri the URI
	 */
	/** Sets the URI for the server-push related errors specified in a map.
	 * @param Map errors A map of errors where the key is the error code (int),
	 * while the value is the URI (String).
	 */
	setPushErrorURI: function (code, uri) {
		if (arguments.length == 1) {
			for (var c in code)
				zAu.setPushErrorURI(c, code[c]);
			return;
		}
		_perrURIs['' + code] = uri;
	},

	////Ajax Send////
	/** Returns whether ZK Client Engine is busy for processing something,
	 * such as mounting the widgets, processing the AU responses and on.
	 * @return boolean whether ZK Client Engine is busy
	 */
	processing: function () {
		return zk.mounting || cmdsQue.length || ajaxReq || pendingReqInf;
	},

	/** Sends an AU request and appends it to the end if there is other pending
	 * AU requests.
	 *
	 * @param Event the request. If {@link Event#target} is null,
	 * the request will be sent to each desktop at the client.
	 * @param int timeout the time to wait before sending the request.
	 * 0 is assumed if not specified or negative.
	 * If negative, the request is assumed to be implicit, i.e., no message will
	 * be shown if an error occurs.
	 */
	send: function (aureq, timeout) {
		if (timeout < 0)
			aureq.opts = zk.copy(aureq.opts, {implicit: true});

		var t = aureq.target;
		if (t) {
			ajaxSend(t.className == 'zk.Desktop' ? t: t.desktop, aureq, timeout);
		} else {
			var dts = zk.Desktop.all;
			for (var dtid in dts)
				ajaxSend(dts[dtid], aureq, timeout);
		}
	},
	/** Sends an AU request by placing in front of any other pending request.
	 * @param Event the request. If {@link Event#target} is null,
	 * the request will be sent to each desktop at the client.
	 * @param int timeout the time to wait before sending the request.
	 * 0 is assumed if not specified or negative.
	 * If negative, the request is assumed to be implicit, i.e., no message will
	 * be shown if an error occurs.
	 */
	sendAhead: function (aureq, timeout) {
		var t = aureq.target;
		if (t) {
			var dt = t.className == 'zk.Desktop' ? t: t.desktop;
			dt._aureqs.unshift(aureq);
			ajaxSend2(dt, timeout);
		} else {
			var dts = zk.Desktop.all;
			for (var dtid in dts) {
				dt._aureqs.unshift(aureq);
				ajaxSend2(dts[dtid], timeout);
			}
			return;
		}
	},

	////Ajax////
	/** Processes the AU response sent from the server
	 * @param String cmd the command, such as echo
	 * @param String data the data in a JSON string.
	 */
	process: function (cmd, data) { //by server only (encoded)
		doProcess(cmd, data ? jq.evalJSON(data): []);
	},
	/** Returns whether to ignore the ESC keystroke.
	 * It returns true if ZK Client Engine is sending an AU request
	 */
	shallIgnoreESC: function () {
		return ajaxReq;
	},
	_doCmds: function () { //called by mount.js, too
		zk.mnt.t = zUtl.now(); //used by zk.mnt's run

		for (var fn; fn = doCmdFns.shift();)
			fn();

		var ex, j = 0, rid = responseId;
		for (; j < cmdsQue.length; ++j) {
			if (zk.mounting) return; //wait zk.mnt's mtAU to call

			var cmds = cmdsQue[j];
			if (rid == cmds.rid || !rid || !cmds.rid //match
			|| zk.Desktop._ndt > 1) { //ignore multi-desktops (risky but...)
				cmdsQue.splice(j, 1);

				var oldrid = rid;
				if (cmds.rid) {
					if ((rid = cmds.rid + 1) >= 1000)
						rid = 1; //1~999
					responseId = rid;
				}

				try {
					if (doCmdsNow(cmds)) { //done
						j = -1; //start over
						if (zk.pfmeter) {
							var fn = function () {zAu._pfdone(cmds.dt, cmds.pfIds);};
							if (zk.mounting) doCmdFns.push(fn);
							else fn();
						}
					} else { //not done yet (=zk.mounting)
						responseId = oldrid; //restore
						cmdsQue.splice(j, 0, cmds); //put it back
						return; //wait zk.mnt's mtAU to call
					}
				} catch (e) {
					if (!ex) ex = e;
					j = -1; //start over
				}
			}
		}

		if (cmdsQue.length) { //sequence is wrong => enforce to run if timeout
			setTimeout(function () {
				if (cmdsQue.length && rid == responseId) {
					var r = cmdsQue[0].rid;
					for (j = 1; j < cmdsQue.length; ++j) { //find min
						var r2 = cmdsQue[j].rid,
							v = r2 - r;
						if (v > 500 || (v < 0 && v > -500)) r = r2;
					}
					responseId = r;
					zAu._doCmds();
				}
			}, 3600);
		} else
			checkProcessng();

		if (ex) throw ex;
	},

	/** Enforces all pending AU requests of the specified desktop to send immediately
	 * @return boolean whether it is sent successfully. If it has to wait
	 * for other condition, this method returns false.
	 */
	sendNow: function (dt) {
		var es = dt._aureqs;
		if (es.length == 0)
			return false;

		if (zk.mounting) {
			zk.afterMount(function(){zAu.sendNow(dt);});
			return true; //wait
		}

		if (ajaxReq || pendingReqInf) { //send ajax request one by one
			sendPending = true;
			return true;
		}

		//notify watches (fckez uses it to ensure its value is sent back correctly
		try {
			zWatch.fire('onSend', implicit);
		} catch (e) {
			zk.error(e.message);
		}

		//bug 1721809: we cannot filter out ctl even if zAu.processing

		//decide implicit and ignorable
		var implicit = true, ignorable = true, ctli, ctlc, uri, alive;
		for (var j = 0, el = es.length; j < el; ++j) {
			var aureq = es[j],
				evtnm = aureq.name,
				opts = aureq.opts = aureq.opts||{};
			if (opts.uri != uri) {
				if (j) break;
				uri = opts.uri;
			}
			if (implicit && !opts.ignorable) { //ignorable implies implicit
				ignorable = false;
				if (!opts.implicit)
					implicit = false;
			}
			if (opts.ctl && !ctli) {
				ctli = aureq.target.uuid;
				ctlc = evtnm;
			}
			if (!alive && (zk.timerAlive || evtnm != "onTimer") && evtnm != "dummy")
				alive = true;
		}

		if (alive)
			zAu._resetTimeout();

		//Consider XML (Pros: ?, Cons: larger packet)
		var content = "";
		for (var j = 0, el = es.length; el; ++j, --el) {
			var aureq = es.shift(),
				evtnm = aureq.name,
				target = aureq.target;
			if (aureq.opts.uri != uri) {
				es.unshift(aureq);
				break;
			}
			content += "&cmd_"+j+"="+evtnm;
			if (target && target.className != 'zk.Desktop')
				content += "&uuid_"+j+"="+target.uuid;

			var data = aureq.data, dtype = typeof data;
			if (dtype == 'string' || dtype == 'number' || dtype == 'boolean'
			|| jq.isArray(data))
				data = {'':data};
			if (data)
				content += "&data_"+j+"="+encodeURIComponent(toJSON(target, data));
		}

		if (content)
			ajaxSendNow({
				sid: seqId, uri: uri || zk.ajaxURI(null, {desktop:dt,au:true}),
				dt: dt, content: "dtid=" + dt.id + content,
				ctli: ctli, ctlc: ctlc, implicit: implicit,
				ignorable: ignorable, tmout: 0
			});
		return true;
	},
	/** A map of Ajax default setting used to send the AU requests.
	 * @type Map
	 */
	ajaxSettings: zk.$default({
		global: false,
		contentType: "application/x-www-form-urlencoded;charset=UTF-8"
	}, jq.ajaxSettings),

	// Adds performance request IDs that have been processed completely.
	// Called by moun.js, too
	_pfrecv: function (dt, pfIds) {
		pfAddIds(dt, '_pfRecvIds', pfIds);
	},
	// Adds performance request IDs that have been processed completely.
	// Called by moun.js, too
	_pfdone: function (dt, pfIds) {
		pfAddIds(dt, '_pfDoneIds', pfIds);
	},
	// Sets performance rquest IDs to the request's header
	// Called by moun.js, too
	_pfsend: function (dt, req, completeOnly) {
		if (!completeOnly)
			req.setRequestHeader("ZK-Client-Start",
				dt.id + "-" + pfIndex++ + "=" + Math.round(zUtl.now()));

		var ids;
		if (ids = dt._pfRecvIds) {
			req.setRequestHeader("ZK-Client-Receive", ids);
			dt._pfRecvIds = null;
		}
		if (ids = dt._pfDoneIds) {
			req.setRequestHeader("ZK-Client-Complete", ids);
			dt._pfDoneIds = null;
		}
	},

	/** Creates widgets based on an array of JavaScritp codes generated by
	 * Component.redraw() at the server.
	 * <p>This method is usually used with Java's ComponentsCtrl.redraw, and
	 * {@link Widget#replaceCavedChildren_}.
	 * @param Array codes an array of JavaScript codes generated at the server.
	 * For example, <code>smartUpdate("foo", ComponentsCtrl.redraw(getChildren());</code>
	 * @return Array an array of {@link Widget}.
	 */
	createWidgets: function (codes) {
		var wgts = [];
		for (var j = 0, len = codes.length; j < len; ++j) {
			zAu.stub = function (newwgt) {
				wgts.push(newwgt);
			};
			zk.mounting = true;
			$eval(codes[j]);
		}
		return wgts;
	}

	/** The AU command handler that handles commands not related to widgets.
	 * @type AuCmd0
	 */
	//cmd0: null, //jsdoc
	/** The AU command handler that handles commands releated to widgets.
	 * @type AuCmd1
	 */
	//cmd1: null, //jsdoc
};

//Commands//
/** @class zk.AuCmd0
 * The AU command handler for processes commands not related to widgets,
 * sent from the server.
 * <p>Notice {@link zAu}, {@link zk.AuCmd0} and {@link zk.AuCmd1}
 * are not available in <a href="http://docs.zkoss.org/wiki/ZK_Light">ZK Light</a>.
 * @see zAu#cmd0
 */
zAu.cmd0 = /*prototype*/ { //no uuid at all
	/** Sets a bookmark
	 * @param String bk the bookmark
	 */
	bookmark: function (bk) {
		zk.bmk.bookmark(bk);
	},
	/** Shows an error to indicate the desktop is timeout.
	 * @param String dtid the desktop UUID
	 * @param String msg the error message
	 */
	obsolete: function (dtid, msg) {
		zk.error(msg);
	},
	/** Shows an alert to indicate some error occurs.
	 * For widget's error message, use {@link #wrongValue} instead.
	 * @param String msg the error message
	 */
	alert: function (msg) {
		jq.alert(msg, {icon:'ERROR'});
	},
	/** Redirects to the specified URL.
	 * @param String url the URL to redirect to
	 * @param String target [optional] the window name to show the content
	 * of the URL. If omitted, it will replace the current content.
	 */
	redirect: function (url, target) {
		try {
			zUtl.go(url, {target: target, reload: true});
		} catch (ex) {
			if (!zk.confirmClose) throw ex;
		}
	},
	/** Changes the brower window's titile.
	 * @param String title the new title
	 */
	title: function (title) {
		document.title = title;
	},
	/* Executes the JavaScript.
	 * @param String script the JavaScript code snippet to execute
	 */
	script: function (script) {
		$eval(script);
	},
	/** Asks the client to echo back an AU request, such that
	 * the server can return other commands.
	 * It is used to give the end user a quick response before doing
	 * a long operation.
	 * @param String dtid the desktop ID ({@link zk.Desktop}).
	 * @see zk.AuCmd1#echo2
	 */
	echo: function (dtid) {
		zAu.send(new zk.Event(zk.Desktop.$(dtid), "dummy", null, {ignorable: true}));
	},
	/** Asks the client information.
	 * The client will reply the information in the <code>onClientInfo</code> response.
	 * @param String dtid the desktop ID ({@link zk.Desktop}).
	 */
	clientInfo: function (dtid) {
		zAu._cInfoReg = true;
		zAu.send(new zk.Event(zk.Desktop.$(dtid), "onClientInfo", 
			[new Date().getTimezoneOffset(),
			screen.width, screen.height, screen.colorDepth,
			jq.innerWidth(), jq.innerHeight(), jq.innerX(), jq.innerY()]));
	},
	/** Asks the client to download the resource at the specified URL.
	 * @param String url the URL to download from
	 */
	download: function (url) {
		if (url) {
			var ifr = jq('#zk_download')[0];
			if (ifr) {
				ifr.src = url; //It is OK to reuse the same iframe
			} else {
				var html = '<iframe src="'+url
				+'" id="zk_download" name="zk_download" style="visibility:hidden;width:0;height:0;border:0" frameborder="0"></iframe>';
				jq(document.body).append(html);
			}
		}
	},
	/** Prints the content of the browser window.
	 */
	print: function () {
		window.print();
	},
	/** Scrolls the content of the browser window.
	 * @param int x the offset (difference) in the X coordinate (horizontally) (pixels)
	 * @param int y the offset in the Y coordinate (vertically) (pixels)
	 */
	scrollBy: function (x, y) {
		window.scrollBy(x, y);
	},
	/** Scrolls the contents of the browser window to the specified location.
	 * @param int x the X coordinate to scroll to (pixels)
	 * @param int y the Y coordinate to scroll to (pixels)
	 */
	scrollTo: function (x, y) {
		window.scrollTo(x, y);
	},
	/** Resizes the browser window.
	 * @param int x the number of pixels to increase/decrease (pixels)
	 * @param int y the number of pixels to increase/decrease (pixels)
	 */
	resizeBy: function (x, y) {
		window.resizeBy(x, y);
	},
	/** Resizes the browser window to the specified size.
	 * @param int x the required width (pixels)
	 * @param int y the required height (pixels)
	 */
	resizeTo: function (x, y) {
		window.resizeTo(x, y);
	},
	/** Moves the browser window.
	 * @param int x the number of pixels to move in the X coordinate
	 * @param int y the number of pixels to move in the Y coordinate
	 */
	moveBy: function (x, y) {
		window.moveBy(x, y);
	},
	/** Moves the browser window to the specified location
	 * @param int x the left (pixels)
	 * @param int y the top (pixels)
	 */
	moveTo: function (x, y) {
		window.moveTo(x, y);
	},
	/** Sets the message used to confirm the user when he is closing
	 * the browser window.
	 * @param String msg the message to show in the confirm dialog
	 */
	cfmClose: function (msg) {
		zk.confirmClose = msg;
	},
	/** Shows or removes the busy message.
	 * A mask will be shown to cover the browser window such that the
	 * user won't be able to take other actions.
	 * @param String msg the message. Ingored if open is false.
	 * @param boolean open whether to show. If omitted, true is assumed.
	 * @param String uuid the widget is applied the mask.
	 * If false, the message is removed.
	 */
	showBusy: function (msg, open, uuid) {
		var w = uuid ? zk.Widget.$(uuid): null, shby;
		if (w && (shby = w.effects_.showBusy)) {
			shby.destroy();
			delete w.effects_.showBusy;
		}
		jq("#zk_showBusy").remove(); //since user might want to show diff msg
		
		if (open || arguments.length == 1) {
			zUtl.destroyProgressbox("zk_loadprog");
			if (!uuid)
				zUtl.progressbox("zk_showBusy", msg || msgzk.PLEASE_WAIT, true);
			else if (w) {
				w.effects_.showBusy = new zk.eff.Mask( {
					id: w.uuid + "-shby",
					anchor: w.$n(),
					message: msg
				});
			}
		}
	},
	/** Closes the all error messages related to the specified widgets.
	 * It assumes {@link zk.Widget} has a method called <code>clearErrorMessage</code>
	 * (such as {@link zul.inp.InputWidget#clearErrorMessage}).
	 * If no such method, nothing happens.
	 * @param String... any number of UUID of widgets.
	 * @see #wrongValue
	 */
	clearWrongValue: function () {
		for (var i = arguments.length; i--;) {
			var wgt = zk.Widget.$(arguments[i]);
			if (wgt && wgt.clearErrorMessage)
				wgt.clearErrorMessage();
		}
	},
	/** Shows the error messages for the specified widgets.
	 * It assumes {@link zk.Widget} has a method called <code>setErrorMessage</code>
	 * (such as {@link zul.inp.InputWidget#setErrorMessage}).
	 * If no such method, {@link jq#alert} is used instead.
	 * @param Object... the widgets and messages. The first argument
	 * is the widget's UUID, and the second is the error message.
	 * The third is UUID, then the fourth the error message, and so on.
	 * @see #clearWrongValue
	 */
	wrongValue: function () {
		for (var i = 0, len = arguments.length - 1; i < len; i += 2) {
			var uuid = arguments[i], msg = arguments[i + 1],
				wgt = zk.Widget.$(uuid);
			if (wgt) {
				if (wgt.setErrorMessage) wgt.setErrorMessage(msg);
				else jq.alert(msg);
			} else if (!uuid) //keep silent if component (of uuid) not exist (being detaced)
				jq.alert(msg);
		}
	},
	/** Submit a form.
	 * This method looks for the widget first. If found and the widget
	 * has a method called <code>submit</code>, then the widget's <code>submit</code>
	 * method is called. Otherwise, it looks for the DOM element
	 * and invokes the <code>submit</code> method (i.e., assume it is
	 * the FROM element).
	 * @param String id the UUID of the widget, or the ID of the FORM element.
	 */
	submit: function (id) {
		setTimeout(function (){
			var n = zk.Widget.$(id);
			if (n && n.submit)
				n.submit();
			else {
				n = zk(id).jq[0];
				if (n && n.submit) {
					jq.Event.fire(n, 'submit');
					n.submit();
				}
			}
		}, 50);
	},
	/** Scrolls the widget or an DOM element into the view
	 * @param String id the UUID of the widget, or the ID of the DOM element.
	 */
	scrollIntoView: function (id) {
		var w = zk.Widget.$(id);
		if (w) w.scrollIntoView();
		else zk(id).scrollIntoView();
	}
};
/** @class zk.AuCmd1
 * The AU command handler for processes commands related to widgets,
 * sent from the server.
 * <p>Notice {@link zAu}, {@link zk.AuCmd0} and {@link zk.AuCmd1}
 * are not available in <a href="http://docs.zkoss.org/wiki/ZK_Light">ZK Light</a>.
 * @see zAu#cmd1
 */
zAu.cmd1 = /*prototype*/ {
	/** Sets the attribute of a widget.
	 * @param zk.Widget wgt the widget
	 * @param String name the name of the attribute
	 * @param Object value the value of the attribute.
	 */
	setAttr: function (wgt, nm, val) {
		if (nm == 'z$pk') zk.load(val); //load pkgs
		else if (nm == 'z$al') { //afterLoad
			zk.afterLoad(function () {
				for (var p in val)
					props[p] = v[p](); //must be func
			});
		} else
			wgt.set(nm, val, true); //3rd arg: fromServer
	},
	/** Replaces the widget with the widget(s) generated by evaluating the specified JavaScript code snippet.
	 * @param zk.Widget wgt the old widget to be replaced
	 * @param String code the JavaScript code snippet to generate new widget(s).
	 */
	outer: function (wgt, code) {
		zAu.stub = function (newwgt) {
			wgt._replaceWgt(newwgt);
		};
		zk.mounting = true;
		$eval(code);
	},
	/** Adds the widget(s) generated by evaluating the specified JavaScript code snippet
	 * after the specified widget (as sibling).
	 * @param zk.Widget wgt the existent widget that new widget(s) will be inserted after
	 * @param String code the JavaScript code snippet to generate new widget(s).
	 */
	addAft: function (wgt, code, pgid) {
		//Bug 1939059: This is a dirty fix. Refer to AuInsertBefore
		//Format: comp-uuid:pg-uuid (if native root)
		if ((!wgt || (!wgt.z_rod && !wgt.$n())) && pgid) {
			wgt = zk.Widget.$(pgid);
			if (wgt) zAu.cmd1.addChd(pgid, wgt, code);
			else {
				zAu.stub = _asBodyChild;
				zk.mounting = true;
				$eval(code);
			}
			return;
		}

		zAu.stub = function (child) {
			var p = wgt.parent;
			if (p) {
				p.insertBefore(child, wgt.nextSibling);
				if (p.$instanceof(zk.Desktop))
					_asBodyChild(child);
			} else {
				var n = wgt.$n();
				if (n)
					jq(n).after(child, wgt.desktop);
				else
					_asBodyChild(child);
			}
			if (!child.z_rod) {
				zWatch.fireDown('beforeSize', child);
				zWatch.fireDown('onSize', child);
			}
		};
		zk.mounting = true;
		$eval(code);
	},
	/** Adds the widget(s) generated by evaluating the specified JavaScript code snippet
	 * before the specified widget (as sibling).
	 * @param zk.Widget wgt the existent widget that new widget(s) will be inserted before
	 * @param String code the JavaScript code snippet to generate new widget(s).
	 */
	addBfr: function (wgt, code) {
		zAu.stub = function (child) {
			wgt.parent.insertBefore(child, wgt);
			if (!child.z_rod) {
				zWatch.fireDown('beforeSize', child);
				zWatch.fireDown('onSize', child);
			}
		};
		zk.mounting = true;
		$eval(code);
	},
	/** Adds the widget(s) generated by evaluating the specified JavaScript code snippet
	 * as the last child of the specified widget.
	 * @param zk.Widget wgt the existent widget that will become the parent of new widget(s)
	 * @param String code the JavaScript code snippet to generate new widget(s).
	 */
	addChd: function (wgt, code) {
		zAu.stub = function (child) {
			wgt.appendChild(child);
			if (!child.z_rod) {
				zWatch.fireDown('beforeSize', child);
				zWatch.fireDown('onSize', child);
			}
		};
		zk.mounting = true;
		$eval(code);
	},
	/** Removes the widget.
	 * @param zk.Widget wgt the widget to remove
	 */
	rm: function (wgt) {
		if (wgt)
			wgt.detach();
	},
	/** Set the focus to the specified widget.
	 * It invokes {@link zk.Widget#focus}. Not all widgets support
	 * this method. In other words, it has no effect if the widget doesn't support it.
	 * @param zk.Widget wgt the widget.
	 */
	focus: function (wgt) {
		if (wgt)
			wgt.focus(0); //wgt.focus() failed in FF
	},
	/** Selects all text of the specified widget.
	 * It invokes the <code>select</code> method, if any, of the widget.
	 * It does nothing if the method doesn't exist.
	 * @param zk.Widget wgt the widget.
	 */
	select: function (wgt, s, e) {
		if (wgt.select) wgt.select(s, e);
	},
	/** Invokes the specifed method of the specified widget.
	 * In other words, it is similar to execute the following:
	 * <pre><code>wgt[func].apply(wgt, vararg);</code></pre>
	 *
	 * @param zk.Widget wgt the widget to invoke
	 * @param String func the function name
	 * @param Object... vararg any number of arguments passed to the function
	 * invocation.
	 */
	invoke: function (wgt, func, vararg) {
		var args = [];
		for (var j = arguments.length; --j > 2;)
			args.unshift(arguments[j]);
		wgt[func].apply(wgt, args);
	},
	/** Ask the client to echo back an AU request with the specified
	 * evant name and the target widget.
	 * @param zk.Widget wgt the target widget to which the AU request will be sent
	 * @param String evtnm the name of the event, such as onUser
	 * @param Object data any data
	 */
	echo2: function (wgt, evtnm, data) {
		zAu.send(new zk.Event(wgt, "echo",
			data != null ? [evtnm, data]: [evtnm], {ignorable: true}));
	}
};
})();

function onIframeURLChange(uuid, url) { //doc in jsdoc
	if (!zk.unloading) {
		var wgt = zk.Widget.$(uuid);
		if (wgt) wgt.fire("onURIChange", url);
	}
};
