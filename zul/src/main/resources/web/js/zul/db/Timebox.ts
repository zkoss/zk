/* Timebox.ts

	Purpose:
		testing textbox.intbox.spinner,timebox,doublebox,longbox and decimalbox on zk5
	Description:

	History:
		Thu June 11 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var LEGAL_CHARS = 'ahKHksmz',
	/*constant for MINUTE (m) field alignment.
	 * @type int
	 */
	MINUTE_FIELD = 1,
	/*constant for SECOND (s) field alignment.
	 * @type int
	 */
	SECOND_FIELD = 2,
	/*constant for AM_PM (a) field alignment.
	 * @type int
	 */
	AM_PM_FIELD = 3,
	/*constant for HOUR0 (H) field alignment. (Hour in day (0-23))
	 * @type int
	 */
	HOUR0_FIELD = 4,
	/*constant for HOUR1 (k) field alignment. (Hour in day (1-24))
	 * @type int
	 */
	HOUR1_FIELD = 5,
	/*constant for HOUR2 (h) field alignment. (Hour in am/pm (1-12))
	 * @type int
	 */
	HOUR2_FIELD = 6,
	/*constant for HOUR3 (K) field alignment. (Hour in am/pm (0-11))
	 * @type int
	 */
	HOUR3_FIELD = 7,
	globallocalizedSymbols: Record<string, zk.LocalizedSymbols> = {};

@zk.WrapClass('zul.db.Timebox')
export class Timebox extends zul.inp.FormatWidget<DateImpl> {
	_buttonVisible = true;
	override readonly _format = 'HH:mm';
	_timezoneAbbr = '';
	_fmthdler!: TimeHandler[];
	_timeZone?: string;
	_unformater?: string;
	static _unformater?: zul.db.Unformater;
	_changed?: boolean;
	type?: number;
	timerId?: number;
	lastPos?: number;
	_lastPos?: number;
	_constraint?: string;
	_currentbtn?: HTMLElement;

	constructor() {
		super();
		Timebox._updFormat(this, this._format);
	}

	getTimezoneAbbr(): string {
		return this._timezoneAbbr;
	}

	setTimezoneAbbr(v: string, opts?: Record<string, boolean>): this {
		const o = this._timezoneAbbr;
		this._timezoneAbbr = v;

		if (o !== v || (opts && opts.force)) {
			Timebox._updFormat(this, this._format);
		}

		return this;
	}

	/** Sets the time zone ID that this time box belongs to.
	 * @param String timezone the time zone's ID, such as "America/Los_Angeles".
	 * @since 9.0.0
	 */
	setTimeZone(v: string, opts?: Record<string, boolean>): this {
		const o = this._timeZone;
		this._timeZone = v;

		if (o !== v || (opts && opts.force)) {
			this._value && this._value.tz(v);
		}

		return this;
	}

	/** Returns the time zone ID that this time box belongs to.
	 * @return String the time zone's ID, such as "America/Los_Angeles".
	 * @since 9.0.0
	 */
	override getTimeZone(): string | undefined {
		return this._timeZone;
	}

	/** Returns whether the button (on the right of the textbox) is visible.
	 * <p>Default: true.
	 * @return boolean
	 */
	isButtonVisible(): boolean {
		return this._buttonVisible;
	}

	/** Sets whether the button (on the right of the textbox) is visible.
	 * @param boolean buttonVisible
	 */
	setButtonVisible(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._buttonVisible;
		this._buttonVisible = v;

		if (o !== v || (opts && opts.force)) {
			zul.inp.RoundUtl.buttonVisible(this, v);
		}

		return this;
	}

	/** Sets the unformater function. This method is called from Server side.
	 * @param String unf the unformater function
	 */
	setUnformater(unf: string, opts?: Record<string, boolean>): this {
		const o = this._unformater;
		this._unformater = unf;

		if (o !== unf || (opts && opts.force)) {
			eval('Timebox._unformater = ' + unf); // eslint-disable-line no-eval
		}

		return this;
	}

	/** Returns the unformater.
	 * @return String the unformater function
	 */
	getUnformater(): string | undefined {
		return this._unformater;
	}

	getLocalizedSymbols(): zk.LocalizedSymbols | undefined {
		return this._localizedSymbols;
	}

	setLocalizedSymbols(val: [string, zk.LocalizedSymbols]): this {
		if (val) {
			if (!globallocalizedSymbols[val[0]])
				globallocalizedSymbols[val[0]] = val[1];
			this._localizedSymbols = globallocalizedSymbols[val[0]];
		}
		return this;
	}

