/* domsafari.js

	Purpose:
		Enhance/fix jQuery for Safari
	Description:
		
	History:
		Fri Jun 12 12:03:53     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	function _dissel() {
		this.style.KhtmlUserSelect = "none";
	}
	function _ensel() {
		this.style.KhtmlUserSelect = "";
	}

zk.copy(zjq.prototype, {
	disableSelection: function () {
		return this.jq.each(_dissel);
	},
	enableSelection: function () {
		return this.jq.each(_ensel);
	},
	beforeHideOnUnbind: function () { //Bug 3076384 (though i cannot reproduce in chrome/safari)
		return this.jq.each(function () {
			for (var ns = this.getElementsByTagName("iframe"), j = ns.length; j--;)
				ns[j].src = zjq.src0;
		});
	}
});

zjq._sfKeys = {
	25: 9, 	   // SHIFT-TAB
	63232: 38, // up
	63233: 40, // down
	63234: 37, // left
	63235: 39, // right
	63272: 46, // delete
	63273: 36, // home
	63275: 35, // end
	63276: 33, // pgup
	63277: 34  // pgdn
};
zk.override(jq.event, zjq._evt = {}, {
	fix: function (evt) {
		evt = zjq._evt.fix.apply(this, arguments);
		var v = zjq._sfKeys[evt.keyCode];
		if (v) evt.keyCode = v;
		return evt;
	}
});


if (zk.ios) {
	zjq._simulatedMouseEvent = function (type, button, changedTouch){
		var simulatedEvent = document.createEvent("MouseEvent");
		simulatedEvent.initMouseEvent(type, true, true, window, button, 
			changedTouch.screenX, changedTouch.screenY, changedTouch.clientX, changedTouch.clientY,
			false, false, false, false, 0, null);
		return changedTouch.target.dispatchEvent(simulatedEvent);					
	};
	
	var lastTap = null,		// Holds last tapped element (so we can compare for double tap)
		tapValid = false,	// Are we still in the .6 second window where a double tap can occur
		tapTimeout = null;	// The timeout reference

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
				lastTap = null;
				tapValid = false;
				
				if (!zjq._simulatedMouseEvent('dblclick', 0, changedTouch))
					touchEvt.preventDefault();	
			} else {
				lastTap = node;
				tapValid = true;
				tapTimeout = setTimeout(cancelTap, 600);
			}
		}
	});
}
})();
