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
zAu = {
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
	/** Called by mount.js when onReSize */
	_onClientInfo: function () {
		if (zAu._cInfoReg)
			setTimeout(zAu._fireClientInfo, 20);
				//we cannot pass zAu.cmd0.clientInfo directly
				//otherwise, FF will pass 1 as the firt argument,
				//i.e., it is equivalent to zAu.cmd0.clientInfo(1)
	},
	_fireClientInfo: function () {
		zAu.cmd0.clientInfo();
	},

	//Error Handling//
	confirmRetry: function (msgCode, msg2) {
		var msg = mesg[msgCode];
		return zDom.confirm((msg?msg:msgCode)+'\n'+mesg.TRY_AGAIN+(msg2?"\n\n("+msg2+")":""));
	},
	showError: function (msgCode, msg2, cmd, ex) {
		var msg = mesg[msgCode];
		zk.error((msg?msg:msgCode)+'\n'+(msg2?msg2:"")+(cmd?cmd:"")+(ex?"\n"+ex.message:""));
	},
	getErrorURI: function (code) {
		return zAu._eru['e' + code];
	},
	setErrorURI: function (code, uri) {
		if (len > 2) {
			for (var j = 0; j < len; j += 2)
				zAu.setErrorURI(args[j], args[j + 1]);
			return;
		}
		zAu._eru['e' + code] = uri;
	},
	_eru: {},

	////Ajax Send////
	processing: function () {
		return zk.mounting || zAu._cmdsQue.length || zAu._areq || zAu._preqInf;
	},
	/** Checks whether to turn off the progress prompt. */
	_ckProcessng: function () {
		if (!zAu.processing()) {
			zk.endProcessing();
			zAu.doneTime = zUtl.now();
		}
	},

	send: function (aureq, timeout) {
		if (timeout < 0) {
			var opts = aureq.opts;
			if (!opts) opts = aureq.opts = {};
			opts.implicit = true;
		}

		var t = aureq.target;
		if (t) {
			zAu._send(t.className == 'zk.Desktop' ? t: t.desktop, aureq, timeout);
		} else {
			var dts = zk.Desktop.all;
			for (var dtid in dts)
				zAu._send(dts[dtid], aureq, timeout);
		}
	},
	sendAhead: function (aureq, timeout) {
		var t = aureq.target;
		if (t) {
			var dt = t.className == 'zk.Desktop' ? t: t.desktop;
			dt._aureqs.unshift(aureq);
			zAu._send2(dt, timeout);
		} else {
			var dts = zk.Desktop.all;
			for (var dtid in dts) {
				dt._aureqs.unshift(aureq);
				zAu._send2(dts[dtid], timeout);
			}
			return;
		}
	},

	////Ajax receive////
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
			rid = zk.parseInt(zUtl.getElementValue(rid[0])); //response ID
			if (!isNaN(rid)) cmds.rid = rid;
		}

		for (var j = 0, rl = rs ? rs.length: 0; j < rl; ++j) {
			var cmd = rs[j].getElementsByTagName("c")[0],
				data = rs[j].getElementsByTagName("d");

			if (!cmd) {
				zk.error(mesg.ILLEGAL_RESPONSE+"Command required");
				continue;
			}

			cmds.push(cmd = {cmd: zUtl.getElementValue(cmd),
				data: data && data.length ?
					eval(zUtl.getElementValue(data[0])): []});
		}

		zAu._cmdsQue.push(cmds);
		return true;
	},
	process: function (cmd, data) { //by server only (encoded)
		zAu._process(cmd, data ? eval(data): []);
	},
	_process: function (cmd, data) { //decoded
		//I. process commands that data[0] is not UUID
		var fn = zAu.cmd0[cmd];
		if (fn) {
			fn.apply(zAu, data);
			return;
		}

		//I. process commands that require uuid
		if (!data || !data.length) {
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
	},
	shallIgnoreESC: function () {
		return zAu._areq;
	},

	//ajax internal//
	_cmdsQue: [], //response commands in XML
	_seqId: (zUtl.now() % 9999) + 1, //1-9999 (random init: bug 2691017)

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
	/** Called when the response is received from _areq. */
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
						if (sid && ++zAu._seqId > 9999) zAu._seqId = 1;
						zAu._areqTry = 0;
						zAu._preqInf = null;
					}
				} else if (!sid || sid == zAu._seqId) { //ignore only if out-of-seq (note: 467 w/o sid)
					zAu._errcode = req.status;
					var eru = zAu._eru['e' + req.status];
					if (typeof eru == "string") {
						zUtl.go(eru);
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

						if (!zAu._ignorable && !zk.unloading) {
							var msg = req.statusText;
							if (zAu.confirmRetry("FAILED_TO_RESPONSE", req.status+(msg?": "+msg:""))) {
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
			if (!zAu._ignorable && !zk.unloading) {
				var msg = e.message;
				zAu._errcode = "[Receive] " + msg;
				//if (e.fileName) zAu._errcode += ", "+e.fileName;
				//if (e.lineNumber) zAu._errcode += ", "+e.lineNumber;
				if (zAu.confirmRetry("FAILED_TO_RESPONSE", (msg&&msg.indexOf("NOT_AVAILABLE")<0?msg:""))) {
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
	},

	_send: function (dt, aureq, timeout) {
		var opts = aureq.opts, clkfd = zk.clickFilterDelay;
		if (clkfd > 0 && opts && opts.ctl) {
			//Don't send the same request if it is in processing
			if (zAu._areqInf && zAu._areqInf.ctli == aureq.uuid
			&& zAu._areqInf.ctlc == aureq.cmd)
				return;

			var t = zUtl.now();
			if (zAu._ctli == aureq.uuid && zAu._ctlc == aureq.cmd //Bug 1797140
			&& t - zAu._ctlt < clkfd)
				return; //to prevent key stroke are pressed twice (quickly)

			//Note: it is still possible to queue two ctl with same uuid and cmd,
			//if the first one was not sent yet and the second one is generated
			//after 390ms. However, it is rare so no handle it

			zAu._ctlt = t;
			zAu._ctli = aureq.uuid;
			zAu._ctlc = aureq.cmd;
		}

		dt._aureqs.push(aureq);

		zAu._send2(dt, timeout);
			//Note: we don't send immediately (Bug 1593674)
	},
	_send2: function (dt, timeout) {
		if (!timeout) timeout = 0;
		if (dt && timeout >= 0)
			setTimeout(function(){zAu.sendNow(dt);}, timeout);
	},
	sendNow: function (dt) {
		var es = dt._aureqs;
		if (es.length == 0)
			return false;

		if (zk.mounting) {
			zk.afterMount(function(){zAu.sendNow(dt);});
			return true; //wait
		}

		if (zAu._areq || zAu._preqInf) { //send ajax request one by one
			zAu._sendPending = true;
			return true;
		}

		//notify watches (fckez uses it to ensure its value is sent back correctly
		try {
			zWatch.fire('onSend', null, implicit);
		} catch (e) {
			zk.error(e.message);
		}

		//bug 1721809: we cannot filter out ctl even if zAu.processing

		//decide implicit and ignorable
		var implicit = true, ignorable = true, ctli, ctlc;
		for (var j = es.length; --j >= 0;) {
			var aureq = es[j], opts = aureq.opts;
			if (implicit && (!opts || !opts.ignorable)) { //ignorable implies implicit
				ignorable = false;
				if (!opts || !opts.implicit)
					implicit = false;
			}
			if (opts && opts.ctl && !ctli) {
				ctli = aureq.target.uuid;
				ctlc = aureq.name;
			}
		}
		zAu._ignorable = ignorable;

		//Consider XML (Pros: ?, Cons: larger packet)
		var content = "", $Event = zk.Event;
		for (var j = 0, el = es.length; el; ++j, --el) {
			var aureq = es.shift(),
				evtnm = aureq.name,
				target = aureq.target;
			content += "&cmd."+j+"="+evtnm;
			if (target && target.className != 'zk.Desktop')
				content += "&uuid."+j+"="+target.uuid;

			var data = aureq.data, dtype = typeof data;
			if (dtype == 'string' || dtype == 'number' || dtype == 'boolean'
			|| (data && data.$array))
				data = {'':data};
			if (data)
				content += "&data."+j+"="+encodeURIComponent(zJSON.stringify(data));
		}

		if (content)
			zAu._sendNow2({
				sid: zAu._seqId, uri: zAu.comURI(null, dt),
				dt: dt, content: "dtid=" + dt.id + content,
				ctli: ctli, ctlc: ctlc, implicit: implicit,
				ignorable: ignorable, tmout: 0
			});
		return true;
	},
	_sendNow2: function(reqInf) {
		var req = zUtl.newAjax(),
			uri = zAu._useQS(reqInf) ? reqInf.uri + '?' + reqInf.content: null;
		zAu.sentTime = zUtl.now(); //used by server-push (zkex)
		try {
			req.onreadystatechange = zAu._onRespReady;
			req.open("POST", uri ? uri: reqInf.uri, true);
			req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			req.setRequestHeader("ZK-SID", reqInf.sid);
			if (zAu._errcode) {
				req.setRequestHeader("ZK-Error-Report", zAu._errcode);
				delete zAu._errcode;
			}

			if (zk.pfmeter) zAu._pfsend(reqInf.dt, req);

			zAu._areq = req;
			zAu._areqInf = reqInf;
			if (zk.resendDelay > 0)
				zAu._areqInf.tfn = setTimeout(zAu._areqTmout, zk.resendDelay + reqInf.tmout);

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
				zAu._errcode = "[Send] " + msg;
				if (zAu.confirmRetry("FAILED_TO_SEND", msg)) {
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

	doCmds: function () {
		_zkmt = zUtl.now(); //used by zkm.exec

		for (var fn; fn = zAu._dcfns.shift();)
			fn();

		var ex, j = 0, que = zAu._cmdsQue, rid = zAu._resId;
		for (; j < que.length; ++j) {
			if (zk.mounting) return; //wait zkm.mtAU to call

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
						if (zk.pfmeter) {
							var fn = function () {zAu.pfdone(cmds.dt, cmds.pfIds);};
							if (zk.mounting) zAu._dcfns.push(fn);
							else fn();
						}
					} else { //not done yet (=zk.mounting)
						zAu._resId = oldrid; //restore
						que.splice(j, 0, cmds); //put it back
						return; //wait zkm.mtAU to call
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
		} else
			zAu._ckProcessng();

		if (ex) throw ex;
	},
	_dcfns: [],
	_doCmds1: function (cmds) {
		var processed;
		try {
			while (cmds && cmds.length) {
				if (zk.mounting) return false;

				processed = true;
				var cmd = cmds.shift();
				try {
					zAu._process(cmd.cmd, cmd.data);
				} catch (e) {
					zAu.showError("FAILED_TO_PROCESS", null, cmd.cmd, e);
					throw e;
				}
			}
		} finally {
			if (processed && (!cmds || !cmds.length))
				zWatch.fire('onResponse', {timeout:0}); //use setTimeout
		}
		return true;
	},
	//Used by zkm.mtAU to know any pending
	_moreCmds: function () {
		return zAu._cmdsQue.length
	},

	/** Cleans up if we detect obsolete or other severe errors. */
	_cleanupOnFatal: function (ignorable) {
		for (var uuid in zAu._metas) {
			var meta = zAu._metas[uuid];
			if (meta && meta.cleanupOnFatal)
				meta.cleanupOnFatal(ignorable);
		}
	},

	//Perfomance Meter//
	_pfj: 0, //an index
	_pfsend: function (dt, req, completeOnly) {
		if (!completeOnly)
			req.setRequestHeader("ZK-Client-Start",
				dt.id + "-" + zAu._pfj++ + "=" + Math.round(zUtl.now()));

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
	/** Returns request IDs sent from the server separated by space. */
	_pfGetIds: function (req) {
		return req.getResponseHeader("ZK-Client-Complete");
	},
	/** Adds performance request IDs that have been processed completely. */
	pfrecv: function (dt, pfIds) {
		zAu._pfAddIds(dt, '_pfRecvIds', pfIds);
	},
	/** Adds performance request IDs that have been processed completely. */
	pfdone: function (dt, pfIds) {
		zAu._pfAddIds(dt, '_pfDoneIds', pfIds);
	},
	_pfAddIds: function (dt, prop, pfIds) {
		if (pfIds && (pfIds = pfIds.trim())) {
			var s = pfIds + "=" + Math.round(zUtl.now());
			if (dt[prop]) dt[prop] += ',' + s;
			else dt[prop] = s;
		}
	}
};

//Commands//
zAu.cmd0 = { //no uuid at all
	bookmark: function (bk) {
		zHistory.bookmark(bk);
	},
	obsolete: function (dt0, dt1) { //desktop timeout
		zAu._cleanupOnFatal();
		zk.error(dt1);
	},
	alert: function (msg) {
		zDom.alert(msg);
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
		eval(dt0);
	},
	echo: function (dtid) {
		zAu.send(new zk.Event(zk.Desktop.$(dtid), "dummy", null, {ignorable: true}));
	},
	clientInfo: function (dtid) {
		zAu._cInfoReg = true;
		zAu.send(new zk.Event(zk.Desktop.$(dtid), "onClientInfo", 
			[new Date().getTimezoneOffset(),
			screen.width, screen.height, screen.colorDepth,
			zDom.innerWidth(), zDom.innerHeight(), zDom.innerX(), zDom.innerY()]));
	},
	download: function (url) {
		if (url) {
			var ifr = zDom.$('zk_download');
			if (ifr) {
				ifr.src = url; //It is OK to reuse the same iframe
			} else {
				var html = '<iframe src="'+url
				+'" id="zk_download" name="zk_download" style="visibility:hidden;width:0;height:0;border:0" frameborder="0"></iframe>';
				zDom.insertHTMLBeforeEnd(document.body, html);
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
		zDom.remove("zk_showBusy"); //since user might want to show diff msg

		if (open) {
			zUtl.destroyProgressbox("zk_loadprog");
			zUtl.progressbox("zk_showBusy", msg || mesg.PLEASE_WAIT, true);
		}
	},
	closeErrbox: function () {
		for (var i = arguments.length; --i >= 0;) {
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
				if (wgt.showErrorMessage) wgt.showErrorMessage(msg);
				else zDom.alert(msg);
			} else if (!uuid) //keep silent if component (of uuid) not exist (being detaced)
				zDom.alert(msg);
		}
	},
	submit: function (id) {
		setTimeout(function (){
			var n = zk.Widget.$(id);
			if (n && n.submit)
				n.submit();
			else {
				n = zDom.$(id);
				if (n && n.submit) {
					zEvt.fire(n, 'submit');
					n.submit();
				}
			}
		}, 50);
	}
};
zAu.cmd1 = {
	setAttr: function (uuid, wgt, nm, val) {
		wgt.set(nm, val, true); //3rd arg: fromServer
	},
	outer: function (uuid, wgt, code) {
		zAu.stub = function (newwgt) {
			wgt._replaceWgt(newwgt);
		};
		zk.mounting = true;
		eval(code);
	},
	addAft: function (uuid, wgt, code) {
		//Bug 1939059: This is a dirty fix. Refer to AuInsertBefore
		//Format: comp-uuid:pg-uuid (if native root)
		if (!wgt) {
			var j = uuid.indexOf(':');
			if (j >= 0) { //native root
				wgt = zk.Widget.$(uuid.substring(0, j)); //try comp (though not possible)
				if (!wgt) {
					uuid = uuid.substring(j + 1); //try page
					wgt = zk.Widget.$(uuid);
					if (wgt) zAu.cmd1.addChd(uuid, wgt, code);
					else {
						zAu.stub = zAu.cmd1._asBodyChild;
						zk.mounting = true;
						eval(code);
					}
					return;
				}
			}
		}

		zAu.stub = function (child) {
			var p = wgt.parent;
			p.insertBefore(child, wgt.nextSibling);
			if (p.$instanceof(zk.Desktop))
				zAu.cmd1._asBodyChild(child);
		};
		zk.mounting = true;
		eval(code);
	},
	addBfr: function (uuid, wgt, code) {
		zAu.stub = function (child) {
			wgt.parent.insertBefore(child, wgt);
		};
		zk.mounting = true;
		eval(code);
	},
	addChd: function (uuid, wgt, code) {
		zAu.stub = function (child) {
			wgt.appendChild(child);
		};
		zk.mounting = true;
		eval(code);
	},
	_asBodyChild: function (child) {
		child.insertHTML(document.body, "beforeEnd");
	},
	rm: function (uuid, wgt) {
		if (wgt) {
			var p = wgt.parent;
			if (p) p.removeChild(wgt);
			else {
				p = wgt.getNode();
				wgt.unbind_();
				zDom.remove(p);
			}
		}
	},
	focus: function (uuid, wgt) {
		setTimeout(function() {wgt.focus();}, 5);
			//consecutive call wgt.focus causes error in FF
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
