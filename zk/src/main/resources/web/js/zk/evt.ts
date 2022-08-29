/* evt.ts

	Purpose:
		ZK Event and ZK Watch
	Description:

	History:
		Thu Oct 23 10:53:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
export interface EventOptions {
	implicit?: boolean;
	ignorable?: boolean;
	toServer?: boolean;
	uri?: string;
	down?: boolean;
	timeout?: number;
	defer?: boolean;
	serverAlive?: boolean;
	forceAjax?: boolean;
	sendAhead?: boolean;
	reverse?: boolean;
	rtags?: Record<string, unknown>;
	start?: {
		time: number | Date;
		coords: zk.Offset;
	};
	stop?: {
		time: number | Date;
		coords: zk.Offset;
	};
	dir?: string;
	uploadCallback?: Partial<Pick<XMLHttpRequestEventTarget, `on${keyof XMLHttpRequestEventTargetEventMap}`>>;
	ctl?: boolean; // zul.Imagemap.onclick
	file?: File; // zkbind.Binder
	duplicateIgnore?: boolean; // zkbind.Binder
	repeatIgnore?: boolean; // zkbind.Binder
}

export interface EventStopOptions {
	revoke?: boolean;
	propagation?: boolean;
	dom?: boolean;
	au?: boolean;
}
/** The class representing a widget event (aka., a ZK event).
 * A widget event is the widget-level event that a widget can fire and the client application can listen.
 *
 * <p>On the other hand, a DOM event ({@link jq.Event}) is the low-level event
 * related to DOM elements. It is usually listened by the implementation of a widget, rather than the client application.
 *
 * <p> In additions, {@link zWatch} is an utility to manage the system-level events,
 * and both the client application and the widget implementation might listen.
 *
 * <p>To fire a widget event, use {@link zk.Widget#fire}.
 * To listen a widget event, use {@link zk.Widget#listen}.
 *
 * <p>See Also
 * <ul>
 * <li><a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Communication/AU_Requests">AU Requests</a></li>
 * <li><a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Communication/AU_Responses">AU Responses</a></li>
 * <li><a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">Notifications</a></li>
  * </ul>
 * <p>Common Key Codes:
 * <table cellspacing="0" cellpadding="3" border="1">
 * <tr>
 * <td>BACKSSPACE</td><td>8</td>
 * <td>TAB</td><td>9</td>
 * <td>ENTER</td><td>13</td>
 * <td>SHIFT</td><td>16</td>
 * <td>CTRL</td><td>17</td>
 * </tr><tr>
 * <td>ALT</td><td>18</td>
 * <td>ESC</td><td>27</td>
 * <td>PGUP</td><td>33</td>
 * <td>PGDN</td><td>34</td>
 * <td>END</td><td>35</td>
 * </tr><tr>
 * <td>HOME</td><td>36</td>
 * <td>LEFT</td><td>37</td>
 * <td>UP</td><td>38</td>
 * <td>RIGHT</td><td>39</td>
 * <td>DOWN</td><td>40</td>
 * </tr><tr>
 * <td>INS</td><td>45</td>
 * <td>DEL</td><td>46</td>
 * <td>F1</td><td>112</td>
 * </tr>
 * </table>
 * @disable(zkgwt)
 */
export class Event<TData = unknown> extends zk.Object {
	keys?: zul.wnd.Dimension; // zul.wnd.Window
	itemSelected?: boolean; // zul.sel.ItemWidget.prototype.doSelect_
	tooltipped?: boolean; // zul.Widget.prototype.doTooltipOver_
	contextSelected?: boolean; // zul.Widget.prototype.doClick_

	// dom.ts#metaData
	declare which: number; // non-standard. Tested in zkmax.Chosenbox.prototype.doKeyPress_
	declare metaKey: boolean;
	declare ctrlKey;
	declare altKey;
	declare shiftKey: boolean;

	// dom.ts#mouseData
	declare pageX: number;
	declare pageY: number;

