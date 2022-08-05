/* Slider.ts

	Purpose:

	Description:

	History:
		Thu May 22 11:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

export interface SliderPosition {
	top?: string;
	left?: string;
}

/**
 * A slider.
 *  <p>Default {@link #getZclass}: z-slider.
 */
@zk.WrapClass('zul.inp.Slider')
export class Slider extends zul.Widget {
	_orient = 'horizontal';
	_curpos = 0;
	_minpos = 0;
	_maxpos = 100;
	_slidingtext = '{0}';
	_pageIncrement = -1;
	_step = -1;
	_mode = 'integer';
	_name?: string;
	efield?: HTMLInputElement;
	slidetip?: HTMLElement;
	static down_btn?: HTMLElement;
	slidepos?: number;

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 * @return String
	 */
	getOrient(): string {
		return this._orient;
	}

	/** Sets the orient.
	 * <p>Default : "horizontal"
	 * @param String orient either "horizontal" or "vertical".
	 */
	setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;

		if (o !== orient || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns the current position of the slider.
	 * <p>Default: 0.
	 * @return double
	 */
	getCurpos(): number {
		return this._curpos;
	}

	/** Sets the current position of the slider.
	 * If negative, 0 is assumed. If larger than {@link #getMaxpos},
	 * {@link #getMaxpos} is assumed.
	 * @param double curpos
	 */
	setCurpos(curpos: number, opts?: Record<string, boolean>): this {
		const o = this._curpos;
		this._curpos = curpos;

		if (o !== curpos || (opts && opts.force)) {
			if (this.desktop) {
				this._fixPos();
			}
		}

		return this;
	}

	/** Returns the minimum position of the slider.
	 * <p>Default: 0.
	 * @return double (since 7.0.1)
	 */
	getMinpos(): number {
		return this._minpos;
	}

	/** Sets the minimum position of the slider.
	 * @param double minpos (since 7.0.1)
	 */
	setMinpos(minpos: number, opts?: Record<string, boolean>): this {
		const o = this._minpos;
		this._minpos = minpos;

		if (o !== minpos || (opts && opts.force)) {
			if (this._curpos < minpos) {
				this._curpos = minpos;
			}
			this._fixStep();
			if (this.desktop)
				this._fixPos();
		}

		return this;
	}

	/** Returns the maximum position of the slider.
	 * <p>Default: 100.
	 * @return double (since 7.0.1)
	 */
	getMaxpos(): number {
		return this._maxpos;
	}

	/** Sets the maximum position of the slider.
	 * @param double maxpos (since 7.0.1)
	 */
	setMaxpos(maxpos: number, opts?: Record<string, boolean>): this {
		const o = this._maxpos;
		this._maxpos = maxpos;

		if (o !== maxpos || (opts && opts.force)) {
			if (this._curpos > maxpos) {
				this._curpos = maxpos;
			}
			this._fixStep();
			if (this.desktop)
				this._fixPos();
		}

		return this;
	}

	/** Returns the sliding text.
	 * <p>Default : "{0}"
	 * @return String
	 */
	getSlidingtext(): string | undefined {
		return this._slidingtext;
	}

	/** Sets the sliding text.
	 * The syntax "{0}" will be replaced with the position at client side.
	 * @param String slidingtext
	 */
	setSlidingtext(slidingtext: string): this {
		this._slidingtext = slidingtext;
		return this;
	}

	/** Returns the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 *
	 * <p>Default: -1 (means it will scroll to the position the user clicks).
	 */
	setPageIncrement(pageIncrement: number): this {
		this._pageIncrement = pageIncrement;
		return this;
	}

	/** Sets the amount that the value of {@link #getCurpos}
	 * changes by when the tray of the scroll bar is clicked.
	 * <p>Default: -1 (means it will scroll to the position the user clicks).
	 * @param double pginc the page increment. If negative, slider will scroll (since 7.0.1)
	 * to the position that user clicks.
	 */
	getPageIncrement(): number {
		return this._pageIncrement;
	}

	/** Returns the step of slider
	 * @return double
	 * @since 7.0.1
	 */
	getStep(): number {
		return this._step;
	}

	/** Sets the step of slider
	 * <p>Default: -1 (means it will scroll to the position the user clicks).
	 * <strong>Note:</strong> In "decimal" mode, the fraction part only contains one digit if step is -1.
	 * @param double step
	 * @since 7.0.1
	 */
	setStep(step: number, opts?: Record<string, boolean>): this {
		const o = this._step;
		this._step = step;

		if (o !== step || (opts && opts.force)) {
			this._fixStep();
		}

		return this;
	}

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
	getName(): string | undefined {
		return this._name;
	}

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
	setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || (opts && opts.force)) {
			if (this.efield)
				this.efield.name = this._name;
		}