	/** Sets the constraint.
	 * <p>Default: null (means no constraint all all).
	 * @param String cst
	 */
	override setConstraint(cst?: string, opts?: Record<string, boolean>): this {
		const o = this._constraint;
		this._constraint = cst;

		if (o !== cst || (opts && opts.force)) {
			if (typeof cst == 'string' && cst.charAt(0) != '['/*by server*/)
				this._cst = new zul.inp.SimpleLocalTimeConstraint(cst, this);
			else
				this._cst = cst;
			if (this._cst)
				this._reVald = true; //revalidate required
			// FIXME: never assigned
			// if (this._pop) {
			// 	this._pop.setConstraint(this._constraint);
			// 	this._pop.rerender();
			// }
		}

		return this;
	}

	/** Returns the constraint, or null if no constraint at all.
	 * @return String
	 */
	override getConstraint(): string | undefined {
		return this._constraint;
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
	 * Sets the time zone ID that this time box belongs to.
	 * @param String timezone the time zone's ID, such as "America/Los_Angeles".
	 * @deprecated Use {@link #setTimeZone(String)} instead.
	 */
	setTimezone(v: string): this {
		return this.setTimeZone(v);
	}

	/** Returns the time zone ID that this time box belongs to.
	 * @return String the time zone's ID, such as "America/Los_Angeles".
	 * @since 8.5.1
	 * @deprecated Use {@link #getTimeZone()} instead.
	 */
	getTimezone(): string | undefined {
		return this._timeZone;
	}

	override inRoundedMold(): boolean {
		return true;
	}

	override setFormat(fmt: string, opts?: Record<string, boolean> | undefined): this {
		fmt = fmt ? fmt.replace(/'/g, '') : fmt;
		Timebox._updFormat(this, fmt);
		return super.setFormat(fmt, opts);
	}

	override setValue(value: DateImpl, fromServer?: boolean): this {
		var tz = this.getTimeZone();
		if (tz && value) value.tz(tz);
		if (fromServer && value == null) //Bug ZK-1322: if from server side, return empty string
			this._changed = false;
		return super.setValue(value, fromServer);
	}

	override coerceToString_(date?: DateImpl): string {
		if (!this._changed && !date && arguments.length) return '';
		var out = '', th: TimeHandler, text: string, offset: number | boolean;
		for (var i = 0, f = this._fmthdler, l = f.length; i < l; i++) {
			th = f[i];
			text = th.format(date);
			out += text;
			//sync handler index
			if (th.type && (offset = th.isSingleLength()) !== false
				&& ((offset as number) += text.length - 1))
				th._doShift(this, offset as number);
		}
		return out;
	}

	override coerceFromString_(val: string | undefined): DateImpl | undefined {
		var unf = Timebox._unformater,
			tz = this.getTimeZone();
		if (unf && jq.isFunction(unf)) {
			var cusv = unf(val);
			if (cusv) {
				this._shortcut = val;
				return cusv;
			}
		}
		if (!val) return undefined;

		// F65-ZK-1825: use this._value instead of "today"
		// We cannot use this._value in this case, which won't trigger onChange
		// event. Using clone date instead.
		var date = this._value ? Dates.newInstance(this._value) : zUtl.today(this._format, tz),
			hasAM, isAM,
			fmt: TimeHandler[] = [],
			fmtObeyCount: boolean[] = [],
			emptyCount = 0;
		date.setSeconds(0);
		date.setMilliseconds(0);

		for (var i = 0, f = this._fmthdler, l = f.length; i < l; i++) {
			var th = f[i];
			if (th.type == AM_PM_FIELD) {
				hasAM = true;
				isAM = th.unformat(date, val, {});
				if (!th.getText(val).trim().length)
					emptyCount++;
			} else if (th.type) {
				var shouldObeyCount = i + 1 < l && Timebox._shouldObeyCount(f[i + 1].type);
				fmt.push(th);
				fmtObeyCount.push(shouldObeyCount);
				th.parse(val, {obeyCount: shouldObeyCount}); // in order to shift if necessary
				if (!th.getText(val).trim().length) // ZK-4342: no need to pass obeyCount to determine if part is empty
					emptyCount++;
			}
		}

		if (fmt.length == (hasAM ? --emptyCount : emptyCount)) {
			this._changed = false;//for return empty string
			return;
		}

		for (var i = 0, l = fmt.length; i < l; i++) {
			if (!hasAM && (fmt[i].type == HOUR2_FIELD || fmt[i].type == HOUR3_FIELD))
				isAM = true;
			date = fmt[i].unformat(date, val, {am: isAM, obeyCount: fmtObeyCount[i]}) as DateImpl; // FIXME: inconsistent use of unformat
		}
		return date;
	}

	override onSize(): void {
		var inp = this.getInputNode();
		if (inp && this._value && !inp.value)
			inp.value = this.coerceToString_(this._value);
	}

	onHide = undefined;
	validate = undefined;

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		if (evt.domTarget == this.getInputNode())
			this._doCheckPos();
		super.doClick_(evt, popupOnly);
	}

	override doKeyPress_(evt: zk.Event): void {
		if (zk.opera && evt.keyCode != 9) {
			evt.stop();
			return;
		}
		super.doKeyPress_(evt);
	}

	_doBeforeInput(evt: zk.Event): void {
		var inp = this.getInputNode()!;
		if (inp.disabled || inp.readOnly)
			return;

		// control input keys only when no custom unformater is given
		if (!Timebox._unformater) {
			var char = (evt.domEvent!.originalEvent as InputEvent).data!;
			if (/\d/.test(char)) {
				this._doType(parseInt(char));
				evt.stop();
			}
		}
	}

	override doKeyDown_(evt: zk.Event): void {
		var inp = this.getInputNode()!;
		if (inp.disabled || inp.readOnly)
			return;

		// control input keys only when no custom unformater is given
		if (!Timebox._unformater) {
			var code = evt.keyCode;
			switch (code) {
			case 48: case 96://0
			case 49: case 97://1
			case 50: case 98://2
			case 51: case 99://3
			case 52: case 100://4
			case 53: case 101://5
			case 54: case 102://6
			case 55: case 103://7
			case 56: case 104://8
			case 57: case 105://9
				code = code - (code >= 96 ? 96 : 48);
				this._doType(code);
				evt.stop();
				return;
			case 35://end
				this.lastPos = inp.value.length;
				return;
			case 36://home
				this.lastPos = 0;
				return;
			case 37://left
				if (this.lastPos! > 0)
					this.lastPos!--;
				return;
			case 39://right
				if (this.lastPos! < inp.value.length)
					this.lastPos!++;
				return;
			case 38://up
				this._doUp();
				evt.stop();
				return;
			case 40://down
				this._doDown();
				evt.stop();
				return;
			case 46://del
				this._doDel();
				evt.stop();
				return;
			case 8://backspace
				this._doBack();
				evt.stop();
				return;
			case 9:
				// do nothing
				break;
			case 13: case 27://enter,esc,tab
				break;
			default:
				if (!(code >= 112 && code <= 123) //F1-F12
						&& !evt.ctrlKey && !evt.metaKey && !evt.altKey) {
					if (evt.shiftKey && code == 45) break; // Allow macOS paste (shift + insert)
					evt.stop();
				}
			}
		}
		super.doKeyDown_(evt);
	}

	override doPaste_(evt: zk.Event): void {
		this._updateChanged();
		super.doPaste_(evt);
	}

	_updateChanged(): void {
		var inp = this.getInputNode()!,
			self = this,
			curVal = inp.value;
		setTimeout(function () {
			var inpNode = self.getInputNode();
			if (inpNode) {
				if (curVal != inpNode.value) {
					self._changed = true;
				}
			}
		}, 0);
	}

	_ondropbtnup(evt: zk.Event): void {
		this.domUnlisten_(document.body, 'onZMouseup', '_ondropbtnup');
		this._stopAutoIncProc();
		this._currentbtn = undefined;
	}

	_btnDown(evt: zk.Event): void { // TODO: format the value first
		if (!this._buttonVisible || this._disabled) return;

		// cache it for IE and others to keep the same position at the first time being clicked.
		this._lastPos = this._getPos();

		var btn = this.$n('btn'),
			inp = this.getInputNode()!;

		if (!zk.dragging) {
			if (this._currentbtn) // just in case
				this._ondropbtnup(evt);

			this.domListen_(document.body, 'onZMouseup', '_ondropbtnup');
			this._currentbtn = btn;
		}

		// if btn down before blur, needs to convert to real time string first
		if (inp.value && Timebox._unformater)
			inp.value = this.coerceToString_(this.coerceFromString_(inp.value));
		if (!inp.value)
			inp.value = this.coerceToString_();

		var ofs = zk(btn).revisedOffset(),
			isOverUpBtn = (evt.pageY - ofs[1]) < btn!.offsetHeight / 2;
		if (zk.webkit) {
			zk(inp).focus(); //Bug ZK-1527: chrome and safari will trigger focus if executing setSelectionRange, focus it early here
		}

		var newLastPos = this._getPos();

		// Chrome and Firefox get wrong position at initial case
		if (this._lastPos != newLastPos)
			zk(inp).setSelectionRange(this._lastPos);

		if (isOverUpBtn) { //up
			this._doUp();
			this._startAutoIncProc(true);
		} else {
			this._doDown();
			this._startAutoIncProc(false);
		}

		this._changed = true;
		delete this._shortcut;

		zk.Widget.mimicMouseDown_(this); //set zk.currentFocus
		zk(inp).focus(); //we have to set it here; otherwise, if it is in popup of
			//datebox, datebox's onblur will find zk.currentFocus is null

		// disable browser's text selection
		evt.stop();
	}

	_btnUp(evt: zk.Event): void {
		if (!this._buttonVisible || this._disabled || zk.dragging) return;

		if (zk.opera) zk(this.getInputNode()).focus();
			//unfortunately, in opera, it won't gain focus if we set in _btnDown

		this._onChanging();
		this._stopAutoIncProc();

		if (zk.webkit && this._lastPos)
			zk(this.getInputNode()).setSelectionRange(this._lastPos, this._lastPos);
	}

	_getPos(): number {
		return zk(this.getInputNode()).getSelectionRange()[0];
	}

	_doCheckPos(): void {
		this.lastPos = this._getPos();
	}

	_doUp(): void {
		this._changed = true;
		this.getTimeHandler().increase(this, 1);
		this._onChanging();
	}

	_doDown(): void {
		this._changed = true;
		this.getTimeHandler().increase(this, -1);
		this._onChanging();
	}

	_doBack(): void {
		this._changed = true;
		this.getTimeHandler().deleteTime(this, true);
	}

	_doDel(): void {
		this._changed = true;
		this.getTimeHandler().deleteTime(this, false);
	}

	_doType(val: number): void {
		this._changed = true;
		this.getTimeHandler().addTime(this, val);
	}

	getTimeHandler(): TimeHandler {
		var sr = zk(this.getInputNode()).getSelectionRange(),
			start = sr[0],
			end = sr[1],
			//don't use [0] as the end variable, it may have a bug when the format is aHH:mm:ss
			//when use UP/Down key to change the time
			hdler: TimeHandler | undefined;

		// Bug ZK-434
		for (var i = 0, f = this._fmthdler, l = f.length; i < l; i++) {
			if (!f[i].type) continue;
			if (f[i].index[0] <= start) {
				hdler = f[i];
				if (f[i].index[1] + 1 >= end)
					return f[i];
			}
		}
		return hdler || this._fmthdler[0];
	}

	getNextTimeHandler(th: TimeHandler): TimeHandler {
		var f = this._fmthdler,
			index = f.$indexOf(th),
			lastHandler: TimeHandler;

		while ((lastHandler = f[++index])
				&& (!lastHandler.type || lastHandler.type == AM_PM_FIELD));

		return lastHandler;
	}

	_startAutoIncProc(up: boolean): void {
		if (this.timerId)
			clearInterval(this.timerId);
		var self = this,
			fn: '_doUp' | '_doDown' = up ? '_doUp' : '_doDown';
		this.timerId = setInterval(function () {
			if (zk.webkit && self._lastPos)
				zk(self.getInputNode()).setSelectionRange(self._lastPos, self._lastPos);
			self[fn]();
		}, 300);
		jq(this.$n('btn-' + (up ? 'up' : 'down'))).addClass(this.$s('active'));
	}

	_stopAutoIncProc(): void {
		if (this.timerId)
			clearTimeout(this.timerId);
		// this.currentStep = this.defaultStep; // FIXME: both properties are not initialized
		this.timerId = undefined;
		jq('.' + this.$s('icon'), this.$n('btn')).removeClass(this.$s('active'));
	}

	override doFocus_(evt: zk.Event): void {
		super.doFocus_(evt);
		var inp = this.getInputNode()!,
			selrng = zk(inp).getSelectionRange();

		if (!inp.value)
			inp.value = Timebox._unformater ? '' : this.coerceToString_();

		this._doCheckPos();

		// Bug 2688620
		if (selrng[0] !== selrng[1]) {
			zk(inp).setSelectionRange(selrng[0], selrng[1]);
			this.lastPos = selrng[1];
		}

		zul.inp.RoundUtl.doFocus_(this);
	}

	override doBlur_(evt: zk.Event): void {
		// skip onchange, Bug 2936568
		if (!this._value && !this._changed && !Timebox._unformater)
			this.getInputNode()!.value = this._defRawVal = '';

		super.doBlur_(evt);

		zul.inp.RoundUtl.doBlur_(this);
	}

	override afterKeyDown_(evt: zk.Event, simulated?: boolean): boolean {
		if (!simulated && this._inplace)
			jq(this.$n_()).toggleClass(this.getInplaceCSS(), evt.keyCode == 13);

		return super.afterKeyDown_(evt, simulated);
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		var btn: HTMLElement | undefined;

		if (zk.android && zk.chrome)
			this.domListen_(this.getInputNode()!, 'onBeforeInput', '_doBeforeInput');
		if (btn = this.$n('btn'))
			this.domListen_(btn, 'onZMouseDown', '_btnDown')
				.domListen_(btn, 'onZMouseUp', '_btnUp');
		zWatch.listen({onSize: this});
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this.timerId) {
			clearTimeout(this.timerId);
			this.timerId = undefined;
		}
		zWatch.unlisten({onSize: this});
		var btn = this.$n('btn');
		if (btn) {
			this.domUnlisten_(btn, 'onZMouseDown', '_btnDown')
				.domUnlisten_(btn, 'onZMouseUp', '_btnUp');
		}
		if (zk.android && zk.chrome)
			this.domUnlisten_(this.getInputNode()!, 'onBeforeInput', '_doBeforeInput');
		this._changed = false;
		super.unbind_(skipper, after, keepRod);
	}

	getBtnUpIconClass_(): string {
		return 'z-icon-angle-up';
	}

	getBtnDownIconClass_(): string {
		return 'z-icon-angle-down';
	}

	static _updFormat(wgt: Timebox, fmt: string): void {
		var index: TimeHandler[] = [],
			APM = wgt._localizedSymbols ? wgt._localizedSymbols.APM! : zk.APM;
		for (var i = 0, l = fmt.length; i < l; i++) {
			var c = fmt.charAt(i);
			switch (c) {
			case 'a':
				var len = APM[0].length;
				index.push(new zul.inp.AMPMHandler([i, i + len - 1], AM_PM_FIELD, wgt));
				break;
			case 'K':
				var start = i,
					end = fmt.charAt(i + 1) == 'K' ? ++i : i;
				index.push(new zul.inp.HourHandler2([start, end], HOUR3_FIELD, wgt));
				break;
			case 'h':
				var start = i,
					end = fmt.charAt(i + 1) == 'h' ? ++i : i;
				index.push(new zul.inp.HourHandler([start, end], HOUR2_FIELD, wgt));
				break;
			case 'H':
				var start = i,
					end = fmt.charAt(i + 1) == 'H' ? ++i : i;
				index.push(new zul.inp.HourInDayHandler([start, end], HOUR0_FIELD, wgt));
				break;
			case 'k':
				var start = i,
					end = fmt.charAt(i + 1) == 'k' ? ++i : i;
				index.push(new zul.inp.HourInDayHandler2([start, end], HOUR1_FIELD, wgt));
				break;
			case 'm':
				var start = i,
					end = fmt.charAt(i + 1) == 'm' ? ++i : i;
				index.push(new zul.inp.MinuteHandler([start, end], MINUTE_FIELD, wgt));
				break;
			case 's':
				var start = i,
					end = fmt.charAt(i + 1) == 's' ? ++i : i;
				index.push(new zul.inp.SecondHandler([start, end], SECOND_FIELD, wgt));
				break;
			case 'z':
				// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
				index.push({index: [i, i], format: (function (text) {
					return function () {
						return text;
					};
				})(wgt._timezoneAbbr)} as TimeHandler);
				break;
			default:
				var ary = '',
					start = i,
					end = i;

				while ((ary += c) && ++end < l) {
					c = fmt.charAt(end);
					if (LEGAL_CHARS.indexOf(c) != -1) {
						end--;
						break;
					}
				}
				// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
				index.push({index: [start, end], format: (function (text) {
					return function () {
						return text;
					};
				})(ary)} as TimeHandler);
				i = end;
			}
		}
		for (var shift, i = 0, l = index.length; i < l; i++) {
			if (index[i].type == AM_PM_FIELD) {
				shift = index[i].index[1] - index[i].index[0];
				if (!shift) break; // no need to shift.
			} else if (shift) {
				index[i].index[0] += shift;
				index[i].index[1] += shift;
			}
		}
		wgt._fmthdler = index;
	}

	static _shouldObeyCount(nextType: number): boolean {
		switch (nextType) {
			case MINUTE_FIELD:
			case SECOND_FIELD:
			case HOUR0_FIELD:
			case HOUR1_FIELD:
			case HOUR2_FIELD:
			case HOUR3_FIELD:
				return true;
			default:
				return false;
		}
	}
}

@zk.WrapClass('zul.inp.TimeHandler')
export class TimeHandler extends zk.Object {
	maxsize = 59;
	minsize = 0;
	digits = 2;
	index: number[];
	type: number;
	wgt: Timebox;

