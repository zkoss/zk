/* Datebox.ts

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
var globallocalizedSymbols: Record<string, zk.LocalizedSymbols> = {},
	_quotePattern = /'/g, // move pattern string here to avoid jsdoc failure
	_innerDateFormat = 'yyyy/MM/dd ';

@zk.WrapClass('zul.db.Datebox')
export class Datebox extends zul.inp.FormatWidget<DateImpl> {
	/** @internal */
	_buttonVisible = true;
	/** @internal */
	_lenient = true;
	/** @internal */
	_strictDate = false;
	/** @internal */
	_selectLevel: 'day' | 'month' | 'year' | 'decade' | 'today' = 'day';
	/** @internal */
	_closePopupOnTimezoneChange = true;
	/** @internal */
	_pop: CalendarPop;
	/** @internal */
	_tm: CalendarTime;
	// defSet will generate `_timeZone` but actually `_timezone` is used in code.
	// Debugger in ZKDemo shows that both properties are created and agree.
	// However, the server should expect `_timeZone`.
	/** @internal */
	_timeZone?: string;
	/** @internal */
	_constraint?: string;
	/** @internal */
	_displayedTimeZones?: string;
	/** @internal */
	_dtzones?: string[];
	/** @internal */
	_unformater?: string;
	/** @internal */
	_weekOfYear?: boolean;
	/** @internal */
	_showTodayLink?: boolean;
	/** @internal */
	_todayLinkLabel?: string;
	/** @internal */
	_defaultDateTime?: DateImpl;
	/** @internal */
	_refDate?: DateImpl;
	/** @internal */
	_timeZonesReadonly?: boolean;
	/** @internal */
	static _unformater?: zul.db.Unformater;
	localizedFormat?: string;
	/** @internal */
	_position?: string;

	constructor() {
		super();
		this.listen({ onChange: this }, -1000);
		this._pop = new zul.db.CalendarPop();
		this._tm = new zul.db.CalendarTime();
		this.appendChild(this._pop);
		this.appendChild(this._tm);
	}

	setPosition(position: string): this {
		this._position = position;
		return this;
	}

	getPosition(): string | undefined {
		return this._position;
	}

	/**
	 * Sets whether the button (on the right of the textbox) is visible.
	 */
	setButtonVisible(buttonVisible: boolean, opts?: Record<string, boolean>): this {
		const o = this._buttonVisible;
		this._buttonVisible = buttonVisible;

		if (o !== buttonVisible || opts?.force) {
			zul.inp.RoundUtl.buttonVisible(this, buttonVisible);
		}

		return this;
	}

	/**
	 * @returns whether the button (on the right of the textbox) is visible.
	 * @defaultValue `true`.
	 */
	isButtonVisible(): boolean {
		return this._buttonVisible;
	}

	/**
	 * Sets the date format.
	 * <p>The following pattern letters are defined:
	 * <table border="0" cellspacing="3" cellpadding="0">
	 * <tr bgcolor="#ccccff">
	 * <th align="left">Letter
	 * <th align="left">Date or Time Component
	 * <th align="left">Presentation
	 * <th align="left">Examples
	 * <tr><td>`G`
	 * <td>Era designator
	 * <td>Text</a>
	 * <td>`AD`
	 * <tr bgcolor="#eeeeff">
	 * <td>`y`
	 * <td>Year
	 * <td>Year</a>
	 * <td>`1996`; `96`
	 * <tr><td>`M`
	 * <td>Month in year
	 * <td>Month</a>
	 * <td>`July`; `Jul`; `07`
	 * <tr bgcolor="#eeeeff">
	 * <td>`w`
	 * <td>Week in year (starting at 1)
	 * <td>Number</a>
	 * <td>`27`
	 * <tr><td>`W`
	 * <td>Week in month (starting at 1)
	 * <td>Number</a>
	 * <td>`2`
	 * <tr bgcolor="#eeeeff">
	 * <td>`D`
	 * <td>Day in year (starting at 1)
	 * <td>Number</a>
	 * <td>`189`
	 * <tr><td>`d`
	 * <td>Day in month (starting at 1)
	 * <td>Number</a>
	 * <td>`10`
	 * <tr bgcolor="#eeeeff">
	 * <td>`F`
	 * <td>Day of week in month
	 * <td>Number</a>
	 * <td>`2`
	 * <tr><td>`E`
	 * <td>Day in week
	 * <td>Text</a>
	 * <td>`Tuesday`; `Tue`
	 * </table>
	 * @param format - the pattern.
	 */
	override setFormat(format: string, opts?: Record<string, boolean>): this {
		const o = this._format;
		this._format = format;

		if (o !== format || opts?.force) {
			if (this._pop) {
				this._pop.setFormat(this._format);
				if (this._value)
					this._value = this._pop.getTime();
			}
			var inp = this.getInputNode();
			if (inp)
				inp.value = this.getText();
		}

		return this;
	}

	/**
	 * @returns the full date format of the specified format
	 */
	override getFormat(): string | undefined {
		return this._format;
	}

	/**
	 * Sets the constraint.
	 * @defaultValue `null` (means no constraint all all).
	 */
	override setConstraint(constraint: string | undefined, opts?: Record<string, boolean>): this {
		const o = this._constraint;
		this._constraint = constraint;

		if (o !== constraint || opts?.force) {
			if (typeof constraint == 'string' && !constraint.startsWith('[')/*by server*/)
				this._cst = new zul.inp.SimpleDateConstraint(constraint, this);
			else
				this._cst = constraint;
			if (this._cst)
				this._reVald = true; //revalidate required
			if (this._pop) {
				this._pop.setConstraint(this._constraint);
				this._pop.rerender();
			}
		}

		return this;
	}

	/**
	 * @returns the constraint, or null if no constraint at all.
	 */
	override getConstraint(): string | undefined {
		return this._constraint;
	}

	/**
	 * Sets the time zone that this date box belongs to.
	 * @param timezone - the time zone's ID, such as "America/Los_Angeles".
	 */
	setTimeZone(timeZone: string, opts?: Record<string, boolean>): this {
		const o = this._timeZone;
		this._timeZone = timeZone;

		if (o !== timeZone || opts?.force) {
			this._timeZone = timeZone;
			this._tm.setTimezone(timeZone);
			this._setTimeZonesIndex();
			this._value?.tz(timeZone);
			this._pop && this._pop._fixConstraint();
			var cst = this._cst;
			if (cst && cst instanceof zul.inp.SimpleConstraint)
				cst.reparseConstraint();
		}

		return this;
	}

	/**
	 * @returns the time zone that this date box belongs to, such as "America/Los_Angeles"
	 */
	override getTimeZone(): string | undefined {
		return this._timeZone;
	}

	/**
	 * Sets whether the list of the time zones to display is readonly.
	 * If readonly, the user cannot change the time zone at the client.
	 */
	setTimeZonesReadonly(timeZonesReadonly: boolean, opts?: Record<string, boolean>): this {
		const o = this._timeZonesReadonly;
		this._timeZonesReadonly = timeZonesReadonly;

		if (o !== timeZonesReadonly || opts?.force) {
			var select = this.$n<HTMLSelectElement>('dtzones');
			if (select) select.disabled = timeZonesReadonly;
		}

		return this;
	}

	/**
	 * @returns whether the list of the time zones to display is readonly.
	 * If readonly, the user cannot change the time zone at the client.
	 */
	isTimeZonesReadonly(): boolean {
		return !!this._timeZonesReadonly;
	}

	/**
	 * Sets a catenation of a list of the time zones' ID, separated by comma,
	 * that will be displayed at the client and allow user to select.
	 * @param dtzones - a catenation of a list of the timezones' ID, such as
	 * `"America/Los_Angeles,GMT+8"`
	 */
	setDisplayedTimeZones(displayedTimeZones: string, opts?: Record<string, boolean>): this {
		const o = this._displayedTimeZones;
		this._displayedTimeZones = displayedTimeZones;

		if (o !== displayedTimeZones || opts?.force) {
			this._dtzones = displayedTimeZones ? displayedTimeZones.split(',') : undefined;
		}

		return this;
	}

	/**
	 * @returns a list of the time zones that will be displayed at the
	 * client and allow user to select.
	 * @defaultValue `null`
	 */
	getDisplayedTimeZones(): string | undefined {
		return this._displayedTimeZones;
	}

	/**
	 * Sets the unformater function. This method is called from Server side.
	 * @param unf - the unformater function
	 */
	setUnformater(unformater: string, opts?: Record<string, boolean>): this {
		const o = this._unformater;
		this._unformater = unformater;

		if (o !== unformater || opts?.force) {
			eval('Datebox._unformater = ' + unformater); // eslint-disable-line no-eval
		}

		return this;
	}

	/**
	 * @returns the unformater.
	 */
	getUnformater(): string | undefined {
		return this._unformater;
	}

	/**
	 * Sets whether or not date/time parsing is to be lenient.
	 *
	 * <p>
	 * With lenient parsing, the parser may use heuristics to interpret inputs
	 * that do not precisely match this object's format. With strict parsing,
	 * inputs must match this object's format.
	 *
	 * @defaultValue `true`.
	 */
	setLenient(lenient: boolean): this {
		this._lenient = lenient;
		return this;
	}

	/**
	 * @returns whether or not date/time parsing is to be lenient.
	 *
	 * <p>
	 * With lenient parsing, the parser may use heuristics to interpret inputs
	 * that do not precisely match this object's format. With strict parsing,
	 * inputs must match this object's format.
	 */
	isLenient(): boolean {
		return this._lenient;
	}

	getLocalizedSymbols(): zk.LocalizedSymbols | undefined {
		return this._localizedSymbols;
	}

	setLocalizedSymbols(localizedSymbols?: [string, zk.LocalizedSymbols], opts?: Record<string, boolean>): this {
		const o = this._localizedSymbols;

		if (localizedSymbols) {
			if (!globallocalizedSymbols[localizedSymbols[0]])
				globallocalizedSymbols[localizedSymbols[0]] = localizedSymbols[1];
			this._localizedSymbols = globallocalizedSymbols[localizedSymbols[0]];
		}

		if (o !== localizedSymbols || opts?.force) {

			// in this case, we cannot use setLocalizedSymbols() for Timebox
			if (this._tm)
				this._tm._localizedSymbols = this._localizedSymbols;
			if (this._pop)
				this._pop.setLocalizedSymbols(this._localizedSymbols);
		}

		return this;
	}

	/**
	 * Sets whether enable to show the week number in the current calendar or
	 * not. [ZK EE]
	 * @since 6.5.0
	 */
	setWeekOfYear(weekOfYear: boolean, opts?: Record<string, boolean>): this {
		const o = this._weekOfYear;
		this._weekOfYear = weekOfYear;

		if (o !== weekOfYear || opts?.force) {
			if (this._pop)
				this._pop.setWeekOfYear(weekOfYear);
		}

		return this;
	}

	/**
	 * @returns whether enable to show the week number in the current calendar
	 * or not.
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
			if (this._pop) {
				this._pop.setShowTodayLink(showTodayLink);
			}
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
			if (this._pop) {
				this._pop.setTodayLinkLabel(todayLinkLabel);
			}
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
	 * Sets whether or not date/time should be strict.
	 * If true, any invalid input like "Jan 0" or "Nov 31" would be refused.
	 * If false, it won't be checked and let lenient parsing decide.
	 * @since 8.6.0
	 */
	setStrictDate(strictDate: boolean): this {
		this._strictDate = strictDate;
		return this;
	}

	/**
	 * @returns whether or not date/time should be strict.
	 * @defaultValue `false`.
	 * @since 8.6.0
	 */
	isStrictDate(): boolean {
		return this._strictDate;
	}

	/**
	 * Sets the default datetime if the value is empty.
	 * @since 9.0.0
	 * @param defaultDateTime - Default datetime. null means current datetime.
	 */
	setDefaultDateTime(defaultDateTime: DateImpl): this {
		this._defaultDateTime = defaultDateTime;
		return this;
	}

	/**
	 * @returns the default datetime if the value is empty.
	 * @defaultValue `null` (means current datetime).
	 * @since 9.0.0
	 */
	getDefaultDateTime(): DateImpl | undefined {
		return this._defaultDateTime;
	}

	/**
	 * Sets the level that a user can select.
	 * The valid options are "day", "month", and "year".
	 *
	 * @param selectLevel - the level that a user can select
	 * @since 9.5.1
	 */
	setSelectLevel(selectLevel: 'day' | 'month' | 'year' | 'decade' | 'today'): this {
		this._selectLevel = selectLevel;
		return this;
	}

	/**
	 * @returns the level that a user can select.
	 * @defaultValue `"day"`
	 * @since 9.5.1
	 */
	getSelectLevel(): string {
		return this._selectLevel;
	}

	/**
	 * Sets whether to auto close the datebox popup after changing the timezone.
	 *
	 * @param closePopupOnTimezoneChange - shall close the datebox popup or not
	 * @since 9.6.0
	 */
	setClosePopupOnTimezoneChange(closePopupOnTimezoneChange: boolean): this {
		this._closePopupOnTimezoneChange = closePopupOnTimezoneChange;
		return this;
	}

	/**
	 * @returns whether to auto close the datebox popup after changing the timezone.
	 * @defaultValue `true`
	 * @since 9.6.0
	 */
	isClosePopupOnTimezoneChange(): boolean {
		return this._closePopupOnTimezoneChange;
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
	setValueInZonedDateTime(valueInZonedDateTime: DateImpl, fromServer?: boolean): this {
		return this.setValue(valueInZonedDateTime, fromServer);
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
	setValueInLocalDateTime(valueInLocalDateTime: DateImpl, fromServer?: boolean): this {
		return this.setValue(valueInLocalDateTime, fromServer);
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
	setValueInLocalDate(valueInLocalDate: DateImpl, fromServer?: boolean): this {
		return this.setValue(valueInLocalDate, fromServer);
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
	setValueInLocalTime(valueInLocalTime: DateImpl, fromServer?: boolean): this {
		return this.setValue(valueInLocalTime, fromServer);
	}

	/**
	 * @returns the iconSclass name of this Datebox.
	 * @since 8.6.2
	 */
	getIconSclass(): string {
		return 'z-icon-calendar';
	}

	override inRoundedMold(): boolean {
		return true;
	}

	/** @internal */
	_setTimeZonesIndex(): this {
		var select = this.$n<HTMLSelectElement>('dtzones');
		if (select && this._timeZone) {
			var opts = jq(select).children('option');
			for (var i = opts.length; i--;) {
				if (opts[i].text == this._timeZone) select.selectedIndex = i;
			}
		}
		return this;
	}

	override onSize(): void {
		if (this.isOpen())
			Datebox._reposition(this, true);
	}

	/**
	 * @returns the Time format of the specified format
	 */
	getTimeFormat(): string {
		//Note: S (milliseconds not supported yet)
		var fmt = this._format!,
			aa = fmt.indexOf('a'),
			hh = fmt.indexOf('h'),
			KK = fmt.indexOf('K'),
			HH = fmt.indexOf('H'), // ZK-2964: local nl time format has only one 'H'
			kk = fmt.indexOf('k'),
			mm = fmt.indexOf('m'),
			ss = fmt.indexOf('s'),
			hasAM = aa > -1,
			//bug 3284144: The databox format parse a wrong result with hh:mm:ss
			hasHour1 = (hasAM || hh) ? hh > -1 || KK > -1 : false,
			mv = mm > -1 ? 'mm' : '',
			sv = ss > -1 ? 'ss' : '';

		if (hasHour1) {
			var time = Datebox._prepareTimeFormat(hh < KK ? 'KK' : 'hh', mv, sv);
			if (aa == -1)
				return time;
			else if ((hh != -1 && aa < hh) || (KK != -1 && aa < KK))
				return 'a ' + time;
			else
				return time + ' a';
		} else
			return Datebox._prepareTimeFormat(HH < kk ? 'kk' : HH > -1 ? 'HH' : '', mv, sv);

	}

	/**
	 * @returns the Date format of the specified format
	 */
	getDateFormat(): string {
		return this._format!.replace(/[(s.S{1,3})ahKHksm]*:?/g, '').trim();
	}

	/**
	 * Drops down or closes the calendar to select a date.
	 */
	setOpen(open: boolean, _focus_: boolean): this {
		if (this.isRealVisible()) {
			var pop = this._pop;
			if (pop) {
				if (open) pop.open(!_focus_);
				else pop.close(!_focus_);
			}
		}
		return this;
	}

	isOpen(): boolean {
		return !!this._pop && this._pop.isOpen();
	}

	override setValue(value: DateImpl, fromServer?: boolean): this {
		var tz = this.getTimeZone();
		if (tz && value) value.tz(tz);
		return super.setValue(value, fromServer);
	}

	/** @internal */
	override coerceFromString_(val: string | undefined, pattern?: string): zul.inp.CoerceFromStringResult | DateImpl | undefined {
		var unf = Datebox._unformater,
			tz = this.getTimeZone();
		if (unf && jq.isFunction(unf)) {
			var cusv = unf(val);
			if (cusv) {
				this._shortcut = val;
				return cusv;
			}
		}
		if (val) {
			var refDate = this._refDate || this._value,
				format = this.getFormat()!,
				d = new zk.fmt.Calendar().parseDate(val, pattern || format, !this._lenient, refDate, this._localizedSymbols, tz, this._strictDate);
			this._refDate = undefined;
			if (!d) return { error: zk.fmt.Text.format(msgzul.DATE_REQUIRED + (this.localizedFormat!.replace(_quotePattern, ''))) };
			// B70-ZK-2382 escape shouldn't be used in format including hour
			if (!format.match(/[HkKh]/))
				d = new zk.fmt.Calendar().escapeDSTConflict(d, tz);
			return d;
		}
		return undefined;
	}

	/** @internal */
	override coerceToString_(val: DateImpl | undefined, pattern?: string): string {
		return val ? new zk.fmt.Calendar().formatDate(val, pattern || this.getFormat(), this._localizedSymbols) : '';
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (this._disabled) return;
		if (this._readonly && this._buttonVisible && this._pop && !this._pop.isOpen())
			this._pop.open();
		super.doClick_(evt, popupOnly);
	}

	/** @internal */
	override doKeyDown_(evt: zk.Event): void {
		this._doKeyDown(evt);
		if (!evt.stopped)
			super.doKeyDown_(evt);
	}

	/** @internal */
	_doKeyDown(evt: zk.Event): void {
		if (jq.nodeName(evt.domTarget, 'option', 'select'))
			return;

		var keyCode = evt.keyCode,
			bOpen = this._pop.isOpen();
		if (!evt.shiftKey && keyCode == 9 && evt.domTarget == this.$n('real') && bOpen) { // Tab focus to popup
			this._pop.focus();
			evt.stop();
			return;
		}
		if (keyCode == 9 || (zk.webkit && keyCode == 0)) { //TAB or SHIFT-TAB (safari)
			if (evt.target != this._tm) { // avoid closing the popup if moving focus to the timezone dropdown
				if (bOpen) this._pop.close();
				return;
			}
		}

		if (evt.altKey && (keyCode == 38 || keyCode == 40)) {//UP/DN
			if (bOpen) this._pop.close();
			else this._pop.open();

			//FF: if we eat UP/DN, Alt+UP degenerate to Alt (select menubar)
			var opts = { propagation: true };
			evt.stop(opts);
			return;
		}

		//Request 1537962: better responsive
		if (bOpen && (keyCode == 13 || keyCode == 27 || keyCode == 32)) { //ENTER or ESC or SPACE
			if (keyCode == 13) this.enterPressed_(evt);
			else if (keyCode == 27) this.escPressed_(evt);
			return;
		}

		if (keyCode == 18 || keyCode == 27 || keyCode == 13
			|| (keyCode >= 112 && keyCode <= 123)) //ALT, ESC, Enter, Fn
			return; //ignore it (doc will handle it)

		// ZK-2202: should not trigger too early when key code is ENTER
		// select current time
		if (this._pop.isOpen()) {
			this._pop.doKeyDown_(evt);
		}
	}

	/**
	 * Called when the user presses enter when this widget has the focus ({@link focus}).
	 * <p>call the close function
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @internal
	 */
	enterPressed_(evt: zk.Event): void {
		this._pop.close();
		this.updateChange_();
		evt.stop();
	}

	/**
	 * Called when the user presses escape key when this widget has the focus ({@link focus}).
	 * <p>call the close function
	 * @param evt - the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 * @internal
	 */
	escPressed_(evt: zk.Event): void {
		this._pop.close();
		evt.stop();
	}

	/** @internal */
	override afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean {
		if (!simulated && this._inplace)
			jq(this.$n_()).toggleClass(this.getInplaceCSS(), evt.keyCode == 13);

		return super.afterKeyDown_(evt, simulated);
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var btn = this.$n('btn');

		if (btn) {
			this.domListen_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
			if (this._inplace) this.domListen_(btn, 'onMouseDown', '_doBtnMouseDown');
		}

		zWatch.listen({ onSize: this, onScroll: this, onShow: this });
		this._pop.setFormat(this.getDateFormat());
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var pop = this._pop;
		if (pop)
			pop.close(true);

		var btn = this.$n('btn');
		if (btn) {
			this.domUnlisten_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
			if (this._inplace) this.domUnlisten_(btn, 'onMouseDown', '_doBtnMouseDown');
		}

		zWatch.unlisten({ onSize: this, onScroll: this, onShow: this });
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_doBtnClick(evt: zk.Event): void {
		this._inplaceIgnore = false;
		if (!this._buttonVisible) return;
		if (!this._disabled)
			this.setOpen(!jq(this.$n_('pp')).zk.isVisible(), zul.db.DateboxCtrl.isPreservedFocus(this));
		// Bug ZK-2544, B70-ZK-2849
		evt.stop((this._pop && this._pop._open ? { propagation: true } : undefined));
	}

	/** @internal */
	_doBtnMouseDown(): void {
		this._inplaceIgnore = true;
	}

	/** @internal */
	_doTimeZoneChange(evt: zk.Event): void {
		var select = this.$n_<HTMLSelectElement>('dtzones'),
			timezone = select.value;
		this.updateChange_();
		this.fire('onTimeZoneChange', { timezone: timezone }, { toServer: true }, 150);
		if (this._pop && this._closePopupOnTimezoneChange) this._pop.close();
	}

	// NOTE: data value will become string after event handler
	onChange(evt: zk.Event & { data: zul.db.CalendarOnChangeData }): void {
		var data = evt.data,
			inpValue = this.getInputNode()!.value;
		if (this._pop)
			this._pop.setValue(data.value!);
		// B50-ZK-631: Datebox format error message not shown with implements CustomConstraint
		// pass input value to server for showCustomError
		if (!data.value && inpValue
			&& this.getFormat() && this._cst == '[c')
			data.value = inpValue as unknown as DateImpl; // FIXME: type mismatch
	}

	onScroll(wgt?: zk.Widget): void {
		if (this.isOpen()) {
			var pp = this._pop;
			// ZK-1552: fix the position of popup when scroll
			if (wgt && pp) {
				// ZK-2211: should close when the input is out of view
				if (this.getInputNode() && zul.inp.InputWidget._isInView(this))
					Datebox._reposition(this, true);
				else
					pp.close();
			}
		}
	}

	override onShow(): void {
		if (this.__ebox) {
			this.setFloating_(true);
			this.__ebox.show();
		}
	}

	/**
	 * @returns the label of the time zone
	 */
	getTimeZoneLabel(): string {
		return '';
	}

	/** @internal */
	redrawpp_(out: string[]): void {
		out.push('<div id="', this.uuid, '-pp" class="', this.$s('popup'),
			'" style="display:none" role="dialog" aria-labelledby="', this._pop.uuid, '-title" tabindex="-1">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		this._redrawTimezone(out);
		out.push('</div>');
	}

	/** @internal */
	_redrawTimezone(out: string[]): void {
		var timezones = this._dtzones;
		if (timezones) {
			out.push('<div class="', this.$s('timezone'), '">',
				this.getTimeZoneLabel(),
				'<select id="', this.uuid, '-dtzones">');
			for (var i = 0, len = timezones.length; i < len; i++)
				out.push('<option value="', timezones[i], '">', timezones[i], '</option>');
			out.push('</select></div>');
			// B50-ZK-577: Rendering Issue using Datebox with displayedTimeZones
		}
	}

	/** @internal */
	override updateChange_(clear?: boolean): boolean {
		if (!this.isOpen())
			super.updateChange_(clear);
		return false;
	}

	/** @internal */
	static _reposition(db: Datebox, silent?: boolean): void {
		if (!db.$n()) return;
		var pp = db.$n('pp'),
			n = db.$n_(),
			inp = db.getInputNode();

		if (pp) {
			// dodgeRef is only ever tested for truthiness, and only here is it set to a non-boolean value.
			zk(pp).position(n, db._position, { dodgeRef: n as unknown as boolean });
			db._pop.syncShadow();
			if (!silent)
				zk(inp).focus();
		}
	}

	/** @internal */
	static _prepareTimeFormat(h: string, m: string, s: string): string {
		var o: string[] = [];
		if (h) o.push(h);
		if (m) o.push(m);
		if (s) o.push(s);
		return o.join(':');
	}
}

@zk.WrapClass('zul.db.CalendarPop')
export class CalendarPop extends zul.db.Calendar {
	override parent!: Datebox;
	/** @internal */
	_shadow?: zk.eff.Shadow;
	/** @internal */
	_open?: boolean;

	constructor() {
		super();
		this.listen({ onChange: this }, -1000);
	}

	setFormat(format: string): this {
		this._fmt = format;
		return this;
	}

	setLocalizedSymbols(localizedSymbols?: zk.LocalizedSymbols): this {
		this._localizedSymbols = localizedSymbols;
		return this;
	}

	// ZK-2047: should sync shadow when shiftView
	override rerender(skipper?: number | zk.Skipper): this {
		super.rerender(skipper);
		if (this.desktop) this.syncShadow();
		return this;
	}

	close(silent?: boolean): void {
		var db = this.parent,
			pp = db.$n('pp');

		if (!pp || !zk(pp).isVisible()) return;

		if (db._inplace) {
			db._inplaceIgnore = false;
			db._inplaceTimerId = setTimeout(function () {
				if (db.desktop) jq(db.$n_()).addClass(db.getInplaceCSS());
			}, db._inplaceTimeout);
		}
		// firefox and safari only
		try {
			if ((zk.ff || zk.safari) && zk.currentFocus) {
				var n = zk.currentFocus.getInputNode ?
					zk.currentFocus.getInputNode()! : zk.currentFocus.$n_();
				if (jq.nodeName(n, 'input') && jq.isAncestor(pp, n)) // Bug ZK-2922, check ancestor first.
					jq(n).blur(); // trigger a missing blur event.
			}
		} catch (e) {
			zk.debugLog((e as Error).message || e as string);
		}

		if (this._shadow) {
			// B65-ZK-1904: Make shadow behavior the same as ComboWidget
			this._shadow.destroy();
			this._shadow = undefined;
		}
		pp.style.display = 'none';
		pp.className = db.$s('popup');

		jq(pp).zk.undoVParent();
		db.setFloating_(false);

		if (silent)
			db.updateChange_();
		else if (zul.db.DateboxCtrl.isPreservedFocus(this))
			zk(db.getInputNode()).focus();
		//remove extra CSS class
		var openClass = db.$s('open');
		jq(db.$n_()).removeClass(openClass);
		jq(pp).removeClass(openClass);
	}

	isOpen(): boolean {
		return zk(this.parent.$n('pp')).isVisible();
	}

	open(silent?: boolean): void {
		var db = this.parent,
			dbn = db.$n(), pp = db.$n('pp');
		if (!dbn || !pp)
			return;
		if (db._inplace) db._inplaceIgnore = true;
		db.setFloating_(true, { node: pp });
		zWatch.fire('onFloatUp', db); //notify all
		var topZIndex = this.setTopmost();
		this._setView(db._selectLevel);
		var zcls = db.getZclass();

		pp.className = dbn.className + ' ' + pp.className;
		jq(pp).removeClass(zcls);

		pp.style.width = 'auto'; //reset
		pp.style.display = 'block';
		pp.style.zIndex = (topZIndex > 0 ? topZIndex : 1) as unknown as string;
		this.setDomVisible_(pp, true, { visibility: true });

		//FF: Bug 1486840
		//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
		jq(pp).zk.makeVParent();

		if (pp.offsetWidth < dbn.offsetWidth) {
			pp.style.width = `${dbn.offsetWidth}px`;
		} else {
			var wd = jq.innerWidth() - 20;
			if (wd < dbn.offsetWidth)
				wd = dbn.offsetWidth;
			if (pp.offsetWidth > wd)
				pp.style.width = wd as unknown as string;
		}
		delete db._shortcut;

		var fmt = db.getTimeFormat(),
			tz = db.getTimeZone(),
			value = db._value || db._defaultDateTime || zUtl.today(fmt, tz);
		if (value)
			this.setValue(value);
		if (fmt) {
			var tm = db._tm;
			tm.setVisible(true);
			tm.setFormat(fmt);
			tm.setValue(value || Dates.newInstance().tz(tz));
			tm.onSize();
		} else {
			db._tm.setVisible(false);
		}
		//add extra CSS class for easy customize
		var openClass = db.$s('open');
		jq(db.$n_()).addClass(openClass);
		jq(pp).addClass(openClass);

		Datebox._reposition(db, silent); //ZK-3217: only need to calculate position once during open
	}

	syncShadow(): void {
		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this.parent.$n_('pp'), {
				left: -4, right: 4, top: 2, bottom: 3
			});
		this._shadow.sync();
	}

	override onChange(evt: zk.Event & { data: zul.db.CalendarOnChangeData }): void {
		var date: DateImpl | undefined = this.getTime(),
			db = this.parent,
			fmt = db.getTimeFormat(),
			oldDate = db.getValue(),
			tz = db.getTimeZone(),
			cal = new zk.fmt.Calendar();

		if (fmt) {
			var tm = db._tm,
				time = tm.getValue()!;
			if (fmt.match(/[HK]/i))
				date.setHours(time.getHours());
			if (fmt.match(/[m]/))
				date.setMinutes(time.getMinutes());
			if (fmt.match(/[s]/))
				date.setSeconds(time.getSeconds());
			if (fmt.match(/[S]/))
				date.setMilliseconds(time.getMilliseconds());

			// B70-ZK-2382 escape shouldn't be used in format including hour
			if (!fmt.match(/[HkKh]/))
				date = cal.escapeDSTConflict(date, tz);
		} else if (oldDate) {
			date = Dates.newInstance([date.getFullYear(), date.getMonth(),
			date.getDate(), oldDate.getHours(),
			oldDate.getMinutes(), oldDate.getSeconds(), oldDate.getMilliseconds()], tz);
			//Note: we cannot call setFullYear(), setMonth(), then setDate(),
			//since Date object will adjust month if date larger than max one

			// B70-ZK-2382 escape shouldn't be used in format including hour
			if (!this.getFormat().match(/[HkKh]/))
				date = cal.escapeDSTConflict(date, tz);
		}

		//Bug ZK-1712: no need to set datebox input value when shift view
		if (!evt.data.shiftView)
			db.getInputNode()!.value = db.coerceToString_(date);

		// eslint-disable-next-line @typescript-eslint/no-unnecessary-boolean-literal-compare
		if (this._view == db._selectLevel && evt.data.shallClose !== false) {
			this.close();

			// Bug 3122159 and 3301374
			evt.data.value = date;
			if (!CalendarPop._equalDate(date, oldDate)) {
				db._refDate = date; // for coerceFromString_
				db.updateChange_();
			}
		}
		evt.stop();
	}

	onFloatUp(ctl: zk.ZWatchController): void {
		if (this.isOpen()) {
			var db = this.parent;
			if (!zUtl.isAncestor(db, ctl.origin)) {
				this.close(true);
			}
		}
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._bindTimezoneEvt();

		zWatch.listen({ onFloatUp: this });
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({ onFloatUp: this });
		this._unbindfTimezoneEvt();
		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = undefined;
		}
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_bindTimezoneEvt(): void {
		var db = this.parent,
			select = db.$n<HTMLSelectElement>('dtzones');
		if (select) {
			select.disabled = (db.isTimeZonesReadonly() ? 'disable' : '') as unknown as boolean;
			db.domListen_(select, 'onChange', '_doTimeZoneChange');
			db._setTimeZonesIndex();
		}
	}

	/** @internal */
	_unbindfTimezoneEvt(): void {
		var db = this.parent,
			select = db.$n('dtzones');
		if (select)
			db.domUnlisten_(select, 'onChange', '_doTimeZoneChange');
	}

	/** @internal */
	override _setView(val: 'day' | 'month' | 'year' | 'decade' | 'today', force?: number): this {
		if (this.parent.getTimeFormat())
			this.parent._tm.setVisible(val == 'day');
		super._setView(val, force);

		// ZK-2047: when sync shadow, the calendar popup should be above the pdf
		if (zk.ie11) {
			this.syncShadow();
		}

		return this;
	}

	// ZK-2308
	/** @internal */
	override doKeyDown_(evt: zk.Event): void {
		super.doKeyDown_(evt);
		if (evt.keyCode == 27) {
			this.parent.escPressed_(evt);
		}
	}

	/** @internal */
	override animationSpeed_(): number | 'slow' | 'fast' {
		return zk(this.parent).getAnimationSpeed();
	}

	/** @internal */
	override _chooseDate(target: HTMLTableCellElement | undefined, val: number): void {
		var db = this.parent,
			selectLevel = db._selectLevel;
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
						this._markCal({ sameMonth: true }); // optimize
					} else {
						this.rerender(-1);
						this.focus();
					}
					break;
				case 'month':
					if (selectLevel == 'month') {
						this._setTime(undefined, val, 1, true);
						break;
					}
					this._setTime(undefined, val);
					this._setView('day');
					break;
				case 'year':
					if (selectLevel == 'year') {
						this._setTime(val, 0, 1, true);
						break;
					}
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
	static _equalDate(d1: DateImpl | undefined, d2: DateImpl | undefined): boolean {
		return !!((d1 == d2) || (d1 && d2 && d1.getTime() == d2.getTime()));
	}
}

@zk.WrapClass('zul.db.CalendarTime')
export class CalendarTime extends zul.db.Timebox {
	override parent!: Datebox;

	constructor() {
		super();
		this.listen({ onChanging: this }, -1000);
	}

	onChanging(evt: zk.Event & { data: zul.db.CalendarOnChangeData }): void {
		var db = this.parent,
			oldDate = db.getValue() || db._pop.getValue(),
			// ZK-2382 we must do the conversion with date and time in the same time
			// otherwise the result may be affcted by DST adjustment
			dateTime = db.coerceToString_(oldDate, _innerDateFormat) + evt.data.value, //onChanging's data is string
			pattern = _innerDateFormat + db.getTimeFormat();

		// add 'AM' by default, if pattern specified AMPM
		dateTime += pattern.includes('a') ?
			!dateTime.includes('AM') && !dateTime.includes('PM') ? 'AM' : '' : '';
		var date = db.coerceFromString_(dateTime, pattern);

		// do nothing if date converted from String is not a valid Date object e.g. dateTime = "2014/10/10 1 :  :     "
		if (date instanceof DateImpl) {
			db.getInputNode()!.value = (evt.data.value as unknown as string)
				= db.coerceToString_(date);
			db.fire(evt.name, evt.data); //onChanging
		}

		evt.stop();
	}
}

/** @class zul.db.DateboxCtrl
 * @import zk.Widget
 * The extra control for the Datebox.
 * It is designed to be overriden
 * @since 6.5.0
 */
export var DateboxCtrl = {
	/**
	 * @returns whether to preserve the focus state.
	 * @param wgt - a widget
	 */
	isPreservedFocus(wgt: zk.Widget): boolean {
		return true;
	}
};
zul.db.DateboxCtrl = DateboxCtrl;