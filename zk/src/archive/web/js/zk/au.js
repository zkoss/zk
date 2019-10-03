/* au.js

	Purpose:
		ZK Client Engine
	Description:
	
	History:
		Mon Sep 29 17:17:37     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _perrURIs = {}, //server-push error URI
		_onErrs = [], //onError functions
		cmdsQue = [], //response commands in XML
		sendPending, ctlUuid, ctlTime, ctlCmd, responseId,
		doCmdFns = [],
		idTimeout, //timer ID for automatica timeout
		pfIndex = 0, //performance meter index
		_detached = [], //used for resolving #stub/#stubs in mount.js (it stores detached widgets in this AU)
		Widget = zk.Widget,
		_portrait = {'0': true, '180': true}, //default portrait definition
		_initLandscape = jq.innerWidth() > jq.innerHeight(), // initial orientation is landscape or not
		_initDefault = _portrait[window.orientation], //default orientation
		_aftAuResp = []; //store callbacks to be triggered when au is back
	
	// Checks whether to turn off the progress prompt
	function checkProgressing(sid) {
		if (!zAu.processing()) {
			_detached = []; //clean up
			if (!zk.clientinfo)
				zk.endProcessing(sid);
				//setTimeout(zk.endProcessing, 50);
				// using a timeout to stop the processing after doing onSize in the fireSized() method of the Utl.js
				//Bug ZK-1505: using timeout cause progress bar disapper such as Thread.sleep(1000) case, so revert it back

			zAu.doneTime = jq.now();
		}
	}
	function pushCmds(cmds, rs) {
		for (var j = 0, rl = rs ? rs.length : 0; j < rl; ++j) {
			var r = rs[j],
				cmd = r[0],
				data = r[1];

			if (!cmd) {
				zAu.showError('ILLEGAL_RESPONSE', 'command required');
				continue;
			}

			cmds.push({cmd: cmd, data: data || []});
		}

		cmdsQue.push(cmds);
	}
	function dataNotReady(cmd, data) {
		for (var j = data.length, id, w; j--;)
			if (id = data[j] && data[j].$u) {
				if (!(w = Widget.$(id))) { //not ready
					zk.afterMount(function () {
						do {
							if (id = data[j] && data[j].$u)
								data[j] = Widget.$(id);
						} while (j--);
						doProcess(cmd, data);
					}, -1);
					return true; //not ready
				}
				data[j] = w;
			}
	}
	function doProcess(cmd, data) { //decoded
		if (!dataNotReady(cmd, data)) {
			if (!zAu.processPhase) {
				zAu.processPhase = cmd;
			}
			//1. process zAu.cmd1 (cmd1 has higher priority)
			var fn = zAu.cmd1[cmd];
			if (fn) {
				if (!data.length)
					return zAu.showError('ILLEGAL_RESPONSE', 'uuid required', cmd);

				data[0] = Widget.$(data[0]); //might be null (such as rm)

				// Bug ZK-2827
				if (!data[0] && cmd != 'invoke' && cmd != 'addChd' /*Bug ZK-2839*/) {
					return;
				}

				if (cmd == 'setAttr') {
					if (!zAu.doAfterProcessWgts) {
						zAu.doAfterProcessWgts = [];
					}
					zAu.doAfterProcessWgts.push(data[0]);
				}

			} else {
				//2. process zAu.cmd0
				fn = zAu.cmd0[cmd];
				if (!fn)
					return zAu.showError('ILLEGAL_RESPONSE', 'Unknown', cmd);
			}
			fn.apply(zAu, data);
			zAu.processPhase = null;
		}
	}

	function ajaxReqResend2() {
		var reqInf = zAu.pendingReqInf;
		if (reqInf) {
			zAu.pendingReqInf = null;
			if (zAu.seqId == reqInf.sid)
				ajaxSendNow(reqInf);
		}
	}
	function _exmsg(e) {
		var msg = e.message || e, m2 = '';
		if (e.name) m2 = ' ' + e.name;
//		if (e.fileName) m2 += " " +e.fileName;
//		if (e.lineNumber) m2 += ":" +e.lineNumber;
//		if (e.stack) m2 += " " +e.stack;
		return msg + (m2 ? ' (' + m2.substring(1) + ')' : m2);
	}

	function ajaxSend(dt, aureq, timeout) {
		//ZK-1523: dt(desktop) could be null, so search the desktop from target's parent.
		//call stack: echo2() -> send()
		if (!dt) {
			//original dt is decided by aureq.target.desktop, so start by it's parent.
			var wgt = aureq.target.parent;
			while (!wgt.desktop) {
				wgt = wgt.parent;
			}
			dt = wgt.desktop;
		}
		////
		zAu.addAuRequest(dt, aureq);

		ajaxSend2(dt, timeout);
			//Note: we don't send immediately (Bug 1593674)
	}
	function ajaxSend2(dt, timeout) {
		if (!timeout) timeout = 0;
		if (dt && timeout >= 0)
			setTimeout(function () {zAu.sendNow(dt);}, timeout);
	}
	function ajaxSendNow(reqInf) {
		var setting = zAu.ajaxSettings,
			req = setting.xhr();
		zAu.sentTime = jq.now(); //used by server-push (cpsp)
		try {
			zk.ausending = true;
			req.onreadystatechange = zAu._onResponseReady;
			req.open('POST', reqInf.uri, true);
			req.setRequestHeader('Content-Type', setting.contentType);
			req.setRequestHeader('ZK-SID', reqInf.sid);
			if (zAu._errCode) {
				req.setRequestHeader('ZK-Error-Report', zAu._errCode);
				zAu._errCode = null;
			}

			var forceAjax = reqInf.forceAjax;
			if (zk.pfmeter) zAu._pfsend(reqInf.dt, req, false, forceAjax);

			zAu.ajaxReq = req;
			zAu.ajaxReqInf = reqInf;
			
			if (!forceAjax && typeof zWs != 'undefined' && zWs.ready) {
				zWs.send(req, reqInf);
				return;
			}

			req.send(reqInf.content);

			if (!reqInf.implicit)
				zk.startProcessing(zk.procDelay, reqInf.sid); //wait a moment to avoid annoying
		} catch (e) {
			//handle error
			try {
				if (typeof req.abort == 'function') req.abort();
			} catch (e2) {
				zk.debugLog(e2.message || e2);
			}

			if (!reqInf.ignorable && !zk.unloading) {
				var msg = _exmsg(e);
				zAu._errCode = '[Send] ' + msg;
				if (zAu.confirmRetry('FAILED_TO_SEND', msg)) {
					ajaxReqResend(reqInf);
					return;
				}
			}
		}
	}
	function doCmdsNow(cmds) {
		var rtags = cmds.rtags || {}, ex;
		try {
			while (cmds && cmds.length) {
				if (zk.mounting) return false;

				var cmd = cmds.shift();
				try {
					doProcess(cmd.cmd, cmd.data);
				} catch (e) {
					zk.mounting = false; //make it able to proceed
					zAu.showError('FAILED_TO_PROCESS', null, cmd.cmd, e);
					if (!ex) ex = e;
				}
			}
			if (zAu.doAfterProcessWgts) {
				zAu.doAfterProcessWgts.forEach(function (wgt) {
					if (wgt.doAfterProcessRerenderArgs) {
						wgt.rerender.apply(wgt, wgt.doAfterProcessRerenderArgs);
						wgt.doAfterProcessRerenderArgs = null;
					}
				});
				zAu.doAfterProcessWgts = null;
			}
		} finally {
		//Bug #2871135, always fire since the client might send back empty
			if (!cmds || !cmds.length) {
				// ZK-3288, If the wpd file of new created widget was never loaded,
				// sometimes onCommandReady and onResponse will be called during the widget mounting phase. (timing issue)
				zk.afterMount(function () {
					// Bug ZK-2516
					zWatch.fire('onCommandReady', null, {timeout: -1, rtags: rtags}); //won't use setTimeout
					zWatch.fire('onResponse', null, {timeout: 0, rtags: rtags}); //use setTimeout
					if (rtags.onClientInfo) {
						setTimeout(zk.endProcessing, 50); // always stop the processing
						delete zk.clientinfo;
					}
				}, -1);
			}
			zk.ausending = false;
			zk.doAfterAuResponse();
		}
		if (ex)
			throw ex;
		return true;
	}
	function _asBodyChild(child) {
		jq(document.body).append(child);
	}

	//misc//
	function fireClientInfo() {
		zAu.cmd0.clientInfo();
	}
	function sendTimeout() {
		zAu.send(new zk.Event(null, 'dummy', null, {ignorable: true, serverAlive: true, rtags: {isDummy: true}, forceAjax: true}));
			//serverAlive: the server shall not ignore it if session timeout
		zk.isTimeout = true; //ZK-3304: already timeout
	}

	//store all widgets into a map
	function _wgt2map(wgt, map) {
		map[wgt.uuid] = wgt;
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			_wgt2map(wgt, map);
	}

	function _beforeAction(wgt, actnm) {
		var act;
		if (wgt._visible && (act = wgt.actions_[actnm])) {
			wgt.z$display = 'none'; //control zk.Widget.domAttrs_
			return act;
		}
	}
	function _afterAction(wgt, act) {
		if (act) {
			delete wgt.z$display;
			act[0].call(wgt, wgt.$n(), act[1]);
			return true;
		}
	}

