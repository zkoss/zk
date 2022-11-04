/* mount.js

	Purpose:

	Description:

	History:
		Sat Oct 18 19:24:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
export namespace mount_global {
	//define a package and returns the package info (used in WpdExtendlet)
	export function zkpi(nm: string, wv: boolean): Record<string, unknown> | undefined {
		return zk.isLoaded(nm) ? undefined : {n: nm, p: zk.$package(nm, false, wv)};
	}

	//ZK JSP: page creation (backward compatible)
	export function zkpb(pguid: string, dtid: string, contextURI: string, updateURI: string, resourceURI: string, reqURI: string, props: Record<string, string>): void {
		if (props === undefined && typeof reqURI !== 'string') { //ZK-4827: for backward compatible (other addons, ex. zuljsp)
			props = reqURI;
			reqURI = resourceURI;
			resourceURI = updateURI;
		}
		window.zkx([0, pguid,
			Object.assign(props ?? {}, {
				dt: dtid,
				cu: contextURI,
				uu: updateURI,
				rsu: resourceURI,
				ru: reqURI
			}), {}, []]);
	}

	//ZK JSP (useless; backward compatible)
	export const zkpe = zk.$void;

	//Initializes with version and options
	export function zkver(ver: string, build: string, ctxURI: string, updURI: string, modVers: Record<string, string>, opts: Record<string, unknown>): void {
		zk.version = ver;
		zk.build = build;
		zk.contextURI = ctxURI;
		zk.updateURI = updURI;

		for (var nm in modVers)
			zk.setVersion(nm, modVers[nm]);

		if (!zk.feature)
			zk.feature = {standard: true};
		window.zkopt(opts);
	}

	//Define a mold
	export function zkmld(wgtcls: Record<string, unknown>, molds: Record<string, (() => void)>): void {
		if (!wgtcls.$oid) {
			zk.afterLoad(function () {
				window.zkmld(wgtcls, molds);
			});
			return;
		}

		var ms = wgtcls.molds = {};
		for (var nm in molds) {
			var fn = molds[nm];
			// eslint-disable-next-line @typescript-eslint/dot-notation
			ms[nm] = typeof fn == 'function' ? fn : fn[0]['molds'][fn[1]];
		}
	}

	//Run Ajax-as-a-service's main
	export function zkamn(pkg: string, fn: (() => void)): void {
		zk.load(pkg, function () {
			setTimeout(function () {
				zk.afterMount(fn);
			}, 20);
		});
	}
}

interface Pcai {
	s: number;
	e: number;
	f0: (() => void)[];
	f1: (() => void)[];
	i?: number;
}

interface MountContext {
	curdt?: zk.Desktop;
	bindOnly: boolean;
}
type WidgetType = number | string;
type Uuid = string;
type WidgetProps = Record<'dt' | 'cu' | 'uu' | 'rsu' | 'ru' | 'ow' | 'wc' | string, unknown>;
type ShadowProps = Record<string, unknown[]>;
type WidgetInfo = [WidgetType, Uuid, WidgetProps, ShadowProps, WidgetInfo[], string?/*mold*/];
type AuCmds = string[];
//extra is [stub-fn, filter] if AU,  aucmds if BL
type ExtraInfo = [CallableFunction, CallableFunction] | AuCmds;
type InfoBeforeLoad = [zk.Desktop, WidgetInfo, boolean | undefined, zk.Widget | undefined, ExtraInfo?] & {pked?};

var Widget = zk.Widget,
	_wgt_$ = Widget.$, //the original zk.Widget.$
	_crInfBL0: InfoBeforeLoad[] = [], _crInfBL1: [zk.Desktop, zk.Widget, boolean | undefined, ExtraInfo?][] = [], //create info for BL
	_crInfAU0: InfoBeforeLoad[] = [], //create info for AU
	_aftMounts: (() => void)[] = [], //afterMount
	_aftResizes: (() => void)[] = [], //afterResize
	_mntctx: Partial<MountContext> = {}, //the context
	_paci: Pcai | undefined = {s: 0, e: -1, f0: [], f1: []}, //for handling page's AU responses
	_t0 = Date.now();

