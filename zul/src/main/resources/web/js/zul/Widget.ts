/* Widget.ts

	Purpose:

	Description:

	History:
		Fri Nov  7 17:14:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The ZUL widgets and utilities
 */
//zk.$package('zul');

export interface PopupParams {
	id?: string;
	position?: string;
	delay?: number;
	ref?: zul.wgt.Ref;
	x?: number;
	y?: number;
	type?: string;
}

//Tooltip
var _tt_inf: {
	tip: zul.wgt.Popup;
	ref: zul.Widget;
	params: PopupParams;
	timer: number;
} | undefined,
	_tt_tmClosing: number | undefined,
	_tt_tip: zul.wgt.Popup | undefined,
	_tt_ref: zul.Widget | undefined;
function _tt_beforeBegin(ref: zul.Widget): boolean {
	if (_tt_tip && !_tt_tip.isOpen()) { //closed by other (such as clicking on menuitem)
		_tt_clearOpening_();
		_tt_clearClosing_();
		_tt_tip = _tt_ref = undefined;
	}

	var overTip = _tt_tip && zUtl.isAncestor(_tt_tip, ref);
	if (overTip) _tt_clearClosing_(); //not close tip if over tip
	return !overTip;//disable tip in tip
}
function _tt_begin(tip: zul.wgt.Popup, ref: zul.Widget, params: PopupParams, event: zk.Event): void {
	if (_tt_tip != tip || _tt_ref != ref) {
		_tt_close_();
		_tt_inf = {
			tip: tip, ref: ref, params: params,
			timer: setTimeout(function () {_tt_open_(event);}, params.delay !== undefined ? params.delay : zk.tipDelay)
		};
	} else
		_tt_clearClosing_();
}
function _tt_end(ref: zul.Widget): void {
	if (_tt_ref == ref || (_tt_tip as unknown as zul.Widget) == ref) {
		_tt_clearClosing_(); //just in case
		_tt_tmClosing = setTimeout(_tt_close_, 100);
		//don't cloes immediate since user might move from ref to toolip
	} else
		_tt_clearOpening_();
}
function _tt_clearOpening_(): void {
	var inf = _tt_inf;
	if (inf) {
		_tt_inf = undefined;
		clearTimeout(inf.timer);
	}
}
function _tt_clearClosing_(): void {
	var tmClosing = _tt_tmClosing;
	if (tmClosing) {
		_tt_tmClosing = undefined;
		clearTimeout(tmClosing);
	}
}
function _tt_open_(event: zk.Event): undefined | undefined {
	var inf = _tt_inf;
	if (inf) {
		_tt_tip = inf.tip,
			_tt_ref = inf.ref;
		_tt_inf = undefined;

		var n = _tt_ref.$n();
		// B65-ZK-1934: If reference's dom is null or not visible, then just return.
		if (!n || !zk(n).isRealVisible()) //gone
			return _tt_tip = _tt_ref = undefined;

		// `params.x/y` should be `number` throughout except being initialized by Java.
		// Hence, we define `params.x/y` as number and force-cast to string here.
		var params = inf.params,
			x = params.x as unknown as string,
			y = params.y as unknown as string;
		if (x)
			params.x = _parseParamFunc(event, x);
		if (y)
			params.y = _parseParamFunc(event, y);

		var xy: zk.Offset = params.x !== undefined ? [params.x, params.y!] : zk.currentPointer;
		_tt_tip.open(params.ref || _tt_ref, xy, zul.Widget._getPopupPosition(params), {focusFirst: true, sendOnOpen: true});
	}
}
function _tt_close_(): void {
	_tt_clearOpening_();
	_tt_clearClosing_();

	var tip = _tt_tip;
	if (tip?.desktop) { //check still attached to desktop
		// Bug ZK-1222, ZK-1594
		// If the tooltip (popup) and mouse pointer overlapped, a TooltipOut event
		// will be triggered again that closes the tooltip immediately, then another
		// TooltipOver event will open the tooltip again...
		// If mouse pointer still overlapped on tooltip, do not close.
		// IE10: Bug ZK-1519, Chrome: Bug ZK-3583
		var $tip = jq(tip.$n()),
			$tipOff = $tip.offset()!,
			pointer = zk.currentPointer;
		if ((pointer[0] >= $tipOff.left && pointer[0] <= ($tipOff.left + $tip.width()!))
			&& (pointer[1] >= $tipOff.top && pointer[1] <= ($tipOff.top + $tip.height()!)))
			return;
		_tt_tip = _tt_ref = undefined;
		tip.close({sendOnOpen: true});
	}
}
function _setCtrlKeysErr(msg: string): void {
	zk.error('setCtrlKeys: ' + msg);
}
function _parseParamFunc(event: zk.Event | undefined, funcBody: string): number {
	if (funcBody.includes('(') && funcBody.includes(')')) {
		var func = new Function('event', 'return ' + funcBody + ';');
		return func(event) as number;
	} else {
		return zk.parseInt(funcBody);
	}
}
export type ParsedCtlKeys = Record<number, boolean>[];