	constructor(index: number[], type: number, wgt: Timebox) {
		super();
		this.index = index;
		this.type = type;
		if (index[0] == index[1])
			this.digits = 1;
		this.wgt = wgt;
	}

	format(date?: DateImpl): string {
		return '00';
	}

	unformat(date: DateImpl, val: string, opt: Record<string, unknown>): DateImpl | boolean {
		return date;
	}

	increase(wgt: Timebox, up: number): void {
		var inp = wgt.getInputNode()!,
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value,
			text: string | number = this.getText(val),
			singleLen = this.isSingleLength() !== false,
			ofs: number | undefined;

		text = zk.parseInt(singleLen ? text :
				text.replace(/ /g, '0')) + up;

		var max = this.maxsize + 1;
		if (text < this.minsize) {
			text = this.maxsize;
			ofs = 1;
		} else if (text >= max) {
			text = this.minsize;
			ofs = -1;
		} else if (singleLen)
			ofs = (up > 0) ?
					(text == 10) ? 1 : 0 :
					(text == 9) ? -1 : 0;

		if (text < 10 && !singleLen)
				text = '0' + text;

		inp.value = val.substring(0, start) + text + val.substring(end);

		if (singleLen && ofs) {
			this._doShift(wgt, ofs);
			end += ofs;
		}

		zk(inp).setSelectionRange(start, end);
	}

