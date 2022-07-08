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
	private _running = true;
	private _delay = 0;
	private _repeats = false;
	private _iid?: number;
	private _tid?: number;

	/** Returns whether the timer shall send Event repeatly.
	 * <p>Default: false.
	 * @return boolean
	 */
	public isRepeats(): boolean {
		return this._repeats;
	}

	/** Sets whether the timer shall send Event repeatly.
	 * @param boolean repeats
	 */
	public setRepeats(repeats: boolean, opts?: Record<string, boolean>): this {
		const o = this._repeats;
		this._repeats = repeats;

		if (o !== repeats || (opts && opts.force)) {
			if (this.desktop) this._sync();
		}

		return this;
	}

	/** Returns the delay, the number of milliseconds between
	 * successive action events.
	 * <p>Default: 0 (immediately).
	 * @return int
	 */
	public getDelay(): number {
		return this._delay;
	}

	/** Sets the delay, the number of milliseconds between
	 * successive action events.
	 * @param int delay
	 */
	public setDelay(delay: number, opts?: Record<string, boolean>): this {
		const o = this._delay;
		this._delay = delay;

		if (o !== delay || (opts && opts.force)) {
			if (this.desktop) this._sync();
		}

		return this;
	}

	/** Returns whether this timer is running.
	 * <p>Default: true.
	 * @see #play
	 * @see #stop
	 * @return boolean
	 */
	public isRunning(): boolean {
		return this._running;
	}

	/** Start or stops the timer.
	 * @param boolean running
	 */
	public setRunning(running: boolean, opts?: Record<string, boolean>): this {
		const o = this._running;
		this._running = running;

		if (o !== running || (opts && opts.force)) {
			if (this.desktop) this._sync();
		}

		return this;
	}

	/** Starts the timer.
	 */
	public play(): void {
		this.setRunning(true);
	}

	/** Stops the timer.
	 */
	public stop(): void {
		this.setRunning(false);
	}

	private _sync(): void {
		this._stop();
		this._play();
	}

	private _play(): void {
		if (this._running) {
			var fn = this.proxy(this._tmfn);
			if (this._repeats) {
				this._iid = setInterval(fn, this._delay);
				zAu.onError(this.proxy(this._onErr));
			} else
				this._tid = setTimeout(fn, this._delay);
		}
	}

	private _stop(): void {
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

	private _onErr(req: unknown, errCode: number): boolean {
		if (errCode == 410 || errCode == 404 || errCode == 405)
			this._stop();
		return false;
	}

	private _tmfn(): void {
		if (!this._repeats) this._running = false;
		this.fire('onTimer', null, {
			ignorable: true,
			rtags: {onTimer: 1} // Bug ZK-2720 only timer-keep-alive should reset the timeout
		});
	}

	//super//
	public override redraw(out: Array<string>, skipper?: zk.Skipper | null): void {
		// empty
	}

	protected override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._visible = false; //Bug ZK-1516: no DOM element widget should always return false.
		if (this._running) this._play();
	}

	protected override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		this._stop();
		super.unbind_(skipper, after, keepRod);
	}
}
