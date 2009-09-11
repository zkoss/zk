/* SimpleDateConstraint.js

	Purpose:
		
	Description:
		
	History:
		Thu Sep 10 10:09:47     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.SimpleDateConstraint = zk.$extends(zul.inp.SimpleConstraint, {
	format: 'yyyyMMdd',
	parseConstraint_: function(constraint){
		if (constraint.startsWith("between")) {
			var j = constraint.indexOf("and", 7);
			if (j < 0 && zk.debugJS) 
				zk.error('Unknown constraint: ' + constraint);
			this._beg = zDateFormat.parseDate(constraint.substring(7, j), this.format);
			this._end = zDateFormat.parseDate(constraint.substring(j + 3), this.format);
			if (this._beg.getTime() > this._end.getTime()) {
				var d = this._beg;
				this._beg = this._end;
				this._end = d;
			}
			return;
		} else if (constraint.startsWith("before")) {
			this._end = zDateFormat.parseDate(constraint.substring(6), this.format);
			return;
		} else if (constraint.startsWith("after")) {
			this._beg = zDateFormat.parseDate(constraint.substring(5), this.format);
			return;
		}
		return this.$supers('parseConstraint_', arguments);
	},
	validate: function (wgt, val) {
		if (typeof val.getTime == 'function') {
			if (this._beg != null && this._beg.getTime() > val.getTime())
				return this.outOfRangeValue();
			if (this._end != null && this._end.getTime() < val.getTime())
				return this.outOfRangeValue();
		}
		return this.$supers('validate', arguments);
	},
	outOfRangeValue: function () {
		return msgzul.OUT_OF_RANGE + ': ' + (this._beg != null ? this._end != null ?
					zDateFormat.formatDate(this._beg, this.format) + " ~ "
					+ zDateFormat.formatDate(this._end, this.format) :
					">= " + zDateFormat.formatDate(this._beg, this.format):
					"<= " + zDateFormat.formatDate(this._end, this.format));
	}
});