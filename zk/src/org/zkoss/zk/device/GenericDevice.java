/* GenericDevice.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 23 18:44:47     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.ServerPush;

/**
 * A skeletal implementation of {@link Device}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
abstract public class GenericDevice implements Device {
	private String _type, _uamsg;
	private Class _spushcls;
	private String _embed;

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

	/** @deprecated */
	public String getTimeoutURI() {
		return null;
	}
	/** @deprecated */
	public String setTimeoutURI(String timeoutURI) {
		throw new UnsupportedOperationException("Use Configuration.setTimeoutURI() instead");
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

	/** Tests if a client is the givent type.
	 * <p>This implementation always return false.
	 * @param userAgent represents a client.
	 * @param type the type of the browser.
	 * @return true if it matches, false if unable to identify
	 * @since 5.0.0
	 */
	public boolean isClient(String userAgent, String type) {
		return false;
	}

	public void init(String type, DeviceConfig config) {
		_type = type;
		_uamsg = config.getUnavailableMessage();
		_spushcls = config.getServerPushClass();
		_embed = config.getEmbedded();
	}
	public void sessionWillPassivate(Desktop desktop) {
	}
	public void sessionDidActivate(Desktop desktop) {
	}
}
