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
zkCpsp._infs = {}; //interval timers

zkCpsp.start = function (dtid, freq) {
	var inf = zkCpsp._infs[dtid];
	if (inf) {
		clearInterval(inf.timer);
	} else {
		inf = zkCpsp._infs[dtid] = {};
	}

	inf.freq = freq ? freq: 5000;
	inf.timer = setInterval("zkCpsp._do('"+dtid+"')", inf.freq);
zk.debug("start "+dtid);
};
zkCpsp.stop = function (dtid) {
	var inf = zkCpsp._infs[dtid];
	if (inf) {
		clearInterval(inf.timer);
		delete zkCpsp._infs[dtid];
	}
zk.debug("stop "+dtid);
};

zkCpsp._do = function (dtid) {
	zkau.send({dtid: dtid, cmd: "dummy", data: null, ignorable: true});
};