//Issue of handling page's AU responses
//1. page's AU must be processed after all zkx(), while they might be added
//  before zkx (such as test/test.zhtml), or multiple zkx (such jspTags.jsp)
//2. mount.js:_startCheck must be called after processing page's AU
//  (otherwise, zksandbox will jump to #f1 causing additional step)
//Note: it is better to block zAu but the chance to be wrong is low --
//a timer must be started early and its response depends page's AU
jq(function () {
	/** @internal */
	function _stateless(): boolean {
		var dts = zk.Desktop.all;
		for (var dtid in dts)
			if (dts[dtid].stateless) return true;
		return false;
	}

	if (!_paci) return;
	_paci.i = window.setInterval(function () {
		var stateless;
		if (_paci != undefined && ((zk.booted && !zk.mounting) || (stateless = _stateless())))
			if (stateless || _paci.s == _paci.e) { //done
				clearInterval(_paci.i);
				var fs = _paci.f0.concat(_paci.f1);
				_paci = undefined;
				for (var f: undefined | CallableFunction; (f = fs.shift());)
					f();
			} else
				_paci.e = _paci.s;
	}, 25);
});
//run after page AU cmds
zk._apac = _apac;
export function _apac(fn: () => void, _which_?: string): void {
	if (_paci) {
		(_paci[_which_ || 'f1'] as CallableFunction[]).push(fn);
		return;
	}
	zk.afterMount(fn); //it might happen if ZUML loaded later (with custom JS code)
}

/**
 * Adds a function that will be executed after the mounting is done. By mounting we mean the creation of peer widgets.
 * <p>By mounting we mean the creation of the peer widgets under the
 * control of the server. To run after the mounting of the peer widgets,
 * <p>If the delay argument is not specified and no mounting is taking place,
 * the function is executed with `setTimeout(fn, 0)`.
 * @param fn - the function to execute after mounted
 * @param delay - (since 5.0.6) how many milliseconds to wait before execute if
 * there is no mounting taking place. If omitted, 0 is assumed.
 * If negative, the function is executed immediately (if no mounting is taking place).
 * @returns true if this method has been called before return (delay must
 * be negative, and no mounting); otherwise, undefined is returned.
 * @see {@link mounting}
 * @see {@link afterLoad}
 * @see {@link afterAnimate}
 */
export function afterMount(fn?: () => void, delay?: number): boolean { //part of zk
	if (fn) {
		if (!jq.isReady)
			jq(function () {
				zk.afterMount(fn);
			}); //B3278524
		else if (zk.mounting)
			_aftMounts.push(fn); //normal
		else if (zk.loading)
			zk.afterLoad(fn);
		else if (delay !== undefined && delay < 0) {
			fn();
			return true; //called
		} else
			setTimeout(fn, delay);
	}
	return false;
}
zk.afterMount = afterMount;

/**
 * Adds a function that will be executed after all of the onSize events are done.
 * <p>Here lists the execution phases:
 * <ol>
 *     <li>After the page loaded, the function added in the afterResze() will be invoked</li>
 *     <li>After the browser resized, the function added in the afterResze() will be invoked</li>
 *     <li>After zWatch.fire/fireDown('onSize'), the function added in the afterResze() will be invoked</li>
 * </ol>
 * </p>
 * @param fn - the function to execute after resized
 * @since 6.5.2
 */
export function afterResize(fn: () => void): void {
	if (fn)
		_aftResizes.push(fn);
}
zk.afterResize = afterResize;

export function doAfterResize(): void {
	for (var fn: undefined | CallableFunction; fn = _aftResizes.shift();) {
		fn();
	}
}
zk.doAfterResize = doAfterResize;

function _curdt(): zk.Desktop {
	return _mntctx.curdt ?? (_mntctx.curdt = zk.Desktop.$());
}

//Load all required packages
function mountpkg(infs: InfoBeforeLoad[]): void {
	var types: Record<string, zk.Desktop> = {};
	for (var j = infs.length; j--;) {
		var inf = infs[j];
		if (!inf.pked) { //mountpkg might be called multiple times before mount()
			inf.pked = true;
			getTypes(types, inf[0], inf[1] as never);
		}
	}

	for (var type in types) {
		var index: number = type.lastIndexOf('.');
		if (index >= 0)
			zk._load(type.substring(0, index), types[type]); //use _load for better performance
	}
}

//Loads package of a widget tree
function getTypes(types: Record<string, zk.Desktop>, dt: zk.Desktop, wi: WidgetInfo): void {
	var type = wi[0];
	if (type === 0) //page
		type = wi[2].wc as string;
	else if (type === 1) //1: zhtml.Widget
		wi[0] = type = 'zhtml.Widget';
	if (type)
		types[type] = dt;

	for (var children = wi[4], j = children.length; j--;) {
		getTypes(types, dt, children[j] as never);
	}
}

//mount for browser loading
function mtBL(): void {
	for (; ;) {
		if (zk.loading) {
			zk.afterLoad(mtBL); //later
			return;
		}

		var inf = _crInfBL0.shift();
		if (!inf)
			break; //done

		_crInfBL1.push([inf[0], create(inf[3] || inf[0], inf[1] as never, true), inf[2], inf[4]]);
		//inf[0]: desktop used as default parent if no owner
		//inf[3]: owner passed from zkx
		//inf[2]: bindOnly
		//inf[4]: aucmds (if BL)
		//true: don't update DOM

		if (breathe(mtBL))
			return; //mtBL has been scheduled for later execution
	}

	mtBL0();
}

