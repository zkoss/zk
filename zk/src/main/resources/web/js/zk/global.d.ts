/* eslint-disable one-var, @typescript-eslint/triple-slash-reference */
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

// The following imports are for the use in this file only, as `zk.*` (where `*`
// is re-exported under `export as namespace zk`) won't be visible in this file.
// The reason `zk.Object` can be found in this file is due to it being exported
// through `namespace _zk` in `zk.ts` and then re-exported through `export = zk`
// here.
import { Desktop, Widget } from './widget';
import { _JQEvent, _JQEventStatic } from './dom';
import { anima_global } from './anima';
import { dateImpl_global } from './dateImpl';
import { widget_global } from './widget';
import { au_global, AuRequestInfo } from './au';
import { mount_global } from './mount';
import { flex_global } from './flex';
import { evt_global } from './evt';
import { utl_global } from './utl';
import { keys_global } from './keys';
import * as websocket_global from '@zkmax/websocket';
import moment_global from 'moment-timezone';

// JQ should only be used in this file. Thus, it is not exported through zk or global.
declare namespace JQ {
	type Event = typeof _JQEvent;
	type EventStatic = typeof _JQEventStatic;
}

declare global {
	// Augmenting JQuery with declaration merging.
	interface JQuery {

		selector?: string; // expose
		zk: zjq;

		on(selector: string, func: CallableFunction): this;
		on(selector: string, data: unknown, func: CallableFunction): this;
		on(type: string, selector: string | undefined, data: unknown, fn: CallableFunction, ...rest: unknown[]): this;
		off(selector: string, func: CallableFunction): this;
		off(selector: string, data: unknown, func: CallableFunction): this;
		off(type: string, selector: unknown, fn: unknown, ...rest: unknown[]): this;
		zon<TData>(
			events: JQuery.TypeEventHandlers<HTMLElement, TData, never, never> | string,
			selector?: JQuery.Selector,
			data: TData,
			delegateEventFunc: CallableFunction,
			...args: unknown[]
		): this;
		zoff(event?: JQuery.TriggeredEvent<HTMLElement> | string,
			selector?: JQuery.Selector,
			delegateEventFunc?: CallableFunction,
			...args: unknown[]): this;
		after(widget: Widget, dt?: Desktop): this;
		append(widget: Widget, dt?: Desktop): this;
		before(widget: Widget, dt?: Desktop): this;
		prepend(widget: Widget, dt?: Desktop): this;
		absolutize(): this;

		// Used extensively in zul.mesh.Paging
		attr(attributeName: 'disabled', value: boolean);

		// fix JQuery.unmousewheel() type error.
		unmousewheel(handler: JQueryMousewheel.JQueryMousewheelEventHandler): JQuery;
	}

	declare namespace JQuery {
		interface Event extends Pick<JQ.Event, 'stop' | 'metaData'> { }
		interface MouseEventBase extends Pick<JQ.Event, 'mouseData'> { }
		interface KeyboardEventBase extends Pick<JQ.Event, 'keyData' | '_keyDataKey'> { }
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
		borders: { l: string; r: string; t: string; b: string };
		browser: BrowserOptions;
		margins: { l: string; r: string; t: string; b: string };
		paddings: { l: string; r: string; t: string; b: string };
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

		alert(msg: string, opts?: AlertOptions): void;
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
		isOverlapped(ofs1: Offset, dim1: Offset, ofs2: Offset, dim2: Offset, tolerant?: number): boolean;
		j2d(s: string): Date;
		newFrame(id: string, src?: string, style?: string): HTMLIFrameElement;
		newHidden(nm: string, val: string, parent?: Node): HTMLInputElement;
		newStackup(el: Node | undefined, id: string, anchor?: Node): HTMLIFrameElement;
		nodeName(el: Node): string;
		nodeName(el: Node, ...tags: string[]): boolean;
		onSyncScroll(wgt: Widget): void;
		onzsync(obj: ZSyncObject): void;
		parseStyle(style: string): Record<string, string>;
		px(v: number): string;
		px0(v: number | undefined): string;
		scrollbarWidth(): number;
		toJSON(obj, replace?: (key, value) => unknown): string;
		uaMatch(ua: string): { browser: string; version: string };
		unSyncScroll(wgt: Widget): void;
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

	type ZKStatic = typeof zk;

	export import Moment = moment_global.Moment;
	export import DateImpl = dateImpl_global.DateImpl;
	export import Dates = dateImpl_global.Dates;

	export import zkservice = widget_global.zkservice;
	export import zkopt = widget_global.zkopt;
	export import zkreg = widget_global.zkreg;

	interface Window {
		onIframeURLChange?: typeof au_global.onIframeURLChange;
	}
	export import zAu = au_global.zAu;

	export import zkpi = mount_global.zkpi;
	export import zkpb = mount_global.zkpb;
	export import zkpe = mount_global.zkpe;
	export import zkver = mount_global.zkver;
	export import zkmld = mount_global.zkmld;
	export import zkamn = mount_global.zkamn;
	export import zkdt = mount_global.zkdt;
	export import zkx = mount_global.zkx;
	export import zkx_ = mount_global.zkx_;
	export import zkac = mount_global.zkac;
	export import zkmx = mount_global.zkmx;
	export import zkmb = mount_global.zkmb;
	export import zkme = mount_global.zkme;
	export import zkdh = mount_global.zkdh;

	export import zFlex = flex_global.zFlex;
	export import zWatch = evt_global.zWatch;
	export import zUtl = utl_global.zUtl;
	export import zKeys = keys_global.zKeys;
	export import zjq = anima_global.zjq;

	declare function $eval<T>(x: unknown): T | undefined;
	declare var zDebug: zk.Object;
	export import zWs = websocket_global.zWs;

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
}

export as namespace zk;
export = zk;
import { default as zk } from './zk';
export * as mm from 'moment';

export * from '.';
export * from './zswipe'; // Only required in `zk/index.ts` if `zk.touchEnabled` is true.
export * as canvas from './canvas';
export * as cpsp from './cpsp';
export * as fmt from './fmt';
export * as gapi from './gapi';
export * as wgt from './wgt';
export * as xml from './xml';
export * as zuml from './zuml';

export interface BrowserOptions {
	chrome?: boolean;
	mozilla?: boolean;
	msie?: boolean;
	opera?: boolean;
	safari?: boolean;
	version: string;
	webkit?: boolean;
}

export interface AlertOptions {
	mode?: 'os' | 'modal' | 'embedded' | 'overlapped' | 'popup' | 'highlighted';
	title?: string;
	icon?: 'QUESTION' | 'EXCLAMATION' | 'INFORMATION' | 'ERROR' | 'none' | string;
	button?: string | Record<string, unknown>;
	desktop?: Desktop;
}

export interface LocalizedSymbols {
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
export namespace LocalizedSymbols {
	export interface ErasElementType {
		firstYear: number;
		direction: number;
	}
}

export type Offset = [number, number];
export type { FlexOrient, FlexSize } from './flex';