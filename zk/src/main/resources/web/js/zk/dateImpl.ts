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
			if (tz)
				tz = parseTzId(tz);
			else
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
					d.getUTCHours(), d.getUTCMinutes(), d.getUTCSeconds(), d.getUTCMilliseconds()], tz);
			}
			// Each possible type of `m` are covered by the `if-else` sequence of.
			return new DateImpl(m!, tz);
		}
	};

	export class DateImpl {
		_moment: Moment;
		_timezone: string;
		constructor(m: Moment, tz: string) {
			this._moment = m;
			this._timezone = tz;
		}
		tz(v?: string): this {
			if (v) this._timezone = parseTzId(v);
			return this;
		}
		_getTzMoment(): Moment {
			return this._moment.tz(this._timezone);
		}
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
		setDate(v: number): number {
			return this._getTzMoment().date(v).valueOf();
		}
		setFullYear(y: number, m?: number, d?: number): number {
			var mt = this._getTzMoment();
			mt.year(y);
			if (m != null) {
				mt.month(m);
				if (d != null) mt.date(d);
			}
			return mt.valueOf();
		}
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
		setMilliseconds(v: number): number {
			return this._getTzMoment().millisecond(v).valueOf();
		}
		setMinutes(v: number): number {
			return this._getTzMoment().minute(v).valueOf();
		}
		setMonth(v: number): number {
			return this._getTzMoment().month(v).valueOf();
		}
		setSeconds(v: number): number {
			return this._getTzMoment().second(v).valueOf();
		}
		setTime(v: number): number {
			this._moment = zk.mm(v);
			return this._moment.valueOf();
		}
		setUTCDate(v: number): number {
			return this._getUTCMoment().date(v).valueOf();
		}
		setUTCFullYear(v: number): number {
			return this._getUTCMoment().year(v).valueOf();
		}
		setUTCHours(v: number): number {
			return this._getUTCMoment().hour(v).valueOf();
		}
		setUTCMilliseconds(v: number): number {
			return this._getUTCMoment().millisecond(v).valueOf();
		}
		setUTCMinutes(v: number): number {
			return this._getUTCMoment().minute(v).valueOf();
		}
		setUTCMonth(v: number): number {
			return this._getUTCMoment().month(v).valueOf();
		}
		setUTCSeconds(v: number): number {
			return this._getUTCMoment().second(v).valueOf();
		}
		setYear(v: number): number {
			return this._getTzMoment().year(v).valueOf();
		}
		toString(): string {
			return this._getTzMoment().toString();
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
	}
}

function parseTzId(id: string): string {
	if (/^GMT\+([0]\d|[1][0-2]):[0]{2}$/i.test(id)) {
		return 'Etc/GMT-' + parseInt(id.substring(4, 6));
	} else if (/^GMT-([0]\d|[1][0-4]):[0]{2}$/i.test(id)) {
		return 'Etc/GMT+' + parseInt(id.substring(4, 6));
	} else {
		return id;
	}
}
zk.copy(window, dateImpl_global);