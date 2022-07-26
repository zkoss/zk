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
	_buttonVisible = true;
	_lenient = true;
	_strictDate = false;
	_selectLevel = 'day';
	_closePopupOnTimezoneChange = true;
	_pop: CalendarPop;
	_tm: CalendarTime;
	_localizedSymbols?: zk.LocalizedSymbols;
	// defSet will generate `_timeZone` but actually `_timezone` is used in code.
	// Debugger in ZKDemo shows that both properties are created and agree.
	// However, the server should expect `_timeZone`.
	_timeZone?: string;
	_constraint?: string;
	_displayedTimeZones?: string;
	_dtzones?: string[];
	_unformater?: string;
	_weekOfYear?: boolean;
	_showTodayLink?: boolean;
	_todayLinkLabel?: string;
	_defaultDateTime?: DateImpl;
	_refDate?: DateImpl;
	_timeZonesReadonly?: boolean;
	static _unformater?: zul.db.Unformater;
	localizedFormat?: string;
	_position?: string;

	constructor() {
		super();
		this.listen({onChange: this}, -1000);
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
	 * @param boolean visible
	 */
	setButtonVisible(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._buttonVisible;
		this._buttonVisible = v;

		if (o !== v || (opts && opts.force)) {
			zul.inp.RoundUtl.buttonVisible(this, v);
		}

		return this;
	}

	/**
	 * Returns whether the button (on the right of the textbox) is visible.
	 * <p>
	 * Default: true.
	 * @return boolean
	 */
	isButtonVisible(): boolean {
		return this._buttonVisible;
	}

	/** Sets the date format.
	 * <p>The following pattern letters are defined:
	 * <table border=0 cellspacing=3 cellpadding=0>
	 * <tr bgcolor="#ccccff">
	 * <th align=left>Letter
	 * <th align=left>Date or Time Component
	 * <th align=left>Presentation
	 * <th align=left>Examples
	 * <tr><td><code>G</code>
	 * <td>Era designator
	 * <td>Text</a>
	 * <td><code>AD</code>
	 * <tr bgcolor="#eeeeff">
	 * <td><code>y</code>
	 * <td>Year
	 * <td>Year</a>
	 * <td><code>1996</code>; <code>96</code>
	 * <tr><td><code>M</code>
	 * <td>Month in year
	 * <td>Month</a>
	 * <td><code>July</code>; <code>Jul</code>; <code>07</code>
	 * <tr bgcolor="#eeeeff">
	 * <td><code>w</code>
	 * <td>Week in year (starting at 1)
	 * <td>Number</a>
	 * <td><code>27</code>
	 * <tr><td><code>W</code>
	 * <td>Week in month (starting at 1)
	 * <td>Number</a>
	 * <td><code>2</code>
	 * <tr bgcolor="#eeeeff">
	 * <td><code>D</code>
	 * <td>Day in year (starting at 1)
	 * <td>Number</a>
	 * <td><code>189</code>
	 * <tr><td><code>d</code>
	 * <td>Day in month (starting at 1)
	 * <td>Number</a>
	 * <td><code>10</code>
	 * <tr bgcolor="#eeeeff">
	 * <td><code>F</code>
	 * <td>Day of week in month
	 * <td>Number</a>
	 * <td><code>2</code>
	 * <tr><td><code>E</code>
	 * <td>Day in week
	 * <td>Text</a>
	 * <td><code>Tuesday</code>; <code>Tue</code>
	 * </table>
	 * @param String format the pattern.
	 */
	override setFormat(format: string, opts?: Record<string, boolean>): this {
		const o = this._format;
		this._format = format;

		if (o !== format || (opts && opts.force)) {
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

	/** Returns the full date format of the specified format
	 * @return String
	 */
	override getFormat(): string | undefined {
		return this._format;
	}

	/** Sets the constraint.
	 * <p>Default: null (means no constraint all all).
	 * @param String cst
	 */
	override setConstraint(cst: string | undefined, opts?: Record<string, boolean>): this {
		const o = this._constraint;
		this._constraint = cst;

		if (o !== cst || (opts && opts.force)) {
			if (typeof cst == 'string' && cst.charAt(0) != '['/*by server*/)
				this._cst = new zul.inp.SimpleDateConstraint(cst, this);
			else
				this._cst = cst as undefined;
			if (this._cst)
				this._reVald = true; //revalidate required
			if (this._pop) {
				this._pop.setConstraint(this._constraint);
				this._pop.rerender();
			}
		}

		return this;
	}

	/** Returns the constraint, or null if no constraint at all.
	 * @return String
	 */
	override getConstraint(): string | undefined {
		return this._constraint;
	}

	/** Sets the time zone that this date box belongs to.
	 * @param String timezone the time zone's ID, such as "America/Los_Angeles".
	 */
	setTimeZone(timezone: string, opts?: Record<string, boolean>): this {
		const o = this._timeZone;
		this._timeZone = timezone;

		if (o !== timezone || (opts && opts.force)) {
			this._timeZone = timezone;
			this._tm.setTimezone(timezone);
			this._setTimeZonesIndex();
			this._value && this._value.tz(timezone);
			this._pop && this._pop._fixConstraint();
			var cst = this._cst;
			if (cst && cst instanceof zul.inp.SimpleConstraint)
				cst.reparseConstraint();
		}

		return this;
	}

	/** Returns the time zone that this date box belongs to.
	 * @return String the time zone's ID, such as "America/Los_Angeles".
	 */
	getTimeZone(): string | undefined {
		return this._timeZone;
	}

	/** Sets whether the list of the time zones to display is readonly.
	 * If readonly, the user cannot change the time zone at the client.
	 * @param boolean readonly
	 */
	setTimeZonesReadonly(readonly: boolean, opts?: Record<string, boolean>): this {
		const o = this._timeZonesReadonly;
		this._timeZonesReadonly = readonly;

		if (o !== readonly || (opts && opts.force)) {
			var select = this.$n<HTMLSelectElement>('dtzones');
			if (select) select.disabled = (readonly ? 'disabled' : '') as unknown as boolean;
		}

		return this;
	}

	/** Returns whether the list of the time zones to display is readonly.
	 * If readonly, the user cannot change the time zone at the client.
	 * @return boolean
	 */
	isTimeZonesReadonly(): boolean {
		return !!this._timeZonesReadonly;
	}

	/** Sets a catenation of a list of the time zones' ID, separated by comma,
	 * that will be displayed at the client and allow user to select.
	 * @param String dtzones a catenation of a list of the timezones' ID, such as
	 * <code>"America/Los_Angeles,GMT+8"</code>
	 */
	setDisplayedTimeZones(dtzones: string, opts?: Record<string, boolean>): this {
		const o = this._displayedTimeZones;
		this._displayedTimeZones = dtzones;

		if (o !== dtzones || (opts && opts.force)) {
			this._dtzones = dtzones ? dtzones.split(',') : undefined;
		}

		return this;
	}

	/** Returns a list of the time zones that will be displayed at the
	 * client and allow user to select.
	 * <p>Default: null
	 * @return Array
	 */
	getDisplayedTimeZones(): string | undefined {
		return this._displayedTimeZones;
	}

	/** Sets the unformater function. This method is called from Server side.
	 * @param String unf the unformater function
	 */
	setUnformater(unf: string, opts?: Record<string, boolean>): this {
		const o = this._unformater;
		this._unformater = unf;

		if (o !== unf || (opts && opts.force)) {
			eval('Datebox._unformater = ' + unf); // eslint-disable-line no-eval
		}

		return this;
	}

	/** Returns the unformater.
	 * @return String the unformater function
	 */
	getUnformater(): string | undefined {
		return this._unformater;
	}

	/** Sets whether or not date/time parsing is to be lenient.
	 *
	 * <p>
	 * With lenient parsing, the parser may use heuristics to interpret inputs
	 * that do not precisely match this object's format. With strict parsing,
	 * inputs must match this object's format.
	 *
	 * <p>Default: true.
	 * @param boolean lenient
	 */
	setLenient(lenient: boolean): this {
		this._lenient = lenient;
		return this;
	}

	/** Returns whether or not date/time parsing is to be lenient.
	 *
	 * <p>
	 * With lenient parsing, the parser may use heuristics to interpret inputs
	 * that do not precisely match this object's format. With strict parsing,
	 * inputs must match this object's format.
	 * @return boolean
	 */
	isLenient(): boolean {
		return this._lenient;
	}

	getLocalizedSymbols(): zk.LocalizedSymbols | undefined {
		return this._localizedSymbols;
	}

	setLocalizedSymbols(val: [string, zk.LocalizedSymbols], opts?: Record<string, boolean>): this {
		const o = this._localizedSymbols;

		if (val) {
			if (!globallocalizedSymbols[val[0]])
				globallocalizedSymbols[val[0]] = val[1];
			this._localizedSymbols = globallocalizedSymbols[val[0]];
		}

		if (o !== val || (opts && opts.force)) {

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
	 * @param boolean weekOfYear
	 */
	setWeekOfYear(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._weekOfYear;
		this._weekOfYear = v;

		if (o !== v || (opts && opts.force)) {
			if (this._pop)
				this._pop.setWeekOfYear(v);
		}

		return this;
	}

	/**
	 * Returns whether enable to show the week number in the current calendar
	 * or not.
	 * <p>Default: false
	 * @since 6.5.0
	 * @return boolean
	 */
	isWeekOfYear(): boolean {
		return !!this._weekOfYear;
	}

	/**
	 * Sets whether enable to show the link that jump to today in day view
	 * @since 8.0.0
	 * @param boolean showTodayLink
	 */
	setShowTodayLink(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._showTodayLink;
		this._showTodayLink = v;

		if (o !== v || (opts && opts.force)) {
			if (this._pop) {
				this._pop.setShowTodayLink(v);
			}
		}

		return this;
	}

	/**
	 * Returns whether enable to show the link that jump to today in day view
	 * <p>Default: false
	 * @since 8.0.0
	 * @return boolean
	 */
	isShowTodayLink(): boolean {
		return !!this._showTodayLink;
	}

	/**
	 * Sets the label of the link that jump to today in day view
	 * @since 8.0.4
	 * @param String todayLinkLabel
	 */
	setTodayLinkLabel(v: string, opts?: Record<string, boolean>): this {
		const o = this._todayLinkLabel;
		this._todayLinkLabel = v;

		if (o !== v || (opts && opts.force)) {
			if (this._pop) {
				this._pop.setTodayLinkLabel(v);
			}
		}

		return this;
	}

	/**
	 * Returns the label of the link that jump to today in day view
	 * @since 8.0.4
	 * @return String
	 */
	getTodayLinkLabel(): string | undefined {
		return this._todayLinkLabel;
	}

	/**
	 * Sets whether or not date/time should be strict.
	 * If true, any invalid input like "Jan 0" or "Nov 31" would be refused.
	 * If false, it won't be checked and let lenient parsing decide.
	 * @since 8.6.0
	 * @param boolean strictDate
	 */
	setStrictDate(strictDate: boolean): this {
		this._strictDate = strictDate;
		return this;
	}

	/**
	 * Returns whether or not date/time should be strict.
	 * <p>Default: false.
	 * @since 8.6.0
	 * @return boolean
	 */
	isStrictDate(): boolean {
		return this._strictDate;
	}

	/**
	 * Sets the default datetime if the value is empty.
	 * @since 9.0.0
	 * @param Date defaultDateTime Default datetime. null means current datetime.
	 */
	setDefaultDateTime(defaultDateTime: DateImpl): this {
		this._defaultDateTime = defaultDateTime;
		return this;
	}

	/**
	 * Returns the default datetime if the value is empty.
	 * <p>Default: null (means current datetime).
	 * @since 9.0.0
	 * @return Date
	 */
	getDefaultDateTime(): DateImpl | undefined {
		return this._defaultDateTime;
	}

	/**
	 * Sets the level that a user can select.
	 * The valid options are "day", "month", and "year".
	 *
	 * @param String selectLevel the level that a user can select
	 * @since 9.5.1
	 */
	setSelectLevel(selectLevel: string): this {
		this._selectLevel = selectLevel;
		return this;
	}

	/**
	 * Returns the level that a user can select.
	 * <p>
	 * Default: "day"
	 * @return String
	 * @since 9.5.1
	 */
	getSelectLevel(): string {
		return this._selectLevel;
	}

	/**
	 * Sets whether to auto close the datebox popup after changing the timezone.
	 *
	 * @param boolean closePopupOnTimezoneChange shall close the datebox popup or not
	 * @since 9.6.0
	 */
	setClosePopupOnTimezoneChange(closePopupOnTimezoneChange: boolean): this {
		this._closePopupOnTimezoneChange = closePopupOnTimezoneChange;
		return this;
	}

	/**
	 * Returns whether to auto close the datebox popup after changing the timezone.
	 * <p>
	 * Default: true
	 * @return boolean
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
	setValueInZonedDateTime(value: DateImpl, fromServer?: boolean): this {
		return this.setValue(value, fromServer);
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
	setValueInLocalDateTime(value: DateImpl, fromServer?: boolean): this {
		return this.setValue(value, fromServer);
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
	setValueInLocalDate(value: DateImpl, fromServer?: boolean): this {
		return this.setValue(value, fromServer);
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
	setValueInLocalTime(value: DateImpl, fromServer?: boolean): this {
		return this.setValue(value, fromServer);
	}

	/**
	 * Returns the iconSclass name of this Datebox.
	 * @return String the iconSclass name
	 * @since 8.6.2
	 */
	getIconSclass(): string {
		return 'z-icon-calendar';
	}

	override inRoundedMold(): boolean {
		return true;
	}

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

	/** Returns the Time format of the specified format
	 * @return String
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

	/** Returns the Date format of the specified format
	 * @return String
	 */
	getDateFormat(): string {
		return this._format!.replace(/[(s.S{1,3})ahKHksm]*:?/g, '').trim();
	}

	/** Drops down or closes the calendar to select a date.
	 */
	setOpen(open: boolean, _focus_: boolean): this {
		if (this.isRealVisible()) {
			var pop: CalendarPop | undefined;
			if (pop = this._pop) {
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
			if (!d) return {error: zk.fmt.Text.format(msgzul.DATE_REQUIRED + (this.localizedFormat!.replace(_quotePattern, '')))};
			// B70-ZK-2382 escape shouldn't be used in format including hour
			if (!format.match(/[HkKh]/))
				d = new zk.fmt.Calendar().escapeDSTConflict(d, tz);
			return d;
		}
		return undefined;
	}

	override coerceToString_(val: DateImpl | undefined, pattern?: string): string {
		return val ? new zk.fmt.Calendar().formatDate(val, pattern || this.getFormat(), this._localizedSymbols) : '';
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (this._disabled) return;
		if (this._readonly && this._buttonVisible && this._pop && !this._pop.isOpen())
			this._pop.open();
		super.doClick_(evt, popupOnly);
	}

	override doKeyDown_(evt: zk.Event): void {
		this._doKeyDown(evt);
		if (!evt.stopped)
			super.doKeyDown_(evt);
	}

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
			var opts = {propagation: true};
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

	/** Called when the user presses enter when this widget has the focus ({@link #focus}).
	 * <p>call the close function
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 */
	enterPressed_(evt: zk.Event): void {
		this._pop.close();
		this.updateChange_();
		evt.stop();
	}

	/** Called when the user presses escape key when this widget has the focus ({@link #focus}).
	 * <p>call the close function
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 */
	escPressed_(evt: zk.Event): void {
		this._pop.close();
		evt.stop();
	}

	override afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean {
		if (!simulated && this._inplace)
			jq(this.$n_()).toggleClass(this.getInplaceCSS(), evt.keyCode == 13);

		return super.afterKeyDown_(evt, simulated);
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var btn: HTMLElement | undefined;

		if (btn = this.$n('btn')) {
			this.domListen_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
			if (this._inplace) this.domListen_(btn, 'onMouseDown', '_doBtnMouseDown');
		}

		zWatch.listen({onSize: this, onScroll: this, onShow: this});
		this._pop.setFormat(this.getDateFormat());
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		var btn: CalendarPop | HTMLElement | undefined;
		if (btn = this._pop)
			btn.close(true);

		if (btn = this.$n('btn')) {
			this.domUnlisten_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
			if (this._inplace) this.domUnlisten_(btn, 'onMouseDown', '_doBtnMouseDown');
		}

		zWatch.unlisten({onSize: this, onScroll: this, onShow: this});
		super.unbind_(skipper, after, keepRod);
	}

	_doBtnClick(evt: zk.Event): void {
		this._inplaceIgnore = false;
		if (!this._buttonVisible) return;
		if (!this._disabled)
			this.setOpen(!jq(this.$n_('pp')).zk.isVisible(), zul.db.DateboxCtrl.isPreservedFocus(this));
		// Bug ZK-2544, B70-ZK-2849
		evt.stop((this._pop && this._pop._open ? {propagation: true} : undefined));
	}

	_doBtnMouseDown(): void {
		this._inplaceIgnore = true;
	}

	_doTimeZoneChange(evt: zk.Event): void {
		var select = this.$n_<HTMLSelectElement>('dtzones'),
			timezone = select.value;
		this.updateChange_();
		this.fire('onTimeZoneChange', {timezone: timezone}, {toServer: true}, 150);
		if (this._pop && this._closePopupOnTimezoneChange) this._pop.close();
	}

	// NOTE: data value will become string after event handler
	onChange(evt: zk.Event & {data: zul.db.CalendarOnChangeData}): void {
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

	onScroll(wgt: zk.Widget): void {
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

	/** Returns the label of the time zone
	 * @return String
	 */
	getTimeZoneLabel(): string {
		return '';
	}

	redrawpp_(out: string[]): void {
		out.push('<div id="', this.uuid, '-pp" class="', this.$s('popup'),
			'" style="display:none" role="dialog" aria-labelledby="', this._pop.uuid, '-title">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		this._redrawTimezone(out);
		out.push('</div>');
	}

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

	override updateChange_(clear?: boolean): boolean {
		if (!this.isOpen())
			super.updateChange_(clear);
		return false;
	}

	static _reposition(db: Datebox, silent?: boolean): void {
		if (!db.$n()) return;
		var pp = db.$n('pp'),
			n = db.$n_(),
			inp = db.getInputNode();

		if (pp) {
			// dodgeRef is only ever tested for truthiness, and only here is it set to a non-boolean value.
			zk(pp).position(n, db._position, {dodgeRef: n as unknown as boolean});
			db._pop.syncShadow();
			if (!silent)
				zk(inp).focus();
		}
	}

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
	_shadow?: zk.eff.Shadow;
	_open?: CalendarPop;

	constructor() {
		super();
		this.listen({onChange: this}, -1000);
	}

	setFormat(fmt: string): this {
		this._fmt = fmt;
		return this;
	}

	setLocalizedSymbols(symbols?: zk.LocalizedSymbols): this {
		this._localizedSymbols = symbols;
		return this;
	}

	// ZK-2047: should sync shadow when shiftView
	override rerender(skipper?: number | zk.Skipper): void {
		super.rerender(skipper);
		if (this.desktop) this.syncShadow();
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
		db.setFloating_(true, {node: pp});
		zWatch.fire('onFloatUp', db); //notify all
		var topZIndex = this.setTopmost();
		this._setView(db._selectLevel);
		var zcls = db.getZclass();

		pp.className = dbn.className + ' ' + pp.className;
		jq(pp).removeClass(zcls);

		pp.style.width = 'auto'; //reset
		pp.style.display = 'block';
		pp.style.zIndex = (topZIndex > 0 ? topZIndex : 1) as unknown as string;
		this.setDomVisible_(pp, true, {visibility: true});

		//FF: Bug 1486840
		//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
		jq(pp).zk.makeVParent();

		if (pp.offsetWidth < dbn.offsetWidth) {
			pp.style.width = dbn.offsetWidth + 'px';
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
				left: -4, right: 4, top: 2, bottom: 3});
		this._shadow.sync();
	}

	override onChange(evt: zk.Event & {data: zul.db.CalendarOnChangeData}): void {
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

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._bindTimezoneEvt();

		zWatch.listen({onFloatUp: this});
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onFloatUp: this});
		this._unbindfTimezoneEvt();
		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = undefined;
		}
		super.unbind_(skipper, after, keepRod);
	}

	_bindTimezoneEvt(): void {
		var db = this.parent,
			select = db.$n<HTMLSelectElement>('dtzones');
		if (select) {
			select.disabled = (db.isTimeZonesReadonly() ? 'disable' : '') as unknown as boolean;
			db.domListen_(select, 'onChange', '_doTimeZoneChange');
			db._setTimeZonesIndex();
		}
	}

	_unbindfTimezoneEvt(): void {
		var db = this.parent,
			select = db.$n('dtzones');
		if (select)
			db.domUnlisten_(select, 'onChange', '_doTimeZoneChange');
	}

	override _setView(val: string, force?: number): this {
		if (this.parent.getTimeFormat())
			this.parent._tm.setVisible(val == 'day');
		super._setView(val, force);

		// ZK-2047: when sync shadow, the calendar popup should be above the pdf
		if (zk.ie > 9) {
			this.syncShadow();
		}

		return this;
	}

	// ZK-2308
	override doKeyDown_(evt: zk.Event): void {
		super.doKeyDown_(evt);
		if (evt.keyCode == 27) {
			this.parent.escPressed_(evt);
		}
	}

	override animationSpeed_(): number | 'slow' | 'fast' {
		return zk(this.parent).getAnimationSpeed('_default');
	}

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
						this._markCal({sameMonth: true}); // optimize
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

	static _equalDate(d1: DateImpl | undefined, d2: DateImpl | undefined): boolean {
		return !!((d1 == d2) || (d1 && d2 && d1.getTime() == d2.getTime()));
	}
}

@zk.WrapClass('zul.db.CalendarTime')
export class CalendarTime extends zul.db.Timebox {
	override parent!: Datebox;

	constructor() {
		super();
		this.listen({onChanging: this}, -1000);
	}

	onChanging(evt: zk.Event & {data: zul.db.CalendarOnChangeData}): void {
		var db = this.parent,
			oldDate = db.getValue() || db._pop.getValue(),
			// ZK-2382 we must do the conversion with date and time in the same time
			// otherwise the result may be affcted by DST adjustment
			dateTime = db.coerceToString_(oldDate, _innerDateFormat) + evt.data.value, //onChanging's data is string
			pattern = _innerDateFormat + db.getTimeFormat();

		// add 'AM' by default, if pattern specified AMPM
		dateTime += pattern.indexOf('a') > -1 ?
				dateTime.indexOf('AM') < 0 && dateTime.indexOf('PM') < 0 ? 'AM' : '' : '';
		var	date = db.coerceFromString_(dateTime, pattern);

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
export let DateboxCtrl = {
	/**
	 * Returns whether to preserve the focus state.
	 * @param zk.Widget wgt a widget
	 * @return boolean
	 */
	isPreservedFocus(wgt: zk.Widget): boolean {
		return true;
	}
};
zul.db.DateboxCtrl = DateboxCtrl;