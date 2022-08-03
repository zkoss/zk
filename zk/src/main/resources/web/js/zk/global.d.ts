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

	on(selector: string, func: CallableFunction): this;
	on(selector: string, data: unknown, func: CallableFunction): this;
	off(selector: string, func: CallableFunction): this;
	off(selector: string, data: unknown, func: CallableFunction): this;
	zon<TData>(
		events: JQuery.TypeEventHandlers<HTMLElement, TData, never, never>,
		selector: JQuery.Selector,
		data: TData,
		delegateEventFunc: CallableFunction,
		...args: unknown[]
	): this;
	zoff(event?: JQuery.TriggeredEvent<HTMLElement>,
		 selector?: JQuery.Selector,
		delegateEventFunc?: CallableFunction,
		...args: unknown[]): this;
	after(widget: zk.Widget, dt?: zk.Desktop): this;
	append(widget: zk.Widget, dt?: zk.Desktop): this;
	before(widget: zk.Widget, dt?: zk.Desktop): this;
	prepend(widget: zk.Widget, dt?: zk.Desktop): this;
	absolutize(): this;

	// Used extensively in zul.mesh.Paging
	attr(attributeName: 'disabled', value: boolean);

	// fix JQuery.unmousewheel() type error.
	unmousewheel(handler: JQueryMousewheel.JQueryMousewheelEventHandler): JQuery;
}

declare namespace JQ {
	type Event = typeof import('./dom')._JQEvent;
	type EventStatic = typeof import('./dom')._JQEventStatic;
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

	interface EventExtensions {
		/*
		fix: function(originalEvent) {
			return originalEvent[jQuery.expando] ?
				originalEvent :
				new jQuery.Event(originalEvent);
		},
		*/
		fix(originalEvent: globalThis.Event): JQuery.TriggeredEvent;

		/* JS source code copied from https://stackoverflow.com/a/50344996
		jQuery.event.mouseHooks = {
			props: ( "button buttons clientX clientY fromElement offsetX offsetY " +
				"pageX pageY screenX screenY toElement" ).split( " " ),
			filter: function( event, original ) {
				var body, eventDoc, doc,
					button = original.button,
					fromElement = original.fromElement;

				// Calculate pageX/Y if missing and clientX/Y available
				if ( event.pageX == null && original.clientX != null ) {
					eventDoc = event.target.ownerDocument || document;
					doc = eventDoc.documentElement;
					body = eventDoc.body;

					event.pageX = original.clientX +
						( doc && doc.scrollLeft || body && body.scrollLeft || 0 ) -
						( doc && doc.clientLeft || body && body.clientLeft || 0 );
					event.pageY = original.clientY +
						( doc && doc.scrollTop  || body && body.scrollTop  || 0 ) -
						( doc && doc.clientTop  || body && body.clientTop  || 0 );
				}

				// Add relatedTarget, if necessary
				if ( !event.relatedTarget && fromElement ) {
					event.relatedTarget = fromElement === event.target ?
						original.toElement :
						fromElement;
				}

				// Add which for click: 1 === left; 2 === middle; 3 === right
				// Note: button is not normalized, so don't use it
				if ( !event.which && button !== undefined ) {
					event.which = ( button & 1 ? 1 : ( button & 2 ? 3 : ( button & 4 ? 2 : 0 ) ) );
				}

				return event;
			}
		};
		*/
		mouseHooks: {
			props: string[];
			filter<T extends JQuery.Event>(event: T, original: globalThis.Event): T;
		};
	}
}

interface ZSyncObject {
	zsync(opts?: zk.Object): void;
}

interface JQueryStatic {
	borders: {l: string; r: string; t: string; b: string};
	browser: zk.BrowserOptions;
	margins: {l: string; r: string; t: string; b: string};
	paddings: {l: string; r: string; t: string; b: string};
	isReady: boolean; // expose jQuery undocumented property

