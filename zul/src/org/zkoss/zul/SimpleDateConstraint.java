/* SimpleDateConstraint.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 25 12:06:30     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.zkoss.mesg.Messages;
import org.zkoss.util.Dates;
import org.zkoss.util.TimeZones;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.mesg.MZul;

/**
 * A simple date constraint.
 *
 * @author tomyeh
 * @since 3.0.2
 */
public class SimpleDateConstraint extends SimpleConstraint {
	private Date _beg, _end;

	public SimpleDateConstraint(int flags) {
		super(flags);
		fixConstraint();
	}
	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleDateConstraint(int flags, String errmsg) {
		super(flags, errmsg);
		fixConstraint();
	}
	/** Constructs a regular-expression constraint.
	 *
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleDateConstraint(String regex, String errmsg) {
		super(regex, errmsg);
		fixConstraint();
	}
	/** Constructs a constraint combining regular expression.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleDateConstraint(int flags, String regex, String errmsg) {
		super(flags, regex, errmsg);
		fixConstraint();
	}
	/** Constructs a constraint with beginning and ending date.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param begin the beginning date, or null if no constraint at the beginning
	 * date.
	 * @param end the ending date, or null if no constraint at the ending
	 * date.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleDateConstraint(int flags, Date begin, Date end, String errmsg) {
		super(flags, errmsg);
		_beg = begin;
		_end = end;
		fixConstraint();
	}
	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: "between 20071012 and 20071223", "before 20080103"
	 */
	public SimpleDateConstraint(String constraint) {
		super(constraint);
		fixConstraint();
	}
	private void fixConstraint() {
		if ((_flags & NO_FUTURE) != 0 && _end == null)
			_end = Dates.today();
		if ((_flags & NO_PAST) != 0 && _beg == null)
			_beg = Dates.today();
	}

	/** Returns the beginning date, or null if there is no constraint of
	 * the beginning date.
	 */
	public Date getBeginDate() {
		return _beg;
	}
	/** Returns the ending date, or null if therer is no constraint of
	 * the ending date.
	 */
	public Date getEndDate() {
		return _end;
	}

	//super//
	protected int parseConstraint(String constraint) throws UiException {
		if (constraint.startsWith("between")) {
			final int j = constraint.indexOf("and", 7);
			if (j < 0)
				throw new UiException("Constraint syntax error: "+constraint);
			_beg = parseDate(constraint.substring(7, j));
			_end = parseDate(constraint.substring(j + 3));
			if (_beg.compareTo(_end) > 0) {
				final Date d = _beg;
				_beg = _end;
				_end = d;
			}
			return 0;
		} else if (constraint.startsWith("before")) {
			_end = parseDate(constraint.substring(6));
			return 0;
		} else if (constraint.startsWith("after")) {
			_beg = parseDate(constraint.substring(5));
			return 0;
		}
		return super.parseConstraint(constraint);
	}
	private static Date parseDate(String val) throws UiException {
		try {
			return getDateFormat().parse(val.trim());
		} catch (ParseException ex) {
			throw new UiException("Not a date: "+val+". Format: yyyyMMdd", ex);
		}
	}
	private static SimpleDateFormat getDateFormat() {
		SimpleDateFormat df = (SimpleDateFormat)_df.get();
		if (df == null)
			_df.set(df = new SimpleDateFormat("yyyyMMdd"));
		df.setTimeZone(TimeZones.getCurrent());
		return df;
	}
	private static final ThreadLocal _df = new ThreadLocal();

	public void validate(Component comp, Object value)
	throws WrongValueException {
		if (value instanceof Date) {
			final Date d = (Date)value;
			if (_beg != null && _beg.compareTo(d) > 0)
				throw outOfRangeValue(comp);
			if (_end != null && _end.compareTo(d) < 0)
				throw outOfRangeValue(comp);
		}
		super.validate(comp, value);
	}
	private WrongValueException outOfRangeValue(Component comp) {
		final String errmsg = getErrorMessage(comp);
		if (errmsg != null)
			return new WrongValueException(comp, errmsg);

		final String s =
			_beg != null ? _end != null ?
					dateToString(comp, _beg) + " ~ " + dateToString(comp, _end):
					">= " + dateToString(comp, _beg):
					"<= " + dateToString(comp, _end);
		return new WrongValueException(comp, MZul.OUT_OF_RANGE, s);
	}
	private static String dateToString(Component comp, Date d) {
		if (d == null)
			return "";
		if (comp instanceof Datebox)
			return ((Datebox)comp).coerceToString(d);
		return getDateFormat().format(d);
	}
}
