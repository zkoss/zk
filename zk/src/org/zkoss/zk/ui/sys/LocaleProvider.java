/* LocaleProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 13 15:43:35     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Locale;
import org.zkoss.zk.ui.Session;

/**
 * Provides the locale for the specified session.
 * Once specified (by calling
 * {@link org.zkoss.zk.ui.util.Configuration#setLocaleProviderClass}),
 * one instance of the specified class is created for each Web application
 * to serve all requests.
 *
 * <p>Each time a request is received, {@link #getLocale} is called
 * against the instance returned by
 * {@link org.zkoss.zk.ui.util.Configuration#getLocaleProvider}.
 *
 * @author tomyeh
 */
public interface LocaleProvider {
	/** Returns the locale of the specified session, or null if the default
	 * shall be used.
	 *
	 * <p>The default is determined by browser's preference, if any.
	 *
	 * @param sess the session
	 * @param request the request, depending on the protocol.
	 * For exampple, it is javax.servlet.http.HttpServletRequest if HTTP.
	 * @param response the response, depending on the protocol.
	 * For exampple, it is javax.servlet.http.HttpServletResponse if HTTP.
	 * @since 2.3.2
	 */
	public Locale getLocale(Session sess, Object request, Object response);
}
