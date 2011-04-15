function _simulatedMouseEvent (type, button, changedTouch, target) {
	return (target ? target: changedTouch.target)
		.dispatchEvent(_toMouseEvent(type, button, changedTouch));
}

function _toMouseEvent (type, button, changedTouch) {
	var simulatedEvent = document.createEvent("MouseEvent");
	simulatedEvent.initMouseEvent(type, true, true, window, button, 
		changedTouch.screenX, changedTouch.screenY, changedTouch.clientX, changedTouch.clientY,
		false, false, false, false, 0, null);
	return simulatedEvent;		
}

function _toJQEvent (target, type, button, changedTouch) {
	//do not allow text
	if (target.nodeType === 3 || target.nodeType === 8)
		target = target.parentNode;
	
	var originalEvent = _toMouseEvent(type, button, changedTouch),
		props = jQuery.event.props,
		event = jQuery.Event(originalEvent);

	for ( var i = props.length, prop; i; ) {
		prop = props[--i];
		event[prop] = originalEvent[prop];
	}
	event.target = target;
	return event;
}

var origBind = jQuery.fn.bind,
	origUnbind = jQuery.fn.unbind;

function _listen($elem, type, fn, data) {
	var elem = $elem[0],
		eventFuncs = jq.data(elem, 'zk_eventFuncs'),
		funcs;
		
	// refer to jquery bind function
	if ( jQuery.isFunction(data) || data === false ) {
		fn = data;
		data = undefined;
	}
	
	//store functions in jq data
	if (!eventFuncs) {
		eventFuncs = {};
		jq.data(elem, 'zk_eventFuncs', eventFuncs);
	}
	
	if (funcs = eventFuncs[type]) {
		funcs.push(fn);
	} else {
		eventFuncs[type] = [fn];
		origBind.apply($elem, [type, data, delegateEventFunc]);
	}
		
}

function _unlisten($elem, type, fn) {
	var elem = $elem[0];
		eventFuncs = jq.data(elem, 'zk_eventFuncs'),
		funcs;
	
	if (eventFuncs) {
		if (funcs = eventFuncs[type]) {
			funcs.$remove(fn);
			if (!funcs.length) {
				delete eventFuncs[type];
				var count0 = true;
				for (var i in eventFuncs)
					if (i) {
						count = false;
						break;
					}
				if (count0)
					jq.removeData(elem, 'zk_eventFuncs');
				origUnbind.apply($elem, [type, delegateEventFunc]);
			}
		}
	} else
		origUnbind.apply($elem, [type, fn]);
}

function _toMobileEventType(type){
	switch (type) {
	case 'mousedown':
		return 'touchstart';
		break;
	case 'mouseup':
		return 'touchend';
		break;
	case 'mousemove':
		return 'touchmove';
		break;
	}
}
function _toMouseEvent(event, changedTouch) {
	switch (event.type) {
	case 'touchstart':
		return _toJQEvent(changedTouch.target, 'mousedown', 1, changedTouch);
	case 'touchend':
		return _toJQEvent(
			document.elementFromPoint(
				changedTouch.clientX, 
				changedTouch.clientY), 
				'mouseup', 1, changedTouch);
		break;
	case 'touchmove':
		return _toJQEvent(
			document.elementFromPoint(
				changedTouch.clientX, 
				changedTouch.clientY),
			'mousemove', 1, changedTouch);
		break;
	}
	return event;
}

var delegateEventFunc = function (event) {
	var touchEvt = event.originalEvent;
	if (touchEvt.touches.length > 1) return;
	
	var changedTouch = touchEvt.changedTouches[0],
		mouseEvt = _toMouseEvent(event, changedTouch),
		eventFuncs = jq.data(event.currentTarget, 'zk_eventFuncs'),
		funcs;
		
	if (eventFuncs && (funcs = eventFuncs[event.type])) {
		//store original event for invoke stop
		mouseEvt.touchEvent = touchEvt;
		for (var i = 0, l = funcs.length; i < l; i++)
			funcs[i](mouseEvt);
	}
}

jQuery.fn.extend({
	bind : function(type, data, fn) {
		var evtType;
		
		if (evtType = _toMobileEventType(type))
			_listen(this, evtType, fn, data);
		else
			origBind.apply(this, arguments);
			
		return this;
	},
	unbind: function(type, fn){
		var evtType;
		
		if (evtType = _toMobileEventType(type))
			_unlisten(this, evtType, fn);
		else
			origUnbind.apply(this, arguments);
			
		return this;
	}
});

//Event Handler//
jq(function() {
	var lastTap,		// Holds last tapped element (so we can compare for double tap)
		tapValid,	// Are we still in the .6 second window where a double tap can occur
		tapTimeout,	// The timeout reference
		dbTap;
		
	function cancelTap() {
		tapValid = false;
	}
	//double tap
	jq(document).bind("touchstart", function(evt){
		var touchEvt = evt.originalEvent;
		if (touchEvt.touches.length > 1) return;
			
		var	changedTouch = touchEvt.changedTouches[0],
			node = changedTouch.target;
		
		if (!tapValid) {
			lastTap = node;
			tapValid = true;
			tapTimeout = setTimeout(cancelTap, 600);
		} else {
			clearTimeout(tapTimeout);
			if (node == lastTap) {
				dbTap = true;
			} else {
				lastTap = node;
				tapValid = true;
				tapTimeout = setTimeout(cancelTap, 600);
			}
		}
	}).bind("touchend", function(evt){
		var touchEvt = evt.originalEvent;
		if (touchEvt.touches.length > 1) return;
			
		var	changedTouch = touchEvt.changedTouches[0],
			node = changedTouch.target;
			if (dbTap) {
				lastTap = null;
				dbTap = tapValid = false;
				if (!_simulatedMouseEvent('dblclick', 0, changedTouch))
						touchEvt.preventDefault();	
			}
	});
	
}); //jq()