/** @class zAu
 * @import zk.Widget
 * @import zk.Desktop
 * @import zk.Event
 * @import zk.AuCmd0
 * @import zk.AuCmd1
 * The AU Engine used to send the AU requests to the server and to process
 * the AU responses.
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
	//Used by mount.js to search widget being detached in this AU
	_wgt$: function (uuid) {
		var map = _detached.wgts = _detached.wgts || {}, wgt;
		while (wgt = _detached.shift())
			_wgt2map(wgt, map);
		return map[uuid];
	},
	_onVisibilityChange: function () { //Called by mount.js when page visibility changed
		if (zk.visibilitychange) zAu.cmd0.visibilityChange();
	},
	//Bug ZK-1596: native will be transfer to stub in EE, store the widget for used in mount.js
	_storeStub: function (wgt) {
		if (wgt)
			_detached.push(wgt);
	},
	//Error Handling//
	/** Register a listener that will be called when the Ajax request failed.
	 * The listener shall be
	 * <pre><code>function (req, errCode)</code></pre>
	 *
	 * where req is an instance of {@link  _global_.XMLHttpRequest},
	 * and errCode is the error code.
	 * Furthermore, the listener could return true to ignore the error.
	 * In other words, if true is returned, the error is ignored (the
	 * listeners registered after won't be called either).
	 * <p>Notice that req.status might be 200, since ZK might send the error
	 * back with the ZK-Error header.
	 *
	 * <p>To remove the listener, use {@link #unError}.
	 * @since 5.0.4
	 * @see #unError
	 * @see #confirmRetry
	 */
	onError: function (fn) {
		_onErrs.push(fn);
	},
	/** Unregister a listener for handling errors.
	 * @since 5.0.4
	 * @see #onError
	 */
	unError: function (fn) {
		_onErrs.$remove(fn);
	},

	/** Called to confirm the user whether to retry, when an error occurs.
	 * @param String msgCode the message code
	 * @param String msg2 the additional message. Ignored if not specified or null.
	 * @return boolean whether to retry
	 */
	confirmRetry: function (msgCode, msg2) {
		var msg = msgzk[msgCode];
		return jq.confirm((msg ? msg : msgCode) + '\n' + msgzk.TRY_AGAIN + (msg2 ? '\n\n(' + msg2 + ')' : ''));
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
		zk.error((msg ? msg : msgCode) + '\n' + (msg2 ? msg2 + ': ' : '') + (cmd || '')
			+ (ex ? '\n' + _exmsg(ex) : ''));
	},
	/** Returns the URI for the specified error.
	 * @param int code the error code
	 * @return String the URI.
	 */
	getErrorURI: function (code) {
		return zAu._errURIs['' + code];
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
			zAu._errURIs['' + code] = uri;
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
		return zk.mounting || cmdsQue.length || zAu.ajaxReq || zAu.pendingReqInf;
	},

	/** Sends an AU request and appends it to the end if there is other pending
	 * AU requests.
	 *
	 * @param Event aureq the request. If {@link Event#target} is null,
	 * the request will be sent to each desktop at the client.
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 * 0 is assumed if not specified or negative.
	 * If negative, the request is assumed to be implicit, i.e., no message will
	 * be shown if an error occurs.
	 */
	send: function (aureq, timeout) {
		//ZK-2790: when unload event is triggered, the desktop is destroyed
		//we shouldn't send request back to server
		if (zk.unloading && zk.rmDesktoping) //it's safer to check if both zk.unloading and zk.rmDesktoping are true
			return;
		
		if (timeout < 0)
			aureq.opts = zk.copy(aureq.opts, {defer: true});

		var t = aureq.target;
		if (t) {
			ajaxSend(t.className == 'zk.Desktop' ? t : t.desktop, aureq, timeout);
		} else {
			var dts = zk.Desktop.all;
			for (var dtid in dts)
				ajaxSend(dts[dtid], aureq, timeout);
		}
	},
	/** Sends an AU request by placing in front of any other pending request.
	 * @param Event aureq the request. If {@link Event#target} is null,
	 * the request will be sent to each desktop at the client.
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 * 0 is assumed if not specified or negative.
	 * If negative, the request is assumed to be implicit, i.e., no message will
	 * be shown if an error occurs.
	 */
	sendAhead: function (aureq, timeout) {
		var t = aureq.target;
		if (t) {
			var dt = t.className == 'zk.Desktop' ? t : t.desktop;
			zAu.getAuRequests(dt).unshift(aureq);
			ajaxSend2(dt, timeout);
		} else {
			var dts = zk.Desktop.all;
			for (var dtid in dts) {
				zAu.getAuRequests(dt).unshift(aureq);
				ajaxSend2(dts[dtid], timeout);
			}
			return;
		}
	},

	//remove desktop (used in mount.js and wiget.js)
	_rmDesktop: function (dt, dummy) {
		var url = zk.ajaxURI(null, {desktop: dt, au: true}),
			data = jq.param({dtid: dt.id, cmd_0: dummy ? 'dummy' : 'rmDesktop', opt_0: 'i'}),
			headers = {};
		if (zk.pfmeter) {
			var fakeReq = {
				setRequestHeader: function (name, value) {
					headers[name] = value;
				}
			};
			zAu._pfsend(dt, fakeReq, true, false);
		}
		// ZK-4204
		if (navigator.sendBeacon && window.URLSearchParams) {
			var params = new URLSearchParams(data);
			for (var key in headers) {
				if (headers.hasOwnProperty(key))
					params.append(key, headers[key]);
			}
			navigator.sendBeacon(url, zk.chrome // https://crbug.com/747787
				? new Blob([params.toString()], {type: 'application/x-www-form-urlencoded'})
				: params
			);
		} else {
			this._rmDesktopAjax(url, data, headers);
		}
		// B65-ZK-2210: clean up portlet2 data when desktop removed.
		if (!dummy && zk.portlet2Data && zk.portlet2Data[dt.id]) {
			delete zk.portlet2Data[dt.id];
		}
	},
	_rmDesktopAjax: function (url, data, headers) {
		jq.ajax(zk.$default({
			url: url,
			data: data,
			beforeSend: function (xhr) {
				for (var key in headers) {
					if (headers.hasOwnProperty(key))
						xhr.setRequestHeader(key, headers[key]);
				}
			},
			//2011/04/22 feature 3291332
			//Use sync request for chrome, safari and firefox (4 and later).
			//Note: when pressing F5, the request's URL still arrives before this even async:false
			async: !!zk.ie // (!!) coerce to boolean, undefined will be wrong for safari and chrome.
			// conservative, though it shall be (!zk.safari || zk.ff >= 4)
		}, zAu.ajaxSettings), null, true/*fixed IE memory issue for jQuery 1.6.x*/);
	},

	////Ajax////
	/** Processes the AU response sent from the server.
	 * <p>Don't call it directly at the client.
	 * @param String cmd the command, such as echo
	 * @param String data the data in a JSON string.
	 */
	process: function (cmd, data) {
		doProcess(cmd, data ? jq.evalJSON(data) : []);
	},
	/** Returns whether to ignore the ESC keystroke.
	 * It returns true if ZK Client Engine is sending an AU request
	 * @return Object
	 */
	shallIgnoreESC: function () {
		return zAu.ajaxReq;
	},
	/** Process the specified commands.
	 * @param String dtid the desktop's ID
	 * @param Array rs a list of responses
	 * @since 5.0.5
	 */
	doCmds: function (dtid, rs) {
		var cmds = [];
		cmds.dt = zk.Desktop.$(dtid);
		pushCmds(cmds, rs);
		zAu._doCmds();
	},
	_doCmds: function (sid) { //called by mount.js, too
		for (var fn; fn = doCmdFns.shift();)
			fn();

		var ex, j = 0, rid = responseId;
		for (; j < cmdsQue.length; ++j) {
			if (zk.mounting) return; //wait mount.js mtAU to call

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
						return; //wait mount.js mtAU to call
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
					zAu._doCmds(sid);
				}
			}, 3600);
		} else
			checkProgressing(sid);

		if (ex) throw ex;
	},

	/** Called before sending an AU request.
	 * <p>Default: append {@link zk.Widget#autag} to <code>uri</code>.
	 * <p>It is designed to be overriden by an application to record
	 * what AU requests have been sent. For example, to work with Google Analytics,
	 * you can add the following code:
	 * <pre><code>
&lt;script defer="true">&lt;![CDATA[
var pageTracker = _gat._getTracker("UA-123456");
pageTracker._setDomainName("zkoss.org");
pageTracker._initData();
pageTracker._trackPageview();

var auBfSend = zAu.beforeSend;
zAu.beforeSend = function (uri, req, dt) {
 try {
  var target = req.target;
  if (target.id) {
   var data = req.data||{},
       value = data.items &amp;&amp; data.items[0]?data.items[0].id:data.value;
   pageTracker._trackPageview((target.desktop?target.desktop.requestPath:"") + "/" + target.id + "/" + req.name + (value?"/"+value:""));
  }
 } catch (e) {
 }
 return auBfSend(uri, req, dt);
};
]]>&lt;/script>
	 *</code></pre>
	 *
	 * @param String uri the AU's request URI (such as /zkau)
	 * @param Event aureq the AU request
	 * @param Desktop dt the desktop
	 * @return String the AU's request URI.
	 * @since 5.0.2
	 */
	beforeSend: function (uri, aureq/*, dt*/) {
		var target, tag;
		if ((target = aureq.target) && (tag = target.autag)) {
			tag = '/' + encodeURIComponent(tag);
			if (uri.indexOf('/_/') < 0) {
				var v = target.desktop;
				if ((v = v ? v.requestPath : '') && v.charAt(0) != '/')
					v = '/' + v; //just in case
				tag = '/_' + v + tag;
			}

			var j = uri.lastIndexOf(';');
			if (j >= 0) uri = uri.substring(0, j) + tag + uri.substring(j);
			else uri += tag;
		}
		return uri;
	},
	/** Returns the content to send to the server.
	 * By default, it is encoded into several parameters and the data
	 * parameters (data_*) is encoded in JSON.
	 * <p>If you prefer to encode it into another format, you could override
	 * this method, and also implement a Java interface called
	 * <a href="http://www.zkoss.org/javadoc/latest/zk/org/zkoss/zk/au/AuDecoder.html">org.zkoss.zk.au.AuDecoder</a>\
	 * to decode the format at the server.
	 * <p>If you prefer to encode it into URI, you could override
	 * {@link #beforeSend}.
	 * @param int j the order of the AU request. ZK sends a batch of AU
	 * request at once and this argument indicates the order an AU request is
	 * (starting from 0).
	 * @param Event aureq the AU request
	 * @param Desktop dt the desktop
	 * @return String the content of the AU request.
	 * @since 5.0.4
	 */
	encode: function (j, aureq, dt) {
		var target = aureq.target,
			opts = aureq.opts || {},
			portlet2Namespace = '';
			
		// B65-ZK-2210: add porlet namespace
		if (zk.portlet2Data && zk.portlet2Data[dt.id]) {
			portlet2Namespace = zk.portlet2Data[dt.id].namespace || '';
		}
		var content = j ? '' : portlet2Namespace + 'dtid=' + dt.id;

		content += '&' + portlet2Namespace + 'cmd_' + j + '=' + aureq.name;
		if ((opts.implicit || opts.ignorable) && !(opts.serverAlive))
			content += '&' + portlet2Namespace + 'opt_' + j + '=i';
			//thus, the server will ignore it if session timeout

		if (target && target.className != 'zk.Desktop')
			content += '&' + portlet2Namespace + 'uuid_' + j + '=' + target.uuid;

		var data = aureq.data, dtype = typeof data;
		if (dtype == 'string' || dtype == 'number' || dtype == 'boolean' || jq.isArray(data))
			data = {'': data};
		if (data)
			content += '&' + portlet2Namespace + 'data_' + j + '=' + encodeURIComponent(zAu.toJSON(target, data));
		return content;
	},

	/** Enforces all pending AU requests of the specified desktop to send immediately
	 * @param Desktop dt
	 * @return boolean whether it is sent successfully. If it has to wait
	 * for other condition, this method returns false.
	 */
	sendNow: function (dt) {
		if (zAu.disabledRequest) {
			return false;
		}
		var es = zAu.getAuRequests(dt);
		if (es.length == 0)
			return false;

		if (zk.mounting) {
			zk.afterMount(function () {zAu.sendNow(dt);});
			return true; //wait
		}

		if (zAu.ajaxReq || zAu.pendingReqInf) { //send ajax request one by one
			sendPending = true;
			return true;
		}

		//decide implicit (and uri)
		var implicit, uri;
		for (var j = 0, el = es.length; j < el; ++j) {
			var aureq = es[j],
				opts = aureq.opts || {};
			if (opts.uri != uri) {
				if (j) break;
				uri = opts.uri;
			}

			//ignorable and defer implies implicit
			if (!(implicit = opts.ignorable || opts.implicit || opts.defer))
				break;
		}

		//notify watches (fckez uses it to ensure its value is sent back correctly
		try {
			zWatch.fire('onSend', null, implicit);
		} catch (e) {
			zAu.showError('FAILED_TO_SEND', null, null, e);
		}

		//decide ignorable
		var ignorable = true;
		for (var j = 0, el = es.length; j < el; ++j) {
			var aureq = es[j],
				opts = aureq.opts || {};
			if ((opts.uri != uri)
			|| !(ignorable = ignorable && opts.ignorable)) //all ignorable
				break;
		}
		var forceAjax = false;
		for (var j = 0, el = es.length; j < el; ++j) {
			var aureq = es[j],
				opts = aureq.opts || {};
			if (opts.forceAjax) {
				forceAjax = true;
				break;
			}
		}
		//Consider XML (Pros: ?, Cons: larger packet)
		var content, rtags = {},
			requri = uri || zk.ajaxURI(null, {desktop: dt,au: true}),
			ws = typeof zWs != 'undefined' && zWs.ready;
		if (!forceAjax && ws) {
			content = {};
		} else {
			content = '';
		}
		for (var j = 0, el = es.length; el; ++j, --el) {
			var aureq = es.shift();
			if ((aureq.opts || {}).uri != uri) {
				es.unshift(aureq);
				break;
			}

			requri = zAu.beforeSend(requri, aureq, dt);
			if (!forceAjax && ws) {
				zk.copy(content, zWs.encode(j, aureq, dt));
			} else {
				content += zAu.encode(j, aureq, dt);
			}
			zk.copy(rtags, (aureq.opts || {}).rtags);
		}
		// B65-ZK-2210: get resourceURL by desktop id
		if (zk.portlet2Data && zk.portlet2Data[dt.id]) {
			requri = zk.portlet2Data[dt.id].resourceURL;
		}
		
		//if (zk.portlet2AjaxURI)
			//requri = zk.portlet2AjaxURI;
		if (content)
			ajaxSendNow({
				sid: zAu.seqId, uri: requri, dt: dt, content: content,
				implicit: implicit,
				ignorable: ignorable, tmout: 0, rtags: rtags, forceAjax: forceAjax
			});
		return true;
	},
	/** Add the AU request to the ajax queue.
	 * @param Desktop dt
	 * @param Event aureq the request.
	 * @since 7.0.3
	 */
	addAuRequest: function (dt, aureq) {
		dt._aureqs.push(aureq);
	},
	/** Returns all pending AU requests.
	 * @param Desktop dt
	 * @return Array an array of {@link Event}
	 * @since 7.0.3
	 */
	getAuRequests: function (dt) {
		return dt._aureqs;
	},
	/** A map of Ajax default setting used to send the AU requests.
	 * @type Map
	 */
	ajaxSettings: zk.$default({
		global: false,
		//cache: false, //no need to turn off cache since server sends NO-CACHE
		contentType: 'application/x-www-form-urlencoded;charset=UTF-8'
	}, jq.ajaxSettings),

	// Adds performance request IDs that have been processed completely.
	// Called by moun.js, too
	_pfrecv: function (dt, pfIds) {
		zAu.pfAddIds(dt, '_pfRecvIds', pfIds);
	},
	// Adds performance request IDs that have been processed completely.
	// Called by moun.js, too
	_pfdone: function (dt, pfIds) {
		zAu.pfAddIds(dt, '_pfDoneIds', pfIds);
	},
	// Sets performance rquest IDs to the request's header
	// Called by moun.js, too
	_pfsend: function (dt, req, completeOnly, forceAjax) {
		var ws = !forceAjax && typeof zWs != 'undefined' && zWs.ready;
		if (!completeOnly) {
			var dtt = dt.id + '-' + pfIndex++ + '=' + Math.round(jq.now());
			req.setRequestHeader('ZK-Client-Start', dtt);
			if (ws) {
				zWs.setRequestHeaders('ZK-Client-Start', dtt);
			}
		}

		var ids;
		if (ids = dt._pfRecvIds) {
			req.setRequestHeader('ZK-Client-Receive', ids);
			if (ws) {
				zWs.setRequestHeaders('ZK-Client-Receive', ids);
			}
			dt._pfRecvIds = null;
		}
		if (ids = dt._pfDoneIds) {
			req.setRequestHeader('ZK-Client-Complete', ids);
			if (ws) {
				zWs.setRequestHeaders('ZK-Client-Complete', ids);
			}
			dt._pfDoneIds = null;
		}
	},

	/** Creates widgets based on an array of JavaScritp codes generated by
	 * Component.redraw() at the server.
	 * <p>This method is usually used with Java's ComponentsCtrl.redraw, and
	 * {@link Widget#replaceCavedChildren_}.
	 * <p>Notice that, since the creation of widgets might cause some packages
	 * to be loaded, the callback function, fn, might be called after this
	 * method is returned
	 * @param Array codes an array of JavaScript objects generated at the server.
	 * For example, <code>smartUpdate("foo", ComponentsCtrl.redraw(getChildren());</code>
	 * @param Function fn the callback function. When the widgets are created.
	 * <code>fn</code> is called with an array of {@link Widget}. In other words,
	 * the callback's signature is as follows:<br/>
	 * <code>void callback(zk.Widget[] wgts);</code>
	 * @param Function filter the filter to avoid the use of widgets being replaced.
	 * Ignored if null
	 * @since 5.0.2
	 */
	createWidgets: function (codes, fn, filter) {
		//bug #3005632: Listbox fails to replace with empty model if in ROD mode
		var wgts = [], len = codes.length;
		if (len > 0) {
			for (var j = 0; j < len; ++j)
				zkx_(codes[j], function (newwgt) {
					wgts.push(newwgt);
					if (wgts.length == len)
						fn(wgts);
				}, filter);
		} else
			fn(wgts);
	},

	/* (not jsdoc)
	 * Shows or clear an error message. It is overriden by zul.wpd.
	 * <p>wrongValue_(wgt, msg): show an error message
	 * <p>wrongValue_(wgt, false): clear an error message
	 */
	wrongValue_: function (wgt, msg) {
		if (msg !== false)
			jq.alert(msg);
	},
	// Called when the response is received from zAu.ajaxReq.
	_onResponseReady: function () {
		var req = zAu.ajaxReq, reqInf = zAu.ajaxReqInf, sid;
		try {
			if (req && req.readyState == 4) {
				zAu.ajaxReq = zAu.ajaxReqInf = null;
				if (zk.pfmeter) zAu._pfrecv(reqInf.dt, zAu.pfGetIds(req));

				sid = req.getResponseHeader('ZK-SID');

				var rstatus;
				if ((rstatus = req.status) == 200) { //correct
					if (zAu._respSuccess(req, reqInf, sid)) return;
				} else if ((!sid || sid == zAu.seqId) //ignore only if out-of-seq (note: 467 w/o sid)
				&& !zAu.onResponseError(req, zAu._errCode = rstatus)) {
					if (zAu._respFailure(req, reqInf, rstatus)) return;
				}
			}
		} catch (e) {
			if (zAu._respException(req, reqInf, e)) return;
		}

		zAu.afterResponse(sid);
	},
	_respSuccess: function (req, reqInf, sid) {
		if (sid && sid != zAu.seqId) {
			zAu._errCode = 'ZK-SID ' + (sid ? 'mismatch' : 'required');
			zAu.afterResponse(); //continue the pending request if any
			return true;
		} //if sid null, always process (usually for error msg)

		var v;
		if ((v = req.getResponseHeader('ZK-Error'))
		&& !zAu.onResponseError(req, v = zk.parseInt(v) || v)
		&& (v == 5501 || v == 5502) //Handle only ZK's SC_OUT_OF_SEQUENCE or SC_ACTIVATION_TIMEOUT
		&& zAu.confirmRetry('FAILED_TO_RESPONSE',
				v == 5501 ? 'Request out of sequence' : 'Activation timeout')) {
			zAu.ajaxReqResend(reqInf);
			return true;
		}
		if (v != 410 //not timeout (SC_GONE)
		&& !(reqInf.rtags && reqInf.rtags.isDummy) //ZK-3304: dummy request shouldn't reset timeout
		&& (!reqInf.rtags || !reqInf.rtags.onTimer || zk.timerAlive)) // Bug ZK-2720 only timer-keep-alive should reset the timeout
			zAu._resetTimeout();

		if (zAu.pushReqCmds(reqInf, req)) { //valid response
			//advance SID to avoid receive the same response twice
			if (sid && ++zAu.seqId > 9999) zAu.seqId = 1;
			zAu.ajaxReqTries = 0;
			zAu.pendingReqInf = null;
		}
	},
	_respFailure: function (req, reqInf, rstatus) {
		var eru = zAu._errURIs['' + rstatus];
		if (typeof eru == 'string') {
			zUtl.go(eru);
			return true;
		}

		if (typeof zAu.ajaxErrorHandler == 'function') {
			zAu.ajaxReqTries = zAu.ajaxErrorHandler(req, rstatus, req.statusText, zAu.ajaxReqTries);
			if (zAu.ajaxReqTries > 0) {
				zAu.ajaxReqTries--;
				zAu.ajaxReqResend(reqInf, zk.resendTimeout);
				return true;
			}
		} else {
		//handle MSIE's buggy HTTP status codes
		//http://msdn2.microsoft.com/en-us/library/aa385465(VS.85).aspx
			switch (rstatus) { //auto-retry for certain case
			default:
				if (!zAu.ajaxReqTries) break;
				//fall thru
			case 12002: //server timeout
			case 12030: //http://danweber.blogspot.com/2007/04/ie6-and-error-code-12030.html
			case 12031:
			case 12152: // Connection closed by server.
			case 12159:
			case 13030:
			case 503: //service unavailable
				if (!zAu.ajaxReqTries) zAu.ajaxReqTries = 3; //two more try
				if (--zAu.ajaxReqTries) {
					zAu.ajaxReqResend(reqInf, zk.resendTimeout);
					return true;
				}
			}
			if (!reqInf.ignorable && !zk.unloading) {
				var msg = req.statusText;
				if (zAu.confirmRetry('FAILED_TO_RESPONSE', rstatus + (msg ? ': ' + msg : ''))) {
					zAu.ajaxReqTries = 2; //one more try
					zAu.ajaxReqResend(reqInf);
					return true;
				}
			}
		}
	},
	_respException: function (req, reqInf, e) {
		if (!window.zAu)
			return true; //the doc has been unloaded

		zAu.ajaxReq = zAu.ajaxReqInf = null;
		try {
			if (req && typeof req.abort == 'function') req.abort();
		} catch (e2) {
			zk.debugLog(e2.message || e2);
		}

		//NOTE: if connection is off and req.status is accessed,
		//Mozilla throws exception while IE returns a value
		if (reqInf && !reqInf.ignorable && !zk.unloading) {
			var msg = _exmsg(e);
			zAu._errCode = '[Receive] ' + msg;
			//if (e.fileName) _errCode += ", "+e.fileName;
			//if (e.lineNumber) _errCode += ", "+e.lineNumber;
			if (zAu.confirmRetry('FAILED_TO_RESPONSE', (msg && msg.indexOf('NOT_AVAILABLE') < 0 ? msg : ''))) {
				zAu.ajaxReqResend(reqInf);
				return true;
			}
		}
	},

	/** The AU command handler that handles commands not related to widgets.
	 * @type AuCmd0
	 */
	//cmd0: null, //jsdoc
	/** The AU command handler that handles commands releated to widgets.
	 * @type AuCmd1
	 */
	//cmd1: null, //jsdoc

	pushReqCmds: function (reqInf, req) {
		var dt = reqInf.dt,
			rt = req.responseText;
		if (!rt) {
			if (zk.pfmeter) zAu._pfdone(dt, zAu.pfGetIds(req));
			return false; //invalid
		}

		var cmds = [];
		cmds.rtags = reqInf.rtags;
		if (zk.pfmeter) {
			cmds.dt = dt;
			cmds.pfIds = zAu.pfGetIds(req);
		}

		try {
			rt = jq.evalJSON(rt);
		} catch (e) {
			if (e.name == 'SyntaxError') { //ZK-4199: handle json parse error
				zAu.showError('FAILED_TO_PARSE_RESPONSE', e.message);
				zk.debugLog(e.message + ', response text:\n' + req.responseText);
				return false;
			}
			throw e;
		}
		var	rid = rt.rid;
		if (rid) {
			rid = parseInt(rid); //response ID
			if (!isNaN(rid)) cmds.rid = rid;
		}

		pushCmds(cmds, rt.rs);
		return true;
	},

	/* internal use only */
	afterResponse: function (sid) {
		zAu._doCmds(sid); //invokes checkProgressing

		//handle pending ajax send
		if (sendPending && !zAu.ajaxReq && !zAu.pendingReqInf) {
			sendPending = false;
			var dts = zk.Desktop.all;
			for (var dtid in dts)
				ajaxSend2(dts[dtid], 0);
		}
	},
	/* @param zk.Widget target
	 */
	toJSON: function (target, data) {
		if (!jq.isArray(data)) {
			if (data.pageX != null && data.x == null) {
				var ofs = target && target.desktop ? // B50-3336745: target may have been detached
						target.fromPageCoord(data.pageX, data.pageY) :
						[data.pageX, data.pageY];
				data.x = ofs[0];
				data.y = ofs[1];
			}

			var v;
			for (var n in data)
				if ((v = data[n]) instanceof DateImpl)
					data[n] = '$z!t#d:' + jq.d2j(v);
		}
		return jq.toJSON(data);
	},
	/* internal use only */
	ajaxReq: null,
	/* internal use only */
	ajaxReqInf: null,
	/* internal use only */
	ajaxReqTries: null,
	/* internal use only */
	seqId: (jq.now() % 9999) + 1,
	/* internal use only */
	pendingReqInf: null,
	_errCode: null,
	_errURIs: {},
	//Perfomance Meter//
	// Returns request IDs sent from the server separated by space.
	pfGetIds: function (req) {
		return req.getResponseHeader('ZK-Client-Complete');
	},
	pfAddIds: function (dt, prop, pfIds) {
		if (pfIds && (pfIds = pfIds.trim())) {
			var s = pfIds + '=' + Math.round(jq.now());
			if (dt[prop]) dt[prop] += ',' + s;
			else dt[prop] = s;
		}
	},
	ajaxReqResend: function (reqInf, timeout) {
		if (zAu.seqId == reqInf.sid) {//skip if the response was recived
			zAu.pendingReqInf = reqInf; //store as a pending request info
			setTimeout(ajaxReqResend2, timeout ? timeout : 0);
		}
	},
	onResponseError: function (req, errCode) {
		//$clone first since it might add or remove onError
		for (var errs = _onErrs.$clone(), fn; fn = errs.shift();)
			if (fn(req, errCode))
				return true; //ignored
	}
};