	// dom.ts#keyData
	declare key: string;
	// ECMAScript is deprecating `keyCode`; see https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent/keyCode
	// However, ZK uses this field.
	declare keyCode: number; // Tested in zkmax.Chosenbox.prototype.doKeyPress_, but assumed to exist everywhere else.
	declare charCode?: number; // non-standard. Tested in zkmax.Chosenbox.prototype.doKeyPress_

	shallStop = false;

	/** The target widget (readonly).
	 * @type zk.Widget
	 * @see #currentTarget
	 */
	target: zk.Widget;
  
	/** Indicates the target which is handling this event.
	 * <p>By default, an event will be propagated to its parent, and this member tells which widget is handling it, while #target is the widget that the event is targeting.
	 * @type zk.Widget
	 * @see #target
	 */
	currentTarget?: zk.Widget;
	/** The event name, such as 'onChange'.
	 * The data which depends on the event. Here is the list of Event Data.
	 * <p>However, if data is an instance of Map, its content is copied to the event instance. Thus, you can access them directly with the event instance as follows.
<pre><code>
onClick: function (evt) {
  if (evt.altKey) { //it is the same as evt.data.altKey
  }
}
</code></pre>
	 * @type String
	 */
	name: string;
	/** The data which depends on the event. Here is the list of Event Data.
	 * <p>Data can be any javascript Object. You can bring Number, String or JSON object to server side with it.
	 * <p>The javascript JSON object will be transfered to org.zkoss.json.JSONObject at server side as the following:
<pre><code>
// at client side
var json = { "aa" : [{"a11":"11", "a12":12},{"a21":"21","a22":22.2}],
	"bb":[{"b11":"31","b12":"32"},{"b21":"41","b22":"42","b23":43}]
  },
  data = {jsonObject:json};

// at server side
final JSONObject jsonObject = (JSONObject)data.get("jsonObject"); // get JSONObject
// will output "22.2"
System.out.println(((JSONObject)((JSONArray)jsonObject.get("aa")).get(1)).get("a22"));
// will output "class java.lang.Double"
System.out.println(((JSONObject)((JSONArray)jsonObject.get("aa")).get(1)).get("a22").getClass());
</code></pre>
	 * <p>However, if data is an instance of Map, its content is copied to the event instance. Thus, you can access them directly with the event instance as follows.
<pre><code>
onClick: function (evt) {
  if (evt.altKey) { //it is the same as evt.data.altKey
  }
}
</code></pre>
	 * <p>Refer to <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Communication/AU_Requests/Server-side_Processing">ZK Client-side Reference: AU Requests: Server-side Processing</a>.
	 * @type Object
	 */
	data?: TData;
	/** The options (never null).
	 * <p>Allowed properties:
	 * <ul>
	 * <li>implicit: whether this event is an implicit event, i.e., whether it is implicit to users (so no progressing bar).</li>
	 * <li>ignorable: whether this event is ignorable, i.e., whether to ignore any error of sending this event back the server.
		<ul><li>An ignorable event is also an imiplicit event.</li></ul></li>
	 * <li>toServer: whether to send this event to the server. If specified, it is always sent no matter the widget is created at the server or not.</li>
	 * <li>uri: the URI to send the Ajax request to. If not specified, zAu#comURI is used (i.e., the desktop's update URI is used). If specified, the URI specified in this option is used -- notice that there is no encoding at all, so make sure it is correct. </li>
	 * <li>rtags (since 5.0.2): a map ({@link Map}) that can be anything and will be passed to
	 * the <code>onResponse</code> watch (see also {@link _global_.zWatch}).</li>
	 * </ul>
	 * @type Map
	 */
	opts: EventOptions;
	/** The DOM event that causes this widget event, or null if not available.
	 * @type jq.Event
	 */
	domEvent?: JQuery.TriggeredEvent;
	/** The DOM element that the event is targeting, or null if not available.
	 * @type DOMElement
	 */
	domTarget!: HTMLElement;

