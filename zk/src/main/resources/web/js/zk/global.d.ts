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
import DOMPurify from 'dompurify';

// JQ should only be used in this file. Thus, it is not exported through zk or global.
declare namespace JQ {
	type Event = typeof _JQEvent;
	type EventStatic = typeof _JQEventStatic;
}

declare global {
	// Augmenting JQuery with declaration merging.
	interface JQuery {

		selector?: string; // expose
		/**
		 * The associated instance of {@link zk.JQZK} that
		 * provides additional utilities to <a href="http://docs.jquery.com/Main_Page" target="jq">jQuery</a>.
		 */
		zk: zjq;

		/**
		 * Replaces the match elements with the specified HTML, DOM or {@link Widget}.
		 * We extends <a href="http://docs.jquery.com/Manipulation/replaceWith">jQuery's replaceWith</a>
		 * to allow replacing with an instance of {@link Widget}.
		 * @param widget - a widget
		 * @param desktop - the desktop. It is optional.
		 * @param skipper - the skipper. It is optional.
		 * @returns the jq object matching the DOM element after replaced
		 */
		replaceWith(w: zk.Widget | unknown, desktop: zk.Desktop, skipper: zk.Skipper): this;

		on(type: string, selector: string | undefined, data, fn: CallableFunction, ...rest: unknown[]): JQuery;
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
		/**
		 * Insert content after each of the matched elements.
		 * <p>Notice that this method is extended to handle {@link Widget}.
		 * <p>Refer to <a href="http://docs.jquery.com/Manipulation/after">jQuery documentation</a>
		 * for more information.
		 * @param content - If it is a string, it is assumed to be
		 * a HTML fragment. If it is a widget, the widget will be insert after
		 * @param desktop - the desktop. It is used only
		 * if content is a widget.
		 */
		after(content: Widget, desktop?: Desktop): this;
		/**
		 * Append content to inside of every matched element.
		 * <p>Notice that this method is extended to handle {@link Widget}.
		 * <p>Refer to <a href="http://docs.jquery.com/Manipulation/append">jQuery documentation</a>
		 * for more information.
		 * @param content - If it is a string, it is assumed to be
		 * a HTML fragment. If it is a widget, the widget will be appended
		 * @param desktop - the desktop. It is used only
		 * if content is a widget.
		 */
		append(content: Widget, desktop?: Desktop): this;
		/**
		 * Insert content before each of the matched elements.
		 * <p>Notice that this method is extended to handle {@link Widget}.
		 * <p>Refer to <a href="http://docs.jquery.com/Manipulation/before">jQuery documentation</a>
		 * for more information.
		 * @param content - If it is a string, it is assumed to be
		 * a HTML fragment. If it is a widget, the widget will be insert before
		 * @param desktop - the desktop. It is used only
		 * if content is a widget.
		 */
		before(content: Widget, desktop?: Desktop): this;
		/**
		 * Prepend content to the inside of every matched element.
		 * <p>Notice that this method is extended to handle {@link Widget}.
		 * <p>Refer to <a href="http://docs.jquery.com/Manipulation/prepend">jQuery documentation</a>
		 * for more information.
		 * @param content - If it is a string, it is assumed to be
		 * a HTML fragment. If it is a widget, the widget will be prepended
		 * @param desktop - the desktop. It is used only
		 * if content is a widget.
		 */
		prepend(content: Widget, desktop?: Desktop): this;
		absolutize(): this;

		// Used extensively in zul.mesh.Paging
		attr(attributeName: 'disabled', value: boolean);

		// fix JQuery.unmousewheel() type error.
		unmousewheel(handler: JQueryMousewheel.JQueryMousewheelEventHandler): JQuery;

		/**
		 * Removes all matched elements from the DOM.
		 * <p>Unlike <a href="http://docs.jquery.com/Manipulation/remove">jQuery</a>,
		 * it does nothing if nothing is matched.
		 */
		//remove(): this
		/**
		 * Removes all children of the matched element from the DOM.
		 * <p>Unlike <a href="http://docs.jquery.com/Manipulation/empty">jQuery</a>,
		 * it does nothing if nothing is matched.
		 */
		//empty(): this
		/**
		 * Shows all matched elements from the DOM.
		 * <p>Unlike <a href="http://docs.jquery.com/show">jQuery</a>,
		 * it does nothing if nothing is matched.
		 */
		//show(): this
		/**
		 * Hides all matched elements from the DOM.
		 * <p>Unlike <a href="http://docs.jquery.com/hide">jQuery</a>,
		 * it does nothing if nothing is matched.
		 */
		//hide(): this
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
		/**
		 * An override function that provide a way to get the style value where is
		 * defined in the CSS file or the style object, rather than the computed value.
		 * <p> Note that the function is only applied to the width or height property,
		 *  and the third argument must be 'styleonly'.
		 * <p> For example,
		 * ```ts
		 * jq.css(elem, 'height', 'styleonly');
		 * // or
		 * jq.css(elem, 'width', 'styleonly');
		 * ```
		 * @since 5.0.6
		 * @param elem - a Dom element
		 * @param name - the style name
		 * @param extra - an option in this case, it must be 'styleonly'
		 * @returns the style value.
		 */
		css(elem: Node, name: string, extra: 'styleonly', styles?: CSSStyleDeclaration): number;
		/**
		 * Marshalls the Date object into a string such that it can be sent
		 * back to the server.
		 * <p>It works with `org.zkoss.json.JSONs.d2j()` to transfer data from client
		 * to server.
		 * @param d - the date object to marshall. If null, null is returned
		 * @returns a string
		 * @since 5.0.5
		 */
		d2j(d: Date | DateImpl): string;
		doSyncScroll(): void;
		/**
		 * Decodes a JSON string to a JavaScript object.
		 * <p>It is similar to jq.parseJSON (jQuery's default function), except
		 * 1) it doesn't check if the string is a valid JSON
		 * 2) it uses eval to evaluate
		 * <p>Thus, it might not be safe to invoke this if the string's source
		 * is not trustable (and then it is better to use jq.parseJSON)
		 * @param s - the JSON string
		 * @returns the converted object.
		 */
		evalJSON(s: string): unknown;
		/**
		 * @returns the text-relevant style of the specified style
		 * (same as HTMLs.getTextRelevantStyle in Java).
		 * ```ts
		 * jq.filterTextStyle('width:100px;font-size:10pt;font-weight:bold');
		 * //return 'font-size:10pt;font-weight:bold'
		 * ```
		 *
		 * @param style - the style to filter
		 * @param plus - an array of the names of the additional style to
		 * include, such as `['width', 'height']`. Ignored if not specified or null.
		 */
		filterTextStyle(style: string, plus?: string[]): string;
		/**
		 * @returns the text-relevant style of the specified styles
		 * (same as HTMLs.getTextRelevantStyle in Java).
		 * ```ts
		 * jq.filterTextStyle({width:"100px", fontSize: "10pt"});
		 * //return {font-size: "10pt"}
		 *```
		 *
		 * @param styles - the styles to filter
		 * @param plus - an array of the names of the additional style to
		 * include, such as `['width', 'height']`. Ignored if not specified or null.
		 */
		filterTextStyle(style: Record<string, string>, plus?: string[]): Record<string, string>;
		find<TElement extends HTMLElement = HTMLElement>(html: JQuery.htmlString, ownerDocument_attributes?: Document | JQuery.PlainObject): JQuery<TElement>;
		find<TElement extends Element = HTMLElement>(selector: JQuery.Selector, context?: Element | Document | JQuery | JQuery.Selector): JQuery<TElement>;
		find(element: HTMLSelectElement): JQuery<HTMLSelectElement>;
		find<T extends Element>(element_elementArray: T | ArrayLike<T>): JQuery<T>;
		find<T>(selection: JQuery<T>): JQuery<T>;
		find<TElement = HTMLElement>(callback: ((this: Document, $: JQueryStatic) => void)): JQuery<TElement>;
		find<T extends JQuery.PlainObject>(object: T): JQuery<T>;
		focusOut(): void;
		head(): HTMLElement | undefined;
		innerHeight(): number;
		innerWidth(): number;
		innerX(): number;
		innerY(): number;
		// eslint-disable-next-line zk/noNull
		isAncestor(p: Element | undefined | null, c: Element | undefined | null): boolean;
		/**
		 * @returns if the specified rectangles are overlapped with each other.
		 * @param ofs1 - the offset of the first rectangle
		 * @param dim1 - the dimension (size) of the first rectangle
		 * @param ofs2 - the offset of the second rectangle
		 * @param dim2 - the dimension (size) of the second rectangle
		 * @param tolerant - the tolerant value for the calculation
		 */
		isOverlapped(ofs1: Offset, dim1: Offset, ofs2: Offset, dim2: Offset, tolerant?: number): boolean;
		/**
		 * Unmarshalls the string back to a Date object.
		 * <p>It works with `org.zkoss.json.JSONs.j2d()` to transfer data from server
		 * to client.
		 * @param s - the string that is marshalled at the server
		 * @returns the date object after unmarshalled back
		 * @since 5.0.5
		 */
		j2d(s: string): Date;
		newFrame(id: string, src?: string, style?: string): HTMLIFrameElement;
		newHidden(nm: string, val: string, parent?: Node): HTMLInputElement;
		newStackup(el: Node | undefined, id: string, anchor?: Node): HTMLIFrameElement;
		/**
		 * @returns the node name of the specified element in the lower case.
		 * @param el - the element to test.
		 * If el is null, an empty string is returned.
		 * @since 5.0.1
		 */
		nodeName(el: Node): string;
		/**
		 * @returns if the node name of the specified element is the same
		 * as one of the specified name (case insensitive).
		 * @param el - the element to test
		 * @param tags - the name to test. You can have any number
		 * of names to test, such as `jq.nodeName(el, "tr", "td", "span")`
		 * @since 5.0.1
		 */
		nodeName(el: Node, ...tags: string[]): boolean;
		onSyncScroll(wgt: Widget): void;
		onzsync(obj: ZSyncObject): void;
		parseStyle(style: string): Record<string, string>;
		px(v: number): string;
		px0(v: number | undefined): string;
		scrollbarWidth(): number;
		/**
		 * Encodes a JavaScript object to a JSON string. To decode, use jq.evalJSON(s), where s is a JSON string.
		 *
		 * <p>You can provide an optional replacer method. It will be passed the key and value of each member, with this bound to the containing object. The value that is returned from your method will be serialized. If your method returns undefined, then the member will be excluded from the serialization.
		 * Values that do not have JSON representations, such as undefined or functions, will not be serialized. Such values in objects will be dropped; in arrays they will be replaced with null. You can use a replacer function to replace those with JSON values. JSON.stringify(undefined) returns undefined.
		 * <p>The optional space parameter produces a stringification of the value that is filled with line breaks and indentation to make it easier to read.
		 * <p>If the space parameter is a non-empty string, then that string will be used for indentation. If the space parameter is a number, then the indentation will be that many spaces.
		 * <p>Example:
		 * ```ts
		 * text = jq.toJSON(['e', {pluribus: 'unum'}]);
		 * // text is '["e",{"pluribus":"unum"}]'
		 *
		 * text = jq.toJSON([new Date()], function (key, value) {
		 * 	return this[key] instanceof Date ?
		 * 		'Date(' + this[key] + ')' : value;
		 * });
		 * // text is '["Date(---current time---)"]'
		 * ```
		 * @param obj - any JavaScript object
		 * @param replace - an optional parameter that determines how object values are stringified for objects. It can be a function.
		 */
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

	// eslint-disable-next-line @typescript-eslint/no-require-imports
	export import DOMPurify = require('dompurify');
	export import Moment = moment_global.Moment;
	export import MomentFormatSpecification = moment_global.MomentFormatSpecification;
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
	declare var zDebug: typeof import('./debug/debugger').Debugger;
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
export * as debug from './debug';
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