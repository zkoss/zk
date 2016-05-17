/**
 * A simple spinner constraint.
 * @disable(zkgwt)
 */
zul.inp.SimpleSpinnerConstraint = zk.$extends(zul.inp.SimpleConstraint, {
	$define: {
		/** Returns the minimum value.
		 * @return int
		 */
		/** Set the minimum value.
		 * @param int min
		 */
		min: _zkf = function () {},
		/** Returns the maximum value.
		 * @return int
		 */
		/** Set the maximum value.
		 * @param int max
		 */
		max: _zkf
	},
	parseConstraint_: function (cst) {
		var cstList = cst.replace(/ +/g,' ').split(/[, ]/),
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
			return this.$supers('parseConstraint_', arguments);
	},
	validate: function (wgt, val) {
		switch (typeof val) {
			case 'number':
				var maxErr = this._max && val > this._max,
					minErr = this._min && val < this._min;
				if (maxErr || minErr) {
					var maxminErrMsg = this._errmsg['maxmin'],
						errmsg = maxminErrMsg ? maxminErrMsg : (maxErr ? this._errmsg['max'] : this._errmsg['min']),
						msg = errmsg ? errmsg : msgzul.OUT_OF_RANGE + ': ' + (this._min != null ? this._max != null ?
							this._min + ' - ' + this._max : '>= ' + this._min : '<= ' + this._max);
				}
		}
		return msg || this.$supers('validate',arguments);
	}
});