/** @partial zAu
 */
//@{
    /** Implements this function to be called if the request fails.
     * The function receives four arguments: The XHR (XMLHttpRequest) object,
     * a number describing the status of the request, a string describing the text
     * of the status, and a number describing the retry value to re-send.
     *
     * <p>For example,
<pre><code>
zAu.ajaxErrorHandler = function (req, status, statusText, ajaxReqTries) {
    if (ajaxReqTries == null)
        ajaxReqTries = 3; // retry 3 times
        
    // reset the resendTimeout, for more detail, please refer to
    // http://books.zkoss.org/wiki/ZK_Configuration_Reference/zk.xml/The_client-config_Element/The_auto-resend-timeout_Element
    zk.resendTimeout = 2000;//wait 2 seconds to resend.
    
    if (!zAu.confirmRetry("FAILED_TO_RESPONSE", status+(statusText?": "+statusText:"")))
        return 0; // no retry;
    return ajaxReqTries;
}
</code></pre>
     * @param Object req the object of XMLHttpRequest
     * @param int status the status of the request
     * @param String statusText the text of the status from the request
     * @param int ajaxReqTries the retry value for re-sending the request, if undefined
     *      means the function is invoked first time.
     * @since 6.5.2
     */
    //ajaxErrorHandler: function () {}
