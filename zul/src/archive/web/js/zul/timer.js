/* timer.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 26 14:04:22     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
zk.load("zul.zul");

//Timer//
zkTimer = {};

zk.Timer = Class.create();
zk.Timer.prototype = {
	initialize: function (comp) {
		this.id = comp.id;
		zkau.setMeta(comp, this);
		this.init();
	},
	init: function () {
		var el = $e(this.id);
		if (!el) return;

		this.cleanup(); //stop pending timer/interval

		var repeats = getZKAttr(el, "repeats") == "true";
		var delay = getZKAttr(el, "delay");
		var func = "zkTimer._fire('"+this.id+"')";
		if (repeats)
			this.interval = window.setInterval(func, delay);
		else
			this.timeout = window.setTimeout(func, delay);
	},
	cleanup: function ()  {
		if (this.timeout) {
			window.clearTimeout(this.timeout);
			this.timeout = null;
		}
		if (this.interval) {
			window.clearInterval(this.interval);
			this.interval = null;
		}
	},
	/** Recognized by zkau.js when 'obsolte' or other severe fatal
	 * error is found. It clears any timeout and interval to avoid
	 * annoying users.
	 */
	cleanupOnFatal: function () {
		this.cleanup();
	}
};

/** Init (and re-init) a timer. */
zkTimer.init = function (cmp) {
	if (getZKAttr(cmp, "running") != "false") {
		var meta = zkau.getMeta(cmp);
		if (meta) meta.init();
		else new zk.Timer(cmp);
	}
};
zkTimer.cleanup = zkau.cleanupMeta;
zkTimer.setAttr = function (cmp, nm, val) {
	if (nm == "z:running") {
		zkau.setAttr(cmp, nm, val);
		if (val == "true") zkTimer.init(cmp);
		else {
			var meta = zkau.getMeta(cmp);
			if (meta) meta.cleanup();
		}
		return true;
	}
	return false;
};

/** Fires an onTimer event. */
zkTimer._fire = function (uuid) {
	zkau.send({uuid: uuid, cmd: "onTimer", data: null, implicit: true}, 0);
};