	deleteTime(wgt: Timebox, backspace: boolean): void {
		var inp = wgt.getInputNode()!,
			sel = zk(inp).getSelectionRange(),
			pos = sel[0],
			val = inp.value,
			maxLength = TimeHandler._getMaxLen(wgt);

		// clean over text
		if (val.length > maxLength) {
			val = inp.value = val.substr(0, maxLength);
			sel = [Math.min(sel[0], maxLength), Math.min(sel[1], maxLength)];
			pos = sel[0];
		}

		if (pos != sel[1]) {
			//select delete
			inp.value = val.substring(0, pos) + TimeHandler._cleanSelectionText(wgt, this)
							+ val.substring(sel[1]);
		} else {
			var fmthdler = wgt._fmthdler,
				index = fmthdler.$indexOf(this),
				ofs = backspace ? -1 : 1,
				ofs2 = backspace ? 0 : 1,
				ofs3 = backspace ? 1 : 0,
				hdler: TimeHandler,
				posOfs: number | boolean | undefined;
			if (pos == this.index[ofs2] + ofs2) {// on start or end
				//delete by sibling handler
				if (hdler = fmthdler[index + ofs * 2])
					pos = hdler.index[ofs3] + ofs3 + ofs;
			} else {// delete self
				pos += ofs;
				hdler = this;
			}
			if (hdler) {
				posOfs = hdler.isSingleLength();
				inp.value = val.substring(0, (ofs3 += pos) - 1)
					+ (posOfs ? '' : ' ') + val.substring(ofs3);
				if (posOfs)
					hdler._doShift(wgt, posOfs as number);
			}
			if (posOfs && !backspace) pos--;
		}
		zk(inp).setSelectionRange(pos, pos);
	}

