/* Scrollbar.ts

	Purpose:

	Description:
		A scrollbar used for mesh element
	History:
		Thu, May 23, 2013 4:44:22 PM, Created by vincentjian

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

*/
function _showScrollbar(wgt: Scrollbar, orient: string, opacity: number): void {
	var isHide = opacity == 0,
		bar = wgt.$n(orient),
		embed = wgt.$n(orient + '-embed'),
		style: CSSStyleDeclaration;
	if (bar) {
		style = bar.style;
		style.display = isHide ? 'none' : 'block';
		style.opacity = opacity as unknown as string;
	}
	if (embed) {
		style = embed.style;
		style.display = isHide ? 'block' : 'none';
	}
}
function _setScrollPos(position: number, min: number, max: number): number {
	if (min > max)
		return position;
	if (position < min)
		position = min;
	if (position > max)
		position = max;
	return position;
}

export interface Position {
	x: number;
	y: number;
}
export interface ScrollbarOptions {
	onSyncPosition?: (this: zul.Scrollbar) => void;
	onScrollEnd?: (this: zul.Scrollbar) => void;
	embed?: boolean;
	step: number; // scrolling pixels
	wheelAmountStep: number; // wheel amount of step
	startPositionX: number; // hor-bar start position
	startPositionY: number; // ver-bar start position
}

/**
 * A Scrollbar used to replace browser native scrollbar on Mesh Element to
 * navigate the content, such as Grid/Listbox/Tree.
 */
@zk.WrapClass('zul.Scrollbar')
export class Scrollbar extends zk.Object {
	/** The container object for this scrolling that user can scroll the whole content
	 * @type DOMElement
	 */
	//cave: null,
	/** The content inside container object that will be scrolled.
	 * @type DOMElement
	 */
	//scroller: null,
	/**
	 * The opts of this scrollbar controls.
	 *
	 * <h4>embed</h4>
	 * boolean embed
	 * <p>Embed the scrollbar inside container or not.
	 * @defaultValue `false`
	 *
	 * <h4>step</h4>
	 * int step
	 * <p>Specifies scrolling pixels each time click on scroll-bar arrow.
	 * @defaultValue `20`
	 *
	 * <h4>wheelAmountStep</h4>
	 * int wheelAmountStep
	 * <p>Specifies the multiple of step when mouse wheel scrolling.
	 * @defaultValue `3`
	 *
	 * <h4>startPositionX</h4>
	 * int startPositionX
	 * <p>Specifies the horizontal scroll-bar start position according to the scrolling area.
	 * @defaultValue `0`
	 *
	 * <h4>startPositionY</h4>
	 * int startPositionY
	 * <p>Specifies the vertical scroll-bar start position according to the scrolling area.
	 * @defaultValue `0`
	 *
	 * @type Map
	 */
		//opts: null,
	/** @internal */
	_pos?: zk.Offset = undefined;
	/** @internal */
	_barPos?: zk.Offset = undefined;
	/** @internal */
	_pressTimer?: number = undefined;
	cave: HTMLElement;
	scroller: HTMLElement;
	widget: zk.Widget & {frozen?: zul.mesh.Frozen};
	uid: string;
	currentPos?: Position;
	opts: ScrollbarOptions;
	dragging?: boolean;
	needV?: boolean;
	needH?: boolean;
	hLimit!: number;
	hBarLimit!: number;
	hRatio!: number;
	vLimit!: number;
	vBarLimit!: number;
	vRatio!: number;
	frozen?: zul.mesh.Frozen; // Tested for falsity in `zul.mesh.Scrollbar.init`

	$n(id: string): HTMLElement {
		return jq(this.uid + '-' + id, zk)[0];
	}

