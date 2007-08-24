/* Device.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 14 19:13:14     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import org.zkoss.zk.ui.Desktop;

/**
 * Represents a client device.
 * For example, the HTML browsers with Ajax are called {@link AjaxDevice},
 * the XML output called {@link XmlDevice},
 * and the MIL device called org.zkoss.mil.device.MilDevice.
 *
 * <p>Note: the same device is shared by all desktops of the same device
 * type.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public interface Device {
	/** Returns the device type.
	 */
	public String getType();

	/** Returns whether the output can be cached by the client.
	 */
	public boolean isCacheable();

	/** Returns the unavailable message that is shown to the client
	 * if the client doesn't support this device.
	 *
	 * @return the unavailable message, or null if no such message
	 * @since 2.4.0
	 */
	public String getUnavailableMessage();
	/** Sets the unavailable message that is shown to the client
	 * if the client doesn't support this device.
	 *
	 * @param unavailmsg the unavailable message.
	 * @since 2.4.0
	 */
	public void setUnavailableMessage(String unavailmsg);

	/** Returns the timeout URI for this device.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 *
	 * <p>Default: null (to shown an error message).
	 *
	 * @since 2.5.0
	 */
	public String getTimeoutURI();
	/** Sets the timeout URI.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 *
	 * @param timeoutURI the timeout URI. If empty, it means to reload
	 * the same page. If null, an error message is shown instead of
	 * redirecting to another page.
	 * @since 2.5.0
	 */
	public void setTimeoutURI(String timeoutURI);

	/** Returns the default content type (never null).
	 * 
	 * @since 2.5.0
	 */
	public String getContentType();
	/** Returns the default doc type, or null if no doc type at all.
	 *
	 * @since 2.5.0
	 */
	public String getDocType();

	/** Initializes the device.
	 * A device is created for each desktop, and this method is called
	 * when it is associated to the desktop.
	 *
	 * @param type the device type
	 * @param unavailmsg the message to shown when the device is not, or null
	 * no message will be shown
	 * @param timeoutURI the timeout URI. If empty, it means to reload
	 * the same page. If null, an error message is shown instead of
	 * redirecting to another page.
	 * @since 2.5.0
	 */
	public void init(String type, String unavailmsg, String timeoutURI);
	/** Notification that the desktop, which owns this device,
	 * is about to be passivated (aka., serialized) by the Web container.
	 * @since 2.4.0
	 */
	public void sessionWillPassivate(Desktop desktop);
	/** Notification that the desktop, which owns this device,
	 * has just been activated (aka., deserialized) by the Web container.
	 * @since 2.4.0
	 */
	public void sessionDidActivate(Desktop desktop);
}
