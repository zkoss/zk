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
import {default as zk} from './zk';

export const Dates = {
	newInstance(param?: number | DateImpl | Parameters<DateConstructor['UTC']>, tz?: string): DateImpl {
		var m;
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
			var d = new Date(Date.UTC.apply(null, param));
			if (param[0] < 100) d.setUTCFullYear(param[0]); //ZK-4292: incorrect year when the year is less than 100
			m = zk.mm.tz([d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate(),
				d.getUTCHours(), d.getUTCMinutes(), d.getUTCSeconds(), d.getUTCMilliseconds()], tz);
		}
		return new DateImpl(m, tz);
	}
};
window.Dates = Dates;

export class DateImpl {
	public _moment: Moment;
	public _timezone: string;
	public constructor(m: Moment, tz: string) {
		this._moment = m;
		this._timezone = tz;
	}
	public tz(v?: string): this {
		if (v) this._timezone = parseTzId(v);
		return this;
	}
	public _getTzMoment(): Moment {
		return this._moment.tz(this._timezone);
	}
	public _getUTCMoment(): Moment {
		return this._moment.tz('UTC');
	}
	public getTimeZone(): string {
		return this._timezone;
	}
	public getDate(): number {
		return this._getTzMoment().date();
	}
	public getDay(): number {
		return this._getTzMoment().day();
	}
	public getFullYear(): number {
		return this._getTzMoment().year();
	}
	public getHours(): number {
		return this._getTzMoment().hour();
	}
	public getMilliseconds(): number {
		return this._getTzMoment().millisecond();
	}
	public getMinutes(): number {
		return this._getTzMoment().minute();
	}
	public getMonth(): number {
		return this._getTzMoment().month();
	}
	public getSeconds(): number {
		return this._getTzMoment().second();
	}
	public getTime(): number {
		return this._moment.valueOf();
	}
	public getTimezoneOffset(): number {
		return -this._getTzMoment().utcOffset();
	}
	public getUTCDate(): number {
		return this._getUTCMoment().date();
	}
	public getUTCDay(): number {
		return this._getUTCMoment().day();
	}
	public getUTCFullYear(): number {
		return this._getUTCMoment().year();
	}
	public getUTCHours(): number {
		return this._getUTCMoment().hour();
	}
	public getUTCMilliseconds(): number {
		return this._getUTCMoment().millisecond();
	}
	public getUTCMinutes(): number {
		return this._getUTCMoment().minute();
	}
	public getUTCMonth(): number {
		return this._getUTCMoment().month();
	}
	public getUTCSeconds(): number {
		return this._getUTCMoment().second();
	}
	public getYear(): number {
		return this._getTzMoment().year() - 1900;
	}
	public setDate(v: number): number {
		return this._getTzMoment().date(v).valueOf();
	}
	public setFullYear(y: number, m?: number, d?: number): number {
		var mt = this._getTzMoment();
		mt.year(y);
		if (m != null) {
			mt.month(m);
			if (d != null) mt.date(d);
		}
		return mt.valueOf();
	}
	public setHours(hr: number, min?: number, sec?: number, msec?: number): number {
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
	public setMilliseconds(v: number): number {
		return this._getTzMoment().millisecond(v).valueOf();
	}
	public setMinutes(v: number): number {
		return this._getTzMoment().minute(v).valueOf();
	}
	public setMonth(v: number): number {
		return this._getTzMoment().month(v).valueOf();
	}
	public setSeconds(v: number): number {
		return this._getTzMoment().second(v).valueOf();
	}
	public setTime(v: number): number {
		this._moment = zk.mm(v);
		return this._moment.valueOf();
	}
	public setUTCDate(v: number): number {
		return this._getUTCMoment().date(v).valueOf();
	}
	public setUTCFullYear(v: number): number {
		return this._getUTCMoment().year(v).valueOf();
	}
	public setUTCHours(v: number): number {
		return this._getUTCMoment().hour(v).valueOf();
	}
	public setUTCMilliseconds(v: number): number {
		return this._getUTCMoment().millisecond(v).valueOf();
	}
	public setUTCMinutes(v: number): number {
		return this._getUTCMoment().minute(v).valueOf();
	}
	public setUTCMonth(v: number): number {
		return this._getUTCMoment().month(v).valueOf();
	}
	public setUTCSeconds(v: number): number {
		return this._getUTCMoment().second(v).valueOf();
	}
	public setYear(v: number): number {
		return this._getTzMoment().year(v).valueOf();
	}
	public toString(): string {
		return this._getTzMoment().toString();
	}
	public valueOf(): number {
		return this._moment.valueOf();
	}
	public toDateString(): string {
		return '';
	}
	public toTimeString(): string {
		return '';
	}
	public toLocaleDateString(): string {
		return '';
	}
	public toLocaleTimeString(): string {
		return '';
	}
	public toUTCString(): string {
		return '';
	}
	public toISOString(): string {
		return '';
	}
	public toJSON(key): string {
		return '';
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

window.DateImpl = DateImpl;