/* eslint-disable @typescript-eslint/dot-notation */
/* widget.ts

	Purpose:
		Widget - the UI object at the client
	Description:
		z_rod indicates a widget is in the status of ROD (i.e., no rendered due to ROD)

	History:
		Tue Sep 30 09:23:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
declare global {
	interface Document {
		mozFullScreen?;
		webkitIsFullScreen?;
	}
}
export interface RealVisibleOptions {
	dom?: boolean;
	until?: zk.Object;
	strict?: boolean;
	cache?: Record<string, unknown>;
}

export interface DomVisibleOptions {
	display?: boolean;
	visibility?: boolean;
}

export interface DomStyleOptions {
	style?: boolean;
	width?: boolean;
	height?: boolean;
	left?: boolean;
	top?: boolean;
	zIndex?: boolean;
	visible?: boolean;
}

export interface DomClassOptions {
	sclass?: boolean;
	zclass?: boolean;
	input?: boolean; // zul.inp.InputWidget.prototype.domClass_
}

export interface DomAttrsOptions extends DomStyleOptions, DomClassOptions {
	id?: boolean;
	domStyle?: boolean;
	domClass?: boolean;
	tooltiptext?: boolean;
	tabindex?: boolean;
	text?: boolean; // zul.inp.InputWidget.prototype.domAttrs_
	content?: boolean; // zul.wgt.Image
}

export interface MinFlexInfo {
	wgt: zk.Widget;
	wgtn: HTMLElement;
	orient: zk.FlexOrient;
}

var _binds: Record<string, Widget> = {}, //{uuid, wgt}: bind but no node
	_globals: Record<string, Widget[]> = {}, //global ID space {id, [wgt...]}
	_floatings: ({widget: zk.Widget; node: HTMLElement})[] = [], //[{widget:w,node:n}]
	_nextUuid = 0,
	_domevtfnm: Record<string, string> = {}, //{evtnm, funnm}
	_domevtnm: Record<string, string> = {onDoubleClick: 'dblclick'}, //{zk-evt-nm, dom-evt-nm}
	_wgtcls: Record<string, typeof Widget> = {}, //{clsnm, cls}
	_hidden: HTMLElement[] = [], //_autohide
	_noChildCallback, _noParentCallback, //used by removeChild/appendChild/insertBefore
	_syncdt: number | undefined, //timer ID to sync destkops
	_rdque: zk.Widget[] = [], _rdtid: number | undefined, //async rerender's queue and timeout ID
	_ignCanActivate, //whether canActivate always returns true
	REGEX_DQUOT = /"/g; //jsdoc can't handle it correctly, so we have to put here


// cache the naming lookup
type SetterFunc = (wgt, nm, val, extra) => Widget;
var _setCaches: Record<string, {name: string; func: SetterFunc}> = {};
function _setterName(name: string): {name: string; func: SetterFunc} {
	var setter: Partial<{name: string ; func: SetterFunc}> = {name: 'set' + name.charAt(0).toUpperCase() + name.substring(1)},
		cc;
	if ((cc = name.charAt(0)) == '$') {
		setter.func = function (wgt: zk.Widget, nm: string, val, extra): zk.Widget {
			var result = wgt._setServerListener(nm, val as boolean);
			if (!result)
				zk._set2(wgt, undefined, nm, val, extra);
			return wgt;
		};
	} else if (cc == 'o' && name.charAt(1) == 'n'
		&& ((cc = name.charAt(2)) <= 'Z' && cc >= 'A')) {
		setter.func = function (wgt: zk.Widget, nm: string, val, extra): zk.Widget {
			wgt.setListener(nm, val);
			return wgt;
		};
	} else {
		setter.func = function (wgt: zk.Widget, nm: string, val, extra): zk.Widget {
			var fun: CallableFunction | undefined;
			if (fun = wgt[setter.name!] as SetterFunc | undefined) {
				//to optimize the performance we check the method first (most common)
				zk._set2(wgt, fun, undefined, val, extra);
				return wgt;
			}
			zk._set2(wgt, undefined, nm, val, extra);
			return wgt;
		};
	}
	_setCaches[name] = setter as never;
	return setter as never;
}

//Check if el is a prolog
function _isProlog(el: Node | null): boolean { // eslint-disable-line zk/noNull
	return !!el && el.nodeType == 3 //textnode
		&& !el.nodeValue?.trim().length;
}

//Event Handling//
type JQueryEventHandler = (evt: JQuery.TriggeredEvent, ...args: unknown[]) => unknown;
function _domEvtInf(wgt: Widget, evtnm: string, fn?: string | CallableFunction, keyword?: unknown): [string, JQueryEventHandler] { //proxy event listener
	if (typeof fn != 'function') {
		if (!fn && !(fn = _domevtfnm[evtnm]))
			_domevtfnm[evtnm] = fn = '_do' + evtnm.substring(2);

		var f = wgt[fn] as CallableFunction | undefined;
		if (typeof f != 'function')
			throw 'Listener ' + fn + ' not found in ' + wgt.className;
		fn = f;
	}

	var domn = _domevtnm[evtnm];
	if (!domn)
		domn = _domevtnm[evtnm] = evtnm.substring(2).toLowerCase();
	return [domn, _domEvtProxy(wgt, fn, evtnm, keyword)];
}
function _domEvtProxy(wgt: zk.Widget, f: CallableFunction & {__keyword?}, evtnm: string, keyword?: unknown): JQueryEventHandler {
	var fps = wgt._$evproxs, fp: undefined | JQueryEventHandler;
	if (!fps) wgt._$evproxs = fps = new WeakMap();
	if (keyword)
		f.__keyword = keyword;
	else if (fp = fps.get(f)) return fp;
	var fn = _domEvtProxy0(wgt, f, keyword);
	fps.set(f, fn);
	return fn;
}
function _domEvtProxy0(wgt: Widget, f: CallableFunction, keyword?: unknown): JQueryEventHandler {
	return function (evt, ...rest) {
		var devt = evt, //make a copy since we will change evt (and arguments) in the following line
			zkevt = jq.Event.zk(devt, wgt);
		arguments[0] = zkevt; // change arguments[0]

		switch (devt.type) {
			case 'focus':
				if (zk._focusByClearBusy) return; // ZK-4294: don't trigger focus event while in restoring focus
				if (wgt.canActivate()) {
					zk.currentFocus = wgt;
					//add triggerByFocus option for notification
					zWatch.fire('onFloatUp', wgt, {triggerByFocus: true}); //notify all
					break;
				}
				return; //ignore it
			case 'blur':
				//due to mimicMouseDown_ called, zk.currentFocus already corrected,
				//so we clear it only if caused by other case
				if (!zk._cfByMD) zk.currentFocus = undefined;
				break;
			case 'click':
			case 'dblclick':
			case 'mouseup': //we cannot simulate mousedown:(
				if (zk.Draggable.ignoreClick())
					return;
		}

		// ZK 7.0 support the extra arguments for callback function
		var args: unknown[];
		if (keyword) {
			args = [].slice.call(arguments);
			args.push(keyword);
		} else
			args = arguments as never;
		// @ts-expect-error: trouble assigning `wgt` as `this` param for `f`
		var ret = f.call(wgt, ...args);
		if (ret === undefined) ret = (zkevt as {returnValue?}).returnValue;
		if (zkevt.domStopped) devt.stop();
		if (zkevt.stopped && devt.originalEvent) (devt.originalEvent as {zkstopped?: boolean}).zkstopped = true;
		return devt.type == 'dblclick' && ret === undefined ? false : ret;
	};
}

function _unlink(wgt: zk.Widget, child: zk.Widget): void {
	var p = child.previousSibling, n = child.nextSibling;
	if (p) p.nextSibling = n;
	else wgt.firstChild = n;
	if (n) n.previousSibling = p;
	else wgt.lastChild = p;
	child.nextSibling = child.previousSibling = child.parent = undefined;

	--wgt.nChildren;
}
//replace the link of from with the link of to (note: it assumes no child)
function _replaceLink(from: zk.Widget, to: zk.Widget): void {
	var p = to.parent = from.parent,
		q = to.previousSibling = from.previousSibling;
	if (q) q.nextSibling = to;
	else if (p) p.firstChild = to;

	q = to.nextSibling = from.nextSibling;
	if (q) q.previousSibling = to;
	else if (p) p.lastChild = to;
}

function _fixBindLevel(wgt: zk.Widget, v: number): void {
	wgt.bindLevel = v++;
	for (let w = wgt.firstChild; w; w = w.nextSibling)
		_fixBindLevel(w, v);
}

function _addIdSpace(wgt: zk.Widget): void {
	if (wgt._fellows) wgt._fellows[wgt.id!] = wgt;
	var p = wgt.parent;
	if (p) {
		p = p.$o();
		if (p) p._fellows![wgt.id!] = wgt;
	}
}
function _rmIdSpace(wgt: zk.Widget): void {
	if (wgt._fellows) delete wgt._fellows[wgt.id!];
	var p = wgt.parent;
	if (p) {
		p = p.$o();
		if (p) delete p._fellows![wgt.id!];
	}
}
function _addIdSpaceDown(wgt: zk.Widget): void {
	let ow = wgt.parent;
	ow = ow ? ow.$o() : undefined;
	if (ow)
		_addIdSpaceDown0(wgt, ow);
}
function _addIdSpaceDown0(wgt: zk.Widget, owner: zk.Widget): void {
	if (wgt.id) owner._fellows![wgt.id] = wgt;
	if (!wgt._fellows)
		for (let w = wgt.firstChild; w; w = w.nextSibling)
			_addIdSpaceDown0(w, owner);
}
function _rmIdSpaceDown(wgt: zk.Widget): void {
	let ow = wgt.parent;
	ow = ow ? ow.$o() : undefined;
	if (ow)
		_rmIdSpaceDown0(wgt, ow);
}
function _rmIdSpaceDown0(wgt: zk.Widget, owner: zk.Widget): void {
	if (wgt.id)
		delete owner._fellows![wgt.id];
	if (!wgt._fellows)
		for (let w = wgt.firstChild; w; w = w.nextSibling)
			_rmIdSpaceDown0(w, owner);
}
//check if a desktop exists
function _exists(wgt: zk.Widget): boolean {
	if (document.getElementById(wgt.uuid)) //don't use $n() since it caches
		return true;

	for (let w = wgt.firstChild; w; w = w.nextSibling)
		if (_exists(w))
			return true;
	return false;
}

function _fireClick(wgt: zk.Widget, evt: zk.Event): boolean {
	if (!wgt.shallIgnoreClick_(evt)
		&& !wgt.fireX(evt).stopped && evt.shallStop) {
		evt.stop();
		return false;
	}
	return !evt.stopped;
}

function _rmDom(wgt: zk.Widget, n: HTMLElement | HTMLElement[]): void {
	//TO IMPROVE: actions_ always called if removeChild is called, while
	//insertBefore/appendChild don't (it is called only if attached by au)
	//NOT CONSISTENT! Better to improve in the future
	var act: [CallableFunction, unknown][] | undefined;
	if (wgt.isVisible() && (act = wgt.actions_.hide as never)) {
		wgt._rmAftAnm = function () {
			jq(n).remove();
		};
		if (Array.isArray(n)) {
			for (const e of n) {
				e.style.visibility = '';
			}
		} else {
			n.style.visibility = ''; //Window (and maybe other) might turn it off
		}
		(act[0] as CallableFunction).bind(wgt)(n, act[1]);
	} else
		jq(n).remove();
}

//whether it is controlled by another dragControl
//@param invoke whether to invoke dragControl
function _dragCtl(wgt: zk.Widget, invoke?: boolean): boolean {
	var p: zk.Widget & {dragControl?(wgt)} | undefined;
	return !!(wgt && (p = wgt.parent) && p.dragControl && (!invoke || p.dragControl(wgt)));
}

//backup current focus
type CurrentFocusInfo = {focus: zk.Widget; range?: zk.Offset} | undefined;
function _bkFocus(wgt: zk.Widget): CurrentFocusInfo {
	var cf = zk.currentFocus;
	if (cf && zUtl.isAncestor(wgt, cf)) {
		zk.currentFocus = undefined;
		return {focus: cf, range: _bkRange(cf)};
	}
	return undefined;
}
function _bkRange(wgt: zk.Widget): zk.Offset | undefined {
	const input = wgt.getInputNode && wgt.getInputNode();
	return input ? zk(input).getSelectionRange() : undefined;
}
//restore focus
function _rsFocus(cfi?: CurrentFocusInfo): void {
	var cf: undefined | zk.Widget | HTMLInputElement;
	if (cfi && (cf = cfi.focus) && cf.desktop && !zk.currentFocus) {
		_ignCanActivate = true;
		//s.t., Window's rerender could gain focus back and receive onblur correctly
		try {
			cf.focus();
			// B65-ZK-1803: Check if InputNode is visible or not
			if (cfi.range && cf.getInputNode && (cf = cf.getInputNode()) && zk(cf).isRealVisible())
				zk(cf).setSelectionRange(cfi.range[0], cfi.range[1]);
		} finally {
			_ignCanActivate = false;
		}
	}
}

function _listenFlex(wgt: zk.Widget & {_flexListened?: boolean}): void {
	if (!wgt._flexListened) {
		var parent = wgt.parent,
			cssFlexEnabled = wgt._cssflex && parent && ((parent instanceof zk.Page) || parent.getFlexContainer_() != null);
		if (cssFlexEnabled)
			zWatch.listen({onSize: [wgt, zFlex.applyCSSFlex]});
		else {
			zWatch.listen({onSize: [wgt, zFlex.onSize]});
			zWatch.listen({
				_beforeSizeForRead: [wgt, zFlex.beforeSizeForRead],
				beforeSize: [wgt, zFlex.beforeSize]
			});
		}
		if (wgt.getHflex() == 'min' || wgt.getVflex() == 'min') {
			wgt.listenOnFitSize_();
			if (cssFlexEnabled)
				zWatch.listen({beforeSize: [wgt, zFlex.beforeSizeClearCachedSize]});
		} else {
			wgt.unlistenOnFitSize_();
			if (cssFlexEnabled)
				zWatch.unlisten({beforeSize: [wgt, zFlex.beforeSizeClearCachedSize]});
		}
		wgt._flexListened = true;
	}
}
function _unlistenFlex(wgt: zk.Widget): void {
	if (wgt._flexListened) {
		var cssFlexApplied = wgt._cssFlexApplied;
		if (cssFlexApplied) {
			//remove css flex flag
			zWatch.unlisten({onSize: [wgt, zFlex.applyCSSFlex]});
			delete wgt._cssFlexApplied;
		} else {
			zWatch.unlisten({onSize: [wgt, zFlex.onSize]});
			zWatch.unlisten({
				_beforeSizeForRead: [wgt, zFlex.beforeSizeForRead],
				beforeSize: [wgt, zFlex.beforeSize]
			});
		}
		wgt.unlistenOnFitSize_();
		if (cssFlexApplied)
			zWatch.unlisten({beforeSize: [wgt, zFlex.beforeSizeClearCachedSize]});
		delete wgt._flexListened;
	}
}

/**
 * @class zk.DnD
 * Drag-and-drop utility.
 * It is the low-level utility reserved for overriding for advanced customization.
 */
// zk scope
export var DnD = class { //for easy overriding
	/**
	 * @returns the drop target from the event, or the element from the event's
	 * ClientX and ClientY. This function is to fix IE9~11, Edge, and Firefox which
	 * will receive a wrong target from the mouseup event.
	 *
	 * @since 8.0.2
	 * @param evt - the DOM event
	 * @param drag - the draggable controller
	 */
	static getDropTarget(evt: zk.Event, drag?: zk.Draggable): zk.Widget {
		var wgt: zk.Widget | undefined;
		// Firefox's bug -  https://bugzilla.mozilla.org/show_bug.cgi?id=1259357
		if ((zk.ff && jq(evt.domTarget).css('overflow') !== 'visible')) {
			var n = document.elementFromPoint(evt.domEvent!.clientX || 0, evt.domEvent!.clientY || 0);
			if (n)
				wgt = zk.$(n);
		} else {
			wgt = evt.target;
		}
		return wgt!;
	}
	/**
	 * @returns the widget to drop to.
	 * @param drag - the draggable controller
	 * @param pt - the mouse pointer's position.
	 * @param evt - the DOM event
	 */
	static getDrop(drag: zk.Draggable, pt: zk.Offset, evt: zk.Event): zk.Widget | undefined {
		var wgt = this.getDropTarget(evt, drag);
		return wgt ? wgt.getDrop_(drag.control as zk.Widget) : undefined;
	}
	/** Ghost the DOM element being dragged.
	 * @param drag - the draggable controller
	 * @param ofs - the offset of the returned element (left/top)
	 * @param msg - the message to show inside the returned element (a safe HTML string)
	 */
	static ghost(drag: zk.Draggable, ofs: zk.Offset, msg?: string): HTMLElement {
		if (msg != null) {
			if (msg)
				/*safe*/ msg = '<span class="z-drop-text">' + DOMPurify.sanitize(msg) + '</span>';
			jq(document.body).append(/*safe*/
				'<div id="zk_ddghost" class="z-drop-ghost z-drop-disallow" style="position:absolute;top:'
				+ jq.px0(ofs[1]) + ';left:' + jq.px(ofs[0]) + ';"><div class="z-drop-content"><span id="zk_ddghost-img" class="z-drop-icon"></span>' + /*safe*/ msg + '</div></div>');
			drag._dragImg = jq('#zk_ddghost-img')[0];
			return jq('#zk_ddghost')[0];
		}

		var dgelm = jq(drag.node).clone()[0];
		dgelm.id = 'zk_ddghost';
		zk.copy(dgelm.style, {
			position: 'absolute', left: ofs[0] + 'px', top: ofs[1] + 'px'
		});
		jq(dgelm).addClass('z-drag-ghost');
		document.body.appendChild(dgelm);
		return dgelm;
	}
};
function DD_cleanLastDrop(drag: zk.Draggable): void {
	if (drag) {
		var drop: zk.Widget | undefined;
		if (drop = drag._lastDrop) {
			drag._lastDrop = undefined;
			drop.dropEffect_();
		}
		drag._lastDropTo = undefined;
	}
}
function DD_pointer(evt: zk.Event, height: number): zk.Offset {
	return [evt.pageX + 7, evt.pageY + 5];
}
function DD_enddrag(drag: zk.Draggable & {control: zk.Widget}, evt: zk.Event): void {
	DD_cleanLastDrop(drag);
	var pt: zk.Offset = [evt.pageX, evt.pageY],
		wgt = zk.DnD.getDrop(drag, pt, evt);
	if (wgt) wgt.onDrop_(drag, evt);
}
function DD_dragging(drag: zk.Draggable & {control: zk.Widget}, pt: zk.Offset, evt: zk.Event): void {
	var dropTo: zk.Widget | undefined;
	if (!evt || (dropTo = zk.DnD.getDropTarget(evt)) == drag._lastDropTo) {
		return;
	}

	var dropw = zk.DnD.getDrop(drag, pt, evt),
		found = dropw && dropw == drag._lastDrop;
	if (!found) {
		DD_cleanLastDrop(drag); //clean _lastDrop
		if (dropw) {
			drag._lastDrop = dropw;
			dropw.dropEffect_(true);
			found = true;
		}
	}

	var dragImg = drag._dragImg;
	if (dragImg) {
		if (found) {
			jq(drag.node).removeClass('z-drop-disallow').addClass('z-drop-allow');
			// ZK-2008: should use jQuery
			jq(dragImg).removeClass('z-icon-ban').addClass('z-icon-plus-circle');
		} else {
			jq(drag.node).removeClass('z-drop-allow').addClass('z-drop-disallow');
			// ZK-2008: should use jQuery
			jq(dragImg).removeClass('z-icon-plus-circle').addClass('z-icon-ban');
		}
	}

	drag._lastDropTo = dropTo; //do it after _cleanLastDrop

	if (zk.mobile)
		zk(drag.node).redoCSS();
}
function DD_ghosting(drag: zk.Draggable & {control: zk.Widget}, ofs: zk.Offset, evt: zk.Event): HTMLElement {
	return drag.control.cloneDrag_(drag, DD_pointer(evt, jq(drag.node).height() ?? 0));
}
function DD_endghosting(drag: zk.Draggable & {control: zk.Widget}, origin: HTMLElement): void {
	drag.control.uncloneDrag_(drag);
	drag._dragImg = undefined;
}
function DD_constraint(drag: zk.Draggable, pt: zk.Offset, evt: zk.Event): zk.Offset {
	return DD_pointer(evt, jq(drag.node).height() || 0);
}
function DD_ignoredrag(drag: zk.Draggable & {control: zk.Widget}, pt: zk.Offset, evt: zk.Event): boolean {
	//ZK 824:Textbox dragging issue with Listitem
	//since 5.0.11,6.0.0 introduce evt,drag to the wgt.ignoreDrag_() to provide more information.
	return drag.control.ignoreDrag_(pt, evt, drag);
}

function _topnode(n: HTMLElement): HTMLElement | undefined {
	// eslint-disable-next-line zk/noNull
	let curr: HTMLElement | null | undefined = n;
	for (var body = document.body; curr && curr != body; curr = curr.parentElement) { //no need to check vparentNode
		var position = jq(curr).css('position');
		if (position == 'absolute' || position == 'relative')
			return curr;
	}
	return undefined;
}
function _zIndex(n: HTMLElement | undefined): number {
	return n ? zk.parseInt(n.style.zIndex) : 0;
}

function _getFirstNodeDown(wgt: zk.Widget): HTMLElement | undefined {
	let n = wgt.$n();
	if (n) return n;
	for (let w = wgt.firstChild; w; w = w.nextSibling) {
		n = w.getFirstNode_();
		if (n) return n;
	}
	return undefined;
}
//Returns if the specified widget's visibility depends the self widget.
function _floatVisibleDependent(self: zk.Widget, wgt: zk.Widget): boolean {
	for (let w: zk.Widget | undefined = wgt; w; w = w.parent) {
		if (w == self) return true;
		else if (!w.isVisible()) break;
	}
	return false;
}

function _fullScreenZIndex(zi: number): number {
	var pseudoFullscreen;
	if (document.fullscreenElement) {
		pseudoFullscreen = ':fullscreen';
	} else if (document.mozFullScreen) {
		//pseudoFullscreen = ":-moz-full-screen";
		//Firefox return zindex by scientific notation "2.14748e+9"
		//use zk.parseFloat() will get 2147480000, so return magic number directly.
		return 2147483648;
	} else if (document.webkitIsFullScreen) {
		pseudoFullscreen = ':-webkit-full-screen';
	}
	if (pseudoFullscreen) {
		var fsZI = jq.css(jq(pseudoFullscreen)[0] as Node, 'zIndex');
		return fsZI == 'auto' ? 2147483648 : parseInt(fsZI) + 1;
	}
	return zi;
}

//Returns the topmost z-index for this widget
function _topZIndex(wgt: zk.Widget): number {
	var zi = 1800; // we have to start from 1800 depended on all the css files.

	//ZK-1226: Full Screen API will make element's ZIndex bigger than 1800
	//	so set a higher zindex if browser is in full screen mode.
	zi = _fullScreenZIndex(zi);
	//

	for (var j = _floatings.length; j--;) {
		var w = _floatings[j].widget,
			wzi = zk.parseInt(w.getFloatZIndex_(_floatings[j].node));
		if (wzi >= zi && !zUtl.isAncestor(wgt, w) && w.isVisible())
			zi = wzi + 1;
	}
	return zi;
}

function _prepareRemove(wgt: zk.Widget, ary: HTMLElement[]): void {
	for (let w = wgt.firstChild; w; w = w.nextSibling) {
		var n = w.$n();
		if (n) ary.push(n);
		else _prepareRemove(w, ary);
	}
}

//render the render defer (usually controlled by server)
function _doDeferRender(wgt: zk.Widget): void {
	if (wgt._z$rd) { //might be redrawn by forcerender
		delete wgt._z$rd;
		wgt._norenderdefer = true;
		wgt.replaceHTML('#' + wgt.uuid, wgt.parent?.desktop, undefined, true);
		if (wgt.parent)
			wgt.parent.onChildRenderDefer_(wgt);
	}
}

//invoke rerender later
function _rerender(wgt: zk.Widget, timeout: number): void {
	if (_rdtid)
		clearTimeout(_rdtid);
	_rdque.push(wgt);
	if (timeout === 0) {
		queueMicrotask(_rerender0);
	} else {
		_rdtid = window.setTimeout(_rerender0, timeout);
	}
}
function _rerender0(): void {
	_rdtid = undefined;
	l_out:
	for (var wgt: zk.Widget | undefined; wgt = _rdque.shift();) {
		if (!wgt.desktop) {
			continue;
		}

		for (var j = _rdque.length; j--;) {
			if (zUtl.isAncestor(wgt, _rdque[j])) {
				_rdque.splice(j, 1); //skip _rdque[j]
			} else if (zUtl.isAncestor(_rdque[j], wgt)) {
				continue l_out; //skip wgt
			}
		}

		wgt.rerender(-1);
	}
}
/* Bug ZK-2281 */
function _rerenderNow(wgt: zk.Widget, skipper?: Skipper | undefined): void {
	var rdque: zk.Widget[] = [];
	for (var j = _rdque.length; j--;)
		if (zUtl.isAncestor(wgt, _rdque[j])) {// if wgt's children or itself is in a rerender queue
			if (!skipper || !skipper.skipped(wgt, _rdque[j]))
				rdque = rdque.concat(_rdque.splice(j, 1));
		}

	// just in case
	if (!_rdque.length && _rdtid) {
		clearTimeout(_rdtid);
		_rdtid = undefined;
	}

	l_out2:
	for (var w: zk.Widget | undefined; w = rdque.shift();) {
		if (!w.desktop)
			continue;

		for (var j = rdque.length; j--;) {
			if (zUtl.isAncestor(w, rdque[j]))
				rdque.splice(j, 1); //skip rdque[j]
			else if (zUtl.isAncestor(rdque[j], w))
				continue l_out2; //skip wgt
		}

		w.rerender(-1);
	}
}
function _rerenderDone(wgt: zk.Widget, skipper?: Skipper | undefined/* Bug ZK-1463 */): void {
	for (var j = _rdque.length; j--;)
		if (zUtl.isAncestor(wgt, _rdque[j])) {
			if (!skipper || !skipper.skipped(wgt, _rdque[j]))
				_rdque.splice(j, 1);
		}
}

function _markCache(cache: Record<string, boolean>, visited: zk.Widget[], visible: boolean): boolean {
	if (cache)
		for (var p: zk.Widget | undefined; p = visited.pop();)
			cache[p.uuid] = visible;
	return visible;
}

const _dragoptions: zk.DraggableOptions = {
	starteffect: zk.$void, //see bug #1886342
	endeffect: DD_enddrag, change: DD_dragging,
	ghosting: DD_ghosting, endghosting: DD_endghosting,
	constraint: DD_constraint, //s.t. cursor won't be overlapped with ghosting
	ignoredrag: DD_ignoredrag,
	zIndex: '88800'
};

export function WrapClass(pkg: string) {
	return function _WrapClass<T extends typeof zk.Object>(constr: T): T {
		abstract class $subclass$ extends constr {
			// eslint-disable-next-line @typescript-eslint/no-explicit-any
			constructor(...args: any[]) {
				super(...args as []);
				if ((String((args as [])[args.length - 1])) != 'Symbol(HasDescendant)' && (this as {className?}).className == pkg) {
					// call old ZK widget $init(), which uses zk.override() to
					// override an ES6 class
					this.$init(...args as []);
					this.afterCreated_(...args as []);
				}
			}
		}

		let pkges = pkg.split('.'),
			context = window;
		// ignore zk module
		if (pkges[0] != 'zk' || pkges.length > 2) {
			for (let i = 0, j = pkges.length - 1; i < j && context; i++) {
				context = context[pkges[i]] as never;
			}
			const run = function (context: object): void {
				(constr.prototype as zk.Widget).className = pkg;
				for (let i = 0, j = pkges.length - 1; i < j; i++) {
					context = context[pkges[i]] as never;
				}
				context[pkges[pkges.length - 1]] = zk.regClass($subclass$);
			};
			if (!context) {
				zk.afterLoad(pkges.slice(0, pkges.length - 1).join('.'), function () {
					run(window);
				});
			} else {
				run(window);
			}
		}
		return $subclass$;
	};
}

/** A widget, i.e., an UI object.
 * Each component running at the server is associated with a widget
 * running at the client.
 * <p>Refer to <a href="http://books.zkoss.org/wiki/ZK_Component_Development_Essentials">ZK Component Development Essentials</a>
 * and <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference">ZK Client-side Reference</a>
 * for more information.
 * <p>Notice that, unlike the component at the server, {@link zk.Desktop}
 * and {@link zk.Page} are derived from zk.Widget. It means desktops, pages and widgets are in a widget tree.
 */
// zk scope
export class Widget<TElement extends HTMLElement = HTMLElement> extends zk.Object {
	declare z$rod?: boolean; // Used by rod in zkmax.
	/** @internal */
	declare _rodopen?: boolean; // Used by rod in zkmax.
	declare z_virnd?: boolean;

	// zul/sel/SelectWidget
	declare $button?: boolean;
	declare $inputWidget?: boolean;

	/** @internal */
	declare _uplder?: zul.Upload;
	/** @internal */
	declare _autodisable_self?: boolean;
	/** @internal */
	declare _uploading?: boolean;

	declare offsetWidth?;
	declare offsetHeight?;
	declare blankPreserved?: boolean;

	getInputNode?(): HTMLInputElement | undefined;

	declare z_rod?: boolean | number;
	/** @internal */
	declare _z$rd?: boolean;
	declare z$display?: string; // see au.ts
	declare prolog?: string;
	/** @internal */
	declare _rodKid?: boolean;
	/** @internal */
	declare _node?: TElement;
	/** @internal */
	declare _nodeSolved;
	/** @internal */
	declare _rmAftAnm?: CallableFunction;
	/** @internal */
	declare _$evproxs?: WeakMap<CallableFunction, JQueryEventHandler>;
	/** @internal */
	declare _fellows?: Record<string, zk.Widget>;
	/** @internal */
	declare _norenderdefer;
	/** @internal */
	declare _rerendering;
	declare domExtraAttrs?: Record<string, string>;
	/** @internal */
	declare _flexListened;
	/** @internal */
	declare _cssFlexApplied?: {flexApplied?; minFlexInfoList?: MinFlexInfo[]};
	/** @internal */
	declare _beforeSizeHasScroll?: boolean;
	declare doAfterProcessRerenderArgs: undefined | unknown[];
	/** @internal */
	declare _vflex?: string | boolean;
	/** @internal */
	declare _hflex?: string | boolean;
	/** @internal */
	declare _flexFixed;
	/** @internal */
	declare _nvflex?: number;
	/** @internal */
	declare _nhflex?: number;
	/** @internal */
	declare _hflexsz?: number;
	/** @internal */
	declare _vflexsz?: number;

