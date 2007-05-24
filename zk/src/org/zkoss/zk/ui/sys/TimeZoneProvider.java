/* TimeZoneProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 13 15:48:32     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.TimeZone;
import org.zkoss.zk.ui.Session;

/**
 * Provides the time zone for the specified session.
 * Once specified (by calling
 * {@link org.zkoss.zk.ui.util.Configuration#setTimeZoneProviderClass}),
 * one instance of the specified class is created for each Web application
 * to serve all requests.
 *
 * <p>Each time a request is received, {@link #getTimeZone} is called
 * against the instance returned by
 * {@link org.zkoss.zk.ui.util.Configuration#getTimeZoneProvider}.
 * 
 * @author tomyeh
 */
public interface TimeZoneProvider {
	/** Returns the time zone of the specified session, or null if the default
	 * shall be used.
	 *
	 * <p>The default is determined by JVM's preference.
	 * Notice that there is no standard way to retrieve browser's time zone,
	 * so all clients have the same time zone if you don't specify a
	 * instance of {@link TimeZoneProvider}.
	 *
	 * @param sess the session
	 * @param request the request, depending on the protocol.
	 * For exampple, it is javax.servlet.http.HttpServletRequest if HTTP.
	 * @param response the response, depending on the protocol.
	 * For exampple, it is javax.servlet.http.HttpServletResponse if HTTP.
	 * @since 2.3.2
	 */
	public TimeZone getTimeZone(Session sess, Object request, Object response);
}
