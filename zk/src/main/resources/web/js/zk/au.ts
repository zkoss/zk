/* au.ts

	Purpose:
		ZK Client Engine
	Description:

	History:
		Mon Sep 29 17:17:37     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
export type ErrorHandler = (response: AuResponse, errCode: number | string) => boolean;

export type AjaxErrorHandler = (req: Response, status: number, statusText: string, ajaxReqTries?: number) => number;

export interface AuCommand {
	cmd: string;
	data: ({ $u?: string } & zk.Widget | undefined)[]; // command-specific
}

export interface AuCommands extends Array<AuCommand> {
	rid?: string | number;
	dt?: zk.Desktop;
	pfIds?: string | null; // eslint-disable-line zk/noNull
	rtags?: Record<string, unknown>;
}

export interface AuRequestInfo {
	sid: number;
	uri: string;
	dt: zk.Desktop;
	content: string | FormData;
	implicit: boolean;
	ignorable: boolean;
	tmout: number;
	rtags: Record<string, unknown>;
	forceAjax: boolean;
	uploadCallbacks?: unknown[];
	header?: Record<string, string>;
	$ZKWS_ECHO$?: boolean;
}

declare global {
	interface Response {
		/** @see {@link XMLHttpRequest.responseText} */
		responseText?: string;
	}
}
export interface AuResponse extends Partial<Pick<XMLHttpRequest,
	| 'responseText'
	| 'readyState'
	| 'status'
	| 'statusText'
>> {
	/** @see {@link Response.headers} */
	readonly headers: Pick<Headers, 'get'>;
}

var _perrURIs: Record<string, string> = {}, //server-push error URI
	_onErrs: ErrorHandler[] = [], //onError functions
	cmdsQue: AuCommands[] = [], //response commands in XML
	sendPending, responseId: string | number,
	doCmdFns: (() => void)[] = [],
	idTimeout: undefined | number, //timer ID for automatica timeout
	pfIndex = 0, //performance meter index
	_detached: zk.Widget[] & { wgts?: Record<string, zk.Widget> } = [], //used for resolving #stub/#stubs in mount.js (it stores detached widgets in this AU)
	_portrait: Record<string, boolean> = { '0': true, '180': true }, //default portrait definition
	_initLandscape = jq.innerWidth() > jq.innerHeight(), // initial orientation is landscape or not
	_initDefault = _portrait[window.orientation], //default orientation
	_aftAuResp: (() => void)[] = []; //store callbacks to be triggered when au is back

// Checks whether to turn off the progress prompt
function checkProgressing(sid?: string): void {
	if (!zAu.processing()) {
		_detached = []; //clean up
		if (!zk.clientinfo)
			zk.endProcessing(sid);
		//setTimeout(zk.endProcessing, 50);
		// using a timeout to stop the processing after doing onSize in the fireSized() method of the Utl.js
		//Bug ZK-1505: using timeout cause progress bar disapper such as Thread.sleep(1000) case, so revert it back

		zAu.doneTime = Date.now();
	}
}
function pushCmds(cmds: AuCommands, rs: [string, ({ $u?: string } & zk.Widget | undefined)[]][]): void {
	for (var j = 0, rl = rs ? rs.length : 0; j < rl; ++j) {
		var r = rs[j],
			cmd = r[0],
			data = r[1];

		if (!cmd) {
			zAu.showError('ILLEGAL_RESPONSE', 'command required');
			continue;
		}

		cmds.push({ cmd: cmd, data: data || [] });
	}

	cmdsQue.push(cmds);
}
function dataNotReady(cmd: string, data: ({ $u?: string } & zk.Widget | undefined)[]): boolean {
	for (var j = data.length, id: string | undefined, w: zk.Widget | undefined; j--;)
		if (id = data[j] && data[j]!.$u) {
			if (!(w = zk.Widget.$(id))) { //not ready
				var processFn = function (): void {
					if (zk._crWgtUuids.includes(id!) && !zk.Widget.$(id)) {
						zk.afterMount(processFn, 0);
					} else {
						do {
							if (id = data[j] && data[j]!.$u)
								data[j] = zk.Widget.$(id) as never;
						} while (j--);
						doProcess(cmd, data);
					}
				};
				zk.afterMount(processFn, -1);
				return true; //not ready
			}
			data[j] = w;
		}
	return false;
}
function doProcess(cmd: string, data: ({ $u?: string } & zk.Widget | undefined)[]): void { //decoded
	if (!dataNotReady(cmd, data)) {
		if (!zAu.processPhase) {
			zAu.processPhase = cmd;
		}
		//1. process zAu.cmd1 (cmd1 has higher priority)
		var fn: undefined | CallableFunction = zAu.cmd1[cmd] as CallableFunction | undefined;
		if (fn) {
			if (!data.length)
				return zAu.showError('ILLEGAL_RESPONSE', 'uuid required', cmd);

			// fix stateless JQ selector issue on client ROD case
			let selector = String(data[0]),
				hasStateless = zk.isLoaded('stateless') && (selector.includes('@') || selector.includes('$'));
			try {
				if (hasStateless) {
					stateless['disableROD'] = true;
				}
                data[0] = zk.Widget.$(data[0]); //might be null (such as rm)
            } finally {
                if (hasStateless) {
                    stateless['disableROD'] = undefined;
                }
			}
			// Bug ZK-2827
			if (!data[0] && cmd != 'invoke' && cmd != 'addChd' /*Bug ZK-2839*/) {
				return;
			}

			if (cmd == 'setAttr' || cmd == 'setAttrs') {
				if (!zAu.doAfterProcessWgts) {
					zAu.doAfterProcessWgts = [];
				}
				zAu.doAfterProcessWgts.push(data[0]!);
			}

		} else {
			//2. process zAu.cmd0
			fn = zAu.cmd0[cmd] as CallableFunction | undefined;
			if (!fn)
				return zAu.showError('ILLEGAL_RESPONSE', 'Unknown', cmd);
		}
		fn.bind(zAu)(...data);
		zAu.processPhase = undefined;
	}
}

function ajaxReqResend2(): void {
	var reqInf = zAu.pendingReqInf;
	if (reqInf) {
		zAu.pendingReqInf = undefined;
		if (zAu.seqId == reqInf.sid)
			ajaxSendNow(reqInf);
	}
}
function _exmsg(e: Error): string {
	var msg = e.message || e, m2 = '';
	if (e.name) m2 = ' ' + e.name;
	//		if (e.fileName) m2 += " " +e.fileName;
	//		if (e.lineNumber) m2 += ":" +e.lineNumber;
	//		if (e.stack) m2 += " " +e.stack;
	return msg + (m2 ? ' (' + m2.substring(1) + ')' : m2);
}