	_addNextTime(wgt: Timebox, num: number): void {
		var inp = wgt.getInputNode(),
			index: number,
			NTH: TimeHandler;
		if (NTH = wgt.getNextTimeHandler(this)) {
			index = NTH.index[0];
			zk(inp).setSelectionRange(index,
				Math.max(index,
					zk(inp).getSelectionRange()[1]));
			NTH.addTime(wgt, num);
		}
	}

	addTime(wgt: Timebox, num: number): void {
		var inp = wgt.getInputNode()!,
			sel = zk(inp).getSelectionRange(),
			val = inp.value,
			pos = sel[0],
			maxLength = TimeHandler._getMaxLen(wgt),
			posOfs = this.isSingleLength();

		// clean over text
		if (val.length > maxLength) {
			val = inp.value = val.substr(0, maxLength);
			sel = [Math.min(sel[0], maxLength), Math.min(sel[1], maxLength)];
			pos = sel[0];
		}

		if (pos == maxLength)
			return;

		// first number (hendle max bound)
		if (pos == this.index[0]) {
			var text = this.getText(val)
						.substring((posOfs === 0) ? 0 : 2).trim(),
				i;
			if (!text.length) text = '0';

			if ((i = zk.parseInt(num + text)) > this.maxsize) {
				if (posOfs !== 0) {
					val = inp.value = val.substring(0, pos) + (posOfs ? '0' : '00')
						+ val.substring(pos + 2);
					if (!posOfs) pos++;
					zk(inp).setSelectionRange(pos, Math.max(sel[1], pos));
					sel = zk(inp).getSelectionRange();
				}
				if (posOfs)
					this._doShift(wgt, posOfs as number);
			}
		} else if (pos == (this.index[1] + 1)) {//end of handler
			var i;
			if (posOfs !== false) {
				var text = this.getText(val);
				if ((i = zk.parseInt(text + num)) <= this.maxsize) {//allow add number
					if (i && i < 10) // 1-9
						pos--;
					else if (i || posOfs) { // 0 or larger then 10, except zero and non-posOfs
						val = inp.value = val.substring(0, (pos + (posOfs as number)))
								+ (posOfs ? '' : '0') + val.substring(pos);
						if (i) // larger then 10
							this._doShift(wgt, 1);
						else { // 0
							zk(inp).setSelectionRange(pos, Math.max(sel[1], pos));
							if (posOfs)//2 digits zero
								this._doShift(wgt, posOfs as number);
						}
					}
				}
			}

			if (!i || i > this.maxsize) {
				this._addNextTime(wgt, num);
				return;
			}
		}

		if (pos != sel[1]) {
			//select edit
			var s = TimeHandler._cleanSelectionText(wgt, this),
				ofs: number | undefined;
			//in middle position
			if (posOfs !== false && (ofs = pos - this.index[1]))
				this._doShift(wgt, ofs);

			inp.value = val.substring(0, pos++) + num
				+ s.substring(ofs ? 0 : 1)
				+ val.substring(sel[1]);
		} else {
			inp.value = val.substring(0, pos)
				+ num + val.substring(++pos);
		}
		wgt.lastPos = pos;
		zk(inp).setSelectionRange(pos, pos);
	}

