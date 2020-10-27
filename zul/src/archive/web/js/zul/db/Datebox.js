/* Datebox.js

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
(function () {
var globallocalizedSymbols = {},
	_quotePattern = /\'/g, // move pattern string here to avoid jsdoc failure
	_innerDateFormat = 'yyyy/MM/dd ',
	Datebox =
/**
 * An edit box for holding a date.
 * <p>Default {@link #getZclass}: z-datebox.
 */
zul.db.Datebox = zk.$extends(zul.inp.FormatWidget, {
	_buttonVisible: true,
	_lenient: true,
	_strictDate: false,
	_selectLevel: 'day',
	$init: function () {
		this.$supers('$init', arguments);
		this.afterInit(this.$class._initPopup);
		this.listen({onChange: this}, -1000);
	},

	$define: {
		/**
		 * Sets whether the button (on the right of the textbox) is visible.
		 * @param boolean visible
		 */
		/**
		 * Returns whether the button (on the right of the textbox) is visible.
		 * <p>
		 * Default: true.
		 * @return boolean
		 */
		buttonVisible: function (v) {
			zul.inp.RoundUtl.buttonVisible(this, v);
		},
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
		 * <td><Text</a>
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
		/** Returns the full date format of the specified format
		 * @return String
		 */
		format: function () {
			if (this._pop) {
				this._pop.setFormat(this._format);
				if (this._value)
					this._value = this._pop.getTime();
			}
			var inp = this.getInputNode();
			if (inp)
				inp.value = this.getText();
		},
		/** Sets the constraint.
	 	 * <p>Default: null (means no constraint all all).
	 	 * @param String cst
	 	 */
		/** Returns the constraint, or null if no constraint at all.
		 * @return String
		 */
		constraint: function (cst) {
			if (typeof cst == 'string' && cst.charAt(0) != '['/*by server*/)
				this._cst = new zul.inp.SimpleDateConstraint(cst, this);
			else
				this._cst = cst;
			if (this._cst)
				this._reVald = true; //revalidate required
			if (this._pop) {
				this._pop.setConstraint(this._constraint);
				this._pop.rerender();
			}
		},
		/** Sets the time zone that this date box belongs to.
		 * @param String timezone the time zone's ID, such as "America/Los_Angeles".
		 */
		/** Returns the time zone that this date box belongs to.
		 * @return String the time zone's ID, such as "America/Los_Angeles".
		 */
		timeZone: function (timezone) {
			this._timezone = timezone;
			this._tm.setTimezone(timezone);
			this._setTimeZonesIndex();
			this._value && this._value.tz(timezone);
			this._pop && this._pop._fixConstraint();
			var cst = this._cst;
			if (cst && cst instanceof zul.inp.SimpleConstraint)
				cst.reparseConstraint();
		},
		/** Sets whether the list of the time zones to display is readonly.
		 * If readonly, the user cannot change the time zone at the client.
		 * @param boolean readonly
	 	 */
		/** Returns whether the list of the time zones to display is readonly.
		 * If readonly, the user cannot change the time zone at the client.
		 * @return boolean
		 */
		timeZonesReadonly: function (readonly) {
			var select = this.$n('dtzones');
			if (select) select.disabled = readonly ? 'disabled' : '';
		},
		/** Sets a catenation of a list of the time zones' ID, separated by comma,
		 * that will be displayed at the client and allow user to select.
		 * @param String dtzones a catenation of a list of the timezones' ID, such as
		 * <code>"America/Los_Angeles,GMT+8"</code>
		 */
		/** Returns a list of the time zones that will be displayed at the
		 * client and allow user to select.
		 * <p>Default: null
		 * @return Array
		 */
		displayedTimeZones: function (dtzones) {
			this._dtzones = dtzones ? dtzones.split(',') : null;
		},
		/** Sets the unformater function. This method is called from Server side.
		 * @param String unf the unformater function
		 */
		/** Returns the unformater.
		 * @return String the unformater function
		 */
		unformater: function (unf) {
			eval('Datebox._unformater = ' + unf); // eslint-disable-line no-eval
		},
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
		/** Returns whether or not date/time parsing is to be lenient.
		 *
		 * <p>
		 * With lenient parsing, the parser may use heuristics to interpret inputs
		 * that do not precisely match this object's format. With strict parsing,
		 * inputs must match this object's format.
		 * @return boolean
		 */
		lenient: null,
		localizedSymbols: [
			function (val) {
				if (val) {
					if (!globallocalizedSymbols[val[0]])
						globallocalizedSymbols[val[0]] = val[1];
					return globallocalizedSymbols[val[0]];
				}
				return val;
			},
			function () {

				// in this case, we cannot use setLocalizedSymbols() for Timebox
				if (this._tm)
					this._tm._localizedSymbols = this._localizedSymbols;
				if (this._pop)
					this._pop.setLocalizedSymbols(this._localizedSymbols);
			}
		],
		/**
		 * Sets whether enable to show the week number in the current calendar or
    	 * not. [ZK EE]
    	 * @since 6.5.0
    	 * @param boolean weekOfYear
		 */
	    /**
	     * Returns whether enable to show the week number in the current calendar
	     * or not.
	     * <p>Default: false
	     * @since 6.5.0
	     * @return boolean
	     */
		weekOfYear: function (v) {
			if (this._pop)
				this._pop.setWeekOfYear(v);
		},
		/**
		 * Sets whether enable to show the link that jump to today in day view
    	 * @since 8.0.0
    	 * @param boolean showTodayLink
		 */
	    /**
	     * Returns whether enable to show the link that jump to today in day view
	     * <p>Default: false
	     * @since 8.0.0
	     * @return boolean
	     */
		showTodayLink: function (v) {
			if (this._pop) {
				this._pop.setShowTodayLink(v);
			}
		},
		/**
		 * Sets the label of the link that jump to today in day view
		 * @since 8.0.4
		 * @param String todayLinkLabel
		 */
		/**
		 * Returns the label of the link that jump to today in day view
		 * @since 8.0.4
		 * @return String
		 */
		todayLinkLabel: function (v) {
			if (this._pop) {
				this._pop.setTodayLinkLabel(v);
			}
		},
		/**
		 * Sets whether or not date/time should be strict.
		 * If true, any invalid input like "Jan 0" or "Nov 31" would be refused.
		 * If false, it won't be checked and let lenient parsing decide.
		 * @since 8.6.0
		 * @param boolean strictDate
		 */
		/**
		 * Returns whether or not date/time should be strict.
		 * <p>Default: false.
		 * @since 8.6.0
		 * @return boolean
		 */
		strictDate: null,
		/**
		 * Sets the default datetime if the value is empty.
		 * @since 9.0.0
		 * @param Date defaultDateTime Default datetime. null means current datetime.
		 */
		/**
		 * Returns the default datetime if the value is empty.
		 * <p>Default: null (means current datetime).
		 * @since 9.0.0
		 * @return Date
		 */
		defaultDateTime: null,
		/**
		 * Sets the level that a user can select.
		 * The valid options are "day", "month", and "year".
		 *
		 * @param String selectLevel the level that a user can select
		 * @since 9.5.1
		 */
		/**
		 * Returns the level that a user can select.
		 * <p>
		 * Default: "day"
		 * @return String
		 * @since 9.5.1
		 */
		selectLevel: null
	},
	/**
	 * Returns the iconSclass name of this Datebox.
	 * @return String the iconSclass name
	 * @since 8.6.2
	 */
	getIconSclass: function () {
		return 'z-icon-calendar';
	},
	inRoundedMold: function () {
		return true;
	},
	_setTimeZonesIndex: function () {
		var select = this.$n('dtzones');
		if (select && this._timezone) {
			var opts = jq(select).children('option');
			for (var i = opts.length; i--;) {
				if (opts[i].text == this._timezone) select.selectedIndex = i;
			}
		}
	},
	onSize: function () {
		if (this.isOpen())
			this.$class._reposition(this, true);
	},
	/** Returns the Time format of the specified format
	 * @return String
	 */
	getTimeFormat: function () {
	//Note: S (milliseconds not supported yet)
		var fmt = this._format,
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
			hv,
			mv = mm > -1 ? 'mm' : '',
			sv = ss > -1 ? 'ss' : '';

		if (hasHour1) {
			var time = this.$class._prepareTimeFormat(hh < KK ? 'KK' : 'hh', mv, sv);
			if (aa == -1)
				return time;
			else if ((hh != -1 && aa < hh) || (KK != -1 && aa < KK))
				return 'a ' + time;
			else
				return time + ' a';
		} else
			return this.$class._prepareTimeFormat(HH < kk ? 'kk' : HH > -1 ? 'HH' : '', mv, sv);

	},
	/** Returns the Date format of the specified format
	 * @return String
	 */
	getDateFormat: function () {
		return this._format.replace(/[(s\.S{1,3})ahKHksm]*:?/g, '').trim();
	},
	/** Drops down or closes the calendar to select a date.
	 */
	setOpen: function (open, _focus_) {
		if (this.isRealVisible()) {
			var pop;
			if (pop = this._pop) {
				if (open) pop.open(!_focus_);
				else pop.close(!_focus_);
			}
		}
	},
	isOpen: function () {
		return this._pop && this._pop.isOpen();
	},
	setValue: function (value, fromServer) {
		var tz = this.getTimeZone();
		if (tz && value) value.tz(tz);
		this.$supers('setValue', arguments);
	},
	coerceFromString_: function (val, pattern) {
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
			// We cannot use this._value in this case, which won't trigger onChange
			// event. Using clone date instead.
			var date = this._value ? Dates.newInstance(this._value) : null,
				format = this.getFormat(),
				d = new zk.fmt.Calendar().parseDate(val, pattern || format, !this._lenient, date, this._localizedSymbols, tz, this._strictDate);
			if (!d) return {error: zk.fmt.Text.format(msgzul.DATE_REQUIRED + (this.localizedFormat.replace(_quotePattern, '')))};
			// B70-ZK-2382 escape shouldn't be used in format including hour
			if (!format.match(/[HkKh]/))
				d = new zk.fmt.Calendar().escapeDSTConflict(d, tz);
			return d;
		}
		return null;
	},
	coerceToString_: function (val, pattern) {
		return val ? new zk.fmt.Calendar().formatDate(val, pattern || this.getFormat(), this._localizedSymbols) : '';
	},
	doClick_: function (evt) {
		if (this._disabled) return;
		if (this._readonly && this._buttonVisible && this._pop && !this._pop.isOpen())
			this._pop.open();
		this.$supers('doClick_', arguments);
	},
	doKeyDown_: function (evt) {
		this._doKeyDown(evt);
		if (!evt.stopped)
			this.$supers('doKeyDown_', arguments);
	},
	_doKeyDown: function (evt) {
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
			if (zk.ie < 11) opts.dom = true;
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
	},
	/** Called when the user presses enter when this widget has the focus ({@link #focus}).
	 * <p>call the close function
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 */
	enterPressed_: function (evt) {
		this._pop.close();
		this.updateChange_();
		evt.stop();
	},
	/** Called when the user presses escape key when this widget has the focus ({@link #focus}).
	 * <p>call the close function
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget}
	 */
	escPressed_: function (evt) {
		this._pop.close();
		evt.stop();
	},
	afterKeyDown_: function (evt, simulated) {
		if (!simulated && this._inplace)
			jq(this.$n()).toggleClass(this.getInplaceCSS(), evt.keyCode == 13 ? null : false);

		return this.$supers('afterKeyDown_', arguments);
	},
	bind_: function () {
		this.$supers(Datebox, 'bind_', arguments);
		var btn, inp = this.getInputNode();

		if (btn = this.$n('btn')) {
			this.domListen_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
			if (this._inplace) this.domListen_(btn, 'onMouseDown', '_doBtnMouseDown');
		}

		zWatch.listen({onSize: this, onScroll: this, onShow: this});
		this._pop.setFormat(this.getDateFormat());
	},
	unbind_: function () {
		var btn;
		if (btn = this._pop)
			btn.close(true);

		if (btn = this.$n('btn')) {
			this.domUnlisten_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
			if (this._inplace) this.domUnlisten_(btn, 'onMouseDown', '_doBtnMouseDown');
		}

		zWatch.unlisten({onSize: this, onScroll: this, onShow: this});
		this.$supers(Datebox, 'unbind_', arguments);
	},
	_doBtnClick: function (evt) {
		this._inplaceIgnore = false;
		if (!this._buttonVisible) return;
		if (!this._disabled)
			this.setOpen(!jq(this.$n('pp')).zk.isVisible(), zul.db.DateboxCtrl.isPreservedFocus(this));
		// Bug ZK-2544, B70-ZK-2849
		evt.stop((this._pop && this._pop._open ? {propagation: 1} : null));
	},
	_doBtnMouseDown: function () {
		this._inplaceIgnore = true;
	},
	_doTimeZoneChange: function (evt) {
		var select = this.$n('dtzones'),
			timezone = select.value;
		this.updateChange_();
		this.fire('onTimeZoneChange', {timezone: timezone}, {toServer: true}, 150);
		if (this._pop) this._pop.close();
	},
	onChange: function (evt) {
		var data = evt.data,
			inpValue = this.getInputNode().value;
		if (this._pop)
			this._pop.setValue(data.value);
		// B50-ZK-631: Datebox format error message not shown with implements CustomConstraint
		// pass input value to server for showCustomError
		if (!data.value && inpValue
				&& this.getFormat() && this._cst == '[c')
			data.value = inpValue;
	},
	onScroll: function (wgt) {
		if (this.isOpen()) {
			// ZK-1552: fix the position of popup when scroll
			if (wgt && (pp = this._pop)) {
				// ZK-2211: should close when the input is out of view
				if (this.getInputNode() && zul.inp.InputWidget._isInView(this))
					this.$class._reposition(this, true);
				else
					pp.close();
			}
		}
	},
	onShow: function () {
		if (this.__ebox) {
			this.setFloating_(true);
			this.__ebox.show();
		}
	},
	/** Returns the label of the time zone
	 * @return String
	 */
	getTimeZoneLabel: function () {
		return '';
	},

	redrawpp_: function (out) {
		out.push('<div id="', this.uuid, '-pp" class="', this.$s('popup'),
			'" style="display:none" role="dialog" aria-labelledby="', this._pop.uuid, '-title">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		this._redrawTimezone(out);
		out.push('</div>');
	},
	_redrawTimezone: function (out) {
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
	},
	updateChange_: function () {
		if (this.isOpen()) return;
		this.$supers('updateChange_', arguments);
	}
}, {
	_initPopup: function () {
		this._pop = new zul.db.CalendarPop();
		this._tm = new zul.db.CalendarTime();
		this.appendChild(this._pop);
		this.appendChild(this._tm);
	},
	_reposition: function (db, silent) {
		if (!db.$n()) return;
		var pp = db.$n('pp'),
			n = db.$n(),
			inp = db.getInputNode();

		if (pp) {
			zk(pp).position(n, db.position, {dodgeRef: n});
			db._pop.syncShadow();
			if (!silent)
				zk(inp).focus();
		}
	},
	_prepareTimeFormat: function (h, m, s) {
		var o = [];
		if (h) o.push(h);
		if (m) o.push(m);
		if (s) o.push(s);
		return o.join(':');
	}
});

