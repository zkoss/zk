/* Device.java

	Purpose:
		
	Description:
		
	History:
		Mon May 14 19:13:14     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.sys.ServerPush;

/**
 * Represents a client device.
 * For example, the HTML browsers with Ajax are called {@link AjaxDevice},
 * the XML output called org.zkoss.zml.device.XmlDevice,
 * and the MIL device called org.zkoss.mil.device.MilDevice.
 *
 * <p>Note: the same device is shared by all desktops of the same device
 * type.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public interface Device {
	
	/** Used with {@link #isSupported} to know whether the device supports
	 * the resend mechanism.
	 * In other words, whether the client will resend the request if
	 * the time specified in {@link org.zkoss.zk.ui.util.Configuration#getResendDelay}
	 * expires.
	 * @since 3.0.3
	 */
	public static final int RESEND = 1;

	/** Returns whether the specified function is supported.
	 *
	 * @param func the function. It can be one of {@link #RESEND}.
	 * @since 3.0.3
	 */
	public boolean isSupported(int func);

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
	 * @return the previous unavailable message, or null if not such message
	 * @since 2.4.0
	 */
	public String setUnavailableMessage(String unavailmsg);

	/** @deprecated As of release 3.6.3, use {@link org.zkoss.zk.ui.util.Configuration#getTimeoutURI}
	 * instead.
	 */
	public String getTimeoutURI();
	/** @deprecated As of release 3.6.3, use {@link org.zkoss.zk.ui.util.Configuration#setTimeoutURI}
	 * instead.
	 */
	public String setTimeoutURI(String timeoutURI);

	/** Returns whether this device supports the specified client.
	 *
	 * @param userAgent represents a client.
	 * For HTTP clients, It is the user-agent header.
	 * @return Boolean.TRUE if this device supports the specified client,
	 * Boolean.FALSE if cannot, or null if unknown.
	 * @see org.zkoss.zk.ui.Execution#getUserAgent
	 * @see Devices#getDeviceByClient
	 * @since 3.5.0
	 */
	public Boolean isCompatible(String userAgent);

	/** Returns the class that implements the server-push feature
	 * ({@link ServerPush}) for this device, or null if the default is used.
	 * @since 3.0.0
	 */
	public Class getServerPushClass();
	/** Sets the class that implements the server-push feature
	 * ({@link ServerPush}) for this device, or null to use the default.
	 *
	 * <p>Default: {@link org.zkoss.zk.ui.impl.PollingServerPush}.
	 *
	 * <p>If ZK EE (with zkmax.jar) is loaded,
	 * the COMET-based server push ({@link org.zkoss.zkmax.ui.comet.CometServerPush})
	 * is the default.
	 * @return the previous class, or null if not available.
	 * @since 3.0.0
	 */
	public Class setServerPushClass(Class cls);

	/** Returns the default content type (never null).
	 * 
	 * @since 3.0.0
	 */
	public String getContentType();
	/** Returns the default doc type, or null if no doc type at all.
	 *
	 * @since 3.0.0
	 */
	public String getDocType();

	/** Adds the content that shall be added to the output generated and
	 * sent to the client, when rending a desktop.
	 * What content can be embedded depends on the device.
	 * For Ajax devices, it can be anything that can be placed inside
	 * HTML HEAD, such as JavaScript codes.
	 *
	 * <p>As the method name suggests, the embedded contents are accumulated
	 * and all generated to the output.
	 * @since 3.0.6
	 */
	public void addEmbedded(String content);
	/** Returns the content that shall be embedded to the output being
	 * generated to the client, or null if no embedded content.
	 *
	 * @since 3.0.6
	 */
	public String getEmbedded();

	/** Initializes the device.
	 * A device is created for each desktop, and this method is called
	 * when it is associated to the desktop.
	 *
	 * @param deviceType the device type (never null)
	 * @param config the configuration to initialize the device (never null)
	 * @since 3.0.0
	 */
	public void init(String deviceType, DeviceConfig config);
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

	/** Tests if a client is the givent type.
	 * @param userAgent represents a client.
	 * @param type the type of the browser.
	 * @return true if it matches, false if unable to identify.
	 * Note: the default identifies ie*, gecko*, safari, opera and hil.
	 * So, the implementation needs not to identify them (i.e., simply
	 * return false), though it is harmless to identify.
	 * @since 5.0.0
	 */
	public boolean isClient(String userAgent, String type);
}
