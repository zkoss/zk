/* Slider.js

	Purpose:

	Description:

	History:
		Thu May 22 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
/**
 * A slider.
 *  <p>Default {@link #getZclass}: z-slider.
 */
zul.inp.Slider = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_curpos: 0,
	_minpos: 0,
	_maxpos: 100,
	_slidingtext: '{0}',
	_pageIncrement: -1,
	_step: -1,
	_mode: 'integer',

	$define: {
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * <p>Default : "horizontal"
		 * @param String orient either "horizontal" or "vertical".
		 */
		orient: function () {
			this.rerender();
		},
		/** Returns the current position of the slider.
		 * <p>Default: 0.
		 * @return double
		 */
		/** Sets the current position of the slider.
		 * If negative, 0 is assumed. If larger than {@link #getMaxpos},
		 * {@link #getMaxpos} is assumed.
		 * @param double curpos
		 */
		curpos: function () {
			if (this.desktop) {
				this._fixPos();
			}
		},
		/** Returns the minimum position of the slider.
		 * <p>Default: 0.
		 * @return double (since 7.0.1)
		 */
		/** Sets the minimum position of the slider.
		 * @param double minpos (since 7.0.1)
		 */
		minpos: function (minpos) {
			if (this._curpos < minpos) {
				this._curpos = minpos;
			}
			this._fixStep();
			if (this.desktop)
				this._fixPos();
		},
		/** Returns the maximum position of the slider.
		 * <p>Default: 100.
		 * @return double (since 7.0.1)
		 */
		/** Sets the maximum position of the slider.
		 * @param double maxpos (since 7.0.1)
		 */
		maxpos: function (maxpos) {
			if (this._curpos > maxpos) {
				this._curpos = maxpos;
			}
			this._fixStep();
			if (this.desktop)
				this._fixPos();
		},
		/** Returns the sliding text.
		 * <p>Default : "{0}"
		 * @return String
		 */
		/** Sets the sliding text.
		 * The syntax "{0}" will be replaced with the position at client side.
		 * @param String slidingtext
		 */
		slidingtext: null,
		/** Returns the amount that the value of {@link #getCurpos}
		 * changes by when the tray of the scroll bar is clicked.
		 *
		 * <p>Default: -1 (means it will scroll to the position the user clicks).
		 */
		/** Sets the amount that the value of {@link #getCurpos}
		 * changes by when the tray of the scroll bar is clicked.
		 * <p>Default: -1 (means it will scroll to the position the user clicks).
		 * @param double pginc the page increment. If negative, slider will scroll (since 7.0.1)
		 * to the position that user clicks.
		 */
		pageIncrement: null,
		/** Returns the step of slider
		 * @return double
		 * @since 7.0.1
		 */
		/** Sets the step of slider
		 * <p>Default: -1 (means it will scroll to the position the user clicks).
		 * <strong>Note:</strong> In "decimal" mode, the fraction part only contains one digit if step is -1.
		 * @param double step
		 * @since 7.0.1
		 */
		step: function () {
			this._fixStep();
		},
		/** Returns the name of this component.
		 * <p>Default: null.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 * @return String
		 */
		/** Sets the name of this component.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 *
		 * @param String name the name of this component.
		 */
		name: function () {
			if (this.efield)
				this.efield.name = this._name;
		},
		/** Returns the current mode of slider
		 * One of "integer", "decimal".
		 * <p> Default: "integer"
		 * @return String
		 * @since 7.0.1
		 */
		/** Sets the mode to integer or decimal.
		 *
		 * @param String name the mode which could be one of
		 * "integer", "decimal".
		 * @since 7.0.1
		 */
		mode: function () {
			this._fixStep();
			if (this.desktop) {
				this._fixPos();
			}
		}
	},
	setWidth: function (w) {
		this.$supers('setWidth', arguments);
		if (this.desktop && this._mold != 'knob') {
			this.onSize();
		}
	},
	setHeight: function (h) {
		this.$supers('setHeight', arguments);
		if (this.desktop && this._mold != 'knob') {
			this.onSize();
		}
	},
	domClass_: function () {
		var scls = this.$supers('domClass_', arguments);
		if (this._mold == 'knob')
			return scls;

		var isVertical = this.isVertical();
		if (isVertical)
			scls += ' ' + this.$s('vertical');
		else
			scls += ' ' + this.$s('horizontal');
		if (this.inSphereMold())
			scls += ' ' + this.$s('sphere');
		else if (this.inScaleMold() && !isVertical)
			scls += ' ' + this.$s('scale');

		return scls;
	},
	onup_: function (evt) {
		var btn = zul.inp.Slider.down_btn, widget;
		if (btn) {
			widget = zk.Widget.$(btn);
		}

		zul.inp.Slider.down_btn = null;
		if (widget)
			jq(document).off('zmouseup', widget.onup_);
	},
	doMouseDown_: function (evt) {
		if (this._mold == 'knob') {
			this.$supers('doMouseDown_', arguments);
			return;
		}
		jq(document).on('zmouseup', this.onup_);
		zul.inp.Slider.down_btn = this.$n('btn');
		this.$supers('doMouseDown_', arguments);
	},
	doClick_: function (evt) {
		if (this._mold == 'knob') {
			this.$supers('doClick_', arguments);
			return;
		}
		var $btn = jq(this.$n('btn')),
			pos = $btn.zk.revisedOffset(),
			wgt = this,
			pageIncrement = this._pageIncrement,
			moveToCursor = pageIncrement < 0 && this.$class._getStep(this) < 0,
			isVertical = this.isVertical(),
			height = this._getHeight(),
			width = this._getWidth(),
			offset = isVertical ? evt.pageY - pos[1] : evt.pageX - pos[0];

		if (!$btn[0] || $btn.is(':animated')) return;

		if (!moveToCursor) {
			if (pageIncrement > 0) {
				var curpos = this._curpos + (offset > 0 ? pageIncrement : -pageIncrement);
				this._curpos = this.$class._roundDecimal(this._constraintPos(curpos), this.$class._digitsAfterDecimal(pageIncrement));
			} else {
				var total = isVertical ? height : width,
					to = (offset / total) * (this._maxpos - this._minpos);
				this._curpos = this._getSteppedPos(to + this._curpos);
			}
			offset = null; // update by _curpos
		}
		// B65-ZK-1884: Avoid button's animation out of range
		// B86-ZK-4125: Vertical slider area is not aligned with button
		if (offset != null) {
			var n = this.$n(),
				sign = offset >= 0 ? 1 : -1,
				wgtDim = isVertical ? n.clientHeight : n.clientWidth,
				stepDim = wgtDim / (this._maxpos - this._minpos),
				stepOffset = Math.floor(Math.abs(offset / stepDim));
			if (stepOffset < 1) stepOffset = 1;
			offset = wgtDim ? stepOffset * stepDim * sign : 0;
		}
		var nextPos = this.$class._getNextPos(this, offset),
			speed = $btn.zk.getAnimationSpeed('slow');
		if (isVertical && zk.parseInt(nextPos.top) > height)
			nextPos.top = jq.px0(height);
		if (!isVertical && zk.parseInt(nextPos.left) > width)
			nextPos.left = jq.px0(width);
		//ZK-2332 use the speed set in the client-attribute, use the default value 'slow'
		$btn.animate(nextPos, speed, function () {
			pos = moveToCursor ? wgt._realpos() : wgt._curpos;
			pos = wgt._constraintPos(pos);
			wgt.fire('onScroll', wgt.isDecimal() ? {decimal: pos} : pos);
		});
		jq(this.$n('area')).animate(isVertical ? {height: nextPos.top} : {width: nextPos.left}, speed);
		this.$supers('doClick_', arguments);
	},
	_makeDraggable: function () {
		var opt = {
			constraint: this._orient || 'horizontal',
			starteffect: this._startDrag,
			snap: opt,
			change: this._dragging,
			endeffect: this._endDrag
		};
		if (this.$class._getStep(this) > 0)
			opt.snap = this._getStepOffset();
		this._drag = new zk.Draggable(this, this.$n('btn'), opt);
	},
	_snap: function (x, y) {
		var btn = this.$n('btn'), ofs = zk(this.$n()).cmOffset();
		ofs = zk(btn).toStyleOffset(ofs[0], ofs[1]);
		if (x <= ofs[0]) {
			x = ofs[0];
		} else {
			var max = ofs[0] + this._getWidth();
			if (x > max)
				x = max;
		}
		if (y <= ofs[1]) {
			y = ofs[1];
		} else {
			var max = ofs[1] + this._getHeight();
			if (y > max)
				y = max;
		}
		return [x, y];
	},
	_startDrag: function (dg) {
		var widget = dg.control,
			sclass = widget.getSclass();
		widget.$n('btn').title = ''; //to avoid annoying effect
		widget.slidepos = widget._curpos;

		jq(document.body)
			.append('<div id="zul_slidetip" class="'
			+ widget.$s('popup')
			+ (sclass ? ' ' + sclass + '">' : '">')
			+ DOMPurify.sanitize(widget._slidingtext.replace(/\{0\}/g, widget.slidepos))
			+ '</div>');

		widget.slidetip = jq('#zul_slidetip')[0];
		if (widget.slidetip) {
			var slideStyle = widget.slidetip.style;
			if (zk.webkit) { //give initial position to avoid browser scrollbar
				slideStyle.top = '0px';
				slideStyle.left = '0px';
			}
		}
	},
	_dragging: function (dg) {
		var widget = dg.control,
			isDecimal = widget.isDecimal(),
			pos = widget._realpos();
		if (pos != widget.slidepos) {
			widget.slidepos = widget._curpos = pos = widget._constraintPos(pos);
			var text = isDecimal ? pos.toFixed(widget.$class._digitsAfterDecimal(widget.$class._getStep(widget))) : pos;
			if (widget.slidetip) // B70-ZK-2081: Replace "{0}" with the position.
				widget.slidetip.innerHTML = DOMPurify.sanitize(widget._slidingtext.replace(/\{0\}/g, text));
			widget.fire('onScrolling', isDecimal ? {decimal: pos} : pos);
		}
		widget._fixPos();
	},
	_endDrag: function (dg) {
		var widget = dg.control,
			pos = widget._constraintPos(widget._realpos());

		widget.fire('onScroll', widget.isDecimal() ? {decimal: pos} : pos);

		widget._fixPos();
		jq(widget.slidetip).remove();
		widget.slidetip = null;
	},
	_realpos: function (dg) {
		var btnofs = zk(this.$n('btn')).revisedOffset(),
			refofs = zk(this.$n()).revisedOffset(),
			maxpos = this._maxpos,
			minpos = this._minpos,
			step = this.$class._getStep(this),
			pos;
		if (this.isVertical()) {
			var ht = this._getHeight();
			pos = ht ? ((btnofs[1] - refofs[1]) * (maxpos - minpos)) / ht : 0;
		} else {
			var wd = this._getWidth();
			pos = wd ? ((btnofs[0] - refofs[0]) * (maxpos - minpos)) / wd : 0;
		}
		if (!this.isDecimal())
			pos = Math.round(pos);
		if (step > 0) {
			return this._curpos = pos > 0 ? this.$class._roundDecimal(pos + minpos, this.$class._digitsAfterDecimal(step)) : minpos;
		} else
			return this._curpos = (pos > 0 ? pos : 0) + minpos;
	},
	_constraintPos: function (pos) {
		var step = this.$class._getStep(this),
			max = this._maxpos;

		if (pos < max) {
			pos -= (pos - this._minpos) % step;
		} else {
			pos = max;
		}
		return pos;
	},
	_getSteppedPos: function (pos) {
		var minpos = this._minpos,
			step = this.$class._getStep(this),
			mul = 1,
			rmdPos;
		pos -= minpos;
		if (this.isDecimal()) {
			mul = Math.pow(10, this.$class._digitsAfterDecimal(step));
			pos *= mul;
			step *= mul;
		}
		rmdPos = pos % step;
		return (pos - rmdPos + Math.round((rmdPos) / step) * step) / mul + minpos;
	},
	_getWidth: function () {
		return this.$n().clientWidth - this.$n('btn').offsetWidth;
	},
	_getHeight: function () {
		return this.$n().clientHeight - this.$n('btn').offsetHeight;
	},
	_getStepOffset: function () {
		var totalLen = this.isVertical() ? this._getHeight() : this._getWidth(),
			step = this.$class._getStep(this),
			ofs = [0, 0];
		if (step)
			ofs[(this.isVertical() ? 1 : 0)] = totalLen > 0 ? totalLen * step / (this._maxpos - this._minpos) : 0;
		return ofs;
	},
	_fixSize: function () {
		var n = this.$n(),
			btn = this.$n('btn'),
			inners = this.$n('inner').style;
		if (this.isVertical()) {
			btn.style.top = '-' + btn.offsetHeight / 2 + 'px';
			var het = n.clientHeight;
			inners.height = jq.px0(het > 0 ? het : this._height - btn.offsetHeight);
		} else {
			btn.style.left = '-' + btn.offsetWidth / 2 + 'px';
			var wd = n.clientWidth;
			inners.width = jq.px0(wd > 0 ? wd : this._width - btn.offsetWidth);
		}
	},
	_fixPos: function () {
		var btn = this.$n('btn'),
			vert = this.isVertical(),
			newPos = jq.px0(this.$class._getBtnNewPos(this));
		this.$n('area').style[vert ? 'height' : 'width'] = newPos;
		btn.style[vert ? 'top' : 'left'] = newPos;
		if (this.slidetip)
			zk(this.slidetip).position(btn, vert ? 'end_before' : 'before_start');
	},
	_fixStep: function () {
		var step = this.$class._getStep(this);
		if (this._drag) {
			if (step <= 0) {
				if (this._drag.opts.snap)
					delete this._drag.opts.snap;
			} else
				this._drag.opts.snap = this._getStepOffset();
		}
	},
	onSize: function () {
		this._fixSize();
		this._fixPos();
	},
	/** Return whether this widget in scale mold
	 * @return boolean
	 */
	inScaleMold: function () {
		return this.getMold() == 'scale';
	},
	/** Return whether this widget in sphere mold
	 * @return boolean
	 */
	inSphereMold: function () {
		return this.getMold() == 'sphere';
	},
	/** Returns whether it is a vertical slider.
	 * @return boolean
	 */
	isVertical: function () {
		return 'vertical' == this._orient;
	},
	/** Returns whether it is a decimal slider.
	 * @return boolean
	 * @since 7.0.1
	 */
	isDecimal: function () {
		return 'decimal' == this._mode;
	},
	updateFormData: function (val) {
		if (this._name) {
			val = val || 0;
			if (!this.efield)
				this.efield = jq.newHidden(this._name, val, this.$n());
			else
				this.efield.value = val;
		}
	},
	onShow: function () {
		//B70-ZK-2438
		//retrieve snap again for whom is inside detail component
		if (!this._drag) {
			this._makeDraggable();
		}
	},
	setFlexSize_: function (sz, isFlexMin) {
		this.$supers('setFlexSize_', arguments);
		if (this._mold != 'knob') {
			this.onSize();
		}
	},
	bind_: function () {
		this.$supers(zul.inp.Slider, 'bind_', arguments);
		if (this._mold == 'knob')
			return;
		this._fixSize();
		//fix B70-ZK-2438
		if (this.isRealVisible())
			this._makeDraggable();

		zWatch.listen({
			onSize: this,
			//fix B70-ZK-2438
			onShow: this
		});
		this.updateFormData(this._curpos);
		this._fixPos();
	},
	unbind_: function () {
		if (this._mold == 'knob') {
			this.$supers(zul.inp.Slider, 'unbind_', arguments);
			return;
		}
		this.efield = null;
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}

		zWatch.unlisten({
			onSize: this,
			//fix B70-ZK-2438
			onShow: this
		});
		this.$supers(zul.inp.Slider, 'unbind_', arguments);
	}
}, {
	_digitsAfterDecimal: function (v) {
		var vs = '' + v,
			i = vs.indexOf('.');
		return i < 0 ? 0 : vs.length - i - 1;
	},
	_roundDecimal: function (v, p) {
		var mul = Math.pow(10, p);
		return Math.round(v * mul) / mul;
	},
	_getBtnNewPos: function (wgt) {
		var btn = wgt.$n('btn'),
			curpos = wgt._curpos,
			isDecimal = wgt.isDecimal();
	
		btn.title = isDecimal ? curpos.toFixed(wgt.$class._digitsAfterDecimal(wgt.$class._getStep(wgt))) : curpos;
		wgt.updateFormData(curpos);
	
		var isVertical = wgt.isVertical(),
			ofs = zk(wgt.$n()).cmOffset(),
			totalLen = isVertical ? wgt._getHeight() : wgt._getWidth(),
			x = totalLen > 0 ? ((curpos - wgt._minpos) * totalLen) / (wgt._maxpos - wgt._minpos) : 0;
		if (!isDecimal)
			x = Math.round(x);
	
		ofs = zk(btn).toStyleOffset(ofs[0], ofs[1]);
		ofs = isVertical ? [0, (ofs[1] + x)] : [(ofs[0] + x), 0];
		ofs = wgt._snap(ofs[0], ofs[1]);
	
		return ofs[(isVertical ? 1 : 0)];
	},
	_getNextPos: function (wgt, offset) {
		var $btn = jq(wgt.$n('btn')),
			fum = wgt.isVertical() ? ['top', 'height'] : ['left', 'width'],
			newPosition = {};
	
		newPosition[fum[0]] = jq.px0(offset ?
			(offset + zk.parseInt($btn.css(fum[0])) - $btn[fum[1]]() / 2) :
			wgt.$class._getBtnNewPos(wgt));
	
		return newPosition;
	},
	_getStep: function (wgt) {
		var step = wgt._step;
		return (!wgt.isDecimal() || step != -1) ? step : 0.1;
	}
});
})();
