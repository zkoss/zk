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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.zkoss.json.JavaScriptValue;
import org.zkoss.lang.Objects;
import org.zkoss.mesg.Messages;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.ext.Constrainted;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.mesg.MZul;

/**
 * A calendar.
 *
 * <p>Default {@link #getZclass}: z-calendar. (since 3.5.0)
 *
 * <h3>Support display the week number within the current year</h3>
 * Events: onWeekClick
 * <p>
 * For example
 * <pre><code>
 * &lt;calendar weekOfYear=&quot;true&quot; onWeekClick='alert(event.data)'/&gt;
 * </code></pre>
 * [ZK EE]
 * [Since 6.5.0]
 * 
 * @author tomyeh
 */
public class Calendar extends XulElement implements Constrainted {
	private ZonedDateTime _value;
	private TimeZone _defaultTzone = TimeZones.getCurrent();
	private boolean _weekOfYear;
	private boolean _showTodayLink = false;
	private String _todayLinkLabel = Messages.get(MZul.CALENDAR_TODAY);
	private SimpleDateConstraint _constraint;

	/** The name. */
	private String _name;

	/** Constructs a calendar whose value is default to today.
	 */
	static {
		addClientEvent(Calendar.class, Events.ON_CHANGE, CE_IMPORTANT | CE_REPEAT_IGNORE);
		addClientEvent(Calendar.class, Events.ON_WEEK_CLICK, CE_REPEAT_IGNORE);
	}

	public Calendar() {
		this((ZonedDateTime) null);
	}