function mtBL0(): void {
	for (; ;) {
		if (_crInfBL0.length)
			return; //another page started

		if (zk.loading) {
			zk.afterLoad(mtBL0);
			return;
		}

		var inf = _crInfBL1.shift();
		if (!inf) break;

		var wgt: zk.Widget = inf[1];
		if (inf[2])
			wgt.bind(inf[0]); //bindOnly
		else {
			var $jq: JQuery | undefined;
			if (zk.processing
				&& ($jq = jq('#zk_proc')).length) {
				if ($jq.hasClass('z-loading') && $jq.parent().hasClass('z-temp')) {
					$jq[0].id = 'zna';
					if (!jq('#zk_proc').length) //B65-ZK-1431: check if progressbox exists
						zUtl.progressbox('zk_proc', window.msgzk ? msgzk.PLEASE_WAIT : 'Processing...', true);
				}
			}
			wgt.replaceHTML('#' + wgt.uuid, inf[0]);
		}

		doAuCmds(inf[3] as AuCmds); //aucmds
	}

	mtBL1();
}

function mtBL1(): void {
	if (_crInfBL0.length || _crInfBL1.length)
		return; //another page started

	zk.booted = true;
	zk.mounting = false;
	doAfterMount(mtBL1);
	_paci && ++_paci.s;
	if (!zk.clientinfo) {// if existed, the endProcessing() will be invoked after onResponse()
		zk.endProcessing();
	} else {
		mtAU(); // resume last not done yet (=zk.mounting) clientinfo ZKAU
	}

	zk.bmk.onURLChange();
	if (zk.pfmeter) {
		var dts = zk.Desktop.all;
		for (var dtid in dts)
			zAu._pfdone(dts[dtid], dtid);
	}
}

/* mount for AU */
function mtAU(): void {
	for (; ;) {
		if (zk.loading) {
			zk.afterLoad(mtAU);
			return;
		}

		var inf = _crInfAU0.shift(), filter, wgt;
		zk._crWgtUuids.shift(); //sync to _crInfAU0
		if (!inf)
			break; //done

			if (filter = inf[4]![1]) {//inf[4] is extra if AU
				_wgt_$ = Widget.$; // update the latest one, if somewhere else override it already.
				Widget.$ = function <T extends zk.Widget> (n, opts?: Partial<{exact: boolean; strict: boolean; child: boolean}>): T | undefined {
					return (filter as CallableFunction)(_wgt_$(n, opts)) as T | undefined;
				};
			}
		try {
			wgt = create(undefined, inf[1] as never);
		} finally {
			if (filter && _wgt_$) Widget.$ = _wgt_$;
		}
		(inf[4]![0] as CallableFunction)(wgt); //invoke stub

		if (breathe(mtAU))
			return; //mtAU has been scheduled for later execution
	}
	mtAU0();
}

function mtAU0(): void {
	zk.mounting = false;
	doAfterMount(mtAU0);

	zAu._doCmds(); //server-push (w/ afterLoad) and _pfdone
	doAfterMount(mtAU0);
}

function doAfterMount(fnext: CallableFunction): boolean {
	for (var fn: CallableFunction | undefined; fn = _aftMounts.shift();) {
		fn();
		if (zk.loading) {
			zk.afterLoad(fnext); //fn might load packages
			return true; //wait
		}
	}
	return false;
}

function doAuCmds(cmds?: AuCmds): void {
	if (cmds && cmds.length)
		zk._apac(function () {
			for (var j = 0; j < cmds.length; j += 2)
				zAu.process(cmds[j], cmds[j + 1]);
		}, 'f0');
}

/* create the widget tree. */

