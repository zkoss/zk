/* cmsp.js

{{IS_NOTE
	Purpose:
		Comet-based Server Push
	Description:
		The comet algorithm is long-polling
	History:
		Tue May  6 10:26:29     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zkCmsp = {};
zkCmsp._reqs = {};
zkCmsp._start = {};
zkCmsp._nStart = 0;
zkCmsp._sid = 1; //1-999

zk.override(zkau, "ignoreESC", zkCmsp,
	function () {
		return zkCmsp.ignoreESC() || zkCmsp._nStart;
	});

zkCmsp.start = function (dtid) {
	++zkCmsp._nStart;
	zkCmsp._start[dtid] = true;
	zkCmsp._asend(dtid, 50);
};

zkCmsp.stop = function (dtid) {
	if (zkCmsp._start[dtid]) {
		--zkCmsp._nStart;
		zkCmsp._start[dtid] = false;
	}
	var req = zkCmsp._reqs[dtid];
	if (req) {
		zkCmsp._reqs[dtid] = null;
		try {
			if(typeof req.abort == "function") req.abort();
		} catch (e2) {
		}
	}
};
zkCmsp.stopAll = function () {
	for (var dtid in zkCmsp._start)
		zkCmsp.stop(dtid);
};

zkCmsp._send = function (dtid) {
	var req = zkCmsp._reqs[dtid] = zkau.ajaxRequest();
	zkau.sentTime = $now();
	try {
		req.onreadystatechange = zkCmsp._onRespReady;
		req.open("POST", zk.getUpdateURI("/comet?dtid="+dtid, false, null, dtid), true);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		req.setRequestHeader("ZK-SID", zkCmsp._sid);
		req.send(null);
	} catch (e) {
		zkCmsp._reqs[dtid] = null;
		zkCmsp._asend(dtid, 5000); //5 sec
	}
};
zkCmsp._asend = function (dtid, timeout) {
	if (zkCmsp._start[dtid])
		setTimeout(function () {zkCmsp._send(dtid);},
			Math.max(zkau.sendNow(dtid) ? 900:0, timeout));
			//At least 900 so sendNow has time to send any pending status back
};

zkCmsp._onRespReady = function () {
	try {
		for (var dtid in zkCmsp._reqs) {
			var req = zkCmsp._reqs[dtid], timeout = 2000;
			try {
				if (req && req.readyState == 4) {
					zkCmsp._reqs[dtid] = null;

					switch (req.getResponseHeader("ZK-Error")) {
					case "404": //SC_NOT_FOUND: server restart
					case "410": //SC_GONE: session timeout
						zkCmsp.stop(dtid);
						return;
					}
					if (req.status == 200) {
						var sid = req.getResponseHeader("ZK-SID");
						if (!sid || sid == zkCmsp._sid) {
							if (zkau.pushXmlResp(dtid, req)) {
								timeout = 50;
								if (sid && ++zkCmsp._sid > 999) zkCmsp._sid = 1;
								//both pushXmlResp and doCmds might ex
							}
							zkau.doCmds();
						}
					}
					zkCmsp._asend(dtid, timeout);
				}
			} catch (e) {
				zkCmsp._asend(dtid, 2000); //2 sec
			}
		}
	} catch (e) {
		//FF: complain zkCmsp not found if ESC. reason: unknown
	}
};