	public Calendar(Date value) {
		this(value == null ? null : ZonedDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()));
	}

	public Calendar(ZonedDateTime value) {
		_value = value != null ? value : ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
	}

	public Calendar(LocalDateTime value) {
		this(value == null ? null : value.atZone(ZoneId.systemDefault()));
	}

	public Calendar(LocalDate value) {
		this(value == null ? null : value.atStartOfDay(ZoneId.systemDefault()));
	}

	/**
	 * Sets whether enable to show the week number within the current year or
	 * not.
	 * [ZK EE]
	 * @since 6.5.0
	 */
	public void setWeekOfYear(boolean weekOfYear) {
		if (_weekOfYear != weekOfYear) {
			_weekOfYear = weekOfYear;
			smartUpdate("weekOfYear", _weekOfYear);
		}
	}

	/**
	 * Returns whether enable to show the week number within the current year or not.
	 * <p>Default: false
	 * @since 6.5.0
	 */
	public boolean isWeekOfYear() {
		return _weekOfYear;
	}

	/** @deprecated As of release 5.0.5, it is meaningless to set time zone for a calendar.
	 */
	public TimeZone getTimeZone() {
		return null;
	}

	/** As of release 5.0.5, it is meaningless to set time zone for a calendar.
	 */
	public void setTimeZone(TimeZone tzone) {
	}

	/** Returns the value that is assigned to this component, never null.
	 */
	public Date getValue() {
		return Date.from(_value.toInstant());
	}

	/** Assigns a value to this component.
	 * @param value the date to assign. If null, today is assumed.
	 */
	public void setValue(Date value) {
		setValueInZonedDateTime(value == null ? null : ZonedDateTime.ofInstant(value.toInstant(), _value.getZone()));
	}

	/**
	 * Returns the value (in ZonedDateTime) that is assigned to this component, never null.
	 * @since 9.0.0
	 */
	public ZonedDateTime getValueInZonedDateTime() {
		return _value;
	}

	/**
	 * Assigns a value (in ZonedDateTime) to this component.
	 * @param value the date to assign. If null, today is assumed.
	 * @since 9.0.0
	 */
	public void setValueInZonedDateTime(ZonedDateTime value) {
		if (value == null)
			value = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
		if (!value.equals(_value)) {
			_value = value;
			smartUpdate("value", getValue());
		}
	}

	/**
	 * Returns the value (in LocalDateTime) that is assigned to this component, never null.
	 * @since 9.0.0
	 */
	public LocalDateTime getValueInLocalDateTime() {
		return getValueInZonedDateTime().toLocalDateTime();
	}

	/**
	 * Assigns a value (in LocalDateTime) to this component.
	 * @param value the date to assign. If null, today is assumed.
	 * @since 9.0.0
	 */
	public void setValueInLocalDateTime(LocalDateTime value) {
		setValueInZonedDateTime(value == null ? null : value.atZone(_value.getZone()));
	}

	/**
	 * Returns the value (in LocalDate) that is assigned to this component, never null.
	 * @since 9.0.0
	 */
	public LocalDate getValueInLocalDate() {
		return getValueInZonedDateTime().toLocalDate();
	}

	/**
	 * Assigns a value (in LocalDate) to this component.
	 * @param value the date to assign. If null, today is assumed.
	 * @since 9.0.0
	 */
	public void setValueInLocalDate(LocalDate value) {
		setValueInZonedDateTime(value == null ? null : value.atStartOfDay(_value.getZone()));
	}

	/**
	 * Returns the value (in LocalTime) that is assigned to this component, never null.
	 * @since 9.0.0
	 */
	public LocalTime getValueInLocalTime() {
		return getValueInZonedDateTime().toLocalTime();
	}

	/**
	 * It is meaningless to set only LocalTime in calendar.
	 * @since 9.0.0
	 */
	public void setValueInLocalTime(LocalTime value) throws WrongValueException {
		throw new UnsupportedOperationException("need date");
	}

	private DateFormat getDateFormat() {
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locales.getCurrent());
		df.setTimeZone(TimeZones.getCurrent());
		return df;
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
		if (name != null && name.length() == 0)
			name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", _name);
		}
	}

	/**
	 * Returns whether enable to show the link that jump to today in day view
	 * <p>Default: false
	 * @since 8.0.0
	 * @return boolean
	 */
	public boolean getShowTodayLink() {
		return _showTodayLink;
	}

	/**
	 * Sets whether enable to show the link that jump to today in day view
	 * @param showTodayLink show or hidden
	 * @since 8.0.0
	 */
	public void setShowTodayLink(boolean showTodayLink) {
		if (_showTodayLink != showTodayLink) {
			_showTodayLink = showTodayLink;
			smartUpdate("showTodayLink", _showTodayLink);
		}
	}

	/**
	 * Returns the label of the link that jump to today in day view
	 * <p>Default: Today
	 * @since 8.0.4
	 * @return String
	 */
	public String getTodayLinkLabel() {
		return _todayLinkLabel;
	}

	/**
	 * Sets the label of the link that jump to today in day view
	 * @param todayLinkLabel today link label
	 * @since 8.0.4
	 */
	public void setTodayLinkLabel(String todayLinkLabel) {
		if (!Objects.equals(_todayLinkLabel, todayLinkLabel)) {
			_todayLinkLabel = todayLinkLabel;
			smartUpdate("todayLinkLabel", todayLinkLabel);
		}
	}
	
	/**
	 * Sets a list of constraints separated by comma.
	 * Example: "between 20071012 and 20071223", "before 20080103".
	 * @param constr a list of constraints separated by comma.
	 */
	public void setConstraint(String constr) {
		if (constr != null) {
			setConstraint(new SimpleDateConstraint(constr));
		}
	}
	
	public void setConstraint(Constraint constr) {
		if (!Objects.equals(_constraint, constr)) {
			_constraint = (SimpleDateConstraint) constr;
			smartUpdate("constraint", new JavaScriptValue(_constraint.getClientConstraint()));
		}
	}
	
	public Constraint getConstraint() {
		return _constraint;
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
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHANGE)) {
			final Map<String, Object> data = request.getData();
			final Date value = (Date) data.get("value");
			final ZonedDateTime zonedValue = ZonedDateTime.ofInstant(value.toInstant(), _value.getZone());
			if (Objects.equals(_value, zonedValue))
				return; //nothing happen

			_value = zonedValue;
			final InputEvent evt = new InputEvent(cmd, this, getDateFormat().format(value), value,
					AuRequests.getBoolean(data, "bySelectBack"), AuRequests.getInt(data, "start", 0));
			Events.postEvent(evt);
		} else {
			super.service(request, everError);
		}
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		if (_name != null)
			render(renderer, "name", _name);
		render(renderer, "defaultTzone", _defaultTzone.getID());
		render(renderer, "weekOfYear", _weekOfYear);
		render(renderer, "value", getValue());
		render(renderer, "showTodayLink", _showTodayLink);
		render(renderer, "todayLinkLabel", _todayLinkLabel);
		if (_constraint != null) {
			render(renderer, "constraint", new JavaScriptValue(_constraint.getClientConstraint()));
		}
	}
}
