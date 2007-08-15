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

import java.util.Map;
import org.zkoss.zk.ui.Desktop;

/**
 * Represents a client device.
 * For example, the HTML browsers with Ajax are called {@link AjaxDevice},
 * and the MIL device called org.zkoss.mil.device.MilDevice.
 *
 * <p>Note: the device must be serializable.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public interface Device {
	/** Returns the device type.
	 */
	public String getType();
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
	 * <p>Note: this method must be called before the desktop is rendered
	 * (and sent to the client).
	 * Otherwise, it is meaningless.
	 *
	 * @param unavailmsg the unavailable message.
	 * @since 2.4.0
	 */
	public void setUnavailableMessage(String unavailmsg);

	/** Initializes the device.
	 * A device is created for each desktop, and this method is called
	 * when it is associated to the desktop.
	 *
	 * @param type the device type
	 * @param desktop the desktop to associate this device with (never null).
	 * @param unavailmsg the message to shown when the device is not, or null
	 * no message will be shown
	 * @since 2.4.0
	 */
	public void init(String type, Desktop desktop, String unavailmsg);
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

	/**
	 * Returns the begin portion of the raw tag (never null).
	 * For example, HTML devices returns "&lt;tr&gt;" when
	 * getRawTagBegin("tr", null) is called.
	 * It returns "&lt;br/&gt;" when getRawTagBegin("br") is called.
	 *
	 * @param tagname the tag name, e.g., tr, or null to ignore.
	 * @param props a map of properties, or null to ignore.
	 * It is pairs of name and value.
	 * @since 2.5.0
	 */
	public String getRawTagBegin(String tagname, Map props);
	/**
	 * Returns the end portion of the raw tag (never null).
	 * For example, HTML devices returns "&lt;/tr&gt;" when
	 * getRawTagEnd("tr") is called.
	 * It returns "" when getRawTagEnd("br") is called.
	 *
	 * @param tagname the tag name, e.g., tr, or null to ignore.
	 * @since 2.5.0
	 */
	public String getRawTagEnd(String tagname);
}