function create(parent: zk.Widget | undefined, wi: WidgetInfo, ignoreDom?: boolean): zk.Widget {
	let nm;
	var wgt: zk.Widget, stub: boolean,
		type = wi[0],
		uuid = wi[1],
		props: WidgetProps = wi[2] || {},
		seProps: ShadowProps = wi[3] || {};
	if (type === 0) { //page
		type = zk.cut(props, 'wc') as string;
		const cls = type ? zk.$import(type) as typeof zk.Page : zk.Page;
		(wgt = new cls({uuid: uuid}, zk.cut(props, 'ct') as boolean)).inServer = true;
		if (parent) parent.appendChild(wgt, ignoreDom);
	} else {
		if ((stub = type == '#stub') || type == '#stubs') {
			if (!(wgt = (_wgt_$ || Widget.$)(uuid) //use the original one since filter() might applied
				|| zAu._wgt$(uuid))) //search detached (in prev cmd of same AU)
				throw 'Unknown stub ' + uuid;
			var w = new zk.Widget();
			//Bug ZK-1596: may already unbind
			//Bug ZK-1821: should also unbind wgt if in ROD status
			if (wgt.desktop || wgt.z_rod === 9)
				wgt.unbind(); //reuse it as new widget, bug ZK-1589: should unbind first then replace
			zk._wgtutl.replace(wgt, w, stub);
			//to reuse wgt, we replace it with a dummy widget, w
			//if #stubs, we have to reuse the whole subtree (not just wgt), so don't move children
		} else {
			const cls = zk.$import(type as string) as typeof zk.Widget;
			if (!cls)
				throw 'Unknown widget: ' + type;
			(wgt = new cls(window.zkac)).inServer = true;
			//zkac used as token to optimize the performance of zk.Widget.$init
			wgt.uuid = uuid;
			const v = wi[5];
			if (v)
				wgt.setMold(v);
		}
		if (parent) parent.appendChild(wgt, ignoreDom);

		//z$is: IdSpace
		//There are two ways to specify IdSpace at client
		//1) Override $init and assign _fellows (e.g., Macro/Include/Window)
		//2) Assign this.z$is to true (used by AbstractComponent.java)
		if (zk.cut(props, 'z$is')) {
			// eslint-disable-next-line @typescript-eslint/dot-notation
			wgt['z$is'] = true;
			wgt._fellows = {};
		}

		const al = zk.cut(props, 'z$al') as Record<string, CallableFunction> | undefined;
		//z$al: afterLoad
		if (al)
			zk.afterLoad(function () {
				for (const p in al)
					wgt.set(p, al[p]() as never, true); //value must be func; fromServer
			});

		const sal = zk.cut(seProps, 'z$al') as Record<string, CallableFunction>[] | undefined;
		if (sal)
			zk.afterLoad(function () {
				for (var i = 0, j = sal.length; i < j; i++) {
					const vv = sal[i];
					for (var p in vv)
						wgt.set(p, vv[p]() as never, true); //value must be func; fromServer
				}
			});
	}

	for (nm in props)
		wgt.set(nm as string, props[nm as string] as never, true); //fromServer

	for (nm in seProps) {
		const seProp = seProps[nm as never];
		for (var i = 0, length = seProp.length; i < length; i++) {
			const vv = seProp[i];
			wgt.set(nm as string, vv as never, true); //fromServer
		}
	}

	for (var j = 0, childs = wi[4], len = childs.length;
		 j < len; ++j)
		create(wgt, childs[j] as never);

	// call afterCompose_() after zk.loaded, because some packages may not
	// be loaded yet or call afterCompose_() directly if every required
	// packages are loaded.
	if (zk.loading) {
		zk.afterLoad(() => wgt.afterCompose_());
	} else {
		wgt.afterCompose_();
	}
	return wgt;
}

/* Schedules fn for later execution if it takes too long to boot up,
 * so progressbox has a chance to show
 */
function breathe(fn: CallableFunction): boolean {
	var t = Date.now(), dt = t - _t0;
	if (dt > 2500) { //huge page (the shorter the longer to load; but no loading icon)
		_t0 = t;
		dt >>= 6;
		setTimeout(fn, dt < 10 ? dt : 10); //breathe
		//IE optimize the display if delay is too short
		return true;
	}
	return false;
}

export namespace mount_global {
	//define a desktop
	export function zkdt(dtid?: string, contextURI?: string, updateURI?: string, resourceURI?: string, reqURI?: string): zk.Desktop {
		var dt = zk.Desktop.$(dtid);
		if (dt == null) {
			dt = new zk.Desktop(dtid!, contextURI, updateURI, resourceURI, reqURI);
			if (zk.pfmeter) zAu._pfrecv(dt, dtid);
		} else {
			if (updateURI != null) dt.updateURI = updateURI;
			if (resourceURI != null) dt.resourceURI = resourceURI;
			if (contextURI != null) dt.contextURI = contextURI;
			if (reqURI != null) dt.requestPath = reqURI;
		}
		_mntctx.curdt = dt;
		return dt;
	}