	<U, T extends HTMLElement = HTMLElement>(selector: T | U, zk: ZKStatic): JQuery<T>;
	/* This overload above delegates to the following two. Separately, they can't accept an otherwise valid first argument of union type: `string | HTMLElement`.
	 * <TElement extends HTMLElement = HTMLElement>(html: JQuery.htmlString, ownerDocument_attributes?: Document | JQuery.PlainObject): JQuery<TElement>;
	 * <T extends Element>(element_elementArray: T | ArrayLike<T>): JQuery<T>;
	 */
	<T extends HTMLElement = HTMLElement>(html?: string | T): JQuery<T>;

	// Accept "readonly" array.
	inArray<T>(value: T, array: readonly T[], fromIndex?: number): number;

	// NodeListOf<HTMLElement> is not the same as NodeList. NodeList will give incorrect "this" type
	// for the <callback> of `jq(<selector>).each(<callback>)` in `zul.mesh.Paging._callWgtDoAfterGo`.
	// Furthermore, `document.getElementsByName` is defined to return `NodeListOf<HTMLElement>`.
	// Also, we shouldn't rule out the possibility of returning null in this overload, for an
	// empty string of type "string" will not match an empty string of type "empty string literal".
	// See this demo: https://bit.ly/3n7R7p2
	$$(id: string, subId?: string): NodeListOf<HTMLElement> | undefined;

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
	filterTextStyle(style: Record<string, string>, plus?: string[]): Record<string, string>;
	focusOut(): void;
	head(): HTMLElement | undefined;
	innerHeight(): number;
	innerWidth(): number;
	innerX(): number;
	innerY(): number;
	isAncestor(p: HTMLElement | undefined, c: HTMLElement | undefined): boolean;
	isOverlapped(ofs1: zk.Offset, dim1: zk.Offset, ofs2: zk.Offset, dim2: zk.Offset, tolerant?: number): boolean;
	j2d(s: string): Date;
	newFrame(id: string, src?: string, style?: string): HTMLIFrameElement;
	newHidden(nm: string, val: string, parent?: Node): HTMLInputElement;
	newStackup(el: Node | undefined, id: string, anchor?: Node): HTMLIFrameElement;
	nodeName(el: Node): string;
	nodeName(el: Node, ...tags: string[]): boolean;
	onSyncScroll(wgt: zk.Widget): void;
	onzsync(obj: ZSyncObject): void;
	parseStyle(style: string): {[key: string]: string};
	px(v: number): string;
	px0(v: number | undefined): string;
	scrollbarWidth(): number;
	toJSON(obj, replace?: (key, value) => unknown): string;
	uaMatch(ua: string): { browser: string; version: string };
	unSyncScroll(wgt: zk.Widget): void;
	unzsync(obj: ZSyncObject): void;
	zsync(org: zk.Object): void;
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
	$equals(o: unknown[] | unknown): boolean;
	$remove(o: T): boolean;
	$addAll(o: T[]): number;
	$clone(): T[];
}

type Moment = import('moment-timezone').Moment;
type DateImpl = import('./dateImpl').DateImpl; // exposed under globalThis, see the end of zk/dateImpl
declare var DateImpl: typeof import('./dateImpl').DateImpl; // assigned at the end of zk/dateImpl
declare var Dates: typeof import('./dateImpl').Dates;

interface Window {
	zkservice: typeof import('./widget').zkservice;
	zkopt: typeof import('./widget').zkopt;
	onIframeURLChange: typeof import('./au').onIframeURLChange;
	zkpe: import('./zk').$void;
	zkpi(nm: string, wv: boolean): Record<string, unknown> | undefined;
	zkpb(pguid: string, dtid: string, contextURI: string, updateURI: string, resourceURI: string, reqURI: string, props: Record<string, string>): void;
	zkver(ver: string, build: string, ctxURI: string, updURI: string, modVers: Record<string, string>, opts: Record<string, unknown>): void;
	zkmld(wgtcls: Record<string, unknown>, molds: Record<string, (() => void)>): void;
	zkamn(pkg: string, fn: (() => void)): void;

