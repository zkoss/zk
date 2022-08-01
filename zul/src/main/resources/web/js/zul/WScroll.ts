/* WScroll.ts

	Purpose:

	Description:
		A wave scrollbar control
	History:
		Wed, Feb 22, 2012  4:16:53 PM, Created by jumperchen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

*/
function easing(x: number, t: number, b: number, c: number, d: number): number {
	return -c * ((t = t / d - 1) * t * t * t - 1) + b; // easeOutQuart
}
function snap(dg: zk.Draggable, pointer: zk.Offset): zk.Offset {
	var x = pointer[0],
		y = pointer[1];
	if (dg._isVer) {
		var move = y;

		if (move - dg._start < 0) {
			move = pointer[1] = dg._start;
		} else if (move > dg._end) {
			move = pointer[1] = dg._end;
		}

		if (dg._lastPos) { // optimized
			if (Math.abs(dg._lastPos - move) < 3)
				return pointer;
		}

		dg._lastPos = move;
	} else {
		var move = x;

		if (move - dg._start < 0) {
			move = pointer[0] = dg._start;
		} else if (move > dg._end) {
			move = pointer[0] = dg._end;
		}

		if (dg._lastPos) { // optimized
			if (Math.abs(dg._lastPos - move) < 3)
				return pointer;
		}

		dg._lastPos = move;
	}
	return pointer;
}

function starteffect(dg: zk.Draggable): void {
	var ctrl = dg.control as unknown as zul.WScroll,
		opts = ctrl.opts;

	dg._steps = opts.startStep;
	dg._endStep = opts.endStep - opts.viewport;
	dg._scale = ctrl._scale;
	dg._epos = ctrl.epos;
	dg._lastPos = dg._start;
	if (ctrl._isVer) {
		dg._isVer = true;
		dg._start = opts.startPosition;
		if (zk(ctrl.eend).isVisible()) {
			dg._end = ctrl.eend.offsetTop + Math.ceil(dg.handle!.offsetHeight / 2);
			if (dg._end > opts.viewportSize + dg._start)
				dg._end = opts.viewportSize + dg._start;
		} else {
			dg._end = opts.viewportSize + dg._start;
		}
		dg._end -= dg.node!.offsetHeight - ctrl._gap;
	} else {
		dg._isVer = false;
		dg._start = opts.startPosition;
		if (zk(ctrl.eend).isVisible()) {
			dg._end = ctrl.eend.offsetLeft + Math.ceil(dg.handle!.offsetWidth / 2);
			if (dg._end > opts.viewportSize + dg._start)
				dg._end = opts.viewportSize + dg._start;
		} else {
			dg._end = opts.viewportSize + dg._start;
		}
		dg._end -= dg.node!.offsetWidth - ctrl._gap;
	}
	jq(dg._epos).show().delay(200).fadeIn(500);

	if (dg._timer) {
		clearTimeout(dg._timer);
	}
	var lastP,
		lastS: number[] = [],
		timeout = 30,
		duration = timeout * 20,
		t = 10,
		running = function (orient: 'top' | 'left'): void {
			var norient = zk.parseFloat(dg.node!.style[orient]),
				diff = norient - zk.parseFloat(dg._epos!.style[orient]);
			if (lastP == norient) {
				lastS.push(dg._lastSteps);
				if (lastS.length > 4 && lastS.shift() == dg._lastSteps) {
					lastS[0] = dg._lastSteps;
					clearTimeout(dg._timer);
					dg._timer = setTimeout(function () {running(orient);}, 100);
					return;
				}
			} else t = 10; // reset

			lastP = norient;

			var down = diff > 0,
				total = down ? Math.max(0, diff / dg._scale) : Math.min(0, diff / dg._scale),
				step = Math.round(zul.WScroll.easing(t / duration, t, 0, total, duration));

			if (down) {
				if (total > 1)
					step = Math.max(1, step);
			} else {
				if (-total > 1)
					step = Math.min(-1, step);
			}

			if (diff == 0 && step == 0) {
				if (norient == dg._start)
					step = -dg._steps;
				else if (norient == dg._end)
					step = dg._endStep - dg._steps;
			}

			dg._steps += step;
			if (down) {
				if (dg._steps > dg._endStep)
					dg._steps = dg._endStep;
			} else {
				if (dg._steps < 0)
					dg._steps = 0;
			}
			dg._epos!.style[orient] = dg._start + (dg._scale * dg._steps) + 'px';
			t += timeout;
			if (dg._lastSteps != dg._steps) {
				dg._lastSteps = dg._steps;
				var func = orient == 'top' ? ctrl.opts.onScrollY : ctrl.opts.onScrollX;
				if (typeof func == 'function') {
					func.call((dg.control as unknown as zul.WScroll).widget, dg._steps + ctrl.opts.offset);
				}
			}
			clearTimeout(dg._timer);
			dg._timer = setTimeout(function () {running(orient);}, timeout);
		};
	dg._timer = setTimeout(function () {running((dg._isVer ? 'top' : 'left'));}, 50);
}
function endeffect(dg: zk.Draggable): void {
	var ctrl = dg.control as unknown as zul.WScroll;
	if (dg._timer) {
		clearTimeout(dg._timer);
	}
	var move: number, end: number;
	if (dg._isVer) {
		move = zk.parseInt(dg._epos!.style.top);
		end = dg._end;
		if (move > end)
			move = end;
		jq(dg.node).animate({top: move + 'px'}, 400, 'swing');

	} else {
		move = zk.parseInt(dg._epos!.style.left);
		end = dg._end;
		if (move > end)
			move = end;
		jq(dg.node).animate({left: move + 'px'}, 400, 'swing');

	}
	ctrl.opts.startStep = dg._steps;
	ctrl._syncButtonStatus();
	var $jq = jq(dg._epos),
		old = $jq.css('opacity'); // fix old IE version
	jq(dg._epos).delay(300).fadeOut(500).css('opacity', old);
}
function ignoredrag(dg: zk.Draggable, p: zk.Offset, evt: zk.Event): boolean {
	return (dg.control as unknown as WScroll).edragBody != evt.domTarget;
}

