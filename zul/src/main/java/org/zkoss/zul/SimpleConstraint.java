/* SimpleConstraint.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:58:11     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Strings;
import org.zkoss.util.Dates;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.mesg.MZul;

/**
 * The default constraint supporting no empty, regular expressions and so on.
 *
 * <p>Depending on the component (such as {@link Intbox} and {@link Datebox},
 * you could combine the flags, such as {@link #NO_POSITIVE} + {@link #NO_ZERO}
 * to accept only negative number. 
 *
 * @author tomyeh
 */
public class SimpleConstraint implements Constraint, ClientConstraint, java.io.Serializable {
	private static final long serialVersionUID = 20070411L;

	/** Positive numbers are not allowed. */
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
	/** Indicates this constraint requires the server validation.
	 * It means, after the client validates the value successfully, it
	 * will send the value to the server for further validation (by calling
	 * {@link #validate}.
	 * It is useful if the result of the regular expressions is different
	 * at the client (with JavaScript) and the server with ava).
	 */
	public static final int SERVER = 0x0400;
	/** Date in the future is not allowed. (Only date part is compared)
	 */
	public static final int NO_FUTURE = NO_POSITIVE;
	/** Date in the past is not allowed. (Only date part is compared)
	 */
	public static final int NO_PAST = NO_NEGATIVE;
	/** Today is not allowed. (Only date part is compared)
	 */
	public static final int NO_TODAY = NO_ZERO;
	/** The Error-box position. 
	 */
	public static final int BEFORE_START = 0x1000;
	/** The Error-box position. 
	 */
	public static final int BEFORE_END = 0x2000;
	/** The Error-box position. 
	 */
	public static final int END_BEFORE = 0x3000;
	/** The Error-box position. 
	 */
	public static final int END_AFTER = 0x4000;
	/** The Error-box position. 
	 */
	public static final int AFTER_END = 0x5000;
	/** The Error-box position. 
	 */
	public static final int AFTER_START = 0x6000;
	/** The Error-box position. 
	 */
	public static final int START_AFTER = 0x7000;
	/** The Error-box position. 
	 */
	public static final int START_BEFORE = 0x8000;
	/** The Error-box position. 
	 */
	public static final int OVERLAP = 0x9000;
	/** The Error-box position. 
	 */
	public static final int OVERLAP_END = 0xa000;
	/** The Error-box position. 
	 */
	public static final int OVERLAP_BEFORE = 0xb000;
	/** The Error-box position. 
	 */
	public static final int OVERLAP_AFTER = 0xc000;
	/** The Error-box position. 
	 */
	public static final int AT_POINTER = 0xd000;
	/** The Error-box position. 
	 */
	public static final int AFTER_POINTER = 0xe000;

	/** The constraints. A combination of {@link #NO_POSITIVE} and others.
	 */
	protected int _flags;
	protected boolean _finishParseCst = true;
	private Pattern _regex;
	private String _errmsg;
	private String _raw;
	private String _refined;

	/** Constructs a constraint with flags.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 */
	public SimpleConstraint(int flags) {
		this(flags, (Pattern) null, null);
	}

	/** Constructs a constraint with flags and an error message.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param errmsg the error message to display. Ignored if null or empty.
	 */
	public SimpleConstraint(int flags, String errmsg) {
		this(flags, (Pattern) null, errmsg);
	}

	/** Constructs a regular-expression constraint.
	 *
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @since 8.0.1
	 */
	public SimpleConstraint(Pattern regex, String errmsg) {
		this(0, regex, errmsg);
	}

	/** Constructs a constraint combining regular expression.
	 *
	 * @param flags a combination of {@link #NO_POSITIVE}, {@link #NO_NEGATIVE},
	 * {@link #NO_ZERO}, and so on.
	 * @param regex ignored if null or empty
	 * @param errmsg the error message to display. Ignored if null or empty.
	 * @since 8.0.1
	 */
	public SimpleConstraint(int flags, Pattern regex, String errmsg) {
		_flags = flags;
		_regex = regex;
		_errmsg = errmsg == null || errmsg.length() == 0 ? null : errmsg;
		_raw = null;
	}

	/** Constructs a constraint with a list of constraints separated by comma.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: <code>no positive</code>, <code>no zero</code>, or use '/'
	 * to enclose the regular expression as follows. {@code /.+@.+\.[a-z]+/: email only}
	 * @since 3.0.2
	 */
	public SimpleConstraint(String constraint) {
		_raw = constraint;
		_finishParseCst = false;
	}