	constructor(cave: HTMLElement | undefined, scroller: HTMLElement | undefined, opts: Partial<ScrollbarOptions>) {
		super();
		if (!cave || !scroller)
			throw 'Both wrapper and scroller dom element are required to generate scrollbar';
		//define cave container style to hide native browser scroll-bar
		this.cave = cave;
		var cs = cave.style;
		cs.position = 'relative';
		cs.overflow = 'hidden';
		//scrolling content
		this.scroller = scroller;
		//default options
		this.opts = zk.$default(opts, {
			embed: false,
			step: 20, //scrolling pixels
			wheelAmountStep: 3, //wheel amount of step
			startPositionX: 0, //hor-bar start position
			startPositionY: 0 //ver-bar start position
		});
		this.widget = zk.Widget.$(cave)!;
		this.uid = this.widget.uuid;
		//initialize scroll-bar position
		this._pos = [0, 0];
		this._barPos = [0, 0];
		this.currentPos = {x: this._pos[0], y: this._pos[1]};

		jq(cave)
			//bind scroll event for input tab key scroll
			.on('scroll', this.proxy(this._fixScroll))
			//bind mouse enter / mouse leave
			.on('mouseenter', this.proxy(this._mouseEnter))
			.on('mouseleave', this.proxy(this._mouseLeave));
	}

	destroy(): void {
		var cave = this.cave;
		jq(cave)
			//unbind scroll event for input tab scroll
			.off('scroll', this.proxy(this._fixScroll))
			//unbind mouse enter / mouse leave
			.off('mouseenter', this.proxy(this._mouseEnter))
			.off('mouseleave', this.proxy(this._mouseLeave));
		this._unbindMouseEvent('hor');
		this._unbindMouseEvent('ver');
		var hbar = this.$n('hor'),
			vbar = this.$n('ver');
		if (hbar)
			jq(hbar).remove();

		if (vbar)
			jq(vbar).remove();
		if (this.opts.embed) {
			var hembed = this.$n('hor-embed'),
				vembed = this.$n('ver-embed');
			if (hembed)
				jq(hembed).remove();
			if (vembed)
				jq(vembed).remove();
		}
		this._pos = this._barPos = this.currentPos = undefined;
	}

	hasVScroll(): boolean {
		return !!this.needV;
	}

	hasHScroll(): boolean {
		return !!this.needH;
	}