	/** Indicates whether the event propagation is stopped.
	 * @type boolean
	 * @see #stop
	 * @see #auStopped
	 * @see #domStopped
	 */
	stopped = false;
	/** Indicates whether to stop the sending of the AU request to the server.
	 * @type boolean
	 * @see #stop
	 * @see #stopped
	 * @see #domStopped
	 */
	auStopped = false;
	/** Indicates whether to stop the native DOM event.
	 * @type boolean
	 * @see #stop
	 * @see #stopped
	 * @see #auStopped
	 */
	domStopped = false;

	/** Constructor.
	 * @param zk.Widget target the target widget.
	 * @param String name the event name, such as onClick
	 * @param Object data [optional] the data depending on the event.
	 * @param Map opts [optional] the options. Refer to {@link #opts}
	 * @param jq.Event domEvent [optional] the DOM event that causes this widget event.
	 */
	// NOTE: There are 3 instances where `target` is set to `undefined`, but these cases are rare; search for `zk.Event(undefined`.
	constructor(target: zk.Widget | undefined, name: string, data?: TData, opts?: EventOptions, domEvent?: JQuery.TriggeredEvent) { // FIXME: TriggeredEvent missing type parameters
		super();
		this.target = target!;
		this.currentTarget = target;
		this.name = name;
		this.data = data;
		if (data && typeof data == 'object' && !Array.isArray(data))
			zk.$default(this, data);

		this.opts = opts ?? {rtags: {}};
		if (this.domEvent = domEvent)
			this.domTarget = domEvent.target as HTMLElement;
	}
	/** Adds the additions options to {@link #opts}.
	 * @param Map opts a map of options to append to #opts
	 */
	addOptions(opts: EventOptions): void {
		this.opts = Object.assign(this.opts, opts);
	}
	/** Stop the event propagation.
<pre><code>
evt.stop();
evt.stop({propagation:true}); //stop only the event propagation (see below)
evt.stop({au:true}); //stop only the sending of the AU request
</code></pre>
	 *<p>If you want to revoke the stop of the event propagation, you can specify {revoke:true} to the opts argument.
<pre><code>
evt.stop({progagation:true,revoke:true}); //revoke the event propagation
</code></pre>
	* <p>Notice that the event won't be sent to the server if stop() was called.
	*
	* @param Map opts [optional] control what to stop.
	* If omitted, the event propagation ({@link #stopped}) and the native DOM event ({@link #domStopped}) are both stopped
	* (but not {@link #auStopped}).
	* For fine control, you can use a combination of the following values:
	<ul>
	<li>revoke - revoke the stop, i.e., undo the last invocation of {@link #stop}</li>
	<li>propagation - stop (or revoke) the event propagation ({@link #stopped}).</li>
	<li>dom - stop (or revoke) the native DOM event ({@link #domStopped}).</li>
	<li>au - stop (or revoke) the sending of the AU request to the server ({@link #auStopped}).
	Notice that, unlike the propagation and dom options, the sending of AU requests won't be stopped if opts is omitted.
	In other words, to stop it, you have to specify the au option explicitly. </li>
	</ul>
	*/
	stop(opts?: EventStopOptions): void {
		var b = !opts || !opts.revoke;
		if (!opts || opts.propagation) this.stopped = b;
		if (!opts || opts.dom) this.domStopped = b;
		if (opts && opts.au) this.auStopped = b;
	}
	/** Indicates whether a key is currently pressed.
	 * You can also pass more then one key into this method as a key combination, but combination only works with modifier keys(ALT/CONTROL/SHIFT/META).
	 * If you combine two(or more) keys that are not modifier keys, you will always get an exception since it is invalid.
	 * For example:
	 * isPressed(zKeys.CONTROL, zKeys.ALT, zKeys.ARROWDOWN) is valid.
	 * isPressed(zKeys.ARROWDOWN, zKeys.ARROWUP) is invalid.
	 * @param String... The target key and the modifier keys
	 * @return boolean
	 * @since 9.5.0
	 * @see _global_.zKeys
	 */
	isPressed(): boolean {
		let keyCount = 0,
			result = true;
		for (var i = 0, len = arguments.length; i < len; i++) {
			var arg = arguments[i] as string;
			if (arg != zKeys.META && arg != zKeys.ALT && arg != zKeys.CONTROL && arg != zKeys.SHIFT) {
				keyCount++;
				if (keyCount > 1)
					throw 'Invalid key combination';
				else if (arg != this.key)
					result = false;
			} else if ((arg == zKeys.META && !this.metaKey) || (arg == zKeys.ALT && !this.altKey)
					|| (arg == zKeys.CONTROL && !this.ctrlKey) || (arg == zKeys.SHIFT && !this.shiftKey)) {
				return false;
			}
		}
		return result;
	}
}
export interface ClientActivity {
	_onSyncScroll: {_onSyncScroll} | [unknown, CallableFunction];
	_beforeSizeForRead: {_beforeSizeForRead} | [unknown, CallableFunction];
	beforeSize: {beforeSize} | [unknown, CallableFunction];
	afterSize: {afterSize} | [unknown, CallableFunction];
	onBindLevelChange: {onBindLevelChange} | [unknown, CallableFunction];
	onBindLevelMove: {onBindLevelMove} | [unknown, CallableFunction];
	onFitSize: {onFitSize} | [unknown, CallableFunction];
	onHide: {onHide} | [unknown, CallableFunction];
	onFloatUp: {onFloatUp} | [unknown, CallableFunction];
	onResponse: {onResponse} | [unknown, CallableFunction];
	onCommandReady: {onCommandReady} | [unknown, CallableFunction];
	onRestore: {onRestore} | [unknown, CallableFunction];
	onScroll: {onScroll} | [unknown, CallableFunction];
	onSend: {onSend} | [unknown, CallableFunction];
	onSize: {onSize} | [unknown, CallableFunction];
	onShow: {onShow} | [unknown, CallableFunction];
	onVParent: {onVParent} | [unknown, CallableFunction];
	onMove: {onMove} | [unknown, CallableFunction];
}

