/* SimpleConstraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:58:11     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Date;
import java.util.regex.Pattern;

import com.potix.lang.Classes;
import com.potix.util.Dates;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;

import com.potix.zul.mesg.MZul;

/**
 * A simple constraint that you could build based the predefined constants.
 *
 * <p>Depending on the component (such as {@link Intbox} and {@link Datebox},
 * you could combine the flags, such as {@link #NO_POSITIVE} + {@link #NO_ZERO}
 * to accept only negative number. 
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class SimpleConstraint implements Constraint {
	/** Postive numbers are not allowed. */
	public static final int NO_POSITIVE = 0x0001;
	/** Negative numbers are not allowed. */
	public static final int NO_NEGATIVE = 0x0002;
	/** Zero numbers are not allowed. */
	public static final int NO_ZERO = 0x0004;
	/** Empty is not allowed.
	 * If not specified, empty usually means null.
	 */
	public static final int NO_EMPTY = 0x0100;
	/** Date in the future is not allowed. (Only date part is compared)
	 */
	public static final int NO_FUTURE = NO_POSITIVE;
	/** Date in the past is not allowed. (Only date part is compared)
	 */
	public static final int NO_PAST = NO_NEGATIVE;
	/** Today is not allowed. (Only date part is compared)
	 */
	public static final int NO_TODAY = NO_ZERO;

	private final int _flags;
	private final Pattern _regex;
	private final String _errmsg;

	public SimpleConstraint(int flags) {
		this(flags, null, null);
	}
	/** Constraints a constraint.
	 *
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleConstraint(int flags, String errmsg) {
		this(flags, null, errmsg);
	}
	/** Constructs a regular-expression constraint.
	 *
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleConstraint(String regex, String errmsg) {
		this(0, regex, errmsg);
	}
	/** Constructs a constraint combining regular expression.
	 *
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleConstraint(int flags, String regex, String errmsg) {
		_flags = flags;
		_regex = regex == null || regex.length() == 0 ?
			null: Pattern.compile(regex);
		_errmsg = errmsg == null || errmsg.length() == 0 ? null: errmsg;
	}

	/** Parses flags from a string to an integer representing a combination
	 * of {@link #NO_POSITIVE} and other NO_xxx flags.
	 *
	 * @param flags a list of constraint separated by comma.
	 * Example: no positive, no zero
	 */
	public static final SimpleConstraint getInstance(String flags) {
		int iflags = 0;
		String regex = null, errmsg = null;
		l_out:
		for (int j = 0, k = 0, len = flags.length(); k >= 0; j = k + 1) {
			for (;; ++j) {
				if (j >= len) break l_out; //done

				char cc = flags.charAt(j);
				switch (cc) {
				case '/':
					for (k = ++j;; ++k) { //look for ending /
						if (k >= len) { //no ending /
							k = -1;
							break;
						}

						cc = flags.charAt(k);
						if (cc == '/') break; //ending / found
						if (cc == '\\') ++k; //skip one
					}
					regex = k >= 0 ? flags.substring(j, k): flags.substring(j);
					continue l_out;
				case ':':
					errmsg = flags.substring(j + 1).trim();
					break l_out; //done
				}
				if (!Character.isWhitespace(cc))
					break;
			}

			String s;
			for (k = j;; ++k) {
				if (k >= len) {
					s = flags.substring(j);
					k = -1;
					break;
				}
				final char cc = flags.charAt(k);
				if (cc == ',' || cc == ':' || cc == ';' || cc == '/') {
					s = flags.substring(j, k);
					if (cc == ':' || cc == '/') --k;
					break;
				}
			}
			s = s.trim().toLowerCase();
			if (s.equals("no positive"))
				iflags |= NO_POSITIVE;
			else if (s.equals("no negative"))
				iflags |= NO_NEGATIVE;
			else if (s.equals("no zero"))
				iflags |= NO_ZERO;
			else if (s.equals("no empty"))
				iflags |= NO_EMPTY;
			else if (s.equals("no future"))
				iflags |= NO_FUTURE;
			else if (s.equals("no past"))
				iflags |= NO_PAST;
			else if (s.equals("no today"))
				iflags |= NO_TODAY;
			else if (s.length() > 0)
				throw new UiException("Unknown constraint: "+s);
		}

		return new SimpleConstraint(iflags, regex, errmsg);
	}

	//-- Constraint --//
	public void validate(Component comp, Object value)
	throws WrongValueException {
		if (value == null) {
			if ((_flags & NO_EMPTY) != 0)
				throw wrongValue(comp, MZul.EMPTY_NOT_ALLOWED);
		} else if (value instanceof Number) {
			if ((_flags & (NO_POSITIVE|NO_NEGATIVE|NO_ZERO)) == 0)
				return; //nothing to check

			final int cmp = ((Comparable)value).compareTo(
				Classes.coerce(value.getClass(), null, false)); //compare to zero
			if (cmp > 0) {
				if ((_flags & NO_POSITIVE) != 0)
					throw wrongValue(comp, getMessageForNumberDenied());
			} else if (cmp == 0) {
				if ((_flags & NO_ZERO) != 0)
					throw wrongValue(comp, getMessageForNumberDenied());
			} else {
				if ((_flags & NO_NEGATIVE) != 0)
					throw wrongValue(comp, getMessageForNumberDenied());
			}
		} else if (value instanceof String) {
			final String s = (String)value;
			if ((_flags & NO_EMPTY) != 0 && s.length() == 0)
				throw wrongValue(comp, MZul.EMPTY_NOT_ALLOWED);
			if (_regex != null && !_regex.matcher(s != null ? s: "").matches())
				throw wrongValue(comp, MZul.ILLEGAL_VALUE);
		} else if (value instanceof Date) {
			if ((_flags & (NO_FUTURE|NO_PAST|NO_TODAY)) == 0)
				return;
			final Date date = Dates.beginOfDate((Date)value, null);
			final int cmp = date.compareTo(Dates.today());
			if (cmp > 0) {
				if ((_flags & NO_FUTURE) != 0)
					throw wrongValue(comp, getMessageForDateDenied());
			} else if (cmp == 0) {
				if ((_flags & NO_TODAY) != 0)
					throw wrongValue(comp, getMessageForDateDenied());
			} else {
				if ((_flags & NO_PAST) != 0)
					throw wrongValue(comp, getMessageForDateDenied());
			}
		}
	}
	private WrongValueException wrongValue(Component comp, int errcode) {
		return _errmsg != null ? new WrongValueException(comp, _errmsg):
			new WrongValueException(comp, errcode);
	}

	private int getMessageForNumberDenied() {
		switch (_flags & (NO_POSITIVE|NO_NEGATIVE|NO_ZERO)) {
		case (NO_POSITIVE|NO_ZERO):
			return MZul.NO_POSITIVE_ZERO;
		case (NO_POSITIVE):
			return MZul.NO_POSITIVE;
		case (NO_NEGATIVE|NO_ZERO):
			return MZul.NO_NEGATIVE_ZERO;
		case (NO_NEGATIVE):
			return MZul.NO_NEGATIVE;
		case (NO_ZERO):
			return MZul.NO_ZERO;
		case (NO_POSITIVE|NO_NEGATIVE|NO_ZERO):
			return MZul.NO_POSITIVE_NEGATIVE_ZERO;
		case (NO_POSITIVE|NO_NEGATIVE):
			return MZul.NO_POSITIVE_NEGATIVE;
		}
		throw new InternalError();
	}
	private int getMessageForDateDenied() {
		switch (_flags & (NO_FUTURE|NO_PAST|NO_TODAY)) {
		case (NO_FUTURE|NO_TODAY):
			return MZul.NO_FUTURE_TODAY;
		case (NO_FUTURE):
			return MZul.NO_FUTURE;
		case (NO_PAST|NO_TODAY):
			return MZul.NO_PAST_TODAY;
		case (NO_PAST):
			return MZul.NO_PAST;
		case (NO_TODAY):
			return MZul.NO_TODAY;
		case (NO_FUTURE|NO_PAST|NO_TODAY):
			return MZul.NO_FUTURE_PAST_TODAY;
		case (NO_FUTURE|NO_PAST):
			return MZul.NO_FUTURE_PAST;
		}
		throw new InternalError();
	}
	public String getValidationScript() {
		return (_flags & NO_EMPTY) != 0 ? "zkVld.noEmpty": null;
			//FUTURE: support more validation in client
	}
	public boolean isClientComplete() {
		return (_flags == 0 || _flags == NO_EMPTY)
			&& _regex == null && _errmsg == null;
	}
}