	getText(val: string, obeyCount?: boolean): string {
		var start = this.index[0],
			end = this.index[1] + 1;
		return obeyCount !== false ? val.substring(start, end) : val.substring(start);
	}

	_doShift(wgt: Timebox, shift: number): void {
		var f = wgt._fmthdler,
			index = f.$indexOf(this),
			NTH: TimeHandler;
		this.index[1] += shift;
		while (NTH = f[++index]) {
			NTH.index[0] += shift;
			NTH.index[1] += shift;
		}
	}

	isSingleLength(): boolean | number {
		return this.digits == 1 && (this.index[0] - this.index[1]);
	}

	parse(val: string, opt: {obeyCount?: boolean}): number {
		var text = this.getText(val, opt.obeyCount),
			parsed = /^\s*\d*/.exec(text)!,
			offset = parsed.length ? parsed[0].length - (this.index[1] - this.index[0] + 1) : 0;
		if (offset) {
			this._doShift(this.wgt, offset);
		}
		return zk.parseInt(text);
	}

	static _getMaxLen(wgt: Timebox): number {
		var val = wgt.getInputNode()!.value,
			len = 0,
			th: TimeHandler,
			lastTh: TimeHandler | undefined;
		for (var i = 0, f = wgt._fmthdler, l = f.length; i < l; i++) {
			th = f[i];
			if (i == l - 1) {
				len += th.format().length;
			} else
				len += (th.type ? th.getText(val) : th.format()).length;
			if (th.type) lastTh = th;
		}
		return (lastTh!.digits == 1) ? ++len : len;
	}

