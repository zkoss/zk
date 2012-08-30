/* domios.js

	Purpose:
		Enhance/fix ios dom event
	Description:
		
	History:
		Wedi Mar 30 15:14:49     2011, Created by jimmyshiau

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
function _createMouseEvent (type, button, changedTouch, ofs) {
	if (!ofs)
		ofs = {sx: 0,sy: 0,cx: 0,cy: 0};
	
	var simulatedEvent = document.createEvent("MouseEvent");
	simulatedEvent.initMouseEvent(type, true, true, window, 1, 
		changedTouch.screenX + ofs.sx, changedTouch.screenY + ofs.sy, 
		changedTouch.clientX + ofs.cx, changedTouch.clientY + ofs.cy,
		false, false, false, false, button, null);
	return simulatedEvent;		
}
function _createJQEvent (target, type, button, changedTouch, ofs) {
	//do not allow text
	//ZK-1011
	if (target && (target.nodeType === 3 || target.nodeType === 8))
		target = target.parentNode;
	
	var originalEvent = _createMouseEvent(type, button, changedTouch, ofs),
		props = jQuery.event.props,
		event = jQuery.Event(originalEvent);

	for ( var i = props.length, prop; i; ) {
		prop = props[--i];
		event[prop] = originalEvent[prop];
	}
	event.target = target;
	return event;
}
function _toMouseEvent(event, changedTouch) {
	switch (event.type) {
	case 'touchstart':
		return _createJQEvent(changedTouch.target, 'mousedown', 0, changedTouch);
	case 'touchend':
		return _createJQEvent(
			document.elementFromPoint(
				changedTouch.clientX, 
				changedTouch.clientY), 
				'mouseup', 0, changedTouch);
		break;
	case 'touchmove':
		var ele = document.elementFromPoint(changedTouch.clientX,changedTouch.clientY);
		return (ele && _createJQEvent(ele, 'mousemove', 0, changedTouch)) || null;
	}
	return event;
}
function _doEvt(type, evt, jqevt) {
	var eventFuncs = jq.data(evt.currentTarget, 'zk_eventFuncs'),
		funcs;
	//store original event for invoke stop
	jqevt.touchEvent = evt.originalEvent;
	if (eventFuncs && (funcs = eventFuncs[type])) {
		for (var i = 0, l = funcs.length; i < l; i++)
			funcs[i](jqevt);
	}
}
function delegateEventFunc (event) {
	var touchEvt = event.originalEvent;
	if (touchEvt.touches.length > 1) return;
	
	var evt;
	
	if(evt = _toMouseEvent(event, touchEvt.changedTouches[0]))
		_doEvt(event.type, event, evt);
}
zk.copy(zjq, {
	eventTypes: {
		zmousedown: 'touchstart',
		zmouseup: 'touchend',
		zmousemove: 'touchmove',
		zcontextmenu: 'iosHold'
	}
});
function _storeEventFunction(elem, type, data, fn) {
	var eventFuncs = jq.data(elem, 'zk_eventFuncs'),
		funcs;
		
	//store functions in jq data
	if (!eventFuncs) {
		eventFuncs = {};
		jq.data(elem, 'zk_eventFuncs', eventFuncs);
	}
	
	if (funcs = eventFuncs[type]) {
		funcs.push(fn);
		return false; //already listen
	}
	eventFuncs[type] = [fn];
	return true;
}
function _removeEventFunction(elem, type, fn) {
	var eventFuncs = jq.data(elem, 'zk_eventFuncs'),
		funcs;
	
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
}
function cancelHold() {
	if (rightClickPending) {
		rightClickPending = false;
		rightClickEvent = null;
		clearTimeout(holdTimeout);
		holdTimeout = null;
	}
}
function startHold(evt) {
	if (!rightClickPending) {
		rightClickPending = true; // We could be performing a right click
		rightClickEvent = evt;
		holdTimeout = setTimeout(doRightClick, holdTime);
	}
}
function doRightClick() {
	rightClickPending = false;
	var evt = rightClickEvent,
		changedTouch = evt.originalEvent.changedTouches[0];

/* orignal implement	
 	simulatedMouseEvent('mouseup', 0, changedTouch);// We need to mouse up (as we were down)
	simulatedMouseEvent('mousedown', 2, changedTouch);// emulate a right click

	simulatedEvent.initMouseEvent("contextmenu", true, true, window, 1, 
		changedTouch.screenX + 20, changedTouch.screenY - 20, 
		changedTouch.clientX + 20, changedTouch.clientY - 20,
      	false, false, false, false, 2, null);
	changedTouch.target.dispatchEvent(simulatedEvent);// Show a context menu
*/
	_doEvt('iosHold', evt, 
		_createJQEvent(changedTouch.target, 
			'contextmenu', 0, changedTouch, {sx: 20,sy: -20,cx: 20,cy: -20}));
	
	// Note:: I don't mouse up the right click here however feel free to add if required
	cancelMouseUp = true;
	rightClickEvent = null;
}

