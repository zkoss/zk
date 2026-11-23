/* zAu.d.ts

	Purpose:
		Type definitions for ZK
	Description:

	History:
		Mon Apr 01 14:39:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
declare namespace zk {
    type ErrorHandler = (response: Response, errCode: number) => boolean;
    type AjaxErrorHandler = (req: Response, status: number, statusText: string, ajaxReqTries?: number | null) => number;

    interface AuCommand {
        cmd: string;
        data: any; // command-specific
    }

    interface AuCommands extends Array<AuCommand> {
        rid?: number;
        dt?: zk.Desktop | null;
        pfIds?: string | null;
        rtags?: Record<string, unknown>;
    }

    interface AuRequestInfo {
        sid: number;
        uri: string;
        dt: zk.Desktop;
        content: string;
        implicit: boolean;
        ignorable: boolean;
        tmout: number;
        rtags: Record<string, unknown>;
        forceAjax: boolean;
    }

    interface AUEngine {
        ajaxReq: boolean | null;
        ajaxReqInf: zk.AuRequestInfo | null;
        ajaxReqTries: number | null;
        ajaxReqMaxCount: number;
        ajaxSettings: JQuery.AjaxSettings;
        ajaxErrorHandler?: AjaxErrorHandler;
        cmd0: Record<string, Function>;
        cmd1: Record<string, Function>;
        disabledRequest?: boolean;
        doAfterProcessWgts?: zk.Widget[] | null;
        doneTime?: number;
        pendingReqInf: zk.AuRequestInfo | null;
        processPhase?: string | null;
        sentTime?: number;
        seqId: number;
        _cInfoReg?: boolean;
        _clientInfo?: Array<unknown> | null;
        _errCode: string | number | null;
        _errURIs: Record<string, string>;

        _doCmds(sid?: number): void;
        _fetch(input: RequestInfo, init?: RequestInit): Promise<Response>;
        _pfdone(dt: zk.Desktop, pfIds?: string | null): void;
        _pfrecv(dt: zk.Desktop, pfIds?: string | null): void;
        _pfsend(dt: zk.Desktop, fetchOpts: RequestInit, completeOnly: boolean, forceAjax: boolean): void;
        _onClientInfo(): void;
        _onResponseReady(response: Response): void;
        _onVisibilityChange(): void;
        _respException(response: Response, reqInf: zk.AuRequestInfo, e: Error): boolean;
        _respFailure(response: Response, reqInf: zk.AuRequestInfo, rstatus: number): boolean;
        _respSuccess(response: Response, reqInf: zk.AuRequestInfo, sid: number): boolean;
        _rmDesktop(dt: zk.Desktop, dummy?: boolean): void;
        _rmDesktopAjax(url: string, data: string, headers: Record<string, string>): void;
        _resetTimeout(): void;
        _storeStub(wgt: zk.Widget): void;
        _wgt$(uuid: string): zk.Widget;
        addAuRequest(dt: zk.Desktop, aureq: Event): void;
        afterResponse(sid?: number): void;
        ajaxReqResend(reqInf: zk.AuRequestInfo, timeout?: number): void;
        beforeSend(uri: string, aureq: Event, dt?: zk.Desktop): string;
        confirmRetry(msgCode: string, msg2?: string): boolean;
        createWidgets(codes: any[], fn: (wgts: zk.Widget[]) => void, filter?: (wgt: zk.Widget) => zk.Widget | null): void;
        doCmds(dtid: string, rs: any[]): void;
        encode(j: number, aureq: Event, dt: zk.Desktop): string;
        getAuRequests(dt: zk.Desktop): Event[];
        getErrorURI(code: number): string;
        getPushErrorURI(code: number): string;
        onError(fn: ErrorHandler): void;
        onResponseError(response: Response, errCode: number): boolean;
        pfAddIds(dt: zk.Desktop, prop: string, pfIds?: string | null): void;
        pfGetIds(response: Response): string | null;
        process(cmd: string, data: string): void;
        processing(): boolean;
        pushReqCmds(reqInf: zk.AuRequestInfo, response: Response): boolean;
        send(aureq: Event, timeout?: number): void;
        sendAhead(aureq: Event, timeout?: number): void;
        sendNow(dt: zk.Desktop): boolean;
        setErrorURI(code: number, uri: string): void;
        setErrorURI(errors: {[code: number]: string}): void;
        setPushErrorURI(code: number, uri: string): void;
        setPushErrorURI(errors: {[code: number]: string}): void;
        shallIgnoreESC(): boolean;
        showError(msgCode: string, msg2?: string | null, cmd?: string | null, ex?: Error): void;
        toJSON(target: zk.Widget, data: any): string;
        unError(fn: ErrorHandler): void;
        wrongValue_(wgt: zk.Widget, msg: string | false): void;
    }
}

interface Response {
    responseText: string; // for compatibility
    abort?: () => void;
}

declare var zAu: zk.AUEngine;