	static _cleanSelectionText(wgt: Timebox, startHandler: TimeHandler): string {
		var inp = wgt.getInputNode(),
			sel = zk(inp).getSelectionRange(),
			pos = sel[0],
			selEnd = sel[1],
			fmthdler = wgt._fmthdler,
			index = fmthdler.$indexOf(startHandler),
			text = '',
			hdler = startHandler,
			isFirst = true,
			prevStart: number | undefined,
			ofs,
			hStart: number,
			hEnd,
			posOfs: number | boolean;

		//restore separator
		do {
			hStart = hdler.index[0];
			hEnd = hdler.index[1] + 1;

			if (hdler.type && (posOfs = hdler.isSingleLength())) {
				//sync handler index
				hdler._doShift(wgt, posOfs as number);
				selEnd--;
			}

			//latest one
			if (hEnd >= selEnd && hdler.type) {
				ofs = selEnd - hStart;
				while (ofs-- > 0) //replace by space (after)
					text += ' ';
				break;
			}

			if (hdler.type) {
				prevStart = isFirst ? pos : hStart;
				isFirst = false;
				continue;
			}
			ofs = hStart - prevStart!;
			while (ofs-- > 0) //replace by space (before)
				text += ' ';

			text += hdler.format();

		} while (hdler = fmthdler[++index]);
		return text;
	}
}

@zk.WrapClass('zul.inp.HourInDayHandler')
export class HourInDayHandler extends zul.inp.TimeHandler {
	override maxsize = 23;
	override minsize = 0;

	override format(date?: DateImpl): string {
		var singleLen = this.digits == 1;
		if (!date) return singleLen ? '0' : '00';
		else {
			var h: number | string = date.getHours();
			if (!singleLen && h < 10)
				h = '0' + h;
			return h.toString();
		}
	}

	override unformat(date: DateImpl, val: string, opt: Record<string, unknown>): DateImpl {
		date.setHours(this.parse(val, opt));
		return date;
	}
}

@zk.WrapClass('zul.inp.HourInDayHandler2')
export class HourInDayHandler2 extends zul.inp.TimeHandler {
	override maxsize = 24;
	override minsize = 1;

	override format(date?: DateImpl): string {
		if (!date) return '24';
		else {
			var h: number | string = date.getHours();
			if (h == 0)
				h = '24';
			else if (this.digits == 2 && h < 10)
				h = '0' + h;
			return h.toString();
		}
	}

	override unformat(date: DateImpl, val: string, opt: Record<string, unknown>): DateImpl {
		var hours = this.parse(val, opt);
		if (hours >= this.maxsize)
			hours = 0;
		date.setHours(hours);
		return date;
	}
}

@zk.WrapClass('zul.inp.HourHandler')
export class HourHandler extends zul.inp.TimeHandler {
	override maxsize = 12;
	override minsize = 1;

