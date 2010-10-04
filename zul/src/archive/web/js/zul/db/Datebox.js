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
	function _reposition(wgt, silent) {
		var db = wgt.$n();
		if (!db) return;
		var pp = wgt.$n("pp"),
			inp = wgt.getInputNode();

		if(pp) {
			zk(pp).position(inp, "after_start");
			wgt._pop.syncShadow();
			if (!silent)
				zk(inp).focus();
		}
	}

var Datebox =
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
			var n = this.$n('btn'),
				zcls = this.getZclass();
			if (n) {
				if (!this.inRoundedMold()) {
					jq(n)[v ? 'show': 'hide']();
					jq(this.getInputNode())[v ? 'removeClass': 'addClass'](zcls + '-right-edge');
				} else {
					var fnm = v ? 'removeClass': 'addClass';
					jq(n)[fnm](zcls + '-btn-right-edge');
					
					if (zk.ie6_) {						
						jq(n)[fnm](zcls + 
							(this._readonly ? '-btn-right-edge-readonly':'-btn-right-edge'));
						
						if (jq(this.getInputNode()).hasClass(zcls + "-text-invalid"))
							jq(n)[fnm](zcls + "-btn-right-edge-invalid");
					}
				}
				this.onSize();
			}
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
				inp.value = this.coerceToString_(this._value);
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
				this._cst = new zul.inp.SimpleDateConstraint(cst);
			else
				this._cst = cst;
			if (this._cst) delete this._lastRawValVld; //revalidate required
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
			if (select) select.disabled = readonly ? "disabled" : "";
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
			this._dtzones = dtzones.split(",");
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
		lenient: null
	},
	setValue: function (val) {
		var args;
		if (val) {
			args = [];
			for (var j = arguments.length; --j > 0;)
				args.unshift(arguments[j]);

			args.unshift((typeof val == 'string') ? this.coerceFromString_(val) : val);
		} else
			args = arguments;
		this.$supers('setValue', args);
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
	onSize: _zkf = function () {
		var width = this.getWidth();
		if (!width || width.indexOf('%') != -1)
			this.getInputNode().style.width = '';
		this.syncWidth();
	},
	onShow: _zkf,
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-datebox" + (this.inRoundedMold() ? "-rounded": "");
	},
	/** Returns the String of the Date that is assigned to this component.
	 *  <p>returns empty String if value is null
	 * @return String
	 */
	getRawText: function () {
		return this.coerceToString_(this._value);
	},
	/** Returns the Time format of the specified format
	 * @return String
	 */
	getTimeFormat: function () {
		var fmt = this._format,
			aa = fmt.indexOf('a'),
			hh = fmt.indexOf('h'),
			KK = fmt.indexOf('K'),
			HH= fmt.indexOf('HH'),
			kk = fmt.indexOf('k'),
			mm = fmt.indexOf('m'),
			ss = fmt.indexOf('s'),
			hasAM = aa > -1,
			hasHour1 = hasAM ? hh > -1 || KK > -1 : false;

		if (hasHour1) {
			if ((hh != -1 && aa < hh) || (kk != -1 && aa < kk)) {
				var f = hh < KK ? 'a KK' : 'a hh';
				return f + (mm > -1 ? ':mm': '') + (ss > -1 ? ':ss': '');
			} else {
				var f = hh < KK ? 'KK' : 'hh';
				f = f + (mm > -1 ? ':mm': '') + (ss > -1 ? ':ss': '');
				return f + ' a';
			}
		} else {
			var f = HH < kk ? 'kk' : HH > -1 ? 'HH' : '';
			return f + (mm > -1 ? ':mm': '') + (ss > -1 ? ':ss': '');
		}
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
		var pop;
		if (pop = this._pop)
			if (open) pop.open(!_focus_);
			else pop.close(!_focus_);
	},
	isOpen: function () {
		return this._pop && this._pop.isOpen();
	},
	coerceFromString_: function (val) {
		if (val) {
			var d = new zk.fmt.Calendar().parseDate(val, this.getFormat(), !this._lenient);
			if (!d) return {error: zk.fmt.Text.format(msgzul.DATE_REQUIRED + this._format)};
			return d;
		} else
			return val;
	},
	coerceToString_: function (val) {
		return val ? new zk.fmt.Calendar().formatDate(val, this.getFormat()) : '';
	},
	/** Synchronizes the input element's width of this component
	 */
	syncWidth: function () {
		var node = this.$n();
		if (!zk(node).isRealVisible() || (!this._inplace && !node.style.width))
			return;
		
		var inp = this.getInputNode(),
    		$n = jq(node),
    		$inp = jq(inp),
    		inc = this.getInplaceCSS(),
    		shallClean = !node.style.width && this._inplace;
		if (this._buttonVisible && shallClean) {
			$n.removeClass(inc);
			$inp.removeClass(inc);
			
			if (zk.opera)
				node.style.width = jq.px0(zk(node).revisedWidth(node.clientWidth) + zk(node).borderWidth());
			else
				node.style.width = jq.px0(zk(node).revisedWidth(node.offsetWidth));
			$n.addClass(inc);
			$inp.addClass(inc);
		}
		var extraWidth = this.inRoundedMold() && shallClean;
		
		if (extraWidth) {
    		$n.removeClass(inc);
    		$inp.removeClass(inc);
		}
		var width = zk.opera ? zk(node).revisedWidth(node.clientWidth) + zk(node).borderWidth()
							 : zk(node).revisedWidth(node.offsetWidth),
			btn = this.$n('btn');
		
		if (extraWidth) {
    		$n.addClass(inc);
    		$inp.addClass(inc);
		}
		inp.style.width = jq.px0(zk(inp).revisedWidth(width - (btn ? btn.offsetWidth : 0)));
	},
	doFocus_: function (evt) {
		var n = this.$n();
		if (this._inplace)
			n.style.width = jq.px0(zk(n).revisedWidth(n.offsetWidth));
			
		this.$supers('doFocus_', arguments);

		if (this._inplace) {
			if (jq(n).hasClass(this.getInplaceCSS())) {
				jq(n).removeClass(this.getInplaceCSS());
				this.onSize();
			}
		}
	},
	doClick_: function (evt) {
		if (this._disabled) return;
		if (this._readonly && this._buttonVisible && this._pop)
			this._pop.open();
		this.$supers('doClick_', arguments);
	},
	doBlur_: function (evt) {
		var n = this.$n();
		if (this._inplace && this._inplaceout) {
			n.style.width = jq.px0(zk(n).revisedWidth(n.offsetWidth));
		}
		this.$supers('doBlur_', arguments);
		if (this._inplace && this._inplaceout) {
			jq(n).addClass(this.getInplaceCSS());
			this.onSize();
			n.style.width = this.getWidth() || '';
		}
	},
	doKeyDown_: function (evt) {
		this._doKeyDown(evt);
		if (!evt.stopped)
			this.$supers('doKeyDown_', arguments);
	},
	_doKeyDown: function (evt) {
		var keyCode = evt.keyCode,
			bOpen = this._pop.isOpen();
		if (keyCode == 9 || (zk.safari && keyCode == 0)) { //TAB or SHIFT-TAB (safari)
			if (bOpen) this._pop.close();
			return;
		}

		if (evt.altKey && (keyCode == 38 || keyCode == 40)) {//UP/DN
			if (bOpen) this._pop.close();
			else this._pop.open();

			//FF: if we eat UP/DN, Alt+UP degenerate to Alt (select menubar)
			var opts = {propagation:true};
			if (zk.ie) opts.dom = true;
			evt.stop(opts);
			return;
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
		
		if (this._pop.isOpen()) {
			var ofs = keyCode == 37 ? -1 : keyCode == 39 ? 1 : keyCode == 38 ? -7 : keyCode == 40 ? 7 : 0;
			if (ofs)
				this._pop._shift(ofs);
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
	afterKeyDown_: function (evt) {
		if (this._inplace)
			jq(this.$n()).toggleClass(this.getInplaceCSS(),  evt.keyCode == 13 ? null : false);
			
		this.$supers('afterKeyDown_', arguments);
	},
	bind_: function (){
		this.$supers(Datebox, 'bind_', arguments);
		var btn = this.$n('btn'),
			inp = this.getInputNode();
			
		if (this._inplace)
			jq(inp).addClass(this.getInplaceCSS());
			
		if (btn) {
			this._auxb = new zul.Auxbutton(this, btn, inp);
			this.domListen_(btn, 'onClick', '_doBtnClick');
		}
		
		zul.db.ThemeHandler.addRightEdgeClass(this);
			
		this.syncWidth();
		
		zWatch.listen({onSize: this, onShow: this});
		this._pop.setFormat(this.getDateFormat());
	},
	unbind_: function () {
		var btn;
		if (btn = this._pop)
			btn.close(true);

		if (btn = this.$n('btn')) {
			this._auxb.cleanup();
			this._auxb = null;
			this.domUnlisten_(btn, 'onClick', '_doBtnClick');
		}
			
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers(Datebox, 'unbind_', arguments);
	},
	_doBtnClick: function (evt) {
		if (this.inRoundedMold() && !this._buttonVisible) return;
		if (!this._disabled)
			this.setOpen(!jq(this.$n("pp")).zk.isVisible(), true);
		evt.stop();
	},
	_doTimeZoneChange: function (evt) {
		var select = this.$n('dtzones'),
			timezone = select.value;
		if (!this.getValue()) {
			this.setValue(this._tm.getValue());
		}
		this.updateChange_();
		this.fire("onTimeZoneChange", {timezone: timezone}, {toServer:true}, 150);
		if (this._pop) this._pop.close();
	},
	onChange: function (evt) {
		if (this._pop)
			this._pop.setValue(evt.data.value);
	},
	/** Returns the label of the time zone
	 * @return String
	 */
	getTimeZoneLabel: function () {
		return "";
	},

	redrawpp_: function (out) {
		out.push('<div id="', this.uuid, '-pp" class="', this.getZclass(),
			'-pp" style="display:none" tabindex="-1">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		this._redrawTimezone(out);
		out.push('</div>');
	},
	_redrawTimezone: function (out) {
		var timezones = this._dtzones;
		if (timezones) {
			var cls = this.getZclass();
			out.push('<div class="', cls, '-timezone">');
			out.push(this.getTimeZoneLabel());
			out.push('<select id="', this.uuid, '-dtzones" class="', cls, '-timezone-body">');
			for (var i = 0, len = timezones.length; i < len; i++)
				out.push('<option value="', timezones[i], '" class="', cls, '-timezone-item">', timezones[i], '</option>');
			out.push('</select><div>');
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
		if (fmt != this._fmt) {
			var old = this._fmt;
			this._fmt = fmt;
			if (this.getValue())
				this._value = new zk.fmt.Calendar().formatDate(new zk.fmt.Calendar().parseDate(this.getValue(), old), fmt);
		}
	},
	rerender: function () {
		this.$supers('rerender', arguments);
		if (this.desktop) this.syncShadow();
	},
	close: function (silent) {
		var db = this.parent,
			pp = db.$n("pp");

		if (!pp || !zk(pp).isVisible()) return;
		if (this._shadow) this._shadow.hide();

		var zcls = db.getZclass();
		pp.style.display = "none";
		pp.className = zcls + "-pp";

		jq(pp).zk.undoVParent();

		var btn = this.$n("btn");
		if (btn)
			jq(btn).removeClass(zcls + "-btn-over");

		if (!silent)
			jq(db.getInputNode()).focus();
	},
	isOpen: function () {
		return zk(this.parent.$n("pp")).isVisible();
	},
	open: function(silent) {
		var wgt = this.parent,
			db = wgt.$n(), pp = wgt.$n("pp");
		if (!db || !pp)
			return;
		this._setView("day");
		var zcls = wgt.getZclass();

		pp.className = db.className + " " + pp.className;
		jq(pp).removeClass(zcls);

		pp.style.width = pp.style.height = "auto";
		pp.style.position = "absolute"; //just in case
		//pp.style.overflow = "auto"; //don't set since it might turn on scrollbar unexpectedly (IE: http://www.zkoss.org/zkdemo/userguide/#f9)
		pp.style.display = "block";
		pp.style.zIndex = "88000";

		//FF: Bug 1486840
		//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
		jq(pp).zk.makeVParent();

		if (pp.offsetHeight > 200) {
			//pp.style.height = "200px"; commented by the bug #2796461
			pp.style.width = "auto"; //recalc
		} else if (pp.offsetHeight < 10) {
			pp.style.height = "10px"; //minimal
		}
		if (pp.offsetWidth < db.offsetWidth) {
			pp.style.width = db.offsetWidth + "px";
		} else {
			var wd = jq.innerWidth() - 20;
			if (wd < db.offsetWidth)
				wd = db.offsetWidth;
			if (pp.offsetWidth > wd)
				pp.style.width = wd;
		}
		zk(pp).position(wgt.getInputNode(), "after_start");
		setTimeout(function() {
			_reposition(wgt, silent);
		}, 150);
		//IE, Opera, and Safari issue: we have to re-position again because some dimensions
		//in Chinese language might not be correct here.
		var fmt = wgt.getTimeFormat(),
			value = wgt.getValue();

		if (value) {
			var calVal = new zk.fmt.Calendar().formatDate(value, this.getFormat());
			if (calVal)
				this.setValue(calVal);
		}

		if (fmt) {
			var tm = wgt._tm;
			tm.setVisible(true);
			tm.setFormat(fmt);
			tm.setValue(value || new Date());
			tm.onShow();
		} else {
			wgt._tm.setVisible(false);
		}
	},
	syncShadow: function () {
		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this.parent.$n('pp'), {
				left: -4, right: 4, top: 2, bottom: 3});
		this._shadow.sync();
	},
	onChange: function (evt) {
		var date = this.getTime(),
			oldDate = this.parent.getValue(),
			readonly = this.parent.isReadonly();
		if (oldDate) {
			//Note: we cannot call setFullYear(), setMonth(), then setDate(),
			//since Date object will adjust month if date larger than max one
			this.parent._value = new Date(date.getFullYear(), date.getMonth(),
				date.getDate(), oldDate.getHours(),
				oldDate.getMinutes(), oldDate.getSeconds());
		} else
			this.parent._value = date;
		this.parent.getInputNode().value = evt.data.value = this.parent.getRawText();
		this.parent.fire(evt.name, evt.data);
		if (this._view == 'day' && evt.data.shallClose !== false) {
			this.close(readonly);
			this.parent._inplaceout = true;
		}
		if (!readonly)
			this.parent.focus();
		evt.stop();
	},
	onFloatUp: function (ctl) {
		if (!zUtl.isAncestor(this.parent, ctl.origin))
			this.close(true);
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
		var wgt = this.parent;
		var select = wgt.$n('dtzones');
		if (select) {
			select.disabled = wgt.isTimeZonesReadonly() ? "disable" : "";
			wgt.domListen_(select, 'onChange', '_doTimeZoneChange');
			wgt._setTimeZonesIndex();
		}
	},
	_unbindfTimezoneEvt: function () {
		var wgt = this.parent,
			select = wgt.$n('dtzones');
		if (select)
			wgt.domUnlisten_(select, 'onChange', '_doTimeZoneChange');
	},
	_setView: function (val) {
		if (this.parent.getTimeFormat())
			this.parent._tm.setVisible(val == 'day');
		this.$supers('_setView', arguments);
	}
});
zul.db.CalendarTime = zk.$extends(zul.inp.Timebox, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onChanging: this}, -1000);
	},
	onChanging: function (evt) {
		var date = this.coerceFromString_(evt.data.value),
			oldDate = this.parent.getValue();
		if (oldDate) {
			oldDate.setHours(date.getHours());
			oldDate.setMinutes(date.getMinutes());
			oldDate.setSeconds(date.getSeconds());
		} else
			this.parent._value = date;
		this.parent.getInputNode().value = evt.data.value = this.parent.getRawText();
		this.parent.fire(evt.name, evt.data);
		if (this._view == 'day' && evt.data.shallClose !== false) {
			this.close();
			this.parent._inplaceout = true;
		}
		evt.stop();
	}
});

zul.db.ThemeHandler = {
	 addRightEdgeClass: function (wgt) {
	  	if (wgt._readonly && !wgt.inRoundedMold() && !wgt._buttonVisible)
			jq(wgt.getInputNode()).addClass(wgt.getZclass() + '-right-edge');
	 },
	 addDayOfWeekClass: function (isWeekend, out) {
	 	if (isWeekend) out.push(' class="z-weekend"');
	 },
	 addDayRowClass: function (isWeekend, zcls, out) {
	 	out.push ('<td></td>');
	 }
};
})();