export interface WScrollOptions {
	orient: 'vertical' | 'horizontal';
	startPosition: number;
	startStep: number;
	offset: number;

	anchor?: HTMLElement | null; // eslint-disable-line zk/noNull
	viewport: number;
	endStep: number;
	viewportSize: number;
	syncSize: boolean;
	onScrollX?(this: zk.Widget, delta: number): void;
	onScrollY?(this: zk.Widget, delta: number): void;
}

/**
 * A wave Scrollbar used to scroll the specific content and provides four controls
 * to navigate the content, such as Home/Previous/Next/End, and also supports the
 * mousewheel control.
 */
@zk.WrapClass('zul.WScroll')
export class WScroll extends zk.Object {
	/** The control object for this scrolling that user can scroll the whole content
	 * @type DOMElement
	 */
	//control: null,
	/** The widget object that owns the control object.
	 * @type zk.Widget
	 */
	//widget: null,
	/**
	 * The opts of this scrollbar controls.
	 * <h4>startPosition</h4>
	 * int startPosition
	 * <p>Specifies the start position according to the scrolling area, like offset top for
	 * the vertical scrolling and offset left for the horizental scrolling.
	 *
	 * <h4>startStep</h4>
	 * int startStep
	 * <p>Specifies the start step for the scrolling.
	 * <p>Note: it cannot be negative.
	 *
	 * <h4>endStep</h4>
	 * int endStep
	 * <p>Specifies how many steps for the scrolling.
	 * <p>Note: it cannot be negative.
	 *
	 * <h4>viewport</h4>
	 * int viewport
	 * <p>Specifies how many steps will show in the viewport.
	 * <p>Note: it cannot be negative.
	 *
	 * <h4>viewportSize</h4>
	 * int viewportSize
	 * <p>Specifies how many pixels for the viewport size, like offsetHeight for
	 * vertical scrolling and offsetWidth for horizental scrolling.
	 * <p>Note: it cannot be negative.
	 *
	 * <h4>orient</h4>
	 * String orient
	 * <p>Specifies either 'vertical' or 'horizontal' to indicate that it can be
	 * scrolled only in the vertical or horizontal direction.
	 * <p>Default: 'horizontal'
	 *
	 * <h4>anchor</h4>
	 * DOMElement anchor
	 * <p>Specifies the anchor that indicates the scrollbar will be its child node.
	 * <p>Default: the parent node of the control.
	 *
	 * <h4>syncSize</h4>
	 * boolean syncSize
	 * <p>Specifies whether to sync the scrolling area size at initial phase.
	 * <p>Default: true.
	 *
	 * <h4>onScrollY</h4>
	 * <pre><code>void onScrollY(int step);</code></pre>
	 * <p>Specifies the callback function for the vertical scrolling, when user
	 * changes the vertical scrolling step.
	 *
	 * <h4>onScrollX</h4>
	 * <pre><code>void onScrollX(int step);</code></pre>
	 * <p>Specifies the callback function for the horizental scrolling, when user
	 * changes the horizental scrolling step.
	 *
	 * <h4>offset</h4>
	 * int offset
	 * <p>Specifies the offset for the scrolling step to shift when the callback
	 * functions (onScrollX and onScrollY) are invoked.
	 * For example, if the offset is 2, then the steps in the onScrollX/Y event
	 * will start at 2.
	 * <p>Default: 0
	 *
	 * @type Map
	 */
	//opts: null,
	opts: WScrollOptions;
	_gap = 0;
	control: HTMLElement;
	widget: zk.Widget & {_cols?: number};
	_scale!: number; // Initialized before use.
	_isVer: boolean;
	uid: string;
	zcls: string;
	anchor: HTMLElement;

