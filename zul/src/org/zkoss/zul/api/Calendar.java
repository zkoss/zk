/* Calendar.java

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
import org.zkoss.util.TimeZones; //for javadoc

/**
 * A calendar.
 * 
 * <p>
 * Default {@link #getZclass}: z-calendar. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Calendar extends org.zkoss.zul.impl.api.XulElement {
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
	 * Returns the value that is assigned to this component, never null.
	 */
	public Date getValue();

	/**
	 * Assigns a value to this component.
	 * 
	 * @param value
	 *            the date to assign. If null, today is assumed.
	 */
	public void setValue(Date value);

	/**
	 * Returns the name of this component.
	 * <p>
	 * Default: null.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 * 
	 */
	public String getName();

	/**
	 * Sets the name of this component.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 * 
	 * @param name
	 *            the name of this component.
	 */
	public void setName(String name);

}
