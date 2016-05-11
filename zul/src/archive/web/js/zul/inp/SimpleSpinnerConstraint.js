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
		for(var i = 0; i < len + 1; i++) {
			if (cstList[i] == 'min') {
				this._min = cstList[++i] * 1;
				isSpinner = true;
			} else if (cstList[i] == 'max') {
				this._max = cstList[++i] * 1;
				isSpinner = true;
			}
		}
		if (isSpinner) {
			arr[arr.length] = cst.substring(0, 3);
			return;
		} else
			return this.$supers('parseConstraint_', arguments);
	},
	validate: function (wgt, val) {
		switch (typeof val) {
			case 'number':
				var maxErr = this._max && val > this._max;
				if (maxErr || (this._min && val < this._min)) {
					var errmsg = maxErr ? this._errmsg['max'] : this._errmsg['min'],
						msg = errmsg ? errmsg : msgzul.OUT_OF_RANGE + ': ' + (this._min != null ? this._max != null ?
							this._min + ' - ' + this._max : '>= ' + this._min : '<= ' + this._max);
				}
		}
		return msg || this.$supers('validate',arguments);
	}
});