export interface FireOptions {
	reverse: boolean;
	timeout: number;
	triggerByFocus: boolean;
	triggerByClick: number;
	rtags: Record<string, unknown>;
}

type WatchInfo = [zk.Widget, (zk.Widget | [zk.Widget, CallableFunction])[]];
let _visiEvts = {onFitSize: true, onSize: true, onShow: true, onHide: true, beforeSize: true, afterSize: true},
	_watches: Record<string, WatchInfo[]> = {}, //Map(watch-name, [object, [watches..]]) [0]: obj, [1]: [inf]
	_dirty: boolean;

// See `ctl.orign` of `zul.wgt.Popup.prototype.onFloatUp`.
// `ZWatchController` was previously named `_Gun` and not exported.
// To export this class, the variables above and functions below are pulled out from `zWatch` (exported later in this file).
export class ZWatchController extends zk.Object {
	name: string;
	xinfs: WatchInfo[];
	args: unknown[];
	origin: zk.Object;
	fns?: [CallableFunction, zk.Widget][];

	constructor(name: string, xinfs: WatchInfo[], args: unknown[], org: zk.Object, fns?: [CallableFunction, zk.Widget][]) {
		super();
		this.name = name;
		this.xinfs = xinfs;
		this.args = args;
		this.origin = org;
		this.fns = fns;
	}
	fire(ref?: zk.Widget): void {
		let infs: (zk.Widget | [zk.Widget, CallableFunction])[] | undefined, xinf: WatchInfo | undefined,
			name = this.name,
			xinfs = this.xinfs,
			args = this.args,
			fns = this.fns;
		if (ref) {
			for (var j = 0, l = xinfs.length; j < l; ++j)
				if (xinfs[j][0] == ref) {
					infs = xinfs[j][1];
					xinfs.splice(j--, 1);
					--l;
					_invoke(name, infs, ref, args, fns);
				}
		} else
			while (xinf = xinfs.shift())
				_invoke(name, xinf[1], xinf[0], args, fns);
	}
	fireDown(ref: zk.Widget): void {
		if (!ref || ref.bindLevel == null)
			this.fire(ref);

		(new ZWatchController(this.name, _visiChildSubset(this.name, this.xinfs, ref, true), this.args, this.origin, this.fns))
		.fire();
	}
}

