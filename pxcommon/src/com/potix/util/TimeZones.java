/* TimeZones.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 12 12:17:03     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.util;

import java.util.TimeZone;

/**
 * Utilities to access time-zone.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class TimeZones {
	private static final
		InheritableThreadLocal _thdTZone = new InheritableThreadLocal();
	/** Returns the current locale; never null.
	 * This is the locale that every other objects shall use,
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
	 * <p>Each thread could have an independent locale, called
	 * the thread locale.
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
	 * @param locale the thread locale; null to denote no thread locale
	 * @return the previous thread locale
	 */
	public static final TimeZone setThreadLocal(TimeZone locale) {
		final TimeZone old = (TimeZone)_thdTZone.get();
		_thdTZone.set(locale);
		return old;
	}
}
