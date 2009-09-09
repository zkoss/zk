/* au.js

	Purpose:
		ZK Client Engine
	Description:
	
	History:
		Mon Sep 29 17:17:37     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zAu = (function () {
	var errURIs = {}, errCode,
		cmdsQue = [], //response commands in XML
		ajaxReq, ajaxReqInf, pendingReqInf, ajaxReqTries,
		sendPending, ctlUuid, ctlTime, ctlCmd, responseId,
		seqId = (zUtl.now() % 9999) + 1, //1-9999 (random init: bug 2691017)
		doCmdFns = [],
		pfIndex = 0; //performance meter index

	/** Checks whether to turn off the progress prompt. */
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

		if (zk.pfmeter) {
			cmds.dt = dt;
			cmds.pfIds = pfGetIds(req);
		}

		rt = jq.evalJSON(rt);

		var cmds = [],
			rs = rt.rs,
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
				zk.error(mesg.ILLEGAL_RESPONSE+"Command required");
				continue;
			}

			cmds.push({cmd: cmd, data: data || []});
		}

		cmdsQue.push(cmds);
		return true;
	}
	function doProcess(cmd, data) { //decoded
		//I. process commands that data[0] is not UUID
		var fn = zAu.cmd0[cmd];
		if (fn) {
			fn.apply(zAu, data);
			return;
		}

		//I. process commands that require uuid
		if (!data.length) {
			zAu.showError("ILLEGAL_RESPONSE", "uuid is required for ", cmd);
			return;
		}

		fn = zAu.cmd1[cmd];
		if (fn) {
			data.splice(1, 0, zk.Widget.$(data[0])); //insert wgt
			fn.apply(zAu, data);
			return;
		}

		zAu.showError("ILLEGAL_RESPONSE", "Unknown command: ", cmd);
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
	/** Called when the response is received from ajaxReq. */
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
					var eru = errURIs['e' + req.status];
					if (typeof eru == "string") {
						zUtl.go(eru);
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
		zAu.sentTime = zUtl.now(); //used by server-push (zkex)
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
		var processed;
		try {
			while (cmds && cmds.length) {
				if (zk.mounting) return false;

				processed = true;
				var cmd = cmds.shift();
				try {
					doProcess(cmd.cmd, cmd.data);
				} catch (e) {
					zAu.showError("FAILED_TO_PROCESS", null, cmd.cmd, e);
					throw e;
				}
			}
		} finally {
			if (processed && (!cmds || !cmds.length))
				zWatch.fire('onResponse', null, {timeout:0}); //use setTimeout
		}
		return true;
	}

	//Perfomance Meter//
	/** Returns request IDs sent from the server separated by space. */
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

  return {
	_onClientInfo: (function () { //Called by mount.js when onReSize
		function fireClientInfo() {
			zAu.cmd0.clientInfo();
		}
		return function () {
			if (zAu._cInfoReg) setTimeout(fireClientInfo, 20);
			//we cannot pass zAu.cmd0.clientInfo directly
			//otherwise, FF will pass 1 as the firt argument,
			//i.e., it is equivalent to zAu.cmd0.clientInfo(1)
		}
	})(),

	//Error Handling//
	confirmRetry: function (msgCode, msg2) {
		var msg = mesg[msgCode];
		return jq.confirm((msg?msg:msgCode)+'\n'+mesg.TRY_AGAIN+(msg2?"\n\n("+msg2+")":""));
	},
	showError: function (msgCode, msg2, cmd, ex) {
		var msg = mesg[msgCode];
		zk.error((msg?msg:msgCode)+'\n'+(msg2?msg2:"")+(cmd?cmd:"")+(ex?"\n"+ex.message:""));
	},
	getErrorURI: function (code) {
		return errURIs['e' + code];
	},
	setErrorURI: function (code, uri) {
		if (arguments.length == 1) {
			for (var c in code)
				zAu.setErrorURI(c, code[c]);
			return;
		}
		errURIs['e' + code] = uri;
	},

	////Ajax Send////
	processing: function () {
		return zk.mounting || cmdsQue.length || ajaxReq || pendingReqInf;
	},

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
	process: function (cmd, data) { //by server only (encoded)
		doProcess(cmd, data ? jq.evalJSON(data): []);
	},
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
		var implicit = true, ignorable = true, ctli, ctlc, uri;
		for (var j = 0, el = es.length; j < el; ++j) {
			var aureq = es[j], opts = aureq.opts = aureq.opts||{};
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
				ctlc = aureq.name;
			}
		}

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
			|| (data && data.$array))
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
	ajaxSettings: zk.$default({
		global: false,
		contentType: "application/x-www-form-urlencoded;charset=UTF-8"
	}, jq.ajaxSettings),

	/** Adds performance request IDs that have been processed completely.
	 * Called by moun.js, too
	 */
	_pfrecv: function (dt, pfIds) {
		pfAddIds(dt, '_pfRecvIds', pfIds);
	},
	/** Adds performance request IDs that have been processed completely.
	 * Called by moun.js, too
	 */
	_pfdone: function (dt, pfIds) {
		pfAddIds(dt, '_pfDoneIds', pfIds);
	},
	/** Sets performance rquest IDs to the request's header
	 * Called by moun.js, too
	 */
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
	}
 };
})();

