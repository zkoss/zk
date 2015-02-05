/* Binder.js

	Purpose:
		
	Description:
		
	History:
		Wed, Jan 07, 2015 12:08:17 PM, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.
*/
(function () {
	var _WidgetX = {};

zk.override(zk.Widget.prototype, _WidgetX, {
	$binder: function () {
		var w = this;
		for (; w ; w = w.parent) {
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
	$afterCommand: function (command) {
		var binder = this.$binder();
		if (binder)
			binder.$doAfterCommand(command);
	},
	unbind_: function () {
		if (this._$binder) {
			this._$binder.destroy();
			this._$binder = null;
		}
		_WidgetX.unbind_.apply(this, arguments);
	}
});
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
	},
	/**
	 * Registers a callback after some command executed.
	 * @param String command the name of the command
	 * @param Function func the function to execute
	 */
	after: function (cmd, fn) {
		var ac = this._aftercmd[cmd];
		if (!ac) this._aftercmd[cmd] = [fn];
		else {
			ac.push(fn);
		}
		return this;
	},
	/**
	 * Unregisters a callback after some command executed.
	 * @param String command the name of the command
	 * @param Function func the function to execute
	 */
	unAfter: function (cmd, fn) {
		var ac = this._aftercmd[cmd];
		for (var j = ac ? ac.length: 0; j--;) {
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
		this.$view = null;
		this.$currentTarget = null;
	},
	/**
	 * Post a command to the binder
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 */
	command: function (cmd, args) {
		this.$command0(cmd, args);
	},
	/**
	 * Post a command to the binder with a timeout if any.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param int timeout to delay the post.
	 */
	$command0: function (cmd, args, timeout) {
		var wgt = this.$view;
		if (timeout) {
			setTimeout(function () {
				zAu.send(new zk.Event(wgt, "onBindCommand", {cmd: cmd, args: args}, {toServer:true}));
			}, timeout); // make command at the end of this request
		} else {
			zAu.send(new zk.Event(wgt, "onBindCommand", {cmd: cmd, args: args}, {toServer:true}));
		}
	},
	/**
	 * Post a global command from the binder.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 */
	globalCommand: function (cmd, args) {
		this.$globalCommand0(cmd, args);
	},
	/**
	 * Post a global command from the binder with a timeout if any.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 * @param int timeout to delay the post.
	 */
	$globalCommand0: function (cmd, args, timeout) {
		var wgt = this.$view;
		if (timeout) {
			setTimeout(function () {
				zAu.send(new zk.Event(wgt, "onBindGlobalCommand", {cmd: cmd, args: args}, {toServer:true}));
			}, timeout); // make command at the end of this request
		} else {
			zAu.send(new zk.Event(wgt, "onBindGlobalCommand", {cmd: cmd, args: args}, {toServer:true}));
		}
	},
	$doAfterCommand: function (cmd) {
		var ac = this._aftercmd[cmd];
		for (var i = 0, j = ac ? ac.length: 0; i < j; i++) {
			ac[i].apply(this);
		}
	}
}, {
	/**
	 * Post a command to the binder from the give dom element.
	 * @param DOMElement dom the target of the dom element.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 */
	postCommand: function (dom, command, args) {
		var w = zk.Widget.$(dom);
		if (w) {
			var binder = w.$binder();
			if (binder) {
				binder.command(command, args);
			}
		}
	},
	/**
	 * Post a global command from the binder of the give dom element.
	 * @param DOMElement dom the target of the dom element.
	 * @param String command the name of the command
	 * @param Map args the arguments for this command. (the value should be json type)
	 */
	postGlobalCommand: function (dom, command, args) {
		var w = zk.Widget.$(dom);
		if (w) {
			var binder = w.$binder();
			if (binder) {
				binder.globalCommand(command, args);
			}
		}
	}
});
})();