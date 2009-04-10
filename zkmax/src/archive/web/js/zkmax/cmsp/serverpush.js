/* serverpush.js

	Purpose:
		
	Description:
		
	History:
		Wed Feb 11 18:26:30     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkmax.cmsp.SPush = zk.$extends(zk.Object, {
	_sid: (zUtl.now() % 999) + 1, //1-999 (random init: bug 2691017)

	start: function (dt) {
		++zkmax.cmsp._nStart;
		this.desktop = dt;
		this._asend(50);
	},
	stop: function () {
		--zkmax.cmsp._nStart;
		this._stopped = true;
		var req = this._req;
		if (req) {
			this._req = null;
			try {
				if(typeof req.abort == "function") req.abort();
			} catch (e2) {
			}
		}
	},
	_send: function () {
		var req = this._req = zUtl.newAjax(),
			dt = this.desktop;
		zAu.sentTime = zUtl.now();
		try {
			req.onreadystatechange = this.proxy(this._onRespReady, '_xonRespReady');
			req.open("POST", zAu.comURI("/comet?dtid="+dt.id, dt, false), true);
			req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			req.setRequestHeader("ZK-SID", this._sid);
			req.send(null);
		} catch (e) {
			this._req = null;
			this._asend(5000); //5 sec
		}
	},
	_onRespReady: function () {
		var req = this._req, timeout = 2000;
		try {
			if (req && req.readyState == 4) {
				this._req = null;

				switch (req.getResponseHeader("ZK-Error")) {
				case "404": //SC_NOT_FOUND: server restart
				case "410": //SC_GONE: session timeout
					this.stop();
					return;
				}

				if (req.status == 200) {
					var sid = req.getResponseHeader("ZK-SID");
					if (!sid || sid == this._sid) {
						if (zAu.pushCmds(this.desktop, req)) {
							timeout = 50;
							if (sid && ++this._sid > 999) this._sid = 1;
							//both pushCmds and doCmds might ex
						}
						zAu.doCmds();
					}
				}
				this._asend(timeout);
			}
		} catch (e) {
			this._asend(2000); //2 sec
		}
	},
	_asend: function (timeout) {
		if (!this._stopped)
			setTimeout(this.proxy(this._send, "_pxsend"),
				Math.max(zAu.sendNow(this.desktop) ? 900: 0, timeout));
				//At least 900 so zAu.sendNow has time to send any pending status back
	}
});

zk.copy(zkmax.cmsp, {
	_nStart: 0,

	start: function (dtid) {
		var dt = zk.Desktop.$(dtid);
		if (dt._cmsp) dt._cmsp.stop();
		(dt._cmsp = new zkmax.cmsp.SPush()).start(dt);
	},
	stop: function (dtid) {
		var dt = zk.Desktop.$(dtid);
		if (dt && dt._cmsp) {
			dt._cmsp.stop();
			dt._cmsp = null;
		}
	}
});

zkmax.cmsp._auSIE = zAu.shallIgnoreESC;
zAu.shallIgnoreESC = function () {
	return zkmax.cmsp._auSIE() || zkmax.cmsp._nStart;
};
