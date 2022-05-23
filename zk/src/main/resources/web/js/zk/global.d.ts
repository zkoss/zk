/* eslint-disable @typescript-eslint/no-empty-interface */
/* eslint-disable @typescript-eslint/ban-types */
/* eslint-disable one-var */
/* eslint-disable @typescript-eslint/triple-slash-reference */
/* globals.d.ts

	Purpose:
		Type definitions for ZK
	Description:

	History:
		Mon Apr 01 14:39:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/// <reference types="jquery.transit"/>
/// <reference types="jquery-mousewheel"/>

declare namespace zk {

	interface BrowserOptions {
		chrome?: boolean;
		mozilla?: boolean;
		msie?: boolean;
		opera?: boolean;
		safari?: boolean;
		version: string;
		webkit?: boolean;
	}

	interface AlertOptions {
		mode: 'os' | 'modal' | 'embedded' | 'overlapped' | 'popup' | 'highlighted';
		title: string;
		icon: 'QUESTION' | 'EXCLAMATION' | 'INFORMATION' | 'ERROR' | 'none' | string;
		button: string | Record<string, unknown>;
		desktop: Desktop;
	}
}

// declaration merging with ./JQuery.d.ts
interface JQuery {

	selector?: string; // expose
	zk: zk.JQZK;

	on(selector: string, func: Function): this;
	on(selector: string, data: unknown, func: Function): this;
	off(selector: string, func: Function): this;
	off(selector: string, data: unknown, func: Function): this;
	zon<TData>(
		events: JQuery.TypeEventHandlers<HTMLElement, TData, never, never>,
		selector: JQuery.Selector,
		data: TData,
		delegateEventFunc: Function,
		...args: unknown[]
	): this;
	zoff(event?: JQuery.TriggeredEvent<HTMLElement>,
		 selector?: JQuery.Selector,
		delegateEventFunc?: Function,
		...args: unknown[]): this;
	after(widget: zk.Widget, dt?: zk.Desktop): this;
	append(widget: zk.Widget, dt?: zk.Desktop): this;
	before(widget: zk.Widget, dt?: zk.Desktop): this;
	prepend(widget: zk.Widget, dt?: zk.Desktop): this;
	absolutize(): this;
}

declare namespace JQ {
	type Event = typeof import('@zk/dom')._JQEvent;
	type EventStatic = typeof import('@zk/dom')._JQEventStatic;
}

declare namespace JQuery {
	interface Event extends Pick<JQ.Event, 'stop' | 'metaData'> {}
	interface MouseEventBase extends Pick<JQ.Event, 'mouseData'> {}
	interface KeyboardEventBase extends Pick<JQ.Event, 'keyData' | '_keyDataKey'> {}
	interface EventStatic extends JQ.EventStatic {
		<T extends object>(event: string | UIEvent, properties?: T): TriggeredEvent & T;
	}
	interface Effects {
		speeds: Record<string, number>;
	}
}

interface JQueryStatic {
	borders: {l: string; r: string; t: string; b: string};
	browser: zk.BrowserOptions;
	margins: {l: string; r: string; t: string; b: string};
	paddings: {l: string; r: string; t: string; b: string};
	isReady: boolean; // expose jQuery undocumented property
	
	<U, T extends HTMLElement = HTMLElement>(selector: T | U, zk: ZKStatic): JQuery<T>;
	
	$$(id: '', subId?: string): null;
	$$(id: string, subId?: string): NodeList;
	$$<T>(id: T, subId?: string): T;
	alert(msg: string, opts?: Partial<zk.AlertOptions>): void;
	clearSelection(): boolean;
	confirm(msg: string): boolean;
	css(elem: Node, name: string): string;
	css(elem: Node, name: string, numeric: true): number;
	css(elem: Node, name: string, extra: 'styleonly', styles?: CSSStyleDeclaration): number;
	d2j(d: Date | DateImpl): string;
	doSyncScroll(): void;
	evalJSON(s: string): unknown;
	filterTextStyle(style: string, plus?: string[]): string;
	filterTextStyle(style: {[key: string]: unknown}, plus?: string[]): {[key: string]: unknown};
	focusOut(): void;
	head(): HTMLElement | null;
	innerHeight(): number;
	innerWidth(): number;
	innerX(): number;
	innerY(): number;
	isAncestor(p: HTMLElement | null | undefined, c: HTMLElement | null | undefined): boolean;
	isOverlapped(ofs1: zk.Offset, dim1: zk.Offset, ofs2: zk.Offset, dim2: zk.Offset, tolerant?: number): boolean;
	j2d(s: string): Date;
	newFrame(id: string, src?: string, style?: string | null): HTMLIFrameElement;
	newHidden(nm: string, val: string, parent?: Node): HTMLInputElement;
	newStackup(el: Node | null, id: string, anchor?: Node): HTMLIFrameElement;
	nodeName(el: Node): string;
	nodeName(el: Node, ...tag: string[]): boolean;
	onSyncScroll(wgt): void;
	onzsync(obj): void;
	parseStyle(style: string): {[key: string]: string};
	px(v: number): string;
	px0(v: number | null | undefined): string;
	scrollbarWidth(): number;
	toJSON(obj, replace?: (key, value) => unknown): string;
	uaMatch(ua: string): { browser: string; version: string };
	unSyncScroll(wgt): void;
	unzsync(obj): void;
	zsync(org): void;
}

declare var jq: JQueryStatic;

interface String {
	$camel(): string;
	$inc(diff: number): string;
	$sub(cc: string): number;
}

interface Array<T> {
	$indexOf(o: T): number;
	$contains(o: T): boolean;
	$equals(o: Record<string, unknown>): boolean;
	$remove(o: T): boolean;
	$addAll(o: T[]): number;
	$clone(): T[];
}

type Widget = import('@zk/widget').Widget;
type EventOptions = import('@zk/evt').EventOptions;
type Moment = import('moment-timezone').Moment;
type DateImpl = import('@zk/dateimpl').DateImpl;

interface Window {
	zkservice: typeof import('@zk/widget').zkservice;
	zkopt: typeof import('@zk/widget').zkopt;
	onIframeURLChange: typeof import('@zk/au').onIframeURLChange;
	zkpe: import('@zk/zk').$void;
	zkpi(nm: string, wv: boolean): Record<string, unknown> | null;
	zkpb(pguid: string, dtid: string, contextURI: string, updateURI: string, resourceURI: string, reqURI: string, props: Record<string, string>): void;
	zkver(ver: string, build: string, ctxURI: string, updURI: string, modVers: Record<string, string>, opts: Record<string, unknown>): void;
	zkmld(wgtcls: Record<string, unknown>, molds: Record<string, (() => void)>): void;
	zkamn(pkg: string, fn: (() => void)): void;
	DateImpl: typeof import('@zk/dateimpl').DateImpl;
	Dates: {
		newInstance(param?: number | DateImpl | Parameters<DateConstructor['UTC']>, tz?: string): DateImpl;
	};

	// assigned in ./mount
	zkdt: typeof import('@zk/mount').zkdt;
	zkx: typeof import('@zk/mount').zkx;
	zkx_: typeof import('@zk/mount').zkx_;
	zkmx: typeof import('@zk/mount').zkmx;
	zkmb: typeof import('@zk/mount').zkmb;
	zkme: typeof import('@zk/mount').zkme;
	zkdh: typeof import('@zk/mount').zkdh;
}
declare var zkac: typeof import('@zk/mount').zkac;
declare function $eval(x: string): unknown;
declare var zkreg: typeof import('@zk/widget').zkreg;
declare var zDebug: import('@zk/zk').ZKObject;
declare var zAu: typeof import('@zk/au').default;
declare var msgzk: Record<
	| 'NOT_FOUND'
	| 'UNSUPPORTED'
	| 'FAILED_TO_SEND'
	| 'FAILED_TO_RESPONSE'
	| 'FAILED_TO_PARSE_RESPONSE'
	| 'TRY_AGAIN'
	| 'UNSUPPORTED_BROWSER'
	| 'ILLEGAL_RESPONSE'
	| 'FAILED_TO_PROCESS'
	| 'GOTO_ERROR_FIELD'
	| 'PLEASE_WAIT'
	| 'FILE_SIZE'
	| 'BYTES'
	| 'KBYTES'
	| 'MBYTES'
	| 'GBYTES'
	| 'TBYTES'
	| 'FAILED_TO_LOAD'
	| 'FAILED_TO_LOAD_DETAIL'
	| 'CAUSE'
	| 'LOADING'
	, string>;
declare var zWatch: import('@zk/evt').ZWatch;
declare var zUtl: import('@zk/utl').ZUtl;
declare var zKeys: typeof import('@zk/keys').zKeys;

declare namespace zk {
	type Object = import('@zk/zk').ZKObject;
	type Offset = import('@zk/types').Offset;
	type Desktop = import('@zk/widget').Desktop;
	type Widget = import('@zk/widget').Widget;
	type BigDecimal = import('@zk/math').BigDecimal;
	type JQZK = import('@zk/dom').JQZK;

	interface Websocket {
		readonly ready: boolean;

		encode(j: number, aureq, dt: zk.Desktop): Record<string, unknown>;
		send(reqInf: import('@zk/au').AuRequestInfo): void;
		setRequestHeaders(key: string, value: string): void;
	}

	interface ZKBind {
		$(n: string | HTMLElement | Event | JQuery.Event, opts?: BinderOptions): Binder | null;
		Binder: BinderStatic;
	}

	interface BinderStatic {
		new (wgt: Widget, target): Binder;
		postCommand(dom: HTMLElement, command: string, args?: Record<string, unknown> | null,
			opts?: Partial<EventOptions> | null, timeout?: number): void;
		postGlobalCommand(dom: HTMLElement, command: string, args?: Record<string, unknown> | null,
			opts?: Partial<EventOptions> | null, timeout?: number): void;
	}

	interface BinderOptions {
		exact?: boolean;
		strict?: boolean;
		child?: boolean;
	}

	interface Binder extends zk.Object {
		after(cmd: string, fn: (args?: Record<string, unknown>) => void): this;
		unAfter(cmd: string, fn: (args?: Record<string, unknown>) => void): this;
		destroy(): void;
		command(cmd: string, args?: Record<string, unknown> | null, opts?: Partial<EventOptions> | null, timeout?: number): this;
		globalCommand(cmd: string, args?: Record<string, unknown> | null, opts?: Partial<EventOptions> | null, timeout?: number): this;
		upload(cmd: string, file: File): void;
	}
}

declare var zkex: Record<string, unknown>;
declare var zkmax: Record<string, unknown>;
declare var zWs: zk.Websocket;
declare var zkbind: zk.ZKBind;

type ZKStatic = typeof import('@zk/zk').default;
declare var zk: ZKStatic;

declare namespace zul {
	namespace wgt {
		class Image {} // ./widget
		class Caption {} // ./flex
		// ./au
		class Popup extends zk.Widget {
			public reposition(): void
		}
		class Notification extends Popup {
			public static show(msg: string, pid: string, opts: Record<string, unknown>): void
		}
		class Errorbox extends Notification {
			public _fixarrow(): void
		}
	}
	namespace mesh {
		class HeaderWidget {} // ./flex
		class Auxheader {} // ./flex
	}
	namespace wnd {
		class Panelchildren {} // ./flex
	}
	namespace sel {
		class ItemWidget {} // ./dom
		// ./domtouch
		class Listitem {}
		class Treerow {}
	}
	// ./domtouch
	namespace grid {
		class Row {}
	}
}