/* Widget.js

	Purpose:
		
	Description:
		
	History:
		Fri Nov  7 17:14:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The base class for XUL widget (org.zkoss.zul.impl.XulElement).
 */
zul.Widget = zk.$extends(zk.Widget, {
	getContext: function () {
		return this._context;
	},
	setContext: function (context) {
		if (zk.Widget.isInstance(context))
			context = 'uuid(' + context.uuid + ')';
		this._context = context;
		return this;
	},
	getPopup: function () {
		return this._popup;
	},
	setPopup: function (popup) {
		if (zk.Widget.isInstance(popup))
			popup = 'uuid(' + popup.uuid + ')';
		this._popup = popup;
		return this;
	},
	getTooltip: function () {
		return this._tooltip;
	},
	setTooltip: function (tooltip) {
		if (zk.Widget.isInstance(tooltip))
			tooltip = 'uuid(' + tooltip.uuid + ')';
		this._tooltip = tooltip;
		return this;
	},
	getCtrlKeys: function () {
		return this._ctrlKeys;
	},
	setCtrlKeys: function (keys) {
		if (this._ctrlKeys == keys) return;
		if (!keys) {
			this._ctrlKeys = this._parsedCtlKeys = null;
			return;
		}

		var parsed = [{}, {}, {}, {}, {}], //ext(#), ctl, alt, shft
			which = 0, $Event = zk.Event;
		for (var j = 0, len = keys.length; j < len; ++j) {
			var cc = keys.charAt(j); //ext
			switch (cc) {
			case '^':
			case '$':
			case '@':
				if (which)
					throw "Combination of Shift, Alt and Ctrl not supported: "+keys;
				which = cc == '^' ? 1: cc == '@' ? 2: 3;
				break;
			case '#':
				var k = j + 1;
				for (; k < len; ++k) {
					var c2 = keys.charAt(k);
					if ((c2 > 'Z' || c2 < 'A') 	&& (c2 > 'z' || c2 < 'a')
					&& (c2 > '9' || c2 < '0'))
						break;
				}
				if (k == j + 1)
					throw "Unexpected character "+cc+" in "+keys;

				var s = keys.substring(j+1, k).toLowerCase();
				if ("pgup" == s) cc = $Event.PGUP;
				else if ("pgdn" == s) cc = $Event.PGDN;
				else if ("end" == s) cc = $Event.END;
				else if ("home" == s) cc = $Event.HOME;
				else if ("left" == s) cc = $Event.LFT;
				else if ("up" == s) cc = $Event.UP;
				else if ("right" == s) cc = $Event.RGH;
				else if ("down" == s) cc = $Event.DN;
				else if ("ins" == s) cc = $Event.INS;
				else if ("del" == s) cc = $Event.DEL;
				else if (s.length > 1 && s.charAt(0) == 'f') {
					var v = zk.parseInt(s.substring(1));
					if (v == 0 || v > 12)
						throw "Unsupported function key: #f" + v;
					cc = $Event.F1 + v - 1;
				} else
					throw "Unknown #"+s+" in "+keys;

				parsed[which][cc] = true;
				which = 0;
				j = k - 1;
				break;
			default:
				if (!which || ((cc > 'Z' || cc < 'A') 
				&& (cc > 'z' || cc < 'a') && (cc > '9' || cc < '0')))
					throw "Unexpected character "+cc+" in "+keys;
				if (which == 3)
					throw "$a - $z not supported (found in "+keys+"). Allowed: $#f1, $#home and so on.";

				if (cc <= 'z' && cc >= 'a')
					cc = cc.toUpperCase();
				parsed[which][cc.charCodeAt(0)] = true;
				which = 0;
				break;
			}
		}

		this._parsedCtlKeys = parsed;
		this._ctrlKeys = keys;
		return this;
	},

	_parsePopParams: function (txt) {
		var params = {},
			index = txt.indexOf(','),
			start = txt.indexOf('='),
			t = txt;
		if (start != -1)
			t = txt.substring(0, txt.substring(0, start).lastIndexOf(','));
		
		if (index != -1) {
			params.id = t.substring(0, index).trim();
			var t2 = t.substring(index + 1, t.length);
			if (t2)
				params.position = t2.trim();				
			
			zk.copy(params, zUtl.parseMap(txt.substring(t.length, txt.length)));
		} else
			params.id = txt.trim();
		
		if (params.x)
			params.x = zk.parseInt(params.x);
		if (params.y)
			params.y = zk.parseInt(params.y);
		if (params.delay)
			params.delay = zk.parseInt(params.delay);
		return params;
	},
	//super//
	doClick_: function (evt, popupOnly) {
		if (!evt._popuped) {
			var params = this._popup ? this._parsePopParams(this._popup) : {},
				popup = this._smartFellow(params.id);
			if (popup) {
				evt._popuped = true;
				
				// to avoid a focus in IE, we have to pop up it later. for example, userguide/#t5
				var self = this,
					xy = params.x !== undefined ? [params.x, params.y]
							: [evt.pageX, evt.pageY];
				setTimeout(function() {
					popup.open(self, xy, params.position ? params.position : null, {sendOnOpen:true});
				}, 0);
				evt.stop();
			}
		}
		if (popupOnly !== true)
			this.$supers('doClick_', arguments);
	},
	doRightClick_: function (evt) {
		if (!evt._ctxed) {
			var params = this._context ? this._parsePopParams(this._context) : {},
				ctx = this._smartFellow(params.id);
			if (ctx) {
				evt._ctxed = true;
				
				// to avoid a focus in IE, we have to pop up it later. for example, userguide/#t5
				var self = this,
					xy = params.x !== undefined ? [params.x, params.y]
							: [evt.pageX, evt.pageY];
				setTimeout(function() {
					ctx.open(self, xy, params.position ? params.position : null, {sendOnOpen:true});
				}, 0);
				evt.stop(); //prevent default context menu to appear
			}
		}
		if (this.isListen('onRightClick'))
			evt.stop();
		this.$supers('doRightClick_', arguments);
	},
	doTooltipOver_: function (evt) {
		if (!evt._tiped && zk.eff.tooltip.beforeBegin(this)) {
			var params = this._tooltip ? this._parsePopParams(this._tooltip) : {},
				tip = this._smartFellow(params.id);
			if (tip) {
				evt._tiped = true;
				zk.eff.tooltip.begin(tip, this, params);
			}
		}
	},
	doTooltipOut_: function (evt) {
		zk.eff.tooltip.end(this);
	},
	doMouseOver_: function (evt) {
		this.doTooltipOver_(evt);
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		this.doTooltipOut_(evt);
		this.$supers('doMouseOut_', arguments);
	},
	_smartFellow: function (id) {
		return id ? id.startsWith('uuid(') && id.endsWith(')') ?
			zk.Widget.$(id.substring(5, id.length - 1)):
			this.$f(id, true): null;
	},

	afterKeyDown_: function (evt) {
		var keyCode = evt.keyCode, evtnm = "onCtrlKey", okcancel;
		switch (keyCode) {
		case 13: //ENTER
			var target = evt.domTarget, tn = target.tagName;
			if (tn == "TEXTAREA" || tn == "BUTTON"
			|| (tn == "INPUT" && target.type.toLowerCase() == "button"))
				return; //don't change button's behavior (Bug 1556836)
			okcancel = evtnm = "onOK";
			break;
		case 27: //ESC
			okcancel = evtnm = "onCancel";
			break;
		case 16: //Shift
		case 17: //Ctrl
		case 18: //Alt
			return;
		case 45: //Ins
		case 46: //Del
			break;
		default:
			if ((keyCode >= 33 && keyCode <= 40) //PgUp, PgDn, End, Home, L, U, R, D
			|| (keyCode >= 112 && keyCode <= 123) //F1: 112, F12: 123
			|| evt.ctrlKey || evt.altKey)
				break;
			return;
		}

		var target = evt.target, wgt = target;
		for (;; wgt = wgt.parent) {
			if (!wgt) return;
			if (!wgt.isListen(evtnm, {any:true})) continue;

			if (okcancel)
				break

			var parsed = wgt._parsedCtlKeys;
			if (parsed
			&& parsed[evt.ctrlKey ? 1: evt.altKey ? 2: evt.shiftKey ? 3: 0][keyCode])
				break; //found
		}

		for (var w = target;; w = w.parent) {
			if (w.beforeCtrlKeys_ && w.beforeCtrlKeys_(evt))
				return;

			if (w == wgt) break;
		}

		wgt.fire(evtnm, zk.copy({reference: target}, evt.data),
			{ctl: true});
		evt.stop();
		//TODO: Bug 1756559

		//Bug 2041347
		if (zk.ie && keyCode == 112) {
			zk._oldOnHelp = window.onhelp;
			window.onhelp = function () {return false;}
			setTimeout(function () {window.onhelp = zk._oldOnHelp; zk._oldOnHelp = null;}, 200);
		}
	},
	beforeCtrlKeys_: function (evt) {
	}
});