	syncSize(showScrollbar?: boolean): void {
		this._checkBarRequired();

		var wgt = this.widget,
			frozen = wgt.frozen, froenScrollWidth = 0,
			tpad = wgt.$n('tpad'), bpad = wgt.$n('bpad'),
			cave = this.cave, scroller = this.scroller,
			hbar = this.$n('hor'), vbar = this.$n('ver'),
			needH = this.needH, needV = this.needV,
			opts = this.opts,
			scrollHeight = scroller.scrollHeight;

		if (tpad && bpad) //for Mesh Widget ROD
			scrollHeight += tpad.offsetHeight + bpad.offsetHeight;
		if (frozen) //for Frozen component
			froenScrollWidth = 50 * (frozen._scrollScale || 0);

		if (needH) {
			var old = hbar.style.display;
			hbar.style.display = 'block'; // for calculate size
			var embed = this.$n('hor-embed'),
				left = this.$n('hor-left'),
				right = this.$n('hor-right'),
				wrapper = this.$n('hor-wrapper'),
				ws = wrapper.style,
				startX = opts.startPositionX,
				wdh = cave.offsetWidth - startX,
				swdh = scroller.scrollWidth - startX + froenScrollWidth,
				lwdh = left.offsetWidth,
				rwdh = right.offsetWidth,
				hwdh = wdh - lwdh - rwdh;

			if (swdh < wdh) //only happened with Frozen
				swdh = wdh + froenScrollWidth;

			if (startX) {
				left.style.left = jq.px(startX);
				ws.left = jq.px(startX + lwdh);
				if (embed)
					embed.style.left = jq.px(startX);
			}

			if (needV) {
				ws.width = jq.px(hwdh - rwdh);
				right.style.right = jq.px(rwdh);
				var oldv = vbar.style.display;
				vbar.style.display = 'block';
				swdh += jq(vbar).outerWidth(true)!;
				vbar.style.display = oldv;
			} else {
				ws.width = jq.px(hwdh);
			}

			var indicator = this.$n('hor-indicator'),
				wwdh = wrapper.offsetWidth,
				iwdh = Math.round(wwdh * wdh / swdh),
				iwdh = iwdh > 15 ? iwdh : 15;

			indicator.style.width = iwdh + 'px';
			if (embed)
				embed.style.width = (iwdh + lwdh + rwdh) + 'px';
			//sync scroller position limit
			this.hLimit = swdh - wdh;
			//sync scroll-bar indicator position limit
			var limit = wwdh - iwdh;
			if (limit <= 0) {
				this.hBarLimit = 0;
				indicator.style.display = 'none';
			} else {
				this.hBarLimit = limit;
			}
			//sync indicator/scroller width ratio
			this.hRatio = Math.abs(this.hLimit / this.hBarLimit);

			hbar.style.display = old; // for calculate size
		}
		if (needV) {
			var old = vbar.style.display;
			vbar.style.display = 'block'; // for calculate size
			var embed = this.$n('ver-embed'),
				up = this.$n('ver-up'),
				down = this.$n('ver-down'),
				wrapper = this.$n('ver-wrapper'),
				ws = wrapper.style,
				startY = opts.startPositionY,
				hgh = cave.offsetHeight - startY,
				shgh = scrollHeight - startY,
				uhgh = up.offsetHeight,
				dhgh = down.offsetHeight,
				vhgh = hgh - uhgh - dhgh;

			if (startY) {
				vbar.style.top = jq.px(cave.offsetTop + startY);
				down.style.bottom = jq.px(startY);
				if (embed)
					embed.style.top = vbar.style.top;
			}
			if (needH) {
				ws.height = jq.px0(vhgh - dhgh);
				down.style.bottom = jq.px(startY + dhgh);
				var oldh = hbar.style.display;
				hbar.style.display = 'block';
				shgh += jq(hbar).outerHeight(true)!;
				hbar.style.display = oldh;
			} else {
				ws.height = jq.px(vhgh);
			}

			var indicator = this.$n('ver-indicator'),
				whgh = wrapper.offsetHeight,
				ihgh = Math.round(whgh * hgh / shgh),
				ihgh = ihgh > 15 ? ihgh : 15;

			indicator.style.height = ihgh + 'px';
			if (embed)
				embed.style.height = (ihgh + uhgh + dhgh) + 'px';
			//sync scroller position limit
			this.vLimit = shgh - hgh;
			//sync scroll-bar indicator position limit
			var limit = whgh - ihgh;
			if (limit <= 0) {
				this.vBarLimit = 0;
				indicator.style.display = 'none';
			} else {
				this.vBarLimit = limit;
			}
			//sync indicator/scroller width ratio
			this.vRatio = Math.abs(this.vLimit / this.vBarLimit);

			vbar.style.display = old; // for calculate size
		}

		this.scrollTo(this._pos![0], this._pos![1]); //keep scroll position
		if (showScrollbar) {
			_showScrollbar(this, 'hor', 1);
			_showScrollbar(this, 'ver', 1);
		}
	}

	scrollTo(x: number, y: number, time?: number /* Compatibility with IScroll */, relative?: boolean /* Compatibility with IScroll */): void {
		if (this.needH) {
			x = _setScrollPos(x, 0, this.hLimit);
			var barPos = x / this.hRatio;
			this._syncPosition('hor', x);
			this._syncBarPosition('hor', barPos);
			this._syncEmbedBarPosition('hor', x + barPos);
		}
		if (this.needV) {
			y = _setScrollPos(y, 0, this.vLimit);
			var barPos = y / this.vRatio;
			this._syncPosition('ver', y);
			this._syncBarPosition('ver', barPos);
			this._syncEmbedBarPosition('ver', y + barPos);
		}
		//onScrollEnd callback
		this._onScrollEnd();
	}