	/** @internal */
	declare _binding;
	declare rawId;
	/** @internal */
	declare _$service?: zk.Service;
	/** @internal */
	declare childReplacing_;
	/** @internal */
	declare _userZIndex;
	/** @internal */
	declare _zIndex: number | string;
	declare z_isDataHandlerBound;
	/** @internal */
	declare _drag?: zk.Draggable;
	/** @internal */
	declare _preWidth;
	/** @internal */
	declare _preHeight;
	/** @internal */
	declare _action?: string;
	/** @internal */
	declare _tabindex?: number;
	/** @internal */
	declare _draggable?: string | boolean;
	/** @internal */
	declare _asaps: Record<string, unknown>;
	/** @internal */
	declare _lsns: Record<string, ({priority?: number} | [{priority?: number}, CallableFunction])[]>;
	/** @internal */
	declare _bklsns: Record<string, unknown>;
	/** @internal */
	declare _subnodes: Record<string, HTMLElement | string | undefined>;
	/** @internal */
	declare _subzcls: Record<string, string>;
	/** @internal */
	declare _sclass?: string;
	/** @internal */
	declare _zclass?: string;
	/** @internal */
	declare _width?: string;
	/** @internal */
	declare _height?: string;
	/** @internal */
	declare _left?: string;
	/** @internal */
	declare _top?: string;
	/** @internal */
	declare _tooltiptext?: string;
	/** @internal */
	declare _droppable?: string | boolean;
	/** @internal */
	declare _dropTypes?: string[];
	/** @internal */
	declare _fitSizeListened: boolean;
	/** @internal */
	declare _focus: boolean;

	/** @internal */
	declare static _importantEvts?: Record<string, boolean>;
	/** @internal */
	declare static _duplicateIgnoreEvts?: Record<string, boolean>;
	/** @internal */
	declare static _repeatIgnoreEvts?: Record<string, boolean>;
	declare static molds: {
		'default'(out: string[]): void;
	};

	/** @internal */
	_visible = true;
	/** @internal */
	_mold = 'default';
	/** @internal */
	_style?: string;
	/** @internal */
	_renderdefer?: number;

	/** @internal */
	_cssflex = true;

	/** @internal */
	actions_: Record<'hide' | string, unknown> = {};

	/**
	 * The number of children (integer).
	 * @readonly
	 */
	nChildren = 0;
	/**
	 * The bind level (integer).
	 * @readonly
	 * The level in the widget tree after this widget is bound to a DOM tree ({@link bind_}).
	 * For example, a widget's bind level is one plus the parent widget's
	 * <p>It starts at 0 if it is the root of the widget tree (a desktop, zk.Desktop), then 1 if a child of the root widget, and son on. Notice that it is -1 if not bound.
	 * <p>It is mainly useful if you want to maintain a list that parent widgets is in front of (or after) child widgets.
	 * bind level.
	 */
	bindLevel = -1;
	/** The class name of the widget.
	 * For example, zk.Widget's class name is "zk.Widget", while
	 * zul.wnd.Window's "zul.wnd.Window".
	 * <p>Notice that it is available if a widget class is loaded by WPD loader (i.e., specified in zk.wpd). If you create a widget class dynamically,
	 * you have to invoke {@link register} to make this member available.
	 * On the other hand, {@link zk.Object.$class} is available for all objects
	 * extending from {@link zk.Object}.
	 *
	 * @see widgetName
	 */
	declare className: string;
	static {
		this.prototype.className = 'zk.Widget';
	}
	/** The widget name of the widget.
	 * It is the same as `this.className.substring(this.className.lastIndexOf('.') + 1).toLowerCase()`.
	 * For example, if {@link className} is zul.wnd.Window, then
	 * {@link widgetName} is window.
	 * <p>Notice that {@link className} is unique while {@link widgetName}
	 * is not necessary unique.
	 * @see className
	 * @since 5.0.2
	 */
	declare widgetName: string;
	static {
		this.prototype.widgetName = 'widget';
	}
	/** The AU tag of this widget.
	 * The AU tag tag is used to tag the AU requests sent by the peer widget.
	 * For instance, if the AU tag is `xxx,yyy` and the desktop's
	 * request path ({@link Desktop#requestPath}) is `/foo.zul`, then
	 * the URL of the AU request will contain `/_/foo.zul/xxx,yyy`,.
	 * @defaultValue `null`
	 * @since 6.0.0
	 */
	autag?: string;

	/** @internal */
	_floating = false;

	/**
	 * The first child, or `null` if no child at all.
	 * @readonly
	 * @see getChildAt
	 */
	firstChild?: zk.Widget;
	/**
	 * The last child, or `null` if no child at all.
	 * @readonly
	 * @see getChildAt
	 */
	lastChild?: zk.Widget;
	/**
	 * The parent, or `null` if this widget has no parent.
	 * @readonly
	 */
	parent?: zk.Widget;
	/**
	 * The next sibling, or `null` if this widget is the last child.
	 * @readonly
	 */
	nextSibling?: zk.Widget;
	/**
	 * The previous sibling, or `null` if this widget is the first child.
	 * @readonly
	 */
	previousSibling?: zk.Widget;
	/**
	 * The desktop that this widget belongs to.
	 * @readonly
	 * It is set when it is bound to the DOM tree.
	 * <p>Notice it is always non-null if bound to the DOM tree, while
	 * {@link Widget.$n} is always non-null if bound. For example, {@link zul.utl.Timer}.
	 * <p>It is readonly, and set automatically when {@link bind_} is called.
	 */
	desktop?: Desktop;
	/**
	 * The identifier of this widget, or `null` if not assigned.
	 * @readonly
	 * It is the same as {@link getId}.
	 * <p>To change the value, use {@link setId}.
	 */
	id?: string;
	/**
	 * Whether this widget has a peer component.
	 * @readonly
	 * It is set if a widget is created automatically to represent a component
	 * at the server. On the other hand, it is false if a widget is created
	 * by the client application (by calling, say, `new zul.inp.Textox()`).
	 */
	inServer = false;
	/**
	 * The UUID. Don't change it if it is bound to the DOM tree, or {@link inServer} is true.
	 * Developers rarely need to modify it since it is generated automatically.
	 */
	uuid = '';
	/**
	 * Indicates an invocation of {@link appendChild} is made by {@link insertBefore}.
	 */
	/** @internal */
	insertingBefore_ = false;

	/**
	 * A map of objects that are associated with this widget, and
	 * they shall be removed when this widget is unbound ({@link unbind}).
	 * <p>The key must be an unique name of the object, while the value
	 * must be an object that implement the destroy method.
	 * <p>When {@link unbind_} is called, `destroy()` is
	 * called for each object stored in this map. Furthermore,
	 * if the visibility of this widget is changed, and the object implements
	 * the sync method, then `sync()` will be called.
	 * Notice that the sync method is optional. It is ignored if not implemented.
	 * <p>It is useful if you implement an effect, such as shadow, mask
	 * and error message, that is tightly associated with a widget.
	 */
	/** @internal */
	effects_?: Record<string, zk.eff.Effect>;

	/** The weave controller that is used by ZK Weaver.
	 * It is not null if it is created and controlled by ZK Weaver.
	 * In other words, it is called in the Design Mode if {@link Widget.$weave} is not null.
	 */
	$weave?: object = undefined;

	/** The constructor.
	 * For example,
```js
new zul.wnd.Window({
  border: 'normal',
  title: 'Hello World',
  closable: true
});
```
	 * @param props - the properties to be assigned to this widget.
	 */
	constructor(props?: Record<string, unknown> | typeof zkac) {
		super();
		this._asaps = {}; //event listened at server
		this._lsns = {}; //listeners(evtnm,listener)
		this._bklsns = {}; //backup for listeners by setListeners
		this._subnodes = {}; //store sub nodes for widget(domId, domNode)
		this.effects_ = {};
		this._subzcls = {}; // cache the zclass + subclass name, like zclass + '-hover'
	}

	/** @internal */
	override afterCreated_(props?: Record<string, unknown> | typeof zkac): void {
		//zkac is a token used by create() in mount.js for optimizing performance
		if (props !== zkac) {
			//if props.$oid, it must be an object other than {} so ignore
			if (props && typeof props == 'object' && !props.$oid)
				for (var nm in props)
					this.set(nm, props[nm] as never);

			if ((zk.spaceless || this.rawId) && this.id)
				this.uuid = this.id; //setId was called
			if (!this.uuid)
				this.uuid = Widget.nextUuid();
		}
	}

	/**
	 * Sets this widget's mold. A mold is a template to render a widget.
	 * In other words, a mold represents a visual presentation of a widget.
	 * Depending on implementation, a widget can have multiple molds.
	 * @defaultValue `default`
	 * @param mold - the mold
	 */
	setMold(mold: string): this {
		if (this._mold != mold) {
			this._mold = mold;
			this.rerender();
		}
		return this;
	}

	/**
	 * @returns this widget's mold. A mold is a template to render a widget.
	 * In other words, a mold represents a visual presentation of a widget.
	 * Depending on implementation, a widget can have multiple molds.
	 */
	getMold(): string {
		return this._mold;
	}

	/**
	 * Sets the CSS style of this widget.
	 * @defaultValue `null`
	 * @param style - the CSS style
	 * @see getStyle
	 * @see setSclass
	 * @see setZclass
	 */
	setStyle(style: string): this {
		if (this._style != style) {
			this._style = style;
			this.updateDomStyle_();
		}
		return this;
	}

	/**
	 * @returns the CSS style of this widget
	 * @see setStyle
	 * @see getSclass
	 * @see getZclass
	 */
	getStyle(): string | undefined {
		return this._style;
	}

	/**
	 * Sets the CSS class of this widget.
	 * @defaultValue `null`
	 * <p>The default styles of ZK components doesn't depend on sclass at all. Rather, setSclass is provided to perform small adjustment, e.g., changing only the font size. In other words, the default style is still applied if you change sclass.
	 * <p>To replace the default style completely, use {@link setZclass} instead.
	 * <p>The real CSS class is a concatenation of {@link getZclass} and {@link getSclass}.
	 * @param sclass - the style class
	 * @see getSclass
	 * @see setZclass
	 * @see setStyle
	 */
	setSclass(sclass: string): this {
		if (this._sclass != sclass) {
			this._sclass = sclass;
			this.updateDomClass_();
		}
		return this;
	}

	/**
	 * @returns the CSS class of this widget.
	 * @see setSclass
	 * @see getZclass
	 * @see getStyle
	 */
	getSclass(): string | undefined {
		return this._sclass;
	}

	/**
	 * Sets the ZK Cascading Style class(es) for this widget. It is the CSS class used to implement a mold of this widget. n implementation It usually depends on the implementation of the mold ({@link getMold}).
	 * @defaultValue `null`
	 * but an implementation usually provides a default class, such as z-button.
	 * <p>Calling setZclass with a different value will completely replace the default style of a widget.
	 * Once you change it, all default styles are gone.
	 * If you want to perform small adjustments, use {@link setSclass} instead.
	 * <p>The real CSS class is a concatenation of {@link getZclass} and
	 * {@link getSclass}.
	 * @param zclass - the style class used to apply the whole widget.
	 * @see getZclass
	 * @see setSclass
	 * @see setStyle
	 */
	setZclass(zclass: string): this {
		if (this._zclass != zclass) {
			this._zclass = zclass;
			this._subzcls = {}; // reset
			this.rerender();
		}
		return this;
	}

	/**
	 * @returns the ZK Cascading Style class(es) for this widget.
	 * @see setZclass
	 * @see getSclass
	 * @see getStyle
	 */
	getZclass(): string {
		var zcls = this._zclass;
		return zcls != null ? zcls : 'z-' + this.widgetName;
	}

	/**
	 * Sets the width of this widget.
	 * @param width - the width. Remember to specify 'px', 'pt' or '%'.
	 * An empty or null value means "auto"
	 */
	setWidth(width?: string): this {
		if (this._width != width) {
			this._width = width;
			if (!this._nhflex) {
				var n = this.$n();
				if (n) n.style.width = width || '';
			}
		}
		return this;
	}
	/**
	 * @returns the width of this widget.
	 * @see getHeight
	 */
	getWidth(): string | undefined {
		return this._width;
	}
	/**
	 * Sets the height of this widget.
	 * @param height - the height. Remember to specify 'px', 'pt' or '%'.
	 * An empty or null value means "auto"
	 */
	setHeight(height?: string): this {
		if (this._height != height) {
			this._height = height;
			if (!this._nvflex) {
				var n = this.$n();
				if (n) n.style.height = height || '';
			}
		}
		return this;
	}
	/**
	 * @returns the height of this widget.
	 * @see getWidth
	 */
	getHeight(): string | undefined {
		return this._height;
	}

	/**
	 * Sets the left of this widget.
	 * @param left - the left. Remember to specify 'px', 'pt' or '%'.
	 * An empty or null value means "auto"
	 */
	setLeft(left: string): this {
		if (this._left != left) {
			this._left = left;
			var n = this.$n();
			if (n) n.style.left = left || '';
		}
		return this;
	}

	/**
	 * @returns the left of this widget.
	 * @see getTop
	 */
	getLeft(): string | undefined {
		return this._left;
	}

	/**
	 * Sets the top of this widget.
	 * If you want to specify `bottom`, use {@link setStyle} instead.
	 * For example, `setStyle("bottom: 0px");`
	 * @param top - the top. Remember to specify 'px', 'pt' or '%'.
	 * An empty or null value means "auto"
	 */
	setTop(top: string): this {
		if (this._top != top) {
			this._top = top;
			var n = this.$n();
			if (n) n.style.top = top || '';
		}
		return this;
	}

	/**
	 * @returns the top of this widget.
	 * @see getLeft
	 */
	getTop(): string | undefined {
		return this._top;
	}
	/**
	 * Sets the tooltip text of this widget.
	 * @defaultValue implementation of setTooltiptext: update the title attribute of {@link Widget.$n}
	 * @param title - the tooltip text
	 */
	setTooltiptext(tooltiptext: string): this {
		if (this._tooltiptext != tooltiptext) {
			this._tooltiptext = tooltiptext;
			var n = this.$n();
			// ZK-676 , ZK-752
			if (n) n.title = this._tooltiptext || '';
		}
		return this;
	}
	/**
	 * @returns the tooltip text of this widget.
	 */
	getTooltiptext(): string | undefined {
		return this._tooltiptext;
	}

	/**
	 * Sets the identifier, or a list of identifiers of a droppable type for this widget.
	 * @defaultValue `null`
	 * <p>The simplest way to make a component droppable is to set this attribute to `true`. To disable it, set this to `false` (or `null`).
	 * <p>If there are several types of draggable objects and this widget accepts only some of them, you could assign a list of identifiers that this widget accepts, separated by comma.
	 * <p>For example, if this component accepts dg1 and dg2, then assign "dg1, dg2" to this attribute.
	 * @param droppable - `false`, `null` or `""` to denote not-droppable; `true` for accepting any draggable types; a list of identifiers, separated by comma for identifiers of draggables this widget accept (to be dropped in).
	 */
	setDroppable(droppable?: string | boolean /* boolean is for zkmax override */): this {
		droppable = droppable && 'false' != droppable ? droppable : undefined;
		if (this._droppable != droppable) {
			this._droppable = droppable;

			let dropTypes: string[] | undefined;
			if (droppable && droppable != 'true') {
				dropTypes = (droppable as string).split(',');
				for (let j = dropTypes.length; j--;)
					if (!(dropTypes[j] = dropTypes[j].trim()))
						dropTypes.splice(j, 1);
			}
			this._dropTypes = dropTypes;
		}
		return this;
	}
	/**
	 * @returns the identifier, or a list of identifiers of a droppable type for this widget, or null if not droppable.
	 */
	getDroppable(): string | boolean | undefined {
		return this._droppable;
	}

	/**
	 * Sets vertical flexibility hint of this widget.
	 * <p>The parameter flex is a number in String type indicating how this
	 * widget's parent container distributes remaining empty space among
	 * its children widget vertically. Flexible
	 * widget grow and shrink to fit their given space. Flexible widget with
	 * larger flex values will be made larger than widget with lower flex
	 * values, at the ratio determined by all flexible widgets. The actual
	 * flex value is not relevant unless there are other flexible widget within
	 * the same parent container. Once the default sizes of widget in a
	 * parent container are calculated, the remaining space in the parent
	 * container is divided among the flexible widgets, according to their
	 * flex ratios.</p>
	 * <p>Specify a flex value of negative value, 0, or "false" has the
	 * same effect as leaving the flex attribute out entirely.
	 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
	 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
	 * given to this flexible widget to enclose all of its children widgets.
	 * That is, the flexible widget grow and shrink to fit its children widgets.</p>
	 *
	 * @see setHflex
	 * @see getVflex
	 * @param vflex - the vertical flex hint.
	 */
	setVflex(vflex?: boolean | string): this {
		if (this._vflex != vflex) {
			this._vflex = vflex;
			this.setVflex_(vflex);

			let p: zk.Widget | undefined;
			if (this.desktop
				&& (p = this.parent) && !p.isBinding()) //ZK-307
				zUtl.fireSized(p, -1); //no beforeSize
		}
		return this;
	}

	/**
	 * @see setVflex
	 * @returns vertical flex hint of this widget.
	 */
	getVflex(): string | boolean | undefined {
		return this._vflex;
	}
	isVflex(): string | boolean | undefined {
		return this.getVflex();
	}

	/**
	 * Sets horizontal flexibility hint of this widget.
	 * <p>The parameter flex is a number in String type indicating how this
	 * widget's parent container distributes remaining empty space among
	 * its children widget horizontally. Flexible
	 * widget grow and shrink to fit their given space. Flexible widget with
	 * larger flex values will be made larger than widget with lower flex
	 * values, at the ratio determined by all flexible widgets. The actual
	 * flex value is not relevant unless there are other flexible widget
	 * within the same parent container. Once the default sizes of widget
	 * in a parent container are calculated, the remaining space in the parent
	 * container is divided among the flexible widgets, according to their
	 * flex ratios.</p>
	 * <p>Specify a flex value of negative value, 0, or "false" has the
	 * same effect as leaving this flex attribute out entirely.
	 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
	 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
	 * given to this flexible widget to enclose all of its children widgets.
	 * That is, the flexible widget grow and shrink to fit its children widgets.</p>
	 *
	 * @param hflex - the horizontal flex hint.
	 * @see setVflex
	 * @see getHflex
	 */
	setHflex(hflex?: boolean | string): this {
		if (this._hflex != hflex) {
			this._hflex = hflex;
			this.setHflex_(hflex);

			var p: zk.Widget | undefined;
			if (this.desktop/*if already bind*/
				&& (p = this.parent) && !p.isBinding()/*ZK-307*/)
				zUtl.fireSized(p, -1); //no beforeSize
		}
		return this;
	}

	/**
	 * @returns horizontal flex hint of this widget.
	 * @see setHflex
	 */
	getHflex(): string | boolean | undefined {
		return this._hflex;
	}
	isHflex(): string | boolean | undefined {
		return this.getHflex();
	}
	/**
	 * @returns the number of milliseconds before rendering this component
	 * at the client.
	 * @defaultValue `-1` (don't wait).
	 * @since 5.0.2
	 */
	getRenderdefer(): number {
		return this._renderdefer === undefined ? -1 : this._renderdefer;
	}

	/**
	 * Sets the number of milliseconds before rendering this component
	 * at the client.
	 * @defaultValue -1 (don't wait).
	 *
	 * <p>This method is useful if you have a sophisticated page that takes
	 * long to render at a slow client. You can specify a non-negative value
	 * as the render-defer delay such that the other part of the UI can appear
	 * earlier. The styling of the render-deferred widget is controlled by
	 * a CSS class called `z-render-defer`.
	 *
	 * <p>Notice that it has no effect if the component has been rendered
	 * at the client.
	 * @param renderdefer - time to wait in milliseconds before rendering.
	 * Notice: 0 also implies deferring the rendering (just right after
	 * all others are rendered).
	 * @since 5.0.2
	 */
	setRenderdefer(renderdefer: number): this {
		if (this._renderdefer != renderdefer) {
			this._renderdefer = renderdefer;
		}
		return this;
	}

	/**
	 * @returns the client-side action.
	 * @since 5.0.6
	 */
	getAction(): string | undefined {
		return this._action;
	}

	/**
	 * Sets the client-side action.
	 * @defaultValue `null` (no CSA at all)
	 * <p>The format: <br>
	 * `action1: action-effect1; action2: action-effect2`<br/>
	 *
	 * <p>Currently, only two actions are `show` and `hide`.
	 * They are called when the widget is becoming visible (show) and invisible (hide).
	 * <p>The action effect (`action-effect1`) is the name of a method
	 * defined in <a href="http://www.zkoss.org/javadoc/latest/jsdoc/zk/eff/Actions.html">zk.eff.Actions</a>,
	 * such as
	 * `show: slideDown; hide: slideUp`
	 * @param action - the client-side action
	 * @since 5.0.6
	 */
	setAction(action?: string): this {
		if (this._action != action) {
			this._action = action;
			if (action) {
				for (var ps = action.split(';'), j = ps.length; j--;) {
					var p = ps[j], k = p.indexOf(':');
					if (k >= 0) {
						var nm = p.substring(0, k).trim(),
							val = p.substring(k + 1).trim(),
							opts, fn: CallableFunction | undefined, l: number;
						if (nm && val) {
							k = val.indexOf('(');
							if (k >= 0) {
								if ((l = val.lastIndexOf(')')) > k)
									opts = jq.evalJSON(val.substring(k + 1, l));
								val = val.substring(0, k);
							}
							if (fn = zk.eff.Actions[val] as never)
								this.actions_[nm] = [fn, opts];
							else
								zk.error('Unknown action: ' + val);
							continue;
						}
					}
					zk.error('Illegal action: ' + action + ', ' + this.className);
				}
			}
		}
		return this;
	}

