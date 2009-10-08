/* Devices.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 15 14:09:16     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;

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

	/** Map(String type, DeviceInfo info or Device device). */
	private static final Map _devs = new LinkedHashMap(8);

	/** Returns the device for the specified desktop type.
	 *
	 * @param deviceType the device type, such as ajax, xml and mil.
	 * @exception DeviceNotFoundException if not found.
	 * @since 3.0.0
	 */
	public static final Device getDevice(String deviceType)
	throws DeviceNotFoundException {
		final Object o; //null, Device or DeviceInfo
		synchronized (_devs) {
			o = _devs.get(deviceType);
		}

		if (o instanceof Device)
			return (Device)o;
		if (o == null)
			throw new DeviceNotFoundException(deviceType, MZk.NOT_FOUND, deviceType);

		final Device device = ((DeviceInfo)o).newDevice(deviceType);
		synchronized (_devs) {
			final Object old = _devs.put(deviceType, device);
			if (old != o)
				_devs.put(deviceType, old); //changed by someone else; so restore
		}
		return device;
	}
	/** Returns the device for the specified client.
	 * It invokes {@link Device#isCompatible} to return the correct device.
	 *If all devices returns null (means unknown), one of the devices returning
	 *null is returned. If all devices returns Boolean.FALSE,
	 *DeviceNotFoundException is thrown.
	 *
	 * @param userAgent represents a client.
	 * It is the user-agent header for HTTP-base client.
	 * @see org.zkoss.zk.ui.Execution#getUserAgent
	 * @exception DeviceNotFoundException if not found.
	 * @since 3.0.0
	 */
	public static final Device getDeviceByClient(String userAgent)
	throws DeviceNotFoundException {
		String[] devTypes;
		synchronized (_devs) {
			Collection c = _devs.keySet();
			devTypes = (String[])c.toArray(new String[c.size()]);
		}

		Device device = null;
		for (int j = 0; j < devTypes.length; ++j) {
			Device dev;
			try {
				dev = getDevice(devTypes[j]);
			} catch (Throwable ex) {
				continue; //skip
			}

			Boolean b;
			try {
				b = dev.isCompatible(userAgent);
			} catch (Throwable ex) { //backward compatible
				b = null;
			}

			if (b != null) {
				if (b.booleanValue())
					return dev;
			} else if (device == null
			|| "ajax".equals(devTypes[j]) //ajax highest priority
			|| "xml".equals(device.getType())) { //xml lowest priority
				device = dev;
			}
		}
		if (device == null)
			throw new DeviceNotFoundException(userAgent, MZk.NOT_FOUND, userAgent);
		return device;
	}

	/** Tests whether the device for the specified type exists.
	 *
	 * @param deviceType the device type, say, ajax.
	 * @since 2.4.0
	 */
	public static final boolean exists(String deviceType) {
		if (deviceType == null) return false;

		final Object o;
		synchronized (_devs) {
			o = _devs.get(deviceType);
		}
		return o instanceof Device
			|| (o != null && ((DeviceInfo)o).isValid());
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
	 * @since 3.0.0
	 */
	public static final String add(String deviceType, Class cls) {
		return add0(deviceType, cls);
	}
	private static final String add0(String deviceType, Object cls) {
		if (deviceType == null || deviceType.length() == 0
		|| cls == null)
			throw new IllegalArgumentException();

		synchronized (_devs) {
			final Object o = _devs.get(deviceType);
			if (o instanceof DeviceInfo) {
				return ((DeviceInfo)o).setDeviceClass(cls);
			} else if (o instanceof Device) {
				final Device device = (Device)o;
				_devs.put(deviceType,
					new DeviceInfo(cls,
						device.getUnavailableMessage(),
						device.getTimeoutURI(),
						device.isAutomaticTimeout(),
						device.getServerPushClass()));
				return device.getClass().getName();
			} else {
				_devs.put(deviceType, new DeviceInfo(cls));
				return null;
			}
		}
	}

	/** Returns the unavailable message for the specified device type.
	 *
	 * <p>The result is the same as the invocation of {@link Device#getUnavailableMessage}
	 * against {@link #getDevice}, but this method will not load the device
	 * if it is not loaded yet.
	 *
	 * @see Device
	 * @see Device#getUnavailableMessage
	 */
	public static final String getUnavailableMessage(String deviceType) {
		final Object o;
		synchronized (_devs) {
			o = _devs.get(deviceType);
		}
		return o instanceof Device ? ((Device)o).getUnavailableMessage():
			o instanceof DeviceInfo ? ((DeviceInfo)o).getUnavailableMessage(): null;
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
			if (o instanceof Device) {
				return ((Device)o).setUnavailableMessage(msg);
			} else if (o instanceof DeviceInfo) {
				return ((DeviceInfo)o).setUnavailableMessage(msg);
			} else {
				final DeviceInfo info = new DeviceInfo();
				_devs.put(deviceType, info);
				info.setUnavailableMessage(msg);
				return null;
			}
		}
	}

	/** Returns the timeout URI for the specified device type.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 *
	 * <p>Default: null (to shown an error message).
	 *
	 * <p>The result is the same as the invocation of {@link Device#getTimeoutURI}
	 * against {@link #getDevice}, but this method will not load the device
	 * if it is not loaded yet.
	 */
	public static final String getTimeoutURI(String deviceType) {
		final Object o;
		synchronized (_devs) {
			o = _devs.get(deviceType);
		}
		return o instanceof Device ? ((Device)o).getTimeoutURI():
			o instanceof DeviceInfo ? ((DeviceInfo)o).getTimeoutURI(): null;
	}
	/** Sets the timeout URI for the specified device type.
	 * It is used to show the error message if the desktop being requested
	 * is not found. It is usually caused by session timeout.
	 *
	 * @param timeoutURI the timeout URI. If empty, it means to reload
	 * the same page. If null, an error message is shown instead of
	 * redirecting to another page.
	 * @return the previous timeout URI if any.
	 * @see Device#setTimeoutURI
	 */
	public static final String setTimeoutURI(String deviceType, String timeoutURI) {
		if (deviceType == null || deviceType.length() == 0)
			throw new IllegalArgumentException();

		synchronized (_devs) {
			final Object o = _devs.get(deviceType);
			if (o instanceof Device) {
				return ((Device)o).setTimeoutURI(timeoutURI);
			} else if (o instanceof DeviceInfo) {
				return ((DeviceInfo)o).setTimeoutURI(timeoutURI);
			} else {
				final DeviceInfo info = new DeviceInfo();
				_devs.put(deviceType, info);
				info.setTimeoutURI(timeoutURI);
				return null;
			}
		}
	}

	/** Returns whether to automatical redirect to the timeout URI.
	 * @see #setAutomaticTimeout
	 * @see #getTimeoutURI
	 * @since 3.6.3
	 */
	public static final boolean isAutomaticTimeout(String deviceType) {
		final Object o;
		synchronized (_devs) {
			o = _devs.get(deviceType);
		}
		return (o instanceof Device && ((Device)o).isAutomaticTimeout())
			|| (o instanceof DeviceInfo && ((DeviceInfo)o).isAutomaticTimeout());
	}
	/** Sets whether to automatical redirect to the timeout URI.
	 *
	 * <p>Default: false. It means this page is redirected to the timeout URI
	 * when the use takes some action after timeout. In other words,
	 * nothing happens if the user does nothing.
	 * If it is set to true, it is redirected as soon as timeout,
	 * no matter the user takes any action.
	 *
	 * @see #setTimeoutURI
	 * @see Device#setAutomaticTimeout
	 * @since 3.6.3
	 */
	public static final boolean setAutomaticTimeout(String deviceType, boolean auto) {
		if (deviceType == null || deviceType.length() == 0)
			throw new IllegalArgumentException();

		synchronized (_devs) {
			final Object o = _devs.get(deviceType);
			if (o instanceof Device) {
				return ((Device)o).setAutomaticTimeout(auto);
			} else if (o instanceof DeviceInfo) {
				return ((DeviceInfo)o).setAutomaticTimeout(auto);
			} else {
				final DeviceInfo info = new DeviceInfo();
				_devs.put(deviceType, info);
				info.setAutomaticTimeout(auto);
				return false;
			}
		}
	}

	/** Returns the content that shall be embedded to the output being
	 * generated to the client, or null if no embedded content.
	 *
	 * @since 3.0.6
	 */
	public String getEmbedded(String deviceType) {
		final Object o;
		synchronized (_devs) {
			o = _devs.get(deviceType);
		}
		return o instanceof Device ? ((Device)o).getEmbedded():
			o instanceof DeviceInfo ? ((DeviceInfo)o).getEmbedded(): null;
	}
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
	public static void addEmbedded(String deviceType, String content) {
		if (deviceType == null || deviceType.length() == 0)
			throw new IllegalArgumentException();
		if (content == null || content.length() == 0)
			return; //nothing to do

		synchronized (_devs) {
			final Object o = _devs.get(deviceType);
			if (o instanceof Device) {
				((Device)o).addEmbedded(content);
			} else if (o instanceof DeviceInfo) {
				((DeviceInfo)o).addEmbedded(content);
			} else {
				final DeviceInfo info = new DeviceInfo();
				_devs.put(deviceType, info);
				info.addEmbedded(content);
			}
		}
	}

	/** Returns the class name that implements the server push feature.
	 *
	 * <p>Default: null (the server-push feature is not available).
	 *
	 * <p>The result is the same as the invocation of {@link Device#getServerPushClass}
	 * against {@link #getDevice}, but this method will not load the device
	 * if it is not loaded yet.
	 * @since 3.0.0
	 */
	public static final String getServerPushClass(String deviceType) {
		final Object o;
		synchronized (_devs) {
			o = _devs.get(deviceType);
		}
		if (o instanceof Device) {
			final Class cls = ((Device)o).getServerPushClass();
			return cls != null ? cls.getName(): null;
		}
		return o instanceof DeviceInfo ? ((DeviceInfo)o).getServerPushClassName(): null;
	}
	/** Sets the name of the class that implements the server-push feature.
	 *
	 * @param clsnm the class name that implements the server push.
	 * If null, it means no server push is available.
	 * @return the previous class name, or null if not available.
	 * @since 3.0.0
	 */
	public static final String setServerPushClass(String deviceType, String clsnm) {
		return setServerPushClass0(deviceType, clsnm);
	}
	/** Sets the class that implements the server-push feature.
	 *
	 * @param cls the class that implements the server push.
	 * If null, it means no server push is available.
	 * @return the previous class name, or null if not available.
	 * @since 3.0.0
	 */
	public static final String setServerPushClass(String deviceType, Class cls) {
		return setServerPushClass0(deviceType, cls);
	}
	private static final String setServerPushClass0(String deviceType, Object cls) {
		if (deviceType == null || deviceType.length() == 0)
			throw new IllegalArgumentException();

		try {
			synchronized (_devs) {
				final Object o = _devs.get(deviceType);
				if (o instanceof Device) {
					final Class old = ((Device)o).setServerPushClass(
						cls instanceof Class ? (Class)cls:
						cls != null ? Classes.forNameByThread((String)cls): null);
					return old != null ? old.getName(): null;
				} else if (o instanceof DeviceInfo) {
					return ((DeviceInfo)o).setServerPushClass(cls);
				} else {
					final DeviceInfo info = new DeviceInfo();
					_devs.put(deviceType, info);
					info.setServerPushClass(cls);
					return null;
				}
			}
		} catch (ClassNotFoundException ex) {
			throw new UiException("Class not found: "+cls);
		}
	}

	/** Adds a device based on the XML declaration.
	 *
	 * <pre><code>
&lt;device-config&gt;
  &lt;device-type&gt;superajax&lt;/device-type&gt;
  &lt;device-class&gt;my.MyDevice&lt;/device-class&gt;
  &lt;unavailable-message&gt;error message&lt;/unavailable-message&gt;
  &lt;timeout-uri&gt;/WEB-INF/timeout.zul&lt;/timeout-uri&gt;
  &lt;automatic/&gt;
  &lt;server-push-class&gt;my.MyServerPush&lt;/server-push-class&gt;
&lt;/device-config&gt;
	 * </code></pre>
	 *
	 * @param config the XML element called zscript-config
	 */
	public static final void add(Element config) {
		//Spec: it is OK to declare an nonexist device
		final String deviceType =
			IDOMs.getRequiredElementValue(config, "device-type");

		String s = config.getElementValue("device-class", true);
		if (s != null)
			add(deviceType, s);

		s = config.getElementValue("unavailable-message", true);
		if (s != null)
			setUnavailableMessage(deviceType, s);

		s = config.getElementValue("timeout-uri", true);
		if (s != null)
			setTimeoutURI(deviceType, s);

		s = config.getElementValue("automatic", true);
		if (s != null)
			setAutomaticTimeout(deviceType, !"false".equals(s));

		s = config.getElementValue("server-push-class", true);
		if (s != null)
			setServerPushClass(deviceType, s);

		for (Iterator it = config.getElements("embed").iterator();
		it.hasNext();) {
			addEmbedded(deviceType, ((Element)it.next()).getText(true));
		}
	}
	/** Device info.
	 */
	private static class DeviceInfo implements DeviceConfig {
		/** The class or class name of {@link Device}
		 * of the device's implementation.
		 */
		private Object _dvcls;
		private String _uamsg, _tmoutURI;
		/** The class name or class of {@link ServerPush}.
		 */
		private Object _spushcls;
		private String _embed;
		private boolean _autoTimeout;

		private DeviceInfo() {
		}
		private DeviceInfo(Object deviceClass) {
			_dvcls = deviceClass;
		}
		private DeviceInfo(Object deviceClass, String unavailable,
		String timeoutURI, boolean autoTimeout, Class spushcls) {
			_dvcls = deviceClass;
			_uamsg = unavailable;
			_tmoutURI = timeoutURI;
			_autoTimeout = autoTimeout;
			_spushcls = spushcls;
		}
		/** Returns whether this device is valid, i.e., defined with a device class.
		 */
		private boolean isValid() {
			return _dvcls != null;
		}
		/** Sets the device class.
		 */
		private String setDeviceClass(Object cls) {
			final Object old = _dvcls;
			_dvcls = cls;
			return old instanceof Class ? ((Class)old).getName(): (String)old;
		}
		public String getUnavailableMessage() {
			return _uamsg;
		}
		public String setUnavailableMessage(String msg) {
			final String old = _uamsg;
			_uamsg = msg != null && msg.length() > 0 ? msg: null;
			return old;
		}
		public String getTimeoutURI() {
			return _tmoutURI;
		}
		public String setTimeoutURI(String timeoutURI) {
			final String old = _tmoutURI;
			_tmoutURI = timeoutURI;
			return old;
		}
		public boolean isAutomaticTimeout() {
			return _autoTimeout;
		}
		public boolean setAutomaticTimeout(boolean auto) {
			final boolean old = _autoTimeout;
			_autoTimeout = auto;
			return old;
		}
		/**
		 * @param cls the class name or class of the server push.
		 */
		public String setServerPushClass(Object cls) {
			final Object old = _spushcls;
			_spushcls = cls;
			return old instanceof Class ? ((Class)old).getName(): (String)old;
		}
		public String getServerPushClassName() {
			return _spushcls instanceof Class ? ((Class)_spushcls).getName(): (String)_spushcls;
		}
		public Class getServerPushClass() {
			try {
				return _spushcls instanceof Class ? (Class)_spushcls:
					_spushcls != null ?
						Classes.forNameByThread((String)_spushcls): null;
			} catch (ClassNotFoundException ex) {
				throw new UiException("Class not found: "+_spushcls);
			}
		}
		public void addEmbedded(String content) {
			if (content != null && content.length() > 0)
				_embed = _embed != null ? _embed + '\n' + content: content;
		}
		public String getEmbedded() {
			return _embed;
		}

		/** Creates a device based on this device info.
		 */
		private Device newDevice(String deviceType) {
			if (_dvcls == null) //possible
				throw new DeviceNotFoundException(deviceType, MZk.NOT_FOUND, deviceType);

			try {
				final Class cls;
				if (_dvcls instanceof Class) {
					cls = (Class)_dvcls;
				} else {
					cls = Classes.forNameByThread((String)_dvcls);
					if (!Device.class.isAssignableFrom(cls))
						throw new IllegalArgumentException(cls+" must implements "+Device.class);
				}

				final Device device = (Device)cls.newInstance();
				device.init(deviceType, this);
				return device;
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to create "+_dvcls);
			}
		}
	}
}
