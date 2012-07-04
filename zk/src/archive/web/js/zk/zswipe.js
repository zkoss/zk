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
		endEvt = hastouch ? 'touchend' : 'mouseup';
	
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

zk.Swipe = zk.$extends(zk.Object, {
	_start: null,
	_stop: null,
	
	$init: function () {
		jq(document).bind(startEvt, this._swipeStart)
			.bind(moveEvt, this._swipeMove)
			.bind(endEvt, this._swipeEnd);
	},
	destroy: function() {
		jq(document).unbind(startEvt, this._swipeStart)
			.unbind(moveEvt, this._swipeMove)
			.unbind(endEvt, this._swipeEnd);
	},
	_swipeStart: function(devt) {
		var evt = devt.originalEvent,
			data = evt.touches ? evt.touches[0] : evt;
		
		this._start = {
			time: (new Date()).getTime(),
			coords: [data.pageX, data.pageY]
		};
	},
	_swipeMove: function(devt) {
		if (!this._start) return;
		var evt = devt.originalEvent,
			data = evt.touches ? evt.touches[0] : evt;
			
		this._stop = {
			time: (new Date()).getTime(),
			coords: [data.pageX, data.pageY]
		};
		
		// prevent scrolling
		var dispX = Math.abs(this._start.coords[0] - this._stop.coords[0]),
			dispY = Math.abs(this._start.coords[1] - this._stop.coords[1]);
		if (dispX > 5)
			evt.preventDefault();
	},
	_swipeEnd: function(devt) {
		var start = this._start,
			stop = this._stop;
		
		if (start && stop) {
			var wgt = zk.Widget.$(devt, {child:true}),
				evt = devt.originalEvent,
				data = evt.touches ? evt.touches[0] : evt;;
			if (wgt)
				_doEvt(new zk.Event(wgt, 'onSwipe', data, {start: start, stop: stop}, devt));
		}
		this._start = this._stop = null;
	}
});
})();