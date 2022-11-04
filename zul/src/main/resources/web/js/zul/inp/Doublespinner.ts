/* Dooublespinner.ts

	Purpose:

	Description:

	History:
		Mon Dec 20 10:17:24     2010, Created by jumperchen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _digitsAfterDecimal(v, DECIMAL?: string): number {
	var vs = '' + v,
		i = vs.indexOf(DECIMAL!);
	return i < 0 ? 0 : vs.length - i - 1;
}
function _shiftedSum(v1: number, v2: number, exp: number, asc: boolean): number {
	var mul;
	if (exp) {
		mul = Math.pow(10, exp);
		v1 *= mul;
		v2 *= mul;
		v1 = Math.round(v1);
		v2 = Math.round(v2);
	}
	var res = asc ? v1 + v2 : v1 - v2;
	if (exp)
		res /= mul;
	return res;
}
function _updateFixedDigits(wgt: Doublespinner, val?: number): void {
	var decimal = wgt._localizedSymbols ? wgt._localizedSymbols.DECIMAL : zk.DECIMAL,
			stepd = _digitsAfterDecimal(wgt._step, decimal),
			vald = _digitsAfterDecimal(val || wgt._value, decimal);
	wgt._fixedDigits = Math.max(stepd, vald);
}

/**
 * An edit box for holding a constrained double.
 *
 * @defaultValue {@link getZclass}: z-doublespinner.
 * @since 5.0.6
 */
@zk.WrapClass('zul.inp.Doublespinner')
export class Doublespinner extends zul.inp.NumberInputWidget<number> {
	/** @internal */
	_step = 1;
	/** @internal */
	_buttonVisible = true;
	timerId?: number;
	/** @internal */
	_currentbtn?: HTMLElement;
	/** @internal */
	_min?: number;
	/** @internal */
	_max?: number;
	/** @internal */
	_fixedDigits?: number;

	/**
	 * @returns the step of double spinner
	 */
	getStep(): number {
		return this._step;
	}

	/**
	 * Set the step of dobule spinner
	 */
	setStep(step: number, opts?: Record<string, boolean>): this {
		const o = this._step;
		this._step = step;

		if (o !== step || opts?.force) {
			_updateFixedDigits(this);
		}

		return this;
	}

	/**
	 * @returns whether the button (on the right of the textbox) is visible.
	 * @defaultValue `true`.
	 */
	isButtonVisible(): boolean {
		return this._buttonVisible;
	}

	/**
	 * Sets whether the button (on the right of the textbox) is visible.
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

	/**
	 * @returns the value in double. If null, zero is returned.
	 */
	doubleValue(): number | undefined {
		return super.getValue();
	}

	override setConstraint(constraint: string): this {
		if (typeof constraint == 'string' && !constraint.startsWith('[')/*by server*/) {
			var simpleDoubleSpinnerConstraint = new zul.inp.SimpleDoubleSpinnerConstraint(constraint);
			this._min = simpleDoubleSpinnerConstraint._min;
			this._max = simpleDoubleSpinnerConstraint._max;
			super.setConstraint(simpleDoubleSpinnerConstraint);
		} else
			super.setConstraint(constraint);
		return this;
	}

	/** @internal */
	override coerceFromString_(value: string | undefined): zul.inp.CoerceFromStringResult | number | undefined {//copy from doublebox
		// B50-3322816
		if (!value) return undefined;

		var info = zk.fmt.Number.unformat(this._format!, value, false, this._localizedSymbols),
			raw = info.raw,
			val = parseFloat(raw),
			valstr = '' + val,
			valind = valstr.indexOf('.'),
			rawind = raw.indexOf('.');

		if (isNaN(val) || !valstr.includes('e')) {
			if (rawind == 0) {
				raw = '0' + raw;
				++rawind;
			}

			if (rawind >= 0 && raw.substring(raw.substring(rawind + 1) as unknown as number) && valind < 0) {
				valind = valstr.length;
				valstr += '.';
			}

			var len = raw.length,
				vallen = valstr.length;

			//pre zeros
			if (valind >= 0 && valind < rawind) {
				vallen -= valind;
				len -= rawind;
				for (var zerolen = rawind - valind; zerolen-- > 0;)
					valstr = '0' + valstr;
			}

			//post zeros
			if (vallen < len) {
				for (var zerolen = len - vallen; zerolen-- > 0;)
					valstr += '0';
			}

			if (isNaN(val) || (raw != valstr && raw != '-' + valstr && !raw.includes('e'))) { //1e2: assumes OK
				if (!isNaN(val) && raw != valstr) //Bug ZK-1218: show Illegal value instead if input is number but too long
					return {error: zk.fmt.Text.format(msgzul.ILLEGAL_VALUE)};
				return {error: zk.fmt.Text.format(msgzul.NUMBER_REQUIRED, value)};
			}
		}

		if (info.divscale) val = val / Math.pow(10, info.divscale);

		// B50-3322795
		_updateFixedDigits(this, val);
		return val;
	}