	override format(date?: DateImpl): string {
		if (!date) return '12';
		else {
			var h: number | string = date.getHours();
			h = (h % 12);
			if (h == 0)
				h = '12';
			else if (this.digits == 2 && h < 10)
				h = '0' + h;
			return h.toString();
		}
	}

	override unformat(date: DateImpl, val: string, opt: Record<string, unknown>): DateImpl {
		var hours = this.parse(val, opt);
		if (hours >= this.maxsize)
			hours = 0;
		date.setHours(opt.am ? hours : hours + 12);
		return date;
	}
}

@zk.WrapClass('zul.inp.HourHandler2')
export class HourHandler2 extends zul.inp.TimeHandler {
	override maxsize = 11;
	override minsize = 0;

	override format(date?: DateImpl): string {
		var singleLen = this.digits == 1;
		if (!date) return singleLen ? '0' : '00';
		else {
			var h: number | string = date.getHours();
			h = (h % 12);
			if (!singleLen && h < 10)
				h = '0' + h;
			return h.toString();
		}
	}

	override unformat(date: DateImpl, val: string, opt: Record<string, unknown>): DateImpl {
		var hours = this.parse(val, opt);
		date.setHours(opt.am ? hours : hours + 12);
		return date;
	}
}

@zk.WrapClass('zul.inp.MinuteHandler')
export class MinuteHandler extends zul.inp.TimeHandler {
	override format(date?: DateImpl): string {
		var singleLen = this.digits == 1;
		if (!date) return singleLen ? '0' : '00';
		else {
			var m: number | string = date.getMinutes();
			if (!singleLen && m < 10)
				m = '0' + m;
			return m.toString();
		}
	}

	override unformat(date: DateImpl, val: string, opt: Record<string, unknown>): DateImpl {
		date.setMinutes(this.parse(val, opt));
		return date;
	}
}

@zk.WrapClass('zul.inp.SecondHandler')
export class SecondHandler extends zul.inp.TimeHandler {
	override format(date?: DateImpl): string {
		var singleLen = this.digits == 1;
		if (!date) return singleLen ? '0' : '00';
		else {
			var s: number | string = date.getSeconds();
			if (!singleLen && s < 10)
				s = '0' + s;
			return s.toString();
		}
	}

	override unformat(date: DateImpl, val: string, opt: Record<string, unknown>): DateImpl {
		date.setSeconds(this.parse(val, opt));
		return date;
	}
}

@zk.WrapClass('zul.inp.AMPMHandler')
export class AMPMHandler extends zul.inp.TimeHandler {
	override format(date?: DateImpl): string {
		var APM = this.wgt._localizedSymbols ? this.wgt._localizedSymbols.APM! : zk.APM;
		if (!date)
			return APM[0];
		var h = date.getHours();
		return APM[h < 12 ? 0 : 1];
	}

	override unformat(date: DateImpl, val: string, opt: Record<string, unknown>): boolean {
		var text = this.getText(val).trim(),
			APM = this.wgt._localizedSymbols ? this.wgt._localizedSymbols.APM! : zk.APM;
		return (text.length == APM[0].length) ?
			APM[0] == text : true;
	}

	override addTime(wgt: Timebox, num: number): void {
		var inp = wgt.getInputNode()!,
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value,
			text = val.substring(start, end),
			APM = wgt._localizedSymbols ? wgt._localizedSymbols.APM! : zk.APM;
		//restore A/PM text
		if (text != APM[0] && text != APM[1]) {
			text = APM[0];
			inp.value = val.substring(0, start) + text + val.substring(end);
		}
		this._addNextTime(wgt, num);
	}

	// Bug ZK-434, we have to delete a sets of "AM/PM", rather than a single word "A/P/M"
	override deleteTime(wgt: Timebox, backspace: boolean): void {
		var inp = wgt.getInputNode()!,
			sel = zk(inp).getSelectionRange(),
			pos = sel[0],
			pos1 = sel[1],
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value;
		if (pos1 - pos > end - start)
			return super.deleteTime(wgt, backspace);

		var t = [''];
		for (var i = end - start; i > 0; i--)
			t.push(' ');

		inp.value = val.substring(0, start) + t.join('') + val.substring(end);
		zk(inp).setSelectionRange(start, start);
	}

	override increase(wgt: Timebox, up: number): void {
		var inp = wgt.getInputNode()!,
			start = this.index[0],
			end = this.index[1] + 1,
			val = inp.value,
			text = val.substring(start, end),
			APM = wgt._localizedSymbols ? wgt._localizedSymbols.APM! : zk.APM;

		text = APM[0] == text ? APM[1] : APM[0];
		inp.value = val.substring(0, start) + text + val.substring(end);
		zk(inp).setSelectionRange(start, end);
	}

	override parse(val: string, opt: {obeyCount?: boolean}): number {
		return zk.parseInt(this.getText(val).trim());
	}
}