	// The following properties are initialized in _initDragdrop which is called
	// by the constructor.
	node!: HTMLElement;
	edrag!: HTMLElement;
	edragBody!: ChildNode;
	epos!: HTMLElement;
	eend!: HTMLElement;
	drag!: zk.Draggable;

	constructor(control: HTMLElement, opts: WScrollOptions) {
		super();
		this.control = control;
		this.opts = zk.$default(opts, {
			orient: 'horizontal',
			startPosition: 0,
			startStep: 0,
			offset: 0
		});
		this.anchor = this.opts.anchor || control.parentNode as HTMLElement;
		this.widget = zk.Widget.$(control)!;
		this.uid = this.widget.uuid;
		this.zcls = this.widget.getZclass();
		this._isVer = opts.orient == 'vertical';
		// ZK-2178: viewportSize is 0 if biglistbox has not model
		if (!opts.viewportSize && opts.viewportSize != 0)
			throw 'Handle required for a viewport size: {viewportSize: size}';
		this.redraw(this.anchor);
		this._initDragdrop();
		this._listenMouseEvent();
		if (this.opts.syncSize)
			this.syncSize();
		this._syncButtonStatus();
	}

	/**
	 * Syncs the scrolling area and control bar size.
	 * @param Map opts the opts can override the initail opts data for resizing.
	 */
	syncSize(opts?: Record<string, unknown>): void {
		if (opts) {
			this.opts = zk.copy(this.opts, opts);
		}
		this.edrag.style.display = '';
		if (this._isVer) {
			const opts = this.opts,
				top = opts.startPosition,
				start = opts.startStep,
				view = opts.viewport,
				end = opts.endStep,
				rest = end - view,
				edragHeight = this.edrag.offsetHeight - this._gap;
			let vsize = opts.viewportSize;
			if (rest <= 0) {
				this.eend.style.display = this.edrag.style.display = 'none';
				if (typeof this.opts.onScrollY == 'function')
					this.opts.onScrollY.call(this.widget, opts.offset); //reset scrolling
				return;
			}
			vsize -= edragHeight;
			if (vsize > rest) {
				this.epos.style.height = edragHeight + 'px';
				this._scale = 1;
				var es = this.eend.style;
				es.display = '';
				es.top = top + edragHeight + rest + 'px';
			} else {
				var rate = vsize / rest,
					height = Math.max(edragHeight * rate, 5);
				this.epos.style.height = height + 'px';
				this._scale = rate;
				this.eend.style.display = 'none'; // no end point
				if (vsize < 10)
					this.edrag.style.display = 'none';
			}
			var top1 = top + (this._scale * start),
				top2 = top + vsize;
			if (top1 > top2)
				top1 = top2;
			this.epos.style.top = this.edrag.style.top = top1 + 'px';
		} else {
			const opts = this.opts,
				left = opts.startPosition,
				start = opts.startStep,
				view = opts.viewport,
				end = opts.endStep,
				rest = end - view,
				edragWidth = this.edrag.offsetWidth - this._gap;
			let vsize = opts.viewportSize;
			if (rest <= 0) {
				this.eend.style.display = this.edrag.style.display = 'none';
				if (typeof this.opts.onScrollX == 'function')
					this.opts.onScrollX.call(this.widget, opts.offset); //reset scrolling
				return;
			}
			vsize -= edragWidth;
			if (vsize > rest) {
				this.epos.style.width = edragWidth + 'px';
				this._scale = 1;
				var es = this.eend.style;
				es.display = '';
				es.left = left + edragWidth + rest + 'px';
			} else {
				var rate = vsize / rest,
					width = Math.max(edragWidth * rate, 5);
				this.epos.style.width = width + 'px';
				this._scale = rate;
				this.eend.style.display = 'none'; // no end point
				if (vsize < 10)
					this.edrag.style.display = 'none';
			}

			var left1 = left + (this._scale * start),
				left2 = left + vsize;
			if (left1 > left2)
				left1 = left2;
			this.epos.style.left = this.edrag.style.left = left1 + 'px';
		}
	}