	//widget creations
	// wi's index meaning
	// wi[0] = widget type
	// wi[1] = uuid
	// wi[2] = widget properties
	// wi[3] = shadow properties - since ZK 8.0.0
	// wi[4] = children
	// wi[5] = mold
	export function zkx(wi?: WidgetInfo, extra?: ExtraInfo, aucmds?: AuCmds, js?: string): void { //extra is either delay (BL) or [stub, filter] (AU)
		zk.mounting = true;

		try {
			if (js) jq.globalEval(js);

			var mount = mtAU, infs = _crInfAU0, delay, owner: string | zk.Widget | undefined;
			if (!extra || !extra.length) { //if 2nd argument not stub, it must be BL (see zkx_)
				delay = extra;
				if (wi) {
					extra = aucmds;
					aucmds = undefined;
				}
				mount = mtBL;
				infs = _crInfBL0;
			} //else assert(!aucmds); //no aucmds if AU

			if (wi) {
				if (wi[0] === 0) { //page
					var props = wi[2],
						dtid = zk.cut(props, 'dt') as string | undefined,
						cu = zk.cut(props, 'cu') as string | undefined,
						uu = zk.cut(props, 'uu') as string | undefined,
						rsu = zk.cut(props, 'rsu') as string | undefined,
						ru = zk.cut(props, 'ru') as string | undefined;
					window.zkdt(dtid, cu, uu, rsu, ru);
					if (owner = zk.cut(props, 'ow') as string)
						owner = zk.Widget.$(owner)!;
				}

				infs.push([_curdt(), wi, _mntctx.bindOnly, owner as zk.Widget | undefined, extra]);
				if (!zk._crWgtUuids) zk._crWgtUuids = [];
				zk._crWgtUuids.push(wi[1]);
				//extra is [stub-fn, filter] if AU,  aucmds if BL
				mountpkg(infs);
			}

			if (delay)
				setTimeout(mount, 0); //Bug 2983792 (delay until non-defer script evaluated)
			else if (!breathe(mount)) //give the browser a chance to breathe
				mount();

			doAuCmds(aucmds);
		} catch (e) {
			zk.mounting = false;
			zk.error('Failed to mount: ' + ((e as Error).message ?? e));
			setTimeout(function () {
				throw e;
			}, 0);
		}
	}

	//widget creation called by au.js
	//args: [wi] (a single element array containing wi)
	/** @internal */
	export function zkx_(args: ArrayLike<unknown>, stub: unknown, filter?: CallableFunction): void {
		_t0 = Date.now(); //so breathe() won't do unncessary delay
		(args as unknown[])[1] = [stub, filter]; //assign stub as 2nd argument (see zkx)
		window.zkx(...args as unknown as []); //args[2] (aucmds) must be null
	}

	//Run AU commands (used only with ZHTML)
	export function zkac(): void {
		doAuCmds(arguments as never);
	}

	//mount and zkx (BL)
	export function zkmx(wi?: WidgetInfo, extra?: ExtraInfo, aucmds?: AuCmds, js?: string): void {
		window.zkmb();
		try {
			zkx.call(window, wi, extra, aucmds, js);
		} finally {
			window.zkme();
		}
	}

	//begin of mounting
	export function zkmb(bindOnly?: boolean): void {
		_mntctx.bindOnly = bindOnly;
		var t = 390 - (Date.now() - _t0);
		zk.startProcessing(t > 0 ? t : 0);
	}

	//end of mounting
	export function zkme(): void {
		_mntctx.curdt = undefined;
		_mntctx.bindOnly = false;
	}

	// window scope
	// register data-attributres handler (since 8.0.0
	export function zkdh(name: string, script: string): void {
		zk.addDataHandler('data-' + name, script);
	}
}

