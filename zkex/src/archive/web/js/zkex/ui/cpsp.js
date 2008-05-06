/* cpsp.js

{{IS_NOTE
	Purpose:
		Client-Polling-based Server Push
	Description:
		
	History:
		Mon Aug  6 14:26:09     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zkCpsp = {};
zkCpsp._infos = {}; //interval timers

zkCpsp.start = function (dtid, min, max, factor) {
	var info = zkCpsp._infos[dtid];
	if (info) {
		clearInterval(info.timer);
	} else {
		info = zkCpsp._infos[dtid] = {};
	}

	var defInfo = zkau.getSPushInfo(dtid);
	if (defInfo) {
		if (min == null) min = defInfo.min;
		if (max == null) max = defInfo.max;
		if (factor == null) factor = defInfo.factor;
	}
	if (min != null) info.min = min;
	if (max != null) info.max = max;
	if (factor != null) info.factor = factor;

	var freq = zkCpsp._min(info) / 4;
	if (freq < 500) freq = 500; //no less than 500

	info.timer = setInterval("zkCpsp._do('"+dtid+"')", freq);
};
zkCpsp.stop = function (dtid) {
	var info = zkCpsp._infos[dtid];
	if (info) {
		clearInterval(info.timer);
		delete zkCpsp._infos[dtid];
	}
};

zkCpsp._min = function (info) {
	return info.min > 0 ? info.min: 1000;
};
zkCpsp._max = function (info) {
	return info.max > 0 ? info.max: 15000;
};

zkCpsp._do = function (dtid) {
	if (!zkau.processing()) {
		var doNow = !zkau.doneTime;
		if (!doNow) {
			var info = zkCpsp._infos[dtid],
				delay = (zkau.doneTime - zkau.sentTime)
					* (info.factor > 0 ? info.factor: 5),
				max = zkCpsp._max(info),
				min = zkCpsp._min(info);
			if (delay > max) delay = max;
			if (delay < min) delay = min;
			doNow = $now() > zkau.doneTime + delay;
		}

		if (doNow)
			zkau.send({dtid: dtid, cmd: "dummy", data: null, ignorable: true});
	}
};

zkCpsp._setInfo = zkau.setSPushInfo;
zkau.setSPushInfo = function (dtid, info) {
	var i = zkCpsp._infos[dtid];
	if (i)
		zkCpsp.start(dtid,
			info.min || i.min, info.max || i.max, info.factor || i.factor);
	zkCpsp._setInfo(dtid, info);
};