	/** @internal */
	override coerceToString_(value?: number | string): string {//copy from intbox
		var fmt = this._format,
			DECIMAL = this._localizedSymbols ? this._localizedSymbols.DECIMAL : zk.DECIMAL;

		// ZK-2084: fix display for different step
		if (typeof value === 'number' && value % 1 == 0) { // is integer
			var precision = 1,
				decimal: string;
			if (this._step && (decimal = (this._step + '').split('.')[1])) {
				precision = decimal.length;
			}
			value = parseFloat(value as unknown as string).toFixed(precision);
		}

		return value == null ? '' : fmt ?
			zk.fmt.Number.format(fmt, parseFloat(value as string) as unknown as string, this._rounding!, this._localizedSymbols) :
				DECIMAL == '.' ? ('' + value) : ('' + value).replace('.', DECIMAL!);
	}

	onHide = undefined; // FIXME: requires further observation
	validate = undefined; // FIXME: requires further observation

	/** @internal */
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

	/** @internal */
	_ondropbtnup(evt: zk.Event): void {
		this.domUnlisten_(document.body, 'onZMouseup', '_ondropbtnup');
		this._stopAutoIncProc();
		this._currentbtn = undefined;
	}

	/** @internal */
	_btnDown(evt: zk.Event): void {
		if (!this._buttonVisible || this._disabled) return;

		var btn = this.$n('btn')!;

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

		if (!inp.value) {
			if (min && max)
				inp.value = ((min <= 0 && 0 <= max) ? 0 : min) as unknown as string;
			else if (min)
				inp.value = (min <= 0 ? 0 : min) as unknown as string;
			else if (max)
				inp.value = (0 <= max ? 0 : max) as unknown as string;
			else
				inp.value = 0 as unknown as string;
		}
	}

	/** @internal */
	_btnUp(evt: zk.Event): void {
		if (!this._buttonVisible || this._disabled || zk.dragging) return;

		this._onChanging();
		this._stopAutoIncProc();

		var inp = this.getInputNode()!;
		inp.focus();
	}

	/** @internal */
	_increase(asc: boolean): void {
		var inp = this.getInputNode()!,
			value = this.coerceFromString_(inp.value);

		if (value && (value as zul.inp.CoerceFromStringResult).error)
			return; //nothing to do if error happens

		var	shiftLen = Math.max(_digitsAfterDecimal(value), this._fixedDigits!),
			result = _shiftedSum(value as number, this._step, shiftLen, asc); // B50-3301517

		// control overflow
		if (result > Math.pow(2, 63) - 1)	result = Math.pow(2, 63) - 1;
		else if (result < -Math.pow(2, 63)) result = -Math.pow(2, 63);

		//over bound shall restore value
		if (this._max != null && result > this._max) result = value as number;
		else if (this._min != null && result < this._min) result = value as number;

		inp.value = this.coerceToString_(result);

		this._onChanging();

	}

	/** @internal */
	_clearValue(): boolean {
		this.getInputNode()!.value = this._defRawVal = '';
		return true;
	}

	/** @internal */
	_startAutoIncProc(isup: boolean): void {
		var widget = this;
		if (this.timerId)
			clearInterval(this.timerId);

		this.timerId = setInterval(function () {widget._increase(isup);}, 200);
		jq(this.$n('btn-' + (isup ? 'up' : 'down'))).addClass(this.$s('active'));
	}

	/** @internal */
	_stopAutoIncProc(): void {
		if (this.timerId)
			clearTimeout(this.timerId);

		this.timerId = undefined;
		jq('.' + this.$s('icon'), this.$n('btn')).removeClass(this.$s('active'));
	}

	/** @internal */
	override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);

		zul.inp.RoundUtl.doFocus_(this);
	}

	/** @internal */
	override doBlur_(evt: zk.Event): void {
		super.doBlur_(evt);
		zul.inp.RoundUtl.doBlur_(this);
	}

	/** @internal */
	override afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean {
		if (!simulated && this._inplace)
			jq(this.$n()).toggleClass(this.getInplaceCSS(), evt.keyCode == 13);

		return super.afterKeyDown_(evt, simulated);
	}

	/** @internal */
	override getAllowedKeys_(): string {
		var symbols = this._localizedSymbols;
		return super.getAllowedKeys_()
			+ (symbols ? symbols : zk).DECIMAL + 'e';
		//supports scientific expression such as 1e2
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		var btn: HTMLElement | undefined;
		if (btn = this.$n('btn'))
			this.domListen_(btn, 'onZMouseDown', '_btnDown')
				.domListen_(btn, 'onZMouseUp', '_btnUp');

		zWatch.listen({onSize: this});
	}

	/** @internal */
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

	/** @internal */
	getBtnUpIconClass_(): string {
		return 'z-icon-angle-up';
	}

	/** @internal */
	getBtnDownIconClass_(): string {
		return 'z-icon-angle-down';
	}
}