/* dateImpl.ts

	Purpose:
		date with timezone information
	Description:
		Methods defined in DateImpl object is pretty similar to js Date object.
		The difference is there is timezone information in DateImpl object,
		but isn't in js Date object.
	History:
		Tue Dec 5 12:35:34     2017, Created by bobpeng

Copyright (C) 2017 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
export namespace dateImpl_global {
	export const Dates = {
		newInstance(param?: number | DateImpl | Parameters<DateConstructor['UTC']>, tz?: string): DateImpl {
			let m: Moment;
			if (!tz)
				tz = zk.mm.tz.guess();
			if (arguments.length == 0) {
				m = zk.mm();
			} else if (typeof param == 'number') {
				m = zk.mm(param);
			} else if (param instanceof DateImpl) {
				m = zk.mm(param._moment);
				tz = param._timezone;
			} else if (param instanceof Array) { // [y, m, d, hr, min, sec, millisec]
				var d = new Date(Date.UTC(...param));
				if (param[0] < 100) d.setUTCFullYear(param[0]); //ZK-4292: incorrect year when the year is less than 100
				m = zk.mm.tz([d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate(),
					d.getUTCHours(), d.getUTCMinutes(), d.getUTCSeconds(), d.getUTCMilliseconds()], parseTzId(tz));
			}
			// Each possible type of `m` are covered by the `if-else` sequence of.
			return new DateImpl(m!, tz);
		}
	};

	export class DateImpl {
		/** @internal */
		_moment: Moment;
		/** @internal */
		_timezone: string;
		constructor(m: Moment, tz: string) {
			this._moment = m;
			this._timezone = parseTzId(tz);
			this._offsetIfNonWholeHour(tz, false);
		}
		tz(v?: string): this {
			if (v) {
				this._timezone = parseTzId(v);
				this._offsetIfNonWholeHour(v, true);
			}
			return this;
		}
		/** @internal */
		_getTzMoment(): Moment {
			return this._moment.tz(this._timezone);
		}
		/** @internal */
		_getUTCMoment(): Moment {
			return this._moment.tz('UTC');
		}
		getTimeZone(): string {
			return this._timezone;
		}
		getDate(): number {
			return this._getTzMoment().date();
		}
		getDay(): number {
			return this._getTzMoment().day();
		}
		getFullYear(): number {
			return this._getTzMoment().year();
		}
		getHours(): number {
			return this._getTzMoment().hour();
		}
		getMilliseconds(): number {
			return this._getTzMoment().millisecond();
		}
		getMinutes(): number {
			return this._getTzMoment().minute();
		}
		getMonth(): number {
			return this._getTzMoment().month();
		}
		getSeconds(): number {
			return this._getTzMoment().second();
		}
		getTime(): number {
			return this._moment.valueOf();
		}
		getTimezoneOffset(): number {
			return -this._getTzMoment().utcOffset();
		}
		getUTCDate(): number {
			return this._getUTCMoment().date();
		}
		getUTCDay(): number {
			return this._getUTCMoment().day();
		}
		getUTCFullYear(): number {
			return this._getUTCMoment().year();
		}
		getUTCHours(): number {
			return this._getUTCMoment().hour();
		}
		getUTCMilliseconds(): number {
			return this._getUTCMoment().millisecond();
		}
		getUTCMinutes(): number {
			return this._getUTCMoment().minute();
		}
		getUTCMonth(): number {
			return this._getUTCMoment().month();
		}
		getUTCSeconds(): number {
			return this._getUTCMoment().second();
		}
		getYear(): number {
			return this._getTzMoment().year() - 1900;
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setDate(date: number): number {
			return this._getTzMoment().date(date).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setFullYear(y: number, m?: number, d?: number): number {
			var mt = this._getTzMoment();
			mt.year(y);
			if (m != null) {
				mt.month(m);
				if (d != null) mt.date(d);
			}
			return mt.valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setHours(hr: number, min?: number, sec?: number, msec?: number): number {
			var mt = this._getTzMoment();
			mt.hour(hr);
			if (min != null) {
				mt.minute(min);
				if (sec != null) {
					mt.second(sec);
					if (msec != null) mt.millisecond(msec);
				}
			}
			return mt.valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setMilliseconds(milliseconds: number): number {
			return this._getTzMoment().millisecond(milliseconds).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setMinutes(minutes: number): number {
			return this._getTzMoment().minute(minutes).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setMonth(month: number): number {
			return this._getTzMoment().month(month).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setSeconds(seconds: number): number {
			return this._getTzMoment().second(seconds).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setTime(time: number): number {
			this._moment = zk.mm(time);
			return this._moment.valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setUTCDate(uTCDate: number): number {
			return this._getUTCMoment().date(uTCDate).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setUTCFullYear(uTCFullYear: number): number {
			return this._getUTCMoment().year(uTCFullYear).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setUTCHours(uTCHours: number): number {
			return this._getUTCMoment().hour(uTCHours).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setUTCMilliseconds(uTCMilliseconds: number): number {
			return this._getUTCMoment().millisecond(uTCMilliseconds).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setUTCMinutes(uTCMinutes: number): number {
			return this._getUTCMoment().minute(uTCMinutes).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setUTCMonth(uTCMonth: number): number {
			return this._getUTCMoment().month(uTCMonth).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setUTCSeconds(uTCSeconds: number): number {
			return this._getUTCMoment().second(uTCSeconds).valueOf();
		}
		// eslint-disable-next-line zk/javaStyleSetterSignature
		setYear(year: number): number {
			return this._getTzMoment().year(year).valueOf();
		}
		toString(): string {
			return String(this._getTzMoment());
		}
		valueOf(): number {
			return this._moment.valueOf();
		}
		toDateString(): string {
			return '';
		}
		toTimeString(): string {
			return '';
		}
		toLocaleDateString(): string {
			return '';
		}
		toLocaleTimeString(): string {
			return '';
		}
		toUTCString(): string {
			return '';
		}
		toISOString(): string {
			return '';
		}
		toJSON(key: never): string {
			return '';
		}
		/** @internal */
		_offsetIfNonWholeHour(id: string, keepLocalTime: boolean): void {
			if (gmtNonWholeHour.test(id)) {
				const sign = id.charAt(3) === '+' ? '-' : '+',
					offset = sign + id.substring(4);
				if (this._moment.utcOffset() === 0) // offset only if the utcOffset() is NOT set
					this._moment.utcOffset(offset, keepLocalTime);
			}
		}
	}
}

/**
 * Regex to check if the timezone is GMT with whole hour offset.
 */
const gmtNonWholeHour = /^GMT([+-])(\d{2}):(?!00$)(\d{2})$/i;

function parseTzId(id: string): string {
	// non-whole-hour GMT case, return UTC and set utcOffset() later
	return gmtNonWholeHour.test(id) ? 'UTC' : id;
}

zk.copy(window, dateImpl_global);