	_listenMouseEvent(): void {
		if (this._isVer) {
			// @ts-expect-error: zk.Event is not completely compatible with JQueryMousewheelEventObject
			jq(this.control).mousewheel(this._mousewheelY.bind(this));
		} else if (!zk.opera) { // ie and opera unsupported
			// @ts-expect-error: zk.Event is not completely compatible with JQueryMousewheelEventObject
			jq(this.control).mousewheel(this._mousewheelX.bind(this));
		}

		var $drag = jq(this.edrag);
		$drag.children('div')
			.on('mouseover', this._mouseOver.bind(this))
			.on('mouseout', this._mouseOut.bind(this))
			.on('mouseup', this._mouseUp.bind(this))
			.on('mousedown', this._mouseDown.bind(this));
		$drag.on('click', zk.$void);
	}

	_unlistenMouseEvent(): void {
		if (this._isVer)
			// @ts-expect-error: unmousewheel expects 0 arguments, but got 1
			jq(this.control).unmousewheel(this._mousewheelY.bind(this));
		else if (!zk.opera) // ie and opera unsupported
			// @ts-expect-error: unmousewheel expects 0 arguments, but got 1
			jq(this.control).unmousewheel(this._mousewheelX.bind(this));

		var $drag = jq(this.edrag);
		$drag.children('div')
			.off('mouseover', this._mouseOver.bind(this))
			.off('mouseout', this._mouseOut.bind(this))
			.off('mouseup', this._mouseUp.bind(this))
			.off('mousedown', this._mouseDown.bind(this));
		$drag.off('click', zk.$void);
	}

