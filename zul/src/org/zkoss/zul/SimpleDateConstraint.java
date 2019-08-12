/* SimpleDateConstraint.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 25 12:06:30     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.zkoss.util.Dates;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

/**
 * A simple date constraint.
 *
 * @author tomyeh
 * @since 3.0.2
 */
public class SimpleDateConstraint extends AbstractSimpleDateTimeConstraint<Date> {
	public SimpleDateConstraint(int flags) {
		super(flags);
	}

	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleDateConstraint(int flags, String errmsg) {
		super(flags, errmsg);
	}

	/** Constructs a regular-expression constraint.
	 *
	 * @param regex ignored if null or empty. Unlike constraint, the regex doesn't need to enclose with '/'.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @deprecated As of release 8.0.1, replaced with {@link #SimpleDateConstraint(Pattern, String)}
	 */
	public SimpleDateConstraint(String regex, String errmsg) {
		super(regex == null || regex.length() == 0 ? null : Pattern.compile(regex), errmsg);
	}

	/** Constructs a regular-expression constraint.
	 *
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @since 8.0.1
	 */
	public SimpleDateConstraint(Pattern regex, String errmsg) {
		super(regex, errmsg);
	}

	/** Constructs a constraint combining regular expression.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty. Unlike constraint, the regex doesn't need to enclose with '/'.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @deprecated As of release 8.0.1, replaced with {@link #SimpleDateConstraint(int, Pattern, String)}
	 */
	public SimpleDateConstraint(int flags, String regex, String errmsg) {
		super(flags, regex == null || regex.length() == 0 ? null : Pattern.compile(regex), errmsg);
	}

	/** Constructs a constraint combining regular expression.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @since 8.0.1
	 */
	public SimpleDateConstraint(int flags, Pattern regex, String errmsg) {
		super(flags, regex, errmsg);
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
	}

	@Override
	protected void fixConstraint() {
		if ((_flags & NO_FUTURE) != 0 && _end == null)
			_end = Dates.today();
		if ((_flags & NO_PAST) != 0 && _beg == null)
			_beg = Dates.today();
	}

	@Override
	protected Date parseFrom(String val) throws UiException {
		try {
			return getDateFormat(_tzone).parse(val.trim());
		} catch (ParseException ex) {
			throw new UiException("Not a date: " + val + ". Format: yyyyMMdd", ex);
		}
	}

	private static SimpleDateFormat getDateFormat(TimeZone tzone) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locales.getCurrent());
		df.setTimeZone(tzone != null ? tzone : TimeZones.getCurrent());
		return df;
	}

	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		super.validate(comp, value);
		if (value instanceof Date) {
			validate0(comp, Dates.beginOfDate((Date) value, _tzone));
		}
	}

	@Override
	protected String valueToString(Component comp, Date value) {
		if (value == null)
			return "";
		if (comp instanceof Datebox)
			return ((Datebox) comp).coerceToString(value);
		return getDateFormat(_tzone).format(value);
	}
}
