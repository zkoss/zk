/* Calendar.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:34 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
/** The date related widgets, such as datebox and calendar.
 */
//zk.$package('zul.db');

export interface MarkCalOptions {
	sameMonth?: boolean;
	silent?: boolean;
	timeout?: number;
}
interface TimeZoneWidget extends zk.Widget {
	getTimeZone?: () => string;
}
declare global {
	interface HTMLTableCellElement {
		/** @internal */
		_monofs?: number;
	}
}

function _newDate(year, month, day, bFix, tz?: string): DateImpl {
	var v = Dates.newInstance([year, month, day], tz);
	return bFix && v.getMonth() != month && v.getDate() != day ? //Bug ZK-1213: also need to check date
		Dates.newInstance([year, month + 1, 0], tz)/*last day of month*/ : v;
}

function _getTimeZone(wgt: zul.db.Calendar): string | undefined {
	var parent: TimeZoneWidget | undefined = wgt.parent,
		tz = parent?.getTimeZone?.();
	return tz ? tz : wgt._defaultTzone;
}

/** @class zul.db.Renderer
 * The renderer used to render a calendar.
 * It is designed to be overridden
 */
export var Renderer = {
	/**
	 * @returns the HTML fragment representing a day cell.
	 * By overriding this method, you could customize the look of a day cell.
	 * @defaultValue day
	 * @param cal - the calendar
	 * @param y - the year
	 * @param m - the month (between 0 to 11)
	 * @param day - the day (between 1 to 31)
	 * @param monthofs - the month offset. If the day is in the same month
	 * @since 5.0.3
	 */
	cellHTML(cal: zul.db.Calendar, y: number, m: number, day: number, monthofs: number): string {
		return String(day);
	},
	/**
	 * @returns the label of a date cell.
	 * By overriding this method, you could customize the aria-label of a day cell.
	 * @defaultValue dd MMMM, yyyy
	 * @param cal - the calendar
	 * @param y - the year
	 * @param m - the month (between 0 to 11)
	 * @param day - the day (between 1 to 31)
	 * @param monthofs - the month offset. If the day is in the same month
	 * @param dayofweek - the day of the week (between 0 to 6)
	 * @since 9.5.0
	 */
	cellAriaLabel(cal: zul.db.Calendar, y: number, m: number, day: number, monthofs: number, dayofweek: number): string {
		var localizedSymbols = cal.getLocalizedSymbols();
		return day + ' ' + localizedSymbols.FMON![m] + ', ' + y;
	},
	/**
	 * Called before {@link zul.db.Calendar#redraw} is invoked.
	 * @defaultValue does nothing
	 * @param cal - the calendar
	 * @since 5.0.3
	 */
	beforeRedraw(cal: zul.db.Calendar): void {
		// empty on purpose
	},
	/**
	 * Tests if the specified date is disabled.
	 * @defaultValue it depends on the constraint, if any
	 * @param cal - the calendar
	 * @param y - the year
	 * @param m - the month (between 0 to 11)
	 * @param v - the day (between 1 to 31)
	 * @param today - today
	 * @since 5.0.3
	 */
	disabled(cal: zul.db.Calendar, y: number, m: number, v: number, today: DateImpl): boolean {
		var d = Dates.newInstance([y, m, v, 0, 0, 0, 0], _getTimeZone(cal)),
			constraint;

		if ((constraint = cal._constraint) && typeof constraint == 'string') {

			// Bug ID: 3106676
			if ((constraint.includes('no past') && (+d - +today) / 86400000 < 0)
				|| (constraint.includes('no future') && (+today - +d) / 86400000 < 0)
				|| (constraint.includes('no today') && +today - +d == 0))
					return true;
		}

		var result = false;
		if (cal._beg && (result = (+d - +cal._beg) / 86400000 < 0))
			return result;
		if (cal._end && (result = (+cal._end - +d) / 86400000 < 0))
			return result;
		return result;
	},
	/**
	 * @returns the label of the week of year.
	 * @defaultValue the string of the value
	 * @param wgt - the calendar widget
	 * @param val - number of the week of the value
	 * @since 6.5.0
	 */
	labelOfWeekOfYear(wgt: zul.db.Calendar, val: number): string {
		return String(val);
	},
	/**
	 * @returns the title of the week of year.
	 * @defaultValue 'Wk'
	 * @param wgt - the calendar widget
	 * @since 6.5.0
	 */
	titleOfWeekOfYear(wgt: zul.db.Calendar): string {
		return 'Wk';
	},
	/**
	 * Generates the title of the content HTML.
	 * @param wgt - the calendar widget
	 * @param out - an array to output HTML fragments.
	 * @param localizedSymbols - the symbols for localization
	 * @since 6.5.3
	 */
	titleHTML(wgt: zul.db.Calendar, out: string[], localizedSymbols: zk.LocalizedSymbols): void {
		var uuidHTML = /*safe*/ wgt.uuid,
			view = wgt._view,
			val = wgt.getTime(),
			m = val.getMonth(),
			y = val.getFullYear(),
			date = val._moment.toDate(),
			localeDateTimeFormat = new Intl.DateTimeFormat(localizedSymbols.LAN_TAG, {year: 'numeric'}),
			displayYear = this._getDisplayYear(date, localizedSymbols, localeDateTimeFormat),
			yofs = y - (y % 10 + 1),
			ydec = zk.parseInt(y / 100),
			textHTML = wgt.$s('text'),
			minyear = wgt._minyear,
			maxyear = wgt._maxyear,
			endYearLength = this._getPadYearLength(wgt, localizedSymbols, localeDateTimeFormat);


		switch (view) {
		case 'day':
			out.push('<span id="', uuidHTML, '-tm" class="', textHTML, '">',
				/*safe*/ localizedSymbols.SMON![m], '</span> <span id="', uuidHTML,
					'-ty" class="', textHTML, '">', /*safe*/ displayYear, '</span>');
			break;
		case 'month':
			out.push('<span id="', uuidHTML,
					'-ty" class="', textHTML, '">', /*safe*/ displayYear, '</span>');
			break;
		case 'year':
			var yearGap = 11,
				startYear = yofs < minyear ? minyear : yofs,
				startDate = new Date(startYear, m),
				displayStartYear = this._getDisplayYear(startDate, localizedSymbols, localeDateTimeFormat, endYearLength),
				expectedEndYear = yofs + yearGap;
				endYear = expectedEndYear > maxyear ? maxyear : expectedEndYear,
				endDate = new Date(endYear, m),
				displayEndYear = this._getDisplayYear(endDate, localizedSymbols, localeDateTimeFormat, endYearLength);
			out.push('<span id="', uuidHTML, '-tyd" class="', textHTML, '">',
				/*safe*/ displayStartYear, ' - ', /*safe*/ displayEndYear, '</span>');
			break;
		case 'decade':
			// each start year of cell is ten more than previous one,
			// so the end year of last cell equals the start year of first cell add 10 * 11 + 9.
			var yearGap = 10 * 11 + 9,
				expectedStartYear = ydec * 100 - 10,
				startYear = expectedStartYear < minyear ? minyear : expectedStartYear,
				startDate = new Date(startYear, m),
				displayStartYear = this._getDisplayYear(startDate, localizedSymbols, localeDateTimeFormat, endYearLength),
				expectedEndYear = expectedStartYear + yearGap,
				endYear = expectedEndYear > maxyear ? maxyear : expectedEndYear,
				endDate = new Date(endYear, m),
				displayEndYear = this._getDisplayYear(endDate, localizedSymbols, localeDateTimeFormat, endYearLength);
			out.push('<span id="', uuidHTML, '-tyd" class="', textHTML, '">',
				/*safe*/ displayStartYear, ' - ', /*safe*/ displayEndYear, '</span>');
			break;
		}
	},
	/**
	 * Renderer the dayView for this calendar
	 * @param wgt - the calendar widget
	 * @param out - an array to output HTML fragments.
	 * @param localizedSymbols - the symbols for localization
	 * @since 6.5.0
	 */
	dayView(wgt: zul.db.Calendar, out: string[], localizedSymbols: zk.LocalizedSymbols): void {
		var uuidHTML = /*safe*/ wgt.uuid,
			sun = (7 - localizedSymbols.DOW_1ST!) % 7, sat = (6 + sun) % 7,
			wkend = wgt.$s('weekend'),
			wkday = wgt.$s('weekday'),
			cell = wgt.$s('cell');

		out.push('<table role="grid" class="', wgt.$s('body'), '" id="', uuidHTML, '-mid"',
				/*safe*/ zUtl.cellps0, '>', '<thead><tr>');
		for (var j = 0; j < 7; ++j)
			out.push('<th class="', (j == sun || j == sat) ? /*safe*/ wkend : /*safe*/ wkday,
					'" aria-label="', /*safe*/ localizedSymbols.FDOW![j], '">', /*safe*/ localizedSymbols.S2DOW![j], '</th>');
		out.push('</tr></thead><tbody>');
		for (var j = 0; j < 6; ++j) { //at most 7 rows
			out.push('<tr id="', uuidHTML, '-w', j as unknown as string, '">');
			for (var k = 0; k < 7; ++k)
				out.push('<td id="', uuidHTML, '-w', j as unknown as string, '-p', k as unknown as string, '" class="', /*safe*/ cell, ' ', (k == sun || k == sat) ? /*safe*/ wkend : /*safe*/ wkday,
						'"></td>');
			out.push('</tr>');
		}
		out.push('</tbody></table>');
	},
	/**
	 * Renderer the monthView for this calendar
	 * @param wgt - the calendar widget
	 * @param out - an array to output HTML fragments.
	 * @param localizedSymbols - the symbols for localization
	 * @since 6.5.0
	 */
	monthView(wgt: zul.db.Calendar, out: string[], localizedSymbols: zk.LocalizedSymbols): void {
		var uuid = wgt.uuid,
			cell = wgt.$s('cell');
		out.push('<table role="grid" class="', wgt.$s('body'), ' ', wgt.$s('month'),
				'" id="', /*safe*/ uuid, '-mid"', /*safe*/ zUtl.cellps0, '><tbody>');
		for (var j = 0; j < 12; ++j) {
			if (!(j % 4)) out.push('<tr>');
			out.push('<td class="', /*safe*/ cell, '" id="', /*safe*/ uuid, '-m',
				j as unknown as string, '" data-value="', j as unknown as string,
				'" aria-label="', /*safe*/ localizedSymbols.FMON![j], '">',
				/*safe*/ localizedSymbols.SMON![j], '</td>');
			if (!((j + 1) % 4)) out.push('</tr>');
		}
		out.push('</tbody></table>');
	},
	/**
	 * Renderer the yearView for this calendar
	 * @param wgt - the calendar widget
	 * @param out - an array to output HTML fragments.
	 * @param localizedSymbols - the symbols for localization
	 * @since 6.5.0
	 */
	yearView(wgt: zul.db.Calendar, out: string[], localizedSymbols: zk.LocalizedSymbols): void {
		var uuid = wgt.uuid,
			cell = wgt.$s('cell'),
			disd = wgt.$s('disabled'),
			val = wgt.getTime(),
			y = val.getFullYear(),
			yofs = y - (y % 10 + 1),
			minyear = wgt._minyear,
			maxyear = wgt._maxyear,
			localeDateTimeFormat = new Intl.DateTimeFormat(localizedSymbols.LAN_TAG, {year: 'numeric'}),
			endYearLength = this._getPadYearLength(wgt, localizedSymbols, localeDateTimeFormat);
		out.push('<table role="grid" class="', wgt.$s('body'), ' ', wgt.$s('year'), '" id="', /*safe*/ uuid, '-mid"',
				/*safe*/ zUtl.cellps0, '><tbody>');

		for (var j = 0; j < 12; ++j) {
			if (!(j % 4)) out.push('<tr>');
			if (yofs < minyear || yofs > maxyear) {
				out.push('<td class="', /*safe*/ disd, '">&nbsp;</td>');
				if (j + 1 == 12)
					out.push('</tr>');
				yofs++;
				continue;
			}
			var date = new Date(yofs, 0);
			out.push('<td class="', /*safe*/ cell, '" data-value="', yofs as unknown as string, '" id="', /*safe*/ uuid, '-y', j as unknown as string, '" >',
				/*safe*/ this._getDisplayYear(date, localizedSymbols, localeDateTimeFormat, endYearLength), '</td>');
			if (!((j + 1) % 4)) out.push('</tr>');
			yofs++;
		}
		out.push('</tbody></table>');
	},
	/**
	 * Renderer the decadeView for this calendar
	 * @param wgt - the calendar widget
	 * @param out - an array to output HTML fragments.
	 * @param localizedSymbols - the symbols for localization
	 * @since 6.5.0
	 */
	decadeView(wgt: zul.db.Calendar, out: string[], localizedSymbols: zk.LocalizedSymbols): void {
		var uuid = wgt.uuid,
			cell = wgt.$s('cell'),
			disd = wgt.$s('disabled'),
			val = wgt.getTime(),
			y = val.getFullYear(),
			ydec = zk.parseInt(y / 100),
			minyear = wgt._minyear,
			maxyear = wgt._maxyear,
			mindec = zk.parseInt(minyear / 10) * 10,
			maxdec = zk.parseInt(maxyear / 10) * 10,
			localeDateTimeFormat = new Intl.DateTimeFormat(localizedSymbols.LAN_TAG, {year: 'numeric'}),
			endYearLength = this._getPadYearLength(wgt, localizedSymbols, localeDateTimeFormat);


		out.push('<table role="grid" class="', wgt.$s('body'), ' ', wgt.$s('decade'),
				'" id="', /*safe*/ uuid, '-mid"',
			/*safe*/ zUtl.cellps0, '><tbody>');
		var temp = ydec * 100 - 10,
			selected = wgt.$s('selected');
		for (var j = 0; j < 12; ++j, temp += 10) {
			if (!(j % 4)) out.push('<tr>');
			if (temp < mindec || temp > maxdec) {
				out.push('<td class="', /*safe*/ disd, '">&nbsp;</td>');
				if (j + 1 == 12)
					out.push('</tr>');
				continue;
			}

			var startDate = new Date(temp < minyear ? minyear : temp, 0),
				endDate = new Date(temp + 9 > maxyear ? maxyear : temp + 9, 11);
			out.push('<td data-value="', temp as unknown as string, '" id="', /*safe*/ uuid, '-de', j as unknown as string, '" class="',
				/*safe*/ cell, (y >= temp && y <= (temp + 9)) ? ' ' + /*safe*/ selected : '', '" >',
				/*safe*/ this._getDisplayYear(startDate, localizedSymbols, localeDateTimeFormat, endYearLength) + ' -<br aria-hidden="true" />'
							+ /*safe*/ this._getDisplayYear(endDate, localizedSymbols, localeDateTimeFormat, endYearLength) + '</td>');
			if (!((j + 1) % 4)) out.push('</tr>');
		}
		out.push('</tbody></table>');
	},
	/**
	 * Renderer the today link for this calendar
	 * @param wgt - the calendar widget
	 * @param out - an array to output HTML fragments.
	 * @param localizedSymbols - the symbols for localization
	 * @since 8.0.0
	 */
	todayView(wgt: zul.db.Calendar, out: string[], localizedSymbols: zk.LocalizedSymbols): void {
		var val = wgt.getTodayLinkLabel();
		if (!val) {
			var tz = _getTimeZone(wgt);
			val = new zk.fmt.Calendar().formatDate(zUtl.today(!!wgt.parent, tz), wgt.getFormat(), localizedSymbols);
		}
		out.push(zUtl.encodeXML(val));
	},
	/** @internal */
	_getDisplayYear(date: Date, localizedSymbols: zk.LocalizedSymbols, localeDateTimeFormat: Intl.DateTimeFormat, padLength?: number): string { // override
		return date.getFullYear() + localizedSymbols.YDELTA! + '';
	},
	/** @internal */
	_getPadYearLength(wgt: zul.db.Calendar, localizedSymbols: zk.LocalizedSymbols, localeDateTimeFormat: Intl.DateTimeFormat): number {
		var y = wgt.getTime().getFullYear(),
			yearGap = 10 * 11 + 9,
			maxyear = wgt._maxyear,
			ydec = zk.parseInt(y / 100),
			expectedStartYear = ydec * 100 - 10,
			expectedEndYear = expectedStartYear + yearGap,
			endYear = expectedEndYear > maxyear ? maxyear : expectedEndYear,
			endYearLength = this._getDisplayYear(new Date(endYear, 0), localizedSymbols, localeDateTimeFormat).replace(/^\D+/g, '').length;
		return endYearLength;
	}
};
zul.db.Renderer = Renderer;

