/* Binder.ts

	Purpose:
		
	Description:
		
	History:
		Wed, Jan 07, 2015 12:08:17 PM, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.
*/
var _WidgetX: Partial<zk.Widget> = {},
	_zkMatchMediaRegexPattern = /ZKMatchMedia=([^;]*)/,
	_portrait: Record<string | number, boolean> = {'0': true, '180': true}, //default portrait definition
	_initLandscape = jq.innerWidth() > jq.innerHeight(), // initial orientation is landscape or not
	_initDefault = _portrait[window.orientation] as boolean | undefined; //default orientation

zk.override(zk.Widget.prototype, _WidgetX, {
	$binder(this: zk.Widget): zkbind.Binder | undefined {
		var w: zk.Widget | undefined = this;
		for (; w; w = w.parent) {
			if (w.$ZKBINDER$)
				break;
		}
		if (w) {
			if (!w._$binder)
				w._$binder = new zkbind.Binder(w, this);
			return w._$binder;
		}
		return undefined;
	},
	$afterCommand(command: string, args?: unknown[]): void {
		const binder = this.$binder();
		if (binder)
			binder.$doAfterCommand(command, args);
	},
	unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this._$binder) {
			this._$binder.destroy();
			this._$binder = undefined;
		}
		_WidgetX.unbind_!.call(this, skipper, after, keepRod);
	}
});

export interface BinderOptions {
	exact?: boolean;
	strict?: boolean;
	child?: boolean;
}

/** @class zkbind
 * @import zkbind.Binder
 * A collection of ZK bind utilities.
 * @since 8.0.0
 */
//@{
/** Retrieves the binder if any.
 * @param Object n the object to look for. If it is a string,
 * it is assumed to be UUID, unless it starts with '$'.
 * For example, <code>zkbind.$('uuid')<code> is the same as <code>zkbind.$('#uuid')<code>,
 * and both look for a widget whose ID is 'uuid'. On the other hand,
 * <code>zkbind.$('$id') looks for a widget whose ID is 'id'.<br/>
 * and <code>zkbind.$('.className') looks for a widget whose CSS selector is 'className'.<br/>
 * If it is an DOM element ({@link DOMElement}), it will look up
 * which widget it belongs to.<br/>
 * If the object is not a DOM element and has a property called
 * <code>target</code>, then <code>target</code> is assumed.
 * Thus, you can pass an instance of {@link jq.Event} or {@link zk.Event},
 * and the target widget will be returned.
 * @param Map opts [optional] the options. Allowed values:
 * <ul>
 * <li>exact - id must exactly match uuid (i.e., uuid-xx ignored).
 * It also implies strict</li>
 * <li>strict - whether not to look up the parent node.(since 5.0.2)
 * If omitted, false is assumed (and it will look up parent).</li>
 * <li>child - whether to ensure the given element is a child element
 * of the widget's main element ({@link zk.Widget#$n}). In most cases, if ID
 * of an element is xxx-yyy, the the element must be a child of
 * the element whose ID is xxx. However, there is some exception
 * such as the shadow of a window.</li>
 * </ul>
 * @return Binder
 * @since 8.0.0
 */
//$: function () {}
//@};
export function $(n: string | HTMLElement | zk.Event | JQuery.Event, opts?: BinderOptions): Binder | undefined {
	var widget = zk.Widget.$(n, opts);
	if (widget)
		return widget.$binder!();
	zk.error('Not found ZK Binder with [' + n + ']');
}
zkbind.$ = $;

function _fixCommandName(prefix: string, cmd: string, opts: zk.EventOptions, prop: string): void {
	if (opts[prop]) {
		var ignores = {};
		ignores[prefix + cmd] = true;
		opts[prop] = ignores;
	}
}

