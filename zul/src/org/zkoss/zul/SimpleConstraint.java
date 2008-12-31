/* SimpleConstraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:58:11     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Strings;
import org.zkoss.util.Dates;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.mesg.MZul;

/**
 * A simple constraint that you could build based the predefined constants.
 *
 * <p>Depending on the component (such as {@link Intbox} and {@link Datebox},
 * you could combine the flags, such as {@link #NO_POSITIVE} + {@link #NO_ZERO}
 * to accept only negative number. 
 *
 * @author tomyeh
 */
public class SimpleConstraint
implements Constraint, ClientConstraint, java.io.Serializable {
    private static final long serialVersionUID = 20070411L;

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
	/**
	 * The value must match inside the data from ListModel only.
	 */
	public static final int STRICT = 0x0200;
	/** Date in the future is not allowed. (Only date part is compared)
	 */
	public static final int NO_FUTURE = NO_POSITIVE;
	/** Date in the past is not allowed. (Only date part is compared)
	 */
	public static final int NO_PAST = NO_NEGATIVE;
	/** Today is not allowed. (Only date part is compared)
	 */
	public static final int NO_TODAY = NO_ZERO;
	/** The constraints. A combination of {@link #NO_POSITIVE} and others.
	 */
	protected int _flags;
	private Pattern _regex;
	private String _errmsg;
	private String _raw;

	/** Constructs a constraint with flags.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 */
	public SimpleConstraint(int flags) {
		this(flags, null, null);
	}
	/** Constructs a constraint with flags and an error message.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
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
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleConstraint(int flags, String regex, String errmsg) {
		_flags = flags;
		_regex = regex == null || regex.length() == 0 ?
			null: Pattern.compile(regex);
		_errmsg = errmsg == null || errmsg.length() == 0 ? null: errmsg;
		_raw = null;
	}
	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: no positive, no zero
	 * @since 3.0.2
	 */
	public SimpleConstraint(String constraint) {
		String regex = null, errmsg = null;
		l_out:
		for (int j = 0, k = 0, len = constraint.length(); k >= 0; j = k + 1) {
			for (;; ++j) {
				if (j >= len) break l_out; //done

				char cc = constraint.charAt(j);
				switch (cc) {
				case '/':
					for (k = ++j;; ++k) { //look for ending /
						if (k >= len) { //no ending /
							k = -1;
							break;
						}

						cc = constraint.charAt(k);
						if (cc == '/') break; //ending / found
						if (cc == '\\') ++k; //skip one
					}
					regex = k >= 0 ? constraint.substring(j, k): constraint.substring(j);
					continue l_out;
				case ':':
					errmsg = constraint.substring(j + 1).trim();
					break l_out; //done
				}
				if (!Character.isWhitespace(cc))
					break;
			}

			String s;
			for (k = j;; ++k) {
				if (k >= len) {
					s = constraint.substring(j);
					k = -1;
					break;
				}
				final char cc = constraint.charAt(k);
				if (cc == ',' || cc == ':' || cc == ';' || cc == '/') {
					s = constraint.substring(j, k);
					if (cc == ':' || cc == '/') --k;
					break;
				}
			}

			_flags |= parseConstraint(s.trim().toLowerCase());
		}

		_raw = constraint;
		_regex = regex == null || regex.length() == 0 ?
			null: Pattern.compile(regex);
		_errmsg = errmsg == null || errmsg.length() == 0 ? null: errmsg;
	}

	/** Parses a list of constraints from a string to an integer
	 * representing a combination of {@link #NO_POSITIVE} and other flags.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: no positive, no zero
	 */
	public static final SimpleConstraint getInstance(String constraint) {
		return new SimpleConstraint(constraint);
	}
	/** Parses a constraint into an integer value.
	 * For example, "no positive" is parsed to {@link #NO_POSITIVE}.
	 *
	 * <p>Deriving classes might override this to provide more constraints.
	 *
	 * @since 3.0.2
	 */
	protected int parseConstraint(String constraint) throws UiException {
		if (constraint.equals("no positive"))
			return NO_POSITIVE;
		else if (constraint.equals("no negative"))
			return NO_NEGATIVE;
		else if (constraint.equals("no zero"))
			return NO_ZERO;
		else if (constraint.equals("no empty"))
			return NO_EMPTY;
		else if (constraint.equals("no future"))
			return NO_FUTURE;
		else if (constraint.equals("no past"))
			return NO_PAST;
		else if (constraint.equals("no today"))
			return NO_TODAY;
		else if (constraint.equals("strict"))
			return STRICT;
		else if (constraint.length() > 0)
			throw new UiException("Unknown constraint: "+constraint);
		return 0;
	}
	/**
	 * Returns the constraint flags, i.e., a combination of
	 * {@link #NO_POSITIVE}, {@link #NO_NEGATIVE}, {@link #STRICT} and others.
	 *
	 * @since 3.0.2
	 */
	public int getFlags() {
		return _flags;
	}
	/** Returns the custom error message that shall be shown if an error occurs,
	 * or null if no custom error message specified.
	 */
	public String getErrorMessage(Component comp) {
		return _errmsg;
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
			if ((_flags & STRICT) != 0) {
				if (s.length() > 0 && comp instanceof Combobox) {
					for (Iterator it = ((Combobox)comp).getItems().iterator(); it.hasNext();) {
						final Comboitem ci = (Comboitem)it.next();
						if(!ci.isDisabled() && ci.isVisible() && s.equalsIgnoreCase(ci.getLabel()))
							return;
					}
					throw wrongValue(comp, MZul.VALUE_NOT_MATCHED);
				}
			}
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
	//ClientConstraint//
	public String getClientConstraint() {
		if (_raw != null)
			return '\'' + Strings.escape(_raw, "\\'") + '\'';

		final StringBuffer sb = new StringBuffer("new zul.inp.SimpleConstraint(");
		if (_flags != 0 || _regex != null || _errmsg != null) {
			sb.append(_flags);
			if (_regex != null || _errmsg != null) {
				sb.append(',');
				if (_regex != null) {
					sb.append('\'');
					Strings.escape(sb, _regex.pattern(), "\\'");
					sb.append('\'');
				} else
					sb.append("null");
				if (_errmsg != null) {
					sb.append(",'");
					Strings.escape(sb, _errmsg, "\\'");
					sb.append('\'');
				}
			}
		}
		return sb.append(")").toString();
	}
	/** Default: null (since it depends on zul.inp which is loaded for
	* all input widgets).
	 */
	public String getClientPackages() {
		return null;
	}
}
