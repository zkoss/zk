/* Timer.js

	Purpose:
		
	Description:
		
	History:
		Thu Dec 11 17:37:20     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.utl.Timer = zk.$extends(zk.Widget, {
	_running: true,
	_delay: 0,

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
			var fn = this.proxy(this._tmfn);
			if (this._repeats)
				this._iid = setInterval(fn, this._delay);
			else
				this._tid = setTimeout(fn, this._delay);
		}
	},
	_stop: function () {
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
}), { //zk.def
	repeats: _zkf = function () {
		if (this.desktop) this._sync();
	},
	delay: _zkf,
	running: _zkf
});
