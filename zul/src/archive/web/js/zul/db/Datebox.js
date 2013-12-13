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
	function _initPopup () {
		this._pop = new zul.db.CalendarPop();
		this._tm = new zul.db.CalendarTime();
		this.appendChild(this._pop);
		this.appendChild(this._tm);
	}
	function _reposition(db, silent) {
		if (!db.$n()) return;
		var pp = db.$n('pp'),
			inp = db.getInputNode();

		if(pp) {
			zk(pp).position(inp, 'after_start');
			db._pop.syncShadow();
			if (!silent)
				zk(inp).focus();
		}
	}
	function _blurInplace(db) {
		zul.inp.RoundUtl.doBlur_(db);
	}
	function _equalDate(d1, d2) {
		return (d1 == d2) || (d1 && d2 && d1.getTime() == d2.getTime());
	}
	function _prepareTimeFormat(h, m, s) {
		var o =[];
		if (h) o.push(h);
		if (m) o.push(m);
		if (s) o.push(s);
		return o.join(':');
	}
	
var globallocalizedSymbols = {},
	_quotePattern = /\'/g, // move pattern string here to avoid jsdoc failure
	Datebox =
/**
 * An edit box for holding a date.
 * <p>Default {@link #getZclass}: z-datebox.
 */
zul.db.Datebox = zk.$extends(zul.inp.FormatWidget, {
	_buttonVisible: true,
	_lenient: true,
	$init: function() {
		this.$supers('$init', arguments);
		this.afterInit(_initPopup);
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
			this._setTimeZonesIndex();
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
			eval('Datebox._unformater = ' + unf);
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
				if(val) {
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
		}
	},
	getIconClass_: function () {
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
		zul.inp.RoundUtl.onSize(this);
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
			HH= fmt.indexOf('HH'),
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
			var time = _prepareTimeFormat(hh < KK ? 'KK' : 'hh', mv, sv);
			if (aa == -1) 
				return time;
			else if ((hh != -1 && aa < hh) || (KK != -1 && aa < KK)) 
				return 'a ' + time;
			else
				return time + ' a';
		} else
			return _prepareTimeFormat(HH < kk ? 'kk' : HH > -1 ? 'HH' : '', mv, sv);
		
	},
	/** Returns the Date format of the specified format
	 * @return String
	 */
	getDateFormat: function () {
		return this._format.replace(/[ahKHksm]/g, '');
	},
	/** Drops down or closes the calendar to select a date.
	 */
	setOpen: function(open, _focus_) {
		if (this.isRealVisible()) {
			var pop;
			if (pop = this._pop)
				if (open) pop.open(!_focus_);
				else pop.close(!_focus_);
		}
	},
	isOpen: function () {
		return this._pop && this._pop.isOpen();
	},
	coerceFromString_: function (val) {
		var unf = Datebox._unformater;
		if (unf && jq.isFunction(unf)) {
			var cusv = unf(val);
			if (cusv) {
				this._shortcut = val;
				return cusv;
			}
		}
		if (val) {
			var d = new zk.fmt.Calendar().parseDate(val, this.getFormat(), !this._lenient, this._value, this._localizedSymbols);
			if (!d) return {error: zk.fmt.Text.format(msgzul.DATE_REQUIRED + (this.localizedFormat.replace(_quotePattern, '')))};
			return d;
		}
		return null;
	},
	coerceToString_: function (val) {
		return val ? new zk.fmt.Calendar().formatDate(val, this.getFormat(), this._localizedSymbols) : '';
	},
	doFocus_: function (evt) {
		this.$supers('doFocus_', arguments);

		zul.inp.RoundUtl.doFocus_(this);
	},
	doBlur_: function (evt) {
		if (this._inplace && this._pop && this._pop.isOpen())
			return; // prevent blur if popup is opened
		this.$supers('doBlur_', arguments);
		_blurInplace(this);
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
		if (keyCode == 9 || (zk.webkit && keyCode == 0)) { //TAB or SHIFT-TAB (safari)
			if (bOpen) this._pop.close();
			return;
		}

		if (evt.altKey && (keyCode == 38 || keyCode == 40)) {//UP/DN
			if (bOpen) this._pop.close();
			else this._pop.open();

			//FF: if we eat UP/DN, Alt+UP degenerate to Alt (select menubar)
			var opts = {propagation:true};
			if (zk.ie < 11) opts.dom = true;
			evt.stop(opts);
			return;
		}

		// select current time
		if (this._pop.isOpen()) {
			this._pop.doKeyDown_(evt);
		}
		
		//Request 1537962: better responsive
		if (bOpen && (keyCode == 13 || keyCode == 27)) { //ENTER or ESC
			if (keyCode == 13) this.enterPressed_(evt);
			else this.escPressed_(evt);
			return;
		}

		if (keyCode == 18 || keyCode == 27 || keyCode == 13
		|| (keyCode >= 112 && keyCode <= 123)) //ALT, ESC, Enter, Fn
			return; //ignore it (doc will handle it)
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
			jq(this.$n()).toggleClass(this.getInplaceCSS(),  evt.keyCode == 13 ? null : false);

		return this.$supers('afterKeyDown_', arguments);
	},
	bind_: function (){
		this.$supers(Datebox, 'bind_', arguments);
		var btn, inp = this.getInputNode();

		if (btn = this.$n('btn')) {
			this.domListen_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
		}

		zWatch.listen({onSize: this, onScroll: this});
		this._pop.setFormat(this.getDateFormat());
	},
	unbind_: function () {
		var btn;
		if (btn = this._pop)
			btn.close(true);

		if (btn = this.$n('btn')) {
			this.domUnlisten_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
		}

		zWatch.unlisten({onSize: this, onScroll: this});
		this.$supers(Datebox, 'unbind_', arguments);
	},
	_doBtnClick: function (evt) {
		if (!this._buttonVisible) return;
		if (!this._disabled)
			this.setOpen(!jq(this.$n('pp')).zk.isVisible(), zul.db.DateboxCtrl.isPreservedFocus(this));
		evt.stop();
	},
	_doTimeZoneChange: function (evt) {
		var select = this.$n('dtzones'),
			timezone = select.value;
		this.updateChange_();
		this.fire('onTimeZoneChange', {timezone: timezone}, {toServer:true}, 150);
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
		// ZK-1552: fix the position of popup when scroll
		if (wgt && (pp = this._pop))
			if (zk(this).isScrollIntoView())
				_reposition(this, true);
			else
				pp.close();
	},
	/** Returns the label of the time zone
	 * @return String
	 */
	getTimeZoneLabel: function () {
		return '';
	},

	redrawpp_: function (out) {
		out.push('<div id="', this.uuid, '-pp" class="', this.$s('popup'),
			'" style="display:none" tabindex="-1">');
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
		jq(pp).removeClass(db.$s('open'));
	},
	isOpen: function () {
		return zk(this.parent.$n('pp')).isVisible();
	},
	open: function(silent) {
		var db = this.parent,
			dbn = db.$n(), pp = db.$n('pp');
		if (!dbn || !pp)
			return;

		db.setFloating_(true, {node:pp});
		zWatch.fire('onFloatUp', db); //notify all
		var topZIndex = this.setTopmost();
		this._setView('day');
		var zcls = db.getZclass();

		pp.className = dbn.className + ' ' + pp.className;
		jq(pp).removeClass(zcls);

		pp.style.width = 'auto'; //reset
		pp.style.display = 'block';
		pp.style.zIndex = topZIndex > 0 ? topZIndex : 1;

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
		var inp = db.getInputNode();
		zk(pp).position(inp, 'after_start');
		delete db._shortcut;
		
		var self = this;
		setTimeout(function() {
			_reposition(db, silent);
			zWatch.fireDown('onVParent', self.parent.$n('pp'), { shadow: self._shadow });
		}, 150);
		//IE, Opera, and Safari issue: we have to re-position again because some dimensions
		//in Chinese language might not be correct here.
		var fmt = db.getTimeFormat(),
			unf = Datebox._unformater,
			value = unf ? unf(inp.value) : null;
		//we should use UTC date instead of Locale date to our value.
		if (!value)
			value = new zk.fmt.Calendar(zk.fmt.Date.parseDate(inp.value, db._format, false, db._value, this._localizedSymbols), this._localizedSymbols).toUTCDate()
				|| (inp.value ? db._value: zUtl.today(fmt));
		
		if (value)
			this.setValue(value);
		if (fmt) {
			var tm = db._tm;
			tm.setVisible(true);
			tm.setFormat(fmt);
			tm.setValue(value || new Date());
			tm.onSize();
		} else {
			db._tm.setVisible(false);
		}
		//add extra CSS class for easy customize
		jq(pp).addClass(db.$s('open'));
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
			readonly = db.isReadonly();

		if (oldDate)
			date = new Date(date.getFullYear(), date.getMonth(),
				date.getDate(), oldDate.getHours(),
				oldDate.getMinutes(), oldDate.getSeconds(), oldDate.getMilliseconds());
			//Note: we cannot call setFullYear(), setMonth(), then setDate(),
			//since Date object will adjust month if date larger than max one

		if (fmt) {
			var tm = db._tm,
				time = tm.getValue();
			date.setHours(time.getHours(), time.getMinutes(), time.getSeconds(), time.getMilliseconds());
		}
		
		//Bug ZK-1712: no need to set datebox input value when shift view
		if (!evt.data.shiftView)
			db.getInputNode().value = db.coerceToString_(date);
		
		if (this._view == 'day' && evt.data.shallClose !== false) {
			this.close();
			db._inplaceout = true;
			
			// Bug 3122159 and 3301374
			evt.data.value = date;
			if(!_equalDate(date, oldDate))
				db.updateChange_();
		}
		evt.stop();
	},
	onFloatUp: function (ctl) {
		var db = this.parent;
		if (!zUtl.isAncestor(db, ctl.origin)) {
			this.close(true);
			db._inplaceout = true;
			_blurInplace(db);
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

		// fix shadow ghost for ie9
		if (zk.ie9_ && force) {
			zk(this.parent.$n('pp')).redoCSS(500); // wait for animation
		}
	}
});
zul.db.CalendarTime = zk.$extends(zul.db.Timebox, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onChanging: this}, -1000);
	},
	onChanging: function (evt) {
		var db = this.parent,
			date = this.coerceFromString_(evt.data.value), //onChanging's data is string
			oldDate = db.getValue();
		if (oldDate) {
			oldDate = new Date(oldDate); //make a copy
			oldDate.setHours(date.getHours());
			oldDate.setMinutes(date.getMinutes());
			oldDate.setSeconds(date.getSeconds());
			oldDate.setMilliseconds(date.getMilliseconds());
			date = oldDate;
		}
		db.getInputNode().value = evt.data.value
			= db.coerceToString_(date);

		db.fire(evt.name, evt.data); //onChanging
		if (this._view == 'day' && evt.data.shallClose !== false) {
			this.close();
			db._inplaceout = true;
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
