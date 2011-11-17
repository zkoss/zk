/* SimpleDateConstraint.js

	Purpose:
		
	Description:
		
	History:
		Thu Sep 10 10:09:47     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A simple date constraint.
 * @disable(zkgwt)
 */
zul.inp.SimpleDateConstraint = zk.$extends(zul.inp.SimpleConstraint, {
	/** Constructor.
	 * @param Object a
	 * It can be String or number, the number or name of flag, 
	 * such as "no positive", 0x0001.
	 * @param zk.Widget the datebox
	 * @since 5.0.8
	 */
	$init: function (a, wgt) {
		this.$super('$init', a);
		this._localizedSymbols = wgt._localizedSymbols;
	},
	format: 'yyyyMMdd',
	parseConstraint_: function(constraint){
		if (constraint.startsWith("between")) {
			var j = constraint.indexOf("and", 7);
			if (j < 0 && zk.debugJS) 
				zk.error('Unknown constraint: ' + constraint);
			this._beg = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(7, j), this.format);
			this._end = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(j + 3), this.format);
			if (this._beg.getTime() > this._end.getTime()) {
				var d = this._beg;
				this._beg = this._end;
				this._end = d;
			}
				
			this._beg.setHours(0,0,0,0);
			this._end.setHours(0,0,0,0);
			return;
		} else if (constraint.startsWith("before")) {
			this._end = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(6), this.format);
			this._end.setHours(0,0,0,0);
			return;
		} else if (constraint.startsWith("after")) {
			this._beg = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(5), this.format);
			this._beg.setHours(0,0,0,0);
			return;
		}
		return this.$supers('parseConstraint_', arguments);
	},
	validate: function (wgt, val) {
		if (jq.type(val) == 'date') {
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
				new zk.fmt.Calendar(null, this._localizedSymbols).formatDate(this._beg, this.format) + " ~ "
					+ new zk.fmt.Calendar().formatDate(this._end, this.format) :
					">= " + new zk.fmt.Calendar().formatDate(this._beg, this.format):
					"<= " + new zk.fmt.Calendar().formatDate(this._end, this.format));
	}
});