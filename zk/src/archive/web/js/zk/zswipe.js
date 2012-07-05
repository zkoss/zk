/* zswipe.js

	Purpose:
		Support swipe event for tablet
	Description:
		
	History:
		Tue, July 3, 2012  12:24:28 PM, Created by vincentjian

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
(function () {
	var hastouch = 'ontouchstart' in window,
		startEvt = hastouch ? 'touchstart' : 'mousedown',
		moveEvt = hastouch ? 'touchmove' : 'mousemove',
		endEvt = hastouch ? 'touchend' : 'mouseup',
		start, stop;
	
	jq(document).bind(startEvt, swipeStart)
		.bind(moveEvt, swipeMove)
		.bind(endEvt, swipeEnd);
	
	function _doEvt(wevt) {
		var wgt = wevt.target;
		if (wgt && !wgt.$weave) {
			var en = wevt.name;
			if (!wevt.stopped)
				wgt['do' + en.substring(2) + '_'].call(wgt, wevt);
			if (wevt.domStopped)
				wevt.domEvent.stop();
		}
	}

	function swipeStart(devt) {
		var evt = devt.originalEvent,
			data = evt.touches ? evt.touches[0] : evt;
		
		start = {
			time: (new Date()).getTime(),
			coords: [data.pageX, data.pageY]
		};
	}
	
	function swipeMove(devt) {
		if (!start) return;
		var evt = devt.originalEvent,
			data = evt.touches ? evt.touches[0] : evt;
			
		stop = {
			time: (new Date()).getTime(),
			coords: [data.pageX, data.pageY]
		};
		
		// prevent scrolling
		var dispX = Math.abs(start.coords[0] - stop.coords[0]),
			dispY = Math.abs(start.coords[1] - stop.coords[1]);
		if (dispX > 5 || dispY > 5)
			evt.preventDefault();
	}
	
	function swipeEnd(devt) {
		if (start && stop) {
			var wgt = zk.Widget.$(devt, {child:true}),
				evt = devt.originalEvent,
				data = evt.touches ? evt.touches[0] : evt;;
			if (wgt)
				_doEvt(new zk.Event(wgt, 'onSwipe', data, {start: start, stop: stop}, devt));
		}
		start = stop = null;
	}
})();