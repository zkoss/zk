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
	$init: function (widget, node, opts) {
		this.widget = widget;
		this.node = node = node ? jq(node, zk)[0]: widget.node || (widget.$n ? widget.$n() : null);
		if (!node)
			throw "Handle required for " + widget;
		
		this.opts = zk.$default(opts, {
			scrollThreshold: 5,
			duration: 500,
			minDisplacement: 30,
			maxDisplacement: 75
		});
		
		jq(this.node).bind(startEvt, this.proxy(this._swipeStart));
	},
	
	destroy: function (node) {
		jq(node).unbind(startEvt, this.proxy(this._swipeStart));
		this.widget = this.node = this.opts = null;
	},
	
	_swipeStart: function (devt) {
		var evt = devt.originalEvent,
			data = evt.touches ? evt.touches[0] : evt;
		
		start = {
			time: evt.timeStamp || Date.now(),
			coords: [data.pageX, data.pageY]
		};
		jq(this.node).bind(moveEvt, this.proxy(this._swipeMove)).one(endEvt, this.proxy(this._swipeEnd));
	},
	
	_swipeMove: function (devt) {
		if (!start) return;
		var evt = devt.originalEvent,
			data = evt.touches ? evt.touches[0] : evt;
		evt.stopPropagation();
		
		stop = {
			time: evt.timeStamp || Date.now(),
			coords: [data.pageX, data.pageY]
		};
		
		// prevent scrolling when displacement is larger than scrollThreshold
		var dispX = Math.abs(start.coords[0] - stop.coords[0]),
			dispY = Math.abs(start.coords[1] - stop.coords[1]),
			scrollThreshold = this.opts.scrollThreshold;
		if (dispX > scrollThreshold || dispY > scrollThreshold)
			evt.preventDefault();
	},
	
	_swipeEnd: function (devt) {
		jq(this.node).unbind(moveEvt, this.proxy(this._swipeMove));
		if (start && stop) {
			var dispX, dispY, dispT = stop.time - start.time, dir;
			
			if (dispT < this.opts.duration) {
				var deltaX = start.coords[0] - stop.coords[0],
					deltaY = start.coords[1] - stop.coords[1],
					min = this.opts.minDisplacement,
					max = this.opts.maxDisplacement;
				
				dispX = Math.abs(deltaX);
				dispY = Math.abs(deltaY);
				
				if (dispX > min && dispY < max)
					dir = deltaX > 0 ? 'left' : 'right';
				else if (dispY > min && dispX < max)
					dir = deltaY > 0 ? 'up' : 'down';
			}
			
			var wgt = this.widget;
			if (wgt)
				_doEvt(new zk.Event(wgt, 'onSwipe',
					{dispX: dispX, dispY: dispY, dispT: dispT, dir: dir},
					{start: start, stop: stop, dir: dir}, devt));
		}
		start = stop = null;
	}
});
})();