	scrollToElement(dom: HTMLElement): void {
		var cave = this.cave,
			domTop = jq(dom).offset()!.top,
			domBottom = domTop + dom.offsetHeight,
			domLeft = jq(dom).offset()!.left,
			domRight = domLeft + dom.offsetWidth,
			viewTop = jq(cave).offset()!.top,
			viewBottom = viewTop + cave.offsetHeight,
			viewLeft = jq(cave).offset()!.left,
			viewRight = viewLeft + cave.offsetWidth,
			scrollUp = true,
			// if the offsetWidth are the same, we don't need to move to left.
			scrollLeft = dom.offsetWidth == cave.offsetWidth;

		// Bug ZK-2418 should consider the case when dom size is greater than view size
		if (((domRight >= viewLeft && domRight <= viewRight)
				|| (domLeft >= viewLeft && domLeft <= viewRight)
				|| (domLeft <= viewLeft && domRight >= viewRight))
			&& ((domTop >= viewTop && domTop <= viewBottom)
				|| (domBottom >= viewTop && domBottom <= viewBottom)
				|| (domTop <= viewTop && domBottom >= viewBottom))) {
			return; //already in the view port
		}

		if (domTop < viewTop)
			scrollUp = false;
		if (domLeft < viewLeft)
			scrollLeft = false;

		//calculate scrolling movement
		var movementY = scrollUp ? domBottom - viewBottom : viewTop - domTop,
			posY = this._pos![1] + (scrollUp ? movementY : -movementY),
			movementX = scrollLeft ? domRight - viewRight : viewLeft - domLeft,
			posX = this._pos![0] + (scrollLeft ? movementX : -movementX),
			barPos: number;

		if (this.needV) {
			//set and check if exceed scrolling limit
			posY = _setScrollPos(posY, 0, this.vLimit);
			barPos = posY / this.vRatio;
			//sync scroll position
			this._syncPosition('ver', posY);
			this._syncBarPosition('ver', barPos);
			this._syncEmbedBarPosition('ver', posY + barPos);
		}
		if (this.needH) {
			//set and check if exceed scrolling limit
			posX = _setScrollPos(posX, 0, this.hLimit);
			barPos = posX / this.hRatio;
			//sync scroll position
			this._syncPosition('hor', posX);
			this._syncBarPosition('hor', barPos);
			this._syncEmbedBarPosition('hor', posX + barPos);
		}
		//onScrollEnd callback
		this._onScrollEnd();

		// Bug ZK-2194
		// always sync with native scrollbar, if any.
		zk(dom).scrollIntoView();
	}

	isScrollIntoView(dom: HTMLElement): boolean {
		var cave = this.cave,
			domTop = jq(dom).offset()!.top,
			domBottom = domTop + dom.offsetHeight,
			domLeft = jq(dom).offset()!.left,
			domRight = domLeft + dom.offsetWidth,
			viewTop = jq(cave).offset()!.top,
			viewBottom = viewTop + cave.offsetHeight,
			viewLeft = jq(cave).offset()!.left,
			viewRight = viewLeft + cave.offsetWidth;

		if ((this.needV && domBottom <= viewBottom && domTop >= viewTop)
			|| (this.needH && domRight <= viewRight && domLeft >= viewLeft))
			return true;
		else
			return false;
	}

	getCurrentPosition(): Position | undefined {
		return this.currentPos;
	}

	setBarPosition(barPosition: number): this { //for Frozen use only
		var frozen = this.widget.frozen;
		if (frozen && this.needH) {
			var step = this.hBarLimit / frozen._scrollScale,
				barPos = barPosition * step;
			this._syncBarPosition('hor', barPos);
			this._syncEmbedBarPosition('hor', barPos);
		}
		return this;
	}

