/* TimeZones.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 12 12:17:03     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.TimeZone;

/**
 * Utilities to access time-zone.
 *
 * @author tomyeh
 */
public class TimeZones {
	private static final
		InheritableThreadLocal _thdTZone = new InheritableThreadLocal();
	/** Returns the current time zone; never null.
	 * This is the time zone that every other objects shall use,
	 * unless they have special consideration.
	 *
	 * <p>Default: If {@link #setThreadLocal} was called with non-null,
	 * the value is returned. Otherwise, TimeZone.getDefault() is returned,
	 */
	public static final TimeZone getCurrent() {
		final TimeZone l = (TimeZone)_thdTZone.get();
		return l != null ? l: TimeZone.getDefault();
	}
	/**
	 * Sets the time-zone for the current thread only.
	 *
	 * <p>Each thread could have an independent time zone, called
	 * the thread time zone.
	 *
	 * <p>When Invoking this method under a thread that serves requests,
	 * remember to clean up the setting upon completing each request.
	 *
	 * <pre><code>TimeZone old = TimeZones.setThreadLocal(newValue);
	 *try { 
	 *  ...
	 *} finally {
	 *  TimeZones.setThreadLocal(old);
	 *}</code></pre>
	 *
	 * @param timezone the thread time zone; null to denote no thread time zone
	 * (and the system's timezone will be used instead)
	 * @return the previous thread time zone, or null if no previous time zone
	 */
	public static final TimeZone setThreadLocal(TimeZone timezone) {
		final TimeZone old = (TimeZone)_thdTZone.get();
		_thdTZone.set(timezone);
		return old;
	}
	/**
	 * Returns the time zone defined by {@link #setThreadLocal}.
	 *
	 * @since 3.0.0
	 * @see #getCurrent
	 */
	public static final TimeZone getThreadLocal() {
		return (TimeZone)_thdTZone.get();
	}

	/** Returns the time by specifying the offset in minutes.
	 *
	 * <p>For example, the following are equivalent.
	 *<pre><code>
	 *TimeZone.getTimeZone("GMT+8");
	 *TimeZones.getTimeZone(480);
	 *</code></pre>
	 *
	 * @param ofsmins the offset in minutes.
	 */
	public static final TimeZone getTimeZone(int ofsmins) {
		final StringBuffer sb = new StringBuffer(8).append("GMT");
		if (ofsmins >= 0) {
			sb.append('+');
		} else {
			sb.append('-');
			ofsmins = -ofsmins;
		}
		final int hr = ofsmins / 60, min = ofsmins % 60;
		if (min == 0) {
			sb.append(hr);
		} else {
			if (hr < 10) sb.append('0');
			sb.append(hr).append(':');
			if (min < 10) sb.append('0');
			sb.append(min);
		}
		return TimeZone.getTimeZone(sb.toString());
	}
}
