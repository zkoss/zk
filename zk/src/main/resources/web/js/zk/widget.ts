/*global zkreg*/
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
import {default as zk, ZKObject} from './zk';

import {
	Callable,
	cast,
	NumberFieldValue,
	Offset,
	StringFieldValue
} from './types';
import type {Mask, Effect} from './effect';
import {JQZK, zjq} from './dom';
import type {DraggableOptions, Draggable} from './drag';
import type {Event, EventOptions} from './evt';
import {BooleanFieldValue, DOMFieldValue} from './types';
import {zWatch} from './evt';
import zFlex, {type FlexOrient} from './flex';
import type { SPush } from './cpsp/serverpush';

export interface RealVisibleOptions {
	dom?: boolean;
	until?: Widget;
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
}

var _binds: Record<string, Widget> = {}, //{uuid, wgt}: bind but no node
	_globals: Record<string, Array<Widget>> = {}, //global ID space {id, [wgt...]}
	_floatings: ({widget: Widget; node: HTMLElement})[] = [], //[{widget:w,node:n}]
	_nextUuid = 0,
	_domevtfnm: Record<string, string> = {}, //{evtnm, funnm}
	_domevtnm: Record<string, string> = {onDoubleClick: 'dblclick'}, //{zk-evt-nm, dom-evt-nm}
	_wgtcls: Record<string, typeof Widget> = {}, //{clsnm, cls}
	_hidden: HTMLElement[] = [], //_autohide
	_noChildCallback, _noParentCallback, //used by removeChild/appendChild/insertBefore
	_syncdt, //timer ID to sync destkops
	_rdque: Widget[] = [], _rdtid: NumberFieldValue, //async rerender's queue and timeout ID
	_ignCanActivate, //whether canActivate always returns true
	REGEX_DQUOT = /"/g; //jsdoc can't handle it correctly, so we have to put here

//Check if el is a prolog
function _isProlog(el: Node | null): boolean {
	return !!el && el.nodeType == 3 //textnode
		&& !el.nodeValue?.trim().length;
}

//Event Handling//
// eslint-disable-next-line no-undef
type JQueryEventHandler = (evt: JQuery.TriggeredEvent, ...args: unknown[]) => unknown;
type ZKEventHandler = (evt: Event, ...args: unknown[]) => unknown;
function _domEvtInf(wgt: Widget, evtnm: string, fn?: string | ZKEventHandler, keyword?: string): [string, JQueryEventHandler] { //proxy event listener
	if (typeof fn != 'function') {
		if (!fn && !(fn = _domevtfnm[evtnm]))
			_domevtfnm[evtnm] = fn = '_do' + evtnm.substring(2);

		var f = wgt[fn];
		if (typeof f != 'function')
			throw 'Listener ' + fn + ' not found in ' + wgt.className;
		fn = f;
	}

	var domn = _domevtnm[evtnm];
	if (!domn)
		domn = _domevtnm[evtnm] = evtnm.substring(2).toLowerCase();
	return [domn, _domEvtProxy(wgt, fn as ZKEventHandler, evtnm, keyword)];
}
function _domEvtProxy(wgt: Widget, f: ZKEventHandler, evtnm: string, keyword?: string): JQueryEventHandler {
	var fps = wgt._$evproxs, fp;
	if (!fps) wgt._$evproxs = fps = new WeakMap();
	if (keyword)
		f['__keyword'] = keyword;
	else if (fp = fps.get(f)) return fp;
	var fn = _domEvtProxy0(wgt, f, keyword);
	fps.set(f, fn);
	return fn;
}
function _domEvtProxy0(wgt: Widget, f: ZKEventHandler, keyword?: string): JQueryEventHandler {
	return function (evt) {
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
			if (!zk._cfByMD) zk.currentFocus = null;
			break;
		case 'click':
		case 'dblclick':
		case 'mouseup': //we cannot simulate mousedown:(
			if (zk.Draggable.ignoreClick())
				return;
		}

		// ZK 7.0 support the extra arguments for callback function
		var args;
		if (keyword) {
			args = [].slice.call(arguments);
			args.push(keyword);
		} else
			args = arguments;
		var ret = f.apply(wgt, args);
		if (ret === undefined) ret = zkevt['returnValue'];
		if (zkevt.domStopped) devt.stop();
		if (zkevt.stopped && devt.originalEvent) devt.originalEvent['zkstopped'] = true;
		return devt.type == 'dblclick' && ret === undefined ? false : ret;
	};
}

function _unlink(wgt: Widget, child: Widget): void {
	var p = child.previousSibling, n = child.nextSibling;
	if (p) p.nextSibling = n;
	else wgt.firstChild = n;
	if (n) n.previousSibling = p;
	else wgt.lastChild = p;
	child.nextSibling = child.previousSibling = child.parent = null;

	--wgt.nChildren;
}
//replace the link of from with the link of to (note: it assumes no child)
function _replaceLink(from: Widget, to: Widget): void {
	var p = to.parent = from.parent,
		q = to.previousSibling = from.previousSibling;
	if (q) q.nextSibling = to;
	else if (p) p.firstChild = to;

	q = to.nextSibling = from.nextSibling;
	if (q) q.previousSibling = to;
	else if (p) p.lastChild = to;
}

function _fixBindLevel(wgt: Widget, v: number): void {
	wgt.bindLevel = v++;
	for (let w = wgt.firstChild; w; w = w.nextSibling)
		_fixBindLevel(w, v);
}

function _addIdSpace(wgt: Widget): void {
	if (wgt._fellows) wgt._fellows[wgt.id as string] = wgt;
	var p = wgt.parent;
	if (p) {
		p = p.$o();
		if (p) p._fellows[wgt.id as string] = wgt;
	}
}
function _rmIdSpace(wgt: Widget): void {
	if (wgt._fellows) delete wgt._fellows[wgt.id as string];
	var p = wgt.parent;
	if (p) {
		p = p.$o();
		if (p) delete p._fellows[wgt.id as string];
	}
}
function _addIdSpaceDown(wgt: Widget): void {
	var ow = wgt.parent;
	ow = ow ? ow.$o() : null;
	if (ow)
		_addIdSpaceDown0(wgt, ow);
}
function _addIdSpaceDown0(wgt: Widget, owner: Widget): void {
	if (wgt.id) owner._fellows[wgt.id] = wgt;
	if (!wgt._fellows)
		for (let w = wgt.firstChild; w; w = w.nextSibling)
			_addIdSpaceDown0(w, owner);
}
function _rmIdSpaceDown(wgt: Widget): void {
	var ow = wgt.parent;
	ow = ow ? ow.$o() : null;
	if (ow)
		_rmIdSpaceDown0(wgt, ow);
}
function _rmIdSpaceDown0(wgt: Widget, owner: Widget): void {
	if (wgt.id)
		delete owner._fellows[wgt.id];
	if (!wgt._fellows)
		for (let w = wgt.firstChild; w; w = w.nextSibling)
			_rmIdSpaceDown0(w, owner);
}
//check if a desktop exists
function _exists(wgt: Widget): boolean {
	if (document.getElementById(wgt.uuid as string)) //don't use $n() since it caches
		return true;

	for (let w = wgt.firstChild; w; w = w.nextSibling)
		if (_exists(w))
			return true;
	return false;
}

function _fireClick(wgt: Widget, evt: Event): boolean {
	if (!wgt.shallIgnoreClick_(evt)
		&& !wgt.fireX(evt).stopped && evt.shallStop) {
		evt.stop();
		return false;
	}
	return !evt.stopped;
}

function _rmDom(wgt: Widget, n: HTMLElement | HTMLElement[]): void {
	//TO IMPROVE: actions_ always called if removeChild is called, while
	//insertBefore/appendChild don't (it is called only if attached by au)
	//NOT CONSISTENT! Better to improve in the future
	var act;
	if (wgt.isVisible() && (act = wgt.actions_['hide'])) {
		wgt._rmAftAnm = function () {
			jq(n).remove();
		};
		if (Array.isArray(n)) {
			for (let e of n) {
				e.style.visibility = '';
			}
		} else {
			n.style.visibility = ''; //Window (and maybe other) might turn it off
		}
		act[0].call(wgt, n, act[1]);
	} else
		jq(n).remove();
}

//whether it is controlled by another dragControl
//@param invoke whether to invoke dragControl
function _dragCtl(wgt: Widget, invoke?: boolean): boolean {
	var p;
	return wgt && (p = wgt.parent) && p.dragControl && (!invoke || p.dragControl(wgt));
}