var rightClickPending,	// Is a right click still feasibl
	rightClickEvent,	// the original event
	holdTimeout,		// timeout reference
	holdTime = 800,
	initSensitivity = 3,
	pt,
	cancelMouseUp,		// prevents a click from occuring as we want the context menu
	cancelClick,
	contextmenuHandler = {
		touchstart: function(evt) {
			var touchEvt = evt.originalEvent;
			if (touchEvt.touches.length > 1) return;
			var	changedTouch = touchEvt.changedTouches[0];
			pt = [changedTouch.clientX, changedTouch.clientY];
			startHold(evt);
		},
		touchmove: function(evt) {
			var touchEvt = evt.originalEvent;
			if (touchEvt.touches.length > 1 || !pt) return;
			var	changedTouch = touchEvt.changedTouches[0];
			if (Math.abs(changedTouch.clientX - pt[0]) > initSensitivity ||
				Math.abs(changedTouch.clientY - pt[1]) > initSensitivity )			
				cancelHold();
		},
		click: function(evt) {
			if (cancelClick) {
				//stop click after hold
				if ((zUtl.now() - cancelClick) < 100) {
					evt.stopImmediatePropagation();
					return false;
				}
				cancelClick = null;
			}
		},
		touchend: function(evt) {
			var touchEvt = evt.originalEvent;
			if (touchEvt.touches.length > 1) return;
			if (cancelMouseUp) {
				cancelClick = zUtl.now();
				cancelMouseUp = false;
				evt.stopImmediatePropagation();
				return false;
			}
			cancelHold();
		}
	};

var _jq = {},
	_jqEvent = {};
zk.override(jq.fn, _jq, {
	bind: function(type, data, fn) {
		var evtType;
		if (evtType = zjq.eventTypes[type]) {
			// refer to jquery bind function for reassign args
			if ( jq.isFunction(data) || data === false ) {
				fn = data;
				data = undefined;
			}
			if (_storeEventFunction(this[0], evtType, data, fn)) {
				switch (evtType) {
				case 'iosHold':
					this.zbind('touchstart', data, contextmenuHandler.touchstart)
						.zbind('touchmove', data, contextmenuHandler.touchmove)
						.zbind('click', data, contextmenuHandler.click)
						.zbind('touchend', data, contextmenuHandler.touchend);
					break;
				default:
					this.zbind(evtType, data, delegateEventFunc);
				}
			}
		} else
			this.zbind.apply(this, arguments); // Bug ZK-1142: recover back to latest domios.js
			
		return this;
	},
	unbind: function(type, fn){
		var evtType;
		if (evtType = zjq.eventTypes[type]) {
			if (_removeEventFunction(this[0], evtType, fn)) {
				switch (evtType) {
				case 'iosHold':
					this.zunbind('touchstart', contextmenuHandler.touchstart)
						.zunbind('touchmove', contextmenuHandler.touchmove)
						.zunbind('click', contextmenuHandler.click)
						.zunbind('touchend', contextmenuHandler.touchend);
					break;
				default:
					this.zunbind(evtType, delegateEventFunc);
				}
			}
		} else
			this.zunbind.apply(this, arguments); // Bug ZK-1142: recover back to latest domios.js
		return this;
	}
});
zk.override(jq.Event.prototype, _jqEvent, {
	stop: function () {
		_jqEvent.stop.apply(this);
		var tEvt;
		if (tEvt = this.touchEvent) {
			tEvt.preventDefault();
			tEvt.stopPropagation();
		}
	}
});
})();