function _invoke(name: string, infs: (zk.Widget | [zk.Widget, CallableFunction])[], o: zk.Widget, args: unknown[], fns?: [CallableFunction, zk.Widget][]): void {
	for (let j = 0, l = infs.length; j < l;) {
		const f = _fn(infs[j++], o, name);

		if (fns)
			fns.push([f, o]); //store it fns first
		else
			f.bind(o)(...(args as []));
	}
	if (name == 'onSize') { //Feature ZK-1672: invoke onAfterSize after onSize
		var after = o.onAfterSize;
		if (after)
			after.call(o, ...(args as []));
	}
}
//Returns if c is visible
function _visible(name: string, c: zk.Widget): boolean {
	return !!(c.isWatchable_ && c.isWatchable_(name)); //in future, c might not be a widget
}
//Returns if c is a visible child of p (assuming p is visible)
function _visibleChild(name: string, p: zk.Widget, c: zk.Widget, cache: Record<string, unknown>): boolean {
	for (let w: zk.Widget | undefined = c; w; w = w.parent)
		if (p == w) //yes, c is a child of p
			return !cache || c.isWatchable_(name, p, cache);
	return false;
}
//Returns subset of xinfs that are visible and childrens of p
function _visiChildSubset(name: string, xinfs: WatchInfo[], p: zk.Object & {bindLevel?: number}, remove?: boolean): WatchInfo[] {
	let found: WatchInfo[] = [], bindLevel = p.bindLevel,
		cache = _visiEvts[name] ? {} : false, pvisible;
	if (p instanceof zk.Widget) {//in future, w might not be a widget
		for (let j = xinfs.length; j--;) {
			const xinf = xinfs[j],
				o = xinf[0],
				diff = bindLevel ? bindLevel > o.bindLevel : false;
			if (diff) //neither ancestor, nor this (nor sibling)
				break;

			if (!pvisible && cache) { //not cached yet
				if (!(pvisible = _visible(name, p as never))) //check p first (since _visibleChild checks only o)
					break; //p is NOT visible
				cache[p.uuid] = true; //cache result to speed up _visiChild
			}

			if (_visibleChild(name, p as never, o, cache)) {
				if (remove)
					xinfs.splice(j, 1);
				found.unshift(xinf); //parent first
			}
		}
	}
	return found;
}
function _visiSubset(name: string, xinfs: WatchInfo[]): WatchInfo[] {
	xinfs = xinfs.$clone(); //make a copy since unlisten might happen
	if (_visiEvts[name])
		for (var j = xinfs.length; j--;)
			if (!_visible(name, xinfs[j][0]))
				xinfs.splice(j, 1);
	return xinfs;
}
function _target(inf: zk.Widget | [zk.Widget, CallableFunction]): zk.Widget {
	return Array.isArray(inf) ? inf[0] : inf;
}
function _fn(inf, o: zk.Widget, name: string): CallableFunction {
	const fn = (Array.isArray(inf) ? inf[1] : o[name]) as undefined | CallableFunction;
	if (!fn)
		throw (o.className || o) + ':' + name + ' not found';
	return fn;
}
function _sync(): void {
	if (!_dirty) return;

	_dirty = false;
	for (var nm in _watches) {
		var wts = _watches[nm];
		if (wts.length && wts[0][0].bindLevel != null)
			wts.sort(_cmpLevel);
	}
}
function _bindLevel(a: {bindLevel?: number}): number {
	let a1;
	return (a1 = a.bindLevel) == null || isNaN(a1 as number) ? -1 : a1 as number;
}
function _cmpLevel(a: WatchInfo, b: WatchInfo): number {
	return _bindLevel(a[0]) - _bindLevel(b[0]);
}
zk._zsyncFns = _zsyncFns;
export function _zsyncFns(name: string, org: zk.Object): void {
	if (name == 'onSize' || name == 'onShow' || name == 'onHide') {
		jq.zsync(org);
		if (name == 'onSize')
			setTimeout(zk.doAfterResize, 20); // invoked after mounted
	}
	if (name == 'onResponse')
		jq.doSyncScroll();
}
//invoke fns in the reverse order
function _reversefns(fns: undefined | [CallableFunction, zk.Widget][], args: unknown[]): void {
	if (fns)
		//we group methods together if their parents are the same
		//then we invoke them in the normal order (not reverse), s.t.,
		//child invokes firsd, but also superclass invoked first (first register, first call if same object)
		for (var j = fns.length, k = j - 1, i: number, f: [CallableFunction, zk.Widget], oldp: undefined | zk.Widget, newp: undefined | zk.Widget; j >= 0;) {
			if (--j < 0 || (oldp != (newp = fns[j][1].parent) && oldp)) {
				for (i = j; ++i <= k;) {
					f = fns[i];
					f[0].bind(f[1])(...args);
				}
				k = j;
			}
			oldp = newp;
		}
}
function _fire(name: string, org: zk.Object & {bindLevel?}, opts: EventOptions | undefined | unknown, vararg: IArguments): void {
	const wts = _watches[name];
	if (wts && wts.length) {
		const down = opts && (opts as EventOptions).down && org.bindLevel != null;
		if (down) _sync();

		const args: unknown[] = [],
			fns = opts && (opts as EventOptions).reverse ? [] : undefined,
			gun = new ZWatchController(name,
				down ? _visiChildSubset(name, wts, org) : _visiSubset(name, wts),
				args, org, fns);
		args.push(gun);
		for (var j = 2, l = vararg.length; j < l;) //skip name and origin
			args.push(vararg[j++]);

		if (opts && (opts as EventOptions).timeout != null && (opts as EventOptions).timeout! >= 0)
			setTimeout(function () {
				gun.fire();
				_reversefns(fns, args);
				zk._zsyncFns(name, org);
			}, (opts as EventOptions).timeout);
		else {
			gun.fire();
			_reversefns(fns, args);
			zk._zsyncFns(name, org);
		}
	} else
		zk._zsyncFns(name, org);
}
//Feature ZK-1672: check if already listen to the same listener
function _isListened(wts: unknown[] | undefined, inf: unknown): boolean {
	if (wts) {
		if (Array.isArray(inf)) {
			let isListen = false;
			for (let i = wts.length; i > 0; i--) {
				if (Array.isArray(wts[i]) && (wts[i] as []).$equals(inf)) {
					isListen = true;
					break;
				}
			}
			return isListen;
		}
		return wts.$contains(inf);
	}
	return false;
}