	private void parseCst(String constraint) {
		String regex = null, errmsg = null, regexFlags = "";
		l_out:
		for (int j = 0, k = 0, len = constraint.length(); k >= 0; j = k + 1) {
			for (; ; ++j) {
				if (j >= len)
					break l_out; //done

				char cc = constraint.charAt(j);
				switch (cc) {
					case '/':
						boolean hasEndingSlash = false;
						for (k = ++j; ; ++k) { //look for ending /
							if (k >= len) { //no ending /
								k = -1;
								break;
							}

							cc = constraint.charAt(k);
							if (cc == '/') {
								hasEndingSlash = true;
								break; //ending / found
							}
							if (cc == '\\')
								++k; //skip one
						}
						if (hasEndingSlash) {
							String restCst = constraint.substring(k + 1);
							Pattern pattern = Pattern.compile(".*?(?=,|:|$)");
							final Matcher matcher = pattern.matcher(restCst);
							if (matcher.find()) {
								regexFlags = matcher.group(0).trim();
							}
						}
						regex = k >= 0 ? constraint.substring(j, k) : constraint.substring(j);
						if (regexFlags.length() > 0)
							k += regexFlags.length(); // skip regex flags
						continue l_out;
					case ':':
						errmsg = constraint.substring(j + 1).trim();
						break l_out; //done
				}
				if (!Character.isWhitespace(cc))
					break;
			}

			String s;
			for (k = j; ; ++k) {
				if (k >= len) {
					s = constraint.substring(j);
					k = -1;
					break;
				}
				final char cc = constraint.charAt(k);
				if (cc == ',' || cc == ':' || cc == ';' || cc == '/') {
					s = constraint.substring(j, k);
					if (cc == ':' || cc == '/')
						--k;
					break;
				}
			}

			_flags |= parseConstraint(s.trim().toLowerCase(java.util.Locale.ENGLISH));
		}
		_regex = getRegex(regex, regexFlags);
		_errmsg = errmsg == null || errmsg.length() == 0 ? null : errmsg;
	}

	private Pattern getRegex(String regex, String regexFlags) {
		if (regex == null || regex.length() == 0)
			return null;
		if (regexFlags.length() == 0) {
			return Pattern.compile(regex);
		} else { // ZK-4863: add pattern flags
			int regexFlag = 0;
			if (regexFlags.contains("i"))
				regexFlag |= Pattern.CASE_INSENSITIVE;
			if (regexFlags.contains("m"))
				regexFlag |= Pattern.MULTILINE;
			if (regexFlags.contains("s"))
				regexFlag |= Pattern.DOTALL;
			if (regexFlags.contains("u"))
				regexFlag |= Pattern.UNICODE_CASE;

			return Pattern.compile(regex, regexFlag);
		}
	}

