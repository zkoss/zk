/* serverpush.ts

	Purpose:
		
	Description:
		
	History:
		Tue Feb 10 18:53:42     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
export class SPush extends zk.Object {
	declare desktop?: zk.Desktop;
	declare min?: number;
	declare max?: number;
	declare factor?: number;
	declare intv?: number;
	start(dt: zk.Desktop, min: number, max: number, factor: number): void {
		this.desktop = dt;
		this.min = min > 0 ? min : 1000;
		this.max = max > 0 ? max : 15000;
		this.factor = factor > 0 ? factor : 5;

		var freq = this.min / 4;
		if (freq < 500) freq = 500; //no less than 500

		this.intv = setInterval(this.proxy(this._do), freq);
	}
	stop(): void {
		clearInterval(this.intv);
		this.intv = undefined;
	}
	_do(): void {
		if (!zAu.processing()) {
			var doNow = !zAu.doneTime;
			if (!doNow) {
				var doneTime = zAu.doneTime || 0,
					delay = (doneTime - (zAu.sentTime || 0)) * this.factor!,
					max = this.max,
					min = this.min;
				if (delay > max!) delay = max!;
				else if (isNaN(delay) || delay < min!) delay = min!;
				doNow = jq.now() > doneTime + delay;
			}

			if (doNow)
				zAu.send(new zk.Event(this.desktop!, 'dummy', undefined, {ignorable: true, rtags: {isDummy: true}}));
		}
	}
}
zk.cpsp.SPush = SPush;

zk.cpsp.start = start;
export function start(dtid: string, min: number, max: number, factor: number): void {
	var dt = zk.Desktop.$(dtid);
	if (dt._cpsp instanceof zk.cpsp.SPush) dt._cpsp.stop();
	(dt._cpsp = new zk.cpsp.SPush()).start(dt, min, max, factor);
}
zk.cpsp.stop = stop;
export function stop(dtid: string): void {
	var dt = zk.Desktop.$(dtid);
	if (dt && dt._cpsp instanceof zk.cpsp.SPush) {
		dt._cpsp.stop();
		dt._cpsp = undefined;
	}
}