	/**
	 * @returns the tab order of this component.
	 * @since 8.0.2
	 */
	getTabindex(): number | undefined {
		return this._tabindex;
	}
	/**
	 * Sets the tab order of this component.
	 * @since 8.0.2
	 */
	setTabindex(tabindex: number): this {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;

			var n = this.$n();
			if (n) {
				if (tabindex == null)
					n.removeAttribute('tabindex');
				else
					n.tabIndex = tabindex;
			}
		}
		return this;
	}

	/**
	 * @returns whether using css flex in this component or not.
	 * @since 9.0.0
	 */
	getCssflex(): boolean {
		return this._cssflex;
	}
	isCssflex(): boolean {
		return this.getCssflex();
	}

	/**
	 * Sets whether to use css flex in this component or not.
	 * @param cssflex - enable css flex or not
	 * @since 9.0.0
	 */
	setCssflex(cssflex: boolean): this {
		if (this._cssflex != cssflex) {
			this._cssflex = cssflex;
			if (this.desktop) {
				this.rerender();
			}
		}
		return this;
	}

	/** @internal */
	setHflex_(hflex?: string | boolean): void {
		this._nhflex = (true === hflex || 'true' == hflex) ? 1 : hflex == 'min' ? -65500 : zk.parseInt(hflex);
		if (this._nhflex < 0 && hflex != 'min')
			this._nhflex = 0;
		if (_binds[this.uuid] === this) { //ZK-1784 only update the components style when it is attached to desktop
			//checking on (_binds[this.uuid] === this) as before does not work when
			//nested inside native component. in this case the nested component
			//is bound earlier, when the native component is reused (mount.js create())
			if (this._cssflex && this._nhflex <= 0) // min or no flex
				zFlex.clearCSSFlex(this, 'h');

			if (!this._nhflex) {
				if (!this._cssflex)
					this.setFlexSize_({width: 'auto'}); //clear the width
				delete this._hflexsz;
				this._hflex = undefined;
				if (!this._nvflex)
					_unlistenFlex(this);
			} else {
				if (this._cssflex) delete this._cssFlexApplied;
				_listenFlex(this);
			}
		}
	}

	/** @internal */
	setVflex_(vflex?: string | boolean): void {
		this._nvflex = (true === vflex || 'true' == vflex) ? 1 : vflex == 'min' ? -65500 : zk.parseInt(vflex);
		if (this._nvflex < 0 && vflex != 'min')
			this._nvflex = 0;
		if (this.desktop) {
			if (this._cssflex && this._nvflex <= 0) // min or no flex
				zFlex.clearCSSFlex(this, 'w');

			if (!this._nvflex) {
				if (!this._cssflex)
					this.setFlexSize_({height: 'auto'});
				delete this._vflexsz;
				this._vflex = undefined;
				if (!this._nhflex)
					_unlistenFlex(this);
			} else {
				if (this._cssflex) delete this._cssFlexApplied;
				_listenFlex(this);
			}
		}
	}

	/**
	 * Invoked after an animation (e.g., {@link zjq.slideDown}) has finished.
	 * You could override to clean up anything related to animation.
	 * Notice that, if you override, you have to call back this method.
	 * @param visible - whether the result of the animation will make the DOM element visible
	 * @since 5.0.6
	 * @internal
	 */
	afterAnima_(visible: boolean): void {
		var fn: CallableFunction | undefined;
		if (fn = this._rmAftAnm) {
			this._rmAftAnm = undefined;
			fn();
		}
	}

	/**
	 * Sets the identifier of a draggable type for this widget.
	 * @defaultValue `null`
	 * <p>The simplest way to make a widget draggable is to set this property to "true". To disable it, set this to "false" (or null).
	 * If there are several types of draggable objects, you could assign an identifier for each type of draggable object.
	 * The identifier could be anything but empty and "false".
	 * @param draggable - "false", "" or null to denote non-draggable; "true" for draggable with anonymous identifier; others for an identifier of draggable.
	 */
	setDraggable(draggable?: string | boolean /* boolean is for zkmax override */): this {
		if (!draggable && draggable != null) draggable = 'false'; //null means default
		this._draggable = draggable;

		if (this.desktop && !_dragCtl(this, true)) {
			if (draggable && draggable != 'false') this.initDrag_();
			else this.cleanDrag_();
		}
		return this;
	}
	/**
	 * @returns the identifier of a draggable type for this widget, or null if not draggable.
	 */
	getDraggable(): string | boolean {
		var v = this._draggable;
		return v ? v : _dragCtl(this) ? 'true' : 'false';
	}

	/**
	 * @returns the owner of the ID space that this widget belongs to, or null if it doesn't belong to any ID space.
	 * <p>Notice that, if this widget is an ID space owner, this method returns itself.
	 */
	$o<T extends Widget = Widget>(): T | undefined {
		for (let w: zk.Widget | undefined = this; w; w = w.parent)
			if (w._fellows) return w as T;
		return undefined;
	}

	/**
	 * @returns the map of all fellows of this widget.
	 * ```js
	 * wgt.$f().main.setTitle("foo");
	 * ```
	 * @since 5.0.2
	 */
	$f(): Record<string, Widget> | undefined
	/**
	 * @returns the fellow ({@link Widget}) of the specified ID of the ID space that this widget belongs to. It returns `undefined` if not found.
	 * @param id - the widget's ID ({@link id})
	 * @param global - whether to search all ID spaces of this desktop.
	 * If true, it first search its own ID space, and then the other Id spaces in this browser window (might have one or multiple desktops).
	 * If omitted, it won't search all ID spaces.
	 */
	$f<T extends Widget = Widget>(id: string, global?: boolean): T | undefined
	$f(id?: string, global?: boolean): Record<string, Widget> | Widget | undefined {
		var f = this.$o();
		if (!arguments.length)
			return f ? f._fellows : {};
		// If `argumens.length` is not zero, `id` is guaranteed to be a string.
		for (var ids = id!.split('/'), j = 0, len = ids.length; j < len; ++j) {
			id = ids[j];
			if (id) {
				if (f) f = f._fellows![id];
				if (!f && global && _globals[id]) f = _globals[id][0];
				if (!f || zk.spaceless) break;
				global = false;
			}
		}
		return f;
	}
	/**
	 * @returns the identifier of this widget, or null if not assigned.
	 * It is the same as {@link id}.
	 */
	getId(): string | undefined {
		return this.id;
	}

	/**
	 * Sets the identifier of this widget.
	 * @param id - the identifier to assigned to.
	 */
	setId(id?: string): this {
		if (id != this.id) {
			if (this.id) {
				_rmIdSpace(this);
				this.get$Class<typeof Widget>()._rmGlobal(this); //no need to check this.desktop
			}

			if (id && (zk.spaceless || this.rawId))
				zk._wgtutl.setUuid(this, id);
			this.id = id;

			if (id) {
				_addIdSpace(this);
				if (this.desktop || this.z_rod)
					this.get$Class<typeof Widget>()._addGlobal(this);
			}
		}
		return this;
	}

	/**
	 * Sets a property.
	 * The property updates sent from the server, including
	 * renderProperties and smartUpdate, will invoke this method.
	 * <h2>Special Names</h2>
	 * <h3>`onXxx`</h3>
	 * <p>If the name starts with `on`, it is assumed to be
	 * an event listener and {@link setListener} will be called.
	 *
	 * <h3>`$onXxx`</h3>
	 * <p>If the name starts with `$on`, the value is assumed to
	 * be a boolean indicating if the server registers a listener.
	 *
	 * <h3>`$$onXxx`</h3>
	 * <p>If the name starts with `$$on`, it indicates
	 * the event is an important event that the client must send it
	 * back to the server. In additions, the value is assumed to
	 * be a boolean indicating if the server registers a listener.
	 *
	 * <h2>Special Value</h2>
	 * <h3>`{$u: uuid}`</h3>
	 * <p>If the value is in this format, it indicates `$u`'s
	 * value is UUID of a widget, and it will be resolved to a widget
	 * before calling the real method.
	 * <p>However, since we cannot resolve a widget by its UUID until
	 * the widget is bound (to DOM). Thus, ZK sets property after mounted.
	 * For example, `wgt.set("radiogroup", {$u: uuid})` is equivalent
	 * to the following.
	 * ```js
	 * zk.afterMount(function () {
	 *   wgt.set("radiogroup", zk.Widget.$(uuid))
	 * });
	 * ```
	 *
	 * @param name - the name of property.
	 * @param value - the value
	 *
	 * {@label TWO_PARAMS}
	 */
	set(name: string, value: string): zk.Widget
	/**
	 * Sets a property.
	 * The property updates sent from the server, including
	 * renderProperties and smartUpdate, will invoke this method.
	 * @param name - the name of property.
	 * Refer to {@link (set:TWO_PARAMS)} for special names.
	 * @param value - the value
	 * @param extra - the extra argument. It could be anything.
	 */
	set(name: string, value: string, extra: unknown): zk.Widget
	set(name: string, value: string & {$u?: string}, extra?: unknown): zk.Widget {
		var cc: string | undefined, ref: string & {$u?} | zk.Widget | undefined = value;
		if ((cc = ref && ref.$u as string | undefined) //ref.$u is UUID
			&& !(ref = Widget.$(cc))) { //not created yet
			var self = this;
			zk.afterMount(function () {
				var v = Widget.$(cc);
				// ZK-1069: may not be ready even in afterMount
				if (v)
					zk._set(self, name, v, extra);
				else
					setTimeout(function () {
						zk._set(self, name, Widget.$(cc), extra);
					});
			}, -1);
			return this;
		}
		var setter = _setCaches[name] || _setterName(name);
		return setter.func.call(this, this, name, ref, extra);
	}

	/** @internal */
	_setServerListener(name: string, value: boolean): zk.Widget | undefined {
		if (name.startsWith('$$')) {
			const cls = this.$class as typeof Widget;
			if (name.startsWith('$$on')) {
				let ime = cls._importantEvts;
				(ime || (cls._importantEvts = {}))[name.substring(2)] = value;
				return this;
			} else if (name.startsWith('$$0on')) {
				let ime = cls._duplicateIgnoreEvts;
				(ime || (cls._duplicateIgnoreEvts = {}))[name.substring(3)] = value;
				return this;
			} else if (name.startsWith('$$1on')) {
				let ime = cls._repeatIgnoreEvts;
				(ime || (cls._repeatIgnoreEvts = {}))[name.substring(3)] = value;
				return this;
			}
		} else if (name.startsWith('$on')) {
			this._asaps[name.substring(1)] = value;
			return this;
		}
	}

	/**
	 * @returns a value from the specified property.
	 * @param name - the name of property.
	 * @since 5.0.2
	 */
	get(name: string): unknown {
		return zk.get(this, name);
	}

	/**
	 * @returns the child widget at the specified index or null if no such index.
	 * <p>Notice this method is not good if there are a lot of children
	 * since it iterates all children one by one.
	 * @param j - the index of the child widget to return. 0 means the first
	 * child, 1 for the second and so on.
	 * @param skipHidden - whether to skip hidden child widgets, defaults to false.
	 * @see {@link getChildIndex}
	 */
	getChildAt<T extends Widget>(j: number, skipHidden?: boolean): T | undefined {
		if (j >= 0 && j < this.nChildren) {
			for (var w = this.firstChild; w; w = w.nextSibling) {
				if (!skipHidden || w.isVisible())
					j--;
				if (j < 0)
					return w as T;
			}
		}
	}

	/**
	 * @returns the child index of this widget.
	 * By child index we mean the order of the child list of the parent. For example, if this widget is the parent's first child, then 0 is returned.
	 * <p>Notice that {@link getChildAt} is called against the parent, while
	 * this method called against the child. In other words,
	 * `w.parent.getChildAt(w.getChildIndex())` returns `w`.
	 * <p>Notice this method is not good if there are a lot of children
	 * since it iterates all children one by one.
	 */
	getChildIndex(): number {
		var w = this.parent, j = 0;
		if (w)
			for (w = w.firstChild; w; w = w.nextSibling, ++j)
				if (w == this)
					return j;
		return 0;
	}

	/** Appends an array of children.
	 * Notice this method does NOT remove any existent child widget.
	 * @param children - an array of children ({@link zk.Widget}) to add
	 */
	setChildren(children: zk.Widget[]): this {
		if (children)
			for (var j = 0, l = children.length; j < l;)
				this.appendChild(children[j++]);
		return this;
	}

	/**
	 * Append a child widget.
	 * The child widget will be attached to the DOM tree automatically,
	 * if this widget has been attached to the DOM tree,
	 * unless this widget is {@link zk.Desktop}.
	 * In other words, you have to attach child widgets of {@link zk.Desktop}
	 * manually (by use of, say, {@link replaceHTML}).
	 *
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If this widget is bound to the DOM tree, this method invoke {@link insertChildHTML_}
	 * to insert the DOM content of the child to the DOM tree.
	 * Thus, override {@link insertChildHTML_} if you want to insert more than
	 * the DOM content generated by {@link redraw}.</li>
	 * <li>If a widget wants to do something when the parent is changed, overrides {@link beforeParentChanged_}
	 * (which is called by {@link insertBefore}, {@link removeChild} and {@link appendChild}).</li>
	 * <li>{@link insertBefore} might invoke this method (if the widget shall be the last child).
	 * To know if it is the case you can check {@link insertingBefore_}.</li>
	 * </ul>
	 * @param child - the child widget to add
	 * @param ignoreDom - whether not to generate DOM elements
	 * could prevent it from generating DOM element.
	 * It is usually used with {@link rerender}.
	 * @returns whether the widget was added successfully. It returns false if the child is always the last child ({@link lastChild}).
	 * @see {@link insertBefore}
	 */
	appendChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		if (child == this.lastChild)
			return false;

		if (!this.beforeChildAdded_(child)) {
			return false;
		}
		this.triggerBeforeHostChildAdded_(child, undefined);

		var oldpt: zk.Widget | undefined;
		if ((oldpt = child.parent) != this) {
			child.beforeParentChanged_(this);
			child.triggerBeforeHostParentChanged_(this);
		}

		if (oldpt) {
			_noParentCallback = true;
			try {
				oldpt.removeChild(child, ignoreDom);
			} finally {
				_noParentCallback = false;
			}
		}

		child.parent = this;
		var ref = this.lastChild;
		if (ref) {
			ref.nextSibling = child;
			child.previousSibling = ref;
			this.lastChild = child;
		} else {
			this.firstChild = this.lastChild = child;
		}
		++this.nChildren;

		if (child.id || child.firstChild) //optimize for mount.js's create()
			_addIdSpaceDown(child);

		if (!ignoreDom) {
			if (this.shallChildROD_(child))
				this.get$Class<typeof Widget>()._bindrod(child);
			else {
				var dt = this.desktop;
				if (dt) this.insertChildHTML_(child, undefined, dt);
			}
		}

		child.afterParentChanged_(oldpt);
		if (!_noChildCallback) {
			this.onChildAdded_(child);
			this.triggerAfterHostChildAdded_(child);
		}
		return true;
	}

	/**
	 * @returns whether a new child shall be ROD.
	 * @defaultValue return true if child.z_rod or this.z_rod
	 * @since 5.0.1
	 * @internal
	 */
	shallChildROD_(child: zk.Widget): boolean {
		return !!(child.z_rod || this.z_rod);
	}

	/**
	 * Inserts a child widget before the reference widget (the `sibling` argument).
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If this widget is bound to the DOM tree, this method invoke {@link insertChildHTML_}
	 * to insert the DOM content of the child to the DOM tree. Thus, override {@link insertChildHTML_}
	 * if you want to insert more than the DOM content generated by {@link redraw}.</li>
	 * <li>If a widget wants to do something when the parent is changed,
	 * overrides {@link beforeParentChanged_} (which is called by
	 * {@link insertBefore}, {@link removeChild} and {@link appendChild}).
	 *
	 * @param child - the child widget
	 * @param sibling - the sibling widget (the 'insert' point where
	 * the new widget will be placed before). If null or omitted, it is
	 * the same as {@link appendChild}
	 * @returns whether the widget was added successfully. It returns false if the child is always the last child ({@link lastChild}).
	 * @see {@link appendChild}
	 */
	insertBefore(child: zk.Widget, sibling?: zk.Widget, ignoreDom?: boolean): boolean {
		if (!this.beforeChildAdded_(child, sibling)) {
			return false;
		}
		if (!sibling || sibling.parent != this) {
			this.insertingBefore_ = true;
			try {
				return this.appendChild(child, ignoreDom);
			} finally {
				this.insertingBefore_ = false;
			}
		}

		if (child == sibling || child.nextSibling == sibling)
			return false;

		var oldpt: zk.Widget | undefined;
		if ((oldpt = child.parent) != this)
			child.beforeParentChanged_(this);

		if (oldpt) {
			_noParentCallback = true;
			try {
				oldpt.removeChild(child, ignoreDom);
			} finally {
				_noParentCallback = false;
			}
		}

		child.parent = this;
		var ref = sibling.previousSibling;
		if (ref) {
			child.previousSibling = ref;
			ref.nextSibling = child;
		} else this.firstChild = child;

		sibling.previousSibling = child;
		child.nextSibling = sibling;

		++this.nChildren;

		_addIdSpaceDown(child);

		if (!ignoreDom) {
			if (this.shallChildROD_(child))
				this.get$Class<typeof Widget>()._bindrod(child);
			else {
				var dt = this.desktop;
				if (dt) this.insertChildHTML_(child, sibling, dt);
			}
		}

		child.afterParentChanged_(oldpt);
		if (!_noChildCallback) {
			this.onChildAdded_(child);
			this.triggerAfterHostChildAdded_(child);
		}
		return true;
	}

	/** A callback called before removing a child.
	 * @param child - the child being removed.
	 * @since 10.0.0
	 * @internal
	 */
	beforeChildRemoved_(child: zk.Widget): void {
		return;
	}

	/** A callback called before removing a child for shadow host.
	 * @param child - the child being removed.
	 * @since 10.0.0
	 * @internal
	 */
	triggerBeforeHostChildRemoved_(child: zk.Widget): void {
		return;
	}

	/**
	 * Removes a child with more control.
	 * It is similar to {@link removeChild} except the caller
	 * could prevent it from removing the DOM element.
	 *
	 * <p>Notice that the associated DOM elements and {@link unbind_}
	 * is called first (i.e., called before {@link beforeParentChanged_},
	 * modifying the widget tree, ID space, and {@link onChildRemoved_}).
	 * @param child - the child to remove.
	 * @param ignoreDom - whether to remove the DOM element
	 * @returns whether it is removed successfully.
	 * @see {@link detach}
	 * @see {@link clear}
	 */
	removeChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		var oldpt: zk.Widget | undefined;
		if (!(oldpt = child.parent))
			return false;
		if (this != oldpt)
			return false;

		this.beforeChildRemoved_(child);
		this.triggerBeforeHostChildRemoved_(child);
		_rmIdSpaceDown(child);

		//Note: remove HTML and unbind first, so unbind_ will have all info
		if (child.z_rod) {
			this.get$Class<typeof Widget>()._unbindrod(child);

			// Bug ZK-454
			jq(child.uuid, zk).remove();
		} else if (child.desktop)
			this.removeChildHTML_(child, ignoreDom);

		if (!_noParentCallback)
			child.beforeParentChanged_(undefined);

		_unlink(this, child);


		if (!_noParentCallback)
			child.afterParentChanged_(oldpt);
		if (!_noChildCallback) {
			this.onChildRemoved_(child);
			this.triggerAfterHostChildRemoved_(child);
		}
		return true;
	}

	/** Removes this widget (from its parent).
	 * If it was attached to a DOM tree, the associated DOM elements will
	 * be removed, too.
	 * @see {@link removeChild}
	 */
	detach(): void {
		if (this.parent) this.parent.removeChild(this);
		else {
			var cf = zk.currentFocus;
			if (cf && zUtl.isAncestor(this, cf))
				zk.currentFocus = undefined;
			var n = this.$n();
			if (n) {
				this.unbind();
				_rmDom(this, n);
			}
		}
	}

	/** Removes all children.
	 */
	clear(): void {
		while (this.lastChild)
			this.removeChild(this.lastChild);
	}

	/** Replaces this widget with the specified one.
	 * The parent and siblings of this widget will become the parent
	 * and siblings of the specified one.
	 * <p>Notice that {@link replaceHTML} is used to replace a DOM element
	 * that usually doesn't not belong to any widget.
	 * And, {@link replaceWidget} is used to replace the widget, and
	 * it maintains both the widget tree and the DOM tree.
	 * @param newwgt - the new widget that will replace this widget.
	 * @param skipper - the skipper used to skip a portion of DOM nodes.
	 * @see {@link replaceHTML}
	 * @since 5.0.1
	 */
	replaceWidget(newwgt: zk.Widget, skipper?: Skipper): void {
		_replaceLink(this, newwgt);

		_rmIdSpaceDown(this);
		_addIdSpaceDown(newwgt);

		var cf: zk.Widget | undefined = zk.currentFocus, cfid,
			cfrg: zk.Offset | undefined;
		if (cf && zUtl.isAncestor(this, cf)) {
			cfid = cf.uuid;
			cfrg = _bkRange(cf);
			zk.currentFocus = undefined;
		}

		// eslint-disable-next-line zk/noNull
		let node: HTMLElement | undefined | null = this.$n(),
			p = this.parent, shallReplace,
			dt = newwgt.desktop || this.desktop;
		if (this.z_rod) {
			this.get$Class<typeof Widget>()._unbindrod(this);
			if (!(shallReplace = (dt = dt || (p ? p.desktop : p))
				&& (node = document.getElementById(this.uuid))))
				this.get$Class<typeof Widget>()._bindrod(newwgt);
		} else
			shallReplace = dt;

		// ZK-5050
		if (p)
			p.beforeChildReplaced_(this, newwgt);

		var callback: CallableFunction[] = [];
		if (shallReplace) {
			if (node) newwgt.replaceHTML(node, dt, skipper, true, callback);
			else {
				this.unbind();
				newwgt.bind(dt);
			}

			_fixBindLevel(newwgt, p ? p.bindLevel + 1 : 0);
			zWatch.fire('onBindLevelMove', newwgt);
		}

		if (p)
			p.onChildReplaced_(this, newwgt);

		this.parent = this.nextSibling = this.previousSibling = undefined;

		// For Bug ZK-2271, we delay the fireSized calculation after p.onChilReplaced_,
		// because the sub-nodes mapping are not getting up to date.
		if (callback && callback.length) {
			let f: CallableFunction | undefined;
			while ((f = callback.shift()) && typeof f === 'function')
				f();
		}
		if (cfid) {
			cf = zk.Widget.$(cfid) as zk.Widget | undefined;
			if (!cf) {
				// Bug ZK-2664, we should not restore the focus to root component, which
				// may not be correct one.
				// _rsFocus({focus: newwgt, range: cfrg}); // restore to outer root
			} else if (zUtl.isAncestor(newwgt, cf))
				_rsFocus({focus: cf, range: cfrg});
		}
	}

	/** Replaced the child widgets with the specified widgets.
	 * It is useful if you want to replace a part of children whose
	 * DOM element is a child element of `subId` (this.$n(subId)).
	 * <p>Note: it assumes this.$n(subId) exists.
	 * @param subId - the ID of the cave that contains the child widgets
	 * to replace with.
	 * @param wgts - an array of widgets that will become children of this widget
	 * @param tagBeg - the beginning of HTML tag, such as `<tbody>`.
	 * Ignored if null.
	 * @param tagEnd - the ending of HTML tag, such as `</tbody>`;
	 * Ignored if null.
	 * @see zAu#createWidgets
	 * @internal
	 */
	replaceCavedChildren_(subId: string, wgts: zk.Widget[], tagBeg?: string, tagEnd?: string): void {
		_noChildCallback = true; //no callback
		try {
			//1. remove (but don't update DOM)
			var cave = this.$n(subId), fc: zk.Widget | zk.Desktop | undefined, oldwgts: zk.Widget[] = [];
			for (var w = this.firstChild; w;) {
				var sib = w.nextSibling;
				if (jq.isAncestor(cave, w.$n())) {
					if (!fc || fc == w) fc = sib;
					this.removeChild(w, true); //no dom
					oldwgts.push(w);
				}
				w = sib;
			}

			//2. insert (but don't update DOM)
			for (var j = 0, len = wgts.length; j < len; ++j)
				this.insertBefore(wgts[j], fc, true); //no dom
		} finally {
			_noChildCallback = false;
		}

		if (fc = this.desktop) {
			//3. generate HTML
			var out = new zk.Buffer();
			if (tagBeg) out.push(DOMPurify.sanitize(tagBeg));
			for (var j = 0, len = wgts.length; j < len; ++j)
				wgts[j].redraw(out);
			if (tagEnd) out.push(DOMPurify.sanitize(tagEnd));

			//4. update DOM
			// eslint-disable-next-line @microsoft/sdl/no-html-method
			jq(cave).html(out.join(''));

			//5. bind
			for (var j = 0, len = wgts.length; j < len; ++j) {
				wgts[j].bind(fc as zk.Desktop | undefined);
				//Bug 3322909 Dirty fix for nrows counting wrong,
				//currently the nrows is for Listbox.
				var n = this['_nrows'] as never;
				this.onChildReplaced_(oldwgts[j], wgts[j]);
				this['_nrows'] = n;
			}
		}
	}

	/** A callback called before the parent is changed.
	 * @param newparent - the new parent (null if it is removed)
	 * The previous parent can be found by {@link parent}.
	 * @see {@link onChildAdded_}
	 * @see {@link onChildRemoved_}
	 * @see {@link afterParentChanged_}
	 * @internal
	 */
	beforeParentChanged_(newparent?: zk.Widget): void {
		return;
	}

	/**
	 * A callback called before the parent is changed for shadow host.
	 * @param newparent - the new parent (null if it is removed)
	 * The previous parent can be found by {@link parent}.
	 * @since 10.0.0
	 * @internal
	 */
	triggerBeforeHostParentChanged_(newparent?: zk.Widget): void {
		return;
	}

	/** A callback called after the parent has been changed.
	 * @param oldparent - the previous parent (null if it was not attached)
	 * The current parent can be found by {@link parent}.
	 * @since 5.0.4
	 * @see {@link beforeParentChanged_}
	 * @internal
	 */
	afterParentChanged_(oldparent?: zk.Widget): void {
		return;
	}

	/**
	 * @returns if this widget is really visible, i.e., all ancestor widget and itself are visible.
	 * @param opts - the options. Allowed values:
	 * <ul>
	 * <li>dom - whether to check DOM element instead of {@link isVisible}</li>
	 * <li>until - specifies the ancestor to search up to (included).
	 * If not specified, this method searches all ancestors.
	 * If specified, this method searches only this widget and ancestors up
	 * to the specified one (included).</li>
	 * <li>strict - whether to check DOM element's style.visibility.
	 * It is used only if `dom` is also specified.</li>
	 * <li>cache - a map of cached result (since 5.0.8). Ignored if null.
	 * If specified, the result will be stored and used to speed up the processing.</li>
	 * </ul>
	 * @see {@link isVisible}
	 */
	isRealVisible(opts?: RealVisibleOptions): boolean {
		var dom = opts && opts.dom,
			cache = opts && opts.cache, visited: zk.Widget[] = [], ck: undefined | string | zk.Widget;

		//Bug ZK-1692: widget may not bind or render yet.
		if (!this.desktop)
			return false;

		let wgt: zk.Widget | undefined = this;
		while (wgt) {
			if (cache && (ck = wgt.uuid) && (ck = cache[ck] as never) !== undefined)
				return _markCache(cache as never, visited, ck);

			if (cache)
				visited.push(wgt);

			if (dom && !(wgt as {z_virnd?}).z_virnd) { //z_virnd implies zk.Native, zk.Page and zk.Desktop
				//Except native, we have to assume it is invsibile if $n() is null
				//Example, tabs in the accordion mold (case: zktest/test2 in IE)
				//Alertinative is to introduce another isVisibleXxx but not worth
				if (!zk(wgt.$n()).isVisible(opts?.strict))
					return _markCache(cache as never, visited, false);
			} else if (!wgt.isVisible())
				return _markCache(cache as never, visited, false);

			//check if it is hidden by parent, such as child of hbox/vbox or border-layout
			const wp = wgt.parent;
			let p: HTMLElement | undefined, n: HTMLElement | undefined;
			if (wp && wp._visible && (n = wgt.$n()) && (p = zk(n).vparentNode(true)))
				while (p && p != n) {
					if (p.style && p.style.display == 'none') //hidden by parent
						return _markCache(cache as never, visited, false);
					p = zk(p).vparentNode(true);
				}

			if (opts && opts.until == wgt)
				break;

			wgt = wp as never;
		}
		return _markCache(cache as never, visited, true);
	}

	/**
	 * @returns if this widget is visible
	 * @param strict - whether to check the visibility of the associated
	 * DOM element. If true, this widget and the associated DOM element
	 * must be both visible.
	 * @see {@link isRealVisible}
	 * @see {@link zk.JQZK.isVisible}
	 * @see {@link setVisible}
	 */
	isVisible(strict?: boolean): boolean {
		const visible = this._visible;
		if (!strict || !visible)
			return visible;
		const n = this.$n();
		return !!(n && zk(n).isVisible()); //ZK-1692: widget may not bind or render yet
	}

	/** Sets whether this widget is visible.
	 * <h3>Subclass Notes</h3>
	 * <ul>
	 * <li>setVisible invokes the parent's {@link onChildVisible_}, so you
	 * can override {@link onChildVisible_} to change the related DOM element.
	 * For example, updating the additional enclosing tags (such as zul.box.Box). </li>
	 * <li>setVisible invokes {@link setDomVisible_} to change the visibility of a child DOM element, so override it if necessary.</li>
	 * </ul>
	 * @param visible - whether to be visible
	 */
	setVisible(visible: boolean): this {
		if (this._visible != visible) {
			this._visible = visible;

			var p = this.parent, ocvCalled;
			if (this.desktop) {
				var parentVisible = !p || p.isRealVisible(),
					node = this.$n()!,
					floating = this._floating;

				if (!parentVisible) {
					if (!floating) this.setDomVisible_(node, visible);
				} else if (visible) {
					var zi: number | undefined;
					if (floating)
						this.setZIndex(zi = _topZIndex(this), {fire: true, floatZIndex: true});

					this.setDomVisible_(node, true);

					//from parent to child
					for (var j = 0, fl = _floatings.length; j < fl; ++j) {
						var w = _floatings[j].widget,
							n = _floatings[j].node;
						if (this == w)
							w.setDomVisible_(n, true, {visibility: true});
						else if (_floatVisibleDependent(this, w)) {
							zi = zi != null && zi >= 0 ? ++zi : _topZIndex(w);
							w.setFloatZIndex_(n, zi);
							w.setDomVisible_(n, true, {visibility: true});
						}
					}

					if (ocvCalled = p) p.onChildVisible_(this);
					//after setDomVisible_ and before onShow (Box depends on it)

					this.fire('onShow');
					// B70-ZK-2032: Fire shown after animate
					var wgt = this;
					zk.afterAnimate(function () {zUtl.fireShown(wgt);}, -1);
				} else {
					this.fire('onHide');
					// B70-ZK-2032: Fire down onHide after animate
					var wgt = this;
					zWatch.fireDown('onHide', this);

					for (var j = _floatings.length, bindLevel = this.bindLevel; j--;) {
						var w = _floatings[j].widget;
						if (bindLevel >= w.bindLevel)
							break; //skip non-descendant (and this)
						if (_floatVisibleDependent(this, w))
							w.setDomVisible_(_floatings[j].node, false, {visibility: true});
					}

					this.setDomVisible_(node, false);

					// Bug ZK-2236 we need to inform its parent to do the resize.
					if (wgt._nvflex || wgt._nhflex)
						zk.afterAnimate(function () {
							if (wgt.parent)
								zUtl.fireSized(wgt.parent);
						}, -1);
				}
			}
			if (p && !ocvCalled) p.onChildVisible_(this);
			//after setDomVisible_ and after onHide
			jq.onSyncScroll(this);
		}
		return this;
	}

	/**
	 * @returns the focus set by {@link setFocus}.
	 * <p>Note: it simply returns what is passed to {@link setFocus}.
	 * @defaultValue `false`
	 * @since 10.0.0
	 */
	isFocus(): boolean {
		return this._focus;
	}
	/**
	 * Sets the focus on this widget.
	 * @param focus - True to focus on this widget.
	 * @since 10.0.0
	 */
	setFocus(focus: boolean): this {
		if (this._focus != focus) {
			this._focus = focus;
			if (focus) {
				if (this.desktop) {
					zAu.cmd1.focus(this);
				} else {
					zk.afterMount(() => {
						zAu.cmd1.focus(this);
					});
				}
			}
		}
		return this;
	}
	/** Synchronizes a map of objects that are associated with this widget, and
	 * they shall be resized when the size of this widget is changed.
	 * <p>It is useful to sync the layout, such as shadow, mask
	 * and error message, that is tightly associated with a widget.
	 * @param opts - the options, or undefined if none of them specified.
	 * Allowed values:<br/>
	 */
	zsync(opts?: zk.Object): void {
		for (var nm in this.effects_) {
			var ef = this.effects_[nm];
			if (ef && ef.sync) ef.sync();
		}
	}

	/**
	 * Makes this widget visible.
	 * It is a shortcut of `setVisible(true)`
	 */
	show(): this {
		this.setVisible(true);
		return this;
	}

	/**
	 * Makes this widget invisible.
	 * It is a shortcut of `setVisible(false)`
	 */
	hide(): this {
		this.setVisible(false);
		return this;
	}

	/** Changes the visibility of a child DOM content of this widget.
	 * It is called by {@link setVisible} to really change the visibility
	 * of the associated DOM elements.
	 * @defaultValue change n.style.display directly.
	 * @param n - the element (never null)
	 * @param visible - whether to make it visible
	 * @param opts - the options.
	 * If omitted, `{display:true}` is assumed. Allowed value:
	 * <ul>
	 * <li>display - Modify n.style.display</li>
	 * <li>visibility - Modify n.style.visibility</li>
	 * </ul>
	 * @internal
	 */
	setDomVisible_(domVisible: HTMLElement, visible: boolean, opts?: DomVisibleOptions): void {
		if (!opts || opts.display) {
			var act: [CallableFunction, unknown] | undefined;
			if (act = this.actions_[visible ? 'show' : 'hide'] as [CallableFunction, unknown] | undefined)
				act[0].bind(this)(domVisible, act[1]);
			else
				domVisible.style.display = visible ? '' : 'none';
		}
		if (opts && opts.visibility)
			domVisible.style.visibility = visible ? 'visible' : 'hidden';
	}
	/**
	 * Called before adding a child.
	 * If a widget accepts only certain types of children, it shall
	 * override this method and return false for an illegal child.
	 *
	 * @param child - the child to be added (never null).
	 * @param insertBefore - another child widget that the new child
	 * will be inserted before it. If null, the new child will be the
	 * last child.
	 * @returns whether the widget was able to added.
	 * @since 10.0.0
	 * @internal
	 */
	beforeChildAdded_(child: zk.Widget, insertBefore?: zk.Widget): boolean {
		return true; //to be overridden
	}

	/**
	 * Called before adding a child for shadow host.
	 * @since 10.0.0
	 * @internal
	 */
	triggerBeforeHostChildAdded_(child: zk.Widget, insertBefore?: zk.Widget): void {
		return; //to be overridden
	}
	/** A callback called after a child has been added to this widget.
	 * <p>Notice: when overriding this method, {@link onChildReplaced_}
	 * is usually required to override, too.
	 * @param child - the child being added
	 * @see {@link beforeParentChanged_}
	 * @see {@link onChildRemoved_}
	 * @internal
	 */
	onChildAdded_(child: zk.Widget): void {
		if (this.desktop)
			jq.onSyncScroll(this);
	}

	/** A callback called after a child has been added to this widget for shadow host.
	 * @since 10.0.0
	 * @param child - the child being added
	 * @internal
	 */
	triggerAfterHostChildAdded_(child: zk.Widget): void {
		return;
	}

	/** A callback called after a child has been removed to this widget.
	 * <p>Notice: when overriding this method, {@link onChildReplaced_}
	 * @param child - the child being removed
	 * @see {@link beforeParentChanged_}
	 * @see {@link onChildAdded_}
	 * @internal
	 */
	onChildRemoved_(child: zk.Widget): void {
		if (this.desktop)
			jq.onSyncScroll(this);
	}

	/** A callback called after a child has been removed to this widget for shadow
	 * host.
	 * @param child - the child being removed
	 * @since 10.0.0
	 * @internal
	 */
	triggerAfterHostChildRemoved_(child: zk.Widget): void {
		return;
	}
	/** A callback called after a child has been replaced.
	 * Unlike {@link onChildAdded_} and {@link onChildRemoved_}, this
	 * method is called only if {@link zk.AuCmd1#outer}.
	 * And if this method is called, neither {@link onChildAdded_} nor {@link onChildRemoved_}
	 * will be called.
	 * @defaultValue invoke {@link onChildRemoved_} and then
	 * {@link onChildAdded_}.
	 * Furthermore, it sets this.childReplacing_ to true before invoking
	 * {@link onChildRemoved_} and {@link onChildAdded_}, so we can optimize
	 * the code (such as rerender only once) by checking its value.
	 * @param oldc - the old child (being removed). Note: it might be null.
	 * @param newc - the new child (being added). Note: it might be null.
	 * @internal
	 */
	onChildReplaced_(oldc: zk.Widget | undefined, newc: zk.Widget | undefined): void {
		this.childReplacing_ = true;
		try {
			if (oldc) {
				this.onChildRemoved_(oldc);
				this.triggerAfterHostChildRemoved_(oldc);
			}
			if (newc) {
				this.onChildAdded_(newc);
				this.triggerAfterHostChildAdded_(newc);
			}
		} finally {
			this.childReplacing_ = false;
		}
	}

	/** A callback called after a child's visibility is changed
	 * (i.e., {@link setVisible} was called).
	 * <p>Notice that this method is called after the _visible property
	 * and the associated DOM element(s) have been changed.
	 * <p>To know if it is becoming visible, you can check {@link isVisible}
	 * (such as this._visible).
	 * @param child - the child whose visiblity is changed
	 * @internal
	 */
	onChildVisible_(child: zk.Widget): void {
		return;
	}

	/** A callback called after a child has been delay rendered.
	 * @param child - the child being rendered
	 * @see {@link deferRedraw_}
	 * @since 6.5.1
	 * @internal
	 */
	onChildRenderDefer_(child: zk.Widget): void {
		return;
	}

	/** Makes this widget as topmost.
	 * <p>If this widget is not floating, this method will look for its ancestors for the first ancestor who is floating. In other words, this method makes the floating containing this widget as topmost.
	 * To make a widget floating, use {@link setFloating_}.
	 * <p>This method has no effect if it is not bound to the DOM tree, or none of the widget and its ancestors is floating.
	 * <p>Notice that it does not fire onFloatUp so it is caller's job if it is necessary
	 * to close other popups.
	 * @returns the new value of z-index of the topmost floating window, -1 if this widget and none of its ancestors is floating or not bound to the DOM tree.
	 * @see {@link setFloating_}
	 */
	// eslint-disable-next-line zk/javaStyleSetterSignature
	setTopmost(): number {
		if (!this.desktop || this._userZIndex) return -1;

		for (let wgt: zk.Widget | undefined = this; wgt; wgt = wgt.parent)
			if (wgt._floating) {
				let zi = _topZIndex(wgt);
				for (let j = 0, fl = _floatings.length; j < fl; ++j) { //from child to parent
					const w = _floatings[j].widget,
						n = _floatings[j].node;
					if (wgt == w)
						w.setFloatZIndex_(n, zi); //must be hit before any parent
					else if (zUtl.isAncestor(wgt, w) && w.isVisible())
						w.setFloatZIndex_(n, ++zi);
				}
				return zi;
			}
		return -1;
	}

	/** Sets the z-index for a floating widget.
	 * It is called by {@link setTopmost} to set the z-index,
	 * and called only if {@link setFloating_} is ever called.
	 * @param node - the element whose z-index needs to be set.
	 * It is the value specified in `opts.node` when {@link setFloating_}
	 * is called. If not specified, it is the same as {@link Widget.$n}.
	 * @param zi - the z-index to set
	 * @see {@link setFloating_}
	 * @since 5.0.3
	 * @internal
	 */
	// eslint-disable-next-line zk/javaStyleSetterSignature
	setFloatZIndex_(node: HTMLElement, zi: number): void {
		if (node != this.$n()) node.style.zIndex = zi as unknown as string; //only a portion
		else this.setZIndex(zi, {fire: true, floatZIndex: true});
	}

	/**
	 * @returns the z-index of a floating widget.
	 * It is called by {@link setTopmost} to decide the topmost z-index,
	 * and called only if {@link setFloating_} is ever called.
	 * @param node - the element whose z-index needs to be set.
	 * It is the value specified in `opts.node` when {@link setFloating_}
	 * is called. If not specified, it is the same as {@link Widget.$n}.
	 * @since 5.0.3
	 * @see {@link setFloating_}
	 * @internal
	 */
	getFloatZIndex_(node: HTMLElement): number | string {
		return node != this.$n() ? node.style.zIndex : this._zIndex;
	}

	/**
	 * @returns the top widget, which is the first floating ancestor,
	 * or null if no floating ancestor.
	 * @see {@link isFloating_}
	 */
	getTopWidget(): zk.Widget | undefined {
		for (var wgt: zk.Widget | undefined = this; wgt; wgt = wgt.parent)
			if (wgt._floating)
				return wgt;
	}

	/**
	 * @returns if this widget is floating.
	 * <p>We say a widget is floating if the widget floats on top of others, rather than embed inside the parent. For example, an overlapped window is floating, while an embedded window is not.
	 * @see {@link setFloating_}
	 * @internal
	 */
	isFloating_(): boolean {
		return this._floating;
	}

	/** Sets a status to indicate if this widget is floating.
	 * <p>Notice that it doesn't change the DOM tree. It is caller's job.
	 * In the other words, the caller have to adjust the style by assigning
	 * `position` with `absolute` or `relative`.
	 * @param floating - whether to make it floating
	 * @param opts - The options. Allowed options:
	 * <ul>
	 * <li>node: the DOM element. If omitted, {@link Widget.$n} is assumed.</li>
	 * </ul>
	 * @see {@link isFloating_}
	 * @internal
	 */
	setFloating_(floating: boolean, opts?: Partial<{ node: HTMLElement }>): void {
		if (this._floating != floating) {
			if (floating) {
				//parent first
				var inf = {widget: this, node: opts && opts.node ? opts.node : this.$n()!},
					bindLevel = this.bindLevel;
				for (var j = _floatings.length; ;) {
					if (--j < 0) {
						_floatings.unshift(inf);
						break;
					}
					if (bindLevel >= _floatings[j].widget.bindLevel) { //parent first
						_floatings.splice(j + 1, 0, inf);
						break;
					}
				}
				this._floating = true;
			} else {
				for (var j = _floatings.length; j--;)
					if (_floatings[j].widget == this)
						_floatings.splice(j, 1);
				this._floating = false;
			}
		}
	}

	/**
	 * @returns the Z index.
	 */
	getZIndex(): number | string {
		return this._zIndex;
	}
	getZindex(): number | string {
		return this._zIndex;
	}

	/** Sets the Z index.
	 * @param zIndex - the Z index to assign to
	 * @param opts - if opts.fire is specified the onZIndex event will be triggered. If opts.floatZIndex is false, represent it is not from setFloatZIndex, so the userZIndex may be true.
	 */
	setZIndex(zIndex: number | string, opts: Partial<{ floatZIndex: boolean; fire: boolean }>): this {
		if (opts && opts.floatZIndex && this._userZIndex)
			return this;
		if (!opts || !opts.floatZIndex)
			this._userZIndex = true;
		if (!zIndex)
			this._userZIndex = false;
		if (this._zIndex != zIndex) {
			this._zIndex = zIndex;
			var n = this.$n();
			if (n) {
				n.style.zIndex = (zIndex as number) >= 0 ? (zIndex as string) : '';
				if (opts && opts.fire) this.fire('onZIndex', ((zIndex as number) > 0 || zIndex === 0) ? zIndex : -1, {ignorable: true});
			}
		}
		return this;
	}

	setZindex(zindex: number, opts: Partial<{ floatZIndex: boolean; fire: boolean }>): this {
		return this.setZIndex(zindex, opts);
	}

	/**
	 * @returns the scroll top of the associated DOM element of this widget.
	 * 0 is always returned if this widget is not bound to a DOM element yet.
	 */
	getScrollTop(): number {
		var n = this.$n();
		return n ? n.scrollTop : 0;
	}

	/**
	 * @returns the scroll left of the associated DOM element of this widget.
	 * 0 is always returned if this widget is not bound to a DOM element yet.
	 */
	getScrollLeft(): number {
		var n = this.$n();
		return n ? n.scrollLeft : 0;
	}

	/**
	 * Sets the scroll top of the associated DOM element of this widget.
	 * This method does nothing if this widget is not bound to a DOM element yet.
	 */
	setScrollTop(scrollTop: number): this {
		var n = this.$n();
		if (n) n.scrollTop = scrollTop;
		return this;
	}

	/**
	 * Sets the scroll left of the associated DOM element of this widget.
	 * This method does nothing if this widget is not bound to a DOM element yet.
	 */
	setScrollLeft(scrollLeft: number): this {
		const n = this.$n();
		if (n) n.scrollLeft = scrollLeft;
		return this;
	}

	/**
	 * Makes this widget visible in the browser window by scrolling ancestors up or down, if necessary.
	 * @defaultValue invoke zk(this).scrollIntoView();
	 * @see {@link zk.JQZK.scrollIntoView}
	 */
	scrollIntoView(): this {
		zk(this.$n()).scrollIntoView();
		return this;
	}

	/**
	 * Generates the HTML fragment for this widget.
	 * The HTML fragment shall be pushed to out. For example,
	 * ```ts
	 * out.push('<div', this.domAttrs_(), '>');
	 * for (var w = this.firstChild; w; w = w.nextSibling)
	 *   w.redraw(out);
	 * out.push('</div>');
	 * ```
	 * @defaultValue it retrieves the redraw function associated with
	 * the mold ({@link getMold}) and then invoke it.
	 * The redraw function must have the same signature as this method.
	 * @param out - an array to output HTML fragments.
	 * Technically it can be anything that has the method called `push`
	 */
	redraw(out: string[], skipper?: Skipper): void {
		if (!this.deferRedraw_(out)) {
			var f: string | undefined | typeof Widget | Record<string, CallableFunction>;
			if (f = this.prolog)
				out.push(DOMPurify.sanitize(f)); // unlike Native and ZHTML, we should sanitize the html content here.

			let fn: CallableFunction | undefined;
			if ((f = this.get$Class<typeof Widget>().molds) && (fn = f[this._mold])) {
				fn.bind(this)(...(arguments as unknown as []));
				return;
			}

			zk.error('Mold ' + this._mold + ' not found in ' + this.className);
		}
	}

	/** Utilities for handling the so-called render defer ({@link setRenderdefer}).
	 * This method is called automatically by {@link redraw},
	 * so you only need to use it if you override {@link redraw}.
	 * <p>A typical usage is as follows.
	 * ```ts
	 * redraw: function (out) {
	 *   if (!this.deferRedraw_(out)) {
	 * 		out.push(...); //redraw
	 *   }
	 * }
	 * ```
	 * @param out - an array to output the HTML fragments.
	 * @since 5.0.2
	 * @internal
	 */
	deferRedraw_(out: string[] | undefined): boolean {
		var delay: undefined | number;
		if ((delay = this._renderdefer) && delay != null && delay >= 0) {
			if (!this._norenderdefer) {
				this.z_rod = this._z$rd = true;
				this.deferRedrawHTML_(out!);
				out = undefined; //to free memory

				var wgt = this;
				setTimeout(function () {_doDeferRender(wgt);}, delay);
				return true;
			}
			delete this._norenderdefer;
			delete this.z_rod;
		}
		return false;
	}

	/**
	 * Renders a fake DOM element that will replace with the correct element after
	 * the deferring time is up. The method is designed for some widgets to override,
	 * such as Treeitem, Listitem, and Row, whose HTML tag is created inside a table.
	 * <p>By default, the Div tag is assumed.
	 * @param out - an array to output the HTML fragments.
	 * @since 5.0.6
	 * @internal
	 */
	deferRedrawHTML_(out: string[]): void {
		out.push('<div', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></div>');
	}

	/** Forces the rendering if it is deferred.
	 * A typical way to defer the render is to specify {@link setRenderdefer}
	 * with a non-negative value. The other example is some widget might be
	 * optimized for the performance by not rendering some or the whole part
	 * of the widget. If the rendering is deferred, the corresponding DOM elements
	 * ({@link Widget.$n}) are not available. If it is important to you, you can
	 * force it to be rendered.
	 * <p>Notice that this method only forces this widget to render. It doesn't
	 * force any of its children. If you want, you have invoke {@link forcerender}
	 * one-by-one
	 * <p>The derived class shall override this method, if it implements
	 * the render deferring (other than {@link setRenderdefer}).
	 * @since 5.0.2
	 */
	forcerender(): void {
		_doDeferRender(this);
	}

	/** Updates the DOM element's CSS class. It is called when the CSS class is changed (e.g., setZclass is called).
	 * @defaultValue it changes the class of {@link Widget.$n}.
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>Override it if the class has to be copied to DOM elements other than {@link Widget.$n}.</li>
	 * </ul>
	 * @see {@link updateDomStyle_}
	 * @internal
	 */
	updateDomClass_(): void {
		if (this.desktop) {
			var n = this.$n();
			if (n) /*safe*/ n.className = this.domClass_();
			this.zsync();
		}
	}

	/** Updates the DOM element's style. It is called when the CSS style is changed (e.g., setStyle is called).
	 * @defaultValue it changes the CSS style of {@link Widget.$n}.
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>Override it if the CSS style has to be copied to DOM elements other than {@link Widget.$n}.</li>
	 * </ul>
	 * @internal
	 */
	updateDomStyle_(): void {
		if (this.desktop) {
			var s = jq.parseStyle(this.domStyle_()),
				n = this.$n()!;
			// B50-3355680: size is potentially affected when setStyle
			if (!s.width && this._hflex)
				s.width = n.style.width;
			if (!s.height && this._vflex)
				s.height = n.style.height;
			zk(n).clearStyles().jq.css(s);

			var t = this.getTextNode();
			if (t && t != n) {
				s = /*safe*/ this._domTextStyle(t, s);
				zk(t).clearStyles().jq.css(s);
			}
			this.zsync();
		}
	}
	/** @internal */
	_domTextStyle(t: HTMLElement, s: Record<string, string>): Record<string, string> {
		// B50-3355680
		const style = /*safe*/ jq.filterTextStyle(s);
		// B70-ZK-1807: reserve style width and height,it will make sure that textnode has correct size.
		if (t.style.width)
			style.width = t.style.width;
		if (t.style.height)
			style.height = t.style.height;
		return style;
	}

	/**
	 * @returns the DOM element that is used to hold the text, or null
	 * if this widget doesn't show any text.
	 * @defaultValue return null (no text node).
	 * <p>For example, {@link updateDomStyle_} will change the style
	 * of the text node, if any, to make sure the text is displayed correctly.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component_Development/Client-side/Text_Styles_and_Inner_Tags">ZK Client-side Reference: Text Styles and Inner Tags</a>.
	 * @see {@link domTextStyleAttr_}
	 * @see {@link updateDomStyle_}
	 */
	getTextNode(): HTMLElement | undefined {
		return undefined;
	}

	/**
	 * @returns the style used for the DOM element of this widget, such as `"width:100px;z-index:1;"`
	 * @defaultValue a concatenation of style, width, visible and so on.
	 * @param no - [options] the style to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want width to generate, call `domStyle_({width:1})`.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>style - exclude {@link getStyle}</li>
	 * <li>width - exclude {@link getWidth}</li>
	 * <li>height - exclude {@link getHeight}</li>
	 * <li>left - exclude {@link getLeft}</li>
	 * <li>top - exclude {@link getTop}</li>
	 * <li>zIndex - exclude {@link getZIndex}</li>
	 * <li>visible - exclude {@link isVisible}</li>
	 * </ul>
	 * @see {@link domClass_}
	 * @see {@link domAttrs_}
	 * @internal
	 */
	domStyle_(no?: DomStyleOptions): string {
		var out = '', s: string | undefined;
		if (s = this.z$display) //see au.js
			out += 'display:' + s + ';';
		else if (!this.isVisible() && (!no || !no.visible))
			out += 'display:none;';

		if ((!no || !no.style) && (s = /*safe*/ this.getStyle())) {
			s = s.replace(REGEX_DQUOT, '\'');  // B50-ZK-647
			out += s;
			if (!s.endsWith(';'))
				out += ';';
		}
		if ((!no || !no.width) && (s = this.getWidth()))
			out += 'width:' + s + ';';
		if ((!no || !no.height) && (s = this.getHeight()))
			out += 'height:' + s + ';';
		if ((!no || !no.left) && (s = this.getLeft()))
			out += 'left:' + s + ';';
		if ((!no || !no.top) && (s = this.getTop()))
			out += 'top:' + s + ';';
		let zIndex;
		if ((!no || !no.zIndex) && ((zIndex = this.getZIndex() as number)) >= 0)
			out += 'z-index:' + zIndex + ';';
		return DOMPurify.sanitize(out);
	}

	/**
	 * @returns the class name(s) used for the DOM element of this widget, such as `"z-button foo"`
	 * @defaultValue a concatenation of {@link getZclass} and {@link getSclass}.
	 *
	 * @param no - [options] the style class to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want sclass to generate, call `domClass_({sclass:1})`.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>sclass - exclude {@link getSclass}</li>
	 * <li>zclass - exclude {@link getZclass}</li>
	 * </ul>
	 * @see {@link domStyle_}
	 * @see {@link domAttrs_}
	 * @internal
	 */
	domClass_(no?: DomClassOptions): string {
		var s: undefined | string, z: undefined | string;
		if (!no || !no.sclass)
			s = /*safe*/ this.getSclass();
		if (!no || !no.zclass)
			z = /*safe*/ this.getZclass();
		let domClass = s ? z ? s + ' ' + z : s : z || '',
			n = this.$n();
		// FIX ZK-5137: modifying sclass clears vflex="1 here to avoid circular dependency issue in ZK 10
		if (n) {
			const jqn = jq(n),
				flexClasses = ['z-flex', 'z-flex-row', 'z-flex-column', 'z-flex-item'];
			for (let i = 0, length = flexClasses.length; i < length; i++) {
				const flexClass = flexClasses[i];
				if (jqn.hasClass(flexClass)) {
					domClass += ' ' + flexClass;
				}
			}
		}
		return DOMPurify.sanitize(domClass);
	}

	/**
	 * @returns the HTML attributes that is used to generate DOM element of this widget.
	 * It is usually used to implement a mold ({@link redraw}):
	 * ```ts
	 * function () {
	 *   return '<div' + this.domAttrs_() + '></div>';
	 * }
	 * ```
	 * @defaultValue it generates id, style, class, and tooltiptext.
	 * Notice that it invokes {@link domClass_} and {@link domStyle_},
	 * unless they are disabled by the `no` argument.
	 *
	 * @param no - [options] the attributes to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want the style class to generate, call `domAttrs_({domClass:1})`.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>id - exclude {@link uuid}</li>
	 * <li>domClass - exclude {@link domClass_}</li>
	 * <li>domStyle - exclude {@link domStyle_}</li>
	 * <li>tooltiptext - exclude {@link getTooltiptext}</li>
	 * <li>tabindex - exclude {@link getTabindex}</li>
	 * </ul>
	 * <p>return the HTML attributes, such as id="z_u7_3" class="z-button"
	 * @internal
	 */
	domAttrs_(no?: DomAttrsOptions): string {
		var outHtml = '', tempHtml: undefined | string, tabIndexHtml: undefined | number;
		if (!no) {
			if ((tempHtml = this.uuid))
				outHtml += ' id="' + tempHtml + '"';
			if ((tempHtml = this.domStyle_(no)))
				outHtml += ' style="' + tempHtml + '"';
			if ((tempHtml = this.domClass_(no)))
				outHtml += ' class="' + /*safe*/ zUtl.encodeXML(tempHtml) + '"';
			if ((tempHtml = this.domTooltiptext_()))
				outHtml += ' title="' + /*safe*/ zUtl.encodeXML(tempHtml) + '"'; // ZK-676
			if ((tabIndexHtml = /*safe*/ this.getTabindex()) != undefined)
				outHtml += ' tabindex="' + tabIndexHtml + '"';
		} else {
			if (!no.id && (tempHtml = this.uuid))
				outHtml += ' id="' + tempHtml + '"';
			if (!no.domStyle && (tempHtml = this.domStyle_(no)))
				outHtml += ' style="' + tempHtml + '"';
			if (!no.domClass && (tempHtml = this.domClass_(no)))
				outHtml += ' class="' + /*safe*/ zUtl.encodeXML(tempHtml) + '"';
			if (!no.tooltiptext && (tempHtml = this.domTooltiptext_()))
				outHtml += ' title="' + /*safe*/ zUtl.encodeXML(tempHtml) + '"'; // ZK-676
			if (!no.tabindex && (tabIndexHtml = /*safe*/ this.getTabindex()) != undefined)
				outHtml += ' tabindex="' + tabIndexHtml + '"';
		}
		if (this.domExtraAttrs) {
			outHtml += this.domExtraAttrs_();
		}
		return DOMPurify.sanitize(outHtml);
	}

	// B80-ZK-2957
	/** @internal */
	domExtraAttrs_(): string {
		var dh: Record<string, string> = {},
			has = false,
			out = '',
			attrs: Record<string, string> | undefined;

		for (var nm in (attrs = this.domExtraAttrs)) {
			if (zk.hasDataHandler(nm)) {
				has = true;
				dh[nm] = attrs[nm];
			}
			out += ' ' + nm + '="' + zUtl.encodeXMLAttribute(attrs[nm] ?? '') + '"'; //generate even if val is empty
		}
		if (has && !this.z_isDataHandlerBound) {
			this.z_isDataHandlerBound = function () {
				for (var nm in dh)
					zk.getDataHandler(nm)!.run(this, dh[nm]);
			};
			this.listen({onBind: this.z_isDataHandlerBound});
		}
		return out;
	}

	/**
	 * @returns the tooltiptext for generating the title attribute of the DOM element.
	 * @defaultValue return {@link getTooltiptext}.
	 * <p>Deriving class might override this method if the parent widget
	 * is not associated with any DOM element, such as treerow's parent: treeitem.
	 * @since 5.0.2
	 * @internal
	 */
	domTooltiptext_(): string | undefined {
		return this.getTooltiptext();
	}

	/**
	 * @returns the style attribute that contains only the text related CSS styles. For example, it returns style="font-size:12pt;font-weight:bold" if #getStyle is border:none;font-size:12pt;font-weight:bold.
	 * <p>It is usually used with {@link getTextNode} to
	 * <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component_Development/Client-side/Text_Styles_and_Inner_Tags">ZK Client-side Reference: Text Styles and Inner Tags</a>.
	 * @see {@link getTextNode}
	 * @internal
	 */
	domTextStyleAttr_(): string | undefined {
		const html = this.getStyle();
		return DOMPurify.sanitize(html ? zUtl.appendAttr('style', jq.filterTextStyle(html)) : (html ?? ''));
	}

	/** Replaces the specified DOM element with the HTML content generated this widget.
	 * It is the same as `jq(n).replaceWith(wgt, desktop, skipper)`.
	 * <p>The DOM element to be replaced can be {@link Widget.$n} or any independent DOM element. For example, you can replace a DIV element (and all its descendants) with this widget (and its descendants).
	 * <p>This method is usually used to replace a DOM element with a root widget (though, with care, it is OK for non-root widgets). Non-root widgets usually use {@link appendChild}
	 *  and {@link insertBefore} to attach to the DOM tree[1]
	 * <p>If the DOM element doesn't exist, you can use {@link jq.before} or {@link jq.after} instead.
	 * <p>Notice that, both {@link replaceHTML} fires the beforeSize and onSize watch events
	 * (refer to {@link zWatch}).
	 * <p>If skipper is null. It implies the caller has to fire these two events if it specifies a skipper
	 * (that is how {@link rerender} is implemented).
	 * <h3>Subclass Note</h3>
	 * This method actually forwards the invocation to its parent by invoking
	 * parent's {@link replaceChildHTML_} to really replace the DOM element.
	 * Thus, override {@link replaceChildHTML_} if you want to do something special for particular child widgets.
	 *
	 * @param n - the DOM element ({@link DOMElement}) or anything
	 * {@link Widget.$} allowed.
	 * @param desktop - the desktop that this widget shall belong to.
	 * If omitted, it is retrieve from the current desktop.
	 * If null, it is decided automatically ( such as the current value of {@link desktop} or the first desktop)
	 * @param skipper - it is used only if it is called by {@link rerender}
	 * @see {@link replaceWidget}
	 * @see {@link JQuery.replaceWith}
	 */
	replaceHTML(n: HTMLElement | string, desktop?: Desktop, skipper?: Skipper, _trim_?: boolean, _callback_?: CallableFunction[]): void {
		if (!desktop) {
			desktop = this.desktop;
			if (!zk.Desktop._ndt) zk.stateless();
		}

		var cfi = skipper ? undefined : _bkFocus(this),
			p = this.parent;
		if (p) p.replaceChildHTML_(this, n, desktop, skipper, _trim_);
		else {
			var oldwgt = this.getOldWidget_(n);
			if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
			else if (this.z_rod) this.get$Class<typeof Widget>()._unbindrod(this); //possible (if replace directly)
			jq(n).replaceWith(/*safe*/ this.redrawHTML_(skipper, _trim_));
			this.bind(desktop, skipper);
		}

		if (!skipper) {
			if (!Array.isArray(_callback_))
				zUtl.fireSized(this);
			else {
				// for Bug ZK-2271, we delay this calculation
				_callback_.push(() => {
					zUtl.fireSized(this);
				});
			}

		}

		_rsFocus(cfi);
	}

	/**
	 * @returns the widget associated with the given node element.
	 * It is used by {@link replaceHTML} and {@link replaceChildHTML_} to retrieve
	 * the widget associated with the note.
	 * <p>It is similar to {@link Widget.$} but it gives the widget a chance to
	 * handle extreme cases. For example, Treeitem doesn't associate a DOM element
	 * (or you can say Treeitem and Treerow shares the same DOM element), so
	 * `zk.Widget.$(n)` will return Treerow, not Treeitem.
	 * If it is the case, you can override it to make {@link replaceHTML}
	 * works correctly.
	 * @param n - the DOM element to match the widget.
	 * @since 5.0.3
	 * @internal
	 */
	getOldWidget_(n: HTMLElement | string): zk.Widget | undefined {
		return Widget.$(n, {strict: true});
	}

	/**
	 * @returns the HTML fragment of this widget.
	 * @param skipper - the skipper. Ignored if null
	 * @param trim - whether to trim the HTML content before replacing
	 * @internal
	 */
	redrawHTML_(skipper?: Skipper, trim?: boolean): string {
		var out = new zk.Buffer(); // Due to the side-effect of B65-ZK-1628, we remove the optimization of the array's join() for chrome.
		this.redraw(out, skipper);
		var html = /*safe*/ out.join('');
		return trim ? html.trim() : html;
		//To avoid the prolog being added repeatedly if keep invalidated:
		//<div><textbox/> <button label="Click!" onClick="self.invalidate()"/></div>
	}

	/**
	 * Re-renders the DOM element(s) of this widget.
	 * By re-rendering we mean to generate HTML again ({@link redraw})
	 * and then replace the DOM elements with the new generated HTML code snippet.
	 * <p>It is equivalent to replaceHTML(this.node, null, skipper).
	 * <p>It is usually used to implement a setter of this widget.
	 * For example, if a setter (such as `setBorder`) has to
	 * modify the visual appearance, it can update the DOM tree directly,
	 * or it can call this method to re-render all DOM elements associated
	 * with is widget and its descendants.
	 * <p>It is convenient to synchronize the widget's state with
	 * the DOM tree with this method. However, it shall be avoided
	 * if the HTML code snippet is complex (otherwise, the performance won't be good).
	 * <p>If re-rendering is required, you can improve the performance
	 * by passing an instance of {@link zk.Skipper} that is used to
	 * re-render some or all descendant widgets of this widget.
	 * @param skipper - skip some portion of this widget
	 * to speed up the re-rendering.
	 * If not specified, rerender(0) is assumed (since ZK 6).
	 */
	rerender(skipper?: Skipper): this
	/**
	 * Re-renders after the specified time (milliseconds).
	 * <p>Notice that, to have the best performance, we use the single timer
	 * to handle all pending rerenders for all widgets.
	 * In other words, if the previous timer is not expired (and called),
	 * the second call will reset the expiration time to the value given
	 * in the second call.
	 * @param timeout - the number milliseconds (non-negative) to wait
	 * before rerender. If negative, it means rerender shall take place
	 * immediately. If not specified, 0 is assumed (since ZK 6).
	 * @since 5.0.4
	 */
	rerender(timeout?: number): this
	// eslint-disable-next-line zk/tsdocValidation
	/** This overload is for internal use @internal */
	rerender(skipper?: Skipper | number): this
	rerender(skipper?: Skipper | number): this {
		if (this.desktop) {
			if (!skipper || (skipper as number) > 0) { //default: 0
				_rerender(this, typeof skipper === 'number' ? skipper : 0);
				return this;
			}
			if ((skipper as number) < 0)
				skipper = undefined; //negative -> immediately

			var n = this.$n();
			if (n) {
				try {
					this._rerendering = true;
					zk._avoidRod = true;
					//to avoid side effect since the caller might look for $n(xx)

					var skipInfo: HTMLElement | undefined;
					if (skipper instanceof Skipper) {
						skipInfo = skipper.skip(this);
						if (skipInfo) {
							var cfi = _bkFocus(this);

							this.replaceHTML(n, undefined, skipper, true);

							skipper.restore(this, skipInfo);

							zWatch.fireDown('onRestore', this);
							//to notify it is restored from rerender with skipper
							zUtl.fireSized(this);

							_rsFocus(cfi);
						}
					}

					if (!skipInfo)
						this.replaceHTML(n, undefined, undefined, true);
				} finally {
					delete zk._avoidRod;
					delete this._rerendering;
				}
			}
		}
		return this;
	}

	/** A function that postpones the invoke of rerender function until all the cmds from server are processed.
	 * This avoids rerendering twice or more. It works only in the setAttrs phase,
	 * otherwise rerender will be invoked immediately.
	 * @since 8.6.0
	 * @internal
	 */
	rerenderLater_(skipper?: Skipper): void {
		const processPhase = zAu.processPhase;
		if (processPhase == 'setAttr' || processPhase == 'setAttrs') {
			this.doAfterProcessRerenderArgs = arguments as never;
		} else {
			this.rerender(skipper);
		}
	}

	/** Replaces the DOM element(s) of the specified child widget.
	 * It is called by {@link replaceHTML} to give the parent a chance to
	 * do something special for particular child widgets.
	 * @param child - the child widget whose DOM content is used to replace the DOM tree
	 * @param n - the DOM element to be replaced
	 * @param dt - [optional the desktop that this widget shall belong to.
	 * If null, it is decided automatically ( such as the current value of {@link desktop} or the first desktop)
	 * @param skipper - it is used only if it is called by {@link rerender}
	 * @internal
	 */
	replaceChildHTML_(child: zk.Widget, n: HTMLElement | string, desktop?: Desktop, skipper?: Skipper, _trim_?: boolean): void {
		var oldwgt = child.getOldWidget_(n),
			skipInfo: HTMLElement | undefined;
		if (oldwgt) {
			oldwgt.unbind(skipper); //unbind first (w/o removal)
			skipInfo = skipper ? skipper.skip(oldwgt) : undefined;
		} else if (this.shallChildROD_(child))
			this.get$Class<typeof Widget>()._unbindrod(child); //possible (e.g., Errorbox: jq().replaceWith)

		jq(n).replaceWith(/*safe*/ child.redrawHTML_(skipper, _trim_));
		if (skipInfo) {
			skipper?.restore(child, skipInfo);
		}
		child.bind(desktop, skipper);
	}

	/** Inserts the HTML content generated by the specified child widget before the reference widget (the before argument).
	 * It is called by {@link insertBefore} and {@link appendChild} to handle the DOM tree.
	 * <p>Deriving classes might override this method to modify the HTML content, such as enclosing with TD.
	 * <p>Notice that when inserting the child (without the before argument), this method will call {@link getCaveNode} to find the location to place the DOM element of the child. More precisely, the node returned by {@link getCaveNode} is the parent DOM element of the child. The default implementation of {@link getCaveNode} is to look for a sub-node named uuid$cave. In other words, it tried to place the child inside the so-called cave sub-node, if any.
	 * Otherwise, {@link Widget.$n} is assumed.
	 * @param child - the child widget to insert
	 * @param before - the child widget as the reference to insert the new child before. If null, the HTML content will be appended as the last child.
	 * The implementation can use before.getFirstNode_() ({@link getFirstNode_}) to retrieve the DOM element
	 * @see {@link getCaveNode}
	 * @internal
	 */
	insertChildHTML_(child: zk.Widget, before?: zk.Widget, desktop?: Desktop): void {
		var ben: zk.Widget | undefined | HTMLElement, html = child.redrawHTML_(),
			before0: zk.Widget | HTMLElement | undefined = before;
		if (before0) {
			if (before0 instanceof Native) { //native.$n() is usually null
				ben = before0.previousSibling;
				if (ben) {
					if (ben == child) //always true (since link ready), but to be safe
						ben = ben.previousSibling;
					if (ben && (ben = ben.$n())) {
						jq(ben).after(/*safe*/ html);
						child.bind(desktop);
						return;
					}
				}
				//FUTURE: it is not correct to go here, but no better choice yet
			}
			before0 = (before0 as Widget).getFirstNode_();
		}
		if (!before0)
			for (let w: zk.Widget | undefined = this; ;) {
				ben = w.getCaveNode();
				if (ben) break;

				const w2 = w.nextSibling;
				if (w2 && (before0 = w2.getFirstNode_()))
					break;

				if (!(w = w.parent)) {
					ben = document.body;
					break;
				}
			}

		if (before0) {
			const sib = (before0).previousSibling;
			if (_isProlog(sib)) before0 = sib as HTMLElement;
			jq(before0).before(/*safe*/ html);
		} else {
			// fix for B70-ZK-2128.zul on client mvvm that the HeadWidget creates a
			// '-bar' element as the last child of the HeadWidget element, if the
			// upcoming added child is appended, the added child will be placed at
			// the end of the elements, which is a wrong position.
			if (this.lastChild === child && ben!.lastChild != null && !(ben!.lastChild as HTMLElement).id
						// Fix F80_ZK_327Test.java
					&& (ben!.lastChild as HTMLElement).nodeType == 1) {
				jq(ben!.lastChild).before(/*safe*/ html);
			} else {
				jq(ben as HTMLElement).append(/*safe*/ html);
			}
		}
		child.bind(desktop);
	}

	/**
	 * Called by {@link insertChildHTML_} to find the location to place the DOM element of the child.
	 * More precisely, the node returned by {@link getCaveNode} is the parent DOM element of the child's DOM element.
	 * @defaultValue `this.$n('cave') || this.$n()`
	 * You can override it to return whatever DOM element you want.
	 * @see {@link insertChildHTML_}
	 */
	getCaveNode(): HTMLElement | undefined {
		return this.$n('cave') || this.$n();
	}
	/**
	 * @returns the first DOM element of this widget.
	 * If this widget has no corresponding DOM element, this method will look
	 * for its siblings.
	 * <p>This method is designed to be used with {@link insertChildHTML_}
	 * for retrieving the DOM element of the `before` widget.
	 * @internal
	 */
	getFirstNode_(): HTMLElement | undefined {
		for (let w: zk.Widget | undefined = this; w; w = w.nextSibling) {
			const n = _getFirstNodeDown(w);
			if (n) return n;
		}
	}

	/** Removes the corresponding DOM content of the specified child.
	 * It is called by {@link removeChild} to remove the DOM content.
	 * <p>The default implementation of this method will invoke {@link removeHTML_}
	 * if the ignoreDom argument is false or not specified.
	 * <p>Overrides this method or {@link removeHTML_} if you have to
	 * remove DOM elements other than child's node (and the descendants).
	 * @param child - the child widget to remove
	 * @param ignoreDom - whether to remove the DOM element
	 * @internal
	 */
	removeChildHTML_(child: zk.Widget, ignoreDom?: boolean): void {
		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(child, cf))
			zk.currentFocus = undefined;

		var n = child.$n(), ary: HTMLElement[] = [];
		if (n) {
			var sib = n.previousSibling;
			if (child.prolog && _isProlog(sib))
				jq(sib as Node).remove();
		} else
			_prepareRemove(child, ary);

		child.unbind();

		if (!ignoreDom)
			child.removeHTML_(n || ary);
	}

	/**
	 * Removes the HTML DOM content.
	 * <p>The default implementation simply removes the DOM element passed in.
	 * <p>Overrides this method if you have to remove the related DOM elements.
	 * @since 5.0.1
	 * @param n - an array of {@link DOMElement} to remove.
	 * If this widget is associated with a DOM element ({@link Widget.$n} returns non-null),
	 * n is a single element array.
	 * If this widget is not associated with any DOM element, an array of
	 * child widget's DOM elements are returned.
	 * @internal
	 */
	removeHTML_(n: HTMLElement | HTMLElement[]): void {
		_rmDom(this, n);
		this.clearCache();
	}

	/**
	 * @returns the DOM element that this widget is bound to.
	 * It is null if it is not bound to the DOM tree, or it doesn't have the associated DOM node (for example, {@link zul.utl.Timer}).
	 * <p>Notice that {@link desktop} is always non-null if it is bound to the DOM tree.
	 * In additions, this method is much faster than invoking jq() (see {@link _global_.jq},
	 * since it caches the result (and clean up at the {@link unbind_}).
	 * ```ts
	 * var n = wgt.$n();
	 * ```
	 * @see #$n(String)
	 */
	$n(): TElement | undefined
	/**
	 * @returns the child element of the DOM element(s) that this widget is bound to.
	 * This method assumes the ID of the child element the concatenation of
	 * {@link uuid}, -, and subId. For example,
	 * ```ts
	 * var cave = wgt.$n('cave'); //the same as jq('#' + wgt.uuid + '-' + 'cave')[0]
	 * ```
	 * Like {@link Widget.$n}, this method caches the result so the performance is much better
	 * than invoking jq() directly.
	 * @param subId - the sub ID of the child element
	 * @see {@link Widget.$n}
	 */
	$n<T extends HTMLElement = HTMLElement>(subId?: string): T | undefined
	$n(subId?: string): TElement | undefined {
		if (subId) {
			let n = this._subnodes[subId];
			if (!n && this.desktop) {
				n = jq(this.uuid + '-' + subId, zk)[0];
				this._subnodes[subId] = n ? n : 'n/a';
			}
			return n == 'n/a' ? undefined : n as TElement;
		}
		let n = this._node;
		if (!n && this.desktop && !this._nodeSolved) {
			this._node = n = jq<string, TElement>(this.uuid, zk)[0];
			this._nodeSolved = true;
		}
		return n!;
	}

	/**
	 * @returns the DOM element that this widget is bound to. (Never null)
	 * @see {@link Widget.$n_}
	 * @since 10.0.0
	 * @internal
	 */
	$n_(): TElement
	/**
	 * @returns the child element of the DOM element(s) that this widget is bound to.
	 *  (Never null)
	 * @param subId - the sub ID of the child element
	 * @see {@link Widget.$n_}
	 * @since 10.0.0
	 * @internal
	 */
	$n_<T extends HTMLElement = HTMLElement>(subId: string | undefined): T
	/** @internal */
	$n_(subId?: string): HTMLElement {
		const n = this.$n(subId);
		if (n == null) {
			throw 'Node ' + (subId ? 'with ' + subId : '') + ' is not found!';
		}
		return n;
	}

	/**
	 * @returns the service instance from the current widget, if any.
	 * @since 8.0.0
	 */
	$service(): Service | undefined {
		let w: zk.Widget | undefined = this;
		for (; w; w = w.parent) {
			if (w['$ZKAUS$'])
				break;
		}
		if (w) {
			if (!w._$service)
				w._$service = new zk.Service(w, this);
			return w._$service;
		}
		return undefined;
	}

	$afterCommand(command: string, args?: unknown[]): void {
		var service = this.$service();
		if (service)
			service.$doAfterCommand(command, args);
	}

	/**
	 * @returns whether the widget has its own element bound to HTML DOM tree.
	 * @since 7.0.0
	 */
	isRealElement(): boolean {
		return true;
	}

	/**
	 * @returns whether the widget is in re-rendering phases.
	 * @since 10.0.0
	 * @internal
	 */
	inRerendering_(): boolean {
		// Fix a side-effect of https://github.com/zkoss/zk/commit/97407d42c9bebd412dc1e5632728f6c3546ce21d
		// for ZK-5028
		if (_rdque.includes(this)) return true;

		let p = this as zk.Widget | undefined;
		while (p) {
			if (p._rerendering) {
				return true;
			}
			p = p.parent;
		}
		return false;
	}

	/**
	 * @returns the sub zclass name that cache for this widget.
	 * It returns the zclass if the subclass is empty or null,
	 * since it caches the result (and clean up at the {@link setZclass}).
	 * ```ts
	 * var subzcls = wgt.$s('hover'); // z-xxx-hover will be return
	 * ```
	 * @see {@link getZclass}
	 * @since 7.0.0
	 */
	$s(subclass?: string): string {
		if (subclass) {
			var subcls = this._subzcls[subclass];
			if (!subcls) {
				subcls = /*safe*/ this._subzcls[subclass] = zUtl.encodeXML(this.getZclass()) + '-' + /*safe*/ subclass;
			}
			return subcls;
		}
		return this.getZclass();
	}

	/** Clears the cached nodes (by {@link Widget.$n}). */
	clearCache(): void {
		this._node = undefined;
		this._subnodes = {};
		this._nodeSolved = false;
	}

	/**
	 * Unbinds when in rod mode. (internal use for callback)
	 * @since 10.0.0
	 * @internal
	 */
	unbindRod_(): void {
		// for override
	}

	/**
	 * @returns the page that this widget belongs to, or null if there is
	 * no page available.
	 */
	getPage(): Page | undefined {
		var page: undefined | zk.Widget | Page, dt: undefined | zk.Desktop;
		for (page = this.parent; page; page = page.parent)
			if (page instanceof Page)
				return page;

		return (page = (dt = this.desktop!)._bpg) ?
			page as Page | undefined : (dt._bpg = new Body(dt));
	}

	/**
	 * @returns whether this widget is being bound to DOM.
	 * In other words, it returns true if {@link bind} is called
	 * against this widget or any of its ancestors.
	 * @since 5.0.8
	 */
	isBinding(): boolean {
		if (this.desktop)
			for (var w: zk.Widget | undefined = this; w; w = w.parent)
				if (w._binding)
					return true;
		return false;
	}

	/**
	 * Forces the delayed rerendering children or itself to do now.
	 * @param skipper - used if {@link rerender} is called with a non-null skipper.
	 * @since 7.0.2
	 * @internal
	 */
	rerenderNow_(skipper?: Skipper): void { // for Bug ZK-2281 and others life cycle issues when the dom of children of itself is undefined.
		_rerenderNow(this, skipper);
	}

	/**
	 * Recursively bind ancestors that are currently unbound (i.e., `desktop` is falsy). Used by ROD.
	 * Introduced by ZK-5368.
	 * @param desktop - the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param skipper - used if {@link rerender} is called with a non-null skipper.
	 * @since 9.6.4
	 */
	bindMissingAncestors(desktop?: Desktop, skipper?: Skipper): this {
		const { parent } = this;
		if (parent && !parent.desktop) {
			// `skipper` for this widget and its descendents shouldn't affect that of ancestors.
			parent.bind(desktop, skipper, true).bindMissingAncestors(desktop);
		}
		return this;
	}

	/**
	 * Binds this widget.
	 * It is called to associate (aka., attach) the widget with
	 * the DOM tree.
	 * <p>Notice that you rarely need to invoke this method, since
	 * it is called automatically (such as {@link replaceHTML}
	 * and {@link appendChild}).
	 * <p>Notice that you rarely need to override this method, either.
	 * Rather, override {@link bind_} instead.
	 *
	 * @see {@link bind_}
	 * @see {@link unbind}
	 * @param desktop - the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param skipper - used if {@link rerender} is called with a non-null skipper.
	 * @param bindSelfOnly - set to true if one doesn't want to recursively bind descendents.
	 */
	bind(desktop?: Desktop, skipper?: Skipper, bindSelfOnly?: boolean): this {
		this._binding = true;

		_rerenderDone(this, skipper); //cancel pending async rerender
		if (this.z_rod && !bindSelfOnly)
			this.get$Class<typeof Widget>()._bindrod(this);
		else {
			var after: CallableFunction[] = [], fn: CallableFunction | undefined;
			this.bind_(desktop, skipper, after, bindSelfOnly);
			while (fn = after.shift())
				fn();
		}

		delete this._binding;
		return this;
	}

	/**
	 * Unbinds this widget.
	 * It is called to remove the association (aka., detach) the widget from
	 * the DOM tree.
	 * <p>Notice that you rarely need to invoke this method, since
	 * it is called automatically (such as {@link replaceHTML}).
	 * <p>Notice that you rarely need to override this method, either.
	 * Rather, override {@link unbind_} instead.
	 *
	 * @see {@link unbind_}
	 * @see {@link bind}
	 * @param skipper - used if {@link rerender} is called with a non-null skipper.
	 * @param keepRod - used if the ROD flag needs to be kept.
	 */
	unbind(skipper?: Skipper, keepRod?: boolean): this {
		if (this._$service) {
			this._$service.destroy();
			this._$service = undefined;
		}
		_rerenderDone(this, skipper); //cancel pending async rerender
		if (this.z_rod)
			this.get$Class<typeof Widget>()._unbindrod(this, keepRod); // keepRod is "nest" here
		else {
			var after: (() => void)[] = [];
			this.unbind_(skipper, after, keepRod);
			for (var j = 0, len = after.length; j < len;)
				after[j++]();
		}
		return this;
	}

	/** Callback when this widget is bound (aka., attached) to the DOM tree.
	 * It is called after the DOM tree has been modified (with the DOM content of this widget, i.e., {@link redraw})
	 * (for example, by {@link replaceHTML}).
	 * <p>Note: don't invoke this method directly. Rather, invoke {@link bind} instead.
	 * ```ts
	 * wgt.bind();
	 * ```
	 * <h3>Subclass Note</h3>
	 * <p>Subclass overrides this method to initialize the DOM element(s), such as adding a DOM listener. Refer to Widget and DOM Events and {@link domListen_} for more information.
	 *
	 * @see {@link bind}
	 * @see {@link unbind_}
	 * @param desktop - the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param skipper - used if {@link rerender} is called with a non-null skipper.
	 * @param after - an array of function ({@link Function}) that will be invoked after {@link bind_} has been called. For example,
	 * ```ts
	 * bind_: function (desktop, skipper, after) {
	 *   this.$supers('bind_', arguments);
	 *   var self = this;
	 *   after.push(function () {
	 * 	   self._doAfterBind(something);
	 * 	   ...
	 *   });
	 * }
	 * ```
	 * @param bindSelfOnly - set to true if one doesn't want to recursively bind descendents.
	 * @internal
	 */
	bind_(desktop?: Desktop, skipper?: Skipper, after?: CallableFunction[], bindSelfOnly?: boolean): void {
		this.get$Class<typeof Widget>()._bind0(this);

		this.desktop = desktop ?? (desktop = zk.Desktop.$(this.parent));

		var p = this.parent, v;
		this.bindLevel = p ? p.bindLevel + 1 : 0;

		if ((v = this._draggable) && v != 'false' && !_dragCtl(this))
			this.initDrag_();

		if (this._nvflex || this._nhflex)
			_listenFlex(this);

		if (!bindSelfOnly)
			this.bindChildren_(desktop, skipper, after);
		var self = this;
		if (this.isListen('onBind')) {
			zk.afterMount(function () {
				if (self.desktop) //might be unbound
					self.fire('onBind');
			});
		}

		if (this.isListen('onAfterSize')) //Feature ZK-1672
			zWatch.listen({onSize: this});

		if ((zk.mobile || zk.touchEnabled) && after) {
			after.push(function () {
				setTimeout(function () {// lazy init
					self.bindSwipe_();
					self.bindDoubleTap_();
					self.bindTapHold_();
				}, 300);
			});
		}
	}

	/** Binds the children of this widget.
	 * It is called by {@link bind_} to invoke child's {@link bind_} one-by-one.
	 * @param desktop - the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param skipper - used if {@link rerender} is called with a non-null skipper.
	 * @param after - an array of function ({@link Function}) that will be invoked after {@link bind_} has been called. For example,
	 * @since 5.0.5
	 * @internal
	 */
	bindChildren_(desktop?: Desktop, skipper?: Skipper, after?: CallableFunction[]): void {
		for (var child: zk.Widget | undefined = this.firstChild, nxt: zk.Widget | undefined; child; child = nxt) {
			nxt = child.nextSibling;
			//we have to store first since RefWidget will replace widget

			if (!skipper || !skipper.skipped(this, child)) {
				if (child.z_rod) this.get$Class<typeof Widget>()._bindrod(child);
				else child.bind_(desktop, undefined, after); //don't pass skipper
			}
		}
	}

	/** Callback when a widget is unbound (aka., detached) from the DOM tree.
	 * It is called before the DOM element(s) of this widget is going to be removed from the DOM tree (such as {@link removeChild}.
	 * <p>Note: don't invoke this method directly. Rather, invoke {@link unbind} instead.
	 * <p>Note: after invoking `this.$supers('unbind_', arguments)`,
	 * the association with DOM elements are lost. Thus it is better to invoke
	 * it as the last statement.
	 * <p>Notice that {@link removeChild} removes DOM elements first, so
	 * {@link unbind_} is called before {@link beforeParentChanged_} and
	 * the modification of the widget tree. It means it is safe to access
	 * {@link parent} and other information here
	 * @see {@link bind_}
	 * @see {@link unbind}
	 * @param skipper - used if {@link rerender} is called with a non-null skipper
	 * @param after - an array of function ({@link Function})that will be invoked after {@link unbind_} has been called. For example,
	 * ```ts
	 * unbind_: function (skipper, after) {
	 *   var self = this;
	 *   after.push(function () {
	 * 	self._doAfterUnbind(something);
	 * 	...
	 *   }
	 *   this.$supers('unbind_', arguments);
	 * }
	 * ```
	 * @param keepRod - used if the ROD flag needs to be kept.
	 * @internal
	 */
	unbind_(skipper?: Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this.get$Class<typeof Widget>()._unbind0(this);
		_unlistenFlex(this);

		this.unbindChildren_(skipper, after, keepRod);
		this.cleanDrag_(); //ok to invoke even if not init
		this.unbindSwipe_();
		this.unbindDoubleTap_();
		this.unbindTapHold_();

		if (this.isListen('onAfterSize')) //Feature ZK-1672
			zWatch.unlisten({onSize: this});

		if (this.isListen('onUnbind')) {
			var self = this;
			zk.afterMount(function () {
				if (!self.desktop) //might be bound
					self.fire('onUnbind');
			});
		}

		for (var nm in this.effects_) {
			const ef = this.effects_[nm];
			if (ef) {
				ef.destroy();
			}
		}
		this.effects_ = {};
	}

	/** Unbinds the children of this widget.
	 * It is called by {@link unbind_} to invoke child's {@link unbind_} one-by-one.
	 * @param skipper - used if {@link rerender} is called with a non-null skipper
	 * @param after - an array of function ({@link Function})that will be invoked after {@link unbind_} has been called. For example,
	 * @param keepRod - used if the ROD flag needs to be kept.
	 * @since 5.0.5
	 * @internal
	 */
	unbindChildren_(skipper?: Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		for (var child: zk.Widget | undefined = this.firstChild, nxt: zk.Widget | undefined; child; child = nxt) {
			nxt = child.nextSibling; //just in case

			// check child's desktop for bug 3035079: Dom elem isn't exist when parent do appendChild and rerender
			if (!skipper || !skipper.skipped(this, child)) {
				if (child.z_rod) this.get$Class<typeof Widget>()._unbindrod(child, keepRod);
				else if (child.desktop) {
					child.unbind_(undefined, after, keepRod); //don't pass skipper
					//Bug ZK-1596: native will be transfer to stub in EE, store the widget for used in mount.js
					if ((child instanceof zk.Native))
						zAu._storeStub(child);
				}
			}
		}
	}

	/** Associates UUID with this widget.
	 * <p>Notice that {@link uuid} is automatically associated (aka., bound) to this widget.
	 * Thus, you rarely need to invoke this method unless you want to associate with other identifiers.
	 * <p>For example, ZK Google Maps uses this method since it has to
	 * bind the anchors manually.
	 *
	 * @param uuid - the UUID to assign to the widgtet
	 * @param add - whether to bind. Specify true if you want to bind;
	 * false if you want to unbind.
	 * @internal
	 */
	extraBind_(uuid: string, add: boolean): void {
		if (!add) delete _binds[uuid];
		else _binds[uuid] = this;
	}

	/** @internal */
	setFlexSize_(flexSize: zk.FlexSize, isFlexMin?: boolean): void {
		if (this._cssflex && this.parent && this.parent.getFlexContainer_() != null && !isFlexMin)
			return;
		var n = this.$n()!,
			zkn = zk(n);
		if (flexSize.height !== undefined) {
			if (flexSize.height == 'auto')
				n.style.height = '';
			else if (flexSize.height != '' || (typeof flexSize.height === 'number' && flexSize.height === 0 && !this.isFloating_())) //bug #2943174, #2979776, ZK-1159, ZK-1358
				this.setFlexSizeH_(n, zkn, flexSize.height as number, isFlexMin);
			else
				n.style.height = this._height || '';
		}
		if (flexSize.width !== undefined) {
			if (flexSize.width == 'auto')
				n.style.width = '';
			else if (flexSize.width != '' || (typeof flexSize.width === 'number' && flexSize.width === 0 && !this.isFloating_())) //bug #2943174, #2979776, ZK-1159, ZK-1358
				this.setFlexSizeW_(n, zkn, flexSize.width as number, isFlexMin);
			else
				n.style.width = this._width || '';
		}
	}


	// eslint-disable-next-line zk/javaStyleSetterSignature
	/** @internal */
	setFlexSizeH_(flexSizeH: HTMLElement, zkn: zk.JQZK, height: number, isFlexMin?: boolean): void {
		// excluding margin for F50-3000873.zul and B50-3285635.zul
		flexSizeH.style.height = jq.px0(height - (!isFlexMin ? zkn.marginHeight() : 0));
	}

	// eslint-disable-next-line zk/javaStyleSetterSignature
	/** @internal */
	setFlexSizeW_(flexSizeW: HTMLElement, zkn: zk.JQZK, width: number, isFlexMin?: boolean): void {
		// excluding margin for F50-3000873.zul and B50-3285635.zul
		flexSizeW.style.width = jq.px0(width - (!isFlexMin ? zkn.marginWidth() : 0));
	}

	// ZK-5050
	/** @internal */
	beforeChildReplaced_(oldc: zk.Widget, newc: zk.Widget): void {
		//to be overridden
	}

	/** @internal */
	beforeChildrenFlex_(kid: zk.Widget): boolean {
		//to be overridden
		return true; //return true to continue children flex fixing
	}

	/** @internal */
	afterChildrenFlex_(kid?: zk.Widget): void {
		//to be overridden
	}

	// @since 7.0.1
	/** @internal */
	afterChildMinFlexChanged_(kid: zk.Widget, attr: zk.FlexOrient): void { //attr 'w' for width or 'h' for height
		//to be overridden, after each of my children fix the minimum flex (both width and height),
		// only if when myself is not in min flex.
	}

	/** @internal */
	ignoreFlexSize_(attr: zk.FlexOrient): boolean { //'w' for width or 'h' for height calculation
		//to be overridden, whether ignore widget dimension in vflex/hflex calculation
		return false;
	}

	/** @internal */
	ignoreChildNodeOffset_(attr: zk.FlexOrient): boolean { //'w' for width or 'h' for height calculation
		//to be overridden, whether ignore child node offset in vflex/hflex calculation
		return false;
	}

	/** @internal */
	beforeMinFlex_(attr: zk.FlexOrient): number | undefined { //'w' for width or 'h' for height
		//to be overridden, before calculate my minimum flex
		return undefined;
	}

	/** @internal */
	beforeParentMinFlex_(attr: zk.FlexOrient): void { //'w' for width or 'h' for height
		//to be overridden, before my minimum flex parent ask my natural(not minimized) width/height
	}

	/** @internal */
	afterChildrenMinFlex_(orient: zk.FlexOrient): void {
		//to be overridden, after my children fix the minimum flex (both width and height)
	}

	/** @internal */
	afterResetChildSize_(attr: string): void {
		//to be overridden, after my children reset the size of (both width and height)
	}

	/** @internal */
	isExcludedHflex_(): boolean {
		return jq(this.$n()).css('position') == 'absolute'; // B60-ZK-917
		//to be overridden, if the widget is excluded for hflex calculation.
	}

	/** @internal */
	isExcludedVflex_(): boolean {
		return jq(this.$n()).css('position') == 'absolute'; // B60-ZK-917
		//to be overridden, if the widget is excluded for vflex calculation.
	}

	// to overridden this method have to fix the IE9 issue (ZK-483)
	// you can just add 1 px more for the offsetWidth
	/** @internal */
	getChildMinSize_(attr: zk.FlexOrient, wgt: zk.Widget): number { //'w' for width or 'h' for height
		if (attr == 'w') {
			return zjq.minWidth(wgt);
		} else {
			return zk(wgt).offsetHeight();//See also bug ZK-483
		}
	}

	// for v/hflex, if the box-sizing is in border-box mode (like ZK 7+),
	// we should return the content size only (excluding padding and border)
	/** @internal */
	getParentSize_(p: HTMLElement): {width: number; height: number} { //to be overridden
		var zkp = zk(p);
		return {height: zkp.contentHeight(), width: zkp.contentWidth()};
	}

	/** @internal */
	getMarginSize_(attr: zk.FlexOrient): number { //'w' for width or 'h' for height
		return zk(this).sumStyles(attr == 'h' ? 'tb' : 'lr', jq.margins);
	}

	/** @internal */
	getContentEdgeHeight_(height: number/*current calculated height*/): number {
		var p = this.$n(),
			body = document.body,
			fc = this.firstChild;

		// ZK-1524: Caption should be ignored
		fc = fc && zk.isLoaded('zul.wgt') && (fc instanceof window.zul.wgt.Caption) ? fc.nextSibling : fc;

		// ZK-2248: skip widget which does not have dimension
		while (fc && fc.ignoreFlexSize_('h'))
			fc = fc.nextSibling;

		var c = (fc && fc.$n()) || p?.firstElementChild,
			zkp = zk(p),
			h = zkp.padBorderHeight();

		if (c) {
			c = c.parentElement;
			while (c && p != c && c != body) {
				var zkc = zk(c);
				h += zkc.padBorderHeight() + zkc.sumStyles('tb', jq.margins);
				c = c.parentElement;
			}
			return h;
		}
		return h;
	}

	/** @internal */
	getContentEdgeWidth_(width: number/*current calculated width*/): number {
		var p = this.$n(),
			body = document.body,
			fc = this.firstChild,
			// ZK-1524: Caption should be ignored
			fc = fc && zk.isLoaded('zul.wgt') && (fc instanceof window.zul.wgt.Caption) ? fc.nextSibling : fc;

		// ZK-2248: skip widget which does not have dimension
		while (fc && fc.ignoreFlexSize_('w'))
			fc = fc.nextSibling;

		var c = (fc && fc.$n()) || p?.firstElementChild,
			zkp = zk(p),
			w = zkp.padBorderWidth();

		if (c) {
			c = c.parentElement;
			while (c && p != c && c != body) {
				var zkc = zk(c);
				w += zkc.padBorderWidth() + zkc.sumStyles('lr', jq.margins);
				c = c.parentElement;
			}
			return w;
		}
		return w;
	}

	/** @internal */
	fixFlex_(): void {
		zFlex.fixFlex(this);
	}

	/** @internal */
	fixMinFlex_(n: HTMLElement, orient: zk.FlexOrient): number { //internal use
		return zFlex.fixMinFlex(this, n, orient);
	}

	/** @internal */
	clearCachedSize_(): void {
		delete this._hflexsz;
		delete this._vflexsz;
	}

	/** @internal */
	resetSize_(orient: zk.FlexOrient): void {
		var n = this.$n()!,
			hasScroll = this._beforeSizeHasScroll;
		if (hasScroll || (hasScroll == null && (n.scrollTop || n.scrollLeft))) // keep the scroll status, the issue also happens (not only IE8) if trigger by resize browser window.
			return;// do nothing Bug ZK-1885: scrollable div (with vflex) and tooltip
		n.style[orient == 'w' ? 'width' : 'height'] = '';
	}

	/** @internal */
	getFlexContainer_(): HTMLElement | undefined {
		return this.getCaveNode();
	}

	/** @internal */
	getFlexDirection_(): string | undefined { // if it is null, by default it would check this display is block or not
		//to be overridden
		return undefined;
	}

	/** @internal */
	afterClearFlex_(): void {
		//to be overridden
	}

	/** Initializes the widget to make it draggable.
	 * It is called if {@link getDraggable} is set (and bound).
	 * <p>You rarely need to override this method, unless you want to handle drag-and-drop differently.
	 * @defaultValue use {@link zk.Draggable} to implement drag-and-drop,
	 * and the handle to drag is the element returned by {@link getDragNode}
	 * @see {@link cleanDrag_}
	 * @internal
	 */
	initDrag_(): void {
		var n = this.getDragNode();
		if (n) { //ZK-1686: should check if DragNode exist
			this._drag = new zk.Draggable(this, n, this.getDragOptions_(_dragoptions));
		}
	}

	/**
	 * Cleans up the widget to make it un-draggable. It is called if {@link getDraggable}
	 * is cleaned (or unbound).
	 * <p>You rarely need to override this method, unless you want to handle drag-and-drop differently.
	 * @see {@link cleanDrag_}
	 * @internal
	 */
	cleanDrag_(): void {
		var drag = this._drag;
		if (drag) {
			this._drag = undefined;
			drag.destroy();
		}
	}

	/**
	 * @returns the DOM element of this widget that can be dragged.
	 * @defaultValue it returns {@link Widget.$n}, i.e., the user can drag the widget anywhere.
	 * @see {@link ignoreDrag_}
	 */
	getDragNode(): HTMLElement {
		return this.$n()!;
	}

	/**
	 * @returns the options used to instantiate {@link zk.Draggable}.
	 * @defaultValue it does nothing but returns the `map` parameter,
	 * i.e., the default options.
	 * <p>Though rarely used, you can override any option passed to
	 * {@link zk.Draggable}, such as the start effect, ghosting and so on.
	 * @param map - the default implementation
	 * @internal
	 */
	getDragOptions_(map: zk.DraggableOptions): zk.DraggableOptions {
		return map;
	}

	/**
	 * @returns if the location that an user is trying to drag is allowed, i.e., whether to ignore.
	 * @defaultValue it always returns false.
	 * If the location that an user can drag is static, override {@link getDragNode},
	 * which is easier to implement.
	 * @internal
	 */
	ignoreDrag_(pt: zk.Offset, evt?: zk.Event, drag?: zk.Draggable): boolean {
		return false;
	}

	/**
	 * @returns the widget if it allows to drop the specified widget (being dragged), or null if not allowed. It is called when the user is dragging a widget on top a widget.
	 * @defaultValue it check if the values of droppable and draggable match. It will check the parent ({@link parent}), parent's parent, and so on until matched, or none of them are matched.
	 * <p>Notice that the widget to test if droppable might be the same as the widget being dragged (i.e., this == dragged). By default, we consider them as non-matched.
	 * @param dragged - - the widget being dragged (never null).
	 * @internal
	 */
	getDrop_(dragged: zk.Widget): zk.Widget | undefined {
		if (this == dragged) {
			return undefined; //non-matched if the same target. Bug for ZK-1565
		} else {
			var dropType = this._droppable,
				dragType = dragged._draggable;
			if (dropType == 'true') return this;
			if (dropType && dragType != 'true') {
				if (this._dropTypes) {
					for (var dropTypes = this._dropTypes, j = dropTypes.length; j--;)
						if (dragType == dropTypes[j])
							return this;
				}
			}
		}
		return this.parent ? this.parent.getDrop_(dragged) : undefined;
	}

	/** Called to have some visual effect when the user is dragging a widget over this widget and this widget is droppable.
	 * Notice it is the effect to indicate a widget is droppable.
	 * @defaultValue it adds the CSS class named 'z-drag-over' if over is true, and remove it if over is false.
	 * @param over - whether the user is dragging over (or out, if false)
	 * @internal
	 */
	dropEffect_(over?: boolean): void {
		jq(this.$n() || [])[over ? 'addClass' : 'removeClass']('z-drag-over');
	}

	/**
	 * @returns the message to show when an user is dragging this widget, or null if it prefers to clone the widget with {@link cloneDrag_}.
	 * @defaultValue it return the inner text if if {@link Widget.$n} returns a TR, TD, or TH element. Otherwise, it returns null and {@link cloneDrag_} will be called to create a DOM element to indicate dragging.
	 * <p>Notice that the text would be encoded for XSS issue since 8.0.4.2. It should be considered when overriding.
	 * @internal
	 */
	getDragMessage_(): string | undefined {
		if (jq.nodeName(this.getDragNode(), 'tr', 'td', 'th')) {
			var n = this.$n('real') || this.getCaveNode(),
				msg = n ? n.innerText || '' : '';
			return msg ? zUtl.encodeXML(msg) : msg;
		}
	}

	/** Called to fire the onDrop event.
	 * You could override it to implement some effects to indicate dropping.
	 * @defaultValue it fires the onDrop event (with {@link fire}).
	 * The subclass can override this method to pass more options such as the coordination where a widget is dropped.
	 * @param drag - the draggable controller
	 * @param evt - the event causes the drop
	 * @internal
	 */
	onDrop_(drag: zk.Draggable, evt: zk.Event): void {
		var data = zk.copy({dragged: drag.control}, evt.data);
		this.fire('onDrop', data, undefined, Widget.auDelay);
	}

	/**
	 * Called to create the visual effect representing what is being dragged.
	 * In other words, it creates the DOM element that will be moved with the mouse pointer when the user is dragging.
	 * <p>This method is called if {@link getDragMessage_} returns null.
	 * If {@link getDragMessage_} returns a string (empty or not),
	 * a small popup containing the message is created to represent the widget being dragged.
	 * <p>You rarely need to override this method, unless you want a different visual effect.
	 * @see {@link uncloneDrag_}
	 * @param drag - the draggable controller
	 * @param ofs - the offset of the returned element (left/top)
	 * @internal
	 */
	cloneDrag_(drag: zk.Draggable, ofs: zk.Offset): HTMLElement {
		//See also bug 1783363 and 1766244

		var msg = this.getDragMessage_();
		if (typeof msg == 'string' && msg.length > 9)
			msg = msg.substring(0, 9) + '...';

		var dgelm = zk.DnD.ghost(drag, ofs, msg);

		drag._orgcursor = document.body.style.cursor;
		document.body.style.cursor = 'pointer';
		jq(this.getDragNode()).addClass('z-dragged'); //after clone
		return dgelm;
	}

	/** Undo the visual effect created by {@link cloneDrag_}.
	 * @param drag - the draggable controller
	 * @internal
	 */
	uncloneDrag_(drag: zk.Draggable): void {
		document.body.style.cursor = drag._orgcursor || '';

		jq(this.getDragNode()).removeClass('z-dragged');
	}

	//Feature ZK-1672: provide empty onSize function if the widget is listened to onAfterSize
	//	but the widget is never listened to onSize event
	// The parameter `ctl` is required by zul.wnd.Panel but no one else.
	onSize(ctl?: zk.ZWatchController/*For zul.wnd.Panel only*/): void {
		return;
	}

	/**
	 * Called to fire the onAfterSize event.
	 * @since 6.5.2
	 */
	onAfterSize(): void {
		if (this.desktop && this.isListen('onAfterSize')) {
			var n = this.$n()!, // ZK-5089: don't use "this.getCaveNode()" here
				width = n.offsetWidth,
				height = n.offsetHeight;
			if (this._preWidth != width || this._preHeight != height) {
				this._preWidth = width;
				this._preHeight = height;
				this.fire('onAfterSize', {width: width, height: height});
			}
		}
	}

	/**
	 * Bind swipe event to the widget on tablet device.
	 * It is called if HTML 5 data attribute (data-swipeable) is set to true.
	 * <p>You rarely need to override this method, unless you want to bind swipe behavior differently.
	 * @defaultValue use {@link zk.Swipe} to implement swipe event.
	 * @see {@link doSwipe_}
	 * @since 6.5.0
	 * @internal
	 */
	bindSwipe_(): void { return; }
	/**
	 * Unbind swipe event to the widget on tablet device.
	 * It is called if swipe event is unbound.
	 * <p>You rarely need to override this method, unless you want to unbind swipe event differently.
	 * @see {@link doSwipe_}
	 * @since 6.5.0
	 * @internal
	 */
	unbindSwipe_(): void { return; }

	/**
	 * Bind double click event to the widget on tablet device.
	 * It is called if the widget is listen to onDoubleClick event.
	 * <p>You rarely need to override this method, unless you want to implement double click behavior differently.
	 * @see {@link doDoubleClick_}
	 * @since 6.5.0
	 * @internal
	 */
	bindDoubleTap_(): void { return; }

	/**
	 * Unbind double click event to the widget on tablet device.
	 * It is called if the widget is listen to onDoubleClick event.
	 * <p>You rarely need to override this method, unless you want to implement double click behavior differently.
	 * @see {@link doDoubleClick_}
	 * @since 6.5.0
	 * @internal
	 */
	unbindDoubleTap_(): void { return; }

	/**
	 * Bind right click event to the widget on tablet device.
	 * It is called if the widget is listen to onRightClick event.
	 * <p>You rarely need to override this method, unless you want to implement right click behavior differently.
	 * @see {@link doRightClick_}
	 * @since 6.5.1
	 * @internal
	 */
	bindTapHold_(): void { return; }

	/**
	 * Unbind right click event to the widget on tablet device.
	 * It is called if the widget is listen to onRightClick event.
	 * <p>You rarely need to override this method, unless you want to implement right click behavior differently.
	 * @see {@link doRightClick_}
	 * @since 6.5.1
	 * @internal
	 */
	unbindTapHold_(): void { return; }

	/**
	 * Sets the focus to this widget.
	 * This method will check if this widget can be activated by invoking {@link canActivate} first.
	 * <p>Notice: don't override this method. Rather, override {@link focus_},
	 * which this method depends on.
	 * @param timeout - how many milliseconds before changing the focus. If not specified or negative, the focus is changed immediately,
	 * @returns whether the focus is gained to this widget.
	 */
	focus(timeout?: number): boolean {
		return this.canActivate({checkOnly: true})
			&& zk(this.$n()).isRealVisible()
			&& this.focus_(timeout);
	}

	/**
	 * Called by {@link focus} to set the focus.
	 * @defaultValue call child widget's focus until it returns true, or no child at all.
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If a widget is able to gain focus, it shall override this method to invoke {@link _global_.jqzk#focus}.</li>
	 * <li>It is called only if the DOM element is real visible (so you don't need to check again)</li>
	 * </ul>
	 * ```ts
	 * focus_: function (timeout) {
	 *   zk(this.$n('foo').focus(timeout);
	 *   return true;
	 * }
	 * ```
	 * @param timeout - how many milliseconds before changing the focus. If not specified or negative, the focus is changed immediately,
	 * @returns whether the focus is gained to this widget.
	 * @since 5.0.5
	 * @internal
	 */
	focus_(timeout?: number): boolean {
		if (zk(this.$n()).focus(timeout)) {
			this.setTopmost();
			return true;
		}
		for (var w = this.firstChild; w; w = w.nextSibling) {
			//B65-ZK-2035: make sure the DOM element of child is real visible
			if (w.isRealVisible() && w.focus_(timeout))
				return true;
		}
		return false;
	}

	/**
	 * Checks if this widget can be activated (gaining focus and so on).
	 * @defaultValue return false if it is not a descendant of
	 * {@link _global_.zk#currentModal}.
	 * @param opts - the options. Allowed values:
	 * <ul>
	 * <li>checkOnly: not to change focus back to modal dialog if unable to
	 * activate. If not specified, the focus will be changed back to
	 * {@link _global_.zk#currentModal}.
	 * In additions, if specified, it will ignore {@link zk#busy}, which is set
	 * if {@link zk.AuCmd0#showBusy} is called.
	 * This flag is usually set by {@link focus}, and not set
	 * if it is caused by user's activity, such as clicking.</li>
	 * </ul>
	 * The reason to ignore busy is that we allow application to change focus
	 * even if busy, while the user cannot.
	 */
	canActivate(opts?: Partial<{ checkOnly: boolean }>): boolean {
		if (_ignCanActivate)
			return true;
		if (zk.busy && (!opts || !opts.checkOnly)) { //Bug 2912533: none of widget can be activated if busy
			jq.focusOut(); // Bug 2968706
			return false;
		}

		var modal = zk.currentModal;
		if (modal && !zUtl.isAncestor(modal, this)
			&& !jq.isAncestor(modal.$n(), this.$n())) { //ZK-393: this might be included
			var wgt = this.getTopWidget();

			// Bug #3201879
			if (wgt && wgt != modal && wgt.getZIndex() > modal.getZIndex())
				return true;

			if (!opts || !opts.checkOnly) {
				var cf = zk.currentFocus;
				//Note: browser might change focus later, so delay a bit
				if (cf && zUtl.isAncestor(modal, cf) && cf.focus(0)) {
					return true;
				} else {
					modal.focus(0);
				}
			}
			return false;
		}
		return true;
	}

	//server comm//
	/**
	 * Smart-updates a property of the peer component associated with this widget, running at the server, with the specified value.
	 * <p>It is actually fired an AU request named `setAttr`, and
	 * it is handled by the `updateByClient` method in `org.zkoss.zk.ui.AbstractComponent` (at the server).
	 * <p>By default, it is controlled by a component attribute called `org.zkoss.zk.ui.updateByClient`.
	 * And, it is default to false.
	 * Thus, the component developer has to override `updateByClient` at
	 * the server (in Java) and then update it rather than calling back superclass.
	 * For example,
	 * ```ts
	 * void updateByClient(String name, Object value) {
	 *   if ("disabled".equals(name))
	 *     setDisabled(value instanceof Boolean && ((Boolean)value).booleanValue());
	 *   else
	 *     super.updateByClient(name, value);
	 * }
	 * ```
	 *
	 * @param name - the property name
	 * @param value - the property value
	 * @param timeout - the delay before sending out the AU request. It is optional. If omitted, -1 is assumed (i.e., it will be sent with next non-deferrable request).
	 * @see {@link zAu.send}
	 */
	smartUpdate(name: string, value: unknown, timeout?: number): void {
		zAu.send(new zk.Event(this, 'setAttr', [name, value]),
			(timeout !== undefined && timeout >= 0) ? timeout : -1);
	}

	//widget event//
	/**
	 * Fire a widget event.
	 * @param evt - the event to fire
	 * @param timeout - the delay before sending the non-deferrable AU request (if necessary).
	 * If not specified or negative, it is decided automatically.
	 * It is ignored if no non-deferrable listener is registered at the server.
	 * @returns the event being fired, i.e., evt.
	 * @see {@link fire}
	 * @see {@link listen}
	 */
	fireX(evt: zk.Event, timeout?: number): zk.Event {
		var oldtg = evt.currentTarget;
		evt.currentTarget = this;
		try {
			var evtnm = evt.name,
				lsns = this._lsns[evtnm],
				len = lsns ? lsns.length : 0;
			if (len) {
				for (var j = 0; j < len;) {
					var inf = lsns[j++], o = inf[0] as zk.Widget;
					(inf[1] as CallableFunction || o[evtnm]).bind(o)(evt);
					if (evt.stopped) return evt; //no more processing
				}
			}

			if (!evt.auStopped) {
				const toServer = evt.opts && evt.opts.toServer;
				if (toServer || (this.inServer && this.desktop)) {
					let asap = toServer || this._asaps[evtnm];
					if (asap == null) {
						const ime = this.get$Class<typeof Widget>()._importantEvts;
						if (ime) {
							const ime0 = ime[evtnm];
							if (ime0 != null)
								asap = ime0;
						}
					}
					if (asap != null //true or false
						|| evt.opts.sendAhead)
						this.sendAU_(evt,
							asap ? ((timeout !== undefined && timeout >= 0) ? timeout : zk.Widget.auDelay) : -1);
				}
			}
			return evt;
		} finally {
			evt.currentTarget = oldtg;
		}
	}

	/** Callback before sending an AU request.
	 * It is called by {@link sendAU_}.
	 * @defaultValue this method will stop the event propagation
	 * and prevent the browser's default handling
	 * (by calling {@link zk.Event#stop}),
	 * if the event is onClick, onRightClick or onDoubleClick.
	 * <p>Notice that {@link sendAU_} is called against the widget sending the AU request
	 * to the server, while {@link beforeSendAU_} is called against the event's
	 * target (evt.target).
	 *
	 * <p>Notice that since this method will stop the event propagation for onClick,
	 * onRightClick and onDoubleClick, it means the event propagation is stopped
	 * if the server registers a listener. However, it doesn't stop if
	 * only a client listener is registered (and, in this case, {@link zk.Event#stop}
	 * must be called explicitly if you want to stop).
	 *
	 * @param wgt - the widget that causes the AU request to be sent.
	 * It will be the target widget when the server receives the event.
	 * @param evt - the event to be sent back to the server.
	 * Its content will be cloned to the AU request.
	 * @see {@link sendAU_}
	 * @since 5.0.2
	 * @internal
	 */
	beforeSendAU_(wgt: zk.Widget, evt: zk.Event): void {
		var en = evt.name;
		if (en == 'onClick' || en == 'onRightClick' || en == 'onDoubleClick')
			evt.shallStop = true;//Bug: 2975748: popup won't work when component with onClick handler
	}

	/** Sends an AU request to the server.
	 * It is invoked when {@link fire} will send an AU request to the server.
	 *
	 * <p>Override Notice: {@link sendAU_} will call evt.target's
	 * {@link beforeSendAU_} to give the original target a chance to
	 * process it.
	 *
	 * @param evt - the event that will be sent to the server.
	 * @param timeout - the delay before really sending out the AU request
	 * @see {@link fire}
	 * @see {@link beforeSendAU_}
	 * @see zAu#sendAhead
	 * @since 5.0.1
	 * @internal
	 */
	sendAU_(evt: zk.Event, timeout: number): void {
		(evt.target || this).beforeSendAU_(this, evt);
		evt = new zk.Event(this, evt.name, evt.data, evt.opts, evt.domEvent);
		//since evt will be used later, we have to make a copy and use this as target
		if (evt.opts.sendAhead) zAu.sendAhead(evt, timeout);
		else zAu.send(evt, timeout);
	}

	/**
	 * Check whether to ignore the click which might be caused by
	 * {@link doClick_}
	 * {@link doRightClick_}, or {@link doDoubleClick_}.
	 * @defaultValue return false.
	 * <p>Deriving class might override this method to return true if
	 * it wants to ignore the click on certain DOM elements, such as
	 * the open icon of a treerow.
	 * <p>Notice: if true is returned, {@link doClick_}
	 * {@link doRightClick_}, and {@link doDoubleClick_} won't be called.
	 * In additions, the popup and context of {@link zul.Widget} won't be
	 * handled, either.
	 * @param the - event that causes the click ({@link doClick_}
	 * {@link doRightClick_}, or {@link doDoubleClick_}).
	 * @returns whether to ignore it
	 * @since 5.0.1
	 * @internal
	 */
	shallIgnoreClick_(evt: zk.Event): boolean {
		return false;
	}

	/**
	 * Fire a widget event. An instance of {@link zk.Event} is created to represent the event.
	 *
	 * <p>The event listeners for this event will be called one-by-one unless {@link zk.Event#stop} is called.
	 *
	 * <p>If the event propagation is not stopped (i.e., {@link zk.Event#stop} not called)
	 * and {@link inServer} is true, the event will be converted to an AU request and sent to the server.
	 * Refer to <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Communication/AU_Requests/Client-side_Firing">ZK Client-side Reference: AU Requests: Client-side Firing</a> for more information.
	 * @param evtnm - the event name, such as onClick
	 * @param data - the data depending on the event ({@link zk.Event}).
	 * @param opts - the options. Refer to {@link zk.Event#opts}
	 * @param timeout - the delay before sending the non-deferrable AU request (if necessary).
	 * If not specified or negative, it is decided automatically.
	 * It is ignored if no non-deferrable listener is registered at the server.
	 * @returns the event being fired.
	 * @see {@link fire}
	 * @see {@link listen}
	 */
	fire(evtnm: string, data?: unknown, opts?: zk.EventOptions, timeout?: number): zk.Event {
		return this.fireX(new zk.Event(this, evtnm, data, opts), timeout);
	}

	/**
	 * Registers listener(s) to the specified event. For example,
	 * ```ts
	 * wgt.listen({
	 *   onClick: wgt,
	 *   onOpen: wgt._onOpen,
	 *   onMove: [o, o._onMove]
	 * });
	 * ```
	 * <p>As shown above, you can register multiple listeners at the same time, and echo value in infos can be a target, a function, or a two-element array, where the first element is a target and the second the function.
	 * A target can be any object that this will reference to when the event listener is called.
	 * Notice it is not {@link zk.Event#target}. Rather, it is `this` when the listener is called.
	 * <p>If the function is not specified, the target must have a method having the same name as the event. For example, if `wgt.listen({onChange: target})` was called, then target.onChange(evt) will be called when onChange event is fired (by {@link fire}). On the other hand, if the target is not specified, the widget is assumed to be the target.
	 * @param infos - a map of event listeners.
	 * Each key is the event name, and each value can be the target, the listener function, or a two-element array, where the first element is the target and the second the listener function.
	 * Notice that the target is not {@link zk.Event#target}. Rather, it is `this` when the listener is called.
	 * @param priority - the higher the number, the earlier it is called. If omitted, 0 is assumed.
	 * If a widget needs to register a listener as the default behavior (such as zul.wnd.Window's onClose), -1000 is suggested
	 * @see {@link unlisten}
	 * @see {@link fire}
	 * @see {@link fireX}
	 * @see {@link setListeners}
	 * @see {@link setListener}
	 */
	listen(infs: Record<string, unknown>, priority?: number): this {
		priority = priority ? priority : 0;
		for (var evt in infs) {
			var inf = infs[evt];
			if (Array.isArray(inf)) inf = [inf[0] || this, inf[1]];
			else if (typeof inf == 'function') inf = [this, inf];
			else inf = [inf || this, undefined];
			(inf as {priority: number}).priority = priority;

			var lsns = this._lsns[evt];
			if (!lsns) {
				this._lsns[evt] = [inf as {priority: number}];
			} else {
				for (var j = lsns.length; ;) {
					if (--j < 0 || (lsns[j] as {priority: number}).priority >= priority) {
						lsns.splice(j + 1, 0, inf as { priority: number });
						break;
					}
				}
			}
		}
		return this;
	}

	/**
	 * Removes a listener from the specified event.
	 * ```ts
	 * wgt.unlisten({
	 *   onClick: wgt,
	 *   onOpen: wgt._onOpen,
	 *   onMove: [o, o._onMove]
	 * });
	 * ```
	 * @param infos - a map of event listeners.
	 * Each key is the event name, and each value can be the target, the listener function, or a two-element array, where the first element is the target and the second the listener function.
	 * @see {@link listen}
	 * @see {@link isListen}
	 * @see {@link fire}
	 * @see {@link fireX}
	 */
	unlisten(infos: Record<string, unknown>): this {
		l_out:
		for (var evt in infos) {
			var inf = infos[evt],
				lsns = this._lsns[evt], lsn: [unknown, unknown];
			for (var j = lsns ? lsns.length : 0; j--;) {
				lsn = lsns[j] as [unknown, unknown];
				if (Array.isArray(inf)) inf = [inf[0] || this, inf[1]];
				else if (typeof inf == 'function') inf = [this, inf];
				else inf = [inf || this, undefined];
				if (lsn[0] == (inf as unknown[])[0] && lsn[1] == (inf as unknown[])[1]) {
					lsns.splice(j, 1);
					continue l_out;
				}
			}
		}
		return this;
	}

	/**
	 * @returns if a listener is registered for the specified event.
	 * @param evtnm - the event name, such as onClick.
	 * @param opts - the options. If omitted, it checks only if the server registers any non-deferrable listener, and if the client register any listener. Allowed values:
	 * <ul>
	 * <li>any - in addition to the server's non-deferrable listener and client's listener, it also checks deferrable listener, and the so-called important events</li>
	 * <li>asapOnly - it checks only if the server registers a non-deferrable listener, and if any non-deferrable important event. Use this option, if you want to know whether an AU request will be sent.</li>
	 * </ul>
	 */
	isListen(evt: string, opts?: Partial<{any: boolean; asapOnly: boolean}>): boolean {
		var v = this._asaps[evt];
		if (v) return true;
		if (opts) {
			if (opts.asapOnly) {
				v = this.get$Class<typeof Widget>()._importantEvts;
				return !!v && !!(v as object)[evt];
			}
			if (opts.any) {
				if (v != null) return true;
				v = this.get$Class<typeof Widget>()._importantEvts;
				if (v && (v as object)[evt] != null) return true;
			}
		}

		var lsns = this._lsns[evt];
		return lsns && lsns.length > 0;
	}

	/** Sets the listener a map of listeners.
	 * It is similar to {@link listen}, except
	 * <ul>
	 * <li>It will 'remember' what the listeners are, such that it can unlisten
	 * by specifying null as the value of the `infs` argument</li>
	 * <li>The function can be a string and it will be converted to {@link Function}
	 * automatically.</li>
	 * </ul>
	 * <p>This method is mainly designed to be called by the application running
	 * at the server.
	 *
	 * <p>Example:
	 * ```ts
	 * wgt.setListeners({
	 *  onChange: function (event) {this.doSomething();},
	 *  onFocus: 'this.doMore();',
	 *  onBlur: null //unlisten
	 * });
	 * ```
	 * @param infos - a map of event listeners.
	 * Each key is the event name, and each value is a string, a function or null.
	 * If the value is null, it means unlisten.
	 * If the value is a string, it will be converted to a {@link Function}.
	 * Notice that the target is not {@link zk.Event#target}. Rather, it is `this` when the listener is called.
	 */
	setListeners(listeners: Record<string, unknown & {priority: number}[]>): this {
		for (var evt in listeners)
			this.setListener(evt, listeners[evt]);
		return this;
	}
	// since 8.0
	setListeners0(listeners0: Record<string, unknown & {priority: number}[]>): this { //used by server
		for (var evt in listeners0)
			this.setListener0(evt, listeners0[evt]);
		return this;
	}
	/** Sets a listener that can be unlistened easily.
	 * It is designed to be called from server.
	 * For client-side programming, it is suggested to use {@link listen}.
	 * <p>It is based {@link listen}, but, unlike {@link listen}, the second
	 * invocation for the same event will unlisten the previous one automatically.
	 * <p>In additions, if the function (specified in the second element of inf)
	 * is null, it unlistens the previous invocation.
	 * @param inf - a two-element array. The first element is the event name,
	 * while the second is the listener function
	 * @see {@link setListeners}
	 */
	setListener(listener: [string, unknown]): this;
	/** Sets a listener
	 * It is designed to be called from server.
	 * For client-side programming, it is suggested to use {@link listen}.
	 * Use it only if you want to unlisten the listener registered at the
	 * server (by use of the client namespace).
	 * <p>It is based {@link listen}, but, unlike {@link listen}, the second
	 * invocation for the same event will unlisten the previous one automatically.
	 * <p>In additions, if fn is null, it unlistens the previous invocation.
	 * @param evt - the event name
	 * @param fn - the listener function.
	 * If null, it means unlisten.
	 * @see {@link setListeners}
	 * @see {@link listen}
	 */
	setListener(listener: string, fn: unknown): this;
	setListener(listener: string | [string, unknown], fn?: unknown): this { //used by server
		if (Array.isArray(listener)) {
			fn = listener[1];
			listener = listener[0];
		}

		var bklsns = this._bklsns,
			oldfn = bklsns[listener],
			inf = {};
		if (oldfn) { //unlisten first
			delete bklsns[listener];
			inf[listener] = oldfn;
			this.unlisten(inf);
		}
		if (fn) {
			inf[listener] = bklsns[listener]
				// eslint-disable-next-line no-new-func
				= typeof fn != 'function' ? new Function('var event=arguments[0];' + (fn as string)) : fn;
			this.listen(inf);
		}
		return this;
	}
	// since 8.0, it won't unlisten the old function.
	setListener0(listener0: string | [string, unknown], fn: unknown): this { //used by server
		if (Array.isArray(listener0)) {
			fn = listener0[1];
			listener0 = listener0[0];
		}

		var bklsns = this._bklsns,
			inf = {};
		if (fn) {
			inf[listener0] = bklsns[listener0]
				// eslint-disable-next-line no-new-func
				= typeof fn != 'function' ? new Function('var event=arguments[0];' + (fn as string)) : fn;
			this.listen(inf);
		}
		return this;
	}

	setOverride(override: string | [string, unknown], val: unknown): this { //used by server (5.0.2)
		if (Array.isArray(override)) {
			val = override[1];
			override = override[0];
		}
		if (val) {
			var oldnm = '$' + override;
			if (this[oldnm] == null && this[override]) //only once
				this[oldnm] = this[override] as never;
			this[override] = val;
			//use eval, since complete func decl
		} else {
			var oldnm = '$' + override;
			this[override] = this[oldnm] as never; //restore
			delete this[oldnm];
		}
		return this;
	}

	setOverrides(overrides: Record<string, unknown>): this { //used by server
		for (var nm in overrides)
			this.setOverride(nm, overrides[nm]);
		return this;
	}

	//ZK event handling//
	/** Called when the user clicks or right-clicks on widget or a child widget.
	 * It is called before {@link doClick_} and {@link doRightClick_}.
	 * @defaultValue does nothing but invokes the parent's {@link doSelect_}.
	 * Notice that it does not fire any event.
	 * <p>Deriving class that supports selection (such as {@link zul.sel.ItemWidget})
	 * shall override this to handle the selection.
	 * <p>Technically, the selection can be handled in {@link doClick_}.
	 * However, it is better to handle here since this method is invoked first
	 * such that the widget will be selected before one of its descendant widget
	 * handles {@link doClick_}.
	 * <p>Notice that calling {@link zk.Event#stop} will stop the invocation of
	 * parent's {@link doSelect_} and {@link doClick_}/{@link doRightClick_}.
	 * If you just don't want to call parent's {@link doSelect_}, simply
	 * not to invoke super's doSelect_.
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doClick_}
	 * @see {@link doRightClick_}
	 * @since 5.0.1
	 * @internal
	 */
	doSelect_(evt: zk.Event): void {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doSelect_(evt);
		}
	}

	/** Called when the mouse is moved over this widget.
	 * It is called before {@link doMouseOver_}.
	 * @defaultValue does nothing but invokes the parent's {@link doTooltipOver_}.
	 * Notice that it does not fire any event.
	 * <p>Notice that calling {@link zk.Event#stop} will stop the invocation of
	 * parent's {@link doTooltipOver_} and {@link doMouseOver_}.
	 * If you just don't want to call parent's {@link doMouseOver_}, simply
	 * not to invoke super's doMouseOver_.
	 * @since 5.0.5
	 * @see {@link doTooltipOut_}
	 * @internal
	 */
	doTooltipOver_(evt: zk.Event): void {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doTooltipOver_(evt);
		}
	}

	/** Called when the mouse is moved out of this widget.
	 * It is called before {@link doMouseOut_}.
	 * @defaultValue does nothing but invokes the parent's {@link doTooltipOut_}.
	 * Notice that it does not fire any event.
	 * <p>Notice that calling {@link zk.Event#stop} will stop the invocation of
	 * parent's {@link doTooltipOut_} and {@link doMouseOut_}.
	 * If you just don't want to call parent's {@link doMouseOut_}, simply
	 * not to invoke super's doMouseOut_.
	 * @since 5.0.5
	 * @see {@link doTooltipOver_}
	 * @internal
	 */
	doTooltipOut_(evt: zk.Event): void {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doTooltipOut_(evt);
		}
	}

	/** Called when the user clicks on a widget or a child widget.
	 * A widget doesn't need to listen the click DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and call parent's doClick_
	 * if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>If a widget, such as zul.wgt.Button, handles onClick, it is better to override this method and <i>not</i> calling back the superclass.
	 * <p>Note: if {@link shallIgnoreClick_} returns true, {@link fireX} won't be
	 * called and this method invokes the parent's {@link doClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doDoubleClick_}
	 * @see {@link doRightClick_}
	 * @see {@link doSelect_}
	 * @internal
	 */
	doClick_(evt: zk.Event): void {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doClick_(evt);
		}
	}

	/** Called when the user double-clicks on a widget or a child widget.
	 * A widget doesn't need to listen the dblclick DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and call parent's
	 * doDoubleClick_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>Note: if {@link shallIgnoreClick_} returns true, {@link fireX} won't be
	 * called and this method invokes the parent's {@link doDoubleClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doClick_}
	 * @see {@link doRightClick_}
	 * @internal
	 */
	doDoubleClick_(evt: zk.Event): void {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doDoubleClick_(evt);
		}
	}

	/** Called when the user right-clicks on a widget or a child widget.
	 * A widget doesn't need to listen the contextmenu DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and call parent's
	 * doRightClick_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>Note: if {@link shallIgnoreClick_} returns true, {@link fireX} won't be
	 * called and this method invokes the parent's {@link doRightClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doClick_}
	 * @see {@link doDoubleClick_}
	 * @internal
	 */
	doRightClick_(evt: zk.Event): void {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doRightClick_(evt);
		}
	}

	/** Called when the user moves the mouse pointer on top of a widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseover DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doMouseOver_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doMouseMove_}
	 * @see {@link doMouseOver_}
	 * @see {@link doMouseOut_}
	 * @see {@link doMouseDown_}
	 * @see {@link doMouseUp_}
	 * @see {@link doTooltipOver_}
	 * @internal
	 */
	doMouseOver_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOver_(evt);
		}
	}

	/** Called when the user moves the mouse pointer out of a widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseout DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doMouseOut_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @see {@link doMouseMove_}
	 * @see {@link doMouseOver_}
	 * @see {@link doMouseDown_}
	 * @see {@link doMouseUp_}
	 * @see {@link doTooltipOut_}
	 * @internal
	 */
	doMouseOut_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOut_(evt);
		}
	}

	/** Called when the user presses down the mouse button on this widget (or one of its child widget).
	 * A widget doesn't need to listen the mousedown DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doMouseDown_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doMouseMove_}
	 * @see {@link doMouseOver_}
	 * @see {@link doMouseOut_}
	 * @see {@link doMouseUp_}
	 * @see {@link doClick_}
	 * @internal
	 */
	doMouseDown_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseDown_(evt);
		}
	}

	/** Called when the user presses up the mouse button on this widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseup DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doMouseUp_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doMouseMove_}
	 * @see {@link doMouseOver_}
	 * @see {@link doMouseOut_}
	 * @see {@link doMouseDown_}
	 * @see {@link doClick_}
	 * @internal
	 */
	doMouseUp_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseUp_(evt);
		}
	}

	/** Called when the user moves the mouse pointer over this widget (or one of its child widget).
	 * A widget doesn't need to listen the mousemove DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doMouseMove_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doMouseOver_}
	 * @see {@link doMouseOut_}
	 * @see {@link doMouseDown_}
	 * @see {@link doMouseUp_}
	 * @internal
	 */
	doMouseMove_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseMove_(evt);
		}
	}

	/** Called when the user presses down a key when this widget has the focus ({@link focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keydown DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doKeyDown_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doKeyUp_}
	 * @see {@link doKeyPress_}
	 * @internal
	 */
	doKeyDown_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyDown_(evt);
		}
	}

	/** Called when the user presses up a key when this widget has the focus ({@link focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keyup DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doKeyUp_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doKeyDown_}
	 * @see {@link doKeyPress_}
	 * @internal
	 */
	doKeyUp_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyUp_(evt);
		}
	}

	/** Called when the user presses a key when this widget has the focus ({@link focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keypress DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doKeyPress_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doKeyDown_}
	 * @see {@link doKeyUp_}
	 * @internal
	 */
	doKeyPress_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyPress_(evt);
		}
	}

	/** Called when the user paste text to this widget which has been the focused ({@link focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the paste DOM event.
	 * Rather, it shall override this method if necessary.
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doPaste_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doKeyDown_}
	 * @see {@link doKeyUp_}
	 * @see {@link doKeyPress_}
	 * @internal
	 */
	doPaste_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doPaste_(evt);
		}
	}

	/** Called when the user swipe left/right/up/down this widget.
	 * <p>For example,
```ts
var opts = evt.opts, dir = opts.dir;
switch (dir) {
case 'left': doSwipeLeft(); break;
case 'right': doSwipeRight(); break;
case 'up': doSwipeUp(); break;
case 'down': doSwipeDown(); break;
}
```
	 * To define swipe direction rather than default condition,
```ts
var opts = evt.opts, start = opts.start, stop = opts.stop,
	dispT = stop.time - start.time,
	deltaX = start.coords[0] - stop.coords[0],
	deltaY = start.coords[1] - stop.coords[1],
	dispX = Math.abs(deltaX),
	dispY = Math.abs(deltaY);

//if swipe time is less than 500ms, it is considered as swipe event
if (dispT < 500) {
	  //if horizontal displacement is larger than 30px and vertical displacement is smaller than 75px, it is considered swipe left/right
	if (dispX > 30 && dispY < 75)
		//swipe left if deltaX > 0

	//if vertical displacement is large than 30px and horizontal displacement is smaller than 75px, it is considered swipe up/down
	else if (dispY > 30 && dispX < 75)
		//swipe up if deltaY > 0
}
```
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doSwipe_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * @param evt - the widget event.
	 * @since 6.5.0
	 * @internal
	 */
	doSwipe_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doSwipe_(evt);
		}
	}

	/** A utility to simplify the listening of `onFocus`.
	 * Unlike other doXxx_ (such as {@link doClick_}), a widget needs to listen
	 * the onFocus event explicitly if it might gain and lose the focus.
	 * <p>For example,
```ts
var fn = this.$n('focus');
this.domListen_(fn, 'onFocus', 'doFocus_');
this.domListen_(fn, 'onBlur', 'doBlur_');
```
	 *<p>Of course, you can listen it with jQuery DOM-level utilities, if you pefer to handle it differently.
	 *
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doFocus_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doBlur_}
	 * @internal
	 */
	doFocus_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doFocus_(evt);
		}
	}

	/** A utility to simplify the listening of `onBlur`.
	 * Unlike other doXxx_ (such as {@link doClick_}), a widget needs to listen
	 * the onBlur event explicitly if it might gain and lose the focus.
	 * <p>For example,
```ts
var fn = this.$n('focus');
this.domListen_(fn, 'onFocus', 'doFocus_');
this.domListen_(fn, 'onBlur', 'doBlur_');
```
	 *<p>Of course, you can listen it with jQuery DOM-level utilities, if you pefer to handle it differently.
	 *
	 * @defaultValue fire the widget event ({@link fireX}), and
	 * call parent's doBlur_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see {@link doFocus_}
	 * @internal
	 */
	doBlur_(evt: zk.Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doBlur_(evt);
		}
	}

	/** Resize zul.Scrollbar size after child added/removed or hide/show.
	 * @since 6.5.0
	 * @internal
	 */
	doResizeScroll_(): void {
		var p = this.parent;
		if (p) p.doResizeScroll_();
	}

	//DOM event handling//
	/** Registers an DOM event listener for the specified DOM element (aka., node).
	 * You can use jQuery to listen the DOM event directly, or
	 * use this method instead.
	 * ```ts
	 * bind_: function () {
	 *   this.$supers('bind_', arguments);
	 *   this.domListen_(this.$n(), "onChange"); //fn is omitted, so _doChange is assumed
	 *   this.domListen_(this.$n("foo"), "onSelect", "_doFooSelect"); //specify a particular listener
	 * },
	 * unbind_: function () {
	 *   this.domUnlisten_(this.$n(), "onChange"); //unlisten
	 *   this.domUnlisten_(this.$n("foo"), "onSelect", "_doFooSelect");
	 *   this.$supers('unbind_', arguments);
	 * },
	 * _doChange_: function (evt) { //evt is an instance of zk.Event
	 *   //event listener
	 * },
	 * _doFooSelect: function (evt) {
	 * }
	 * ```
	 * See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 *
	 * <h3>Design Mode</h3>
	 * If a widget is created and controlled by ZK Weaver for visual design,
	 * we call the widget is in design mode ({@link Widget.$weave}).
	 * Furthermore, this method does nothing if the widget is in the design mode.
	 * Thus, if you want to listen a DOM event ({@link jq.Event}), you have
	 * to use jQuery directly.
	 * @param node - a node of this widget.
	 * It is usually retrieved by {@link Widget.$n}.
	 * @param evtnm - the event name to register, such as onClick.
	 * @param fn - the name ({@link String}) of the member method to handle the event,
	 * or the function ({@link Function}).
	 * It is optional. If omitted, <i>_doEvtnm</i> is assumed, where <i>evtnm</i>
	 * is the value passed thru the `evtnm` argument.
	 * For example, if the event name is onFocus, then the method is assumed to be
	 * _doFocus.
	 * @param keyword - the extra argumenet for the function, which is passed
	 * into the callback function. (since 7.0)
	 * @see {@link domUnlisten_}
	 * @internal
	 */
	domListen_(node: HTMLElement | JQuery, evtnm: string, fn?: string | CallableFunction, keyword?: unknown): this {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn, keyword);
			jq(node, zk).on(inf[0], inf[1]);
		}
		return this;
	}

	/**
	 * Un-registers an event listener for the specified DOM element (aka., node).
	 * <p>Refer to {@link domListen_} for more information.
	 * @param node - a node of this widget.
	 * It is usually retrieved by {@link Widget.$n}.
	 * @param evtnm - the event name to register, such as onClick.
	 * @param fn - the name ({@link String}) of the member method to handle the event,
	 * or the function ({@link Function}).
	 * It is optional. If omitted, <i>_doEvtnm</i> is assumed, where <i>evtnm</i>
	 * is the value passed thru the `evtnm` argument.
	 * For example, if the event name is onFocus, then the method is assumed to be
	 * _doFocus.
	 * @param keyword - the extra argumenet for the function, which is passed
	 * into the callback function. (since 7.0)
	 * @see {@link domListen_}
	 * @internal
	 */
	domUnlisten_(n: HTMLElement | JQuery, evtnm: string, fn?: string | CallableFunction, keyword?: unknown): this {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn, keyword);
			jq(n, zk).off(inf[0], inf[1]);
		}
		return this;
	}

	/**
	 * Listens to onFitSize event. Override if a subclass wants to skip listening
	 * or have extra processing.
	 * @see {@link unlistenOnFitSize_}
	 * @since 5.0.8
	 * @internal
	 */
	listenOnFitSize_(): void {
		if (!this._fitSizeListened && (this._hflex == 'min' || this._vflex == 'min')) {
			zWatch.listen({onFitSize: [this, zFlex.onFitSize]});
			this._fitSizeListened = true;
		}
	}

	/**
	 * Unlistens to onFitSize event. Override if a subclass wants to skip listening
	 * or have extra processing.
	 * @see {@link listenOnFitSize_}
	 * @since 5.0.8
	 * @internal
	 */
	unlistenOnFitSize_(): void {
		if (this._fitSizeListened) {
			zWatch.unlisten({onFitSize: [this, zFlex.onFitSize]});
			this._fitSizeListened = false;
		}
	}

	/** Converts a coordinate related to the browser window into the coordinate
	 * related to this widget.
	 * @param x - the X coordinate related to the browser window
	 * @param y - the Y coordinate related to the browser window
	 * @returns the coordinate related to this widget (i.e., [0, 0] is
	 * the left-top corner of the widget).
	 * @since 5.0.2
	 */
	fromPageCoord(x: number, y: number): zk.Offset {
		var ofs = zk(this).revisedOffset();
		return [x - ofs[0], y - ofs[1]];
	}

	/**
	 * @returns if the given watch shall be fired for this widget.
	 * It is called by {@link zWatch} to check if the given watch shall be fired
	 * @param name - the name of the watch, such as onShow
	 * @param p - the parent widget causing the watch event.
	 * It is null if it is not caused by {@link _global_.zWatch#fireDown}.
	 * @param cache - a map of cached result (since 5.0.8). Ignored if null.
	 * If specified, the result will be stored and used to speed up the processing
	 * @since 5.0.3
	 * @internal
	 */
	isWatchable_(name: string, p?: zk.Widget, cache?: Record<string, unknown>): boolean {
		//if onShow, we don't check visibility since window uses it for
		//non-embedded window that becomes invisible because of its parent
		var strict = name != 'onShow', wgt: zk.Widget | undefined;
		if (p)
			return this.isRealVisible({dom: true, strict: strict, until: p, cache: cache});

		for (wgt = this; ;) {
			//1. if native, $n() might be null or wrong (if two with same ID)
			//2. ZK-5058: if noDom, $n() is invisible by default.
			if (!(wgt instanceof zk.Native) && wgt.getMold() != 'nodom')
				break;

			//Note: we check _visible only if native, since, when onHide is fired,
			//_visible is false but DOM element is visible (so it is watchable)
			if (!wgt._visible)
				return false;

			//it might be native or others, so we look up parent
			if (!(wgt = wgt.parent))
				return true; //consider as visible if it is root
		}

		return zk(wgt.$n()).isRealVisible(strict);
	}

	toJSON(): string { //used by JSON
		return this.uuid;
	}

	/**
	 * A widget call this function of its ancestor if it wants to know whether its ancestor prefer ignore float up event of it self.
	 * @defaultValue `false`.
	 * @since 6.0.0
	 * @internal
	 */
	ignoreDescendantFloatUp_(des: zk.Widget): boolean {
		return false;
	}

	// internal use only in zkmax package
	/** @internal */
	getDomEvtInf_(wgt: zk.Widget, evtnm: string, fn?: string | CallableFunction, keyword?: unknown): [string, JQueryEventHandler] {
		return _domEvtInf(wgt, evtnm, fn, keyword);
	}

	/**
	 * @returns whether a widget should fireSized later when addChd was invoked
	 * @defaultValue `false`.
	 * @internal
	 */
	shallFireSizedLaterWhenAddChd_(): boolean {
		return false;
	}

	/** A callback called after a widget is composed but not yet be bound to DOM tree.
	 * @since 10.0.0
	 * @internal
	 */
	afterCompose_(): void {
		return;
	}
	// internal use, since zk 8.0.0
	static disableChildCallback(): void {
		_noChildCallback = true;
	}

	// internal use, since zk 8.0.0
	static enableChildCallback(): void {
		_noChildCallback = false;
	}

	/** Retrieves the widget.
	 * @param n - the object to look for. If it is a string,
	 * it is assumed to be UUID, unless it starts with '$'.
	 * For example, `zk.Widget.$('uuid')` is the same as `zk.Widget.$('#uuid')`,
	 * and both look for a widget whose ID is 'uuid'. On the other hand,
	 * `zk.Widget.$('$id') `looks for a widget whose ID is 'id'.<br/>
	 * and `zk.Widget.$('.className')` looks for a widget whose CSS selector is 'className'. (since zk 8.0)<br/>
	 * If it is an DOM element ({@link DOMElement}), it will look up
	 * which widget it belongs to.<br/>
	 * If the object is not a DOM element and has a property called
	 * `target`, then `target` is assumed.
	 * Thus, you can pass an instance of {@link jq.Event} or {@link zk.Event},
	 * and the target widget will be returned.
	 * @param opts - the options. Allowed values:
	 * <ul>
	 * <li>exact - id must exactly match uuid (i.e., uuid-xx ignored).
	 * It also implies strict (since 5.0.2)</li>
	 * <li>strict - whether not to look up the parent node.(since 5.0.2)
	 * If omitted, false is assumed (and it will look up parent).</li>
	 * <li>child - whether to ensure the given element is a child element
	 * of the widget's main element ({@link Widget.$n}). In most cases, if ID
	 * of an element is xxx-yyy, the the element must be a child of
	 * the element whose ID is xxx. However, there is some exception
	 * such as the shadow of a window.</li>
	 * </ul>
	 */
	static $<T extends Widget>(n?: string | JQuery | JQuery.Event | zk.Event | Node | undefined | null | T, // eslint-disable-line zk/noNull
		opts?: Partial<{exact: boolean; strict: boolean; child: boolean}>): T | undefined {
		if (n && (n as JQuery).zk && (n as JQuery).zk.jq == n) //jq()
			n = n[0];

		if (!n)
			return undefined;

		if ((n instanceof Widget)) {
			return n;
		}

		let wgt: zk.Widget | undefined, id: string | undefined;
		if (typeof n == 'string') {
			//Don't look for DOM (there might be some non-ZK node with same ID)
			const query = n;

			// fix zk.$("$tree @treeitem") case
			if (!query.includes(' ')) {
				if ((id = n.charAt(0)) == '#') n = n.substring(1);
				else if (id == '$') {
					const v = _globals[n.substring(1)] as T[] | undefined;
					return v ? v[0] : undefined;
				}
				wgt = _binds[n]; //try first (since ZHTML might use -)
				if (!wgt) {
					let idx: number;
					wgt = (idx = n.indexOf('-')) >= 0 ? _binds[n.substring(0, idx)] : undefined;
				}
			}

			if (!wgt)
				return jq(query).zk.$<T>(opts);
			return wgt as T;
		}

		if (!(n instanceof Element)) { //n could be an event (skip Element)
			var e1: unknown & {z$target?: Node} | undefined, e2: Node | undefined;
			n = ((e1 = n['originalEvent'] as {z$target?: Node}) ? e1.z$target : undefined)
				|| ((e1 = n['target'] as never) && (e2 = (e1 as {z$proxy?: Node}).z$proxy) ? e2 : e1) || n; //check DOM event first
		}

		opts = opts || {};
		if (opts.exact)
			return _binds[(n as HTMLElement).id] as T;

		for (; n; n = zk(n).vparentNode(true)) {
			try {
				id = (n as HTMLElement).id || ((n as HTMLElement).getAttribute ? (n as HTMLElement).getAttribute('id') as string | undefined : '');
				if (id && typeof id == 'string') {
					wgt = _binds[id]; //try first (since ZHTML might use -)
					if (wgt)
						return wgt as T;

					var j = id.indexOf('-');
					if (j >= 0) {
						wgt = _binds[id = id.substring(0, j)];
						if (wgt) {
							if (!opts.child)
								return wgt as T;

							var n2 = wgt.$n();
							if (n2 && jq.isAncestor(n2, n as HTMLElement))
								return wgt as T;
						}
					}
				}
			} catch (e) {
				zk.debugLog((e as Error).message ?? e);
			}
			if (opts.strict)
				break;
		}
		return undefined;
	}

	/** Called to mimic the mouse down event fired by the browser.
	 * It is used for implement a widget. In most cases, you don't need to
	 * invoke this method.
	 * <p>However, it is useful if the widget you are implemented will 'eat'
	 * the mouse-down event so ZK Client Engine won't be able to intercept it
	 * at the document level.
	 * @param wgt - the widget that receives the mouse-down event
	 * @param noFocusChange - whether zk.currentFocus shall be changed to wgt.
	 * @param which - the button number that was pressed.
	 * @internal
	 */
	static mimicMouseDown_(wgt?: zk.Widget, noFocusChange?: boolean, which?: number): void { //called by mount
		var modal = zk.currentModal;
		if (modal && !wgt) {
			var cf = zk.currentFocus;
			//Note: browser might change focus later, so delay a bit
			//(it doesn't work if we stop event instead of delay - IE)
			if (cf && zUtl.isAncestor(modal, cf)) cf.focus(0);
			else modal.focus(0);
		} else if (!wgt || wgt.canActivate()) {
			if (!noFocusChange) {
				zk._prevFocus = zk.currentFocus;
				zk.currentFocus = wgt;
				zk._cfByMD = true;
				setTimeout(function () {zk._cfByMD = false; zk._prevFocus = undefined;}, 0);
				//turn it off later since onBlur_ needs it
			}
		}
		if (wgt) // F70-ZK-2007: Add the button number information.
			zWatch.fire('onFloatUp', wgt, {triggerByClick: which}); //notify all
		else
			for (var dtid in zk.Desktop.all)
				zWatch.fire('onFloatUp', zk.Desktop.all[dtid]); //notify all
	}

	/**
	 * @returns all {@link DOMElement} with the given widget name.
	 * @param name - the widget name {@link widgetName}.
	 * @since 5.0.2
	 */
	static getElementsByName(name: string): HTMLElement[] {
		var els: {n: HTMLElement; w: zk.Widget}[] = [];
		for (var wid in _binds) {
			if (name == '*' || name == _binds[wid].widgetName) {
				var _w = _binds[wid],
					n = _w.$n(), w;

				// force rod to render before query.
				if (!n && _w.z_rod && zk.isLoaded('stateless') && stateless['disableROD']) {
					var parent = _w;
					for (var p: zk.Widget | undefined = _w; p; p = p.parent) {
						if (p.z_rod || p._rodKid) {
							parent = p;
						} else {
							break;
						}
					}
					if (parent != null) {
						parent.forcerender();
					}
					n = _w.$n();
				}
				//Bug B50-3310406 need to check if widget is removed or not.
				if (n && (w = Widget.$(_binds[wid]))) {
					els.push({
						n: n,
						w: w as zk.Widget
					});
				}
			}
		}
		if (els.length) {
			// fixed the order of the component that have been changed dynamically.
			// (Bug in B30-1892446.ztl, B50-3095549.ztl, and B50-3131173.ztl)
			els.sort(function (a, b) {
				var w1: zk.Widget | undefined = a.w,
					w2: zk.Widget | undefined = b.w;
				// We have to compare each ancestor to make the result as CSS selector.
				// The performance is bad but it is only used for testing purpose.
				if (w1.bindLevel < w2.bindLevel) {
					do {
						w2 = w2.parent;
					} while (w1 && w2 && w1.bindLevel < w2.bindLevel);
				} else if (w1.bindLevel > w2.bindLevel) {
					do {
						w1 = w1.parent;
					} while (w1 && w2 && w1.bindLevel > w2.bindLevel);
				}
				var wp1 = w1?.parent,
					wp2 = w2?.parent;
				while (wp1 && wp2 && wp1 != wp2) {
					w1 = wp1;
					w2 = wp2;
					wp1 = wp1.parent;
					wp2 = wp2.parent;
				}
				if (w1 && w2) {
					return w1.getChildIndex() - w2.getChildIndex();
				}
				return 0;
			});
			var tmp: HTMLElement[] = [];
			for (let i = els.length; i--;) {
				const ele = els[i];
				ele.n[zk.Widget._TARGET] = ele.w;

				// clear up
				delete ele.n[zk.Widget._CURRENT_TARGET];
				tmp.unshift(ele.n);
			}
			return tmp;
		}
		return [];
	}

	/**
	 * @returns all {@link DOMElement} with the given ID.
	 * @param id - the id of a widget, {@link id}.
	 * @since 5.0.2
	 */
	static getElementsById(id: string): HTMLElement[] {
		var els: HTMLElement[] = [];
		for (var n: HTMLElement | undefined, wgts = _globals[id], i = wgts ? wgts.length : 0; i--;) {
			n = wgts[i].$n();
			if (n) els.unshift(n);
		}
		return els;
	}

	/**
	 * @returns the {@link zk.Widget} with the given Uuid.
	 * @param uuid - the uuid of a widget.
	 * @since 10.0.0
	 */
	static getWidgetByUuid(uuid: string): zk.Widget {
		return _binds[uuid];
	}

	//uuid//
	/**
	 * Converts an ID of a DOM element to UUID.
	 * It actually removes '-*'. For example, zk.Widget.uuid('z_aa-box') returns 'z_aa'.
	 * @param subId - the ID of a DOM element
	 * @returns the uuid of the widget (notice that the widget might not exist)
	 */
	static uuid(id: HTMLElement | string): string {
		var uuid = typeof id == 'object' ? id.id || '' : id,
			j = uuid.indexOf('-');
		return j >= 0 ? uuid.substring(0, j) : id as string;
	}

	/**
	 * @returns the next unique UUID for a widget.
	 * The UUID is unique in the whole browser window and does not conflict with the peer component's UUID.
	 * <p>This method is called automatically if {@link Widget.$init} is called without uuid.
	 */
	static nextUuid(): string {
		return '_z_' + _nextUuid++;
	}

	/**
	 * @deprecated we cannot really detect at the client if UUID is generated automatically.
	 * @param uuid - the UUID to test
	 */
	static isAutoId(id: string): boolean {
		return !id;
	}

	/** Registers a widget class.
	 * It is called automatically if the widget is loaded by WPD loader, so you rarely
	 * need to invoke this method.
	 * However, if you create a widget class at run time, you have to call this method explicitly.
	 * Otherwise, {@link className}, {@link getClass}, and {@link newInstance}
	 * won't be applicable.
	 * <p>Notice that the class must be declared before calling this method.
	 * In other words, zk.$import(clsnm) must return the class of the specified class name.
```ts
zk.Widget.register('foo.Cool'); //class name
zk.Widget.getClass('cool'); //widget name
```
	 * @param clsnm - the class name, such as zul.wnd.Window
	 * @param blankPreserved - whether to preserve the whitespaces between child widgets when declared in iZUML. If true, a widget of clsnm will have a data member named blankPreserved (assigned with true). And, iZUML won't trim the whitespaces (aka., the blank text) between two adjacent child widgets.
	 */
	static register(clsnm: string, blankprev: boolean): void {
		var cls = zk.$import(clsnm) as typeof Widget;
		cls.prototype.className = clsnm;
		var j = clsnm.lastIndexOf('.');
		if (j >= 0) clsnm = clsnm.substring(j + 1);
		_wgtcls[cls.prototype.widgetName = clsnm.toLowerCase()] = cls;
		if (blankprev) cls.prototype.blankPreserved = true;
	}

	/**
	 * @returns the class of the specified widget's name. For example,
	 * ```ts
	 * zk.Widget.getClass('combobox');
	 * ```
	 *<p>Notice that null is returned if the widget is not loaded (or not exist) yet.
	 * @param wgtnm - the widget name, such as textbox.
	 * @see {@link newInstance}
	 * @see {@link register}
	 */
	static getClass(wgtnm: string): typeof Widget {
		return _wgtcls[wgtnm];
	}

	/**
	 * Creates a widget by specifying the widget name.
	 * The widget name is the last part of the class name of a widget (and converting the first letter to lower case).
	 * For example, if a widget's class name is zul.inp.Textbox, then the widget name is textbox.
	 * <p>This method is usually used by tools, such as zk.zuml.Parser, rather than developers, since developers can create the widget directly if he knows the class name.
	 * @param wgtnm - the widget name, such as textbox.
	 * @param props - the properties that will be passed to
	 * {@link Widget.$init}.
	 * @see {@link getClass}
	 * @see {@link register}
	 */
	static newInstance<T extends typeof Widget>(wgtnm: string, props?: Record<string, unknown>): InstanceType<T> {
		var cls = _wgtcls[wgtnm];
		if (!cls) {
			let msg;
			zk.error(msg = 'Unknown widget: ' + wgtnm);
			throw msg;
		}
		return new cls(props) as InstanceType<T>;
	}

	/**
	 * The default delay before sending an AU request when {@link fire}
	 * is called (and the server has an ARAP event listener registered).
	 * @defaultValue `38` (Unit: miliseconds).
	 * @since 5.0.8
	 */
	static auDelay = 38;
	/** @internal */
	static _bindrod(wgt: zk.Widget): void {
		this._bind0(wgt);
		if (!wgt.z_rod)
			wgt.z_rod = 9; //Bug 2948829: don't use true which is used by real ROD, such as combo-rod.js

		for (var child = wgt.firstChild; child; child = child.nextSibling)
			this._bindrod(child);
	}
	/** @internal */
	static _bind0(wgt: zk.Widget): void { //always called no matter ROD or not
		_binds[wgt.uuid] = wgt;
		if (wgt.id)
			this._addGlobal(wgt);
	}
	/** @internal */
	static _addGlobal(wgt: zk.Widget): void { //note: wgt.id must be checked before calling this method
		var gs = _globals[wgt.id!];
		if (gs)
			gs.push(wgt);
		else
			_globals[wgt.id!] = [wgt];
	}
	/** @internal */
	static _unbindrod(wgt: zk.Widget, nest?: boolean, keepRod?: boolean): void {
		this._unbind0(wgt);

		if (!nest || wgt.z_rod === 9) { //Bug 2948829: don't delete value set by real ROD
			if (!keepRod) delete wgt.z_rod;

			for (var child = wgt.firstChild; child; child = child.nextSibling) {
				this._unbindrod(child, true, keepRod);
				//Bug ZK-1827: native component with rod should also store the widget for used in mount.js(create function)
				if ((child instanceof zk.Native))
					zAu._storeStub(child);
			}
		}
	}
	/** @internal */
	static _unbind0(wgt: zk.Widget): void {
		if (wgt.id)
			this._rmGlobal(wgt);
		delete _binds[wgt.uuid];
		wgt.desktop = undefined;
		wgt.clearCache();
		wgt.unbindRod_(); // fix for Client MVVM
	}

	/** @internal */
	static _rmGlobal(wgt: zk.Widget): void {
		var gs = _globals[wgt.id!];
		if (gs) {
			gs.$remove(wgt);
			if (!gs.length) delete _globals[wgt.id!];
		}
	}
	/** @internal */
	static readonly _TARGET = '__target__'; // used for storing the query widget target
	/** @internal */
	static readonly _TARGETS = '__targets__'; // used for storing the query widget targets into one element,
	// such as Treerow, Treechildren, and Treeitem.
	/** @internal */
	static readonly _CURRENT_TARGET = '__ctarget__'; // used for storing the current query widget target
}