	/** @internal */
	_checkBarRequired(): void {
		var cave = this.cave,
			scroller = this.scroller,
			frozen = this.widget.frozen;
		//check if horizontal scroll-bar required
		this.needH = frozen ? true : (cave.offsetWidth < scroller.scrollWidth);
		var hbar = this.$n('hor');
		if (!this.needH) {
			if (hbar) {
				this._unbindMouseEvent('hor');
				this._syncPosition('hor', 0);
				jq(hbar).remove();
				if (this.opts.embed) {
					var hembed = this.$n('hor-embed');
					if (hembed)
						jq(hembed).remove();
				}
			}
		} else {
			if (!this.$n('hor')) {
				this.redraw(cave, 'hor');
				this._bindMouseEvent('hor');
			}
		}
		//check if vertical scroll-bar required
		this.needV = cave.offsetHeight < scroller.offsetHeight;
		var vbar = this.$n('ver');
		if (!this.needV) {
			if (vbar) {
				this._unbindMouseEvent('ver');
				this._syncPosition('ver', 0);
				jq(vbar).remove();
				if (this.opts.embed) {
					var vembed = this.$n('ver-embed');
					if (vembed)
						jq(vembed).remove();
				}
			}
		} else {
			if (!vbar) {
				this.redraw(cave, 'ver');
				this._bindMouseEvent('ver');
			}
		}
	}

	/** @internal */
	_bindMouseEvent(orient: string): void {
		var self = this,
			cave = self.cave,
			isH = orient == 'hor',
			bar = self.$n(orient),
			ind = self.$n(orient + '-indicator'),
			rail = self.$n(orient + '-rail'),
			arrow1 = self.$n(orient + (isH ? '-left' : '-up')),
			arrow2 = self.$n(orient + (isH ? '-right' : '-down'));

		if (isH && !zk.opera) {//IE and Opera does not support mouse wheel
			jq(cave).mousewheel(self._mousewheelX.bind(self) as never);
		} else {
			jq(cave).mousewheel(self._mousewheelY.bind(self) as never);
		}

		jq(bar).click(zk.$void);
		jq(ind).on('mousedown', self.proxy(self._dragStart));
		jq(rail)
			.on('mousedown', self.proxy(self._mouseDown))
			.on('mouseup', self.proxy(self._mouseUp));
		jq(arrow1)
			.on('mousedown', self.proxy(self._mouseDown))
			.on('mouseup', self.proxy(self._mouseUp));
		jq(arrow2)
			.on('mousedown', self.proxy(self._mouseDown))
			.on('mouseup', self.proxy(self._mouseUp));
	}

	/** @internal */
	_unbindMouseEvent(orient: string): void {
		var self = this,
			cave = self.cave,
			isH = orient == 'hor',
			bar = self.$n(orient),
			ind = self.$n(orient + '-indicator'),
			rail = self.$n(orient + '-rail'),
			arrow1 = self.$n(orient + (isH ? '-left' : '-up')),
			arrow2 = self.$n(orient + (isH ? '-right' : '-down'));

		if (isH && !zk.opera) {//IE and Opera does not support mouse wheel
			jq(cave).unmousewheel(self._mousewheelX.bind(self) as never);
		} else {
			jq(cave).unmousewheel(self._mousewheelY.bind(self) as never);
		}

		jq(bar).off('click', zk.$void);
		jq(ind).off('mousedown', self.proxy(self._dragStart));
		jq(rail)
			.off('mousedown', self.proxy(self._mouseDown))
			.off('mouseup', self.proxy(self._mouseUp));
		jq(arrow1)
			.off('mousedown', self.proxy(self._mouseDown))
			.off('mouseup', self.proxy(self._mouseUp));
		jq(arrow2)
			.off('mousedown', self.proxy(self._mouseDown))
			.off('mouseup', self.proxy(self._mouseUp));
	}

	/** @internal */
	_fixScroll(evt: zk.Event): void {
		var cave = this.cave;
		if (!this.dragging)
			this.scrollTo(cave.scrollLeft, cave.scrollTop);
	}

	/** @internal */
	_mouseEnter(evt: JQuery.MouseEnterEvent): void {
		_showScrollbar(this, 'hor', 1);
		_showScrollbar(this, 'ver', 1);
	}

	/** @internal */
	_mouseLeave(evt: JQuery.MouseLeaveEvent): void {
		if (this.dragging)
			return;

		_showScrollbar(this, 'hor', 0);
		_showScrollbar(this, 'ver', 0);
	}