export interface CalendarOnChangeData {
	value?: DateImpl;
	shallClose: boolean;
	shiftView: boolean;
}
@zk.WrapClass('zul.db.Calendar')
export class Calendar extends zul.Widget {
	/** @internal */
	_view = 'day';
	/** @internal */
	_minyear = 1900; //"day", "month", "year", "decade",
	/** @internal */
	_maxyear = 2099;
	/** @internal */
	_beg?: DateImpl;
	/** @internal */
	_end?: DateImpl;
	/** @internal */
	_constraint?: string;
	/** @internal */
	_localizedSymbols?: zk.LocalizedSymbols;
	/** @internal */
	_selectedValue?: DateImpl;
	/** @internal */
	_value?: DateImpl;
	/** @internal */
	_defaultTzone?: string;
	/** @internal */
	_name?: string;
	/** @internal */
	_weekOfYear?: boolean;
	/** @internal */
	_showTodayLink?: boolean;
	efield?: HTMLInputElement;
	/** @internal */
	_fmt?: string;
	/** @internal */
	_todayLinkLabel?: string;

	constructor() {
		super();
		this.listen({onChange: this}, -1000);
	}

	/**
	 * Assigns a value to this component.
	 * @param value - the date to assign. If null, today is assumed.
	 */
	setValue(value: DateImpl, opts?: Record<string, boolean>): this {
		const o = this._value;
		this._value = value;

		if (o !== value || opts?.force) {
			var parent: TimeZoneWidget | undefined = this.parent;
			if (!parent || !parent.getTimeZone) {
				this._value.tz(this._defaultTzone);
			}
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the value that is assigned to this component.
	 */
	getValue(): DateImpl | undefined {
		return this._value;
	}

	/**
	 * Sets default time zone that this calendar belongs to.
	 * @param defaultTzone - the time zone's ID, such as "America/Los_Angeles".
	 */
	setDefaultTzone(defaultTzone: string): this {
		this._defaultTzone = defaultTzone;
		return this;
	}

	/**
	 * @returns default time zone that this calendar belongs to, such as "America/Los_Angeles".
	 */
	getDefaultTzone(): string | undefined {
		return this._defaultTzone;
	}

	/**
	 * Set the date limit for this component with yyyyMMdd format,
	 * such as 20100101 is mean Jan 01 2010
	 *
	 * <dl>
	 * <dt>Example:</dt>
	 * <dd>between 20091201 and 20091231</dd>
	 * <dd>before 20091201</dd>
	 * <dd>after 20091231</dd>
	 * </dl>
	 *
	 */
	setConstraint(constraint: string | undefined, opts?: Record<string, boolean>): this {
		const o = this._constraint;
		this._constraint = constraint;

		if (o !== constraint || opts?.force) {
			this._fixConstraint();
			// ZK-3619, this method could be called when datebox opening the calendar,
			// inServer means there is a calendar tag in zul file.
			if (this.desktop && this.inServer) {
				this.rerender();
			}
		}

		return this;
	}

	/**
	 * @returns the constraint of this component.
	 */
	getConstraint(): string | undefined {
		return this._constraint;
	}

	/**
	 * Sets the name of this component.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 *
	 * @param name - the name of this component.
	 */
	setName(name: string, opts?: Record<string, boolean>): this {
		const o = this._name;
		this._name = name;

		if (o !== name || opts?.force) {
			if (this.efield)
				this.efield.name = this._name;
		}

		return this;
	}

	/**
	 * @returns the name of this component.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * @defaultValue `null`.
	 */
	getName(): string | undefined {
		return this._name;
	}

	/**
	 * Sets whether enable to show the week number within the current year or
	 * not. [ZK EE]
	 * @since 6.5.0
	 */
	setWeekOfYear(weekOfYear: boolean, opts?: Record<string, boolean>): this {
		const o = this._weekOfYear;
		this._weekOfYear = weekOfYear;

		if (o !== weekOfYear || opts?.force) {
			if (this.desktop && zk.feature.ee)
				this.rerender();
		}

		return this;
	}

	/**
	 * @returns whether enable to show the week number within the current year or not.
	 * @defaultValue `false`
	 * @since 6.5.0
	 */
	isWeekOfYear(): boolean {
		return !!this._weekOfYear;
	}

	/**
	 * Sets whether enable to show the link that jump to today in day view
	 * @since 8.0.0
	 */
	setShowTodayLink(showTodayLink: boolean, opts?: Record<string, boolean>): this {
		const o = this._showTodayLink;
		this._showTodayLink = showTodayLink;

		if (o !== showTodayLink || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns whether enable to show the link that jump to today in day view
	 * @defaultValue `false`
	 * @since 8.0.0
	 */
	isShowTodayLink(): boolean {
		return !!this._showTodayLink;
	}

	/**
	 * Sets the label of the link that jump to today in day view
	 * @since 8.0.4
	 */
	setTodayLinkLabel(todayLinkLabel: string, opts?: Record<string, boolean>): this {
		const o = this._todayLinkLabel;
		this._todayLinkLabel = todayLinkLabel;

		if (o !== todayLinkLabel || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the label of the link that jump to today in day view
	 * @since 8.0.4
	 */
	getTodayLinkLabel(): string | undefined {
		return this._todayLinkLabel;
	}

	/**
	 * A method for component getter symmetry, it will call getValue
	 * @since 10.0.0
	 */
	getValueInZonedDateTime(): DateImpl | undefined {
		return this.getValue();
	}

	/**
	 * A method for component setter symmetry, it will call setValue
	 * @since 10.0.0
	 */
	setValueInZonedDateTime(valueInZonedDateTime: DateImpl, opts?: Record<string, boolean>): this {
		return this.setValue(valueInZonedDateTime, opts);
	}

	/**
	 * A method for component getter symmetry, it will call getValue
	 * @since 10.0.0
	 */
	getValueInLocalDateTime(): DateImpl | undefined {
		return this.getValue();
	}

	/**
	 * A method for component setter symmetry, it will call setValue
	 * @since 10.0.0
	 */
	setValueInLocalDateTime(valueInLocalDateTime: DateImpl, opts?: Record<string, boolean>): this {
		return this.setValue(valueInLocalDateTime, opts);
	}

	/**
	 * A method for component getter symmetry, it will call getValue
	 * @since 10.0.0
	 */
	getValueInLocalDate(): DateImpl | undefined {
		return this.getValue();
	}

	/**
	 * A method for component setter symmetry, it will call setValue
	 * @since 10.0.0
	 */
	setValueInLocalDate(valueInLocalDate: DateImpl, opts?: Record<string, boolean>): this {
		return this.setValue(valueInLocalDate, opts);
	}

	/**
	 * A method for component getter symmetry, it will call getValue
	 * @since 10.0.0
	 */
	getValueInLocalTime(): DateImpl | undefined {
		return this.getValue();
	}

	/**
	 * A method for component setter symmetry, it will call setValue
	 * @since 10.0.0
	 */
	setValueInLocalTime(valueInLocalTime: DateImpl, opts?: Record<string, boolean>): this {
		return this.setValue(valueInLocalTime, opts);
	}

	override redraw(out: string[], skipper?: zk.Skipper): void {
		Renderer.beforeRedraw(this);
		super.redraw(out, skipper);
	}

	onChange(evt: zk.Event): void {
		this._updFormData((evt.data as {value: DateImpl}).value);
	}

	/** @internal */
	override doKeyDown_(evt: zk.Event): void {

		const currentView = this._view;
		if (evt.key === 'PageUp') {
			// PageUp: prev month ; Shift + PageUp: prev year
			this._setView(evt.shiftKey ? 'year' : 'month');
			this._shift(-1);
		} else if (evt.key === 'PageDown') {
			// PageDown: next month ; Shift + PageDown: next year
			this._setView(evt.shiftKey ? 'year' : 'month');
			this._shift(1);
		}
		this._setView(currentView);

		var keyCode = evt.keyCode,
			ofs = keyCode == 37 ? -1 : keyCode == 39 ? 1 : keyCode == 38 ? -7 : keyCode == 40 ? 7 : 0;
		if (ofs) {
			this._shift(ofs);
			evt.stop(); // Bug ZK-2306: using the arrow keys in the calendar widget scrolls the browser window
		} else if (keyCode == 32 || keyCode == 13) {
			// pass a fake event
			this._clickDate({
				target: this,
				domTarget: jq(this.$n('mid')).find('.' + this.$s('selected'))[0],
				stop: zk.$void
			});
		}
	}

	/** @internal */
	setMinYear_(minYear: number): void {
		if (minYear) {
			var y = this.getTime().getFullYear();
			this._minyear = minYear > y ? y : (minYear > 100 ? minYear : 100);
		} else {
			this._minyear = 1900;
		}
	}

	/** @internal */
	setMaxYear_(maxYear: number): void {
		if (maxYear) {
			var y = this.getTime().getFullYear();
			this._maxyear = maxYear < y ? y : (maxYear > this._minyear ? maxYear : this._minyear);
		} else {
			this._maxyear = 2099;
		}
	}

	/** @internal */
	_shift(ofs: number, opts?: MarkCalOptions): void {
		var oldTime = this.getTime(),
			tz = _getTimeZone(this),
			shiftTime = Dates.newInstance(oldTime.getTime(), tz),
			minTime = Dates.newInstance([this._minyear, 0, 1, 0, 0, 0, 0], tz),
			maxTime = Dates.newInstance([this._maxyear, 11, 31, 23, 59, 59, 999], tz),
			today = zUtl.today(false, tz);

		switch (this._view) {
		case 'day':
			shiftTime.setDate(oldTime.getDate() + ofs);
			break;
		case 'month':
			if (ofs == 7)
				ofs = 4;
			else if (ofs == -7)
				ofs = -4;
			shiftTime.setMonth(oldTime.getMonth() + ofs);
			break;
		case 'year':
			if (ofs == 7)
				ofs = 4;
			else if (ofs == -7)
				ofs = -4;
			shiftTime.setYear(oldTime.getFullYear() + ofs);
			break;
		case 'decade':
			if (ofs == 7)
				ofs = 4;
			else if (ofs == -7)
				ofs = -4;
			ofs *= 10;
			shiftTime.setYear(oldTime.getFullYear() + ofs);
			break;
		}
		//Bug B65-ZK-1804: Constraint the shifted time should not be out of range between _minyear and _maxyear
		//Bug B96-ZK-4543: Calendar should respect the constraint while Month changing
		if (shiftTime.getTime() < minTime.getTime() || shiftTime.getTime() > maxTime.getTime() ||
			Renderer.disabled(this, shiftTime.getFullYear(), shiftTime.getMonth(), shiftTime.getDate(), today))
			return; // out of range

		this._shiftDate(this._view, ofs);

		var newTime = this.getTime();
		switch (this._view) {
		case 'day':
			if (oldTime.getYear() == newTime.getYear()
				&& oldTime.getMonth() == newTime.getMonth()) {
				opts = opts || {};
				opts.sameMonth = true; //optimize
				this._markCal(opts);
			} else {
				this.rerender(-1);
				this.focus();
			}
			break;
		case 'month':
			if (oldTime.getYear() == newTime.getYear())
				this._markCal(opts);
			else {
				this.rerender(-1);
				this.focus();
			}
			break;
		default:
			this.rerender(-1);
			this.focus();
		}
	}

	/** @internal */
	_fixConstraint(): void {
		var constraint = this._constraint || '';
		// ZK-4641: Datebox doesn't clean beginning and end at client when removing constraint
		this._beg = undefined;
		this._end = undefined;
		if (typeof constraint != 'string' || constraint == '') return;
		// B50-ZK-591: Datebox constraint combination yyyymmdd and
		// no empty cause javascript error in zksandbox
		var constraints = constraint.split(','),
			format = 'yyyyMMdd',
			len = format.length + 1,
			tz = _getTimeZone(this);
		// eslint-disable-next-line @typescript-eslint/prefer-for-of
		for (var i = 0; i < constraints.length; i++) {
			constraint = jq.trim(constraints[i]); //Bug ZK-1718: should trim whitespace
			if (constraint.startsWith('between')) {
				var j = constraint.indexOf('and', 7);
				if (j < 0 && zk.debugJS)
					zk.error('Unknown constraint: ' + constraint);
				this._beg = new zk.fmt.Calendar(undefined, this._localizedSymbols).parseDate(constraint.substring(7, j), format, undefined, undefined, undefined, tz);
				this._end = new zk.fmt.Calendar(undefined, this._localizedSymbols).parseDate(constraint.substring(j + 3, j + 3 + len), format, undefined, undefined, undefined, tz);
				if (this._beg!.getTime() > this._end!.getTime()) {
					var d = this._beg;
					this._beg = this._end;
					this._end = d;
				}
				this._beg!.setHours(0, 0, 0, 0);
				this._end!.setHours(0, 0, 0, 0);
			} else if (constraint.startsWith('before_') || constraint.startsWith('after_')) {
				continue; //Constraint start with 'before_' and 'after_' means errorbox position, skip it
			} else if (constraint.startsWith('before')) {
				this._end = new zk.fmt.Calendar(undefined, this._localizedSymbols).parseDate(constraint.substring(6, 6 + len), format, undefined, undefined, undefined, tz);
				this._end!.setHours(0, 0, 0, 0);
			} else if (constraint.startsWith('after')) {
				this._beg = new zk.fmt.Calendar(undefined, this._localizedSymbols).parseDate(constraint.substring(5, 5 + len), format, undefined, undefined, undefined, tz);
				this._beg!.setHours(0, 0, 0, 0);
			}
		}
	}

	/**
	 * @returns the format of this component.
	 */
	getFormat(): string {
		return this._fmt || 'yyyy/MM/dd';
	}

	/** @internal */
	_updFormData(formData: DateImpl): void {
		let val = new zk.fmt.Calendar().formatDate(formData, this.getFormat(), this._localizedSymbols);
		if (this._name) {
			val = val || '';
			if (!this.efield)
				this.efield = jq.newHidden(this._name, val, this.$n());
			else
				this.efield.value = val;
		}
	}

	/** @internal */
	override focus_(timeout: number): boolean {
		if (this._view != 'decade')
			this._markCal({timeout: timeout});
		else {
			var anc: HTMLAnchorElement | undefined;
			if (anc = this.getAnchor_())
				this._doFocus(anc, true);
		}
		return true;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var node = this.$n(),
			title = this.$n('title'),
			mid = this.$n('mid'),
			left = this.$n('left'),
			right = this.$n('right'),
			today = this.$n('today');
		if (this._view != 'decade')
			this._markCal({silent: true});

		this.domListen_(title!, 'onClick', '_changeView')
			.domListen_(mid!, 'onClick', '_clickDate')
			.domListen_(left!, 'onClick', '_clickArrow')
			.domListen_(right!, 'onClick', '_clickArrow')
			.domListen_(today!, 'onClick', '_clickToday')
			.domListen_(node!, 'onMousewheel');

		this._updFormData(this.getTime());
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var node = this.$n(),
			title = this.$n('title'),
			mid = this.$n('mid'),
			left = this.$n('left'),
			right = this.$n('right'),
			today = this.$n('today');
		this.domUnlisten_(title!, 'onClick', '_changeView')
			.domUnlisten_(mid!, 'onClick', '_clickDate')
			.domUnlisten_(left!, 'onClick', '_clickArrow')
			.domUnlisten_(right!, 'onClick', '_clickArrow')
			.domUnlisten_(today!, 'onClick', '_clickToday')
			.domUnlisten_(node!, 'onMousewheel');
		super.unbind_(skipper, after, keepRod);
		this.efield = undefined;
	}

	override rerender(skipper?: zk.Skipper | number): this {
		if (this.desktop) {
			var s = this.$n()!.style,
				w = s.width,
				h = s.height,
				result = super.rerender(skipper);
			s = this.$n()!.style;
			s.width = w;
			s.height = h;
			return result;
		}
		return this;
	}

	/** @internal */
	_clickArrow(evt: zk.Event): void {
		if (zk.animating()) return; // ignore
		var node = jq.nodeName(evt.domTarget, 'a') ? evt.domTarget
					: jq(evt.domTarget).parent('a')[0];
		if (jq(node).attr('disabled'))
			return;
		this._shiftView(jq(node).hasClass(this.$s('left')) ? -1 : 1);
		//ZK-2679: prevent default behavior of clicking anchor
		evt.stop();
	}

	/** @internal */
	_clickToday(): void {
		this.setValue(zUtl.today(!!this.parent, _getTimeZone(this)));
		this._setView('day');
	}

	/** @internal */
	_shiftView(ofs: number, disableAnima?: boolean): void {
		switch (this._view) {
		case 'day':
			this._shiftDate('month', ofs);
			break;
		case 'month':
			this._shiftDate('year', ofs);
			break;
		case 'year':
			this._shiftDate('year', ofs * 10);
			break;
		case 'decade':
			this._shiftDate('year', ofs * 100);
			break;
		}
		if (!disableAnima)
			this._setView(this._view, ofs);
		else {
			this.rerender(-1);
			this.focus();
		}
	}

	/** @internal */
	_doMousewheel(evt: zk.Event, intDelta: number): void {
		if (jq(this.$n(-intDelta > 0 ? 'right' : 'left')).attr('disabled'))
			return;
		this._shiftView(intDelta > 0 ? -1 : 1, true);
		evt.stop();
	}

	/**
	 * @returns the Date that is assigned to this component.
	 *  <p>returns today if value is null
	 */
	getTime(): DateImpl {
		return this._value || zUtl.today(this.getFormat(), _getTimeZone(this));
	}

	/** @internal */
	_setTime(y: number | undefined, m?: number, d?: number, fireOnChange?: boolean): this {
		var dateobj = this.getTime(),
			year = y != null ? y : dateobj.getFullYear(),
			month = m != null ? m : dateobj.getMonth(),
			day = d != null ? d : dateobj.getDate(),
			tz = _getTimeZone(this),
			val = new zk.fmt.Calendar().escapeDSTConflict(_newDate(year, month, day, d == null, tz), tz); // B70-ZK-2382

		this._value = val;
		this._selectedValue = val;
		if (fireOnChange)
			this.fire('onChange', {value: val});
		return this;
	}

	// calendar-ctrl.js will override this function
	/** @internal */
	_clickDate(evt: Pick<zk.Event, 'target' | 'domTarget' | 'stop'>): void {
		var target = evt.domTarget as HTMLTableCellElement | undefined,
			val: number;
		for (; target; target = target.parentNode as HTMLTableCellElement | undefined)
			try { //Note: data-dt is also used in mold/calendar.js
				if ((val = jq(target).data('value') as number) !== undefined) {
					val = zk.parseInt(val);
					break;
				}
			} catch (e) {
				continue; //skip
			}
		this._chooseDate(target, val!);
		var anc: HTMLAnchorElement | undefined;
		if (anc = this.getAnchor_())
			this._doFocus(anc, true);

		evt.stop();
	}

	/** @internal */
	_chooseDate(target: HTMLTableCellElement | undefined, val: number): void {
		if (target && !jq(target).hasClass(this.$s('disabled'))) {
			var cell = target,
				dateobj = this.getTime();
			switch (this._view) {
			case 'day':
				var oldTime = this.getTime();
				this._setTime(undefined, cell._monofs != null && cell._monofs != 0 ?
						dateobj.getMonth() + cell._monofs : undefined, val, true /*fire onChange */);
				var newTime = this.getTime();
				if (oldTime.getYear() == newTime.getYear()
					&& oldTime.getMonth() == newTime.getMonth()) {
						this._markCal({sameMonth: true}); // optimize
				} else {
					this.rerender(-1);
					this.focus();
				}
				break;
			case 'month':
				this._setTime(undefined, val);
				this._setView('day');
				break;
			case 'year':
				this._setTime(val);
				this._setView('month');
				break;
			case 'decade':
				//Decade mode Set Year Also
				this._setTime(val);
				this._setView('year');
				break;
			}
		}
	}

	/** @internal */
	_shiftDate(opt: string, ofs: number, ignoreUpdate?: boolean): DateImpl {
		var dateobj = this.getTime(),
			year = dateobj.getFullYear(),
			month = dateobj.getMonth(),
			day = dateobj.getDate(),
			tz = _getTimeZone(this),
			nofix;
		switch (opt) {
		case 'day':
			day += ofs;
			nofix = true;
			break;
		case 'month':
			month += ofs;
			break;
		case 'year':
			year += ofs;
			break;
		case 'decade':
			year += ofs;
			break;
		}
		var newTime = _newDate(year, month, day, !nofix, tz);
		if (!ignoreUpdate) {
			this._value = newTime;
			if (!Renderer.disabled(this, year, month, day, zUtl.today(false, this._defaultTzone))) {
				this._selectedValue = newTime;
				this.fire('onChange', {value: this._selectedValue, shallClose: false, shiftView: true});
			}
		}
		return newTime;
	}

	/** @internal */
	_changeView(evt: zk.Event): void {
		var tm = this.$n('tm'),
			ty = this.$n('ty'),
			tyd = this.$n('tyd'),
			title = this.$n('title');
		if (evt.domTarget == tm)
			this._setView('month');
		else if (evt.domTarget == ty)
			this._setView('year');
		else if (evt.domTarget == tyd)
			this._setView('decade');
		else if (evt.domTarget == title) {
			if (tm == null && ty != null)
				this._setView('year');
			else if (ty == null)
				this._setView('decade');
			else
				this._setView('month');
		}
		evt.stop();
	}

	/** @internal */
	_setView(view: string, force?: number): this {
		// check whether to disable the arrow
		/** @internal */
		function _updateArrow(wgt: zul.db.Calendar): void {
			if (wgt.isOutOfRange(true)) {
				jq(wgt.$n('left')).attr('disabled', 'disabled');
			} else {
				jq(wgt.$n('left')).removeAttr('disabled');
			}
			if (wgt.isOutOfRange()) {
				jq(wgt.$n('right')).attr('disabled', 'disabled');
			} else {
				jq(wgt.$n('right')).removeAttr('disabled');
			}
		}
		type ComputedView = (wgt: zul.db.Calendar, out: string[], localizedSymbols: zk.LocalizedSymbols) => void;

		if (this._view != view) {
			this._view = view;

			var out = new zk.Buffer(),
				localizedSymbols = this.getLocalizedSymbols();

			(Renderer[view + 'View'] as ComputedView)(this, out, localizedSymbols);

			jq(this.$n('mid')).after(out.join('')).remove();

			var after = [];
			// unlisten event
			this.unbind_(undefined, after);
			// listen event
			this.bind_(this.desktop, undefined, after);

			out = []; // reset
			Renderer.titleHTML(this, out, localizedSymbols);
			// eslint-disable-next-line @microsoft/sdl/no-html-method
			jq(this.$n('title')).html(DOMPurify.sanitize(out.join('')));
			jq(this.$n('mid')).transition({scale: 0}, 0).transition({scale: 1}, this.animationSpeed_() as number);

			_updateArrow(this);

			var anc = this.getAnchor_();
			if (anc)
				this._doFocus(anc, true);

		} else if (force) {
			var out: string[] = [],
				localizedSymbols = this.getLocalizedSymbols(),
				oldMid = this.$n('mid')!,
				isLeft = force == -1,
				width = oldMid.offsetWidth,
				x = width * -1,
				self = this,
				animaCSSHTML = this.$s('anima'),
				todayBtn = this.isShowTodayLink() ? jq(this.$n('today')).parent() : undefined;

			if (todayBtn) todayBtn.is(':hidden') && todayBtn.css('display', 'none');

			(Renderer[view + 'View'] as ComputedView)(this, out, localizedSymbols);

			jq(oldMid).after(/*safe*/ '<div style="height:' + jq.px(oldMid.offsetHeight)
					+ ';width:' + jq.px(width) + '" class="' + animaCSSHTML
					+ '"><div class="' + animaCSSHTML + '-inner"></div');

			var animaInner = oldMid.nextSibling!.firstChild!;
			jq(animaInner).append(oldMid);
			oldMid = animaInner.firstChild as HTMLElement;
			if (isLeft) {
				jq(oldMid).before(out.join('')).remove();
			} else {
				jq(oldMid).after(out.join('')).remove();
			}

			// clear for _makrCal to get the latest reference
			this.clearCache();
			if (view != 'decade')
				this._markCal();

			var newMid: HTMLElement;
			if (isLeft) {
				jq(animaInner.firstChild!).after(oldMid);
				newMid = oldMid.previousSibling as HTMLElement;
				jq(animaInner).css({left: x});
				x = 0;
			} else {
				jq(animaInner.firstChild!).before(oldMid);
				newMid = oldMid.nextSibling as HTMLElement;
			}

			jq(animaInner).animate({left: x}, {
				duration: this.animationSpeed_(),
				always: function (/*callback*/) {
					self.domUnlisten_(oldMid, 'onClick', '_clickDate');
					jq(animaInner.parentNode!).after(newMid).remove();
					self.domListen_(newMid, 'onClick', '_clickDate');
					var out = []; // reset
					Renderer.titleHTML(self, out, localizedSymbols);
					// eslint-disable-next-line @microsoft/sdl/no-html-method
					jq(self.$n('title')).html(DOMPurify.sanitize(out.join('')));
					self.clearCache();
					if (todayBtn) todayBtn.css('display', '');
				}
			});

			_updateArrow(this);
		}
		return this;
	}

	getLocalizedSymbols(): zk.LocalizedSymbols {
		return this._localizedSymbols || {
			DOW_1ST: zk.DOW_1ST,
			MINDAYS: zk.MINDAYS,
				ERA: zk.ERA,
			 YDELTA: zk.YDELTA,
			LAN_TAG: zk.LAN_TAG,
			   SDOW: zk.SDOW,
			  S2DOW: zk.S2DOW,
			   FDOW: zk.FDOW,
			   SMON: zk.SMON,
			  S2MON: zk.S2MON,
			   FMON: zk.FMON,
				APM: zk.APM
		};
	}

	/**
	 * Check whether the date is out of range between 1900~2100 years
	 * @param left - it is used for the left arrow button
	 * @param date - the date object for the range if null, the current value
	 * of {@link getTime} is assumed.
	 * @returns if true it means the date is out of range.
	 * @since 6.5.3
	 */
	isOutOfRange(left?: boolean, date?: Date): boolean {
		var view = this._view,
			val = date || this.getTime(),
			y = val.getFullYear(),
			yofs = y - (y % 10 + 1),
			ydec = zk.parseInt(y / 100),
			minyear = this._minyear,
			maxyear = this._maxyear,
			mincen = zk.parseInt(minyear / 100) * 100,
			maxcen = zk.parseInt(maxyear / 100) * 100;

		if (view == 'decade') {
			var value = ydec * 100;
			return left ? value == mincen : value == maxcen;
		} else if (view == 'year') {
			var value = yofs;
			return left ? value < minyear : value + 10 >= maxyear;
		} else if (view == 'day') {
			var value = y,
				m = val.getMonth();
			return left ? value <= minyear && m == 0 : value >= maxyear && m == 11;
		} else {
			var value = y;
			return left ? value <= minyear : value >= maxyear;
		}

	}

	/** @internal */
	_markCal(opts?: MarkCalOptions): void {
		this._markCal0(opts);
		var anc: HTMLAnchorElement | undefined;
		if ((anc = this.getAnchor_()) && (!opts || !opts.silent))
			this._doFocus(anc, opts && opts.timeout);
	}

	// calendar-ctrl.js will override this function
	/** @internal */
	_markCal0(opts?: MarkCalOptions): void {
		var	seldate = this.getTime(),
			m = seldate.getMonth(),
			mid = this.$n('mid')!,
			$mid = jq(mid),
			seldClass = this.$s('selected'),
			y = seldate.getFullYear(),
			minyear = this._minyear,
			maxyear = this._maxyear;

		if (this._view == 'day') {
			//B70-ZK-2477, if zul declares the locale, don't use system's locale
			var DOW_1ST = zk.DOW_1ST;
			if (this._localizedSymbols && this._localizedSymbols.DOW_1ST != undefined) {
				DOW_1ST = this._localizedSymbols.DOW_1ST;
			}
			var d = seldate.getDate(),
				tz = seldate.getTimeZone(),
				v = Dates.newInstance([y, m, 1], 'UTC').getDay() - DOW_1ST,
				last = Dates.newInstance([y, m + 1, 0], 'UTC').getDate(), //last date of this month
				prev = Dates.newInstance([y, m, 0], 'UTC').getDate(), //last date of previous month
				today = zUtl.today(false, tz), //no time part
				outsideClass = this.$s('outside'),
				disdClass = this.$s('disabled');

			$mid.find('.' + seldClass).removeClass(seldClass);
			if (!opts || !opts.sameMonth) {
				$mid.find('.' + outsideClass).removeClass(outsideClass);
				$mid.find('.' + disdClass).removeClass(disdClass);
			}

			if (v < 0) v += 7;
			for (var j = 0, cur = -v + 1; j < 6; ++j) {
				var week = this.$n<HTMLTableRowElement>('w' + j);
				if (week != null) {
					for (var k = 0; k < 7; ++k, ++cur) {
						v = cur <= 0 ? prev + cur : cur <= last ? cur : cur - last;
						if (k == 0 && cur > last)
							week.style.display = 'none';
						else {
							if (k == 0) week.style.display = '';
							var	monofs = cur <= 0 ? -1 : cur <= last ? 0 : 1,
								bSel = cur == d;

							// Bug B65-ZK-1804: check whether the date is out of range
							if (y >= maxyear && m == 11 && monofs == 1
									|| y <= minyear && m == 0 && monofs == -1)
								continue;

							var $cell = jq(week.cells[k]),
								isSelectDisabled = Renderer.disabled(this, y, m + monofs, v, today);

							$cell[0]._monofs = monofs;
							if (bSel && !isSelectDisabled) {
								$cell.addClass(seldClass);
							}


							//not same month
							if (!opts || !opts.sameMonth) { // optimize
								if (monofs) {
									$cell.addClass(outsideClass);
								}
								if (isSelectDisabled) {
									$cell.addClass(disdClass);
								}
								// eslint-disable-next-line @microsoft/sdl/no-inner-html
								$cell[0].innerHTML = DOMPurify.sanitize(Renderer.cellHTML(this, y, m + monofs, v, monofs)) as unknown as string;
								$cell[0].setAttribute('aria-label', Renderer.cellAriaLabel(this, y, m + monofs, v, monofs, k));
								$cell.data('value', v);
							}
						}
					}
				}
			}
		} else {
			var isMon = this._view == 'month',
				field = isMon ? 'm' : 'y',
				index = isMon ? m : y % 10 + 1,
				node: HTMLElement | undefined;

			$mid.find('.' + seldClass).removeClass(seldClass);

			for (var j = 0; j < 12; ++j)
				if (index == j && (node = this.$n(field + j)))
					jq(node).addClass(seldClass);
		}
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var cls = '';
		if (this._weekOfYear)
			cls += this.$s('wk') + ' ';
		return cls + super.domClass_(no);
	}

	/** @internal */
	animationSpeed_(): 'slow' | 'fast' | number {
		return zk(this).getAnimationSpeed();
	}

	/** @internal */
	getAnchor_(): HTMLAnchorElement | undefined {
		return this.$n('a');
	}

	// Bug 2936994, fixed unnecessary setting scrollTop
	/** @internal */
	_doFocus(n: HTMLElement, timeout?: number | boolean): void {
		if (zk.gecko && timeout)
			setTimeout(() => zk(n).focus()); // FIXME: missing timeout argument?
		else
			zk(n).focus();
	}
}