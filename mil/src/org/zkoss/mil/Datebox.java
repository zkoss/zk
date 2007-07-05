/* Datebox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 5, 2007 11:57:36 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import java.util.Date;
import java.util.TimeZone;

import org.zkoss.lang.Objects;
import org.zkoss.util.TimeZones;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.client.Inputable;

/**
 * Datebox for date, time, and date time entry.
 * @author henrichen
 *
 */
public class Datebox extends Item {
	private static final long serialVersionUID = 200707051515L;
	private static final int DATE = 1;
	private static final int TIME = 2;
	private static final int DATE_TIME = 3;
	
	private Date _value = new Date();
	private TimeZone _tzone;
	
	public Datebox() {
	}
	public Datebox(Date value) throws WrongValueException {
		setValue(value);
	}
	
	public void setValue(Date value) {
		if (!Objects.equals(_value, value)) {
			_value = value;
			smartUpdate("dt", "" + (value.getTime() + getTzoneOffset()));
		}
	}
	
	public Date getValue() {
		return _value;
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
	
	protected int getTzoneOffset() {
		final TimeZone tz = _tzone != null ? _tzone: TimeZones.getCurrent();
		return tz.getRawOffset();
	}

	protected int getInputMode() {
		String mold = getMold();
		if ("default".equals(mold)) {
			return DATE;
		} else if ("time".equals(mold)) {
			return TIME;
		} else if ("date_time".equals(mold)) {
			return DATE_TIME;
		}
		
		//default to DATE mode
		return DATE;
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =	new StringBuffer(64).append(super.getOuterAttrs());
		appendAsapAttr(sb, "onChange");
		return sb.toString();
	}

	public String getInnerAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super.getInnerAttrs());
		if (_value != null) {
			HTMLs.appendAttribute(sb, "dt",  "" + (_value.getTime() + getTzoneOffset())); //text
		}
		HTMLs.appendAttribute(sb, "md",  getInputMode()); //InputMode
		final TimeZone tz = _tzone != null ? _tzone: TimeZones.getCurrent();
		HTMLs.appendAttribute(sb, "tz", tz.getID()); //TimeZone
		return sb.toString();
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl implements Inputable {
		//-- Inputable --//
		public void setTextByClient(String value) throws WrongValueException {
			_value = new Date(Long.parseLong(value) - getTzoneOffset());
		}
	}
}
