/* SimpleDateConstraint.js

	Purpose:
		
	Description:
		
	History:
		Thu Sep 10 10:09:47     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A simple date constraint.
 */
zul.inp.SimpleDateConstraint = zk.$extends(zul.inp.SimpleConstraint, {
	format: 'yyyyMMdd',
	parseConstraint_: function(constraint){
		if (constraint.startsWith("between")) {
			var j = constraint.indexOf("and", 7);
			if (j < 0 && zk.debugJS) 
				zk.error('Unknown constraint: ' + constraint);
			this._beg = zk.fmt.Date.parseDate(constraint.substring(7, j), this.format);
			this._end = zk.fmt.Date.parseDate(constraint.substring(j + 3), this.format);
			if (this._beg.getTime() > this._end.getTime()) {
				var d = this._beg;
				this._beg = this._end;
				this._end = d;
			}
				
			this._beg.setHours(0);
			this._beg.setMinutes(0);
			this._beg.setSeconds(0);
			this._beg.setMilliseconds(0);
			
			this._end.setHours(0);
			this._end.setMinutes(0);
			this._end.setSeconds(0);
			this._end.setMilliseconds(0);
			return;
		} else if (constraint.startsWith("before")) {
			this._end = zk.fmt.Date.parseDate(constraint.substring(6), this.format);
			this._end.setHours(0);
			this._end.setMinutes(0);
			this._end.setSeconds(0);
			this._end.setMilliseconds(0);
			return;
		} else if (constraint.startsWith("after")) {
			this._beg = zk.fmt.Date.parseDate(constraint.substring(5), this.format);
			this._beg.setHours(0);
			this._beg.setMinutes(0);
			this._beg.setSeconds(0);
			this._beg.setMilliseconds(0);
			return;
		}
		return this.$supers('parseConstraint_', arguments);
	},
	validate: function (wgt, val) {
		if (typeof val.getTime == 'function') {
			var v = new Date(val.getFullYear(), val.getMonth(), val.getDate());
			if (this._beg != null && this._beg.getTime() > v.getTime())
				return this.outOfRangeValue();
			if (this._end != null && this._end.getTime() < v.getTime())
				return this.outOfRangeValue();
		}
		return this.$supers('validate', arguments);
	},
	/** Returns the message about out of range value
	 * @return String
	 */
	outOfRangeValue: function () {
		return msgzul.OUT_OF_RANGE + ': ' + (this._beg != null ? this._end != null ?
					zk.fmt.Date.formatDate(this._beg, this.format) + " ~ "
					+ zk.fmt.Date.formatDate(this._end, this.format) :
					">= " + zk.fmt.Date.formatDate(this._beg, this.format):
					"<= " + zk.fmt.Date.formatDate(this._end, this.format));
	}
});