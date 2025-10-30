/* SimpleDateConstraint.ts

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
 */
@zk.WrapClass('zul.inp.SimpleDateConstraint')
export class SimpleDateConstraint extends zul.inp.SimpleConstraint {
	readonly format = 'yyyyMMdd';
	/** @internal */
	_wgt: zul.db.Datebox;
	/** @internal */
	_localizedSymbols?: zk.LocalizedSymbols;
	/** @internal */
	_beg?: DateImpl;
	/** @internal */
	_end?: DateImpl;
	/** @internal */
	_disabledDates?: DateImpl[];

	/**
	 * It can be String or number, the number or name of flag,
	 * such as "no positive", 0x0001.
	 * @param wgt - datebox
	 * @since 5.0.8
	 */
	constructor(a: unknown, wgt: zul.db.Datebox) {
		super(a);
		this._wgt = wgt;
		this._localizedSymbols = wgt._localizedSymbols;
		this._disabledDates = [];
	}

	/** @internal */
	override parseConstraint_(constraint: string): void {
		var len = this.format.length + 1,
			arr = this._cstArr,
			wgt = this._wgt,
			tz = wgt && wgt.getTimeZone && wgt.getTimeZone();
		if (constraint.startsWith('between')) {
			const dates = this._parseBetweenDates(constraint, this.format, len, tz);
			this._beg = dates[0];
			this._end = dates[1];
			arr[arr.length] = 'between';
		} else if (constraint.startsWith('before') && !constraint.startsWith('before_')) {
			this._end = new zk.fmt.Calendar(undefined, this._localizedSymbols).parseDate(constraint.substring(6, 6 + len), this.format, undefined, undefined, undefined, tz);
			this._end!.setHours(0, 0, 0, 0);
			arr[arr.length] = 'before';
		} else if (constraint.startsWith('after') && !constraint.startsWith('after_')) {
			this._beg = new zk.fmt.Calendar(undefined, this._localizedSymbols).parseDate(constraint.substring(5, 5 + len), this.format, undefined, undefined, undefined, tz);
			this._beg!.setHours(0, 0, 0, 0);
			arr[arr.length] = 'after';
		} else if (constraint.startsWith('not')) {
			if (constraint.startsWith('not between')) {
				const dates = this._parseBetweenDates(constraint, this.format, len, tz);
				let disabled = dates[0];
				this._disabledDates!.push(disabled);
				while (disabled.getTime() != dates[1].getTime()) {
					disabled = Dates.newInstance(disabled.getTime() + 86400 * 1000);
					this._disabledDates!.push(disabled);
				}
				arr[arr.length] = 'not between';
			} else {
				const disabled = new zk.fmt.Calendar(undefined, this._localizedSymbols).parseDate(constraint.substring(3, 3 + len), this.format, undefined, undefined, undefined, tz);
				disabled!.setHours(0, 0, 0, 0);
				this._disabledDates!.push(disabled!);
				arr[arr.length] = 'not';
			}
		}
		return super.parseConstraint_(constraint);
	}

	/** @internal */
	_parseBetweenDates(constraint: string, format: string, len: number, tz: string | undefined): DateImpl[] {
		let j = constraint.indexOf('and', 7);
		if (j < 0 && zk.debugJS) {
			zk.error('Unknown constraint: ' + constraint);
		}
		let datesStrIndex = constraint.indexOf('between') + 7,
			beg = new zk.fmt.Calendar(undefined, this._localizedSymbols).parseDate(constraint.substring(datesStrIndex, j), format, undefined, undefined, undefined, tz),
			end = new zk.fmt.Calendar(undefined, this._localizedSymbols).parseDate(constraint.substring(j + 3, j + 3 + len), format, undefined, undefined, undefined, tz);
		if (beg!.getTime() > end!.getTime()) {
			const d = beg;
			beg = end;
			end = d;
		}
		beg!.setHours(0, 0, 0, 0);
		end!.setHours(0, 0, 0, 0);
		return [beg!, end!];
	}

	override validate(wgt: zk.Widget, val: unknown): zul.inp.SimpleConstraintErrorMessages | string | undefined {
		var result = super.validate(wgt, val);
		if (val instanceof window.DateImpl) {
			var msg = this._errmsg,
				v = Dates.newInstance([val.getFullYear(), val.getMonth(), val.getDate()], val.getTimeZone());
			if (this._beg != null && this._beg.getTime() > v.getTime())
				return msg.between || msg.after || this.outOfRangeValue();
			if (this._end != null && this._end.getTime() < v.getTime())
				return msg.between || msg.before || this.outOfRangeValue();
			if (this._disabledDates && this._disabledDates.length > 0) {
				let res = false;
				this._disabledDates.forEach(each => {
					if (each.getTime() === v.getTime())
						res = true;
				});
				if (res) {
					return msgzul.DATE_DISABLED;
				}
			}
		}
		return result;
	}

	/**
	 * @returns the message about out of range value
	 */
	outOfRangeValue(): string {
		var format = this._wgt._format,
			separator = msgzul.OUT_OF_RANGE_SEPARATOR ? ' ' + msgzul.OUT_OF_RANGE_SEPARATOR + ' ' : ' ~ ';
		return msgzul.OUT_OF_RANGE + ': ' + (this._beg != null ? this._end != null ?
				new zk.fmt.Calendar(undefined, this._localizedSymbols).formatDate(this._beg, format) + separator
					+ new zk.fmt.Calendar().formatDate(this._end, format) :
					'>= ' + new zk.fmt.Calendar().formatDate(this._beg, format) :
					'<= ' + new zk.fmt.Calendar().formatDate(this._end!, format));
	}
}