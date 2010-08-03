/* Widget.js

	Purpose:
		
	Description:
		
	History:
		Fri Nov  7 17:14:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The ZUL widgets and utilities
 */
//zk.$package('zul');

(function () {
	//Tooltip
	var _tt_inf, _tt_tmClosing, _tt_tip, _tt_ref, _tt_params;
	function _tt_beforeBegin(ref) {
		if (_tt_tip && !_tt_tip.isOpen()) { //closed by other (such as clicking on menuitem)
			_tt_clearOpening_();
			_tt_clearClosing_();
			_tt_tip = _tt_ref = null;
		}

		var overTip = _tt_tip && zUtl.isAncestor(_tt_tip, ref);
		if (overTip) _tt_clearClosing_(); //not close tip if over tip
		return !overTip;//disable tip in tip
	}
	function _tt_begin(tip, ref, params) {
		_tt_params = params;
		if (_tt_tip != tip) {
			_tt_close_();

			_tt_inf = {
				tip: tip, ref: ref,
				timer: setTimeout(_tt_open_, params.delay !== undefined ? params.delay : zk.tipDelay)
			};
		} else if (_tt_ref == ref)
			_tt_clearClosing_();
	}
	function _tt_end(ref) {
		if (_tt_ref == ref || _tt_tip == ref)
			_tt_tmClosing =
				setTimeout(_tt_close_, 100);
			//don't cloes immediate since user might move from ref to toolip
		else
			_tt_clearOpening_();
	}
	function _tt_clearOpening_() {
		var inf = _tt_inf;
		if (inf) {
			_tt_inf = null;
			clearTimeout(inf.timer);
		}
	}
	function _tt_clearClosing_() {
		var tmClosing = _tt_tmClosing;
		if (tmClosing) {
			_tt_tmClosing = null;
			clearTimeout(tmClosing);
		}
	}
	function _tt_open_() {
		var inf = _tt_inf;
		if (inf) {
			_tt_tip = inf.tip,
			_tt_ref = inf.ref;
			_tt_inf = null;
			var params = _tt_params,
				xy = params.x !== undefined ? [params.x, params.y]
							: zk.currentPointer;
			_tt_tip.open(_tt_ref, xy, params.position ? params.position : params.x === null ? "after_pointer" : null, {sendOnOpen:true});
		}
	}
	function _tt_close_() {
		_tt_clearOpening_();
		_tt_clearClosing_();

		var tip = _tt_tip;
		if (tip) {
			_tt_tip = _tt_ref = null;
			tip.close({sendOnOpen:true});
		}
	}
	function _setCtrlKeysErr(msg) {
		zk.error("setCtrlKeys: " + msg);
	}

/** The base class for ZUL widget.
* <p>The corresponding Java class is org.zkoss.zul.impl.XulElement.
 */
zul.Widget = zk.$extends(zk.Widget, {
	/** Returns the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user right-clicks on the element (aka., context menu).
	 *
	 * <p>Default: null (no context menu).
	 * @return String
	 */
	getContext: function () {
		return this._context;
	},
	/**
	 * Sets the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user right-clicks on the element (aka., context menu).
	 * @param String context the ID of the popup widget.
	 * @see #setContext(zul.wgt.Popup) 
	 */
	/** Sets the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user right-clicks on the element (aka., context menu).
	 *
	 * <p>An onOpen event is sent to the context menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
	 *
	 * <p>If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 *
	 * <p>Example:<br/>
	 * <pre><code>
	 * wgt.setContext('an_id');
	 * wgt.setContext('uuid(an_uuid)');
	 * wgt.setContext(a_wgt);
	 * </code></pre>
	 * Both reference a component whose ID is "some".
	 * But, if there are several components with the same ID,
	 * the first one can reference to any of them.
	 * And, the second one reference to the component in the same ID space
	 * (of the label component).
	 * 
	 * 
	 * <p>The context menu can be shown by a position from
	 * {@link zul.wgt.Popup#open(zk.Widget, Offset, String, Map)}
	 * or the location of <code>x</code> and <code>y</code>, you can specify the following format:</br>
	 * <ul>
	 * <li><code>id, position</code></li>
	 * <li><code>id, position=before_start</code></li>
	 * <li><code>id, x=15, y=20</code></li>
	 * <li><code>uuid(comp_uuid), position</code></li>
	 * <li><code>uuid(comp_uuid), x=15, y=20</code></li>
	 * </ul>
	 * For example,
	 * <pre>
	 * wgt.setContext('an_id', 'start_before');
	 * </pre>
	 * @param zul.wgt.Popup context the popup widget.
	 * @return zul.Widget
	 */
	setContext: function (context) {
		if (zk.Widget.isInstance(context))
			context = 'uuid(' + context.uuid + ')';
		this._context = context;
		return this;
	},
	/** Returns the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user clicks on the element.
	 *
	 * <p>Default: null (no popup).
	 * @return String the ID of the popup widget
	 */
	getPopup: function () {
		return this._popup;
	},
	/**
	 * Sets the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user clicks on the element.
	 * @param String popup the ID of the popup widget.
	 * @see #setPopup(zul.wgt.Popup) 
	 */
	/** Sets the ID of the popup ({@link zul.wgt.Popup}) that should appear
	 * when the user clicks on the element.
	 *
	 * <p>An onOpen event is sent to the popup menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
	 *
	 * <p>If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 *
	 * <p>Example:<br/>
	 * <pre><code>
	 * wgt.setPopup('an_id');
	 * wgt.setPopup('uuid(an_uuid)');
	 * wgt.setPopup(a_wgt);
	 * </code></pre>
	 * Both reference a component whose ID is "some".
	 * But, if there are several components with the same ID,
	 * the first one can reference to any of them.
	 * And, the second one reference to the component in the same ID space
	 * (of the label component).
	 * 
	 * 
	 * <p>The popup menu can be shown by a position from
	 * {@link zul.wgt.Popup#open(zk.Widget, Offset, String, Map)}
	 * or the location of <code>x</code> and <code>y</code>, you can specify the following format:</br>
	 * <ul>
	 * <li><code>id, position</code></li>
	 * <li><code>id, position=before_start</code></li>
	 * <li><code>id, x=15, y=20</code></li>
	 * <li><code>uuid(comp_uuid), position</code></li>
	 * <li><code>uuid(comp_uuid), x=15, y=20</code></li>
	 * </ul>
	 * For example,
	 * <pre>
	 * wgt.setPopup('an_id', 'start_before');
	 * </pre>
	 * @param zul.wgt.Popup popup the popup widget.
	 * @return zul.Widget
	 */
	setPopup: function (popup) {
		if (zk.Widget.isInstance(popup))
			popup = 'uuid(' + popup.uuid + ')';
		this._popup = popup;
		return this;
	},
	/** Returns the ID of the popup ({@link zul.wgt.Popup}) that should be used
	 * as a tooltip window when the mouse hovers over the element for a moment.
	 * The tooltip will automatically disappear when the mouse is moved away.
	 *
	 * <p>Default: null (no tooltip).
	 * @return String the ID of the popup widget
	 */
	getTooltip: function () {
		return this._tooltip;
	},
	/**
	 * Sets the ID of the popup ({@link zul.wgt.Popup}) that should be used
	 * as a tooltip window when the mouse hovers over the element for a moment.
	 * @param String tooltip the ID of the popup widget.
	 * @see #setPopup(zul.wgt.Popup) 
	 */
	/** Sets the ID of the popup ({@link zul.wgt.Popup}) that should be used
	 * as a tooltip window when the mouse hovers over the element for a moment.
	 *
	 * <p>An onOpen event is sent to the tooltip if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it not only searches its ID space,
	 * but also all ID spaces in the desktop.
	 * It first searches its own ID space, and then the other Id spaces
	 * in the same browser window (might have one or multiple desktops).
	 *
	 * <p>If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 *
	 * <p>Example:<br/>
	 * <pre><code>
	 * wgt.setTooltip('an_id');
	 * wgt.setTooltip('uuid(an_uuid)');
	 * wgt.setTooltip(a_wgt);
	 * </code></pre>
	 * Both reference a component whose ID is "some".
	 * But, if there are several components with the same ID,
	 * the first one can reference to any of them.
	 * And, the second one reference to the component in the same ID space
	 * (of the label component).
	 * 
	 * 
	 * <p>The tooltip can be shown by a position from
	 * {@link zul.wgt.Popup#open(zk.Widget, Offset, String, Map)}
	 * or the location of <code>x</code> and <code>y</code>, and can be specified
	 * with a delay time (in millisecond), you can specify the following format:
	 * </br>
	 * <ul>
	 * <li><code>id, position</code></li>
	 * <li><code>id, position=before_start, delay=500</code></li>
	 * <li><code>id, x=15, y=20</code></li>
	 * <li><code>uuid(comp_uuid), position</code></li>
	 * <li><code>uuid(comp_uuid), x=15, y=20</code></li>
	 * </ul>
	 * For example,
	 * <pre>
	 * wgt.setTooltip('an_id', 'start_before');
	 * </pre>
	 * @param zul.wgt.Popup popup the popup widget.
	 * @return zul.Widget
	 */
	setTooltip: function (tooltip) {
		if (zk.Widget.isInstance(tooltip))
			tooltip = 'uuid(' + tooltip.uuid + ')';
		this._tooltip = tooltip;
		return this;
	},
	/** Returns what keystrokes to intercept.
	 * <p>Default: null.
	 * @return String
	 */
	getCtrlKeys: function () {
		return this._ctrlKeys;
	},
	/** Sets what keystrokes to intercept.
	 *
	 * <p>The string could be a combination of the following:
	 * <dl>
	 * <dt>^k</dt>
	 * <dd>A control key, i.e., Ctrl+k, where k could be a~z, 0~9, #n</dd>
	 * <dt>@k</dt>
	 * <dd>A alt key, i.e., Alt+k, where k could be a~z, 0~9, #n</dd>
	 * <dt>$n</dt>
	 * <dd>A shift key, i.e., Shift+n, where n could be #n.
	 * Note: $a ~ $z are not supported.</dd>
	 * <dt>#home</dt>
	 * <dd>Home</dd>
	 * <dt>#end</dt>
	 * <dd>End</dd>
	 * <dt>#ins</dt>
	 * <dd>Insert</dd>
	 * <dt>#del</dt>
	 * <dd>Delete</dd>
	 * <dt>#left</dt>
	 * <dd>Left arrow</dd>
	 * <dt>#right</dt>
	 * <dd>Right arrow</dd>
	 * <dt>#up</dt>
	 * <dd>Up arrow</dd>
	 * <dt>#down</dt>
	 * <dd>Down arrow</dd>
	 * <dt>#pgup</dt>
	 * <dd>PageUp</dd>
	 * <dt>#pgdn</dt>
	 * <dd>PageDn</dd>
	 * <dt>#f1 #f2 ... #f12</dt>
	 * <dd>Function keys representing F1, F2, ... F12</dd>
	 * </dl>
	 *
	 * <p>For example,
	 * <dl>
	 * <dt>^a^d@c#f10#left#right</dt>
	 * <dd>It means you want to intercept Ctrl+A, Ctrl+D, Alt+C, F10,
	 * Left and Right.</dd>
	 * <dt>^#left</dt>
	 * <dd>It means Ctrl+Left.</dd>
	 * <dt>^#f1</dt>
	 * <dd>It means Ctrl+F1.</dd>
	 * <dt>@#f3</dt>
	 * <dd>It means Alt+F3.</dd>
	 * </dl>
	 *
	 * <p>Note: it doesn't support Ctrl+Alt, Shift+Ctrl, Shift+Alt or Shift+Ctrl+Alt.
	 * @param String keys
	 * @return zul.Widget
	 */
	setCtrlKeys: function (keys) {
		if (this._ctrlKeys == keys) return;
		if (!keys) {
			this._ctrlKeys = this._parsedCtlKeys = null;
			return;
		}

		var parsed = [{}, {}, {}, {}, {}], //ext(#), ctl, alt, shft
			which = 0;
		for (var j = 0, len = keys.length; j < len; ++j) {
			var cc = keys.charAt(j); //ext
			switch (cc) {
			case '^':
			case '$':
			case '@':
				if (which)
					return _setCtrlKeysErr("Combination of Shift, Alt and Ctrl not supported: "+keys);
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
					return _setCtrlKeysErr("Unexpected character "+cc+" in "+keys);

				var s = keys.substring(j+1, k).toLowerCase();
				if ("pgup" == s) cc = 33;
				else if ("pgdn" == s) cc = 34;
				else if ("end" == s) cc = 35;
				else if ("home" == s) cc = 36;
				else if ("left" == s) cc = 37;
				else if ("up" == s) cc = 38;
				else if ("right" == s) cc = 39;
				else if ("down" == s) cc = 40;
				else if ("ins" == s) cc = 45;
				else if ("del" == s) cc = 46;
				else if (s.length > 1 && s.charAt(0) == 'f') {
					var v = zk.parseInt(s.substring(1));
					if (v == 0 || v > 12)
						return _setCtrlKeysErr("Unsupported function key: #f" + v);
					cc = 112 + v - 1;
				} else
					return _setCtrlKeysErr("Unknown #"+s+" in "+keys);

				parsed[which][cc] = true;
				which = 0;
				j = k - 1;
				break;
			default:
				if (!which || ((cc > 'Z' || cc < 'A') 
				&& (cc > 'z' || cc < 'a') && (cc > '9' || cc < '0')))
					return _setCtrlKeysErr("Unexpected character "+cc+" in "+keys);
				if (which == 3)
					return _setCtrlKeysErr("$a - $z not supported (found in "+keys+"). Allowed: $#f1, $#home and so on.");

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
		if (!this.shallIgnoreClick_(evt) && !evt.contextSelected) {
			var params = this._popup ? this._parsePopParams(this._popup) : {},
				popup = this._smartFellow(params.id);
			if (popup) {
				evt.contextSelected = true;
				
				// to avoid a focus in IE, we have to pop up it later. for example, userguide/#t5
				var self = this,
					xy = params.x !== undefined ? [params.x, params.y]
							: [evt.pageX, evt.pageY];
				setTimeout(function() {
					popup.open(self, xy, params.position ? params.position : null, {sendOnOpen:true});
				}, 0);
				evt.stop({dom:true});
			}
		}
		if (popupOnly !== true)
			this.$supers('doClick_', arguments);
	},
	doRightClick_: function (evt) {
		if (!this.shallIgnoreClick_(evt) && !evt.contextSelected) {
			var params = this._context ? this._parsePopParams(this._context) : {},
				ctx = this._smartFellow(params.id);
			if (ctx) {
				evt.contextSelected = true;
				
				// to avoid a focus in IE, we have to pop up it later. for example, userguide/#t5
				var self = this,
					xy = params.x !== undefined ? [params.x, params.y]
							: [evt.pageX, evt.pageY];
				setTimeout(function() {
					ctx.open(self, xy, params.position ? params.position : null, {sendOnOpen:true}); //Bug #2870620
				}, 0);
				evt.stop({dom:true}); //prevent default context menu to appear
			}
		}
		this.$supers('doRightClick_', arguments);
	},
	doTooltipOver_: function (evt) {
		if (!evt._tiped && _tt_beforeBegin(this)) {
			var params = this._tooltip ? this._parsePopParams(this._tooltip) : {},
				tip = this._smartFellow(params.id);
			if (tip) {
				evt._tiped = true;
				_tt_begin(tip, this, params);
			}
		}
	},
	doTooltipOut_: function (evt) {
		_tt_end(this);
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
	/**
	 * Called after {@link zk.Widget#doKeyDown_} is called and the event
	 * propagation is not stopped.
	 * <p>Default: handles the control keys, including onOK and onCancel,
	 *  by searching up the ancestor chain to see if any one is listening.
	 *  If found, it calls {@link #beforeCtrlKeys_} for each widget that were
	 *  searched, and then fire the event.
	 *  @param zk.Event evt the widget event.
	 *  @see #setCtrlKeys
	 */
	afterKeyDown_: function (evt) {
		var keyCode = evt.keyCode, evtnm = "onCtrlKey", okcancel;
		switch (keyCode) {
		case 13: //ENTER
			var target = evt.domTarget, tn = jq.nodeName(target);
			if (tn == "textarea" || (tn == "button"
			// if button's ID end with '-a' still fire onOK(Like Listbox and Menupopup) 
			&& (!target.id || !target.id.endsWith('-a')))
			|| (tn == "input" && target.type.toLowerCase() == "button"))
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
				break;

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
	/**
	 * Called before a control key is pressed. A control key includes onOK and
	 * onCancel; refer to #setCtrlKeys for details.
	 * <p>Default: does nothing (but return false)
	 * It is usually overridden by a stateful widget, such as an input box,
	 * to update its state to the server, such as firing the onChange event.
	 * @param zk.Event evt the widget event.
	 * @return boolean if true, the widget want to abort the firing of the control
	 * 		key. In other words, if true is returned, the control key is ignored. 
	 */
	beforeCtrlKeys_: function (evt) {
	}
});

})();