/* GenericDevice.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 23 18:44:47     2007, Created by tomyeh
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
 * A skeletal implementation of {@link Device}.
 *
 * @author tomyeh
 * @since 2.5.0
 */
abstract public class GenericDevice implements Device {
	private String _type, _uamsg, _tmoutURI;

	//Device//
	public String getType() {
		return _type;
	}
	public String getUnavailableMessage() {
		return _uamsg;
	}
	public void setUnavailableMessage(String msg) {
		_uamsg = msg != null && msg.length() > 0 ? msg: null;
	}
	public String getTimeoutURI() {
		return _tmoutURI;
	}
	public void setTimeoutURI(String timeoutURI) {
		_tmoutURI = timeoutURI;
	}

	public String getDocType() {
		return null;
	}

	public void init(String type, String unavailmsg, String timeoutURI) {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("type");
		_type = type;
		_uamsg = unavailmsg;
		_tmoutURI = timeoutURI;
	}
	public void sessionWillPassivate(Desktop desktop) {
	}
	public void sessionDidActivate(Desktop desktop) {
	}
}
