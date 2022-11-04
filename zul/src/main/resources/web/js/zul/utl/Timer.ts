/* Timer.ts

	Purpose:

	Description:

	History:
		Thu Dec 11 17:37:20     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
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
@zk.WrapClass('zul.utl.Timer')
export class Timer extends zk.Widget {
	/** @internal */
	_running = true;
	/** @internal */
	_delay = 0;
	/** @internal */
	_repeats = false;
	/** @internal */
	_iid?: number;
	/** @internal */
	_tid?: number;

	/**
	 * @returns whether the timer shall send Event repeatly.
	 * @defaultValue `false`.
	 */
	isRepeats(): boolean {
		return this._repeats;
	}

	/**
	 * Sets whether the timer shall send Event repeatly.
	 */
	setRepeats(repeats: boolean, opts?: Record<string, boolean>): this {
		const o = this._repeats;
		this._repeats = repeats;

		if (o !== repeats || opts?.force) {
			if (this.desktop) this._sync();
		}

		return this;
	}

	/**
	 * @returns the delay, the number of milliseconds between
	 * successive action events.
	 * @defaultValue `0` (immediately).
	 */
	getDelay(): number {
		return this._delay;
	}

	/**
	 * Sets the delay, the number of milliseconds between
	 * successive action events.
	 */
	setDelay(delay: number, opts?: Record<string, boolean>): this {
		const o = this._delay;
		this._delay = delay;

		if (o !== delay || opts?.force) {
			if (this.desktop) this._sync();
		}

		return this;
	}

	/**
	 * @returns whether this timer is running.
	 * @defaultValue `true`.
	 * @see {@link play}
	 * @see {@link stop}
	 */
	isRunning(): boolean {
		return this._running;
	}

	/**
	 * Start or stops the timer.
	 */
	setRunning(running: boolean, opts?: Record<string, boolean>): this {
		const o = this._running;
		this._running = running;

		if (o !== running || opts?.force) {
			if (this.desktop) this._sync();
		}

		return this;
	}

	/**
	 * Starts the timer.
	 */
	play(): void {
		this.setRunning(true);
	}

	/**
	 * Stops the timer.
	 */
	stop(): void {
		this.setRunning(false);
	}

	/** @internal */
	_sync(): void {
		this._stop();
		this._play();
	}

	/** @internal */
	_play(): void {
		if (this._running) {
			var fn = this.proxy(this._tmfn);
			if (this._repeats) {
				this._iid = setInterval(fn, this._delay);
				zAu.onError(this.proxy(this._onErr));
			} else
				this._tid = setTimeout(fn, this._delay);
		}
	}

	/** @internal */
	_stop(): void {
		var id = this._iid;
		if (id) {
			delete this._iid;
			clearInterval(id);
		}
		id = this._tid;
		if (id) {
			delete this._tid;
			clearTimeout(id);
		}
		zAu.unError(this._onErr.bind(this));
	}

	/** @internal */
	_onErr(req: unknown, errCode: number | string): boolean {
		if (errCode == 410 || errCode == 404 || errCode == 405)
			this._stop();
		return false;
	}

	/** @internal */
	_tmfn(): void {
		if (!this._repeats) this._running = false;
		this.fire('onTimer', undefined, {
			ignorable: true,
			rtags: { onTimer: 1 } // Bug ZK-2720 only timer-keep-alive should reset the timeout
		});
	}
	
	override redraw(out: string[], skipper?: zk.Skipper): void {
		// empty
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._visible = false; //Bug ZK-1516: no DOM element widget should always return false.
		if (this._running) this._play();
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		this._stop();
		super.unbind_(skipper, after, keepRod);
	}
}