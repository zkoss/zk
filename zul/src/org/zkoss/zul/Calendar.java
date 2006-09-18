/* Calendar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 24 17:12:27     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.potix.util.Dates;
import com.potix.util.Locales;
import com.potix.util.TimeZones;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.ext.Inputable;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.event.Events;

import com.potix.zul.html.impl.XulElement;

/**
 * A calendar.
 *
 * <p>Default {@link #getSclass}: calendar.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Calendar extends XulElement implements Inputable {
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
			smartUpdate("zk_value", getDateFormat().format(_value));
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

	//-- Inputable --//
	public void setTextByClient(String value) throws WrongValueException {
		try {
			_value = getDateFormat().parse(value);
		} catch (ParseException ex) {
			throw new InternalError(value);
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());

		appendAsapAttr(sb, Events.ON_CHANGE);

		HTMLs.appendAttribute(sb, "zk_value", getDateFormat().format(_value));
		if (_compact) sb.append(" zk_compact=\"true\"");
		return sb.toString();
	}
}