/**
 * A shortcut of `Widget.$()` function.
 *
 * Note: zk.Widget is the same as `zk.Widget`.
 * @since 8.0.0
 * @see {@link Widget.$}
 */
export var $ = Widget.$;

/** @internal */
export namespace widget_global {
	export const zkreg = Widget.register; //a shortcut for WPD loader
}

/**
 * A reference widget. It is used as a temporary widget that will be
 * replaced with a real widget when {@link bind_} is called.
 * <p>Developers rarely need it.
 * Currently, it is used only for the server to generate the JavaScript codes
 * for mounting.
 */
// zk scope
export class RefWidget extends Widget {
	/**
	 * The class name (`zk.RefWidget`).
	 * @since 5.0.3
	 */
	override className = 'zk.RefWidget';
	/**
	 * The widget name (`refWidget`).
	 * @since 5.0.3
	 */
	override widgetName = 'refWidget';
	/** @internal */
	override bind_(): void {
		var w = Widget.$(this.uuid);
		if (!w) {
			zk.error('RefWidget not found: ' + this.uuid);
			return;
		}

		var p: undefined | zk.Widget;
		if (p = w.parent) //shall be a desktop
			_unlink(p, w); //unlink only

		_replaceLink(this, w);
		this.parent = this.nextSibling = this.previousSibling = undefined;

		_addIdSpaceDown(w); //add again since parent is changed

		//no need to call super since it is bound
	}
}

