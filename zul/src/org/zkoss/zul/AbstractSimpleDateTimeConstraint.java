/* AbstractSimpleDateTimeConstraint.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 13 17:14:58 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.util.TimeZone;
import java.util.regex.Pattern;

import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.mesg.MZul;

/**
 * A skeleton of simple date/time constraint.
 *
 * @author rudyhuang
 * @since 9.0.0
 */
public abstract class AbstractSimpleDateTimeConstraint<T extends Comparable<? super T>> extends SimpleConstraint {
	protected T _beg;
	protected T _end;
	protected TimeZone _tzone = TimeZones.getCurrent();

	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 */
	public AbstractSimpleDateTimeConstraint(int flags) {
		super(flags);
		fixConstraint();
	}

	/** Constraints a constraint.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public AbstractSimpleDateTimeConstraint(int flags, String errmsg) {
		super(flags, errmsg);
		fixConstraint();
	}

	/** Constructs a constraint combining regular expression.
	 *
	 * @param regex ignored if null or empty. Unlike constraint, the regex doesn't need to enclose with '/'.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @deprecated As of release 8.0.1, replaced with {@link #AbstractSimpleDateTimeConstraint(int, Pattern, String)}
	 */
	@Deprecated
	public AbstractSimpleDateTimeConstraint(String regex, String errmsg) {
		super(regex, errmsg);
		fixConstraint();
	}

	/** Constructs a regular-expression constraint.
	 *
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public AbstractSimpleDateTimeConstraint(Pattern regex, String errmsg) {
		super(regex, errmsg);
		fixConstraint();
	}

	/** Constructs a constraint combining regular expression.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty. Unlike constraint, the regex doesn't need to enclose with '/'.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @deprecated As of release 8.0.1, replaced with {@link #AbstractSimpleDateTimeConstraint(int, Pattern, String)}
	 */
	@Deprecated
	public AbstractSimpleDateTimeConstraint(int flags, String regex, String errmsg) {
		super(flags, regex, errmsg);
		fixConstraint();
	}

	/** Constructs a constraint combining regular expression.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public AbstractSimpleDateTimeConstraint(int flags, Pattern regex, String errmsg) {
		super(flags, regex, errmsg);
		fixConstraint();
	}

	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: "between 20071012 and 20071223", "before 20080103"
	 */
	public AbstractSimpleDateTimeConstraint(String constraint) {
		super(constraint);
		fixConstraint();
	}

	protected abstract void fixConstraint();

	/** Returns the beginning date, or null if there is no constraint of
	 * the beginning date.
	 */
	public T getBeginDate() {
		return _beg;
	}

	/** Returns the ending date, or null if there is no constraint of
	 * the ending date.
	 */
	public T getEndDate() {
		return _end;
	}

	/**
	 * Sets time zone that this date constraint belongs to
	 */
	public void setTimeZone(TimeZone tzone) {
		_tzone = tzone;
		_finishParseCst = false;
	}

	@Override
	protected int parseConstraint(String constraint) throws UiException {
		if (constraint.startsWith("between")) {
			final int j = constraint.indexOf("and", 7);
			if (j < 0)
				throw new UiException("Constraint syntax error: " + constraint);
			_beg = parseFrom(constraint.substring(7, j));
			_end = parseFrom(constraint.substring(j + 3));
			if (_beg.compareTo(_end) > 0) {
				final T d = _beg;
				_beg = _end;
				_end = d;
			}
			return 0;
		} else if (constraint.startsWith("before") && !constraint.startsWith("before_")) {
			_end = parseFrom(constraint.substring(6));
			return 0;
		} else if (constraint.startsWith("after") && !constraint.startsWith("after_")) {
			_beg = parseFrom(constraint.substring(5));
			return 0;
		}
		return super.parseConstraint(constraint);
	}

	protected abstract T parseFrom(String val) throws UiException;

	@Override
	public void validate(Component comp, Object value) throws WrongValueException {
		super.validate(comp, value);
	}

	protected void validate0(Component comp, T value) throws WrongValueException {
		if (_beg != null && _beg.compareTo(value) > 0) {
			throw outOfRangeValue(comp);
		}
		if (_end != null && _end.compareTo(value) < 0) {
			throw outOfRangeValue(comp);
		}
	}

	private WrongValueException outOfRangeValue(Component comp) {
		final String errmsg = getErrorMessage(comp);
		if (errmsg != null)
			return new WrongValueException(comp, errmsg);

		final String s = _beg != null ? _end != null ? valueToString(comp, _beg) + " ~ " + valueToString(comp, _end)
				: ">= " + valueToString(comp, _beg) : "<= " + valueToString(comp, _end);
		return new WrongValueException(comp, MZul.OUT_OF_RANGE, s);
	}

	protected abstract String valueToString(Component comp, T value);
}
