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

import org.zkoss.lang.Objects;
import org.zkoss.util.Dates;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.*;

import org.zkoss.zul.impl.XulElement;

/**
 * A calendar.
 *
 * <p>Default {@link #getZclass}: z-calendar. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Calendar extends XulElement implements org.zkoss.zul.api.Calendar {
	private TimeZone _tzone;
	private Date _value;
	/** The name. */
	private String _name;
	private boolean _compact;

	/** Contructs a calendar whose value is default to today.
	 */
	public Calendar() {
		this(null);
	}
	public Calendar(Date value) {
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
			smartUpdate("z.value", getDateFormat().format(_value));
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

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * @since 3.0.0
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this component.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 *
	 * @param name the name of this component.
	 * @since 3.0.0
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("z.name", _name);
		}
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-calendar" : _zclass;
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onChange, onChanging and onError.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String name = request.getName();
		if (name.equals(Events.ON_CHANGE)) {
			InputEvent evt = InputEvent.getInputEvent(request);

			final String value = evt.getValue();
			try {
				final Date newval = getDateFormat().parse(value);
				if (Objects.equals(_value, newval))
					return; //not post event
				_value = newval;
			} catch (ParseException ex) {
				throw new InternalError(value);
			}

			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
