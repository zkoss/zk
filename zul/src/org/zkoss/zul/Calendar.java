/* Calendar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 24 17:12:27     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.zkoss.util.Dates;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.ext.client.Inputable;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.impl.XulElement;

/**
 * A calendar.
 *
 * <p>Default {@link #getSclass}: calendar.
 *
 * @author tomyeh
 */
public class Calendar extends XulElement {
	private TimeZone _tzone;
	private Date _value;
	private boolean _compact;

	/** Contructs a calendar whose value is default to today.
	 */
	public Calendar() {
		this(null);
	}
	public Calendar(Date value) {
		setSclass("calendar");
		_value = value != null ? value: Dates.today();
		_compact = "zh".equals(Locales.getCurrent().getLanguage());
	}

	/** Returns the time zone that this date box belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public TimeZone getTimeZone() {
		return _tzone;
	}
	/** Sets the time zone that this date box belongs to, or null if
	 * the default time zone is used.
	 * <p>The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public void setTimeZone(TimeZone tzone) {
		_tzone = tzone;
	}

	/** Returns the value that is assigned to this component, never null.
	 */
	public Date getValue() {
		return _value;
	}
	/** Assigns a value to this component.
	 * @param value the date to assign. If null, today is assumed.
	 */
	public void setValue(Date value) {
		if (value == null) value = Dates.today();
		if (!value.equals(_value)) {
			_value = value;
			smartUpdate("z:value", getDateFormat().format(_value));
		}
	}

	private final DateFormat getDateFormat() {
		final DateFormat df =
			new SimpleDateFormat("yyyy/MM/dd", Locales.getCurrent());
		final TimeZone tz = _tzone != null ? _tzone: TimeZones.getCurrent();
		df.setTimeZone(tz);
		return df;
	}

	/** Returns whether to use a compact layout.
	 * <p>Default: true if zh_TW or zh_CN; false otherwise.
	 */
	public boolean isCompact() {
		return _compact;
	}
	/** Sets whether to use a compact layout.
	 */
	public void setCompact(boolean compact) {
		if (_compact != compact) {
			_compact = compact;
			invalidate();
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());

		appendAsapAttr(sb, Events.ON_CHANGE);

		HTMLs.appendAttribute(sb, "z:value", getDateFormat().format(_value));
		if (_compact) sb.append(" z:compact=\"true\"");
		return sb.toString();
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Inputable {
		//-- Inputable --//
		public void setTextByClient(String value) throws WrongValueException {
			try {
				_value = getDateFormat().parse(value);
			} catch (ParseException ex) {
				throw new InternalError(value);
			}
		}
	}
}