var CalendarPop =
zul.db.CalendarPop = zk.$extends(zul.db.Calendar, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onChange: this}, -1000);
	},
	setFormat: function (fmt) {
		this._fmt = fmt;
	},
	setLocalizedSymbols: function (symbols) {
		this._localizedSymbols = symbols;
	},
	// ZK-2047: should sync shadow when shiftView
	rerender: function () {
		this.$supers('rerender', arguments);
		if (this.desktop) this.syncShadow();
	},

	close: function (silent) {
		var db = this.parent,
			pp = db.$n('pp');

		if (!pp || !zk(pp).isVisible()) return;

		if (db._inplace) {
			db._inplaceIgnore = false;
			db._inplaceTimerId = setTimeout(function () {
				if (db.desktop) jq(db.$n()).addClass(db.getInplaceCSS());
			}, db._inplaceTimeout);
		}
		// firefox and safari only
		try {
			if ((zk.ff || zk.safari) && zk.currentFocus) {
				var n = zk.currentFocus.getInputNode ?
						zk.currentFocus.getInputNode() : zk.currentFocus.$n();
				if (jq.nodeName(n, 'input') && jq.isAncestor(pp, n)) // Bug ZK-2922, check ancestor first.
					jq(n).blur(); // trigger a missing blur event.
			}
		} catch (e) {
			zk.debugLog(e.message || e);
		}

		if (this._shadow) {
			// B65-ZK-1904: Make shadow behavior the same as ComboWidget
			this._shadow.destroy();
			this._shadow = null;
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
		jq(db.$n()).removeClass(openClass);
		jq(pp).removeClass(openClass);
	},
	isOpen: function () {
		return zk(this.parent.$n('pp')).isVisible();
	},
	open: function (silent) {
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
		pp.style.zIndex = topZIndex > 0 ? topZIndex : 1;
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
				pp.style.width = wd;
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
		jq(db.$n()).addClass(openClass);
		jq(pp).addClass(openClass);

		db.$class._reposition(db, silent); //ZK-3217: only need to calculate position once during open
	},
	syncShadow: function () {
		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this.parent.$n('pp'), {
				left: -4, right: 4, top: 2, bottom: 3});
		this._shadow.sync();
	},
	onChange: function (evt) {
		var date = this.getTime(),
			db = this.parent,
			fmt = db.getTimeFormat(),
			oldDate = db.getValue(),
			readonly = db.isReadonly(),
			tz = db.getTimeZone(),
			cal = new zk.fmt.Calendar();

		if (fmt) {
			var tm = db._tm,
				time = tm.getValue();
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
			db.getInputNode().value = db.coerceToString_(date);

		if (this._view == db._selectLevel && evt.data.shallClose !== false) {
			this.close();

			// Bug 3122159 and 3301374
			evt.data.value = date;
			if (!this.$class._equalDate(date, oldDate)) {
				db._value = date;
				db.updateChange_();
			}
		}
		evt.stop();
	},
	onFloatUp: function (ctl) {
		if (this.isOpen()) {
			var db = this.parent;
			if (!zUtl.isAncestor(db, ctl.origin)) {
				this.close(true);
			}
		}
	},
	bind_: function () {
		this.$supers(CalendarPop, 'bind_', arguments);
		this._bindTimezoneEvt();

		zWatch.listen({onFloatUp: this});
	},
	unbind_: function () {
		zWatch.unlisten({onFloatUp: this});
		this._unbindfTimezoneEvt();
		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = null;
		}
		this.$supers(CalendarPop, 'unbind_', arguments);
	},
	_bindTimezoneEvt: function () {
		var db = this.parent;
		var select = db.$n('dtzones');
		if (select) {
			select.disabled = db.isTimeZonesReadonly() ? 'disable' : '';
			db.domListen_(select, 'onChange', '_doTimeZoneChange');
			db._setTimeZonesIndex();
		}
	},
	_unbindfTimezoneEvt: function () {
		var db = this.parent,
			select = db.$n('dtzones');
		if (select)
			db.domUnlisten_(select, 'onChange', '_doTimeZoneChange');
	},
	_setView: function (val, force) {
		if (this.parent.getTimeFormat())
			this.parent._tm.setVisible(val == 'day');
		this.$supers('_setView', arguments);

		// ZK-2047: when sync shadow, the calendar popup should be above the pdf
		if (zk.ie > 9) {
			this.syncShadow();
		}
		// fix shadow ghost for ie9
		if (zk.ie9_ && force) {
			zk(this.parent.$n('pp')).redoCSS(500); // wait for animation
		}
	},
	// ZK-2308
	doKeyDown_: function (evt) {
		this.$supers('doKeyDown_', arguments);
		if (evt.keyCode == 27) {
			this.parent.escPressed_(evt);
		}
	},
	animationSpeed_: function () {
		return zk(this.parent).getAnimationSpeed('_default');
	},
	_chooseDate: function (target, val) {
		var db = this.parent,
			selectLevel = db._selectLevel;
		if (target && !jq(target).hasClass(this.$s('disabled'))) {
			var cell = target,
				dateobj = this.getTime();
			switch (this._view) {
				case 'day' :
					var oldTime = this.getTime();
					this._setTime(null, cell._monofs != null && cell._monofs != 0 ?
						dateobj.getMonth() + cell._monofs : null, val, true /*fire onChange */);
					var newTime = this.getTime();
					if (oldTime.getYear() == newTime.getYear()
						&& oldTime.getMonth() == newTime.getMonth()) {
						this._markCal({sameMonth: true}); // optimize
					} else {
						this.rerender(-1);
						this.focus();
					}
					break;
				case 'month' :
					if (selectLevel == 'month') {
						this._setTime(null, val, 1, true);
						break;
					}
					this._setTime(null, val);
					this._setView('day');
					break;
				case 'year' :
					if (selectLevel == 'year') {
						this._setTime(val, 0, 1, true);
						break;
					}
					this._setTime(val);
					this._setView('month');
					break;
				case 'decade' :
					//Decade mode Set Year Also
					this._setTime(val);
					this._setView('year');
					break;
			}
		}
	}
}, {
	_equalDate: function (d1, d2) {
		return (d1 == d2) || (d1 && d2 && d1.getTime() == d2.getTime());
	}
});
zul.db.CalendarTime = zk.$extends(zul.db.Timebox, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onChanging: this}, -1000);
	},
	onChanging: function (evt) {
		var db = this.parent,
			oldDate = db.getValue() || db._pop.getValue(),
			cal = new zk.fmt.Calendar(),
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
			db.getInputNode().value = evt.data.value
				= db.coerceToString_(date);
			db.fire(evt.name, evt.data); //onChanging
		}

		if (this._view == db._selectLevel && evt.data.shallClose !== false) {
			this.close();
		}
		evt.stop();
	}
});


/** @class zul.db.DateboxCtrl
 * @import zk.Widget
 * The extra control for the Datebox.
 * It is designed to be overriden
 * @since 6.5.0
 */
zul.db.DateboxCtrl = {
	/**
	 * Returns whether to preserve the focus state.
	 * @param zk.Widget wgt a widget
	 * @return boolean
	 */
	isPreservedFocus: function (wgt) {
		return true;
	}
};
})();
