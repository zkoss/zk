/* zswipe.ts

	Purpose:
		Support swipe event for tablet
	Description:

	History:
		Tue, July 3, 2012  12:24:28 PM, Created by vincentjian

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
let hastouch = 'ontouchstart' in window,
	startEvt = hastouch ? 'touchstart' : 'mousedown',
	moveEvt = hastouch ? 'touchmove' : 'mousemove',
	endEvt = hastouch ? 'touchend' : 'mouseup',
	start: {coords: zk.Offset; time: number} | undefined,
	stop: {coords: zk.Offset; time: number} | undefined;

function _doEvt(wevt: zk.Event): void {
	var wgt = wevt.target;
	if (wgt && !wgt.$weave) {
		var en = wevt.name;
		if (!wevt.stopped)
			(wgt['do' + en.substring(2) + '_'] as CallableFunction)(wevt);
//	no need to stop the event for domtouch.js
//			if (wevt.domStopped)
//				wevt.domEvent.stop();
	}
}
export interface SwipeOptions {
	scrollThreshold: number;
	duration: number;
	minDisplacement: number;
	maxDisplacement: number;
}
export interface SwipeEventOptions extends zk.EventOptions {
	start: {coords: zk.Offset; time: number};
	stop: {coords: zk.Offset; time: number};
	dir: 'left' | 'right' | 'up' | 'down';
}
/**
 * A swipe object used to make a DOM element swipe-able.
 */
export class Swipe extends zk.Object {
	declare widget?: zk.Widget;
	declare node?: HTMLElement;
	declare opts?: SwipeOptions;
	/**
	 * The Constructor.
	 * @param widget - the object for swipe.
	 * It can be anything, but it is usually a widget ({@link zk.Widget}).
	 * @param node - the DOM element that is made to be swipe-able.
	 * If omitted and widget is a widget, {@link zk.Widget.$n} is assumed.
	 * @param opts - options. Allowed options:
	 * <ul>
	 * <li>int scrollThreshold: swipe displacement(pixel). When larger than threshold, prevent page scrolling. Default: 5.</li>
	 * <li>int duration: swipe start to end time duration(millisecond). When larger than time duration, it is not a swipe action. Default: 500.</li>
	 * <li>int minDisplacement: the minimum swipe displacement(pixel). When smaller than minimum displacement, it is not a swipe action. Default: 30.</li>
	 * <li>int maxDisplacement: the maximum swipe displacement(pixel). When larger than maximum displacement, it is not a swipe action. Default: 75.</li>
	 * </ul>
	 */
	constructor(widget: zk.Widget, node?: HTMLElement, opts?: Partial<SwipeOptions>) {
		super();
		this.widget = widget;
		// eslint-disable-next-line @typescript-eslint/dot-notation
		this.node = node ? jq(node, zk)[0] as HTMLElement | undefined : widget['node'] as HTMLElement | undefined || (widget.$n ? widget.$n() : undefined);
		if (!this.node)
			throw 'Handle required for ' + widget;

		this.opts = zk.$default(opts, {
			scrollThreshold: 5,
			duration: 500,
			minDisplacement: 30,
			maxDisplacement: 75
		});

		jq(this.node).on(startEvt, this.proxy(this._swipeStart));
	}

	/**
	 * Destroys this swipe-able object.
	 * This method must be called to clean up, if you don't want to associate the swipe-able feature to a DOM element.
	 */
	destroy(node: HTMLElement): void {
		jq(node).off(startEvt, this.proxy(this._swipeStart));
		this.widget = this.node = this.opts = undefined;
	}

	/** @internal */
	_swipeStart(devt: JQuery.TriggeredEvent): void {
		var evt = devt.originalEvent as TouchEvent,
			data = evt.touches ? evt.touches[0] : (evt as unknown as MouseEvent);

		start = {
			time: evt.timeStamp || Date.now(),
			coords: [data.pageX, data.pageY]
		};
		jq(this.node)
			.on(moveEvt, this.proxy(this._swipeMove) as unknown as false)
			.one(endEvt, this.proxy(this._swipeEnd) as unknown as false);
	}

	/** @internal */
	_swipeMove(devt: JQuery.TriggeredEvent): void {
		if (!start) return;
		var evt = devt.originalEvent as TouchEvent,
			data = evt.touches ? evt.touches[0] : (evt as unknown as MouseEvent);
		evt.stopPropagation();

		// eslint-disable-next-line no-global-assign
		stop = {
			time: evt.timeStamp || Date.now(),
			coords: [data.pageX, data.pageY]
		};

		// prevent scrolling when displacement is larger than scrollThreshold
		var dispX = Math.abs(start.coords[0] - stop.coords[0]),
			dispY = Math.abs(start.coords[1] - stop.coords[1]),
			scrollThreshold = this.opts!.scrollThreshold;
		if (dispX > scrollThreshold || dispY > scrollThreshold)
			evt.preventDefault();
	}

	/** @internal */
	_swipeEnd(devt: JQuery.TriggeredEvent): void {
		jq(this.node).off(moveEvt, this.proxy(this._swipeMove));
		if (start && stop && this.opts) {
			var dispX: number | undefined, dispY: number | undefined,
				dispT = stop.time - start.time, dir: 'left' | 'right' | 'up' | 'down' | undefined;

			if (dispT < this.opts.duration) {
				var deltaX = start.coords[0] - stop.coords[0],
					deltaY = start.coords[1] - stop.coords[1],
					min = this.opts.minDisplacement,
					max = this.opts.maxDisplacement;

				dispX = Math.abs(deltaX);
				dispY = Math.abs(deltaY);

				if (dispX > min && dispY < max)
					dir = deltaX > 0 ? 'left' : 'right';
				else if (dispY > min && dispX < max)
					dir = deltaY > 0 ? 'up' : 'down';
			}

			var wgt = this.widget;
			if (wgt)
				_doEvt(new zk.Event(wgt, 'onSwipe',
					{dispX: dispX, dispY: dispY, dispT: dispT, dir: dir},
					{start: start, stop: stop, dir: dir}, devt));
		}
		// eslint-disable-next-line no-global-assign
		start = stop = undefined;
	}
}
zk.Swipe = Swipe;