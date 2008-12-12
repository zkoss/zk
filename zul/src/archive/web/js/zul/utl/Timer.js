/* Timer.js

	Purpose:
		
	Description:
		
	History:
		Thu Dec 11 17:37:20     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.utl.Timer = zk.$extends(zk.Widget, {
	_running: true,
	_delay: 0,

	isRepeats: function () {
		return this._repeats;
	},
	setRepeats: function (repeats) {
		if (this._repeats != repeats) {
			this._repeats = repeats;
			if (this.desktop) this._sync();
		}
	},
	getDelay: function () {
		return this._delay;
	},
	setDelay: function (delay) {
		if (this._delay != delay) {
			this._delay = delay;
			if (this.desktop) this._sync();
		}
	},
	isRunning: function () {
		return this._running;
	},
	setRunning: function (running) {
		if (this._running != running) {
			this._running = running;
			if (this.desktop) this._sync();
		}
	},
	play: function () {
		this.setRunning(true);
	},
	stop: function () {
		this.setRunning(false);
	},

	_sync: function () {
		this._stop();
		this._play();
	},
	_play: function () {
		if (this._running) {
			this._tmfnpx = this.proxy(this._tmfn);
			if (this._repeats)
				this._iid = setInterval(this._tmfnpx, this._delay);
			else
				this._tid = setTimeout(this._tmfnpx, this._delay);
		}
	},
	_stop: function () {
		this._tmfnpx = null;
		var id = this._iid;
		if (id) {
			this._iid = null;
			clearInterval(id)
		}
		id = this._tid;
		if (id) {
			this._tid = null;
			clearTimeout(id);
		}
	},
	_tmfn: function () {
		this.fire('onTimer', null, {ignorable: true});
	},

	//super//
	redraw: function () {
		return '';
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this._running) this._play();
	},
	unbind_: function () {
		this._stop();
		this.$supers('unbind_', arguments);
	}
});
