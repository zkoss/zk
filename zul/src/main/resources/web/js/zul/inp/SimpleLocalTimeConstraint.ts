/* SimpleLocalTimeConstraint.js

	Purpose:

	Description:

	History:
		Wed Aug 14 15:01:24 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A simple time constraint.
 * @disable(zkgwt)
 * @since 9.0.0
 */
zul.inp.SimpleLocalTimeConstraint = zk.$extends(zul.inp.SimpleConstraint, {
	/** Constructor.
	 * @param Object a
	 * It can be String or number, the number or name of flag,
	 * such as "no positive", 0x0001.
	 * @param zk.Widget the datebox
	 */
	$init: function (a, wgt) {
		this._wgt = wgt;
		this._localizedSymbols = wgt._localizedSymbols;
		this.$super('$init', a);
	},
	format: 'HHmmss',
	parseConstraint_: function (constraint) {
		var len = this.format.length + 1,
			arr = this._cstArr,
			wgt = this._wgt,
			tz = wgt && wgt.getTimeZone && wgt.getTimeZone();
		if (constraint.startsWith('between')) {
			var j = constraint.indexOf('and', 7);
			if (j < 0 && zk.debugJS)
				zk.error('Unknown constraint: ' + constraint);
			this._beg = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(7, j), this.format, null, null, null, tz);
			this._end = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(j + 3, j + 3 + len), this.format, null, null, null, tz);
			if (this._beg.getTime() > this._end.getTime()) {
				var d = this._beg;
				this._beg = this._end;
				this._end = d;
			}
			arr[arr.length] = 'between';
		} else if (constraint.startsWith('before') && !constraint.startsWith('before_')) {
			this._end = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(6, 6 + len), this.format, null, null, null, tz);
			arr[arr.length] = 'before';
		} else if (constraint.startsWith('after') && !constraint.startsWith('after_')) {
			this._beg = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(5, 5 + len), this.format, null, null, null, tz);
			arr[arr.length] = 'after';
		}
		return this.$supers('parseConstraint_', arguments);
	},
	validate: function (wgt, val) {
		var result = this.$supers('validate', arguments);
		if (val instanceof DateImpl) {
			var msg = this._errmsg,
				time = this._getTime(val);
			if (this._beg != null && this._getTime(this._beg) > time)
				return msg['between'] || msg['after'] || this.outOfRangeValue();
			if (this._end != null && this._getTime(this._end) < time)
				return msg['between'] || msg['before'] || this.outOfRangeValue();
		}
		return result;
	},
	_getTime: function (val) {
		return val.getSeconds() + val.getMinutes() * 60 + val.getHours() * 3600;
	},
	/** Returns the message about out of range value
	 * @return String
	 */
	outOfRangeValue: function () {
		var format = this._wgt._format,
			separator = msgzul.OUT_OF_RANGE_SEPARATOR ? ' ' + msgzul.OUT_OF_RANGE_SEPARATOR + ' ' : ' ~ ';
		return msgzul.OUT_OF_RANGE + ': ' + (this._beg != null ? this._end != null ?
				new zk.fmt.Calendar(null, this._localizedSymbols).formatDate(this._beg, format) + separator
					+ new zk.fmt.Calendar().formatDate(this._end, format) :
					'>= ' + new zk.fmt.Calendar().formatDate(this._beg, format) :
					'<= ' + new zk.fmt.Calendar().formatDate(this._end, format));
	}
});