//Event Handler//
jq(function () {
	var Widget = zk.Widget,
		_bfUploads: CallableFunction[] = [],
		_reszInf = {},
		_subevts = { //additonal invocation
			onClick: 'doSelect_',
			onRightClick: 'doSelect_',
			onMouseOver: 'doTooltipOver_',
			onMouseOut: 'doTooltipOut_'
		};

	zk.beforeUnload = function (fn, opts): void { //part of zk
		if (opts?.remove) _bfUploads.$remove(fn);
		else _bfUploads.push(fn);
	};

	/** @internal */
	function _doEvt(wevt: zk.Event): void {
		var wgt = wevt.target;
		if (wgt && !wgt.$weave) {
			var en = wevt.name,
				fn = _subevts[en] as string | undefined;
			if (fn) {
				// Bug 3300935, disable tooltip for IOS
				if (!zk.ios || (fn != 'doTooltipOver_' && fn != 'doTooltipOut_')) {
					(wgt[fn] as CallableFunction)(wevt);
				}
			}
			// eslint-disable-next-line @typescript-eslint/dot-notation
			if (!wevt.stopped && (!wevt['originalEvent'] || !(wevt['originalEvent'] as object)['zkstopped'])) // Bug ZK-2544
				(wgt['do' + en.substring(2) + '_'] as CallableFunction)(wevt);
			if (wevt.domStopped)
				wevt.domEvent!.stop();
		}
	}

	/** @internal */
	function _docMouseDown(evt: zk.Event, wgt: zk.Widget | undefined, noFocusChange?: boolean): void {
		zk.clickPointer[0] = evt.pageX!;
		zk.clickPointer[1] = evt.pageY!;

		if (!wgt) wgt = evt.target;

		var dEvent = evt.domEvent!,
			body = document.body,
			old = zk.currentFocus;
		if (dEvent.clientX! <= body.clientWidth && dEvent.clientY! <= body.clientHeight) //not click on scrollbar
			// F70-ZK-2007: Add the button information in it.
			Widget.mimicMouseDown_(wgt, noFocusChange, evt.which); //wgt is null if mask

		_doEvt(evt);

		//Bug 2799334, 2635555 and 2807475: need to enforce a focus event (IE only)
		//However, ZK-354: if target is upload, we can NOT focus to it. Thus, focusBackFix was introduced
		if (old && zk.ie) { // Bug ZK-2795, IE11 still fails in this case.
			var n = jq(old)[0];
			if (n)
				setTimeout(function () {
					try {
						var cf = zk.currentFocus;
						if (cf && cf != old && !n.offsetWidth && !n.offsetHeight) {
							zk.focusBackFix = true;
							cf.focus();
						}
					} catch (e) {
						zk.debugLog((e as Error).message ?? e);
					} finally {
						delete zk.focusBackFix;
					}
				});
		}
	}

	/** @internal */
	function _docResize(): void {
		// eslint-disable-next-line @typescript-eslint/dot-notation
		if (!_reszInf['time']) return; //already handled

		var now = Date.now();
		// eslint-disable-next-line @typescript-eslint/dot-notation
		if (zk.mounting || zk.loading || now < _reszInf['time'] || zk.animating()) {
			setTimeout(_docResize, 10);
			return;
		}

		// eslint-disable-next-line @typescript-eslint/dot-notation
		_reszInf['time'] = undefined; //handled
		// eslint-disable-next-line @typescript-eslint/dot-notation
		_reszInf['lastTime'] = now + 1000;
		//ignore following for a while if processing (in slow machine)

		zAu._onClientInfo();

		// eslint-disable-next-line @typescript-eslint/dot-notation
		_reszInf['inResize'] = true;
		try {
			const dt = zk.Desktop._dt!;
			zWatch.fire('_beforeSizeForRead', dt); //notify all
			zWatch.fire('beforeSize', dt); //notify all
			zWatch.fire('onFitSize', dt, {reverse: true}); //notify all
			zWatch.fire('onSize', dt); //notify all
			zWatch.fire('afterSize', dt); //notify all
			// eslint-disable-next-line @typescript-eslint/dot-notation
			_reszInf['lastTime'] = Date.now() + 8;
		} finally {
			// eslint-disable-next-line @typescript-eslint/dot-notation
			_reszInf['inResize'] = false;
		}
	}

	//Invoke the first root wiget's afterKeyDown_
	/** @internal */
	function _afterKeyDown(wevt: zk.Event): void {
		var dts = zk.Desktop.all, Page = zk.Page;
		for (var dtid in dts)
			for (var wgt = dts[dtid].firstChild; wgt; wgt = wgt.nextSibling)
				if (wgt instanceof Page) {
					for (var w = wgt.firstChild; w; w = w.nextSibling)
						if (_afterKD(w, wevt))
							return;
				} else if (_afterKD(wgt, wevt))
					return; //handled
	}

	/** @internal */
	function _afterKD(wgt: zk.Widget | zul.Widget, wevt: zk.Event): boolean {
		if (!zk.isLoaded('zul') || !(wgt instanceof zul.Widget))
			return false; //handled
		wevt.target = wgt; //mimic as keydown directly sent to wgt
		return wgt.afterKeyDown_(wevt, true);
	}

	var lastTimestamp, lastTarget;
	jq(document)
		.keydown(function (evt) {
			const wgt = Widget.$(evt, {child: true})!,
				wevt = new zk.Event(wgt, 'onKeyDown', evt.keyData(), undefined, evt);
			if (wgt) {
				_doEvt(wevt);
				if (!wevt.stopped && zk.isLoaded('zul') && wgt instanceof zul.Widget) {
					wgt.afterKeyDown_(wevt);
					if (wevt.domStopped)
						wevt.domEvent!.stop();
				}
			// eslint-disable-next-line @typescript-eslint/dot-notation
			} else if (zk['invokeFirstRootForAfterKeyDown'])
				_afterKeyDown(wevt);

			if (evt.keyCode == 27
				&& (zk._noESC > 0 || zAu.shallIgnoreESC())) //Bug 1927788: prevent FF from closing connection
				return false; //eat
		})
		.keyup(function (evt) {
			var wgt = zk.keyCapture;
			if (wgt) zk.keyCapture = undefined;
			else wgt = Widget.$(evt, {child: true});
			_doEvt(new zk.Event(wgt, 'onKeyUp', evt.keyData(), undefined, evt));
		})
		.keypress(function (evt) {
			var wgt = zk.keyCapture;
			if (!wgt) wgt = Widget.$(evt, {child: true});
			_doEvt(new zk.Event(wgt, 'onKeyPress', evt.keyData(), undefined, evt));
		})
		.on('paste', function (evt: JQuery.TriggeredEvent & {keyData()}) {
			var wgt = zk.keyCapture;
			if (!wgt) wgt = Widget.$(evt, {child: true});
			_doEvt(new zk.Event(wgt, 'onPaste', evt.keyData(), undefined, evt));
		})
		.on('zcontextmenu', function (evt: JQuery.MouseEventBase) {
			//ios: zcontextmenu shall be listened first,
			//due to need stop other event (ex: click, mouseup)

			zk.clickPointer[0] = evt.pageX || 0;
			zk.clickPointer[1] = evt.pageY || 0;

			var wgt = Widget.$(evt, {child: true});
			if (wgt) {
				var wevt = new zk.Event(wgt, 'onRightClick', evt.mouseData(), {}, evt);
				_doEvt(wevt);
				if (wevt.domStopped)
					return false;
			}
			return true;
		})
		.on('zmousedown', function (evt: JQuery.MouseEventBase) {
			if (zk.mobile) {
				zk.currentPointer[0] = evt.pageX || 0;
				zk.currentPointer[1] = evt.pageY || 0;
			}
			var wgt = Widget.$(evt, {child: true});
			_docMouseDown(
				new zk.Event(wgt, 'onMouseDown', evt.mouseData(), undefined, evt),
				wgt);
		})
		.on('zmouseup', function (evt: JQuery.MouseUpEvent) {
			var e = zk.Draggable.ignoreMouseUp(), wgt;
			if (e === true)
				return; //ignore

			if (e != null) {
				_docMouseDown(e as zk.Event, undefined, true); //simulate mousedown
			}

			wgt = zk.mouseCapture;
			if (wgt) zk.mouseCapture = undefined;
			else wgt = Widget.$(evt, {child: true});
			_doEvt(new zk.Event(wgt as zk.Widget, 'onMouseUp', evt.mouseData(), undefined, evt));
		})
		.on('zmousemove', function (evt: JQuery.MouseMoveEvent) {
			zk.currentPointer[0] = evt.pageX || 0;
			zk.currentPointer[1] = evt.pageY || 0;

			var wgt = zk.mouseCapture;
			if (!wgt) wgt = Widget.$(evt, {child: true});
			_doEvt(new zk.Event(wgt, 'onMouseMove', evt.mouseData(), undefined, evt));
		})
		.mouseover(function (evt) {
			if (zk.mobile) return; // unsupported on touch device for better performance
			zk.currentPointer[0] = evt.pageX || 0;
			zk.currentPointer[1] = evt.pageY || 0;

			_doEvt(new zk.Event(Widget.$(evt, {child: true}), 'onMouseOver', evt.mouseData(), {ignorable: true}, evt));
		})
		.mouseout(function (evt) {
			_doEvt(new zk.Event(Widget.$(evt, {child: true}), 'onMouseOut', evt.mouseData(), {ignorable: true}, evt));
		})
		.click(function (evt) {
			if (zk.Draggable.ignoreClick()) return;

			if (zk.android
				&& (lastTimestamp && lastTimestamp + 400 > evt.timeStamp)
				&& (lastTarget && lastTarget == evt.target)) { //fix android 4.1.1 fire twice or more
				return;
			} else {
				lastTimestamp = evt.timeStamp;
				lastTarget = evt.target;

				zjq._fixClick(evt);

				if (evt.which == 1)
					_doEvt(new zk.Event(Widget.$(evt, {child: true}),
						'onClick', evt.mouseData(), {}, evt));
				//don't return anything. Otherwise, it replaces event.returnValue in IE (Bug 1541132)
			}
		})
		.on('zdblclick', function (evt: JQuery.DoubleClickEvent) {
			if (zk.Draggable.ignoreClick()) return;

			var wgt = Widget.$(evt, {child: true});
			if (wgt && evt.which == 1) {
				var wevt = new zk.Event(wgt, 'onDoubleClick', evt.mouseData(), {}, evt);
				_doEvt(wevt);
				if (wevt.domStopped)
					return false;
			}
		})
		.on((document.hidden !== undefined ? '' : zk.vendor_) + 'visibilitychange', function (evt) {
			zAu._onVisibilityChange();
		});

	if (zk.scriptErrorHandlerEnabled) {
		zk.scriptErrorHandler = function (evt: JQuery.TriggeredEvent & {originalEvent: ErrorEvent}): void {
			const errorMsg = evt.originalEvent.message,
				stack = (evt.originalEvent.error as Error).stack!,
				checkFunctionList = ['globalEval', 'script', '_doCmds', 'afterResponse', '_onResponseReady'];

			for (let i = 0, l = checkFunctionList.length, lastCheckPos = -1; i < l; i++) {
				const checkPos = stack.indexOf(checkFunctionList[i]);
				if (checkPos == -1 || checkPos < lastCheckPos)
					return; //not from Clients.evalJavascript
				lastCheckPos = checkPos;
			}
			zAu.send(new zk.Event(zk.Desktop._dt, 'onScriptError', {
				message: errorMsg,
				stack: stack
			}));
			zk.scriptErrorHandlerRegistered = false;
		};
	}

	var _sizeHandler = function (evt): void {
		if (zk.mounting)
			return;

		//Tom Yeh: 20051230:
		//1. In certain case, IE will keep sending onresize (because
		//grid/listbox may adjust size, which causes IE to send onresize again)
		//To avoid this endless loop, we ignore onresize a while if this method
		//was called
		//
		//2. IE keeps sending onresize when dragging the browser's border,
		//so we have to filter (most of) them out

		var now = Date.now();
		// eslint-disable-next-line @typescript-eslint/dot-notation
		if ((_reszInf['lastTime'] && now < _reszInf['lastTime']) || _reszInf['inResize'])
			return; //ignore resize for a while (since onSize might trigger onsize)

		var delay = zk.android ? 250 : 50;
		// eslint-disable-next-line @typescript-eslint/dot-notation
		_reszInf['time'] = now + delay - 1; //handle it later
		setTimeout(_docResize, delay);

		if (zk.mobile && zAu._cInfoReg) {
			if (!jq('#zk_proc').length && !jq('#zk_showBusy').length) {
				zUtl.progressbox('zk_proc', window.msgzk ? msgzk.PLEASE_WAIT : 'Processing...', true);
			}
		}
	};

	if (zk.mobile) {
		jq(window).on('orientationchange', _sizeHandler);

		// Bug ZK-2697
		if (zk.ios) {
			jq(window).on('pagehide', function () {
				zk.unloading = true; //to disable error message

				if (!zk.rmDesktoping) {
					rmDesktop();
				}
			});
		}
	} else {
		jq(window).resize(_sizeHandler);
	}

	jq(window).scroll(function () {
		zWatch.fire('onScroll', zk.Desktop._dt!); //notify all
	}).on('unload', function () {
		zk.unloading = true; //to disable error message

		// B65-ZK-2051: Remove desktop if is IE.
		if (zk.ie || !zk.rmDesktoping) {
			rmDesktop();
		}
	});

	function rmDesktop(): void {
		//20061109: Tom Yeh: Failed to disable Opera's cache, so it's better not
		//to remove the desktop.
		//Good news: Opera preserves the most udpated content, when BACK to
		//a cached page, its content. OTOH, IE/FF/Safari cannot.
		var bRmDesktop = !zk.opera && !zk.keepDesktop;
		if (bRmDesktop || zk.pfmeter) {
			zk.rmDesktoping = true;
			try {
				var dts = zk.Desktop.all;
				for (var dtid in dts)
					zAu._rmDesktop(dts[dtid], !bRmDesktop);
			} catch (e) {
				zk.debugLog((e as Error).message ?? e);
			}
		}
	}

	var _oldBfUnload = window.onbeforeunload;
	window.onbeforeunload = function (ev: BeforeUnloadEvent) {
		if (!zk.skipBfUnload) {
			if (zk.confirmClose) {
				setTimeout(function () { //If user click 'cancel', the disabledRequest should be false. Ref: https://stackoverflow.com/questions/4650692/
					zAu.disabledRequest = false;
				}, 0);
				return zk.confirmClose;
			}

			for (var j = 0; j < _bfUploads.length; ++j) {
				var s = _bfUploads[j]() as unknown;
				if (s) return s;
			}
		}

		if (_oldBfUnload) {
			var s = _oldBfUnload.call(window, ev) as unknown;
			if (s) return s;
		}

		zk.unloading = true; //FF3 aborts ajax before calling window.onunload

		// B65-ZK-2051: Remove desktop if not IE.
		if (!zk.ie) {
			rmDesktop();
		}
		//Return nothing
	};

	zk.afterMount(function () {
		jq('script.z-runonce').remove();
	});
	//clean up the runonce script. otherwise, it might be run again if
	//the script element is moved
}); //jq()

zk.copy(window, mount_global);