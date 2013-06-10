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

(function () {
	// Bug 2936994, fixed unnecessary setting scrollTop
	var _doFocus = zk.gecko ? function (n, timeout) {
			if (timeout)
				setTimeout(function () {
					zk(n).focus();
				});
			else
				zk(n).focus();
		} : function (n) {
			zk(n).focus();
		};

	function _newDate(year, month, day, bFix) {
		var v = new Date(year, month, day);
		return bFix && v.getMonth() != month && v.getDate() != day ? //Bug ZK-1213: also need to check date
			new Date(year, month + 1, 0)/*last day of month*/: v;
	}

var Renderer = 
/** @class zul.db.Renderer
 * The renderer used to render a calendar.
 * It is designed to be overridden
 */
zul.db.Renderer = {
	/** Returns the HTML fragment representing a day cell.
	 * By overriding this method, you could customize the look of a day cell.
	 * <p>Default: day
	 * @param zul.db.Calendar cal the calendar
	 * @param int y the year
	 * @param int m the month (between 0 to 11)
	 * @param int day the day (between 1 to 31)
	 * @param int monthofs the month offset. If the day is in the same month
	 * @return String the HTML fragment
	 * @since 5.0.3
	 */
	cellHTML: function (cal, y, m, day, monthofs) {
		return day;
	},
	/** Called before {@link zul.db.Calendar#redraw} is invoked.
	 * <p>Default: does nothing
	 * @param zul.db.Calendar cal the calendar
	 * @since 5.0.3
	 */
	beforeRedraw: function (cal) {
	},
	/** Tests if the specified date is disabled.
	 * <p>Default: it depends on the constraint, if any
	 * @param zul.db.Calendar cal the calendar
	 * @param int y the year
	 * @param int m the month (between 0 to 11)
	 * @param int v the day (between 1 to 31)
	 * @param Date today today
	 * @since 5.0.3
	 * @return boolean
	 */
	disabled: function (cal, y, m, v, today) {
		var d = new Date(y, m, v, 0, 0, 0, 0),
			constraint;
		
		if ((constraint = cal._constraint)&& typeof constraint == 'string') {
			
			// Bug ID: 3106676
			if ((constraint.indexOf('no past') > -1 && (d - today) / 86400000 < 0) ||
			    (constraint.indexOf('no future') > -1 && (today - d) / 86400000 < 0) ||
			    (constraint.indexOf('no today') > -1 && today - d == 0))
					return true;
		}
		
		var result = false;
		if (cal._beg && (result = (d - cal._beg) / 86400000 < 0))
			return result;
		if (cal._end && (result = (cal._end - d) / 86400000 < 0))
			return result;
		return result;
	},
	/**
	 * Generates the label of the week of year.
	 * <p>Default: the string of the value
	 * @param zul.db.Calendar wgt the calendar widget
	 * @param int the number of the week of the value
	 * @param Map localizedSymbols the symbols for localization 
	 * @return String the label of the week of year
	 * @since 6.5.0
	 */
	labelOfWeekOfYear: function (wgt, val) {
		return val;
	},
	/**
	 * Generates the title of the week of year.
	 * <p>Default: 'Wk'
	 * @param zul.db.Calendar wgt the calendar widget 
	 * @return String the title of the week of year
	 * @since 6.5.0
	 */
	titleOfWeekOfYear: function (wgt) {
		return 'Wk';
	},
	/**
	 * Generates the title of the content HTML.
	 * @param zul.db.Calendar wgt the calendar widget
	 * @param Array out an array to output HTML fragments.
	 * @param Map localizedSymbols the symbols for localization 
	 * @since 6.5.3
	 */
	titleHTML: function (wgt, out, localizedSymbols) {
		var uuid = wgt.uuid,
			view = wgt._view,
			val = wgt.getTime(),
			m = val.getMonth(),
			y = val.getFullYear(),
			ydelta = new zk.fmt.Calendar(val, wgt._localizedSymbols).getYear() - y, 
			yofs = y - (y % 10 + 1),
			ydec = zk.parseInt(y/100),
			text = wgt.$s('text'),
			minyear = wgt._minyear,
			maxyear = wgt._maxyear;
		
		switch(view) {
		case 'day':
			out.push('<span id="', uuid, '-tm" class="', text, '">',
					localizedSymbols.SMON[m], '</span> <span id="', uuid,
					'-ty" class="', text, '">', y + ydelta, '</span>');
			break;
		case 'month':
			out.push('<span id="', uuid,
					'-ty" class="', text, '">', y + ydelta, '</span>');
			break;
		case 'year':
			out.push('<span id="', uuid, '-tyd" class="', text, '">',
					(yofs + 1 > minyear ? yofs + 1 : minyear ) + ydelta, '-',
					(yofs + 10 < maxyear ? yofs + 10 : maxyear) + ydelta, '</span>');
			break;
		case 'decade':
			var ycen = ydec*100;
			out.push('<span id="', uuid, '-tyd" class="', text, '">',
					(ycen > minyear ? ycen : minyear) + ydelta, '-', 
					(ycen + 99 < maxyear ? ycen + 99 : maxyear) + ydelta, '</span>');
			break;
		}
	},
	/**
	 * Renderer the dayView for this calendar
	 * @param zul.db.Calendar wgt the calendar widget
	 * @param Array out an array to output HTML fragments.
	 * @param Map localizedSymbols the symbols for localization 
	 * @since 6.5.0
	 */
	dayView: function (wgt, out, localizedSymbols) {
		var uuid = wgt.uuid,
			sun = (7 - localizedSymbols.DOW_1ST) % 7, sat = (6 + sun) % 7,
			wkend = wgt.$s('weekend'),
			wkday = wgt.$s('weekday'),
			cell = wgt.$s('cell');
		
		out.push('<table class="', wgt.$s('body'), '" id="', uuid, '-mid"',
				zUtl.cellps0, '>', '<thead><tr>');
		for (var j = 0 ; j < 7; ++j)
			out.push('<th class="', (j == sun || j == sat) ? wkend : wkday, 
					'">' + localizedSymbols.S2DOW[j] + '</th>');
		out.push('</tr></thead><tbody>');
		for (var j = 0; j < 6; ++j) { //at most 7 rows
			out.push('<tr id="', uuid, '-w', j, '">');
			for (var k = 0; k < 7; ++k)
				out.push ('<td class="', cell, ' ', (k == sun || k == sat) ? wkend : wkday,
						'"></td>');
			out.push('</tr>');
		}
		out.push('</tbody></table>');
	},
	/**
	 * Renderer the monthView for this calendar
	 * @param zul.db.Calendar wgt the calendar widget
	 * @param Array out an array to output HTML fragments.
	 * @param Map localizedSymbols the symbols for localization 
	 * @since 6.5.0
	 */
	monthView: function (wgt, out, localizedSymbols) {
		var uuid = wgt.uuid,
			cell = wgt.$s('cell');
		out.push('<table class="', wgt.$s('body'), ' ', wgt.$s('month'),
				'" id="', uuid, '-mid"', zUtl.cellps0, '><tbody>');
		for (var j = 0 ; j < 12; ++j) {
			if (!(j % 4)) out.push('<tr>');
			out.push('<td class="', cell, '" id="', uuid, '-m', j, '" data-value="', j ,'">', 
					localizedSymbols.SMON[j] + '</td>');
			if (!((j + 1) % 4)) out.push('</tr>');
		}
		out.push('</tbody></table>');
	},
	/**
	 * Renderer the yearView for this calendar
	 * @param zul.db.Calendar wgt the calendar widget
	 * @param Array out an array to output HTML fragments.
	 * @param Map localizedSymbols the symbols for localization 
	 * @since 6.5.0
	 */
	yearView: function (wgt, out, localizedSymbols) {
		var uuid = wgt.uuid,
			cell = wgt.$s('cell'),
			disd = wgt.$s('disabled'),
			val = wgt.getTime(),
			y = val.getFullYear(),
			ydelta = new zk.fmt.Calendar(val, localizedSymbols).getYear() - y, 
			yofs = y - (y % 10 + 1),
			minyear = wgt._minyear,
			maxyear = wgt._maxyear;
		out.push('<table class="', wgt.$s('body'), ' ', wgt.$s('year'), '" id="', uuid, '-mid"',
				zUtl.cellps0, '><tbody>');

		for (var j = 0 ; j < 12; ++j) {
			if (!(j % 4)) out.push('<tr>');
			if (yofs + ydelta < minyear || yofs + ydelta > maxyear) {
				out.push('<td class="', disd, '">&nbsp;</td>');
				if (j + 1 == 12)
					out.push('</tr>'); 
				yofs++;
				continue;
			}
			out.push('<td class="', cell, '" data-value="', yofs ,'" id="', uuid, '-y', j, '" >', 
					yofs + ydelta, '</td>');
			if (!((j + 1) % 4)) out.push('</tr>');
			yofs++;
		}
		out.push('</tbody></table>');
	},
	/**
	 * Renderer the decadeView for this calendar
	 * @param zul.db.Calendar wgt the calendar widget
	 * @param Array out an array to output HTML fragments.
	 * @param Map localizedSymbols the symbols for localization 
	 * @since 6.5.0
	 */
	decadeView: function (wgt, out, localizedSymbols) {
		var uuid = wgt.uuid,
			cell = wgt.$s('cell'),
			disd = wgt.$s('disabled'),
			val = wgt.getTime(),
			y = val.getFullYear(),
			ydelta = new zk.fmt.Calendar(val, localizedSymbols).getYear() - y,
			ydec = zk.parseInt(y/100),
			minyear = wgt._minyear,
			maxyear = wgt._maxyear,
			mindec = zk.parseInt(minyear/10) * 10,
			maxdec = zk.parseInt(maxyear/10) * 10;
		
		out.push('<table class="', wgt.$s('body'), ' ', wgt.$s('decade'),
				'" id="', uuid, '-mid"',
				zUtl.cellps0, '><tbody>');
		var temp = ydec*100 - 10,
			selected = wgt.$s('selected');
		for (var j = 0 ; j < 12; ++j, temp += 10) {
			if (!(j % 4)) out.push('<tr>');
			if (temp < mindec || temp > maxdec) {
				out.push('<td class="', disd, '">&nbsp;</td>');
				if (j + 1 == 12)
					out.push('</tr>'); 
				continue;
			}
			
			out.push('<td data-value="', temp ,'" id="', uuid, '-de', j, '" class="',
					cell, (y >= temp && y <= (temp + 9)) ? ' ' + selected : '', '" >',
					(temp < minyear ? minyear : temp) + ydelta + '-<br />' +
					((temp + 9 > maxyear ? maxyear : temp + 9) + ydelta) + '</td>');
			if (!((j + 1) % 4)) out.push('</tr>');
		}
		out.push('</tbody></table>');
	}
};
var Calendar =
/**
 * A calendar.
 * <p>Default {@link #getZclass}: z-calendar.
 */
zul.db.Calendar = zk.$extends(zul.Widget, {
	_view : 'day', //"day", "month", "year", "decade",
	_minyear: 1900,
	_maxyear: 2099,
	
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onChange: this}, -1000);
	},
	$define: {
		/** Assigns a value to this component.
		 * @param Date value the date to assign. If null, today is assumed.
		 */
		/** Returns the value that is assigned to this component.
		 * @return Date
	 	 */
		value: function() {
			this.rerender();
		},
		/** Set the date limit for this component with yyyyMMdd format,
		 * such as 20100101 is mean Jan 01 2010 
		 * 
		 * <dl>
		 * <dt>Example:</dt>
		 * <dd>between 20091201 and 20091231</dd>
		 * <dd>before 20091201</dd>
		 * <dd>after 20091231</dd>
		 * </dl>
		 * 
		 * @param String constraint
		 */
		/** Returns the constraint of this component.
		 * @return String
		 */
		constraint: function() {
			var constraint = this._constraint || '';
			if (typeof this._constraint != 'string') return;
			// B50-ZK-591: Datebox constraint combination yyyymmdd and
			// no empty cause javascript error in zksandbox
			var constraints = constraint.split(','),
				format = 'yyyyMMdd',
				len = format.length + 1;
			for (var i = 0; i < constraints.length; i++) {
				constraint = jq.trim(constraints[i]); //Bug ZK-1718: should trim whitespace
				if (constraint.startsWith('between')) {
					var j = constraint.indexOf('and', 7);
					if (j < 0 && zk.debugJS) 
						zk.error('Unknown constraint: ' + constraint);
					this._beg = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(7, j), format);
					this._end = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(j + 3, j + 3 + len), format);
					if (this._beg.getTime() > this._end.getTime()) {
						var d = this._beg;
						this._beg = this._end;
						this._end = d;
					}
					
					this._beg.setHours(0, 0, 0, 0);
					this._end.setHours(0, 0, 0, 0);
				} else if (constraint.startsWith('before_') || constraint.startsWith('after_')) {
					continue; //Constraint start with 'before_' and 'after_' means errorbox position, skip it
				} else if (constraint.startsWith('before')) {
					this._end = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(6, 6 + len), format);
					this._end.setHours(0, 0, 0, 0);
				} else if (constraint.startsWith('after')) {
					this._beg = new zk.fmt.Calendar(null, this._localizedSymbols).parseDate(constraint.substring(5, 5 + len), format);
					this._beg.setHours(0, 0, 0, 0);
				}
			}
		},
		/** Sets the name of this component.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 *
		 * @param String name the name of this component.
		 */
		/** Returns the name of this component.
		 * <p>The name is used only to work with "legacy" Web application that
		 * handles user's request by servlets.
		 * It works only with HTTP/HTML-based browsers. It doesn't work
		 * with other kind of clients.
		 * <p>Don't use this method if your application is purely based
		 * on ZK's event-driven model.
		 * <p>Default: null.
		 * @return String
		 */
		name: function () {
			if (this.efield)
				this.efield.name = this._name;
		},
		/**
		 * Sets whether enable to show the week number within the current year or
    	 * not. [ZK EE]
    	 * @since 6.5.0
    	 * @param boolean weekOfYear
		 */
	    /**
	     * Returns whether enable to show the week number within the current year or not.
	     * <p>Default: false
	     * @since 6.5.0
	     * @return boolean
	     */
		weekOfYear: function () {
			if (this.desktop && zk.feature.ee)
				this.rerender();
		}
	},
	//@Override
	redraw: function () {
		Renderer.beforeRedraw(this);
		this.$supers('redraw', arguments);
	},
	onChange: function (evt) {
		this._updFormData(evt.data.value);
	},
	doKeyDown_: function (evt) {
		var keyCode = evt.keyCode,
			ofs = keyCode == 37 ? -1 : keyCode == 39 ? 1 : keyCode == 38 ? -7 : keyCode == 40 ? 7 : 0;
		if (ofs) {
			this._shift(ofs);
		} else if (keyCode == 32 || keyCode == 13) {
			// pass a fake event
			this._clickDate({
				target: this,
				domTarget: jq(this.$n('mid')).find('.' + this.$s('selected'))[0],
				stop: zk.$void
			});
		}
	},
	setMinYear_: function(v) {
		if (v === undefined) {
			this._minyear = 1900;
		} else {
			var y = this.getTime().getFullYear();
			this._minyear = v > y ? y : (v > 100 ? v : 100);
		}
	},
	setMaxYear_: function(v) {
		if (v === undefined) {
			this._maxyear = 2099;
		} else {
			var y = this.getTime().getFullYear();			
			this._maxyear = v < y ? y : (v > this._minyear ? v : this._minyear);
		}
	},
	_shift: function (ofs, opts) {
		var oldTime = this.getTime();	
		
		switch(this._view) {
		case 'month':
		case 'year':
			if (ofs == 7)
				ofs = 4;
			else if (ofs == -7)
				ofs = -4;
			break;
		case 'decade':
			if (ofs == 7)
				ofs = 4;
			else if (ofs == -7)
				ofs = -4;
			ofs *= 10;
			break;
		}		
		
		var newTime = this._shiftDate(this._view, ofs, true),
			y = newTime.getFullYear();
		
		if (y < this._minyear || y > this._maxyear)
			return; // out of the range
		
		this._shiftDate(this._view, ofs);
		
		var newTime = this.getTime();
		switch(this._view) {
		case 'day':
			if (oldTime.getYear() == newTime.getYear() &&
				oldTime.getMonth() == newTime.getMonth()) {
				opts = opts || {};
				opts.sameMonth = true; //optimize
				this._markCal(opts);
			} else 
				this.rerender();
			break;
		case 'month':
			if (oldTime.getYear() == newTime.getYear())
				this._markCal(opts);
			else
				this.rerender();
			break;
		default:		
			this.rerender();
		}
	},
	/** Returns the format of this component.
	 * @return String
	 */
	getFormat: function () {
		return this._fmt || 'yyyy/MM/dd';
	},
	_updFormData: function (val) {
		val = new zk.fmt.Calendar().formatDate(val, this.getFormat(), this._localizedSymbols);
		if (this._name) {
			val = val || '';
			if (!this.efield)
				this.efield = jq.newHidden(this._name, val, this.$n());
			else
				this.efield.value = val;
		}
	},
	focus_: function (timeout) {
		if (this._view != 'decade') 
			this._markCal({timeout: timeout});
		else {
			var anc;
			if (anc = this.$n('a'))
				_doFocus(anc, true);
		}
		return true;
	},
	bind_: function (){
		this.$supers(Calendar, 'bind_', arguments);
		var node = this.$n(),
			title = this.$n('title'),
			mid = this.$n('mid'),
			left = this.$n('left'),
			right = this.$n('right');
		if (this._view != 'decade') 
			this._markCal({silent: true});

		this.domListen_(title, 'onClick', '_changeView')
			.domListen_(mid, 'onClick', '_clickDate')
			.domListen_(left, 'onClick', '_clickArrow')
			.domListen_(right, 'onClick', '_clickArrow')
			.domListen_(node, 'onMousewheel');

		this._updFormData(this.getTime());
	},
	unbind_: function () {
		var node = this.$n(),
			title = this.$n('title'),
			mid = this.$n('mid'),
			left = this.$n('left'),
			right = this.$n('right');
		this.domUnlisten_(title, 'onClick', '_changeView')
			.domUnlisten_(mid, 'onClick', '_clickDate')
			.domUnlisten_(left, 'onClick', '_clickArrow')		
			.domUnlisten_(right, 'onClick', '_clickArrow')
			.domUnlisten_(node, 'onMousewheel')
			.$supers(Calendar, 'unbind_', arguments);
		this.efield = null;
	},
	rerender: function () {
		if (this.desktop) {
			var s = this.$n().style,
				w = s.width,
				h = s.height,
				result = this.$supers('rerender', arguments);
			s = this.$n().style;
			s.width = w;
			s.height = h;
			return result;
		}
	},
	_clickArrow: function (evt) {
		if(zk.animating()) return; // ignore
		var node = jq.nodeName(evt.domTarget, 'a') ? evt.domTarget
					: jq(evt.domTarget).parent('a')[0];
		if (jq(node).attr('disabled'))
			return;
		this._shiftView(jq(node).hasClass(this.$s('left')) ? -1 : 1);
	},
	_shiftView: function (ofs, disableAnima) {
		switch(this._view) {
		case 'day' :
			this._shiftDate('month', ofs);
			break;
		case 'month' :
			this._shiftDate('year', ofs);
			break;
		case 'year' :
			this._shiftDate('year', ofs*10);
			break;
		case 'decade' :
			this._shiftDate('year', ofs*100);
			break;
		}
		if (!disableAnima)
			this._setView(this._view, ofs);
		else
			this.rerender();
	},
	_doMousewheel: function (evt, intDelta) {		
		if (jq(this.$n(-intDelta > 0 ? 'right': 'left')).attr('disabled'))
			return;
		this._shiftView(intDelta > 0 ? -1: 1, true);
		evt.stop();
	},
	/** Returns the Date that is assigned to this component.
	 *  <p>returns today if value is null
	 * @return Date
	 */
	getTime: function () {
		return this._value || zUtl.today(this.getFormat());
	},
	_setTime: function (y, m, d, fireOnChange) {
		var dateobj = this.getTime(),
			year = y != null ? y  : dateobj.getFullYear(),
			month = m != null ? m : dateobj.getMonth(),
			day = d != null ? d : dateobj.getDate();
		this._value = _newDate(year, month, day, d == null);
		if (fireOnChange)
			this.fire('onChange', {value: this._value});
	},
	// calendar-ctrl.js will override this function 
	_clickDate: function (evt) {
		var target = evt.domTarget, val;
		for (; target; target = target.parentNode)
			try { //Note: data-dt is also used in mold/calendar.js
				if ((val = jq(target).data('value')) !== undefined) {
					val = zk.parseInt(val);
					break;
				}
			} catch (e) {
				continue; //skip
			}
		this._chooseDate(target, val);
		var anc;
		if (anc = this.$n('a'))
			_doFocus(anc, true);

		evt.stop();
	},
	_chooseDate: function (target, val) {
		if (target && !jq(target).hasClass(this.$s('disabled'))) {
			var cell = target,
				dateobj = this.getTime();
			switch(this._view) {
			case 'day' :
				var oldTime = this.getTime();
				this._setTime(null, cell._monofs != null && cell._monofs != 0 ?
						dateobj.getMonth() + cell._monofs : null, val, true /*fire onChange */);
				var newTime = this.getTime();
				if (oldTime.getYear() == newTime.getYear() &&
					oldTime.getMonth() == newTime.getMonth()) {
						this._markCal({sameMonth: true}); // optimize
				} else
					this.rerender();
				break;
			case 'month' :
				this._setTime(null, val);
				this._setView('day');
				break;
			case 'year' :
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
	},
	_shiftDate: function (opt, ofs, ignoreUpdate) {
		var dateobj = this.getTime(),
			year = dateobj.getFullYear(),
			month = dateobj.getMonth(),
			day = dateobj.getDate(),
			nofix;
		switch(opt) {
		case 'day' :
			day += ofs;
			nofix = true;
			break;
		case 'month' :
			month += ofs;
			break;
		case 'year' :
			year += ofs;
			break;
		case 'decade' :
			year += ofs;
			break;
		}
		var newTime = _newDate(year, month, day, !nofix);
		if (!ignoreUpdate) {
			this._value = newTime;
			this.fire('onChange', {value: this._value, shallClose: false, shiftView: true});
		}
		return newTime; 
	},
	_changeView: function (evt) {
		var tm = this.$n('tm'),
			ty = this.$n('ty'),
			tyd = this.$n('tyd'),
			title = this.$n('title');
		if (evt.domTarget == tm)
			this._setView('month');
		else if (evt.domTarget == ty)
			this._setView('year');
		else if (evt.domTarget == tyd )
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
	},
	_setView: (function () {
		
		// check whether to disable the arrow
		function _updateArrow(wgt) {
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
		return function (view, force) {
			if (this._view != view) {
				this._view = view;
				
				// ie9 and early won't support css3 transition
				if (zk.ie < 10) {
					this.rerender();
					return;
				}
				var out = [],
					localizedSymbols = this.getLocalizedSymbols();
				
				Renderer[view + 'View'](this, out, localizedSymbols);
				
				
				jq(this.$n('mid')).after(out.join('')).remove();
				// unlisten event
				this.unbind_();
				// listen event
				this.bind_();
				
				out = []; // reset
				Renderer.titleHTML(this, out, localizedSymbols);
				jq(this.$n('title')).html(out.join(''));
				jq(this.$n('mid')).transition({scale: 0}, 0).transition({scale: 1});
				
				_updateArrow(this);

				var anc;
				if (anc = this.$n('a'))
					_doFocus(anc, true);
				
			} else if (force) {
				var out = [],
					localizedSymbols = this.getLocalizedSymbols(),
					oldMid = this.$n('mid'),
					isLeft = force == -1,
					width = oldMid.offsetWidth,
					x = width * -1,
					self = this,
					animaCSS = this.$s('anima');
				
				Renderer[view + 'View'](this, out, localizedSymbols);
				
				jq(oldMid).after('<div style="height:' + oldMid.offsetHeight +
						'px;width:' + width + 'px" class="' + animaCSS +
						'"><div class="' + animaCSS + '-inner"></div');
				
				var animaInner = oldMid.nextSibling.firstChild;
				jq(animaInner).append(oldMid);
				oldMid = animaInner.firstChild;
				if (isLeft) {
					jq(oldMid).before(out.join('')).remove();
				} else {
					jq(oldMid).after(out.join('')).remove();
				}
				
				// clear for _makrCal to get the latest reference
				this.clearCache();
				if (view != 'decade') 
					this._markCal();
				
				var newMid;
				if (isLeft) {
					jq(animaInner.firstChild).after(oldMid);
					newMid = oldMid.previousSibling;
					jq(animaInner).css({left: x});
					x = 0;
				} else {
					jq(animaInner.firstChild).before(oldMid);
					newMid = oldMid.nextSibling;
				}
				
				jq(animaInner).animate({left: x}, {done: function (/*callback*/) {
						self.domUnlisten_(oldMid, 'onClick', '_clickDate');
						jq(animaInner.parentNode).after(newMid).remove();
						self.domListen_(newMid, 'onClick', '_clickDate');
						var out = []; // reset
						Renderer.titleHTML(self, out, localizedSymbols);
						jq(self.$n('title')).html(out.join(''));
						self.clearCache();
					}
				});

				_updateArrow(this);
			}
		};
	})(),
	getLocalizedSymbols: function () {
		return this._localizedSymbols || {
			DOW_1ST: zk.DOW_1ST,
				ERA: zk.ERA,    
			 YDELTA: zk.YDELTA,
			   SDOW: zk.SDOW,
			  S2DOW: zk.S2DOW,
			   FDOW: zk.FDOW,
			   SMON: zk.SMON,
			  S2MON: zk.S2MON,
			   FMON: zk.FMON,
				APM: zk.APM
		};
	},
	/**
	 * Check whether the date is out of range between _minyear and _maxyear
	 * @param boolean left it is used for the left arrow button
	 * @param Date date the date object for the range if null, the current value
	 * of {@link #getTime()} is assumed.
	 * @returns boolean if true it means the date is out of range.
	 * @since 6.5.3
	 */
	isOutOfRange: function (left, date) {
		var view = this._view,
			val = date || this.getTime(),
			y = val.getFullYear(),
			ydelta = new zk.fmt.Calendar(val, this._localizedSymbols).getYear() - y, 
			yofs = y - (y % 10 + 1),
			ydec = zk.parseInt(y/100),
			minyear = this._minyear,
			maxyear = this._maxyear,		
			mincen = zk.parseInt(minyear / 100) * 100,
			maxcen = zk.parseInt(maxyear / 100) * 100,	
			mindec = zk.parseInt(minyear / 10) * 10,
			maxdec = zk.parseInt(maxyear / 10) * 10;
		
		if (view == 'decade') {
			var value = ydec*100 + ydelta;
			return left ? value == mincen : value == maxcen;
		} else if (view == 'year') {
			var value = yofs + ydelta;
			return left ? value < minyear : value + 10 >= maxyear;
		} else if (view == 'day') {
			var value = y + ydelta,
				m = val.getMonth();
			return left ? value <= minyear && m == 0 : value >= maxyear && m == 11;
		} else {
			var value = y + ydelta;
			return left ? value <= minyear : value >= maxyear;
		}
	},
	_markCal: function (opts) {
		this._markCal0(opts);
		var anc;
		if ((anc = this.$n('a')) && (!opts || !opts.silent))
			_doFocus(anc, opts && opts.timeout );
	},
	// calendar-ctrl.js will override this function 
	_markCal0: function (opts) {
		var	seldate = this.getTime(),
		 	m = seldate.getMonth(),
			mid = this.$n('mid'),
			$mid = jq(mid),
			seldClass = this.$s('selected'),
			y = seldate.getFullYear(),
			minyear = this._minyear,
			maxyear = this._maxyear;
		if (this._view == 'day') {
			var d = seldate.getDate(),
				DOW_1ST = (this._localizedSymbols && this._localizedSymbols.DOW_1ST )|| zk.DOW_1ST, //ZK-1061
				v = new Date(y, m, 1).getDay()- DOW_1ST,
				last = new Date(y, m + 1, 0).getDate(), //last date of this month
				prev = new Date(y, m, 0).getDate(), //last date of previous month
				today = zUtl.today(), //no time part
				outsideClass = this.$s('outside'),
				disdClass = this.$s('disabled');
			
			$mid.find('.' + seldClass).removeClass(seldClass);
			if (!opts || !opts.sameMonth) {
				$mid.find('.' + outsideClass).removeClass(outsideClass);
				$mid.find('.' + disdClass).removeClass(disdClass);
			}
			
			if (v < 0) v += 7;
			for (var j = 0, cur = -v + 1; j < 6; ++j) {
				var week = this.$n('w' + j);
				if (week != null) {
					for (var k = 0; k < 7; ++k, ++cur) {
						v = cur <= 0 ? prev + cur: cur <= last ? cur: cur - last;
						if (k == 0 && cur > last)
							week.style.display = 'none';
						else {
							if (k == 0) week.style.display = '';
							var	monofs = cur <= 0 ? -1: cur <= last ? 0: 1,
								bSel = cur == d;
							
							// check whether the date is out of range
							if (y >= maxyear && m == 11 && monofs == 1
									|| y <= minyear && m == 0 && monofs == -1)
								continue;

							var $cell = jq(week.cells[k]);
							
							$cell[0]._monofs = monofs;
							if (bSel)
								$cell.addClass(seldClass);
								
								
							//not same month
							if (!opts || !opts.sameMonth) { // optimize
								if (monofs) {
									$cell.addClass(outsideClass);
								}
								if (Renderer.disabled(this, y, m + monofs, v, today)) {
									$cell.addClass(disdClass);
								}
								$cell.html(Renderer.cellHTML(this, y, m + monofs, v, monofs)).
									data('value', v);
							}
						}
					}
				}
			}
		} else {
			var isMon = this._view == 'month',
				field = isMon ? 'm': 'y',
				index = isMon? m: y % 10 + 1,
				node;

			$mid.find('.' + seldClass).removeClass(seldClass);
				
			for (var j = 0; j < 12; ++j)
				if (index == j && (node = this.$n(field + j)))
					jq(node).addClass(seldClass);
		}
	}
});
})();