	/** Parses a list of constraints from a string to an integer
	 * representing a combination of {@link #NO_POSITIVE} and other flags.
	 *
	 * @param constraint a list of constraints separated by comma.
	 * Example: no positive, no zero
	 */
	public static SimpleConstraint getInstance(String constraint) {
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
		else if (constraint.equals("server"))
			return SERVER;
		else if (constraint.equals("before_start"))
			return BEFORE_START;
		else if (constraint.equals("before_end"))
			return BEFORE_END;
		else if (constraint.equals("end_before"))
			return END_BEFORE;
		else if (constraint.equals("end_after"))
			return END_AFTER;
		else if (constraint.equals("after_end"))
			return AFTER_END;
		else if (constraint.equals("after_start"))
			return AFTER_START;
		else if (constraint.equals("start_after"))
			return START_AFTER;
		else if (constraint.equals("start_before"))
			return START_BEFORE;
		else if (constraint.equals("overlap"))
			return OVERLAP;
		else if (constraint.equals("overlap_end"))
			return OVERLAP_END;
		else if (constraint.equals("overlap_before"))
			return OVERLAP_BEFORE;
		else if (constraint.equals("overlap_after"))
			return OVERLAP_AFTER;
		else if (constraint.equals("at_pointer"))
			return AT_POINTER;
		else if (constraint.equals("after_pointer"))
			return AFTER_POINTER;
		else if (constraint.length() > 0)
			throw new UiException("Unknown constraint: " + constraint);
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
	public void validate(Component comp, Object value) throws WrongValueException {
		if (!_finishParseCst) {
			if (_raw != null) parseCst(_raw);
			_finishParseCst = true;
		}
		if (value == null) {
			if ((_flags & NO_EMPTY) != 0)
				throw wrongValue(comp, MZul.EMPTY_NOT_ALLOWED);
		} else if (value instanceof Number) {
			if ((_flags & (NO_POSITIVE | NO_NEGATIVE | NO_ZERO)) == 0)
				return; //nothing to check

			final int cmp = compareTo((Comparable) value, Classes.coerce(value.getClass(), null, false)); //compare to zero
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
			final String s = (String) value;
			if ((_flags & NO_EMPTY) != 0 && s.length() == 0)
				throw wrongValue(comp, MZul.EMPTY_NOT_ALLOWED);
			if (_regex != null && !_regex.matcher(s != null ? s : "").matches())
				throw wrongValue(comp, MZul.ILLEGAL_VALUE);
			if ((_flags & STRICT) != 0) {
				if (s.length() > 0 && comp instanceof Combobox) {
					for (Iterator it = ((Combobox) comp).getItems().iterator(); it.hasNext();) {
						final Comboitem ci = (Comboitem) it.next();
						if (!ci.isDisabled() && ci.isVisible() && s.equalsIgnoreCase(ci.getLabel()))
							return;
					}
					throw wrongValue(comp, MZul.VALUE_NOT_MATCHED);
				}
			}
		} else if (value instanceof Date) {
			if ((_flags & (NO_FUTURE | NO_PAST | NO_TODAY)) == 0)
				return;
			final Date date = Dates.beginOfDate((Date) value, null);
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

	@SuppressWarnings("unchecked")
	private static int compareTo(Comparable v1, Object v2) {
		return v1.compareTo(v2);
	}

	private WrongValueException wrongValue(Component comp, int errcode) {
		return _errmsg != null ? new WrongValueException(comp, _errmsg) : new WrongValueException(comp, errcode);
	}

	private int getMessageForNumberDenied() {
		switch (_flags & (NO_POSITIVE | NO_NEGATIVE | NO_ZERO)) {
		case (NO_POSITIVE | NO_ZERO):
			return MZul.NO_POSITIVE_ZERO;
		case (NO_POSITIVE):
			return MZul.NO_POSITIVE;
		case (NO_NEGATIVE | NO_ZERO):
			return MZul.NO_NEGATIVE_ZERO;
		case (NO_NEGATIVE):
			return MZul.NO_NEGATIVE;
		case (NO_ZERO):
			return MZul.NO_ZERO;
		case (NO_POSITIVE | NO_NEGATIVE | NO_ZERO):
			return MZul.NO_POSITIVE_NEGATIVE_ZERO;
		case (NO_POSITIVE | NO_NEGATIVE):
			return MZul.NO_POSITIVE_NEGATIVE;
		}
		throw new InternalError();
	}

	private int getMessageForDateDenied() {
		switch (_flags & (NO_FUTURE | NO_PAST | NO_TODAY)) {
		case (NO_FUTURE | NO_TODAY):
			return MZul.NO_FUTURE_TODAY;
		case (NO_FUTURE):
			return MZul.NO_FUTURE;
		case (NO_PAST | NO_TODAY):
			return MZul.NO_PAST_TODAY;
		case (NO_PAST):
			return MZul.NO_PAST;
		case (NO_TODAY):
			return MZul.NO_TODAY;
		case (NO_FUTURE | NO_PAST | NO_TODAY):
			return MZul.NO_FUTURE_PAST_TODAY;
		case (NO_FUTURE | NO_PAST):
			return MZul.NO_FUTURE_PAST;
		}
		throw new InternalError();
	}

	//ClientConstraint//
	public String getClientConstraint() {
		if (_raw != null)
			return '\'' + Strings.escape(_raw, Strings.ESCAPE_JAVASCRIPT) + '\'';

		final StringBuffer sb = new StringBuffer("new zul.inp.SimpleConstraint(");
		if (_flags != 0 || _regex != null || _errmsg != null) {
			sb.append(_flags);
			if (_regex != null || _errmsg != null) {
				sb.append(',');
				if (_regex != null) {
					sb.append('\'');
					Strings.escape(sb, _regex.pattern(), Strings.ESCAPE_JAVASCRIPT);
					sb.append('\'');
				} else
					sb.append("null");
				if (_errmsg != null) {
					sb.append(",'");
					Strings.escape(sb, _errmsg, Strings.ESCAPE_JAVASCRIPT);
					sb.append('\'');
				}
			}
		}
		return sb.append(')').toString();
	}

	/** Default: null (since it depends on zul.inp which is loaded for
	* all input widgets).
	 */
	public String getClientPackages() {
		return null;
	}

	/**
	 * Returns the raw value
	 * @since 10.0.0
	 */
	public String getRawValue() {
		return _raw;
	}
}
