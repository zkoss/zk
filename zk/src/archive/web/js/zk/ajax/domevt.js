/* domevt.js

	Purpose:
		DOM Event Handling
	Description:
		
	History:
		Thu Oct 23 10:53:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
/** DOM Event Utilities. */
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
	/** Returns the target widget (zk.Widget) of the event. */
	widget: function (event) {
		for (var n = zEvt.target(event); n; n = n.parentNode)
			if (n.z_wgt) return n.z_wgt;
		return null;
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

		var ofs = zDom.cmOffset(target ? target: zEvt.target(evt));
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
	},

	/** Listens a browser event.
	 */
	listen: function (el, evtnm, fn) {
		if (el.addEventListener)
			el.addEventListener(evtnm, fn, false);
		else /*if (el.attachEvent)*/
			el.attachEvent('on' + evtnm, fn);

		//Bug 1811352
		if ("submit" == evtnm && zDom.tag(el) == "FORM") {
			if (!el._submfns) el._submfns = [];
			el._submfns.push(fn);
		}
	},
	/** Un-listens a browser event.
	 */
	unlisten: function (el, evtnm, fn) {
		if (el.removeEventListener)
			el.removeEventListener(evtnm, fn, false);
		else if (el.detachEvent) {
			try {
				el.detachEvent('on' + evtnm, fn);
			} catch (e) {
			}
		}

		//Bug 1811352
		if ("submit" == evtnm && zDom.tag(el) == "FORM" && el._submfns)
			el._submfns.$remove(fn);
	},

	/** Enables ESC (default behavior). */
	enableESC: function () {
		if (zDom._noESC) {
			zEvt.unlisten(document, "keydown", zDom._noESC);
			delete zDom._noESC;
		}
		if (zDom._onErrChange) {
			window.onerror = zDom._oldOnErr;
			if (zDom._oldOnErr) delete zDom._oldOnErr;
			delete zDom._onErrChange;
		}
	},
	/** Disables ESC (so loading won't be aborted). */
	disableESC: function () {
		if (!zDom._noESC) {
			zDom._noESC = function (evt) {
				if (!evt) evt = window.event;
				if (evt.keyCode == 27) {
					if (evt.preventDefault) {
						evt.preventDefault();
						evt.stopPropagation();
					} else {
						evt.returnValue = false;
						evt.cancelBubble = true;
					}
					return false;//eat
				}
				return true;
			};
			zEvt.listen(document, "keydown", zDom._noESC);

			//FUTURE: onerror not working in Safari and Opera
			//if error occurs, loading will be never ended, so try to ignore
			//we cannot use zEvt.listen. reason: no way to get back msg...(FF)
			zDom._oldOnErr = window.onerror;
			zDom._onErrChange = true;
			window.onerror =
		function (msg, url, lineno) {
			//We display errors only for local class web resource
			//It is annoying to show error if google analytics's js not found
			var au = zAu.comURI();
			if (au && url.indexOf(location.host) >= 0) {
				var v = au.lastIndexOf(';');
				v = v >= 0 ? au.substring(0, v): au;
				if (url.indexOf(v + "/web/") >= 0) {
					msg = mesg.FAILED_TO_LOAD + url + "\n" + mesg.FAILED_TO_LOAD_DETAIL
						+ "\n" + mesg.CAUSE + msg+" (line "+lineno + ")";
					if (zk.error) zk.error(msg);
					else alert(msg);
					return true;
				}
			}
		};
		}
	},

	//event handler//
	/** The default onfocus. */
	onfocus: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zEvt.widget(evt);
		zk.currentFocus = wgt;

		//TODO: handle zIndex, close floats

		if (wgt.isListen('onFocus'))
			wgt.fire2("onFocus");
	},
	/** The default onblur. */
	onblur: function (evt) {
		if (!evt) evt = window.event;
		var wgt = zEvt.widget(evt);
		zk.currentFocus = null;

		//TODO: handle validation

		if (wgt.isListen('onBlur'))
			wgt.fire2("onBlur");
	}
 };