	/** @internal */
	_dragStart(evt: JQuery.DragStartEvent): void {
		if (this._pressTimer) { //just in case
			clearInterval(this._pressTimer);
			this._pressTimer = undefined;
		}
		evt.preventDefault();
		var self = this,
			orient = self.$n('hor-indicator') == evt.currentTarget ? 'hor' : 'ver',
			isHor = orient == 'hor',
			point = isHor ? evt.pageX : evt.pageY,
			pos = isHor ? self._barPos![0] : self._barPos![1],
			data = {
				orient: orient,
				point: point,
				pos: pos
			};

		jq(document)
			.on('mousemove', data, self.proxy(self._dragMove))
			.on('mouseup', self.proxy(self._dragEnd));
	}

	/** @internal */
	_dragEnd(evt: JQuery.DragEndEvent): void {
		var self = this,
			x = evt.pageX!,
			y = evt.pageY!,
			cave = self.cave,
			left = jq(cave).offset()!.left,
			top = jq(cave).offset()!.top;

		jq(document)
			.off('mousemove', self.proxy(self._dragMove))
			.off('mouseup', self.proxy(self._dragEnd));

		self.dragging = false;

		if ((x < left || x > left + cave.offsetWidth)
			|| (y < top || y > top + cave.offsetHeight)) {
			_showScrollbar(self, 'hor', 0);
			_showScrollbar(self, 'ver', 0);
		}
	}

	/** @internal */
	_dragMove(evt: JQuery.DragEvent<never, {orient: string; point: number; pos: number}>): void {
		var data = evt.data,
			orient = data.orient,
			point = data.point,
			pos = data.pos,
			isHor = orient == 'hor',
			disp = (isHor ? evt.pageX : evt.pageY)! - point,
			barPos = pos + disp,
			limit = isHor ? this.hBarLimit : this.vBarLimit,
			ratio = isHor ? this.hRatio : this.vRatio,
			frozen = this.widget.frozen;

		this.dragging = true;
		//set and check if exceed scrolling limit
		barPos = _setScrollPos(barPos, 0, limit);
		//sync position
		this._syncBarPosition(orient, barPos);
		if (frozen && isHor) {
			var step = limit / frozen._scrollScale;
			frozen._doScroll(barPos / step);
			this._syncEmbedBarPosition('hor', barPos);
		} else {
			var pos = barPos * ratio;
			this._syncPosition(orient, pos);
			this._syncEmbedBarPosition(orient, pos + barPos);
			//onScrollEnd callback
			this._onScrollEnd();
		}
	}

	/** @internal */
	_mousewheelX(evt: zk.Event, delta: number, deltaX: number, deltaY: number): void {
		var opts = this.opts,
			step = opts.step * opts.wheelAmountStep,
			pos = this._pos![0];

		if (deltaX) {
			evt.stop();
			//left: -step, right: step
			pos += (deltaX > 0 ? -step : step);
			//set and check if exceed scrolling limit
			pos = _setScrollPos(pos + step, 0, this.hLimit);
			//sync position
			var frozen = this.widget.frozen,
				barPos = pos / this.hRatio;
			if (frozen) {
				step = this.hBarLimit / frozen._scrollScale;
				barPos = barPos / step;
				frozen._doScroll(barPos);
				this._syncEmbedBarPosition('hor', barPos);
			} else {
				this._syncPosition('hor', pos);
				this._syncEmbedBarPosition('hor', pos + barPos);
			}
			this._syncBarPosition('hor', barPos);
			//onScrollEnd callback
			this._onScrollEnd();
		}
	}

	/** @internal */
	_mousewheelY(evt: zk.Event, delta: number, deltaX: number, deltaY: number): void {
		var opts = this.opts,
			step = opts.step * opts.wheelAmountStep,
			pos = this._pos![1],
			barPos: number;

		if (deltaY) {
			var scrollUp = deltaY > 0;
			if (scrollUp && pos == 0)
				return;
			if (!scrollUp && pos == this.vLimit)
				return;

			evt.stop();
			//up: -step, down: step
			pos += (scrollUp ? -step : step);
			//set and check if exceed scrolling limit
			pos = _setScrollPos(pos, 0, this.vLimit);
			barPos = pos / this.vRatio;
			//sync position
			this._syncPosition('ver', pos);
			this._syncBarPosition('ver', barPos);
			this._syncEmbedBarPosition('ver', pos + barPos);
			//onScrollEnd callback
			this._onScrollEnd();
		}
	}

