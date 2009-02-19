/* serverpush.js

	Purpose:
		
	Description:
		
	History:
		Tue Feb 10 18:53:42     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkex.cpsp.SPush = zk.$extends(zk.Object, {
	start: function (dt, min, max, factor) {
		this.desktop = dt;
		this.min = min > 0 ? min: 1000;
		this.max = max > 0 ? max: 15000;
		this.factor = factor > 0 ? factor: 5;

		var freq = this.min / 4;
		if (freq < 500) freq = 500; //no less than 500

		this.intv = setInterval(this.proxy(this._do), freq);
	},
	stop: function () {
		clearInterval(this.intv);
		this.intv = null;
	},
	_do: function () {
		if (!zAu.processing()) {
			var doNow = !zAu.doneTime;
			if (!doNow) {
				var delay = (zAu.doneTime - zAu.sentTime) * this.factor,
					max = this.max,
					min = this.min;
				if (delay > max) delay = max;
				else if (isNaN(delay) || delay < min) delay = min;
				doNow = zUtl.now() > zAu.doneTime + delay;
			}

			if (doNow)
				zAu.send(new zk.Event(this.desktop, "dummy", null, {ignorable: true}));
		}
	}
});

zkex.cpsp.start = function (dtid, min, max, factor) {
	var dt = zk.Desktop.$(dtid);
	if (dt._cpsp) dt._cpsp.stop();
	(dt._cpsp = new zkex.cpsp.SPush()).start(dt, min, max, factor);
};
zkex.cpsp.stop = function (dtid) {
	var dt = zk.Desktop.$(dtid);
	if (dt && dt._cpsp) {
		dt._cpsp.stop();
		dt._cpsp = null;
	}
};
