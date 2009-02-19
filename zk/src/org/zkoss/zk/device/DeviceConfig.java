/* DeviceConfig.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 24 18:43:51     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

/**
 * Used to initialize a device ({@link Device}).
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface DeviceConfig {
	/** Returns the unavailable message that is shown to the client
	 * if the client doesn't support this device.
	 *
	 * @return the unavailable message, or null if no such message
	 */
	public String getUnavailableMessage();
	/** Returns the timeout URI for this device.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 * @since 3.0.0
	 */
	public String getTimeoutURI();
	/** Returns the class that implements the server-push feature
	 * ({@link org.zkoss.zk.ui.sys.ServerPush}) for this device, or null if the default is used.
	 * @since 3.0.0
	 */
	public Class getServerPushClass();
	/** Returns the content that shall be embedded to the output being
	 * generated to the client, or null if no embedded content.
	 *
	 * @since 3.0.6
	 */
	public String getEmbedded();
}