	/** @internal */
	_mouseUp(evt: JQuery.MouseUpEvent): void {
		clearInterval(this._pressTimer);
		this._pressTimer = undefined;
	}

	/** @internal */
	_mouseDown(evt: JQuery.MouseDownEvent<never, never, HTMLElement>): void {
		if (this._pressTimer) {
			clearInterval(this._pressTimer);
			this._pressTimer = undefined;
		}
		var target = evt.currentTarget,
			hRail = this.$n('hor-rail'),
			vRail = this.$n('ver-rail'),
			up = this.$n('ver-up'),
			down = this.$n('ver-down'),
			left = this.$n('hor-left'),
			right = this.$n('hor-right'),
			frozen = this.widget.frozen,
			hBarLimit = this.hBarLimit,
			step = frozen ? hBarLimit / frozen._scrollScale : this.opts.step;

		//click on rail
		if (target == hRail || target == vRail) {
			var isHor = target == hRail,
				orient = isHor ? 'hor' : 'ver',
				offset0 = jq(target).offset()!,
				offset = isHor ? offset0.left : offset0.top,
				ind = this.$n(orient + '-indicator'),
				indsz = isHor ? ind.offsetWidth : ind.offsetHeight,
				point = (isHor ? evt.pageX : evt.pageY) - offset - indsz / 2,
				limit = isHor ? this.hLimit : this.vLimit,
				ratio = isHor ? this.hRatio : this.vRatio,
				pos = isHor ? this._pos![0] : this._pos![1],
				barPos = isHor ? this._barPos![0] : this._barPos![1],
				pointlimit = point * ratio,
				rstep = step * 10;

			if (frozen) {
				point = _setScrollPos(point, 0, hBarLimit);
				this._syncBarPosition('hor', point);
				this._syncEmbedBarPosition('hor', point);
				frozen._doScroll(point / step);
			} else {
				//setInterval for long press on scroll rail
				this._pressTimer = setInterval(() => {
					var isLeftDown = point > barPos,
						min: number,
						max: number,
						bPos: number;

					min = isLeftDown ? pos : pointlimit;
					min = min < 0 ? 0 : min;
					max = isLeftDown ? pointlimit : pos;
					max = max > limit ? limit : max;
					//left/down: rstep, right/up: -rstep
					pos += (isLeftDown ? rstep : -rstep);
					//set and check if exceed scrolling limit
					pos = _setScrollPos(pos, min, max);
					bPos = pos / ratio;
					if (pos == pointlimit || pos <= min || pos >= max) {
						clearInterval(this._pressTimer);
						this._pressTimer = undefined;
					}
					//sync position
					this._syncPosition(orient, pos);
					this._syncBarPosition(orient, bPos);
					this._syncEmbedBarPosition(orient, pos + bPos);
					//onScrollEnd callback
					this._onScrollEnd();
				}, 50);
			}
		}
		//click on arrows
		if (target == left || target == right || target == up || target == down) {
			var isHor = target == left || target == right,
				orient = isHor ? 'hor' : 'ver',
				limit = isHor ? this.hLimit : this.vLimit,
				ratio = isHor ? this.hRatio : this.vRatio,
				pos = isHor ? this._pos![0] : this._pos![1],
				barPos = this._barPos![0];

			//setInterval for long press on arrow button
			this._pressTimer = setInterval(() => {
				if (frozen && isHor) { //Frozen Mesh
					barPos += (target == left ? -step : step);
					barPos = _setScrollPos(barPos, 0, hBarLimit);
					this._syncBarPosition(orient, barPos);
					this._syncEmbedBarPosition(orient, barPos);
					frozen._doScroll(barPos / step);
				} else {
					//horizontal scroll
					if (isHor) //left: -step, right: step
						pos += (target == left ? -step : step);
					else //up: -step, down: step
						pos += (target == up ? -step : step);
					//set and check if exceed scrolling limit
					pos = _setScrollPos(pos, 0, limit);
					barPos = pos / ratio;
					//sync position
					this._syncPosition(orient, pos);
					this._syncBarPosition(orient, barPos);
					this._syncEmbedBarPosition(orient, pos + barPos);
					//onScrollEnd callback
					this._onScrollEnd();
				}
			}, 50);
		}
	}