	_mouseOver(evt: zk.Event): void {
		var cls = evt.target.className,
			index = cls.lastIndexOf('-'),
			key = cls.substring(index + 1),
			$drag = jq(this.edrag);
		if ($drag.hasClass(cls + '-clk')) {
			$drag.removeClass(cls + '-clk');
		}
		switch (key) {
		case 'home':
		case 'up':
			if (this.opts.startStep > 0)
				$drag.addClass(cls + '-over');
			break;
		case 'down':
		case 'end':
			var opts = this.opts;
			if (opts.startStep < opts.endStep - opts.viewport) {
				$drag.addClass(cls + '-over');
			}
			break;
		}
	}

	_mouseOut(evt: zk.Event): void {
		var cls = evt.target.className,
			$drag = jq(this.edrag);
		$drag.removeClass(cls + '-over');
		if ($drag.hasClass(cls + '-clk')) {
			$drag.removeClass(cls + '-clk');
		}
	}

	_mouseUp(evt: zk.Event): void {
		jq(this.edrag).removeClass(evt.target.className + '-clk');
	}

	_mouseDown(evt: zk.Event): void {
		var cls = evt.target.className,
			index = cls.lastIndexOf('-'),
			key = cls.substring(index + 1),
			$drag = jq(this.edrag);
		if (!$drag.hasClass(cls + '-over') && !zk.mobile) //no mouse over for mobile
			return;// disable

		$drag.addClass(cls + '-clk');

		var opts = this.opts;
		switch (key) {
		case 'home':
			if (opts.startStep > 0) {
				opts.startStep = 0;
				if (this._isVer) {
					const moving = opts.startPosition + 'px';
					this.epos.style.top = moving;
					$drag.animate({top: moving}, 500);
					if (typeof this.opts.onScrollY == 'function')
						this.opts.onScrollY.call(this.widget, opts.startStep + opts.offset);
				} else {
					const moving = opts.startPosition + 'px';
					this.epos.style.left = moving;
					$drag.animate({left: moving}, 500);
					if (typeof this.opts.onScrollX == 'function')
						this.opts.onScrollX.call(this.widget, opts.startStep + opts.offset);
				}
				$drag.removeClass(cls + '-over');
			}
			break;
		case 'up':
			if (opts.startStep > 0) {
				opts.startStep -= 1;
				var move = opts.startPosition + (opts.startStep * this._scale);
				if (this._isVer) {
					var end;
					if (zk(this.eend).isVisible()) {
						end = this.eend.offsetTop;
					} else {
						end = opts.viewportSize + opts.startPosition;
					}
					end -= this.edrag.offsetHeight - this._gap;

					this.epos.style.top = move + 'px';
					if (end < move) {
						this.edrag.style.top = end + 'px';
					} else {
						this.edrag.style.top = move + 'px';
					}
					if (typeof this.opts.onScrollY == 'function')
						this.opts.onScrollY.call(this.widget, opts.startStep + opts.offset);
				} else {
					var end;
					if (zk(this.eend).isVisible()) {
						end = this.eend.offsetLeft;
					} else {
						end = opts.viewportSize + opts.startPosition;
					}
					end -= this.edrag.offsetWidth - this._gap;

					this.epos.style.left = move + 'px';
					if (end < move) {
						this.edrag.style.left = end + 'px';
					} else {
						this.edrag.style.left = move + 'px';
					}

					if (typeof this.opts.onScrollX == 'function')
						this.opts.onScrollX.call(this.widget, opts.startStep + opts.offset);
				}

				if (opts.startStep == 0)
					$drag.removeClass(cls + '-over');
			}
			break;
		case 'down':
			if (opts.startStep < opts.endStep - opts.viewport) {
				opts.startStep += 1;
				var move = opts.startPosition + (opts.startStep * this._scale);
				if (this._isVer) {
					var end;
					if (zk(this.eend).isVisible()) {
						end = this.eend.offsetTop;
					} else {
						end = opts.viewportSize + opts.startPosition;
					}
					end -= this.edrag.offsetHeight - this._gap;

					this.epos.style.top = move + 'px';
					if (end < move) {
						this.edrag.style.top = end + 'px';
					} else {
						this.edrag.style.top = move + 'px';
					}
					if (typeof this.opts.onScrollY == 'function')
						this.opts.onScrollY.call(this.widget, opts.startStep + opts.offset);
				} else {
					var end;
					if (zk(this.eend).isVisible()) {
						end = this.eend.offsetLeft;
					} else {
						end = opts.viewportSize + opts.startPosition;
					}
					end -= this.edrag.offsetWidth - this._gap;

					this.epos.style.left = move + 'px';
					if (end < move) {
						this.edrag.style.left = end + 'px';
					} else {
						this.edrag.style.left = move + 'px';
					}

					if (typeof this.opts.onScrollX == 'function')
						this.opts.onScrollX.call(this.widget, opts.startStep + opts.offset);
				}
				if (opts.startStep == opts.endStep - opts.viewport)
					$drag.removeClass(cls + '-over');
			}
			break;
		case 'end':
			if (opts.startStep < opts.endStep - opts.viewport) {
				opts.startStep = opts.endStep - opts.viewport;
				if (this._isVer) {
					let moving: number;
					if (zk(this.eend).isVisible()) {
						moving = this.eend.offsetTop - (this.edrag.offsetHeight - this._gap);
					} else {
						moving = opts.startPosition + opts.viewportSize - (this.edrag.offsetHeight - this._gap);
					}
					this.epos.style.top = moving as unknown as string;
					$drag.animate({top: moving}, 500);
					if (typeof this.opts.onScrollY == 'function')
						this.opts.onScrollY.call(this.widget, opts.startStep + opts.offset);
				} else {
					let moving: number;
					if (zk(this.eend).isVisible()) {
						moving = this.eend.offsetLeft - (this.edrag.offsetWidth - this._gap);
					} else {
						moving = opts.startPosition + opts.viewportSize - (this.edrag.offsetWidth - this._gap);
					}
					this.epos.style.left = moving as unknown as string;
					$drag.animate({left: moving}, 500);
					if (typeof this.opts.onScrollX == 'function')
						this.opts.onScrollX.call(this.widget, opts.startStep + opts.offset);
				}
				$drag.removeClass(cls + '-over');
			}
			break;
		}
		this._syncButtonStatus();
	}

