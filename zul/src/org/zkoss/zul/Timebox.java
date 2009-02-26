/* Timebox.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 	Jul 9, 2007 10:03:38 AM , Created by Dennis Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zul;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.zkoss.lang.Objects;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.mesg.MZul;

/**
 * An input box for holding a time (a java.util.Date Object , but only Hour & Minute are used.
 *
 * <p>Default {@link #getZclass}: z-timebox. (since 3.5.0)
 *
 * <p>timebox doens't support customized format. It support HH:mm formate, where HH is hour of day and mm is minute of hour.
 * 
 * <p>timebox supports below key events.
 * <lu>
 *  <li>0-9 : set the time digit depend on the position on the inner text box.
 * 	<li>up : increase time digit depend on the position on the inner text box.
 *  <li>down : decrease time digit depend on the position on the inner text box.
 * 	<li>delete : clear the time to empty (null)
 * </lu>
 *
 * <p>Like {@link Combobox} and {@link Datebox},
 * the value of a read-only time box ({@link #isReadonly}) can be changed
 * by clicking the up or down button (though users cannot type anything
 * in the input box).
 *
 * @author Dennis Chen
 * @since 3.0.0
 */
public class Timebox extends InputElement implements org.zkoss.zul.api.Timebox {
	private TimeZone _tzone;
	private boolean _btnVisible = true;
	
	public Timebox() {
		setCols(5);
		setMaxlength(5);
	}
	public Timebox(Date date) throws WrongValueException {
		this();
		setValue(date);
	}

	/** Returns the value (in Date), might be null unless
	 *  a constraint stops it. And, only Hour and Mintue field is effective.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Date getValue() throws WrongValueException {
		return (Date)getTargetValue();
	}
	/** Sets the value (in Date).
	 * If value is null, then an empty will be sent(render) to client.
	 * If else, only the Hour and Mintue field will be sent(render) to client. 
	 * 
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Date value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}
	
	/** Returns whether the button (on the right of the textbox) is visible.
	 * <p>Default: true.
	 */
	public boolean isButtonVisible() {
		return _btnVisible;
	}
	/** Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible) {
		if (_btnVisible != visible) {
			_btnVisible = visible;
			smartUpdate("z.btnVisi", visible);
		}
	}
	/** Returns the URI of the button image.
	 * <p>Default: null. (since 3.5.0)
	 * @deprecated As of release 3.5.0
	 */
	public String getImage() {
		return null;
	}
	/** Sets the URI of the button image.
	 *
	 * @param img the URI of the button image.
	 * @deprecated As of release 3.5.0
	 */
	public void setImage(String img) {
	}
	
	/** Returns the time zone that this time box belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public TimeZone getTimeZone() {
		return _tzone;
	}
	/** Sets the time zone that this time box belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public void setTimeZone(TimeZone tzone) {
		_tzone = tzone;
	}
	
	protected Object coerceFromString(String value) throws WrongValueException {
		//null or empty string,
		if (value == null || value.length() == 0){
			return null;
		}

		final DateFormat df = getDateFormat();
		final Date date;
		try {
			date = df.parse(value);
		} catch (ParseException ex) {
			throw showCustomError(
				new WrongValueException(this, MZul.DATE_REQUIRED,
					new Object[] {value, "HH:mm"}));
		}
		return date;
	}
	protected String coerceToString(Object value) {
		if(value==null) return "";
		final DateFormat df = getDateFormat();
		return value != null ? df.format((Date)value): "";
	}
	
	/** Returns the date format of the time only,
	 *
	 * <p>Default: it uses SimpleDateFormat to format the date.
	 */
	protected DateFormat getDateFormat() {
		String fmt = "HH:mm";
		final DateFormat df = new SimpleDateFormat(fmt, Locales.getCurrent());
		final TimeZone tz = _tzone != null ? _tzone: TimeZones.getCurrent();
		df.setTimeZone(tz);
		return df;
	}

	// super
	public String getZclass() {
		return _zclass == null ?  "z-timebox" : _zclass;
	}
}