//desktop//
/** A desktop.
 * Unlike the component at the server, a desktop is a widget.
 * <p>However, the desktop are different from normal widgets:
 * <ol>
 * <li>The desktop is a conceptual widget. It is never attached with the DOM tree. Its desktop field is always null. In addition, calling zk.Widget#appendChild won't cause the child to be attached to the DOM tree automatically.</li>
 * <li>The desktop's ID and UUID are the same. </li>
 * </ol>
 */
// zk scope
export class Desktop extends Widget {
	/** @internal */
	declare _cmsp?: zkex.cmsp.SPush;
	/** @internal */
	declare _cpsp?: zk.cpsp.SPush;
	/** @internal */
	declare static _dt?: Desktop;
	/** @internal */
	declare _aureqs: zk.Event[];
	/** @internal */
	declare _bpg: Body;
	/** @internal */
	declare _pfDoneIds?: string;
	/** @internal */
	declare _pfRecvIds?: string;
	declare obsolete?: boolean;

	//a virtual node that might have no DOM node and must be handled specially
	override z_virnd = true;

	override bindLevel = 0;
	/** The class name (`zk.Desktop`).
	 * @type String
	 */
	override className = 'zk.Desktop';
	/** The widget name (`desktop`).
	 * @type String
	 * @since 5.0.2
	 */
	override widgetName = 'desktop';
	/** The request path.
	 * @type String
	 */
	requestPath: string | undefined;

