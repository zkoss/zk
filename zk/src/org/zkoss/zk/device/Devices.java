/* Devices.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 15 14:09:16     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.lang.Classes;
import org.zkoss.idom.Element;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;

/**
 * A manager of devices ({@link Device}).
 *
 * @author tomyeh
 * @since 2.4.0
 */
public class Devices {
	private Devices() {}

	/** Map(String type, String/Class cls). */
	private static final Map _devs = new HashMap(3);
	/** Map(String type, String unavailableMesssage). */
	private static final Map _uamsgs = new HashMap(3);
	/** Map(String type, String timeoutURI). */
	private static final Map _tmoutURIs = new HashMap(3);

	/** Returns the device for the specified desktop.
	 *
	 * @param desktop the desktop to create a device and associate with.
	 * @exception DeviceNotFoundException if not found.
	 * @since 2.4.0
	 */
	public static final Device newDevice(Desktop desktop) {
		if (desktop == null)
			throw new IllegalArgumentException("null");

		final String type = desktop.getDeviceType();
		final Object clsnm;
		synchronized (_devs) {
			clsnm = _devs.get(type);
		}
		if (clsnm == null)
			throw new DeviceNotFoundException(type, MZk.NOT_FOUND, type);

		final Class cls;
		if (clsnm instanceof Class) {
			cls = (Class)clsnm;
		} else {
			try {
				cls = Classes.forNameByThread((String)clsnm);
			} catch (ClassNotFoundException ex) {
				throw new UiException("Failed to load class "+clsnm);
			}
			if (!Device.class.isAssignableFrom(cls))
				throw new IllegalArgumentException(cls+" must implements "+Device.class);

			synchronized (_devs) {
				final Object old = _devs.put(type, cls);
				if (old != clsnm)
					_devs.put(type, old); //changed by someone else; so restore
			}
		}

		final String msg;
		synchronized (_uamsgs) {
			msg = (String)_uamsgs.get(type);
		}

		try {
			final Device device = (Device)cls.newInstance();
			device.init(type, desktop, msg);
			return device;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex, "Unable to create "+cls);
		}
	}
	/** Tests whether the device for the specified type exists.
	 *
	 * @param type the device type, say, ajax.
	 * @since 2.4.0
	 */
	public static final boolean exists(String type) {
		if (type == null) return false;

		type = type.toLowerCase();
		synchronized (_devs) {
			return _devs.containsKey(type);
		}
	}

	/** Adds a device.
	 *
	 * @param type the device type (aka., the device name).
	 * @param cls the device class name
	 * @return the previous class of the device with the same type, if any,
	 * or null if no such device.
	 */
	public static final String add(String type, String cls) {
		if (type == null || type.length() == 0
		|| cls == null || cls.length() == 0)
			throw new IllegalArgumentException("emty or null");

		final Object old;
		synchronized (_devs) {
			old = _devs.put(type, cls);
		}

		return old instanceof Class ? ((Class)old).getName(): (String)old;
	}
	/** Returns the unavailable message for the specified device type.
	 *
	 * <p>Note: the current device (the device associated with the current
	 * desktop) might have a different message.
	 *
	 * @see Device
	 * @see Device#getUnavailableMessage
	 */
	public static final String getUnavailableMessage(String type) {
		synchronized (_uamsgs) {
			return (String)_uamsgs.get(type);
		}
	}
	/** Sets the unavailable message for the specified device type.
	 * It affects only the devices ({@link Device}) that are created for
	 * the new desktops. The message for the devices that are already
	 * instantiated are not changed.
	 *
	 * @return the previous unavailable message if any.
	 * @see Device
	 * @see Device#setUnavailableMessage
	 */
	public static final String setUnavailableMessage(String type, String msg) {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("type");

		if (msg != null && msg.length() == 0)
			msg = null;

		synchronized (_uamsgs) {
			return (String)(
				msg != null ? _uamsgs.put(type, msg): _uamsgs.remove(type));
		}
	}

	/** Returns the timeout URI for the specified device type.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 *
	 * <p>Default: null (to shown an error message).
	 *
	 * <p>Note: the current device (the device associated with the current
	 * desktop) might have a different timeout URI.
	 */
	public static final String getTimeoutURI(String type) {
		synchronized (_tmoutURIs) {
			return (String)_tmoutURIs.get(type);
		}
	}
	/** Sets the timeout URI for the specified device type.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 *
	 * @param timeoutURI the timeout URI. If empty, it means to reload
	 * the same page. If null, an error message is shown instead of
	 * redirecting to another page.
	 * @return the previous timeout URI if any.
	 */
	public static final String setTimeoutURI(String type, String timeoutURI) {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("type");

		synchronized (_tmoutURIs) {
			return (String)(
				timeoutURI != null ?
					_tmoutURIs.put(type, timeoutURI): _tmoutURIs.remove(type));
		}
	}

	/** Adds a device based on the XML declaration.
	 *
	 * <pre><code>
&lt;device-config&gt;
  &lt;device-type&gt;superajax&lt;/device-type&gt;
  &lt;device-class&gt;my.MyDevice&lt;/device-class&gt;
  &lt;unavailable-message&gt;error message&lt;/unavailable-message&gt;
&lt;/device-config&gt;
	 * </code></pre>
	 *
	 * @param config the XML element called zscript-config
	 */
	public static final void add(Element config) {
		//Spec: it is OK to declare an nonexist device, since
		//deployer might remove unused jar files.
		final String type =
			IDOMs.getRequiredElementValue(config, "device-type");
		String s = config.getElementValue("device-class", true);
		if (s != null)
			add(type, s);

		s = config.getElementValue("unavailable-message", true);
		if (s != null)
			setUnavailableMessage(type, s);

		s = config.getElementValue("timeout-uri", true);
		if (s != null)
			setTimeoutURI(type, s);
	}
}