/** The base class for ZUL widget.
 * <p>The corresponding Java class is org.zkoss.zul.impl.XulElement.
 * <p>If a widget has a client attribute 'scrollable', it will listen `onScroll` event.
 */
@zk.WrapClass('zul.Widget')
export class Widget<TElement extends HTMLElement = HTMLElement> extends zk.Widget<TElement> {
	/** @internal */
	_context?: string;
	/** @internal */
	_popup?: string;
	/** @internal */
	_doScrollableSyncScroll?: (() => void);
	/** @internal */
	_tooltip?: string;
	/** @internal */
	_ctrlKeys?: string;
	/** @internal */
	_parsedCtlKeys?: ParsedCtlKeys;

	constructor(props?: Record<string, unknown> | typeof zkac) {
		super(props);
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		// B70-ZK-2069: some widget need fire onScroll event, which has
		// characteristic of container
		if (jq(this.uuid, zk).data('scrollable')) { // Avoid caching $n() too early
			this._doScrollableSyncScroll = zUtl.throttle(function (this: zk.Object) {
				if (jq(this).data('scrollable')) {
					zWatch.fireDown('onScroll', this);
					zWatch.fire('_onSyncScroll', this); // ZK-4408: for Popup only
				}
			}, 1000 / 60); // 60fps
			this.domListen_(this.getCaveNode()!, 'onScroll', '_doScrollableSyncScroll');
		}
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this._doScrollableSyncScroll) {
			this.domUnlisten_(this.getCaveNode()!, 'onScroll', '_doScrollableSyncScroll');
		}
		super.unbind_(skipper, after, keepRod);
	}

	/**
	 * @returns the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user right-clicks on the element (aka., context menu).
	 *
	 * @defaultValue `null` (no context menu).
	 */
	getContext(): string | undefined {
		return this._context;
	}

	/**
	 * Sets the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user right-clicks on the element (aka., context menu).
	 * @param context - the ID of the popup widget.
	 * @see {@link setContext}
	 */
	setContext(context: string): this
	/**
	 * Sets the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user right-clicks on the element (aka., context menu).
	 *
	 * <p>An onOpen event is sent to the context menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
	 *
	 * <p>If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * `uuid(comp_uuid)`
	 *
	 * <p>Example:<br/>
	 * ```ts
	 * wgt.setContext('an_id');
	 * wgt.setContext('uuid(an_uuid)');
	 * wgt.setContext(a_wgt);
	 * ```
	 * Both reference a component whose ID is "some".
	 * But, if there are several components with the same ID,
	 * the first one can reference to any of them.
	 * And, the second one reference to the component in the same ID space
	 * (of the label component).
	 *
	 *
	 * <p>The context menu can be shown by a position from
	 * {@link zul.wgt.Popup.open}
	 * or the location of `x` and `y`, you can specify the following format:</br>
	 * <ul>
	 * <li>`id, position`</li>
	 * <li>`id, position=before_start`</li>
	 * <li>`id, x=15, y=20`</li>
	 * <li>`uuid(comp_uuid), position`</li>
	 * <li>`uuid(comp_uuid), x=15, y=20`</li>
	 * </ul>
	 * For example,
	 * ```ts
	 * wgt.setContext('an_id', 'start_before');
	 * ```
	 * Since 6.5.2, the context menu can also be shown on customized location of `x` and `y` by adding parentheses"()", for example,
	 * ```ts
	 * wgt.setContext('an_id', 'x=(zk.currentPointer[0] + 10), y=(zk.currentPointer[1] - 10)');
	 * ```
	 * @param context - the popup widget.
	 */
	setContext(context: zul.wgt.Popup): this
	setContext(context: zul.wgt.Popup | string): this {
		if (context instanceof zk.Widget)
			context = 'uuid(' + context.uuid + ')';
		this._context = context;
		return this;
	}

	/**
	 * @returns the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user clicks on the element.
	 *
	 * @defaultValue `null` (no popup).
	 */
	getPopup(): string | undefined {
		return this._popup;
	}

	/**
	 * Sets the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user clicks on the element.
	 * @param popup - the ID of the popup widget.
	 * @see {@link setPopup}
	 */
	setPopup(popup: string): this
	/**
	 * Sets the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user clicks on the element.
	 *
	 * <p>An onOpen event is sent to the popup menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
	 *
	 * <p>If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * `uuid(comp_uuid)`
	 *
	 * <p>Example:<br/>
	 * ```ts
	 * wgt.setPopup('an_id');
	 * wgt.setPopup('uuid(an_uuid)');
	 * wgt.setPopup(a_wgt);
	 * ```
	 * Both reference a component whose ID is "some".
	 * But, if there are several components with the same ID,
	 * the first one can reference to any of them.
	 * And, the second one reference to the component in the same ID space
	 * (of the label component).
	 *
	 *
	 * <p>The popup menu can be shown by a position from
	 * {@link zul.wgt.Popup.open}
	 * or the location of `x` and `y`, you can specify the following format:</br>
	 * <ul>
	 * <li>`id, position`</li>
	 * <li>`id, position=before_start`</li>
	 * <li>`id, x=15, y=20`</li>
	 * <li>`uuid(comp_uuid), position`</li>
	 * <li>`uuid(comp_uuid), x=15, y=20`</li>
	 * </ul>
	 * For example,
	 * ```ts
	 * wgt.setPopup('an_id', 'start_before');
	 * ```
	 * Since 6.5.2, the popup can also be shown on customized location of `x` and `y` by adding parentheses"()", for example,
	 * ```ts
	 * wgt.setPopup('an_id', 'x=(zk.currentPointer[0] + 10), y=(zk.currentPointer[1] - 10)');
	 * ```
	 * @param popup - the popup widget.
	 */
	setPopup(popup: zul.wgt.Popup): this
	// eslint-disable-next-line zk/tsdocValidation
	/** This overload is for internal use @internal */
	setPopup(popup: zul.wgt.Popup | string): this
	setPopup(popup: zul.wgt.Popup | string): this {
		if (popup instanceof zk.Widget)
			popup = 'uuid(' + popup.uuid + ')';
		this._popup = popup;
		return this;
	}

	/**
	 * @returns the ID of the popup ({@link zul.wgt.Popup}) that should be used
	 * as a tooltip window when the mouse hovers over the element for a moment.
	 * The tooltip will automatically disappear when the mouse is moved away.
	 *
	 * @defaultValue `null` (no tooltip).
	 */
	getTooltip(): string | undefined {
		return this._tooltip;
	}

	/**
	 * Sets the ID of the popup ({@link zul.wgt.Popup}) that should be used
	 * as a tooltip window when the mouse hovers over the element for a moment.
	 * @param tooltip - the ID of the popup widget.
	 * @see {@link setPopup}
	 */
	setTooltip(tooltip: string): this
	/**
	 * Sets the ID of the popup ({@link zul.wgt.Popup}) that should be used
	 * as a tooltip window when the mouse hovers over the element for a moment.
	 *
	 * <p>An onOpen event is sent to the tooltip if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
	 *
	 * <p>If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * `uuid(comp_uuid)`
	 *
	 * <p>Example:<br/>
	 * ```ts
	 * wgt.setTooltip('an_id');
	 * wgt.setTooltip('uuid(an_uuid)');
	 * wgt.setTooltip(a_wgt);
	 * ```
	 * Both reference a component whose ID is "some".
	 * But, if there are several components with the same ID,
	 * the first one can reference to any of them.
	 * And, the second one reference to the component in the same ID space
	 * (of the label component).
	 *
	 *
	 * <p>The tooltip can be shown by a position from
	 * {@link zul.wgt.Popup.open}
	 * or the location of `x` and `y`, and can be specified
	 * with a delay time (in millisecond), you can specify the following format:
	 * </br>
	 * <ul>
	 * <li>`id, position`</li>
	 * <li>`id, position=before_start, delay=500`</li>
	 * <li>`id, x=15, y=20`</li>
	 * <li>`uuid(comp_uuid), position`</li>
	 * <li>`uuid(comp_uuid), x=15, y=20`</li>
	 * </ul>
	 * For example,
	 * ```ts
	 * wgt.setTooltip('an_id', 'start_before');
	 * ```
	 * Since 6.5.2, the tooltip can also be shown on customized location of `x` and `y` by adding parentheses"()", for example,
	 * ```ts
	 * wgt.setPopup('an_id', 'x=(zk.currentPointer[0] + 10), y=(zk.currentPointer[1] - 10)');
	 * ```
	 * @param popup - the popup widget.
	 */
	setTooltip(tooltip: zul.wgt.Popup): this
	setTooltip(tooltip: zul.wgt.Popup | string): this {
		if (tooltip instanceof zk.Widget)
			tooltip = 'uuid(' + tooltip.uuid + ')';
		this._tooltip = tooltip;
		return this;
	}

	/**
	 * @returns what keystrokes to intercept.
	 * @defaultValue `null`.
	 */
	getCtrlKeys(): string | undefined {
		return this._ctrlKeys;
	}

	/**
	 * Sets what keystrokes to intercept.
	 *
	 * <p>The string could be a combination of the following:
	 * <dl>
	 * <dt>^k</dt>
	 * <dd>A control key, i.e., Ctrl+k, where k could be a~z, 0~9, #n</dd>
	 * <dt>\@k</dt>
	 * <dd>A alt key, i.e., Alt+k, where k could be a~z, 0~9, #n</dd>
	 * <dt>$n</dt>
	 * <dd>A shift key, i.e., Shift+n, where n could be #n.
	 * Note: $a ~ $z are not supported.</dd>
	 * <dt>#home</dt>
	 * <dd>Home</dd>
	 * <dt>#end</dt>
	 * <dd>End</dd>
	 * <dt>#ins</dt>
	 * <dd>Insert</dd>
	 * <dt>#del</dt>
	 * <dd>Delete</dd>
	 * <dt>#bak</dt>
	 * <dd>Backspace</dd>
	 * <dt>#left</dt>
	 * <dd>Left arrow</dd>
	 * <dt>#right</dt>
	 * <dd>Right arrow</dd>
	 * <dt>#up</dt>
	 * <dd>Up arrow</dd>
	 * <dt>#down</dt>
	 * <dd>Down arrow</dd>
	 * <dt>#pgup</dt>
	 * <dd>PageUp</dd>
	 * <dt>#pgdn</dt>
	 * <dd>PageDn</dd>
	 * <dt>#f1 #f2 ... #f12</dt>
	 * <dd>Function keys representing F1, F2, ... F12</dd>
	 * </dl>
	 *
	 * <p>For example,
	 * <dl>
	 * <dt>^a^d\@c#f10#left#right</dt>
	 * <dd>It means you want to intercept Ctrl+A, Ctrl+D, Alt+C, F10,
	 * Left and Right.</dd>
	 * <dt>^#left</dt>
	 * <dd>It means Ctrl+Left.</dd>
	 * <dt>^#f1</dt>
	 * <dd>It means Ctrl+F1.</dd>
	 * <dt>\@#f3</dt>
	 * <dd>It means Alt+F3.</dd>
	 * </dl>
	 */
	setCtrlKeys(ctrlKeys: string): this {
		if (this._ctrlKeys == ctrlKeys) return this;
		if (!ctrlKeys) {
			this._ctrlKeys = this._parsedCtlKeys = undefined;
			return this;
		}
		//ext(#), ctrl(001), alt(010), ctrl + alt(011), shift(100), ctrl + shift(101), alt + shift(110), ctrl + alt + shift(111)
		var parsed: ParsedCtlKeys = [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}],
			which = 0;
		for (var j = 0, len = ctrlKeys.length; j < len; ++j) {
			var cc = ctrlKeys.charAt(j); //ext
			switch (cc) {
				case '^': //ctrl
				case '@': //alt
				case '$': //shift
				case '%': //meta
					var flag = cc == '^' ? 1 : cc == '@' ? 2 : cc == '$' ? 4 : 8;
					if ((which & flag) != 0) {
						_setCtrlKeysErr('Unexpected key combination: ' + ctrlKeys);
						return this;
					} else
						which |= flag;
					break;
				case '#': {
					var k = j + 1;
					for (; k < len; ++k) {
						var c2 = ctrlKeys.charAt(k);
						if ((c2 > 'Z' || c2 < 'A') && (c2 > 'z' || c2 < 'a')
							&& (c2 > '9' || c2 < '0'))
							break;
					}
					if (k == j + 1) {
						_setCtrlKeysErr('Unexpected character # in ' + ctrlKeys);
						return this;
					}

					let cc: number;
					var s = ctrlKeys.substring(j + 1, k).toLowerCase();
					if ('pgup' == s) cc = 33;
					else if ('pgdn' == s) cc = 34;
					else if ('end' == s) cc = 35;
					else if ('home' == s) cc = 36;
					else if ('left' == s) cc = 37;
					else if ('up' == s) cc = 38;
					else if ('right' == s) cc = 39;
					else if ('down' == s) cc = 40;
					else if ('ins' == s) cc = 45;
					else if ('del' == s) cc = 46;
					else if ('bak' == s) cc = 8;
					else if ('tab' == s) cc = 9;
					else if (s.length > 1 && s.startsWith('f')) {
						var v = zk.parseInt(s.substring(1));
						if (v == 0 || v > 12) {
							_setCtrlKeysErr('Unsupported function key: #f' + v);
							return this;
						}
						cc = 112 + v - 1;
					} else {
						_setCtrlKeysErr('Unknown #' + s + ' in ' + ctrlKeys);
						return this;
					}

					parsed[which][cc] = true;
					which = 0;
					j = k - 1;
					break;
				}
				default:
					if (!which || ((cc > 'Z' || cc < 'A')
						&& (cc > 'z' || cc < 'a') && (cc > '9' || cc < '0'))) {
						_setCtrlKeysErr('Unexpected character ' + cc + ' in ' + ctrlKeys);
						return this;
					}
					if (which == 4) {
						_setCtrlKeysErr('$a - $z not supported (found in ' + ctrlKeys + '). Allowed: $#f1, $#home and so on.');
						return this;
					}

					if (cc <= 'z' && cc >= 'a')
						cc = cc.toUpperCase();
					parsed[which][cc.charCodeAt(0)] = true;
					which = 0;
					break;
			}
		}

		this._parsedCtlKeys = parsed;
		this._ctrlKeys = ctrlKeys;
		return this;
	}

	/** @internal */
	_parsePopParams(txt: string, event?: zk.Event): PopupParams {
		var params: {
			id?: string;
			position?: string;
			delay?: number | string;
			ref?: zul.wgt.Ref | undefined;
			x?: number | string;
			y?: number | string;
			type?: string;
		} = {},
			index = txt.indexOf(','),
			start = txt.indexOf('='),
			t = txt;
		if (start != -1)
			t = txt.substring(0, txt.substring(0, start).lastIndexOf(','));

		if (index != -1) {
			params.id = t.substring(0, index).trim();
			var t2 = t.substring(index + 1, t.length);
			if (t2)
				params.position = t2.trim();

			zk.copy(params, zUtl.parseMap(txt.substring(t.length, txt.length)));
		} else
			params.id = txt.trim();

		if (this._popup || this._context) { //should prepare tooltip in _tt_open_
			var x = params.x as string, y = params.y as string;
			if (x)
				params.x = _parseParamFunc(event, x);
			if (y)
				params.y = _parseParamFunc(event, y);
		}
		if (params.delay)
			params.delay = zk.parseInt(params.delay);
		if (params.ref)
			params.ref = this._smartFellow(params.ref as string);
		return params as PopupParams;
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (!this.shallIgnoreClick_(evt) && !evt.contextSelected) {
			var params = this._popup ? this._parsePopParams(this._popup, evt) : {},
				popup = this._smartFellow(params.id) as zul.wgt.Popup | undefined;
			if (popup) {
				evt.contextSelected = true;

				// to avoid a focus in IE, we have to pop up it later. for example, zksandbox/#t5
				var self = this,
					xy: zk.Offset = params.x !== undefined ? [params.x, params.y!]
						: [evt.pageX, evt.pageY];
				// F70-ZK-2007: When type=toggle, close the popup
				if (params.type && params.type == 'toggle' && popup.isOpen()) {
					popup.close({sendOnOpen: true});
				} else {
					setTimeout(function () { // F70-ZK-2007: Add the type and button number information
						if (self.desktop)
							popup!.open(params.ref || self, xy, zul.Widget._getPopupPosition(params), {focusFirst: true, sendOnOpen: true, type: params.type, which: 1});
					}, 0);
				}
				evt.stop({dom: true});
			}
		}
		if (popupOnly !== true)
			super.doClick_(evt); // super method doesn't accept a second parameter
	}

	/** @internal */
	override doRightClick_(evt: zk.Event): void {
		if (!this.shallIgnoreClick_(evt) && !evt.contextSelected) {
			var params = this._context ? this._parsePopParams(this._context, evt) : {},
				ctx = this._smartFellow(params.id) as zul.wgt.Popup | undefined;
			if (ctx) {
				evt.contextSelected = true;

				// to avoid a focus in IE, we have to pop up it later. for example, zksandbox/#t5
				var self = this,
					xy: zk.Offset = params.x !== undefined ? [params.x, params.y!]
						: [evt.pageX, evt.pageY];
				// F70-ZK-2007: When type=toggle, close the popup
				if (params.type && params.type == 'toggle' && ctx.isOpen()) {
					ctx.close({sendOnOpen: true});
				} else {
					setTimeout(function () { // F70-ZK-2007: Add the type and button number information
						if (self.desktop)
							ctx!.open(params.ref || self, xy, zul.Widget._getPopupPosition(params), {focusFirst: true, sendOnOpen: true, type: params.type, which: 3}); //Bug #2870620
					}, 0);
				}
				evt.stop({dom: true}); //prevent default context menu to appear
			}
		}
		super.doRightClick_(evt);
	}

	/** @internal */
	override doTooltipOver_(evt: zk.Event): void {
		if (!evt.tooltipped && _tt_beforeBegin(this)) {
			var params = this._tooltip ? this._parsePopParams(this._tooltip) : {},
				tip = this._smartFellow(params.id) as zul.wgt.Popup | undefined;
			if (tip) {
				evt.tooltipped = true;
				//still call parent's doTooltipOver_ for better extensibility (though not necessary)
				_tt_begin(tip, this, params, evt);
			}
		}
		super.doTooltipOver_(evt);
	}

	/** @internal */
	override doTooltipOut_(evt: zk.Event): void {
		_tt_end(this);
		super.doTooltipOut_(evt);
	}

	/** @internal */
	_smartFellow(id?: string): zk.Widget | undefined {
		return id ? id.startsWith('uuid(') && id.endsWith(')') ?
			zk.Widget.$(id.substring(5, id.length - 1)) :
			this.$f(id, true) : undefined;
	}

	//B70-ZK-2435: catch key down event right now rather than propagate it
	/** @internal */
	override doKeyDown_(evt: zk.Event): void {
		if (this.getCtrlKeys() || this.isListen('onOK') || this.isListen('onCancel')) {
			//B70-ZK-2532: if afterKeyDown_ doesn't handle evt, then propagate to super
			if (!this.afterKeyDown_(evt))
				super.doKeyDown_(evt);
		} else
			super.doKeyDown_(evt);
	}

	/**
	 * Called after {@link zk.Widget#doKeyDown_} is called and the event
	 * propagation is not stopped.
	 * @defaultValue handles the control keys, including onOK and onCancel,
	 * by searching up the ancestor chain to see if any one is listening.
	 * If found, it calls {@link beforeCtrlKeys_} for each widget that were
	 * searched, and then fire the event.
	 * @param evt - the widget event.
	 * @param simulated - if the event was not sent to the widget originally (rather,
	 * it is caused by pressing when none of widget but document gains the focus)
	 * @returns true if the event has been processed
	 * @see {@link setCtrlKeys}
	 * @internal
	 */
	afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean {
		var keyCode = evt.keyCode, evtnm = 'onCtrlKey', okcancel, commandKey = zk.mac && evt.metaKey;
		switch (keyCode) {
			case 13: { //ENTER
				const target = evt.domTarget, tn = jq.nodeName(target);
				if (tn == 'textarea' || (tn == 'button'
					// if button's ID end with '-a' still fire onOK(Like Listbox and Menupopup)
					&& (!target.id || !target.id.endsWith('-a')))
					|| (tn == 'input' && (target as HTMLInputElement).type.toLowerCase() == 'button'))
					return false; //don't change button's behavior (Bug 1556836)
				okcancel = evtnm = 'onOK';
				break;
			}
			case 27: //ESC
				okcancel = evtnm = 'onCancel';
				break;
			case 16: //Shift
			case 17: //Ctrl
			case 18: //Alt
				return false;
			case 45: //Ins
			case 46: //Del
			case 8: //Backspace
			case 9: //Tab
				break;
			default:
				if ((keyCode >= 33 && keyCode <= 40) //PgUp, PgDn, End, Home, L, U, R, D
					|| (keyCode >= 112 && keyCode <= 123) //F1: 112, F12: 123
					|| evt.ctrlKey || evt.altKey || commandKey)
					break;
				return false;
		}

		var target = evt.target,
			wgt: zk.Widget & {_parsedCtlKeys?: ParsedCtlKeys} | undefined = target;
		for (;; wgt = wgt.parent) {
			if (!wgt) return false;
			if (!wgt.isListen(evtnm, {any: true})) continue;

			if (okcancel)
				break;

			var parsed = wgt._parsedCtlKeys, which = 0;
			if (parsed) {
				if (evt.ctrlKey)
					which |= 1;
				if (evt.altKey)
					which |= 2;
				if (evt.shiftKey)
					which |= 4;
				if (commandKey)
					which |= 8;
				if (parsed[which][keyCode])
					break; //found
			}
		}

		//Bug 3304408: SELECT fixes the selected index later than mousedown
		//so we have to defer the firing of ctrl keys
		setTimeout(function () {
			interface WidgetBeforeCtrlKeys extends zk.Widget {
				beforeCtrlKeys_?(evt: zk.Event): unknown;
			}
			for (var w: WidgetBeforeCtrlKeys = target; ; w = w.parent!) {
				if (w.beforeCtrlKeys_?.(evt))
					return;
				if (w == wgt) break;
			}
			wgt.fire(evtnm, zk.copy({reference: target}, evt.data));
		}, 0);

		evt.stop();
		if (jq.nodeName(evt.domTarget, 'select'))
			evt.stop({dom: true, revoke: true}); //Bug 1756559: don't stop DOM since it affects IE and Opera's SELECT's closing dropdown

		return true; //handled
	}

	/**
	 * Called before a control key is pressed. A control key includes onOK and
	 * onCancel; refer to #setCtrlKeys for details.
	 * @defaultValue does nothing (but return false)
	 * It is usually overridden by a stateful widget, such as an input box,
	 * to update its state to the server, such as firing the onChange event.
	 * @param _evt - the widget event.
	 * @returns if true, the widget want to abort the firing of the control
	 * 		key. In other words, if true is returned, the control key is ignored.
	 * @internal
	 */
	beforeCtrlKeys_(_evt: zk.Event): void {
		// empty on purpose
	}

	/**
	 * @returns the tooltip that is opened, or null if no tooltip is opened.
	 * @since 5.0.5
	 */
	static getOpenTooltip(): zul.wgt.Popup | undefined {
		return _tt_tip?.isOpen() ? _tt_tip : undefined;
	}

	/** @internal */
	static _getPopupPosition(params: PopupParams): string | undefined {
		if (params.position)
			return params.position;
		if ('x' in params || 'y' in params) // ZK-1655
			return undefined;
		return 'at_pointer';
	}
}