/* Timer.js

	Purpose:
		
	Description:
		
	History:
		Thu Dec 11 17:37:20     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Fires one or more timer after a specified delay.
 *
 * <p>{@link Timer} is a special component that is invisible.
 *
 * <p>Notice that the timer won't fire any event until it is attached
 * to a page.
 */
zul.utl.Timer = zk.$extends(zk.Widget, {
	_running: true,
	_delay: 0,

	$define: {
    	/** Returns whether the timer shall send Event repeatly.
    	 * <p>Default: false.
    	 * @return boolean
    	 */
    	/** Sets whether the timer shall send Event repeatly.
    	 * @param boolean repeats
    	 */
		repeats: _zkf = function () {
			if (this.desktop) this._sync();
		},
		/** Returns the delay, the number of milliseconds between
		 * successive action events.
		 * <p>Default: 0 (immediately).
		 * @return int
		 */
		/** Sets the delay, the number of milliseconds between
		 * successive action events.
		 * @param int delay
		 */
		delay: _zkf,
		/** Returns whether this timer is running.
		 * <p>Default: true.
		 * @see #play
		 * @see #stop
		 * @return boolean
		 */
		/** Start or stops the timer.
		 * @param boolean running
		 */
		running: _zkf
	},
	/** Starts the timer.
	 */
	play: function () {
		this.setRunning(true);
	},
	/** Stops the timer.
	 */
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
		if (!this._repeats) this._running = false;
		this.fire('onTimer', null, {ignorable: true});
	},

	//super//
	redraw: function () {
	},
	bind_: function () {
		this.$supers(zul.utl.Timer, 'bind_', arguments);
		if (this._running) this._play();
	},
	unbind_: function () {
		this._stop();
		this.$supers(zul.utl.Timer, 'unbind_', arguments);
	}
});
