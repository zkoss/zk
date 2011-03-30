zjq._simulatedMouseEvent = function (type, button, changedTouch, target){
	var simulatedEvent = document.createEvent("MouseEvent");
	simulatedEvent.initMouseEvent(type, true, true, window, button, 
		changedTouch.screenX, changedTouch.screenY, changedTouch.clientX, changedTouch.clientY,
		false, false, false, false, 0, null);
	return (target ? target: changedTouch.target).dispatchEvent(simulatedEvent);					
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