/* PreferenceManager.java

{{IS_NOTE
	$Id: PreferenceManager.java,v 1.3 2006/02/27 03:42:05 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sun Jan  1 18:30:59     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.prefs.impl;

import java.util.Locale;
import java.util.TimeZone;

/**
 * A preference manager which manages the preference of the current user.
 * By default, there is one set of preferences for the whole system.
 * If you have the user management system, you could change the
 * preference manager by use of {@link com.potix.util.prefs.Apps#setPreferenceManager}.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/02/27 03:42:05 $
 */
public interface PreferenceManager {
	/** Returns the current locale; never null.
	 */
	public Locale getCurrentLocale();
	/** Returns the current time zone; never null.
	 */
	public TimeZone getCurrentTimeZone();

	/** Returns the current doamin; never null.
	 */
	public String getCurrentDomain();
	/** Returns the current user name, or null if no one logins
	 * the current execution.
	 */
	public String getCurrentUsername();
	/** Returns the current theme.
	 */
	public String getCurrentTheme();
	/**
	 * Sets the thread locale.
	 *
	 * @param locale the thread locale; null to denote no thread locale
	 * @return the previous thread locale
	 */
	public Locale setThreadLocale(Locale locale);
	/** Keeps the current login session, if any, alive (prevents it from timeout).
	 */
	public void keepAlive();
}