	// assigned in ./mount
	zkdt: typeof import('./mount').zkdt;
	zkx: typeof import('./mount').zkx;
	zkx_: typeof import('./mount').zkx_;
	zkmx: typeof import('./mount').zkmx;
	zkmb: typeof import('./mount').zkmb;
	zkme: typeof import('./mount').zkme;
	zkdh: typeof import('./mount').zkdh;
}
declare var zjq: typeof import('./anima').JQZKEx;
declare var zkac: typeof import('./mount').zkac;
declare function $eval(x: string): unknown;
declare var zkreg: typeof import('./widget').zkreg;
declare var zDebug: import('./zk').ZKObject;
declare var zAu: typeof import('./au').zAu;
declare var zFlex: typeof import('./flex').zFlex;
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
declare var zWatch: typeof import('./evt').zWatch;
declare var zUtl: typeof import('./utl').zUtl;
declare var zKeys: typeof import('./keys').zKeys;

declare namespace zk {
	type Offset = [number, number];

	type Object = import('./zk').ZKObject;
	type Desktop = import('./widget').Desktop;
	type Widget = import('./widget').Widget;
	type Service = import('./widget').Service;
	type BigDecimal = import('./math').BigDecimal;
	type JQZK = import('./anima').JQZKEx;

	type Event<TData = unknown> = import('./evt').Event<TData>;
	type Skipper = import('./widget').Skipper;
	type Dimension = import('./dom').Dimension;
	type PositionOptions = import('./dom').PositionOptions;
	type SlideOptions = import('./dom').SlideOptions;
	type ZWatchController = import('./evt').ZWatchController;
	type FireOptions = import('./evt').FireOptions;
	type EventOptions = import('./evt').EventOptions;
	type DomAttrsOptions = import('./widget').DomAttrsOptions;
	type DomClassOptions = import('./widget').DomClassOptions;
	type DomStyleOptions = import('./widget').DomStyleOptions;
	type DomVisibleOptions = import('./widget').DomVisibleOptions;
	type RealVisibleOptions = import('./widget').RealVisibleOptions;
	type MinFlexInfo = import('./widget').MinFlexInfo;
	type EventMetaData = import('./dom').EventMetaData;
	type EventMouseData = import('./dom').EventMouseData;
	type EventKeyData = import('./dom').EventKeyData;
	type FlexOrient = import('./flex').FlexOrient;
	type FlexSize = import('./flex').FlexSize;
	type Draggable = import('./drag').Draggable;
	type DraggableOptions = import('./drag').DraggableOptions;
	type Long = import('./math').Long;
	type Swipe = import('./zswipe').Swipe;
	namespace eff {
		type Mask = import('./effect').Mask;
		type FullMask = import('./effect').FullMask
		type Shadow = import('./effect').Shadow;
		type Effect = import('./effect').Effect;
		type EffectStackupOptions = import('./effect').EffectStackupOptions;
		type KeyboardTrap = import('./effect').KeyboardTrap;
	}
	namespace fmt {
		type Calendar = typeof zk.fmt.Calendar;
	}

	interface Websocket {
		readonly ready: boolean;

		encode(j: number, aureq, dt: zk.Desktop): Record<string, unknown>;
		send(reqInf: import('./au').AuRequestInfo): void;
		setRequestHeaders(key: string, value: string): void;
	}

	interface LocalizedSymbols {
		APM?: string[];
		DECIMAL?: string;
		DOW_1ST?: number;
		ERA?: string;
		ERAS?: Record<string, LocalizedSymbols.ErasElementType>;
		FDOW?: string[];
		FMON?: string[];
		GROUPING?: string;
		LAN_TAG?: string;
		MINDAYS?: number;
		MINUS?: string;
		PER_MILL?: string;
		PERCENT?: string;
		S2DOW?: string[];
		S2MON?: never;
		SDOW?: string[];
		SMON?: string[];
		YDELTA?: number;
	}
	namespace LocalizedSymbols {
		export interface ErasElementType {
			firstYear: number;
			direction: number;
		}
	}
	namespace cpsp {
		type SPush = import('cpsp/index').SPush;
	}
}

declare var zWs: zk.Websocket;

type ZKStatic = typeof import('./zk').default;
declare var zk: ZKStatic;