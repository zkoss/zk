/* timer.js

{{IS_NOTE
	Purpose:
		Timer
	Description:
		
	History:
		Mon Sep 26 14:04:22     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
//Timer//
zkTimer = {};
zkTimer._intvs = []; //an array of intervals

zk.Timer = Class.create();
zk.Timer.prototype = {
	initialize: function (comp) {
		this.id = comp.id;
		zkau.setMeta(comp, this);
		this.init();
	},
	/** Recognized by zkau.js when 'obsolte' or other severe fatal
	 * error is found. It clears any timeout and interval to avoid
	 * annoying users.
	 */
//	cleanupOnFatal: function (ignorable) {
//		this.cleanup();
//	},
//Tom M. Yeh: 5/21/2007
//Don't cleanup since it may take a while to failover to another server
//Rather, we turn off the error message (by specifying ignorable)

	init: function () {
		var el = $e(this.id);
		if (!el) return;

		this.cleanup(); //stop pending timer/interval

		var repeats = getZKAttr(el, "repeats") == "true";
		var delay = getZKAttr(el, "delay");
		var func = "zkTimer._fire('"+this.id+"')";
		if (repeats)
			zkTimer._intvs.push(this.interval = setInterval(func, delay));
		else
			this.timeout = setTimeout(func, delay);
	},
	cleanup: function ()  {
		if (this.timeout) {
			clearTimeout(this.timeout);
			this.timeout = null;
		}
		if (this.interval) {
			clearInterval(this.interval);
			zkTimer._intvs.remove(this.interval);
			this.interval = null;
		}
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
zkTimer.setAttr = function (cmp, nm, val) {
	if (nm == "z.running") {
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
/** Stops all interval timer.
 * @since 3.0.7
 */
zkTimer.stopAll = function () {
	for (var ivs = zkTimer._intvs; ivs.length;)
		clearInterval(ivs.shift());
};

/** Fires an onTimer event. */
zkTimer._fire = function (uuid) {
	zkau.send({uuid: uuid, cmd: "onTimer", ignorable: true}, 0);
};

zk.override(zkau, "pushXmlResp", zkTimer, function (dtid, req) {
	if (req.getResponseHeader("ZK-Error") == "410") //SC_GONE: session timeout
		zkTimer.stopAll();
	return zkTimer.pushXmlResp(dtid, req);
});
