/* Timebox.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.Date;
import java.util.TimeZone;
import org.zkoss.util.TimeZones;//for javadoc
import org.zkoss.zk.ui.WrongValueException;

/**
 * An input box for holding a time (a java.util.Date Object , but only Hour &
 * Minute are used.
 * 
 * <p>
 * Default {@link #getZclass}: z-timebox. (since 3.5.0)
 * 
 * <p>
 * timebox doens't support customized format. It support HH:mm formate, where HH
 * is hour of day and mm is minute of hour.
 * 
 * <p>
 * timebox supports below key events. <lu>
 * <li>0-9 : set the time digit depend on the position on the inner text box.
 * <li>up : increase time digit depend on the position on the inner text box.
 * <li>down : decrease time digit depend on the position on the inner text box.
 * <li>delete : clear the time to empty (null) </lu>
 * 
 * <p>
 * Like {@link Combobox} and {@link Datebox}, the value of a read-only time box
 * ({@link #isReadonly}) can be changed by clicking the up or down button
 * (though users cannot type anything in the input box).
 * 
 * @author Dennis Chen
 * @since 3.5.2
 */
public interface Timebox extends org.zkoss.zul.impl.api.InputElement {

	/**
	 * Returns the value (in Date), might be null unless a constraint stops it.
	 * And, only Hour and Mintue field is effective.
	 * 
	 * @exception WrongValueException
	 *                if user entered a wrong value
	 */
	public Date getValue() throws WrongValueException;

	/**
	 * Sets the value (in Date). If value is null, then an empty will be
	 * sent(render) to client. If else, only the Hour and Mintue field will be
	 * sent(render) to client.
	 * 
	 * @exception WrongValueException
	 *                if value is wrong
	 */
	public void setValue(Date value) throws WrongValueException;

	/**
	 * Returns whether the button (on the right of the textbox) is visible.
	 * <p>
	 * Default: true.
	 */
	public boolean isButtonVisible();

	/**
	 * Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible);

	/**
	 * Returns the time zone that this time box belongs to, or null if the
	 * default time zone is used.
	 * <p>
	 * The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public TimeZone getTimeZone();

	/**
	 * Sets the time zone that this time box belongs to, or null if the default
	 * time zone is used.
	 * <p>
	 * The default time zone is determined by {@link TimeZones#getCurrent}.
	 */
	public void setTimeZone(TimeZone tzone);
}