//backup current focus
type CurrentFocusInfo = {focus: Widget; range: Offset | null} | null;
function _bkFocus(wgt: Widget): CurrentFocusInfo {
	var cf = zk.currentFocus;
	if (cf && zUtl.isAncestor(wgt, cf)) {
		zk.currentFocus = null;
		return {focus: cf, range: _bkRange(cf)};
	}
	return null;
}
function _bkRange(wgt: Widget): [number, number] | null {
	if (zk.ie && zk.ie < 11 && zk.cfrg) { //Bug ZK-1377
		var cfrg = zk.cfrg;
		delete zk.cfrg;
		return cfrg;
	}
	let input = wgt.getInputNode && wgt.getInputNode();
	return input ? zk(input).getSelectionRange() : null;
}
//restore focus
function _rsFocus(cfi: CurrentFocusInfo): void {
	var cf;
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

function _listenFlex(wgt: Widget & {_flexListened?: boolean}): void {
	if (!wgt._flexListened) {
		if (zk.ie) // not to use css flex in ie
			wgt._cssflex = false;
		var parent = wgt.parent,
			cssFlexEnabled = wgt._cssflex && parent && (parent.$instanceof(zk.Page) || parent.getFlexContainer_() != null);
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
function _unlistenFlex(wgt: Widget): void {
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

/** @class zk.DnD
 * Drag-and-drop utility.
 * It is the low-level utility reserved for overriding for advanced customization.
 */
// zk scope
export let DnD = { //for easy overriding
	/** Returns the drop target from the event, or the element from the event's
	 * ClientX and ClientY. This function is to fix IE9~11, Edge, and Firefox which
	 * will receive a wrong target from the mouseup event.
	 *
	 * @since 8.0.2
	 * @param jq.Event evt the DOM event
	 * @param zk.Draggable drag the draggable controller
	 * @return zk.Widget
	 */
	getDropTarget(evt: Event, drag?): Widget {
		var wgt;
		// Firefox's bug -  https://bugzilla.mozilla.org/show_bug.cgi?id=1259357
		if ((zk.ff && jq(evt.domTarget!).css('overflow') !== 'visible') ||
			// IE 9~11 and Edge may receive a wrong target when dragging with an Image.
			(((zk.ie && zk.ie > 8) || zk.edge_legacy) && jq.nodeName(evt.domTarget!, 'img'))) {
			var n = document.elementFromPoint(evt.domEvent!.clientX || 0, evt.domEvent!.clientY || 0);
			if (n)
				wgt = zk.$(n);
		} else {
			wgt = evt.target;
		}
		return wgt;
	},
	/** Returns the widget to drop to.
	 * @param zk.Draggable drag the draggable controller
	 * @param Offset pt the mouse pointer's position.
	 * @param jq.Event evt the DOM event
	 * @return zk.Widget
	 */
	getDrop(drag, pt: Offset, evt: Event): Widget | null {
		var wgt = this.getDropTarget(evt, drag);
		return wgt ? wgt.getDrop_(drag.control) : null;
	},
	/** Ghost the DOM element being dragged.
	 * @param zk.Draggable drag the draggable controller
	 * @param Offset ofs the offset of the returned element (left/top)
	 * @param String msg the message to show inside the returned element
	 * @return DOMElement the element representing what is being dragged
	 */
	ghost(drag, ofs: Offset, msg?: string): HTMLElement {
		if (msg != null) {
			if (msg)
				msg = '<span class="z-drop-text">' + msg + '</span>';
			jq(document.body).append(
				'<div id="zk_ddghost" class="z-drop-ghost z-drop-disallow" style="position:absolute;top:'
				+ ofs[1] + 'px;left:' + ofs[0] + 'px;"><div class="z-drop-content"><span id="zk_ddghost-img" class="z-drop-icon"></span>' + msg + '</div></div>');
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
function DD_cleanLastDrop(drag): void {
	if (drag) {
		var drop;
		if (drop = drag._lastDrop) {
			drag._lastDrop = null;
			drop.dropEffect_();
		}
		drag._lastDropTo = null;
	}
}
function DD_pointer(evt: Event, height: number): Offset {
	return [evt.pageX + 7, evt.pageY + 5];
}
function DD_enddrag(drag, evt: Event): void {
	DD_cleanLastDrop(drag);
	var pt: zk.Offset = [evt.pageX, evt.pageY],
		wgt = zk.DnD.getDrop(drag, pt, evt);
	if (wgt) wgt.onDrop_(drag, evt);
}
function DD_dragging(drag, pt, evt: Event): void {
	var dropTo;
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
function DD_ghosting(drag, ofs: zk.Offset, evt: Event): HTMLElement {
	return drag.control.cloneDrag_(drag, DD_pointer(evt, jq(drag.node).height() || 0));
}
function DD_endghosting(drag, origin): void {
	drag.control.uncloneDrag_(drag);
	drag._dragImg = null;
}
function DD_constraint(drag, pt, evt: Event): zk.Offset {
	return DD_pointer(evt, jq(drag.node).height() || 0);
}
function DD_ignoredrag(drag, pt, evt: Event): boolean {
	//ZK 824:Textbox dragging issue with Listitem
	//since 5.0.11,6.0.0 introduce evt,drag to the wgt.ignoreDrag_() to provide more information.
	return drag.control.ignoreDrag_(pt, evt, drag);
}

function _topnode(n: HTMLElement): HTMLElement | null {
	let curr: HTMLElement | null = n;
	for (var body = document.body; curr && curr != body; curr = curr.parentElement) { //no need to check vparentNode
		var position = jq(curr).css('position');
		if (position == 'absolute' || position == 'relative')
			return curr;
	}
	return null;
}
function _zIndex(n: HTMLElement | null): number {
	return n ? zk.parseInt(n.style.zIndex) : 0;
}

function _getFirstNodeDown(wgt: Widget): HTMLElement | null {
	var n = wgt.$n();
	if (n) return n;
	for (var w = wgt.firstChild; w; w = w.nextSibling) {
		n = w.getFirstNode_();
		if (n) return n;
	}
	return null;
}
//Returns if the specified widget's visibility depends the self widget.
function _floatVisibleDependent(self: Widget, wgt: Widget): boolean {
	for (let w: Widget | null = wgt; w; w = w.parent) {
		if (w == self) return true;
		else if (!w.isVisible()) break;
	}
	return false;
}

function _fullScreenZIndex(zi: number): number {
	var pseudoFullscreen;
	if (document.fullscreenElement) {
		pseudoFullscreen = ':fullscreen';
	} else if (document['mozFullScreen']) {
		//pseudoFullscreen = ":-moz-full-screen";
		//Firefox return zindex by scientific notation "2.14748e+9"
		//use zk.parseFloat() will get 2147480000, so return magic number directly.
		return 2147483648;
	} else if (document['webkitIsFullScreen']) {
		pseudoFullscreen = ':-webkit-full-screen';
	}
	if (pseudoFullscreen) {
		var fsZI = jq.css(jq(pseudoFullscreen)[0], 'zIndex');
		return fsZI == 'auto' ? 2147483648 : parseInt(fsZI) + 1;
	}
	return zi;
}

//Returns the topmost z-index for this widget
function _topZIndex(wgt: Widget): number {
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

function _prepareRemove(wgt: Widget, ary: HTMLElement[]): void {
	for (let w = wgt.firstChild; w; w = w.nextSibling) {
		var n = w.$n();
		if (n) ary.push(n);
		else _prepareRemove(w, ary);
	}
}

//render the render defer (usually controlled by server)
function _doDeferRender(wgt: Widget & {_z$rd?}): void {
	if (wgt._z$rd) { //might be redrawn by forcerender
		delete wgt._z$rd;
		wgt._norenderdefer = true;
		wgt.replaceHTML('#' + wgt.uuid, wgt.parent ? wgt.parent.desktop : null, null, true);
		if (wgt.parent)
			wgt.parent.onChildRenderDefer_(wgt);
	}
}

//invoke rerender later
function _rerender(wgt: Widget, timeout: number): void {
	if (_rdtid)
		clearTimeout(_rdtid);
	wgt._rerendering = true;
	_rdque.push(wgt);
	_rdtid = window.setTimeout(_rerender0, timeout);
}
function _rerender0(): void {
	_rdtid = null;
	l_out:
	for (var wgt; wgt = _rdque.shift();) {
		if (!wgt.desktop) {
			delete wgt._rerendering;
			continue;
		}

		for (var j = _rdque.length; j--;) {
			if (zUtl.isAncestor(wgt, _rdque[j]))
				_rdque.splice(j, 1); //skip _rdque[j]
			else if (zUtl.isAncestor(_rdque[j], wgt))
				continue l_out; //skip wgt
		}

		wgt.rerender(-1);
		delete wgt._rerendering;
	}
}
/* Bug ZK-2281 */
function _rerenderNow(wgt: Widget, skipper?: Skipper | null): void {
	var rdque: Widget[] = [];
	for (var j = _rdque.length; j--;)
		if (zUtl.isAncestor(wgt, _rdque[j])) {// if wgt's children or itself is in a rerender queue
			if (!skipper || !skipper.skipped(wgt, _rdque[j]))
				rdque = rdque.concat(_rdque.splice(j, 1));
		}

	// just in case
	if (!_rdque.length && _rdtid) {
		clearTimeout(_rdtid);
		_rdtid = null;
	}

	l_out2:
		for (var w; w = rdque.shift();) {
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
function _rerenderDone(wgt: Widget, skipper?: Skipper | null/* Bug ZK-1463 */): void {
	for (var j = _rdque.length; j--;)
		if (zUtl.isAncestor(wgt, _rdque[j])) {
			if (!skipper || !skipper.skipped(wgt, _rdque[j]))
				_rdque.splice(j, 1);
		}
}

function _markCache(cache, visited, visible: boolean): boolean {
	if (cache)
		for (var p; p = visited.pop();)
			cache[p.uuid] = visible;
	return visible;
}

const _dragoptions: Partial<DraggableOptions> = {
	starteffect: zk.$void, //see bug #1886342
	endeffect: DD_enddrag, change: DD_dragging,
	ghosting: DD_ghosting, endghosting: DD_endghosting,
	constraint: DD_constraint, //s.t. cursor won't be overlapped with ghosting
	ignoredrag: DD_ignoredrag,
	zIndex: 88800
};

/** A widget, i.e., an UI object.
 * Each component running at the server is associated with a widget
 * running at the client.
 * <p>Refer to <a href="http://books.zkoss.org/wiki/ZK_Component_Development_Essentials">ZK Component Development Essentials</a>
 * and <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference">ZK Client-side Reference</a>
 * for more information.
 * <p>Notice that, unlike the component at the server, {@link zk.Desktop}
 * and {@link zk.Page} are derived from zk.Widget. It means desktops, pages and widgets are in a widget tree.
 * @disable(zkgwt)
 */
// zk scope
export class Widget extends ZKObject {
	declare public _loaded?: boolean; // zul.mesh.MeshWidget
	declare public _index?: number; // zul.mesh.MeshWidget
	declare public _navWidth?: number; // zul.mesh.Paging
	declare public _uplder?: zul.Upload | null;
	declare public _autodisable_self?: boolean;
	declare public _uploading?: boolean;

	declare public offsetWidth?;
	declare public offsetHeight?;
	declare public blankPreserved?: boolean;
	// FIXME: _scrollbar?: any;
	// FIXME: $binder(): zk.Binder | null;

	declare public getInputNode?: () => HTMLInputElement | null | undefined;

	declare public z_rod?: boolean | number;
	declare public _rodKid?: boolean;
	declare public _node: DOMFieldValue;
	declare public _nodeSolved;
	declare public _rmAftAnm;
	declare public _$evproxs;
	declare public _fellows;
	declare public _norenderdefer;
	declare public _rerendering;
	declare public domExtraAttrs;
	declare public _flexListened;
	declare public _cssFlexApplied;
	declare public _beforeSizeHasScroll;
	declare public doAfterProcessRerenderArgs;
	declare public _vflex: StringFieldValue | boolean;
	declare public _hflex: StringFieldValue | boolean;
	declare public _flexFixed;
	declare public _nvflex;
	declare public _nhflex?: number;
	declare public _hflexsz?: number;
	declare public _vflexsz?: number;

	declare private _binding;
	declare public rawId;
	declare private _$service;
	declare private _tooltiptex;
	declare protected childReplacing_;
	declare private _userZIndex;
	declare private _zIndex;
	declare private z_isDataHandlerBound;
	declare protected _drag: Draggable | null;
	declare private _preWidth;
	declare private _preHeight;
	declare private _action: StringFieldValue;
	declare protected _tabindex: NumberFieldValue;
	declare private _draggable: StringFieldValue;
	declare private _asaps: Record<string, unknown>;
	declare private _lsns: Record<string, unknown & {priority: number}[]>;
	declare private _bklsns: Record<string, unknown>;
	declare protected _subnodes: Record<string, HTMLElement | string | null | undefined>;
	declare private _subzcls: Record<string, string>;
	declare private _sclass: StringFieldValue;
	declare protected _zclass: StringFieldValue;
	declare public _width: StringFieldValue;
	declare public _height: StringFieldValue;
	declare private _left: StringFieldValue;
	declare private _top: StringFieldValue;
	declare public _tooltiptext: StringFieldValue;
	declare private _droppable: StringFieldValue;
	declare private _dropTypes: string[] | null;
	declare private _fitSizeListened: boolean | undefined;

	declare public static _importantEvts;
	declare public static _duplicateIgnoreEvts;
	declare public static _repeatIgnoreEvts;
	declare public static molds;

	public _visible?: boolean = true;
	protected _mold = 'default';
	protected _style: StringFieldValue;
	private _renderdefer = -1;

	public _cssflex = true;

	public actions_ = {};

	/** The number of children (readonly).
	 * @type int
	 */
	public nChildren = 0;
	/** The bind level (readonly)
	 * The level in the widget tree after this widget is bound to a DOM tree ({@link #bind_}).
	 * For example, a widget's bind level is one plus the parent widget's
	 * <p>It starts at 0 if it is the root of the widget tree (a desktop, zk.Desktop), then 1 if a child of the root widget, and son on. Notice that it is -1 if not bound.
	 * <p>It is mainly useful if you want to maintain a list that parent widgets is in front of (or after) child widgets.
	 * bind level.
	 * @type int
	 */
	public bindLevel = -1;
	/** The class name of the widget.
	 * For example, zk.Widget's class name is "zk.Widget", while
	 * zul.wnd.Window's "zul.wnd.Window".
	 * <p>Notice that it is available if a widget class is loaded by WPD loader (i.e., specified in zk.wpd). If you create a widget class dynamically,
	 * you have to invoke {@link #register} to make this member available.
	 * On the other hand, {@link zk.Object#$class} is available for all objects
	 * extending from {@link zk.Object}.
	 *
	 * @see #widgetName
	 * @type String
	 */
	public className = 'zk.Widget';
	/** The widget name of the widget.
	 * It is the same as <code>this.className.substring(this.className.lastIndexOf('.') + 1).toLowerCase()</code>.
	 * For example, if {@link #className} is zul.wnd.Window, then
	 * {@link #widgetName} is window.
	 * <p>Notice that {@link #className} is unique while {@link #widgetName}
	 * is not necessary unique.
	 * @see #className
	 * @type String
	 * @since 5.0.2
	 */
	public widgetName = 'widget';
	/** The AU tag of this widget.
	 * The AU tag tag is used to tag the AU requests sent by the peer widget.
	 * For instance, if the AU tag is <code>xxx,yyy</code> and the desktop's
	 * request path ({@link Desktop#requestPath}) is <code>/foo.zul</code>, then
	 * the URL of the AU request will contain <code>/_/foo.zul/xxx,yyy</code>,.
	 * <p>Default: null.
	 * @type String
	 * @since 6.0.0
	 */
	public autag: StringFieldValue = null;

	private _floating = false;

	/** The first child, or null if no child at all (readonly).
	 * @see #getChildAt
	 * @type zk.Widget
	 */
	public firstChild: null | Widget = null;
	/** The last child, or null if no child at all (readonly).
	 * @see #getChildAt
	 * @type zk.Widget
	 */
	public lastChild: null | Widget = null;
	/** The parent, or null if this widget has no parent (readonly).
	 * @type zk.Widget
	 */
	public parent: null | Widget = null;
	/** The next sibling, or null if this widget is the last child (readonly).
	 * @type zk.Widget
	 */
	public nextSibling: null | Widget = null;
	/** The previous sibling, or null if this widget is the first child (readonly).
	 * @type zk.Widget
	 */
	public previousSibling: null | Widget = null;
	/** The desktop that this widget belongs to (readonly).
	 * It is set when it is bound to the DOM tree.
	 * <p>Notice it is always non-null if bound to the DOM tree, while
	 * {@link #$n()} is always non-null if bound. For example, {@link zul.utl.Timer}.
	 * <p>It is readonly, and set automatically when {@link #bind_} is called.
	 * @type zk.Desktop
	 */
	public desktop: null | Desktop = null;
	/** The identifier of this widget, or null if not assigned (readonly).
	 * It is the same as {@link #getId}.
	 * <p>To change the value, use {@link #setId}.
	 * @type String the ID
	 */
	public id: StringFieldValue;
	/** Whether this widget has a peer component (readonly).
	 * It is set if a widget is created automatically to represent a component
	 ( at the server. On the other hand, it is false if a widget is created
	 * by the client application (by calling, say, <code>new zul.inp.Textox()</code>).
	 * @type boolean
	 */
	public inServer = false;
	/** The UUID. Don't change it if it is bound to the DOM tree, or {@link #inServer} is true.
	 * Developers rarely need to modify it since it is generated automatically.
	 * @type String
	 */
	public uuid = '';
	/** Indicates an invocation of {@link #appendChild} is made by
	 * {@link #insertBefore}.
	 * @type boolean
	 */
	protected insertingBefore_ = false;

	/** A map of objects that are associated with this widget, and
	 * they shall be removed when this widget is unbound ({@link #unbind}).
	 * <p>The key must be an unique name of the object, while the value
	 * must be an object that implement the destroy method.
	 * <p>When {@link #unbind_} is called, <code>destroy()</code> is
	 * called for each object stored in this map. Furthermore,
	 * if the visibility of this widget is changed, and the object implements
	 * the sync method, then <code>sync()</code> will be called.
	 * Notice that the sync method is optional. It is ignored if not implemented.
	 * <p>It is useful if you implement an effect, such as shadow, mask
	 * and error message, that is tightly associated with a widget.
	 * @type Map
	 */
	public effects_: Record<string, Effect> | null;

	/** The weave controller that is used by ZK Weaver.
	 * It is not null if it is created and controlled by ZK Weaver.
	 * In other words, it is called in the Design Mode if $weave is not null.
	 * @type Object
	 */
	public $weave = null;

	/** The constructor.
	 * For example,
<pre><code>
new zul.wnd.Window({
  border: 'normal',
  title: 'Hello World',
  closable: true
});
</code></pre>
	 * @param Map props the properties to be assigned to this widget.
	 */
	public constructor(props?: Record<string, unknown> | typeof zkac) {
		super();
		this.className = this.constructor.prototype.className;
		this.widgetName = this.constructor.prototype.widgetName;
		this._asaps = {}; //event listened at server
		this._lsns = {}; //listeners(evtnm,listener)
		this._bklsns = {}; //backup for listeners by setListeners
		this._subnodes = {}; //store sub nodes for widget(domId, domNode)
		this.effects_ = {};
		this._subzcls = {}; // cache the zclass + subclass name, like zclass + '-hover'

		//zkac is a token used by create() in mount.js for optimizing performance
		if (props !== zkac) {
			//if props.$oid, it must be an object other than {} so ignore
			if (props && typeof props == 'object' && !props.$oid)
				for (var nm in props)
					this.set(nm, props[nm]);

			if ((zk.spaceless || this.rawId) && this.id)
				this.uuid = this.id; //setId was called
			if (!this.uuid)
				this.uuid = Widget.nextUuid();
		}
	}

	/** Sets this widget's mold. A mold is a template to render a widget.
	 * In other words, a mold represents a visual presentation of a widget. Depending on implementation, a widget can have multiple molds.
	 * <p>Default: <code>default</code>
	 * @param String mold the mold
	 */
	public setMold(mold: string): void {
		if (this._mold != mold) {
			this._mold = mold;
			this.rerender();
		}
	}

	/** Returns this widget's mold. A mold is a template to render a widget.
	 * In other words, a mold represents a visual presentation of a widget. Depending on implementation, a widget can have multiple molds.
	 * @return String
	 */
	public getMold(): string {
		return this._mold;
	}

	/** Sets the CSS style of this widget.
	 * <p>Default: null
	 * @param String style the CSS style
	 * @see #getStyle
	 * @see #setSclass
	 * @see #setZclass
	 */
	public setStyle(style: string): void {
		if (this._style != style) {
			this._style = style;
			this.updateDomStyle_();
		}
	}

	/** Returns the CSS style of this widget
	 * @return String
	 * @see #setStyle
	 * @see #getSclass
	 * @see #getZclass
	 */
	public getStyle(): StringFieldValue {
		return this._style;
	}

	/** Sets the CSS class of this widget.
	 *<p>Default: null.
	 *<p>The default styles of ZK components doesn't depend on sclass at all. Rather, setSclass is provided to perform small adjustment, e.g., changing only the font size. In other words, the default style is still applied if you change sclass.
	 *<p>To replace the default style completely, use {@link #setZclass} instead.
	 *<p>The real CSS class is a concatenation of {@link #getZclass} and {@link #getSclass}.
	 * @param String sclass the style class
	 * @see #getSclass
	 * @see #setZclass
	 * @see #setStyle
	 */
	public setSclass(sclass: string): void {
		if (this._sclass != sclass) {
			this._sclass = sclass;
			this.updateDomClass_();
		}
	}

	/** Returns the CSS class of this widget.
	 * @return String
	 * @see #setSclass
	 * @see #getZclass
	 * @see #getStyle
	 */
	public getSclass(): StringFieldValue {
		return this._sclass;
	}

	/** Sets the ZK Cascading Style class(es) for this widget. It is the CSS class used to implement a mold of this widget. n implementation It usually depends on the implementation of the mold ({@link #getMold}).
	 * <p>Default: null but an implementation usually provides a default class, such as z-button.
	 * <p>Calling setZclass with a different value will completely replace the default style of a widget.
	 * Once you change it, all default styles are gone.
	 * If you want to perform small adjustments, use {@link #setSclass} instead.
	 * <p>The real CSS class is a concatenation of {@link #getZclass} and
	 * {@link #getSclass}.
	 * @param String zclass the style class used to apply the whole widget.
	 * @see #getZclass
	 * @see #setSclass
	 * @see #setStyle
	 */
	public setZclass(zclass: string): void {
		if (this._zclass != zclass) {
			this._zclass = zclass;
			this._subzcls = {}; // reset
			this.rerender();
		}
	}

	/** Returns the ZK Cascading Style class(es) for this widget.
	 * @return String
	 * @see #setZclass
	 * @see #getSclass
	 * @see #getStyle
	 */
	public getZclass(): string {
		var zcls = this._zclass;
		return zcls != null ? zcls : 'z-' + this.widgetName;
	}

	/** Sets the width of this widget.
	 * @param String width the width. Remember to specify 'px', 'pt' or '%'.
	 * An empty or null value means "auto"
	 */
	public setWidth(width: string | null): void {
		if (this._width != width) {
			this._width = width;
			if (!this._nhflex) {
				var n = this.$n();
				if (n) n.style.width = width || '';
			}
		}
	}
	/** Returns the width of this widget.
	 * @return String
	 * @see #getHeight
	 */
	public getWidth(): StringFieldValue {
		return this._width;
	}
	/** Sets the height of this widget.
	 * @param String height the height. Remember to specify 'px', 'pt' or '%'.
	 * An empty or null value means "auto"
	 */
	public setHeight(height: string | null): void {
		if (this._height != height) {
			this._height = height;
			if (!this._nvflex) {
				var n = this.$n();
				if (n) n.style.height = height || '';
			}
		}
	}
	/** Returns the height of this widget.
	 * @return String
	 * @see #getWidth
	 */
	public getHeight(): StringFieldValue {
		return this._height;
	}

	/** Sets the left of this widget.
	 * @param String left the left. Remember to specify 'px', 'pt' or '%'.
	 * An empty or null value means "auto"
	 */
	public setLeft(left: string): void {
		if (this._left != left) {
			this._left = left;
			var n = this.$n();
			if (n) n.style.left = left || '';
		}
	}

	/** Returns the left of this widget.
	 * @return String
	 * @see #getTop
	 */
	public getLeft(): StringFieldValue {
		return this._left;
	}

	/** Sets the top of this widget.
	 * If you want to specify <code>bottom</code>, use {@link #setStyle} instead.
	 * For example, <code>setStyle("bottom: 0px");</code>
	 * @param String top the top. Remember to specify 'px', 'pt' or '%'.
	 * An empty or null value means "auto"
	 */
	public setTop(top: string): void {
		if (this._top != top) {
			this._top = top;
			var n = this.$n();
			if (n) n.style.top = top || '';
		}
	}

	/** Returns the top of this widget.
	 * @return String
	 * @see #getLeft
	 */
	public getTop(): StringFieldValue {
		return this._top;
	}
	/** Sets the tooltip text of this widget.
	 * <p>Default implementation of setTooltiptext: update the title attribute of {@link #$n}
	 * @param String title the tooltip text
	 */
	public setTooltiptext(tooltiptext: string): void {
		if (this._tooltiptext != tooltiptext) {
			this._tooltiptex = tooltiptext;
			var n = this.$n();
			// ZK-676 , ZK-752
			if (n) n.title = this._tooltiptex || '';
		}
	}
	/** Returns the tooltip text of this widget.
	 * @return String
	 */
	public getTooltiptext(): StringFieldValue {
		return this._tooltiptext;
	}

	/** Sets the identifier, or a list of identifiers of a droppable type for this widget.
	 * <p>Default: null
	 * <p>The simplest way to make a component droppable is to set this attribute to "true". To disable it, set this to "false" (or null).
	 * <p>If there are several types of draggable objects and this widget accepts only some of them, you could assign a list of identifiers that this widget accepts, separated by comma.
	 * <p>For example, if this component accepts dg1 and dg2, then assign "dg1, dg2" to this attribute.
	 * @param String droppable "false", null or "" to denote not-droppable; "true" for accepting any draggable types; a list of identifiers, separated by comma for identifiers of draggables this widget accept (to be dropped in).
	 * @return zk.Widget this widget
	 */
	public setDroppable(droppable: StringFieldValue): void {
		droppable = droppable && 'false' != droppable ? droppable : null;
		if (this._droppable != droppable) {
			this._droppable = droppable;

			var dropTypes;
			if (droppable && droppable != 'true') {
				dropTypes = droppable.split(',');
				for (var j = dropTypes.length; j--;)
					if (!(dropTypes[j] = dropTypes[j].trim()))
						dropTypes.splice(j, 1);
			}
			this._dropTypes = dropTypes;
		}
	}
	/** Returns the identifier, or a list of identifiers of a droppable type for this widget, or null if not droppable.
	 * @return String
	 */
	public getDroppable(): StringFieldValue {
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
	 * @see #setHflex
	 * @see #getVflex
	 * @param String flex the vertical flex hint.
	 */
	public setVflex(vflex: StringFieldValue | boolean): void {
		if (this._vflex != vflex) {
			this._vflex = vflex;
			this.setVflex_(vflex);

			var p;
			if (this.desktop
				&& (p = this.parent) && !p.isBinding()) //ZK-307
				zUtl.fireSized(p, -1); //no beforeSize
		}
	}

	/**
	 * Returns vertical flex hint of this widget.
	 * @see #setVflex
	 * @return String vertical flex hint of this widget.
	 */
	public getVflex(): StringFieldValue | boolean {
		return this._vflex;
	}
	public isVflex(): StringFieldValue | boolean {
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
	 * @param String flex the horizontal flex hint.
	 * @see #setVflex
	 * @see #getHflex
	 */
	public setHflex(hflex: StringFieldValue | boolean): void {
		if (this._hflex != hflex) {
			this._hflex = hflex;
			this.setHflex_(hflex);

			var p;
			if (this.desktop/*if already bind*/
				&& (p = this.parent) && !p.isBinding()/*ZK-307*/)
				zUtl.fireSized(p, -1); //no beforeSize
		}
	}

	/**
	 * Return horizontal flex hint of this widget.
	 * @return String horizontal flex hint of this widget.
	 * @see #setHflex
	 */
	public getHflex(): StringFieldValue | boolean {
		return this._hflex;
	}
	public isHflex(): StringFieldValue | boolean {
		return this.getHflex();
	}
	/** Returns the number of milliseconds before rendering this component
	 * at the client.
	 * <p>Default: -1 (don't wait).
	 * @return int the number of milliseconds to wait
	 * @since 5.0.2
	 */
	public getRenderdefer(): number {
		return this._renderdefer;
	}

	/** Sets the number of milliseconds before rendering this component
	 * at the client.
	 * <p>Default: -1 (don't wait).
	 *
	 * <p>This method is useful if you have a sophisticated page that takes
	 * long to render at a slow client. You can specify a non-negative value
	 * as the render-defer delay such that the other part of the UI can appear
	 * earlier. The styling of the render-deferred widget is controlled by
	 * a CSS class called <code>z-render-defer</code>.
	 *
	 * <p>Notice that it has no effect if the component has been rendered
	 * at the client.
	 * @param int ms time to wait in milliseconds before rendering.
	 * Notice: 0 also implies deferring the rendering (just right after
	 * all others are rendered).
	 * @since 5.0.2
	 */
	public setRenderdefer(renderdefer: number): void {
		if (this._renderdefer != renderdefer) {
			this._renderdefer = renderdefer;
		}
	}

	/** Returns the client-side action.
	 * @return String the client-side action
	 * @since 5.0.6
	 */
	public getAction(): StringFieldValue {
		return this._action;
	}

	/** Sets the client-side action.
	 * <p>Default: null (no CSA at all)
	 * <p>The format: <br>
	 * <code>action1: action-effect1; action2: action-effect2</code><br/>
	 *
	 * <p>Currently, only two actions are <code>show</code> and <code>hide</code>.
	 * They are called when the widget is becoming visible (show) and invisible (hide).
	 * <p>The action effect (<code>action-effect1</code>) is the name of a method
	 * defined in <a href="http://www.zkoss.org/javadoc/latest/jsdoc/zk/eff/Actions.html">zk.eff.Actions</a>,
	 * such as
	 * <code>show: slideDown; hide: slideUp</code>
	 * @param String action the client-side action
	 * @since 5.0.6
	 */
	public setAction(action: StringFieldValue): void {
		if (this._action != action) {
			this._action = action;
			if (action) {
				for (var ps = action.split(';'), j = ps.length; j--;) {
					var p = ps[j], k = p.indexOf(':');
					if (k >= 0) {
						var nm = p.substring(0, k).trim(),
							val = p.substring(k + 1).trim(),
							opts, fn, l;
						if (nm && val) {
							k = val.indexOf('(');
							if (k >= 0) {
								if ((l = val.lastIndexOf(')')) > k)
									opts = jq.evalJSON(val.substring(k + 1, l));
								val = val.substring(0, k);
							}
							if (fn = zk.eff.Actions![val])
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
	}

	/** Returns the tab order of this component.
	 * @return int
	 * @since 8.0.2
	 */
	public getTabindex(): NumberFieldValue {
		return this._tabindex;
	}
	/** Sets the tab order of this component.
	 * @param int tabindex
	 * @since 8.0.2
	 */
	public setTabindex(tabindex: number): void {
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
	}

	/** Returns whether using css flex in this component or not.
	 * @return boolean
	 * @since 9.0.0
	 */
	public getCssflex(): boolean {
		return this._cssflex;
	}
	public isCssflex(): boolean {
		return this.getCssflex();
	}

	/** Sets whether to use css flex in this component or not.
	 * @param boolean enable css flex or not
	 * @since 9.0.0
	 */
	public setCssflex(cssflex: boolean): void {
		if (this._cssflex != cssflex) {
			if (this.desktop) {
				this.rerender();
			}
		}
	}

	public setHflex_(v: StringFieldValue | boolean): void {
		this._nhflex = (true === v || 'true' == v) ? 1 : v == 'min' ? -65500 : zk.parseInt(v);
		if (this._nhflex < 0 && v != 'min')
			this._nhflex = 0;
		if (this.desktop) { //ZK-1784 only update the components style when it is attached to desktop
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

	public setVflex_(v: StringFieldValue | boolean): void {
		this._nvflex = (true === v || 'true' == v) ? 1 : v == 'min' ? -65500 : zk.parseInt(v);
		if (this._nvflex < 0 && v != 'min')
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
				if (this._cssflex) delete this['_cssFlexApplied'];
				_listenFlex(this);
			}
		}
	}

	/** Invoked after an animation (e.g., {@link jqzk#slideDown}) has finished.
	 * You could override to clean up anything related to animation.
	 * Notice that, if you override, you have to call back this method.
	 * @param boolean visible whether the result of the animation will make
	 * the DOM element visible
	 * @since 5.0.6
	 */
	protected afterAnima_(visible: boolean): void {
		var fn;
		if (fn = this._rmAftAnm) {
			this._rmAftAnm = null;
			fn();
		}
	}

	/** Sets the identifier of a draggable type for this widget.
	 * <p>Default: null
	 * <p>The simplest way to make a widget draggable is to set this property to "true". To disable it, set this to "false" (or null).
	 * If there are several types of draggable objects, you could assign an identifier for each type of draggable object.
	 * The identifier could be anything but empty and "false".
	 * @param String draggable "false", "" or null to denote non-draggable; "true" for draggable with anonymous identifier; others for an identifier of draggable.
	 */
	public setDraggable(draggable: StringFieldValue): void {
		if (!draggable && draggable != null) draggable = 'false'; //null means default
		this._draggable = draggable;

		if (this.desktop && !_dragCtl(this, true)) {
			if (draggable && draggable != 'false') this.initDrag_();
			else this.cleanDrag_();
		}
	}
	/** Returns the identifier of a draggable type for this widget, or null if not draggable.
	 * @return String
	 */
	public getDraggable(): string {
		var v = this._draggable;
		return v ? v : _dragCtl(this) ? 'true' : 'false';
	}

	/** Returns the owner of the ID space that this widget belongs to,
	 * or null if it doesn't belong to any ID space.
	 * <p>Notice that, if this widget is an ID space owner, this method
	 * returns itself.
	 * @return zk.Widget
	 */
	public $o(): Widget | null {
		for (var w: Widget | null = this; w; w = w.parent)
			if (w._fellows) return w;
		return null;
	}

	/** Returns the map of all fellows of this widget.
	 * <pre><code>
wgt.$f().main.setTitle("foo");
</code></pre>
	 * @return Map the map of all fellows.
	 * @since 5.0.2
	 */
	/** Returns the fellow of the specified ID of the ID space that this widget belongs to. It returns null if not found.
	 * @param String id the widget's ID ({@link #id})
	 * @return zk.Widget
	 */
	/** Returns the fellow of the specified ID of the ID space that this widget belongs to. It returns null if not found.
	 * @param String id the widget's ID ({@link #id})
	 * @param boolean global whether to search all ID spaces of this desktop.
	 * If true, it first search its own ID space, and then the other Id spaces in this browser window (might have one or multiple desktops).
	 * If omitted, it won't search all ID spaces.
	 * @return zk.Widget
	 */
	public $f(id: string, global?: boolean): Widget | null {
		var f = this.$o();
		if (!arguments.length)
			return f ? f._fellows : {};
		for (var ids = id.split('/'), j = 0, len = ids.length; j < len; ++j) {
			id = ids[j];
			if (id) {
				if (f) f = f._fellows[id];
				if (!f && global) f = _globals[id]?.[0];
				if (!f || zk.spaceless) break;
				global = false;
			}
		}
		return f;
	}
	/** Returns the identifier of this widget, or null if not assigned.
	 * It is the same as {@link #id}.
	 * @return String the ID
	 */
	public getId(): StringFieldValue {
		return this.id;
	}

	/** Sets the identifier of this widget.
	 * @param String id the identifier to assigned to.
	 */
	public setId(id: StringFieldValue): void {
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
	}

	/** Sets a property.
	 * The property updates sent from the server, including
	 * renderProperties and smartUpdate, will invoke this method.
	 * <h2>Special Names</h2>
	 * <h3>onXxx</h3>
	 * <p>If the name starts with <code>on</code>, it is assumed to be
	 * an event listener and {@link #setListener} will be called.
	 *
	 * <h3>$onXxx</h3>
	 * <p>If the name starts with <code>$on</code>, the value is assumed to
	 * be a boolean indicating if the server registers a listener.
	 *
	 * <h3>$$onXxx</h3>
	 * <p>If the name starts with <code>$$on</code>, it indicates
	 * the event is an important event that the client must send it
	 * back to the server. In additions, the value is assumed to
	 * be a boolean indicating if the server registers a listener.
	 *
	 * <h2>Special Value</h2>
	 * <h3>{$u: uuid}</h3>
	 * <p>If the value is in this format, it indicates <code>$u</code>'s
	 * value is UUID of a widget, and it will be resolved to a widget
	 * before calling the real method.
	 * <p>However, since we cannot resolve a widget by its UUID until
	 * the widget is bound (to DOM). Thus, ZK sets property after mounted.
	 * For example, <code>wgt.set("radiogroup", {$u: uuid})</code> is equivalent
	 * to the following.
	 * <pre><code>zk.afterMount(function () {
	 wgt.set("radiogroup", zk.Widget.$(uuid))
	 *});</code></pre>
	 *
	 * @param String name the name of property.
	 * @param Object value the value
	 * @return zk.Widget this widget
	 */
	/** Sets a property.
	 * The property updates sent from the server, including
	 * renderProperties and smartUpdate, will invoke this method.
	 * @param String name the name of property.
	 * Refer to {@link #set(String, Object)} for special names.
	 * @param Object value the value
	 * @param Object extra the extra argument. It could be anything.
	 * @return zk.Widget this widget
	 */
	public set = (function () {

		// cache the naming lookup
		type SetterFunc = (wgt, nm, val, extra) => Widget;
		var _setCaches: Record<string, {name: string; func: SetterFunc}> = {};
		function _setterName(name): {name: string; func: SetterFunc} {
			var setter = {name: 'set' + name.charAt(0).toUpperCase() + name.substring(1), func: (wgt, nm, val, extra) => wgt },
				cc;
			if ((cc = name.charAt(0)) == '$') {
				setter.func = function (wgt: Widget, nm, val, extra) {
					var result = wgt._setServerListener(nm, val);
					if (!result)
						zk._set2(wgt, null, nm, val, extra);
					return wgt;
				};
			} else if (cc == 'o' && name.charAt(1) == 'n'
				&& ((cc = name.charAt(2)) <= 'Z' && cc >= 'A')) {
				setter.func = function (wgt, nm, val, extra) {
					wgt.setListener(nm, val);
					return wgt;
				};
			} else {
				setter.func = function (wgt, nm, val, extra) {
					var fun;
					if (fun = wgt[setter.name]) {
						//to optimize the performance we check the method first (most common)
						zk._set2(wgt, fun, null, val, extra);
						return wgt;
					}
					zk._set2(wgt, null, nm, val, extra);
					return wgt;
				};
			}
			_setCaches[name] = setter;
			return setter;
		}
		return function (this: Widget, name: string, value, extra?): Widget {
			var cc;
			if ((cc = value && value.$u) //value.$u is UUID
			&& !(value = Widget.$(cc))) { //not created yet
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
			return setter.func.call(this, this, name, value, extra);
		};
	})();

	private _setServerListener(name: string, value): Widget | undefined {
		if (name.startsWith('$$')) {
			let cls = this.$class as typeof Widget;
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

	/** Retrieves a value from the specified property.
	 * @param String name the name of property.
	 * @return Object the value of the property
	 * @since 5.0.2
	 */
	public get(name: string): unknown {
		return zk.get(this, name);
	}

	/** Return the child widget at the specified index.
	 * <p>Notice this method is not good if there are a lot of children
	 * since it iterates all children one by one.
	 * @param int j the index of the child widget to return. 0 means the first
	 * child, 1 for the second and so on.
	 * @return zk.Widget the widget or null if no such index
	 * @see #getChildIndex
	 */
	public getChildAt(j: number): Widget | undefined {
		if (j >= 0 && j < this.nChildren)
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (--j < 0)
					return w;
	}

	/** Returns the child index of this widget.
	 * By child index we mean the order of the child list of the parent. For example, if this widget is the parent's first child, then 0 is returned.
	 * <p>Notice that {@link #getChildAt} is called against the parent, while
	 * this method called against the child. In other words,
	 * <code>w.parent.getChildAt(w.getChildIndex())</code> returns <code>w</code>.
	 * <p>Notice this method is not good if there are a lot of children
	 * since it iterates all children one by one.
	 * @return int the child index
	 */
	public getChildIndex(): number {
		var w = this.parent, j = 0;
		if (w)
			for (w = w.firstChild; w; w = w.nextSibling, ++j)
				if (w == this)
					return j;
		return 0;
	}

	/** Appends an array of children.
	 * Notice this method does NOT remove any existent child widget.
	 * @param Array children an array of children ({@link zk.Widget}) to add
	 */
	public setChildren(...children: Widget[]): void {
		if (children)
			for (var j = 0, l = children.length; j < l;)
				this.appendChild(children[j++]);
	}

	/** Append a child widget.
	 * The child widget will be attached to the DOM tree automatically,
	 * if this widget has been attached to the DOM tree,
	 * unless this widget is {@link zk.Desktop}.
	 * In other words, you have to attach child widgets of {@link zk.Desktop}
	 * manually (by use of, say, {@link #replaceHTML}).
	 *
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If this widget is bound to the DOM tree, this method invoke {@link #insertChildHTML_}
	 * to insert the DOM content of the child to the DOM tree.
	 * Thus, override {@link #insertChildHTML_} if you want to insert more than
	 * the DOM content generated by {@link #redraw}.</li>
	 * <li>If a widget wants to do something when the parent is changed, overrides {@link #beforeParentChanged_}
	 * (which is called by {@link #insertBefore}, {@link #removeChild} and {@link #appendChild}).</li>
	 * <li>{@link #insertBefore} might invoke this method (if the widget shall be the last child).
	 * To know if it is the case you can check {@link #insertingBefore_}.</li>
	 * </ul>
	 * @param zk.Widget child the child widget to add
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}).
	 * @see #insertBefore
	 */
	/** Append a child widget with more control.
	 * It is similar to {@link #appendChild(zk.Widget)} except the caller
	 * could prevent it from generating DOM element.
	 * It is usually used with {@link #rerender}.
	 * @param zk.Widget child the child widget to add
	 * @param boolean ignoreDom whether not to generate DOM elements
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}).
	 * @see #appendChild(zk.Widget)
	 * @see #insertBefore
	 */
	public appendChild(child: Widget, ignoreDom?: boolean): boolean {
		if (child == this.lastChild)
			return false;

		var oldpt;
		if ((oldpt = child.parent) != this)
			child.beforeParentChanged_(this);

		if (oldpt) {
			_noParentCallback = true;
			try {
				oldpt.removeChild(child);
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
				if (dt) this.insertChildHTML_(child, null, dt);
			}
		}

		child.afterParentChanged_(oldpt);
		if (!_noChildCallback)
			this.onChildAdded_(child);
		return true;
	}

	/** Returns whether a new child shall be ROD.
	 * <p>Default: return true if child.z_rod or this.z_rod
	 * @return boolean whether a new child shall be ROD.
	 * @since 5.0.1
	 */
	protected shallChildROD_(child: Widget): boolean | number | undefined {
		return child.z_rod || this.z_rod;
	}

	/** Inserts a child widget before the reference widget (the <code>sibling</code> argument).
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If this widget is bound to the DOM tree, this method invoke {@link #insertChildHTML_}
	 * to insert the DOM content of the child to the DOM tree. Thus, override {@link #insertChildHTML_}
	 * if you want to insert more than the DOM content generated by {@link #redraw}.</li>
	 * <li>If a widget wants to do something when the parent is changed,
	 * overrides {@link #beforeParentChanged_} (which is called by
	 * {@link #insertBefore}, {@link #removeChild} and {@link #appendChild}).
	 *
	 * @param zk.Widget child the child widget
	 * @param zk.Widget sibling the sibling widget (the 'insert' point where
	 * the new widget will be placed before). If null or omitted, it is
	 * the same as {@link #appendChild}
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}).
	 * @see #appendChild(zk.Widget)
	 */
	public insertBefore(child: Widget, sibling: Widget | null | undefined, ignoreDom?: boolean): boolean {
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

		var oldpt;
		if ((oldpt = child.parent) != this)
			child.beforeParentChanged_(this);

		if (oldpt) {
			_noParentCallback = true;
			try {
				oldpt.removeChild(child);
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
		if (!_noChildCallback)
			this.onChildAdded_(child);
		return true;
	}

	/** Removes a child.
	 * @param zk.Widget child the child to remove.
	 * @return boolean whether it is removed successfully.
	 * @see #detach
	 * @see #clear
	 */
	/** Removes a child with more control.
	 * It is similar to {@link #removeChild(zk.Widget)} except the caller
	 * could prevent it from removing the DOM element.
	 *
	 * <p>Notice that the associated DOM elements and {@link #unbind_}
	 * is called first (i.e., called before {@link #beforeParentChanged_},
	 * modifying the widget tree, ID space, and {@link #onChildRemoved_}).
	 * @param zk.Widget child the child to remove.
	 * @param boolean ignoreDom whether to remove the DOM element
	 * @return boolean whether it is removed successfully.
	 * @see #detach
	 * @see #clear
	 */
	public removeChild(child: Widget, ignoreDom?: boolean): boolean {
		var oldpt;
		if (!(oldpt = child.parent))
			return false;
		if (this != oldpt)
			return false;

		_rmIdSpaceDown(child);

		//Note: remove HTML and unbind first, so unbind_ will have all info
		if (child.z_rod) {
			this.get$Class<typeof Widget>()._unbindrod(child);

			// Bug ZK-454
			jq(child.uuid as string, zk).remove();
		} else if (child.desktop)
			this.removeChildHTML_(child, ignoreDom);

		if (!_noParentCallback)
			child.beforeParentChanged_(null);

		_unlink(this, child);


		if (!_noParentCallback)
			child.afterParentChanged_(oldpt);
		if (!_noChildCallback)
			this.onChildRemoved_(child);
		return true;
	}

	/** Removes this widget (from its parent).
	 * If it was attached to a DOM tree, the associated DOM elements will
	 * be removed, too.
	 * @see #removeChild
	 */
	public detach(): void {
		if (this.parent) this.parent.removeChild(this);
		else {
			var cf = zk.currentFocus;
			if (cf && zUtl.isAncestor(this, cf))
				zk.currentFocus = null;
			var n = this.$n();
			if (n) {
				this.unbind();
				_rmDom(this, n);
			}
		}
	}

	/** Removes all children.
	 */
	public clear(): void {
		while (this.lastChild)
			this.removeChild(this.lastChild);
	}

	/** Replaces this widget with the specified one.
	 * The parent and siblings of this widget will become the parent
	 * and siblings of the specified one.
	 * <p>Notice that {@link #replaceHTML} is used to replace a DOM element
	 * that usually doesn't not belong to any widget.
	 * And, {@link #replaceWidget} is used to replace the widget, and
	 * it maintains both the widget tree and the DOM tree.
	 * @param zk.Widget newwgt the new widget that will replace this widget.
	 * @param zk.Skipper skipper [optional] the skipper used to skip a portion of DOM nodes.
	 * @see #replaceHTML
	 * @since 5.0.1
	 */
	public replaceWidget(newwgt: Widget, skipper?: Skipper): void {
		_replaceLink(this, newwgt);

		_rmIdSpaceDown(this);
		_addIdSpaceDown(newwgt);

		var cf = zk.currentFocus, cfid, cfrg;
		if (cf && zUtl.isAncestor(this, cf)) {
			cfid = cf.uuid;
			cfrg = _bkRange(cf);
			zk.currentFocus = null;
		}

		let node: DOMFieldValue = this.$n(),
			p = this.parent, shallReplace,
			dt = newwgt.desktop || this.desktop;
		if (this.z_rod) {
			this.get$Class<typeof Widget>()._unbindrod(this);
			if (!(shallReplace = (dt = dt || (p ? p.desktop : p))
			&& (node = document.getElementById(this.uuid as string))))
				this.get$Class<typeof Widget>()._bindrod(newwgt);
		} else
			shallReplace = dt;

		// ZK-5050
		if (p)
			p.beforeChildReplaced_(this, newwgt);

		var callback = [];
		if (shallReplace) {
			if (node) newwgt.replaceHTML(node, dt, skipper, true, callback);
			else {
				this.unbind();
				newwgt.bind(dt as Desktop);
			}

			_fixBindLevel(newwgt, p ? p.bindLevel + 1 : 0);
			zWatch.fire('onBindLevelMove', newwgt);
		}

		if (p)
			p.onChildReplaced_(this, newwgt);

		this.parent = this.nextSibling = this.previousSibling = null;

		// For Bug ZK-2271, we delay the fireSized calculation after p.onChilReplaced_,
		// because the sub-nodes mapping are not getting up to date.
		if (callback && callback.length) {
			var f;
			while ((f = callback.shift()) && jq.isFunction(f))
				f();
		}
		if (cfid) {
			cf = zk.Widget.$(cfid);
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
	 * DOM element is a child element of <code>subId</code> (this.$n(subId)).
	 * <p>Note: it assumes this.$n(subId) exists.
	 * @param String subId the ID of the cave that contains the child widgets
	 * to replace with.
	 * @param Array wgts an array of widgets that will become children of this widget
	 * @param String tagBeg the beginning of HTML tag, such as &tl;tbody&gt;.
	 * Ignored if null.
	 * @param String tagEnd the ending of HTML tag, such as &lt;/tbody&gt;
	 * Ignored if null.
	 * @see zAu#createWidgets
	 */
	protected replaceCavedChildren_(subId: string, wgts: Widget[], tagBeg?: string, tagEnd?: string): void {
		_noChildCallback = true; //no callback
		try {
			//1. remove (but don't update DOM)
			var cave = this.$n(subId), fc, oldwgts: Widget[] = [];
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
			if (tagBeg) out.push(tagBeg);
			for (var j = 0, len = wgts.length; j < len; ++j)
				wgts[j].redraw(out);
			if (tagEnd) out.push(tagEnd);

			//4. update DOM
			jq(cave!).html(out.join(''));

			//5. bind
			for (var j = 0, len = wgts.length; j < len; ++j) {
				wgts[j].bind(fc);
				//Bug 3322909 Dirty fix for nrows counting wrong,
				//currently the nrows is for Listbox.
				var n = this['_nrows'];
				this.onChildReplaced_(oldwgts[j], wgts[j]);
				this['_nrows'] = n;
			}
		}
	}

	/** A callback called before the parent is changed.
	 * @param zk.Widget newparent the new parent (null if it is removed)
	 * The previous parent can be found by {@link #parent}.
	 * @see #onChildAdded_
	 * @see #onChildRemoved_
	 * @see #afterParentChanged_
	 */
	public beforeParentChanged_(newparent: Widget | null): void {
	}

	/** A callback called after the parent has been changed.
	 * @param zk.Widget oldparent the previous parent (null if it was not attached)
	 * The current parent can be found by {@link #parent}.
	 * @since 5.0.4
	 * @see #beforeParentChanged_
	 */
	protected afterParentChanged_(oldparent: Widget | null): void {
	}

	/** Returns if this widget is really visible, i.e., all ancestor widget and itself are visible.
	 * @return boolean
	 * @see #isVisible
	 */
	/** Returns if this widget is really visible, i.e., all ancestor widget and itself are visible.
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>dom - whether to check DOM element instead of {@link #isVisible}</li>
	 * <li>until - specifies the ancestor to search up to (included).
	 * If not specified, this method searches all ancestors.
	 * If specified, this method searches only this widget and ancestors up
	 * to the specified one (included).</li>
	 * <li>strict - whether to check DOM element's style.visibility.
	 * It is used only if <code>dom</code> is also specified.</li>
	 * <li>cache - a map of cached result (since 5.0.8). Ignored if null.
	 * If specified, the result will be stored and used to speed up the processing.</li>
	 * </ul>
	 * @return boolean
	 * @see #isVisible
	 */
	public isRealVisible(opts?: RealVisibleOptions): boolean {
		var dom = opts && opts.dom,
			cache = opts && opts.cache, visited: Widget[] = [], ck,
			wgt: Widget | null = this;

		//Bug ZK-1692: widget may not bind or render yet.
		if (!wgt.desktop)
			return false;

		while (wgt) {
			if (cache && (ck = wgt.uuid) && (ck = cache[ck as string]) !== undefined)
				return _markCache(cache, visited, ck);

			if (cache)
				visited.push(wgt);

			if (dom && !wgt['z_virnd']) { //z_virnd implies zk.Native, zk.Page and zk.Desktop
			//Except native, we have to assume it is invsibile if $n() is null
			//Example, tabs in the accordion mold (case: zktest/test2 in IE)
			//Alertinative is to introduce another isVisibleXxx but not worth
				if (!zk(wgt.$n()).isVisible(opts?.strict))
					return _markCache(cache, visited, false);
			} else if (!wgt.isVisible())
				return _markCache(cache, visited, false);

			//check if it is hidden by parent, such as child of hbox/vbox or border-layout
			var wp = wgt.parent, p, n;
			if (wp && wp._visible && (n = wgt.$n()) && (p = zk(n).vparentNode(true)))
				while (p && p != n) {
					if (p.style && p.style.display == 'none') //hidden by parent
						return _markCache(cache, visited, false);
					p = zk(p).vparentNode(true);
				}

			if (opts && opts.until == wgt)
				break;

			wgt = wp;
		}
		return _markCache(cache, visited, true);
	}

	/** Returns if this widget is visible
	 * @return boolean
	 * @see #isRealVisible
	 * @see jqzk#isVisible
	 */
	/** Returns if this widget is visible
	 * @param boolean strict whether to check the visibility of the associated
	 * DOM element. If true, this widget and the associated DOM element
	 * must be both visible.
	 * @return boolean
	 * @see #isRealVisible
	 * @see jqzk#isVisible
	 * @see #setVisible
	 */
	public isVisible(strict?: boolean): BooleanFieldValue {
		var visible = this._visible;
		if (!strict || !visible)
			return visible;
		var n = this.$n();
		return n && zk(n).isVisible(); //ZK-1692: widget may not bind or render yet
	}

	/** Sets whether this widget is visible.
	 * <h3>Subclass Notes</h3>
	 * <ul>
	 * <li>setVisible invokes the parent's {@link #onChildVisible_}, so you
	 * can override {@link #onChildVisible_} to change the related DOM element.
	 * For example, updating the additional enclosing tags (such as zul.box.Box). </li>
	 * <li>setVisible invokes {@link #setDomVisible_} to change the visibility of a child DOM element, so override it if necessary.</li>
	 * </ul>
	 * @param boolean visible whether to be visible
	 */
	public setVisible(visible: boolean | undefined): void {
		if (this._visible != visible) {
			this._visible = visible;

			var p = this.parent, ocvCalled;
			if (this.desktop) {
				var parentVisible = !p || p.isRealVisible(),
					node = this.$n() as HTMLElement,
					floating = this._floating;

				if (!parentVisible) {
					if (!floating) this.setDomVisible_(node, visible);
				} else if (visible) {
					var zi;
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
							zi = zi >= 0 ? ++zi : _topZIndex(w);
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
	}

	/** Synchronizes a map of objects that are associated with this widget, and
	 * they shall be resized when the size of this widget is changed.
	 * <p>It is useful to sync the layout, such as shadow, mask
	 * and error message, that is tightly associated with a widget.
	 * @param Map opts the options, or undefined if none of them specified.
	 * Allowed values:<br/>
	 */
	public zsync(opts?: Record<string, unknown>): void {
		for (var nm in this.effects_) {
			var ef = this.effects_[nm];
			if (ef && ef['sync']) ef['sync']();
		}
	}

	/** Makes this widget visible.
	 * It is a shortcut of <code>setVisible(true)</code>
	 * @return zk.Widget this widget
	 */
	public show(): this {
		this.setVisible(true);
		return this;
	}

	/** Makes this widget invisible.
	 * It is a shortcut of <code>setVisible(false)</code>
	 * @return zk.Widget this widget
	 */
	public hide(): this {
		this.setVisible(false);
		return this;
	}

	/** Changes the visibility of a child DOM content of this widget.
	 * It is called by {@link #setVisible} to really change the visibility
	 * of the associated DOM elements.
	 * <p>Default: change n.style.display directly.
	 * @param DOMElement n the element (never null)
	 * @param boolean visible whether to make it visible
	 * @param Map opts [optional] the options.
	 * If omitted, <code>{display:true}</code> is assumed. Allowed value:
	 * <ul>
	 * <li>display - Modify n.style.display</li>
	 * <li>visibility - Modify n.style.visibility</li>
	 * </ul>
	 */
	public setDomVisible_(n: HTMLElement, visible: boolean | undefined, opts?: DomVisibleOptions): void {
		if (!opts || opts.display) {
			var act;
			if (act = this.actions_[visible ? 'show' : 'hide'])
				act[0].call(this, n, act[1]);
			else
				n.style.display = visible ? '' : 'none';
		}
		if (opts && opts.visibility)
			n.style.visibility = visible ? 'visible' : 'hidden';
	}

	/** A callback called after a child has been added to this widget.
	 * <p>Notice: when overriding this method, {@link #onChildReplaced_}
	 * is usually required to override, too.
	 * @param zk.Widget child the child being added
	 * @see #beforeParentChanged_
	 * @see #onChildRemoved_
	 */
	protected onChildAdded_(child: Widget): void {
		if (this.desktop)
			jq.onSyncScroll(this);
	}

	/** A callback called after a child has been removed to this widget.
	 * <p>Notice: when overriding this method, {@link #onChildReplaced_}
	 * @param zk.Widget child the child being removed
	 * @see #beforeParentChanged_
	 * @see #onChildAdded_
	 */
	protected onChildRemoved_(child: Widget): void {
		if (this.desktop)
			jq.onSyncScroll(this);
	}

	/** A callback called after a child has been replaced.
	 * Unlike {@link #onChildAdded_} and {@link #onChildRemoved_}, this
	 * method is called only if {@link zk.AuCmd1#outer}.
	 * And if this method is called, neither {@link #onChildAdded_} nor {@link #onChildRemoved_}
	 * will be called.
	 * <p>Default: invoke {@link #onChildRemoved_} and then
	 * {@link #onChildAdded_}.
	 * Furthermore, it sets this.childReplacing_ to true before invoking
	 * {@link #onChildRemoved_} and {@link #onChildAdded_}, so we can optimize
	 * the code (such as rerender only once) by checking its value.
	 * @param zk.Widget oldc the old child (being removed). Note: it might be null.
	 * @param zk.Widget newc the new child (being added). Note: it might be null.
	 */
	protected onChildReplaced_(oldc: Widget | null | undefined, newc: Widget | null | undefined): void {
		this.childReplacing_ = true;
		try {
			if (oldc) this.onChildRemoved_(oldc);
			if (newc) this.onChildAdded_(newc);
		} finally {
			this.childReplacing_ = false;
		}
	}

	/** A callback called after a child's visibility is changed
	 * (i.e., {@link #setVisible} was called).
	 * <p>Notice that this method is called after the _visible property
	 * and the associated DOM element(s) have been changed.
	 * <p>To know if it is becoming visible, you can check {@link #isVisible}
	 * (such as this._visible).
	 * @param zk.Widget child the child whose visiblity is changed
	 */
	protected onChildVisible_(child: Widget): void {
	}

	/** A callback called after a child has been delay rendered.
	 * @param zk.Widget child the child being rendered
	 * @see #deferRedraw_
	 * @since 6.5.1
	 */
	public onChildRenderDefer_(child: Widget): void {
	}

	/** Makes this widget as topmost.
	 * <p>If this widget is not floating, this method will look for its ancestors for the first ancestor who is floating. In other words, this method makes the floating containing this widget as topmost.
	 * To make a widget floating, use {@link #setFloating_}.
	 * <p>This method has no effect if it is not bound to the DOM tree, or none of the widget and its ancestors is floating.
	 * <p>Notice that it does not fire onFloatUp so it is caller's job if it is necessary
	 * to close other popups.
	 * @return int the new value of z-index of the topmost floating window, -1 if this widget and none of its ancestors is floating or not bound to the DOM tree.
	 * @see #setFloating_
	 */
	public setTopmost(): number {
		if (!this.desktop || this._userZIndex) return -1;

		for (var wgt: Widget | null = this; wgt; wgt = wgt.parent)
			if (wgt._floating) {
				var zi = _topZIndex(wgt);
				for (var j = 0, fl = _floatings.length; j < fl; ++j) { //from child to parent
					var w = _floatings[j].widget,
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
	 * It is called by {@link #setTopmost} to set the z-index,
	 * and called only if {@link #setFloating_} is ever called.
	 * @param DOMElement node the element whose z-index needs to be set.
	 * It is the value specified in <code>opts.node</code> when {@link #setFloating_}
	 * is called. If not specified, it is the same as {@link #$n}.
	 * @param int zi the z-index to set
	 * @see #setFloating_
	 * @since 5.0.3
	 */
	protected setFloatZIndex_(node: HTMLElement, zi: number): void {
		if (node != this.$n()) node.style.zIndex = zi as unknown as string; //only a portion
		else this.setZIndex(zi, {fire: true, floatZIndex: true});
	}

	/** Returns the z-index of a floating widget.
	 * It is called by {@link #setTopmost} to decide the topmost z-index,
	 * and called only if {@link #setFloating_} is ever called.
	 * @param DOMElement node the element whose z-index needs to be set.
	 * It is the value specified in <code>opts.node</code> when {@link #setFloating_}
	 * is called. If not specified, it is the same as {@link #$n}.
	 * @since 5.0.3
	 * @see #setFloating_
	 */
	public getFloatZIndex_(node): number {
		return node != this.$n() ? node.style.zIndex : this._zIndex;
	}

	/** Returns the top widget, which is the first floating ancestor,
	 * or null if no floating ancestor.
	 * @return zk.Widget
	 * @see #isFloating_
	 */
	public getTopWidget(): Widget | undefined {
		for (var wgt: Widget | null = this; wgt; wgt = wgt.parent)
			if (wgt._floating)
				return wgt;
	}

	/** Returns if this widget is floating.
	 * <p>We say a widget is floating if the widget floats on top of others, rather than embed inside the parent. For example, an overlapped window is floating, while an embedded window is not.
	 * @return boolean
	 * @see #setFloating_
	 */
	public isFloating_(): boolean {
		return this._floating;
	}

	/** Sets a status to indicate if this widget is floating.
	 * <p>Notice that it doesn't change the DOM tree. It is caller's job.
	 * In the other words, the caller have to adjust the style by assigning
	 * <code>position</code> with <code>absolute</code> or <code>relative</code>.
	 * @param boolean floating whether to make it floating
	 * @param Map opts [optional] The options. Allowed options:
	 * <ul>
	 * <li>node: the DOM element. If omitted, {@link #$n} is assumed.</li>
	 * </ul>
	 * @see #isFloating_
	 */
	public setFloating_(floating: boolean, opts?: Partial<{ node: HTMLElement }>): void {
		if (this._floating != floating) {
			if (floating) {
				//parent first
				var inf = {widget: this, node: opts && opts.node ? opts.node : this.$n() as HTMLElement},
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

	/** Returns the Z index.
	 * @return int
	 */
	public getZIndex(): number {
		return this._zIndex;
	}
	public getZindex(): number {
		return this.getZIndex();
	}

	/** Sets the Z index.
	 * @param int zIndex the Z index to assign to
	 * @param Map opts if opts.fire is specified the onZIndex event will be triggered. If opts.floatZIndex is false, represent it is not from setFloatZIndex, so the userZIndex may be true.
	 */
	public setZIndex(zIndex: number, opts: Partial<{ floatZIndex: boolean; fire: boolean }>): void {
		if (opts && opts.floatZIndex && this._userZIndex)
			return;
		if (!opts || !opts.floatZIndex)
			this._userZIndex = true;
		if (!zIndex)
			this._userZIndex = false;
		if (this._zIndex != zIndex) {
			this._zIndex = zIndex;
			var n = this.$n();
			if (n) {
				n.style.zIndex = zIndex >= 0 ? (zIndex as unknown as string) : '';
				if (opts && opts.fire) this.fire('onZIndex', (zIndex > 0 || zIndex === 0) ? zIndex : -1, {ignorable: true});
			}
		}
	}

	public setZindex(zIndex: number, opts: Partial<{ floatZIndex: boolean; fire: boolean }>): void {
		this.setZIndex(zIndex, opts);
	}

	/** Returns the scroll top of the associated DOM element of this widget.
	 * <p>0 is always returned if this widget is not bound to a DOM element yet.
	 * @return int
	 */
	public getScrollTop(): number {
		var n = this.$n();
		return n ? n.scrollTop : 0;
	}

	/** Returns the scroll left of the associated DOM element of this widget.
	 * <p>0 is always returned if this widget is not bound to a DOM element yet.
	 * @return int
	 */
	public getScrollLeft(): number {
		var n = this.$n();
		return n ? n.scrollLeft : 0;
	}

	/** Sets the scroll top of the associated DOM element of this widget.
	 * <p>This method does nothing if this widget is not bound to a DOM element yet.
	 * @param int the scroll top.
	 */
	public setScrollTop(val: number): void {
		var n = this.$n();
		if (n) n.scrollTop = val;
	}

	/** Sets the scroll left of the associated DOM element of this widget.
	 * <p>This method does nothing if this widget is not bound to a DOM element yet.
	 * @param int the scroll top.
	 */
	public setScrollLeft(val: number): void {
		var n = this.$n();
		if (n) n.scrollLeft = val;
	}

	/** Makes this widget visible in the browser window by scrolling ancestors up or down, if necessary.
	 * <p>Default: invoke zk(this).scrollIntoView();
	 * @see jqzk#scrollIntoView
	 * @return zk.Widget this widget
	 */
	public scrollIntoView(): this {
		zk(this.$n()).scrollIntoView();
		return this;
	}

	/** Generates the HTML fragment for this widget.
	 * The HTML fragment shall be pushed to out. For example,
<pre>{@code
out.push('<div', this.domAttrs_(), '>');
for (var w = this.firstChild; w; w = w.nextSibling)
	w.redraw(out);
out.push('</div>');
}
	 * <p>Default: it retrieves the redraw function associated with
	 * the mold ({@link #getMold}) and then invoke it.
	 * The redraw function must have the same signature as this method.
	 * @param Array out an array to output HTML fragments.
	 * Technically it can be anything that has the method called <code>push</code>
	 */
	public redraw(out: Array<string>, skipper?: Skipper | null): void {
		if (!this.deferRedraw_(out)) {
			var f;
			if (f = this['prolog'])
				out.push(f);

			if ((f = this.get$Class<typeof Widget>().molds) && (f = f[this._mold]))
				return f.apply(this, arguments);

			zk.error('Mold ' + this._mold + ' not found in ' + this.className);
		}
	}

	/** Utilities for handling the so-called render defer ({@link #setRenderdefer}).
	 * This method is called automatically by {@link #redraw},
	 * so you only need to use it if you override {@link #redraw}.
	 * <p>A typical usage is as follows.
	 * <pre><code>
redraw: function (out) {
  if (!this.deferRedraw_(out)) {
  	out.push(...); //redraw
  }
}
	 * </code></pre>
	 * @param Array out an array to output the HTML fragments.
	 * @since 5.0.2
	 */
	protected deferRedraw_(out: Array<string> | null): boolean {
		var delay;
		if ((delay = this._renderdefer) >= 0) {
			if (!this._norenderdefer) {
				this.z_rod = this['_z$rd'] = true;
				this.deferRedrawHTML_(out as string[]);
				out = null; //to free memory

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
	 * @param Array out an array to output the HTML fragments.
	 * @since 5.0.6
	 */
	protected deferRedrawHTML_(out: Array<string>): void {
		out.push('<div', this.domAttrs_({domClass: true}), ' class="z-renderdefer"></div>');
	}

	/** Forces the rendering if it is deferred.
	 * A typical way to defer the render is to specify {@link #setRenderdefer}
	 * with a non-negative value. The other example is some widget might be
	 * optimized for the performance by not rendering some or the whole part
	 * of the widget. If the rendering is deferred, the corresponding DOM elements
	 * ({@link #$n}) are not available. If it is important to you, you can
	 * force it to be rendered.
	 * <p>Notice that this method only forces this widget to render. It doesn't
	 * force any of its children. If you want, you have invoke {@link #forcerender}
	 * one-by-one
	 * <p>The derived class shall override this method, if it implements
	 * the render deferring (other than {@link #setRenderdefer}).
	 * @since 5.0.2
	 */
	public forcerender(): void {
		_doDeferRender(this);
	}

	/** Updates the DOM element's CSS class. It is called when the CSS class is changed (e.g., setZclass is called).
	 * <p>Default: it changes the class of {@link #$n}.
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>Override it if the class has to be copied to DOM elements other than {@link #$n}.</li>
	 * </ul>
	 * @see #updateDomStyle_
	 */
	protected updateDomClass_(): void {
		if (this.desktop) {
			var n = this.$n();
			if (n) n.className = this.domClass_();
			this.zsync();
		}
	}

	/** Updates the DOM element's style. It is called when the CSS style is changed (e.g., setStyle is called).
	 * <p>Default: it changes the CSS style of {@link #$n}.
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>Override it if the CSS style has to be copied to DOM elements other than {@link #$n}.</li>
	 * </ul>
	 */
	protected updateDomStyle_(): void {
		if (this.desktop) {
			var s = jq.parseStyle(this.domStyle_()),
				n = this.$n() as HTMLElement;
			// B50-3355680: size is potentially affected when setStyle
			if (!s.width && this._hflex)
				s.width = n.style.width;
			if (!s.height && this._vflex)
				s.height = n.style.height;
			zk(n).clearStyles().jq.css(s);

			var t = this.getTextNode();
			if (t && t != n) {
				s = this._domTextStyle(t, s);
				zk(t).clearStyles().jq.css(s);
			}
			this.zsync();
		}
	}
	private _domTextStyle(t, s): Record<string, string> {
		// B50-3355680
		s = jq.filterTextStyle(s);
		// B70-ZK-1807: reserve style width and height,it will make sure that textnode has correct size.
		if (t.style.width)
			s.width = t.style.width;
		if (t.style.height)
			s.height = t.style.height;
		return s;
	}

	/** Returns the DOM element that is used to hold the text, or null
	 * if this widget doesn't show any text.
	 * <p>Default: return null (no text node).
	 * <p>For example, {@link #updateDomStyle_} will change the style
	 * of the text node, if any, to make sure the text is displayed correctly.
	 * @return DOMElement the DOM element.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component_Development/Client-side/Text_Styles_and_Inner_Tags">ZK Client-side Reference: Text Styles and Inner Tags</a>.
	 * @see #domTextStyleAttr_
	 * @see #updateDomStyle_
	 */
	public getTextNode(): HTMLElement | null | undefined {
		return null;
	}

	/** Returns the style used for the DOM element of this widget.
	 * <p>Default: a concatenation of style, width, visible and so on.
	 * @param Map no [options] the style to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want width to generate, call <code>domStyle_({width:1})</code>.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>style - exclude {@link #getStyle}</li>
	 * <li>width - exclude {@link #getWidth}</li>
	 * <li>height - exclude {@link #getHeight}</li>
	 * <li>left - exclude {@link #getLeft}</li>
	 * <li>top - exclude {@link #getTop}</li>
	 * <li>zIndex - exclude {@link #getZIndex}</li>
	 * <li>visible - exclude {@link #isVisible}</li>
	 * </ul>
	 * @return String the content of the style, such as width:100px;z-index:1;
	 * @see #domClass_
	 * @see #domAttrs_
	 */
	protected domStyle_(no?: DomStyleOptions): string {
		var out = '', s;
		if (s = this['z$display']) //see au.js
			out += 'display:' + s + ';';
		else if (!this.isVisible() && (!no || !no.visible))
			out += 'display:none;';

		if ((!no || !no.style) && (s = this.getStyle())) {
			s = s.replace(REGEX_DQUOT, '\'');  // B50-ZK-647
			out += s;
			if (s.charAt(s.length - 1) != ';')
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
		if ((!no || !no.zIndex) && (s = this.getZIndex()) >= 0)
			out += 'z-index:' + s + ';';
		return out;
	}

	/** Returns the class name(s) used for the DOM element of this widget.
	 * <p>Default: a concatenation of {@link #getZclass} and {@link #getSclass}.
	 *
	 * @param Map no [options] the style class to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want sclass to generate, call <code>domClass_({sclass:1})</code>.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>sclass - exclude {@link #getSclass}</li>
	 * <li>zclass - exclude {@link #getZclass}</li>
	 * </ul>
	 * @return String the CSS class names, such as <code>z-button foo</code>
	 * @see #domStyle_
	 * @see #domAttrs_
	 */
	protected domClass_(no?: DomClassOptions): string {
		var s, z;
		if (!no || !no.sclass)
			s = this.getSclass();
		if (!no || !no.zclass)
			z = this.getZclass();
		let domClass = s ? z ? s + ' ' + z : s : z || '',
			n = this.$n();
		// FIX ZK-5137: modifying sclass clears vflex="1 here to avoid circular dependency issue in ZK 10
		if (n) {
			let jqn = jq(n),
				flexClasses = ['z-flex', 'z-flex-row', 'z-flex-column', 'z-flex-item'];
			for (let i = 0, length = flexClasses.length; i < length; i++) {
				let flexClass = flexClasses[i];
				if (jqn.hasClass(flexClass)) {
					domClass += ' ' + flexClass;
				}
			}
		}
		return domClass;
	}

	/** Returns the HTML attributes that is used to generate DOM element of this widget.
	 * It is usually used to implement a mold ({@link #redraw}):
<pre>{@code
function () {
 return '<div' + this.domAttrs_() + '></div>';
}}
	 * <p>Default: it generates id, style, class, and tooltiptext.
	 * Notice that it invokes {@link #domClass_} and {@link #domStyle_},
	 * unless they are disabled by the <code>no</code> argument.
	 *
	 * @param Map no [options] the attributes to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want the style class to generate, call <code>domAttrs_({domClass:1})</code>.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>id - exclude {@link #uuid}</li>
	 * <li>domClass - exclude {@link #domClass_}</li>
	 * <li>domStyle - exclude {@link #domStyle_}</li>
	 * <li>tooltiptext - exclude {@link #getTooltiptext}</li>
	 * <li>tabindex - exclude {@link #getTabindex()}</li>
	 * </ul>
	 * <p>return the HTML attributes, such as id="z_u7_3" class="z-button"
	 * @return String
	 */
	public domAttrs_(no?: DomAttrsOptions): string {
		var out = '', s;
		if (!no) {
			if ((s = this.uuid))
				out += ' id="' + s + '"';
			if ((s = this.domStyle_(no)))
				out += ' style="' + s + '"';
			if ((s = this.domClass_(no)))
				out += ' class="' + s + '"';
			if ((s = this.domTooltiptext_()))
				out += ' title="' + zUtl.encodeXML(s) + '"'; // ZK-676
			if ((s = this.getTabindex()) != undefined)
				out += ' tabindex="' + s + '"';
		} else {
			if (!no.id && (s = this.uuid))
				out += ' id="' + s + '"';
			if (!no.domStyle && (s = this.domStyle_(no)))
				out += ' style="' + s + '"';
			if (!no.domClass && (s = this.domClass_(no)))
				out += ' class="' + s + '"';
			if (!no.tooltiptext && (s = this.domTooltiptext_()))
				out += ' title="' + zUtl.encodeXML(s) + '"'; // ZK-676
			if (!no.tabindex && (s = this.getTabindex()) != undefined)
				out += ' tabindex="' + s + '"';
		}
		if (this.domExtraAttrs) {
			out += this.domExtraAttrs_();
		}
		return out;
	}

	// B80-ZK-2957
	protected domExtraAttrs_(): string {
		var dh = {},
			has = false,
			out = '',
			attrs;

		for (var nm in (attrs = this.domExtraAttrs)) {
			if (zk.hasDataHandler(nm)) {
				has = true;
				dh[nm] = attrs[nm];
			}
			out += ' ' + nm + '="' + zUtl.encodeXMLAttribute(attrs[nm] || '') + '"'; //generate even if val is empty
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

	/** Returns the tooltiptext for generating the title attribute of the DOM element.
	 * <p>Default: return {@link #getTooltiptext}.
	 * <p>Deriving class might override this method if the parent widget
	 * is not associated with any DOM element, such as treerow's parent: treeitem.
	 * @return String the tooltiptext
	 * @since 5.0.2
	 */
	protected domTooltiptext_(): StringFieldValue {
		return this.getTooltiptext();
	}

	/** Returns the style attribute that contains only the text related CSS styles. For example, it returns style="font-size:12pt;font-weight:bold" if #getStyle is border:none;font-size:12pt;font-weight:bold.
	 * <p>It is usually used with {@link #getTextNode} to
	 * <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component_Development/Client-side/Text_Styles_and_Inner_Tags">ZK Client-side Reference: Text Styles and Inner Tags</a>.
	 * @see #getTextNode
	 * @return String the CSS style that are related to text (string).
	 */
	protected domTextStyleAttr_(): StringFieldValue {
		var s = this.getStyle();
		return s ? zUtl.appendAttr('style', jq.filterTextStyle(s)) : s;
	}

	/** Replaces the specified DOM element with the HTML content generated this widget.
	 * It is the same as <code>jq(n).replaceWith(wgt, desktop, skipper)</code>.
	 * <p>The DOM element to be replaced can be {@link #$n} or any independent DOM element. For example, you can replace a DIV element (and all its descendants) with this widget (and its descendants).
	 * <p>This method is usually used to replace a DOM element with a root widget (though, with care, it is OK for non-root widgets). Non-root widgets usually use {@link #appendChild}
	 *  and {@link #insertBefore} to attach to the DOM tree[1]
	 * <p>If the DOM element doesn't exist, you can use {@link _global_.jq#before} or {@link _global_.jq#after} instead.
	 * <p>Notice that, both {@link #replaceHTML} fires the beforeSize and onSize watch events
	 * (refer to {@link zWatch}).
	 * <p>If skipper is null. It implies the caller has to fire these two events if it specifies a skipper
	 * (that is how {@link #rerender} is implemented).
	 * <h3>Subclass Note</h3>
	 * This method actually forwards the invocation to its parent by invoking
	 * parent's {@link #replaceChildHTML_} to really replace the DOM element.
	 * Thus, override {@link #replaceChildHTML_} if you want to do something special for particular child widgets.
	 *
	 * @param Object n the DOM element ({@link DOMElement}) or anything
	 * {@link #$} allowed.
	 * @param zk.Desktop desktop [optional] the desktop that this widget shall belong to.
	 * If omitted, it is retrieve from the current desktop.
	 * If null, it is decided automatically ( such as the current value of {@link #desktop} or the first desktop)
	 * @param zk.Skipper skipper [optional] it is used only if it is called by {@link #rerender}
	 * @see #replaceWidget
	 * @see _global_.jq#replaceWith
	 */
	public replaceHTML(n: HTMLElement | string, desktop: Desktop | null, skipper?: Skipper | null, _trim_?: boolean, _callback_?: CallableFunction[]): void {
		if (!desktop) {
			desktop = this.desktop;
			if (!zk.Desktop._ndt) zk.stateless();
		}

		var cfi = skipper ? null : _bkFocus(this),
			p = this.parent;
		if (p) p.replaceChildHTML_(this, n, desktop, skipper, _trim_);
		else {
			var oldwgt = this.getOldWidget_(n);
			if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
			else if (this.z_rod) this.get$Class<typeof Widget>()._unbindrod(this); //possible (if replace directly)
			jq(n as string).replaceWith(this.redrawHTML_(skipper, _trim_));
			this.bind(desktop as Desktop, skipper);
		}

		if (!skipper) {
			if (!jq.isArray(_callback_))
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
	 * Returns the widget associated with the given node element.
	 * It is used by {@link #replaceHTML} and {@link #replaceChildHTML_} to retrieve
	 * the widget associated with the note.
	 * <p>It is similar to {@link #$} but it gives the widget a chance to
	 * handle extreme cases. For example, Treeitem doesn't associate a DOM element
	 * (or you can say Treeitem and Treerow shares the same DOM element), so
	 * <code>zk.Widget.$(n)</code> will return Treerow, not Treeitem.
	 * If it is the case, you can override it to make {@link #replaceHTML}
	 * works correctly.
	 * @param DOMElement n the DOM element to match the widget.
	 * @since 5.0.3
	 */
	public getOldWidget_(n: HTMLElement | string): Widget | null {
		return Widget.$(n, {strict: true});
	}

	/** Returns the HTML fragment of this widget.
	 * @param zk.Skipper skipper the skipper. Ignored if null
	 * @param boolean trim whether to trim the HTML content before replacing
	 * @return String the HTML fragment
	 */
	public redrawHTML_(skipper?: Skipper | null, trim?: boolean): string {
		var out = new zk.Buffer(); // Due to the side-effect of B65-ZK-1628, we remove the optimization of the array's join() for chrome.
		this.redraw(out, skipper);
		var html = out.join('');
		return trim ? html.trim() : html;
			//To avoid the prolog being added repeatedly if keep invalidated:
			//<div><textbox/> <button label="Click!" onClick="self.invalidate()"/></div>
	}

	/** Re-renders the DOM element(s) of this widget.
	 * By re-rendering we mean to generate HTML again ({@link #redraw})
	 * and then replace the DOM elements with the new generated HTML code snippet.
	 * <p>It is equivalent to replaceHTML(this.node, null, skipper).
	 * <p>It is usually used to implement a setter of this widget.
	 * For example, if a setter (such as <code>setBorder</code>) has to
	 * modify the visual appearance, it can update the DOM tree directly,
	 * or it can call this method to re-render all DOM elements associated
	 * with is widget and its descendants.
	 * <p>It is convenient to synchronize the widget's state with
	 * the DOM tree with this method. However, it shall be avoided
	 * if the HTML code snippet is complex (otherwise, the performance won't be good).
	 * <p>If re-rendering is required, you can improve the performance
	 * by passing an instance of {@link zk.Skipper} that is used to
	 * re-render some or all descendant widgets of this widget.
	 * @param zk.Skipper skipper [optional] skip some portion of this widget
	 * to speed up the re-rendering.
	 * If not specified, rerender(0) is assumed (since ZK 6).
	 * @return zk.Widget this widget.
	 */
	/** Re-renders after the specified time (milliseconds).
	 * <p>Notice that, to have the best performance, we use the single timer
	 * to handle all pending rerenders for all widgets.
	 * In other words, if the previous timer is not expired (and called),
	 * the second call will reset the expiration time to the value given
	 * in the second call.
	 * @param int timeout the number milliseconds (non-negative) to wait
	 * before rerender. If negative, it means rerender shall take place
	 * immediately. If not specified, 0 is assumed (since ZK 6).
	 * @since 5.0.4
	 */
	public rerender(skipper?: Skipper | number | null): void {
		if (this.desktop) {
			if (!skipper || skipper > 0) { //default: 0
				_rerender(this, typeof skipper === 'number' ? skipper : 0);
				return;
			}
			if (skipper < 0)
				skipper = null; //negative -> immediately

			var n = this.$n();
			if (n) {
				try {
					zk._avoidRod = true;
						//to avoid side effect since the caller might look for $n(xx)

					var skipInfo;
					if (skipper instanceof Skipper) {
						skipInfo = skipper.skip(this);
						if (skipInfo) {
							var cfi = _bkFocus(this);

							this.replaceHTML(n, null, skipper, true);

							skipper.restore(this, skipInfo);

							zWatch.fireDown('onRestore', this);
								//to notify it is restored from rerender with skipper
							zUtl.fireSized(this);

							_rsFocus(cfi);
						}
					}

					if (!skipInfo)
						this.replaceHTML(n, null, null, true);
				} finally {
					delete zk._avoidRod;
				}
			}
		}
	}

	/** A function that postpones the invoke of rerender function until all the cmds from server are processed.
	 * This avoids rerendering twice or more. It works only in the setAttrs phase,
	 * otherwise rerender will be invoked immediately.
	 * @since 8.6.0
	 */
	protected rerenderLater_(...args: [skipper?: Skipper]): void {
		var processPhase = zAu.processPhase;
		if (processPhase == 'setAttr' || processPhase == 'setAttrs') {
			this.doAfterProcessRerenderArgs = args;
		} else {
			this.rerender.apply(this, args);
		}
	}

	/** Replaces the DOM element(s) of the specified child widget.
	 * It is called by {@link #replaceHTML} to give the parent a chance to
	 * do something special for particular child widgets.
	 * @param zk.Widget child the child widget whose DOM content is used to replace the DOM tree
	 * @param DOMElement n the DOM element to be replaced
	 * @param zk.Desktop dt [optional the desktop that this widget shall belong to.
	 * If null, it is decided automatically ( such as the current value of {@link #desktop} or the first desktop)
	 * @param zk.Skipper skipper it is used only if it is called by {@link #rerender}
	 */
	protected replaceChildHTML_(child: Widget, n: HTMLElement | string, desktop?: Desktop | null, skipper?: Skipper | null, _trim_?: boolean): void {
		var oldwgt = child.getOldWidget_(n),
			skipInfo;
		if (oldwgt) {
			oldwgt.unbind(skipper); //unbind first (w/o removal)
			skipInfo = skipper ? skipper.skip(oldwgt) : null;
		} else if (this.shallChildROD_(child))
			this.get$Class<typeof Widget>()._unbindrod(child); //possible (e.g., Errorbox: jq().replaceWith)

		jq(n as string).replaceWith(child.redrawHTML_(skipper, _trim_));
		if (skipInfo) {
			skipper?.restore(child, skipInfo);
		}
		child.bind(desktop, skipper);
	}

	/** Inserts the HTML content generated by the specified child widget before the reference widget (the before argument).
	 * It is called by {@link #insertBefore} and {@link #appendChild} to handle the DOM tree.
	 * <p>Deriving classes might override this method to modify the HTML content, such as enclosing with TD.
	 * <p>Notice that when inserting the child (without the before argument), this method will call {@link #getCaveNode} to find the location to place the DOM element of the child. More precisely, the node returned by {@link #getCaveNode} is the parent DOM element of the child. The default implementation of {@link #getCaveNode} is to look for a sub-node named uuid$cave. In other words, it tried to place the child inside the so-called cave sub-node, if any.
	 * Otherwise, {@link #$n} is assumed.
	 * @param zk.Widget child the child widget to insert
	 * @param zk.Widget before the child widget as the reference to insert the new child before. If null, the HTML content will be appended as the last child.
	 * The implementation can use before.getFirstNode_() ({@link #getFirstNode_}) to retrieve the DOM element
	 * @param zk.Desktop desktop
	 * @see #getCaveNode
	 */
	protected insertChildHTML_(child: Widget, before?: Widget | null, desktop?: Desktop | null): void {
		var ben, html = child.redrawHTML_(),
			before0: Widget | HTMLElement | null | undefined = before;
		if (before0) {
			if (before0 instanceof Native) { //native.$n() is usually null
				ben = before0.previousSibling;
				if (ben) {
					if (ben == child) //always true (since link ready), but to be safe
						ben = ben.previousSibling;
					if (ben && (ben = ben.$n())) {
						jq(ben).after(html);
						child.bind(desktop);
						return;
					}
				}
				//FUTURE: it is not correct to go here, but no better choice yet
			}
			before0 = (before0 as Widget).getFirstNode_();
		}
		if (!before0)
			for (var w: Widget | null = this; ;) {
				ben = w.getCaveNode();
				if (ben) break;

				var w2 = w.nextSibling;
				if (w2 && (before0 = w2.getFirstNode_()))
					break;

				if (!(w = w.parent)) {
					ben = document.body;
					break;
				}
			}

		if (before0) {
			var sib = (before0 as HTMLElement).previousSibling;
			if (_isProlog(sib)) before0 = sib as HTMLElement;
			jq(before0).before(html);
		} else
			jq(ben).append(html);
		child.bind(desktop);
	}

	/** Called by {@link #insertChildHTML_} to find the location to place the DOM element of the child.
	 * More precisely, the node returned by {@link #getCaveNode} is the parent DOM element of the child's DOM element.
	 * <p>Default: <code>this.$n('cave') || this.$n()</code>
	 * You can override it to return whatever DOM element you want.
	 * @see #insertChildHTML_
	 * @return DOMElement
	 */
	public getCaveNode(): HTMLElement | null | undefined {
		return this.$n('cave') || this.$n();
	}
	/** Returns the first DOM element of this widget.
	 * If this widget has no corresponding DOM element, this method will look
	 * for its siblings.
	 * <p>This method is designed to be used with {@link #insertChildHTML_}
	 * for retrieving the DOM element of the <code>before</code> widget.
	 * @return DOMElement
	 */
	public getFirstNode_(): HTMLElement | undefined {
		for (var w: Widget | null = this; w; w = w.nextSibling) {
			var n = _getFirstNodeDown(w);
			if (n) return n;
		}
	}

	/** Removes the corresponding DOM content of the specified child.
	 * It is called by {@link #removeChild} to remove the DOM content.
	 * <p>The default implementation of this method will invoke {@link #removeHTML_}
	 * if the ignoreDom argument is false or not specified.
	 * <p>Overrides this method or {@link #removeHTML_} if you have to
	 * remove DOM elements other than child's node (and the descendants).
	 * @param zk.Widget child the child widget to remove
	 * @param boolean ignoreDom whether to remove the DOM element
	 */
	public removeChildHTML_(child: Widget, ignoreDom?: boolean): void {
		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(child, cf))
			zk.currentFocus = null;

		var n = child.$n(), ary: HTMLElement[] = [];
		if (n) {
			var sib = n.previousSibling;
			if (child['prolog'] && _isProlog(sib))
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
	 * @param Array n an array of {@link DOMElement} to remove.
	 * If this widget is associated with a DOM element ({@link #$n} returns non-null),
	 * n is a single element array.
	 * If this widget is not associated with any DOM element, an array of
	 * child widget's DOM elements are returned.
	 */
	public removeHTML_(n: HTMLElement | HTMLElement[]): void {
		_rmDom(this, n);
		this.clearCache();
	}

	/**
	 * Returns the DOM element that this widget is bound to.
	 * It is null if it is not bound to the DOM tree, or it doesn't have the associated DOM node (for example, {@link zul.utl.Timer}).
	 * <p>Notice that {@link #desktop} is always non-null if it is bound to the DOM tree.
	 * In additions, this method is much faster than invoking jq() (see {@link _global_.jq},
	 * since it caches the result (and clean up at the {@link #unbind_}).
	 * <pre><code>var n = wgt.$n();</code></pre>
	 * @return DOMElement
	 * @see #$n(String)
	 */
	/** Returns the child element of the DOM element(s) that this widget is bound to.
	 * This method assumes the ID of the child element the concatenation of
	 * {@link #uuid}, -, and subId. For example,
<pre><code>var cave = wgt.$n('cave'); //the same as jq('#' + wgt.uuid + '-' + 'cave')[0]</code></pre>
	 * Like {@link #$n()}, this method caches the result so the performance is much better
	 * than invoking jq() directly.
	 * @param String subId the sub ID of the child element
	 * @return DOMElement
	 * @see #$n()
	 */
	public $n(subId?: string): DOMFieldValue {
		if (subId) {
			let n = this._subnodes[subId];
			if (!n && this.desktop) {
				n = jq(this.uuid + '-' + subId, zk)[0];
				this._subnodes[subId] = n ? n : 'n/a';
			}
			return n == 'n/a' ? null : n as HTMLElement;
		}
		let n = this._node;
		if (!n && this.desktop && !this._nodeSolved) {
			this._node = n = jq(this.uuid, zk)[0];
			this._nodeSolved = true;
		}
		return n;
	}

	/**
	 * Returns the DOM element that this widget is bound to. (Never null)
	 * @return DOMElement
	 * @see #$n_(String)
	 * @since 10.0
	 */
	/** Returns the child element of the DOM element(s) that this widget is bound to.
	 *  (Never null)
	 * @param String subId the sub ID of the child element
	 * @return DOMElement
	 * @see #$n_()
	 * @since 10.0
	 */
	public $n_(subId?: string): HTMLElement {
		let n = this.$n(subId);
		if (n == null) {
			throw 'Node ' + (subId ? 'with ' + subId : '') + ' is not found!';
		}
		return n;
	}

	/**
	 * Returns the service instance from the current widget, if any.
	 * @since 8.0.0
	 * @return zk.Service
	 */
	public $service(): Service | null {
		var w: Widget | null = this;
		for (; w; w = w.parent) {
			if (w['$ZKAUS$'])
				break;
		}
		if (w) {
			if (!w._$service)
				w._$service = new zk.Service(w, this);
			return w._$service;
		}
		return null;
	}

	public $afterCommand(command: string, args?: unknown[]): void {
		var service = this.$service();
		if (service)
			service.$doAfterCommand(command, args);
	}

	/**
	 * Returns whether the widget has its own element bound to HTML DOM tree.
	 * @return boolean
	 * @since 7.0.0
	 */
	public isRealElement(): boolean {
		return true;
	}

	/**
	 * Returns the sub zclass name that cache for this widget.
	 * It returns the zclass if the subclass is empty or null,
	 * since it caches the result (and clean up at the {@link #setZclass(String)}).
	 * <pre><code>var subzcls = wgt.$s('hover'); // z-xxx-hover will be return</code></pre>
	 * @return String
	 * @see #getZclass()
	 * @since 7.0.0
	 */
	public $s(subclass?: string): string {
		if (subclass) {
			var subcls = this._subzcls[subclass];
			if (!subcls) {
				subcls = this._subzcls[subclass] = this.getZclass() + '-' + subclass;
			}
			return subcls;
		}
		return this.getZclass();
	}

	/** Clears the cached nodes (by {@link #$n}). */
	public clearCache(): void {
		this._node = null;
		this._subnodes = {};
		this._nodeSolved = false;
	}

	/** Returns the page that this widget belongs to, or null if there is
	 * no page available.
	 * @return zk.Page
	 */
	public getPage(): Page | undefined {
		var page, dt;
		for (page = this.parent; page; page = page.parent)
			if (page instanceof Page)
				return page;

		return (page = (dt = this.desktop as Desktop)._bpg) ?
			page : (dt._bpg = new Body(dt));
	}

	/** Returns whether this widget is being bound to DOM.
	 * In other words, it returns true if {@link #bind} is called
	 * against this widget or any of its ancestors.
	 * @return boolean
	 * @since 5.0.8
	 */
	public isBinding(): boolean {
		if (this.desktop)
			for (var w: Widget | null = this; w; w = w.parent)
				if (w._binding)
					return true;
		return false;
	}

	/**
	 * Forces the delaied rerendering children or itself to do now.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @since 7.0.2
	 */
	public rerenderNow_(skipper?: Skipper | null): void { // for Bug ZK-2281 and others life cycle issues when the dom of children of itself is undefined.
		_rerenderNow(this, skipper);
	}

	/** Binds this widget.
	 * It is called to associate (aka., attach) the widget with
	 * the DOM tree.
	 * <p>Notice that you rarely need to invoke this method, since
	 * it is called automatically (such as {@link #replaceHTML}
	 * and {@link #appendChild}).
	 * <p>Notice that you rarely need to override this method, either.
	 * Rather, override {@link #bind_} instead.
	 *
	 * @see #bind_
	 * @see #unbind
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @return zk.Widget this widget
	 */
	public bind(desktop?: Desktop | null, skipper?: Skipper | null): this {
		this._binding = true;

		_rerenderDone(this, skipper); //cancel pending async rerender
		if (this.z_rod)
			this.get$Class<typeof Widget>()._bindrod(this);
		else {
			var after = [], fn;
			this.bind_(desktop, skipper, after);
			while (fn = after.shift())
				fn();
		}

		delete this._binding;
		return this;
	}

	/** Unbinds this widget.
	 * It is called to remove the association (aka., detach) the widget from
	 * the DOM tree.
	 * <p>Notice that you rarely need to invoke this method, since
	 * it is called automatically (such as {@link #replaceHTML}).
	 * <p>Notice that you rarely need to override this method, either.
	 * Rather, override {@link #unbind_} instead.
	 *
	 * @see #unbind_
	 * @see #bind
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @param boolean keepRod [optional] used if the ROD flag needs to be kept.
	 * @return zk.Widget this widget
	 */
	public unbind(skipper?: Skipper | null, keepRod?: boolean): this {
		if (this._$service) {
			this._$service.destroy();
			this._$service = null;
		}
		_rerenderDone(this, skipper); //cancel pending async rerender
		if (this.z_rod)
			this.get$Class<typeof Widget>()._unbindrod(this, false, keepRod);
		else {
			var after: (() => void)[] = [];
			this.unbind_(skipper, after, keepRod);
			for (var j = 0, len = after.length; j < len;)
				after[j++]();
		}
		return this;
	}

	/** Callback when this widget is bound (aka., attached) to the DOM tree.
	 * It is called after the DOM tree has been modified (with the DOM content of this widget, i.e., {@link #redraw})
	 * (for example, by {@link #replaceHTML}).
	 * <p>Note: don't invoke this method directly. Rather, invoke {@link #bind} instead.
<pre><code>
wgt.bind();
</code></pre>
	 * <h3>Subclass Note</h3>
	 * <p>Subclass overrides this method to initialize the DOM element(s), such as adding a DOM listener. Refer to Widget and DOM Events and {@link #domListen_} for more information.
	 *
	 * @see #bind
	 * @see #unbind_
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @param Array after an array of function ({@link Function}) that will be invoked after {@link #bind_} has been called. For example,
<pre><code>
bind_: function (desktop, skipper, after) {
  this.$supers('bind_', arguments);
  var self = this;
  after.push(function () {
	self._doAfterBind(something);
	...
  });
}
</code></pre>
	 */
	protected bind_(desktop?: Desktop | null, skipper?: Skipper | null, after?: CallableFunction[]): void {
		this.get$Class<typeof Widget>()._bind0(this);

		this.desktop = desktop || (desktop = zk.Desktop.$(this.parent));

		var p = this.parent, v;
		this.bindLevel = p ? p.bindLevel + 1 : 0;

		if ((v = this._draggable) && v != 'false' && !_dragCtl(this))
			this.initDrag_();

		if (this._nvflex || this._nhflex)
			_listenFlex(this);

		this.bindChildren_(desktop as Desktop, skipper, after);
		var self = this;
		if (this.isListen('onBind')) {
			zk.afterMount(function () {
				if (self.desktop) //might be unbound
					self.fire('onBind');
			});
		}

		if (this.isListen('onAfterSize')) //Feature ZK-1672
			zWatch.listen({onSize: this});

		if (zk.mobile && after) {
			after.push(function () {
				setTimeout(function () {// lazy init
					self.bindSwipe_();
					self.bindDoubleTap_();
					self.bindTapHold_();
				}, 300);
			} as Callable);
		}
	}

	/** Binds the children of this widget.
	 * It is called by {@link #bind_} to invoke child's {@link #bind_} one-by-one.
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @param Array after an array of function ({@link Function}) that will be invoked after {@link #bind_} has been called. For example,
	 * @since 5.0.5
	 */
	protected bindChildren_(desktop?: Desktop, skipper?: Skipper | null, after?: CallableFunction[]): void {
		for (var child = this.firstChild, nxt; child; child = nxt) {
			nxt = child.nextSibling;
				//we have to store first since RefWidget will replace widget

			if (!skipper || !skipper.skipped(this, child)) {
				if (child.z_rod) this.get$Class<typeof Widget>()._bindrod(child);
				else child.bind_(desktop, null, after); //don't pass skipper
			}
		}
	}

	/** Callback when a widget is unbound (aka., detached) from the DOM tree.
	 * It is called before the DOM element(s) of this widget is going to be removed from the DOM tree (such as {@link #removeChild}.
	 * <p>Note: don't invoke this method directly. Rather, invoke {@link #unbind} instead.
	 * <p>Note: after invoking <code>this.$supers('unbind_', arguments)</code>,
	 * the association with DOM elements are lost. Thus it is better to invoke
	 * it as the last statement.
	 * <p>Notice that {@link #removeChild} removes DOM elements first, so
	 * {@link #unbind_} is called before {@link #beforeParentChanged_} and
	 * the modification of the widget tree. It means it is safe to access
	 * {@link #parent} and other information here
	 * @see #bind_
	 * @see #unbind
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper
	 * @param Array after an array of function ({@link Function})that will be invoked after {@link #unbind_} has been called. For example,
<pre><code>
unbind_: function (skipper, after) {
  var self = this;
  after.push(function () {
	self._doAfterUnbind(something);
	...
  }
  this.$supers('unbind_', arguments);
}
</code></pre>
	 * @param boolean keepRod [optional] used if the ROD flag needs to be kept.
	 */
	protected unbind_(skipper?: Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
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
			let ef = this.effects_[nm];
			if (ef) {
				ef.destroy();
			}
		}
		this.effects_ = {};
	}

	/** Unbinds the children of this widget.
	 * It is called by {@link #unbind_} to invoke child's {@link #unbind_} one-by-one.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper
	 * @param Array after an array of function ({@link Function})that will be invoked after {@link #unbind_} has been called. For example,
	 * @param boolean keepRod [optional] used if the ROD flag needs to be kept.
	 * @since 5.0.5
	 */
	protected unbindChildren_(skipper?: Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		for (var child = this.firstChild, nxt; child; child = nxt) {
			nxt = child.nextSibling; //just in case

			// check child's desktop for bug 3035079: Dom elem isn't exist when parent do appendChild and rerender
			if (!skipper || !skipper.skipped(this, child)) {
				if (child.z_rod) this.get$Class<typeof Widget>()._unbindrod(child, keepRod);
				else if (child.desktop) {
					child.unbind_(null, after, keepRod); //don't pass skipper
					//Bug ZK-1596: native will be transfer to stub in EE, store the widget for used in mount.js
					if (child.$instanceof(zk.Native))
						zAu._storeStub(child);
				}
			}
		}
	}

	/** Associates UUID with this widget.
	 * <p>Notice that {@link #uuid} is automatically associated (aka., bound) to this widget.
	 * Thus, you rarely need to invoke this method unless you want to associate with other identifiers.
	 * <p>For example, ZK Google Maps uses this method since it has to
	 * bind the anchors manually.
	 *
	 * @param String uuid the UUID to assign to the widgtet
	 * @param boolean add whether to bind. Specify true if you want to bind;
	 * false if you want to unbind.
	 */
	protected extraBind_(uuid: string, add: boolean): void {
		if (add == false) delete _binds[uuid];
		else _binds[uuid] = this;
	}

	public setFlexSize_(sz: zk.FlexSize, isFlexMin?: boolean): void {
		if (this._cssflex && this.parent && this.parent.getFlexContainer_() != null && !isFlexMin)
			return;
		var n = this.$n() as HTMLElement,
			zkn = zk(n);
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				n.style.height = '';
			else if (sz.height != '' || (typeof sz.height === 'number' && sz.height === 0 && !this.isFloating_())) //bug #2943174, #2979776, ZK-1159, ZK-1358
				this.setFlexSizeH_(n, zkn, sz.height as number, isFlexMin);
			else
				n.style.height = this._height || '';
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				n.style.width = '';
			else if (sz.width != '' || (typeof sz.width === 'number' && sz.width === 0 && !this.isFloating_())) //bug #2943174, #2979776, ZK-1159, ZK-1358
				this.setFlexSizeW_(n, zkn, sz.width as number, isFlexMin);
			else
				n.style.width = this._width || '';
		}
	}

	protected setFlexSizeH_(n: HTMLElement, zkn: JQZK, height: number, isFlexMin?: boolean): void {
		// excluding margin for F50-3000873.zul and B50-3285635.zul
		n.style.height = jq.px0(height - (!isFlexMin ? zkn.marginHeight() : 0));
	}

	protected setFlexSizeW_(n: HTMLElement, zkn: JQZK, width: number, isFlexMin?: boolean): void {
		// excluding margin for F50-3000873.zul and B50-3285635.zul
		n.style.width = jq.px0(width - (!isFlexMin ? zkn.marginWidth() : 0));
	}

	// ZK-5050
	protected beforeChildReplaced_(oldc: Widget, newc: Widget): void {
		//to be overridden
	}

	public beforeChildrenFlex_(kid: Widget): boolean {
		//to be overridden
		return true; //return true to continue children flex fixing
	}

	public afterChildrenFlex_(kid?: Widget): void {
		//to be overridden
	}

	// @since 7.0.1
	public afterChildMinFlexChanged_(kid: Widget, attr: string): void { //attr 'w' for width or 'h' for height
		//to be overridden, after each of my children fix the minimum flex (both width and height),
		// only if when myself is not in min flex.
	}

	public ignoreFlexSize_(attr: string): boolean { //'w' for width or 'h' for height calculation
		//to be overridden, whether ignore widget dimension in vflex/hflex calculation
		return false;
	}

	public ignoreChildNodeOffset_(attr: string): boolean { //'w' for width or 'h' for height calculation
		//to be overridden, whether ignore child node offset in vflex/hflex calculation
		return false;
	}

	public beforeMinFlex_(attr: zk.FlexOrient): NumberFieldValue { //'w' for width or 'h' for height
		//to be overridden, before calculate my minimum flex
		return undefined;
	}

	public beforeParentMinFlex_(attr: zk.FlexOrient): void { //'w' for width or 'h' for height
		//to be overridden, before my minimum flex parent ask my natural(not minimized) width/height
	}

	public afterChildrenMinFlex_(orient: zk.FlexOrient): void {
		//to be overridden, after my children fix the minimum flex (both width and height)
	}

	public afterResetChildSize_(attr: string): void {
		//to be overridden, after my children reset the size of (both width and height)
	}

	public isExcludedHflex_(): boolean {
		return jq(this.$n()!).css('position') == 'absolute'; // B60-ZK-917
		//to be overridden, if the widget is excluded for hflex calculation.
	}

	public isExcludedVflex_(): boolean {
		return jq(this.$n()!).css('position') == 'absolute'; // B60-ZK-917
		//to be overridden, if the widget is excluded for vflex calculation.
	}

	// to overridden this method have to fix the IE9 issue (ZK-483)
	// you can just add 1 px more for the offsetWidth
	public getChildMinSize_(attr: string, wgt: Widget): number { //'w' for width or 'h' for height
		if (attr == 'w') {
			// feature #ZK-314: zjq.minWidth function return extra 1px in IE9/10/11
			var wd = zjq.minWidth(wgt);
			if (zk.ie && zk.ie > 8 && zk.isLoaded('zul.wgt') && wgt.$instanceof(zul.wgt.Image)) {
				wd = zk(wgt).offsetWidth();
			}
			return wd;
		} else {
			return zk(wgt).offsetHeight();//See also bug ZK-483
		}
	}

	// for v/hflex, if the box-sizing is in border-box mode (like ZK 7+),
	// we should return the content size only (excluding padding and border)
	public getParentSize_(p: HTMLElement): {width: number; height: number} { //to be overridden
		var zkp = zk(p);
		return {height: zkp.contentHeight(), width: zkp.contentWidth()};
	}

	public getMarginSize_(attr: FlexOrient): number { //'w' for width or 'h' for height
		return zk(this).sumStyles(attr == 'h' ? 'tb' : 'lr', jq.margins);
	}

	protected getContentEdgeHeight_(height: number/*current calculated height*/): number {
		var p = this.$n(),
			body = document.body,
			fc = this.firstChild;

		// ZK-1524: Caption should be ignored
		fc = fc && zk.isLoaded('zul.wgt') && fc.$instanceof(window['zul']['wgt']['Caption']) ? fc.nextSibling : fc;

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

	protected getContentEdgeWidth_(width: number/*current calculated width*/): number {
		var p = this.$n(),
			body = document.body,
			fc = this.firstChild,
			// ZK-1524: Caption should be ignored
			fc = fc && zk.isLoaded('zul.wgt') && fc.$instanceof(window['zul']['wgt']['Caption']) ? fc.nextSibling : fc;

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

	public fixFlex_(): void {
		zFlex.fixFlex(this);
	}

	public fixMinFlex_(n: HTMLElement, orient: FlexOrient): number { //internal use
		return zFlex.fixMinFlex(this, n, orient);
	}

	protected clearCachedSize_(): void {
		delete this._hflexsz;
		delete this._vflexsz;
	}

	protected resetSize_(orient: zk.FlexOrient): void {
		var n = this.$n() as HTMLElement,
			hasScroll = this._beforeSizeHasScroll;
		if (hasScroll || (hasScroll == null && (n.scrollTop || n.scrollLeft))) // keep the scroll status, the issue also happens (not only IE8) if trigger by resize browser window.
			return;// do nothing Bug ZK-1885: scrollable div (with vflex) and tooltip
		n.style[orient == 'w' ? 'width' : 'height'] = '';
	}

	public getFlexContainer_(): HTMLElement | null | undefined {
		return this.getCaveNode();
	}

	protected getFlexDirection_(): string | null { // if it is null, by default it would check this display is block or not
		//to be overridden
		return null;
	}

	public afterClearFlex_(): void {
		//to be overridden
	}

	/** Initializes the widget to make it draggable.
	 * It is called if {@link #getDraggable} is set (and bound).
	 * <p>You rarely need to override this method, unless you want to handle drag-and-drop differently.
	 * <p>Default: use {@link zk.Draggable} to implement drag-and-drop,
	 * and the handle to drag is the element returned by {@link #getDragNode}
	 * @see #cleanDrag_
	 */
	public initDrag_(): void {
		var n = this.getDragNode();
		if (n) { //ZK-1686: should check if DragNode exist
			this._drag = new zk.Draggable(this, n, this.getDragOptions_(_dragoptions));
		}
	}

	/** Cleans up the widget to make it un-draggable. It is called if {@link #getDraggable}
	 * is cleaned (or unbound).
	 * <p>You rarely need to override this method, unless you want to handle drag-and-drop differently.
	 * @see #cleanDrag_
	 */
	public cleanDrag_(): void {
		var drag = this._drag;
		if (drag) {
			var n;
			if (zk.ie9 && (n = this.getDragNode()) && jq.nodeName(n, 'img'))
				jq(n).off('mousedown', zk.$void);

			this._drag = null;
			drag.destroy();
		}
	}

	/** Returns the DOM element of this widget that can be dragged.
	 * <p>Default, it returns {@link #$n}, i.e., the user can drag the widget anywhere.
	 * @return DOMElement
	 * @see #ignoreDrag_
	 */
	public getDragNode(): HTMLElement {
		return this.$n()!;
	}

	/** Returns the options used to instantiate {@link zk.Draggable}.
	 * <p>Default, it does nothing but returns the <code>map</code> parameter,
	 * i.e., the default options.
	 * <p>Though rarely used, you can override any option passed to
	 * {@link zk.Draggable}, such as the start effect, ghosting and so on.
	 * @param Map map the default implementation
	 * @return Map
	 */
	public getDragOptions_(map: Partial<DraggableOptions>): Partial<DraggableOptions> {
		return map;
	}

	/** Returns if the location that an user is trying to drag is allowed.
	 * <p>Default: it always returns false.
	 * If the location that an user can drag is static, override {@link #getDragNode},
	 * which is easier to implement.
	 * @param zk.Draggable pt
	 * @return boolean whether to ignore
	 */
	protected ignoreDrag_(pt: Draggable): boolean {
		return false;
	}

	/** Returns the widget if it allows to drop the specified widget (being dragged), or null if not allowed. It is called when the user is dragging a widget on top a widget.
	 * <p>Default: it check if the values of droppable and draggable match. It will check the parent ({@link #parent}), parent's parent, and so on until matched, or none of them are matched.
	 * <p>Notice that the widget to test if droppable might be the same as the widget being dragged (i.e., this == dragged). By default, we consider them as non-matched.
	 * @param zk.Widget dragged - the widget being dragged (never null).
	 * @return zk.Widget the widget to drop to.
	 */
	public getDrop_(dragged: Widget): Widget | null {
		if (this == dragged) {
			return null; //non-matched if the same target. Bug for ZK-1565
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
		return this.parent ? this.parent.getDrop_(dragged) : null;
	}

	/** Called to have some visual effect when the user is dragging a widget over this widget and this widget is droppable.
	 * Notice it is the effect to indicate a widget is droppable.
	 * <p>Default, it adds the CSS class named 'z-drag-over' if over is true, and remove it if over is false.
	 * @param boolean over whether the user is dragging over (or out, if false)
	 */
	public dropEffect_(over: boolean): void {
		jq(this.$n() || [])[over ? 'addClass' : 'removeClass']('z-drag-over');
	}

	/** Returns the message to show when an user is dragging this widget, or null if it prefers to clone the widget with {@link #cloneDrag_}.
	 * <p>Default, it return the inner text if if {@link #$n} returns a TR, TD, or TH element. Otherwise, it returns null and {@link #cloneDrag_} will be called to create a DOM element to indicate dragging.
	 * <p>Notice that the text would be encoded for XSS issue since 8.0.4.2. It should be considered when overriding.
	 * @return String the message to indicate the dragging, or null if clone is required
	 */
	protected getDragMessage_(): string | undefined {
		if (jq.nodeName(this.getDragNode(), 'tr', 'td', 'th')) {
			var n = this.$n('real') || this.getCaveNode(),
				msg = n ? n.innerText || '' : '';
			return msg ? zUtl.encodeXML(msg) : msg;
		}
	}

	/** Called to fire the onDrop event.
	 * You could override it to implement some effects to indicate dropping.
	 * <p>Default, it fires the onDrop event (with {@link #fire}).
	 * The subclass can override this method to pass more options such as the coordination where a widget is dropped.
	 * @param zk.Draggable drag the draggable controller
	 * @param zk.Event evt the event causes the drop
	 */
	public onDrop_(drag: Draggable, evt: Event): void {
		var data = zk.copy({dragged: drag.control}, evt.data);
		this.fire('onDrop', data, undefined, Widget.auDelay);
	}

	/** Called to create the visual effect representing what is being dragged.
	 * In other words, it creates the DOM element that will be moved with the mouse pointer when the user is dragging.
	 * <p>This method is called if {@link #getDragMessage_} returns null.
	 * If {@link #getDragMessage_} returns a string (empty or not),
	 * a small popup containing the message is created to represent the widget being dragged.
	 * <p>You rarely need to override this method, unless you want a different visual effect.
	 * @see #uncloneDrag_
	 * @param zk.Draggable drag the draggable controller
	 * @param Offset ofs the offset of the returned element (left/top)
	 * @return DOMElement the clone
	 */
	protected cloneDrag_(drag: Draggable, ofs: Offset): HTMLElement {
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

	/** Undo the visual effect created by {@link #cloneDrag_}.
	 * @param zk.Draggable drag the draggable controller
	 */
	protected uncloneDrag_(drag: Draggable): void {
		document.body.style.cursor = drag._orgcursor || '';

		jq(this.getDragNode()).removeClass('z-dragged');
	}

	//Feature ZK-1672: provide empty onSize function if the widget is listened to onAfterSize
	//	but the widget is never listened to onSize event
	public onSize(): void {}

	/**
	 * Called to fire the onAfterSize event.
	 * @since 6.5.2
	 */
	public onAfterSize(): void {
		if (this.desktop && this.isListen('onAfterSize')) {
			var n = this.$n() as HTMLElement, // ZK-5089: don't use "this.getCaveNode()" here
				width = n.offsetWidth,
				height = n.offsetHeight;
			if (this._preWidth != width || this._preHeight != height) {
				this._preWidth = width;
				this._preHeight = height;
				this.fire('onAfterSize', {width: width, height: height});
			}
		}
	}

	/** Bind swipe event to the widget on tablet device.
	 * It is called if HTML 5 data attribute (data-swipeable) is set to true.
	 * <p>You rarely need to override this method, unless you want to bind swipe behavior differently.
	 * <p>Default: use {@link zk.Swipe} to implement swipe event.
	 * @see #doSwipe_
	 * @since 6.5.0
	 */
	public bindSwipe_ = zk.$void;
	/** Unbind swipe event to the widget on tablet device.
	 * It is called if swipe event is unbound.
	 * <p>You rarely need to override this method, unless you want to unbind swipe event differently.
	 * @see #doSwipe_
	 * @since 6.5.0
	 */
	public unbindSwipe_ = zk.$void;

	/** Bind double click event to the widget on tablet device.
	 * It is called if the widget is listen to onDoubleClick event.
	 * <p>You rarely need to override this method, unless you want to implement double click behavior differently.
	 * @see #doDoubleClick_
	 * @since 6.5.0
	 */
	public bindDoubleTap_ = zk.$void;

	/** Unbind double click event to the widget on tablet device.
	 * It is called if the widget is listen to onDoubleClick event.
	 * <p>You rarely need to override this method, unless you want to implement double click behavior differently.
	 * @see #doDoubleClick_
	 * @since 6.5.0
	 */
	public unbindDoubleTap_ = zk.$void;

	/** Bind right click event to the widget on tablet device.
	 * It is called if the widget is listen to onRightClick event.
	 * <p>You rarely need to override this method, unless you want to implement right click behavior differently.
	 * @see #doRightClick_
	 * @since 6.5.1
	 */
	public bindTapHold_ = zk.$void;

	/** Unbind right click event to the widget on tablet device.
	 * It is called if the widget is listen to onRightClick event.
	 * <p>You rarely need to override this method, unless you want to implement right click behavior differently.
	 * @see #doRightClick_
	 * @since 6.5.1
	 */
	public unbindTapHold_ = zk.$void;

	/** Sets the focus to this widget.
	 * This method will check if this widget can be activated by invoking {@link #canActivate} first.
	 * <p>Notice: don't override this method. Rather, override {@link #focus_},
	 * which this method depends on.
	 * @param int timeout how many milliseconds before changing the focus. If not specified or negative, the focus is changed immediately,
	 * @return boolean whether the focus is gained to this widget.
	 */
	public focus(timeout?: number): boolean {
		return this.canActivate({checkOnly: true})
			&& zk(this.$n()).isRealVisible()
			&& this.focus_(timeout);
	}

	/** Called by {@link #focus} to set the focus.
	 * <p>Default: call child widget's focus until it returns true, or no child at all.
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If a widget is able to gain focus, it shall override this method to invoke {@link _global_.jqzk#focus}.</li>
	 * <li>It is called only if the DOM element is real visible (so you don't need to check again)</li>
	 * </ul>
<pre><code>
focus_: function (timeout) {
  zk(this.$n('foo').focus(timeout);
  return true;
}
</pre></code>
	 * @param int timeout how many milliseconds before changing the focus. If not specified or negative, the focus is changed immediately,
	 * @return boolean whether the focus is gained to this widget.
	 * @since 5.0.5
	 */
	public focus_(timeout?: number): boolean {
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

	/** Checks if this widget can be activated (gaining focus and so on).
	 * <p>Default: return false if it is not a descendant of
	 * {@link _global_.zk#currentModal}.
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>checkOnly: not to change focus back to modal dialog if unable to
	 * activate. If not specified, the focus will be changed back to
	 * {@link _global_.zk#currentModal}.
	 * In additions, if specified, it will ignore {@link zk#busy}, which is set
	 * if {@link zk.AuCmd0#showBusy} is called.
	 * This flag is usually set by {@link #focus}, and not set
	 * if it is caused by user's activity, such as clicking.</li>
	 * </ul>
	 * The reason to ignore busy is that we allow application to change focus
	 * even if busy, while the user cannot.
	 * @return boolean
	 */
	public canActivate(opts?: Partial<{ checkOnly: boolean }>): boolean {
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
	/** Smart-updates a property of the peer component associated with this widget, running at the server, with the specified value.
	 * <p>It is actually fired an AU request named <code>setAttr</code>, and
	 * it is handled by the <code>updateByClient</code> method in <code>org.zkoss.zk.ui.AbstractComponent</code> (at the server).
	 * <p>By default, it is controlled by a component attribute called <code>org.zkoss.zk.ui.updateByClient</code>.
	 * And, it is default to false.
	 * Thus, the component developer has to override <code>updateByClient</code> at
	 * the server (in Java) and then update it rather than calling back superclass.
	 * For example,
	 * <pre><code>protected void updateByClient(String name, Object value) {
	if ("disabled".equals(name))
		setDisabled(value instanceof Boolean && ((Boolean)value).booleanValue());
	else
		super.updateByClient(name, value);
}</code></pre>
	 *
	 * @param String name the property name
	 * @param Object value the property value
	 * @param int timeout the delay before sending out the AU request. It is optional. If omitted, -1 is assumed (i.e., it will be sent with next non-deferrable request).
	 * @see zAu#send
	 */
	public smartUpdate(nm: string, val: unknown, timeout?: number): void {
		zAu.send(new zk.Event(this, 'setAttr', [nm, val]),
			(timeout !== undefined && timeout >= 0) ? timeout : -1);
	}

	//widget event//
	/** Fire a widget event.
	 * @param zk.Event evt the event to fire
	 * @param int timeout the delay before sending the non-deferrable AU request (if necessary).
	 * If not specified or negative, it is decided automatically.
	 * It is ignored if no non-deferrable listener is registered at the server.
	 * @return zk.Event the event being fired, i.e., evt.
	 * @see #fire
	 * @see #listen
	 */
	public fireX(evt: Event, timeout?: number): Event {
		var oldtg = evt.currentTarget;
		evt.currentTarget = this;
		try {
			var evtnm = evt.name,
				lsns = this._lsns[evtnm],
				len = lsns ? lsns.length : 0;
			if (len) {
				for (var j = 0; j < len;) {
					var inf = lsns[j++], o = inf[0];
					(inf[1] || o[evtnm]).call(o, evt);
					if (evt.stopped) return evt; //no more processing
				}
			}

			if (!evt.auStopped) {
				var toServer = evt.opts && evt.opts.toServer;
				if (toServer || (this.inServer && this.desktop)) {
					var asap = toServer || this._asaps[evtnm];
					if (asap == null) {
						var ime = this.get$Class<typeof Widget>()._importantEvts;
						if (ime) {
							var ime = ime[evtnm];
							if (ime != null)
								asap = ime;
						}
					}
					if (asap != null //true or false
					|| evt.opts.sendAhead)
						this.sendAU_(evt,
							asap ? ((timeout !== undefined && timeout >= 0) ? timeout : Widget.auDelay) : -1);
				}
			}
			return evt;
		} finally {
			evt.currentTarget = oldtg;
		}
	}

	/** Callback before sending an AU request.
	 * It is called by {@link #sendAU_}.
	 * <p>Default: this method will stop the event propagation
	 * and prevent the browser's default handling
	 * (by calling {@link zk.Event#stop}),
	 * if the event is onClick, onRightClick or onDoubleClick.
	 * <p>Notice that {@link #sendAU_} is called against the widget sending the AU request
	 * to the server, while {@link #beforeSendAU_} is called against the event's
	 * target (evt.target).
	 *
	 * <p>Notice that since this method will stop the event propagation for onClick,
	 * onRightClick and onDoubleClick, it means the event propagation is stopped
	 * if the server registers a listener. However, it doesn't stop if
	 * only a client listener is registered (and, in this case, {@link zk.Event#stop}
	 * must be called explicitly if you want to stop).
	 *
	 * @param zk.Widget wgt the widget that causes the AU request to be sent.
	 * It will be the target widget when the server receives the event.
	 * @param zk.Event evt the event to be sent back to the server.
	 * Its content will be cloned to the AU request.
	 * @see #sendAU_
	 * @since 5.0.2
	 */
	protected beforeSendAU_(wgt: Widget, evt: Event): void {
		var en = evt.name;
		if (en == 'onClick' || en == 'onRightClick' || en == 'onDoubleClick')
			evt.shallStop = true;//Bug: 2975748: popup won't work when component with onClick handler
	}

	/** Sends an AU request to the server.
	 * It is invoked when {@link #fire} will send an AU request to the server.
	 *
	 * <p>Override Notice: {@link #sendAU_} will call evt.target's
	 * {@link #beforeSendAU_} to give the original target a chance to
	 * process it.
	 *
	 * @param zk.Event evt the event that will be sent to the server.
	 * @param int timeout the delay before really sending out the AU request
	 * @see #fire
	 * @see #beforeSendAU_
	 * @see zAu#sendAhead
	 * @since 5.0.1
	 */
	protected sendAU_(evt: Event, timeout: number): void {
		(evt.target || this).beforeSendAU_(this, evt);
		evt = new zk.Event(this, evt.name, evt.data, evt.opts, evt.domEvent);
			//since evt will be used later, we have to make a copy and use this as target
		if (evt.opts.sendAhead) zAu.sendAhead(evt, timeout);
		else zAu.send(evt, timeout);
	}

	/** Check whether to ignore the click which might be caused by
	 * {@link #doClick_}
	 * {@link #doRightClick_}, or {@link #doDoubleClick_}.
	 * <p>Default: return false.
	 * <p>Deriving class might override this method to return true if
	 * it wants to ignore the click on certain DOM elements, such as
	 * the open icon of a treerow.
	 * <p>Notice: if true is returned, {@link #doClick_}
	 * {@link #doRightClick_}, and {@link #doDoubleClick_} won't be called.
	 * In additions, the popup and context of {@link zul.Widget} won't be
	 * handled, either.
	 * @param zk.Event the event that causes the click ({@link #doClick_}
	 * {@link #doRightClick_}, or {@link #doDoubleClick_}).
	 * @return boolean whether to ignore it
	 * @since 5.0.1
	 */
	public shallIgnoreClick_(evt: Event): boolean | undefined {
		return false;
	}

	/** Fire a widget event. An instance of {@link zk.Event} is created to represent the event.
	 *
	 * <p>The event listeners for this event will be called one-by-one unless {@link zk.Event#stop} is called.
	 *
	 * <p>If the event propagation is not stopped (i.e., {@link zk.Event#stop} not called)
	 * and {@link #inServer} is true, the event will be converted to an AU request and sent to the server.
	 Refer to <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Communication/AU_Requests/Client-side_Firing">ZK Client-side Reference: AU Requests: Client-side Firing</a> for more information.
	 * @param String evtnm the event name, such as onClick
	 * @param Object data [optional] the data depending on the event ({@link zk.Event}).
	 * @param Map opts [optional] the options. Refer to {@link zk.Event#opts}
	 * @param int timeout the delay before sending the non-deferrable AU request (if necessary).
	 * If not specified or negative, it is decided automatically.
	 * It is ignored if no non-deferrable listener is registered at the server.
	 * @return zk.Event the event being fired.
	 * @see #fire
	 * @see #listen
	 */
	public fire(evtnm: string, data?: unknown, opts?: EventOptions | null, timeout?: number): Event {
		return this.fireX(new zk.Event(this, evtnm, data, opts), timeout);
	}

	/** Registers listener(s) to the specified event. For example,
<pre><code>
wgt.listen({
  onClick: wgt,
  onOpen: wgt._onOpen,
  onMove: [o, o._onMove]
});
</code></pre>
	 * <p>As shown above, you can register multiple listeners at the same time, and echo value in infos can be a target, a function, or a two-element array, where the first element is a target and the second the function.
	 * A target can be any object that this will reference to when the event listener is called.
	 * Notice it is not {@link zk.Event#target}. Rather, it is <code>this</code> when the listener is called.
	 * <p>If the function is not specified, the target must have a method having the same name as the event. For example, if wgt.listen({onChange: target}) was called, then target.onChange(evt) will be called when onChange event is fired (by {@link #fire}). On the other hand, if the target is not specified, the widget is assumed to be the target.
	 * @param Map infos a map of event listeners.
	 * Each key is the event name, and each value can be the target, the listener function, or a two-element array, where the first element is the target and the second the listener function.
	 * Notice that the target is not {@link zk.Event#target}. Rather, it is <code>this</code> when the listener is called.
	 * @param int priority the higher the number, the earlier it is called. If omitted, 0 is assumed.
	 * If a widget needs to register a listener as the default behavior (such as zul.wnd.Window's onClose), -1000 is suggested
	 * @return zk.Widget this widget
	 * @see #unlisten
	 * @see #fire
	 * @see #fireX
	 * @see #setListeners
	 * @see #setListener
	 */
	public listen(infs: Record<string, unknown>, priority?: number): this {
		priority = priority ? priority : 0;
		for (var evt in infs) {
			var inf = infs[evt];
			if (jq.isArray(inf)) inf = [inf[0] || this, inf[1]];
			else if (typeof inf == 'function') inf = [this, inf];
			else inf = [inf || this, null];
			(inf as {priority: number}).priority = priority;

			var lsns = this._lsns[evt];
			if (!lsns) {
				this._lsns[evt] = [inf as {priority: number}];
			} else {
				for (var j = lsns.length; ;) {
					if (--j < 0 || lsns[j].priority >= priority) {
						lsns.splice(j + 1, 0, inf as { priority: number });
						break;
					}
				}
			}
		}
		return this;
	}

	/** Removes a listener from the specified event.
<pre><code>
wgt.unlisten({
  onClick: wgt,
  onOpen: wgt._onOpen,
  onMove: [o, o._onMove]
});
</code></pre>
	 * @param Map infos a map of event listeners.
	 * Each key is the event name, and each value can be the target, the listener function, or a two-element array, where the first element is the target and the second the listener function.
	 * @return zk.Widget this widget
	 * @see #listen
	 * @see #isListen
	 * @see #fire
	 * @see #fireX
	 */
	public unlisten(infs: Record<string, unknown>): this {
		l_out:
		for (var evt in infs) {
			var inf = infs[evt],
				lsns = this._lsns[evt], lsn;
			for (var j = lsns ? lsns.length : 0; j--;) {
				lsn = lsns[j];
				if (jq.isArray(inf)) inf = [inf[0] || this, inf[1]];
				else if (typeof inf == 'function') inf = [this, inf];
				else inf = [inf || this, null];
				if (lsn[0] == (inf as Array<unknown>)[0] && lsn[1] == (inf as Array<unknown>)[1]) {
					lsns.splice(j, 1);
					continue l_out;
				}
			}
		}
		return this;
	}

	/** Returns if a listener is registered for the specified event.
	 * @param String evtnm the event name, such as onClick.
	 * @param Map opts [optional] the options. If omitted, it checks only if the server registers any non-deferrable listener, and if the client register any listener. Allowed values:
	 * <ul>
	 * <li>any - in addition to the server's non-deferrable listener and client's listener, it also checks deferrable listener, and the so-called important events</li>
	 * <li>asapOnly - it checks only if the server registers a non-deferrable listener, and if any non-deferrable important event. Use this option, if you want to know whether an AU request will be sent.</li>
	 * </ul>
	 * @return boolean
	 */
	public isListen(evt: string, opts?: Partial<{any: boolean; asapOnly: boolean}>): boolean {
		var v = this._asaps[evt];
		if (v) return true;
		if (opts) {
			if (opts.asapOnly) {
				v = this.get$Class<typeof Widget>()._importantEvts;
				return v !== undefined && (v as object)[evt] != null;
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
	 * It is similar to {@link #listen}, except
	 * <ul>
	 * <li>It will 'remember' what the listeners are, such that it can unlisten
	 * by specifying null as the value of the <code>infs</code> argument</li>
	 * <li>The function can be a string and it will be converted to {@link Function}
	 * automatically.</li>
	 * </ul>
	 * <p>This method is mainly designed to be called by the application running
	 * at the server.
	 *
	 * <p>Example:
<pre><code>
wgt.setListeners({
 onChange: function (event) {this.doSomething();},
 onFocus: 'this.doMore();',
 onBlur: null //unlisten
});
</code></pre>
	 * @param Map infos a map of event listeners.
	 * Each key is the event name, and each value is a string, a function or null.
	 * If the value is null, it means unlisten.
	 * If the value is a string, it will be converted to a {@link Function}.
	 * Notice that the target is not {@link zk.Event#target}. Rather, it is <code>this</code> when the listener is called.
	 */
	public setListeners(infs: Record<string, unknown & {priority: number}[]>): void {
		for (var evt in infs)
			this.setListener(evt, infs[evt]);
	}
	// since 8.0
	public setListeners0(infs: Record<string, unknown & {priority: number}[]>): void { //used by server
		for (var evt in infs)
			this.setListener0(evt, infs[evt]);
	}
	/** Sets a listener that can be unlistened easily.
	 * It is designed to be called from server.
	 * For client-side programming, it is suggested to use {@link #listen}.
	 * <p>It is based {@link #listen}, but, unlike {@link #listen}, the second
	 * invocation for the same event will unlisten the previous one automatically.
	 * <p>In additions, if the function (specified in the second element of inf)
	 * is null, it unlistens the previous invocation.
	 * @param Array inf a two-element array. The first element is the event name,
	 * while the second is the listener function
	 * @see #setListeners
	 */
	public setListener(inf: [string, unknown]): void;
	/** Sets a listener
	 * It is designed to be called from server.
	 * For client-side programming, it is suggested to use {@link #listen}.
	 * Use it only if you want to unlisten the listener registered at the
	 * server (by use of the client namespace).
	 * <p>It is based {@link #listen}, but, unlike {@link #listen}, the second
	 * invocation for the same event will unlisten the previous one automatically.
	 * <p>In additions, if fn is null, it unlistens the previous invocation.
	 * @param String evt the event name
	 * @param Function fn the listener function.
	 * If null, it means unlisten.
	 * @see #setListeners
	 * @see #listen
	 */
	public setListener(evt: string, fn: unknown): void;
	public setListener(evt: string | [string, unknown], fn?: unknown): void { //used by server
		if (jq.isArray(evt)) {
			fn = evt[1];
			evt = evt[0];
		}

		var bklsns = this._bklsns,
			oldfn = bklsns[evt],
			inf = {};
		if (oldfn) { //unlisten first
			delete bklsns[evt];
			inf[evt] = oldfn;
			this.unlisten(inf);
		}
		if (fn) {
			inf[evt] = bklsns[evt]
				= typeof fn != 'function' ? new Function('var event=arguments[0];' + fn) : fn;
			this.listen(inf);
		}
	}
	// since 8.0, it won't unlisten the old function.
	public setListener0(evt: string | [string, unknown], fn: unknown): void { //used by server
		if (jq.isArray(evt)) {
			fn = evt[1];
			evt = evt[0];
		}

		var bklsns = this._bklsns,
			inf = {};
		if (fn) {
			inf[evt] = bklsns[evt]
				= typeof fn != 'function' ? new Function('var event=arguments[0];' + fn) : fn;
			this.listen(inf);
		}
	}

	public setOverride(nm: string | [string, unknown], val: unknown): void { //used by server (5.0.2)
		if (jq.isArray(nm)) {
			val = nm[1];
			nm = nm[0];
		}
		if (val) {
			var oldnm = '$' + nm;
			if (this[oldnm] == null && this[nm]) //only once
				this[oldnm] = this[nm];
			this[nm] = val;
				//use eval, since complete func decl
		} else {
			var oldnm = '$' + nm;
			this[nm] = this[oldnm]; //restore
			delete this[oldnm];
		}
	}

	public setOverrides(infs: {[key: string]: unknown}[]): void { //used by server
		for (var nm in infs)
			this.setOverride(nm, infs[nm]);
	}

	//ZK event handling//
	/** Called when the user clicks or right-clicks on widget or a child widget.
	 * It is called before {@link #doClick_} and {@link #doRightClick_}.
	 * <p>Default: does nothing but invokes the parent's {@link #doSelect_}.
	 * Notice that it does not fire any event.
	 * <p>Deriving class that supports selection (such as {@link zul.sel.ItemWidget})
	 * shall override this to handle the selection.
	 * <p>Technically, the selection can be handled in {@link #doClick_}.
	 * However, it is better to handle here since this method is invoked first
	 * such that the widget will be selected before one of its descendant widget
	 * handles {@link #doClick_}.
	 * <p>Notice that calling {@link zk.Event#stop} will stop the invocation of
	 * parent's {@link #doSelect_} and {@link #doClick_}/{@link #doRightClick_}.
	 * If you just don't want to call parent's {@link #doSelect_}, simply
	 * not to invoke super's doSelect_.
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doClick_
	 * @see #doRightClick_
	 * @since 5.0.1
	 */
	protected doSelect_(evt: Event): void {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doSelect_(evt);
		}
	}

	/** Called when the mouse is moved over this widget.
	 * It is called before {@link #doMouseOver_}.
	 * <p>Default: does nothing but invokes the parent's {@link #doTooltipOver_}.
	 * Notice that it does not fire any event.
	 * <p>Notice that calling {@link zk.Event#stop} will stop the invocation of
	 * parent's {@link #doTooltipOver_} and {@link #doMouseOver_}.
	 * If you just don't want to call parent's {@link #doMouseOver_}, simply
	 * not to invoke super's doMouseOver_.
	 * @since 5.0.5
	 * @see #doTooltipOut_
	 */
	protected doTooltipOver_(evt: Event): void {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doTooltipOver_(evt);
		}
	}

	/** Called when the mouse is moved out of this widget.
	 * It is called before {@link #doMouseOut_}.
	 * <p>Default: does nothing but invokes the parent's {@link #doTooltipOut_}.
	 * Notice that it does not fire any event.
	 * <p>Notice that calling {@link zk.Event#stop} will stop the invocation of
	 * parent's {@link #doTooltipOut_} and {@link #doMouseOut_}.
	 * If you just don't want to call parent's {@link #doMouseOut_}, simply
	 * not to invoke super's doMouseOut_.
	 * @since 5.0.5
	 * @see #doTooltipOver_
	 */
	protected doTooltipOut_(evt: Event): void {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doTooltipOut_(evt);
		}
	}

	/** Called when the user clicks on a widget or a child widget.
	 * A widget doesn't need to listen the click DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and call parent's doClick_
	 * if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>If a widget, such as zul.wgt.Button, handles onClick, it is better to override this method and <i>not</i> calling back the superclass.
	 * <p>Note: if {@link #shallIgnoreClick_} returns true, {@link #fireX} won't be
	 * called and this method invokes the parent's {@link #doClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doDoubleClick_
	 * @see #doRightClick_
	 * @see #doSelect_
	 */
	protected doClick_(evt: Event): void {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doClick_(evt);
		}
	}

	/** Called when the user double-clicks on a widget or a child widget.
	 * A widget doesn't need to listen the dblclick DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and call parent's
	 * doDoubleClick_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>Note: if {@link #shallIgnoreClick_} returns true, {@link #fireX} won't be
	 * called and this method invokes the parent's {@link #doDoubleClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doClick_
	 * @see #doRightClick_
	 */
	protected doDoubleClick_(evt: Event): void {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doDoubleClick_(evt);
		}
	}

	/** Called when the user right-clicks on a widget or a child widget.
	 * A widget doesn't need to listen the contextmenu DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and call parent's
	 * doRightClick_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>Note: if {@link #shallIgnoreClick_} returns true, {@link #fireX} won't be
	 * called and this method invokes the parent's {@link #doRightClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doClick_
	 * @see #doDoubleClick_
	 */
	protected doRightClick_(evt: Event): void {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doRightClick_(evt);
		}
	}

	/** Called when the user moves the mouse pointer on top of a widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseover DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseOver_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseDown_
	 * @see #doMouseUp_
	 * @see #doTooltipOver_
	 */
	protected doMouseOver_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOver_(evt);
		}
	}

	/** Called when the user moves the mouse pointer out of a widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseout DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseOut_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseDown_
	 * @see #doMouseUp_
	 * @see #doTooltipOut_
	 */
	protected doMouseOut_(evt): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOut_(evt);
		}
	}

	/** Called when the user presses down the mouse button on this widget (or one of its child widget).
	 * A widget doesn't need to listen the mousedown DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseDown_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseUp_
	 * @see #doClick_
	 */
	protected doMouseDown_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseDown_(evt);
		}
	}

	/** Called when the user presses up the mouse button on this widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseup DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseUp_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseDown_
	 * @see #doClick_
	 */
	protected doMouseUp_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseUp_(evt);
		}
	}

	/** Called when the user moves the mouse pointer over this widget (or one of its child widget).
	 * A widget doesn't need to listen the mousemove DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseMove_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseDown_
	 * @see #doMouseUp_
	 */
	protected doMouseMove_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseMove_(evt);
		}
	}

	/** Called when the user presses down a key when this widget has the focus ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keydown DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doKeyDown_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doKeyUp_
	 * @see #doKeyPress_
	 */
	protected doKeyDown_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyDown_(evt);
		}
	}

	/** Called when the user presses up a key when this widget has the focus ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keyup DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doKeyUp_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doKeyDown_
	 * @see #doKeyPress_
	 */
	protected doKeyUp_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyUp_(evt);
		}
	}

	/** Called when the user presses a key when this widget has the focus ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keypress DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doKeyPress_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doKeyDown_
	 * @see #doKeyUp_
	 */
	protected doKeyPress_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyPress_(evt);
		}
	}

	/** Called when the user paste text to this widget which has been the focused ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the paste DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doPaste_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doKeyDown_
	 * @see #doKeyUp_
	 * @see #doKeyPress_
	 */
	protected doPaste_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doPaste_(evt);
		}
	}

	/** Called when the user swipe left/right/up/down this widget.
	 * <p>For example,
<pre><code>
var opts = evt.opts, dir = opts.dir;
switch (dir) {
case 'left': doSwipeLeft(); break;
case 'right': doSwipeRight(); break;
case 'up': doSwipeUp(); break;
case 'down': doSwipeDown(); break;
}
</code></pre>
	 * To define swipe direction rather than default condition,
<pre><code>
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
</code></pre>
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doSwipe_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * @param zk.Event evt the widget event.
	 * @since 6.5.0
	 */
	protected doSwipe_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doSwipe_(evt);
		}
	}

	/** A utility to simplify the listening of <code>onFocus</code>.
	 * Unlike other doXxx_ (such as {@link #doClick_}), a widget needs to listen
	 * the onFocus event explicitly if it might gain and lose the focus.
	 * <p>For example,
<pre><code>
var fn = this.$n('focus');
this.domListen_(fn, 'onFocus', 'doFocus_');
this.domListen_(fn, 'onBlur', 'doBlur_');
</code></pre>
	 *<p>Of course, you can listen it with jQuery DOM-level utilities, if you pefer to handle it differently.
	 *
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doFocus_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doBlur_
	 */
	protected doFocus_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doFocus_(evt);
		}
	}

	/** A utility to simplify the listening of <code>onBlur</code>.
	 * Unlike other doXxx_ (such as {@link #doClick_}), a widget needs to listen
	 * the onBlur event explicitly if it might gain and lose the focus.
	 * <p>For example,
<pre><code>
var fn = this.$n('focus');
this.domListen_(fn, 'onFocus', 'doFocus_');
this.domListen_(fn, 'onBlur', 'doBlur_');
</code></pre>
	 *<p>Of course, you can listen it with jQuery DOM-level utilities, if you pefer to handle it differently.
	 *
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doBlur_ if the event propagation is not stopped ({@link zk.Event#stopped}).
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @see #doFocus_
	 */
	protected doBlur_(evt: Event): void {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doBlur_(evt);
		}
	}

	/** Resize zul.Scrollbar size after child added/removed or hide/show.
	 * @since 6.5.0
	 */
	protected doResizeScroll_(): void {
		var p = this.parent;
		if (p) p.doResizeScroll_();
	}

	//DOM event handling//
	/** Registers an DOM event listener for the specified DOM element (aka., node).
	 * You can use jQuery to listen the DOM event directly, or
	 * use this method instead.
<pre><code>
bind_: function () {
  this.$supers('bind_', arguments);
  this.domListen_(this.$n(), "onChange"); //fn is omitted, so _doChange is assumed
  this.domListen_(this.$n("foo"), "onSelect", "_doFooSelect"); //specify a particular listener
},
unbind_: function () {
  this.domUnlisten_(this.$n(), "onChange"); //unlisten
  this.domUnlisten_(this.$n("foo"), "onSelect", "_doFooSelect");
  this.$supers('unbind_', arguments);
},
_doChange_: function (evt) { //evt is an instance of zk.Event
  //event listener
},
_doFooSelect: function (evt) {
}
</code></pre>
	 * See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 *
	 * <h3>Design Mode</h3>
	 * If a widget is created and controlled by ZK Weaver for visual design,
	 * we call the widget is in design mode ({@link #$weave}).
	 * Furthermore, this method does nothing if the widget is in the design mode.
	 * Thus, if you want to listen a DOM event ({@link jq.Event}), you have
	 * to use jQuery directly.
	 * @param DOMElement node a node of this widget.
	 * It is usually retrieved by {@link #$n}.
	 * @param String evtnm the event name to register, such as onClick.
	 * @param Object fn the name ({@link String}) of the member method to handle the event,
	 * or the function ({@link Function}).
	 * It is optional. If omitted, <i>_doEvtnm</i> is assumed, where <i>evtnm</i>
	 * is the value passed thru the <code>evtnm</code> argument.
	 * For example, if the event name is onFocus, then the method is assumed to be
	 * _doFocus.
	 * @param String keyword the extra argumenet for the function, which is passed
	 * into the callback function. (since 7.0)
	 * @return zk.Widget this widget
	 * @see #domUnlisten_
	 */
	public domListen_(n: HTMLElement, evtnm: string, fn?: string | Callable, keyword?: string): this {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn, keyword);
			jq(n, zk).on(inf[0], inf[1]);
		}
		return this;
	}

	/** Un-registers an event listener for the specified DOM element (aka., node).
	 * <p>Refer to {@link #domListen_} for more information.
	 * @param DOMElement node a node of this widget.
	 * It is usually retrieved by {@link #$n}.
	 * @param String evtnm the event name to register, such as onClick.
	 * @param Object fn the name ({@link String}) of the member method to handle the event,
	 * or the function ({@link Function}).
	 * It is optional. If omitted, <i>_doEvtnm</i> is assumed, where <i>evtnm</i>
	 * is the value passed thru the <code>evtnm</code> argument.
	 * For example, if the event name is onFocus, then the method is assumed to be
	 * _doFocus.
	 * @param String keyword the extra argumenet for the function, which is passed
	 * into the callback function. (since 7.0)
	 * @return zk.Widget this widget
	 * @see #domListen_
	 */
	public domUnlisten_(n: HTMLElement, evtnm: string, fn?: string | Callable, keyword?: string): this {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn, keyword);
			jq(n, zk).off(inf[0], inf[1]);
		}
		return this;
	}

	/**
	 * Listens to onFitSize event. Override if a subclass wants to skip listening
	 * or have extra processing.
	 * @see #unlistenOnFitSize_
	 * @since 5.0.8
	 */
	public listenOnFitSize_(): void {
		if (!this._fitSizeListened && (this._hflex == 'min' || this._vflex == 'min')) {
			zWatch.listen({onFitSize: [this, zFlex.onFitSize]});
			this._fitSizeListened = true;
		}
	}

	/**
	 * Unlistens to onFitSize event. Override if a subclass wants to skip listening
	 * or have extra processing.
	 * @see #listenOnFitSize_
	 * @since 5.0.8
	 */
	public unlistenOnFitSize_(): void {
		if (this._fitSizeListened) {
			zWatch.unlisten({onFitSize: [this, zFlex.onFitSize]});
			delete this._fitSizeListened;
		}
	}

	/** Converts a coordinate related to the browser window into the coordinate
	 * related to this widget.
	 * @param int x the X coordinate related to the browser window
	 * @param int y the Y coordinate related to the browser window
	 * @return Offset the coordinate related to this widget (i.e., [0, 0] is
	 * the left-top corner of the widget).
	 * @since 5.0.2
	 */
	public fromPageCoord(x: number, y: number): Offset {
		var ofs = zk(this).revisedOffset();
		return [x - ofs[0], y - ofs[1]];
	}

	/** Returns if the given watch shall be fired for this widget.
	 * It is called by {@link zWatch} to check if the given watch shall be fired
	 * @param String name the name of the watch, such as onShow
	 * @param zk.Widget p the parent widget causing the watch event.
	 * It is null if it is not caused by {@link _global_.zWatch#fireDown}.
	 * @param Map cache a map of cached result (since 5.0.8). Ignored if null.
	 * If specified, the result will be stored and used to speed up the processing
	 * @return boolean
	 * @since 5.0.3
	 */
	public isWatchable_(name: string, p: Widget, cache?: Record<string, unknown>): boolean {
		//if onShow, we don't check visibility since window uses it for
		//non-embedded window that becomes invisible because of its parent
		var strict = name != 'onShow', wgt;
		if (p)
			return this.isRealVisible({dom: true, strict: strict, until: p, cache: cache});

		for (wgt = this; ;) {
			//1. if native, $n() might be null or wrong (if two with same ID)
			//2. ZK-5058: if noDom, $n() is invisible by default.
			if (!wgt.$instanceof(zk.Native) && wgt.getMold() != 'nodom')
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

	public toJSON(): string { //used by JSON
		return this.uuid;
	}

	/** A widget call this function of its ancestor if it wants to know whether its ancestor prefer ignore float up event of it self.
	 * <p>Default: false.
	 * @return boolean
	 * @since 6.0.0
	 */
	public ignoreDescendantFloatUp_(des: Widget): boolean {
		return false;
	}

	// internal use only in zkmax package
	protected getDomEvtInf_(wgt: Widget, evtnm: string, fn: Callable, keyword?: string): [string, JQueryEventHandler] {
		return _domEvtInf(wgt, evtnm, fn, keyword);
	}

	/**
	 * Returns whether a widget should fireSized later when addChd was invoked
	 * Default: false.
	 */
	protected shallFireSizedLaterWhenAddChd_(): boolean {
		return false;
	}

	/** A callback called after a widget is composed but not yet be bound to DOM tree.
	 * @since 10.0.0
	 */
	protected afterCompose_(): void {
	}
	// internal use, since zk 8.0.0
	public static disableChildCallback(): void {
		_noChildCallback = true;
	}

	// internal use, since zk 8.0.0
	public static enableChildCallback(): void {
		_noChildCallback = false;
	}

	/** Retrieves the widget.
	 * @param Object n the object to look for. If it is a string,
	 * it is assumed to be UUID, unless it starts with '$'.
	 * For example, <code>zk.Widget.$('uuid')</code> is the same as <code>zk.Widget.$('#uuid')</code>,
	 * and both look for a widget whose ID is 'uuid'. On the other hand,
	 * <code>zk.Widget.$('$id') </code>looks for a widget whose ID is 'id'.<br/>
	 * and <code>zk.Widget.$('.className') looks for a widget whose CSS selector is 'className'. (since zk 8.0)<br/>
	 * If it is an DOM element ({@link DOMElement}), it will look up
	 * which widget it belongs to.<br/>
	 * If the object is not a DOM element and has a property called
	 * <code>target</code>, then <code>target</code> is assumed.
	 * Thus, you can pass an instance of {@link jq.Event} or {@link zk.Event},
	 * and the target widget will be returned.
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>exact - id must exactly match uuid (i.e., uuid-xx ignored).
	 * It also implies strict (since 5.0.2)</li>
	 * <li>strict - whether not to look up the parent node.(since 5.0.2)
	 * If omitted, false is assumed (and it will look up parent).</li>
	 * <li>child - whether to ensure the given element is a child element
	 * of the widget's main element ({@link #$n}). In most cases, if ID
	 * of an element is xxx-yyy, the the element must be a child of
	 * the element whose ID is xxx. However, there is some exception
	 * such as the shadow of a window.</li>
	 * </ul>
	 * @return zk.Widget
	 */
	public static $<T extends Widget>(n?: string | JQuery | JQuery.Event | Node | null | T,
					opts?: Partial<{exact: boolean; strict: boolean; child: boolean}>): T | null {
		if (n && n['zk'] && n['zk'].jq == n) //jq()
			n = n[0];

		if (!n)
			return null;

		if (Widget.isInstance(n)) {
			return n;
		}

		var wgt, id;
		if (typeof n == 'string') {
			//Don't look for DOM (there might be some non-ZK node with same ID)
			let query = n;

			// fix zk.$("$tree @treeitem") case
			if (query.indexOf(' ') == -1) {
				if ((id = n.charAt(0)) == '#') n = n.substring(1);
				else if (id == '$') {
					id = _globals[n.substring(1)];
					return id ? id[0] : null;
				}
				wgt = _binds[n]; //try first (since ZHTML might use -)
				if (!wgt)
					wgt = (id = n.indexOf('-')) >= 0 ? _binds[n.substring(0, id)] : null;
			}

			if (!wgt)
				return jq(query).zk.$() as T;
			return wgt;
		}

		if (!n['nodeType']) { //n could be an event (skip Element)
			var e1, e2;
			n = ((e1 = n['originalEvent']) ? e1.z$target : null)
				|| ((e1 = n['target']) && (e2 = e1.z$proxy) ? e2 : e1) || n; //check DOM event first
		}

		opts = opts || {};
		if (opts.exact)
			return _binds[n!['id'] as string] as T;

		for (; n; n = zk(n).vparentNode(true)) {
			try {
				id = n['id'] || (n['getAttribute'] ? n['getAttribute']('id') : '');
				if (id && typeof id == 'string') {
					wgt = _binds[id]; //try first (since ZHTML might use -)
					if (wgt)
						return wgt;

					var j = id.indexOf('-');
					if (j >= 0) {
						wgt = _binds[id = id.substring(0, j)];
						if (wgt) {
							if (!opts.child)
								return wgt;

							var n2 = wgt.$n();
							if (n2 && jq.isAncestor(n2, n as DOMFieldValue))
								return wgt;
						}
					}
				}
			} catch (e) {
				zk.debugLog(e.message || e);
			}
			if (opts.strict)
				break;
		}
		return null;
	}

	/** Called to mimic the mouse down event fired by the browser.
	 * It is used for implement a widget. In most cases, you don't need to
	 * invoke this method.
	 * <p>However, it is useful if the widget you are implemented will 'eat'
	 * the mouse-down event so ZK Client Engine won't be able to intercept it
	 * at the document level.
	 * @param zk.Widget wgt the widget that receives the mouse-down event
	 * @param boolean noFocusChange whether zk.currentFocus shall be changed to wgt.
	 * @param int which the button number that was pressed.
	 */
	public static mimicMouseDown_(wgt: Widget, noFocusChange?: boolean, which?: number): void { //called by mount
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
				setTimeout(function () {zk._cfByMD = false; zk._prevFocus = null;}, 0);
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
	 * Returns all elements with the given widget name.
	 * @param String name the widget name {@link #widgetName}.
	 * @return Array an array of {@link DOMElement}
	 * @since 5.0.2
	 */
	public static getElementsByName(name: string): HTMLElement[] {
		var els: {n: HTMLElement; w: Widget}[] = [];
		for (var wid in _binds) {
			if (name == '*' || name == _binds[wid].widgetName) {
				var _w = _binds[wid],
					n = _w.$n(), w;

				// force rod to render before query.
				if (!n && _w.z_rod) {
					var parent = _w;
					for (var p: zk.Widget|null = _w; p != null; p = p.parent) {
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
						w: w
					});
				}
			}
		}
		if (els.length) {
			// fixed the order of the component that have been changed dynamically.
			// (Bug in B30-1892446.ztl, B50-3095549.ztl, and B50-3131173.ztl)
			els.sort(function (a, b) {
				var w1: Widget | null = a.w,
					w2: Widget | null = b.w;
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
				let ele = els[i];
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
	 * Returns all elements with the given ID.
	 * @param String id the id of a widget, {@link #id}.
	 * @return Array an array of {@link DOMElement}
	 * @since 5.0.2
	 */
	public static getElementsById(id: string): HTMLElement[] {
		var els: HTMLElement[] = [];
		for (var n, wgts = _globals[id], i = wgts ? wgts.length : 0; i--;) {
			n = wgts[i].$n();
			if (n) els.unshift(n);
		}
		return els;
	}

	/**
	 * Returns the {@link zk.Widget} with the given Uuid.
	 * @param String uuid the uuid of a widget.
	 * @return zk.Widget
	 * @since 10.0.0
	 */
	public static getWidgetByUuid(uuid: string): Widget {
		return _binds[uuid];
	}

	//uuid//
	/** Converts an ID of a DOM element to UUID.
	 * It actually removes '-*'. For example, zk.Widget.uuid('z_aa-box') returns 'z_aa'.
	 * @param String subId the ID of a DOM element
	 * @return String the uuid of the widget (notice that the widget might not exist)
	 */
	public static uuid(id: string): string {
		var uuid = typeof id == 'object' ? id['id'] || '' : id,
			j = uuid.indexOf('-');
		return j >= 0 ? uuid.substring(0, j) : id;
	}

	/** Returns the next unique UUID for a widget.
	 * The UUID is unique in the whole browser window and does not conflict with the peer component's UUID.
	 * <p>This method is called automatically if {@link #$init} is called without uuid.
	 * @return String the next unique UUID for a widget
	 */
	public static nextUuid(): string {
		return '_z_' + _nextUuid++;
	}

	/** @deprecated we cannot really detect at the client if UUID is generated automatically.
	 * @param String uuid the UUID to test
	 * @return boolean
	 */
	public static isAutoId(id: string): boolean {
		return !id;
	}

	/** Registers a widget class.
	 * It is called automatically if the widget is loaded by WPD loader, so you rarely
	 * need to invoke this method.
	 * However, if you create a widget class at run time, you have to call this method explicitly.
	 * Otherwise, {@link #className}, {@link #getClass}, and {@link #newInstance}
	 * won't be applicable.
	 * <p>Notice that the class must be declared before calling this method.
	 * In other words, zk.$import(clsnm) must return the class of the specified class name.
<pre><code>
zk.Widget.register('foo.Cool'); //class name
zk.Widget.getClass('cool'); //widget name
</code></pre>
	 * @param String clsnm the class name, such as zul.wnd.Window
	 * @param boolean blankPreserved whether to preserve the whitespaces between child widgets when declared in iZUML. If true, a widget of clsnm will have a data member named blankPreserved (assigned with true). And, iZUML won't trim the whitespaces (aka., the blank text) between two adjacent child widgets.
	 */
	public static register(clsnm: string, blankprev: boolean): void {
		var cls = zk.$import(clsnm) as typeof Widget;
		cls.prototype.className = clsnm;
		var j = clsnm.lastIndexOf('.');
		if (j >= 0) clsnm = clsnm.substring(j + 1);
		_wgtcls[cls.prototype.widgetName = clsnm.toLowerCase()] = cls;
		if (blankprev) cls.prototype.blankPreserved = true;
	}

	/** Returns the class of the specified widget's name. For example,
<pre><code>
zk.Widget.getClass('combobox');
</code></pre>
	 *<p>Notice that null is returned if the widget is not loaded (or not exist) yet.
	 * @param String wgtnm the widget name, such as textbox.
	 * @return zk.Class the class of the widget.
	 * @see #newInstance
	 * @see #register
	 */
	public static getClass(wgtnm: string): typeof Widget {
		return _wgtcls[wgtnm];
	}

	/** Creates a widget by specifying the widget name.
	 * The widget name is the last part of the class name of a widget (and converting the first letter to lower case).
	 * For example, if a widget's class name is zul.inp.Textbox, then the widget name is textbox.
	 * <p>This method is usually used by tools, such as zk.zuml.Parser, rather than developers, since developers can create the widget directly if he knows the class name.
	 * @param String wgtnm the widget name, such as textbox.
	 * @param Map props [optional] the properties that will be passed to
	 * {@link #$init}.
	 * @see #getClass
	 * @see #register
	 * @return zk.Widget
	 */
	public static newInstance<T extends typeof Widget>(wgtnm: string, props?: Record<string, unknown>): InstanceType<T> {
		var cls = _wgtcls[wgtnm];
		if (!cls) {
			let msg;
			zk.error(msg = 'Unknown widget: ' + wgtnm);
			throw msg;
		}
		return new cls(props) as InstanceType<T>;
	}

	/** The default delay before sending an AU request when {@link #fire}
	 * is called (and the server has an ARAP event listener registered).
	 * <p>Default: 38 (Unit: miliseconds).
	 * @since 5.0.8
	 * @type int
	 */
	public static auDelay = 38;
	public static _bindrod(wgt: Widget): void {
		this._bind0(wgt);
		if (!wgt.z_rod)
			wgt.z_rod = 9; //Bug 2948829: don't use true which is used by real ROD, such as combo-rod.js

		for (var child = wgt.firstChild; child; child = child.nextSibling)
			this._bindrod(child);
	}
	public static _bind0(wgt: Widget): void { //always called no matter ROD or not
		_binds[wgt.uuid as string] = wgt;
		if (wgt.id)
			this._addGlobal(wgt);
	}
	public static _addGlobal(wgt: Widget): void { //note: wgt.id must be checked before calling this method
		var gs = _globals[wgt.id as string];
		if (gs)
			gs.push(wgt);
		else
			_globals[wgt.id as string] = cast([wgt]);
	}
	public static _unbindrod(wgt: Widget, nest?: boolean, keepRod?: boolean): void {
		this._unbind0(wgt);

		if (!nest || wgt.z_rod === 9) { //Bug 2948829: don't delete value set by real ROD
			if (!keepRod) delete wgt.z_rod;

			for (var child = wgt.firstChild; child; child = child.nextSibling) {
				this._unbindrod(child, true, keepRod);
				//Bug ZK-1827: native component with rod should also store the widget for used in mount.js(create function)
				if (child.$instanceof(zk.Native))
					zAu._storeStub(child);
			}
		}
	}
	public static _unbind0(wgt: Widget): void {
		if (wgt.id)
			this._rmGlobal(wgt);
		delete _binds[wgt.uuid as string];
		wgt.desktop = null;
		wgt.clearCache();
	}

	public static _rmGlobal(wgt: Widget): void {
		var gs = _globals[wgt.id as string];
		if (gs) {
			gs.$remove(wgt);
			if (!gs.length) delete _globals[wgt.id as string];
		}
	}
	public static readonly _TARGET = '__target__'; // used for storing the query widget target
	public static readonly _TARGETS = '__targets__'; // used for storing the query widget targets into one element,
							 // such as Treerow, Treechildren, and Treeitem.
	public static readonly _CURRENT_TARGET = '__ctarget__'; // used for storing the current query widget target
}
/**	@partial zk
 */
//@{
	/**
	 * A shortcut of <code>Widget.$()</code> function.
	 *
	 * Note: Widget is the same as <code>zk.Widget</code>.
	 * @return Widget
	 * @since 8.0.0
	 * @see Widget#$(Object, Map)
	 */
	//$: function () {}
//@};
// zk scope
export let $ = Widget.$;

// window scope
export const zkreg = Widget.register; //a shortcut for WPD loader

/** A reference widget. It is used as a temporary widget that will be
 * replaced with a real widget when {@link #bind_} is called.
 * <p>Developers rarely need it.
 * Currently, it is used only for the server to generate the JavaScript codes
 * for mounting.
 * @disable(zkgwt)
 */
// zk scope
export class RefWidget extends Widget {
	/** The class name (<code>zk.RefWidget</code>).
	 * @type String
	 * @since 5.0.3
	 */
	public override className = 'zk.RefWidget';
	/** The widget name (<code>refWidget</code>).
	 * @type String
	 * @since 5.0.3
	 */
	public override widgetName = 'refWidget';
	protected override bind_(): void {
		var w = Widget.$(this.uuid as string);
		if (!w) {
			zk.error('RefWidget not found: ' + this.uuid);
			return;
		}

		var p;
		if (p = w.parent) //shall be a desktop
			_unlink(p, w); //unlink only

		_replaceLink(this, w);
		this.parent = this.nextSibling = this.previousSibling = null;

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
 * @disable(zkgwt)
 */
// zk scope
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
export class Desktop extends Widget {
	declare public _cpsp?: SPush | null;
	declare private static _dt: Desktop | null;
	// FIXME: static isInstance(dtid: DesktopAccessor): boolean;
	// FIXME: type DesktopAccessor = string | Widget | HTMLElement | null;
	declare public _aureqs: Event[];
	declare public _bpg: Body;
	declare public _pfDoneIds?: string | null;
	declare public _pfRecvIds?: string | null;
	declare public obsolete?: boolean;

	//a virtual node that might have no DOM node and must be handled specially
	public z_virnd = true;

	public override bindLevel = 0;
	/** The class name (<code>zk.Desktop</code>).
	 * @type String
	 */
	public override className = 'zk.Desktop';
	/** The widget name (<code>desktop</code>).
	 * @type String
	 * @since 5.0.2
	 */
	public override widgetName = 'desktop';
	/** The request path.
	 * @type String
	 */
	public requestPath: string | undefined;

	public updateURI: StringFieldValue;
	public resourceURI: StringFieldValue;
	public contextURI: StringFieldValue;
	public stateless: BooleanFieldValue;

	/** Constructor
	 * @param String dtid the ID of the desktop
	 * @param String contextURI the context URI, such as <code>/zkdemo</code>
	 * @param String updateURI the URI of ZK Update Engine, such as <code>/zkdemo/zkau</code>
	 * @param String reqURI the URI of the request path.
	 * @param boolean stateless whether this desktop is used for a stateless page.
	 * Specify true if you want to use <a href="http://books.zkoss.org/wiki/Small_Talks/2009/July/ZK_5.0_and_Client-centric_Approach">the client-centric approach</a>.
	 */
	public constructor(dtid: string, contextURI?: string, updateURI?: string,
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

	public override bind_ = zk.$void;
	public override unbind_ = zk.$void;
	/** This method is voided (does nothing) since the desktop's ID
	 * can be changed.
	 * @param String id the ID
	 * @return zk.Widget this widget
	 */
	public override setId = zk.$void;

	//ZK-2663: Popup does not show up when its parent is native
	public override isRealVisible(): boolean {
		return true;
	}

	/** Returns the desktop of the specified desktop ID, widget, widget UUID, or DOM element.
	 * <p>Notice that the desktop's ID and UUID are the same.
	 * @param Object o a desktop's ID, a widget, a widget's UUID, or a DOM element.
	 * If not specified, the default desktop is assumed.
	 * @return zk.Desktop
	 */
	public static override $<T extends Desktop>(dtid: T): T
	public static override $(dtid?: string | Widget | HTMLElement | null): Desktop | null
	public static override $<T extends Desktop>(dtid?: string | T | null): T | Desktop | null {
		var w: T | Desktop | Widget | null;
		if (dtid) {
			if (Desktop.isInstance(dtid))
				return dtid;

			w = Desktop.all[dtid];
			if (w)
				return w as Desktop;

			w = Widget.$(dtid);
			for (; w; w = w.parent) {
				if (w.desktop)
					return w.desktop;
				if (w instanceof Desktop)
					return w;
			}
			return null;
		}

		if (w = Desktop._dt)
			return w as Desktop;
		for (dtid in Desktop.all)
			return Desktop.all[dtid];
		return null;
	}

	/** A map of all desktops (readonly).
	 * The key is the desktop ID and the value is the desktop.
	 * @type Map
	 */
	public static all: Record<string, Desktop> = {};

	public static _ndt = 0; //used in au.js/dom.js
	/** Checks if any desktop becomes invalid, and removes the invalid desktops.
	 * This method is called automatically when a new desktop is added. Application developers rarely need to access this method.
	 * @param int timeout how many miliseconds to wait before doing the synchronization
	 * @return zk.Desktop the first desktop, or null if no desktop at all.
	 */
	public static sync(timeout?: number): Desktop | null {
		var dts = Desktop.all, dt: Desktop;

		if (_syncdt) {
			clearTimeout(_syncdt);
			_syncdt = null;
		}

		if (timeout as number >= 0) // (undefined >= 0) is false
			_syncdt = setTimeout(function () {
				_syncdt = null;
				Desktop.sync();
			}, timeout); //Liferay on IE will create widgets later
		else {
			for (var dtid in dts)
				if (!_exists(dt = dts[dtid]) && dt.firstChild) { //to be safe, don't remove if no child)
					delete dts[dtid];
					--Desktop._ndt;
					if (Desktop._dt == dt)
						Desktop._dt = null;
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
	 * @param Desktop zk desktop
	 * @since 9.6.2
	 */
	public static destroy(desktop: zk.Desktop | null): void {
		if (desktop != null) {
			zAu._rmDesktop(desktop);
			delete zk.Desktop.all[desktop.id as string];
			--zk.Desktop._ndt;
		}
	}
}

export let _wgtutl = { //internal utilities
	setUuid(wgt: Widget, uuid: string): void { //called by au.js
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
	},
	//kids: whehter to move children of from to
	replace(from: Widget, to: Widget, kids: boolean): void { //called by mount.js
		_replaceLink(from, to);
		from.parent = from.nextSibling = from.previousSibling = null;

		if (kids) {
			to.lastChild = from.lastChild;
			for (var p = to.firstChild = from.firstChild; p; p = p.nextSibling)
				p.parent = to;
			to.nChildren = from.nChildren;
			from.firstChild = from.lastChild = null;
			from.nChildren = 0;
		}
		from.nChildren = 0;
	},

	autohide(): void { //called by effect.js
		if (!_floatings.length) {
			for (let n; n = _hidden.shift();)
				n.style.visibility = n.getAttribute('z_ahvis') || '';
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
					hide = function (f): boolean {
						var tf = _topnode(f);
						if (tf == tc || _zIndex(tf) < _zIndex(tc) || !$n.isOverlapped(f))
							return false;

						if (visi) {
							_hidden.push(n);
							try {
								n.setAttribute('z_ahvis', n.style.visibility);
							} catch (e) {
								zk.debugLog(e.message || e);
							}
							n.style.visibility = 'hidden';
						}
						return true; //processed
					};

				for (var k = _floatings.length; k--;)
					if (hide(_floatings[k].node))
						continue l_nxtel;

				if ((_hidden as Array<unknown>).$remove(n))
					n.style.visibility = n.getAttribute('z_ahvis') || '';
			}
	}
};
zk._wgtutl = _wgtutl;

/** A page.
 * Unlike the component at the server, a page is a widget.
 * @disable(zkgwt)
*/
// zk scope
export class Page extends Widget {
	declare public _applyMask?: Mask | null;
	//a virtual node that might have no DOM node and must be handled specially
	public z_virnd = true;

	protected override _style = 'width:100%;height:100%';
	/** The class name (<code>zk.Page</code>).
	 * @type String
	 */
	public override className = 'zk.Page';
	/** The widget name (<code>page</code>).
	 * @type String
	 * @since 5.0.2
	 */
	public override widgetName = 'page';

	/** Constructor.
	 * @param Map props the properties to assign to this page
	 * @param boolean contained whether this page is contained.
	 * By contained we mean this page is a top page (i.e., not included
	 * by the include widget) but it is included by other technologies,
	 * such as JSP.
	 */
	public constructor(props?, contained?: boolean) {
		super(props);
		this._fellows = {};


		if (contained) Page.contained.push(this);
	}
	/** Generates the HTML fragment for this macro component.
	 * <p>Default: it generate DIV to enclose the HTML fragment
	 * of all child widgets.
	 * @param Array out an array of HTML fragments.
	 */
	public override redraw(out: string[]): void {
		out.push('<div', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div>');
	}

	public static $redraw(this: Widget, out: string[]): void {
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
	public static contained: Page[] = [];
}

// zk scope;
//a fake page used in circumstance that a page is not available ({@link #getPage})
export class Body extends Page {
	public constructor(dt: Desktop) {
		super({});
		this.desktop = dt;
	}

	public override $n(subId?: StringFieldValue): HTMLElement | null {
		return subId ? null : document.body;
	}

	public override redraw = zk.$void;
}

/** A native widget.
 * It is used mainly to represent the native componet created at the server.
 * @disable(zkgwt)
 */
// zk scope
export class Native extends Widget {
	declare public prolog;
	declare public domExtraAttrs;
	declare public epilog;
	declare public value;

	//a virtual node that might have no DOM node and must be handled specially
	public z_virnd = true;

	/** The class name (<code>zk.Native</code>)
	 * @type String
	 */
	public override className = 'zk.Native';
	/** The widget name (<code>native</code>).
	 * @type String
	 * @since 5.0.2
	 */
	public override widgetName = 'native';
	//rawId: true, (Bug 3358505: it cannot be rawId)

	public override $n(subId?: StringFieldValue): DOMFieldValue {
		return !subId && this.id ? document.getElementById(this.id) :
			this.$supers('$n', arguments as unknown as unknown[]) as DOMFieldValue; // Bug ZK-606/607
	}
	public override redraw(out: string[]): void {
		var s = this.prolog, p;
		if (s) {
			//Bug ZK-606/607: hflex/vflex and many components need to know
			//child.$n(), so we have to generate id if the parent is not native
			//(and no id is assigned) (otherwise, zk.Native.$n() failed)
			if (this.$instanceof(zk.Native) //ZK-745
			&& !this.id && (p = this.parent) && (!p.z_virnd || p.getMold() == 'nodom')) { //z_virnd implies zk.Native, zk.Page and zk.Desktop
				var j = 0, len = s.length, cond, cc;
				for (cond = {whitespace: 1}; j < len; ++j) {
					if ((cc = s.charAt(j)) == '<')
						break; //found
					if (!zUtl.isChar(cc, cond)) {
						j = len; //not recognized => don't handle
						break;
					}
				}
				if (j < len) {
					cond = {upper: 1, lower: 1, digit: 1, '-': 1};
					while (++j < len)
						if (!zUtl.isChar(s.charAt(j), cond))
							break;
					s = s.substring(0, j) + ' id="' + this.uuid + '"' + s.substring(j);
				}
			}
			// B80-ZK-2957
			if (this.domExtraAttrs) {
				var postTag = s.indexOf('/>') == -1 ? '>' : '/>';
				s = s.replace(postTag, this.domExtraAttrs_() + postTag);
			}
			// B65-ZK-1836 and B70-ZK-2622
			out.push(zk.Native.replaceScriptContent(s.replace(/ sclass=/ig, ' class=')));
			if (this.value && s.startsWith('<textarea'))
				out.push(this.value);
		}

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		s = this.epilog;
		if (s) out.push(s);
	}

	public static $redraw = Native.prototype.redraw;

	public static replaceScriptContent(str: string): string {
		try {
			var result = str.match(/<script[^>]*>([\s\S]*?)<\/script>/g);
			if (!result)
				return str.replace(/<\/(?=script>)/ig, '<\\/');
			else {
				for (var i = 0, j = result.length; i < j; i++) {
					var substr = result[i];

					// enclose with <script></script>
					if (substr.length >= 17) {
						var cnt = substr.substring(8, substr.length - 9),
							cnt2 = zk.Native.replaceScriptContent(cnt);
						if (cnt != cnt2)
							str = str.replace(cnt, cnt2);
					}
				}
			}
			return str;
		} catch (e) {
			zk.debugLog(e.message || e);
		}
		return str;
	}
}

/** A macro widget.
 * It is used mainly to represent the macro componet created at the server.
 */
// zk scope
export class Macro extends Widget {
	declare public _fellows;
	/** The class name (<code>zk.Macro</code>).
	 * @type String
	 */
	public override className = 'zk.Macro';
	/** The widget name (<code>macro</code>).
	 * @type String
	 * @since 5.0.2
	 */
	public override widgetName = 'macro';
	// B70-ZK-2065: Replace span with div, because block-level element inside an inline element is not valid.
	private _enclosingTag = 'div';

	public constructor() {
		super();
		this._fellows = {};
	}

	/** Returns the tag name for this macro widget.
	 * <p>Default: div (since 7.0.1)
	 * @return String the tag name (such as div or span)
	 * @since 5.0.3
	 */
	public getEnclosingTag(): string {
		return this._enclosingTag;
	}

	/** Sets the tag name for this macro widget
	 * @param String tag the tag name, such as div
	 * @since 5.0.3
	 */
	public setEnclosingTag(enclosingTag: string): void {
		if (this._enclosingTag != enclosingTag) {
			this._enclosingTag = enclosingTag;
			this.rerender();
		}
	}

	/** Generates the HTML fragment for this macro component.
	 * <p>Default: it generate DIV (since 7.0.1) to enclose the HTML fragment
	 * of all child widgets.
	 * @param Array out an array of HTML fragments (String).
	 */
	public override redraw(out: string[]): void {
		out.push('<', this._enclosingTag, this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</', this._enclosingTag, '>');
	}
}

/** Retrieves the service if any.
 * @param Object n the object to look for. If it is a string,
 * it is assumed to be UUID, unless it starts with '$'.
 * For example, <code>zkservice.$('uuid')<code> is the same as <code>zkservice.$('#uuid')<code>,
 * and both look for a widget whose ID is 'uuid'. On the other hand,
 * <code>zkservice.$('$id') looks for a widget whose ID is 'id'.<br/>
 * and <code>zkservice.$('.className') looks for a widget whose CSS selector is 'className'.<br/>
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
 * of the widget's main element ({@link #$n}). In most cases, if ID
 * of an element is xxx-yyy, the the element must be a child of
 * the element whose ID is xxx. However, there is some exception
 * such as the shadow of a window.</li>
 * </ul>
 * @return zk.Service
 * @since 8.0.0
 */
// window scope
export const zkservice = {
	$(n: string | JQuery | HTMLElement | Widget,
		opts?: Partial<{exact: boolean; strict: boolean; child: boolean}>): Service | null {
		var widget = zk.Widget.$(n, opts);
		if (widget)
			return widget.$service();
		zk.error('Not found ZK Service with [' + n + ']');
		return null;
	}
};
function _fixCommandName(prefix: string, cmd: string, opts: EventOptions, prop: string): void {
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
export class Service extends Object {
	declare private _aftercmd;
	declare private _lastcmd: StringFieldValue;

	public $view: Widget | null;
	public $currentTarget: Widget | null;
	public constructor(widget: Widget, currentTarget: Widget) {
		super();
		this.$view = widget;
		this.$currentTarget = currentTarget;
		this._aftercmd = {};
	}

	/**
	 * Registers a callback after some command executed.
	 * @param String command the name of the command
	 * @param Function func the function to execute
	 */
	public after(cmd: string | Callable, fn: Callable): this {
		if (!fn && jq.isFunction(cmd)) {
			fn = cmd;
			cmd = this._lastcmd as string;
		}

		if (typeof cmd === 'string') {
			var ac = this._aftercmd[cmd];
			if (!ac) this._aftercmd[cmd] = [fn];
			else
				ac.push(fn);
		}
		return this;
	}

	/**
	 * Unregisters a callback after some command executed.
	 * @param String command the name of the command
	 * @param Function func the function to execute
	 */
	public unAfter(cmd: string, fn: Callable): Service {
		var ac = this._aftercmd[cmd];
		for (var j = ac ? ac.length : 0; j--;) {
			if (ac[j] == fn)
				ac.splice(j, 1);
		}
		return this;
	}

	/**
	 * Destroy this binder.
	 */
	public destroy(): void {
		this._aftercmd = null;
		this.$view = null;
		this.$currentTarget = null;
	}

	/**
	 * Post a command to the service
	 * @param String command the name of the command
	 * @param Array args the arguments for this command. (the value should be json type)
	 * @param Map opts a map of options to zk.Event, if any.
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 */
	public command(cmd: string, args: unknown[], opts?: EventOptions &
			{ duplicateIgnore?: boolean; repeatIgnore?: boolean}, timeout?: number): this {
		var wgt = this.$view;
		if (opts) {
			if (opts.duplicateIgnore)
				_fixCommandName('onAuServiceCommand$', cmd, opts, 'duplicateIgnore');
			if (opts.repeatIgnore)
				_fixCommandName('onAuServiceCommand$', cmd, opts, 'repeatIgnore');
		}
		zAu.send(new zk.Event(wgt, 'onAuServiceCommand$' + cmd, {cmd: cmd, args: args}, zk.copy({toServer: true}, opts)), timeout != undefined ? timeout : 38);
		this._lastcmd = cmd;
		return this;
	}
	public $doAfterCommand(cmd: string, args?: unknown[]): void {
		var ac = this._aftercmd[cmd];
		for (var i = 0, j = ac ? ac.length : 0; i < j; i++)
			ac[i].apply(this, [args]);
	}
}
/** A skipper is an object working with {@link zk.Widget#rerender}
 * to rerender portion(s) of a widget (rather than the whole widget).
 * It can improve the performance a lot if it can skip a lot of portions, such as a lot of child widgets.
 * <p>The skipper decides what to skip (i.e., not to rerender), detach the skipped portion(s), and attach them back after rerendering. Thus, the skipped portion won't be rerendered, nor unbound/bound.
 * <p>The skipper has to implement three methods, {@link #skipped},
 * {@link #skip} and {@link #restore}. {@link #skipped} is used to test whether a child widget shall be skipped.
 * {@link #skip} and {@link #restore} works together to detach and attach the skipped portions from the DOM tree. Here is how
 * {@link zk.Widget#rerender} uses these two methods:
<pre><code>
rerender: function (skipper) {
  var skipInfo;
  if (skipper) skipInfo = skipper.skip(this);

  this.replaceHTML(this.node, null, skipper);

  if (skipInfo) skipper.restore(this, skipInfo);
}
</code></pre>
 * <p>Since {@link zk.Widget#rerender} will pass the returned value of {@link #skip} to {@link #restore}, the skipper doesn't need to store what are skipped. That means, it is possible to have one skipper to serve many widgets. {@link #nonCaptionSkipper} is a typical example.
 * <p>In additions to passing a skipper to {@link zk.Widget#rerender}, the widget has to implement the mold method to handle the skipper:
 * <pre>{@code
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
 * }</pre>
 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component Development/Server-side/Property_Rendering">ZK Client-side Reference: Property Rendering</a>.
 * @disable(zkgwt)
 */
// zk scope
export class Skipper extends ZKObject {
	/** Returns whether the specified child widget will be skipped by {@link #skip}.
	 * <p>Default: returns if wgt.caption != child. In other words, it skip all children except the caption.
	 * @param zk.Widget wgt the widget to re-render
	 * @param zk.Widget child a child (descendant) of this widget.
	 * @return boolean
	 */
	public skipped(wgt: Widget & {caption?: Widget}, child: Widget): boolean {
		return wgt.caption != child;
	}
	/** Skips all or subset of the descendant (child) widgets of the specified widget.
	 * <p>Notice that the <pre>skipId</pre> argument is not used by {@link zk.Widget#rerender}.
	 * Rather it is used to simplify the overriding of this method,
	 * such that the deriving class can call back this class and
	 * to pass a different ID to skip
	 *
	 * <p>If you don't want to pass a different ID (default: uuid + '-cave'),
	 * you can ignore <code>skipId</code>
<pre><code>
Object skip(zk.Widget wgt);
</code></pre>
	 * <p>Default: it detaches all DOM elements whose parent element is
	 * <code>jq(skipId || (wgt.uuid + '-cave'), zk)</code>.

	 * @param zk.Widget wgt the widget being rerendered.
	 * @param String skipId [optional] the ID of the element where all its descendant
	 * elements shall be detached by this method, and restored later by {@link #restore}.
	 * If not specified, <code>uuid + '-cave'</code> is assumed.
	 * @return DOMElement
	 */
	public skip(wgt: Widget, skipId?: string): HTMLElement | undefined {
		var skip = jq(skipId || wgt.getCaveNode(), zk)[0];
		if (skip && skip.firstChild) {
			var cf = zk.currentFocus,
				iscf = cf && cf.getInputNode;

			if (iscf && zk.ie && zk.ie < 11) //Bug ZK-1377 IE will lost input selection range after remove node
				zk.cfrg = zk(cf!.getInputNode!()).getSelectionRange();

			skip.parentNode?.removeChild(skip);
				//don't use jq to remove, since it unlisten events

			if (iscf && zk.chrome) //Bug ZK-1377 chrome will lost focus target after remove node
				zk.currentFocus = cf;

			return skip;
		}
	}

	/** Restores the DOM elements that are detached (i.e., skipped) by {@link #skip}.
	 * @param zk.Widget wgt the widget being re-rendered
	 * @param Object inf the object being returned by {@link #skip}.
	 * It depends on how a skipper is implemented. It is usually to carry the information about what are skipped
	 */
	public restore(wgt: Widget, skip: DOMFieldValue): void {
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
<pre><code>
setClosable: function (closable) {
 if (this._closable != closable) {
  this._closable = closable;
  if (this.node) this.rerender(zk.Skipper.nonCaptionSkipper);
 }
}
</pre></code>
	 * @type zk.Skipper
	 */
		//nonCaptionSkipper: null
//@};
	public static nonCaptionSkipper = new Skipper();
}

export interface NoDOMInterface extends Widget {
	_startNode: Comment;
	_endNode: Comment;
	_oldWgt: Widget;
	$getInterceptorContext$(): {stop: boolean; args: unknown[]; result: unknown};
	get lastChild(): NoDOMInterface;
	removeHTML_(n: HTMLElement[] | HTMLElement);
}
/**
 * It's a object contains some interceptors.
 * You could use this to intercept a widget, and its node would change to comment node (start node and end node).
<pre><code>
zk.$intercepts(zul.wgt.Idspace, zk.NoDOM);
</pre></code>
 * @since 8.0.3
 */
// zk scope
export let NoDOM: ThisType<NoDOMInterface> = {
	bind_() {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			this.$supers('bind_', context.args);
			var node = this.$n('tmp'),
				desc = this.getZclass() + ' ' + this.uuid,
				startDesc = desc + ' start',
				endDesc = desc + ' end';
			if (node) {
				var start = document.createComment(startDesc),
					end = document.createComment(endDesc),
					parentNode = node.parentNode;
				parentNode?.insertBefore(start, node);
				var endNode: Node = node,
					lastChild = this.lastChild;
				if (lastChild) {
					if (lastChild.getMold() == 'nodom') {
						endNode = lastChild._endNode;
					} else {
						var lastChildNode = lastChild.$n();
						if (!lastChildNode)
							lastChildNode = jq(lastChild.uuid as string, zk)[0];
						if (lastChildNode)
							endNode = lastChildNode;
					}
				}
				parentNode?.insertBefore(end, endNode.nextSibling);
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
	},

	removeHTML_(n: HTMLElement): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$(),
				//clear the dom between start node and end node
				node = this._startNode ? this._startNode.nextSibling : null,
				next;
			while (node && node != this._endNode) {
				next = node.nextSibling;
				jq(node).remove();
				node = next;
			}
			jq(this._startNode).remove();
			jq(this._endNode).remove();
			jq(n).remove();
			this.clearCache();
			context.stop = true;
		}
	},

	setDomVisible_(n: Element, visible: boolean, opts?: {display?: boolean; visibility?: boolean}): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			for (var w = this.firstChild; w; w = w.nextSibling) {
				if (visible)
					w.setDomVisible_(w.$n()!, w.isVisible()!, opts);
				else
					w.setDomVisible_(w.$n()!, visible, opts);
			}
			context.stop = true;
		}
	},

	isRealVisible(): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.result = this.isVisible() && (this.parent ? this.parent?.isRealVisible() : true);
			context.stop = true;
		}
		return false;
	},

	getFirstNode_(): HTMLElement | undefined {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.result = this._startNode;
			context.stop = true;
		}
		return undefined;
	},

	insertChildHTML_(child: Widget, before?: Widget, desktop?: Desktop): void {
		if (this.getMold() == 'nodom' && !before) {
			var context = this.$getInterceptorContext$();
			jq(this._endNode).before(child.redrawHTML_());
			child.bind(desktop as Desktop);
			context.stop = true;
		}
	},

	detach(): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			if (this.parent) this.parent.removeChild(this);
			else {
				var cf = zk.currentFocus;
				if (cf && zUtl.isAncestor(this, cf))
					zk.currentFocus = null;
				var n = this.$n();
				if (n) {
					this.removeHTML_(n);
					this.unbind();
				}
			}
			context.stop = true;
		}
	},
	getOldWidget_(n: HTMLElement): Widget {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.result = this._oldWgt;
			context.stop = true;
		}
		return this._oldWgt;
	},
	replaceHTML(n: HTMLElement, desktop: Desktop | null, skipper: Skipper
						   , _trim_?: boolean, _callback_?: Callable | Callable[]): void {
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
			jq(this._endNode).remove();
			jq(n as Element).remove();
			jq(this._startNode).replaceWith(this.redrawHTML_(skipper, _trim_));
			this.bind(desktop as Desktop, skipper);

			if (!skipper) {
				if (!jq.isArray(_callback_))
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
	},
	replaceWidget(newwgt: NoDOMInterface): void {
		if (this.getMold() == 'nodom') {
			newwgt._startNode = this._startNode;
			newwgt._endNode = this._endNode;
			newwgt._oldWgt = this;
		}
	},
	$n(subId: StringFieldValue): DOMFieldValue {
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
	},
	redraw(out: string[]): void {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			out.push('<div id="', this.uuid, '-tmp', '" style="display:none"></div>');
			for (var w = this.firstChild; w; w = w.nextSibling)
				w.redraw(out);
			context.stop = true;
		}
	},
	ignoreFlexSize_(attr: string): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.stop = true;
			context.result = true;
		}
		return true;
	},

	ignoreChildNodeOffset_(attr: string): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.stop = true;
			context.result = true;
		}
		return true;
	},

	isExcludedHflex_(): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.stop = true;
			context.result = true;
		}
		return true;
	},
	isExcludedVflex_(): boolean {
		if (this.getMold() == 'nodom') {
			var context = this.$getInterceptorContext$();
			context.stop = true;
			context.result = true;
		}
		return true;
	}
};
//Extra//
export function zkopt(opts: Record<string, unknown>): void {
	for (var nm in opts) {
		var val = opts[nm];
		switch (nm) {
		case 'pd': zk.procDelay = val as number; break;
		case 'td': zk.tipDelay = val as number; break;
		case 'art': zk.resendTimeout = val as number; break;
		case 'dj': zk.debugJS = val as boolean; break;
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
		case 'eu': zAu.setErrorURI(val as string); break;
		case 'ppos': zk.progPos = val as string; break;
		case 'hs': zk.historystate.enabled = val as boolean; break;
		case 'eup': zAu.setPushErrorURI(val as string); break;
		case 'resURI': zk.resourceURI = val as string;
		}
	}
}