//@};

//Commands//
/** @class zk.AuCmd0
 * The AU command handler for processes commands not related to widgets,
 * sent from the server.
 * @see zAu#cmd0
 */
zAu.cmd0 = /*prototype*/ { //no uuid at all
	/** Sets a bookmark
	 * @param String bk the bookmark
	 * @param boolean replace if true, it will replace the bookmark without creating
	 * 		a new one history.
	 */
	bookmark: function (bk, replace) {
		zk.bmk.bookmark(bk, replace);
	},
	/** Shows an error to indicate the desktop is timeout.
	 * @param String dtid the desktop UUID
	 * @param String msg the error message
	 */
	obsolete: function (dtid, msg) {
		if (msg.startsWith('script:'))
			return $eval(msg.substring(7));

		// ZK-2397: prevent from showing reload dialog again while browser is reloading
		if (zk._isReloadingInObsolete)
			return;
		
		var v = zk.Desktop.$(dtid);
		if (v && (v = v.requestPath))
			msg = msg.replace(dtid, v + ' (' + dtid + ')');

		zAu.disabledRequest = true;
		
		jq.alert(msg, {
			icon: 'ERROR',
			button: {
				Reload: function () {
					zk._isReloadingInObsolete = true;
					location.reload();
				},
				Cancel: true
			}
		});
	},
	/** Shows an alert to indicate some error occurs.
	 * For widget's error message, use {@link #wrongValue} instead.
	 * @param String msg the error message
	 */
	alert: function (msg, title, icon, disabledAuRequest) {
		if (disabledAuRequest)
			zAu.disabledRequest = true;
		jq.alert(msg, {icon: icon || 'ERROR', title: title});
	},
	/** Redirects to the specified URL.
	 * @param String url the URL to redirect to
	 * @param String target [optional] the window name to show the content
	 * of the URL. If omitted, it will replace the current content.
	 */
	redirect: function (url, target) {
		try {
			zUtl.go(url, {target: target});

			// '#' for bookmark change only, Bug ZK-2874
			var idx;
			if (url && !url.startsWith('/') && (idx = url.indexOf('#')) >= 0) {
				var uri = url.substring(0, idx),
					hash = url.substring(idx + 1),
					locHash = window.location.hash,
					locUrl = window.location.href;
				if (locHash) {
					locUrl = locUrl.substring(0, locUrl.length - locHash.length); // excluding '#'
				}
				if (locUrl.endsWith(uri))
					return; // not to disable request for Bug ZK-2844
			}

			// Bug ZK-2844
			if (!target)
				zAu.disabledRequest = true; // Bug ZK-2616
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
	/** Logs the message.
	 * @param String msg the message to log
	 * @since 5.0.8
	 */
	log: zk.log,
	/** Executes the JavaScript.
	 * @param String script the JavaScript code snippet to execute
	 */
	script: function (script) {
		jq.globalEval(script);
	},
	/** Asks the client to echo back an AU request, such that
	 * the server can return other commands.
	 * It is used to give the end user a quick response before doing
	 * a long operation.
	 * @param String dtid the desktop ID ({@link zk.Desktop}).
	 * @see zk.AuCmd1#echo2
	 * @see #echoGx
	 */
	echo: function (dtid) {
		var dt = zk.Desktop.$(dtid),
			aureqs = zAu.getAuRequests(dt);
		// Bug ZK-2741
		for (var i = 0, j = aureqs.length; i < j; i++) {
			var aureq0 = aureqs[i];
			if ((!aureq0.target || aureq0.target.$instanceof(zk.Desktop)) && aureq0.name == 'dummy') {
				return; //no need to send more
			}
		}
		zAu.send(new zk.Event(dt, 'dummy', null, {ignorable: true, rtags: {isDummy: true}}));
	},
	/** Ask the client to echo back globally.
	 * <p>Unlike {@link #echo}, it will search all browser windows for
	 * <p>Note: this feature requires ZK EE
	 * the given desktop IDs.
	 * @param String evtnm the event name
	 * @param String data any string-typed data
	 * @param String... any number of desktop IDs.
	 * @since 5.0.4
	 */
	//echoGx: function () {}

	/** Asks the client information.
	 * The client will reply the information in the <code>onClientInfo</code> response.
	 * @param String dtid the desktop ID ({@link zk.Desktop}).
	 */
	clientInfo: function (dtid) {
		zAu._cInfoReg = true;
		var orient = '',
			dpr = 1;
		
		if (zk.mobile) {
			//change default portrait definition because landscape is the default orientation for this device/browser.
			if ((_initLandscape && _initDefault) || (!_initLandscape && !_initDefault))
				_portrait = {'-90': true, '90': true};
			
			orient = _portrait[window.orientation] ? 'portrait' : 'landscape';
		} else {
			orient = jq.innerWidth() > jq.innerHeight() ? 'landscape' : 'portrait';
		}
		
		if (window.devicePixelRatio)
			dpr = window.devicePixelRatio;

		var clientInfo = [new Date().getTimezoneOffset(),
			screen.width, screen.height, screen.colorDepth,
			jq.innerWidth(), jq.innerHeight(), jq.innerX(), jq.innerY(), dpr.toFixed(1), orient];

		// ZK-3181: only send when value changed
		var oldClientInfo = zAu._clientInfo;
		if (oldClientInfo) {
			var same = oldClientInfo.every(function (el, index) {
				return el === clientInfo[index];
			});
			if (same) return;
		}

		zAu._clientInfo = clientInfo;
		zAu.send(new zk.Event(zk.Desktop.$(dtid), 'onClientInfo',
			zAu._clientInfo,
			{implicit: true, rtags: {onClientInfo: 1}}));
	},
	visibilityChange: function (dtid) {
		var hidden = !!(document.hidden || document[zk.vendor_ + 'Hidden']),
			visibilityState = document.visibilityState || document[zk.vendor_ + 'VisibilityState'];
		
		zAu.send(new zk.Event(zk.Desktop.$(dtid), 'onVisibilityChange',
			{hidden: hidden, visibilityState: visibilityState}, {implicit: true, ignorable: true}));
	},
	/** Asks the client to download the resource at the specified URL.
	 * @param String url the URL to download from
	 */
	download: function (url) {
		if (url) {
			var ifr = jq('#zk_download')[0],
				ie = zk.ie,
				sbu = zk.skipBfUnload;
			if (ie) zk.skipBfUnload = true;

			if (!ifr) {
				ifr = document.createElement('iframe');
				ifr.id = ifr.name = 'zk_download';
				ifr.style.display = 'none';
				ifr.style.width = ifr.style.height = ifr.style.border = ifr.frameBorder = '0';
				document.body.appendChild(ifr);
			}

			if (ie < 11) { // Since IE11 dropped onreadystatechange support: https://stackoverflow.com/a/26835889
				// Use onreadystatechange to listen if iframe is loaded
				ifr.onreadystatechange = function () {
					var state = ifr.contentWindow.document.readyState;
					if (state === 'interactive')
						setTimeout(function () { zk.skipBfUnload = sbu; }, 0);
				};
			}
			ifr.src = url; //It is OK to reuse the same iframe

			// Workaround for IE11: wait a second (not perfect) for iframe loading
			if (ie === 11)
				setTimeout(function () { zk.skipBfUnload = sbu; }, 1000);
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
	/** Shows a notification popup.
	 * @param String msg message to show
	 * @param String type the notification type (warning, info, error)
	 * @param String pid uuid of the page to which it belongs
	 * @param String ref uuid of a reference component
	 * @param String pos the position of notification
	 * @param Offset off the offset of x and y
	 * @param int dur the duration of notification
	 * @param boolean closable the close button of notification
	 */
	showNotification: function (msg, type, pid, ref, pos, off, dur, closable) {
		var notif = (zul && zul.wgt) ? zul.wgt.Notification : null; // in zul
		if (notif) {
			var opts = {ref: ref, pos: pos, off: off, dur: dur, type: type, closable: closable};
			//ZK-2687, show notif after zAu.cmd0.scrollIntoView
			zk.delayFunction(ref ? ref.uuid : 'nouuid', function () {
				notif.show(msg, pid, opts);
			});
		} else {
			// TODO: provide a hook to customize
			jq.alert(msg); // fall back to alert when zul is not available
		}
	},
	/** Shows the busy message covering the specified widget.
	 * @param String uuid the component's UUID
	 * @param String msg the message.
	 */
	/** Shows the busy message covering the whole browser window.
	 * @param String msg the message.
	 */
	showBusy: function (uuid, msg) {
		if (arguments.length == 1) {
			msg = uuid;
			uuid = null;
		}

		zAu.cmd0.clearBusy(uuid);

		var w = uuid ? Widget.$(uuid) : null;
		if (!uuid) {
			zk._prevFocus = zk.currentFocus;
			zUtl.progressbox('zk_showBusy', msg || msgzk.PLEASE_WAIT, true, null, {busy: true});
		} else if (w) {
			zk.delayFunction(uuid, function () {
				w.effects_.showBusy = new zk.eff.Mask({
					id: w.uuid + '-shby',
					anchor: w.$n(),
					message: msg
				});
			});
		}
	},
	/** Removes the busy message covering the specified widget.
	 * @param String uuid the component's UUID
	 */
	/** Removes the busy message covering the whole browser.
	 */
	clearBusy: function (uuid) {
		if (uuid) {
			zk.delayFunction(uuid, function () {
				var w = Widget.$(uuid),
					efs = w ? w.effects_ : null;
				if (efs && efs.showBusy) {
					efs.showBusy.destroy();
					delete efs.showBusy;
				}
			});
		} else {
			zUtl.destroyProgressbox('zk_showBusy', {busy: true}); //since user might want to show diff msg
			if (zk._prevFocus) {
				zk.currentFocus = zk._prevFocus;
				zk._prevFocus = null;
				var wgt = zk.currentFocus;
				try {
					zk._focusByClearBusy = true;
					wgt.focus();
				} finally {
					zk._focusByClearBusy = false;
				}
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
			var wgt = Widget.$(arguments[i]);
			if (wgt) {
				var toClearErrMsg = function (w) {
					return function () {
						if (w.clearErrorMessage) w.clearErrorMessage();
						else zAu.wrongValue_(w, false);
					};
				};
				zk.delayFunction(wgt.uuid, toClearErrMsg(wgt));
			}
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
		var args = arguments;
		for (var i = 0, len = args.length - 1; i < len; i += 2) {
			var uuid = args[i], msg = args[i + 1],
				wgt = Widget.$(uuid);
			if (wgt) {
				//ZK-2687: create a closure to record the current wgt
				var toSetErrMsg = function (w, m) {
					return function () {
						zk.afterAnimate(function () {
							if (w.setErrorMessage) w.setErrorMessage(m);
							else zAu.wrongValue_(w, m);
						}, -1);
					};
				};
				zk.delayFunction(uuid, toSetErrMsg(wgt, msg));
			} else if (!uuid) //keep silent if component (of uuid) not exist (being detaced)
				jq.alert(msg);
		}
		// for a bug fixed of B60-ZK-1208, we need to delay the func for this test case, B36-2935398.zul
		// has been removed since 7.0.6
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
		setTimeout(function () {
			var n = Widget.$(id);
			if (n && n.submit)
				n.submit();
			else
				zk(id).submit();
		}, 50);
	},
	/** Scrolls the widget or an DOM element into the view
	 * @param String id the UUID of the widget, or the ID of the DOM element.
	 */
	scrollIntoView: function (id) {
		if (!id) return;
		var w = Widget.$(id);
		if (w) {
			zk.delayFunction(w.uuid, function () {
				w.scrollIntoView();
			});
		} else {
			var zkjq = zk(id);
			if (zkjq.$()) {
				zk.delayFunction(zkjq.$().uuid, function () {
					zkjq.scrollIntoView();
				});
			}
		}
	},
	/**
	 * Loads a JavaScript file and execute it.
	 * @param String url the JavaScript file path
	 * @param String callback the function to execute after the JavaScript file loaded.
	 * @param boolean once true means the JavaScript file will be cached.
	 * @since 8.0.0
	 */
	loadScript: function (url, callback, once) {
		jq.ajax({
			dataType: 'script',
			cache: once,
			url: url
		}).done(function () {
			if (jq.isFunction(callback)) {
				callback();
			} else
				jq.globalEval(callback);
		});
	},
	/** Loads a CSS file.
	 * @param String href the URL of the CSS file.
	 * @param String id the identifier. Ignored if not specified.
	 * @param String media the media attribute. Ignored if not specified.
	 * @since 8.0.0
	 */
	loadCSS: zk.loadCSS,
	/** Pushes or replaces a history state.
	 * @param boolean replace if true, it will replace the current history without creating a new one.
	 * @param Object state a state object.
	 * @param String title a title for the state. May be ignored by some browsers.
	 * @param String url the new history entry's URL. Ignored if not specified.
	 * @since 8.5.0
	 */
	historyState: function (replace, state, title, url) {
		if (replace && window.history.replaceState)
			window.history.replaceState(state, title, url);
		if (!replace && window.history.pushState)
			window.history.pushState(state, title, url);
	},
	/** Ask the client to sync all the errorboxes and its reference widget position on the desktop.
	 * @since 8.5.2
	 */
	syncAllErrorbox: function () {
		jq('.z-errorbox').toArray().forEach(function (ebox) {
			ebox = jq(ebox).zk.$();
			ebox.reposition();
			ebox._fixarrow();
		});
	}
};
/** @class zk.AuCmd1
 * The AU command handler for processes commands related to widgets,
 * sent from the server.
 * @see zAu#cmd1
 */
zAu.cmd1 = /*prototype*/ {
	/** Sets the attribute of a widget.
	 * @param zk.Widget wgt the widget
	 * @param String name the name of the attribute
	 * @param Object value the value of the attribute.
	 */
	setAttr: function (wgt, nm, val) {
		if (wgt) { // server push may cause wgt is null in some case - zksandbox/#a1
			if (nm == 'z$al') { //afterLoad
				zk.afterLoad(function () {
					for (nm in val)
						wgt.set(nm, val[nm](), true); //must be func
				});
			} else {
				// Bug ZK-2281, make sure the wgt is not in the rerendering queue.
				wgt.rerenderNow_(null);
				wgt.set(nm, val, true); //3rd arg: fromServer
			}
		}
	},
	/** Replaces the widget with the widget(s) generated by evaluating the specified JavaScript code snippet.
	 * @param zk.Widget wgt the old widget to be replaced
	 * @param String code the JavaScript code snippet to generate new widget(s).
	 */
	outer: function (wgt, code) {
		zkx_(code, function (newwgt) {
			var act = _beforeAction(newwgt, 'invalidate');
			wgt.replaceWidget(newwgt);
			_afterAction(newwgt, act);
		}, function (wx) {
			for (var w = wx; w; w = w.parent)
				if (w == wgt)
					return null; //ignore it since it is going to be removed
			return wx;
		});
	},
	/** Replaces the widget with the widget(s) generated by evaluating the specified JavaScript code snippet.
	 * @param zk.Widget wgt the old widget to be replaced
	 * @param String code the JavaScript code snippet to generate new widget(s).
	 * @param String subId the redrawn node subid
	 * @since 9.0.0
	 */
	outerPartial: function (wgt, code, subId) {
		zkx_(code, function (newwgt) {
			var act = _beforeAction(newwgt, 'invalidate');
			wgt._outerPartialSubId = subId;
			wgt.replaceWidget(newwgt);
			_afterAction(newwgt, act);
		}, function (wx) {
			for (var w = wx; w; w = w.parent)
				if (w == wgt)
					return null; //ignore it since it is going to be removed
			return wx;
		});
	},
	/** Adds the widget(s) generated by evaluating the specified JavaScript code snippet
	 * after the specified widget (as sibling).
	 * @param zk.Widget wgt the existent widget that new widget(s) will be inserted after
	 * @param String code the JavaScript code snippet to generate new widget(s).
	 */
	/** Adds the widget(s) generated by evaluating the specified JavaScript code snippet
	 * after the specified widget (as sibling).
	 * @param zk.Widget wgt the existent widget that new widget(s) will be inserted after
	 * @param String... codes the JavaScript code snippet to generate new widget(s).
	 */
	addAft: function (wgt) {
		var fn = function (child) {
				var p = wgt.parent,
					act = _beforeAction(child, 'show');
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
				if (!_afterAction(child, act) && !child.z_rod)
					zUtl.fireSized(child);
			};
		for (var args = arguments, j = args.length; --j;)
			zkx_(args[j], fn);
	},
	/** Adds the widget(s) generated by evaluating the specified JavaScript code snippet
	 * before the specified widget (as sibling).
	 * @param zk.Widget wgt the existent widget that new widget(s) will be inserted before
	 * @param String... codes the JavaScript code snippet to generate new widget(s).
	 */
	addBfr: function (wgt) {
		var fn = function (child) {
			var act = _beforeAction(child, 'show');
			wgt.parent.insertBefore(child, wgt);
			if (!_afterAction(child, act) && !child.z_rod)
				zUtl.fireSized(child);
		};
		for (var args = arguments, j = 1; j < args.length; ++j)
			zkx_(args[j], fn);
	},
	/** Adds the widget(s) generated by evaluating the specified JavaScript code snippet
	 * as the last child of the specified widget.
	 * @param zk.Widget wgt the existent widget that will become the parent of new widget(s)
	 * @param String... codes the JavaScript code snippet to generate new widget(s).
	 */
	addChd: function (wgt) {
		if (wgt) {
			var fn = function (child) {
				var act = _beforeAction(child, 'show'),
					shallFireSizedLater = wgt.shallFireSizedLaterWhenAddChd_();
				wgt.appendChild(child);
				if (!_afterAction(child, act) && !child.z_rod && !shallFireSizedLater)
					zUtl.fireSized(child);
			};
			for (var args = arguments, j = 1; j < args.length; ++j)
				zkx_(args[j], fn);
		} else {
			for (var args = arguments, j = 1; j < args.length; ++j) {
				//possible if <?page complete="true"?>
				zkx_(args[j], _asBodyChild);
			}
		}
	},
	/** Removes the widget.
	 * @param zk.Widget wgt the widget to remove
	 */
	rm: function (wgt) {
		if (wgt) {
			wgt.detach();
			_detached.push(wgt); //used by mount.js
		}
	},
	/** Rename UUID.
	 * @param zk.Widget wgt the widget to rename
	 * @param String newId the new UUID
	 * @since 5.0.3
	 */
	uuid: function (wgt, newId) {
		if (wgt)
			zk._wgtutl.setUuid(wgt, newId); //see widget.js
	},

	/** Set the focus to the specified widget.
	 * It invokes {@link zk.Widget#focus}. Not all widgets support
	 * this method. In other words, it has no effect if the widget doesn't support it.
	 * @param zk.Widget wgt the widget.
	 */
	focus: function (wgt) {
		if (wgt) {
			// bug in ZK-2195, the focus command executed after window's doModal() for IE
			// so we have to do the same as that code in Window.js
			setTimeout(function () {
				zk.afterAnimate(function () {
					if (zk.ie9_)
						wgt.focus(100);
					else
						wgt.focus(0); //wgt.focus() failed in FF
				}, -1);
			});
		}
	},
	/** Selects all text of the specified widget.
	 * It invokes the <code>select</code> method, if any, of the widget.
	 * It does nothing if the method doesn't exist.
	 * @param zk.Widget wgt the widget.
	 * @param int start the starting index of the selection range
	 * @param int end the ending index of the selection range (excluding).
	 * 		In other words, the text between start and (end-1) is selected.
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
	invoke: function (wgt, func/*, vararg*/) {
		var args = [];
		for (var j = arguments.length; --j > 1;) //exclude wgt and func
			args.unshift(arguments[j]);
		if (wgt)
			wgt[func].apply(wgt, args);
		else {
			var fn = zk.$import(func);
			if (!fn) zk.error('not found: ' + func);
			fn.apply(null, args);
		}
	},
	/** Ask the client to echo back an AU request with the specified
	 * evant name and the target widget.
	 * @param zk.Widget wgt the target widget to which the AU request will be sent
	 * @param String evtnm the name of the event, such as onUser
	 * @param Object data any data
	 * @see zk.AuCmd0#echo
	 * @see zk.AuCmd0#echoGx
	 */
	echo2: function (wgt, evtnm, data) {
		zAu.send(new zk.Event(wgt, 'echo',
			data != null ? [evtnm, data] : [evtnm], {ignorable: true}));
	},
	/** Ask the client to re-cacluate the size of the given widget.
	 * @param zk.Widget wgt the widget to resize
	 * @since 5.0.8
	 */
	resizeWgt: function (wgt) {
		zUtl.fireSized(wgt, 1); //force cleanup
	},
	/** Ask the client to sync a target widget and its errorbox position.
	 * @param zk.Widget wgt the widget
	 * @since 8.5.2
	 */
	syncErrorbox: function (wgt) {
		if (wgt._errbox) {
			wgt._errbox.reposition();
			wgt._errbox._fixarrow();
		}
	}
};
/** @partial zk
 */
//@{
	/** Adds a function that will be executed after onResponse events are done.
	 * That means, after au responses, the function added in the afterAuResponse() will be invoked
	 * @param Function fn the function to execute after au responses
	 * @since 7.0.6
	 */
	//afterAuResponse: function () {}
//@};
zk.afterAuResponse = function (fn) {
	if (fn)
		_aftAuResp.push(fn);
};
zk.doAfterAuResponse = function () {
	for (var fn; fn = _aftAuResp.shift();) {
		fn();
	}
};
})();

function onIframeURLChange(uuid, url) { //doc in jsdoc
	if (!zk.unloading) {
		var wgt = zk.Widget.$(uuid);
		if (wgt) wgt.fire('onURIChange', url);
	}
}
