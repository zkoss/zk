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
 * and the MIL device called {@link MilDevice}.
 *
 * <p>Note: the device must be serializable.
 *
 * @author tomyeh
 */
public interface Device {
	/** Returns the device type.
	 */
	public String getType();
	/** Returns the unavailable message that is shown to the client
	 * if the client doesn't support this device.
	 */
	public String getUnavailableMessage();
	/** Sets the unavailable message that is shown to the client
	 * if the client doesn't support this device.
	 *
	 * <p>Note: this method must be called before the desktop is rendered
	 * (and sent to the client).
	 * Otherwise, it is meaningless.
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
	 * supported by the client (aka., unavailable).
	 */
	public void init(String type, Desktop desktop, String unavailmsg);
	/** Notification that the desktop, which owns this device,
	 * is about to be passivated (aka., serialized) by the Web container.
	 */
	public void sessionWillPassivate(Desktop desktop);
	/** Notification that the desktop, which owns this device,
	 * has just been activated (aka., deserialized) by the Web container.
	 */
	public void sessionDidActivate(Desktop desktop);
}
