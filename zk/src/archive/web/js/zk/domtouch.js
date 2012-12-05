/* domtouch.js

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
		zmousemove: 'touchmove'
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
var _xWidget = {};
zk.override(zk.Widget.prototype, _xWidget, {
	bindSwipe_: function () {
		var node = this.$n();
		if (this.isListen('onSwipe') || jq(node).data('swipeable'))
			this._swipe = new zk.Swipe(this, node);
	},
	unbindSwipe_: function () {
		var swipe = this._swipe;
		if (swipe) {
			this._swipe = null;
			swipe.destroy(this.$n());
		}
	},
	bindDoubleTap_: function () {
		if (this.isListen('onDoubleClick')) {
			var doubleClickTime = 500;
			this._startTap = function (wgt) {
				wgt._lastTap = wgt.$n();  //Holds last tapped element (so we can compare for double tap)
				wgt._tapValid = true;     //Are we still in the .5 second window where a double tap can occur
				wgt._tapTimeout = setTimeout(function() {
					wgt._tapValid = false;
				}, doubleClickTime);
			};
			jq(this.$n()).bind('touchstart', this.proxy(this._dblTapStart))
				.bind('touchend', this.proxy(this._dblTapEnd));
		}
	},
	unbindDoubleTap_: function () {
		if (this.isListen('onDoubleClick')) {
			this._startTap = null;
			jq(this.$n()).unbind('touchstart', this.proxy(this._dblTapStart))
				.unbind('touchend', this.proxy(this._dblTapEnd));
		}
	},
	_dblTapStart: function(evt) {
		var tevt = evt.originalEvent;
		if (tevt.touches.length > 1) return;
		var	changedTouch = tevt.changedTouches[0];
		if (!this._tapValid) {
			this._startTap(this);
		} else {
			clearTimeout(this._tapTimeout);
			this._tapTimeout = null;
			if (this.$n() == this._lastTap) {
				this._dbTap = true;
			} else {
				this._startTap(this);
			}
		}
		tevt.stopPropagation();
	},
	_dblTapEnd: function(evt) {
		var tevt = evt.originalEvent;
		if (tevt.touches.length > 1) return;
		if (this._dbTap) {
			this._dbTap = this._tapValid = this._lastTap = null;
			var	changedTouch = tevt.changedTouches[0],
				wevt = new zk.Event(this, 'onDoubleClick', {pageX: changedTouch.clientX, pageY: changedTouch.clientY}, {}, evt);
			if (!this.$weave) {
				if (!wevt.stopped)
					this['doDoubleClick_'].call(this, wevt);
				if (wevt.domStopped)
					wevt.domEvent.stop();
			}
			tevt.preventDefault(); //stop ios zoom
		}
	},
	bindTapHold_: function () {
		if (this.isListen('onRightClick') || (this.getContext && this.getContext())) { //also register context menu to tapHold event
			this._holdTime = 800;
			this._startHold = function (evt) {
				if (!this._rightClickPending) {
					self = this;
					self._rightClickPending = true; // We could be performing a right click
					self._rightClickEvent = evt;
					self._holdTimeout = setTimeout(function () {
						self._rightClickPending = false;
						var evt = self._rightClickEvent,
							wevt = new zk.Event(self, 'onRightClick', {pageX: self._pt[0], pageY: self._pt[1]}, {}, evt);
						
						if (!self.$weave) {
							if (!wevt.stopped)
								self['doRightClick_'].call(self, wevt);
							if (wevt.domStopped)
								wevt.domEvent.stop();
						}
						
						// Note:: I don't mouse up the right click here however feel free to add if required
						self._cancelMouseUp = true;
						self._rightClickEvent = null;
					}, self._holdTime);
				}
			};
			this._cancelHold = function () {
				if (this._rightClickPending) {
					this._rightClickPending = false;
					this._rightClickEvent = null;
					clearTimeout(this._holdTimeout);
					this._holdTimeout = null;
				}
			};
			jq(this.$n()).bind('touchstart', this.proxy(this._tapHoldStart))
				.bind('touchmove', this.proxy(this._tapHoldMove)) //cancel hold if moved
				.bind('click', this.proxy(this._tapHoldClick))    //prevent click during hold
				.bind('touchend', this.proxy(this._tapHoldEnd));
		}
	},
	unbindTapHold_: function () {
		if (this.isListen('onRightClick') || (this.getContext && this.getContext())) { //also register context menu to tapHold event
			this._startHold = this._cancelHold = null
			jq(this.$n()).unbind('touchstart', this.proxy(this._tapHoldStart))
				.unbind('touchmove', this.proxy(this._tapHoldMove)) //cancel hold if moved
				.unbind('click', this.proxy(this._tapHoldClick))    //prevent click during hold
				.unbind('touchend', this.proxy(this._tapHoldEnd));
		}
	},
	_tapHoldStart: function (evt) {
		var tevt = evt.originalEvent;
		
		if (tevt.touches.length > 1)
			return;
		
		var	changedTouch = tevt.changedTouches[0];
		this._pt = [changedTouch.clientX, changedTouch.clientY];
		this._startHold(evt);
		tevt.stopPropagation();
	},
	_tapHoldMove: function (evt) {
		var tevt = evt.originalEvent,
			initSensitivity = 3;
		
		if (tevt.touches.length > 1 || !this._pt)
			return;
		
		var	changedTouch = tevt.changedTouches[0];
		if (Math.abs(changedTouch.clientX - this._pt[0]) > initSensitivity ||
			Math.abs(changedTouch.clientY - this._pt[1]) > initSensitivity )
			this._cancelHold();
	},
	_tapHoldClick: function (evt) {
		if (this._cancelClick) {
			//stop click after hold
			if ((zUtl.now() - this._cancelClick) < 100) {
				evt.stopImmediatePropagation();
				return false;
			}
			this._cancelClick = null;
		}
	},
	_tapHoldEnd: function (evt) {
		var tevt = evt.originalEvent;
		if (tevt.touches.length > 1) return;
		if (this._cancelMouseUp) {
			this._cancelClick = zUtl.now();
			this._cancelMouseUp = false;
			evt.stopImmediatePropagation();
			return false;
		}
		this._cancelHold();
	}
});
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
			if (_storeEventFunction(this[0], evtType, data, fn))
				this.zbind(evtType, data, delegateEventFunc);
		} else
			this.zbind.apply(this, arguments); // Bug ZK-1142: recover back to latest domios.js
			
		return this;
	},
	unbind: function(type, fn){
		var evtType;
		if (evtType = zjq.eventTypes[type]) {
			if (_removeEventFunction(this[0], evtType, fn))
				this.zunbind(evtType, delegateEventFunc);
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