	updateURI?: string;
	resourceURI?: string;
	contextURI?: string;
	stateless?: boolean;

	/**
	 * @param dtid - the ID of the desktop
	 * @param contextURI - the context URI, such as `/zkdemo`
	 * @param updateURI - the URI of ZK Update Engine, such as `/zkdemo/zkau`
	 * @param reqURI - the URI of the request path.
	 * @param stateless - whether this desktop is used for a stateless page.
	 * Specify true if you want to use <a href="http://books.zkoss.org/wiki/Small_Talks/2009/July/ZK_5.0_and_Client-centric_Approach">the client-centric approach</a>.
	 */
	constructor(dtid: string, contextURI?: string, updateURI?: string,
		resourceURI?: string, reqURI?: string, stateless?: boolean) {
		super({uuid: dtid}); //id also uuid

		var dts = Desktop.all, dt: Desktop | undefined;

		this._aureqs = [];
		//Sever side effect: this.desktop = this;

		if (dt = dts[dtid]) {
			if (updateURI != null) dt.updateURI = updateURI;
			if (resourceURI != null) dt.resourceURI = resourceURI;
			if (contextURI != null) dt.contextURI = contextURI;
		} else {
			this.uuid = this.id = dtid;
			this.updateURI = updateURI != null ? updateURI : zk.updateURI;
			this.resourceURI = resourceURI != null ? resourceURI : zk.resourceURI;
			this.contextURI = contextURI != null ? contextURI : zk.contextURI;
			this.requestPath = reqURI || '';
			this.stateless = stateless;
			dts[dtid] = this;
			++Desktop._ndt;
		}

		Desktop._dt = dt || this; //default desktop
		Desktop.sync(60000); //wait since liferay on IE delays the creation
	}

