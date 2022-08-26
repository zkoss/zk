/* Spinner.ts

	Purpose:

	Description:

	History:
		Thu May 27 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An edit box for holding a constrained integer.
 *
 * <p>Default {@link #getZclass}: z-spinner.
 */
@zk.WrapClass('zul.inp.Spinner')
export class Spinner extends zul.inp.NumberInputWidget<number> {
	_step = 1;
	_buttonVisible = true;
	_min?: number;
	_max?: number;
	_currentbtn?: HTMLElement;
	_noPreviousValue?: boolean;
	timerId?: number;

	/** Return the step of spinner
	 * @return int
	 */
	getStep(): number | undefined {
		return this._step;
	}

	/** Set the step of spinner
	 * @param int step
	 */
	setStep(step: number, opts?: Record<string, boolean>): this {
		this._step = step;
		return this;
	}

	/** Returns whether the button (on the right of the textbox) is visible.
	 * <p>Default: true.
	 * @return boolean
	 */
	isButtonVisible(): boolean {
		return this._buttonVisible;
	}

	/** Sets whether the button (on the right of the textbox) is visible.
	 * @param boolean visible
	 */
	setButtonVisible(buttonVisible: boolean, opts?: Record<string, boolean>): this {
		const o = this._buttonVisible;
		this._buttonVisible = buttonVisible;

		if (o !== buttonVisible || opts?.force) {
			zul.inp.RoundUtl.buttonVisible(this, buttonVisible);
		}

		return this;
	}

	override inRoundedMold(): boolean {
		return true;
	}

	/** Returns the value in int. If null, zero is returned.
	 * @return int
	 */
	intValue(): number | undefined {
		return super.getValue();
	}

	override setConstraint(constraint: string): this {
		if (typeof constraint == 'string' && constraint.charAt(0) != '['/*by server*/) {
			var spinnerConstraint = new zul.inp.SimpleSpinnerConstraint(constraint);
			this._min = spinnerConstraint._min;
			this._max = spinnerConstraint._max;
			super.setConstraint(spinnerConstraint);
		} else
			super.setConstraint(constraint);
		return this;
	}

	override coerceFromString_(value: string | undefined): zul.inp.CoerceFromStringResult | number | undefined {//copy from intbox
		if (!value) return undefined;

		var info = zk.fmt.Number.unformat(this._format!, value, false, this._localizedSymbols),
			val = parseInt(info.raw, 10);
		if (isNaN(val) || (info.raw != '' + val && info.raw != '-' + val))
			return {error: zk.fmt.Text.format(msgzul.INTEGER_REQUIRED, value)};
		if (val > 2147483647 || val < -2147483648)
			return {error: zk.fmt.Text.format(msgzul.OUT_OF_RANGE + '(−2147483648 - 2147483647)')};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;
	}

	override coerceToString_(value: unknown): string {//copy from intbox
		var fmt = this._format;
		return fmt ? zk.fmt.Number.format(fmt, value as string, this._rounding!, this._localizedSymbols)
				: value != null ? '' + value : '';
	}

	onHide = undefined;
	validate = undefined;

	override doKeyDown_(evt: zk.Event): void {
		var inp = this.getInputNode()!;
		if (inp.disabled || inp.readOnly)
			return;

		switch (evt.keyCode) {
		case 38://up
			this.checkValue();
			this._increase(true);
			evt.stop();
			return;
		case 40://down
			this.checkValue();
			this._increase(false);
			evt.stop();
			return;
		}
		super.doKeyDown_(evt);
	}

	_ondropbtnup(evt: zk.Event): void {
		this.domUnlisten_(document.body, 'onZMouseup', '_ondropbtnup');
		this._stopAutoIncProc();
		this._currentbtn = undefined;
	}

	_btnDown(evt: zk.Event): void {
		if (!this._buttonVisible || this._disabled) return;

		var btn = this.$n_('btn');

		if (!zk.dragging) {
			if (this._currentbtn) // just in case
				this._ondropbtnup(evt);

			this.domListen_(document.body, 'onZMouseup', '_ondropbtnup');
			this._currentbtn = btn;
		}

		this.checkValue();

		var ofs = zk(btn).revisedOffset(),
			isOverUpBtn = (evt.pageY - ofs[1]) < btn.offsetHeight / 2;

		if (isOverUpBtn) { //up
			this._increase(true);
			this._startAutoIncProc(true);
		} else {	// down
			this._increase(false);
			this._startAutoIncProc(false);
		}

		// disable browser's text selection
		evt.stop();
	}

