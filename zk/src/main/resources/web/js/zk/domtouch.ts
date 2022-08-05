/* domtouch.ts

	Purpose:
		Enhance/fix ios dom event
	Description:

	History:
		Wedi Mar 30 15:14:49     2011, Created by jimmyshiau

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _createMouseEvent (type: string, button: number, changedTouch: Touch, ofs = {sx: 0, sy: 0, cx: 0, cy: 0}): MouseEvent {
	var simulatedEvent = document.createEvent('MouseEvent');
	simulatedEvent.initMouseEvent(type, true, true, window, 1,
		changedTouch.screenX + ofs.sx, changedTouch.screenY + ofs.sy,
		changedTouch.clientX + ofs.cx, changedTouch.clientY + ofs.cy,
		false, false, false, false, button, null); // eslint-disable-line zk/noNull
	return simulatedEvent;
}
function _createJQEvent(target: Element, type: string, button: number, changedTouch: Touch): JQuery.Event {
	//do not allow text
	//ZK-1011
	if (target && (target.nodeType === 3 || target.nodeType === 8))
		target = target.parentNode as Element;

	var originalEvent = _createMouseEvent(type, button, changedTouch),
		props: string[] = [], // ZK-4565, jq.event.props is removed in jquery 3.5.0
		event = jq.Event(originalEvent);

	//Add missing props removed by jQuery
	props.push('button', 'charCode', 'clientX', 'clientY', 'detail',
			'fromElement', 'keyCode', 'layerX', 'layerY', 'offsetX', 'offsetY',
			'pageX', 'pageY', 'screenX', 'screenY', 'srcElement', 'toElement');
	for (var i = props.length, prop: string; i;) {
		prop = props[--i];
		event[prop] = originalEvent[prop] as unknown;
	}
	event.target = target;
	return event;
}
function _toMouseEvent(event: JQuery.TouchEventBase, changedTouch: Touch): JQuery.Event | undefined {
	switch (event.type) {
	case 'touchstart':
		return _createJQEvent(changedTouch.target as HTMLElement, 'mousedown', 0, changedTouch);
	case 'touchend':
		return _createJQEvent(
			document.elementFromPoint(
				changedTouch.clientX,
				changedTouch.clientY) as HTMLElement,
				'mouseup', 0, changedTouch);
	case 'touchmove':
		var ele = document.elementFromPoint(changedTouch.clientX, changedTouch.clientY);
		return (ele && _createJQEvent(ele, 'mousemove', 0, changedTouch)) || undefined;
	}
	return event;
}
function _doEvt(type: string, evt: JQuery.TouchEventBase, jqevt: JQuery.Event): void {
	var eventFuncs = jq.data(evt.currentTarget as never, 'zk_eventFuncs') as Record<string, CallableFunction[]>,
		typeLabel = _findEventTypeLabel(type, eventFuncs),
		funcs: CallableFunction[];
	//store original event for invoke stop
	jqevt['touchEvent'] = evt.originalEvent;
	if (eventFuncs && typeLabel && (funcs = eventFuncs[typeLabel])) {
		for (var i = 0, l = funcs.length; i < l; i++)
			funcs[i](jqevt);
	}
}
function delegateEventFunc (event: JQuery.TouchEventBase): void {
	var touchEvt = event.originalEvent as TouchEvent & {sourceCapabilities?: object},
		touches = touchEvt.touches,
		sourceCapabilities = touchEvt.sourceCapabilities;
	if (touches && touches.length > 1) return;
	if (touchEvt instanceof MouseEvent
		&& sourceCapabilities && sourceCapabilities['firesTouchEvents']) return; // handled by touch handler

	var evt: JQuery.Event | undefined,
		changedTouches = touchEvt.changedTouches ? touchEvt.changedTouches[0] : undefined;

	if ((evt = _toMouseEvent(event, changedTouches!)))
		_doEvt(event.type, event, evt);
}
zk.copy(zjq.eventTypes, {
	zmousedown: 'touchstart mousedown',
	zmouseup: 'touchend mouseup',
	zmousemove: 'touchmove mousemove'
});
function _findEventTypeLabel(type: string, eventFuncs: Record<string, unknown>): string | undefined {
	var exactType = eventFuncs[type];
	if (exactType)
		return type;

	var evtTypes = Object.keys(eventFuncs);
	for (var i = 0, length = evtTypes.length; i < length; i++) {
		var val = evtTypes[i];
		if (val.indexOf(type) !== -1)
			return val;
	}
	return undefined;
}
function _storeEventFunction(elem: Element, type: string, data: unknown, fn: CallableFunction): boolean {
	var eventFuncs = jq.data(elem, 'zk_eventFuncs') as Record<string, CallableFunction[]>,
		funcs: CallableFunction[];

	//store functions in jq data
	if (!eventFuncs) {
		eventFuncs = {};
		jq.data(elem, 'zk_eventFuncs', eventFuncs);
	}

	if ((funcs = eventFuncs[type])) {
		funcs.push(fn);
		return false; //already listen
	}
	eventFuncs[type] = [fn];
	return true;
}
function _removeEventFunction(elem: Element, type: string, fn: CallableFunction): boolean {
	var eventFuncs = jq.data(elem, 'zk_eventFuncs') as Record<string, CallableFunction[]>,
		funcs: CallableFunction[];

	if (eventFuncs && (funcs = eventFuncs[type])) {
		funcs.$remove(fn);
		if (!funcs.length) {
			delete eventFuncs[type];
			for (var i in eventFuncs)
				if (i)
					return true;
			jq.removeData(elem, 'zk_eventFuncs');
			return true; //has no listen
		}
	}
	return false;
}

const _xWidget: Partial<zk.Widget> = {};
zk.override(zk.Widget.prototype, _xWidget, {
	_swipe: <zk.Swipe | undefined> undefined,
	_startTap: <((wgt) => void) | undefined> undefined,
	_holdTime: 0,
	_rightClickPending: false,
	_holdTimeout: <number | undefined> undefined,
	_rightClickEvent: <JQuery.Event | undefined> undefined,
	_tapValid: false,
	_tapTimeout: <number | undefined> undefined,
	_lastTap: <Element | undefined> undefined,
	_dbTap: false,
	_startHold: <((evt: JQuery.TouchEventBase) => void) | undefined> undefined,
	_cancelHold: <CallableFunction | undefined> undefined,
	_pt: [0, 0],
	_cancelMouseUp: false,
	_cancelClick: <number | undefined> undefined,
	bindSwipe_() {
		var node = this.$n() as HTMLElement;
		if (this.isListen('onSwipe') || jq(node).data('swipeable'))
			this._swipe = new zk.Swipe(this, node);
	},
	unbindSwipe_() {
		var swipe = this._swipe;
		if (swipe) {
			this._swipe = undefined;
			swipe.destroy(this.$n() as HTMLElement);
		}
	},
	bindDoubleTap_() {
		if (this.isListen('onDoubleClick')) {
			var doubleClickTime = 500;
			this._startTap = (wgt: typeof this) => {
				wgt._lastTap = wgt.$n() as HTMLElement;  //Holds last tapped element (so we can compare for double tap)
				wgt._tapValid = true;     //Are we still in the .5 second window where a double tap can occur
				wgt._tapTimeout = setTimeout(function () {
					wgt._tapValid = false;
				}, doubleClickTime);
			};
			jq(this.$n()).on('touchstart', this.proxy(this._dblTapStart))
				.on('touchend', this.proxy(this._dblTapEnd));
		}
	},
	unbindDoubleTap_() {
		if (this.isListen('onDoubleClick')) {
			this._startTap = undefined;
			jq(this.$n()).off('touchstart', this.proxy(this._dblTapStart))
				.off('touchend', this.proxy(this._dblTapEnd));
		}
	},
	_dblTapStart(evt: JQuery.TouchStartEvent) {
		var tevt = evt.originalEvent as TouchEvent;
		if (tevt.touches.length > 1) return;
		if (!this._tapValid) {
			this._startTap!(this);
		} else {
			clearTimeout(this._tapTimeout);
			this._tapTimeout = undefined;
			if (this.$n() == this._lastTap) {
				this._dbTap = true;
			} else {
				this._startTap!(this);
			}
		}
		// ZK-2179: should skip row widget
		var p = (zk.Widget.$(evt.target) as zk.Widget).parent;
		if (p && (!zk.isLoaded('zul.grid') || !(p instanceof zul.grid.Row))
			&& (!zk.isLoaded('zul.sel') || (!(p instanceof zul.sel.Listitem) && !(p instanceof zul.sel.Treerow))))
		tevt.stopPropagation();
	},
	_dblTapEnd(evt: JQuery.TouchEndEvent) {
		var tevt = evt.originalEvent as TouchEvent;
		if (tevt.touches.length > 1) return;
		if (this._dbTap) {
			this._dbTap = this._tapValid = false;
			this._lastTap = undefined;
			var	changedTouch = tevt.changedTouches[0],
				wevt = new zk.Event(this, 'onDoubleClick', {pageX: changedTouch.clientX, pageY: changedTouch.clientY}, {}, evt);
			if (!this.$weave) {
				if (!wevt.stopped)
					this.doDoubleClick_(wevt);
				if (wevt.domStopped)
					wevt.domEvent!.stop();
			}
			tevt.preventDefault(); //stop ios zoom
		}
	},
	bindTapHold_() {
		if (this.isListen('onRightClick') || (window.zul && this instanceof zul.Widget && this.getContext())) { //also register context menu to tapHold event
			this._holdTime = 800;
			this._startHold = (evt: JQuery.TouchEventBase): void => {
				if (!this._rightClickPending) {
					var self = this;
					self._rightClickPending = true; // We could be performing a right click
					self._rightClickEvent = evt;
					self._holdTimeout = setTimeout(function () {
						self._rightClickPending = false;
						var evt = self._rightClickEvent,
							wevt = new zk.Event(self, 'onRightClick', {pageX: self._pt[0], pageY: self._pt[1]}, {}, evt as never);

						if (!self.$weave) {
							if (!wevt.stopped)
								self.doRightClick_(wevt);
							if (wevt.domStopped)
								wevt.domEvent!.stop();
						}

						// Note: I don't mouse up the right click here however feel free to add if required
						self._cancelMouseUp = true;
						self._rightClickEvent = undefined;
					}, self._holdTime);
				}
			};
			this._cancelHold = () => {
				if (this._rightClickPending) {
					this._rightClickPending = false;
					this._rightClickEvent = undefined;
					clearTimeout(this._holdTimeout);
					this._holdTimeout = undefined;
				}
			};
			jq(this.$n()!).on('touchstart', this.proxy(this._tapHoldStart))
				.on('touchmove', this.proxy(this._tapHoldMove)) //cancel hold if moved
				.on('click', this.proxy(this._tapHoldClick))    //prevent click during hold
				.on('touchend', this.proxy(this._tapHoldEnd));
		}
	},
	unbindTapHold_() {
		if (this.isListen('onRightClick') || (window.zul && this instanceof zul.Widget && this.getContext())) { //also register context menu to tapHold event
			this._startHold = this._cancelHold = undefined;
			jq(this.$n()!).off('touchstart', this.proxy(this._tapHoldStart))
				.off('touchmove', this.proxy(this._tapHoldMove)) //cancel hold if moved
				.off('click', this.proxy(this._tapHoldClick))    //prevent click during hold
				.off('touchend', this.proxy(this._tapHoldEnd));
		}
	},
	_tapHoldStart(evt: JQuery.TouchEventBase) {
		var tevt = evt.originalEvent as TouchEvent;

		if (tevt.touches.length > 1)
			return;

		var	changedTouch = tevt.changedTouches[0];
		this._pt = [changedTouch.clientX, changedTouch.clientY];
		this._startHold!(evt);

		// ZK-2179: should skip row widget
		var p = (zk.Widget.$(evt.target) as zk.Widget).parent;
		if (p && (!zk.isLoaded('zul.grid') || !(p instanceof zul.grid.Row))
			&& (!zk.isLoaded('zul.sel') || (!(p instanceof zul.sel.Listitem) && !(p instanceof zul.sel.Treerow))))
			tevt.stopPropagation();
	},
	_tapHoldMove(evt: JQuery.TouchEventBase) {
		var tevt = evt.originalEvent as TouchEvent,
			initSensitivity = 3;

		if (tevt.touches.length > 1 || !this._pt)
			return;

		var	changedTouch = tevt.changedTouches[0];
		if (Math.abs(changedTouch.clientX - this._pt[0]) > initSensitivity
			|| Math.abs(changedTouch.clientY - this._pt[1]) > initSensitivity)
			this._cancelHold!();
	},
	_tapHoldClick(evt: JQuery.TouchEventBase) {
		if (this._cancelClick) {
			//stop click after hold
			if ((Date.now() - this._cancelClick) < 100) {
				evt.stopImmediatePropagation();
				return false;
			}
			this._cancelClick = undefined;
		}
	},
	_tapHoldEnd(evt: JQuery.TouchEventBase) {
		var tevt = evt.originalEvent as TouchEvent;
		if (tevt.touches.length > 1) return;
		if (this._cancelMouseUp) {
			this._cancelClick = jq.now();
			this._cancelMouseUp = false;
			evt.stopImmediatePropagation();
			return false;
		}
		this._cancelHold!();
	}
});
var _jq = {},
	_jqEvent: Partial<JQuery.Event> = {},
	_jqEventSpecial = {};
zk.override(jq.fn, _jq, {
	on: function (this: JQuery, type: string, selector, data: unknown, fn: unknown, ...rest: unknown[]) {
		var evtType: string | undefined;
		if ((evtType = zjq.eventTypes[type])) {
			// refer to jquery on function for reassign args
			if (data == null && fn == null) {
				// ( type, fn )
				fn = selector;
				data = selector = undefined;
			} else if (fn == null) {
				if (typeof selector === 'string') {
					// ( type, selector, fn )
					fn = data;
					data = undefined;
				} else {
					// ( type, data, fn )
					fn = data;
					data = selector;
					selector = undefined;
				}
			}
			if (_storeEventFunction(this[0], evtType, data, fn as CallableFunction))
				this.zon(evtType, selector as string, data, delegateEventFunc);
		} else {
			this.zon(type, selector as string, data, fn as CallableFunction, ...rest); // Bug ZK-1142: recover back to latest domios.js
		}
		return this;
	},
	off: function (this: JQuery, type: string, selector: unknown, fn: unknown, ...rest: unknown[]) {
		var evtType: string | undefined;
		if ((evtType = zjq.eventTypes[type])) {
			// refer to jquery on function for reassign args
			if (selector === false || typeof selector === 'function') {
				// ( type [, fn] )
				fn = selector;
				selector = undefined;
			}
			if (_removeEventFunction(this[0], evtType, fn as never))
				this.zoff(evtType, selector as string, delegateEventFunc);
		} else {
			this.zoff(type, selector as string, fn as never, ...rest); // Bug ZK-1142: recover back to latest domios.js
		}
		return this;
	}
});
zk.override(jq.Event.prototype as JQuery.Event, _jqEvent, {
	touchEvent: <TouchEvent | undefined> undefined,
	stop() {
		_jqEvent.stop!.bind(this as JQuery.Event)();
		var tEvt: TouchEvent | undefined;
		if ((tEvt = this.touchEvent)) {
			if (tEvt.cancelable) tEvt.preventDefault();
			tEvt.stopPropagation();
		}
	}
});
zk.override(jq.event.special, _jqEventSpecial, {
	touchstart: {
		setup(this: EventTarget, data, namespaces, eventHandle: EventListenerOrEventListenerObject) {
			this.addEventListener('touchstart', eventHandle, {passive: false}); // ZK-4678
		}
	},
	touchmove: {
		setup: function (this: EventTarget, data, namespaces, eventHandle: EventListenerOrEventListenerObject) {
			this.addEventListener('touchmove', eventHandle, {passive: false}); // ZK-4678
		}
	}
});