	/** @internal */
	override bind_(): void { return; }
	/** @internal */
	override unbind_(): void { return; }
	/**
	 * This method is voided (does nothing) since the desktop's ID
	 * can be changed.
	 * @param id - the ID
	 */
	override setId(id: string): this {
		return this;
	}

	//ZK-2663: Popup does not show up when its parent is native
	override isRealVisible(): boolean {
		return true;
	}

	/**
	 * @returns the desktop of the specified desktop ID, widget, widget UUID, or DOM element.
	 * <p>Notice that the desktop's ID and UUID are the same.
	 * @param o - a desktop's ID, a widget, a widget's UUID, or a DOM element.
	 * If not specified, the default desktop is assumed.
	 */
	static override $<T extends Desktop>(dtid?: string | T): T
	static override $<T extends Desktop>(dtid: zk.Widget | undefined): T | undefined
	static override $<T extends Desktop>(dtid: T | undefined): T | undefined
	static override $<T extends Desktop>(dtid: string | T | undefined): T | undefined {
		let w: T | Widget | undefined;
		if (dtid) {
			if ((dtid instanceof Desktop))
				return dtid;

			w = Desktop.all[dtid as never];
			if (w)
				return w as T;

			w = Widget.$(dtid);
			for (; w; w = w.parent) {
				if (w.desktop)
					return w.desktop as T;
				if (w instanceof Desktop)
					return w as T;
			}
			return undefined;
		}

		if (w = Desktop._dt)
			return w as T;
		for (const dtid in Desktop.all)
			return Desktop.all[dtid] as T;
		return undefined;
	}