//Commands//
zAu.cmd0 = { //no uuid at all
	bookmark: function (bk) {
		zk.bmk.bookmark(bk);
	},
	obsolete: function (dt0, dt1) { //desktop timeout
		zk.error(dt1);
	},
	alert: function (msg) {
		jq.alert(msg, {icon:'ERROR'});
	},
	redirect: function (url, target) {
		try {
			zUtl.go(url, false, target);
		} catch (ex) {
			if (!zk.confirmClose) throw ex;
		}
	},
	title: function (dt0) {
		document.title = dt0;
	},
	script: function (dt0) {
		jq.globalEval(dt0);
	},
	echo: function (dtid) {
		zAu.send(new zk.Event(zk.Desktop.$(dtid), "dummy", null, {ignorable: true}));
	},
	clientInfo: function (dtid) {
		zAu._cInfoReg = true;
		zAu.send(new zk.Event(zk.Desktop.$(dtid), "onClientInfo", 
			[new Date().getTimezoneOffset(),
			screen.width, screen.height, screen.colorDepth,
			jq.innerWidth(), jq.innerHeight(), jq.innerX(), jq.innerY()]));
	},
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
		zk.confirmClose = msg;
	},
	showBusy: function (msg, open) {
		jq("#zk_showBusy").remove(); //since user might want to show diff msg

		if (open) {
			zUtl.destroyProgressbox("zk_loadprog");
			zUtl.progressbox("zk_showBusy", msg || mesg.PLEASE_WAIT, true);
		}
	},
	closeErrbox: function () {
		for (var i = arguments.length; i--;) {
			var wgt = zk.Widget.$(arguments[i]);
			if (wgt && wgt.clearErrorMessage)
				wgt.clearErrorMessage();
		}
	},
	wrongValue: function () {
		for (var i = 0, len = arguments.length - 1; i < len; i += 2) {
			var uuid = arguments[i], msg = arguments[i + 1],
				wgt = zk.Widget.$(uuid);
			if (wgt) {
				if (wgt.showErrorMessage_) wgt.showErrorMessage_(msg);
				else jq.alert(msg);
			} else if (!uuid) //keep silent if component (of uuid) not exist (being detaced)
				jq.alert(msg);
		}
	},
	submit: function (id) {
		setTimeout(function (){
			var n = zk.Widget.$(id);
			if (n && n.submit)
				n.submit();
			else {
				n = zk(id).jq[0];
				if (n && n.submit) {
					zEvt.fire(n, 'submit');
					n.submit();
				}
			}
		}, 50);
	},
	scrollIntoView: function (id) {
		var w = zk.Widget.$(id);
		if (w) w.scrollIntoView();
		else zk(id).scrollIntoView();
	}
};
zAu.cmd1 = {
	setAttr: function (uuid, wgt, nm, val) {
		if (nm == 'z_pk') zk.load(val); //load pkgs
		else if (nm == 'z_al') { //afterLoad
			zk.afterLoad(function () {
				for (var p in val)
					props[p] = v[p](); //must be func
			});
		} else
			wgt.set(nm, val, true); //3rd arg: fromServer
	},
	outer: function (uuid, wgt, code) {
		zAu.stub = function (newwgt) {
			wgt._replaceWgt(newwgt);
		};
		zk.mounting = true;
		jq.globalEval(code);
	},
	addAft: function (uuid, wgt, code, pgid) {
		//Bug 1939059: This is a dirty fix. Refer to AuInsertBefore
		//Format: comp-uuid:pg-uuid (if native root)
		if (!wgt && pgid) {
			wgt = zk.Widget.$(pgid);
			if (wgt) zAu.cmd1.addChd(pgid, wgt, code);
			else {
				zAu.stub = zAu.cmd1._asBodyChild;
				zk.mounting = true;
				jq.globalEval(code);
			}
			return;
		}

		zAu.stub = function (child) {
			var p = wgt.parent;
			p.insertBefore(child, wgt.nextSibling);
			if (p.$instanceof(zk.Desktop))
				zAu.cmd1._asBodyChild(child);
			zWatch.fireDown('beforeSize', child);
			zWatch.fireDown('onSize', child);
		};
		zk.mounting = true;
		jq.globalEval(code);
	},
	addBfr: function (uuid, wgt, code) {
		zAu.stub = function (child) {
			wgt.parent.insertBefore(child, wgt);
			zWatch.fireDown('beforeSize', child);
			zWatch.fireDown('onSize', child);
		};
		zk.mounting = true;
		jq.globalEval(code);
	},
	addChd: function (uuid, wgt, code) {
		zAu.stub = function (child) {
			wgt.appendChild(child);
			zWatch.fireDown('beforeSize', child);
			zWatch.fireDown('onSize', child);
		};
		zk.mounting = true;
		jq.globalEval(code);
	},
	_asBodyChild: function (child) {
		jq(document.body).append(child);
	},
	rm: function (uuid, wgt) {
		if (wgt)
			wgt.detach();
	},
	focus: function (uuid, wgt) {
		setTimeout(function() {wgt.focus();}, 5);
			//consecutive call wgt.focus causes error in FF
	},
	select: function (uuid, wgt, s, e) {
		if (wgt.select) wgt.select(s, e);
	},
	invoke: function (uuid, wgt, func, vararg) {
		var args = [];
		for (var j = arguments.length; --j > 2;)
			args.unshift(arguments[j]);
		wgt[func].apply(wgt, args);
	},
	echo2: function (uuid, wgt, evtnm, data) {
		zAu.send(new zk.Event(wgt, "echo",
			data != null ? [evtnm, data]: [evtnm], {ignorable: true}));
	}
};

/** Callback when iframe's URL/bookmark been changed.
 * Notice the containing page might not be ZK. It could be any technology
 * and it can got the notification by implementing this method.
 * @param uuid the component UUID
 * @param url the new URL
 */
function onIframeURLChange(uuid, url) {
	if (!zk.unloading) {
		var wgt = zk.Widget.$(uuid);
		if (wgt) wgt.fire("onURIChange", url);
	}
};