	_mousewheelY(evt: zk.Event, delta: number, deltaX: number, deltaY: number): void {
		if (deltaY) {
			evt.stop();
			var opts = this.opts,
				steps = opts.startStep,
				endStep = opts.endStep - opts.viewport,
				scale = this._scale,
				wgt = this.widget;
			if (deltaY > 0) { // up
				opts.startStep -= Math.max(Math.round(wgt._cols! / 5), 1);
				if (opts.startStep < 0)
					opts.startStep = 0;
			} else { // down
				opts.startStep += Math.max(Math.round(wgt._cols! / 5), 1);
				if (opts.startStep > endStep)
					opts.startStep = endStep;
			}
			if (steps == opts.startStep)
				return;// nothing changed

			var moving = opts.startPosition + (opts.startStep * scale),
				end = zk(this.eend).isVisible() ? this.eend.offsetTop - (this.edrag.offsetHeight - this._gap)
							: opts.startPosition + opts.viewportSize - (this.edrag.offsetHeight - this._gap);
			this.epos.style.top = moving + 'px';

			if (moving > end)
				moving = end;

			this.edrag.style.top = moving + 'px';
			if (typeof this.opts.onScrollY == 'function')
				this.opts.onScrollY.call(this.widget, opts.startStep + opts.offset);
			this._syncButtonStatus();
		}
	}