	/**
	 * Sets bound value if the value out of range
	 */
	checkValue(): void {
		var inp = this.getInputNode()!,
			min = this._min,
			max = this._max;
		this._noPreviousValue = inp.value == '';

		if (!inp.value) {
			if (min && max)
				inp.value = ((min <= 0 && 0 <= max) ? 0 : min) as unknown as string;
			else if (min)
				inp.value = (min <= 0 ? 0 : min) as unknown as string;
			else if (max)
				inp.value = (0 <= max ? 0 : max) as unknown as string;
			else
				inp.value = (0) as unknown as string;
		}
	}

	_btnUp(evt: zk.Event): void {
		if (!this._buttonVisible || this._disabled || zk.dragging) return;

		this._onChanging();
		this._stopAutoIncProc();

		var inp = this.getInputNode()!;
		inp.focus();
	}

	_increase(is_add: boolean): void {
		var inp = this.getInputNode()!,
			value = this.coerceFromString_(inp.value), //ZK-1851 convert input value using pattern
			result;

		if (value && (value as zul.inp.CoerceFromStringResult).error)
			return; //nothing to do if error happens

		if (this._noPreviousValue && (this._min! > 0 || this._max! < 0)) {
			result = value;
			this._noPreviousValue = false;
		} else {
			result = is_add ? ((value as number) + this._step) : ((value as number) - this._step);
		}

		// control overflow
		if (result > Math.pow(2, 31) - 1)
			result = Math.pow(2, 31) - 1;
		else if (result < -Math.pow(2, 31))
			result = -Math.pow(2, 31);

		//over bound shall restore value
		if (this._max != null && result > this._max) result = value;
		else if (this._min != null && result < this._min) result = value;

		inp.value = this.coerceToString_(result); //ZK-1851 convert result using pattern

		this._onChanging();

	}

	_clearValue(): boolean {
		this.getInputNode()!.value = this._defRawVal = '';
		return true;
	}

	_startAutoIncProc(isup: boolean): void {
		var widget = this;
		if (this.timerId)
			clearInterval(this.timerId);

		this.timerId = setInterval(function () {widget._increase(isup);}, 200);
		jq(this.$n_('btn-' + (isup ? 'up' : 'down'))).addClass(this.$s('active'));
	}

	_stopAutoIncProc(): void {
		if (this.timerId)
			clearTimeout(this.timerId);

		this.timerId = undefined;
		jq('.' + this.$s('icon'), this.$n_('btn')).removeClass(this.$s('active'));
	}

	override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);

		zul.inp.RoundUtl.doFocus_(this);
	}

	override doBlur_(evt: zk.Event): void {
		super.doBlur_(evt);
		zul.inp.RoundUtl.doBlur_(this);
	}

	override afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean {
		if (!simulated && this._inplace)
			jq(this.$n_()).toggleClass(this.getInplaceCSS(), evt.keyCode == 13);

		return super.afterKeyDown_(evt, simulated);
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {//after compose
		super.bind_(desktop, skipper, after);

		var btn: HTMLElement | undefined;
		if (btn = this.$n('btn'))
			this.domListen_(btn, 'onZMouseDown', '_btnDown')
				.domListen_(btn, 'onZMouseUp', '_btnUp');

		zWatch.listen({onSize: this});
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this.timerId) {
			clearTimeout(this.timerId);
			this.timerId = undefined;
		}
		zWatch.unlisten({onSize: this});
		var btn = this.$n('btn');
		if (btn)
			this.domUnlisten_(btn, 'onZMouseDown', '_btnDown')
				.domUnlisten_(btn, 'onZMouseUp', '_btnUp');

		super.unbind_(skipper, after, keepRod);
	}

	getBtnUpIconClass_(): string {
		return 'z-icon-angle-up';
	}

	getBtnDownIconClass_(): string {
		return 'z-icon-angle-down';
	}
}