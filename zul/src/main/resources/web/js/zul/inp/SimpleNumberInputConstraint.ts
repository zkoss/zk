/**
 * A simple NumberInput constraint.
 */
@zk.WrapClass('zul.inp.SimpleNumberInputConstraint')
export class SimpleNumberInputConstraint extends zul.inp.SimpleConstraint {
	/** @internal */
	_min?: number;
	/** @internal */
	_max?: number;

	/**
	 * @returns the minimum value.
	 */
	getMin(): number | undefined {
		return this._min;
	}

	/**
	 * Set the minimum value.
	 */
	setMin(min: number): this {
		this._min = min;
		return this;
	}

	/**
	 * @returns the maximum value.
	 */
	getMax(): number | undefined {
		return this._max;
	}

	/**
	 * Set the maximum value.
	 */
	setMax(max: number): this {
		this._max = max;
		return this;
	}

	/** @internal */
	override parseConstraint_(cst: string): void {
		var cstList = cst.replace(/ +/g, ' ').split(/[, ]/),
			len = cstList.length,
			isSpinner,
			arr = this._cstArr;
		if (cstList.$contains('max') && cstList.$contains('min')) {
			arr[arr.length] = 'maxmin';
			isSpinner = true;
		}
		for (var i = 0; i < len + 1; i++) {
			var csti = cstList[i];
			if (csti == 'min' || csti == 'max') {
				if (!isSpinner) {
					arr[arr.length] = csti;
					isSpinner = true;
				}
				this['_' + csti] = +cstList[++i];
			}
		}
		if (isSpinner) {
			return;
		} else
			return super.parseConstraint_(cst);
	}

	override validate(wgt: zk.Widget, val: unknown): zul.inp.SimpleConstraintErrorMessages | string | undefined {
		const result = super.validate(wgt, val),
			isNumber = typeof val === 'number',
			isZkNumber = val instanceof zk.BigDecimal || val instanceof zk.Long;
		if (isNumber || isZkNumber) {
			const realVal = isZkNumber ? val.$toNumber() : val,
				maxErr = this._max && realVal > this._max,
				minErr = this._min && realVal < this._min;
			if (maxErr || minErr) {
				var maxminErrMsg = this._errmsg.maxmin,
					errmsg = maxminErrMsg ? maxminErrMsg : (maxErr ? this._errmsg.max : this._errmsg.min),
					msg: string | undefined = errmsg ? errmsg : msgzul.OUT_OF_RANGE + ': ' + (this._min != null ? this._max != null ?
						this._min + ' - ' + this._max : '>= ' + this._min : '<= ' + this._max);
			}
		}
		return msg || result;
	}
}