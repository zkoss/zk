/**
 * A simple spinner constraint.
 * @disable(zkgwt)
 */
export class SimpleSpinnerConstraint extends zul.inp.SimpleConstraint {
	private _min?: number;
	private _max?: number;

	/** Returns the minimum value.
	 * @return int
	 */
	public getMin(): number | undefined {
		return this._min;
	}

	/** Set the minimum value.
	 * @param int min
	 */
	public setMin(min: number): this {
		this._min = min;
		return this;
	}

	/** Returns the maximum value.
	 * @return int
	 */
	public getMax(): number | undefined {
		return this._max;
	}

	/** Set the maximum value.
	 * @param int max
	 */
	public setMax(max: number): this {
		this._max = max;
		return this;
	}

	protected override parseConstraint_(cst: string): void {
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

	public override validate(wgt: zk.Widget, val: unknown): zul.inp.SimpleConstraintErrorMessages | string | undefined {
		var result = super.validate(wgt, val);
		switch (typeof val) {
			case 'number':
				var maxErr = this._max && val > this._max,
					minErr = this._min && val < this._min;
				if (maxErr || minErr) {
					var maxminErrMsg = this._errmsg['maxmin'],
						errmsg = maxminErrMsg ? maxminErrMsg : (maxErr ? this._errmsg['max'] : this._errmsg['min']),
						msg: string | undefined = errmsg ? errmsg : msgzul.OUT_OF_RANGE + ': ' + (this._min != null ? this._max != null ?
							this._min + ' - ' + this._max : '>= ' + this._min : '<= ' + this._max);
				}
		}
		return msg || result;
	}
}
zul.inp.SimpleSpinnerConstraint = zk.regClass(SimpleSpinnerConstraint);