export namespace evt_global {
	/** @class zWatch
	 * @import zk.Widget
	 * <p>An utility to manage watches.
	 *
	 * <p>A watch is a system-level event, such as onSize and beforeSize. For example, when an AU request is going to be sent to the server, the onSend watch is fired so the client application and/or the widget implementation can listen to it.
	 *
	 * <p>Here is a full list of <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications/Client_Activity_Watches">Client Activity Watches</a></li>.

	<h3>Add a Watch</h3>

	<p>To add a listener to a watch, use {@link #listen}. The listener must implement a method with the same as the action name. For example,
	<pre><code>
	MyListener = zk.$extends(zk.Object, {
	onSend: function() {
	}
	});
	var ml = new MyListener();
	zWatch.listen({onSend: ml})
	</code></pre>

	<p>Then, ml.onSend will be called when sending an AU request.
	<h3>Invocation Sequence</h3>
	<h4>Sequence of {@link #fireDown}</h4>

	<p>The watch listener is added in the parent-first sequence if it has a method called getParent, or a member called parent (a typical example is {@link Widget}). Thus, the parent will be called before its children, if they are all registered to the same action.
	*/
	export class zWatch {
		/** Registers watch listener(s). For example,
	<pre><code>
	zWatch.listen({
	onSize: this,
	onShow: this,
	onHide: [this, this._onHide]
	});
	</code></pre>
		* <p>As shown above, each key of the infs map is the watch name, and each value is the target against which the watch listener will be called, or a two-element array, where the first element is the target and the second the listener function. For example, zWatch({onSize: foo}) will cause foo.onSize to be called when onSize is fired. The arguments passed are the same as {@link #fire}/{@link #fireDown}.
		* <p>Note: the order is parent-first (if the watch has a method called getParent or a member called parent), so the invocation ({@link #fire}) is from the parent to the child if both are registered.
		* @param Map infs a map of the watch listeners. Each key of the map is the watch name, and each value is the target or a two-element array, where the first element is the target and the second the listener function. It assumes the target implements the method with the same name as the watch name. In addition, when the method is called, this references to the target.
		*/
		static listen(infs: Partial<ClientActivity>): void {
			for (const name in infs) {
				const wts = _watches[name],
					inf = infs[name] as zk.Widget | [zk.Widget, CallableFunction],
					o = _target(inf),
					xinf: WatchInfo = [o, [inf]];

				if (wts) {
					var bindLevel = o.bindLevel;
					if (bindLevel != null) {
						for (var j = wts.length; ;) {
							if (--j < 0) {
								wts.unshift(xinf);
								break;
							}
							if (wts[j][0] == o) {
								if (!_isListened(wts[j][1], inf)) //Feature ZK-1672: check if already listened
									wts[j][1].push(inf);
								break;
							}
							if (bindLevel >= wts[j][0].bindLevel) { //parent first
								wts.splice(j + 1, 0, xinf);
								break;
							}
						}
					} else
						for (var j = wts.length; ;) {
							if (--j < 0) {
								wts.push(xinf);
								break;
							}
							if (wts[j][0] == o) {
								wts[j][1].push(inf);
								break;
							}
						}
				} else {
					_watches[name] = [xinf];
				}
			}
		}
		/** Removes watch listener(s).
		 * @param Map infs a map of watch listeners. Each key is the watch name, and each value is the target or or a two-element array, where the first element is the target and the second the listener function.
		 */
		static unlisten(infs: Partial<ClientActivity>): void {
			for (var name in infs) {
				var wts = _watches[name];
				if (wts) {
					const inf = infs[name] as zk.Widget | [zk.Widget, CallableFunction],
						o = _target(inf);
					for (var j = wts.length; j--;)
						// ZK-3605 might failed to remove listener because we remove in reverse order
						// listeners listening on the same event might be shadowed and failed to remove
						// should continue searching if the event name is a match but failed to remove
						if (wts[j][0] == o && wts[j][1].$remove(inf)) {
							if (!wts[j][1].length)
								wts.splice(j, 1);
							break;
						}
				}
			}
		}
		/** Removes all listener of the specified watch.
		 * @param String name the watch name, such as onShow
		 */
		static unlistenAll(name: keyof ClientActivity): void {
			delete _watches[name];
		}
		/** Fires an watch that invokes all listeners of the watch.
		 * <p>For example, zWatch.fire('onX', null, 'a', 123) will cause
		 * ml.onX(ctl, 'a', 123) being called -- assuming ml is a listener of onX.
		 * <p>Notice that the first argument (ctl in the above example) is a special
		 * controller.
		 * The first two argument of {@link #fire} become part of the control
		 * (as the name and origin fields).
		 * In additions, the control can be used to control the invocation sequence.
		 * For example, the invocation sequence is, by default, evaluated in the
		 * order of fist-listen-first-call, and you can use the controller to force
		 * the listeners of a certain target to be called first as follows.
	<pre><code>
	onX: function (ctl) {
	ctl.fire(specialTarget); //enforce the listeners of specialTarget to execute first
	....
	}
	</code></pre>

		* <p>If you want the listeners of descendants to execute too, use fireDown instead as follows:
	<pre><code>
	onX: function (ctl) {
	ctl.fireDown(specialTarget); //enforce the listeners of specialTarget and descendants to execute first
	....
	}
	</code></pre>
		* @param String name the watch name, such as onFloatUp.
		* @param Object origin the origin (optional).
		* It could be anything and it will become the origin member of the special controller (the first argument of the listener)
		* @param Map opts [optional] options:
		* <ul>
		*<li>reverse - whether to reverse the execution order.
		* If false or omitted, the parent is called first.
		* If true, the child is called first. Notice that there is a limitation: if reverse, you can invoke
		* <code>ctl.fire</code> in the callback.</li>
		* <li>timeout - how many miliseconds to wait before calling the listeners. If Omitted or negative, the listeners are invoked immediately.</li>
		* @param Object... vararg any number of arguments to pass to the listener. They will become the second, third and following arguments when the listener is called.
		*/
		static fire(name: string, org: zk.Object, opts?: Partial<FireOptions> | unknown): void {
			_fire(name, org, opts, arguments);
		}
		/** Fires an watch but invokes only the listeners that are a descendant of the specified origin.
		 * <p>By descendant we mean the watch listener is the same or an descendant of the specified origin. In other words, if the specified origin is not the ancestor of a watch listener, the listener won't be called.
		 *
		 * <p>Notice that it assumes:
		 * <ol>
		 * <li>The watch listener's parent can be retrieved by either a method called getParent, or a property called parent.</li>
		 * <li>It has a data member called bindLevel indicating which level the object in the parent-child tree.</li>
		 * </ol>
		 * <p>{@link Widget} is a typical example ({@link Widget#parent} and {@link Widget#bindLevel}).
		 *
		 * <p>For example, zWatch.fireDown('onX', wgt, opts, 'a', 123) will cause ml.onX(ctl, opts, 'a', 123) being called -- assuming ml is a listener of onX and zUtl.isAncestor(wgt, ml) is true (zUtl#isAncestor).
		 * <p>Notice that the first argument (ctl in the above example) is a special controller that a listen can use to do further control. For example, origin (of fire()) can be retrieved by accessing the member of the controller called origin.
	<pre><code>
	onSize: function (ctl) {
	if (ctl.origin) //retrieve the origin
	...
	</code></pre>
		* <p>Notice that the second argument (opts in the above example) is also a special argument used to pass optional control info to the zWatch engine.
		* <p>The invocation sequence is, by default, evaluated in the order of parent-first, and you can use the controller to change it. For example, the following will cause the listener of specialTarget, if any, to execute first.
	<pre><code>
	onX: function (ctl) {
	ctl.fire(specialTarget); //enfore the listeners of specialTarget to execute first
	....
	}
	</code></pre>

		* <p>If you want the listeners of descendants to execute too, use fireDown instead as follows:
	<pre><code>
	onX: function (ctl) {
	ctl.fireDown(specialTarget); //enfore the listeners of specialTarget and descendants to execute first
	....
	}
	</code></pre>
		* <p>It is useful if a listener depends some of its children's listeners to complete (notice that the parent's listener is, by default, called first). For example, when onSize of a widget is called, it might want some of its children's onSiz to be called first (so he can have their updated size).
		* @param String name the watch name, such as onShow.
		* @param Object origin the reference object used to decide what listeners to invoke (required). Notice, unlike {@link #fire}, it cannot be null. It will become the origin member of the controller (i.e., the first argument when the listener is called).
		* @param Map opts [optional] options:
		* <ul>
		*<li>reverse - whether to reverse the execution order.
		* If false or omitted, the parent is called first.
		* If true, the child is called first. Notice that there is a limitation: if reverse, you can invoke
		* <code>ctl.fireDown</code> in the callback.</li>
		* <li>timeout - how many miliseconds to wait before calling the listeners. If Omitted or negative, the listeners are invoked immediately.</li></ul>
		* @param Object... vararg any number of arguments to pass to the listener. They will become the third, forth, and following arguments when the listener is called.
		*/
		static fireDown(name: string, org: zk.Object, opts?: Partial<FireOptions>, ...vararg: unknown[]): void {
			_fire(name, org, zk.copy(opts, {down: true}), arguments);
		}
		static onBindLevelMove(): void { //internal
			_dirty = true;
		}
	}
}
zk.copy(window, evt_global);
zWatch.listen({onBindLevelMove: zWatch});
zk.Event = Event;