	_mousewheelX(evt: zk.Event, delta: number, deltaX: number, deltaY: number): void {
		if (deltaX) {
			evt.stop();
			var opts = this.opts,
				steps = opts.startStep,
				endStep = opts.endStep - opts.viewport,
				scale = this._scale,
				wgt = this.widget;
			if (deltaX < 0) { // up
				opts.startStep -= Math.max(Math.round(wgt._cols! / 5), 1);
				if (opts.startStep < 0)
					opts.startStep = 0;
			} else { // down
				opts.startStep += Math.max(Math.round(wgt._cols! / 5), 1);
				if (opts.startStep > endStep)
					opts.startStep = endStep;
			}
			if (steps == opts.startStep)
				return;// nothing changed

			var moving = opts.startPosition + (opts.startStep * scale),
				end = zk(this.eend).isVisible() ? this.eend.offsetLeft - (this.edrag.offsetWidth - this._gap)
							: opts.startPosition + opts.viewportSize - (this.edrag.offsetWidth - this._gap);
			this.epos.style.left = moving + 'px';

			if (moving > end)
				moving = end;

			this.edrag.style.left = moving + 'px';
			if (typeof this.opts.onScrollX == 'function')
				this.opts.onScrollX.call(this.widget, opts.startStep + opts.offset);
			this._syncButtonStatus();
		}
	}

	_initDragdrop(): void {
		var orient = this._isVer ? 'v' : 'h',
			uuid = this.uid + '-' + orient + 'bar';
		this.node = jq(uuid, zk)[0];
		this.edrag = this.node.firstChild as HTMLElement;
		this.edragBody = this.edrag.childNodes[2];
		this.epos = this.edrag.nextSibling as HTMLElement;
		this.eend = this.node.lastChild as HTMLElement;

		// sync the gap between edrag and epos
		var s = this.epos.style,
			old = s.display;
		s.display = 'block';
		this._gap = this._isVer ? this.edrag.offsetHeight - this.epos.offsetHeight
					: this.edrag.offsetWidth - this.epos.offsetWidth;
		s.display = old;

		this.drag = new zk.Draggable(this, this.edrag, {
			constraint: this._isVer ? 'vertical' : 'horizontal',
			snap: snap,
			starteffect: starteffect,
			zIndex: '12000',
			ignoredrag: ignoredrag,
			endeffect: endeffect
		});
		jq(this.epos).hide();
	}

	destroy(): void {
		this.drag.destroy();
		this._unlistenMouseEvent();
		jq(this.node).remove();
		// @ts-expect-error: these variables should only become undefined after destroy
		this.node = this.edrag = this.epos = this.drag = undefined;
	}

	redraw(p: HTMLElement): void {
		var orient = this._isVer ? 'v' : 'h',
			ocls = this._isVer ? 'vertical' : 'horizontal',
			uuid = this.uid + '-' + orient + 'bar',
			zcls = this.widget.$s('wscroll');
		jq(p).append('<div id="' + uuid + '" class="' + zcls + '-' + ocls + '">'
				+ '<div class="' + zcls + '-drag">'
					+ '<div class="' + zcls + '-home" title="' + msgzul.WS_HOME + '"></div>'
					+ '<div class="' + zcls + '-up" title="' + msgzul.WS_PREV + '"></div>'
					+ '<div class="' + zcls + '-body"></div>'
					+ '<div class="' + zcls + '-down" title="' + msgzul.WS_NEXT + '"></div>'
					+ '<div class="' + zcls + '-end" title="' + msgzul.WS_END + '"></div>'
				+ '</div>'
				+ '<div class="' + zcls + '-pos"></div>'
				+ '<div class="' + zcls + '-endbar"></div>'
			+ '</div>');
	}

	_syncButtonStatus(): void {
		var zcls = this.zcls + '-wscroll',
			$drag = jq(this.edrag),
			opts = this.opts;
		$drag.toggleClass(zcls + '-head', opts.startStep == 0);
		$drag.toggleClass(zcls + '-tail', opts.startStep == opts.endStep - opts.viewport);
	}

	/**
	 * Sets the easing animation function for the scrolling effects.
	 * For more details, please refer to jquery's easing plugin.
	 * http://gsgd.co.uk/sandbox/jquery/easing/
	 */
	static easing = easing;
}