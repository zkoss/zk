/* Binder.js

	Purpose:
		
	Description:
		
	History:
		Wed, Jan 07, 2015 12:08:17 PM, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.
*/
(function () {
	var _WidgetX = {},
		_zkMatchMediaRegexPattern = /ZKMatchMedia=([^;]*)/,
		_portrait = {'0': true, '180': true}, //default portrait definition
		_initLandscape = jq.innerWidth() > jq.innerHeight(), // initial orientation is landscape or not
		_initDefault = _portrait[window.orientation]; //default orientation

zk.override(zk.Widget.prototype, _WidgetX, {
	$binder: function () {
		var w = this;
		for (; w; w = w.parent) {
			if (w['$ZKBINDER$'])
				break;
		}
		if (w) {
			if (!w._$binder)
				w._$binder = new zkbind.Binder(w, this);
			return w._$binder;
		}
		return null;
	},
	$afterCommand: function (command, args) {
		var binder = this.$binder();
		if (binder)
			binder.$doAfterCommand(command, args);
	},
	unbind_: function () {
		if (this._$binder) {
			this._$binder.destroy();
			this._$binder = null;
		}
		_WidgetX.unbind_.apply(this, arguments);
	}
});
/** Retrieves the binder if any.
 * @param Object n the object to look for. If it is a string,
 * it is assumed to be UUID, unless it starts with '$'.
 * For example, <code>zkbind.$('uuid')<code> is the same as <code>zkbind.$('#uuid')<code>,
 * and both look for a widget whose ID is 'uuid'. On the other hand,
 * <code>zkbind.$('$id') looks for a widget whose ID is 'id'.<br/>
 * and <code>zkbind.$('.className') looks for a widget whose CSS selector is 'className'.<br/>
 * If it is an DOM element ({@link DOMElement}), it will look up
 * which widget it belongs to.<br/>
 * If the object is not a DOM element and has a property called
 * <code>target</code>, then <code>target</code> is assumed.
 * Thus, you can pass an instance of {@link jq.Event} or {@link zk.Event},
 * and the target widget will be returned.
 * @param Map opts [optional] the options. Allowed values:
 * <ul>
 * <li>exact - id must exactly match uuid (i.e., uuid-xx ignored).
 * It also implies strict</li>
 * <li>strict - whether not to look up the parent node.(since 5.0.2)
 * If omitted, false is assumed (and it will look up parent).</li>
 * <li>child - whether to ensure the given element is a child element
 * of the widget's main element ({@link #$n}). In most cases, if ID
 * of an element is xxx-yyy, the the element must be a child of
 * the element whose ID is xxx. However, there is some exception
 * such as the shadow of a window.</li>
 * </ul>
 * @return zkbind.Binder
 * @since 8.0.0
 */
zkbind.$ = function (n, opts) {
	var widget = zk.Widget.$(n, opts);
	if (widget)
		return widget.$binder();
	zk.error('Not found ZK Binder with [' + n + ']');
};
	function _fixCommandName(prefix, cmd, opts, prop) {
		if (opts[prop]) {
			var ignores = {};
			ignores[prefix + cmd] = true;
			opts[prop] = ignores;
		}
	}
	//ZK-3133
	function _matchMedia(event, binder, value) {
		var cookies = binder._cookies,
			// see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent
			encodeRFC5987ValueChars = function (str) {
				return encodeURIComponent(str).replace(/['()]/g, escape).replace(/\*/g, '%2A').replace(/%(?:7C|60|5E)/g, unescape);
			};
		if (event.matches) {
			var orient = '',
				dpr = 1;
			if (zk.mobile) {
				if ((_initLandscape && _initDefault) || (!_initLandscape && !_initDefault))
					_portrait = {'-90': true, '90': true};
				orient = _portrait[window.orientation] ? 'portrait' : 'landscape';
			} else {
				orient = jq.innerWidth() > jq.innerHeight() ? 'landscape' : 'portrait';
			}
			if (window.devicePixelRatio)
				dpr = window.devicePixelRatio;
			// 16 is the length of string MATCHMEDIAVALUE_PREFIX in BinderCtrl.java
			var ci = [new Date().getTimezoneOffset(), screen.width, screen.height, screen.colorDepth, jq.innerWidth(),
					jq.innerHeight(), jq.innerX(), jq.innerY(), dpr.toFixed(1), orient, event.matches, value.substring(16)];
			// $ZKCLIENTINFO$ refers to CLIENT_INFO string in BinderCtrl.java
			binder.command(value, {'$ZKCLIENTINFO$': ci});
			if (!cookies.$contains(value)) cookies.push(value);
			document.cookie = 'ZKMatchMedia=' + encodeRFC5987ValueChars(cookies);
			document.cookie = 'ZKClientInfo=' + encodeRFC5987ValueChars(JSON.stringify(ci));
		} else {
			cookies.$remove(value);
			document.cookie = 'ZKMatchMedia=' + encodeRFC5987ValueChars(cookies);
		}
	}
/**
 * A data binder utile widget.
 * @since 8.0.0
 */
zkbind.Binder = zk.$extends(zk.Object, {
	$init: function (widget, currentTarget) {
		this.$supers('$init', arguments);
		this.$view = widget;
		this.$currentTarget = currentTarget;
		this._aftercmd = {};
		//ZK-3133
		if (widget['$ZKMATCHMEDIA$']) {
			var cookies = [];
			if (matched = _zkMatchMediaRegexPattern.exec(document.cookie)) {
				var m = matched[1];
				if (m) {
					cookies = decodeURIComponent(m).trim().split(',');
				}
			}
			this._cookies = cookies;
			var binder = this;
			var mqls = [];
			for (var i = 0; i < widget['$ZKMATCHMEDIA$'].length; i++) {
				var media = widget['$ZKMATCHMEDIA$'][i];
				var mql = window.matchMedia(media.substring(16));
				var handler = (function (s) {
					return function (event) {
						_matchMedia(event, binder, s);
					};
				})(media);
				mql.addListener(handler);
				handler(mql);
				mqls.push({mql: mql, handler: handler});
			}
			this._mediaQueryLists = mqls;
		}
	},
	/**
	 * Registers a callback after some command executed.
	 * @param String command the name of the command
	 * @param Function func the function to execute
	 */
	after: function (cmd, fn) {
		if (!fn && jq.isFunction(cmd)) {
			fn = cmd;
			cmd = this._lastcmd;
		}
			
		var ac = this._aftercmd[cmd];
		if (!ac) this._aftercmd[cmd] = [fn];
		else
			ac.push(fn);
		return this;
	},
	/**
	 * Unregisters a callback after some command executed.
	 * @param String command the name of the command
	 * @param Function func the function to execute
	 */
	unAfter: function (cmd, fn) {
		var ac = this._aftercmd[cmd];
		for (var j = ac ? ac.length : 0; j--;) {
			if (ac[j] == fn)
				ac.splice(j, 1);
		}
		return this;
	},
	/**
	 * Destroy this binder.
	 */
	destroy: function () {
		this._aftercmd = null;
		if (this._mediaQueryLists != null) {
			var mqls = this._mediaQueryLists;
			for (var i = 0; i < mqls.length; i++) {
				mqls[i].mql.removeListener(mqls[i].handler);
			}
			this._mediaQueryLists = null;
			this._cookies = null;
		}
		this.$view = null;
		this.$currentTarget = null;
	},
	/**
	 * Post a command to the binder
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param Map opts a map of options to zk.Event, if any.
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 */
	command: function (cmd, args, opts, timeout) {
		var wgt = this.$view;
		if (opts) {
			if (opts.duplicateIgnore)
				_fixCommandName('onBindCommand$', cmd, opts, 'duplicateIgnore');
			if (opts.repeatIgnore)
				_fixCommandName('onBindCommand$', cmd, opts, 'repeatIgnore');
		}
		zAu.send(new zk.Event(wgt, 'onBindCommand$' + cmd, {cmd: cmd, args: args}, zk.copy({toServer: true}, opts)), timeout != undefined ? timeout : 38);
		this._lastcmd = cmd;
		return this;
	},
	/**
	 * Post a global command from the binder.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param Map opts a map of options to zk.Event, if any.
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 */
	globalCommand: function (cmd, args, opts, timeout) {
		var wgt = this.$view;
		if (opts) {
			if (opts.duplicateIgnore)
				_fixCommandName('onBindGlobalCommand$', cmd, opts, 'duplicateIgnore');
			if (opts.repeatIgnore)
				_fixCommandName('onBindGlobalCommand$', cmd, opts, 'repeatIgnore');
		}
		zAu.send(new zk.Event(wgt, 'onBindGlobalCommand$' + cmd, {cmd: cmd, args: args}, zk.copy({toServer: true}, opts)), timeout != undefined ? timeout : 38);
		this._lastcmd = cmd;
		return this;
	},
	$doAfterCommand: function (cmd, args) {
		var ac = this._aftercmd[cmd];
		for (var i = 0, j = ac ? ac.length : 0; i < j; i++)
			ac[i].apply(this, [args]);
	}
}, {
	/**
	 * Post a command to the binder from the give dom element.
	 * @param DOMElement dom the target of the dom element.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param Map opts a map of options to zk.Event, if any.
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 */
	postCommand: function (dom, command, args, opt, timeout) {
		var w = zk.Widget.$(dom);
		if (w) {
			var binder = w.$binder();
			if (binder) {
				binder.command(command, args, opt, timeout);
			}
		}
	},
	/**
	 * Post a global command from the binder of the give dom element.
	 * @param DOMElement dom the target of the dom element.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param int timeout the time (milliseconds) to wait before sending the request.
	 */
	postGlobalCommand: function (dom, command, args, opt, timeout) {
		var w = zk.Widget.$(dom);
		if (w) {
			var binder = w.$binder();
			if (binder) {
				binder.globalCommand(command, args, opt, timeout);
			}
		}
	}
});
})();