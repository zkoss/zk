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
	private static final Map _devs = new HashMap(5);
	/** Map(String type, String unavailableMesssage).
	 * Note: we store the message
	 */
	private static final Map _uamsgs = new HashMap(3);
	/** Map(String type, String timeoutURI). */
	private static final Map _tmoutURIs = new HashMap(3);

	/** Returns the device for the specified desktop type.
	 *
	 * @param deviceType the device type, such as ajax, xml and mil.
	 * @exception DeviceNotFoundException if not found.
	 * @since 2.5.0
	 */
	public static final Device getDevice(String deviceType) {
		final Object obj;
		synchronized (_devs) {
			obj = _devs.get(deviceType);
		}

		if (obj instanceof Device)
			return (Device)obj;
		if (obj == null)
			throw new DeviceNotFoundException(deviceType, MZk.NOT_FOUND, deviceType);

		final Class cls;
		if (obj instanceof Class) {
			cls = (Class)obj;
		} else {
			try {
				cls = Classes.forNameByThread((String)obj);
			} catch (ClassNotFoundException ex) {
				throw new UiException("Failed to load class "+obj);
			}
			if (!Device.class.isAssignableFrom(cls))
				throw new IllegalArgumentException(cls+" must implements "+Device.class);
		}

		final String msg;
		synchronized (_uamsgs) {
			msg = (String)_uamsgs.get(deviceType);
		}
		final String timeoutURI;
		synchronized (_tmoutURIs) {
			timeoutURI = (String)_tmoutURIs.get(deviceType);
		}

		try {
			final Device device = (Device)cls.newInstance();
			device.init(deviceType, msg, timeoutURI);

			synchronized (_devs) {
				final Object old = _devs.put(deviceType, device);
				if (old != obj)
					_devs.put(deviceType, old); //changed by someone else; so restore
			}
			return device;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex, "Unable to create "+cls);
		}
	}
	/** Tests whether the device for the specified type exists.
	 *
	 * @param deviceType the device type, say, ajax.
	 * @since 2.4.0
	 */
	public static final boolean exists(String deviceType) {
		if (deviceType == null) return false;

		synchronized (_devs) {
			return _devs.containsKey(deviceType);
		}
	}

	/** Adds a device type.
	 *
	 * @param deviceType the device type (aka., the device name).
	 * @param clsnm the device class name
	 * @return the previous class of the device with the same type, if any,
	 * or null if no such device.
	 */
	public static final String add(String deviceType, String clsnm) {
		return add0(deviceType, clsnm);
	}
	/** Adds a device type.
	 *
	 * @param deviceType the device type (aka., the device name).
	 * @param cls the device class
	 * @return the previous class of the device with the same type, if any,
	 * or null if no such device.
	 * @since 2.5.0
	 */
	public static final String add(String deviceType, Class cls) {
		return add0(deviceType, cls);
	}
	private static final String add0(String deviceType, Object cls) {
		if (deviceType == null || deviceType.length() == 0
		|| cls == null)
			throw new IllegalArgumentException();

		final Object old;
		synchronized (_devs) {
			old = _devs.put(deviceType, cls);
		}

		return old instanceof Class ? ((Class)old).getName():
			old instanceof Device ? old.getClass().getName(): (String)old;
	}

	/** Returns the unavailable message for the specified device type.
	 *
	 * <p>The result is the same as the invocation of {@link Device#getUnavailableMessage}
	 * against {@link #getDevice}, but this method don't load the device
	 * if it is not loaded yet.
	 *
	 * @see Device
	 * @see Device#getUnavailableMessage
	 */
	public static final String getUnavailableMessage(String deviceType) {
		synchronized (_devs) {
			final Object o = _devs.get(deviceType);
			if (o instanceof Device)
				return ((Device)o).getUnavailableMessage();
		}
		synchronized (_uamsgs) {
			return (String)_uamsgs.get(deviceType);
		}
	}
	/** Sets the unavailable message for the specified device type.
	 *
	 * @return the previous unavailable message if any.
	 * @see Device
	 * @see Device#setUnavailableMessage
	 */
	public static final String setUnavailableMessage(String deviceType, String msg) {
		if (deviceType == null || deviceType.length() == 0)
			throw new IllegalArgumentException();

		if (msg != null && msg.length() == 0)
			msg = null;

		synchronized (_devs) {
			final Object o = _devs.get(deviceType);
			if (o instanceof Device)
				((Device)o).setUnavailableMessage(msg);
		}
		synchronized (_uamsgs) {
			return (String)(
				msg != null ?
					_uamsgs.put(deviceType, msg):
					_uamsgs.remove(deviceType));
		}
	}

	/** Returns the timeout URI for the specified device type.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 *
	 * <p>Default: null (to shown an error message).
	 *
	 * <p>The result is the same as the invocation of {@link Device#getTimeoutURI}
	 * against {@link #getDevice}, but this method don't load the device
	 * if it is not loaded yet.
	 */
	public static final String getTimeoutURI(String deviceType) {
		synchronized (_devs) {
			final Object o = _devs.get(deviceType);
			if (o instanceof Device)
				return ((Device)o).getTimeoutURI();
		}
		synchronized (_tmoutURIs) {
			return (String)_tmoutURIs.get(deviceType);
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
	public static final String setTimeoutURI(String deviceType, String timeoutURI) {
		if (deviceType == null || deviceType.length() == 0)
			throw new IllegalArgumentException();

		synchronized (_devs) {
			final Object o = _devs.get(deviceType);
			if (o instanceof Device)
				((Device)o).setTimeoutURI(timeoutURI);
		}
		synchronized (_tmoutURIs) {
			return (String)(
				timeoutURI != null ?
					_tmoutURIs.put(deviceType, timeoutURI):
					_tmoutURIs.remove(deviceType));
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
		//Spec: it is OK to declare an nonexist device
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