	/** @internal */
	_syncPosition(orient: string, pos: number): void {
		if (!this._pos)
			return;

		var isH = orient == 'hor',
			cave = this.cave,
			bar = this.$n(orient),
			embed = this.opts.embed;

		if (bar) {
			pos = Math.round(pos);
			this._pos[isH ? 0 : 1] = pos;

			bar.style[isH ? 'left' : 'top'] = pos + 'px';
			if (isH && this.needV) {
				this.$n('ver').style.right = -pos + 'px';
				if (embed)
					this.$n('ver-embed').style.right = -pos + 'px';
			}
			if (!isH && this.needH) {
				this.$n('hor').style.bottom = -pos + 'px';
				if (embed)
					this.$n('hor-embed').style.bottom = -pos + 'px';
			}
			cave[isH ? 'scrollLeft' : 'scrollTop'] = pos;

			//onSyncPosition callback
			var onSyncPosition = this.opts.onSyncPosition;
			if (onSyncPosition) {
				this.currentPos = {x: this._pos[0], y: this._pos[1]};
				onSyncPosition.call(this);
			}
		}
	}

	/** @internal */
	_syncBarPosition(orient: string, pos: number): void {
		var isH = orient == 'hor',
			indicator = this.$n(orient + '-indicator');

		this._barPos![isH ? 0 : 1] = pos;
		indicator.style[isH ? 'left' : 'top'] = pos + 'px';
	}

	/** @internal */
	_syncEmbedBarPosition(orient: string, pos: number): void {
		if (this.opts.embed) {
			var isH = orient == 'hor',
				embed = this.$n(orient + '-embed'),
				opts = this.opts,
				start = isH ? opts.startPositionX : opts.startPositionY;
			embed.style[isH ? 'left' : 'top'] = (pos + start) + 'px';
		}
	}

	/** @internal */
	_onScrollEnd(): void {
		var onScrollEnd = this.opts.onScrollEnd;
		if (onScrollEnd) {
			this.currentPos = {x: this._pos![0], y: this._pos![1]};
			onScrollEnd.call(this);
		}
	}

	redraw(cave: HTMLElement, orient: string): void {
		var isH = orient == 'hor',
			uidHTML = /*safe*/ this.uid + '-' + /*safe*/ orient,
			hvHTML = isH ? 'horizontal' : 'vertical',
			luHTML = isH ? 'left' : 'up',
			rdHTML = isH ? 'right' : 'down',
			outHTML = '';

		if (this.opts.embed) {
			outHTML += '<div id="' + uidHTML + '-embed" class="z-scrollbar-' + hvHTML + '-embed"></div>';
		}
		outHTML +=
			'<div id="' + uidHTML + '" class="z-scrollbar z-scrollbar-' + hvHTML + '">'
			+ '<div id="' + uidHTML + '-' + luHTML + '" class="z-scrollbar-' + luHTML + '">'
			+ '<i class="z-icon-caret-' + luHTML + '"></i>'
			+ '</div>'
			+ '<div id="' + uidHTML + '-wrapper" class="z-scrollbar-wrapper">'
			+ '<div id="' + uidHTML + '-indicator" class="z-scrollbar-indicator">'
			+ '<i class="z-scrollbar-icon z-icon-reorder"></i></div>'
			+ '<div id="' + uidHTML + '-rail" class="z-scrollbar-rail"></div>'
			+ '</div>'
			+ '<div id="' + uidHTML + '-' + rdHTML + '" class="z-scrollbar-' + rdHTML + '">'
			+ '<i class="z-icon-caret-' + rdHTML + '"></i>'
			+ '</div>'
			+ '</div>';
		jq(cave).append(/*safe*/ outHTML);
	}
}