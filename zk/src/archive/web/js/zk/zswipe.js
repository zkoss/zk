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
	
	jq(document).bind(startEvt, swipeStart);
	
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
			jq(document).unbind(moveEvt, swipeMove);
			if (start && stop) {
				var wgt = zk.Widget.$(devt, {child:true}),
					dispX, dispY, dispT = stop.time - start.time, dir;
				
				if (dispT < 500) {
					var deltaX = start.coords[0] - stop.coords[0],
						deltaY = start.coords[1] - stop.coords[1];
					
					dispX = Math.abs(deltaX);
					dispY = Math.abs(deltaY);
					
					if (dispX > 30 && dispY < 75)
						dir = deltaX > 0 ? 'left' : 'right';
					else if (dispY > 30 && dispX < 75)
						dir = deltaY > 0 ? 'up' : 'down';
				}
				
				if (wgt && dir)
					_doEvt(new zk.Event(wgt, 'onSwipe',
						{dispX: dispX, dispY: dispY, dispT: dispT, dir: dir},
						{start: start, stop: stop, dir: dir}, devt));
			}
			start = stop = null;
		}
		
		jq(document).bind(moveEvt, swipeMove)
			.one(endEvt, swipeEnd);
	}
})();