/* domevt.js

{{IS_NOTE
	Purpose:
		DOM Event Handling
	Description:
		
	History:
		Thu Oct 23 10:53:17     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zEvt = {
	BS:		8,
	TAB:	9,
	ENTER:	13,
	SHIFT:	16,
	CTRL:	17,
	ALT:	18,
	ESC:	27,
	LFT:	37,
	UP:		38,
	RGH:	39,
	DN:		40,
	INS:	45,
	DEL:	46,
	HOME:	36,
	END:	35,
	PGUP:	33,
	PGDN:	34,
	F1:		112,

	/** Returns the target element of the event. */
	target: function(event) {
		return event.target || event.srcElement;
	},
	/** Stops the event propogation. */
	stop: function(event) {
		if (event.preventDefault) {
			event.preventDefault();
			event.stopPropagation();
		} else {
			event.returnValue = false;
			event.cancelBubble = true;
			if (!event.shiftKey && !event.ctrlKey)
				event.keyCode = 0; //Bug 1834891
		}
	},

	//Mouse Info//
	/** Returns if it is the left click. */
	leftClick: function(event) {
		return (((event.which) && (event.which == 1)) ||
		((event.button) && (event.button == 1)));
	},
	/** Returns the mouse status.
	 */
	mouseData: function (evt, target) {
		var extra = "";
		if (evt.altKey) extra += "a";
		if (evt.ctrlKey) extra += "c";
		if (evt.shiftKey) extra += "s";

		var ofs = zDom.cmOffset(target);
		var x = zEvt.x(evt) - ofs[0];
		var y = zEvt.y(evt) - ofs[1];
		return [x, y, extra];
	},
	/** Returns the X coordinate of the mouse pointer. */
	x: function (event) {
		return event.pageX || (event.clientX +
		(document.documentElement.scrollLeft || document.body.scrollLeft));
  	},
	/** Returns the Y coordinate of the mouse pointer. */
	y: function(event) {
		return event.pageY || (event.clientY +
		(document.documentElement.scrollTop || document.body.scrollTop));
	},

	//Key Info//
	/** Returns the char code. */
	charCode: function(evt) {
		return evt.charCode || evt.keyCode;
	},
	/** Returns the key code. */
	keyCode: function(evt) {
		var k = evt.keyCode || evt.charCode;
		return zk.safari ? (this.safariKeys[k] || k) : k;
	}
 };
