/* TimeZoneProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 13 15:48:32     2006, Created by tomyeh@potix.com
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
 * Once specified (in {@link org.zkoss.zk.ui.util.Configuration}), an instance of the specified class
 * is created and then {@link #getTimeZone} is called, each time
 * a request from the client is received.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface TimeZoneProvider {
	/** Returns the time zone of the specified session, or null if the default
	 * shall be used.
	 *
	 * <p>The default is determined by JVM's preference.
	 * Notice that there is no standard way to retrieve browser's time zone,
	 * so all clients have the same time zone if you don't specify a
	 * instance of {@link TimeZoneProvider}.
	 */
	public TimeZone getTimeZone(Session sess);
}