		return this;
	}

	/** Returns the current mode of slider
	 * One of "integer", "decimal".
	 * <p> Default: "integer"
	 * @return String
	 * @since 7.0.1
	 */
	getMode(): string {
		return this._mode;
	}

	/** Sets the mode to integer or decimal.
	 *
	 * @param String name the mode which could be one of
	 * "integer", "decimal".
	 * @since 7.0.1
	 */
	setMode(mode: string, opts?: Record<string, boolean>): this {
		const o = this._mode;
		this._mode = mode;

		if (o !== mode || (opts && opts.force)) {
			this._fixStep();
			if (this.desktop) {
				this._fixPos();
			}
		}

		return this;
	}

	override setWidth(width?: string): this {
		super.setWidth(width);
		if (this.desktop && this._mold != 'knob') {
			this.onSize();
		}
		return this;
	}

	override setHeight(height?: string): this {
		super.setHeight(height);
		if (this.desktop && this._mold != 'knob') {
			this.onSize();
		}
		return this;
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
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
	}

	onup_(evt: zk.Event): void {
		var btn = zul.inp.Slider.down_btn,
			widget: Slider | undefined;
		if (btn) {
			widget = zk.Widget.$<Slider>(btn);
		}

		zul.inp.Slider.down_btn = undefined;
		if (widget)
			jq(document).off('zmouseup', widget.onup_);
	}

	override doMouseDown_(evt: zk.Event): void {
		if (this._mold == 'knob') {
			super.doMouseDown_(evt);
			return;
		}
		jq(document).on('zmouseup', this.onup_);
		zul.inp.Slider.down_btn = this.$n('btn');
		super.doMouseDown_(evt);
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (this._mold == 'knob') {
			super.doClick_(evt, popupOnly);
			return;
		}
		var $btn = jq(this.$n_('btn')),
			pos: zk.Offset | number = $btn.zk.revisedOffset(),
			wgt = this,
			pageIncrement = this._pageIncrement,
			moveToCursor = pageIncrement < 0 && Slider._getStep(this) < 0,
			isVertical = this.isVertical(),
			height = this._getHeight(),
			width = this._getWidth(),
			offset: number | undefined = isVertical ? evt.pageY - pos[1] : evt.pageX - pos[0];

		if (!$btn[0] || $btn.is(':animated')) return;

		if (!moveToCursor) {
			if (pageIncrement > 0) {
				var curpos = this._curpos + (offset > 0 ? pageIncrement : -pageIncrement);
				this._curpos = Slider._roundDecimal(this._constraintPos(curpos), Slider._digitsAfterDecimal(pageIncrement));
			} else {
				var total = isVertical ? height : width,
					to = (offset / total) * (this._maxpos - this._minpos);
				this._curpos = this._getSteppedPos(to + this._curpos);
			}
			offset = undefined; // update by _curpos
		}
		// B65-ZK-1884: Avoid button's animation out of range
		// B86-ZK-4125: Vertical slider area is not aligned with button
		if (offset != null) {
			var n = this.$n_(),
				sign = offset >= 0 ? 1 : -1,
				wgtDim = isVertical ? n.clientHeight : n.clientWidth,
				stepDim = wgtDim / (this._maxpos - this._minpos),
				stepOffset = Math.floor(Math.abs(offset / stepDim));
			if (stepOffset < 1) stepOffset = 1;
			offset = wgtDim ? stepOffset * stepDim * sign : 0;
		}
		var nextPos = Slider._getNextPos(this, offset),
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
		jq(this.$n_('area')).animate(isVertical ? {height: nextPos.top} : {width: nextPos.left}, speed);
		super.doClick_(evt, popupOnly);
	}

	_makeDraggable(): void {
		var opt: Partial<zk.DraggableOptions> = {
			constraint: this._orient || 'horizontal',
			starteffect: this._startDrag,
			change: this._dragging,
			endeffect: this._endDrag
		};
		if (Slider._getStep(this) > 0)
			opt.snap = this._getStepOffset();
		this._drag = new zk.Draggable(this, this.$n_('btn'), opt);
	}

	_snap(x: number, y: number): zk.Offset {
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
	}

	_startDrag(dg: zk.Draggable): void {
		var widget = dg.control as Slider,
			sclass = widget.getSclass();
		widget.$n_('btn').title = ''; //to avoid annoying effect
		widget.slidepos = widget._curpos;

		jq(document.body)
			.append('<div id="zul_slidetip" class="'
			+ widget.$s('popup')
			+ (sclass ? ' ' + sclass + '">' : '">')
			+ widget._slidingtext.replace(/\{0\}/g, widget.slidepos as unknown as string)
			+ '</div>');

		widget.slidetip = jq('#zul_slidetip')[0];
		if (widget.slidetip) {
			var slideStyle = widget.slidetip.style;
			if (zk.webkit) { //give initial position to avoid browser scrollbar
				slideStyle.top = '0px';
				slideStyle.left = '0px';
			}
		}
	}

	_dragging(dg: zk.Draggable): void {
		var widget = dg.control as Slider,
			isDecimal = widget.isDecimal(),
			pos = widget._realpos();
		if (pos != widget.slidepos) {
			widget.slidepos = widget._curpos = pos = widget._constraintPos(pos);
			var text = isDecimal ? pos.toFixed(Slider._digitsAfterDecimal(Slider._getStep(widget))) : pos;
			if (widget.slidetip) // B70-ZK-2081: Replace "{0}" with the position.
				widget.slidetip.innerHTML = widget._slidingtext.replace(/\{0\}/g, text as string);
			widget.fire('onScrolling', isDecimal ? {decimal: pos} : pos);
		}
		widget._fixPos();
	}

	_endDrag(dg: zk.Draggable): void {
		var widget = dg.control as Slider,
			pos = widget._constraintPos(widget._realpos());

		widget.fire('onScroll', widget.isDecimal() ? {decimal: pos} : pos);

		widget._fixPos();
		jq(widget.slidetip).remove();
		widget.slidetip = undefined;
	}

	_realpos(dg?: zk.Draggable): number {
		var btnofs = zk(this.$n('btn')).revisedOffset(),
			refofs = zk(this.$n()).revisedOffset(),
			maxpos = this._maxpos,
			minpos = this._minpos,
			step = Slider._getStep(this),
			pos: number;
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
			return this._curpos = pos > 0 ? Slider._roundDecimal(pos + minpos, Slider._digitsAfterDecimal(step)) : minpos;
		} else
			return this._curpos = (pos > 0 ? pos : 0) + minpos;
	}

	_constraintPos(pos: number): number {
		var step = Slider._getStep(this),
			max = this._maxpos;

		if (pos < max) {
			pos -= (pos - this._minpos) % step;
		} else {
			pos = max;
		}
		return pos;
	}

	_getSteppedPos(pos: number): number {
		var minpos = this._minpos,
			step = Slider._getStep(this),
			mul = 1,
			rmdPos;
		pos -= minpos;
		if (this.isDecimal()) {
			mul = Math.pow(10, Slider._digitsAfterDecimal(step));
			pos *= mul;
			step *= mul;
		}
		rmdPos = pos % step;
		return (pos - rmdPos + Math.round((rmdPos) / step) * step) / mul + minpos;
	}

	_getWidth(): number {
		return this.$n_().clientWidth - this.$n_('btn').offsetWidth;
	}

	_getHeight(): number {
		return this.$n_().clientHeight - this.$n_('btn').offsetHeight;
	}

	_getStepOffset(): zk.Offset {
		var totalLen = this.isVertical() ? this._getHeight() : this._getWidth(),
			step = Slider._getStep(this),
			ofs: zk.Offset = [0, 0];
		if (step)
			ofs[(this.isVertical() ? 1 : 0)] = totalLen > 0 ? totalLen * step / (this._maxpos - this._minpos) : 0;
		return ofs;
	}

	_fixSize(): void {
		var n = this.$n_(),
			btn = this.$n_('btn'),
			inners = this.$n_('inner').style;
		if (this.isVertical()) {
			btn.style.top = '-' + btn.offsetHeight / 2 + 'px';
			var het = n.clientHeight;
			inners.height = jq.px0(het > 0 ? het : (this._height as unknown as number) - btn.offsetHeight);
		} else {
			btn.style.left = '-' + btn.offsetWidth / 2 + 'px';
			var wd = n.clientWidth;
			inners.width = jq.px0(wd > 0 ? wd : (this._width as unknown as number) - btn.offsetWidth);
		}
	}

	_fixPos(): void {
		var btn = this.$n_('btn'),
			vert = this.isVertical(),
			newPos = jq.px0(Slider._getBtnNewPos(this));
		this.$n_('area').style[vert ? 'height' : 'width'] = newPos;
		btn.style[vert ? 'top' : 'left'] = newPos;
		if (this.slidetip)
			zk(this.slidetip).position(btn, vert ? 'end_before' : 'before_start');
	}

	_fixStep(): void {
		var step = Slider._getStep(this);
		if (this._drag) {
			if (step <= 0) {
				if (this._drag.opts.snap)
					delete this._drag.opts.snap;
			} else
				this._drag.opts.snap = this._getStepOffset();
		}
	}

	override onSize(): void {
		this._fixSize();
		this._fixPos();
	}

	/** Return whether this widget in scale mold
	 * @return boolean
	 */
	inScaleMold(): boolean {
		return this.getMold() == 'scale';
	}

	/** Return whether this widget in sphere mold
	 * @return boolean
	 */
	inSphereMold(): boolean {
		return this.getMold() == 'sphere';
	}

	/** Returns whether it is a vertical slider.
	 * @return boolean
	 */
	isVertical(): boolean {
		return 'vertical' == this._orient;
	}

	/** Returns whether it is a decimal slider.
	 * @return boolean
	 * @since 7.0.1
	 */
	isDecimal(): boolean {
		return 'decimal' == this._mode;
	}

	updateFormData(val: number): void {
		if (this._name) {
			val = val || 0;
			if (!this.efield)
				this.efield = jq.newHidden(this._name, val as unknown as string, this.$n_());
			else
				this.efield.value = val as unknown as string;
		}
	}

	onShow(): void {
		//B70-ZK-2438
		//retrieve snap again for whom is inside detail component
		if (!this._drag) {
			this._makeDraggable();
		}
	}

	override setFlexSize_(flexSize_: zk.FlexSize, isFlexMin?: boolean): void {
		super.setFlexSize_(flexSize_, isFlexMin);
		if (this._mold != 'knob') {
			this.onSize();
		}
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
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
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this._mold == 'knob') {
			super.unbind_(skipper, after, keepRod);
			return;
		}
		this.efield = undefined;
		if (this._drag) {
			this._drag.destroy();
			this._drag = undefined;
		}

		zWatch.unlisten({
			onSize: this,
			//fix B70-ZK-2438
			onShow: this
		});
		super.unbind_(skipper, after, keepRod);
	}

	static _digitsAfterDecimal(v: number): number {
		var vs = '' + v,
			i = vs.indexOf('.');
		return i < 0 ? 0 : vs.length - i - 1;
	}

	static _roundDecimal(v: number, p: number): number {
		var mul = Math.pow(10, p);
		return Math.round(v * mul) / mul;
	}

	static _getBtnNewPos(wgt: Slider): number {
		var btn = wgt.$n_('btn'),
			curpos = wgt._curpos,
			isDecimal = wgt.isDecimal();
	
		btn.title = isDecimal ? curpos.toFixed(Slider._digitsAfterDecimal(Slider._getStep(wgt))) : curpos as unknown as string;
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
	}

	static _getNextPos(wgt: Slider, offset?: number): SliderPosition {
		var $btn = jq(wgt.$n_('btn')),
			fum = wgt.isVertical() ? ['top', 'height'] as const : ['left', 'width'] as const,
			newPosition: SliderPosition = {};
	
		newPosition[fum[0]] = jq.px0(offset ?
			(offset + zk.parseInt($btn.css(fum[0])) - $btn[fum[1]]()! / 2) :
			Slider._getBtnNewPos(wgt));
	
		return newPosition;
	}

	static _getStep(wgt: Slider): number {
		var step = wgt._step;
		return (!wgt.isDecimal() || step != -1) ? step : 0.1;
	}
}