	/** A map of all desktops (readonly).
	 * The key is the desktop ID and the value is the desktop.
	 * @type Map
	 */
	static all: Record<string, Desktop> = {};

	/** @internal */
	static _ndt = 0; //used in au.js/dom.js
	/**
	 * Checks if any desktop becomes invalid, and removes the invalid desktops.
	 * This method is called automatically when a new desktop is added. Application developers rarely need to access this method.
	 * @param timeout - how many miliseconds to wait before doing the synchronization
	 * @returns the first desktop, or null if no desktop at all.
	 */
	static sync(timeout?: number): Desktop | undefined {
		var dts = Desktop.all, dt: Desktop;

		if (_syncdt) {
			clearTimeout(_syncdt);
			_syncdt = undefined;
		}

		if (timeout! >= 0) // (undefined >= 0) is false
			_syncdt = setTimeout(function () {
				_syncdt = undefined;
				Desktop.sync();
			}, timeout); //Liferay on IE will create widgets later
		else {
			for (var dtid in dts)
				if (!_exists(dt = dts[dtid]) && dt.firstChild) { //to be safe, don't remove if no child)
					delete dts[dtid];
					--Desktop._ndt;
					if (Desktop._dt == dt)
						Desktop._dt = undefined;
					zAu._rmDesktop(dt);
				}

			if (!Desktop._dt)
				for (var dtid in dts) {
					Desktop._dt = dts[dtid];
					break;
				}
		}
		return Desktop._dt;
	}
	/**
	 * Destroy the desktop
	 * @param zk - desktop
	 * @since 9.6.2
	 */
	static destroy(desktop?: zk.Desktop): void {
		if (desktop != null) {
			zAu._rmDesktop(desktop);
			delete zk.Desktop.all[desktop.id!];
			--zk.Desktop._ndt;
		}
	}
}

/** @internal */
export var _wgtutl = class { //internal utilities
	static setUuid(wgt: zk.Widget, uuid: string): void { //called by au.js
		if (!uuid)
			uuid = Widget.nextUuid();
		if (uuid != wgt.uuid) {
			var n = wgt.$n();
			if (n) {
				//Note: we assume RawId doesn't have sub-nodes
				if (!wgt.rawId)
					throw 'id immutable after bound'; //might have subnodes
				n.id = uuid;
				delete _binds[wgt.uuid];
				_binds[uuid] = wgt;
				wgt.clearCache();
			}
			wgt.uuid = uuid;
		}
	}
	//kids: whehter to move children of from to
	static replace(from: zk.Widget, to: zk.Widget, kids: boolean): void { //called by mount.js
		_replaceLink(from, to);
		from.parent = from.nextSibling = from.previousSibling = undefined;

		if (kids) {
			to.lastChild = from.lastChild;
			for (var p = to.firstChild = from.firstChild; p; p = p.nextSibling)
				p.parent = to;
			to.nChildren = from.nChildren;
			from.firstChild = from.lastChild = undefined;
			from.nChildren = 0;
		}
		from.nChildren = 0;
	}

	static autohide(): void { //called by effect.js
		if (!_floatings.length) {
			for (let n: HTMLElement | undefined; n = _hidden.shift();)
				n.style.visibility = n.getAttribute('z_ahvis') ?? '';
			return;
		}

		for (var tns = ['IFRAME', 'APPLET'], i = 2; i--;)
			l_nxtel:
			for (var ns = document.getElementsByTagName(tns[i]), j = ns.length; j--;) {
				var n = ns[j] as HTMLElement, $n = zk(n), visi;
				if ((!(visi = $n.isVisible(true)) && !_hidden.includes(n))
					|| (!i && !n.getAttribute('z_autohide') && !n.getAttribute('z.autohide'))) //check z_autohide (5.0) and z.autohide (3.6) if iframe
					continue; //ignore

				var tc = _topnode(n),
					hide = function (f: HTMLElement): boolean {
						var tf = _topnode(f);
						if (tf == tc || _zIndex(tf) < _zIndex(tc) || !$n.isOverlapped(f))
							return false;

						if (visi) {
							_hidden.push(n);
							try {
								n.setAttribute('z_ahvis', n.style.visibility);
							} catch (e) {
								zk.debugLog((e as Error).message ?? e);
							}
							n.style.visibility = 'hidden';
						}
						return true; //processed
					};

				for (var k = _floatings.length; k--;)
					if (hide(_floatings[k].node))
						continue l_nxtel;

				if ((_hidden as unknown[]).$remove(n))
					n.style.visibility = n.getAttribute('z_ahvis') || '';
			}
	}
};
zk._wgtutl = _wgtutl;

/**
 * A page.
 * Unlike the component at the server, a page is a widget.
 */
// zk scope
@WrapClass('zk.Page')
export class Page extends Widget {
	/** @internal */
	declare _applyMask?: zk.eff.Mask;
	//a virtual node that might have no DOM node and must be handled specially
	override z_virnd = true;

	/** @internal */
	override _style = 'width:100%;height:100%';
	/** The class name (`zk.Page`).
	 * @type String
	 */
	override className = 'zk.Page';
	/** The widget name (`page`).
	 * @type String
	 * @since 5.0.2
	 */
	override widgetName = 'page';

	/**
	 * @param props - the properties to assign to this page
	 * @param contained - whether this page is contained.
	 * By contained we mean this page is a top page (i.e., not included
	 * by the include widget) but it is included by other technologies,
	 * such as JSP.
	 */
	constructor(props?: Record<string, unknown>, contained?: boolean) {
		super(props);
		this._fellows = {};


		if (contained) Page.contained.push(this);
	}
	/** Generates the HTML fragment for this macro component.
	 * @defaultValue it generate DIV to enclose the HTML fragment
	 * of all child widgets.
	 * @param out - an array of HTML fragments.
	 */
	override redraw(out: string[]): void {
		out.push('<div', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div>');
	}

	static $redraw(this: zk.Widget, out: string[]): void {
		out.push('<div', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div>');
	}
	/** An array of contained pages (i.e., a standalone ZK page but included by other technology).
	 * For example, a ZUL age that is included by a JSP page.
	 * A contained page usually covers a portion of the browser window.
	 * @type Array an array of contained pages ({@link zk.Page})
	 */
	static contained: Page[] = [];
}

// zk scope;
//a fake page used in circumstance that a page is not available ({@link getPage})
@WrapClass('zk.Body')
export class Body extends Page {
	constructor(dt: Desktop) {
		super({});
		this.desktop = dt;
	}

	override $n(subId?: string): HTMLElement | undefined {
		return subId ? undefined : document.body;
	}

	override redraw(): void { return; }
}

/**
 * A native widget.
 * It is used mainly to represent the native componet created at the server.
 */
// zk scope
@WrapClass('zk.Native')
export class Native extends Widget {
	declare prolog?: string;
	declare domExtraAttrs;
	declare epilog?: string;
	declare value;

	//a virtual node that might have no DOM node and must be handled specially
	override z_virnd = true;

	override rawId = true; // Fix a side effect of ZK-5270 for B86-ZK-4055

	/** The class name (`zk.Native`)
	 * @type String
	 */
	override className = 'zk.Native';
	/** The widget name (`native`).
	 * @type String
	 * @since 5.0.2
	 */
	override widgetName = 'native';
	//rawId: true, (Bug 3358505: it cannot be rawId)

	override $n(subId?: string): HTMLElement | undefined {
		return !subId && this.id ? document.getElementById(this.id) as HTMLElement | undefined :
			super.$n(subId); // Bug ZK-606/607
	}
	override redraw(out: string[]): void {
		var s = this.prolog, p: zk.Widget | zk.Desktop | undefined;
		if (s) {
			//Bug ZK-606/607: hflex/vflex and many components need to know
			//child.$n(), so we have to generate id if the parent is not native
			//(and no id is assigned) (otherwise, zk.Native.$n() failed)
			if (/*(this instanceof zk.Native) //ZK-745
				&&*/ (this.rawId || (p = this.parent) && (!(p as {z_virnd?}).z_virnd || p.getMold() == 'nodom'))) { //z_virnd implies zk.Native, zk.Page and zk.Desktop
				var j = 0, len = s.length, cond, cc: string;
				for (cond = {whitespace: 1}; j < len; ++j) {
					if ((cc = s.charAt(j)) == '<')
						break; //found
					if (!zUtl.isChar(cc, cond as never)) {
						j = len; //not recognized => don't handle
						break;
					}
				}
				if (j < len) {
					cond = {upper: 1, lower: 1, digit: 1, '-': 1};
					while (++j < len)
						if (!zUtl.isChar(s.charAt(j), cond as never))
							break;
					s = s.substring(0, j) + ' id="' + (this.id || this.uuid) + '"' + s.substring(j);
				}
			}
			// B80-ZK-2957
			if (this.domExtraAttrs) {
				var postHTMLTag = !s.includes('/>') ? '>' : '/>';
				s = s.replace(postHTMLTag, this.domExtraAttrs_() + postHTMLTag);
			}
			// B65-ZK-1836 and B70-ZK-2622
			out.push(s.replace(/ sclass=/ig, ' class='));
			if (this.value && s.startsWith('<textarea'))
				out.push(this.value as string);
		}

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		s = this.epilog;
		if (s) out.push(/*safe*/ s);
	}

	static $redraw = Native.prototype.redraw;

	static replaceScriptContent(str: string): string {
		return str; // handle ZK-2622 on server side.
	}
}

/** A macro widget.
 * It is used mainly to represent the macro component created at the server.
 */
// zk scope
@WrapClass('zk.Macro')
export class Macro extends Widget {
	/** @internal */
	declare _fellows;
	/** The class name (`zk.Macro`).
	 * @type String
	 */
	override className = 'zk.Macro';
	/** The widget name (`macro`).
	 * @type String
	 * @since 5.0.2
	 */
	override widgetName = 'macro';
	// B70-ZK-2065: Replace span with div, because block-level element inside an inline element is not valid.
	/** @internal */
	_enclosingTag = 'div';

	constructor() {
		super();
		this._fellows = {};
	}

	/**
	 * @returns the tag name for this macro widget (such as `"div"` or `"span"`).
	 * @defaultValue div (since 7.0.1)
	 * @since 5.0.3
	 */
	getEnclosingTag(): string {
		return this._enclosingTag;
	}

	/** Sets the tag name for this macro widget
	 * @param enclosingTag - the tag name, such as div
	 * @since 5.0.3
	 */
	setEnclosingTag(enclosingTag: string): this {
		if (this._enclosingTag != enclosingTag) {
			this._enclosingTag = enclosingTag;
			this.rerender();
		}
		return this;
	}

	/**
	 * Generates the HTML fragment for this macro component.
	 * @defaultValue it generate DIV (since 7.0.1) to enclose the HTML fragment
	 * of all child widgets.
	 * @param out - an array of HTML fragments (String).
	 */
	override redraw(out: string[]): void {
		out.push('<', zUtl.encodeXML(this._enclosingTag), this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</', zUtl.encodeXML(this._enclosingTag), '>');
	}
}

/** @internal */
export namespace widget_global {
	export const zkservice = {
		/**
		 * Retrieves the service if any.
		 * @param n - the object to look for. If it is a string,
		 * it is assumed to be UUID, unless it starts with '$'.
		 * For example, `zkservice.$('uuid')` is the same as `zkservice.$('#uuid')`,
		 * and both look for a widget whose ID is 'uuid'. On the other hand,
		 * `zkservice.$('$id')` looks for a widget whose ID is 'id'.<br/>
		 * and `zkservice.$('.className')` looks for a widget whose CSS selector is 'className'.<br/>
		 * If it is an DOM element ({@link DOMElement}), it will look up
		 * which widget it belongs to.<br/>
		 * If the object is not a DOM element and has a property called
		 * `target`, then `target` is assumed.
		 * Thus, you can pass an instance of {@link jq.Event} or {@link zk.Event},
		 * and the target widget will be returned.
		 * @param opts - the options. Allowed values:
		 * <ul>
		 * <li>exact - id must exactly match uuid (i.e., uuid-xx ignored).
		 * It also implies strict</li>
		 * <li>strict - whether not to look up the parent node.(since 5.0.2)
		 * If omitted, false is assumed (and it will look up parent).</li>
		 * <li>child - whether to ensure the given element is a child element
		 * of the widget's main element ({@link Widget.$n}). In most cases, if ID
		 * of an element is xxx-yyy, the the element must be a child of
		 * the element whose ID is xxx. However, there is some exception
		 * such as the shadow of a window.</li>
		 * </ul>
		 * @since 8.0.0
		 */
		$(n: string | JQuery | HTMLElement | Widget,
			opts?: Partial<{exact: boolean; strict: boolean; child: boolean}>): Service | undefined {
			var widget = zk.Widget.$(n, opts);
			if (widget)
				return widget.$service();
			zk.error('Not found ZK Service with [' + String(n) + ']');
			return undefined;
		}
	};
}
function _fixCommandName(prefix: string, cmd: string, opts: zk.EventOptions, prop: string): void {
	if (opts[prop]) {
		var ignores = {};
		ignores[prefix + cmd] = true;
		opts[prop] = ignores;
	}
}
/**
 * A service utile widget
 * @since 8.0.0
 */
// zk scope
@WrapClass('zk.Service')
export class Service extends zk.Object {
	/** @internal */
	_aftercmd?: Record<string, CallableFunction[]>;
	/** @internal */
	declare _lastcmd?: string;

	$view?: zk.Widget;
	$currentTarget?: zk.Widget;
	constructor(widget: zk.Widget, currentTarget: zk.Widget) {
		super();
		this.$view = widget;
		this.$currentTarget = currentTarget;
		this._aftercmd = {};
	}

	/**
	 * Registers a callback after some command executed.
	 * @param command - the name of the command
	 * @param func - the function to execute
	 */
	after(cmd: string | CallableFunction, fn: CallableFunction): this {
		if (!fn && typeof cmd === 'function') {
			fn = cmd;
			cmd = this._lastcmd!;
		}

		if (typeof cmd === 'string') {
			var ac = this._aftercmd![cmd];
			if (!ac) this._aftercmd![cmd] = [fn];
			else
				ac.push(fn);
		}
		return this;
	}

	/**
	 * Unregisters a callback after some command executed.
	 * @param command - the name of the command
	 * @param func - the function to execute
	 */
	unAfter(cmd: string, fn: CallableFunction): this {
		var ac = this._aftercmd![cmd];
		for (var j = ac ? ac.length : 0; j--;) {
			if (ac[j] == fn)
				ac.splice(j, 1);
		}
		return this;
	}

	/**
	 * Destroy this binder.
	 */
	destroy(): void {
		this._aftercmd = undefined;
		this.$view = undefined;
		this.$currentTarget = undefined;
	}

	/**
	 * Post a command to the service
	 * @param command - the name of the command
	 * @param args - the arguments for this command. (the value should be json type)
	 * @param opts - a map of options to zk.Event, if any.
	 * @param timeout - the time (milliseconds) to wait before sending the request.
	 */
	command(
		cmd: string,
		args: unknown[],
		opts?: zk.EventOptions & {duplicateIgnore?: boolean; repeatIgnore?: boolean},
		timeout?: number
	): this {
		var wgt = this.$view;
		if (opts) {
			if (opts.duplicateIgnore)
				_fixCommandName('onAuServiceCommand$', cmd, opts, 'duplicateIgnore');
			if (opts.repeatIgnore)
				_fixCommandName('onAuServiceCommand$', cmd, opts, 'repeatIgnore');
		}
		zAu.send(new zk.Event(
			wgt,
			'onAuServiceCommand$' + cmd,
			{cmd, args},
			zk.copy({toServer: true}, opts)),
			timeout ?? 38,
		);
		this._lastcmd = cmd;
		return this;
	}
	$doAfterCommand(cmd: string, args?: unknown[]): void {
		const ac = this._aftercmd![cmd];
		for (let i = 0, j = ac ? ac.length : 0; i < j; i++)
			ac[i].bind(this)(args);
	}
}
/** A skipper is an object working with {@link zk.Widget#rerender}
 * to rerender portion(s) of a widget (rather than the whole widget).
 * It can improve the performance a lot if it can skip a lot of portions, such as a lot of child widgets.
 * <p>The skipper decides what to skip (i.e., not to rerender), detach the skipped portion(s), and attach them back after rerendering. Thus, the skipped portion won't be rerendered, nor unbound/bound.
 * <p>The skipper has to implement three methods, {@link skipped},
 * {@link skip} and {@link restore}. {@link skipped} is used to test whether a child widget shall be skipped.
 * {@link skip} and {@link restore} works together to detach and attach the skipped portions from the DOM tree. Here is how
 * {@link zk.Widget#rerender} uses these two methods:
```ts
rerender: function (skipper) {
  var skipInfo;
  if (skipper) skipInfo = skipper.skip(this);

  this.replaceHTML(this.node, null, skipper);

  if (skipInfo) skipper.restore(this, skipInfo);
}
```
 * <p>Since {@link zk.Widget#rerender} will pass the returned value of {@link skip} to {@link restore}, the skipper doesn't need to store what are skipped. That means, it is possible to have one skipper to serve many widgets. {@link nonCaptionSkipper} is a typical example.
 * <p>In additions to passing a skipper to {@link zk.Widget#rerender}, the widget has to implement the mold method to handle the skipper:
 * ```ts
 * function (skipper) {
 * 	var html = '<fieldset' + this.domAttrs_() + '>',
 * 		cap = this.caption;
 * 	if (cap) html += cap.redraw();
 *
 * 	html += '<div id="' + this.uuid + '$cave"' + this._contentAttrs() + '>';
 * 	if (!skipper)
 * 	for (var w = this.firstChild; w; w = w.nextSibling)
 * 		if (w != cap) html += w.redraw();
 * 	return html + '</div></fieldset>';
 * }
 * ```
 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component Development/Server-side/Property_Rendering">ZK Client-side Reference: Property Rendering</a>.
 */
// zk scope
@WrapClass('zk.Skipper')
export class Skipper extends zk.Object {
	/**
	 * @returns whether the specified child widget will be skipped by {@link skip}.
	 * @defaultValue returns if wgt.caption != child. In other words, it skip all children except the caption.
	 * @param wgt - the widget to re-render
	 * @param child - a child (descendant) of this widget.
	 */
	skipped(wgt: zk.Widget & {caption?: zk.Widget}, child: zk.Widget): boolean {
		return wgt.caption != child;
	}
	/** Skips all or subset of the descendant (child) widgets of the specified widget.
	 * <p>Notice that the `skipId` argument is not used by {@link zk.Widget#rerender}.
	 * Rather it is used to simplify the overriding of this method,
	 * such that the deriving class can call back this class and
	 * to pass a different ID to skip
	 *
	 * <p>If you don't want to pass a different ID (default: uuid + '-cave'),
	 * you can ignore `skipId`
	 * ```ts
	 * Object skip(zk.Widget wgt);
	 * ```
	 * @defaultValue it detaches all DOM elements whose parent element is
	 * `jq(skipId || (wgt.uuid + '-cave'), zk)`.

	 * @param wgt - the widget being rerendered.
	 * @param skipId - the ID of the element where all its descendant
	 * elements shall be detached by this method, and restored later by {@link restore}.
	 * If not specified, `uuid + '-cave'` is assumed.
	 */
	skip(wgt: zk.Widget, skipId?: string): HTMLElement | undefined {
		var skip = jq(skipId || wgt.getCaveNode(), zk)[0];
		if (skip && skip.firstChild) {
			var cf = zk.currentFocus,
				iscf = cf && cf.getInputNode;

			skip.parentNode?.removeChild(skip);
			//don't use jq to remove, since it unlisten events

			if (iscf && zk.chrome) //Bug ZK-1377 chrome will lost focus target after remove node
				zk.currentFocus = cf;

			return skip;
		}
	}

	/** Restores the DOM elements that are detached (i.e., skipped) by {@link skip}.
	 * @param wgt - the widget being re-rendered
	 * @param inf - the object being returned by {@link skip}.
	 * It depends on how a skipper is implemented. It is usually to carry the information about what are skipped
	 */
	restore(wgt: zk.Widget, skip: HTMLElement | undefined): void {
		if (skip) {
			var loc = jq(skip.id, zk)[0];
			for (var el; el = skip.firstChild;) {
				skip.removeChild(el);
				loc.appendChild(el);
			}
		}
	}
	/** @partial zk.Skipper
	 */
	//@{
	/** An instance of {@link zk.Skipper} that can be used to skip the rerendering of child widgets except the caption.
	 * <p>It assumes
	 * <ol>
	 * <li>The child widget not to skip can be found by the caption data member.</li>
	 * <li>The DOM elements to skip are child elements of the DOM element whose ID is widgetUUID$cave, where widgetUUID is the UUID of the widget being rerendered. </li>
	 * </ol>
	 * <p>In other words, it detaches (i.e., skipped) all DOM elements under widget.$n('cave').
```ts
setClosable: function (closable) {
 if (this._closable != closable) {
  this._closable = closable;
  if (this.node) this.rerender(zk.Skipper.nonCaptionSkipper);
 }
}
```
	 * @type zk.Skipper
	 */
	//nonCaptionSkipper: null
	//@};
	static nonCaptionSkipper = new Skipper();
}

/**
 * It's a object contains some interceptors.
 * You could use this to intercept a widget, and its node would change to comment node (start node and end node).
```ts
zk.$intercepts(zul.wgt.Idspace, zk.NoDOM);
```
 * @since 8.0.3
 */
interface NoDOMInterceptor extends zk.Widget {
	$getInterceptorContext$(): {
		stop: boolean;
		result: unknown;
		args: Parameters<zk.Widget['bind_']>;
	};
	/** @internal */
	_startNode?: Comment;
	/** @internal */
	_endNode?: Comment;
	/** @internal */
	_oldWgt?: zk.Widget;
}
// zk scope
export var NoDOM = class NoDOM {
	/** @internal */
	static bind_(this: NoDOMInterceptor): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			this.$supers('bind_', context.args);
			var node = this.$n('tmp'),
				/*safe*/ desc = zUtl.encodeXML(this.getZclass() + ' ' + this.uuid),
				startDesc = desc + ' start',
				endDesc = desc + ' end';
			if (node) {
				var start = document.createComment(startDesc),
					end = document.createComment(endDesc),
					parentNode = node.parentNode;
				parentNode?.insertBefore(start, node);
				var endNode: Node | undefined = node,
					lastChild = this.lastChild;
				if (lastChild) {
					if (lastChild.getMold() == 'nodom') {
						endNode = (lastChild as { _endNode?: Node })._endNode;
					} else {
						var lastChildNode = lastChild.$n();
						if (!lastChildNode)
							lastChildNode = jq(lastChild.uuid, zk)[0];
						if (lastChildNode)
							endNode = lastChildNode;
					}
				}
				parentNode?.insertBefore(end, endNode!.nextSibling);
				this._startNode = start;
				this._endNode = end;
				var id = '_z_nodomfs0',
					f = jq('#' + id);
				if (f.length == 0) {
					var fd = document.createElement('div');
					fd.id = id;
					document.body.appendChild(fd);
					f = jq('#' + id);
				}
				node.id = this.uuid + '-fake';
				f.append(node);
			}
			context.stop = true;
		}
	}

	/** @internal */
	static removeHTML_(this: NoDOMInterceptor, n: HTMLElement): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$(),
				//clear the dom between start node and end node
				node = this._startNode ? this._startNode.nextSibling : undefined,
				next: HTMLElement | undefined;
			while (node && node != this._endNode) {
				next = node.nextSibling as HTMLElement | undefined;
				jq(node).remove();
				node = next;
			}
			jq(this._startNode!).remove();
			jq(this._endNode!).remove();
			jq(n).remove();
			this.clearCache();
			context.stop = true;
		}
	}

	/** @internal */
	static setDomVisible_(this: NoDOMInterceptor, n: Element, visible: boolean, opts?: {display?: boolean; visibility?: boolean}): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			for (var w = this.firstChild; w; w = w.nextSibling) {
				if (visible)
					w.setDomVisible_(w.$n()!, w.isVisible(), opts);
				else
					w.setDomVisible_(w.$n()!, visible, opts);
			}
			context.stop = true;
		}
	}

	static isRealVisible(this: NoDOMInterceptor): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.result = this.isVisible() && (this.parent ? this.parent.isRealVisible() : true);
			context.stop = true;
		}
		return false;
	}

	/** @internal */
	static getFirstNode_(this: NoDOMInterceptor): HTMLElement | undefined {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.result = this._startNode;
			context.stop = true;
		}
		return undefined;
	}

	/** @internal */
	static insertChildHTML_(this: NoDOMInterceptor, child: zk.Widget, before?: zk.Widget, desktop?: Desktop): void {
		if (this.getMold() == 'nodom' && !before) {
			var context = this.$getInterceptorContext$();
			jq(this._endNode!).before(/*safe*/ child.redrawHTML_());
			child.bind(desktop);
			context.stop = true;
		}
	}

	static detach(this: NoDOMInterceptor): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			if (this.parent) this.parent.removeChild(this);
			else {
				var cf = zk.currentFocus;
				if (cf && zUtl.isAncestor(this, cf))
					zk.currentFocus = undefined;
				var n = this.$n();
				if (n) {
					this.removeHTML_(n);
					this.unbind();
				}
			}
			context.stop = true;
		}
	}
	/** @internal */
	static getOldWidget_(this: NoDOMInterceptor, n: HTMLElement): zk.Widget {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.result = this._oldWgt;
			context.stop = true;
		}
		return this._oldWgt!;
	}
	static replaceHTML(this: NoDOMInterceptor, n: HTMLElement, desktop: Desktop | undefined, skipper: Skipper
		, _trim_?: boolean, _callback_?: CallableFunction | CallableFunction[]): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			if (!desktop) {
				desktop = this.desktop;
				if (!zk.Desktop._ndt) zk.stateless();
			}

			var oldwgt = this.getOldWidget_(n);
			if (oldwgt) {
				//remove end and children
				for (var child = oldwgt.firstChild; child; child = child.nextSibling)
					oldwgt.removeChildHTML_(child, false);
				//unbind (w/o removal)
				oldwgt.unbind(skipper);
			}
			jq(this._endNode!).remove();
			jq(n as Element).remove();
			jq(this._startNode!).replaceWith(/*safe*/ this.redrawHTML_(skipper, _trim_));
			this.bind(desktop, skipper);

			if (!skipper) {
				if (!Array.isArray(_callback_))
					zUtl.fireSized(this);
				else {
					// for Bug ZK-2271, we delay this calculation
					var self = this;
					_callback_.push(function () {
						zUtl.fireSized(self);
					});
				}
			}
			context.result = this;
			context.stop = true;
		}
	}
	static replaceWidget(this: NoDOMInterceptor, newwgt: NoDOMInterceptor): void {
		if (this.getMold() == 'nodom') {
			newwgt._startNode = this._startNode;
			newwgt._endNode = this._endNode;
			newwgt._oldWgt = this;
		}
	}
	static $n(this: NoDOMInterceptor, subId?: string): HTMLElement | undefined {
		if (!subId && this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$(),
				n = this._node;
			if (!n && this.desktop && !this._nodeSolved) {
				this._node = n = jq(this.uuid + '-fake', zk)[0];
				this._nodeSolved = true;
			}
			context.result = n;
			context.stop = true;
		}
		return undefined;
	}
	static redraw(this: NoDOMInterceptor, out: string[]): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			out.push('<div id="', this.uuid, '-tmp', '" style="display:none"></div>');
			for (var w = this.firstChild; w; w = w.nextSibling)
				w.redraw(out);
			context.stop = true;
		}
	}
	/** @internal */
	static ignoreFlexSize_(this: NoDOMInterceptor, attr: string): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.stop = true;
			context.result = true;
		}
		return true;
	}

	/** @internal */
	static ignoreChildNodeOffset_(this: NoDOMInterceptor, attr: string): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.stop = true;
			context.result = true;
		}
		return true;
	}

	/** @internal */
	static isExcludedHflex_(this: NoDOMInterceptor): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.stop = true;
			context.result = true;
		}
		return true;
	}
	/** @internal */
	static isExcludedVflex_(this: NoDOMInterceptor): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.stop = true;
			context.result = true;
		}
		return true;
	}
};
/** @internal */
export namespace widget_global {
	export function zkopt(opts: Record<string, unknown>): void {
		for (var nm in opts) {
			var val = opts[nm];
			switch (nm) {
				case 'pd': zk.procDelay = val as number; break;
				case 'td': zk.tipDelay = val as number; break;
				case 'art': zk.resendTimeout = val as number; break;
				case 'dj': zk.debugJS = val as boolean; break;
				case 'sce': zk.sendClientErrors = val as boolean; break;
				case 'kd': zk.keepDesktop = val as boolean; break;
				case 'pf': zk.pfmeter = val as boolean; break;
				case 'ta': zk.timerAlive = val as boolean; break;
				case 'gd': zk.groupingDenied = val as boolean; break;
				case 'to':
					zk.timeout = val as number;
					zAu._resetTimeout();
					break;
				case 'ed':
					switch (val) {
						case 'e':
							zk.feature.ee = true;
						//fallthrough
						case 'p':
							zk.feature.pe = true;
					}
					break;
				case 'eu': zAu.setErrorURI(val as number); break;
				case 'ppos': zk.progPos = val as string; break;
				case 'hs': zk.historystate.enabled = val as boolean; break;
				case 'eup': zAu.setPushErrorURI(val as number); break;
				case 'resURI': zk.resourceURI = val as string;
			}
		}
	}
}
zk.DnD = DnD;
zk.Widget = Widget;
zk.$ = $;
zk.RefWidget = RefWidget;
zk.Desktop = Desktop;
zk.Page = Page;
zk.Body = Body;
zk.Native = Native;
zk.Macro = Macro;
zk.Service = Service;
zk.Skipper = Skipper;
zk.NoDOM = NoDOM;
zk.WrapClass = WrapClass;

zk.copy(window, widget_global);