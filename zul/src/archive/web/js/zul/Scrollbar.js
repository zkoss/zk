/* Scrollbar.js

	Purpose:
	
	Description:	
		A scrollbar used for mesh element	
	History:
		Thu, May 23, 2013 4:44:22 PM, Created by vincentjian

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

*/
(function (zk) {
	function _showScrollbar(wgt, orient, opacity) {
		var isHide = opacity == 0,
			bar = wgt.$n(orient),
			embed = wgt.$n(orient + '-embed'),
			style;
		if (bar) {
			style = bar.style;
			style.display = isHide ? 'none' : 'block';
			style.opacity = opacity;
			if (zk.ie < 9)
				style.filter = 'alpha(opacity=' + 100 * opacity + ')';
		}
		if (embed) {
			style = embed.style;
			style.display = isHide ? 'block' : 'none';
			style.opacity = isHide ? 0.2 : 0;
			if (zk.ie < 9)
				style.filter = isHide ? 'alpha(opacity=20)' : 'alpha(opacity=0)';
		}
	};
	function _setScrollPos(position, min, max) {
		if (min > max)
			return position;
		if (position < min)
			position = min;
		if (position > max)
			position = max;
		return position;
	};
/**
 * A Scrollbar used to replace browser native scrollbar on Mesh Element to 
 * navigate the content, such as Grid/Listbox/Tree.
 */
zul.Scrollbar = zk.$extends(zk.Object, {
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
	 * <p>Default: false
	 * 
	 * <h4>step</h4>
	 * int step
	 * <p>Specifies scrolling pixels each time click on scroll-bar arrow.
	 * <p>Default: 20
	 * 
	 * <h4>wheelAmountStep</h4>
	 * int wheelAmountStep
	 * <p>Specifies the multiple of step when mouse wheel scrolling.
	 * <p>Default: 3
	 * 
	 * <h4>startPositionX</h4>
	 * int startPositionX
	 * <p>Specifies the horizontal scroll-bar start position according to the scrolling area.
	 * <p>Default: 0
	 * 
	 * <h4>startPositionY</h4>
	 * int startPositionY
	 * <p>Specifies the vertical scroll-bar start position according to the scrolling area.
	 * <p>Default: 0
	 * 
	 * @type Map
	 */
	//opts: null,
	_pos: null,
	_barPos: null,
	_pressTimer: null,
	_frozenScroll: 0,
	_frozenLimit: false,
	$n: function (id) {
		return jq(this.uid + '-' + id, zk)[0];
	},
	$init: function (cave, scroller, opts) {
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
		this.widget = zk.Widget.$(cave);
		this.uid = this.widget.uuid;
		//initialize scroll-bar position
		this._pos = [0, 0];
		this._barPos = [0, 0];
		this.currentPos = {x: this._pos[0], y: this._pos[1]};
		
		jq(cave)
			//bind scroll event for input tab key scroll
			.bind('scroll', this.proxy(this._fixScroll))
			//bind mouse enter / mouse leave
			.bind('mouseenter', this.proxy(this._mouseEnter))
			.bind('mouseleave', this.proxy(this._mouseLeave));
	},
	destroy: function () {
		var cave = this.cave;
		jq(cave)
			//unbind scroll event for input tab scroll
			.unbind('scroll', this.proxy(this._fixScroll))
			//unbind mouse enter / mouse leave
			.unbind('mouseenter', this.proxy(this._mouseEnter))
			.unbind('mouseleave', this.proxy(this._mouseLeave));
		this._unbindMouseEvent('hor');
		this._unbindMouseEvent('ver');
		jq(this.$n('hor')).remove();
		jq(this.$n('ver')).remove();
		this._pos = this._barPos = this.currentPos = null;
	},
	hasVScroll: function () {
		return this.needV;
	},
	hasHScroll: function () {
		return this.needH;
	},
	syncSize: function (showScrollbar) {
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
			hbar.style.display = 'none'; // for calculate size
		}
		if (needV) {
			vbar.style.display = 'block'; // for calculate size
			var embed = this.$n('ver-embed'),
				up = this.$n('ver-up'),
				down = this.$n('ver-down'),
				wrapper = this.$n('ver-wrapper'),
				ws = wrapper.style,
				startY = opts.startPositionY,
				hgh = cave.offsetHeight - startY,
				shgh = scrollHeight - startY,
				uhgh = up.offsetHeight
				dhgh = down.offsetHeight,
				vhgh = hgh - uhgh - dhgh;
			
			if (startY) {
				vbar.style.top = jq.px(cave.offsetTop + startY);
				down.style.bottom = jq.px(startY);
				if (embed)
					embed.style.top = vbar.style.top;
			}
			if (needH) {
				ws.height = jq.px(vhgh - dhgh);
				down.style.bottom = jq.px(startY + dhgh);
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
			vbar.style.display = 'none'; // for calculate size
		}
		
		this.scrollTo(this._pos[0], this._pos[1]); //keep scroll position
		if (showScrollbar) {
			_showScrollbar(this, 'hor', 0.8);
			_showScrollbar(this, 'ver', 0.8);
		}
	},
	scrollTo: function (x, y) {
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
	},
	scrollToElement: function (dom) {
		var cave = this.cave,
			domTop = jq(dom).offset().top,
			domBottom = domTop + dom.offsetHeight,
			domLeft = jq(dom).offset().left,
			domRight = domLeft + dom.offsetWidth,
			viewTop = jq(cave).offset().top,
			viewBottom = viewTop + cave.offsetHeight,
			viewLeft = jq(cave).offset().left,
			viewRight = viewLeft + cave.offsetWidth
			scrollUp = true,
			scrollLeft = true;
		
		if ((this.needV && domBottom <= viewBottom && domTop >= viewTop) ||
			(this.needH && domRight <= viewRight && domLeft >= viewLeft))
			return; //already in the view port
		
		if (domTop < viewTop)
			scrollUp = false;
		if (domLeft < viewLeft)
			scrollLeft = false;
		
		//calculate scrolling movement
		var movementY = scrollUp ? domBottom - viewBottom : viewTop - domTop,
			posY = this._pos[1] + (scrollUp ? movementY : -movementY),
			movementX = scrollLeft ? domRight - viewRight : viewLeft - domLeft,
			posX = this._pos[0] + (scrollLeft ? movementX : -movementX),
			barPos;
		
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
	},
	isScrollIntoView: function (dom) {
		var cave = this.cave,
			domTop = jq(dom).offset().top,
			domBottom = domTop + dom.offsetHeight,
			domLeft = jq(dom).offset().left,
			domRight = domLeft + dom.offsetWidth,
			viewTop = jq(cave).offset().top,
			viewBottom = viewTop + cave.offsetHeight,
			viewLeft = jq(cave).offset().left,
			viewRight = viewLeft + cave.offsetWidth;
		
		if ((this.needV && domBottom <= viewBottom && domTop >= viewTop) ||
			(this.needH && domRight <= viewRight && domLeft >= viewLeft))
			return true;
		else
			return false;
	},
	getCurrentPosition: function () {
		return this.currentPos;
	},
	setBarPosition: function (start) { //for Frozen use only
		var frozen = this.widget.frozen;
		if (frozen && this.needH) {
			var step = this.hBarLimit / frozen._scrollScale,
				barPos = start * step;
			this._syncBarPosition('hor', barPos);
			this._syncEmbedBarPosition('hor', barPos);
		}
	},
	_checkBarRequired: function () {
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
			}
		} else {
			if (!vbar) {
				this.redraw(cave, 'ver');
				this._bindMouseEvent('ver');
			}
		}
	},
	_bindMouseEvent: function (orient) {
		var self = this,
			cave = self.cave,
			isH = orient == 'hor',
			bar = self.$n(orient),
			ind = self.$n(orient + '-indicator'),
			rail = self.$n(orient + '-rail'),
			arrow1 = self.$n(orient + (isH ? '-left' : '-up')),
			arrow2 = self.$n(orient + (isH ? '-right' : '-down'));
		
		if (isH && (!zk.ie || !zk.opera)) //IE and Opera does not support mouse wheel
			jq(cave).mousewheel(self.proxy(self._mousewheelX));
		else
			jq(cave).mousewheel(self.proxy(self._mousewheelY));
		
		jq(bar).click(zk.$void);
		jq(ind).bind('mousedown', self.proxy(self._dragStart));
		jq(rail)
			.bind('mousedown', self.proxy(self._mouseDown))
			.bind('mouseup', self.proxy(self._mouseUp));
		jq(arrow1)
			.bind('mousedown', self.proxy(self._mouseDown))
			.bind('mouseup', self.proxy(self._mouseUp));
		jq(arrow2)
			.bind('mousedown', self.proxy(self._mouseDown))
			.bind('mouseup', self.proxy(self._mouseUp));
	},
	_unbindMouseEvent: function (orient) {
		var self = this,
			cave = self.cave,
			isH = orient == 'hor',
			bar = self.$n(orient),
			ind = self.$n(orient + '-indicator'),
			rail = self.$n(orient + '-rail'),
			arrow1 = self.$n(orient + (isH ? '-left' : '-up')),
			arrow2 = self.$n(orient + (isH ? '-right' : '-down'));
		
		if (isH && (!zk.ie || !zk.opera)) //IE and Opera does not support mouse wheel
			jq(cave).unmousewheel(self.proxy(self._mousewheelX));
		else
			jq(cave).unmousewheel(self.proxy(self._mousewheelY));
		
		jq(bar).unbind('click', zk.$void);
		jq(ind).unbind('mousedown', self.proxy(self._dragStart));
		jq(rail)
			.unbind('mousedown', self.proxy(self._mouseDown))
			.unbind('mouseup', self.proxy(self._mouseUp));
		jq(arrow1)
			.unbind('mousedown', self.proxy(self._mouseDown))
			.unbind('mouseup', self.proxy(self._mouseUp));
		jq(arrow2)
			.unbind('mousedown', self.proxy(self._mouseDown))
			.unbind('mouseup', self.proxy(self._mouseUp));
	},
	_fixScroll: function (evt) {
		var cave = this.cave;
		if (!this.dragging)
			this.scrollTo(cave.scrollLeft, cave.scrollTop);
	},
	_mouseEnter: function (evt) {
		_showScrollbar(this, 'hor', 0.8);
		_showScrollbar(this, 'ver', 0.8);
	},
	_mouseLeave: function (evt) {
		if (this.dragging)
			return;
		
		_showScrollbar(this, 'hor', 0);
		_showScrollbar(this, 'ver', 0);
	},
	_dragStart: function (evt) {
		if (this._pressTimer) { //just in case
			clearInterval(this._pressTimer);
			this._pressTimer = null;
		}
		evt.preventDefault();
		var self = this,
			orient = self.$n('hor-indicator') == evt.currentTarget ? 'hor' : 'ver',
			isHor = orient == 'hor',
			point = isHor ? evt.pageX : evt.pageY,
			pos = isHor ? self._barPos[0] : self._barPos[1],
			data = {
				orient: orient,
				point: point,
				pos: pos
			};
		
		jq(document)
			.bind('mousemove', data, self.proxy(self._dragMove))
			.bind('mouseup', self.proxy(self._dragEnd));
	},
	_dragEnd: function (evt) {
		var self = this,
			x = evt.pageX,
			y = evt.pageY,
			cave = self.cave,
			left = jq(cave).offset().left,
			top = jq(cave).offset().top;
		
		jq(document)
			.unbind('mousemove', self.proxy(self._dragMove))
			.unbind('mouseup', self.proxy(self._dragEnd));
		
		self.dragging = false;
		
		if ((x < left || x > left + cave.offsetWidth) ||
			(y < top || y > top + cave.offsetHeight)) {
			_showScrollbar(self, 'hor', 0);
			_showScrollbar(self, 'ver', 0);
		}
	},
	_dragMove: function (evt) {
		var data = evt.data,
			orient = data.orient,
			point = data.point,
			pos = data.pos,
			isHor = orient == 'hor',
			disp = (isHor ? evt.pageX : evt.pageY) - point,
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
	},
	_mousewheelX: function (evt, delta, deltaX, deltaY) {
		var opts = this.opts,
			step = opts.step * opts.wheelAmountStep,
			pos = this._pos[0];
		
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
	},
	_mousewheelY: function (evt, delta, deltaX, deltaY) {
		var opts = this.opts,
			step = opts.step * opts.wheelAmountStep,
			pos = this._pos[1],
			barPos;
		
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
	},
	_mouseUp: function (evt) {
		clearInterval(this._pressTimer);
		this._pressTimer = null;
	},
	_mouseDown: function (evt) {
		if (this._pressTimer) {
			clearInterval(this._pressTimer);
			this._pressTimer = null;
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
				offset = jq(target).offset(),
				offset = isHor ? offset.left : offset.top,
				ind = this.$n(orient + '-indicator'),
				indsz = isHor ? ind.offsetWidth : ind.offsetHeight,
				point = (isHor ? evt.pageX : evt.pageY) - offset  - indsz/2,
				limit = isHor ? this.hLimit : this.vLimit,
				ratio = isHor ? this.hRatio : this.vRatio,
				pos = isHor ? this._pos[0] : this._pos[1],
				barPos = isHor ? this._barPos[0] : this._barPos[1],
				pointlimit = point * ratio,
				rstep = step * 10;
				self = this;
			
			if (frozen) {
				point = _setScrollPos(point, 0, hBarLimit);
				self._syncBarPosition('hor', point);
				self._syncEmbedBarPosition('hor', point);
				frozen._doScroll(point / step);
			} else {
				//setInterval for long press on scroll rail
				self._pressTimer = setInterval(function () {
					var isLeftDown = point > barPos,
						min, max, bPos;
					
					min = isLeftDown ? pos : pointlimit;
					min = min < 0 ? 0 : min;
					max = isLeftDown ? pointlimit : pos;
					max = max > limit ? limit : max;
					//left/down: rstep, right/up: -rstep
					pos += (isLeftDown ? rstep : -rstep);
					//set and check if exceed scrolling limit
					pos = _setScrollPos(pos, min, max);
					bPos = pos/ratio;
					if (pos == pointlimit || pos <= min || pos >= max) {
						clearInterval(self._pressTimer);
						self._pressTimer = null;
					}
					//sync position
					self._syncPosition(orient, pos);
					self._syncBarPosition(orient, bPos);
					self._syncEmbedBarPosition(orient, pos + bPos);
					//onScrollEnd callback
					self._onScrollEnd();
				}, 50);
			}
		}
		//click on arrows
		if (target == left || target == right || target == up || target == down) {
			var self = this,
				isHor = target == left || target == right,
				orient = isHor ? 'hor' : 'ver',
				limit = isHor ? self.hLimit : self.vLimit,
				ratio = isHor ? self.hRatio : self.vRatio,
				pos = isHor ? self._pos[0] : self._pos[1],
				barPos = self._barPos[0];
			
			//setInterval for long press on arrow button
			self._pressTimer = setInterval(function () {
				if (frozen && isHor) { //Frozen Mesh
					barPos += (target == left ? -step : step);
					barPos = _setScrollPos(barPos, 0, hBarLimit);
					self._syncBarPosition(orient, barPos);
					self._syncEmbedBarPosition(orient, barPos);
					frozen._doScroll(barPos / step);
				} else {
					var bpos;
					//horizontal scroll
					if (isHor) //left: -step, right: step
						pos += (target == left ? -step : step);
					else //up: -step, down: step
						pos += (target == up ? -step : step);
					//set and check if exceed scrolling limit
					pos = _setScrollPos(pos, 0, limit);
					barPos = pos / ratio;
					//sync position
					self._syncPosition(orient, pos);
					self._syncBarPosition(orient, barPos);
					self._syncEmbedBarPosition(orient, pos + barPos);
					//onScrollEnd callback
					self._onScrollEnd();
				}
			}, 50);
		}
	},
	_syncPosition: function (orient, pos) {
		if (!this._pos)
			return;
		
		var isH = orient == 'hor',
			cave = this.cave,
			bar = this.$n(orient);
		
		if (bar) {
			pos = Math.round(pos);
			this._pos[isH ? 0 : 1] = pos;
			
			bar.style[isH ? 'left' : 'top'] = pos + 'px';
			if (isH && this.needV)
				this.$n('ver').style.right = -pos + 'px';
			if (!isH && this.needH)
				this.$n('hor').style.bottom = -pos + 'px';
			
			cave[isH ? 'scrollLeft' : 'scrollTop'] = pos;
			
			//onSyncPosition callback
			var onSyncPosition = this.opts.onSyncPosition;
			if (onSyncPosition) {
				this.currentPos = {x: this._pos[0], y: this._pos[1]};
				onSyncPosition.call(this);
			}
		}
	},
	_syncBarPosition: function (orient, pos) {
		var isH = orient == 'hor',
			indicator = this.$n(orient + '-indicator');
		
		this._barPos[isH ? 0 : 1] = pos;
		indicator.style[isH ? 'left' : 'top'] = pos + 'px';
	},
	_syncEmbedBarPosition: function (orient, pos) {
		if (this.opts.embed) {
			var isH = orient == 'hor',
				embed = this.$n(orient + '-embed'),
				opts = this.opts,
				start = isH ? opts.startPositionX : opts.startPositionY;
			embed.style[isH ? 'left' : 'top'] = (pos + start) + 'px';
		}
	},
	_onScrollEnd: function () {
		var onScrollEnd = this.opts.onScrollEnd;
		if (onScrollEnd) {
			this.currentPos = {x: this._pos[0], y: this._pos[1]};
			onScrollEnd.call(this);
		}
	},
	redraw: function (cave, orient) {
		var isH = orient == 'hor',
			uid = this.uid + '-' + orient,
			hv = isH ? 'horizontal' : 'vertical',
			lu = isH ? 'left' : 'up',
			rd = isH ? 'right' : 'down',
			out = [];
		
		if (this.opts.embed) {
			out.push('<div id="', uid, '-embed" class="z-scrollbar-', hv,
					'-embed"></div>');
		}
		out.push(
		'<div id="', uid, '" class="z-scrollbar z-scrollbar-', hv, '">',
			'<div id="', uid, '-', lu, '" class="z-scrollbar-', lu, '">',
				'<i class="z-icon-caret-', lu, '"></i>',
			'</div>',
			'<div id="', uid,'-wrapper" class="z-scrollbar-wrapper">',
				'<div id="', uid,'-indicator" class="z-scrollbar-indicator">',
					'<i class="z-icon-reorder"></i></div>',
				'<div id="', uid,'-rail" class="z-scrollbar-rail"></div>',
			'</div>',
			'<div id="', uid, '-', rd, '" class="z-scrollbar-', rd, '">',
				'<i class="z-icon-caret-', rd, '"></i>',
			'</div>',
		'</div>');
		jq(cave).append(out.join(''));
		out = null;
	}
});
})(zk);