// see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent
function encodeRFC5987ValueChars(str: string | string[]): string {
	return encodeURIComponent(str).replace(/['()]/g, escape).replace(/\*/g, '%2A').replace(/%(?:7C|60|5E)/g, unescape);
}

//ZK-3133
function _matchMedia(event: MediaQueryListEvent | MediaQueryList, binder: Binder, value: string): void {
	var cookies = binder._cookies!;
	if (event.matches) {
		var orient = '',
			dpr = 1;
		if (zk.mobile) {
			if ((_initLandscape && _initDefault) || (!_initLandscape && !_initDefault))
				_portrait = {'-90': true, '90': true};
			orient = _portrait[window.orientation] ? 'portrait' : 'landscape';
		} else {
			orient = jq.innerWidth() > jq.innerHeight() ? 'landscape' : 'portrait';
		}
		if (window.devicePixelRatio)
			dpr = window.devicePixelRatio;
		// 16 is the length of string MATCHMEDIAVALUE_PREFIX in BinderCtrl.java
		var ci = [new Date().getTimezoneOffset(), screen.width, screen.height, screen.colorDepth, jq.innerWidth(),
				jq.innerHeight(), jq.innerX(), jq.innerY(), dpr.toFixed(1), orient, zk.mm.tz.guess(), event.matches, value.substring(16)];
		// $ZKCLIENTINFO$ refers to CLIENT_INFO string in BinderCtrl.java
		binder.command(value, {'$ZKCLIENTINFO$': ci});
		if (!cookies.$contains(value)) cookies.push(value);
		document.cookie = 'ZKMatchMedia=' + encodeRFC5987ValueChars(cookies);
		document.cookie = 'ZKClientInfo=' + encodeRFC5987ValueChars(JSON.stringify(ci));
	} else {
		cookies.$remove(value);
		document.cookie = 'ZKMatchMedia=' + encodeRFC5987ValueChars(cookies);
	}
}

export interface MediaQueryListWithHandler {
	mql: MediaQueryList;
	handler: (evt: MediaQueryListEvent) => void;
}

/**
 * A data binder utile widget.
 * @import _global_.File
 * @since 8.0.0
 */
@zk.WrapClass('zkbind.Binder')
export class Binder extends zk.Object {
	_cookies?: string[];
	_lastcmd?: string;
	_aftercmd?: Record<string, CallableFunction[]>;
	_mediaQueryLists?: MediaQueryListWithHandler[];
	_processingAfterCommand?: boolean;
	$view?: zk.Widget;
	_toDoUnAftercmd: Record<string, CallableFunction[]>;
	$currentTarget?: object;

	constructor(widget: zk.Widget, currentTarget: object) {
		super(); // FIXME: params?
		this.$view = widget;
		this.$currentTarget = currentTarget;
		this._aftercmd = {};
		this._toDoUnAftercmd = {};
		//ZK-3133
		if (widget.$ZKMATCHMEDIA$) {
			var cookies: string[] = [],
				matched = _zkMatchMediaRegexPattern.exec(document.cookie);
			if (matched) {
				var m = matched[1];
				if (m) {
					cookies = decodeURIComponent(m).trim().split(',');
				}
			}
			this._cookies = cookies;
			var binder = this,
				mqls: MediaQueryListWithHandler[] = [];
			// eslint-disable-next-line @typescript-eslint/prefer-for-of
			for (var i = 0; i < widget.$ZKMATCHMEDIA$.length; i++) {
				var media = widget.$ZKMATCHMEDIA$[i],
					mql = window.matchMedia(media.substring(16)),
					handler = (function (s) {
						return function (event: MediaQueryListEvent | MediaQueryList) {
							_matchMedia(event, binder, s);
						};
					})(media);
				mql.addListener(handler);
				handler(mql);
				mqls.push({mql: mql, handler: handler});
			}
			this._mediaQueryLists = mqls;
		}
	}

	/**
	 * Registers a callback after some command executed.
	 * @param String command the name of the command
	 * @param Function func the function to execute
	 */
	after(cmd: string | CallableFunction, fn: CallableFunction): this {
		if (!fn && jq.isFunction(cmd)) {
			fn = cmd;
			cmd = this._lastcmd!;
		}
			
		var ac = this._aftercmd![cmd as string];
		if (!ac) this._aftercmd![cmd as string] = [fn];
		else
			ac.push(fn);
		return this;
	}

	/**
	 * Unregisters a callback after some command executed.
	 * @param String command the name of the command
	 * @param Function func the function to execute
	 */
	unAfter(cmd: string, fn: CallableFunction): this {
		var ac = this._aftercmd![cmd];
		for (var j = ac ? ac.length : 0; j--;) {
			if (ac[j] == fn) {
				if (!this._processingAfterCommand)
					ac.splice(j, 1);
				else { // ZK-4482: queue unAfter if $doAfterCommand still processing
					var tduac = this._toDoUnAftercmd[cmd];
					if (!tduac) this._toDoUnAftercmd[cmd] = [fn];
					else
						tduac.push(fn);
				}
			}
		}
		return this;
	}

	/**
	 * Destroy this binder.
	 */
	destroy(): void {
		this._aftercmd = undefined;
		if (this._mediaQueryLists != null) {
			var mqls = this._mediaQueryLists;
			// eslint-disable-next-line @typescript-eslint/prefer-for-of
			for (var i = 0; i < mqls.length; i++) {
				mqls[i].mql.removeListener(mqls[i].handler);
			}
			this._mediaQueryLists = undefined;
			this._cookies = undefined;
		}
		this.$view = undefined;
		this.$currentTarget = undefined;
	}

	/**
	 * Post a command to the binder
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param Map opts a map of options to zk.Event, if any.
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 */
	command(cmd: string, args?: Record<string, unknown>, opts?: zk.EventOptions, timeout?: number): this {
		var wgt = this.$view;
		if (opts) {
			if (opts.duplicateIgnore)
				_fixCommandName('onBindCommand$', cmd, opts, 'duplicateIgnore');
			if (opts.repeatIgnore)
				_fixCommandName('onBindCommand$', cmd, opts, 'repeatIgnore');
		}
		zAu.send(new zk.Event(wgt, 'onBindCommand$' + cmd, {cmd: cmd, args: args}, zk.copy({toServer: true}, opts)), timeout != undefined ? timeout : 38);
		this._lastcmd = cmd;
		return this;
	}

	/**
	 * Post a global command from the binder.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param Map opts a map of options to zk.Event, if any.
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 */
	globalCommand(cmd: string, args?: Record<string, unknown>, opts?: zk.EventOptions, timeout?: number): this {
		var wgt = this.$view;
		if (opts) {
			if (opts.duplicateIgnore)
				_fixCommandName('onBindGlobalCommand$', cmd, opts, 'duplicateIgnore');
			if (opts.repeatIgnore)
				_fixCommandName('onBindGlobalCommand$', cmd, opts, 'repeatIgnore');
		}
		zAu.send(new zk.Event(wgt, 'onBindGlobalCommand$' + cmd, {cmd: cmd, args: args}, zk.copy({toServer: true}, opts)), timeout != undefined ? timeout : 38);
		this._lastcmd = cmd;
		return this;
	}

	$doAfterCommand(cmd: string, args?: unknown[]): void {
		var ac = this._aftercmd![cmd],
			tduac = this._toDoUnAftercmd[cmd];
		this._processingAfterCommand = true; // ZK-4482
		for (var i = 0, j = ac ? ac.length : 0; i < j; i++)
			ac[i].bind(this)(args);
		this._processingAfterCommand = false;
		for (var i = 0, j = tduac ? tduac.length : 0; i < j; i++) { // ZK-4482: do unAfter
			this.unAfter(cmd, tduac[i]);
		}
		this._toDoUnAftercmd[cmd] = [];
	}

	/**
	 * Post a upload command to the binder
	 * @param String cmd the name of the command
	 * @param File file the file to upload. (the value should be a file type)
	 * @since 9.0.1
	 */
	upload(cmd: string, file: File): void {
		this.$view!.fire('onBindCommandUpload$' + cmd, {cmd: cmd}, {file: file});
	}

	/**
	 * Post a command to the binder from the give dom element.
	 * @param DOMElement dom the target of the dom element.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param Map opts a map of options to zk.Event, if any.
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 */
	static postCommand(dom: HTMLElement, command: string, args?: Record<string, unknown>, opt?: zk.EventOptions, timeout?: number): void {
		var w = zk.Widget.$(dom);
		if (w) {
			var binder = w.$binder!();
			if (binder) {
				binder.command(command, args, opt, timeout);
			}
		}
	}

	/**
	 * Post a global command from the binder of the give dom element.
	 * @param DOMElement dom the target of the dom element.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 */
	static postGlobalCommand(dom: HTMLElement, command: string, args?: Record<string, unknown>, opt?: zk.EventOptions, timeout?: number): void {
		var w = zk.Widget.$(dom);
		if (w) {
			var binder = w.$binder!();
			if (binder) {
				binder.globalCommand(command, args, opt, timeout);
			}
		}
	}
}