/* Datebox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.Date;
import java.util.TimeZone;
import org.zkoss.util.TimeZones;//for javadoc
import org.zkoss.zk.ui.WrongValueException;

/**
 * An edit box for holding a date.
 * 
 * <p>
 * The default format ({@link #getFormat}) depends on JVM's setting and the
 * current user's locale. That is,
 * <code>DateFormat.getDateInstance(DateFormat,DEFAULT, Locales.getCurrent).</code>
 * You might override {@link org.zkoss.zul.Datebox#getDefaultFormat} to provide
 * your own default format.
 * <p>
 * Default {@link #getZclass}: z-datebox.(since 3.5.0)
 * 
 * @since 3.5.2
 * @author tomyeh
 */
public interface Datebox extends org.zkoss.zul.impl.api.FormatInputElement {

	/**
	 * Returns whether or not date/time parsing is to be lenient.
	 * 
	 * <p>
	 * With lenient parsing, the parser may use heuristics to interpret inputs
	 * that do not precisely match this object's format. With strict parsing,
	 * inputs must match this object's format.
	 */
	public boolean isLenient();

	/**
	 * Returns whether or not date/time parsing is to be lenient.
	 * <p>
	 * Default: true.
	 * 
	 * <p>
	 * With lenient parsing, the parser may use heuristics to interpret inputs
	 * that do not precisely match this object's format. With strict parsing,
	 * inputs must match this object's format.
	 */
	public void setLenient(boolean lenient);

	/**
	 * Returns whether to use a compact layout.
	 * <p>
	 * Default: true if zh_TW or zh_CN; false otherwise.
	 */
	public boolean isCompact();

	/**
	 * Sets whether to use a compact layout.
	 */
	public void setCompact(boolean compact);

	/**
	 * Returns whether the button (on the right of the textbox) is visible.
	 * <p>
	 * Default: true.
	 * 
	 */
	public boolean isButtonVisible();

	/**
	 * Sets whether the button (on the right of the textbox) is visible.
	 * 
	 */
	public void setButtonVisible(boolean visible);

	/**
	 * Returns the value (in Date), might be null unless a constraint stops it.
	 * 
	 * @exception WrongValueException
	 *                if user entered a wrong value
	 */
	public Date getValue() throws WrongValueException;

	/**
	 * Sets the value (in Date).
	 * 
	 * @exception WrongValueException
	 *                if value is wrong
	 */
	public void setValue(Date value) throws WrongValueException;

	/**
	 * Returns the time zone that this date box belongs to, or null if the
	 * default time zone is used.
	 * <p>
	 * The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public TimeZone getTimeZone();

	/**
	 * Sets the time zone that this date box belongs to, or null if the default
	 * time zone is used.
	 * <p>
	 * The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public void setTimeZone(TimeZone tzone);

	/**
	 * Drops down or closes the calendar to select a date.
	 * 
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open);

	/**
	 * Drops down the calendar to select a date. The same as setOpen(true).
	 * 
	 */
	public void open();

	/**
	 * Closes the calendar if it was dropped down. The same as setOpen(false).
	 * 
	 */
	public void close();
}
