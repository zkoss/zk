/* GenericDevice.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 23 18:44:47     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.device.marshal.Marshaller;
import org.zkoss.zk.device.marshal.SimpleMarshaller;

/**
 * A skeletal implementation of {@link Device}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
abstract public class GenericDevice implements Device {
	private String _type, _uamsg, _tmoutURI;
	private Class _spushcls;
	private String _embed;
	private Marshaller _marshaller;

	//Device//
	public boolean isSupported(int func) {
		return false;
	}
	public String getType() {
		return _type;
	}
	/** Return true to indicate it is cacheable.
	 */
	public boolean isCacheable() {
		return true;
	}
	/** Returns null to indicate unknown.
	 * Deriving should override it to provide more precise information.
	 */
	public Boolean isCompatible(String userAgent) {
		return null;
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
	public Class setServerPushClass(Class cls) {
		if (cls != null && !ServerPush.class.isAssignableFrom(cls))
			throw new IllegalArgumentException("ServerPush not implemented: "+cls);
		final Class old = _spushcls;
		_spushcls = cls;
		return old;
	}
	public Class getServerPushClass() {
		return _spushcls;
	}

	/** Returns null to indicate not to generate &lt;!DOCTYPE&gt; at all.
	 */
	public String getDocType() {
		return null;
	}

	public void addEmbedded(String content) {
		if (content != null && content.length() > 0)
			_embed = _embed != null ? _embed + '\n' + content: content;
	}
	public String getEmbedded() {
		return _embed;
	}

	/** Returns the marshaller to marshall the object between the
	 * client and server.
	 * <p>Default: returns the object created by {@link #newMarshaller}.
	 * <p>Derived class don't override this method directly.
	 * Rather, overrides {@link #newMarshaller}.
	 * @since 5.0.0
	 */
	public Marshaller getMarshaller() {
		return _marshaller;
	}
	/** Instantiates a marshaller for {@link #getMarshaller}.
	 *
	 * <p>Default: an instance of {@link SimpleMarshaller}.
	 * @since 5.0.0
	 */
	public Marshaller newMarshaller() {
		return new SimpleMarshaller();
	}
	/** Identify if a client is the givent type.
	 * <p>This implementation always return false.
	 * @param userAgent represents a client.
	 * @param type the type of the browser.
	 * @return true if it matches, false if unable to identify
	 * @since 5.0.0
	 */
	public boolean identifyClient(String userAgent, String type) {
		return false;
	}

	public void init(String type, DeviceConfig config) {
		_type = type;
		_uamsg = config.getUnavailableMessage();
		_tmoutURI = config.getTimeoutURI();
		_spushcls = config.getServerPushClass();
		_embed = config.getEmbedded();
		_marshaller = newMarshaller();
	}
	public void sessionWillPassivate(Desktop desktop) {
	}
	public void sessionDidActivate(Desktop desktop) {
	}
}
