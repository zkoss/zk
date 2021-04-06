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
var Dates = {
	newInstance: function (param: number | DateImpl | [number, number, number | undefined, number | undefined, number | undefined, number | undefined, number | undefined], tz) {
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

function DateImpl(this: DateImpl, m, tz): void {
	this._moment = m;
	this._timezone = tz;
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

DateImpl.prototype = {
	tz: function (v) {
		if (v) this._timezone = parseTzId(v);
		return this;
	},
	_getTzMoment: function () {
		return this._moment.tz(this._timezone);
	},
	_getUTCMoment: function () {
		return this._moment.tz('UTC');
	},
	getTimeZone: function () {
		return this._timezone;
	},
	getDate: function () {
		return this._getTzMoment().date();
	},
	getDay: function () {
		return this._getTzMoment().day();
	},
	getFullYear: function () {
		return this._getTzMoment().year();
	},
	getHours: function () {
		return this._getTzMoment().hour();
	},
	getMilliseconds: function () {
		return this._getTzMoment().millisecond();
	},
	getMinutes: function () {
		return this._getTzMoment().minute();
	},
	getMonth: function () {
		return this._getTzMoment().month();
	},
	getSeconds: function () {
		return this._getTzMoment().second();
	},
	getTime: function () {
		return this._moment.valueOf();
	},
	getTimezoneOffset: function () {
		return -this._getTzMoment().utcOffset();
	},
	getUTCDate: function () {
		return this._getUTCMoment().date();
	},
	getUTCDay: function () {
		return this._getUTCMoment().day();
	},
	getUTCFullYear: function () {
		return this._getUTCMoment().year();
	},
	getUTCHours: function () {
		return this._getUTCMoment().hour();
	},
	getUTCMilliseconds: function () {
		return this._getUTCMoment().millisecond();
	},
	getUTCMinutes: function () {
		return this._getUTCMoment().minute();
	},
	getUTCMonth: function () {
		return this._getUTCMoment().month();
	},
	getUTCSeconds: function () {
		return this._getUTCMoment().second();
	},
	getYear: function () {
		return this._getTzMoment().year() - 1900;
	},
	setDate: function (v) {
		return this._getTzMoment().date(v).valueOf();
	},
	setFullYear: function (y, m, d) {
		var mt = this._getTzMoment();
		mt.year(y);
		if (m != null) {
			mt.month(m);
			if (d != null) mt.date(d);
		}
		return mt.valueOf();
	},
	setHours: function (hr, min, sec, msec) {
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
	},
	setMilliseconds: function (v) {
		return this._getTzMoment().millisecond(v).valueOf();
	},
	setMinutes: function (v) {
		return this._getTzMoment().minute(v).valueOf();
	},
	setMonth: function (v) {
		return this._getTzMoment().month(v).valueOf();
	},
	setSeconds: function (v) {
		return this._getTzMoment().second(v).valueOf();
	},
	setTime: function (v) {
		this._moment = zk.mm(v);
		return this._moment.valueOf();
	},
	setUTCDate: function (v) {
		return this._getUTCMoment().date(v).valueOf();
	},
	setUTCFullYear: function (v) {
		return this._getUTCMoment().year(v).valueOf();
	},
	setUTCHours: function (v) {
		return this._getUTCMoment().hour(v).valueOf();
	},
	setUTCMilliseconds: function (v) {
		return this._getUTCMoment().millisecond(v).valueOf();
	},
	setUTCMinutes: function (v) {
		return this._getUTCMoment().minute(v).valueOf();
	},
	setUTCMonth: function (v) {
		return this._getUTCMoment().month(v).valueOf();
	},
	setUTCSeconds: function (v) {
		return this._getUTCMoment().second(v).valueOf();
	},
	setYear: function (v) {
		return this._getTzMoment().year(v).valueOf();
	},
	toString: function () {
		return this._getTzMoment().toString();
	},
	valueOf: function () {
		return this._moment.valueOf();
	},
	toDateString: function () {
		return '';
	},
	toTimeString: function () {
		return '';
	},
	toLocaleDateString: function () {
		return '';
	},
	toLocaleTimeString: function () {
		return '';
	},
	toUTCString: function () {
		return '';
	},
	toISOString: function () {
		return '';
	},
	toJSON: function (key) {
		return '';
	}
};