function ajaxSend(dt: zk.Desktop, aureq: zk.Event, timeout?: number): void {
	//ZK-1523: dt(desktop) could be null, so search the desktop from target's parent.
	//call stack: echo2() -> send()
	if (!dt) {
		//original dt is decided by aureq.target.desktop, so start by it's parent.
		var wgt: zk.Widget | undefined = aureq.target.parent;
		while (!wgt!.desktop) {
			wgt = wgt!.parent;
			if (wgt instanceof zk.Desktop) break;
		}
		if (wgt instanceof zk.Desktop) {
			dt = wgt;
		} else {
			dt = wgt!.desktop as never;
		}
	}
	////
	zAu.addAuRequest(dt, aureq);

	ajaxSend2(dt, timeout);
	//Note: we don't send immediately (Bug 1593674)
}
function ajaxSend2(dt: zk.Desktop, timeout?: number): void {
	if (!timeout) timeout = 0;
	if (dt && timeout >= 0)
		setTimeout(function () { zAu.sendNow(dt); }, timeout);
}
function ajaxSendNow(reqInf: AuRequestInfo): void {
	var fetchOpts: RequestInit = {
		credentials: 'same-origin',
		method: 'POST',
		headers: reqInf.content instanceof FormData ? { 'ZK-SID': '' + reqInf.sid } : { 'Content-Type': zAu.ajaxSettings.contentType, 'ZK-SID': '' + reqInf.sid },
		body: reqInf.content
	};
	zAu.sentTime = Date.now(); //used by server-push (cpsp)
	zk.ausending = true;
	if (zk.xhrWithCredentials)
		fetchOpts.credentials = 'include';
	if (zAu._errCode) {
		if (fetchOpts.headers) {
			fetchOpts.headers['ZK-Error-Report'] = zAu._errCode;
		}
		zAu._errCode = undefined;
	}

	var forceAjax = reqInf.forceAjax;
	if (zk.pfmeter) zAu._pfsend(reqInf.dt, fetchOpts, false, forceAjax);

	// FIXME: Assigning `true` to `zAu.ajaxReq` is potentially problematic.
	zAu.ajaxReq = true as unknown as XMLHttpRequest; // processing flag
	zAu.ajaxReqInf = reqInf;

	if (!forceAjax && typeof zWs != 'undefined' && zWs.ready) {
		zWs.send(reqInf);
		return;
	}

	zAu._fetch(reqInf.uri, fetchOpts)
		.then(function (response: Response) {
			return response.text().then(function (responseText) {
				response.responseText = responseText;
				zAu._onResponseReady(response);
			});
		}).catch(function (e: Error) {
			zAu.ajaxReq = zAu.ajaxReqInf = undefined; // ZK-4775: should clear processing flag
			if (!reqInf.ignorable && !zk.unloading) {
				var msg = _exmsg(e);
				zAu._errCode = '[Send] ' + msg;
				if (zAu.confirmRetry('FAILED_TO_SEND', msg)) {
					zAu.ajaxReqResend(reqInf);
				}
			}
		});

	if (!reqInf.implicit)
		zk.startProcessing(zk.procDelay, reqInf.sid); //wait a moment to avoid annoying
}
function doCmdsNow(cmds: AuCommands): boolean {
	var rtags = cmds.rtags || {}, ex: undefined | Error;
	try {
		while (cmds && cmds.length) {
			if (zk.mounting) return false;

			var cmd = cmds.shift();
			try {
				doProcess(cmd!.cmd, cmd!.data);
			} catch (e) {
				zk.mounting = false; //make it able to proceed
				zAu.showError('FAILED_TO_PROCESS', undefined, cmd!.cmd, e as Error);
				if (!ex) ex = e as Error;
			}
		}
		if (zAu.doAfterProcessWgts) {
			zAu.doAfterProcessWgts.forEach(function (wgt: zk.Widget) {
				if (wgt.doAfterProcessRerenderArgs) {
					wgt.rerender(...(wgt.doAfterProcessRerenderArgs as []));
					wgt.doAfterProcessRerenderArgs = undefined;
				}
			});
			zAu.doAfterProcessWgts = undefined;
		}
	} finally {
		//Bug #2871135, always fire since the client might send back empty
		if (!cmds || !cmds.length) {
			// ZK-3288, If the wpd file of new created widget was never loaded,
			// sometimes onCommandReady and onResponse will be called during the widget mounting phase. (timing issue)
			zk.afterMount(function () {
				// Bug ZK-2516
				zWatch.fire('onCommandReady', undefined!, { timeout: -1, rtags: rtags }); //won't use setTimeout
				zWatch.fire('onResponse', undefined!, { timeout: 0, rtags: rtags }); //use setTimeout
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
function _asBodyChild(child: zk.Widget): void {
	jq(document.body).append(child);
}

//misc//
function fireClientInfo(): void {
	zAu.cmd0.clientInfo();
}
function sendTimeout(): void {
	zAu.send(new zk.Event(zk.Desktop._dt, 'dummy', undefined, { ignorable: true, serverAlive: true, rtags: { isDummy: true }, forceAjax: true }));
	//serverAlive: the server shall not ignore it if session timeout
	zk.isTimeout = true; //ZK-3304: already timeout
}

//store all widgets into a map
function _wgt2map(wgt: zk.Widget, map: Record<string, zk.Widget>): void {
	map[wgt.uuid] = wgt;
	for (var child = wgt.firstChild; child; child = child.nextSibling)
		_wgt2map(child, map);
}

function _beforeAction(wgt: zk.Widget, actnm: string): unknown {
	var act;
	if (wgt.isVisible() && (act = wgt.actions_[actnm])) {
		wgt.z$display = 'none'; //control zk.Widget.domAttrs_
		return act;
	}
}
function _afterAction(wgt: zk.Widget, act: [CallableFunction, unknown]): boolean {
	if (act) {
		delete wgt.z$display;
		act[0].bind(wgt)(wgt.$n(), act[1]);
		return true;
	}
	return false;
}

/**
 * @class zAu
 * @import zk.Widget
 * @import zk.Desktop
 * @import zk.Event
 * @import zk.AuCmd0
 * @import zk.AuCmd1
 * The AU Engine used to send the AU requests to the server and to process
 * the AU responses.
 */
export namespace au_global {
	export namespace zAu {
		export var disabledRequest = false;
		/** @internal */
		export var _cInfoReg = false;
		/** @internal */
		export var _fetch = window.fetch.bind(window);
		export let doneTime: number;
		export let sentTime: number;
		export let doAfterProcessWgts: zk.Widget[] | undefined;

		/** Implements this function to be called if the request fails.
		 * The function receives four arguments: The XHR (XMLHttpRequest) object,
		 * a number describing the status of the request, a string describing the text
		 * of the status, and a number describing the retry value to re-send.
		 *
		 * For example,
		 * ```ts
		 * zAu.ajaxErrorHandler = function (req, status, statusText, ajaxReqTries) {
		 * 	 if (ajaxReqTries == null)
		 * 	    ajaxReqTries = 3; // retry 3 times
		 *
		 *    // reset the resendTimeout, for more detail, please refer to
		 *    // http://books.zkoss.org/wiki/ZK_Configuration_Reference/zk.xml/The_client-config_Element/The_auto-resend-timeout_Element
		 *    zk.resendTimeout = 2000;//wait 2 seconds to resend.
		 *
		 *    if (!zAu.confirmRetry("FAILED_TO_RESPONSE", status+(statusText?": "+statusText:"")))
		 *       return 0; // no retry;
		 *    return ajaxReqTries;
		 * }
		 * ```
		 * @param req - the object of XMLHttpRequest
		 * @param status - the status of the request
		 * @param statusText - the text of the status from the request
		 * @param ajaxReqTries - the retry value for re-sending the request, if undefined means the function is invoked first time.
		 * @since 6.5.2
		 */
		export let ajaxErrorHandler: AjaxErrorHandler | undefined;
		export let processPhase: string | undefined;
		/** @internal */
		export let _clientInfo: unknown[];

		/** @internal */
		export function _resetTimeout(): void { //called by mount.js
			if (idTimeout) {
				clearTimeout(idTimeout);
				idTimeout = undefined;
			}
			if (zk.timeout > 0)
				idTimeout = setTimeout(sendTimeout, zk.timeout * 1000);
		}
		/** @internal */
		export function _onClientInfo(): void { //Called by mount.js when onReSize
			if (zAu._cInfoReg) setTimeout(fireClientInfo, 20);
			//we cannot pass zAu.cmd0.clientInfo directly
			//otherwise, FF will pass 1 as the firt argument,
			//i.e., it is equivalent to zAu.cmd0.clientInfo(1)
		}
		//Used by mount.js to search widget being detached in this AU
		/** @internal */
		export function _wgt$(uuid: string): zk.Widget {
			var map = _detached.wgts = _detached.wgts || {}, wgt: undefined | zk.Widget;
			while (wgt = _detached.shift())
				_wgt2map(wgt, map);
			return map[uuid];
		}
		/** @internal */
		export function _onVisibilityChange(): void { //Called by mount.js when page visibility changed
			if (zk.visibilitychange) zAu.cmd0.visibilityChange();
		}
		//Bug ZK-1596: native will be transfer to stub in EE, store the widget for used in mount.js
		/** @internal */
		export function _storeStub(wgt?: zk.Widget): void {
			if (wgt)
				_detached.push(wgt);
		}
		//Error Handling//
		/**
		 * Register a listener that will be called when the Ajax request failed.
		 * The listener shall be `function (response, errCode)`
		 *
		 * where response is an instance of response from Fetch API,
		 * and errCode is the error code.
		 * Furthermore, the listener could return true to ignore the error.
		 * In other words, if true is returned, the error is ignored (the
		 * listeners registered after won't be called either).
		 * <p>Notice that response.status might be 200, since ZK might send the error
		 * back with the ZK-Error header.
		 *
		 * <p>To remove the listener, use {@link unError}.
		 * @since 5.0.4
		 * @see {@link unError}
		 * @see {@link confirmRetry}
		 */
		export function onError(fn: ErrorHandler): void {
			_onErrs.push(fn);
		}
		/**
		 * Unregister a listener for handling errors.
		 * @since 5.0.4
		 * @see {@link onError}
		 */
		export function unError(fn: ErrorHandler): void {
			_onErrs.$remove(fn);
		}

		/**
		 * Called to confirm the user whether to retry, when an error occurs.
		 * @param msgCode - the message code
		 * @param msg2 - the additional message. Ignored if not specified or null.
		 * @returns whether to retry
		 */
		export function confirmRetry(msgCode: string, msg2?: string): boolean {
			var msg = msgzk[msgCode] as string;
			return jq.confirm((msg ? msg : msgCode) + '\n' + msgzk.TRY_AGAIN + (msg2 ? '\n\n(' + msg2 + ')' : ''));
		}
		/**
		 * Called to show an error if a severe error occurs.
		 * By default, it is an orange box.
		 * @param msgCode - the message code
		 * @param msg2 - the additional message. Ignored if not specified or null.
		 * @param cmd - the command causing the problem. Ignored if not specified or null.
		 * @param ex - the exception
		 */
		export function showError(msgCode: string, msg2?: string, cmd?: string, ex?: Error): void {
			var msg = msgzk[msgCode] as string;
			zk.error((msg ? msg : msgCode) + '\n' + (msg2 ? msg2 + ': ' : '') + (cmd || '')
				+ (ex ? '\n' + _exmsg(ex) : ''));
		}
		/**
		 * @returns the URI for the specified error.
		 * @param code - the error code
		 */
		export function getErrorURI(code: number): string | undefined {
			return zAu._errURIs['' + code];
		}
		/**
		 * Sets the URI for the specified error.
		 * @param code - the error code
		 * @param uri - the URI
		 */
		export function setErrorURI(code: number, uri?: string): void
		/**
		 * Sets the URI for the errors specified in a map.
		 * @param errors - A map of errors where the key is the error code (int),
		 * while the value is the URI (String).
		 */
		export function setErrorURI(errors: Record<number | string, string>): void
		export function setErrorURI(code: number | Record<number | string, string>, uri?: string): void {
			if (arguments.length == 1) {
				for (var c in code as Record<number | string, string>)
					zAu.setErrorURI(parseInt(c), code[c] as string);
			} else
				zAu._errURIs['' + code] = uri!;
		}
		/**
		 * Sets the URI for the server-push related error.
		 * @param code - the error code
		 * @returns the URI.
		 */
		export function getPushErrorURI(code: number): string {
			return _perrURIs['' + code];
		}
		/**
		 * Sets the URI for the server-push related error.
		 * @param code - the error code
		 * @param uri - the URI
		 */
		export function setPushErrorURI(code: number, uri?: string): void
		/**
		 * Sets the URI for the server-push related errors specified in a map.
		 * @param errors - A map of errors where the key is the error code (int),
		 * while the value is the URI (String).
		 */
		export function setPushErrorURI(errors: Record<number | string, string>): void
		export function setPushErrorURI(code: number | Record<number | string, string>, uri?: string): void {
			if (arguments.length == 1) {
				for (var c in code as Record<number | string, string>)
					zAu.setPushErrorURI(parseInt(c), code[c] as string);
				return;
			}
			_perrURIs['' + code] = uri!;
		}

		////Ajax Send////
		/**
		 * @returns whether ZK Client Engine is busy for processing something,
		 * such as mounting the widgets, processing the AU responses and on.
		 */
		export function processing(): boolean {
			return zk.mounting || !!cmdsQue.length || !!zAu.ajaxReq || !!zAu.pendingReqInf;
		}

		/**
		 * Sends an AU request and appends it to the end if there is other pending
		 * AU requests.
		 *
		 * @param aureq - the request. If {@link Event.target} is null,
		 * the request will be sent to each desktop at the client.
		 * @param timeout - the time (milliseconds) to wait before sending the request.
		 * 0 is assumed if not specified or negative.
		 * If negative, the request is assumed to be implicit, i.e., no message will
		 * be shown if an error occurs.
		 */
		export function send(aureq: zk.Event, timeout = 0): void {
			//ZK-2790: when unload event is triggered, the desktop is destroyed
			//we shouldn't send request back to server
			if (zk.unloading && zk.rmDesktoping) //it's safer to check if both zk.unloading and zk.rmDesktoping are true
				return;

			if (timeout < 0)
				aureq.opts = zk.copy(aureq.opts, { defer: true });

			var t = aureq.target;
			if (t && t !== zk.Desktop._dt) {
				ajaxSend(t instanceof zk.Desktop ? t : t.desktop!, aureq, timeout);
			} else {
				var dts = zk.Desktop.all;
				aureq.target = undefined!;
				for (var dtid in dts)
					ajaxSend(dts[dtid], aureq, timeout);
			}
		}
		/**
		 * Sends an AU request by placing in front of any other pending request.
		 * @param aureq - the request. If {@link Event.target} is null,
		 * the request will be sent to each desktop at the client.
		 * @param timeout - the time (milliseconds) to wait before sending the request.
		 * 0 is assumed if not specified or negative.
		 * If negative, the request is assumed to be implicit, i.e., no message will
		 * be shown if an error occurs.
		 */
		export function sendAhead(aureq: zk.Event, timeout = 0): void {
			const t = aureq.target;
			if (t) {
				const dt = t instanceof zk.Desktop ? t : t.desktop!;
				zAu.getAuRequests(dt).unshift(aureq);
				ajaxSend2(dt, timeout);
			} else {
				var dts = zk.Desktop.all;
				for (var dtid in dts) {
					let dt = dts[dtid];
					zAu.getAuRequests(dt).unshift(aureq);
					ajaxSend2(dt, timeout);
				}
				return;
			}
		}

		//remove desktop (used in mount.js and wiget.js)
		/** @internal */
		export function _rmDesktop(dt: zk.Desktop, dummy?: boolean): void {
			var url = zk.ajaxURI(undefined, { desktop: dt, au: true }),
				data = jq.param({
					dtid: dt.id,
					cmd_0: dummy ? 'dummy' : 'rmDesktop',
					opt_0: 'i'
				}),
				headers = {};
			if (zk.pfmeter) {
				var fakeFetachOpts = {
					headers: {}
				};
				headers = fakeFetachOpts.headers;
				zAu._pfsend(dt, fakeFetachOpts, true, false);
			}
			// ZK-4943
			if (dt) dt.fire('onBeforeDestroy');
			// ZK-4204
			if (navigator.sendBeacon && window.URLSearchParams) {
				var params = new URLSearchParams(data);
				for (var key in headers) {
					if (Object.prototype.hasOwnProperty.call(headers, key))
						params.append(key, headers[key] as string);
				}
				navigator.sendBeacon(url, zk.chrome // https://crbug.com/747787
					? new Blob([params.toString()], { type: 'application/x-www-form-urlencoded' })
					: params
				);
			} else {
				zAu._rmDesktopAjax(url, data, headers);
			}
			// B65-ZK-2210: clean up portlet2 data when desktop removed.
			if (!dummy && zk.portlet2Data && zk.portlet2Data[dt.id!]) {
				delete zk.portlet2Data[dt.id!];
			}
		}
		/** @internal */
		export function _rmDesktopAjax(url: string, data: string, headers: Record<string, string>): void {
			void jq.ajax(zk.$default({
				url: url,
				data: data,
				beforeSend: function (xhr: XMLHttpRequest) {
					for (var key in headers) {
						if (Object.prototype.hasOwnProperty.call(headers, key))
							xhr.setRequestHeader(key, headers[key]);
					}
				},
				//2011/04/22 feature 3291332
				//Use sync request for IE, chrome, safari and firefox (4 and later).
				//Note: when pressing F5, the request's URL still arrives before this even async:false
				async: false
			}, zAu.ajaxSettings));
		}

		////Ajax////
		/**
		 * Processes the AU response sent from the server.
		 * <p>Don't call it directly at the client.
		 * @param cmd - the command, such as echo
		 * @param data - the data in a JSON string.
		 */
		export function process(cmd: string, data: string): void {
			doProcess(cmd, (data ? jq.evalJSON(data) : []) as never);
		}
		/**
		 * @returns whether to ignore the ESC keystroke.
		 * It returns true if ZK Client Engine is sending an AU request
		 */
		export function shallIgnoreESC(): boolean {
			return !!zAu.ajaxReq;
		}
		/**
		 * Process the specified commands.
		 * @param dtid - the desktop's ID
		 * @param rs - a list of responses
		 * @since 5.0.5
		 */
		export function doCmds(dtid: string, rs: [string, ({ $u?: string } & zk.Widget | undefined)[]][]): void {
			var cmds: AuCommands = [];
			cmds.dt = zk.Desktop.$(dtid);
			pushCmds(cmds, rs);
			zAu._doCmds();
		}
		/** @internal */
		export function _doCmds(sid?: string): void { //called by mount.js, too
			for (var fn: (() => void) | undefined; fn = doCmdFns.shift();)
				fn();

			var ex: Error | undefined, j = 0, rid = responseId;
			for (; j < cmdsQue.length; ++j) {
				if (zk.mounting) return; //wait mount.js mtAU to call

				var cmds = cmdsQue[j];
				if (rid == cmds.rid || !rid || !cmds.rid //match
					|| zk.Desktop._ndt > 1) { //ignore multi-desktops (risky but...)
					cmdsQue.splice(j, 1);

					var oldrid = rid;
					if (cmds.rid) {
						if ((rid = cmds.rid as number + 1) >= 1000)
							rid = 1; //1~999
						responseId = rid;
					}

					try {
						if (doCmdsNow(cmds)) { //done
							j = -1; //start over
							if (zk.pfmeter) {
								var fnPfDone = function (): void {
									zAu._pfdone(cmds.dt!, cmds.pfIds);
								};
								if (zk.mounting) doCmdFns.push(fnPfDone);
								else fnPfDone();
							}
						} else { //not done yet (=zk.mounting)
							responseId = oldrid; //restore
							cmdsQue.splice(j, 0, cmds); //put it back
							return; //wait mount.js mtAU to call
						}
					} catch (e) {
						if (!ex) ex = e as Error;
						j = -1; //start over
					}
				}
			}

			if (cmdsQue.length) { //sequence is wrong => enforce to run if timeout
				setTimeout(function () {
					if (cmdsQue.length && rid == responseId) {
						var r = cmdsQue[0].rid as number || 0;
						for (j = 1; j < cmdsQue.length; ++j) { //find min
							var r2 = cmdsQue[j].rid as number || 0,
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
		}

		/**
		 * Called before sending an AU request.
		 * @defaultValue append {@link zk.Widget.autag} to `uri`.
		 * <p>It is designed to be overriden by an application to record
		 * what AU requests have been sent. For example, to work with Google Analytics,
		 * you can add the following code:
		 * ```html
		 * <script defer="true"><![CDATA[
		 * var pageTracker = _gat._getTracker("UA-123456");
		 * pageTracker._setDomainName("zkoss.org");
		 * pageTracker._initData();
		 * pageTracker._trackPageview();
	 	 *
		 * var auBfSend = zAu.beforeSend;
		 * zAu.beforeSend = function (uri, req, dt) {
		 *   try {
		 *     var target = req.target;
		 *     if (target.id) {
		 *       var data = req.data||{},
		 *       	value = data.items && data.items[0]?data.items[0].id:data.value;
		 *       pageTracker._trackPageview((target.desktop?target.desktop.requestPath:"") + "/" + target.id + "/" + req.name + (value?"/"+value:""));
		 *     }
		 *   } catch (e) {
		 *   }
		 *   return auBfSend(uri, req, dt);
		 * };
		 * ]]></script>
		 * ```
		 *
		 * @param uri - the AU's request URI (such as /zkau)
		 * @param aureq - the AU request
		 * @param dt - the desktop
		 * @returns the AU's request URI.
		 * @since 5.0.2
		 */
		export function beforeSend(uri: string, aureq: zk.Event, dt?: zk.Desktop): string {
			var target: zk.Widget | undefined, tag: string | undefined;
			if ((target = aureq.target) && (tag = target.autag)) {
				tag = '/' + encodeURIComponent(tag);
				if (!uri.includes('/_/')) {
					var v: undefined | zk.Desktop | string = target.desktop;
					if ((v = v ? v.requestPath : '') && !v.startsWith('/'))
						v = '/' + v; //just in case
					tag = '/_' + v + tag;
				}

				var j = uri.lastIndexOf(';');
				if (j >= 0) uri = uri.substring(0, j) + tag + uri.substring(j);
				else uri += tag;
			}
			return uri;
		}
		/**
		 * @returns the content to send to the server.
		 * By default, it is encoded into several parameters and the data
		 * parameters (data_*) is encoded in JSON.
		 * <p>If you prefer to encode it into another format, you could override
		 * this method, and also implement a Java interface called
		 * <a href="http://www.zkoss.org/javadoc/latest/zk/org/zkoss/zk/au/AuDecoder.html">org.zkoss.zk.au.AuDecoder</a>
		 * to decode the format at the server.
		 * <p>If you prefer to encode it into URI, you could override
		 * {@link beforeSend}.
		 * @param j - the order of the AU request. ZK sends a batch of AU
		 * request at once and this argument indicates the order an AU request is
		 * (starting from 0).
		 * @param aureq - the AU request
		 * @param dt - the desktop
		 * @since 5.0.4
		 */
		export function encode(j: number, aureq: zk.Event, dt: zk.Desktop): string {
			var target = aureq.target,
				opts = aureq.opts || {},
				portlet2Namespace = '';

			// B65-ZK-2210: add porlet namespace
			if (zk.portlet2Data && zk.portlet2Data[dt.id!]) {
				portlet2Namespace = zk.portlet2Data[dt.id!].namespace || '';
			}
			var content = j ? '' : portlet2Namespace + 'dtid=' + dt.id;

			content += '&' + portlet2Namespace + 'cmd_' + j + '=' + aureq.name;
			if ((opts.implicit || opts.ignorable) && !(opts.serverAlive))
				content += '&' + portlet2Namespace + 'opt_' + j + '=i';
			//thus, the server will ignore it if session timeout

			if (target && target.className != 'zk.Desktop')
				content += '&' + portlet2Namespace + 'uuid_' + j + '=' + target.uuid;

			var data = aureq.data, dtype = typeof data;
			if (dtype === 'string' || dtype === 'number' || dtype === 'boolean' || Array.isArray(data)) {
				data = { '': data };
			}
			if (data) {
				content += '&' + portlet2Namespace + 'data_' + j + '=' + encodeURIComponent(zAu.toJSON(target, data as never));
			}
			return content;
		}

		/**
		 * Enforces all pending AU requests of the specified desktop to send immediately
		 * @returns whether it is sent successfully. If it has to wait
		 * for other condition, this method returns false.
		 */
		export function sendNow(dt: zk.Desktop): boolean {
			if (zAu.disabledRequest) {
				if (zk.processing)
					zk.endProcessing();
				return false;
			}
			var es = zAu.getAuRequests(dt);
			if (es.length == 0)
				return false;

			if (zk.mounting) {
				zk.afterMount(function () {
					zAu.sendNow(dt);
				});
				return true; //wait
			}

			if (zAu.ajaxReq || zAu.pendingReqInf) { //send ajax request one by one
				sendPending = true;
				return true;
			}

			var ajaxReqMaxCount = zAu.ajaxReqMaxCount;
			if (es.length > ajaxReqMaxCount) {
				console.warn('The count of au requests is unexpectedly huge: ' + es.length); // eslint-disable-line no-console
				es = es.splice(0, ajaxReqMaxCount);
				sendPending = true;
			}

			//decide implicit (and uri)
			var implicit = false, uri: string | undefined;
			for (var j = 0, el = es.length; j < el; ++j) {
				var aureq = es[j],
					opts = aureq.opts || {};
				if (opts.uri != uri) {
					if (j) break;
					uri = opts.uri;
				}

				//ignorable and defer implies implicit
				if (!(implicit = !!(opts.ignorable || opts.implicit || opts.defer)))
					break;
			}

			//notify watches (fckez uses it to ensure its value is sent back correctly
			try {
				zWatch.fire('onSend', undefined!, implicit);
			} catch (e) {
				zAu.showError('FAILED_TO_SEND', undefined, undefined, e as Error);
			}

			//decide ignorable
			var ignorable = true;
			for (var j = 0, el = es.length; j < el; ++j) {
				var aureq = es[j],
					opts = aureq.opts || {};
				if ((opts.uri != uri)
					|| !(ignorable = ignorable && !!opts.ignorable)) //all ignorable
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
			var content: Record<string, unknown> | string | FormData, rtags = {},
				requri = uri || zk.ajaxURI(undefined, { desktop: dt, au: true }),
				ws = typeof zWs != 'undefined' && zWs.ready;
			if (!forceAjax && ws) {
				content = {};
			} else {
				content = '';
			}
			const files = [], uploadCallbacks: unknown[] = [];
			let lasturi = requri;
			for (let j = 0, aureq: zk.Event | undefined; aureq = es.shift(); ++j) {
				if ((aureq.opts || {}).uri != uri) {
					es.unshift(aureq);
					break;
				}

				const oldRequri = zAu.beforeSend(requri, aureq, dt);

				// split the different request for the different ReqUri for stateless
				if (j > 0 && oldRequri != lasturi) {
					es.unshift(aureq);
					sendPending = true;
					break;
				}
				lasturi = oldRequri;

				aureq.data = zAu._deconstructPacket(aureq.data as never, files);

				if (files.length) {
					// TODO: forceAjax for file upload, we may support it on websocket later on.
					forceAjax = true;
					if (aureq.opts && aureq.opts.uploadCallback) {
						uploadCallbacks.push(aureq.opts.uploadCallback);
					}
				}

				if (!forceAjax && ws) {
					zk.copy(content, zWs.encode(j, aureq, dt));
				} else {
					content += zAu.encode(j, aureq, dt);
				}
				zk.copy(rtags, (aureq.opts || {}).rtags);
			}
			requri = lasturi;
			// B65-ZK-2210: get resourceURL by desktop id
			if (zk.portlet2Data && zk.portlet2Data[dt.id!]) {
				requri = zk.portlet2Data[dt.id!].resourceURL;
			}

			if (files.length) {

				const data = content;
				content = new FormData();
				content.append('data', data as never);
				content.append('attachments', files.length + '');
				for (let i = 0, j = files.length; i < j; i++) {
					content.append('files_' + i, files[i]);
				}
			}

			//if (zk.portlet2AjaxURI)
			//requri = zk.portlet2AjaxURI;
			if (content)
				ajaxSendNow({
					sid: zAu.seqId,
					uri: requri,
					dt: dt,
					content: content as FormData | string,
					implicit: implicit,
					ignorable: ignorable,
					tmout: 0,
					rtags: rtags,
					forceAjax: forceAjax,
					uploadCallbacks: uploadCallbacks
				});
			return true;
		}
		/**
		 * Add the AU request to the ajax queue.
		 * @param aureq - the request.
		 * @since 7.0.3
		 */
		export function addAuRequest(dt: zk.Desktop, aureq: zk.Event): void {
			if (!dt.obsolete)
				dt._aureqs.push(aureq);
		}
		/**
		 * @returns all pending AU requests, an array of {@link Event}
		 * @since 7.0.3
		 */
		export function getAuRequests(dt: zk.Desktop): zk.Event[] {
			return dt._aureqs;
		}
		/**
		 * A map of Ajax default setting used to send the AU requests.
		 */
		export const ajaxSettings = zk.$default({
			global: false,
			//cache: false, //no need to turn off cache since server sends NO-CACHE
			contentType: 'application/x-www-form-urlencoded;charset=UTF-8'
		}, jq.ajaxSettings);

		// Adds performance request IDs that have been processed completely.
		// Called by moun.js, too
		/** @internal */
		// eslint-disable-next-line zk/noNull
		export function _pfrecv(dt: zk.Desktop, pfIds?: string | null): void {
			zAu.pfAddIds(dt, '_pfRecvIds', pfIds);
		}
		// Adds performance request IDs that have been processed completely.
		// Called by moun.js, too
		/** @internal */
		// eslint-disable-next-line zk/noNull
		export function _pfdone(dt: zk.Desktop, pfIds?: string | null): void {
			zAu.pfAddIds(dt, '_pfDoneIds', pfIds);
		}
		// Sets performance rquest IDs to the request's header
		// Called by moun.js, too
		/** @internal */
		export function _pfsend(dt: zk.Desktop, fetchOpts: RequestInit, completeOnly: boolean, forceAjax: boolean): void {
			var ws = !forceAjax && typeof zWs != 'undefined' && zWs.ready,
				fetchHeaders = fetchOpts.headers;
			if (!completeOnly) {
				var dtt = dt.id + '-' + pfIndex++ + '=' + Math.round(jq.now());
				if (fetchHeaders) fetchHeaders['ZK-Client-Start'] = dtt;
				if (ws) {
					zWs.setRequestHeaders('ZK-Client-Start', dtt);
				}
			}

			var ids: undefined | string;
			if (ids = dt._pfRecvIds) {
				if (fetchHeaders) fetchHeaders['ZK-Client-Receive'] = ids;
				if (ws) {
					zWs.setRequestHeaders('ZK-Client-Receive', ids);
				}
				dt._pfRecvIds = undefined;
			}
			if (ids = dt._pfDoneIds) {
				if (fetchHeaders) fetchHeaders['ZK-Client-Complete'] = ids;
				if (ws) {
					zWs.setRequestHeaders('ZK-Client-Complete', ids);
				}
				dt._pfDoneIds = undefined;
			}
		}

		/**
		 * Creates widgets based on an array of JavaScritp codes generated by
		 * Component.redraw() at the server.
		 * <p>This method is usually used with Java's ComponentsCtrl.redraw, and
		 * {@link zk.Widget.replaceCavedChildren_}.
		 * <p>Notice that, since the creation of widgets might cause some packages
		 * to be loaded, the callback function, fn, might be called after this
		 * method is returned
		 * @param codes - an array of JavaScript objects generated at the server.
		 * For example, `smartUpdate("foo", ComponentsCtrl.redraw(getChildren());`
		 * @param fn - the callback function. When the widgets are created.
		 * `fn` is called with an array of {@link zk.Widget}. In other words,
		 * the callback's signature is as follows:<br/>
		 * `void callback(zk.Widget[] wgts);`
		 * @param filter - the filter to avoid the use of widgets being replaced.
		 * Ignored if null
		 * @since 5.0.2
		 */
		export function createWidgets(codes: ArrayLike<unknown>[], fn: (wgts: zk.Widget[]) => void, filter?: (wgt: zk.Widget) => zk.Widget | undefined): void {
			//bug #3005632: Listbox fails to replace with empty model if in ROD mode
			var wgts: zk.Widget[] = [], len = codes.length;
			if (len > 0) {
				for (var j = 0; j < len; ++j)
					window.zkx_(codes[j], function (newwgt: zk.Widget) {
						wgts.push(newwgt);
						if (wgts.length == len)
							fn(wgts);
					}, filter);
			} else
				fn(wgts);
		}

		/* (not jsdoc)
		* Shows or clear an error message. It is overriden by zul.wpd.
		* <p>wrongValue_(wgt, msg): show an error message
		* <p>wrongValue_(wgt, false): clear an error message
		*/
		/** @internal */
		export function wrongValue_(wgt: zk.Widget, msg: string | false): void {
			if (msg !== false)
				jq.alert(msg);
		}
		// Called when the response is received from fetch.
		/** @internal */
		export function _onResponseReady(response: Response): void {
			var reqInf = zAu.ajaxReqInf, sid: string | undefined;
			try {
				if (response) {
					zAu.ajaxReq = zAu.ajaxReqInf = undefined;
					if (zk.pfmeter) zAu._pfrecv(reqInf!.dt, zAu.pfGetIds(response));

					sid = response.headers.get('ZK-SID')!;

					var rstatus: number;
					if ((rstatus = response.status) == 200) { //correct
						if (zAu._respSuccess(response, reqInf!, sid)) return;
					} else if ((!sid || sid == String(zAu.seqId)) //ignore only if out-of-seq (note: 467 w/o sid)
						&& !zAu.onResponseError(response, zAu._errCode = rstatus)) {
						if (zAu._respFailure(response, reqInf!, rstatus)) return;
					}
				}
			} catch (e) {
				if (zAu._respException(reqInf!, e as Error)) return;
			}

			zAu.afterResponse(sid);
		}
		/** @internal */
		export function _respSuccess(response: Response, reqInf: AuRequestInfo, sid: string | number): boolean {
			if (sid && sid != zAu.seqId) {
				zAu._errCode = 'ZK-SID ' + (sid ? 'mismatch' : 'required');
				zAu.afterResponse(); //continue the pending request if any
				return true;
			} //if sid null, always process (usually for error msg)

			// eslint-disable-next-line zk/noNull
			var v: null | number | string;
			if ((v = response.headers.get('ZK-Error'))
				&& !zAu.onResponseError(response, v = zk.parseInt(v) ?? v)
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

			if (zAu.pushReqCmds(reqInf, response)) { //valid response
				//advance SID to avoid receive the same response twice
				if (sid && ++zAu.seqId > 9999) zAu.seqId = 1;
				zAu.ajaxReqTries = 0;
				zAu.pendingReqInf = undefined;
			}
			return false;
		}
		/** @internal */
		export function _respFailure(response: Response, reqInf: AuRequestInfo, rstatus: number): boolean {
			var eru = zAu._errURIs['' + rstatus];
			if (typeof eru == 'string') {
				zUtl.go(eru);
				return true;
			}

			if (typeof zAu.ajaxErrorHandler == 'function') {
				zAu.ajaxReqTries = zAu.ajaxErrorHandler(response, rstatus, response.statusText, zAu.ajaxReqTries);
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
					//fall through
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
					var msg = response.statusText;
					if (zAu.confirmRetry('FAILED_TO_RESPONSE', rstatus + (msg ? ': ' + msg : ''))) {
						zAu.ajaxReqTries = 2; //one more try
						zAu.ajaxReqResend(reqInf);
						return true;
					}
				}
			}
			return false;
		}
		/** @internal */
		export function _respException(reqInf: AuRequestInfo, e: Error): boolean {
			if (!window.zAu)
				return true; //the doc has been unloaded

			zAu.ajaxReq = zAu.ajaxReqInf = undefined;

			//NOTE: if connection is off and req.status is accessed,
			//Mozilla throws exception while IE returns a value
			if (reqInf && !reqInf.ignorable && !zk.unloading) {
				var msg = _exmsg(e);
				zAu._errCode = '[Receive] ' + msg;
				//if (e.fileName) _errCode += ", "+e.fileName;
				//if (e.lineNumber) _errCode += ", "+e.lineNumber;
				if (zAu.confirmRetry('FAILED_TO_RESPONSE', (msg && !msg.includes('NOT_AVAILABLE') ? msg : ''))) {
					zAu.ajaxReqResend(reqInf);
					return true;
				}
			}
			return false;
		}

		export function pushReqCmds(reqInf: AuRequestInfo, response: AuResponse): boolean {
			var dt = reqInf.dt,
				rt = response.responseText;
			if (!rt) {
				if (zk.pfmeter) zAu._pfdone(dt, zAu.pfGetIds(response));
				return false; //invalid
			}

			var cmds: AuCommands = [];
			cmds.rtags = reqInf.rtags;
			if (zk.pfmeter) {
				cmds.dt = dt;
				cmds.pfIds = zAu.pfGetIds(response);
			}

			var json: { rid?: string; rs?: [] };
			try {
				json = jq.evalJSON(rt) as never;
			} catch (e) {
				if (e instanceof Error) {
					if (e.name == 'SyntaxError') { //ZK-4199: handle json parse error
						zAu.showError('FAILED_TO_PARSE_RESPONSE', e.message);
						zk.debugLog(e.message + ', response text:\n' + rt);
						return false;
					}
				}
				throw e;
			}
			var rid: string | number | undefined = json.rid;
			if (rid) {
				rid = parseInt(rid); //response ID
				if (!isNaN(rid)) cmds.rid = rid;
			}

			pushCmds(cmds, json.rs as never);
			return true;
		}

		/* internal use only */
		export function afterResponse(sid?: string): void {
			zAu._doCmds(sid); //invokes checkProgressing

			//handle pending ajax send
			if (sendPending && !zAu.ajaxReq && !zAu.pendingReqInf) {
				sendPending = false;
				var dts = zk.Desktop.all;
				for (var dtid in dts)
					ajaxSend2(dts[dtid], 0);
			}
		}
		/* @param zk.Widget target
		*/
		export function toJSON(
			target: zk.Widget | undefined,
			data: // `pageX` and `pageY` should co-exist
				| { pageX: number; pageY: number; x?: number; y?: number; z$dateKeys?: string[] }
				| { pageX?: undefined; pageY?: undefined; x?: number; y?: number; z$dateKeys?: string[] }
				| unknown[]
		): string {
			if (!Array.isArray(data)) {
				const { pageX, pageY, x } = data;
				if (pageX != null && x == null) {
					[data.x, data.y] = target?.desktop ? // B50-3336745: target may have been detached
						target.fromPageCoord(pageX, pageY) : [pageX, pageY];
				}

				const dateKeys: string[] = [];
				for (const n in data) {
					const v = data[n] as unknown;
					if (v instanceof DateImpl) {
						data[n] = jq.d2j(v);
						dateKeys.push(n);
					}
				}
				if (dateKeys.length != 0)
					data.z$dateKeys = dateKeys;
			}
			return jq.toJSON(data);
		}
		/* internal use only */

		// refer to socket.io
		const _withNativeArrayBuffer = typeof ArrayBuffer === 'function',
			isView = (obj: ArrayBufferView | unknown & { buffer?}): boolean => {
				return typeof ArrayBuffer.isView === 'function'
					? ArrayBuffer.isView(obj)
					: obj.buffer instanceof ArrayBuffer;
			},
			toString = Object.prototype.toString,
			_withNativeBlob = typeof Blob === 'function' ||
				(typeof Blob !== 'undefined' &&
					toString.call(Blob) === '[object BlobConstructor]'),
			_withNativeFile = typeof File === 'function' ||
				(typeof File !== 'undefined' &&
					toString.call(File) === '[object FileConstructor]');

		/** @internal */
		export function _isBinary(obj: ArrayBufferView | unknown & { buffer?}): boolean {
			return ((_withNativeArrayBuffer && (obj instanceof ArrayBuffer || isView(obj))) ||
				(_withNativeBlob && obj instanceof Blob) ||
				(_withNativeFile && obj instanceof File));
		}
		/** @internal */
		export function _constructPacket(data: unknown | undefined, files: unknown[]): unknown {
			if (!data) return data;
			if (Array.isArray(data)) {
				for (let i = 0; i < data.length; i++) {
					data[i] = _constructPacket(data[i] as never, files);
				}
			} else if (typeof data === 'object' && !(data instanceof Date) &&
				!(data instanceof DateImpl) && !(data instanceof zk.Object)) {

				if (Object.hasOwnProperty.call(data, '_placeholder') && typeof data['num'] === 'number') {
					return files[data['num']];
				}
				for (const key in data) {
					if (Object.prototype.hasOwnProperty.call(data, key)) {
						data[key] = _constructPacket(data[key] as never, files);
					}
				}
			}
			return data;
		}
		/** @internal */
		export function _deconstructPacket(data: ArrayBufferView | unknown & { buffer?} | undefined, buffers: unknown[]): unknown {
			if (!data) return data;

			if (_isBinary(data)) {
				const placeholder = { _placeholder: true, num: buffers.length };
				buffers.push(data);
				return placeholder;
			} else if (Array.isArray(data)) {
				const newData = new Array(data.length);
				for (let i = 0; i < data.length; i++) {
					newData[i] = _deconstructPacket(data[i] as never, buffers);
				}
				return newData as never;
			} else if (data instanceof FileList) { // avoid Object type toJson.
				const newData = new Array(data.length);
				for (let i = 0; i < data.length; i++) {
					newData[i] = _deconstructPacket(data.item(i) as never, buffers);
				}
				return newData as never;
			} else if (typeof data === 'object' && !(data instanceof Date) &&
				!(data instanceof DateImpl) && !(data instanceof zk.Object)) {
				const newData = {};
				for (const key in data) {
					if (Object.prototype.hasOwnProperty.call(data, key)) {
						newData[key] = _deconstructPacket(data[key] as never, buffers);
					}
				}
				return newData;
			}
			return data;
		}
		export let ajaxReq: XMLHttpRequest | undefined;
		/* internal use only */
		export let ajaxReqInf: AuRequestInfo | undefined;
		/* internal use only */
		export let ajaxReqTries: number;
		/* internal use only */
		export const ajaxReqMaxCount = 300;
		/* internal use only */
		export var seqId = (Date.now() % 9999) + 1;
		/* internal use only */
		export let pendingReqInf: AuRequestInfo | undefined;
		/** @internal */
		export let _errCode: string | number | undefined;
		/** @internal */
		export const _errURIs: Record<string, string> = {};
		//Perfomance Meter//
		// Returns request IDs sent from the server separated by space.
		// eslint-disable-next-line zk/noNull
		export function pfGetIds(response: AuResponse): string | null {
			return response.headers.get('ZK-Client-Complete');
		}
		// eslint-disable-next-line zk/noNull
		export function pfAddIds(dt: zk.Desktop, prop: string, pfIds?: string | null): void {
			pfIds = pfIds?.trim();
			if (pfIds) {
				const s = `${pfIds}=${Math.round(jq.now())}`;
				if (dt[prop])
					dt[prop] += ',' + s;
				else
					dt[prop] = s;
			}
		}
		export function ajaxReqResend(reqInf: AuRequestInfo, timeout?: number): void {
			if (zAu.seqId == reqInf.sid) {//skip if the response was recived
				zAu.pendingReqInf = reqInf; //store as a pending request info
				setTimeout(ajaxReqResend2, timeout ?? 0);
			}
		}
		export function onResponseError(response: AuResponse, errCode: number | string): boolean {
			//$clone first since it might add or remove onError
			for (var errs = _onErrs.$clone(), fn: undefined | ErrorHandler; fn = errs.shift();)
				if (fn(response, errCode))
					return true; //ignored
			return false;
		}

		//Commands//
		/**
		 * @class zk.AuCmd0
		 * The AU command handler for processes commands not related to widgets,
		 * sent from the server.
		 * @see {@link zAu.cmd0}
		 */
		export namespace cmd0 { //no uuid at all
			/**
			 * Sets a bookmark
			 * @param bk - the bookmark
			 * @param replace - if true, it will replace the bookmark without creating
			 * 		a new one history.
			 */
			export function bookmark(bk: string, replace: boolean): void {
				zk.bmk.bookmark(bk, replace);
			}
			/**
			 * Shows an error to indicate the desktop is timeout.
			 * @param dtid - the desktop UUID
			 * @param msg - the error message
			 */
			export function obsolete(dtid: string, msg: string): void {
				var v = zk.Desktop.$(dtid);
				if (v) v.obsolete = true;

				if (msg.startsWith('script:'))
					return $eval(msg.substring(7));

				// ZK-2397: prevent from showing reload dialog again while browser is reloading
				if (zk._isReloadingInObsolete)
					return;

				var reqPath;
				if (v && (reqPath = v.requestPath))
					msg = msg.replace(dtid, reqPath + ' (' + dtid + ')');

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
			}
			/**
			 * Shows an alert to indicate some error occurs.
			 * For widget's error message, use {@link wrongValue} instead.
			 * @param msg - the error message
			 */
			export function alert(msg: string, title: string, icon: string, disabledAuRequest: boolean): void {
				if (disabledAuRequest)
					zAu.disabledRequest = true;
				jq.alert(msg, { icon: icon || 'ERROR', title: title });
			}
			/**
			 * Redirects to the specified URL.
			 * @param url - the URL to redirect to
			 * @param target - the window name to show the content
			 * of the URL. If omitted, it will replace the current content.
			 */
			export function redirect(url: string, target?: string): void {
				try {
					zUtl.go(url, { target: target });

					// '#' for bookmark change only, Bug ZK-2874
					var idx: number;
					if (url && !url.startsWith('/') && (idx = url.indexOf('#')) >= 0) {
						var uri = url.substring(0, idx),
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
			}
			/**
			 * Changes the brower window's titile.
			 * @param title - the new title
			 */
			export function title(title: string): void {
				document.title = title;
			}
			/**
			 * @see {@link zk.log}
			 * @since 5.0.8
			 */
			export import log = zk.log;
			/**
			 * Executes the JavaScript.
			 * @param script - the JavaScript code snippet to execute
			 */
			export function script(script: string): void {
				const scriptErrorHandler = zk.scriptErrorHandler;
				if (scriptErrorHandler && !zk.scriptErrorHandlerRegistered) {
					zk.scriptErrorHandlerRegistered = true;
					jq(window).one('error', scriptErrorHandler);
				}
				jq.globalEval(script);
			}
			/**
			 * Asks the client to echo back an AU request, such that
			 * the server can return other commands.
			 * It is used to give the end user a quick response before doing
			 * a long operation.
			 * @param dtid - the desktop ID ({@link zk.Desktop}).
			 * @see zk.AuCmd1#echo2
			 * @see {@link echoGx}
			 */
			export function echo(dtid?: string | zk.Desktop): void {
				var dt = zk.Desktop.$(dtid),
					aureqs = dt ? zAu.getAuRequests(dt) : [];
				// Bug ZK-2741
				for (var i = 0, j = aureqs.length; i < j; i++) {
					var aureq0 = aureqs[i];
					if ((!aureq0.target || (aureq0.target instanceof zk.Desktop)) && aureq0.name == 'dummy') {
						return; //no need to send more
					}
				}
				zAu.send(new zk.Event(dt, 'dummy', undefined, {
					ignorable: true,
					rtags: { isDummy: true }
				}));
			}
			/**
			 * Ask the client to echo back globally.
			 * <p>Unlike {@link echo}, it will search all browser windows for
			 * <p>Note: this feature requires ZK EE
			 * the given desktop IDs.
			 * @param evtnm - the event name
			 * @param data - any string-typed data
			 * @param args - any number of desktop IDs.
			 * @since 5.0.4
			 */
			export function echoGx(evtnm: string, data: unknown, ...args: string[]): void { return; }

			/**
			 * Asks the client information.
			 * The client will reply the information in the `onClientInfo` response.
			 * @param dtid - the desktop ID ({@link zk.Desktop}).
			 */
			export function clientInfo(dtid?: string): void {
				zAu._cInfoReg = true;
				var orient = '',
					dpr = 1.0;

				if (zk.mobile) {
					//change default portrait definition because landscape is the default orientation for this device/browser.
					if ((_initLandscape && _initDefault) || (!_initLandscape && !_initDefault))
						_portrait = { '-90': true, '90': true };

					orient = _portrait[window.orientation] ? 'portrait' : 'landscape';
				} else {
					orient = jq.innerWidth() > jq.innerHeight() ? 'landscape' : 'portrait';
				}

				if (window.devicePixelRatio)
					dpr = window.devicePixelRatio;

				var clientInfo = [new Date().getTimezoneOffset(),
				screen.width, screen.height, screen.colorDepth,
				jq.innerWidth(), jq.innerHeight(), jq.innerX(), jq.innerY(), String(dpr), orient,
				zk.mm.tz.guess()],
					oldClientInfo = zAu._clientInfo;

				// ZK-3181: only send when value changed
				if (oldClientInfo) {
					var same = oldClientInfo.every(function (el, index) {
						return el === clientInfo[index];
					});
					if (same) {
						// ZK-4696: should endprocessing manually since no event is fired
						zk.endProcessing();
						delete zk.clientinfo;
						return;
					}
				}

				zAu._clientInfo = clientInfo;
				zAu.send(new zk.Event(zk.Desktop.$(dtid), 'onClientInfo',
					zAu._clientInfo,
					{ implicit: true, rtags: { onClientInfo: 1 } }));
			}
			export function visibilityChange(dtid?: string): void {
				var hidden = !!(document.hidden || document[zk.vendor_ + 'Hidden']),
					visibilityState = document.visibilityState || document[zk.vendor_ + 'VisibilityState'];

				zAu.send(new zk.Event(zk.Desktop.$(dtid), 'onVisibilityChange',
					{
						hidden: hidden,
						visibilityState: visibilityState
					}, { implicit: true, ignorable: true }));
			}
			/**
			 * Asks the client to download the resource at the specified URL.
			 * @param url - the URL to download from
			 */
			export function download(url: string): void {
				if (url) {
					var ifr: HTMLIFrameElement = jq('#zk_download')[0] as HTMLIFrameElement,
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

					ifr.src = url; //It is OK to reuse the same iframe

					// Workaround for IE11: wait a second (not perfect) for iframe loading
					if (ie === 11)
						setTimeout(function () {
							zk.skipBfUnload = sbu;
						}, 1000);
				}
			}
			/**
			 * Prints the content of the browser window.
			 */
			export function print(): void {
				return window.print();
			}
			/**
			 * Scrolls the content of the browser window.
			 * @param x - the offset (difference) in the X coordinate (horizontally) (pixels)
			 * @param y - the offset in the Y coordinate (vertically) (pixels)
			 */
			export function scrollBy(x: number, y: number): void {
				return window.scrollBy(x, y);
			}
			/**
			 * Scrolls the contents of the browser window to the specified location.
			 * @param x - the X coordinate to scroll to (pixels)
			 * @param y - the Y coordinate to scroll to (pixels)
			 */
			export function scrollTo(x: number, y: number): void {
				return window.scrollTo(x, y);
			}
			/**
			 * Resizes the browser window.
			 * @param x - the number of pixels to increase/decrease (pixels)
			 * @param y - the number of pixels to increase/decrease (pixels)
			 */
			export function resizeBy(x: number, y: number): void {
				return window.resizeBy(x, y);
			}
			/**
			 * Resizes the browser window to the specified size.
			 * @param x - the required width (pixels)
			 * @param y - the required height (pixels)
			 */
			export function resizeTo(x: number, y: number): void {
				return window.resizeTo(x, y);
			}
			/**
			 * Moves the browser window.
			 * @param x - the number of pixels to move in the X coordinate
			 * @param y - the number of pixels to move in the Y coordinate
			 */
			export function moveBy(x: number, y: number): void {
				return window.moveBy(x, y);
			}
			/**
			 * Moves the browser window to the specified location
			 * @param x - the left (pixels)
			 * @param y - the top (pixels)
			 */
			export function moveTo(x: number, y: number): void {
				return window.moveTo(x, y);
			}
			/**
			 * Sets the message used to confirm the user when he is closing
			 * the browser window.
			 * @param msg - the message to show in the confirm dialog
			 */
			export function cfmClose(msg: string): void {
				zk.confirmClose = msg;
			}
			/**
			 * Shows a notification popup.
			 * @param msg - message to show
			 * @param type - the notification type (warning, info, error)
			 * @param pid - uuid of the page to which it belongs
			 * @param ref - a reference component
			 * @param pos - the position of notification
			 * @param off - the offset of x and y
			 * @param dur - the duration of notification
			 * @param closable - the close button of notification
			 */
			export function showNotification(msg: string, type: zul.wgt.NotificationType, pid: string, ref: zk.Widget,
				pos: string, off: zk.Offset, dur: number, closable: boolean): void {
				var notif = zk.load('zul.wgt') ? zul.wgt.Notification : undefined; // in zul
				if (notif) {
					var opts = {
						ref: ref,
						pos: pos,
						off: off,
						dur: dur,
						type: type,
						closable: closable
					};
					//ZK-2687, show notif after zAu.cmd0.scrollIntoView
					zk.delayFunction(ref ? ref.uuid : 'nouuid', () => {
						notif?.show(msg, pid, opts);
					});
				} else {
					// TODO: provide a hook to customize
					jq.alert(msg); // fall back to alert when zul is not available
				}
			}
			/**
			 * Shows the busy message covering the specified widget.
			 * @param uuid - the component's UUID
			 * @param msg - the message.
			 */
			export function showBusy(uuid: string, msg: string): void
			/**
			 * Shows the busy message covering the whole browser window.
			 * @param msg - the message.
			 */
			export function showBusy(msg: string): void
			export function showBusy(uuid: string, msg?: string): void {
				let uid: string | undefined = uuid;
				if (msg === undefined) {
					msg = uuid;
					uid = undefined;
				}

				zAu.cmd0.clearBusy(uid);

				var w: zk.Widget | undefined = uid ? zk.Widget.$(uid) : undefined;
				if (!uid) {
					zk._prevFocus = zk.currentFocus;
					zUtl.progressbox('zk_showBusy', msg || msgzk.PLEASE_WAIT, true, undefined, { busy: true });
					jq('html').on('keydown', zk.$void);
				} else if (w) {
					zk.delayFunction(uid, function () {
						if (w) {
							(w.effects_!).showBusy = new zk.eff.Mask({
								id: w.uuid + '-shby',
								anchor: w.$n(),
								message: msg
							});
						}
					});
				}
			}
			/**
			 * Removes the busy message covering the specified widget.
			 * @param uuid - the component's UUID
			 */
			export function clearBusy(uuid?: string): void {
				if (uuid) {
					zk.delayFunction(uuid, function () {
						var w = zk.Widget.$(uuid),
							efs = w ? w.effects_ : undefined;
						if (efs && efs.showBusy) {
							efs.showBusy.destroy();
							delete efs.showBusy;
						}
					});
				} else {
					zUtl.destroyProgressbox('zk_showBusy', { busy: true }); //since user might want to show diff msg
					if (zk._prevFocus) {
						zk.currentFocus = zk._prevFocus;
						zk._prevFocus = undefined;
						var wgt = zk.currentFocus;
						try {
							zk._focusByClearBusy = true;
							wgt.focus();
						} finally {
							zk._focusByClearBusy = false;
						}
					}
					jq('html').off('keydown', zk.$void);
				}
			}
			/**
			 * Closes the all error messages related to the specified widgets.
			 * It assumes {@link zk.Widget} has a method called `clearErrorMessage`
			 * (such as {@link zul.inp.InputWidget.clearErrorMessage}).
			 * If no such method, nothing happens.
			 * @param args - any number of UUID of widgets.
			 * @see {@link wrongValue}
			 */
			export function clearWrongValue(...args: string[]): void {
				for (var i = args.length; i--;) {
					var wgt = zk.Widget.$(args[i]);
					if (wgt) {
						var toClearErrMsg = function (w: zk.Widget & { clearErrorMessage?() }) {
							return function () {
								if (w.clearErrorMessage) w.clearErrorMessage();
								else zAu.wrongValue_(w, false);
							};
						};
						zk.delayFunction(wgt.uuid, toClearErrMsg(wgt));
					}
				}
			}
			/**
			 * Shows the error messages for the specified widgets.
			 * It assumes {@link zk.Widget} has a method called `setErrorMessage`
			 * (such as {@link zul.inp.InputWidget#setErrorMessage}).
			 * If no such method, {@link jq.alert} is used instead.
			 * @param args - the widgets and messages. The first argument
			 * is the widget's UUID, and the second is the error message.
			 * The third is UUID, then the fourth the error message, and so on.
			 * @see {@link clearWrongValue}
			 */
			export function wrongValue(...args: string[]): void {
				for (var i = 0, len = args.length - 1; i < len; i += 2) {
					var uuid = args[i], msg = args[i + 1],
						wgt = zk.Widget.$(uuid);
					if (wgt) {
						//ZK-2687: create a closure to record the current wgt
						var toSetErrMsg = function (w: zk.Widget & { setErrorMessage?(m: string) }, m: string) {
							return function () {
								zk.afterAnimate(function () {
									if (w.setErrorMessage) w.setErrorMessage(m);
									else zAu.wrongValue_(w, m);
								}, -1);
							};
						};
						zk.delayFunction(wgt.uuid, toSetErrMsg(wgt, msg));
					} else if (!uuid) //keep silent if component (of uuid) not exist (being detaced)
						jq.alert(msg);
				}
				// for a bug fixed of B60-ZK-1208, we need to delay the func for this test case, B36-2935398.zul
				// has been removed since 7.0.6
			}
			/**
			 * Submit a form.
			 * This method looks for the widget first. If found and the widget
			 * has a method called `submit`, then the widget's `submit`
			 * method is called. Otherwise, it looks for the DOM element
			 * and invokes the `submit` method (i.e., assume it is
			 * the FROM element).
			 * @param id - the UUID of the widget, or the ID of the FORM element.
			 */
			export function submit(id: string): void {
				setTimeout(function () {
					var n = zk.Widget.$(id) as { submit?() };
					if (n && n.submit)
						n.submit();
					else
						zk(id).submit();
				}, 50);
			}
			/**
			 * Scrolls the widget or an DOM element into the view
			 * @param id - the UUID of the widget, or the ID of the DOM element.
			 */
			export function scrollIntoView(id: string): void {
				if (!id) return;
				var w = zk.Widget.$(id);
				if (w) {
					zk.delayFunction(w.uuid, function () {
						w?.scrollIntoView();
					});
				} else {
					var zkjq = zk(id);
					if (zkjq.$()) {
						zk.delayFunction(zkjq.$().uuid, function () {
							zkjq.scrollIntoView();
						});
					}
				}
			}
			/**
			 * Loads a JavaScript file and execute it.
			 * @param url - the JavaScript file path
			 * @param callback - the function to execute after the JavaScript file loaded.
			 * @param once - true means the JavaScript file will be cached.
			 * @since 8.0.0
			 */
			export function loadScript(url: string, callback: CallableFunction | string, once: boolean): void {
				void jq.ajax({
					dataType: 'script',
					cache: once,
					url: url
				}).done(function () {
					if (jq.isFunction(callback)) {
						callback();
					} else
						jq.globalEval(callback);
				});
			}
			/**
			 * @see {@link zk.loadCSS}
			 * @param href - the URL of the CSS file.
			 * @param id - the identifier. Ignored if not specified.
			 * @param media - the media attribute. Ignored if not specified.
			 * @since 8.0.0
			 */
			export import loadCSS = zk.loadCSS;
			/**
			 * Pushes or replaces a history state.
			 * @param replace - if true, it will replace the current history without creating a new one.
			 * @param state - a state object.
			 * @param title - a title for the state. May be ignored by some browsers.
			 * @param url - the new history entry's URL. Ignored if not specified.
			 * @since 8.5.0
			 */
			export function historyState(replace: boolean, state: object, title: string, url: string): void {
				if (replace && window.history.replaceState)
					window.history.replaceState(state, title, url);
				if (!replace && window.history.pushState)
					window.history.pushState(state, title, url);
			}
			/**
			 * Ask the client to sync all the errorboxes and its reference widget position on the desktop.
			 * @since 8.5.2
			 */
			export function syncAllErrorbox(): void {
				jq('.z-errorbox').toArray().forEach(function (ebox) {
					var wgt = jq(ebox).zk.$<zul.inp.Errorbox>();
					wgt.reposition();
					wgt._fixarrow();
				});
			}
		}
		/**
		 * @class zk.AuCmd1
		 * The AU command handler for processes commands related to widgets,
		 * sent from the server.
		 */
		export namespace cmd1 {
			/**
			 * Sets the attribute of a widget.
			 * @param wgt - the widget
			 * @param name - the name of the attribute
			 * @param value - the value of the attribute.
			 */
			export function setAttr(wgt: zk.Widget | undefined, name: string, value: unknown): void {
				if (wgt) { // server push may cause wgt is null in some case - zksandbox/#a1
					if (name == 'z$al') { //afterLoad
						zk.afterLoad(function () {
							for (name in value as object)
								wgt.set(name, ((value as Record<string, unknown>)[name] as CallableFunction)() as never, true); //must be func
						});
					} else {
						// Bug ZK-2281, make sure the wgt is not in the rerendering queue.
						wgt.rerenderNow_(undefined);
						wgt.set(name, value as never, true); //3rd arg: fromServer
					}
				}
			}
			/**
			 * Sets the attributes of a widget.
			 * @param wgt - the widget
			 * @param attrs - an array of [name1, value1, name2, value2, ...]
			 * @since 9.0.1
			 */
			export function setAttrs(wgt: zk.Widget, attrs: unknown[]): void {
				if (wgt) { // server push may cause wgt is null in some case - zksandbox/#a1
					var setAttr = zAu.cmd1.setAttr,
						attrsLength = attrs.length;
					if (attrsLength % 2)
						zk.error('Expected an even length of attrs, but ' + attrsLength + ' found.');
					for (var i = 0; i + 1 <= attrsLength; i += 2)
						setAttr(wgt, attrs[i] as string, attrs[i + 1]);
				}
			}
			/**
			 * Replaces the widget with the widget(s) generated by evaluating the specified JavaScript code snippet.
			 * @param wgt - the old widget to be replaced
			 * @param code - the JavaScript code snippet to generate new widget(s).
			 */
			export function outer(wgt: zk.Widget, code: string): void {
				window.zkx_(code, function (newwgt: zk.Widget) {
					var act = _beforeAction(newwgt, 'invalidate') as never;
					wgt.replaceWidget(newwgt);
					_afterAction(newwgt, act);
				}, function (wx: zk.Widget) {
					for (var w: zk.Widget | undefined = wx; w; w = w.parent)
						if (w == wgt)
							return undefined; //ignore it since it is going to be removed
					return wx;
				});
			}
			/**
			 * Adds the widget(s) generated by evaluating the specified JavaScript code snippet
			 * after the specified widget (as sibling).
			 * @param wgt - the existent widget that new widget(s) will be inserted after
			 * @param the - JavaScript code snippet to generate new widget(s).
			 */
			export function addAft(wgt: zk.Widget, ...codes: string[]): void {
				const p = wgt.parent,
					onSizeChildren: zk.Widget[] = [],
					fn = function (child: zk.Widget): void {
						var act = _beforeAction(child, 'show') as never;
						if (p) {
							p.insertBefore(child, wgt.nextSibling);
							if ((p instanceof zk.Desktop))
								_asBodyChild(child);
							_afterAction(child, act);
						} else {
							var n = wgt.$n();
							if (n)
								jq(n).after(child, wgt.desktop);
							else
								_asBodyChild(child);
							if (!_afterAction(child, act) && !child.z_rod)
								onSizeChildren.push(child);
						}
					};
				for (var args = arguments, j = args.length; --j;)
					window.zkx_(args[j] as never, fn);

				if (p && !p.z_rod)
					zk.afterMount(function () {
						if (zk(p).isRealVisible()) {
							zUtl.fireSized(p);
						} else {
							// call each child instead for ZK-5203
							while (onSizeChildren.length) {
								zUtl.fireSized(onSizeChildren.shift()!);
							}
						}
					}, -1);
			}
			/**
			 * Adds the widget(s) generated by evaluating the specified JavaScript code snippet
			 * before the specified widget (as sibling).
			 * @param wgt - the existent widget that new widget(s) will be inserted before
			 * @param rargs - the JavaScript code snippet to generate new widget(s).
			 */
			export function addBfr(wgt: zk.Widget, ...rargs: string[]): void {
				var p = wgt.parent!,
					onSizeChildren: zk.Widget[] = [],
					fn = function (child: zk.Widget): void {
						var act = _beforeAction(child, 'show') as never;
						p.insertBefore(child, wgt);
						if (!_afterAction(child, act) && !child.z_rod) {
							onSizeChildren.push(child);
						}
					};
				for (let args = arguments, j = 1; j < args.length; ++j)
					window.zkx_(args[j] as never, fn);

				if (p && !p.z_rod)
					zk.afterMount(function () {
						if (zk(p).isRealVisible()) {
							zUtl.fireSized(p);
						} else {
							// call each child instead for ZK-5203
							while (onSizeChildren.length) {
								zUtl.fireSized(onSizeChildren.shift()!);
							}
						}
					}, -1);
			}
			/**
			 * Adds the widget(s) generated by evaluating the specified JavaScript code snippet
			 * as the last child of the specified widget.
			 * @param wgt - the existent widget that will become the parent of new widget(s)
			 * @param rargs - the JavaScript code snippet to generate new widget(s).
			 */
			export function addChd(wgt: zk.Widget, ...rargs: string[]): void {
				if (wgt) {
					const onSizeChildren: zk.Widget[] = [],
						fn = function (child: zk.Widget): void {
							const act = _beforeAction(child, 'show') as never,
								shallFireSizedLater = wgt.shallFireSizedLaterWhenAddChd_();
							wgt.appendChild(child);
							if (!_afterAction(child, act) && !child.z_rod && !shallFireSizedLater) {
								onSizeChildren.push(child);
							}
						};
					for (let args = arguments, j = 1; j < args.length; ++j)
						window.zkx_(args[j] as never, fn);

					if (!wgt.z_rod && !wgt.shallFireSizedLaterWhenAddChd_()) {
						zk.afterMount(function () {
							if (zk(wgt).isRealVisible()) {
								zUtl.fireSized(wgt);
							} else {
								// call each child instead for ZK-5203
								while (onSizeChildren.length) {
									zUtl.fireSized(onSizeChildren.shift()!);
								}
							}
						}, -1);
					}
				} else {
					for (let args = arguments, j = 1; j < args.length; ++j) {
						//possible if <?page complete="true"?>
						window.zkx_(args[j] as never, _asBodyChild);
					}
				}
			}
			/**
			 * Removes the widget.
			 * @param wgt - the widget to remove
			 */
			export function rm(wgt: zk.Widget): void {
				if (wgt) {
					wgt.detach();
					_detached.push(wgt); //used by mount.js
				}
			}
			/**
			 * Rename UUID.
			 * @param wgt - the widget to rename
			 * @param newId - the new UUID
			 * @since 5.0.3
			 */
			export function uuid(wgt: zk.Widget, newId: string): void {
				if (wgt)
					zk._wgtutl.setUuid(wgt, newId); //see widget.js
			}

			/**
			 * Set the focus to the specified widget.
			 * It invokes {@link zk.Widget#focus}. Not all widgets support
			 * this method. In other words, it has no effect if the widget doesn't support it.
			 * @param wgt - the widget.
			 */
			export function focus(wgt: zk.Widget): void {
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
			}
			/**
			 * Selects all text of the specified widget.
			 * It invokes the `select` method, if any, of the widget.
			 * It does nothing if the method doesn't exist.
			 * @param wgt - the widget.
			 * @param start - the starting index of the selection range
			 * @param end - the ending index of the selection range (excluding).
			 * 		In other words, the text between start and (end-1) is selected.
			 */
			export function select(wgt: zk.Widget & { select?(s, e) }, start: number, end: number): void {
				if (wgt.select) wgt.select(start, end);
			}
			/**
			 * Invokes the specified method of the specified widget.
			 * In other words, it is similar to execute the following:
			 * ```ts
			 * wgt[func].apply(wgt, vararg);
			 * ```
			 *
			 * @param wgt - the widget to invoke
			 * @param func - the function name
			 * @param rargs - any number of arguments passed to the function
			 * invocation.
			 */
			export function invoke(wgt: zk.Widget, func: string, ...rargs: unknown[]): void {
				var args: unknown[] = [];
				for (var j = arguments.length; --j > 1;) //exclude wgt and func
					args.unshift(arguments[j]);
				if (wgt)
					(wgt[func] as CallableFunction)(...args);
				else {
					var fn = zk.$import(func) as CallableFunction;
					if (!fn) zk.error('not found: ' + func);
					fn(...args);
				}
			}
			/**
			 * Ask the client to echo back an AU request with the specified
			 * evant name and the target widget.
			 * @param wgt - the target widget to which the AU request will be sent
			 * @param evtnm - the name of the event, such as onUser
			 * @param data - any data
			 * @see zk.AuCmd0#echo
			 * @see zk.AuCmd0#echoGx
			 */
			export function echo2(wgt: zk.Widget, evtnm: string, data: unknown): void {
				zAu.send(new zk.Event(wgt, 'echo',
					data != null ? [evtnm, data] : [evtnm], { ignorable: true }));
			}
			/**
			 * Ask the client to re-cacluate the size of the given widget.
			 * @param wgt - the widget to resize
			 * @since 5.0.8
			 */
			export function resizeWgt(wgt: zk.Widget): void {
				zUtl.fireSized(wgt, 1); //force cleanup
			}
			/**
			 * Ask the client to sync a target widget and its errorbox position.
			 * @param wgt - the widget
			 * @since 8.5.2
			 */
			export function syncErrorbox(wgt: zul.inp.InputWidget): void {
				if (wgt._errbox) {
					wgt._errbox.reposition();
					wgt._errbox._fixarrow();
				}
			}
		}
	}
}

/**
 * Adds a function that will be executed after onResponse events are done.
 * That means, after au responses, the function added in the afterAuResponse() will be invoked
 * @param fn - the function to execute after au responses
 * @since 7.0.6
 */
export function afterAuResponse(fn: () => void): void {
	if (fn)
		_aftAuResp.push(fn);
}
zk.afterAuResponse = afterAuResponse;

export function doAfterAuResponse(): void {
	// Fix ZK-5017, not to use a live array for doAfterAuResponse();
	let backup = _aftAuResp.splice(0, _aftAuResp.length);
	for (var fn: CallableFunction | undefined; fn = backup.shift();) {
		fn();
	}
}
zk.doAfterAuResponse = doAfterAuResponse;

export namespace au_global {
	export function onIframeURLChange(uuid: string, url: string): void { //doc in jsdoc
		if (!zk.unloading) {
			var wgt = zk.Widget.$(uuid);
			if (wgt) wgt.fire('onURIChange', url);
		}
	}
}
zk.copy(window, au_global);